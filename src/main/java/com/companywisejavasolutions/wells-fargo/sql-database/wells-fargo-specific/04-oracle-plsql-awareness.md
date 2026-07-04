# Oracle and PL/SQL Awareness For Wells Fargo

Most SQL practice in this repository is PostgreSQL-first. Use PostgreSQL syntax for the main SQL prep unless a file explicitly says otherwise.

This file is the Oracle/PLSQL exception: it is written for Wells Fargo-style teams that still work with Oracle-heavy banking platforms, legacy stored procedures, reporting systems, batch jobs, audit tables, and package-based service layers.

If a job description mentions Oracle, PL/SQL, stored procedures, packages, triggers, batch processing, or legacy banking systems, revise this guide carefully.

## 1. What PL/SQL Is

PL/SQL means Procedural Language for SQL. It is Oracle's procedural extension to SQL.

SQL is mainly declarative:

```sql
SELECT account_id, balance
FROM accounts
WHERE customer_id = 101;
```

PL/SQL lets you add programming constructs around SQL:

```sql
DECLARE
    v_balance accounts.balance%TYPE;
BEGIN
    SELECT balance
    INTO v_balance
    FROM accounts
    WHERE account_id = 1001;

    IF v_balance < 0 THEN
        DBMS_OUTPUT.PUT_LINE('Account is overdrawn');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Account balance is healthy');
    END IF;
END;
/
```

In Oracle-heavy enterprise systems, PL/SQL is commonly used for:

- Stored procedures for business operations.
- Functions for reusable calculations.
- Packages for grouping related procedures, functions, types, constants, and cursors.
- Triggers for automatic actions on table events.
- Batch jobs for account processing, reconciliation, fee calculation, and reporting.
- Data validation close to the database.
- Audit logging and operational controls.

For Wells Fargo interviews, you do not need to pretend PL/SQL is always ideal. A strong answer is balanced: PL/SQL is powerful when the business logic is data-heavy and should execute near Oracle tables, but it can become hard to test and maintain if too much application logic is hidden in the database.

## 2. Basic PL/SQL Block Structure

Every PL/SQL program is built from blocks.

```sql
DECLARE
    -- optional declaration section
    v_message VARCHAR2(100);
BEGIN
    -- mandatory executable section
    v_message := 'Hello from PL/SQL';
    DBMS_OUTPUT.PUT_LINE(v_message);
EXCEPTION
    -- optional exception section
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;
/
```

Sections:

- `DECLARE`: variables, constants, cursors, local procedures, local functions.
- `BEGIN`: executable logic.
- `EXCEPTION`: error handling.
- `END;`: closes the block.
- `/`: tells tools like SQL*Plus or SQL Developer to execute the block.

Anonymous block:

```sql
BEGIN
    DBMS_OUTPUT.PUT_LINE('One-time script');
END;
/
```

Named block:

```sql
CREATE OR REPLACE PROCEDURE print_status AS
BEGIN
    DBMS_OUTPUT.PUT_LINE('Procedure executed');
END;
/
```

## 3. Variables, Constants, and Assignment

PL/SQL variables are declared in the `DECLARE` section or inside named objects.

```sql
DECLARE
    v_customer_id NUMBER := 101;
    v_customer_name VARCHAR2(100);
    v_is_active BOOLEAN := TRUE;
    c_bank_code CONSTANT VARCHAR2(10) := 'WF';
BEGIN
    v_customer_name := 'Asha';
    DBMS_OUTPUT.PUT_LINE(c_bank_code || ': ' || v_customer_name);
END;
/
```

Common scalar types:

- `NUMBER`
- `VARCHAR2(size)`
- `DATE`
- `TIMESTAMP`
- `CHAR(size)`
- `BOOLEAN` inside PL/SQL only, not directly stored as a SQL column type in older Oracle patterns.

Naming convention often used in PL/SQL:

- `v_` for variables.
- `c_` for constants.
- `p_` for parameters.
- `r_` for records.
- `cur_` for cursors.

## 4. Anchored Types With `%TYPE`

`%TYPE` makes a PL/SQL variable use the same type as a table column or another variable.

```sql
DECLARE
    v_balance accounts.balance%TYPE;
    v_status accounts.status%TYPE;
BEGIN
    SELECT balance, status
    INTO v_balance, v_status
    FROM accounts
    WHERE account_id = 1001;

    DBMS_OUTPUT.PUT_LINE('Balance: ' || v_balance);
END;
/
```

Why it matters:

- The variable automatically follows the column datatype.
- If `accounts.balance` changes from `NUMBER(12,2)` to `NUMBER(15,2)`, the PL/SQL variable does not need manual datatype changes.
- It reduces mismatch bugs in large enterprise schemas.

Interview answer:

`%TYPE` anchors a variable to the datatype of a database column or another variable. It improves maintainability because the PL/SQL code adapts when the underlying column datatype changes.

## 5. Row Variables With `%ROWTYPE`

`%ROWTYPE` creates a record variable that can hold an entire row.

```sql
DECLARE
    r_account accounts%ROWTYPE;
BEGIN
    SELECT *
    INTO r_account
    FROM accounts
    WHERE account_id = 1001;

    DBMS_OUTPUT.PUT_LINE('Customer: ' || r_account.customer_id);
    DBMS_OUTPUT.PUT_LINE('Balance: ' || r_account.balance);
END;
/
```

Why it matters:

- Useful when you need many columns from a row.
- Keeps the record structure aligned with the table.
- Reduces long variable lists.

Be careful:

- `SELECT * INTO table%ROWTYPE` works only when the selected columns match the table row structure.
- If you only need two columns, explicit variables may be clearer.

## 6. `SELECT INTO`

In PL/SQL, `SELECT INTO` reads query results into variables.

```sql
DECLARE
    v_customer_name customers.full_name%TYPE;
BEGIN
    SELECT full_name
    INTO v_customer_name
    FROM customers
    WHERE customer_id = 101;

    DBMS_OUTPUT.PUT_LINE('Customer: ' || v_customer_name);
END;
/
```

Important rule:

`SELECT INTO` must return exactly one row.

If it returns zero rows:

```sql
NO_DATA_FOUND
```

If it returns more than one row:

```sql
TOO_MANY_ROWS
```

Safe example:

```sql
DECLARE
    v_balance accounts.balance%TYPE;
BEGIN
    SELECT balance
    INTO v_balance
    FROM accounts
    WHERE account_id = 1001;

    DBMS_OUTPUT.PUT_LINE('Balance: ' || v_balance);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('No account found');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Data issue: duplicate account rows');
END;
/
```

Interview answer:

`SELECT INTO` is used when PL/SQL expects a single row. If the query may return multiple rows, use a cursor, cursor FOR loop, collection with `BULK COLLECT`, or a stricter filter.

## 7. Conditional Logic: `IF` and `CASE`

### `IF`

```sql
DECLARE
    v_balance NUMBER := 2500;
BEGIN
    IF v_balance < 0 THEN
        DBMS_OUTPUT.PUT_LINE('Overdrawn');
    ELSIF v_balance = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Zero balance');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Positive balance');
    END IF;
END;
/
```

### `CASE` Statement

```sql
DECLARE
    v_status accounts.status%TYPE := 'A';
BEGIN
    CASE v_status
        WHEN 'A' THEN
            DBMS_OUTPUT.PUT_LINE('Active');
        WHEN 'C' THEN
            DBMS_OUTPUT.PUT_LINE('Closed');
        WHEN 'F' THEN
            DBMS_OUTPUT.PUT_LINE('Frozen');
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unknown status');
    END CASE;
END;
/
```

### Searched `CASE`

```sql
DECLARE
    v_balance NUMBER := 10000;
BEGIN
    CASE
        WHEN v_balance < 0 THEN
            DBMS_OUTPUT.PUT_LINE('High risk');
        WHEN v_balance < 1000 THEN
            DBMS_OUTPUT.PUT_LINE('Low balance');
        ELSE
            DBMS_OUTPUT.PUT_LINE('Normal');
    END CASE;
END;
/
```

Interview tip:

Use `IF` for procedural branching. Use `CASE` when checking a variable against known choices or when the logic reads more cleanly as categories.

## 8. Loops

PL/SQL supports several loop styles.

### Basic Loop

```sql
DECLARE
    v_counter NUMBER := 1;
BEGIN
    LOOP
        DBMS_OUTPUT.PUT_LINE('Counter: ' || v_counter);
        v_counter := v_counter + 1;

        EXIT WHEN v_counter > 5;
    END LOOP;
END;
/
```

### `WHILE` Loop

```sql
DECLARE
    v_counter NUMBER := 1;
BEGIN
    WHILE v_counter <= 5 LOOP
        DBMS_OUTPUT.PUT_LINE('Counter: ' || v_counter);
        v_counter := v_counter + 1;
    END LOOP;
END;
/
```

### Numeric `FOR` Loop

```sql
BEGIN
    FOR i IN 1..5 LOOP
        DBMS_OUTPUT.PUT_LINE('Iteration: ' || i);
    END LOOP;
END;
/
```

### Reverse `FOR` Loop

```sql
BEGIN
    FOR i IN REVERSE 1..5 LOOP
        DBMS_OUTPUT.PUT_LINE('Iteration: ' || i);
    END LOOP;
END;
/
```

Performance warning:

Avoid slow row-by-row processing for large data sets. In Oracle interviews, row-by-row PL/SQL is often called "slow-by-slow" processing. Prefer set-based SQL first, then bulk processing if procedural logic is truly required.

## 9. Cursors

A cursor points to a query result set.

Oracle has:

- Implicit cursors.
- Explicit cursors.
- Cursor FOR loops.
- Cursor variables/ref cursors in advanced code.

### Implicit Cursor

Oracle automatically creates an implicit cursor for SQL statements like `INSERT`, `UPDATE`, `DELETE`, and single-row `SELECT INTO`.

```sql
BEGIN
    UPDATE accounts
    SET status = 'F'
    WHERE balance < -5000;

    DBMS_OUTPUT.PUT_LINE('Rows updated: ' || SQL%ROWCOUNT);
END;
/
```

Useful implicit cursor attributes:

- `SQL%ROWCOUNT`: number of rows affected.
- `SQL%FOUND`: true if at least one row affected.
- `SQL%NOTFOUND`: true if no rows affected.
- `SQL%ISOPEN`: always false for implicit cursors after execution.

Example:

```sql
BEGIN
    UPDATE accounts
    SET last_reviewed_at = SYSDATE
    WHERE account_id = 1001;

    IF SQL%FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Account reviewed');
    ELSE
        DBMS_OUTPUT.PUT_LINE('No matching account');
    END IF;
END;
/
```

### Explicit Cursor

An explicit cursor is declared and controlled by the developer.

```sql
DECLARE
    CURSOR cur_overdrawn_accounts IS
        SELECT account_id, customer_id, balance
        FROM accounts
        WHERE balance < 0;

    v_account_id accounts.account_id%TYPE;
    v_customer_id accounts.customer_id%TYPE;
    v_balance accounts.balance%TYPE;
BEGIN
    OPEN cur_overdrawn_accounts;

    LOOP
        FETCH cur_overdrawn_accounts
        INTO v_account_id, v_customer_id, v_balance;

        EXIT WHEN cur_overdrawn_accounts%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
            'Account ' || v_account_id || ' balance ' || v_balance
        );
    END LOOP;

    CLOSE cur_overdrawn_accounts;
END;
/
```

Explicit cursor attributes:

- `cursor_name%ROWCOUNT`
- `cursor_name%FOUND`
- `cursor_name%NOTFOUND`
- `cursor_name%ISOPEN`

Use explicit cursors when:

- You need manual open/fetch/close control.
- You need to pass cursor parameters.
- You need more control than a simple cursor FOR loop gives.

### Parameterized Explicit Cursor

```sql
DECLARE
    CURSOR cur_customer_accounts(p_customer_id accounts.customer_id%TYPE) IS
        SELECT account_id, balance
        FROM accounts
        WHERE customer_id = p_customer_id;
BEGIN
    FOR r_account IN cur_customer_accounts(101) LOOP
        DBMS_OUTPUT.PUT_LINE(
            r_account.account_id || ': ' || r_account.balance
        );
    END LOOP;
END;
/
```

### Cursor FOR Loop

A cursor FOR loop is the cleanest way to loop through query rows when you do not need manual cursor control.

```sql
BEGIN
    FOR r_account IN (
        SELECT account_id, customer_id, balance
        FROM accounts
        WHERE balance < 0
    ) LOOP
        DBMS_OUTPUT.PUT_LINE(
            'Overdrawn account: ' || r_account.account_id
        );
    END LOOP;
END;
/
```

Why it is useful:

- Oracle opens the cursor automatically.
- Oracle fetches rows automatically.
- Oracle closes the cursor automatically.
- The loop record is declared automatically.

Interview answer:

Use a cursor FOR loop for simple row iteration. Use an explicit cursor when you need more control. Use set-based SQL or bulk processing for large changes.

## 10. Exception Handling

PL/SQL exception handling lets code recover from expected errors and log unexpected ones.

```sql
DECLARE
    v_balance accounts.balance%TYPE;
BEGIN
    SELECT balance
    INTO v_balance
    FROM accounts
    WHERE account_id = 1001;

    DBMS_OUTPUT.PUT_LINE('Balance: ' || v_balance);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('No account found');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('More than one account found');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
END;
/
```

### `NO_DATA_FOUND`

Raised when `SELECT INTO` returns zero rows.

```sql
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Customer does not exist');
```

### `TOO_MANY_ROWS`

Raised when `SELECT INTO` returns more than one row.

```sql
EXCEPTION
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Expected one row but found many');
```

### `WHEN OTHERS`

`WHEN OTHERS` catches all exceptions not already handled.

```sql
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error code: ' || SQLCODE);
        DBMS_OUTPUT.PUT_LINE('Error message: ' || SQLERRM);
        RAISE;
```

Best practice:

- Handle known errors explicitly.
- Use `WHEN OTHERS` for logging or cleanup.
- Usually re-raise with `RAISE;` after logging, especially in production code.
- Avoid swallowing errors silently.

Bad pattern:

```sql
EXCEPTION
    WHEN OTHERS THEN
        NULL;
END;
/
```

Why bad:

The code hides failures. In banking systems, hidden failures can cause reconciliation gaps, missing audit records, incorrect balances, or failed downstream jobs.

### User-Defined Exception

```sql
DECLARE
    e_negative_transfer EXCEPTION;
    v_amount NUMBER := -100;
BEGIN
    IF v_amount <= 0 THEN
        RAISE e_negative_transfer;
    END IF;
EXCEPTION
    WHEN e_negative_transfer THEN
        DBMS_OUTPUT.PUT_LINE('Transfer amount must be positive');
END;
/
```

### `RAISE_APPLICATION_ERROR`

Use `RAISE_APPLICATION_ERROR` to raise a custom Oracle error from PL/SQL.

```sql
BEGIN
    RAISE_APPLICATION_ERROR(-20001, 'Transfer amount exceeds daily limit');
END;
/
```

Custom error numbers must be between `-20000` and `-20999`.

## 11. Procedures

A procedure performs an action. It may accept parameters and may return values through `OUT` parameters, but it does not return a value like a function.

```sql
CREATE OR REPLACE PROCEDURE freeze_account (
    p_account_id IN accounts.account_id%TYPE,
    p_reason     IN VARCHAR2
) AS
BEGIN
    UPDATE accounts
    SET status = 'F',
        freeze_reason = p_reason,
        updated_at = SYSDATE
    WHERE account_id = p_account_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Account not found');
    END IF;
END;
/
```

Calling a procedure:

```sql
BEGIN
    freeze_account(1001, 'Suspicious activity review');
END;
/
```

Parameter modes:

- `IN`: caller passes value into procedure. This is the default.
- `OUT`: procedure assigns value back to caller.
- `IN OUT`: caller passes value in, procedure can modify it and return it.

Example with `OUT`:

```sql
CREATE OR REPLACE PROCEDURE get_account_balance (
    p_account_id IN accounts.account_id%TYPE,
    p_balance    OUT accounts.balance%TYPE
) AS
BEGIN
    SELECT balance
    INTO p_balance
    FROM accounts
    WHERE account_id = p_account_id;
END;
/
```

```sql
DECLARE
    v_balance accounts.balance%TYPE;
BEGIN
    get_account_balance(1001, v_balance);
    DBMS_OUTPUT.PUT_LINE('Balance: ' || v_balance);
END;
/
```

## 12. Functions

A function returns a value.

```sql
CREATE OR REPLACE FUNCTION get_available_balance (
    p_account_id IN accounts.account_id%TYPE
) RETURN NUMBER AS
    v_balance accounts.balance%TYPE;
    v_hold_amount NUMBER;
BEGIN
    SELECT balance, hold_amount
    INTO v_balance, v_hold_amount
    FROM accounts
    WHERE account_id = p_account_id;

    RETURN v_balance - NVL(v_hold_amount, 0);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/
```

Calling a function from PL/SQL:

```sql
DECLARE
    v_available_balance NUMBER;
BEGIN
    v_available_balance := get_available_balance(1001);
    DBMS_OUTPUT.PUT_LINE('Available balance: ' || v_available_balance);
END;
/
```

Calling a function from SQL:

```sql
SELECT get_available_balance(1001) AS available_balance
FROM dual;
```

Interview answer:

Use a procedure for actions. Use a function when a value should be returned. Be careful calling PL/SQL functions from SQL if they perform queries or procedural work for many rows, because that can become slow.

## 13. Packages

A package groups related PL/SQL objects.

Package specification:

```sql
CREATE OR REPLACE PACKAGE account_pkg AS
    PROCEDURE freeze_account (
        p_account_id IN accounts.account_id%TYPE,
        p_reason     IN VARCHAR2
    );

    FUNCTION get_available_balance (
        p_account_id IN accounts.account_id%TYPE
    ) RETURN NUMBER;
END account_pkg;
/
```

Package body:

```sql
CREATE OR REPLACE PACKAGE BODY account_pkg AS
    PROCEDURE freeze_account (
        p_account_id IN accounts.account_id%TYPE,
        p_reason     IN VARCHAR2
    ) AS
    BEGIN
        UPDATE accounts
        SET status = 'F',
            freeze_reason = p_reason,
            updated_at = SYSDATE
        WHERE account_id = p_account_id;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Account not found');
        END IF;
    END freeze_account;

    FUNCTION get_available_balance (
        p_account_id IN accounts.account_id%TYPE
    ) RETURN NUMBER AS
        v_balance accounts.balance%TYPE;
        v_hold_amount NUMBER;
    BEGIN
        SELECT balance, hold_amount
        INTO v_balance, v_hold_amount
        FROM accounts
        WHERE account_id = p_account_id;

        RETURN v_balance - NVL(v_hold_amount, 0);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
    END get_available_balance;
END account_pkg;
/
```

Calling package members:

```sql
BEGIN
    account_pkg.freeze_account(1001, 'Fraud review');
END;
/
```

```sql
SELECT account_pkg.get_available_balance(1001)
FROM dual;
```

Why packages matter in Oracle-heavy teams:

- They organize related database logic.
- The package specification is a public API.
- The package body hides implementation details.
- They reduce repeated code.
- They can hold shared constants, types, cursors, and private helper routines.
- Changing the package body often avoids invalidating callers if the specification does not change.

Interview answer:

A package has a specification and a body. The specification exposes the public procedures, functions, types, constants, and cursors. The body implements them and can contain private logic.

## 14. Triggers

A trigger runs automatically when a database event occurs.

Common trigger types:

- `BEFORE INSERT`
- `BEFORE UPDATE`
- `BEFORE DELETE`
- `AFTER INSERT`
- `AFTER UPDATE`
- `AFTER DELETE`
- Statement-level triggers.
- Row-level triggers using `FOR EACH ROW`.

Example audit trigger:

```sql
CREATE OR REPLACE TRIGGER trg_accounts_audit
AFTER UPDATE OF balance ON accounts
FOR EACH ROW
BEGIN
    INSERT INTO account_audit (
        account_id,
        old_balance,
        new_balance,
        changed_at
    ) VALUES (
        :OLD.account_id,
        :OLD.balance,
        :NEW.balance,
        SYSDATE
    );
END;
/
```

`:OLD` and `:NEW`:

- `:OLD.column_name`: value before the change.
- `:NEW.column_name`: value after the change.

Example validation trigger:

```sql
CREATE OR REPLACE TRIGGER trg_accounts_no_negative_limit
BEFORE INSERT OR UPDATE OF credit_limit ON accounts
FOR EACH ROW
BEGIN
    IF :NEW.credit_limit < 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Credit limit cannot be negative');
    END IF;
END;
/
```

Interview caution:

Triggers are powerful but can hide behavior. In large banking systems, hidden trigger logic can make debugging difficult. Use them for audit, validation, and cross-cutting database rules when appropriate, but avoid surprising business workflows that developers cannot easily trace.

## 15. Transactions: `COMMIT`, `ROLLBACK`, and `SAVEPOINT`

Oracle transactions group database changes into a unit of work. For money movement, never teach or implement an unconditional commit. Validate the source account, destination account, balance rule, and affected row counts before committing.

Safer transfer-style pattern:

```sql
DECLARE
    v_from_balance accounts.balance%TYPE;
    v_debit_rows   PLS_INTEGER;
    v_credit_rows  PLS_INTEGER;
BEGIN
    SELECT balance
    INTO v_from_balance
    FROM accounts
    WHERE account_id = 1001
    FOR UPDATE;

    IF v_from_balance < 500 THEN
        RAISE_APPLICATION_ERROR(-20010, 'Insufficient funds');
    END IF;

    UPDATE accounts
    SET balance = balance - 500
    WHERE account_id = 1001;

    v_debit_rows := SQL%ROWCOUNT;

    UPDATE accounts
    SET balance = balance + 500
    WHERE account_id = 2001;

    v_credit_rows := SQL%ROWCOUNT;

    IF v_debit_rows = 1 AND v_credit_rows = 1 THEN
        COMMIT;
    ELSE
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20011, 'Transfer failed validation');
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20012, 'Source account not found');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
```

### `COMMIT`

`COMMIT` permanently saves changes.

```sql
COMMIT;
```

### `ROLLBACK`

`ROLLBACK` undoes uncommitted changes.

```sql
ROLLBACK;
```

### `SAVEPOINT`

`SAVEPOINT` marks a point inside a transaction that you can roll back to.

```sql
DECLARE
    v_debit_rows  PLS_INTEGER;
    v_credit_rows PLS_INTEGER;
BEGIN
    SAVEPOINT before_transfer;

    UPDATE accounts
    SET balance = balance - 500
    WHERE account_id = 1001
      AND balance >= 500;

    v_debit_rows := SQL%ROWCOUNT;

    UPDATE accounts
    SET balance = balance + 500
    WHERE account_id = 2001
      AND v_debit_rows = 1;

    v_credit_rows := SQL%ROWCOUNT;

    IF v_debit_rows = 1 AND v_credit_rows = 1 THEN
        COMMIT;
    ELSE
        ROLLBACK TO before_transfer;
        RAISE_APPLICATION_ERROR(-20003, 'Transfer failed validation');
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
```

Important banking answer:

For money movement, debit and credit should be part of the same transaction. If credit fails, debit should not remain committed.

Practical transaction guidance:

- Commit at clear business transaction boundaries.
- Do not commit inside low-level reusable procedures unless that is an explicit design decision.
- Let the caller control the transaction when composing multiple operations.
- Roll back on unexpected failures.
- Be careful with triggers or procedures that commit independently.

## 16. Autonomous Transactions

An autonomous transaction runs independently from the caller's transaction.

Common use:

- Audit logging.
- Error logging.
- Operational trace records that must persist even if the main transaction rolls back.

Example:

```sql
CREATE OR REPLACE PROCEDURE log_error (
    p_module_name IN VARCHAR2,
    p_error_text  IN VARCHAR2
) AS
    PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
    INSERT INTO error_log (
        module_name,
        error_text,
        logged_at
    ) VALUES (
        p_module_name,
        p_error_text,
        SYSDATE
    );

    COMMIT;
END;
/
```

Using it:

```sql
BEGIN
    UPDATE accounts
    SET balance = balance - 500
    WHERE account_id = 1001;

    RAISE_APPLICATION_ERROR(-20004, 'Simulated failure');
EXCEPTION
    WHEN OTHERS THEN
        log_error('transfer_funds', SQLERRM);
        ROLLBACK;
        RAISE;
END;
/
```

What happens:

- The `accounts` update rolls back.
- The `error_log` insert commits because it is autonomous.

Interview answer:

Autonomous transactions are useful for independent audit or error logging, but they should be used carefully because they break the normal transaction flow. They can make consistency harder to reason about if used for business data.

## 17. Collections, `BULK COLLECT`, and `FORALL`

PL/SQL can process rows one at a time, but large row-by-row loops are slow because of context switching between SQL and PL/SQL engines.

Two key bulk tools:

- `BULK COLLECT`: fetch many rows into collections.
- `FORALL`: send many DML operations to SQL in bulk.

### Collection Type

```sql
DECLARE
    TYPE t_account_ids IS TABLE OF accounts.account_id%TYPE;
    v_account_ids t_account_ids;
BEGIN
    SELECT account_id
    BULK COLLECT INTO v_account_ids
    FROM accounts
    WHERE status = 'A';

    DBMS_OUTPUT.PUT_LINE('Fetched: ' || v_account_ids.COUNT);
END;
/
```

### `BULK COLLECT`

```sql
DECLARE
    TYPE t_account_rec IS RECORD (
        account_id accounts.account_id%TYPE,
        balance    accounts.balance%TYPE
    );

    TYPE t_account_tab IS TABLE OF t_account_rec;
    v_accounts t_account_tab;
BEGIN
    SELECT account_id, balance
    BULK COLLECT INTO v_accounts
    FROM accounts
    WHERE balance < 0;

    FOR i IN 1..v_accounts.COUNT LOOP
        DBMS_OUTPUT.PUT_LINE(
            v_accounts(i).account_id || ': ' || v_accounts(i).balance
        );
    END LOOP;
END;
/
```

### `BULK COLLECT` With `LIMIT`

Use `LIMIT` to avoid loading too many rows into memory.

```sql
DECLARE
    CURSOR cur_accounts IS
        SELECT account_id, balance
        FROM accounts
        WHERE status = 'A';

    TYPE t_account_tab IS TABLE OF cur_accounts%ROWTYPE;
    v_accounts t_account_tab;
BEGIN
    OPEN cur_accounts;

    LOOP
        FETCH cur_accounts
        BULK COLLECT INTO v_accounts
        LIMIT 500;

        EXIT WHEN v_accounts.COUNT = 0;

        FOR i IN 1..v_accounts.COUNT LOOP
            DBMS_OUTPUT.PUT_LINE(v_accounts(i).account_id);
        END LOOP;
    END LOOP;

    CLOSE cur_accounts;
END;
/
```

### `FORALL`

`FORALL` performs bulk DML.

```sql
DECLARE
    TYPE t_account_ids IS TABLE OF accounts.account_id%TYPE;
    v_account_ids t_account_ids;
BEGIN
    SELECT account_id
    BULK COLLECT INTO v_account_ids
    FROM accounts
    WHERE balance < -5000;

    FORALL i IN 1..v_account_ids.COUNT
        UPDATE accounts
        SET status = 'F',
            updated_at = SYSDATE
        WHERE account_id = v_account_ids(i);

    DBMS_OUTPUT.PUT_LINE('Rows updated: ' || SQL%ROWCOUNT);
END;
/
```

### `FORALL` With `SAVE EXCEPTIONS`

```sql
DECLARE
    TYPE t_account_ids IS TABLE OF accounts.account_id%TYPE;
    v_account_ids t_account_ids := t_account_ids(1001, 1002, 1003);
BEGIN
    FORALL i IN 1..v_account_ids.COUNT SAVE EXCEPTIONS
        UPDATE accounts
        SET status = 'R'
        WHERE account_id = v_account_ids(i);
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -24381 THEN
            FOR j IN 1..SQL%BULK_EXCEPTIONS.COUNT LOOP
                DBMS_OUTPUT.PUT_LINE(
                    'Failed index ' ||
                    SQL%BULK_EXCEPTIONS(j).ERROR_INDEX ||
                    ', error code ' ||
                    SQL%BULK_EXCEPTIONS(j).ERROR_CODE
                );
            END LOOP;
        ELSE
            RAISE;
        END IF;
END;
/
```

Interview answer:

`BULK COLLECT` improves fetch performance by retrieving multiple rows at once. `FORALL` improves DML performance by sending many bind values to SQL in one bulk operation. Use `LIMIT` with large result sets to protect memory.

## 18. Practical Example: Transfer Funds

This example demonstrates variables, `%TYPE`, transaction control, exceptions, and business validation.

This version shows a database-owned transaction, where the procedure commits or rolls back internally. This can be acceptable for a standalone batch or database API, but many service-oriented systems prefer caller-owned transactions so the Java service controls the final commit.

```sql
CREATE OR REPLACE PROCEDURE transfer_funds (
    p_from_account_id IN accounts.account_id%TYPE,
    p_to_account_id   IN accounts.account_id%TYPE,
    p_amount          IN NUMBER
) AS
    v_from_balance accounts.balance%TYPE;
    v_debit_rows   PLS_INTEGER;
    v_credit_rows  PLS_INTEGER;
BEGIN
    IF p_amount <= 0 THEN
        RAISE_APPLICATION_ERROR(-20010, 'Transfer amount must be positive');
    END IF;

    SELECT balance
    INTO v_from_balance
    FROM accounts
    WHERE account_id = p_from_account_id
    FOR UPDATE;

    IF v_from_balance < p_amount THEN
        RAISE_APPLICATION_ERROR(-20011, 'Insufficient funds');
    END IF;

    UPDATE accounts
    SET balance = balance - p_amount,
        updated_at = SYSDATE
    WHERE account_id = p_from_account_id;

    v_debit_rows := SQL%ROWCOUNT;

    UPDATE accounts
    SET balance = balance + p_amount,
        updated_at = SYSDATE
    WHERE account_id = p_to_account_id
      AND v_debit_rows = 1;

    v_credit_rows := SQL%ROWCOUNT;

    IF v_debit_rows <> 1 THEN
        RAISE_APPLICATION_ERROR(-20014, 'Debit account update failed');
    END IF;

    IF v_credit_rows <> 1 THEN
        RAISE_APPLICATION_ERROR(-20012, 'Destination account not found');
    END IF;

    INSERT INTO transfer_audit (
        from_account_id,
        to_account_id,
        amount,
        transferred_at
    ) VALUES (
        p_from_account_id,
        p_to_account_id,
        p_amount,
        SYSDATE
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20013, 'Source account not found');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
```

Interview discussion:

- `FOR UPDATE` locks the source row while checking balance.
- The debit, credit, and audit insert are one transaction.
- `ROLLBACK` prevents partial money movement.
- `NO_DATA_FOUND` handles missing source account.
- `v_debit_rows` and `v_credit_rows` verify both account updates before commit.
- In real systems, transaction ownership may be at service level rather than inside the procedure.

Caller-owned version:

```sql
CREATE OR REPLACE PROCEDURE transfer_funds_no_commit (
    p_from_account_id IN accounts.account_id%TYPE,
    p_to_account_id   IN accounts.account_id%TYPE,
    p_amount          IN accounts.balance%TYPE
) AS
    v_from_balance accounts.balance%TYPE;
    v_debit_rows   PLS_INTEGER;
    v_credit_rows  PLS_INTEGER;
BEGIN
    IF p_amount <= 0 THEN
        RAISE_APPLICATION_ERROR(-20010, 'Transfer amount must be positive');
    END IF;
    SELECT balance
    INTO v_from_balance
    FROM accounts
    WHERE account_id = p_from_account_id
    FOR UPDATE;

    IF v_from_balance < p_amount THEN
        RAISE_APPLICATION_ERROR(-20011, 'Insufficient funds');
    END IF;

    UPDATE accounts
    SET balance = balance - p_amount,
        updated_at = SYSDATE
    WHERE account_id = p_from_account_id;

    v_debit_rows := SQL%ROWCOUNT;

    UPDATE accounts
    SET balance = balance + p_amount,
        updated_at = SYSDATE
    WHERE account_id = p_to_account_id
      AND v_debit_rows = 1;

    v_credit_rows := SQL%ROWCOUNT;

    IF v_debit_rows <> 1 THEN
        RAISE_APPLICATION_ERROR(-20014, 'Debit account update failed');
    END IF;

    IF v_credit_rows <> 1 THEN
        RAISE_APPLICATION_ERROR(-20012, 'Destination account not found');
    END IF;

    INSERT INTO transfer_audit (
        from_account_id,
        to_account_id,
        amount,
        transferred_at
    ) VALUES (
        p_from_account_id,
        p_to_account_id,
        p_amount,
        SYSDATE
    );
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20013, 'Source account not found');
END;
/
```

Interview framing:

- If the stored procedure is the full business transaction boundary, internal `COMMIT` and `ROLLBACK` can be acceptable.
- If Java service code coordinates multiple database calls, message publishing, or external operations, avoid committing inside the procedure and let the caller own transaction control.
- Be explicit in interviews about which model you are using.

## 19. Practical Example: Monthly Fee Batch

This example shows cursor FOR loops and row-level processing. For very large batches, prefer set-based SQL or bulk DML.

```sql
BEGIN
    FOR r_account IN (
        SELECT account_id, balance
        FROM accounts
        WHERE status = 'A'
          AND account_type = 'CHECKING'
          AND balance < 1000
    ) LOOP
        UPDATE accounts
        SET balance = balance - 10,
            updated_at = SYSDATE
        WHERE account_id = r_account.account_id;

        INSERT INTO account_fee_audit (
            account_id,
            fee_amount,
            fee_reason,
            charged_at
        ) VALUES (
            r_account.account_id,
            10,
            'LOW_BALANCE_FEE',
            SYSDATE
        );
    END LOOP;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
```

Set-based alternative:

```sql
BEGIN
    INSERT INTO account_fee_audit (
        account_id,
        fee_amount,
        fee_reason,
        charged_at
    )
    SELECT account_id,
           10,
           'LOW_BALANCE_FEE',
           SYSDATE
    FROM accounts
    WHERE status = 'A'
      AND account_type = 'CHECKING'
      AND balance < 1000;

    UPDATE accounts
    SET balance = balance - 10,
        updated_at = SYSDATE
    WHERE status = 'A'
      AND account_type = 'CHECKING'
      AND balance < 1000;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
```

Interview point:

If the same rule can be expressed as set-based SQL, set-based SQL is usually faster and simpler than a cursor loop.

## 20. Performance Tips For PL/SQL Interviews

Strong Oracle/PLSQL performance answers usually include these points:

- Prefer set-based SQL over row-by-row loops.
- Use `BULK COLLECT` and `FORALL` when procedural bulk processing is required.
- Use `BULK COLLECT ... LIMIT` to control memory.
- Avoid committing inside every loop iteration.
- Use bind variables instead of dynamic string concatenation.
- Avoid unnecessary context switches between SQL and PL/SQL.
- Use `%TYPE` and `%ROWTYPE` to reduce datatype mismatch bugs.
- Keep transactions short enough to avoid excessive locks, but large enough to preserve business atomicity.
- Index columns used in joins, filters, and foreign key relationships when appropriate.
- Avoid functions on indexed columns in `WHERE` clauses unless function-based indexes exist.
- Check execution plans for slow SQL.
- Do not hide errors with `WHEN OTHERS THEN NULL`.
- Avoid triggers that perform heavy queries or complex cascading changes.
- Use packages to organize shared logic and avoid duplicated procedures.
- Consider result caching only when data stability and invalidation behavior are well understood.

Example of a non-sargable predicate:

```sql
SELECT *
FROM accounts
WHERE TRUNC(created_at) = DATE '2026-06-04';
```

Better range predicate:

```sql
SELECT *
FROM accounts
WHERE created_at >= DATE '2026-06-04'
  AND created_at <  DATE '2026-06-05';
```

## 21. Common Interview Questions With Answers

### 1. What is PL/SQL?

PL/SQL is Oracle's procedural extension to SQL. It lets developers write variables, conditions, loops, exceptions, procedures, functions, packages, and triggers around SQL operations.

### 2. What is the structure of a PL/SQL block?

A PL/SQL block has an optional `DECLARE` section, a mandatory `BEGIN` section, an optional `EXCEPTION` section, and an `END;`.

```sql
DECLARE
    v_value NUMBER;
BEGIN
    v_value := 10;
EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END;
/
```

### 3. What is `%TYPE`?

`%TYPE` anchors a variable to the datatype of a table column or another variable.

```sql
v_balance accounts.balance%TYPE;
```

It improves maintainability when schema datatypes change.

### 4. What is `%ROWTYPE`?

`%ROWTYPE` creates a record variable that can hold an entire row from a table or cursor.

```sql
r_account accounts%ROWTYPE;
```

### 5. What happens if `SELECT INTO` returns no rows?

Oracle raises `NO_DATA_FOUND`.

### 6. What happens if `SELECT INTO` returns multiple rows?

Oracle raises `TOO_MANY_ROWS`.

### 7. What is the difference between an implicit and explicit cursor?

An implicit cursor is automatically managed by Oracle for SQL statements. An explicit cursor is declared, opened, fetched, and closed by the developer.

### 8. Why use a cursor FOR loop?

A cursor FOR loop automatically opens, fetches, and closes the cursor. It is simpler and safer for straightforward row iteration.

### 9. What are cursor attributes?

Cursor attributes expose cursor state:

- `%FOUND`
- `%NOTFOUND`
- `%ROWCOUNT`
- `%ISOPEN`

For implicit cursors, use `SQL%ROWCOUNT`, `SQL%FOUND`, and `SQL%NOTFOUND`.

### 10. What is the difference between a procedure and a function?

A procedure performs an action and does not return a value directly. A function returns a value and can be used in expressions. Procedures can still return values through `OUT` parameters.

### 11. What is a package?

A package groups related PL/SQL objects. The specification exposes the public API, and the body contains implementation details.

### 12. Why are packages useful?

They organize code, hide private implementation, reduce duplication, support shared constants and types, and provide a stable API for callers.

### 13. What is a trigger?

A trigger is PL/SQL code that runs automatically when a database event happens, such as insert, update, or delete.

### 14. What are `:OLD` and `:NEW` in triggers?

In row-level triggers, `:OLD` refers to the previous row value and `:NEW` refers to the incoming row value.

### 15. What is `COMMIT`?

`COMMIT` permanently saves the current transaction.

### 16. What is `ROLLBACK`?

`ROLLBACK` undoes uncommitted changes.

### 17. What is `SAVEPOINT`?

`SAVEPOINT` marks a point inside a transaction so you can roll back part of the transaction.

### 18. What is an autonomous transaction?

An autonomous transaction commits or rolls back independently from the calling transaction. It is often used for audit or error logging.

### 19. Why can autonomous transactions be risky?

They can persist data even when the main business transaction rolls back. That is useful for logs but dangerous for business data if misused.

### 20. What is `BULK COLLECT`?

`BULK COLLECT` fetches multiple rows into a collection in one operation, reducing context switching.

### 21. What is `FORALL`?

`FORALL` sends multiple DML operations to SQL in bulk. It is faster than running one update, insert, or delete per loop iteration.

### 22. What is the main performance problem with cursor loops?

Row-by-row processing causes repeated context switches and can be slow for large data sets. Prefer set-based SQL or bulk processing.

### 23. Should you commit inside a loop?

Usually no. Committing inside every loop iteration can break transaction atomicity, slow processing, and make recovery harder. Commit at meaningful transaction boundaries.

### 24. What is wrong with `WHEN OTHERS THEN NULL`?

It hides failures. In banking systems, hidden failures can cause missing audit records, incorrect balances, failed reconciliation, and difficult production debugging.

### 25. How would you explain PL/SQL experience if your main SQL practice is PostgreSQL?

Say that the main prep is PostgreSQL-first, but you understand Oracle-specific PL/SQL concepts: block structure, `%TYPE`, `%ROWTYPE`, `SELECT INTO`, cursors, procedures, functions, packages, triggers, exception handling, transaction control, autonomous transactions, and bulk processing.

## 22. Quick Revision Sheet

```sql
-- Anonymous block
DECLARE
    v_name VARCHAR2(100);
BEGIN
    v_name := 'Oracle';
    DBMS_OUTPUT.PUT_LINE(v_name);
END;
/
```

```sql
-- Anchored variable
v_balance accounts.balance%TYPE;
```

```sql
-- Row variable
r_account accounts%ROWTYPE;
```

```sql
-- Single-row query
SELECT balance
INTO v_balance
FROM accounts
WHERE account_id = 1001;
```

```sql
-- Implicit cursor attribute
UPDATE accounts
SET status = 'F'
WHERE balance < 0;

DBMS_OUTPUT.PUT_LINE(SQL%ROWCOUNT);
```

```sql
-- Cursor FOR loop
FOR r_account IN (
    SELECT account_id, balance
    FROM accounts
) LOOP
    DBMS_OUTPUT.PUT_LINE(r_account.account_id);
END LOOP;
```

```sql
-- Exception handling
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('No row found');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Multiple rows found');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE(SQLERRM);
        RAISE;
```

```sql
-- Transaction control
SAVEPOINT before_change;
COMMIT;
ROLLBACK;
ROLLBACK TO before_change;
```

```sql
-- Autonomous transaction
PRAGMA AUTONOMOUS_TRANSACTION;
```

```sql
-- Bulk fetch
SELECT account_id
BULK COLLECT INTO v_account_ids
FROM accounts;
```

```sql
-- Bulk DML
FORALL i IN 1..v_account_ids.COUNT
    UPDATE accounts
    SET status = 'R'
    WHERE account_id = v_account_ids(i);
```

## 23. Advanced PL/SQL Topics For Senior Interviews

These topics are not always asked in Java backend interviews, but they are valuable for Oracle-heavy teams and senior roles.

### 23.1 REF CURSOR and SYS_REFCURSOR

A `REF CURSOR` is a pointer to a query result set that can be returned to a caller.

`SYS_REFCURSOR` is Oracle's built-in weak ref cursor type.

```sql
CREATE OR REPLACE PROCEDURE get_accounts_for_customer (
    p_customer_id IN accounts.customer_id%TYPE,
    p_result      OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_result FOR
        SELECT account_id, balance, status
        FROM accounts
        WHERE customer_id = p_customer_id;
END;
/
```

When to use:

- Returning result sets from stored procedures.
- Java applications calling Oracle procedures.
- Dynamic report-style queries.

Interview answer:

Use `SYS_REFCURSOR` when a PL/SQL procedure needs to return rows to a client. The caller is responsible for fetching from the cursor.

### 23.2 Dynamic SQL With EXECUTE IMMEDIATE

Dynamic SQL builds and runs SQL at runtime.

```sql
DECLARE
    v_sql VARCHAR2(1000);
    v_count NUMBER;
BEGIN
    v_sql := 'SELECT COUNT(*) FROM accounts WHERE status = :status';

    EXECUTE IMMEDIATE v_sql
        INTO v_count
        USING 'A';

    DBMS_OUTPUT.PUT_LINE('Active accounts: ' || v_count);
END;
/
```

Use dynamic SQL when:

- Table name, column name, or predicate shape is not known until runtime.
- You are writing generic admin or reporting utilities.

Avoid dynamic SQL when normal static SQL works. Static SQL is easier to validate, optimize, and secure.

### 23.3 SQL Injection Risk In Dynamic SQL

Bad dynamic SQL concatenates user input directly.

```sql
v_sql := 'SELECT * FROM accounts WHERE account_id = ' || p_account_id;
```

Better approach: bind variables.

```sql
EXECUTE IMMEDIATE
    'UPDATE accounts SET status = :status WHERE account_id = :account_id'
USING p_status, p_account_id;
```

Interview answer:

Use bind variables with `EXECUTE IMMEDIATE`. Never concatenate untrusted input into SQL strings.

### 23.4 Mutating Table Trigger Problem

A mutating table error can happen when a row-level trigger queries or modifies the same table that fired the trigger.

Example risk:

```sql
CREATE OR REPLACE TRIGGER trg_accounts_check
BEFORE UPDATE ON accounts
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM accounts;
END;
/
```

Why it happens:

Oracle prevents inconsistent reads of a table that is currently being changed row by row.

How to handle:

- Move logic out of the trigger.
- Use statement-level triggers if possible.
- Use compound triggers for advanced cases.
- Prefer explicit procedure logic for critical banking workflows.

### 23.5 Compound Triggers

Compound triggers let you define multiple timing sections in one trigger.

```sql
CREATE OR REPLACE TRIGGER trg_accounts_compound
FOR UPDATE ON accounts
COMPOUND TRIGGER
    TYPE t_account_ids IS TABLE OF accounts.account_id%TYPE;
    g_account_ids t_account_ids := t_account_ids();

    AFTER EACH ROW IS
    BEGIN
        g_account_ids.EXTEND;
        g_account_ids(g_account_ids.COUNT) := :NEW.account_id;
    END AFTER EACH ROW;

    AFTER STATEMENT IS
    BEGIN
        FOR i IN 1..g_account_ids.COUNT LOOP
            INSERT INTO account_audit(account_id, audit_message, created_at)
            VALUES (g_account_ids(i), 'Account updated', SYSDATE);
        END LOOP;
    END AFTER STATEMENT;
END;
/
```

Interview answer:

Compound triggers help collect row-level information and process it after the statement, which can avoid some mutating table problems.

### 23.6 Package Overloading, State, and Initialization

Packages can overload procedures and functions with the same name but different parameters.

```sql
CREATE OR REPLACE PACKAGE account_pkg AS
    PROCEDURE close_account(p_account_id IN NUMBER);
    PROCEDURE close_account(p_account_no IN VARCHAR2);
END account_pkg;
/
```

Package state:

Package-level variables can persist for a session.

```sql
CREATE OR REPLACE PACKAGE session_pkg AS
    g_request_id VARCHAR2(100);
END session_pkg;
/
```

Package initialization:

The package body can have an initialization block that runs when the package is first referenced in a session.

Interview caution:

Package state can surprise application developers because connection pools reuse database sessions. Avoid relying on package state for business correctness.

### 23.7 Invoker Rights vs Definer Rights

Definer rights means the procedure runs with the privileges of the schema that owns it. This is the default.

Invoker rights means it runs with the privileges of the caller.

```sql
CREATE OR REPLACE PROCEDURE show_accounts
AUTHID CURRENT_USER
AS
BEGIN
    NULL;
END;
/
```

Interview answer:

Use definer rights for controlled database APIs. Use invoker rights when behavior should depend on the caller's privileges. Security-sensitive banking systems should be explicit about this.

### 23.8 DBMS_SCHEDULER

`DBMS_SCHEDULER` runs scheduled database jobs.

Conceptual example:

```sql
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        job_name        => 'MONTHLY_FEE_JOB',
        job_type        => 'STORED_PROCEDURE',
        job_action      => 'CHARGE_MONTHLY_FEES',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=MONTHLY',
        enabled         => TRUE
    );
END;
/
```

Banking use cases:

- End-of-day processing
- Monthly fee jobs
- Reconciliation jobs
- Report refreshes

Interview answer:

Use scheduler jobs for database-side recurring tasks, but monitor failures, runtime, and retry behavior carefully.

### 23.9 Error Backtraces

`SQLERRM` gives the error message. `DBMS_UTILITY.FORMAT_ERROR_BACKTRACE` helps locate where the error happened.

```sql
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE(SQLERRM);
        DBMS_OUTPUT.PUT_LINE(DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
        RAISE;
```

Interview answer:

For production PL/SQL, log the error message and backtrace, then re-raise unless you are intentionally handling the error.

### 23.10 Collection Types

Common PL/SQL collection types:

- Associative array
- Nested table
- VARRAY

Associative array example:

```sql
DECLARE
    TYPE t_balance_map IS TABLE OF accounts.balance%TYPE INDEX BY PLS_INTEGER;
    v_balances t_balance_map;
BEGIN
    v_balances(1) := 1000;
    v_balances(2) := 2000;
END;
/
```

Use collections for:

- Bulk processing
- Passing lists of IDs
- Temporary in-memory data

### 23.11 FORALL With INDICES OF and VALUES OF

`INDICES OF` is useful when a collection is sparse.

```sql
FORALL i IN INDICES OF v_account_ids
    UPDATE accounts
    SET status = 'R'
    WHERE account_id = v_account_ids(i);
```

`VALUES OF` lets another collection define which indexes to process.

Interview answer:

Use `INDICES OF` or `VALUES OF` when collections are not dense from `1..COUNT`.

### 23.12 Pipelined Functions

A pipelined function returns rows progressively instead of building the entire result first.

Use cases:

- Transforming rows for reports
- Streaming generated rows
- Table-function style processing

Interview caution:

Pipelined functions are powerful but can be harder to debug and optimize than plain SQL.

### 23.13 Function Result Cache

Oracle can cache deterministic-ish function results using result cache.

Conceptual example:

```sql
CREATE OR REPLACE FUNCTION get_fee_rate (
    p_account_type IN VARCHAR2
) RETURN NUMBER
RESULT_CACHE
AS
    v_rate NUMBER;
BEGIN
    SELECT fee_rate
    INTO v_rate
    FROM fee_config
    WHERE account_type = p_account_type;

    RETURN v_rate;
END;
/
```

Use only when:

- Inputs are stable.
- Underlying data changes infrequently.
- You understand invalidation behavior.

Do not use result cache for rapidly changing balances or transactional state.

### 23.14 Testing and Debugging PL/SQL

Practical testing patterns:

- Test procedures with controlled seed data.
- Verify both success and failure paths.
- Test exception cases like `NO_DATA_FOUND`.
- Test transaction behavior with rollback.
- Use `DBMS_OUTPUT` only for learning or simple debugging.
- In production, log errors to audit/error tables carefully.

For teams using automated testing, PL/SQL logic can be tested with database test frameworks or integration tests from Java.

Senior interview answer:

I would keep PL/SQL units small, make transaction ownership clear, avoid hidden commits, test failure paths, and log errors with enough context to support production debugging.

## 24. Wells Fargo Interview Framing

When discussing PL/SQL with a Wells Fargo or Oracle-heavy banking team, frame your answers around correctness, transaction safety, auditability, and performance.

Good answer themes:

- Banking operations need atomic transactions.
- Hidden failures are dangerous.
- Audit and reconciliation matter.
- Set-based SQL is preferred for large data changes.
- PL/SQL packages can act as stable database APIs.
- Triggers should be used carefully because they hide behavior.
- Autonomous transactions are useful for logs, not normal business data.
- Bulk operations reduce context switching for large batches.

Short answer to memorize:

PL/SQL is Oracle's procedural language for writing database-side logic. I would use it for data-heavy operations, packages, stored procedures, validations, audit logic, and batch processing in Oracle systems. I would be careful with transaction boundaries, exception handling, triggers, and row-by-row loops because banking systems need correctness, auditability, and predictable performance.
