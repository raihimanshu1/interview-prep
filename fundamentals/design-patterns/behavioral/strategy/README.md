# Strategy Pattern

> **Defines a family of algorithms, encapsulates each one, and makes them interchangeable. Lets the algorithm vary independently from clients that use it.**

## 📖 Concept

**Real-world analogy:** Navigation app — you can choose Car, Bike, or Walking route. The algorithm changes, but the app's interface remains the same.

## 🔍 When to Use

- Multiple algorithms for the same task (sorting, payment, compression)
- Need to switch algorithms at runtime
- Avoid conditional statements (if-else/switch) for algorithm selection
- Want to follow Open/Closed Principle

## ✅ Interview Checklist

- [ ] Strategy interface declares algorithm method
- [ ] Concrete Strategies implement the interface
- [ ] Context holds reference to Strategy
- [ ] Client sets/chooses strategy at runtime
- [ ] Client depends only on Strategy interface

## 🧪 Common Interview Question

**Problem:** Design a Payment Processing system where users can choose payment method at checkout. Available methods: Credit Card, PayPal, UPI.

## 💻 Java Implementation

### 1. Basic Strategy

```java
// Strategy Interface
interface PaymentStrategy {
    void pay(double amount);
}

// Concrete Strategies
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    @Override
    public void pay(double amount) {
        System.out.println("Paid via Credit Card: ₹" + amount);
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;
    public PayPalPayment(String email) { this.email = email; }
    @Override
    public void pay(double amount) {
        System.out.println("Paid via PayPal: ₹" + amount);
    }
}

class UPIPayment implements PaymentStrategy {
    private String upiId;
    public UPIPayment(String upiId) { this.upiId = upiId; }
    @Override
    public void pay(double amount) {
        System.out.println("Paid via UPI: ₹" + amount);
    }
}

// Context
class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    public void checkout(double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment method not selected");
        }
        paymentStrategy.pay(amount);
    }
}
```

### 2. Usage

```java
public class StrategyDemo {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.setPaymentStrategy(new CreditCardPayment("1234-5678"));
        cart.checkout(1500.0);
        cart.setPaymentStrategy(new UPIPayment("user@upi"));
        cart.checkout(500.0);
    }
}
```

### 3. Full Working Example: Sorting Strategies

```java
// Strategy
interface SortStrategy {
    void sort(int[] arr);
}

// Concrete Strategies
class BubbleSort implements SortStrategy {
    @Override
    public void sort(int[] arr) {
        System.out.println("Sorting using Bubble Sort");
        // implementation
    }
}

class QuickSort implements SortStrategy {
    @Override
    public void sort(int[] arr) {
        System.out.println("Sorting using Quick Sort");
        // implementation
    }
}

class MergeSort implements SortStrategy {
    @Override
    public void sort(int[] arr) {
        System.out.println("Sorting using Merge Sort");
        // implementation
    }
}

// Context
class SortExecutor {
    private SortStrategy strategy;
    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }
    public void executeSort(int[] arr) {
        if (strategy == null) throw new IllegalStateException("Strategy not set");
        strategy.sort(arr);
    }
}

// Usage
public class SortDemo {
    public static void main(String[] args) {
        int[] data = {5, 2, 9, 1, 5};
        SortExecutor executor = new SortExecutor();
        // Small array → Bubble
        executor.setStrategy(new BubbleSort());
        executor.executeSort(data);
        // Large array → Quick
        executor.setStrategy(new QuickSort());
        executor.executeSort(data);
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Too many strategy classes | Group similar strategies or use lambda expressions |
| Strategy selection scattered | Use factory or strategy pattern together |
| Client knows all strategies | Client should only know Strategy interface |
| Duplicate code between strategies | Extract common logic to abstract class |

## 🎯 Related Interview Questions

1. **Design a Sorting system** — BubbleSort, QuickSort, MergeSort interchangeable
2. **Design a Compression system** — ZIP, RAR, GZIP strategies
3. **Difference between Strategy and State?** — Strategy: client chooses; State: object changes automatically
4. **How does Strategy relate to Dependency Injection?** — Strategy is DI at its core

## 🆚 Strategy vs State

| Aspect | Strategy | State |
|--------|----------|-------|
| Selection | Client chooses | Object transitions automatically |
| Purpose | Interchangeable algorithms | Behavior changes with state |
| Switching | Done by client | Done by state object |
| Example | Payment method selection | Vending machine states |