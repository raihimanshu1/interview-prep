# Default Methods Compatibility - Interview Response

## What Is It?

Default methods let interfaces add behavior without breaking existing implementers, but they can create ambiguity and should not become hidden business logic.

## In Simple Terms

Default methods let interfaces add behavior without breaking existing implementers, but they can create ambiguity and should not become hidden business logic.

## Why It Matters

Adding default supportsCurrency to PaymentGateway can keep old implementations compiling while new ones override when needed.

If we get it wrong:

```text
Do not hide complex rules in default methods.
Do not ignore conflicting defaults from multiple interfaces.
Do not use defaults as a replacement for proper service design.
```

## Example

```text
interface PaymentGateway {
default boolean supportsCurrency(String currency) {
return "USD".equals(currency);
}
}
```

## Safe vs Unsafe

Safe:

```text
Use defaults for small compatibility behavior.
Document when implementations should override.
Resolve diamond conflicts explicitly.
Avoid default methods that depend on mutable implementation state.
```

Unsafe:

```text
Do not hide complex rules in default methods.
Do not ignore conflicting defaults from multiple interfaces.
Do not use defaults as a replacement for proper service design.
```

## Java / Spring Backend Use Case

Adding default supportsCurrency to PaymentGateway can keep old implementations compiling while new ones override when needed.

Java/Spring angle:

```text
interface PaymentGateway {
default boolean supportsCurrency(String currency) {
return "USD".equals(currency);
}
}
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not hide complex rules in default methods.
- Do not ignore conflicting defaults from multiple interfaces.
- Do not use defaults as a replacement for proper service design.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Default Methods Compatibility changes are deployed.
Avoid removing fields, renaming fields, changing meanings, or making optional inputs required without a versioned rollout.
```

Semantic versioning:

```text
MAJOR -> breaking API/event/library contract change
MINOR -> backward-compatible capability or optional field
PATCH -> bug fix, tuning, or internal implementation improvement
```

Big-company API evolution mindset:

```text
Amazon/Google-style evolution usually favors additive contracts, consumer-driven tests, telemetry on old client usage, deprecation windows, gradual rollout, and rollback paths.
```

Related patterns:

- Adapter
- Facade
- Consumer-driven contracts
- Strangler migration

## Follow-Up Interview Questions

- How does this behave under concurrent requests?
- What happens when a downstream service or database operation fails?
- How would you test this and prove it works in production?

## Interview Answer

In an interview, I would say: Default methods let interfaces add behavior without breaking existing implementers, but they can create ambiguity and should not become hidden business logic. For example, Adding default supportsCurrency to PaymentGateway can keep old implementations compiling while new ones override when needed. The main production risk is hide complex rules in default methods.
