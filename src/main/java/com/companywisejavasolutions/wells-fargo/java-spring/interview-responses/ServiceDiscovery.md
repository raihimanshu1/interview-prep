# Service Discovery - Low Level / High Level Design Deep Dive

# Problem Statement

Explain service discovery in microservices.

# Requirements

```text
Correct
Scalable
Reliable
Observable
Testable
Backward compatible
Production safe
```

# Why Do We Need This?

Service discovery lets clients or infrastructure find healthy service instances dynamically instead of hardcoding hostnames and ports.

# High Level Flow

```text
Client / caller
   |
API or entry component
   |
Spring Boot service layer
   |
Domain logic
   |
Database / cache / broker / downstream service
   |
Response, retry, compensation, or rejection
```

# Key Interview Question

```text
What is the core contract?
Where is state stored?
What can fail?
How do we handle concurrency?
How do we evolve the design safely?
```

# Core Design Points

- Client-side discovery lets the app choose an instance.
- Server-side discovery uses a load balancer/proxy to route traffic.
- Health checks determine whether instances should receive traffic.
- Discovery does not replace timeouts, retries, or circuit breakers.

# Data Structures / Classes

```text
In Kubernetes:
service DNS name fraud-service.default.svc.cluster.local routes to ready pods.
The app still sets connect/read timeouts and retry budgets.
```

# Approaches

## Approach 1: Simple Implementation

Idea:

```text
Keep the design direct and local. Use this when scale, distribution, and failure complexity are low.
```

Pros:

```text
Easy to build
Easy to debug
Good for first version
```

Cons:

```text
Limited scalability
Weak failure isolation
May not work across multiple service instances
```

## Approach 2: Production Implementation

Idea:

```text
Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.
```

Pros:

```text
Scales better
Handles failures explicitly
Easier to operate and monitor
```

Cons:

```text
More moving parts
Requires integration tests
Needs careful rollout and ownership
```

# Concurrency Problem

```text
Multiple requests can touch the same business entity, cache key, event stream, or database row at the same time.
```

Fixes:

- Use transactions and correct isolation.
- Use idempotency keys for retries.
- Use optimistic or pessimistic locking where needed.
- Avoid shared mutable state in singleton Spring beans.

# Distributed System Problem

```text
Multiple service instances do not share memory.
Downstream services can fail.
Messages can be duplicated.
Deployments can happen gradually.
```

Fixes:

- Store shared state in database/cache/broker.
- Use timeout, retry, circuit breaker, and bulkhead policies.
- Use outbox/inbox or reconciliation for cross-resource consistency.
- Keep contracts backward compatible.

# Production Architecture

```text
Client
   |
Gateway / Load Balancer
   |
Spring Boot API
   |
Domain Service
   |
Repository / Redis / Kafka
   |
Database / Downstream Services
   |
Logs + Metrics + Traces + Alerts
```

# Failure Handling

- Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
- Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
- Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
- Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.

# Topic-Specific Details

- Eureka/Consul/Kubernetes DNS, client vs server-side discovery, health checks, stale instances.

# Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Service Discovery changes are deployed.
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

- Strategy
- Factory
- Adapter
- Repository
- Outbox
- Saga
- Circuit breaker

# Follow-Up Questions

- What breaks under high concurrency or partial failure?
- How would you verify this with tests, metrics, logs, or traces?
- What trade-off would make you choose a different design?

# Senior-Level Interview Answer

In an interview, I would say: Service discovery lets clients or infrastructure find healthy service instances dynamically instead of hardcoding hostnames and ports. For Service Discovery, I would first clarify requirements, scale, consistency, latency, and failure expectations. Then I would design the API/service boundary, state model, concurrency control, failure handling, and observability. The production answer must include idempotency or locking where correctness matters, clear rollback or compensation behavior, and compatibility planning so gradual deployments do not break existing clients or services.
