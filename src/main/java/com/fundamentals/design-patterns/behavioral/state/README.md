# State Pattern

> **Allows an object to alter its behavior when its internal state changes. The object will appear to change its class.**

## 📖 Concept

**Real-world analogy:** A vending machine — when it has coins (HasMoney state), it accepts selections. When empty (NoMoney state), it only accepts coins. Behavior changes with state.

## 🔍 When to Use

- Object behavior depends on its state and changes at runtime
- Large conditional statements (if-else/switch) based on state
- State-specific behaviors should be in separate classes
- Want to avoid state-dependent conditionals

## ✅ Interview Checklist

- [ ] State interface declares behavior methods
- [ ] Concrete States implement behavior for specific state
- [ ] Context maintains current state reference
- [ ] State transitions happen inside state objects
- [ ] States can transition to other states

## 🧪 Common Interview Question

**Problem:** Design a Vending Machine with states: Idle, HasMoney, Dispensing. Each state has different behavior for insertMoney(), selectProduct(), dispense().

## 💻 Java Implementation

### 1. Basic State

```java
// State Interface
interface State {
    void insertMoney(int amount);
    void selectProduct();
    void dispense();
}

// Context
class VendingMachine {
    private State idleState;
    private State hasMoneyState;
    private State dispenseState;
    private State currentState;
    private int inventory = 5;
    private int money = 0;

    public VendingMachine() {
        idleState = new IdleState(this);
        hasMoneyState = new HasMoneyState(this);
        dispenseState = new DispenseState(this);
        currentState = idleState;
    }

    public void setState(State state) { this.currentState = state; }

    public void insertMoney(int amount) { currentState.insertMoney(amount); }
    public void selectProduct() { currentState.selectProduct(); }
    public void dispense() { currentState.dispense(); }
}

// Concrete States
class IdleState implements State {
    private VendingMachine machine;
    public IdleState(VendingMachine machine) { this.machine = machine; }
    @Override public void insertMoney(int amount) {
        machine.setMoney(amount);
        System.out.println("Inserted ₹" + amount);
        machine.setState(machine.getHasMoneyState());
    }
    @Override public void selectProduct() {
        System.out.println("Insert money first!");
    }
    @Override public void dispense() {
        System.out.println("Insert money and select product first!");
    }
}

class HasMoneyState implements State {
    private VendingMachine machine;
    public HasMoneyState(VendingMachine machine) { this.machine = machine; }
    @Override
    public void insertMoney(int amount) {
        machine.setMoney(machine.getMoney() + amount);
        System.out.println("Added ₹" + amount + ". Total: ₹" + machine.getMoney());
    }
    @Override
    public void selectProduct() {
        if (machine.getInventory() > 0) {
            System.out.println("Product selected!");
            machine.setState(machine.getDispenseState());
        } else {
            System.out.println("Out of stock!");
            machine.setMoney(0);
            machine.setState(machine.getIdleState());
        }
    }
    @Override public void dispense() {
        System.out.println("Select product first!");
    }
}

class DispenseState implements State {
    private VendingMachine machine;
    public DispenseState(VendingMachine machine) { this.machine = machine; }
    @Override
    public void dispense() {
        if (machine.getInventory() > 0) {
            machine.setInventory(machine.getInventory() - 1);
            System.out.println("Product dispensed! Remaining: " + machine.getInventory());
            machine.setMoney(0);
            machine.setState(machine.getIdleState());
        }
    }
    @Override public void insertMoney(int amount) {
        System.out.println("Please wait, dispensing");
    }
    @Override public void selectProduct() {
        System.out.println("Already selected, please collect");
    }
}
```

### 2. Usage

```java
public class StateDemo {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine();
        machine.insertMoney(50);
        machine.selectProduct();
        machine.dispense();
        // Try without money:
        new VendingMachine().selectProduct(); // "Insert money first!"
    }
}
```

### 3. Full Working Example: Order Lifecycle

```java
// State Interface
interface OrderState {
    void process(Order order);
    void ship(Order order);
    void deliver(Order order);
    void cancel(Order order);
}

// Context
class Order {
    private OrderState currentState;
    private String orderId;
    private double amount;

    public Order(String orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.currentState = new NewOrderState();
    }

    public void setState(OrderState state) {
        this.currentState = state;
    }

    public void process() { currentState.process(this); }
    public void ship() { currentState.ship(this); }
    public void deliver() { currentState.deliver(this); }
    public void cancel() { currentState.cancel(this); }

    @Override
    public String toString() {
        return "Order " + orderId + " (₹" + amount + ")";
    }
}

// Concrete States
class NewOrderState implements OrderState {
    @Override
    public void process(Order order) {
        System.out.println(order + " → Processing payment");
        order.setState(new ProcessingState());
    }
    @Override public void ship(Order order) {
        System.out.println("Cannot ship: order not processed");
    }
    @Override public void deliver(Order order) {
        System.out.println("Cannot deliver: order not shipped");
    }
    @Override public void cancel(Order order) {
        System.out.println(order + " → Cancelled before processing");
        order.setState(new CancelledState());
    }
}

class ProcessingState implements OrderState {
    @Override
    public void process(Order order) {
        System.out.println("Already processing");
    }
    @Override
    public void ship(Order order) {
        System.out.println(order + " → Shipped");
        order.setState(new ShippedState());
    }
    @Override public void deliver(Order order) {
        System.out.println("Cannot deliver: order not shipped");
    }
    @Override public void cancel(Order order) {
        System.out.println(order + " → Cancelled during processing");
        order.setState(new CancelledState());
    }
}

class ShippedState implements OrderState {
    @Override
    public void process(Order order) {
        System.out.println("Already processed and shipped");
    }
    @Override public void ship(Order order) {
        System.out.println("Already shipped");
    }
    @Override
    public void deliver(Order order) {
        System.out.println(order + " → Delivered");
        order.setState(new DeliveredState());
    }
    @Override public void cancel(Order order) {
        System.out.println("Cannot cancel: already shipped");
    }
}

class DeliveredState implements OrderState {
    @Override public void process(Order order) {
        System.out.println("Order already delivered");
    }
    @Override public void ship(Order order) {
        System.out.println("Order already delivered");
    }
    @Override public void deliver(Order order) {
        System.out.println("Already delivered");
    }
    @Override public void cancel(Order order) {
        System.out.println("Cannot cancel: already delivered");
    }
}

class CancelledState implements OrderState {
    @Override public void process(Order order) { System.out.println("Order cancelled"); }
    @Override public void ship(Order order) { System.out.println("Order cancelled"); }
    @Override public void deliver(Order order) { System.out.println("Order cancelled"); }
    @Override public void cancel(Order order) { System.out.println("Already cancelled"); }
}

// Usage
public class OrderDemo {
    public static void main(String[] args) {
        Order order = new Order("ORD-123", 1500);
        order.process();
        order.ship();
        order.deliver();
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Too many states | Combine related states or use state pattern only when beneficial |
| God state object | Keep each state focused |
| State explosion | Consider using enum + strategy instead |
| Circular transitions | Use state machine diagram to validate |

## 🎯 Related Interview Questions

1. **Design an Order Lifecycle** — NEW → PAID → SHIPPED → DELIVERED
2. **Design a Media Player** — Playing, Paused, Stopped states
3. **Design a Traffic Light system** — Red, Green, Yellow transitions

## 🆚 State vs Strategy

| Aspect | State | Strategy |
|--------|-------|----------|
| Selection | Automatic based on state | Client chooses |
| Transition | State changes itself | Client changes strategy |
| Purpose | Behavior changes with state | Interchangeable algorithms |
| Example | Vending machine, Order lifecycle | Payment, Sorting |