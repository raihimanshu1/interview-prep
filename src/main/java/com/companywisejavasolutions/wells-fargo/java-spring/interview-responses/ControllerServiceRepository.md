# Controller Service Repository - Interview Response

## What Is It?

Controller handles HTTP translation, service owns business use cases and transactions, and repository owns persistence queries.

## In Simple Terms

Controller handles HTTP translation, service owns business use cases and transactions, and repository owns persistence queries.

## Why It Matters

TransferController validates request shape, TransferService enforces balance/idempotency rules, and LedgerRepository writes ledger rows.

If we get it wrong:

```text
Do not put SQL in controllers.
Do not put business workflow decisions in repositories.
Do not return persistence entities directly from public APIs.
```

## Example

```text
Controller: HTTP request/response mapping.
Service: @Transactional use case, validation, idempotency, orchestration.
Repository: database reads/writes with clear query methods.
This separation makes unit, slice, and integration tests simpler.
```

## Safe vs Unsafe

Safe:

```text
Keep controllers thin and free of business rules.
Put transaction boundaries in the service layer.
Keep repositories focused on persistence, not workflow decisions.
Return DTOs at the API boundary and domain objects internally.
```

Unsafe:

```text
Do not put SQL in controllers.
Do not put business workflow decisions in repositories.
Do not return persistence entities directly from public APIs.
```

## Java / Spring Backend Use Case

TransferController validates request shape, TransferService enforces balance/idempotency rules, and LedgerRepository writes ledger rows.

Java/Spring angle:

```text
Controller: HTTP request/response mapping.
Service: @Transactional use case, validation, idempotency, orchestration.
Repository: database reads/writes with clear query methods.
This separation makes unit, slice, and integration tests simpler.
```

## Production Concerns

- Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
- Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
- Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
- Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.

## Common Mistakes

- Do not put SQL in controllers.
- Do not put business workflow decisions in repositories.
- Do not return persistence entities directly from public APIs.

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

- Dependency Injection
- Service layer
- Repository
- DTO/Adapter

## Follow-Up Interview Questions

- How does this behave under concurrent requests?
- What happens when a downstream service or database operation fails?
- How would you test this and prove it works in production?

## Interview Answer

In an interview, I would say: Controller handles HTTP translation, service owns business use cases and transactions, and repository owns persistence queries. For example, TransferController validates request shape, TransferService enforces balance/idempotency rules, and LedgerRepository writes ledger rows. The main production risk is put SQL in controllers.
