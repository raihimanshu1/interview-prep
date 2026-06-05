# PostgreSQL Indexes and Performance

Indexing questions test whether you can reason about how PostgreSQL finds rows, joins tables, sorts results, and protects predictable latency under real banking workloads.

For Wells Fargo-style interviews, do not answer "add an index" too quickly. A senior answer usually covers:

1. What query pattern is slow.
2. How many rows the table has and how selective the filter is.
3. What the execution plan shows.
4. Which index or query rewrite helps.
5. What the tradeoff is for writes, storage, maintenance, and correctness.

PostgreSQL syntax is used throughout.

## Sample Banking Tables

```sql
CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    risk_segment VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
    account_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(customer_id),
    account_number VARCHAR(30) UNIQUE NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    opened_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(account_id),
    customer_id BIGINT NOT NULL REFERENCES customers(customer_id),
    merchant_id BIGINT,
    amount NUMERIC(12, 2) NOT NULL,
    transaction_type VARCHAR(10) NOT NULL CHECK (transaction_type IN ('DEBIT', 'CREDIT')),
    transaction_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'POSTED', 'FAILED', 'REVERSED')),
    channel VARCHAR(20),
    reference_id VARCHAR(80)
);
```

Banking reality: transaction tables can become very large. Customer-service screens need recent account activity quickly, fraud systems need selective lookups, and reporting queries may scan large date ranges. The right solution depends on workload.

## 1. What An Index Does

Theory: An index is a separate data structure that lets PostgreSQL find matching rows without scanning the whole table.

Basic index:

```sql
CREATE INDEX idx_transactions_account_id
ON transactions(account_id);
```

Query helped by this index:

```sql
SELECT transaction_id, amount, transaction_time
FROM transactions
WHERE account_id = 1001;
```

Why it helps:

- Without an index, PostgreSQL may do a `Seq Scan` and check every row.
- With a selective index, PostgreSQL can locate rows for one account more directly.
- This is useful when a table has millions of transactions and one account has a small subset.

Performance note: An index is not free. Every `INSERT`, `UPDATE`, and `DELETE` must maintain the index. On a high-volume transaction table, unnecessary indexes can slow posting, settlement, and ingestion.

Interview answer: "I add indexes for proven access patterns. I verify with `EXPLAIN ANALYZE`, and I consider write overhead before adding indexes to hot OLTP tables."

## 2. B-Tree Index Basics

PostgreSQL's default index type is B-tree.

```sql
CREATE INDEX idx_transactions_time
ON transactions(transaction_time);
```

B-tree indexes are good for:

- Equality: `account_id = 1001`
- Range filters: `transaction_time >= TIMESTAMP '2026-06-01'`
- Sorting: `ORDER BY transaction_time`
- Prefix matching when the pattern is anchored, depending on collation and operator class

Example:

```sql
SELECT transaction_id, amount
FROM transactions
WHERE transaction_time >= TIMESTAMP '2026-06-01'
  AND transaction_time <  TIMESTAMP '2026-07-01';
```

Banking example: a regulatory report may filter transactions by posting date or transaction date. A date/time index can help if the date range is selective. If the report reads most of the table, a sequential scan may still be better.

Common follow-up: "Is a sequential scan always bad?"

Answer: No. If a query reads a large percentage of rows, sequential scan can be faster than using an index because many random heap lookups can be more expensive than scanning pages sequentially.

## 3. Composite Indexes And Column Order

Theory: A composite index uses multiple columns. Column order matters because PostgreSQL can use the leftmost prefix most directly.

```sql
CREATE INDEX idx_transactions_account_time
ON transactions(account_id, transaction_time);
```

Good query:

```sql
SELECT transaction_id, amount, transaction_time
FROM transactions
WHERE account_id = 1001
  AND transaction_time >= CURRENT_DATE - INTERVAL '30 days'
ORDER BY transaction_time;
```

Why this order works:

- `account_id` is first because the query filters to one account.
- `transaction_time` is second because it narrows by date and supports ordering inside the account.
- PostgreSQL can walk the index entries for that account in timestamp order.

Less suitable query for the same index:

```sql
SELECT transaction_id, account_id, amount
FROM transactions
WHERE transaction_time >= CURRENT_DATE - INTERVAL '1 day';
```

Why: The index starts with `account_id`, but the query does not filter by `account_id`. PostgreSQL may not be able to use it efficiently for a global date search.

Alternate index:

```sql
CREATE INDEX idx_transactions_time_account
ON transactions(transaction_time, account_id);
```

This is better for date-first reporting queries, such as "all transactions from yesterday."

Interview rule of thumb:

- Put equality filters first.
- Put range filters after equality filters.
- Include sort columns when they match the query's ordering.
- Avoid creating many overlapping composite indexes without evidence.

## 4. Descending Indexes For Recent Activity

Customer-service and mobile-banking screens often fetch the latest transactions.

```sql
CREATE INDEX idx_transactions_account_time_desc
ON transactions(account_id, transaction_time DESC);
```

Query:

```sql
SELECT transaction_id, amount, status, transaction_time
FROM transactions
WHERE account_id = 1001
ORDER BY transaction_time DESC
LIMIT 20;
```

Why it helps:

- The index groups rows by account.
- Within each account, recent transactions are already in descending order.
- `LIMIT 20` lets PostgreSQL stop early.

Banking example: "Show latest 20 card transactions for account 1001" should not sort the entire transaction history every time a support agent opens the account page.

Performance note: PostgreSQL can scan B-tree indexes backward, so an explicit `DESC` index is not always required. It matters more when there are mixed sort directions in a multi-column index.

Example:

```sql
CREATE INDEX idx_transactions_customer_time_status
ON transactions(customer_id ASC, transaction_time DESC, status ASC);
```

## 5. Covering Indexes With INCLUDE

Theory: PostgreSQL supports covering indexes using `INCLUDE`. Key columns participate in search and sort. Included columns are stored in the index only for returning data.

```sql
CREATE INDEX idx_transactions_account_time_cover
ON transactions(account_id, transaction_time DESC)
INCLUDE (transaction_id, amount, status);
```

Query:

```sql
SELECT transaction_id, amount, status
FROM transactions
WHERE account_id = 1001
ORDER BY transaction_time DESC
LIMIT 20;
```

Why it helps:

- `account_id` and `transaction_time` locate and order rows.
- `transaction_id`, `amount`, and `status` can be returned from the index.
- PostgreSQL may use an `Index Only Scan` if visibility information allows it.

Important PostgreSQL detail: An `Index Only Scan` is not guaranteed just because all selected columns are in the index. PostgreSQL also checks the visibility map to know whether it can avoid heap reads.

Tradeoff:

- Covering indexes are larger.
- Larger indexes use more memory and storage.
- Larger indexes increase write cost.

Interview answer: "I use `INCLUDE` when a high-frequency read needs a small stable column set, such as recent transactions. I avoid covering every column because that turns the index into another copy of the table."

## 6. Partial Indexes

Theory: A partial index stores only rows that match a condition.

```sql
CREATE INDEX idx_transactions_failed_account_time
ON transactions(account_id, transaction_time DESC)
WHERE status = 'FAILED';
```

Query:

```sql
SELECT transaction_id, account_id, amount, transaction_time
FROM transactions
WHERE status = 'FAILED'
  AND account_id = 1001
ORDER BY transaction_time DESC;
```

Why it helps:

- Failed transactions may be a small percentage of total transactions.
- The index is smaller than indexing every transaction.
- It supports operational dashboards that investigate failed or rejected payments.

Banking example: fraud, payment operations, or customer support may frequently ask for failed debit-card transactions for an account.

Correctness note: The query predicate must imply the partial index predicate. If the query does not include `status = 'FAILED'`, PostgreSQL usually cannot use this partial index.

Alternate:

```sql
CREATE INDEX idx_transactions_status_account_time
ON transactions(status, account_id, transaction_time DESC);
```

Use the full composite index when many statuses are queried. Use a partial index when one status is special, small, and heavily queried.

## 7. Unique Indexes And Data Integrity

Indexes are not only for speed. Unique indexes enforce correctness.

```sql
CREATE UNIQUE INDEX ux_transactions_reference_id
ON transactions(reference_id)
WHERE reference_id IS NOT NULL;
```

Why this matters:

- Payment systems often receive retries.
- A unique external reference can prevent duplicate posting.
- The partial condition allows multiple rows with `NULL` reference IDs while enforcing uniqueness for real references.

Banking example: if an upstream payment gateway sends the same `reference_id` twice, the database should reject a duplicate insert rather than double debit a customer.

Interview answer: "For money movement, I prefer enforcing idempotency at the database boundary with a unique constraint or unique index, not only in application memory."

## 8. Expression Indexes

Theory: An expression index stores the result of an expression.

Problem query:

```sql
SELECT transaction_id, amount
FROM transactions
WHERE DATE(transaction_time) = DATE '2026-06-01';
```

A normal index on `transaction_time` is not ideal because the query applies a function to the column.

Better query rewrite:

```sql
SELECT transaction_id, amount
FROM transactions
WHERE transaction_time >= TIMESTAMP '2026-06-01 00:00:00'
  AND transaction_time <  TIMESTAMP '2026-06-02 00:00:00';
```

Alternate expression index:

```sql
CREATE INDEX idx_transactions_transaction_date
ON transactions ((DATE(transaction_time)));
```

Use expression indexes when:

- The expression is common and stable.
- Rewriting the predicate is not practical.
- The index tradeoff is justified.

Performance note: The query must use the same expression shape for PostgreSQL to match the expression index reliably.

## 9. Sargability

A predicate is sargable when PostgreSQL can use an index efficiently to search.

Good:

```sql
SELECT transaction_id, amount
FROM transactions
WHERE account_id = 1001
  AND transaction_time >= TIMESTAMP '2026-06-01'
  AND transaction_time <  TIMESTAMP '2026-07-01';
```

Bad:

```sql
SELECT transaction_id, amount
FROM transactions
WHERE account_id = 1001
  AND TO_CHAR(transaction_time, 'YYYY-MM') = '2026-06';
```

Why the bad version hurts:

- It applies a function to every candidate row.
- It blocks direct use of a normal timestamp range index.
- It may force PostgreSQL to scan and filter more rows.

Banking example: monthly statements should use date ranges, not string formatting, when filtering posted transactions.

Common follow-up: "What about `LOWER(email)`?"

Answer: Either normalize email to a stored lowercase column, use PostgreSQL `citext`, or create an expression index:

```sql
CREATE INDEX idx_customers_lower_email
ON customers ((LOWER(email)));
```

## 10. Indexes For Joins

Foreign key columns are not automatically indexed in PostgreSQL.

```sql
CREATE INDEX idx_accounts_customer_id
ON accounts(customer_id);

CREATE INDEX idx_transactions_account_id
ON transactions(account_id);
```

Join query:

```sql
SELECT a.account_id,
       a.account_type,
       t.transaction_id,
       t.amount
FROM accounts a
JOIN transactions t
    ON t.account_id = a.account_id
WHERE a.customer_id = 501;
```

Why indexes help:

- `accounts(customer_id)` finds the customer's accounts.
- `transactions(account_id)` finds transactions for those accounts.
- Primary keys already index `accounts(account_id)` and `customers(customer_id)`.

Correctness note: A foreign key enforces relationship validity. An index supports performance. They are related but not the same thing.

Interview follow-up: "Should every foreign key be indexed?"

Answer: Usually yes for large OLTP tables, especially if the FK is used in joins or parent deletes/updates. But for tiny lookup tables or rarely queried FKs, verify the benefit.

## 11. Indexes For Sorting And Pagination

Offset pagination:

```sql
SELECT transaction_id, amount, transaction_time
FROM transactions
WHERE account_id = 1001
ORDER BY transaction_time DESC, transaction_id DESC
OFFSET 100000
LIMIT 20;
```

Problem: PostgreSQL still has to walk and discard many rows before returning the page.

Better keyset pagination:

```sql
SELECT transaction_id, amount, transaction_time
FROM transactions
WHERE account_id = 1001
  AND (
      transaction_time < TIMESTAMP '2026-06-04 10:30:00'
      OR (
          transaction_time = TIMESTAMP '2026-06-04 10:30:00'
          AND transaction_id < 900500
      )
  )
ORDER BY transaction_time DESC, transaction_id DESC
LIMIT 20;
```

Supporting index:

```sql
CREATE INDEX idx_transactions_account_time_id_desc
ON transactions(account_id, transaction_time DESC, transaction_id DESC);
```

Why it helps:

- The index supports the filter and stable ordering.
- The query starts after the last seen row instead of skipping a large offset.
- The `transaction_id` tie-breaker makes ordering deterministic.

Banking example: transaction history screens should not become slower as the customer scrolls deeper into history.

## 12. When PostgreSQL May Not Use An Index

PostgreSQL can ignore an index even if it exists.

Common reasons:

- The table is small.
- The filter is not selective.
- Statistics are stale.
- The query returns a large portion of the table.
- The predicate is not sargable.
- The index column order does not match the query.
- Data distribution is skewed.
- The planner estimates that sequential scan is cheaper.

Example:

```sql
SELECT transaction_id
FROM transactions
WHERE status = 'POSTED';
```

If 95 percent of rows are `POSTED`, an index on `status` alone may not help. PostgreSQL may prefer a sequential scan.

Better pattern for operational exceptions:

```sql
CREATE INDEX idx_transactions_pending_time
ON transactions(transaction_time)
WHERE status = 'PENDING';
```

This helps a payment operations queue if `PENDING` is rare and frequently monitored.

## 13. EXPLAIN And EXPLAIN ANALYZE

Use `EXPLAIN` to see the planned query. Use `EXPLAIN ANALYZE` to execute the query and show actual runtime.

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT transaction_id, amount, status
FROM transactions
WHERE account_id = 1001
ORDER BY transaction_time DESC
LIMIT 20;
```

Look for:

- `Seq Scan` on a large table for a selective query.
- `Index Scan`, `Index Only Scan`, or `Bitmap Heap Scan`.
- Expensive `Sort` nodes.
- `Nested Loop` with high loop counts.
- Large differences between estimated rows and actual rows.
- `Rows Removed by Filter`.
- Buffer reads from disk versus hits from memory.
- Sorts or hashes spilling to disk.

Common plan terms:

- `Seq Scan`: scans the full table.
- `Index Scan`: uses an index, then reads table rows.
- `Index Only Scan`: reads from the index when visibility map permits it.
- `Bitmap Index Scan`: finds matching row locations from an index.
- `Bitmap Heap Scan`: fetches heap pages after bitmap lookup.
- `Nested Loop`: joins by repeated lookup.
- `Hash Join`: builds a hash table for joining.
- `Merge Join`: joins sorted inputs.
- `Sort`: sorts rows explicitly.
- `Limit`: stops after a number of rows.

Senior interview answer: "I compare estimated rows with actual rows. If they are far apart, I investigate statistics, data skew, or missing extended statistics before blindly adding indexes."

## 14. Statistics And ANALYZE

PostgreSQL uses table statistics to choose plans.

```sql
ANALYZE transactions;
```

Why statistics matter:

- The planner estimates how many rows match a predicate.
- Bad estimates can cause bad join order or wrong scan type.
- Skewed banking data is common. For example, one merchant or account may have far more transactions than others.

When to care:

- After large batch loads.
- After major data changes.
- When estimated rows and actual rows differ significantly in `EXPLAIN ANALYZE`.

Extended statistics can help when columns are correlated:

```sql
CREATE STATISTICS st_transactions_status_type
ON status, transaction_type
FROM transactions;

ANALYZE transactions;
```

Example: `status` and `transaction_type` may be correlated if reversals or failed transactions appear mostly for certain transaction types.

## 15. Too Many Indexes

Too many indexes can hurt a banking OLTP system.

Problems:

- Slower inserts into transaction tables.
- Slower updates to account or payment status.
- More storage and backup size.
- More autovacuum and maintenance work.
- More cache pressure.
- More complex planner choices.

Example of overlapping indexes:

```sql
CREATE INDEX idx_txn_account ON transactions(account_id);
CREATE INDEX idx_txn_account_time ON transactions(account_id, transaction_time);
CREATE INDEX idx_txn_account_time_status ON transactions(account_id, transaction_time, status);
```

These may not all be needed. If the composite index satisfies the common account lookup, the single-column index may be redundant, depending on query patterns.

Interview answer: "I review real query usage before adding indexes. I remove redundant indexes carefully after validating no critical query depends on them."

## 16. Write-Heavy Banking Workloads

In write-heavy systems, every extra index creates cost.

Examples:

- Card authorization events.
- Ledger posting.
- Payment status updates.
- Fraud event ingestion.
- Real-time notification outbox inserts.

Guidelines:

- Keep indexes narrow.
- Prefer partial indexes for rare operational states.
- Avoid indexing low-cardinality columns alone, such as `status`, unless selective.
- Do not index every column used in a `WHERE` clause independently.
- Monitor insert/update latency after adding indexes.

Correctness note: Do not remove unique indexes or constraints just to improve write speed if they enforce money-movement safety.

## 17. Partitioning And Indexes

Large transaction tables are often partitioned by date.

```sql
CREATE TABLE transaction_events (
    transaction_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    transaction_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL
) PARTITION BY RANGE (transaction_time);
```

Example partition:

```sql
CREATE TABLE transaction_events_2026_06
PARTITION OF transaction_events
FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
```

Index on partitioned table:

```sql
CREATE INDEX idx_transaction_events_account_time
ON transaction_events(account_id, transaction_time DESC);
```

Why partitioning helps:

- PostgreSQL can prune partitions for date-bounded queries.
- Maintenance can be easier by month or day.
- Archival and retention operations become safer.

What partitioning does not solve:

- It does not automatically fix bad predicates.
- It does not replace useful local indexes.
- It can add operational complexity.

Banking example: keeping seven years of transaction history may require partitioning by month while keeping recent months optimized for customer-service lookups.

## 18. BRIN Indexes For Large Time-Ordered Tables

BRIN indexes are compact indexes useful when data is physically correlated with the indexed column.

```sql
CREATE INDEX idx_transactions_time_brin
ON transactions
USING BRIN (transaction_time);
```

When it helps:

- Very large append-heavy tables.
- Rows are inserted roughly in transaction-time order.
- Queries scan large date ranges.

Tradeoff:

- BRIN is much smaller than B-tree.
- BRIN is less precise and may scan extra pages.
- It is usually not the best index for finding one account's latest 20 transactions.

Banking example: a monthly audit export over a large append-only transaction table may benefit from BRIN on `transaction_time`.

## 19. Materialized Views And Precomputed Results

Sometimes indexing is not enough. Aggregations over huge data can be precomputed.

```sql
CREATE MATERIALIZED VIEW daily_account_transaction_summary AS
SELECT account_id,
       DATE(transaction_time) AS transaction_date,
       COUNT(*) AS transaction_count,
       SUM(amount) FILTER (WHERE transaction_type = 'CREDIT') AS total_credits,
       SUM(amount) FILTER (WHERE transaction_type = 'DEBIT') AS total_debits
FROM transactions
WHERE status = 'POSTED'
GROUP BY account_id, DATE(transaction_time);
```

Index the materialized view:

```sql
CREATE UNIQUE INDEX ux_daily_account_transaction_summary
ON daily_account_transaction_summary(account_id, transaction_date);
```

Refresh:

```sql
REFRESH MATERIALIZED VIEW CONCURRENTLY daily_account_transaction_summary;
```

Tradeoff:

- Reads become faster.
- Data can be stale between refreshes.
- Concurrent refresh requires a unique index.
- Operational scheduling matters.

Banking example: dashboards can use summaries, but current balances and final money movement decisions should use source-of-truth tables.

## 20. Query Rewrite Before Indexing

Sometimes the query is the problem.

Poor pattern:

```sql
SELECT *
FROM transactions
WHERE DATE(transaction_time) = CURRENT_DATE
ORDER BY transaction_time DESC;
```

Better:

```sql
SELECT transaction_id, account_id, amount, status, transaction_time
FROM transactions
WHERE transaction_time >= CURRENT_DATE
  AND transaction_time <  CURRENT_DATE + INTERVAL '1 day'
ORDER BY transaction_time DESC;
```

Why:

- Avoids function on indexed column.
- Returns only needed columns.
- Uses a range predicate.

Another poor pattern:

```sql
SELECT account_id
FROM transactions
GROUP BY account_id
HAVING COUNT(*) > 0;
```

Better:

```sql
SELECT DISTINCT account_id
FROM transactions;
```

Or if checking one account:

```sql
SELECT 1
FROM transactions
WHERE account_id = 1001
LIMIT 1;
```

Interview answer: "The smallest safe fix may be rewriting the SQL, not adding another index."

## 21. Banking Examples

### Latest Transactions For Account

Query:

```sql
SELECT transaction_id, amount, status, transaction_time
FROM transactions
WHERE account_id = 1001
ORDER BY transaction_time DESC, transaction_id DESC
LIMIT 20;
```

Index:

```sql
CREATE INDEX idx_transactions_account_time_id_desc
ON transactions(account_id, transaction_time DESC, transaction_id DESC)
INCLUDE (amount, status);
```

Why: Supports the account filter, stable ordering, and small result set.

### Failed Transactions Dashboard

Query:

```sql
SELECT account_id, transaction_id, amount, transaction_time
FROM transactions
WHERE status = 'FAILED'
  AND transaction_time >= CURRENT_TIMESTAMP - INTERVAL '1 hour'
ORDER BY transaction_time DESC;
```

Index:

```sql
CREATE INDEX idx_transactions_failed_recent
ON transactions(transaction_time DESC)
WHERE status = 'FAILED';
```

Why: Failed rows are usually a small operational subset.

### Merchant Fraud Review

Query:

```sql
SELECT transaction_id, account_id, amount, transaction_time
FROM transactions
WHERE merchant_id = 7001
  AND transaction_time >= CURRENT_TIMESTAMP - INTERVAL '24 hours'
  AND status = 'POSTED';
```

Index:

```sql
CREATE INDEX idx_transactions_merchant_time_posted
ON transactions(merchant_id, transaction_time DESC)
WHERE status = 'POSTED';
```

Why: Fraud investigation often filters by merchant and recent time window.

### Duplicate Payment Prevention

Index:

```sql
CREATE UNIQUE INDEX ux_transactions_reference_id_not_null
ON transactions(reference_id)
WHERE reference_id IS NOT NULL;
```

Why: Prevents duplicate external payment references from posting twice.

## 22. Interview Follow-Ups

### Why is column order important in a composite index?

Because PostgreSQL can use the leading columns most directly. `(account_id, transaction_time)` is good for account-specific history, while `(transaction_time, account_id)` is better for date-first reporting.

### Why might an index not be used?

The table may be small, the predicate may not be selective, statistics may be stale, the query may not match the index order, or a sequential scan may be cheaper.

### What is the difference between `Index Scan` and `Index Only Scan`?

`Index Scan` uses the index to locate rows and then reads the table. `Index Only Scan` can return data from the index itself when all needed columns are present and visibility checks allow it.

### How do you tune a slow query?

Use `EXPLAIN (ANALYZE, BUFFERS)`, compare estimates to actuals, identify scan/join/sort bottlenecks, check indexes and statistics, rewrite non-sargable predicates, then choose the smallest change with clear tradeoffs.

### Should you index every column in the `WHERE` clause?

No. Index query patterns, not isolated columns. Composite indexes should match filters, joins, sorting, and selectivity.

### How do indexes affect writes?

Every insert, update, and delete must maintain indexes. On hot transaction tables, too many indexes can slow ingestion and increase storage and maintenance.

### What index would you create for latest 20 account transactions?

```sql
CREATE INDEX idx_transactions_account_time_desc
ON transactions(account_id, transaction_time DESC);
```

If the query always returns a few additional columns, consider `INCLUDE` after validating the read benefit.

### When would you use a partial index?

Use it for frequent queries on a small subset, such as `FAILED`, `PENDING`, or active unresolved exceptions.

### When would you use BRIN instead of B-tree?

Use BRIN for very large, naturally ordered tables when queries scan broad ranges, especially timestamp ranges. Use B-tree for selective lookups and ordered small result sets.

### How do you explain performance tradeoffs in a banking interview?

Say: "For customer-facing screens, I optimize predictable low-latency reads. For posting and ledger paths, I am careful with write overhead. For money movement, I never remove constraints that protect correctness just to gain speed."
