# Runnable Callable Future Completable Future - Interview Response

## What Is It?

Runnable performs work without a result, Callable returns a result or checked exception, Future represents pending completion, and CompletableFuture supports composing async stages.

## In Simple Terms

Runnable performs work without a result, Callable returns a result or checked exception, Future represents pending completion, and CompletableFuture supports composing async stages.

## Why It Matters

Use CompletableFuture to combine independent fraud and limits checks when latency matters and failure policy is clear.

If we get it wrong:

```text
Do not ignore Future.get timeouts.
Do not use the common pool for heavy blocking I/O.
Do not let async exceptions disappear silently.
```

## Example

```text
CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> "approved", executor)
.orTimeout(500, TimeUnit.MILLISECONDS)
.exceptionally(ex -> "failed");
```

Key interview details:

- comparison table and Java examples.

## Safe vs Unsafe

Safe:

```text
Do not block common-pool threads with long blocking I/O.
Prefer explicit executors for backend async work.
Handle exceptional completion.
Keep async composition readable and bounded.
```

Unsafe:

```text
Do not ignore Future.get timeouts.
Do not use the common pool for heavy blocking I/O.
Do not let async exceptions disappear silently.
```

## Java / Spring Backend Use Case

Use CompletableFuture to combine independent fraud and limits checks when latency matters and failure policy is clear.

Java/Spring angle:

```text
CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> "approved", executor)
.orTimeout(500, TimeUnit.MILLISECONDS)
.exceptionally(ex -> "failed");
```

## Production Concerns

- Define the shared-state or scheduling problem before naming a concurrency primitive.
- Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
- Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
- Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.

## Common Mistakes

- Do not ignore Future.get timeouts.
- Do not use the common pool for heavy blocking I/O.
- Do not let async exceptions disappear silently.

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

In an interview, I would say: Runnable performs work without a result, Callable returns a result or checked exception, Future represents pending completion, and CompletableFuture supports composing async stages. For example, use CompletableFuture to combine independent fraud and limits checks when latency matters and failure policy is clear. The main production risk is ignore Future.get timeouts.
