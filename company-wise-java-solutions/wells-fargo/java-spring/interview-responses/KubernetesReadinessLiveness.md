# Kubernetes Readiness Liveness - Interview Response

## What Is It?

Readiness says whether a pod should receive traffic; liveness says whether the container should be restarted because it is stuck or unhealthy.

## In Simple Terms

Readiness says whether a pod should receive traffic; liveness says whether the container should be restarted because it is stuck or unhealthy.

## Why It Matters

A payment service should fail readiness when it cannot connect to required dependencies, but liveness should avoid restarting for every temporary downstream outage.

If we get it wrong:

```text
Do not make liveness depend on every downstream service.
Do not send traffic before warm-up is complete.
Do not hide real dependency failures from readiness.
```

## Example

```text
Readiness: app initialized and can serve traffic.
Liveness: JVM/process is not deadlocked or permanently broken.
Keep dependency checks stricter in readiness than liveness.
```

Key interview details:

- Actuator probes, startup probe, DB dependency risk, bad liveness checks.

## Safe vs Unsafe

Safe:

```text
Readiness protects users by removing unready pods from load balancing.
Liveness protects the platform by restarting stuck containers.
Startup probes help slow-starting apps avoid premature restarts.
Spring Boot Actuator health groups can expose different health views.
```

Unsafe:

```text
Do not make liveness depend on every downstream service.
Do not send traffic before warm-up is complete.
Do not hide real dependency failures from readiness.
```

## Java / Spring Backend Use Case

A payment service should fail readiness when it cannot connect to required dependencies, but liveness should avoid restarting for every temporary downstream outage.

Java/Spring angle:

```text
Readiness: app initialized and can serve traffic.
Liveness: JVM/process is not deadlocked or permanently broken.
Keep dependency checks stricter in readiness than liveness.
```

## Production Concerns

- Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
- Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
- Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
- Production answer: optimize for correctness and operability first, then throughput.

## Common Mistakes

- Do not make liveness depend on every downstream service.
- Do not send traffic before warm-up is complete.
- Do not hide real dependency failures from readiness.

## Extra Details

Forward compatibility:

```text
Compatibility matters when this topic changes behavior exposed through APIs, shared libraries, event payloads, config properties, or deployment defaults. New behavior should be rolled out so older callers and services keep working safely.
```

Backward compatibility:

```text
Do not break existing callers, tests, serialized data, configuration, or operational runbooks silently. Keep old behavior available until users or services migrate.
```

Semantic versioning:

```text
MAJOR -> breaking public behavior or contract
MINOR -> compatible feature or API addition
PATCH -> bug fix or internal tuning
```

Big-company evolution mindset:

```text
Large engineering teams roll out changes gradually, keep compatibility during migration, measure usage, document deprecation, and avoid forcing all services to upgrade at once.
```

Related patterns:

- Dependency Injection
- Service layer
- Repository
- DTO/Adapter

## Follow-Up Interview Questions

- What breaks under high concurrency or partial failure?
- How would you verify this with tests, metrics, logs, or traces?
- What trade-off would make you choose a different design?

## Interview Answer

In an interview, I would say: Readiness says whether a pod should receive traffic; liveness says whether the container should be restarted because it is stuck or unhealthy. For example, a payment service should fail readiness when it cannot connect to required dependencies, but liveness should avoid restarting for every temporary downstream outage. The main production risk is make liveness depend on every downstream service.
