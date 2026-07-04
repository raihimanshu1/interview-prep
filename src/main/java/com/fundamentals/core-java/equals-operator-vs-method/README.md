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

## 4. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| String comparison with `==` | False negatives for strings with same content | Always `.equals()` for string content |
| `==` with Integer wrappers | Works for -128..127, fails beyond | Always `.equals()` for wrapper values |
| Overriding `equals()` without `hashCode()` | HashMap/HashSet lookups fail (objects in different buckets) | Override both together |
| Forgetting null check before `.equals()` | NullPointerException | Use `Objects.equals(a, b)` or call `.equals()` on known non-null |
| Breaking symmetry in `equals()` | `x.equals(y)` true but `y.equals(x)` false | Always check `getClass()`, never use `instanceof` for equals |
| Using `==` for enum comparison | Actually works (singletons) — but inconsistent style | Either `==` (safe) or `equals()` — be consistent |

## 5. Final 30-Second Answer

`==` compares references (memory addresses) for objects, values for primitives. `equals()` compares content/logical equality. String literals are interned → `==` works for same literals. Integer cache (-128..127) makes `==` work inconsistently. Always override `equals()` AND `hashCode()` together for custom classes. Use `Objects.equals(a, b)` for null-safe comparison. Never use `==` for string or wrapper comparison unless you specifically want reference check.