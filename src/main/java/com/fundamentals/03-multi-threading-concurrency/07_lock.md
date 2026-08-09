Excellent. This is one of the most frequently asked topics for **7+ years Java interviews**. Most candidates only know `ReentrantLock`, but senior interviews expect you to know **why the entire `java.util.concurrent.locks` package exists** and when to choose each lock.

---

# Module 4 — Chapter 13

# Locks Framework (`Lock`, `ReentrantLock`, `ReadWriteLock`, `StampedLock`) ⭐⭐⭐⭐⭐

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> Common in Oracle, Amazon, Microsoft, JPMorgan, Goldman Sachs, Uber, Flipkart and product companies.
>
> Interviewers typically ask:
>
> * Why use `ReentrantLock` when `synchronized` exists?
> * Fair vs Non-Fair lock?
> * What is `tryLock()`?
> * What is `lockInterruptibly()`?
> * What is `Condition`?
> * ReadWriteLock vs synchronized?
> * StampedLock vs ReadWriteLock?

---

# 1. Why Was the Lock Framework Introduced?

Before Java 5, developers only had:

```java
synchronized
```

Example

```java
public synchronized void transfer() {

    // business logic

}
```

It works.

But imagine a requirement:

> Try to acquire the lock for **2 seconds**. If not available, do something else.

Can `synchronized` do this?

**No.**

Or

> Cancel waiting if the thread is interrupted.

Can `synchronized` do this?

Again,

**No.**

The Lock Framework was introduced to provide **more control** over locking.

---

# 2. Lock Interface

The root interface is

```java
java.util.concurrent.locks.Lock
```

Important methods

```java
lock()

unlock()

tryLock()

lockInterruptibly()

newCondition()
```

Think of it as an advanced version of `synchronized`.

---

# 3. ReentrantLock ⭐⭐⭐⭐⭐

Most commonly used implementation.

Example

```java
private final Lock lock = new ReentrantLock();

public void deposit(int amount) {

    lock.lock();

    try {

        balance += amount;

    } finally {

        lock.unlock();

    }
}
```

Notice something important.

Unlike `synchronized`

you **must unlock manually**.

Always use

```java
finally
```

Otherwise,

deadlock may occur because the lock is never released.

---

# 4. Why "Reentrant"?

Exactly like `synchronized`.

The same thread can acquire the lock multiple times.

Example

```java
public void methodA() {

    lock.lock();

    try {

        methodB();

    } finally {

        lock.unlock();

    }

}

public void methodB() {

    lock.lock();

    try {

        // work

    } finally {

        lock.unlock();

    }

}
```

No deadlock.

Internally

```text
Acquire Lock

Hold Count = 1

↓

Acquire Again

Hold Count = 2

↓

Unlock

Count = 1

↓

Unlock

Count = 0
```

Exactly like monitor locks.

---

# 5. `tryLock()` ⭐⭐⭐⭐⭐

One of the biggest advantages over `synchronized`.

Example

```java
if (lock.tryLock()) {

    try {

        // Critical section

    } finally {

        lock.unlock();

    }

} else {

    System.out.println("Could not acquire lock");

}
```

Unlike

```java
lock.lock();
```

`tryLock()`

does **not wait**.

It immediately returns

```text
true
```

or

```text
false
```

---

## Timed `tryLock()`

Even better

```java
if (lock.tryLock(2, TimeUnit.SECONDS)) {

    try {

        // Work

    } finally {

        lock.unlock();

    }

}
```

Meaning

```text
Wait

↓

Maximum 2 seconds

↓

Acquire?

↓

Yes → Continue

No → Return false
```

Impossible with `synchronized`.

---

# 6. `lockInterruptibly()` ⭐⭐⭐⭐⭐

Suppose

Thread A

owns lock.

Thread B

is waiting.

Administrator decides

Cancel Thread B.

With `synchronized`

Impossible.

Thread B keeps waiting.

---

With

```java
lock.lockInterruptibly();
```

Thread B can be interrupted while waiting.

Example

```java
try {

    lock.lockInterruptibly();

    try {

        // work

    } finally {

        lock.unlock();

    }

} catch (InterruptedException e) {

    System.out.println("Interrupted while waiting");

}
```

Useful in responsive applications.

---

# 7. Fair vs Non-Fair Lock ⭐⭐⭐⭐⭐

Question

Several threads waiting.

Who gets lock first?

### Fair Lock

```java
Lock lock = new ReentrantLock(true);
```

Diagram

```text
Queue

Thread A

↓

Thread B

↓

Thread C

↓

Acquire Lock

A

↓

B

↓

C
```

FIFO.

Oldest waiting thread gets lock first.

---

### Non-Fair Lock (Default)

```java
Lock lock = new ReentrantLock();
```

or

```java
new ReentrantLock(false);
```

Thread arriving later may "barge" ahead.

Diagram

```text
Waiting

A

B

C

↓

New Thread D

↓

Gets Lock
```

Higher throughput.

Lower fairness.

Default because it is faster.

---

# 8. Condition Object ⭐⭐⭐⭐⭐

With

```java
synchronized
```

we have

```java
wait()

notify()

notifyAll()
```

With

```java
ReentrantLock
```

we use

```java
Condition
```

Example

```java
Lock lock = new ReentrantLock();

Condition notEmpty = lock.newCondition();
```

Methods

```java
await()

signal()

signalAll()
```

Equivalent mapping

| synchronized | Lock API    |
| ------------ | ----------- |
| wait()       | await()     |
| notify()     | signal()    |
| notifyAll()  | signalAll() |

---

# 9. Why Condition is Better

Object monitor has

only **one waiting queue**.

Diagram

```text
Monitor

↓

Waiting Queue
```

Condition allows

multiple queues.

Example

```java
Condition producerQueue;

Condition consumerQueue;
```

Diagram

```text
Lock

↓

Producer Queue

↓

Consumer Queue
```

Producer wakes only consumers.

Consumers wake only producers.

Much more efficient.

---

# 10. ReadWriteLock ⭐⭐⭐⭐⭐

Suppose

1000 threads

Only reading data.

Example

```java
getUser()
```

Do readers really need to block each other?

No.

---

ReadWriteLock solves this.

Diagram

```text
Readers

Thread A

Thread B

Thread C

↓

Read Together

----------------

Writer

Thread D

↓

Exclusive Access
```

Many readers

One writer.

---

Example

```java
ReadWriteLock rwLock = new ReentrantReadWriteLock();

rwLock.readLock().lock();

rwLock.writeLock().lock();
```

---

# 11. When Can Readers Execute Together?

Example

```java
public User getUser() {

}
```

Ten threads call simultaneously.

Diagram

```text
Reader

↓

Reader

↓

Reader

↓

Reader

↓

All Run Together
```

Huge performance improvement.

---

# 12. When Writer Arrives

Diagram

```text
Readers Running

↓

Writer Arrives

↓

Wait

↓

Readers Finish

↓

Writer Starts
```

No reader and writer execute simultaneously.

---

# 13. StampedLock ⭐⭐⭐⭐⭐

Introduced in Java 8.

Designed for

Very high read performance.

Supports

* Read Lock
* Write Lock
* Optimistic Read

---

# 14. Optimistic Read

Normal Read Lock

```text
Acquire Lock

↓

Read

↓

Unlock
```

Optimistic Read

```text
Read Without Lock

↓

Check

↓

Was Data Modified?

↓

No

Success

↓

Yes

Retry
```

Very fast.

Perfect when writes are rare.

---

Example

```java
long stamp = lock.tryOptimisticRead();

double x = this.x;

double y = this.y;

if (!lock.validate(stamp)) {

    stamp = lock.readLock();

    try {

        x = this.x;
        y = this.y;

    } finally {

        lock.unlockRead(stamp);

    }

}
```

---

# 15. Lock Comparison

| Feature          | synchronized | ReentrantLock | ReadWriteLock | StampedLock |
| ---------------- | ------------ | ------------- | ------------- | ----------- |
| Mutual Exclusion | ✔            | ✔             | Write only    | Write only  |
| Fairness         | ✘            | ✔             | ✔             | ✘           |
| tryLock()        | ✘            | ✔             | ✔             | ✔           |
| Interruptible    | ✘            | ✔             | ✔             | ✔           |
| Multiple Readers | ✘            | ✘             | ✔             | ✔           |
| Optimistic Read  | ✘            | ✘             | ✘             | ✔           |
| Manual Unlock    | ✘            | ✔             | ✔             | ✔           |

---

# 16. Which Lock Should You Use?

### Use `synchronized`

* Simple critical sections
* Low contention
* Small applications
* Simpler code

---

### Use `ReentrantLock`

* Need `tryLock()`
* Need interruptible waiting
* Need fairness
* Need multiple conditions

---

### Use `ReadWriteLock`

* Many readers
* Few writers
* Read-heavy caches
* Configuration data

---

### Use `StampedLock`

* Extremely read-heavy systems
* Writes are very rare
* High-performance concurrent algorithms

---

# 17. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. Why use `ReentrantLock` instead of `synchronized`?

Because it provides features unavailable in `synchronized`, such as:

* `tryLock()`
* Timed lock acquisition
* Interruptible locking
* Fair locking
* Multiple `Condition` objects

---

### Q2. What is Fair Lock?

A fair lock grants access roughly in FIFO order to waiting threads.

---

### Q3. Why is Non-Fair Lock the default?

Because allowing newly arriving threads to acquire the lock can reduce context switching and improve throughput.

---

### Q4. Difference between `wait()` and `await()`?

`wait()` belongs to the monitor (`Object`) and requires `synchronized`.

`await()` belongs to a `Condition` and requires a `Lock`.

---

### Q5. When should you use `ReadWriteLock`?

When reads greatly outnumber writes, allowing multiple readers to execute concurrently while still providing exclusive access for writers.

---

### Q6. What is Optimistic Locking in `StampedLock`?

The thread reads without acquiring a traditional read lock and later validates whether a write occurred during the read. If validation fails, it retries under a real read lock.

---

# 18. Production Examples

### Example 1: Configuration Cache

Thousands of requests read configuration.

Configuration changes once every few hours.

**Best choice:** `ReadWriteLock` or `StampedLock`.

---

### Example 2: Banking Transaction

Money transfer updates account balances.

**Best choice:** `ReentrantLock` or `synchronized`, because writes must be mutually exclusive.

---

### Example 3: LRU Cache

Many concurrent reads with occasional writes.

**Good choice:** `ReadWriteLock`.

---

# 19. Common Mistakes

### ❌ Forgetting `unlock()`

```java
lock.lock();

// Exception thrown

// unlock() never called
```

Always use

```java
lock.lock();

try {

    // work

} finally {

    lock.unlock();

}
```

---

### ❌ Using `StampedLock` with long-running operations

Optimistic reads are intended for **very short read operations**. If validation fails repeatedly because writes are frequent or reads are long, performance may degrade.

---

### ❌ Assuming `ReadWriteLock` is always faster

If writes are frequent, readers are often blocked waiting for writers, and the additional lock-management overhead can make it slower than a simple `ReentrantLock` or `synchronized`.

---

# 20. Interview Boundary ⭐⭐⭐⭐⭐

For a **7+ years Java Developer**, you should confidently explain:

* Why the Lock framework was introduced.
* `Lock` vs `synchronized`.
* `ReentrantLock` features.
* `tryLock()`, timed `tryLock()`, and `lockInterruptibly()`.
* Fair vs Non-Fair locking.
* `Condition` vs `wait()/notify()`.
* `ReadWriteLock` internals and use cases.
* `StampedLock` and optimistic reads.
* Which lock to choose for different production scenarios.

---

## Next Chapter Recommendation

The natural next topic is:

# **Thread Communication (`wait()`, `notify()`, `notifyAll()`, Producer-Consumer Pattern)** ⭐⭐⭐⭐⭐

This chapter will connect everything you've learned so far and explain:

* Why threads need communication, not just synchronization.
* `wait()`, `notify()`, and `notifyAll()` internals.
* Why `wait()` releases the monitor but `sleep()` does not.
* Spurious wakeups.
* Producer-Consumer implementation using `wait()/notify()`.
* Modern alternatives like `BlockingQueue`.

This is another topic that appears regularly in senior Java interviews and complements the locking concepts you've just covered.
