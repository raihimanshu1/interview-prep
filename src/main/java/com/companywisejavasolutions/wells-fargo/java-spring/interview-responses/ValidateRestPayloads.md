# Validate Rest Payloads - Interview Response

## What Is It?

Validate REST payloads with Bean Validation annotations, explicit domain validation, sanitized error responses, and validation at every external boundary.

## In Simple Terms

Validate REST payloads with Bean Validation annotations, explicit domain validation, sanitized error responses, and validation at every external boundary.

## Why It Matters

A transfer request should validate account IDs, positive amount, supported currency, and idempotency key before calling the service.

If we get it wrong:

```text
Do not trust client-side validation.
Do not put all validation in controllers when domain invariants belong in services.
Do not return inconsistent validation error shapes.
```

## Example

```text
record TransferRequest(
@NotBlank String fromAccount,
@NotBlank String toAccount,
@Positive BigDecimal amount
) { }
```

Key interview details:

- DTO annotations, @Valid, custom validator, ControllerAdvice validation response.

## Safe vs Unsafe

Safe:

```text
Use @Valid on request bodies.
Use DTO annotations for shape constraints.
Keep cross-field/domain validation in service or custom validator.
Return consistent validation error responses.
```

Unsafe:

```text
Do not trust client-side validation.
Do not put all validation in controllers when domain invariants belong in services.
Do not return inconsistent validation error shapes.
```

## Java / Spring Backend Use Case

A transfer request should validate account IDs, positive amount, supported currency, and idempotency key before calling the service.

Java/Spring angle:

```text
record TransferRequest(
@NotBlank String fromAccount,
@NotBlank String toAccount,
@Positive BigDecimal amount
) { }
```

## Production Concerns

- Keep external contracts stable during deployment.
- Use DTOs instead of exposing entities or internal models.
- Add contract tests for public APIs and events.
- Define rollback, deprecation, and client migration plans.
- Monitor old-client usage before removing old versions.

## Common Mistakes

- Do not trust client-side validation.
- Do not put all validation in controllers when domain invariants belong in services.
- Do not return inconsistent validation error shapes.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Validate Rest Payloads changes are deployed.
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

In an interview, I would say: Validate REST payloads with Bean Validation annotations, explicit domain validation, sanitized error responses, and validation at every external boundary. For example, a transfer request should validate account IDs, positive amount, supported currency, and idempotency key before calling the service. The main production risk is trust client-side validation.
