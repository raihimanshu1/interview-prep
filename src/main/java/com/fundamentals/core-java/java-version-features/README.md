# Java 8 → 17 → 21 — What Changed? (Explained Simply)

## Chapter 1: Why Should You Care About Java Versions?

### The "Phone Upgrade" Analogy

```
Java 8  = iPhone 6  (2014) — revolutionary, changed everything
Java 11 = iPhone X  (2018) — solid, important improvements
Java 17 = iPhone 14 (2021) — modern, polished, LTS
Java 21 = iPhone 16 (2023) — latest, virtual threads, patterns
```

Most companies are still on Java 8 or Java 11. But Java 17 and 21 bring **huge productivity improvements**. If you're interviewing for a senior role, you NEED to know what changed.

### What This Guide Covers

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  We'll cover:                                                │
│                                                             │
│  Java 8  (2014) — Lambda, Streams, Optional, Date/Time API  │
│  Java 9  (2017) — Module System, Collection factories       │
│  Java 10 (2018) — Local variable type inference (var)       │
│  Java 11 (2018) — HTTP Client, String methods, LTS         │
│  Java 12-16 — Switch expressions, Text blocks, Records     │
│  Java 17 (2021) — Sealed classes, Pattern matching, LTS    │
│  Java 21 (2023) — Virtual threads, Pattern matching switch │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Chapter 2: Java 8 — The Game Changer (2014)

### Before Java 8: The Pain

```java
// ─── THE OLD WAY (Java 7 and earlier) ───

// Sorting a list of names by length:
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Dave");

// You had to write an ANONYMOUS INNER CLASS:
Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return Integer.compare(a.length(), b.length());
    }
});
// So much code for something so simple!

// Iterating through a list to print:
for (String name : names) {
    System.out.println(name);
}
// Every. Single. Time. You write this loop.

// Checking if a value is present:
String result = someFunction();
if (result != null) {
    System.out.println(result);
} else {
    System.out.println("Default");
}
// Null checks everywhere! NullPointerException lurking!
```

### Feature 1: Lambda Expressions

**What it solves:** Anonymous inner classes are too verbose.

```java
// ─── JAVA 7: The old way (SO MUCH CODE) ───
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Button clicked!");
    }
});

// ─── JAVA 8: Lambda (CLEAN!) ───
button.addActionListener(e -> System.out.println("Button clicked!"));
//                      ↑
//            "e goes to print this message"
//            Read as: "given e, do this"
```

**More examples:**
```java
// ─── SORTING ───
// Old way:
Collections.sort(names, new Comparator<String>() {
    public int compare(String a, String b) {
        return Integer.compare(a.length(), b.length());
    }
});

// Java 8:
names.sort((a, b) -> Integer.compare(a.length(), b.length()));
//        ↑        ↑
//   "sort using: given a and b, compare lengths"

// Even shorter:
names.sort(Comparator.comparingInt(String::length));
//                          ↑ method reference
```

```java
// ─── THREADS ───
// Old way:
new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("Running in thread");
    }
}).start();

// Java 8:
new Thread(() -> System.out.println("Running in thread")).start();
```

**Real-world use case:**
```java
// Instead of writing loops everywhere, pass behavior as data.

// Old way: Write a method for every operation
List<Order> heavyOrders = new ArrayList<>();
for (Order o : allOrders) {
    if (o.getTotal() > 1000) {
        heavyOrders.add(o);
    }
}

// Java 8: Pass the condition as a lambda
List<Order> heavyOrders = allOrders.stream()
    .filter(o -> o.getTotal() > 1000)  // ← condition as lambda
    .collect(Collectors.toList());
```

### Feature 2: Streams API

**What it solves:** Loops are imperative, verbose, and error-prone. Streams let you express "what" not "how".

```java
// ─── THE OLD WAY (imperative) ───
// "HOW" to do it: create list, loop, check condition, add to result

List<String> result = new ArrayList<>();
for (String name : names) {
    if (name.startsWith("A")) {
        result.add(name.toUpperCase());
    }
}
Collections.sort(result);


// ─── JAVA 8 STREAMS (declarative) ───
// "WHAT" to do: filter, map, sort, collect

List<String> result = names.stream()
    .filter(name -> name.startsWith("A"))  // keep names starting with A
    .map(name -> name.toUpperCase())       // convert to uppercase
    .sorted()                              // sort alphabetically
    .collect(Collectors.toList());         // collect into a list

// Key insight: Each step is a building block.
// You can READ this like English:
// "Take names, filter those starting with A,
//  convert to uppercase, sort, collect to list."
```

**Real-world example: Order processing system**
```java
// ─── WITHOUT STREAMS (Java 7) ───
// 30 lines of code. Easy to miss bugs.

List<Order> validOrders = new ArrayList<>();
for (Order o : allOrders) {
    if (o.getStatus() != OrderStatus.CANCELLED
        && o.getTotal() > 0) {
        validOrders.add(o);
    }
}

Map<String, List<Order>> byCustomer = new HashMap<>();
for (Order o : validOrders) {
    String custId = o.getCustomerId();
    if (!byCustomer.containsKey(custId)) {
        byCustomer.put(custId, new ArrayList<>());
    }
    byCustomer.get(custId).add(o);
}

List<String> reports = new ArrayList<>();
for (Map.Entry<String, List<Order>> entry : byCustomer.entrySet()) {
    double total = 0;
    for (Order o : entry.getValue()) {
        total += o.getTotal();
    }
    reports.add(entry.getKey() + ": $" + total);
}

// ─── WITH STREAMS (Java 8) ───
// 5 lines. Clear intent. No bugs hiding in loops.

List<String> reports = allOrders.stream()
    .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
    .filter(o -> o.getTotal() > 0)
    .collect(Collectors.groupingBy(Order::getCustomerId))
    .entrySet().stream()
    .map(e -> e.getKey() + ": $" +
              e.getValue().stream().mapToDouble(Order::getTotal).sum())
    .collect(Collectors.toList());
```

**Key Stream operations:**
```java
List<String> list = List.of("apple", "banana", "cherry", "date", "elderberry");

// filter: keep elements that match a condition
list.stream()
    .filter(s -> s.length() > 5)           // ["banana", "cherry", "elderberry"]
    .collect(Collectors.toList());

// map: transform each element
list.stream()
    .map(String::toUpperCase)               // ["APPLE", "BANANA", "CHERRY", ...]
    .collect(Collectors.toList());

// flatMap: flatten nested lists
List<List<String>> nested = List.of(List.of("a","b"), List.of("c","d"));
nested.stream()
    .flatMap(Collection::stream)            // ["a", "b", "c", "d"]
    .collect(Collectors.toList());

// sorted: sort elements
list.stream()
    .sorted()                                // alphabetical order
    .collect(Collectors.toList());

// distinct: remove duplicates
list.stream()
    .distinct()                              // unique values only
    .collect(Collectors.toList());

// limit/skip: pagination
list.stream()
    .skip(2)                                 // skip first 2
    .limit(2)                                // take next 2
    .collect(Collectors.toList());           // ["cherry", "date"]

// reduce: combine all elements into one
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);            // sum all numbers

// collect: group by, partition, etc.
Map<Integer, List<String>> byLength = list.stream()
    .collect(Collectors.groupingBy(String::length));
// {5=[apple], 6=[banana, cherry], 4=[date], 9=[elderberry]}
```

### Feature 3: Optional

**What it solves:** NullPointerException is the most common bug in Java. Optional forces you to HANDLE the "no value" case.

```java
// ─── BEFORE OPTIONAL (Null checking everywhere) ───

public String getCity(User user) {
    if (user != null) {                    // ← null check 1
        Address address = user.getAddress();
        if (address != null) {             // ← null check 2
            return address.getCity();
        }
    }
    return "Unknown";
}
// What if I forget a null check? 💥 NullPointerException!
```

```java
// ─── WITH OPTIONAL (Compiler forces you to handle absence) ───

public Optional<String> getCity(User user) {
    return Optional.ofNullable(user)
        .map(User::getAddress)             // returns Optional<Address>
        .map(Address::getCity);            // returns Optional<String>
        // If any step returns null, the result is Optional.empty()
        // No NullPointerException possible!
}

// The CALLER must handle the "maybe no value" case:
String city = getCity(user)
    .orElse("Unknown");                    // provide default

// Or throw an exception if value must be present:
String city = getCity(user)
    .orElseThrow(() -> new NotFoundException("User has no city"));

// Or run different logic:
getCity(user).ifPresentOrElse(
    city -> System.out.println("City: " + city),
    () -> System.out.println("No city found")
);
```

**Real-world: Service layer**
```java
// ─── OLD WAY ───
public User findUser(Long id) {
    User user = userRepository.findById(id);
    if (user == null) {
        throw new UserNotFoundException(id);
    }
    return user;
}

// ─── JAVA 8 WAY ───
public User findUser(Long id) {
    return userRepository.findById(id)      // returns Optional<User>
        .orElseThrow(() -> new UserNotFoundException(id));
}
```

### Feature 4: Default Methods in Interfaces

**What it solves:** Before Java 8, adding a method to an interface broke ALL implementing classes.

```java
// ─── THE PROBLEM ───
// Interface with 100 implementations
interface PaymentGateway {
    void processPayment(Order order);
    // void refund(Order order);  // ← Want to add this?
    // But ALL 100 implementations would BREAK!
}

// ─── JAVA 8 SOLUTION: DEFAULT METHOD ───
interface PaymentGateway {
    void processPayment(Order order);
    
    default void refund(Order order) {
        // Default implementation
        // Old implementations DON'T break!
        // They inherit this method automatically
        throw new UnsupportedOperationException("Refund not supported");
    }
}
```

```java
// ─── REAL EXAMPLE: java.util.Collection ───
// Java 8 added these methods to Collection interface WITHOUT breaking anyone:

list.removeIf(s -> s.length() < 3);      // Remove short strings
list.replaceAll(String::toUpperCase);     // Transform all elements
list.sort(Comparator.naturalOrder());     // Sort in place
list.forEach(System.out::println);        // Print each element

// These were added via DEFAULT METHODS.
// Every existing ArrayList, LinkedList, HashSet just got these for free!
```

### Feature 5: Date/Time API (java.time)

**What it solves:** The old `java.util.Date` was TERRIBLE — mutable, not thread-safe, confusing.

```java
// ─── THE OLD WAY (BROKEN) ───
Date date = new Date(2024, 1, 1);  // Month is 0-based! January = 0
// ^^ This is DEPRECATED. Don't use it.
// Months are 0-indexed. Years start at 1900. Hours are 0-23.
// NOBODY can remember this.

Calendar cal = Calendar.getInstance();
cal.set(Calendar.MONTH, Calendar.JANUARY);
cal.set(Calendar.DAY_OF_MONTH, 1);
cal.set(Calendar.YEAR, 2024);
Date date = cal.getTime();
// 5 lines just to create "January 1, 2024"

// ─── JAVA 8 (CLEAN) ───
LocalDate date = LocalDate.of(2024, Month.JANUARY, 1);
//               ^--- Less typing, clear intent

LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plusDays(1);
boolean isLeap = today.isLeapYear();

// Time with timezone:
ZonedDateTime meeting = ZonedDateTime.of(
    2024, 1, 1, 10, 0, 0, 0,
    ZoneId.of("America/New_York")
);

// Duration between two dates:
long days = ChronoUnit.DAYS.between(startDate, endDate);
```

---

## Chapter 3: Java 9-11 — The Bridge (2017-2018)

### Feature: Collection Factory Methods (Java 9)

**What it solves:** Creating small, immutable lists required too much code.

```java
// ─── BEFORE JAVA 9 ───
List<String> list = new ArrayList<>();
list.add("a");
list.add("b");
list.add("c");
list = Collections.unmodifiableList(list);
// 5 lines just for a read-only list!

// Or using Arrays.asList (but it's not truly immutable):
List<String> list = Arrays.asList("a", "b", "c");
list.add("d");  // 💥 UnsupportedOperationException at RUNTIME!

// ─── JAVA 9 ───
List<String> list = List.of("a", "b", "c");       // truly immutable
Set<String> set = Set.of("a", "b", "c");           // truly immutable
Map<String, Integer> map = Map.of("a", 1, "b", 2); // truly immutable

list.add("d");  // 💥 UnsupportedOperationException at COMPILE... no, at RUNTIME
// But intent is clear: this is an IMMUTABLE list from the start
```

### Feature: Local Variable Type Inference (Java 10)

**What it solves:** Writing type names twice is redundant.

```java
// ─── BEFORE JAVA 10 ───
// Type is written TWICE:
Map<String, List<Order>> ordersByCustomer = new HashMap<String, List<Order>>();
// ↑                              ↑
// Both say the same thing!

// ─── JAVA 10 ───
var ordersByCustomer = new HashMap<String, List<Order>>();
// ↑
// Compiler INFERS the type. Less to read. Less to type.
```

**When to use var (and when NOT to):**

```java
// ✅ GOOD use of var — type is OBVIOUS from the right side:
var list = new ArrayList<String>();        // Clearly ArrayList<String>
var reader = new BufferedReader(file);     // Clearly BufferedReader
var order = orderRepository.findById(123); // Clearly Optional<Order>

// ❌ BAD use of var — type is NOT obvious:
var result = doSomething();                // What type? No idea!
var data = service.getData();              // Map? List? String?
// Don't use var when the type isn't clear from context
```

### Feature: HTTP Client (Java 11)

**What it solves:** The old `HttpURLConnection` was painful to use.

```java
// ─── BEFORE JAVA 11 (using HttpURLConnection) ───
URL url = new URL("https://api.example.com/users");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
conn.setRequestProperty("Accept", "application/json");

int responseCode = conn.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(conn.getInputStream())
);
String inputLine;
StringBuilder response = new StringBuilder();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
// 15+ lines for a simple GET request!

// ─── JAVA 11 ───
var client = HttpClient.newHttpClient();
var request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users"))
    .header("Accept", "application/json")
    .GET()
    .build();

var response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
// 5 lines for the same thing!
```

### Feature: New String Methods (Java 11)

```java
// ─── JAVA 11 STRING HELPER METHODS ───

// isBlank(): check if string is empty or only whitespace
"   ".isBlank();    // true (Java 11)
"   ".isEmpty();    // false (empty is NOT the same as blank)

// lines(): split into lines as a Stream
"line1\nline2\nline3".lines()
    .map(String::toUpperCase)
    .forEach(System.out::println);  // LINE1 LINE2 LINE3

// strip() vs trim(): strip handles Unicode whitespace
"\u2000hello".trim();   // "\u2000hello" — trim doesn't remove this space
"\u2000hello".strip();  // "hello" — strip DOES remove it

// repeat(): repeat the string N times
"VIP".repeat(3);    // "VIPVIPVIP"
"-".repeat(10);     // "----------"
```

---

## Chapter 4: Java 12-16 — Preview Features Becoming Official

### Feature: Switch Expressions (Java 14 — official)

**What it solves:** Old switch is verbose, error-prone (forgot break?), and doesn't return a value.

```java
// ─── OLD SWITCH (Java 7 and earlier) ───
String result;
switch (day) {
    case MONDAY:
    case FRIDAY:
    case SUNDAY:
        result = "Beach day";
        break;                  // ← Easy to forget!
    case TUESDAY:
        result = "Workout day";
        break;
    default:
        result = "Regular day";
        // Forgotten break? Falls through! BUG!
}
// 15 lines. Easy to miss break statements.

// ─── JAVA 14+ SWITCH EXPRESSION ───
String result = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> "Beach day";     // → No break needed!
    case TUESDAY                 -> "Workout day";
    case WEDNESDAY, THURSDAY     -> "Regular day";
    default                      -> "Unknown day";
};
// 7 lines. Clean. Returns a value. No fall-through bugs.
```

**Switch with yield (for multi-line cases):**
```java
String result = switch (day) {
    case MONDAY -> {
        // Multi-line block
        System.out.println("Starting week!");
        yield "Beach day";    // ← yield returns the value
    }
    case FRIDAY -> "Party day";
    default -> "Regular day";
};
```

### Feature: Text Blocks (Java 15 — official)

**What it solves:** Multi-line strings with escaping was a nightmare.

```java
// ─── BEFORE TEXT BLOCKS (HTML/JSON/SQL) ───
String json = "{\n" +
              "  \"name\": \"Alice\",\n" +
              "  \"age\": 30,\n" +
              "  \"city\": \"New York\"\n" +
              "}";
// Unreadable. Error-prone. Plus signs everywhere.

// ─── JAVA 15+ TEXT BLOCKS ───
String json = """
    {
      "name": "Alice",
      "age": 30,
      "city": "New York"
    }
    """;
// Just write it. No escaping. No concatenation. Beautiful.
```

**More examples:**
```java
// SQL queries:
String query = """
    SELECT u.name, o.total
    FROM users u
    JOIN orders o ON u.id = o.user_id
    WHERE o.total > 1000
    ORDER BY o.total DESC
    """;
// You can read the SQL! No string concatenation!

// HTML:
String html = """
    <html>
      <body>
        <h1>Welcome, %s!</h1>
        <p>You have %d messages.</p>
      </body>
    </html>
    """.formatted(name, count);
// Clean HTML template with placeholders
```

### Feature: Records (Java 16 — official)

**What it solves:** Data carrier classes require too much boilerplate (constructor, getters, equals, hashCode, toString).

```java
// ─── BEFORE RECORDS (A LOT of code for a simple data class) ───
public class Person {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}
// ~50 lines just to hold name and age!

// ─── JAVA 16+ RECORD ───
public record Person(String name, int age) {}
// ↑ That's it. 1 line. Java generates:
//   - Constructor: Person(String name, int age)
//   - Accessors: name(), age()
//   - equals() and hashCode() based on ALL fields
//   - toString(): Person[name=Alice, age=30]
//   - All fields are final (immutable!)
```

**Real-world DTO:**
```java
// API response:
public record ApiResponse<T>(
    int statusCode,
    String message,
    T data,
    long timestamp
) {}

// Usage:
var response = new ApiResponse<>(200, "OK", userList, System.currentTimeMillis());
System.out.println(response.message());  // "OK"
System.out.println(response.data());     // the user list
```

---

## Chapter 5: Java 17 LTS — The Modern Java (2021)

### Feature: Sealed Classes

**What it solves:** You want to RESTRICT which classes can extend a parent class.

```java
// ─── THE PROBLEM ───
// You want to model "Payment" as either Card, UPI, or NetBanking.
// But WITHOUT sealed, ANYONE can extend Payment:

public class Payment { ... }
public class Card extends Payment { ... }          // ✅ Allowed
public class UPI extends Payment { ... }           // ✅ Allowed
public class Crypto extends Payment { ... }        // 🤔 Should this be allowed?
public class RandomHack extends Payment { ... }    // ❌ Should NOT be allowed!

// Without sealed, you can't control this.
```

```java
// ─── JAVA 17: SEALED CLASSES ───
// Only Card, UPI, and NetBanking can extend Payment.
// NO other class can.

public abstract sealed class Payment
    permits Card, UPI, NetBanking {
    //  ^^^^^^                      ^^^^^^^^^^^^^^
    // "This class is SEALED.       "Only these classes
    //  Only permitted classes       can extend me."
    //  can extend me."
    public abstract double getAmount();
}

// These are the ONLY permitted subclasses:
final class Card extends Payment {           // must be final, sealed, or non-sealed
    private String cardNumber;
    public double getAmount() { return 100; }
}

final class UPI extends Payment {
    private String upiId;
    public double getAmount() { return 200; }
}

final class NetBanking extends Payment {
    private String bankName;
    public double getAmount() { return 300; }
}

// This would NOT compile:
// class Crypto extends Payment { }  💥 ERROR! Crypto not in permits list!
```

**Why sealed classes matter:**
```java
// 1. EXHAUSTIVE SWITCH (compiler knows ALL possible types)
public String processPayment(Payment payment) {
    return switch (payment) {
        case Card c       -> "Processing card payment: " + c.getAmount();
        case UPI u        -> "Processing UPI payment: " + u.getAmount();
        case NetBanking n -> "Processing net banking: " + n.getAmount();
        // No "default" needed! Compiler knows these are ALL the types.
        // If you add a new type, switch must be updated — compiler catches it!
    };
}
```

### Feature: Pattern Matching for instanceof (Java 16 → 17)

**What it solves:** The classic "check type, cast, use variable" pattern was 3 steps when it should be 1.

```java
// ─── OLD WAY (Java 16 and earlier) ───
if (obj instanceof String) {
    String s = (String) obj;  // ← Separate cast!
    System.out.println(s.length());
}
// 3 lines. Repeated pattern.

// ─── JAVA 17 PATTERN MATCHING ───
if (obj instanceof String s) {
    //       ↑           ↑
    //  "Is obj a String? If so, assign to variable s"
    System.out.println(s.length());
    //     ↑ Use s directly. No cast needed!
}
// 1 line. Compiler handles the cast.
```

**More complex example:**
```java
public double getArea(Shape shape) {
    if (shape instanceof Circle c) {         // Check AND cast in one step
        return Math.PI * c.radius() * c.radius();
    }
    if (shape instanceof Rectangle r) {
        return r.width() * r.height();
    }
    if (shape instanceof Triangle t) {
        return 0.5 * t.base() * t.height();
    }
    return 0;
}
```

---

## Chapter 6: Java 21 LTS — The Cutting Edge (2023)

### Feature 1: Virtual Threads (Project Loom)

**What it solves:** Threads are expensive (1 MB stack → 1M threads = 1 TB RAM). Virtual threads are cheap (few KB).

```java
// ─── THE PROBLEM WITH PLATFORM THREADS ───
// Each thread costs ~1 MB of stack memory
// 100,000 threads = 100 GB RAM! Not feasible.

// Traditional way to handle 10,000 concurrent requests:
ExecutorService executor = Executors.newFixedThreadPool(200);
// Only 200 threads! Why? Can't create 10,000 — would crash.
// But 200 threads means some requests must WAIT.
// Wasted potential!

// ─── JAVA 21 VIRTUAL THREADS ───
// Virtual threads cost ~few KB each.
// 100,000 threads = ~100 MB RAM. Completely feasible.

try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Create ONE MILLION virtual threads:
    for (int i = 0; i < 1_000_000; i++) {
        int taskId = i;
        executor.submit(() -> {
            System.out.println("Task " + taskId + " running");
            Thread.sleep(1000);  // ← This would be expensive with platform threads!
            return taskId;
        });
    }
}
// With platform threads: 1 million threads = 1 TB RAM → CRASH
// With virtual threads:  1 million threads = ~1 GB RAM → Fine!
```

**Real-world: Web server handling 100K requests**
```java
// ─── BEFORE VIRTUAL THREADS ───
@RestController
public class OrderController {
    
    @GetMapping("/orders/{id}")
    public Order getOrder(@PathVariable Long id) {
        // Each request uses a PLATFORM thread
        // Thread pool limited to 200 threads
        // If all 200 are busy waiting for database... new requests QUEUE
        // Queue grows → timeout → 503 Service Unavailable
        
        Order order = orderService.findById(id);   // Blocks thread
        User user = userService.findById(order.getUserId());  // Blocks thread
        List<Item> items = itemService.findByOrderId(id);     // Blocks thread
        
        return new OrderResponse(order, user, items);
    }
}

// ─── WITH VIRTUAL THREADS ───
@RestController
public class OrderController {
    
    @GetMapping("/orders/{id}")
    public Order getOrder(@PathVariable Long id) {
        // Each request uses a VIRTUAL thread
        // Can create 100,000+ without issues
        // When thread blocks on DB call → JVM parks it (costs nothing)
        // When DB responds → JVM resumes it
        
        Order order = orderService.findById(id);   // "Blocks" — JVM parks thread
        User user = userService.findById(order.getUserId());  // Parks again
        List<Item> items = itemService.findByOrderId(id);     // Parks again
        
        return new OrderResponse(order, user, items);
    }
}
// Same code! Just different Executor.
// No thread pool limit. Each "blocking" call parks the virtual thread.
```

**When to use virtual threads:**
```java
// ✅ USE Virtual threads for:
//   - I/O bound work (calls to DB, REST APIs, file system)
//   - Many concurrent requests (web servers)
//   - Any task that blocks frequently

// ❌ DON'T use Virtual threads for:
//   - CPU-intensive work (video encoding, image processing, complex calculations)
//   - Use platform threads for CPU work (they actually run in parallel)
//   - Long-running synchronized blocks (virtual threads can't be parked inside synchronized)

// ─── HOW TO ENABLE ───
// Spring Boot 3.2+ with Java 21:
// application.properties:
//   spring.threads.virtual.enabled=true
// That's it! All your @Async, @EventListener, MVC requests become virtual threads.

// Programmatically:
Thread vThread = Thread.startVirtualThread(() -> {
    System.out.println("Running in virtual thread!");
});

try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> processOrder(order));
}
```

### Feature 2: Pattern Matching for Switch (Java 21 — official)

**What it solves:** Complex if-else chains checking types can be replaced with a clean switch.

```java
// ─── BEFORE JAVA 21 (nested if-else) ───
public String process(Object obj) {
    if (obj == null) {
        return "Null input";
    }
    
    if (obj instanceof String s) {
        return "String of length " + s.length();
    }
    
    if (obj instanceof Integer i) {
        if (i > 0) {
            return "Positive integer: " + i;
        } else {
            return "Non-positive integer: " + i;
        }
    }
    
    if (obj instanceof List<?> list) {
        return "List with " + list.size() + " items";
    }
    
    return "Unknown type";
}
// Lots of if-else. Hard to read. Easy to miss a case.
```

```java
// ─── JAVA 21: PATTERN MATCHING SWITCH ───
public String process(Object obj) {
    return switch (obj) {
        case null                        -> "Null input";  // ← Handle null!
        case String s                    -> "String of length " + s.length();
        case Integer i when i > 0        -> "Positive integer: " + i;  // ← Guard!
        case Integer i                   -> "Non-positive integer: " + i;
        case List<?> list                -> "List with " + list.size() + " items";
        default                          -> "Unknown type";
    };
}
// Clean. Exhaustive. Null-safe. Guards (when clause)!
```

**Real-world: Discount calculator**
```java
public double calculateDiscount(Object customer) {
    return switch (customer) {
        // VIP customers get 20% off
        case VipCustomer v when v.yearsSinceJoining() > 5
            -> v.getPurchaseTotal() * 0.20;
        
        case VipCustomer v             -> v.getPurchaseTotal() * 0.15;
        
        // Regular customers: holiday discount
        case RegularCustomer r when isHolidaySeason()
            -> r.getPurchaseTotal() * 0.10;
        
        case RegularCustomer r         -> r.getPurchaseTotal() * 0.05;
        
        // New customers: flat $10 off
        case NewCustomer n             -> Math.min(10, n.getFirstPurchaseTotal());
        
        // Null check built in!
        case null                      -> 0.0;
        
        default                        -> 0.0;
    };
}
```

### Feature 3: Record Patterns (Java 21)

**What it solves:** Destructuring records in pattern matching required manual field extraction.

```java
// ─── BEFORE RECORD PATTERNS ───
record Point(int x, int y) {}
record Line(Point start, Point end) {}

public void printLine(Object obj) {
    if (obj instanceof Line line) {
        Point start = line.start();  // ← Manual extraction
        Point end = line.end();      // ← Manual extraction
        System.out.println("Line from (" + start.x() + "," + start.y()
            + ") to (" + end.x() + "," + end.y() + ")");
    }
}
```

```java
// ─── JAVA 21: RECORD PATTERNS ───
public void printLine(Object obj) {
    if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
        //       ↑ Destructure IN the pattern!
        System.out.println("Line from (" + x1 + "," + y1
            + ") to (" + x2 + "," + y2 + ")");
    }
}
```

**Combined with switch (POWERFUL):**
```java
// ─── JSON-like data processing ───
sealed interface JsonValue {}
record JsonString(String value) implements JsonValue {}
record JsonNumber(double value) implements JsonValue {}
record JsonArray(List<JsonValue> items) implements JsonValue {}
record JsonObject(Map<String, JsonValue> fields) implements JsonValue {}

public String formatJson(JsonValue json) {
    return switch (json) {
        case JsonString(var value)    -> "\"" + value + "\"";
        case JsonNumber(var value)    -> String.valueOf(value);
        case JsonArray(var items)     -> items.stream()
            .map(this::formatJson)
            .collect(Collectors.joining(", ", "[", "]"));
        case JsonObject(var fields)   -> fields.entrySet().stream()
            .map(e -> "\"" + e.getKey() + "\": " + formatJson(e.getValue()))
            .collect(Collectors.joining(", ", "{", "}"));
        case null                     -> "null";
    };
}
// All cases covered. No casting. No manual extraction. Beautiful.
```

### Feature 4: Sequenced Collections (Java 21)

**What it solves:** Getting first/last element or reversing a collection was inconsistent.

```java
// ─── BEFORE JAVA 21 (inconsistent APIs) ───
// ArrayList:
list.get(0);                    // First element
list.get(list.size() - 1);     // Last element

// LinkedList:
linkedList.getFirst();          // First (different method!)
linkedList.getLast();           // Last

// TreeSet:
treeSet.first();                // First (different method!)

// Reversed order:
Collections.reverse(list);      // Modifies the list
// Or create a new list and reverse it

// ─── JAVA 21: SEQUENCED COLLECTIONS ───
// Same methods for ALL collections!

SequencedCollection<String> seq = new ArrayList<>();

seq.getFirst();                 // First element (consistent!)
seq.getLast();                  // Last element (consistent!)
seq.addFirst("X");              // Add at front (O(1) for ArrayDeque, O(n) for ArrayList)
seq.addLast("Z");               // Add at end

SequencedCollection<String> reversed = seq.reversed();  // Get reversed view!
//                          ↑ Returns a REVERSED view (doesn't modify original)

// Works for ALL collections:
SequencedSet<String> set = new LinkedHashSet<>();
set.getFirst();                          // First in insertion order
set.getLast();                           // Last in insertion order

SequencedMap<String, Integer> map = new LinkedHashMap<>();
map.firstEntry();                        // First entry
map.lastEntry();                         // Last entry
map.pollFirstEntry();                    // Remove and return first
```

---

## Chapter 7: Version Migration Guide

### Quick Reference: What to Use When

```
FEATURE                   JAVA VERSION    STATUS
────────────────────────  ─────────────  ──────────────────────
Lambda expressions        8              ✅ Use everywhere
Streams API               8              ✅ Use everywhere
Optional                  8              ✅ Use everywhere
Date/Time API (java.time) 8              ✅ Use everywhere
Default methods           8              ✅ Use everywhere

Collection factory methods 9             ✅ Prefer over Arrays.asList
Optional improvements      9             ✅ orElseThrow, ifPresentOrElse

Local variable type (var) 10            ✅ Use with clear right-side

HttpClient                11            ✅ Prefer over HttpURLConnection
String helpers (isBlank)  11            ✅ Use

Switch expressions        14            ✅ Prefer over old switch
Text blocks               15            ✅ Prefer for multi-line strings
Records                   16            ✅ Prefer over data classes

Pattern matching for      16→17         ✅ Use with instanceof
  instanceof
Sealed classes            17            ✅ Use for restricted hierarchies

Virtual threads           21            ✅ Use for I/O-bound work
Pattern matching switch   21            ✅ Prefer over if-else chains
Record patterns           21            ✅ Use when records in patterns
Sequenced collections     21            ✅ Use for first/last/reverse
```

### Summary: What Each Version Means for YOU

```
JAVA 8 — THE MINIMUM
  └─ If your company is still on Java 8, you're in the past
  └─ But Java 8 was SO good that many stayed here for years
  └─ You MUST know: Lambda, Streams, Optional, Date/Time

JAVA 11 — THE BRIDGE
  └─ First LTS after Java 8. Many companies moved here
  └─ var, HttpClient, String methods
  └─ If you're on Java 11 → upgrade to 17 or 21

JAVA 17 — THE MODERN STANDARD
  └─ Current LTS. All new projects should use this (or 21)
  └─ Sealed classes, Records, Pattern matching, Text blocks
  └─ Switch expressions (no fall-through!)
  └─ If you're starting a new project → start here

JAVA 21 — THE FUTURE
  └─ Latest LTS. Virtual threads change EVERYTHING for servers
  └─ Pattern matching switch + record patterns = less code
  └─ Sequenced collections = consistent APIs
  └─ If you're building new services → use this
```

### 30-Second Summary

```
JAVA 8 (2014) — GAME CHANGER
  Lambda: (a, b) -> a.length() - b.length()
  Streams: list.stream().filter().map().collect()
  Optional: .orElseThrow() — never return null
  Date/Time: LocalDate.of(2024, Month.JANUARY, 1)

JAVA 17 (2021) — MODERN JAVA
  Records: public record Person(String name, int age) {}  // 1 line!
  Sealed: permits Card, UPI, NetBanking
  Text blocks: """ { "json": "here" } """
  Pattern matching: if (obj instanceof String s) → use s

JAVA 21 (2023) — VIRTUAL THREADS & PATTERNS
  Virtual threads: 1M threads, not 200
  Pattern switch: switch(obj) { case Integer i → ... }
  Record patterns: if (obj instanceof Point(var x, var y))
  Sequenced: .getFirst(), .getLast(), .reversed()