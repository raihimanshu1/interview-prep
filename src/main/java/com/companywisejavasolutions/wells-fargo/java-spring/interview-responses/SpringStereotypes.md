# Spring Stereotypes - Interview Response

## What Is It?

@Component is the generic stereotype; @Service marks business services; @Repository marks persistence components and enables exception translation; @Controller handles web requests.

## In Simple Terms

@Component is the generic stereotype; @Service marks business services; @Repository marks persistence components and enables exception translation; @Controller handles web requests.

## Why It Matters

Use @Service for TransferService and @Repository for LedgerRepository to communicate architectural intent.

If we get it wrong:

```text
Do not label every class @Component without intent.
Do not put controller logic in services or service logic in repositories.
Do not forget package scanning rules.
```

## Example

```text
@Service: business use case and transaction boundary.
@Repository: persistence access.
@Controller/@RestController: HTTP adapter.
@Component: generic infrastructure component.
```

Key interview details:

- @Component, @Service, @Repository, @Controller, exception translation, scanning.

## Safe vs Unsafe

Safe:

```text
Stereotypes aid scanning and readability.
@Repository can translate persistence exceptions.
@Controller participates in MVC request mapping.
Do not choose annotations randomly; match responsibility.
```

Unsafe:

```text
Do not label every class @Component without intent.
Do not put controller logic in services or service logic in repositories.
Do not forget package scanning rules.
```

## Java / Spring Backend Use Case

Use @Service for TransferService and @Repository for LedgerRepository to communicate architectural intent.

Java/Spring angle:

```text
@Service: business use case and transaction boundary.
@Repository: persistence access.
@Controller/@RestController: HTTP adapter.
@Component: generic infrastructure component.
```

## Production Concerns

- Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
- Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
- Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
- Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.

## Common Mistakes

- Do not label every class @Component without intent.
- Do not put controller logic in services or service logic in repositories.
- Do not forget package scanning rules.

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

In an interview, I would say: @Component is the generic stereotype; @Service marks business services; @Repository marks persistence components and enables exception translation; @Controller handles web requests. For example, use @Service for TransferService and @Repository for LedgerRepository to communicate architectural intent. The main production risk is label every class @Component without intent.
