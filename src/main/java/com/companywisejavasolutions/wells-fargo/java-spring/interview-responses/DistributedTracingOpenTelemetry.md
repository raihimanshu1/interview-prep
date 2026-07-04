# Distributed Tracing Open Telemetry - Interview Response

## What Is It?

Distributed tracing follows one request across services using trace IDs and spans, helping engineers identify latency, errors, and dependency paths in microservices.

## In Simple Terms

Distributed tracing follows one request across services using trace IDs and spans, helping engineers identify latency, errors, and dependency paths in microservices.

## Why It Matters

A payment request trace can show controller time, DB write time, fraud-service latency, Kafka publish time, and downstream errors.

If we get it wrong:

```text
Do not add sensitive account data as span attributes.
Do not trace only the first service.
Do not rely on traces without metrics and logs.
```

## Example

```text
Production practice:
propagate traceparent header, create spans around dependencies, log traceId, export via OTLP to tracing backend.
```

## Safe vs Unsafe

Safe:

```text
Trace context must be propagated through HTTP, messaging, and async boundaries.
Spans should have useful names and safe attributes.
Sampling balances cost with diagnostic value.
Tracing works best with metrics and structured logs.
```

Unsafe:

```text
Do not add sensitive account data as span attributes.
Do not trace only the first service.
Do not rely on traces without metrics and logs.
```

## Java / Spring Backend Use Case

A payment request trace can show controller time, DB write time, fraud-service latency, Kafka publish time, and downstream errors.

Java/Spring angle:

```text
Production practice:
propagate traceparent header, create spans around dependencies, log traceId, export via OTLP to tracing backend.
```

## Production Concerns

- Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
- Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
- Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
- Production answer: optimize for correctness and operability first, then throughput.

## Common Mistakes

- Do not add sensitive account data as span attributes.
- Do not trace only the first service.
- Do not rely on traces without metrics and logs.

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

In an interview, I would say: Distributed tracing follows one request across services using trace IDs and spans, helping engineers identify latency, errors, and dependency paths in microservices. For example, a payment request trace can show controller time, DB write time, fraud-service latency, Kafka publish time, and downstream errors. The main production risk is add sensitive account data as span attributes.
