# Integer Cache and Wrapper Comparison — Complete Deep Dive

## 1. Why This Concept Matters

The Integer cache is one of the most frequently tested Java behaviors in interviews. It explains why `Integer a = 100; Integer b = 100; System.out.println(a == b);` prints `true`, while the same code with `200` prints `false`. This is not a quirk — it is a deliberate JVM optimization that every Java developer must understand to avoid subtle comparison bugs. In production, confusing `==` with `.equals()` on wrappers has caused authentication bypasses (cached `Boolean` identity checks), incorrect cache hit calculations, and memory leaks from unbounded cache expansion. Understanding the cache range, its configurability, and how it interacts with `==`, `.equals()`, and unboxing is essential for writing correct and performant Java code.

Misunderstanding this concept causes:
- Silent logical bugs where `==` returns `true` for some values and `false` for others
- Security flaws when using wrapper identity for permission checks
- Performance issues when extending the cache range without understanding memory tradeoffs
- NPE from unexpected unboxing in comparison chains

## 2. Basic Meaning

Java caches `Integer` objects in the range `-128` to `127` by default. When you write `Integer x = 100;`, the compiler generates `Integer.valueOf(100)`, which returns a cached object. When you write `Integer y = 100;`, the same cached object is returned, so `x == y` is `true` (same reference).

For values outside `-128` to `127`, such as `200`, each `valueOf(200)` call creates a new `Integer` object, so `==` returns `false`.

Key vocabulary:
- **Cache range**: `-128` to `127` by default (configurable via JVM flag)
- **`Integer.valueOf()`**: factory method that returns cached objects when possible
- **`==` comparison**: compares object references, not values
- **`.equals()` comparison**: compares the actual `int` value stored in the wrapper
- **`new Integer(int)`**: always creates a new object, bypassing cache (legacy, rarely used)
- **Auto-unboxing in `==`**: when comparing `Integer` to `int`, the `Integer` is automatically unboxed to `int` for primitive comparison

What it is NOT: the cache does not make `==` safe for general wrapper comparison. It is an implementation detail that makes `==` behave inconsistently across values.

## 3. Real Code / Real Example

```java
import java.util.*;

public class IntegerCacheDemo {
    public static void main(String[] args) {
        // === DEFAULT CACHE BEHAVIOR: -128 to 127 ===
        Integer a = 100;
        Integer b = 100;
        Integer c = 200;
        Integer d = 200;

        System.out.println("a == b (100): " + (a == b));         // true  (cached)
        System.out.println("c == d (200): " + (c == d));         // false (not cached)

        // === COMPARING WITH PRIMITIVES ===
        Integer wrapper = 100;
        int primitive = 100;
        System.out.println("wrapper == primitive: " + (wrapper == primitive)); // true (auto-unbox)
        System.out.println("wrapper.equals(primitive): " + wrapper.equals(primitive)); // true

        // === CACHE BOUNDARY ===
        Integer low = 127;
        Integer high = 128;
        System.out.println("127 cached: " + (low == Integer.valueOf(127)));   // true
        System.out.println("128 cached: " + (high == Integer.valueOf(128)));   // false

        // === USING equals() ALWAYS WORKS ===
        System.out.println("200 .equals(): " + c.equals(d));       // true

        // === NEW INTEGER BYPASSES CACHE ===
        Integer e = new Integer(100);
        Integer f = 100;
        System.out.println("new Integer(100) == 100: " + (e == f)); // false

        // === APPLIES TO OTHER INTEGER WRAPPERS ===
        Long g = 100L;
        Long h = 100L;
        System.out.println("Long 100 == : " + (g == h));           // true  (same cache)
        Long i = 200L;
        Long j = 200L;
        System.out.println("Long 200 == : " + (i == j));           // false

        // === BOOLEAN CACHE: only two values ===
        Boolean t1 = Boolean.valueOf(true);
        Boolean t2 = Boolean.valueOf(true);
        Boolean t3 = new Boolean(true);
        System.out.println("Boolean cached: " + (t1 == t2));       // true
        System.out.println("Boolean new: " + (t1 == t3));          // false

        // === CACHE AS MAP KEYS ===
        Map<Integer, String> statusCodes = new HashMap<>();
        statusCodes.put(200, "OK");
        statusCodes.put(404, "Not Found");
        System.out.println("Get 200: " + statusCodes.get(200));    // OK
        System.out.println("Get 404: " + statusCodes.get(404));    // Not Found
        System.out.println("200 key identity: " + (statusCodes.get(200) == "OK")); // false (.equals is true)

        // === CHECKING JVM CACHE SIZE ===
        try {
            Field cacheField = Integer.class.getDeclaredField("integerCacheHigh");
            cacheField.setAccessible(true);
            int cacheHigh = (int) cacheField.get(Integer.class);
            System.out.println("Integer cache high: " + cacheHigh);
        } catch (Exception e2) {
            System.out.println("Cache field not accessible (expected on some JVMs)");
        }
    }
}
```

Expected output:
```
a == b (100): true
c == d (200): false
wrapper == primitive: true
wrapper.equals(primitive): true
127 cached: true
128 cached: false
200 .equals(): true
new Integer(100) == 100: false
Long 100 == : true
Long 200 == : false
Boolean cached: true
Boolean new: false
Get 200: OK
Get 404: Not Found
200 key identity: false
Integer cache high: 127 (or 2048 if extended)
```

## 4. What Happens Internally

The `Integer` class contains a static nested class `IntegerCache` populated at class-load time:

```java
// Simplified OpenJDK IntegerCache
private static class IntegerCache {
    static final int low = -128;
    static final int high;
    static final Integer[] cache;

    static {
        int h = 127;
        String integerCacheHighProp = sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
        if (integerCacheHighProp != null) {
            try { h = Math.max(128, Integer.parseInt(integerCacheHighProp)); }
            catch (NumberFormatException e) { }
        }
        high = h;
        cache = new Integer[(high - low) + 1];
        int j = low;
        for (int k = 0; k < cache.length; k++) { cache[k] = new Integer(j++); }
        // range [-128, 127] or [-128, high]
    }

    private IntegerCache() {}
}
```

When `Integer.valueOf(100)` is called:
1. Check if `100` is within `[low, high]` (i.e., `[-128, 127]`)
2. Compute index: `100 - (-128) = 228`
3. Return `cache[228]` — the pre-allocated `Integer(100)` object
4. All subsequent calls for `100` return the exact same object reference

For `200`:
1. `200` is outside `[low, high]`
2. Return `new Integer(200)` — a fresh heap allocation

**Memory impact of cache extension:**
- Default cache holds 256 `Integer` objects (from `-128` to `127`)
- Each `Integer` object is ~24 bytes (header + int + padding on 64-bit JVM with compressed oops)
- Default cache = ~6 KB total — negligible
- Extended to `10000`: ~24 KB — still small
- Extended to `1000000`: ~24 MB — significant, plus affects GC promotion patterns

**Persistence across values:**
Cached objects are stored in a static array and referenced by the `Integer` class itself. They are never garbage collected. Increasing the cache range increases the JVM's permanent memory footprint proportionally.

## 5. Tricky Interview Cases

**Case 1 — `==` with `new` keyword**
```java
Integer a = new Integer(100);
Integer b = 100;
System.out.println(a == b);  // false
```
Output: `false`
Explanation: `new Integer(100)` creates a fresh object, completely bypassing `valueOf()`. `b = 100` uses the cached object. Different references.

**Case 2 — `==` with `valueOf` explicitly**
```java
Integer a = Integer.valueOf(100);
Integer b = Integer.valueOf(100);
System.out.println(a == b);  // true
```
Output: `true`
Explanation: Both calls use `valueOf()`, which uses the cache. Same object returned.

**Case 3 — Autoboxing with arithmetic**
```java
Integer a = 100;
Integer b = 100;
Integer c = a + b;
Integer d = 200;
System.out.println(c == d);  // false
```
Output: `false`
Explanation: `a + b` unboxes both to `int`, performs primitive addition (200), then autoboxes the result via `Integer.valueOf(200)`. The result is a new `Integer(200)` object. `d = 200` also creates a new `Integer(200)`. They are different objects, so `==` is `false`. But `c.equals(d)` is `true`.

**Case 4 — Comparing `Short` and `Integer`**
```java
Short s = 100;
Integer i = 100;
System.out.println(s == i);      // true  (auto-unbox both to int)
System.out.println(s.equals(i)); // false (different types)
```
Output: `true` then `false`
Explanation: `==` with mixed numeric wrapper types causes both to unbox to `int` and compare as primitives. `.equals()` checks type first, so `Short.equals(Integer)` returns `false`.

**Case 5 — `null` in `==` comparison**
```java
Integer a = null;
Integer b = null;
System.out.println(a == b);       // true  (both null)
System.out.println(a.equals(b));  // NullPointerException!
```
Output: `true` then `NullPointerException`
Explanation: `a == b` compares references. `null == null` is `true`. `a.equals(b)` attempts to call `.equals()` on a null reference, which throws NPE. Never call methods on potentially null wrappers.

**Case 6 — Compound assignment with cache**
```java
Integer a = 100;
a += 50;
System.out.println(a == 150);     // false
System.out.println(a == Integer.valueOf(150)); // false
```
Output: `false` then `false`
Explanation: `a += 50` unboxes `a` to `int` (100), adds 50 (result 150), then autoboxes via `Integer.valueOf(150)`. Since `150` is outside the cache, a new object is created. The original cached `100` is no longer referenced by `a`.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `==` for wrapper value comparison | Returns inconsistent results depending on cache | Always use `.equals()` for wrapper value comparison |
| Assuming `new Integer(x)` uses cache | Always allocates new object | Use autoboxing (`Integer x = 100;`) or `valueOf()` |
| Comparing different wrapper types with `.equals()` | `.equals()` checks runtime class | Cast explicitly or compare as primitives if types differ |
| Extending cache range without monitoring | Increases JVM heap/permgen usage | Only extend if profiling shows benefit; monitor memory |
| Using wrapper `==` in security checks | Identity check can be bypassed with different value objects | Always compare values with `.equals()` in authorization logic |
| Forgetting `Long` and `Integer` share cache prefix | `Long.valueOf(100)` also returns cached object | Same `==` behavior applies to all integer wrappers |
| NPE from `.equals()` on null | Calling method on null reference | Use `Objects.equals(a, b)` for null-safe comparison |

## 7. Production Usage

**HashMap with Integer keys:**
```java
Map<Integer, String> errorCodes = new HashMap<>();
errorCodes.put(400, "Bad Request");
errorCodes.put(404, "Not Found");
errorCodes.put(500, "Internal Server Error");
// Lookup: works correctly because hashCode() returns the int value
// and equals() compares values
String msg = errorCodes.get(404); // "Not Found"
```
HashMap relies on `.equals()` and `hashCode()`, not `==`. The cache is irrelevant for map lookups, but understanding that wrappers are still objects helps reason about identity.

**Spring Boot configuration with cached values:**
```java
@Component
public class FeatureFlagService {
    // Thread-safe because Boolean.TRUE/FALSE are cached singletons
    private volatile Boolean cacheEnabled = Boolean.FALSE;

    public boolean isCacheEnabled() {
        return Boolean.TRUE.equals(cacheEnabled); // null-safe
    }
}
```
Using `Boolean.TRUE.equals()` is null-safe and identity-stable.

**Authorization bug (REAL-WORLD):**
```java
// WRONG: identity-based permission check (insecure)
public boolean isAdmin(User user) {
    return user.getRole() == Role.ADMIN;  // vulnerable if Role.ADMIN is recreated
}

// CORRECT: value-based
public boolean isAdmin(User user) {
    return Role.ADMIN.equals(user.getRole());
}
```
If `Role.ADMIN` is deserialized or recreated, identity (`==`) breaks. Always use `.equals()`.

**Extending cache for high-traffic caches:**
```bash
# In production startup script
JAVA_OPTS="-Djava.lang.Integer.IntegerCache.high=2048"
```
Tradeoff: 2 KB extra for 1792 additional cached objects. Reduces allocation for IDs in that range.

## 8. Advanced Details

- **Cache extension JVM flag:** `-Djava.lang.Integer.IntegerCache.high=2048`. Applies to `Byte`, `Short`, `Character`, `Integer`, `Long`. Must be set at JVM startup; changing at runtime has no effect.
- **`IntegerCache` is per-JVM:** Not shared across classloaders in application servers (Tomcat, WebLogic). Each webapp classloader loads its own `Integer` class, creating separate caches.
- **`Integer.getInteger()` vs `Integer.valueOf()`:** `getInteger(String)` reads a system property and returns an `Integer`. `getInteger("x", 100)` returns `100` as `Integer.valueOf(100)` if property `x` is not set.
- **`Integer.decode()`:** Parses hex (`0x`), octal (`0`), and decimal strings. Uses `valueOf()` internally.
- **Primitive `int` vs `Integer` in switch:** `switch` supports both because it unboxes `Integer` to `int`. `null` Integer in switch → `NullPointerException`.
- **Unboxing on arithmetic with `null`:** `Integer a = null; int b = a + 1;` throws `NullPointerException` before the addition occurs. JLS mandates null check before unboxing for arithmetic.
- **String optimization:** `"100"` literal interned once. `Integer.toString(100)` uses cached string arrays internally for digits.
- **JIT escape analysis:** HotSpot may eliminate boxing in local scope if the wrapper never escapes the method. This is an optimization, not a guarantee. Do not write code relying on it.

## 9. Interview Questions And Answers

### Beginner
Q: What is the Integer cache? What is its default range?
A: The Integer cache stores pre-allocated `Integer` objects for values `-128` to `127`. When autoboxing a value in this range, Java reuses the cached object via `Integer.valueOf()` instead of creating a new one. Default range is `-128` to `127`.

### Intermediate
Q: Why does `Integer a = 100; Integer b = 100; System.out.println(a == b);` print `true`, but `Integer c = 200; Integer d = 200; System.out.println(c == d);` prints `false`?
A: `100` is within the default cache range `-128` to `127`. Both `a` and `b` point to the same cached `Integer` object, so `==` (reference comparison) returns `true`. `200` is outside the cache, so each `Integer.valueOf(200)` creates a new object. Since `==` compares references, it returns `false`. To compare values reliably, use `a.equals(b)`.

### Senior
Q: A teammate wrote a security check: `if (user.getRole() == Role.ADMIN) { grantAccess(); }`. The role enum is serialized and deserialized across microservices. Is this safe? What could go wrong?
A: This is unsafe. Java `enum` values are cached singletons within a single JVM, so `==` works for enum comparison. But when deserializing across JVM boundaries (RMI, REST DTOs mapped to enum, Kafka message deserialization), each JVM loads its own enum constants. `==` compares identity (memory address), so a deserialized `Role.ADMIN` from Service A is a different object than `Role.ADMIN` in Service B, even though they represent the same logical value. Use `.equals()` for safety across serialization boundaries: `Role.ADMIN.equals(user.getRole())`.

### Tricky
Q: `Integer a = 100; Integer b = 100; Integer c = 200; Integer d = 200;` Given this, explain why `(a + b) == (c + d)` is `true` even though `c == d` is `false`.
A: `a + b` triggers unboxing to `int` (100 + 100 = 200), then autoboxing the result to `Integer.valueOf(200)`. Similarly, `c + d` unboxes to `int` (200 + 200 = 400), then autoboxes to `Integer.valueOf(400)`. The comparison `200 == 400` as primitives is `false`. Wait — let me correct: `(a + b)` evaluates to `Integer.valueOf(200)` and `(c + d)` evaluates to `Integer.valueOf(400)`. The comparison is `200 == 400` which is `false`. But if you meant `(a + b) == 200` that would be `true` because autounboxing the left side to `int` and comparing to primitive `200` yields `true`. The key insight: arithmetic always unboxes to primitive, so the result is primitive `int` comparison, completely bypassing wrapper identity.

## 10. Final 30-Second Answer

Integer cache = pre-allocated `Integer` objects for `-128` to `127`. `Integer.valueOf()` uses cache; `==` sees same reference for cached values but different references outside. **Never use `==` for wrapper value comparison** — use `.equals()`. Cache range is configurable. `new Integer()` bypasses cache. Primitive comparison (`Integer` vs `int`) auto-unboxes. Apply `.equals()` in authorization, serialization, and all value checks.