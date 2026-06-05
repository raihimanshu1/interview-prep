# Java / Spring / Backend Quick Refresher

## `.equals()` vs `==`

### What Is It?

`==` compares references for objects.

`.equals()` compares logical equality if the class overrides it.

In simple terms:

```text
==        -> are these the same object in memory?
.equals  -> do these objects mean the same value?
```

### Example

```java
String a = new String("Wells");
String b = new String("Wells");

System.out.println(a == b);      // false
System.out.println(a.equals(b)); // true
```

### Why It Matters

In backend systems, IDs, strings, DTOs, and entity keys are compared constantly.

Wrong comparison can cause:

```text
wrong cache lookup
wrong authorization check
duplicate detection failure
HashMap lookup bugs
```

### Common Mistake

```java
if (status == "APPROVED") { }
```

Better:

```java
if ("APPROVED".equals(status)) { }
```

### Interview Answer

`==` checks whether two object references point to the same object. `.equals()` checks logical equality, assuming the class implements it correctly. For primitives, `==` compares values. For objects like `String`, DTOs, and entity IDs, I use `.equals()` unless I intentionally want reference comparison.

---

## Servlet vs JSP

### What Is It?

A Servlet is a Java class that handles HTTP requests and responses.

JSP is a view technology that lets HTML contain Java-like dynamic content.

In simple terms:

```text
Servlet -> controller/request handling
JSP     -> server-side HTML rendering
```

### Example

Servlet:

```java
protected void doGet(HttpServletRequest req, HttpServletResponse res) {
    req.setAttribute("name", "Himanshu");
    req.getRequestDispatcher("/profile.jsp").forward(req, res);
}
```

JSP:

```jsp
Hello ${name}
```

### Modern Spring Mindset

In Spring Boot:

```text
Servlet API still exists underneath Spring MVC.
Controllers replaced handwritten servlets for most apps.
REST APIs usually return JSON, not JSP pages.
```

### Interview Answer

A Servlet is Java request-handling code, while JSP is mainly used to render dynamic HTML. In modern Spring Boot applications, I usually use `@RestController` for JSON APIs or MVC controllers with templates, but the underlying web stack still uses Servlet concepts like request, response, filters, and sessions.

---

## `volatile` Keyword

### What Is It?

`volatile` guarantees visibility of a variable across threads.

It does not make compound operations atomic.

```text
Good for: stop flag, config reference
Bad for: count++, balance updates
```

### Example

```java
class Worker {
    private volatile boolean running = true;

    void stop() {
        running = false;
    }

    void run() {
        while (running) {
            // keep working
        }
    }
}
```

### Broken Example

```java
volatile int count = 0;
count++; // not thread-safe
```

`count++` is:

```text
read
increment
write
```

Use:

```java
AtomicInteger count = new AtomicInteger();
count.incrementAndGet();
```

### Interview Answer

`volatile` gives visibility and ordering guarantees, so when one thread writes a volatile variable, another thread can see the updated value. But it does not provide mutual exclusion, so compound operations like `count++` are still unsafe. For counters I use atomics; for multi-step invariants I use locks or transactions.

---

## Thread Safety

### What Is It?

Code is thread-safe when multiple threads can use it at the same time without corrupting state.

In simple terms:

```text
Concurrent calls should produce correct results.
```

### Safe Example

```java
class PriceCalculator {
    BigDecimal total(BigDecimal amount, BigDecimal fee) {
        return amount.add(fee);
    }
}
```

This is safe because it uses only local variables and immutable `BigDecimal`.

### Unsafe Example

```java
@Service
class TransferService {
    private String currentRequestId;

    void process(String requestId) {
        currentRequestId = requestId;
    }
}
```

Spring services are usually singleton beans, so this field is shared across requests.

### Fixes

```text
Prefer stateless services
Use local variables
Use immutable objects
Use Atomic classes for simple counters
Use locks for critical sections
Use DB transactions for persisted state
```

### Interview Answer

Thread safety means shared state remains correct under concurrent access. In Spring Boot, the safest service is usually stateless because singleton beans serve many requests. If shared state is unavoidable, I use immutability, atomics, synchronization, locks, or database transactions depending on the operation.

---

## Which Pattern Suits Plugin Architecture?

### Best Answer

Use a combination of:

```text
Strategy pattern
Factory pattern
Service Provider Interface (SPI)
Dependency Injection
```

### Why Strategy?

Each plugin implements the same contract differently.

```java
interface PaymentPlugin {
    boolean supports(String type);
    PaymentResult process(PaymentCommand command);
}
```

Implementations:

```java
class CardPaymentPlugin implements PaymentPlugin { }
class WirePaymentPlugin implements PaymentPlugin { }
class AchPaymentPlugin implements PaymentPlugin { }
```

### Why Factory?

Factory selects the right plugin.

```java
class PaymentPluginFactory {
    PaymentPlugin find(String type) {
        return plugins.stream()
            .filter(p -> p.supports(type))
            .findFirst()
            .orElseThrow();
    }
}
```

### Spring Boot Mindset

Spring can inject all implementations:

```java
PaymentPluginFactory(List<PaymentPlugin> plugins) {
    this.plugins = plugins;
}
```

### Interview Answer

For plugin architecture, I would use Strategy because every plugin follows the same interface but provides different behavior. I would use a Factory or registry to select the correct plugin at runtime. In Spring Boot, I can inject a list of implementations and route based on type. This keeps the system open for new plugins without modifying core business logic.

---

## REST API Versioning Best Practices

### What Is It?

API versioning lets the backend evolve without breaking old clients.

Common options:

```text
/api/v1/users
X-API-Version: 2
Accept: application/vnd.company.v2+json
```

### Best Practices

```text
Use additive changes when possible
Version only for breaking changes
Keep old versions during migration
Use DTOs per version
Add contract tests
Publish deprecation timelines
Monitor old-client usage
```

### Breaking Change

```json
{
  "id": 1,
  "name": "John"
}
```

changed to:

```json
{
  "userId": 1,
  "fullName": "John"
}
```

Old clients break.

### Safe Change

```json
{
  "id": 1,
  "name": "John",
  "userId": 1,
  "fullName": "John"
}
```

Old clients still work.

### Interview Answer

I version APIs when the contract changes in a breaking way. For additive fields, I avoid creating a new version. For breaking changes, URI versioning like `/api/v1` and `/api/v2` is simple and clear. I also use DTOs, contract tests, deprecation windows, and telemetry before removing old versions.

---

## Eventual Consistency vs Immediate Consistency

### Immediate Consistency

After a write, every read immediately sees the latest value.

```text
Write balance = 100
Read balance -> 100 immediately
```

Best for:

```text
ledger writes
account balance correctness
transactional DB updates
```

### Eventual Consistency

After a write, different systems may temporarily see old data, but they converge later.

```text
Payment created
Kafka event published
Notification service receives later
Analytics updates later
```

Best for:

```text
notifications
analytics
search indexes
read replicas
cross-region replication
```

### Memory Visibility Angle

In Java threads, visibility is different from distributed consistency.

```text
volatile/synchronized/locks -> memory visibility between threads
database/events/replication -> distributed consistency between systems
```

### Interview Answer

Immediate consistency means reads reflect writes right away. Eventual consistency means systems may temporarily disagree but will converge. For financial ledger updates, I prefer strong consistency. For notifications, analytics, and replicated views, eventual consistency is acceptable if users and downstream systems can tolerate delay.

---

## `synchronized` vs `Lock` - Complete Interview Deep Dive

This is one of the most common Java concurrency interview questions.

Most candidates say:

```text
synchronized is keyword.
Lock is interface.
```

That is correct, but too shallow.

In a senior interview, the interviewer wants to know:

```text
Why synchronization is needed
What race condition means
How synchronized uses object monitor
Difference between method and block synchronization
Static synchronization
Why Lock/ReentrantLock was introduced
tryLock, timeout, interruptible lock
Fair vs unfair locking
Condition variable
Deadlock possibility
Performance in modern JVM
When to use which one
```

---

### Why Do We Need Synchronization?

Suppose two threads update the same bank account balance.

Initial balance:

```text
balance = 1000
```

Thread 1:

```java
balance = balance - 100;
```

Thread 2:

```java
balance = balance - 200;
```

Expected final balance:

```text
700
```

Possible wrong balance:

```text
800
```

Why?

Because both threads may read the old value at the same time.

```text
Thread 1 reads 1000
Thread 2 reads 1000
Thread 1 writes 900
Thread 2 writes 800
```

The deduction of `100` is lost.

This is called:

```text
Race condition
```

Race condition means:

```text
Multiple threads access and modify shared data at the same time,
and final result depends on timing.
```

---

### What Is Critical Section?

Critical section means:

```text
Code that accesses shared mutable data.
```

Example:

```java
balance = balance - amount;
```

This looks like one line, but internally it is:

```text
read balance
subtract amount
write balance
```

So it must be protected when multiple threads can execute it.

---

### Solution

Allow only one thread at a time to enter the critical section.

Java gives two common ways:

```text
1. synchronized
2. Lock / ReentrantLock
```

---

### `synchronized`

`synchronized` is built into the Java language.

Example:

```java
class Account {

    private int balance = 1000;

    public synchronized void withdraw(int amount) {
        balance = balance - amount;
    }
}
```

Only one thread can execute `withdraw()` on the same object at a time.

---

### What Actually Happens Internally?

Every Java object can be used as a monitor lock.

```java
Object lock = new Object();
```

Conceptually:

```text
Object
  |
  +-- Monitor
        |
        +-- Lock
```

When a thread enters:

```java
synchronized (lock) {
    // critical section
}
```

JVM does:

```text
Acquire monitor
Execute critical section
Release monitor
```

If another thread tries to enter the same monitor:

```text
It waits.
```

---

### Method-Level Synchronization

```java
public synchronized void increment() {
    count++;
}
```

This is equivalent to:

```java
public void increment() {
    synchronized (this) {
        count++;
    }
}
```

Lock object:

```text
this
```

Meaning:

```text
Only one synchronized instance method can run on the same object at a time.
```

---

### Block-Level Synchronization

Instead of locking the whole method:

```java
public void increment() {
    synchronized (this) {
        count++;
    }
}
```

Better when only a small part needs locking.

Why?

```text
Smaller lock scope
Less waiting
Better readability around shared state
```

---

### Static Synchronization

```java
public static synchronized void printReport() {
}
```

This does not lock `this`.

Static method belongs to class, not object.

Lock is:

```text
MyClass.class
```

Equivalent:

```java
synchronized (MyClass.class) {
}
```

---

### Follow-Up: Can Two Synchronized Methods Run Simultaneously?

Case 1:

```java
obj1.method();
obj2.method();
```

Different objects.

Different locks.

Answer:

```text
Yes, they can run simultaneously.
```

Case 2:

```java
obj.method1();
obj.method2();
```

Same object.

Same monitor lock.

Answer:

```text
No, only one synchronized method can run at a time.
```

Case 3:

```java
obj.instanceMethod();
MyClass.staticMethod();
```

Locks are different:

```text
obj instance lock
class lock
```

Answer:

```text
Yes, they can run simultaneously.
```

---

### Is `synchronized` Reentrant?

Yes.

Same thread can enter another synchronized method protected by the same lock.

Example:

```java
class Service {

    public synchronized void outer() {
        inner();
    }

    public synchronized void inner() {
        // same thread can enter again
    }
}
```

Without reentrancy:

```text
The thread would deadlock itself.
```

---

### Problems With `synchronized`

Suppose one thread enters:

```java
synchronized (lock) {
    callSlowService();
}
```

Other threads wait.

Problems:

```text
Cannot check if lock is available
Cannot timeout while waiting for lock
Cannot interrupt waiting thread easily
No fairness control
Only one wait set per object monitor
```

So Java introduced:

```text
Lock API
```

---

### Lock Interface

Package:

```java
java.util.concurrent.locks
```

Most common implementation:

```java
ReentrantLock
```

Example:

```java
private final Lock lock = new ReentrantLock();

public void increment() {
    lock.lock();

    try {
        count++;
    } finally {
        lock.unlock();
    }
}
```

---

### Why `try-finally`?

This is a favorite interview follow-up.

Wrong:

```java
lock.lock();
count++;
lock.unlock();
```

If exception happens before `unlock()`:

```text
Lock is never released.
Other threads wait forever.
```

Correct:

```java
lock.lock();

try {
    count++;
} finally {
    lock.unlock();
}
```

`finally` guarantees unlock.

---

### Why Is It Called `ReentrantLock`?

Because the same thread can acquire the same lock multiple times.

Example:

```java
private final ReentrantLock lock = new ReentrantLock();

public void outer() {
    lock.lock();
    try {
        inner();
    } finally {
        lock.unlock();
    }
}

public void inner() {
    lock.lock();
    try {
        // work
    } finally {
        lock.unlock();
    }
}
```

Lock hold count:

```text
outer lock -> count = 1
inner lock -> count = 2
inner unlock -> count = 1
outer unlock -> count = 0
```

Only when count becomes `0`, lock is fully released.

---

### Major Feature 1: `tryLock()`

With `synchronized`:

```text
Wait until lock is available.
```

With `tryLock()`:

```java
if (lock.tryLock()) {
    try {
        count++;
    } finally {
        lock.unlock();
    }
} else {
    // do something else
}
```

Meaning:

```text
Try immediately.
If not available, do not wait.
```

Useful when:

```text
You want fail-fast behavior
You want fallback
You want to avoid thread blocking
```

---

### Major Feature 2: Timed Lock

```java
if (lock.tryLock(5, TimeUnit.SECONDS)) {
    try {
        // critical section
    } finally {
        lock.unlock();
    }
} else {
    // timeout
}
```

Meaning:

```text
Wait maximum 5 seconds.
If lock not available, return false.
```

Useful in backend systems because:

```text
Requests should not wait forever.
```

---

### Major Feature 3: Interruptible Lock

```java
lock.lockInterruptibly();
```

If thread is waiting for lock:

```java
thread.interrupt();
```

waiting thread can stop waiting.

Useful for:

```text
task cancellation
shutdown
timeout-driven systems
```

With `synchronized`, waiting to enter a monitor is not interruptible in the same flexible way.

---

### Major Feature 4: Fair Lock

Default:

```java
new ReentrantLock();
```

This is unfair lock.

Meaning:

```text
A new thread may acquire lock before older waiting thread.
```

Fair lock:

```java
new ReentrantLock(true);
```

Meaning:

```text
Threads acquire lock roughly in waiting order.
```

Example:

```text
T1 waits first
T2 waits second
T3 waits third
```

Fair lock tries:

```text
T1 -> T2 -> T3
```

Interview question:

```text
Why is fair lock not default?
```

Answer:

```text
Fairness reduces throughput because JVM has extra scheduling overhead.
Unfair locks usually perform better.
```

---

### Condition Variable

With `synchronized`, threads coordinate using:

```java
wait();
notify();
notifyAll();
```

With `Lock`, we use:

```java
Condition
```

Example:

```java
private final Lock lock = new ReentrantLock();
private final Condition notEmpty = lock.newCondition();
```

Wait:

```java
notEmpty.await();
```

Signal:

```java
notEmpty.signal();
```

Signal all:

```java
notEmpty.signalAll();
```

Why useful?

```text
Lock can create multiple Condition objects.
synchronized has one wait set per monitor.
```

---

### Deadlock

Deadlock can happen with both:

```text
synchronized
ReentrantLock
```

Example:

Thread 1:

```text
lockA -> lockB
```

Thread 2:

```text
lockB -> lockA
```

Both wait forever.

Fixes:

```text
Always acquire locks in same order
Keep lock scope small
Avoid calling external services while holding lock
Use tryLock with timeout
Prefer higher-level concurrency utilities when possible
```

---

### Performance

Old answer:

```text
ReentrantLock is faster.
```

This is not a safe modern answer.

Modern JVM has optimized `synchronized` heavily:

```text
biased locking in older JVMs
lightweight locking
lock elimination
lock coarsening
JIT optimizations
```

Correct answer:

```text
For most normal cases, performance is close.
Choose based on feature requirement and readability.
```

Do not say:

```text
Lock is always faster.
```

That sounds outdated.

---

### Comparison Table

| Feature | `synchronized` | `ReentrantLock` |
|---|---|---|
| Type | Java keyword | Java API/class |
| Lock release | Automatic | Manual |
| Needs `finally` | No | Yes |
| Reentrant | Yes | Yes |
| `tryLock()` | No | Yes |
| Timeout | No | Yes |
| Interruptible wait | Limited | Yes |
| Fairness | No direct control | Yes |
| Conditions | `wait/notify` | `Condition` |
| Simplicity | Easier | More code |
| Flexibility | Lower | Higher |

---

### When Would You Use `synchronized`?

Use when:

```text
Simple mutual exclusion
Small critical section
No timeout needed
No fairness needed
Readability matters
```

Example:

```java
synchronized (counter) {
    counter.increment();
}
```

---

### When Would You Use `ReentrantLock`?

Use when:

```text
Need tryLock
Need timeout
Need interruptible lock
Need fairness
Need multiple conditions
Need advanced lock control
```

Example:

```java
if (lock.tryLock(2, TimeUnit.SECONDS)) {
    try {
        process();
    } finally {
        lock.unlock();
    }
}
```

---

### Real-World Banking Note

For in-memory state inside one JVM:

```text
synchronized / Lock can help.
```

For database-backed money movement:

```text
Use database transactions
row-level locks
optimistic locking
idempotency keys
ledger correctness
```

Why?

Because JVM locks do not protect data across:

```text
multiple pods
multiple servers
multiple service instances
```

---

### Common Follow-Up Questions

#### Is `synchronized` reentrant?

Yes.

Same thread can reacquire the same monitor.

#### Is `ReentrantLock` reentrant?

Yes.

That is why it is called `ReentrantLock`.

#### Can `synchronized` cause deadlock?

Yes.

If locks are acquired in inconsistent order.

#### Can `ReentrantLock` cause deadlock?

Yes.

Especially if `unlock()` is missed or lock order is inconsistent.

#### Does `synchronized` release lock on exception?

Yes.

JVM releases monitor when block/method exits.

#### Does `ReentrantLock` release automatically?

No.

You must call:

```java
unlock();
```

usually inside `finally`.

#### Which one is preferred today?

For simple cases:

```text
synchronized
```

For advanced concurrency:

```text
ReentrantLock
```

---

### Senior-Level Interview Answer

> Synchronization is needed to protect shared mutable state from race conditions. `synchronized` is a Java keyword that uses an object monitor. It is simple, reentrant, and automatically releases the lock when the block exits, even during exceptions. It is best for small and simple critical sections. `ReentrantLock` provides more advanced features like `tryLock`, timeout, interruptible locking, fair locking, and multiple `Condition` variables, but it requires manual unlock in a `finally` block. In modern JVMs, performance is usually close, so I choose based on readability and required features. For distributed banking data, I do not rely only on JVM locks; I use database transactions, row locks, optimistic locking, and idempotency.

---

## Microservices Return 500: Debug Steps

### What Is It?

HTTP 500 means the server failed unexpectedly.

In microservices, the root cause may be:

```text
current service
downstream service
database
cache
message broker
bad deployment
bad config
```

### Debug Flow

```text
1. Check request path, method, payload, headers
2. Find correlation ID / trace ID
3. Check logs for that trace
4. Check recent deployments/config changes
5. Check downstream errors
6. Check DB/cache/broker health
7. Check metrics: latency, error rate, saturation
8. Reproduce with same payload in lower environment
9. Add fix or rollback
```

### Production Mindset

Do not only ask:

```text
Which line threw exception?
```

Ask:

```text
Why did this input reach that code?
Why was it not validated?
Why did monitoring not catch it earlier?
```

### Interview Answer

For a 500 in microservices, I start with trace ID and logs, then check recent deploys, request payload, downstream calls, database/cache health, and metrics. I isolate whether the failure is validation, code bug, dependency failure, timeout, or bad config. In production, I either rollback, apply a feature flag, or patch safely, then add tests and monitoring.

---

## Git Commands To Combine Branches

### Merge Branch Into Current Branch

```bash
git checkout main
git pull
git checkout feature-a
git merge main
```

or:

```bash
git checkout main
git merge feature-a
```

### Rebase Feature Branch

```bash
git checkout feature-a
git fetch origin
git rebase origin/main
```

### Squash Commits

```bash
git rebase -i HEAD~3
```

### Create One Combined Branch From Two Features

```bash
git checkout main
git pull
git checkout -b combined-feature
git merge feature-a
git merge feature-b
```

### Interview Answer

To combine branches, I usually merge when I want to preserve branch history and rebase when I want a cleaner linear feature history before opening a PR. For combining two features, I create a new branch from updated main and merge both feature branches into it, resolving conflicts carefully and running tests.

---

## Executor Service

### What Is It?

`ExecutorService` manages a pool of threads and runs submitted tasks asynchronously.

In simple terms:

```text
Instead of creating threads manually,
submit tasks to a managed pool.
```

### Example

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

Future<String> result = executor.submit(() -> {
    return callFraudService();
});

executor.shutdown();
```

### Important Concepts

```text
execute() -> no result
submit()  -> returns Future
shutdown() -> stop accepting new tasks
shutdownNow() -> attempt to interrupt
```

### Production Mindset

Avoid:

```java
Executors.newCachedThreadPool();
```

for uncontrolled backend traffic.

Prefer bounded pools:

```java
new ThreadPoolExecutor(
    10,
    10,
    0L,
    TimeUnit.MILLISECONDS,
    new ArrayBlockingQueue<>(100),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

### Interview Answer

`ExecutorService` separates task submission from thread management. It helps reuse threads and control concurrency. In production, I avoid unbounded pools and queues, size the pool based on CPU/downstream capacity, define rejection behavior, set timeouts, and monitor active threads, queue depth, and failures.

---

## Strict Review Fixes: Follow-Up Questions And Senior Details

This section exists to make every quick-refresher topic match the expected interview template: definition, example, common mistake, production mindset, follow-ups, and final answer.

### `.equals()` vs `==` Follow-Ups

Likely follow-up questions:

```text
Why does == sometimes work for string literals?
What happens if equals() is not overridden?
Why must hashCode() be overridden with equals()?
How do you compare objects safely when null is possible?
```

Senior detail:

```text
In Java collections like HashMap and HashSet, equals() and hashCode()
must be consistent. If two objects are equal, they must have same hashCode.
```

### Servlet vs JSP Follow-Ups

Why it matters:

```text
This checks whether you understand old Java web architecture and why
modern Spring applications moved toward controller + REST patterns.
```

Safe usage:

```text
Servlet / Spring Controller -> request handling
JSP / template engine       -> rendering view only
REST API                    -> return JSON for frontend/mobile
```

Common mistakes:

```text
Putting business logic inside JSP
Mixing SQL directly into JSP
Using JSP for REST APIs
Not understanding servlet lifecycle
```

Likely follow-up questions:

```text
What is the servlet lifecycle?
How does Spring MVC use DispatcherServlet?
Why is JSP less common in modern microservices?
What is MVC separation?
```

Interview answer:

> Servlet is server-side Java code used to handle HTTP requests and responses. JSP is mainly a view technology used to generate HTML. In modern Spring Boot systems, request handling usually goes through Spring MVC controllers backed by `DispatcherServlet`, and APIs usually return JSON instead of JSP pages. JSP should not contain business logic; it should only render views when server-side rendering is required.

### `volatile` Follow-Ups

Likely follow-up questions:

```text
Does volatile make increment operations atomic?
What problem does volatile solve in double-checked locking?
How is volatile different from synchronized?
When should AtomicInteger be used instead?
```

Senior detail:

```text
volatile gives visibility and ordering guarantees, but not mutual exclusion.
For compound operations like count++, use synchronized, Lock, or atomic classes.
```

### Thread Safety Follow-Ups

Likely follow-up questions:

```text
What makes a class immutable?
Is a stateless Spring service thread-safe?
How do ConcurrentHashMap and HashMap differ under concurrency?
How would you find a race condition in production?
```

Senior detail:

```text
In Spring Boot, singleton beans are shared across requests. They are safe
when stateless, but risky when they store request-specific mutable fields.
```

### Plugin Architecture Follow-Ups

Common mistakes:

```text
Hardcoded if/else for every plugin
Reflection without validation
No stable plugin interface
No version compatibility rules
No isolation when plugin fails
No security check before loading plugin code
```

Production concerns:

```text
Plugin discovery
Plugin configuration
Contract/version compatibility
Failure isolation
Feature flags
Security permissions
Testing plugin behavior independently
Gradual rollout and rollback
```

Likely follow-up questions:

```text
Why Strategy pattern fits plugin behavior?
How would you discover plugins dynamically?
How do you prevent one bad plugin from breaking the app?
How do you version plugin contracts?
```

Interview answer:

> Strategy pattern is a good fit for plugin architecture because each plugin implements the same contract but provides different behavior. For production, I would avoid hardcoded conditionals and define a stable interface, discovery mechanism, configuration, version compatibility, security validation, and failure isolation. This makes the platform extensible without modifying core business logic for every new plugin.

### REST API Versioning Follow-Ups

Mandatory extra details:

```text
Backward compatibility -> old clients continue working
Forward compatibility  -> clients tolerate unknown future fields
Semantic versioning    -> MAJOR.MINOR.PATCH communicates breaking vs safe changes
Deprecation policy     -> warn clients before removing old behavior
Contract testing       -> verify consumers do not break
```

Safe changes:

```text
Add optional field
Add new endpoint
Add optional query parameter
Add response header
```

Breaking changes:

```text
Remove field
Rename field
Change data type
Change meaning of existing field
Make optional field required
```

Likely follow-up questions:

```text
When would you create /v2 API?
How do mobile apps make backward compatibility harder?
What is consumer-driven contract testing?
How do Amazon/Google-style APIs evolve safely?
```

Interview answer:

> I maintain API compatibility by adding rather than changing fields, versioning breaking changes, supporting old clients during migration, and using deprecation windows. For APIs used by web, mobile, and other services, backward compatibility is mandatory because clients deploy independently. I would also use contract tests, semantic versioning for SDKs, and clear API documentation.

### Consistency Follow-Ups

Common mistakes:

```text
Reading stale balance from replica
Using eventual consistency for ledger correctness
Processing duplicate events without idempotency
Assuming async replication means no data loss
Ignoring read-after-write requirements
```

Likely follow-up questions:

```text
Where is eventual consistency acceptable?
Where is immediate consistency required in banking?
How do you handle duplicate events?
How do you explain read-your-write consistency?
```

Interview answer:

> Eventual consistency is acceptable for search, analytics, notifications, and cached views where temporary staleness is allowed. Immediate or strong consistency is required for ledger balances, payments, and transaction correctness. In distributed systems, I define consistency requirements per use case and use idempotency, retries, reconciliation, and monitoring where async processing is involved.

### 500 Debugging Follow-Ups

Likely follow-up questions:

```text
How do you use correlation ID?
How do you separate client issue from server issue?
What dashboards would you check first?
How do retries make an incident worse?
```

Senior detail:

```text
First find the first failing service, not just the service returning the final 500.
Distributed tracing helps identify whether the root cause is DB, downstream API,
timeout, bad deploy, payload, or configuration.
```

### Git Branch Combining Follow-Ups

Common mistakes:

```text
Rebasing shared public branches without team agreement
Resolving conflicts without running tests
Squashing away useful context too early
Merging stale main branch
Force pushing to shared branch accidentally
```

When to use:

```text
merge  -> preserve branch history
rebase -> clean local feature history before PR
squash -> combine many small commits into one reviewable commit
```

Likely follow-up questions:

```text
Merge vs rebase?
How do you resolve conflicts safely?
What is a fast-forward merge?
Why avoid force push on shared branches?
```

Interview answer:

> I use merge when preserving history matters, rebase to clean my local feature branch before review, and squash when the team wants one logical commit. Before combining branches, I update from main, resolve conflicts carefully, run tests, and open a PR. I avoid rewriting shared branch history unless the team explicitly agrees.

### ExecutorService Follow-Ups

Common mistakes:

```text
Using unbounded queues
Using newCachedThreadPool for backend request traffic
Forgetting shutdown()
Calling Future.get() without timeout
Blocking inside a small thread pool
Ignoring rejected task behavior
```

Sizing mindset:

```text
CPU-bound work -> near number of CPU cores
I/O-bound work -> can be larger, but limited by downstream capacity
```

Likely follow-up questions:

```text
execute() vs submit()?
Why should Future.get() have timeout?
How do you size a thread pool?
What is rejection policy?
```

Interview answer:

> `ExecutorService` manages task execution through reusable thread pools. It prevents manual thread creation and gives control over concurrency. In production, I use bounded pools and queues, define rejection policy, set timeouts, shut down gracefully, and monitor active threads, queue depth, failures, and latency.
