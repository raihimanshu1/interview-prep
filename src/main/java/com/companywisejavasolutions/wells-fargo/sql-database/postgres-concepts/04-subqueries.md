# PostgreSQL Subqueries

Subqueries test whether you can use one query result inside another query. In Wells Fargo-style interviews, subqueries commonly show up in salary comparisons, department checks, customer eligibility, fraud screening, and "has / does not have" questions.

## Sample Tables

```sql
CREATE TABLE employee (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    department_id INT NOT NULL,
    salary INT NOT NULL,
    manager_id INT
);

CREATE TABLE department (
    department_id INT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL
);

CREATE TABLE customer (
    customer_id INT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL
);

CREATE TABLE account (
    account_id INT PRIMARY KEY,
    customer_id INT NOT NULL REFERENCES customer(customer_id),
    account_type VARCHAR(20) NOT NULL,
    balance NUMERIC(12, 2) NOT NULL
);
```

## Scalar Subquery

Theory: A scalar subquery returns exactly one value. It can be used anywhere PostgreSQL expects a single value, such as in a `WHERE`, `SELECT`, or `HAVING` clause.

Simple example:

```sql
SELECT employee_id,
       employee_name,
       salary
FROM employee
WHERE salary > (
    SELECT AVG(salary)
    FROM employee
);
```

Use case: Find employees earning more than the company average salary.

Why it works: The inner query calculates one average salary. The outer query compares each employee salary against that single value.

Alternate approach:

```sql
SELECT e.employee_id,
       e.employee_name,
       e.salary
FROM employee e
CROSS JOIN (
    SELECT AVG(salary) AS average_salary
    FROM employee
) avg_salary
WHERE e.salary > avg_salary.average_salary;
```

Performance and correctness note: A scalar subquery must return one row and one column. If it returns more than one row, PostgreSQL raises an error. Aggregate functions such as `AVG`, `MAX`, and `COUNT` are common because they naturally return one value.

Interviewer follow-up: "What happens if the subquery returns multiple rows?"

## Correlated Subquery

Theory: A correlated subquery refers to columns from the outer query. It is evaluated logically for each outer row, although PostgreSQL may optimize the execution internally.

Simple example:

```sql
SELECT e.employee_id,
       e.employee_name,
       e.department_id,
       e.salary
FROM employee e
WHERE e.salary > (
    SELECT AVG(e2.salary)
    FROM employee e2
    WHERE e2.department_id = e.department_id
);
```

Use case: Find employees earning more than the average salary in their own department.

Why it works: For each employee `e`, the inner query calculates the average salary for only that employee's department. The outer query then compares the employee salary to that department-specific average.

Alternate approach:

```sql
SELECT e.employee_id,
       e.employee_name,
       e.department_id,
       e.salary
FROM employee e
JOIN (
    SELECT department_id,
           AVG(salary) AS average_salary
    FROM employee
    GROUP BY department_id
) dept_avg
    ON dept_avg.department_id = e.department_id
WHERE e.salary > dept_avg.average_salary;
```

Performance and correctness note: Correlated subqueries are easy to read for row-by-row logic, but joins to pre-aggregated results can be clearer and faster for large tables. Indexes on correlated columns, such as `employee(department_id)`, help PostgreSQL find matching rows efficiently.

Interviewer follow-up: "How would you rewrite this query using a join or a window function?"

## EXISTS

Theory: `EXISTS` checks whether the subquery returns at least one row. It returns true as soon as a matching row is found.

Simple example:

```sql
SELECT d.department_id,
       d.department_name
FROM department d
WHERE EXISTS (
    SELECT 1
    FROM employee e
    WHERE e.department_id = d.department_id
);
```

Use case: Return departments that have at least one employee.

Why it works: For each department, the subquery searches for a related employee. The selected value `1` is only a convention; `EXISTS` cares about whether a row exists, not what value is selected.

Alternate approach:

```sql
SELECT DISTINCT d.department_id,
       d.department_name
FROM department d
JOIN employee e
    ON e.department_id = d.department_id;
```

Performance and correctness note: `EXISTS` avoids duplicate department rows when many employees belong to the same department. It is often a strong choice for "has at least one" interview questions.

Interviewer follow-up: "Why do we often write `SELECT 1` inside an `EXISTS` subquery?"

## NOT EXISTS

Theory: `NOT EXISTS` checks that no matching row exists.

Simple example:

```sql
SELECT d.department_id,
       d.department_name
FROM department d
WHERE NOT EXISTS (
    SELECT 1
    FROM employee e
    WHERE e.department_id = d.department_id
);
```

Use case: Return departments with no employees.

Why it works: For each department, PostgreSQL searches for a matching employee. If no employee row is found, the department is returned.

Alternate approach:

```sql
SELECT d.department_id,
       d.department_name
FROM department d
LEFT JOIN employee e
    ON e.department_id = d.department_id
WHERE e.employee_id IS NULL;
```

Performance and correctness note: `NOT EXISTS` is usually safer than `NOT IN` when the subquery column can contain `NULL`. `NULL` values can make `NOT IN` return no rows because SQL three-valued logic treats comparisons with `NULL` as unknown.

Interviewer follow-up: "Why can `NOT IN` produce surprising results when the subquery returns `NULL`?"

## IN

Theory: `IN` checks whether a value equals any value returned by a subquery.

Simple example:

```sql
SELECT employee_id,
       employee_name,
       department_id
FROM employee
WHERE department_id IN (
    SELECT department_id
    FROM department
    WHERE department_name IN ('Engineering', 'Finance')
);
```

Use case: Find employees who belong to selected departments.

Why it works: The inner query returns department IDs for Engineering and Finance. The outer query keeps employees whose `department_id` is in that returned list.

Alternate approach:

```sql
SELECT e.employee_id,
       e.employee_name,
       e.department_id
FROM employee e
JOIN department d
    ON d.department_id = e.department_id
WHERE d.department_name IN ('Engineering', 'Finance');
```

Performance and correctness note: `IN` is clear when comparing to a simple list of values. If the subquery can return duplicate values, the logical result is unchanged, but the join version may duplicate rows if the joined table is not unique on the join key.

Interviewer follow-up: "When would you prefer a join over `IN`?"

## NOT IN

Theory: `NOT IN` checks whether a value is not equal to any value returned by a subquery.

Simple example:

```sql
SELECT employee_id,
       employee_name,
       department_id
FROM employee
WHERE department_id NOT IN (
    SELECT department_id
    FROM department
    WHERE department_name = 'Finance'
);
```

Use case: Find employees who are not in the Finance department.

Why it works: The inner query returns Finance department IDs. The outer query removes employees whose department is in that set.

Alternate approach:

```sql
SELECT e.employee_id,
       e.employee_name,
       e.department_id
FROM employee e
WHERE NOT EXISTS (
    SELECT 1
    FROM department d
    WHERE d.department_id = e.department_id
      AND d.department_name = 'Finance'
);
```

Performance and correctness note: Prefer `NOT EXISTS` when the subquery column might contain `NULL`. With `NOT IN`, a single `NULL` in the returned list can make the predicate evaluate to unknown for every row.

Interviewer follow-up: "Can you explain SQL's `NULL` behavior in `NOT IN` using a small example?"

## Subquery In FROM

Theory: A subquery in the `FROM` clause creates a derived table that the outer query can join to or filter.

Simple example:

```sql
SELECT dept_avg.department_id,
       dept_avg.average_salary
FROM (
    SELECT department_id,
           AVG(salary) AS average_salary
    FROM employee
    GROUP BY department_id
) dept_avg
WHERE dept_avg.average_salary > 90000;
```

Use case: Filter aggregated department results.

Why it works: The inner query groups employees by department and calculates one average per department. The outer query treats that result like a table and filters departments above the threshold.

Alternate approach:

```sql
SELECT department_id,
       AVG(salary) AS average_salary
FROM employee
GROUP BY department_id
HAVING AVG(salary) > 90000;
```

Performance and correctness note: Use a `HAVING` clause when the filter is simple and directly tied to the aggregate. Use a derived table when the aggregated result needs to be joined, reused, or made easier to read.

Interviewer follow-up: "What is the difference between `WHERE` and `HAVING`?"

## Subquery In SELECT

Theory: A subquery in the `SELECT` list calculates a value for each output row.

Simple example:

```sql
SELECT d.department_id,
       d.department_name,
       (
           SELECT COUNT(*)
           FROM employee e
           WHERE e.department_id = d.department_id
       ) AS employee_count
FROM department d;
```

Use case: Show each department with its employee count.

Why it works: For each department row, the inner query counts employees that belong to that department.

Alternate approach:

```sql
SELECT d.department_id,
       d.department_name,
       COUNT(e.employee_id) AS employee_count
FROM department d
LEFT JOIN employee e
    ON e.department_id = d.department_id
GROUP BY d.department_id,
         d.department_name;
```

Performance and correctness note: A correlated subquery in the `SELECT` list can be readable for small result sets. For many rows, a `LEFT JOIN` with `GROUP BY` is often easier for PostgreSQL to optimize.

Interviewer follow-up: "Why is `COUNT(e.employee_id)` better than `COUNT(*)` in the left-join version?"

## IN vs EXISTS

Theory: `IN` compares a value to a returned list. `EXISTS` checks whether a related row exists.

Simple examples:

```sql
SELECT employee_id,
       employee_name
FROM employee
WHERE department_id IN (
    SELECT department_id
    FROM department
    WHERE department_name = 'Finance'
);
```

```sql
SELECT employee_id,
       employee_name
FROM employee e
WHERE EXISTS (
    SELECT 1
    FROM department d
    WHERE d.department_id = e.department_id
      AND d.department_name = 'Finance'
);
```

Why it works: The `IN` version builds a set of eligible department IDs. The `EXISTS` version checks whether each employee has a matching Finance department row.

Alternate approach:

```sql
SELECT e.employee_id,
       e.employee_name
FROM employee e
JOIN department d
    ON d.department_id = e.department_id
WHERE d.department_name = 'Finance';
```

Performance and correctness note: For many PostgreSQL queries, the optimizer can transform `IN`, `EXISTS`, and joins into similar plans. Choose the form that best expresses the logic, then use `EXPLAIN ANALYZE` if performance matters.

Interviewer follow-up: "If two forms produce the same result, how would you decide which one to use?"

## Employees Earning More Than Their Manager

Theory: Some interview problems can be solved with either a subquery or a self join. This pattern tests whether you can compare a row to a related row in the same table.

Simple example using a self join:

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

Why it works: The employee row is joined to the manager row using `manager_id`. The `WHERE` clause compares the two salaries after the relationship is established.

Alternate approach using a correlated subquery:

```sql
SELECT e.employee_id,
       e.employee_name,
       e.salary
FROM employee e
WHERE e.salary > (
    SELECT m.salary
    FROM employee m
    WHERE m.employee_id = e.manager_id
);
```

Performance and correctness note: The self join is usually clearer when you need manager details in the output. The scalar subquery is compact when you only need to compare salaries. The scalar subquery assumes each employee has at most one matching manager, which is enforced when `employee_id` is a primary key.

Interviewer follow-up: "How would your query change if some employees do not have managers?"

## Interview Tip

When explaining subqueries, say whether the inner query returns:

- One value
- Many values
- Matching existence
- Non-matching existence
- A derived table used by the outer query

Then explain whether the subquery is correlated or non-correlated.
