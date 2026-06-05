# Idempotent Post Apis - Interview Response

## What Is It?

Design idempotent POST APIs with a client-provided idempotency key, a uniqueness constraint, request fingerprinting, and replay of the original result.

## In Simple Terms

Design idempotent POST APIs with a client-provided idempotency key, a uniqueness constraint, request fingerprinting, and replay of the original result.

## Why It Matters

Retrying a transfer request with the same key should return the same transfer result, not create a second transfer.

If we get it wrong:

```text
Do not depend only on a client retry flag.
Do not allow one idempotency key to represent different payloads.
Do not keep idempotency state only in memory.
```

## Example

```text
@Transactional
INSERT idempotency_key; if duplicate, return stored result.
Verify request hash matches the first request.
Execute operation once and persist final response before commit.
```

Key interview details:

- Idempotency-Key, request hash, response replay, TTL, unique constraint, race handling.

## Safe vs Unsafe

Safe:

```text
Require a unique idempotency key per logical operation.
Store request hash and response/status atomically.
Reject key reuse with a different payload.
Use database uniqueness as the final guard.
```

Unsafe:

```text
Do not depend only on a client retry flag.
Do not allow one idempotency key to represent different payloads.
Do not keep idempotency state only in memory.
```

## Java / Spring Backend Use Case

Retrying a transfer request with the same key should return the same transfer result, not create a second transfer.

Java/Spring angle:

```text
@Transactional
INSERT idempotency_key; if duplicate, return stored result.
Verify request hash matches the first request.
Execute operation once and persist final response before commit.
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not depend only on a client retry flag.
- Do not allow one idempotency key to represent different payloads.
- Do not keep idempotency state only in memory.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Idempotent Post Apis changes are deployed.
Avoid removing fields, renaming fields, changing meanings, or making optional inputs required without a versioned rollout.
```

Semantic versioning:

```text
MAJOR -> breaking API/event/library contract change
MINOR -> backward-compatible capability or optional field
PATCH -> bug fix, tuning, or internal implementation improvement
```

Big-company API evolution mindset:

```text
Amazon/Google-style evolution usually favors additive contracts, consumer-driven tests, telemetry on old client usage, deprecation windows, gradual rollout, and rollback paths.
```

Related patterns:

- Adapter
- Facade
- Consumer-driven contracts
- Strangler migration

## Follow-Up Interview Questions

- How does this behave under concurrent requests?
- What happens when a downstream service or database operation fails?
- How would you test this and prove it works in production?

## Interview Answer

In an interview, I would say: Design idempotent POST APIs with a client-provided idempotency key, a uniqueness constraint, request fingerprinting, and replay of the original result. For example, Retrying a transfer request with the same key should return the same transfer result, not create a second transfer. The main production risk is depend only on a client retry flag.
