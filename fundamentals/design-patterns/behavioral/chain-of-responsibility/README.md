# Chain of Responsibility Pattern

> **Avoids coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chains the receiving objects and passes the request along until one handles it.**

## 📖 Concept

**Real-world analogy:** Customer support — Level 1 handles basic issues. If they can't, they escalate to Level 2. If that fails, to Level 3 (manager).

## 🔍 When to Use

- Multiple handlers can process a request
- Handler should be determined at runtime
- Want to decouple sender and receiver
- More than one object may handle a request
- Want to add/reorder handlers dynamically

## ✅ Interview Checklist

- [ ] Handler interface declares handle method and successor
- [ ] Concrete handlers process or forward request
- [ ] Request travels chain until handled
- [ ] Handlers can be added/removed dynamically
- [ ] Consider using single method vs chain of responsibility

## 🧪 Common Interview Question

**Problem:** Design a Logging system with levels: DEBUG → INFO → ERROR. Each log message should be handled by the appropriate log level.

## 💻 Java Implementation

### 1. Basic Chain

```java
// Handler
abstract class Logger {
    protected int level;
    protected Logger nextLogger;

    public void setNext(Logger nextLogger) {
        this.nextLogger = nextLogger;
    }

    public void logMessage(int level, String message) {
        if (this.level <= level) {
            write(message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }

    protected abstract void write(String message);
}

// Concrete Handlers
class DebugLogger extends Logger {
    public DebugLogger() { this.level = 1; }
    @Override protected void write(String message) {
        System.out.println("[DEBUG] " + message);
    }
}

class InfoLogger extends Logger {
    public InfoLogger() { this.level = 2; }
    @Override protected void write(String message) {
        System.out.println("[INFO] " + message);
    }
}

class ErrorLogger extends Logger {
    public ErrorLogger() { this.level = 3; }
    @Override protected void write(String message) {
        System.out.println("[ERROR] " + message);
    }
}
```

### 2. Usage

```java
public class ChainDemo {
    public static void main(String[] args) {
        Logger debug = new DebugLogger();
        Logger info = new InfoLogger();
        Logger error = new ErrorLogger();

        debug.setNext(info);
        info.setNext(error);

        debug.logMessage(1, "Entering method");    // DEBUG handles
        debug.logMessage(2, "Processing order");    // DEBUG + INFO handle
        debug.logMessage(3, "Order failed!");       // All three handle
    }
}
```

### 3. Full Working Example: ATM Cash Dispenser

```java
// Handler
abstract class CashDispenser {
    protected int denomination;
    protected CashDispenser next;

    public void setNext(CashDispenser next) {
        this.next = next;
    }

    public void dispense(int amount) {
        if (amount >= denomination) {
            int notes = amount / denomination;
            int remainder = amount % denomination;
            System.out.println("Dispensing " + notes + " x ₹" + denomination);
            if (remainder != 0 && next != null) {
                next.dispense(remainder);
            }
        } else if (next != null) {
            next.dispense(amount);
        }
    }
}

// Concrete Handlers
class Rs2000Dispenser extends CashDispenser {
    public Rs2000Dispenser() { this.denomination = 2000; }
}

class Rs500Dispenser extends CashDispenser {
    public Rs500Dispenser() { this.denomination = 500; }
}

class Rs100Dispenser extends CashDispenser {
    public Rs100Dispenser() { this.denomination = 100; }
}

class Rs50Dispenser extends CashDispenser {
    public Rs50Dispenser() { this.denomination = 50; }
}

// Usage
public class ATMDemo {
    public static void main(String[] args) {
        CashDispenser r2000 = new Rs2000Dispenser();
        CashDispenser r500 = new Rs500Dispenser();
        CashDispenser r100 = new Rs100Dispenser();
        CashDispenser r50 = new Rs50Dispenser();

        r2000.setNext(r500);
        r500.setNext(r100);
        r100.setNext(r50);

        r2000.dispense(2750);
        // Output:
        // Dispensing 1 x ₹2000
        // Dispensing 1 x ₹500
        // Dispensing 2 x ₹100
        // Dispensing 1 x ₹50
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Request not handled | Add default handler at end of chain |
| Infinite loop | Ensure chain terminates |
| Long chains | Split into multiple chains if needed |
| Handler order matters | Document and test order |

## 🎯 Related Interview Questions

1. **Design an ATM Cash Dispenser** — ₹2000 → ₹500 → ₹100 → ₹50
2. **Design a Validation pipeline** — Validate → Sanitize → Enrich → Persist
3. **Design a Support Ticket System** — L1 → L2 → L3 escalation

## 🆚 Chain of Responsibility vs Decorator

| Aspect | Chain of Responsibility | Decorator |
|--------|------------------------|-----------|
| Request handling | One handler processes it | All decorators process |
| Purpose | Find handler for request | Add behavior at each layer |
| Flow | Stops when handled | Continues through all layers |
| Example | Logging levels, ATM | I/O streams, toppings |