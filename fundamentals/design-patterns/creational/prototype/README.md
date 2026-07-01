# Prototype Pattern

> **Create new objects by copying an existing object (the prototype) instead of building from scratch.**

## 📖 What Problem Does This Solve?

### Scenario

Imagine you run a document generation service. You create invoice templates:

```
Template Invoice: Company letterhead, tax footer, logo
    ↓
Clone it
    ↓
Customize: Client name, amount, date
    ↓
Result: New invoice ready in milliseconds
```

**Without Prototype:** Each invoice = database fetch + formatting + setup = 2 seconds
**With Prototype:** Clone template + customize = 20 milliseconds

**100x faster!**

### Real-World Analogy

**Cell division (mitosis):**
```
Parent Cell (Prototype)
    ↓
DNA copied
    ↓
Two identical daughter cells
```

**Photocopy machine:**
```
Original document (Prototype)
    ↓
Photocopy (Clone)
    ↓
Write on the copy
    ↓
New document with same layout
```

---

## 🔍 When Do You Need This?

| Situation | Without Prototype | With Prototype |
|-----------|-------------------|----------------|
| Creating game enemies | Load model from disk (100ms each) × 1000 = 100s | Load once, clone (1ms) × 1000 = 1s |
| Generating reports | Query DB + format each time | Clone template, just change data |
| Complex object setup | Call 10 methods to initialize | Clone pre-initialized object |

**Interview Question:** "When would you use Prototype over other creational patterns?"

**Answer:**
- Object creation is expensive (DB calls, network, file I/O)
- Many similar objects needed
- Classes to instantiate determined at runtime
- Want to avoid building complex object graphs repeatedly

---

## ✅ Interview Checklist

- [ ] Prototype interface with `clone()` method
- [ ] Concrete prototypes implement clone
- [ ] Deep copy vs shallow copy — important distinction!
- [ ] Prototype registry (optional but useful)
- [ ] Handle `CloneNotSupportedException`

---

## 🧪 Core Problem: Shape Editor

**Problem Statement:**
Design a shape editor where users can:
1. Create shapes (Circle, Rectangle) with expensive setup (loading textures, calculating bounds)
2. Clone shapes to create similar ones quickly
3. Modify cloned shapes without affecting original

**Without Prototype:**
```java
// Each creation = expensive operation
Circle circle1 = new Circle();
circle1.setColor("Red");
circle1.setRadius(10);
circle1.loadTexture();  // 100ms
circle1.calculateBounds(); // 50ms

// Creating similar circle = repeat ALL steps
Circle circle2 = new Circle();
circle2.setColor("Red");  // Same setup again!
circle2.setRadius(10);
circle2.loadTexture();  // 100ms again
circle2.calculateBounds(); // 50ms again
// Total: 600ms for 2 circles
```

**With Prototype:**
```java
// Create template once
Circle template = new Circle();
template.setColor("Red");
template.setRadius(10);
template.loadTexture();  // 100ms (once)
template.calculateBounds(); // 50ms (once)

// Clone = instant!
Circle circle1 = template.clone();  // 1ms
Circle circle2 = template.clone();  // 1ms
// Total: 152ms for 3 circles (5x faster!)
```

---

## 💻 Implementation: Step by Step

### Step 1: Define Prototype Interface

```java
// All prototypes must support cloning
interface Shape extends Cloneable {
    Shape clone();  // The key method
    void draw();
}
```

**Why `Cloneable`?**
- Marker interface (no methods)
- Tells JVM: "This class supports cloning"
- Without it: `clone()` throws `CloneNotSupportedException`

### Step 2: Create Concrete Prototype

```java
class Circle implements Shape {
    private String color;
    private int radius;
    private String texture;  // Expensive to load!

    @Override
    public Shape clone() {
        Circle cloned = new Circle();
        cloned.color = this.color;        // Copy simple field
        cloned.radius = this.radius;      // Copy simple field
        cloned.texture = this.texture;    // Share reference (shallow copy)
        return cloned;
    }

    @Override
    public void draw() {
        System.out.println("Drawing " + color + " circle, radius=" + radius);
    }

    // Getter/Setter
    public void setColor(String color) { this.color = color; }
    public void setRadius(int radius) { this.radius = radius; }
}
```

**Line-by-line explanation of `clone()`:**

```
1. Circle cloned = new Circle();
   ↓
   Create NEW empty Circle object (not shared!)

2. cloned.color = this.color;
   ↓
   Copy color value from original to clone

3. cloned.radius = this.radius;
   ↓
   Copy radius value from original to clone

4. cloned.texture = this.texture;
   ↓
   Copy texture reference (both share same texture object)
   This is SHALLOW copy!

5. return cloned;
   ↓
   Return the NEW object
```

**Memory representation:**

```
Before clone:
template (Circle)
  ├─ color: "Red"
  ├─ radius: 10
  └─ texture → "red_circle.png" (in memory)

After clone:
template (Circle)          cloned (Circle)
  ├─ color: "Red"           ├─ color: "Red"
  ├─ radius: 10             ├─ radius: 10
  └─ texture → "red_circle.png"   └─ texture → "red_circle.png"
                                            ↑
                                    SAME texture object!
```

### Step 3: Create Prototype Registry

```java
class ShapeRegistry {
    // Store prototypes by name
    private Map<String, Shape> prototypes = new HashMap<>();

    // Register a prototype
    public void addShape(String key, Shape shape) {
        prototypes.put(key, shape);
    }

    // Clone and return
    public Shape getShape(String key) {
        Shape prototype = prototypes.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("Unknown shape: " + key);
        }
        return prototype.clone();  // Return NEW object each time
    }
}
```

**Why registry?**
- Central place to manage templates
- Easy to lookup by name
- Can register new types at runtime

### Step 4: Usage — Complete Flow

```java
public class PrototypeDemo {
    public static void main(String[] args) {
        // Create registry
        ShapeRegistry registry = new ShapeRegistry();

        // Create and register template (ONE TIME)
        Circle redCircle = new Circle();
        redCircle.setColor("Red");
        redCircle.setRadius(10);
        registry.addShape("red_circle", redCircle);

        // Now clone is fast!
        Shape circle1 = registry.getShape("red_circle");
        Shape circle2 = registry.getShape("red_circle");

        circle1.draw();  // Drawing Red circle, radius=10
        circle2.draw();  // Drawing Red circle, radius=10

        // Verify: different objects!
        System.out.println(circle1 == circle2);  // false
    }
}
```

---

## 🧪 Advanced: Prototype Registry with Multiple Types

```java
// Rectangle also implements Shape
class Rectangle implements Shape {
    private int width, height;

    @Override
    public Shape clone() {
        Rectangle r = new Rectangle();
        r.width = this.width;
        r.height = this.height;
        return r;
    }

    @Override
    public void draw() {
        System.out.println("Drawing Rectangle " + width + "x" + height);
    }
}

// Registry can store any Shape
class ShapeRegistry {
    private Map<String, Shape> prototypes = new HashMap<>();

    public void addShape(String key, Shape shape) {
        prototypes.put(key, shape);
    }

    public Shape getShape(String key) {
        Shape prototype = prototypes.get(key);
        return prototype != null ? prototype.clone() : null;
    }
}

// Usage
public class AdvancedDemo {
    public static void main(String[] args) {
        ShapeRegistry registry = new ShapeRegistry();

        // Register different shapes
        registry.addShape("circle", new Circle());
        registry.addShape("rectangle", new Rectangle());

        // Get instances
        Shape shape1 = registry.getShape("circle");
        Shape shape2 = registry.getShape("rectangle");

        shape1.draw();  // Drawing Circle
        shape2.draw();  // Drawing Rectangle
    }
}
```

---

## ⚠️ Deep Dive: Shallow Copy vs Deep Copy

### Shallow Copy (Default)

```java
@Override
public Shape clone() {
    Circle cloned = new Circle();
    cloned.color = this.color;
    cloned.radius = this.radius;
    cloned.texture = this.texture;  // ← SHARED reference
    return cloned;
}
```

**Problem:**
```
Original: texture → "red.png" (file in memory)
Clone:    texture → "red.png" (SAME file object)

If you modify texture in clone:
    cloned.texture = "blue.png";
    Original texture STILL "red.png"  ✓ (strings are immutable)

But with MUTABLE objects:
    cloned.texture.setColor("Blue");
    Original texture ALSO changes!  ✗ (same object modified)
```

### Deep Copy (Fix)

```java
@Override
public Shape clone() {
    try {
        Circle cloned = (Circle) super.clone();  // Built-in deep copy
        // OR manually:
        // cloned.texture = new Texture(this.texture);
        return cloned;
    } catch (CloneNotSupportedException e) {
        return null;
    }
}
```

**With `super.clone()`:**
```
super.clone() creates completely independent copy:
    - New Circle object
    - New Texture object
    - All fields deeply copied
```

### Comparison Table

| Aspect | Shallow Copy | Deep Copy |
|--------|-------------|-----------|
| Speed | Fast | Slower (copies everything) |
| Memory | Low | Higher |
| Shared refs | Yes | No |
| Use when | Immutable fields | Mutable nested objects |
| Example | Strings, ints | Collections, custom objects |

---

## ⚠️ Pitfalls to Avoid

| Issue | Why It Happens | Solution |
|-------|---------------|----------|
| **Clone not deep enough** | Shallow copy shares mutable objects | Use `super.clone()` or copy constructor |
| **`CloneNotSupportedException`** | Class doesn't implement `Cloneable` | Implement `Cloneable` interface |
| **Final fields can't be cloned** | `final` fields can't be reassigned | Use constructor injection instead |
| **Circular references** | A references B, B references A | Careful with deep copy, or use serialization |
| **Registry grows unbounded** | Never removes old templates | Implement eviction policy if needed |

---

## 🎯 Interview Questions

### Q1: "Why use Prototype instead of creating new objects?"

**Answer:**
1. **Performance:** Cloning is faster than complex initialization
2. **Dynamic:** Can determine type at runtime
3. **Complex graphs:** Easier than rebuilding object relationships
4. **Registry pattern:** Central management of templates

### Q2: "Shallow copy vs deep copy?"

**Answer:**
- **Shallow:** Copies references (shared objects)
- **Deep:** Copies everything (independent objects)
- **Rule of thumb:** If object has mutable nested objects, use deep copy

### Q3: "Prototype vs other creational patterns?"

| Pattern | Creates By | Use When |
|---------|-----------|----------|
| **Prototype** | Cloning existing | Expensive creation, similar objects |
| **Factory** | Calling constructor | Different types, centralized |
| **Builder** | Step-by-step | Complex construction with options |
| **Singleton** | Exactly one | Need single instance |

### Q4: "Real-world examples?"

1. **Game development:** Spawning enemies from templates
2. **Document systems:** Invoice/report templates
3. **UI frameworks:** Component cloning in form builders
4. **Java itself:** `Object.clone()`, `ArrayList.clone()`

---

## 🆚 Prototype vs Similar Patterns

| Pattern | How It Creates | Best For |
|---------|---------------|----------|
| Prototype | Copy existing | Similar objects, expensive creation |
| Factory | Constructor | Different types, clean interface |
| Builder | Step-by-step | Complex objects with many options |

**Key Difference:**
```
Factory: "Make me a NEW car"
Prototype: "Copy that car and change color"
Builder: "Build car: engine V8, red, sunroof, leather seats"
```

---

## ✅ Summary

**Prototype Pattern:**
1. **Problem:** Creating objects is expensive, need many similar ones
2. **Solution:** Create template once, clone when needed
3. **Key:** `clone()` method, shallow vs deep copy
4. **Benefit:** Performance, flexibility, runtime type determination
5. **Use:** Game objects, document templates, UI components