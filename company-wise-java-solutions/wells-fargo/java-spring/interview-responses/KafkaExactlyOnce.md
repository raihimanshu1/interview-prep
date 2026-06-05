# Kafka Exactly Once - Interview Response

## What Is It?

Kafka exactly-once semantics means Kafka can avoid duplicate records in a consume-process-produce pipeline when producers, transactions, and offset commits are configured correctly.

Important:

```text
Kafka exactly-once != exactly-once for every external side effect.
```

If the consumer writes to a database, sends email, or calls a payment partner, Kafka alone cannot make that side effect exactly once.

## In Simple Terms

Kafka can make this atomic:

```text
Read from topic A
Process message
Write to topic B
Commit consumed offset
```

But this is not automatically atomic:

```text
Read Kafka event
Update database ledger
Call external payment API
Commit Kafka offset
```

## Why It Matters

In banking, duplicate event processing can create serious issues:

```text
Duplicate ledger entry
Duplicate notification
Duplicate fraud review
Duplicate downstream payment instruction
```

So we must separate:

```text
Kafka delivery guarantee
Business-level duplicate prevention
```

## Kafka Transaction Flow

```text
Consumer reads input record
   |
Producer begins transaction
   |
Process record
   |
Produce output record
   |
Send consumed offset to transaction
   |
Commit transaction
```

If commit fails:

```text
Output record is not visible
Offset is not committed
Message can be retried
```

## Producer Configuration

```properties
enable.idempotence=true
acks=all
transactional.id=payment-processor-1
```

Meaning:

```text
enable.idempotence=true -> producer retries do not create duplicate records
transactional.id -> enables transactional producer behavior
acks=all -> wait for full acknowledgement from replicas
```

## Spring Kafka Angle

In Spring Kafka, you usually configure:

```text
KafkaTransactionManager
transactional KafkaTemplate
listener container transaction support
```

High-level shape:

```java
@KafkaListener(topics = "payment-requested")
public void handle(PaymentRequested event) {
    // process event
    // write output event through transactional KafkaTemplate
}
```

For database + Kafka:

```text
Prefer transactional outbox.
Do not pretend Kafka transaction also wraps your database commit.
```

## External Database Problem

Bad assumption:

```text
Kafka transaction commits
therefore database update is exactly once
```

Reality:

```text
Database commit may succeed
Kafka commit may fail
Consumer may retry
Database update may happen again
```

Fixes:

```text
Idempotency key
Unique constraint
Inbox table
Transactional outbox
Deduplication table
Business operation status
```

## Forward Compatibility

For Kafka events:

```text
Add optional fields
Use defaults
Keep old field meanings
Make consumers ignore unknown fields
Avoid changing key semantics
```

## Backward Compatibility

Old consumers should still process new events.

Breaking event changes:

```text
Rename field
Remove field
Change type
Change enum meaning
Change partition key behavior
```

## Semantic Versioning

```text
MAJOR -> breaking event schema or topic contract
MINOR -> optional event field or compatible event type
PATCH -> producer bug fix or config tuning
```

## Big-Company Evolution Mindset

Large Kafka platforms usually use:

```text
Schema registry
Compatibility checks
Consumer lag monitoring
Dead-letter queues
Replay strategy
Idempotent consumers
Gradual producer rollout
```

## Related Patterns

- Transactional outbox
- Idempotent consumer
- Inbox/deduplication table
- Saga
- Event-carried state transfer

## Follow-Up Interview Questions

### Does Kafka exactly-once solve duplicate payments?

```text
No. It helps inside Kafka. Payment correctness still needs idempotency,
unique business keys, database transactions, and reconciliation.
```

### What happens if DB commit succeeds but Kafka publish fails?

```text
Use the outbox pattern so DB update and event record are committed together,
then publish asynchronously.
```

### What should consumers assume?

```text
Assume duplicates can happen. Make consumers idempotent.
```

## Interview Answer

In an interview, I would say: Kafka exactly-once is useful for consume-process-produce pipelines where Kafka can atomically write output records and commit consumed offsets in one transaction. But it does not magically make database writes, emails, payment gateway calls, or ledger updates exactly once. For financial systems, I would combine Kafka transactions with idempotent consumers, unique business keys, and often the transactional outbox pattern. I would also use schema compatibility rules, replay testing, DLQs, and consumer lag monitoring so event evolution and retries remain safe in production.
