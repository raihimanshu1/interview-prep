# PostgreSQL Window Functions

Window functions calculate values across related rows while keeping individual rows visible.

This is different from `GROUP BY`. `GROUP BY` collapses rows into one result per group. A window function adds a calculated value to each row.

Window functions are heavily used in banking interviews for:

- Latest transaction per account
- Top N transactions per customer
- Running balances
- Moving averages
- Consecutive failures
- Month-over-month or day-over-day comparisons

## Sample Table

```sql
CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    transaction_type VARCHAR(10) NOT NULL CHECK (transaction_type IN ('CREDIT', 'DEBIT')),
    status VARCHAR(20) NOT NULL,
    transaction_time TIMESTAMP NOT NULL
);
```

Useful index for many examples:

```sql
CREATE INDEX idx_transactions_account_time
ON transactions(account_id, transaction_time, transaction_id);
```

## Window Function Syntax

Basic pattern:

```sql
function_name() OVER (
    PARTITION BY group_column
    ORDER BY sort_column
)
```

Meaning:

- `PARTITION BY` divides rows into groups without collapsing them.
- `ORDER BY` defines row order inside each partition.
- A frame clause, such as `ROWS BETWEEN 2 PRECEDING AND CURRENT ROW`, controls which rows are visible to aggregate window functions.

Example:

```sql
SELECT account_id,
       transaction_id,
       amount,
       SUM(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
       ) AS running_amount
FROM transactions;
```

## ROW_NUMBER

Concept: `ROW_NUMBER()` gives a unique sequence number within each partition.

```sql
SELECT transaction_id,
       account_id,
       transaction_time,
       ROW_NUMBER() OVER (
           PARTITION BY account_id
           ORDER BY transaction_time DESC, transaction_id DESC
       ) AS row_num
FROM transactions;
```

Why it matters: Use `ROW_NUMBER()` when you need exactly one row or exactly N rows per group.

Use case: Pick one latest transaction per account.

```sql
SELECT account_id,
       transaction_id,
       transaction_time,
       amount
FROM (
    SELECT t.*,
           ROW_NUMBER() OVER (
               PARTITION BY account_id
               ORDER BY transaction_time DESC, transaction_id DESC
           ) AS row_num
    FROM transactions t
) x
WHERE row_num = 1;
```

Correctness detail: `transaction_id DESC` is a tie-breaker. If two transactions have the same timestamp, the query still returns a deterministic row.

Alternate PostgreSQL approach using `DISTINCT ON`:

```sql
SELECT DISTINCT ON (account_id)
       account_id,
       transaction_id,
       transaction_time,
       amount
FROM transactions
ORDER BY account_id, transaction_time DESC, transaction_id DESC;
```

Performance and correctness:

- `ROW_NUMBER()` is standard SQL and generalizes well.
- `DISTINCT ON` is PostgreSQL-specific and often concise for one latest row per group.
- Index `(account_id, transaction_time DESC, transaction_id DESC)` can help both patterns.

Interviewer follow-ups:

- Latest successful transaction per account.
- Latest transaction per account per day.
- Why is a tie-breaker needed?

## RANK

Concept: `RANK()` gives the same rank to tied values and leaves gaps.

```sql
SELECT customer_id,
       transaction_id,
       amount,
       RANK() OVER (ORDER BY amount DESC) AS amount_rank
FROM transactions;
```

Example:

- Amounts: 500, 500, 300
- Ranks: 1, 1, 3

When to use: Use `RANK()` when the gap has meaning, like competition ranking.

Top 3 transaction amounts including ties:

```sql
SELECT transaction_id,
       customer_id,
       amount
FROM (
    SELECT t.*,
           RANK() OVER (ORDER BY amount DESC) AS amount_rank
    FROM transactions t
) ranked
WHERE amount_rank <= 3;
```

Correctness detail: This can return more than three rows if ties exist.

Interviewer follow-ups:

- Difference between top 3 rows and top 3 ranks.
- What happens when two rows tie for rank 2?
- Why might a result skip rank 2 or rank 3?

## DENSE_RANK

Concept: `DENSE_RANK()` gives the same rank to tied values without gaps.

```sql
SELECT customer_id,
       transaction_id,
       amount,
       DENSE_RANK() OVER (ORDER BY amount DESC) AS amount_rank
FROM transactions;
```

Example:

- Amounts: 500, 500, 300
- Dense ranks: 1, 1, 2

When to use: Use `DENSE_RANK()` when the question asks for Nth distinct value.

Second highest distinct transaction amount:

```sql
SELECT transaction_id,
       customer_id,
       amount
FROM (
    SELECT t.*,
           DENSE_RANK() OVER (ORDER BY amount DESC) AS amount_rank
    FROM transactions t
) ranked
WHERE amount_rank = 2;
```

Department or account-wise ranking:

```sql
SELECT account_id,
       transaction_id,
       amount
FROM (
    SELECT t.*,
           DENSE_RANK() OVER (
               PARTITION BY account_id
               ORDER BY amount DESC
           ) AS amount_rank
    FROM transactions t
) ranked
WHERE amount_rank = 2;
```

Performance and correctness:

- `DENSE_RANK()` is tie-aware and returns all rows sharing the Nth distinct value.
- It may return many rows for a popular amount.
- If the interviewer wants exactly one row, use `ROW_NUMBER()` instead.

Interviewer follow-ups:

- Difference between `RANK()` and `DENSE_RANK()`.
- Return all transactions tied for second highest amount.
- Return exactly one second-highest transaction per account.

## Ranking Function Comparison

```sql
SELECT transaction_id,
       amount,
       ROW_NUMBER() OVER (ORDER BY amount DESC, transaction_id) AS row_num,
       RANK() OVER (ORDER BY amount DESC) AS rank_num,
       DENSE_RANK() OVER (ORDER BY amount DESC) AS dense_rank_num
FROM transactions;
```

Interview answer:

- `ROW_NUMBER()` makes every row unique.
- `RANK()` keeps ties but leaves gaps.
- `DENSE_RANK()` keeps ties and does not leave gaps.

Choosing correctly:

- Deduplication: `ROW_NUMBER()`
- Latest row per group: `ROW_NUMBER()` or PostgreSQL `DISTINCT ON`
- Top N rows exactly: `ROW_NUMBER()`
- Top N values with ties: `RANK()` or `DENSE_RANK()`
- Nth distinct salary or amount: `DENSE_RANK()`

## LEAD

Concept: `LEAD()` reads a future row without self-joining.

```sql
SELECT transaction_id,
       account_id,
       transaction_time,
       LEAD(transaction_time) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
       ) AS next_transaction_time
FROM transactions;
```

Use case: Find time gap until next transaction.

```sql
SELECT account_id,
       transaction_id,
       transaction_time,
       LEAD(transaction_time) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
       ) - transaction_time AS time_until_next_transaction
FROM transactions;
```

Performance and correctness:

- `LEAD()` depends on ordering. Use a stable order.
- The last row in each partition returns `NULL` unless a default value is supplied.

Interviewer follow-ups:

- Find transactions followed by another transaction within 5 minutes.
- Compare current transaction amount with next transaction amount.
- What does `LEAD()` return for the final row?

## LAG

Concept: `LAG()` reads a previous row without self-joining.

```sql
SELECT transaction_id,
       account_id,
       amount,
       LAG(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
       ) AS previous_amount
FROM transactions;
```

Use case: Find transactions larger than the previous transaction for the same account.

```sql
SELECT account_id,
       transaction_id,
       amount,
       previous_amount
FROM (
    SELECT t.*,
           LAG(amount) OVER (
               PARTITION BY account_id
               ORDER BY transaction_time, transaction_id
           ) AS previous_amount
    FROM transactions t
) x
WHERE amount > previous_amount;
```

Date comparison use case: transactions after more than 30 days of inactivity.

```sql
SELECT account_id,
       transaction_id,
       transaction_time,
       previous_transaction_time
FROM (
    SELECT t.*,
           LAG(transaction_time) OVER (
               PARTITION BY account_id
               ORDER BY transaction_time, transaction_id
           ) AS previous_transaction_time
    FROM transactions t
) x
WHERE previous_transaction_time IS NOT NULL
  AND transaction_time > previous_transaction_time + INTERVAL '30 days';
```

Performance and correctness:

- `LAG()` compares with the previous row in sort order, not necessarily the previous calendar day.
- Use `INTERVAL` for timestamp difference logic.
- Indexing the partition and order columns can help PostgreSQL process ordered account histories.

Interviewer follow-ups:

- Find first transaction after a long gap.
- Compare current month with previous month.
- Difference between `LAG()` and a self-join.

## FIRST_VALUE

Concept: `FIRST_VALUE()` returns the first value in the ordered window.

```sql
SELECT transaction_id,
       account_id,
       amount,
       FIRST_VALUE(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
       ) AS first_transaction_amount
FROM transactions;
```

Use case: Compare every transaction with the first transaction for that account.

```sql
SELECT account_id,
       transaction_id,
       amount,
       amount - FIRST_VALUE(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
       ) AS difference_from_first_amount
FROM transactions;
```

Performance and correctness:

- The result depends completely on the `ORDER BY`.
- Include a tie-breaker when timestamps can repeat.

Interviewer follow-ups:

- First transaction per account versus first transaction amount on every row.
- Difference between `FIRST_VALUE()` and `MIN()`.
- Return first posted transaction only.

## LAST_VALUE

Concept: `LAST_VALUE()` returns the last value in the window frame, not always the last row in the partition.

In PostgreSQL, define the window frame carefully:

```sql
SELECT transaction_id,
       account_id,
       amount,
       LAST_VALUE(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
           ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
       ) AS last_transaction_amount
FROM transactions;
```

Why the frame matters: With an `ORDER BY`, PostgreSQL's default frame ends at the current row. Without `UNBOUNDED FOLLOWING`, `LAST_VALUE()` often returns the current row's value, which surprises many candidates.

Alternate approach for latest amount:

```sql
SELECT account_id,
       transaction_id,
       amount,
       FIRST_VALUE(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time DESC, transaction_id DESC
       ) AS latest_transaction_amount
FROM transactions;
```

Performance and correctness:

- For a single latest row per account, `ROW_NUMBER()` or `DISTINCT ON` is usually clearer.
- For showing latest amount on every row, `LAST_VALUE()` with the full frame is appropriate.

Interviewer follow-ups:

- Why does `LAST_VALUE()` sometimes equal current row?
- How do you get latest value per account on every row?
- How do you get only the latest row per account?

## Running Sum

Concept: A running sum accumulates values in order.

```sql
SELECT account_id,
       transaction_id,
       transaction_time,
       amount,
       SUM(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_sum
FROM transactions;
```

Why it matters: Account statements, daily balances, and cumulative payment volume all use this pattern.

Correctness detail: Use `ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW` to make the frame row-based and deterministic when multiple rows share the same timestamp.

Running daily total:

```sql
WITH daily_transaction AS (
    SELECT account_id,
           transaction_time::date AS transaction_date,
           SUM(amount) AS daily_amount
    FROM transactions
    WHERE status = 'POSTED'
    GROUP BY account_id, transaction_time::date
)
SELECT account_id,
       transaction_date,
       daily_amount,
       SUM(daily_amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_date
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_daily_amount
FROM daily_transaction;
```

Performance and correctness:

- Pre-aggregate when the business question is daily or monthly; this reduces rows before the window function.
- Keep the order stable with a unique tie-breaker for row-level running totals.
- Use `NUMERIC` for money-like values.

Interviewer follow-ups:

- Running total per account per month.
- Difference between row-level and daily running totals.
- Why should failed or pending transactions often be filtered out?

## Running Balance

Concept: Convert credits and debits into signed amounts, then calculate a running sum.

```sql
SELECT account_id,
       transaction_id,
       transaction_time,
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
           ORDER BY transaction_time, transaction_id
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_balance
FROM transactions
WHERE status = 'POSTED'
ORDER BY account_id, transaction_time, transaction_id;
```

How it works:

- Credits increase balance.
- Debits decrease balance.
- The running `SUM()` gives the balance after each posted transaction.

Alternate approach with an opening balance:

```sql
SELECT account_id,
       transaction_id,
       transaction_time,
       opening_balance
       + SUM(signed_amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_balance
FROM (
    SELECT t.*,
           1000.00::numeric AS opening_balance,
           CASE
               WHEN transaction_type = 'CREDIT' THEN amount
               WHEN transaction_type = 'DEBIT' THEN -amount
               ELSE 0
           END AS signed_amount
    FROM transactions t
    WHERE status = 'POSTED'
) x;
```

Performance and correctness:

- Do not include failed, reversed, or pending transactions unless the business requirement says so.
- Running balances need a strict ordering rule. Same timestamp rows must still have a deterministic order.
- For audited banking ledgers, stored ledger entries are often append-only, and balances are derived or stored with controls.

Interviewer follow-ups:

- Add opening balance.
- Handle reversals.
- Calculate end-of-day balance instead of transaction-level balance.

## Moving Average

Concept: A moving average calculates an average over nearby rows or a time window.

Three-transaction moving average:

```sql
SELECT account_id,
       transaction_id,
       transaction_time,
       amount,
       AVG(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
           ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
       ) AS three_transaction_moving_average
FROM transactions
WHERE status = 'POSTED';
```

How it works:

- `2 PRECEDING` plus current row gives up to three rows.
- The first two rows in a partition use smaller frames because fewer previous rows exist.

Seven-day moving average by daily amount:

```sql
WITH daily_transaction AS (
    SELECT account_id,
           transaction_time::date AS transaction_date,
           SUM(amount) AS daily_amount
    FROM transactions
    WHERE status = 'POSTED'
    GROUP BY account_id, transaction_time::date
)
SELECT account_id,
       transaction_date,
       daily_amount,
       AVG(daily_amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_date
           ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
       ) AS seven_row_moving_average
FROM daily_transaction;
```

Important distinction:

- `ROWS BETWEEN 6 PRECEDING AND CURRENT ROW` means seven rows, not seven calendar days.
- If missing dates matter, build a calendar table or `generate_series()` first so missing days are represented.

Calendar-based 7-day moving average:

```sql
WITH account_dates AS (
    SELECT a.account_id,
           d.transaction_date
    FROM (
        SELECT DISTINCT account_id
        FROM transactions
    ) a
    CROSS JOIN generate_series(
        CURRENT_DATE - INTERVAL '30 days',
        CURRENT_DATE,
        INTERVAL '1 day'
    ) AS d(transaction_date)
),
daily_transaction AS (
    SELECT account_id,
           transaction_time::date AS transaction_date,
           SUM(amount) AS daily_amount
    FROM transactions
    WHERE status = 'POSTED'
    GROUP BY account_id, transaction_time::date
)
SELECT ad.account_id,
       ad.transaction_date::date AS transaction_date,
       COALESCE(dt.daily_amount, 0) AS daily_amount,
       AVG(COALESCE(dt.daily_amount, 0)) OVER (
           PARTITION BY ad.account_id
           ORDER BY ad.transaction_date
           ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
       ) AS seven_day_moving_average
FROM account_dates ad
LEFT JOIN daily_transaction dt
    ON ad.account_id = dt.account_id
   AND ad.transaction_date::date = dt.transaction_date;
```

Performance and correctness:

- Moving averages can be row-based or calendar-based. Clarify the requirement before writing SQL.
- Calendar expansion can create many rows; limit the date range.
- Pre-aggregation usually improves performance and matches reporting requirements.

Interviewer follow-ups:

- Last 3 transactions versus last 3 days.
- How to handle days with no transactions.
- Moving average by count, amount, or balance.

## Top N Per Group

Concept: Rank rows inside each group, then filter the rank.

Exactly top 3 transactions per account:

```sql
SELECT account_id,
       transaction_id,
       amount
FROM (
    SELECT t.*,
           ROW_NUMBER() OVER (
               PARTITION BY account_id
               ORDER BY amount DESC, transaction_time DESC, transaction_id DESC
           ) AS row_num
    FROM transactions t
    WHERE status = 'POSTED'
) ranked
WHERE row_num <= 3;
```

Top 3 transaction amounts per account including ties:

```sql
SELECT account_id,
       transaction_id,
       amount
FROM (
    SELECT t.*,
           DENSE_RANK() OVER (
               PARTITION BY account_id
               ORDER BY amount DESC
           ) AS amount_rank
    FROM transactions t
    WHERE status = 'POSTED'
) ranked
WHERE amount_rank <= 3;
```

Why the choice matters:

- `ROW_NUMBER()` returns at most 3 rows per account.
- `DENSE_RANK()` can return more than 3 rows if multiple transactions tie.
- `RANK()` can skip rank values, so `rank <= 3` may include fewer distinct amount levels than `DENSE_RANK()`.

Performance and correctness:

- Filter to relevant rows before ranking.
- Use deterministic ordering when using `ROW_NUMBER()`.
- Consider an index such as `(account_id, amount DESC, transaction_time DESC)` for frequent top-N account queries.

Interviewer follow-ups:

- Top N per customer instead of account.
- Include ties or not?
- Return the Nth row only.

## Consecutive Patterns With LAG

Concept: Consecutive-row problems compare the current row with previous rows in a defined order.

Example: three failed transactions in a row per account.

```sql
SELECT DISTINCT account_id
FROM (
    SELECT account_id,
           status,
           LAG(status, 1) OVER (
               PARTITION BY account_id
               ORDER BY transaction_time, transaction_id
           ) AS previous_status,
           LAG(status, 2) OVER (
               PARTITION BY account_id
               ORDER BY transaction_time, transaction_id
           ) AS status_two_back
    FROM transactions
) x
WHERE status = 'FAILED'
  AND previous_status = 'FAILED'
  AND status_two_back = 'FAILED';
```

Why it works:

- The current row is failed.
- The previous row is failed.
- The row before that is failed.
- Together, those three rows form a consecutive failed streak.

Interviewer follow-ups:

- Find four or more consecutive failures.
- Return the start and end of the failure streak.
- Reset the streak after a successful transaction.

## Consecutive Patterns With Islands

Concept: Island grouping finds streaks by creating a stable key for consecutive rows with the same value.

```sql
WITH marked AS (
    SELECT t.*,
           ROW_NUMBER() OVER (
               PARTITION BY account_id
               ORDER BY transaction_time, transaction_id
           ) AS all_row_num,
           ROW_NUMBER() OVER (
               PARTITION BY account_id, status
               ORDER BY transaction_time, transaction_id
           ) AS status_row_num
    FROM transactions t
),
grouped AS (
    SELECT *,
           all_row_num - status_row_num AS streak_key
    FROM marked
)
SELECT account_id,
       status,
       MIN(transaction_time) AS streak_started_at,
       MAX(transaction_time) AS streak_ended_at,
       COUNT(*) AS streak_length
FROM grouped
GROUP BY account_id, status, streak_key
HAVING status = 'FAILED'
   AND COUNT(*) >= 3;
```

How it works:

- `all_row_num` counts every transaction for the account.
- `status_row_num` counts only rows within the same account and status.
- The difference stays constant during a consecutive streak of the same status.

Performance and correctness:

- Island queries are powerful for variable-length streaks.
- They require a stable event order.
- If the streak condition includes time gaps, add that rule separately. Consecutive rows and events within a time interval are not the same requirement.

Interviewer follow-ups:

- Find the longest failure streak per account.
- Find streaks where each event is within 10 minutes of the previous event.
- Find consecutive debit transactions that exceed a fraud threshold.

## Date And Interval Logic

Concept: PostgreSQL uses `DATE`, `TIMESTAMP`, and `INTERVAL` to express time-based filters.

Rolling last 30 days:

```sql
SELECT transaction_id,
       account_id,
       amount,
       transaction_time
FROM transactions
WHERE transaction_time >= CURRENT_TIMESTAMP - INTERVAL '30 days';
```

Current calendar month:

```sql
SELECT transaction_id,
       account_id,
       amount,
       transaction_time
FROM transactions
WHERE transaction_time >= date_trunc('month', CURRENT_DATE)
  AND transaction_time < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month';
```

Yesterday only:

```sql
SELECT transaction_id,
       account_id,
       amount,
       transaction_time
FROM transactions
WHERE transaction_time >= CURRENT_DATE - INTERVAL '1 day'
  AND transaction_time < CURRENT_DATE;
```

Compare with previous transaction after a 7-day gap:

```sql
SELECT account_id,
       transaction_id,
       transaction_time,
       previous_transaction_time
FROM (
    SELECT t.*,
           LAG(transaction_time) OVER (
               PARTITION BY account_id
               ORDER BY transaction_time, transaction_id
           ) AS previous_transaction_time
    FROM transactions t
) x
WHERE previous_transaction_time IS NOT NULL
  AND transaction_time >= previous_transaction_time + INTERVAL '7 days';
```

Performance and correctness:

- Prefer range predicates on the timestamp column.
- Avoid wrapping the column in functions in the `WHERE` clause when an index should be used.
- Use an exclusive upper bound for time ranges.
- Clarify rolling period versus calendar period.

Interviewer follow-ups:

- Last 30 days versus current month.
- `CURRENT_DATE` versus `CURRENT_TIMESTAMP`.
- How to handle time zones in financial reporting.

## Window Frames: ROWS Versus RANGE

Concept: Aggregate window functions use a frame to decide which rows are included for the current row.

Explicit row-based running total:

```sql
SELECT account_id,
       transaction_id,
       transaction_time,
       amount,
       SUM(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_time, transaction_id
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_sum
FROM transactions;
```

Why `ROWS` is often preferred:

- `ROWS` counts physical rows in the ordered result.
- It makes running totals advance one row at a time.
- It avoids surprises when multiple rows have the same sort value.

`RANGE` is value-based. Rows with the same `ORDER BY` value can be treated as peers. That may be correct for some reports, but it can surprise candidates in transaction-level running balance questions.

Interviewer follow-ups:

- Why did two rows with the same timestamp get the same running total?
- What is the default frame in PostgreSQL when `ORDER BY` is present?
- When would a value-based frame be useful?

## Aggregation Versus Window Function

Grouped total per account:

```sql
SELECT account_id,
       SUM(amount) AS total_amount
FROM transactions
WHERE status = 'POSTED'
GROUP BY account_id;
```

Total per account shown on every row:

```sql
SELECT account_id,
       transaction_id,
       amount,
       SUM(amount) OVER (
           PARTITION BY account_id
       ) AS account_total_amount
FROM transactions
WHERE status = 'POSTED';
```

Interview answer:

- Use `GROUP BY` when you want one row per group.
- Use a window function when you want row detail plus group-level calculation.

Interviewer follow-ups:

- Why does the grouped query return fewer rows?
- Can window functions be used in `WHERE` directly?
- Why do we often use a subquery or CTE before filtering ranked rows?

## Filtering Window Results

PostgreSQL does not allow window functions directly in `WHERE` because `WHERE` is evaluated before the `SELECT` list.

Correct pattern:

```sql
SELECT account_id,
       transaction_id,
       amount
FROM (
    SELECT t.*,
           ROW_NUMBER() OVER (
               PARTITION BY account_id
               ORDER BY transaction_time DESC, transaction_id DESC
           ) AS row_num
    FROM transactions t
) ranked
WHERE row_num = 1;
```

You can also use a CTE:

```sql
WITH ranked AS (
    SELECT t.*,
           ROW_NUMBER() OVER (
               PARTITION BY account_id
               ORDER BY transaction_time DESC, transaction_id DESC
           ) AS row_num
    FROM transactions t
)
SELECT account_id,
       transaction_id,
       amount
FROM ranked
WHERE row_num = 1;
```

Performance and correctness:

- Subquery and CTE are both common for interview answers.
- In modern PostgreSQL versions, simple CTEs can often be inlined by the planner.
- Filter base rows before the window calculation when possible, such as `WHERE status = 'POSTED'`.

## Performance Notes

Window functions often require PostgreSQL to sort rows by partition and order columns.

Helpful practices:

- Filter unnecessary rows before the window calculation.
- Select only needed columns in large production queries.
- Use indexes that match common `PARTITION BY` and `ORDER BY` patterns.
- Pre-aggregate daily or monthly data before running reporting windows.
- Check with `EXPLAIN` or `EXPLAIN ANALYZE` instead of guessing.

Example index for latest transaction per account:

```sql
CREATE INDEX idx_transactions_account_latest
ON transactions(account_id, transaction_time DESC, transaction_id DESC);
```

Example partial index for posted transactions:

```sql
CREATE INDEX idx_transactions_posted_account_time
ON transactions(account_id, transaction_time, transaction_id)
WHERE status = 'POSTED';
```

Correctness practices:

- Always define tie handling.
- Use deterministic ordering.
- Use `ROWS` frames for row-by-row running calculations.
- Clarify whether the output should include ties.
- Clarify whether time windows are rolling or calendar-based.

## Interview Tip

Remember this pattern:

```sql
function_name() OVER (
    PARTITION BY group_column
    ORDER BY sort_column
    ROWS BETWEEN frame_start AND frame_end
)
```

Strong answers explain:

- What is the partition?
- What is the order?
- What happens with ties?
- Does the function keep rows or collapse rows?
- Does the frame mean rows, dates, or values?
