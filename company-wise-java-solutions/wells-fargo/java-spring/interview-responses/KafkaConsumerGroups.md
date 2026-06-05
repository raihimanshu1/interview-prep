# Kafka Consumer Groups - Interview Response

## What Is It?

A Kafka consumer group lets multiple consumers share work by assigning partitions so each partition is consumed by at most one consumer in the group at a time.

## In Simple Terms

A Kafka consumer group lets multiple consumers share work by assigning partitions so each partition is consumed by at most one consumer in the group at a time.

## Why It Matters

A payment notification service can scale consumers horizontally as long as the topic has enough partitions.

If we get it wrong:

```text
Do not expect more consumers than partitions to increase throughput.
Do not commit offsets before durable processing.
Do not ignore rebalance behavior.
```

## Example

```text
Production pattern:
poll records -> process safely -> commit offset after durable success.
Keep processing idempotent because duplicates can still happen.
```

Key interview details:

- partitions, rebalance, offsets, concurrency limits, Spring Kafka listener.

## Safe vs Unsafe

Safe:

```text
Partitions define maximum parallelism inside a consumer group.
Consumers in different groups each receive their own copy of messages.
Rebalances move partition ownership when consumers join, leave, or fail.
Offset commits define where processing resumes.
```

Unsafe:

```text
Do not expect more consumers than partitions to increase throughput.
Do not commit offsets before durable processing.
Do not ignore rebalance behavior.
```

## Java / Spring Backend Use Case

A payment notification service can scale consumers horizontally as long as the topic has enough partitions.

Java/Spring angle:

```text
Production pattern:
poll records -> process safely -> commit offset after durable success.
Keep processing idempotent because duplicates can still happen.
```

## Production Concerns

- Explain partition assignment, rebalancing, offset commits, lag, and max poll interval.
- Discuss at-least-once processing and why idempotent consumers are normally required.
- Mention ordering only within a partition and how key choice affects correctness.
- Production answer: commit offsets after durable success and monitor lag/rebalance frequency.

## Common Mistakes

- Do not expect more consumers than partitions to increase throughput.
- Do not commit offsets before durable processing.
- Do not ignore rebalance behavior.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Kafka Consumer Groups changes are deployed.
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

In an interview, I would say: A Kafka consumer group lets multiple consumers share work by assigning partitions so each partition is consumed by at most one consumer in the group at a time. For example, a payment notification service can scale consumers horizontally as long as the topic has enough partitions. The main production risk is expect more consumers than partitions to increase throughput.
