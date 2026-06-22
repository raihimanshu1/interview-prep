# Factory Design Pattern — Complete Deep Dive

## 1. Why This Concept Matters

Factory pattern is one of the most commonly asked creational design patterns. It provides an interface for creating objects without specifying their exact class. In production, factories are used for object creation with complex logic, dependency injection, plugin systems, and decoupling clients from concrete implementations. Interviewers test this because it reveals your understanding of abstraction, loose coupling, and design principles.

Misunderstanding Factory pattern causes:
- Tight coupling between client and concrete classes
- Difficult testing due to hardcoded dependencies
- Code duplication when creating similar objects
- Inflexible code that resists change

## 2. Basic Meaning

**Factory Method**: defines an interface for creating objects, lets subclasses decide which class to instantiate.
**Simple Factory**: not a formal GoF pattern, but commonly used — single factory class with method returning different types.
**Abstract Factory**: provides interface for creating families of related objects without specifying concrete classes.

**Key vocabulary:**
- **Creator**: class declaring factory method
- **Product**: interface/abstract class of created objects
- **Concrete Creator**: subclass overriding factory method
- **Concrete Product**: actual object created
- **Decoupling**: client code depends on abstraction, not concrete class
- **Open/Closed Principle**: add new products without modifying existing client code

## 3. Real Code / Real Example

```java
// === PRODUCT INTERFACE ===
interface Payment {
    void pay(double amount);
}

// === CONCRETE PRODUCTS ===
class CreditCardPayment implements Payment {
    private String cardNumber;
    public CreditCardPayment(String card) { this.cardNumber = card; }
    @Override public void pay(double amount) {
        System.out.println("Paid $" + amount + " via Credit Card ending " + cardNumber.substring(cardNumber.length() - 4));
    }
}

class PayPalPayment implements Payment {
    private String email;
    public PayPalPayment(String email) { this.email = email; }
    @Override public void pay(double amount) {
        System.out.println("Paid $" + amount + " via PayPal (" + email + ")");
    }
}

class CryptoPayment implements Payment {
    private String walletAddress;
    public CryptoPayment(String wallet) { this.walletAddress = wallet; }
    @Override public void pay(double amount) {
        System.out.println("Paid $" + amount + " via Crypto to " + walletAddress);
    }
}

// === SIMPLE FACTORY ===
class PaymentFactory {
    public static Payment createPayment(String type, String details) {
        return switch (type.toLowerCase()) {
            case "credit" -> new CreditCardPayment(details);
            case "paypal" -> new PayPalPayment(details);
            case "crypto" -> new CryptoPayment(details);
            default -> throw new IllegalArgumentException("Unknown payment type: " + type);
        };
    }
}

// === FACTORY METHOD PATTERN ===
abstract class Document {
    abstract void open();
    abstract void save();
}

class PdfDocument extends Document {
    @Override void open() { System.out.println("Opening PDF"); }
    @Override void save() { System.out.println("Saving PDF"); }
}

class WordDocument extends Document {
    @Override void open() { System.out.println("Opening Word"); }
    @Override void save() { System.out.println("Saving Word"); }
}

abstract class DocumentApplication {
    // Factory method — subclasses decide which document to create
    abstract Document createDocument(String content);
    
    void newDocument(String content) {
        Document doc = createDocument(content);
        doc.open();
        doc.save();
        System.out.println("Document created successfully\n");
    }
}

class PdfApplication extends DocumentApplication {
    @Override Document createDocument(String content) { return new PdfDocument(); }
}

class WordApplication extends DocumentApplication {
    @Override Document createDocument(String content) { return new WordDocument(); }
}

// === ABSTRACT FACTORY ===
interface GUIFactory {
    Button createButton();
    TextField createTextField();
    Checkbox createCheckbox();
}

class WindowsFactory implements GUIFactory {
    public Button createButton() { return new WindowsButton(); }
    public TextField createTextField() { return new WindowsTextField(); }
    public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}

class MacFactory implements GUIFactory {
    public Button createButton() { return new MacButton(); }
    public TextField createTextField() { return new MacTextField(); }
    public Checkbox createCheckbox() { return new MacCheckbox(); }
}

// Abstract products
interface Button { void paint(); }
class WindowsButton implements Button { public void paint() { System.out.println("Windows button"); } }
class MacButton implements Button { public void paint() { System.out.println("Mac button"); } }
interface TextField { void render(); }
class WindowsTextField implements TextField { public void render() { System.out.println("Windows text field"); } }
class MacTextField implements TextField { public void render() { System.out.println("Mac text field"); } }
interface Checkbox { void toggle(); }
class WindowsCheckbox implements Checkbox { public void toggle() { System.out.println("Windows checkbox"); } }
class MacCheckbox implements Checkbox { public void toggle() { System.out.println("Mac checkbox"); } }

// === DEMO ===
public class FactoryDemo {
    public static void main(String[] args) {
        // === SIMPLE FACTORY ===
        System.out.println("=== Simple Factory ===");
        Payment cc = PaymentFactory.createPayment("credit", "1234-5678-9012-3456");
        cc.pay(100.0);
        Payment pp = PaymentFactory.createPayment("paypal", "user@example.com");
        pp.pay(50.0);
        Payment crypto = PaymentFactory.createPayment("crypto", "0x1234abcd");
        crypto.pay(200.0);

        // === FACTORY METHOD ===
        System.out.println("=== Factory Method ===");
        DocumentApplication pdfApp = new PdfApplication();
        pdfApp.newDocument("PDF content");
        
        DocumentApplication wordApp = new WordApplication();
        wordApp.newDocument("Word content");

        // === ABSTRACT FACTORY ===
        System.out.println("=== Abstract Factory ===");
        GUIFactory windows = new WindowsFactory();
        Button winButton = windows.createButton();
        TextField winText = windows.createTextField();
        winButton.paint();
        winText.render();

        GUIFactory mac = new MacFactory();
        Button macButton = mac.createButton();
        TextField macText = mac.createTextField();
        macButton.paint();
        macText.render();
    }
}
```

Expected output:
```
=== Simple Factory ===
Paid $100.0 via Credit Card ending 3456
Paid $50.0 via PayPal (user@example.com)
Paid $200.0 via Crypto to 0x1234abcd

=== Factory Method ===
Opening PDF
Saving PDF
Document created successfully

Opening Word
Saving Word
Document created successfully

=== Abstract Factory ===
Windows button
Windows text field
Mac button
Mac text field
```

## 4. What Happens Internally

**Simple Factory:**
```java
class PaymentFactory {
    public static Payment createPayment(String type, String details) {
        // Switch statement maps type to concrete class
        // Client never sees concrete class names
    }
}
```
Client calls `PaymentFactory.createPayment("credit", "...")`. Factory decides which concrete class to instantiate. Client only knows `Payment` interface.

**Factory Method:**
```java
abstract class Application {
    abstract Document createDocument(); // factory method
    
    void newDocument() {
        Document doc = createDocument(); // calls subclass implementation
        // use doc...
    }
}

class PdfApplication extends Application {
    Document createDocument() { return new PdfDocument(); }
}
```
Subclass overrides factory method to return different concrete product.

**Abstract Factory:**
```java
interface GUIFactory {
    Button createButton();
    TextField createTextField();
    // family of related products
}
```
Returns factory for product family (Windows, Mac). Client gets all UI components from same factory, ensuring consistency.

## 5. Tricky Interview Cases

**Case 1 — Simple Factory vs Factory Method**
```java
// Simple Factory: static method, single class
Payment p = PaymentFactory.create("credit");

// Factory Method: abstract method in superclass, overridden by subclasses
abstract class App { abstract Document create(); }
class PdfApp extends App { Document create() { return new Pdf(); } }
```
Output: Different structures.
Explanation: Simple Factory = one class with switch/if-else. Factory Method = inheritance-based, subclass decides.

**Case 2 — Abstract Factory for cross-platform UI**
```java
// Abstract factory ensures consistent look-and-feel
GUIFactory factory = getFactoryForOS(); // WindowsFactory or MacFactory
Button b = factory.createButton();
TextField t = factory.createTextField();
// All components from same factory → consistent theme
```
Output: Consistent UI components.
Explanation: Abstract Factory creates families of related objects.

**Case 3 — Adding new product to Simple Factory breaks OCP**
```java
class PaymentFactory {
    static Payment create(String type) {
        switch(type) {
            case "credit": return new CreditCardPayment();
            case "paypal": return new PayPalPayment();
            case "crypto": return new CryptoPayment();
            // To add "bitcoin": must MODIFY this class!
        }
    }
}
```
Output: Modification required for each new product.
Explanation: Simple Factory violates Open/Closed Principle.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Using Simple Factory for everything | Grows massive switch statements | Use Factory Method or Abstract Factory for extensibility |
| Factory returning concrete types | Client still coupled to concrete class | Return interface/abstract class |
| Over-engineering | Simple creation needs no pattern | Use Simple Factory for 2-3 types; Factory Method for extensibility |
| Forgetting dependency injection | Hard to test with factories | Inject factory, don't call static directly |
| Abstract factory with unrelated products | Not a true family | Group logically related products |

## 7. Production Usage

**Spring bean factory:**
```java
// Spring is essentially a sophisticated factory
ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
PaymentService service = ctx.getBean(PaymentService.class);
// Spring decides which implementation to inject
```

**JSON parsing with factory:**
```java
interface JsonParser { Object parse(String json); }
class GsonParser implements JsonParser { ... }
class JacksonParser implements JsonParser { ... }

class JsonParserFactory {
    static JsonParser create(String library) {
        if ("gson".equals(library)) return new GsonParser();
        if ("jackson".equals(library)) return new JacksonParser();
        throw new IllegalArgumentException();
    }
}
```

**Database connection factory:**
```java
interface Connection { void query(String sql); }
class MySQLConnection implements Connection { ... }
class PostgreSQLConnection implements Connection { ... }

class ConnectionFactory {
    static Connection create(String dbType, String url) {
        return switch(dbType) {
            case "mysql" -> new MySQLConnection(url);
            case "postgres" -> new PostgreSQLConnection(url);
            default -> throw new IllegalArgumentException();
        };
    }
}
```

## 8. Advanced Details

- **Dependency Injection as factory:** Spring's `@Bean` methods are factory methods. Container manages lifecycle.
- **Reflection-based factory:** `Class.forName(className).getConstructor().newInstance()` — dynamic loading without switch.
- **ServiceLoader (Java SPI):** `ServiceLoader.load(Service.class)` discovers implementations at runtime. Used by JDBC, JNDI.
- **Factory registration:** Map of `String → Supplier<Product>` for extensible factory without switch.
- **Abstract Factory vs Factory Method:** Abstract Factory creates families; Factory Method creates one product type.

## 9. Interview Questions And Answers

### Beginner
Q: What is the Factory pattern? Why is it useful?
A: Factory provides an interface for creating objects without specifying exact classes. It decouples client code from concrete implementations, making code more flexible and easier to extend. Instead of `new ConcreteClass()`, client calls `Factory.create()`.

### Intermediate
Q: What is the difference between Simple Factory, Factory Method, and Abstract Factory?
A:
- **Simple Factory**: single class with static method using switch/if-else. Not a formal GoF pattern.
- **Factory Method**: defines factory method in abstract class, subclasses override to create different products.
- **Abstract Factory**: provides interface for creating families of related objects without specifying concrete classes.

### Senior
Q: You are building a notification system supporting Email, SMS, Push, and Slack. A junior developer hardcodes `if-else` with `new EmailSender()`, `new SMSSender()`, etc. How do you refactor using Factory pattern?
A: Create notification interface and factory:

```java
interface Notifier { void send(String msg); }

class EmailNotifier implements Notifier { ... }
class SMSNotifier implements Notifier { ... }

// Factory with registration (extensible)
class NotifierFactory {
    private static final Map<String, Supplier<Notifier>> registry = new HashMap<>();
    
    static {
        registry.put("email", EmailNotifier::new);
        registry.put("sms", SMSNotifier::new);
    }
    
    static Notifier create(String type) {
        Supplier<Notifier> supplier = registry.get(type);
        if (supplier == null) throw new IllegalArgumentException();
        return supplier.get();
    }
    
    static void register(String type, Supplier<Notifier> supplier) {
        registry.put(type, supplier);
    }
}
```
New channels registered without modifying factory code (Open/Closed Principle).

### Tricky
Q: When would you choose Factory Method over Abstract Factory?
A: Factory Method when:
- Creating one type of product
- Subclasses should decide which concrete class to instantiate
- Example: cross-platform app where each OS has its own document type

Abstract Factory when:
- Creating families of related products
- Need to ensure consistency across product family
- Example: cross-platform UI toolkit (Windows button + Windows text field + Mac button + Mac text field)

## 10. Final 30-Second Answer

Factory patterns decouple object creation from usage. **Simple Factory**: static method with switch. **Factory Method**: abstract creator class, subclasses override to create products. **Abstract Factory**: interface for creating families of related objects. Use when creation logic is complex, or when you need to decouple client from concrete classes. Prefer DI containers (Spring) over manual factories — they handle lifecycle too. Return interfaces, not concrete types.