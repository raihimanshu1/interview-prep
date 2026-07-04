# PostgreSQL Real Interview Patterns

These are common SQL interview patterns for backend engineers, especially for banking and payment systems where correctness, auditability, and performance matter.

The interviewer usually checks three things:

- Can you translate a business question into joins, grouping, ranking, or date logic?
- Can you explain why the query is correct when there are duplicates, ties, or missing rows?
- Can you reason about performance on large PostgreSQL tables?

## Sample Employee Table

```sql
CREATE TABLE employee (
    employee_id BIGINT PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    department_id BIGINT NOT NULL,
    salary NUMERIC(12, 2) NOT NULL,
    manager_id BIGINT REFERENCES employee(employee_id),
    joined_at DATE NOT NULL
);
```

## Sample Banking Tables

Many Wells Fargo-style SQL questions use employee data for simple logic and transaction data for real business logic.

```sql
CREATE TABLE customer (
    customer_id BIGINT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE account (
    account_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer(customer_id),
    account_type VARCHAR(20) NOT NULL,
    opened_at DATE NOT NULL
);

CREATE TABLE payment (
    payment_id BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(account_id),
    amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    paid_at TIMESTAMP NOT NULL
);

CREATE TABLE transaction_log (
    transaction_id BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(account_id),
    amount NUMERIC(12, 2) NOT NULL,
    transaction_type VARCHAR(10) NOT NULL CHECK (transaction_type IN ('CREDIT', 'DEBIT')),
    status VARCHAR(20) NOT NULL,
    transaction_time TIMESTAMP NOT NULL
);
```

## Second Highest Salary

Concept: Find the second distinct salary value, not necessarily the second row after sorting.

Why it matters: Interviewers use this to test tie handling. If two employees earn the highest salary, the second highest salary should usually mean the next distinct salary.

```sql
SELECT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
    FROM employee
) ranked
WHERE salary_rank = 2;
```

How it works:

- `DENSE_RANK()` assigns the same rank to equal salaries.
- `ORDER BY salary DESC` makes the largest salary rank 1.
- Filtering `salary_rank = 2` returns the second distinct salary.

Alternate approach:

```sql
SELECT MAX(salary) AS second_highest_salary
FROM employee
WHERE salary < (
    SELECT MAX(salary)
    FROM employee
);
```

Performance and correctness:

- `DENSE_RANK()` is clearer when the interviewer later asks for third highest, top N, or department-wise salary.
- The `MAX()` approach is compact for exactly second highest but does not generalize well.
- If salaries can be `NULL`, add `WHERE salary IS NOT NULL`. In this schema, salary is `NOT NULL`.
- An index on `salary DESC` can help ordered salary queries, but window ranking may still sort depending on table size and plan.

Interviewer follow-ups:

- What if multiple employees have the second highest salary?
- What if the table has only one distinct salary?
- Difference between second highest row and second highest distinct salary?
- Why use `DENSE_RANK()` instead of `ROW_NUMBER()`?

## Nth Highest Salary

Concept: Rank salaries and filter the rank requested by the interviewer.

```sql
SELECT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
    FROM employee
) ranked
WHERE salary_rank = 3;
```

Use `3` for third highest salary. Replace it with the requested N.

Alternate approach using distinct salaries:

```sql
SELECT salary
FROM (
    SELECT DISTINCT salary
    FROM employee
    ORDER BY salary DESC
    OFFSET 2
    LIMIT 1
) x;
```

Explanation:

- For third highest, `OFFSET 2` skips the first two distinct salaries.
- `LIMIT 1` returns the next salary.

Performance and correctness:

- `DENSE_RANK()` keeps row-level detail available if you need employee names too.
- `DISTINCT + OFFSET` is readable for one column, but can become awkward when returning all employees with that salary.
- For very large tables, both approaches may need sorting unless an index and plan can satisfy the order.

Interviewer follow-ups:

- Return employees who earn the Nth highest salary.
- Return Nth highest salary department-wise.
- Explain what happens when N is larger than the number of distinct salaries.

## Ranking Functions: ROW_NUMBER, RANK, DENSE_RANK

Concept: Ranking functions assign position to rows after sorting.

```sql
SELECT employee_id,
       employee_name,
       department_id,
       salary,
       ROW_NUMBER() OVER (ORDER BY salary DESC, employee_id) AS row_number_value,
       RANK() OVER (ORDER BY salary DESC) AS rank_value,
       DENSE_RANK() OVER (ORDER BY salary DESC) AS dense_rank_value
FROM employee;
```

Detailed explanation:

- `ROW_NUMBER()` always gives unique numbers. If two rows tie, PostgreSQL still assigns different row numbers based on the full `ORDER BY`.
- `RANK()` gives the same rank to ties and leaves gaps. If two rows are rank 1, the next row is rank 3.
- `DENSE_RANK()` gives the same rank to ties without gaps. If two rows are rank 1, the next row is rank 2.

When to use:

- Use `ROW_NUMBER()` when you need exactly one row per group, such as latest transaction per account.
- Use `RANK()` when competition-style gaps matter.
- Use `DENSE_RANK()` when you need Nth distinct value, such as second highest distinct salary.

Correctness tip: Always include a deterministic tie-breaker with `ROW_NUMBER()`, such as `employee_id` or `transaction_id`. Without it, tied rows can be returned in arbitrary order.

Interviewer follow-ups:

- Which function returns exactly three rows for top 3?
- Which function can return more than three rows for top 3 when ties exist?
- Which function should be used for deduplication?

## Duplicate Records

Concept: Group by the columns that define duplication.

```sql
SELECT employee_name,
       department_id,
       COUNT(*) AS duplicate_count
FROM employee
GROUP BY employee_name, department_id
HAVING COUNT(*) > 1;
```

Why it matters: Duplicate detection depends on business meaning. Two employees with the same name are not always duplicates. Two payment rows with the same external reference, amount, and timestamp might be suspicious.

Banking-style duplicate payment example:

```sql
SELECT account_id,
       amount,
       paid_at,
       COUNT(*) AS duplicate_count
FROM payment
GROUP BY account_id, amount, paid_at
HAVING COUNT(*) > 1;
```

Alternate approach: Use a window count to keep row details visible.

```sql
SELECT payment_id,
       account_id,
       amount,
       paid_at,
       duplicate_count
FROM (
    SELECT p.*,
           COUNT(*) OVER (
               PARTITION BY account_id, amount, paid_at
           ) AS duplicate_count
    FROM payment p
) x
WHERE duplicate_count > 1;
```

Performance and correctness:

- `GROUP BY` is best when you only need duplicate keys and counts.
- Window count is better when you need the exact row IDs to inspect.
- Create a unique constraint when duplicates must never happen. Detection queries are not a substitute for data integrity rules.

Interviewer follow-ups:

- How would you prevent duplicates at insert time?
- What columns define duplicate transactions?
- How would you find duplicate rows but keep full row details?

## Remove Duplicates

Concept: Keep one row from each duplicate group and delete the rest.

```sql
DELETE FROM employee
WHERE employee_id IN (
    SELECT employee_id
    FROM (
        SELECT employee_id,
               ROW_NUMBER() OVER (
                   PARTITION BY employee_name, department_id, salary
                   ORDER BY employee_id
               ) AS row_num
        FROM employee
    ) x
    WHERE row_num > 1
);
```

How it works:

- `PARTITION BY employee_name, department_id, salary` defines duplicate groups.
- `ORDER BY employee_id` chooses the row to keep.
- Rows with `row_num > 1` are duplicates.

PostgreSQL alternate using `ctid` when there is no primary key:

```sql
DELETE FROM employee e
USING (
    SELECT ctid,
           ROW_NUMBER() OVER (
               PARTITION BY employee_name, department_id, salary
               ORDER BY ctid
           ) AS row_num
    FROM employee
) x
WHERE e.ctid = x.ctid
  AND x.row_num > 1;
```

Performance and correctness:

- Prefer deleting by primary key when available.
- `ctid` is PostgreSQL-specific and should be treated as a last resort for cleanup, not application logic.
- In production, preview rows with `SELECT` first and delete in batches if the table is large.
- After cleanup, add a unique constraint or unique index if the duplicate state is invalid.

Interviewer follow-ups:

- How do you decide which duplicate to keep?
- How would you remove duplicates from a table with no primary key?
- Why should cleanup be followed by a constraint?

## Employees Earning More Than Manager

Concept: Self-join the employee table to compare each employee with their manager.

```sql
SELECT e.employee_id,
       e.employee_name,
       e.salary,
       m.employee_name AS manager_name,
       m.salary AS manager_salary
FROM employee e
JOIN employee m
    ON e.manager_id = m.employee_id
WHERE e.salary > m.salary;
```

Why it matters: Self-joins test whether you understand that a table can represent two roles in the same relationship.

Alternate approach using `EXISTS`:

```sql
SELECT e.employee_id,
       e.employee_name,
       e.salary
FROM employee e
WHERE EXISTS (
    SELECT 1
    FROM employee m
    WHERE m.employee_id = e.manager_id
      AND e.salary > m.salary
);
```

Performance and correctness:

- The join version is usually clearer and returns manager details.
- The `EXISTS` version is useful when you only need employee rows.
- `manager_id` can be `NULL` for top-level employees, and the inner join naturally excludes them.

Interviewer follow-ups:

- Include employees without managers.
- Find managers whose team average salary is greater than their own salary.
- Find employees earning more than department average.

## Department Highest Salary

Concept: Rank employees inside each department.

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
    FROM employee e
) ranked
WHERE salary_rank = 1;
```

Why `DENSE_RANK()` is correct: If two employees tie for the highest salary in a department, both are rank 1 and both should usually be returned.

Alternate approach:

```sql
SELECT e.department_id,
       e.employee_id,
       e.employee_name,
       e.salary
FROM employee e
JOIN (
    SELECT department_id,
           MAX(salary) AS max_salary
    FROM employee
    GROUP BY department_id
) m
    ON e.department_id = m.department_id
   AND e.salary = m.max_salary;
```

Performance and correctness:

- The aggregate join can be efficient and clear for maximum-per-group.
- The ranking approach generalizes to second highest, top N, and tie-aware reports.
- A composite index on `(department_id, salary DESC)` can help grouping or ordered partition access, depending on the PostgreSQL plan.

Interviewer follow-ups:

- Return only one employee per department.
- Return all ties for highest salary.
- Return the top 3 earners per department.

## Department Second Highest Salary

Concept: Use partitioned dense ranking and filter rank 2.

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
    FROM employee e
) ranked
WHERE salary_rank = 2;
```

How it works:

- `PARTITION BY department_id` restarts ranking for every department.
- `ORDER BY salary DESC` ranks the highest salary first.
- `DENSE_RANK()` handles ties and returns the second distinct salary level.

Alternate approach using a correlated subquery:

```sql
SELECT e.department_id,
       e.employee_id,
       e.employee_name,
       e.salary
FROM employee e
WHERE 1 = (
    SELECT COUNT(DISTINCT e2.salary)
    FROM employee e2
    WHERE e2.department_id = e.department_id
      AND e2.salary > e.salary
);
```

Performance and correctness:

- Window ranking is usually easier to read and less error-prone.
- Correlated subqueries can be slower on large tables if PostgreSQL cannot optimize them well.
- Departments with only one distinct salary return no rows.

Interviewer follow-ups:

- Return departments that do not have a second highest salary.
- Return second highest salary amount only, not employees.
- Handle ties differently using `ROW_NUMBER()`.

## Top N Per Group

Concept: Rank rows within each group, then filter the rank.

Banking example: top 3 payments by amount per account.

```sql
SELECT account_id,
       payment_id,
       amount,
       paid_at
FROM (
    SELECT p.*,
           ROW_NUMBER() OVER (
               PARTITION BY account_id
               ORDER BY amount DESC, paid_at DESC, payment_id DESC
           ) AS row_num
    FROM payment p
    WHERE status = 'POSTED'
) ranked
WHERE row_num <= 3
ORDER BY account_id, row_num;
```

Why `ROW_NUMBER()` here: The question asks for exactly 3 rows per account. If ties should be included, use `RANK()` or `DENSE_RANK()` instead.

Tie-inclusive top 3 amounts:

```sql
SELECT account_id,
       payment_id,
       amount,
       paid_at
FROM (
    SELECT p.*,
           DENSE_RANK() OVER (
               PARTITION BY account_id
               ORDER BY amount DESC
           ) AS amount_rank
    FROM payment p
    WHERE status = 'POSTED'
) ranked
WHERE amount_rank <= 3;
```

Performance and correctness:

- Filter early with `WHERE status = 'POSTED'` before ranking.
- Use deterministic ordering for `ROW_NUMBER()` to make results stable.
- A useful index for this pattern is often `(account_id, amount DESC)` with included columns, but validate with `EXPLAIN`.

Interviewer follow-ups:

- Return top N customers by total revenue.
- Include ties at rank N.
- Return accounts where the third highest payment exceeds a threshold.

## Customers Never Ordered

Concept: Use an anti-join to find parent rows without child rows.

Sample tables:

```sql
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY,
    customer_id BIGINT REFERENCES customer(customer_id),
    order_amount NUMERIC(12, 2) NOT NULL,
    ordered_at TIMESTAMP NOT NULL
);
```

Query:

```sql
SELECT c.customer_id,
       c.customer_name
FROM customer c
LEFT JOIN orders o
    ON c.customer_id = o.customer_id
WHERE o.order_id IS NULL;
```

Alternate approach:

```sql
SELECT c.customer_id,
       c.customer_name
FROM customer c
WHERE NOT EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.customer_id = c.customer_id
);
```

Performance and correctness:

- `NOT EXISTS` avoids mistakes when checking nullable columns.
- With `LEFT JOIN`, filter a non-null child key such as `o.order_id IS NULL`.
- Index `orders(customer_id)` for large child tables.

Interviewer follow-ups:

- Customers with no orders in the last 90 days.
- Customers with accounts but no posted payments.
- Difference between `NOT IN` and `NOT EXISTS` when nulls exist.

## Top Customers By Revenue

Concept: Join orders to customers, group by customer, then sort by aggregate revenue.

```sql
SELECT c.customer_id,
       c.customer_name,
       SUM(o.order_amount) AS total_revenue
FROM customer c
JOIN orders o
    ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.customer_name
ORDER BY total_revenue DESC
LIMIT 10;
```

Banking version: top customers by posted payment volume in the last 30 days.

```sql
SELECT c.customer_id,
       c.customer_name,
       SUM(p.amount) AS posted_payment_volume
FROM customer c
JOIN account a
    ON c.customer_id = a.customer_id
JOIN payment p
    ON a.account_id = p.account_id
WHERE p.status = 'POSTED'
  AND p.paid_at >= CURRENT_TIMESTAMP - INTERVAL '30 days'
GROUP BY c.customer_id, c.customer_name
ORDER BY posted_payment_volume DESC
LIMIT 10;
```

DATE and INTERVAL explanation:

- `CURRENT_TIMESTAMP - INTERVAL '30 days'` creates a rolling 30-day timestamp boundary.
- If the business asks for calendar month, use `date_trunc('month', CURRENT_DATE)` instead of a rolling interval.

Performance and correctness:

- Filter by status and date before aggregation.
- Use `NUMERIC` for money-like values, not floating point.
- A partial index such as `(paid_at, account_id) WHERE status = 'POSTED'` can help recent posted-payment reports.

Interviewer follow-ups:

- Top customers by count instead of amount.
- Top customers for the current calendar month.
- Include customers with zero revenue.

## Running Total

Concept: A running total adds values row by row in a defined order while keeping each row visible.

```sql
SELECT account_id,
       payment_id,
       paid_at,
       amount,
       SUM(amount) OVER (
           PARTITION BY account_id
           ORDER BY paid_at, payment_id
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_total
FROM payment
WHERE status = 'POSTED'
ORDER BY account_id, paid_at, payment_id;
```

How it works:

- `PARTITION BY account_id` calculates a separate running total for each account.
- `ORDER BY paid_at, payment_id` defines transaction sequence.
- `ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW` means start at the first row in the account and add through the current row.

Why the frame matters in PostgreSQL: When an ordered window has duplicate sort values, the default frame can behave like a peer-group range. `ROWS` makes the running total advance row by row.

Alternate approach: Pre-aggregate by day first, then calculate daily running total.

```sql
WITH daily_payment AS (
    SELECT account_id,
           paid_at::date AS payment_date,
           SUM(amount) AS daily_amount
    FROM payment
    WHERE status = 'POSTED'
    GROUP BY account_id, paid_at::date
)
SELECT account_id,
       payment_date,
       daily_amount,
       SUM(daily_amount) OVER (
           PARTITION BY account_id
           ORDER BY payment_date
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS daily_running_total
FROM daily_payment;
```

Performance and correctness:

- Running totals require a reliable order. Use a tie-breaker such as `payment_id`.
- For ledger-style balances, credits and debits must have signs applied before summing.
- Index `(account_id, paid_at, payment_id)` can help order rows for account history queries.

Interviewer follow-ups:

- Calculate running balance with credits and debits.
- Reset running total every month.
- Explain why `ROWS` is safer than relying on the default frame.

## Moving Average

Concept: A moving average calculates an average over the current row and nearby previous rows.

Example: 3-payment moving average per account.

```sql
SELECT account_id,
       payment_id,
       paid_at,
       amount,
       AVG(amount) OVER (
           PARTITION BY account_id
           ORDER BY paid_at, payment_id
           ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
       ) AS three_payment_moving_average
FROM payment
WHERE status = 'POSTED'
ORDER BY account_id, paid_at, payment_id;
```

How it works:

- Current row plus two previous rows gives a maximum frame size of three rows.
- The first row averages one row, the second averages two rows, and the third onward averages three rows.

Date-based moving average: average posted payment amount over the last 7 calendar days.

```sql
SELECT p1.account_id,
       p1.payment_id,
       p1.paid_at,
       p1.amount,
       (
           SELECT AVG(p2.amount)
           FROM payment p2
           WHERE p2.account_id = p1.account_id
             AND p2.status = 'POSTED'
             AND p2.paid_at >= p1.paid_at - INTERVAL '7 days'
             AND p2.paid_at <= p1.paid_at
       ) AS seven_day_average_amount
FROM payment p1
WHERE p1.status = 'POSTED';
```

Performance and correctness:

- Row-based moving averages count rows, not days.
- Date-based moving averages need date or timestamp conditions.
- For large tables, date-based correlated subqueries can be expensive; consider pre-aggregating by day or using range frames if the ordering expression and requirement fit.

Interviewer follow-ups:

- Difference between last 3 transactions and last 3 days.
- How to handle missing days.
- How to calculate moving sum instead of moving average.

## Consecutive Numbers

Concept: Compare each row with previous and next rows.

Sample table:

```sql
CREATE TABLE log_number (
    id BIGINT PRIMARY KEY,
    num INT NOT NULL
);
```

Query:

```sql
SELECT DISTINCT num
FROM (
    SELECT num,
           LAG(num, 1) OVER (ORDER BY id) AS previous_num,
           LEAD(num, 1) OVER (ORDER BY id) AS next_num
    FROM log_number
) x
WHERE num = previous_num
  AND num = next_num;
```

Explanation: This finds a number that appears in three consecutive rows.

Alternate approach using grouped islands:

```sql
SELECT num
FROM (
    SELECT num,
           id - ROW_NUMBER() OVER (PARTITION BY num ORDER BY id) AS group_key
    FROM log_number
) x
GROUP BY num, group_key
HAVING COUNT(*) >= 3;
```

How the island approach works:

- Consecutive rows with the same `num` have a stable `id - row_number` difference when `id` increases by 1.
- Grouping by that difference finds runs of the same number.
- `HAVING COUNT(*) >= 3` keeps runs of at least three.

Correctness warning: The island approach assumes `id` values are consecutive. If IDs have gaps, use a separate `ROW_NUMBER() OVER (ORDER BY id)` sequence for position.

Interviewer follow-ups:

- Find values appearing at least 5 times consecutively.
- Find consecutive failed login attempts per customer.
- Handle gaps in IDs.

## Consecutive Failed Transactions

Concept: Use `LAG()` or island grouping to find repeated consecutive statuses.

Example: accounts with three failed transactions in a row.

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
    FROM transaction_log
) x
WHERE status = 'FAILED'
  AND previous_status = 'FAILED'
  AND status_two_back = 'FAILED';
```

Alternate approach for runs of any length:

```sql
WITH marked AS (
    SELECT transaction_log.*,
           ROW_NUMBER() OVER (
               PARTITION BY account_id
               ORDER BY transaction_time, transaction_id
           ) AS all_row_num,
           ROW_NUMBER() OVER (
               PARTITION BY account_id, status
               ORDER BY transaction_time, transaction_id
           ) AS status_row_num
    FROM transaction_log
),
grouped AS (
    SELECT *,
           all_row_num - status_row_num AS run_key
    FROM marked
)
SELECT account_id,
       MIN(transaction_time) AS run_started_at,
       MAX(transaction_time) AS run_ended_at,
       COUNT(*) AS failed_count
FROM grouped
WHERE status = 'FAILED'
GROUP BY account_id, run_key
HAVING COUNT(*) >= 3;
```

Performance and correctness:

- Always define sequence using timestamp plus a unique tie-breaker.
- `LAG()` is simple for exactly three in a row.
- Island grouping is better when the threshold can change.

Interviewer follow-ups:

- Find customers with three failed payments within 10 minutes.
- Find the first failed transaction in each failure streak.
- Reset the streak after a successful transaction.

## Rising Temperature

Concept: Compare each date with the previous calendar date.

Sample table:

```sql
CREATE TABLE weather (
    id BIGINT PRIMARY KEY,
    record_date DATE NOT NULL,
    temperature INT NOT NULL
);
```

Query:

```sql
SELECT today.id
FROM weather today
JOIN weather yesterday
    ON today.record_date = yesterday.record_date + INTERVAL '1 day'
WHERE today.temperature > yesterday.temperature;
```

DATE and INTERVAL explanation:

- `record_date` is a `DATE`.
- `INTERVAL '1 day'` adds one calendar day.
- The join requires the previous date to actually exist. If a date is missing, the row is not compared to the last available date.

Alternate approach using `LAG()`:

```sql
SELECT id
FROM (
    SELECT id,
           record_date,
           temperature,
           LAG(record_date) OVER (ORDER BY record_date) AS previous_date,
           LAG(temperature) OVER (ORDER BY record_date) AS previous_temperature
    FROM weather
) x
WHERE record_date = previous_date + INTERVAL '1 day'
  AND temperature > previous_temperature;
```

Performance and correctness:

- The self-join is direct and can use an index on `record_date`.
- The `LAG()` version is useful when you also need previous-row values in the output.
- Keep the consecutive-date check in the `LAG()` version; otherwise it compares with the previous available row, not necessarily yesterday.

Interviewer follow-ups:

- Compare with previous available reading instead of previous calendar day.
- Return dates, not IDs.
- Find three consecutive days of increasing temperature.

## Transactions In Last N Days

Concept: Use PostgreSQL interval arithmetic for rolling windows.

```sql
SELECT transaction_id,
       account_id,
       amount,
       transaction_time
FROM transaction_log
WHERE transaction_time >= CURRENT_TIMESTAMP - INTERVAL '7 days'
ORDER BY transaction_time DESC;
```

Current calendar month:

```sql
SELECT transaction_id,
       account_id,
       amount,
       transaction_time
FROM transaction_log
WHERE transaction_time >= date_trunc('month', CURRENT_DATE)
  AND transaction_time < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month';
```

Why this is better than extracting month:

```sql
WHERE EXTRACT(MONTH FROM transaction_time) = EXTRACT(MONTH FROM CURRENT_DATE)
```

The `EXTRACT` version can mix years and usually prevents efficient use of a normal timestamp index because it applies a function to the column.

Performance and correctness:

- Prefer range predicates on the raw timestamp column.
- Use an exclusive upper bound for date ranges: `< next_period_start`.
- Be explicit about rolling window versus calendar period.

Interviewer follow-ups:

- Last 30 days versus current month.
- Yesterday's transactions only.
- How timezone affects timestamp comparisons.

## Accounts With No Transactions In 90 Days

Concept: Anti-join accounts against recent transactions.

```sql
SELECT a.account_id,
       a.customer_id,
       a.opened_at
FROM account a
WHERE NOT EXISTS (
    SELECT 1
    FROM transaction_log t
    WHERE t.account_id = a.account_id
      AND t.transaction_time >= CURRENT_TIMESTAMP - INTERVAL '90 days'
);
```

Alternate approach using aggregate:

```sql
SELECT a.account_id,
       a.customer_id,
       MAX(t.transaction_time) AS last_transaction_time
FROM account a
LEFT JOIN transaction_log t
    ON a.account_id = t.account_id
GROUP BY a.account_id, a.customer_id
HAVING MAX(t.transaction_time) IS NULL
    OR MAX(t.transaction_time) < CURRENT_TIMESTAMP - INTERVAL '90 days';
```

Performance and correctness:

- `NOT EXISTS` can stop searching after finding one recent row.
- Aggregate is useful when you also need the last transaction timestamp.
- Index `(account_id, transaction_time)` is helpful for this pattern.

Interviewer follow-ups:

- Exclude accounts opened less than 90 days ago.
- Count inactive accounts by account type.
- Return last transaction date for inactive accounts.

## Interview Tip

Most medium SQL interview problems are combinations of:

- Join
- Group
- Rank
- Filter ranked result
- Compare current row with previous or next row
- Use date ranges with `DATE`, `TIMESTAMP`, and `INTERVAL`

A strong interview answer usually says:

- What defines the group or partition?
- What ordering makes the result correct?
- How ties should be handled?
- Whether the date condition is rolling or calendar-based?
- What index would help if the table is large?
