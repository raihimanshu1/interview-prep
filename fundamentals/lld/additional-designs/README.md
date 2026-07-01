# LLD: Rate Limiter, Logger, Cache Design, Payment State Machine

## 1. Rate Limiter LLD

**Problem:** Design a rate limiter that limits API requests per user/IP to N requests per time window.

**Token Bucket Algorithm (most common):**
```java
public class TokenBucketRateLimiter {
    private final long maxTokens;        // Max burst size
    private final long refillRate;       // Tokens added per refillInterval
    private final long refillInterval;   // Milliseconds between refills
    
    private long currentTokens;
    private long lastRefillTime;
    
    public TokenBucketRateLimiter(long maxTokens, long refillRate, long refillInterval) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.refillInterval = refillInterval;
        this.currentTokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }
    
    public synchronized boolean allow() {
        refill();
        if (currentTokens > 0) {
            currentTokens--;
            return true;
        }
        return false;
    }
    
    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        long tokensToAdd = (elapsed / refillInterval) * refillRate;
        if (tokensToAdd > 0) {
            currentTokens = Math.min(maxTokens, currentTokens + tokensToAdd);
            lastRefillTime = now;
        }
    }
}

// Usage: 10 requests per second, max burst of 20
RateLimiter limiter = new TokenBucketRateLimiter(20, 10, 1000);
if (limiter.allow()) {
    processRequest();
} else {
    return 429 Too Many Requests;
}
```

**Sliding Window (distributed, Redis):**
```java
public class SlidingWindowRateLimiter {
    private final RedisTemplate<String, String> redis;
    
    public boolean allow(String key, int maxRequests, long windowMs) {
        long now = System.currentTimeMillis();
        String redisKey = "ratelimit:" + key;
        
        // Remove timestamps outside window
        redis.opsForZSet().removeRangeByScore(redisKey, 0, now - windowMs);
        
        // Count requests in current window
        Long count = redis.opsForZSet().size(redisKey);
        if (count != null && count >= maxRequests) {
            return false;
        }
        
        // Add current request
        redis.opsForZSet().add(redisKey, String.valueOf(now), now);
        redis.expire(redisKey, windowMs, TimeUnit.MILLISECONDS);
        return true;
    }
}
```

**Class diagram:**
```
RateLimiter (interface)
├── TokenBucketRateLimiter (single-node, synchronized)
├── SlidingWindowRateLimiter (distributed, Redis + ZSet)
└── FixedWindowRateLimiter (simple, resets at window boundary)
```

## 2. Logger Design LLD

**Problem:** Design a logging framework that supports multiple appenders (console, file, DB), log levels, and formatting.

```java
// === ENUM ===
enum LogLevel { DEBUG, INFO, WARN, ERROR }

// === LOGGER (Singleton per class) ===
class Logger {
    private static final Map<String, Logger> instances = new ConcurrentHashMap<>();
    private final String name;
    private final LoggerConfig config = LoggerConfig.getInstance();
    
    private Logger(String name) { this.name = name; }
    public static Logger getLogger(String name) {
        return instances.computeIfAbsent(name, Logger::new);
    }
    
    public void info(String msg) { log(LogLevel.INFO, msg); }
    public void error(String msg) { log(LogLevel.ERROR, msg); }
    public void error(String msg, Throwable t) { log(LogLevel.ERROR, msg, t); }
    
    private void log(LogLevel level, String msg) {
        log(level, msg, null);
    }
    
    private void log(LogLevel level, String msg, Throwable t) {
        if (level.ordinal() < config.getMinLevel().ordinal()) return; // Level check
        
        LogMessage logMsg = new LogMessage(level, name, msg, t, System.currentTimeMillis());
        for (Appender appender : config.getAppenders()) {
            appender.append(logMsg);
        }
    }
}

// === LOG MESSAGE ===
record LogMessage(LogLevel level, String loggerName, String message, 
                  Throwable throwable, long timestamp) {}

// === APPENDER (Strategy pattern) ===
interface Appender {
    void append(LogMessage msg);
}

class ConsoleAppender implements Appender {
    public void append(LogMessage msg) {
        System.out.println(format(msg));
    }
    private String format(LogMessage msg) {
        return String.format("[%s] %s - %s", msg.level(), msg.loggerName(), msg.message());
    }
}

class FileAppender implements Appender {
    private final String filePath;
    private final long maxSize; // Rollover at this size
    private BufferedWriter writer;
    
    public FileAppender(String filePath, long maxSize) { /* init */ }
    public void append(LogMessage msg) { /* write to file, rollover if needed */ }
}

// === CONFIGURATION (Singleton) ===
class LoggerConfig {
    private static final LoggerConfig instance = new LoggerConfig();
    private LogLevel minLevel = LogLevel.DEBUG;
    private List<Appender> appenders = new CopyOnWriteArrayList<>();
    
    public static LoggerConfig getInstance() { return instance; }
    public void addAppender(Appender appender) { appenders.add(appender); }
    // getters/setters...
}

// === USAGE ===
public class MyService {
    private static final Logger log = Logger.getLogger(MyService.class.getName());
    
    public void doWork() {
        log.info("Starting work");
        try { /* ... */ } 
        catch (Exception e) { log.error("Failed", e); }
    }
}
```

## 3. Cache Design LLD

**Problem:** Design an in-memory cache with TTL, eviction policy (LRU), and thread safety.

```java
class CacheEntry<V> {
    final V value;
    final long expiryMs;
    final long createdAt = System.currentTimeMillis();
    
    CacheEntry(V value, long ttlMs) {
        this.value = value;
        this.expiryMs = ttlMs > 0 ? createdAt + ttlMs : Long.MAX_VALUE;
    }
    
    boolean isExpired() { return System.currentTimeMillis() > expiryMs; }
}

class LRUCache<K, V> {
    private final int capacity;
    private final long defaultTtlMs;
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final LinkedHashMap<K, Boolean> accessOrder = new LinkedHashMap<>(16, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<K, Boolean> eldest) {
            return size() > capacity;
        }
    };
    
    public LRUCache(int capacity, long defaultTtlMs) {
        this.capacity = capacity;
        this.defaultTtlMs = defaultTtlMs;
    }
    
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        synchronized (this) { accessOrder.get(key); } // Update access order
        return entry.value;
    }
    
    public void put(K key, V value) {
        put(key, value, defaultTtlMs);
    }
    
    public void put(K key, V value, long ttlMs) {
        synchronized (this) {
            if (accessOrder.size() >= capacity && !accessOrder.containsKey(key)) {
                K eldest = accessOrder.keySet().iterator().next();
                cache.remove(eldest);
                accessOrder.remove(eldest);
            }
            accessOrder.put(key, true);
        }
        cache.put(key, new CacheEntry<>(value, ttlMs));
    }
    
    public void evictExpired() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(e -> e.getValue().isExpired());
    }
}
```

## 4. Payment State Machine LLD

```java
// States
enum PaymentState { CREATED, AUTHORIZED, CAPTURED, REFUNDED, FAILED, EXPIRED }

// Events
enum PaymentEvent { AUTHORIZE, CAPTURE, REFUND, FAIL, EXPIRE, RETRY }

// Transition rules
class PaymentStateMachine {
    private static final Map<PaymentState, Map<PaymentEvent, PaymentState>> transitions = Map.of(
        PaymentState.CREATED, Map.of(
            PaymentEvent.AUTHORIZE, PaymentState.AUTHORIZED,
            PaymentEvent.FAIL, PaymentState.FAILED,
            PaymentEvent.EXPIRE, PaymentState.EXPIRED
        ),
        PaymentState.AUTHORIZED, Map.of(
            PaymentEvent.CAPTURE, PaymentState.CAPTURED,
            PaymentEvent.REFUND, PaymentState.REFUNDED,
            PaymentEvent.FAIL, PaymentState.FAILED,
            PaymentEvent.EXPIRE, PaymentState.EXPIRED
        ),
        PaymentState.CAPTURED, Map.of(
            PaymentEvent.REFUND, PaymentState.REFUNDED
        )
    );
    
    public static PaymentState next(PaymentState current, PaymentEvent event) {
        Map<PaymentEvent, PaymentState> allowed = transitions.get(current);
        if (allowed == null || !allowed.containsKey(event)) {
            throw new IllegalStateException(
                "Cannot transition from " + current + " via " + event);
        }
        return allowed.get(event);
    }
}

// Domain entity
class Payment {
    private Long id;
    private PaymentState state = PaymentState.CREATED;
    
    public void processEvent(PaymentEvent event) {
        this.state = PaymentStateMachine.next(this.state, event);
    }
}
```

## 5. Final 30-Second Answer

**Rate Limiter**: Token Bucket (single-node, synchronized) or Sliding Window (distributed, Redis ZSet). **Logger**: Singleton per logger name, Strategy pattern for Appenders (Console, File, DB), level filtering, LogMessage record. **Cache**: LRU via LinkedHashMap (access-order), ConcurrentHashMap for thread-safe storage, TTL with expiry check, periodic eviction. **Payment State Machine**: finite states + allowed transitions map, immutable transition rules, state field on entity.