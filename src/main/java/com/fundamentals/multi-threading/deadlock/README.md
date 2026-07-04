# Deadlock — Complete Deep Dive

## 1. Why This Concept Matters

Deadlock is the most dangerous concurrency bug: the application freezes completely, threads stop responding, and no exceptions are thrown. Understanding deadlock conditions, detection, and prevention is essential for writing robust concurrent systems. In production, deadlocks cause service outages that are extremely difficult to diagnose because no error is logged — the application simply hangs. Interviewers test deadlock because it reveals your understanding of lock ordering, the four Coffman conditions, and practical prevention strategies.

Misunderstanding deadlock causes:
- Production outages with no error logs
- Unpredictable hangs under high concurrency
- Difficulty debugging because stack traces show threads waiting, not erroring
- False confidence that "it works in testing" (deadlocks are timing-dependent)

## 2. Basic Meaning

A deadlock occurs when two or more threads are each waiting for a resource held by another thread in the group, creating a circular wait where no thread can proceed.

**Four Coffman conditions (ALL must hold for deadlock):**
1. **Mutual exclusion**: At least one resource is held in exclusive mode (not shareable)
2. **Hold and wait**: Thread holds at least one resource while waiting for another
3. **No preemption**: Resources cannot be forcibly taken from threads
4. **Circular wait**: Threads form a cycle, each waiting for next thread's resource

Key vocabulary:
- **Deadlock**: circular wait where no thread can proceed
- **Livelock**: threads keep running but make no progress (e.g., both stepping aside)
- **Starvation**: thread never gets access to resource (always picked last)
- **Lock ordering**: establishing global sequence for acquiring locks
- **Lock timeout**: `tryLock(timeout)` gives up after deadline instead of waiting forever
- **Resource allocation graph**: visual representation of thread-resource relationships
- **Preemption**: forcibly taking a resource from a thread (not supported by `synchronized`)

What it is NOT: Deadlock is not the same as infinite blocking (which can be interrupted). It is not a memory leak. It is not resolved automatically — external intervention (thread interrupt, process kill) is required.

## 3. Real Code / Real Example

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockDemo {
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) throws InterruptedException {
        // === CLASSIC DEADLOCK ===
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("T1: holding lockA");
                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.println("T1: waiting for lockB...");
                synchronized (lockB) {
                    System.out.println("T1: acquired lockB");
                }
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("T2: holding lockB");
                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.println("T2: waiting for lockA...");
                synchronized (lockA) {
                    System.out.println("T2: acquired lockA");
                }
            }
        }, "Thread-2");

        t1.start(); t2.start();
        t1.join(1000); t2.join(1000); // wait up to 1 second

        if (t1.isAlive() || t2.isAlive()) {
            System.out.println("DEADLOCK DETECTED! Threads are stuck.");
            t1.interrupt(); t2.interrupt(); // break deadlock
        } else {
            System.out.println("No deadlock (timing was lucky).");
        }

        // === DEADLOCK WITH ReentrantLock (detectable via ThreadMXBean) ===
        demonstrateLockOrdering();

        // === PREVENTION: LOCK ORDERING ===
        demonstratePrevention();
    }

    // Detectable deadlock using ThreadMXBean
    static class DetectableDeadlock {
        private final Lock lock1 = new ReentrantLock();
        private final Lock lock2 = new ReentrantLock();

        void ThreadA() {
            lock1.lock();
            try {
                Thread.sleep(50);
                lock2.lock();
                try {
                    System.out.println("ThreadA: both locks acquired");
                } finally { lock2.unlock(); }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { lock1.unlock(); }
        }

        void ThreadB() {
            lock2.lock();
            try {
                Thread.sleep(50);
                lock1.lock();
                try {
                    System.out.println("ThreadB: both locks acquired");
                } finally { lock1.unlock(); }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { lock2.unlock(); }
        }
    }

    static void demonstrateLockOrdering() throws InterruptedException {
        DetectableDeadlock dd = new DetectableDeadlock();
        Thread a = new Thread(dd::ThreadA, "A");
        Thread b = new Thread(dd::ThreadB, "B");
        a.start(); b.start();
        Thread.sleep(200);

        // Detect deadlock via ThreadMXBean
        java.lang.management.ThreadMXBean mxBean =
            java.lang.management.ManagementFactory.getThreadMXBean();
        long[] deadlocked = mxBean.findDeadlockedThreads();
        if (deadlocked != null && deadlocked.length > 0) {
            System.out.println("Detected deadlock between threads: " + deadlocked.length);
            a.interrupt(); b.interrupt();
        }
    }

    // Prevention: consistent lock ordering
    static class PreventedDeadlock {
        private final Lock firstLock = new ReentrantLock();
        private final Lock secondLock = new ReentrantLock();

        void safeMethod1() {
            firstLock.lock();
            try {
                Thread.sleep(50);
                secondLock.lock();
                try {
                    System.out.println("Safe: acquired both locks in order");
                } finally { secondLock.unlock(); }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { firstLock.unlock(); }
        }

        void safeMethod2() {
            firstLock.lock();      // same order: first → second
            try {
                Thread.sleep(50);
                secondLock.lock();
                try {
                    System.out.println("Safe: acquired both locks in order");
                } finally { secondLock.unlock(); }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { firstLock.unlock(); }
        }
    }

    static void demonstratePrevention() throws InterruptedException {
        PreventedDeadlock pd = new PreventedDeadlock();
        Thread a = new Thread(pd::safeMethod1, "Safe-A");
        Thread b = new Thread(pd::safeMethod2, "Safe-B");
        a.start(); b.start();
        a.join(1000); b.join(1000);
        if (!a.isAlive() && !b.isAlive()) {
            System.out.println("No deadlock — lock ordering prevented it.");
        }
    }
}
```

Expected output:
```
T1: holding lockA
T2: holding lockB
T1: waiting for lockB...
T2: waiting for lockA...
DEADLOCK DETECTED! Threads are stuck.
Detected deadlock between threads: 2
No deadlock — lock ordering prevented it.
```

Note: exact interleaving may vary. Key indicator: threads stuck waiting indefinitely.

## 4. What Happens Internally

**Classic deadlock scenario:**
```
Thread A                    Thread B
─────────────────────────────────────────
synchronized(lockA) → acquired
                              synchronized(lockB) → acquired
waiting for lockB...         waiting for lockA...
[suspended indefinitely]     [suspended indefinitely]
```

**Resource allocation graph:**
- Nodes: threads (circles) and locks (squares)
- Edges: request (dotted) and assignment (solid)
- Deadlock = cycle in graph

**Thread states during deadlock:**
- Thread enters `BLOCKED` state trying `monitorenter`
- `Thread.getState()` returns `BLOCKED` or `WAITING`
- Thread consumes minimal CPU but holds no CPU time (scheduled out)
- Stack trace shows thread stuck at `synchronized` acquisition

**`ThreadMXBean.findDeadlockedThreads()`:**
```java
ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
long[] deadlocked = mxBean.findDeadlockedThreads(); // or findMonitorDeadlockedThreads()
if (deadlocked != null) {
    for (long tid : deadlocked) {
        ThreadInfo info = mxBean.getThreadInfo(tid);
        System.out.println("Deadlocked: " + info.getThreadName());
        System.out.println("  Locked on: " + info.getLockName());
        System.out.println("  Waiting for: " + info.getLockOwnerName());
    }
}
```

**JVM deadlock detection:**
- JVM periodically checks for cycles in monitor lock graph
- `jstack <pid>` prints detected deadlocks
- `jcmd <pid> Thread.print` or `Thread.dump()`
- Detection is conservative: may miss some deadlocks, rarely false positives

## 5. Tricky Interview Cases

**Case 1 — Nested synchronized blocks**
```java
class Bank {
    private final Object lockA = new Object();
    private final Object lockB = new Object();

    void transferAtoB() {
        synchronized (lockA) {
            synchronized (lockB) { /* transfer */ }
        }
    }

    void transferBtoA() {
        synchronized (lockB) {
            synchronized (lockA) { /* transfer */ }
        }
    }
}
```
Output: Deadlock if two threads call opposite methods simultaneously.
Thread1 holds A, waits B. Thread2 holds B, waits A. Both blocked forever.

Fix: Always acquire locks in same order: `transferBtoA` should acquire A then B, not B then A.

**Case 2 — `ReentrantLock` with `tryLock` breaks deadlock**
```java
void transferSafe(Account from, Account to, int amt) {
    long start = System.currentTimeMillis();
    while (!from.lock.tryLock(1, TimeUnit.SECONDS)) {
        if (System.currentTimeMillis() - start > 10) throw new TimeoutException();
    }
    try {
        if (!to.lock.tryLock(1, TimeUnit.SECONDS)) {
            throw new TimeoutException(); // release from lock, retry later
        }
        try { from.debit(amt); to.credit(amt); }
        finally { to.lock.unlock(); }
    } finally { from.lock.unlock(); }
}
```
Output: No deadlock — one thread gives up instead of waiting forever.

**Case 3 — Deadlock via `join()`**
```java
Thread t1 = new Thread(() -> {
    try { t2.join(); } catch (InterruptedException e) {}
    System.out.println("T1 done");
});
Thread t2 = new Thread(() -> {
    try { t1.join(); } catch (InterruptedException e) {}
    System.out.println("T2 done");
});
t1.start(); t2.start();
```
Output: Both threads wait for each other forever. Circular wait via `join()`.

**Case 4 — Livelock (not deadlock)**
```java
class PoliteThread implements Runnable {
    private final Object lock;
    private final Object otherLock;
    void run() {
        while (true) {
            if (lock.tryLock()) {
                try {
                    if (otherLock.tryLock()) {
                        // critical section
                        return;
                    } else {
                        lock.unlock(); // release and retry
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }
}
```
Output: Threads keep running, never blocking, but never making progress. Both keep releasing and retrying.

**Case 5 — Starvation (not deadlock)**
```java
class Starvation {
    synchronized void slow() { Thread.sleep(10000); }
    synchronized void fast() { /* quick */ }
}
// Thread 1 calls slow() repeatedly.
// Thread 2 keeps calling fast().
// Thread 2 may never get lock if Thread 1 keeps reacquiring.
```
Output: Thread 2 waits indefinitely. Not deadlock — only one thread blocked, other is running.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Acquiring locks in different orders | Circular wait → deadlock | Establish global lock ordering (by ID, hash, etc.) |
| Nested synchronized on different objects | Circular wait possible | Lock ordering or `tryLock` with timeout |
| Calling `join()` on each other | Circular wait on thread completion | Use `CountDownLatch` or `Future.get(timeout)` |
| No timeout on lock acquisition | Waits forever if deadlock | `tryLock(timeout)` with fallback |
| Not detecting deadlock in production | Application hangs with no alert | JMX monitoring, `ThreadMXBean`, health checks |
| Assuming testing catches deadlock | Deadlocks are timing-dependent | Stress testing, thread sanitizers, formal analysis |
| Holding locks during I/O | Long hold times increase deadlock window | Release locks before I/O, minimize critical section |

## 7. Production Usage

**Detection in production:**
```java
// Scheduled deadlock detector
@Scheduled(fixedRate = 5000)
public void checkDeadlocks() {
    ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
    long[] deadlocked = mxBean.findDeadlockedThreads();
    if (deadlocked != null && deadlocked.length > 0) {
        alert("Deadlock detected! Threads: " + deadlocked.length);
        // Optional: dump thread info
        for (long tid : deadlocked) {
            ThreadInfo info = mxBean.getThreadInfo(tid);
            log.error("Deadlocked: {} on {}", info.getThreadName(), info.getLockName());
        }
    }
}
```

**Database transaction deadlock:**
```sql
-- Database detects deadlock at row/table lock level
-- PostgreSQL: ERROR 40P01: deadlock detected
-- MySQL:ERROR 1213: Deadlock found when trying to get lock
```
Databases detect and resolve by aborting one transaction. Application must retry.

**Prevention via lock ordering:**
```java
void transfer(Account from, Account to, BigDecimal amt) {
    Account first = from.getId() < to.getId() ? from : to;
    Account second = from.getId() < to.getId() ? to : from;
    synchronized (first) {
        synchronized (second) {
            from.debit(amt);
            to.credit(amt);
        }
    }
}
```
Always lock lower-ID account first. Eliminates circular wait.

**`tryLock` with retry:**
```java
boolean transferWithTimeout(Account from, Account to, BigDecimal amt, long timeoutMs) {
    long deadline = System.currentTimeMillis() + timeoutMs;
    while (System.currentTimeMillis() < deadline) {
        if (from.lock.tryLock(100, TimeUnit.MILLISECONDS)) {
            try {
                if (to.lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                    try {
                        from.debit(amt); to.credit(amt);
                        return true;
                    } finally { to.lock.unlock(); }
                }
            } finally { from.lock.unlock(); }
        }
    }
    throw new TimeoutException("Could not acquire locks");
}
```

## 8. Advanced Details

- **`ReentrantLock.tryLock()` vs `synchronized`:** `tryLock` supports timeout and interruptible acquisition. `synchronized` blocks indefinitely. `tryLock` enables deadlock avoidance.
- **`java.util.concurrent` classes:** `ConcurrentHashMap`, `BlockingQueue`, `AtomicInteger` avoid explicit locks entirely. Use them first.
- **Lock ordering with `System.identityHashCode()`:** If objects don't have natural ordering, use `System.identityHashCode(lock)` as tiebreaker.
- **Phaser/CyclicBarrier:** For thread coordination without explicit locks. Parties arrive at barrier, all proceed together. No deadlock if barrier correctly sized.
- **Deadlock in databases:** Row-level locks (SELECT...FOR UPDATE), gap locks (InnoDB), table locks. Deadlock detection by DB engine. Retry at application layer.
- **JVM thread dump analysis:** `jstack <pid>` shows "Found one Java-level deadlock" with thread stack traces. Learn to read thread dumps.
- **ABA problem:** Not deadlock but related concurrency bug. `AtomicReference.compareAndSet` fails if value changed A→B→A. Fix: `AtomicStampedReference`.

## 9. Interview Questions And Answers

### Beginner
Q: What is a deadlock? What are the four necessary conditions for deadlock (Coffman conditions)?
A: Deadlock is a situation where two or more threads are blocked forever, each waiting for a resource held by another thread in the group. The four necessary conditions (ALL must be true for deadlock):
1. **Mutual exclusion**: Resources are not sharable
2. **Hold and wait**: Thread holds one resource while waiting for another
3. **No preemption**: Resources cannot be forcibly taken away
4. **Circular wait**: Threads form a circular chain, each waiting for the next

To prevent deadlock, break at least one condition. In Java, we typically break circular wait via lock ordering.

### Intermediate
Q: You have two methods that both acquire `lockA` and `lockB` in opposite orders. How do you fix the potential deadlock?
A: Establish a global lock ordering. Always acquire locks in the same order regardless of which method you're in.

```java
// Method 1
void transfer(Account a, Account b) {
    if (a.getId() < b.getId()) {
        synchronized (a) { synchronized (b) { ... } }
    } else {
        synchronized (b) { synchronized (a) { ... } }
    }
}
```
Order by account ID (or `System.identityHashCode()` if no natural ID). This eliminates circular wait.

### Senior
Q: In a payment processing system, you have three microservices: `OrderService`, `PaymentService`, `InventoryService`. Each has its own database. A distributed transaction spans all three: create order → charge payment → reserve inventory. Describe the deadlock risks at database level and how to handle them.
A: Database-level deadlocks occur when two transactions lock rows in different orders:

Transaction A: locks order (id=1), then tries to lock payment (id=2)
Transaction B: locks payment (id=2), then tries to lock order (id=1)

Both hold one lock, wait for the other → database detects deadlock, aborts one transaction (PostgreSQL, MySQL detect this).

Handling:
1. **Application retry:** Catch `DeadlockLoserDataAccessException` (Spring) or SQL state 40P01, retry with backoff.
2. **Consistent ordering:** Always lock resources in same order across services (e.g., by entity ID ascending).
3. **Short transactions:** Minimize time locks are held. Do NOT hold locks during external API calls.
4. **Optimistic locking:** Use `@Version` to avoid locks entirely. If concurrent update detected, retry at application level.
5. **Saga pattern:** Break distributed transaction into local transactions with compensation. Eliminates long-held locks across services.

### Tricky
Q: `Thread A` calls `synchronized(lock1)` then `synchronized(lock2)`. `Thread B` calls `synchronized(lock2)` then `synchronized(lock1)`. Is this guaranteed to deadlock? If not, why? What would make it guaranteed?
A: Not guaranteed — it is *possible* but *not certain*. Deadlock requires a specific timing:

Scenario 1 (no deadlock): Thread A acquires both locks, releases, then Thread B acquires both. No cycle.

Scenario 2 (deadlock): Thread A acquires lock1, gets preempted. Thread B acquires lock2, tries lock1 → blocked. Thread A resumes, tries lock2 → blocked. Cycle formed.

To make deadlock more likely (for testing): add `Thread.sleep()` between lock acquisitions to increase window for interleaving.

To guarantee no deadlock: enforce lock ordering (always acquire lock1 before lock2 in both threads), OR use `tryLock` with timeout so one thread backs off instead of blocking.

## 10. Final 30-Second Answer

Deadlock = circular wait: Thread A holds X waits Y, Thread B holds Y waits X. Four Coffman conditions must all hold. Break any one to prevent: easiest is **lock ordering** (always acquire locks in globally consistent order). In production: use `ThreadMXBean.findDeadlockedThreads()` for detection, `tryLock` with timeout for prevention, `java.util.concurrent` to avoid explicit locks. Databases detect and abort deadlocks — retry with backoff. Deadlocks are timing-dependent: test with stress/load, not just unit tests.