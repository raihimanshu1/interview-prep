# Interface Vs Abstract Class - Interview Response

## What Is It?

Use an interface for a capability contract and an abstract class when implementations must share state, constructors, or protected template behavior.

## In Simple Terms

Use an interface for a capability contract and an abstract class when implementations must share state, constructors, or protected template behavior.

## Why It Matters

PaymentGateway can be an interface; AbstractRetryingGateway can share retry bookkeeping only if every gateway truly needs it.

If we get it wrong:

```text
Do not put mutable shared state in interface default methods.
Prefer composition when sharing behavior is optional.
Avoid exposing implementation details through abstract protected APIs.
```

## Example

```text
interface FraudCheck { boolean approve(Transaction tx); }
abstract class AuditedFraudCheck implements FraudCheck {
public final boolean approve(Transaction tx) {
return doApprove(tx);
}
protected abstract boolean doApprove(Transaction tx);
}
```

Key interview details:

- Java 8 default methods, Spring service ports, skeletal implementation.

## Safe vs Unsafe

Safe:

```text
Interfaces define what a type can do and support multiple inheritance of type.
Abstract classes define partial implementation and shared protected behavior.
Java 8 default methods help evolve interfaces, but they should stay small and compatibility-focused.
In Spring services, interfaces are useful for ports; abstract classes are useful for skeletal domain algorithms.
```

Unsafe:

```text
Do not put mutable shared state in interface default methods.
Prefer composition when sharing behavior is optional.
Avoid exposing implementation details through abstract protected APIs.
```

## Java / Spring Backend Use Case

PaymentGateway can be an interface; AbstractRetryingGateway can share retry bookkeeping only if every gateway truly needs it.

Java/Spring angle:

```text
interface FraudCheck { boolean approve(Transaction tx); }
abstract class AuditedFraudCheck implements FraudCheck {
public final boolean approve(Transaction tx) {
return doApprove(tx);
}
protected abstract boolean doApprove(Transaction tx);
}
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not put mutable shared state in interface default methods.
- Prefer composition when sharing behavior is optional.
- Avoid exposing implementation details through abstract protected APIs.

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

In an interview, I would say: Use an interface for a capability contract and an abstract class when implementations must share state, constructors, or protected template behavior. For example, PaymentGateway can be an interface; AbstractRetryingGateway can share retry bookkeeping only if every gateway truly needs it. The main production risk is put mutable shared state in interface default methods.
