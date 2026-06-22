# Optional — Complete Deep Dive

## 1. Why This Concept Matters

Optional (Java 8+) is a container object that may or may not contain a non-null value. It forces callers to consciously handle "absent" cases, reducing NullPointerException. In production, Optional is used for return types from methods that can fail to produce a result. Interviewers test this to assess understanding of functional programming patterns, proper Optional usage, and NPE prevention philosophy.

Misunderstanding Optional causes:
- Using `Optional.get()` without checking `isPresent()` — defeats purpose
- Using Optional as field/method parameter (misuse)
- Using Optional for collections (empty collection is better)
- Chaining `orElse()` with expensive computation (always evaluated)

## 2. Basic Meaning

Optional is a value-based container: either empty (`Optional.empty()`) or present (`Optional.of(value)`). Supports functional chaining with `map`, `filter`, `flatMap`, `orElse`, `orElseGet`, `orElseThrow`.

**Key vocabulary:**
- **`Optional.of(value)`**: creates Optional with non-null value (NPE if null)
- **`Optional.ofNullable(value)`**: creates Optional (empty if null)
- **`Optional.empty()`**: creates empty Optional
- **`isPresent()`**: boolean check
- **`ifPresent(Consumer)`**: run action if present
- **`orElse(default)`**: return value or default (eager)
- **`orElseGet(Supplier)`**: return value or compute default (lazy)
- **`orElseThrow(Supplier)`**: return value or throw exception
- **`map(Function)`**: transform if present
- **`flatMap(Function)`**: transform if present (for Optional-returning functions)
- **`filter(Predicate)`**: conditionally keep
- **`or()`** (Java 9+): return self if present, else fallback Optional

What it is NOT: Not Serializable. Not for fields. Not for method parameters. Not for collections. Not a replacement for checked exceptions.

## 3. Real Code / Real Example

```java
import java.util.*;

public class OptionalDemo {
    public static void main(String[] args) {
        // === CREATION ===
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        Optional<String> nullable = Optional.ofNullable(getMaybeNull());

        // === CHECKING ===
        System.out.println("Present: " + present.isPresent()); // true
        System.out.println("Empty: " + empty.isPresent()); // false

        // === GETTING VALUES SAFELY ===
        // BAD: present.get() // works but NPE if empty
        // GOOD:
        String val1 = present.orElse("default");
        String val2 = empty.orElseGet(() -> computeDefault()); // lazy
        String val3 = empty.orElseThrow(() -> new NoSuchElementException("Missing"));

        // === IF PRESENT ===
        present.ifPresent(s -> System.out.println("Value: " + s));
        empty.ifPresent(s -> System.out.println("Won't print"));

        // === MAP ===
        Optional<Integer> length = present.map(String::length); // Optional[5]
        System.out.println("Length: " + length.orElse(0));

        // === FILTER ===
        Optional<String> filtered = present.filter(s -> s.length() > 10);
        System.out.println("Filtered present: " + filtered.isPresent()); // false

        // === FLAT MAP ===
        Optional<String> result = Optional.of("hello@example.com")
                .flatMap(OptionalDemo::extractDomain);
        System.out.println("Domain: " + result.orElse("none"));

        // === CHAINING EXAMPLE ===
        String configValue = Optional.ofNullable(System.getenv("MY_CONFIG"))
                .orElseGet(() -> getDefaultConfig());
        System.out.println("Config: " + configValue);

        // === OR (Java 9+) ===
        Optional<String> fallback = present.or(() -> Optional.of("fallback"));
        System.out.println("Or fallback: " + fallback.get()); // "Hello"

        // === STREAM (Java 9+) ===
        List<Optional<String>> list = List.of(Optional.of("A"), Optional.empty(), Optional.of("B"));
        List<String> nonEmpty = list.stream()
                .flatMap(Optional::stream)
                .toList(); // [A, B]
        System.out.println("Non-empty: " + nonEmpty);

        // === IF PRESENT OR ELSE (Java 9+) ===
        present.ifPresentOrElse(
                s -> System.out.println("Present: " + s),
                () -> System.out.println("Empty")
        );
        empty.ifPresentOrElse(
                s -> System.out.println("Present: " + s),
                () -> System.out.println("Empty")
        );
    }

    private static String getMaybeNull() { return null; }
    private static String computeDefault() { return "computed-default"; }
    private static String getDefaultConfig() { return "default-config"; }

    private static Optional<String> extractDomain(String email) {
        if (email == null || !email.contains("@")) return Optional.empty();
        return Optional.of(email.substring(email.indexOf("@") + 1));
    }
}
```

Expected output:
```
Present: true
Empty: false
Value: Hello
Length: 5
Filtered present: false
Domain: example.com
Config: default-config
Or fallback: Hello
Non-empty: [A, B]
Present: Hello
Empty
```

## 4. What Happens Internally

```java
public final class Optional<T> {
    private static final Optional<?> EMPTY = new Optional<>(null);
    private final T value;

    private Optional(T value) {
        this.value = value;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(Objects.requireNonNull(value)); // NPE if null!
    }

    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? (Optional<T>) EMPTY : new Optional<>(value);
    }

    public static <T> Optional<T> empty() {
        return (Optional<T>) EMPTY;
    }
}
```

Simple wrapper — stores value (or null for empty). Stateless singleton for empty.

**`map()` flow:**
```java
public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    if (!isPresent()) return empty();
    return Optional.ofNullable(mapper.apply(value));
}
```
If empty: returns empty. If present: applies mapper, wraps result in Optional.

**`flatMap()` flow:**
```java
public <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
    Objects.requireNonNull(mapper);
    if (!isPresent()) return empty();
    return (Optional<U>) mapper.apply(value); // no wrapping — expects Optional return
}
```
Similar to `map()` but assumes mapper returns Optional — avoids double wrapping.

**`orElseGet()` vs `orElse()`:**
```java
// orElse: expression evaluated ALWAYS (eager)
String s = opt.orElse(expensive()); // expensive() called even if opt is present!

// orElseGet: supplier called ONLY when empty (lazy)
String s = opt.orElseGet(() -> expensive()); // expensive() called only if opt is empty
```

## 5. Tricky Interview Cases

**Case 1 — `orElse` always evaluates arguments**
```java
Optional<String> opt = Optional.of("present");
String result = opt.orElse(computeExpensive()); // computeExpensive called!
```
Output: `computeExpensive()` always runs even though value is present.
Fix: Use `orElseGet(() -> computeExpensive())` for lazy evaluation.

**Case 2 — `Optional.of(null)` throws NPE**
```java
String nullString = null;
Optional<String> opt = Optional.of(nullString); // NullPointerException!
```
Fix: Use `Optional.ofNullable(nullString)` when value may be null.

**Case 3 — Chaining `map` with `orElse` for default processing**
```java
Optional<String> name = Optional.empty();
int length = name.map(String::length).orElse(0); // 0 — no NPE
System.out.println(length); // 0
```
Output: 0. Chain works safely — empty map stays empty.

**Case 4 — Using Optional as field type**
```java
public class User {
    private Optional<String> email; // DON'T DO THIS
}
```
Problem: Optional is not Serializable. Breaks JPA/Hibernate. Use null-checking instead.

**Case 5 — `flatMap` for nested Optional**
```java
Optional<Optional<String>> nested = Optional.of(Optional.of("hello"));
// with map: returns Optional<Optional<String>> — double wrapped
Optional<Optional<String>> mapped = nested.map(s -> s);
// with flatMap: returns Optional<String> — unwrapped
Optional<String> flatMapped = nested.flatMap(s -> s);
System.out.println(flatMapped.get()); // "hello"
```

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `Optional.get()` without `isPresent()` | NoSuchElementException on empty | Use `orElse`, `orElseGet`, `orElseThrow` |
| Optional as field/parameter | Not Serializable, violates design | Use null checks, @Nullable annotations |
| `orElse(expensive())` | Expensive call always evaluates | Use `orElseGet(() -> expensive())` |
| `Optional.of(null)` | NPE | Use `Optional.ofNullable()` |
| Optional for collections | empty list already signals absence | Return empty collection, not Optional |
| Wrapping in Optional unnecessarily | Runtime overhead | Use null for simple cases |

## 7. Production Usage

**Repository return type:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // good: caller must handle missing
}
```

**Service layer:**
```java
public Optional<Order> findOrderById(Long id) {
    return orderRepository.findById(id)
            .filter(order -> !order.isDeleted())
            .map(this::enrichWithStatus);
}
```

**Configuration with fallback:**
```java
public String getDatabaseUrl() {
    return Optional.ofNullable(System.getenv("DB_URL"))
            .orElseGet(() -> applicationProperties.getDefaultDbUrl());
}
```

**Controller response:**
```java
@GetMapping("/user/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

## 8. Advanced Details

- **Value-based class**: `Optional` is annotated `@ValueBased` — use `==` comparison is discouraged, use `equals()`.
- **`OptionalInt`, `OptionalLong`, `OptionalDouble`**: Primitive specializations for avoiding boxing.
- **Java 9 additions**: `or()`, `ifPresentOrElse()`, `stream()`.
- **`Optional.stream()`**: Returns stream of zero or one element. Enables flatMap with `Stream<Optional<T>>` → `Stream<T>`.
- **`equals()`**: Two Optionals equal if both empty or both present with equal values.
- **`hashCode()`**: hash of value (0 for empty).

## 9. Interview Questions And Answers

### Beginner
Q: What is Optional? When should you use it?
A: Optional is a container that may or may not contain a non-null value. Use it for METHOD RETURN TYPES where absence is a valid outcome. Don't use for fields, method parameters, or collections. It forces callers to handle the absent case.

### Intermediate
Q: Difference between `orElse` and `orElseGet`? Which is preferred?
A: `orElse(value)` eagerly evaluates argument — even when Optional is present. `orElseGet(supplier)` lazily evaluates supplier — only when Optional is empty. Prefer `orElseGet()` when default is expensive to compute. Use `orElse()` for simple constants.

### Senior
Q: How would you use Optional in a REST API response mapping? When would you map to 200 vs 404?
A: Use `Optional.map(response -> ok(response)).orElse(notFound().build())`. Example:
```java
return userService.findById(id)
    .map(user -> ResponseEntity.ok(user))
    .orElse(ResponseEntity.notFound().build());
```

If both present and absent are valid (e.g., search results), return empty list instead (not Optional).

### Tricky
Q: Optional is not Serializable. How do you handle Optional in a Serializable class or JPA entity?
A: Don't declare fields as Optional. Use convention:
- `private String email;` with `public Optional<String> getEmail() { return Optional.ofNullable(email); }`.
- Or use `@Nullable` annotation for field-level nullability.
- JPA: Optional return types on repository methods only (findBy methods).

## 10. Final 30-Second Answer

Optional = container for nullable return types. **Create**: `Optional.of(value)` (NPE if null), `Optional.ofNullable(value)`, `Optional.empty()`. **Consume**: `orElse(default)`, `orElseGet(supplier)` (lazy), `orElseThrow(exception)`. **Transform**: `map()`, `flatMap()`, `filter()`. **Check**: `isPresent()`, `ifPresent(consumer)`. Java 9+: `or()`, `ifPresentOrElse()`, `stream()`. **Never**: Optional as field/parameter, `get()` without check, `of(null)`.