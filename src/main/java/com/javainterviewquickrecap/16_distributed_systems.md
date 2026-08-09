# Module 8-9 — Distributed Systems: Microservices, Kafka, Redis — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---


This is a **System Design recap**, not a deep dive. For a **7+ years backend interview**, interviewers usually don't expect formal definitions—they expect you to know **what problem each pattern solves** and **when to use it**.

---

# Distributed Systems & Microservices (30-Minute Interview Recap) ⭐⭐⭐⭐⭐

---

# Complete Picture

```text
                   Client
                      │
                      ▼
                 API Gateway
                      │
      ┌───────────────┼────────────────┐
      ▼               ▼                ▼
 User Service    Order Service    Payment Service
      │               │                │
      └───────────────┼────────────────┘
                      ▼
                 Database/Event Bus

Cross-cutting Concepts

• Service Discovery
• Retry
• Circuit Breaker
• Bulkhead
• Distributed Locks
• Rate Limiting
• Observability
```

---

# 1. CAP Theorem ⭐⭐⭐⭐⭐

Distributed systems cannot guarantee all three simultaneously.

```text
Consistency

      ▲
     / \
    /   \
Availability ----- Partition Tolerance
```

### Consistency

Every client sees the latest data.

### Availability

Every request receives a response.

### Partition Tolerance

System continues working despite network failures.

Example

* Banking → Prefer Consistency
* Social Media → Often Prefer Availability

---

# 2. PACELC ⭐⭐⭐⭐

CAP explains **network failures**.

PACELC adds:

```text
If Partition

↓

Consistency or Availability

Else

↓

Latency or Consistency
```

Even when everything is healthy,

you often trade

Latency vs Consistency.

---

# 3. Consistency Models ⭐⭐⭐⭐

| Model               | Meaning                                 |
| ------------------- | --------------------------------------- |
| Strong              | Latest data immediately visible         |
| Eventual            | Data becomes consistent later           |
| Causal              | Preserves cause-effect ordering         |
| Read-your-own-write | User immediately sees their own updates |

---

# 4. Service Discovery ⭐⭐⭐⭐⭐

Problem

IP addresses keep changing.

Solution

```text
Service A

↓

Discovery Server

↓

Find Service B

↓

Call Service B
```

Examples

* Eureka
* Consul
* Kubernetes Service Discovery

---

# 5. API Gateway ⭐⭐⭐⭐⭐

Single entry point.

```text
Client

↓

API Gateway

↓

Authentication

↓

Rate Limiting

↓

Routing

↓

Microservices
```

Benefits

* Security
* Routing
* Aggregation
* Logging

---

# 6. Circuit Breaker ⭐⭐⭐⭐⭐

Problem

Downstream service is failing.

Without Circuit Breaker

```text
Request

↓

Timeout

↓

Timeout

↓

Timeout
```

With Circuit Breaker

```text
Failures

↓

Circuit Opens

↓

Fail Fast

↓

Recover Later
```

Examples

* Resilience4j
* Sentinel

---

# 7. Retry ⭐⭐⭐⭐

Retry only for **transient failures**.

```text
Call

↓

Fail

↓

Retry

↓

Success
```

Use exponential backoff.

Don't retry everything blindly.

---

# 8. Bulkhead ⭐⭐⭐⭐

Prevent one failing component from consuming all resources.

```text
Thread Pool A → Payment

Thread Pool B → Notification
```

If Notification fails,

Payment still works.

---

# 9. Saga Pattern ⭐⭐⭐⭐⭐

Used for distributed transactions.

```text
Create Order

↓

Reserve Inventory

↓

Take Payment

↓

Ship Order
```

If payment fails

```text
Cancel Inventory

↓

Cancel Order
```

Uses **compensating transactions** instead of database rollback.

---

# 10. Outbox Pattern ⭐⭐⭐⭐⭐

Problem

Database updated but event not published.

Solution

```text
Business Transaction

↓

Update Database

↓

Write Outbox Table

↓

Background Publisher

↓

Kafka/Event Bus
```

Guarantees reliable event publishing.

---

# 11. CQRS ⭐⭐⭐⭐

Separate read and write models.

```text
Write Model

↓

Database

↓

Read Model
```

Useful for high-read systems.

---

# 12. Event Sourcing ⭐⭐⭐⭐

Instead of storing current state,

store every event.

```text
Account Created

↓

Money Deposited

↓

Money Withdrawn

↓

Current State
```

Benefits

* Audit history
* Replay events

---

# 13. Idempotency ⭐⭐⭐⭐⭐

Multiple identical requests should produce the same result.

```text
Retry Payment

↓

Already Processed

↓

Ignore Duplicate
```

Usually implemented with an **Idempotency Key**.

---

# 14. Distributed Locks ⭐⭐⭐⭐

Prevent multiple services from processing the same work.

```text
Instance A

↓

Acquire Lock

↓

Process Job

↓

Release Lock
```

Examples

* Redis
* ZooKeeper

---

# 15. Rate Limiting ⭐⭐⭐⭐⭐

Protect APIs.

```text
100 Requests

↓

Limit = 10/sec

↓

Reject Remaining
```

Common algorithms

* Token Bucket
* Leaky Bucket
* Sliding Window

---

# 16. Observability ⭐⭐⭐⭐⭐

Three pillars.

```text
Application

↓

Logs

↓

Metrics

↓

Tracing
```

### Logging

Understand what happened.

### Metrics

CPU

Memory

Latency

Error Rate

### Tracing

Follow one request across multiple services.

Example

```text
Gateway

↓

Order

↓

Payment

↓

Notification
```

Popular tools

* Prometheus
* Grafana
* OpenTelemetry
* Jaeger
* Zipkin

---

# 17. Resilience Patterns ⭐⭐⭐⭐⭐

```text
Retry

↓

Circuit Breaker

↓

Bulkhead

↓

Timeout

↓

Fallback
```

Goal

Keep the system running even when dependencies fail.

---

# Frequently Asked Interview Questions ⭐⭐⭐⭐⭐

### Architecture

* Explain CAP Theorem.
* CAP vs PACELC.
* Strong vs Eventual Consistency.

### Microservices

* What is Service Discovery?
* Why API Gateway?
* How does Circuit Breaker work?
* Retry vs Circuit Breaker?
* What is Bulkhead?

### Distributed Transactions

* Saga Pattern?
* Outbox Pattern?
* Why not use two-phase commit (2PC) in microservices?

### Scalability

* What is CQRS?
* Event Sourcing?
* Idempotency?
* Distributed Lock?

### Reliability

* How do you implement rate limiting?
* What metrics do you monitor?
* Explain distributed tracing.

---

# 5-Minute Revision

```text
Client
   │
API Gateway
   │
Service Discovery
   │
Microservices
   │
Retry
Circuit Breaker
Bulkhead
Saga
Outbox
CQRS
Idempotency
Distributed Lock
Rate Limiting
Observability
```

---

# ⭐ Highest Priority for 7+ Years

| Topic              | Priority |
| ------------------ | -------- |
| CAP Theorem        | ⭐⭐⭐⭐⭐    |
| Service Discovery  | ⭐⭐⭐⭐⭐    |
| API Gateway        | ⭐⭐⭐⭐⭐    |
| Circuit Breaker    | ⭐⭐⭐⭐⭐    |
| Retry              | ⭐⭐⭐⭐⭐    |
| Saga Pattern       | ⭐⭐⭐⭐⭐    |
| Outbox Pattern     | ⭐⭐⭐⭐⭐    |
| Idempotency        | ⭐⭐⭐⭐⭐    |
| Rate Limiting      | ⭐⭐⭐⭐⭐    |
| Observability      | ⭐⭐⭐⭐⭐    |
| Distributed Locks  | ⭐⭐⭐⭐     |
| PACELC             | ⭐⭐⭐      |
| CQRS               | ⭐⭐⭐⭐     |
| Event Sourcing     | ⭐⭐⭐      |
| Bulkhead           | ⭐⭐⭐⭐     |
| Consistency Models | ⭐⭐⭐⭐     |

### 🎯 Senior interview tip

A common VP/Staff-level follow-up is not **"What is Saga?"** but rather:

> **"You're building an e-commerce checkout service. Which patterns would you use?"**

A strong architecture answer is:

```text
Client
   │
API Gateway
   │
Rate Limiter
   │
Order Service
   │
Saga Orchestrator
   ├── Inventory Service
   ├── Payment Service
   └── Notification Service
          │
      Outbox Pattern
          │
         Kafka
          │
Observability (Logs + Metrics + Traces)
          │
Circuit Breaker + Retry + Timeout
```

This naturally demonstrates knowledge of **Saga, Outbox, Idempotency, Retry, Circuit Breaker, Rate Limiting, Observability, and Resilience** together, which is exactly the kind of integrated thinking interviewers look for from senior backend engineers.


## Q1. Explain Microservices Architecture Patterns — Saga, Outbox, Circuit Breaker.

### 1. Why This Concept Matters
Microservices introduce distributed data problems that don't exist in monoliths. Without these patterns, you'll have inconsistent data across services, cascading failures, and lost events. Interviewers ask this to test if you understand **distributed systems trade-offs** — not just microservices buzzwords.

### 2. Basic Meaning

| Pattern | Problem It Solves | How |
|---------|------------------|-----|
| **Saga** | Distributed transactions across services | Sequence of local transactions with compensating actions |
| **Outbox** | Reliable event publishing without 2PC | Write event to DB in same transaction as business data |
| **Circuit Breaker** | Cascading failures | Detect failures, stop calling failing service, recover gracefully |

### 3. Real Code / Real Example

```java
// =====================================================
// SAGA PATTERN — Order processing across services
// =====================================================

// Saga: Create Order → Reserve Inventory → Charge Payment → Ship
// If Payment fails: Release Inventory (compensating transaction!)

// Step 1: Order Service creates order
@Transactional
public void createOrder(OrderRequest request) {
    Order order = orderRepository.save(new Order(request));
    // Publish event — via OUTBOX pattern (see below)
    outbox.save(new OutboxEvent("OrderCreated", order.getId()));
}

// Step 2: Inventory Service listens and reserves
@EventListener
public void onOrderCreated(OrderCreatedEvent event) {
    try {
        inventoryService.reserve(event.getItems());
        outbox.save(new OutboxEvent("InventoryReserved", event.getOrderId()));
    } catch (Exception e) {
        outbox.save(new OutboxEvent("InventoryFailed", event.getOrderId()));
    }
}

// Step 3: Payment Service listens
@EventListener
public void onInventoryReserved(InventoryReservedEvent event) {
    try {
        paymentService.charge(event.getOrderId());
        outbox.save(new OutboxEvent("PaymentCompleted", event.getOrderId()));
    } catch (Exception e) {
        // COMPENSATING TRANSACTION: Release inventory!
        outbox.save(new OutboxEvent("PaymentFailed", event.getOrderId()));
    }
}

// Compensating handler:
@EventListener
public void onPaymentFailed(PaymentFailedEvent event) {
    // Release the reserved inventory — ROLLBACK!
    inventoryService.release(event.getOrderId());
}

// =====================================================
// OUTBOX PATTERN — Reliable event publishing
// =====================================================

// Write event to DB in SAME transaction as business data:
@Transactional
public void placeOrder(Order order) {
    // 1. Save business data
    orderRepository.save(order);
    
    // 2. Save outbox event (same DB transaction!)
    outboxRepository.save(new OutboxMessage(
        "OrderCreated", 
        objectMapper.writeValueAsString(order)
    ));
    // If step 1 succeeds and step 2 fails → BOTH rollback (same transaction!)
    // If both succeed → event will be reliably published
}

// Separate process polls outbox and publishes to Kafka/RabbitMQ:
@Scheduled(fixedDelay = 1000)  // Every second
@Transactional
public void publishOutbox() {
    List<OutboxMessage> messages = outboxRepository.findTop100ByPublishedFalse();
    for (OutboxMessage msg : messages) {
        try {
            kafkaTemplate.send(msg.getTopic(), msg.getPayload());
            msg.setPublished(true);  // Mark as published
            // If Kafka is down → next poll will retry
            // At-least-once delivery!
        } catch (Exception e) {
            log.error("Failed to publish message: {}", msg.getId());
            // Will retry on next poll
        }
    }
}

// =====================================================
// CIRCUIT BREAKER — Prevent cascading failures
// =====================================================

// With Resilience4j:
@CircuitBreaker(name = "inventoryService", fallbackMethod = "fallback")
public boolean checkInventory(String sku) {
    return inventoryClient.check(sku);  // HTTP call to inventory service
}

// If 50% of calls fail in last 100 calls → CIRCUIT OPENS!
public boolean fallback(String sku, Throwable t) {
    log.warn("Inventory service unavailable, assuming available: {}", sku);
    return true;  // Graceful degradation
}

// Circuit states: CLOSED (normal) → OPEN (failing) → HALF_OPEN (testing)
```

### 4. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Using distributed transactions (2PC) | Slow, complex, coordinators fail | Use Saga pattern with compensating actions |
| Not handling duplicate events | Idempotency violations | Make event handlers idempotent (check before process) |
| Infinite retries | Overloading downstream services | Use exponential backoff + max retry limit |
| No circuit breaker | One slow service slows all callers | Add circuit breaker with timeout |
| Direct DB access from multiple services | Tight coupling to schema | Each service owns its data, expose via API only |

### 5. Production Usage

**Idempotency key pattern:**
```java
// Prevent duplicate processing of the same event:
@Transactional
public void processPayment(String idempotencyKey, PaymentRequest request) {
    // Check if already processed
    if (processedPayments.exists(idempotencyKey)) {
        return;  // Already processed — skip!
    }
    
    // Process payment
    paymentGateway.charge(request);
    
    // Mark as processed
    processedPayments.save(idempotencyKey);
}
```

### 6. Interview Questions And Answers

#### Beginner

**Q**: What problem does the Outbox pattern solve?

**A**: It solves dual-write problem — when you need to write to DB AND publish an event atomically. Without outbox: you write to DB, then send to Kafka. If Kafka fails after DB write, you have data in DB but no event → inconsistency. Outbox writes event to DB in the same transaction, then a separate process reliably publishes it.

#### Intermediate

**Q**: Explain Saga pattern and when to use choreography vs orchestration.

**A**: Saga is a sequence of local transactions with compensating actions. **Choreography** — each service publishes events and listens to events of interest (decentralized, simpler, harder to track). **Orchestration** — an orchestrator tells each service what to do (centralized, easier to manage, single point of failure). Choreography suits simpler flows (<5 services). Orchestration suits complex flows with branching/loops. Example: Order → Inventory → Payment is simple → choreography works. Travel booking (Flight + Hotel + Car + Payment) → orchestration is better.

#### Senior

**Q**: Design a payment system that guarantees exactly-once processing.

**A**: True exactly-once in distributed systems is impossible — use idempotency to achieve "effectively once." (1) Every request gets unique idempotency key. (2) Payment service checks if key was processed before processing. (3) Store result with key. (4) On retry, return stored result. (5) For Kafka: use idempotent producer + transactional outbox + idempotent consumer. Exactly-once is: at-least-once delivery + idempotent processing. Never rely on exactly-once from messaging alone.

#### Tricky

**Q**: Your microservice calls 3 downstream services. One is slow, causing thread pool exhaustion. How do you handle it?

**A**: (1) Add **timeouts** — set reasonable timeouts on HTTP clients (e.g., 2s connect, 5s read). (2) Add **circuit breaker** — if service fails 50% of calls, open circuit, fail fast. (3) Add **bulkhead** — separate thread pool for each downstream call, so one slow service doesn't exhaust the main pool. (4) Use **asynchronous** calls with CompletableFuture when possible. (5) Monitor thread pool queue depth and rejection rate. Resilience4j or Hystrix provide these patterns.

### 10. Final 30-Second Answer

Microservices need: Saga for distributed transactions (with compensating actions), Outbox for reliable event publishing, Circuit Breaker for fault isolation, Idempotency for safe retries. Each service owns its data. Use async communication (Kafka) over sync (HTTP) when possible. Design for failure — it's inevitable in distributed systems.

---

## Q2. How does Kafka work? Explain producers, consumers, partitions, and exactly-once semantics.

### 1. Why This Concept Matters
Kafka is the de facto standard for event-driven architectures. Understanding partitions, consumer groups, and delivery semantics is essential for designing reliable data pipelines. Interviewers ask this to test **distributed messaging expertise**.

### 2. Basic Meaning
**Kafka**: Distributed event streaming platform. Producers write events to topics. Topics are split into partitions for parallelism. Consumers read from partitions in consumer groups.

### 3. Architecture Overview

```
Producer → Topic (partitioned) → Consumer Group
                                      │
                              ┌───────┴───────┐
                          Consumer 1    Consumer 2
                          (partition 0)  (partition 1-2)

Topic: "orders" (3 partitions)
┌──────────┐ ┌──────────┐ ┌──────────┐
│Partition0│ │Partition1│ │Partition2│
│ msg-0    │ │ msg-1    │ │ msg-2    │
│ msg-3    │ │ msg-4    │ │ msg-5    │
│ ...      │ │ ...      │ │ ...      │
└──────────┘ └──────────┘ └──────────┘
     │             │             │
     │        ┌────┘             │
     ▼        ▼                  ▼
Consumer Group "order-processor"
  Consumer-1: partition 0
  Consumer-2: partition 1, 2  (balanced)
  
If Consumer-2 dies:
  Consumer-1 takes partitions 0, 1, 2 (REBALANCE)
```

### 4. Core Concepts

| Concept | Description | Key Detail |
|---------|-------------|------------|
| **Topic** | Category of events | Durable, ordered within partition |
| **Partition** | Ordered sequence of events | Events have offset (position) |
| **Offset** | Position in partition | Sequential ID, never reused |
| **Producer** | Writes to topic partition | Can specify key for partitioning |
| **Consumer** | Reads from partitions | Part of a consumer group |
| **Consumer Group** | Group of consumers for a topic | Each partition → ONE consumer in group |
| **Broker** | Kafka server | Cluster of brokers |
| **ISR** | In-Sync Replicas | Minimum replicas that acknowledge writes |

### 5. Producer Configuration

```java
// Producer properties for reliability:
Properties props = new Properties();
props.put("bootstrap.servers", "kafka1:9092,kafka2:9092");
props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

// ACKS — durability guarantee:
props.put("acks", "all");  // Leader + ALL ISR replicas acknowledge
// "acks=0": fire-and-forget (fast, can lose data)
// "acks=1": leader only (default, some durability)
// "acks=all": strongest durability (slower, no data loss)

// Idempotent producer (exactly-once):
props.put("enable.idempotence", true);  // No duplicate writes!
// Uses producer ID + sequence number to deduplicate
```

### 6. Consumer Configuration

```java
Properties props = new Properties();
props.put("group.id", "order-processor");    // Consumer group name
props.put("enable.auto.commit", "false");     // Manual offset commit (preferred)

// Auto offset reset — what if no committed offset exists?
props.put("auto.offset.reset", "earliest");
// "earliest": read from beginning
// "latest": read only new messages (default)
// "none": fail if no offset

// Manual offset commit with at-least-once:
while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        try {
            process(record);        // Process the message
            consumer.commitSync();  // Commit AFTER successful processing!
            // If process fails → message is re-processed (at-least-once)
            // If commit fails before process → message is re-processed
        } catch (Exception e) {
            // Retry or send to DLQ
        }
    }
}
```

### 7. Exactly-Once Semantics

```
Delivery Semantics:
┌──────────────────────────────────────────────────┐
│ At-most-once:  May lose (acks=0, auto-commit)    │
│ At-least-once: May duplicate (manual commit)      │
│ Exactly-once:  Neither lose nor duplicate         │
│   (idempotent producer + transactions + EOS)      │
└──────────────────────────────────────────────────┘

Exactly-once requires:
1. enable.idempotence=true (producer dedup)
2. transactional.id=unique (producer transaction)
3. isolation.level=read_committed (consumer)
4. Atomic write to Kafka + process result

Exactly-once is SLOW (~30% throughput reduction) — use only when needed.
Most systems: at-least-once + idempotent consumer is sufficient.
```

### 8. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| More consumers than partitions | Extra consumers sit idle | Max consumers = partitions |
| Auto-commit enabled | Duplicate or lost messages | Use manual commit |
| Not handling rebalance | Processed messages re-processed | Use cooperative rebalancing |
| Fire-and-forget producer | Data loss on broker failure | Use acks=all |
| Same consumer group for different processing | One group slows another | Separate groups for separate work |

### 9. Interview Questions And Answers

#### Beginner

**Q**: What is a Kafka partition and why does it matter?

**A**: A partition is an ordered, immutable sequence of messages. Partitions enable parallelism — multiple consumers (from same group) can read different partitions concurrently. The order guarantee is WITHIN a partition, not across partitions. Messages with the same key go to the same partition, preserving order per key. Partitions also provide fault tolerance through replication.

#### Intermediate

**Q**: How does Kafka achieve high throughput?

**A**: (1) Sequential I/O — writes to disk are append-only, reads are sequential (exploits OS page cache). (2) Zero-copy — sends data from disk to network without copying through application memory. (3) Batching — producers batch messages, consumers fetch in large chunks. (4) Partition parallelism — multiple consumers read in parallel. (5) Minimal overhead — no full message indexing, just offset-based reads. Kafka can handle millions of messages/second on modest hardware.

#### Senior

**Q**: Design a system where an order service must produce events that are consumed by exactly one of N inventory services.

**A**: Use Kafka with message key = orderId (or SKU). Orders with same SKU go to same partition → same consumer. This ensures: (1) All orders for SKU "ABC" go to partition 0 → Consumer A processes them in order; (2) Multiple consumers in same group share partition load; (3) If Consumer A fails, another consumer takes partition 0 (rebalance). For **exactly-once**: idempotent producer + transactional writes + consumer idempotency. For **exactly-one-consumer-per-key**: use a key that maps to one partition, and that partition maps to one consumer in the group.

#### Tricky

**Q**: Can Kafka guarantee order? What happens during a rebalance?

**A**: Kafka guarantees order **within a partition** only. Not across partitions. During rebalance (consumer joins/leaves group), partition assignment changes. Pause can be 5-30 seconds. During rebalance: (1) Old consumer stops processing; (2) New consumer starts from last committed offset; (3) Messages between last commit and rebalance may be reprocessed. This breaks strict ordering temporarily. For strict ordering: single partition (sacrifices parallelism) or use sequential processing with a key that maps to one partition.

### 10. Final 30-Second Answer

Kafka: topics → partitions → consumers. Producers write with keys for partitioning. Consumer groups share partition load. At-least-once (manual commit + idempotent processing) for most cases. Exactly-once (idempotent + transactions) for critical data. Order guaranteed within partition only. Use acks=all for durability. Handle rebalances gracefully.