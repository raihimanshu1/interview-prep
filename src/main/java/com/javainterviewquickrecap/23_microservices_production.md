# Module — Microservices Production Patterns: Health, Graceful Shutdown, Kubernetes — Q&A

> **Skill**: 7+ years — health checks, graceful shutdown, service mesh, Kubernetes probes, graceful degradation.

---

## Q1. Production Patterns for Microservices

### 1. Health Checks — Liveness vs Readiness vs Startup

```
┌─────────────────────────────────────────────────────┐
│                    Kubernetes Pod                    │
│                                                      │
│  ┌──────────────────────────────────────────────┐   │
│  │            Application Container              │   │
│  │                                              │   │
│  │  /actuator/health/liveness    (is JVM alive?) │   │
│  │  /actuator/health/readiness  (can serve reqs?)│   │
│  │  /actuator/health/startup    (fully started?) │   │
│  └──────────────────────────────────────────────┘   │
│           ▲                    ▲          ▲          │
│           │                    │          │          │
│        ┌──┴──┐              ┌─┴──┐    ┌──┴──┐      │
│        │liveness│           │readiness│ │startup│    │
│        │probe │            │ probe   │ │ probe │    │
│        └──────┘            └────────┘ └──────┘      │
└─────────────────────────────────────────────────────┘

Liveness: "Is the app alive?" — if fails → KILL + restart pod
  Check: JVM healthy, not in deadlock loop
  Use: lightweight check (e.g., ping endpoint)

Readiness: "Can I serve traffic?" — if fails → REMOVE from Service
  Check: DB connected, cache warm, queue not full
  Use: full dependency check

Startup: "Has it finished initializing?" — delays liveness/readiness
  Check: migration done, caches loaded
  Use: for slow-starting apps (delay probes)
```

### 2. Graceful Shutdown

```java
// =====================================================
// GRACEFUL SHUTDOWN — Don't drop in-flight requests!
// =====================================================

@Slf4j
@Component
public class GracefulShutdown implements ServletContextListener {
    
    private volatile boolean shuttingDown = false;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    
    @EventListener
    public void onShutdown(ContextClosedEvent event) {
        log.info("Initiating graceful shutdown...");
        shuttingDown = true;
        
        // 1. Mark health check as NOT READY (Kubernetes stops sending traffic)
        //    → /health/readiness returns 503
        
        // 2. Wait for in-flight requests to complete (max 30s)
        executor.shutdown();  // No new tasks accepted
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();  // Force stop remaining
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        
        // 3. Close database connections
        // 4. Close message queue connections
        // 5. Close HTTP client connections
        
        log.info("Graceful shutdown complete");
    }
}

// application.yml:
server:
  shutdown: graceful  # Wait for active requests
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s  # Max wait for graceful shutdown
```

### 3. Service Mesh (Istio/Linkerd) Patterns

```
Service Mesh = Dedicated infrastructure layer for service-to-service communication

WITHOUT Service Mesh:
    Service A ──HTTP──→ Service B
    (Each service handles: retry, timeout, circuit breaker, tracing, mtls)

WITH Service Mesh (Istio):
    Service A → Envoy Proxy (sidecar) → Envoy Proxy → Service B
                ↑                       ↑
                |  All networking logic  |
                |  (retry, timeout, mTLS,|
                |   tracing, metrics)    |
                └───────────────────────┘
    Benefits: Application code is NETWORK-UNAWARE
              All network concerns handled by the mesh
```

### 4. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Same endpoint for liveness and readiness | Restart loop when DB is slow | Separate: liveness = quick check, readiness = dependency check |
| No graceful shutdown | Dropped in-flight requests on deploy | Implement ApplicationListener<ContextClosedEvent> |
| No startup probe | Restart loop for slow-starting services | Add startup probe with enough initial delay |
| Synchronous processing for long operations | Thread pool exhaustion | Use async processing with message queue |
| No timeout on HTTP calls | Cascading failures from slow downstream | Always set connect + read timeouts |

**Final 30-Second**: Separate liveness (JVM alive) and readiness (can serve traffic) probes. Graceful shutdown: mark not-ready, stop accepting, drain in-flight, close connections. Service mesh offloads network concerns from application code. Always set timeouts, implement circuit breakers, and handle partial failures gracefully.