# PostgreSQL Banking Data Design Concepts

These concepts are frequently asked in fintech and banking interviews. Learn the definition, the PostgreSQL shape, why it matters, and the production tradeoff.

## Normalization

Concept: Normalization organizes data so each fact is stored in one place. It reduces duplication and update mistakes.

Example:

```sql
CREATE TABLE customer (
    customer_id BIGINT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL
);

CREATE TABLE account (
    account_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer(customer_id),
    account_type VARCHAR(20) NOT NULL
);
```

Why it works: Customer details are stored once in `customer`. Account rows point to the customer through `customer_id`.

Banking example: If a customer changes their name, you update one customer row, not every account or transaction row.

Tradeoff: Highly normalized schemas often need more joins. For operational systems, correctness usually matters more than avoiding joins.

Follow-up: Normalization is best for core source-of-truth data such as customers, accounts, and ledger entries.

## Denormalization

Concept: Denormalization intentionally duplicates data to improve read performance or preserve a historical snapshot.

Example:

```sql
CREATE TABLE monthly_statement (
    statement_id BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    statement_month DATE NOT NULL,
    opening_balance NUMERIC(12, 2) NOT NULL,
    closing_balance NUMERIC(12, 2) NOT NULL
);
```

Why it works: `customer_name` is duplicated because a statement is a historical artifact. If the customer later changes their name, the old statement may still need the old name.

Alternate approach: Store only `account_id` and join to `customer` at read time. That is more normalized but may not preserve historical display values.

Performance note: Denormalization can reduce joins for reports, but it creates consistency work when duplicated values change.

Follow-up: Good denormalization is intentional and documented. Accidental duplication is a data-quality risk.

## Primary Key, Unique Key, Foreign Key

Concept:

- Primary key identifies one row.
- Unique key prevents duplicate values.
- Foreign key enforces a relationship between tables.

Example:

```sql
CREATE TABLE account (
    account_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer(customer_id),
    account_number VARCHAR(30) UNIQUE NOT NULL
);
```

Why it works: `account_id` identifies the row. `account_number` cannot repeat. `customer_id` must exist in `customer`.

Performance note: PostgreSQL automatically indexes primary keys and unique constraints. It does not automatically index the referencing foreign-key column, so index `account(customer_id)` if you frequently join accounts to customers.

Follow-up: A unique column can still have different null behavior depending on database and constraint design. In PostgreSQL, regular unique constraints allow multiple nulls.

## Immutable Ledger Entries

Concept: A ledger should be append-only. Posted financial facts should not be updated or deleted.

Example:

```sql
CREATE TABLE ledger_entry (
    ledger_entry_id BIGINT PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    entry_type VARCHAR(10) NOT NULL CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Transfer example:

```sql
INSERT INTO ledger_entry
    (ledger_entry_id, transaction_id, account_id, entry_type, amount)
VALUES
    (1, 9001, 101, 'DEBIT', 100.00),
    (2, 9001, 202, 'CREDIT', 100.00);
```

Why it works: A transfer creates equal and opposite entries. The audit trail remains intact.

Correction approach:

```sql
INSERT INTO ledger_entry
    (ledger_entry_id, transaction_id, account_id, entry_type, amount)
VALUES
    (3, 9002, 101, 'CREDIT', 100.00),
    (4, 9002, 202, 'DEBIT', 100.00);
```

Why corrections are new rows: Updating the old row hides history. Reversal entries preserve auditability.

Performance note: Index `(account_id, created_at)` for account history queries.

Follow-up: Current balance can be derived from ledger entries or maintained as a carefully controlled projection.

## Event Sourcing

Concept: Event sourcing stores every state change as an event. Current state is derived from events.

```sql
CREATE TABLE account_event (
    event_id BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    amount NUMERIC(12, 2),
    event_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Example:

```sql
INSERT INTO account_event (event_id, account_id, event_type, amount)
VALUES (1, 101, 'ACCOUNT_DEBITED', 100.00);
```

Why it works: Instead of only storing "balance is now 900," the system stores "account was debited by 100." This gives a complete history.

Tradeoff: Reads can become more complex because the current state may require replaying events or reading a projection table.

Banking answer: Event sourcing fits audit-heavy domains, but it needs careful idempotency, ordering, replay, and projection handling.

## SQL vs NoSQL for Banking Records

Use SQL for core banking transactions because it supports:

- ACID transactions
- Constraints
- Joins
- Strong consistency
- Mature audit patterns

Use NoSQL for supporting systems like:

- Activity feed
- Session data
- Fraud signals
- Logs

Why: Money movement needs strong correctness guarantees. Flexible scale is useful, but not at the cost of inconsistent balances.

Follow-up: NoSQL can support fraud analytics or customer activity streams, while PostgreSQL remains the transactional source of truth.

## Redis With PostgreSQL

Concept: Redis is an in-memory store often used for cache, counters, sessions, and short-lived keys.

Examples:

- Login rate limits
- OTP/session storage
- Idempotency keys
- Cached merchant metadata

Do not use Redis as the source of truth for account balances.

Why: Redis is fast, but balances need durable transactional correctness. PostgreSQL should own final financial state.

Follow-up: Redis can store a short-lived idempotency key to prevent duplicate payment submission while PostgreSQL stores the final transaction.

## Cache Invalidation

Safe account-data caching practices:

- Update or invalidate cache after database commit.
- Use short TTLs.
- Use version numbers.
- Avoid caching final balances unless consistency is carefully handled.

Example strategy:

```text
1. Update PostgreSQL inside a transaction.
2. Commit.
3. Publish account-updated event.
4. Consumer invalidates or refreshes Redis cache.
```

Correctness note: Invalidating before commit can remove a good cache value and then leave the database unchanged if the transaction rolls back.

Follow-up: For balances, prefer reading from the source of truth or a strongly consistent projection.

## Zero-Downtime Migration

Concept: Change schema without breaking running application versions.

Use expand and contract:

1. Add a new nullable column.
2. Deploy code that can handle old and new schemas.
3. Backfill data in batches.
4. Switch reads to the new column.
5. Stop writing the old column.
6. Drop old column later.

PostgreSQL example:

```sql
ALTER TABLE customer
ADD COLUMN full_name VARCHAR(150);
```

Why it works: Old code can ignore the new column. New code can start writing it. Both versions can run during deployment.

Follow-up: Avoid adding a `NOT NULL` column with a heavy default to a huge table without understanding lock and rewrite behavior.

## Large Backfill

Concept: A backfill updates old rows after a schema or logic change.

```sql
UPDATE customer
SET full_name = customer_name
WHERE full_name IS NULL
  AND customer_id BETWEEN 1 AND 10000;
```

Why batch: Updating millions of rows in one transaction can create long locks, huge WAL, replication lag, and rollback risk.

Better operational pattern:

```text
1. Pick a primary-key range.
2. Update a small batch.
3. Commit.
4. Sleep briefly if needed.
5. Continue from the last processed key.
```

Follow-up: Make backfills restartable and monitor rows updated, runtime, locks, replication lag, and errors.

## Mask PII

Concept: PII masking removes or transforms sensitive data in non-production environments.

```sql
UPDATE customer
SET customer_name = 'Customer ' || customer_id;
```

Other examples:

```sql
UPDATE customer
SET email = 'customer' || customer_id || '@example.com';
```

Why: Test data should preserve useful shape without exposing real customer information.

Follow-up: Mask names, emails, phone numbers, addresses, SSNs, account numbers, and card data. Keep referential integrity.

## Reconciliation

Concept: Reconciliation compares two sources to find missing, extra, or mismatched records.

Example: `core_transaction` is the bank's internal record. `settlement_transaction` is an external settlement processor's record.

Find records missing in settlement:

```sql
SELECT c.transaction_id,
       c.amount
FROM core_transaction c
LEFT JOIN settlement_transaction s
    ON c.transaction_id = s.transaction_id
WHERE s.transaction_id IS NULL;
```

Why it works: `LEFT JOIN` keeps all core rows. Rows with no settlement match have null settlement columns.

Find amount mismatches:

```sql
SELECT c.transaction_id,
       c.amount AS core_amount,
       s.amount AS settlement_amount
FROM core_transaction c
JOIN settlement_transaction s
    ON c.transaction_id = s.transaction_id
WHERE c.amount <> s.amount;
```

Find settlement records missing in core:

```sql
SELECT s.transaction_id,
       s.amount
FROM settlement_transaction s
LEFT JOIN core_transaction c
    ON s.transaction_id = c.transaction_id
WHERE c.transaction_id IS NULL;
```

Alternative using `FULL JOIN`:

```sql
SELECT COALESCE(c.transaction_id, s.transaction_id) AS transaction_id,
       c.amount AS core_amount,
       s.amount AS settlement_amount,
       CASE
           WHEN c.transaction_id IS NULL THEN 'MISSING_IN_CORE'
           WHEN s.transaction_id IS NULL THEN 'MISSING_IN_SETTLEMENT'
           WHEN c.amount <> s.amount THEN 'AMOUNT_MISMATCH'
           ELSE 'MATCHED'
       END AS reconciliation_status
FROM core_transaction c
FULL JOIN settlement_transaction s
    ON c.transaction_id = s.transaction_id
WHERE c.transaction_id IS NULL
   OR s.transaction_id IS NULL
   OR c.amount <> s.amount;
```

Why `FULL JOIN` helps: It shows missing records from both sides in one query. This is exactly what "reconciliation between two systems" means.

Performance note: Index transaction IDs in both systems. For huge reconciliations, compare by business date or batch id rather than all history.

Follow-up: Differences should be investigated and corrected with auditable adjustment entries, not silent updates.
