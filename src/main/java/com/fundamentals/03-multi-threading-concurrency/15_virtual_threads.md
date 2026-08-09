# Virtual Threads (Project Loom — Java 21)

## Why Virtual Threads Matter for Senior Java Interviews

With Java 21, **Virtual Threads** changed how Java applications handle concurrency.

Before Java 21:

```
Request comes
      |
      v
Platform Thread
      |
      v
Blocking I/O (DB/API/File)
      |
      v
Thread waits doing nothing
```

The problem:

A traditional Java thread is expensive because it maps closely to an **OS thread**.

Modern backend systems are mostly **I/O-bound**:

* REST API calls
* Database queries
* Kafka operations
* File operations
* Network calls

Example:

A microservice receives:

```
10,000 concurrent HTTP requests
```

Traditional approach:

```
Request 1  ---> Thread 1
Request 2  ---> Thread 2
Request 3  ---> Thread 3
...
Request 500 ---> Thread 500
```

Creating thousands of platform threads is expensive.

Java 21 introduced:

```
Request
   |
Virtual Thread
   |
Cheap continuation
   |
Platform Thread only when executing CPU work
```

Now we can handle millions of concurrent tasks.

---

# 1. First Understand Platform Threads

Before virtual threads, Java threads were **platform threads**.

A platform thread is a wrapper around an OS thread.

Architecture:

```
Java Application
       |
       |
 JVM Thread
       |
       |
 Operating System Thread
       |
       |
 CPU Core
```

Example:

```java
Thread thread = new Thread(() -> {
    System.out.println("Running");
});

thread.start();
```

This creates:

```
Java Thread
      |
      |
OS Thread
```

The OS manages:

* Scheduling
* Context switching
* Stack memory
* CPU allocation

---

# Why Are Platform Threads Expensive?

## 1. Memory Cost

Every thread needs a stack.

Typical stack size:

```
~1 MB
```

Example:

```
1000 threads

1000 × 1MB

= 1GB memory
```

Imagine:

```
100,000 concurrent requests

100,000 threads

100GB memory only for stacks
```

Not practical.

---

## 2. Context Switching Cost

CPU does not run all threads simultaneously.

Suppose:

```
CPU Core

Thread A running

        |
        v

Save Thread A state

        |
        v

Load Thread B state

        |
        v

Run Thread B
```

This is context switching.

Cost includes:

* Save registers
* Restore registers
* Cache misses

More threads:

```
More threads
      |
      v
More switching
      |
      v
Less CPU efficiency
```

---

## 3. Thread Pool Limitation

Because threads are expensive, we created pools.

Example:

```java
ExecutorService executor =
        Executors.newFixedThreadPool(100);
```

Architecture:

```
100 Worker Threads


Request 1 ----|
Request 2 ----|
Request 3 ----|---- Thread Pool
Request 4 ----|
              |
              |
           100 Threads
```

Only 100 tasks execute.

Others wait:

```
Queue

Request 101
Request 102
Request 103
```

---

# Problem With Thread Pools

Imagine:

```
API Request

Thread
 |
 |
Call Database
 |
 |
Wait 500ms
```

During 500ms:

Thread is doing nothing.

Example:

```
Thread-1

Executing
 |
 |
Database call
 |
 |
WAITING........500ms
```

But the OS thread is still occupied.

---

# Traditional Backend Example

Spring Boot application:

```
Tomcat Thread Pool

Maximum threads = 200
```

Traffic:

```
10000 requests/sec
```

Flow:

```
Request
   |
Tomcat Thread
   |
Database
   |
Wait
```

After 200 requests:

```
No threads available

New requests wait
```

Result:

* latency increases
* thread pool exhaustion
* timeouts

---

# Enter Project Loom

Project Loom goal:

> Make concurrent programming easier by allowing millions of lightweight threads.

Java 21 introduced:

```
Virtual Threads
```

Officially:

```
java.lang.Thread
```

but implemented differently.

---

# 2. What is a Virtual Thread?

A virtual thread is a lightweight thread managed by JVM instead of OS.

Platform Thread:

```
Java Thread
      |
      |
OS Thread
```

Virtual Thread:

```
Java Virtual Thread

        |
        |
 JVM Scheduler

        |
        |
Carrier Thread

        |
        |
OS Thread
```

Important terms:

## Virtual Thread

The task we create.

Example:

```
Handle HTTP request
```

---

## Carrier Thread

The actual platform thread executing virtual threads.

Example:

```
Carrier Thread-1

runs:

Virtual Thread A
Virtual Thread B
Virtual Thread C
```

Like:

```
                 Carrier Thread

                       |
        --------------------------------
        |              |              |
    Virtual       Virtual        Virtual
    Thread 1      Thread 2       Thread 3

```

---

# 3. How Virtual Threads Work Internally

Suppose:

```java
Thread.startVirtualThread(() -> {

    callDatabase();

});
```

Flow:

```
Virtual Thread starts

        |
        v

Runs on Carrier Thread

        |
        v

Database call begins

        |
        v

Thread blocks

        |
        v

JVM removes virtual thread from carrier

        |
        v

Carrier executes another virtual thread
```

Important difference:

Platform thread:

```
BLOCKING

OS Thread stuck
```

Virtual thread:

```
BLOCKING

Virtual thread parked

Carrier reused
```

---

# 4. Creating Virtual Threads

## Method 1

```java
Thread.startVirtualThread(() -> {

    System.out.println(
        "Hello Virtual Thread"
    );

});
```

---

## Method 2

Using Builder API:

```java
Thread thread =
    Thread.ofVirtual()
          .start(() -> {

              System.out.println(
                  "Running"
              );

          });
```

---

## Method 3 (Most Important)

Executor style:

```java
try(ExecutorService executor =
        Executors.newVirtualThreadPerTaskExecutor()) {


    executor.submit(() -> {

        callDatabase();

    });

}
```

This is the replacement for:

```java
Executors.newFixedThreadPool()
```

---

# 5. Virtual Threads vs Thread Pool

Very common interview question.

## Traditional Thread Pool

Example:

```java
newFixedThreadPool(200)
```

Meaning:

```
200 Threads

10000 Tasks

Queue waiting
```

---

## Virtual Thread Executor

Example:

```java
Executors.newVirtualThreadPerTaskExecutor()
```

Meaning:

```
10000 Tasks

10000 Virtual Threads
```

No queue.

Architecture:

```
Request 1 ---> Virtual Thread 1
Request 2 ---> Virtual Thread 2
Request 3 ---> Virtual Thread 3
...
Request 10000 ---> Virtual Thread 10000


            |
            |
 JVM schedules them

            |
            |
Carrier Threads
```

---

# Interview Question

## "Does Java create one OS thread per virtual thread?"

Answer:

No.

Virtual threads are not mapped 1:1 with OS threads.

Example:

```
1 Million Virtual Threads


        |
        |
        v


100 Carrier Threads


        |
        |
        v


100 OS Threads
```

JVM manages scheduling.

---

# 6. When Should We Use Virtual Threads?

Best for:

## I/O Bound Work

Examples:

### REST API calls

```
Service A

   |
   |
HTTP call

   |
   |
Service B
```

---

### Database queries

```
Request

 |
 |
SQL query

 |
 |
Wait
```

---

### File operations

```
Read file

 |
 |
Wait
```

---

# When NOT To Use Virtual Threads?

## 1. CPU Intensive Tasks

Example:

```java
calculatePrimeNumbers();
```

or

```java
imageProcessing();
```

Why?

Because CPU is limited.

Example:

```
8 CPU cores


Only 8 threads can execute
```

Creating:

```
1 million virtual threads
```

does not increase CPU.

---

## 2. Long Running Computation

Example:

```
Machine Learning training

Video encoding

Large calculations
```

Better:

```
ForkJoinPool
Parallel Streams
Dedicated Executor
```

---

# 7. Blocking vs Non Blocking

Important interview area.

Old model:

```
Thread

 |
 |
Blocking call

 |
 |
Thread waits
```

Virtual thread:

```
Virtual Thread

 |
 |
Blocking call

 |
 |
JVM parks it

 |
 |
Carrier reused
```

The code still looks blocking:

```java
User user =
    userRepository.findById(id);
```

But internally:

```
Thread parked
```

---

# 8. Are Virtual Threads Asynchronous?

Common trap.

Question:

"Are virtual threads async?"

Answer:

No.

Virtual threads are still **synchronous programming model**.

Example:

```java
User user =
    database.getUser();
```

Code waits.

But:

```
Waiting does not waste OS thread
```

Difference:

Old:

```
Blocking + expensive thread
```

New:

```
Blocking + cheap virtual thread
```

---

# 9. Pinning

Very important senior interview topic.

## What is Pinning?

Normally:

```
Virtual Thread

      |
      |
      v

Carrier Thread released
```

But sometimes:

```
Virtual Thread

      |
      |
      v

Carrier Thread cannot detach
```

This is called:

**Pinning**

---

# Causes of Pinning

## 1. synchronized block

Example:

```java
synchronized(lock){

    databaseCall();

}
```

Problem:

```
Virtual Thread enters synchronized

        |
        |
Carrier cannot be released
```

---

## 2. Native methods

Example:

JNI calls.

---

# Better Alternative

Instead of:

```java
synchronized(lock)
```

Use:

```java
ReentrantLock
```

Example:

```java
Lock lock =
    new ReentrantLock();


lock.lock();

try {

    databaseCall();

}
finally {

    lock.unlock();

}
```

---

# 10. Structured Concurrency

New concept from Project Loom.

Problem:

Today:

```java
ExecutorService executor;

Future<User> user =
    executor.submit(getUser());

Future<Order> order =
    executor.submit(getOrder());
```

Problems:

* lifecycle management
* cancellation
* exception handling

Structured concurrency:

Think:

```
Parent Task

 |
 |
 ----------------
 |              |
Child 1       Child 2

```

Parent controls children.

Example:

```
Request

 |
 |
 +---- Get User
 |
 +---- Get Orders
 |
 +---- Get Payments

```

If request fails:

cancel everything.

---

# 11. Virtual Threads in Spring Boot

Spring Boot 3.2+ supports virtual threads.

application.properties:

```properties
spring.threads.virtual.enabled=true
```

Then:

Tomcat uses virtual threads.

Architecture:

Before:

```
HTTP Request

Tomcat Thread Pool

200 threads
```

After:

```
HTTP Request

Virtual Thread

millions possible
```

---

# Senior Interview Summary

## Platform Thread

```
Java Thread
     |
OS Thread
```

Expensive.

---

## Virtual Thread

```
Java Thread

     |
 JVM Scheduler

     |
Carrier Thread

     |
OS Thread
```

Cheap.

---

## Use Virtual Threads for:

| Work               | Use |
| ------------------ | --- |
| REST calls         | Yes |
| Database calls     | Yes |
| File IO            | Yes |
| Message processing | Yes |
| CPU computation    | No  |
| Heavy calculations | No  |

---

## Most Asked Interview Questions

1. Why were virtual threads introduced?
2. Difference between platform and virtual threads?
3. Are virtual threads faster?
4. How does JVM schedule virtual threads?
5. What is carrier thread?
6. What happens during blocking?
7. What is pinning?
8. Why avoid synchronized with virtual threads?
9. Virtual threads vs CompletableFuture?
10. Virtual threads vs reactive programming?
11. How does Spring Boot use virtual threads?

Next we should cover **Virtual Threads Internals + JVM Scheduler + Continuations + ForkJoinPool implementation details**, because that is the level expected from a **7+ years senior Java backend engineer**.

# Virtual Threads Internals — JVM Scheduler, Continuations, Carrier Threads (Deep Dive)

Now we go one level deeper.

For a **7+ years Java backend interview**, knowing "virtual threads are lightweight threads" is not enough.

Senior interviewers usually ask:

* How does JVM schedule virtual threads?
* Where do virtual threads actually run?
* What happens when a virtual thread blocks?
* What is continuation?
* What is the role of ForkJoinPool?
* How does JVM park and unpark virtual threads?

Let's understand the internal architecture.

---

# 1. Platform Thread Execution Model

First, compare with old threads.

A normal Java thread:

```text
Java Thread
     |
     |
 JVM Thread Object
     |
     |
 OS Thread
     |
     |
 CPU Core
```

The OS scheduler controls execution.

Example:

```java
Thread t = new Thread(() -> {

    processRequest();

});

t.start();
```

Internally:

```
JVM
 |
 |
Create native thread
 |
 |
Operating System schedules it
```

The JVM has very little control over scheduling.

---

# 2. Virtual Thread Execution Model

Virtual threads change this.

Architecture:

```
                JVM

        Virtual Threads

        VT1
        VT2
        VT3
        VT4
          |
          |
          v

     JVM Scheduler

          |
          |
          v

   Carrier Threads

     CT1     CT2     CT3

          |
          |
          v

     OS Threads
```

Important:

**Virtual threads are scheduled by JVM, not OS.**

The JVM decides:

* Which virtual thread runs
* When it should pause
* When it should resume

---

# 3. What is a Carrier Thread?

A carrier thread is a normal platform thread.

Example:

```
Carrier Thread-1

running:

Virtual Thread A


Carrier Thread-2

running:

Virtual Thread B
```

Think:

```
Platform Thread
=
CPU worker


Virtual Thread
=
Task running on worker
```

Similar to:

```
ExecutorService

Worker Threads
       |
       |
Tasks
```

But now:

```
Carrier Threads
       |
       |
Millions of Virtual Threads
```

---

# 4. JVM Scheduler

Virtual threads use:

**ForkJoinPool**

internally.

Default scheduler:

```
ForkJoinPool

      |
      |
Carrier Threads

      |
      |
Virtual Threads
```

The scheduler maintains runnable virtual threads.

Example:

```
Ready Queue


VT-1
VT-2
VT-3
VT-4
VT-5


        |
        |
        v


Carrier Threads


CT-1 executes VT-1

CT-2 executes VT-2
```

---

# 5. Why ForkJoinPool?

Good question.

ForkJoinPool was already designed for:

* lightweight tasks
* work stealing
* parallel execution

Example:

```
Carrier-1 queue

VT1
VT2
VT3


Carrier-2 queue

VT4
VT5
```

If Carrier-1 becomes free:

```
Carrier-1 steals VT4
```

This is called:

**Work Stealing**

---

# 6. Continuations — The Most Important Concept

Now the core magic.

A continuation means:

> A suspended computation that can be paused and resumed later.

Simple example:

Imagine:

```java
void process(){

    step1();

    databaseCall();

    step3();

}
```

Normally:

```
Thread executes

step1()

databaseCall()

(wait)

step3()
```

During database wait:

```
Thread blocked
```

---

With virtual threads:

```
Execute

step1()


databaseCall()


     |
     |
     v

Suspend execution


Store current state


Release carrier thread


Later:


Resume from here


step3()

```

The JVM saves:

* Stack frames
* Local variables
* Execution position

This saved state is the continuation.

---

# 7. Continuation Example

Code:

```java
public void process(){

    System.out.println("Start");


    callDatabase();


    System.out.println("End");

}
```

Execution:

```
Start

      |
      |
Database call


      |
      |
Suspend


      |
      |
Carrier reused


      |
      |
Database response


      |
      |
Resume


End
```

The method continues from where it stopped.

The developer does not manage this.

---

# 8. Stack Management Difference

This is where memory saving happens.

## Platform Thread

Each thread has:

```
OS Stack

1 MB fixed memory

-----------------

method1()

method2()

method3()

```

Even if waiting:

```
Stack memory remains allocated
```

---

## Virtual Thread

Virtual thread stack is:

```
JVM managed

stored in heap

grows dynamically
```

Example:

```
Virtual Thread


Heap


Continuation object

    |
    |
 Stack Frames


```

Only active parts consume memory.

---

# 9. Blocking Operation Internals

Let's take:

```java
User user =
    userRepository.findById(id);
```

Assume DB takes 500ms.

---

## Platform Thread

```
Platform Thread

 |
 |
Execute SQL

 |
 |
Waiting 500ms


OS Thread blocked


 |
 |
Cannot execute other work

```

---

## Virtual Thread

```
Virtual Thread

 |
 |
Execute SQL


 |
 |
JDBC blocks


 |
 |
JVM parks virtual thread


 |
 |
Continuation saved


 |
 |
Carrier released


 |
 |
Carrier executes another VT


```

When DB responds:

```
Continuation restored

Virtual thread continues

```

---

# 10. Parking and Unparking

Two important terms.

## Parking

Temporarily stopping execution.

Example:

```
Virtual Thread

RUNNING

    |
    |
WAITING IO

    |
    |
PARKED
```

---

## Unparking

Resume execution.

```
Database response arrives


PARKED VT


     |
     |
UNPARK


     |
     |
RUNNING
```

---

# 11. Does Every Blocking Call Release Carrier?

Important interview trap.

Answer:

**No.**

Only JVM-managed blocking operations.

Examples that work well:

```
Socket IO
File IO
HTTP calls
JDBC drivers (mostly)
```

---

But some operations can pin.

Example:

```java
synchronized(lock){

    blockingCall();

}
```

Flow:

```
Virtual Thread

inside synchronized


       |
       |
blocking


       |
       |
Carrier cannot detach

```

Result:

Carrier thread blocked.

---

# 12. Pinning Deep Dive

Normal case:

```
Virtual Thread

       |
       |
Blocking


       |
       |
Unmount from Carrier


Carrier free

```

Pinned case:

```
Virtual Thread

       |
       |
synchronized section


       |
       |
Blocking


       |
       |
Cannot unmount


Carrier stuck
```

---

# Example

Bad:

```java
public synchronized void process(){

    callExternalAPI();

}
```

or:

```java
synchronized(lock){

    databaseCall();

}
```

Problem:

The lock is tied to the carrier execution.

---

Better:

```java
Lock lock =
        new ReentrantLock();


lock.lock();

try {

    databaseCall();

}
finally {

    lock.unlock();

}
```

---

# 13. Virtual Threads vs CompletableFuture

Very common senior question.

## CompletableFuture

Style:

```java
CompletableFuture
    .supplyAsync(() -> callAPI())
    .thenApply(result -> process());
```

Model:

```
Asynchronous programming

Callbacks / pipeline
```

---

## Virtual Threads

Style:

```java
User user =
    callAPI();

process(user);
```

Model:

```
Normal synchronous code

but cheap concurrency
```

---

Comparison:

|                   | CompletableFuture | Virtual Thread     |
| ----------------- | ----------------- | ------------------ |
| Programming style | Async             | Sync               |
| Complexity        | Higher            | Lower              |
| Debugging         | Harder            | Easier             |
| Blocking code     | Avoid             | Allowed            |
| Best for          | Async pipelines   | Request-per-thread |

---

# 14. Virtual Threads vs Reactive Programming

Example:

Reactive:

```
Request

 |
Mono<User>

 |
Callback chain

 |
Response
```

Virtual thread:

```
Request

 |
Virtual Thread

 |
Blocking call

 |
Response
```

Reactive:

* maximum efficiency
* complex programming model

Virtual threads:

* simpler
* almost same scalability for I/O workloads

---

# 15. Important JVM Properties

You can control scheduler.

Example:

```bash
-Djdk.virtualThreadScheduler.parallelism=8
```

Meaning:

Number of carrier threads.

Default:

```
Available processors
```

---

# 16. Debugging Virtual Threads

Thread dump example:

Old:

```
"http-worker-23"

RUNNABLE
```

New:

```
VirtualThread[#102]

WAITING
```

Useful commands:

```bash
jcmd <pid> Thread.print
```

---

# Interview Ready Explanation

If interviewer asks:

**"Explain virtual thread internals."**

Answer:

> Virtual threads are lightweight JVM-managed threads introduced in Java 21. Unlike platform threads, they are not mapped one-to-one with OS threads. The JVM schedules virtual threads onto carrier platform threads using an internal ForkJoinPool scheduler. When a virtual thread performs blocking I/O, the JVM captures its continuation, parks the virtual thread, releases the carrier thread, and allows another virtual thread to execute. Once the blocking operation completes, the continuation is restored and execution resumes. This allows millions of concurrent I/O-bound tasks with minimal memory overhead.

---

## Next Chapter

**Virtual Threads vs Thread Pools vs CompletableFuture vs Reactive Programming — Production Decision Guide**

This is the chapter where senior interviews usually test architecture decisions:

* "Should we replace all thread pools with virtual threads?"
* "Do we still need ExecutorService?"
* "Can Kafka consumers use virtual threads?"
* "Can Spring Boot production apps directly enable virtual threads?"
* "What happens with database connection pools?"


# Virtual Threads vs Thread Pools vs CompletableFuture vs Reactive Programming — Production Decision Guide

This is where senior engineers are separated from people who only know the API.

Knowing how to create a virtual thread is easy.

The real interview question is:

> "When should you choose virtual threads, thread pools, CompletableFuture, or reactive programming in a production system?"

Let's build the decision-making ability.

---

# 1. The Evolution of Java Concurrency Models

Java concurrency evolved through several generations.

## Generation 1: Platform Threads

(Pre Java 21)

```text
Request

   |
   v

OS Thread

   |
   v

Blocking I/O

   |
   v

Thread waits
```

Problem:

Threads are expensive.

Solution:

Thread pools.

---

# Generation 2: Thread Pools

Example:

```java
ExecutorService executor =
    Executors.newFixedThreadPool(200);
```

Architecture:

```text
              Thread Pool


Thread-1  ----|
Thread-2  ----|
Thread-3  ----|------ Task Queue
Thread-4  ----|

```

Idea:

Reuse expensive threads.

---

# Generation 3: CompletableFuture

Java 8 introduced:

```java
CompletableFuture
```

Goal:

Avoid blocking.

Example:

```java
CompletableFuture
    .supplyAsync(() -> getUser())
    .thenApply(user -> enrich(user))
    .thenAccept(result -> save(result));
```

Model:

```text
Task A

   |
   v

Task B

   |
   v

Task C

```

Async pipeline.

---

# Generation 4: Reactive Programming

Examples:

* Project Reactor
* RxJava

Spring WebFlux:

```java
Mono<User> getUser();
```

Model:

```text
Event Loop

     |
     |
Non-blocking operations

     |
     |
Callbacks
```

Maximum resource efficiency.

---

# Generation 5: Virtual Threads

Java 21:

```java
Thread.startVirtualThread(...)
```

Idea:

Keep simple blocking code.

But make blocking cheap.

```text
Request

   |
   v

Virtual Thread

   |
   v

Blocking DB call

   |
   v

Carrier released

```

---

# 2. Virtual Threads vs Thread Pools

Important:

Virtual threads do NOT replace all executors.

They replace a specific use case.

---

## Traditional Thread Pool

Example:

```java
ExecutorService executor =
    Executors.newFixedThreadPool(100);
```

Assume:

```
100 threads
```

Incoming requests:

```
1000 requests
```

Flow:

```
Request 1  ---> Thread 1
Request 2  ---> Thread 2
...
Request100 ---> Thread100


Request101
Request102

waiting in queue
```

---

## Virtual Thread Executor

Example:

```java
ExecutorService executor =
    Executors.newVirtualThreadPerTaskExecutor();
```

Flow:

```
Request 1 ---> Virtual Thread 1
Request 2 ---> Virtual Thread 2
Request 3 ---> Virtual Thread 3
...
Request10000 ---> Virtual Thread10000
```

JVM handles scheduling.

---

# Key Interview Point

Question:

"Can virtual threads replace thread pools?"

Answer:

> Virtual threads replace the need for manually sized thread pools for I/O-bound workloads, but they do not eliminate the need for executors. CPU-bound workloads still require bounded thread pools.

---

# 3. CPU Bound vs I/O Bound

This is the most important decision factor.

---

# CPU Bound Work

Example:

```java
public int calculate() {

    return fibonacci(100000);

}
```

Work:

```
CPU
 |
 |
Calculation
 |
 |
CPU
```

CPU is the bottleneck.

Example machine:

```
8 CPU cores
```

Maximum useful parallelism:

```
~8 threads
```

Creating:

```
1 million virtual threads
```

does not help.

Why?

Because:

```
CPU cores are limited
```

Use:

```java
ForkJoinPool
```

or

```java
FixedThreadPool(numberOfCores)
```

---

# I/O Bound Work

Example:

REST API:

```
Service A

 |
 |
HTTP Call

 |
 |
Service B

 |
 |
Wait
```

Database:

```
Application

 |
 |
SQL Query

 |
 |
Wait 200ms
```

The bottleneck:

```
Waiting
```

Virtual threads are perfect here.

---

# Decision Table

| Work Type              | Recommended       |
| ---------------------- | ----------------- |
| Database calls         | Virtual Threads   |
| REST API calls         | Virtual Threads   |
| File I/O               | Virtual Threads   |
| Message processing     | Virtual Threads   |
| CPU calculations       | Thread Pool       |
| Image processing       | Thread Pool       |
| ML training            | Thread Pool       |
| Complex async workflow | CompletableFuture |
| Streaming events       | Reactive          |

---

# 4. Virtual Threads vs CompletableFuture

Very common interview question.

Let's compare.

---

## CompletableFuture Example

Suppose:

Need user, orders, and payments.

Old style:

```java
CompletableFuture<User> user =
    getUserAsync();


CompletableFuture<Order> order =
    getOrderAsync();


CompletableFuture<Payment> payment =
    getPaymentAsync();


CompletableFuture.allOf(
    user,
    order,
    payment
);
```

Good.

But complexity increases.

---

## Virtual Thread Style

Same problem:

```java
try(var executor =
    Executors.newVirtualThreadPerTaskExecutor()) {


    Future<User> user =
        executor.submit(this::getUser);


    Future<Order> order =
        executor.submit(this::getOrders);


    Future<Payment> payment =
        executor.submit(this::getPayment);


}
```

Looks like normal code.

---

# Difference

CompletableFuture:

```
Think in callbacks
```

Virtual Threads:

```
Think in sequential code
```

---

# Debugging Difference

CompletableFuture:

Stack trace:

```
callback1()
callback2()
callback3()
```

Harder.

---

Virtual Thread:

Stack trace:

```
Controller

 |
Service

 |
Repository

 |
Database
```

Normal.

---

# 5. Virtual Threads vs Reactive Programming

This is an architecture question.

---

# Reactive Model

Example:

Spring WebFlux:

```java
@GetMapping
public Mono<User> getUser(){

    return userService.findUser();

}
```

Flow:

```
Request

 |
Event Loop

 |
Non-blocking call

 |
Callback

 |
Response
```

---

Advantages:

* highest scalability
* fewer threads
* excellent for streaming

Disadvantages:

* complex
* harder debugging
* learning curve

---

# Virtual Thread Model

Example:

Spring MVC:

```java
@GetMapping
public User getUser(){

    return userService.findUser();

}
```

Flow:

```
Request

 |
Virtual Thread

 |
Database

 |
Response
```

Advantages:

* simple
* readable
* easy migration

---

# Interview Answer

Question:

"Will virtual threads kill reactive programming?"

Answer:

> No. Virtual threads solve efficient concurrency for blocking I/O workloads while maintaining imperative programming style. Reactive programming is still valuable for systems requiring streaming, backpressure, and extremely high throughput with non-blocking pipelines.

---

# 6. Spring Boot Production Decision

Before Java 21:

Typical:

```
Spring MVC

Tomcat Thread Pool

200 threads

```

Problem:

Long DB calls consume threads.

---

After Java 21:

Enable:

```properties
spring.threads.virtual.enabled=true
```

Now:

```
Spring MVC

Request

 |

Virtual Thread

 |

Controller

 |

Service

 |

Repository

```

---

# 7. Important: Database Connection Pool Limitation

Very common senior question.

Suppose:

You have:

```
1 million virtual threads
```

All call:

```sql
SELECT * FROM orders;
```

But database pool:

```
Maximum connections = 50
```

What happens?

Only 50 execute.

Others wait.

Architecture:

```
1 million Virtual Threads


          |
          |

50 DB Connections


          |
          |

Database

```

The bottleneck moves.

---

Important:

Virtual threads increase concurrency.

They do NOT increase:

* DB capacity
* CPU capacity
* network bandwidth

---

# Example

Before:

```
200 Tomcat threads

50 DB connections
```

After:

```
100000 Virtual threads

50 DB connections
```

Database is still:

```
50 connections
```

---

# 8. Kafka Consumers with Virtual Threads

Question:

"Can Kafka consumers use virtual threads?"

Answer:

Yes, but carefully.

Typical:

```
Kafka Consumer Thread

       |
       |
Poll records

       |
       |
Process messages

```

Option:

Consumer receives:

```
Batch of messages

       |
       |
Create virtual thread per message

```

Example:

```java
records.forEach(record -> {

    Thread.startVirtualThread(() -> {

        process(record);

    });

});
```

Benefits:

* parallel processing
* simpler code

Need care:

* ordering requirements
* offset commits
* backpressure

---

# 9. Do We Still Need ExecutorService?

Yes.

Examples:

## CPU Work

```java
ExecutorService cpuPool =
    Executors.newFixedThreadPool(8);
```

---

## External API Calls

```java
Executors.newVirtualThreadPerTaskExecutor();
```

Different tools.

---

# 10. Production Decision Framework

When designing a service ask:

## Question 1

Is work mostly waiting?

Example:

```
DB
HTTP
File
Network
```

YES:

Use virtual threads.

---

## Question 2

Is work CPU heavy?

YES:

Use bounded pool.

---

## Question 3

Need streaming/backpressure?

YES:

Reactive.

---

## Question 4

Need parallel independent tasks?

YES:

CompletableFuture or Structured Concurrency.

---

# Senior Interview Cheat Sheet

## Use Virtual Threads

```
High concurrency
+
Blocking I/O
+
Simple request-response
```

Examples:

* REST APIs
* CRUD services
* Database applications

---

## Use Thread Pool

```
CPU intensive
+
Controlled parallelism
```

Examples:

* computation
* batch processing

---

## Use CompletableFuture

```
Need async composition
+
parallel workflows
```

Examples:

* aggregate APIs

---

## Use Reactive

```
Streaming
+
Backpressure
+
event-driven systems
```

Examples:

* real-time feeds
* streaming platforms

---

# Final Interview Answer

> Virtual threads are not a replacement for every concurrency mechanism. They simplify high-concurrency I/O workloads by allowing developers to write synchronous blocking code while avoiding the cost of platform threads. Thread pools remain important for CPU-bound tasks where execution must be limited. CompletableFuture is useful for asynchronous composition, and reactive programming remains valuable for streaming and backpressure-heavy systems.

---

Next chapter:

# **Structured Concurrency (Java 21+) — Managing Multiple Virtual Threads Safely**

This is the final advanced Project Loom topic and is increasingly asked in senior Java interviews:

* Fork vs Join model
* Shutdown propagation
* Exception handling
* Cancellation
* StructuredTaskScope
* Real microservice examples

# Structured Concurrency (Project Loom) — Managing Multiple Virtual Threads Safely

Now we enter the last advanced Project Loom topic.

For senior Java interviews, this is where interviewers check whether you understand **real production concurrency design**.

Most applications do not execute only one task.

A typical microservice request may need:

```text
Incoming Request

        |
        |
        +----------------+
        |                |
        v                v
 Get User          Get Orders

        |
        |
        +----------------+
        |
        v

 Get Recommendations

        |
        |
        v

Combine Response
```

We need to execute multiple independent tasks concurrently.

The question is:

> How do we manage these child tasks safely?

That is the problem Structured Concurrency tries to solve.

---

# 1. Problem With Traditional Concurrency

Let's look at current Java style.

Example:

User profile API.

Need:

1. User details
2. Orders
3. Recommendations

Traditional approach:

```java
ExecutorService executor =
        Executors.newFixedThreadPool(10);


Future<User> userFuture =
        executor.submit(() -> getUser());


Future<List<Order>> orderFuture =
        executor.submit(() -> getOrders());


Future<List<Product>> recommendationFuture =
        executor.submit(() -> getRecommendations());

```

Then:

```java
User user = userFuture.get();

List<Order> orders =
        orderFuture.get();

List<Product> products =
        recommendationFuture.get();
```

Looks okay.

But problems exist.

---

# Problem 1: Task Lifecycle Management

Who owns these tasks?

Example:

```text
Parent Request

        |
        |
        +---- Task A
        |
        +---- Task B
        |
        +---- Task C
```

If parent request fails:

What happens?

```text
Request cancelled

       |
       |
       ?

Task A still running

Task B still running

Task C still running

```

We created orphan tasks.

---

# Problem 2: Exception Handling

Suppose:

```text
Get User       SUCCESS

Get Orders     FAILURE

Get Payment    RUNNING
```

Now what?

Should payment continue?

Maybe not.

But traditional Future does not automatically handle this.

---

# Problem 3: Cancellation

Example:

User closes browser.

Request is cancelled.

But:

```text
Database call still running

External API call still running

Background task still running
```

Resources wasted.

---

# Structured Concurrency Idea

The principle:

> Treat concurrent tasks as a single unit of work.

A parent task owns child tasks.

---

Instead of:

```text
Independent Threads


Thread A

Thread B

Thread C

```

Structured model:

```text
              Parent Task


                  |
      ----------------------------

      |             |            |

   Child A       Child B      Child C


```

Lifecycle:

Parent starts children.

Parent waits.

Parent finishes.

Children finish.

---

# 2. Structured Concurrency Rules

Structured concurrency follows:

## Rule 1

Child tasks cannot outlive parent.

Example:

```text
Request finished

        |
        |
Children automatically cancelled

```

---

## Rule 2

Failure propagates.

Example:

```text
Child A SUCCESS

Child B FAILURE


        |
        |

Cancel Child C

```

---

## Rule 3

Resources are automatically cleaned.

---

# 3. StructuredTaskScope

Java provides:

```java
StructuredTaskScope
```

(Currently a preview feature in Java 21/22 era; availability depends on Java version and release status.)

Concept:

```java
try(var scope =
    new StructuredTaskScope.ShutdownOnFailure()) {


}
```

---

# Example: Parallel API Calls

Imagine:

```text
User Dashboard API


Need:

1. Profile
2. Orders
3. Payments

```

Without structured concurrency:

```java
Future<User> user =
    executor.submit(this::getUser);


Future<Order> orders =
    executor.submit(this::getOrders);


Future<Payment> payment =
    executor.submit(this::getPayment);

```

---

With structured concurrency:

```java
try(var scope =
    new StructuredTaskScope.ShutdownOnFailure()) {


    Subtask<User> user =
        scope.fork(() -> getUser());


    Subtask<List<Order>> orders =
        scope.fork(() -> getOrders());


    Subtask<Payment> payment =
        scope.fork(() -> getPayment());


    scope.join();


    scope.throwIfFailed();


    return new Dashboard(
        user.get(),
        orders.get(),
        payment.get()
    );
}
```

---

# 4. Understanding fork()

Important method:

```java
scope.fork()
```

means:

"Create a child task under this parent."

Example:

```java
scope.fork(() -> {

    return databaseCall();

});
```

Internally:

```text
StructuredTaskScope

          |
          |
          +---- Virtual Thread
          |
          +---- Virtual Thread
          |
          +---- Virtual Thread

```

Each child runs in a virtual thread.

---

# 5. Understanding join()

Example:

```java
scope.join();
```

Meaning:

Wait for all children.

Before:

```text
Child A running

Child B running

Child C running


Parent waiting

```

After:

```text
Child A complete

Child B complete

Child C complete


Parent continues

```

---

# 6. ShutdownOnFailure

Very important.

Example:

```java
new StructuredTaskScope
        .ShutdownOnFailure()
```

Meaning:

If one child fails:

Cancel others.

Example:

```text
Parent Request


        |
 ---------------------
 |          |          |
User     Orders    Payment


SUCCESS  FAILED    RUNNING


             |
             |
             v

Cancel Payment

```

Why?

Because response cannot succeed.

---

# 7. ShutdownOnSuccess

Another strategy:

```java
ShutdownOnSuccess
```

Use case:

Multiple sources.

Example:

Get user location.

Sources:

```text
Database

Cache

External API
```

First success wins.

```text
Cache

   |
   |
SUCCESS


Cancel others

```

Example:

```java
try(var scope =
    new StructuredTaskScope.ShutdownOnSuccess<>()) {


    scope.fork(() -> getFromCache());

    scope.fork(() -> getFromDatabase());

    scope.fork(() -> getFromRemoteAPI());


    scope.join();


    return scope.result();

}
```

---

# 8. Structured Concurrency vs CompletableFuture

Important interview comparison.

## CompletableFuture

Example:

```java
CompletableFuture<User> user =
    getUser();


CompletableFuture<Order> order =
    getOrders();


CompletableFuture.allOf(
    user,
    order
);
```

Problem:

No natural parent-child relationship.

---

## Structured Concurrency

Relationship:

```text
Parent

 |
 |
 +---- Child
 |
 +---- Child
 |
 +---- Child

```

The hierarchy is explicit.

---

Comparison:

| Feature                | CompletableFuture | Structured Concurrency |
| ---------------------- | ----------------- | ---------------------- |
| Task ownership         | Manual            | Parent owns child      |
| Cancellation           | Manual            | Automatic              |
| Exception handling     | Complex           | Built-in               |
| Debugging              | Harder            | Easier                 |
| Virtual thread support | Optional          | Designed for it        |

---

# 9. Structured Concurrency in Microservices

Real example:

Order Service.

Request:

```text
GET /order/123
```

Need:

```text
Order Service

     |
     |
     +---- Inventory Service

     |
     |
     +---- Pricing Service

     |
     |
     +---- Customer Service

```

With structured concurrency:

```text
Request Virtual Thread


        |
        |
 StructuredTaskScope


        |
 -------------------------
 |          |             |

Inventory Pricing Customer


```

If pricing fails:

```text
Cancel inventory

Cancel customer

Return error

```

No wasted work.

---

# 10. Thread Context Propagation Problem

Another senior topic.

Suppose:

```java
ThreadLocal<String> userId;
```

Traditional threads:

```text
Thread

 |
 |
ThreadLocal

```

Virtual threads support ThreadLocal, but:

creating millions of ThreadLocals can consume memory.

Newer Java introduces:

```text
Scoped Values
```

for structured data sharing.

---

# 11. Structured Concurrency and Transactions

Important backend question.

Suppose:

```text
Create Order


 |
 |
 +---- Save Order

 |
 |
 +---- Reserve Inventory

 |
 |
 +---- Payment

```

If payment fails:

Need:

Rollback.

Structured concurrency helps with:

* cancellation
* coordination

But it does NOT replace:

* database transactions
* distributed transactions
* Saga patterns

---

# 12. What Structured Concurrency Does NOT Solve

It does not solve:

## Distributed Transactions

Example:

```text
Service A

Database


Service B

Database

```

Need:

* Saga
* Outbox pattern
* Event-driven consistency

---

## CPU Parallelism

Still use:

* ForkJoinPool
* Parallel streams

---

# Interview Answer

Question:

**"What is structured concurrency?"**

Answer:

> Structured concurrency is a programming model where concurrent tasks are treated as child tasks belonging to a parent task. It creates clear ownership, lifecycle management, cancellation propagation, and error handling. In Java, StructuredTaskScope allows multiple virtual threads to execute concurrently while ensuring that child tasks cannot outlive their parent scope.

---

# Complete Project Loom Module Summary

We have now covered:

## 1. Platform Threads

* OS-backed
* expensive
* limited scalability

## 2. Virtual Threads

* JVM-managed
* lightweight
* ideal for blocking I/O

## 3. Internals

* carrier threads
* JVM scheduler
* ForkJoinPool
* continuations
* parking/unparking

## 4. Production Decisions

* virtual threads vs pools
* CompletableFuture
* reactive programming
* database limits

## 5. Structured Concurrency

* parent-child task model
* cancellation
* exception propagation
* microservice usage

---

This completes **Project Loom / Virtual Threads for Senior Java Interviews**.

Next, the remaining advanced concurrency topics for a complete 7+ years Java concurrency handbook would be:

1. **Atomic Classes Internals (CAS, Unsafe, VarHandle)**
2. **Lock Internals (AQS, ReentrantLock, ReadWriteLock, StampedLock)**
3. **Concurrent Collections Deep Dive**
4. **Executors Internals (ThreadPoolExecutor, Work Queue, Rejection Policies)**
5. **Concurrency Design Patterns (Producer Consumer, Bulkhead, Rate Limiter, Semaphore)**

These are the areas most commonly asked after JMM + Virtual Threads.
