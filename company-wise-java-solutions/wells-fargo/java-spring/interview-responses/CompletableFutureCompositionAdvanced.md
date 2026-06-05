# Completable Future Composition Advanced - Interview Response

## What Is It?

Compose CompletableFuture workflows by using thenCompose for dependent async steps, thenCombine for independent results, explicit executors, timeouts, and centralized exception handling.

## In Simple Terms

Compose CompletableFuture workflows by using thenCompose for dependent async steps, thenCombine for independent results, explicit executors, timeouts, and centralized exception handling.

## Why It Matters

A transfer pre-check can combine independent fraud and limits calls, then compose into the final authorization call only if both pass.

If we get it wrong:

```text
Do not use join everywhere and lose timeout control.
Do not let async exceptions disappear.
Do not run blocking work on the common pool without thinking.
```

## Example

```text
CompletableFuture<FraudResult> fraud = CompletableFuture.supplyAsync(this::fraudCheck, executor);
CompletableFuture<LimitResult> limits = CompletableFuture.supplyAsync(this::limitCheck, executor);
return fraud.thenCombine(limits, Decision::from)
.orTimeout(800, TimeUnit.MILLISECONDS)
.exceptionally(Decision::failed);
```

## Safe vs Unsafe

Safe:

```text
thenApply transforms a result synchronously; thenCompose flattens a future-returning step.
thenCombine joins independent futures.
Use explicit executors for blocking work.
Always define timeout and exceptional behavior.
```

Unsafe:

```text
Do not use join everywhere and lose timeout control.
Do not let async exceptions disappear.
Do not run blocking work on the common pool without thinking.
```

## Java / Spring Backend Use Case

A transfer pre-check can combine independent fraud and limits calls, then compose into the final authorization call only if both pass.

Java/Spring angle:

```text
CompletableFuture<FraudResult> fraud = CompletableFuture.supplyAsync(this::fraudCheck, executor);
CompletableFuture<LimitResult> limits = CompletableFuture.supplyAsync(this::limitCheck, executor);
return fraud.thenCombine(limits, Decision::from)
.orTimeout(800, TimeUnit.MILLISECONDS)
.exceptionally(Decision::failed);
```

## Production Concerns

- Define the shared-state or scheduling problem before naming a concurrency primitive.
- Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
- Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
- Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.

## Common Mistakes

- Do not use join everywhere and lose timeout control.
- Do not let async exceptions disappear.
- Do not run blocking work on the common pool without thinking.

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

In an interview, I would say: Compose CompletableFuture workflows by using thenCompose for dependent async steps, thenCombine for independent results, explicit executors, timeouts, and centralized exception handling. For example, a transfer pre-check can combine independent fraud and limits calls, then compose into the final authorization call only if both pass. The main production risk is use join everywhere and lose timeout control.
