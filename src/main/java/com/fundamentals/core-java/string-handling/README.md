# String Handling — Complete Deep Dive

## 1. Why This Concept Matters

Strings are the most used data type in Java. Every application handles user input, API responses, file processing, and logging with strings. Understanding string immutability, the String pool, `StringBuilder`/`StringBuffer`, and performance implications of concatenation is essential. In production, poor string handling causes excessive GC pressure, memory leaks, and performance bottlenecks. Interviewers test string handling to verify you understand memory management, mutability vs immutability, and when to use which string type.

Misunderstanding string handling causes:
- Excessive object allocation from concatenation in loops
- Memory leaks from `substring()` retaining large char arrays (pre-Java 7u6)
- Performance issues in logging and string building
- Security issues with mutable string buffers

## 2. Basic Meaning

**String** is immutable — once created, its content cannot change. All operations that appear to modify a string return a new `String` object.

**StringBuilder** is mutable — designed for efficient string construction. Modifications change the same object.

**StringBuffer** is the synchronized version of `StringBuilder` — thread-safe but slower.

Key vocabulary:
- **Immutability**: cannot change after creation
- **String pool**: JVM cache of unique string literals
- **`intern()`**: returns pooled instance
- **`StringBuilder`**: mutable, non-thread-safe string builder
- **`StringBuffer`**: mutable, thread-safe (synchronized)
- **Concatenation**: combining strings via `+` or `concat()`
- **`String.format()`**: creates formatted strings (slow, allocates)
- **`String.join()`**: joins CharSequence with delimiter

What it is NOT: String concatenation is not always inefficient (compiler optimizes simple cases). StringBuilder is not always faster (small concatenations similar). Strings are not always pooled (runtime-created strings are not pooled automatically).

## 3. Real Code / Real Example

```java
import java.util.*;

public class StringHandlingDemo {
    public static void main(String[] args) {
        // === IMMUTABILITY ===
        String s1 = "hello";
        String s2 = s1.concat(" world"); // returns NEW string
        System.out.println("s1: " + s1);       // hello
        System.out.println("s2: " + s2);       // hello world

        // === CONCATENATION IN LOOP (BAD vs GOOD) ===
        long start1 = System.currentTimeMillis();
        String bad = "";
        for (int i = 0; i < 10_000; i++) {
            bad += i; // creates new String each iteration
        }
        long time1 = System.currentTimeMillis() - start1;

        long start2 = System.currentTimeMillis();
        StringBuilder good = new StringBuilder();
        for (int i = 0; i < 10_000; i++) {
            good.append(i); // reuses same builder
        }
        long time2 = System.currentTimeMillis() - start2;

        System.out.printf("String concat: %d ms | StringBuilder: %d ms%n", time1, time2);
        // String concat: 200-500 ms | StringBuilder: 1-10 ms

        // === STRINGBUILDER VS STRINGBUFFER ===
        StringBuilder sb = new StringBuilder();
        sb.append("hello");    // mutable
        sb.append(" ");
        sb.append("world");
        sb.insert(5, ",");     // insert at index
        sb.delete(5, 6);      // delete range
        System.out.println("StringBuilder: " + sb); // hello world

        // StringBuffer (synchronized)
        StringBuffer sync = new StringBuffer();
        sync.append("thread");
        sync.append("-safe");
        System.out.println("StringBuffer: " + sync); // thread-safe

        // === STRING FORMAT (avoid in hot paths) ===
        String formatted = String.format("User %s logged in at %s", "Alice", "10:30");
        System.out.println("Formatted: " + formatted);

        // === STRING JOIN ===
        String joined = String.join(", ", "Java", "Python", "Go");
        System.out.println("Joined: " + joined); // Java, Python, Go

        // === COMPARISON ===
        String a = "test";
        String b = "test";
        String c = new String("test");
        System.out.println("a == b (pooled): " + (a == b));       // true
        System.out.println("a == c (new): " + (a == c));          // false
        System.out.println("a.equals(c): " + a.equals(c));       // true

        // === SUBSTRING ===
        String original = "interview preparation";
        String sub = original.substring(0, 10); // "interview"
        System.out.println("Substring: " + sub);

        // === CHAR AT AND INDEXOF ===
        String text = "hello world";
        System.out.println("charAt(0): " + text.charAt(0));     // h
        System.out.println("indexOf('o'): " + text.indexOf('o')); // 4
        System.out.println("lastIndexOf('l'): " + text.lastIndexOf('l')); // 9
        System.out.println("startsWith('hel'): " + text.startsWith("hel")); // true
        System.out.println("endsWith('ld'): " + text.endsWith("ld"));       // true
    }
}
```

Expected output:
```
s1: hello
s2: hello world
String concat: 200-500 ms | StringBuilder: 1-10 ms
StringBuilder: hello world
StringBuffer: thread-safe
Formatted: User Alice logged in at 10:30
Joined: Java, Python, Go
a == b (pooled): true
a == c (new): false
a.equals(c): true
Substring: interview
charAt(0): h
indexOf(o): 4
lastIndexOf(l): 9
startsWith(hel): true
endsWith(ld): true
```

## 4. What Happens Internally

**String concatenation via `+`:**
Compiler translates `s1 + s2 + s3` into `new StringBuilder().append(s1).append(s2).append(s3).toString()`.

For loop concatenation:
```java
String s = "";
for (int i = 0; i < 100; i++) s += i;
```
Equivalent to:
```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 100; i++) sb.append(i);
String s = sb.toString();
```

**Memory allocation:**
Each loop iteration with `s += i`:
1. Allocate new `StringBuilder`
2. Append current `s` (existing string)
3. Append `i`
4. `toString()` allocates new `String`
5. Old `s` becomes garbage

With `StringBuilder` outside loop: single allocation, reused.

**`String.substring()` pre-Java 7u6 vs post:**
- **Pre-Java 7u6**: `substring()` shared original `char[]` with offset/length. Memory efficient but could cause memory leak: a small substring retained reference to entire large original string.
- **Java 7u6+**: `substring()` copies the relevant portion to new `char[]` (or `byte[]` in Java 9+). No memory leak but slightly slower.

**`StringBuilder` internals:**
```java
class StringBuilder {
    char[] value;    // or byte[] in Java 9+
    int count;
    StringBuilder append(String str) {
        // Ensure capacity, copy chars, increment count
        return this;
    }
}
```
Default capacity: 16 characters. Expands by ~2x when full.

**`String.format()` internals:**
`String.format()` creates:
1. `Formatter` object (allocates)
2. `StringBuilder` for building result
3. Final `String` from `toString()`
Total: 3 allocations per call. Expensive in hot paths.

## 5. Tricky Interview Cases

**Case 1 — String concatenation optimization**
```java
String a = "hello";
String b = "world";
String c = a + " " + b;    // compiler: new StringBuilder().append(a).append(" ").append(b).toString()
String d = "hello" + " " + "world"; // compiler: "hello world" (compile-time constant)
System.out.println(c == d); // false
System.out.println(c.equals(d)); // true
```
Output: `false`, `true`
Explanation: Runtime concatenation creates new object. Compile-time constant folding creates pooled literal.

**Case 2 — StringBuilder default capacity**
```java
StringBuilder sb1 = new StringBuilder();   // capacity 16
StringBuilder sb2 = new StringBuilder(100); // capacity 100
sb1.append("123456789012345678901234567890"); // 31 chars, capacity expands to 16*2+2=34
```
Output: Capacity grows automatically.
Explanation: Default capacity is 16. When exceeded, grows to `(oldCapacity * 2) + 2`. Pre-sizing avoids reallocation for known-size builds.

**Case 3 — `StringBuffer` vs `StringBuilder` in single-threaded context**
```java
// Single-threaded: StringBuilder is faster (no synchronization overhead)
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) sb.append(i);

// Multi-threaded: StringBuffer required for thread safety
StringBuffer sync = new StringBuffer();
// Multiple threads calling append() — synchronized internally
```
Output: StringBuilder is ~20-30% faster in single-threaded benchmarks.
Explanation: `StringBuffer` methods are `synchronized`. In single-threaded code, synchronization overhead is wasted.

**Case 4 — `split()` behavior**
```java
String csv = "a,b,c,";
String[] parts = csv.split(",");
System.out.println("Length: " + parts.length); // 4, not 3
System.out.println("Last: '" + parts[3] + "'");  // '' (empty string)
```
Output: `Length: 4`, `Last: ''`
Explanation: Trailing empty strings are NOT discarded by `split()`. Only leading empty strings are discarded. To preserve all: `split(",", -1)`.

**Case 5 — `intern()` on concatenated strings**
```java
String a = new String("hello");
String b = "hel" + "lo";       // pooled "hello"
String c = a.intern();
System.out.println(b == c);    // true
```
Output: `true`
Explanation: `b` is pooled literal `"hello"`. `a.intern()` finds pooled `"hello"` (from `b`), returns it. Both `b` and `c` point to pooled object.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `+` concatenation in loops | O(n^2) allocations, GC pressure | Use `StringBuilder` outside loop |
| `String.format()` in hot paths | Allocates Formatter + StringBuilder | Avoid in loops; use concatenation or `MessageFormat` |
| `new String("literal")` | Bypasses pool, extra allocation | Use `"literal"` directly |
| `substring()` on large strings (pre-Java 7u6) | Retains reference to large char array | Be aware of JVM version |
| `StringBuffer` in single-threaded code | Unnecessary synchronization overhead | Use `StringBuilder` |
| Forgetting `split` discards trailing empties | Missing empty trailing values | Use `split(",", -1)` |

## 7. Production Usage

**Logging optimization:**
```java
// BAD: concatenation even if log level disabled
logger.info("User " + userId + " processed " + count + " items");

// GOOD: parameterized logging
logger.info("User {} processed {} items", userId, count);
// SLF4J/Logback checks level BEFORE allocating message
```

**JSON building:**
```java
// DON'T build JSON manually
String json = "{\"id\":" + id + ",\"name\":\"" + name + "\"}"; // error-prone, escaping issues

// DO use library
ObjectNode node = JsonNodeFactory.instance.objectNode();
node.put("id", id);
node.put("name", name);
String json = node.toString(); // Jackson handles escaping, types
```

**Batch processing:**
```java
StringBuilder report = new StringBuilder(10_000); // pre-size for expected output
for (Record r : records) {
    report.append(r.getId()).append(",")
          .append(r.getTimestamp()).append(",")
          .append(r.getAmount()).append("\n");
}
String result = report.toString();
```

**String interning for repeated values:**
```java
// In parser with repeated field names
String field = parser.nextField().intern();
// Reduces memory for repeated keys across millions of messages
```

## 8. Advanced Details

- **Compact strings (Java 9+):** `String` stores `byte[]` (not `char[]`) for Latin-1 strings. Reduces memory by 50%. Internal `coder` flag (LATIN1 or UTF16).
- **`StringBuilder` capacity growth:** Default 16. Grows by `(oldCapacity * 2) + 2`. Pre-size: `new StringBuilder(expectedLength)`.
- **`String.concat()` vs `+`:** `concat()` only works with `String` argument. `+` works with any type (auto-converts via `String.valueOf()`).
- **`String.valueOf()` vs `toString()`:** `valueOf(null)` returns `"null"`. `toString()` on null throws NPE.
- **`String.intern()` is synchronized:** High concurrency `intern()` causes contention. Java 9+ improved pool implementation.
- **G1 String Deduplication:** `-XX:+UseStringDeduplication` with G1 GC. Automatically deduplicates String char arrays.
- **`String.replace()` vs `replaceAll()`:** `replace()` takes literal char/string. `replaceAll()` takes regex (much slower). Use `replace()` unless regex needed.
- **`String.split()` regex overhead:** `split(regex)` compiles regex each time. For repeated splits, pre-compile: `Pattern.compile(",").split(s)`.
- **`StringBuilder` vs array of chars:** For very large strings (MBs), consider `char[]` or `byte[]` directly to avoid intermediate copies.

## 9. Interview Questions And Answers

### Beginner
Q: Why is `String` immutable in Java? What are the advantages?
A: `String` is immutable because:
1. **String pool safety**: Immutable objects can be freely shared between multiple references without risk of modification.
2. **Security**: Passwords, file paths, and other sensitive strings cannot be modified after validation.
3. **Hash code caching**: hashCode can be cached, making HashMap keys efficient.
4. **Thread safety**: Immutable objects are inherently thread-safe — no synchronization needed.

### Intermediate
Q: What is the difference between `StringBuilder` and `StringBuffer`? When would you use each?
A: `StringBuilder` (Java 5+) is mutable and non-thread-safe. `StringBuffer` is mutable and thread-safe (all methods are `synchronized`).

Use `StringBuilder` in single-threaded contexts (most application code). Use `StringBuffer` only when multiple threads modify the same string buffer concurrently (rare).

Performance: `StringBuilder` is 20-30% faster because it avoids synchronization overhead.

### Senior
Q: A reporting job generates a 50 MB CSV by concatenating strings in a loop. The job causes frequent GC pauses. Explain why and provide an optimized implementation.
A: Root cause: `report += row` in a loop creates a new `String` on each iteration. For 50 MB output with ~500k rows, each intermediate string copies all previous characters. Total allocations: O(n^2) in string length. This fills the young generation, triggers frequent minor GCs, and may promote large arrays to old generation.

Optimized:
```java
// Pre-calculate approximate size
int rows = 500_000;
int cols = 10;
int avgColLength = 15;
int totalLength = rows * (cols * avgColLength + 1); // +1 for newline

StringBuilder sb = new StringBuilder(totalLength);
for (Row row : rows) {
    sb.append(row.id).append(',')
      .append(row.name).append(',')
      .append(row.amount).append('\n');
}
String report = sb.toString();
```

Further optimization for very large reports: write directly to file via `BufferedWriter` or `PrintWriter`, avoiding holding entire report in memory:
```java
try (BufferedWriter w = Files.newBufferedWriter(path)) {
    for (Row row : rows) {
        w.write(row.id + "," + row.name + "," + row.amount);
        w.newLine();
    }
}
```

### Tricky
Q: You have `String s = "hello"; s = s + " world";`. Does this modify the original String object? What happens to the original "hello" in the String pool? Can it be garbage collected?
A: No, `s = s + " world"` does NOT modify the original `"hello"` String. It creates a NEW String object `"hello world"` and assigns it to variable `s`. The original `"hello"` object in the pool is unchanged.

Can `"hello"` be GC'd? In this case, no — it remains in the String pool because:
1. Class loading interns all string literals in the class constant pool
2. The `"hello"` literal is referenced by the class's constant pool
3. As long as the class is loaded, `"hello"` stays in the pool

However, if `"hello"` was created at runtime via `new String("hello")` and NOT referenced elsewhere, it COULD be GC'd (only the pooled literal from class loading remains, the `new String()` heap object can be collected).

Key insight: String literals are GC roots (referenced by classloader). Runtime-created strings are GC'd normally.

## 10. Final 30-Second Answer

`String` = immutable (`+` creates new objects). `StringBuilder` = mutable, non-thread-safe, efficient for loops. `StringBuffer` = mutable, synchronized. **Never use `+` in loops** — O(n^2) allocations. StringBuilder in O(n). `String.format()` allocates heavily — avoid hot paths. `substring()` copies in Java 7+. `split()` discards trailing empties. `intern()` returns pooled instance. Java 9+ compact strings (`byte[]`). Pre-size StringBuilder for known lengths. Use logging parameterization. Write large output to streams, not StringBuilder.