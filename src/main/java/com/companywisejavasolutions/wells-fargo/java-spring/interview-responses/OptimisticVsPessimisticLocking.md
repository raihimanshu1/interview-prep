# Optimistic Vs Pessimistic Locking - Interview Response

## What Is It?

Optimistic locking detects conflicts at commit using a version column; pessimistic locking prevents conflicts earlier by locking rows while the transaction runs.

## In Simple Terms

Optimistic locking detects conflicts at commit using a version column; pessimistic locking prevents conflicts earlier by locking rows while the transaction runs.

## Why It Matters

Optimistic locking is good for rare account-profile conflicts, while pessimistic locking may be needed for high-risk balance updates.

If we get it wrong:

```text
Do not hold pessimistic locks during remote calls.
Do not ignore optimistic-lock retry policy.
Do not use application-only locks across multiple service instances.
```

## Example

```text
Optimistic:
@Version long version;
On stale update, catch OptimisticLockException and retry or return conflict.
Pessimistic: SELECT ... FOR UPDATE around short critical transaction.
```

Key interview details:

- @Version, PESSIMISTIC_WRITE, transfer conflict scenario.

## Safe vs Unsafe

Safe:

```text
Optimistic locking scales well when conflicts are rare.
Pessimistic locking gives stronger immediate protection but can block and deadlock.
Version columns allow safe retry logic.
Lock choice depends on contention, invariant risk, and transaction duration.
```

Unsafe:

```text
Do not hold pessimistic locks during remote calls.
Do not ignore optimistic-lock retry policy.
Do not use application-only locks across multiple service instances.
```

## Java / Spring Backend Use Case

Optimistic locking is good for rare account-profile conflicts, while pessimistic locking may be needed for high-risk balance updates.

Java/Spring angle:

```text
Optimistic:
@Version long version;
On stale update, catch OptimisticLockException and retry or return conflict.
Pessimistic: SELECT ... FOR UPDATE around short critical transaction.
```

## Production Concerns

- Explain version columns and conflict detection for optimistic locking.
- Explain row locks, blocking, deadlock risk, and short critical sections for pessimistic locking.
- Tie choice to contention level and invariant risk.
- Production answer: retry optimistic conflicts carefully and never hold DB locks across remote calls.

## Common Mistakes

- Do not hold pessimistic locks during remote calls.
- Do not ignore optimistic-lock retry policy.
- Do not use application-only locks across multiple service instances.

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

- Immutable object pattern
- Thread confinement
- Producer-consumer
- Bulkhead

## Follow-Up Interview Questions

- What breaks under high concurrency or partial failure?
- How would you verify this with tests, metrics, logs, or traces?
- What trade-off would make you choose a different design?

## Interview Answer

In an interview, I would say: Optimistic locking detects conflicts at commit using a version column; pessimistic locking prevents conflicts earlier by locking rows while the transaction runs. For example, Optimistic locking is good for rare account-profile conflicts, while pessimistic locking may be needed for high-risk balance updates. The main production risk is hold pessimistic locks during remote calls.
