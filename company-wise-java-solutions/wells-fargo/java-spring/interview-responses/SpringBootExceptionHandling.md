# Spring Boot Exception Handling - Interview Response

## What Is It?

Handle exceptions consistently with @ControllerAdvice, domain-specific exceptions, client-safe error DTOs, and clear mapping to HTTP status codes.

## In Simple Terms

Handle exceptions consistently with @ControllerAdvice, domain-specific exceptions, client-safe error DTOs, and clear mapping to HTTP status codes.

## Why It Matters

Insufficient funds maps to 409, validation errors to 400, authentication failures to 401, and internal failures to 500.

If we get it wrong:

```text
Do not return raw exception messages to clients.
Do not map every failure to HTTP 500.
Do not forget validation and security exception handling.
```

## Example

```text
@ControllerAdvice
class ApiExceptionHandler {
@ExceptionHandler(InsufficientFundsException.class)
ResponseEntity<ApiError> insufficientFunds(...) { return status(CONFLICT).body(...); }
}
```

Key interview details:

- @ControllerAdvice, @ExceptionHandler, error DTO, status mapping, validation errors, no stack trace leaks.

## Safe vs Unsafe

Safe:

```text
Do not leak stack traces to clients.
Use one response shape for errors.
Log server-side details with correlation IDs.
Keep business exception names meaningful.
```

Unsafe:

```text
Do not return raw exception messages to clients.
Do not map every failure to HTTP 500.
Do not forget validation and security exception handling.
```

## Java / Spring Backend Use Case

Insufficient funds maps to 409, validation errors to 400, authentication failures to 401, and internal failures to 500.

Java/Spring angle:

```text
@ControllerAdvice
class ApiExceptionHandler {
@ExceptionHandler(InsufficientFundsException.class)
ResponseEntity<ApiError> insufficientFunds(...) { return status(CONFLICT).body(...); }
}
```

## Production Concerns

- Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
- Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
- Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
- Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.

## Common Mistakes

- Do not return raw exception messages to clients.
- Do not map every failure to HTTP 500.
- Do not forget validation and security exception handling.

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

In an interview, I would say: Handle exceptions consistently with @ControllerAdvice, domain-specific exceptions, client-safe error DTOs, and clear mapping to HTTP status codes. For example, Insufficient funds maps to 409, validation errors to 400, authentication failures to 401, and internal failures to 500. The main production risk is return raw exception messages to clients.
