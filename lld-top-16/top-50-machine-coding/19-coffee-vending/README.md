# ☕ Problem 19: Coffee Vending Machine

> **Difficulty**: ⭐⭐ | **Company Fit**: Starbucks, Nestlé, Keurig  
> **Est. Time**: 60 min | **Patterns**: State, Factory, Strategy

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a vending machine for coffee."

**What the interviewer tests**:
```
1. Can you model a finite state machine? (States: Idle, Selecting, Preparing, Dispensing, etc.)
2. Can you handle multiple payment methods? (Cash, Card, App)
3. Can you track inventory? (Coffee types, sugar, milk, cups)
4. Can you handle failure gracefully? (Out of coffee, payment fails, machine jams)
```

### Step 2: The "Aha!" Moment

The key insight: **A vending machine is a STATE MACHINE.**

```
[Idle] --select--> [Payment] --pay success--> [Preparing] 
                                           --> [Dispensing] 
                                           --> [Complete] --> back to [Idle]

At any state, certain inputs are valid, others are invalid.
If you press "dispense" while in [Idle], ignore it.
If payment fails, roll back to [Idle] without preparing.
```

Each state has:
- **Entry actions** (what happens when you ENTER this state)
- **Exit actions** (what happens when you LEAVE this state)  
- **Transitions** (what moves you to NEXT state)
- **Valid inputs** (what buttons work in this state)

### Step 3: How to make it extensible?

```
New beverage type? → Just add to `BeverageType` enum.
New payment method? → Implement PaymentStrategy and register.
New recipe? → Add a Recipe object.
```

---

## 💻 Core Implementation

```java
package com.vending;

/**
 * INTUITION: The state enum captures ALL possible machine states.
 * 
 * Why not booleans like isIdle, isPreparing, etc.?
 * - Multiple booleans can be true simultaneously (bug!)
 * - State enum ensures ONLY ONE state at a time
 * - Easy to validate: "Are we allowed to select while in state X?"
 */
public enum MachineState {
    IDLE,              // Waiting for user
    SELECTING,         // User pressed buttons to select beverage
    PAYMENT_PENDING,   // Waiting for payment
    PREPARING,         // Beverage is being made
    DISPENSING,        // Beverage is dropping into cup
    MAINTENANCE,       // Out of order/being refilled
    ERROR              // Something went wrong
}
```

```java
package com.vending;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * INTUITION: The VendingMachine is the central controller.
 * It's a Singleton because there's ONE physical machine.
 * 
 * Thread-safety: Multiple people could press buttons at the same time.
 * We need synchronized access to state transitions.
 */
public class VendingMachine {
    
    private static volatile VendingMachine instance;
    
    private MachineState currentState;
    private Beverage selectedBeverage;
    private PaymentProcessor paymentProcessor;
    private final Map<BeverageType, Integer> inventory = new HashMap<>();
    private final Map<PaymentMethod, PaymentStrategy> paymentStrategies = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Transaction> transactionHistory = new CopyOnWriteArrayList<>();

    private VendingMachine() {
        this.currentState = MachineState.IDLE;
        this.paymentProcessor = new PaymentProcessor();
        initializeInventory();
        initializePaymentMethods();
    }

    public static VendingMachine getInstance() {
        if (instance == null) {
            synchronized (VendingMachine.class) {
                if (instance == null) {
                    instance = new VendingMachine();
                }
            }
        }
        return instance;
    }

    /**
     * INTUITION: User selects a beverage.
     * 
     * Valid from: IDLE
     * Transitions to: SELECTING (if valid), ERROR (if unavailable)
     */
    public synchronized Beverage selectBeverage(BeverageType type) {
        lock.lock();
        try {
            if (currentState != MachineState.IDLE) {
                throw new IllegalStateException("Cannot select in state: " + currentState);
            }
            
            if (!isAvailable(type)) {
                currentState = MachineState.ERROR;
                throw new NoSuchElementException(type + " not available");
            }
            
            selectedBeverage = new Beverage(type, getRecipe(type));
            currentState = MachineState.SELECTING;
            
            System.out.println("Selected: " + type + " - $" + selectedBeverage.getPrice());
            return selectedBeverage;
            
        } finally {
            lock.unlock();
        }
    }

    /**
     * INTUITION: User inserts payment.
     * 
     * Valid from: SELECTING
     * Transitions to: PAYMENT_PENDING
     */
    public synchronized PaymentResult makePayment(PaymentMethod method, double amount) {
        lock.lock();
        try {
            if (currentState != MachineState.SELECTING) {
                throw new IllegalStateException("No beverage selected");
            }
            
            currentState = MachineState.PAYMENT_PENDING;
            
            PaymentStrategy strategy = paymentStrategies.get(method);
            if (strategy == null) {
                throw new IllegalArgumentException("Unsupported payment: " + method);
            }
            
            boolean success = strategy.pay(amount, selectedBeverage.getPrice());
            
            if (success) {
                currentState = MachineState.PREPARING;
                // Start beverage preparation (async in real machine)
                System.out.println("Preparing " + selectedBeverage.getType() + "...");
                currentState = MachineState.DISPENSING;
                System.out.println("Dispensing...");
                
                // Deduct inventory
                deductInventory(selectedBeverage.getType());
                
                // Record transaction
                transactionHistory.add(new Transaction(
                    selectedBeverage.getType(), selectedBeverage.getPrice(), method
                ));
                
                currentState = MachineState.IDLE;
                Beverage result = selectedBeverage;
                selectedBeverage = null;
                return PaymentResult.success(result);
            } else {
                currentState = MachineState.IDLE;
                selectedBeverage = null;
                return PaymentResult.failure("Payment declined");
            }
            
        } finally {
            lock.unlock();
        }
    }

    public synchronized void refill(BeverageType type, int quantity) {
        lock.lock();
        try {
            if (currentState == MachineState.DISPENSING) {
                throw new IllegalStateException("Cannot refill while dispensing");
            }
            inventory.merge(type, quantity, Integer::sum);
            if (currentState == MachineState.ERROR) {
                currentState = MachineState.IDLE;
            }
        } finally {
            lock.unlock();
        }
    }

    // --- Helpers ---

    private boolean isAvailable(BeverageType type) {
        return inventory.getOrDefault(type, 0) > 0;
    }

    private void deductInventory(BeverageType type) {
        inventory.computeIfPresent(type, (k, v) -> v - 1);
    }

    private void initializeInventory() {
        // Start fully stocked
        for (BeverageType type : BeverageType.values()) {
            inventory.put(type, 50);
        }
    }

    private void initializePaymentMethods() {
        paymentStrategies.put(PaymentMethod.CASH, new CashPaymentStrategy());
        paymentStrategies.put(PaymentMethod.CARD, new CardPaymentStrategy());
        paymentStrategies.put(PaymentMethod.APP, new AppPaymentStrategy());
    }

    private Recipe getRecipe(BeverageType type) {
        switch (type) {
            case ESPRESSO: return new Recipe.Builder()
                .coffee(30).water(60).milk(0).sugar(0).build();
            case LATTE: return new Recipe.Builder()
                .coffee(30).water(60).milk(120).sugar(10).build();
            case CAPPUCCINO: return new Recipe.Builder()
                .coffee(30).water(60).milk(90).sugar(10).build();
            default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}
```

```java
package com.vending;

/**
 * Strategy Pattern for payments.
 */
public interface PaymentStrategy {
    boolean pay(double amount, double price);
}

class CashPaymentStrategy implements PaymentStrategy {
    // Real machine would interface with a bill acceptor
    @Override
    public boolean pay(double cashInserted, double price) {
        return cashInserted >= price;
    }
}

class CardPaymentStrategy implements PaymentStrategy {
    // Real machine would call payment gateway
    @Override
    public boolean pay(double dummy, double price) {
        // Simulate success
        return true;
    }
}

class AppPaymentStrategy implements PaymentStrategy {
    // Real machine would show QR code, poll for confirmation
    @Override
    public boolean pay(double dummy, double price) {
        // Simulate app notification
        return true;
    }
}
```

```java
package com.vending;

import java.util.Map;

/**
 * Recipe - how to make each beverage.
 * Uses Builder for flexible construction with optional defaults.
 */
public class Recipe {
    private final int coffee;      // mg of coffee
    private final int water;       // ml of water
    private final int milk;        // ml of milk
    private final int sugar;       // grams of sugar
    private final int temperature; // Celsius
    private final int size;        // ml cup size

    private Recipe(Builder builder) {
        this.coffee = builder.coffee;
        this.water = builder.water;
        this.milk = builder.milk;
        this.sugar = builder.sugar;
        this.temperature = builder.temperature;
        this.size = builder.size;
    }

    // Getters...

    /**
     * Builder pattern: allows optional parameters with sensible defaults.
     */
    public static class Builder {
        private int coffee = 0;
        private int water = 0;
        private int milk = 0;
        private int sugar = 0;
        private int temperature = 80;  // default hot
        private int size = 180;        // default cup size

        public Builder coffee(int coffee) { this.coffee = coffee; return this; }
        public Builder water(int water) { this.water = water; return this; }
        public Builder milk(int milk) { this.milk = milk; return this; }
        public Builder sugar(int sugar) { this.sugar = sugar; return this; }
        public Builder temperature(int temp) { this.temperature = temp; return this; }
        public Builder size(int size) { this.size = size; return this; }

        public Recipe build() {
            return new Recipe(this);
        }
    }
}

public enum BeverageType {
    COFFEE, ESPRESSO, LATTE, CAPPUCCINO, TEA, HOT_CHOCOLATE;

    public double getPrice() {
        switch (this) {
            case COFFEE: return 2.50;
            case ESPRESSO: return 3.00;
            case LATTE: return 4.00;
            case CAPPUCCINO: return 3.50;
            case TEA: return 2.00;
            case HOT_CHOCOLATE: return 3.50;
            default: throw new IllegalArgumentException();
        }
    }
}
```

```java
package com.vending;

public class PaymentResult {
    private final boolean success;
    private final Beverage beverage;
    private final String errorMessage;

    private PaymentResult(boolean success, Beverage beverage, String errorMessage) {
        this.success = success;
        this.beverage = beverage;
        this.errorMessage = errorMessage;
    }

    public static PaymentResult success(Beverage b) {
        return new PaymentResult(true, b, null);
    }

    public static PaymentResult failure(String msg) {
        return new PaymentResult(false, null, msg);
    }

    public boolean isSuccess() { return success; }
    public Beverage getBeverage() { return beverage; }
    public String getErrorMessage() { return errorMessage; }
}

public enum PaymentMethod { CASH, CARD, APP }

class Transaction {
    private final String id;
    private final BeverageType beverage;
    private final double amount;
    private final PaymentMethod method;
    private final LocalDateTime timestamp;

    public Transaction(BeverageType beverage, double amount, PaymentMethod method) {
        this.id = UUID.randomUUID().toString();
        this.beverage = beverage;
        this.amount = amount;
        this.method = method;
        this.timestamp = LocalDateTime.now();
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle when machine runs out of stock?"
> "Each BeverageType has an inventory count. In `selectBeverage()`, check count > 0. If 0, transition to ERROR state with message 'Sold Out'. Refill via `refill()` method which increments count."

### Q2: "What if network payment gateway is down?"
> "Retry 3 times with exponential backoff. If still down, suggest alternative payment method. Queue transaction locally, reconcile when network returns."

### Q3: "How to support custom orders (no sugar, extra shot)?"
> "Add `CustomizationOptions` to Beverage. When selecting, user can override defaults. Recipe is built from template + overrides."

### Q4: "How to schedule maintenance?"
> "Add a `lastCleaned` timestamp. Periodic timer checks if cleaning needed (after 500 beverages). Auto-transition to MAINTENANCE state, alert staff via notification."

### Q5: "How to prevent same drink being dispensed twice for one payment?"
> "Transaction ID stored in PaymentStrategy. If same ID retried, return cached result. Idempotency key prevents double-charging."