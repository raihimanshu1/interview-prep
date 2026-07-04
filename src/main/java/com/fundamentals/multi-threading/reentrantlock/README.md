# ReentrantLock — Complete Deep Dive

## 1. Why This Concept Matters

ReentrantLock is the flagship Lock implementation in java.util.concurrent.locks. It provides explicit lock/unlock control, condition variables, fairness policy, and polling/timed lock acquisition that `synchronized` cannot match. Understanding ReentrantLock reveals mastery of concurrent programming beyond basic `synchronized`. Interviewers test this to assess deep knowledge of lock mechanics, tryLock patterns, and condition management.

Misunderstanding ReentrantLock causes:
- Deadlocks from missing `unlock()` in `finally` block
- Incorrect condition signaling (signal vs signalAll)
- Performance degradation from fairness=true on hot locks
- Throwing InterruptedException without restoring interrupt flag

## 2. Basic Meaning

ReentrantLock is a mutual exclusion lock with the same basic behavior as `synchronized` but extended capabilities. "Reentrant" means the owning thread can re-acquire the lock without blocking (count-based). Each `lock()` increments hold count, each `unlock()` decrements.

**Key vocabulary:**
- **`lock()`**: acquire, block until available
- **`unlock()`**: release (MUST be in finally block)
- **`tryLock()`**: non-blocking acquire, returns boolean
- **`tryLock(time, unit)`**: timed acquire, returns boolean
- **`lockInterruptibly()`**: acquire, respond to interrupt
- **`isHeldByCurrentThread()`**: query ownership
- **`getHoldCount()`**: current thread's hold count
- **`isFair`**: true → FIFO ordering for waiting threads
- **`Condition`**: object to wait/signal on lock
- **`newCondition()`**: create condition on this lock

What it is NOT: Not a replacement for every `synchronized` block. More complex, requires explicit management. Not suitable for simple single-method synchronization.

## 3. Real Code / Real Example

```java
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class ReentrantLockDemo {
    private final ReentrantLock lock = new ReentrantLock();
    private int counter = 0;

    public void increment() {
        lock.lock();
        try {
            counter++;
        } finally {
            lock.unlock(); // ALWAYS in finally
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // === BASIC LOCK/UNLOCK ===
        ReentrantLock basic = new ReentrantLock();
        basic.lock();
        try {
            System.out.println("Protected section");
        } finally {
            basic.unlock();
        }

        // === TRYLOCK (non-blocking) ===
        ReentrantLock tryLock = new ReentrantLock();
        if (tryLock.tryLock()) {
            try {
                System.out.println("Lock acquired immediately");
            } finally {
                tryLock.unlock();
            }
        } else {
            System.out.println("Could not acquire lock");
        }

        // === TRYLOCK WITH TIMEOUT ===
        ReentrantLock timeout = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            timeout.lock();
            try {
                System.out.println("t1 holds lock, waiting...");
                Thread.sleep(2000);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { timeout.unlock(); }
        });
        Thread t2 = new Thread(() -> {
            try {
                boolean acquired = timeout.tryLock(500, TimeUnit.MILLISECONDS);
                System.out.println("t2 acquired: " + acquired);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        t1.start(); Thread.sleep(100); t2.start();
        t1.join(); t2.join();

        // === REENTRANT COUNT ===
        ReentrantLock reentrant = new ReentrantLock();
        reentrant.lock();
        reentrant.lock(); // re-enter
        System.out.println("Hold count: " + reentrant.getHoldCount()); // 2
        if (reentrant.isHeldByCurrentThread()) {
            System.out.println("Held by current thread");
        }
        reentrant.unlock();
        System.out.println("Hold count after one unlock: " + reentrant.getHoldCount()); // 1
        reentrant.unlock(); // now held 0 times

        // === CONDITION (producer-consumer) ===
        ReentrantLock conditionLock = new ReentrantLock();
        Condition notFull = conditionLock.newCondition();
        Condition notEmpty = conditionLock.newCondition();
        java.util.LinkedList<Integer> buffer = new java.util.LinkedList<>();
        final int CAPACITY = 5;

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                conditionLock.lock();
                try {
                    while (buffer.size() == CAPACITY) notFull.await(); // wait if full
                    buffer.add(i);
                    System.out.println("Produced: " + i);
                    notEmpty.signal(); // wake consumer
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                finally { conditionLock.unlock(); }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                conditionLock.lock();
                try {
                    while (buffer.isEmpty()) notEmpty.await(); // wait if empty
                    int val = buffer.removeFirst();
                    System.out.println("Consumed: " + val);
                    notFull.signal(); // wake producer
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                finally { conditionLock.unlock(); }
            }
        });

        producer.start(); consumer.start();
        producer.join(); consumer.join();
    }
}
```

Expected output (simplified):
```
Protected section
Lock acquired immediately
t1 holds lock, waiting...
t2 acquired: false
Hold count: 2
Held by current thread
Hold count after one unlock: 1
Produced: 0
Consumed: 0
Produced: 1
Consumed: 1
...
```

## 4. What Happens Internally

ReentrantLock uses an `AbstractQueuedSynchronizer` (AQS) as its synchronizer:

```java
public class ReentrantLock implements Lock, Serializable {
    private final Sync sync;
    abstract static class Sync extends AbstractQueuedSynchronizer { ... }
    static final class NonfairSync extends Sync { ... }
    static final class FairSync extends Sync { ... }
}
```

**AQS internal mechanics:**
- `state` (int): 0 = unlocked, >0 = locked with hold count
- `exclusiveOwnerThread`: which thread owns the lock
- CLH queue: FIFO queue of waiting threads (Node objects)
- `acquire()`: CAS state 0→1 (non-fair) or queued (fair)
- `release()`: decrement state, wake next waiter

**Fair vs Non-fair:**
- **Non-fair (default):** Thread calling `lock()` attempts CAS directly. If succeeds, bypasses queue. Higher throughput, potential starvation.
- **Fair:** Thread goes to end of queue. First come, first served. Lower throughput, no starvation.

**`lock()` non-fair:**
```java
final void lock() {
    if (compareAndSetState(0, 1)) setExclusiveOwnerThread(Thread.currentThread());
    else acquire(1); // enqueue + park
}
```

**Condition implementation:**
```java
public class ConditionObject implements Condition {
    private Node firstWaiter;
    private Node lastWaiter;
    
    public final void await() { ... } // release lock, park, on signal reacquire
    public final void signal() { ... } // transfer one waiter to sync queue
    public final void signalAll() { ... } // transfer all waiters
}
```

`await()`: release lock → park → on signal → acquire lock again
`signal()`: move one waiting Node from condition queue to sync queue

## 5. Tricky Interview Cases

**Case 1 — Missing unlock = deadlock**
```java
ReentrantLock lock = new ReentrantLock();
lock.lock();
if (someCondition) return; // lock NEVER released!
lock.unlock();
```
Problem: If `someCondition` is true, lock never unlocked. Next thread trying to `lock()` hangs forever. **Fix**: Always use try-finally.

**Case 2 — `tryLock()` in loop (backoff)**
```java
ReentrantLock lock = new ReentrantLock();
while (true) {
    if (lock.tryLock()) {
        try { /* do work */ } finally { lock.unlock(); break; }
    }
    Thread.sleep(50); // backoff
}
```
Output: Works but inefficient if contention high. Prefer `tryLock(timeout)`.

**Case 3 — `lockInterruptibly()` vs `lock()`**
```java
ReentrantLock lock = new ReentrantLock();
Thread t = new Thread(() -> {
    try { lock.lockInterruptibly(); } catch (InterruptedException e) {
        System.out.println("Interrupted while waiting"); return;
    } finally { if (lock.isHeldByCurrentThread()) lock.unlock(); }
});
t.start();
t.interrupt(); // will wake t from blocking
```
Output: `Interrupted while waiting`. `lock()` does NOT respond to interrupt. `lockInterruptibly()` does.

**Case 4 — Reentrant count must match**
```java
ReentrantLock lock = new ReentrantLock();
lock.lock();
lock.lock(); // hold count = 2
lock.unlock(); // hold count = 1
// NOT yet released — another thread still blocked
```
Output: Thread still holds lock after one `unlock()`. Must call unlock exactly as many times as lock.

**Case 5 — `Condition.signal()` loses wakeup**
```java
ReentrantLock lock = new ReentrantLock();
Condition cond = lock.newCondition();
// Thread A
lock.lock();
cond.await(); // release lock, park
// Thread B
lock.lock();
cond.signal(); // wake ONE waiter
lock.unlock();
// Thread A reacquires lock
```
If no thread is waiting, `signal()` does nothing. Use `signalAll()` when multiple conditions.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Missing `unlock()` in `finally` | Deadlock | Always `lock(); try {...} finally { unlock(); }` |
| `Condition.signal()` when should be `signalAll()` | Lost wakeups (one waiter missed) | Use `signalAll()` unless only one waiter |
| `await()` outside condition loop | Spurious wakeup | Always `while (condition) cond.await()` |
| Fair lock on hot path | Reduced throughput | Use non-fair (default) unless fairness critical |
| `tryLock()` without timeout | Busy-wait loop | Use `tryLock(timeout)` instead |
| Locking in wrong order | Potential deadlock | Establish lock ordering discipline |

## 7. Production Usage

**Thread-safe counter with lock instead of synchronized:**
```java
public class Counter {
    private final ReentrantLock lock = new ReentrantLock();
    private int count;
    public void increment() { lock(); try { count++; } finally { unlock(); } }
}
```

**Condition-based bounded buffer (ArrayBlockingQueue uses this):**
```java
// ArrayBlockingQueue uses ReentrantLock + Conditions internally
BlockingQueue<Request> queue = new ArrayBlockingQueue<>(100);
queue.put(new Request()); // uses lock, notFull.await, notEmpty.signal
Request r = queue.take(); // uses lock, notEmpty.await, notFull.signal
```

**TryLock with timeout for fail-fast operations:**
```java
public boolean updateResource(Resource r, long timeout, TimeUnit unit) throws InterruptedException {
    if (r.lock.tryLock(timeout, unit)) {
        try {
            return r.update();
        } finally { r.lock.unlock(); }
    }
    return false; // timed out, return gracefully
}
```

## 8. Advanced Details

- **`getQueueLength()`**: Number of threads waiting for this lock (approximate)
- **`hasQueuedThreads()`**: Check if any threads are waiting
- **`hasQueuedThread(Thread)`**: Check specific thread is waiting
- **`hasWaiters(Condition)`**: Check if threads are waiting on condition
- **`getWaitQueueLength(Condition)`**: Number of threads waiting on condition
- **`getOwner()`**: Current owner (null if none)
- **`isLocked()`**: Lock is held by any thread
- **`tryLock()` unfair by design**: Even fair ReentrantLock allows non-fair tryLock to avoid deadlock

## 9. Interview Questions And Answers

### Beginner
Q: What is ReentrantLock? How is it different from synchronized?
A: ReentrantLock is an explicit lock class. Key differences:
- `lock()`/`unlock()` manual (synchronized automatic)
- `tryLock()` non-blocking acquire (synchronized cannot)
- `lockInterruptibly()` respond to interrupts (synchronized blocks till release)
- Condition variables (wait/notify equivalent)
- Fairness policy (synchronized always unfair)
- Performance: similar for low contention, ReentrantLock better for high contention

### Intermediate
Q: Explain ReentrantLock fairness. When would you use fair=true vs fair=false?
A: fair=false (default): thread can "barge" ahead of waiting queue. Higher throughput (no queuing overhead), but potential starvation. fair=true: FIFO order. Lower throughput (enqueue required), no starvation.

Use fair=false for high-throughput, non-time-sensitive tasks. Use fair=true for resource allocation fairness, rate limiting, predictable scheduling.

### Senior
Q: You have a distributed lock service with a local ReentrantLock fallback. Describe a scenario where `tryLock()` with timeout is better than `lock()`.
A: Distributed lock acquisition may fail (network timeout, lock held by another node). Combined with local `tryLock(timeout)` permits fail-fast behavior:
1. Attempt distributed lock with timeout
2. If distributed fails → attempt local `tryLock` as fallback
3. If both fail → return error to caller (instead of blocking indefinitely)

Also useful in UI applications — prevent lock wait from freezing UI.

### Tricky
Q: Can `ReentrantLock.lock()` throw `InterruptedException`? What about `lockInterruptibly()`? What is the behavior difference?
A: `lock()` does NOT throw `InterruptedException`. Thread waiting for `lock()` continues waiting even if interrupted. `lockInterruptibly()` DOES throw `InterruptedException` — if thread is interrupted while waiting, it throws and exits.

## 10. Final 30-Second Answer

ReentrantLock = explicit mutual exclusion lock. **Lock/unlock manually**, must `unlock()` in `finally`. `tryLock()` non-blocking, `tryLock(timeout)` timed, `lockInterruptibly()` interrupt-aware. **Reentrant**: same thread re-acquires without blocking (count-based). **Fairness**: non-fair (default, higher throughput) vs fair (FIFO, no starvation). **Conditions**: `await()`/`signal()` instead of `wait()`/`notify()`. Uses **AQS** internally (CAS on `state`, CLH queue). Always `while(condition) cond.await()` to handle spurious wakeups. Prefer `synchronized` for simple cases, ReentrantLock for advanced lock management.