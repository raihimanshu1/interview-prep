# Chapter 3 — Creating Threads in Java ⭐⭐⭐⭐⭐

> **This is one of the most frequently asked Java interview topics.**
>
> Every interviewer eventually asks:
>
> * How do you create a thread?
> * Thread vs Runnable?
> * Callable vs Runnable?
> * Future?
> * Why is extending Thread considered bad practice?
> * What actually happens when `start()` is called?
> * Difference between `start()` and `run()`?

---

# 1. Why This Concept Exists

Imagine you're writing a Spring Boot application.

A user uploads a 2 GB file.

While the upload is processing, another user wants to browse products.

Should the second user wait?

No.

We need another execution path.

That execution path is called a **Thread**.

The question becomes:

> **How do we create one?**

---

# 2. Four Ways to Create Threads

Java has evolved over time.

```text
Thread Class          (Old)

↓

Runnable              (Preferred)

↓

Callable + Future

↓

ExecutorService (Modern Production Standard)
```

Although people say "4 ways", in production today we almost never manually create threads.

We'll understand all of them because interviewers expect it.

---

# 3. Method 1 — Extending Thread Class

The oldest approach.

```java
class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}

public class Main {

    public static void main(String[] args) {

        MyThread thread = new MyThread();

        thread.start();
    }
}
```

Output

```text
Running in: Thread-0
```

---

## What Happens Internally?

```text
new MyThread()

↓

Thread Object Created

↓

start()

↓

JVM asks OS to create Native Thread

↓

OS schedules Thread

↓

run() executes
```

Notice

`run()` is **never called directly by us**.

It is invoked by the JVM after the thread starts.

---

# 4. Why Isn't Extending Thread Preferred?

Suppose:

```java
class OrderService extends Thread
```

Now imagine later:

```java
class OrderService extends BaseService
```

Problem.

Java supports **only one class inheritance**.

You cannot extend both.

```text
Thread

        X

BaseService
```

This is why extending `Thread` is considered poor design.

Another issue:

The class now represents both:

* Business Logic
* Thread Management

This violates the **Single Responsibility Principle (SRP)**.

---

# 5. Method 2 — Implementing Runnable ⭐⭐⭐⭐⭐

Instead of extending Thread, separate the **task** from the **thread**.

```java
class OrderTask implements Runnable {

    @Override
    public void run() {
        System.out.println(
            "Processing order on " +
            Thread.currentThread().getName()
        );
    }
}

public class Main {

    public static void main(String[] args) {

        Runnable task = new OrderTask();

        Thread thread = new Thread(task);

        thread.start();
    }
}
```

Output

```text
Processing order on Thread-0
```

---

# 6. Why Runnable is Better

Notice the separation.

```text
Runnable

↓

What to execute
```

```text
Thread

↓

Who executes it
```

Think of it like this:

```text
Chef

↓

Knows how to cook
```

```text
Kitchen

↓

Provides the environment
```

The chef is not the kitchen.

Similarly,

The task is not the thread.

---

# 7. Thread vs Runnable

| Thread                      | Runnable                 |
| --------------------------- | ------------------------ |
| Represents execution thread | Represents a task        |
| Extends Thread              | Implements interface     |
| Cannot extend another class | Can extend another class |
| Less flexible               | More flexible            |
| Rarely used in production   | Standard approach        |

---

# 8. Method 3 — Callable ⭐⭐⭐⭐⭐

Runnable has one limitation.

It cannot:

* Return a value
* Throw checked exceptions

Suppose:

```java
int total = calculateInvoice();
```

We want the background thread to return the result.

Runnable cannot.

Enter **Callable**.

```java
Callable<Integer> task = () -> {

    Thread.sleep(1000);

    return 500;
};
```

Unlike Runnable:

```java
public interface Callable<V> {

    V call() throws Exception;
}
```

Notice:

* Returns value
* Can throw checked exceptions

---

# 9. But How Do We Get the Result?

Using **Future**.

```java
ExecutorService executor =
        Executors.newSingleThreadExecutor();

Future<Integer> future =
        executor.submit(task);

Integer value = future.get();

System.out.println(value);

executor.shutdown();
```

Output

```text
500
```

---

# 10. What Happens Internally?

```text
submit()

↓

Executor Queue

↓

Worker Thread

↓

call()

↓

Result Stored

↓

Future

↓

future.get()
```

The thread executes independently.

Future acts like a placeholder for the result.

---

# 11. Runnable vs Callable

| Runnable              | Callable                   |
| --------------------- | -------------------------- |
| run()                 | call()                     |
| Returns nothing       | Returns value              |
| No checked exceptions | Checked exceptions allowed |
| submit(Runnable)      | submit(Callable)           |

---

# 12. Method 4 — ExecutorService ⭐⭐⭐⭐⭐

This is how almost every production Java application creates threads.

Instead of:

```java
new Thread(...)
```

We do:

```java
ExecutorService executor =
        Executors.newFixedThreadPool(5);

executor.submit(() ->
        System.out.println(
            Thread.currentThread().getName()
        ));

executor.shutdown();
```

Why?

Because creating threads repeatedly is expensive.

ExecutorService reuses threads from a pool.

We'll dedicate an entire chapter to it later.

---

# 13. Production Example

Suppose 500 users place orders simultaneously.

Bad:

```java
for (...) {

    new Thread(task).start();
}
```

Creates 500 native threads.

Memory increases.

Context switching increases.

Performance suffers.

Better:

```java
ExecutorService executor =
        Executors.newFixedThreadPool(20);

executor.submit(task);
```

20 worker threads process all requests efficiently.

---

# 14. Visual Flow

```text
Runnable

↓

Thread

↓

start()

↓

JVM

↓

Operating System

↓

Native Thread

↓

run()
```

Callable:

```text
Callable

↓

ExecutorService

↓

Worker Thread

↓

call()

↓

Future

↓

Result
```

---

# 15. Common Mistakes

### ❌ Calling `run()` directly

```java
thread.run();
```

This does **not** create a new thread.

It behaves like a normal method call on the current thread.

Always use:

```java
thread.start();
```

---

### ❌ Creating too many threads

```java
for (...) {

    new Thread(...).start();
}
```

Very poor practice.

Use a thread pool.

---

### ❌ Extending Thread unnecessarily

Prefer Runnable or Callable unless you have a very specific reason to subclass Thread.

---

# 16. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. How many ways are there to create a thread?

Traditionally:

* Extend `Thread`
* Implement `Runnable`
* Implement `Callable`
* Use `ExecutorService` (recommended in production)

---

### Q2. Why is Runnable preferred over Thread?

Because it separates the task from the execution mechanism, supports interface-based design, and avoids Java's single inheritance limitation.

---

### Q3. When would you use Callable instead of Runnable?

When the task needs to:

* Return a value.
* Throw checked exceptions.

---

### Q4. What is Future?

A `Future` represents the result of an asynchronous computation. It allows you to check completion, retrieve the result, or cancel the task.

---

### Q5. Why don't we use `new Thread()` in production?

Because thread creation is expensive. Production applications use thread pools (`ExecutorService`) to reuse threads, reduce resource consumption, and improve scalability.

---

### Q6. Which approach do Spring Boot applications use?

Spring Boot applications typically rely on **ExecutorService** or framework-managed thread pools (such as those used by embedded Tomcat, `@Async`, or `TaskExecutor`) rather than creating threads manually.

---

# 17. Interview Boundary ⭐⭐⭐⭐⭐

For a **7+ years Java developer**, you should know deeply:

* All thread creation mechanisms.
* Why `Runnable` is preferred over extending `Thread`.
* When to use `Callable`.
* How `Future` works.
* Why thread pools are preferred in production.
* The execution flow from `start()` to the native OS thread.

## Next Chapter (One of the Most Important)

We'll cover **`start()` vs `run()`**, including:

* What `start()` does internally.
* How the JVM creates a native thread.
* Why calling `run()` directly does **not** create a new thread.
* Thread scheduling and execution flow.
* Common interview traps around these methods.

This topic is asked in almost every Java multithreading interview.

# Chapter 4 — `start()` vs `run()` ⭐⭐⭐⭐⭐

> **This is one of the most frequently asked Java interview questions.**
>
> Many developers know that "start creates a new thread," but senior interviewers expect you to explain **what actually happens inside the JVM**.

---

# 1. Why This Concept Matters

Imagine you write:

```java
Thread thread = new Thread(() -> {
    System.out.println("Hello");
});
```

Now you have two options:

```java
thread.start();
```

or

```java
thread.run();
```

Both compile.

Both execute the same `run()` method.

**So why do they behave differently?**

Understanding this explains how Java creates threads.

---

# 2. First Understand What `run()` Is

When you create a thread:

```java
class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("Running...");
    }
}
```

Notice something.

`run()` is just a normal Java method.

Nothing special.

You can call it like any other method.

```java
MyThread t = new MyThread();

t.run();
```

This is perfectly valid Java.

---

# 3. What Happens When We Call `run()`?

Example

```java
public class Main {

    public static void main(String[] args) {

        Thread t = new Thread(() -> {
            System.out.println(
                Thread.currentThread().getName()
            );
        });

        t.run();

        System.out.println(
            Thread.currentThread().getName()
        );
    }
}
```

Output

```text
main

main
```

Why?

Because **no new thread was created**.

Execution simply entered the `run()` method like any normal method call.

---

## Execution Flow

```text
main()

↓

t.run()

↓

run()

↓

Return

↓

main()
```

Only one thread exists.

```
Main Thread
      |
      +---- run()
```

---

# 4. What Happens When We Call `start()`?

Now:

```java
Thread t = new Thread(() -> {

    System.out.println(
        Thread.currentThread().getName()
    );

});

t.start();

System.out.println(
    Thread.currentThread().getName()
);
```

Possible Output

```text
main

Thread-0
```

or

```text
Thread-0

main
```

Both are correct.

---

Why?

Because two threads now exist.

```
Main Thread

↓

start()

↓

JVM creates Native Thread

↓

OS Scheduler

↓

run()
```

Notice:

**You never call `run()`.**

The JVM does.

---

# 5. Internal Working of `start()`

This is the interview gold.

When you write

```java
thread.start();
```

the following happens.

```text
start()

↓

JVM verifies thread state

↓

Requests Operating System

↓

Create Native Thread

↓

Allocate Native Stack

↓

Register Thread

↓

OS Scheduler decides

↓

JVM invokes run()
```

Notice

`start()` **never executes your business logic directly**.

It only prepares a new thread.

Later,

the JVM calls

```java
run();
```

inside that newly created thread.

---

# 6. Why Doesn't `start()` Immediately Execute `run()`?

Because thread scheduling belongs to the Operating System.

Example

```java
thread.start();

System.out.println("Main");
```

The scheduler may choose

```
Main

Thread-0
```

or

```
Thread-0

Main
```

Java gives **no ordering guarantee** unless synchronization is used.

---

# 7. Visual Difference

## Calling `run()`

```text
Main Thread

|

run()

|

Business Logic

|

Return
```

Only one execution path.

---

## Calling `start()`

```text
Main Thread

|

start()

|

+-----------------------------+

|

OS creates Thread-1

|

run()

|

Business Logic
```

Two independent execution paths.

---

# 8. Real Example

```java
public class Demo {

    public static void main(String[] args) {

        Thread thread = new Thread(() -> {

            for (int i = 1; i <= 3; i++) {
                System.out.println(
                    "Child : " + i
                );
            }

        });

        thread.start();

        for (int i = 1; i <= 3; i++) {
            System.out.println(
                "Main : " + i
            );
        }

    }

}
```

Possible Output

```text
Main : 1

Child : 1

Main : 2

Child : 2

Main : 3

Child : 3
```

Or

```text
Child : 1

Child : 2

Main : 1

Main : 2

Child : 3

Main : 3
```

Every execution can be different.

The scheduler decides.

---

Now replace

```java
thread.start();
```

with

```java
thread.run();
```

Output

```text
Child : 1

Child : 2

Child : 3

Main : 1

Main : 2

Main : 3
```

Why?

Because everything executes sequentially inside the main thread.

---

# 9. Can We Call `start()` Twice?

Example

```java
Thread t = new Thread();

t.start();

t.start();
```

Output

```text
java.lang.IllegalThreadStateException
```

Why?

A Java thread can only be started once.

Thread lifecycle:

```text
NEW

↓

RUNNABLE

↓

RUNNING

↓

TERMINATED
```

Once it leaves **NEW**, it can never go back.

If you need another execution, create a new `Thread` object.

---

# 10. Common Mistakes

### ❌ Calling `run()` expecting multithreading

```java
thread.run();
```

Wrong.

This is just a normal method call.

---

### ❌ Calling `start()` twice

```java
thread.start();

thread.start();
```

Throws

```
IllegalThreadStateException
```

---

### ❌ Assuming execution order

```java
thread.start();

System.out.println("Done");
```

Never assume which line prints first.

The scheduler decides.

---

# 11. `start()` vs `run()`

| Feature                  | `start()` | `run()`                           |
| ------------------------ | --------- | --------------------------------- |
| Creates new thread       | ✅ Yes     | ❌ No                              |
| Calls `run()` internally | ✅ Yes     | ❌ N/A                             |
| Returns immediately      | ✅ Yes     | ❌ Executes synchronously          |
| OS Scheduler involved    | ✅ Yes     | ❌ No                              |
| Concurrent execution     | ✅ Yes     | ❌ No                              |
| Can be called twice      | ❌ No      | ✅ Yes (it's just a normal method) |

> **Note:** While `run()` can be called multiple times because it is an ordinary method, repeatedly invoking it does **not** restart or recreate the thread.

---

# 12. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. What is the difference between `start()` and `run()`?

`start()` creates a new thread by asking the JVM to create a native OS thread. The JVM later invokes `run()` on that new thread.

`run()` is simply a normal Java method. Calling it directly executes on the current thread and does not create a new thread.

---

### Q2. Who calls the `run()` method when we use `start()`?

The JVM calls `run()` after the operating system schedules the newly created native thread.

---

### Q3. Why can't we call `start()` twice?

A `Thread` instance represents one execution. After it transitions out of the **NEW** state, it cannot be started again. Calling `start()` a second time results in an `IllegalThreadStateException`.

---

### Q4. Is `run()` a special method?

No. It's an ordinary Java method. It becomes the entry point of a new thread **only when invoked by the JVM** after a successful `start()`.

---

### Q5. Why can the output order change every time?

Because thread scheduling is controlled by the operating system scheduler, and Java does not guarantee the execution order of independent threads.

---

# 13. Production Interview Scenario ⭐⭐⭐⭐⭐

**Interviewer:**

> I accidentally replaced `thread.start()` with `thread.run()`. The application still works. Why?

**Good Answer:**

The code still compiles because `run()` is a normal method. However, no new thread is created. All work executes on the calling thread, so the application loses concurrency and may become slower or block other work.

---

# 14. Interview Boundary

For a **7+ years Java developer**, you should know:

* Why `run()` is just a normal method.
* How `start()` creates a native thread.
* That the JVM—not your code—invokes `run()` after `start()`.
* Why `start()` cannot be called twice.
* Why output ordering is non-deterministic.
* The role of the operating system scheduler in thread execution.

## Next Chapter

We'll move to **Thread Lifecycle & Thread States**, where we'll cover:

* `NEW`
* `RUNNABLE`
* `BLOCKED`
* `WAITING`
* `TIMED_WAITING`
* `TERMINATED`

and understand exactly how a thread transitions between these states during execution. This topic is another core area of senior Java interviews.

Absolutely. I also want to improve one thing going forward.

Your handbook is becoming something you'll revise for years. So whenever a **diagram can explain something better than 2 paragraphs**, I'll include it. For JVM, Concurrency, Collections, Spring, Kafka, System Design, there will be **lots of diagrams**.

---

# Module 4 — Chapter 5

# Thread Lifecycle & Thread States ⭐⭐⭐⭐⭐

> **This is one of the favourite topics in Java interviews.**
>
> Many developers memorize the six thread states, but interviewers usually ask:
>
> **"How does a thread actually move from one state to another?"**

Understanding the transitions is far more important than memorizing the enum names.

---

# 1. Why This Concept Exists

Think about a human.

A person isn't always working.

Sometimes they're:

* Sleeping
* Waiting in a queue
* Eating
* Working
* Finished

A thread behaves similarly.

It doesn't spend its entire life executing CPU instructions.

Instead, it moves through different states depending on what it's doing.

---

# 2. Complete Thread Lifecycle

This diagram is worth remembering.

```text
                         new Thread()

                              │
                              ▼

                        +-------------+
                        |    NEW      |
                        +-------------+
                              │
                         start()
                              │
                              ▼
                    +-----------------+
                    |   RUNNABLE      |
                    +-----------------+
                     │      │       │
                     │      │       │
                     │      │       │
         waiting     │      │       │ sleeping
         for lock    │      │       │
                     ▼      ▼       ▼
              +---------+ +---------+ +---------------+
              |BLOCKED | |WAITING | |TIMED_WAITING  |
              +---------+ +---------+ +---------------+
                     │       │              │
                     └───────┴──────────────┘
                              │
                              ▼
                       +---------------+
                       |   RUNNABLE    |
                       +---------------+
                              │
                       run() completes
                              │
                              ▼
                      +----------------+
                      | TERMINATED     |
                      +----------------+
```

**This diagram alone answers nearly half the thread-state interview questions.**

---

# 3. Java Thread States

Java defines exactly **6 states**.

```java
public enum Thread.State {

    NEW,

    RUNNABLE,

    BLOCKED,

    WAITING,

    TIMED_WAITING,

    TERMINATED
}
```

Notice something important.

There is **no RUNNING state**.

Many books draw RUNNING.

Java's `Thread.State` enum does **not**.

This is a very common interview trick.

---

# 4. NEW State

A thread has been created.

But it hasn't started.

Example

```java
Thread thread = new Thread(() -> {
    System.out.println("Hello");
});
```

Current state

```java
System.out.println(thread.getState());
```

Output

```text
NEW
```

Diagram

```text
Thread Object

↓

Created

↓

Not Started Yet
```

No native OS thread exists yet.

The JVM has only created the Java object.

---

# 5. RUNNABLE State ⭐⭐⭐⭐⭐

This is the most misunderstood state.

After:

```java
thread.start();
```

people assume:

> Thread is running.

Not exactly.

Java says:

```text
RUNNABLE
```

RUNNABLE means

> **The thread is eligible to run.**

The OS scheduler now decides:

* Execute immediately
* Execute after 2 ms
* Execute after another thread

Diagram

```text
               CPU Scheduler

                   │

      ----------------------------

      │            │            │

   Thread A     Thread B     Thread C

      │

      Eligible to Run

      │

   RUNNABLE
```

Important:

RUNNABLE includes

* Actually running on CPU
* Waiting for CPU time

Java combines both.

---

# Interview Question ⭐⭐⭐⭐⭐

### Why doesn't Java have a RUNNING state?

Because Java abstracts away OS scheduling.

The JVM only knows the thread is **eligible to execute**.

Whether it's actually executing depends on the operating system scheduler.

Therefore Java exposes only **RUNNABLE**.

---

# 6. BLOCKED State

Suppose:

```java
synchronized(lock) {

}
```

Another thread already owns the lock.

Current thread cannot continue.

Diagram

```text
Thread A

Holding Lock

      │

      ▼

+----------------+

| synchronized   |

+----------------+


Thread B

      │

Needs Same Lock

      ▼

BLOCKED
```

The thread is waiting only for one thing:

> The monitor lock.

As soon as the lock is released,

it becomes RUNNABLE again.

---

# Example

```java
Object lock = new Object();

Thread t1 = new Thread(() -> {

    synchronized (lock) {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

});

Thread t2 = new Thread(() -> {

    synchronized (lock) {

        System.out.println("Acquired Lock");

    }

});
```

While `t1` owns the monitor,

`t2` enters

```text
BLOCKED
```

---

# 7. WAITING State

WAITING means

> The thread will wait forever until another thread wakes it.

Examples

```java
object.wait();

thread.join();

LockSupport.park();
```

Diagram

```text
Thread A

↓

wait()

↓

WAITING

↓

notify()

↓

RUNNABLE
```

Notice

No timeout exists.

The thread may wait forever.

---

# 8. TIMED_WAITING

Very similar.

Difference:

There is a timeout.

Examples

```java
Thread.sleep(5000);

object.wait(3000);

thread.join(2000);
```

Diagram

```text
sleep(5 sec)

↓

TIMED_WAITING

↓

5 sec completed

↓

RUNNABLE
```

The JVM wakes it automatically after the timeout expires.

---

# 9. TERMINATED

The thread has finished execution.

```java
Thread t = new Thread(() -> {

    System.out.println("Done");

});

t.start();
```

After

```text
run()

↓

completed
```

State

```text
TERMINATED
```

Diagram

```text
run()

↓

Return

↓

Thread Dies

↓

TERMINATED
```

Cannot restart.

---

# 10. Complete Example

```java
Thread thread = new Thread(() -> {

    try {

        Thread.sleep(2000);

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

});

System.out.println(thread.getState());

thread.start();

System.out.println(thread.getState());

Thread.sleep(100);

System.out.println(thread.getState());

thread.join();

System.out.println(thread.getState());
```

Possible Output

```text
NEW

RUNNABLE

TIMED_WAITING

TERMINATED
```

Notice that **RUNNABLE** may be printed even if the thread is actively executing because Java does not expose a separate RUNNING state.

---

# 11. State Transition Summary

```text
NEW
 │
 │ start()
 ▼
RUNNABLE
 │
 ├──────────────► BLOCKED
 │                   │
 │                   ▼
 │              RUNNABLE
 │
 ├──────────────► WAITING
 │                   │
 │              notify()/join ends
 │                   ▼
 │              RUNNABLE
 │
 ├──────────────► TIMED_WAITING
 │                   │
 │          timeout expires
 │                   ▼
 │              RUNNABLE
 │
 └──────────────► TERMINATED
```

This is one of the most useful diagrams for revision.

---

# 12. Common Mistakes

### ❌ Java has a RUNNING state

False.

Java exposes only six states.

---

### ❌ BLOCKED means sleeping

False.

BLOCKED means

Waiting for a monitor lock.

---

### ❌ WAITING and TIMED_WAITING are the same

No.

WAITING has no timeout.

TIMED_WAITING automatically resumes after the timeout.

---

### ❌ A TERMINATED thread can be restarted

Impossible.

Create a new `Thread` object.

---

# 13. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. How many thread states are there in Java?

Six:

* NEW
* RUNNABLE
* BLOCKED
* WAITING
* TIMED_WAITING
* TERMINATED

---

### Q2. Why doesn't Java have a RUNNING state?

Because the JVM does not expose the operating system's scheduling details. A thread that is ready to run and a thread currently executing are both represented as **RUNNABLE**.

---

### Q3. What's the difference between BLOCKED and WAITING?

| BLOCKED                                            | WAITING                                                                        |
| -------------------------------------------------- | ------------------------------------------------------------------------------ |
| Waiting to acquire a monitor lock (`synchronized`) | Waiting for another thread to perform an action (`wait()`, `join()`, `park()`) |
| Lock acquisition resumes it                        | Notification or completion resumes it                                          |

---

### Q4. Difference between WAITING and TIMED_WAITING?

`WAITING` has no timeout and can wait indefinitely.

`TIMED_WAITING` resumes automatically after the specified timeout if no earlier event wakes it.

---

### Q5. Can a TERMINATED thread become RUNNABLE again?

No. Once a thread has terminated, it cannot be restarted. A new `Thread` instance must be created.

---

# 14. Interview Boundary ⭐⭐⭐⭐⭐

For a **7+ years Java developer**, you should confidently explain:

* The lifecycle of a Java thread.
* All six thread states.
* Why Java has no RUNNING state.
* The differences between BLOCKED, WAITING, and TIMED_WAITING.
* How a thread transitions between states.
* Real APIs (`start()`, `sleep()`, `wait()`, `join()`, `notify()`, `synchronized`) that cause those transitions.

---

## Next Chapter (One of the Biggest in Concurrency)

We'll cover **Thread Scheduling & Context Switching**, including:

* How the OS scheduler chooses the next thread.
* Time slicing.
* CPU cores.
* Preemptive scheduling.
* Context switching internals.
* Why context switching is expensive.
* Thread priorities (and why you should almost never rely on them).
* How all of this affects application performance.
