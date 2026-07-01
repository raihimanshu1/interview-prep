# Production Scenarios — Debugging, High Traffic, Memory Leaks, Thread Dumps, Cache Issues

## 1. Why This Concept Matters

Production debugging separates senior engineers from juniors. When the system goes down at 3 AM, you need a systematic approach: gather data (metrics, logs, thread dumps, heap dumps), form hypotheses, test, and fix. These scenarios test your ability to diagnose real problems under pressure — not just write code that works when everything is fine.

## 2. High CPU in Production

**Symptoms:** Slower response times, alerts from CloudWatch/Datadog, users complaining.

**Diagnosis steps:**
```bash
# 1. Find the PID of the problematic Java process
top -H                      # Find high CPU threads
ps aux | grep java          # Or find by process name

# 2. Get thread dump from the Java process
jstack <pid> > threaddump.txt

# 3. Convert high CPU thread ID to hex (for matching jstack output)
printf "%x\n" <thread-id>   # Example: 12345 → 0x3039

# 4. Search the thread dump for that thread
grep -A 20 "0x3039" threaddump.txt   # Shows the stack trace

# Alternative: Use async-profiler for CPU flame graph
# ./profiler.sh -d 30 -f cpu.html <pid>  # 30-second CPU profile
```

**Common causes:**
- **Infinite loop** (`while(true)` without condition)
- **Excessive GC** (GC thread using CPU trying to free memory)
- **Regular expression backtracking** (Catastrophic backtracking on complex regex)
- **Unoptimized hot loop** (string concatenation in loop, boxing in loop)
- **Garbage collection thrashing** (object creation rate > GC throughput)

**Fix:** Thread dump shows the exact line of code causing the loop. Fix the algorithm, optimize regex, or reduce object creation.

## 3. Memory Leak / OutOfMemoryError

**Symptoms:** Application crashes with `OutOfMemoryError`, heap usage grows over time and never recovers.

**Diagnosis steps:**
```bash
# 1. Enable heap dump on OOM (do this BEFORE the problem)
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/app/heap.hprof

# 2. Monitor heap usage over time
jstat -gcutil <pid> 5s     # Print GC stats every 5 seconds
  S0  S1  E   O   M   YGC  YGCT  FGC  FGCT
  0   0   50  95  85  500  12s   10   30s
  # Old gen (O) at 95% — indicates leak or insufficient heap

# 3. Analyze heap dump with Eclipse MAT or jvisualvm
# jmap -dump:live,format=b,file=heap.hprof <pid>

# 4. In MAT: "Leak Suspects Report" → shows what's retaining memory
```

**Common causes:**
- **ThreadLocal not cleaned up in thread pool** (thread lives forever, ThreadLocal value is held)
- **HashMap key without equals/hashCode** (same "key" added repeatedly — never found, never removed)
- **Classloader leak** (redeploying webapp without proper cleanup)
- **Cached data without eviction** (in-memory cache grows unbounded)
- **JDBC connection leak** (Connection created but not closed — pool exhaustion)

```java
// Leak example: ThreadLocal not removed
public class RequestContextHolder {
    private static final ThreadLocal<RequestContext> ctx = new ThreadLocal<>();
    
    public static void setContext(RequestContext c) { ctx.set(c); }
    public static RequestContext getContext() { return ctx.get(); }
    // Missing: public static void clear() { ctx.remove(); }
    // When thread returns to pool, old context stays!
}

// Fix: Always remove in finally
try {
    RequestContextHolder.setContext(userCtx);
    processRequest();
} finally {
    RequestContextHolder.clear();
}
```

## 4. Thread Pool Exhaustion

**Symptoms:** Requests hang, `RejectedExecutionException`, connection pool errors, "Thread is not available" messages.

**Root cause:** All threads in the pool are busy waiting for external resources (database queries, HTTP calls, locks). No threads available for new requests.

```bash
# 1. Thread dump shows many threads in WAITING/BLOCKED state
grep -c "java.lang.Thread.State: WAITING" threaddump.txt
grep -c "java.lang.Thread.State: BLOCKED" threaddump.txt
# If WAITING > pool size, threads are waiting on something

# 2. Check what they're waiting on
grep -A 3 "WAITING\|BLOCKED" threaddump.txt | head -100
```

**Fixes (in order):**
1. **Add timeouts** — `@Transactional(timeout = 5)`, `RestTemplate.setConnectTimeout(5000)`
2. **Add circuit breaker** — fail fast instead of waiting forever
3. **Increase pool size** — temporary fix, not root cause
4. **Async processing** — move long operations to queue (Kafka)
5. **Bulkhead** — isolate different downstream calls into separate pools

## 5. GC Pause Spikes

**Symptoms:** Latency spikes every few seconds/minutes, p99 latency 10x normal, applications temporarily unresponsive.

**Diagnosis:**
```bash
# Enable GC logging
-Xlog:gc*:file=gc.log:time,uptime:filecount=10,filesize=10M

# Analyze with GCeasy or gceasy.io (upload gc.log)
# Look for:
# - Full GC events (should be 0 in production)
# - Long pause times (> 200ms for G1)
# - Increasing heap after GC (not returning to baseline)
```

**Fixes:**
- **G1 tuning**: `-XX:MaxGCPauseMillis=100`, `-XX:G1HeapRegionSize=4m`
- **Reduce object allocation** — fewer objects = less GC work
- **Increase heap** — `-Xmx4g` to give GC more room
- **Switch GC** — ZGC for <10ms pauses with large heaps
- **Reduce thread count** — fewer threads = fewer thread-local GC roots

## 6. Slow Database Queries

**Symptoms:** API calls timing out, database CPU high, slow response times.

**Diagnosis:**
```sql
-- PostgreSQL: Find currently running queries
SELECT pid, now() - pg_stat_activity.query_start AS duration, query, state
FROM pg_stat_activity
WHERE state = 'active'
ORDER BY duration DESC;

-- MySQL: SHOW PROCESSLIST;
SHOW FULL PROCESSLIST;

-- Find slow queries (pg_stat_statements extension)
SELECT query, total_time / calls AS avg_time, calls
FROM pg_stat_statements
ORDER BY total_time DESC
LIMIT 10;

-- Check for missing indexes
SELECT schemaname, tablename, seq_scan, seq_tup_read, idx_scan
FROM pg_stat_user_tables
WHERE seq_scan > 1000 AND idx_scan = 0;
-- Sequential scans without index = missing index!

-- Blocked queries (deadlocks)
SELECT blocked_locks.pid AS blocked_pid, blocking_locks.pid AS blocking_pid
FROM pg_locks blocked_locks
JOIN pg_locks blocking_locks ON blocking_locks.locktype = blocked_locks.locktype;
```

**Fixes:**
- **Add missing index** (most common fix — check WHERE clauses)
- **Optimize query** (avoid SELECT *, rewrite JOINs)
- **Add caching** (Redis for frequent same results)
- **Add read replicas** (offload reads from primary)
- **Batch operations** (10 INSERTs in one batch vs 10 individual)

## 7. Cache Inconsistency

**Symptoms:** Users see stale data, data appears and disappears between refreshes.

**Root causes:**
- **Stale cache** (TTL too long — cache not refreshed when data changes)
- **Cache-aside race** (Thread A reads cache miss, Thread B writes DB, Thread A writes stale value to cache)
- **Distributed cache** (one node updated, another not)
- **Partial updates** (multiple cache entries for same entity, only some updated)

```java
// Cache-aside race condition fix:
public User getUser(Long id) {
    String key = "user:" + id;
    User cached = redis.get(key);
    if (cached != null) return cached;
    
    // Use distributed lock to prevent cache stampede
    String lockKey = "lock:" + key;
    if (redis.setnx(lockKey, "1", 10)) {  // Lock for 10s
        try {
            User user = userRepo.findById(id);
            redis.setex(key, 3600, user);  // TTL 1 hour
            return user;
        } finally {
            redis.del(lockKey);
        }
    } else {
        Thread.sleep(50);
        return getUser(id); // Retry
    }
}

// Write-through cache (always consistent):
public User updateUser(Long id, User updated) {
    User saved = userRepo.save(updated);
    redis.setex("user:" + id, 3600, saved);  // Update cache IMMEDIATELY
    return saved;
}
```

## 8. Queue Backlog

**Symptoms:** Consumer lag growing (Kafka lag, SQS queue depth increasing), messages processed slowly.

**Diagnosis:**
```bash
# Kafka consumer lag
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group payment-processor --describe

# Output: LAG column growing = consumers falling behind
```

**Fixes:**
1. **Add more consumers** (if partitions allow — you need partitions ≥ consumers)
2. **Optimize processing** (batch messages, reduce per-message work)
3. **Add partitions** (if topic is under-partitioned)
4. **Parallelize** (within each consumer, use thread pool to process messages)
5. **Drop non-critical messages** (graceful degradation — process payments, skip analytics)

## 9. Final 30-Second Answer

**High CPU**: `top → jstack → find thread → fix loop/regex/GC`. **OOM**: `-XX:+HeapDumpOnOOM → Eclipse MAT Leak Suspects → fix leak`. **Thread exhaustion**: `jstack → find blocked threads → add timeouts + circuit breaker`. **Slow DB**: `pg_stat_activity → find slow query → EXPLAIN ANALYZE → add index`. **Cache inconsistency**: fix race with lock, use write-through pattern, set appropriate TTL. **Queue backlog**: add consumers, optimize processing, batch messages. Never: skip GC logging, ignore thread dumps, fix cache without locking.