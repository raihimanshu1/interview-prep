# Method Reference Vs Lambda - Interview Response

## What Is It?

A method reference is compact syntax for a lambda that only calls an existing method; a lambda is better when logic needs parameters, conditions, or clarity.

## In Simple Terms

A method reference is compact syntax for a lambda that only calls an existing method; a lambda is better when logic needs parameters, conditions, or clarity.

## Why It Matters

Transaction::id is readable for simple mapping; tx -> mask(tx.id()) is clearer when extra logic is needed.

If we get it wrong:

```text
Do not use method references when a lambda would explain intent better.
Do not hide checked exception handling awkwardly.
Do not optimize syntax at the cost of readability.
```

## Example

```text
List<String> ids = transactions.stream().map(Transaction::id).toList();
List<String> masked = transactions.stream().map(tx -> mask(tx.id())).toList();
```

Key interview details:

- User::getName vs lambda with conditional logic.

## Safe vs Unsafe

Safe:

```text
Use method references for direct delegation.
Use lambdas when naming parameters improves readability.
Do not sacrifice clarity for clever syntax.
Both compile to functional-interface implementations.
```

Unsafe:

```text
Do not use method references when a lambda would explain intent better.
Do not hide checked exception handling awkwardly.
Do not optimize syntax at the cost of readability.
```

## Java / Spring Backend Use Case

Transaction::id is readable for simple mapping; tx -> mask(tx.id()) is clearer when extra logic is needed.

Java/Spring angle:

```text
List<String> ids = transactions.stream().map(Transaction::id).toList();
List<String> masked = transactions.stream().map(tx -> mask(tx.id())).toList();
```

## Production Concerns

- Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
- Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
- Explain tuning trade-offs and why blindly changing flags or heap size is risky.
- Production answer: optimize based on workload evidence and SLO impact.

## Common Mistakes

- Do not use method references when a lambda would explain intent better.
- Do not hide checked exception handling awkwardly.
- Do not optimize syntax at the cost of readability.

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

In an interview, I would say: A method reference is compact syntax for a lambda that only calls an existing method; a lambda is better when logic needs parameters, conditions, or clarity. For example, Transaction::id is readable for simple mapping; tx -> mask(tx.id()) is clearer when extra logic is needed. The main production risk is use method references when a lambda would explain intent better.
