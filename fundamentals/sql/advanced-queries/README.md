# SQL Advanced: Window Functions, Composite Indexes, Optimization, Query Patterns

## 1. Window Functions

Window functions perform calculations across a set of rows related to the current row, WITHOUT collapsing rows like GROUP BY.

**ROW_NUMBER vs RANK vs DENSE_RANK:**
```sql
SELECT 
  employee_name, 
  salary,
  department,
  ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) AS row_num,
  RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS rank,
  DENSE_RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS dense_rank
FROM employees;
```
Difference: if salaries are (100, 100, 90):
- ROW_NUMBER: 1, 2, 3 (unique, no ties)
- RANK: 1, 1, 3 (ties skip numbers)
- DENSE_RANK: 1, 1, 2 (ties don't skip)

**Common window patterns:**

```sql
-- Running total
SELECT date, amount,
  SUM(amount) OVER (ORDER BY date) AS running_total
FROM transactions;

-- Moving average (last 7 days)
SELECT date, amount,
  AVG(amount) OVER (ORDER BY date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) AS ma_7
FROM daily_sales;

-- Previous/next value (LAG/LEAD)
SELECT date, amount,
  LAG(amount, 1) OVER (ORDER BY date) AS prev_day_amount,
  LEAD(amount, 1) OVER (ORDER BY date) AS next_day_amount
FROM daily_sales;

-- First/last value in partition
SELECT department, employee, salary,
  FIRST_VALUE(employee) OVER (PARTITION BY department ORDER BY salary DESC) AS highest_paid,
  LAST_VALUE(employee) OVER (PARTITION BY department ORDER BY salary DESC 
    RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS lowest_paid
FROM employees;

-- Percentile
SELECT department, salary,
  NTILE(4) OVER (ORDER BY salary) AS quartile,
  PERCENT_RANK() OVER (ORDER BY salary) AS pct_rank
FROM employees;
```

## 2. Composite Indexes

```sql
-- Single-column index
CREATE INDEX idx_email ON users(email);

-- Composite index (multiple columns) — ORDER MATTERS!
CREATE INDEX idx_user_status_created ON orders(user_id, status, created_at);

-- When does this index help?
SELECT * FROM orders 
WHERE user_id = 123                    -- ✅ Uses index (leftmost prefix)
  AND status = 'PAID';                 -- ✅ Uses index (second column)
  
SELECT * FROM orders 
WHERE user_id = 123
  AND status = 'PAID'
  AND created_at > '2024-01-01';       -- ✅ Uses index (third column)

SELECT * FROM orders 
WHERE status = 'PAID';                -- ❌ Index NOT used (skipped user_id column)
```

**B-Tree composite index ordering:**
- Columns are stored left-to-right in the index tree
- Queries must reference the leftmost columns to use the index
- Equality conditions first, then range conditions

**When to create composite indexes:**
- High-frequency queries with multiple WHERE conditions
- Covering indexes (all columns in SELECT are in the index — no table access needed)
- ORDER BY + WHERE (avoids filesort)

## 3. Query Optimization

```sql
-- 1. Find 2nd highest salary
SELECT DISTINCT salary 
FROM employees 
ORDER BY salary DESC 
LIMIT 1 OFFSET 1;                     -- Simple approach

SELECT MAX(salary) FROM employees 
WHERE salary < (SELECT MAX(salary) FROM employees);  -- Alternative

-- 2. WHERE vs HAVING
SELECT department, COUNT(*) as emp_count
FROM employees
WHERE salary > 50000                   -- Filter rows BEFORE aggregation
GROUP BY department
HAVING COUNT(*) > 10;                  -- Filter groups AFTER aggregation

-- 3. Duplicate detection
SELECT email, COUNT(*) as cnt
FROM users
GROUP BY email
HAVING COUNT(*) > 1;

-- 4. Find records in one table not in another
SELECT * FROM orders o
LEFT JOIN payments p ON o.id = p.order_id
WHERE p.id IS NULL;                    -- Orders without payments (anti-join)

-- 5. Running difference between consecutive rows
SELECT id, amount,
  amount - COALESCE(LAG(amount) OVER (ORDER BY id), 0) AS diff_from_previous
FROM transactions;
```


## 4. NoSQL Indexing — Elasticsearch Deep Dive (Interview Critical for 7+ YOE)

### Elasticsearch Inverted Index (How Full-Text Search Works)

**B+Tree vs Inverted Index:**

```
B+Tree (SQL - good for exact match, range):
email → [row1, row5, row9]  (email="alice@example.com" → find rows)

Inverted Index (Elasticsearch - good for full-text):
"payment" → [doc1, doc5, doc23]
"refund"  → [doc5, doc17, doc42]
"success" → [doc1, doc5, doc17, doc23, doc42]
```

**How Elasticsearch builds an inverted index:**
1. **Analysis phase**: "Payment was successful" → tokenize → lowercase → stem
   - Tokens: `[payment, success]`
2. **Index phase**: Add terms to dictionary with document IDs
3. **Search phase**: Query "payments" → stem to "payment" → lookup in dictionary → return doc IDs

**Elasticsearch mapping types:**
```json
PUT /orders
{
  "mappings": {
    "properties": {
      "order_id": { "type": "keyword" },        // exact match (not analyzed)
      "status":   { "type": "keyword" },        // enum values
      "amount":   { "type": "double" },
      "tags":     { "type": "keyword" },        // array of exact values
      "notes":    { "type": "text" },           // full-text search (analyzed)
      "created_at":{ "type": "date" }
    }
  }
}
```

**text vs keyword:**
- `text`: Analyzed (tokenized, stemmed) — good for full-text search (`MATCH`)
- `keyword`: Not analyzed — good for exact match, sorting, aggregations (`TERM`)

**Aggregations (like SQL GROUP BY):**
```json
GET /orders/_search
{
  "size": 0,
  "aggs": {
    "by_status": {
      "terms": { "field": "status" }  // GROUP BY status
    },
    "avg_amount": {
      "avg": { "field": "amount" }     // AVG(amount)
    },
    "date_histogram": {
      "date_histogram": {
        "field": "created_at",
        "calendar_interval": "day"     // GROUP BY day
      }
    }
  }
}
```

### NoSQL Trade-offs: When to Use What

| Database | Strength | Weakness | Use Case |
|----------|----------|----------|----------|
| PostgreSQL | ACID, complex queries, joins | Scale writes harder | Financial systems, user data |
| MySQL | Simple, fast reads, replication | Limited advanced features | Web apps, content sites |
| MongoDB | Schema-flexible, horizontal scale | No joins, eventual consistency | Catalogs, IoT, JSON documents |
| Elasticsearch | Full-text, aggregations | Near real-time, not ACID | Search, logs, analytics |
| Redis | In-memory, sub-ms latency | Data size limited by RAM | Caching, sessions, leaderboard |
| Cassandra | Write-heavy, geo-replication | No joins, eventual consistency | Time-series, IoT, messaging |

**CAP theorem trade-offs:**
- **CP** (Consistency + Partition tolerance): MongoDB, Redis, HBase
  - During network partition: reject writes until consistency restored
- **AP** (Availability + Partition tolerance): Cassandra, DynamoDB
  - During network partition: accept writes, resolve conflicts later (last-write-wins)
- **CA** (Consistency + Availability): Traditional RDBMS (PostgreSQL, MySQL)
  - Works only if no network partition (single datacenter)

**BASE vs ACID:**
- ACID: Atomicity, Consistency, Isolation, Durability → RDBMS
- BASE: Basically Available, Soft state, Eventually consistent → NoSQL

### Advanced SQL Concepts for 7+ Years Experience

**1. Query Planner Hints (force execution strategy):**
```sql
-- PostgreSQL: force index usage
SELECT * FROM employees WHERE salary > 50000;
-- Without hint: planner may choose seq scan for low selectivity

-- Force index (PostgreSQL):
SET enable_seqscan = OFF;
SELECT * FROM employees WHERE salary > 50000;

-- MySQL: USE INDEX / FORCE INDEX
SELECT * FROM employees FORCE INDEX (idx_salary) WHERE salary > 50000;
```

**2. Partial Indexes (reduce index size):**
```sql
-- Only index active users (10% of table)
CREATE INDEX idx_active_users ON users(email) WHERE is_active = true;

-- Only index pending orders
CREATE INDEX idx_pending_orders ON orders(created_at) WHERE status = 'PENDING';
-- Much smaller than indexing ALL orders
```

**3. Expression/Function Indexes:**
```sql
-- Case-insensitive lookup
CREATE INDEX idx_email_lower ON users(LOWER(email));
SELECT * FROM users WHERE LOWER(email) = 'alice@example.com'; -- now uses index
```

**4. Materialized Views (pre-compute expensive queries):**
```sql
-- PostgreSQL: store query result physically
CREATE MATERIALIZED VIEW daily_sales_summary AS
SELECT date, SUM(amount) AS total, COUNT(*) AS cnt
FROM sales
GROUP BY date;

-- Refresh (locks table unless concurrent)
REFRESH MATERIALIZED VIEW daily_sales_summary;
-- PostgreSQL 12+: concurrent refresh
REFRESH MATERIALIZED VIEW CONCURRENTLY daily_sales_summary;
```

**5. Table Partitioning (horizontal scaling within single DB):**
```sql
-- PostgreSQL range partition by date
CREATE TABLE logs (
    id SERIAL,
    created_at TIMESTAMP NOT NULL,
    data JSONB
) PARTITION BY RANGE (created_at);

CREATE TABLE logs_2024_01 PARTITION OF logs
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
CREATE TABLE logs_2024_02 PARTITION OF logs
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

-- Queries automatically prune partitions
SELECT * FROM logs WHERE created_at >= '2024-01-15' AND created_at < '2024-01-20';
-- Only scans logs_2024_01, not entire table
```

**6. CTEs (Common Table Expressions) — Recursive Queries:**
```sql
-- Recursive: org chart (employee → manager → CEO)
WITH RECURSIVE org_tree AS (
    -- Anchor: CEO (no manager)
    SELECT id, name, manager_id, 1 AS level
    FROM employees WHERE manager_id IS NULL
    
    UNION ALL
    
    -- Recursive: find subordinates
    SELECT e.id, e.name, e.manager_id, ot.level + 1
    FROM employees e
    JOIN org_tree ot ON e.manager_id = ot.id
)
SELECT * FROM org_tree ORDER BY level, name;

-- Non-recursive CTE (readability)
WITH high_earners AS (
    SELECT department_id, AVG(salary) AS avg_sal
    FROM employees GROUP BY department_id
    HAVING AVG(salary) > 80000
)
SELECT e.name, e.salary, h.avg_sal
FROM employees e
JOIN high_earners h ON e.department_id = h.department_id;
```

**7. Locking Strategies — Avoiding Deadlocks at Scale:**

```sql
-- Pessimistic locking:
SELECT * FROM accounts WHERE id = 1 FOR UPDATE;  -- locks row until commit
SELECT * FROM accounts WHERE id = 1 FOR SHARE;   -- shared lock (read)

-- NOWAIT: don't wait for lock
SELECT * FROM accounts WHERE id = 1 FOR UPDATE NOWAIT;
-- Error immediately if locked

-- SKIP LOCKED: skip locked rows, process next
SELECT * FROM job_queue WHERE status = 'PENDING'
ORDER BY id LIMIT 10 FOR UPDATE SKIP LOCKED;
-- For queue consumers: each worker grabs next available row
-- No contention, no deadlocks
```

**8. Isolation Level Internals — What the Database Actually Does**

```
READ COMMITTED (PostgreSQL):
- Each statement gets fresh snapshot
- No shared locks held until commit
- Writers don't block readers (MVCC)
- Non-repeatable reads possible

REPEATABLE READ (PostgreSQL - Snapshot Isolation):
- First query creates snapshot (xmin-based)
- All subsequent queries see same snapshot
- No non-repeatable reads
- Phantom reads prevented (different row versions invisible)
- Write skew anomaly possible!

SERIALIZABLE (PostgreSQL - SSI):
- Detects dangerous structures (rw-rw conflicts)
- Aborts one transaction if serialization anomaly detected
- Requires retry logic in application
```

**Write Skew Anomaly (Snapshot Isolation problem):**
```sql
-- Two doctors checking on-call count (need ≥ 2)
-- Transaction A: sees 2 on-call, takes leave
-- Transaction B: sees 2 on-call, takes leave
-- Both commit → only 1 on-call! But neither violated constraint at read time.

-- Fix: use SERIALIZABLE or SELECT...FOR UPDATE
```

**9. Database Internals: WAL, Checkpoints, VACUUM**

**PostgreSQL WAL (Write-Ahead Log):**
```
Transaction flow:
1. Modify tuple in shared buffer pool
2. Write change to WAL buffer
3. wal_writer flushes WAL to disk (every wal_writer_delay = 200ms)
4. Commit record written to WAL
5. Return "commit" to client (after WAL flush)
6. Background writer later writes dirty pages to table files

Recovery after crash:
1. Read WAL from last checkpoint
2. Replay all changes
3. Database restored to last committed state
```

**MySQL Redo Log:**
- Doublewrite buffer: write changes to buffer twice (safety)
- InnoDB flushes redo log on commit (`innodb_flush_log_at_trx_commit=1`)
- Checkpoint: mark data in buffer pool as flushed to disk

**VACUUM (PostgreSQL space reclamation):**
```sql
-- Dead tuples from UPDATE/DELETE accumulate
UPDATE employees SET salary = 60000 WHERE id = 1;
-- Old tuple with salary=50000 still exists (marked dead)
-- Old tuple removed only after no transaction can see it

VACUUM VERBOSE employees;
-- Returns: removed N dead tuples, reclaimed X MB

-- Auto-vacuum runs in background
-- tunables: autovacuum_vacuum_scale_factor, autovacuum_analyze_scale_factor
```

**10. Distributed Database Patterns (7+ YOE Architect Level):**

**Sharding strategies:**
```sql
-- Hash sharding: even distribution
user_id % 256 → shard 0-255

-- Range sharding: ordered data
user_id 1-1M → shard1, 1M-2M → shard2

-- Geographic sharding: EU users → EU cluster
```

**Two-Phase Commit (2PC) coordinator:**
```
Phase 1 (Prepare):
  Coordinator → All participants: "Prepare to commit?"
  Each participant → writes prepare record, holds locks
  Participants respond: YES (prepared) or NO (abort)

Phase 2 (Commit/Abort):
  If all YES → Coordinator sends COMMIT
  If any NO → Coordinator sends ABORT
  Participants finalize, release locks

Problems:
- Coordinator is SPOF
- Blocking: participants wait for coordinator decision
- 3PC partially solves but adds complexity
```

**Saga Pattern (preferred for microservices):**
```
Order Saga:
1. Reserve inventory → success
2. Charge payment   → success  
3. Schedule shipping → FAILS!

Compensate:
3'. Refund payment   → undo step 2
2'. Release inventory → undo step 1

Each step has explicit compensating action.
No locks held across services.
Eventual consistency.
```

## 5. Index Troubleshooting: Real-World Debugging Walkthrough

```sql
-- Problem: Query taking 5 seconds despite index on user_id
EXPLAIN ANALYZE 
SELECT * FROM orders WHERE user_id = 123 AND total > 100;

-- Output:
-- Seq Scan on orders  (cost=0.00..15000.00 rows=5000 width=100)
--   Filter: (user_id = 123 AND total > 100)
--   Rows Removed by Filter: 999500
-- Planning Time: 0.1ms
-- Execution Time: 4521.123ms

-- Diagnosis:
-- 1. Planner chose Seq Scan — why?
--    → Statistics say row count after WHERE is high (low selectivity)
--    → Planner thinks index scan + random heap fetches = slower than sequential

-- 2. Why statistics wrong?
--    → ANALYZE hasn't run after recent bulk insert
--    → default_statistics_target too low (100 vs 10000)

-- Fix options:
-- Option A: Force index (testing only)
SELECT * FROM orders WHERE user_id = 123 AND total > 100;

-- Option B: Create better index (covering)
CREATE INDEX idx_orders_user_total ON orders(user_id, total) INCLUDE (id, status, created_at);
-- Now index-only scan possible — no heap fetch needed

-- Option C: Update statistics
ANALYZE orders;

-- Re-run EXPLAIN
-- Index Scan using idx_orders_user_total on orders  (cost=0.43..125.00 rows=5000 width=100)
--   Index Cond: (user_id = 123 AND total > 100)
--   Planning Time: 0.05ms
--   Execution Time: 2.345ms  ← 1900x faster!
```

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Index on low-cardinality column (boolean, status) | Index not used — scanning is faster | Don't index boolean/status — combine with other columns |
| Not using EXPLAIN ANALYZE | Guessing query plan instead of knowing | `EXPLAIN ANALYZE SELECT ...` to see actual vs estimated |
| SELECT * in production | Reads unnecessary columns, can't use covering index | Select only needed columns |
| No LIMIT on large queries | Returns millions of rows, OOM | Always add LIMIT unless you need all rows |
| Missing index on foreign key | Cascading N+1 on JOIN | Index FK columns used in JOINs |
| WHERE on indexed column wrapped in function | Index not used | `WHERE DATE(created_at) = '2024-01-01'` → use `WHERE created_at >= '2024-01-01' AND created_at < '2024-01-02'` |
| Not vacuuming PostgreSQL | Dead tuples bloat table, slow queries | Configure autovacuum, run manual VACUUM if needed |
| Keeping write-heavy indexes | Insert/update slowdown | Drop unused indexes, use covering indexes sparingly |
| Assuming default isolation is enough | Write skew, phantom reads | Choose isolation level based on consistency needs |
| Ignoring query selectivity | Bad query plans | Keep statistics fresh, index high-selectivity columns |

## 7. Interview Questions And Answers

### Beginner
Q: What is the difference between INNER JOIN and LEFT JOIN?
A: INNER JOIN returns only rows with matching keys in both tables. LEFT JOIN returns all rows from left table, NULLs for right columns when no match.

Q: What is an index?
A: Index = data structure (B+Tree) for fast lookups. Create on columns used in WHERE, JOIN, ORDER BY. Not helpful for small tables, low cardinality columns, or heavily updated columns.

### Intermediate
Q: How does a composite index work? Why does column order matter?
A: Composite index stores columns left-to-right. Queries must reference leftmost columns. Example: index on (a, b, c) supports WHERE a=1 AND b=2 AND c=3. But WHERE b=2 alone cannot use index. Order: equality columns first, then range/sort columns.

Q: What is a covering index?
A: An index that contains all columns the query needs. Example: index on (email) INCLUDE (status, created_at). Query SELECT email, status, created_at WHERE email='x' never touches the table — just the index. This is called Index-Only Scan.

Q: What is the difference between WHERE and HAVING?
A: WHERE filters rows BEFORE GROUP BY aggregation. HAVING filters groups AFTER aggregation. Use WHERE for row-level conditions, HAVING for aggregate conditions (HAVING COUNT(*) > 10).

### Senior
Q: PostgreSQL vs MySQL — when would you choose each?
A: PostgreSQL: complex queries, JSONB, GIS, analytics, CTEs, window functions, stricter SQL compliance. MySQL: simple read-heavy workloads, replication ease, smaller operational overhead. PostgreSQL is usually the better choice for modern applications.

Q: Your query is slow despite having an index. Walk through debugging steps.
A: 1. RUN EXPLAIN ANALYZE — check actual vs estimated rows. 2. Is index being used? Look for "Index Scan" vs "Seq Scan". 3. Is column wrapped in function? (kills index). 4. Type mismatch? (VARCHAR vs INT). 5. Selectivity too low? (planner chooses seq scan for >5-10% of rows). 6. Statistics outdated? RUN ANALYZE. 7. Index covering? (no table fetch). 8. Too many rows returned? (index scan + heap fetch may beat sequential scan).

Q: Explain PostgreSQL MVCC internals. What problems does it solve? What new problems does it create?
A: MVCC = Multi-Version Concurrency Control. Each row can have multiple versions. When UPDATE runs, PostgreSQL creates new tuple instead of overwriting. Readers see snapshot (based on xmin/xmax), writers don't block readers. Solves: read vs write blocking. Creates: dead tuples accumulation (needs VACUUM), table bloat, transaction ID wraparound risk. MySQL InnoDB uses undo log instead of tuple versioning.

### Tricky / 7+ Years Experience
Q: What is write skew and how does it happen under snapshot isolation?
A: Write skew = two transactions read overlapping data, make independent decisions based on stale view, and commit violating business invariant.

Example: Two doctors checking on-call count (must be ≥ 2). Both see 2, both take leave, commit → only 0 on-call. Neither violated constraint at read time — but invariant broken.

Fix: Use SERIALIZABLE isolation, or SELECT...FOR UPDATE to get exclusive locks.

Q: How does Elasticsearch inverted index differ from relational B+Tree? When would you choose one over the other?
A: B+Tree: sorted keys, good for exact match, range, sorting. O(log n) lookup. Inverted index: term → document list. Good for full-text search, any-word matching, relevancy scoring. Trade-off: B+Tree works for "find user by email", inverted index works for "find documents containing 'urgent payment'".

Choose Elasticsearch when: full-text search, multi-field filtering + sorting, faceted navigation, log analytics with aggregations. Choose RDBMS when: strong consistency, complex joins, transactions, structured data with known schema.

Q: What is the difference between PostgreSQL's REPEATABLE READ and SERIALIZABLE? When would you use each?
A: REPEATABLE READ = snapshot isolation. Guarantees snapshot consistency, prevents dirty/non-repeatable reads, prevents phantoms (unlike ANSI SQL spec). But write skew possible (rw-rw conflicts not detected). SERIALIZABLE uses SSI (Serializable Snapshot Isolation) — detects dangerous structures and aborts one transaction. Use REPEATABLE READ for most workloads. Use SERIALIZABLE only when business invariants require true serialization (banking, accounting). Must implement retry logic for serialization failures.

Q: How would you design a schema to store billions of IoT sensor readings with high write throughput?
A: PostgreSQL with partitioning by time (range partition per day/week). BRIN index on timestamp (naturally ordered data). No secondary indexes on sensor data. Use separate metadata table with B-Tree indexes. Consider TimescaleDB (PostgreSQL extension) for auto-partitioning and compression. For higher scale: Cassandra (hash partition on device_id + time). Write path: batch inserts (COPY command), not single-row INSERT.

Q: What are materialized views and when should you use them vs regular views?
A: Materialized view stores query result physically (actual table). Regular view is just stored SELECT (computed on every read). Use materialized view when: expensive aggregation (GROUP BY with millions of rows), data freshness can be eventual (refresh every 5 min), frequent reads of same computed result. Trade-off: storage cost, refresh latency. PostgreSQL: `REFRESH MATERIALIZED VIEW CONCURRENTLY` for non-blocking refresh.

Q: Explain PostgreSQL autovacuum tuning. When would you manually trigger VACUUM?
A: Autovacuum runs when dead tuples exceed `autovacuum_vacuum_scale_factor` (default 20% of table). For write-heavy tables, lower threshold: `ALTER TABLE logs SET (autovacuum_vacuum_scale_factor = 0.05)`. Manual VACUUM when: autovacuum can't keep up (dead tuples accumulating), after bulk delete, before index rebuild. VACUUM FULL rewrites entire table — expensive, requires exclusive lock. Better: pg_repack for online reclamation.

## 8. Final 30-Second Answer

**SQL core**: JOINs (INNER/LEFT/RIGHT/FULL), GROUP BY + HAVING, WHERE vs HAVING, window functions (ROW_NUMBER, LAG/LEAD, SUM OVER). **Indexing**: B+Tree O(log n), composite leftmost-prefix rule, covering index for index-only scan. **NoSQL**: Elasticsearch inverted index for full-text (text vs keyword), Redis for caching, MongoDB for flexible schema, Cassandra for write-heavy time-series. **Advanced**: CTEs/recursive queries, materialized views, table partitioning, VACUUM/WAL internals, write skew/snapshot isolation, distributed patterns (2PC vs Saga). **Troubleshooting**: EXPLAIN ANALYZE → check seq scan, selectivity, functions on columns, type mismatches, dead tuples. 7+ YOE must know: sharding strategies, CAP theorem trade-offs (CP vs AP), WAL/checkpoint internals, SSI serialization anomalies, and when to denormalize.
