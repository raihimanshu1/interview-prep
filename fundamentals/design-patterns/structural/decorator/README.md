# Decorator Pattern

> **Attaches additional responsibilities to an object dynamically. Provides a flexible alternative to subclassing for extending functionality.**

## 📖 Concept

**Real-world analogy:** Pizza toppings — you start with a base pizza, then add cheese, pepperoni, olives dynamically. Each topping decorates the pizza without changing the base.

## 🔍 When to Use

- Need to add behaviors to individual objects, not all objects of a class
- Behaviors should be removable/addable at runtime
- Subclass explosion (too many classes for combinations)
- Want to follow Open/Closed Principle

## ✅ Interview Checklist

- [ ] Component interface defines core behavior
- [ ] Concrete Component is base implementation
- [ ] Abstract Decorator implements Component, wraps Component
- [ ] Concrete Decorators override methods and delegate to wrapped component
- [ ] Decorators can be stacked (wrap decorators with decorators)

## 🧪 Common Interview Question

**Problem:** Design a Pizza billing system. Base pizza costs ₹100. Add toppings: Cheese (+₹30), Pepperoni (+50), Olives (+₹20). Each topping can be added/removed. Calculate total dynamically.

## 💻 Java Implementation

### 1. Basic Decorator

```java
// Component Interface
interface Pizza {
    String getDescription();
    double getCost();
}

// Concrete Component
class BasePizza implements Pizza {
    @Override
    public String getDescription() {
        return "Base Pizza";
    }

    @Override
    public double getCost() {
        return 100.0; // base price ₹100
    }
}

// Abstract Decorator
abstract class ToppingDecorator implements Pizza {
    protected Pizza pizza;

    public ToppingDecorator(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription();
    }

    @Override
    public double getCost() {
        return pizza.getCost();
    }
}

// Concrete Decorators
class CheeseTopping extends ToppingDecorator {
    public CheeseTopping(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Cheese";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 30.0;
    }
}

class PepperoniTopping extends ToppingDecorator {
    public PepperoniTopping(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Pepperoni";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 50.0;
    }
}

class OliveTopping extends ToppingDecorator {
    public OliveTopping(Pizza pizza) { super(pizza); }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Olives";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 20.0;
    }
}
```

### 2. Usage

```java
public class DecoratorDemo {
    public static void main(String[] args) {
        Pizza pizza = new BasePizza();
        pizza = new CheeseTopping(pizza);
        pizza = new PepperoniTopping(pizza);
        pizza = new OliveTopping(pizza);

        System.out.println(pizza.getDescription() + " → ₹" + pizza.getCost());
        // Output: Base Pizza + Cheese + Pepperoni + Olives → ₹200.0
    }
}
```

### 3. Full Working Example: Coffee Shop

```java
// Beverage.java
interface Beverage {
    String getDescription();
    double getCost();
}

// Concrete Beverages
class Espresso implements Beverage {
    @Override
    public String getDescription() { return "Espresso"; }
    @Override
    public double getCost() { return 120.0; }
}

class HouseBlend implements Beverage {
    @Override
    public String getDescription() { return "House Blend Coffee"; }
    @Override
    public double getCost() { return 90.0; }
}

// Abstract Condiment Decorator
abstract class CondimentDecorator implements Beverage {
    protected Beverage beverage;

    public CondimentDecorator(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public String getDescription() {
        return beverage.getDescription();
    }

    @Override
    public double getCost() {
        return beverage.getCost();
    }
}

// Concrete Condiments
class Milk extends CondimentDecorator {
    public Milk(Beverage beverage) { super(beverage); }

    @Override
    public String getDescription() {
        return beverage.getDescription() + " + Milk";
    }

    @Override
    public double getCost() {
        return beverage.getCost() + 15.0;
    }
}

class WhipCream extends CondimentDecorator {
    public WhipCream(Beverage beverage) { super(beverage); }

    @Override
    public String getDescription() {
        return beverage.getDescription() + " + Whip Cream";
    }

    @Override
    public double getCost() {
        return beverage.getCost() + 25.0;
    }
}

class CaramelSyrup extends CondimentDecorator {
    public CaramelSyrup(Beverage beverage) { super(beverage); }

    @Override
    public String getDescription() {
        return beverage.getDescription() + " + Caramel Syrup";
    }

    @Override
    public double getCost() {
        return beverage.getCost() + 20.0;
    }
}

// Usage
public class CoffeeShop {
    public static void main(String[] args) {
        Beverage order = new Espresso();
        order = new Milk(order);
        order = new WhipCream(order);
        order = new CaramelSyrup(order);

        System.out.println(order.getDescription() + " → ₹" + order.getCost());
        // Output: Espresso + Milk + Whip Cream + Caramel Syrup → ₹180.0
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Decorator order matters | Document expected order; some decorators depend on others |
| Too many small decorators | Group related behaviors |
| Decorator forgets to delegate | Always call `super.method()` or `wrapped.method()` |
| Type checking breaks | Program to Component interface, not concrete classes |

## 🎯 Related Interview Questions

1. **Design a Coffee shop billing** — Base coffee + milk, sugar, cream, caramel
2. **Design an Encryption system** — Base data + AES encryption + Base64 encoding
3. **Java I/O Streams use Decorator** — `BufferedReader(new FileReader(...))`
4. **Difference between Decorator and Proxy?** — Decorator adds behavior; Proxy controls access

## 🆚 Decorator vs Inheritance

| Aspect | Inheritance | Decorator |
|--------|-------------|-----------|
| Flexibility | Fixed at compile time | Dynamic at runtime |
| Combinations | Need class per combination | Stack decorators freely |
| Code reuse | Through superclass | Through composition |
| Example | `PepperoniPizza extends Pizza` | `new PepperoniTopping(base)` |