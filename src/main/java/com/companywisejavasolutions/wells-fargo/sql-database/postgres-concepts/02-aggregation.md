# PostgreSQL Aggregation

Aggregation questions test whether you can summarize many rows into useful business metrics using `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`, `GROUP BY`, and `HAVING`. In Wells Fargo-style interviews, aggregation usually appears in transaction reporting, fraud monitoring, reconciliation, daily volume dashboards, account activity summaries, or customer segmentation.

Use PostgreSQL syntax in all examples.

## Sample Table

```sql
CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    transaction_type VARCHAR(10) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_time TIMESTAMP NOT NULL
);
```

```sql
INSERT INTO transactions VALUES
(1, 101, 1001, 'CREDIT', 5000.00, 'SUCCESS', '2024-05-01 09:00:00'),
(2, 101, 1001, 'DEBIT', 1200.00, 'SUCCESS', '2024-05-02 10:00:00'),
(3, 102, 1002, 'CREDIT', 7000.00, 'SUCCESS', '2024-05-03 11:00:00'),
(4, 102, 1002, 'DEBIT', 3000.00, 'FAILED',  '2024-05-04 12:00:00'),
(5, 103, 1003, 'DEBIT', 800.00,  'SUCCESS', '2024-05-05 13:00:00'),
(6, 101, 1001, 'DEBIT', 400.00,  'SUCCESS', '2024-06-01 09:30:00'),
(7, 104, 1004, 'CREDIT', 9000.00, 'SUCCESS', '2024-06-02 15:15:00');
```

## Aggregation Mental Model

Without `GROUP BY`, aggregate functions summarize the whole filtered result into one row:

```sql
SELECT COUNT(*) AS total_transactions
FROM transactions;
```

With `GROUP BY`, aggregate functions summarize each group separately:

```sql
SELECT customer_id,
       COUNT(*) AS transaction_count
FROM transactions
GROUP BY customer_id;
```

Logical processing order for aggregation:

```text
FROM -> WHERE -> GROUP BY -> HAVING -> SELECT -> ORDER BY -> LIMIT
```

Why this matters:

- `WHERE` filters individual rows before aggregation.
- `GROUP BY` creates groups.
- Aggregate functions calculate one result per group.
- `HAVING` filters groups after aggregation.
- `ORDER BY` sorts the final grouped result.

Interview follow-up: If asked why `WHERE COUNT(*) >= 2` is invalid, answer that `WHERE` runs before groups and aggregate values exist. Use `HAVING COUNT(*) >= 2`.

## COUNT

### Concept

`COUNT` counts rows or non-null values.

```sql
SELECT COUNT(*) AS total_transactions
FROM transactions;
```

### Simple Example Meaning

This returns the total number of transaction rows in the table.

### Why And How The SQL Works

`COUNT(*)` counts every row that reaches the aggregate step. It does not care whether individual columns contain nulls.

### PostgreSQL Note: COUNT Variants

```sql
SELECT COUNT(*) AS row_count,
       COUNT(transaction_id) AS transaction_id_count,
       COUNT(DISTINCT customer_id) AS unique_customer_count
FROM transactions;
```

Meaning:

- `COUNT(*)` counts all rows.
- `COUNT(transaction_id)` counts rows where `transaction_id` is not null.
- `COUNT(DISTINCT customer_id)` counts unique customers.

Because `transaction_id` is the primary key and not null, `COUNT(*)` and `COUNT(transaction_id)` return the same value here.

### Performance And Correctness Note

`COUNT(*)` on a large PostgreSQL table may still need to scan many rows, especially with a `WHERE` clause. PostgreSQL does not maintain an always-exact row count for every table query. For dashboards, teams often precompute counts by day, product, or status.

### Interview Follow-Ups

- What is the difference between `COUNT(*)` and `COUNT(column_name)`?
- How do you count unique customers?
- Why can `COUNT(*)` be expensive on a very large table?

## SUM

### Concept

`SUM` adds numeric values.

```sql
SELECT SUM(amount) AS total_amount
FROM transactions;
```

### Simple Example Meaning

This returns the sum of all transaction amounts, regardless of whether the transaction is a credit, debit, success, or failure.

### Why And How The SQL Works

PostgreSQL reads each row that passes filtering and adds the `amount` values into one total.

### Correctness Note

In banking queries, always clarify what amount means. A debit amount may be stored as a positive number with `transaction_type = 'DEBIT'`, or it may be stored as a negative ledger movement. The sample table stores all amounts as positive values, so credits and debits must be interpreted using `transaction_type`.

To sum only successful transactions:

```sql
SELECT SUM(amount) AS successful_amount
FROM transactions
WHERE status = 'SUCCESS';
```

### PostgreSQL Note: NUMERIC For Money

`NUMERIC(12, 2)` is appropriate for exact decimal values. Avoid floating-point types like `REAL` or `DOUBLE PRECISION` for money because they can introduce rounding behavior that is not acceptable for financial calculations.

### Interview Follow-Ups

- Should failed transactions be included in total volume?
- Are debit amounts stored as positive values or negative values?
- Why is `NUMERIC` safer than floating-point for money?

## AVG

### Concept

`AVG` calculates the average of numeric values.

```sql
SELECT AVG(amount) AS average_transaction_amount
FROM transactions;
```

### Simple Example Meaning

This returns the average transaction amount across all rows.

### Why And How The SQL Works

PostgreSQL calculates the sum of non-null `amount` values and divides by the count of non-null `amount` values.

### Correctness Note

Average transaction amount can be misleading if credits and debits are mixed, failed transactions are included, or outliers exist. A high-value wire transfer can heavily skew the average.

For successful debit average:

```sql
SELECT AVG(amount) AS average_successful_debit
FROM transactions
WHERE status = 'SUCCESS'
  AND transaction_type = 'DEBIT';
```

### Alternate Approach

To show both average and transaction count, include both metrics:

```sql
SELECT AVG(amount) AS average_amount,
       COUNT(*) AS transaction_count
FROM transactions
WHERE status = 'SUCCESS';
```

The count helps the interviewer see whether the average is based on enough rows to be meaningful.

### Interview Follow-Ups

- Does `AVG` ignore nulls?
- Why can average be misleading for transaction analysis?
- How would you calculate average debit amount only?

## MIN and MAX

### Concept

`MIN` returns the lowest value and `MAX` returns the highest value.

```sql
SELECT MIN(amount) AS minimum_amount,
       MAX(amount) AS maximum_amount
FROM transactions;
```

### Simple Example Meaning

This returns the smallest and largest transaction amounts in the table.

### Why And How The SQL Works

PostgreSQL compares all `amount` values that pass filtering and returns the lowest and highest.

### Banking Use Case

Minimum and maximum values help detect outliers, validate transaction limits, or understand activity ranges.

```sql
SELECT customer_id,
       MIN(amount) AS smallest_transaction,
       MAX(amount) AS largest_transaction
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY customer_id;
```

### Performance Note

An index on `amount` can help some min/max lookups:

```sql
CREATE INDEX idx_transactions_amount
ON transactions (amount);
```

For grouped min/max by customer, a composite index may be more useful:

```sql
CREATE INDEX idx_transactions_customer_amount
ON transactions (customer_id, amount);
```

### Interview Follow-Ups

- How would you find each customer's largest transaction?
- How would you return the full row for the largest transaction, not only the amount?
- Should failed transactions be considered for min/max reports?

## GROUP BY

### Concept

`GROUP BY` creates one result row per group.

```sql
SELECT customer_id,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
GROUP BY customer_id;
```

### Simple Example Meaning

This returns one row per customer, showing how many transactions each customer has and the total amount for those transactions.

### Why And How The SQL Works

PostgreSQL groups rows with the same `customer_id`. Inside each group, `COUNT(*)` counts rows and `SUM(amount)` adds amounts.

Every selected column must be either:

- included in the `GROUP BY`, or
- wrapped in an aggregate function.

This is invalid:

```sql
SELECT customer_id,
       account_id,
       COUNT(*) AS transaction_count
FROM transactions
GROUP BY customer_id;
```

`account_id` is not grouped or aggregated, so PostgreSQL cannot know which account id to display for a customer with multiple accounts.

### Correctness Note

Grouping by the wrong level changes the meaning:

```sql
SELECT customer_id,
       account_id,
       COUNT(*) AS transaction_count
FROM transactions
GROUP BY customer_id, account_id;
```

This returns one row per customer-account pair, not one row per customer.

### Interview Follow-Ups

- Why must non-aggregated selected columns appear in `GROUP BY`?
- What is the difference between grouping by customer and grouping by account?
- How would you group successful transactions only?

## WHERE With Aggregation

### Concept

`WHERE` filters rows before grouping.

```sql
SELECT customer_id,
       COUNT(*) AS successful_transaction_count,
       SUM(amount) AS successful_total_amount
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY customer_id;
```

### Simple Example Meaning

This summarizes successful transactions per customer and ignores failed transactions before the aggregation happens.

### Why And How The SQL Works

PostgreSQL first removes rows where `status` is not `SUCCESS`. Then it groups the remaining rows by customer and calculates counts and sums.

### Performance Note

If most queries filter by status and time, this index can help:

```sql
CREATE INDEX idx_transactions_status_time_customer
ON transactions (status, transaction_time, customer_id);
```

If successful transactions are a small subset, a partial index can be useful:

```sql
CREATE INDEX idx_transactions_success_time_customer
ON transactions (transaction_time, customer_id)
WHERE status = 'SUCCESS';
```

### Interview Follow-Ups

- Should status filtering happen in `WHERE` or `HAVING`?
- How would you filter for successful May 2024 transactions?
- What index supports status and date-range reporting?

## HAVING

### Concept

`HAVING` filters groups after aggregation.

```sql
SELECT customer_id,
       COUNT(*) AS transaction_count
FROM transactions
GROUP BY customer_id
HAVING COUNT(*) >= 2;
```

### Simple Example Meaning

This returns customers who have at least two transactions.

### Why And How The SQL Works

PostgreSQL groups transactions by customer, counts rows inside each customer group, and then keeps only groups where the count is at least two.

### WHERE vs HAVING

Use `WHERE` for row-level conditions:

```sql
WHERE status = 'SUCCESS'
```

Use `HAVING` for aggregate conditions:

```sql
HAVING COUNT(*) >= 2
```

Combined example:

```sql
SELECT customer_id,
       COUNT(*) AS successful_transaction_count
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY customer_id
HAVING COUNT(*) >= 2;
```

This means: first keep successful rows, then find customers with at least two successful transactions.

### Alternate Approach

You can calculate grouped results in a CTE and filter outside:

```sql
WITH customer_summary AS (
    SELECT customer_id,
           COUNT(*) AS transaction_count
    FROM transactions
    GROUP BY customer_id
)
SELECT customer_id, transaction_count
FROM customer_summary
WHERE transaction_count >= 2;
```

This can be easier to read when the aggregation is complex.

### Interview Follow-Ups

- Why is `HAVING COUNT(*) >= 2` valid but `WHERE COUNT(*) >= 2` invalid?
- Can `HAVING` be used without `GROUP BY`?
- When would a CTE make an aggregation query clearer?

## Conditional Aggregation With CASE

### Concept

Conditional aggregation calculates multiple filtered metrics in one grouped query.

```sql
SELECT account_id,
       SUM(CASE WHEN transaction_type = 'CREDIT' THEN amount ELSE 0 END) AS total_credit,
       SUM(CASE WHEN transaction_type = 'DEBIT' THEN amount ELSE 0 END) AS total_debit
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY account_id;
```

### Simple Example Meaning

This returns one row per account with separate successful credit and debit totals.

### Why And How The SQL Works

The `CASE` expression returns `amount` only for matching rows. For non-matching rows, it returns `0`. `SUM` then adds those values inside each account group.

For a credit row:

```text
SUM(CASE WHEN transaction_type = 'CREDIT' THEN amount ELSE 0 END)
```

adds the amount.

For a debit row, it adds `0` to the credit total.

### PostgreSQL Alternate: FILTER

PostgreSQL supports the `FILTER` clause, which is often cleaner:

```sql
SELECT account_id,
       SUM(amount) FILTER (WHERE transaction_type = 'CREDIT') AS total_credit,
       SUM(amount) FILTER (WHERE transaction_type = 'DEBIT') AS total_debit,
       COUNT(*) FILTER (WHERE transaction_type = 'DEBIT') AS debit_count
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY account_id;
```

### Correctness Note

`SUM(...) FILTER (...)` returns `NULL` if no rows match that condition. If the report should show zero, use `COALESCE`:

```sql
SELECT account_id,
       COALESCE(SUM(amount) FILTER (WHERE transaction_type = 'CREDIT'), 0) AS total_credit,
       COALESCE(SUM(amount) FILTER (WHERE transaction_type = 'DEBIT'), 0) AS total_debit
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY account_id;
```

### Interview Follow-Ups

- How do you calculate debit and credit totals in one query?
- What is PostgreSQL's `FILTER` clause?
- Why might `SUM(...) FILTER (...)` return null?

## Net Account Movement

### Business Question

Calculate net movement per account, where credits increase balance and debits decrease balance.

### Query

```sql
SELECT account_id,
       SUM(
           CASE
               WHEN transaction_type = 'CREDIT' THEN amount
               WHEN transaction_type = 'DEBIT' THEN -amount
               ELSE 0
           END
       ) AS net_movement
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY account_id;
```

### Why And How The SQL Works

The `CASE` expression converts each transaction into a signed value:

- Credit `5000.00` becomes `+5000.00`.
- Debit `1200.00` becomes `-1200.00`.

Then `SUM` adds the signed values per account.

### Correctness Note

This query assumes `amount` is always stored as positive and `transaction_type` gives the direction. If the database stores debits as negative amounts, this query would double-negate debits and produce wrong results.

Production systems may also have reversals, chargebacks, pending authorizations, and settlement status. Clarify which statuses belong in the official balance calculation.

### Interview Follow-Ups

- How are debits represented in the source system?
- Should pending transactions affect net movement?
- How would reversals be handled?

## DATE_TRUNC For Daily, Monthly, And Yearly Aggregation

### Concept

`DATE_TRUNC` rounds a timestamp down to a chosen boundary. It is commonly used in PostgreSQL reporting queries.

Concrete examples:

```sql
SELECT DATE_TRUNC('day', TIMESTAMP '2024-05-17 14:35:20') AS day_start,
       DATE_TRUNC('month', TIMESTAMP '2024-05-17 14:35:20') AS month_start,
       DATE_TRUNC('year', TIMESTAMP '2024-05-17 14:35:20') AS year_start;
```

Result meaning:

```text
day_start   -> 2024-05-17 00:00:00
month_start -> 2024-05-01 00:00:00
year_start  -> 2024-01-01 00:00:00
```

### Daily Transaction Totals

```sql
SELECT DATE_TRUNC('day', transaction_time) AS transaction_day,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY DATE_TRUNC('day', transaction_time)
ORDER BY transaction_day;
```

### Why And How The SQL Works

`DATE_TRUNC('day', transaction_time)` changes every timestamp on the same calendar day to midnight for that day. PostgreSQL groups by that value, then counts and sums successful transactions per day.

### Monthly Transaction Totals

```sql
SELECT DATE_TRUNC('month', transaction_time) AS transaction_month,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY DATE_TRUNC('month', transaction_time)
ORDER BY transaction_month;
```

For May 2024 transactions, values such as:

```text
2024-05-01 09:00:00
2024-05-03 11:00:00
2024-05-05 13:00:00
```

all become:

```text
2024-05-01 00:00:00
```

### Alternate Approach: CAST To DATE

For daily reports, this is also common:

```sql
SELECT transaction_time::DATE AS transaction_date,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY transaction_time::DATE
ORDER BY transaction_date;
```

Use `DATE_TRUNC` when you may need hour, day, week, month, quarter, or year. Use `::DATE` when you specifically need a date value.

### Performance And Correctness Note

Filtering should usually use a timestamp range rather than applying a function to the column in `WHERE`.

Prefer:

```sql
SELECT COUNT(*) AS may_transaction_count
FROM transactions
WHERE transaction_time >= TIMESTAMP '2024-05-01 00:00:00'
  AND transaction_time <  TIMESTAMP '2024-06-01 00:00:00';
```

Avoid this for large indexed tables:

```sql
SELECT COUNT(*) AS may_transaction_count
FROM transactions
WHERE DATE_TRUNC('month', transaction_time) = TIMESTAMP '2024-05-01 00:00:00';
```

The second query applies a function to every row's timestamp and may prevent normal index range usage.

Helpful index:

```sql
CREATE INDEX idx_transactions_time_status
ON transactions (transaction_time, status);
```

### Interview Follow-Ups

- What does `DATE_TRUNC('month', transaction_time)` return?
- Why are half-open timestamp ranges useful?
- How would you group by month but filter only the last 12 months?
- How do time zones affect daily transaction totals?

## This Month's Transaction Volume

### Business Question

Count successful transactions from the current calendar month.

### Query

```sql
SELECT COUNT(*) AS transaction_count
FROM transactions
WHERE status = 'SUCCESS'
  AND transaction_time >= DATE_TRUNC('month', CURRENT_DATE)
  AND transaction_time <  DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month';
```

### Why And How The SQL Works

`DATE_TRUNC('month', CURRENT_DATE)` returns the first day of the current month at midnight. Adding `INTERVAL '1 month'` gives the first day of the next month. The query uses a half-open range:

```text
start inclusive, end exclusive
```

That includes all timestamps in the current month without accidentally including midnight of the next month.

### Concrete Example

If `CURRENT_DATE` is `2026-06-04`, then:

```sql
DATE_TRUNC('month', CURRENT_DATE)
```

returns:

```text
2026-06-01 00:00:00
```

and:

```sql
DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'
```

returns:

```text
2026-07-01 00:00:00
```

The filter becomes:

```sql
transaction_time >= TIMESTAMP '2026-06-01 00:00:00'
AND transaction_time <  TIMESTAMP '2026-07-01 00:00:00'
```

### PostgreSQL Note

`CURRENT_DATE` is a date. `CURRENT_TIMESTAMP` includes date, time, and time zone. For month boundaries, `CURRENT_DATE` is often enough, but be careful if application and database sessions use different time zones.

### Interview Follow-Ups

- Why use `< next_month` instead of `<= end_of_month`?
- What happens if the database timezone differs from business timezone?
- How would you calculate previous month's volume?

Previous month example:

```sql
SELECT COUNT(*) AS previous_month_transaction_count
FROM transactions
WHERE status = 'SUCCESS'
  AND transaction_time >= DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '1 month'
  AND transaction_time <  DATE_TRUNC('month', CURRENT_DATE);
```

## Top Customers By Transaction Volume

### Business Question

Find customers with the highest successful transaction volume.

### Query

```sql
SELECT customer_id,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
WHERE status = 'SUCCESS'
GROUP BY customer_id
ORDER BY total_amount DESC, customer_id
LIMIT 5;
```

### Why And How The SQL Works

The query filters successful transactions, groups them by customer, calculates count and total amount, sorts customers by total amount descending, and returns the top five.

The `customer_id` tie-breaker makes the result deterministic when two customers have the same total amount.

### Alternate Approach: Rank Instead Of Limit

If the business wants all customers tied in the top five ranks, use a window function:

```sql
WITH customer_totals AS (
    SELECT customer_id,
           SUM(amount) AS total_amount
    FROM transactions
    WHERE status = 'SUCCESS'
    GROUP BY customer_id
),
ranked_customers AS (
    SELECT customer_id,
           total_amount,
           DENSE_RANK() OVER (ORDER BY total_amount DESC) AS amount_rank
    FROM customer_totals
)
SELECT customer_id, total_amount, amount_rank
FROM ranked_customers
WHERE amount_rank <= 5
ORDER BY amount_rank, customer_id;
```

### Performance Note

This query may scan and aggregate many rows. For frequent reporting over large tables, consider:

- an index on `(status, customer_id)`,
- date-range filters,
- partitioning by transaction date,
- daily/monthly summary tables,
- materialized views for dashboard workloads.

### Interview Follow-Ups

- Should "top 5" include ties?
- Should the ranking be by count or amount?
- How would you optimize this for hundreds of millions of transactions?

## Customers With Suspicious High-Value Activity

### Business Question

Find customers who made at least three successful transactions of `10000` or more in the last 24 hours.

### Query

```sql
SELECT customer_id,
       COUNT(*) AS high_value_transaction_count,
       SUM(amount) AS high_value_total_amount
FROM transactions
WHERE status = 'SUCCESS'
  AND amount >= 10000
  AND transaction_time >= CURRENT_TIMESTAMP - INTERVAL '24 hours'
GROUP BY customer_id
HAVING COUNT(*) >= 3;
```

### Why And How The SQL Works

`WHERE` keeps only successful high-value transactions from the last 24 hours. `GROUP BY customer_id` creates one group per customer. `HAVING COUNT(*) >= 3` keeps only customers with at least three qualifying transactions.

### Correctness Note

This query uses "last 24 hours from now", not "any rolling 24-hour window". If the interviewer asks for a rolling window around each transaction, this query is not enough.

### Alternate Approach

A self-join can identify rolling clusters:

```sql
SELECT t1.customer_id,
       t1.transaction_id,
       COUNT(t2.transaction_id) AS nearby_high_value_count
FROM transactions t1
JOIN transactions t2
    ON t1.customer_id = t2.customer_id
   AND t2.transaction_time BETWEEN t1.transaction_time - INTERVAL '24 hours'
                               AND t1.transaction_time
   AND t2.status = 'SUCCESS'
   AND t2.amount >= 10000
WHERE t1.status = 'SUCCESS'
  AND t1.amount >= 10000
GROUP BY t1.customer_id, t1.transaction_id
HAVING COUNT(t2.transaction_id) >= 3;
```

### Performance Note

The first query benefits from a date/status/amount lookup:

```sql
CREATE INDEX idx_transactions_recent_high_value
ON transactions (transaction_time, customer_id)
WHERE status = 'SUCCESS'
  AND amount >= 10000;
```

The rolling-window self-join is more expensive because it compares transactions to nearby transactions. On large data, use careful indexes, partitioning, or dedicated fraud/event-stream processing.

### Interview Follow-Ups

- Is the requirement last 24 hours or any rolling 24-hour period?
- Should failed, reversed, or pending transactions count?
- How would you reduce false positives for business customers?

## Showing Groups With Zero Transactions

### Business Question

Show daily successful transaction totals for every day in May 2024, including days with zero transactions.

### Query

```sql
SELECT d.transaction_date,
       COUNT(t.transaction_id) AS transaction_count,
       COALESCE(SUM(t.amount), 0) AS total_amount
FROM GENERATE_SERIES(
         DATE '2024-05-01',
         DATE '2024-05-31',
         INTERVAL '1 day'
     ) AS d(transaction_date)
LEFT JOIN transactions t
    ON t.transaction_time >= d.transaction_date
   AND t.transaction_time <  d.transaction_date + INTERVAL '1 day'
   AND t.status = 'SUCCESS'
GROUP BY d.transaction_date
ORDER BY d.transaction_date;
```

### Why And How The SQL Works

`GENERATE_SERIES` creates one row per date. The `LEFT JOIN` attaches matching successful transactions for each day. Days without transactions still remain because the date series is the left side of the join.

`COUNT(t.transaction_id)` returns zero for no matched transactions. `SUM(t.amount)` would return `NULL` for no matches, so `COALESCE` converts it to zero.

### PostgreSQL-Specific Note

`GENERATE_SERIES` is a very useful PostgreSQL function for reports that need complete date ranges.

### Correctness Note

Keep right-table filters such as `t.status = 'SUCCESS'` inside the `JOIN` condition. If you put them in `WHERE`, the `LEFT JOIN` can behave like an inner join and remove zero-transaction days.

### Interview Follow-Ups

- How do you show dates with zero activity?
- Why use `COUNT(t.transaction_id)` instead of `COUNT(*)` after a left join?
- Why should transaction filters stay in the join condition?

## Aggregation Interview Checklist

When answering aggregation questions, explain:

- What rows are filtered before grouping.
- What column defines each group.
- What each aggregate function calculates.
- Whether nulls affect the result.
- Whether failed, pending, or reversed transactions should be included.
- Whether timestamp boundaries are inclusive or exclusive.
- Whether the query needs an index, partitioning, or precomputed summary at scale.

