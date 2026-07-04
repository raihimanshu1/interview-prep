# Wells Fargo Top 25 SQL Practice Order

If preparing for a Wells Fargo Senior Java Engineer or backend role, practice these 25 SQL patterns first.

All examples use PostgreSQL syntax. The goal is not only to memorize answers, but to understand the pattern well enough to explain it, adapt it, and defend it in an interview.

## Primer: SQL Execution Order and Window Functions

SQL is written in one order but logically evaluated in another order. A useful mental model is:

1. `FROM` and `JOIN`: choose source rows and combine tables.
2. `WHERE`: filter individual rows before grouping.
3. `GROUP BY`: collapse rows into groups.
4. `HAVING`: filter grouped results.
5. Window functions: calculate values across related rows without collapsing them.
6. `SELECT`: return normal expressions and window-function results.
7. `ORDER BY`: sort the final result.
8. `LIMIT` / `OFFSET`: return only part of the final result.

This matters because window functions are evaluated after `WHERE`, `GROUP BY`, and `HAVING`. Their results are usable in `SELECT` and final `ORDER BY`, but not in the same query block's `WHERE` or `HAVING`. Put the window calculation in a subquery or CTE, then filter outside it.

Ranking functions are common in salary, transaction, score, and latest-record questions:

- `ROW_NUMBER()` gives every row a unique sequence number. Ties still get different numbers.
- `RANK()` gives ties the same rank, but leaves gaps after ties.
- `DENSE_RANK()` gives ties the same rank and does not leave gaps.
- `LEAD(column)` reads a later row in the current window.
- `LAG(column)` reads an earlier row in the current window.

Use `PARTITION BY` when the ranking or comparison resets per group, such as per department or per account. Use `ORDER BY` inside the `OVER (...)` clause to define what "first", "next", "previous", or "highest" means.

## 1. Combine Two Tables

**PostgreSQL Query**

```sql
SELECT p.first_name,
       p.last_name,
       a.city,
       a.state
FROM person p
LEFT JOIN address a
    ON p.person_id = a.person_id;
```

**Concept involved:** Basic `LEFT JOIN`.

**Why the query works:** `person` is the primary table, so every person should appear even if there is no address. A `LEFT JOIN` keeps all rows from `person` and attaches matching `address` rows when `person_id` matches. If no address exists, PostgreSQL returns `NULL` for address columns.

**Alternate approach:** Use an `INNER JOIN` only if the requirement says to return people who have addresses. Use `COALESCE(a.city, 'Unknown')` if the interviewer wants a display value instead of `NULL`.

**Performance/index note:** Index `address(person_id)` because it is the lookup column on the joined table. `person(person_id)` is usually already indexed as a primary key.

**Common interviewer follow-up:** What changes if one person can have multiple addresses? The result will contain multiple rows per person, one per matching address.

## 2. Customers Who Never Order

**PostgreSQL Query**

```sql
SELECT c.customer_id,
       c.customer_name
FROM customers c
LEFT JOIN orders o
    ON c.customer_id = o.customer_id
WHERE o.order_id IS NULL;
```

**Concept involved:** Anti-join using `LEFT JOIN` and `IS NULL`.

**Why the query works:** The `LEFT JOIN` keeps every customer. Customers with at least one order get matching order data. Customers without orders get `NULL` values from `orders`, so `WHERE o.order_id IS NULL` isolates customers who never ordered.

**Alternate approach:** `NOT EXISTS` is often cleaner and avoids duplicate join work:

```sql
SELECT c.customer_id,
       c.customer_name
FROM customers c
WHERE NOT EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.customer_id = c.customer_id
);
```

**Performance/index note:** Add an index on `orders(customer_id)`. For large tables, `NOT EXISTS` with this index is usually a strong choice.

**Common interviewer follow-up:** Why not use `NOT IN`? If the subquery returns `NULL`, `NOT IN` can produce surprising results, so `NOT EXISTS` is safer.

## 3. Duplicate Emails

**PostgreSQL Query**

```sql
SELECT email
FROM users
GROUP BY email
HAVING COUNT(*) > 1;
```

**Concept involved:** Aggregation with `GROUP BY` and `HAVING`.

**Why the query works:** `GROUP BY email` creates one group for each email value. `COUNT(*)` counts rows in each group. `HAVING` filters after grouping, so only email groups with more than one row remain.

**Alternate approach:** To show the duplicate rows themselves, use a window count:

```sql
SELECT user_id,
       email
FROM (
    SELECT u.*,
           COUNT(*) OVER (PARTITION BY email) AS email_count
    FROM users u
) x
WHERE email_count > 1;
```

**Performance/index note:** An index on `users(email)` helps grouping and duplicate detection. In production, a unique index on normalized email prevents duplicates.

**Common interviewer follow-up:** How do you handle case-insensitive duplicates? Use `LOWER(email)` in the query or PostgreSQL `citext` for case-insensitive text.

## 4. Second Highest Salary

**PostgreSQL Query**

```sql
SELECT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
    FROM employees
) ranked
WHERE salary_rank = 2;
```

**Concept involved:** `DENSE_RANK()` for distinct salary ranking.

**Why the query works:** `DENSE_RANK()` orders salaries from highest to lowest and gives equal salaries the same rank. Because it does not skip ranks, rank `2` means the second distinct salary value.

**Alternate approach:** For one value, use distinct ordering:

```sql
SELECT DISTINCT salary
FROM employees
ORDER BY salary DESC
OFFSET 1
LIMIT 1;
```

**Performance/index note:** An index on `employees(salary DESC)` can help the ordered distinct approach. Window ranking may still scan many rows.

**Common interviewer follow-up:** What if two employees share the highest salary? `DENSE_RANK()` still returns the next distinct salary as rank `2`.

## 5. Nth Highest Salary

**PostgreSQL Query**

```sql
SELECT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
    FROM employees
) ranked
WHERE salary_rank = 5;
```

**Concept involved:** Generalized ranking for the Nth distinct value.

**Why the query works:** The inner query assigns each distinct salary a dense rank. The outer query filters for the required rank. Replace `5` with the desired `N`.

**Alternate approach:** Parameterize `N` in application code or use `OFFSET :n - 1` with `SELECT DISTINCT salary` when only the salary value is needed.

**Performance/index note:** Index `employees(salary DESC)`. If the table is large and this query is frequent, consider whether the salary distribution can be maintained in a reporting table.

**Common interviewer follow-up:** Should this return one employee or all employees with the Nth salary? This version returns the salary value; joining back or selecting employee columns returns all employees at that salary.

## 6. Employees Earning More Than Managers

**PostgreSQL Query**

```sql
SELECT e.employee_id,
       e.employee_name,
       e.salary,
       m.employee_name AS manager_name,
       m.salary AS manager_salary
FROM employees e
JOIN employees m
    ON e.manager_id = m.employee_id
WHERE e.salary > m.salary;
```

**Concept involved:** Self join.

**Why the query works:** The same `employees` table represents both employees and managers. Alias `e` is the employee row, and alias `m` is the manager row. Joining `e.manager_id` to `m.employee_id` places both salaries on one row so they can be compared.

**Alternate approach:** Use `LEFT JOIN` if you also need to show employees without managers, then filter carefully because manager columns may be `NULL`.

**Performance/index note:** `employee_id` should be the primary key. Index `employees(manager_id)` if hierarchy lookups are common.

**Common interviewer follow-up:** How would you find employees two or more levels above or below someone? Use a recursive CTE for multi-level hierarchy traversal.

## 7. Department Highest Salary

**PostgreSQL Query**

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
WHERE salary_rank = 1;
```

**Concept involved:** Ranking within each department.

**Why the query works:** `PARTITION BY department_id` restarts the ranking for every department. `ORDER BY salary DESC` puts the highest salary first in each department. `DENSE_RANK() = 1` returns every employee tied for the top salary.

**Alternate approach:** Join to a grouped max salary:

```sql
SELECT e.department_id,
       e.employee_id,
       e.employee_name,
       e.salary
FROM employees e
JOIN (
    SELECT department_id,
           MAX(salary) AS max_salary
    FROM employees
    GROUP BY department_id
) m
    ON e.department_id = m.department_id
   AND e.salary = m.max_salary;
```

**Performance/index note:** A composite index on `employees(department_id, salary DESC)` helps both ranking and grouped max patterns.

**Common interviewer follow-up:** What if the interviewer wants only one employee per department? Use `ROW_NUMBER()` with a deterministic tie-breaker such as `employee_id`.

## 8. Department Top Three Salaries

**PostgreSQL Query**

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
WHERE salary_rank <= 3;
```

**Concept involved:** Top N distinct values per group.

**Why the query works:** The window function ranks salaries separately in each department. Filtering `salary_rank <= 3` keeps employees whose salary is in the top three distinct salary levels for their department.

**Alternate approach:** Use `ROW_NUMBER()` if "top three employees" means exactly three rows per department rather than top three salary levels.

**Performance/index note:** Use `employees(department_id, salary DESC)`. Add a tie-breaker column to the index when using `ROW_NUMBER()` with deterministic ordering.

**Common interviewer follow-up:** Why might this return more than three rows for a department? Because `DENSE_RANK()` includes all ties at each rank.

## 9. Rank Scores

**PostgreSQL Query**

```sql
SELECT score,
       DENSE_RANK() OVER (ORDER BY score DESC) AS rank
FROM scores
ORDER BY score DESC;
```

**Concept involved:** Global ranking without partitioning.

**Why the query works:** Without `PARTITION BY`, the entire result is one ranking group. `DENSE_RANK()` gives the same rank to equal scores and keeps the next rank consecutive.

**Alternate approach:** Use `RANK()` if the expected output should leave gaps after ties, or `ROW_NUMBER()` if every row must have a unique number.

**Performance/index note:** Index `scores(score DESC)` for frequent ordered score queries.

**Common interviewer follow-up:** What is the difference between `RANK()` and `DENSE_RANK()` for scores `100, 100, 90`? `RANK()` gives `1, 1, 3`; `DENSE_RANK()` gives `1, 1, 2`.

## 10. Consecutive Numbers

**PostgreSQL Query**

```sql
SELECT DISTINCT num
FROM (
    SELECT num,
           LAG(num) OVER (ORDER BY id) AS previous_num,
           LEAD(num) OVER (ORDER BY id) AS next_num
    FROM logs
) x
WHERE num = previous_num
  AND num = next_num;
```

**Concept involved:** Neighbor comparison with `LAG()` and `LEAD()`.

**Why the query works:** Ordering by `id` defines the sequence. `LAG()` gets the previous row's number and `LEAD()` gets the next row's number. If the current number equals both neighbors, that number appears at least three times consecutively.

**Alternate approach:** Self join three adjacent rows:

```sql
SELECT DISTINCT l1.num
FROM logs l1
JOIN logs l2 ON l2.id = l1.id + 1
JOIN logs l3 ON l3.id = l1.id + 2
WHERE l1.num = l2.num
  AND l2.num = l3.num;
```

**Performance/index note:** Index `logs(id)`. The self-join approach assumes there are no gaps in `id`; the window approach only needs a stable ordering.

**Common interviewer follow-up:** How would you find five consecutive rows instead of three? Use multiple `LEAD()` calls or group consecutive runs with row-number differences.

## 11. Rising Temperature

**PostgreSQL Query**

```sql
SELECT today.id
FROM weather today
JOIN weather yesterday
    ON today.record_date = yesterday.record_date + INTERVAL '1 day'
WHERE today.temperature > yesterday.temperature;
```

**Concept involved:** Self join using date arithmetic.

**Why the query works:** The join pairs each row with the row from the previous calendar day. Once today and yesterday are on the same row, the `WHERE` clause compares temperatures.

**Alternate approach:** Use `LAG()`:

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

**Performance/index note:** Index `weather(record_date)`. The explicit date condition matters because "previous row" is not always "previous day" if dates are missing.

**Common interviewer follow-up:** What if the table has multiple readings per day? Aggregate to one row per day first, or include timestamp logic depending on the requirement.

## 12. Running Total

**PostgreSQL Query**

```sql
SELECT account_id,
       transaction_id,
       transaction_timestamp,
       amount,
       SUM(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_timestamp, transaction_id
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_total
FROM transactions;
```

**Concept involved:** Window aggregate.

**Why the query works:** `SUM(amount) OVER (...)` calculates a cumulative sum without collapsing rows. `PARTITION BY account_id` creates a separate running total per account. The frame from `UNBOUNDED PRECEDING` to `CURRENT ROW` includes all earlier transactions in that account.

**Alternate approach:** A correlated subquery can compute the same result, but it is usually slower and harder to read.

**Performance/index note:** Index `transactions(account_id, transaction_timestamp, transaction_id)` to support partitioning and ordering. Include `transaction_id` to make ordering deterministic when timestamps tie.

**Common interviewer follow-up:** Why specify `ROWS`? It avoids the default `RANGE` behavior, which can group rows with equal ordering values.

## 13. Moving Average

**PostgreSQL Query**

```sql
SELECT account_id,
       transaction_id,
       amount,
       AVG(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_timestamp, transaction_id
           ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
       ) AS moving_average_3_rows
FROM transactions;
```

**Concept involved:** Window frame over a limited number of rows.

**Why the query works:** The frame includes the current row plus the two previous rows in the same account partition. `AVG(amount)` then returns a three-row moving average when enough rows exist, and a smaller average at the start of the partition.

**Alternate approach:** For a time-based moving average, use a `RANGE` frame over an interval when the ordering column supports it, or join against a filtered time window.

**Performance/index note:** Index `transactions(account_id, transaction_timestamp, transaction_id)`. Moving averages over very large partitions may still require sorting and memory.

**Common interviewer follow-up:** What is the difference between "last three rows" and "last three days"? Rows count records; days require date or timestamp range logic.

## 14. Top N Per Group

**PostgreSQL Query**

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
WHERE row_num <= 3;
```

**Concept involved:** `ROW_NUMBER()` for a fixed number of rows per group.

**Why the query works:** `PARTITION BY customer_id` restarts numbering for each customer. `ORDER BY amount DESC` puts the largest transactions first. `row_num <= 3` keeps exactly the top three rows per customer, assuming each customer has at least three rows.

**Alternate approach:** Use `DENSE_RANK()` if the requirement is top three distinct transaction amounts and ties should be included.

**Performance/index note:** Index `transactions(customer_id, amount DESC, transaction_id)`. The tie-breaker makes results repeatable.

**Common interviewer follow-up:** How do you include ties for third place? Replace `ROW_NUMBER()` with `RANK()` or `DENSE_RANK()` depending on whether gaps matter.

## 15. Remove Duplicates

**PostgreSQL Query**

```sql
DELETE FROM users
WHERE user_id IN (
    SELECT user_id
    FROM (
        SELECT user_id,
               ROW_NUMBER() OVER (
                   PARTITION BY email
                   ORDER BY user_id
               ) AS row_num
        FROM users
    ) x
    WHERE row_num > 1
);
```

**Concept involved:** Deduplication with `ROW_NUMBER()`.

**Why the query works:** The inner query groups rows by email and orders each duplicate set by `user_id`. `ROW_NUMBER() = 1` is the row to keep, and rows with `row_num > 1` are duplicates to delete.

**Alternate approach:** PostgreSQL also supports deleting with a CTE:

```sql
WITH duplicates AS (
    SELECT user_id,
           ROW_NUMBER() OVER (
               PARTITION BY email
               ORDER BY user_id
           ) AS row_num
    FROM users
)
DELETE FROM users u
USING duplicates d
WHERE u.user_id = d.user_id
  AND d.row_num > 1;
```

**Performance/index note:** Index `users(email, user_id)`. Before deleting, run the inner `SELECT` alone to verify which rows will be removed.

**Common interviewer follow-up:** After cleanup, how do you prevent the issue from returning? Add a unique index, often on `LOWER(email)` if emails should be case-insensitive.

## 16. Latest Record Per User

**PostgreSQL Query**

```sql
SELECT user_id,
       login_timestamp,
       status
FROM (
    SELECT l.*,
           ROW_NUMBER() OVER (
               PARTITION BY user_id
               ORDER BY login_timestamp DESC, login_id DESC
           ) AS row_num
    FROM logins l
) ranked
WHERE row_num = 1;
```

**Concept involved:** Latest row per group.

**Why the query works:** Each user's login rows are ordered newest first. `ROW_NUMBER()` assigns `1` to the latest row in each user partition, and the outer query keeps only that row.

**Alternate approach:** PostgreSQL-specific `DISTINCT ON` is concise:

```sql
SELECT DISTINCT ON (user_id)
       user_id,
       login_timestamp,
       status
FROM logins
ORDER BY user_id, login_timestamp DESC, login_id DESC;
```

**Performance/index note:** Index `logins(user_id, login_timestamp DESC, login_id DESC)`. Include a tie-breaker because two records can share the same timestamp.

**Common interviewer follow-up:** Why not use `MAX(login_timestamp)` only? It gives the timestamp, not necessarily the full row tied to that timestamp.

## 17. Self Join Manager Hierarchy

**PostgreSQL Query**

```sql
SELECT e.employee_name AS employee_name,
       m.employee_name AS manager_name
FROM employees e
LEFT JOIN employees m
    ON e.manager_id = m.employee_id;
```

**Concept involved:** Self join for parent-child relationships.

**Why the query works:** The employee row stores a `manager_id` that points back to another employee row. A `LEFT JOIN` preserves employees without managers, such as a CEO or department head.

**Alternate approach:** For full reporting chains, use a recursive CTE:

```sql
WITH RECURSIVE hierarchy AS (
    SELECT employee_id,
           employee_name,
           manager_id,
           1 AS level
    FROM employees
    WHERE manager_id IS NULL

    UNION ALL

    SELECT e.employee_id,
           e.employee_name,
           e.manager_id,
           h.level + 1
    FROM employees e
    JOIN hierarchy h
        ON e.manager_id = h.employee_id
)
SELECT *
FROM hierarchy;
```

**Performance/index note:** Index `employees(manager_id)` for finding direct reports. Primary key index on `employee_id` supports manager lookup.

**Common interviewer follow-up:** How do you find all employees under one manager? Start the recursive CTE from that manager and walk downward.

## 18. Daily Active Users

**PostgreSQL Query**

```sql
SELECT login_timestamp::date AS login_date,
       COUNT(DISTINCT user_id) AS daily_active_users
FROM logins
GROUP BY login_timestamp::date
ORDER BY login_date;
```

**Concept involved:** Date bucketing and distinct counts.

**Why the query works:** Casting the timestamp to `date` groups all logins from the same calendar day together. `COUNT(DISTINCT user_id)` counts each user once per day, even if they logged in multiple times.

**Alternate approach:** Use `DATE_TRUNC('day', login_timestamp)` if the output should remain a timestamp at midnight rather than a date.

**Performance/index note:** For frequent reporting, consider an expression index on `(login_timestamp::date)` or a generated date column. If time zones matter, convert before casting.

**Common interviewer follow-up:** What if activity is defined by transactions, page views, or API calls instead of logins? Use the relevant event table and the same date-bucket plus distinct-user pattern.

## 19. Monthly Revenue

**PostgreSQL Query**

```sql
SELECT DATE_TRUNC('month', order_timestamp) AS order_month,
       SUM(order_amount) AS monthly_revenue
FROM orders
GROUP BY DATE_TRUNC('month', order_timestamp)
ORDER BY order_month;
```

**Concept involved:** Time-based aggregation.

**Why the query works:** `DATE_TRUNC('month', order_timestamp)` converts every timestamp in the same month to the same month-start value. Grouping by that expression creates one revenue bucket per month.

**Alternate approach:** If the business uses a fiscal month or timezone-specific month, adjust the timestamp before truncation.

**Performance/index note:** For large reporting workloads, index the timestamp column and consider summary tables. Expression indexes can help if the exact `DATE_TRUNC` expression is queried often.

**Common interviewer follow-up:** How do you exclude canceled orders? Add `WHERE status = 'completed'` before the `GROUP BY`.

## 20. Fraud Transaction Detection

**PostgreSQL Query**

```sql
SELECT customer_id,
       COUNT(*) AS high_value_transaction_count,
       SUM(amount) AS total_amount
FROM transactions
WHERE transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '1 day'
  AND amount >= 10000
GROUP BY customer_id
HAVING COUNT(*) >= 3;
```

**Concept involved:** Filtering, grouping, and threshold detection.

**Why the query works:** The `WHERE` clause first narrows rows to high-value transactions from the last day. The query then groups those rows by customer and uses `HAVING` to keep customers with at least three suspicious transactions.

**Alternate approach:** For a rolling 24-hour window per transaction, use window functions or a self join to compare events around each transaction timestamp.

**Performance/index note:** Useful indexes depend on data distribution. Common choices include `transactions(transaction_timestamp)`, `transactions(customer_id, transaction_timestamp)`, or a partial index for high-value transactions:

```sql
CREATE INDEX idx_transactions_high_value
ON transactions (customer_id, transaction_timestamp)
WHERE amount >= 10000;
```

**Common interviewer follow-up:** Why is `HAVING` used instead of `WHERE COUNT(*) >= 3`? `WHERE` filters rows before aggregation; `HAVING` filters groups after aggregation.

## 21. ROW_NUMBER

**PostgreSQL Query**

```sql
SELECT employee_id,
       salary,
       ROW_NUMBER() OVER (ORDER BY salary DESC, employee_id) AS row_num
FROM employees;
```

**Concept involved:** Unique row sequencing.

**Why the query works:** `ROW_NUMBER()` assigns a unique number to each row based on the specified order. Adding `employee_id` makes the result deterministic when salaries tie.

**Alternate approach:** Use `RANK()` or `DENSE_RANK()` when tied values should share the same rank.

**Performance/index note:** Index `employees(salary DESC, employee_id)` if this ordered ranking is common.

**Common interviewer follow-up:** Why can `ROW_NUMBER()` be dangerous without a tie-breaker? Equal ordering values may be returned in an unpredictable order.

## 22. RANK

**PostgreSQL Query**

```sql
SELECT employee_id,
       salary,
       RANK() OVER (ORDER BY salary DESC) AS salary_rank
FROM employees;
```

**Concept involved:** Ranking with ties and gaps.

**Why the query works:** `RANK()` gives equal salaries the same rank. If two rows tie for rank `1`, the next rank becomes `3`, because two rows already occupied the first two positions.

**Alternate approach:** Use `DENSE_RANK()` when gaps are not desired, especially for "Nth distinct salary" questions.

**Performance/index note:** Index `employees(salary DESC)` to support ordered processing.

**Common interviewer follow-up:** When would `RANK()` be preferred over `DENSE_RANK()`? In competition-style ranking where skipped positions communicate the number of tied rows.

## 23. DENSE_RANK

**PostgreSQL Query**

```sql
SELECT employee_id,
       salary,
       DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
FROM employees;
```

**Concept involved:** Ranking with ties and no gaps.

**Why the query works:** `DENSE_RANK()` gives tied salaries the same rank, but the next distinct salary gets the next consecutive rank. This makes it ideal for second-highest or Nth-highest distinct value problems.

**Alternate approach:** Use `ROW_NUMBER()` if exactly one row must be selected from each rank position.

**Performance/index note:** Index `employees(salary DESC)`. Add partition columns first in the index when ranking inside groups, such as `(department_id, salary DESC)`.

**Common interviewer follow-up:** For salaries `100, 100, 90, 80`, what rank is `80`? With `DENSE_RANK()`, it is rank `3`.

## 24. LEAD

**PostgreSQL Query**

```sql
SELECT account_id,
       transaction_id,
       transaction_timestamp,
       LEAD(transaction_timestamp) OVER (
           PARTITION BY account_id
           ORDER BY transaction_timestamp, transaction_id
       ) AS next_transaction_timestamp
FROM transactions;
```

**Concept involved:** Looking ahead within an ordered partition.

**Why the query works:** `LEAD()` returns a value from the next row in the same account partition. The ordering defines what "next" means, and the partition prevents one account's transaction from being compared to another account's transaction.

**Alternate approach:** Use `LEAD(column, 2)` to look two rows ahead, or provide a default with `LEAD(column, 1, default_value)`.

**Performance/index note:** Index `transactions(account_id, transaction_timestamp, transaction_id)` for partitioned ordering.

**Common interviewer follow-up:** What does `LEAD()` return for the last transaction in an account? It returns `NULL` unless a default value is supplied.

## 25. LAG

**PostgreSQL Query**

```sql
SELECT account_id,
       transaction_id,
       transaction_timestamp,
       amount,
       LAG(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_timestamp, transaction_id
       ) AS previous_transaction_amount
FROM transactions;
```

**Concept involved:** Looking backward within an ordered partition.

**Why the query works:** `LAG()` returns a value from the previous row in the same account partition. This is useful for comparing current and previous values, such as transaction amount changes, temperature changes, or status transitions.

**Alternate approach:** Use `LAG(amount, 2)` for the amount two rows back, or calculate differences directly:

```sql
SELECT account_id,
       transaction_id,
       amount,
       amount - LAG(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_timestamp, transaction_id
       ) AS amount_change
FROM transactions;
```

**Performance/index note:** The same index used for `LEAD()` helps here: `transactions(account_id, transaction_timestamp, transaction_id)`.

**Common interviewer follow-up:** What does `LAG()` return for the first transaction in an account? It returns `NULL` unless a default value is supplied.
