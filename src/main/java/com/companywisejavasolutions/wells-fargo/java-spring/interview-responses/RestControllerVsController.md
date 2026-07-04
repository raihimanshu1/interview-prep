# Rest Controller Vs Controller - Interview Response

## What Is It?

@RestController combines @Controller and @ResponseBody, so methods return response bodies; @Controller typically returns views unless @ResponseBody is used.

## In Simple Terms

@RestController combines @Controller and @ResponseBody, so methods return response bodies; @Controller typically returns views unless @ResponseBody is used.

## Why It Matters

A JSON payment API uses @RestController; an MVC web page controller returns a template name.

If we get it wrong:

```text
Do not use @Controller for JSON endpoints unless you also use @ResponseBody.
Do not put business logic in controllers.
Do not return entities directly from APIs.
```

## Example

```text
@RestController
class TransferController {
@PostMapping("/transfers")
TransferResponse create(@Valid @RequestBody TransferRequest request) { ... }
}
```

Key interview details:

- @ResponseBody, view resolution, JSON response, Thymeleaf/page use case.

## Safe vs Unsafe

Safe:

```text
Use @RestController for REST APIs.
Use @Controller for server-rendered pages.
Keep HTTP concerns at this layer.
Return DTOs, not persistence entities.
```

Unsafe:

```text
Do not use @Controller for JSON endpoints unless you also use @ResponseBody.
Do not put business logic in controllers.
Do not return entities directly from APIs.
```

## Java / Spring Backend Use Case

A JSON payment API uses @RestController; an MVC web page controller returns a template name.

Java/Spring angle:

```text
@RestController
class TransferController {
@PostMapping("/transfers")
TransferResponse create(@Valid @RequestBody TransferRequest request) { ... }
}
```

## Production Concerns

- Keep external contracts stable during deployment.
- Use DTOs instead of exposing entities or internal models.
- Add contract tests for public APIs and events.
- Define rollback, deprecation, and client migration plans.
- Monitor old-client usage before removing old versions.

## Common Mistakes

- Do not use @Controller for JSON endpoints unless you also use @ResponseBody.
- Do not put business logic in controllers.
- Do not return entities directly from APIs.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Rest Controller Vs Controller changes are deployed.
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

In an interview, I would say: @RestController combines @Controller and @ResponseBody, so methods return response bodies; @Controller typically returns views unless @ResponseBody is used. For example, a JSON payment API uses @RestController; an MVC web page controller returns a template name. The main production risk is use @Controller for JSON endpoints unless you also use @ResponseBody.
