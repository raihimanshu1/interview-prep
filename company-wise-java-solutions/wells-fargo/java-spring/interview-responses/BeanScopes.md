# Bean Scopes - Interview Response

## What Is It?

Spring bean scope controls lifecycle and sharing: singleton is one bean per container, prototype creates new instances, request is per HTTP request, and session is per user session.

## In Simple Terms

Spring bean scope controls lifecycle and sharing: singleton is one bean per container, prototype creates new instances, request is per HTTP request, and session is per user session.

## Why It Matters

Most stateless services should be singleton; request-specific state should live in method variables or request-scoped objects.

If we get it wrong:

```text
Do not store request data in singleton fields.
Do not assume prototype dependencies are recreated on every singleton method call.
Do not use session scope casually in stateless APIs.
```

## Example

```text
Production rule: singleton service fields should be collaborators, not per-request data.
class TransferService {
private final LedgerRepository ledgerRepository;
request data stays inside method parameters/local variables
}
```

Key interview details:

- singleton, prototype, request, session examples and mutable state bug in singleton beans.

## Safe vs Unsafe

Safe:

```text
Use singleton for stateless services and repositories.
Avoid mutable per-request fields in singleton beans.
Inject prototype/request beans carefully because lifecycle differs.
Keep session scope rare in backend APIs.
```

Unsafe:

```text
Do not store request data in singleton fields.
Do not assume prototype dependencies are recreated on every singleton method call.
Do not use session scope casually in stateless APIs.
```

## Java / Spring Backend Use Case

Most stateless services should be singleton; request-specific state should live in method variables or request-scoped objects.

Java/Spring angle:

```text
Production rule: singleton service fields should be collaborators, not per-request data.
class TransferService {
private final LedgerRepository ledgerRepository;
request data stays inside method parameters/local variables
}
```

## Production Concerns

- Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
- Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
- Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
- Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.

## Common Mistakes

- Do not store request data in singleton fields.
- Do not assume prototype dependencies are recreated on every singleton method call.
- Do not use session scope casually in stateless APIs.

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

In an interview, I would say: Spring bean scope controls lifecycle and sharing: singleton is one bean per container, prototype creates new instances, request is per HTTP request, and session is per user session. For example, Most stateless services should be singleton; request-specific state should live in method variables or request-scoped objects. The main production risk is store request data in singleton fields.
