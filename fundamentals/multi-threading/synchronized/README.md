# `synchronized` Keyword — Complete Deep Dive

## 1. Why This Concept Matters

`synchronized` is Java's fundamental mechanism for thread safety. It appears in virtually every concurrent Java application, from web servers handling thousands of requests to batch jobs processing records in parallel. Misusing `synchronized` causes deadlocks, thread starvation, and performance bottlenecks. Understanding intrinsic locks, reentrancy, and the difference between method-level and block-level synchronization is mandatory for any concurrency interview. Interviewers test `synchronized` because it reveals your grasp of the Java Memory Model, happens-before guarantees, and lock optimization techniques.

Misunderstanding `synchronized` causes:
- Deadlocks from nested lock ordering
- Performance degradation from coarse-grained locking
- Visibility issues when mixing `synchronized` with non-synchronized access
- Starvation in high-contention scenarios

## 2. Basic Meaning

`synchronized` is a keyword that provides mutual exclusion (mutex) and visibility guarantees. It ensures that only one thread can execute a synchronized block or method at a time for the same lock object.

Key vocabulary:
- **Intrinsic lock / monitor lock**: Every object has an implicit lock. `synchronized` uses this lock.
- **Mutual exclusion**: Only one thread holds the lock; others block until released.
- **Reentrant**: A thread that already holds a lock can reacquire it without deadlocking.
- **Happens-before**: Write inside synchronized block is visible to any thread that subsequently enters synchronized block on same monitor.
- **Monitor**: The mechanism combining lock + wait set + notification.
- **Lock scope**: `synchronized` on instance method locks `this`. On static method locks `Class` object. On block locks specified object.

What it is NOT: `synchronized` does not prevent deadlocks. It does not support timeout (use `ReentrantLock.tryLock` for that). It is not interruptible while waiting (use `Lock` for that).

## 3. Real Code / Real Example

```java
public class SynchronizedDemo {
    private int count = 0;
    private final Object lock = new Object(); // explicit lock object

    public static void main(String[] args) throws InterruptedException {
        SynchronizedDemo demo = new SynchronizedDemo();
        demo.runDemo();
    }

    void runDemo() throws InterruptedException {
        // === METHOD-LEVEL SYNCHRONIZATION ===
        Thread t1 = new Thread(() -> incrementSync(1000));
        Thread t2 = new Thread(() -> incrementSync(1000));
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("Method sync count: " + count); // 2000

        // === BLOCK-LEVEL SYNCHRONIZATION ===
        count = 0;
        Thread t3 = new Thread(() -> incrementBlock(1000));
        Thread t4 = new Thread(() -> incrementBlock(1000));
        t3.start(); t4.start();
        t3.join(); t4.join();
        System.out.println("Block sync count: " + count); // 2000

        // === STATIC SYNCHRONIZATION ===
        Thread t5 = new Thread(() -> incrementStatic(1000));
        Thread t6 = new Thread(() -> incrementStatic(1000));
        t5.start(); t6.start();
        t5.join(); t6.join();
        System.out.println("Static sync count: " + staticCount); // 2000

        // === REENTRANCY ===
        reentrantExample();

        // === WAIT/NOTIFY ===
        waitNotifyExample();
    }

    // Method-level: locks 'this'
    public synchronized void incrementSync(int n) {
        for (int i = 0; i < n; i++) count++;
    }

    // Block-level: locks explicit object
    public void incrementBlock(int n) {
        synchronized (lock) {  // only this block is synchronized
            for (int i = 0; i < n; i++) count++;
        }
    }

    // Static: locks Class object
    private static int staticCount = 0;
    public static synchronized void incrementStatic(int n) {
        for (int i = 0; i < n; i++) staticCount++;
    }

    // Reentrant: same thread can reacquire
    public synchronized void outer() {
        inner(); // allowed: same thread already holds lock
        System.out.println("Outer done");
    }
    public synchronized void inner() {
        System.out.println("Inner entered");
    }

    public void reentrantExample() {
        new Thread(this::outer).start();
    }

    // Wait/notify: producer-consumer
    private final Object waitLock = new Object();
    private boolean ready = false;
    private String message;

    public void waitNotifyExample() throws InterruptedException {
        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(100); // let producer start first
                synchronized (waitLock) {
                    while (!ready) waitLock.wait(); // wait until ready
                    System.out.println("Consumer got: " + message);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        Thread producer = new Thread(() -> {
            synchronized (waitLock) {
                message = "Hello from synchronized";
                ready = true;
                waitLock.notifyAll(); // wake up waiting threads
            }
        });
        consumer.start(); producer.start();
        consumer.join(); producer.join();
    }
}
```

Expected output:
```
Method sync count: 2000
Block sync count: 2000
Static sync count: 2000
Inner entered
Outer done
Consumer got: Hello from synchronized
```

## 4. What Happens Internally

**Monitor structure in JVM:**
Every object in JVM has an associated monitor (lock record). Monitor contains:
- Owner: thread ID of thread holding lock (or -1 if unowned)
- Entry count: reentrancy count (0 = unlocked)
- Wait set: threads waiting via `wait()`
- Blocked threads: threads blocked trying to acquire lock

**Biased locking (JVM optimization):**
1. **Biased Locking (default in HotSpot):** When uncontended, lock "biases" toward first acquiring thread. Subsequent acquires by same thread cost ~0 assembly instructions (no CAS).
2. **Lightweight Locking:** If second thread contends, bias revoked, lock inflated to thin lock (spin + CAS).
3. **Heavyweight Locking:** If contention persists, lock becomes OS mutex. Thread blocks, OS schedules.

**`synchronized` compilation:**
```java
// Source
public synchronized void foo() { ... }

// Bytecode (simplified)
public void foo();
    aload_0          // push 'this'
    dup
    monitorenter     // acquire lock on this
    try {
        ... body ...
    } finally {
        monitorexit    // release lock on this
    }
```

**`synchronized` block compilation:**
```java
// Source
synchronized (lock) { ... }

// Bytecode
aload_1          // push lock object
dup
monitorenter
try { ... } finally { monitorexit }
```

**Happens-before guarantee:**
The Java Memory Model (JMM) guarantees:
1. **Unlock → Lock:** A write inside synchronized block happens-before any read in subsequent synchronized block on same monitor.
2. **Lock → Unlock:** Reads inside synchronized block see writes from previous synchronized block on same monitor.
3. **Exit → Enter:** Changes to shared variables are flushed to main memory on unlock, invalidated CPU caches, and reloaded on lock.

**Reentrancy internals:**
When a thread holding a lock enters another synchronized block requiring same lock, JVM increments the lock's recursion count. Lock fully released only when `monitorexit` executes count times.

## 5. Tricky Interview Cases

**Case 1 — `synchronized` on `null`**
```java
Object lock = null;
synchronized (lock) { ... }
```
Output: `NullPointerException` at runtime.
Explanation: `synchronized (lock)` evaluates the expression first, throws NPE if null. Always ensure lock object is initialized.

**Case 2 — Synchronized on String literal**
```java
String lock1 = "lock";
String lock2 = "lock";
synchronized (lock1) { ... }
// In another thread:
synchronized (lock2) { ... } // blocks! same pooled object
```
Output: Second thread blocks until first releases.
Explanation: `"lock"` is interned. Both `lock1` and `lock2` point to same pooled object. Using string literals as locks is dangerous — any other code using same literal blocks each other. **Always use `private final Object lock = new Object()` for explicit locks.**

**Case 3 — Synchronized on `this` vs method**
```java
class Counter {
    public synchronized void inc() { for (int i = 0; i < 100; i++) count++; }
    public void add() { synchronized(this) { for (int i = 0; i < 100; i++) count++; } }
}
```
`inc()` and `add()` lock on same object (`this`). They are mutually exclusive.
Explanation: Method-level `synchronized` locks `this`. Block-level `synchronized(this)` locks same monitor. Use private lock object to avoid external interference.

**Case 4 — Static vs instance synchronization**
```java
class Service {
    public synchronized void instance() { ... }
    public static synchronized void static() { ... }
}

Service s1 = new Service();
Service s2 = new Service();
```
`s1.instance()` and `s2.instance()` lock on different monitors (`s1` vs `s2`) → run concurrently.
`Service.static()` locks on `Service.class` → blocks both instance calls.
Explanation: Static `synchronized` locks `Class` object (shared across all instances). Instance `synchronized` locks per-instance `this`.

**Case 5 — Visibility without `synchronized`**
```java
class Visibility {
    private boolean running = true;
    public void stop() { running = false; } // no synchronized
    public void work() { while (running) { /* busy wait */ } }
}
```
Output: May loop forever even after `stop()` called.
Explanation: Without `synchronized` (or `volatile`), JMM does not guarantee `running = false` is visible to other threads. CPU cache may retain old value forever. Fix: `volatile boolean running` or synchronized read/write.

**Case 6 — Lock ordering deadlock**
```java
class Account {
    int id; long balance;
    void transfer(Account to, long amt) {
        synchronized (this) {
            synchronized (to) {
                this.balance -= amt;
                to.balance += amt;
            }
        }
    }
}
```
Output: Possible deadlock if two threads transfer in opposite directions.
Explanation: Thread1 locks A then B. Thread2 locks B then A. Both wait for each other. Fix: lock ordering (always lock smaller ID first) or `tryLock` with timeout.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `synchronized (stringLiteral)` | Other code using same literal blocks each other | Use `private final Object lock = new Object()` |
| Synchronized getters/setters | Unnecessary contention | Only synchronize when needed |
| Calling `wait()` outside synchronized | `IllegalMonitorStateException` | Always `wait()`/`notify()` inside synchronized block |
| Forgetting `while` loop with `wait()` | Spurious wakeups + lost notifications | `while (!condition) lock.wait();` |
| Locking on `this` | External code can interfere with your lock | Use private lock object |
| Mixing `HashMap` with `synchronized` | Fixed-size lock, poor concurrency | Use `ConcurrentHashMap` |
| No lock ordering | Deadlock in nested locks | Establish global lock ordering |
| Synchronized on auto-boxed Integers | Same cached objects block unrelated code | Use explicit `new Object()` |

## 7. Production Usage

**Spring `@Transactional` (synchronization proxy):**
```java
@Service
public class PaymentService {
    // Transaction managed via synchronized proxy on bean instance
    public synchronized void process(Payment p) {
        // Actually Spring uses AOP proxies, not synchronized keyword
        // But concept similar: mutual exclusion on bean instance
    }
}
```
Spring transactions use database-level locks, not JVM `synchronized`. For distributed systems, database locks work across JVMs; `synchronized` does not.

**Singleton lazy initialization:**
```java
public class ConfigLoader {
    private static volatile Config instance;
    public static Config getInstance() {
        if (instance == null) {
            synchronized (ConfigLoader.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }
}
```
Double-checked locking needs `volatile` to prevent seeing partially constructed object.

**Connection pool (HikariCP internal pattern):**
```java
// HikariCP uses Semaphore, not synchronized, for connection borrowing
// But concept similar: acquire permit = acquire lock
Connection borrow(long timeout) throws SQLException {
    semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    // critical section: return connection
}
```
Synchronized is too coarse for high-throughput pools. Semaphore or `ReentrantLock` preferred.

**Web server session replication:**
```java
// Synchronized on user session for in-place updates
public void updateSession(String userId, UserData data) {
    synchronized(userSessions.get(userId)) {
        userSessions.put(userId, data);
    }
}
```
Synchronizing on session object ensures concurrent requests for same user are serialized.

## 8. Advanced Details

- **Biased locking:** Enabled by default in HotSpot (`-XX:+UseBiasedLocking`). First lock acquisition biases toward thread. Subsequent unlocks/relocks by same thread cost ~0. Revoked on contention. Disable with `-XX:-UseBiasedLocking`.
- **Lock inflation:** From biased → thin ( lightweight, spin) → fat (OS mutex, heavyweight). Inflation is one-way until biased lock.
- **Lock coarsening:** JIT may merge adjacent synchronized blocks on same lock into one larger block, reducing lock acquire/release overhead.
- **Escape analysis + lock elision:** JIT can eliminate synchronized entirely if lock object never escapes the thread. Happens with local `synchronized (new Object())`.
- **`synchronized` vs `ReentrantLock`:** `synchronized` is simpler, auto-released on exception, JIT-optimized. `ReentrantLock` offers `tryLock`, interruptible lock, multiple condition variables, fair ordering.
- **`notify()` vs `notifyAll()`:** `notify()` wakes one random waiting thread. `notifyAll()` wakes all. Use `notifyAll()` to avoid lost wakeups when multiple conditions possible.
- **Spurious wakeups:** Thread can wake from `wait()` without `notify()` being called. Always use `while` loop to recheck condition.
- **`Object.wait(0)`:** Wait indefinitely until notified. `wait(timeout)` returns if timeout expires or notified.
- **`Thread.sleep()` vs `wait()`:** `sleep` does not release lock. `wait` releases lock and adds to wait set.

## 9. Interview Questions And Answers

### Beginner
Q: What does `synchronized` keyword do in Java? What does it lock on?
A: `synchronized` provides mutual exclusion — only one thread can execute a synchronized block or method at a time for the same lock object. It locks on the intrinsic lock (monitor) associated with the object. For instance methods, it locks on `this` (or the instance). For static methods, it locks on the `Class` object. For blocks, it locks on the specified object.

### Intermediate
Q: What is the difference between `synchronized` method and `synchronized` block? When would you use each?
A: Synchronized method locks the entire method body on `this` (or `Class` for static). Synchronized block locks only a specific section of code on an explicitly chosen object.

Use synchronized block when:
- You want finer-grained control over lock scope
- Only part of the method needs protection (reduces contention)
- You want to lock on a private object instead of `this`
Use synchronized method for simplicity when the entire method is a critical section.

### Senior
Q: You have a high-traffic web service where `synchronized` on the user session object causes 30% latency increase under load. Why? How would you redesign?
A: `synchronized` on session object serializes ALL requests for the same user. Even read-only requests must wait for any ongoing write. This creates a bottleneck for active users.

Redesign options:
1. **Read-write lock:** `ReentrantReadWriteLock` allows concurrent reads, exclusive writes.
2. **Optimistic locking (version column):** Add `@Version` to entity. Detect concurrent updates at database level instead of JVM level. Works across JVMs and is scalable.
3. **Non-blocking data structures:** `ConcurrentHashMap` for session attributes. No coarse lock.
4. **Sticky sessions / partitioned sessions:** Route same user to same server instance, reduce cross-node contention.
5. **Event sourcing:** Append-only event log for session changes. Readers build state from events.

For web sessions, database-level optimistic locking or `ConcurrentHashMap` (per-node, not distributed) is standard.

### Tricky
Q: In a `synchronized` block, is it safe to call `wait()` inside a loop checking a condition? What happens if the thread doesn't get notified? Explain spurious wakeups.
A: Yes, `wait()` must always be in a loop:

```java
synchronized (lock) {
    while (!condition) {
        lock.wait(); // releases lock, waits
    }
}
```

If the condition is false when notified (spurious wakeup, or another thread changed condition first), the loop rechecks and waits again.

**Spurious wakeup** means a thread can return from `wait()` even though no thread called `notify()` or `notifyAll()`. The JVM does not guarantee this won't happen. Always use `while` loop, never `if`.

Livelock example:
```java
// Thread A waits, Thread B calls notify, but Thread C also waits and grabs condition first
synchronized (lock) {
    while (!ready) lock.wait();
    process(); // ready was set by B, but C also waiting now
}
ready = false; // reset for next round
lock.notify(); // wakes C
```

## 10. Final 30-Second Answer

`synchronized` = mutual exclusion via intrinsic object monitor. Only one thread holds monitor at a time. Provides happens-before visibility: writes inside synchronized are visible to subsequent synchronized reads on same monitor. Method-level locks `this` or `Class`. Block-level locks explicit object. **Always `wait()` in while loop** for condition check. Reentrant. JVM optimizes uncontended locks (biased locking). For timeout/interruptible/fair: use `ReentrantLock`. Avoid string literals or `this` as lock — use `private final Object lock = new Object()`.