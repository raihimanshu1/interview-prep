# Atomic Classes Internals — CAS, Unsafe, VarHandle, ABA Problem, LongAdder

This is one of the most important advanced concurrency topics.

For a **7+ years Java backend interview**, don't stop at:

> "AtomicInteger provides lock-free thread-safe operations."

The interviewer will usually ask:

* How does AtomicInteger work internally?
* Why is CAS better than synchronized?
* What is Unsafe?
* What replaced Unsafe?
* How does compareAndSet work?
* What is ABA problem?
* Why was LongAdder introduced?

Let's go deep.

---

# 1. Problem Before Atomic Classes

Consider a simple counter.

```java
public class Counter {

    private int count = 0;

    public void increment() {
        count++;
    }

}
```

Looks simple.

But:

```java
count++;
```

is NOT one operation.

It is actually:

```
Read
 |
 v
count = 10


Increment
 |
 v
11


Write
 |
 v
count = 11
```

Three steps.

---

# 2. Race Condition Example

Two threads:

```
Initial count = 0
```

Thread A:

```
read count
0
```

Thread B:

```
read count
0
```

Thread A:

```
increment
1

write
```

Thread B:

```
increment
1

write
```

Final:

```
count = 1
```

Expected:

```
count = 2
```

Lost update.

---

# 3. Solution 1: synchronized

We can fix:

```java
public synchronized void increment() {

    count++;

}
```

Now:

```
Thread A

lock

increment

unlock


Thread B

lock

increment

unlock
```

Correct.

But:

## Problem

Only one thread executes.

```
Thread A
   |
   |
 critical section


Thread B waiting


Thread C waiting

```

For highly concurrent systems this creates contention.

---

# 4. Atomic Classes

Java introduced:

```
java.util.concurrent.atomic
```

Examples:

```
AtomicInteger

AtomicLong

AtomicBoolean

AtomicReference

AtomicStampedReference

LongAdder
```

Example:

```java
AtomicInteger counter =
        new AtomicInteger(0);


counter.incrementAndGet();
```

No explicit lock.

How?

Answer:

**CAS (Compare And Swap)**

---

# 5. CAS — Compare And Swap

CAS is a CPU-level atomic instruction.

The idea:

> Update a value only if it has not changed since I read it.

Syntax:

```
CAS(
    memory location,
    expected value,
    new value
)
```

Example:

Current:

```
count = 10
```

Thread reads:

```
expected = 10
```

Wants:

```
new value = 11
```

CAS:

```
if(current == expected)

        update

else

        fail
```

---

# 6. CAS Flow

Without CAS:

```
Thread A

read 10


Thread B

read 10


Thread A

write 11


Thread B

write 11

```

Lost update.

---

With CAS:

```
Thread A

expected = 10

CAS(10,11)

SUCCESS


Thread B

expected = 10

CAS(10,11)


current value is 11


FAIL

```

Thread B retries.

---

# 7. CAS is Optimistic Locking

Compare:

Database optimistic locking:

```
UPDATE account

SET balance = 500

WHERE version = 10
```

If version changed:

```
FAIL
```

CAS is the same idea.

---

# 8. AtomicInteger Internals

Let's look conceptually.

Example:

```java
AtomicInteger count =
        new AtomicInteger(0);


count.incrementAndGet();
```

Internally:

```java
public final class AtomicInteger {


    private volatile int value;


    public final int incrementAndGet(){

        return unsafe.getAndAddInt(
                this,
                valueOffset,
                1
        );

    }

}
```

Important:

```
value is volatile
```

Why?

Because all threads must see latest value.

---

# 9. Unsafe Class

Older Java implementations use:

```
sun.misc.Unsafe
```

It provides low-level JVM operations.

Examples:

```
memory access

CAS operations

object creation

park/unpark

```

Example:

```java
Unsafe.compareAndSwapInt()
```

This directly calls CPU CAS instruction.

---

# 10. How Unsafe Gets Memory Address

Problem:

Java does not allow:

```
object memory address manipulation
```

Normally.

But AtomicInteger needs:

```
where is value field located?
```

Example:

```
AtomicInteger object

+----------------+

 value field

+----------------+

```

Unsafe finds offset:

```
valueOffset = 12 bytes
```

Then:

```
object + offset

= actual memory location
```

---

# 11. CAS Internally

Conceptually:

```java
boolean compareAndSet(
        int expected,
        int update
){

    return unsafe.compareAndSwapInt(
            this,
            valueOffset,
            expected,
            update
    );

}
```

CPU executes:

```
LOCK CMPXCHG instruction
```

on x86 architecture.

The operation is atomic.

---

# 12. Why CAS Does Not Need Locks?

Because CPU provides atomicity.

Imagine:

```
Multiple threads


        |
        |
        v


CPU instruction


        |
        |
        v


Single atomic update

```

No lock object.

No thread blocking.

---

# 13. CAS vs synchronized

| Feature             | synchronized  | CAS            |
| ------------------- | ------------- | -------------- |
| Lock                | Yes           | No             |
| Blocking            | Yes           | No             |
| Context switch      | Possible      | No             |
| Kernel involvement  | Possible      | No             |
| Contention handling | Poor          | Retry          |
| Best for            | Complex logic | Simple updates |

---

# 14. CAS Retry Loop

Atomic operations usually use:

```
do while
```

Example:

```java
public int increment(){

    int current;

    do {

        current = value;

    } while(
        !compareAndSet(
            current,
            current + 1
        )
    );


    return current + 1;

}
```

Flow:

```
Read value

    |
    v

Try CAS

    |
    |
 SUCCESS
    |
    v

Done


FAIL

Retry

```

---

# 15. The ABA Problem

Very important interview topic.

CAS checks:

```
value is same?
```

But what if value changes and comes back?

Example:

Initial:

```
A
```

Thread 1:

```
reads A
```

Thread 2:

```
A -> B

B -> A
```

Thread 1:

```
CAS(A,C)
```

CAS succeeds.

But something changed!

Timeline:

```
Thread 1

Read A


          Thread 2

          A -> B

          B -> A



Thread 1

CAS A -> C

SUCCESS

```

Problem:

CAS only checks value.

Not history.

---

# 16. Solution: AtomicStampedReference

Java provides:

```java
AtomicStampedReference
```

Instead of:

```
value
```

store:

```
(value, version)
```

Example:

Initial:

```
(A,1)
```

Thread 2:

```
A -> B

version 2


B -> A

version 3
```

Now:

```
(A,3)
```

Thread 1 expects:

```
(A,1)
```

CAS fails.

---

# 17. VarHandle — Modern Replacement for Unsafe

Java 9 introduced:

```
VarHandle
```

Reason:

Unsafe is dangerous.

VarHandle provides:

* safe memory access
* CAS
* atomic operations
* memory ordering

Example:

```java
private volatile int count;


private static final VarHandle COUNT;


static {

    COUNT =
      MethodHandles.lookup()
      .findVarHandle(
          Counter.class,
          "count",
          int.class
      );

}
```

CAS:

```java
COUNT.compareAndSet(
        this,
        0,
        1
);
```

---

# 18. VarHandle Memory Ordering

VarHandle supports:

## Plain

Normal access.

```
No guarantees
```

---

## Opaque

Weak ordering.

```
Visibility eventually
```

---

## Acquire

Read barrier.

---

## Release

Write barrier.

---

## Volatile

Strongest.

Equivalent to volatile variable access.

---

# 19. AtomicInteger vs LongAdder

Very common question.

Suppose:

```
1000 threads

all increment same counter
```

AtomicInteger:

```
        Counter

           |
           |

        CAS


Thread1 success

Thread2 retry

Thread3 retry

```

High contention.

---

LongAdder solution:

Instead of one counter:

```
        Total


          |
 ----------------

 Cell1 Cell2 Cell3 Cell4


```

Threads update different cells.

Later:

```
sum()

Cell1+
Cell2+
Cell3+
Cell4

```

---

# 20. LongAdder Internals

Internally:

```
LongAdder

base value


+

Cell array

```

Low contention:

```
update base
```

High contention:

```
create cells

spread updates

```

Uses:

```
Striped64
```

internally.

---

# 21. AtomicInteger vs LongAdder

|                | AtomicInteger | LongAdder          |
| -------------- | ------------- | ------------------ |
| Accuracy       | Exact         | Exact              |
| CAS contention | High          | Low                |
| Memory         | Low           | Higher             |
| sum() cost     | O(1)          | O(number of cells) |
| Best use       | IDs, counters | Metrics            |

---

Example:

## Request counter

Good:

```java
LongAdder requests =
        new LongAdder();
```

because:

```
10 million requests/sec
```

---

## Sequence number

Need:

```
1
2
3
4
```

Use:

```java
AtomicInteger
```

because LongAdder cannot provide ordering.

---

# 22. False Sharing

Advanced interview topic.

CPU cache works in cache lines.

Usually:

```
64 bytes
```

Example:

```
Cache Line


Counter A

Counter B

```

Two threads:

```
Thread 1 updates A

Thread 2 updates B

```

Even though independent:

CPU invalidates whole cache line.

Result:

```
performance degradation
```

---

LongAdder reduces this by spreading cells.

---

# Senior Interview Summary

## CAS

> CAS is a lock-free atomic CPU operation that updates a value only if it matches an expected value. Java atomic classes use CAS internally to provide thread-safe updates without blocking.

---

## Unsafe

> Unsafe provides low-level JVM operations including direct memory access and CAS operations. Older atomic implementations relied on Unsafe to invoke CPU atomic instructions.

---

## VarHandle

> VarHandle is the safer modern API introduced in Java 9 that provides atomic and memory ordering operations without relying directly on Unsafe.

---

## ABA Problem

> ABA occurs when a value changes from A to B and back to A. CAS sees the same value and incorrectly succeeds. AtomicStampedReference solves this by adding version information.

---

## LongAdder

> LongAdder improves performance under high contention by distributing updates across multiple cells instead of forcing all threads to update a single CAS variable.

---

Next chapter:

# **AQS (AbstractQueuedSynchronizer) Internals — The Heart of ReentrantLock, Semaphore, CountDownLatch**

This is arguably the deepest concurrency topic in Java interviews because many concurrency utilities are built on AQS.

# AQS (AbstractQueuedSynchronizer) Internals — The Heart of ReentrantLock, Semaphore, CountDownLatch

This is one of the **most important senior-level Java concurrency topics**.

Many developers know:

```java
lock.lock();
lock.unlock();
```

But interviewers at 7+ years often ask:

* How does ReentrantLock work internally?
* How does a thread wait when a lock is unavailable?
* Does ReentrantLock use synchronized internally?
* What is AQS?
* What is the CLH queue?
* How does park/unpark work?
* How do Semaphore and CountDownLatch use the same mechanism?

Understanding AQS answers all of these.

---

# 1. What is AQS?

AQS stands for:

**AbstractQueuedSynchronizer**

Package:

```java
java.util.concurrent.locks.AbstractQueuedSynchronizer
```

It is a framework provided by Java to build synchronization primitives.

Examples built using AQS:

```text
ReentrantLock
Semaphore
CountDownLatch
ReentrantReadWriteLock
FutureTask
```

Think of AQS as:

> A reusable engine for managing threads waiting for a shared resource.

---

# 2. Why Was AQS Created?

Before AQS:

Every developer had to implement:

* waiting threads
* queues
* blocking
* waking threads
* state management

Example:

Building a lock requires:

```
Thread tries lock

        |
        v

Resource unavailable

        |
        v

Put thread somewhere

        |
        v

Suspend thread

        |
        v

Wake it later
```

This logic is common.

Doug Lea designed AQS to provide this common infrastructure.

---

# 3. High-Level AQS Architecture

AQS maintains two main things:

## 1. State

A volatile integer:

```java
private volatile int state;
```

Meaning depends on implementation.

Example:

ReentrantLock:

```
state = 0

lock free
```

```
state = 1

locked once
```

```
state = 3

same thread acquired lock 3 times
```

---

## 2. Waiting Queue

Threads waiting for the resource.

Architecture:

```
                 AQS


              state

                |
                |

        Waiting Queue


        HEAD
         |
         |
      Thread A
         |
         |
      Thread B
         |
         |
      Thread C
        TAIL

```

---

# 4. AQS State Concept

AQS itself does not know what state means.

The subclass decides.

Example:

## ReentrantLock

State:

```
0 = unlocked

1 = locked
```

---

## Semaphore

State:

```
number of permits available
```

Example:

```java
Semaphore semaphore =
        new Semaphore(3);
```

State:

```
state = 3
```

After acquire:

```
state = 2
```

---

## CountDownLatch

State:

```
remaining count
```

Example:

```java
CountDownLatch latch =
        new CountDownLatch(5);
```

State:

```
state = 5
```

After countDown:

```
state decreases
```

---

# 5. AQS Template

AQS provides methods:

```java
acquire()
release()
```

Internally:

```
acquire()

     |
     |
tryAcquire()

     |
     |
success?
     |
     |
YES ---> continue

NO

     |
     |
queue thread

     |
     |
park thread

```

The subclass implements:

```java
tryAcquire()
tryRelease()
```

---

# 6. ReentrantLock Internals

Let's understand:

```java
Lock lock =
        new ReentrantLock();

lock.lock();

criticalSection();

lock.unlock();
```

---

Internally:

```
ReentrantLock

       |
       |
       v

Sync extends AQS

       |
       |
       v

state variable

```

---

# 7. First Thread Acquires Lock

Initial:

```
state = 0
owner = null
```

Thread A:

```java
lock.lock();
```

Flow:

```
Thread A

     |
     |
acquire()

     |
     |
tryAcquire()

     |
     |
CAS state 0 -> 1

     |
     |
SUCCESS

```

Now:

```
state = 1

owner = Thread A

```

---

# 8. Second Thread Tries Lock

Thread B:

```java
lock.lock();
```

Flow:

```
Thread B

    |
    |
tryAcquire()

    |
    |
state == 1

    |
    |
FAIL

```

Now AQS queues Thread B.

---

# 9. AQS Queue Internals

The queue is based on:

**CLH Queue**

(Craig, Landin, Hagersten)

Conceptually:

```
HEAD


 |
 |
Node(Thread B)

 |
 |
Node(Thread C)

 |
 |
TAIL

```

Each waiting thread gets a Node.

Node contains:

```java
class Node {

    Thread thread;

    Node prev;

    Node next;

    int waitStatus;

}
```

---

# 10. What Happens After Queueing?

Thread B:

```
Queue

HEAD
 |
 |
Thread B

```

Now it cannot keep running.

It gets parked.

---

# 11. park() Mechanism

AQS uses:

```java
LockSupport.park()
```

Example:

```java
LockSupport.park();
```

Meaning:

Suspend current thread.

Flow:

```
Thread B

RUNNING

    |
    |
park()

    |
    |
WAITING

```

The OS does not schedule it.

CPU is free.

---

# 12. Thread A Releases Lock

Thread A:

```java
lock.unlock();
```

Flow:

```
unlock()

    |
    |
tryRelease()

    |
    |
state = 0

    |
    |
find waiting thread

    |
    |
unpark()

```

---

# 13. unpark()

AQS calls:

```java
LockSupport.unpark(thread);
```

Thread B:

```
WAITING

     |
     |
unpark()

     |
     |
RUNNABLE

```

Eventually:

```
Thread B acquires lock
```

---

# 14. Complete ReentrantLock Flow

Let's visualize:

## Thread A

```
lock()

 |
 v

CAS state 0->1

 |
 v

Acquire lock

 |
 v

execute code

 |
 v

unlock()

```

---

## Thread B

```
lock()

 |
 v

CAS fails

 |
 v

Create Node

 |
 v

Add to AQS queue

 |
 v

park()

 |
 v

wait

 |
 v

unpark()

 |
 v

retry acquire

 |
 v

success

```

---

# 15. Why Reentrant?

Reentrant means:

> Same thread can acquire the same lock multiple times.

Example:

```java
lock.lock();

method1();

lock.unlock();


void method1(){

    lock.lock();

    method2();

    lock.unlock();

}
```

Without reentrancy:

Deadlock.

---

Internally:

First acquisition:

```
state = 1
owner = Thread A
```

Second acquisition:

```
same owner

state++

```

Example:

```
state = 2
```

Unlock:

```
state--
```

Only when:

```
state = 0
```

is lock released.

---

# 16. Fair vs Non-Fair ReentrantLock

Creation:

```java
new ReentrantLock(true);
```

Fair lock.

---

## Non Fair (default)

Thread can jump queue.

Example:

```
Queue:

Thread B
Thread C


Thread D arrives


Thread D gets lock first

```

Higher throughput.

---

## Fair

FIFO ordering.

```
Thread B

then

Thread C

then

Thread D

```

Less starvation.

But slower.

---

# 17. AQS Shared vs Exclusive Mode

Very important.

AQS supports two modes.

---

# Exclusive Mode

Only one thread owns resource.

Examples:

```
ReentrantLock
```

Example:

```
Thread A

LOCKED


Thread B waiting

Thread C waiting

```

---

# Shared Mode

Multiple threads can acquire.

Examples:

```
Semaphore

CountDownLatch

ReadLock
```

Example:

Semaphore:

```
permits = 3


Thread A acquire

Thread B acquire

Thread C acquire


Thread D waits

```

---

# 18. Semaphore Internals

Example:

```java
Semaphore semaphore =
        new Semaphore(3);
```

AQS state:

```
state = 3
```

Thread:

```java
semaphore.acquire();
```

Internally:

```
CAS

state 3 -> 2

success
```

Another:

```
state 2 -> 1
```

Another:

```
state 1 -> 0
```

Next:

```
state = 0

queue thread

park
```

Release:

```java
semaphore.release();
```

State:

```
0 -> 1
```

Wake waiting thread.

---

# 19. CountDownLatch Internals

Example:

```java
CountDownLatch latch =
        new CountDownLatch(3);
```

State:

```
3
```

Three workers:

```
Worker1 complete

countDown()

state=2


Worker2 complete

state=1


Worker3 complete

state=0

```

When:

```
state == 0
```

waiting threads are released.

---

# 20. Condition Variables

ReentrantLock supports:

```java
Condition condition =
        lock.newCondition();
```

Example:

Producer Consumer:

```
Producer

lock

add item

signal


Consumer

lock

await

```

Internally:

AQS maintains another queue:

```
Lock Queue

Thread A
Thread B


Condition Queue

Thread C
Thread D

```

When:

```java
signal()
```

Thread moves:

```
Condition Queue

        |
        v

AQS Queue

```

Then competes for lock.

---

# 21. AQS vs synchronized

|                     | synchronized | AQS                 |
| ------------------- | ------------ | ------------------- |
| Implemented by      | JVM          | Java library        |
| Lock type           | Monitor      | Custom synchronizer |
| Queue control       | JVM          | AQS                 |
| Fairness            | No control   | Supported           |
| Multiple conditions | No           | Yes                 |
| tryLock             | No           | Yes                 |
| Interruptible lock  | No           | Yes                 |

---

# 22. Interview Questions

## Q1. Does ReentrantLock use synchronized internally?

Answer:

No.

ReentrantLock is built on AQS and uses CAS + LockSupport.

---

## Q2. How does a waiting thread consume CPU?

Answer:

It does not continuously spin.

AQS parks it using:

```
LockSupport.park()
```

---

## Q3. Why use AQS?

Answer:

To provide reusable synchronization infrastructure with:

* state management
* FIFO waiting queue
* blocking
* waking threads

---

## Q4. Why use CAS in AQS?

Answer:

To atomically update state without locking.

Example:

```
state 0 -> 1
```

---

# Senior Interview Summary

A good answer:

> AbstractQueuedSynchronizer is the foundation for many Java concurrency utilities. It maintains a volatile state variable and a CLH-based FIFO waiting queue. Synchronizers built on AQS implement tryAcquire and tryRelease logic while AQS manages thread queuing, parking, and unparking. ReentrantLock uses AQS in exclusive mode, while Semaphore and CountDownLatch use shared mode.

---

Next chapter:

# **ThreadPoolExecutor Internals — execute() Flow, ctl Variable, Worker Lifecycle, Blocking Queues, Rejection Policies**

This is another extremely common 7+ years Java interview topic because almost every production Java service uses executors.

# ThreadPoolExecutor Internals — execute() Flow, ctl Variable, Worker Lifecycle, Blocking Queues, Rejection Policies

This is one of the **most frequently asked concurrency internals topics** for senior Java developers.

Most developers know:

```java
ExecutorService executor =
        Executors.newFixedThreadPool(10);
```

But interviewers ask:

* How does ThreadPoolExecutor decide when to create a thread?
* Why does it queue tasks before creating more threads?
* What is the `ctl` variable?
* How does Worker work internally?
* Difference between `execute()` and `submit()`?
* How do rejection policies work?
* How do you tune a production thread pool?

Let's go deep.

---

# 1. Why Thread Pools Exist

Creating threads repeatedly is expensive.

Without a pool:

```text
Request

   |
   v

Create Thread

   |
   v

Execute Task

   |
   v

Destroy Thread

```

Problems:

* thread creation cost
* memory usage
* too many OS threads
* context switching

---

Thread pool approach:

```text
                ThreadPoolExecutor


        Worker Thread 1
              |
              |
        Worker Thread 2
              |
              |
        Worker Thread 3


              |
              |
          Task Queue

```

Threads are reused.

---

# 2. ThreadPoolExecutor Architecture

Main components:

```text
                 ThreadPoolExecutor


                        |
        ----------------------------------

        |              |                |

     Worker       BlockingQueue       ctl

   Threads          Tasks          State


```

---

## Worker

Actual thread running tasks.

Example:

```text
Worker-1

while(tasks available){

    execute task

}

```

---

## BlockingQueue

Stores waiting tasks.

Example:

```text
Queue:

Task A

Task B

Task C

```

---

## ctl

Internal atomic variable tracking:

* worker count
* pool state

We will cover this deeply.

---

# 3. Creating ThreadPoolExecutor

The constructor:

```java
ThreadPoolExecutor(
    int corePoolSize,
    int maximumPoolSize,
    long keepAliveTime,
    TimeUnit unit,
    BlockingQueue<Runnable> workQueue
)
```

Example:

```java
ThreadPoolExecutor executor =
    new ThreadPoolExecutor(
        5,
        10,
        60,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>()
    );
```

Meaning:

```
Core threads = 5

Maximum threads = 10

Idle timeout = 60 seconds

Queue = LinkedBlockingQueue

```

---

# 4. The Most Important Part: execute() Flow

When we call:

```java
executor.execute(task);
```

ThreadPoolExecutor follows a decision tree.

This is the heart of the topic.

---

# Step 1: Are core threads available?

Condition:

```
workerCount < corePoolSize
```

If yes:

Create new worker.

Example:

```
corePoolSize = 5


Current workers = 2


New task arrives


Create Worker-3

```

Flow:

```text
Task

 |
 v

Create Thread

 |
 v

Execute task

```

---

# Step 2: Core threads are full

Example:

```
corePoolSize = 5


Current workers = 5

```

Now:

Should we create another thread?

No.

First:

Put task into queue.

Example:

```text
Workers:


T1
T2
T3
T4
T5


Queue:

Task-6
Task-7
Task-8

```

---

# Step 3: Queue is Full

Example:

```
Core threads = 5

Maximum threads = 10


Current:

5 workers

Queue full

```

Now create extra workers.

Example:

```text
Workers:


T1
T2
T3
T4
T5
T6
T7


```

Until:

```
maximumPoolSize
```

---

# Step 4: Maximum Threads Reached

Example:

```
Core = 5

Max = 10

Workers = 10

Queue full

```

No capacity.

Now:

```
RejectedExecutionHandler
```

is called.

---

# Complete execute() Decision Tree

```text
                 execute(task)


                       |
                       v

          workerCount < corePoolSize ?

                  YES
                   |
                   v

             Create Worker


                  NO

                   |
                   v

             Queue task


                   |
                   v

          Queue accepted?


             YES
              |
              v

        Worker executes later


             NO

              |
              v

      workerCount < maxPoolSize?


             YES
              |
              v

        Create extra worker


             NO

              |
              v

          Reject task

```

---

# 5. Why Queue Before Creating More Threads?

This is a common interview question.

Example:

Configuration:

```
corePoolSize = 10
maxPoolSize = 100
queue = LinkedBlockingQueue
```

Suppose:

```
20 tasks arrive
```

What happens?

Many developers think:

```
10 threads + 10 new threads
```

Wrong.

Actual:

```
10 threads created

Remaining 10 tasks go to queue

```

Why?

Because ThreadPoolExecutor prefers:

1. reuse existing threads
2. queue work
3. create extra threads only under pressure

---

# 6. ThreadPoolExecutor ctl Variable

This is advanced.

Internally:

```java
private final AtomicInteger ctl;
```

It stores two things:

1. Pool state
2. Worker count

Single integer.

How?

Using bit manipulation.

---

Conceptually:

```
32 bits


| Pool State | Worker Count |

```

Example:

```
Upper bits:

RUNNING
SHUTDOWN
STOP
TIDYING
TERMINATED


Lower bits:

number of workers

```

---

Why combine them?

Need atomic updates.

Example:

Need to change:

```
state

+

worker count

```

together.

AtomicInteger gives consistency.

---

# 7. ThreadPool States

ThreadPoolExecutor has five states.

---

## RUNNING

Normal state.

Accepts:

* new tasks
* processes queued tasks

Example:

```
Pool running

submit accepted

```

---

## SHUTDOWN

After:

```java
executor.shutdown();
```

Meaning:

Accept existing queue tasks.

Reject new tasks.

Example:

```
Queue:

Task A
Task B

will execute


New Task C

Rejected

```

---

## STOP

After:

```java
executor.shutdownNow();
```

Meaning:

* interrupts running threads
* clears queue

---

## TIDYING

All workers gone.

Pool cleanup.

---

## TERMINATED

Complete shutdown.

---

# 8. Worker Internals

Worker is an inner class:

```java
private final class Worker
        extends AbstractQueuedSynchronizer
        implements Runnable
```

Interesting:

Worker itself uses AQS!

---

Worker contains:

```java
Thread thread;

Runnable firstTask;

```

Example:

```text
Worker


Thread

 |
 |
Runs tasks

```

---

# 9. runWorker() Method

Conceptually:

```java
while(task != null || 
      (task = getTask()) != null){

    task.run();

}

```

Flow:

```text
Worker starts


 |
 |
Run first task


 |
 |
Get next task from queue


 |
 |
Execute


 |
 |
Repeat

```

---

# 10. Worker Lifecycle

Example:

Pool:

```
core = 2

```

Submit:

```
Task A
Task B
Task C

```

Flow:

```
Worker-1
    |
    |
Task A
    |
    |
Queue
    |
    |
Task C


Worker-2
    |
    |
Task B

```

Workers stay alive waiting.

---

# 11. BlockingQueue Choices

Queue selection impacts behaviour.

---

# 1. LinkedBlockingQueue

Most common.

Example:

```java
new LinkedBlockingQueue<>()
```

Characteristics:

* optionally bounded
* FIFO
* high throughput

Spring Boot commonly uses queues.

---

# 2. ArrayBlockingQueue

Fixed capacity.

Example:

```java
new ArrayBlockingQueue<>(100)
```

Memory predictable.

Good for:

* controlled systems

---

# 3. SynchronousQueue

No storage.

Direct handoff.

Example:

```
Producer

 |
 |
Consumer

```

Used by:

```java
Executors.newCachedThreadPool()
```

---

# 4. PriorityBlockingQueue

Tasks ordered by priority.

---

# 12. Rejection Policies

When:

```
maximumPoolSize reached

+

queue full

```

What happens?

---

Java provides:

## 1. AbortPolicy (Default)

Throws exception.

```java
RejectedExecutionException
```

Example:

```text
Task rejected

Application receives error

```

---

## 2. CallerRunsPolicy

Caller executes task.

Example:

```text
Request Thread

       |
       |
       executes task itself

```

Effect:

Creates backpressure.

---

## 3. DiscardPolicy

Silently drops task.

Dangerous.

---

## 4. DiscardOldestPolicy

Drops oldest queued task.

Then retries.

---

# 13. execute() vs submit()

Very common.

---

## execute()

```java
executor.execute(task);
```

Accepts:

```java
Runnable
```

No return value.

Exception:

goes to thread's uncaught exception handler.

---

## submit()

```java
Future future =
    executor.submit(task);
```

Returns:

```java
Future
```

Exception stored inside Future.

Example:

```java
future.get();
```

throws exception.

---

# 14. FixedThreadPool Internals

Created by:

```java
Executors.newFixedThreadPool(10)
```

Internally:

```java
new ThreadPoolExecutor(
    10,
    10,
    0,
    MILLISECONDS,
    new LinkedBlockingQueue<>()
)

```

Meaning:

```
core = max

threads never increase

unbounded queue

```

---

# 15. CachedThreadPool Internals

Created by:

```java
Executors.newCachedThreadPool()
```

Internally:

```
core = 0

max = Integer.MAX_VALUE

queue = SynchronousQueue

```

Meaning:

Create threads aggressively.

Danger:

Can create thousands of threads.

---

# 16. ScheduledThreadPoolExecutor

For:

```java
schedule()
scheduleAtFixedRate()
```

Uses:

```text
DelayedWorkQueue

```

Tasks ordered by execution time.

---

# 17. Production Thread Pool Tuning

Important senior topic.

Never blindly use:

```java
Executors.newFixedThreadPool()
```

because:

```
LinkedBlockingQueue
=
unbounded

```

Memory risk.

---

Better:

```java
ThreadPoolExecutor executor =
new ThreadPoolExecutor(
    20,
    50,
    60,
    TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(500),
    new CustomThreadFactory(),
    new CallerRunsPolicy()
);

```

---

# 18. How To Choose Pool Size

## CPU Bound

Formula:

```
Number of cores + 1
```

Example:

8 cores:

```
9 threads
```

---

## I/O Bound

Formula:

```
CPU cores *
(1 + wait time / compute time)

```

Example:

CPU:

```
8 cores
```

Task:

```
10ms CPU

90ms waiting

```

Approx:

```
8 * (1 + 90/10)

=
80 threads

```

---

# 19. Common Interview Questions

## Q1. Why ThreadPoolExecutor uses BlockingQueue?

Answer:

To decouple task submission from execution and provide buffering when all workers are busy.

---

## Q2. Why not create unlimited threads?

Answer:

Threads consume memory and increase context switching.

---

## Q3. Difference between corePoolSize and maximumPoolSize?

Answer:

Core threads are created first. Maximum threads are created only when the queue is full and additional capacity is required.

---

## Q4. Why use CallerRunsPolicy?

Answer:

It provides natural backpressure by slowing down the producer.

---

# Senior Interview Summary

> ThreadPoolExecutor manages a pool of worker threads and a task queue. When execute() is called, it first creates threads up to corePoolSize, then queues tasks, then creates additional threads up to maximumPoolSize if the queue is full. Once both limits are reached, the rejection policy is invoked. Internally it uses an AtomicInteger ctl variable to maintain pool state and worker count atomically, while Worker objects execute tasks in a loop by fetching them from the BlockingQueue.

---

Next chapter:

# **Concurrent Collections Deep Dive**

* ConcurrentHashMap internals (Java 8 source-level)
* CopyOnWriteArrayList
* BlockingQueue family
* ConcurrentLinkedQueue
* ConcurrentSkipListMap
* Weakly consistent iterators
* Lock-free data structures

This will complete the remaining core Java concurrency internals.
