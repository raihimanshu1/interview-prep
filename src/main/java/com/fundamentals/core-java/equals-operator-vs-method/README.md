# == vs equals() — Complete Deep Dive

## 1. Why This Concept Matters

The `==` operator and `equals()` method are the most common source of comparison bugs in Java. `==` compares **references** (memory addresses) for objects — it checks if two variables point to the exact same object. `equals()` compares **contents** (logical equality) — it checks if two objects are meaningfully equivalent. Every senior Java interview includes questions about this distinction, especially combined with String interning, Integer caching, and custom `equals()` implementations. This concept reveals whether you truly understand object identity versus object equality.

Misunderstanding causes:
- String comparison with `==` returning false even for identical strings
- Integer wrapper comparison with `==` working for values -128 to 127 but failing beyond
- Custom objects not working in HashSet/HashMap because `equals()` is not overridden
- Subtle bugs when using `==` with enum constants (works, but `equals()` is safer)

## 2. Basic Meaning

**`==` operator:** compares primitive values (int, char, boolean) OR object references (memory addresses). For objects, `==` returns `true` only if both references point to the **same object instance**.

**`equals()` method:** defined in `Object` class — by default behaves like `==`. But classes override it to provide **logical equality**: `String`, `Integer`, `Date`, all wrapper classes, and custom classes can override it.

**The contract of `equals()` (from Java docs):**
- **Reflexive**: `x.equals(x)` → true
- **Symmetric**: `x.equals(y)` == `y.equals(x)`
- **Transitive**: if `x.equals(y)` and `y.equals(z)`, then `x.equals(z)`
- **Consistent**: multiple calls return same result (if no modifications)
- **Non-null**: `x.equals(null)` → false

**`equals()` vs `hashCode()` contract:**
- If `x.equals(y)` → `x.hashCode() == y.hashCode()` (MUST hold)
- If `x.hashCode() == y.hashCode()` → `x.equals(y)` may be true OR false (collisions allowed)
- **Violation**: if you override `equals()` without `hashCode()`, HashMap/HashSet break

## 3. Real Code / Real Example

```java
import java.util.*;

public class EqualsOperatorVsMethodDemo {
    
    public static void main(String[] args) {
        // === 1. PRIMITIVES: == works fine ===
        int a = 10, b = 10;
        System.out.println("primitive ==: " + (a == b)); // true
        
        // === 2. STRING: == vs equals() ===
        String s1 = "hello";           // String literal → goes to String pool
        String s2 = "hello";           // Same literal → same pool reference
        String s3 = new String("hello"); // New object on heap (NOT in pool)
        String s4 = s3.intern();        // Returns pool reference
        
        System.out.println("s1 == s2: " + (s1 == s2));           // true (same pool object)
        System.out.println("s1 == s3: " + (s1 == s3));           // false (pool vs heap)
        System.out.println("s1.equals(s3): " + s1.equals(s3));   // true (same content)
        System.out.println("s1 == s4: " + (s1 == s4));           // true (s4 interned → pool)
        
        // === 3. INTEGER CACHE: == trap ===
        Integer i1 = 100;  // valueOf() → cached
        Integer i2 = 100;
        Integer i3 = 200;  // valueOf() → NOT cached (outside -128..127)
        Integer i4 = 200;
        
        System.out.println("i1 == i2: " + (i1 == i2)); // true (cached)
        System.out.println("i3 == i4: " + (i3 == i4)); // false (not cached!)
        System.out.println("i3.equals(i4): " + i3.equals(i4)); // true (correct comparison)
        
        // === 4. CUSTOM CLASS: without equals() ===
        Person p1 = new Person("Alice", 30);
        Person p2 = new Person("Alice", 30);
        Person p3 = p1;
        
        System.out.println("p1 == p2: " + (p1 == p2));             // false (different objects)
        System.out.println("p1.equals(p2): " + p1.equals(p2));      // false if not overridden! (uses Object.equals = ==)
        System.out.println("p1 == p3: " + (p1 == p3));             // true (same reference)
        
        // === 5. CUSTOM CLASS: WITH equals() ===
        Employee e1 = new Employee("Bob", "E123");
        Employee e2 = new Employee("Bob", "E123");
        Employee e3 = new Employee("Bob", "E456");
        
        System.out.println("e1.equals(e2): " + e1.equals(e2)); // true (by id)
        System.out.println("e1.equals(e3): " + e1.equals(e3)); // false (different id)
        
        // === 6. HASHMAP WITH equals/hashCode ===
        Map<Employee, String> map = new HashMap<>();
        map.put(new Employee("Alice", "E001"), "Engineer");
        System.out.println("map.get: " + map.get(new Employee("Alice", "E001"))); 
        // "Engineer" if equals+hashCode correct, null if not!
        
        // === 7. ENUM: == is safe ===
        Color c1 = Color.RED;
        Color c2 = Color.RED;
        System.out.println("enum ==: " + (c1 == c2)); // true (enum constants are singletons)
        
        // === 8. NULL SAFETY ===
        String nullStr = null;
        // System.out.println(nullStr.equals("test")); // NullPointerException!
        System.out.println("test".equals(nullStr));     // false (safe — calls on known object)
        System.out.println(Objects.equals(nullStr, "test")); // false (null-safe utility)
    }
}

class Person {
    String name; int age;
    Person(String name, int age) { this.name = name; this.age = age; }
    // NO equals() override — uses Object.equals() = ==
}

class Employee {
    String name; String id;
    Employee(String name, String id) { this.name = name; this.id = id; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                     // Same reference
        if (o == null || getClass() != o.getClass()) return false; // Null/type check
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);         // Compare by business key
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id); // Must match equals() fields
    }
}

enum Color { RED, GREEN, BLUE }
```

Expected output:
```
primitive ==: true
s1 == s2: true
s1 == s3: false
s1.equals(s3): true
s1 == s4: true
i1 == i2: true
i3 == i4: false
i3.equals(i4): true
p1 == p2: false
p1.equals(p2): false
p1 == p3: true
e1.equals(e2): true
e1.equals(e3): false
map.get: Engineer
enum ==: true
test.equals(nullStr): false
Objects.equals(nullStr, "test"): false
```

## 4. What Happens Internally

### `equals()` Contract Violation — Step by Step

**Why `equals()` and `hashCode()` must be overridden together:**
```java
// Without hashCode() override:
class BadPerson {
    String name;
    BadPerson(String name) { this.name = name; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BadPerson)) return false;
        return Objects.equals(name, ((BadPerson) o).name);
    }
    // NO hashCode() override — inherits Object.hashCode()
}

Map<BadPerson, String> map = new HashMap<>();
map.put(new BadPerson("Alice"), "Engineer");
System.out.println(map.get(new BadPerson("Alice"))); // null!
```

**Why? HashMap bucket logic — step by step:**
```
put(new BadPerson("Alice"), "Engineer"):
  → badPerson1.hashCode() = 12345 (Object's native hash)
  → bucket[12345 % 16] = entry(badPerson1 → "Engineer")

get(new BadPerson("Alice")):
  → badPerson2.hashCode() = 67890 (DIFFERENT! Different Object.hashCode())
  → bucket[67890 % 16] = ????? different bucket!
  → Returns null even though badPerson2.equals(badPerson1) is true!
```

**Fix:** Override `hashCode()` to use the same fields as `equals()`:
```java
@Override
public int hashCode() {
    return Objects.hash(name); // Must use same fields as equals()
}
```
Now both objects produce the same hash → same bucket → `equals()` finds the match.

### JVM `==` Operator Implementation

The `==` operator is implemented at the bytecode level as:
- `if_icmpne` / `if_icmpeq` for integer primitives — compares 32-bit values directly
- `lcmp` for long primitives — compares 64-bit values
- `if_acmpne` / `if_acmpeq` for object references — compares 32-bit/64-bit reference pointers
- `dcmpg` / `dcmpl` for double, `fcmpg` / `fcmpl` for float

There is NO method dispatch for `==`. It is a single bytecode instruction. This is why `==` is faster than `equals()` — `equals()` requires virtual method dispatch, which involves vtable lookup.

### String Pool Intern Mechanism

```java
String s1 = "hello";              // String literal → interned in String pool
String s2 = "hello";              // Same literal → same pool reference
String s3 = new String("hello");  // Heap object (NOT in pool)
String s4 = s3.intern();          // Looks up pool → finds s1's reference → returns it

// Pool is in the heap (PermGen before Java 7, heap since Java 7+)
// Managed by StringTable (a HashMap-like structure)
// Default size: 1009 buckets (configurable via -XX:StringTableSize)
```

## 5. Tricky Interview Cases

**Case 1 — `equals()` with inheritance breaks symmetry**
```java
class Person {
    protected String name;
    Person(String name) { this.name = name; }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Person)) return false;
        return name.equals(((Person) o).name);
    }
}

class Employee extends Person {
    private String employeeId;
    Employee(String name, String id) { super(name); this.employeeId = id; }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Employee)) return false;
        return super.equals(o) && employeeId.equals(((Employee) o).employeeId);
    }
}

Person p = new Person("Alice");
Employee e = new Employee("Alice", "E001");
System.out.println(p.equals(e)); // true (Person accepts any Person)
System.out.println(e.equals(p)); // false (Employee rejects non-Employee)
// SYMMETRY VIOLATED! p.equals(e) = true, e.equals(p) = false
```
Fix: Prefer composition over inheritance for equals. Or use `getClass()` instead of `instanceof`.

**Case 2 — Lombok `@Data` with inheritance**
```java
@Data
class Base {
    private int id;
}

@Data
class Derived extends Base {
    private String extra;
}

// Lombok generates:
// Base.equals(): compares id (but does NOT call super.equals — Object's ==)
// Derived.equals(): compares extra AND calls super.equals() which compares id
// BUT: Base's equals() does NOT check extra → violates symmetry again!
```
Fix: Lombok recommends not using `@Data` / `@EqualsAndHashCode` across inheritance boundaries. Use `@EqualsAndHashCode(callSuper = true)` carefully.

**Case 3 — HashMap key mutation (mutable field used in equals)**
```java
class MutableKey {
    int id;
    MutableKey(int id) { this.id = id; }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof MutableKey && this.id == ((MutableKey) o).id;
    }
    
    @Override
    public int hashCode() { return Objects.hash(id); }
}

Map<MutableKey, String> map = new HashMap<>();
MutableKey key = new MutableKey(1);
map.put(key, "value");

key.id = 2; // MUTATED the key! hash changes!
System.out.println(map.get(key)); // null! (hash changed → wrong bucket)
System.out.println(map.get(new MutableKey(1))); // null! (key.id is now 2)
```
Fix: Use IMMUTABLE keys in HashMap. If mutation is unavoidable, remove before mutation and re-insert.

**Case 4 — `Float.NaN` and `equals()`**
```java
Float f1 = Float.NaN;
Float f2 = Float.NaN;
System.out.println(f1 == f2);         // false (NaN != NaN even in float comparison)
System.out.println(f1.equals(f2));    // true (Float.equals() handles NaN specially)
System.out.println(f1.compareTo(f2)); // 0 (Float.compareTo() treats NaN as equal)

Double d1 = Double.NaN;
Double d2 = Double.NaN;
System.out.println(d1 == d2);         // false
System.out.println(d1.equals(d2));    // true
```
Explanation: IEEE 754 defines NaN != NaN. But Java's wrapper `equals()` explicitly checks for NaN and returns true. Without this, NaN-based keys in HashMap would be unlucky.

**Case 5 — `Comparator` vs `equals()` inconsistency**
```java
class Person {
    String name;
    Person(String n) { name = n; }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof Person && Objects.equals(name, ((Person) o).name);
    }
}

// TreeSet uses compareTo/compare, NOT equals()
// If Comparator returns 0 for unequal names → TreeSet treats them as duplicate
// If Comparator never returns 0 for equal names → TreeSet allows duplicates
```
Rule: `compareTo()` should be consistent with `equals()` (x.compareTo(y) == 0 should imply x.equals(y)). This is recommended but NOT required by Java. Violating it causes unexpected behavior in `TreeSet`/`TreeMap`.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|------|
| String comparison with `==` | False negatives for strings with same content | Always `.equals()` for string content |
| `==` with Integer wrappers | Works for -128..127, fails beyond | Always `.equals()` for wrapper values |
| Overriding `equals()` without `hashCode()` | HashMap/HashSet lookups fail (objects in different buckets) | Override both together |
| Forgetting null check before `.equals()` | NullPointerException | Use `Objects.equals(a, b)` or call `.equals()` on known non-null |
| Breaking symmetry in `equals()` | `x.equals(y)` true but `y.equals(x)` false | Always check `getClass()`, never use `instanceof` for equals |
| Using `==` for enum comparison | Actually works (singletons) — but inconsistent style | Either `==` (safe) or `equals()` — be consistent |
| Mutable fields in `equals()` / `hashCode()` | Object lost in HashMap after mutation | Use immutable fields only for hash/equals |
| Lombok `@Data` with inheritance | Violates symmetry (super vs subclass equals) | Don't use inheritance with `@Data` |
| Forgetting `Float.NaN`/`Double.NaN` in `equals()` | `NaN != NaN` breaks `equals()` contract | `Float.equals()` handles it — but custom `equals()` must too |
| `TreeSet` without consistent `compareTo` | Elements added that are "equal" by `equals()` but not by compare | Keep `compareTo` consistent with `equals` |

## 7. Production Usage

**JPA Entity equals/hashCode — The tricky persistence case:**
```java
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // null before persist!

    private BigDecimal amount;
    private String status;

    // BAD: uses generated ID in equals/hashCode
    // Before persist: id is null → all new Payments have same hash (0)
    // After persist: id changes → object is LOST in any Set!
    
    // GOOD: use business key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return amount != null && amount.compareTo(payment.amount) == 0
            && Objects.equals(transactionRef, payment.transactionRef);
        // Never use auto-generated @Id in equals/hashCode!
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(transactionRef, amount); // Stable, immutable business key
    }
}
```

**HashMap with custom key in caching:**
```java
public class CacheKey {
    private final String merchantId;
    private final LocalDate date;    // immutable fields
    private final String currency;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheKey)) return false;
        CacheKey key = (CacheKey) o;
        return merchantId.equals(key.merchantId)
            && date.equals(key.date)
            && currency.equals(key.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(merchantId, date, currency);
    }
}

// Used in production caching:
CacheKey key = new CacheKey("MERC-123", today, "USD");
Map<CacheKey, AggregatedData> cache = new ConcurrentHashMap<>();
```

**Objects.equals() in Spring Data / JPA streams:**
```java
// Production pattern for filtering without NPE:
List<Payment> pendingPayments = allPayments.stream()
    .filter(p -> Objects.equals(p.getStatus(), "PENDING"))
    .collect(Collectors.toList());
// Objects.equals(null, "PENDING") → false (no NPE)
// Objects.equals("PENDING", null) → false (no NPE)
```

## 8. Advanced Details

- **String pool intern (Java 7+ changes)**: Before Java 7, the String pool was in PermGen (fixed size). Since Java 7, it's in the heap (GC-managed). This means interned strings can now be garbage collected if no references exist outside the pool.
- **`StringTable` tuning**: `-XX:StringTableSize=100003` (prime number). Default was 1009 before Java 8, then 60013. Larger table = faster `intern()` lookup but more memory.
- **`IdentityHashMap`**: Uses `==` instead of `equals()`. Useful for reference-based identity tracking (e.g., serialization, deep copy, tracking object instances). NEVER use with String keys expecting content equality.
- **`Comparator.naturalOrder()` vs `Comparator.nullsFirst()`**: `nullsFirst()` handles null keys but nulls are never equal — `null == null` is `Object.equals()` which returns true, but `Comparator.nullSafe` treats nulls specially.
- **Auto-boxing in equals**: `Integer a = 1000; Integer b = 1000; a.equals(b) == true` always. But `a == b` depends on cache. `equals()` never has cache issues — it compares `intValue()`.
- **Arrays.equals() vs deepEquals**: `Arrays.equals()` compares element-by-element using `equals()` on each element. `Arrays.deepEquals()` handles nested arrays (recursive comparison). For `int[][]`, use `deepEquals`.
- **Proxy and equals**: Dynamic proxies may have different `getClass()` than the original. Use `instanceof` in `equals()` when dealing with proxies (Hibernate, Mockito) instead of `getClass()`.
- **Records (Java 16+)**: `record` automatically generates `equals()`, `hashCode()`, and `toString()` based on ALL components. The generated `equals()` uses `instanceof` + component comparison. This is correct for value objects but does NOT support inheritance (records are implicitly final).

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between `==` and `equals()` in Java?
A: `==` is an operator that compares primitive values OR object references (memory addresses). For objects, `==` returns true only if both variables point to the exact same object instance. `equals()` is a method defined in Object class that compares contents/logical equality. Classes like String, Integer, and custom classes override `equals()` to define what "same value" means. The Object's default `equals()` behaves like `==`.

### Intermediate
Q: What is the contract between `equals()` and `hashCode()`? What happens if you override `equals()` but not `hashCode()`?
A: The contract: if `x.equals(y)` is true, then `x.hashCode()` MUST equal `y.hashCode()`. The reverse is NOT required (hash collisions are allowed). If you override `equals()` but not `hashCode()`, objects that are "equal" by `equals()` may have different hash codes (because `Object.hashCode()` returns different values for different instances). This breaks HashMap and HashSet — you can put an object into the map, but when you look it up with an equal key, the lookup goes to a different hash bucket and returns null. Always override both together using the same fields.

### Senior
Q: In a payment system, you have a `Payment` entity with `@Id Long id` (auto-generated). How should you implement `equals()` and `hashCode()`? What if equals uses the generated ID?
A: NEVER use the auto-generated `@Id` in `equals()`/`hashCode()`. Before persisting, `id` is null, so all unsaved objects have hash 0. When persisted, `id` gets a value — if the object is already in a Set, it's lost because its hash changed. The object is still in the old bucket but lookups search the new bucket.

**Recommended approach**: Use a stable business key like a unique transaction reference or a combination of immutable fields:
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Payment)) return false;
    Payment p = (Payment) o;
    return transactionRef != null && transactionRef.equals(p.transactionRef);
}
```

### Tricky
Q: What is the output of this code?
```java
public class EqualsTricky {
    public static void main(String[] args) {
        String s1 = "hello";
        String s2 = new String("hello");
        String s3 = "hello";
        
        Integer i1 = 100;
        Integer i2 = 100;
        Integer i3 = 200;
        Integer i4 = 200;
        
        List<String> list1 = List.of("a", "b");
        List<String> list2 = List.of("a", "b");
        
        System.out.println("s1 == s2: " + (s1 == s2));
        System.out.println("s1.equals(s2): " + s1.equals(s2));
        System.out.println("i1 == i2: " + (i1 == i2));
        System.out.println("i3 == i4: " + (i3 == i4));
        System.out.println("list1 == list2: " + (list1 == list2));
        System.out.println("list1.equals(list2): " + list1.equals(list2));
    }
}
```
A: Output is:
```
s1 == s2: false    // pool vs heap reference
s1.equals(s2): true // same content
i1 == i2: true     // both cached (-128..127)
i3 == i4: false    // NOT cached (200 outside range)
list1 == list2: false // different List instances
list1.equals(list2): true // List.equals() compares content
```

Explanation: String pool makes `s1 == s3` true but `s1 == s2` false. Integer cache makes `i1 == i2` true (`100` is cached) but `i3 == i4` false (`200` is not cached). `List.of()` returns different instances but `equals()` compares content.

## 10. Final 30-Second Answer

`==` compares references (memory addresses) for objects, values for primitives. `equals()` compares content/logical equality. String literals are interned → `==` works for same literals. Integer cache (-128..127) makes `==` work inconsistently. Always override `equals()` AND `hashCode()` together for custom classes. Use `Objects.equals(a, b)` for null-safe comparison. Never use `==` for string or wrapper comparison unless you specifically want reference check.
