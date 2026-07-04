# PostgreSQL Basics

Basic SQL questions test whether you can read a table, filter rows, sort data, format output, and explain the logical order of a query. In a Wells Fargo-style interview, even a simple query may be framed as a customer search, account review, KYC report, or transaction-monitoring lookup.

Use PostgreSQL syntax in all answers.

## Sample Table

```sql
CREATE TABLE customer (
    customer_id BIGINT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    city VARCHAR(50),
    state VARCHAR(50),
    age INT,
    account_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

```sql
INSERT INTO customer VALUES
(1, 'Amit Sharma', 'Mumbai', 'Maharashtra', 32, 'ACTIVE', '2024-01-10 09:00:00'),
(2, 'Neha Verma', 'Pune', 'Maharashtra', 28, 'ACTIVE', '2024-02-12 10:30:00'),
(3, 'Rahul Singh', 'Delhi', 'Delhi', 41, 'INACTIVE', '2024-03-05 14:00:00'),
(4, 'Priya Nair', 'Bengaluru', 'Karnataka', 35, 'ACTIVE', '2024-04-20 16:45:00'),
(5, 'Pooja Mehta', 'Mumbai', 'Maharashtra', 67, 'ACTIVE', '2024-04-25 08:15:00'),
(6, 'Vikram Rao', NULL, 'Karnataka', NULL, 'PENDING', '2024-05-01 12:00:00');
```

## Logical Query Order

SQL is written in this order:

```sql
SELECT ...
FROM ...
WHERE ...
ORDER BY ...
LIMIT ...
```

PostgreSQL logically processes this basic query in this order:

```text
FROM -> WHERE -> SELECT -> ORDER BY -> LIMIT
```

Why this matters:

- `FROM` decides the source rows.
- `WHERE` removes rows before output is prepared.
- `SELECT` chooses and calculates output columns.
- `ORDER BY` sorts the final result.
- `LIMIT` keeps only the requested number of sorted rows.

Interview follow-up: If an interviewer asks why a `SELECT` alias usually cannot be used in `WHERE`, answer that `WHERE` is evaluated before the `SELECT` list is produced.

## SELECT All Rows

### Concept

`SELECT *` returns all columns from the table.

```sql
SELECT *
FROM customer;
```

### Why And How The SQL Works

`FROM customer` tells PostgreSQL to read rows from the `customer` table. `SELECT *` means return every column available in that row.

For the sample data, this returns customer id, name, city, state, age, account status, and created timestamp for all six customers.

### Production Note

`SELECT *` is acceptable while exploring data, but production code should usually list required columns explicitly. This keeps API responses stable if new columns are added and avoids transferring unnecessary data.

### Interview Follow-Ups

- Why is `SELECT *` risky in production services?
- Can adding a new column break an application that uses `SELECT *`?
- How would you inspect only a few rows from a large table?

## SELECT Specific Columns

### Concept

Selecting only required columns makes the result smaller and clearer.

```sql
SELECT customer_id, customer_name, city
FROM customer;
```

### Simple Example Meaning

This query gives a lightweight customer list: id, name, and city. It does not return age, state, account status, or created timestamp.

### Why And How The SQL Works

PostgreSQL reads rows from `customer`, then outputs only the three named columns. The table may contain many more columns, but they are not part of the result set.

### Performance And Correctness Note

On very wide tables, avoiding unnecessary columns can reduce memory, network transfer, and application parsing cost. It also avoids exposing sensitive columns accidentally, which matters in banking systems.

### Interview Follow-Ups

- Which columns should a customer-search API return?
- How would column selection affect performance on a wide table?
- Why should sensitive columns not be fetched unless needed?

## DISTINCT

### Concept

`DISTINCT` removes duplicate result rows.

```sql
SELECT DISTINCT state
FROM customer;
```

### Simple Example Meaning

This returns the unique states from the customer table:

```text
Maharashtra
Delhi
Karnataka
```

Even though Maharashtra appears for multiple customers, it appears once in the result.

### Why And How The SQL Works

PostgreSQL first builds the selected output values, then removes duplicate rows from that selected output. Here the output has one column, so duplicate states are collapsed.

If multiple columns are selected, `DISTINCT` applies to the full combination:

```sql
SELECT DISTINCT state, city
FROM customer;
```

This returns unique state-city pairs, not unique states.

### Alternate Approach

For unique values with counts, use `GROUP BY`:

```sql
SELECT state,
       COUNT(*) AS customer_count
FROM customer
GROUP BY state;
```

### Performance And Correctness Note

`DISTINCT` often requires sorting or hashing to remove duplicates. Do not use it to hide join mistakes. If duplicate rows appear after a join, first check whether the join relationship is one-to-many.

### Interview Follow-Ups

- What is the difference between `DISTINCT state` and `GROUP BY state`?
- Does `DISTINCT` apply to one column or the whole selected row?
- Why can `DISTINCT` hide incorrect joins?

## WHERE

### Concept

`WHERE` filters individual rows before they are returned.

```sql
SELECT customer_id, customer_name, account_status
FROM customer
WHERE account_status = 'ACTIVE';
```

### Simple Example Meaning

This returns only active customers. In a banking system, that could mean customers whose accounts are eligible for normal servicing.

### Why And How The SQL Works

PostgreSQL checks each row. If `account_status = 'ACTIVE'` is true, the row remains. If it is false, the row is removed from the result.

### Alternate Approach

If you need multiple statuses, use `IN`:

```sql
SELECT customer_id, customer_name, account_status
FROM customer
WHERE account_status IN ('ACTIVE', 'PENDING');
```

### Performance And Correctness Note

For frequent status lookups, an index can help:

```sql
CREATE INDEX idx_customer_account_status
ON customer (account_status);
```

If most customers are active, this index may not be very selective. A partial index can help for less common statuses:

```sql
CREATE INDEX idx_customer_pending_status
ON customer (customer_id)
WHERE account_status = 'PENDING';
```

### Interview Follow-Ups

- Should inactive customers be excluded from all reports?
- How would you filter active customers created in the last 30 days?
- When is an index on a status column useful?

## Comparison Operators

### Concept

Basic filters use comparison operators such as `=`, `<>`, `>`, `>=`, `<`, and `<=`.

```sql
SELECT customer_id, customer_name, age
FROM customer
WHERE age >= 60;
```

### Simple Example Meaning

This returns senior customers according to a rule that treats age `60` and above as senior.

### Why And How The SQL Works

The condition is evaluated per row. Customers with `age` values of `60` or more pass the filter. Customers with lower ages do not.

Rows where `age` is `NULL` do not pass this condition because SQL treats comparisons with `NULL` as unknown, not true.

### PostgreSQL Note: NULL Handling

Use `IS NULL` or `IS NOT NULL` for null checks:

```sql
SELECT customer_id, customer_name
FROM customer
WHERE age IS NULL;
```

Do not write:

```sql
WHERE age = NULL
```

That is not correct SQL logic because `NULL` means unknown.

### Interview Follow-Ups

- Why does `age >= 60` not return rows where age is null?
- What is the difference between `<>` and `IS DISTINCT FROM`?
- How would you include customers whose age is missing?

PostgreSQL supports `IS DISTINCT FROM`, which treats `NULL` values as comparable:

```sql
SELECT customer_id, customer_name
FROM customer
WHERE city IS DISTINCT FROM 'Mumbai';
```

This includes customers whose city is not Mumbai and customers whose city is `NULL`.

## BETWEEN

### Concept

`BETWEEN` checks an inclusive range.

```sql
SELECT customer_id, customer_name, age
FROM customer
WHERE age BETWEEN 30 AND 40;
```

### Simple Example Meaning

This returns customers aged `30` through `40`, including both boundary values.

### Why And How The SQL Works

PostgreSQL rewrites this logic like:

```sql
WHERE age >= 30
  AND age <= 40
```

Both conditions must be true.

### Correctness Note

For dates and timestamps, be careful with inclusive upper bounds. This query may miss rows after midnight on May 31:

```sql
SELECT customer_id, customer_name
FROM customer
WHERE created_at BETWEEN '2024-05-01' AND '2024-05-31';
```

The value `'2024-05-31'` is interpreted as midnight at the start of May 31. Prefer a half-open range:

```sql
SELECT customer_id, customer_name
FROM customer
WHERE created_at >= TIMESTAMP '2024-05-01 00:00:00'
  AND created_at <  TIMESTAMP '2024-06-01 00:00:00';
```

### Interview Follow-Ups

- Is `BETWEEN` inclusive or exclusive?
- Why are half-open timestamp ranges safer?
- How would you filter customers created in May 2024?

## IN

### Concept

`IN` checks whether a value matches any value in a list.

```sql
SELECT customer_id, customer_name, city
FROM customer
WHERE city IN ('Mumbai', 'Pune', 'Delhi');
```

### Simple Example Meaning

This returns customers from Mumbai, Pune, or Delhi.

### Why And How The SQL Works

The condition is equivalent to:

```sql
WHERE city = 'Mumbai'
   OR city = 'Pune'
   OR city = 'Delhi'
```

`IN` is more readable when the list has several allowed values.

### Alternate Approach

If allowed cities come from another table, use a subquery:

```sql
SELECT customer_id, customer_name, city
FROM customer
WHERE city IN (
    SELECT city
    FROM serviceable_city
);
```

For existence checks against another table, `EXISTS` is often clearer and avoids null-related surprises:

```sql
SELECT c.customer_id, c.customer_name, c.city
FROM customer c
WHERE EXISTS (
    SELECT 1
    FROM serviceable_city sc
    WHERE sc.city = c.city
);
```

### Correctness Note

`NOT IN` behaves unexpectedly if the list or subquery contains `NULL`. Prefer `NOT EXISTS` for anti-matching against another table.

### Interview Follow-Ups

- When would you use `IN` vs `EXISTS`?
- What happens if a `NOT IN` subquery returns `NULL`?
- How would you filter customers from a dynamic city list?

## LIKE and ILIKE

### Concept

`LIKE` performs pattern matching. PostgreSQL also supports `ILIKE`, which is case-insensitive.

```sql
SELECT customer_id, customer_name
FROM customer
WHERE customer_name ILIKE 'p%';
```

### Simple Example Meaning

This returns customers whose names start with `p` or `P`, such as `Priya Nair` and `Pooja Mehta`.

### Why And How The SQL Works

The pattern `'p%'` means:

- `p`: first character should be p.
- `%`: any number of characters after that.

Common wildcards:

```text
%  -> zero or more characters
_  -> exactly one character
```

Example:

```sql
SELECT customer_id, customer_name
FROM customer
WHERE customer_name ILIKE '%sharma%';
```

This finds names containing `sharma` in any case.

### PostgreSQL-Specific Notes

PostgreSQL has `ILIKE`; many other databases do not. For portable SQL, use `LOWER(...)`:

```sql
SELECT customer_id, customer_name
FROM customer
WHERE LOWER(customer_name) LIKE 'p%';
```

### Performance Note

A normal B-tree index can help with prefix searches such as `customer_name LIKE 'Priya%'`, depending on collation and operator class. It usually cannot help with leading-wildcard searches such as `ILIKE '%priya%'`.

For flexible text search, PostgreSQL's trigram extension can help:

```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_customer_name_trgm
ON customer USING gin (customer_name gin_trgm_ops);
```

### Interview Follow-Ups

- What is the difference between `LIKE` and `ILIKE`?
- Why is `'%abc%'` harder to index than `'abc%'`?
- How would you support fast customer-name search in PostgreSQL?

## ORDER BY

### Concept

`ORDER BY` sorts the final result.

```sql
SELECT customer_id, customer_name, created_at
FROM customer
ORDER BY created_at DESC;
```

### Simple Example Meaning

This returns newest customers first.

### Why And How The SQL Works

PostgreSQL creates the result rows and then sorts them by `created_at` from largest to smallest because `DESC` means descending.

Use `ASC` for ascending order:

```sql
SELECT customer_id, customer_name, created_at
FROM customer
ORDER BY created_at ASC;
```

### Correctness Note: Stable Ordering

If two customers have the same `created_at`, their relative order is not guaranteed unless you add a tie-breaker:

```sql
SELECT customer_id, customer_name, created_at
FROM customer
ORDER BY created_at DESC, customer_id DESC;
```

This is important when using pagination or `LIMIT`.

### PostgreSQL Note: NULL Ordering

PostgreSQL lets you control where null values appear:

```sql
SELECT customer_id, customer_name, age
FROM customer
ORDER BY age DESC NULLS LAST;
```

### Performance Note

An index can help avoid expensive sorting:

```sql
CREATE INDEX idx_customer_created_at_id
ON customer (created_at DESC, customer_id DESC);
```

### Interview Follow-Ups

- Why should `LIMIT` usually be paired with `ORDER BY`?
- How would you make pagination deterministic?
- What does `NULLS LAST` do?

## LIMIT and OFFSET

### Concept

PostgreSQL uses `LIMIT` to restrict returned rows. `OFFSET` skips rows.

```sql
SELECT customer_id, customer_name, created_at
FROM customer
ORDER BY created_at DESC, customer_id DESC
LIMIT 2;
```

### Simple Example Meaning

This returns the two newest customers.

### Why And How The SQL Works

PostgreSQL sorts customers by creation time and id, then keeps only the first two rows.

Pagination with offset:

```sql
SELECT customer_id, customer_name, created_at
FROM customer
ORDER BY created_at DESC, customer_id DESC
LIMIT 2 OFFSET 2;
```

This returns the next two rows after skipping the first two.

### Alternate Approach: Keyset Pagination

For large tables, keyset pagination is usually faster and more stable:

```sql
SELECT customer_id, customer_name, created_at
FROM customer
WHERE (created_at, customer_id) < (TIMESTAMP '2024-04-25 08:15:00', 5)
ORDER BY created_at DESC, customer_id DESC
LIMIT 2;
```

This means "give me the next page after the last row I already saw."

### Performance And Correctness Note

Large `OFFSET` values become expensive because PostgreSQL still has to walk past skipped rows. Offset pagination can also duplicate or miss rows if new data is inserted between page requests.

### Interview Follow-Ups

- Why is `OFFSET 100000` inefficient?
- What is keyset pagination?
- Why does pagination need a deterministic sort?

## CASE WHEN

### Concept

`CASE WHEN` creates conditional output.

```sql
SELECT customer_id,
       customer_name,
       age,
       CASE
           WHEN age >= 60 THEN 'SENIOR'
           WHEN age >= 18 THEN 'ADULT'
           ELSE 'MINOR_OR_UNKNOWN'
       END AS age_group
FROM customer;
```

### Simple Example Meaning

This labels each customer by age group:

- Age `67` becomes `SENIOR`.
- Age `32` becomes `ADULT`.
- Missing age falls into `MINOR_OR_UNKNOWN` in this version.

### Why And How The SQL Works

PostgreSQL evaluates `CASE` from top to bottom and returns the result for the first true condition. The order matters. `age >= 60` must appear before `age >= 18`; otherwise seniors would match the adult condition first.

### Correctness Note

If missing age should be separate, handle it explicitly:

```sql
SELECT customer_id,
       customer_name,
       age,
       CASE
           WHEN age IS NULL THEN 'UNKNOWN'
           WHEN age >= 60 THEN 'SENIOR'
           WHEN age >= 18 THEN 'ADULT'
           ELSE 'MINOR'
       END AS age_group
FROM customer;
```

### Alternate Approach

For reusable business rules, store categories in a reference table rather than repeating `CASE` in many queries.

### Interview Follow-Ups

- Why does the order of `WHEN` clauses matter?
- How should null age be handled?
- Would you hard-code business categories in SQL or store them in a table?

## DATE_TRUNC Basics

### Concept

`DATE_TRUNC` is a PostgreSQL function that rounds a timestamp down to a date/time boundary such as day, month, or year.

```sql
SELECT customer_id,
       customer_name,
       created_at,
       DATE_TRUNC('month', created_at) AS created_month
FROM customer;
```

### Concrete Example

For this value:

```text
2024-04-20 16:45:00
```

PostgreSQL returns:

```text
2024-04-01 00:00:00
```

The timestamp is moved to the start of the month.

### Banking Use Case

Monthly grouping is common for account openings, new customer onboarding, transaction volume, fee reporting, and reconciliation.

```sql
SELECT DATE_TRUNC('month', created_at) AS onboarding_month,
       COUNT(*) AS customer_count
FROM customer
GROUP BY DATE_TRUNC('month', created_at)
ORDER BY onboarding_month;
```

### Why And How The SQL Works

`DATE_TRUNC('month', created_at)` converts all timestamps in the same month into the same month-start timestamp. `GROUP BY` then creates one group per month.

### Correctness Note

`DATE_TRUNC` returns a timestamp, not a plain date. If the business output should show only the date, cast it:

```sql
SELECT DATE_TRUNC('month', created_at)::DATE AS onboarding_month,
       COUNT(*) AS customer_count
FROM customer
GROUP BY DATE_TRUNC('month', created_at)::DATE
ORDER BY onboarding_month;
```

### Interview Follow-Ups

- How would you group customer onboarding by day, month, or year?
- What does `DATE_TRUNC('month', '2024-04-20 16:45:00')` return?
- How do time zones affect month boundaries if timestamps are stored in UTC?

## Basic Query Combining Multiple Concepts

### Business Question

Find the three newest active customers from Maharashtra.

### Query

```sql
SELECT customer_id,
       customer_name,
       city,
       created_at
FROM customer
WHERE account_status = 'ACTIVE'
  AND state = 'Maharashtra'
ORDER BY created_at DESC, customer_id DESC
LIMIT 3;
```

### Why And How The SQL Works

PostgreSQL reads `customer`, filters only active Maharashtra customers, selects the required columns, sorts newest first, and returns the first three rows.

### Performance Note

For this exact access pattern, a composite index can help:

```sql
CREATE INDEX idx_customer_status_state_created
ON customer (account_status, state, created_at DESC, customer_id DESC);
```

Column order should match common filters and sort patterns. Do not add indexes blindly; each index speeds some reads but adds write and storage cost.

### Interview Follow-Ups

- Which part of this query runs first logically?
- Why include `customer_id` in the `ORDER BY`?
- What composite index would help this query?

## Basic Interview Checklist

When answering basic SQL questions, explain:

- What rows the query reads.
- What rows the `WHERE` clause keeps.
- What columns the `SELECT` clause returns.
- Whether the result order is deterministic.
- How null values behave.
- Whether the query needs an index at production scale.

