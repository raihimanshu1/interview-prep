# Transactional Basics - Interview Response

## What Is It?

@Transactional wraps a Spring-managed method in a database transaction so work is committed on success and rolled back on configured failures.

## In Simple Terms

@Transactional wraps a Spring-managed method in a database transaction so work is committed on success and rolled back on configured failures.

## Why It Matters

A transfer service should debit, credit, and insert ledger rows in one transaction.

If we get it wrong:

```text
Do not put @Transactional on private methods and expect proxy advice.
Do not perform slow remote calls while holding database locks.
Do not assume checked exceptions roll back unless configured.
```

## Example

```text
@Transactional
public void transfer(Command command) {
debit(command); credit(command); ledgerRepository.insert(...);
}
```

Key interview details:

- proxy behavior, rollback rules, checked exceptions, self-invocation, transaction boundaries.

## Safe vs Unsafe

Safe:

```text
The annotation works through Spring proxies by default.
Rollback defaults to unchecked exceptions unless configured.
Keep transactions short and avoid slow remote calls inside them.
Put transaction boundaries at service use-case methods.
```

Unsafe:

```text
Do not put @Transactional on private methods and expect proxy advice.
Do not perform slow remote calls while holding database locks.
Do not assume checked exceptions roll back unless configured.
```

## Java / Spring Backend Use Case

A transfer service should debit, credit, and insert ledger rows in one transaction.

Java/Spring angle:

```text
@Transactional
public void transfer(Command command) {
debit(command); credit(command); ledgerRepository.insert(...);
}
```

## Production Concerns

- Keep external contracts stable during deployment.
- Use DTOs instead of exposing entities or internal models.
- Add contract tests for public APIs and events.
- Define rollback, deprecation, and client migration plans.
- Monitor old-client usage before removing old versions.

## Common Mistakes

- Do not put @Transactional on private methods and expect proxy advice.
- Do not perform slow remote calls while holding database locks.
- Do not assume checked exceptions roll back unless configured.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Transactional Basics changes are deployed.
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

In an interview, I would say: @Transactional wraps a Spring-managed method in a database transaction so work is committed on success and rolled back on configured failures. For example, a transfer service should debit, credit, and insert ledger rows in one transaction. The main production risk is put @Transactional on private methods and expect proxy advice.
