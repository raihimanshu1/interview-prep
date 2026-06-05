# Transactional Self Invocation - Interview Response

## What Is It?

Self-invocation bypasses Spring proxy advice, so calling a @Transactional method from another method in the same class may not start a transaction.

## In Simple Terms

Self-invocation bypasses Spring proxy advice, so calling a @Transactional method from another method in the same class may not start a transaction.

## Why It Matters

A public method this.writeAudit() inside the same service will not trigger REQUIRES_NEW proxy behavior.

If we get it wrong:

```text
Do not call this.transactionalMethod() and expect Spring proxy advice.
Do not solve this by making methods public only; proxy routing still matters.
Do not skip integration tests for transaction boundaries.
```

## Example

```text
Better: AuditService is a separate bean.
transferService calls auditService.writeAuditRequiresNew(...).
Spring proxy can then apply the transactional advice.
```

Key interview details:

- same-class call bypasses proxy, move method to another bean, self-injection/AopContext caveats.

## Safe vs Unsafe

Safe:

```text
Move transactional method to another Spring bean.
Call through the proxied bean only when appropriate.
Prefer clear service boundaries over proxy tricks.
Write integration tests for transaction propagation behavior.
```

Unsafe:

```text
Do not call this.transactionalMethod() and expect Spring proxy advice.
Do not solve this by making methods public only; proxy routing still matters.
Do not skip integration tests for transaction boundaries.
```

## Java / Spring Backend Use Case

A public method this.writeAudit() inside the same service will not trigger REQUIRES_NEW proxy behavior.

Java/Spring angle:

```text
Better: AuditService is a separate bean.
transferService calls auditService.writeAuditRequiresNew(...).
Spring proxy can then apply the transactional advice.
```

## Production Concerns

- Keep external contracts stable during deployment.
- Use DTOs instead of exposing entities or internal models.
- Add contract tests for public APIs and events.
- Define rollback, deprecation, and client migration plans.
- Monitor old-client usage before removing old versions.

## Common Mistakes

- Do not call this.transactionalMethod() and expect Spring proxy advice.
- Do not solve this by making methods public only; proxy routing still matters.
- Do not skip integration tests for transaction boundaries.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Transactional Self Invocation changes are deployed.
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

In an interview, I would say: Self-invocation bypasses Spring proxy advice, so calling a @Transactional method from another method in the same class may not start a transaction. For example, a public method this.writeAudit() inside the same service will not trigger REQUIRES_NEW proxy behavior. The main production risk is call this.transactionalMethod() and expect Spring proxy advice.
