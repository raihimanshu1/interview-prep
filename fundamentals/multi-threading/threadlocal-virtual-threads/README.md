# ThreadLocal & Virtual Threads (Java 21) — Complete Deep Dive

## 1. Why This Concept Matters

**ThreadLocal** provides per-thread variable isolation — each thread has its own copy of a variable, invisible to other threads. It's used for request-scoped data (user context, transaction IDs, DB connections) without passing parameters through every method call. **Virtual Threads** (Project Loom, Java 21) are lightweight threads that enable high-concurrency apps with simple thread-per-request code, without the overhead of OS threads. Understanding both is critical for modern Java concurrency — ThreadLocal for thread-scoped state, Virtual Threads for massive scalability.

Misunderstanding ThreadLocal causes: memory leaks in thread pools (threads are reused, ThreadLocal values persist), confusing thread-scoped with request-scoped, and security issues if user data leaks between requests. Misunderstanding virtual threads causes: using synchronized blocks (pinning), not knowing they're 1000x cheaper than platform threads, incorrect ThreadLocal usage patterns.

## 2. ThreadLocal

**Basic Meaning:** Each thread that accesses a ThreadLocal variable gets its own independently initialized copy.

```java
public class ThreadLocalDemo {
    
    // User context for the current request
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();
    
    // Set from filter/interceptor when request comes in
    public static void setCurrentUser(UserContext ctx) {
        userContext.set(ctx);
    }
    
    // Get from anywhere in the same thread
    public static UserContext getCurrentUser() {
        return userContext.get();
    }
    
    // Clear after request completes (CRITICAL for thread pools!)
    public static void clear() {
        userContext.remove();
    }
    
    // Usage in service layer:
    public void processOrder(Long orderId) {
        UserContext user = ThreadLocalDemo.getCurrentUser();
        // user.getUserId(), user.getRole() — available without passing parameters
        log.info("User {} processing order {}", user.getUserId(), orderId);
    }
}
```

**Common use cases:**
- Request context (user ID, role, tenant ID) — avoid passing through every method
- Transactional context (Hibernate session per thread)
- Random ID generators (per-thread seed)
- DateFormat instances (not thread-safe, use ThreadLocal)

**Memory leak danger with thread pools:**
```java
ExecutorService pool = Executors.newFixedThreadPool(10);
// Thread 1 runs: userContext.set("user-A")
// Thread 1 returns to pool
// Thread 1 runs AGAIN: userContext never cleared!
// Next request on same thread might see "user-A" from previous request!

// FIX: Always call remove() in finally block or after request completes:
try {
    userContext.set(user);
    // process request
} finally {
    userContext.remove(); // Critical for thread pools!
}
```

**InheritableThreadLocal**: Child threads inherit parent's ThreadLocal values. Use with care (only simple immutable data).

## 3. Virtual Threads (Java 21+)

**What they are:** Lightweight threads managed by the JVM (not the OS). Millions can run simultaneously. When a virtual thread blocks (on I/O, sleep, lock), it is unmounted from the carrier platform thread and another virtual thread runs.

**Key differences from platform threads:**

| Aspect | Platform Thread | Virtual Thread |
|--------|----------------|---------------|
| Managed by | OS kernel | JVM (scheduler) |
| Stack size | ~1MB (fixed) | ~10KB (grows/shrinks) |
| Max per JVM | ~10,000-30,000 | Millions |
| Creation cost | Expensive (syscall) | Cheap (in-memory) |
| Context switch | OS kernel (costly) | JVM user-mode (cheap) |
| Blocking behavior | Blocks OS thread | Unmounts, carrier thread does other work |

```java
// Creating virtual threads (Java 21+)
public class VirtualThreadDemo {
    
    public static void main(String[] args) throws Exception {
        // Method 1: Thread.ofVirtual()
        Thread vt1 = Thread.ofVirtual()
            .name("vt-1")
            .start(() -> {
                System.out.println("Running on virtual thread");
                // Blocking calls DON'T block carrier thread
                Thread.sleep(100); // Unmounts VT, carrier continues
            });
        vt1.join();
        
        // Method 2: Executors.newVirtualThreadPerTaskExecutor()
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                executor.submit(() -> {
                    // Each task runs on a fresh virtual thread
                    handleRequest();
                });
            }
        } // Auto-closes, waits for all tasks
        
        // Method 3: Structured Concurrency (Java 21 preview)
        // try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        //     Future<String> user = scope.fork(() -> fetchUser());
        //     Future<Order> order = scope.fork(() -> fetchOrder());
        //     scope.join();
        //     scope.throwIfFailed();
        //     return new Response(user.resultNow(), order.resultNow());
        // }
    }
    
    static void handleRequest() {
        // Simple thread-per-request — no reactive/callback hell needed
        var user = fetchUser();
        var order = fetchOrder();
        process(user, order);
    }
}
```

**Pinning (the main gotcha):**
```java
// BAD — pinned virtual thread
synchronized (this) {  // synchronized blocks PIN the virtual thread
    // While inside synchronized, VT cannot be unmounted
    // Carrier thread is blocked!
    Thread.sleep(100);  // Blocks the carrier OS thread!
}

// GOOD — use ReentrantLock instead
private final Lock lock = new ReentrantLock();
lock.lock();
try {
    Thread.sleep(100); // VT can be unmounted while waiting for lock
} finally {
    lock.unlock();
}
```

**ThreadLocal with Virtual Threads:**
```java
// ThreadLocal works with VTs, BUT:
// - Each VT gets its own ThreadLocal copy (expensive with millions of VTs)
// - InheritableThreadLocal has unexpected behavior
// - Best practice: avoid ThreadLocal with VTs, pass context explicitly

// Alternative for VTs:
public record RequestContext(String userId, String role) {}

public void handle(RequestContext ctx) {
    // Pass context as parameter instead of ThreadLocal
    process(ctx, otherArgs);
}
```

## 4. Final 30-Second Answer

**ThreadLocal**: per-thread isolated variables. Use for request context (user, tenant). CRITICAL: call `remove()` in finally block to prevent memory leaks in thread pools. **Virtual Threads (Java 21)**: lightweight, ~10KB each, millions per JVM. Blocking unmounts VT from carrier thread. Avoid `synchronized` (causes pinning), use `ReentrantLock`. ThreadPool with virtual threads: `Executors.newVirtualThreadPerTaskExecutor()`. Platform threads for CPU-intensive work, VTs for I/O-heavy workloads.