# Composite Pattern

> **Composes objects into tree structures to represent part-whole hierarchies. Lets clients treat individual objects and compositions uniformly.**

## 📖 Concept

**Real-world analogy:** Army hierarchy — a General commands multiple Colonels, each Colonel commands multiple Captains. An order at the top flows down. Both a General (composite) and a Soldier (leaf) can receive orders uniformly.

## 🔍 When to Use

- Tree structure of objects (files & folders, UI components)
- Clients should treat leaf and composite objects the same way
- Want to represent part-whole hierarchies
- Need to manipulate complex tree structures

## ✅ Interview Checklist

- [ ] Component interface with common operations (add, remove, getChild, operation)
- [ ] Leaf implements Component (no children)
- [ ] Composite implements Component, stores children
- [ ] Composite delegates operation to children
- [ ] Client uses Component interface, doesn't know if leaf or composite

## 🧪 Common Interview Question

**Problem:** Design a File System where you have Files and Folders. Both should support `getSize()` and `display()`. A Folder can contain other Files or Folders.

## 💻 Java Implementation

### 1. Basic Composite

```java
import java.util.ArrayList;
import java.util.List;

// Component
interface FileSystemComponent {
    long getSize();
    void display(String indent);
}

// Leaf
class File implements FileSystemComponent {
    private String name;
    private long size;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public long getSize() { return size; }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📄 " + name + " (" + size + " KB)");
    }
}

// Composite
class Folder implements FileSystemComponent {
    private String name;
    private List<FileSystemComponent> children = new ArrayList<>();

    public Folder(String name) {
        this.name = name;
    }

    public void add(FileSystemComponent component) {
        children.add(component);
    }

    public void remove(FileSystemComponent component) {
        children.remove(component);
    }

    @Override
    public long getSize() {
        return children.stream().mapToLong(FileSystemComponent::getSize).sum();
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📁 " + name + " (" + getSize() + " KB)");
        for (FileSystemComponent child : children) {
            child.display(indent + "  ");
        }
    }
}
```

### 2. Usage

```java
public class CompositeDemo {
    public static void main(String[] args) {
        // Create files
        File file1 = new File("readme.txt", 10);
        File file2 = new File("photo.jpg", 500);
        File file3 = new File("main.java", 25);
        File file4 = new File("index.html", 30);

        // Create folders and add files
        Folder src = new Folder("src");
        src.add(file3);

        Folder docs = new Folder("docs");
        docs.add(file1);
        docs.add(file4);

        Folder images = new Folder("images");
        images.add(file2);

        // Root folder
        Folder root = new Folder("project");
        root.add(src);
        root.add(docs);
        root.add(images);

        root.display(""); // Displays entire tree
        System.out.println("Total size: " + root.getSize() + " KB");
    }
}
```

### 3. Full Working Example: Organization Hierarchy

```java
// Component
interface Employee {
    void showDetails(String indent);
    double getSalary();
}

// Leaf
class Developer implements Employee {
    private String name;
    private double salary;

    public Developer(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "👨‍💻 Developer: " + name + " (₹" + salary + ")");
    }

    @Override
    public double getSalary() {
        return salary;
    }
}

class Manager implements Employee {
    private String name;
    private double salary;

    public Manager(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "👔 Manager: " + name + " (₹" + salary + ")");
    }

    @Override
    public double getSalary() {
        return salary;
    }
}

// Composite
class Department implements Employee {
    private String name;
    private List<Employee> employees = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    public void add(Employee employee) {
        employees.add(employee);
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "🏢 Department: " + name);
        for (Employee emp : employees) {
            emp.showDetails(indent + "  ");
        }
    }

    @Override
    public double getSalary() {
        return employees.stream().mapToDouble(Employee::getSalary).sum();
    }
}

// Usage
public class OrganizationDemo {
    public static void main(String[] args) {
        // Create employees
        Developer dev1 = new Developer("Alice", 80000);
        Developer dev2 = new Developer("Bob", 90000);
        Manager mgr1 = new Manager("Charlie", 120000);
        Manager mgr2 = new Manager("David", 150000);

        // Build organization
        Department engineering = new Department("Engineering");
        engineering.add(mgr1);
        engineering.add(dev1);
        engineering.add(dev2);

        Department management = new Department("Management");
        management.add(mgr2);

        Department company = new Department("Company");
        company.add(engineering);
        company.add(management);

        company.showDetails("");
        System.out.println("Total salary: ₹" + company.getSalary());
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Composite exposes add/remove to leaf | Keep add/remove in Composite only, not in Component interface |
| Circular references | Validate parent-child relationships |
| Deep recursion on large trees | Use iterative traversal with stack |
| Shared state between composites | Ensure each composite manages its own children |

## 🎯 Related Interview Questions

1. **Design a UI Component tree** — Window contains Panel contains Button, TextField — all support `render()`
2. **Design an Organization hierarchy** — CEO → Managers → Employees, all have `getTotalSalary()`
3. **Design a Menu system** — Menu contains MenuItems and sub-Menus
4. **Difference between Composite and Decorator?** — Composite aggregates children; Decorator wraps a single component

## 🆚 Composite vs Decorator

| Aspect | Composite | Decorator |
|--------|-----------|-----------|
| Purpose | Tree structure, part-whole | Add behavior dynamically |
| Children | Multiple children | Wraps one component |
| Structure | Hierarchical tree | Linear chain |
| Example | File system, Org chart | Pizza toppings,Coffee condiments |