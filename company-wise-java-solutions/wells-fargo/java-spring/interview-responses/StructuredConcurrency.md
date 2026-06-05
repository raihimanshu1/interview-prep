# Structured Concurrency - Interview Response

## What Is It?

Structured concurrency treats related concurrent tasks as one scoped unit, making cancellation, failure handling, and joining easier to reason about than scattered futures.

## In Simple Terms

Structured concurrency treats related concurrent tasks as one scoped unit, making cancellation, failure handling, and joining easier to reason about than scattered futures.

## Why It Matters

A loan decision service can run fraud, credit, and limits checks concurrently and cancel the remaining checks when one mandatory check fails.

If we get it wrong:

```text
Do not create background futures that outlive the request accidentally.
Do not ignore cancellation when one child task fails.
Do not hide business failure policy inside random CompletableFuture chains.
```

## Example

```text
Conceptual shape:
create scope -> fork related tasks -> join -> handle failure -> return combined result.
The key design rule is that child tasks cannot outlive the request/use-case scope.
```

Key interview details:

- Java 21 task scope, cancellation, failure propagation, timeout, CompletableFuture comparison.

## Safe vs Unsafe

Safe:

```text
The parent scope owns child tasks and waits for them before leaving the scope.
Failure policy becomes explicit: fail-fast, collect all results, or use first successful result.
It improves observability because task lifetime matches the business operation.
In Java, structured concurrency has been evolving as a preview/incubator API, so production usage depends on the JDK version and policy.
```

Unsafe:

```text
Do not create background futures that outlive the request accidentally.
Do not ignore cancellation when one child task fails.
Do not hide business failure policy inside random CompletableFuture chains.
```

## Java / Spring Backend Use Case

A loan decision service can run fraud, credit, and limits checks concurrently and cancel the remaining checks when one mandatory check fails.

Java/Spring angle:

```text
Conceptual shape:
create scope -> fork related tasks -> join -> handle failure -> return combined result.
The key design rule is that child tasks cannot outlive the request/use-case scope.
```

## Production Concerns

- Define the shared-state or scheduling problem before naming a concurrency primitive.
- Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
- Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
- Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.

## Common Mistakes

- Do not create background futures that outlive the request accidentally.
- Do not ignore cancellation when one child task fails.
- Do not hide business failure policy inside random CompletableFuture chains.

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

In an interview, I would say: Structured concurrency treats related concurrent tasks as one scoped unit, making cancellation, failure handling, and joining easier to reason about than scattered futures. For example, a loan decision service can run fraud, credit, and limits checks concurrently and cancel the remaining checks when one mandatory check fails. The main production risk is create background futures that outlive the request accidentally.
