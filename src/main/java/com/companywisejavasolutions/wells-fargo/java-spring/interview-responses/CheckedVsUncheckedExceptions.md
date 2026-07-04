# Checked Vs Unchecked Exceptions - Interview Response

## What Is It?

Checked exceptions force callers to handle recoverable conditions; unchecked exceptions are better for programming errors and most service-layer business failures translated at boundaries.

## In Simple Terms

Checked exceptions force callers to handle recoverable conditions; unchecked exceptions are better for programming errors and most service-layer business failures translated at boundaries.

## Why It Matters

A service can throw a domain exception for insufficient funds and a controller advice can map it to HTTP 409.

If we get it wrong:

```text
Do not leak low-level exceptions through service APIs.
Do not catch Exception and continue silently.
Do not log the same exception repeatedly at every layer.
```

## Example

```text
try {
transferService.transfer(command);
} catch (InsufficientFundsException ex) {
Map to a client-safe response; do not leak internal stack details.
return "409 CONFLICT";
}
```

## Safe vs Unsafe

Safe:

```text
Expose meaningful domain exceptions, not JDBC or vendor exceptions.
Translate exceptions at service/API boundaries.
Use checked exceptions only when the caller can realistically recover.
Log once at the boundary with correlation information.
```

Unsafe:

```text
Do not leak low-level exceptions through service APIs.
Do not catch Exception and continue silently.
Do not log the same exception repeatedly at every layer.
```

## Java / Spring Backend Use Case

A service can throw a domain exception for insufficient funds and a controller advice can map it to HTTP 409.

Java/Spring angle:

```text
try {
transferService.transfer(command);
} catch (InsufficientFundsException ex) {
Map to a client-safe response; do not leak internal stack details.
return "409 CONFLICT";
}
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not leak low-level exceptions through service APIs.
- Do not catch Exception and continue silently.
- Do not log the same exception repeatedly at every layer.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Checked Vs Unchecked Exceptions changes are deployed.
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

In an interview, I would say: Checked exceptions force callers to handle recoverable conditions; unchecked exceptions are better for programming errors and most service-layer business failures translated at boundaries. For example, a service can throw a domain exception for insufficient funds and a controller advice can map it to HTTP 409. The main production risk is leak low-level exceptions through service APIs.
