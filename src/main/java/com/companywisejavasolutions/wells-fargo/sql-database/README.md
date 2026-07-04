# Wells Fargo SQL / Database Questions

This guide uses simple, interview-friendly SQL. Table and column names are assumed, so adjust them to match the schema given in the interview.

## PostgreSQL Concept Files

Use these files for focused PostgreSQL interview preparation:

1. [Practice Roadmap](postgres-concepts/00-practice-roadmap.md)
2. [PostgreSQL Basics](postgres-concepts/01-basics.md)
3. [PostgreSQL Aggregation](postgres-concepts/02-aggregation.md)
4. [PostgreSQL Joins](postgres-concepts/03-joins.md)
5. [PostgreSQL Subqueries](postgres-concepts/04-subqueries.md)
6. [Real Interview Patterns](postgres-concepts/05-interview-patterns.md)
7. [Window Functions](postgres-concepts/06-window-functions.md)
8. [Indexes and Performance](postgres-concepts/07-indexes-and-performance.md)
9. [Transactions and Locking](postgres-concepts/08-transactions-and-locking.md)
10. [Banking Data Design Concepts](postgres-concepts/09-banking-data-design.md)

## Wells Fargo-Specific SQL Prep

Use these files for Wells Fargo-focused SQL preparation:

1. [Wells Fargo SQL Roadmap](wells-fargo-specific/00-wells-fargo-sql-roadmap.md)
2. [Tier-Wise Detailed Answers](wells-fargo-specific/05-tier-wise-detailed-answers.md)
3. [Top 25 Practice Order](wells-fargo-specific/01-top-25-practice-order.md)
4. [Banking-Style PostgreSQL Queries](wells-fargo-specific/02-banking-style-postgres-queries.md)
5. [Senior Performance Concepts](wells-fargo-specific/03-senior-performance-concepts.md)
6. [Oracle and PL/SQL Awareness](wells-fargo-specific/04-oracle-plsql-awareness.md)

The detailed question bank below also follows PostgreSQL-style SQL.

Common tables used in examples:

- `employees(employee_id, employee_name, department_id, manager_id, salary)`
- `departments(department_id, department_name)`
- `customers(customer_id, customer_name)`
- `accounts(account_id, customer_id, account_type)`
- `transactions(transaction_id, account_id, customer_id, merchant_id, transaction_type, amount, transaction_timestamp, status, transfer_id)`
- `logins(user_id, login_timestamp, status)`
- `addresses(address_id, customer_id, address_line, is_primary)`

## SQL Queries

### 1. Find the employee with the minimum salary in each department.

Use a subquery to find the minimum salary per department, then join back to employees to get employee details. This also handles ties.

```sql
SELECT e.department_id,
       e.employee_id,
       e.employee_name,
       e.salary
FROM employees e
JOIN (
    SELECT department_id, MIN(salary) AS min_salary
    FROM employees
    GROUP BY department_id
) m
  ON e.department_id = m.department_id
 AND e.salary = m.min_salary;
```

Explanation: The inner query finds the lowest salary for every department. The outer query returns the employee or employees who have that salary.

### 2. Find departments with at least five direct reports.

Direct reports are employees who report to a manager. Group by department and count employees who have a manager.

```sql
SELECT d.department_id,
       d.department_name,
       COUNT(*) AS direct_report_count
FROM departments d
JOIN employees e
  ON d.department_id = e.department_id
WHERE e.manager_id IS NOT NULL
GROUP BY d.department_id, d.department_name
HAVING COUNT(*) >= 5;
```

Explanation: `WHERE e.manager_id IS NOT NULL` counts employees who directly report to someone. `HAVING` keeps only departments with at least five such employees.

### 3. Find the second-highest salary per department.

Use `DENSE_RANK()` so duplicate top salaries do not incorrectly skip the second distinct salary.

```sql
SELECT department_id,
       employee_id,
       employee_name,
       salary
FROM (
    SELECT e.*,
           DENSE_RANK() OVER (
               PARTITION BY department_id
               ORDER BY salary DESC
           ) AS salary_rank
    FROM employees e
) ranked
WHERE salary_rank = 2;
```

Explanation: `PARTITION BY department_id` ranks salaries inside each department. `DENSE_RANK()` returns the second distinct salary, including ties.

### 4. Find duplicate transaction IDs.

Group by transaction ID and keep only IDs that appear more than once.

```sql
SELECT transaction_id,
       COUNT(*) AS duplicate_count
FROM transactions
GROUP BY transaction_id
HAVING COUNT(*) > 1;
```

Explanation: In a well-designed system, `transaction_id` should usually be unique. This query helps find data quality issues.

### 5. Find customers who made transactions on three consecutive days.

First remove duplicate transaction dates per customer, then check whether the next two dates are exactly one and two days after the current date.

```sql
WITH customer_days AS (
    SELECT DISTINCT customer_id,
           CAST(transaction_timestamp AS DATE) AS transaction_date
    FROM transactions
),
day_check AS (
    SELECT customer_id,
           transaction_date,
           LEAD(transaction_date, 1) OVER (
               PARTITION BY customer_id
               ORDER BY transaction_date
           ) AS next_day,
           LEAD(transaction_date, 2) OVER (
               PARTITION BY customer_id
               ORDER BY transaction_date
           ) AS third_day
    FROM customer_days
)
SELECT DISTINCT customer_id
FROM day_check
WHERE next_day = transaction_date + INTERVAL '1 day'
  AND third_day = transaction_date + INTERVAL '2 day';
```

Explanation: `LEAD()` looks at future rows. If a customer has transactions on Monday, Tuesday, and Wednesday, the condition matches.

### 6. Calculate running balance per account ordered by transaction timestamp.

Treat credits as positive and debits as negative, then calculate a running sum.

```sql
SELECT account_id,
       transaction_id,
       transaction_timestamp,
       transaction_type,
       amount,
       SUM(
           CASE
               WHEN transaction_type = 'CREDIT' THEN amount
               WHEN transaction_type = 'DEBIT' THEN -amount
               ELSE 0
           END
       ) OVER (
           PARTITION BY account_id
           ORDER BY transaction_timestamp, transaction_id
       ) AS running_balance
FROM transactions;
```

Explanation: The window function keeps adding transaction amounts in timestamp order for each account.

### 7. Find accounts whose total debit exceeds total credit in the last 30 days.

Use conditional aggregation to calculate debit and credit totals separately.

```sql
SELECT account_id,
       SUM(CASE WHEN transaction_type = 'DEBIT' THEN amount ELSE 0 END) AS total_debit,
       SUM(CASE WHEN transaction_type = 'CREDIT' THEN amount ELSE 0 END) AS total_credit
FROM transactions
WHERE transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '30 days'
GROUP BY account_id
HAVING SUM(CASE WHEN transaction_type = 'DEBIT' THEN amount ELSE 0 END)
     > SUM(CASE WHEN transaction_type = 'CREDIT' THEN amount ELSE 0 END);
```

Explanation: This is useful for spotting accounts where outgoing money is greater than incoming money during a time period.

### 8. Find latest transaction per account.

Rank transactions inside each account by timestamp descending.

```sql
SELECT account_id,
       transaction_id,
       amount,
       transaction_timestamp
FROM (
    SELECT t.*,
           ROW_NUMBER() OVER (
               PARTITION BY account_id
               ORDER BY transaction_timestamp DESC, transaction_id DESC
           ) AS row_num
    FROM transactions t
) ranked
WHERE row_num = 1;
```

Explanation: `ROW_NUMBER()` returns one latest row per account. `transaction_id` is added as a tie-breaker.

### 9. Find customers who have checking but no savings account.

Use `EXISTS` and `NOT EXISTS` to express the requirement clearly.

```sql
SELECT c.customer_id,
       c.customer_name
FROM customers c
WHERE EXISTS (
    SELECT 1
    FROM accounts a
    WHERE a.customer_id = c.customer_id
      AND a.account_type = 'CHECKING'
)
AND NOT EXISTS (
    SELECT 1
    FROM accounts a
    WHERE a.customer_id = c.customer_id
      AND a.account_type = 'SAVINGS'
);
```

Explanation: The customer must have at least one checking account and zero savings accounts.

### 10. Find top 10 merchants by transaction volume this month.

Count transactions per merchant for the current month.

```sql
SELECT merchant_id,
       COUNT(*) AS transaction_count
FROM transactions
WHERE transaction_timestamp >= DATE_TRUNC('month', CURRENT_DATE)
  AND transaction_timestamp <  DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'
GROUP BY merchant_id
ORDER BY transaction_count DESC
LIMIT 10;
```

Explanation: Transaction volume means count of transactions, not dollar amount. If the interviewer means amount, use `SUM(amount)`.

### 11. Find failed login count per user in rolling one-hour windows.

For each failed login, count failures by the same user in the previous one hour.

```sql
SELECT l1.user_id,
       l1.login_timestamp AS window_end_time,
       COUNT(*) AS failed_login_count
FROM logins l1
JOIN logins l2
  ON l1.user_id = l2.user_id
 AND l2.status = 'FAILED'
 AND l2.login_timestamp BETWEEN l1.login_timestamp - INTERVAL '1 hour'
                            AND l1.login_timestamp
WHERE l1.status = 'FAILED'
GROUP BY l1.user_id, l1.login_timestamp
ORDER BY l1.user_id, l1.login_timestamp;
```

Explanation: Each failed login becomes the end of a rolling one-hour window. This is often used for fraud or security monitoring.

### 12. Find all transfers where debit exists but matching credit is missing.

Assume both sides of a transfer share the same `transfer_id`.

```sql
SELECT d.transfer_id,
       d.transaction_id AS debit_transaction_id,
       d.account_id AS debit_account_id,
       d.amount
FROM transactions d
WHERE d.transaction_type = 'DEBIT'
  AND d.transfer_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM transactions c
      WHERE c.transfer_id = d.transfer_id
        AND c.transaction_type = 'CREDIT'
        AND c.amount = d.amount
  );
```

Explanation: A valid transfer should normally have both a debit entry and a matching credit entry. Missing credits can indicate incomplete processing.

### 13. Find transactions above the customer average amount.

Calculate the average amount for each customer, then compare each transaction to that average.

```sql
SELECT transaction_id,
       customer_id,
       amount,
       transaction_timestamp
FROM (
    SELECT t.*,
           AVG(amount) OVER (
               PARTITION BY customer_id
           ) AS customer_avg_amount
    FROM transactions t
) x
WHERE amount > customer_avg_amount;
```

Explanation: Window functions let us keep row-level transaction details while also calculating a customer-level average.

### 14. Find customers with more than one address marked primary.

Group primary addresses by customer and count them.

```sql
SELECT customer_id,
       COUNT(*) AS primary_address_count
FROM addresses
WHERE is_primary = 'Y'
GROUP BY customer_id
HAVING COUNT(*) > 1;
```

Explanation: Usually a customer should have only one primary address. This query finds invalid customer records.

### 15. Paginate transaction search results reliably.

Use keyset pagination instead of large offsets. It is more stable when new rows are inserted.

First page:

```sql
SELECT transaction_id,
       account_id,
       amount,
       transaction_timestamp
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp DESC, transaction_id DESC
LIMIT 20;
```

Next page, using the last row from the previous page:

```sql
SELECT transaction_id,
       account_id,
       amount,
       transaction_timestamp
FROM transactions
WHERE account_id = 101
  AND (
      transaction_timestamp <  TIMESTAMP '2026-06-01 10:15:00'
      OR (
          transaction_timestamp = TIMESTAMP '2026-06-01 10:15:00'
          AND transaction_id < 5001
      )
  )
ORDER BY transaction_timestamp DESC, transaction_id DESC
LIMIT 20;
```

Explanation: The timestamp and ID together create a stable sort order. The next page starts after the last seen row.

## Database Concepts

### 16. Explain normalization and denormalization with banking examples.

Normalization means organizing data to reduce duplication and avoid update mistakes.

Example: Instead of storing customer name and address in every transaction row, store customer details in `customers`, account details in `accounts`, and transaction details in `transactions`.

Denormalization means intentionally adding duplicated or precomputed data to improve read performance.

Example: A monthly statement table may store `customer_name`, `account_number`, and `closing_balance` as a snapshot, even though those values can be derived from other tables.

Interview answer: Normalize core banking data for correctness. Denormalize reports, dashboards, and statements when reads need to be faster.

### 17. 1NF, 2NF, 3NF, BCNF.

1NF means each column has atomic values, not lists.

Example: Do not store multiple phone numbers in one column like `phone_numbers = '111,222'`. Use a separate `customer_phones` table.

2NF means the table is in 1NF and non-key columns depend on the whole primary key.

Example: In a table with key `(account_id, transaction_id)`, account holder name should not depend only on `account_id`.

3NF means the table is in 2NF and non-key columns do not depend on other non-key columns.

Example: If `zip_code` determines `city`, do not store city as dependent data in a customer table unless needed.

BCNF is a stricter version of 3NF where every determinant must be a candidate key.

Interview answer: These forms reduce redundancy and make updates safer.

### 18. Primary key vs unique key vs foreign key.

Primary key uniquely identifies each row and cannot be null.

Unique key ensures values are unique, but many databases allow nulls depending on the database.

Foreign key links one table to another table.

Example:

```sql
CREATE TABLE accounts (
    account_id  BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_no  VARCHAR(30) UNIQUE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
```

Interview answer: A primary key identifies the row. A unique key prevents duplicate values. A foreign key enforces relationships between tables.

### 19. Clustered vs non-clustered index.

In PostgreSQL, normal indexes are separate structures from the table. PostgreSQL does not maintain a permanent clustered index like SQL Server.

PostgreSQL has a `CLUSTER` command that can physically reorder a table based on an index once, but that order is not automatically maintained after future writes.

Most PostgreSQL interview discussions focus on regular B-tree indexes, composite indexes, partial indexes, and covering indexes with `INCLUDE`.

Interview answer: In PostgreSQL, think of indexes as separate lookup structures. Use them to speed up filters, joins, ordering, and range scans.

### 20. Composite index order: why does column order matter?

In a composite index like `(account_id, transaction_timestamp)`, the database can efficiently search by `account_id`, or by `account_id` plus timestamp. It usually cannot use the same index efficiently for timestamp alone.

Example:

```sql
CREATE INDEX idx_txn_account_time
ON transactions(account_id, transaction_timestamp);
```

Good query:

```sql
SELECT *
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp;
```

Less effective query:

```sql
SELECT *
FROM transactions
WHERE transaction_timestamp >= CURRENT_DATE;
```

Interview answer: Put equality filters first, then range filters or sort columns, based on the most common query pattern.

### 21. Covering index.

A covering index contains all columns needed by a query, so the database can answer from the index without reading the full table.

Example:

```sql
CREATE INDEX idx_txn_covering
ON transactions(account_id, transaction_timestamp, amount, transaction_id);
```

Query:

```sql
SELECT transaction_id, transaction_timestamp, amount
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp;
```

Interview answer: Covering indexes improve read performance, but they increase storage and write cost.

### 22. How can too many indexes hurt performance?

Indexes speed up reads but slow down writes. Every insert, update, or delete may need to update multiple indexes.

Problems caused by too many indexes:

- More storage usage
- Slower inserts and updates
- More maintenance during rebalancing
- More choices for the optimizer, sometimes leading to bad plans

Banking example: A high-volume transaction table should not have unnecessary indexes because every card swipe, transfer, or deposit creates writes.

### 23. How do you analyze a slow query?

Steps:

1. Reproduce the slow query with realistic parameters.
2. Check the execution plan using `EXPLAIN` or `EXPLAIN ANALYZE`.
3. Look for full table scans, bad joins, large sorts, and missing indexes.
4. Check whether statistics are stale.
5. Review filters, joins, grouping, and ordering.
6. Add or adjust indexes only when they match a real query pattern.
7. Test again and compare before/after performance.

Interview answer: Do not guess. Use the execution plan and data distribution to find the bottleneck.

### 24. Explain query execution plan.

A query execution plan shows how the database plans to run a SQL query.

It can show:

- Table scans
- Index scans or seeks
- Join order
- Join type, such as nested loop, hash join, or merge join
- Sorts and aggregations
- Estimated and actual row counts

Interview answer: The plan helps explain why a query is slow and whether indexes or query changes will help.

### 25. Explain ACID properties.

ACID describes transaction reliability.

Atomicity: All steps succeed or all steps roll back.

Consistency: Data moves from one valid state to another valid state.

Isolation: Concurrent transactions do not interfere incorrectly.

Durability: Once committed, data remains saved even after a crash.

Banking example: In a fund transfer, debit and credit must both happen, constraints must remain valid, other users should not see partial updates, and committed transfers must survive failures.

### 26. Isolation levels: read uncommitted, read committed, repeatable read, serializable.

Read uncommitted allows reading uncommitted changes. It is fastest but can read dirty data.

Read committed only reads committed data. It prevents dirty reads.

Repeatable read ensures rows read once in a transaction stay the same if read again.

Serializable is the strictest level. It behaves like transactions ran one after another.

Interview answer: Higher isolation improves correctness but may reduce concurrency.

### 27. Dirty read, non-repeatable read, phantom read.

Dirty read: A transaction reads data another transaction has not committed yet.

Non-repeatable read: A transaction reads the same row twice and gets different values because another transaction updated it.

Phantom read: A transaction repeats a search and sees new rows inserted by another transaction.

Banking example: A dirty read could show money that is later rolled back. A non-repeatable read could show a changed balance. A phantom read could change the count of transactions matching a fraud rule.

### 28. Optimistic locking vs pessimistic locking.

Optimistic locking assumes conflicts are rare. It checks a version number before updating.

```sql
UPDATE accounts
SET balance = balance - 100,
    version = version + 1
WHERE account_id = 101
  AND version = 5;
```

If zero rows are updated, someone else changed the row first.

Pessimistic locking assumes conflicts are likely. It locks the row before updating.

```sql
SELECT balance
FROM accounts
WHERE account_id = 101
FOR UPDATE;
```

Interview answer: Use optimistic locking for low-conflict systems and pessimistic locking when financial correctness requires blocking concurrent changes.

### 29. What is a deadlock in a database?

A deadlock happens when two or more transactions wait for each other forever.

Example:

- Transaction A locks account 1 and waits for account 2.
- Transaction B locks account 2 and waits for account 1.

The database detects this cycle and usually aborts one transaction.

Interview answer: Deadlocks are caused by inconsistent lock ordering or long transactions. Applications should retry aborted transactions safely.

### 30. How would you avoid deadlocks during fund transfers?

Use a consistent lock order. For example, always lock the smaller account ID first.

```sql
BEGIN;

SELECT account_id, balance
FROM accounts
WHERE account_id IN (101, 202)
ORDER BY account_id
FOR UPDATE;

UPDATE accounts
SET balance = balance - 100
WHERE account_id = 101;

UPDATE accounts
SET balance = balance + 100
WHERE account_id = 202;

COMMIT;
```

Other practices:

- Keep transactions short.
- Do not wait for user input inside a transaction.
- Access tables in the same order across services.
- Retry safely when the database reports a deadlock.

### 31. Explain write-ahead logging.

Write-ahead logging means the database writes the change record to a log before changing the actual data pages.

Why it matters:

- Helps recover committed transactions after a crash
- Helps roll back uncommitted transactions
- Supports replication in many databases

Banking example: If a transfer commits and the server crashes immediately after, the log helps recover the committed debit and credit.

### 32. Replication vs sharding vs partitioning.

Replication copies the same data to multiple database nodes.

Use it for high availability and read scaling.

Sharding splits data across multiple databases or servers.

Use it when one database cannot handle the total data or traffic.

Partitioning splits a table into smaller parts within a database system.

Use it to improve manageability and query performance for large tables.

Banking example: Transactions may be partitioned by month, replicated for availability, and sharded by customer or account for scale.

### 33. Horizontal vs vertical partitioning.

Horizontal partitioning splits rows.

Example: Store January transactions in one partition and February transactions in another.

Vertical partitioning splits columns.

Example: Store frequently used account columns in one table and rarely used audit columns in another table.

Interview answer: Horizontal partitioning helps with large row counts. Vertical partitioning helps when some columns are large or rarely needed.

### 34. How do you choose a sharding key?

A good sharding key should:

- Distribute data evenly
- Match common query patterns
- Avoid hot shards
- Minimize cross-shard transactions
- Be stable and not frequently updated

Banking example: `customer_id` or `account_id` can be a good sharding key if most queries are customer/account based.

Poor choice: `country` if most customers are from one country. That creates uneven shards.

### 35. What happens when one shard becomes hot?

A hot shard receives much more traffic than other shards. It can become slow while other shards are underused.

Causes:

- Uneven sharding key
- Large customer or merchant concentrated on one shard
- Time-based shard receiving all current writes

Solutions:

- Split the hot shard
- Add sub-sharding
- Use better key hashing
- Cache read-heavy data
- Move heavy tenants to dedicated shards

Interview answer: The goal is to spread traffic and data more evenly without breaking correctness.

### 36. SQL vs NoSQL for banking transaction records.

SQL is usually preferred for core banking transactions because it provides strong consistency, transactions, constraints, joins, and mature auditing.

NoSQL can be useful for high-volume, flexible, or non-core workloads like activity feeds, session data, fraud signals, or logs.

Interview answer: Use relational databases for the source of truth in banking ledgers. Use NoSQL when scale or flexible schema is more important than relational consistency.

### 37. When would you use Redis with a relational database?

Redis is useful as a fast in-memory cache or temporary data store.

Banking examples:

- Cache customer profile summary
- Store login rate-limit counters
- Store short-lived OTP/session data
- Cache merchant metadata
- Support idempotency keys for a short time

Interview answer: Redis should not replace the relational source of truth for balances or final transaction records.

### 38. Cache-aside vs write-through vs write-behind.

Cache-aside: Application checks cache first. If missing, it reads from database and stores in cache.

Write-through: Application writes to cache and database together.

Write-behind: Application writes to cache first, and cache later writes to database asynchronously.

Banking answer:

- Cache-aside is common for read-heavy reference data.
- Write-through can keep cache and database more aligned.
- Write-behind is risky for critical financial data because a failure can lose writes.

### 39. How do you invalidate cached account data safely?

For account data, safety matters more than speed.

Good practices:

- Do not cache final balances unless there is a strong consistency strategy.
- Invalidate or update cache immediately after database commit.
- Use short TTLs for sensitive data.
- Use version numbers to avoid serving stale values.
- Publish events after commits to refresh dependent caches.

Interview answer: The database remains the source of truth. Cache invalidation should happen after successful commit, not before.

### 40. Event sourcing vs traditional relational updates.

Traditional relational update stores current state directly.

Example: Update account balance from `1000` to `900`.

Event sourcing stores every change as an event.

Example: Store `AccountDebited(account_id=101, amount=100)`, then derive balance from events.

Banking answer: Event sourcing is close to ledger thinking because history is preserved. Traditional updates are simpler, but need strong audit tables for financial systems.

### 41. How do you model immutable ledger entries?

Use append-only ledger rows. Do not update or delete posted ledger entries.

Example:

```sql
CREATE TABLE ledger_entries (
    ledger_entry_id BIGINT PRIMARY KEY,
    transaction_id  BIGINT NOT NULL,
    account_id      BIGINT NOT NULL,
    entry_type      VARCHAR(10) NOT NULL,
    amount          DECIMAL(18, 2) NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    created_by      VARCHAR(50) NOT NULL
);
```

For a transfer, insert two entries:

```sql
INSERT INTO ledger_entries
    (ledger_entry_id, transaction_id, account_id, entry_type, amount, created_at, created_by)
VALUES
    (1, 9001, 101, 'DEBIT', 100.00, CURRENT_TIMESTAMP, 'SYSTEM'),
    (2, 9001, 202, 'CREDIT', 100.00, CURRENT_TIMESTAMP, 'SYSTEM');
```

Interview answer: Corrections should be new reversing entries, not updates to old entries.

### 42. How do you handle schema migrations with zero downtime?

Use an expand-and-contract approach.

Steps:

1. Add the new column or table without breaking old code.
2. Deploy code that can write both old and new fields if needed.
3. Backfill existing data in small batches.
4. Switch reads to the new field.
5. Stop writing the old field.
6. Drop old columns only after all services are updated.

Interview answer: Make each deployment backward compatible so old and new application versions can run at the same time.

### 43. How do you backfill a new column in a large table?

Backfill in small batches instead of updating the whole table at once.

Example:

```sql
UPDATE customers
SET full_name = first_name || ' ' || last_name
WHERE full_name IS NULL
  AND customer_id BETWEEN 1 AND 10000;
```

Then repeat with the next ID range.

Good practices:

- Batch by primary key range.
- Commit each batch.
- Run during low-traffic periods if possible.
- Monitor locks, replication lag, and error rate.
- Make the script restartable.

Interview answer: Large backfills should be controlled, observable, and safe to retry.

### 44. How do you mask PII in non-production databases?

PII includes names, addresses, phone numbers, emails, SSNs, account numbers, and card numbers.

Masking approaches:

- Replace values with fake but realistic data.
- Hash values when matching is needed but the real value is not needed.
- Keep only last four digits for account/card numbers.
- Remove highly sensitive fields entirely.

Example:

```sql
UPDATE customers
SET customer_name = 'Customer ' || customer_id,
    email = 'customer' || customer_id || '@example.com',
    phone_number = '0000000000';
```

Interview answer: Non-production data should preserve testing usefulness without exposing real customer information.

### 45. How do you reconcile two financial data sources?

Reconciliation compares two systems to find missing, mismatched, or extra records.

Example tables:

- `core_transactions(transaction_id, amount, transaction_date)`
- `settlement_transactions(transaction_id, amount, transaction_date)`

Find records missing in settlement:

```sql
SELECT c.transaction_id,
       c.amount,
       c.transaction_date
FROM core_transactions c
LEFT JOIN settlement_transactions s
  ON c.transaction_id = s.transaction_id
WHERE s.transaction_id IS NULL;
```

Find amount mismatches:

```sql
SELECT c.transaction_id,
       c.amount AS core_amount,
       s.amount AS settlement_amount
FROM core_transactions c
JOIN settlement_transactions s
  ON c.transaction_id = s.transaction_id
WHERE c.amount <> s.amount;
```

Interview answer: Reconciliation should compare keys, amounts, dates, and statuses. Differences should be reported, investigated, and corrected with auditable entries.
