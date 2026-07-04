# Java Memory Leaks - Interview Response

## What Is It?

A Java memory leak happens when objects are still reachable but no longer useful, so garbage collection cannot reclaim them.

## In Simple Terms

A Java memory leak happens when objects are still reachable but no longer useful, so garbage collection cannot reclaim them.

## Why It Matters

Static collections, unbounded caches, listeners, and ThreadLocals commonly retain old request data.

If we get it wrong:

```text
Do not assume garbage collection prevents all leaks.
Do not keep request objects in static fields.
Do not use unbounded caches without eviction.
```

## Example

```text
private static final Map<String, String> cache = new LinkedHashMap<>();
Production cache should use max size/TTL, not grow forever.
```

Key interview details:

- static maps, unbounded cache, ThreadLocal, listeners, Hibernate persistence context.

## Safe vs Unsafe

Safe:

```text
Bound caches and define eviction policies.
Remove listeners and callbacks when lifecycle ends.
Clear ThreadLocal values in finally blocks.
Use heap dumps and allocation profiling to verify.
```

Unsafe:

```text
Do not assume garbage collection prevents all leaks.
Do not keep request objects in static fields.
Do not use unbounded caches without eviction.
```

## Java / Spring Backend Use Case

Static collections, unbounded caches, listeners, and ThreadLocals commonly retain old request data.

Java/Spring angle:

```text
private static final Map<String, String> cache = new LinkedHashMap<>();
Production cache should use max size/TTL, not grow forever.
```

## Production Concerns

- Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
- Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
- Explain tuning trade-offs and why blindly changing flags or heap size is risky.
- Production answer: optimize based on workload evidence and SLO impact.

## Common Mistakes

- Do not assume garbage collection prevents all leaks.
- Do not keep request objects in static fields.
- Do not use unbounded caches without eviction.

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

In an interview, I would say: A Java memory leak happens when objects are still reachable but no longer useful, so garbage collection cannot reclaim them. For example, Static collections, unbounded caches, listeners, and ThreadLocals commonly retain old request data. The main production risk is assume garbage collection prevents all leaks.
