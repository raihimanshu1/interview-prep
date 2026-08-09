# Module 1b — OOP & SOLID Principles — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.
> Every Q&A covers all 10 sections: Why it matters | Basic meaning | Real code | Internals | Tricky cases | Common mistakes | Production usage | Advanced details | 4 Q&As | 30-sec answer.

---

## Q1. What are the four pillars of OOP? Explain with real examples.

### 1. Why This Concept Matters
OOP is the foundation of every Java application. Without understanding encapsulation, inheritance, polymorphism, and abstraction, you'll write procedural code in Java classes — leading to fragile, unmaintainable systems. Interviewers ask this to test if you understand the **practical application**, not just definitions.

### 2. Basic Meaning

| Pillar | What It Means | Analogy | Violation Consequence |
|--------|--------------|---------|----------------------|
| **Encapsulation** | Hide internal state, expose controlled access | ATM: you see the screen, not the cash tray | Direct field access → fragile code |
| **Inheritance** | Child class derives from parent | Child inherits eye color from parents | Code duplication across similar classes |
| **Polymorphism** | One interface, many implementations | Same brake pedal works on car, truck, motorcycle | Massive if-else chains checking types |
| **Abstraction** | Hide complexity, show essential details | Car key: turn to start, don't need to know engine | Callers need to understand internal implementation |

### 3. Real Code / Real Example

```java
// =====================================================
// BAD: Procedural approach (no OOP)
// =====================================================
// Problem: Adding a new payment type means modifying PaymentProcessor
public class PaymentProcessor {
    public void process(String type, double amount) {
        // ❌ Every new payment type requires changing this method
        // ❌ Open/Closed violated - not closed for modification
        if (type.equals("CREDIT_CARD")) {
            // Connect to card network, charge, handle response
            System.out.println("Processing credit card: " + amount);
        } else if (type.equals("PAYPAL")) {
            // Connect to PayPal API, charge, handle response
            System.out.println("Processing PayPal: " + amount);
        }
        // Add UPI? → Modify this method! → Risk of breaking existing payments
    }
}

// =====================================================
// GOOD: OOP approach
// =====================================================

// Step 1: ABSTRACTION - Define the contract (what, not how)
interface PaymentMethod {
    // Each payment type implements its OWN way of charging
    // Caller doesn't need to know the details
    void charge(double amount);
}

// Step 2: ENCAPSULATION - Each payment method hides its internals
class CreditCardPayment implements PaymentMethod {
    // Private state - hidden from outside
    private String cardNumber;
    private String cvv;
    
    public CreditCardPayment(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }
    
    @Override
    public void charge(double amount) {
        // Internals: connect to Visa/Mastercard API
        // Encapsulated - caller just calls charge()
        System.out.println("Charging $" + amount + " from card " + cardNumber);
    }
}

class PayPalPayment implements PaymentMethod {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public void charge(double amount) {
        // Internals: OAuth2 token, PayPal REST API
        System.out.println("Charging $" + amount + " from PayPal " + email);
    }
}

// Step 3: POLYMORPHISM - Same method, different behavior
class OrderService {
    // Accepts ANY PaymentMethod - doesn't care which!
    // New payment types don't require changing this class
    public void checkout(PaymentMethod payment, double amount) {
        // Polymorphic call - exact behavior determined at RUNTIME
        payment.charge(amount);  // Could be CreditCard, PayPal, UPI, etc.
    }
}

// Step 4: INHERITANCE - Share common behavior
abstract class OnlinePayment implements PaymentMethod {
    protected String transactionId;
    
    // Common to ALL online payments
    protected void logTransaction(double amount) {
        transactionId = "TXN-" + System.currentTimeMillis();
        System.out.println("Logging: " + transactionId + " = $" + amount);
    }
    
    // Each subclass provides its own implementation
    public abstract void charge(double amount);
}

class UPIPayment extends OnlinePayment {
    private String upiId;
    
    public UPIPayment(String upiId) {
        this.upiId = upiId;
    }
    
    @Override
    public void charge(double amount) {
        logTransaction(amount);  // Inherited behavior
        System.out.println("UPI charge from " + upiId);
    }
}

// =====================================================
// Usage demonstrates all 4 pillars
// =====================================================
public class Main {
    public static void main(String[] args) {
        OrderService service = new OrderService();
        
        // Polymorphism in action - same checkout() works for ALL payment types
        service.checkout(new CreditCardPayment("4111-1111-1111-1111", "123"), 100.0);
        service.checkout(new PayPalPayment("user@example.com"), 50.0);
        service.checkout(new UPIPayment("user@upi"), 25.0);
        
        // Output:
        // Charging $100.0 from card 4111-1111-1111-1111
        // Charging $50.0 from PayPal user@example.com
        // Logging: TXN-1234567890 = $25.0
        // UPI charge from user@upi
    }
}
```

### 4. What Happens Internally

**Polymorphism — Virtual Method Dispatch (VTable):**

```
Reference: PaymentMethod p = new CreditCardPayment(...)

JVM sees: p.charge(100.0)

Step 1: JVM looks at p's runtime type (CreditCardPayment)
         NOT at p's compile-time type (PaymentMethod)

Step 2: JVM finds CreditCardPayment's vtable (virtual method table)
         vtable = {
             [0]: Object.toString()
             [1]: Object.hashCode()
             [2]: PaymentMethod.charge() → CreditCardPayment.charge()
         }

Step 3: JVM calls the actual implementation from the vtable
         → CreditCardPayment.charge(100.0) is executed

Step 4: If no @Override exists, parent's method is called
         → UPIPayment doesn't override charge()? → OnlinePayment.charge() would be called
         → But charge() is abstract, so UPIPayment MUST implement it
```

### 5. Tricky Interview Cases

**Case 1: Encapsulation violated by reflection**
```java
class BankAccount {
    private double balance = 1000;
}

// Reflection can BREAK encapsulation:
Field field = BankAccount.class.getDeclaredField("balance");
field.setAccessible(true);  // THIS IS THE DANGER ZONE
BankAccount account = new BankAccount();
field.set(account, 9999999);  // Balance changed! Encapsulation bypassed!

// But: SecurityManager can prevent setAccessible() in production
// Java 17+: --illegal-access=deny (default since Java 17)
```

**Case 2: Polymorphism with static methods**
```java
class Parent {
    public static void print() {
        System.out.println("Parent");
    }
}
class Child extends Parent {
    public static void print() {  // HIDES, not overrides!
        System.out.println("Child");
    }
}

Parent p = new Child();
p.print();  // "Parent" — NOT polymorphism!
// Static methods are resolved at COMPILE time, not runtime
// This is METHOD HIDING, not OVERRIDING
```

**Case 3: Polymorphism with private methods**
```java
class Base {
    private void show() { System.out.println("Base"); }  // Private = NOT inherited
    public void call() {
        show();  // Always calls Base.show(), even if subclass has show()
    }
}
class Derived extends Base {
    private void show() { System.out.println("Derived"); }  // New method, NOT override
}

Derived d = new Derived();
d.call();  // "Base" — private methods use early binding, not polymorphism!
```

**Case 4: Diamond problem with interfaces (Java 8+ default methods)**
```java
interface A {
    default void hello() { System.out.println("A"); }
}
interface B {
    default void hello() { System.out.println("B"); }
}
// ❌ This fails compilation:
// class MyClass implements A, B { }
// Error: MyClass inherits unrelated defaults for hello() from types A and B

// ✅ Fix: Must override the conflicting method
class MyClass implements A, B {
    @Override
    public void hello() {
        A.super.hello();  // Choose A's version
        // Or: B.super.hello();  // Choose B's version
        // Or: provide entirely new implementation
    }
}
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Using inheritance for code reuse only (is-a vs has-a) | Fragile base class problem — changing parent breaks all children | Prefer composition: `class Car { Engine engine; }` not `class Car extends Engine` |
| Making all fields private with only getters | Anemic domain model — data without behavior | Encapsulate BEHAVIOR: `account.transfer(amount, target)` not `if(acc1.getBalance() > x) acc2.setBalance(...)` |
| Not using `@Override` | Typo in method name → accidental overloading, not overriding | Always add `@Override` — compiler catches mismatches |
| Using `instanceof` to check types instead of polymorphism | Tight coupling, violates Open/Closed | Use polymorphic dispatch: `visitor.visit(element)` not `if(e instanceof A) ... else if(e instanceof B)` |

### 7. Production Usage

**Strategy Pattern (Polymorphism in action):**
```java
// Real e-commerce: dynamically select tax calculator based on location
@Service
public class TaxCalculator {
    // Spring injects ALL implementations of TaxStrategy
    // New country = new TaxStrategy implementation = no code change
    private final Map<String, TaxStrategy> strategies;
    
    public double calculateTax(Order order) {
        TaxStrategy strategy = strategies.get(order.getCountry());
        if (strategy == null) throw new UnsupportedOperationException("Country not supported");
        return strategy.calculate(order);  // Polymorphic call
    }
}
```

**Testing with Polymorphism:**
```java
// Production: real payment gateway
PaymentMethod payment = new CreditCardPayment("4111-1111-1111-1111", "123");

// Test: mock payment gateway (same interface!)
PaymentMethod mockPayment = mock(PaymentMethod.class);
when(mockPayment.charge(anyDouble())).thenReturn(true);

// OrderService doesn't know the difference!
orderService.checkout(mockPayment, 100.0);  // Same code, different behavior
```

### 8. Advanced Details

**Performance: Polymorphism has a cost**
- **Virtual method dispatch**: ~1-2 CPU cycles extra per call vs non-virtual
- **JIT inlining**: JIT can inline non-virtual methods (private, static, final), but virtual methods require deoptimization
- **JVM optimization**: JIT detects "monomorphic" call sites (only one implementation used) and devirtualizes — making it as fast as non-virtual

**Composition over Inheritance — When to use each:**
```
Use Inheritance (is-a):
  - Dog extends Animal (Dog IS-A Animal)
  - Square extends Shape (Square IS-A Shape)
  - Cat extends Mammal (Cat IS-A Mammal)

Use Composition (has-a):
  - Car has-a Engine (Car is NOT an Engine)
  - Order has-a List<Item> (Order is NOT a List)
  - UserService has-a UserRepository (UserService is NOT a Repository)
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: Explain encapsulation with a real code example.

**A**: Encapsulation hides internal state and provides controlled access. Example: `BankAccount` has `private double balance`. You can't set `account.balance = 1000000` directly. Instead, you call `account.deposit(500)` which validates the amount. This prevents invalid states — like negative balance from direct field assignment. Getter/setter is the mechanism, but the GOAL is maintaining valid state.

#### Intermediate

**Q**: What's the difference between method overloading and overriding in terms of polymorphism?

**A**: Overloading is compile-time polymorphism (static binding) — same method name, different parameters, resolved at compile time. Overriding is runtime polymorphism (dynamic binding) — same signature, different implementation, resolved at runtime via vtable. Overloading is determined by the **reference type**, overriding by the **actual object type**. Example: `Parent p = new Child(); p.overloadedMethod(5)` uses Parent's version; `p.overriddenMethod()` uses Child's version.

#### Senior

**Q**: You have a class hierarchy: `Animal → Bird → Penguin`. Penguin can't fly. How do you design this without violating LSP?

**A**: The classic mistake is `class Bird { void fly() {} }` then overriding `fly()` in `Penguin` to throw `UnsupportedOperationException`. This violates LSP because substituting Penguin for Bird breaks behavior. Solution: **segregate the interface** — `interface Flyable { void fly(); }` implemented only by flying birds. `Bird` has common behavior (eat, sleep). `Penguin extends Bird`, `Sparrow extends Bird implements Flyable`. Now substituting Penguin for Bird never breaks — callers who need flying use `Flyable` interface specifically.

#### Tricky

**Q**: Can you have polymorphism without inheritance in Java?

**A**: Yes, through **interfaces**. A class can implement an interface without extending any class (except Object). Example: `class Logger implements Loggable` doesn't inherit from `BaseLogger`, but can still be used polymorphically: `Loggable log = new Logger()`. This is "interface polymorphism" without class inheritance. Additionally, **duck typing isn't supported in Java** (unlike Go or TypeScript) — a class must explicitly declare `implements Loggable` to be used as `Loggable`. This is stricter but safer.

### 10. Final 30-Second Answer

OOP's four pillars: encapsulation hides state, inheritance shares behavior, polymorphism dispatches via runtime type, abstraction hides complexity. Use composition over inheritance. Prefer interfaces for polymorphism. Apply SOLID to avoid fragile code. Always design for extension, not modification.

---

## Q2. Explain the SOLID principles with real-world examples.

### 1. Why This Concept Matters
SOLID is the most frequently tested design concept in senior engineer interviews. Without SOLID, code becomes rigid (hard to change), fragile (changes break unrelated things), and immobile (hard to reuse). Interviewers ask SOLID to test if you can **design maintainable systems**, not just write working code.

### 2. Basic Meaning

| Letter | Principle | 1-Line Rule | When You're Violating It |
|--------|-----------|-------------|------------------------|
| **S** | Single Responsibility | One class = one job | Your class has >1 reason to change |
| **O** | Open/Closed | Open for extension, closed for modification | Adding a feature means changing existing code |
| **L** | Liskov Substitution | Subtypes must be substitutable for base types | Subclass methods throw unexpected exceptions |
| **I** | Interface Segregation | Don't force clients to depend on methods they don't use | Your interface has methods that some impls don't need |
| **D** | Dependency Inversion | Depend on abstractions, not concretions | Your class creates its own dependencies with `new` |

### 3. Real Code / Real Example

```java
// =====================================================
// ❌ VIOLATES SOLID — Every principle broken
// =====================================================

class ReportService {
    // S: VIOLATED — generates report AND sends email AND connects to DB
    // O: VIOLATED — adding new format means changing this class
    // D: VIOLATED — directly creates MySQLConnection
    
    private MySQLConnection connection;
    
    public ReportService() {
        this.connection = new MySQLConnection();  // HARD-CODED dependency
    }
    
    public void generateAndSendReport(String type) {
        // Generate report
        String data = connection.query("SELECT * FROM sales");
        String report = "";
        
        if (type.equals("PDF")) {         // O: Adding CSV means new if-else
            report = generatePDF(data);
        } else if (type.equals("EXCEL")) {
            report = generateExcel(data);
        }
        
        // Send email — S: TWO reasons to change this class:
        // 1. Report format changes  2. Email logic changes
        // L: If subclass overrides sendEmail() and doesn't actually send?
        EmailSender.send(report);
    }
    
    private String generatePDF(String data) { return "PDF: " + data; }
    private String generateExcel(String data) { return "Excel: " + data; }
}

// =====================================================
// ✅ FOLLOWS SOLID
// =====================================================

// S — Single Responsibility: Each class has ONE job
interface ReportGenerator {           // Job 1: Generate reports
    String generate(String data);
}

class PdfReportGenerator implements ReportGenerator {
    @Override
    public String generate(String data) {
        return "PDF formatted: " + data;
    }
}

class ExcelReportGenerator implements ReportGenerator {
    @Override
    public String generate(String data) {
        return "Excel formatted: " + data;
    }
}

// O — Open/Closed: New formats = NEW class, not modifying existing
// To add CSV format: just create CsvReportGenerator implements ReportGenerator
// ReportService doesn't change!

// I — Interface Segregation: Small, focused interfaces
interface ReportDataFetcher {          // Job: fetch data only
    String fetchData();
}

interface ReportDispatcher {           // Job: dispatch report only
    void dispatch(String report);
}

// D — Dependency Inversion: Depends on ABSTRACTIONS, not concretions
class ReportService {
    // Constructor injection — doesn't create its own dependencies
    private final ReportGenerator generator;
    private final ReportDataFetcher fetcher;
    private final ReportDispatcher dispatcher;
    
    public ReportService(
            ReportGenerator generator,      // Abstract! Could be PDF, Excel, CSV...
            ReportDataFetcher fetcher,       // Abstract! Could be DB, API, file...
            ReportDispatcher dispatcher) {   // Abstract! Could be Email, SQS, Kafka...
        this.generator = generator;
        this.fetcher = fetcher;
        this.dispatcher = dispatcher;
    }
    
    public void processReport() {
        String data = fetcher.fetchData();
        String report = generator.generate(data);
        dispatcher.dispatch(report);
    }
}

// L — Liskov Substitution: Subtypes are INTERCHANGEABLE
// Any ReportGenerator can replace another without breaking the system
public class Main {
    public static void main(String[] args) {
        // PDF scenario
        ReportService pdfService = new ReportService(
            new PdfReportGenerator(),
            new DatabaseFetcher(),
            new EmailDispatcher()
        );
        pdfService.processReport();  // Works correctly
        
        // Excel scenario — SAME ReportService, no modification!
        ReportService excelService = new ReportService(
            new ExcelReportGenerator(),
            new DatabaseFetcher(),
            new SqsDispatcher()
        );
        excelService.processReport();  // Works correctly — Liskov satisfied!
    }
}
```

### 4. What Happens Internally

**Dependency Injection (DIP in action):**

```
❌ Without DIP (hard-coded):
┌──────────────────┐
│   ReportService  │──→ new MySQLConnection()
│                  │──→ new EmailSender()
│                  │──→ new PdfGenerator()
└──────────────────┘
  Problem: Can't test without MySQL. Can't switch to PostgreSQL.
           Can't test email without actually sending.

✅ With DIP (abstractions injected):
┌──────────────────┐     ┌──────────────────────┐
│   ReportService  │────→│ ReportGenerator (I/F) │
│                  │     ├──────────────────────┤
│                  │     │ PdfReportGenerator    │
│                  │     │ ExcelReportGenerator  │  ← Swappable!
│                  │     │ CsvReportGenerator    │
│                  │     └──────────────────────┘
│                  │     ┌──────────────────────┐
│                  │────→│ ReportDataFetcher(I/F)│
│                  │     ├──────────────────────┤
│                  │     │ DatabaseFetcher       │
│                  │     │ ApiFetcher            │  ← Swappable!
│                  │     │ FileFetcher           │
│                  │     └──────────────────────┘
└──────────────────┘
  Benefit: Test with mocks! Switch implementations without code changes!
```

### 5. Tricky Interview Cases

**Case 1: LSP violation — Square extends Rectangle**
```java
// ❌ CLASSIC LSP VIOLATION
class Rectangle {
    private int width, height;
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int getArea() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int w) {
        super.setWidth(w);
        super.setHeight(w);  // Force square behavior!
    }
    @Override
    public void setHeight(int h) {
        super.setWidth(h);   // Force square behavior!
        super.setHeight(h);
    }
}

// Client code that works for Rectangle but BREAKS for Square:
void resize(Rectangle r) {
    r.setWidth(5);
    r.setHeight(10);
    assert r.getArea() == 50;  // Rectangle: OK (5*10=50)
                                // Square: FAILS (10*10=100) ← LSP violation!
}
```

**Case 2: ISP violation — Fat interface**
```java
// ❌ FAT INTERFACE — violates ISP
interface Worker {
    void work();
    void eat();
    void sleep();
}

class Human implements Worker {
    public void work() { /* works */ }
    public void eat() { /* eats */ }
    public void sleep() { /* sleeps */ }
}

class Robot implements Worker {
    public void work() { /* works */ }
    public void eat() { throw new UnsupportedOperationException(); }  // !!
    public void sleep() { /* doesn't sleep — forced to implement */ }
}

// ✅ ISP: Separate interfaces
interface Workable { void work(); }
interface Eatable { void eat(); }
interface Sleepable { void sleep(); }

class SmartRobot implements Workable {  // Only what it needs!
    public void work() { /* works */ }
}
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Every class gets exactly one public method (over-SRP) | Class explosion — too many tiny classes | SRP is about REASONS TO CHANGE, not number of methods |
| Making everything `final` for "closed" (OCP) | Can't extend at all | Use interfaces/abstract classes for extension points |
| Creating interfaces for everything (over-DIP) | Indirection without benefit | Apply DIP to UNSTABLE dependencies (DB, external APIs) not to stable ones (String, List) |
| Checking type with `instanceof` instead of polymorphism | OCP violation — adding type means adding if-else | Use visitor pattern or polymorphic dispatch |

### 7. Production Usage

**Spring Boot — DIP in action:**
```java
@Configuration
public class AppConfig {
    @Bean
    @Profile("dev")
    public PaymentGateway testGateway() {
        return new TestPaymentGateway();  // Doesn't charge real cards
    }
    
    @Bean
    @Profile("prod")
    public PaymentGateway realGateway() {
        return new StripePaymentGateway();  // Real charges
    }
    
    // PaymentService doesn't change between dev and prod!
    @Bean
    public PaymentService paymentService(PaymentGateway gateway) {
        return new PaymentService(gateway);  // DIP satisfied via injection
    }
}
```

### 8. Advanced Details

**SOLID and Functional Programming:**
- SRP: Functions should do one thing (already natural in FP)
- OCP: Higher-order functions accept behavior as parameters
- LSP: Immutable data has no subtypes, so LSP is trivially satisfied
- ISP: Functions have single inputs/outputs
- DIP: Functions depend on abstractions (function signatures)

**SOLID Tradeoffs:**
```
SRP + ISP: More classes/interfaces → more boilerplate
OCP: More extension points → more complexity to understand
DIP: More indirection → harder to trace code flow

Rule of thumb: Apply SOLID where CHANGE IS EXPECTED
Don't apply SOLID to stable, unchanging code
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What is the Single Responsibility Principle?

**A**: A class should have only one reason to change. If a class handles both report generation AND email sending, a change to email logic might break report generation. Split them: `ReportGenerator` and `EmailService` each have one job. Test: describe what would force a change to your class — if you list two unrelated things, split it.

#### Intermediate

**Q**: Explain Dependency Inversion with a code example.

**A**: DIP says: depend on abstractions, not concretions. Violation: `class Service { Database db = new MySQLDatabase(); }` — hard-coded to MySQL. Fix: `class Service { Database db; // injected }` where Database is an interface. Now you can inject MySQLDatabase, PostgreSQLDatabase, or MockDatabase for testing. The high-level Service doesn't depend on low-level Database implementation — both depend on the Database abstraction.

#### Senior

**Q**: How do you decide when to apply SOLID vs when over-engineering?

**A**: Apply SOLID to code that CHANGES. Analysis: (1) Track change frequency — if `PaymentProcessor` changes every sprint, apply OCP; (2) Identify unstable dependencies — external APIs, databases → apply DIP; (3) Consider team size — smaller teams can tolerate less abstraction; (4) Evaluate testing needs — if hard to test, apply DIP for mockability. Over-engineering is applying patterns to code that NEVER changes. Rule: "You Aren't Gonna Need It" (YAGNI) — add abstraction when you have at least 2 real use cases, not when you imagine future ones.

#### Tricky

**Q**: Can you violate LSP even if the subclass doesn't throw exceptions?

**A**: Yes. LSP violations can be **behavioral** not just signature-based. Example: `java.util.Properties extends Hashtable<Object,Object>`. Properties only accepts String keys/values, but inherited `put()` from Hashtable accepts any Object. If code uses Properties as a Hashtable: `Properties props = new Properties(); props.put(123, 456);` — it compiles and runs, but violates the behavioral contract of Properties (key should be String). This is a real Java API violation of LSP. Another: `Collections.emptyList()` returns a list where `add()` throws — it's a `List` that can't be added to.

### 10. Final 30-Second Answer

SOLID: Single Responsibility (one reason to change), Open/Closed (extend without modifying), Liskov Substitution (subtypes must be swappable), Interface Segregation (small focused interfaces), Dependency Inversion (depend on abstractions). Apply where change is expected. Avoid over-engineering stable code.


Perfect. Let's start.

---

# Chapter 1 — Object Lifecycle (Interview Recap Edition)

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> **Expected Time in Interview:** 15–30 mins (with follow-up questions)

This chapter is the foundation of:

* Heap vs Stack
* Garbage Collection
* Constructors
* References
* Association
* Composition
* Spring Bean Lifecycle
* Hibernate Entity Lifecycle

---

# Big Picture

Every Java object goes through this lifecycle.

```text
                    Object Lifecycle

        Source Code
             │
             ▼
      new Employee()
             │
             │ ① JVM receives NEW instruction
             ▼
      Allocate Heap Memory
             │
             │ ② Default values assigned
             ▼
     Constructor Executes
             │
             │ ③ Object initialized
             ▼
     Reference Returned
             │
             │ ④ Application uses object
             ▼
      Business Operations
             │
             │ ⑤ No strong references remain
             ▼
     Eligible for Garbage Collection
             │
             │ ⑥ GC reclaims memory
             ▼
      Memory Released
```

This is the complete story.

Everything else is details.

---

# Step 1 — Writing the Code

Suppose we write

```java
public class Employee {

    private int id;
    private String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

public class Main {

    public static void main(String[] args) {

        Employee employee = new Employee(101, "John");

    }
}
```

Looks like one line.

But internally many things happen.

---

# Step 2 — What Happens When JVM Sees `new`?

Interview Question

> **What happens when JVM executes `new Employee()`?**

Expected answer should follow this sequence.

```text
new Employee()

      │
      │ JVM executes NEW bytecode instruction
      ▼

Locate Employee Class

      │
      │ Class already loaded?
      ├───────────────► YES → Continue
      │
      ▼
NO

Load Class

      │
      ▼

Allocate Heap Memory

      │
      ▼

Assign Default Values

      │
      ▼

Invoke Constructor

      │
      ▼

Return Reference

      │
      ▼

Assign Reference to Variable
```

Notice

Constructor is **not** the first thing.

Memory allocation happens first.

---

# Step 3 — Memory Allocation

Before constructor executes

JVM allocates memory.

```text
Heap

+-----------------------------+
| Employee                    |
|-----------------------------|
| id = 0                      |
| name = null                 |
+-----------------------------+
```

Important

Fields contain

default values.

NOT constructor values.

Interview Question

> Why is `id` zero?

Because constructor has not executed yet.

---

# Step 4 — Constructor Executes

Now

```java
public Employee(int id, String name) {

    this.id = id;
    this.name = name;

}
```

Memory becomes

```text
Heap

+-----------------------------+
| Employee                    |
|-----------------------------|
| id = 101                    |
| name = "John"               |
+-----------------------------+
```

Constructor

doesn't create the object.

It **initializes** an already allocated object.

⭐ Interview Favourite.

---

# Step 5 — Reference Assignment

Now

```java
Employee employee = new Employee(101, "John");
```

Memory

```text
               JVM Memory

Stack                         Heap
------                        ---------------------

employee ───────────────────► +--------------------+
                              | Employee           |
                              |--------------------|
                              | id = 101          |
                              | name = "John"     |
                              +--------------------+
```

Notice

Object

↓

Heap

Reference

↓

Stack

Interview Question

> Where is object stored?

Heap.

> Where is reference stored?

Inside current thread's stack frame (for local variables).

---

# Step 6 — Object Usage

Application uses object.

```java
employee.work();

employee.getSalary();

employee.setName("Bob");
```

Nothing special.

Object lives because

reference exists.

---

# Step 7 — Object Becomes Eligible for GC

Suppose

```java
employee = null;
```

Now

```text
Stack                         Heap
------                        ----------------------

employee = null               +-------------------+
                              | Employee          |
                              | id = 101          |
                              +-------------------+
```

Nobody references it.

Now

```text
Employee Object

↓

No Strong Reference

↓

Eligible for GC
```

Important

Eligible

≠

Immediately deleted.

GC decides when to reclaim memory.

---

# Step 8 — Garbage Collector

Eventually

GC runs.

```text
Heap

+----------------------+
| Employee             |
+----------------------+

        │

        │ No references
        ▼

Garbage Collector

        │

        ▼

Memory Reclaimed
```

---

# Complete Lifecycle

```text
Source Code

       │

       ▼

new Employee()

       │

       ▼

Heap Allocation

       │

       ▼

Default Values

       │

       ▼

Constructor

       │

       ▼

Reference Returned

       │

       ▼

Application Uses Object

       │

       ▼

Reference Lost

       │

       ▼

Eligible for GC

       │

       ▼

Garbage Collector

       │

       ▼

Memory Freed
```

---

# Object vs Reference ⭐⭐⭐⭐⭐

One of the most asked questions.

Example

```java
Employee e1 = new Employee();

Employee e2 = e1;
```

Memory

```text
Stack

e1 ───────┐
           │
e2 ────────┘
           │
           ▼

Heap

+----------------+
| Employee       |
+----------------+
```

Interview Question

How many objects?

Answer

```text
Object

1

References

2
```

Common mistake

People answer

2 Objects.

Wrong.

---

# What If Constructor Throws Exception?

```java
public Employee() {

    throw new RuntimeException();

}
```

Sequence

```text
Heap Allocated

↓

Constructor Starts

↓

Exception

↓

Construction Fails

↓

No Usable Reference

↓

Object Becomes Unreachable

↓

GC Reclaims Memory
```

The partially constructed object is not usable by application code because object creation never completed successfully.

---

# Common Interview Questions

### Q1. Does constructor create object?

❌ No.

Constructor only initializes.

Memory allocation happens before constructor invocation.

---

### Q2. What creates object?

The JVM executes the `new` bytecode instruction, allocates memory, and then invokes the constructor.

---

### Q3. Where is object stored?

Heap.

---

### Q4. Where is reference stored?

For local variables:

Stack Frame.

For instance fields:

Inside another Heap object.

For static fields:

Method Area / Metaspace (class metadata area holding static variables).

---

### Q5. When is object eligible for GC?

When it is no longer reachable through any **strong reference** from the application.

---

### Q6. Is eligible for GC equal to immediately destroyed?

No.

GC timing is determined by the JVM.

---

### Q7. Can constructors return values?

No.

Constructors have no return type.

---

### Q8. Can constructor be inherited?

No.

Subclasses invoke superclass constructors using `super()`, but constructors themselves are not inherited.

---

# Framework Connection

Spring

```java
Employee employee = new Employee();
```

Manual lifecycle.

Spring

```java
@Component
class EmployeeService {}
```

Spring manages:

```text
Create Object

↓

Inject Dependencies

↓

@PostConstruct

↓

Ready

↓

@PreDestroy

↓

Destroy
```

Same lifecycle.

Different creator.

---

# Interview Cheat Sheet

## Lifecycle

```text
new
 │
 ▼
Heap Allocation
 │
 ▼
Default Values
 │
 ▼
Constructor
 │
 ▼
Reference Returned
 │
 ▼
Application Uses Object
 │
 ▼
Reference Lost
 │
 ▼
Eligible for GC
 │
 ▼
Memory Reclaimed
```

---

## Must Remember

* `new` triggers object creation.
* Memory allocation happens **before** the constructor.
* Constructors initialize; they do **not** allocate memory.
* Objects live in the heap.
* Local references live in stack frames.
* Multiple references can point to one object.
* "Eligible for GC" does not mean "immediately collected."

---

# Connection to Next Topic

Now that we understand **how an object is created and lives in memory**, the next question naturally becomes:

> **How do multiple objects relate to each other?**

That leads us to:

```text
Object Lifecycle
        │
        ▼
Association
        │
        ▼
Aggregation
        │
        ▼
Composition
```

These concepts are much easier to understand now because we already know what an object is, where it lives, and how references connect objects together.


# Chapter 2 — Association (Interview Recap Edition)

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> **Expected Time:** 10–20 mins (usually followed by Aggregation vs Composition)

This is one of the most misunderstood OOP concepts.

Many candidates memorize:

> Association means relationship.

An interviewer immediately asks:

> **"What kind of relationship?"**

or

> **"Is Composition also an Association?"**

If you don't know the hierarchy, you'll get confused.

---

# Big Picture

Before learning Aggregation and Composition, understand this:

```text
                    Object Relationships

                         Association
                              │
               ┌──────────────┴──────────────┐
               │                             │
         Aggregation                   Composition
        (Weak HAS-A)                  (Strong HAS-A)
```

⭐ **Important Interview Point**

> **Aggregation and Composition are specialised forms of Association.**

Every Composition is an Association.

Every Aggregation is an Association.

But every Association is **not** Composition.

---

# Why Do We Need Association?

Imagine these two classes.

```java
class Employee {
}

class Department {
}
```

Currently

```text
Employee

Department
```

They know nothing about each other.

Now suppose every employee works in a department.

We need a relationship.

That relationship is called **Association**.

---

# Definition

Association is a relationship where **one object knows about another object**.

Notice the wording.

It doesn't mean ownership.

It doesn't mean lifetime dependency.

It simply means

> One object can communicate with another object.

---

# Real World Example

Think about

```text
Student

↓

College
```

A student studies in a college.

The college exists.

The student exists.

They are related.

Neither necessarily owns the other.

That's Association.

---

# Java Example

```java
class Department {

    private String name;

    public Department(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Employee {

    private String name;
    private Department department;

    public Employee(String name, Department department) {
        this.name = name;
        this.department = department;
    }

    public void printDetails() {
        System.out.println(name + " works in " + department.getName());
    }
}
```

Usage

```java
public class Main {

    public static void main(String[] args) {

        Department engineering = new Department("Engineering");

        Employee john = new Employee("John", engineering);

        john.printDetails();
    }
}
```

Output

```text
John works in Engineering
```

---

# Memory Diagram

```text
                    JVM Memory

Heap
──────────────────────────────────────────────

+-----------------------+
| Department            |
|-----------------------|
| name = Engineering    |
+-----------▲-----------+
            │
            │ Reference
            │
+-----------┴-----------+
| Employee              |
|-----------------------|
| name = John           |
| department ----------┘
+-----------------------+
```

Notice carefully:

The Employee object stores **only a reference** to the Department object.

It doesn't contain the Department object itself.

This understanding is extremely important for Aggregation and Composition.

---

# Sequence Diagram

```text
Application
      │
      │ Create Department
      ▼
Department Object
      │
      │ Reference passed
      ▼
Employee Constructor
      │
      │ Store Department reference
      ▼
Employee Object
      │
      │ Invoke department.getName()
      ▼
Department Object
```

The employee communicates with the department through the reference.

---

# Types of Association

There are three common forms.

---

## 1. One-to-One

Example

```text
Person

↓

Passport
```

One person has one passport.

One passport belongs to one person.

---

Java

```java
class Person {

    private Passport passport;

}
```

---

## 2. One-to-Many

Example

```text
Department

↓

Employees
```

One department.

Many employees.

---

```java
class Department {

    List<Employee> employees;

}
```

---

## 3. Many-to-Many

Example

```text
Student

↓

Courses
```

One student studies many courses.

One course has many students.

---

```java
class Student {

    List<Course> courses;

}
```

---

# Direction of Association

Another favourite interview question.

---

## Unidirectional

```text
Employee ─────────► Department
```

Employee knows Department.

Department doesn't know Employee.

---

Code

```java
class Employee {

    private Department department;

}
```

Department has no Employee field.

---

## Bidirectional

```text
Employee ◄────────► Department
```

Both know each other.

---

Example

```java
class Employee {

    private Department department;

}

class Department {

    private List<Employee> employees;

}
```

This is common in Hibernate/JPA.

---

# Association vs Dependency

Interviewers love asking this.

Consider

```java
public void print(Printer printer) {

    printer.print();

}
```

Here,

Printer is used only during method execution.

This is **Dependency**, not Association.

Association usually means the relationship is maintained through a field.

---

# Association vs Inheritance

Another common confusion.

Association

```text
Employee

↓

Department
```

Employee **has a** Department reference.

Inheritance

```text
Animal

↓

Dog
```

Dog **is an** Animal.

Interview shortcut:

| Relationship | Meaning        |
| ------------ | -------------- |
| Association  | HAS-A / Uses-A |
| Inheritance  | IS-A           |

---

# Where Is Association Used?

Almost everywhere.

Example in Spring

```java
@Service
class OrderService {

    private final PaymentService paymentService;

}
```

OrderService is associated with PaymentService.

Dependency Injection simply creates this association for you.

---

Hibernate

```java
@Entity
class Employee {

    @ManyToOne
    private Department department;

}
```

This is also an association.

---

Collections

```java
Map<Employee, Address>
```

Objects are associated through references.

---

# Common Interview Questions

### Q1. What is Association?

A relationship where one object knows about or communicates with another object.

---

### Q2. Is Association always HAS-A?

Generally yes, but don't reduce it to just "HAS-A". The key idea is **relationship between independent objects**.

---

### Q3. Is Composition an Association?

Yes.

Composition is a specialised form of Association.

---

### Q4. Is Aggregation an Association?

Yes.

Aggregation is also a specialised form of Association.

---

### Q5. Can Association be bidirectional?

Yes.

Example

```text
Employee ◄────► Department
```

---

### Q6. Does Association imply ownership?

No.

Ownership comes later with **Composition**.

Association only indicates a relationship.

---

# Quick Comparison

| Feature                     | Association |
| --------------------------- | ----------- |
| Relationship                | Yes         |
| Ownership                   | No          |
| Lifetime Dependency         | No          |
| Objects Exist Independently | Yes         |
| Uses References             | Yes         |

---

# Interview Cheat Sheet

## Mental Model

```text
Association

↓

Objects know each other

↓

Communicate using references

↓

No ownership

↓

Independent lifecycles
```

---

## Must Remember

* Association is the most general relationship between objects.
* Objects remain independent.
* References connect objects.
* Can be one-to-one, one-to-many, or many-to-many.
* Can be unidirectional or bidirectional.
* Aggregation and Composition both build on Association.

---

# Connection to Next Topic

Now comes the interview question that almost always follows:

> **If Association doesn't imply ownership, what if one object owns another?**

That leads directly to:

```text
Association
      │
      ▼
Aggregation
      │
      ▼
Composition
```

We'll next explore **Aggregation**, where objects are still independent, but one object groups or contains others without owning their lifetime. This is where the distinction between **weak HAS-A** and **strong HAS-A** begins to make sense.


# Chapter 3 — Aggregation (Interview Recap Edition)

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> **Expected Time:** 15–20 mins (Almost always followed by Composition)

This is one of the **most frequently confused OOP concepts**.

Interviewers often ask:

* Difference between Association and Aggregation?
* Difference between Aggregation and Composition?
* Why is Aggregation called **Weak HAS-A**?
* Give a real-world example.
* Which one does Spring use?
* Which one does Hibernate use?

---

# Big Picture

Let's build on the previous chapter.

```text
                    Object Relationships

                         Association
                              │
               ┌──────────────┴──────────────┐
               │                             │
         Aggregation                   Composition
        (Weak HAS-A)                  (Strong HAS-A)
```

Think of it like inheritance.

```text
Vehicle
   │
Car
```

Similarly

```text
Association
      │
Aggregation
      │
Composition
```

---

# First Understand the Problem

Suppose we have

```text
Department

Employees
```

Question:

**Who owns whom?**

Does deleting a Department mean Employees should disappear?

**No.**

Employees still exist.

They can move to another department.

So,

The Department is **using** Employees.

It is **not owning** Employees.

That is Aggregation.

---

# Definition

Aggregation is a specialised form of Association where:

* One object **contains** another object.
* The contained object has an **independent lifecycle**.
* Both objects can exist without each other.

Keyword:

> **Independent Lifecycle**

---

# Real World Example

## University → Professor

```text
University
      │
      ▼
Professors
```

If university closes

↓

Professors don't disappear.

They simply join another university.

Therefore

University aggregates Professors.

---

Another example

```text
Team

↓

Players
```

If a team dissolves

↓

Players still exist.

---

# Java Example

## Department and Employee

```java
class Employee {

    private String name;

    public Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Department {

    private List<Employee> employees;

    public Department(List<Employee> employees) {
        this.employees = employees;
    }

    public void showEmployees() {
        employees.forEach(e ->
                System.out.println(e.getName()));
    }
}
```

Usage

```java
Employee john = new Employee("John");
Employee alice = new Employee("Alice");

List<Employee> employees =
        Arrays.asList(john, alice);

Department engineering =
        new Department(employees);
```

Notice something very important.

Who created Employees?

```java
Employee john = new Employee(...);
```

NOT Department.

Department simply receives them.

This is the biggest indicator of Aggregation.

---

# Memory Diagram

```text
                 JVM Heap

      +-----------------------------+
      | Department                  |
      |-----------------------------|
      | employees ------------------|------+
      +-----------------------------+      |
                                           |
                                           ▼
                         +--------------------------+
                         | List<Employee>           |
                         +------------+-------------+
                                      |
                     +----------------+----------------+
                     |                                 |
                     ▼                                 ▼

          +-------------------+             +-------------------+
          | Employee          |             | Employee          |
          | John              |             | Alice             |
          +-------------------+             +-------------------+
```

Notice

Department does **not** own Employees.

It only stores references.

Employees can exist even if Department disappears.

---

# Lifecycle Diagram

This is the most important interview point.

```text
Create Employee
       │
       ▼

Employee Exists

       │
       │
       ├─────────────┐
       ▼             │

Create Department    │

       │             │

Department -----------┘
references Employee

       │

Delete Department

       │

Employee Still Exists ✅
```

That's Aggregation.

---

# Ownership Diagram

```text
Department

      │

      │ Uses

      ▼

Employee
```

Not

```text
Department

      │

      │ Owns

      ▼

Employee
```

---

# Why is it Called Weak HAS-A?

Interview favourite.

Because

```text
Department HAS Employees
```

But

Employees don't depend on Department for survival.

If Department disappears

↓

Employee survives.

Hence

Weak HAS-A.

---

# Sequence Diagram

```text
Application
      │
      │ Create Employee
      ▼
Employee
      │
      │ Pass reference
      ▼
Department
      │
      │ Store reference only
      ▼
Business Logic
      │
      │ Department deleted
      ▼
Employee Still Alive
```

Notice

Department never created Employee.

---

# How to Identify Aggregation?

This is a practical interview trick.

Suppose you see

```java
class Department {

    Department(List<Employee> employees) {
    }
}
```

Ask yourself:

Who created Employee?

If answer is

```text
Outside class
```

It's probably Aggregation.

---

# Real Production Examples

## Cricket Team

```text
Team

↓

Players
```

Players can join another team.

Aggregation.

---

## School

```text
School

↓

Teachers
```

School closes.

Teachers still exist.

Aggregation.

---

## Company

```text
Company

↓

Employees
```

Company shuts down.

Employees don't vanish.

Aggregation.

---

# Spring Example

```java
@Service
class OrderService {

    private final PaymentService paymentService;

}
```

Who creates `PaymentService`?

Spring Container.

Not OrderService.

OrderService simply receives it.

This is conceptually much closer to **Aggregation** than Composition because the dependency has an independent lifecycle managed by the IoC container.

---

# Hibernate Example

```java
@ManyToOne
private Department department;
```

Department exists independently.

Employee references Department.

This is another Aggregation-like relationship.

---

# Common Interview Questions

## Q1. What is Aggregation?

A weak HAS-A relationship where objects have independent lifecycles.

---

## Q2. Why Weak HAS-A?

Because contained objects survive even if container is destroyed.

---

## Q3. Does Aggregation imply ownership?

No.

Only reference sharing.

---

## Q4. Who creates the child object?

Usually

External code.

Not container.

---

## Q5. Can aggregated object belong to multiple parents?

Yes.

Example

```text
Professor

↓

University A

University B (Visiting Faculty)
```

Perfectly valid.

---

# Aggregation vs Association

| Feature                | Association     | Aggregation |
| ---------------------- | --------------- | ----------- |
| Relationship           | Yes             | Yes         |
| Reference              | Yes             | Yes         |
| Container holds object | Not necessarily | Yes         |
| Lifecycle dependency   | No              | No          |
| Ownership              | No              | No          |

Aggregation is simply a more specific form of Association where one object groups or contains references to others.

---

# Aggregation vs Composition (Preview)

| Aggregation                  | Composition                      |
| ---------------------------- | -------------------------------- |
| Weak HAS-A                   | Strong HAS-A                     |
| Independent lifecycle        | Dependent lifecycle              |
| Child created outside        | Child usually created inside     |
| Child reusable               | Child belongs to one parent      |
| Parent dies → Child survives | Parent dies → Child usually dies |

We'll cover Composition next.

---

# Interview Cheat Sheet

## Mental Model

```text
Aggregation

↓

Container references child

↓

Child created externally

↓

Independent lifecycle

↓

Weak HAS-A
```

---

## Decision Rule (Excellent Interview Trick)

Ask one question:

```text
Can the child object
exist without the parent?
```

If

```text
YES
```

↓

Aggregation

If

```text
NO
```

↓

Composition

This single question answers **90%** of interview scenarios.

---

# Complete Story So Far

```text
Object Lifecycle
        │
        ▼
Objects Created
        │
        ▼
Association
        │
        ▼
Objects Know Each Other
        │
        ▼
Aggregation
        │
        ▼
Objects Are Connected
But Live Independently
```

---

## Next Chapter — Composition ⭐⭐⭐⭐⭐

Composition is where interviewers usually spend the most time because it's directly related to:

* Effective Java's recommendation: **"Prefer Composition over Inheritance."**
* Spring bean design
* Domain-Driven Design (DDD)
* LLD interviews
* Real-world object modelling

We'll see how one object truly **owns** another and why their lifecycles become tightly coupled. This is one of the most valuable OOP concepts for designing maintainable systems.

# Chapter 4 — Composition (Interview Recap Edition)

> **Interview Weight:** ⭐⭐⭐⭐⭐ (One of the highest in OOP)
>
> **Expected Time:** 20–30 mins

This is probably the **most asked OOP design topic** for senior developers.

Interviewers love asking:

* Difference between Aggregation and Composition?
* Why is Composition called **Strong HAS-A**?
* Why does **Effective Java** say *"Favor Composition over Inheritance"*?
* Give a production example.
* Which one should we use while designing systems?

---

# Big Picture

Let's complete the relationship hierarchy.

```text
                         Object Relationships

                              Association
                                   │
                     ┌─────────────┴─────────────┐
                     │                           │
                Aggregation                 Composition
               (Weak HAS-A)               (Strong HAS-A)
```

Everything we learnt so far leads here.

---

# The Interview Story

Let's revisit our favourite example.

```
Department
      │
Employees
```

Delete Department.

Employees still exist.

↓

Aggregation.

Now consider another example.

```
House
   │
Rooms
```

Delete House.

Do those rooms exist independently?

No.

The rooms belong to that house.

That is Composition.

---

# Definition

Composition is a **strong HAS-A relationship** where:

* Parent owns the child.
* Child cannot exist independently.
* Parent controls the child's lifecycle.

Keyword

> **Ownership**

---

# Real World Examples

## House → Room

```text
House
   │
Rooms
```

House destroyed

↓

Rooms disappear.

---

## Human → Heart

```text
Human
   │
Heart
```

Heart belongs to Human.

Not shared.

Strong ownership.

---

## Car → Engine (Interview Discussion)

This is interesting.

Many books use

```
Car
   │
Engine
```

But in the real world

Engines can sometimes be replaced.

Therefore

This example is **not perfect**.

A better interview example is

```
House
   │
Rooms
```

or

```
Order
   │
OrderLine
```

The child logically belongs to one parent.

---

# Java Example

## House and Room

```java
class Room {

    private final String name;

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class House {

    private final List<Room> rooms = new ArrayList<>();

    public House() {
        rooms.add(new Room("Living Room"));
        rooms.add(new Room("Kitchen"));
        rooms.add(new Room("Bedroom"));
    }

    public void showRooms() {
        rooms.forEach(room ->
                System.out.println(room.getName()));
    }
}
```

Usage

```java
House house = new House();

house.showRooms();
```

---

# Why is this Composition?

Look carefully.

Who creates `Room`?

```java
rooms.add(new Room("Kitchen"));
```

House creates it.

House owns it.

House manages it.

This is the biggest indicator of Composition.

---

# Memory Diagram

```text
                     JVM Heap

           +-----------------------------+
           | House                       |
           |-----------------------------|
           | rooms ----------------------+---------+
           +-----------------------------+         |
                                                   |
                                                   ▼
                               +----------------------------+
                               | List<Room>                 |
                               +-------------+--------------+
                                             |
                      +----------------------+----------------------+
                      |                      |                      |
                      ▼                      ▼                      ▼

             +---------------+     +---------------+     +---------------+
             | Living Room   |     | Kitchen       |     | Bedroom       |
             +---------------+     +---------------+     +---------------+
```

Notice

House creates Rooms.

House owns Rooms.

Rooms belong to one House.

---

# Lifecycle Diagram ⭐

This is where Composition differs from Aggregation.

```text
Create House
      │
      ▼

House Constructor

      │
      ▼

Create Room Objects

      │
      ▼

House Uses Rooms

      │
      ▼

House Deleted

      │
      ▼

Rooms Become Unreachable

      │
      ▼

Eligible for Garbage Collection
```

Parent and child have the **same lifecycle**.

---

# Ownership Diagram

```text
House

     │

     │ Owns

     ▼

Room
```

Unlike Aggregation

```
Department

     │

     │ Uses

     ▼

Employee
```

Ownership changes everything.

---

# Sequence Diagram

```text
Application
      │
      │ new House()
      ▼
House Constructor
      │
      │ new Room()
      ▼
Room Object
      │
      │ Add to List
      ▼
House Ready
      │
      │ House becomes unreachable
      ▼
Rooms become unreachable
```

Notice

The child object is created **inside** the parent.

---

# How to Identify Composition?

This interview trick works almost every time.

Ask

```text
Who creates the child object?
```

If answer is

```text
Parent
```

↓

Composition.

If answer is

```text
Outside code
```

↓

Aggregation.

---

# Another Interview Trick

Ask

```text
Can child survive
without parent?
```

If

```
NO
```

↓

Composition.

---

# Real Production Examples

## Order → Order Items

```text
Order

↓

OrderLine
```

Delete Order.

Order lines disappear.

Composition.

---

## Resume → Sections

```
Resume

↓

Education

Experience

Skills
```

Delete Resume.

Sections have no meaning.

---

## HTML Page

```
Page

↓

Header

Body

Footer
```

Delete Page.

These sections disappear.

---

# Spring Example

Spring generally encourages **Composition** in design.

```java
@Service
class OrderService {

    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Be careful here.

At the **object lifecycle** level, Spring creates both beans independently (similar to aggregation).

But at the **class design** level, `OrderService` is designed using **composition** because it is built from collaborating objects rather than extending another class.

This is why you'll often hear:

> Spring prefers **composition over inheritance**.

Different context.

Very common interview trap.

---

# Why Does Effective Java Say

## "Favor Composition over Inheritance"?

One of the most famous interview questions.

Suppose

```java
class Animal {

    void eat() {}
}
```

```java
class Dog extends Animal {

}
```

Now later

Animal changes.

Dog may break.

Inheritance creates

```
Tight Coupling
```

Now compare

```java
class Dog {

    private Animal animal;
}
```

Dog simply uses Animal.

More flexible.

You can replace Animal implementation.

Composition gives

* Loose coupling
* Better testing
* Better maintainability
* Better extensibility

This is why modern frameworks prefer composition.

---

# Aggregation vs Composition

This table is extremely important.

| Feature          | Aggregation    | Composition                       |
| ---------------- | -------------- | --------------------------------- |
| Relationship     | Weak HAS-A     | Strong HAS-A                      |
| Ownership        | No             | Yes                               |
| Child lifecycle  | Independent    | Dependent                         |
| Child created by | External code  | Parent                            |
| Child reusable   | Yes            | Usually No                        |
| Parent destroyed | Child survives | Child usually becomes unreachable |

---

# Aggregation vs Composition Diagram

```text
Aggregation

Department
     │
     │ Uses
     ▼
Employee

Delete Department

↓

Employee survives
```

```text
Composition

House
   │
   │ Owns
   ▼
Room

Delete House

↓

Rooms disappear
```

One picture explains the difference.

---

# Common Interview Questions

### Q1. What is Composition?

A strong HAS-A relationship where the parent owns the child and controls its lifecycle.

---

### Q2. Why Strong HAS-A?

Because the child depends on the parent.

---

### Q3. Does child always die with parent?

Conceptually, yes.

Practically in Java:

When the parent becomes unreachable and there are no other references to the child, both become eligible for garbage collection.

---

### Q4. Why is Composition preferred?

* Loose coupling
* Reusability
* Better testing
* Flexible design
* Avoids inheritance problems

---

### Q5. Can Composition contain multiple children?

Absolutely.

```
House

↓

Rooms
```

```
Order

↓

OrderLines
```

---

# Complete Comparison

```text
Association
     │
     ▼
Objects know each other

Aggregation
     │
     ▼
Objects know each other
+
Independent lifecycle

Composition
     │
     ▼
Objects know each other
+
Ownership
+
Shared lifecycle
```

---

# Interview Cheat Sheet

## Mental Model

```text
Composition

↓

Parent owns child

↓

Parent creates child

↓

Child depends on parent

↓

Strong HAS-A
```

---

## 30-Second Decision Tree

```text
Can objects know each other?

        │
       YES
        │
        ▼

Association

        │
        ▼

Does child live without parent?

       YES
        │
        ▼
Aggregation

       NO
        │
        ▼
Composition
```

This one decision tree solves almost every interview question.

---

# OOP Relationship Story (Completed)

```text
                    Objects Created
                          │
                          ▼
                    Object Lifecycle
                          │
                          ▼
                    Association
                (Objects know each other)
                          │
             ┌────────────┴────────────┐
             ▼                         ▼
      Aggregation               Composition
   (Weak HAS-A)              (Strong HAS-A)
   Independent                Dependent
   Lifecycle                  Lifecycle
```

---

# Before Moving On

At this point, we've completed the **object relationship** part of OOP.

Next, we'll move to the **four pillars of OOP** in the order that makes the most sense for interviews:

```text
Object Lifecycle
        │
        ▼
Association
        │
        ▼
Aggregation
        │
        ▼
Composition
        │
        ▼
Encapsulation   ← Next
        │
        ▼
Abstraction
        │
        ▼
Inheritance
        │
        ▼
Polymorphism
```

This order is deliberate. Once you understand **how objects are created and how they relate**, the next logical question is:

> **How do we protect and expose object data correctly?**

That naturally leads to **Encapsulation**.

# Chapter 6 — Abstraction ⭐⭐⭐⭐⭐

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> **Expected Time:** 8–10 mins

One of the most asked OOP topics, especially:

* Abstract Class vs Interface
* When to use Abstract Class?
* Why do we need Abstraction?
* Can an abstract class have constructors?
* Can we create an abstract class object?

---

# 1. What is Abstraction?

**Abstraction means hiding implementation details and exposing only the necessary behaviour.**

> **Focus on "What" the object does, not "How" it does it.**

---

# 2. Why do we need it?

Imagine driving a car.

You only know

```text
Accelerate
Brake
Steering
```

You don't need to know

* Fuel injection
* Gear mechanism
* Engine timing
* ECU programming

Those implementation details are hidden.

That's Abstraction.

---

# 3. Java Example

Without Abstraction

```java
class PaymentService {

    public void pay() {

        // Connect DB
        // Validate User
        // Call Bank API
        // Retry Logic
        // Update Transaction
        // Send Email

    }
}
```

Client doesn't care about the implementation.

It only needs

```java
paymentService.pay();
```

---

Using Interface

```java
interface Payment {

    void pay(double amount);
}
```

Implementation

```java
class CreditCardPayment implements Payment {

    @Override
    public void pay(double amount) {
        System.out.println("Paid using Credit Card");
    }
}
```

Usage

```java
Payment payment = new CreditCardPayment();

payment.pay(1000);
```

Client knows **what** to call, not **how** it's implemented.

---

# 4. Interview Diagram

```text
                 Client

                   │
                   │ pay(1000)
                   ▼

              Payment Interface
         +----------------------+
         | + pay(amount)        |
         +----------┬-----------+
                    │
      Contract says "WHAT"
                    │
         ┌──────────┴──────────┐
         ▼                     ▼

 CreditCardPayment      UPIPayment

    How?                  How?
  Hidden Logic         Hidden Logic
```

### Arrow Explanation

* Client depends on **interface**, not implementation.
* Interface defines the contract.
* Multiple implementations can exist.
* Implementation can change without affecting the client.

---

# 5. How is Abstraction Achieved?

```text
Abstraction
      │
      ├── Interface ⭐⭐⭐⭐⭐
      │
      └── Abstract Class ⭐⭐⭐⭐⭐
```

Java provides two primary ways:

* Interface
* Abstract Class

---

# 6. Real-world Example

Spring

```java
@Autowired
PaymentService paymentService;
```

You don't know whether Spring injects

* Stripe
* Razorpay
* PayPal

Your code depends only on the contract.

---

JDBC

```java
Connection connection = DriverManager.getConnection(...);
```

You never create

```text
OracleConnection

MySQLConnection

PostgresConnection
```

Implementation is hidden.

---

# 7. Interview Questions ⭐⭐⭐⭐⭐

### Q1. What is Abstraction?

Hiding implementation details while exposing only the required behaviour through a contract.

---

### Q2. Difference between Encapsulation and Abstraction?

| Encapsulation                   | Abstraction                                |
| ------------------------------- | ------------------------------------------ |
| Protects object state           | Hides implementation                       |
| Focuses on data                 | Focuses on behaviour                       |
| Achieved using access modifiers | Achieved using interfaces/abstract classes |

---

### Q3. Can we create an object of an abstract class?

❌ No.

```java
abstract class Animal {
}

Animal a = new Animal(); // Compilation Error
```

Because the class is incomplete.

---

### Q4. Can an abstract class have constructors?

✅ Yes.

```java
abstract class Animal {

    Animal() {
        System.out.println("Animal Constructor");
    }
}
```

The constructor executes when a subclass object is created.

```java
class Dog extends Animal {
}

new Dog();
```

Execution

```text
Animal Constructor

↓

Dog Constructor
```

---

### Q5. Can abstract class have implemented methods?

Yes.

```java
abstract class Animal {

    void sleep() {
        System.out.println("Sleeping");
    }

    abstract void sound();
}
```

It can contain both:

* Concrete methods
* Abstract methods

---

### Q6. Can an interface have method implementations?

✅ Yes (Java 8+)

* `default`
* `static`

Java 9+

* `private` methods

---

### Q7. When to use Interface vs Abstract Class?

| Interface                     | Abstract Class                |
| ----------------------------- | ----------------------------- |
| Defines capability/contract   | Defines common base behaviour |
| Supports multiple inheritance | Single inheritance only       |
| No instance state (generally) | Can have instance fields      |
| Example: Payment, Runnable    | Example: Vehicle, Animal      |

---

# 8. Interview Trap 🚨

### Trap 1

**Interface = 100% abstraction?**

Not anymore.

Since Java 8

```java
default void print() {
}
```

Interfaces can contain implementations.

---

### Trap 2

**Abstract class cannot have constructors?**

Wrong.

Constructors are allowed and are commonly used for shared initialization.

---

### Trap 3

**Abstract class must contain abstract methods?**

Wrong.

```java
abstract class Animal {

    void sleep() {}

}
```

Perfectly valid.

It's abstract because you don't want direct instantiation.

---

# 9. Cheat Sheet

```text
                 Abstraction

          Hide "HOW"
              │
              ▼
      Expose "WHAT"
              │
              ▼
        Interface /
      Abstract Class
              │
              ▼
     Loose Coupling
```

---

# Frequently Asked Follow-up

> **Why is abstraction important in Spring?**

```text
Controller
      │
      ▼
Service Interface
      │
      ▼
Service Implementation
      │
      ▼
Repository Interface
      │
      ▼
Database
```

Each layer depends on **contracts**, making the application easier to test, maintain, and extend.

---

# Quick Revision

* **Abstraction** = Hide implementation, expose behaviour.
* Achieved using **interfaces** and **abstract classes**.
* Client programs to a **contract**, not a concrete implementation.
* Enables **loose coupling**, **testability**, and **extensibility**.
* Core principle behind Spring, JDBC, Strategy Pattern, and Dependency Injection.

---

## Connection to Next Topic

```text
Encapsulation
      │
      ▼
Protect Data
      │
      ▼
Abstraction
      │
      ▼
Hide Implementation
      │
      ▼
Inheritance
      │
      ▼
Reuse Behaviour
      │
      ▼
Polymorphism
```

**Next:** **Inheritance ⭐⭐⭐⭐⭐**, where we'll cover **IS-A relationship**, constructor chaining, method overriding, inheritance hierarchy, `super`, multiple inheritance in Java, and why inheritance should be used carefully in modern application design.

# Chapter 7 — Inheritance ⭐⭐⭐⭐⭐

> **Interview Weight:** ⭐⭐⭐⭐⭐
>
> **Expected Time:** 8–10 mins

Most asked questions:

* What is Inheritance?
* Why do we need it?
* IS-A vs HAS-A?
* Constructor execution order?
* Why multiple inheritance is not supported?
* Why is Composition preferred over Inheritance?

---

# 1. What is Inheritance?

**Inheritance allows a child class to acquire the properties and behaviour of its parent class.**

> **Relationship = IS-A**

Example

```text
Dog IS-A Animal

Car IS-A Vehicle

SavingsAccount IS-A BankAccount
```

---

# 2. Why do we need it?

Without inheritance

```java
class Dog {

    void eat() {}

    void sleep() {}
}

class Cat {

    void eat() {}

    void sleep() {}
}
```

Duplicate code.

With inheritance

```java
class Animal {

    void eat() {}

    void sleep() {}
}

class Dog extends Animal {
}

class Cat extends Animal {
}
```

Reuse common behaviour.

---

# 3. Java Example

```java
class Animal {

    void eat() {
        System.out.println("Eating...");
    }
}

class Dog extends Animal {

    void bark() {
        System.out.println("Barking...");
    }
}
```

Usage

```java
Dog dog = new Dog();

dog.eat();   // inherited

dog.bark();  // own method
```

---

# 4. Interview Diagram

```text
               Animal
        +----------------+
        | eat()          |
        | sleep()        |
        +-------▲--------+
                │
      "Dog IS-A Animal"
                │
                ▼
              Dog
        +----------------+
        | bark()         |
        +----------------+
```

### Arrow Explanation

* `extends` creates an **IS-A** relationship.
* Child automatically inherits accessible members.
* Child can add new behaviour or override existing behaviour.

---

# 5. Constructor Execution ⭐⭐⭐⭐⭐

One of the most asked questions.

```java
class Animal {

    Animal() {
        System.out.println("Animal");
    }
}

class Dog extends Animal {

    Dog() {
        System.out.println("Dog");
    }
}
```

```java
new Dog();
```

Execution Flow

```text
new Dog()

     │
     ▼

Allocate Dog Object

     │
     │ JVM inserts super()
     ▼

Animal Constructor

     │
     │ Parent initialized first
     ▼

Dog Constructor

     │
     ▼

Object Ready
```

Output

```text
Animal

Dog
```

### Why?

Parent state must be initialized before child state.

---

# 6. Types of Inheritance

```text
                Inheritance

                     │
     ┌───────────────┼──────────────┐
     ▼               ▼              ▼

 Single         Multilevel      Hierarchical

Animal          Animal          Animal
  │               │            ┌───┴───┐
 Dog            Mammal        Dog    Cat
                  │
                 Dog
```

---

### Multiple Inheritance?

```text
        Animal A

             ▲
             │

Dog ? 

             │
             ▼

        Animal B
```

❌ Java **does not support** multiple inheritance with classes.

Reason:

Diamond Problem.

---

# 7. Diamond Problem

```text
          Animal

         /      \

 Bird           Mammal

         \      /

          Bat
```

Suppose both parent classes have

```java
move();
```

Question:

Which implementation should `Bat` inherit?

Ambiguity.

To avoid this, Java disallows multiple inheritance of classes.

Interfaces solve this differently using explicit conflict resolution.

---

# 8. Where is Inheritance Used?

Good examples

```text
Exception
     │
IOException
     │
FileNotFoundException
```

Collections

```text
AbstractList
      │
ArrayList
```

Spring

```text
RuntimeException
       ▲
YourCustomException
```

---

# 9. Interview Questions ⭐⭐⭐⭐⭐

### Q1. What is Inheritance?

An **IS-A** relationship where a child acquires properties and behaviour of a parent.

---

### Q2. Difference between IS-A and HAS-A?

| IS-A             | HAS-A                   |
| ---------------- | ----------------------- |
| Inheritance      | Composition/Aggregation |
| Dog is an Animal | Car has an Engine       |
| `extends`        | Field reference         |

---

### Q3. Can constructors be inherited?

❌ No.

They are executed through `super()`.

They are not inherited.

---

### Q4. Can private members be inherited?

Technically they are part of the object, but **they are not directly accessible** from the child class.

```java
class Animal {

    private int age;
}
```

```java
class Dog extends Animal {

    void print() {

        // age ❌ Not Accessible

    }
}
```

---

### Q5. Can child override static methods?

❌ No.

Static methods are **hidden**, not overridden.

This is a common interview question.

---

### Q6. Why doesn't Java support multiple inheritance?

To avoid the **Diamond Problem** and ambiguity.

---

### Q7. Why is Composition preferred over Inheritance?

Because inheritance creates:

* Tight coupling
* Fragile hierarchies
* Less flexibility

Composition provides:

* Loose coupling
* Better testing
* Easier maintenance

This is one of the most common senior-level design discussions.

---

# 10. Interview Trap 🚨

### Trap 1

Every "IS-A" should use inheritance?

❌ No.

Example

```text
Car IS-A Vehicle
```

Good.

But

```text
Stack IS-A Vector
```

Java's legacy `Stack` extending `Vector` is widely considered a poor design choice because a stack shouldn't expose arbitrary vector operations. This is a classic example of choosing inheritance where composition would have been better.

---

### Trap 2

Inheritance = Code Reuse?

Only partly.

Modern design uses inheritance **for modelling an IS-A relationship**, not merely for sharing code.

---

### Trap 3

Can a subclass reduce method visibility?

```java
public void print()
```

↓

```java
protected void print()
```

❌ Not allowed.

Visibility cannot be reduced when overriding.

---

# 11. Cheat Sheet

```text
                Inheritance

          Child extends Parent
                  │
                  ▼
             IS-A Relationship
                  │
                  ▼
          Reuse Behaviour
                  │
                  ▼
        Parent Constructor First
                  │
                  ▼
        Child Constructor Second
```

---

# Quick Comparison

| Composition                | Inheritance            |
| -------------------------- | ---------------------- |
| HAS-A                      | IS-A                   |
| Loose coupling             | Tight coupling         |
| More flexible              | Less flexible          |
| Preferred in modern design | Use only for true IS-A |

---

# Frequently Asked Follow-up

> **When should I choose inheritance?**

Use inheritance only when **all** of these are true:

```text
            True IS-A Relationship
                     │
                     ▼
          Child should behave as Parent
                     │
                     ▼
      Parent contract should be preserved
                     │
                     ▼
           Reuse makes domain sense
```

If your main goal is just code reuse, composition is usually the better choice.

---

# Quick Revision

* **Inheritance = IS-A**
* Uses `extends`
* Parent constructor executes before child constructor.
* Constructors are **not inherited**.
* Java doesn't support multiple inheritance of classes because of the Diamond Problem.
* Prefer **Composition over Inheritance** unless the relationship is a genuine **IS-A**.

---

## Connection to Next Topic

```text
Object Lifecycle
      │
      ▼
Object Relationships
      │
      ▼
Encapsulation
      │
      ▼
Abstraction
      │
      ▼
Inheritance
      │
      ▼
Polymorphism ⭐⭐⭐⭐⭐
```

The next chapter, **Polymorphism**, ties everything together. It explains how Java decides **which method actually executes** at runtime, covering **method overloading vs overriding**, **compile-time vs runtime polymorphism**, **dynamic method dispatch**, and the JVM's method lookup process—topics that are almost guaranteed in senior Java interviews.

Agreed. From here onwards, we'll **club smaller topics**. We'll spend our time on the topics that matter most (Collections, JVM, Concurrency, Spring, Kafka, Hibernate).

---

# Chapter 8 — Polymorphism ⭐⭐⭐⭐⭐

> **Interview Weight:** ⭐⭐⭐⭐⭐

## What?

**One interface, multiple implementations.**

> Same method call → Different behaviour depending on the object.

---

## Types

```text
               Polymorphism

                    │
        ┌───────────┴───────────┐
        ▼                       ▼

Compile Time             Runtime

(Method Overloading)    (Method Overriding)
```

---

## 1. Compile-Time Polymorphism (Overloading)

```java
class Calculator {

    int add(int a, int b) { }

    double add(double a, double b) { }

    int add(int a, int b, int c) { }
}
```

Compiler decides **which method** to call.

---

## 2. Runtime Polymorphism (Overriding)

```java
class Animal {
    void sound() {
        System.out.println("Animal");
    }
}

class Dog extends Animal {
    @Override
    void sound() {
        System.out.println("Bark");
    }
}

Animal animal = new Dog();
animal.sound();
```

Output

```
Bark
```

---

## Interview Flow

```text
Animal a = new Dog();

      │
      ▼
Reference Type = Animal

      │
      ▼
Actual Object = Dog

      │
      ▼
Runtime checks actual object

      │
      ▼
Dog.sound()
```

👉 Compiler checks **reference type**.

👉 JVM executes based on **actual object**.

---

## Interview Questions ⭐⭐⭐⭐⭐

* Difference between Overloading and Overriding?
* Why is method overloading compile time?
* Why is overriding runtime?
* Can private methods be overridden? (**No**)
* Can static methods be overridden? (**No, hidden**)
* Why doesn't field access show polymorphism?
* What is Dynamic Method Dispatch?

---

## Cheat Sheet

| Overloading          | Overriding       |
| -------------------- | ---------------- |
| Compile Time         | Runtime          |
| Same class           | Parent-Child     |
| Different Parameters | Same Signature   |
| Faster lookup        | Dynamic Dispatch |

---

# Object Lifecycle (Quick Recap)

```text
new
 │
 ▼
Heap Allocation
 │
 ▼
Constructor
 │
 ▼
Object Ready
 │
 ▼
Used
 │
 ▼
Eligible for GC
```

---

# OOP Cheat Sheet ⭐⭐⭐⭐⭐

| Concept       | Keyword                   | Relationship            |
| ------------- | ------------------------- | ----------------------- |
| Association   | Uses                      | Objects know each other |
| Aggregation   | Weak HAS-A                | Independent lifecycle   |
| Composition   | Strong HAS-A              | Shared lifecycle        |
| Encapsulation | Hide Data                 | Protect state           |
| Abstraction   | Hide Implementation       | Contract                |
| Inheritance   | IS-A                      | Reuse behaviour         |
| Polymorphism  | One Interface, Many Forms | Runtime flexibility     |

---

# Frequently Asked OOP Interview Questions (7+ Years)

* Difference between Association, Aggregation and Composition?
* Why prefer Composition over Inheritance?
* Difference between Encapsulation and Abstraction?
* IS-A vs HAS-A?
* Overloading vs Overriding?
* Can constructors be overridden?
* Can private methods be overridden?
* Can static methods be overridden?
* What is Dynamic Method Dispatch?
* Why doesn't Java support multiple inheritance?
* Explain Liskov Substitution Principle with inheritance.
* Give a real-world example where inheritance is a bad design.
* How does Spring use composition and polymorphism?

---

## OOP Module ✅ Completed

```text
                 OOP

                  │
        ┌─────────┴──────────┐
        ▼                    ▼

Object Lifecycle      Object Relationships
                              │
             ┌────────────────┼────────────────┐
             ▼                ▼                ▼

        Association     Aggregation     Composition

                              │
                              ▼

                     Four Pillars of OOP

      Encapsulation → Abstraction → Inheritance → Polymorphism
```

---

## Next (High Priority ⭐⭐⭐⭐⭐)

Now we'll move to the section where **60–70% of senior Java interviews** are focused:

```text
Collections Framework
        │
        ▼
List
Map
Set
Queue
Deque
        │
        ▼
ArrayList Internals
LinkedList Internals
HashMap Internals
ConcurrentHashMap
HashSet
TreeMap
PriorityQueue
Fail-Fast
Iterator
Comparable vs Comparator
equals() & hashCode()
```

This is where we'll slow down again, because it's one of the most heavily tested areas for 7+ years Java backend interviews.
