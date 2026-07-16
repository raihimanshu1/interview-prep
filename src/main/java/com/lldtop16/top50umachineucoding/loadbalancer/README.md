# ⚖️ Problem 30: Load Balancer

> **Difficulty**: ⭐⭐ | **Company Fit**: Any company with web traffic  
> **Est. Time**: 60 min | **Patterns**: Strategy, Health Check, Round-Robin

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Distribute traffic across multiple servers."

**What the interviewer tests**:
```
1. Can you choose an algorithm? (Round-robin, least-connections, IP-hash)
2. Can you detect dead servers? (Health checks)
3. Can you handle sessions? (Sticky vs non-sticky)
4. Can you scale out? (Add/remove servers dynamically)
```

### Step 2: The "Aha!" Moment

The key insight: **Load balancer is a Strategy pattern.**

```
Layers:
       Client
         ↓
   [Load Balancer]  ← THIS is what we're building
         ↓
   ┌─────┴─────┐
   ↓           ↓
Server1     Server2

Algorithms (strategies):
  - Round Robin: cycle through servers
  - Least Connections: pick server with fewest active requests
  - IP Hash: same client → same server (sticky)
  - Weighted: servers have different capacities
```

### Step 3: How to handle failures?

```
Health Check:
  - Send HTTP GET /health every 5 seconds
  - If 3 consecutive failures → mark server DOWN
  - Remove from rotation (stop sending traffic)
  - If 2 consecutive successes → mark UP, add back

Graceful removal:
  - Stop sending NEW requests to draining server
  - Wait for existing requests to complete
  - Then fully remove
```

---

## 💻 Core Implementation

```java
package com.lb;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * INTUITION: LoadBalancer is the traffic distributor.
 * 
 * Uses Strategy pattern to switch algorithms.
 * Uses ConcurrentHashMap to track server health.
 */
public class LoadBalancer {
    
    // All registered servers
    private final List<Server> servers = new CopyOnWriteArrayList<>();
    
    // Current load balancing strategy
    private LoadBalancingStrategy strategy;
    
    // Health checker
    private final HealthChecker healthChecker;
    
    // Round-robin counter
    private final AtomicLong requestCounter = new AtomicLong(0);

    public LoadBalancer() {
        this.strategy = new RoundRobinStrategy();
        this.healthChecker = new HealthChecker(this);
        this.healthChecker.start();
    }

    /**
     * INTUITION: Pick a server for incoming request.
     * 
     * 1. Get list of healthy servers
     * 2. Apply strategy to select one
     * 3. Return the selected server
     */
    public Server selectServer() {
        List<Server> healthyServers = getHealthyServers();
        if (healthyServers.isEmpty()) {
            throw new NoAvailableServerException("No healthy servers");
        }
        
        return strategy.select(healthyServers);
    }

    /**
     * HTTP-style request with path.
     */
    public Server selectServer(String clientIp) {
        List<Server> healthyServers = getHealthyServers();
        if (healthyServers.isEmpty()) {
            throw new NoAvailableServerException("No healthy servers");
        }
        
        return strategy.select(healthyServers, clientIp);
    }

    public void addServer(Server server) {
        servers.add(server);
        System.out.println("Added server: " + server.getUrl());
    }

    public void removeServer(String serverId) {
        servers.removeIf(s -> s.getId().equals(serverId));
        System.out.println("Removed server: " + serverId);
    }

    public void setStrategy(LoadBalancingStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Server> getServers() {
        return Collections.unmodifiableList(servers);
    }

    public List<Server> getHealthyServers() {
        return servers.stream()
            .filter(Server::isHealthy)
            .collect(Collectors.toList());
    }

    public void shutdown() {
        healthChecker.shutdown();
    }
}
```

```java
package com.lb;

import java.util.*;

/**
 * INTUITION: Server represents a backend instance.
 * 
 * Tracks:
 * - URL/IP
 * - Health status
 * - Active connections (for least-connections algo)
 * - Weight (for weighted round-robin)
 */
public class Server {
    private final String id;
    private final String url;
    private final int weight;
    private volatile boolean healthy = true;
    private volatile boolean draining = false;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    public Server(String id, String url) {
        this(id, url, 1);  // Default weight = 1
    }

    public Server(String id, String url, int weight) {
        this.id = id;
        this.url = url;
        this.weight = weight;
    }

    public void incrementConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementConnections() {
        activeConnections.decrementAndGet();
    }

    // Getters
    public String getId() { return id; }
    public String getUrl() { return url; }
    public int getWeight() { return weight; }
    public boolean isHealthy() { return healthy && !draining; }
    public boolean isDraining() { return draining; }
    public int getActiveConnections() { return activeConnections.get(); }

    public void setHealthy(boolean healthy) { this.healthy = healthy; }
    public void setDraining(boolean draining) { this.draining = draining; }
}
```

```java
package com.lb;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * INTUITION: Strategy pattern for load balancing algorithms.
 * 
 * Each algorithm decides HOW to pick a server.
 * The LoadBalancer doesn't care which algorithm is used.
 */
public interface LoadBalancingStrategy {
    Server select(List<Server> servers);
    default Server select(List<Server> servers, String clientIp) {
        return select(servers);  // Default: ignore client IP
    }
}

/**
 * Round Robin: cycle through servers in order.
 * 
 * Requests: 1, 2, 3, 4, 5, 6...
 * Servers:  A, B, C, A, B, C...
 */
class RoundRobinStrategy implements LoadBalancingStrategy {
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public Server select(List<Server> servers) {
        int index = (int) (counter.getAndIncrement() % servers.size());
        return servers.get(index);
    }
}

/**
 * Least Connections: pick server with fewest active connections.
 * 
 * Best for: Long-lived connections (WebSocket, streaming)
 */
class LeastConnectionsStrategy implements LoadBalancingStrategy {
    @Override
    public Server select(List<Server> servers) {
        return servers.stream()
            .min(Comparator.comparingInt(Server::getActiveConnections))
            .orElse(null);
    }
}

/**
 * IP Hash: route same client IP to same server.
 * 
 * Best for: Session affinity (shopping carts, user state)
 */
class IpHashStrategy implements LoadBalancingStrategy {
    @Override
    public Server select(List<Server> servers, String clientIp) {
        int hash = Math.abs(clientIp.hashCode());
        int index = hash % servers.size();
        return servers.get(index);
    }
}

/**
 * Weighted Round Robin: servers have different capacities.
 * 
 * Server A (weight=5) gets 5x more traffic than Server B (weight=1).
 */
class WeightedRoundRobinStrategy implements LoadBalancingStrategy {
    private final AtomicLong counter = new AtomicLong(0);
    
    @Override
    public Server select(List<Server> servers) {
        // Build weighted list: [A,A,A,A,A,B]
        List<Server> weighted = new ArrayList<>();
        for (Server server : servers) {
            for (int i = 0; i < server.getWeight(); i++) {
                weighted.add(server);
            }
        }
        
        int index = (int) (counter.getAndIncrement() % weighted.size());
        return weighted.get(index);
    }
}
```

```java
package com.lb;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: HealthChecker pings servers periodically.
 * 
 * Runs in background thread.
 * Marks unhealthy servers so they're not selected.
 */
public class HealthChecker {
    private final LoadBalancer loadBalancer;
    private final ScheduledExecutorService scheduler;
    private final long checkIntervalMs = 5000;  // Every 5 seconds
    private final int failureThreshold = 3;     // 3 failures → down
    private final int successThreshold = 2;     // 2 successes → up
    private final Map<String, Integer> failureCount = new ConcurrentHashMap<>();
    private final Map<String, Integer> successCount = new ConcurrentHashMap<>();

    public HealthChecker(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkAllServers, 
            checkIntervalMs, checkIntervalMs, TimeUnit.MILLISECONDS);
    }

    private void checkAllServers() {
        for (Server server : loadBalancer.getServers()) {
            boolean healthy = pingServer(server);
            
            if (healthy) {
                successCount.merge(server.getId(), 1, Integer::sum);
                failureCount.remove(server.getId());
                
                if (successCount.getOrDefault(server.getId(), 0) >= successThreshold) {
                    if (!server.isHealthy()) {
                        System.out.println("✓ Server " + server.getId() + " is UP");
                        server.setHealthy(true);
                        server.setDraining(false);
                    }
                    successCount.remove(server.getId());
                }
            } else {
                failureCount.merge(server.getId(), 1, Integer::sum);
                successCount.remove(server.getId());
                
                if (failureCount.getOrDefault(server.getId(), 0) >= failureThreshold) {
                    if (server.isHealthy()) {
                        System.out.println("✗ Server " + server.getId() + " is DOWN");
                        server.setHealthy(false);
                    }
                    failureCount.remove(server.getId());
                }
            }
        }
    }

    private boolean pingServer(Server server) {
        try {
            // In production: HTTP GET to server's health endpoint
            // For demo: simulate random failures (10% chance of failure)
            return Math.random() > 0.1;
        } catch (Exception e) {
            return false;
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle sticky sessions (user always goes to same server)?"
> "Use IP Hash strategy. Or use session cookie with server ID. Store session in shared cache (Redis) so any server can handle it."

### Q2: "How to add a server without dropping connections?"
> "Mark new server as 'draining = false'. Add to rotation. Old server: set 'draining = true'. Stop sending new requests. Wait for active connections to drain. Then remove."

### Q3: "How to handle sudden traffic spike?"
> "Auto-scaling: monitor CPU/requests per second. If threshold crossed, spin up new server. Register with LB. Cloud: use Kubernetes HPA."

### Q4: "How to prevent cascading failures?"
> "Circuit breaker: if server error rate > 50%, stop sending traffic. Retry with exponential backoff. Fallback to cache or default response."