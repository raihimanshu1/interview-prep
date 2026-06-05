# Overloading Vs Overriding - Interview Response

## What Is It?

Overloading is compile-time method selection by parameter list; overriding is runtime dispatch where a subclass provides behavior for an inherited method contract.

## In Simple Terms

Overloading is compile-time method selection by parameter list; overriding is runtime dispatch where a subclass provides behavior for an inherited method contract.

## Why It Matters

A NotificationService may overload send methods for different request shapes, while EmailNotification overrides a common deliver contract.

If we get it wrong:

```text
Do not confuse static overload selection with dynamic overriding.
Do not reduce method visibility when overriding.
Avoid overloads that differ only by nullable parameters.
```

## Example

```text
class FeeCalculator { Money calculate(Account account) { return Money.ZERO; } }
final class PremiumFeeCalculator extends FeeCalculator {
@Override Money calculate(Account account) { return Money.ZERO; }
}
```

Key interview details:

- compile-time vs runtime dispatch examples.

## Safe vs Unsafe

Safe:

```text
Overloading improves readability only when the methods represent the same operation with different inputs.
Overriding requires the same signature and respects visibility, return-type, and exception rules.
Runtime polymorphism means a parent reference can call the child implementation.
Use @Override so compiler catches accidental signature mistakes.
```

Unsafe:

```text
Do not confuse static overload selection with dynamic overriding.
Do not reduce method visibility when overriding.
Avoid overloads that differ only by nullable parameters.
```

## Java / Spring Backend Use Case

A NotificationService may overload send methods for different request shapes, while EmailNotification overrides a common deliver contract.

Java/Spring angle:

```text
class FeeCalculator { Money calculate(Account account) { return Money.ZERO; } }
final class PremiumFeeCalculator extends FeeCalculator {
@Override Money calculate(Account account) { return Money.ZERO; }
}
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not confuse static overload selection with dynamic overriding.
- Do not reduce method visibility when overriding.
- Avoid overloads that differ only by nullable parameters.

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

In an interview, I would say: Overloading is compile-time method selection by parameter list; overriding is runtime dispatch where a subclass provides behavior for an inherited method contract. For example, a NotificationService may overload send methods for different request shapes, while EmailNotification overrides a common deliver contract. The main production risk is confuse static overload selection with dynamic overriding.
