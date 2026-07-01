# Abstract Factory Pattern

> **Provides an interface for creating families of related or dependent objects without specifying their concrete classes.**

## 📖 Concept

**Real-world analogy:** A furniture store has different styles (Modern, Victorian). Each style has a family of products (Chair, Sofa, CoffeeTable). If you buy Modern, everything you get is Modern. The factory ensures you don't mix styles.

## 🔍 When to Use

- System needs to be independent of how its products are created
- Need to enforce that products from same family are used together
- Adding new product families shouldn't break existing code
- Client shouldn't depend on concrete classes of products

## ✅ Interview Checklist

- [ ] Abstract Factory interface declares creation methods for each product type
- [ ] Concrete Factories implement the interface for each family
- [ ] Abstract Product interfaces for each type of product
- [ ] Concrete Products implement their respective interfaces
- [ ] Client uses only abstract interfaces
- [ ] Adding new product family = add new concrete factory + all concrete products

## 🧪 Common Interview Question

**Problem:** Design a Cross-Platform UI Toolkit. Support two themes: Dark Theme and Light Theme. Each theme produces Button, TextField, and Checkbox that are consistent with the theme.

## 💻 Java Implementation

### 1. Basic Abstract Factory

```java
// --- Abstract Products ---
interface Button {
    void render();
}

interface TextField {
    void display();
}

interface Checkbox {
    void check();
}

// --- Concrete Products: Dark Theme ---
class DarkButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering Dark Button");
    }
}

class DarkTextField implements TextField {
    @Override
    public void display() {
        System.out.println("Displaying Dark TextField");
    }
}

class DarkCheckbox implements Checkbox {
    @Override
    public void check() {
        System.out.println("Checking Dark Checkbox");
    }
}

// --- Concrete Products: Light Theme ---
class LightButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering Light Button");
    }
}

class LightTextField implements TextField {
    @Override
    public void display() {
        System.out.println("Displaying Light TextField");
    }
}

class LightCheckbox implements Checkbox {
    @Override
    public void check() {
        System.out.println("Checking Light Checkbox");
    }
}

// --- Abstract Factory ---
interface UIFactory {
    Button createButton();
    TextField createTextField();
    Checkbox createCheckbox();
}

// --- Concrete Factories ---
class DarkThemeFactory implements UIFactory {
    @Override
    public Button createButton() { return new DarkButton(); }
    @Override
    public TextField createTextField() { return new DarkTextField(); }
    @Override
    public Checkbox createCheckbox() { return new DarkCheckbox(); }
}

class LightThemeFactory implements UIFactory {
    @Override
    public Button createButton() { return new LightButton(); }
    @Override
    public TextField createTextField() { return new LightTextField(); }
    @Override
    public Checkbox createCheckbox() { return new LightCheckbox(); }
}
```

### 2. Client Code

```java
class Application {
    private Button button;
    private TextField textField;
    private Checkbox checkbox;

    public Application(UIFactory factory) {
        button = factory.createButton();
        textField = factory.createTextField();
        checkbox = factory.createCheckbox();
    }

    public void renderUI() {
        button.render();
        textField.display();
        checkbox.check();
    }
}

public class UIDemo {
    public static void main(String[] args) {
        // Using Dark Theme
        UIFactory factory = new DarkThemeFactory();
        Application app = new Application(factory);
        app.renderUI();

        // Switch to Light Theme — just change factory
        factory = new LightThemeFactory();
        app = new Application(factory);
        app.renderUI();
    }
}
```

### 3. Full Working Example: Cloud Resource Manager

```java
// Abstract Products
interface VM {
    void start();
    void stop();
}

interface Storage {
    void upload(String file);
    void download(String file);
}

interface Database {
    void connect();
    void query(String sql);
}

// AWS Products
class AWSVM implements VM {
    @Override public void start() { System.out.println("Starting AWS EC2 instance"); }
    @Override public void stop() { System.out.println("Stopping AWS EC2 instance"); }
}

class AWSS3 implements Storage {
    @Override public void upload(String file) { System.out.println("Uploading to AWS S3: " + file); }
    @Override public void download(String file) { System.out.println("Downloading from AWS S3: " + file); }
}

class AWSRDS implements Database {
    @Override public void connect() { System.out.println("Connected to AWS RDS"); }
    @Override public void query(String sql) { System.out.println("AWS RDS Query: " + sql); }
}

// Azure Products
class AzureVM implements VM {
    @Override public void start() { System.out.println("Starting Azure VM"); }
    @Override public void stop() { System.out.println("Stopping Azure VM"); }
}

class AzureBlob implements Storage {
    @Override public void upload(String file) { System.out.println("Uploading to Azure Blob: " + file); }
    @Override public void download(String file) { System.out.println("Downloading from Azure Blob: " + file); }
}

class AzureSQL implements Database {
    @Override public void connect() { System.out.println("Connected to Azure SQL"); }
    @Override public void query(String sql) { System.out.println("Azure SQL Query: " + sql); }
}

// Abstract Factory
interface CloudFactory {
    VM createVM();
    Storage createStorage();
    Database createDatabase();
}

// Concrete Factories
class AWSFactory implements CloudFactory {
    @Override public VM createVM() { return new AWSVM(); }
    @Override public Storage createStorage() { return new AWSS3(); }
    @Override public Database createDatabase() { return new AWSRDS(); }
}

class AzureFactory implements CloudFactory {
    @Override public VM createVM() { return new AzureVM(); }
    @Override public Storage createStorage() { return new AzureBlob(); }
    @Override public Database createDatabase() { return new AzureSQL(); }
}

// Client
class CloudApp {
    private VM vm;
    private Storage storage;
    private Database db;

    public CloudApp(CloudFactory factory) {
        vm = factory.createVM();
        storage = factory.createStorage();
        db = factory.createDatabase();
    }

    public void deploy() {
        vm.start();
        storage.upload("app.jar");
        db.connect();
        db.query("SELECT * FROM users");
    }
}

public class CloudDemo {
    public static void main(String[] args) {
        CloudApp app = new CloudApp(new AWSFactory());
        app.deploy();
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Adding new product type breaks all factories | Adding new product type requires changing Abstract Factory interface too — this is a known trade-off |
| Too many interfaces (one per product type) | Accept the cost for type safety. For simpler cases, use Factory Method |
| Concrete factories become too large | Split by domain concern or use dependency injection |

## 🎯 Related Interview Questions

1. **Design a Cloud Resource Manager** — Support AWS, Azure, GCP — each creates VM, Storage, Database
2. **Design a Vehicle Manufacturing system** — Honda/Toyota factories producing Sedan/SUV/Hatchback
3. **Difference between Factory Method and Abstract Factory?** — Factory Method: one product type; Abstract Factory: families of products
4. **Design a Database Access system** supporting MySQL, PostgreSQL, MongoDB with Connection + Query + Transaction

## 🆚 Factory Method vs Abstract Factory

| Aspect | Factory Method | Abstract Factory |
|--------|---------------|------------------|
| Creates | One product type | Family of related products |
| Scale | Single product | Multiple products |
| Example | `PaymentFactory` creates only `Payment` | `UIFactory` creates Button + TextField + Checkbox |
| When to add new product | Easy — add new creator | Hard — must add method to AbstractFactory interface |