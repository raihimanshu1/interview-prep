# Visitor Pattern

> **Lets you define a new operation without changing the classes of the elements on which it operates. Separates algorithm from object structure.**

## 📖 Concept

**Real-world analogy:** A taxi booking app — you (the visitor) can visit different places (objects). The places don't change, but your actions at each place can be different. You visit a mall (shopping), then a gym (workout), then a restaurant (dining).

## 🔍 When to Use

- Object structure rarely changes but operations added frequently
- Need to perform unrelated operations on objects
- Want to keep operations separate from object structure
- Avoid polluting classes with many unrelated methods

## ✅ Interview Checklist

- [ ] Element interface declares accept(Visitor) method
- [ ] Concrete Elements implement accept
- [ ] Visitor interface declares visit methods for each element type
- [ ] Concrete Visitors implement operations for each element
- [ ] Elements call visitor.visit(this)
- [ ] Double dispatch: element accepts visitor, visitor visits element

## 🧪 Common Interview Question

**Problem:** Design a Shopping Cart system where you need to calculate total price, apply discounts, and generate invoice for different item types (Electronics, Clothing, Food). Add new operations without modifying item classes.

## 💻 Java Implementation

### 1. Basic Visitor

```java
// Element Interface
interface ShoppingItem {
    double accept(PriceVisitor visitor);
    void accept(InvoiceVisitor visitor);
}

// Concrete Elements
class Electronics implements ShoppingItem {
    private String name;
    private double price;
    private int warrantyMonths;

    public Electronics(String name, double price, int warrantyMonths) {
        this.name = name;
        this.price = price;
        this.warrantyMonths = warrantyMonths;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getWarrantyMonths() { return warrantyMonths; }

    @Override
    public double accept(PriceVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(InvoiceVisitor visitor) {
        visitor.visit(this);
    }
}

class Clothing implements ShoppingItem {
    private String name;
    private double price;
    private String size;

    public Clothing(String name, double price, String size) {
        this.name = name;
        this.price = price;
        this.size = size;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getSize() { return size; }

    @Override
    public double accept(PriceVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(InvoiceVisitor visitor) {
        visitor.visit(this);
    }
}

class Food implements ShoppingItem {
    private String name;
    private double price;
    private Date expiryDate;

    public Food(String name, double price, Date expiryDate) {
        this.name = name;
        this.price = price;
        this.expiryDate = expiryDate;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public Date getExpiryDate() { return expiryDate; }

    @Override
    public double accept(PriceVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(InvoiceVisitor visitor) {
        visitor.visit(this);
    }
}
```

### 2. Visitor Interfaces

```java
// Visitor Interface 1: Calculate Price
interface PriceVisitor {
    double visit(Electronics electronics);
    double visit(Clothing clothing);
    double visit(Food food);
}

// Visitor Interface 2: Generate Invoice
interface InvoiceVisitor {
    void visit(Electronics electronics);
    void visit(Clothing clothing);
    void visit(Food food);
}
```

### 3. Concrete Visitors

```java
// Concrete Visitor: Calculate Final Price
class PriceCalculator implements PriceVisitor {
    @Override
    public double visit(Electronics electronics) {
        double price = electronics.getPrice();
        // Electronics: 10% discount
        return price * 0.9;
    }

    @Override
    public double visit(Clothing clothing) {
        double price = clothing.getPrice();
        // Clothing: 5% discount
        return price * 0.95;
    }

    @Override
    public double visit(Food food) {
        double price = food.getPrice();
        // Food: no discount
        return price;
    }
}

// Concrete Visitor: Generate Invoice
class InvoiceGenerator implements InvoiceVisitor {
    private StringBuilder invoice = new StringBuilder();

    @Override
    public void visit(Electronics electronics) {
        invoice.append(electronics.getName())
               .append(" - ₹").append(electronics.getPrice())
               .append(" (Warranty: ").append(electronics.getWarrantyMonths())
               .append(" months)\n");
    }

    @Override
    public void visit(Clothing clothing) {
        invoice.append(clothing.getName())
               .append(" - ₹").append(clothing.getPrice())
               .append(" (Size: ").append(clothing.getSize())
               .append(")\n");
    }

    @Override
    public void visit(Food food) {
        invoice.append(food.getName())
               .append(" - ₹").append(food.getPrice())
               .append(" (Expires: ").append(food.getExpiryDate())
               .append(")\n");
    }

    public String getInvoice() {
        return invoice.toString();
    }
}
```

### 4. Usage

```java
public class VisitorDemo {
    public static void main(String[] args) {
        List<ShoppingItem> cart = List.of(
            new Electronics("Laptop", 50000, 12),
            new Clothing("T-Shirt", 500, "M"),
            new Food("Chocolate", 100, new Date())
        );

        // Calculate total price
        PriceVisitor priceCalculator = new PriceCalculator();
        double total = 0;
        for (ShoppingItem item : cart) {
            total += item.accept(priceCalculator);
        }
        System.out.println("Total: ₹" + total);

        // Generate invoice
        InvoiceVisitor invoiceGenerator = new InvoiceGenerator();
        for (ShoppingItem item : cart) {
            item.accept(invoiceGenerator);
        }
        System.out.println("\nInvoice:\n" + invoiceGenerator.getInvoice());
    }
}
```

### 5. Full Working Example: Tax Calculation System

```java
// Elements
interface Taxable {
    double accept(TaxVisitor visitor);
}

class SalaryIncome implements Taxable {
    private double amount;
    private String designation;

    public SalaryIncome(double amount, String designation) {
        this.amount = amount;
        this.designation = designation;
    }

    public double getAmount() { return amount; }
    public String getDesignation() { return designation; }

    @Override
    public double accept(TaxVisitor visitor) {
        return visitor.visit(this);
    }
}

class PropertyIncome implements Taxable {
    private double propertyValue;
    private String location;

    public PropertyIncome(double propertyValue, String location) {
        this.propertyValue = propertyValue;
        this.location = location;
    }

    public double getPropertyValue() { return propertyValue; }
    public String getLocation() { return location; }

    @Override
    public double accept(TaxVisitor visitor) {
        return visitor.visit(this);
    }
}

class CapitalGains implements Taxable {
    private double gainAmount;
    private String type; // "short-term" or "long-term"

    public CapitalGains(double gainAmount, String type) {
        this.gainAmount = gainAmount;
        this.type = type;
    }

    public double getGainAmount() { return gainAmount; }
    public String getType() { return type; }

    @Override
    public double accept(TaxVisitor visitor) {
        return visitor.visit(this);
    }
}

// Visitor
interface TaxVisitor {
    double visit(SalaryIncome salary);
    double visit(PropertyIncome property);
    double visit(CapitalGains gains);
}

// Concrete Visitor: Indian Tax Calculator
class IndianTaxCalculator implements TaxVisitor {
    @Override
    public double visit(SalaryIncome salary) {
        double amount = salary.getAmount();
        // Progressive tax: 0-2.5L: 0%, 2.5-5L: 5%, 5-10L: 20%, 10L+: 30%
        if (amount <= 250000) return 0;
        if (amount <= 500000) return (amount - 250000) * 0.05;
        if (amount <= 1000000) return 12500 + (amount - 500000) * 0.20;
        return 12500 + 100000 + (amount - 1000000) * 0.30;
    }

    @Override
    public double visit(PropertyIncome property) {
        // 0.5% of property value
        return property.getPropertyValue() * 0.005;
    }

    @Override
    public double visit(CapitalGains gains) {
        if (gains.getType().equals("short-term")) {
            return gains.getGainAmount() * 0.15; // 15% STCG
        } else {
            return gains.getGainAmount() * 0.10; // 10% LTCG
        }
    }
}

classUSTaxCalculator implements TaxVisitor {
    @Override
    public double visit(SalaryIncome salary) {
        double amount = salary.getAmount();
        // Simplified US tax brackets
        if (amount <= 11000) return amount * 0.10;
        if (amount <= 44725) return 1100 + (amount - 11000) * 0.12;
        return 5100 + (amount - 44725) * 0.22;
    }

    @Override
    public double visit(PropertyIncome property) {
        return property.getPropertyValue() * 0.012; // 1.2% property tax
    }

    @Override
    public double visit(CapitalGains gains) {
        return gains.getGainAmount() * 0.20; // 20% capital gains
    }
}

// Usage
public class TaxDemo {
    public static void main(String[] args) {
        List<Taxable> incomes = List.of(
            new SalaryIncome(1200000, "Manager"),
            new PropertyIncome(5000000, "Mumbai"),
            new CapitalGains(200000, "long-term")
        );

        TaxVisitor indianTax = new IndianTaxCalculator();
        double totalTax = 0;
        for (Taxable income : incomes) {
            totalTax += income.accept(indianTax);
        }
        System.out.println("Total Tax (India): ₹" + totalTax);
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Adding new Element type breaks all Visitors | This is a known trade-off — use when elements are stable |
| Too many visitor types | Group visitors or use parameter object |
| State sharing between visitors | Pass context via constructor or parameters |
| Cyclic dependencies | Separate visitor interfaces by concern |

## 🎯 Related Interview Questions

1. **Design a Shopping Cart** — Calculate price, tax, discount, generate invoice
2. **Design a Tax System** — Salary, Property, Capital Gains each taxed differently
3. **Design a Report Generator** — HTML, PDF, CSV reports for same data

## 🆚 Visitor vs Other Patterns

| Aspect | Visitor | Strategy | Iterator |
|--------|---------|----------|----------|
| Purpose | Add operations without changing classes | Interchangeable algorithms | Traverse collections |
| Class changes | Only Visitor changes | Only Strategy changes | Only Iterator changes |
| Double dispatch | Yes | No | No |
| Use | Multiple operations on stable structure | One algorithm | Traversal |