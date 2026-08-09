Excellent. This is one of the **most misunderstood topics** in Java concurrency. Many developers can write `wait()` and `notify()`, but very few understand **what actually happens to the thread and the monitor**.

This chapter is frequently asked in **6–10 years Java interviews**, especially the differences between `wait()`, `sleep()`, `notify()`, `notifyAll()`, and `BlockingQueue`.

---

# Module 4 — Chapter 14

# Thread Communication (`wait()`, `notify()`, `notifyAll()`) ⭐⭐⭐⭐⭐

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> Interviewers commonly ask:
>
> * What is thread communication?
> * Difference between `wait()` and `sleep()`
> * Why must `wait()` be inside `synchronized`?
> * Why does `wait()` release the lock?
> * `notify()` vs `notifyAll()`
> * Spurious Wakeup
> * Producer-Consumer problem
> * Why use `BlockingQueue` instead?

---

# 1. Why Do Threads Need Communication?

Synchronization prevents **data corruption**.

Communication allows threads to **coordinate**.

Example:

Imagine an online shopping system.

* Producer → Places new orders
* Consumer → Processes orders

Question:

If there are no orders,

should the consumer keep checking continuously?

```java
while (true) {

    if (!queue.isEmpty()) {
        process();
    }

}
```

This is called **Busy Waiting (Busy Spinning).**

Problems:

* CPU usage becomes 100%
* Battery drain
* Waste of CPU cycles

We need a way for the consumer to **sleep until work arrives**.

---

# 2. Thread Communication

Instead of continuously checking,

Producer tells Consumer

```text
Consumer,

Nothing available.

Go to sleep.

I'll wake you up later.
```

Diagram

```text
Producer

↓

Produces Data

↓

notify()

--------------------

Consumer

↓

wait()

↓

Sleeping

↓

Wake Up

↓

Consume
```

This is called **Thread Communication**.

---

# 3. Object Monitor

Every Java object has a monitor.

Think of it like this:

```text
+----------------------+
|      Object          |
|----------------------|
| Monitor Lock         |
| Wait Set (Queue)     |
+----------------------+
```

The monitor contains:

* Lock (Mutual Exclusion)
* Wait Set (Threads waiting)

Important:

`wait()`, `notify()`, and `notifyAll()` all operate on the **same object's monitor**.

---

# 4. `wait()`

Suppose

Consumer

```java
synchronized (queue) {

    while (queue.isEmpty()) {

        queue.wait();

    }

}
```

Question

What happens?

Sequence

```text
Acquire Lock

↓

Queue Empty

↓

Release Lock

↓

Move Thread to Wait Set

↓

Thread Sleeps
```

Very important.

`wait()`

**releases the monitor lock**.

---

# 5. Why Must `wait()` Release the Lock?

Imagine if it didn't.

Consumer

```text
Consumer

Acquire Lock

↓

wait()

↓

Still Holding Lock
```

Producer

```text
Producer

Needs Same Lock

↓

Blocked Forever
```

Deadlock.

Therefore,

`wait()` always:

* Releases lock
* Sleeps
* Reacquires lock before returning

---

# 6. `notify()`

Producer

```java
synchronized(queue){

    queue.add(item);

    queue.notify();

}
```

Sequence

```text
Producer

Acquire Lock

↓

Add Item

↓

notify()

↓

Release Lock
```

Important

`notify()`

**does not release the lock immediately**.

The awakened thread cannot continue until the notifier exits the synchronized block and releases the monitor.

---

# 7. What Happens After `notify()`?

Diagram

```text
Consumer

wait()

↓

WAITING

------------------

Producer

notify()

↓

Still Owns Lock

↓

Unlock

------------------

Consumer

Reacquires Lock

↓

Continues
```

Interview favourite.

---

# 8. `notify()` vs `notifyAll()` ⭐⭐⭐⭐⭐

Suppose

Three consumers waiting.

```text
Consumer A

Consumer B

Consumer C
```

### notify()

```text
Wake ONE Thread
```

Which one?

JVM decides.

No guarantee.

---

### notifyAll()

```text
Wake

A

B

C
```

All become runnable.

They compete for the monitor.

Only one acquires it first.

---

# 9. Why `notifyAll()` is Usually Safer

Imagine

Producer

Consumer

Both waiting.

Calling

```java
notify();
```

may wake another producer instead of a consumer.

Nothing progresses.

Possible deadlock or starvation.

With

```java
notifyAll();
```

every waiting thread wakes up.

The correct one proceeds after checking the condition again.

This is why `notifyAll()` is generally preferred unless you can prove that waking a single thread is sufficient.

---

# 10. Why `wait()` Must Be Inside `while`, Not `if` ⭐⭐⭐⭐⭐

Wrong

```java
if(queue.isEmpty()){

    queue.wait();

}
```

Correct

```java
while(queue.isEmpty()){

    queue.wait();

}
```

Why?

Because of **Spurious Wakeups**.

---

# 11. Spurious Wakeup

A waiting thread may wake up **without**:

* `notify()`
* `notifyAll()`
* timeout

This is permitted by the JVM specification.

Therefore,

always recheck the condition.

Diagram

```text
Thread

↓

wait()

↓

Unexpected Wakeup

↓

Check Condition Again

↓

Still Empty?

↓

wait()
```

Hence

Always

```java
while(condition){

    wait();

}
```

Never

```java
if(condition){

    wait();

}
```

---

# 12. Producer-Consumer Example ⭐⭐⭐⭐⭐

```java
class SharedQueue {

    private final Queue<Integer> queue = new LinkedList<>();

    private final int capacity = 5;

    public synchronized void produce(int item)
            throws InterruptedException {

        while (queue.size() == capacity) {
            wait();
        }

        queue.offer(item);

        notifyAll();
    }

    public synchronized int consume()
            throws InterruptedException {

        while (queue.isEmpty()) {
            wait();
        }

        int value = queue.poll();

        notifyAll();

        return value;
    }
}
```

---

# 13. Execution Flow

Initially

```text
Queue Empty
```

Consumer

```text
Acquire Lock

↓

Empty

↓

wait()

↓

WAITING
```

Producer

```text
Acquire Lock

↓

Insert

↓

notifyAll()

↓

Unlock
```

Consumer

```text
Wake

↓

Acquire Lock

↓

Remove Item
```

---

# 14. `wait()` vs `sleep()` ⭐⭐⭐⭐⭐

| wait()                                              | sleep()                           |
| --------------------------------------------------- | --------------------------------- |
| Releases monitor lock                               | Does **not** release lock         |
| Must be inside synchronized                         | Can be called anywhere            |
| Object method                                       | Thread static method              |
| Used for thread communication                       | Used for delaying execution       |
| Needs `notify()`/`notifyAll()` (or timeout) to wake | Wakes automatically after timeout |

---

## Example

```java
synchronized(lock){

    Thread.sleep(5000);

}
```

Lock is still held.

Other threads cannot enter.

---

```java
synchronized(lock){

    lock.wait();

}
```

Lock released.

Other threads can acquire it.

---

# 15. State Diagram

```text
RUNNING

↓

wait()

↓

WAITING

↓

notify()

↓

BLOCKED (waiting to reacquire monitor)

↓

RUNNING
```

Notice:

After `notify()`, the thread does **not** immediately become RUNNING. It first competes to reacquire the monitor.

---

# 16. Common Mistakes

### Calling `wait()` without synchronization

```java
queue.wait();
```

Throws

```text
IllegalMonitorStateException
```

Because the thread does not own the monitor.

---

### Using `if` instead of `while`

Wrong

```java
if(queue.isEmpty())

    wait();
```

Correct

```java
while(queue.isEmpty())

    wait();
```

---

### Forgetting `notify()`

Consumer waits forever.

Classic interview question.

---

### Using `notify()` when multiple conditions exist

Can wake the wrong thread.

Often safer to use `notifyAll()`.

---

# 17. Modern Alternative — `BlockingQueue` ⭐⭐⭐⭐⭐

Instead of manually writing

```java
wait()

notify()

notifyAll()
```

Use

```java
BlockingQueue<Integer> queue =
        new ArrayBlockingQueue<>(10);
```

Producer

```java
queue.put(item);
```

Consumer

```java
int item = queue.take();
```

Internally,

`BlockingQueue` handles:

* Waiting
* Waking
* Locking
* Thread safety

No explicit `wait()` or `notify()` needed.

This is the preferred approach in modern Java applications.

---

# 18. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. Why must `wait()` be called inside `synchronized`?

Because the calling thread must own the object's monitor before it can release it and enter the wait set.

---

### Q2. Why does `wait()` release the lock?

So that another thread (typically the producer) can acquire the lock, change the shared state, and notify waiting threads.

---

### Q3. Does `notify()` release the lock immediately?

No.

It only moves a waiting thread to the runnable state. The lock is released only when the synchronized block exits.

---

### Q4. Why use `while` instead of `if` around `wait()`?

To protect against spurious wakeups and because another thread may have consumed the shared resource before the awakened thread reacquires the lock.

---

### Q5. Difference between `notify()` and `notifyAll()`?

`notify()` wakes one arbitrary waiting thread.

`notifyAll()` wakes all waiting threads, which then compete to acquire the monitor.

---

### Q6. Why is `BlockingQueue` preferred today?

It encapsulates locking and thread coordination, making producer-consumer implementations simpler, safer, and less error-prone.

---

# 19. Production Examples

### Logging System

Multiple application threads produce log messages.

A background logging thread consumes them.

**Best choice:** `BlockingQueue`.

---

### Order Processing

Many clients place orders.

Worker threads process them asynchronously.

**Best choice:** `BlockingQueue` with `ExecutorService`.

---

### Task Scheduler

Producers enqueue tasks.

Worker pool consumes them.

Again, `BlockingQueue` is the standard choice.

---

# 20. Interview Boundary ⭐⭐⭐⭐⭐

For a **7+ years Java Developer**, you should confidently explain:

* Why thread communication is needed.
* Object monitor and wait set.
* Internal working of `wait()`.
* Why `wait()` releases the lock.
* Internal working of `notify()` and `notifyAll()`.
* `wait()` vs `sleep()`.
* Spurious wakeups.
* Why `while` is mandatory.
* Producer-Consumer using `wait()/notifyAll()`.
* Why modern code prefers `BlockingQueue`.

---

## Next Chapter Recommendation

The next logical topic is:

# **Executor Framework & Thread Pools** ⭐⭐⭐⭐⭐

This is one of the most important topics for senior Java interviews. We'll cover:

* Why creating threads manually is a bad idea.
* `Executor`, `ExecutorService`, and `ScheduledExecutorService`.
* `ThreadPoolExecutor` internals.
* Core Pool Size vs Maximum Pool Size.
* Work queues (`ArrayBlockingQueue`, `LinkedBlockingQueue`, `SynchronousQueue`).
* Rejection policies.
* Thread pool sizing for CPU-bound vs I/O-bound workloads.
* Common production issues (thread pool exhaustion, memory leaks, starvation).

This naturally follows thread communication because modern concurrent Java applications rely on thread pools rather than manually managing threads.
