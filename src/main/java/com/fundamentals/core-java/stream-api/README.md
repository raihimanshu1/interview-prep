# Stream API — Complete Deep Dive

## 1. Why This Concept Matters

Stream API (Java 8+) revolutionized Java data processing by enabling declarative, functional-style operations on collections. It allows chaining filter-map-reduce pipelines, parallel execution, and lazy evaluation. In production, streams replace complex for-loops with concise, readable pipelines. Interviewers test this extensively — lambda expressions, intermediate vs terminal operations, parallel streams, collector mechanics.

Misunderstanding Stream API causes:
- Incorrect stream reuse (streams are single-use)
- Forgetting streams are lazy (no terminal op = no execution)
- Stateful lambda side effects (race conditions in parallel)
- Performance traps (boxing overhead, excessive intermediate ops)

## 2. Basic Meaning

A stream is a sequence of elements supporting sequential and parallel aggregate operations. Streams don't store data, they operate on a source (collection, array, generator) via a pipeline of operations.

**Key vocabulary:**
- **Stream pipeline**: source → zero/more intermediate ops → terminal op
- **Intermediate operation**: returns new stream (filter, map, sorted, distinct, limit, skip)
- **Terminal operation**: produces result or side effect (collect, forEach, reduce, count, anyMatch, findFirst)
- **Lazy evaluation**: intermediate ops run only when terminal op is invoked
- **`Stream<T>`**: object stream. **`IntStream`**, **`LongStream`**, **`DoubleStream`**: primitive streams
- **`Collector`**: mutable reduction (toList, toSet, groupingBy, partitioningBy)
- **Short-circuiting**: limit, findFirst, anyMatch — stop processing early

What it is NOT: Not a data structure. Not reusable (consumed after terminal op). Not a replacement for simple for-loops (overhead). Not thread-safe for stateful lambdas.

## 3. Real Code / Real Example

```java
import java.util.*;
import java.util.stream.*;

public class StreamAPIDemo {
    public static void main(String[] args) {
        // === BASIC PIPELINE ===
        List<String> names = List.of("Alice", "Bob", "Charlie", "David", "Eve");
        List<String> result = names.stream()
                .filter(name -> name.length() > 3)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Filtered & sorted: " + result);

        // === PRIMITIVE STREAMS ===
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int sum = Arrays.stream(numbers)
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .sum();
        System.out.println("Sum of squares of evens: " + sum);

        // === REDUCE ===
        List<Integer> values = List.of(1, 2, 3, 4, 5);
        Optional<Integer> product = values.stream().reduce((a, b) -> a * b);
        System.out.println("Product: " + product.orElse(0));

        // === GROUPING BY ===
        List<String> items = List.of("apple", "banana", "apricot", "blueberry", "avocado");
        Map<Character, List<String>> grouped = items.stream()
                .collect(Collectors.groupingBy(s -> s.charAt(0)));
        System.out.println("Grouped by first letter: " + grouped);

        // === PARTITIONING BY ===
        Map<Boolean, List<Integer>> partitioned = Stream.of(1,2,3,4,5,6)
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("Even: " + partitioned.get(true));
        System.out.println("Odd: " + partitioned.get(false));

        // === FLAT MAP ===
        List<List<String>> listOfLists = List.of(List.of("a","b"), List.of("c","d"));
        List<String> flat = listOfLists.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        System.out.println("Flat: " + flat);

        // === SHORT-CIRCUITING ===
        List<Integer> big = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
        Optional<Integer> firstEven = big.stream()
                .filter(n -> n % 2 == 0)
                .findFirst();
        System.out.println("First even: " + firstEven.orElse(-1)); // 2

        boolean hasLarge = big.stream().anyMatch(n -> n > 500);
        System.out.println("Has >500: " + hasLarge); // true

        // === PARALLEL STREAM ===
        long count = IntStream.rangeClosed(1, 10_000_000)
                .parallel()
                .filter(n -> n % 2 == 0)
                .count();
        System.out.println("Count of evens (parallel): " + count);

        // === CUSTOM COLLECTOR ===
        String joined = Stream.of("a", "b", "c")
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Joined: " + joined);

        // === GENERATE / ITERATE ===
        Stream.generate(() -> Math.random())
                .limit(5)
                .forEach(System.out::println);

        Stream.iterate(0, n -> n + 2)
                .limit(5)
                .forEach(n -> System.out.print(n + " ")); // 0 2 4 6 8
        System.out.println();
    }
}
```

Expected output:
```
Filtered & sorted: [ALICE, CHARLIE, DAVID]
Sum of squares of evens: 220
Product: 120
Grouped by first letter: {a=[apple, apricot, avocado], b=[banana, blueberry]}
Even: [2, 4, 6]
Odd: [1, 3, 5]
Flat: [a, b, c, d]
First even: 2
Has >500: true
Count of evens (parallel): 5000000
Joined: [a, b, c]
0 2 4 6 8
```

## 4. What Happens Internally

**Stream pipeline execution:**
1. Source → **Spliterator** (splittable iterator) provides elements
2. Intermediate ops wrap streams in **lazy** stages (ReferencePipeline)
3. Terminal op triggers traversal via **Sink chain** — each element passes through all stages

**Example: `filter().map().collect(toList())`**
```
Source → Spliterator → 
  stage0(filter): Sink{test each element, pass if matches}
  stage1(map): Sink{transform element}
  terminal(collect): Sink{accumulate into list}
```

Each terminal op creates a **Sink chain** from last to first:
```
collect() sink → wraps → map() sink → wraps → filter() sink → wraps → spliterator
```

**`filter` creates a `StatelessOp`** — no state needed between elements.
**`sorted` creates a `StatefulOp`** — buffers all elements, sorts, then emits.

**Parallel stream:**
- Splits source via `Spliterator.trySplit()`
- ForkJoinPool processes each partition
- For `collect()`: each partition accumulates → combiner merges results
- For `reduce()`: each partition reduces → combiner merges

**Collector.internal structure:**
```java
interface Collector<T, A, R> {
    Supplier<A> supplier();      // creates accumulator
    BiConsumer<A, T> accumulator(); // adds element
    BinaryOperator<A> combiner();   // merges accumulators
    Function<A, R> finisher();      // final transformation
    Set<Characteristics> characteristics();
}
```

## 5. Tricky Interview Cases

**Case 1 — Stream is single-use**
```java
Stream<String> stream = Stream.of("a", "b", "c");
stream.forEach(System.out::println);
stream.forEach(System.out::println); // IllegalStateException!
```
Output: First print works. Second throws `IllegalStateException: stream has already been operated upon or closed`.
Explanation: Stream is consumed after terminal operation.

**Case 2 — Lazy evaluation**
```java
Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
stream.peek(System.out::println) // prints nothing!
       .filter(n -> n > 2);
System.out.println("After filter (no terminal op)");
// Output: "After filter (no terminal op)" only
```
Output: No peek output. No terminal op = no execution.

**Case 3 — Stateful lambda in parallel**
```java
List<Integer> result = new ArrayList<>();
IntStream.range(0, 100).parallel()
        .forEach(i -> result.add(i)); // NON-THREAD-SAFE!
System.out.println(result.size()); // likely < 100
```
Output: Race condition on ArrayList.add(). Use `collect(Collectors.toList())` instead.

**Case 4 — `findFirst()` vs `findAny()` in parallel**
```java
Optional<Integer> first = IntStream.range(1, 100).parallel().filter(n -> n > 50).findFirst();
Optional<Integer> any = IntStream.range(1, 100).parallel().filter(n -> n > 50).findAny();
```
Output: `findFirst()` returns 51 (deterministic). `findAny()` returns some value > 50 (non-deterministic).
Explanation: `findFirst()` respects encounter order. `findAny()` optimizes for parallel.

**Case 5 — Boxing overhead**
```java
// BAD — boxed Integer for each element
int sum1 = IntStream.range(1, 1000).boxed().reduce(0, Integer::sum);
// GOOD — primitive IntStream
int sum2 = IntStream.range(1, 1000).sum();
```
Primitive streams avoid boxing (~10x faster for large datasets).

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Reusing a stream | `IllegalStateException` | Create new stream each time |
| No terminal operation | Nothing executes | Always add terminal op (collect, forEach, etc.) |
| Stateful lambda in parallel | Race condition | Use `collect()` (thread-safe mutable reduction) |
| Using `boxed()` unnecessarily | Generic boxed types | Use `IntStream`, `LongStream`, `DoubleStream` |
| `forEach` vs `collect` for mutation | Side effects | Prefer `collect()` for producing result |
| Calling `parallel()` after intermediate ops | Partial parallelization | Call `parallel()` early or start with `parallelStream()` |

## 7. Production Usage

**Batch processing with grouping:**
```java
Map<String, List<Transaction>> byCurrency = transactions.stream()
    .filter(t -> t.getAmount() > 100)
    .collect(Collectors.groupingBy(Transaction::getCurrency));
```

**Log aggregation:**
```java
Map<String, Long> errorCounts = logLines.stream()
    .filter(line -> line.contains("ERROR"))
    .collect(Collectors.groupingBy(
        line -> extractServiceName(line),
        Collectors.counting()
    ));
```

**Pagination with limit/skip:**
```java
public <T> List<T> getPage(Stream<T> stream, int page, int size) {
    return stream
        .skip((long) page * size)
        .limit(size)
        .collect(Collectors.toList());
}
```

**Spring JPA streaming:**
```java
@Query("SELECT u FROM User u")
Stream<User> streamAllUsers();

// Use in transaction:
try (Stream<User> stream = repo.streamAllUsers()) {
    stream.filter(u -> u.isActive())
          .map(User::getEmail)
          .forEach(emailService::sendNewsletter);
}
```

## 8. Advanced Details

- **`Spliterator` characteristics**: `ORDERED`, `DISTINCT`, `SORTED`, `SIZED`, `NONNULL`, `IMMUTABLE`, `CONCURRENT`, `SUBSIZED`. Parallel split behavior depends on these.
- **`Stream.of()` vs `Arrays.stream()`**: `Stream.of(arr)` treats array as single element (unless varargs). `Arrays.stream(arr)` correctly streams array elements.
- **`Collectors.toUnmodifiableList()`** (Java 10+): Returns immutable list.
- **`teeing()` collector** (Java 12+): Collect to two downstream collectors simultaneously.
- **`takeWhile()` / `dropWhile()`** (Java 9+): Short-circuiting intermediate ops.
- **`Stream.iterate(seed, hasNext, next)`** (Java 9+): Finite iterate with predicate.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between intermediate and terminal operations on a Stream?
A: Intermediate operations (filter, map, sorted) return a new Stream and are lazy — they don't execute until a terminal operation is invoked. Terminal operations (collect, forEach, reduce, count) produce a result or side effect and consume the stream. After a terminal operation, the stream cannot be reused.

### Intermediate
Q: How does `flatMap` work? Give an example where you'd use it over `map`.
A: `flatMap` maps each element to a Stream, then flattens all those streams into a single stream. Use when each element produces multiple output elements (1-to-many). Example: splitting sentences into words:
```java
List<String> sentences = List.of("Hello world", "Java streams");
List<String> words = sentences.stream()
    .flatMap(s -> Arrays.stream(s.split(" ")))
    .collect(Collectors.toList());
// ["Hello", "world", "Java", "streams"]
```
`map` would produce `List<String[]>` (1-to-1), not flattening.

### Senior
Q: In a parallel stream, what determines the number of threads used? How would you configure the thread pool? What are the risks?
A: Parallel streams use `ForkJoinPool.commonPool()` — default pool size = `Runtime.getRuntime().availableProcessors() - 1`. Cannot be easily changed (global property `java.util.concurrent.ForkJoinPool.common.parallelism`).

Risks:
1. Blocking operations in parallel stream block the common pool — affects other parts of system.
2. Uneven work distribution (spliterator splits poorly for some data structures) — some threads idle.
3. Shared state in lambdas — thread safety issues.
4. Overhead for small datasets.

Custom pool: Wrap in `ForkJoinPool` submit:
```java
ForkJoinPool customPool = new ForkJoinPool(10);
try {
    customPool.submit(() -> stream.parallel().forEach(...)).get();
} finally { customPool.shutdown(); }
```

### Tricky
Q: Can a `Stream<Stream<T>>` be created, and how does that differ from `flatMap`?
A: Yes — `Stream.of(stream1, stream2)` creates `Stream<Stream<T>>`. But you almost never want this. The inner streams must be consumed independently, and nesting streams creates complex lifecycle management. `flatMap` automatically merges all inner streams into a single `Stream<T>` — simpler and correct for most cases.

## 10. Final 30-Second Answer

Stream = lazy sequence of elements with functional pipeline. **Intermediate ops**: filter, map, flatMap, sorted, distinct, limit, skip. **Terminal ops**: collect, forEach, reduce, count, anyMatch, findFirst, findAny. **Lazy**: nothing executes until terminal op. **Single-use**: consumed after terminal op. **Primitive streams**: IntStream, LongStream, DoubleStream (avoid boxing). **Parallel**: `stream.parallel()` or `collection.parallelStream()` — uses common ForkJoinPool. **Collectors**: toList, toSet, toMap, groupingBy, partitioningBy, joining. **Custom collector**: supplier + accumulator + combiner + finisher. Short-circuit: limit, findFirst, anyMatch.