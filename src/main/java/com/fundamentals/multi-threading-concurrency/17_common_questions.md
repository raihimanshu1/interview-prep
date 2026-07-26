Yes. This is the **remaining practical layer** of concurrency.

We have already covered:

* Threads
* synchronized
* volatile
* Atomic classes
* CAS
* AQS
* Locks
* ThreadPoolExecutor
* Concurrent Collections

Now we move from **Java concurrency primitives → concurrency design patterns → real production system design**.

This is exactly what separates a 3–5 year developer from a **7+ years senior backend engineer**.

The remaining module will be:

# Module 5 — Concurrency Design Patterns & System Design

---

# Chapter 1: Producer Consumer Pattern (Advanced) ⭐⭐⭐⭐⭐

## Why Producer Consumer Exists

Problem:

A producer generates work faster than consumers can process.

Example:

Order system:

```
User Request

      |
      v

Order Service
(Producer)

      |
      |
      v

Message Queue

      |
      |
      v

Payment Service
(Consumer)

```

Without buffering:

```
Producer
   |
   |
   v

Consumer

```

Producer must wait.

Poor throughput.

---

## Basic Idea

Introduce a buffer:

```
              Queue


Producer  ---> [ Task ] ---> Consumer


```

Producer responsibility:

```
Create tasks
```

Consumer responsibility:

```
Process tasks
```

They are decoupled.

---

# 1. Java Implementation Using BlockingQueue

Example:

```java
BlockingQueue<String> queue =
        new LinkedBlockingQueue<>(100);
```

---

## Producer

```java
class Producer implements Runnable {

    private final BlockingQueue<String> queue;


    Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }


    public void run() {

        for(int i=0;i<10;i++) {

            try {

                queue.put("Task-" + i);

            } catch(Exception e){

                Thread.currentThread()
                      .interrupt();

            }
        }
    }
}
```

---

## Consumer

```java
class Consumer implements Runnable {


    private final BlockingQueue<String> queue;


    Consumer(BlockingQueue<String> queue){
        this.queue = queue;
    }


    public void run(){

        while(true){

            try {

                String task =
                    queue.take();


                process(task);


            } catch(Exception e){

                Thread.currentThread()
                      .interrupt();

                break;
            }
        }
    }


    private void process(String task){

        System.out.println(task);

    }
}
```

---

# Execution

```java
public class Main {


public static void main(String[] args){


BlockingQueue<String> queue =
        new LinkedBlockingQueue<>(10);


ExecutorService executor =
        Executors.newFixedThreadPool(3);



executor.submit(
    new Producer(queue)
);


executor.submit(
    new Consumer(queue)
);


}

}
```

---

# 2. Why BlockingQueue Instead of wait()/notify()

Old approach:

```
Producer

wait()

notify()


Consumer

```

Problems:

* manual locking
* missed signals
* complicated code

BlockingQueue internally handles:

* locking
* waiting
* notification

using:

```
AQS
+
Condition
```

---

# 3. Backpressure ⭐⭐⭐⭐⭐

Very important production concept.

Example:

```
Producer

10000 requests/sec


Consumer

1000 requests/sec

```

Without limit:

```
Queue

1M
2M
3M

Memory explosion

```

Solution:

Bounded queue:

```java
new LinkedBlockingQueue<>(1000)
```

Now:

```
Queue Full

     |
     v

Producer slows down

```

This is backpressure.

---

# 4. Multiple Producers / Consumers

Production systems usually have:

```
Producer-1
Producer-2
Producer-3

        |
        |
        v

     Queue

        |
        |
        v

Consumer-1
Consumer-2
Consumer-3

```

Advantages:

* higher throughput
* parallel processing

---

# 5. Poison Pill Pattern

Problem:

How do consumers stop?

Example:

Consumer:

```java
while(true){

 task = queue.take();

 process(task);

}
```

Infinite loop.

Solution:

Special message:

```java
POISON_PILL
```

Producer:

```java
queue.put("STOP");
```

Consumer:

```java
if(task.equals("STOP")){
    break;
}
```

---

# 6. Real Examples

## Kafka

Producer:

```
Application

    |
    v

Kafka Topic

```

Consumer:

```
Consumer Group

    |
    v

Processing

```

---

## ThreadPoolExecutor

Internally:

```
execute()

     |
     v

BlockingQueue

     |
     v

Worker threads

```

ThreadPoolExecutor itself follows Producer Consumer.

---

# Interview Questions

## Q1. Why bounded queue is preferred?

Answer:

Because it prevents unlimited memory growth and provides backpressure.

---

## Q2. Difference between BlockingQueue and ConcurrentLinkedQueue?

Answer:

BlockingQueue supports waiting when empty/full.

ConcurrentLinkedQueue is non-blocking and immediately returns.

---

# Chapter 2: Semaphore Pattern ⭐⭐⭐⭐⭐

Semaphore controls access to limited resources.

Think:

> "Only N threads are allowed inside."

---

Example:

Database connections.

Suppose:

```
DB Connection Pool

Capacity = 10

```

But:

```
100 requests arrive

```

We cannot allow 100 DB connections.

---

Semaphore:

```java
Semaphore semaphore =
        new Semaphore(10);
```

---

Acquire:

```java
semaphore.acquire();

callDatabase();

semaphore.release();
```

---

Flow:

```
Thread 1

Acquire

 |
 v

Permit available


Thread 11

Acquire

 |
 v

WAIT

```

---

# Semaphore Internals

It uses:

```
AQS Shared Mode

```

State represents:

```
available permits
```

Example:

Initial:

```
state = 10
```

After acquire:

```
state--

```

After release:

```
state++

```

---

# Common Uses

## API throttling

```
Allow 100 concurrent requests

```

## Database pool

```
Maximum connections

```

## File processing

```
Maximum parallel uploads

```

---

# Chapter 3: Bulkhead Pattern ⭐⭐⭐⭐⭐

Very important in microservices.

Origin:

Ship design.

A ship has separate compartments.

If one compartment floods:

```
Whole ship does not sink

```

---

Software equivalent:

Prevent one workload from consuming all resources.

---

Bad design:

```
Application


Payment Requests
      |
      |
      v

Same Thread Pool


Report Generation
      |
      |
      v

Same Thread Pool

```

Report generation can exhaust threads.

Payment fails.

---

Bulkhead:

Separate pools.

```
Application


Payment Pool

10 Threads


Report Pool

5 Threads


Notification Pool

5 Threads

```

Failure isolated.

---

# Java Example

```java
ExecutorService paymentExecutor =
    Executors.newFixedThreadPool(10);


ExecutorService reportExecutor =
    Executors.newFixedThreadPool(5);

```

---

# Spring Cloud Example

Often implemented using:

* Resilience4j Bulkhead
* ThreadPoolBulkhead

---

# Chapter 4: Rate Limiter Pattern ⭐⭐⭐⭐⭐

Controls request rate.

Example:

```
Allow:

100 requests/sec

```

Anything above:

```
Reject

```

---

Common algorithms:

1. Fixed Window
2. Sliding Window
3. Token Bucket
4. Leaky Bucket

---

# Token Bucket

Most common.

Imagine:

```
Bucket

Capacity = 10 tokens

```

Each request:

```
Consumes 1 token

```

No token:

```
Reject request

```

Tokens refill:

```
+1 token every 100ms

```

---

Diagram:

```
          Refill


             |
             v


        +-----------+

        | Tokens    |

        +-----------+


             |
             v


        Request

```

---

Implementation idea:

State:

```
capacity

currentTokens

lastRefillTime

```

Request:

```
calculate new tokens

if token > 0

consume

else reject

```

---

# Chapter 5: Thread Confinement ⭐⭐⭐⭐⭐

Idea:

> Never share mutable state between threads.

---

Example:

Bad:

```java
static List<String> list;
```

Multiple threads modify.

---

Thread confinement:

Each thread owns its data.

---

Example:

ThreadLocal:

```java
ThreadLocal<UserContext> context =
        new ThreadLocal<>();
```

Thread 1:

```
User A Context

```

Thread 2:

```
User B Context

```

No sharing.

---

Used in:

* Spring Security Context
* Request context
* Transaction context

---

# Chapter 6: Immutable Objects ⭐⭐⭐⭐⭐

One of the simplest concurrency techniques.

Rule:

If object cannot change:

No synchronization required.

---

Example:

```java
public final class Employee {


private final String name;


public Employee(String name){

    this.name=name;

}


public String getName(){

    return name;

}

}
```

---

Characteristics:

* class final
* fields private final
* no setters
* defensive copies

---

Why thread safe?

Because:

```
Object state

never changes

```

Multiple threads can read safely.

---

# Chapter 7: Actor Model ⭐⭐⭐⭐☆

Alternative concurrency model.

Instead of:

```
Shared memory

+
Locks

```

Use:

```
Independent actors

+
Messages

```

---

Actor:

```
        Actor


 State


 Mailbox


 Behavior

```

Actor owns its state.

Other actors communicate through messages.

---

Example:

Bank Account Actor:

```
Account Actor


Balance = 1000


Message:

Withdraw 100


Process internally


Balance=900

```

No lock.

---

Popular implementations:

* Akka
* Erlang
* Orleans

---

# Concurrency Design System Examples

After patterns, we will build:

## 1. Connection Pool

Concepts:

* Semaphore
* BlockingQueue
* Object lifecycle
* Timeout handling

---

## 2. Distributed Rate Limiter

Concepts:

* Token Bucket
* Redis
* Atomic operations
* Lua scripting
* Sliding window

---

## 3. Async Job Executor

Concepts:

* ThreadPoolExecutor
* Priority Queue
* Retry
* Dead Letter Queue
* Worker management

---

## 4. In-Memory Cache

Concepts:

* ConcurrentHashMap
* Locks
* Expiration
* Eviction
* LRU
* Background cleanup

---

## Current Concurrency Handbook Status

```
Java Concurrency

✅ Threads
✅ Executors
✅ CompletableFuture
✅ synchronized
✅ volatile
✅ Atomic Classes
✅ CAS
✅ AQS
✅ ReentrantLock
✅ Semaphore internals
✅ ThreadPoolExecutor
✅ Concurrent Collections

Remaining:

➡ Producer Consumer Advanced
➡ Bulkhead
➡ Rate Limiter
➡ Thread Confinement
➡ Immutable Design
➡ Actor Model
➡ Connection Pool Design
➡ Async Executor Design
➡ Cache Design
```

We will continue next with **Semaphore + Bulkhead in production system design depth**, because these are heavily asked in senior backend interviews.

# Semaphore + Bulkhead in Production System Design Depth ⭐⭐⭐⭐⭐

We already covered the basic idea of Semaphore and Bulkhead.

Now we go one level deeper:

* How they solve real production problems
* Internal design
* When to use which
* Implementation patterns
* Interview scenarios

These topics are very common in **system design + concurrency interviews**.

---

# 1. Semaphore — Production Understanding

## Problem Statement

Imagine a payment service.

Traffic:

```
1000 requests/sec
```

Each request needs:

```
Database connection
```

Database supports:

```
Maximum 50 concurrent connections
```

Without control:

```
Request 1  ---- DB
Request 2  ---- DB
...
Request 500 ---- DB
```

Result:

```
Database overload
Connection exhaustion
Application failure
```

---

Semaphore solves:

> Limit the number of concurrent operations accessing a resource.

---

# 2. Semaphore Mental Model

Think of permits.

Example:

```java
Semaphore semaphore = new Semaphore(3);
```

Initially:

```
Available permits = 3
```

Visual:

```
+---+---+---+
| P | P | P |
+---+---+---+

```

Three threads can enter.

---

Thread A:

```java
semaphore.acquire();
```

Result:

```
Available permits = 2
```

---

Thread B:

```
Available permits = 1
```

---

Thread C:

```
Available permits = 0
```

---

Thread D:

```java
semaphore.acquire();
```

Waits.

```
Thread D
   |
   |
   v

WAITING
```

---

When a thread finishes:

```java
semaphore.release();
```

Permit returns.

```
Available permits = 1
```

Thread D continues.

---

# 3. Semaphore Internal Working

Semaphore uses:

```
AbstractQueuedSynchronizer
```

in shared mode.

Internally:

```
Semaphore


      AQS


       |
       |
    state


```

The state represents:

```
number of permits
```

Example:

```java
new Semaphore(5)
```

internally:

```
state = 5
```

---

## acquire()

Internally:

```
Thread
 |
 |
tryAcquireShared()

 |
 |
CAS(state - 1)

 |
 |
success

```

Example:

```
state = 5

CAS(5,4)

success

```

---

## release()

Flow:

```
release()

   |
   v

CAS(state + 1)

   |
   v

Wake waiting thread

```

---

# 4. Semaphore Fair vs Non-Fair

Same concept as ReentrantLock.

Creation:

```java
Semaphore semaphore =
        new Semaphore(10,true);
```

Fair semaphore.

---

## Non Fair (Default)

A new thread may jump ahead.

Example:

Queue:

```
Thread A
Thread B
```

New Thread C arrives.

Possible:

```
Thread C gets permit first
```

Higher throughput.

---

## Fair

FIFO:

```
Thread A

then

Thread B

then

Thread C
```

Less starvation.

---

# 5. Real Example: Database Connection Pool

A simplified connection pool:

```
              Requests


                  |
                  v


             Semaphore

             permits=10


                  |
                  v


          Database Connections


```

Code:

```java
class ConnectionPool {


    private final Semaphore semaphore;


    ConnectionPool(int size){

        this.semaphore =
                new Semaphore(size);

    }


    public void execute(){

        try {

            semaphore.acquire();

            useDatabaseConnection();


        } catch(Exception e){

        }
        finally {

            semaphore.release();

        }

    }

}
```

---

# 6. Semaphore vs ThreadPoolExecutor

Very common interview question.

They look similar.

But they solve different problems.

---

## ThreadPoolExecutor

Controls:

```
Number of threads executing tasks
```

Example:

```
Maximum 20 workers
```

---

## Semaphore

Controls:

```
Number of accesses to a resource
```

Example:

```
Maximum 20 database calls
```

---

Example:

You may have:

```
Thread Pool

100 threads


        |
        |
        v


Semaphore

10 permits


        |
        |
        v


Database

```

Meaning:

100 threads exist.

Only 10 can hit DB simultaneously.

---

# 7. Bulkhead Pattern

Now combine concurrency with resilience.

Bulkhead comes from ship design.

A ship:

```
+----------------+
| Engine Room    |
|                |
+----------------+
| Storage        |
|                |
+----------------+
| Crew Area      |
+----------------+

```

If one area floods:

```
Only that compartment fails.
```

---

Software:

Separate resources for separate workloads.

---

# 8. Problem Without Bulkhead

Imagine one Spring Boot application:

```
Application


Thread Pool (100 threads)


        |
 --------------------

 Payment API

 Report API

 Notification API

```

Now:

Report generation starts.

Each report takes:

```
30 seconds
```

100 requests arrive.

Threads:

```
Thread 1  Report
Thread 2  Report
...
Thread 100 Report
```

Now:

Payment request arrives.

Need thread.

But:

```
No threads available
```

Payment fails.

---

One failure impacts everything.

---

# 9. Bulkhead Solution

Separate pools.

```
                 Application


        ----------------------------


        Payment Pool

        20 threads


        Report Pool

        30 threads


        Notification Pool

        10 threads


```

Now:

Report failure:

```
Report Pool exhausted

```

But:

```
Payment Pool

still healthy
```

---

# 10. Thread Pool Bulkhead Implementation

Example:

```java
ExecutorService paymentExecutor =
        Executors.newFixedThreadPool(20);


ExecutorService reportExecutor =
        Executors.newFixedThreadPool(30);


ExecutorService notificationExecutor =
        Executors.newFixedThreadPool(10);

```

---

Payment:

```java
paymentExecutor.submit(
    () -> processPayment()
);
```

Report:

```java
reportExecutor.submit(
    () -> generateReport()
);
```

---

# 11. Bulkhead With Semaphore

Another implementation:

Instead of separate pools:

```
One Thread Pool


        |
        |
        v


Different Semaphores

```

Example:

```
Payment Semaphore

permits=20


Report Semaphore

permits=5

```

---

Code:

```java
Semaphore paymentLimit =
        new Semaphore(20);


public void payment(){

    paymentLimit.acquire();

    try{

        processPayment();

    }
    finally{

        paymentLimit.release();

    }

}
```

---

# 12. Bulkhead vs Rate Limiter

Very common confusion.

They solve different problems.

---

## Rate Limiter

Controls:

```
How many requests per time period
```

Example:

```
100 requests/sec
```

---

## Bulkhead

Controls:

```
How many requests can execute simultaneously
```

Example:

```
20 concurrent requests
```

---

Example:

Suppose:

```
1000 requests arrive
```

Rate limiter:

```
Allow only 100/sec
```

Bulkhead:

```
Allow only 20 running at once
```

---

You often use both:

```
Incoming Request


      |
      v


Rate Limiter

100/sec


      |
      v


Bulkhead

20 concurrent


      |
      v


Service

```

---

# 13. Resilience4j Bulkhead Example

Modern Spring systems commonly use:

Resilience4j

Example concept:

```java
@Bulkhead(
    name="paymentService",
    type=Bulkhead.Type.THREADPOOL
)
public Payment pay(){

    return process();

}
```

---

Two types:

## Semaphore Bulkhead

Uses:

```
Semaphore
```

No extra threads.

---

## ThreadPool Bulkhead

Uses:

```
Dedicated ExecutorService
```

Complete isolation.

---

# 14. Choosing Between Them

## Use Semaphore when:

* same thread can execute
* only need concurrency limit
* lightweight protection

Examples:

```
DB calls
External API calls
File access
```

---

## Use Thread Pool Bulkhead when:

* isolate workloads
* prevent blocking
* need separate queues

Examples:

```
Payment service
Report generation
Email processing
```

---

# 15. Senior Interview Scenario

## Question:

"You have a payment API calling three downstream services:

* Fraud service
* Inventory service
* Notification service

How will you prevent one dependency failure affecting others?"

Answer:

```
Use Bulkhead pattern.

Create isolated execution pools
for each downstream dependency.

Apply timeout and circuit breaker.

Add rate limiting.

Use fallback handling.

```

Architecture:

```
                 Payment Service


                    |
       -----------------------------

       |            |             |

    Fraud       Inventory     Notification


    Pool          Pool            Pool


    20             30              10


```

---

# 16. Production Design Rules

## Never:

Create one global executor.

Example:

```java
Executors.newFixedThreadPool(200);
```

for everything.

---

## Prefer:

```
PaymentExecutor

ReportExecutor

NotificationExecutor

```

because:

* failures isolated
* easier tuning
* predictable latency

---

# 17. Interview Summary

### Semaphore

> Semaphore controls concurrent access to a limited resource using permits. Internally it uses AQS shared mode where state represents available permits. It is commonly used for database connections, API concurrency limits, and resource throttling.

---

### Bulkhead

> Bulkhead isolates different workloads by separating thread pools or concurrency limits. It prevents one slow or failing operation from consuming all application resources and impacting unrelated functionality.

---

### Difference

|                | Semaphore             | Bulkhead                     |
| -------------- | --------------------- | ---------------------------- |
| Purpose        | Limit resource access | Isolate failures             |
| Scope          | Usually one resource  | Entire workload              |
| Implementation | Permit counter        | Separate pools/limits        |
| Example        | DB connections        | Payment vs Report processing |

---

Next chapter:

# Rate Limiter Deep Dive ⭐⭐⭐⭐⭐

* Fixed Window
* Sliding Window
* Token Bucket
* Leaky Bucket
* Distributed Rate Limiter
* Redis + Lua implementation
* API Gateway rate limiting design

This is one of the most frequently asked system design topics for senior backend engineers.

# Rate Limiter Design Pattern — Deep Dive ⭐⭐⭐⭐⭐

Rate Limiting is one of the most important **distributed systems + concurrency design topics**.

Almost every senior backend interview asks:

* How do you protect an API from too many requests?
* Design a rate limiter.
* Token bucket vs sliding window?
* How does Redis-based rate limiting work?
* How do you implement distributed rate limiting?
* Where should rate limiting happen?

This connects:

* Concurrency
* Atomic operations
* Redis
* API Gateway
* Microservices architecture

---

# 1. What is Rate Limiting?

Rate limiting controls:

> How many requests a client can make in a given time period.

Example:

API:

```
GET /user/profile
```

Policy:

```
100 requests per minute per user
```

Meaning:

User A:

```
Request 1
Request 2
...
Request 100

Allowed
```

Request 101:

```
Rejected
```

---

# 2. Why Do We Need Rate Limiting?

## Problem 1: Abuse Protection

Example:

A user sends:

```
100000 requests/sec
```

Without protection:

```
API Server

CPU 100%

Memory exhausted

Database overloaded

Service crash
```

---

## Problem 2: Fair Resource Sharing

Without rate limiting:

```
Customer A
|
|
10000 requests/sec


Customer B
|
|
10 requests/sec
```

Customer A consumes everything.

---

## Problem 3: Cost Control

External APIs often charge per request.

Example:

```
Payment Provider

10000 calls/minute limit
```

Rate limiter protects you.

---

# 3. Where Should Rate Limiting Happen?

Architecture:

```
                 Client


                   |
                   v


            API Gateway


                   |
                   v


          Rate Limiter


                   |
                   v


             Services


                   |
                   v


             Database

```

---

Usually implemented at:

1. API Gateway
2. Load Balancer
3. Service level

---

# 4. Rate Limiting Dimensions

Important interview point.

Rate limiting can be based on:

## User ID

Example:

```
user123
100 requests/min
```

---

## IP Address

Example:

```
192.168.1.10

1000 requests/min
```

---

## API Endpoint

Example:

```
/payment

10 requests/min
```

---

## API Key

Example:

```
Premium customer

10000 requests/min
```

---

# 5. Algorithms

Main algorithms:

```
1. Fixed Window Counter

2. Sliding Window Log

3. Sliding Window Counter

4. Token Bucket

5. Leaky Bucket
```

---

# 6. Fixed Window Counter

Simplest approach.

Rule:

```
100 requests
per minute
```

Maintain:

```
current window count
```

Example:

Time:

```
10:00 - 10:01
```

Counter:

```
count = 0
```

Requests:

```
Request 1

count++

Request 2

count++

...

Request 100

count=100
```

Request 101:

```
Reject
```

---

## Implementation

Storage:

```
Redis Key:

rate:user123:10:00

Value:

75
```

Every request:

```
INCR key
```

Set expiry:

```
EXPIRE 60 seconds
```

---

## Problem: Boundary Issue

Example:

Limit:

```
100 requests/min
```

Timeline:

```
10:00:59

100 requests


10:01:00

100 requests
```

Actual:

```
200 requests in 2 seconds
```

Allowed!

This is called:

**Burst problem**

---

# 7. Sliding Window Log

More accurate.

Store timestamps.

Example:

Redis:

```
user123

[
10:00:01,
10:00:05,
10:00:20
]

```

New request:

```
Current time = 10:00:30
```

Remove old timestamps:

```
before 10:00:30 - 60 seconds
```

Count remaining.

---

Algorithm:

```
Remove old entries

Count requests

If count < limit

Allow

Else

Reject

```

---

## Problem

Memory heavy.

Example:

Million users:

```
Each user

1000 timestamps
```

Large storage.

---

# 8. Sliding Window Counter

Hybrid approach.

Combines:

* fixed window
* weighted calculation

Example:

Limit:

```
100 requests/min
```

Current:

```
10:01 window

60 requests
```

Previous:

```
10:00 window

80 requests
```

At:

```
10:01:30
```

Weight:

```
Previous window contribution:

50%
```

Calculation:

```
80 * 0.5 + 60

=100
```

Reject.

---

# 9. Token Bucket Algorithm ⭐⭐⭐⭐⭐

Most commonly used in production.

Used by:

* API gateways
* cloud providers
* networking systems

---

## Concept

Imagine a bucket:

```
        Token Bucket


       +---------+

       | o o o o |

       | o o o   |

       +---------+

```

Bucket capacity:

```
100 tokens
```

---

Each request:

Consumes one token.

Example:

```
Request arrives

     |
     v

Take token

     |
     v

Process request

```

---

If no token:

```
Reject
```

---

Tokens refill continuously.

Example:

```
10 tokens/sec
```

---

# 10. Token Bucket State

Need to maintain:

```
capacity

currentTokens

refillRate

lastRefillTimestamp

```

Example:

```
capacity = 100

tokens = 70

refill = 10/sec

lastRefill = 10:00:00
```

---

Request at:

```
10:00:05
```

Calculate:

```
5 seconds passed

New tokens:

5 * 10

=50
```

Update:

```
tokens = min(
 capacity,
 tokens + refill
)

```

---

Then:

```
tokens--

allow request
```

---

# 11. Token Bucket Implementation

Pseudo code:

```java
class TokenBucket {


    long capacity;

    long tokens;

    long refillRate;

    long lastRefillTime;


    synchronized boolean allowRequest(){

        refill();


        if(tokens > 0){

            tokens--;

            return true;

        }


        return false;

    }


    void refill(){

        long now =
            currentTime();


        long tokensToAdd =
            (now-lastRefillTime)
            * refillRate;


        tokens =
          Math.min(
              capacity,
              tokens + tokensToAdd
          );


        lastRefillTime = now;

    }

}
```

---

# 12. Why synchronized?

Because multiple threads may call:

```
allowRequest()
```

simultaneously.

Example:

Two threads:

```
Token count = 1
```

Thread A:

```
check token
```

Thread B:

```
check token
```

Both consume.

Incorrect:

```
-2 tokens
```

Need atomicity.

---

# 13. Better Production Implementation

Use Redis.

Architecture:

```
                Request


                   |
                   v


              API Gateway


                   |
                   v


                 Redis


                   |
                   v


             Token Bucket State

```

---

Redis stores:

```
user123

tokens = 50

lastRefill = timestamp

```

---

Problem:

Multiple servers.

Example:

```
Service Instance 1

        |
        |
     Redis


Service Instance 2

        |
        |
     Redis

```

Need centralized state.

---

# 14. Redis + Lua Atomic Implementation

Why Lua?

Because:

Multiple Redis commands:

```
GET tokens

calculate

SET tokens

```

are not atomic.

Another request can come between.

---

Without Lua:

```
Thread A

GET tokens = 1


Thread B

GET tokens = 1


Thread A

SET tokens = 0


Thread B

SET tokens = 0

```

Two requests accepted.

---

Lua executes atomically:

```
Read

Calculate

Update

```

as one operation.

---

# 15. Distributed Rate Limiter Architecture

Production design:

```
                  Client


                    |
                    v


              API Gateway


                    |
                    v


             Rate Limiter


                    |
                    v


                Redis Cluster


                    |
                    v


              Microservices

```

---

Components:

## API Gateway

Example:

* Kong
* NGINX
* Spring Cloud Gateway

---

## Redis

Stores:

```
token state
```

---

## Lua Script

Ensures:

```
atomic update
```

---

# 16. Rate Limiter vs Semaphore vs Bulkhead

Very common interview question.

| Pattern      | Controls              |
| ------------ | --------------------- |
| Rate Limiter | Requests per time     |
| Semaphore    | Concurrent executions |
| Bulkhead     | Resource isolation    |

Example:

Incoming:

```
10000 requests/min
```

Rate limiter:

```
Allow 1000/min
```

---

Allowed:

```
1000 requests
```

Semaphore:

```
Only 50 execute simultaneously
```

---

Bulkhead:

```
Payment gets 20 threads

Report gets 10 threads
```

---

# 17. Handling Rejected Requests

Options:

## Return 429

HTTP:

```
429 Too Many Requests
```

---

Response:

```
Retry-After: 60
```

---

## Queue

For asynchronous workloads.

Example:

```
Order processing

```

---

## Degrade

Example:

```
Return cached response
```

---

# 18. Production Considerations

## Clock Synchronization

Distributed systems need:

```
consistent time
```

Use:

* server time
* Redis time

---

## Redis Failure

What happens if Redis is down?

Options:

### Fail Open

Allow requests.

Good for:

non-critical APIs.

---

### Fail Closed

Reject requests.

Good for:

security APIs.

---

## Hot Keys

Problem:

One popular user:

```
user123
```

All traffic hits:

```
one Redis key
```

Solution:

* sharding
* local cache
* key distribution

---

# 19. Interview Design Question

## Design API Rate Limiter

Answer:

```
Client

 |
 v

API Gateway

 |
 v

Rate Limiter


Algorithm:

Token Bucket


Storage:

Redis


Atomicity:

Lua Script


Response:

HTTP 429


Monitoring:

Metrics + Alerts

```

---

# 20. Senior Interview Summary

A strong answer:

> A rate limiter controls the number of requests allowed within a time period. Common algorithms include fixed window, sliding window, and token bucket. For distributed systems, the rate limiter is usually placed at the API gateway and uses Redis for shared state. Atomicity is achieved using Redis Lua scripts to prevent race conditions between concurrent requests. Token bucket is preferred because it allows controlled bursts while maintaining a stable average request rate.

---

Next chapter:

# Thread Confinement + Immutable Objects ⭐⭐⭐⭐⭐

This will cover the **simplest but most powerful concurrency strategy: avoiding shared mutable state completely**.

# Thread Confinement + Immutable Objects ⭐⭐⭐⭐⭐

This chapter is very important because senior engineers understand that:

> The best way to solve concurrency problems is often to avoid sharing data between threads.

Most concurrency problems come from:

```
Multiple threads
        |
        |
        v
Shared Mutable State
        |
        |
        v
Race Conditions
```

The alternative approach:

```
Thread A
   |
   | owns data


Thread B
   |
   | owns data


No sharing
```

This is called **Thread Confinement**.

---

# 1. What is Thread Confinement?

Thread confinement means:

> A piece of data is accessible by only one thread, so synchronization is not required.

Example:

```java
class UserProcessor {

    private User user;

}
```

If only one thread accesses:

```
Thread A

User Object

```

There is no race condition.

---

# 2. Why Sharing Data Creates Problems?

Example:

```java
class Counter {

    int count = 0;


    void increment(){

        count++;

    }

}
```

Multiple threads:

```
Thread A          Thread B


count++           count++

```

Looks simple.

But internally:

```
count++

=

1. Read count

2. Add 1

3. Write count

```

Initial:

```
count = 0
```

Execution:

```
Thread A

Read 0


Thread B

Read 0


Thread A

Write 1


Thread B

Write 1

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

# 3. Thread Confinement Solution

Instead of:

```
Shared Counter

Thread A
Thread B
Thread C

```

Give each thread its own copy.

```
Thread A

Counter = 0


Thread B

Counter = 0


Thread C

Counter = 0

```

No synchronization needed.

---

# 4. Types of Thread Confinement

Java commonly uses:

1. Stack confinement
2. ThreadLocal confinement
3. Executor/task confinement
4. Object confinement

---

# 5. Stack Confinement ⭐⭐⭐⭐⭐

The simplest form.

Local variables are thread-safe automatically.

Example:

```java
public void processOrder(){

    int total = 100;

    calculate(total);

}
```

Memory:

```
Thread Stack


total = 100

```

Each thread has its own stack.

Diagram:

```
Thread A Stack

total = 100


Thread B Stack

total = 100

```

No sharing.

---

## Example

Safe:

```java
public void calculate(){

    List<String> items =
        new ArrayList<>();

}
```

Why?

Because every invocation creates a new list.

```
Thread A

ArrayList A


Thread B

ArrayList B

```

---

# 6. Object Confinement

Sometimes an object is shared but access is controlled.

Example:

```java
class Printer {


    private final PrinterState state;


    public void print(){

        synchronized(state){

            // modify state

        }

    }

}
```

The object controls access.

The internal state is confined.

---

# 7. ThreadLocal ⭐⭐⭐⭐⭐

The most common interview topic.

`ThreadLocal` provides:

> One separate copy of a variable for each thread.

Example:

```java
ThreadLocal<Integer> threadLocal =
        new ThreadLocal<>();
```

---

Imagine:

```
Thread-1

ThreadLocal

 userId = 101



Thread-2

ThreadLocal

 userId = 202



Thread-3

ThreadLocal

 userId = 303

```

Same variable name.

Different values.

---

# 8. ThreadLocal Internal Working

Each Thread has:

```java
class Thread {

    ThreadLocal.ThreadLocalMap threadLocals;

}
```

Conceptually:

```
Thread


 |
 |
 v


ThreadLocalMap


+----------------+
| ThreadLocal |Value
+----------------+
|    key      |101
+----------------+

```

Important:

The data is stored inside the Thread object.

Not inside ThreadLocal.

---

# 9. ThreadLocal Example

Request context example.

Without ThreadLocal:

```
Request

 |
 v

Pass userId everywhere

service()
repository()
helper()

```

Messy.

---

Using ThreadLocal:

```java
public class UserContext {


private static final ThreadLocal<String> userId =
        new ThreadLocal<>();


public static void setUser(String id){

    userId.set(id);

}


public static String getUser(){

    return userId.get();

}


public static void clear(){

    userId.remove();

}

}
```

---

Usage:

Controller:

```java
UserContext.setUser("user123");

service.process();

```

Anywhere:

```java
String user =
    UserContext.getUser();

```

---

# 10. ThreadLocal in Spring

Very common.

Examples:

## Security Context

Spring Security stores:

```
Current authenticated user

inside ThreadLocal

```

---

## Transaction Context

Spring:

```
@Transactional

```

stores transaction information.

---

## Request Context

HTTP request information.

---

# 11. ThreadLocal Memory Leak Problem ⭐⭐⭐⭐⭐

Important interview question.

Question:

> Why can ThreadLocal cause memory leaks?

Especially with thread pools.

---

Example:

```
Thread Pool

Thread-1
Thread-2
Thread-3

```

Threads live for a long time.

Request:

```
Thread-1

ThreadLocal

User Object

```

After request:

```
Request completed

```

But if we forget:

```java
remove()
```

then:

```
Thread-1

still holds

User Object

```

Memory leak.

---

Correct:

```java
try {

    UserContext.setUser(user);

    process();

}
finally {

    UserContext.remove();

}
```

---

# 12. Immutable Objects ⭐⭐⭐⭐⭐

Another powerful concurrency technique.

Definition:

> An immutable object cannot change after creation.

Examples:

Java:

```
String

Integer

LocalDate

BigDecimal

```

---

Example:

```java
String name = "Java";
```

When:

```java
name.concat("17");
```

does not modify original.

Creates new object.

---

# 13. Why Immutable Objects Are Thread Safe?

Because:

```
Thread A

reads object


Thread B

reads object


No modification possible

```

No race condition.

---

# 14. Creating Immutable Class

Requirements:

## 1. Make class final

Prevents inheritance.

```java
public final class Employee {

}
```

Why?

Subclass can add mutable behavior.

---

## 2. Fields private final

```java
private final String name;
private final int age;
```

---

## 3. Initialize through constructor

```java
public Employee(
    String name,
    int age
){

    this.name=name;
    this.age=age;

}
```

---

## 4. No setters

Bad:

```java
setName()
```

because state changes.

---

## 5. Defensive copies

Important for mutable fields.

---

Example:

Wrong:

```java
class Employee {


private final Date joiningDate;


Employee(Date date){

    this.joiningDate=date;

}

}
```

Problem:

External code can modify:

```java
date.setTime(...)
```

---

Correct:

```java
class Employee {


private final Date joiningDate;


Employee(Date date){

    this.joiningDate =
        new Date(date.getTime());

}


public Date getJoiningDate(){

    return new Date(
        joiningDate.getTime()
    );

}

}
```

---

# 15. Immutable Object Example

```java
public final class User {


private final String id;

private final String name;


public User(
        String id,
        String name){

    this.id=id;
    this.name=name;

}


public String getId(){

    return id;

}


public String getName(){

    return name;

}

}
```

Safe:

```
Thread A

read User


Thread B

read User

```

No locking required.

---

# 16. Immutable Objects and Final Keyword

Important connection.

Example:

```java
final User user;
```

Does not mean object immutable.

It means:

```
reference cannot change
```

Example:

```java
final List<String> list =
        new ArrayList<>();


list.add("Java");
```

Allowed.

Because:

```
reference fixed

object mutable

```

---

# 17. Safe Publication of Immutable Objects ⭐⭐⭐⭐⭐

Important advanced topic.

Example:

```java
public final class Config {


private final String url;


public Config(String url){

    this.url=url;

}

}
```

Because fields are final:

Java Memory Model guarantees:

After construction:

```
Other threads

see fully initialized object

```

---

# 18. Thread Confinement vs Immutable Objects

Very common interview comparison.

| Thread Confinement    | Immutable Object   |
| --------------------- | ------------------ |
| Avoid sharing         | Allow sharing      |
| Each thread owns data | Everyone can read  |
| Usually temporary     | Long-lived         |
| Uses ThreadLocal      | Uses final fields  |
| No synchronization    | No synchronization |

---

Example:

## ThreadLocal

```
Each request thread

has own UserContext

```

---

## Immutable

```
All threads

share same Configuration object

```

---

# 19. Real Production Examples

## Example 1: Request Context

Use:

```
ThreadLocal

```

For:

* user id
* correlation id
* transaction context

---

## Example 2: Configuration

Use:

```
Immutable object

```

Example:

```
DatabaseConfig

APIConfig

FeatureFlags

```

---

## Example 3: Cache Values

Instead of:

```
Mutable User object

```

Prefer:

```
Immutable UserSnapshot

```

---

# 20. Interview Questions

## Q1. Why is ThreadLocal thread safe?

Answer:

Because each thread has its own separate copy stored in ThreadLocalMap inside the Thread object.

---

## Q2. Why remove ThreadLocal value?

Answer:

Because thread pools reuse threads. Without remove(), old values can remain attached to long-lived worker threads causing memory leaks or incorrect context propagation.

---

## Q3. Why are immutable objects useful in concurrency?

Answer:

Because their state never changes after creation, so multiple threads can safely access them without locks.

---

## Q4. Is final object immutable?

Answer:

No.

final prevents reference reassignment, not object mutation.

---

# Senior Engineer Summary

A strong interview answer:

> Thread confinement avoids concurrency problems by ensuring mutable state is accessed by only one thread. Java provides stack confinement, ThreadLocal, and object confinement techniques. ThreadLocal stores thread-specific data in ThreadLocalMap associated with each Thread and is commonly used for request and transaction context. Immutable objects take the opposite approach by allowing safe sharing because their state cannot change after construction. They provide thread safety without synchronization and are widely used for configuration, value objects, and cached data.

---

Next chapter:

# Actor Model + Message Passing Concurrency ⭐⭐⭐⭐☆

Then we move into the final section:

**Concurrency System Design Examples**

1. Building a Connection Pool
2. Designing a Rate Limiter
3. Designing an Async Job Executor
4. Designing an In-Memory Cache

That will complete the concurrency handbook.

# Actor Model + Message Passing Concurrency ⭐⭐⭐⭐☆

The Actor Model is an alternative way to design concurrent systems.

So far, most Java concurrency concepts we learned follow this model:

```text
Shared Memory Concurrency

Thread 1
    |
    |
    v

+----------------+
| Shared Object  |
+----------------+

    ^
    |
Thread 2

    ^
    |
Thread 3


Need:
- locks
- synchronized
- atomic variables
- CAS
```

The Actor Model takes a different approach:

> Do not share memory. Communicate through messages.

---

# 1. Problem With Shared Memory Model

Consider a banking system.

Traditional approach:

```java
class Account {

    private double balance;


    void withdraw(double amount){

        balance -= amount;

    }
}
```

Multiple threads:

```text
Thread A

withdraw(100)


Thread B

withdraw(200)

```

Both modify:

```text
Same balance variable
```

Need:

```java
synchronized
Lock
Atomic operations
```

As systems become bigger:

```
1000 services
millions of objects
millions of concurrent operations
```

Managing locks becomes difficult.

---

# 2. Actor Model Idea

Instead of:

```
Threads
 |
 |
Shared Memory
```

Use:

```
Actors
 |
 |
Messages
```

Each actor has:

```
+----------------+
|     Actor      |
+----------------+
| State          |
| Mailbox        |
| Behavior       |
+----------------+
```

---

An actor contains:

## 1. State

Private data.

Example:

```
Account Actor

balance = 1000
```

---

## 2. Mailbox

A queue of incoming messages.

Example:

```
Mailbox

[Deposit 500]

[Withdraw 100]

[Withdraw 200]

```

---

## 3. Behavior

Logic to process messages.

Example:

```
If message = Withdraw

reduce balance

```

---

# 3. Actor Communication

Actors never directly access another actor's state.

Wrong:

```
Actor A

     |
     |
     v

Actor B.balance = 500

```

Correct:

```
Actor A

      |
      |
      v

 Message

      |
      |
      v

Actor B Mailbox

```

---

# 4. Bank Account Example

Without Actor Model:

```
Account Object

balance

   ^
   |
Thread 1
Thread 2
Thread 3

```

With Actor:

```
             Withdraw 100


User
 |
 |
 v


Account Actor


State:

balance=1000


Mailbox:

[Withdraw 100]


Process:

balance=900

```

Only the actor changes its own state.

No lock required.

---

# 5. Actor Processing Model

Important rule:

> One actor processes one message at a time.

Example:

Mailbox:

```
[Deposit 100]
[Withdraw 50]
[Withdraw 20]

```

Processing:

```
Message 1

Deposit

balance=1100


Message 2

Withdraw

balance=1050


Message 3

Withdraw

balance=1030

```

No two threads modify the actor state simultaneously.

---

# 6. Actor Model Architecture

Large system:

```
                 Messages


Actor A  -----------------> Actor B


Actor B  -----------------> Actor C


Actor C  -----------------> Actor D

```

Each actor:

```
Own state

Own mailbox

Own processing

```

---

# 7. Advantages of Actor Model

## 1. No Shared Mutable State

Biggest advantage.

No:

```
synchronized

volatile

locks

deadlocks
```

for internal actor state.

---

## 2. Natural Distribution

Actors can live on different machines.

Example:

```
Machine 1

User Actor


        Network Message


Machine 2

Payment Actor

```

---

## 3. Fault Isolation

One actor failure does not necessarily crash everything.

Example:

```
Payment Actor

FAILED


Order Actor

Still Running

```

---

## 4. High Concurrency

Millions of lightweight actors are possible.

---

# 8. Disadvantages

Actor Model is not magic.

Problems:

## 1. Message Ordering

Example:

Messages:

```
Withdraw 100

Deposit 500

```

Order matters.

Need guarantees.

---

## 2. Debugging

Traditional debugging:

```
Thread A
Thread B

Stack trace
```

Actor debugging:

```
Actor A

sent message


Actor B

processed message

```

More complex.

---

## 3. Message Overhead

Communication has cost.

---

# 9. Actor Model vs Thread Model

| Traditional Threads        | Actor Model                   |
| -------------------------- | ----------------------------- |
| Shared memory              | Message passing               |
| Locks required             | No shared state               |
| Thread owns execution      | Actor owns state              |
| Synchronization needed     | Sequential message processing |
| Harder distributed scaling | Naturally distributed         |

---

# 10. Actor Model Implementations

Popular frameworks:

## Akka

Java/Scala ecosystem.

Concepts:

```
ActorSystem

ActorRef

Mailbox

Message

```

---

## Erlang/OTP

One of the original actor-based systems.

Used heavily in:

* telecom systems
* messaging systems

---

## Microsoft Orleans

Virtual actors model.

---

# 11. Akka Example (Conceptual)

Actor:

```java
class AccountActor {


    private int balance = 1000;


    void onReceive(Message message){

        if(message instanceof Withdraw){

            balance -= message.amount();

        }


        if(message instanceof Deposit){

            balance += message.amount();

        }

    }

}
```

Messages:

```java
record Deposit(int amount){}

record Withdraw(int amount){}

```

Usage:

```
accountActor.tell(
    new Withdraw(100)
)

```

Meaning:

Send message.

Do not directly call method.

---

# 12. Actor Model in Real Systems

## Example 1: Gaming Systems

Millions of players.

Each player:

```
Player Actor

State:

health
position
inventory

```

Messages:

```
Move
Attack
Collect Item

```

---

## Example 2: Trading Systems

Each order:

```
Order Actor

State:

price
quantity
status

```

Messages:

```
Place Order

Cancel Order

Execute Trade

```

---

## Example 3: IoT Systems

Millions of devices:

```
Device Actor

State:

temperature
status

```

Messages:

```
Update Reading

Change Configuration

```

---

# 13. Actor Model vs Reactive Programming

They are related but different.

Reactive programming:

Focus:

```
Async data streams
```

Example:

```
Kafka Stream

Observable

Publisher Subscriber

```

Actor model:

Focus:

```
Independent entities communicating through messages
```

---

# 14. Actor Model vs Producer Consumer

They look similar.

Producer Consumer:

```
Producer

   |
   v

Queue

   |
   v

Consumer

```

Actor:

```
Actor A

   |
   v

Actor B mailbox

```

Difference:

Producer Consumer:

```
Worker consumes tasks
```

Actor:

```
Entity owns state and behavior
```

---

# 15. Java Modern Alternatives

Java itself does not have native actors.

But Java provides similar ideas:

## Virtual Threads

Java 21:

```
Millions of lightweight tasks
```

---

## CompletableFuture

Message-like async composition.

---

## Reactive frameworks

Examples:

* Project Reactor
* RxJava

---

# 16. Interview Question

## Q1. What problem does Actor Model solve?

Strong answer:

> Actor Model avoids shared mutable state by isolating state inside independent actors and allowing communication through asynchronous messages. This reduces locking complexity and makes highly concurrent and distributed systems easier to design.

---

## Q2. How does Actor Model avoid race conditions?

Answer:

> An actor processes only one message at a time, so its internal state is never accessed concurrently.

---

## Q3. Actor Model vs Locks?

Answer:

> Locks coordinate access to shared state. Actor Model avoids shared state completely by using message passing.

---

# 17. Senior Backend Perspective

For Java backend engineers:

You may not implement actors daily, but the concepts appear everywhere:

Kafka:

```
Producer

Message

Consumer

```

Microservices:

```
Service A

Event

Service B

```

Event-driven architecture:

```
Command

Event Bus

Handler

```

These are all influenced by message-passing concurrency.

---

# Concurrency Handbook Progress

Completed:

```
Concurrency Fundamentals
        ✅

Thread Synchronization
        ✅

Atomic Operations
        ✅

Locks / AQS
        ✅

Thread Pools
        ✅

Concurrent Collections
        ✅

Design Patterns

Producer Consumer
        ✅

Semaphore
        ✅

Bulkhead
        ✅

Rate Limiter
        ✅

Thread Confinement
        ✅

Immutable Objects
        ✅

Actor Model
        ✅
```

Remaining final section:

# Concurrency System Design Examples ⭐⭐⭐⭐⭐

We will now design real production components:

1. **Building a Connection Pool**

    * Object pooling
    * Semaphore
    * BlockingQueue
    * Timeout handling
    * Resource lifecycle

2. **Designing a Distributed Rate Limiter**

    * Token bucket
    * Redis
    * Lua scripts

3. **Designing an Async Job Executor**

    * ThreadPoolExecutor
    * Retry
    * Dead Letter Queue
    * Scheduling

4. **Designing an In-Memory Cache**

    * ConcurrentHashMap
    * LRU
    * Expiration
    * Background cleanup

These are the final topics to complete the concurrency module.


# Concurrency System Design Example 1 — Building a Connection Pool ⭐⭐⭐⭐⭐

Now we move from **individual concurrency concepts** into **real backend component design**.

A connection pool is one of the best examples because it combines:

* Thread synchronization
* Semaphore
* BlockingQueue
* Object lifecycle management
* Resource cleanup
* Timeout handling
* Exception safety

This is a very common **LLD + concurrency interview question**.

---

# 1. Problem Statement

Design a database connection pool.

Requirements:

* Multiple threads need database connections.
* Creating connections is expensive.
* We should reuse existing connections.
* Maximum connections should be limited.
* Threads should wait if no connection is available.
* Connections should be returned after use.

---

# 2. Why Do We Need a Connection Pool?

Without pooling:

Every request:

```text
Request

   |
   v

Create DB Connection

   |
   v

Execute Query

   |
   v

Close Connection

```

Problem:

Creating connections is expensive.

Steps:

```
TCP handshake
Authentication
Session creation
Memory allocation
```

For every request this becomes:

```
1000 requests/sec

=
1000 connection creations/sec
```

Very inefficient.

---

# 3. Connection Pool Idea

Instead:

Create connections once.

Keep them ready.

```text
Application Startup


Create 10 Connections


+----------------+
| Connection 1   |
| Connection 2   |
| Connection 3   |
| ...            |
| Connection 10  |
+----------------+

```

Requests borrow and return.

---

# 4. High Level Architecture

```text
                 Threads


 Thread 1 --------\
                   \
 Thread 2 -----------> Connection Pool
                   /
 Thread 3 --------/


                         |
                         |
                         v


              Available Connections


        +----+----+----+----+

        C1   C2   C3   C4


```

---

# 5. Core Components

A connection pool needs:

## 1. Pool

Stores connections.

Example:

```java
Queue<Connection>
```

---

## 2. Connection Object

Represents actual resource.

Example:

```java
class DBConnection {

    void execute(String query){

    }

}
```

---

## 3. Borrow Operation

Thread requests connection.

```text
borrow()

```

---

## 4. Return Operation

Thread releases connection.

```text
release()

```

---

# 6. Concurrency Problems

Imagine:

Pool size:

```
5 connections
```

Requests:

```
100 threads
```

Problem:

Only 5 can execute.

What should others do?

Options:

1. Create unlimited connections ❌

2. Fail immediately ❌

3. Wait until connection available ✅

---

This is where Semaphore comes.

---

# 7. Semaphore + Connection Pool

Semaphore controls:

```
Maximum active connections
```

Example:

```java
Semaphore semaphore =
        new Semaphore(5);
```

Meaning:

Only 5 threads can acquire a connection.

---

Flow:

```text
Thread A

acquire()

 |
 v

Connection


Thread B

acquire()

 |
 v

Connection


Thread F

acquire()

WAIT

```

---

# 8. Pool Design

Let's design:

```text
ConnectionPool


    Semaphore

        |

    Available Queue

        |

    Connections

```

---

# 9. Connection Pool Implementation

## Connection Class

```java
class DBConnection {


    private final int id;


    DBConnection(int id){

        this.id=id;

    }


    public void execute(String query){

        System.out.println(
            "Executing " + query +
            " using connection " + id
        );

    }


}
```

---

# 10. Connection Pool

```java
class ConnectionPool {


    private final BlockingQueue<DBConnection> pool;


    private final Semaphore semaphore;



    ConnectionPool(int size){


        pool =
            new LinkedBlockingQueue<>();


        semaphore =
            new Semaphore(size);



        for(int i=1;i<=size;i++){

            pool.offer(
                new DBConnection(i)
            );

        }

    }


}
```

---

# 11. Borrow Connection

```java
public DBConnection borrow()
        throws InterruptedException {


    semaphore.acquire();


    return pool.take();

}
```

---

Flow:

```
Thread

 |
 |
semaphore.acquire()

 |
 |
take connection

 |
 |
return connection

```

---

# 12. Return Connection

```java
public void release(DBConnection connection){


    if(connection != null){

        pool.offer(connection);

        semaphore.release();

    }

}
```

---

Important:

Order matters.

Correct:

```
Return connection

then

Release permit

```

because permit represents available resource.

---

# 13. Usage Example

```java
public class Application {


public static void main(String[] args)
throws Exception {


ConnectionPool pool =
        new ConnectionPool(3);



ExecutorService executor =
        Executors.newFixedThreadPool(10);



for(int i=0;i<10;i++){


executor.submit(() -> {


    DBConnection connection=null;


    try {


        connection =
            pool.borrow();


        connection.execute(
            "SELECT * FROM USER"
        );


    }
    catch(Exception e){

    }
    finally {


        pool.release(connection);

    }


});


}


}


}
```

---

# 14. Execution Example

Pool size:

```
3
```

Threads:

```
10
```

Initial:

```text
Available:

C1 C2 C3

```

---

First three:

```text
Thread 1 -> C1

Thread 2 -> C2

Thread 3 -> C3

```

Queue:

```text
Thread 4
Thread 5
Thread 6

WAITING

```

---

Thread 1 finishes:

```text
release(C1)

```

Now:

```text
Thread 4 gets C1

```

---

# 15. Why BlockingQueue?

Could we use ArrayList?

Example:

```java
List<Connection> connections;
```

Problem:

Multiple threads:

```text
Thread A

remove connection


Thread B

remove connection

```

Need synchronization.

BlockingQueue already provides:

* thread safety
* waiting
* coordination

---

# 16. Why Semaphore If We Already Have Queue?

Good interview question.

They solve different things.

Queue manages:

```
Which connections are available
```

Semaphore manages:

```
How many threads can acquire
```

---

Example:

Queue:

```
[C1,C2,C3]
```

Semaphore:

```
permits = 3
```

Both represent different concepts.

---

# 17. Handling Timeout

Real systems cannot wait forever.

Example:

```java
connection =
pool.borrow(5 seconds);
```

Implementation:

```java
public DBConnection borrow()
throws InterruptedException {


boolean acquired =
    semaphore.tryAcquire(
        5,
        TimeUnit.SECONDS
    );


if(!acquired){

    throw new RuntimeException(
        "Connection timeout"
    );

}


return pool.take();

}
```

---

Production behaviour:

```
No connection available

       |
       v

Wait 5 seconds

       |
       v

Fail gracefully

```

---

# 18. Connection Validation

Real connection pools check:

"Is this connection still alive?"

Example:

```java
if(connection.isClosed()){

    createNewConnection();

}
```

Because:

* database restart
* network failure
* idle timeout

---

# 19. Connection Leak Problem ⭐⭐⭐⭐⭐

Very important interview question.

Example:

```java
Connection c =
    pool.borrow();


execute();


```

Developer forgot:

```java
release(c);
```

Now:

```
Connection lost forever
```

After many requests:

```
Pool exhausted
```

---

Solution:

## Timeout tracking

Maintain:

```text
Connection

borrow time

owner thread

```

Example:

```
Connection C1

borrowed:

10:00:00


current:

10:10:00


Duration:

10 minutes

```

Alert.

---

# 20. Production Connection Pool Features

Real pools like:

* HikariCP
* Apache DBCP

provide:

## Maximum Pool Size

Example:

```
20 connections
```

---

## Minimum Idle Connections

Example:

```
Keep 5 ready
```

---

## Connection Timeout

Example:

```
30 seconds
```

---

## Idle Timeout

Close unused connections.

---

## Max Lifetime

Recycle old connections.

---

# 21. Interview Design Answer

Question:

> Design a thread-safe connection pool.

Strong answer:

```
I would maintain a BlockingQueue of available connections.
A Semaphore controls maximum concurrent usage.
Threads acquire a permit before borrowing a connection.
If no connection is available they wait with timeout.
After usage, connection is returned and permit released.
I would add validation, leak detection, and lifecycle management.
```

---

# 22. Concepts Used From Previous Chapters

This design combines:

| Concept          | Usage                       |
| ---------------- | --------------------------- |
| Semaphore        | Limit active connections    |
| BlockingQueue    | Store available connections |
| ExecutorService  | Execute client tasks        |
| Thread safety    | Protect shared pool         |
| Timeout          | Avoid indefinite waiting    |
| finally block    | Resource release            |
| Immutable config | Pool configuration          |

---

# Connection Pool Design Completed ✅

Next:

# Concurrency System Design Example 2 — Designing a Distributed Rate Limiter ⭐⭐⭐⭐⭐

We will design:

* Single machine rate limiter
* Distributed rate limiter
* Token bucket implementation
* Redis data model
* Lua atomic script
* API Gateway integration
* Handling Redis failure

This is one of the highest-frequency senior backend system design questions.

# Concurrency System Design Example 2 — Designing a Distributed Rate Limiter ⭐⭐⭐⭐⭐

We already covered **Rate Limiter algorithms**.

Now we will design a **production-grade distributed rate limiter**.

This is a very common **Senior Backend / System Design interview question**.

Interviewers expect you to discuss:

* Where rate limiter should live
* Algorithm selection
* Data model
* Concurrency handling
* Distributed consistency
* Redis usage
* Failure handling
* Scaling

---

# 1. Problem Statement

Design a rate limiter for an API platform.

Example:

API:

```text
POST /payment
```

Requirement:

```text
Each user can make:

100 requests/minute
```

If user exceeds:

```text
Return HTTP 429 Too Many Requests
```

---

# 2. Why Do We Need a Distributed Rate Limiter?

A simple application:

```text
              Client


                |
                v


          Application Server


                |
                v


             Counter


```

works only for one machine.

But production:

```text
                 Client


                   |
                   v


              Load Balancer


          ---------------------

          |         |         |

       Server1   Server2   Server3


```

Now the problem:

Each server has its own counter.

---

Example:

Limit:

```text
100 requests/minute
```

Request distribution:

```text
Server 1

50 requests


Server 2

50 requests


Server 3

50 requests

```

Each server thinks:

```text
50 < 100

Allow
```

Total:

```text
150 requests
```

Limit violated.

---

Need shared state.

---

# 3. High-Level Architecture

Production design:

```text
                    Client


                      |
                      v


                API Gateway


                      |
                      v


             Rate Limiter Service


                      |
                      v


                Redis Cluster


                      |
                      v


             Backend Services

```

---

Components:

## API Gateway

Responsibilities:

* Authentication
* Extract user identity
* Apply rate limit
* Reject early

Examples:

* NGINX
* Kong
* Spring Cloud Gateway

---

## Rate Limiter

Responsible for:

* Algorithm execution
* Checking limits
* Returning decision

---

## Redis

Stores:

* Counters
* Token state
* Timestamps

Why Redis?

Because:

* Very fast
* Distributed
* Atomic operations
* TTL support

---

# 4. Choosing Algorithm

Options:

## Fixed Window

Simple:

```text
100 requests/minute
```

Problem:

Burst at boundary.

---

## Sliding Window Log

Accurate.

Problem:

Memory expensive.

---

## Sliding Window Counter

Balanced.

---

## Token Bucket ⭐

Most commonly used.

Why?

Because it supports:

* steady traffic
* controlled bursts

---

We will design using:

# Token Bucket Algorithm

---

# 5. Token Bucket Design

Each user gets a bucket.

Example:

User:

```text
user123
```

Configuration:

```text
Capacity = 100 tokens

Refill = 100 tokens/minute

```

Initial:

```text
Tokens = 100

```

---

Request:

```text
Incoming request

        |
        v

Check token


        |
        v

Available?

        |
        +---- Yes

             Remove token

             Allow


        |
        +---- No

             Reject

```

---

# 6. Redis Data Model

Need to store:

* Current tokens
* Last refill time

Example Redis key:

```text
rate_limit:user123
```

Value:

```json
{
 "tokens":80,
 "lastRefill":1720000000
}
```

---

Better production approach:

Store separately:

```text
Key:

rate_limit:user123


Hash:

tokens       80

last_refill  1720000000

```

---

# 7. The Race Condition Problem

Imagine:

Current tokens:

```text
1 token
```

Two requests arrive.

Server A:

```text
GET tokens

=1
```

Server B:

```text
GET tokens

=1
```

Both allow.

Result:

```text
2 requests accepted
```

But only one token existed.

---

Need atomic operation.

---

# 8. Redis Lua Script ⭐⭐⭐⭐⭐

Redis Lua scripts execute atomically.

Meaning:

```text
Read

Calculate

Update

```

all happen as one operation.

No other request can interrupt.

---

Flow:

```text
Request


 |
 v


Redis Lua Script


 |
 |
 +---- Calculate refill


 |
 |
 +---- Check tokens


 |
 |
 +---- Decrement token


 |
 v


Allow / Reject

```

---

# 9. Token Bucket Lua Logic

Pseudo logic:

```text
tokens = current tokens

lastRefill = stored timestamp


elapsedTime =
currentTime - lastRefill


newTokens =
elapsedTime * refillRate


tokens =
min(
 capacity,
 tokens + newTokens
)


if tokens >= 1:

    tokens--

    allow


else:

    reject


save tokens

save timestamp

```

---

# 10. Java Implementation (Single Node)

Before distributed version, understand local implementation.

```java
public class TokenBucketRateLimiter {


    private final int capacity;

    private final int refillRate;


    private int tokens;

    private long lastRefillTime;



    public TokenBucketRateLimiter(
            int capacity,
            int refillRate) {


        this.capacity = capacity;
        this.refillRate = refillRate;

        this.tokens = capacity;

        this.lastRefillTime =
                System.currentTimeMillis();

    }



    public synchronized boolean allowRequest(){


        refill();


        if(tokens > 0){

            tokens--;

            return true;

        }


        return false;

    }



    private void refill(){


        long now =
            System.currentTimeMillis();


        long elapsed =
            now - lastRefillTime;



        int refillTokens =
            (int)
            ((elapsed / 1000)
            * refillRate);



        if(refillTokens > 0){


            tokens =
                Math.min(
                    capacity,
                    tokens + refillTokens
                );


            lastRefillTime = now;

        }

    }

}

```

---

# 11. Why synchronized Here?

Because:

```java
allowRequest()
```

is executed by multiple threads.

Problem:

Thread A:

```text
tokens = 1

check

```

Thread B:

```text
tokens = 1

check

```

Both consume.

Need atomicity.

---

# 12. Distributed Version

Now multiple servers:

```text
                Request


                   |
                   v


              Server 1


                   |
                   v


                Redis


                   ^
                   |
                   |

              Server 2

```

All servers share the same bucket.

---

# 13. Request Flow

Example:

User:

```text
user123
```

Request:

```text
POST /payment
```

Flow:

```text
1. Gateway receives request


2. Extract userId


3. Call Rate Limiter


4. Redis Lua executes


5. Response:


   Allowed?


       Yes
        |
        v

   Forward request



       No
        |
        v

   HTTP 429

```

---

# 14. Handling Different Limits

Real systems have multiple policies.

Example:

Free user:

```text
100 requests/min
```

Premium user:

```text
10000 requests/min
```

Store configuration:

```text
user123

plan=premium

limit=10000

```

---

# 15. API Level Rate Limiting

Example:

Payment:

```text
10/min
```

Search:

```text
1000/min
```

Key:

```text
rate_limit:user123:/payment

rate_limit:user123:/search

```

---

# 16. Handling Burst Traffic

Token bucket handles bursts.

Example:

Capacity:

```text
100 tokens
```

User idle for 1 minute.

Bucket:

```text
100 tokens
```

Suddenly:

```text
100 requests
```

All can pass.

After that:

Refill controls speed.

---

# 17. Redis Failure Handling ⭐⭐⭐⭐⭐

Important interview topic.

What happens if Redis is unavailable?

Options:

---

## Option 1: Fail Open

Allow requests.

```text
Redis down

       |
       v

Allow traffic

```

Pros:

* Availability

Cons:

* Possible abuse

Used for:

```text
non-critical APIs
```

---

## Option 2: Fail Closed

Reject requests.

```text
Redis down

       |
       v

Reject traffic

```

Pros:

* Protection

Cons:

* Availability issue

Used for:

```text
security-sensitive APIs
```

---

# 18. Scaling Redis

Problem:

Millions of users.

Keys:

```text
rate_limit:user1

rate_limit:user2

rate_limit:user3

...

```

Solution:

Redis Cluster.

Keys distributed:

```text
Node 1

user1
user2


Node 2

user3
user4


Node 3

user5
user6

```

---

# 19. Hot Key Problem

Example:

Celebrity API:

```text
user=elon
```

Millions of requests.

One Redis key:

```text
rate_limit:elon
```

becomes hotspot.

Solutions:

## Local cache

Keep small counters locally.

---

## Key sharding

Instead of:

```text
elon
```

Use:

```text
elon:1

elon:2

elon:3

```

---

# 20. Rate Limiter Placement

## Client Side

Bad.

Cannot trust clients.

---

## Service Level

Good.

Each service protects itself.

---

## API Gateway ⭐

Best common choice.

Because:

* reject early
* saves backend resources
* centralized control

---

# 21. Monitoring

Production rate limiter needs:

Metrics:

```text
allowed_requests

rejected_requests

redis_latency

token_exhaustion

```

Alerts:

```text
High 429 rate

Redis failures

```

---

# 22. Interview Architecture Answer

Question:

> Design a distributed rate limiter.

Strong answer:

```text
I would place the rate limiter at the API Gateway layer.

For the algorithm I would choose Token Bucket because it supports controlled bursts.

The bucket state would be stored in Redis so multiple application instances share the same state.

Redis Lua scripts would provide atomic token refill and deduction.

For high availability, Redis Cluster would be used with proper failure handling.

Rejected requests would return HTTP 429 with Retry-After headers.

```

---

# 23. Concepts Used From Concurrency Module

| Concept                 | Usage                   |
| ----------------------- | ----------------------- |
| synchronized            | Local atomic operations |
| CAS concept             | Atomic state updates    |
| Redis atomic execution  | Distributed locking     |
| Semaphore idea          | Limiting concurrency    |
| Thread safety           | Shared state protection |
| Immutable configuration | Rate policies           |

---

# Distributed Rate Limiter Completed ✅

Next:

# Concurrency System Design Example 3 — Designing an Async Job Executor ⭐⭐⭐⭐⭐

We will design:

* Task queue
* Worker threads
* ThreadPoolExecutor internals
* Retry mechanism
* Dead Letter Queue
* Scheduling
* Failure handling
* Backpressure

This is extremely common in backend interviews.

