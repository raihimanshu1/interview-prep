Excellent. Now we move into one of the most important chapters of the entire concurrency module.

This chapter explains **why multithreading is not free**. It also explains why creating **1000 threads** doesn't make your application 1000x faster.

---

# Module 4 — Chapter 6

# Thread Scheduling & Context Switching ⭐⭐⭐⭐⭐

> **Senior Interview Weight:** ⭐⭐⭐⭐⭐
>
> This topic is asked frequently because it connects Java, the JVM, and the Operating System.

---

# 1. Why This Concept Exists

Imagine your laptop has:

* Chrome
* IntelliJ
* Spotify
* Slack
* MySQL
* Docker
* Spring Boot

All running simultaneously.

Question:

**How does the CPU decide which thread should execute next?**

Does Java decide?

No.

Does the JVM decide?

Not really.

The **Operating System Scheduler** decides.

Understanding this scheduler is the foundation of multithreading.

---

# 2. Who Controls Thread Execution?

Many beginners think:

```text
Java Thread

↓

Runs on CPU
```

That's incorrect.

The actual flow is:

```text
                    Java Application

                           │

                     Thread.start()

                           │

                           ▼

                    JVM (HotSpot)

                           │

Creates Native Thread (OS Thread)

                           │

                           ▼

                Operating System Scheduler

                           │

                           ▼

                         CPU Core
```

**Important**

Java creates threads.

The **Operating System decides when they actually execute.**

This distinction is asked very often in interviews.

---

# 3. Imagine Only One CPU Core

Suppose your machine has:

```text
1 CPU Core
```

Three Java threads are ready.

```text
Thread A

Thread B

Thread C
```

Can all execute together?

No.

Only one instruction executes on one CPU core at a time.

---

The scheduler rapidly switches between them.

```text
Time →

CPU

AAAA

BBBB

CC

AAA

BB

CCCC

AA
```

To us,

it looks simultaneous.

Actually,

only one thread executes at any instant.

This is **Concurrency**.

---

# 4. Multiple CPU Cores

Now suppose:

```text
4 CPU Cores
```

```text
             CPU

+--------+--------+--------+--------+

 Core 1   Core 2   Core 3   Core 4

 ThreadA  ThreadB  ThreadC  ThreadD

+--------+--------+--------+--------+
```

Now multiple threads execute literally at the same time.

This is **Parallelism**.

---

# 5. What is Thread Scheduling?

Thread Scheduling means:

> **Selecting which runnable thread gets CPU time next.**

Imagine five people waiting for one ATM.

```text
Thread A

↓

Thread B

↓

Thread C

↓

Thread D

↓

Thread E
```

Only one can use it.

The scheduler decides:

* Who goes first
* How long they use it
* Who goes next

CPU scheduling works similarly.

---

# 6. What is Context Switching? ⭐⭐⭐⭐⭐

This is the most important concept in this chapter.

Suppose:

```text
CPU executing Thread A
```

Suddenly,

the scheduler decides:

```text
Run Thread B instead.
```

Can it simply start Thread B?

No.

First,

it must preserve Thread A's current execution state.

---

Imagine reading a book.

You stop on page 157.

Before switching to another book,

you insert a bookmark.

Later,

you reopen at page 157.

That's exactly what context switching does.

---

Diagram

```text
CPU

↓

Running Thread A

↓

Save State

↓

Load Thread B State

↓

Run Thread B
```

---

# 7. What is Saved During Context Switching?

A thread contains execution information.

The OS must save everything.

```text
Thread Context

-------------------------

Program Counter

CPU Registers

Stack Pointer

Thread State

CPU Flags

-------------------------
```

Then load another thread's context.

```text
Save Thread A

↓

Load Thread B

↓

Resume Execution
```

The thread continues exactly where it stopped.

---

# 8. Visual Example

Suppose

Thread A

```java
int x = 10;

x++;

x++;

System.out.println(x);
```

Execution stops after

```java
x++;
```

Current value

```text
x = 11
```

Scheduler switches.

Later,

Thread A resumes.

It should continue from

```java
x++;
```

not from the beginning.

The saved context allows this.

---

# 9. Why Context Switching is Expensive

Many developers think

Switch Thread

↓

Instant

Not true.

Every switch requires:

```text
Save Registers

↓

Save Program Counter

↓

Save Stack Pointer

↓

Kernel Mode Transition

↓

Load Another Thread

↓

Reload CPU Cache

↓

Resume
```

None of this does useful business work.

It's overhead.

---

# 10. CPU Cache Effect ⭐⭐⭐⭐⭐

Modern CPUs are extremely fast because of caches.

```text
CPU

↓

L1 Cache

↓

L2 Cache

↓

L3 Cache

↓

RAM
```

Suppose

Thread A has warmed up the cache.

Now scheduler switches to Thread B.

Thread B accesses completely different memory.

The CPU cache becomes less useful.

This is called **cache pollution** (or cache invalidation effects in practice).

Performance decreases.

This is another hidden cost of excessive context switching.

---

# 11. Why Too Many Threads Reduce Performance

Machine:

```text
8 CPU Cores
```

You create:

```text
20 Threads
```

Reasonable.

Now create:

```text
20,000 Threads
```

The scheduler spends significant time switching.

Diagram

```text
Useful Work

███████████████

Context Switching

█
```

Healthy system.

---

Too many threads

```text
Useful Work

████

Context Switching

██████████████
```

The CPU spends more time switching than doing useful work.

This is called **thread thrashing**.

---

# 12. Time Slice (Quantum)

The scheduler doesn't let one thread run forever.

Example

```text
Time Slice = 5 ms
```

```text
0ms --------5ms--------10ms-------15ms

Thread A

Thread B

Thread C
```

After the quantum expires,

another runnable thread may get CPU time.

The exact scheduling policy depends on the operating system.

---

# 13. Thread Priority

Java allows priorities.

```java
Thread t = new Thread();

t.setPriority(Thread.MAX_PRIORITY);
```

Values

```text
1

↓

5 (Default)

↓

10
```

Interview trap:

**Does priority guarantee execution first?**

No.

Priority is only a hint to the operating system.

Modern operating systems may ignore or reinterpret it.

Never rely on thread priority for program correctness.

---

# 14. Production Example

Imagine a web server.

```text
Incoming Requests

↓

Thread Pool

↓

100 Worker Threads

↓

8 CPU Cores
```

Only eight threads execute simultaneously.

The remaining runnable threads wait for CPU time.

The scheduler continuously switches between them.

---

# 15. Common Mistakes

### ❌ Java decides thread execution

Wrong.

The operating system scheduler decides which runnable thread gets CPU time.

---

### ❌ More threads always improve performance

Wrong.

Beyond an optimal point, context switching and memory overhead reduce throughput.

---

### ❌ Thread priority guarantees execution

False.

It is only a scheduling hint.

---

### ❌ Context switching is free

No.

Saving and restoring execution context, kernel transitions, and cache effects all add overhead.

---

# 16. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. Who schedules Java threads?

The JVM creates Java threads and maps them to native OS threads (on modern HotSpot JVMs). The operating system scheduler decides when each runnable thread executes.

---

### Q2. What is context switching?

Context switching is the process of saving the execution state of the currently running thread and restoring the state of another thread so the CPU can switch execution.

---

### Q3. Why is context switching expensive?

Because the operating system must save registers, the program counter, stack pointer, and other execution state, perform scheduler work, and the CPU may lose cache locality.

---

### Q4. What information is saved during a context switch?

Typically:

* Program Counter (PC)
* CPU registers
* Stack pointer
* Thread state
* Scheduling information

---

### Q5. Why doesn't creating 10,000 threads improve performance?

A limited number of CPU cores can execute threads simultaneously. Excessive threads increase memory usage and context-switch overhead, often reducing overall performance.

---

### Q6. Does `Thread.setPriority()` guarantee execution order?

No. It provides only a hint to the operating system scheduler and should never be relied upon for correctness.

---

# 17. Senior Interview Scenario ⭐⭐⭐⭐⭐

**Interviewer:**

> Your application has only 8 CPU cores, but you created 5,000 worker threads. Why did throughput decrease instead of increase?

**Expected Answer:**

Only eight threads can execute at the same time. The remaining threads compete for CPU time, causing frequent context switching. Each context switch incurs CPU overhead and reduces cache efficiency, so the processor spends more time scheduling threads than executing application logic. This is why modern Java applications prefer thread pools with a bounded number of worker threads instead of creating unbounded threads.

---

# 18. Interview Boundary

For a **7+ years Java Developer**, you should be able to explain:

* How Java threads are scheduled.
* The JVM's role versus the operating system's role.
* What a context switch is.
* Why context switching is expensive.
* Why CPU caches matter for thread performance.
* Why thread pools outperform creating thousands of threads.

---

## Next Chapter (Extremely Important)

We'll move to **Synchronization & Race Conditions**, where everything starts to connect:

* Why shared heap memory is dangerous.
* Race conditions with real examples.
* Critical sections.
* Monitor locks.
* The `synchronized` keyword.
* Object lock vs Class lock.
* Intrinsic locks.
* Reentrancy.
* How the JVM implements synchronization.

This is one of the deepest and most frequently asked topics in senior Java interviews.
