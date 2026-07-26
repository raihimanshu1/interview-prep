Perfect. From here onward, we'll switch into **Senior Engineer Mode**. Every topic will be taught from **first principles**, then gradually move to **JVM internals**, **production usage**, and **interview depth**.

---

# Module 4 — Multithreading & Concurrency

## Chapter 1 — Process vs Thread ⭐⭐⭐⭐⭐

> **Everything in Concurrency starts here.**
>
> If you don't understand Process vs Thread properly, synchronization, locks, ExecutorService, CompletableFuture, ConcurrentHashMap, and even Spring Boot request handling will feel like memorization.

---

1. Process vs Thread ⭐⭐⭐⭐⭐

2. Thread Lifecycle ⭐⭐⭐⭐⭐

3. Ways to Create Threads
   ├── Thread class
   ├── Runnable
   ├── Callable
   ├── Future
   ├── FutureTask

4. Thread States
   ├── NEW
   ├── RUNNABLE
   ├── BLOCKED
   ├── WAITING
   ├── TIMED_WAITING
   └── TERMINATED

5. Synchronization ⭐⭐⭐⭐⭐
   ├── synchronized keyword
   ├── Monitor Lock
   ├── Object Lock
   ├── Class Lock
   ├── Reentrant Synchronization

6. Java Memory Model ⭐⭐⭐⭐⭐
   ├── Main Memory
   ├── Working Memory
   ├── Visibility
   ├── Happens Before
   ├── Reordering

7. volatile ⭐⭐⭐⭐⭐

8. Atomic Classes ⭐⭐⭐⭐
   ├── CAS
   ├── Unsafe
   ├── AtomicInteger
   ├── AtomicReference

9. Locks ⭐⭐⭐⭐⭐
   ├── ReentrantLock
   ├── ReadWriteLock
   ├── StampedLock

10. Thread Communication ⭐⭐⭐⭐⭐
    ├── wait()
    ├── notify()
    ├── notifyAll()
    ├── Producer Consumer

11. Executor Framework ⭐⭐⭐⭐⭐
    ├── Executor
    ├── ExecutorService
    ├── ThreadPoolExecutor
    ├── ScheduledExecutorService

12. CompletableFuture ⭐⭐⭐⭐⭐

13. Fork Join Pool ⭐⭐⭐⭐

14. Parallel Streams ⭐⭐⭐

15. Concurrent Utilities ⭐⭐⭐⭐⭐
    ├── CountDownLatch
    ├── CyclicBarrier
    ├── Semaphore
    ├── Phaser
    ├── Exchanger

16. BlockingQueue ⭐⭐⭐⭐⭐

17. Deadlock ⭐⭐⭐⭐⭐

18. Livelock

19. Starvation

20. Race Condition ⭐⭐⭐⭐⭐

21. ThreadLocal ⭐⭐⭐⭐

22. Virtual Threads (Java 21) ⭐⭐⭐⭐

23. Concurrent Collections (Overview)
    ├── ConcurrentHashMap
    ├── CopyOnWriteArrayList
    ├── ConcurrentLinkedQueue

# 1. Why This Concept Exists (The Story Behind It)

Imagine you're using your laptop.

At the same time, you're:

* Listening to Spotify
* Browsing Chrome
* Running IntelliJ IDEA
* Running a Spring Boot application
* Downloading a file
* Receiving Slack messages

Question:

**How is your computer doing all these tasks simultaneously?**

Does it have one giant program doing everything?

No.

The Operating System divides work into **Processes** and **Threads**.

Without them, your computer could only execute **one task at a time**.

---

# 2. Before Learning Threads, We Need to Understand a Process

Suppose you double-click Chrome.

What actually happens?

The operating system creates a **new process**.

```
Chrome.exe

↓

Operating System

↓

Create Process

↓

Allocate Memory

↓

Load Executable

↓

Start Execution
```

A process is simply:

> **An independent program in execution with its own allocated resources.**

Examples:

```
Chrome

IntelliJ

Spotify

Slack

Spring Boot Application

MySQL
```

Each of these is a separate process.

---

# 3. What Does a Process Contain?

When a process starts, the Operating System allocates several resources.

```
+------------------------------------------------+
|                 Java Process                   |
|                                                |
|  Heap Memory                                  |
|                                                |
|  Method Area / Metaspace                       |
|                                                |
|  Native Libraries                              |
|                                                |
|  File Handles                                  |
|                                                |
|  Network Connections                           |
|                                                |
|  Threads                                       |
|                                                |
+------------------------------------------------+
```

A process owns:

* Memory
* Open files
* Network sockets
* JVM
* Loaded classes
* Threads

Think of a process as a **container** that owns resources.

---

# 4. Then What is a Thread?

Now imagine your Spring Boot application.

A user calls:

```
GET /orders
```

Another user calls:

```
GET /products
```

Another user calls:

```
POST /payment
```

Should the application finish the first request before starting the second?

That would be terrible.

Instead, the JVM creates multiple threads.

```
Spring Boot Process

            |

-----------------------------------------

|             |             |

Thread-1   Thread-2    Thread-3

Orders     Products    Payment
```

Each thread executes a different task independently.

---

# 5. Interview Definition

## Process

> A Process is an independent program in execution with its own memory space and operating system resources.

---

## Thread

> A Thread is the smallest unit of execution inside a process. Multiple threads share the same process resources while executing different tasks concurrently.

---

# 6. Process vs Thread (Visual Understanding)

Suppose we have two Java applications.

```
+--------------------+

Java Process A

Heap

Classes

Threads

+--------------------+




+--------------------+

Java Process B

Heap

Classes

Threads

+--------------------+
```

Notice:

Nothing is shared.

Each process has its own memory.

---

Now look inside one process.

```
+------------------------------------------------------+

Java Process

Heap (Shared)

Method Area (Shared)

-------------------------------------------------------

Thread-1

Stack

PC Register

-------------------------------------------------------

Thread-2

Stack

PC Register

-------------------------------------------------------

Thread-3

Stack

PC Register

+------------------------------------------------------+
```

Important observation:

### Shared by all threads

* Heap
* Method Area (Metaspace)
* Static variables
* Objects

### Private to each thread

* Stack
* Program Counter (PC Register)
* Local variables
* Method call stack

This distinction is **fundamental**. Almost every concurrency issue comes from **shared heap memory**.

---

# 7. Why Do Threads Share Memory?

Imagine if every thread had its own copy of every object.

Example:

```
Customer

↓

OrderService

↓

DatabaseConnection

↓

Cache
```

If 100 threads each had their own copy:

* Memory usage would explode.
* Updating shared data would be impossible.

Instead, all threads share the heap.

Example:

```
                 Heap

           OrderService

                ▲

        -------------------

        ▲         ▲       ▲

     Thread1  Thread2  Thread3
```

Every thread can access the same `OrderService` object.

This is powerful, but it also introduces **race conditions**, which we'll study later.

---

# 8. Why Is a Thread Called "Lightweight"?

Creating a process requires the OS to allocate:

* New address space
* New heap
* New page tables
* New file descriptor table
* New process control block

Creating a thread is much cheaper.

The process already owns all these resources.

A thread only needs:

* Its own stack
* Program counter
* Registers
* Scheduling information

So:

```
Create Process

↓

Allocate Everything

↓

Expensive
```

Whereas:

```
Create Thread

↓

Reuse Existing Process Resources

↓

Allocate Stack

↓

Much Cheaper
```

That's why threads are called **lightweight processes**.

---

# 9. Real Production Example

Suppose your Spring Boot application receives 500 requests per second.

```
Client Requests

↓

Tomcat

↓

Thread Pool

↓

Worker Threads

↓

Controller

↓

Service

↓

Repository
```

Each request is handled by a different thread from the pool.

The application is still **one process**, but many threads execute concurrently.

This is how modern web applications serve thousands of users.

---

# 10. Process vs Thread Comparison

| Feature           | Process                          | Thread                                                         |
| ----------------- | -------------------------------- | -------------------------------------------------------------- |
| Definition        | Independent program in execution | Smallest unit of execution                                     |
| Memory            | Separate                         | Shared within the process                                      |
| Heap              | Separate                         | Shared                                                         |
| Stack             | Own stack                        | Own stack                                                      |
| Communication     | Expensive (IPC)                  | Easy (shared memory)                                           |
| Creation Cost     | High                             | Low                                                            |
| Context Switching | Expensive                        | Cheaper                                                        |
| Failure           | Usually isolated                 | One uncaught error can affect the process if not handled       |
| Managed By        | Operating System                 | JVM (mapped to native OS threads) + Operating System scheduler |

---

# 11. Common Mistakes

### ❌ "Each thread has its own heap."

False.

The heap belongs to the process and is shared.

---

### ❌ "Threads are independent."

No.

They execute independently, but they share objects and memory.

---

### ❌ "Every request creates a new process."

No.

Spring Boot typically runs as a **single JVM process** with a pool of worker threads.

---

### ❌ "Processes communicate easily."

No.

Inter-Process Communication (IPC) is much more expensive than communication between threads because processes do not share memory by default.

---

# 12. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. What is a Process?

An independent program in execution with its own memory space and operating system resources.

---

### Q2. What is a Thread?

The smallest unit of execution within a process. Threads share the process's resources but maintain their own execution state.

---

### Q3. Why are threads called lightweight?

Because they reuse the process's memory and resources, requiring only a separate stack, program counter, and scheduling information.

---

### Q4. Which memory is shared among threads?

* Heap
* Method Area (Metaspace)
* Static variables
* Objects

---

### Q5. Which memory is private to each thread?

* Stack
* Program Counter (PC Register)
* Local variables
* Method call stack

---

### Q6. Why is communication between threads faster than between processes?

Threads communicate through shared memory (the heap), whereas processes require IPC mechanisms such as sockets, pipes, or shared memory managed by the operating system.

---

### Q7. Why can multithreading introduce bugs?

Because multiple threads can access and modify the same objects in the shared heap simultaneously, leading to race conditions, visibility issues, and inconsistent state if proper synchronization is not used.

---

# 13. Interview Boundary

### Expected for a 7+ Years Java Developer

You should be able to explain:

* Process vs Thread
* Shared vs private memory
* Why threads are lightweight
* Spring Boot request handling with thread pools
* Why shared memory leads to concurrency problems

### We'll Cover Later

* Thread lifecycle
* Thread scheduling
* Context switching
* Synchronization
* Java Memory Model (JMM)
* `volatile`
* Locks
* Executor Framework

These concepts build directly on the foundation you've learned in this chapter.

# Chapter 2 — Why Multithreading? (Concurrency vs Parallelism vs Asynchronous Programming) ⭐⭐⭐⭐⭐

> **Before writing a single line of multithreaded code, you must answer one question:**
>
> **Why do we even need multiple threads?**

Many developers jump directly into `Thread`, `Runnable`, and `ExecutorService`. Senior interviewers don't. They first check whether you understand **the problem that multithreading solves**.

---

# 1. Why This Concept Exists

Imagine you're building an e-commerce application.

A customer places an order.

The application needs to:

* Save the order
* Reduce inventory
* Process payment
* Send an email
* Send an SMS
* Update analytics
* Publish a Kafka event

Should these happen one after another?

```text
Save Order
      ↓
Reduce Inventory
      ↓
Payment
      ↓
Email
      ↓
SMS
      ↓
Analytics
      ↓
Kafka
```

Suppose each operation takes **500 ms**.

Total response time:

```text
500 × 6 = 3000 ms
```

The user waits **3 seconds**.

Not good.

---

Instead, only perform the work required for the response immediately.

```text
Save Order
      ↓
Payment
      ↓
Return Success (800 ms)
```

Everything else runs in parallel.

```text
Email

SMS

Analytics

Kafka
```

Now the user receives the response much faster.

This is the motivation behind concurrency.

---

# 2. Single Thread Execution

Suppose one thread performs three tasks.

```text
Time

|-----Task A-----|

                 |-----Task B-----|

                                   |-----Task C-----|
```

Everything waits for the previous task.

CPU utilisation is poor if tasks spend time waiting for I/O (database, network, file system).

---

# 3. Multithreaded Execution

Now imagine three threads.

```text
Thread-1   |-----Task A-----|

Thread-2   |-----Task B-----|

Thread-3   |-----Task C-----|
```

Tasks overlap in time.

Total execution time is reduced if tasks are independent.

---

# 4. Does This Mean Everything Runs at the Same Time?

Not necessarily.

This brings us to one of the most common interview questions.

---

# 5. Concurrency vs Parallelism ⭐⭐⭐⭐⭐

Most developers use these words interchangeably.

They are **not** the same.

---

## Concurrency

Concurrency means:

> **Multiple tasks make progress during the same period of time.**

They may or may not execute simultaneously.

Example:

One CPU core.

```text
Time

Task A

AAAA

Task B

     BBBB

CPU

A B A B A B A B
```

The CPU rapidly switches between tasks.

To the user, both appear to progress together.

Only one instruction executes at any instant.

---

### Real Example

Your browser:

* Downloads a file.
* Plays music.
* Renders a webpage.

On a single core, the OS keeps switching between these tasks.

This is concurrency.

---

## Parallelism

Parallelism means:

> **Multiple tasks execute literally at the same instant.**

Requires multiple CPU cores.

Example:

```text
Core 1

Task A

AAAAAAAA

Core 2

Task B

BBBBBBBB
```

Both tasks execute simultaneously.

No switching between them.

---

## Visual Difference

### Concurrency

```text
Core

A

B

A

B

A

B
```

One core.

Tasks share CPU time.

---

### Parallelism

```text
Core 1

AAAAAAAA

Core 2

BBBBBBBB
```

Two cores.

True simultaneous execution.

---

# 6. Can Java Achieve Both?

Yes.

Java creates threads.

The operating system schedules them.

If the machine has:

### One Core

```text
Java Threads

↓

OS Scheduler

↓

Concurrency
```

---

### Multiple Cores

```text
Java Threads

↓

OS Scheduler

↓

Parallel Execution
```

Java doesn't decide.

The operating system does.

---

# 7. Asynchronous Programming ⭐⭐⭐⭐

Another interview favourite.

People often confuse **asynchronous** with **multithreading**.

They are different.

---

Suppose your application calls a payment service.

Synchronous:

```text
Call Payment

↓

Wait

↓

Wait

↓

Wait

↓

Receive Response
```

The thread is blocked.

---

Asynchronous:

```text
Call Payment

↓

Continue Other Work

↓

Payment Completes

↓

Callback / Future
```

The thread doesn't sit idle waiting.

---

Example:

```java
CompletableFuture<Order> future =
        paymentService.process(order);

// Continue doing other work...

Order result = future.get();
```

We'll cover `CompletableFuture` in depth later.

---

# 8. Real Production Example

A user uploads a photo.

Should the application:

```text
Upload

↓

Resize

↓

Generate Thumbnail

↓

Virus Scan

↓

Store

↓

Send Email

↓

Return Response
```

No.

Better approach:

```text
Upload

↓

Store Original

↓

Return Success
```

Then:

```text
Background Thread

↓

Resize

↓

Thumbnail

↓

Virus Scan

↓

Email
```

The user gets a fast response while background work continues.

---

# 9. Does More Threads Always Mean Better Performance?

No.

Common misconception.

Example:

Machine:

```text
4 CPU cores
```

You create:

```text
10,000 threads
```

Now the operating system spends significant time switching between threads.

This is called **context switching overhead**.

Performance may actually decrease.

We'll study this in detail later.

---

# 10. Summary Table

| Concept                  | Meaning                                | Requires Multiple Cores?                                           |
| ------------------------ | -------------------------------------- | ------------------------------------------------------------------ |
| Concurrency              | Tasks make progress together           | No                                                                 |
| Parallelism              | Tasks execute at exactly the same time | Yes                                                                |
| Asynchronous Programming | Don't wait for long-running operations | No                                                                 |
| Multithreading           | Multiple threads of execution          | No (can run concurrently on one core or in parallel on many cores) |

---

# 11. Common Mistakes

### ❌ Concurrency = Parallelism

False.

Concurrency is about **managing multiple tasks**.

Parallelism is about **executing multiple tasks simultaneously**.

---

### ❌ Async means multithreading

Not always.

Asynchronous programming can be implemented without creating additional threads, depending on the framework and event model.

---

### ❌ More threads always increase performance

False.

Too many threads increase scheduling and context-switch overhead, which can reduce throughput.

---

# 12. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. What is concurrency?

Concurrency is the ability to make multiple tasks progress during the same time period, even if only one task executes at any instant.

---

### Q2. What is parallelism?

Parallelism is the simultaneous execution of multiple tasks on different CPU cores.

---

### Q3. Can concurrency exist without parallelism?

Yes.

On a single-core machine, tasks execute concurrently through rapid context switching but not in parallel.

---

### Q4. Can parallelism exist without concurrency?

In practical software systems, parallel execution is usually a form of concurrency, but conceptually, parallelism specifically refers to simultaneous execution across multiple processing units.

---

### Q5. Is asynchronous programming the same as multithreading?

No.

Asynchronous programming is about **not blocking while waiting for an operation to complete**. It may use multiple threads, event loops, or non-blocking I/O depending on the implementation.

---

### Q6. Why doesn't creating more threads always improve performance?

Because threads consume memory and CPU scheduling time. Beyond a certain point, context switching and contention outweigh any benefit.

---

# 13. Interview Boundary

### Expected for a 7+ Years Java Developer

You should confidently explain:

* Why multithreading exists.
* Concurrency vs parallelism with real examples.
* Asynchronous vs synchronous execution.
* Why more threads are not always better.
* How these concepts apply to Spring Boot request handling and backend services.

### Next Chapter

Now that you understand **why** we need multithreading, we'll study **How Java Creates Threads**:

* `Thread` class
* `Runnable`
* `Callable`
* `Future`
* Why `Runnable` is preferred over extending `Thread`
* What actually happens when you call `start()`
* `start()` vs `run()` (a very common interview question)
