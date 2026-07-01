# Template Method Pattern

> **Defines the skeleton of an algorithm in a base class, letting subclasses override specific steps without changing the algorithm's structure.**

## 📖 Concept

**Real-world analogy:** Cooking recipe — the base steps are fixed (prepare ingredients → cook → serve), but each dish has variations. Making tea vs coffee: same overall process, different ingredients.

## 🔍 When to Use

- Algorithm has fixed structure but varying steps
- Common behavior among subclasses should be in one place
- Want to avoid code duplication in subclasses
- Control over extension points

## ✅ Interview Checklist

- [ ] Abstract class defines template method (final)
- [ ] Template method calls primitive/abstract steps
- [ ] Concrete subclasses override steps
- [ ] Hook methods allow optional overriding
- [ ] Base class provides default implementation

## 🧪 Common Interview Question

**Problem:** Design a Data Export system. All exports follow: Connect → Read → Process → Write → Close. But CSV, PDF, Excel differ in how they read, process, and write.

## 💻 Java Implementation

### 1. Basic Template Method

```java
// Base Class
abstract class DataExporter {
    // Template Method — final so subclasses can't change the flow
    public final void export() {
        connect();
        readData();
        processData();
        writeData();
        close();
    }

    private void connect() {
        System.out.println("Connected to database");
    }

    private void close() {
        System.out.println("Connection closed");
    }

    // Hook methods — subclasses override these
    protected abstract void readData();
    protected abstract void processData();
    protected abstract void writeData();
}

// Concrete Implementations
class CSVExporter extends DataExporter {
    @Override protected void readData() { System.out.println("Reading CSV rows"); }
    @Override protected void processData() { System.out.println("Parsing CSV format"); }
    @Override protected void writeData() { System.out.println("Writing CSV file"); }
}

class PDFExporter extends DataExporter {
    @Override protected void readData() { System.out.println("Reading from DB cursor"); }
    @Override protected void processData() { System.out.println("Generating PDF layout"); }
    @Override protected void writeData() { System.out.println("Writing PDF with headers"); }
}

class ExcelExporter extends DataExporter {
    @Override protected void readData() { System.out.println("Reading DB result set"); }
    @Override protected void processData() { System.out.println("Building Excel workbook"); }
    @Override protected void writeData() { System.out.println("Writing .xlsx with formulas"); }
}
```

### 2. Usage

```java
public class TemplateDemo {
    public static void main(String[] args) {
        DataExporter exporter = new CSVExporter();
        exporter.export();
    }
}
```

### 3. Full Working Example: Game AI

```java
// Abstract AI
abstract class GameAI {
    // Template method
    public final void playTurn() {
        gatherResources();
        buildUnits();
        attackEnemy();
        defendBase();
    }

    // Common steps
    private void gatherResources() {
        System.out.println("Gathering resources");
    }

    private void defendBase() {
        System.out.println("Defending base with defenses");
    }

    // Variable steps
    protected abstract void buildUnits();
    protected abstract void attackEnemy();
}

// Easy AI
class EasyAI extends GameAI {
    @Override protected void buildUnits() {
        System.out.println("Building 2 weak units");
    }
    @Override protected void attackEnemy() {
        System.out.println("Attacking with low strength");
    }
}

// Hard AI
class HardAI extends GameAI {
    @Override protected void buildUnits() {
        System.out.println("Building 10 strong units");
    }
    @Override protected void attackEnemy() {
        System.out.println("Coordinated attack with all units");
    }
}

// Usage
public class GameAIDemo {
    public static void main(String[] args) {
        GameAI easy = new EasyAI();
        GameAI hard = new HardAI();

        easy.playTurn();
        System.out.println("---");
        hard.playTurn();
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Template method too rigid | Use hook methods for optional steps |
| Duplication in subclasses | Pull up common code to base class |
| Violating Liskov Substitution | Ensure subclasses follow contract |
| Order should be final | Mark template method as `final` |

## 🎯 Related Interview Questions

1. **Design a House Building plan** — Foundation → Structure → Interior
2. **Design a Game AI** — Observe → Decide → Act

## 🆚 Template Method vs Strategy

| Aspect | Template Method | Strategy |
|--------|-----------------|----------|
| Structure | Inheritance-based | Composition-based |
| Algorithm | Varies by subclass | Varies by injected strategy |
| Control | Base class controls flow | Client controls strategy |
| Flexibility | Less flexible at runtime | More flexible at runtime |