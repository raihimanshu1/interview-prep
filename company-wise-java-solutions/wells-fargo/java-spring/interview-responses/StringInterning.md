# String Interning - Interview Response

## What Is It?

String interning stores canonical string instances so equal strings can share one object, but manual interning can increase memory pressure and contention if used blindly.

## In Simple Terms

String interning stores canonical string instances so equal strings can share one object, but manual interning can increase memory pressure and contention if used blindly.

## Why It Matters

Interning may help repeated small reference data codes, but it is dangerous for high-cardinality customer IDs or transaction references.

If we get it wrong:

```text
Do not intern user-generated unbounded values.
Do not compare strings with == except when identity is intentionally guaranteed.
Do not optimize memory without measuring retained strings.
```

## Example

```text
String normalized = currencyCode.toUpperCase(Locale.ROOT);
Safe only for bounded sets like ISO currency codes.
String canonical = normalized.intern();
```

Key interview details:

- string pool, == vs equals, intern(), memory risk, high-cardinality input.

## Safe vs Unsafe

Safe:

```text
String literals are interned automatically by the JVM.
String.intern returns a canonical instance from the string pool.
Interning high-cardinality or unbounded data can retain too many strings.
Use profiling before applying interning as a memory optimization.
```

Unsafe:

```text
Do not intern user-generated unbounded values.
Do not compare strings with == except when identity is intentionally guaranteed.
Do not optimize memory without measuring retained strings.
```

## Java / Spring Backend Use Case

Interning may help repeated small reference data codes, but it is dangerous for high-cardinality customer IDs or transaction references.

Java/Spring angle:

```text
String normalized = currencyCode.toUpperCase(Locale.ROOT);
Safe only for bounded sets like ISO currency codes.
String canonical = normalized.intern();
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not intern user-generated unbounded values.
- Do not compare strings with == except when identity is intentionally guaranteed.
- Do not optimize memory without measuring retained strings.

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

In an interview, I would say: String interning stores canonical string instances so equal strings can share one object, but manual interning can increase memory pressure and contention if used blindly. For example, Interning may help repeated small reference data codes, but it is dangerous for high-cardinality customer IDs or transaction references. The main production risk is intern user-generated unbounded values.
