# Flyweight Pattern

> **Uses sharing to support large numbers of fine-grained objects efficiently.**

## 📖 Concept

**Real-world analogy:** A library — books are shared among many readers. Instead of each person owning a copy, they borrow from the shared library.

## 🔍 When to Use

- Many similar objects cause high memory usage
- Object state can be split into intrinsic (shared) and extrinsic (unique)
- Object identity doesn't matter
- Need to reduce memory footprint

## ✅ Interview Checklist

- [ ] Flyweight interface defines shared behavior
- [ ] Concrete Flyweight stores intrinsic (shared) state
- [ ] Flyweight Factory manages pool/cache of flyweights
- [ ] Client passes extrinsic (unique) state as parameters
- [ ] extrinsic state cannot be shared

## 🧪 Common Interview Question

**Problem:** Design a Text Editor character rendering system. Each character in a document of 100,000 characters should not create 100,000 objects. Share character glyphs using Flyweight.

## 💻 Java Implementation

### 1. Basic Flyweight

```java
import java.util.HashMap;
import java.util.Map;

// Flyweight (intrinsic state — shared)
class CharacterGlyph {
    private final char character;
    private final String font;
    private final int size;

    public CharacterGlyph(char character, String font, int size) {
        this.character = character;
        this.font = font;
        this.size = size;
    }

    public void display(int x, int y, String color) { // extrinsic: position + color
        System.out.println("Char '" + character + "' at (" + x + "," + y
            + ") font=" + font + " size=" + size + " color=" + color);
    }
}

// Flyweight Factory
class GlyphFactory {
    private Map<String, CharacterGlyph> pool = new HashMap<>();

    public CharacterGlyph getGlyph(char c, String font, int size) {
        String key = c + "_" + font + "_" + size;
        if (!pool.containsKey(key)) {
            pool.put(key, new CharacterGlyph(c, font, size));
            System.out.println("Creating new glyph for: " + key);
        }
        return pool.get(key);
    }

    public int getPoolSize() { return pool.size(); }
}
```

### 2. Usage

```java
public class FlyweightDemo {
    public static void main(String[] args) {
        GlyphFactory factory = new GlyphFactory();

        // Document rendering — 1000 characters
        String document = "hellohellohello";
        int x = 0, y = 0;

        for (char c : document.toCharArray()) {
            CharacterGlyph glyph = factory.getGlyph(c, "Arial", 12);
            glyph.display(x++, y, "black"); // extrinsic state passed per use
        }

        System.out.println("Unique glyphs created: " + factory.getPoolSize());
        // Output: Only 5 unique glyphs (h, e, l, o) for 15 characters
    }
}
```

### 3. Full Working Example: Game Tree Rendering

```java
// Flyweight - Tree type
class TreeType {
    private String name;
    private String color;
    private String texture;

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
        System.out.println("Creating TreeType: " + name + " (expensive!)");
    }

    public void render(int x, int y, int age) { // extrinsic: position + age
        System.out.println("Rendering " + name + " at (" + x + "," + y
            + ") age=" + age + " color=" + color);
    }
}

// Flyweight Factory
class TreeFactory {
    private Map<String, TreeType> treeTypes = new HashMap<>();

    public TreeType getTreeType(String name, String color, String texture) {
        String key = name + "_" + color + "_" + texture;
        if (!treeTypes.containsKey(key)) {
            treeTypes.put(key, new TreeType(name, color, texture));
        }
        return treeTypes.get(key);
    }

    public int getTreeTypeCount() {
        return treeTypes.size();
    }
}

// Tree - uses flyweight
class Tree {
    private int x, y;
    private TreeType type;
    private int age; // extrinsic state

    public Tree(int x, int y, TreeType type, int age) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.age = age;
    }

    public void render() {
        type.render(x, y, age);
    }
}

// Forest - manages many trees
class Forest {
    private List<Tree> trees = new ArrayList<>();
    private TreeFactory factory = new TreeFactory();

    public void plantTree(int x, int y, String name, String color, String texture, int age) {
        TreeType type = factory.getTreeType(name, color, texture);
        Tree tree = new Tree(x, y, type, age);
        trees.add(tree);
    }

    public void render() {
        for (Tree tree : trees) {
            tree.render();
        }
    }

    public int getTreeTypeCount() {
        return factory.getTreeTypeCount();
    }
}

// Usage
public class GameDemo {
    public static void main(String[] args) {
        Forest forest = new Forest();

        // Plant 1,000,000 trees — only 2 types created
        for (int i = 0; i < 1000000; i++) {
            forest.plantTree(i, i % 1000, "Oak", "Green", "oak_texture", i % 50);
            forest.plantTree(i, i % 1000, "Pine", "DarkGreen", "pine_texture", i % 30);
        }

        System.out.println("Total TreeTypes created: " + forest.getTreeTypeCount());
        // Output: Only 2 TreeTypes (Oak + Pine), not 2 million!

        forest.render(); // Render all trees
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Mutable intrinsic state | Make intrinsic state immutable (final) |
| Too much in extrinsic state | Keep only truly unique data extrinsic |
| Thread safety in factory | Use `computeIfAbsent` in Java 8+ |
| Memory leak in factory | Implement eviction if pool grows unbounded |

## 🎯 Related Interview Questions

1. **Design a Game tree rendering** — Share tree model among millions of trees
2. **Design a Chess piece system** — Share piece type (King, Queen) across all boards
3. **Design a String pool** — Similar to String.intern()
4. **Difference between Flyweight and Singleton?** — Flyweight has multiple similar instances; Singleton has exactly one

## 🆚 Flyweight vs Singleton

| Aspect | Flyweight | Singleton |
|--------|-----------|-----------|
| Instances | Multiple shared instances | Exactly one instance |
| Purpose | Reduce memory for many similar objects | Ensure single global instance |
| Key | Intrinsic state is shared | One instance for entire app |
| Example | Character glyphs in text editor | Logger, Configuration |