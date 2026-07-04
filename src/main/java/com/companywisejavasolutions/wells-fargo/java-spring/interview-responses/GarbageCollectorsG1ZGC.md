# Garbage Collectors G1 ZGC - Interview Response

## What Is It?

G1 is a balanced default collector for many services, while ZGC targets very low pause times for large heaps and latency-sensitive workloads, usually with different memory/throughput trade-offs.

## In Simple Terms

G1 is a balanced default collector for many services, while ZGC targets very low pause times for large heaps and latency-sensitive workloads, usually with different memory/throughput trade-offs.

## Why It Matters

A normal Spring Boot service may run well on G1; a high-throughput low-latency trading or risk service with a large heap may evaluate ZGC.

If we get it wrong:

```text
Do not choose a collector by trend alone.
Do not tune GC before understanding allocation rate and pause SLO.
Do not ignore CPU and memory overhead.
```

## Example

```text
Example tuning conversation, not a universal prescription:
-XX:+UseG1GC -XX:MaxGCPauseMillis=200
or evaluate ZGC for large low-latency heaps after measuring allocation behavior.
```

Key interview details:

- compare pause, throughput, heap size, and banking service choice.

## Safe vs Unsafe

Safe:

```text
G1 divides heap into regions and tries to meet pause-time goals.
ZGC performs most work concurrently to keep pauses very short.
Collector choice depends on latency SLO, heap size, allocation rate, CPU budget, and JDK version.
Senior engineers should verify with GC logs and production-like load tests.
```

Unsafe:

```text
Do not choose a collector by trend alone.
Do not tune GC before understanding allocation rate and pause SLO.
Do not ignore CPU and memory overhead.
```

## Java / Spring Backend Use Case

A normal Spring Boot service may run well on G1; a high-throughput low-latency trading or risk service with a large heap may evaluate ZGC.

Java/Spring angle:

```text
Example tuning conversation, not a universal prescription:
-XX:+UseG1GC -XX:MaxGCPauseMillis=200
or evaluate ZGC for large low-latency heaps after measuring allocation behavior.
```

## Production Concerns

- Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
- Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
- Explain tuning trade-offs and why blindly changing flags or heap size is risky.
- Production answer: optimize based on workload evidence and SLO impact.

## Common Mistakes

- Do not choose a collector by trend alone.
- Do not tune GC before understanding allocation rate and pause SLO.
- Do not ignore CPU and memory overhead.

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

- Immutable object pattern
- Thread confinement
- Producer-consumer
- Bulkhead

## Follow-Up Interview Questions

- What breaks under high concurrency or partial failure?
- How would you verify this with tests, metrics, logs, or traces?
- What trade-off would make you choose a different design?

## Interview Answer

In an interview, I would say: G1 is a balanced default collector for many services, while ZGC targets very low pause times for large heaps and latency-sensitive workloads, usually with different memory/throughput trade-offs. For example, a normal Spring Boot service may run well on G1; a high-throughput low-latency trading or risk service with a large heap may evaluate ZGC. The main production risk is choose a collector by trend alone.
