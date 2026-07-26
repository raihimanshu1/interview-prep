I completely agree. **This is actually where most candidates fail.**

Almost everyone can **define** a deadlock.

Very few people can **look at code** and immediately say:

* "This will deadlock."
* "This is safe."
* "This can deadlock only under certain timing."
* "This is not a deadlock, it's just blocking."

A senior (7+ years) interview is usually code-based.

So let's do a **Deadlock Masterclass** with interview-style questions.

---

# Example 1 — Classic Deadlock ⭐⭐⭐⭐⭐

```java
public class DeadLockExample {

    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {

            synchronized (lock1) {

                System.out.println("T1 acquired lock1");

                sleep();

                synchronized (lock2) {

                    System.out.println("T1 acquired lock2");

                }
            }

        });

        Thread t2 = new Thread(() -> {

            synchronized (lock2) {

                System.out.println("T2 acquired lock2");

                sleep();

                synchronized (lock1) {

                    System.out.println("T2 acquired lock1");

                }

            }

        });

        t1.start();
        t2.start();
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

## Execution Timeline

```
Time →

Thread-1

lock1 acquired
↓

sleep()

↓

tries lock2
(waiting)

---------------------------------

Thread-2

lock2 acquired

↓

sleep()

↓

tries lock1
(waiting)
```

Current state

```
Thread1
holds lock1
waiting lock2

↓

Thread2
holds lock2
waiting lock1
```

Nobody can continue.

Deadlock.

---

## Interview Question

**Q: Which line causes the deadlock?**

Wrong answer:

> synchronized(lock2)

Correct answer:

> **No single line causes the deadlock.**
>
> The problem is the **combination** of:
>
> ```java
> Thread1 : lock1 → lock2
> Thread2 : lock2 → lock1
> ```
>
> The inconsistent lock ordering creates a circular wait.

Senior interviewers love this answer.

---

# Example 2 — Same Locks, No Deadlock ⭐⭐⭐⭐⭐

```java
Thread t1 = new Thread(() -> {

    synchronized (lock1) {

        synchronized (lock2) {

        }

    }

});

Thread t2 = new Thread(() -> {

    synchronized (lock1) {

        synchronized (lock2) {

        }

    }

});
```

---

### Can this deadlock?

NO.

---

### Timeline

```
T1

lock1

↓

lock2

↓

release

---------------------

T2

wait lock1

↓

lock1

↓

lock2
```

Only blocking.

No deadlock.

---

## Why?

Every thread follows

```
lock1

↓

lock2
```

There is no circular dependency.

---

# Example 3 — Three Thread Deadlock ⭐⭐⭐⭐⭐

```java
Object A = new Object();
Object B = new Object();
Object C = new Object();
```

Thread 1

```java
synchronized (A) {

    synchronized (B) {

    }

}
```

Thread 2

```java
synchronized (B) {

    synchronized (C) {

    }

}
```

Thread 3

```java
synchronized (C) {

    synchronized (A) {

    }

}
```

---

Diagram

```
Thread1

A

↓

waiting B

----------------

Thread2

B

↓

waiting C

----------------

Thread3

C

↓

waiting A
```

This is a **3-way circular deadlock**.

---

Interview Question

How many threads are required for deadlock?

Wrong

```
Only 2
```

Correct

```
Minimum is 2.

Can be any number.
```

---

# Example 4 — Looks Dangerous but Safe ⭐⭐⭐⭐⭐

```java
synchronized (lock1) {

    System.out.println("A");

}

synchronized (lock2) {

    System.out.println("B");

}
```

Deadlock?

NO.

---

Reason

First lock released.

Then second lock acquired.

Never holding both together.

---

Timeline

```
Acquire lock1

↓

Release lock1

↓

Acquire lock2
```

Impossible to deadlock.

---

# Example 5 — Recursive synchronized

```java
class Test {

    synchronized void method1() {

        method2();

    }

    synchronized void method2() {

    }

}
```

Deadlock?

NO.

---

Why?

Java monitors are

```
Reentrant
```

Same thread already owns the monitor.

Can enter again.

---

Interview Question

What if synchronized wasn't reentrant?

Then

```
method1()

↓

method2()

↓

same lock

↓

deadlock
```

That's exactly why Java made monitors reentrant.

---

# Example 6 — Different Objects ⭐⭐⭐⭐☆

```java
Object a = new Object();
Object b = new Object();

Thread1

synchronized(a){

}

Thread2

synchronized(b){

}
```

Deadlock?

No.

Different locks.

No dependency.

---

# Example 7 — Nested Lock in Loop ⭐⭐⭐⭐⭐

```java
while (true) {

    synchronized (lock1) {

        synchronized (lock2) {

        }

    }

}
```

Another thread

```java
while (true) {

    synchronized (lock2) {

        synchronized (lock1) {

        }

    }

}
```

Deadlock?

YES.

Actually

Eventually.

---

Reason

Eventually scheduling becomes

```
Thread1

lock1

↓

Thread2

lock2

↓

Thread1 waits lock2

↓

Thread2 waits lock1
```

Infinite loop increases probability.

---

# Example 8 — ReentrantLock ⭐⭐⭐⭐⭐

```java
Lock lock1 = new ReentrantLock();
Lock lock2 = new ReentrantLock();

Thread1

lock1.lock();

lock2.lock();

Thread2

lock2.lock();

lock1.lock();
```

Deadlock?

YES.

---

Interview Trap

Many candidates think

```
ReentrantLock

↓

No Deadlock
```

Wrong.

Deadlock still happens.

---

Only this helps

```java
if(lock.tryLock(2, TimeUnit.SECONDS))
```

Because thread eventually gives up.

---

# Example 9 — tryLock()

```java
if(lock1.tryLock()){

    try{

        if(lock2.tryLock()){

        }

    } finally{

        lock1.unlock();

    }

}
```

Deadlock?

Usually No.

---

Reason

Thread doesn't wait forever.

If second lock unavailable,

it releases first lock.

---

# Example 10 — Bank Transfer ⭐⭐⭐⭐⭐

Very famous interview.

```java
transfer(Account from,
         Account to)
```

Implementation

```java
synchronized(from){

    synchronized(to){

        ...
    }

}
```

Two users

```
Transfer

A

↓

B
```

Another request

```
Transfer

B

↓

A
```

Deadlock?

YES.

---

Production Solution

Always lock

```
Lower Account ID

↓

Higher Account ID
```

Example

Instead of

```
From

↓

To
```

Use

```java
Account first =
    from.id < to.id ? from : to;

Account second =
    from.id < to.id ? to : from;
```

Then

```
Always

small ID

↓

large ID
```

Deadlock eliminated.

---

# Example 11 — Can synchronized(this) Deadlock?

```java
class A {

    synchronized void m1(B b){

        b.last();

    }

}

class B {

    synchronized void m2(A a){

        a.last();

    }

}
```

Thread1

```
A.m1(B)
```

Thread2

```
B.m2(A)
```

Deadlock?

YES.

Classic textbook example.

---

# Example 12 — Is This Deadlock?

```java
Thread1

lock.lock();

Thread.sleep(10000);

unlock();
```

Thread2

```
waiting
```

Deadlock?

NO.

Just blocking.

Eventually

```
Thread1

↓

unlock

↓

Thread2 proceeds
```

Deadlock means

```
Forever
```

Blocking means

```
Temporary waiting
```

---

# Interview Cheat Sheet

| Scenario                                    | Deadlock? | Why                             |
| ------------------------------------------- | --------- | ------------------------------- |
| T1: A→B, T2: B→A                            | ✅         | Circular wait                   |
| Both threads A→B                            | ❌         | Same lock order                 |
| Single lock only                            | ❌         | No circular dependency          |
| Recursive synchronized                      | ❌         | Reentrant monitor               |
| Different lock objects                      | ❌         | No shared resource              |
| ReentrantLock A→B / B→A                     | ✅         | Same deadlock risk              |
| tryLock() with timeout                      | Usually ❌ | Threads back off                |
| Bank Transfer A→B / B→A                     | ✅         | Opposite lock ordering          |
| Holding one lock then releasing before next | ❌         | Never holds both simultaneously |
| One thread sleeping while holding a lock    | ❌         | Blocking, not deadlock          |

---

# ⭐ Interview Brain Teaser

If I give you any deadlock code, always ask these **four questions**:

```
1. How many locks exist?

↓

2. Which thread owns each lock?

↓

3. Which lock is each thread waiting for?

↓

4. Is there a circular dependency?
```

If the answer to **Question 4** is **Yes**, you've found a deadlock.

---

## My recommendation

Before moving to **Virtual Threads**, we should spend one more session on **15–20 tricky concurrency interview code snippets** (deadlock, race condition, volatile, synchronized, CAS, ThreadLocal, ExecutorService, etc.). This is the closest simulation to what you'll face in Oracle, JPMorgan, Goldman Sachs, and similar senior Java interviews, and it will strengthen your ability to reason about concurrent code under pressure.
