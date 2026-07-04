# Payment Service - Low Level Design (LLD) Deep Dive

## Problem Statement

Design a Spring Boot payment service that creates a payment safely, prevents duplicates, writes ledger entries, and publishes an event.

## Requirements

```text
Idempotent POST
Thread-safe
Transactional
Auditable
Retry-safe
Event publishing
Clear error responses
Testable
```

## High Level Flow

```text
POST /payments
   |
Validate request
   |
Check idempotency key
   |
Create payment
   |
Debit/Credit ledger
   |
Insert outbox event
   |
Commit transaction
   |
Return payment response
```

## DTOs

```java
record CreatePaymentRequest(
    String fromAccountId,
    String toAccountId,
    BigDecimal amount,
    String currency,
    String idempotencyKey
) {}

record PaymentResponse(
    String paymentId,
    String status
) {}
```

## Entities

```java
class Payment {
    String id;
    String fromAccountId;
    String toAccountId;
    BigDecimal amount;
    String currency;
    PaymentStatus status;
    String idempotencyKey;
}

enum PaymentStatus {
    CREATED,
    LEDGER_POSTED,
    EVENT_QUEUED,
    FAILED
}
```

Ledger entry:

```java
class LedgerEntry {
    String id;
    String paymentId;
    String accountId;
    Direction direction; // DEBIT or CREDIT
    BigDecimal amount;
}
```

Idempotency record:

```java
class IdempotencyRecord {
    String clientId;
    String idempotencyKey;
    String requestHash;
    String paymentId;
    String responseJson;
}
```

## Repositories

```java
interface PaymentRepository extends JpaRepository<Payment, String> {}

interface LedgerRepository extends JpaRepository<LedgerEntry, String> {}

interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, String> {
    Optional<IdempotencyRecord> findByClientIdAndIdempotencyKey(String clientId, String key);
}

interface OutboxRepository extends JpaRepository<OutboxEvent, String> {}
```

## Service Logic

```java
@Service
class PaymentService {
    @Transactional
    public PaymentResponse createPayment(String clientId, CreatePaymentRequest request) {
        validate(request);

        Optional<IdempotencyRecord> existing =
            idempotencyRepository.findByClientIdAndIdempotencyKey(clientId, request.idempotencyKey());

        if (existing.isPresent()) {
            return replayOrReject(existing.get(), request);
        }

        Payment payment = paymentRepository.save(newPayment(request));

        ledgerRepository.save(debit(payment));
        ledgerRepository.save(credit(payment));

        outboxRepository.save(paymentCreatedEvent(payment));

        saveIdempotencyRecord(clientId, request, payment);

        return new PaymentResponse(payment.id, payment.status.name());
    }
}
```

## Idempotency Rules

```text
Same client + same idempotency key + same request hash -> return old response
Same client + same idempotency key + different request hash -> reject
No idempotency key -> reject for payment creation
```

Database constraint:

```sql
CREATE UNIQUE INDEX ux_idempotency
ON idempotency_record(client_id, idempotency_key);
```

## Transaction Boundary

Everything below should commit together:

```text
payment row
ledger entries
idempotency record
outbox event
```

Do not call external partner API inside this DB transaction if it can block for a long time.

## Locking / Concurrency

Race:

```text
Two same idempotency-key requests arrive together.
Both do lookup.
Both see nothing.
Both try to create payment.
```

Fix:

```text
unique constraint on idempotency key
transaction
catch duplicate key and replay existing response
```

For account balance correctness:

```text
use ledger model
use account row locking if maintaining materialized balance
use optimistic/pessimistic locking depending on contention
```

## Error Responses

```http
400 Bad Request          invalid payload
401 Unauthorized         missing/invalid token
403 Forbidden            missing scope
409 Conflict             same idempotency key with different payload
422 Unprocessable Entity insufficient funds/business rule failed
500 Internal Server Error unexpected failure
```

## Outbox Event

```json
{
  "eventId": "E1",
  "eventType": "PaymentCreated",
  "paymentId": "P1",
  "status": "LEDGER_POSTED"
}
```

Publisher later sends this to Kafka.

## Forward / Backward Compatibility

Payment APIs/events must evolve carefully:

```text
Add optional fields first
Keep paymentId and status stable
Do not rename amount/currency fields
Do not change status meaning silently
Version breaking API/event changes
```

Semantic versioning:

```text
MAJOR -> breaking payment API/event
MINOR -> optional field or new compatible event
PATCH -> validation or retry bug fix
```

## Test Cases

```text
valid payment creates payment + ledger + outbox
duplicate idempotency key returns same response
same key with different payload returns 409
ledger failure rolls back payment
outbox row exists after successful payment
concurrent duplicate requests create only one payment
invalid amount returns 400
missing scope returns 403
```

## Related Patterns

- Idempotency key
- Repository
- Transactional outbox
- Unit of Work
- Saga
- Adapter for partner gateway

## Senior-Level Interview Answer

I would design the payment service so the payment row, ledger entries, idempotency record, and outbox event are saved in one database transaction. The idempotency key prevents duplicate POSTs, and a unique constraint protects against concurrent duplicate requests. I would publish events through an outbox instead of calling Kafka directly inside the business transaction. For account correctness, I would rely on ledger entries and use locking only where a materialized balance must be protected. I would also define clear error responses and test duplicate requests, rollback, concurrent idempotency races, and event publishing.
