# Encapsulation, Abstraction, abstract vs interface, final keyword

## 1. Why This Concept Matters

**Encapsulation** and **abstraction** are two of the four OOP pillars (with inheritance and polymorphism). Encapsulation hides internal state and requires all interaction through public methods. Abstraction hides implementation complexity behind a simplified interface. The `abstract` class vs `interface` decision is one of the most common Java design questions — when to use each, and how Java 8+ default/static methods changed the landscape. The `final` keyword has different meanings for variables (immutable reference), methods (cannot be overridden), and classes (cannot be extended). Interviewers test these together because they build on each other.

Misunderstanding causes:
- Breaking encapsulation by exposing internal fields directly
- Using abstract classes when interfaces are more appropriate (or vice versa)
- Not understanding that final on a variable means the REFERENCE can't change, but the OBJECT can be mutated
- Confusing abstraction (hiding complexity) with encapsulation (hiding data)

## 2. Encapsulation

**Definition:** Bundling data (fields) and methods that operate on that data within a single unit (class), and restricting direct access to internal data.

**Achieved via:** `private` fields, `public` getter/setter methods.

```java
// BAD — no encapsulation
class BadBankAccount {
    public double balance;  // Anyone can set balance to anything!
}

// GOOD — proper encapsulation
class BankAccount {
    private double balance;  // Cannot be accessed directly
    
    public double getBalance() { return balance; }
    
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.balance += amount;
    }
    
    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > balance) throw new InsufficientFundsException("Insufficient balance");
        this.balance -= amount;
    }
}
// Advantage: validation logic is centralized. Can add logging, auditing, or change calculation later.
```

## 3. Abstraction

**Definition:** Hiding implementation details and showing only essential features. You know WHAT an object does, not HOW it does it.

**Achieved via:** abstract classes and interfaces.

```java
// Abstraction: you only know WHAT it does
interface PaymentGateway {
    PaymentResult charge(String cardToken, BigDecimal amount);
    PaymentResult refund(String transactionId);
}

// Implementation is hidden
class StripeGateway implements PaymentGateway {
    @Override
    public PaymentResult charge(String cardToken, BigDecimal amount) {
        // Complex API call, retry logic, error handling — all hidden
        return stripeClient.charge(cardToken, amount);
    }
}
// Caller doesn't know if it's Stripe, PayPal, or Square — only knows PaymentGateway interface
```

**Encapsulation vs Abstraction:**
- **Encapsulation** = hiding DATA (private fields, getters/setters)
- **Abstraction** = hiding IMPLEMENTATION (interfaces, abstract classes)
- Encapsulation is about HOW to protect data. Abstraction is about WHAT to expose.

## 4. abstract class vs interface

| Feature | abstract class | interface (Java 8+) | interface (Java 9+) |
|---------|---------------|-------------------|-------------------|
| Instance fields | ✅ Any fields | ❌ Only `public static final` constants | Same |
| Constructors | ✅ Can have | ❌ Cannot have | Same |
| Method types | abstract + concrete | abstract + `default` + `static` | + `private` methods |
| Multiple inheritance | ❌ Class can extend only ONE | ✅ Class can implement MANY | Same |
| Access modifiers | Any | All methods `public` | `private` helper methods allowed |
| `final` methods | ✅ Can have final methods | ❌ Cannot have final methods | Same |
| When to use | "IS-A" relationship + shared state/code | "CAPABILITY" contract + multiple inheritance needed | Same |

**Rule of thumb:**
- Use **interface** for: capabilities that unrelated classes can implement (`Serializable`, `Comparable`, `Runnable`), multiple inheritance needed, pure contract
- Use **abstract class** for: related classes sharing state/behavior, base implementation with some default logic, when some methods should be `final`

**Java 8+ default methods:** Interfaces can have method implementations. Used to add methods without breaking existing implementations.
```java
interface Vehicle {
    void start();                    // Abstract — must implement
    default void honk() {            // Default — optional override
        System.out.println("Beep!");
    }
    static boolean isRoadLegal() {   // Static — utility
        return true;
    }
}
```

## 5. The `final` keyword

| Usage | Effect |
|-------|--------|
| `final int x = 5;` | Variable can only be assigned ONCE. For primitives: value fixed. For objects: reference fixed (but object content can change!). |
| `final StringBuilder sb = new StringBuilder();` | `sb` always points to the SAME StringBuilder. But you CAN call `sb.append("more")`. |
| `final void myMethod()` | Method CANNOT be overridden by subclasses. Used in Template Method pattern. |
| `final class MyClass` | Class CANNOT be extended. `String`, `Integer`, `Math` are final. |

```java
public class FinalDemo {
    private final int id;  // Can be set in constructor — blank final
    
    public FinalDemo(int id) { this.id = id; } // OK — assigned in constructor
    
    public void modify() {
        // id = 5;  // COMPILE ERROR! Cannot reassign final field
    }
    
    public void objectFinal() {
        final List<String> list = new ArrayList<>();
        list.add("hello");      // OK — modifying object content
        // list = new ArrayList<>();  // COMPILE ERROR! Cannot reassign reference
    }
}
```

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Public fields instead of private + getters | No validation, can't change internal representation | Encapsulate with private + getter/setter |
| Using abstract class when interface suffices | Can't extend another class (single inheritance) | Prefer interfaces for pure contracts |
| Interface with many methods (fat interface) | All implementors must implement all methods (ISP violation) | Split into smaller interfaces |
| `final` on object = object can't change | FALSE — only the reference can't change, object is mutable | Use immutable class (no setters) for truly immutable objects |
| Overriding `final` method | Compile error | Don't — design intention is to prevent overriding |

## 7. Final 30-Second Answer

**Encapsulation**: private fields + public methods (hide data). **Abstraction**: interfaces/abstract classes (hide implementation). **Interface**: capability contract, multiple inheritance, Java 8+ default/static methods. **Abstract class**: shared state/behavior, IS-A relationship, single inheritance. **final**: variable (can't reassign), method (can't override), class (can't extend). Final on object reference ≠ object immutability — only the pointer is fixed.