# Redis Cache Aside Pattern - Interview Response

## What Is It?

Cache-aside means the application checks cache first, loads from database on miss, writes the value to cache, and invalidates or updates cache when data changes.

## In Simple Terms

Cache-aside means the application checks cache first, loads from database on miss, writes the value to cache, and invalidates or updates cache when data changes.

## Why It Matters

A customer profile service can cache profile reads while invalidating the key after profile updates.

If we get it wrong:

```text
Do not cache without invalidation or TTL.
Do not cache sensitive data casually.
Do not ignore cache stampede and hot keys.
```

## Example

```text
CustomerProfile profile = cache.get(id);
if (profile == null) {
profile = repository.findById(id);
cache.put(id, profile, Duration.ofMinutes(10));
}
```

Key interview details:

- cache miss, TTL, invalidation, stale data, stampede protection, RedisTemplate.

## Safe vs Unsafe

Safe:

```text
Cache-aside is simple and keeps the database as source of truth.
TTL protects against stale entries and forgotten invalidation.
Stampede protection may be needed for hot keys.
Sensitive data in cache needs encryption/access controls and careful TTL.
```

Unsafe:

```text
Do not cache without invalidation or TTL.
Do not cache sensitive data casually.
Do not ignore cache stampede and hot keys.
```

## Java / Spring Backend Use Case

A customer profile service can cache profile reads while invalidating the key after profile updates.

Java/Spring angle:

```text
CustomerProfile profile = cache.get(id);
if (profile == null) {
profile = repository.findById(id);
cache.put(id, profile, Duration.ofMinutes(10));
}
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not cache without invalidation or TTL.
- Do not cache sensitive data casually.
- Do not ignore cache stampede and hot keys.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Redis Cache Aside Pattern changes are deployed.
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

- What breaks under high concurrency or partial failure?
- How would you verify this with tests, metrics, logs, or traces?
- What trade-off would make you choose a different design?

## Interview Answer

In an interview, I would say: Cache-aside means the application checks cache first, loads from database on miss, writes the value to cache, and invalidates or updates cache when data changes. For example, a customer profile service can cache profile reads while invalidating the key after profile updates. The main production risk is cache without invalidation or TTL.
