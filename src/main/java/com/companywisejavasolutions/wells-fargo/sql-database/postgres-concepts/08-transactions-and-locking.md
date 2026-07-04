# PostgreSQL Transactions and Locking

Transaction questions are very common in banking and fintech interviews because money movement is about correctness first and performance second. A good Wells Fargo-style answer should show that you understand ACID, isolation, row locks, rollback, retry, deadlock prevention, and safe transfer design.

PostgreSQL syntax is used throughout.

## Interview Mental Model

When a transaction question appears, answer in this order:

1. Define the correctness requirement.
2. Put related writes in one transaction.
3. Lock the right rows at the right time.
4. Validate business rules inside the transaction.
5. Commit only after all changes succeed.
6. Roll back on failure.
7. Retry only when the operation is idempotent and safe.

Banking principle: A transfer is not "debit first, credit later." It is one atomic business operation.

## Sample Tables

```sql
CREATE TABLE account (
    account_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    balance NUMERIC(12, 2) NOT NULL CHECK (balance >= 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    version INT NOT NULL DEFAULT 1,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transfer_request (
    transfer_id BIGINT PRIMARY KEY,
    from_account_id BIGINT NOT NULL REFERENCES account(account_id),
    to_account_id BIGINT NOT NULL REFERENCES account(account_id),
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ledger_entry (
    ledger_entry_id BIGINT PRIMARY KEY,
    transfer_id BIGINT NOT NULL REFERENCES transfer_request(transfer_id),
    account_id BIGINT NOT NULL REFERENCES account(account_id),
    entry_type VARCHAR(10) NOT NULL CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Note: In real banking systems, the ledger is often the source of truth and balances may be derived, cached, or updated as controlled projections. For interview examples, the `account.balance` column makes locking and transfer behavior easy to demonstrate.

## 1. ACID With Practical Examples

ACID describes reliable transaction behavior.

### Atomicity

All operations in a transaction succeed or all roll back.

Example: Debit account 101 and credit account 202. If the credit fails, the debit must not remain.

```sql
DO $$
DECLARE
    v_debit_rows INTEGER;
    v_credit_rows INTEGER;
BEGIN
    UPDATE account
    SET balance = balance - 100.00
    WHERE account_id = 101
      AND status = 'ACTIVE'
      AND balance >= 100.00;

    GET DIAGNOSTICS v_debit_rows = ROW_COUNT;
    IF v_debit_rows <> 1 THEN
        RAISE EXCEPTION 'debit failed, so credit must not run';
    END IF;

    UPDATE account
    SET balance = balance + 100.00
    WHERE account_id = 202
      AND status = 'ACTIVE';

    GET DIAGNOSTICS v_credit_rows = ROW_COUNT;
    IF v_credit_rows <> 1 THEN
        RAISE EXCEPTION 'credit failed, so debit rolls back';
    END IF;
END $$;
```

If a transaction fails before it is committed, use:

```sql
ROLLBACK;
```

### Consistency

The database moves from one valid state to another valid state.

Examples:

- Balances cannot be negative because of `CHECK (balance >= 0)`.
- Ledger entries must reference a valid transfer.
- A transfer amount must be greater than zero.
- Account status must be valid.

Correctness note: Consistency comes from both transaction boundaries and constraints. Do not rely only on application code for core financial rules.

### Isolation

Concurrent transactions should not incorrectly interfere with each other.

Example: Two withdrawals from the same account should not both read the same original balance and overdraw the account.

PostgreSQL uses MVCC, meaning readers and writers do not block each other in many normal cases. But writes to the same row still require locks.

### Durability

After `COMMIT`, data survives crashes.

PostgreSQL uses write-ahead logging (WAL). WAL records changes before data pages are flushed, enabling crash recovery and replication.

Interview answer: "ACID means the transfer is all-or-nothing, rules are preserved, concurrent requests cannot corrupt the result, and committed money movement is durable."

## 2. Basic Transaction Commands

Start:

```sql
BEGIN;
```

Commit:

```sql
COMMIT;
```

Rollback:

```sql
ROLLBACK;
```

Set isolation:

```sql
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;
```

Use a savepoint:

```sql
BEGIN;

UPDATE account
SET updated_at = CURRENT_TIMESTAMP
WHERE account_id = 101;

SAVEPOINT before_optional_audit;

INSERT INTO ledger_entry
    (ledger_entry_id, transfer_id, account_id, entry_type, amount)
VALUES
    (9001, 5001, 101, 'DEBIT', 100.00);

ROLLBACK TO SAVEPOINT before_optional_audit;

COMMIT;
```

Use case for savepoints: recover from a non-critical step inside a larger transaction. For core money movement, prefer clear all-or-nothing behavior unless there is a strong business reason.

## 3. Unsafe Transfer Example

This is unsafe because it does not check account status, balance, affected rows, idempotency, or locking order.

```sql
BEGIN;

UPDATE account
SET balance = balance - 100.00
WHERE account_id = 101;

-- Unsafe anti-pattern:
-- Some codebases immediately credit the destination here without checking
-- whether the debit updated exactly one valid source account.
-- Do not continue to the credit step unless the debit is validated.

ROLLBACK;
```

Problems:

- Account 101 may not exist.
- Account 202 may not exist.
- Account 101 may be frozen.
- Balance may become negative if no constraint exists.
- Retried requests may double-transfer money.
- Concurrent transfers may create lock waits or deadlocks.
- There is no ledger record for audit.

Interview answer: "This shows atomicity only at a basic level. A production banking transfer needs validation, row locking, idempotency, and audit entries."

## 4. Safe Transfer With Row Locks

Pessimistic locking means lock the rows before updating them.

```sql
DO $$
DECLARE
    v_locked_rows INTEGER;
    v_request_rows INTEGER;
    v_debit_rows INTEGER;
    v_credit_rows INTEGER;
BEGIN
    INSERT INTO transfer_request
        (transfer_id, from_account_id, to_account_id, amount, idempotency_key, status)
    VALUES
        (5001, 101, 202, 100.00, 'external-request-abc-123', 'PENDING');

    GET DIAGNOSTICS v_request_rows = ROW_COUNT;
    IF v_request_rows <> 1 THEN
        RAISE EXCEPTION 'transfer request was not created';
    END IF;

    PERFORM account_id
    FROM account
    WHERE account_id IN (101, 202)
    ORDER BY account_id
    FOR UPDATE;

    GET DIAGNOSTICS v_locked_rows = ROW_COUNT;
    IF v_locked_rows <> 2 THEN
        RAISE EXCEPTION 'source or destination account is missing';
    END IF;

    UPDATE account
    SET balance = balance - 100.00,
        version = version + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = 101
      AND status = 'ACTIVE'
      AND balance >= 100.00;

    GET DIAGNOSTICS v_debit_rows = ROW_COUNT;
    IF v_debit_rows <> 1 THEN
        RAISE EXCEPTION 'debit failed because account is inactive or balance is insufficient';
    END IF;

    UPDATE account
    SET balance = balance + 100.00,
        version = version + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = 202
      AND status = 'ACTIVE';

    GET DIAGNOSTICS v_credit_rows = ROW_COUNT;
    IF v_credit_rows <> 1 THEN
        RAISE EXCEPTION 'credit failed because destination account is inactive';
    END IF;

    INSERT INTO ledger_entry
        (ledger_entry_id, transfer_id, account_id, entry_type, amount)
    VALUES
        (9001, 5001, 101, 'DEBIT', 100.00),
        (9002, 5001, 202, 'CREDIT', 100.00);

    UPDATE transfer_request
    SET status = 'COMPLETED'
    WHERE transfer_id = 5001;
END $$;
```

Why it is safer:

- Both account rows are locked before updates.
- Rows are locked in deterministic `account_id` order.
- Debit checks `status = 'ACTIVE'` and `balance >= 100.00`.
- Credit checks destination account status.
- Row counts are validated immediately after each important write.
- If any validation fails, `RAISE EXCEPTION` stops the block and PostgreSQL rolls back the block's changes.
- Ledger entries are inserted in the same transaction.
- The idempotency key prevents accidental duplicate requests.

Important implementation note: In application code, do the same thing shown in the block: check affected row counts before moving to the next step. Never credit the destination or insert ledger entries after a failed debit.

## 5. Safer Transfer With Idempotency First

A common production approach is to insert the transfer request first using a unique idempotency key.

```sql
DO $$
DECLARE
    v_request_rows INTEGER;
    v_locked_rows INTEGER;
    v_debit_rows INTEGER;
    v_credit_rows INTEGER;
BEGIN
    INSERT INTO transfer_request
        (transfer_id, from_account_id, to_account_id, amount, idempotency_key, status)
    VALUES
        (5002, 101, 202, 100.00, 'external-request-def-456', 'PENDING')
    ON CONFLICT (idempotency_key) DO NOTHING;

    GET DIAGNOSTICS v_request_rows = ROW_COUNT;
    IF v_request_rows = 0 THEN
        RAISE NOTICE 'duplicate request; return the existing transfer status instead of posting again';
        RETURN;
    END IF;

    PERFORM account_id
    FROM account
    WHERE account_id IN (101, 202)
    ORDER BY account_id
    FOR UPDATE;

    GET DIAGNOSTICS v_locked_rows = ROW_COUNT;
    IF v_locked_rows <> 2 THEN
        RAISE EXCEPTION 'source or destination account is missing';
    END IF;

    UPDATE account
    SET balance = balance - 100.00,
        version = version + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = 101
      AND status = 'ACTIVE'
      AND balance >= 100.00;

    GET DIAGNOSTICS v_debit_rows = ROW_COUNT;
    IF v_debit_rows <> 1 THEN
        RAISE EXCEPTION 'debit failed; do not continue to credit or ledger insert';
    END IF;

    UPDATE account
    SET balance = balance + 100.00,
        version = version + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE account_id = 202
      AND status = 'ACTIVE';

    GET DIAGNOSTICS v_credit_rows = ROW_COUNT;
    IF v_credit_rows <> 1 THEN
        RAISE EXCEPTION 'credit failed; rollback debit and request';
    END IF;

    INSERT INTO ledger_entry
        (ledger_entry_id, transfer_id, account_id, entry_type, amount)
    VALUES
        (9003, 5002, 101, 'DEBIT', 100.00),
        (9004, 5002, 202, 'CREDIT', 100.00);

    UPDATE transfer_request
    SET status = 'COMPLETED'
    WHERE transfer_id = 5002;
END $$;
```

If the same request is retried, the unique `idempotency_key` prevents creating a second transfer.

Alternate PostgreSQL pattern:

```sql
INSERT INTO transfer_request
    (transfer_id, from_account_id, to_account_id, amount, idempotency_key, status)
VALUES
    (5002, 101, 202, 100.00, 'external-request-def-456', 'PENDING')
ON CONFLICT (idempotency_key) DO NOTHING;
```

Application rule: If `ON CONFLICT DO NOTHING` inserts zero rows, fetch the existing transfer by `idempotency_key` and return its current status instead of posting again.

## 6. Rollback Examples

Manual rollback:

```sql
BEGIN;

UPDATE account
SET balance = balance - 100.00
WHERE account_id = 101;

ROLLBACK;
```

The debit is canceled.

Rollback on failed validation:

```sql
BEGIN;

SELECT account_id, balance, status
FROM account
WHERE account_id = 101
FOR UPDATE;

-- Application checks: balance is too low or account is frozen.

ROLLBACK;
```

Rollback after constraint failure:

```sql
BEGIN;

UPDATE account
SET balance = -10.00
WHERE account_id = 101;

-- CHECK (balance >= 0) fails.
-- The transaction is now in failed state.

ROLLBACK;
```

PostgreSQL note: After an error inside a transaction, the transaction is usually marked aborted. You must roll back before issuing normal commands again, unless you used savepoints for controlled recovery.

## 7. Isolation Levels In PostgreSQL

PostgreSQL supports:

- `READ COMMITTED`
- `REPEATABLE READ`
- `SERIALIZABLE`

PostgreSQL accepts `READ UNCOMMITTED`, but treats it like `READ COMMITTED`.

### READ COMMITTED

Default isolation level.

```sql
BEGIN TRANSACTION ISOLATION LEVEL READ COMMITTED;

SELECT balance
FROM account
WHERE account_id = 101;

COMMIT;
```

Behavior:

- Each statement sees data committed before that statement starts.
- A repeated `SELECT` can see new committed data from another transaction.
- Dirty reads are prevented.

Banking use case: Many simple OLTP updates work well at `READ COMMITTED` when updates are written atomically and row locks protect critical rows.

### REPEATABLE READ

```sql
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;

SELECT balance
FROM account
WHERE account_id = 101;

SELECT balance
FROM account
WHERE account_id = 101;

COMMIT;
```

Behavior:

- The transaction sees a stable snapshot.
- Repeated reads return the same committed view.
- PostgreSQL's `REPEATABLE READ` prevents dirty reads, non-repeatable reads, and phantom reads for normal snapshot reads.

Use case: A statement generation job needs a consistent view of account activity while other transactions continue.

Tradeoff: Long repeatable-read transactions can retain old row versions and increase vacuum pressure.

### SERIALIZABLE

```sql
BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;

SELECT COALESCE(SUM(amount), 0) AS daily_debit_total
FROM ledger_entry
WHERE account_id = 101
  AND entry_type = 'DEBIT'
  AND created_at >= CURRENT_DATE;

-- Application decides whether another debit is allowed.

COMMIT;
```

Behavior:

- PostgreSQL tries to make concurrent transactions behave as if they ran one at a time.
- It can abort a transaction with a serialization failure if concurrent behavior would be unsafe.

Use case: Complex business rules based on aggregate reads, such as daily withdrawal limits, can require `SERIALIZABLE` or a design that locks a summary row.

Important: Applications must retry serialization failures safely.

## 8. Read Phenomena

### Dirty Read

A dirty read means reading uncommitted data from another transaction.

PostgreSQL prevents dirty reads even when `READ UNCOMMITTED` is requested.

### Non-Repeatable Read

A non-repeatable read happens when the same row is read twice and the value changes because another transaction committed an update.

At `READ COMMITTED`, this can happen:

```sql
BEGIN;

SELECT balance
FROM account
WHERE account_id = 101;

-- Another transaction commits an update to account 101.

SELECT balance
FROM account
WHERE account_id = 101;

COMMIT;
```

At `REPEATABLE READ`, the second read sees the same snapshot.

### Phantom Read

A phantom read happens when a repeated search returns newly inserted rows from another committed transaction.

Example:

```sql
BEGIN;

SELECT COUNT(*)
FROM ledger_entry
WHERE account_id = 101
  AND created_at >= CURRENT_DATE;

-- Another transaction inserts a matching ledger row and commits.

SELECT COUNT(*)
FROM ledger_entry
WHERE account_id = 101
  AND created_at >= CURRENT_DATE;

COMMIT;
```

At `READ COMMITTED`, the count can change. PostgreSQL `REPEATABLE READ` prevents this for normal snapshot reads. Use `SERIALIZABLE` when concurrent transactions must be equivalent to one-at-a-time execution.

## 9. Pessimistic Locking

Pessimistic locking locks rows before making decisions.

```sql
BEGIN;

SELECT account_id, balance
FROM account
WHERE account_id = 101
FOR UPDATE;

UPDATE account
SET balance = balance - 100.00
WHERE account_id = 101
  AND balance >= 100.00;

COMMIT;
```

Use cases:

- Core balance updates.
- Transfers between accounts.
- Account closure.
- Payment capture when only one process may finalize a payment.

Variants:

```sql
SELECT account_id
FROM account
WHERE account_id = 101
FOR NO KEY UPDATE;
```

```sql
SELECT account_id
FROM account
WHERE account_id = 101
FOR SHARE;
```

```sql
SELECT account_id
FROM account
WHERE account_id = 101
FOR UPDATE NOWAIT;
```

```sql
SELECT account_id
FROM account
WHERE account_id = 101
FOR UPDATE SKIP LOCKED;
```

Practical meaning:

- `FOR UPDATE`: strong row lock before update/delete.
- `FOR NO KEY UPDATE`: useful when updating non-key columns.
- `FOR SHARE`: shared lock for rows that should not be changed by others during the transaction.
- `NOWAIT`: fail immediately instead of waiting.
- `SKIP LOCKED`: useful for job queues, not for silently skipping bank accounts in a transfer.

Banking warning: `SKIP LOCKED` is good for picking the next notification job. It is usually wrong for account transfer correctness because skipping a locked account can hide a required operation.

## 10. Optimistic Locking

Optimistic locking assumes conflicts are rare and detects changes at update time.

```sql
UPDATE account
SET balance = balance - 100.00,
    version = version + 1,
    updated_at = CURRENT_TIMESTAMP
WHERE account_id = 101
  AND version = 5
  AND balance >= 100.00;
```

If the update affects zero rows, possible reasons:

- The account does not exist.
- The version changed.
- The balance is insufficient.

Use cases:

- Profile updates.
- Account preferences.
- Low-contention workflows.

Core banking caution: For high-contention balance updates, pessimistic row locking or atomic conditional updates are often easier to reason about.

Alternate atomic update:

```sql
UPDATE account
SET balance = balance - 100.00,
    version = version + 1,
    updated_at = CURRENT_TIMESTAMP
WHERE account_id = 101
  AND status = 'ACTIVE'
  AND balance >= 100.00;
```

This avoids a separate read-before-write for the debit condition, but a transfer still needs both debit and credit handled atomically.

## 11. Lost Update Problem

Lost update happens when two transactions read the same value and overwrite each other.

Unsafe pattern:

```sql
-- Application reads balance = 1000.00

UPDATE account
SET balance = 900.00
WHERE account_id = 101;
```

If two requests both read `1000.00`, one sets `900.00` and another sets `800.00` based on stale application state. One update may overwrite the other calculation.

Better:

```sql
UPDATE account
SET balance = balance - 100.00
WHERE account_id = 101
  AND balance >= 100.00;
```

Why:

- The arithmetic happens in the database against the current row value.
- PostgreSQL locks the row during the update.
- The balance check is part of the update.

Interview answer: "Avoid read-modify-write in application memory for balances. Use atomic SQL updates or row locks."

## 12. Deadlocks

A deadlock happens when transactions wait for each other in a cycle.

Example:

- Transaction A locks account 101 and waits for account 202.
- Transaction B locks account 202 and waits for account 101.

PostgreSQL detects deadlocks and aborts one transaction.

Problem sequence:

```sql
-- Transaction A
BEGIN;
SELECT * FROM account WHERE account_id = 101 FOR UPDATE;
SELECT * FROM account WHERE account_id = 202 FOR UPDATE;
COMMIT;
```

```sql
-- Transaction B
BEGIN;
SELECT * FROM account WHERE account_id = 202 FOR UPDATE;
SELECT * FROM account WHERE account_id = 101 FOR UPDATE;
COMMIT;
```

If both transactions get their first lock, each waits for the other.

## 13. Avoid Deadlocks With Locking Order

Always lock shared resources in a consistent order.

```sql
DO $$
DECLARE
    v_locked_rows INTEGER;
    v_debit_rows INTEGER;
    v_credit_rows INTEGER;
BEGIN
    PERFORM account_id
    FROM account
    WHERE account_id IN (101, 202)
    ORDER BY account_id
    FOR UPDATE;

    GET DIAGNOSTICS v_locked_rows = ROW_COUNT;
    IF v_locked_rows <> 2 THEN
        RAISE EXCEPTION 'both accounts must exist before transfer';
    END IF;

    UPDATE account
    SET balance = balance - 100.00
    WHERE account_id = 101
      AND status = 'ACTIVE'
      AND balance >= 100.00;

    GET DIAGNOSTICS v_debit_rows = ROW_COUNT;
    IF v_debit_rows <> 1 THEN
        RAISE EXCEPTION 'debit failed, so destination credit must not run';
    END IF;

    UPDATE account
    SET balance = balance + 100.00
    WHERE account_id = 202
      AND status = 'ACTIVE';

    GET DIAGNOSTICS v_credit_rows = ROW_COUNT;
    IF v_credit_rows <> 1 THEN
        RAISE EXCEPTION 'credit failed, so debit rolls back';
    END IF;
END $$;
```

Why this helps:

- Every transfer locks the lower account ID first.
- Competing transfers wait in line instead of forming a cycle.
- The order is deterministic and easy to enforce.

Good practices:

- Lock rows in the same order everywhere.
- Keep transactions short.
- Avoid remote API calls inside transactions.
- Avoid user input while a transaction is open.
- Update tables in a consistent order across services.
- Add retry logic for deadlock and serialization failures.

Interview answer: "Deadlocks are not always bugs in PostgreSQL; they can happen under concurrency. The application must minimize them through lock ordering and handle retries safely."

## 14. Lock Waits Versus Deadlocks

Lock wait: one transaction waits for another to finish.

Deadlock: two or more transactions wait for each other in a cycle.

Example lock wait:

```sql
-- Transaction A
BEGIN;
UPDATE account
SET balance = balance - 100.00
WHERE account_id = 101;
-- Transaction remains open.
```

```sql
-- Transaction B waits until A commits or rolls back.
UPDATE account
SET balance = balance - 50.00
WHERE account_id = 101;
```

Operational notes:

- Long transactions can block critical updates.
- Idle transactions can hold locks.
- Lock waits can look like slow queries.
- Timeouts should be set thoughtfully for OLTP services.

Useful settings:

```sql
SET lock_timeout = '3s';
SET statement_timeout = '30s';
```

Use timeouts carefully. A timeout aborts the statement and may require rolling back the transaction.

## 15. Table Locks

Most application code should rely on row-level locks, but table locks exist.

```sql
BEGIN;

LOCK TABLE account IN SHARE MODE;

COMMIT;
```

Use cases:

- Controlled maintenance.
- Rare administrative operations.
- Schema changes.

Banking caution: Avoid unnecessary table locks on hot OLTP tables such as accounts, payments, and transactions. They can block customer-facing operations.

## 16. Advisory Locks

PostgreSQL advisory locks are application-defined locks.

```sql
SELECT pg_advisory_xact_lock(101);
```

Transaction-scoped advisory lock example:

```sql
BEGIN;

SELECT pg_advisory_xact_lock(101);

-- Do account-related work.

COMMIT;
```

Use cases:

- Coordinating work where no single row naturally represents the lock.
- Preventing concurrent processing of the same business key.

Caution:

- PostgreSQL does not know what the advisory lock means.
- All application paths must follow the same convention.
- Prefer row locks when the row exists and represents the resource.

## 17. Retry Strategy

Some transaction failures should be retried:

- Deadlock detected.
- Serialization failure.
- Temporary lock timeout, depending on business context.

Do not blindly retry:

- Non-idempotent external calls.
- Duplicate transfer requests without idempotency keys.
- Validation failures such as insufficient funds.

Safe retry requirements:

- Use idempotency keys.
- Keep external side effects outside the database transaction or make them idempotent.
- Re-read current state after retry begins.
- Use bounded retries with backoff.
- Log enough context for audit.

Banking example: If a transfer request times out after the database commit but before the API response reaches the client, the retry should return the existing completed transfer, not post a new one.

## 18. Ledger Design And Transactions

For auditability, money movement should create immutable ledger entries.

```sql
DO $$
DECLARE
    v_locked_rows INTEGER;
    v_debit_rows INTEGER;
    v_credit_rows INTEGER;
    v_ledger_rows INTEGER;
BEGIN
    INSERT INTO transfer_request
        (transfer_id, from_account_id, to_account_id, amount, idempotency_key, status)
    VALUES
        (5003, 101, 202, 100.00, 'external-request-ghi-789', 'PENDING');

    PERFORM account_id
    FROM account
    WHERE account_id IN (101, 202)
    ORDER BY account_id
    FOR UPDATE;

    GET DIAGNOSTICS v_locked_rows = ROW_COUNT;
    IF v_locked_rows <> 2 THEN
        RAISE EXCEPTION 'both accounts must exist before posting ledger entries';
    END IF;

    UPDATE account
    SET balance = balance - 100.00
    WHERE account_id = 101
      AND status = 'ACTIVE'
      AND balance >= 100.00;

    GET DIAGNOSTICS v_debit_rows = ROW_COUNT;
    IF v_debit_rows <> 1 THEN
        RAISE EXCEPTION 'debit failed, so no ledger entries are posted';
    END IF;

    UPDATE account
    SET balance = balance + 100.00
    WHERE account_id = 202
      AND status = 'ACTIVE';

    GET DIAGNOSTICS v_credit_rows = ROW_COUNT;
    IF v_credit_rows <> 1 THEN
        RAISE EXCEPTION 'credit failed, so debit and request are rolled back';
    END IF;

    INSERT INTO ledger_entry
        (ledger_entry_id, transfer_id, account_id, entry_type, amount)
    VALUES
        (9005, 5003, 101, 'DEBIT', 100.00),
        (9006, 5003, 202, 'CREDIT', 100.00);

    GET DIAGNOSTICS v_ledger_rows = ROW_COUNT;
    IF v_ledger_rows <> 2 THEN
        RAISE EXCEPTION 'a balanced transfer must create exactly two ledger entries';
    END IF;

    UPDATE transfer_request
    SET status = 'COMPLETED'
    WHERE transfer_id = 5003;
END $$;
```

Correctness rules:

- Ledger entries should be append-only.
- Corrections should use reversal entries, not updates to posted entries.
- Debit and credit entries should share a transfer ID.
- The sum of entries for a transfer should balance conceptually.
- Ledger entries should be posted only after debit and credit validations pass.

Interview follow-up: "Would you store balance or derive it?"

Answer: Both patterns exist. A ledger is the audit source of truth. A balance table can be maintained transactionally for fast reads, but it must be kept consistent with the ledger.

## 19. Constraints Inside Transactions

Database constraints are part of correctness.

Examples:

```sql
ALTER TABLE account
ADD CONSTRAINT chk_account_balance_non_negative
CHECK (balance >= 0);
```

```sql
ALTER TABLE transfer_request
ADD CONSTRAINT ux_transfer_idempotency_key
UNIQUE (idempotency_key);
```

```sql
ALTER TABLE ledger_entry
ADD CONSTRAINT chk_ledger_amount_positive
CHECK (amount > 0);
```

Why it matters:

- Application bugs happen.
- Multiple services may write to the database.
- Constraints protect the source of truth.

Performance note: Constraints and indexes add write cost, but for financial correctness they are usually non-negotiable.

## 20. Long Transactions And MVCC

PostgreSQL uses MVCC, so updates create new row versions. Long-running transactions can prevent cleanup of old versions.

Problems caused by long transactions:

- Table bloat.
- Index bloat.
- Vacuum delays.
- Replication lag.
- Lock waits.
- Stale snapshots.

Avoid:

- Keeping a transaction open while calling another service.
- Waiting for user confirmation inside a transaction.
- Running huge batch updates in one transaction without planning.

Better batch pattern:

```sql
UPDATE account
SET updated_at = CURRENT_TIMESTAMP
WHERE account_id >= 1
  AND account_id < 10001;
```

Run batches in controlled chunks and commit between chunks when business rules allow.

Banking note: For settlement and reconciliation jobs, batch boundaries must be chosen carefully so partial progress is auditable and resumable.

## 21. Read-Only Transactions

Use read-only transactions for consistent reporting.

```sql
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ READ ONLY;

SELECT account_id, balance
FROM account
WHERE customer_id = 501;

SELECT account_id, COUNT(*) AS ledger_count
FROM ledger_entry
WHERE account_id IN (
    SELECT account_id
    FROM account
    WHERE customer_id = 501
)
GROUP BY account_id;

COMMIT;
```

Use case: Generate a consistent customer statement snapshot while writes continue.

Tradeoff: Keep it short enough to avoid MVCC cleanup pressure.

## 22. Alternate Approaches

### Approach A: Pessimistic Row Locks

Best when:

- Conflicts are likely.
- Correctness is critical.
- The same account may receive concurrent debits.

Tradeoff: Requests may wait for locks.

### Approach B: Atomic Conditional Updates

```sql
UPDATE account
SET balance = balance - 100.00
WHERE account_id = 101
  AND balance >= 100.00
  AND status = 'ACTIVE';
```

Best when:

- The update affects one row.
- The business rule can be expressed in the `WHERE` clause.

Tradeoff: Multi-row transfers still need a transaction and careful handling.

### Approach C: Optimistic Locking

```sql
UPDATE account
SET balance = balance - 100.00,
    version = version + 1
WHERE account_id = 101
  AND version = 5;
```

Best when:

- Conflicts are rare.
- The user can retry.
- The operation is not a high-contention money-posting path.

Tradeoff: More retries under contention.

### Approach D: Serializable Transactions

Best when:

- The transaction depends on aggregate conditions.
- You need one-at-a-time behavior without manually locking all possible rows.

Tradeoff: PostgreSQL may abort transactions with serialization failures, so retry logic is required.

### Approach E: Ledger-First Design

Best when:

- Auditability is central.
- Corrections must be traceable.
- Historical truth matters.

Tradeoff: Balance reads may require projections, summaries, or carefully maintained balance tables.

## 23. Banking Scenarios

### ATM Withdrawal

Requirements:

- Account must be active.
- Balance must be sufficient.
- Debit must be atomic.
- Ledger entry must be created.
- Duplicate ATM messages must not double debit.

Assumption: account `999999` is an internal cash or ATM settlement account that exists in the `account` table.

Core pattern:

```sql
DO $$
DECLARE
    v_request_rows INTEGER;
    v_locked_rows INTEGER;
    v_debit_rows INTEGER;
    v_ledger_rows INTEGER;
BEGIN
    INSERT INTO transfer_request
        (transfer_id, from_account_id, to_account_id, amount, idempotency_key, status)
    VALUES
        (6001, 101, 999999, 200.00, 'atm-msg-001', 'PENDING')
    ON CONFLICT (idempotency_key) DO NOTHING;

    GET DIAGNOSTICS v_request_rows = ROW_COUNT;
    IF v_request_rows = 0 THEN
        RAISE NOTICE 'duplicate ATM message; return existing withdrawal status';
        RETURN;
    END IF;

    PERFORM account_id
    FROM account
    WHERE account_id = 101
    FOR UPDATE;

    GET DIAGNOSTICS v_locked_rows = ROW_COUNT;
    IF v_locked_rows <> 1 THEN
        RAISE EXCEPTION 'account does not exist';
    END IF;

    UPDATE account
    SET balance = balance - 200.00
    WHERE account_id = 101
      AND status = 'ACTIVE'
      AND balance >= 200.00;

    GET DIAGNOSTICS v_debit_rows = ROW_COUNT;
    IF v_debit_rows <> 1 THEN
        RAISE EXCEPTION 'withdrawal failed because account is inactive or balance is insufficient';
    END IF;

    INSERT INTO ledger_entry
        (ledger_entry_id, transfer_id, account_id, entry_type, amount)
    VALUES
        (9101, 6001, 101, 'DEBIT', 200.00);

    GET DIAGNOSTICS v_ledger_rows = ROW_COUNT;
    IF v_ledger_rows <> 1 THEN
        RAISE EXCEPTION 'withdrawal ledger entry was not created';
    END IF;

    UPDATE transfer_request
    SET status = 'COMPLETED'
    WHERE transfer_id = 6001;
END $$;
```

### Account Freeze During Transfer

If an account is frozen while another transaction is transferring money, the transfer must lock and check status inside its transaction.

```sql
UPDATE account
SET balance = balance - 100.00
WHERE account_id = 101
  AND status = 'ACTIVE'
  AND balance >= 100.00;
```

Correctness point: Do not check status only before the transaction starts. Check it in the locked transaction or inside the conditional update.

### Daily Withdrawal Limit

Aggregate rule:

```sql
SELECT COALESCE(SUM(amount), 0) AS today_debits
FROM ledger_entry
WHERE account_id = 101
  AND entry_type = 'DEBIT'
  AND created_at >= CURRENT_DATE;
```

Risk: Two concurrent withdrawals may both see room under the limit.

Safer options:

- Use `SERIALIZABLE` and retry serialization failures.
- Maintain and lock a daily account limit row.
- Use an atomic update on a summary table with a conditional limit check.

Summary-row pattern:

```sql
CREATE TABLE account_daily_limit (
    account_id BIGINT NOT NULL,
    limit_date DATE NOT NULL,
    used_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    PRIMARY KEY (account_id, limit_date)
);
```

```sql
BEGIN;

UPDATE account_daily_limit
SET used_amount = used_amount + 100.00
WHERE account_id = 101
  AND limit_date = CURRENT_DATE
  AND used_amount + 100.00 <= 1000.00;

-- If one row updated, continue with account debit and ledger insert.

COMMIT;
```

## 24. Common Mistakes

- Reading balance in application code and writing back a calculated value.
- Updating debit and credit outside a transaction.
- Checking balance before the transaction but not inside it.
- Locking accounts in inconsistent order.
- Making remote service calls while holding database locks.
- Retrying transfers without idempotency.
- Ignoring zero-row updates.
- Treating lock timeouts as successful operations.
- Using `SKIP LOCKED` for money movement.
- Running long report transactions on hot primary databases without planning.

## 25. Interview Follow-Ups

### Why should debit and credit be in one transaction?

Because the transfer is one atomic business operation. If debit commits and credit fails, money disappears from one account without appearing in the other.

### How do you prevent overdraft under concurrency?

Use a row lock or an atomic conditional update:

```sql
UPDATE account
SET balance = balance - 100.00
WHERE account_id = 101
  AND balance >= 100.00;
```

Then verify that one row was updated.

### What isolation level does PostgreSQL use by default?

`READ COMMITTED`.

### Does PostgreSQL allow dirty reads?

No. PostgreSQL treats `READ UNCOMMITTED` as `READ COMMITTED`.

### What is the difference between non-repeatable read and phantom read?

Non-repeatable read is the same row changing between reads. Phantom read is a repeated search returning a different set of rows because matching rows were inserted or removed.

### When would you use `SERIALIZABLE`?

Use it when correctness depends on aggregate or range conditions and concurrent transactions must behave as if they ran one at a time. Be ready to retry serialization failures.

### How do you avoid deadlocks in transfers?

Lock accounts in a consistent order, such as ascending `account_id`, keep transactions short, and retry safely if PostgreSQL aborts a deadlocked transaction.

### What is the difference between optimistic and pessimistic locking?

Pessimistic locking blocks others early by locking rows. Optimistic locking allows concurrent work but detects conflicts with a version column at update time.

### What should happen after a transaction error in PostgreSQL?

Usually the transaction is marked failed. Roll it back, or use savepoints if the error was expected and recoverable.

### Why is idempotency important in banking APIs?

Clients retry after timeouts. Without an idempotency key, a retry may post the same transfer twice.

### What is WAL?

Write-ahead logging records changes before data pages are written. It supports rollback, crash recovery, replication, and durability.

### What is the senior-level answer for transaction safety?

Say: "I put all money movement writes in one transaction, use constraints for correctness, lock rows in a deterministic order, validate business rules inside the transaction, write immutable ledger entries, commit only after all steps succeed, and make retries idempotent."
