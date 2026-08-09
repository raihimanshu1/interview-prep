# Module 5a — Java 8 Features: Lambda, Stream, Optional — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---


Perfect. These are mostly **Java 8+ and Java 17+ features**. Since you have **30 minutes**, don't memorize internals—just know **what problem each feature solved** and where it's used.

# Java 8 → Java 21 Features (30-Minute Interview Recap) ⭐⭐⭐⭐⭐

---

# Evolution

```text
Java 8
│
├── Lambda
├── Functional Interface
├── Streams
├── Optional
├── New Date API
│
Java 14+
│
├── Switch Expressions
├── Records
├── Text Blocks
│
Java 17
│
├── Sealed Classes
├── Pattern Matching
│
Java 21
│
├── Virtual Threads
└── Structured Concurrency (Preview)
```

---

# 1. Lambda Expressions ⭐⭐⭐⭐⭐

## Problem

Before Java 8

```java
Collections.sort(list, new Comparator<Employee>() {
    @Override
    public int compare(Employee a, Employee b) {
        return a.getAge() - b.getAge();
    }
});
```

Too much boilerplate.

Java 8

```java
list.sort((a, b) -> a.getAge() - b.getAge());
```

## What?

Treat behaviour as data.

```text
Method

↓

Object

↓

Pass Around
```

Internally

* Compiler generates bytecode.
* JVM uses `invokedynamic` and `LambdaMetafactory` (instead of generating anonymous inner classes).

Interview

* Lambda vs Anonymous Class
* Why are lambdas faster?

---

# 2. Functional Interface ⭐⭐⭐⭐⭐

Exactly **one abstract method**.

```java
@FunctionalInterface
interface Calculator {
    int add(int a, int b);
}
```

Examples

* Runnable
* Callable
* Comparator
* Predicate
* Function
* Consumer
* Supplier

Interview

* Can it have default/static methods? → **Yes**

---

# 3. Streams ⭐⭐⭐⭐⭐

## What?

A pipeline for processing collections.

```text
Collection

↓

Filter

↓

Map

↓

Sort

↓

Collect
```

Example

```java
employees.stream()
         .filter(Employee::isActive)
         .map(Employee::getName)
         .toList();
```

Benefits

* Declarative
* Readable
* Parallel processing support

Interview

* Stream vs Collection
* Lazy Evaluation
* Intermediate vs Terminal operations

---

# 4. Stream Pipeline ⭐⭐⭐⭐

```text
Source

↓

Intermediate Operations

(filter/map/sorted)

↓

Terminal Operation

(collect/count/forEach)
```

Intermediate operations are **lazy**.

Nothing executes until a terminal operation is called.

---

# 5. Spliterator ⭐⭐⭐⭐

Supports efficient traversal and splitting.

```text
Large Collection

↓

Split

↓

Split

↓

Parallel Processing
```

Used internally by Parallel Streams.

Interview

* Iterator vs Spliterator

---

# 6. Collector Framework ⭐⭐⭐⭐

Converts Stream results.

```java
.collect(Collectors.toList())

.collect(Collectors.groupingBy())

.collect(Collectors.partitioningBy())
```

Very common in Java 8 interviews.

---

# 7. Parallel Streams ⭐⭐⭐⭐

```java
list.parallelStream()
```

Uses

```text
ForkJoinPool.commonPool()
```

Best for

* CPU-intensive tasks
* Independent operations

Avoid

* Database calls
* Blocking I/O

Interview

* Stream vs Parallel Stream
* When should you avoid Parallel Streams?

---

# 8. Optional ⭐⭐⭐⭐⭐

Problem

```java
NullPointerException
```

Solution

```java
Optional<Employee>
```

Example

```java
user.map(User::getName)
    .orElse("Guest");
```

Interview

* Should Optional be used in entity fields? → Generally **No**
* Good for return types? → **Yes**

---

# 9. New Date API (java.time) ⭐⭐⭐⭐⭐

Replaced

```java
Date

Calendar
```

Problems

* Mutable
* Confusing
* Not thread-safe

New API

* LocalDate
* LocalTime
* LocalDateTime
* Instant
* Duration
* Period

All are immutable and thread-safe.

---

# 10. Records ⭐⭐⭐⭐⭐

Before

```java
class Employee {

    private final String name;

    // constructor

    // getters

    // equals

    // hashCode

    // toString
}
```

After

```java
record Employee(String name, int age){}
```

Automatically generates

* Constructor
* Getters
* equals()
* hashCode()
* toString()

Best for immutable DTOs.

---

# 11. Sealed Classes ⭐⭐⭐⭐

Restrict inheritance.

```java
sealed class Shape
    permits Circle, Rectangle {}
```

Only permitted classes can extend it.

Useful for controlled hierarchies.

---

# 12. Pattern Matching ⭐⭐⭐⭐

Old

```java
if(obj instanceof String){

    String s=(String)obj;
}
```

New

```java
if(obj instanceof String s){

    System.out.println(s);
}
```

Cleaner.

Less casting.

---

# 13. Switch Expressions ⭐⭐⭐⭐

Old

```java
switch(day){

case MONDAY:

...
}
```

New

```java
return switch(day){

case MONDAY -> "Work";

case SUNDAY -> "Holiday";
};
```

Cleaner.

No fall-through.

Can return values.

---

# 14. Text Blocks ⭐⭐⭐

Before

```java
String json="{\n\"name\":\"Java\"}";
```

After

```java
String json="""
{
  "name":"Java"
}
""";
```

Useful for

* SQL
* JSON
* XML

---

# 15. Virtual Threads ⭐⭐⭐⭐⭐

Introduced in Java 21.

Instead of

```text
1 Platform Thread

↓

1 Request
```

Now

```text
1 Platform Thread

↓

Thousands of

Virtual Threads
```

Best for

* REST APIs
* Database calls
* HTTP clients
* Blocking I/O

Interview favourite.

---

# 16. Structured Concurrency (Preview) ⭐⭐⭐

Groups related concurrent tasks.

Instead of manually managing multiple futures,

```text
Parent Task

├── Child Task A

├── Child Task B

└── Child Task C
```

The JVM manages their lifecycle together.

Benefits

* Easier cancellation
* Better error handling
* Cleaner concurrent code

---

# Frequently Asked Interview Questions ⭐⭐⭐⭐⭐

### Java 8

* Lambda vs Anonymous Class
* Functional Interface examples
* Stream vs Collection
* Intermediate vs Terminal operations
* Optional vs null
* Parallel Stream vs Stream
* Spliterator vs Iterator

### Java 17

* Why Records?
* When would you use Sealed Classes?
* Pattern Matching benefits
* Switch Expression improvements

### Java 21

* What are Virtual Threads?
* Virtual Threads vs Platform Threads
* What problem does Structured Concurrency solve?

---

# 30-Second Revision

```text
Java 8
│
├── Lambda
├── Functional Interface
├── Streams
├── Collectors
├── Optional
└── java.time

Java 17
│
├── Records
├── Sealed Classes
├── Pattern Matching
├── Switch Expressions
└── Text Blocks

Java 21
│
├── Virtual Threads
└── Structured Concurrency
```

---

# ⭐ Highest Priority for 7+ Years

| Feature                    | Priority          |
| -------------------------- | ----------------- |
| Streams                    | ⭐⭐⭐⭐⭐             |
| Lambda                     | ⭐⭐⭐⭐⭐             |
| Functional Interfaces      | ⭐⭐⭐⭐⭐             |
| Optional                   | ⭐⭐⭐⭐⭐             |
| CompletableFuture (Java 8) | ⭐⭐⭐⭐⭐             |
| Records                    | ⭐⭐⭐⭐              |
| Virtual Threads            | ⭐⭐⭐⭐⭐             |
| Sealed Classes             | ⭐⭐⭐               |
| Pattern Matching           | ⭐⭐⭐               |
| Switch Expressions         | ⭐⭐⭐               |
| Text Blocks                | ⭐⭐                |
| Structured Concurrency     | ⭐⭐ (good to know) |

**One correction for interviews:** don't say **"Lambda creates an object instead of a method."** The more accurate explanation is:

* A lambda is an implementation of a **functional interface**.
* Unlike an anonymous inner class, the JVM typically creates it using **`invokedynamic`** and **`LambdaMetafactory`**, allowing more efficient implementation and optimization. This distinction is a common senior-level follow-up question.


## Q1. How do lambda expressions work internally? Explain invokedynamic and functional interfaces.

### 1. Why This Concept Matters
Lambdas transformed Java programming, but internally they're NOT syntactic sugar for anonymous inner classes — they use invokedynamic, which is more efficient. Interviewers ask this to test your understanding of **how the JVM actually executes lambdas**, not just how to write them.

### 2. Basic Meaning
**Lambda**: A concise way to implement a single-method interface (functional interface). Unlike anonymous inner classes, lambdas don't create separate .class files at compile time. Instead, the JVM creates the implementation dynamically at runtime using invokedynamic.

### 3. Real Code / Real Example

```java
// =====================================================
// LAMBDA vs ANONYMOUS INNER CLASS
// =====================================================

// What you write (lambda):
Runnable r1 = () -> System.out.println("Hello");

// Old way (anonymous inner class — DON'T):
Runnable r2 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello");
    }
};

// Lambda is FASTER because:
// 1. No separate .class file created
// 2. No new anonymous class loaded by ClassLoader  
// 3. invokedynamic defers implementation to runtime

// =====================================================
// FUNCTIONAL INTERFACE — the contract
// =====================================================

@FunctionalInterface  // Ensures exactly ONE abstract method
interface Calculator {
    int calculate(int a, int b);
    
    // Default methods don't count (can have many)
    default void log() { System.out.println("Calculating..."); }
    
    // Static methods don't count
    static Calculator add() { return (a, b) -> a + b; }
}

// Usage:
Calculator adder = (a, b) -> a + b;
System.out.println(adder.calculate(3, 4));  // 7
```

### 4. What Happens Internally

**invokedynamic — How lambdas actually work:**
```
Source: Runnable r = () -> System.out.println("Hello");

Step 1: Compilation (javac)
  - Generates invokedynamic instruction in bytecode
  - Bootstrap method: LambdaMetafactory.metafactory()
  - Creates a "recipe" (what the lambda does, what interface it implements)
  - NO anonymous class generated!

Bytecode (simplified):
  invokedynamic #run, (Runnable) → lambda$0()V
  ; BootstrapMethods:
  ;  0: LambdaMetafactory.metafactory(
  ;        caller=MyClass, 
  ;        interface=Runnable,
  ;        impl=MyClass::lambda$main$0)

Step 2: First invocation (runtime)
  - LambdaMetafactory is called (bootstrap method)
  - Creates the implementation class ON THE FLY
  - Uses ASM to generate a class inside the running JVM
  - Caches the implementation

Step 3: Subsequent invocations
  - Already created — just reuse cached implementation
  - NO overhead after first call!

This is why lambdas can be FASTER than anonymous classes:
- Anonymous class: class loading + verification + initialization
- Lambda (after first call): direct method call
```

**Capturing vs Non-capturing lambdas:**
```java
// NON-CAPTURING: No variables from outer scope used
// → Lambda is a STATIC method (cached, reused for ALL calls!)
Runnable r1 = () -> System.out.println("Hello");
// Created ONCE, reused forever

// CAPTURING: Uses variables from outer scope
// → Lambda is an INSTANCE method (new instance per call!)
String message = "Hello";
Runnable r2 = () -> System.out.println(message);
// New instance created each time (must capture the value)
// Captured variables must be effectively final!
```

### 5. Tricky Interview Cases

**Case 1: Lambda vs method reference — any difference?**
```java
// Lambda:
Consumer<String> c1 = s -> System.out.println(s);

// Method reference:
Consumer<String> c2 = System.out::println;

// Bytecode: IDENTICAL. Method references are just syntactic sugar
// for the same invokedynamic mechanism
```

**Case 2: this in lambda vs anonymous class**
```java
class MyClass {
    String name = "outer";
    
    void test() {
        Runnable lambda = () -> System.out.println(this.name);
        // "outer" — this refers to MyClass instance
        
        Runnable anon = new Runnable() {
            String name = "inner";
            @Override
            public void run() {
                System.out.println(this.name);
                // "inner" — this refers to anonymous class!
            }
        };
    }
}
// KEY: Lambda's this = enclosing class. Anonymous class this = its own instance
```

**Case 3: Serialization of lambdas**
```java
Runnable r = (Runnable & Serializable) () -> System.out.println("Hi");
// Can serialize if cast includes Serializable

// Regular lambdas CANNOT be serialized:
Runnable r2 = () -> System.out.println("Hi");
// NotSerializableException if you try to serialize
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Modifying captured variable inside lambda | Compile error (must be effectively final) | Use array/AtomicReference as workaround |
| Assuming lambdas are always cheaper | Capturing lambdas create new objects each use | Extract to static method when possible |
| Using lambda with @FunctionalInterface annotation on interface with 2 abstract methods | Compile error | Ensure interface has exactly one abstract method |
| Expecting lambda to work with abstract classes | Lambdas only work with interfaces | Use anonymous class for abstract classes |

### 7. Production Usage

**Method references for cleaner code:**
```java
// BAD:
list.stream()
    .filter(s -> s != null)
    .map(s -> s.toUpperCase())
    .forEach(s -> System.out.println(s));

// GOOD (method references):
list.stream()
    .filter(Objects::nonNull)
    .map(String::toUpperCase)
    .forEach(System.out::println);
```

### 8. Advanced Details

**Performance: Lambda vs Anonymous inner class:**
```
Non-capturing lambda:     ~1ns (first call ~100ns for bootstrap)
Capturing lambda:         ~1ns (first call ~100ns + object creation)
Anonymous inner class:    ~1ns + class loading (~1μs) + verification
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What makes an interface a functional interface?

**A**: A functional interface has exactly one abstract method. Use @FunctionalInterface to enforce this (optional but recommended). Examples: Runnable (run), Callable (call), Comparator (compare). Java 8 added many: Function, Predicate, Consumer, Supplier. Default and static methods don't count toward the single-abstract-method rule.

#### Intermediate

**Q**: How are lambdas different from anonymous inner classes internally?

**A**: Anonymous inner classes create a separate .class file, load it, verify it, and instantiate it — expensive. Lambdas use invokedynamic — at compile time, javac generates a bootstrap method (LambdaMetafactory). At runtime, the JVM calls this bootstrap method once to create the implementation, then caches it. Non-capturing lambdas are created once and reused forever — they're essentially static methods. Also, this in a lambda refers to the enclosing class, not the lambda itself.

#### Senior

**Q**: When would you prefer an anonymous inner class over a lambda?

**A**: (1) When implementing an interface with MULTIPLE abstract methods (lambdas only support SAM interfaces). (2) When you need a separate this context (anonymous class has its own this; lambda's this refers to enclosing class). (3) When you need to override methods from Object (toString, hashCode). (4) When implementing abstract classes (lambdas only work with interfaces). Otherwise, prefer lambdas for readability and performance.

#### Tricky

**Q**: Explain why non-capturing lambdas are effectively static methods.

**A**: When a lambda doesn't capture any variables from outer scope (no this, no local variables), the JVM generates a static method to hold the lambda body. The LambdaMetafactory creates a single instance of the functional interface implementation that delegates to this static method — reused for ALL invocations. This is possible because no state needs to be carried per invocation. Capturing lambdas require an instance (to carry captured values), so they allocate a new instance each time the lambda expression is evaluated.

### 10. Final 30-Second Answer

Lambdas use invokedynamic — no separate class files. Non-capturing lambdas are cached static methods. Capturing lambdas allocate per evaluation. this in lambda refers to enclosing class. Use method references for cleaner code. Lambdas work only with functional interfaces (single abstract method).

---

## Q2. How does Stream API work? Explain intermediate vs terminal operations, lazy evaluation.

### 1. Why This Concept Matters
Streams are Java 8's most powerful feature for data processing. Understanding lazy evaluation, short-circuiting, and parallel streams is essential for writing efficient data pipelines. Interviewers ask this to test functional programming comprehension and performance awareness.

### 2. Basic Meaning
**Stream**: A sequence of elements supporting sequential and parallel aggregate operations. It's NOT a data structure — it's a pipeline that processes data from a source (collection, array, I/O channel).

### 3. Real Code / Real Example

```java
// =====================================================
// STREAM PIPELINE — Intermediate + Terminal
// =====================================================

List<Order> orders = getOrders();

// Pipeline:
double total = orders.stream()          // Source: collection
    .filter(o -> o.getStatus() == COMPLETED)  // Intermediate: lazy
    .map(Order::getTotal)                      // Intermediate: lazy
    .filter(amount -> amount > 100)            // Intermediate: lazy
    .reduce(0.0, Double::sum);                 // Terminal: triggers execution!

// KEY: Until reduce() is called, filter/map DO NOTHING!
// Lazy evaluation: operations are combined into a single pass

// =====================================================
// DEMONSTRATING LAZY EVALUATION
// =====================================================

Stream<String> stream = list.stream()
    .filter(s -> {
        System.out.println("Filter: " + s);
        return s.startsWith("A");
    })
    .map(s -> {
        System.out.println("Map: " + s);
        return s.toUpperCase();
    });
// Nothing printed yet! Stream is NOT executed.

// Now add terminal:
List<String> result = stream.collect(Collectors.toList());
// Now filter+map run: ALL elements processed in ONE pass
// Output: Filter: Alice, Map: Alice, Filter: Bob, Filter: Ann, Map: Ann...
```

### 4. What Happens Internally

**Stream pipeline execution:**
```
Source: ["Alice", "Bob", "Ann", "Alex"]

Pipeline: filter(name → startsWith("A")) → map(toUpperCase) → collect(toList)

Execution (lazy, fused):
  Step 1: Get "Alice" from source
  Step 2: filter → "Alice" starts with "A"? YES
  Step 3: map → "ALICE"  
  Step 4: Add to result list
  
  Step 5: Get "Bob" from source
  Step 6: filter → "Bob" starts with "A"? NO → SKIP map
  Step 7: Get "Ann" from source
  Step 8: filter → "Ann" starts with "A"? YES
  Step 9: map → "ANN"
  Step 10: Add to result list
  ...
  
Result: ["ALICE", "ANN", "ALEX"]

Note: filter and map are FUSED into ONE pass!
NO intermediate collection created between filter and map!
```

**Spliterator — how parallel streams split work:**
```
Data Source: [1, 2, 3, 4, 5, 6, 7, 8]
                         │
                    trySplit()
                         │
        ┌────────────────┴────────────────┐
        │                                 │
    [1,2,3,4]                        [5,6,7,8]
        │                                 │
   trySplit()                        trySplit()
        │                                 │
   ┌────┴────┐                       ┌────┴────┐
   │         │                       │         │
 [1,2]     [3,4]                   [5,6]     [7,8]
   │         │                       │         │
   ▼         ▼                       ▼         ▼
Thread1   Thread2                  Thread3   Thread4
 (FJ pool)
```

### 5. Tricky Interview Cases

**Case 1: Stream is already consumed**
```java
Stream<String> stream = list.stream();
stream.forEach(System.out::println);  // OK, prints all
stream.count();  // ❌ IllegalStateException: stream has already been operated upon or closed
// Streams are ONE-THROW-AWAY — cannot reuse
```

**Case 2: Infinite streams — must short-circuit**
```java
// BAD: Infinite loop!
Stream.iterate(1, i -> i + 1)
    .collect(Collectors.toList());  // OutOfMemoryError — never terminates!

// GOOD: Short-circuit with limit()
Stream.iterate(1, i -> i + 1)
    .limit(10)
    .collect(Collectors.toList());  // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

// BAD: Infinite + no short-circuit
// Stream.generate(Math::random).collect(Collectors.toList()); // OutOfMemoryError
```

**Case 3: Stateful operations break parallel performance**
```java
// Stateful: sorted() needs ALL elements before emitting first
list.parallelStream()
    .filter(Objects::nonNull)
    .sorted()     // Must wait for ALL threads to finish!
    .collect(toList());
// sorted() forces ALL prior operations to complete → limits parallelism

// Stateless: filter, map — can work on elements independently
// Stateful: sorted, distinct, limit, skip — break parallelism
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Reusing a stream | IllegalStateException | Create new stream for each pipeline |
| Using parallelStream() on small collections | Thread overhead > parallelism gain | Use parallel only for large data (10K+ elements) |
| Modifying source while streaming | ConcurrentModificationException | Collect to new collection first |
| Not using short-circuit for infinite streams | Infinite loop / OOM | Always use limit() or findFirst() with infinite streams |
| Forgetting streams are single-use | Attempting to operate on consumed stream | Create new stream instance |
| Using forEach for non-side-effect operations | Hard to debug, not composable | Prefer collectors for accumulating results |

### 7. Production Usage

**Collectors toMap with duplicate key handling:**
```java
// BAD: Throws if duplicate keys
Map<Integer, String> map = list.stream()
    .collect(Collectors.toMap(Item::getId, Item::getName));

// GOOD: Handle duplicates (keep first)
Map<Integer, String> map = list.stream()
    .collect(Collectors.toMap(
        Item::getId,
        Item::getName,
        (existing, replacement) -> existing  // Keep first on conflict
    ));

// Grouping:
Map<String, List<Order>> byStatus = orders.stream()
    .collect(Collectors.groupingBy(Order::getStatus));

// Partitioning:
Map<Boolean, List<Order>> paid = orders.stream()
    .collect(Collectors.partitioningBy(Order::isPaid));
```

### 8. Advanced Details

**Performance: Stream vs Loop benchmark:**
```
Operation               | Loop    | Sequential Stream | Parallel Stream
------------------------|---------|-------------------|-----------------
sum of 10M ints         | 15ms    | 25ms              | 8ms
filter+map 10M items    | 45ms    | 55ms              | 18ms  
groupBy 1M items        | 120ms   | 150ms             | 60ms

Source: OpenJDK benchmarks. Sequential streams have ~30% overhead vs loops for simple ops.
Parallel streams shine with LARGE datasets and independent operations.
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What's the difference between intermediate and terminal operations?

**A**: Intermediate operations (filter, map, sorted) return a new stream and are **lazy** — they don't execute until a terminal operation is called. Terminal operations (collect, forEach, reduce) trigger the entire pipeline and consume the stream. A stream without a terminal operation does nothing. Once a terminal operation is called, the stream is consumed and cannot be reused.

#### Intermediate

**Q**: What is lazy evaluation and why does it matter?

**A**: Lazy evaluation means operations are composed into a pipeline but not executed until a terminal operation is called. Benefits: (1) Fused processing — all operations are applied in a single pass, avoiding intermediate collections; (2) Short-circuiting — findFirst() stops processing once the first match is found; (3) Optimization — the stream implementation can reorder operations for efficiency. Example: `stream.filter(expensive).limit(10)` only processes elements until 10 match, not all elements.

#### Senior

**Q**: When would you use parallel streams? What are the gotchas?

**A**: Parallel streams help when: (1) Large dataset (10K+ elements); (2) Computationally expensive per-element operations; (3) Operations are independent (no shared mutable state). Gotchas: (1) Thread pool is shared (ForkJoinPool.commonPool()) — can starve other operations; (2) Stateful operations (sorted, distinct, limit) require coordination, reducing or eliminating parallelism benefit; (3) Small datasets suffer from thread overhead; (4) Blocking operations (I/O, network) in parallel streams are bad — use CompletableFuture instead; (5) Concurrent modification of shared state causes data races. Benchmark before using parallel.

#### Tricky

**Q**: You need to process a stream with 100K elements where each element processing takes 500ms (I/O bound). Would you use parallel streams?

**A**: No — parallel streams use ForkJoinPool with limited threads (usually #CPU cores = 4-16). With 500ms I/O per element, 100K elements would take hours. Worse, blocking operations in parallel streams waste the pool. Instead, use CompletableFuture with a custom thread pool sized for I/O (e.g., 100 threads): `CompletableFuture.allOf(elements.stream().map(e -> CompletableFuture.supplyAsync(() -> process(e), executor)).toArray(...))`. Parallel streams work for CPU-bound computation; CompletableFuture works for I/O-bound concurrency.

### 10. Final 30-Second Answer

Streams are lazy pipelines: intermediate ops compose, terminal ops trigger execution. Lazy evaluation enables fused processing and short-circuiting. Prefer sequential for simplicity, parallel for large CPU-bound operations. Never reuse streams. Use Collectors for aggregation. Parallel streams with I/O operations waste the thread pool.