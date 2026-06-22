# SOLID Principles — Complete Deep Dive

## 1. Why This Concept Matters

SOLID is the foundation of maintainable, extensible, testable object-oriented design. These five principles guide you away from rigid, fragile code that breaks when you try to add features and toward code that welcomes change. In production, SOLID violations manifest as "that class has 2000 lines and no one wants to touch it," "adding a new payment method requires changing five files," and "this change broke something completely unrelated." Interviewers test SOLID at every level because it's the universal language of code quality — you're expected to recognize violations, explain the fixes, and apply the principles naturally in your design. Many design patterns (Strategy, Observer, Factory, Template Method) are direct solutions for specific SOLID violations.

**The five principles:**
- **S**ingle Responsibility Principle (SRP): one class = one responsibility
- **O**pen/Closed Principle (OCP): open for extension, closed for modification
- **L**iskov Substitution Principle (LSP): subtypes must be substitutable for base types
- **I**nterface Segregation Principle (ISP): many small interfaces > one large interface
- **D**ependency Inversion Principle (DIP): depend on abstractions, not concretions

## 2. Deep Dive of Each Principle

### S — Single Responsibility Principle (SRP)

**Definition:** A class should have one, and only one, reason to change.

**Why it matters:** When a class has multiple responsibilities, a change to one responsibility affects all users of the class, even those who only use the other responsibility. This makes the class fragile — fixing one thing breaks another. It also makes testing harder — you need to set up all responsibilities even when testing just one.

**Violation:**
```java
// This class has THREE responsibilities:
// 1. Business logic (calculatePay)
// 2. Persistence (save)
// 3. Presentation (printReport)
class Employee {
    private String id;
    private String name;
    private double salary;
    
    public double calculatePay() { /* business logic */ }
    public void save() { /* JDBC code to persist */ }
    public void printReport() { /* HTML/PDF generation */ }
}
```
Problems: Changing DB schema requires modifying Employee. Changing report format requires modifying Employee. Testing calculatePay requires mocking DB and printer.

**Fix — Separate responsibilities:**
```java
class Employee {
    private String id;
    private String name;
    private double salary;
    // Only business data and behavior
    public double calculatePay() { /* business logic */ }
}

class EmployeeRepository {
    public void save(Employee emp) { /* JDBC code */ }
}

class EmployeeReport {
    public void print(Employee emp) { /* HTML/PDF generation */ }
}
```

**Detection:**
- Class description contains "and": "Employee manages payroll AND generates reports"
- Class has > 200 lines
- Class has fields from different domains (e.g., email configuration + order processing)
- You can't describe what the class does in one sentence without "and"

---

### O — Open/Closed Principle (OCP)

**Definition:** Software entities (classes, modules, functions) should be open for extension but closed for modification.

**Why it matters:** When you need to add new functionality, you should ADD new code (new classes, new methods) instead of MODIFYING existing code. Modifying existing code risks introducing bugs in code that was already working. Extending with new code limits the blast radius of changes.

**Violation — if-else chain that grows:**
```java
class AreaCalculator {
    public double calculateArea(Object shape) {
        if (shape instanceof Circle) {
            Circle c = (Circle) shape;
            return Math.PI * c.getRadius() * c.getRadius();
        } else if (shape instanceof Rectangle) {
            Rectangle r = (Rectangle) shape;
            return r.getWidth() * r.getHeight();
        }
        // Adding Triangle: MUST modify this method
        throw new IllegalArgumentException("Unknown shape");
    }
}
```
Problem: Adding a Triangle requires modifying `calculateArea()`. This method is closed for modification but NOT open for extension.

**Fix — Polymorphism (Strategy pattern):**
```java
interface Shape {
    double area();
}

class Circle implements Shape {
    private double radius;
    public double area() { return Math.PI * radius * radius; }
}

class Rectangle implements Shape {
    private double width, height;
    public double area() { return width * height; }
}

// New shapes: just implement Shape — no existing code changes
class Triangle implements Shape {
    private double base, height;
    public double area() { return 0.5 * base * height; }
}

class AreaCalculator {
    // Closed for modification (never changes)
    // Open for extension (new Shape implementations)
    public double calculateArea(Shape shape) {
        return shape.area();
    }
}
```

**Detection:**
- Switch/if-else statements checking type (`instanceof`)
- Adding new feature requires modifying existing classes
- "I need to add a case to this switch statement"

---

### L — Liskov Substitution Principle (LSP)

**Definition:** Subtypes must be substitutable for their base types without altering the correctness of the program. If S is a subtype of T, then objects of type T should be replaceable with objects of type S without changing the program's desirable properties.

**Why it matters:** When you use inheritance, the subclass should behave like the parent class. If it doesn't, code that works with the parent class will break when given a subclass instance. This is the most subtle and frequently violated principle.

**Classic violation — Square/Rectangle:**
```java
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
        super.setHeight(w); // Violates LSP!
    }
    
    @Override
    public void setHeight(int h) {
        super.setWidth(h);
        super.setHeight(h); // Violates LSP!
    }
}

// Client code that works for Rectangle but breaks for Square:
void resize(Rectangle r) {
    r.setWidth(5);
    r.setHeight(10);
    assert r.getArea() == 50; // True for Rectangle. False for Square! (100)
}
```
The client assumes `setWidth(5)` + `setHeight(10)` → area = 50. Square violates this assumption.

**Fix:** Don't use inheritance where the relationships aren't "is-a" in behavior. Separate Square and Rectangle, or use a common interface:
```java
interface Shape {
    int getArea();
}

class Rectangle implements Shape {
    private int width, height;
    public Rectangle(int w, int h) { this.width = w; this.height = h; }
    public int getArea() { return width * height; }
}

class Square implements Shape {
    private int side;
    public Square(int side) { this.side = side; }
    public int getArea() { return side * side; }
}
```

**LSP violations to recognize:**
- Subclass overrides a method to throw `UnsupportedOperationException` (e.g., `UnmodifiableList.add()`)
- Subclass changes the behavior of a method in a way that breaks client expectations
- Subclass has stronger preconditions (e.g., requires non-null where parent accepts null)
- Subclass has weaker postconditions (e.g., returns null where parent never returned null)

---

### I — Interface Segregation Principle (ISP)

**Definition:** Clients should not be forced to depend on interfaces they do not use. Many small, specific interfaces are better than one large, general-purpose interface.

**Why it matters:** When an interface has methods that some clients don't need, those clients must implement empty or throwing methods. Changes to the interface (adding a new method) force changes in all clients, even those that don't use the new method.

**Violation — Fat interface:**
```java
interface Worker {
    void work();
    void eat();
    void sleep();
}

class HumanWorker implements Worker {
    public void work() { System.out.println("Working..."); }
    public void eat() { System.out.println("Eating..."); }
    public void sleep() { System.out.println("Sleeping..."); }
}

class RobotWorker implements Worker {
    public void work() { System.out.println("Working..."); }
    
    public void eat() { throw new UnsupportedOperationException("Robots don't eat"); }
    public void sleep() { throw new UnsupportedOperationException("Robots don't sleep"); }
}
```
`RobotWorker` is forced to implement `eat()` and `sleep()` which don't apply. Any client that calls `worker.eat()` on a `RobotWorker` will get an exception.

**Fix — Segregated interfaces:**
```java
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

class HumanWorker implements Workable, Eatable, Sleepable {
    public void work() { }
    public void eat() { }
    public void sleep() { }
}

class RobotWorker implements Workable {
    public void work() { }
    // No unnecessary methods to implement!
}

// Clients depend only on the interfaces they need:
class Cafeteria {
    void provideMeal(Eatable worker) { worker.eat(); }
}
```

**Detection:**
- Interface has methods that some implementations throw `UnsupportedOperationException` for
- Adding a method to an interface breaks many existing implementations
- "I need to implement all 15 methods even though I only use 3"

---

### D — Dependency Inversion Principle (DIP)

**Definition:** High-level modules should not depend on low-level modules. Both should depend on abstractions. Abstractions should not depend on details. Details should depend on abstractions.

**Why it matters:** When high-level (business logic) depends on low-level (database, email, file system), changing infrastructure requires changing business logic. This makes the system rigid — business logic, the most important part of the system, becomes tightly coupled to implementation details. DIP, combined with Dependency Injection, allows you to swap implementations without changing business logic.

**Violation — High-level depends on low-level:**
```java
class OrderService {
    private MySQLDatabase database = new MySQLDatabase(); // Direct dependency!
    private SmtpEmailService emailService = new SmtpEmailService(); // Direct dependency!
    
    public void createOrder(Order order) {
        database.save(order);
        emailService.sendConfirmation(order.getEmail());
    }
}
```
Problems: Can't test OrderService without a real MySQL database. Can't switch from MySQL to PostgreSQL without changing OrderService. Can't switch from SMTP to SendGrid without changing OrderService.

**Fix — Depend on abstractions + Dependency Injection:**
```java
// Abstractions (interfaces) that both high and low level depend on
interface OrderRepository {
    void save(Order order);
}

interface NotificationService {
    void send(String to, String message);
}

// Low-level implementations depend on abstractions
class MySQLOrderRepository implements OrderRepository {
    public void save(Order order) { /* JDBC code */ }
}

class SmtpNotificationService implements NotificationService {
    public void send(String to, String message) { /* SMTP code */ }
}

// High-level module depends ONLY on abstractions
class OrderService {
    private final OrderRepository repository;
    private final NotificationService notifier;
    
    // Dependencies injected via constructor — NOT created internally
    public OrderService(OrderRepository repo, NotificationService notif) {
        this.repository = repo;
        this.notifier = notif;
    }
    
    public void createOrder(Order order) {
        repository.save(order);
        notifier.send(order.getEmail(), "Order confirmed!");
    }
}

// Easy to test — mock the dependencies:
class OrderServiceTest {
    @Test
    void testCreateOrder() {
        OrderRepository mockRepo = mock(OrderRepository.class);
        NotificationService mockNotif = mock(NotificationService.class);
        OrderService service = new OrderService(mockRepo, mockNotif);
        // ... test with mocks
    }
}
```

**Detection:**
- `new ConcreteClass()` inside business logic
- Static method calls to infrastructure classes
- `config.getDatabaseUrl()` in business logic
- Hard to unit test (requires real DB, real email server)
- Changing database requires changing business logic code

## 3. SOLID in Practice — A Real Example

```java
// BEFORE — violates all five principles:
class OrderProcessor {
    private Database db = new Database("localhost:3306", "root", "pass");
    
    public void process(Order order) {
        db.connect();
        String sql = "INSERT INTO orders VALUES (" + order.getId() + ", ...)"; // SQL injection!
        db.execute(sql);
        db.close();
        
        // Send email
        try {
            Process emailProc = Runtime.getRuntime().exec("sendmail " + order.getEmail());
        } catch (IOException e) { }
        
        // Print invoice
        System.out.println("Invoice: " + order.getId());
    }
}

// AFTER — following SOLID:
interface OrderRepository {
    void save(Order order);
}

interface EmailService {
    void send(String to, String subject, String body);
}

interface InvoicePrinter {
    void print(Order order);
}

class JdbcOrderRepository implements OrderRepository {
    private final DataSource dataSource;
    
    public JdbcOrderRepository(DataSource ds) { this.dataSource = ds; }
    
    public void save(Order order) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO orders (id, ...) VALUES (?, ...)")) {
            ps.setLong(1, order.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}

class SmtpEmailService implements EmailService {
    public void send(String to, String subject, String body) {
        // SMTP code
    }
}

class ConsoleInvoicePrinter implements InvoicePrinter {
    public void print(Order order) {
        System.out.println("Invoice: " + order.getId());
    }
}

class OrderProcessor {
    private final OrderRepository repository;
    private final EmailService emailService;
    private final InvoicePrinter printer;
    
    // All dependencies injected — no "new" for infrastructure
    public OrderProcessor(OrderRepository repo, EmailService email, InvoicePrinter printer) {
        this.repository = repo;
        this.emailService = email;
        this.printer = printer;
    }
    
    public void process(Order order) {
        repository.save(order);
        emailService.send(order.getEmail(), "Order Confirmed", "Your order #" + order.getId());
        printer.print(order);
    }
}
```

## 4. Common Questions

### How do SOLID relate to each other?
- **SRP** and **ISP** are about cohesion (keeping related things together, unrelated things apart)
- **OCP** and **DIP** work together: DIP (depend on abstractions) enables OCP (extend without modifying)
- **LSP** is about correct inheritance — you can't have OCP without LSP (substituting a subtype shouldn't break open/closed behavior)

### Which design patterns solve which SOLID violations?
| Violation | Pattern | How |
|-----------|---------|-----|
| SRP violation (god class) | Facade, Adapter | Extract responsibilities into separate classes |
| OCP violation (if-else chain) | Strategy, Template Method | Inject interchangeable algorithms |
| ISP violation (fat interface) | Adapter, Decorator | Split into smaller interfaces |
| DIP violation (tight coupling) | Factory, Abstract Factory | Create objects via factory, inject dependencies |

## 5. Final 30-Second Answer

SOLID = 5 OOP design principles for maintainable code. **SRP**: one class = one responsibility (not "and"). **OCP**: add NEW code, don't MODIFY old code (polymorphism over if-else). **LSP**: subclass must work where parent works (don't break client assumptions). **ISP**: many small interfaces over one fat interface (no UnsupportedOperationException). **DIP**: depend on interfaces, not concrete classes (inject dependencies, don't `new` them). Together: small focused classes (SRP) with clear interfaces (ISP) that you can extend via new implementations (OCP) by injecting them (DIP) without breaking existing code (LSP). Watch for: god classes, switch-by-type, NotImplementedException, new ConcreteClass in business logic.