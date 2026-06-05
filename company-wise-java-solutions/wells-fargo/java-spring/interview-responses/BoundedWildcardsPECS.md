# Bounded Wildcards PECS - Interview Response

## What Is It?

PECS means use ? extends T when a generic source produces T values, and ? super T when a destination consumes T values.

## In Simple Terms

PECS means use ? extends T when a generic source produces T values, and ? super T when a destination consumes T values.

## Why It Matters

A pricing method reading AccountEvent values can accept List<? extends AccountEvent>, while a method adding FraudEvent values can accept Collection<? super FraudEvent>.

If we get it wrong:

```text
Do not use wildcards everywhere by default.
Do not confuse extends with inheritance-only thinking.
Do not expose raw collections.
```

## Example

```text
void copyFraudEvents(List<? extends FraudEvent> source, Collection<? super FraudEvent> target) {
for (FraudEvent event : source) {
target.add(event);
}
}
```

## Safe vs Unsafe

Safe:

```text
? extends improves read flexibility but prevents adding most values.
? super allows adding a specific subtype but reads come back as Object unless cast.
Use invariance carefully: List<PremiumAccount> is not a List<Account>.
This is an API design topic, not only syntax.
```

Unsafe:

```text
Do not use wildcards everywhere by default.
Do not confuse extends with inheritance-only thinking.
Do not expose raw collections.
```

## Java / Spring Backend Use Case

A pricing method reading AccountEvent values can accept List<? extends AccountEvent>, while a method adding FraudEvent values can accept Collection<? super FraudEvent>.

Java/Spring angle:

```text
void copyFraudEvents(List<? extends FraudEvent> source, Collection<? super FraudEvent> target) {
for (FraudEvent event : source) {
target.add(event);
}
}
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not use wildcards everywhere by default.
- Do not confuse extends with inheritance-only thinking.
- Do not expose raw collections.

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

- What breaks under high concurrency or partial failure?
- How would you verify this with tests, metrics, logs, or traces?
- What trade-off would make you choose a different design?

## Interview Answer

In an interview, I would say: PECS means use ? extends T when a generic source produces T values, and ? super T when a destination consumes T values. For example, a pricing method reading AccountEvent values can accept List<? extends AccountEvent>, while a method adding FraudEvent values can accept Collection<? super FraudEvent>. The main production risk is use wildcards everywhere by default.
