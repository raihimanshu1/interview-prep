# Wells Fargo Senior SQL Performance Concepts

For 7+ years Java/backend roles, Wells Fargo interviews may include SQL performance, query tuning, and database-design discussions. The interviewer is usually checking whether you can reason from first principles instead of memorizing index names.

These answers are PostgreSQL-focused.

## How To Use This Guide In An Interview

For senior interviews, answer performance questions in this order:

1. Clarify the workload: read-heavy, write-heavy, reporting, OLTP, batch, latency-sensitive, or throughput-sensitive.
2. Look at the query and data volume: row counts, filters, joins, grouping, sorting, and returned columns.
3. Read the execution plan using `EXPLAIN ANALYZE`.
4. Compare estimated rows with actual rows.
5. Choose the smallest change that fixes the bottleneck: query rewrite, index, statistics, partitioning, caching, materialized view, or schema change.
6. Mention tradeoffs: extra storage, slower writes, maintenance, freshness delay, operational complexity.

Banking systems care about correctness, auditability, predictable latency, and controlled operational risk. A fast query that hides stale balances or creates inconsistent reporting is not acceptable.

## 1. Query Logical Execution Order

SQL is written in one order but logically evaluated in another order. PostgreSQL's optimizer can physically execute steps differently, but this logical model is important for reasoning.

Logical order:

1. `FROM` and `JOIN`
2. `ON`
3. `WHERE`
4. `GROUP BY`
5. aggregate functions
6. `HAVING`
7. `SELECT`
8. `DISTINCT`
9. `ORDER BY`
10. `LIMIT` / `OFFSET`

Example:

```sql
SELECT account_id,
       SUM(amount) AS total_debits
FROM transactions
WHERE transaction_type = 'DEBIT'
  AND transaction_timestamp >= DATE '2026-06-01'
GROUP BY account_id
HAVING SUM(amount) > 100000
ORDER BY total_debits DESC
LIMIT 20;
```

How to explain it:

- PostgreSQL first identifies rows from `transactions`.
- `WHERE` removes non-debit and old transactions before grouping.
- `GROUP BY` creates one group per account.
- `SUM(amount)` is computed for each group.
- `HAVING` filters grouped results.
- `ORDER BY` sorts the surviving groups.
- `LIMIT` returns only the top 20.

Important interview point: `WHERE` filters rows before aggregation. `HAVING` filters groups after aggregation. Prefer `WHERE` whenever possible because it reduces the rows that must be grouped.

## 2. How PostgreSQL Executes A Query

PostgreSQL does not simply follow SQL text top to bottom. It:

1. Parses the SQL.
2. Rewrites it using rules and view expansion.
3. Plans possible execution strategies.
4. Estimates cost using table statistics.
5. Picks a plan.
6. Executes the plan.

The chosen plan is cost-based. Cost is not milliseconds. It is PostgreSQL's internal estimate based on CPU, I/O, row counts, and configured cost parameters.

Common execution plan terms:

- `Seq Scan`: scans the full table.
- `Index Scan`: uses an index to find row locations, then reads table rows.
- `Index Only Scan`: reads from the index without visiting the table when visibility map permits it.
- `Bitmap Index Scan`: finds matching row locations from an index.
- `Bitmap Heap Scan`: fetches table pages after bitmap index lookup.
- `Nested Loop`: repeats an inner lookup for each outer row; good for small outer inputs and indexed inner lookups.
- `Hash Join`: builds a hash table from one input and probes it from another; useful for larger joins.
- `Merge Join`: joins two sorted inputs.
- `HashAggregate`: groups using a hash table.
- `GroupAggregate`: groups sorted input.
- `Sort`: explicit sorting step.
- `Gather` / `Gather Merge`: combines parallel worker output.

Senior-level point: a `Seq Scan` is not always bad. If a query reads a large percentage of a table, sequential scan can be faster than many random index lookups.

## 3. How To Use EXPLAIN ANALYZE

Use `EXPLAIN` to see the planned query. Use `EXPLAIN ANALYZE` to execute the query and show actual runtime details.

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT transaction_id, amount, status
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp DESC
LIMIT 20;
```

Useful options:

- `ANALYZE`: executes the query and shows actual timing and row counts.
- `BUFFERS`: shows shared buffer reads and hits, useful for I/O analysis.
- `VERBOSE`: shows more detail about output columns and internal plan information.
- `FORMAT JSON`: useful for automated analysis tools.

Example plan shape:

```text
Limit  (cost=0.42..15.70 rows=20 width=48)
  ->  Index Scan Backward using idx_transactions_account_time
      on transactions
      (cost=0.42..7642.10 rows=10000 width=48)
      Index Cond: (account_id = 101)
```

How to interpret:

- PostgreSQL uses `idx_transactions_account_time`.
- `Index Scan Backward` means it can read the index in descending timestamp order.
- `Limit` stops after 20 rows.
- This is usually efficient for recent-account-activity screens.

Important fields:

- `cost=startup..total`: estimated startup cost and total cost.
- `rows`: estimated number of rows.
- `actual time=start..end`: real measured time.
- `actual rows`: real rows produced.
- `loops`: how many times the node ran.
- `Rows Removed by Filter`: rows fetched but rejected after filtering.
- `Buffers: shared hit`: pages found in memory.
- `Buffers: shared read`: pages read from disk.
- `Sort Method`: memory or disk sort.

What to look for:

- Estimated rows very different from actual rows.
- Sequential scan on a huge table for a selective query.
- Sort spilling to disk.
- Nested loop running thousands or millions of times.
- Hash join or hash aggregate spilling to batches.
- Too many rows processed before `LIMIT`.
- Filters applied after a join when they could have been applied earlier.

Interview answer: Do not guess. Read the plan. The most important senior signal is comparing estimated rows with actual rows, then explaining why the optimizer may have chosen the plan.

## 4. Why A Query Is Slow

Common reasons:

- Missing index.
- Wrong composite index column order.
- Query scans too many rows.
- Query returns too many rows.
- Expensive sort.
- Expensive join.
- Expensive aggregation.
- Stale or insufficient statistics.
- Data distribution is skewed.
- Predicate is not sargable.
- `OFFSET` pagination scans and discards many rows.
- Lock waits or blocking transactions.
- Table or index bloat.
- Work memory too small for sort or hash operations.
- Network round trips or ORM-generated inefficient SQL.

Example slow query:

```sql
SELECT transaction_id, amount
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp DESC
LIMIT 20;
```

Potential index:

```sql
CREATE INDEX idx_transactions_account_time_desc
ON transactions(account_id, transaction_timestamp DESC);
```

Why it helps:

- `account_id` narrows the search to one account.
- `transaction_timestamp DESC` matches the order required by the query.
- `LIMIT 20` lets PostgreSQL stop early.

Tradeoff:

- Inserts and updates are slower because PostgreSQL must maintain the index.
- The index consumes storage.
- If the query is not frequent or the table is small, the index may not be worth it.

Banking example: a customer-service dashboard often needs the latest 20 transactions for an account. This index is usually worth it because it protects a high-frequency, latency-sensitive lookup.

## 5. Sargability

A predicate is sargable when the database can use an index efficiently to search for matching rows.

Good:

```sql
SELECT *
FROM transactions
WHERE transaction_timestamp >= TIMESTAMP '2026-06-01'
  AND transaction_timestamp <  TIMESTAMP '2026-07-01';
```

Bad:

```sql
SELECT *
FROM transactions
WHERE DATE(transaction_timestamp) = DATE '2026-06-01';
```

The second query applies a function to the indexed column. A normal B-tree index on `transaction_timestamp` cannot be used as directly.

Better options:

- Rewrite the predicate as a range.
- Use an expression index if the expression is genuinely needed.

```sql
CREATE INDEX idx_transactions_txn_date
ON transactions ((DATE(transaction_timestamp)));
```

Tradeoff: expression indexes help specific expressions but add write cost and are only useful when the query uses the same expression.

## 6. Clustered vs Non-Clustered Index

In SQL Server, a clustered index controls the physical order of table rows. PostgreSQL does not maintain a permanent clustered index in that sense.

PostgreSQL indexes are separate structures that point to table rows. PostgreSQL has a `CLUSTER` command:

```sql
CLUSTER transactions USING idx_transactions_account_time_desc;
```

But this physically rewrites the table once. Future writes do not automatically preserve that order.

When it helps:

- A large table is often scanned by the same range pattern.
- Physical locality reduces random I/O.
- The table is mostly append-only or can tolerate periodic maintenance.

Tradeoffs:

- `CLUSTER` takes strong locks during rewrite.
- The benefit decays as new rows are inserted.
- It is an operational maintenance choice, not a normal automatic index feature.

Interview answer: In PostgreSQL, focus first on B-tree indexes, composite indexes, partial indexes, expression indexes, covering indexes using `INCLUDE`, statistics, and query shape.

## 7. Index Basics In PostgreSQL

An index is a data structure that helps PostgreSQL find rows without scanning the whole table.

Common PostgreSQL index types:

- B-tree: default and most common; useful for equality, ranges, sorting, and prefix matching.
- Hash: equality only; less commonly needed because B-tree also handles equality.
- GIN: useful for arrays, JSONB, full-text search.
- GiST: useful for geometric, range, and specialized searches.
- BRIN: useful for very large naturally ordered tables, such as append-only time-series data.

Most interview performance conversations are about B-tree indexes.

B-tree supports:

- `=`
- `<`, `<=`, `>`, `>=`
- `BETWEEN`
- `ORDER BY`
- `IS NULL` / `IS NOT NULL` in some cases
- left-anchored `LIKE 'abc%'`

Index tradeoffs:

- Faster reads for matching query patterns.
- Slower writes because inserts, updates, and deletes maintain indexes.
- More storage.
- More vacuum and maintenance work.
- Too many indexes can confuse tuning and increase operational cost.

Senior answer: do not index every column. Index access paths used by important queries.

## 8. Composite Index And Column Order

A composite index contains multiple columns.

```sql
CREATE INDEX idx_transactions_account_time
ON transactions(account_id, transaction_timestamp);
```

Good query:

```sql
SELECT transaction_id, amount
FROM transactions
WHERE account_id = 101
  AND transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '30 days'
ORDER BY transaction_timestamp DESC;
```

Why this order works:

- `account_id` is an equality filter.
- `transaction_timestamp` is a range filter and sort column.
- PostgreSQL can quickly find one account's rows and then scan the relevant time range.

General rule:

- Put equality filters first.
- Then put range filters.
- Then consider columns used for ordering.
- Add included columns only for covering, not for filtering.

This is not a law. The best order depends on selectivity and query patterns.

Example 1:

```sql
WHERE account_id = 101
  AND status = 'POSTED'
  AND transaction_timestamp >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY transaction_timestamp DESC
```

Potential index:

```sql
CREATE INDEX idx_txn_account_status_time
ON transactions(account_id, status, transaction_timestamp DESC);
```

Example 2:

```sql
WHERE status = 'PENDING'
  AND transaction_timestamp < CURRENT_TIMESTAMP - INTERVAL '10 minutes'
```

Potential index:

```sql
CREATE INDEX idx_txn_status_time
ON transactions(status, transaction_timestamp);
```

Banking example: a payment-processing job may repeatedly find pending transactions older than 10 minutes. The second index supports that operational queue query.

Common interviewer follow-up: "Can the index on `(account_id, transaction_timestamp)` be used for `WHERE transaction_timestamp >= ...`?"

Answer: Not efficiently as a normal leading-column search, because `account_id` is the first column. PostgreSQL may still use the index in limited cases, but a separate index on `transaction_timestamp` or another composite index may be needed if timestamp-only queries are important.

## 9. Covering Indexes With INCLUDE

A covering index contains enough data to satisfy a query without reading the table heap.

PostgreSQL supports this using `INCLUDE`:

```sql
CREATE INDEX idx_transactions_account_time_cover
ON transactions(account_id, transaction_timestamp DESC)
INCLUDE (transaction_id, amount, status);
```

Query:

```sql
SELECT transaction_id, amount, status
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp DESC
LIMIT 20;
```

Why it helps:

- `account_id` and `transaction_timestamp` drive lookup and ordering.
- `transaction_id`, `amount`, and `status` are available from the index.
- PostgreSQL may use an `Index Only Scan`.

Important PostgreSQL detail: an index-only scan still depends on the visibility map. If table pages are not marked all-visible, PostgreSQL may need heap checks.

Tradeoffs:

- Larger index.
- More write overhead.
- Included columns cannot be used for index ordering or search conditions.

Banking example: account activity pages often need only a few displayed columns. A covering index can reduce latency for a heavily used customer-care screen.

## 10. Partial Indexes

A partial index covers only rows that match a condition.

```sql
CREATE INDEX idx_transactions_pending_old
ON transactions(transaction_timestamp)
WHERE status = 'PENDING';
```

Query:

```sql
SELECT transaction_id, account_id, amount
FROM transactions
WHERE status = 'PENDING'
  AND transaction_timestamp < CURRENT_TIMESTAMP - INTERVAL '10 minutes';
```

Why it helps:

- If only a small percentage of transactions are pending, the index is small and selective.
- Write overhead is lower than indexing all statuses.

Tradeoffs:

- Useful only when the query predicate matches the partial-index predicate.
- Application queries must be written consistently.
- If the filtered subset grows large, benefit drops.

Banking example: fraud-review, pending ACH, pending card authorization, and failed-settlement workflows often query a small active subset of a much larger transaction table.

## 11. Expression Indexes

An expression index indexes a computed value.

```sql
CREATE INDEX idx_customers_lower_email
ON customers (LOWER(email));
```

Query:

```sql
SELECT customer_id
FROM customers
WHERE LOWER(email) = LOWER('Alice.Example@email.com');
```

Why it helps:

- The query searches by normalized email.
- PostgreSQL can use the expression index.

Tradeoffs:

- The exact expression must match the query.
- Updates to dependent columns update the index.
- It can hide a data-model issue; storing normalized values may be better for core identifiers.

Banking example: customer lookup by case-insensitive email or normalized tax identifier may use expression indexes, but sensitive identifiers need security, masking, and compliance controls.

## 12. GROUP BY Optimization

`GROUP BY` can be expensive because PostgreSQL must collect rows into groups, often using hash aggregation or sorting.

Example:

```sql
SELECT account_id,
       SUM(amount) AS monthly_amount
FROM transactions
WHERE transaction_timestamp >= DATE_TRUNC('month', CURRENT_DATE)
GROUP BY account_id;
```

Ways to optimize:

- Filter early using `WHERE`.
- Group fewer rows.
- Avoid applying functions to indexed columns in filters.
- Add indexes matching filter columns.
- Use covering indexes for narrow aggregate queries.
- Partition very large time-series tables.
- Pre-aggregate into summary tables.
- Use materialized views for expensive repeated summaries.
- Increase `work_mem` carefully for large sorts or hash aggregates.

Helpful index:

```sql
CREATE INDEX idx_transactions_time_account
ON transactions(transaction_timestamp, account_id);
```

Why this helps:

- The query filters by timestamp first.
- PostgreSQL can scan only recent rows.
- `account_id` is available for grouping.

Alternative if the common query is account-scoped monthly total:

```sql
CREATE INDEX idx_transactions_account_time_amount
ON transactions(account_id, transaction_timestamp)
INCLUDE (amount);
```

Query:

```sql
SELECT SUM(amount)
FROM transactions
WHERE account_id = 101
  AND transaction_timestamp >= DATE '2026-06-01'
  AND transaction_timestamp <  DATE '2026-07-01';
```

Interview distinction:

- For "all accounts this month", timestamp-first can be better.
- For "one account for this month", account-first can be better.

HashAggregate vs GroupAggregate:

- `HashAggregate` builds a hash table of groups. It is fast when groups fit in memory.
- `GroupAggregate` needs sorted input. It can be efficient if an index already provides sorted order.

Common bottleneck:

```text
Sort Method: external merge  Disk: 204800kB
```

This means PostgreSQL sorted on disk. Possible responses include reducing rows earlier, adding a useful index, or tuning `work_mem` for that workload.

Banking example: daily ledger reconciliation may group millions of transactions by branch, account type, currency, and posting date. For repeated dashboards, pre-aggregation is often safer than running large live aggregates during business hours.

## 13. Join Optimization

Joins become slow when PostgreSQL processes too many rows or chooses a poor join strategy because estimates are wrong.

Example:

```sql
SELECT c.customer_id, c.name, SUM(t.amount) AS total_amount
FROM customers c
JOIN accounts a
  ON a.customer_id = c.customer_id
JOIN transactions t
  ON t.account_id = a.account_id
WHERE t.transaction_timestamp >= DATE '2026-06-01'
  AND t.transaction_timestamp <  DATE '2026-07-01'
GROUP BY c.customer_id, c.name;
```

Helpful indexes:

```sql
CREATE INDEX idx_accounts_customer
ON accounts(customer_id);

CREATE INDEX idx_transactions_account_time
ON transactions(account_id, transaction_timestamp);
```

Or, if the timestamp filter is much more selective:

```sql
CREATE INDEX idx_transactions_time_account
ON transactions(transaction_timestamp, account_id);
```

How to reason:

- Join columns usually need indexes on large tables.
- Filter selectivity determines which table should be reduced early.
- A small dimension table may not need an index for every join.

Tradeoff: adding indexes to foreign keys is usually helpful for joins and deletes, but every extra index slows writes.

## 14. ORDER BY, LIMIT, And Pagination

`ORDER BY` can be expensive if PostgreSQL must sort many rows.

Efficient latest-transactions query:

```sql
SELECT transaction_id, amount, transaction_timestamp
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp DESC
LIMIT 20;
```

Index:

```sql
CREATE INDEX idx_txn_account_time_desc
ON transactions(account_id, transaction_timestamp DESC);
```

Avoid deep `OFFSET` pagination:

```sql
SELECT transaction_id, amount
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp DESC
OFFSET 100000
LIMIT 20;
```

This can be slow because PostgreSQL must still walk past many rows.

Prefer keyset pagination:

```sql
SELECT transaction_id, amount, transaction_timestamp
FROM transactions
WHERE account_id = 101
  AND transaction_timestamp < TIMESTAMP '2026-06-04 10:00:00'
ORDER BY transaction_timestamp DESC
LIMIT 20;
```

For stable pagination, include a tie-breaker:

```sql
CREATE INDEX idx_txn_account_time_id_desc
ON transactions(account_id, transaction_timestamp DESC, transaction_id DESC);
```

```sql
SELECT transaction_id, amount, transaction_timestamp
FROM transactions
WHERE account_id = 101
  AND (transaction_timestamp, transaction_id)
      < (TIMESTAMP '2026-06-04 10:00:00', 987654321)
ORDER BY transaction_timestamp DESC, transaction_id DESC
LIMIT 20;
```

Banking example: scrolling through transaction history should use keyset pagination, not deep offsets, especially for long-lived accounts.

## 15. Statistics And Cardinality Estimates

PostgreSQL uses statistics to estimate how many rows each operation will produce. Bad estimates cause bad plans.

Refresh statistics:

```sql
ANALYZE transactions;
```

Vacuum and analyze:

```sql
VACUUM (ANALYZE) transactions;
```

When estimates go wrong:

- Data is skewed.
- Columns are correlated.
- Statistics are stale.
- Default statistics target is too low.
- Query predicates use expressions not covered by stats.

Example issue:

```text
Index Scan using idx_transactions_status
  (cost=0.42..100.00 rows=10)
  (actual time=0.05..900.00 rows=500000)
```

PostgreSQL expected 10 rows but got 500,000. The optimizer may have chosen an index lookup when a sequential scan or different join order would have been better.

Extended statistics can help with correlated columns:

```sql
CREATE STATISTICS stats_transactions_status_type
ON status, transaction_type
FROM transactions;

ANALYZE transactions;
```

Banking example: `status = 'POSTED'` may be very common while `status = 'PENDING'` is rare. Plans for these two values can differ dramatically.

## 16. Partitioning

Partitioning splits a large logical table into smaller physical partitions.

PostgreSQL supports:

- Range partitioning.
- List partitioning.
- Hash partitioning.

Common banking use case: partition transactions by posting month or business date.

```sql
CREATE TABLE transactions_partitioned (
    transaction_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    transaction_timestamp TIMESTAMP NOT NULL,
    status TEXT NOT NULL
) PARTITION BY RANGE (transaction_timestamp);
```

Create one partition:

```sql
CREATE TABLE transactions_2026_06
PARTITION OF transactions_partitioned
FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
```

Why it helps:

- Partition pruning skips irrelevant partitions.
- Old partitions can be archived or dropped more easily.
- Indexes are smaller per partition.
- Maintenance can be performed partition by partition.

Query that benefits:

```sql
SELECT SUM(amount)
FROM transactions_partitioned
WHERE transaction_timestamp >= DATE '2026-06-01'
  AND transaction_timestamp <  DATE '2026-07-01';
```

Tradeoffs:

- More schema and operational complexity.
- Poor partition key choice can make queries slower.
- Too many partitions can increase planning overhead.
- Global uniqueness constraints have limitations in PostgreSQL.
- Cross-partition queries can still be expensive.

Senior answer: partitioning is not a replacement for indexes. Partitions often still need local indexes.

## 17. Partitioning vs Sharding

Partitioning splits one logical table inside one PostgreSQL database cluster.

Sharding splits data across multiple database servers or clusters.

Partitioning helps with:

- Very large tables.
- Time-based pruning.
- Archiving and retention.
- Maintenance windows.
- Partition-local indexes.

Sharding helps with:

- Data too large for one database server.
- Write throughput beyond one primary.
- Tenant or customer isolation.
- Regional scaling.

Sharding key qualities:

- Even distribution.
- Matches query patterns.
- Avoids hot shards.
- Minimizes cross-shard transactions.
- Stable value.

Banking example:

- Partition by `transaction_timestamp` for historical transaction tables.
- Shard by `customer_id` or `account_id` only if the platform truly needs horizontal scaling and most operations are scoped to that key.

Tradeoffs of sharding:

- Cross-shard joins are hard.
- Cross-shard transactions are complex.
- Rebalancing is operationally risky.
- Reporting often needs a separate warehouse or aggregation layer.
- Application code becomes more complex.

Interview answer: partition first for manageability and pruning within one database. Shard only when one database cannot meet scale or isolation requirements.

## 18. Materialized Views

A materialized view stores query results physically.

```sql
CREATE MATERIALIZED VIEW monthly_customer_spend AS
SELECT customer_id,
       DATE_TRUNC('month', transaction_timestamp) AS spend_month,
       SUM(amount) AS total_spend
FROM transactions
WHERE transaction_type = 'DEBIT'
GROUP BY customer_id, DATE_TRUNC('month', transaction_timestamp);
```

Refresh:

```sql
REFRESH MATERIALIZED VIEW monthly_customer_spend;
```

Concurrent refresh requires a unique index:

```sql
CREATE UNIQUE INDEX idx_monthly_customer_spend_unique
ON monthly_customer_spend(customer_id, spend_month);

REFRESH MATERIALIZED VIEW CONCURRENTLY monthly_customer_spend;
```

When to use:

- Expensive reporting query.
- Result can tolerate refresh delay.
- Same aggregation is requested repeatedly.
- Dashboard latency matters more than real-time freshness.

Tradeoffs:

- Data can be stale.
- Refresh consumes resources.
- Concurrent refresh has requirements and may still be expensive.
- You need a refresh schedule and monitoring.

Banking example: a dashboard showing monthly debit-card spend by customer segment can use a materialized view. Real-time available balance should not rely on a stale materialized view.

## 19. CTE vs Subquery vs Temp Table

CTE:

```sql
WITH high_value_transactions AS (
    SELECT *
    FROM transactions
    WHERE amount >= 10000
)
SELECT customer_id, COUNT(*)
FROM high_value_transactions
GROUP BY customer_id;
```

Temporary table:

```sql
CREATE TEMP TABLE high_value_transactions AS
SELECT *
FROM transactions
WHERE amount >= 10000;
```

PostgreSQL behavior:

- In modern PostgreSQL versions, simple CTEs can often be inlined.
- `MATERIALIZED` forces materialization.
- `NOT MATERIALIZED` asks PostgreSQL to inline when possible.

```sql
WITH high_value_transactions AS MATERIALIZED (
    SELECT *
    FROM transactions
    WHERE amount >= 10000
)
SELECT customer_id, COUNT(*)
FROM high_value_transactions
GROUP BY customer_id;
```

Use a CTE when:

- Logic is needed once.
- Readability matters.
- The optimizer should still have freedom to inline.
- Intermediate data volume is manageable.

Use a temp table when:

- Intermediate result is reused multiple times.
- You need indexes on intermediate data.
- You need to break a large process into stages.
- Debugging or batch processing benefits from inspection.

Temp table index example:

```sql
CREATE TEMP TABLE high_value_transactions AS
SELECT transaction_id, customer_id, amount, transaction_timestamp
FROM transactions
WHERE amount >= 10000;

CREATE INDEX idx_temp_hvt_customer
ON high_value_transactions(customer_id);
```

Tradeoffs:

- CTEs keep SQL compact but may hide repeated work if referenced multiple times.
- Temp tables add session-local state and extra writes.
- Temp tables require cleanup thinking in connection pools and long-running sessions.

Banking example: a nightly AML batch may stage high-value transactions in a temp table, index them, then run several checks against the staged subset.

## 20. Query Rewrites That Often Help

Avoid `SELECT *` in production paths:

```sql
SELECT transaction_id, amount, status
FROM transactions
WHERE account_id = 101;
```

Why:

- Reduces I/O.
- Enables covering indexes.
- Reduces network cost.
- Protects sensitive columns from accidental exposure.

Prefer `EXISTS` for existence checks:

```sql
SELECT c.customer_id
FROM customers c
WHERE EXISTS (
    SELECT 1
    FROM accounts a
    WHERE a.customer_id = c.customer_id
      AND a.status = 'ACTIVE'
);
```

Avoid leading wildcard searches on normal B-tree indexes:

```sql
WHERE merchant_name LIKE '%coffee%'
```

This usually cannot use a normal B-tree index efficiently. Consider full-text search, trigram indexes, or a search system depending on requirements.

Avoid function-wrapped indexed columns in filters unless using expression indexes.

Avoid accidental inner joins:

```sql
SELECT c.customer_id, t.transaction_id
FROM customers c
LEFT JOIN transactions t
  ON t.customer_id = c.customer_id
WHERE t.status = 'POSTED';
```

The `WHERE` condition turns the result effectively into an inner join. Put right-table filters in the `ON` clause when preserving unmatched left rows matters.

## 21. Locking And Concurrency Performance

Sometimes a query is slow because it is waiting, not because the plan is bad.

Common causes:

- Long-running transaction holds row locks.
- DDL waits on active queries.
- Batch update blocks OLTP updates.
- Connection pool is exhausted.
- Dead tuples and bloat increase scan cost.

Useful PostgreSQL views:

```sql
SELECT *
FROM pg_stat_activity
WHERE state <> 'idle';
```

```sql
SELECT *
FROM pg_locks;
```

Banking example: a settlement batch updating many rows can block customer-facing transaction status updates if not designed carefully. Senior engineers consider batching, lock order, retry strategy, and isolation level.

## 22. Practical Banking Performance Examples

### Latest Account Transactions

Use case: customer support opens an account and needs recent transactions instantly.

```sql
SELECT transaction_id, amount, status, transaction_timestamp
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp DESC
LIMIT 20;
```

Index:

```sql
CREATE INDEX idx_txn_account_time_desc
ON transactions(account_id, transaction_timestamp DESC)
INCLUDE (amount, status);
```

Tradeoff: great for read latency, extra write cost on a high-write transaction table.

### Pending Payment Sweep

Use case: background job picks pending payments older than 10 minutes.

```sql
SELECT payment_id
FROM payments
WHERE status = 'PENDING'
  AND created_at < CURRENT_TIMESTAMP - INTERVAL '10 minutes'
ORDER BY created_at
LIMIT 500;
```

Index:

```sql
CREATE INDEX idx_payments_pending_created
ON payments(created_at)
WHERE status = 'PENDING';
```

Tradeoff: excellent if pending rows are rare, less useful if most rows are pending.

### Monthly Statement Generation

Use case: generate monthly statements by account.

```sql
SELECT account_id, transaction_id, amount, transaction_timestamp
FROM transactions
WHERE account_id = 101
  AND transaction_timestamp >= DATE '2026-05-01'
  AND transaction_timestamp <  DATE '2026-06-01'
ORDER BY transaction_timestamp;
```

Index:

```sql
CREATE INDEX idx_txn_account_time_asc
ON transactions(account_id, transaction_timestamp);
```

Partitioning by month may also help if statements are generated in large batches.

### Fraud Monitoring

Use case: detect unusually large transactions by customer within recent windows.

Options:

- Index recent transaction lookup paths.
- Pre-aggregate rolling metrics.
- Use materialized views for dashboard reporting.
- Use streaming or event-driven systems for real-time fraud decisions.

Senior answer: a database index can help retrieval, but fraud detection often needs event processing and model scoring outside the OLTP query path.

## 23. Common Interview Follow-Ups

### Is a sequential scan always bad?

No. If PostgreSQL must read a large percentage of the table, a sequential scan may be cheaper than random index lookups.

### Why is my index not used?

Possible reasons:

- Table is small.
- Predicate is not selective.
- Function is applied to the indexed column.
- Wrong column order in composite index.
- Statistics are stale.
- Query returns many columns and heap access is expensive.
- Data type mismatch prevents efficient index use.

### What is the difference between `WHERE` and `HAVING`?

`WHERE` filters rows before grouping. `HAVING` filters groups after aggregation. Prefer `WHERE` for pre-aggregation filters.

### What is the best index for a query?

There is no universal best index. Start from the query:

- Equality filters.
- Range filters.
- Join columns.
- Sort order.
- Returned columns.
- Frequency of the query.
- Write cost.

### Should every foreign key have an index?

Often yes for large tables, especially when joining from child to parent or deleting/updating parent rows. PostgreSQL does not automatically create an index on the referencing foreign key column.

### Does `LIMIT` make every query fast?

No. `LIMIT` helps only if PostgreSQL can find the first matching rows quickly. If it must sort or scan millions of rows first, the query can still be slow.

### What is an index-only scan?

An index-only scan reads required columns from the index without fetching table rows, when the visibility map allows it. Covering indexes can enable this.

### What is index selectivity?

Selectivity describes how much a predicate narrows the result. A highly selective condition returns a small percentage of rows and is usually a good index candidate.

### How do you optimize a slow report?

Answer:

1. Run `EXPLAIN (ANALYZE, BUFFERS)`.
2. Check row estimates and actual rows.
3. Filter earlier.
4. Add or adjust indexes.
5. Consider partition pruning.
6. Consider pre-aggregation or materialized views.
7. Schedule heavy reports away from OLTP peaks.

### CTE or temp table?

Use a CTE for readable one-query logic. Use a temp table when the intermediate result is reused, indexed, debugged, or processed in multiple stages.

### Partitioning or sharding?

Partitioning is inside one database and helps pruning, retention, and maintenance. Sharding spreads data across servers and is used when one database cannot meet scale or isolation needs.

### What tradeoffs should you mention for indexes?

Indexes improve reads for matching access patterns but increase storage, write latency, vacuum overhead, and maintenance complexity.

## 24. Senior Interview Checklist

When asked to tune a query, say:

1. "I would first capture the actual query plan with `EXPLAIN (ANALYZE, BUFFERS)`."
2. "I would compare estimated rows with actual rows."
3. "I would identify whether the main cost is scanning, joining, sorting, grouping, locking, or I/O."
4. "I would check whether existing indexes match the predicates, joins, and ordering."
5. "I would avoid adding indexes blindly because of write and storage overhead."
6. "For repeated expensive summaries, I would consider materialized views or summary tables."
7. "For very large time-based tables, I would consider partitioning."
8. "For scale beyond one database server, I would discuss sharding, but only after understanding operational complexity."

Strong closing answer:

"In PostgreSQL, I would not tune by guessing. I would use `EXPLAIN ANALYZE`, check row estimates versus actuals, identify the expensive node, and then choose the least risky fix. In a banking system, I would also consider write impact, data freshness, auditability, and operational safety."
