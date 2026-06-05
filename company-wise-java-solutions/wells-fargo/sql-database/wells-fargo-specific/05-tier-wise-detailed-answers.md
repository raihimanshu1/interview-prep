# Wells Fargo Tier-Wise SQL Questions With Detailed Answers

This file answers every topic listed in the Wells Fargo SQL roadmap. PostgreSQL syntax is used unless a section explicitly says Oracle/PLSQL.

## How To Study This File

For each interview question, do not memorize only the final query. Learn four things:

1. What concept is being tested.
2. Why the query works.
3. What alternate solution exists.
4. What performance or correctness follow-up the interviewer may ask.

For 7+ years experience, interviewers usually expect you to explain tradeoffs, not just write syntax.

Each answer should be read with this mini-template in mind:

- Concept: what SQL/database idea is being tested.
- Why/how: how the query produces the result.
- Alternate: another valid way to solve the same problem.
- Performance/correctness: indexes, tie handling, null handling, transactions, or ordering.
- Follow-up: what a senior interviewer may ask next.

## Logical SQL Query Execution Order

SQL is written in one order but logically processed in another order.

Written order:

```sql
SELECT ...
FROM ...
JOIN ...
WHERE ...
GROUP BY ...
HAVING ...
ORDER BY ...
LIMIT ...
```

Logical processing order:

```text
FROM / JOIN
WHERE
GROUP BY
HAVING
WINDOW FUNCTIONS
SELECT
ORDER BY
LIMIT
```

Why this matters:

- `WHERE` cannot use aggregate values like `COUNT(*)`; use `HAVING`.
- Window functions are evaluated after row filtering, grouping, and `HAVING`.
- Window function results are available in `SELECT` and final `ORDER BY`, but not in `WHERE` or `HAVING` of the same query block.
- To filter a window function result, calculate it in a subquery or CTE, then filter outside.
- `ORDER BY` controls final output order, but window functions have their own `ORDER BY` inside `OVER (...)`.
- `LIMIT` happens at the end, so use a stable `ORDER BY` with it.

Interview follow-up:

If asked why `WHERE COUNT(*) > 1` is invalid, answer: `WHERE` runs before grouping, so `COUNT(*)` does not exist yet. Use `HAVING COUNT(*) > 1`.

## Core Window Function Concepts

Window functions calculate values across related rows without collapsing the result like `GROUP BY`.

Basic shape:

```sql
window_function() OVER (
    PARTITION BY group_column
    ORDER BY sort_column
)
```

`PARTITION BY` means "restart calculation for each group."

`ORDER BY` means "define row order inside that group."

### ROW_NUMBER vs RANK vs DENSE_RANK

Use this salary list:

```text
100, 90, 90, 80
```

`ROW_NUMBER()` gives every row a unique number:

```text
100 -> 1
90  -> 2
90  -> 3
80  -> 4
```

`RANK()` gives ties the same rank and leaves gaps:

```text
100 -> 1
90  -> 2
90  -> 2
80  -> 4
```

`DENSE_RANK()` gives ties the same rank and does not leave gaps:

```text
100 -> 1
90  -> 2
90  -> 2
80  -> 3
```

When to use:

- Use `ROW_NUMBER()` when you need exactly one row, such as latest order per customer.
- Use `RANK()` when gaps matter, such as competition ranking.
- Use `DENSE_RANK()` for Nth highest distinct value, such as second highest salary.

### LEAD and LAG

`LAG()` looks at a previous row. `LEAD()` looks at a future row.

Use them for:

- Previous transaction amount
- Next transaction timestamp
- Day-over-day comparison
- Consecutive login or transaction patterns

Performance note:

Window functions often require sorting. Indexes on partition and order columns can help, such as `(account_id, transaction_timestamp)` for account transaction history.

## Common PostgreSQL Tables

```sql
CREATE TABLE departments (
    department_id BIGINT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL
);

CREATE TABLE employees (
    employee_id BIGINT PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    department_id BIGINT REFERENCES departments(department_id),
    manager_id BIGINT REFERENCES employees(employee_id),
    salary NUMERIC(12, 2) NOT NULL
);

CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(150)
);

CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(customer_id),
    product_id BIGINT,
    order_amount NUMERIC(12, 2) NOT NULL,
    order_timestamp TIMESTAMP NOT NULL
);

CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(customer_id),
    account_id BIGINT NOT NULL,
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

## Tier 1: Joins

### 1. INNER JOIN vs LEFT JOIN

`INNER JOIN` returns rows that match in both tables. `LEFT JOIN` returns all rows from the left table and matching rows from the right table. If there is no match, right-side columns become `NULL`.

Use `INNER JOIN` when the related record must exist. Use `LEFT JOIN` when you still need the base row even if related data is missing.

```sql
SELECT c.customer_id, c.customer_name, o.order_id
FROM customers c
INNER JOIN orders o
    ON c.customer_id = o.customer_id;
```

```sql
SELECT c.customer_id, c.customer_name, o.order_id
FROM customers c
LEFT JOIN orders o
    ON c.customer_id = o.customer_id;
```

Interview explanation: "Customers who ordered" usually means `INNER JOIN`. "All customers with order details if available" means `LEFT JOIN`.

Concept: This tests whether you understand matched vs unmatched rows and how join type changes result cardinality.

Alternate approach: You can often rewrite a `LEFT JOIN` requirement as `EXISTS` or `NOT EXISTS` when you only care whether a related row exists, not the related columns.

Performance note: Index foreign key columns used in joins, such as `orders(customer_id)`, so the database can find matching rows efficiently.

Common follow-up: If a customer has three orders, an inner join returns three rows for that customer. Joins can multiply rows when the right side has multiple matches.

### 2. Employees and Departments Join

Join employees to departments using the shared `department_id`.

```sql
SELECT e.employee_id,
       e.employee_name,
       d.department_name,
       e.salary
FROM employees e
JOIN departments d
    ON e.department_id = d.department_id;
```

Explanation: `employees.department_id` points to `departments.department_id`.

Concept: This is a standard foreign-key join from a child table (`employees`) to a parent/reference table (`departments`).

Alternate approach: Use `LEFT JOIN` if you want to include employees whose department is missing or not assigned.

```sql
SELECT e.employee_id,
       e.employee_name,
       d.department_name
FROM employees e
LEFT JOIN departments d
    ON e.department_id = d.department_id;
```

Performance/correctness note: `departments.department_id` is already indexed as a primary key. In many systems, `employees(department_id)` should also be indexed because it is used for joins and department-level reports.

Follow-up: If a department has no employees, this employee-driven query will not show it. Start from `departments` with a `LEFT JOIN` to include empty departments.

### 3. Customers Who Never Ordered

Use a left anti-join.

```sql
SELECT c.customer_id, c.customer_name
FROM customers c
LEFT JOIN orders o
    ON c.customer_id = o.customer_id
WHERE o.order_id IS NULL;
```

Explanation: The `LEFT JOIN` keeps every customer. `WHERE o.order_id IS NULL` keeps only customers without matching orders.

Alternate approach using `NOT EXISTS`:

```sql
SELECT c.customer_id, c.customer_name
FROM customers c
WHERE NOT EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.customer_id = c.customer_id
);
```

Which is better: `NOT EXISTS` is often clearer for anti-match logic and avoids mistakes when the joined table can create duplicates.

Concept: This is an anti-join problem: find rows in one table where matching rows do not exist in another table.

Performance note: Index `orders(customer_id)` so the anti-join can check each customer's order existence efficiently.

Follow-up: Ask whether customers with only cancelled orders count as "never ordered." If not, add an order-status filter inside the join or subquery.

### 4. Self Join: Employee-Manager

A self join joins a table to itself. One alias is the employee, the other is the manager.

```sql
SELECT e.employee_id,
       e.employee_name,
       m.employee_name AS manager_name
FROM employees e
LEFT JOIN employees m
    ON e.manager_id = m.employee_id;
```

Explanation: `LEFT JOIN` keeps employees who do not have a manager, such as CEO-level employees.

Concept: This is a parent-child relationship stored inside one table. The child row has `manager_id`, which points to another row's `employee_id`.

Alternate approach for full hierarchy: Use a recursive CTE when the interviewer asks for all reporting levels, not just direct manager.

```sql
WITH RECURSIVE employee_tree AS (
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
           et.level + 1
    FROM employees e
    JOIN employee_tree et
        ON e.manager_id = et.employee_id
)
SELECT *
FROM employee_tree
ORDER BY level, employee_id;
```

Performance note: Index `employees(manager_id)` for direct-report lookups.

Follow-up: A recursive hierarchy needs cycle protection in real systems if bad data can create loops.

### 5. Multiple Table Joins

Multiple joins work when each join condition is clear.

```sql
SELECT c.customer_name,
       o.order_id,
       o.order_amount,
       t.transaction_id,
       t.status
FROM customers c
JOIN orders o
    ON c.customer_id = o.customer_id
LEFT JOIN transactions t
    ON c.customer_id = t.customer_id
   AND t.amount = o.order_amount;
```

Explanation: Start from the business question, choose the base table, and then join only the tables needed to answer it.

Concept: This tests join planning and cardinality control across more than two tables. Every join should have a clear relationship and a clear reason to exist.

Alternate approach: Build complex joins step by step with CTEs when the logic becomes hard to read. For example, first identify customer orders, then join transaction details in the next CTE.

Performance note: Multi-table joins need indexes on join keys. Watch for accidental many-to-many joins, which can multiply rows and inflate sums.

Follow-up: If totals look too high after a join, check whether one order joined to multiple transactions or one customer joined to multiple child rows.

## Tier 1: Aggregations

### 6. Count Employees Per Department

```sql
SELECT d.department_id,
       d.department_name,
       COUNT(e.employee_id) AS employee_count
FROM departments d
LEFT JOIN employees e
    ON d.department_id = e.department_id
GROUP BY d.department_id, d.department_name;
```

Explanation: `COUNT(e.employee_id)` counts matching employees. `LEFT JOIN` includes departments with zero employees.

Concept: This combines outer join behavior with aggregation.

Why not `COUNT(*)`: With `LEFT JOIN`, `COUNT(*)` would count the department row even when there is no employee, giving `1` for empty departments. `COUNT(e.employee_id)` counts only matched employees.

Alternate approach if you only need departments that have employees:

```sql
SELECT department_id,
       COUNT(*) AS employee_count
FROM employees
GROUP BY department_id;
```

Performance note: Index `employees(department_id)` for department-based grouping and joining.

Follow-up: Ask whether departments with zero employees should appear. That choice determines `LEFT JOIN` vs grouping only `employees`.

### 7. Department-Wise Average Salary

```sql
SELECT d.department_id,
       d.department_name,
       AVG(e.salary) AS average_salary
FROM departments d
JOIN employees e
    ON d.department_id = e.department_id
GROUP BY d.department_id, d.department_name;
```

Explanation: Group by department, then calculate average salary inside each department.

Concept: This is grouped aggregation. `AVG` computes one result per group.

Correctness note: `AVG` ignores `NULL`, but `salary` is `NOT NULL` in this sample schema. If salaries can be null, clarify whether null means unknown or zero.

Alternate approach with a window function, if you need each employee row plus department average:

```sql
SELECT employee_id,
       employee_name,
       department_id,
       salary,
       AVG(salary) OVER (
           PARTITION BY department_id
       ) AS department_average_salary
FROM employees;
```

Follow-up: If asked for departments whose average salary is greater than X, use `HAVING`, not `WHERE`.

### 8. Highest Salary Per Department

```sql
SELECT department_id, employee_id, employee_name, salary
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

Explanation: `DENSE_RANK()` returns all employees tied for highest salary in a department.

Alternate approach using correlated subquery:

```sql
SELECT e.department_id, e.employee_id, e.employee_name, e.salary
FROM employees e
WHERE e.salary = (
    SELECT MAX(e2.salary)
    FROM employees e2
    WHERE e2.department_id = e.department_id
);
```

When to prefer window function: Use window functions when you also need ranking, top N, or multiple salary positions. Use a correlated subquery when the requirement is only "equals department max" and readability is more important.

### 9. Departments Having More Than N Employees

```sql
SELECT d.department_id,
       d.department_name,
       COUNT(e.employee_id) AS employee_count
FROM departments d
JOIN employees e
    ON d.department_id = e.department_id
GROUP BY d.department_id, d.department_name
HAVING COUNT(e.employee_id) > 5;
```

Explanation: `HAVING` filters aggregated department groups.

Concept: This tests aggregation plus post-aggregation filtering. The department count does not exist until after `GROUP BY`, so the filter must be in `HAVING`.

Execution-order reasoning: `WHERE` filters employee rows before grouping. `GROUP BY` creates department groups. `COUNT(e.employee_id)` is calculated per department. `HAVING COUNT(e.employee_id) > 5` keeps only departments above the threshold.

Alternate approach using a CTE:

```sql
WITH department_counts AS (
    SELECT department_id,
           COUNT(*) AS employee_count
    FROM employees
    GROUP BY department_id
)
SELECT d.department_id,
       d.department_name,
       dc.employee_count
FROM department_counts dc
JOIN departments d
    ON dc.department_id = d.department_id
WHERE dc.employee_count > 5;
```

Performance note: Index `employees(department_id)` helps grouping and joins by department. For very large organizations, department headcount may also be maintained in reporting tables.

Follow-up: If the interviewer asks for departments with zero employees, use `departments LEFT JOIN employees` and adjust the `HAVING` condition.

### 10. Revenue By Customer

```sql
SELECT c.customer_id,
       c.customer_name,
       SUM(o.order_amount) AS total_revenue
FROM customers c
JOIN orders o
    ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.customer_name
ORDER BY total_revenue DESC;
```

Explanation: Revenue is calculated with `SUM`. If your table has order status, filter for successful or completed orders.

Performance note: For large order tables, an index on `(customer_id)` helps joins, while an index on `(order_timestamp)` helps time-based revenue reports.

## Tier 1: GROUP BY and HAVING

### 11. Difference Between WHERE and HAVING

`WHERE` filters rows before grouping. `HAVING` filters groups after aggregation.

```sql
SELECT department_id, AVG(salary) AS average_salary
FROM employees
WHERE salary > 0
GROUP BY department_id
HAVING AVG(salary) > 80000;
```

Explanation: `salary > 0` is row-level filtering. `AVG(salary) > 80000` is group-level filtering.

### 12. Departments With Average Salary Greater Than X

```sql
SELECT department_id,
       AVG(salary) AS average_salary
FROM employees
GROUP BY department_id
HAVING AVG(salary) > 100000;
```

Explanation: Since average salary is calculated after grouping, the filter belongs in `HAVING`.

Concept: This tests logical query order. `WHERE` happens before `GROUP BY`; `HAVING` happens after aggregate values are calculated.

Alternate approach using a CTE:

```sql
WITH department_average AS (
    SELECT department_id,
           AVG(salary) AS average_salary
    FROM employees
    GROUP BY department_id
)
SELECT *
FROM department_average
WHERE average_salary > 100000;
```

Why this can be useful: The CTE makes the aggregate result explicit and easier to reuse.

Performance note: Filter invalid or irrelevant rows in `WHERE` before grouping whenever possible.

### 13. Products Sold More Than N Times

```sql
SELECT product_id,
       COUNT(*) AS sold_count
FROM orders
GROUP BY product_id
HAVING COUNT(*) > 10;
```

Explanation: Count rows per product and filter products whose count is above the threshold.

Concept: This is frequency analysis: count events per entity and keep entities above a threshold.

Alternate approach to include product details:

```sql
SELECT p.product_id,
       p.product_name,
       COUNT(o.order_id) AS sold_count
FROM products p
JOIN orders o
    ON p.product_id = o.product_id
GROUP BY p.product_id, p.product_name
HAVING COUNT(o.order_id) > 10;
```

Performance note: Index `orders(product_id)` for product-level sales counts.

Follow-up: Clarify whether cancelled/refunded orders count as sold.

## Tier 1: Duplicates

### 14. Find Duplicate Emails

```sql
SELECT email,
       COUNT(*) AS duplicate_count
FROM customers
WHERE email IS NOT NULL
GROUP BY email
HAVING COUNT(*) > 1;
```

Explanation: Group all customers by email and keep only emails that appear more than once.

### 15. Find Duplicate Records

```sql
SELECT customer_name,
       email,
       COUNT(*) AS duplicate_count
FROM customers
GROUP BY customer_name, email
HAVING COUNT(*) > 1;
```

Explanation: Define what "duplicate" means. Here it means same name and same email.

Concept: Duplicate detection depends on business keys. A technical primary key may be unique while business fields are duplicated.

Alternate approach using window count to show duplicate rows:

```sql
SELECT customer_id,
       customer_name,
       email
FROM (
    SELECT c.*,
           COUNT(*) OVER (
               PARTITION BY customer_name, email
           ) AS duplicate_count
    FROM customers c
) x
WHERE duplicate_count > 1;
```

Correctness note: Treating two rows as duplicates by name and email can be wrong if family members share an email or names are not normalized.

Follow-up: Ask what columns define a true duplicate.

### 16. Delete Duplicate Rows

```sql
DELETE FROM customers
WHERE customer_id IN (
    SELECT customer_id
    FROM (
        SELECT customer_id,
               ROW_NUMBER() OVER (
                   PARTITION BY email
                   ORDER BY customer_id
               ) AS row_num
        FROM customers
        WHERE email IS NOT NULL
    ) x
    WHERE row_num > 1
);
```

Explanation: `ROW_NUMBER()` keeps one row per email and marks the rest for deletion.

Safety note: In production, preview rows before deleting.

```sql
SELECT *
FROM (
    SELECT customer_id,
           email,
           ROW_NUMBER() OVER (
               PARTITION BY email
               ORDER BY customer_id
           ) AS row_num
    FROM customers
) x
WHERE row_num > 1;
```

Interview follow-up: Ask which row should be kept. Oldest? newest? active customer? The `ORDER BY` inside `ROW_NUMBER()` defines the keeper.

### 17. Count Duplicate Occurrences

```sql
SELECT email,
       COUNT(*) AS occurrence_count
FROM customers
GROUP BY email
HAVING COUNT(*) > 1
ORDER BY occurrence_count DESC;
```

Explanation: This shows how many times each duplicate value appears.

Concept: This is grouped frequency counting.

Alternate approach: Use `COUNT(*) OVER (PARTITION BY email)` when you want each duplicate row plus its occurrence count.

Performance note: Index `customers(email)` helps duplicate detection and can later be replaced by a unique index if the business rule requires uniqueness.

Follow-up: Should `NULL` emails be grouped as duplicates? In SQL, `GROUP BY` groups nulls together, but business logic may want to ignore null emails.

## Tier 1: Ranking

### 18. Second Highest Salary

```sql
SELECT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
    FROM employees
) ranked
WHERE salary_rank = 2;
```

Explanation: `DENSE_RANK()` finds the second distinct salary.

Why not `LIMIT 1 OFFSET 1` directly on employees: If two employees share the highest salary, offset-based logic may return another highest-salary row instead of the second distinct salary.

Alternate approach using distinct salaries:

```sql
SELECT salary
FROM (
    SELECT DISTINCT salary
    FROM employees
    ORDER BY salary DESC
    LIMIT 1 OFFSET 1
) x;
```

Use this when you only need the salary value. Use `DENSE_RANK()` when you need employee details too.

### 19. Third Highest Salary

```sql
SELECT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
    FROM employees
) ranked
WHERE salary_rank = 3;
```

Explanation: Same pattern as second highest salary. Change the rank number.

Follow-up: If the interviewer asks for the employee name as well, rank the employee rows, not only the salary values.

### 20. Nth Highest Salary

```sql
SELECT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
    FROM employees
) ranked
WHERE salary_rank = 5;
```

Explanation: Replace `5` with N. Use `DENSE_RANK()` when duplicates should share the same rank.

Performance note: Ranking requires sorting salaries. On very large employee tables, an index on `salary` may help simple top salary lookups, but ranking many rows still requires ordered processing.

### 21. Top 3 Salaries Per Department

```sql
SELECT department_id, employee_id, employee_name, salary
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

Explanation: Ranking restarts for each department because of `PARTITION BY department_id`.

Alternative with `ROW_NUMBER()`:

```sql
SELECT department_id, employee_id, employee_name, salary
FROM (
    SELECT e.*,
           ROW_NUMBER() OVER (
               PARTITION BY department_id
               ORDER BY salary DESC, employee_id
           ) AS row_num
    FROM employees e
) ranked
WHERE row_num <= 3;
```

Difference: `ROW_NUMBER()` returns at most 3 rows per department. `DENSE_RANK()` may return more than 3 rows if there are salary ties.

## Tier 2: Window Functions

### 22. Remove Duplicates Using ROW_NUMBER

`ROW_NUMBER()` is best when you want to keep exactly one row from each duplicate group.

```sql
DELETE FROM customers
WHERE customer_id IN (
    SELECT customer_id
    FROM (
        SELECT customer_id,
               ROW_NUMBER() OVER (
                   PARTITION BY email
                   ORDER BY customer_id
               ) AS row_num
        FROM customers
    ) x
    WHERE row_num > 1
);
```

Concept: `PARTITION BY email` creates one numbering sequence per email. `ORDER BY customer_id` decides which duplicate survives.

Production note: Add a unique constraint after cleanup if duplicate emails should never happen again.

### 23. Latest Order Per Customer

```sql
SELECT customer_id, order_id, order_amount, order_timestamp
FROM (
    SELECT o.*,
           ROW_NUMBER() OVER (
               PARTITION BY customer_id
               ORDER BY order_timestamp DESC, order_id DESC
           ) AS row_num
    FROM orders o
) ranked
WHERE row_num = 1;
```

Explanation: Sort newest order first inside each customer and return row 1.

Alternative using `DISTINCT ON`, which is PostgreSQL-specific:

```sql
SELECT DISTINCT ON (customer_id)
       customer_id,
       order_id,
       order_amount,
       order_timestamp
FROM orders
ORDER BY customer_id, order_timestamp DESC, order_id DESC;
```

Interview note: `ROW_NUMBER()` is portable across many databases. `DISTINCT ON` is concise but PostgreSQL-specific.

### 24. Rank Employees By Salary

```sql
SELECT employee_id,
       employee_name,
       salary,
       RANK() OVER (ORDER BY salary DESC) AS salary_rank
FROM employees;
```

Explanation: `RANK()` gives ties the same rank and skips the next rank.

Follow-up: If you do not want skipped ranks, use `DENSE_RANK()`. If you need unique row numbers, use `ROW_NUMBER()`.

### 25. Top N Per Department

```sql
SELECT department_id, employee_id, employee_name, salary
FROM (
    SELECT e.*,
           ROW_NUMBER() OVER (
               PARTITION BY department_id
               ORDER BY salary DESC, employee_id
           ) AS row_num
    FROM employees e
) ranked
WHERE row_num <= 3;
```

Explanation: `ROW_NUMBER()` returns exactly N rows per department. Use `DENSE_RANK()` if salary ties should all be included.

Interviewer trap: Ask whether "top 3 salaries" means top 3 employees or top 3 distinct salary levels. Those produce different results when ties exist.

### 26. Nth Highest Salary Using DENSE_RANK

```sql
SELECT employee_id, employee_name, salary
FROM (
    SELECT e.*,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS salary_rank
    FROM employees e
) ranked
WHERE salary_rank = 4;
```

Explanation: Replace `4` with the required N.

### 27. Department Salary Ranking

```sql
SELECT department_id,
       employee_id,
       employee_name,
       salary,
       DENSE_RANK() OVER (
           PARTITION BY department_id
           ORDER BY salary DESC
       ) AS department_salary_rank
FROM employees;
```

Explanation: This ranks employees within their own department.

Concept: `DENSE_RANK()` with `PARTITION BY department_id` creates independent salary rankings per department.

Alternate approach: Use `RANK()` if skipped ranks should show how many people tied above. Use `ROW_NUMBER()` if each employee must have a unique position.

Performance note: Index `employees(department_id, salary DESC)` supports department-wise ordered processing.

Follow-up: Ask whether ties should share a rank. That choice decides `RANK`, `DENSE_RANK`, or `ROW_NUMBER`.

### 28. Previous Transaction

```sql
SELECT account_id,
       transaction_id,
       amount,
       LAG(amount) OVER (
           PARTITION BY account_id
           ORDER BY transaction_timestamp, transaction_id
       ) AS previous_amount
FROM transactions;
```

Explanation: `LAG()` reads the previous row in the ordered account history.

Use case: Compare current transaction amount with the previous transaction amount to detect sudden changes.

Performance note: Index `(account_id, transaction_timestamp)` helps PostgreSQL process account-wise ordering.

### 29. Next Transaction

```sql
SELECT account_id,
       transaction_id,
       LEAD(transaction_timestamp) OVER (
           PARTITION BY account_id
           ORDER BY transaction_timestamp, transaction_id
       ) AS next_transaction_timestamp
FROM transactions;
```

Explanation: `LEAD()` reads the next row in the ordered account history.

Use case: Find time gaps between current and next transaction.

```sql
SELECT account_id,
       transaction_id,
       LEAD(transaction_timestamp) OVER (
           PARTITION BY account_id
           ORDER BY transaction_timestamp, transaction_id
       ) - transaction_timestamp AS time_until_next_transaction
FROM transactions;
```

Correctness note: Always include a deterministic tie-breaker such as `transaction_id` when timestamps can tie.

Follow-up: To find accounts with suspiciously fast repeated transactions, calculate the gap and filter where it is below a threshold.

### 30. Day-Over-Day Growth

```sql
WITH daily_revenue AS (
    SELECT CAST(order_timestamp AS DATE) AS order_date,
           SUM(order_amount) AS revenue
    FROM orders
    GROUP BY CAST(order_timestamp AS DATE)
)
SELECT order_date,
       revenue,
       revenue - LAG(revenue) OVER (ORDER BY order_date) AS revenue_growth
FROM daily_revenue
ORDER BY order_date;
```

Explanation: First aggregate by day, then compare each day with the previous day.

Why two steps: If you apply `LAG()` directly on raw orders, you compare order rows, not daily totals. First create daily totals, then compare days.

### 31. Consecutive Login Problem

```sql
WITH login_days AS (
    SELECT DISTINCT user_id,
           CAST(login_timestamp AS DATE) AS login_date
    FROM logins
),
ranked_days AS (
    SELECT user_id,
           login_date,
           login_date - (ROW_NUMBER() OVER (
               PARTITION BY user_id
               ORDER BY login_date
           )::INT) AS group_key
    FROM login_days
)
SELECT user_id,
       MIN(login_date) AS streak_start,
       MAX(login_date) AS streak_end,
       COUNT(*) AS consecutive_days
FROM ranked_days
GROUP BY user_id, group_key
HAVING COUNT(*) >= 3;
```

Explanation: For consecutive dates, `date - row_number` stays constant across a streak.

Alternative simpler approach for exactly three consecutive days:

```sql
WITH login_days AS (
    SELECT DISTINCT user_id,
           CAST(login_timestamp AS DATE) AS login_date
    FROM logins
)
SELECT DISTINCT d1.user_id
FROM login_days d1
JOIN login_days d2
    ON d1.user_id = d2.user_id
   AND d2.login_date = d1.login_date + INTERVAL '1 day'
JOIN login_days d3
    ON d1.user_id = d3.user_id
   AND d3.login_date = d1.login_date + INTERVAL '2 days';
```

Use the self-join approach for exactly 3 days. Use the `date - row_number` grouping approach for streaks of N days.

## Tier 3: Subqueries

### 32. Employees Earning More Than Manager

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

Explanation: This is a self join because manager rows are also employee rows.

Alternate approach using correlated subquery:

```sql
SELECT e.employee_id, e.employee_name, e.salary
FROM employees e
WHERE e.salary > (
    SELECT m.salary
    FROM employees m
    WHERE m.employee_id = e.manager_id
);
```

Join version is usually clearer because it can show both employee and manager details.

### 33. Customers Spending Above Average

```sql
WITH customer_spend AS (
    SELECT customer_id,
           SUM(order_amount) AS total_spend
    FROM orders
    GROUP BY customer_id
)
SELECT customer_id, total_spend
FROM customer_spend
WHERE total_spend > (
    SELECT AVG(total_spend)
    FROM customer_spend
);
```

Explanation: Calculate spend per customer, then compare each customer to average spend.

### 34. Products Above Average Price

Assuming `products(product_id, product_name, price)`:

```sql
SELECT product_id, product_name, price
FROM products
WHERE price > (
    SELECT AVG(price)
    FROM products
);
```

Explanation: The subquery returns one scalar value: average product price.

### 35. EXISTS vs IN

`IN` compares a value to a list. `EXISTS` checks whether the subquery returns at least one matching row.

```sql
SELECT c.customer_id, c.customer_name
FROM customers c
WHERE EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.customer_id = c.customer_id
);
```

Explanation: `EXISTS` is usually clearer for relationship checks.

Important `NULL` note: `NOT IN` can behave unexpectedly if the subquery returns `NULL`. `NOT EXISTS` is generally safer for anti-joins.

### 36. Correlated Subquery

```sql
SELECT e.employee_id, e.employee_name, e.salary
FROM employees e
WHERE e.salary > (
    SELECT AVG(e2.salary)
    FROM employees e2
    WHERE e2.department_id = e.department_id
);
```

Explanation: The inner query depends on each outer employee's department.

Alternative using window function:

```sql
SELECT employee_id, employee_name, salary
FROM (
    SELECT e.*,
           AVG(salary) OVER (
               PARTITION BY department_id
           ) AS department_avg_salary
    FROM employees e
) x
WHERE salary > department_avg_salary;
```

Window version can be clearer when you need both row details and group-level values.

### 37. Highest Salary In Each Department

```sql
SELECT e.department_id, e.employee_id, e.employee_name, e.salary
FROM employees e
WHERE e.salary = (
    SELECT MAX(e2.salary)
    FROM employees e2
    WHERE e2.department_id = e.department_id
);
```

Explanation: This returns all employees tied for the highest salary in their department.

Alternative using `DENSE_RANK()` is usually better when the interviewer later asks for second highest, third highest, or top N per department.

## Tier 4: Banking-Style Questions

### 38. Find Suspicious Transactions

```sql
SELECT customer_id,
       COUNT(*) AS high_value_count,
       SUM(amount) AS high_value_total
FROM transactions
WHERE amount >= 10000
  AND transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '1 day'
GROUP BY customer_id
HAVING COUNT(*) >= 3;
```

Explanation: This flags customers with repeated high-value activity in a short period.

Concept: Fraud detection queries usually combine amount thresholds, time windows, counts, location/device signals, and customer history. This example uses amount plus frequency.

Follow-up: A real system should not hard-code only one rule. Rules may come from configuration tables, ML scores, or fraud event streams.

Performance note: For recent suspicious activity, indexes on `(transaction_timestamp)` and possibly `(customer_id, transaction_timestamp)` help.

### 39. Daily Transaction Totals

```sql
SELECT CAST(transaction_timestamp AS DATE) AS transaction_date,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
GROUP BY CAST(transaction_timestamp AS DATE)
ORDER BY transaction_date;
```

Explanation: This is a common operational reporting query.

Alternative with `DATE_TRUNC`:

```sql
SELECT DATE_TRUNC('day', transaction_timestamp) AS transaction_day,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM transactions
GROUP BY DATE_TRUNC('day', transaction_timestamp)
ORDER BY transaction_day;
```

Performance note: On very large transaction tables, daily reporting is often served from summary tables or materialized views instead of scanning raw transactions every time.

### 40. Running Account Balance

```sql
SELECT account_id,
       transaction_id,
       transaction_timestamp,
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

Explanation: Windowed `SUM` calculates balance movement without collapsing rows.

Concept: `GROUP BY` would give one row per account, but running balance needs every transaction row plus cumulative state. That is why a window function is the right tool.

Correctness note: Always order by a deterministic sequence. If two transactions have the same timestamp, add `transaction_id` as a tie-breaker.

Follow-up: In real banking systems, balances should come from immutable ledger entries or a controlled balance projection, not ad hoc recalculation from mutable rows.

### 41. Detect Duplicate Payments

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

Explanation: Same customer, merchant, amount, and day may indicate a duplicate payment.

Alternative: For tighter duplicate detection, use a smaller time window instead of whole day.

```sql
SELECT t1.transaction_id AS transaction_1,
       t2.transaction_id AS transaction_2,
       t1.customer_id,
       t1.amount
FROM transactions t1
JOIN transactions t2
   ON t1.customer_id = t2.customer_id
   AND t1.merchant_id = t2.merchant_id
   AND t1.amount = t2.amount
   AND t2.transaction_timestamp >= t1.transaction_timestamp
   AND t2.transaction_timestamp <= t1.transaction_timestamp + INTERVAL '5 minutes'
   AND (
       t2.transaction_timestamp > t1.transaction_timestamp
       OR (
           t2.transaction_timestamp = t1.transaction_timestamp
           AND t2.transaction_id > t1.transaction_id
       )
   );
```

Interview follow-up: Ask what defines a duplicate: same amount, same merchant, same external payment reference, same idempotency key, or same time window.

Performance note: For duplicate payment checks, useful indexes depend on the rule. For the daily grouping version, consider `(customer_id, merchant_id, amount, transaction_timestamp)`. For production payment systems, an idempotency key or external payment reference is usually safer than fuzzy matching.

### 42. Largest Transaction Per Customer

```sql
SELECT customer_id, transaction_id, amount
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

Explanation: Ranking lets you return the full row for each customer's largest transaction.

Alternative with correlated subquery:

```sql
SELECT t.customer_id, t.transaction_id, t.amount
FROM transactions t
WHERE t.amount = (
    SELECT MAX(t2.amount)
    FROM transactions t2
    WHERE t2.customer_id = t.customer_id
);
```

Difference: Correlated subquery returns ties. `ROW_NUMBER()` returns one row unless you use `RANK()` or `DENSE_RANK()`.

Performance note: Index `(customer_id, amount DESC)` helps largest-transaction-per-customer queries. If ties matter, use `RANK()` instead of `ROW_NUMBER()`.

Follow-up: Clarify whether "largest" means absolute amount, debit only, successful only, or all transaction statuses.

### 43. Customers With No Transactions In Last 90 Days

```sql
SELECT c.customer_id, c.customer_name
FROM customers c
WHERE NOT EXISTS (
    SELECT 1
    FROM transactions t
    WHERE t.customer_id = c.customer_id
      AND t.transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '90 days'
);
```

Explanation: `NOT EXISTS` expresses "no matching recent transaction" clearly.

Alternative using aggregation:

```sql
SELECT c.customer_id, c.customer_name
FROM customers c
LEFT JOIN transactions t
    ON c.customer_id = t.customer_id
GROUP BY c.customer_id, c.customer_name
HAVING MAX(t.transaction_timestamp) < CURRENT_TIMESTAMP - INTERVAL '90 days'
    OR MAX(t.transaction_timestamp) IS NULL;
```

Use `NOT EXISTS` when the question is purely about absence in a time window. It is usually easier to read.

Performance note: Index `transactions(customer_id, transaction_timestamp)` helps the `NOT EXISTS` lookup find recent activity quickly.

### 44. Monthly Spending Trends

```sql
SELECT customer_id,
       DATE_TRUNC('month', transaction_timestamp) AS spend_month,
       SUM(amount) AS total_spend
FROM transactions
WHERE transaction_type = 'DEBIT'
GROUP BY customer_id, DATE_TRUNC('month', transaction_timestamp)
ORDER BY customer_id, spend_month;
```

Explanation: `DATE_TRUNC('month', ...)` creates monthly buckets in PostgreSQL.

Follow-up: If the interviewer asks for month-over-month growth, add `LAG(total_spend)` after creating monthly totals.

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
       total_spend - LAG(total_spend) OVER (
           PARTITION BY customer_id
           ORDER BY spend_month
       ) AS month_over_month_change
FROM monthly_spend;
```

Performance note: Monthly trend queries over raw transaction history can be expensive. For dashboards, use monthly summary tables or materialized views.

### 45. Top Spending Customers

```sql
SELECT c.customer_id,
       c.customer_name,
       SUM(t.amount) AS total_spend
FROM customers c
JOIN transactions t
    ON c.customer_id = t.customer_id
WHERE t.transaction_type = 'DEBIT'
GROUP BY c.customer_id, c.customer_name
ORDER BY total_spend DESC
LIMIT 10;
```

Explanation: Aggregate debit amounts and sort descending.

Performance note: If this query runs often for dashboards, maintain a monthly spend summary table. Sorting all customers by spend can be expensive at scale.

### 46. Consecutive Failed Transactions

```sql
SELECT customer_id, transaction_id, transaction_timestamp
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

Explanation: This identifies the third row in a sequence of three failed transactions.

Why `LAG` works: The current row is failed, and the two previous rows for the same customer are also failed. That means the current row completes a three-failure sequence.

Follow-up: If successful transactions should reset the failure streak, this works because the immediately previous statuses must both be `FAILED`.

### 47. Fraud Pattern Detection Query

```sql
WITH recent_failed_logins AS (
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
       r.failed_login_count
FROM transactions t
JOIN recent_failed_logins r
    ON t.customer_id = r.user_id
WHERE t.amount >= 10000
  AND t.transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '1 hour'
  AND r.failed_login_count >= 3;
```

Explanation: Fraud queries usually combine multiple weak signals into one stronger signal.

Correctness note: This query assumes `customer_id` and `user_id` are comparable. In a real schema, there may be a separate users/customers mapping table.

Performance note: Time-window fraud queries need strong indexing on event timestamp columns, such as `logins(login_timestamp, status)` and `transactions(transaction_timestamp, amount)`.

### 48. Transaction Rollback Scenarios

Rollback is needed when a multi-step transaction fails before completion.

```sql
BEGIN;

UPDATE accounts
SET balance = balance - 100.00
WHERE account_id = 101;

UPDATE accounts
SET balance = balance + 100.00
WHERE account_id = 202;

ROLLBACK;
```

Explanation: In a failed transfer, rollback prevents one-sided money movement.

Correct transfer pattern:

```sql
BEGIN;

-- Step 1: lock both rows in deterministic order.
SELECT account_id, balance
FROM accounts
WHERE account_id IN (101, 202)
ORDER BY account_id
FOR UPDATE;

-- Application/procedure validation before updates:
-- 1. Exactly two rows must be returned.
-- 2. Source account 101 must have balance >= 100.00.
-- 3. If either check fails, run ROLLBACK.

-- Step 2: debit source account.
UPDATE accounts
SET balance = balance - 100.00
WHERE account_id = 101
  AND balance >= 100.00;

-- Application/procedure must verify debit row count = 1.
-- If not, run ROLLBACK.

-- Step 3: credit destination account.
UPDATE accounts
SET balance = balance + 100.00
WHERE account_id = 202;

-- Application/procedure must verify credit row count = 1.
-- If debit row count = 1 and credit row count = 1, run COMMIT.
-- Otherwise, run ROLLBACK.

-- COMMIT;
-- ROLLBACK;
```

Interview note: The debit and credit should be atomic. If either account is missing, if the source balance is insufficient, or if either update affects zero rows, rollback. Also lock accounts in a consistent order to reduce deadlocks.

Why this version is safer: It separates the banking control flow into explicit steps: lock, validate, debit, validate, credit, validate, then commit. That is easier to explain in an interview than a clever one-statement CTE with side effects.

### 49. ACID Properties

Atomicity: all changes happen or none happen.

Consistency: constraints and business rules remain valid.

Isolation: concurrent transactions do not corrupt each other.

Durability: committed data survives crashes.

Banking example: A transfer debit and credit must commit together and survive failure after commit.

Follow-up examples:

- Atomicity prevents debit without credit.
- Consistency enforces valid balances and constraints.
- Isolation prevents two transfers from corrupting the same account balance.
- Durability ensures committed transfers remain after crash recovery.

### 50. Isolation Levels and Locking

PostgreSQL supports `READ COMMITTED`, `REPEATABLE READ`, and `SERIALIZABLE`. It accepts `READ UNCOMMITTED`, but treats it like `READ COMMITTED`.

```sql
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;

SELECT balance
FROM accounts
WHERE account_id = 101
FOR UPDATE;

COMMIT;
```

Explanation: `FOR UPDATE` locks selected rows so other transactions cannot update them until the current transaction finishes.

Performance and safety tradeoff: Higher isolation and explicit locks improve correctness but reduce concurrency. For fund transfers, correctness usually matters more than maximum concurrency.

Deadlock follow-up: Lock accounts in a consistent order, such as smaller `account_id` first.

## Tier 5: Performance Questions

### 51. Clustered vs Non-Clustered Index

PostgreSQL does not maintain a SQL Server-style permanent clustered index. PostgreSQL indexes are separate lookup structures. The `CLUSTER` command can physically reorder a table once, but later writes do not keep that order automatically.

Interview answer: In PostgreSQL, focus on B-tree indexes, composite indexes, partial indexes, covering indexes with `INCLUDE`, and execution plans.

Banking example: For account history, you might create an index on `(account_id, transaction_timestamp)` so the database can quickly fetch one account's transactions in time order. You would not rely on the table being physically ordered forever.

When not to overdo it: Do not create indexes on every column. A high-volume transaction table receives many inserts, and every extra index adds write overhead.

### 52. Why Query Is Slow

Common causes include missing indexes, wrong index order, full scans on large tables, expensive joins, expensive sorts, stale statistics, and returning too many rows.

```sql
EXPLAIN ANALYZE
SELECT transaction_id, amount
FROM transactions
WHERE account_id = 101
ORDER BY transaction_timestamp DESC
LIMIT 20;
```

Explanation: Use `EXPLAIN ANALYZE` to inspect the real execution plan and runtime.

What to check in the plan:

- Is PostgreSQL doing a `Seq Scan` on a huge table?
- Is it sorting millions of rows?
- Are estimated rows very different from actual rows?
- Is the query using the index you expected?
- Is the query reading far more rows than it returns?

Fix approach:

1. Add selective filters.
2. Return fewer columns.
3. Add an index matching the filter and sort pattern.
4. Rewrite joins or subqueries if the plan is poor.
5. Update statistics with `ANALYZE` if estimates are stale.

### 53. How To Optimize GROUP BY

Filter early, group fewer rows, index filter columns, partition very large tables, and consider materialized views for repeated reports.

```sql
SELECT account_id,
       SUM(amount) AS total_amount
FROM transactions
WHERE transaction_timestamp >= DATE_TRUNC('month', CURRENT_DATE)
GROUP BY account_id;
```

Helpful index:

```sql
CREATE INDEX idx_transactions_time_account
ON transactions(transaction_timestamp, account_id);
```

Banking example: Monthly debit totals across a large transaction table can be expensive. Filter by month first, then group. If the same report is used repeatedly, pre-aggregate into a monthly summary table or materialized view.

When not to optimize with an index alone: If a report must aggregate most of the table, an index may not help much. PostgreSQL may still prefer a sequential scan because reading most rows through an index can be slower.

### 54. Explain Query Execution Plan

A query execution plan shows how PostgreSQL executes SQL. Important nodes include `Seq Scan`, `Index Scan`, `Index Only Scan`, `Nested Loop`, `Hash Join`, and `Sort`.

Interview answer: Plans help you identify whether the database is scanning too much data, sorting too much data, or joining inefficiently.

Important plan concepts:

- Cost is PostgreSQL's estimated effort, not exact time.
- Actual time appears only with `EXPLAIN ANALYZE`.
- Estimated rows vs actual rows tells you whether the optimizer understood the data distribution.
- A `Seq Scan` is not always bad. It can be correct if the query reads most of the table.
- An `Index Only Scan` is useful when PostgreSQL can answer from the index and visibility map.

Strong senior answer: "I would compare estimated and actual row counts, check join strategy, look for large sorts or scans, then change the query or index based on the observed bottleneck."

### 55. Composite Index

```sql
CREATE INDEX idx_transactions_account_time
ON transactions(account_id, transaction_timestamp);
```

Explanation: Column order matters. This index is useful for queries filtering by account and then by time.

Why order matters: PostgreSQL can efficiently use the leftmost prefix of a composite index. An index on `(account_id, transaction_timestamp)` helps queries filtering by account, and account plus time. It is much less useful for a query that filters only by timestamp.

Good:

```sql
SELECT *
FROM transactions
WHERE account_id = 101
  AND transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '30 days';
```

Less useful for that index:

```sql
SELECT *
FROM transactions
WHERE transaction_timestamp >= CURRENT_TIMESTAMP - INTERVAL '30 days';
```

Interview rule: equality columns first, then range/sort columns, based on real query patterns.

### 56. Covering Index

```sql
CREATE INDEX idx_transactions_account_time_cover
ON transactions(account_id, transaction_timestamp)
INCLUDE (transaction_id, amount, status);
```

Explanation: `INCLUDE` columns are stored in the index for read efficiency, but they are not part of the index search key.

When to use: Use covering indexes for frequent read queries that return a few columns repeatedly, such as recent account transactions.

Tradeoff: Included columns make the index larger. Larger indexes use more memory and storage and slow down writes.

### 57. Partitioning

Partitioning splits a large table into smaller physical parts.

```sql
CREATE TABLE transactions_partitioned (
    transaction_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    transaction_timestamp TIMESTAMP NOT NULL
) PARTITION BY RANGE (transaction_timestamp);
```

Explanation: Time-based partitions are useful for transaction history because many queries filter by date.

Banking example: A transaction table can be partitioned monthly. Queries for June 2026 can scan only the June 2026 partition instead of years of history.

Benefits:

- Faster date-range queries through partition pruning
- Easier archival and retention
- Easier maintenance on old partitions

Tradeoffs:

- More complex schema management
- Poor partition key choice can hurt performance
- Too many partitions can create planning overhead

### 58. Sharding

Sharding splits data across multiple database servers. A good sharding key distributes data evenly, matches query patterns, avoids hot shards, and minimizes cross-shard transactions.

Banking example: `customer_id` or `account_id` may be good if most queries are customer/account scoped.

Replication vs sharding: Replication copies the same data to other nodes. Sharding splits different data across nodes.

Hot shard problem: If one customer, merchant, or region receives too much traffic, one shard can become overloaded while others are idle.

Tradeoff: Sharding improves scale but makes cross-shard joins, transfers, reporting, and rebalancing harder.

### 59. Materialized View

```sql
CREATE MATERIALIZED VIEW monthly_customer_spend AS
SELECT customer_id,
       DATE_TRUNC('month', transaction_timestamp) AS spend_month,
       SUM(amount) AS total_spend
FROM transactions
WHERE transaction_type = 'DEBIT'
GROUP BY customer_id, DATE_TRUNC('month', transaction_timestamp);
```

```sql
REFRESH MATERIALIZED VIEW monthly_customer_spend;
```

Explanation: Materialized views store results physically and are useful when reports are expensive but can tolerate refresh delay.

Banking example: A dashboard showing monthly customer spend or merchant transaction volume can use a materialized view if it does not need second-by-second freshness.

When not to use: Do not use a materialized view for real-time account balance correctness. Balances and ledger state should come from the source of truth or a carefully maintained ledger projection.

### 60. CTE vs Temp Table

Use a CTE for readable one-time logic:

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

Use a temp table when the result is reused or needs indexes:

```sql
CREATE TEMP TABLE high_value_transactions AS
SELECT *
FROM transactions
WHERE amount >= 10000;
```

Detailed comparison:

- CTE improves readability and keeps logic in one statement.
- Temp table stores intermediate data for the session.
- Temp table can be indexed.
- Temp table is useful when the same intermediate result is reused several times.
- CTE is usually better for simple one-time transformations.

Banking example: For a fraud investigation workflow, you may load suspicious transactions into a temp table, index it, and run several checks against it.

## Tier 6: Oracle/PLSQL Questions

This section gives detailed revision answers. The full basic-to-advanced teaching guide is here: [Oracle and PL/SQL Deep Dive](04-oracle-plsql-awareness.md).

### 61. `%TYPE`

`%TYPE` declares a variable with the same datatype as a table column.

```sql
v_salary employees.salary%TYPE;
```

Detailed answer: Use `%TYPE` when PL/SQL variables should stay aligned with table columns. It reduces bugs when a schema changes. For example, if `accounts.balance` changes precision, `v_balance accounts.balance%TYPE` follows automatically.

Tradeoff: `%TYPE` improves maintainability, but it can hide the exact datatype from a quick code reader. In enterprise code, maintainability usually wins.

Follow-up: Use `%TYPE` for single column values like salary, balance, status, and account id.

### 62. `%ROWTYPE`

`%ROWTYPE` declares a variable that can hold a full row from a table or cursor.

```sql
v_employee employees%ROWTYPE;
```

Detailed answer: Use `%ROWTYPE` when you need to fetch a full row or many columns. It keeps the record structure aligned with the table. Avoid it when you need only one or two columns because explicit variables are clearer.

Example:

```sql
DECLARE
    r_account accounts%ROWTYPE;
BEGIN
    SELECT *
    INTO r_account
    FROM accounts
    WHERE account_id = 101;
END;
/
```

Follow-up: `%ROWTYPE` is convenient, but `SELECT *` can be brittle if you only need a few fields. For APIs, explicit column lists are often clearer.

### 63. `BULK COLLECT`

`BULK COLLECT` fetches multiple rows into a collection in one operation, reducing context switches between SQL and PL/SQL engines.

Detailed answer: Row-by-row fetching is slow for large data sets. `BULK COLLECT` fetches batches. In production, use `LIMIT` with explicit cursors to avoid loading too many rows into memory.

Example pattern:

```sql
FETCH cur_accounts
BULK COLLECT INTO v_account_ids
LIMIT 1000;
```

Performance note: `BULK COLLECT` reduces context switches, but fetching too much at once can consume too much PGA memory.

Follow-up: Use it for batch jobs and data processing, not for tiny lookups.

### 64. `FORALL`

`FORALL` sends many DML operations to SQL efficiently. It is used with collections for bulk insert, update, or delete.

Detailed answer: `FORALL` is not a normal loop. It tells Oracle to send a batch of DML operations efficiently. It is commonly paired with `BULK COLLECT` in batch processing jobs.

Example:

```sql
FORALL i IN 1..v_account_ids.COUNT
    UPDATE accounts
    SET status = 'REVIEW'
    WHERE account_id = v_account_ids(i);
```

Performance note: `FORALL` is faster than row-by-row updates because it reduces PL/SQL-to-SQL context switching.

Follow-up: For sparse collections, know `INDICES OF` and `VALUES OF`.

### 65. Cursor vs Explicit Cursor

A cursor is a handle to a query result. Oracle manages implicit cursors automatically. Explicit cursors are declared and controlled by the developer.

Detailed answer: Use implicit cursors for simple SQL. Use explicit cursors when you need to fetch rows in batches, control open/fetch/close, or process a result set step by step. Cursor FOR loops are simpler because Oracle opens, fetches, and closes automatically.

Cursor FOR loop example:

```sql
BEGIN
    FOR r_account IN (
        SELECT account_id, balance
        FROM accounts
        WHERE status = 'A'
    ) LOOP
        DBMS_OUTPUT.PUT_LINE(r_account.account_id);
    END LOOP;
END;
/
```

Follow-up: Prefer set-based SQL when possible. Use cursors when procedural per-row logic is truly needed.

### 66. `NO_DATA_FOUND`

Raised when `SELECT INTO` returns no rows.

Detailed answer: Handle this when a lookup may not exist. In banking code, this can happen when an account ID or customer ID is invalid. Do not hide the error silently; return a meaningful application error.

Example:

```sql
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20001, 'Account not found');
```

Follow-up: `NO_DATA_FOUND` commonly comes from `SELECT INTO`, not from a cursor loop.

### 67. `TOO_MANY_ROWS`

Raised when `SELECT INTO` returns more than one row.

Detailed answer: This usually means your query is not selective enough. Fix the `WHERE` clause, add a uniqueness constraint if the data should be unique, or use a cursor if multiple rows are valid.

Example cause: selecting by `customer_name` when names are not unique.

Follow-up: In banking systems, use stable keys like `customer_id`, `account_id`, or transaction reference numbers instead of names.

### 68. Exception Handling

PL/SQL uses `EXCEPTION` blocks to handle runtime errors.

```sql
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        NULL;
    WHEN TOO_MANY_ROWS THEN
        NULL;
    WHEN OTHERS THEN
        RAISE;
```

Detailed answer: Handle known exceptions specifically. Use `WHEN OTHERS` carefully and re-raise unless you are intentionally converting the error. `WHEN OTHERS THEN NULL` is dangerous because it hides production failures.

Better pattern:

```sql
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20001, 'Required account was not found');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE(SQLERRM);
        RAISE;
```

Follow-up: For production debugging, log `SQLERRM` and `DBMS_UTILITY.FORMAT_ERROR_BACKTRACE`.

### 69. `COMMIT` vs `ROLLBACK`

`COMMIT` saves changes permanently. `ROLLBACK` cancels uncommitted changes.

Detailed answer: In banking systems, commit only when the full business operation is complete. For a transfer, debit, credit, and audit should commit together. Avoid committing inside every loop row because it breaks atomicity and slows batch jobs.

Savepoint example:

```sql
SAVEPOINT before_fee;

UPDATE accounts
SET balance = balance - 10
WHERE account_id = 101;

ROLLBACK TO before_fee;
```

Follow-up: In Java-backed systems, transaction ownership may belong to the service layer, so stored procedures should not always commit internally.

### 70. Autonomous Transactions

An autonomous transaction commits independently from its parent transaction. It is sometimes used for audit logging, but it must be used carefully.

Detailed answer: Autonomous transactions are useful when an audit log must be saved even if the parent transaction rolls back. They are risky because they can create records that do not match the final business state if used incorrectly.

Example use case: write an error log even when the main transfer fails and rolls back.

Risk: Do not use autonomous transactions for normal money movement. They can commit data independently and make reconciliation harder.

Follow-up: If asked whether autonomous transactions are good or bad, answer that they are specialized. Useful for audit/error logging, dangerous for core business state.
