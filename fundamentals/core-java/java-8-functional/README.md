# Java 8 Functional Programming — Complete Deep Dive

## 1. Why This Concept Matters

Java 8 introduced lambda expressions, functional interfaces, streams, and Optional — fundamentally changing how Java code is written. These features enable functional programming patterns: passing behavior as arguments, composing functions, declarative data processing, and lazy evaluation. In production, lambdas replaced anonymous inner classes in event handlers, comparators, and callables. Functional interfaces (`Function`, `Predicate`, `Consumer`, `Supplier`) form the backbone of the Streams API, method references, and are heavily used in Spring WebFlux, Project Reactor, and modern Java libraries. Interviewers test this heavily because it shows whether you write modern Java or legacy Java, and whether you understand concepts like pure functions, side effects, and declarative programming.

Misunderstanding Java 8 functional programming causes:
- Writing verbose anonymous inner classes where lambdas would be 10x shorter
- Side effects in stream operations (modifying external state)
- Not understanding lazy evaluation (streams don't execute until terminal operation)
- Overusing parallel streams (thread safety issues, slower than sequential for small data)
- Confusing map vs flatMap, filter vs find, reduce vs collect

## 2. Basic Meaning

**Lambda expressions**: anonymous functions — you pass behavior as data. `(parameters) -> expression` or `(parameters) -> { statements; }`

**Functional interfaces**: interfaces with exactly ONE abstract method. Can be annotated with `@FunctionalInterface`. The compiler enforces exactly one abstract method.

**Key functional interfaces in `java.util.function`:**
| Interface | Input | Output | Method | Use Case |
|-----------|-------|--------|--------|----------|
| `Predicate<T>` | T | boolean | `test(T)` | Filtering, validation |
| `Function<T,R>` | T | R | `apply(T)` | Transformation, mapping |
| `Consumer<T>` | T | void | `accept(T)` | Printing, side effects |
| `Supplier<T>` | none | T | `get()` | Lazy initialization, factories |
| `UnaryOperator<T>` | T | T | `apply(T)` | Same type transformation |
| `BinaryOperator<T>` | T,T | T | `apply(T,T)` | Aggregation, reduction |

**Method references**: shorthand for lambdas. `ClassName::methodName` or `object::methodName`.

**What it is NOT:**
- Not true functional programming (Java has mutable state, side effects)
- Not a replacement for loops in all cases (performance-critical code may prefer loops)
- Not automatically faster than imperative code (streams have overhead)

## 3. Real Code / Real Example

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Java8FunctionalDemo {
    
    public static void main(String[] args) {
        // === 1. LAMBDA BASICS ===
        
        // Before Java 8: anonymous inner class
        Comparator<String> oldWay = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();
            }
        };
        
        // Java 8: lambda
        Comparator<String> lambdaWay = (a, b) -> a.length() - b.length();
        
        // Even shorter: method reference
        Comparator<String> methodRef = Comparator.comparingInt(String::length);
        
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        names.sort(methodRef);
        System.out.println("Sorted by length: " + names); // [Bob, Alice, Charlie]
        
        // === 2. FUNCTIONAL INTERFACES IN ACTION ===
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Predicate: test a condition
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isGreaterThan5 = n -> n > 5;
        
        // Combine predicates
        Predicate<Integer> isEvenAndGreaterThan5 = isEven.and(isGreaterThan5);
        
        List<Integer> result = numbers.stream()
            .filter(isEvenAndGreaterThan5)
            .collect(Collectors.toList());
        System.out.println("Even > 5: " + result); // [6, 8, 10]
        
        // Function: transform
        Function<String, Integer> parseAndSquare = s -> {
            int n = Integer.parseInt(s);
            return n * n;
        };
        System.out.println("Square of '5': " + parseAndSquare.apply("5")); // 25
        
        // Compose functions
        Function<Integer, Integer> multiplyBy2 = n -> n * 2;
        Function<Integer, Integer> add3 = n -> n + 3;
        Function<Integer, Integer> addThenMultiply = multiplyBy2.compose(add3); // (n+3)*2
        Function<Integer, Integer> multiplyThenAdd = multiplyBy2.andThen(add3); // n*2+3
        System.out.println("compose (n+3)*2: " + addThenMultiply.apply(5)); // 16
        System.out.println("andThen n*2+3: " + multiplyThenAdd.apply(5)); // 13
        
        // Consumer: perform an action
        Consumer<String> print = s -> System.out.print(s + " ");
        Consumer<String> printWithComma = s -> System.out.print(s + ", ");
        List.of("apple", "banana", "cherry").forEach(printWithComma.andThen(s -> System.out.println()));
        
        // Supplier: provide values
        Supplier<Double> randomSupplier = Math::random;
        System.out.println("Random: " + randomSupplier.get());
        System.out.println("Random: " + randomSupplier.get());
        
        // === 3. METHOD REFERENCES ===
        
        List<String> words = Arrays.asList("hello", "world", "java", "streams");
        
        // Static method reference
        words.stream().map(String::toUpperCase).forEach(System.out::println);
        
        // Instance method of an object
        words.forEach(System.out::println);
        
        // Constructor reference
        Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> newList = listSupplier.get();
        
        // === 4. OPTIONAL WITH FUNCTIONAL ===
        
        Optional<String> optional = Optional.of("hello");
        
        // map: transform if present
        Optional<Integer> length = optional.map(String::length);
        
        // filter: keep only if predicate matches
        Optional<String> startsWithH = optional.filter(s -> s.startsWith("h"));
        
        // orElseGet: supplier if empty
        String result2 = optional.orElseGet(() -> "default");
        
        // ifPresent: consumer if present
        optional.ifPresent(s -> System.out.println("Found: " + s));
        
        // === 5. STREAM PIPELINE (DECLARATIVE) ===
        
        List<Transaction> transactions = Arrays.asList(
            new Transaction(100, "USD", "COMPLETED"),
            new Transaction(200, "EUR", "PENDING"),
            new Transaction(300, "USD", "COMPLETED"),
            new Transaction(50, "USD", "FAILED"),
            new Transaction(150, "EUR", "COMPLETED")
        );
        
        // Declarative: what to do, not how
        Map<String, DoubleSummaryStatistics> stats = transactions.stream()
            .filter(t -> "COMPLETED".equals(t.status()))
            .collect(Collectors.groupingBy(
                Transaction::currency,
                Collectors.summarizingDouble(Transaction::amount)
            ));
        
        System.out.println("Stats: " + stats);
        // {USD=DoubleSummaryStatistics{count=2, sum=400.0, ...}, EUR=...}
        
        // === 6. COLLECTORS ===
        
        // Partition by predicate
        Map<Boolean, List<Integer>> partitioned = numbers.stream()
            .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("Even: " + partitioned.get(true));  // [2, 4, 6, 8, 10]
        System.out.println("Odd: " + partitioned.get(false)); // [1, 3, 5, 7, 9]
        
        // Grouping by
        Map<String, List<Transaction>> byCurrency = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::currency));
        
        // Joining strings
        String joined = names.stream().collect(Collectors.joining(", "));
        System.out.println("Joined: " + joined);
        
        // Mapping to specific collection
        TreeSet<Integer> sorted = numbers.stream()
            .filter(n -> n > 5)
            .collect(Collectors.toCollection(TreeSet::new));
        System.out.println("Sorted: " + sorted);
    }
}

record Transaction(double amount, String currency, String status) {}
```

Expected output:
```
Sorted by length: [Bob, Alice, Charlie]
Even > 5: [6, 8, 10]
Square of '5': 25
compose (n+3)*2: 16
andThen n*2+3: 13
apple banana cherry 
Random: 0.12345...
Random: 0.67890...
HELLO
WORLD
JAVA
STREAMS
hello
world
java
streams
Found: hello
Stats: {USD=..., EUR=...}
Even: [2, 4, 6, 8, 10]
Odd: [1, 3, 5, 7, 9]
Joined: Bob, Alice, Charlie
Sorted: [6, 7, 8, 9, 10]
```

## 4. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Modifying external variables in lambda | Race conditions, unexpected behavior | Use only parameters and local variables (effectively final) |
| Side effects in stream forEach() | Non-deterministic with parallel streams | Use collect() for result aggregation |
| Parallel stream on shared mutable state | Thread safety issues | Use ConcurrentHashMap, synchronized, or avoid |
| map vs flatMap confusion | Wrong result type | map: T→R (one-to-one). flatMap: T→Stream<R> (one-to-many → flattens) |
| Not understanding lazy evaluation | Infinite streams cause OOM if terminal op missing | Terminal operation required to start processing |
| Overusing streams for simple loops | Slower than for-loop for small lists | Benchmark if performance matters |
| Using parallel stream for small data | Thread overhead > parallelism benefit | Use parallel only for large data (>10K elements) |

## 5. Final 30-Second Answer

Java 8 functional = lambdas + functional interfaces + streams. **Lambda**: `(params) -> expression`. **Functional interfaces**: Predicate (boolean test), Function (transform), Consumer (side effect), Supplier (lazy provider). **Method references**: `String::length`, `System::out::println`. **Streams**: declarative pipeline (source → intermediate ops → terminal op). Lazy — nothing happens until terminal op (collect, forEach, reduce). **Collectors**: toList, groupingBy, partitioningBy, joining, summarizingDouble. Never: side effects in parallel streams, modify external variables in lambdas, parallel for small data. Prefer streams for readability, loops for hot performance paths.