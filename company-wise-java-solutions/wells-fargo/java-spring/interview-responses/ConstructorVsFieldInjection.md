# Constructor Vs Field Injection - Interview Response

## What Is It?

Constructor injection makes dependencies explicit, supports final fields, improves testability, and fails fast when required dependencies are missing.

## In Simple Terms

Constructor injection makes dependencies explicit, supports final fields, improves testability, and fails fast when required dependencies are missing.

## Why It Matters

A TransferService should receive repositories, gateways, and clocks through its constructor.

If we get it wrong:

```text
Do not hide required dependencies as private autowired fields.
Do not let one service grow until the constructor has too many responsibilities.
Do not manually new infrastructure collaborators inside business services.
```

## Example

```text
final class TransferService {
private final LedgerRepository ledgerRepository;
TransferService(LedgerRepository ledgerRepository) {
this.ledgerRepository = Objects.requireNonNull(ledgerRepository);
}
}
```

## Safe vs Unsafe

Safe:

```text
Prefer constructor injection for required dependencies.
Use field injection only in legacy code or framework-managed tests when unavoidable.
Keep constructors small by reducing service responsibility if dependencies explode.
Inject Clock for testable time-dependent logic.
```

Unsafe:

```text
Do not hide required dependencies as private autowired fields.
Do not let one service grow until the constructor has too many responsibilities.
Do not manually new infrastructure collaborators inside business services.
```

## Java / Spring Backend Use Case

A TransferService should receive repositories, gateways, and clocks through its constructor.

Java/Spring angle:

```text
final class TransferService {
private final LedgerRepository ledgerRepository;
TransferService(LedgerRepository ledgerRepository) {
this.ledgerRepository = Objects.requireNonNull(ledgerRepository);
}
}
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not hide required dependencies as private autowired fields.
- Do not let one service grow until the constructor has too many responsibilities.
- Do not manually new infrastructure collaborators inside business services.

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

In an interview, I would say: Constructor injection makes dependencies explicit, supports final fields, improves testability, and fails fast when required dependencies are missing. For example, a TransferService should receive repositories, gateways, and clocks through its constructor. The main production risk is hide required dependencies as private autowired fields.
