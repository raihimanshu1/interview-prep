# 🚀 Problem 27: Distributed Cache (Like Redis Cluster)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any infra company  
> **Est. Time**: 120 min | **Patterns**: Consistent Hashing, Replication, Eviction

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a cache that spans multiple servers."

**What the interviewer tests**:
```
1. Can you distribute data across nodes? (Sharding)
2. Can you handle node failures? (Replication)
3. Can you handle adding/removing nodes? (Resharding with minimal data movement)
4. Can you evict old data? (LRU, LFU, TTL)
```

### Step 2: The "Aha!" Moment

The key insight: **Consistent Hashing.**

```
Naive Hashing (Modulo):
  Nodes: N1, N2, N3
  Key → hash(key) % 3 → 0, 1, or 2
  
  Problem: Add N4 → ALL keys remap! 
  hash("user:123") was → N1, now → different node.
  Cache miss storm.

Consistent Hashing:
  Imagine nodes placed on a ring (0 to 2^160).
  Keys also hashed onto the ring.
  Each key goes to NEXT node clockwise.
  
  Add N4: Only keys between N3→N4 move.
  Most keys stay on their original nodes.
  
  Virtual nodes: Each physical node has 100 virtual nodes on ring.
  This ensures even distribution.
```

### Step 3: How to handle failures?

```
REPLICATION:
  Each key is stored on N nodes (N=3 typical).
  
  Read: Try primary node → if miss, try replica.
  Write: Write to primary → async replicate to replicas.
  
  If primary fails:
    1. Detect via heartbeat (fail after 30s no response)
    2. Promote a replica to primary
    3. Redirect requests to new primary
```

---

## 💻 Core Implementation

```java
package com.cache;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: DistributedCache is the client-facing API.
 * 
 * It knows about:
 * - Consistent hashing ring
 * - Node locations
 * - Primary/replica assignment
 * 
 * Client calls cache.get(key):
 *   1. Hash key to find node
 *   2. Try primary node
 *   3. If miss, try replicas
 *   4. Return value or null
 */
public class DistributedCache {
    
    // Consistent hash ring
    private final ConsistentHashRing hashRing;
    
    // All cache nodes
    private final Map<String, CacheNode> nodes;
    
    // Replication factor (how many copies)
    private static final int REPLICATION_FACTOR = 3;
    
    // Local in-memory cache (L1 cache)
    private final Map<String, String> localCache = new ConcurrentHashMap<>();

    public DistributedCache(List<String> nodeAddresses) {
        this.nodes = new ConcurrentHashMap<>();
        this.hashRing = new ConsistentHashRing();
        
        // Add all nodes to ring
        for (String address : nodeAddresses) {
            addNode(address);
        }
    }

    /**
     * INTUITION: GET with caching.
     * 
     * 1. Check local L1 cache first (fastest)
     * 2. If miss, hash key to find responsible node
     * 3. Try primary, then replicas
     * 4. Update local cache on hit
     */
    public String get(String key) {
        // Step 1: Check local cache
        String value = localCache.get(key);
        if (value != null) {
            return value;
        }
        
        // Step 2: Find responsible nodes
        List<String> nodeAddresses = hashRing.getNodes(key, REPLICATION_FACTOR);
        
        // Step 3: Try each node
        for (String nodeAddr : nodeAddresses) {
            CacheNode node = nodes.get(nodeAddr);
            if (node == null || !node.isAlive()) continue;
            
            value = node.get(key);
            if (value != null) {
                // Cache locally for faster future access
                localCache.put(key, value);
                return value;
            }
        }
        
        return null;  // Cache miss
    }

    /**
     * INTUITION: PUT with replication.
     * 
     * 1. Find responsible nodes
     * 2. Write to primary
     * 3. Async replicate to replicas
     */
    public void put(String key, String value, int ttlSeconds) {
        // Find nodes responsible for this key
        List<String> nodeAddresses = hashRing.getNodes(key, REPLICATION_FACTOR);
        
        if (nodeAddresses.isEmpty()) {
            throw new IllegalStateException("No nodes available");
        }
        
        // Write to primary (first node)
        String primaryAddr = nodeAddresses.get(0);
        CacheNode primary = nodes.get(primaryAddr);
        if (primary != null && primary.isAlive()) {
            primary.put(key, value, ttlSeconds);
        }
        
        // Replicate to other nodes (async)
        for (int i = 1; i < nodeAddresses.size(); i++) {
            String replicaAddr = nodeAddresses.get(i);
            CacheNode replica = nodes.get(replicaAddr);
            if (replica != null && replica.isAlive()) {
                // Fire and forget (in production: use async executor)
                new Thread(() -> replica.put(key, value, ttlSeconds)).start();
            }
        }
        
        // Update local cache
        localCache.put(key, value);
    }

    /**
     * Add a new node to the cluster.
     * Only moves keys that should live on this node.
     */
    public void addNode(String address) {
        CacheNode node = new CacheNode(address);
        nodes.put(address, node);
        hashRing.addNode(address, 100);  // 100 virtual nodes
    }

    /**
     * Remove a node from cluster.
     * Redistribute its keys to other nodes.
     */
    public void removeNode(String address) {
        CacheNode node = nodes.remove(address);
        if (node != null) {
            node.shutdown();
            hashRing.removeNode(address);
            // In production: redistribute keys to remaining nodes
        }
    }
}
```

```java
package com.cache;

import java.util.*;

/**
 * INTUITION: ConsistentHashRing distributes keys across nodes.
 * 
 * Uses virtual nodes for even distribution.
 * 
 * Ring layout (simplified):
 *   [N1-v1] → [N2-v1] → [N1-v2] → [N3-v1] → [N2-v2] → [N1-v3] → ...
 * 
 * Key "user:123" hashed → lands between N2-v2 and N1-v3
 * → Assigned to N1 (next node clockwise)
 */
class ConsistentHashRing {
    
    // Sorted map of hash → node address
    private final TreeMap<Long, String> ring;
    
    // Number of virtual nodes per physical node
    private static final int VIRTUAL_NODES = 100;

    ConsistentHashRing() {
        this.ring = new TreeMap<>();
    }

    /**
     * INTUITION: Add a physical node with virtual nodes.
     * 
     * For each virtual node:
     * 1. Hash: nodeAddress + "#" + i → position on ring
     * 2. Add to ring
     */
    void addNode(String nodeAddress, int virtualNodes) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(nodeAddress + "#" + i);
            ring.put(hash, nodeAddress);
        }
    }

    void removeNode(String nodeAddress) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            long hash = hash(nodeAddress + "#" + i);
            ring.remove(hash);
        }
    }

    /**
     * INTUITION: Find the next N nodes for a key.
     * 
     * 1. Hash the key
     * 2. Find the first node at or after the hash position
     * 3. Return N consecutive nodes (wrapping around ring)
     * 
     * This gives us primary + replicas.
     */
    List<String> getNodes(String key, int count) {
        List<String> result = new ArrayList<>();
        
        if (ring.isEmpty()) {
            return result;
        }
        
        long hash = hash(key);
        
        // Find first node at or after hash position
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash);
        if (entry == null) {
            // Wrap around to beginning of ring
            entry = ring.firstEntry();
        }
        
        // Collect N unique nodes
        Set<String> seen = new HashSet<>();
        Map.Entry<Long, String> current = entry;
        
        while (result.size() < count && !seen.contains(current.getValue())) {
            if (seen.add(current.getValue())) {
                result.add(current.getValue());
            }
            
            // Move to next node on ring
            Map.Entry<Long, String> next = ring.higherEntry(current.getKey());
            if (next == null) {
                next = ring.firstEntry();  // Wrap around
            }
            current = next;
            
            // Safety: if we've looped around completely, stop
            if (current == entry) {
                break;
            }
        }
        
        return result;
    }

    /**
     * Simple hash function. In production: use MurmurHash or SHA-1.
     */
    private long hash(String key) {
        return key.hashCode() & 0xffffffffL;  // Make unsigned
    }
}
```

```java
package com.cache;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: CacheNode is a single cache server.
 * 
 * Manages:
 * - In-memory LRU cache
 * - Eviction policy
 * - TTL-based expiration
 * - Health status
 */
public class CacheNode {
    
    private final String address;
    private final Map<String, CacheEntry> cache;
    private final LinkedList<String> lruList;
    private final int maxSize;
    private volatile boolean alive = true;
    private final ScheduledExecutorService cleanupService;
    
    // Stats
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);

    public CacheNode(String address) {
        this(address, 10000);  // Default 10K entries
    }

    public CacheNode(String address, int maxSize) {
        this.address = address;
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                return size() > maxSize;
            }
        };
        this.lruList = new LinkedList<>();
        
        // Periodic cleanup of expired entries
        this.cleanupService = Executors.newScheduledThreadPool(1);
        this.cleanupService.scheduleAtFixedRate(this::expireEntries, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * INTUITION: GET with LRU tracking.
     * 
     * LinkedHashMap with access-order=true:
     * - Every get() moves entry to end (most recently used)
     - eldest entry is always at front (least recently used)
     - When full, eldest is auto-evicted
     */
    public String get(String key) {
        CacheEntry entry = cache.get(key);
        
        if (entry == null) {
            misses.incrementAndGet();
            return null;
        }
        
        // Check TTL
        if (entry.isExpired()) {
            cache.remove(key);
            misses.incrementAndGet();
            return null;
        }
        
        hits.incrementAndGet();
        return entry.getValue();
    }

    /**
     * INTUITION: PUT with LRU eviction.
     * 
     * 1. If key exists, update value and reset TTL
     * 2. If new, add to cache
     * 3. If cache full, eldest entry auto-evicted (LRU)
     */
    public synchronized void put(String key, String value, int ttlSeconds) {
        long expiry = System.currentTimeMillis() + (ttlSeconds * 1000L);
        CacheEntry entry = new CacheEntry(value, expiry);
        cache.put(key, entry);
    }

    public synchronized boolean remove(String key) {
        return cache.remove(key) != null;
    }

    public int size() {
        return cache.size();
    }

    public boolean isAlive() {
        return alive;
    }

    public void shutdown() {
        this.alive = false;
        this.cleanupService.shutdown();
    }

    /**
     * INTUITION: Periodic cleanup of expired entries.
     * 
     * Runs every minute to remove stale data.
     * In production: use a separate expiration queue or TTL-index.
     */
    private void expireEntries() {
        long now = System.currentTimeMillis();
        List<String> toRemove = new ArrayList<>();
        
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().isExpiredAt(now)) {
                toRemove.add(entry.getKey());
            }
        }
        
        for (String key : toRemove) {
            cache.remove(key);
        }
        
        if (!toRemove.isEmpty()) {
            System.out.println("Expired " + toRemove.size() + " entries from " + address);
        }
    }

    // Stats
    public long getHits() { return hits.get(); }
    public long getMisses() { return misses.get(); }
    public double getHitRate() {
        long total = hits.get() + misses.get();
        return total == 0 ? 0 : (double) hits.get() / total;
    }

    /**
     * Cache entry with TTL.
     */
    private static class CacheEntry {
        private final String value;
        private final long expiresAt;  // Epoch millis

        CacheEntry(String value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        String getValue() { return value; }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
        
        boolean isExpiredAt(long now) {
            return now > expiresAt;
        }
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "What if a node is overloaded?"
> "Automatic load shedding: if node CPU > 80%, it stops accepting writes. Clients automatically re-route to other replicas."

### Q2: "How to handle cache stampede (key expires, 1000 requests hit DB)?"
> "Probabilistic early expiration: randomly expire 10% of keys before TTL. Or use lock: only one thread rebuilds, others wait."

### Q3: "How to ensure strong consistency?"
> "Use quorum reads/writes: read from R nodes, write to W nodes, R + W > N. Sacrifices latency for consistency."

### Q4: "How to handle network partitions?"
> "Use vector clocks for conflict resolution. On partition healing, merge conflicting versions."