# Microservice Communication - Interview Response

## What Is It?

Microservices communicate through synchronous APIs such as REST/gRPC and asynchronous messaging/events, chosen by consistency, latency, coupling, and failure tolerance.

## In Simple Terms

Microservices communicate through synchronous APIs such as REST/gRPC and asynchronous messaging/events, chosen by consistency, latency, coupling, and failure tolerance.

## Why It Matters

Balance inquiry may use REST; payment-posted notifications are better as events.

If we get it wrong:

```text
Do not make every interaction synchronous.
Do not publish events without clear ownership and schema versioning.
Do not ignore timeouts and idempotency.
```

## Example

```text
REST: request/response, direct dependency, user-facing latency.
Event: publish state change, consumers process independently, eventual consistency.
Messaging command: async work queue with explicit consumer ownership.
```

Key interview details:

- REST vs messaging vs events, sync/async tradeoffs, WebClient/Kafka, failure handling.

## Safe vs Unsafe

Safe:

```text
Use sync calls for immediate answers the user is waiting for.
Use events for state changes consumed by many services.
Design timeouts, retries, and idempotency for every remote call.
Keep contracts versioned and tested.
```

Unsafe:

```text
Do not make every interaction synchronous.
Do not publish events without clear ownership and schema versioning.
Do not ignore timeouts and idempotency.
```

## Java / Spring Backend Use Case

Balance inquiry may use REST; payment-posted notifications are better as events.

Java/Spring angle:

```text
REST: request/response, direct dependency, user-facing latency.
Event: publish state change, consumers process independently, eventual consistency.
Messaging command: async work queue with explicit consumer ownership.
```

## Production Concerns

- Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
- Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
- Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
- Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.

## Common Mistakes

- Do not make every interaction synchronous.
- Do not publish events without clear ownership and schema versioning.
- Do not ignore timeouts and idempotency.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Microservice Communication changes are deployed.
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

In an interview, I would say: Microservices communicate through synchronous APIs such as REST/gRPC and asynchronous messaging/events, chosen by consistency, latency, coupling, and failure tolerance. For example, Balance inquiry may use REST; payment-posted notifications are better as events. The main production risk is make every interaction synchronous.
