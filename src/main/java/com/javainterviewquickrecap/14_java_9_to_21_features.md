# Module 5b — Java 9 to 21 Features — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---

## Q1. What are Records, Sealed Classes, and Pattern Matching in modern Java?

### 1. Why This Concept Matters
Modern Java features (Java 14-21) fundamentally change how you write Java code — less boilerplate, better domain modeling, more expressive type checking. Interviewers ask this to test if you're **current with Java evolution**, not stuck on Java 8 patterns.

### 2. Basic Meaning

**Records (Java 16+)**: Transparent carriers for immutable data — auto-generates constructor, getters, equals, hashCode, toString.

**Sealed Classes (Java 17+)**: Restricts which classes/interfaces can extend/implement a type — exhaustive pattern matching becomes possible.

**Pattern Matching (Java 16-21 progressive)**: instanceof, switch, and records can be matched against patterns with automatic variable binding.

### 3. Real Code / Real Example

```java
// =====================================================
// BEFORE: Verbose Java (pre-16)
// =====================================================
public class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // Need: getters, equals, hashCode, toString — all manually!

// =====================================================
// AFTER: Record (Java 16+)
// =====================================================
public record Point(int x, int y) {}
// Automatically provides:
// - Constructor: Point(int x, int y)
// - Getters: x(), y() — NOT getX()/getY()!
// - equals(), hashCode(), toString()
// - All fields are final (immutable)

// Usage:
Point p = new Point(3, 4);
System.out.println(p.x());       // 3
System.out.println(p);            // Point[x=3, y=4]
Point p2 = new Point(3, 4);
System.out.println(p.equals(p2)); // true

// Custom validation in compact constructor:
public record PositivePoint(int x, int y) {
    public PositivePoint {  // NO parameters — compact constructor
        if (x < 0 || y < 0) throw new IllegalArgumentException("Must be positive");
    }
}

// =====================================================
// SEALED CLASSES (Java 17+)
// =====================================================
public sealed class Shape permits Circle, Rectangle, Triangle {
    // Only Circle, Rectangle, Triangle can extend Shape
}

final class Circle extends Shape { 
    double radius; 
}
final class Rectangle extends Shape { 
    double w, h; 
}
// ❌ class Diamond extends Shape { } — COMPILE ERROR! Diamond not in permits

// Sealed interfaces:
public sealed interface PaymentMethod permits CreditCard, PayPal, Crypto {}
record CreditCard(String number, String cvv) implements PaymentMethod {}
record PayPal(String email) implements PaymentMethod {}
record Crypto(String walletAddress) implements PaymentMethod {}

// =====================================================
// PATTERN MATCHING instanceof (Java 16+)
// =====================================================

// BEFORE:
if (obj instanceof String) {
    String s = (String) obj;  // Explicit cast needed
    System.out.println(s.length());
}

// AFTER:
if (obj instanceof String s) {  // Pattern variable! Cast + bind in one step
    System.out.println(s.length());  // No cast needed — s is bound
}

// =====================================================
// PATTERN MATCHING switch (Java 17+ preview, Java 21 standard)
// =====================================================

// BEFORE — Nested if-else:
String formatted;
if (shape instanceof Circle c) {
    formatted = "Circle: " + c.radius();
} else if (shape instanceof Rectangle r) {
    formatted = "Rectangle: " + r.w() + "x" + r.h();
} else {
    formatted = "Unknown";
}

// AFTER — Exhaustive switch with pattern matching:
String formatted = switch (shape) {
    case Circle c -> "Circle: " + c.radius();
    case Rectangle r -> "Rectangle: " + r.w() + "x" + r.h();
    case null -> "No shape";  // Handle null case explicitly!
    default -> "Unknown shape";
};
// Compiler checks EXHAUSTIVENESS for sealed types!
// If Shape is sealed and covered all permits → no default needed
```

### 4. What Happens Internally

**Records are classes, not magic:**
```
record Point(int x, int y) { }

After compilation:
public final class Point extends java.lang.Record {
    private final int x;
    private final int y;
    
    public Point(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
    public boolean equals(Object o) { ... }
    public int hashCode() { ... }
    public String toString() { return "Point[x=" + x + ", y=" + y + "]"; }
}

Key: Records CAN'T extend other classes (implicitly extends Record)
     CAN implement interfaces
     CAN have static methods/fields
     Fields are FINAL — no setter
```

**Pattern Matching compilation:**
```
Source: if (obj instanceof String s) { use(s); }

Bytecode equivalent:
if (obj instanceof String) {
    String s = (String) obj;
    use(s);
}
// Pattern matching is compile-time syntactic sugar
// No performance overhead at runtime
```

### 5. Tricky Interview Cases

**Case 1: Records and inheritance**
```java
record Base(int x) {}  // final — cannot extend!
// ❌ record Sub(int y) extends Base(1) { }  // COMPILE ERROR!

// Workaround: records CAN implement interfaces
interface Named { String name(); }
record Person(String name, int age) implements Named {}
```

**Case 2: Pattern matching exhaustiveness**
```java
// Without sealed classes:
int result = switch (obj) {  // ❌ Won't compile without default!
    case String s -> s.length();
    case Integer i -> i;
    // default -> 0;  // NEEDED!
};

// With sealed classes:
sealed interface Color permits Red, Green, Blue {}
record Red() implements Color {}
record Green() implements Color {}
record Blue() implements Color {}

String hex = switch (color) {  // ✅ No default needed — exhaustive!
    case Red r -> "#FF0000";
    case Green g -> "#00FF00";
    case Blue b -> "#0000FF";
};  // Compiler verifies all cases covered!
```

**Case 3: Guarded patterns (Java 17+)**
```java
int result = switch (obj) {
    // Guard conditions with `when` keyword
    case String s when s.length() > 10 -> s.length();
    case String s -> 0;  // Short strings
    case Integer i when i < 0 -> -1;
    case Integer i -> i;
    default -> 0;
};
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Using records for JPA entities | No default constructor → JPA fails | Use @Entity with regular class, not record |
| Forgetting records are final | Can't extend — may surprise devs | Records ARE data carriers, not base classes |
| Not handling null in pattern matching switch | NullPointerException | Add `case null ->` explicitly |
| Expecting getX() from records | Records use x() not getX() | Use the component accessor methods |

### 7. Production Usage

**Domain events with records:**
```java
// Records are perfect for immutable events
public sealed interface OrderEvent permits OrderCreated, OrderShipped, OrderCancelled {}
public record OrderCreated(Long orderId, String customerId, BigDecimal amount) implements OrderEvent {}
public record OrderShipped(Long orderId, String trackingNumber, LocalDateTime shippedAt) implements OrderEvent {}
public record OrderCancelled(Long orderId, String reason) implements OrderEvent {}

// Processing with exhaustive pattern matching:
public void handle(OrderEvent event) {
    switch (event) {
        case OrderCreated e -> inventoryService.reserve(e.orderId());
        case OrderShipped e -> emailService.sendTracking(e.orderId(), e.trackingNumber());
        case OrderCancelled e -> inventoryService.release(e.orderId());
    }
}
```

### 8. Advanced Details

**Text blocks (Java 15+):**
```java
// BEFORE:
String json = "{\n" +
    "  \"name\": \"Alice\",\n" +
    "  \"age\": 30\n" +
    "}";

// AFTER:
String json = """
    {
      "name": "Alice",
      "age": 30
    }
    """;
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What problem do Records solve?

**A**: Records eliminate boilerplate for data carrier classes. A single line `record Point(int x, int y)` generates constructor, getters (x(), y()), equals, hashCode, toString. They're implicitly final and all fields are private final — making them perfect for immutable data transfer objects (DTOs). No more Lombok or manual code generation for simple data holders.

#### Intermediate

**Q**: How does pattern matching for switch improve code quality?

**A**: It eliminates the if-else instanceof chain with cast. Before: 4 lines per type check (instanceof + cast + variable + usage). After: 1 line. It also enables exhaustiveness checking — with sealed classes, the compiler verifies ALL cases are handled. Missing a case becomes a COMPILE ERROR, not a runtime bug. Guard conditions with `when` add fine-grained logic without nested ifs.

#### Senior

**Q**: Design a payment processing system using sealed classes and records.

**A**: Define `sealed interface PaymentMethod permits CreditCard, PayPal, Crypto`. Each subclass is a record. Define `sealed interface PaymentResult permits Success, Failure, Pending`. Process method returns `PaymentResult` — switch exhaustively handles success (update order), failure (retry logic), pending (queue for webhook). Records ensure immutable event data. Sealed types guarantee exhaustive handling — adding a new payment method or result type triggers compile errors in ALL switch statements, forcing complete implementation.

#### Tricky

**Q**: Can a record have mutable fields?

**A**: No. Records are shallowly immutable — all component fields are `private final`. However, if a component field is a reference to a mutable object (e.g., `List`, `Date`, `int[]`), the record's immutability doesn't extend to that object. Example: `record DataHolder(List<String> items)` — you can call `holder.items().add("new")` because the List reference is final, but the List itself is mutable. For true immutability, use `List.copyOf()` or unmodifiable collections inside the compact constructor.

### 10. Final 30-Second Answer

Records provide concise immutable data carriers. Sealed classes restrict inheritance hierarchies enabling exhaustive pattern matching. Pattern matching (instanceof, switch) eliminates manual casts and if-else chains. Text blocks simplify multi-line strings. Modern Java reduces boilerplate and catches domain errors at compile time.

---

## Q2. Explain key Java 9-21 features: Modules, Text Blocks, Switch Expressions, Virtual Threads.

### 1. Why This Concept Matters
Each Java version brings incremental improvements. Interviewers expect you to know major features through at least Java 17 (LTS) or Java 21 (latest LTS). Being stuck at Java 8 in interviews indicates you haven't kept up with the ecosystem.

### 2. Feature Timeline

| Java | Feature | What It Does |
|------|---------|-------------|
| 9 (2017) | Module System (JPMS) | Strong encapsulation, reliable configuration |
| 10 (2018) | Local-Variable Type Inference (var) | `var list = new ArrayList<String>();` |
| 11 (2018 LTS) | HTTP Client, String methods | `"  ".isBlank()`, `strip()`, `lines()` |
| 12-13 | Switch expressions (preview) | Switch returns value, arrow syntax |
| 14 | Pattern matching instanceof | `if (o instanceof String s)` |
| 15 | Text blocks, Sealed classes (preview) | `""" ... """` |
| 16 | Records (standard), Pattern matching instanceof (standard) | |
| 17 (LTS) | Sealed classes (standard), Pattern matching switch (preview) | |
| 21 (2023 LTS) | Virtual Threads (standard), Pattern matching switch (standard) | |

### 3. Key Features Code Examples

```java
// =====================================================
// var — Local variable type inference (Java 10+)
// =====================================================
var list = new ArrayList<String>();  // Inferred: ArrayList<String>
var stream = list.stream();          // Inferred: Stream<String>
var map = Map.of("a", 1, "b", 2);   // Inferred: Map<String, Integer>

// ❌ NOT allowed:
// var x;                      // Must initialize
// var n = null;               // Can't infer null
// var f = () -> "hello";      // Lambda needs explicit type

// =====================================================
// String new methods (Java 11+)
// =====================================================
"  ".isBlank();                  // true
" Hello ".strip();               // "Hello" (trim Unicode-aware)
"Hello".repeat(3);               // "HelloHelloHello"
"A\nB\nC".lines().toList();      // ["A", "B", "C"]

// =====================================================
// Switch expressions (Java 14+)
// =====================================================
// Old: switch statement (fall-through!)
String result = "";
switch (day) {
    case MONDAY:
    case TUESDAY:
        result = "Weekday";
        break;
    case SATURDAY:
        result = "Weekend";
        break;
}

// New: switch expression (no fall-through!)
String result = switch (day) {
    case MONDAY, TUESDAY, WEDNESDAY -> "Weekday";
    case SATURDAY, SUNDAY -> "Weekend";
    default -> "Unknown";
};

// Also works with yield (for blocks):
String result = switch (day) {
    case MONDAY -> {
        log("Monday processing");
        yield "Start of week";  // Return value from block
    }
    case FRIDAY -> "End of week";
    default -> "Midweek";
};
```

### 4. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Overusing var | Reduced readability for complex types | Use var only when type is obvious from RHS |
| Breaking encapsulation with modules | Reflection fails on internal packages | Export packages explicitly in module-info.java |
| Forgetting switch expressions return value | Treating as statement | Assign switch expression to variable or use arrow syntax |
| Java 8 code patterns in Java 17+ | Missing all improvements | Use records, pattern matching, text blocks |

### 5. Interview Questions And Answers

#### Beginner

**Q**: What's the difference between switch statement and switch expression?

**A**: Switch statements can fall through (need break). Switch expressions use arrow syntax (->), have no fall-through, and RETURN a value. Switch expressions need to be EXHAUSTIVE (cover all cases or have default). They support yield for block bodies. Java 17+ adds pattern matching in switch cases.

#### Intermediate

**Q**: When should you use var vs explicit types?

**A**: Use var when the type is obvious from the right side: `var list = new ArrayList<String>()`, `var map = Map.of(...)`. Avoid var when: (1) Type is not obvious: `var result = process()` — what type is result?; (2) You need to emphasize the type for clarity; (3) The right side doesn't reveal the type (e.g., method returns interface, you need concrete type). Rule: if var makes the code MORE readable, use it. If less, don't.

#### Senior

**Q**: How does the Java Module System (JPMS) affect production deployment?

**A**: JPMS provides: (1) Strong encapsulation — internal APIs (sun.misc.Unsafe) are no longer accessible by default; (2) Reliable configuration — no more ClassNotFoundException from missing transitive dependencies; (3) Module path vs classpath — modules on module path are resolved at startup; (4) jlink — create custom JRE with only required modules, reducing Docker image size from ~200MB to ~40MB for simple apps. In practice, many Spring Boot apps still use classpath jars (classpath mode) even on Java 17, but jlink adoption is growing for containerized deployments.

#### Tricky

**Q**: If you're still on Java 8 at work, what argument would you make to upgrade to Java 17/21?

**A**: (1) **Security** — Java 8 is end-of-life for public updates (Oracle). No security patches. (2) **Performance** — G1GC default in Java 9+ (was ParallelGC in 8), better heap management. Java 17 has ~15% better throughput than Java 8 in most benchmarks. (3) **Productivity** — Records eliminate boilerplate, text blocks simplify SQL/JSON in code, pattern matching reduces if-else chains. Count the lines of code that can be simplified. (4) **Virtual threads (Java 21)** — dramatically simplify concurrent code, no more reactive frameworks needed. Risk: some libraries may not support JPMS (uncommon now). Migration: upgrade to Java 11 first, then 17 (both LTS), then 21. Most code compiles and runs with minor flag adjustments.

### 10. Final 30-Second Answer

Java 9-21 features: var (type inference), records (immutable data), sealed classes (restricted hierarchies), pattern matching (instanceof/switch), text blocks, switch expressions, virtual threads. Each LTS release (8, 11, 17, 21) brings significant improvements. Modern Java reduces boilerplate and enables safer, more expressive code.