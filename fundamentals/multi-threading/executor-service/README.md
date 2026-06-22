# Executor Framework and Thread Pool — Complete Deep Dive

## 1. Why This Concept Matters

The Executor framework separates task submission from execution mechanics. It is the standard way to manage threads in Java. Understanding thread pools, task queues, rejection policies, and lifecycle is essential for production systems. Interviewers test this because it reveals your understanding of concurrency utilities, resource management, and performance tuning.

Misunderstanding Executor causes:
- Thread exhaustion from unbounded queue creation
- Task loss from saturation without rejection policy
- Memory leaks from un-reaped queued tasks
- Performance issues from wrong pool sizing

## 2. Basic Meaning

**Executor**: interface for executing tasks (`Runnable`/`Callable`).
**ExecutorService**: manages lifecycle, returns `Future` for results.
**ThreadPoolExecutor**: configurable pool with work queue.
**Executors**: factory class creating common pool configurations.
**ForkJoinPool**: work-stealing pool for recursive tasks (Java 7+).

Key vocabulary:
- **Core threads**: minimum threads always alive (unless timed out)
- **Maximum threads**: upper bound on thread count
- **`workQueue`**: holds tasks waiting for execution
- **`RejectedExecutionHandler`**: what happens when pool + queue full
- **`Future<T>`**: handle for async result
- **`Callable<V>`**: task returning value, can throw exception
- **`Runnable`**: task returning void, cannot throw checked exception
- **`ThreadFactory`**: custom thread creation

## 3. Real Code / Real Example

```java
import java.util.concurrent.*;

public class ExecutorDemo {
    public static void main(String[] args) throws Exception {
        // === FIXED THREAD POOL ===
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            fixedPool.submit(() -> {
                System.out.println("Task " + taskId + " running on " + Thread.currentThread().getName());
                try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return taskId * 10;
            });
        }
        fixedPool.shutdown();
        System.out.println("Fixed pool shutdown, awaiting termination...");
        fixedPool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Fixed pool terminated: " + fixedPool.isTerminated());

        // === CALLABLE + FUTURE ===
        ExecutorService single = Executors.newSingleThreadExecutor();
        Future<Integer> future = single.submit(() -> {
            Thread.sleep(300);
            return 42;
        });
        System.out.println("Future done? " + future.isDone());
        System.out.println("Future result: " + future.get()); // blocks until done
        single.shutdown();

        // === SCHEDULED EXECUTOR ===
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
        scheduled.scheduleAtFixedRate(() -> {
            System.out.println("Heartbeat at " + System.currentTimeMillis());
        }, 0, 1, TimeUnit.SECONDS);
        Thread.sleep(3500);
        scheduled.shutdown();

        // === CUSTOM THREADPOOLEXECUTOR ===
        ThreadPoolExecutor custom = new ThreadPoolExecutor(
            2,                          // corePoolSize
            4,                          // maximumPoolSize
            60, TimeUnit.SECONDS,       // keepAliveTime for idle non-core threads
            new ArrayBlockingQueue<>(2), // bounded queue
            new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
        );
        for (int i = 0; i < 10; i++) {
            final int id = i;
            custom.execute(() -> {
                System.out.println("Custom task " + id + " on " + Thread.currentThread().getName());
                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        custom.shutdown();

        // === COMPLETABLEFUTURE (Java 8+) ===
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return "Hello from async";
        });
        System.out.println("Doing other work...");
        System.out.println("Result: " + cf.get()); // wait for result

        // === INvokeALL / INvokeAny ===
        ExecutorService pool = Executors.newFixedThreadPool(3);
        List<Callable<Integer>> tasks = List.of(
            () -> { Thread.sleep(100); return 1; },
            () -> { Thread.sleep(200); return 2; },
            () -> { Thread.sleep(50); return 3; }
        );
        List<Future<Integer>> results = pool.invokeAll(tasks);
        for (Future<Integer> f : results) {
            System.out.println("invokeAll result: " + f.get());
        }
        pool.shutdown();
    }
}
```

Expected output:
```
Task 1 running on pool-1-thread-1
Task 2 running on pool-1-thread-2
Task 3 running on pool-1-thread-3
Task 4 running on pool-1-thread-1
Task 5 running on pool-1-thread-2
Task 6 running on pool-1-thread-3
Fixed pool shutdown, awaiting termination...
Fixed pool terminated: true
Future done? false (then true after ~300ms)
Future result: 42
Heartbeat at ... (4 times, then stops after ~3.5s)
Custom task 0 on pool-2-thread-1
Custom task 1 on pool-2-thread-2
...
Doing other work...
Result: Hello from async
invokeAll result: 1
invokeAll result: 2
invokeAll result: 3
```

## 4. What Happens Internally

**ThreadPoolExecutor structure:**
```java
public class ThreadPoolExecutor extends AbstractExecutorService {
    private final AtomicInteger ctl = new AtomicInteger(0); // worker count + state
    private final BlockingQueue<Runnable> workQueue;
    private final ReentrantLock mainLock;
    private final Condition termination;
    private final HashSet<Worker> workers; // worker threads

    static final int RUNNING    = -1 << COUNT_BITS;
    static final int SHUTDOWN   =  0 << COUNT_BITS;
    static final int STOP       =  1 << COUNT_BITS;
    static final int TERMINATING= 2 << COUNT_BITS;
    static final int TERMINATED = 3 << COUNT_BITS;

    // Worker = internal class extending AbstractQueuedSynchronizer
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        final Thread thread;
        Runnable firstTask;
        // ...
    }
}
```

**`execute(Runnable command)` flow:**
```java
public void execute(Runnable command) {
    int c = ctl.get();
    if (workerCount < corePoolSize) {
        // 1. Try adding new core worker
        if (addWorker(command, true)) return;
        c = ctl.get();
    }
    if (isRunning(c) && workQueue.offer(command)) {
        // 2. Queue accepted, check if needs shutdown
        if (isRunning(c) && !isRunning(ctl.get()))
            interruptIdleWorkers(ONLY_ONE);
        return;
    }
    if (!addWorker(command, false)) {
        // 3. Queue full, create non-core worker or reject
        reject(command); // CallerRunsPolicy, AbortPolicy, etc.
    }
}
```

Priority: core threads → queue → max threads → reject.

**Worker lifecycle:**
1. `addWorker()`: creates `Worker` thread, starts it
2. Worker loop: `getTask()` from queue (blocks via `take()` or `poll(timeout)`)
3. When worker count > core and idle > keepAliveTime → worker exits
4. On shutdown: interrupt idle workers, await termination

**`FutureTask` (for Callable):**
```java
public class FutureTask<V> implements Runnable, Future<V> {
    private volatile V result;   // null until done
    private volatile Throwable exception;
    
    public void run() {
        try {
            result = callable.call();
            set(result); // volatile write, completes future
        } catch (Throwable t) { exception = t; }
    }
    
    public V get() throws InterruptedException, ExecutionException {
        if (result == null) awaitDone(); // park thread
        return result;
    }
}
```

## 5. Tricky Interview Cases

**Case 1 — `execute()` vs `submit()`**
```java
ExecutorService pool = Executors.newFixedThreadPool(2);
pool.execute(() -> System.out.println("execute"));
Future<?> f = pool.submit(() -> System.out.println("submit"));
```
Output: Both run. `execute()` returns void. `submit()` returns `Future`.
Explanation: `submit()` wraps `Runnable` in `FutureTask`. `execute()` for fire-and-forget.

**Case 2 — Rejection policies**
```java
ThreadPoolExecutor exec = new ThreadPoolExecutor(
    1, 1, 0, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>(1), // capacity 1
    new ThreadPoolExecutor.AbortPolicy() // default
);
exec.execute(() -> System.out.println("Task 1"));
exec.execute(() -> System.out.println("Task 2"));
exec.execute(() -> System.out.println("Task 3")); // REJECTED
```
Output: `RejectedExecutionException` on third task.
Policies: `AbortPolicy` (throw), `CallerRunsPolicy` (run in caller thread), `DiscardPolicy` (silently drop), `DiscardOldestPolicy` (drop oldest).

**Case 3 — Core vs non-core threads**
```java
ThreadPoolExecutor exec = new ThreadPoolExecutor(
    2, 4,           // core=2, max=4
    30, TimeUnit.SECONDS,
    new SynchronousQueue<>() // no queue
);
// Submit 4 tasks fast → creates 4 threads
// After 30s of idle → 2 threads die (down to core)
// Submit task 5 after 30s → creates new thread (max 4)
```
Output: Thread count fluctuates between 2 and 4.

**Case 4 — `invokeAll` waits for all**
```java
ExecutorService pool = Executors.newFixedThreadPool(2);
List<Callable<String>> tasks = List.of(
    () -> { Thread.sleep(1000); return "slow"; },
    () -> { Thread.sleep(100); return "fast"; }
);
List<Future<String>> results = pool.invokeAll(tasks);
// Blocks until ALL complete, then returns list of futures
System.out.println(results.get(0).get()); // "slow"
System.out.println(results.get(1).get()); // "fast"
```
Output: `invokeAll` blocks ~1s.

**Case 5 — `ScheduledExecutorService` fixed rate vs fixed delay**
```java
ScheduledExecutorService s = Executors.newScheduledThreadPool(1);
// Fixed rate: start next at fixed interval from START of previous
s.scheduleAtFixedRate(() -> {
    long start = System.currentTimeMillis();
    try { Thread.sleep(500); } catch (InterruptedException e) {}
    System.out.println("Took " + (System.currentTimeMillis() - start));
}, 0, 1, TimeUnit.SECONDS);
```
Output: Task starts every 1s. If task takes 500ms, next starts at 1s, 2s, etc.
`s.scheduleWithFixedDelay()`: wait 1s after task FINISHES.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `Executors.newCachedThreadPool()` | Unbounded thread creation → OOM | Use bounded queue + explicit `ThreadPoolExecutor` |
| Not shutting down executor | Threads prevent JVM exit | `shutdown()` or `shutdownNow()` |
| `submit()` + never `get()` | Unhandled exceptions lost, thread leak | Always `get()` or handle in `Future` |
| `newFixedThreadPool` with unbounded queue | Queue grows forever if consumers slow | Use bounded queue |
| `CallerRunsPolicy` in web app | Request handling on main thread → DoS | Use explicit rejection + backpressure |
| Large task queue | Memory leak from queued tasks | Use `SynchronousQueue` or bounded queue |

## 7. Production Usage

**Web server thread pool (Tomcat/Jetty):**
```java
// Application server manages thread pools, not you directly
// But concept same: core threads, max threads, queue
Server server = new Server(8080);
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    10, 200, 30, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100), // max 100 queued requests
    new ThreadPoolExecutor.CallerRunsPolicy() // if full, run on acceptor thread
);
server.setExecutor(executor);
```

**Spring Async:**
```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolExecutor(
            4, 8, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            new ThreadFactoryBuilder().setNameFormat("async-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
```

**Batch processing:**
```java
ExecutorService batchPool = Executors.newFixedThreadPool(4);
List<Future<Result>> futures = new ArrayList<>();
for (Record r : records) {
    futures.add(batchPool.submit(() -> process(r)));
}
for (Future<Result> f : futures) {
    Result r = f.get(); // wait for all
}
batchPool.shutdown();
```

## 8. Advanced Details

- **`ForkJoinPool` (Java 7+):** Work-stealing: idle threads steal tasks from others' deques. Used by `parallelStream()`. `ForkJoinTask` (`RecursiveTask`, `RecursiveAction`) supports `fork()`/`join()`.
- **`ThreadPoolExecutor` prestart:** `prestartCoreThread()` / `prestartAllCoreThreads()` creates threads before tasks arrive.
- **`ThreadFactory`:** Custom naming, priority, daemon status, uncaught exception handler.
- **`CompletionService` (`ExecutorCompletionService<V>`):** Queues completed tasks. `poll()` / `take()` returns completed futures in completion order, not submission order.
- **`ForkJoinPool.ManagedBlocker`:** For blocking operations in ForkJoinPool — lets pool add extra threads when tasks block.
- **`WorkStealingPool` (Java 8+):** `Executors.newWorkStealingPool()` — parallel task execution.
- **`ThreadPoolExecutor` ctl field:** Packs worker count + state into single `AtomicInteger` for lock-free status checks.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between `execute()` and `submit()` in ExecutorService?
A: `execute(Runnable)` returns void, cannot get result or catch exceptions. `submit()` returns `Future<T>`: can call `get()` for result, `isDone()` to check completion, `cancel()` to interrupt. `submit()` also accepts `Callable` which returns values.

### Intermediate
Q: What are the differences between `newFixedThreadPool`, `newCachedThreadPool`, and `newSingleThreadExecutor`?
A:
- `newFixedThreadPool(n)`: fixed number of threads, unbounded queue. Good for known concurrency level.
- `newCachedThreadPool()`: creates threads as needed, reuses idle threads (60s timeout). Unbounded max threads — dangerous under load.
- `newSingleThreadExecutor()`: single thread, tasks execute sequentially.

### Senior
Q: You are tuning a web server. Current pool: `newFixedThreadPool(50)` with unbounded queue. Under heavy load, response times degrade and OOM occurs. Why? How do you fix it?
A: Unbounded queue accepts all requests. With 50 threads and thousands queued, memory fills with queued tasks. Threads can't keep up, queue grows until OOM.

Fix with bounded queue:
```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    10,                      // core threads
    50,                      // max threads
    60, TimeUnit.SECONDS,   // idle timeout
    new LinkedBlockingQueue<>(500), // max 500 queued
    new ThreadPoolExecutor.CallerRunsPolicy() // if full, run on caller
);
```
This limits memory usage (max 500 queued + 50 active). `CallerRunsPolicy` provides backpressure: if all threads busy and queue full, caller thread runs task, slowing submission rate.

### Tricky
Q: `ThreadPoolExecutor` with core=2, max=4, queue capacity=2. Submit 6 tasks. How many threads are created? What happens to the 6th task?
A: Threads created:
- Tasks 1-2: core threads 1 and 2 start immediately (no queueing)
- Tasks 3-4: queue accepts them (capacity 2)
- Task 5: queue full, create thread 3 (under max=4)
- Task 6: queue full, create thread 4 (max=4)

All 6 tasks run. Threads: 4 active, 2 queued.

If submitted 7th task while all 4 active + queue full (2), 7th is REJECTED.

After tasks complete and threads idle 60s → down to core=2.

## 10. Final 30-Second Answer

ExecutorService = task submission decoupled from execution. `execute()` void, `submit()` returns `Future`. ThreadPoolExecutor: core threads, queue, max threads, rejection policy. **Never use `newCachedThreadPool`** — unbounded. `Callable` for results, `Future.get()` waits. ScheduledExecutorService for periodic tasks. `invokeAll()` / `invokeAny()` for batch. **Always bound queue + explicit rejection policy** in production. `CompletableFuture` (Java 8+) for async composition. `shutdown()` required to exit JVM.