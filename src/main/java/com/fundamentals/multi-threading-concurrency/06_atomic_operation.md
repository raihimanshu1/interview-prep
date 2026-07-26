Excellent. This chapter is where interviewers separate people who **know concurrency** from people who merely **use concurrency**.

Most candidates know `AtomicInteger.incrementAndGet()`. Very few can explain **how it works internally**.

---

# Module 4 — Chapter 12

# Atomic Operations, CAS (Compare-And-Swap) & Atomic Classes ⭐⭐⭐⭐⭐

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> This topic is extremely common for **6–10 years Java interviews**, especially in product companies (Amazon, Oracle, JPMorgan, Microsoft, Flipkart, Uber).
>
> Interviewers expect you to explain:
>
> * Why `count++` is not atomic
> * What is an atomic operation
> * How `AtomicInteger` works internally
> * What CAS (Compare-And-Swap) is
> * Why CAS is lock-free
> * CAS vs synchronized
> * ABA Problem (basic understanding)

---

# 1. Why Do We Need Atomic Classes?

Suppose we have

```java
class Counter {

    int count = 0;

    public void increment() {
        count++;
    }
}
```

Two threads execute

```java
counter.increment();
```

Expected

```text
count = 2
```

Actual

```text
count = 1
```

Why?

Because

```java
count++;
```

is **not one operation**.

---

# 2. `count++` is Actually Three Operations ⭐⭐⭐⭐⭐

Java code

```java
count++;
```

Internally

```text
Read count

↓

Add 1

↓

Write count
```

Diagram

```text
Thread A

Read = 5

↓

Add = 6

↓

Write = 6

----------------------

Thread B

Read = 5

↓

Add = 6

↓

Write = 6

----------------------

Expected = 7

Actual = 6
```

One update is lost.

This is called a **Lost Update**.

---

# 3. What is an Atomic Operation?

### Interview Definition

> An **atomic operation** is an operation that executes as a single, indivisible unit. No other thread can observe it in a partially completed state.

Think of it like a light switch.

```text
OFF

↓

ON
```

There is no "half ON" state.

Atomic means:

* Either complete
* Or not executed

Never partially completed.

---

# 4. Traditional Solution — `synchronized`

```java
class Counter {

    private int count;

    public synchronized void increment() {
        count++;
    }
}
```

Works.

But...

```text
Acquire Lock

↓

Modify

↓

Release Lock
```

Problems

* Lock acquisition
* Context switching
* Thread blocking
* Waiting

This becomes expensive under heavy contention.

---

# 5. Better Solution — `AtomicInteger`

```java
AtomicInteger counter = new AtomicInteger(0);

counter.incrementAndGet();
```

Question

Does it use

```java
synchronized
```

No.

There is **no monitor lock**.

Instead,

it uses **CAS (Compare-And-Swap).**

---

# 6. What is CAS?

### Interview Definition

> Compare-And-Swap (CAS) is an atomic CPU instruction that updates a memory location only if its current value matches an expected value.

Think of CAS as asking:

> "Is the value still what I expect?"

If yes

Update it.

Otherwise

Retry.

---

# 7. CAS Example ⭐⭐⭐⭐⭐

Suppose

```text
count = 5
```

Thread A wants

```text
6
```

CAS performs

```text
Compare

Current = 5

Expected = 5

Match

↓

Replace

6
```

Success.

---

Now imagine another thread already changed it.

Current

```text
6
```

Expected

```text
5
```

CAS

```text
Compare

6 == 5 ?

NO

↓

Fail
```

Nothing changes.

The thread retries with the latest value.

---

# 8. CAS Retry Loop

Internally,

`AtomicInteger.incrementAndGet()` behaves conceptually like this:

```java
public int incrementAndGet() {

    while (true) {

        int current = get();

        int next = current + 1;

        if (compareAndSet(current, next)) {
            return next;
        }

    }
}
```

Notice

There is **no lock**.

If another thread wins,

this thread simply retries.

---

# 9. How `compareAndSet()` Works

Example

```java
AtomicInteger counter = new AtomicInteger(5);

counter.compareAndSet(5, 6);
```

Meaning

```text
Current Value

↓

Is it 5 ?

↓

YES

↓

Replace with 6
```

If current value is already

```text
7
```

CAS fails.

No update.

---

# 10. CAS Visual Diagram ⭐⭐⭐⭐⭐

Suppose

```text
Initial

count = 10
```

Thread A

```text
Read

10
```

Thread B

```text
Read

10
```

Thread A

```text
CAS

Expected = 10

Current = 10

Success

count = 11
```

Thread B

```text
CAS

Expected = 10

Current = 11

Fail

↓

Retry

Read 11

↓

CAS

Expected = 11

↓

Success

12
```

Final

```text
12
```

No lost update.

---

# 11. Why CAS is Lock-Free ⭐⭐⭐⭐⭐

Traditional locking

```text
Acquire Lock

↓

Wait

↓

Execute

↓

Release
```

CAS

```text
Read

↓

Compare

↓

Swap

↓

Retry if needed
```

No monitor.

No blocking.

No waiting queue.

Threads continue making progress.

This is called **Lock-Free Programming**.

---

# 12. CAS vs `synchronized`

| CAS                                          | synchronized                       |
| -------------------------------------------- | ---------------------------------- |
| Lock-free                                    | Lock-based                         |
| No blocking                                  | Threads may block                  |
| Retry on conflict                            | Wait for lock                      |
| Usually faster under low/moderate contention | Can be slower due to blocking      |
| May spin repeatedly under heavy contention   | Predictable under heavy contention |

---

# 13. Atomic Classes in Java

Java provides many atomic classes.

```java
AtomicInteger

AtomicLong

AtomicBoolean

AtomicReference
```

Example

```java
AtomicLong idGenerator = new AtomicLong();

long id = idGenerator.incrementAndGet();
```

---

# 14. `AtomicReference`

Not only primitives.

Objects too.

```java
AtomicReference<User> user =
        new AtomicReference<>();
```

CAS can replace an entire object reference atomically.

Very useful in concurrent data structures.

---

# 15. Where is CAS Used?

Internally,

many Java classes rely on CAS.

Examples

* `AtomicInteger`
* `ConcurrentHashMap`
* `ConcurrentLinkedQueue`
* `LongAdder`
* `ForkJoinPool`
* `CompletableFuture`
* Thread pool internals

Even the JVM itself uses CAS extensively.

---

# 16. What is the ABA Problem? ⭐⭐⭐⭐⭐

One limitation of CAS.

Suppose

Initially

```text
Value = A
```

Thread A reads

```text
A
```

Meanwhile,

Thread B changes

```text
A → B → A
```

Now Thread A performs

```text
CAS

Expected = A

Current = A

Success
```

CAS thinks nothing changed.

But it actually did.

Diagram

```text
Initial

A

↓

Thread B

A

↓

B

↓

A

↓

Thread A

CAS Success
```

This is called the **ABA Problem**.

---

# 17. How Java Solves ABA

Using

```java
AtomicStampedReference
```

Instead of storing

```text
Value
```

Store

```text
Value

+

Version Number
```

Example

```text
A (Version 1)

↓

B (Version 2)

↓

A (Version 3)
```

CAS now compares

* Value
* Version

Both must match.

---

# 18. Common Mistakes

### ❌ Assuming AtomicInteger uses synchronized

It doesn't.

It primarily relies on CAS and low-level JVM support.

---

### ❌ Using AtomicInteger for multiple related variables

Wrong

```java
balance

transactions
```

Updating both together still isn't atomic.

Use locking when multiple variables must change as one logical operation.

---

### ❌ Thinking CAS always wins

CAS may fail.

Failure is expected under contention.

The algorithm retries until successful.

---

# 19. Production Example

Imagine a page view counter.

Thousands of users increment it simultaneously.

Instead of

```java
synchronized increment()
```

Use

```java
AtomicLong views = new AtomicLong();

views.incrementAndGet();
```

Advantages

* No blocking
* High throughput
* Better scalability under many concurrent readers/writers

---

# 20. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. Why is `count++` not atomic?

Because it consists of three operations:

* Read
* Increment
* Write

Another thread can interleave between them.

---

### Q2. How does `AtomicInteger` work internally?

It uses a CAS (Compare-And-Swap) retry loop backed by low-level JVM and CPU atomic instructions.

---

### Q3. What happens if CAS fails?

It means another thread modified the value first.

The thread reads the latest value and retries.

---

### Q4. Does CAS use locks?

No.

CAS is a lock-free algorithm.

---

### Q5. What is the ABA problem?

A value changes from A → B → A.

CAS only compares the current value, so it cannot detect the intermediate change unless versioning (e.g., `AtomicStampedReference`) is used.

---

### Q6. When should you use `AtomicInteger` instead of `synchronized`?

Use `AtomicInteger` when:

* You're updating **a single independent variable**
* Operations can be expressed using atomic methods (`incrementAndGet()`, `compareAndSet()`, etc.)
* You want high throughput without lock contention

Use `synchronized` (or `Lock`) when:

* Multiple variables must be updated together
* You need larger critical sections
* Complex business logic must execute atomically

---

# 21. Interview Boundary ⭐⭐⭐⭐⭐

For a **7+ years Java Developer**, you should confidently explain:

* What an atomic operation is.
* Why `count++` is not atomic.
* How `AtomicInteger` works internally.
* CAS (Compare-And-Swap) algorithm.
* Why CAS is lock-free.
* CAS retry mechanism.
* CAS vs `synchronized`.
* ABA problem and `AtomicStampedReference`.
* Appropriate use cases for atomic classes.

---

## Important Interview Tip

A very common senior interview question is:

> **"If `AtomicInteger` is faster than `synchronized`, why doesn't Java use it everywhere?"**

**Expected answer:**

`AtomicInteger` only guarantees atomicity for **one variable**. It cannot atomically coordinate updates across multiple variables or protect an entire block of business logic. When multiple operations must succeed as a single unit (for example, debiting one account and crediting another), locks or transactional mechanisms are still required.

---

## Next Chapter Recommendation

The natural next topic is:

# **Locks Framework (`Lock`, `ReentrantLock`, `ReadWriteLock`, `StampedLock`) ⭐⭐⭐⭐⭐**

This is the next level after `synchronized`. Senior interviews frequently ask:

* Why use `ReentrantLock` if `synchronized` already exists?
* Fair vs non-fair locks
* `tryLock()`
* `lockInterruptibly()`
* `Condition`
* `ReadWriteLock`
* `StampedLock` and optimistic locking

This chapter builds directly on everything we've covered so far.
