# Transactional Outbox Pattern - Interview Response

## What Is It?

The transactional outbox pattern stores a domain change and an event record in the same database transaction.

Then a separate publisher sends the event to Kafka or another broker.

In simple terms:

```text
Do database update
Insert outbox event
Commit once
Publish later
```

## Why We Need It

Bad flow:

```text
Update payment table
Publish Kafka event
```

Failure problem:

```text
DB commit succeeds
Kafka publish fails
Other services never learn payment happened
```

Opposite failure:

```text
Kafka publish succeeds
DB commit fails
Other services see event for payment that does not exist
```

## Correct Flow

```text
Begin DB transaction
   |
Insert / update payment
   |
Insert outbox row
   |
Commit DB transaction
   |
Publisher reads outbox
   |
Publish to Kafka
   |
Mark outbox row as published
```

## Outbox Table

```sql
CREATE TABLE outbox_event (
    id              UUID PRIMARY KEY,
    aggregate_type  VARCHAR(100) NOT NULL,
    aggregate_id    VARCHAR(100) NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    payload         JSONB NOT NULL,
    status          VARCHAR(30) NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    published_at    TIMESTAMP NULL,
    retry_count     INT NOT NULL DEFAULT 0
);
```

Example row:

```text
aggregate_type = PAYMENT
aggregate_id   = P123
event_type     = PaymentPosted
status         = NEW
```

## Spring Service Sketch

```java
@Transactional
public Payment createPayment(CreatePaymentCommand command) {
    Payment payment = paymentRepository.save(Payment.from(command));

    outboxRepository.save(OutboxEvent.of(
        "PAYMENT",
        payment.id(),
        "PaymentCreated",
        json(payment)
    ));

    return payment;
}
```

Important:

```text
Payment row and outbox row commit together.
Kafka publish does not happen inside the request transaction.
```

## Publisher Options

### Polling Publisher

```text
Scheduler reads NEW rows
Publishes to Kafka
Marks rows PUBLISHED
```

Simple and common.

### CDC Publisher

```text
Debezium reads database change log
Streams outbox rows to Kafka
```

Better at scale, but more infrastructure.

## Duplicate Publish Problem

Publisher may crash:

```text
Publish event to Kafka
Crash before marking row PUBLISHED
Restart
Publish same event again
```

Therefore consumers must be idempotent.

Consumer guardrail:

```text
eventId unique constraint
processed_event table
deduplicate by event id
```

## Monitoring

Track:

```text
outbox lag
NEW row count
retry count
publish failures
oldest unpublished event age
DLQ count
consumer duplicate count
```

## Forward / Backward Compatibility

Outbox events are contracts.

Safe:

```text
Add optional fields
Keep event type meaning stable
Keep aggregate id stable
Use schema compatibility checks
```

Breaking:

```text
Rename fields
Remove required fields
Change event meaning
Change partition key unexpectedly
```

Semantic versioning:

```text
MAJOR -> breaking event schema
MINOR -> compatible optional event field
PATCH -> publisher retry bug fix
```

## Related Patterns

- Idempotent consumer
- Inbox pattern
- Saga
- CDC
- Event-driven architecture

## Follow-Up Interview Questions

### Why not publish Kafka inside the transaction?

```text
Database transaction and Kafka transaction are separate systems.
Keeping both perfectly atomic is hard. Outbox makes DB commit the source of truth.
```

### Can outbox publish duplicates?

```text
Yes. That is why consumers must be idempotent.
```

### What if outbox grows?

```text
Add cleanup, archiving, partitioning, retry limits, and lag alerts.
```

## Interview Answer

In an interview, I would say: I use the transactional outbox pattern when a service must update its database and publish an event reliably. I write the business row and the outbox event in the same database transaction, then publish asynchronously through polling or CDC. This avoids the classic failure where the database commit succeeds but Kafka publish fails. Since duplicate publishing can still happen, consumers must be idempotent. In production, I would monitor outbox lag, retry counts, publish failures, and schema compatibility.
