# CountDownLatch & CyclicBarrier — Complete Deep Dive

## 1. Why This Concept Matters

CountDownLatch and CyclicBarrier are the two primary coordination primitives in `java.util.concurrent`. CountDownLatch lets one or more threads wait for a set of operations to complete. CyclicBarrier lets a fixed set of threads wait for each other at a common point. They solve fundamentally different problems but are commonly confused. Interviewers test these to assess understanding of thread coordination patterns beyond basic join/wait/notify.

Misunderstanding these causes:
- Using CountDownLatch when CyclicBarrier is needed (or vice versa)
- Deadlocks from incorrect count (too many/too few countDown calls)
- Missed reuse — calling `await()` on used CountDownLatch blocks forever
- Thread leakage from CyclicBarrier timeout

## 2. Basic Meaning

**CountDownLatch**: A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.

**CyclicBarrier**: A synchronization aid that allows a set of threads to all wait for each other to reach a common barrier point.

**Key vocabulary:**
- **CountDownLatch `countDown()`**: decrement the count
- **CountDownLatch `await()`**: wait until count reaches zero
- **CyclicBarrier `await()`**: wait until all parties arrive at barrier
- **Parties**: number of threads that must reach barrier
- **Barrier action**: optional Runnable that runs when barrier is tripped
- **Broken barrier**: one thread timed out/interrupted → barrier broken
- **`reset()`**: reset barrier to initial state (CyclicBarrier only)

**Critical difference:**
- CountDownLatch: threads `countDown()` (fire-and-forget) + threads `await()` (wait). CountDownLatch is ONE-SHOT.
- CyclicBarrier: all threads `await()`. Barrier REUSABLE (cyclic) after all parties arrive.

What they are NOT: Not for mutual exclusion. Not for producer-consumer queueing. Not for signaling between arbitrary threads (use Condition or wait/notify).

## 3. Real Code / Real Example

```java
import java.util.concurrent.*;

public class LatchBarrierDemo {
    public static void main(String[] args) throws InterruptedException {
        // === COUNTDOWNLATCH: Start signal ===
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(3);

        for (int i = 1; i <= 3; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    System.out.println("Worker " + workerId + " waiting for start signal");
                    startSignal.await(); // wait for main thread to start us
                    System.out.println("Worker " + workerId + " started working");
                    Thread.sleep(workerId * 500L);
                    System.out.println("Worker " + workerId + " finished");
                    doneSignal.countDown();
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }).start();
        }

        System.out.println("Main: preparing...");
        Thread.sleep(1000);
        System.out.println("Main: GO!");
        startSignal.countDown(); // RELEASE all workers at once

        doneSignal.await(); // wait for all workers to finish
        System.out.println("Main: All workers done!");

        // === CYCLICBARRIER: Reusable synchronization point ===
        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
            System.out.println("=== ALL THREADS REACHED BARRIER ===");
        });

        for (int i = 1; i <= parties; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int round = 1; round <= 2; round++) {
                        System.out.println("Thread " + threadId + " completed round " + round);
                        barrier.await(); // wait for others
                    }
                    System.out.println("Thread " + threadId + " DONE");
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        // === COUNTDOWNLATCH: Parallel processing ===
        CountDownLatch parallelDone = new CountDownLatch(4);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep((long)(Math.random() * 1000));
                    System.out.println("Parallel task done");
                    parallelDone.countDown();
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        parallelDone.await(); // wait for all parallel tasks
        System.out.println("All parallel tasks complete");
        executor.shutdown();

        // === CYCLICBARRIER: Timeout handling ===
        CyclicBarrier timeoutBarrier = new CyclicBarrier(3);
        Thread t1 = new Thread(() -> {
            try {
                timeoutBarrier.await(1, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.out.println("t1 timed out waiting for barrier");
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
        });
        t1.start();
        // Only 2 threads arrive (t1 + main), 3rd never comes
        try { timeoutBarrier.await(1, TimeUnit.SECONDS); } catch (Exception e) { }
        t1.join();
        System.out.println("Barrier broken: " + timeoutBarrier.isBroken()); // true
    }
}
```

Expected output (simplified):
```
Worker 1 waiting for start signal
Worker 2 waiting for start signal
Worker 3 waiting for start signal
Main: preparing...
Main: GO!
Worker 1 started working
Worker 2 started working
Worker 3 started working
Worker 1 finished
Worker 2 finished
Worker 3 finished
Main: All workers done!
Thread 1 completed round 1
Thread 2 completed round 1
Thread 3 completed round 1
=== ALL THREADS REACHED BARRIER ===
Thread 1 completed round 2
Thread 2 completed round 2
Thread 3 completed round 2
=== ALL THREADS REACHED BARRIER ===
Thread 1 DONE
Thread 2 DONE
Thread 3 DONE
Parallel task done
Parallel task done
Parallel task done
Parallel task done
All parallel tasks complete
t1 timed out waiting for barrier
Barrier broken: true
```

## 4. What Happens Internally

**CountDownLatch uses AQS (AbstractQueuedSynchronizer):**
- `state` = count. `countDown()` = `releaseShared(1)` (CAS decrement). `await()` = `acquireSharedInterruptibly(1)` (park until state == 0).
- State `0` is terminal. Once zero, all future `await()` return immediately.

**CyclicBarrier uses ReentrantLock + Condition:**
```java
public class CyclicBarrier {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition trip = lock.newCondition();
    private final int parties;
    private final Runnable barrierCommand;
    private int count; // remaining parties to arrive
    private Generation generation; // tracks reset cycles
}
```

`await()` flow:
1. Acquire lock
2. Decrement `count`
3. If count == 0: run barrier action, signal all, reset count, increment generation
4. If count > 0: `trip.await()` (park on condition)
5. On signal: wake, check if generation changed (if yes, done), else loop

`reset()`: break current generation, wake all waiters (BrokenBarrierException), create new generation.

## 5. Tricky Interview Cases

**Case 1 — CountDownLatch count mismatch**
```java
CountDownLatch latch = new CountDownLatch(3);
// Only 2 threads call countDown()
latch.await(); // BLOCKS FOREVER — count never reaches 0
```
Problem: countDown never matches initial count. **Fix**: Always ensure exactly n countDown calls.

**Case 2 — Reusing CountDownLatch**
```java
CountDownLatch latch = new CountDownLatch(1);
latch.countDown(); // count = 0
latch.await(); // returns immediately (OK)
// Cannot reset! Need new CountDownLatch for next phase
latch.await(); // still returns immediately (state 0 is terminal)
```
Output: Second await returns immediately — cannot reuse.

**Case 3 — CyclicBarrier broken by timeout**
```java
CyclicBarrier barrier = new CyclicBarrier(3);
// Thread 1
try { barrier.await(1, TimeUnit.SECONDS); } catch (TimeoutException e) { }
// Thread 2
barrier.await(); // BrokenBarrierException — barrier broken!
// Thread 3
barrier.await(); // BrokenBarrierException — barrier broken!
```
Output: Timeout breaks barrier permanently for that generation. `reset()` needed.

**Case 4 — CyclicBarrier with barrier action execution order**
```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("Barrier action by: " + Thread.currentThread().getName()));
// All 3 threads await at barrier
// The LAST thread to arrive runs the barrier action
System.out.println("Main: " + Thread.currentThread().getName());
```
Output: Barrier action runs in the LAST arriving thread.

**Case 5 — `getNumberWaiting()` approximate**
```java
CyclicBarrier barrier = new CyclicBarrier(5);
System.out.println(barrier.getNumberWaiting()); // 0
// 3 threads arrive, 2 still pending
System.out.println(barrier.getNumberWaiting()); // ~3 (approximate under race)
```
Output: Approximate count due to race between threads.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| CountDownLatch when need reuse | Cannot reset | Use CyclicBarrier or new CountDownLatch each phase |
| CyclicBarrier when need one-shot | Barrier reusable (OK) but may be confusing | Either works, but CyclicBarrier has broken barrier risks |
| Missing countDown in exception handler | Latch never reaches 0 | CountDownLatch: countDown() in finally block |
| Not handling BrokenBarrierException | Barrier broken = threads stuck | Catch BrokenBarrierException, reset or fail gracefully |
| reset() during active barrier | All waiters get BrokenBarrierException | Only reset when all threads have arrived or broken |

## 7. Production Usage

**Parallel test execution (CountDownLatch):**
```java
CountDownLatch allDone = new CountDownLatch(100);
for (int i = 0; i < 100; i++) {
    executor.submit(() -> {
        try { runTest(); } finally { allDone.countDown(); }
    });
}
allDone.await(); // wait for all tests
```

**Fork/join simulation (CyclicBarrier):**
```java
CyclicBarrier barrier = new CyclicBarrier(n, () -> mergeResults());
// Each thread processes partition, awaits barrier, then merges
for (int i = 0; i < n; i++) {
    new Thread(() -> {
        while (moreWork) {
            processPartition();
            barrier.await(); // all reach here → merge, then continue
        }
    }).start();
}
```

## 8. Advanced Details

- **CountDownLatch `await(timeout)`**: Returns false if count > 0 after timeout.
- **CyclicBarrier `getParties()`**: Number of parties required to trip barrier.
- **CyclicBarrier `isBroken()`**: Check if barrier is broken.
- **CountDownLatch `getCount()`**: Current count (debugging only, approximate).
- **CountDownLatch `await()` vs `join()`**: `await()` on latch (many-to-one pattern), `join()` on thread (thread death). Latch more flexible.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between CountDownLatch and CyclicBarrier?
A: CountDownLatch: threads `countDown()` (fire-and-forget), threads `await()` until count=0. One-shot, not reusable. CyclicBarrier: `n` threads all call `await()` and wait for each other. Reusable after all parties arrive. CyclicBarrier has optional barrier action Runnable.

### Intermediate
Q: Can you use CountDownLatch to synchronize a repeated computation across multiple phases?
A: You can, but you need a NEW CountDownLatch for each phase. CyclicBarrier is better for multi-phase: same barrier object, `reset()` not needed (auto-resets after all parties arrive).

### Senior
Q: You have 4 threads processing 4 partitions of a large dataset. Each thread must finish its partition, then all 4 threads merge results before starting the next iteration. How would you implement this? What if one thread throws an exception?
A: Use CyclicBarrier(4, mergeResultRunnable). Each thread: process → barrier.await() → continue.

Exception handling: If one thread throws, the other 3 threads wait at barrier forever. Fix: use `await(timeout)` on barrier, catch TimeoutException, handle partial results. Or use `Phaser` (more advanced) which allows unregister on failure.

### Tricky
Q: `CountDownLatch` uses AQS internally. `CyclicBarrier` uses ReentrantLock + Condition. Why the different design?
A: CountDownLatch needs only a count (0 or not 0) — AQS `state` serves as atomic count. `countDown` releases shared state, `await` acquires shared. Simple.

CyclicBarrier needs a reusable count that resets after each generation. AQS state is one-way (only decrements). CyclicBarrier tracks `parties`, `count`, and `generation` — mutable fields protected by ReentrantLock. Condition is natural fit for reusable barrier: `trip.await()` on each cycle, `signalAll()` when barrier tripped. AQS doesn't support reset natively.

## 10. Final 30-Second Answer

**CountDownLatch = one-shot gate**: `countDown()` decrements, `await()` blocks until 0. Use for: start all threads at once, wait for n tasks to finish. NOT reusable. **CyclicBarrier = reusable rendezvous**: n threads `await()` at barrier, all proceed when all arrive. Auto-resets. Optional barrier action. Use for: multi-phase computation. Broken barrier on timeout/interrupt. Handle `BrokenBarrierException`. `Phaser` is more flexible alternative.