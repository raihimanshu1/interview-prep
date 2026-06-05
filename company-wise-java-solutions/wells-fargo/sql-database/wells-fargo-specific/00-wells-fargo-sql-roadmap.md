# Wells Fargo SQL Interview Roadmap

This file is a Wells Fargo-specific SQL preparation map for Java backend, data-heavy backend, and senior engineering interviews.

No one can guarantee every SQL question asked at Wells Fargo because questions vary by team, role, location, interviewer, and year. Still, repeated candidate reports and interview-prep discussions show a consistent pattern: joins, aggregation, duplicate detection, ranking, window functions, banking-style transaction queries, and performance concepts.

Use this as a focused checklist instead of randomly solving hundreds of SQL problems.

For detailed answers to every topic listed below, use [Wells Fargo Tier-Wise SQL Questions With Detailed Answers](05-tier-wise-detailed-answers.md).

## Tier 1: Asked Extremely Frequently

### Joins

Practice:

1. `INNER JOIN` vs `LEFT JOIN`
2. Employees and departments join
3. Customers who never ordered
4. Self join for employee-manager hierarchy
5. Multiple table joins

What interviewer checks:

- Whether you know when unmatched rows should be included
- Whether you can explain join conditions clearly
- Whether you avoid accidental duplicate rows

### Aggregations

Practice:

1. Count employees per department
2. Department-wise average salary
3. Highest salary per department
4. Departments having more than N employees
5. Revenue by customer

What interviewer checks:

- Correct use of `GROUP BY`
- Correct aggregate function selection
- Correct filtering before and after aggregation

### Group By and Having

Practice:

1. Difference between `WHERE` and `HAVING`
2. Departments with average salary greater than X
3. Products sold more than N times

Simple rule:

```text
WHERE filters rows before grouping.
HAVING filters groups after grouping.
```

### Duplicates

Practice:

1. Find duplicate emails
2. Find duplicate records
3. Delete duplicate rows
4. Count duplicate occurrences

Pattern:

```sql
SELECT email, COUNT(*) AS duplicate_count
FROM users
GROUP BY email
HAVING COUNT(*) > 1;
```

### Ranking

Practice:

1. Second highest salary
2. Third highest salary
3. Nth highest salary
4. Top 3 salaries per department

Pattern:

```sql
SELECT *
FROM (
    SELECT employee_id,
           department_id,
           salary,
           DENSE_RANK() OVER (
               PARTITION BY department_id
               ORDER BY salary DESC
           ) AS salary_rank
    FROM employees
) ranked
WHERE salary_rank <= 3;
```

## Tier 2: Window Functions

Window functions are very important for senior backend and data-heavy roles.

Practice:

1. Remove duplicates using `ROW_NUMBER()`
2. Latest order per customer
3. Rank employees by salary
4. Top N per department
5. Nth highest salary using `DENSE_RANK()`
6. Department salary ranking
7. Previous transaction using `LAG()`
8. Next transaction using `LEAD()`
9. Day-over-day growth
10. Consecutive login problem

Core syntax:

```sql
function_name() OVER (
    PARTITION BY group_column
    ORDER BY sort_column
)
```

## Tier 3: Subqueries

Practice:

1. Employees earning more than manager
2. Customers spending above average
3. Products above average price
4. `EXISTS` vs `IN`
5. Correlated subquery
6. Highest salary in each department

Interview expectation:

You should be able to explain whether your subquery returns one value, many values, or just checks existence.

## Tier 4: Banking-Style Questions

Because Wells Fargo is a bank, transaction-style SQL questions are highly relevant.

Practice:

1. Find suspicious transactions
2. Daily transaction totals
3. Running account balance
4. Detect duplicate payments
5. Largest transaction per customer
6. Customers with no transactions in last 90 days
7. Monthly spending trends
8. Top spending customers
9. Consecutive failed transactions
10. Fraud pattern detection query
11. Transaction rollback scenarios
12. ACID properties
13. Isolation levels and locking

## Tier 5: Senior Performance Questions

For 7+ years experience, expect conceptual SQL and database performance questions.

Practice:

1. Clustered vs non-clustered index
2. Why a query is slow
3. How to optimize `GROUP BY`
4. Explain query execution plan
5. Composite index
6. Covering index
7. Partitioning
8. Sharding
9. Materialized view
10. CTE vs temp table

## Tier 6: Oracle and PL/SQL Awareness

Some Wells Fargo systems use Oracle. For a PostgreSQL-focused preparation path, do not mix Oracle syntax into normal query practice. Keep Oracle as conceptual awareness unless the job description mentions Oracle or PL/SQL.

Topics to know:

1. `%TYPE`
2. `%ROWTYPE`
3. `BULK COLLECT`
4. `FORALL`
5. Cursor vs explicit cursor
6. `NO_DATA_FOUND`
7. `TOO_MANY_ROWS`
8. Exception handling
9. `COMMIT` vs `ROLLBACK`
10. Autonomous transactions

## Best Practice Platforms

- LeetCode Database
- DataLemur SQL Practice
- HackerRank SQL Practice
- SQLBolt for fundamentals

## Best Preparation Strategy

Do not solve random questions endlessly. Build pattern recognition.

Master these 10 areas first:

1. Joins
2. `GROUP BY` and `HAVING`
3. Subqueries
4. Window functions
5. Ranking
6. Top-N
7. Duplicate detection
8. Running totals
9. Indexing
10. Transactions and isolation
