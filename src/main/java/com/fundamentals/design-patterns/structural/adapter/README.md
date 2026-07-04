# Adapter Pattern

> **Allows incompatible interfaces to work together. It acts as a wrapper between two interfaces.**

## 📖 Concept

**Real-world analogy:** A power plug adapter — you have a US plug but a European socket. The adapter converts one to the other.

## 🔍 When to Use

- Existing class has wrong interface for your needs
- Want to reuse legacy code with new system
- Third-party library incompatible with your code
- Need to integrate classes that cannot be modified

## ✅ Interview Checklist

- [ ] Target interface — what client expects
- [ ] Adaptee — existing incompatible class
- [ ] Adapter implements Target, wraps Adaptee
- [ ] Client uses Target interface, unaware of Adaptee
- [ ] Prefer composition over inheritance

## 🧪 Common Interview Question

**Problem:** You have a legacy `XMLProcessor` that processes XML. Your new system works with JSON. Create an adapter so XML data can work with the new JSON-based system.

## 💻 Java Implementation

### 1. Basic Adapter

```java
// Target Interface (what client expects)
interface JSONProcessor {
    void processJSON(String json);
}

// Adaptee (existing incompatible class)
class XMLProcessor {
    public void processXML(String xml) {
        System.out.println("Processing XML: " + xml);
    }
}

// Adapter
class XMLToJSONAdapter implements JSONProcessor {
    private XMLProcessor xmlProcessor;

    public XMLToJSONAdapter(XMLProcessor xmlProcessor) {
        this.xmlProcessor = xmlProcessor;
    }

    @Override
    public void processJSON(String json) {
        // Convert JSON to XML (simplified)
        String xml = convertJSONtoXML(json);
        xmlProcessor.processXML(xml);
    }

    private String convertJSONtoXML(String json) {
        return "<data>" + json + "</data>";
    }
}
```

### 2. Usage

```java
public class AdapterDemo {
    public static void main(String[] args) {
        JSONProcessor processor = new XMLToJSONAdapter(new XMLProcessor());
        processor.processJSON("{\"name\": \"John\"}");
        // Output: Processing XML: <data>{"name": "John"}</data>
    }
}
```

### 3. Full Working Example: Payment Gateway Adapter

```java
// Target Interface
interface PaymentProcessor {
    void processPayment(double amount);
    void refund(String transactionId);
}

// Adaptee 1: Legacy payment system
class LegacyPaymentGateway {
    public void makePayment(double amt) {
        System.out.println("Legacy: Payment of ₹" + amt + " processed");
    }

    public void returnPayment(String txnId) {
        System.out.println("Legacy: Refund for " + txnId + " processed");
    }
}

// Adaptee 2: Modern payment SDK
class ModernPaymentSDK {
    public void charge(double amount) {
        System.out.println("Modern SDK: Charged ₹" + amount);
    }

    public void reversal(String id) {
        System.out.println("Modern SDK: Reversal for " + id);
    }
}

// Adapter for Legacy
class LegacyPaymentAdapter implements PaymentProcessor {
    private LegacyPaymentGateway gateway;

    public LegacyPaymentAdapter(LegacyPaymentGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void processPayment(double amount) {
        gateway.makePayment(amount);
    }

    @Override
    public void refund(String transactionId) {
        gateway.returnPayment(transactionId);
    }
}

// Adapter for Modern SDK
class ModernPaymentAdapter implements PaymentProcessor {
    private ModernPaymentSDK sdk;

    public ModernPaymentAdapter(ModernPaymentSDK sdk) {
        this.sdk = sdk;
    }

    @Override
    public void processPayment(double amount) {
        sdk.charge(amount);
    }

    @Override
    public void refund(String transactionId) {
        sdk.reversal(transactionId);
    }
}

// Client code
class CheckoutService {
    private PaymentProcessor processor;

    public CheckoutService(PaymentProcessor processor) {
        this.processor = processor;
    }

    public void checkout(double amount) {
        processor.processPayment(amount);
    }

    public void refund(String txnId) {
        processor.refund(txnId);
    }
}

public class PaymentDemo {
    public static void main(String[] args) {
        // Use legacy gateway via adapter
        CheckoutService legacyCheckout = new CheckoutService(
            new LegacyPaymentAdapter(new LegacyPaymentGateway())
        );
        legacyCheckout.checkout(1000);

        // Use modern SDK via adapter
        CheckoutService modernCheckout = new CheckoutService(
            new ModernPaymentAdapter(new ModernPaymentSDK())
        );
        modernCheckout.checkout(2000);
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Adapter becomes too complex | Keep single responsibility — one adapter per adaptee |
| Multiple adapters for same adaptee | Create one reusable adapter |
| Over-adapting simple cases | Sometimes it's better to refactor the adaptee directly |

## 🎯 Related Interview Questions

1. **Design a payment adapter** — Integrate legacy `OldPaymentGateway` with new `PaymentProcessor` interface
2. **Design a Logger adapter** — Adapt `Log4j` to `SLF4J` interface
3. **Class vs Object Adapter** — Class: inheritance; Object: composition (preferred)

## 🆚 Class Adapter vs Object Adapter

| Aspect | Class Adapter | Object Adapter |
|--------|--------------|----------------|
| Mechanism | Multiple inheritance | Composition |
| Flexibility | Less flexible | More flexible |
| Java support | Not supported (no multiple inheritance) | Preferred approach |
| Adapts | Both interface and implementation | Only interface |