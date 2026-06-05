# Scalable Payment System - High Level Design (HLD) Deep Dive

## Problem Statement

Design a scalable payment system for creating, processing, tracking, and reconciling payments.

Example:

```text
Customer initiates payment
System validates request
Ledger records money movement
Payment processor sends instruction
Customer gets final status
```

## Requirements

```text
Highly reliable
Idempotent
Auditable
Scalable
Secure
Observable
Eventually reconcilable
Backward compatible APIs/events
```

## Core APIs

```http
POST /api/v1/payments
GET  /api/v1/payments/{paymentId}
GET  /api/v1/payments/{paymentId}/status
POST /api/v1/payments/{paymentId}/cancel
```

Create request:

```json
{
  "fromAccountId": "A1",
  "toAccountId": "A2",
  "amount": "100.00",
  "currency": "USD",
  "idempotencyKey": "client-req-123"
}
```

## High Level Architecture

```text
Client
  |
API Gateway
  |
Payment API Service
  |
Payment Orchestrator
  |-------- Fraud/Risk Service
  |-------- Limits Service
  |-------- Ledger Service
  |-------- Payment Partner Gateway
  |
Payment DB
Outbox Table
  |
Kafka
  |
Notification / Reconciliation / Audit Consumers
```

## Main Components

### Payment API Service

```text
Validates request
Checks idempotency key
Creates payment record
Returns paymentId/status
```

### Payment Orchestrator

```text
Runs payment workflow
Calls fraud/risk
Checks limits
Posts ledger entries
Sends instruction to partner
Moves payment state forward
```

### Ledger Service

```text
Source of truth for money movement
Uses double-entry accounting
Never silently deletes entries
```

### Partner Gateway

```text
Calls external payment network
Handles timeout/retry
Maps partner statuses to internal statuses
```

### Reconciliation Service

```text
Compares internal ledger/payment state with partner reports
Finds missing, delayed, or mismatched transactions
```

## Database Tables

```text
payment
-------
payment_id
from_account_id
to_account_id
amount
currency
status
idempotency_key
created_at
updated_at

ledger_entry
------------
entry_id
payment_id
account_id
direction
amount
currency
created_at

outbox_event
------------
event_id
aggregate_id
event_type
payload
status
created_at
published_at
```

## Payment States

```text
CREATED
VALIDATED
RISK_APPROVED
LEDGER_POSTED
SENT_TO_PARTNER
SETTLED
FAILED
CANCELLED
RECONCILIATION_REQUIRED
```

## Idempotency

Client sends:

```http
Idempotency-Key: client-req-123
```

Server stores:

```text
clientId + idempotencyKey -> paymentId + requestHash + response
```

Duplicate request:

```text
Same key + same payload -> return existing response
Same key + different payload -> reject
```

## Failure Handling

### Partner Timeout

```text
Do not blindly create another payment.
Mark status as PENDING_CONFIRMATION.
Use reconciliation or status inquiry.
```

### Kafka Publish Failure

```text
Use transactional outbox.
Payment DB commit and outbox event commit together.
Publisher retries later.
```

### Fraud Service Down

```text
Fail closed for high-risk payment.
Queue/manual review if business allows.
```

## Scaling

```text
Stateless API services scale horizontally
Partition Kafka by paymentId or accountId
Use DB indexes on paymentId, accountId, idempotency key
Use read replicas for status queries
Use async workers for partner/reconciliation workflows
```

## Observability

Track:

```text
payment creation rate
success/failure rate
partner latency
idempotency hit rate
outbox lag
reconciliation mismatches
fraud decline rate
ledger posting failures
```

## Forward / Backward Compatibility

API/event evolution:

```text
Add optional fields first
Keep old payment statuses stable
Do not change event meaning silently
Version partner mappings carefully
Use contract tests for clients and consumers
```

Semantic versioning:

```text
MAJOR -> breaking payment API/event contract
MINOR -> optional field or new status metadata
PATCH -> internal bug fix
```

## Related Patterns

- Idempotency key
- Transactional outbox
- Saga
- Circuit breaker
- Bulkhead
- Adapter for partner APIs
- Repository

## Follow-Up Questions

### Is payment processing synchronous?

```text
Creation can be synchronous, but settlement is often asynchronous.
```

### How do you prevent duplicate payments?

```text
Idempotency key, unique constraint, operation state, and reconciliation.
```

### What is source of truth?

```text
Ledger is source of truth for money movement.
Payment table tracks workflow state.
```

## Senior-Level Interview Answer

I would design the payment system around idempotency, ledger correctness, and reconciliation. The API service validates requests and stores an idempotency record. The orchestrator moves payment through states like CREATED, RISK_APPROVED, LEDGER_POSTED, SENT_TO_PARTNER, and SETTLED. Ledger writes must be transactional and auditable. Events should be published through an outbox, and partner timeouts should not trigger duplicate payments. For scale, I would keep API nodes stateless, use Kafka for async workflows, index payment and idempotency keys, and monitor outbox lag, partner failures, and reconciliation mismatches.
