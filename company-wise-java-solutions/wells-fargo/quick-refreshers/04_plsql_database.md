# Oracle PL/SQL / Database / Performance - Complete Interview Deep Dive

This file is for Oracle PL/SQL interview preparation from basics to advanced.

Assumption:

```text
You know a little SQL.
You are not strong in PL/SQL yet.
You need to answer interview questions today.
```

So every topic starts simple, then moves toward interview-level answers.

---

# 1. SQL vs PL/SQL

## What Is SQL?

SQL is used to work with data.

Examples:

```sql
SELECT * FROM employees;

UPDATE employees
SET salary = salary + 1000
WHERE employee_id = 101;
```

SQL is mainly:

```text
Query data
Insert data
Update data
Delete data
Create database objects
```

---

## What Is PL/SQL?

PL/SQL means:

```text
Procedural Language extension of SQL
```

Oracle added programming features on top of SQL:

```text
variables
if conditions
loops
exceptions
procedures
functions
packages
cursors
bulk operations
triggers
```

---

## Simple Mental Model

SQL:

```text
What data do I want?
```

PL/SQL:

```text
What steps should database execute?
```

---

## Example

SQL:

```sql
SELECT salary
FROM employees
WHERE employee_id = 101;
```

PL/SQL:

```sql
DECLARE
    v_salary employees.salary%TYPE;
BEGIN
    SELECT salary
    INTO v_salary
    FROM employees
    WHERE employee_id = 101;

    IF v_salary > 100000 THEN
        DBMS_OUTPUT.PUT_LINE('High salary');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Normal salary');
    END IF;
END;
/
```

---

## Interview Answer

> SQL is used to query and modify data, while PL/SQL is Oracle's procedural extension that allows variables, conditions, loops, exception handling, procedures, functions, packages, cursors, and triggers. SQL tells the database what data operation to perform. PL/SQL lets us write step-by-step business logic inside the database.

---

# 2. Basic PL/SQL Block Structure

Every PL/SQL program is written in blocks.

## Structure

```sql
DECLARE
    -- variables
BEGIN
    -- executable statements
EXCEPTION
    -- error handling
END;
/
```

---

## Meaning

```text
DECLARE   -> optional variable declaration
BEGIN     -> mandatory executable section
EXCEPTION -> optional error handling
END       -> block ends
/         -> run block in SQL*Plus/SQL Developer style tools
```

---

## Simple Example

```sql
DECLARE
    v_name VARCHAR2(50);
BEGIN
    v_name := 'Himanshu';
    DBMS_OUTPUT.PUT_LINE('Hello ' || v_name);
END;
/
```

Output:

```text
Hello Himanshu
```

---

## Beginner Mistake

Forgetting assignment operator:

Wrong:

```sql
v_name = 'Himanshu';
```

Correct:

```sql
v_name := 'Himanshu';
```

In PL/SQL:

```text
:= is assignment
= is comparison
```

---

## Interview Answer

> A PL/SQL block has a declaration section, executable section, and optional exception section. `DECLARE` is used for variables, `BEGIN` contains logic, `EXCEPTION` handles errors, and `END` closes the block. The executable section is mandatory, while declaration and exception sections are optional.

---

# 3. Variables And Data Types

## What Is A Variable?

A variable stores a value temporarily while PL/SQL code runs.

Example:

```sql
DECLARE
    v_count NUMBER;
    v_name  VARCHAR2(100);
BEGIN
    v_count := 10;
    v_name := 'John';
END;
/
```

---

## Common Oracle Data Types

```text
NUMBER      -> numeric values
VARCHAR2    -> text
DATE        -> date and time
BOOLEAN     -> true/false inside PL/SQL
%TYPE       -> same type as table column
%ROWTYPE    -> same structure as table row
```

---

## Why Use `%TYPE`?

Suppose employee salary column is:

```sql
salary NUMBER(10,2)
```

Instead of:

```sql
v_salary NUMBER(10,2);
```

Use:

```sql
v_salary employees.salary%TYPE;
```

If column type changes later, variable automatically matches.

---

## Interview Answer

> PL/SQL variables store temporary values during execution. Common types are `NUMBER`, `VARCHAR2`, `DATE`, and `BOOLEAN`. For database-related variables, `%TYPE` is preferred because it keeps the variable datatype aligned with the table column.

---

# 4. `%TYPE` vs `%ROWTYPE`

This is a very common Oracle interview question.

---

## `%TYPE`

Used for one column datatype.

```sql
v_salary employees.salary%TYPE;
```

Meaning:

```text
v_salary has same datatype as employees.salary
```

---

## `%ROWTYPE`

Used for complete row structure.

```sql
v_employee employees%ROWTYPE;
```

Meaning:

```text
v_employee can hold one full row from employees table
```

---

## Example

```sql
DECLARE
    v_emp employees%ROWTYPE;
BEGIN
    SELECT *
    INTO v_emp
    FROM employees
    WHERE employee_id = 101;

    DBMS_OUTPUT.PUT_LINE(v_emp.first_name);
END;
/
```

---

## Difference

| Feature | `%TYPE` | `%ROWTYPE` |
|---|---|---|
| Represents | One column | Full row |
| Used for | Single value | Record |
| Example | salary | employee row |
| Good for | parameters, variables | full-row fetch |

---

## Common Mistake

Using `%ROWTYPE` with partial query:

```sql
DECLARE
    v_emp employees%ROWTYPE;
BEGIN
    SELECT first_name
    INTO v_emp
    FROM employees
    WHERE employee_id = 101;
END;
/
```

Wrong because:

```text
v_emp expects full row structure.
Query returns only one column.
```

---

## Interview Answer

> `%TYPE` declares a variable with the same datatype as a table column. `%ROWTYPE` declares a record matching an entire row. I use `%TYPE` for individual values like employee ID, salary, or status. I use `%ROWTYPE` when I want to fetch and process a complete row.

---

# 5. SELECT INTO

In PL/SQL, `SELECT INTO` is used to fetch query result into variables.

---

## Basic Example

```sql
DECLARE
    v_name employees.first_name%TYPE;
BEGIN
    SELECT first_name
    INTO v_name
    FROM employees
    WHERE employee_id = 101;

    DBMS_OUTPUT.PUT_LINE(v_name);
END;
/
```

---

## Most Important Rule

`SELECT INTO` must return exactly one row.

```text
0 rows  -> NO_DATA_FOUND
1 row   -> success
2+ rows -> TOO_MANY_ROWS
```

This is extremely important.

---

# 6. SELECT INTO With No Rows

## Example

```sql
SELECT first_name
INTO v_name
FROM employees
WHERE employee_id = 999999;
```

If employee does not exist:

```text
NO_DATA_FOUND
```

---

## Beginner Misunderstanding

Wrong thinking:

```text
If no row is found, variable becomes null.
```

Correct:

```text
Oracle raises NO_DATA_FOUND exception.
```

---

## Safe Handling

```sql
BEGIN
    SELECT first_name
    INTO v_name
    FROM employees
    WHERE employee_id = p_employee_id;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Employee not found');
END;
/
```

---

## Banking Example

If account does not exist:

```text
Do not silently continue.
Do not treat it as null balance.
Raise clear error or handle explicitly.
```

---

## Interview Answer

> `SELECT INTO` expects exactly one row. If no row is returned, Oracle raises `NO_DATA_FOUND`; it does not assign null automatically. I handle `NO_DATA_FOUND` explicitly when missing data is possible, especially in account, customer, or payment lookup flows.

---

# 7. SELECT INTO With Multiple Rows

## Example

```sql
SELECT first_name
INTO v_name
FROM employees
WHERE department_id = 10;
```

If department 10 has many employees:

```text
TOO_MANY_ROWS
```

---

## Why It Happens

Because `SELECT INTO` expects:

```text
exactly one row
```

but query returns:

```text
multiple rows
```

---

## Fix 1: Use Unique Filter

```sql
SELECT first_name
INTO v_name
FROM employees
WHERE employee_id = 101;
```

---

## Fix 2: Use Cursor Loop

```sql
FOR emp IN (
    SELECT first_name
    FROM employees
    WHERE department_id = 10
) LOOP
    DBMS_OUTPUT.PUT_LINE(emp.first_name);
END LOOP;
```

---

## Fix 3: Use BULK COLLECT

```sql
SELECT first_name
BULK COLLECT INTO v_names
FROM employees
WHERE department_id = 10;
```

---

## Interview Answer

> If `SELECT INTO` returns more than one row, Oracle raises `TOO_MANY_ROWS`. If only one row is expected, I fix the `WHERE` clause using a unique key. If multiple rows are valid, I use a cursor loop or `BULK COLLECT`.

---

# 8. Exception Handling Keywords

## What Is Exception Handling?

Exception handling means:

```text
What should PL/SQL do when an error occurs?
```

---

## Keywords

```text
EXCEPTION -> starts error handling section
WHEN      -> catches specific exception
THEN      -> executes handler code
OTHERS    -> catches all other exceptions
RAISE     -> throws/rethrows exception
SQLCODE   -> numeric error code
SQLERRM   -> error message
```

---

## Example

```sql
BEGIN
    SELECT first_name
    INTO v_name
    FROM employees
    WHERE employee_id = 999;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('No employee found');

    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('More than one employee found');

    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE(SQLCODE || ' - ' || SQLERRM);
        RAISE;
END;
/
```

---

## Dangerous Mistake

Never do this in production:

```sql
WHEN OTHERS THEN
    NULL;
```

Why?

```text
It hides errors.
Job may fail silently.
Data may become inconsistent.
Debugging becomes painful.
```

---

## Interview Answer

> PL/SQL exception handling uses `EXCEPTION`, `WHEN`, `THEN`, `OTHERS`, and `RAISE`. I handle known exceptions like `NO_DATA_FOUND` and `TOO_MANY_ROWS` explicitly. For unexpected errors, I log `SQLCODE` and `SQLERRM`, then usually re-raise. I avoid `WHEN OTHERS THEN NULL` because it hides production failures.

---

# 9. COMMIT And ROLLBACK

## What Is COMMIT?

`COMMIT` permanently saves transaction changes.

Before commit:

```text
Changes are temporary.
Can be rolled back.
```

After commit:

```text
Changes are permanent.
Other sessions can see them.
Locks are released.
```

---

## What Is ROLLBACK?

`ROLLBACK` undoes uncommitted changes.

---

## Banking Example

Money transfer:

```text
Debit account A
Credit account B
```

Both must succeed together.

```sql
BEGIN
    UPDATE accounts
    SET balance = balance - 100
    WHERE account_id = 1;

    UPDATE accounts
    SET balance = balance + 100
    WHERE account_id = 2;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
```

---

## Dangerous Mistake

Committing after debit but before credit:

```text
Debit committed.
Credit failed.
Money disappears.
```

Bad.

---

## Interview Answer

> `COMMIT` permanently saves transaction changes, and `ROLLBACK` undoes uncommitted changes. In business operations like money transfer, transaction boundary should match the full business operation. Debit and credit should commit together or roll back together. Partial commits are dangerous in financial systems.

---

# 10. Cursors

## What Is A Cursor?

A cursor is used to process query result row by row.

---

## Why Needed?

`SELECT INTO` handles one row.

Cursor handles:

```text
multiple rows
```

---

## Implicit Cursor Loop

Most beginner-friendly:

```sql
BEGIN
    FOR emp IN (
        SELECT employee_id, first_name
        FROM employees
        WHERE department_id = 10
    ) LOOP
        DBMS_OUTPUT.PUT_LINE(emp.employee_id || ' ' || emp.first_name);
    END LOOP;
END;
/
```

Oracle opens, fetches, and closes cursor automatically.

---

## Explicit Cursor

```sql
DECLARE
    CURSOR c_emp IS
        SELECT employee_id, first_name
        FROM employees;

    v_emp c_emp%ROWTYPE;
BEGIN
    OPEN c_emp;

    LOOP
        FETCH c_emp INTO v_emp;
        EXIT WHEN c_emp%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(v_emp.first_name);
    END LOOP;

    CLOSE c_emp;
END;
/
```

---

## Interview Answer

> A cursor is used to process multiple rows returned by a query. For simple cases, I prefer cursor `FOR LOOP` because Oracle handles open, fetch, and close automatically. Explicit cursors give more control but require manual open/fetch/close.

---

# 11. BULK COLLECT

## What Is BULK COLLECT?

`BULK COLLECT` fetches many rows at once into a collection.

Normal cursor loop:

```text
Fetch one row
Process one row
Fetch next row
```

Bulk collect:

```text
Fetch many rows together
Process in memory
```

---

## Why Faster?

Because it reduces context switching between:

```text
SQL engine
PL/SQL engine
```

Row-by-row processing is often called:

```text
slow-by-slow
```

---

## Example

```sql
DECLARE
    TYPE t_names IS TABLE OF employees.first_name%TYPE;
    v_names t_names;
BEGIN
    SELECT first_name
    BULK COLLECT INTO v_names
    FROM employees
    WHERE department_id = 10;

    FOR i IN 1 .. v_names.COUNT LOOP
        DBMS_OUTPUT.PUT_LINE(v_names(i));
    END LOOP;
END;
/
```

---

## Memory Risk

This can be dangerous:

```sql
SELECT *
BULK COLLECT INTO v_big_collection
FROM huge_table;
```

Why?

```text
Loads too much data into memory.
```

Use `LIMIT` with cursor for large data.

---

## Interview Answer

> `BULK COLLECT` improves performance by fetching multiple rows into a collection in one operation, reducing context switches between SQL and PL/SQL engines. It is useful for large reads, but it can consume memory, so for huge datasets I use batching with `LIMIT`.

---

# 12. FORALL

## What Is FORALL?

`FORALL` is used for bulk DML.

DML means:

```text
INSERT
UPDATE
DELETE
```

---

## Normal Slow Loop

```sql
FOR i IN 1 .. v_ids.COUNT LOOP
    UPDATE employees
    SET status = 'ACTIVE'
    WHERE employee_id = v_ids(i);
END LOOP;
```

This sends many individual update statements.

---

## FORALL Version

```sql
FORALL i IN 1 .. v_ids.COUNT
    UPDATE employees
    SET status = 'ACTIVE'
    WHERE employee_id = v_ids(i);
```

Faster because Oracle sends bulk operation to SQL engine.

---

## BULK COLLECT vs FORALL

```text
BULK COLLECT -> bulk fetch/read
FORALL       -> bulk insert/update/delete
```

---

## SAVE EXCEPTIONS

If some rows fail but you want to continue:

```sql
FORALL i IN 1 .. v_ids.COUNT SAVE EXCEPTIONS
    UPDATE employees
    SET status = 'ACTIVE'
    WHERE employee_id = v_ids(i);
```

---

## Interview Answer

> `FORALL` is used to perform bulk DML operations efficiently. It reduces context switches by sending many INSERT, UPDATE, or DELETE operations together. `BULK COLLECT` is for bulk reading, while `FORALL` is for bulk writing.

---

# 13. Procedures vs Functions

## Procedure

A procedure performs an action.

```sql
CREATE OR REPLACE PROCEDURE update_salary (
    p_employee_id IN employees.employee_id%TYPE,
    p_amount      IN NUMBER
) AS
BEGIN
    UPDATE employees
    SET salary = salary + p_amount
    WHERE employee_id = p_employee_id;
END;
/
```

---

## Function

A function returns a value.

```sql
CREATE OR REPLACE FUNCTION get_bonus (
    p_salary IN NUMBER
) RETURN NUMBER AS
BEGIN
    RETURN p_salary * 0.10;
END;
/
```

---

## Difference

| Feature | Procedure | Function |
|---|---|---|
| Purpose | Perform action | Return value |
| Return | No direct return required | Must return value |
| SQL usage | Usually not in SELECT | Can be used in SELECT if allowed |
| Example | update salary | calculate bonus |

---

## Interview Answer

> A procedure performs an action and may use OUT parameters, while a function must return a value. I use procedures for business operations like updates or processing, and functions for calculations or reusable value-returning logic.

---

# 14. Packages

## What Is A Package?

A package groups related PL/SQL code.

It has two parts:

```text
Package specification
Package body
```

---

## Package Specification

Public contract:

```sql
CREATE OR REPLACE PACKAGE account_pkg AS
    PROCEDURE transfer_money(
        p_from_account IN NUMBER,
        p_to_account   IN NUMBER,
        p_amount       IN NUMBER
    );
END account_pkg;
/
```

---

## Package Body

Implementation:

```sql
CREATE OR REPLACE PACKAGE BODY account_pkg AS
    PROCEDURE transfer_money(
        p_from_account IN NUMBER,
        p_to_account   IN NUMBER,
        p_amount       IN NUMBER
    ) AS
    BEGIN
        -- implementation
        NULL;
    END;
END account_pkg;
/
```

---

## Why Use Packages?

```text
Group related procedures/functions
Hide private implementation
Improve organization
Support reusable database APIs
Better maintainability
```

---

## Interview Answer

> A package is used to group related procedures, functions, variables, and types. The specification exposes the public API, and the body contains implementation. Packages improve modularity, encapsulation, and maintainability in Oracle PL/SQL.

---

# 15. Triggers

## What Is A Trigger?

A trigger is PL/SQL code that runs automatically when an event happens.

Events:

```text
INSERT
UPDATE
DELETE
```

---

## Example

```sql
CREATE OR REPLACE TRIGGER trg_emp_audit
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
    INSERT INTO employee_audit(employee_id, old_salary, new_salary)
    VALUES(:OLD.employee_id, :OLD.salary, :NEW.salary);
END;
/
```

---

## `:OLD` and `:NEW`

```text
:OLD -> value before change
:NEW -> value after change
```

---

## Common Mistake

Putting too much business logic in triggers.

Why bad?

```text
Hidden behavior
Hard debugging
Performance issues
Unexpected side effects
```

---

## Interview Answer

> A trigger runs automatically before or after DML events like insert, update, or delete. It is useful for auditing and enforcing simple rules, but I avoid heavy business logic in triggers because it becomes hidden, hard to debug, and can hurt performance.

---

# 16. DBMS_OUTPUT / Missing Print Output

## What Is DBMS_OUTPUT?

`DBMS_OUTPUT.PUT_LINE` prints debug text.

Example:

```sql
DBMS_OUTPUT.PUT_LINE('Hello');
```

---

## Why Output May Not Show

In SQL*Plus:

```sql
SET SERVEROUTPUT ON;
```

In SQL Developer:

```text
Enable DBMS Output panel
```

---

## Production Warning

`DBMS_OUTPUT` is for learning/debugging.

Not for production logs.

Production should use:

```text
application logs
audit tables
error tables
monitoring tools
```

---

## Interview Answer

> `DBMS_OUTPUT.PUT_LINE` prints debug output, but it only appears if server output is enabled in the client. It is useful for development, not production logging. In production, I would use proper application logs, audit tables, or error logging tables.

---

# 17. NULL Comparison

## What Is NULL?

NULL means:

```text
unknown / missing value
```

It is not equal to anything.

Not even another NULL.

---

## Wrong

```sql
WHERE manager_id = NULL;
```

This does not work.

---

## Correct

```sql
WHERE manager_id IS NULL;
```

And:

```sql
WHERE manager_id IS NOT NULL;
```

---

## Why?

Because:

```text
NULL = NULL
```

is not true.

It is unknown.

---

## Interview Answer

> NULL represents unknown or missing value. We cannot compare NULL using `=` or `!=`. We must use `IS NULL` or `IS NOT NULL`. This is important because incorrect NULL comparison can silently return wrong query results.

---

# 18. Identify Issue: `SELECT * INTO v_emp FROM employee`

This exact type of question appears often.

---

## Code

```sql
SELECT *
INTO v_emp
FROM employee;
```

---

## What Is The Issue?

`SELECT INTO` expects exactly one row.

But:

```sql
FROM employee
```

without `WHERE` may return many rows.

---

## Possible Results

```text
0 rows  -> NO_DATA_FOUND
1 row   -> success
2+ rows -> TOO_MANY_ROWS
```

Also:

```text
v_emp must match selected columns.
```

If `v_emp` is one variable but `SELECT *` returns many columns:

```text
shape mismatch
```

---

## Correct For One Employee

```sql
SELECT *
INTO v_emp
FROM employee
WHERE employee_id = 101;
```

---

## Correct For Many Employees

Cursor:

```sql
FOR emp IN (SELECT * FROM employee) LOOP
    DBMS_OUTPUT.PUT_LINE(emp.employee_id);
END LOOP;
```

Bulk:

```sql
SELECT *
BULK COLLECT INTO v_emps
FROM employee;
```

---

## Interview Answer

> The issue is that `SELECT * INTO v_emp FROM employee` can return zero, one, or many rows. `SELECT INTO` requires exactly one row. If there are no rows, Oracle raises `NO_DATA_FOUND`; if there are multiple rows, it raises `TOO_MANY_ROWS`. Also `v_emp` must match the selected row structure. I would add a unique `WHERE` clause or use a cursor/BULK COLLECT for multiple rows.

---

# 19. Performance Basics In PL/SQL

## Main Performance Problems

```text
Row-by-row processing
Too many context switches
Missing indexes
SELECT *
Committing too often
Unbounded BULK COLLECT
Functions on indexed columns
Unnecessary triggers
```

---

## Row-By-Row Problem

Bad:

```sql
FOR emp IN (SELECT employee_id FROM employees) LOOP
    UPDATE employees
    SET status = 'ACTIVE'
    WHERE employee_id = emp.employee_id;
END LOOP;
```

Better:

```sql
UPDATE employees
SET status = 'ACTIVE';
```

Best SQL is often:

```text
Set-based SQL
```

not PL/SQL loops.

---

## Interview Answer

> For PL/SQL performance, I first try to use set-based SQL instead of row-by-row loops. If procedural processing is required, I use `BULK COLLECT` and `FORALL` to reduce context switches. I avoid unnecessary `SELECT *`, frequent commits, missing indexes, and unbounded bulk loading.

---

# 20. Senior-Level PL/SQL Answer

If interviewer asks:

```text
How comfortable are you with PL/SQL?
```

Say:

> I understand Oracle PL/SQL block structure, variables, `%TYPE`, `%ROWTYPE`, `SELECT INTO`, exception handling, transactions, cursors, procedures, functions, packages, triggers, and performance basics. I know that `SELECT INTO` must return exactly one row, otherwise Oracle raises `NO_DATA_FOUND` or `TOO_MANY_ROWS`. I handle exceptions explicitly, avoid swallowing errors, use `COMMIT` and `ROLLBACK` carefully, and prefer set-based SQL when possible. For bulk processing, I use `BULK COLLECT` for reads and `FORALL` for DML, while watching memory and transaction boundaries.

---

# 21. Common Follow-Up Questions

## What happens when `SELECT INTO` returns no rows?

Oracle raises:

```text
NO_DATA_FOUND
```

---

## What happens when `SELECT INTO` returns many rows?

Oracle raises:

```text
TOO_MANY_ROWS
```

---

## What saves changes permanently?

```text
COMMIT
```

---

## What undoes uncommitted changes?

```text
ROLLBACK
```

---

## `%TYPE` vs `%ROWTYPE`?

```text
%TYPE    -> one column datatype
%ROWTYPE -> full row structure
```

---

## `BULK COLLECT` vs `FORALL`?

```text
BULK COLLECT -> bulk read
FORALL       -> bulk DML
```

---

## Why does NULL comparison fail?

Because NULL means unknown.

Use:

```sql
IS NULL
IS NOT NULL
```

---

## Why avoid `WHEN OTHERS THEN NULL`?

Because it hides errors and can cause silent data corruption.

---

## Why avoid commit inside loop?

Because it causes:

```text
partial commits
hard rollback
performance overhead
broken transaction boundary
```

---

## What is the best performance mindset?

```text
Use SQL set-based operations first.
Use PL/SQL loops only when procedural logic is needed.
Use BULK COLLECT/FORALL for large procedural processing.
```
