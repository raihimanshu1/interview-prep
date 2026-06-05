# Process Vs Thread - Interview Response

## What Is It?

A process has its own memory space and resources; threads run inside a process and share heap memory with lower creation/context-switch overhead.

## In Simple Terms

A process has its own memory space and resources; threads run inside a process and share heap memory with lower creation/context-switch overhead.

## Why It Matters

A Spring Boot JVM is a process; request handlers run on threads that share application objects.

If we get it wrong:

```text
Do not create unlimited threads.
Do not put request-specific mutable data in shared objects.
Do not assume thread failure is isolated like process failure.
```

## Example

```text
Thread worker = new Thread(() -> processPayment());
In production prefer managed executors/thread pools over ad-hoc threads.
```

Key interview details:

- memory isolation, context switching, JVM request handling.

## Safe vs Unsafe

Safe:

```text
Threads share heap, so shared mutable state needs protection.
Processes isolate failures better but communicate more expensively.
Too many threads increase context switching and memory usage.
Use thread pools instead of creating a thread per request.
```

Unsafe:

```text
Do not create unlimited threads.
Do not put request-specific mutable data in shared objects.
Do not assume thread failure is isolated like process failure.
```

## Java / Spring Backend Use Case

A Spring Boot JVM is a process; request handlers run on threads that share application objects.

Java/Spring angle:

```text
Thread worker = new Thread(() -> processPayment());
In production prefer managed executors/thread pools over ad-hoc threads.
```

## Production Concerns

- Define the shared-state or scheduling problem before naming a concurrency primitive.
- Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
- Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
- Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.

## Common Mistakes

- Do not create unlimited threads.
- Do not put request-specific mutable data in shared objects.
- Do not assume thread failure is isolated like process failure.

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

In an interview, I would say: A process has its own memory space and resources; threads run inside a process and share heap memory with lower creation/context-switch overhead. For example, a Spring Boot JVM is a process; request handlers run on threads that share application objects. The main production risk is create unlimited threads.
