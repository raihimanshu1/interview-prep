# PostgreSQL Joins

Join questions test whether you can combine data from multiple tables using relationships. In Wells Fargo-style interviews, joins often appear in customer-account, transaction-reconciliation, employee-manager, and product-eligibility questions.

## Sample Tables

```sql
CREATE TABLE customer (
    customer_id INT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL
);

CREATE TABLE account (
    account_id INT PRIMARY KEY,
    customer_id INT REFERENCES customer(customer_id),
    account_type VARCHAR(20) NOT NULL,
    balance NUMERIC(12, 2) NOT NULL
);

CREATE TABLE employee (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    manager_id INT REFERENCES employee(employee_id)
);

CREATE TABLE core_transaction (
    transaction_ref VARCHAR(30) PRIMARY KEY,
    account_id INT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    transaction_date DATE NOT NULL
);

CREATE TABLE settlement_transaction (
    settlement_ref VARCHAR(30) PRIMARY KEY,
    account_id INT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    settlement_date DATE NOT NULL
);
```

## INNER JOIN

Theory: `INNER JOIN` returns only rows where the join condition matches in both tables.

Simple example:

```sql
SELECT c.customer_id,
       c.customer_name,
       a.account_id,
       a.account_type
FROM customer c
INNER JOIN account a
    ON c.customer_id = a.customer_id;
```

Use case: Find customers who have at least one account.

Why it works: PostgreSQL compares each customer row with account rows using `c.customer_id = a.customer_id`. Rows without a match on either side are removed from the result.

Alternate approach:

```sql
SELECT c.customer_id,
       c.customer_name
FROM customer c
WHERE EXISTS (
    SELECT 1
    FROM account a
    WHERE a.customer_id = c.customer_id
);
```

Performance and correctness note: If one customer has multiple accounts, the join returns multiple rows for that customer. That is correct when account-level detail is required, but use `DISTINCT` or `EXISTS` if the question asks only whether the customer has any account.

Interviewer follow-up: "How would you return each customer only once even if they have many accounts?"

## LEFT JOIN

Theory: `LEFT JOIN` returns all rows from the left table and matching rows from the right table. When there is no right-side match, right-side columns return `NULL`.

Simple example:

```sql
SELECT c.customer_id,
       c.customer_name,
       a.account_id
FROM customer c
LEFT JOIN account a
    ON c.customer_id = a.customer_id;
```

Use case: Find all customers, including customers who do not have accounts yet.

Why it works: `customer` is the base table because it is on the left side. PostgreSQL keeps every customer row, then fills account columns when a matching account exists.

Alternate approach for customers without accounts:

```sql
SELECT c.customer_id,
       c.customer_name
FROM customer c
WHERE NOT EXISTS (
    SELECT 1
    FROM account a
    WHERE a.customer_id = c.customer_id
);
```

Performance and correctness note: Put right-table filters in the `ON` clause when you still want unmatched left rows. A filter like `WHERE a.account_type = 'CHECKING'` after a `LEFT JOIN` removes `NULL` rows and effectively turns the query into an inner join.

Interviewer follow-up: "Why does adding a right-table condition in the `WHERE` clause change the result of a `LEFT JOIN`?"

## RIGHT JOIN

Theory: `RIGHT JOIN` returns all rows from the right table and matching rows from the left table. Missing left-side matches return `NULL`.

Simple example:

```sql
SELECT c.customer_id,
       c.customer_name,
       a.account_id,
       a.account_type
FROM customer c
RIGHT JOIN account a
    ON c.customer_id = a.customer_id;
```

Use case: Find all accounts, even if an account row points to a missing or invalid customer record.

Why it works: `account` is preserved because it is on the right side. PostgreSQL returns every account row and fills customer columns only when the customer exists.

Alternate approach:

```sql
SELECT c.customer_id,
       c.customer_name,
       a.account_id,
       a.account_type
FROM account a
LEFT JOIN customer c
    ON c.customer_id = a.customer_id;
```

Performance and correctness note: `RIGHT JOIN` is less common because the same logic is usually easier to read as a `LEFT JOIN` by swapping table order. In interviews, prefer the version that makes the base table obvious.

Interviewer follow-up: "Why might a team avoid `RIGHT JOIN` in production SQL style guides?"

## FULL JOIN

Theory: `FULL JOIN` returns all rows from both tables. Matching rows appear together. Rows missing from either side still appear, with `NULL` values for the side that did not match.

Simple reconciliation example:

```sql
SELECT COALESCE(c.transaction_ref, s.settlement_ref) AS reference_id,
       c.account_id AS core_account_id,
       s.account_id AS settlement_account_id,
       c.amount AS core_amount,
       s.amount AS settlement_amount,
       CASE
           WHEN c.transaction_ref IS NULL THEN 'MISSING_IN_CORE'
           WHEN s.settlement_ref IS NULL THEN 'MISSING_IN_SETTLEMENT'
           WHEN c.amount <> s.amount THEN 'AMOUNT_MISMATCH'
           ELSE 'MATCHED'
       END AS reconciliation_status
FROM core_transaction c
FULL JOIN settlement_transaction s
    ON c.transaction_ref = s.settlement_ref;
```

Concrete example: If `core_transaction` contains references `TXN100`, `TXN101`, and `TXN102`, while `settlement_transaction` contains `TXN100`, `TXN102`, and `TXN200`, a `FULL JOIN` returns:

- `TXN100` and `TXN102` as matched rows
- `TXN101` with settlement columns as `NULL`, meaning it is missing in settlement
- `TXN200` with core columns as `NULL`, meaning it is missing in core

Use case: Reconcile records between a core banking ledger and a settlement, payment, card, or downstream reporting system.

Why it works: A `FULL JOIN` preserves both sides at the same time. The `COALESCE` expression gives one reference value for reporting, and the `CASE` expression classifies which side is missing.

Alternate approach:

```sql
SELECT c.transaction_ref AS reference_id,
       'MISSING_IN_SETTLEMENT' AS reconciliation_status
FROM core_transaction c
WHERE NOT EXISTS (
    SELECT 1
    FROM settlement_transaction s
    WHERE s.settlement_ref = c.transaction_ref
)
UNION ALL
SELECT s.settlement_ref AS reference_id,
       'MISSING_IN_CORE' AS reconciliation_status
FROM settlement_transaction s
WHERE NOT EXISTS (
    SELECT 1
    FROM core_transaction c
    WHERE c.transaction_ref = s.settlement_ref
);
```

Performance and correctness note: `FULL JOIN` is excellent for reconciliation reports, but it can be expensive on very large datasets. Make sure both join keys are indexed and use a stable business key. If the join key is not unique, the result can multiply rows and create false mismatch counts.

Interviewer follow-up: "How would you identify records present on both sides but with different amounts or dates?"

## SELF JOIN

Theory: A self join joins a table to itself by giving the same table two different aliases.

Simple example:

```sql
SELECT e.employee_name AS employee_name,
       m.employee_name AS manager_name
FROM employee e
LEFT JOIN employee m
    ON e.manager_id = m.employee_id;
```

Use case: Show each employee with their manager.

Why it works: The alias `e` represents the employee row, while `m` represents the manager row from the same table. The join condition connects `e.manager_id` to `m.employee_id`.

Alternate approach for hierarchy traversal:

```sql
WITH RECURSIVE employee_hierarchy AS (
    SELECT employee_id,
           employee_name,
           manager_id,
           1 AS level
    FROM employee
    WHERE manager_id IS NULL

    UNION ALL

    SELECT e.employee_id,
           e.employee_name,
           e.manager_id,
           eh.level + 1
    FROM employee e
    JOIN employee_hierarchy eh
        ON e.manager_id = eh.employee_id
)
SELECT employee_id,
       employee_name,
       manager_id,
       level
FROM employee_hierarchy;
```

Performance and correctness note: Use `LEFT JOIN` when top-level employees without managers should still appear. Use an inner join only when employees without managers should be excluded.

Interviewer follow-up: "How would you find employees whose manager is missing from the employee table?"

## CROSS JOIN

Theory: `CROSS JOIN` returns every possible combination of rows from two sources. It does not need an `ON` condition.

Simple example:

```sql
SELECT c.customer_name,
       account_type.account_type
FROM customer c
CROSS JOIN (
    VALUES ('CHECKING'), ('SAVINGS'), ('CREDIT_CARD')
) AS account_type(account_type);
```

Use case: Generate all possible customer and account-type combinations before checking which products a customer already has.

Why it works: PostgreSQL pairs each customer row with each row in the account type list. If there are 100 customers and 3 account types, the result has 300 rows.

Alternate approach:

```sql
SELECT c.customer_name,
       account_type.account_type
FROM customer c,
     (VALUES ('CHECKING'), ('SAVINGS'), ('CREDIT_CARD')) AS account_type(account_type);
```

Performance and correctness note: `CROSS JOIN` can produce very large result sets. Always estimate row counts before using it on production-sized tables.

Interviewer follow-up: "How would you use this result to find account types a customer does not currently have?"

## Anti Join Pattern

Theory: An anti join returns rows from one table that do not have a matching row in another table.

Simple example: Customers who have checking but no savings.

```sql
SELECT c.customer_id,
       c.customer_name
FROM customer c
JOIN account checking
    ON c.customer_id = checking.customer_id
   AND checking.account_type = 'CHECKING'
LEFT JOIN account savings
    ON c.customer_id = savings.customer_id
   AND savings.account_type = 'SAVINGS'
WHERE savings.account_id IS NULL;
```

Why it works: The inner join to `checking` proves the customer has a checking account. The left join to `savings` attempts to find a savings account, and `WHERE savings.account_id IS NULL` keeps only customers where that attempt failed.

Alternate approach:

```sql
SELECT c.customer_id,
       c.customer_name
FROM customer c
WHERE EXISTS (
    SELECT 1
    FROM account a
    WHERE a.customer_id = c.customer_id
      AND a.account_type = 'CHECKING'
)
AND NOT EXISTS (
    SELECT 1
    FROM account a
    WHERE a.customer_id = c.customer_id
      AND a.account_type = 'SAVINGS'
);
```

Performance and correctness note: The `NOT EXISTS` version avoids duplicate rows when a customer has multiple checking accounts. For the join version, add `DISTINCT` if the final output must list each customer once.

Interviewer follow-up: "Which version would you choose if customers can have multiple accounts of the same type?"

## Interview Tip

For joins, always explain:

- Which table is the base table
- Which columns connect the tables
- Whether unmatched rows should be included
- Whether one-to-many relationships can duplicate rows
- Whether filters belong in `ON` or `WHERE`
