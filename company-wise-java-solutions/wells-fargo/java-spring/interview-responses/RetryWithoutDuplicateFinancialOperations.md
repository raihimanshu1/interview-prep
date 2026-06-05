# Retry Without Duplicate Financial Operations - Interview Response

## What Is It?

Retries must be protected by idempotency keys, unique constraints, operation status tracking, and safe retry policies so the same financial command is executed once.

## In Simple Terms

Retries must be protected by idempotency keys, unique constraints, operation status tracking, and safe retry policies so the same financial command is executed once.

## Why It Matters

A network timeout after posting a transfer should allow the client to retry and receive the existing transfer result.

If we get it wrong:

```text
Do not retry money movement without idempotency.
Do not store idempotency keys outside the transaction that creates the operation.
Do not treat every error as retryable.
```

## Example

```text
Unique key: (clientId, idempotencyKey)
If insert succeeds, process once.
If duplicate, return stored status/result.
If in progress, return 202 or wait according to API policy.
```

Key interview details:

- idempotency key table, unique constraint, retry states, duplicate response.

## Safe vs Unsafe

Safe:

```text
Generate or require a stable operation key.
Store the key and operation result in the same transaction as the financial write.
Make external calls idempotent or reconcile by reference ID.
Retry only transient failures with backoff and jitter.
```

Unsafe:

```text
Do not retry money movement without idempotency.
Do not store idempotency keys outside the transaction that creates the operation.
Do not treat every error as retryable.
```

## Java / Spring Backend Use Case

A network timeout after posting a transfer should allow the client to retry and receive the existing transfer result.

Java/Spring angle:

```text
Unique key: (clientId, idempotencyKey)
If insert succeeds, process once.
If duplicate, return stored status/result.
If in progress, return 202 or wait according to API policy.
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not retry money movement without idempotency.
- Do not store idempotency keys outside the transaction that creates the operation.
- Do not treat every error as retryable.

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

In an interview, I would say: Retries must be protected by idempotency keys, unique constraints, operation status tracking, and safe retry policies so the same financial command is executed once. For example, a network timeout after posting a transfer should allow the client to retry and receive the existing transfer result. The main production risk is retry money movement without idempotency.
