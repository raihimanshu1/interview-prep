# PostgreSQL SQL Practice Roadmap

This roadmap is for backend interviews around 5 to 10 years of experience. You do not need hundreds of SQL questions. You need to master the repeated patterns: joins, grouping, subqueries, windows, ranking, top-N, duplicates, pagination, indexing, and transactions.

All examples in this folder use PostgreSQL syntax strictly.

## Where to Practice

### LeetCode Database

Best for interview-style SQL questions.

Good for:

- Common product-company patterns
- Ranking problems
- Window functions
- Joins and aggregation
- Multiple SQL dialect discussions

### SQLBolt

Best for fundamentals.

Good for:

- `SELECT`
- `WHERE`
- `JOIN`
- `GROUP BY`
- Aggregations
- Basic window functions

### DataLemur

Very interview-focused.

Good for:

- Amazon-style SQL
- Uber-style SQL
- Google-style SQL
- TikTok-style SQL
- Data analytics SQL patterns

### HackerRank SQL

Good for quick progression from easy to medium.

## Recommended Order

1. Basics
2. Aggregation
3. Joins
4. Subqueries
5. Real interview patterns
6. Window functions
7. Indexing and transactions

## How To Study Each Topic

For every SQL pattern, practice answering five things:

1. Concept: what idea is being tested.
2. Query: the simple PostgreSQL solution.
3. Why it works: explain the query line by line.
4. Alternate approach: another valid way to solve it.
5. Follow-up: performance, correctness, indexes, edge cases, or production tradeoffs.

This matters because senior interviewers rarely stop at "write the query." They usually ask why you chose that approach, what happens with duplicates or nulls, and how the query behaves on a large table.

## PostgreSQL Query Execution Order

SQL is written in one order but logically evaluated in another order.

```text
FROM / JOIN
WHERE
GROUP BY
HAVING
WINDOW FUNCTIONS
SELECT
ORDER BY
LIMIT / OFFSET
```

Important interview points:

- `WHERE` filters rows before grouping.
- `HAVING` filters groups after aggregation.
- Window functions are evaluated after `WHERE`, `GROUP BY`, and `HAVING`.
- Window function results are available in `SELECT` and final `ORDER BY`, but not in `WHERE` or `HAVING` of the same query block.
- To filter a window function result, calculate it in a subquery or CTE, then filter outside.

Example:

```sql
SELECT *
FROM (
    SELECT employee_id,
           department,
           salary,
           DENSE_RANK() OVER (
               PARTITION BY department
               ORDER BY salary DESC
           ) AS salary_rank
    FROM employee
) ranked
WHERE salary_rank <= 3;
```

Why the subquery is needed: `salary_rank` is a window function result. It cannot be filtered in the same query block's `WHERE` clause.

## Local PostgreSQL Practice Setup

Create a simple employee table:

```sql
CREATE TABLE employee (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    salary INT NOT NULL,
    manager_id INT
);
```

Insert sample data:

```sql
INSERT INTO employee (id, name, department, salary, manager_id) VALUES
(1, 'Alice', 'Engineering', 120000, NULL),
(2, 'Bob', 'Engineering', 90000, 1),
(3, 'Charlie', 'Engineering', 90000, 1),
(4, 'David', 'HR', 70000, NULL),
(5, 'Eva', 'HR', 65000, 4),
(6, 'Frank', 'Finance', 85000, NULL),
(7, 'Grace', 'Finance', 78000, 6);
```

Why local practice matters:

- You learn table creation.
- You learn inserts, updates, and deletes.
- You learn constraints.
- You can inspect execution plans.
- You can practice transactions manually.

## How To Use Local PostgreSQL

### 1. Practice DDL

DDL means defining database objects.

```sql
CREATE TABLE department (
    department_id INT PRIMARY KEY,
    department_name VARCHAR(50) NOT NULL UNIQUE
);
```

Interview point: Constraints are part of correctness. A `UNIQUE` constraint prevents duplicate department names. A `PRIMARY KEY` identifies each row.

### 2. Practice DML

DML means changing table data.

```sql
INSERT INTO department (department_id, department_name)
VALUES (1, 'Engineering');

UPDATE employee
SET salary = salary + 5000
WHERE department = 'Engineering';

DELETE FROM employee
WHERE id = 7;
```

Interview point: Always think about the `WHERE` clause before running `UPDATE` or `DELETE`.

### 3. Practice EXPLAIN

```sql
EXPLAIN ANALYZE
SELECT *
FROM employee
WHERE department = 'Engineering'
ORDER BY salary DESC;
```

Why this matters: `EXPLAIN ANALYZE` shows how PostgreSQL actually ran the query. For senior interviews, this is how you move from guessing to evidence.

### 4. Practice Transactions

```sql
BEGIN;

UPDATE employee
SET salary = salary + 1000
WHERE id = 2;

ROLLBACK;
```

Why this matters: Banking systems need safe transaction boundaries. You should be comfortable with `BEGIN`, `COMMIT`, and `ROLLBACK`.

## Most Asked Interview Areas

- Joins
- `GROUP BY` and `HAVING`
- Subqueries
- Window functions
- Ranking problems
- Top-N problems
- Duplicate detection
- Aggregation
- Indexing concepts
- Transactions and isolation levels

## What Interviewers Usually Probe

### Joins

They may ask:

- Why `LEFT JOIN` instead of `INNER JOIN`?
- What happens if the right table has multiple matches?
- How do you find rows missing from one side?

### Aggregation

They may ask:

- Why `HAVING` instead of `WHERE`?
- What happens with `NULL` values?
- How would you include groups with zero rows?

### Window Functions

They may ask:

- Difference between `ROW_NUMBER`, `RANK`, and `DENSE_RANK`.
- Why a subquery is needed to filter rank.
- How to make ordering deterministic when timestamps tie.

### Performance

They may ask:

- What index would help?
- Why might PostgreSQL ignore an index?
- How do you read `EXPLAIN ANALYZE`?

### Banking Correctness

They may ask:

- How do you avoid partial fund transfers?
- How do you avoid deadlocks?
- Why should ledger entries be immutable?
