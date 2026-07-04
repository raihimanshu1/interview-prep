# Banking Transfer Race Conditions - Interview Response

## What Is It?

Protect transfers by making the read-check-write sequence atomic using database transactions, row locks, optimistic versioning, idempotency keys, and consistent lock ordering.

## In Simple Terms

Protect transfers by making the read-check-write sequence atomic using database transactions, row locks, optimistic versioning, idempotency keys, and consistent lock ordering.

## Why It Matters

Two concurrent debit requests must not both see the same balance and overdraw the account.

If we get it wrong:

```text
Do not perform balance check and debit as separate unprotected operations.
Do not rely only on Java synchronized when multiple application instances exist.
Do not allow retries without idempotency.
```

## Example

```text
@Transactional
SELECT account rows FOR UPDATE in sorted account-id order.
Verify available balance, insert ledger entries, update balances, then commit.
Store requestId with a unique constraint to make retries safe.
```

## Safe vs Unsafe

Safe:

```text
Keep debit and credit in one transaction.
Lock accounts in a stable order to avoid deadlocks.
Use optimistic locking with a version column when conflicts are rare.
Record an idempotency key so retries do not post twice.
```

Unsafe:

```text
Do not perform balance check and debit as separate unprotected operations.
Do not rely only on Java synchronized when multiple application instances exist.
Do not allow retries without idempotency.
```

## Java / Spring Backend Use Case

Two concurrent debit requests must not both see the same balance and overdraw the account.

Java/Spring angle:

```text
@Transactional
SELECT account rows FOR UPDATE in sorted account-id order.
Verify available balance, insert ledger entries, update balances, then commit.
Store requestId with a unique constraint to make retries safe.
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not perform balance check and debit as separate unprotected operations.
- Do not rely only on Java synchronized when multiple application instances exist.
- Do not allow retries without idempotency.

## Extra Details

Forward compatibility:

```text
Compatibility matters when this topic changes behavior exposed through APIs, shared libraries, event payloads, config properties, or deployment defaults. New behavior should be rolled out so older callers and services keep working safely.
```

Backward compatibility:

```text
Do not break existing callers, tests, serialized data, configuration, or operational runbooks silently. Keep old behavior available until users or services migrate.
```

Semantic versioning:

```text
MAJOR -> breaking public behavior or contract
MINOR -> compatible feature or API addition
PATCH -> bug fix or internal tuning
```

Big-company evolution mindset:

```text
Large engineering teams roll out changes gradually, keep compatibility during migration, measure usage, document deprecation, and avoid forcing all services to upgrade at once.
```

Related patterns:

- Strategy
- Adapter
- Factory
- Composition over inheritance

## Follow-Up Interview Questions

- How does this behave under concurrent requests?
- What happens when a downstream service or database operation fails?
- How would you test this and prove it works in production?

## Interview Answer

In an interview, I would say: Protect transfers by making the read-check-write sequence atomic using database transactions, row locks, optimistic versioning, idempotency keys, and consistent lock ordering. For example, Two concurrent debit requests must not both see the same balance and overdraw the account. The main production risk is perform balance check and debit as separate unprotected operations.
