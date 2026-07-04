# Factory Method Pattern

> **Defines an interface for creating an object, but lets subclasses decide which class to instantiate.**

## 📖 Concept

**Real-world analogy:** A restaurant kitchen receives orders. The kitchen decides what type of food to prepare — you just order "Pizza" or "Pasta", and the kitchen creates it without you knowing the details.

## 🔍 When to Use

- A class cannot anticipate the type of objects it needs to create
- Subclasses want to specify which objects to create
- Centralize object creation logic to avoid code duplication
- Want to follow Open/Closed Principle — add new types without modifying existing code

## ✅ Interview Checklist

- [ ] Creator is an abstract class/interface with factory method
- [ ] Concrete creators override factory method
- [ ] Product interface for all created objects
- [ ] Client depends on abstraction, not concrete classes
- [ ] Factory method returns product interface/abstract class

## 🧪 Common Interview Question

**Problem:** Design a Payment Gateway system that supports Credit Card, PayPal, and UPI payments. The system should be open for new payment methods without modifying existing code.

## 💻 Java Implementation

### 1. Basic Factory Method — Simple & Clean

```java
// Product Interface
interface Payment {
    void pay(double amount);
}

// Concrete Products
class CreditCardPayment implements Payment {
    private String cardNumber;
    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    @Override
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via Credit Card " + cardNumber);
    }
}

class PayPalPayment implements Payment {
    private String email;
    public PayPalPayment(String email) { this.email = email; }
    @Override
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via PayPal (" + email + ")");
    }
}

class UPIPayment implements Payment {
    private String upiId;
    public UPIPayment(String upiId) { this.upiId = upiId; }
    @Override
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via UPI (" + upiId + ")");
    }
}

// Simple Factory — one class, one method
class PaymentFactory {
    public static Payment createPayment(String type) {
        return switch (type) {
            case "CREDIT" -> new CreditCardPayment("1234-5678");
            case "PAYPAL" -> new PayPalPayment("user@email.com");
            case "UPI" -> new UPIPayment("user@upi");
            default -> throw new IllegalArgumentException("Unknown: " + type);
        };
    }
}
```

### 2. Usage — Clean & Simple

```java
public class PaymentApplication {
    public static void main(String[] args) {
        // Create payment by type
        Payment payment = PaymentFactory.createPayment("UPI");
        payment.pay(1500.0);

        // Different type, different behavior
        Payment credit = PaymentFactory.createPayment("CREDIT");
        credit.pay(500.0);
    }
}
```

### 3. Full Working Example: Notification System

```java
// Notification.java
interface Notification {
    void send(String recipient, String message);
}

// EmailNotification.java
class EmailNotification implements Notification {
    private String smtpServer;
    
    public EmailNotification(String smtpServer) {
        this.smtpServer = smtpServer;
        System.out.println("Connecting to SMTP: " + smtpServer);
    }
    
    @Override
    public void send(String recipient, String message) {
        System.out.println("Email to " + recipient + ": " + message);
    }
}

// SMSNotification.java
class SMSNotification implements Notification {
    private String gateway;
    
    public SMSNotification(String gateway) {
        this.gateway = gateway;
        System.out.println("Connecting to SMS gateway: " + gateway);
    }
    
    @Override
    public void send(String recipient, String message) {
        System.out.println("SMS to " + recipient + ": " + message);
    }
}

// NotificationFactory.java
abstract class NotificationFactory {
    public abstract Notification createNotification();
    
    public void notifyUser(String recipient, String message) {
        Notification notification = createNotification();
        notification.send(recipient, message);
    }
}

class EmailFactory extends NotificationFactory {
    @Override
    public Notification createNotification() {
        return new EmailNotification("smtp.gmail.com");
    }
}

class SMSFactory extends NotificationFactory {
    @Override
    public Notification createNotification() {
        return new SMSNotification("twilio");
    }
}

// Application.java
public class Application {
    public static void main(String[] args) {
        NotificationFactory factory = new EmailFactory();
        factory.notifyUser("user@example.com", "Your order is confirmed!");
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Too many factory classes for each type | Use reflection + configuration, or enum-based factory |
| Hardcoding creation logic | Use dependency injection or configuration file |
| Breaking Open/Closed Principle when adding new type | Add new concrete creator class, don't modify existing |
| Returning concrete types instead of interfaces | Always return product interface/abstract class |

## 🎯 Related Interview Questions

1. **Design a Document Parser** that supports PDF, Word, CSV parsing using Factory
2. **Design a Notification Service** for Email, SMS, Push notifications
3. **What is the difference between Factory Method and Abstract Factory?**
4. **Design a Logging framework** where you can create FileLogger, ConsoleLogger, DatabaseLogger

## 🆚 Factory Method vs Abstract Factory

| Aspect | Factory Method | Abstract Factory |
|--------|---------------|------------------|
| Creates | One product type | Family of related products |
| Scale | Single product | Multiple products |
| Example | `PaymentFactory` creates only `Payment` | `UIFactory` creates Button + TextField + Checkbox |

---

## 🎓 Advanced: Abstract Factory Pattern (When You Need Families)

**Use When:** You need to create families of related products (e.g., cross-platform UI, cloud resources).

### Problem

Design a UI toolkit that supports **Dark Theme** and **Light Theme**. Each theme needs Button, TextField, and Checkbox. All components in a theme must match.

### Solution: Abstract Factory

```java
// 1. Abstract Products — interfaces for each component type
interface Button {
    void render();
}

interface TextField {
    void display();
}

// 2. Concrete Products — Dark theme variants
class DarkButton implements Button {
    @Override public void render() {
        System.out.println("Rendering Dark Button");
    }
}

class DarkTextField implements TextField {
    @Override public void display() {
        System.out.println("Displaying Dark TextField");
    }
}

// 3. Concrete Products — Light theme variants
class LightButton implements Button {
    @Override public void render() {
        System.out.println("Rendering Light Button");
    }
}

class LightTextField implements TextField {
    @Override public void display() {
        System.out.println("Displaying Light TextField");
    }
}

// 4. Abstract Factory — interface for creating families
interface UIFactory {
    Button createButton();
    TextField createTextField();
}

// 5. Concrete Factories — each creates matching family
class DarkThemeFactory implements UIFactory {
    @Override public Button createButton() { return new DarkButton(); }
    @Override public TextField createTextField() { return new DarkTextField(); }
}

class LightThemeFactory implements UIFactory {
    @Override public Button createButton() { return new LightButton(); }
    @Override public TextField createTextField() { return new LightTextField(); }
}

// 6. Client — uses factory, doesn't know concrete classes
class Application {
    private Button button;
    private TextField textField;

    public Application(UIFactory factory) {
        button = factory.createButton();
        textField = factory.createTextField();
    }

    public void render() {
        button.render();
        textField.display();
    }
}

// Usage
public class AdvancedFactoryDemo {
    public static void main(String[] args) {
        // Dark theme app
        UIFactory darkFactory = new DarkThemeFactory();
        Application darkApp = new Application(darkFactory);
        darkApp.render();

        // Light theme app — just swap factory!
        UIFactory lightFactory = new LightThemeFactory();
        Application lightApp = new Application(lightFactory);
        lightApp.render();
    }
}
```

### Key Difference from Simple Factory

| Simple Factory | Abstract Factory |
|---------------|------------------|
| One factory class | Multiple factory classes (one per family) |
| Creates one product type | Creates families of products |
| Easy to add new type | Easy to add new family |
| Adding new product type = modify factory | Adding new product type = add method to interface |

**Interview Tip:** Start with Simple Factory (as shown above). Mention Abstract Factory when interviewer asks about families or cross-platform concerns.

---

## 🎯 Alternative: Dynamic Factory with Registry (Open/Closed)

**Problem with switch:** Every new payment type requires modifying the factory.

**Solution:** Use a registry map — register factories once, then look up by type.

```java
// 1. Product Interface
interface Payment {
    void pay(double amount);
}

// 2. Concrete Products
class CreditCardPayment implements Payment {
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via Credit Card");
    }
}

class PayPalPayment implements Payment {
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via PayPal");
    }
}

class UPIPayment implements Payment {
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via UPI");
    }
}

// 3. Abstract Creator
abstract class PaymentCreator {
    public abstract Payment create();
}

// 4. Concrete Creators
class CreditCardCreator extends PaymentCreator {
    public Payment create() { return new CreditCardPayment(); }
}

class PayPalCreator extends PaymentCreator {
    public Payment create() { return new PayPalPayment(); }
}

class UPICreator extends PaymentCreator {
    public Payment create() { return new UPIPayment(); }
}

// 5. Factory Registry
class PaymentFactory {
    private static Map<String, PaymentCreator> registry = new HashMap<>();

    static {
        // Register all creators once (at class load)
        registry.put("CREDIT", new CreditCardCreator());
        registry.put("PAYPAL", new PayPalCreator());
        registry.put("UPI", new UPICreator());
    }

    public static Payment createPayment(String type) {
        PaymentCreator creator = registry.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown: " + type);
        }
        return creator.create();
    }

    // Bonus: Add new types at runtime!
    public static void registerPayment(String type, PaymentCreator creator) {
        registry.put(type, creator);
    }
}

// Usage
public class DynamicFactoryDemo {
    public static void main(String[] args) {
        Payment payment = PaymentFactory.createPayment("UPI");
        payment.pay(1500.0);
    }
}
```

### Dynamic Factory Key Points

| Feature | Static Switch | Dynamic Registry |
|---------|--------------|------------------|
| Adding new type | Modify factory | Just register new creator |
| Open/Closed | ❌ | ✅ |
| Runtime registration | ❌ | ✅ |
| Performance | Fast | Fast (HashMap lookup) |
| Type safety | Medium | High |

**When to use:** Payment gateways, notification systems, parsers — anywhere you expect new types to be added later.
