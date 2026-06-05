# System Design / Microservices Quick Refresher

## CQRS

### What Is It?

CQRS means Command Query Responsibility Segregation.

```text
Command -> write/change data
Query   -> read/fetch data
```

### Why It Matters

Reads and writes often need different models.

```text
Writes -> validation, transaction, audit, consistency
Reads  -> speed, filters, search, cache, aggregation
```

### Banking Example

```text
POST /payments              -> command
GET /payments/{id}/status   -> query
GET /accounts/{id}/history  -> query
```

The write side can update payment/ledger tables.

The read side can use:

```text
payment_read_view
account_summary_view
Redis cache
Elasticsearch
```

### Trade-Off

Pros:

```text
Separates read/write concerns
Scales reads independently
Simplifies complex queries
Supports audit/event workflows
```

Cons:

```text
More moving parts
Read model lag
Eventual consistency
Projection failures
```

### Interview Answer

CQRS separates write operations from read operations. Commands change state and enforce business rules, while queries fetch optimized read views. It helps when reads and writes have different scale or complexity. The trade-off is eventual consistency because the read model may lag behind the write model.

---

## Circuit Breaker

### What Is It?

A circuit breaker stops calls to an unhealthy downstream service.

In simple terms:

```text
If dependency is failing, stop calling it temporarily.
Fail fast instead of exhausting threads.
```

### Flow

```text
Closed    -> normal calls
Open      -> calls blocked
Half-open -> test limited calls
```

### Example

```text
Payment Service -> Fraud Service
```

If Fraud Service becomes slow:

```text
threads block
queues grow
payment service slows
other services fail
```

Circuit breaker prevents cascading failure.

### Production Mindset

Use with:

```text
timeouts
bulkheads
limited retries
fallbacks
metrics
alerts
```

Spring tool:

```text
Resilience4j
Spring Cloud Circuit Breaker
```

### Interview Answer

A circuit breaker protects a service from repeatedly calling an unhealthy dependency. It opens after failures cross a threshold, fails fast for a while, then tries limited half-open calls. In banking flows, fallback must be business-safe; we should not approve payments just because a fraud service is down.

---

## Biggest Challenge In Global Distributed Cache

### What Is It?

A global distributed cache stores frequently used data across regions.

Examples:

```text
Redis
Memcached
Hazelcast
CDN/edge cache
```

### Biggest Challenge

```text
Consistency across regions
```

If data changes in one region, other regions may still have stale values.

### Example

```text
Region A updates customer preference
Region B cache still has old value
Region C cache has expired value
```

### Production Mindset

For banking:

```text
Do not trust cache for final balance deduction
Use cache for preferences, reference data, offers, feature flags
Use source of truth for money movement
```

Guardrails:

```text
TTL
versioned keys
event-based invalidation
read repair
hot-key monitoring
regional failover plan
```

### Interview Answer

The biggest challenge in a global distributed cache is consistency. Low latency pushes us toward local regional caches, but updates may arrive late in other regions. For critical banking data like ledger balance, I would use the database/source of truth. For less critical read-heavy data, I would use TTLs, invalidation events, versioned keys, and stale-data monitoring.

---

## Dead Letter Queue

### What Is It?

A Dead Letter Queue stores messages that failed processing after retries.

In simple terms:

```text
Do not let one bad message block the whole queue.
Move it to DLQ and inspect later.
```

### Example

```json
{
  "paymentId": "P100",
  "amount": null
}
```

Consumer expects `amount`, fails repeatedly, then message goes to DLQ.

### DLQ Metadata

```text
original message
error reason
retry count
timestamp
consumer name
trace ID
correlation ID
```

### Production Mindset

DLQ is not a trash bin.

You must monitor:

```text
DLQ size
oldest message age
repeated error reason
replay success/failure
poison message patterns
```

### Interview Answer

A DLQ stores failed messages so one poison message does not block the main queue. In production, I include failure metadata, alert on DLQ growth, fix the root cause, and replay carefully with idempotency so financial operations are not duplicated.

---

## Design Scalable Microservices

### Core Idea

A scalable microservice system has clear service boundaries, independent scaling, failure isolation, and strong observability.

### Architecture

```text
Client
  |
API Gateway
  |
Microservices
  |
Database per service
  |
Kafka / Queue
  |
Observability platform
```

### Key Principles

```text
database per service
clear API contracts
async events for decoupling
timeouts and circuit breakers
idempotency for retries
service discovery
centralized observability
defense-in-depth security
```

### Common Mistake

```text
Splitting code into services but sharing one database and tight synchronous calls.
```

That becomes a distributed monolith.

### Interview Answer

To design scalable microservices, I define service boundaries around business capabilities, give each service ownership of its data, expose stable APIs, and use async messaging for workflows that do not need immediate response. I add resilience patterns, idempotency, observability, and secure service-to-service communication. The hardest parts are data consistency, boundaries, and debugging cross-service failures.

---

## API Backward Compatibility

### What Is It?

An API is backward compatible when old clients continue working after backend changes.

```text
Old client + new backend = still works
```

### Breaking Change

```json
{
  "id": 10,
  "name": "John"
}
```

changed to:

```json
{
  "userId": 10,
  "fullName": "John"
}
```

Old clients break.

### Safe Change

```json
{
  "id": 10,
  "name": "John",
  "userId": 10,
  "fullName": "John"
}
```

### Forward Compatibility

```text
Clients ignore unknown fields
Clients tolerate missing optional fields
Clients handle default values
```

### Semantic Versioning

```text
MAJOR -> breaking change
MINOR -> compatible feature
PATCH -> bug fix
```

### Big-Company Mindset

Large companies prefer:

```text
additive changes
versioned APIs
deprecation windows
contract tests
usage monitoring
migration docs
gradual rollout
```

### Interview Answer

API backward compatibility means existing clients do not break when the backend evolves. I prefer additive changes and optional fields. For breaking changes, I create a new version, keep the old version during migration, monitor usage, and deprecate safely.

---

## How To Secure Microservices

### Security Layers

```text
authentication
authorization
TLS/mTLS
input validation
rate limiting
secrets management
audit logging
network policies
dependency scanning
```

### Flow

```text
Client
  |
API Gateway validates token
  |
Service checks scopes/roles
  |
Validate request
  |
Business logic
  |
Audit log
```

### Spring Example

```java
@PreAuthorize("hasAuthority('SCOPE_payment:write')")
public PaymentResponse createPayment(PaymentRequest request) {
    return service.create(request);
}
```

### Interview Answer

I secure microservices with defense in depth. The gateway validates tokens and applies rate limits, but each service still checks authorization. For service-to-service calls, I use mTLS or OAuth2 client credentials. I validate input, protect secrets, avoid logging sensitive data, and maintain audit logs with correlation IDs.

---

## Binary Search Complexity

### What Is It?

Binary search finds a target in sorted data by repeatedly cutting the search space in half.

### Complexity

```text
Time:  O(log n)
Space: O(1) iterative
Space: O(log n) recursive
```

### Example

```text
n = 1,000,000
binary search ~= 20 comparisons
because 2^20 ~= 1,000,000
```

### Java Tip

```java
int mid = left + (right - left) / 2;
```

This avoids overflow.

### Interview Answer

Binary search works only on sorted data. It repeatedly halves the search space, so the time complexity is `O(log n)`. The iterative version uses `O(1)` space.

---

## Load Shedding vs Hash Key / Sharding

### Load Shedding

Load shedding rejects excess traffic to protect the system.

```text
overloaded service -> reject low priority requests -> stay alive
```

### Sharding / Hash Key

Sharding distributes data or traffic.

```text
hash(accountId) % shardCount
```

### Difference

```text
Sharding      -> spreads normal load
Load shedding -> drops overload
```

### Production Example

```text
Payment requests -> high priority
Analytics refresh -> can be shed/delayed
```

### Interview Answer

Sharding distributes load using a key, while load shedding rejects traffic when the system is already overloaded. A good shard key spreads traffic evenly, but hot keys can still overload one shard. In production, I use sharding for scale and load shedding for protection.

---

## Service Discovery

### What Is It?

Service discovery lets services find healthy service instances dynamically.

### Tools

```text
Kubernetes DNS
Eureka
Consul
Zookeeper
AWS Cloud Map
Istio/service mesh
```

### Example

```text
Account Service calls http://payment-service
Kubernetes routes to a healthy payment pod
```

### Production Mindset

Service discovery must be paired with:

```text
health checks
timeouts
retries
circuit breakers
load balancing
zone-aware routing
```

### Interview Answer

Service discovery helps microservices locate healthy instances without hardcoded IPs. In Kubernetes, DNS and Services provide this. In older Spring Cloud systems, Eureka or Consul may be used. Discovery tells where a service is, but resilience patterns are still needed because discovered services can fail.

---

## Strict Review Fixes: Follow-Up Questions And Design Depth

This section adds the mandatory interview follow-ups and the extra design depth called out by review: requirements, flow, trade-offs, failure handling, scaling, and observability.

### CQRS Follow-Ups

Likely follow-up questions:

```text
When should CQRS not be used?
How do read models stay updated?
What consistency model does CQRS usually introduce?
How is CQRS different from event sourcing?
```

Senior detail:

```text
CQRS helps when read and write workloads have different scale, shape, or latency needs.
It adds complexity, so it should not be used for simple CRUD unless there is a real reason.
```

### Circuit Breaker Follow-Ups

Likely follow-up questions:

```text
What are closed, open, and half-open states?
How is circuit breaker different from retry?
Why can retries worsen an outage?
What fallback would you return for a non-critical dependency?
```

Senior detail:

```text
Circuit breaker protects a service from repeatedly calling a failing dependency.
It should be paired with timeouts, limited retries, metrics, alerts, and fallback behavior.
```

### Global Distributed Cache Follow-Ups

Extra failure example:

```text
Region A updates customer profile.
Region B cache still has old profile.
User reads stale data until invalidation/replication completes.
```

Key challenges:

```text
Cache invalidation
Replication lag
Stale reads
Hot keys
Failover behavior
Regional consistency
Conflict handling
Observability of hit rate and lag
```

Likely follow-up questions:

```text
How do you handle cache invalidation globally?
What is a hot key?
When should you bypass cache?
Why is cache risky for account balance?
```

Interview answer:

> The biggest challenge in a global distributed cache is keeping data fast and consistent across regions. Replication lag, invalidation delay, hot keys, and failover can cause stale or uneven behavior. For critical banking data like balances, I would be careful using cache as the source of truth. I would monitor hit rate, stale reads, regional latency, replication lag, and fallback to the database when correctness matters.

### Dead Letter Queue Follow-Ups

Full flow:

```text
Main queue
   |
Consumer fails
   |
Retry with backoff
   |
Max retries exceeded
   |
Dead letter queue
   |
Alert + inspect + fix
   |
Safe replay
```

Replay safety:

```text
Use idempotency keys
Avoid duplicate side effects
Do not replay poison messages blindly
Track reason for failure
Limit replay rate
```

Likely follow-up questions:

```text
When should a message go to DLQ?
How do you replay DLQ messages safely?
What is a poison message?
Why is idempotency important for consumers?
```

Interview answer:

> A dead letter queue stores messages that cannot be processed after retries. It prevents bad messages from blocking the main queue and gives teams a way to inspect, fix, and safely replay failures. In production, I use retry with backoff, alerting, idempotent consumers, failure reason tracking, and controlled replay.

### Design Scalable Microservices Deep-Dive

Requirements:

```text
High availability
Independent deployment
Horizontal scaling
Clear service ownership
Secure service communication
Observability
Failure isolation
Data consistency strategy
```

High level flow:

```text
Client
   |
API Gateway
   |
Service A
   |
Database / Cache / Queue
   |
Service B through event or API
```

Scaling:

```text
Stateless services scale horizontally
Database scales using indexing, read replicas, partitioning, or sharding
Queues absorb spikes
Cache reduces repeated reads
Autoscaling uses CPU, memory, latency, or queue depth
```

Failure handling:

```text
Timeouts
Retries with backoff
Circuit breakers
Bulkheads
DLQ
Idempotency
Graceful degradation
Rollback/canary deployment
```

Observability:

```text
Correlation IDs
Centralized logs
Distributed tracing
Metrics
Dashboards
Alerts
SLOs
Error budget
```

Trade-offs:

```text
Microservices improve independent scaling and deployment,
but add network latency, distributed debugging, data consistency,
versioning, and operational complexity.
```

Likely follow-up questions:

```text
How do services communicate: REST, gRPC, or events?
How do you avoid distributed transactions?
How do you debug a request across services?
How do you deploy without breaking consumers?
```

Interview answer:

> To design scalable microservices, I would define service boundaries around business capabilities, keep services stateless where possible, give each service clear data ownership, and use APIs or events for communication. I would add API gateway, service discovery, security, observability, retries, circuit breakers, queues, idempotency, and independent deployment. The main trade-off is that microservices scale teams and services, but increase operational and consistency complexity.

### API Backward Compatibility Follow-Ups

Likely follow-up questions:

```text
What is a breaking API change?
How do you deprecate old fields?
How does forward compatibility differ?
How does semantic versioning apply to SDKs?
```

Senior detail:

```text
Big companies evolve APIs by adding fields/endpoints, preserving old behavior,
using versioning for breaking changes, publishing migration guides, monitoring usage,
and removing deprecated behavior only after consumers migrate.
```

### Microservices Security Follow-Ups

More production detail:

```text
OAuth2/JWT validation
mTLS for service-to-service calls
Least privilege
Secret rotation
PII masking in logs
Audit trails
Rate limiting
Input validation
Network policies
Token expiry and refresh strategy
```

Debugging 401/403:

```text
Check token expiry
Check audience/issuer
Check scopes/roles
Check gateway policy
Check service-to-service certificate
Check clock skew
Check route-level authorization rules
```

Likely follow-up questions:

```text
Authentication vs authorization?
Why use mTLS between services?
How do you rotate secrets safely?
What should not be logged in banking systems?
```

Interview answer:

> I secure microservices using defense in depth: API gateway validation, OAuth2/JWT, mTLS for service calls, least privilege, network policies, secret rotation, input validation, rate limiting, audit logs, and PII masking. In production, I also monitor auth failures and debug 401/403 by checking token expiry, scopes, issuer, audience, gateway policy, and service certificates.

### Binary Search Follow-Ups

Common mistakes:

```text
Using binary search on unsorted data
Infinite loop due to wrong left/right update
Integer overflow in midpoint calculation
Not handling duplicates when first/last occurrence is required
Off-by-one errors
```

Safe midpoint:

```java
int mid = left + (right - left) / 2;
```

Likely follow-up questions:

```text
Why must input be sorted?
How do you find first occurrence?
What is time complexity?
What is space complexity for iterative version?
```

Interview answer:

> Binary search works on sorted data by repeatedly halving the search range. Its time complexity is O(log n), and iterative binary search uses O(1) space. Common mistakes are off-by-one errors, infinite loops, and using it on unsorted data. For Java, I prefer `left + (right - left) / 2` to avoid midpoint overflow.

### Load Shedding And Shard Key Follow-Ups

Shard key trade-offs:

```text
Even distribution
Avoid hot partitions
Support common query patterns
Minimize cross-shard joins
Allow rebalancing
Preserve data locality where useful
Use consistent hashing when nodes change often
```

Load shedding trade-offs:

```text
Protects system availability
May reject real users
Needs priority rules
Should fail fast with clear error
```

Likely follow-up questions:

```text
How do you choose a shard key?
What is a hot partition?
What is consistent hashing?
When would you shed traffic?
```

Interview answer:

> Sharding spreads data or traffic using a shard key, while load shedding rejects or delays excess traffic to protect the system. A good shard key distributes load evenly and supports access patterns, but a bad key creates hot partitions. Load shedding is a resilience mechanism used during overload, often with priority rules so critical requests are protected.

### Service Discovery Follow-Ups

Client-side vs server-side:

```text
Client-side discovery -> client chooses instance from registry
Server-side discovery -> load balancer/proxy chooses instance
```

Kubernetes behavior:

```text
Service name resolves through DNS.
Kubernetes routes traffic to healthy pods behind the Service.
```

Failure risks:

```text
Stale registry entry
Bad health check
Zone outage
DNS caching issue
Instance marked healthy but dependency is down
```

Observability:

```text
Failed resolution count
5xx by instance
Latency by instance/zone
Health check failures
Traffic distribution
```

Likely follow-up questions:

```text
How does Kubernetes service discovery work?
What is client-side discovery?
Why are health checks important?
Does discovery replace circuit breaker?
```

Interview answer:

> Service discovery lets services find healthy instances dynamically. Kubernetes commonly provides this through Services and DNS, while systems like Eureka or Consul provide registries. Discovery must be combined with health checks, load balancing, timeouts, retries, and circuit breakers because a discovered service can still be slow or unhealthy.
