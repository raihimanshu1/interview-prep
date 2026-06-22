# Redis — Complete Deep Dive

## 1. Why This Concept Matters

Redis is the most popular in-memory data structure store, used for caching, session storage, rate limiting, message queues, and real-time analytics. Understanding Redis data types, eviction policies, persistence, and clustering is critical for building scalable, low-latency systems. Interviewers test this across all levels — basic operations, data structures, cache strategies (cache-aside, write-through), and Sentinel/Cluster architecture.

Misunderstanding Redis causes:
- Key eviction under memory pressure (wrong policy)
- Data loss with RDB/AOF configuration
- Cache stampede (expired key + high concurrency)
- Using Redis as primary database (persistence tradeoffs)

## 2. Basic Meaning

Redis = Remote Dictionary Server. In-memory key-value store with optional persistence, replication, and clustering.

**Key vocabulary:**
- **Key-value store**: String → any data type
- **Data types**: String, List, Set, Sorted Set (ZSet), Hash, Bitmap, HyperLogLog, Stream, Geospatial
- **TTL**: Time-to-live (auto-expire)
- **Eviction policies**: noeviction, allkeys-lru, volatile-lru, allkeys-lfu, volatile-lfu, allkeys-random, volatile-random, volatile-ttl
- **Persistence**: RDB (snapshot), AOF (append-only file), none
- **Pub/Sub**: publish/subscribe messaging
- **Transactions**: MULTI/EXEC (optimistic, no rollback)
- **Lua scripting**: atomic server-side scripts
- **Sentinel**: high availability (automatic failover)
- **Cluster**: sharding (16384 hash slots), no centralized coordinator

What it is NOT: Not a relational database. Not strongly consistent across replicas. Not suitable for storing data > RAM (without Redis on Flash). Not a general-purpose message queue (use Kafka/Pulsar).

## 3. Real Code / Real Example

```java
import redis.clients.jedis.*;
import java.util.*;

public class RedisDemo {
    public static void main(String[] args) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            // === STRING (caching) ===
            jedis.set("user:100:profile", "{\"name\":\"Alice\"}");
            String profile = jedis.get("user:100:profile");
            System.out.println("Profile: " + profile);
            
            jedis.setex("session:token-abc", 3600, "user-100"); // TTL: 1 hour
            long ttl = jedis.ttl("session:token-abc"); // remaining seconds
            System.out.println("Session TTL: " + ttl);

            // === LIST (message queue) ===
            jedis.lpush("queue:notifications", "email-1", "email-2");
            String msg = jedis.rpop("queue:notifications");
            System.out.println("Dequeued: " + msg);

            // === SET (unique items) ===
            jedis.sadd("user:100:followers", "user-200", "user-300");
            Set<String> followers = jedis.smembers("user:100:followers");
            System.out.println("Followers: " + followers);

            // === SORTED SET (leaderboard/ranking) ===
            jedis.zadd("leaderboard:scores", 1500, "player-1");
            jedis.zadd("leaderboard:scores", 2000, "player-2");
            jedis.zadd("leaderboard:scores", 1800, "player-3");
            Set<String> topPlayers = jedis.zrevrange("leaderboard:scores", 0, 2);
            System.out.println("Top 3: " + topPlayers);
            long rank = jedis.zrevrank("leaderboard:scores", "player-1");
            System.out.println("Player-1 rank: " + (rank + 1));

            // === HASH (object storage) ===
            jedis.hset("user:101", "name", "Bob");
            jedis.hset("user:101", "email", "bob@example.com");
            Map<String, String> user = jedis.hgetAll("user:101");
            System.out.println("User: " + user);

            // === CACHE-ASIDE PATTERN ===
            String cacheKey = "product:42";
            String cached = jedis.get(cacheKey);
            if (cached == null) {
                // MISS — load from DB
                String dbValue = loadFromDatabase(42);
                jedis.setex(cacheKey, 300, dbValue); // cache for 5 min
                System.out.println("Cache MISS, loaded from DB");
            } else {
                System.out.println("Cache HIT: " + cached);
            }

            // === ATOMIC INCREMENT (rate limiter) ===
            String bucketKey = "ratelimit:user:100:api";
            long current = jedis.incr(bucketKey);
            if (current == 1) jedis.expire(bucketKey, 60); // TTL on first set
            System.out.println("Rate limit count: " + current + " (max 100/min)");
        }
    }
    
    private static String loadFromDatabase(int id) { return "product-data-" + id; }
}
```

Expected output (simplified):
```
Profile: {"name":"Alice"}
Session TTL: 3599
Dequeued: email-1
Followers: [user-200, user-300]
Top 3: [player-2, player-3, player-1]
Player-1 rank: 3
User: {name=Bob, email=bob@example.com}
Cache MISS, loaded from DB
Rate limit count: 1 (max 100/min)
```

## 4. What Happens Internally

**Data structure internals:**
- String: SDS (Simple Dynamic String) — binary safe, length-prefixed
- List: quicklist (linked list of ziplists) — memory efficient
- Set: dict (hash table) or intset (sorted array for integers)
- Sorted Set: skip list + hash table — O(log N) for both insert and range
- Hash: dict or ziplist (small objects optimized)

**Event loop (single-threaded):**
- Redis uses a single-threaded event loop (epoll/kqueue)
- All commands are serialized — no race conditions on single key
- Lua scripts also execute atomically
- I/O threads in Redis 6+ for read/write (not command execution)

**Persistence:**
- RDB (snapshot): fork() child process, writes dump.rdb. Point-in-time, compact.
- AOF (append-only file): logs every write. Rewrite to compact. More durable (every sec or every write).
- Mixed mode (Redis 5+): RDB for initial snapshot + AOF for incremental writes.

**Cluster architecture:**
- 16384 hash slots distributed across nodes
- Client: `CLUSTER SLOTS` → knows which node for each slot
- MOVED redirect (client to correct node)
- ASK redirect (slot migration in progress)

## 5. Tricky Interview Cases

**Case 1 — Multi-key operations in cluster**
```java
// CLUSTER: keys may be on different nodes
MSET key1 val1 key2 val2 // MOVED redirect (unless hash tags used)
MSET {user:100}:name Alice {user:100}:age 30 // hash tag => same slot!
```
Output: Without hash tags `{}`, multi-key operations fail in cluster.

**Case 2 — DEL large key blocks Redis**
```java
// BAD: DEL a key with millions of elements — blocks event loop!
DEL huge:set // takes seconds — all other requests delayed
// GOOD: UNLINK — async delete in background
UNLINK huge:set // returns immediately, deletes in bg thread
```

**Case 3 — WATCH + MULTI for optimistic locking**
```java
jedis.watch("account:100");
int balance = Integer.parseInt(jedis.get("account:100"));
Transaction t = jedis.multi();
t.set("account:100", String.valueOf(balance + 100));
List<Object> result = t.exec(); // null if key changed during WATCH
```
Output: If `account:100` modified by another client, `exec()` returns null.

**Case 4 — Cache stampede**
```
Time X: Key expires (TTL=0)
Time X+1: 100 concurrent requests → all miss cache → all hit DB
DB: 100x load
```
**Fix**: Use `SET key value NX EX 60` (only first request sets cache, others wait). Or use per-key mutex lock.

**Case 5 — HyperLogLog for approximate counts**
```java
jedis.pfadd("unique:visitors:today", "user-1", "user-2", "user-3");
long count = jedis.pfcount("unique:visitors:today"); // approximate count
System.out.println("Unique visitors (approx): " + count);
```
Output: ~3 (approximate, 0.81% error rate). Uses 12KB per key regardless of cardinality.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| No eviction policy | OOM crash | Set `maxmemory-policy allkeys-lru` |
| Using KEYS in production | Blocks event loop (O(N)) | Use SCAN (cursor-based iteration) |
| Large values > 10MB | Network bottlenecks, replication lag | Split into smaller chunks |
| Cache without TTL | Memory leak | Always set `EXPIRE` or `SETEX` |
| DEL on large collection | Blocks event loop | Use `UNLINK` |
| Not using connection pool | High latency per request | Use JedisPool |

## 7. Production Usage

**Cache-aside (lazy loading):**
```java
private String getUserProfile(String userId) {
    String key = "user:" + userId;
    String cached = jedis.get(key);
    if (cached != null) return cached;
    
    String dbValue = database.getUserProfile(userId);
    if (dbValue != null) {
        jedis.setex(key, 3600, dbValue); // cache for 1 hour
    }
    return dbValue;
}
```

**Write-through cache:**
```java
public void updateUserProfile(String userId, String profile) {
    String key = "user:" + userId;
    database.updateProfile(userId, profile); // Write to DB first
    jedis.setex(key, 3600, profile); // Then update cache
}
```

**Session store with TTL:**
```java
public void createSession(String sessionId, String userId) {
    jedis.setex("session:" + sessionId, 86400, userId); // 24-hour session
}
```

**Rate limiter with sliding window:**
```java
public boolean isAllowed(String userId, int maxRequests, int windowSec) {
    String key = "ratelimit:" + userId + ":" + (System.currentTimeMillis() / 1000 / windowSec);
    long count = jedis.incr(key);
    if (count == 1) jedis.expire(key, windowSec * 2); // some buffer
    return count <= maxRequests;
}
```

## 8. Advanced Details

- **Redis Stack**: Redis + JSON, Search, TimeSeries, BloomFilter modules.
- **Redlock**: Distributed lock algorithm (controversial, prefer ZK/etcd).
- **Redis Streams**: Log-based data structure. Consumer groups, offsets, pending entries.
- **Lua scripting**: `EVAL "return redis.call('SET', KEYS[1], ARGV[1])" 1 key value` — atomic execution.
- **`RESTORE` / `DUMP`**: Serialize/deserialize keys for migration.
- **`CONFIG SET`**: Runtime configuration changes (no restart).
- **`SLOWLOG`**: Command execution time logging.

## 9. Interview Questions And Answers

### Beginner
Q: What is Redis? What data types does it support?
A: Redis is an in-memory key-value store. Data types: String, List, Set, Sorted Set (ZSet), Hash, Bitmap, HyperLogLog, Stream, Geospatial. All operations are atomic.

### Intermediate
Q: Explain Redis eviction policies. Which one would you use for a caching use case?
A: `allkeys-lru`: evict least recently used key (any key). `volatile-lru`: evict LRU key with TTL. `allkeys-lfu`: evict least frequently used. For caching: `allkeys-lru` or `allkeys-lfu` depending on access pattern. For session storage: `volatile-ttl` (evict soonest expiring).

### Senior
Q: How does Redis achieve high performance with single-threaded command execution?
A: 1. In-memory operations (no disk I/O during execution). 2. Event loop (epoll/kqueue) handles thousands of connections efficiently. 3. Simple data structures (hash tables, skip lists) with O(1)/O(log N) operations. 4. Asynchronous persistence (fork+RDB, AOF write to OS buffer). 5. Reading/writing from RAM is orders of magnitude faster than disk.

### Tricky
Q: You have a Redis cluster with 6 nodes. A key is written to node A, but client reads from node B and sees old data. Why? How do you handle this?
A: Redis Cluster is eventually consistent. Write acknowledged by primary, asynchronously replicated to replicas. If client reads from read-replica before replication completes → stale data.

Solutions:
1. Read from primary (waive read scaling). 2. Use `WAIT` command (acknowledge N replicas before returning). 3. Accept eventual consistency if acceptable for use case (most caches). 4. Use Redis Sentinel (single primary, all reads from primary).

## 10. Final 30-Second Answer

Redis = in-memory data store. **Data types**: String, List, Set, ZSet, Hash, Bitmap, HyperLogLog, Stream. **Single-threaded** event loop → atomic operations, no race conditions. **Persistence**: RDB (snapshot), AOF (log every write), none. **Eviction**: LRU, LFU, TTL, random. **Cluster**: 16384 slots, no centralized coordinator, MOVED/ASK redirect. **Cache patterns**: cache-aside (lazy), write-through (write DB + cache). **Tricks**: UNLINK for large deletes, SCAN not KEYS, hash tags for multi-key operations in cluster, WATCH for optimistic locking.