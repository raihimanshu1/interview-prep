# Lambdas And Functional Interfaces - Interview Response

## What Is It?

A lambda is an inline implementation of a functional interface, which is an interface with exactly one abstract method.

## In Simple Terms

A lambda is an inline implementation of a functional interface, which is an interface with exactly one abstract method.

## Why It Matters

Use lambdas for small behavior parameters such as filtering suspicious transactions.

If we get it wrong:

```text
Do not put large business workflows inside one lambda.
Do not mutate shared state from a lambda in a parallel stream.
Do not forget @FunctionalInterface can catch accidental extra abstract methods.
```

## Example

```text
Predicate<Transaction> highValue = tx -> tx.amount().compareTo(limit) > 0;
transactions.stream().filter(highValue).toList();
```

Key interview details:

- Predicate, Function, Consumer, custom functional interface, captured variable rules.

## Safe vs Unsafe

Safe:

```text
Keep lambdas short and side-effect light.
Use named methods when logic needs explanation or reuse.
Functional interfaces enable strategy-style behavior.
Be careful capturing mutable variables from outer scope.
```

Unsafe:

```text
Do not put large business workflows inside one lambda.
Do not mutate shared state from a lambda in a parallel stream.
Do not forget @FunctionalInterface can catch accidental extra abstract methods.
```

## Java / Spring Backend Use Case

Use lambdas for small behavior parameters such as filtering suspicious transactions.

Java/Spring angle:

```text
Predicate<Transaction> highValue = tx -> tx.amount().compareTo(limit) > 0;
transactions.stream().filter(highValue).toList();
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not put large business workflows inside one lambda.
- Do not mutate shared state from a lambda in a parallel stream.
- Do not forget @FunctionalInterface can catch accidental extra abstract methods.

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

In an interview, I would say: A lambda is an inline implementation of a functional interface, which is an interface with exactly one abstract method. For example, use lambdas for small behavior parameters such as filtering suspicious transactions. The main production risk is put large business workflows inside one lambda.
