# Module 4b — Advanced Concurrency: Thread Pools, CompletableFuture, Virtual Threads — Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.


Perfect. **Don't deep dive now.** These topics deserve **6–8 hours** by themselves. For the remaining **30 minutes**, let's build a **mental map** so that later everything fits together.

---

# Java Concurrency - 30 Minute Interview Recap ⭐⭐⭐⭐⭐

## Complete Roadmap

```text
                      Java Concurrency

                              │
     ┌────────────────────────┼────────────────────────┐
     │                        │                        │
     ▼                        ▼                        ▼

 Thread Basics          Synchronization         Thread Management

     │                        │                        │
 Runnable                synchronized          ExecutorService
 Callable                ReentrantLock         ThreadPoolExecutor
 Future                  ReadWriteLock         ForkJoinPool
 CompletableFuture       StampedLock           Virtual Threads

                              │
                              ▼

                     Java Memory Model (JMM)

                              │
      ┌──────────────┬──────────────┬──────────────┐
      ▼              ▼              ▼
  Visibility      Atomicity      Ordering

                              │
                              ▼

                  Atomic Classes & CAS

                              │
                              ▼

                Concurrency Utilities

                              │
                              ▼

               Production Problems
```

---

# 1. Thread Basics ⭐⭐⭐⭐⭐

**Thread = Smallest unit of execution.**

```text
Process

│

├── Thread-1

├── Thread-2

└── Thread-3
```

Interview

* Process vs Thread
* Why multithreading?

---

# 2. Thread Lifecycle ⭐⭐⭐⭐

```text
NEW
 │
 ▼
RUNNABLE
 │
 ▼
RUNNING
 │
 ├────► BLOCKED
 │
 ├────► WAITING
 │
 ├────► TIMED_WAITING
 │
 ▼
TERMINATED
```

Interview

* Difference between BLOCKED and WAITING?

---

# 3. Runnable ⭐⭐⭐⭐⭐

```java
Runnable r = () -> System.out.println("Hello");
```

* Doesn't return value
* Cannot throw checked exception

---

# 4. Callable ⭐⭐⭐⭐⭐

```java
Callable<Integer> task = () -> 100;
```

* Returns value
* Can throw exception

---

# 5. Future ⭐⭐⭐⭐

Represents the **result of an asynchronous task**.

```text
Submit Task

↓

Future

↓

get()

↓

Result
```

Problem:

`get()` blocks.

---

# 6. CompletableFuture ⭐⭐⭐⭐⭐

Future + asynchronous composition.

```text
Task A

↓

Task B

↓

Task C

↓

Final Result
```

Very common in modern Spring Boot applications.

---

# 7. Synchronization ⭐⭐⭐⭐⭐

Purpose:

**Prevent multiple threads from corrupting shared data.**

Tools:

* synchronized
* Lock
* Atomic Classes

---

# 8. Monitor / Intrinsic Lock ⭐⭐⭐⭐

Every Java object has an internal monitor.

```java
synchronized(obj){
}
```

The monitor belongs to `obj`.

---

# 9. synchronized ⭐⭐⭐⭐⭐

* Automatic lock acquisition
* Automatic release
* Reentrant

Interview

* synchronized method vs block
* Object lock vs Class lock

---

# 10. ReentrantLock ⭐⭐⭐⭐⭐

More flexible than synchronized.

Features

* tryLock()
* lockInterruptibly()
* Fair locking
* Conditions

---

# 11. ReadWriteLock ⭐⭐⭐⭐

```text
Readers

↓

Can execute together

Writer

↓

Exclusive
```

Best when

Reads >> Writes

---

# 12. StampedLock ⭐⭐⭐

Adds

* Read Lock
* Write Lock
* Optimistic Read

Higher throughput for read-heavy workloads.

---

# 13. Java Memory Model (JMM) ⭐⭐⭐⭐⭐

Defines how threads communicate.

```text
Thread A

↓

Main Memory

↓

Thread B
```

Three concepts

* Visibility
* Atomicity
* Ordering

---

# 14. Happens-Before ⭐⭐⭐⭐⭐

Guarantees

> If A happens-before B,

then B will see A's changes.

Foundation of JMM.

---

# 15. volatile ⭐⭐⭐⭐⭐

Provides

✔ Visibility

✔ Ordering

❌ Atomicity

Interview

* volatile vs synchronized

---

# 16. Atomic Classes ⭐⭐⭐⭐⭐

Examples

```java
AtomicInteger

AtomicLong

AtomicReference
```

Use CAS internally.

---

# 17. CAS (Compare And Swap) ⭐⭐⭐⭐⭐

```text
Current = 10

Expected = 10

New = 20

↓

Update succeeds
```

Lock-free programming.

---

# 18. ABA Problem ⭐⭐⭐⭐

```text
A

↓

B

↓

A
```

CAS thinks nothing changed.

Solution

```text
AtomicStampedReference
```

---

# 19. LongAdder ⭐⭐⭐⭐

Better than AtomicInteger

when many threads update the same counter.

---

# 20. VarHandle ⭐⭐⭐

Modern low-level API replacing many Unsafe use cases.

---

# 21. ExecutorService ⭐⭐⭐⭐⭐

Instead of

```text
new Thread()
```

Use thread pools.

```text
Tasks

↓

ExecutorService

↓

Worker Threads
```

---

# 22. ThreadPoolExecutor ⭐⭐⭐⭐⭐

Most configurable thread pool.

Interview

* corePoolSize
* maximumPoolSize
* queue
* rejection policy

---

# 23. ForkJoinPool ⭐⭐⭐⭐

Divide and conquer.

```text
Big Task

↓

Split

↓

Split

↓

Combine
```

Uses Work Stealing.

---

# 24. Work Stealing ⭐⭐⭐⭐

Idle thread steals work from busy thread.

Improves CPU utilisation.

---

# 25. Virtual Threads ⭐⭐⭐⭐⭐

Java 21 feature.

```text
Platform Thread

↓

Thousands of

Virtual Threads
```

Excellent for

* I/O bound applications

Interview favourite.

---

# 26. Concurrency Utilities ⭐⭐⭐⭐⭐

| Utility        | Purpose                         |
| -------------- | ------------------------------- |
| CountDownLatch | Wait until N tasks finish       |
| CyclicBarrier  | All threads wait for each other |
| Phaser         | Dynamic barrier                 |
| Semaphore      | Limit concurrent access         |
| Exchanger      | Swap data between two threads   |

---

# 27. Production Problems ⭐⭐⭐⭐⭐

## Deadlock

```text
Thread A

Lock1

Waiting Lock2

Thread B

Lock2

Waiting Lock1
```

Nobody proceeds.

---

## Livelock

Threads keep reacting to each other but never make progress.

---

## Starvation

A thread never gets CPU or a required lock because others continually take precedence.

---

## Race Condition

Multiple threads modify shared data simultaneously.

```java
count++;
```

Classic example.

---

## False Sharing

Different variables share the same CPU cache line.

Results in unnecessary cache invalidation and performance loss.

---

## Lock Contention

Many threads competing for the same lock.

Application throughput drops.

---

# Interview Priority ⭐⭐⭐⭐⭐

## Must Know (Asked Almost Every Interview)

* Thread Lifecycle
* Runnable vs Callable
* Future vs CompletableFuture
* synchronized
* ReentrantLock
* volatile
* Java Memory Model
* Happens-before
* CAS
* AtomicInteger
* ExecutorService
* ThreadPoolExecutor
* Virtual Threads
* Deadlock
* Race Condition

---

## Medium Priority

* ReadWriteLock
* ForkJoinPool
* Work Stealing
* CountDownLatch
* Semaphore
* LongAdder
* ABA Problem

---

## Lower Priority (Know the Basics)

* StampedLock
* Phaser
* Exchanger
* VarHandle
* False Sharing

---

# 30-Second Revision

```text
Thread
   │
Runnable / Callable
   │
Future / CompletableFuture
   │
Synchronization
(synchronized / Locks)
   │
JMM
(Visibility • Atomicity • Ordering)
   │
Atomic Classes (CAS)
   │
ExecutorService / Thread Pools
   │
ForkJoin / Virtual Threads
   │
Concurrency Utilities
   │
Production Issues
(Deadlock • Race • Starvation • Livelock)
```

### Topics to Deep Dive Later (Highest ROI)

When you revisit concurrency in depth, prioritise these in order:

1. Java Memory Model (JMM)
2. `synchronized` internals (monitors, biased/lightweight/heavyweight locking where applicable)
3. `ReentrantLock`
4. `ThreadPoolExecutor`
5. `CompletableFuture`
6. `ConcurrentHashMap`
7. Virtual Threads
8. Atomic classes & CAS
9. Production debugging (deadlocks, thread dumps, contention)

These are the topics that consistently appear in senior Java backend interviews and are worth mastering in depth.

---

## Q1. How does ThreadPoolExecutor work? What core parameters matter?

### 1. Why This Concept Matters
Thread pool misuse is the #1 cause of production outages in Java microservices. Too few threads → queue backup. Too many → thread starvation, OOM. Interviewers ask this to test if you understand **resource management under load**.

### 2. Basic Meaning
**ThreadPoolExecutor**: Manages a pool of worker threads. Instead of creating/destroying threads per task, it reuses threads and queues excess tasks.

### 3. Core Parameters Explained

```java
// ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, handler)

ThreadPoolExecutor executor = new ThreadPoolExecutor(
    10,           // corePoolSize: threads kept alive even when idle (min)
    50,           // maxPoolSize: maximum threads when queue is full
    60,           // keepAliveTime: idle threads beyond core die after this
    TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100),  // workQueue: tasks wait here when all core threads busy
    new ThreadPoolExecutor.AbortPolicy()  // handler: what happens when queue is full AND max threads busy
);

// How it works:
// 1. Task arrives → if running threads < corePoolSize → create new thread
// 2. Task arrives → if running threads >= corePoolSize → add to queue
// 3. Task arrives → if queue full AND threads < maxPoolSize → create new thread (UP TO maxPoolSize)
// 4. Task arrives → if queue full AND threads = maxPoolSize → REJECT (use handler)
```

**Rejection Handlers:**
| Handler | Behavior | Use When |
|---------|----------|----------|
| AbortPolicy (default) | Throws RejectedExecutionException | Failure should be immediate and loud |
| CallerRunsPolicy | Executes on submitter's thread (back-pressure!) | You want to slow down the producer |
| DiscardPolicy | Silently drops the task | Tolerable to lose tasks |
| DiscardOldestPolicy | Drops oldest from queue | Latest tasks are more important |

### 4. Thread Pool Sizing

**CPU-bound tasks:**
```java
int poolSize = Runtime.getRuntime().availableProcessors();  // N CPU cores
// Example: 8 cores → 8 threads
// More threads = context switching overhead, no throughput gain
```

**I/O-bound tasks:**
```java
// Formula: poolSize = N * (1 + waitTime / computeTime)
// If compute = 10ms, wait(I/O) = 90ms:
// poolSize = 8 * (1 + 90/10) = 80 threads
// More threads = more concurrent I/O = higher throughput
```

### 5. Executors Factory Pitfalls

```java
// ❌ BAD: Unbounded queue! Can grow to OOM!
ExecutorService bad = Executors.newFixedThreadPool(10);
// Uses LinkedBlockingQueue (unbounded — no limit!)
// 10 threads busy → tasks keep queuing → OutOfMemoryError!

// ❌ BAD: Unbounded thread creation!
ExecutorService bad2 = Executors.newCachedThreadPool();
// Creates NEW thread for each task, kills after 60s idle
// Under sudden load: creates thousands of threads → OOM / thread starvation

// ✅ GOOD: Explicit ThreadPoolExecutor with bounded queue
ThreadPoolExecutor good = new ThreadPoolExecutor(
    10, 50, 60, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

### 6. CompletableFuture for Async Composition

```java
// =====================================================
// COMPLETABLEFUTURE — chaining async operations
// =====================================================

CompletableFuture.supplyAsync(() -> fetchUser(userId))         // Async
    .thenCompose(user -> fetchOrders(user))                    // Chain with another async op
    .thenApply(orders -> calculateTotal(orders))               // Transform result
    .thenAccept(total -> sendEmail(userId, total))            // Consume result
    .exceptionally(ex -> {                                     // Handle errors
        log.error("Processing failed", ex);
        return null;
    });

// Combine independent futures:
CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> fetchUser(id));
CompletableFuture<Inventory> inventoryFuture = CompletableFuture.supplyAsync(() -> checkInventory(sku));

userFuture.thenCombine(inventoryFuture, (user, inventory) -> {
    return new OrderSummary(user, inventory);
});

// Wait for ALL to complete:
CompletableFuture.allOf(future1, future2, future3).join();

// Wait for FIRST to complete:
CompletableFuture.anyOf(fastService, slowService).thenAccept(result -> {
    System.out.println("Fastest response: " + result);
});
```

### 7. Virtual Threads (Java 21+)

```java
// =====================================================
// VIRTUAL THREADS — lightweight threads (Project Loom)
// =====================================================

// Before: Thread per request (OS thread) — limited (~1000 threads before issues)
ExecutorService platformPool = Executors.newFixedThreadPool(200);

// After: Virtual threads — millions of threads possible!
ExecutorService virtualPool = Executors.newVirtualThreadPerTaskExecutor();

// Or use try-with-resources (Java 21+):
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Submit 10,000 tasks — creates 10,000 virtual threads
    // Virtual threads are ~1KB each vs ~1MB for OS threads
    for (int i = 0; i < 10_000; i++) {
        executor.submit(() -> {
            // I/O operations don't block the underlying OS thread!
            // Virtual thread "yields" when blocking
            Thread.sleep(1000);
            return process();
        });
    }
}

// Virtual threads are optimal for I/O-bound workloads
// NOT for CPU-bound — they don't parallelize CPU work
```

### 8. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Executors.newFixedThreadPool() | Unbounded queue → OOM | Use explicit ThreadPoolExecutor with bounded queue |
| Not setting RejectedExecutionHandler | Tasks dropped silently | Use CallerRunsPolicy for back-pressure |
| Forgetting to shutdown executor | Thread leak, app won't stop | executor.shutdown() in @PreDestroy |
| submit(Runnable) getting Future | Ignored exception | Use Future.get() or handle the exception |

### 9. Interview Questions And Answers

#### Beginner

**Q**: What's the difference between newFixedThreadPool and newCachedThreadPool?

**A**: FixedThreadPool has fixed number of threads + unbounded queue. CachedThreadPool creates threads on demand, kills idle ones after 60s. Fixed is for predictable load. Cached can cause thread explosion under high load. Both have issues: Fixed's queue can OOM, Cached's threads can OOM. Always use explicit ThreadPoolExecutor with bounded queue for production.

#### Intermediate

**Q**: How does CompletableFuture handle errors in a chain?

**A**: Use exceptionally() — receives the throwable and returns a fallback value. Use handle() — receives (result, exception) and always processes both. Use whenComplete() — receives (result, exception) but returns the original result (doesn't modify). Errors in one stage propagate to the next stage that has an error handler. If no handler, the entire future completes exceptionally.

#### Senior

**Q**: Design a thread pool for an API gateway handling 10K requests/second with external API calls averaging 200ms.

**A**: I/O-bound → larger pool. Formula: N_threads = N_CPU * (1 + wait/compute). With 8 cores, 200ms I/O, <1ms compute: 8 * (1 + 200/1) ≈ 1600 threads. But context switching overhead at 1600 threads is high. Better approach: use async (CompletableFuture) with smaller pool. Pool size = 200-400 threads for I/O, use async HTTP client (Netty-based) so threads aren't blocked during I/O. Add circuit breaker for slow downstream services. Monitor queue depth and rejection rate.

#### Tricky

**Q**: Virtual threads vs platform threads: when NOT to use virtual threads?

**A**: (1) CPU-bound tasks — virtual threads don't parallelize, use platform threads equal to CPU count. (2) Synchronized blocks — virtual thread pins the carrier thread, defeating the purpose. Use ReentrantLock instead. (3) Native code (JNI) — pins carrier thread. (4) ThreadLocal with many values — virtual threads are plentiful but each carries ThreadLocal overhead. (5) Fixed thread pool patterns — virtual threads should be per-task, not pooled. Virtual threads shine for I/O-bound high-concurrency workloads.

### 10. Final 30-Second Answer

ThreadPoolExecutor: core vs max threads, bounded queue, rejection handler. I/O-bound: more threads. CPU-bound: N_CPU threads. CompletableFuture chains async ops with thenApply/thenCompose. Virtual threads (Java 21+) are 1KB lightweight threads for I/O — millions possible. Avoid virtual threads for CPU-bound or synchronized code.