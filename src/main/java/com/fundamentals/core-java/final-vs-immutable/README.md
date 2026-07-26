# `final` Variable vs Immutable Object — Complete Deep Dive

## 1. Why This Concept Matters

"`final`" and "immutable" are two of the most confused concepts in Java interviews. A `final` variable cannot be reassigned — it's a restriction on the **variable**, not the **object**. An immutable object cannot have its state changed after construction — it's a restriction on the **object's design**. Mixing them up leads to: assuming `final List` is immutable (it's not), believing `final` makes all fields of an object immutable (it doesn't for reference fields), and incorrect thread-safety analysis. Interviewers ask this to test whether you understand the distinction between variable binding and object state.

Misunderstanding causes:
- Treating `final List<Integer>` as immutable (you can still `list.add()`)
- Assuming `final` guarantees thread-safety (only for primitive fields or properly constructed immutable objects)
- Creating "immutable" classes that expose mutable internal references
- Confusing `final` class (cannot extend) with immutable class (state cannot change)

## 2. Basic Meaning

**`final` on a variable**: the variable cannot be REASSIGNED. If it's a primitive, the value cannot change. If it's an object reference, the reference cannot point to a different object — but the object's internal state CAN change.

**Immutable object**: the object's state is fixed after construction. No setters, all fields are `final`, class is `final` (or no subclassable), and mutable fields are defensively copied.

| Aspect | `final` variable | Immutable object |
|--------|-----------------|------------------|
| What it restricts | Variable assignment | Object state |
| Can value change? | No (primitive) / Reference fixed (object) | No |
| Can object content change? | YES (for reference types) | NO |
| Thread-safe? | Not automatically | Yes (if properly constructed) |
| Example | `final List<String> list = new ArrayList<>()` | `String`, `Integer`, `LocalDate` |
| Can mutate? | `list.add("x")` — YES | `s.concat("y")` — returns NEW object |

What it is NOT: `final` on a field is NOT the same as immutability. `final` on a class means "cannot be subclassed", NOT "immutable". Immutable objects are not automatically serializable or clone-safe.

## 3. Real Code / Real Example

```java
import java.util.*;

public class FinalVsImmutableDemo {
    
    public static void main(String[] args) {
        // === CASE 1: final PRIMITIVE — truly cannot change ===
        final int MAX_USERS = 100;
        // MAX_USERS = 200; // COMPILE ERROR!
        
        // === CASE 2: final REFERENCE — reference fixed, object mutable ===
        final List<String> names = new ArrayList<>();
        names.add("Alice");    // ✅ OK — modifying OBJECT state, not reference
        names.add("Bob");      // ✅ OK
        System.out.println("names: " + names); // [Alice, Bob]
        
        // names = new ArrayList<>(); // COMPILE ERROR! Cannot reassign final reference
        
        // === CASE 3: "IMMUTABLE" List view via Collections.unmodifiableList ===
        List<String> immutableView = Collections.unmodifiableList(names);
        // immutableView.add("Charlie"); // THROWS UnsupportedOperationException!
        // BUT: the underlying list can still change!
        names.add("Charlie");   // Modifies underlying list
        System.out.println("immutableView sees changes: " + immutableView); 
        // [Alice, Bob, Charlie] — the "unmodifiable" view changed!
        
        // === CASE 4: TRUE immutable object ===
        String s = "Hello";
        String result = s.concat(" World");
        System.out.println("Original: " + s);      // Hello (unchanged)
        System.out.println("Result: " + result);   // Hello World (new object)
        
        // === CASE 5: Immutable class (custom) ===
        ImmutablePerson person = new ImmutablePerson("Alice", List.of("admin", "user"));
        // person.name = "Bob"; // COMPILE ERROR! field is final + private
        // person.getRoles().add("superadmin"); // UnsupportedOperationException!
        System.out.println(person); // ImmutablePerson{name='Alice', roles=[admin, user]}
        
        // === CASE 6: Mutable class masquerading as "immutable" ===
        MutablePerson bad = new MutablePerson("Bob");
        bad.getName().append(" Jr"); // Modifies internal StringBuilder!
        System.out.println(bad.getName()); // Bob Jr — state changed!
    }
}

// ✅ CORRECT immutable class
final class ImmutablePerson {
    private final String name;           // String is immutable — safe
    private final List<String> roles;    // Must defensively copy!
    
    ImmutablePerson(String name, List<String> roles) {
        this.name = name;
        // Defensive copy + unmodifiable view
        this.roles = List.copyOf(roles); // Java 10+ — creates immutable copy
    }
    
    public String getName() { return name; } // safe — String is immutable
    
    public List<String> getRoles() { 
        return roles; // already immutable via List.copyOf
    }
    
    @Override
    public String toString() {
        return "ImmutablePerson{name='" + name + "', roles=" + roles + "}";
    }
}

// ❌ VIOLATED immutable class — exposes mutable internal state
final class MutablePerson {
    private final StringBuilder name; // final reference, but StringBuilder is MUTABLE
    
    MutablePerson(String name) {
        this.name = new StringBuilder(name);
    }
    
    public StringBuilder getName() { 
        return name; // EXPOSES the mutable StringBuilder directly!
        // Caller can do: getName().append(" something") — modifies internal state
    }
}
```

Expected output:
```
names: [Alice, Bob]
immutableView sees changes: [Alice, Bob, Charlie]
Original: Hello
Result: Hello World
ImmutablePerson{name='Alice', roles=[admin, user]}
Bob Jr
```

## 4. What Happens Internally

### `final` Variable Semantics in JVM

**Primitive final fields — JVM guarantees initialization:**
```java
class Config {
    final int maxRetries = 3;      // Compile-time constant → inlined
    final String defaultEnv = "prod"; // Inlined by compiler
}
```
The JVM treats compile-time `final` constants differently:
- They are inlined by the compiler — `Config.defaultEnv` becomes `"prod"` in bytecode, no field access
- They do NOT trigger class loading when accessed (like `Integer.MAX_VALUE`)
- They are stored in the constant pool, not the field

**`final` instance fields and the constructor barrier:**
```java
class Safe {
    final int x;
    Safe(int x) { this.x = x; }
}
```
The JVM guarantees that a `final` field is properly initialized by the end of the constructor. This is enforced by:
- The `storefinal` bytecode instruction (or equivalent)
- The JMM (Java Memory Model) guarantees: any thread that sees the constructed object sees the correct value of `final` fields (even without synchronization!)
- This is the **freeze action** — after the constructor completes, `final` fields are "frozen"

**Why `final` reference fields don't make the object immutable:**
```java
final List<String> list = new ArrayList<>();
// JVM guarantees: list will never point to a different object
// JVM does NOT guarantee: list's contents won't change
// list.add() is a normal method call on the same object
```

### Immutable Object JMM Guarantees

For an object to be truly immutable and thread-safe:
1. All fields must be `final` — JMM guarantees visibility of `final` fields after construction
2. Class must be `final` — prevents subclass overriding methods to mutate state
3. No setters — obvious
4. Defensive copies on mutable fields — prevents external mutation
5. No method can modify internal state

**Why `final` fields matter for thread-safety:**
```java
class ImmutablePerson {
    private final String name;  // JMM guarantees: other threads see correct name
    // Without 'final', another thread might see null or stale value
    // Even without 'final', proper constructor + no setters = logically immutable
    // But ONLY 'final' gives the JMM guarantee without synchronization
}
```

## 5. Tricky Interview Cases

**Case 1 — `final` array — contents are mutable**
```java
final int[] arr = {1, 2, 3};
arr[0] = 99;      // ✅ OK — modifying contents, not reference
// arr = new int[]{4, 5, 6}; // COMPILE ERROR! Cannot reassign
System.out.println(Arrays.toString(arr)); // [99, 2, 3]
```

**Case 2 — `final` with inner class**
```java
class Outer {
    void createRunnable() {
        final int localVar = 42;
        // int localVar = 42; // Must be final or effectively final for lambda/anonymous class
        
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println(localVar); // 42 — captured from enclosing scope
            }
        };
        r.run();
    }
}
```
Before Java 8, captured local variables in anonymous classes had to be declared `final`. Since Java 8, they only need to be "effectively final" (not reassigned after initialization).

**Case 3 — `final` parameter does NOT prevent object mutation**
```java
void process(final Person person) {
    // person = new Person("Bob"); // COMPILE ERROR! Cannot reassign final param
    person.setName("Bob"); // ✅ OK! Final prevents reassignment, NOT mutation
}
```

**Case 4 — Immutable class with mutable field — defensive copy failure**
```java
import java.util.Date;

final class BadImmutable {
    private final Date creationDate; // Date is MUTABLE! (setTime() exists)
    
    BadImmutable(Date date) {
        this.creationDate = date; // NOT a defensive copy!
    }
    
    public Date getCreationDate() {
        return creationDate; // EXPOSES mutable reference!
    }
}

// Attack:
Date d = new Date();
BadImmutable bad = new BadImmutable(d);
d.setTime(0); // Changes the internal date!
bad.getCreationDate().setTime(9999999); // Directly mutates from outside!
```
Fix: always defensively copy mutable objects in both constructor and getters:
```java
BadImmutable(Date date) {
    this.creationDate = new Date(date.getTime()); // Defensive copy
}
public Date getCreationDate() {
    return new Date(creationDate.getTime()); // Defensive copy on output
}
```

**Case 5 — Record (Java 16+) is immutable, but watch out:**
```java
record Person(String name, List<String> roles) {}
// Auto-generated: private final fields, constructor, getters
// BUT: Person(new ArrayList<>()) allows roles.add("hacker")!
// Records don't automatically create defensive copies!
```
Fix for records with mutable components: custom constructor with defensive copy.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|------|
| Treating `final List` as immutable | You can still `add()`/`remove()` elements | Use `List.of()` or `Collections.unmodifiableList()` |
| Assuming `final class` = immutable class | `final` only prevents subclassing — class can still have setters | Make all fields `final`, no setters, defensive copies |
| Exposing mutable internal objects from getters | Caller can modify internal state | Return defensive copy or unmodifiable view |
| Not defensively copying mutable constructor params | Caller retains reference to internal state | Copy in constructor: `new ArrayList<>(input)` |
| Forgetting `final` fields on immutable class | Without `final`, JMM doesn't guarantee visibility across threads | Always make fields `final` in immutable classes |
| Using `Date` in immutable class | `Date` is mutable — breaks immutability | Use `LocalDate`, `Instant`, or defensively copy |
| Thinking `final` on method param makes param immutable | Only prevents reassignment, not mutation | Use immutable types for params |
| `Collections.unmodifiableList()` on non-final list | Underlying list can still change through original reference | Use `List.copyOf()` (Java 10+) for truly immutable copy |

## 7. Production Usage

**Spring Boot @ConfigurationProperties with immutable classes:**
```java
@ConfigurationProperties(prefix = "app.payment")
@ConstructorBinding  // Spring 2.2+ — uses constructor injection
public final class PaymentProperties {
    private final int maxRetries;
    private final String apiUrl;
    private final Duration timeout;
    
    // Spring constructs via constructor, not setters
    public PaymentProperties(int maxRetries, String apiUrl, Duration timeout) {
        this.maxRetries = maxRetries;
        this.apiUrl = apiUrl;
        this.timeout = timeout;
    }
    
    // Only getters — no setters = immutable
    public int getMaxRetries() { return maxRetries; }
    public String getApiUrl() { return apiUrl; }
    public Duration getTimeout() { return timeout; }
}
// Usage: @EnableConfigurationProperties(PaymentProperties.class)
```

**DTOs with records (Java 16+):**
```java
// Immutable DTO — no boilerplate
public record PaymentResponse(
    String transactionId,
    BigDecimal amount,
    LocalDateTime timestamp,
    PaymentStatus status
) {}

// Usage:
PaymentResponse response = new PaymentResponse(
    "TXN-123", new BigDecimal("99.99"), LocalDateTime.now(), PaymentStatus.SUCCESS
);
// response.transactionId() — accessor
// No setters — truly immutable
```

**Thread-safe cache with final holders:**
```java
public class ConfigCache {
    private final Map<String, String> config; // final reference
    
    // Atomic replacement — entire map swapped atomically
    private volatile Map<String, String> cache = Map.of();
    
    public ConfigCache(Map<String, String> initialConfig) {
        this.config = new ConcurrentHashMap<>(initialConfig);
        refresh();
    }
    
    public void refresh() {
        // Read from DB and create NEW map — not modify existing
        Map<String, String> newConfig = loadFromDatabase();
        cache = Map.copyOf(newConfig); // Atomic replacement via volatile
    }
    
    public String get(String key) {
        return cache.get(key);
    }
}
```

## 8. Advanced Details

- **JMM freeze action**: The Java Memory Model guarantees that `final` fields are visible to all threads after the constructor completes. This is the only way to publish an object safely without synchronization. The JIT inserts a memory barrier after the constructor if `final` fields exist.
- **Deserialization and `final`**: Deserialization can bypass constructors and set `final` fields via `Unsafe` or `ObjectStreamField`. This means a deserialized "immutable" object may have different state. `readResolve()` can protect against this.
- **Reflection and `final`**: `Field.setAccessible(true)` + `Field.set()` can modify `final` fields at runtime (Java 8-). Since Java 9+ (modules), `setAccessible` fails if the class doesn't open its package to the caller. `Unsafe.putObject()` can still modify `final` fields, but this is JVM-internal.
- **String immutability and hash caching**: `String` caches its hash code (`private int hash`). Despite being "immutable", this field is lazily computed and cached — technically a state change, but transparent. This is safe because the cached value is computed from immutable fields.
- **Value types (Project Valhalla)**: Future Java versions may introduce primitive value types (`inline class`) that are immutable by design and avoid object header overhead. This will eliminate the distinction between primitives and wrappers for many use cases.
- **`Collections.unmodifiableList` vs `List.of` vs `List.copyOf`**: `unmodifiableList` is a VIEW — changes to the original list propagate. `List.of()` is truly immutable (throws on any mutation). `List.copyOf()` creates an immutable copy that does NOT change if the original changes.
- **`final` and performance**: The JIT can aggressively optimize `final` fields — they are treated as constant values. `final` methods can be inlined more aggressively. `final` classes enable devirtualization (the JIT can skip vtable lookup for monomorphic calls).

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between `final` and immutability in Java?
A: `final` is a keyword that prevents variable reassignment. If a reference is `final`, it cannot point to a different object, but the object's internal state can still change. Immutability means an object's state cannot change after construction — all fields are `final`, no setters, and mutable fields are defensively copied. A `final List<String>` is NOT immutable (you can add/remove elements). A `String` is immutable (no method can change its internal `char[]`).

### Intermediate
Q: Is this class immutable? Why or why not?
```java
public final class Config {
    private final Map<String, String> settings;
    public Config(Map<String, String> settings) {
        this.settings = settings;
    }
    public Map<String, String> getSettings() {
        return settings;
    }
}
```
A: NO — this class is NOT immutable. Two problems:
1. The constructor does NOT defensively copy `settings`. The caller can retain a reference to the map and modify it after construction: `settings.put("key", "hacked")`.
2. The getter returns the actual mutable reference. Callers can do `config.getSettings().clear()`.

**Fix:**
```java
public Config(Map<String, String> settings) {
    this.settings = Map.copyOf(settings); // defensive copy + immutable
}
public Map<String, String> getSettings() {
    return Map.copyOf(settings); // or return settings (already immutable)
}
```

### Senior
Q: In a high-throughput payment system, you need to publish configuration that changes once per hour. Configuration is read by 100+ threads. Should you use an immutable object or a `final` reference to a mutable object? Explain trade-offs.
A: **Use an immutable object (record or immutable class) with atomic replacement.**

**Immutable approach** (preferred):
```java
public record PaymentConfig(
    int maxRetries,
    BigDecimal maxAmount,
    List<String> supportedCurrencies,
    Duration timeout
) {}
// Replace entirely: config = new PaymentConfig(newRetries, newAmount, ...)
```
- Thread-safe by construction — all fields are `final`, JMM guarantees visibility
- No synchronization needed for reads
- Atomic replacement via `volatile` field or `AtomicReference`

**Mutable + final** (worse):
- If the config object is mutable, reads must synchronize or use `volatile` on each field
- Partial updates: one thread sees `maxRetries=5` but old `timeout=30s`
- Requires explicit locking or `CopyOnWriteArray` patterns

**Recommendation**: Immutable objects with atomic replacement (`volatile` reference or `AtomicReference`). This provides lock-free reads, consistency, and simplicity. Only use mutable + `final` when mutation frequency is extremely high and creating new objects is too expensive.

### Tricky
Q: Consider `final` on a method parameter. Does it affect the caller? Does it affect the method's safety?
```java
void process(final StringBuilder sb) {
    sb.append(" World"); // OK — mutation, not reassignment
    // sb = new StringBuilder("New"); // COMPILE ERROR
}

void caller() {
    StringBuilder sb = new StringBuilder("Hello");
    process(sb);
    System.out.println(sb); // "Hello World"
}
```
A: `final` on a parameter has NO effect on the caller. It only prevents the parameter variable from being reassigned inside the method. It does NOT prevent object mutation. It does NOT affect bytecode (the compiler enforces it at compile time, but the generated bytecode is the same with or without `final`).

Useful for: code clarity (signal "this param won't be reassigned"), and for local anonymous classes (pre-Java 8 required `final` for captured variables). In modern Java, `final` on parameters is a style choice — `effectively final` is sufficient for most cases.

## 10. Final 30-Second Answer

`final` prevents variable REASSIGNMENT, not object mutation. Immutability prevents object STATE change. `final` reference + `final` fields + defensive copies = immutable. `final` primitive = constant. `final` reference + mutable object = mutable. `Collections.unmodifiableList` is a VIEW — changes to original propagate. `List.of()` is truly immutable. Defensively copy mutable objects in both constructors and getters for true immutability.