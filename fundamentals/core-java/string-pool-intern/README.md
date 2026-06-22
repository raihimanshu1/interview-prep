# String Pool, Immutability, and `intern()` — Complete Deep Dive

## 1. Why This Concept Matters

Strings are the most used object in Java. The String pool is a JVM optimization that saves memory and improves performance by reusing identical string literals. Understanding how the pool works, why `String` is immutable, and when to use `intern()` is critical for Java interviews. In production, incorrect string handling causes memory leaks (permgen/metaspace bloat from classloader mismanagement), unexpected behavior in security checks (string equality vs identity), and performance issues from excessive `new String()` allocations. Interviewers test this because it reveals whether you understand JVM memory management, object pooling, and the difference between reference and value equality.

Misunderstanding String pool causes:
- Memory leaks from `new String("x")` instead of `"x"` literal
- Security vulnerabilities using `==` instead of `.equals()` for string comparison
- OOM errors from loading too many classes with duplicated string literals
- Confusion about why string concatenation sometimes creates new objects

## 2. Basic Meaning

**String pool** (also called string intern pool) is a special memory area in the JVM's heap (or permgen/metaspace in older JVMs) that stores unique string literals.

**Immutability** means once a `String` object is created, its character data cannot be changed. Any operation that appears to modify a string (e.g., `toUpperCase()`, `replace()`, `concat()`) actually returns a new `String` object.

**`intern()`** is a method that returns a canonical representation of the string: if a string with the same content already exists in the pool, it returns the pooled instance; otherwise, it adds the current string to the pool and returns it.

Key vocabulary:
- **String pool / intern pool**: JVM-maintained cache of unique string literals
- **String literal**: `"hello"` — automatically interned at class loading
- **`new String("hello")`**: forces new object on heap, bypasses pool for that instance
- **`intern()`**: returns pooled instance
- **`==`**: compares references (identity). Safe for interned strings only.
- **`.equals()`**: compares character content. Always use for value equality.
- **String constant pool**: part of metaspace (Java 8+) or permgen (Java 7 and earlier)

What it is NOT: String pool is not free. It consumes metaspace. `intern()` does not guarantee lower memory usage for short-lived strings. `==` is not safe for strings created via `new` or runtime concatenation.

## 3. Real Code / Real Example

```java
public class StringPoolDemo {
    public static void main(String[] args) {
        // === LITERAL POOLING ===
        String a = "hello";
        String b = "hello";
        String c = "hello";
        // All three point to SAME object in pool
        System.out.println("a == b: " + (a == b));       // true
        System.out.println("a == c: " + (a == c));       // true

        // === NEW STRING BYPASSES POOL ===
        String d = new String("hello");
        String e = new String("hello");
        System.out.println("d == e: " + (d == e));       // false (new objects)
        System.out.println("d == a: " + (d == a));       // false
        System.out.println("d.equals(a): " + d.equals(a)); // true

        // === INTERN METHOD ===
        String f = d.intern();  // returns pooled "hello"
        System.out.println("f == a: " + (f == a));       // true (both pooled)
        System.out.println("f == d: " + (f == d));       // false

        // === CONCATENATION POOLING ===
        String g = "he" + "llo";  // compile-time constant folding
        String h = "hello";
        System.out.println("g == h: " + (g == h));       // true (folded to "hello")

        // === RUNTIME CONCATENATION ===
        String i = "he";
        String j = i + "llo";    // runtime StringBuilder
        System.out.println("j == h: " + (j == h));       // false (new object)

        // === COMPOUND CONCATENATION ===
        String k = "Java";
        k += " is fun";          // creates new object
        System.out.println("k hash: " + k.hashCode());
        System.out.println("Original unchanged: " + "Java");

        // === SUBSSTRING AND POOL ===
        String original = "interview";
        String sub = original.substring(0, 5);  // "interv"
        System.out.println("sub == pooled 'interv': " + (sub == "interv")); // false (new object)

        // === MEMORY COMPARISON ===
        Runtime rt = Runtime.getRuntime();
        long before = rt.totalMemory() - rt.freeMemory();

        // BAD: 100k new String objects
        for (int i2 = 0; i2 < 100_000; i2++) {
            String s = new String("duplicate-" + i2);
        }

        long afterBad = rt.totalMemory() - rt.freeMemory();

        // GOOD: reuse via intern
        for (int i2 = 0; i2 < 100_000; i2++) {
            String s = ("duplicate-" + i2).intern();
        }

        long afterGood = rt.totalMemory() - rt.freeMemory();
        System.out.printf("Bad: %d KB | Good: %d KB%n",
            (afterBad - before) / 1024, (afterGood - before) / 1024);
    }
}
```

Expected output:
```
a == b: true
a == c: true
d == e: false
d == a: false
d.equals(a): true
f == a: true
f == d: false
g == h: true
j == h: false
k hash: 2314539
Original unchanged: Java
sub == pooled 'interv': false
Bad: 2000-5000 KB | Good: 0-500 KB
```

Note: exact memory numbers depend on JVM and string lengths. The `intern()` version uses significantly less heap because duplicate strings share the pooled instance.

## 4. What Happens Internally

**Class loading and string literals:**
When a class is loaded, all string literals in its constant pool are automatically interned. For `"hello"`, the JVM checks if "hello" already exists in the string pool:
- If yes: return existing reference
- If no: create new String, add to pool, return reference

**`intern()` method logic:**
```java
// Simplified String.intern()
public String intern() {
    synchronized (String.class) {
        // 1. Check if this string is already in pool
        if (pool.contains(this)) return pool.get(this);
        // 2. Add this string to pool
        pool.add(this);
        return this;
    }
}
```
In reality, JVM uses a native internal hash table for the pool. The actual implementation is in the JVM, not in Java code.

**String creation paths:**
1. `String s = "hello";` → pool lookup/add, heap reference to pooled object
2. `String s = new String("hello");` → new heap object + pool entry for "hello"
3. `String s = "hello".intern();` → pool lookup, return pooled reference
4. `String s = str1 + str2;` → `new StringBuilder().append(str1).append(str2).toString()` → new heap object unless compiler optimizes

**Why `==` works for literals but not concatenation:**
- Literals: JVM interns at class load → same reference
- Concatenation: `str1 + str2` uses `StringBuilder.toString()`, which creates a new `String` object each time → different reference

**Memory areas:**
- Java 6 and earlier: String pool in PermGen (fixed size, default ~64 MB, not garbage collected easily)
- Java 7+: String pool moved to Heap (garbage collected, tunable via `-XX:StringTableSize=N`)
- Java 8+: Same as Java 7, in Heap

**String.substring() change (Java 7u6):**
Before Java 7u6, `substring()` shared the original char array (memory efficient but could cause memory leaks with large strings). After Java 7u6, `substring()` copies the char array, preventing memory leaks but using more memory.

## 5. Tricky Interview Cases

**Case 1 — String literal pooling**
```java
String a = "abc";
String b = "abc";
String c = new String("abc");
System.out.println(a == b);    // true
System.out.println(a == c);    // false
System.out.println(a.equals(c)); // true
```
Output: `true`, `false`, `true`
Explanation: `a` and `b` reference the same pooled object. `c` is a new heap object, so `==` is false. `.equals()` compares content.

**Case 2 — `intern()` returning pooled object**
```java
String a = new String("hello");
String b = a.intern();
String c = "hello";
System.out.println(a == b);    // false
System.out.println(b == c);    // true
```
Output: `false`, `true`
Explanation: `a` is a new heap object. `a.intern()` finds "hello" already pooled from the literal `c` (loaded at class initialization). Wait — order matters. If `a.intern()` runs before `"hello"` literal is loaded, it would add the new object to pool. In this code, class loading interns `"hello"` first, so `b` and `c` point to pooled object.

**Case 3 — Concatenation and pooling**
```java
String a = "Java";
String b = "Ja" + "va";    // compile-time constant folding
String c = a + "va";       // runtime StringBuilder
String d = "Javava";
System.out.println(a == b);    // true (folded)
System.out.println(a == c);    // false
System.out.println(b == d);    // false
```
Output: `true`, `false`, `false`
Explanation: `"Ja" + "va"` is a compile-time constant → folded to `"Java"` → same pooled reference. `a + "va"` uses `StringBuilder` at runtime → new object.

**Case 4 — `substring` is not pooled**
```java
String original = "interview preparation";
String sub = original.substring(0, 10); // "interview"
System.out.println(sub == "interview"); // false
System.out.println(sub.equals("interview")); // true
```
Output: `false`, `true`
Explanation: `substring()` creates a new `String` object by copying the relevant char array (Java 7+). It is not automatically interned.

**Case 5 — Null in string comparison**
```java
String a = null;
String b = "hello";
System.out.println(b.equals(a));    // false
System.out.println(a.equals(b));    // NullPointerException
```
Output: `false` then `NullPointerException`
Explanation: `"hello".equals(null)` returns `false` safely. `null.equals("hello")` throws NPE. Always call `.equals()` on the known non-null string literal or use `Objects.equals()`.

**Case 6 — String pool in loops**
```java
for (int i = 0; i < 1000; i++) {
    String s = new String("x"); // 1000 objects
}
// vs
for (int i = 0; i < 1000; i++) {
    String s = "x";             // 1 object in pool
}
```
Output: First loop allocates 1000 objects. Second loop allocates 1.
Explanation: `new String("x")` creates a new heap object every time. `"x"` literal is interned once at class load. Subsequent iterations reuse the same pooled reference.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `new String("literal")` instead of `"literal"` | Wastes heap memory, bypasses pool | Use string literals directly |
| Using `==` for string comparison | Fails for non-interned strings | Always use `.equals()` |
| Assuming concatenation is pooled | `s1 + s2` creates new object at runtime | No fix needed, but don't rely on pooling |
| Interning everything | Can bloat pool, synchronized overhead on `intern()` | Intern only long-lived, frequently repeated strings |
| Ignoring `substring()` memory | Pre-Java 7u6 retained reference to original char array | Be aware of JVM version difference |
| Security check with `==` | Can be bypassed if one string not interned | Use `.equals()` in passwords, tokens, ACLs |

## 7. Production Usage

**Spring Boot application properties:**
```java
// Spring caches property values as strings
@Value("${app.name}")
private String appName;
// Property values are pooled if they come from same PropertySource
```

**Security — password comparison:**
```java
// WRONG: identity check vulnerable to timing attack and non-interned strings
if (inputPassword == storedPassword) { ... }

// CORRECT: value comparison
if (inputPassword.equals(storedPassword)) { ... }

// BETTER: constant-time comparison to prevent timing attacks
if (MessageDigest.isEqual(inputPassword.getBytes(), storedPassword.getBytes())) { ... }
```
Using `==` for passwords fails if one string is created via `new String()` or read from network.

**Enum with string constructor:**
```java
public enum Status {
    ACTIVE("ACTIVE"), INACTIVE("INACTIVE");
    private final String code;
    Status(String code) { this.code = code; }
}
// Enum constructor parameter "ACTIVE" is pooled literal.
// Stored reference points to pooled object. No memory leak.
```

**Parser/caching with intern:**
```java
// JSON field names parsed millions of times
String fieldName = jsonNode.fieldName().intern();
// Field names like "userId", "orderId" repeat across millions of messages.
// Interning reduces memory: all "userId" reference same pooled object.
```
Jackson does not intern field names by default. In high-throughput systems parsing millions of JSON objects, interning repeated keys can reduce GC pressure.

**Avoiding metaspace OOM:**
```java
// In application servers (Tomcat), each webapp classloader has its own string pool.
// If webapp is redeployed without full restart, old classloader strings may leak.
// Monitor metaspace: -XX:MaxMetaspaceSize=256m
// Symptoms: java.lang.OutOfMemoryError: Metaspace
```

## 8. Advanced Details

- **`StringTableSize` JVM flag:** `-XX:StringTableSize=100003` (prime number recommended). Default is ~60013 in modern JVMs. Larger table reduces hash collisions in the pool.
- **Java 7+ pool location:** Moved from PermGen to Heap. Strings can now be garbage collected if no reachable references exist. `intern()` still keeps a hard reference in the pool.
- **`String.intern()` is synchronized:** The `intern()` method uses a global lock on the String class. Under high concurrency, frequent interning can cause contention. Java 9+ uses a `CompactString` optimization (byte arrays for Latin-1 strings) and improved pool implementation.
- **`String` is final:** Cannot be subclassed. All methods return new `String` objects. This enables pooling (immutable objects are safe to share).
- **`StringBuilder` vs `StringBuffer`:** `StringBuilder` (non-thread-safe) and `StringBuffer` (synchronized). Both create new `String` on `toString()`. Neither uses pool unless `toString()` returns literal.
- **`String.format()` creates new objects:** Each call creates new `String` and `Formatter`. Not pooled. Use only for debugging/logging.
- **G1 GC and String deduplication:** Java 8u20+ with G1 GC supports `-XX:+UseStringDeduplication`. Automatically finds duplicate String char arrays and shares them. Alternative to manual `intern()`.
- **Compact strings (Java 9+):** `String` stores bytes (not chars) for Latin-1 content. Reduces memory by 50% for ASCII-heavy strings. Internal `byte[]` with `coder` flag (LATIN1 or UTF16).

## 9. Interview Questions And Answers

### Beginner
Q: What is the String pool in Java? How is it different from the heap?
A: The String pool is a special area in JVM memory (heap in Java 7+, PermGen in older versions) that stores unique string literals. When you write `String s = "hello";`, the JVM checks if "hello" is already in the pool. If yes, it returns the existing reference. If no, it creates a new String and adds it to the pool. The heap is general-purpose memory for all objects. The pool is a cache within the heap (or PermGen) that reduces duplicate string allocations.

### Intermediate
Q: What is the difference between `String s = "hello";` and `String s = new String("hello");`? How many objects are created in each case?
A: `String s = "hello";` creates one object in the string pool (if "hello" is not already there). The reference `s` points to the pooled object.

`String s = new String("hello");` creates two objects:
1. One pooled object for the literal `"hello"` (automatically interned at class loading)
2. One new heap object (because `new String()` explicitly allocates)
The reference `s` points to the new heap object, not the pooled one. This is why `s == "hello"` is `false` but `s.equals("hello")` is `true`.

### Senior
Q: You are building a JSON streaming parser that processes 10 million messages per hour. Each message has repeated field names like "userId", "orderId", "timestamp". You notice high GC pressure from string allocations. How would you optimize string handling, and what tradeoffs would you consider?
A: The repeated field names like "userId" create millions of short-lived `String` objects because `jsonNode.fieldName()` returns a new `String` for each occurrence.

Option 1: Manual interning at parse time
```java
String field = jsonNode.fieldName().intern();
```
Tradeoff: Reduces memory by sharing pooled instances. But `intern()` is synchronized, adding contention under high throughput. Also, over-interning (too many unique strings) can bloat the pool.

Option 2: Use G1 GC string deduplication
```bash
-XX:+UseG1GC -XX:+UseStringDeduplication
```
Tradeoff: JVM automatically deduplicates String char arrays. No code change. Works best with G1 GC. Reduces GC pressure by 20-40% in string-heavy workloads.

Option 3: Reusable `JsonParser` with pre-interned field constants
```java
private static final String FIELD_USER_ID = "userId".intern();
// Compare parsed field against constants instead of creating new strings
```
Tradeoff: Most performant. Requires code changes. Only works for known schemas.

Option 4: Memory-efficient parsing (e.g., Jackson `JsonFactory` with `UTF8JsonParser` instead of `Utf8JsonParser` + `InternPool`)
Tradeoff: Library support may be limited.

For maximum production safety: Combine Option 2 (G1 deduplication) with selective interning on hot field names. Monitor metaspace/string pool size with `jmap -heap`.

### Tricky
Q: You have `String a = new String("xyz"); String b = a.intern(); String c = "xyz";` — explain the exact reference relationships. Then if the class loading order changes and `intern()` is called before the literal `"xyz"` is loaded, what changes?
A: At class loading, all string literals in the class constant pool are interned. If `"xyz"` appears as a literal anywhere in the class, it is pooled before `main()` runs.

Scenario 1 (literal loaded first): `a` points to new heap object. `a.intern()` finds pooled "xyz" (from literal `c`), returns pooled reference. `b` points to pooled object. `c` points to pooled object. Result: `a != b`, `b == c`.

Scenario 2 (literal NOT in class, intern called first): `a` is new heap object. `a.intern()` adds `a` to the pool. `b` points to `a`. Later `"xyz"` literal loads, but pool already has `a`. `c` points to `a`. Result: `a == b == c` — all three point to the same (heap) object.

This demonstrates that `intern()` is not idempotent in terms of what it returns: it returns *a* canonical instance, but which instance depends on what was already in the pool. In practice, always assume literals are pre-interned by class loading.

## 10. Final 30-Second Answer

String pool = JVM cache of unique string literals. `"hello"` interned at class load; `new String("hello")` creates extra heap object. String is immutable: all modifying operations return new objects. `intern()` returns pooled instance. **Never use `==` for string comparison** — use `.equals()`. Java 7+ pool is on heap (GC-able). Java 9+ compact strings use `byte[]` for ASCII. G1 GC has string deduplication. Use `intern()` sparingly for long-lived repeated strings.