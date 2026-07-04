# Blue Green Canary Deployments - Interview Response

## What Is It?

Blue-green switches traffic between two full environments, while canary gradually sends a small percentage of traffic to a new version to reduce blast radius.

## In Simple Terms

Blue-green switches traffic between two full environments, while canary gradually sends a small percentage of traffic to a new version to reduce blast radius.

## Why It Matters

A payment API can canary a new fraud-integration version to 5 percent of traffic while monitoring error rate and latency.

If we get it wrong:

```text
Do not deploy incompatible schema changes first.
Do not canary without objective success metrics.
Do not assume rollback undoes data changes.
```

## Example

```text
Canary checklist:
deploy v2 -> route small traffic -> compare SLOs -> increase gradually -> rollback on error/latency regression.
```

## Safe vs Unsafe

Safe:

```text
Blue-green gives fast rollback but requires duplicate capacity.
Canary gives gradual risk exposure but needs good metrics and traffic control.
Both require backward-compatible database and API changes.
Rollback plans must consider messages and data migrations, not only code.
```

Unsafe:

```text
Do not deploy incompatible schema changes first.
Do not canary without objective success metrics.
Do not assume rollback undoes data changes.
```

## Java / Spring Backend Use Case

A payment API can canary a new fraud-integration version to 5 percent of traffic while monitoring error rate and latency.

Java/Spring angle:

```text
Canary checklist:
deploy v2 -> route small traffic -> compare SLOs -> increase gradually -> rollback on error/latency regression.
```

## Production Concerns

- Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
- Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
- Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
- Production answer: optimize for correctness and operability first, then throughput.

## Common Mistakes

- Do not deploy incompatible schema changes first.
- Do not canary without objective success metrics.
- Do not assume rollback undoes data changes.

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

- Strategy
- Adapter
- Factory
- Composition over inheritance

## Follow-Up Interview Questions

- What breaks under high concurrency or partial failure?
- How would you verify this with tests, metrics, logs, or traces?
- What trade-off would make you choose a different design?

## Interview Answer

In an interview, I would say: Blue-green switches traffic between two full environments, while canary gradually sends a small percentage of traffic to a new version to reduce blast radius. For example, a payment API can canary a new fraud-integration version to 5 percent of traffic while monitoring error rate and latency. The main production risk is deploy incompatible schema changes first.
