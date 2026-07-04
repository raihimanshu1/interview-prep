# Spring Security JWT Scopes - Interview Response

## What Is It?

Spring Security commonly maps OAuth2 JWT scope claims into authorities such as SCOPE_transfer:write, which are then used for method or endpoint authorization.

## In Simple Terms

Spring Security commonly maps OAuth2 JWT scope claims into authorities such as SCOPE_transfer:write, which are then used for method or endpoint authorization.

## Why It Matters

A transfer creation endpoint should require SCOPE_transfer:write while a statement endpoint may require SCOPE_statement:read.

If we get it wrong:

```text
Do not trust unsigned or unvalidated JWT claims.
Do not confuse roles and scopes without a mapping policy.
Do not put authorization only in controllers if services are reused.
```

## Example

```text
@PreAuthorize("hasAuthority('SCOPE_transfer:write')")
TransferResponse createTransfer(TransferRequest request) {
return service.create(request);
}
```

Key interview details:

- scope claim to SCOPE_ authority, hasAuthority, issuer/audience validation.

## Safe vs Unsafe

Safe:

```text
Authentication proves caller identity; authorization checks allowed action.
JWT converters map claims to GrantedAuthority values.
Method security can enforce fine-grained rules with @PreAuthorize.
Always validate issuer, audience, signature, and expiry before trusting claims.
```

Unsafe:

```text
Do not trust unsigned or unvalidated JWT claims.
Do not confuse roles and scopes without a mapping policy.
Do not put authorization only in controllers if services are reused.
```

## Java / Spring Backend Use Case

A transfer creation endpoint should require SCOPE_transfer:write while a statement endpoint may require SCOPE_statement:read.

Java/Spring angle:

```text
@PreAuthorize("hasAuthority('SCOPE_transfer:write')")
TransferResponse createTransfer(TransferRequest request) {
return service.create(request);
}
```

## Production Concerns

- Keep external contracts stable during deployment.
- Use DTOs instead of exposing entities or internal models.
- Add contract tests for public APIs and events.
- Define rollback, deprecation, and client migration plans.
- Monitor old-client usage before removing old versions.

## Common Mistakes

- Do not trust unsigned or unvalidated JWT claims.
- Do not confuse roles and scopes without a mapping policy.
- Do not put authorization only in controllers if services are reused.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Spring Security JWT Scopes changes are deployed.
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

- What breaks under high concurrency or partial failure?
- How would you verify this with tests, metrics, logs, or traces?
- What trade-off would make you choose a different design?

## Interview Answer

In an interview, I would say: Spring Security commonly maps OAuth2 JWT scope claims into authorities such as SCOPE_transfer:write, which are then used for method or endpoint authorization. For example, a transfer creation endpoint should require SCOPE_transfer:write while a statement endpoint may require SCOPE_statement:read. The main production risk is trust unsigned or unvalidated JWT claims.
