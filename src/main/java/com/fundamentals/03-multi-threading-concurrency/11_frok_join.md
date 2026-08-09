Excellent. This chapter explains **why Java introduced an entirely different concurrency framework** instead of relying only on `ExecutorService`.

Many candidates know that **Parallel Streams** and **CompletableFuture** use `ForkJoinPool`, but very few understand **how work stealing actually works**.

This is a favourite interview topic for **Amazon, Oracle, Microsoft, Uber, and other product companies**.

---

# Module 4 — Chapter 17

# Fork/Join Framework & Work Stealing ⭐⭐⭐⭐⭐

> **Interview Weight:** ⭐⭐⭐⭐☆
>
> Frequently asked for senior Java developers, especially when discussing parallel processing.
>
> Interviewers expect you to explain:
>
> * Why Fork/Join was introduced
> * Divide-and-Conquer algorithm
> * RecursiveTask vs RecursiveAction
> * Work-Stealing Algorithm
> * ForkJoinPool
> * Parallel Streams internals
> * Relationship with CompletableFuture

---

# 1. Why Was Fork/Join Framework Introduced?

Suppose we need to calculate the sum of an array containing **100 million numbers**.

Traditional approach

```java
long sum = 0;

for (int number : numbers) {
    sum += number;
}
```

Only **one CPU core** is used.

---

Modern CPUs have

```text
8 Cores

16 Threads
```

Can we use all of them?

Yes.

By splitting the work into smaller independent pieces.

---

# 2. Divide and Conquer

Instead of

```text
100 Million Numbers
```

Split

```text
100 Million

↓

50 Million

↓

25 Million

↓

12.5 Million

↓

...
```

Each CPU core processes one part.

Finally,

Combine all partial results.

Diagram

```text
                 100M

            /            \

        50M              50M

      /     \          /      \

   25M     25M     25M      25M

      ↓       ↓       ↓        ↓

    Worker1 Worker2 Worker3 Worker4

             ↓

        Combine Results
```

This is called

**Divide and Conquer**.

---

# 3. Fork/Join Framework

Java 7 introduced

```java
ForkJoinPool
```

Specially designed for

* Recursive algorithms
* Parallel computation
* Divide-and-conquer problems

Examples

* Sorting
* Searching
* Image processing
* Mathematical computations

---

# 4. ForkJoinPool

Unlike a normal thread pool,

every worker thread has its own queue.

Diagram

```text
             ForkJoinPool

    +---------+ +---------+ +---------+

    |Worker 1 | |Worker 2 | |Worker 3 |

    +---------+ +---------+ +---------+

       Queue       Queue       Queue
```

This design enables **Work Stealing**.

---

# 5. RecursiveTask vs RecursiveAction ⭐⭐⭐⭐⭐

Fork/Join provides two abstract classes.

---

## RecursiveTask

Returns a result.

Example

```java
class SumTask extends RecursiveTask<Long> {

}
```

Used for

* Sum
* Maximum
* Minimum
* Search

---

## RecursiveAction

No return value.

Example

```java
class ImageTask extends RecursiveAction {

}
```

Used for

* Image processing
* File copying
* Data transformation

---

Comparison

| RecursiveTask              | RecursiveAction           |
| -------------------------- | ------------------------- |
| Returns result             | No result                 |
| Extends `RecursiveTask<T>` | Extends `RecursiveAction` |

---

# 6. How Fork/Join Works ⭐⭐⭐⭐⭐

Suppose

```text
Array Size = 1,000,000
```

Threshold

```text
1000
```

Algorithm

```text
If size <= threshold

↓

Compute directly

Else

↓

Split

↓

Fork

↓

Fork

↓

Join
```

---

# 7. Example — Parallel Sum

```java
class SumTask extends RecursiveTask<Long> {

    private final int[] array;

    private final int start;
    private final int end;

    private static final int THRESHOLD = 1000;

    public SumTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {

        if (end - start <= THRESHOLD) {

            long sum = 0;

            for (int i = start; i < end; i++) {
                sum += array[i];
            }

            return sum;
        }

        int mid = (start + end) / 2;

        SumTask left =
                new SumTask(array, start, mid);

        SumTask right =
                new SumTask(array, mid, end);

        left.fork();

        long rightResult = right.compute();

        long leftResult = left.join();

        return leftResult + rightResult;
    }
}
```

---

# 8. Why `left.fork()` then `right.compute()`?

This is a common interview question.

Many beginners write:

```java
left.fork();
right.fork();

left.join();
right.join();
```

Better approach:

```java
left.fork();

right.compute();

left.join();
```

Why?

Because the current thread does useful work instead of becoming idle while waiting.

Diagram

```text
Current Thread

↓

Fork Left

↓

Compute Right

↓

Join Left
```

This improves CPU utilisation.

---

# 9. What is Work Stealing? ⭐⭐⭐⭐⭐

Suppose

Worker 1

finished all tasks.

Worker 2

still has many tasks.

Without work stealing

```text
Worker1

Idle

--------------

Worker2

Busy
```

Poor utilisation.

---

With Work Stealing

```text
Worker1

↓

Steals Task

↓

Worker2 Queue

↓

Executes
```

Now

Both workers remain busy.

---

# 10. Work-Stealing Diagram

Initially

```text
Worker1 Queue

Task A

Task B

Task C

-----------------

Worker2 Queue

Task D

-----------------

Worker3 Queue

Empty
```

Worker3

```text
Idle

↓

Steals

Task C

↓

Executes
```

CPU utilisation increases dramatically.

---

# 11. Why Own Queue Per Worker?

Traditional Thread Pool

```text
One Queue

↓

All Threads
```

Problem

Every thread competes for the same queue.

High contention.

---

ForkJoinPool

```text
Worker1 → Queue1

Worker2 → Queue2

Worker3 → Queue3
```

Less contention.

Better scalability.

---

# 12. Parallel Streams Internally ⭐⭐⭐⭐⭐

Example

```java
list.parallelStream()

    .map(...)

    .filter(...)

    .collect(...);
```

Question

Does Java create new threads?

No.

Internally

```text
Parallel Stream

↓

ForkJoinPool.commonPool()
```

---

# 13. CompletableFuture Relationship

By default

```java
CompletableFuture.supplyAsync(...)
```

also uses

```text
ForkJoinPool.commonPool()
```

Unless

you provide a custom executor.

---

# 14. ForkJoinPool vs ThreadPoolExecutor

| ForkJoinPool                 | ThreadPoolExecutor            |
| ---------------------------- | ----------------------------- |
| Divide-and-conquer           | General task execution        |
| Work stealing                | Shared work queue (typically) |
| Recursive tasks              | Independent tasks             |
| Optimised for CPU-bound work | General purpose               |
| Used by parallel streams     | Used by ExecutorService       |

---

# 15. Common Use Cases

Good

* Merge Sort
* Quick Sort
* Matrix multiplication
* Image rendering
* Recursive tree traversal
* Large mathematical computations

---

Poor

* Database calls
* REST API calls
* File downloads
* Blocking I/O

These block worker threads and reduce the effectiveness of work stealing.

---

# 16. Common Mistakes

### ❌ Using ForkJoinPool for Blocking I/O

Wrong

```java
ForkJoinPool.commonPool()

↓

Database Query

↓

Wait
```

Worker thread remains blocked.

Use a dedicated `ExecutorService` for blocking operations.

---

### ❌ Very Small Tasks

Threshold

```text
1
```

Creates thousands of tiny tasks.

Overhead exceeds useful work.

---

### ❌ Very Large Threshold

Threshold

```text
1,000,000
```

Almost no splitting.

Parallelism is lost.

Choosing an appropriate threshold is important.

---

# 17. Production Example

Suppose

You are processing

```text
10 GB Log File
```

Split

```text
10 GB

↓

2 GB

↓

500 MB

↓

100 MB
```

Workers process chunks independently.

Results combined at the end.

Ideal use case.

---

# 18. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. Why was Fork/Join introduced?

To efficiently execute divide-and-conquer algorithms by recursively splitting work across multiple CPU cores.

---

### Q2. Difference between RecursiveTask and RecursiveAction?

`RecursiveTask<T>` returns a result.

`RecursiveAction` performs work without returning a value.

---

### Q3. What is Work Stealing?

Each worker thread has its own deque (double-ended queue). If a worker becomes idle, it steals tasks from another worker's queue, improving CPU utilisation and reducing idle time.

---

### Q4. Why does each worker have its own queue?

It reduces contention compared to a single shared queue and makes work stealing possible.

---

### Q5. Why is ForkJoinPool good for CPU-bound tasks?

CPU-bound tasks can be divided into many independent computations. Workers stay busy executing computations instead of waiting for external resources.

---

### Q6. Does Parallel Stream create new threads?

No.

By default, it submits tasks to the shared `ForkJoinPool.commonPool()`.

---

### Q7. Does CompletableFuture always use ForkJoinPool?

Only when you use asynchronous methods like `supplyAsync()` or `runAsync()` **without** providing a custom `Executor`.

If you pass an executor:

```java
CompletableFuture.supplyAsync(task, executor);
```

then that executor is used instead.

---

# 19. Fork/Join vs ExecutorService Decision Table ⭐⭐⭐⭐⭐

| Scenario                   | Recommended Choice | Why                      |
| -------------------------- | ------------------ | ------------------------ |
| REST API calls             | `ExecutorService`  | Blocking I/O             |
| Database operations        | `ExecutorService`  | Blocking I/O             |
| File downloads             | `ExecutorService`  | Blocking I/O             |
| Parallel sorting           | `ForkJoinPool`     | CPU-bound recursive task |
| Image processing           | `ForkJoinPool`     | Divide-and-conquer       |
| Parallel stream operations | `ForkJoinPool`     | Used internally          |

---

# 20. Interview Boundary ⭐⭐⭐⭐⭐

For a **7+ years Java Developer**, you should confidently explain:

* Why Fork/Join was introduced.
* Divide-and-conquer processing.
* `ForkJoinPool` architecture.
* `RecursiveTask` vs `RecursiveAction`.
* `fork()`, `compute()`, and `join()`.
* Work-stealing algorithm.
* Why each worker has its own deque.
* Relationship with `parallelStream()`.
* Relationship with `CompletableFuture`.
* Why Fork/Join is best suited for CPU-bound tasks.

---

# Interview Questions Asked in Real Companies ⭐⭐⭐⭐⭐

### 1. Why do `parallelStream()` and `CompletableFuture` use `ForkJoinPool.commonPool()`?

**Expected Answer:**

Both frameworks are designed for lightweight, CPU-bound asynchronous tasks. The shared `ForkJoinPool.commonPool()` avoids creating new threads repeatedly and uses work stealing to maximise CPU utilisation. For blocking operations (database calls, HTTP requests), it's generally better to supply a dedicated `ExecutorService` to avoid starving the common pool.

---

### 2. Why does `left.fork(); right.compute(); left.join();` perform better than forking both tasks?

**Expected Answer:**

Forking both tasks can leave the current thread waiting while other workers execute the tasks. By computing one subtask directly, the current thread remains productive, reducing scheduling overhead and improving throughput. Only the forked task may be executed by another worker.

---

### 3. Can I use `ForkJoinPool` for database queries?

**Expected Answer:**

Generally, no. `ForkJoinPool` is optimised for CPU-bound tasks. Blocking operations like database queries or network calls tie up worker threads and reduce the effectiveness of work stealing. A dedicated `ExecutorService` with an appropriately sized thread pool is a better choice.

---

# Next Chapter Recommendation

The natural next topic is:

# **Concurrent Collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`, `BlockingQueue`, `ConcurrentLinkedQueue`) ⭐⭐⭐⭐⭐**

This is the perfect transition because you've already covered:

* Synchronization
* Locks
* Atomic classes
* Thread pools
* Thread communication

Now you'll learn **how Java's concurrent data structures achieve thread safety without forcing you to synchronize manually**, which is another major interview topic for senior Java developers.
