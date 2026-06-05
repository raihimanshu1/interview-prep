# Pattern Matching Switch - Interview Response

## What Is It?

Pattern matching for switch lets switch branch on type patterns and bind typed variables, making closed-domain logic clearer and safer than long instanceof/cast chains.

## In Simple Terms

Pattern matching for switch lets switch branch on type patterns and bind typed variables, making closed-domain logic clearer and safer than long instanceof/cast chains.

## Why It Matters

A payment result sealed hierarchy can be handled with exhaustive switch branches for Approved, Rejected, and PendingReview.

If we get it wrong:

```text
Do not replace simple polymorphism with a giant switch everywhere.
Do not forget null handling.
Do not write non-exhaustive handling for a closed domain model.
```

## Example

```text
return switch (result) {
case Approved approved -> approved.authorizationId();
case Rejected rejected -> rejected.reason();
case PendingReview ignored -> "manual-review";
};
```

Key interview details:

- Java 21 sealed class and exhaustive switch example.

## Safe vs Unsafe

Safe:

```text
It combines type test and variable binding.
With sealed classes, switch can become exhaustive because the compiler knows all permitted subtypes.
Guards allow additional conditions but should not hide complex business rules.
Null handling is explicit, which avoids surprising NullPointerException behavior.
```

Unsafe:

```text
Do not replace simple polymorphism with a giant switch everywhere.
Do not forget null handling.
Do not write non-exhaustive handling for a closed domain model.
```

## Java / Spring Backend Use Case

A payment result sealed hierarchy can be handled with exhaustive switch branches for Approved, Rejected, and PendingReview.

Java/Spring angle:

```text
return switch (result) {
case Approved approved -> approved.authorizationId();
case Rejected rejected -> rejected.reason();
case PendingReview ignored -> "manual-review";
};
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not replace simple polymorphism with a giant switch everywhere.
- Do not forget null handling.
- Do not write non-exhaustive handling for a closed domain model.

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

In an interview, I would say: Pattern matching for switch lets switch branch on type patterns and bind typed variables, making closed-domain logic clearer and safer than long instanceof/cast chains. For example, a payment result sealed hierarchy can be handled with exhaustive switch branches for Approved, Rejected, and PendingReview. The main production risk is replace simple polymorphism with a giant switch everywhere.
