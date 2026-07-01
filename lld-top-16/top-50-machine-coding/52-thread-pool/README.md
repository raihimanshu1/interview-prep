# 🧵 Problem 52: Thread Pool (Executor Service)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any backend company  
> **Est. Time**: 90 min | **Patterns**: Thread Pool, Producer-Consumer, Factory

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a thread pool for executing tasks asynchronously."

**What the interviewer tests**:
```
1. Can you reuse threads? (Avoid thread creation overhead)
2. Can you queue tasks? (When all threads busy)
3. Can you handle different priorities? (High, normal, low)
4. Can you handle rejection? (When queue full)
```

### Step 2: The "Aha!" Moment

The key insight: **Thread creation is expensive, pool them.**

```
WITHOUT POOL:
  Task 1: Create thread (1ms) → execute (10ms) → destroy
  Task 2: Create thread (1ms) → execute (10ms) → destroy
  Total: 22ms

WITH POOL (10 threads):
  Thread 1: execute Task 1 (10ms)
  Thread 2: execute Task 2 (10ms)
  Total: 10ms
  
SAVINGS: 2x faster + less memory!
```

### Step 3: Thread pool configuration?

```
CORE POOL SIZE: 10 (always alive)
MAX POOL SIZE: 20 (spawns under load)
QUEUE SIZE: 100 (tasks waiting)
KEEP ALIVE: 60s (idle threads die)

SCENARIOS:
  - 5 tasks: 5 threads handle, 5 idle
  - 15 tasks: 15 threads, queue 0
  - 50 tasks: 20 threads max, 30 queued
  - 150 tasks: 20 threads + 100 queued + 30 rejected
```

---

## 💻 Core Implementation

```java
package com.threadpool;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: ThreadPool manages worker threads and task queue.
 * 
 * Similar to java.util.concurrent.ThreadPoolExecutor.
 */
public class ThreadPool {
    
    private final int corePoolSize;
    private final int maxPoolSize;
    private final int queueSize;
    private final long keepAliveTime;
    
    private final BlockingQueue<Runnable> taskQueue;
    private final Set<Worker> workers;
    private final RejectionHandler rejectionHandler;
    
    private volatile boolean isRunning;
    private final Object lock = new Object();
    private int activeThreads;

    private ThreadPool(int corePoolSize, int maxPoolSize, 
                       int queueSize, long keepAliveTime) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.queueSize = queueSize;
        this.keepAliveTime = keepAliveTime;
        this.taskQueue = new ArrayBlockingQueue<>(queueSize);
        this.workers = ConcurrentHashMap.newKeySet();
        this.rejectionHandler = new DefaultRejectionHandler();
        this.isRunning = true;
        this.activeThreads = 0;
        
        // Start core threads
        for (int i = 0; i < corePoolSize; i++) {
            createWorker();
        }
    }

    /**
     * INTUITION: Submit task to pool.
     * 
     * Flow:
     * 1. If active threads < core: create new thread
     * 2. Else: add to queue
     * 3. If queue full: create new thread (up to max)
     * 4. If max reached: reject
     */
    public void execute(Runnable task) {
        if (!isRunning) {
            throw new IllegalStateException("Pool is shutdown");
        }
        
        synchronized (lock) {
            // Try to create new thread if below core
            if (activeThreads < corePoolSize) {
                createWorker(task);
                return;
            }
            
            // Add to queue
            if (taskQueue.offer(task)) {
                return;
            }
            
            // Queue full, try to create new thread (up to max)
            if (activeThreads < maxPoolSize) {
                createWorker(task);
                return;
            }
            
            // Reject
            rejectionHandler.rejected(task);
        }
    }

    /**
     * INTUITION: Submit task with Future (for result).
     */
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = new FutureTask<>(task);
        execute(future);
        return future;
    }

    /**
     * Create new worker thread.
     */
    private void createWorker(Runnable task) {
        Worker worker = new Worker(task);
        workers.add(worker);
        activeThreads++;
        
        Thread thread = new Thread(worker, "Worker-" + activeThreads);
        thread.start();
    }

    private void createWorker() {
        createWorker(null);
    }

    /**
     * Shutdown pool gracefully.
     */
    public void shutdown() {
        isRunning = false;
        
        // Interrupt idle workers
        for (Worker worker : workers) {
            worker.interrupt();
        }
        
        // Drain queue
        taskQueue.clear();
    }

    /**
     * Shutdown immediately.
     */
    public void shutdownNow() {
        isRunning = false;
        
        // Interrupt all workers
        for (Worker worker : workers) {
            worker.interrupt();
        }
        
        // Clear queue
        taskQueue.clear();
    }

    // --- Getters ---

    public int getActiveThreads() { 
        synchronized (lock) {
            return activeThreads;
        }
    }
    public int getQueueSize() { return taskQueue.size(); }
}

/**
 * Worker thread.
 */
class Worker implements Runnable {
    private final Thread thread;
    private volatile boolean running;
    private Runnable task;

    Worker(Runnable task) {
        this.thread = new Thread(this);
        this.task = task;
        this.running = true;
    }

    @Override
    public void run() {
        while (running && !thread.isInterrupted()) {
            try {
                // Execute initial task
                if (task != null) {
                    runTask(task);
                    task = null;
                }
                
                // Poll for more tasks
                Runnable nextTask = threadPool.taskQueue.poll(1, TimeUnit.SECONDS);
                if (nextTask != null) {
                    runTask(nextTask);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void runTask(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            System.err.println("Task failed: " + e.getMessage());
        }
    }

    public void interrupt() {
        thread.interrupt();
    }
}

/**
 * Rejection handler.
 */
interface RejectionHandler {
    void rejected(Runnable task);
}

class DefaultRejectionHandler implements RejectionHandler {
    @Override
    public void rejected(Runnable task) {
        throw new RejectedExecutionException("Task rejected: pool exhausted");
    }
}

class CallerRunsPolicy implements RejectionHandler {
    @Override
    public void rejected(Runnable task) {
        // Run in caller's thread
        task.run();
    }
}

/**
 * FutureTask wrapper.
 */
class FutureTask<T> implements Runnable, java.util.concurrent.Future<T> {
    private final Callable<T> callable;
    private T result;
    private Exception exception;
    private volatile boolean done;
    private volatile boolean cancelled;

    FutureTask(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    public void run() {
        try {
            result = callable.call();
            done = true;
        } catch (Exception e) {
            exception = e;
            done = true;
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        while (!done && !cancelled) {
            Thread.sleep(10);
        }
        
        if (cancelled) {
            throw new CancellationException("Task cancelled");
        }
        
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        
        return result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.cancelled = true;
        return true;
    }

    @Override
    public boolean isDone() { return done || cancelled; }
    @Override
    public boolean isCancelled() { return cancelled; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle task priority?"
> "PriorityQueue for task queue. Priority levels: HIGH, NORMAL, LOW."

### Q2: "How to prevent thread starvation?"
> "Minimum threads guarantee. Timeout for long-running tasks. Monitoring."

### Q3: "How to handle rejected tasks?"
> "Policies: Abort, CallerRuns, Discard, DiscardOldest."

### Q4: "How to monitor pool health?"
> "Metrics: active threads, queue size, completed tasks, rejected count."