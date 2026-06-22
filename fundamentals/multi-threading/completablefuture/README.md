# CompletableFuture — Complete Deep Dive

## 1. Why This Concept Matters

CompletableFuture (Java 8+) is the foundation for asynchronous, non-blocking programming in Java. It extends Future with completion callbacks, chaining, combining, and error handling — all without blocking threads. In production, it powers reactive services, parallel API calls, async event processing, and microservice orchestration. Interviewers test this heavily — it's the modern replacement for raw threads and blocking futures.

Misunderstanding CompletableFuture causes:
- Blocking with `get()`/`join()` instead of composing
- Deadlocks on ForkJoinPool from blocking in callbacks
- Lost exceptions from missing `handle()`/`exceptionally()`
- Thread starvation from fork-join pool oversubscription

## 2. Basic Meaning

CompletableFuture is a Future that can be explicitly completed and supports attaching callbacks that execute asynchronously when the future completes.

**Key vocabulary:**
- **Completed stage**: `CompletableFuture.completedFuture(value)` — already done
- **`supplyAsync(Supplier)`**: starts async task returning a value
- **`runAsync(Runnable)`**: starts async task (no return)
- **`thenApply(Function)`**: transform result (like map)
- **`thenAccept(Consumer)`**: consume result (no return)
- **`thenRun(Runnable)`**: run after completion (no input/output)
- **`thenCompose(Function)`**: chain with another CompletableFuture (flatMap)
- **`thenCombine(CompletionStage, BiFunction)`**: combine two futures
- **`allOf(CompletableFuture...)`**: wait for all
- **`anyOf(CompletableFuture...)`**: first to complete
- **`exceptionally(Function)`**: recover from exception
- **`handle(BiFunction)`**: handle success or failure
- **`whenComplete(BiConsumer)`**: callback on complete (no transformation)

What it is NOT: Not a replacement for synchronized. Not for blocking operations. Not for CPU-intensive tasks on shared ForkJoinPool.

## 3. Real Code / Real Example

```java
import java.util.concurrent.*;
import java.util.*;

public class CompletableFutureDemo {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        // === BASIC SUPPLY ASYNC ===
        CompletableFuture<String> basic = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "Hello";
        }, executor);
        System.out.println("Basic result: " + basic.get()); // blocks — avoid in production

        // === CHAINING (thenApply) ===
        CompletableFuture.supplyAsync(() -> "Hello", executor)
                .thenApply(s -> s + " World")
                .thenApply(String::toUpperCase)
                .thenAccept(System.out::println); // "HELLO WORLD"

        // === FLAT MAP (thenCompose) ===
        CompletableFuture.supplyAsync(() -> getUser(), executor)
                .thenCompose(user -> fetchOrders(user)) // returns CompletableFuture
                .thenAccept(orders -> System.out.println("Orders: " + orders));

        // === COMBINE TWO FUTURES (thenCombine) ===
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10, executor);
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20, executor);
        future1.thenCombine(future2, Integer::sum)
                .thenAccept(sum -> System.out.println("Sum: " + sum));

        // === ALL OF (wait for multiple) ===
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A", executor);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B", executor);
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "C", executor);
        CompletableFuture<Void> allDone = CompletableFuture.allOf(f1, f2, f3);
        allDone.thenRun(() -> {
            try {
                System.out.println("All: " + f1.get() + f2.get() + f3.get()); // ABC
            } catch (Exception e) { Thread.currentThread().interrupt(); }
        });

        // === ANY OF ===
        CompletableFuture<String> any = CompletableFuture.anyOf(f1, f2, f3);
        any.thenAccept(result -> System.out.println("First done: " + result));

        // === ERROR HANDLING (exceptionally) ===
        CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) throw new RuntimeException("Failed!");
            return "Success";
        }, executor)
                .exceptionally(ex -> "Fallback: " + ex.getMessage())
                .thenAccept(System.out::println);

        // === HANDLE (success or failure) ===
        CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) throw new RuntimeException("Error!");
            return "OK";
        }, executor)
                .handle((result, ex) -> ex != null ? "Recovered: " + ex.getMessage() : result)
                .thenAccept(System.out::println);

        // === TIMEOUT (Java 9+) ===
        CompletableFuture<String> timeoutFuture = CompletableFuture.supplyAsync(() -> {
            sleep(2000);
            return "Late";
        }, executor);
        timeoutFuture
                .completeOnTimeout("Default", 1, TimeUnit.SECONDS)
                .thenAccept(r -> System.out.println("Timeout result: " + r));

        // === PARALLEL API CALLS ===
        List<String> urls = List.of("/api/a", "/api/b", "/api/c");
        List<CompletableFuture<String>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> fetchUrl(url), executor))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> futures.stream()
                        .map(CompletableFuture::join)
                        .forEach(result -> System.out.println("API result: " + result)));

        sleep(3000);
        executor.shutdown();
    }

    private static String getUser() { sleep(300); return "user-123"; }
    private static CompletableFuture<List<String>> fetchOrders(String user) {
        return CompletableFuture.supplyAsync(() -> List.of("order1", "order2"), executor);
    }
    private static String fetchUrl(String url) { sleep(200); return "Data from " + url; }
    private static void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } }
}
```

Expected output (simplified):
```
Basic result: Hello
HELLO WORLD
Orders: [order1, order2]
Sum: 30
All: ABC
First done: A
Fallback: Failed!
Recovered: Error!
Timeout result: Default
API result: Data from /api/a
API result: Data from /api/b
API result: Data from /api/c
```

## 4. What Happens Internally

**CompletableFuture structure:**
```java
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {
    volatile Object result; // null = not done, AltResult = exception, T = done
    volatile Completion stack; // Treiber stack of dependent stages
}
```

**Completion via CAS on `result`:**
- `result` is volatile Object. `complete(value)`: CAS from null to value.
- `completeExceptionally(ex)`: wraps in `AltResult`, CAS from null.

**Dependent stage (thenApply):**
```java
public <U> CompletableFuture<U> thenApply(Function<T, U> fn) {
    return uniApplyStage(null, fn); // null = default executor
}
```
- If current stage is already complete: runs fn immediately (in caller thread).
- If not: pushes new `UniApply` node onto stack. When current completes, it signals stack.

**Execution thread:**
- If called with executor: fn runs in executor thread
- If called without executor: `ForkJoinPool.commonPool()` (or caller thread if already complete)
- Java 9+ `thenApplyAsync`: always runs in executor

**Stack structure:**
Completion objects form a Treiber stack (lock-free LIFO). When a stage completes, it walks its stack and signals all dependent stages.

## 5. Tricky Interview Cases

**Case 1 — Blocking with get() on ForkJoinPool**
```java
CompletableFuture.supplyAsync(() -> {
    try { return CompletableFuture.supplyAsync(() -> "inner").get(); } // BLOCKS common pool!
    catch (Exception e) { return ""; }
});
```
Problem: Inner future's supplyAsync uses common pool. If outer already blocked on common pool thread → deadlock.
Fix: Use `thenCompose()` instead of nested `get()`.

**Case 2 — Lost exception**
```java
CompletableFuture.supplyAsync(() -> { throw new RuntimeException("fail"); });
// No .exceptionally(), no .handle(), no .get()
// Exception lost! CompletableFuture becomes "exceptionally completed" but nobody notices.
```
Fix: Always attach error handler or `.get()` at some point.

**Case 3 — Order of completion callbacks**
```java
CompletableFuture<String> cf = new CompletableFuture<>();
cf.thenApply(s -> s + " A");  // added first
cf.thenApply(s -> s + " B");  // added second
cf.complete("X");
// Result: "XB" — second callback runs first! (LIFO stack)
```
Output: "XB". Dependent stages run in reverse order of addition (Treiber stack is LIFO).
But if already completed before adding: first added, first executed.

**Case 4 — thenCompose vs thenApply with get()**
```java
// BAD: nested blocking
CompletableFuture.supplyAsync(() -> fetchUser())
        .thenApply(user -> fetchOrdersBlocking(user)); // blocks this thread

// GOOD: non-blocking composition
CompletableFuture.supplyAsync(() -> fetchUser())
        .thenCompose(user -> fetchOrdersAsync(user)); // returns CF
```

**Case 5 — allOf with heterogenous types**
```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 42);
CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);
// all.get() returns Void — need to extract individual results
String result1 = f1.join();
int result2 = f2.join();
```
Output: Must join individual futures. `allOf` returns `Void`.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `get()`/`join()` in callbacks | Blocks FJP thread, may deadlock | Use `thenCompose()` for composition |
| No error handler | Lost exceptions | Attach `exceptionally()` or `handle()` |
| CPU-intensive on common pool | Thread starvation | Supply custom `Executor` |
| Forgetting `allOf` returns Void | Can't access results directly | `join()` individual futures after allOf |
| Using `thenApply` with CF-returning function | Nested futures | Use `thenCompose()` instead |
| Not handling InterruptedException | Lost interrupt | `Thread.currentThread().interrupt()` |

## 7. Production Usage

**Microservice orchestration:**
```java
public CompletableFuture<OrderResponse> processOrder(OrderRequest req) {
    return CompletableFuture.supplyAsync(() -> validate(req), executor)
            .thenCompose(validated -> 
                CompletableFuture.supplyAsync(() -> paymentService.charge(validated), executor))
            .thenCompose(payment ->
                CompletableFuture.supplyAsync(() -> shippingService.ship(req), executor))
            .thenApply(shipment -> new OrderResponse(shipment.getId(), "SUCCESS"))
            .exceptionally(ex -> new OrderResponse(null, "FAILED: " + ex.getMessage()));
}
```

**Parallel API calls with timeout:**
```java
List<String> results = urls.stream()
    .map(url -> CompletableFuture.supplyAsync(() -> apiClient.call(url), executor)
            .completeOnTimeout("TIMEOUT", 2, TimeUnit.SECONDS))
    .map(CompletableFuture::join)
    .toList();
```

**Spring async support:**
```java
@Service
public class AsyncService {
    @Async
    public CompletableFuture<String> processAsync(String input) {
        String result = doWork(input);
        return CompletableFuture.completedFuture(result);
    }
}
```

## 8. Advanced Details

- **`completeOnTimeout()` (Java 9+)**: Returns default value if not completed in time.
- **`orTimeout()` (Java 9+)**: Throws TimeoutException if not completed in time.
- **`delayedExecutor()` (Java 9+)**: Executor that delays execution.
- **`CompletableFuture` vs `Future`**: Future requires `get()` to block. CompletableFuture supports callbacks without blocking.
- **`CompletableFuture` is not `Cancelable`**: `cancel(true)` marks as cancelled but doesn't interrupt the running thread.
- **`obtrudeValue()`**: Force-set result even if already completed (dangerous, use carefully).

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between CompletableFuture and Future?
A: Future requires blocking `get()` to retrieve result — caller thread blocks. CompletableFuture supports callbacks (`thenApply`, `thenAccept`, `thenCompose`) that execute asynchronously when the result is available — no blocking. CompletableFuture can be manually completed and composed.

### Intermediate
Q: What is the difference between `thenApply`, `thenAccept`, and `thenRun`?
A: `thenApply(Function)`: transforms result (returns new value). `thenAccept(Consumer)`: consumes result, returns nothing (CompletableFuture<Void>). `thenRun(Runnable)`: runs action after completion, ignores input/output.

### Senior
Q: In a Spring Boot microservice, you need to call 3 external APIs in PARALLEL, then combine their results. How would you implement this? What executor would you use?
A: Use `allOf` + custom executor:
```java
@Bean(name = "apiExecutor")
public Executor apiExecutor() {
    return new ThreadPoolExecutor(4, 8, 60, SECONDS, new ArrayBlockingQueue<>(100));
}

public CompletableFuture<AggregatedResponse> aggregate() {
    CompletableFuture<A> a = CompletableFuture.supplyAsync(() -> apiA.call(), apiExecutor);
    CompletableFuture<B> b = CompletableFuture.supplyAsync(() -> apiB.call(), apiExecutor);
    CompletableFuture<C> c = CompletableFuture.supplyAsync(() -> apiC.call(), apiExecutor);
    
    return CompletableFuture.allOf(a, b, c)
            .thenApply(v -> new AggregatedResponse(a.join(), b.join(), c.join()))
            .completeOnTimeout(fallback(), 5, SECONDS)
            .exceptionally(ex -> handleError(ex));
}
```

Custom executor prevents stealing ForkJoinPool threads.

### Tricky
Q: `thenApply` vs `thenCompose` — when to use each? What happens if you return a CompletableFuture from `thenApply`?
A: `thenApply(Function<T, R>)`: returns `CompletableFuture<R>`. Function returns a plain value. If the function returns a `CompletableFuture<R>`, `thenApply` wraps it: `CompletableFuture<CompletableFuture<R>>` — nested future.

`thenCompose(Function<T, CompletionStage<R>>)`: returns `CompletableFuture<R>`. Function returns a CompletionStage, and thenCompose flattens it. Use when the transformation itself is async.

## 10. Final 30-Second Answer

CompletableFuture = async result with callbacks. `supplyAsync(Supplier)` starts async task. `thenApply` (map), `thenAccept` (consume), `thenCompose` (flatMap). Combine: `thenCombine`, `allOf` (all), `anyOf` (first). Error: `exceptionally(recover)`, `handle(result, ex)`. Timeout: `completeOnTimeout`, `orTimeout`. **Avoid blocking with `get()` in callbacks** — use `thenCompose`. Custom Executor to avoid ForkJoinPool starvation. `handle()` always — lost exceptions are silent failures.