# Polymorphism — Complete Deep Dive

## 1. Why This Concept Matters

Polymorphism is one of the four pillars of OOP. It enables writing flexible, extensible code by treating diverse objects uniformly through a common interface. In production, polymorphism powers Strategy pattern implementations, plugin architectures, and test doubles (mocks). Misunderstanding polymorphism leads to fragile class hierarchies, broken Liskov Substitution Principle (LSP) violations, and code that resists extension. Interviewers test polymorphism to verify you understand runtime method dispatch, interface design, and when to favor composition over inheritance.

Misunderstanding polymorphism causes:
- LSP violations where subclass behavior breaks supertype contracts
- Fragile base class problem: changes in parent break children
- Overuse of inheritance leading to deep, rigid hierarchies
- Confusion between compile-time and runtime polymorphism

## 2. Basic Meaning

Polymorphism = "many forms". The same method name behaves differently based on the actual object type at runtime.

**Two types:**
- **Compile-time (static) polymorphism**: Method overloading — multiple methods with same name but different parameters. Resolved at compile time.
- **Runtime (dynamic) polymorphism**: Method overriding — subclass provides specific implementation of superclass method. Resolved at runtime via vtable.

Key vocabulary:
- **Upcasting**: treating subclass object as superclass type (`Animal a = new Dog()`)
- **Downcasting**: casting superclass reference back to subclass (`Dog d = (Dog) a`)
- **`instanceof`**: checks if object is instance of type before downcasting
- **Virtual method**: non-final, non-static, non-private method eligible for overriding
- **vtable / vpointer**: per-class table of method pointers used for dynamic dispatch
- **LSP (Liskov Substitution Principle)**: subclass must be substitutable for superclass
- **Method overriding**: subclass redefines superclass method with same signature

What it is NOT: Polymorphism is not just inheritance. Interfaces provide polymorphism without inheritance. Overloading is not true polymorphism (compile-time only).

## 3. Real Code / Real Example

```java
import java.util.*;

// === CLASS HIERARCHY ===
abstract class PaymentProcessor {
    abstract void process(double amount);
    void log(String msg) { System.out.println("[LOG] " + msg); }
}

class CreditCardProcessor extends PaymentProcessor {
    void process(double amount) {
        log("Processing credit card payment: $" + amount);
        chargeCard(amount);
    }
    void chargeCard(double amount) { System.out.println("Card charged: $" + amount); }
}

class PayPalProcessor extends PaymentProcessor {
    void process(double amount) {
        log("Processing PayPal payment: $" + amount);
        redirectToPayPal(amount);
    }
    void redirectToPayPal(double amount) { System.out.println("PayPal redirect: $" + amount); }
}

class CryptoProcessor extends PaymentProcessor {
    void process(double amount) {
        log("Processing crypto payment: $" + amount);
        convertAndSend(amount);
    }
    void convertAndSend(double amount) { System.out.println("Crypto sent: $" + amount); }
}

// === INTERFACE-BASED POLYMORPHISM ===
interface Notifier {
    void send(String message);
}

class EmailNotifier implements Notifier {
    public void send(String msg) { System.out.println("Email: " + msg); }
}

class SMSNotifier implements Notifier {
    public void send(String msg) { System.out.println("SMS: " + msg); }
}

class PushNotifier implements Notifier {
    public void send(String msg) { System.out.println("Push: " + msg); }
}

// === DEMO ===
public class PolymorphismDemo {
    public static void main(String[] args) {
        // === RUNTIME POLYMORPHISM: same method, different behavior ===
        List<PaymentProcessor> processors = List.of(
            new CreditCardProcessor(),
            new PayPalProcessor(),
            new CryptoProcessor()
        );

        System.out.println("=== Processing payments polymorphically ===");
        for (PaymentProcessor p : processors) {
            p.process(99.99); // same call, different behavior
        }

        // === INTERFACE POLYMORPHISM ===
        List<Notifier> notifiers = List.of(
            new EmailNotifier(),
            new SMSNotifier(),
            new PushNotifier()
        );

        System.out.println("\n=== Sending notifications polymorphically ===");
        for (Notifier n : notifiers) {
            n.send("Your payment was processed"); // same call, different impl
        }

        // === DOWNCASTING (dangerous without instanceof) ===
        PaymentProcessor p = new CreditCardProcessor();
        if (p instanceof CreditCardProcessor) {
            CreditCardProcessor cc = (CreditCardProcessor) p;
            cc.chargeCard(50.0); // accessing subclass-specific method
        }

        // === OVERLOADING (compile-time polymorphism) ===
        Calculator calc = new Calculator();
        System.out.println("\n=== Overloading ===");
        System.out.println("add(1, 2) = " + calc.add(1, 2));
        System.out.println("add(1, 2, 3) = " + calc.add(1, 2, 3));
        System.out.println("add(1.5, 2.5) = " + calc.add(1.5, 2.5));
    }
}

class Calculator {
    int add(int a, int b) { return a + b; }
    int add(int a, int b, int c) { return a + b + c; }
    double add(double a, double b) { return a + b; }
}
```

Expected output:
```
=== Processing payments polymorphically ===
[LOG] Processing credit card payment: $99.99
Card charged: $99.99
[LOG] Processing PayPal payment: $99.99
PayPal redirect: $99.99
[LOG] Processing crypto payment: $99.99
Crypto sent: $99.99

=== Sending notifications polymorphically ===
Email: Your payment was processed
SMS: Your payment was processed
Push: Your payment was processed

=== Overloading ===
add(1, 2) = 3
add(1, 2, 3) = 6
add(1.5, 2.5) = 4.0
```

## 4. What Happens Internally

**Runtime method dispatch (vtable):**
When JVM loads a class with virtual methods, it creates a vtable — an array of method pointers:
```
PaymentProcessor vtable:
  [0] → PaymentProcessor.process()
  [1] → PaymentProcessor.log()

CreditCardProcessor vtable:
  [0] → CreditCardProcessor.process()  // overridden
  [1] → PaymentProcessor.log()         // inherited

PayPalProcessor vtable:
  [0] → PayPalProcessor.process()      // overridden
  [1] → PaymentProcessor.log()
```

When `p.process(99.99)` is called:
1. JVM looks at actual object type (`CreditCardProcessor`)
2. Looks up `process()` in `CreditCardProcessor`'s vtable
3. Calls `CreditCardProcessor.process()`

**`invokevirtual` bytecode:**
```java
// Java
p.process(99.99);

// Bytecode
aload_1          // push p (reference)
ldc2_w #2        // push 99.99 (double)
invokevirtual #3 // PaymentProcessor.process(D)V
```
`invokevirtual` performs runtime lookup in vtable based on actual object type.

**Upcasting and downcasting:**
```java
PaymentProcessor p = new CreditCardProcessor(); // upcast — implicit, always safe
CreditCardProcessor cc = (CreditCardProcessor) p; // downcast — explicit, runtime check
```
JVM inserts `checkcast` bytecode for downcast:
```
checkcast CreditCardProcessor
```
If object is not actually `CreditCardProcessor`, throws `ClassCastException`.

**Interface dispatch:**
Interfaces use `invokeinterface` bytecode. JVM maintains interface method table (itable) per class. Slightly slower than `invokevirtual` due to more complex lookup, but negligible in practice.

**Static methods and `final` methods:**
- `static` methods: resolved at compile time via `invokestatic`. No polymorphism.
- `final` methods: cannot be overridden. Resolved at compile time via `invokevirtual` but JVM devirtualizes (no vtable lookup needed).

## 5. Tricky Interview Cases

**Case 1 — Method overriding vs overloading**
```java
class Parent {
    void show() { System.out.println("Parent.show()"); }
}

class Child extends Parent {
    void show(int x) { System.out.println("Child.show(int)"); } // overload, NOT override
}

public class OverrideVsOverload {
    public static void main(String[] args) {
        Parent p = new Child();
        p.show();        // "Parent.show()" — calls superclass method
        // p.show(1);   // compile error: Parent doesn't have show(int)
    }
}
```
Output: `Parent.show()`
Explanation: `show(int)` overloads, does not override `show()`. `p.show()` resolves to `Parent.show()` because there is no overriding `show()` in `Child`. Method signature for override must match exactly (name + parameters + return type).

**Case 2 — Covariant return types**
```java
class Animal { Animal reproduce() { return new Animal(); } }
class Dog extends Animal {
    @Override
    Dog reproduce() { return new Dog(); } // covariant return: Dog is subtype of Animal
}
```
Output: Compiles and runs. `Dog.reproduce()` returns `Dog` instead of `Animal`.
Explanation: Java 5+ allows covariant return types: overriding method can return subtype of original return type.

**Case 3 — Access modifiers in overriding**
```java
class Parent {
    protected void greet() { System.out.println("Hello from Parent"); }
}
class Child extends Parent {
    @Override
    public void greet() { System.out.println("Hello from Child"); } // protected → public OK
}
```
Output: Compiles fine.
Explanation: Overriding method can increase visibility (protected → public) but cannot decrease it (public → protected = compile error).

**Case 4 — `equals()` polymorphism trap**
```java
class Entity {
    int id;
    @Override public boolean equals(Object o) { ... }
}
class User extends Entity {
    String name;
    // Does NOT override equals — inherits from Entity
}
```
Output: `user1.equals(user2)` compares only `id`, not `name`. Symmetry violation if compared with `Entity`.
Explanation: `equals()` contract requires symmetry: `a.equals(b)` == `b.equals(a)`. If `User` does not override `equals`, comparing `Entity` and `User` compares only `Entity` fields.

**Case 5 — Polymorphism with constructors**
```java
class Parent {
    Parent() { show(); } // calls overridable method during construction
    void show() { System.out.println("Parent.show()"); }
}

class Child extends Parent {
    int x = 10;
    @Override void show() { System.out.println("Child.show(), x=" + x); }
}

public class ConstructorPolymorphism {
    public static void main(String[] args) {
        Child c = new Child(); // prints "Child.show(), x=0"
    }
}
```
Output: `Child.show(), x=0`
Explanation: During `Parent` construction, `show()` is called. JVM dispatches to `Child.show()` (dynamic dispatch), but `Child.x` is still `0` (default) because `Child` constructor hasn't run yet. Calling overridable methods from constructors is dangerous.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Overloading when intending to override | Missing `@Override`, different signature | Always use `@Override`; compiler catches errors |
| Downcasting without `instanceof` | `ClassCastException` at runtime | Check `instanceof` before casting |
| LSP violation in subclass | Subclass behavior breaks supertype contract | Honor superclass invariants and postconditions |
| Calling overridable method in constructor | Subclass fields uninitialized, NPE or wrong values | Make method `private`/`final` or call from factory |
| Using inheritance for code reuse | Fragile base class, tight coupling | Prefer composition: delegate to contained object |
| Deep inheritance hierarchies | Hard to maintain, fragile | Prefer flat hierarchies (1-2 levels max) |
| `instanceof` chains | Extensible code required new instanceof checks | Use polymorphic method instead of if-else chains |

## 7. Production Usage

**Strategy pattern (payment processing):**
```java
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardStrategy implements PaymentStrategy {
    private String cardNumber;
    public void pay(double amount) { /* charge card */ }
}

class PayPalStrategy implements PaymentStrategy {
    private String email;
    public void pay(double amount) { /* PayPal */ }
}

class ShoppingCart {
    private List<Item> items;
    private PaymentStrategy strategy; // polymorphic

    public void setPaymentStrategy(PaymentStrategy s) { this.strategy = s; }
    public void checkout(double total) { strategy.pay(total); }
}
```
New payment methods added without modifying `ShoppingCart`.

**Factory pattern with polymorphism:**
```java
abstract class Shape {
    abstract void draw();
}

class Circle extends Shape { void draw() { /* draw circle */ } }
class Rectangle extends Shape { void draw() { /* draw rect */ } }

class ShapeFactory {
    static Shape create(String type) {
        return switch(type) {
            case "circle" → new Circle();
            case "rectangle" → new Rectangle();
            default → throw new IllegalArgumentException();
        };
    }
}

// Usage
List<Shape> shapes = List.of(ShapeFactory.create("circle"), ShapeFactory.create("rectangle"));
for (Shape s : shapes) s.draw(); // polymorphic call
```

**Test doubles (mocking):**
```java
// Production code
interface EmailService { void send(String to, String msg); }

// Test
class MockEmailService implements EmailService {
    private List<String> sent = new ArrayList<>();
    public void send(String to, String msg) { sent.add(to + ": " + msg); }
    public List<String> getSent() { return sent; }
}

// Test
@Test void testUserRegistration() {
    EmailService mock = new MockEmailService();
    UserService service = new UserService(mock);
    service.register("alice@example.com");
    assertEquals(1, mock.getSent().size());
}
```

## 8. Advanced Details

- **`invokevirtual` vs `invokeinterface`:** `invokevirtual` for class methods (vtable lookup). `invokeinterface` for interface methods (itable lookup, slightly slower but JIT inlines).
- **`invokespecial`:** Used for constructors, private methods, and `super.method()` calls. Bypasses vtable, calls exact method.
- **Covariant return types:** Java 5+ allows overriding method to return subtype. Not applicable to primitives. `Integer` is subtype of `Object`, so valid.
- **Bridge methods:** When overriding generic method with raw type, or covariant return with generics, compiler generates synthetic bridge method. Example:
  ```java
  class Node implements Comparable<Node> {
      public int compareTo(Node o) { ... }
  }
  ```
  Compiler generates: `public int compareTo(Object o)` that delegates to `compareTo(Node)`.
- **`@Override` annotation:** Always use. Catches errors where method signature doesn't actually override (parameter type mismatch, return type incompatible).
- **Diamond problem:** Java 8+ allows default methods in interfaces. If class implements two interfaces with same default method, must override to resolve conflict. Use `InterfaceName.super.method()` to disambiguate.
- **Sealed classes (Java 17+):** `sealed` keyword restricts which classes can extend. Provides exhaustive `switch` and controlled polymorphism. Prevents arbitrary subclassing.
- **Pattern matching (Java 21+):** `if (obj instanceof String s)` both checks type and casts. Cleaner downcasting.

## 9. Interview Questions And Answers

### Beginner
Q: What is polymorphism in Java? How does it help in writing flexible code?
A: Polymorphism allows objects of different types to be treated as objects of a common supertype. The same method call behaves differently based on the actual object type at runtime. For example, `PaymentProcessor p = new CreditCardProcessor();` lets us call `p.process()` without knowing whether it's credit card, PayPal, or crypto. This enables writing generic code that works with any subtype.

### Intermediate
Q: What is the difference between compile-time and runtime polymorphism? Give examples.
A: Compile-time polymorphism (overloading): multiple methods with same name but different parameters. Resolved at compile time.
```java
class Calculator {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
}
```
Runtime polymorphism (overriding): subclass provides specific implementation of superclass method. Resolved at runtime via vtable.
```java
class Animal { void speak() { System.out.println("..."); } }
class Dog extends Animal { void speak() { System.out.println("Woof"); } }
```
`Animal a = new Dog(); a.speak();` → prints "Woof" at runtime.

### Senior
Q: You are designing a notification system that supports Email, SMS, Push, and Slack. A junior developer wrote `if-else` chains with `instanceof` checks. Why is this wrong, and how do you redesign using polymorphism?
A: `instanceof` chains violate Open/Closed Principle. Adding a new notification channel (e.g., WhatsApp) requires modifying the existing code. This is fragile and error-prone.

Bad code:
```java
void sendNotification(User u, String msg) {
    if (u instanceof EmailUser e) e.getEmail();
    else if (u instanceof SMSUser s) s.getPhone();
    // Must modify this method for every new channel
}
```

Polymorphic redesign:
```java
interface Notifiable {
    void notify(String message);
}

class EmailUser implements Notifiable {
    public void notify(String msg) { sendEmail(email, msg); }
}

class SMSUser implements Notifiable {
    public void notify(String msg) { sendSMS(phone, msg); }
}

void sendNotification(Notifiable user, String msg) {
    user.notify(msg); // No instanceof needed. New types just implement Notifiable.
}
```

### Tricky
Q: `Parent p = new Child(); p.show();` where `show()` is overridden in `Child`. If `show()` is called from `Parent`'s constructor, which version executes? What if `Child` has instance fields that `show()` uses?
A: `Child.show()` executes because Java uses dynamic dispatch even during construction. However, `Child`'s instance fields are still at default values (`0`, `null`) because `Child`'s constructor hasn't run yet.

```java
class Parent {
    Parent() { show(); } // calls Child.show() dynamically
    void show() { System.out.println("Parent"); }
}

class Child extends Parent {
    int x = 10;
    void show() { System.out.println("Child x=" + x); } // x is 0, not 10!
}
```
`new Child()` prints `Child x=0` because `show()` is called before `x = 10` executes.

Fix: Don't call overridable methods from constructors. Use `private`/`final` methods in constructors, or factory methods.

## 10. Final 30-Second Answer

Polymorphism = same interface, different behavior. **Runtime polymorphism** via method overriding: JVM dispatches to correct implementation using vtable. **Compile-time** via overloading. Achieve through inheritance (extends) or interfaces (implements). Interfaces preferred: loose coupling, multiple inheritance of type. `instanceof` chains = code smell — use polymorphic method. **LSP**: subclass must honor supertype contract. Calling overridable methods in constructors dangerous: subclass fields uninitialized. Use `@Override` always.