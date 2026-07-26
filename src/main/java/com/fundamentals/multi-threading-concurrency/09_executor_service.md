Excellent. This is probably the **#1 concurrency topic** for senior Java interviews.

Interviewers rarely ask you to create a thread manually anymore. Instead, they ask:

* Why shouldn't we create threads directly?
* How does `ThreadPoolExecutor` work internally?
* What happens when all threads are busy?
* How do you size a thread pool?
* What are rejection policies?

If you master this chapter, you'll answer a huge percentage of concurrency interview questions.

---

# Module 4 — Chapter 15

# Executor Framework & Thread Pools ⭐⭐⭐⭐⭐

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> One of the most frequently asked topics for 6–10+ years Java developers.
>
> Interviewers expect you to explain:
>
> * Why Thread Pools exist
> * Executor vs ExecutorService
> * ThreadPoolExecutor internals
> * Task lifecycle
> * Core Pool Size vs Maximum Pool Size
> * Blocking Queues
> * Rejection Policies
> * Thread Pool sizing

---

# 1. Why Do We Need Thread Pools?

Suppose a web server receives **10,000 requests**.

Naive approach:

```java
for (Request request : requests) {
    new Thread(() -> process(request)).start();
}
```

Question:

How many threads are created?

**10,000 threads.**

Problems:

* Huge memory usage
* Expensive thread creation
* Expensive context switching
* CPU spends more time switching threads than executing work
* Can even crash with `OutOfMemoryError: unable to create native thread`

---

## Visual Diagram

```text
Request 1  ---> Thread 1
Request 2  ---> Thread 2
Request 3  ---> Thread 3
...
Request 10000 ---> Thread 10000
```

This does not scale.

---

# 2. What is a Thread Pool?

Instead of creating new threads every time, we create a **fixed set of reusable worker threads**.

```text
                 Incoming Tasks

       T1  T2  T3  T4  T5  ...

                 ↓

        +-------------------+
        |      Queue        |
        +-------------------+

             ↓       ↓

        Worker1  Worker2  Worker3
```

Workers never die after each task.

They finish one task and immediately pick another.

This reuse is the main performance benefit.

---

# 3. Executor Framework

Java 5 introduced the Executor Framework.

Main interfaces:

```text
Executor
    ↑
ExecutorService
    ↑
ScheduledExecutorService
```

---

## Executor

Very simple interface:

```java
public interface Executor {

    void execute(Runnable command);

}
```

Instead of:

```java
new Thread(task).start();
```

Use:

```java
executor.execute(task);
```

The Executor decides **how** the task is executed.

---

# 4. ExecutorService ⭐⭐⭐⭐⭐

Adds lifecycle management.

Important methods:

```java
submit()

shutdown()

shutdownNow()

invokeAll()

invokeAny()
```

Example

```java
ExecutorService executor =
        Executors.newFixedThreadPool(3);

executor.submit(() -> {
    System.out.println("Running...");
});

executor.shutdown();
```

---

# 5. `execute()` vs `submit()` ⭐⭐⭐⭐⭐

### execute()

```java
executor.execute(task);
```

* Accepts only `Runnable`
* No return value
* Exceptions are sent to the thread's uncaught exception handler

---

### submit()

```java
Future<Integer> future =
        executor.submit(task);
```

* Accepts `Runnable` or `Callable`
* Returns a `Future`
* Exceptions are captured inside the `Future`

---

## Interview Question

**Which one should you prefer?**

Generally:

* `execute()` → fire-and-forget tasks
* `submit()` → when you need a result, exception handling, or cancellation

---

# 6. Runnable vs Callable

Runnable

```java
Runnable task = () -> {
    System.out.println("Hello");
};
```

Cannot return a value.

---

Callable

```java
Callable<Integer> task = () -> {

    return 100;

};
```

Returns a value.

Can throw checked exceptions.

---

Comparison

| Runnable                       | Callable                    |
| ------------------------------ | --------------------------- |
| No return value                | Returns value               |
| Cannot throw checked exception | Can throw checked exception |
| execute()                      | submit()                    |

---

# 7. Future ⭐⭐⭐⭐⭐

Suppose

```java
Future<Integer> future =
        executor.submit(() -> {

            Thread.sleep(2000);

            return 50;

        });
```

Later

```java
int result = future.get();
```

Diagram

```text
Submit Task

↓

Worker Thread

↓

Executing

↓

Completed

↓

Future Holds Result

↓

future.get()
```

---

Important methods

```java
get()

cancel()

isDone()

isCancelled()
```

---

# 8. ThreadPoolExecutor Internals ⭐⭐⭐⭐⭐

Almost every thread pool ultimately uses

```java
ThreadPoolExecutor
```

Constructor

```java
ThreadPoolExecutor(

corePoolSize,

maximumPoolSize,

keepAliveTime,

TimeUnit,

BlockingQueue<Runnable>

)
```

Every interview asks these parameters.

---

# 9. How ThreadPoolExecutor Works ⭐⭐⭐⭐⭐

Suppose

```text
Core Pool = 2

Maximum Pool = 4

Queue Capacity = 2
```

Now 8 tasks arrive.

---

### Task 1

Worker1 created.

---

### Task 2

Worker2 created.

---

### Task 3

Workers busy.

Task enters queue.

---

### Task 4

Queue still has space.

Task enters queue.

---

### Task 5

Queue full.

Create Worker3.

---

### Task 6

Create Worker4.

---

### Task 7

Queue full.

Workers full.

Rejected.

---

Visual Flow

```text
Task Arrives

↓

Core Thread Available?

↓

Yes

↓

Create Worker

↓

No

↓

Queue Full?

↓

No

↓

Add to Queue

↓

Yes

↓

Maximum Thread Available?

↓

Yes

↓

Create Extra Thread

↓

No

↓

Reject Task
```

**This flow is one of the most asked interview questions.**

---

# 10. Core Pool Size vs Maximum Pool Size ⭐⭐⭐⭐⭐

### Core Pool Size

Minimum number of threads kept alive.

Even when idle.

Example

```java
new ThreadPoolExecutor(

2,

10,

...
)
```

Two workers always exist.

---

### Maximum Pool Size

Maximum threads that can ever exist.

Temporary workers above the core size are removed after the keep-alive timeout.

---

# 11. Keep Alive Time

Example

```java
keepAlive = 60 seconds
```

Extra threads

```text
Core = 2

Temporary = 5
```

If temporary workers remain idle for 60 seconds,

they are removed.

Core workers remain.

---

# 12. Blocking Queues ⭐⭐⭐⭐⭐

Different queues completely change thread pool behaviour.

---

## LinkedBlockingQueue

```java
new LinkedBlockingQueue<>();
```

Usually unbounded.

Tasks wait indefinitely.

Workers rarely grow beyond the core size.

---

## ArrayBlockingQueue

Fixed capacity.

```java
new ArrayBlockingQueue<>(100);
```

When full,

the pool may create additional threads up to `maximumPoolSize`.

---

## SynchronousQueue

No storage.

Task must be immediately handed to a worker.

Used by

```java
Executors.newCachedThreadPool()
```

Very fast.

Can create many threads.

---

Comparison

| Queue               | Stores Tasks?         | Typical Use     |
| ------------------- | --------------------- | --------------- |
| LinkedBlockingQueue | Yes (often unbounded) | General purpose |
| ArrayBlockingQueue  | Yes (bounded)         | Backpressure    |
| SynchronousQueue    | No                    | Direct handoff  |

---

# 13. Rejection Policies ⭐⭐⭐⭐⭐

What happens when:

* Queue full
* Maximum threads reached

Default:

### AbortPolicy

Throws

```text
RejectedExecutionException
```

---

Other policies

### CallerRunsPolicy

Calling thread executes the task.

Good natural backpressure.

---

### DiscardPolicy

Silently drops the task.

Rarely appropriate.

---

### DiscardOldestPolicy

Removes the oldest queued task.

Adds the new task.

---

Comparison

| Policy              | Behaviour                 |
| ------------------- | ------------------------- |
| AbortPolicy         | Throw exception           |
| CallerRunsPolicy    | Caller executes           |
| DiscardPolicy       | Drop new task             |
| DiscardOldestPolicy | Remove oldest queued task |

---

# 14. `shutdown()` vs `shutdownNow()` ⭐⭐⭐⭐⭐

### shutdown()

```java
executor.shutdown();
```

* No new tasks accepted
* Existing tasks finish normally

---

### shutdownNow()

```java
executor.shutdownNow();
```

* Attempts to interrupt running tasks
* Returns queued but unstarted tasks
* No guarantee that running tasks stop immediately (they must respond to interruption)

---

# 15. Thread Pool Sizing ⭐⭐⭐⭐⭐

### CPU-Bound Tasks

Examples:

* Image processing
* Encryption
* Mathematical calculations

Rule of thumb:

```text
Threads ≈ Number of CPU cores
```

Reason:

More threads mostly increase context switching.

---

### I/O-Bound Tasks

Examples:

* Database calls
* REST API calls
* File operations

Threads spend much of their time waiting.

Rule of thumb:

```text
Threads > Number of CPU cores
```

A commonly cited formula is:

```text
Threads ≈ CPU Cores × (1 + Wait Time / Compute Time)
```

Use it as a guideline rather than a strict rule.

---

# 16. Common Mistakes

### ❌ Using `Executors.newFixedThreadPool()` blindly

It creates a pool with an **unbounded** `LinkedBlockingQueue`.

If tasks arrive faster than they are processed, the queue can grow until memory is exhausted.

For production systems, many teams prefer constructing a `ThreadPoolExecutor` explicitly with a **bounded queue**.

---

### ❌ Forgetting `shutdown()`

The JVM may not exit because pool threads remain alive.

---

### ❌ Very large thread pools

Example

```text
500 Threads

8 CPU Cores
```

Usually hurts performance due to excessive context switching.

---

# 17. Production Example

Suppose an e-commerce service:

* 500 requests/second
* Each request performs a database query

Configuration

```text
Core Pool = 20

Maximum Pool = 50

Queue = 200

CallerRunsPolicy
```

Why `CallerRunsPolicy`?

When overloaded, the request-handling thread executes the task itself, slowing down incoming requests and naturally reducing pressure on the system instead of endlessly queuing work.

---

# 18. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. Why are thread pools better than creating threads manually?

Because threads are expensive to create and destroy. Thread pools reuse worker threads, reduce memory usage, lower context-switching overhead, and allow controlled concurrency.

---

### Q2. Difference between `execute()` and `submit()`?

`execute()` is for fire-and-forget `Runnable` tasks.

`submit()` returns a `Future`, supports `Callable`, and captures exceptions.

---

### Q3. What happens when all pool threads are busy?

The task handling sequence is:

1. Use a core thread if available.
2. Otherwise, enqueue the task.
3. If the queue is full, create additional threads up to `maximumPoolSize`.
4. If the pool is at maximum and the queue is full, invoke the rejection policy.

---

### Q4. Difference between `shutdown()` and `shutdownNow()`?

`shutdown()` allows running tasks to finish.

`shutdownNow()` attempts to interrupt running tasks and returns tasks that never started.

---

### Q5. Which rejection policy is safest?

It depends on the application, but `CallerRunsPolicy` is often a good production choice because it provides natural backpressure instead of silently dropping work.

---

# 19. Interview Boundary ⭐⭐⭐⭐⭐

For a **7+ years Java Developer**, you should confidently explain:

* Why thread pools exist.
* `Executor`, `ExecutorService`, and `ThreadPoolExecutor`.
* `execute()` vs `submit()`.
* `Runnable` vs `Callable`.
* `Future`.
* Thread pool lifecycle.
* Core pool vs maximum pool.
* Keep-alive time.
* Blocking queue types.
* Rejection policies.
* Thread pool sizing strategies.
* Common production pitfalls with unbounded queues.

---

# Next Chapter Recommendation

The next logical topic is:

# **CompletableFuture & Asynchronous Programming** ⭐⭐⭐⭐⭐

We'll cover:

* Why `Future` is limited.
* `CompletableFuture` internals.
* `thenApply()`, `thenCompose()`, `thenCombine()`.
* `allOf()` and `anyOf()`.
* Exception handling (`exceptionally()`, `handle()`).
* Parallel service calls.
* Common production patterns and interview questions.

This topic is extremely common in modern Spring Boot and microservices interviews.
