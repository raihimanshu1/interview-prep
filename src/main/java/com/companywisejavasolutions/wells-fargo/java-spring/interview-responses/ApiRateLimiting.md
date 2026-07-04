# API Rate Limiting - Low Level Design (LLD) Deep Dive

This is a very common Wells Fargo Java/Spring LLD question because every production API gateway needs traffic protection.

---

# Problem Statement

Design a rate limiter such that:

```text
User A -> max 100 requests/minute
User B -> max 100 requests/minute
101st request -> reject
```

Requirements:

```text
Fast
Scalable
Distributed
Accurate enough
Low memory
Observable
```

---

# Why Do We Need Rate Limiting?

Without rate limiting:

```text
Malicious user
Bot traffic
Infinite retries
DDoS-style spikes
Broken batch job
```

can overload:

```text
API gateway
Spring Boot service
Database
Payment processor
Downstream fraud/risk service
```

---

# High Level Flow

```text
Request
   |
Rate Limiter
   |
Allowed?
 /      \
YES      NO
 |        |
API      429 Too Many Requests
```

---

# Approach 1: Fixed Window Counter

Idea:

```text
Keep one counter per user per time window.
```

Data structure:

```java
class RateLimitEntry {
    int count;
    long windowStartTime;
}
```

Problem:

```text
10:00:59 -> 100 requests
10:01:00 -> 100 requests

User sends 200 requests in about 2 seconds.
```

Pros:

```text
Simple
Low memory
Fast
```

Cons:

```text
Boundary burst problem
Less accurate
```

---

# Approach 2: Sliding Window Log

Idea:

```text
Store timestamp of every request.
Remove timestamps older than currentTime - windowSize.
```

Data structure:

```java
Map<String, Queue<Long>> userToRequestTimes;
```

Algorithm:

```text
Remove expired timestamps
If queue size < limit -> allow and add timestamp
Else -> reject
```

Pros:

```text
Very accurate
Easy to explain
```

Cons:

```text
High memory usage
Bad for millions of users
```

---

# Approach 3: Sliding Window Counter

Idea:

```text
Store current window count and previous window count.
Estimate current load using weighted previous window count.
```

Example:

```text
Current count = 40
Previous count = 100
Window progress = 50%
Estimated count = 40 + (100 * 0.5) = 90
```

Pros:

```text
Low memory
Better than fixed window
Production friendly
```

Cons:

```text
Approximate, not exact
```

---

# Approach 4: Token Bucket

Most popular interview answer.

Idea:

```text
Bucket has tokens.
Every request consumes 1 token.
Tokens refill continuously.
If bucket is empty, reject.
```

Data structure:

```java
class TokenBucket {
    long capacity;
    long tokens;
    long refillRatePerSecond;
    long lastRefillTimeMillis;
}
```

Algorithm:

```text
Before every request:
1. Calculate elapsed time since last refill.
2. Add elapsed * refillRate tokens.
3. Cap tokens at bucket capacity.
4. If tokens > 0, consume one token and allow.
5. Else reject.
```

Why companies like it:

```text
Allows bursts
Smooths traffic
Low memory per user
Easy to tune by endpoint/client tier
```

---

# Approach 5: Leaky Bucket

Idea:

```text
Requests enter a queue quickly.
System processes them at a fixed rate.
Overflow gets rejected.
```

Best for:

```text
Traffic shaping
Smoothing spikes
Network/router style systems
```

---

# Distributed Rate Limiter

Single instance is easy.
Production has:

```text
Gateway 1
Gateway 2
Gateway 3
Gateway 4
```

Problem:

```text
Each gateway sees only part of the traffic.
Local counters allow users to exceed the global limit.
```

Solution:

```text
Use Redis as shared state.
Use atomic operations or Lua scripts.
```

Redis key example:

```text
rate:user:123:/transfers
```

Key design:

```text
rate:user:{userId}:{endpoint}
rate:ip:{ipAddress}:{endpoint}
rate:client:{clientId}:{apiTier}
```

This allows different limits for:

```text
read APIs
write APIs
trusted clients
public clients
internal batch jobs
```

Atomicity:

```text
Check tokens
Refill tokens
Consume token
Set TTL
Return allow/reject
```

This should happen atomically through a Lua script.

Lua-style command flow:

```text
GET bucket state
calculate refill based on current time
tokens = min(capacity, tokens + refill)
if tokens <= 0:
    save state with TTL
    return rejected
else:
    tokens = tokens - 1
    save state with TTL
    return allowed
```

---

# Java / Spring Classes

```java
public interface RateLimiter {
    boolean allowRequest(String key);
}

public class TokenBucketRateLimiter implements RateLimiter {
    private final Map<String, TokenBucket> buckets;

    public boolean allowRequest(String key) {
        TokenBucket bucket = buckets.computeIfAbsent(key, ignored -> new TokenBucket());
        return bucket.tryConsume();
    }
}

class TokenBucket {
    private long capacity;
    private long tokens;
    private long refillRatePerSecond;
    private long lastRefillTimeMillis;

    synchronized boolean tryConsume() {
        refill();
        if (tokens <= 0) {
            return false;
        }
        tokens--;
        return true;
    }
}
```

In a distributed Spring Boot service, this local map becomes Redis-backed state.

---

# Concurrency Problem

Bug:

```text
Thread 1 sees token = 1
Thread 2 sees token = 1
Both consume
Token becomes negative
```

Fix:

```text
synchronized
ReentrantLock
Atomic operations
Redis Lua script for distributed case
```

---

# Production Architecture

```text
Client
   |
Load Balancer
   |
API Gateway / Spring Cloud Gateway
   |
Rate Limiter Filter
   |
Redis
   |
Spring Boot Service
   |
Database / Downstream Services
```

---

# HTTP Behavior

Reject with:

```http
HTTP/1.1 429 Too Many Requests
Retry-After: 60
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
```

---

# Extra Details

Forward compatibility:

```text
New clients can understand new rate-limit headers while old clients still only rely on HTTP 429.
```

Backward compatibility:

```text
Do not suddenly change rate-limit policy for existing clients without rollout, communication, and telemetry.
```

Semantic versioning:

```text
MAJOR -> breaking API limit/contract behavior
MINOR -> add new headers or optional policy metadata
PATCH -> tune Redis script or fix counter bug
```

Big-company mindset:

```text
Large platforms roll out limits gradually, monitor client impact, allow tier-based policies, provide retry metadata, and keep emergency overrides.
```

Related patterns:

- Token Bucket
- Leaky Bucket
- Gateway Filter
- Strategy pattern for algorithm selection
- Circuit breaker and bulkhead for downstream protection

---

# Follow-Up Questions

### Why Redis?

```text
Fast
Shared across instances
Atomic operations
TTL support
```

### Why token bucket over fixed window?

```text
Allows bursts
Avoids sharp boundary problem
Low memory
Good production trade-off
```

### Where should rate limiting happen?

```text
Gateway for broad protection
Service layer for business-specific limits
Both for high-risk financial APIs
```

---

# Senior-Level Interview Answer

I would use a token bucket rate limiter for most production APIs because it is fast, low memory, and supports controlled bursts. Each user, client, IP, or endpoint gets a bucket with a capacity and refill rate. Every request consumes one token, and requests are rejected with HTTP 429 when no tokens are available. In a distributed Spring Boot or gateway deployment, I would store bucket state in Redis and use a Lua script so refill, check, consume, and TTL update happen atomically. I would also return Retry-After and rate-limit headers, monitor rejected traffic, support endpoint-specific policies, and roll out policy changes gradually so existing clients are not broken unexpectedly.
