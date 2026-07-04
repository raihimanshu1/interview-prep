# Sealed Classes - Interview Response

## What Is It?

Sealed classes restrict which classes can extend or implement a type, making domain variants explicit and safer to exhaustively handle.

## In Simple Terms

Sealed classes restrict which classes can extend or implement a type, making domain variants explicit and safer to exhaustively handle.

## Why It Matters

A PaymentResult can be limited to Approved, Rejected, and PendingReview variants.

If we get it wrong:

```text
Do not seal a hierarchy that needs external extension.
Do not use sealed classes as a substitute for clear domain modeling.
Do not forget permitted subclass/module rules.
```

## Example

```text
sealed interface PaymentResult permits Approved, Rejected { }
record Approved(String authorizationId) implements PaymentResult { }
record Rejected(String reason) implements PaymentResult { }
```

Key interview details:

- PaymentMethod permits Card, Upi, Wire and switch exhaustiveness.

## Safe vs Unsafe

Safe:

```text
Use sealed types for closed domain hierarchies.
They improve switch exhaustiveness with modern Java.
Do not use when external teams must add implementations freely.
Keep permitted subclasses in the same module/package rules.
```

Unsafe:

```text
Do not seal a hierarchy that needs external extension.
Do not use sealed classes as a substitute for clear domain modeling.
Do not forget permitted subclass/module rules.
```

## Java / Spring Backend Use Case

A PaymentResult can be limited to Approved, Rejected, and PendingReview variants.

Java/Spring angle:

```text
sealed interface PaymentResult permits Approved, Rejected { }
record Approved(String authorizationId) implements PaymentResult { }
record Rejected(String reason) implements PaymentResult { }
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not seal a hierarchy that needs external extension.
- Do not use sealed classes as a substitute for clear domain modeling.
- Do not forget permitted subclass/module rules.

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

In an interview, I would say: Sealed classes restrict which classes can extend or implement a type, making domain variants explicit and safer to exhaustively handle. For example, a PaymentResult can be limited to Approved, Rejected, and PendingReview variants. The main production risk is seal a hierarchy that needs external extension.
