# Contract Testing Pact - Interview Response

## What Is It?

Contract testing verifies that service providers and consumers agree on API or event contracts, catching integration breakage before deployment.

## In Simple Terms

Contract testing verifies that service providers and consumers agree on API or event contracts, catching integration breakage before deployment.

## Why It Matters

A mobile banking consumer can define the expected transfer-status response, and the payment service verifies it still satisfies that contract.

If we get it wrong:

```text
Do not treat contract tests as full business-flow tests.
Do not ignore versioning and optional fields.
Do not let stale contracts live forever.
```

## Example

```text
Consumer defines expected request/response shape.
Provider test replays the contract against provider code.
CI fails if provider breaks a committed consumer expectation.
```

Key interview details:

- Consumer contract, Pact broker, provider verification, and CI gate before deployment.

## Safe vs Unsafe

Safe:

```text
Consumer-driven contracts focus on what clients actually use.
They complement, not replace, integration tests.
Contracts are valuable for REST APIs, messaging events, and schema evolution.
They reduce the need for every team to run full end-to-end environments for every change.
```

Unsafe:

```text
Do not treat contract tests as full business-flow tests.
Do not ignore versioning and optional fields.
Do not let stale contracts live forever.
```

## Java / Spring Backend Use Case

A mobile banking consumer can define the expected transfer-status response, and the payment service verifies it still satisfies that contract.

Java/Spring angle:

```text
Consumer defines expected request/response shape.
Provider test replays the contract against provider code.
CI fails if provider breaks a committed consumer expectation.
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not treat contract tests as full business-flow tests.
- Do not ignore versioning and optional fields.
- Do not let stale contracts live forever.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Contract Testing Pact changes are deployed.
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

In an interview, I would say: Contract testing verifies that service providers and consumers agree on API or event contracts, catching integration breakage before deployment. For example, a mobile banking consumer can define the expected transfer-status response, and the payment service verifies it still satisfies that contract. The main production risk is treat contract tests as full business-flow tests.
