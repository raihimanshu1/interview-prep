# `wait()` and `notify()` — Complete Deep Dive

## 1. Why This Concept Matters

`wait()` and `notify()` are Java's built-in thread coordination mechanism. They enable producer-consumer patterns, task scheduling, and resource pooling without busy-waiting. Understanding these methods — including the critical `while` loop pattern, spurious wakeups, and the difference between `notify()` and `notifyAll()` — is essential for writing correct concurrent code. In production, incorrect use causes missed signals, deadlocks, and lost notifications. Interviewers test this because it reveals whether you truly understand the monitor pattern, the Java Memory Model, and how threads transition between blocked and runnable states.

Misunderstanding wait/notify causes:
- Lost notifications (thread waits forever because signal was sent before wait)
- Spurious wakeup bugs (thread proceeds without condition being true)
- Deadlocks from incorrect lock ordering
- Missed signals when using `notify()` instead of `notifyAll()`

## 2. Basic Meaning

`wait()` and `notify()` are methods on `java.lang.Object` that enable inter-thread communication via monitors. They must be called from within a `synchronized` block on the same object.

Key vocabulary:
- **Wait set**: queue of threads waiting on a monitor (via `wait()`)
- **Monitor**: object lock + wait set + entry set (blocked threads)
- **`wait()`**: releases lock, adds current thread to wait set, blocks until notified or timeout
- **`notify()`**: wakes ONE random thread from wait set
- **`notifyAll()`**: wakes ALL threads from wait set
- **Spurious wakeup**: thread returns from `wait()` without being notified
- **Reentrant lock**: thread reacquires same lock after `wait()` returns

What it is NOT: `wait()` is not `sleep()`. `notify()` is not an event — it signals that condition *might* be true, caller must still check. `wait()` does not timeout by default (`wait(0)` = infinite).

## 3. Real Code / Real Example

```java
public class WaitNotifyDemo {
    private static final Object lock = new Object();
    private static boolean ready = false;
    private static String message;

    public static void main(String[] args) throws InterruptedException {
        // === PRODUCER-CONSUMER ===
        Thread producer = new Thread(() -> {
            synchronized (lock) {
                message = "Hello from producer";
                ready = true;
                System.out.println("Producer: sent message");
                lock.notifyAll(); // wake up waiting consumer(s)
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(50); // ensure producer starts first
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            synchronized (lock) {
                while (!ready) { // ALWAYS use while, not if
                    System.out.println("Consumer: waiting...");
                    lock.wait(); // releases lock, waits
                    System.out.println("Consumer: woke up, ready=" + ready);
                }
                System.out.println("Consumer received: " + message);
            }
        });

        consumer.start();
        producer.start();
        consumer.join();
        producer.join();

        // === MISSED SIGNAL ===
        MissedSignal missed = new MissedSignal();
        missed.demonstrate();

        // === NOTIFY vs NOTIFYALL ===
        NotifyDemo notifyDemo = new NotifyDemo();
        notifyDemo.demonstrate();
    }

    // Missed signal: notify before wait
    static class MissedSignal {
        private final Object lock2 = new Object();
        private boolean signaled = false;

        void demonstrate() throws InterruptedException {
            synchronized (lock2) {
                signaled = true;
                lock2.notify(); // notify BEFORE any thread is waiting!
                System.out.println("Signal sent (but nobody waiting)");
            }

            Thread waiter = new Thread(() -> {
                synchronized (lock2) {
                    while (!signaled) { // always use while
                        try { lock2.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    }
                    System.out.println("Waiter: received signal (or timed out)");
                }
            });
            waiter.start();
            waiter.join();
        }
    }

    // notify() vs notifyAll() with multiple waiters
    static class NotifyDemo {
        private final Object lock3 = new Object();
        private int turn = 0; // 0 = even, 1 = odd

        void demonstrate() throws InterruptedException {
            Thread even = new Thread(() -> printNumbers("Even", 0));
            Thread odd = new Thread(() -> printNumbers("Odd", 1));
            even.start();
            odd.start();
            even.join();
            odd.join();
        }

        void printNumbers(String name, int myTurn) {
            for (int i = myTurn; i < 10; i += 2) {
                synchronized (lock3) {
                    while (turn != myTurn) {
                        try { lock3.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    }
                    System.out.println(name + ": " + i);
                    turn = 1 - myTurn; // switch turn
                    lock3.notifyAll(); // must wake BOTH threads, only one will proceed
                }
            }
        }
    }
}
```

Expected output:
```
Consumer: waiting...
Producer: sent message
Consumer: woke up, ready=true
Consumer received: Hello from producer
Signal sent (but nobody waiting)
Waiter: received signal (or timed out)
Even: 0
Odd: 1
Even: 2
Odd: 3
Even: 4
Odd: 5
Even: 6
Odd: 7
Even: 8
Odd: 9
```

## 4. What Happens Internally

**Monitor structure in JVM:**
Every object has an associated monitor with:
- **Owner**: thread ID holding lock (-1 if unowned)
- **Entry count**: reentrancy depth (0 = unlocked)
- **Wait set**: threads blocked via `wait()`
- **Blocked threads**: threads trying to acquire lock via `monitorenter`

**`wait()` flow:**
```java
synchronized (lock) {
    lock.wait(0); // wait indefinitely
}
```
1. Current thread holds lock on `lock`
2. `wait()` called → thread added to `lock`'s wait set
3. Lock released (owner cleared, entry count = 0)
4. Thread blocks (state: WAITING)
5. Another thread calls `notify()` or `notifyAll()` on same `lock`
6. Woken thread moved from wait set to entry set (blocked, waiting for lock)
7. When lock available → thread reacquires lock
8. `wait()` returns, thread continues

**`notify()` flow:**
1. `notify()` called on `lock` from thread holding `lock`
2. JVM moves ONE thread from wait set to entry set
3. If multiple threads waiting, one chosen arbitrarily (JVM-dependent)
4. Woken thread competes for lock with other blocked threads
5. `notify()` does NOT release lock immediately — caller must exit synchronized block

**Spurious wakeup:**
A thread can return from `wait()` without `notify()`/`notifyAll()` being called. The JVM does not guarantee this won't happen. Reasons:
- OS signal delivery (interrupt)
- JVM implementation detail
- Hardware memory controller interference

**Happens-before for wait/notify:**
- `unlock` (exiting synchronized) → `lock` (next entering synchronized) is happens-before
- All writes before `notify()`/`notifyAll()` are visible to thread after it reacquires lock and `wait()` returns

## 5. Tricky Interview Cases

**Case 1 — `notify()` waking wrong thread**
```java
class Printer {
    private final Object lock = new Object();
    private int turn = 0; // 0 = first, 1 = second

    void printFirst() throws InterruptedException {
        synchronized (lock) {
            while (turn != 0) lock.wait(); // wait until turn 0
            System.out.println("First");
            turn = 1;
            lock.notify(); // notify one — might wake another waiting thread
        }
    }

    void printSecond() throws InterruptedException {
        synchronized (lock) {
            while (turn != 1) lock.wait(); // wait until turn 1
            System.out.println("Second");
            turn = 0;
            lock.notify();
        }
    }

    void start() {
        new Thread(this::printFirst).start();
        new Thread(this::printSecond).start();
    }
}
```
Output:
```
First
Second
First
Second
...
```
Explanation: `notify()` wakes one thread. If `printSecond` is waiting and `printFirst` calls `notify()`, `printSecond` wakes. But if `printFirst` also waiting and `printSecond` calls `notify()`, `printFirst` wakes. Works here because only 2 threads and condition checks in while loop. But fragile — if more threads, `notify()` may wake wrong one.

**Case 2 — Lost notification (notify before wait)**
```java
class LostNotify {
    private final Object lock = new Object();
    private boolean ready = false;

    void send() {
        synchronized (lock) {
            ready = true;
            lock.notify(); // notify BEFORE consumer waits
        }
    }

    void receive() throws InterruptedException {
        synchronized (lock) {
            while (!ready) {
                lock.wait(); // waits forever — signal was already sent!
            }
            System.out.println("Received");
        }
    }
}
```
Output: "Received" eventually if `receive()` is called first. But if `send()` called first, `receive()` waits forever.
Fix: Use `CountDownLatch` (one-shot) or `java.util.concurrent` classes. Or ensure ordering.

**Case 3 — `notifyAll()` still needs `while` loop**
```java
class NotifyAllButIf {
    private final Object lock = new Object();
    private int count = 0;
    private final int MAX = 5;

    void produce() throws InterruptedException {
        synchronized (lock) {
            while (count >= MAX) lock.wait(); // must use while
            count++;
            lock.notifyAll();
        }
    }

    void consume() throws InterruptedException {
        synchronized (lock) {
            if (count == 0) { // BAD: if, not while
                lock.wait();
            }
            count--;
            lock.notifyAll();
        }
    }
}
```
Output: With spurious wakeup or multiple consumers, `if` allows thread to proceed when `count == 0` (after waking, count might be 0 again because another consumer grabbed first).
Fix: `while (count == 0) lock.wait();`

**Case 4 — Holding lock during `notify`**
```java
class NotifyWithoutLock {
    private final Object lock = new Object();

    void notifyEarly() {
        // lock.notify(); // IllegalMonitorStateException!
        lock.notifyAll(); // also throws — must hold lock
    }
}
```
Output: `IllegalMonitorStateException`
Explanation: `notify()`/`notifyAll()` require caller to hold lock on same object. `wait()` also requires holding lock.

**Case 5 — `wait()` with timeout and spurious wakeup**
```java
synchronized (lock) {
    long timeout = System.currentTimeMillis() + 1000;
    while (!condition && System.currentTimeMillis() < timeout) {
        lock.wait(100); // wait up to 100ms at a time
    }
    if (!condition) {
        // timed out
    }
}
```
Output: Returns from `wait(100)` after 100ms if not notified.
Explanation: Always use loop with timeout. `wait(timeout)` can return early due to spurious wakeup. Recheck condition and deadline.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `if` instead of `while` around `wait()` | Proceeds without condition true (spurious wakeup) | Always `while (!condition) lock.wait();` |
| `notify()` with multiple waiters | Wakes wrong thread, others starve | Use `notifyAll()` unless you know exactly one waiter |
| Calling `notify()` without holding lock | `IllegalMonitorStateException` | Always inside synchronized on same object |
| Not rechecking condition after `wait()` | Proceeds with stale/wrong state | `while` loop rechecks condition |
| `wait()` on wrong object | Waits on different monitor than producer notifies | Both must use same lock object |
| Using `wait()` for timeouts (busy loop) | Wastes CPU | Use `wait(timeout)` or `LockSupport.parkNanos` |
| Not handling `InterruptedException` | Silent swallow or incorrect restoration | Restore interrupt: `Thread.currentThread().interrupt()` |

## 7. Production Usage

**Producer-consumer with blocking queue (preferred):**
```java
// DON'T implement manually unless required
BlockingQueue<String> queue = new LinkedBlockingQueue<>(100);
// Producer
queue.put(message); // blocks if full
// Consumer
String msg = queue.take(); // blocks if empty
```
`BlockingQueue` handles all wait/notify internally. Preferred for production.

**Legacy producer-consumer (manual wait/notify):**
```java
class SimpleQueue {
    private final LinkedList<String> items = new LinkedList<>();
    private final Object lock = new Object();
    private final int MAX = 100;

    void put(String item) throws InterruptedException {
        synchronized (lock) {
            while (items.size() >= MAX) lock.wait();
            items.add(item);
            lock.notifyAll();
        }
    }

    String take() throws InterruptedException {
        synchronized (lock) {
            while (items.isEmpty()) lock.wait();
            String item = items.removeFirst();
            lock.notifyAll();
            return item;
        }
    }
}
```

**Thread pool task queue:**
```java
// Thread pool internally uses wait/notify-like mechanism
// via Condition (AbstractQueuedSynchronizer)
class Worker implements Runnable {
    private final BlockingQueue<Runnable> queue;
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Runnable task = queue.take(); // wait/notify inside BlockingQueue
                task.run();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }
}
```

## 8. Advanced Details

- **`wait(0)` vs `wait(timeout)`**: `wait(0)` waits indefinitely. `wait(timeout)` waits up to timeout ms, then returns (or earlier if notified/erroneously woken).
- **`notify()` vs `notifyAll()` performance**: `notifyAll()` wakes all waiters, causing "thundering herd" where most immediately go back to wait. `notify()` is cheaper but risks missed signals. Use `notifyAll()` unless contention is measurable problem.
- **`LockSupport.park()` / `unpark()`**: Lower-level than wait/notify. Used by `AbstractQueuedSynchronizer` (ReentrantLock, CountDownLatch). Does not require synchronized block.
- **`Condition` interface:** `Lock.newCondition()` provides `await()`/`signal()`/`signalAll()` with same semantics as wait/notify but works with `Lock` instead of intrinsic lock. Supports interruptible, timed, and spurious failure modes.
- **Priority inheritance:** When high-priority thread waits on monitor held by low-priority thread, JVM may temporarily boost low-priority thread priority to prevent priority inversion.
- **Spurious wakeups are rare but real:** POSIX pthread_cond_wait explicitly documents spurious wakeups. Java inherits this. JLS does not mandate but permits.
- **`wait()` releases ONLY the monitor it is called on:** If thread holds multiple locks, only the lock for which `wait()` is called is released. Other locks held remain held.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between `wait()` and `sleep()`? Can you call `wait()` outside a synchronized block?
A: `wait()` releases the object's lock and places the thread in the wait set. `sleep()` does not release any locks and simply pauses the thread for the specified duration. `wait()` must be called from within a synchronized block on the same object; calling it outside throws `IllegalMonitorStateException`. `sleep()` can be called anywhere.

### Intermediate
Q: Why must `wait()` always be called inside a `while` loop, not an `if` statement? What are spurious wakeups?
A: Because:
1. **Spurious wakeups**: `wait()` can return without `notify()`/`notifyAll()` being called (documented by JLS/OS). If we use `if`, the thread proceeds without the condition actually being true.
2. **Multiple waiters**: With multiple threads waiting, `notifyAll()` wakes all. Each thread reacquires lock one at a time. Between `notifyAll()` and this thread's reacquisition, another thread may have consumed the resource. `while` loop rechecks condition.

### Senior
Q: You are implementing a bounded blocking queue. `take()` uses `while (queue.isEmpty()) wait()`. Under high contention, you see threads waking up and immediately going back to wait (thundering herd). How would you optimize?
A: Thundering herd from `notifyAll()`: when producer adds item, it calls `notifyAll()`, waking ALL waiting consumers. Only one gets the item; the rest immediately find `queue.isEmpty()` still true (or another consumer grabbed first), and go back to wait.

Optimizations:
1. **Use `Lock` + `Condition`:** Create separate `Condition` for "not empty" and "not full". Signal specific condition: `notEmpty.signal()` instead of `notifyAll()`. Reduces unnecessary wakeups.
2. **Use `java.util.concurrent` classes:** `LinkedBlockingQueue`, `ArrayBlockingQueue` already optimized with `Condition` and `AbstractQueuedSynchronizer`.
3. **Reduce critical section:** Move non-critical work outside synchronized block.
4. **Avoid notifyAll in producer-consumer:** Use `notify()` if exactly one consumer can proceed. Risk: if consumer misses signal due to timing, may deadlock. Test thoroughly.

### Tricky
Q: `Thread A` calls `wait()` on object `X`. `Thread B` calls `notify()` on object `Y` (different object). Does Thread A wake up? What does this imply for designing wait/notify protocols?
A: No. `wait()` and `notify()` operate on the same monitor object. Thread A waits on monitor of `X`. Thread B notifies monitor of `Y`. They are completely independent. Thread A waits indefinitely.

This is the most common wait/notify bug. Design rule: all threads that need to coordinate must use the SAME lock object. In complex systems, prefer `java.util.concurrent` (CountDownLatch, CyclicBarrier, Phaser, Exchanger, SynchronousQueue) which make coordination explicit and avoid monitor confusion.

## 10. Final 30-Second Answer

`wait()` / `notify()` = inter-thread communication via object monitor. `wait()` releases lock, adds thread to wait set. `notify()` wakes one waiter; `notifyAll()` wakes all. **ALWAYS wrap `wait()` in `while` loop** to handle spurious wakeups and recheck condition. Must be inside `synchronized` on same object. Prefer `java.util.concurrent` (BlockingQueue, CountDownLatch, Condition) in production — less error-prone. `notify()` risky with multiple waiters; use `notifyAll()` unless performance profiling demands otherwise.