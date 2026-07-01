# 🏊 Problem 53: Object Pool (Generic Object Reuse)

> **Difficulty**: ⭐⭐ | **Company Fit**: Any backend company  
> **Est. Time**: 60 min | **Patterns**: Object Pooling, Factory, Strategy

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a generic object pool for reusing expensive objects."

**What the interviewer tests**:
```
1. Can you pool expensive objects? (DB connections, threads, memory)
2. Can you handle pool exhaustion? (Wait or create)
3. Can you validate objects? (Health check)
4. Can you make it generic? (Works for any type)
```

### Step 2: The "Aha!" Moment

The key insight: **Object creation is expensive, pool and reuse.**

```
EXPENSIVE OBJECTS:
  - Database connections
  - Thread objects
  - Large memory buffers
  - Network sockets
  
POOLING BENEFIT:
  Create once → reuse many times → destroy when pool closes
  
Example:
  Create buffer: 100ms
  Get from pool: 1ms
  Return to pool: 0.1ms
  
  1000 uses:
  Without pool: 100ms × 1000 = 100 seconds
  With pool: 1ms × 1000 = 1 second
  100x faster!
```

### Step 3: How to implement generically?

```
GENERIC POOL<T>:
  - Works for any object type T
  - Factory to create objects
  - Validator to check object health
  - Configurable min/max size
  
USAGE:
  Pool<Connection> connPool = new Pool<>(
    () -> DriverManager.getConnection(url),
    conn -> conn.isValid(2),
    10, 20
  );
  
  Connection conn = connPool.acquire();
  try {
    // use connection
  } finally {
    connPool.release(conn);
  }
```

---

## 💻 Core Implementation

```java
package com.pool;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: ObjectPool is a generic pool for any type T.
 * 
 * Reuses objects to avoid creation overhead.
 */
public class ObjectPool<T> {
    
    private final ObjectFactory<T> factory;
    private final ObjectValidator<T> validator;
    private final int minSize;
    private final int maxSize;
    private final long maxWaitTime;
    
    // Available objects
    private final BlockingQueue<PooledObject<T>> available;
    
    // In-use objects
    private final Set<PooledObject<T>> inUse;
    
    private volatile boolean isRunning;
    private final AtomicInteger objectCounter;

    public ObjectPool(ObjectFactory<T> factory, ObjectValidator<T> validator,
                     int minSize, int maxSize, long maxWaitTime) {
        this.factory = factory;
        this.validator = validator;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.maxWaitTime = maxWaitTime;
        this.available = new LinkedBlockingQueue<>(maxSize);
        this.inUse = ConcurrentHashMap.newKeySet();
        this.isRunning = true;
        this.objectCounter = new AtomicInteger(0);
        
        // Initialize pool
        initializePool();
    }

    /**
     * INTUITION: Acquire object from pool.
     * 
     * 1. Get available object
     * 2. Validate it
     * 3. Mark as in-use
     * 4. If invalid, discard and retry
     */
    public T acquire() throws InterruptedException, PoolExhaustedException {
        if (!isRunning) {
            throw new IllegalStateException("Pool is shutdown");
        }
        
        PooledObject<T> pooledObj = null;
        long startTime = System.currentTimeMillis();
        
        while (pooledObj == null) {
            // Try to get available object
            pooledObj = available.poll();
            
            if (pooledObj == null) {
                // Try to create new if under limit
                if (getTotalObjects() < maxSize) {
                    pooledObj = createObject();
                } else {
                    // Wait for available object
                    long waitTime = maxWaitTime - (System.currentTimeMillis() - startTime);
                    if (waitTime <= 0) {
                        throw new PoolExhaustedException("Pool exhausted");
                    }
                    
                    pooledObj = available.poll(waitTime, TimeUnit.MILLISECONDS);
                    
                    if (pooledObj == null) {
                        throw new PoolExhaustedException("Timeout waiting for object");
                    }
                }
            }
            
            // Validate object
            if (pooledObj != null && !validator.isValid(pooledObj.getObject())) {
                destroyObject(pooledObj);
                pooledObj = null;
            }
        }
        
        inUse.add(pooledObj);
        return pooledObj.getObject();
    }

    /**
     * INTUITION: Release object back to pool.
     */
    public void release(T object) {
        if (object == null) return;
        
        // Find pooled object
        PooledObject<T> pooledObj = null;
        for (PooledObject<T> po : inUse) {
            if (po.getObject().equals(object)) {
                pooledObj = po;
                break;
            }
        }
        
        if (pooledObj != null) {
            inUse.remove(pooledObj);
            
            if (isRunning && validator.isValid(object)) {
                available.offer(pooledObj);
            } else {
                destroyObject(pooledObj);
            }
        }
    }

    /**
     * Create new object.
     */
    private PooledObject<T> createObject() {
        T obj = factory.create();
        int id = objectCounter.incrementAndGet();
        return new PooledObject<>(id, obj);
    }

    /**
     * Destroy object.
     */
    private void destroyObject(PooledObject<T> pooledObj) {
        try {
            factory.destroy(pooledObj.getObject());
        } catch (Exception e) {
            System.err.println("Error destroying object: " + e.getMessage());
        }
    }

    /**
     * Initialize pool with minimum objects.
     */
    private void initializePool() {
        for (int i = 0; i < minSize; i++) {
            try {
                PooledObject<T> obj = createObject();
                available.offer(obj);
            } catch (Exception e) {
                System.err.println("Failed to initialize pool: " + e.getMessage());
            }
        }
    }

    /**
     * Get total objects (available + in-use).
     */
    private int getTotalObjects() {
        return available.size() + inUse.size();
    }

    /**
     * Shutdown pool.
     */
    public void shutdown() {
        isRunning = false;
        
        // Destroy available objects
        for (PooledObject<T> obj : available) {
            destroyObject(obj);
        }
        available.clear();
        
        // Destroy in-use objects
        for (PooledObject<T> obj : inUse) {
            destroyObject(obj);
        }
        inUse.clear();
    }

    // --- Getters ---

    public int getAvailableCount() { return available.size(); }
    public int getInUseCount() { return inUse.size(); }
    public int getMinSize() { return minSize; }
    public int getMaxSize() { return maxSize; }
}

/**
 * Pooled object wrapper.
 */
class PooledObject<T> {
    private final int id;
    private final T object;
    private long lastUsed;

    PooledObject(int id, T object) {
        this.id = id;
        this.object = object;
        this.lastUsed = System.currentTimeMillis();
    }

    T getObject() { return object; }
    long getLastUsed() { return lastUsed; }
    void setLastUsed(long lastUsed) { this.lastUsed = lastUsed; }
}

/**
 * Factory for creating objects.
 */
interface ObjectFactory<T> {
    T create();
    void destroy(T object);
}

/**
 * Validator for checking object health.
 */
interface ObjectValidator<T> {
    boolean isValid(T object);
}

class PoolExhaustedException extends Exception {
    public PoolExhaustedException(String message) {
        super(message);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle stale objects?"
> "Validate on acquire and release. Remove if invalid. Create replacement."

### Q2: "How to handle object lifecycle?"
> "Factory pattern: create, initialize, validate, destroy."

### Q3: "How to size the pool?"
> "Based on workload: min = average concurrency, max = peak concurrency."

### Q4: "How to monitor pool usage?"
> "Metrics: available, in-use, wait time, creation rate, destruction rate."