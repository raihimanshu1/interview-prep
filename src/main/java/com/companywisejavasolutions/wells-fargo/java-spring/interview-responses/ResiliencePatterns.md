# Resilience Patterns - Interview Response

## What Is It?

Timeouts cap waiting, retries handle transient failures, circuit breakers stop repeated calls to unhealthy dependencies, and bulkheads isolate capacity.

## In Simple Terms

Timeouts cap waiting, retries handle transient failures, circuit breakers stop repeated calls to unhealthy dependencies, and bulkheads isolate capacity.

## Why It Matters

A transfer service can isolate fraud-check calls so a fraud outage does not exhaust ledger-posting threads.

If we get it wrong:

```text
Do not retry non-idempotent operations without protection.
Do not set timeouts longer than the caller deadline.
Do not share one executor for every dependency.
```

## Example

```text
Good order: caller deadline -> timeout -> retry budget -> circuit breaker -> bulkhead metrics.
Never retry a non-idempotent money movement unless an idempotency key protects it.
```

Key interview details:

- circuit breaker, timeout, retry, bulkhead, retry storms, idempotency.

## Safe vs Unsafe

Safe:

```text
Always combine retries with timeouts and idempotency.
Use jittered backoff to avoid synchronized retry spikes.
Tune circuit breakers with real traffic metrics.
Bulkhead critical dependencies separately.
```

Unsafe:

```text
Do not retry non-idempotent operations without protection.
Do not set timeouts longer than the caller deadline.
Do not share one executor for every dependency.
```

## Java / Spring Backend Use Case

A transfer service can isolate fraud-check calls so a fraud outage does not exhaust ledger-posting threads.

Java/Spring angle:

```text
Good order: caller deadline -> timeout -> retry budget -> circuit breaker -> bulkhead metrics.
Never retry a non-idempotent money movement unless an idempotency key protects it.
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not retry non-idempotent operations without protection.
- Do not set timeouts longer than the caller deadline.
- Do not share one executor for every dependency.

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

In an interview, I would say: Timeouts cap waiting, retries handle transient failures, circuit breakers stop repeated calls to unhealthy dependencies, and bulkheads isolate capacity. For example, a transfer service can isolate fraud-check calls so a fraud outage does not exhaust ledger-posting threads. The main production risk is retry non-idempotent operations without protection.
