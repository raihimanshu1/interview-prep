# Synchronized Vs Reentrant Lock - Interview Response

## What Is It?

synchronized is JVM-managed mutual exclusion; ReentrantLock offers explicit lock/unlock plus features like tryLock, fairness, interruptible waits, and multiple conditions.

## In Simple Terms

synchronized is JVM-managed mutual exclusion; ReentrantLock offers explicit lock/unlock plus features like tryLock, fairness, interruptible waits, and multiple conditions.

## Why It Matters

Use synchronized for simple critical sections and ReentrantLock when timeout or fairness behavior is required.

If we get it wrong:

```text
Do not forget unlock in a finally block.
Do not lock around slow I/O.
Do not assume fairness is free.
```

## Example

```text
Lock lock = new ReentrantLock();
if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
try { protected work } finally { lock.unlock(); }
}
```

Key interview details:

- tryLock, fairness, interruptible lock, finally unlock, deadlock risks.

## Safe vs Unsafe

Safe:

```text
Always release ReentrantLock in finally.
Keep critical sections small.
Use consistent lock ordering.
Do not lock around slow network calls.
```

Unsafe:

```text
Do not forget unlock in a finally block.
Do not lock around slow I/O.
Do not assume fairness is free.
```

## Java / Spring Backend Use Case

Use synchronized for simple critical sections and ReentrantLock when timeout or fairness behavior is required.

Java/Spring angle:

```text
Lock lock = new ReentrantLock();
if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
try { protected work } finally { lock.unlock(); }
}
```

## Production Concerns

- Define the shared-state or scheduling problem before naming a concurrency primitive.
- Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
- Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
- Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.

## Common Mistakes

- Do not forget unlock in a finally block.
- Do not lock around slow I/O.
- Do not assume fairness is free.

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

In an interview, I would say: synchronized is JVM-managed mutual exclusion; ReentrantLock offers explicit lock/unlock plus features like tryLock, fairness, interruptible waits, and multiple conditions. For example, use synchronized for simple critical sections and ReentrantLock when timeout or fairness behavior is required. The main production risk is forget unlock in a finally block.
