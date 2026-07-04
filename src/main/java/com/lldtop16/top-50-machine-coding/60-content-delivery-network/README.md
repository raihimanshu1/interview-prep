# 🌐 Problem 60: Content Delivery Network (CDN)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any company with global users  
> **Est. Time**: 90 min | **Patterns**: Observer, Strategy, Caching

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a CDN for serving static content globally."

**What the interviewer tests**:
```
1. Can you cache content? (Edge servers)
2. Can you route users? (Nearest server)
3. Can you handle cache invalidation? (TTL, purge)
4. Can you handle failures? (Origin fallback)
```

### Step 2: The "Aha!" Moment

The key insight: **CDN = distributed cache with smart routing.**

```
WITHOUT CDN:
  User (India) → Server (US) → 200ms
  
WITH CDN:
  User (India) → Edge (Mumbai) → 20ms
  
CACHE HIERARCHY:
  Browser cache → Edge cache → Origin
  
HIT RATIO:
  90% hit = 10x reduction in origin load
```

### Step 3: How to handle cache invalidation?

```
INVALIDATION STRATEGIES:
1. TTL: Auto-expire after N seconds
2. Purge: Manual invalidation
3. Stale-while-revalidate: Serve stale, update async
4. Versioning: URL versioning (/v1/, /v2/)
```

---

## 💻 Core Implementation

```java
package com.cdn;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: CDN manages edge servers and routes requests.
 */
public class CDN {
    
    private final Map<String, OriginServer> origins;
    private final List<EdgeServer> edgeServers;
    private final RoutingStrategy routingStrategy;

    public CDN() {
        this.origins = new ConcurrentHashMap<>();
        this.edgeServers = new CopyOnWriteArrayList<>();
        this.routingStrategy = new LatencyBasedRouting();
    }

    /**
     * INTUITION: Route request to nearest edge server.
     */
    public CacheResponse get(String path, String clientIp) {
        // Find nearest edge server
        EdgeServer edge = routingStrategy.route(clientIp, edgeServers);
        
        // Check edge cache
        CacheResponse cached = edge.get(path);
        if (cached != null && !cached.isExpired()) {
            return cached;
        }
        
        // Fetch from origin
        OriginServer origin = origins.get(edge.getOriginId());
        CacheResponse response = origin.fetch(path);
        
        // Cache at edge
        if (response != null) {
            edge.cache(path, response);
        }
        
        return response;
    }

    /**
     * Purge content from all edge servers.
     */
    public void purge(String path) {
        for (EdgeServer edge : edgeServers) {
            edge.purge(path);
        }
    }

    public void addOrigin(OriginServer origin) {
        origins.put(origin.getId(), origin);
    }

    public void addEdgeServer(EdgeServer edge) {
        edgeServers.add(edge);
    }
}

/**
 * Edge server (PoP - Point of Presence).
 */
class EdgeServer {
    private final String serverId;
    private final String location;
    private final String originId;
    private final Map<String, CacheResponse> cache;

    EdgeServer(String serverId, String location, String originId) {
        this.serverId = serverId;
        this.location = location;
        this.originId = originId;
        this.cache = new ConcurrentHashMap<>();
    }

    CacheResponse get(String path) {
        return cache.get(path);
    }

    void cache(String path, CacheResponse response) {
        cache.put(path, response);
    }

    void purge(String path) {
        cache.remove(path);
    }

    public String getServerId() { return serverId; }
    public String getLocation() { return location; }
    public String getOriginId() { return originId; }
}

/**
 * Origin server (source of content).
 */
class OriginServer {
    private final String originId;
    private final String url;

    OriginServer(String originId, String url) {
        this.originId = originId;
        this.url = url;
    }

    CacheResponse fetch(String path) {
        // Mock: return response
        String content = "Content from " + url + path;
        return new CacheResponse(content, 200, System.currentTimeMillis() + 3600000);
    }

    public String getId() { return originId; }
}

/**
 * Cached response.
 */
class CacheResponse {
    private final String content;
    private final int statusCode;
    private final long expiresAt;

    CacheResponse(String content, int statusCode, long expiresAt) {
        this.content = content;
        this.statusCode = statusCode;
        this.expiresAt = expiresAt;
    }

    public String getContent() { return content; }
    public int getStatusCode() { return statusCode; }
    
    boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}

/**
 * Routing strategy interface.
 */
interface RoutingStrategy {
    EdgeServer route(String clientIp, List<EdgeServer> servers);
}

/**
 * Route based on latency.
 */
class LatencyBasedRouting implements RoutingStrategy {
    @Override
    public EdgeServer route(String clientIp, List<EdgeServer> servers) {
        // Simplified: return first server
        // In production: measure latency to each server
        return servers.get(0);
    }
}

/**
 * Route based on geo-location.
 */
class GeoBasedRouting implements RoutingStrategy {
    @Override
    public EdgeServer route(String clientIp, List<EdgeServer> servers) {
        // Simplified: return server in same region
        return servers.get(0);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle cache stampede?"
> "Lock on cache miss. Stale-while-revalidate. Probabilistic early expiration."

### Q2: "How to handle large files?"
> "Chunked transfer. Range requests. Partial caching."

### Q3: "How to handle DDoS?"
> "Rate limiting at edge. IP blocking. Challenge-page. Anycast routing."

### Q4: "How to handle cache fragmentation?"
> "TTL jitter. Domain sharding. Consistent hashing."