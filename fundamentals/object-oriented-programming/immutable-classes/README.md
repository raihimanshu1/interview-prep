# Immutable Classes — Complete Deep Dive

## 1. Why This Concept Matters

Immutable objects are thread-safe by construction, cacheable, and secure. They form the backbone of value objects in Domain-Driven Design, message keys in Kafka, and DTOs in REST APIs. In production, mutable objects shared across threads cause race conditions, inconsistent state, and hard-to-reproduce bugs. Understanding how to create truly immutable classes — and when immutability is overkill — is essential for writing robust Java code. Interviewers test this because it combines OOP principles, defensive copying, the `final` keyword, and memory visibility.

Misunderstanding immutability causes:
- Thread-safety bugs from shared mutable state
- Security vulnerabilities when mutable objects are exposed as API responses
- Incorrect use of `String` (immutable) vs `StringBuilder` (mutable)
- Broken invariants when collections or mutable fields leak

## 2. Basic Meaning

An immutable object is one whose state cannot be changed after construction. All fields are `final`, the class is `final` (or constructor is private), there are no setters, and any mutable fields are defensively copied on input and output.

Key vocabulary:
- **Immutable**: state cannot change after construction
- **`final` class**: cannot be subclassed (prevents method override attacks)
- **`final` field**: assigned once, never changed
- **Defensive copy**: creating a copy of a mutable input to prevent external modification
- **Unmodifiable view**: `Collections.unmodifiableList()` prevents modifications through reference
- **Sharing / Flyweight**: immutable objects can be freely shared without copying
- **Value object**: DDD concept where equality is based on value, not identity

What it is NOT: Immutability does not mean the object is serializable, cloneable, or that all referenced objects are also immutable. It also does not mean the object cannot hold references to mutable objects — just that those references themselves do not change and the objects are not exposed.

## 3. Real Code / Real Example

```java
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// === IMMUTABLE CLASS — CORRECT IMPLEMENTATION ===
public final class Money {           // final: cannot subclass
    private final BigDecimal amount;  // final: never reassigned
    private final Currency currency;  // final: never reassigned
    private final List<String> tags;  // final reference, but List is mutable!

    public Money(BigDecimal amount, Currency currency, List<String> tags) {
        // Defensive copy for BigDecimal (immutable, but prevent reference sharing)
        this.amount = Objects.requireNonNull(amount, "amount");
        this.currency = Objects.requireNonNull(currency, "currency");
        // Defensive copy for List (mutable!)
        this.tags = Collections.unmodifiableList(new ArrayList<>(tags));
    }

    // No setters — only getters returning defensive copies or unmodifiable views
    public BigDecimal getAmount() { return amount; }          // BigDecimal is immutable
    public Currency getCurrency() { return currency; }        // Currency is immutable
    public List<String> getTags() { return tags; }            // unmodifiable view

    // Operations return NEW instances
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amount.add(other.amount), currency, tags);
    }

    public Money withTag(String tag) {
        List<String> newTags = new ArrayList<>(this.tags);
        newTags.add(tag);
        return new Money(amount, currency, newTags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money that)) return false;
        return amount.equals(that.amount) && currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return "Money{" + amount + " " + currency + ", tags=" + tags + '}';
    }
}

// === MUTABLE CLASS — FOR COMPARISON ===
class MutableMoney {
    public BigDecimal amount;
    public Currency currency;
    public List<String> tags;
}

// === DEMO ===
class ImmutableDemo {
    public static void main(String[] args) {
        // Create immutable instance
        Money m1 = new Money(new BigDecimal("100.50"), Currency.getInstance("USD"),
                             List.of("salary", "january"));
        System.out.println("Original: " + m1);

        // Try modifying through getter — fails on unmodifiable list
        try {
            m1.getTags().add("bonus");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify tags: " + e);
        }

        // Add operation returns NEW instance
        Money m2 = m1.add(new Money(new BigDecimal("50.25"), Currency.getInstance("USD"),
                                     List.of("bonus")));
        System.out.println("After add: " + m2);

        // With tag returns NEW instance
        Money m3 = m1.withTag("processed");
        System.out.println("After withTag: " + m3);
        System.out.println("Original unchanged: " + m1);

        // Mutable version — dangerous
        MutableMoney mm = new MutableMoney();
        mm.amount = new BigDecimal("100");
        MutableMoney mmRef = mm;           // same reference
        mmRef.amount = new BigDecimal("999"); // modifies through ref!
        System.out.println("Mutable changed via ref: " + mm.amount); // 999!
    }
}
```

Expected output:
```
Original: Money{100.50 USD, tags=[salary, january]}
Cannot modify tags: java.lang.UnsupportedOperationException
After add: Money{150.75 USD, tags=[salary, january, bonus]}
After withTag: Money{100.50 USD, tags=[salary, january, processed]}
Original unchanged: Money{100.50 USD, tags=[salary, january]}
Mutable changed via ref: 999
```

## 4. What Happens Internally

**Construction and initialization:**
1. Caller invokes `new Money(amount, currency, tags)`
2. Constructor validates inputs via `Objects.requireNonNull()`
3. Defensive copies created:
   - `tags` list copied via `new ArrayList<>(tags)` — new backing array
   - Wrapped in `Collections.unmodifiableList()` — any mutation throws `UnsupportedOperationException`
4. `final` fields assigned exactly once
5. Object published to caller

**Memory visibility:**
- `final` fields have special JMM guarantees: once constructor finishes, any thread reading the object sees the fully constructed state without additional synchronization.
- Without `final`, a thread might see partially constructed object (default values for fields not yet written).

**Defensive copy rationale:**
```java
// Without defensive copy — caller retains reference to internal list
public List<String> getTags() { return tags; } // BAD: caller can modify via this ref
// Fix 1: unmodifiable view
public List<String> getTags() { return Collections.unmodifiableList(tags); }
// Fix 2: defensive copy on return
public List<String> getTags() { return new ArrayList<>(tags); }
```

**`Collections.unmodifiableList()` vs copy:**
- Unmodifiable: throws on modification, but if caller retains original mutable list reference passed to constructor, they can still modify. But since constructor copied it, internal list is safe.
- Defensive copy: caller gets independent copy. Safer but allocates more.

**Why `final` on class:**
- Prevents subclassing attacks: malicious subclass overrides `equals()` to break contract, or introduces mutable state via new fields.

## 5. Tricky Interview Cases

**Case 1 — Immutable with mutable field**
```java
final class Money {
    private final BigDecimal amount;
    private final List<String> tags;
    public Money(BigDecimal amount, List<String> tags) {
        this.amount = amount;
        this.tags = tags; // NO defensive copy!
    }
    public List<String> getTags() { return tags; }
}
```
Output: Caller can modify `tags` after insertion.
```java
Money m = new Money(BigDecimal.TEN, new ArrayList<>());
m.getTags().add("x"); // modifies internal state!
```
Explanation: Missing defensive copy in constructor. Caller holds reference to same mutable list. Fix: `this.tags = new ArrayList<>(tags);` or unmodifiable.

**Case 2 — `StringBuilder` passed to immutable class**
```java
final class Message {
    private final StringBuilder text;
    public Message(StringBuilder text) { this.text = text; }
}
Message m = new Message(new StringBuilder("hello"));
m.getText().append(" world"); // modifies internal state!
```
Output: Internal `text` modified externally.
Explanation: `StringBuilder` is mutable. Even though field is `final`, the referenced object's content can change. Fix: `this.text = new StringBuilder(text);` or accept `String` (immutable) instead.

**Case 3 — Immutability broken by `transient` / serialization**
```java
final class Session implements Serializable {
    private final String userId;
    private final transient Date created; // mutable, excluded from serialization
    public Session(String userId, Date created) {
        this.userId = userId;
        this.created = new Date(created.getTime()); // defensive copy
    }
}
```
Output: Deserialized `Session` has `created = null` (transient not serialized).
Explanation: `transient` fields are not serialized. If class claims immutable but relies on `transient` state, it is not fully immutable across serialization boundaries. Fix: reconstruct `Date` in `readObject()`.

**Case 4 — Collections.unmodifiableList is view, not copy**
```java
class Box {
    private final List<String> items;
    Box(List<String> items) {
        this.items = Collections.unmodifiableList(items);
    }
    List<String> getItems() { return items; }
}
List<String> original = new ArrayList<>(List.of("a"));
Box box = new Box(original);
original.add("b"); // modifies through original ref!
System.out.println(box.getItems()); // [a, b] — modified!
```
Output: `[a, b]`
Explanation: `unmodifiableList` wraps the original list. Mutating original list still reflects in view. Fix: `this.items = Collections.unmodifiableList(new ArrayList<>(items));` — copy first, then wrap.

**Case 5 — `equals` and `hashCode` with mutable fields**
```java
final class User {
    private final String name;
    private final List<String> roles; // mutable list
}
User u1 = new User("alice", new ArrayList<>(List.of("ADMIN")));
User u2 = new User("alice", new ArrayList<>(List.of("ADMIN")));
System.out.println(u1.equals(u2)); // true
u2.getRoles().add("USER");         // via unmodifiable view? if copy: no effect
System.out.println(u1.equals(u2)); // depends on getRoles() safety
```
Output: First `true`. If `getRoles()` returns copy: second `true` (equal). If returns unmodifiable view wrapping original mutable list: if original mutable list passed, second could be `false` if copy was defensive.
Explanation: Mutable fields in `equals` cause contracts to break if state changes after insertion in HashSet/HashMap. `equals` should be based on immutable significant fields only.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Missing `final` on class | Subclass can override methods, break immutability | Declare class `final` or make constructor private with factory |
| No defensive copy for mutable params | Caller retains reference to internal state | Copy in constructor, return copy/unmodifiable in getter |
| Exposing internal mutable collection via getter | Caller modifies collection | Return `Collections.unmodifiableList(new ArrayList<>(list))` |
| Using setters | Allows state mutation | Remove setters; use `withX()` returning new instance |
| `transient` mutable fields | Not serialized, breaks immutability across deserialization | Reconstruct in `readObject()` or avoid transient |
| Overriding `equals` with mutable field | HashMap key corruption when field changes | Base `equals`/`hashCode` only on immutable fields |
| Getters returning direct mutable references | External modification possible | Copy or unmodifiable view |

## 7. Production Usage

**Money / Value Object in DDD:**
```java
// Immutable value object — thread-safe, hashable, safe to share
public record Money(BigDecimal amount, Currency currency) { }
// Java 16+ record is implicitly final, final fields, equals/hashCode/toString auto-generated
```

**Event / Message in Kafka:**
```java
// Kafka message keys and values should be immutable for safe sharing
public record PaymentEvent(String eventId, Long orderId, BigDecimal amount, Instant timestamp) { }
// Kafka serializes/deserializes; immutability ensures no accidental mutation between producer and consumer
```

**Spring Boot configuration properties:**
```java
// @ConfigurationProperties beans should be immutable
@ConfigurationProperties(prefix = "datasource")
public record DataSourceConfig(String url, String username, String password, int poolSize) { }
// Spring Boot 3+ supports constructor binding for records
```

**JSON DTO with Jackson:**
```java
// Immutable DTO — Jackson deserializes via constructor
public record UserResponse(Long id, String name, String email) { }
// @JsonCreator not needed for records in Jackson 2.12+
```

**Cache keys:**
```java
// Immutable objects are safe as HashMap keys
Map<Money, Long> priceCache = new HashMap<>();
// Money's hashCode/equals stable — safe for caching
```

## 8. Advanced Details

- **Records (Java 14+):** Records are implicitly `final`, all fields `private final`, equals/hashCode/toString auto-generated. Not a replacement for all immutable classes (cannot inherit, cannot have non-trivial validation in constructor without compact canon constructor).
- **Serialization proxy pattern:** For immutable serializable classes, `writeReplace()` can return a serializable proxy that restores immutability in `readObject()`.
- **JSON deserialization of immutable objects:** Jackson needs `@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)` on constructor for immutable DTOs with `@JsonProperty` annotations.
- **Shallow vs deep immutability:** Shallow: all fields `final` and assigned from unmodifiable copies. Deep: all referenced objects are also immutable (e.g., `String`, `BigDecimal`, `LocalDate`).
- **Performance:** Immutability trades allocation cost for thread safety. In high-throughput systems, consider pooling immutable objects or using mutable builders internally that produce immutable results.
- **Guava `ImmutableList`:** `ImmutableList.copyOf(list)` returns truly immutable list (fails on any mutation attempt). Prefer over `Collections.unmodifiableList` when you control the source.
- **`var` and immutability:** `var` infers type but does NOT make variable effectively final. Use `var` with `final` for local variable immutability.

## 9. Interview Questions And Answers

### Beginner
Q: What makes a class immutable in Java?
A: An immutable class has all `private final` fields, no setters, class declared `final`, and defensive copies for any mutable inputs (like collections or `Date`). Once constructed, its state cannot change. Examples: `String`, `Integer`, `LocalDate`, `BigDecimal`.

### Intermediate
Q: How do you make a class with a `List<String>` field immutable? Why is returning `Collections.unmodifiableList()` not always enough?
A: Three steps:
1. Constructor takes `List<String>`, copies it: `this.tags = new ArrayList<>(tags);`
2. Returns unmodifiable view in getter: `return Collections.unmodifiableList(tags);`
3. No methods that modify the list.

`Collections.unmodifiableList()` alone is not enough because if the caller retains the original mutable list reference, they can still modify the backing list. The constructor must copy to break the reference.

### Senior
Q: You are designing a payment processing system. `Payment` entities are accessed by thousands of threads concurrently. You currently use JPA `@Entity` with mutable fields and getters/setters. What are the risks, and how would you redesign for immutability?
A: Risks: JPA requires no-arg constructor and mutable fields. Entities are mutable by design. Concurrent threads can read partially updated state if transaction isolation is low. Lazy-loaded associations can trigger `LazyInitializationException`.

Redesign options:
1. **Immutable command model:** Create immutable `PaymentCommand` objects for requests. JPA entity stays mutable internally but is not exposed to business logic.
2. **Event-sourced payments:** `PaymentCreated`, `PaymentCharged` events are immutable facts. State rebuilt by replaying events. Naturally thread-safe.
3. **Versioned optimistic locking:** `@Version` field in JPA. Concurrent updates fail with `OptimisticLockException`. Combined with immutable DTOs for API layer.
4. **CQRS:** Separate write model (JPA mutable, transactionally consistent) from read model (immutable projection, eventually consistent).

Tradeoff: JPA does not support immutable entities well (no setters breaks ORM). Use immutability at service/API boundaries, not at ORM layer.

### Tricky
Q: `String` is immutable, yet `String s = "hello"; s.toUpperCase(); System.out.println(s);` prints `"hello"`, not `"HELLO"`. Explain how immutability is enforced at the JVM level, and why `final` fields are insufficient without it.
A: `String` is `final` class with `private final char[]` (Java 8) or `private final byte[]` (Java 9+). All methods like `toUpperCase()` create and return a NEW `String`. The original `s` still references the original `"hello"` in the pool or heap.

`final` on class prevents subclassing. `final` on `value` field prevents reassignment. But `final` alone does not prevent mutation of referenced objects:

```java
class Broken {
    final byte[] data; // final reference, but byte[] is mutable
    Broken(byte[] data) { this.data = data; }
}
```
Here reference cannot change, but `data[0] = 0` mutation is possible. String prevents this by never exposing internal array and copying it when needed (e.g., `substring()` in Java 7+ copies array).

This is why immutability requires BOTH `final` fields AND defensive copies for ALL mutable objects.

## 10. Final 30-Second Answer

Immutable class = state cannot change after construction. `final` class, `final` fields, no setters, defensive copies for mutable inputs. `equals`/`hashCode` on immutable fields only. Thread-safe by construction, safe to share. Java `record` (14+) simplifies immutable DTOs. Use for value objects, event messages, cache keys, DTOs. Be careful with mutable field references — copy in constructor, return unmodifiable in getters. `final` reference ≠ immutable object.