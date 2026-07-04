# Custom Immutable Class - Interview Response

## What Is It?

Make the class final, fields private final, validate in constructor, expose no setters, and defensively copy mutable inputs/outputs.

## In Simple Terms

Make the class final, fields private final, validate in constructor, expose no setters, and defensively copy mutable inputs/outputs.

## Why It Matters

In a banking backend, Custom Immutable Class matters because correctness, security, concurrency, and observability must survive real production traffic.

If we get it wrong:

```text
final does not make a mutable List inside the object immutable.
Do not skip the production failure mode.
Do not ignore testing and observability.
```

## Example

```text
Production answer pattern:
define concept -> give banking example -> name failure mode -> show guardrail -> mention test/monitoring.
```

## Safe vs Unsafe

Safe:

```text
Start with the simple definition, then explain the production consequence.
Name the failure mode: race condition, data inconsistency, memory pressure, latency, or security exposure.
Explain the trade-off instead of presenting one option as universally best.
Close with how you would test or monitor it in a real service.
```

Unsafe:

```text
final does not make a mutable List inside the object immutable.
Do not skip the production failure mode.
Do not ignore testing and observability.
```

## Java / Spring Backend Use Case

In a banking backend, Custom Immutable Class matters because correctness, security, concurrency, and observability must survive real production traffic.

Java/Spring angle:

```text
Production answer pattern:
define concept -> give banking example -> name failure mode -> show guardrail -> mention test/monitoring.
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- final does not make a mutable List inside the object immutable.
- Do not skip the production failure mode.
- Do not ignore testing and observability.

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

In an interview, I would say: Make the class final, fields private final, validate in constructor, expose no setters, and defensively copy mutable inputs/outputs. For example, in a banking backend, Custom Immutable Class matters because correctness, security, concurrency, and observability must survive real production traffic. The main production risk is final does not make a mutable List inside the object immutable.
