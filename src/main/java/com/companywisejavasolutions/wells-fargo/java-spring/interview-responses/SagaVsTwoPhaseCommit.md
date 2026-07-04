# Saga Vs Two Phase Commit - Interview Response

## What Is It?

Two-phase commit coordinates one atomic transaction across resources; saga breaks work into local transactions with compensating actions and eventual consistency.

## In Simple Terms

Two-phase commit coordinates one atomic transaction across resources; saga breaks work into local transactions with compensating actions and eventual consistency.

## Why It Matters

A cross-service loan workflow often uses saga; a single database transfer can use a normal ACID transaction.

If we get it wrong:

```text
Do not use saga when strict immediate atomicity is required and a local transaction is possible.
Do not forget compensation paths.
Do not hide workflow state across many event handlers without observability.
```

## Example

```text
Saga example:
reserve funds -> create payment -> notify ledger.
If payment fails, compensate by releasing reserved funds.
```

Key interview details:

- payment/account saga flow, compensation, orchestration vs choreography, why 2PC is risky.

## Safe vs Unsafe

Safe:

```text
2PC gives stronger atomicity but adds coordinator complexity and blocking risk.
Saga scales across services but requires compensation and state tracking.
Use orchestration when central workflow visibility matters.
Use choreography carefully to avoid hidden coupling.
```

Unsafe:

```text
Do not use saga when strict immediate atomicity is required and a local transaction is possible.
Do not forget compensation paths.
Do not hide workflow state across many event handlers without observability.
```

## Java / Spring Backend Use Case

A cross-service loan workflow often uses saga; a single database transfer can use a normal ACID transaction.

Java/Spring angle:

```text
Saga example:
reserve funds -> create payment -> notify ledger.
If payment fails, compensate by releasing reserved funds.
```

## Production Concerns

- Model saga as a state machine with durable steps, retries, compensation, idempotency keys, and timeout handling.
- Compare orchestration and choreography with coupling and observability trade-offs.
- Mention compensation is not true rollback; it is a business action that must be auditable.
- Production answer: use normal ACID transaction when one database owns the invariant; use saga across service boundaries.

## Common Mistakes

- Do not use saga when strict immediate atomicity is required and a local transaction is possible.
- Do not forget compensation paths.
- Do not hide workflow state across many event handlers without observability.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Saga Vs Two Phase Commit changes are deployed.
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

In an interview, I would say: Two-phase commit coordinates one atomic transaction across resources; saga breaks work into local transactions with compensating actions and eventual consistency. For example, a cross-service loan workflow often uses saga; a single database transfer can use a normal ACID transaction. The main production risk is use saga when strict immediate atomicity is required and a local transaction is possible.
