# Atomic Classes Vs Locks - Interview Response

## What Is It?

Atomic classes handle simple independent state changes with lock-free compare-and-set; locks handle compound operations that must protect multiple variables or larger critical sections.

## In Simple Terms

Atomic classes handle simple independent state changes with lock-free compare-and-set; locks handle compound operations that must protect multiple variables or larger critical sections.

## Why It Matters

A counter for accepted requests can use AtomicLong, but moving money between two accounts needs a transaction or lock around the whole invariant.

If we get it wrong:

```text
Do not use an atomic variable to protect several related fields.
Do not hide business invariants inside scattered CAS loops.
Do not assume lock-free always means faster under contention.
```

## Example

```text
AtomicLong successfulPayments = new AtomicLong();
successfulPayments.incrementAndGet();
For multi-row money movement, use a database transaction instead of only an atomic counter.
```

## Safe vs Unsafe

Safe:

```text
Use AtomicInteger/AtomicLong for counters, flags, and simple CAS updates.
Use locks when several reads and writes must be consistent together.
Prefer database transactions for persistent financial state.
Measure contention because both atomics and locks can become bottlenecks.
```

Unsafe:

```text
Do not use an atomic variable to protect several related fields.
Do not hide business invariants inside scattered CAS loops.
Do not assume lock-free always means faster under contention.
```

## Java / Spring Backend Use Case

A counter for accepted requests can use AtomicLong, but moving money between two accounts needs a transaction or lock around the whole invariant.

Java/Spring angle:

```text
AtomicLong successfulPayments = new AtomicLong();
successfulPayments.incrementAndGet();
For multi-row money movement, use a database transaction instead of only an atomic counter.
```

## Production Concerns

- Define the shared-state or scheduling problem before naming a concurrency primitive.
- Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
- Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
- Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.

## Common Mistakes

- Do not use an atomic variable to protect several related fields.
- Do not hide business invariants inside scattered CAS loops.
- Do not assume lock-free always means faster under contention.

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

- How does this behave under concurrent requests?
- What happens when a downstream service or database operation fails?
- How would you test this and prove it works in production?

## Interview Answer

In an interview, I would say: Atomic classes handle simple independent state changes with lock-free compare-and-set; locks handle compound operations that must protect multiple variables or larger critical sections. For example, a counter for accepted requests can use AtomicLong, but moving money between two accounts needs a transaction or lock around the whole invariant. The main production risk is use an atomic variable to protect several related fields.
