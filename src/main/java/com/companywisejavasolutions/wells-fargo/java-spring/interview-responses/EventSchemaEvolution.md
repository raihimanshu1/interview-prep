# Event Schema Evolution - Interview Response

## What Is It?

Handle event schema evolution with backward/forward compatible changes, versioned contracts, tolerant readers, schema registry validation, and explicit deprecation windows.

## In Simple Terms

Handle event schema evolution with backward/forward compatible changes, versioned contracts, tolerant readers, schema registry validation, and explicit deprecation windows.

## Why It Matters

A PaymentPosted event can add optional fields without breaking old consumers, but removing or renaming fields requires a versioning plan.

If we get it wrong:

```text
Do not rename or remove fields without versioning.
Do not change the meaning of an existing field silently.
Do not deploy producer changes without consumer contract coverage.
```

## Example

```text
Prefer additive event changes:
v1: { paymentId, amount }
v2: { paymentId, amount, currency } // optional/defaulted for old consumers
Breaking changes should publish a new event type or major version.
```

Key interview details:

- Optional fields, defaults, schema registry, Avro/JSON compatibility, and compatible vs breaking changes.

## Safe vs Unsafe

Safe:

```text
Add optional fields before requiring them.
Never change field meaning silently.
Keep consumers tolerant of unknown fields.
Use contract tests and schema registry checks in CI.
```

Unsafe:

```text
Do not rename or remove fields without versioning.
Do not change the meaning of an existing field silently.
Do not deploy producer changes without consumer contract coverage.
```

## Java / Spring Backend Use Case

A PaymentPosted event can add optional fields without breaking old consumers, but removing or renaming fields requires a versioning plan.

Java/Spring angle:

```text
Prefer additive event changes:
v1: { paymentId, amount }
v2: { paymentId, amount, currency } // optional/defaulted for old consumers
Breaking changes should publish a new event type or major version.
```

## Production Concerns

- Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
- Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
- Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
- Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.

## Common Mistakes

- Do not rename or remove fields without versioning.
- Do not change the meaning of an existing field silently.
- Do not deploy producer changes without consumer contract coverage.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Event Schema Evolution changes are deployed.
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

In an interview, I would say: Handle event schema evolution with backward/forward compatible changes, versioned contracts, tolerant readers, schema registry validation, and explicit deprecation windows. For example, a PaymentPosted event can add optional fields without breaking old consumers, but removing or renaming fields requires a versioning plan. The main production risk is rename or remove fields without versioning.
