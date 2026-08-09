Excellent. This is arguably the **most important modern Java concurrency topic**. If you're interviewing for **Spring Boot, Microservices, or Backend Developer (7+ years)**, you should expect multiple questions on `CompletableFuture`.

Most candidates know `Future`. Senior engineers are expected to know **why `Future` was insufficient**, how `CompletableFuture` solves its limitations, and how to build asynchronous pipelines.

---

# Module 4 — Chapter 16

# CompletableFuture & Asynchronous Programming ⭐⭐⭐⭐⭐

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> One of the hottest Java interview topics for senior backend developers.
>
> Interviewers expect you to explain:
>
> * Why `Future` is limited
> * What `CompletableFuture` is
> * Async vs Non-blocking
> * Chaining (`thenApply`, `thenCompose`)
> * Combining multiple tasks
> * Exception handling
> * Thread pool usage
> * Real production examples

---

# 1. Why Do We Need CompletableFuture?

Suppose a service needs data from three different microservices.

```
User Service
Order Service
Payment Service
```

Naive approach

```java
User user = userService.getUser(id);

Order order = orderService.getOrders(id);

Payment payment = paymentService.getPayment(id);
```

Timeline

```
User Service      2 sec

↓

Order Service     2 sec

↓

Payment Service   2 sec

----------------------

Total = 6 sec
```

Every call waits for the previous one.

Very inefficient.

---

# 2. Parallel Execution

Instead

```
User Service      2 sec

Order Service     2 sec

Payment Service   2 sec

(All together)
```

Timeline

```
2 sec

↓

All Completed
```

Total

```
≈ 2 sec
```

This is why asynchronous programming exists.

---

# 3. Future (Java 5)

Before Java 8

```java
ExecutorService executor =
        Executors.newFixedThreadPool(3);

Future<Integer> future =
        executor.submit(() -> {

            Thread.sleep(2000);

            return 100;

        });

Integer value = future.get();
```

Works.

But has serious limitations.

---

# 4. Problems with Future ⭐⭐⭐⭐⭐

Suppose

```
Task A

↓

Task B

↓

Task C
```

Can Future chain them?

No.

---

Future also cannot

✔ Combine multiple tasks

✔ Handle exceptions elegantly

✔ Trigger callbacks automatically

✔ Build async pipelines

It is simply

```
Submit

↓

Wait

↓

Get Result
```

---

# 5. CompletableFuture

Java 8 introduced

```java
CompletableFuture
```

Think of it as

```
Future

+

Callbacks

+

Task Chaining

+

Composition

+

Exception Handling
```

---

# 6. Creating CompletableFuture

### supplyAsync()

Returns a value.

```java
CompletableFuture<String> future =
    CompletableFuture.supplyAsync(() -> {

        return "Java";

    });
```

---

### runAsync()

No return value.

```java
CompletableFuture<Void> future =
    CompletableFuture.runAsync(() -> {

        System.out.println("Running");

    });
```

---

Difference

| Method        | Returns |
| ------------- | ------- |
| runAsync()    | Void    |
| supplyAsync() | Value   |

---

# 7. Execution Flow

```
Task Submitted

↓

Worker Thread

↓

Executes

↓

Completes

↓

Future Completed
```

Unlike

```
future.get()
```

You don't always have to block.

---

# 8. thenApply() ⭐⭐⭐⭐⭐

Transforms the result.

Example

```java
CompletableFuture<String> future =

CompletableFuture
        .supplyAsync(() -> "Java")

        .thenApply(str -> str.toUpperCase());

System.out.println(future.join());
```

Output

```
JAVA
```

Diagram

```
"Java"

↓

thenApply()

↓

"JAVA"
```

Think of

```
Stream.map()
```

Very similar.

---

# 9. thenAccept()

Consumes result.

No return value.

```java
CompletableFuture
        .supplyAsync(() -> "Java")

        .thenAccept(System.out::println);
```

Output

```
Java
```

---

# 10. thenRun()

Runs another task.

Ignores previous result.

```java
CompletableFuture
        .runAsync(() -> System.out.println("Task"))

        .thenRun(() -> System.out.println("Completed"));
```

Output

```
Task

Completed
```

---

# 11. thenCompose() ⭐⭐⭐⭐⭐

Very common interview question.

Suppose

First service returns User.

Second service needs User.

```
User

↓

Orders
```

Example

```java
CompletableFuture<User> future =
        getUser(id);

CompletableFuture<List<Order>> orders =

future.thenCompose(user ->

        getOrders(user));
```

Diagram

```
Future<User>

↓

User

↓

Future<Order>
```

Flattened into

```
Future<Order>
```

---

### Analogy

Exactly like

```
flatMap()
```

in Streams.

---

# 12. thenApply() vs thenCompose() ⭐⭐⭐⭐⭐

Suppose

Method returns

```java
CompletableFuture<Order>
```

Using

```java
thenApply()
```

Result becomes

```
Future<Future<Order>>
```

Nested future.

Bad.

---

Using

```java
thenCompose()
```

Result

```
Future<Order>
```

Flattened.

---

Comparison

| thenApply             | thenCompose               |
| --------------------- | ------------------------- |
| map()                 | flatMap()                 |
| Simple transformation | Async transformation      |
| Returns object        | Returns CompletableFuture |

---

# 13. thenCombine() ⭐⭐⭐⭐⭐

Suppose

```
User Service

AND

Order Service
```

Run independently.

Later combine.

```java
CompletableFuture<User> user =
        getUser();

CompletableFuture<Order> order =
        getOrder();

user.thenCombine(order,

(u,o)->

new Response(u,o));
```

Diagram

```
Future A

↓

Result A

          +

Result B

↓

Combined Result
```

---

# 14. allOf()

Suppose

Need

```
Service A

Service B

Service C
```

All must complete.

```java
CompletableFuture.allOf(

future1,

future2,

future3

).join();
```

Diagram

```
A

B

C

↓

All Finished

↓

Continue
```

---

# 15. anyOf()

Need first available result.

```java
CompletableFuture.anyOf(

future1,

future2,

future3

);
```

Diagram

```
A

B

C

↓

First Completes

↓

Continue
```

Very useful for fallback services.

---

# 16. Exception Handling ⭐⭐⭐⭐⭐

### exceptionally()

```java
CompletableFuture

.supplyAsync(() -> {

    throw new RuntimeException();

})

.exceptionally(ex -> {

    return "Default";

});
```

Output

```
Default
```

---

### handle()

Handles

* Success
* Failure

```java
future.handle((result, ex) -> {

    if (ex != null)

        return "Error";

    return result;

});
```

---

### whenComplete()

Only observes.

Cannot modify result.

Useful for

* Logging
* Metrics
* Auditing

---

# 17. Thread Pool Usage ⭐⭐⭐⭐⭐

Default

```java
CompletableFuture.supplyAsync(...)
```

uses

```
ForkJoinPool.commonPool()
```

Production applications often use a custom executor.

```java
ExecutorService executor =
        Executors.newFixedThreadPool(10);

CompletableFuture.supplyAsync(

task,

executor

);
```

This gives better control over resource usage.

---

# 18. Real Production Example ⭐⭐⭐⭐⭐

Suppose an API

```
GET /dashboard
```

Needs

* User Profile
* Orders
* Recommendations

Sequential

```
2 sec

+

2 sec

+

2 sec

=

6 sec
```

Parallel

```java
CompletableFuture<User> user = getUser();

CompletableFuture<List<Order>> orders = getOrders();

CompletableFuture<List<Product>> recommendations =
        getRecommendations();

CompletableFuture.allOf(
        user,
        orders,
        recommendations
).join();
```

Total

```
≈ 2 sec
```

Huge improvement.

---

# 19. Common Mistakes

### ❌ Calling `get()` immediately

```java
CompletableFuture<User> future =
        getUser();

future.get();
```

This blocks immediately and defeats the purpose of asynchronous execution.

Prefer chaining (`thenApply`, `thenCompose`, etc.) whenever possible.

---

### ❌ Using the common pool for everything

The shared `ForkJoinPool.commonPool()` is suitable for many CPU-bound tasks, but long-running or blocking I/O operations can starve it.

In production, use a dedicated executor for blocking work.

---

### ❌ Blocking inside async callbacks

```java
.thenApply(result -> {

    otherFuture.get();

})
```

This can reduce scalability and even lead to thread starvation.

Prefer composing asynchronous operations with `thenCompose()` instead of blocking.

---

# 20. Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. Why was CompletableFuture introduced?

Because `Future` could only represent a pending result. It could not express asynchronous workflows, combine multiple tasks, or support callback-style processing.

---

### Q2. Difference between `Future` and `CompletableFuture`?

| Future                     | CompletableFuture               |
| -------------------------- | ------------------------------- |
| Blocking API               | Supports non-blocking callbacks |
| No chaining                | Rich chaining API               |
| No composition             | Can combine multiple tasks      |
| Limited exception handling | Built-in exception handling     |

---

### Q3. Difference between `thenApply()` and `thenCompose()`?

`thenApply()` transforms a value.

`thenCompose()` chains another asynchronous computation and flattens the nested `CompletableFuture`.

---

### Q4. Difference between `allOf()` and `anyOf()`?

* `allOf()` waits for **all** tasks.
* `anyOf()` completes when **any one** task completes.

---

### Q5. Why avoid calling `get()` too early?

Because `get()` blocks the current thread. Excessive blocking eliminates the scalability benefits of asynchronous programming.

---

### Q6. Which thread pool does `CompletableFuture` use?

By default, asynchronous methods use the `ForkJoinPool.commonPool()`. You can also provide a custom `Executor` for better control.

---

# 21. Interview Boundary ⭐⭐⭐⭐⭐

For a **7+ years Java Developer**, you should confidently explain:

* Why asynchronous programming improves throughput.
* `Future` limitations.
* `CompletableFuture` architecture.
* `runAsync()` vs `supplyAsync()`.
* `thenApply()`, `thenAccept()`, `thenRun()`.
* `thenCompose()` vs `thenApply()`.
* `thenCombine()`.
* `allOf()` vs `anyOf()`.
* Exception handling methods.
* Default vs custom executors.
* Common production pitfalls.

---

# Next Chapter Recommendation

The next logical topic is:

# **Fork/Join Framework & Work Stealing** ⭐⭐⭐⭐⭐

We'll cover:

* Why the Fork/Join framework was introduced.
* `RecursiveTask` vs `RecursiveAction`.
* Fork, compute, and join.
* Work-stealing algorithm.
* Relationship with `ForkJoinPool.commonPool()`.
* How it differs from traditional thread pools.
* Where it is used internally (parallel streams, `CompletableFuture`, etc.).

This chapter completes the advanced Java concurrency toolkit and is a common topic in senior Java interviews.
