# Records Usage - Interview Response

## What Is It?

Records are concise immutable data carriers that generate constructor, accessors, equals, hashCode, and toString from declared components.

## In Simple Terms

Records are concise immutable data carriers that generate constructor, accessors, equals, hashCode, and toString from declared components.

## Why It Matters

Use records for API DTOs, query projections, and small value objects when behavior is minimal.

If we get it wrong:

```text
Do not treat records as deeply immutable when components are mutable.
Do not use records for mutable persistence entities.
Do not skip constructor validation for value objects.
```

## Example

```text
record TransferRequest(String fromAccount, String toAccount, BigDecimal amount) {
TransferRequest {
Objects.requireNonNull(fromAccount);
Objects.requireNonNull(toAccount);
if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be positive");
}
}
```

Key interview details:

- DTO/value object examples, JPA entity limitation, validation/serialization.

## Safe vs Unsafe

Safe:

```text
Records are shallowly immutable; mutable components still need care.
Validate invariants in a compact constructor.
Do not use records for JPA entities that need proxy/mutable lifecycle behavior.
Keep domain behavior in richer classes when invariants are complex.
```

Unsafe:

```text
Do not treat records as deeply immutable when components are mutable.
Do not use records for mutable persistence entities.
Do not skip constructor validation for value objects.
```

## Java / Spring Backend Use Case

Use records for API DTOs, query projections, and small value objects when behavior is minimal.

Java/Spring angle:

```text
record TransferRequest(String fromAccount, String toAccount, BigDecimal amount) {
TransferRequest {
Objects.requireNonNull(fromAccount);
Objects.requireNonNull(toAccount);
if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be positive");
}
}
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not treat records as deeply immutable when components are mutable.
- Do not use records for mutable persistence entities.
- Do not skip constructor validation for value objects.

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

In an interview, I would say: Records are concise immutable data carriers that generate constructor, accessors, equals, hashCode, and toString from declared components. For example, use records for API DTOs, query projections, and small value objects when behavior is minimal. The main production risk is treat records as deeply immutable when components are mutable.
