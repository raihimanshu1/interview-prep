# Backpressure - Interview Response

## What Is It?

Backpressure is a way for consumers to signal producers to slow down so queues, memory, and downstream services do not collapse under load.

## In Simple Terms

Backpressure is a way for consumers to signal producers to slow down so queues, memory, and downstream services do not collapse under load.

## Why It Matters

A payment event consumer should limit in-flight messages and pause polling when downstream posting is slow.

If we get it wrong:

```text
Do not use unbounded queues for production traffic.
Do not retry faster than the consumer can recover.
Do not ignore queue depth and consumer lag metrics.
```

## Example

```text
BlockingQueue<PaymentEvent> queue = new ArrayBlockingQueue<>(10_000);
boolean accepted = queue.offer(event, 100, TimeUnit.MILLISECONDS);
If accepted is false, reject or retry later instead of growing memory forever.
```

Key interview details:

- Producer/consumer mismatch, bounded queues, Kafka lag, Reactor demand, and slow downstream handling.

## Safe vs Unsafe

Safe:

```text
Bound queues instead of allowing unbounded memory growth.
Apply rate limits, max in-flight requests, and timeouts.
Use reactive streams, broker prefetch, or consumer pause/resume when available.
Expose queue depth and consumer lag as operational metrics.
```

Unsafe:

```text
Do not use unbounded queues for production traffic.
Do not retry faster than the consumer can recover.
Do not ignore queue depth and consumer lag metrics.
```

## Java / Spring Backend Use Case

A payment event consumer should limit in-flight messages and pause polling when downstream posting is slow.

Java/Spring angle:

```text
BlockingQueue<PaymentEvent> queue = new ArrayBlockingQueue<>(10_000);
boolean accepted = queue.offer(event, 100, TimeUnit.MILLISECONDS);
If accepted is false, reject or retry later instead of growing memory forever.
```

## Production Concerns

- Define the shared-state or scheduling problem before naming a concurrency primitive.
- Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
- Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
- Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.

## Common Mistakes

- Do not use unbounded queues for production traffic.
- Do not retry faster than the consumer can recover.
- Do not ignore queue depth and consumer lag metrics.

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

In an interview, I would say: Backpressure is a way for consumers to signal producers to slow down so queues, memory, and downstream services do not collapse under load. For example, a payment event consumer should limit in-flight messages and pause polling when downstream posting is slow. The main production risk is use unbounded queues for production traffic.
