# 🚦 Problem 59: API Rate Limiter (Redis-based)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any backend company  
> **Est. Time**: 90 min | **Patterns**: Token Bucket, Sliding Window, Redis

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a rate limiter for API endpoints."

**What the interviewer tests**:
```
1. Can you limit requests? (Per user, per IP)
2. Can you use different algorithms? (Token bucket, sliding window)
3. Can you use Redis? (Distributed, atomic)
4. Can you handle edge cases? (Clock skew, burst)
```

### Step 2: The "Aha!" Moment

The key insight: **Rate limiting = distributed counter with time window.**

```
ALGORITHMS:
1. Token Bucket: Fill tokens at rate, consume on request
   - Allows bursts
   - Average rate controlled
   
2. Sliding Window: Count requests in last N seconds
   - Strict rate limiting
   - No bursts
   
3. Fixed Window: Count in fixed time buckets
   - Simple but burst at boundaries
```

### Step 3: Implementation in Redis?

```
REDIS COMMANDS:
  INCR key: Atomic increment
  EXPIRE key seconds: Set TTL
  
SLIDING WINDOW:
  Key: rate_limit:user123:1609459200
  INCR → 1 (first request)
  EXPIRE 60
  
  Next request: INCR → 2
  ...
  At 61s: New key, rate resets
  
TOKEN BUCKET:
  Key: bucket:user123
  Tokens: current token count
  Refill: tokens += (now - last_refill) * rate
```

---

## 💻 Core Implementation

```java
package com.ratelimit;

import java.util.*;
import java.util.concurrent.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * INTUITION: RateLimiter uses Redis for distributed rate limiting.
 */
public class RateLimiter {
    
    private final Jedis redis;
    private final int defaultLimit;
    private final int windowSeconds;

    public RateLimiter(String redisHost, int port, int defaultLimit, int windowSeconds) {
        this.redis = new Jedis(redisHost, port);
        this.defaultLimit = defaultLimit;
        this.windowSeconds = windowSeconds;
    }

    /**
     * INTUITION: Check if request allowed.
     * 
     * Uses sliding window with Redis.
     */
    public boolean allowRequest(String key) {
        String redisKey = "rate_limit:" + key + ":" + getWindowStart();
        
        // Atomic increment
        long count = redis.incr(redisKey);
        
        // Set TTL on first request
        if (count == 1) {
            redis.expire(redisKey, windowSeconds);
        }
        
        return count <= defaultLimit;
    }

    /**
     * INTUITION: Get current window start timestamp.
     */
    private long getWindowStart() {
        long now = System.currentTimeMillis() / 1000;
        return (now / windowSeconds) * windowSeconds;
    }

    /**
     * INTUITION: Token bucket algorithm.
     * 
     * More forgiving than sliding window.
     */
    public boolean allowTokenBucket(String key, int bucketSize, double refillRate) {
        String redisKey = "bucket:" + key;
        
        // Get current tokens and last refill time
        List<String> values = redis.hmget(redisKey, "tokens", "last_refill");
        
        double tokens;
        long lastRefill;
        
        if (values.get(0) == null) {
            tokens = bucketSize;
            lastRefill = System.currentTimeMillis();
        } else {
            tokens = Double.parseDouble(values.get(0));
            lastRefill = Long.parseLong(values.get(1));
        }
        
        // Refill tokens
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - lastRefill) / 1000.0 * refillRate;
        tokens = Math.min(bucketSize, tokens + tokensToAdd);
        
        // Try to consume token
        if (tokens >= 1) {
            tokens -= 1;
            
            // Update Redis
            Map<String, String> data = new HashMap<>();
            data.put("tokens", String.valueOf(tokens));
            data.put("last_refill", String.valueOf(now));
            redis.hmset(redisKey, data);
            redis.expire(redisKey, 60);
            
            return true;
        }
        
        return false;
    }

    /**
     * INTUITION: Rate limit with user tier.
     * 
     * Different limits for free vs premium.
     */
    public boolean allowRequest(String userId, UserTier tier) {
        int limit = tier == UserTier.PREMIUM ? 1000 : 100;
        String key = "user:" + userId;
        
        return allowRequestWithLimit(key, limit);
    }

    private boolean allowRequestWithLimit(String key, int limit) {
        String redisKey = "rate_limit:" + key + ":" + getWindowStart();
        
        long count = redis.incr(redisKey);
        
        if (count == 1) {
            redis.expire(redisKey, windowSeconds);
        }
        
        return count <= limit;
    }

    /**
     * Get remaining requests for user.
     */
    public int getRemainingRequests(String key) {
        String redisKey = "rate_limit:" + key + ":" + getWindowStart();
        Long count = redis.incr(redisKey);
        
        return (int) Math.max(0, defaultLimit - count);
    }

    /**
     * Reset rate limit for user.
     */
    public void resetLimit(String key) {
        String redisKey = "rate_limit:" + key + ":" + getWindowStart();
        redis.del(redisKey);
    }

    public void close() {
        redis.close();
    }
}

enum UserTier {
    FREE, PREMIUM, ENTERPRISE
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle distributed systems?"
> "Use Redis for centralized counter. Atomic INCR. Multiple rate limiter instances."

### Q2: "How to handle clock skew?"
> "Use server time, not client. Redis server time. Synchronized NTP."

### Q3: "How to handle burst traffic?"
> "Token bucket allows bursts. Queue excess requests. Spike arrest."

### Q4: "How to prevent DDoS?"
> "IP-based rate limiting. Challenge-response (CAPTCHA). Auto-block suspicious IPs."