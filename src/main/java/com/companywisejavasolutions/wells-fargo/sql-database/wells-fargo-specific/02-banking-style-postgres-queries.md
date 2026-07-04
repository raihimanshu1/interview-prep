# Wells Fargo Banking-Style PostgreSQL Queries

Banking SQL interviews often use transaction, account, customer, login, and merchant-style tables. The interviewer is usually testing more than syntax: they want to see whether you can translate a banking business rule into a correct query, explain the PostgreSQL features involved, and discuss performance, edge cases, and production safety.

All examples below use PostgreSQL syntax only.

## Sample Tables

```sql
CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL
);

CREATE TABLE accounts (
    account_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(customer_id),
    account_type VARCHAR(20) NOT NULL,
    balance NUMERIC(12, 2) NOT NULL
);

CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(account_id),
    customer_id BIGINT NOT NULL REFERENCES customers(customer_id),
    merchant_id BIGINT,
    transaction_type VARCHAR(10) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_timestamp TIMESTAMP NOT NULL
);

CREATE TABLE logins (
    login_id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    login_timestamp TIMESTAMP NOT NULL
);
```

## 1. Find Suspicious Transactions

### Business Meaning

A bank may flag a customer when multiple high-value transactions occur within a short time window. In real systems, this does not prove fraud. It creates an alert for review or for an automated risk model.

### Concept Involved

This query uses filtering, grouping, aggregate functions, and `HAVING`.

- `WHERE` filters individual transactions before grouping.
- `GROUP BY` creates one group per customer.
- `COUNT(*)` counts qualifying transactions.
- `SUM(amount)` calculates total exposure.
- `HAVING` filters aggregated customer groups.

### Query

```sql
SELECT customer_id,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
WHERE transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '1 day'
  AND amount >= 10000
GROUP BY customer_id
HAVING COUNT(*) >= 3;
```

### Why And How The SQL Works

The `WHERE` clause keeps only transactions from the last day with an amount of at least `10000`. After that, PostgreSQL groups the remaining rows by `customer_id`. The `HAVING COUNT(*) >= 3` condition keeps only customers who had at least three such transactions.

Use `HAVING` here because the condition depends on an aggregate value. A common beginner mistake is trying to put `COUNT(*) >= 3` in the `WHERE` clause, but `WHERE` runs before aggregation.

### Alternate Approach

If the interviewer asks for a rolling 24-hour cluster around each transaction instead of the last 24 hours from now, use a self-join or a windowed range check. A self-join is easier to explain:

```sql
SELECT t1.customer_id,
       t1.transaction_id,
       COUNT(t2.transaction_id) AS nearby_high_value_count
FROM transactions t1
JOIN transactions t2
    ON t1.customer_id = t2.customer_id
   AND t2.transaction_timestamp BETWEEN t1.transaction_timestamp - INTERVAL '24 hours'
                                    AND t1.transaction_timestamp
   AND t2.amount >= 10000
WHERE t1.amount >= 10000
GROUP BY t1.customer_id, t1.transaction_id
HAVING COUNT(t2.transaction_id) >= 3;
```

### Performance And Indexing Note

For the main query, an index on timestamp helps PostgreSQL narrow recent rows:

```sql
CREATE INDEX idx_transactions_timestamp_amount_customer
ON transactions (transaction_timestamp, amount, customer_id);
```

If most queries look for high-value transactions, a partial index can be smaller:

```sql
CREATE INDEX idx_transactions_high_value_recent_lookup
ON transactions (transaction_timestamp, customer_id)
WHERE amount >= 10000;
```

The partial index is useful only when the query condition matches `amount >= 10000`.

### Likely Interviewer Follow-Ups

- How would you change the query for a rolling 10-minute window?
- Should failed or reversed transactions be included?
- How would you avoid false positives for business customers?
- What indexes would you add if the table has hundreds of millions of rows?
- How would you store the generated fraud alert?

## 2. Daily Transaction Totals

### Business Meaning

Daily totals help operations teams reconcile payment volume, monitor transaction spikes, and produce dashboards for deposits, withdrawals, card activity, or wire movement.

### Concept Involved

This query uses date extraction, grouping, aggregates, and ordering.

### Query

```sql
SELECT CAST(transaction_timestamp AS DATE) AS transaction_date,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
GROUP BY CAST(transaction_timestamp AS DATE)
ORDER BY transaction_date;
```

### Why And How The SQL Works

`CAST(transaction_timestamp AS DATE)` removes the time portion and converts every timestamp on the same calendar day into the same date value. PostgreSQL then groups by that date, counts rows, and sums amounts.

The selected expression must also appear in the `GROUP BY` clause because PostgreSQL needs to know how non-aggregated output columns are grouped.

### Alternate Approach

`DATE_TRUNC` is often preferred when you may later change the granularity to hour, week, or month:

```sql
SELECT DATE_TRUNC('day', transaction_timestamp) AS transaction_day,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
GROUP BY DATE_TRUNC('day', transaction_timestamp)
ORDER BY transaction_day;
```

If you need separate debit and credit totals:

```sql
SELECT CAST(transaction_timestamp AS DATE) AS transaction_date,
       SUM(amount) FILTER (WHERE transaction_type = 'DEBIT') AS debit_total,
       SUM(amount) FILTER (WHERE transaction_type = 'CREDIT') AS credit_total
FROM transactions
GROUP BY CAST(transaction_timestamp AS DATE)
ORDER BY transaction_date;
```

### Performance And Indexing Note

Applying a function to a timestamp in `GROUP BY` is common, but it may require scanning many rows. For frequent reporting, consider an expression index:

```sql
CREATE INDEX idx_transactions_transaction_date
ON transactions ((CAST(transaction_timestamp AS DATE)));
```

For large banking tables, daily aggregates are often precomputed into a reporting table or materialized view.

### Likely Interviewer Follow-Ups

- How would you show dates with zero transactions?
- How would time zones affect daily totals?
- How would you separate successful, failed, and reversed transactions?
- How would you optimize this for a dashboard?

## 3. Running Account Balance

### Business Meaning

A running balance shows the account balance after each transaction. Customers see this in statements, and back-office teams use it to audit account history.

### Concept Involved

This query uses a window function. Window functions calculate values across related rows while still returning each row.

Key concepts:

- `CASE` converts credits and debits into signed amounts.
- `SUM(...) OVER (...)` computes a cumulative total.
- `PARTITION BY account_id` restarts the running total for each account.
- `ORDER BY transaction_timestamp, transaction_id` defines transaction sequence.

### Query

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
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_balance
FROM transactions
ORDER BY account_id, transaction_timestamp, transaction_id;
```

### Why And How The SQL Works

The `CASE` expression turns each transaction into a signed movement:

- Credit of `500.00` becomes `+500.00`.
- Debit of `200.00` becomes `-200.00`.
- Unknown types become `0`, though production code should usually validate transaction types instead.

The windowed `SUM` then accumulates those signed amounts in transaction order. The window frame:

```sql
ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
```

means "start at the first transaction for this account and sum through the current row." Adding this frame explicitly is a good interview habit because it avoids confusion between `ROWS` and the default `RANGE` behavior.

The tie-breaker `transaction_id` matters. Two transactions can have the same timestamp, so ordering only by timestamp may produce unstable results.

### Basics-To-Advanced Explanation

At the basic level, a running balance is just a cumulative sum. If an account starts at zero and receives a credit, the balance increases. If it has a debit, the balance decreases.

At the intermediate level, you must calculate the cumulative sum separately for each account. That is why `PARTITION BY account_id` is required.

At the advanced level, you must define deterministic transaction order. Banking systems may receive multiple events at the same timestamp, backdated adjustments, or reversals. A production-grade running balance often uses a ledger sequence number, posting timestamp, or effective date plus transaction id.

If the `accounts.balance` column stores the current balance, this query calculates a historical movement total, not necessarily the official statement balance unless you include the opening balance.

### Alternate Approach: Include Opening Balance

If you have a known opening balance of `1000.00`, add it to the cumulative movement:

```sql
SELECT account_id,
       transaction_id,
       transaction_timestamp,
       transaction_type,
       amount,
       1000.00
       + SUM(
             CASE
                 WHEN transaction_type = 'CREDIT' THEN amount
                 WHEN transaction_type = 'DEBIT' THEN -amount
                 ELSE 0
             END
         ) OVER (
             PARTITION BY account_id
             ORDER BY transaction_timestamp, transaction_id
             ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
         ) AS running_balance
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp, transaction_id;
```

In real banking systems, the opening balance usually comes from a statement period table or a ledger snapshot.

### Performance And Indexing Note

Window functions need ordered data per account. This index helps PostgreSQL read rows in the same order the window needs:

```sql
CREATE INDEX idx_transactions_account_time_id
ON transactions (account_id, transaction_timestamp, transaction_id);
```

For very large accounts, calculating the full historical running balance every time is expensive. Banks often store daily balance snapshots, then calculate the running balance only from the snapshot date forward.

### Likely Interviewer Follow-Ups

- How do you include opening balance?
- Why do you order by `transaction_id` in addition to timestamp?
- What happens if a transaction is reversed?
- How would you calculate end-of-day balance?
- How would you make this fast for 10 years of account history?

## 4. Detect Duplicate Payments

### Business Meaning

Duplicate payments can happen because of retries, network timeouts, user double-clicks, batch replay errors, or upstream processor issues. A duplicate detection query helps identify payments that may need reversal or manual review.

### Concept Involved

This query uses grouping and `HAVING` to find repeated combinations of customer, merchant, amount, and date.

### Query

```sql
SELECT customer_id,
       merchant_id,
       amount,
       CAST(transaction_timestamp AS DATE) AS transaction_date,
       COUNT(*) AS duplicate_count
FROM transactions
WHERE transaction_type = 'DEBIT'
GROUP BY customer_id, merchant_id, amount, CAST(transaction_timestamp AS DATE)
HAVING COUNT(*) > 1;
```

### Why And How The SQL Works

The query focuses on debit transactions because duplicate payment problems usually involve money leaving the account. It groups transactions by the attributes that define a possible duplicate:

- Same customer
- Same merchant
- Same amount
- Same transaction date

If a group has more than one row, it is flagged as a potential duplicate.

This is intentionally a candidate-detection query, not a final proof. Two identical transactions on the same day can be valid, such as two separate purchases for the same amount at the same merchant.

### Basics-To-Advanced Explanation

At the basic level, duplicates are rows that look the same according to chosen business columns.

At the intermediate level, you choose a matching key. For payments, `(customer_id, merchant_id, amount, date)` is a reasonable interview answer, but production systems may use payment reference number, authorization code, processor trace id, or idempotency key.

At the advanced level, the time window matters. Same-day matching can miss duplicates that happen across midnight and can falsely group separate purchases made hours apart. A better fraud or operations query often uses a short interval such as 5 or 10 minutes.

### Alternate Approach: Near-Duplicate Payments Within 10 Minutes

```sql
SELECT t1.customer_id,
       t1.merchant_id,
       t1.amount,
       t1.transaction_id AS first_transaction_id,
       t2.transaction_id AS possible_duplicate_transaction_id,
       t1.transaction_timestamp AS first_transaction_timestamp,
       t2.transaction_timestamp AS duplicate_transaction_timestamp
FROM transactions t1
JOIN transactions t2
   ON t1.customer_id = t2.customer_id
   AND t1.merchant_id = t2.merchant_id
   AND t1.amount = t2.amount
   AND t2.transaction_timestamp <= t1.transaction_timestamp + INTERVAL '10 minutes'
   AND t2.transaction_timestamp >= t1.transaction_timestamp
   AND (
       t2.transaction_timestamp > t1.transaction_timestamp
       OR (
           t2.transaction_timestamp = t1.transaction_timestamp
           AND t2.transaction_id > t1.transaction_id
       )
   )
WHERE t1.transaction_type = 'DEBIT'
  AND t2.transaction_type = 'DEBIT';
```

The timestamp comparison finds later transactions inside the time window. The transaction ID is used only as a tie-breaker when two transactions have the same timestamp, which prevents returning the same pair twice and prevents a row from matching itself.

### Performance And Indexing Note

For the grouping version:

```sql
CREATE INDEX idx_transactions_duplicate_payment_group
ON transactions (customer_id, merchant_id, amount, transaction_type, transaction_timestamp);
```

For serious duplicate prevention, do not rely only on after-the-fact detection. Store an idempotency key or external payment reference and enforce it:

```sql
CREATE UNIQUE INDEX idx_transactions_unique_payment_reference
ON transactions (merchant_id, customer_id, amount, transaction_timestamp)
WHERE transaction_type = 'DEBIT' AND status = 'POSTED';
```

That example is still imperfect because timestamp is rarely the right uniqueness key. In production, prefer a true payment reference column.

### Likely Interviewer Follow-Ups

- How do you avoid false positives?
- How would you detect duplicates within five minutes instead of one day?
- How would you prevent duplicate payments at insert time?
- Should failed or pending transactions be included?
- How would you reverse a duplicate safely?

## 5. Largest Transaction Per Customer

### Business Meaning

The bank may want each customer's largest transaction for risk review, rewards analysis, credit underwriting, or customer support investigation.

### Concept Involved

This query uses `ROW_NUMBER()` to rank rows inside each customer group.

### Query

```sql
SELECT customer_id,
       transaction_id,
       amount
FROM (
    SELECT t.*,
           ROW_NUMBER() OVER (
               PARTITION BY customer_id
               ORDER BY amount DESC, transaction_id
           ) AS row_num
    FROM transactions t
) ranked
WHERE row_num = 1;
```

### Why And How The SQL Works

The inner query assigns a row number to each transaction within its customer group. The largest amount receives row number `1`. The outer query keeps only that row.

`transaction_id` is included as a tie-breaker so the result is deterministic if two transactions have the same amount.

### Alternate Approach

PostgreSQL supports `DISTINCT ON`, which is concise and often efficient:

```sql
SELECT DISTINCT ON (customer_id)
       customer_id,
       transaction_id,
       amount
FROM transactions
ORDER BY customer_id, amount DESC, transaction_id;
```

`DISTINCT ON (customer_id)` keeps the first row per customer according to the `ORDER BY`.

### Performance And Indexing Note

This index supports both the window-function and `DISTINCT ON` versions:

```sql
CREATE INDEX idx_transactions_customer_amount_id
ON transactions (customer_id, amount DESC, transaction_id);
```

If the query only considers posted debit transactions, a partial index can be smaller:

```sql
CREATE INDEX idx_transactions_posted_debit_customer_amount
ON transactions (customer_id, amount DESC, transaction_id)
WHERE transaction_type = 'DEBIT' AND status = 'POSTED';
```

### Likely Interviewer Follow-Ups

- How would you return all tied largest transactions?
- What is the difference between `ROW_NUMBER()`, `RANK()`, and `DENSE_RANK()`?
- How would you find the second largest transaction?
- How would you limit this to the current month?

## 6. Customers With No Transactions In Last 90 Days

### Business Meaning

Inactive customers may be targeted for retention campaigns, dormant account review, fee analysis, or fraud monitoring.

### Concept Involved

This query uses an anti-match pattern with `NOT EXISTS`.

### Query

```sql
SELECT c.customer_id,
       c.customer_name
FROM customers c
WHERE NOT EXISTS (
    SELECT 1
    FROM transactions t
    WHERE t.customer_id = c.customer_id
      AND t.transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '90 days'
);
```

### Why And How The SQL Works

For each customer, PostgreSQL checks whether a recent transaction exists. If no matching transaction is found, the customer is returned.

`SELECT 1` is used because the subquery only needs to prove existence. The selected value does not matter.

### Alternate Approach

The same logic can be written using a `LEFT JOIN`:

```sql
SELECT c.customer_id,
       c.customer_name
FROM customers c
LEFT JOIN transactions t
    ON c.customer_id = t.customer_id
   AND t.transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '90 days'
WHERE t.transaction_id IS NULL;
```

The `NOT EXISTS` version is usually clearer and avoids accidental row multiplication.

### Performance And Indexing Note

This index supports the lookup from customer to recent transactions:

```sql
CREATE INDEX idx_transactions_customer_timestamp
ON transactions (customer_id, transaction_timestamp);
```

For very large tables, partitioning transactions by date can also help because PostgreSQL can skip older partitions.

### Likely Interviewer Follow-Ups

- Why use `NOT EXISTS` instead of `NOT IN`?
- How would you exclude closed accounts?
- How would you define inactivity: no login, no transaction, or no posted transaction?
- How would you find customers inactive for exactly 90 to 180 days?

## 7. Monthly Spending Trends

### Business Meaning

Monthly spending trends show how a customer's spending changes over time. This can feed budgeting tools, customer insights, fraud models, or credit risk analysis.

### Concept Involved

This query uses `DATE_TRUNC`, filtering, grouping, and ordering.

### Query

```sql
SELECT customer_id,
       DATE_TRUNC('month', transaction_timestamp) AS spend_month,
       SUM(amount) AS total_spend
FROM transactions
WHERE transaction_type = 'DEBIT'
GROUP BY customer_id, DATE_TRUNC('month', transaction_timestamp)
ORDER BY customer_id, spend_month;
```

### Why And How The SQL Works

`DATE_TRUNC('month', transaction_timestamp)` converts every timestamp in the same month to the first moment of that month. The query then groups by customer and month, summing debit amounts as spend.

Only debit transactions are included because credits are money coming in, not customer spending.

### Alternate Approach

To compare month-over-month change, aggregate first and then use `LAG()`:

```sql
WITH monthly_spend AS (
    SELECT customer_id,
           DATE_TRUNC('month', transaction_timestamp) AS spend_month,
           SUM(amount) AS total_spend
    FROM transactions
    WHERE transaction_type = 'DEBIT'
    GROUP BY customer_id, DATE_TRUNC('month', transaction_timestamp)
)
SELECT customer_id,
       spend_month,
       total_spend,
       total_spend
       - LAG(total_spend) OVER (
             PARTITION BY customer_id
             ORDER BY spend_month
         ) AS spend_change
FROM monthly_spend
ORDER BY customer_id, spend_month;
```

### Performance And Indexing Note

Filtering by transaction type and grouping by timestamp is common in reporting:

```sql
CREATE INDEX idx_transactions_debit_customer_month
ON transactions (customer_id, transaction_timestamp)
WHERE transaction_type = 'DEBIT';
```

For heavy dashboard usage, create a monthly aggregate table instead of scanning raw transactions repeatedly.

### Likely Interviewer Follow-Ups

- How would you include months with zero spending?
- How would you calculate month-over-month percentage change?
- How would you separate ATM, card, ACH, and wire spend?
- How would you handle refunds?

## 8. Top Spending Customers

### Business Meaning

Top spending customers may be reviewed for premium offers, risk monitoring, fraud investigation, or customer segmentation.

### Concept Involved

This query uses a join, filter, grouping, ordering, and `LIMIT`.

### Query

```sql
SELECT c.customer_id,
       c.customer_name,
       SUM(t.amount) AS total_spend
FROM customers c
JOIN transactions t
    ON c.customer_id = t.customer_id
WHERE t.transaction_type = 'DEBIT'
  AND t.transaction_timestamp >= DATE_TRUNC('month', CURRENT_DATE)
GROUP BY c.customer_id, c.customer_name
ORDER BY total_spend DESC
LIMIT 10;
```

### Why And How The SQL Works

The query joins customers to their transactions, filters to current-month debit activity, aggregates spend per customer, sorts from highest to lowest spend, and returns the top 10.

`DATE_TRUNC('month', CURRENT_DATE)` returns the first day of the current month at midnight, which makes it useful for current-month reporting.

### Alternate Approach

If the interviewer asks for the top 10 per account type:

```sql
WITH customer_spend AS (
    SELECT a.account_type,
           c.customer_id,
           c.customer_name,
           SUM(t.amount) AS total_spend
    FROM customers c
    JOIN accounts a
        ON c.customer_id = a.customer_id
    JOIN transactions t
        ON a.account_id = t.account_id
    WHERE t.transaction_type = 'DEBIT'
      AND t.transaction_timestamp >= DATE_TRUNC('month', CURRENT_DATE)
    GROUP BY a.account_type, c.customer_id, c.customer_name
),
ranked AS (
    SELECT customer_spend.*,
           ROW_NUMBER() OVER (
               PARTITION BY account_type
               ORDER BY total_spend DESC, customer_id
           ) AS row_num
    FROM customer_spend
)
SELECT account_type,
       customer_id,
       customer_name,
       total_spend
FROM ranked
WHERE row_num <= 10
ORDER BY account_type, row_num;
```

### Performance And Indexing Note

The transaction table is the likely bottleneck. A partial index for current spending queries can help, though the current-month boundary itself changes over time:

```sql
CREATE INDEX idx_transactions_debit_timestamp_customer
ON transactions (transaction_timestamp, customer_id)
WHERE transaction_type = 'DEBIT';
```

Primary keys on `customers.customer_id` and `accounts.account_id` already support the joins.

### Likely Interviewer Follow-Ups

- How would you include only posted transactions?
- How would refunds change total spend?
- How would you find top spenders per city or account type?
- How would you handle ties for tenth place?

## 9. Consecutive Failed Transactions

### Business Meaning

Repeated failed transactions can indicate insufficient funds, card issues, suspicious payment attempts, or system problems. A bank may alert support or risk teams when failures happen repeatedly.

### Concept Involved

This query uses `LAG()` to compare the current transaction with previous transactions.

### Query

```sql
SELECT customer_id,
       transaction_id,
       transaction_timestamp
FROM (
    SELECT t.*,
           LAG(status, 1) OVER (
               PARTITION BY customer_id
               ORDER BY transaction_timestamp, transaction_id
           ) AS previous_status_1,
           LAG(status, 2) OVER (
               PARTITION BY customer_id
               ORDER BY transaction_timestamp, transaction_id
           ) AS previous_status_2
    FROM transactions t
) x
WHERE status = 'FAILED'
  AND previous_status_1 = 'FAILED'
  AND previous_status_2 = 'FAILED';
```

### Why And How The SQL Works

`LAG(status, 1)` returns the previous transaction status for the same customer. `LAG(status, 2)` returns the status two transactions back. The outer query returns rows where the current and previous two statuses are all `FAILED`.

The output row represents the third failure in a consecutive sequence.

### Alternate Approach

If you need groups of failed streaks, use a gaps-and-islands pattern:

```sql
WITH marked AS (
    SELECT t.*,
           ROW_NUMBER() OVER (
               PARTITION BY customer_id
               ORDER BY transaction_timestamp, transaction_id
           ) AS all_row_num,
           ROW_NUMBER() OVER (
               PARTITION BY customer_id, status
               ORDER BY transaction_timestamp, transaction_id
           ) AS status_row_num
    FROM transactions t
),
streaks AS (
    SELECT customer_id,
           status,
           all_row_num - status_row_num AS streak_group,
           COUNT(*) AS streak_length,
           MIN(transaction_timestamp) AS streak_start,
           MAX(transaction_timestamp) AS streak_end
    FROM marked
    WHERE status = 'FAILED'
    GROUP BY customer_id, status, all_row_num - status_row_num
)
SELECT customer_id,
       streak_length,
       streak_start,
       streak_end
FROM streaks
WHERE streak_length >= 3;
```

### Performance And Indexing Note

The window function benefits from an index that matches partitioning and ordering:

```sql
CREATE INDEX idx_transactions_customer_time_id_status
ON transactions (customer_id, transaction_timestamp, transaction_id, status);
```

For queries focused only on failures, a partial index can help:

```sql
CREATE INDEX idx_transactions_failed_customer_time
ON transactions (customer_id, transaction_timestamp, transaction_id)
WHERE status = 'FAILED';
```

The partial index is not enough for the basic `LAG()` query if successful transactions must be considered because the query needs the full sequence.

### Likely Interviewer Follow-Ups

- Does "consecutive" mean consecutive transactions or failures within a time window?
- How would you find failed streaks of length five or more?
- How would you reset the streak after a successful transaction?
- How would you alert only once per streak?

## 10. Fraud Pattern Detection Query

### Business Meaning

A common fraud pattern is account access trouble followed by a large transaction. For example, multiple failed login attempts followed by a high-value transfer may suggest credential stuffing, account takeover, or suspicious user behavior.

### Concept Involved

This query uses a common table expression, aggregation, join, and time-window filtering.

### Query

```sql
WITH failed_login_counts AS (
    SELECT user_id,
           COUNT(*) AS failed_login_count
    FROM logins
    WHERE status = 'FAILED'
      AND login_timestamp >= CURRENT_TIMESTAMP - INTERVAL '1 hour'
    GROUP BY user_id
)
SELECT t.customer_id,
       t.transaction_id,
       t.amount,
       f.failed_login_count
FROM transactions t
JOIN failed_login_counts f
    ON t.customer_id = f.user_id
WHERE t.amount >= 10000
  AND t.transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '1 hour'
  AND f.failed_login_count >= 3;
```

### Why And How The SQL Works

The CTE first finds users with failed login activity in the last hour. The main query joins those users to high-value transactions in the same recent time window.

This catches customers who have both signals:

- At least three failed logins in the last hour
- A transaction of at least `10000` in the last hour

The query is readable because the login aggregation is separated from the transaction filter.

### Basics-To-Advanced Explanation

At the basic level, fraud pattern detection means combining multiple suspicious signals. One signal alone may be noisy, but multiple signals close together are more meaningful.

At the intermediate level, time windows matter. The failed logins and transaction should be close enough to suggest a related event. The example uses a simple "last hour" window.

At the advanced level, production fraud rules often need sequence. "Failed logins followed by a high-value transaction" means the transaction should happen after the failed logins, not merely in the same hour. You may also include device id, IP address, merchant risk score, account age, customer profile, and historical behavior.

### Alternate Approach: Enforce Event Order

This version requires failed logins to occur before the transaction and within one hour of that transaction:

```sql
SELECT t.customer_id,
       t.transaction_id,
       t.amount,
       COUNT(l.login_id) AS failed_login_count
FROM transactions t
JOIN logins l
    ON l.user_id = t.customer_id
   AND l.status = 'FAILED'
   AND l.login_timestamp >= t.transaction_timestamp - INTERVAL '1 hour'
   AND l.login_timestamp < t.transaction_timestamp
WHERE t.amount >= 10000
GROUP BY t.customer_id, t.transaction_id, t.amount
HAVING COUNT(l.login_id) >= 3;
```

### Performance And Indexing Note

Indexes should match both sides of the rule:

```sql
CREATE INDEX idx_logins_failed_user_time
ON logins (user_id, login_timestamp)
WHERE status = 'FAILED';

CREATE INDEX idx_transactions_customer_time_amount
ON transactions (customer_id, transaction_timestamp, amount);
```

For high-volume fraud detection, real-time systems often stream events into a rules engine, but SQL is still useful for backtesting rules and generating investigation reports.

### Likely Interviewer Follow-Ups

- How do you ensure the login failures happened before the transaction?
- How would you include IP address or device id?
- How would you reduce false positives?
- How would you detect transactions that are unusual for that customer?
- Would this run in OLTP, analytics, or streaming infrastructure?

## 11. Transaction Rollback Scenario

### Business Meaning

A transfer must be atomic: either both sides happen or neither side happens. If account `101` is debited but account `202` is not credited, the bank's ledger is wrong.

### Concept Involved

This example uses transaction control:

- `BEGIN` starts a database transaction.
- `COMMIT` permanently saves all changes.
- `ROLLBACK` undoes all changes since `BEGIN`.
- Row locks protect balances from concurrent updates.

### Basic Query

```sql
BEGIN;

UPDATE accounts
SET balance = balance - 500.00
WHERE account_id = 101;

UPDATE accounts
SET balance = balance + 500.00
WHERE account_id = 202;

-- Commit only after the application/procedure verifies both updates succeeded.
-- Otherwise run ROLLBACK.
-- COMMIT;
```

### Why And How The SQL Works

Both `UPDATE` statements run inside one database transaction. If both statements succeed and the application issues `COMMIT`, PostgreSQL makes both updates visible. If something goes wrong and the application issues `ROLLBACK`, PostgreSQL undoes both updates.

This protects atomicity, but the basic version is not enough for production. It does not check whether the source account has enough funds, whether both accounts exist, or whether concurrent transfers are racing.

### Basics-To-Advanced Explanation

At the basic level, a transaction groups multiple SQL statements into one all-or-nothing unit.

At the intermediate level, transfer logic must validate business rules before committing. A debit should not create a negative balance unless overdraft is allowed.

At the advanced level, you must consider concurrency. Two transfers from the same account can run at the same time. Without careful locking or conditional updates, both may see enough money and overdraw the account.

PostgreSQL row-level locks are acquired by `UPDATE`, and they can also be acquired explicitly using `SELECT ... FOR UPDATE`.

### Safer PostgreSQL Transfer With Balance Check

```sql
BEGIN;

SELECT account_id,
       balance
FROM accounts
WHERE account_id IN (101, 202)
ORDER BY account_id
FOR UPDATE;

WITH debit AS (
    UPDATE accounts
    SET balance = balance - 500.00
    WHERE account_id = 101
      AND balance >= 500.00
    RETURNING account_id
),
credit AS (
    UPDATE accounts
    SET balance = balance + 500.00
    WHERE account_id = 202
      AND EXISTS (
          SELECT 1
          FROM debit
      )
    RETURNING account_id
)
SELECT (SELECT COUNT(*) FROM debit) AS debit_rows,
       (SELECT COUNT(*) FROM credit) AS credit_rows;

-- Commit decision must happen after checking the returned counts:
-- debit_rows = 1 and credit_rows = 1 -> COMMIT
-- otherwise -> ROLLBACK
```

The `FOR UPDATE` locks both account rows until commit or rollback. The `ORDER BY account_id` helps keep lock order consistent, which reduces deadlock risk when many transfers run concurrently.

In application code or procedure logic, check the returned counts before committing. If either count is not `1`, run `ROLLBACK`. That means insufficient funds, a missing account, or another business-rule failure prevented a complete transfer. Do not show or implement an unconditional commit for fund transfer logic.

### Rollback Example

```sql
BEGIN;

UPDATE accounts
SET balance = balance - 500.00
WHERE account_id = 101
  AND balance >= 500.00;

-- Application checks the affected row count.
-- If no row was updated, the application runs:
ROLLBACK;
```

### Ledger-Style Alternative

Many banking systems avoid treating `accounts.balance` as the source of truth. They store immutable ledger entries and derive balance from those entries or from audited snapshots.

```sql
BEGIN;

WITH debit AS (
    UPDATE accounts
    SET balance = balance - 500.00
    WHERE account_id = 101
      AND balance >= 500.00
    RETURNING account_id, customer_id
),
credit AS (
    UPDATE accounts
    SET balance = balance + 500.00
    WHERE account_id = 202
      AND EXISTS (
          SELECT 1
          FROM debit
      )
    RETURNING account_id, customer_id
),
ledger_entries AS (
    INSERT INTO transactions (
        transaction_id,
        account_id,
        customer_id,
        merchant_id,
        transaction_type,
        amount,
        status,
        transaction_timestamp
    )
    SELECT 9001,
           debit.account_id,
           debit.customer_id,
           NULL,
           'DEBIT',
           500.00,
           'POSTED',
           CURRENT_TIMESTAMP
    FROM debit
    JOIN credit
        ON TRUE
    UNION ALL
    SELECT 9002,
           credit.account_id,
           credit.customer_id,
           NULL,
           'CREDIT',
           500.00,
           'POSTED',
           CURRENT_TIMESTAMP
    FROM debit
    JOIN credit
        ON TRUE
    RETURNING transaction_id
)
SELECT (SELECT COUNT(*) FROM debit) AS debit_rows,
       (SELECT COUNT(*) FROM credit) AS credit_rows,
       (SELECT COUNT(*) FROM ledger_entries) AS ledger_rows;

-- Commit decision must happen after checking the returned counts:
-- debit_rows = 1, credit_rows = 1, and ledger_rows = 2 -> COMMIT
-- otherwise -> ROLLBACK
```

The ledger entries create an audit trail. In application code or procedure logic, commit only when `debit_rows = 1`, `credit_rows = 1`, and `ledger_rows = 2`; otherwise rollback. In a mature system, the transaction ids would come from a sequence or identity column, and a transfer id would connect the debit and credit entries. Do not use unconditional commits in transfer examples because they can teach partial money movement.

### Performance And Indexing Note

Primary keys already support direct account updates:

```sql
-- Already created by the primary key:
-- accounts(account_id)
```

For ledger inserts and transfer history, indexes usually target account and time:

```sql
CREATE INDEX idx_transactions_account_posted_time
ON transactions (account_id, transaction_timestamp)
WHERE status = 'POSTED';
```

Keep transactions short. Long-running transactions hold locks longer, increase contention, and can delay cleanup of old row versions in PostgreSQL.

### Likely Interviewer Follow-Ups

- What does `ROLLBACK` do?
- What happens if the debit succeeds but credit fails?
- How do you prevent overdrafts under concurrency?
- Why use `SELECT ... FOR UPDATE`?
- What isolation level would you choose?
- How do you design an auditable ledger?
- How would you handle retry after a timeout?

## Quick Interview Notes

### Running Balance

Use a windowed cumulative sum. Always explain partitioning, ordering, and signed transaction amounts. Mention that production systems need deterministic ordering and usually use ledger entries or snapshots.

### Duplicate Payments

Use grouping for simple duplicate detection and self-join or window logic for near-duplicates. Explain that detection depends on business keys and that prevention should use idempotency keys or unique references.

### Fraud Patterns

Fraud SQL is about combining signals: amount, frequency, failed access attempts, unusual merchant, unusual location, and time proximity. Make sure you distinguish "suspicious" from "fraud proven."

### Rollback And Transaction Safety

Use `BEGIN`, `COMMIT`, and `ROLLBACK` for atomicity. For transfers, also discuss balance checks, affected row counts, row locks, deadlock-safe lock ordering, and audit ledger design.
