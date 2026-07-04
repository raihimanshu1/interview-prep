# ToneTag Senior Java Developer Interview Questions

## Research Status

## 1. Web-Confirmed Exact ToneTag Questions

No exact Senior Java Developer interview questions were found from reliable accessible public sources.

Do not claim the below questions were definitely asked by ToneTag. Use them as targeted preparation for a Senior Java Developer interview at ToneTag-like fintech/payment companies.

## 2. Java Core Questions

1. Explain Java memory model and happens-before relationship.
2. Difference between `volatile`, `synchronized`, and `AtomicInteger`.
3. How does `ConcurrentHashMap` work internally?
4. Difference between `HashMap`, `LinkedHashMap`, `TreeMap`, and `ConcurrentHashMap`.
5. What happens if `equals()` is overridden but `hashCode()` is not?
6. Explain immutable class design in Java.
7. How do you handle memory leaks in Java applications?
8. Explain garbage collection and common GC algorithms.
9. What is the difference between G1 GC and ZGC?
10. How do you tune JVM for a high-throughput backend service?
11. Explain class loading in Java.
12. Difference between checked and unchecked exceptions.
13. When would you use custom exceptions?
14. Explain Java 8 streams and when not to use them.
15. Difference between `map`, `flatMap`, `filter`, and `reduce`.
16. Explain functional interfaces and lambdas.
17. What are records in Java and when are they useful?
18. What are sealed classes?
19. Explain try-with-resources.
20. How do you safely compare `BigDecimal` values in payment systems?

## 3. Multithreading And Concurrency

1. How would you process millions of payment events concurrently?
2. Difference between thread, executor service, fork-join pool, and virtual threads.
3. How do you design a thread-safe service?
4. What is a race condition? Give an example.
5. What is deadlock? How do you detect and prevent it?
6. Explain optimistic locking and pessimistic locking.
7. How does `CompletableFuture` work?
8. How would you combine results from multiple async services?
9. How do you handle timeout and cancellation in async Java code?
10. What is backpressure?
11. How do you prevent one tenant or merchant from overwhelming a shared system?
12. How would you implement rate limiting in Java?
13. Token bucket vs leaky bucket.
14. How do you design a retry mechanism without creating duplicate transactions?
15. How do you avoid thread starvation?

## 4. Spring Boot Questions

1. Explain Spring Boot auto-configuration.
2. Difference between `@Component`, `@Service`, `@Repository`, and `@Controller`.
3. Difference between `@RestController` and `@Controller`.
4. How does dependency injection work in Spring?
5. Constructor injection vs field injection.
6. What is the Spring bean lifecycle?
7. What are Spring profiles?
8. How do you externalize configuration?
9. Explain Spring Boot Actuator.
10. How do you secure REST APIs in Spring Boot?
11. How do you implement request validation?
12. How do you handle global exceptions?
13. How do you version APIs?
14. How do you implement idempotent APIs?
15. How do you design a payment callback endpoint?
16. How do you verify webhook signatures?
17. How do you implement correlation id tracing?
18. How do you prevent sensitive data from leaking into logs?
19. How do you implement graceful shutdown?
20. How do you test Spring Boot APIs?

## 5. Microservices Questions

1. How would you split a monolith into microservices?
2. What services would you create for a payment/POS platform?
3. How do microservices communicate with each other?
4. REST vs gRPC vs messaging.
5. When would you choose synchronous communication?
6. When would you choose asynchronous messaging?
7. How do you handle distributed transactions?
8. Explain saga pattern.
9. Explain outbox pattern.
10. Explain idempotency in distributed systems.
11. How do you handle partial failure?
12. What is circuit breaker pattern?
13. What is bulkhead pattern?
14. How do you handle service discovery?
15. How do you manage API contracts?
16. How do you deploy microservices safely?
17. Blue-green vs canary deployment.
18. How do you design multi-region services?
19. How do you avoid cascading failures?
20. How do you monitor microservices?

## 6. Payments And Fintech Domain Questions

1. How would you design a payment processing system?
2. How do you ensure a payment is not processed twice?
3. What is idempotency key and where do you store it?
4. How do you handle payment timeout after money is debited?
5. How do you reconcile payment state with bank/UPI/provider state?
6. What is the difference between authorization, capture, settlement, and refund?
7. How do you design refunds?
8. How do you design chargeback/dispute handling?
9. How do you handle webhook retries from payment providers?
10. How do you handle duplicate callbacks?
11. How would you design a ledger?
12. Why should ledger entries be append-only?
13. What is double-entry accounting?
14. How do you maintain transaction audit trail?
15. How do you handle eventual consistency in payment status?
16. How would you design a QR payment flow?
17. How would you design a POS device transaction flow?
18. How do you secure communication between POS device and backend?
19. How do you handle offline or poor-network merchant devices?
20. How do you detect fraudulent transactions?

## 7. ToneTag-Specific Domain Preparation

These are not confirmed interview questions, but they are highly relevant to ToneTag's public business area.

1. How would you design a sound-based payment system using phone speaker/microphone?
2. What can go wrong in audio-based proximity payments?
3. How do you prevent replay attacks in proximity-based payment?
4. How do you validate that a transaction came from a nearby device?
5. How would you design a payment-capable edge device?
6. How would you sync POS device transactions to backend reliably?
7. How would you handle 25 million transactions per day?
8. How would you design merchant transaction history lookup?
9. How would you support multilingual voice-driven merchant commands?
10. How would you design analytics for merchants using POS devices?
11. How would you design inventory update after payment success?
12. How do you handle duplicate sale events from a POS device?
13. How do you securely rotate device keys?
14. How do you onboard a new merchant device?
15. How would you handle device firmware version compatibility?
16. How do you design real-time payment notifications?
17. How do you build an audit trail for every transaction?
18. How do you store and mask customer/merchant PII?
19. How do you design a low-latency transaction authorization API?
20. How would you throttle traffic from one merchant or partner?

## 8. Database Questions

1. SQL vs NoSQL for payments.
2. How would you design tables for transactions, merchants, devices, and settlements?
3. How do you choose indexes for transaction search?
4. Composite index design for `merchantId + createdAt + status`.
5. What is database partitioning?
6. Partition by merchant, date, or transaction id?
7. How do you archive old transaction data?
8. How do you handle high write throughput?
9. Explain isolation levels.
10. Read committed vs repeatable read vs serializable.
11. What is phantom read?
12. How do you avoid lost updates?
13. Optimistic locking with version column.
14. How do you design audit tables?
15. How do you store immutable ledger entries?
16. How do you reconcile failed/unknown payments?
17. How do you avoid N+1 queries in Hibernate?
18. Lazy loading vs eager loading.
19. What is connection pooling?
20. How do you debug slow SQL queries?

## 9. Kafka / Messaging Questions

1. Why use Kafka in payment systems?
2. Kafka vs RabbitMQ vs SQS.
3. What is a topic, partition, offset, and consumer group?
4. How does Kafka provide ordering?
5. Ordering by merchant id vs transaction id.
6. How do you handle duplicate messages?
7. What is at-least-once delivery?
8. What is exactly-once semantics in Kafka?
9. Why external payment providers still make exactly-once hard?
10. How do you design dead-letter queues?
11. How do you handle poison messages?
12. How do you replay events safely?
13. How do you handle schema evolution?
14. Avro vs JSON vs Protobuf.
15. How do you monitor Kafka lag?
16. How do you implement retry topics?
17. How do you avoid message loss?
18. How do you make consumers idempotent?
19. How do you process high-priority events before bulk events?
20. How do you guarantee fairness across tenants or merchants?

## 10. System Design Questions

1. Design a high-throughput payment processing platform.
2. Design a POS transaction system.
3. Design a QR-code payment system.
4. Design a merchant analytics dashboard.
5. Design a transaction reconciliation system.
6. Design a real-time fraud detection system.
7. Design a notification service for payment success/failure.
8. Design an audit logging platform.
9. Design an API gateway for payment APIs.
10. Design a device management platform for POS devices.
11. Design a multi-tenant SaaS platform for merchants.
12. Design a rate limiter for merchant APIs.
13. Design a settlement file generation system.
14. Design a transaction search service.
15. Design a system that handles provider callbacks reliably.
16. Design a campaign/offer system for retail merchants.
17. Design inventory and payment integration for retailers.
18. Design an offline-first POS device sync system.
19. Design a dashboard showing real-time transaction metrics.
20. Design a secure webhook delivery system.

## 11. Security Questions

1. How do you secure payment APIs?
2. OAuth2 vs JWT.
3. How do you validate JWT?
4. How do you secure service-to-service calls?
5. mTLS vs API keys.
6. How do you store secrets?
7. How do you rotate API keys?
8. How do you prevent replay attacks?
9. How do you secure webhooks?
10. How do you protect PII in logs?
11. How do you encrypt sensitive data at rest?
12. How do you handle PCI-DSS concerns?
13. How do you avoid storing card data?
14. How do you design RBAC for merchant dashboard?
15. How do you audit admin actions?
16. How do you prevent SQL injection?
17. How do you prevent SSRF?
18. How do you prevent mass assignment vulnerabilities?
19. How do you handle fraud/risk signals?
20. How do you design secure device registration?

## 12. Coding / DSA Questions

1. LRU cache implementation.
2. Rate limiter implementation.
3. Design an idempotency-key store.
4. Find duplicate transaction ids in a stream.
5. Process events in order per merchant.
6. Merge intervals for offer validity windows.
7. Top K merchants by transaction volume.
8. Sliding window maximum for transaction spikes.
9. Design a thread-safe bounded queue.
10. Producer-consumer problem.
11. Implement retry with exponential backoff.
12. Parse and validate transaction records.
13. Group transactions by merchant and day.
14. Detect suspicious repeated transactions.
15. Find missing sequence numbers from device events.
16. Implement token bucket rate limiter.
17. Implement consistent hashing for merchant partitioning.
18. Design a simple in-memory ledger.
19. Build a TTL cache.
20. Find longest consecutive transaction failure streak.

## 13. Behavioral / Senior-Level Questions

1. Tell me about a production issue you debugged.
2. Tell me about a time you improved system performance.
3. Tell me about a time you handled a payment or data correctness bug.
4. How do you mentor junior developers?
5. How do you review code?
6. How do you decide between quick fix and long-term solution?
7. Tell me about a time you disagreed with an architect or manager.
8. How do you handle ambiguous requirements?
9. How do you estimate a backend project?
10. How do you communicate risk to business teams?
11. Tell me about a time you reduced latency.
12. Tell me about a time you designed a scalable system.
13. How do you prioritize technical debt?
14. How do you handle on-call incidents?
15. What metrics do you check first during production failure?
16. How do you make sure your service is observable?
17. How do you handle security requirements?
18. How do you ensure code quality in a fast-moving team?
19. Tell me about a failure and what you learned.
20. Why ToneTag?

## 14. Best Areas To Prepare First

If time is short, prioritize:

1. Java concurrency and JVM.
2. Spring Boot REST APIs.
3. Idempotency and duplicate prevention.
4. Payment transaction lifecycle.
5. Kafka/messaging and outbox pattern.
6. SQL indexing, transactions, and locking.
7. System design for payment/POS/reconciliation.
8. Security: JWT, mTLS, replay prevention, webhook signatures.
9. Observability: logs, metrics, tracing, alerts.
10. Behavioral stories around production ownership.
