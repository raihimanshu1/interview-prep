# `equals()` and `hashCode()` Contract — Complete Deep Dive

## 1. Why This Concept Matters

`equals()` and `hashCode()` are among the most frequently tested Java topics. They form a contract that every Java developer must understand to use objects correctly as HashMap keys, in Sets, and in any equality comparison. In production, violating this contract causes subtle bugs: objects that should be equal fail equality checks, HashMap lookups return wrong values, Sets contain duplicate "equal" objects, and cached data becomes inconsistent. Interviewers probe this because it tests your understanding of object identity, the Object class contract, and how collections depend on these methods.

Misunderstanding equals/hashCode causes:
- HashMap returning wrong values for logically equal keys
- HashSet allowing duplicate "equal" objects
- Incorrect behavior when using custom objects as keys
- Broken contracts when subclassing without proper overriding

## 2. Basic Meaning

**`equals()`** determines whether two objects are logically equal (same value/content).
**`hashCode()`** returns an integer hash code used by hash-based collections to bucket objects.

**The Contract (from Object Javadoc):**
1. Reflexive: `x.equals(x)` is always `true`
2. Symmetric: `x.equals(y)` implies `y.equals(x)`
3. Transitive: `x.equals(y)` and `y.equals(z)` implies `x.equals(z)`
4. Consistent: repeated calls return same result if no fields change
5. Non-null: `x.equals(null)` is always `false`

**hashCode contract:**
- If `x.equals(y)` is `true`, then `x.hashCode() == y.hashCode()`
- If `x.equals(y)` is `false`, hashCode MAY be same (collision) or different

Key vocabulary:
- **Identity**: `==` comparison — same memory reference
- **Equality**: `.equals()` comparison — same logical value
- **Hash code**: integer derived from object's state, used for bucket placement
- **Collision**: two unequal objects with same hash code
- **Hash bucket**: slot in HashMap's internal array
- **Canonical fields**: fields used in equality comparison
- **Symmetry**: `a.equals(b)` must equal `b.equals(a)`

What it is NOT: `equals()` is not identity comparison. `hashCode()` does not uniquely identify an object (collisions are allowed and expected).

## 3. Real Code / Real Example

```java
import java.util.*;
import java.util.stream.Collectors;

// === CORRECT IMPLEMENTATION ===
public final class Person {
    private final String name;
    private final int age;
    private final String email;

    public Person(String name, int age, String email) {
        this.name = Objects.requireNonNull(name);
        this.age = age;
        this.email = Objects.requireNonNull(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;           // identity check
        if (!(o instanceof Person)) return false; // type check
        Person that = (Person) o;
        return age == that.age &&             // primitive comparison
               Objects.equals(name, that.name) && // null-safe String comparison
               Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        // Use same canonical fields as equals, in same order
        return Objects.hash(name, age, email);
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", email='" + email + "'}";
    }
}

// === DEMO ===
public class EqualsHashCodeDemo {
    public static void main(String[] args) {
        // === BASIC EQUALITY ===
        Person p1 = new Person("Alice", 30, "alice@example.com");
        Person p2 = new Person("Alice", 30, "alice@example.com");
        Person p3 = new Person("Bob", 25, "bob@example.com");

        System.out.println("p1 == p2: " + (p1 == p2));          // false (different objects)
        System.out.println("p1.equals(p2): " + p1.equals(p2));  // true (same content)
        System.out.println("p1.equals(p3): " + p1.equals(p3));  // false
        System.out.println("p2.equals(p1): " + p2.equals(p1));  // true (symmetric)

        // === HASH CODE CONSISTENCY ===
        System.out.println("p1.hashCode() == p2.hashCode(): " + (p1.hashCode() == p2.hashCode())); // true

        // === HASHMAP WITH PERSON KEYS ===
        Map<Person, String> roles = new HashMap<>();
        roles.put(p1, "ADMIN");
        roles.put(p3, "USER");
        System.out.println("Get p1 role: " + roles.get(p1));   // ADMIN
        System.out.println("Get p2 role: " + roles.get(p2));   // ADMIN (p2 equals p1!)

        // === HASHSET PREVENTS DUPLICATES ===
        Set<Person> people = new HashSet<>();
        people.add(p1);
        people.add(p2); // p2.equals(p1) is true, so NOT added
        people.add(p3);
        System.out.println("Set size (should be 2): " + people.size()); // 2

        // === BROKEN: SUBCLASS WITHOUT OVERRIDE ===
        BrokenEmployee e1 = new BrokenEmployee("Alice", 30, "alice@example.com", "ENG-101");
        BrokenEmployee e2 = new BrokenEmployee("Alice", 30, "alice@example.com", "ENG-102");
        System.out.println("e1.equals(e2): " + e1.equals(e2)); // true (inherited Person.equals)
        System.out.println("e2.equals(e1): " + e2.equals(e1)); // true — symmetric here but wrong semantically

        // === EQUALS WITH NULL ===
        System.out.println("p1.equals(null): " + p1.equals(null)); // false (never NPE)
    }
}

// === BROKEN SUBCLASS — demonstrates LSP violation ===
class BrokenEmployee extends Person {
    private final String employeeId;

    public BrokenEmployee(String name, int age, String email, String employeeId) {
        super(name, age, email);
        this.employeeId = employeeId;
    }

    // Inherits equals() from Person — compares only name, age, email
    // Two employees with same name/age/email but DIFFERENT IDs are "equal"!
}
```

Expected output:
```
p1 == p2: false
p1.equals(p2): true
p1.equals(p3): false
p2.equals(p1): true
p1.hashCode() == p2.hashCode(): true
Get p1 role: ADMIN
Get p2 role: ADMIN
Set size (should be 2): 2
e1.equals(e2): true
e2.equals(e1): true
p1.equals(null): false
```

## 4. What Happens Internally

**HashMap lookup flow with equals/hashCode:**
1. Compute hash: `hash = hash(key.hashCode())`
   - `hash(keyHash) = keyHash ^ (keyHash >>> 16)` (spreads high bits)
2. Compute index: `index = (table.length - 1) & hash`
3. If `table[index]` is null → no entry found, return null
4. If not null → traverse linked list/tree at that bucket:
   - Compare hash first: if `node.hash != hash`, skip
   - If hash matches, call `node.key.equals(key)`
   - If `equals()` returns true → found! Return value.
   - If not → continue to next node

**Why hashCode must match when equals is true:**
If `a.equals(b)` is true but `a.hashCode() != b.hashCode()`:
1. `a` placed in bucket X
2. `b` placed in bucket Y (different hash)
3. `map.get(a)` looks in bucket X — finds `a`
4. `map.get(b)` looks in bucket Y — does NOT find `a` (wrong bucket!)
5. HashMap considers `a` and `b` as different keys even though they're equal

**`Objects.equals()` vs `==` for null-safe comparison:**
```java
// Manual null-safe
if (name != null && name.equals(that.name)) { ... }

// Using Objects.equals()
Objects.equals(name, that.name) // handles null on both sides
```

`Objects.equals(a, b)` internally:
```java
return (a == b) || (a != null && a.equals(b));
```

## 5. Tricky Interview Cases

**Case 1 — Symmetry violation with inheritance**
```java
class Point {
    int x, y;
    @Override public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return x == p.x && y == p.y;
    }
    @Override public int hashCode() { return Objects.hash(x, y); }
}

class ColorPoint extends Point {
    Color color;
    @Override public boolean equals(Object o) {
        if (!(o instanceof ColorPoint)) return false;
        return super.equals(o) && Objects.equals(color, ((ColorPoint)o).color);
    }
}

Point p = new Point(1, 2);
ColorPoint cp = new ColorPoint(1, 2, Color.RED);
System.out.println(p.equals(cp));    // true (Point.equals sees same x,y)
System.out.println(cp.equals(p));    // false (ColorPoint.equals sees null color)
```
Output: `true` then `false`
Explanation: Symmetry violation! `p.equals(cp)` uses `Point.equals` (checks x,y only → true). `cp.equals(p)` uses `ColorPoint.equals` (checks x,y AND color → false because `p.color` is null). This breaks the contract.

Fix: Use `getClass()` instead of `instanceof` in `equals()` to prevent cross-class equality:
```java
if (getClass() != o.getClass()) return false;
```

**Case 2 — hashCode() not overridden**
```java
class BadPerson {
    String name;
    int age;
    // equals() overridden but hashCode() NOT overridden
    @Override public boolean equals(Object o) { ... }
}

Map<BadPerson, String> map = new HashMap<>();
BadPerson p1 = new BadPerson("Alice", 30);
BadPerson p2 = new BadPerson("Alice", 30);
map.put(p1, "ADMIN");
System.out.println(map.get(p2)); // null! p2 not found
```
Output: `null`
Explanation: `p1.equals(p2)` is true, but `p1.hashCode() != p2.hashCode()` because `BadPerson` inherits `Object.hashCode()` (identity hash based on memory address). Different objects → different hash codes → different buckets → `get(p2)` returns null.

**Case 3 — Mutable fields in equals/hashCode**
```java
class MutableKey {
    String key;
    MutableKey(String k) { this.key = k; }
    @Override public boolean equals(Object o) { return (o instanceof MutableKey) && key.equals(((MutableKey)o).key); }
    @Override public int hashCode() { return key.hashCode(); }
}

Map<MutableKey, String> map = new HashMap<>();
MutableKey k1 = new MutableKey("A");
map.put(k1, "value1");
k1.key = "B"; // mutate after insertion
System.out.println(map.get(new MutableKey("A"))); // null — key "A" lost!
System.out.println(map.get(k1));                 // null — k1 hash changed, wrong bucket
System.out.println(map.size());                  // 1 but effectively lost
```
Output: `null`, `null`, `1`
Explanation: `key` changed after insertion. HashMap stored entry in bucket for hash("A"). Now `k1.key = "B"`, `k1.hashCode()` returns hash("B"). Lookup with `"A"` looks in old bucket (empty). Lookup with `k1` looks in new bucket (empty). Entry exists but unreachable.

**Case 4 — Transient fields and equals**
```java
class Session implements Serializable {
    private String sessionId;
    private transient long created = System.currentTimeMillis();
    @Override public boolean equals(Object o) {
        if (!(o instanceof Session)) return false;
        Session s = (Session) o;
        return sessionId.equals(s.sessionId); // only compare sessionId
    }
    @Override public int hashCode() { return Objects.hash(sessionId); }
}
```
Output: Deserialized `Session` with same `sessionId` is equal. `created` is transient (not serialized) but also not in `equals`.
Explanation: Correct: `equals`/`hashCode` should only use persistent/serializable fields. `transient` fields excluded from both. If you included `created`, deserialized objects would never be equal (different `created` values).

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| No `@Override` on equals/hashCode | Compiler doesn't catch signature mismatch | Always use `@Override` |
| Using `==` in equals | Compares references, not values | Use `.equals()` or `Objects.equals()` |
| Not using same fields in hashCode | Violates contract (equal objects different hash) | Use same canonical fields in both |
| Mutable fields in equals/hashCode | HashMap corruption after mutation | Use immutable fields only |
| Inheritance + `instanceof` in equals | Symmetry violation with subclasses | Use `getClass()` or make class final |
| Forgetting null check in equals | NPE when comparing to null | `if (!(o instanceof Type)) return false` handles it |
| equals on arrays | `Arrays.equals()` needed, not `==` | Use `Arrays.equals(arr1, arr2)` in equals |

## 7. Production Usage

**JPA/Hibernate entities:**
```java
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;    // immutable after persist
    private String email;
    private String name;

    // equals/hashCode based on BUSINESS KEY (natural key)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email); // email is unique business key
    }

    @Override
    public int hashCode() {
        return Objects.hash(email); // only email — stable, unique
    }
}
```
Use business key (natural unique field) for equals, NOT generated ID (null before persist). JPA requires ID for identity but it's not suitable for equals before persistence.

**Jackson JSON deserialization:**
```java
public record Product(String sku, String name, BigDecimal price) { }
// Record auto-generates equals/hashCode using all fields
```
Immutability ensures hashCode stability. Records are safe as HashMap keys.

**Caching with custom keys:**
```java
public record CacheKey(Long userId, String feature) { }
// Use as HashMap key — immutable, correct equals/hashCode
Map<CacheKey, UserConfig> cache = new HashMap<>();
cache.put(new CacheKey(1L, "dark-mode"), config);
```

**Spring bean identity:**
```java
// Spring beans are singletons by default
// equals/hashCode rarely needed for beans themselves
// But for @RequestScoped beans shared across filters:
@Component
@RequestScope
public class RequestContext {
    private String requestId;
    // equals/hashCode if storing in HashMap within request
}
```

## 8. Advanced Details

- **`Objects.hash()` vs manual hash:** `Objects.hash(a, b, c)` creates array and uses `Arrays.hashCode()`. Slightly slower but convenient. Manual: `int result = 31 * result + fieldHash`.
- **Prime 31 in hashCode:** Traditional practice: `result = 31 * result + fieldHash`. 31 is odd prime, allows bit-shift optimization (`31 * i = (i << 5) - i`).
- **`System.identityHashCode()`:** Returns hash based on identity (memory address), ignoring `hashCode()` override. Used by `IdentityHashMap`. Useful for object identity tracking.
- **`IdentityHashMap`:** Uses `==` for equality, `System.identityHashCode()` for hash. Rarely needed. Used by `IdentityHashMap` or `System.identityHashCode`.
- **Guava `Objects.equals()` vs `Objects.hashCode()`:** Pre-Java 7, Guava provided these. Java 7+ `Objects.equals()` and `Objects.hash()` in `java.util`.
- **Value-based equality (records, `Valueable` interface):** Future Java may introduce `value-based` classes. Records are value-based: equality based on state, not identity.
- **`record` classes:** Auto-generate `equals()`/`hashCode()` using all components. Cannot customize without writing custom methods.
- **`Enum` equals/hashCode:** Enums have final `equals()` (identity-based, `==`) and `hashCode()` based on identity. Do NOT override enum equals.

## 9. Interview Questions And Answers

### Beginner
Q: What is the relationship between `equals()` and `hashCode()`? Why must equal objects have equal hash codes?
A: If `a.equals(b)` is `true`, both objects represent the same logical value. For hash-based collections like HashMap to work correctly, logically equal objects must map to the same bucket. If `a.equals(b)` but `a.hashCode() != b.hashCode()`, `a` and `b` go to different buckets. `map.get(b)` would not find `a` even though they're equal. This breaks the fundamental contract of hash-based collections.

### Intermediate
Q: You have a `Person` class with `equals()` comparing `name` and `age`. You put a `Person` in a `HashSet`, then mutate the `name` field. What happens?
A: The object's hashCode changes after insertion. HashSet stores the object in a bucket based on its hashCode at insertion time. After mutation:
- `set.contains(originalPerson)` computes new hashCode → different bucket → returns `false`
- The object is "lost" in the set — present but unreachable
- This is why mutable fields should not be used in `equals`/`hashCode`

Fix: Use only immutable fields in `equals`/`hashCode`, or don't mutate objects stored in hash collections.

### Senior
Q: A teammate overrides `equals()` in `Employee extends Person` using `instanceof`. During a code review, you find `Person p = new Employee(...)` and `Employee e = new Person(...)` can never be equal even if they have same name/age. Is this a bug? Explain the symmetry violation and propose the correct implementation.
A: Yes, this violates the `equals()` symmetry contract.

With `instanceof`:
- `p.equals(e)`: `p` is `Person`, checks `e instanceof Person` → true → compares name/age → true
- `e.equals(p)`: `e` is `Employee`, checks `p instanceof Employee` → false → false

Result: `p.equals(e) != e.equals(p)` — symmetry broken.

Correct implementation using `getClass()`:
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false; // SAME CLASS ONLY
    Person person = (Person) o;
    return age == person.age && Objects.equals(name, person.name);
}
```

`getClass() != o.getClass()` ensures only exact same class can be equal. `Person` never equals `Employee`, even if fields match. This preserves symmetry.

Tradeoff: `getClass()` is strict — `new Person("Alice", 30)` equals `new Person("Alice", 30)` but NOT `new Employee("Alice", 30)`. `instanceof` allows cross-class equality but risks symmetry violations. Choose based on requirements:
- Value objects (Money, Point): use `getClass()`
- Domain entities with natural keys: `instanceof` acceptable if no subclassing

### Tricky
Q: Explain why `java.util.Objects.equals(a, b)` is preferred over `a.equals(b)` in production code. Demonstrate with a concrete asymmetric case where `a.equals(b)` throws NPE.
A: `Objects.equals(a, b)` is null-safe:
```java
public static boolean equals(Object a, Object b) {
    return (a == b) || (a != null && a.equals(b));
}
```

If `a` is null:
- `a.equals(b)` → `NullPointerException`
- `Objects.equals(a, b)` → returns `b == null` safely

Concrete case:
```java
String config = null;
String defaultConfig = "production";

// BAD: NPE if config is null
if (config.equals(defaultConfig)) { ... } // NPE!

// GOOD: null-safe
if (Objects.equals(config, defaultConfig)) { ... } // false, no NPE

// ALSO GOOD: constant first
if ("production".equals(config)) { ... } // false, no NPE
```

In production: API responses, database lookups, caches often return null. Always use `Objects.equals()` or call `.equals()` on known-non-null constant.

## 10. Final 30-Second Answer

`equals()` = logical equality (same value). `hashCode()` = bucket index for hash collections. **Contract**: if `equals()` is true, hashCode MUST be equal. Violating this breaks HashMap/HashSet. Implementation: `@Override` both, use `Objects.equals()` for null-safe comparison, use `Objects.hash()` for hashCode. Same canonical fields in both methods. Make fields used in equality `final` and immutable. `getClass()` in equals prevents symmetry violation from inheritance. Never use mutable fields in `equals`/`hashCode`. Records auto-generate both correctly.