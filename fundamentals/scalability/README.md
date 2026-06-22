# Scalability & System Design — Complete Deep Dive

## 1. Why This Concept Matters

Scalability is the ability of a system to handle increasing load by adding resources. Without deliberate design, systems hit bottlenecks: the database becomes the single write point, the application server runs out of memory handling sessions, or cache misses cascade into database overload. In production systems handling millions of users, scalability failures manifest as page load times increasing from 200ms to 30s, databases hitting connection limits, and costs growing super-linearly with traffic. Interviewers test this at senior+ levels because scaling decisions involve fundamental tradeoffs — consistency vs availability, read throughput vs write throughput, simplicity vs performance. Every design decision has a scaling implication.

Misunderstanding scalability causes:
- Vertical scaling trap: upgrading to bigger machines until hitting hardware limits (no more CPU/memory available)
- Database as bottleneck: all reads and writes hit a single database, causing connection pool exhaustion
- Stateful services: session data in application memory prevents horizontal scaling (requests must route to same server)
- Cache without eviction policy: memory fills, cache thrashing, DB overload when cache misses spike
- No backpressure: producers overwhelm consumers, causing message queue growth forever

## 2. Basic Meaning

Scalability = ability to handle more load cost-effectively.

**Two fundamental approaches:**
- **Vertical scaling (scale up)**: make the machine bigger (more CPU cores, more RAM, faster disks, SSD → NVMe). Simple — no code changes needed. Limited by hardware maximums. A single server can only get so big (today: max ~4TB RAM, 128 CPU cores). Cost grows super-linearly (a 2x bigger machine costs more than 2x).
- **Horizontal scaling (scale out)**: add more machines. Requires application changes (stateless services, shared nothing architecture). Theoretically unlimited — you can keep adding machines. Cost grows linearly. Preferred approach for modern systems.

**Key vocabulary:**
- **Throughput**: requests per second (RPS/QPS). The primary scalability metric.
- **Latency**: time to process a single request. Often increases as throughput increases (queueing).
- **P99 latency**: the worst 1% of request latencies. Critical for user experience.
- **Load balancer**: distributes incoming requests across multiple backend instances. Algorithms: Round Robin (simple, ignores load), Least Connections (sends to least busy), IP Hash (sticky sessions), Weighted (ratio-based), Geographic (route to nearest region).
- **Stateless service**: any instance handles any request. No local state per user. Enables true horizontal scaling.
- **Database read replicas**: primary handles writes, replicas handle reads. Scale reads horizontally. Writes still bottleneck on primary.
- **Database sharding**: split data across multiple databases. Each shard holds a subset. Scale writes horizontally. Complex joins across shards.
- **Caching**: store frequently accessed data in fast storage (Redis, in-memory cache). Reduces database load. Patterns: cache-aside (lazy load), write-through (write cache + DB), write-behind (write cache, async write DB).
- **CDN**: edge servers that cache static content (images, CSS, JS, video) geographically close to users. Reduces origin server load, improves latency.
- **Content Delivery**: static assets served from CDN edges, dynamic content from origin servers.
- **Partitioning**: splitting data across nodes. Horizontal partitioning = sharding. Vertical partitioning = splitting table columns across tables.
- **CAP theorem**: Consistency, Availability, Partition Tolerance — you can only have two out of three in a distributed system. Under network partition (P), you must choose between Consistency (C) and Availability (A).
- **PACELC extension**: If a partition occurs (P), trade off Availability vs Consistency (A vs C). Else (E), trade off Latency vs Consistency (L vs C).
- **Consistent hashing**: distribute keys across nodes with minimal rebalancing when nodes are added/removed. Each key maps to a position on a hash ring. Each node covers a range. When a node is removed, only its keys are redistributed (not all keys).
- **Rate limiting**: throttle requests to protect backend. Algorithms: Token Bucket (fixed rate allows bursts), Leaky Bucket (smooths bursts to constant rate), Sliding Window (counts requests in time window), Fixed Window (resets counter at window boundary).
- **Backpressure**: mechanism where consumer signals producer to slow down. Prevents system overload. Implemented via bounded queues, circuit breakers, or flow control protocols.
- **Auto-scaling**: automatically add/remove instances based on metrics (CPU > 70%, request queue depth > threshold, memory > 80%).

## 3. Common Patterns

| Pattern | Description | Impact |
|---------|-------------|--------|
| **Stateless services** | No session state on app server (stored in Redis). Any instance handles any request. | True horizontal scaling. Can scale from 1 to 1000 instances. |
| **Database read replicas** | Primary for writes, replicas for reads. Replication lag: ms-seconds. | Scales reads linearly with replica count. Writes still bottleneck. |
| **Database sharding** | Split data by key (user_id hash). Each shard is independent DB. | Scales writes linearly with shard count. Complex cross-shard queries. |
| **Caching (cache-aside)** | App checks cache → miss → loads from DB → stores in cache with TTL. | Reduces DB read load by 90%+ for hot data. Cache miss storms dangerous. |
| **CDN** | Static assets at edge locations. Users download from nearest CDN edge. | Reduces origin load by 80%+ for static content. Improves global latency. |
| **Async processing** | Queue tasks (Kafka/SQS), process asynchronously. Decouples producers from consumers. | Producers never wait for consumers. Buffer spikes. Handle traffic bursts. |
| **Bulkheading** | Isolate resources per dependency. Separate thread pools. | One slow dependency doesn't exhaust all threads. Failure isolation. |
| **Auto-scaling** | Add/remove instances based on metrics. Cloud: scale up/down on demand. | Match capacity to demand. Save cost during low traffic. |

## 4. Scaling the Database

**Evolution of database scaling:**
```
Phase 1: Single server + single database
  App → DB
  Bottleneck: DB CPU, memory, disk IO
  
Phase 2: Read replicas
  App → Primary (writes) → Replica1 (reads)
                       → Replica2 (reads)
  Scales reads. Writes still on single primary.
  
Phase 3: Read replicas + cache
  App → Cache (Redis) — 90% cache hit rate → most reads never hit DB
      → Primary (writes)
      → Replicas (reads that miss cache)
  Scales reads massively. Writes still bottleneck.

Phase 4: Sharding
  App → Shard1 (users 1-1M)
      → Shard2 (users 1M-2M)
      → ShardN...
  Scales writes. Cross-shard joins complex.
  
Phase 5: Sharding + replicas + cache
  App → Cache → Shard1_Primary → Shard1_Replica
                              → Shard2_Primary → Shard2_Replica
                              ...
  Fully distributed. Complex operations.
```

**Choose sharding key carefully:**
- Hash-based (user_id % N): even distribution. Simple. Adding shards = rehashing all data.
- Range-based (user_id 1-1M to shard1, 1M-2M to shard2): simple range queries. Uneven distribution possible (hot users).
- Directory-based (lookup table mapping key to shard): flexible (move data between shards). Lookup overhead, single point of failure.
- Geographic (us-east → shard1, eu-west → shard2): low latency for regional users. Uneven distribution.

**Consistent hashing solution for rebalancing:**
- Hash ring: keys and nodes map to positions on a ring.
- Each node covers the range from its position to the next node.
- When adding a node, only keys between the new node and its successor are redistributed.
- Virtual nodes: each physical node appears multiple times on ring (improves distribution).

## 5. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Stateful services (session in memory) | Can't scale horizontally — sticky sessions required | Store session in Redis (externalize state) |
| No caching | DB overload from repeated reads | Add cache layer (cache-aside pattern) |
| Single database for everything | Write bottleneck, no horizontal scaling | Read replicas + sharding |
| No connection pooling | Each request opens new DB connection (slow) | HikariCP (reuse connections) |
| Synchronous processing of slow tasks | Request thread blocked for seconds | Async processing (Kafka, SQS) |
| No auto-scaling | Pay for peak capacity 24/7, or crash under load | Auto-scaling groups based on CPU/queue depth |
| Chatty APIs (many small calls) | N network round trips → high latency | Batch requests, GraphQL, or bulk endpoints |
| Ignoring CAP theorem | Data inconsistency or availability loss during partitions | Choose consistency model intentionally |

## 6. CAP Theorem in Practice

**CAP breakdown:**
- **Consistency**: all nodes see the same data at the same time. After a write, all readers see the new value.
- **Availability**: every request gets a response (success or failure). System continues operating despite node failures.
- **Partition Tolerance**: system continues operating despite network failures (dropped packets, split between nodes).

**Choose two:**

| Database | Category | Behavior |
|----------|----------|----------|
| PostgreSQL (single) | CA | Strong consistency, high availability within single node. Not partition tolerant (doesn't distribute). |
| Cassandra | AP | Available + Partition tolerant. Eventual consistency. All nodes accept writes. Conflicts resolved via last-writer-wins. |
| HBase | CP | Consistent + Partition tolerant. Single write master. Availability reduced if master fails. |
| Redis Cluster | CP (default) | Consistent within partition. Can be unavailable during failover. |
| DynamoDB | AP | Eventual consistency by default. Strongly consistent reads available (higher latency). |

**Real-world:**
- Banking systems: CP (consistency over availability). Losing money is worse than brief downtime.
- Social media: AP (availability over consistency). A few seconds of stale data is acceptable.
- CDNs: AP. Stale content is better than no content.

## 7. Production Usage

**Estimating scale (1 million DAU):**
```
1M DAU:
  - 10% peak concurrent = 100,000 concurrent users
  - Average user action per day = 10 requests
  - Total daily requests = 10M
  - Peak QPS = (10M / 86400) * 5 (peak factor) ≈ 580 QPS
  - Assume 50% reads, 50% writes
  - Read QPS: 290, Write QPS: 290
  
Database storage:
  - 100 bytes per user record × 1M = 100MB (user data)
  - 5KB per user activity × 10 actions/day × 30 days × 1M = 1.5TB/month (activity log)

Cache (Redis):
  - Cache 20% of most active users' data = 200MB
  - Cache TTL: 1 hour
  - Required cache size: 200MB + overhead = ~500MB Redis

App server:
  - Single instance handles ~100-200 QPS (Spring Boot, 4 CPU)
  - Need: 580 / 150 = ~4 instances
  - With redundancy (N+1): 5 instances
```

**Auto-scaling configuration (AWS):**
```
Launch template: t3.large (2 CPU, 8GB RAM)
Auto Scaling Group:
  Min: 2, Max: 20, Desired: 2
  Scale-out policy: CPU > 70% for 5 minutes → add 2 instances
  Scale-in policy: CPU < 30% for 10 minutes → remove 1 instance
  Cooldown: 300 seconds (avoid thrashing)
```

## 8. Advanced Details

- **C10K problem**: handling 10,000 concurrent connections. Solution: event-driven I/O (epoll, kqueue) instead of thread-per-connection. Netty, Spring WebFlux, Nginx handle this.
- **C10M problem**: handling 10,000,000 concurrent connections. Requires kernel bypass (DPDK), user-space networking.
- **Read-after-write consistency**: after user writes data, immediate read should see the new data. Problem with read replicas (replication lag). Fix: read-after-write from primary for that user's session.
- **Geographic redundancy**: active-active (multiple regions accepting traffic) vs active-passive (one region active, one standby for failover).
- **Rate limiting strategies**: Token Bucket (burst-friendly), Leaky Bucket (smooth), Sliding Window Log (precise but memory-heavy), Sliding Window Counter (approximate but efficient).
- **Backpressure in Kafka**: `max.poll.records`, consumer lag monitoring. When consumer falls behind, scale consumers or throttle producers.
- **Idempotency keys**: prevent duplicate processing on retry. Client generates unique idempotency key. Server deduplicates.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between vertical and horizontal scaling?
A: Vertical scaling = bigger machine (more CPU, RAM). Simple, code unchanged. Limited by hardware max. Cost super-linear. Horizontal scaling = more machines. Requires stateless app design. Theoretically unlimited. Cost linear. Modern cloud systems prefer horizontal because a single machine will always have an upper limit.

### Intermediate
Q: How does database sharding work? What is a good sharding key?
A: Sharding splits data across multiple databases. Each shard holds a subset. The sharding key determines which shard stores each record. A good sharding key: evenly distributes data (no hot shards), supports common query patterns (queries include the shard key), rarely changes (moving data across shards is expensive). Example: shard by `user_id % 16` for a user-centric application. Bad key: `country` (US users create hot shard).

### Senior
Q: Your read-heavy application serves 100K QPS. The primary database handles all reads + writes. You're at 80% CPU on the primary. How do you scale without downtime?
A: This is a read-scale problem:

1. **Add read replicas**: Create 3 read replicas. Configure application to route SELECT queries to replicas, INSERT/UPDATE/DELETE to primary. Most DB drivers (HikariCP + RoutingDataSource) support this. Reduces primary CPU from 80% to ~30%.
2. **Add cache**: Add Redis cache layer for hot data (cache-aside pattern). 90% cache hit rate → only 10K QPS hits replicas. Primary CPU drops further.
3. **Connection pooling**: Ensure each service uses connection pools (not one connection per request).
4. **Monitor replication lag**: If replicas lag > 100ms, users see stale data. Adjust replica count or use primary reads for time-sensitive queries.
5. **Result**: Primary handles 1K writes/sec. 3 replicas + cache handle 99K reads/sec. System can handle 10x growth before needing sharding.

### Tricky
Q: You're designing a URL shortener. Writes are low (100/sec) but reads are high (10K/sec). The database is the bottleneck. You add Redis cache (95% hit rate) → now 500 reads/sec hit DB. But occasionally, a popular URL expires from cache and 1000 concurrent requests all miss cache simultaneously, hammering the DB. How do you prevent this cache stampede?
A: Cache stampede solutions:

1. **Mutex lock per key**: Only the first request that misses cache acquires a distributed lock (Redis SETNX), loads from DB, sets cache. Other requests wait briefly then retry cache. Prevents DB overload.

2. **Early recomputation**: If TTL = 3600s and a request sees remaining TTL < 600s, proactively refresh the cache. This spreads cache updates over time.

3. **Prolonged TTL + async refresh**: Return stale data while an async thread refreshes the cache. Users always see a response (possibly slightly stale).

4. **Thundering herd prevention with locking**:
```java
String value = redis.get(key);
if (value == null) {
    // Try to acquire lock (SET NX EX 10)
    String lockKey = "lock:" + key;
    if (redis.setnx(lockKey, "locked", 10)) {
        // Only this thread loads from DB
        value = loadFromDB(key);
        redis.setex(key, 3600, value);
        redis.del(lockKey);
    } else {
        // Another thread is loading — wait and retry
        Thread.sleep(50);
        value = redis.get(key); // Should be available now
    }
}
```

## 10. Final 30-Second Answer

Scalability = handle more load. **Two types**: vertical (bigger machine, limited) vs horizontal (more machines, preferred). **Patterns**: stateless services (scale any instance), read replicas (scale reads), sharding (scale writes), caching (reduce load), CDN (static content), async processing (decouple), auto-scaling (match demand). **CAP**: Consistency vs Availability vs Partition Tolerance — pick 2. **Database path**: single DB → read replicas → +cache → sharding → +replicas. **Estimate**: 1M DAU ≈ 600 QPS, 4 app servers, 3 replicas. **Never**: stateful services, no cache, single DB, chatty APIs, no backpressure. Cache stampede: lock + early recomputation.