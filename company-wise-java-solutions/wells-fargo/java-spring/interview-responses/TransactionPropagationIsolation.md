# Transaction Propagation Isolation - Interview Response

## What Is It?

Propagation controls whether a method joins, creates, suspends, or forbids a transaction; isolation controls what concurrent database changes are visible inside a transaction.

## In Simple Terms

Propagation controls whether a method joins, creates, suspends, or forbids a transaction; isolation controls what concurrent database changes are visible inside a transaction.

## Why It Matters

A transfer use case may use REQUIRED for debit/credit, while an audit write may use REQUIRES_NEW so it commits independently.

If we get it wrong:

```text
Do not use high isolation everywhere without measuring lock impact.
Do not assume REQUIRES_NEW participates in the outer rollback.
Do not call transactional methods through self-invocation and expect proxy behavior.
```

## Example

```text
@Transactional(propagation = REQUIRED, isolation = READ_COMMITTED)
public void transfer(...) { debit(); credit(); }
@Transactional(propagation = REQUIRES_NEW)
public void writeAudit(...) { ... }
```

Key interview details:

- REQUIRED, REQUIRES_NEW, MANDATORY, NESTED, dirty/non-repeatable/phantom reads.

## Safe vs Unsafe

Safe:

```text
REQUIRED joins an existing transaction or creates one.
REQUIRES_NEW suspends the current transaction and starts a new one.
READ_COMMITTED avoids dirty reads in many databases.
SERIALIZABLE is strongest but can reduce throughput and increase retries.
```

Unsafe:

```text
Do not use high isolation everywhere without measuring lock impact.
Do not assume REQUIRES_NEW participates in the outer rollback.
Do not call transactional methods through self-invocation and expect proxy behavior.
```

## Java / Spring Backend Use Case

A transfer use case may use REQUIRED for debit/credit, while an audit write may use REQUIRES_NEW so it commits independently.

Java/Spring angle:

```text
@Transactional(propagation = REQUIRED, isolation = READ_COMMITTED)
public void transfer(...) { debit(); credit(); }
@Transactional(propagation = REQUIRES_NEW)
public void writeAudit(...) { ... }
```

## Production Concerns

- Add rollback rules: unchecked exceptions roll back by default, checked exceptions need rollbackFor unless configured.
- Explain Spring proxy boundaries, including self-invocation and private method traps.
- Discuss isolation anomalies: dirty read, non-repeatable read, phantom read, and database-specific behavior.
- Production answer: keep transactions short, retry deadlocks/serialization failures, and avoid remote calls while holding DB locks.

## Common Mistakes

- Do not use high isolation everywhere without measuring lock impact.
- Do not assume REQUIRES_NEW participates in the outer rollback.
- Do not call transactional methods through self-invocation and expect proxy behavior.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Transaction Propagation Isolation changes are deployed.
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

In an interview, I would say: Propagation controls whether a method joins, creates, suspends, or forbids a transaction; isolation controls what concurrent database changes are visible inside a transaction. For example, a transfer use case may use REQUIRED for debit/credit, while an audit write may use REQUIRES_NEW so it commits independently. The main production risk is use high isolation everywhere without measuring lock impact.
