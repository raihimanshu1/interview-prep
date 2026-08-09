# Module — Database Indexing Deep Dive: B+ Tree, Query Planner, Index Types — Q&A

> **Skill**: 7+ years — covers B+ tree internals, covering indexes, index-only scans, query planner analysis.

---

## Q1. How do B+ Tree Indexes Work Internally?

### 1. B+ Tree Structure

```
B+ Tree of order 3 (each node has 2-3 keys):
                    ┌───────┬───────┐
                    │  30   │  60   │        ← Internal nodes (routing)
                    └───┬───┴───┬───┘
               ┌────────┘       └────────┐
           ┌───▼───┐                ┌───▼───┐
           │ 10,20 │                │ 40,50 │    ← Internal nodes
           └───┬───┘                └───┬───┘
      ┌────────┼────────┐        ┌──────┼──────┐
  ┌──▼──┐ ┌───▼──┐ ┌───▼──┐ ┌──▼──┐ ┌─▼──┐ ┌─▼──┐
  │1..10│ │11..20│ │21..30│ │31..40│ │41..50│ │51..60│ ← Leaf nodes (actual data)
  └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘
     │       │       │       │       │       │
     └───────┴───────┴───────┴───────┴───────┘  ← Linked list between leaves!

Key facts:
- Height: O(log n) — for 1B rows, height ≈ 4 (each node has ~500 keys)
- Leaf nodes contain pointers to actual rows (or the row data itself in clustered index)
- Leaf nodes are linked — enables range scans (index scan)
- Internal nodes route to the correct leaf
```

### 2. Index Types Comparison

| Type | Structure | Data at Leaf | Rows/Leaf | Best For |
|------|-----------|--------------|-----------|----------|
| **Clustered** (Primary Key) | B+ Tree | Full row data | 1 | PK lookups, range scans |
| **Non-clustered** (Secondary Index) | B+ Tree | PK value | Many | WHERE on non-PK columns |
| **Covering** (Index with includes) | B+ Tree | PK + included cols | Many | Queries that only need included cols |
| **Composite** | B+ Tree (multi-column) | PK value | Many | Queries on (col1, col2) |
| **Unique** | B+ Tree (no duplicates) | PK value | 1 max | Uniqueness enforcement |
| **Full-text** | Inverted index | Word positions | Many | Text search (LIKE '%word%') |

### 3. Composite Index — Column Order Matters

```sql
-- Index: CREATE INDEX idx ON orders (status, created_at, customer_id)

-- ✅ FULL match: uses ALL 3 columns
SELECT * FROM orders 
WHERE status = 'SHIPPED' 
  AND created_at > '2024-01-01'
  AND customer_id = 123;
-- Uses: status → created_at → customer_id (3 column lookups)

-- ✅ PREFIX match: uses first 2 columns
SELECT * FROM orders 
WHERE status = 'SHIPPED' 
  AND created_at > '2024-01-01';
-- Uses: status → created_at (2 columns)

-- ✅ PREFIX match: uses first 1 column
SELECT * FROM orders 
WHERE status = 'SHIPPED';
-- Uses: status (1 column, then table access)

-- ❌ SKIP first column → CANNOT use index!
SELECT * FROM orders 
WHERE created_at > '2024-01-01';
-- Can't use idx! created_at is 2nd column, status not filtered
-- → Full table scan!

-- ❌ GAP in middle → can use prefix only
SELECT * FROM orders 
WHERE status = 'SHIPPED' AND customer_id = 123;
-- Uses: status only (created_at skipped in middle)
-- Then table access for customer_id

-- Rule: "Leftmost prefix" — index can be used from the LEFT
-- Skipping any column in the index = index still works for left part only
```

### 4. Query Planner Analysis

```sql
-- EXPLAIN ANALYZE shows actual execution:

EXPLAIN ANALYZE SELECT * FROM orders WHERE status = 'SHIPPED';

-- Output:
-- Index Scan using idx_orders_status on orders  (cost=0.42..124.3 rows=5000 width=42)
--   Index Cond: (status = 'SHIPPED'::text)
--   Planning Time: 0.123 ms
--   Execution Time: 2.345 ms
--   ↑                      ↑
--   actual time            row count estimate

-- Key terms to look for:
-- "Seq Scan" (sequential scan) = full table scan = BAD for large tables
-- "Index Scan" = good (index lookup + table access)
-- "Index Only Scan" = BEST (all needed data in index, no table access!)
-- "Bitmap Index Scan" = multiple index lookups merged with bitmap
-- "Nested Loop" = for each outer row, look up inner (good for small result sets)
-- "Hash Join" = build hash table of one side, probe with other
-- "Sort" = expensive if sorting large result sets
```

### 5. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Indexing low-cardinality columns (boolean, status with 2 values) | Index scan may be worse than seq scan | Use partial indexes: WHERE status = 'ACTIVE' |
| Too many indexes on write-heavy tables | Every INSERT/UPDATE/DELETE updates ALL indexes | Only index columns used in WHERE/JOIN |
| Index on (col1, col2) AND also on (col1) | Redundant — prefix covers same queries | Keep only composite index |
| Not using covering indexes for read-heavy queries | Extra table lookups | Add INCLUDE columns to avoid table access |
| Using functions on indexed columns | Index can't be used | Use expression indexes (PostgreSQL) or computed columns |

### 6. Senior Q&A

**Q**: Query on 10M row table with index is slow. EXPLAIN shows "Index Scan" but still takes 2 seconds. Why?

**A**: Index Scan = reads index to find row locations, then fetches rows from heap. If the query matches many rows (e.g., 10% of 10M = 1M individual row fetches), each row requires a RANDOM I/O to the heap — the index returns row locations in index order, not heap order. 1M random I/Os = slow! Solutions: (1) Use covering index (index-only scan — no heap access); (2) Cluster table on that index (pg: CLUSTER, MySQL: PRIMARY KEY order); (3) If returning large result set, seq scan may be faster; (4) Add LIMIT to reduce result set.

**Final 30-Second**: B+ trees have height O(log n) — for 1B rows, about 4 levels. Composite index: leftmost prefix rule — can't skip first column. Index Only Scan = fastest (all data in index). Avoid indexing low-cardinality columns. Covering indexes eliminate table lookups. Use EXPLAIN ANALYZE to see actual execution.