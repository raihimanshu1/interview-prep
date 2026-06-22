# Singleton Pattern — Complete Deep Dive

## 1. Why This Concept Matters

Singleton is the most frequently asked design pattern in Java interviews. It restricts a class to a single instance and provides global access. In production, Singletons are used for configuration, thread pools, logging, and caching. Understanding different implementation approaches — eager, lazy, double-checked locking, enum — and their tradeoffs is essential. Interviewers test Singleton to verify your understanding of thread safety, lazy initialization, serialization issues, and class loading.

Misunderstanding Singleton causes:
- Multiple instances in multi-threaded environments
- Performance issues from unnecessary synchronization
- Broken singleton via serialization/reflection
- Memory leaks from classloader issues in application servers

## 2. Basic Meaning

Singleton = exactly one instance of a class, globally accessible.

**Key vocabulary:**
- **Eager initialization**: instance created at class loading
- **Lazy initialization**: instance created on first request
- **Double-checked locking (DCL)**: lazy with minimal synchronization
- **Bill Pugh (holder)**: lazy via static inner class
- **Serialization**: `readResolve()` to preserve singleton
- **Reflection**: can break singleton via constructor access
- **Enum singleton**: serialization/reflection safe

What it is NOT: Singleton is not global variables. It is not the same as static classes.

## 3. Real Code / Real Example

```java
// === 1. EAGER INITIALIZATION (simple, not lazy) ===
class EagerSingleton {
    private static final EagerSingleton INSTANCE = new EagerSingleton();
    private EagerSingleton() {}
    public static EagerSingleton getInstance() { return INSTANCE; }
}

// === 2. LAZY (NOT thread-safe) ===
class LazySingleton {
    private static LazySingleton instance;
    private LazySingleton() {}
    public static LazySingleton getInstance() {
        if (instance == null) instance = new LazySingleton();
        return instance;
    }
}

// === 3. DOUBLE-CHECKED LOCKING (thread-safe lazy, Java 5+) ===
class DclSingleton {
    private static volatile DclSingleton instance; // volatile MANDATORY
    private DclSingleton() {}
    public static DclSingleton getInstance() {
        if (instance == null) {                    // first check (no lock)
            synchronized (DclSingleton.class) {
                if (instance == null) {            // second check (with lock)
                    instance = new DclSingleton();
                }
            }
        }
        return instance;
    }
}

// === 4. BILL PUGH (holder pattern, best lazy) ===
class HolderSingleton {
    private HolderSingleton() {}
    private static class Holder {
        static final HolderSingleton INSTANCE = new HolderSingleton();
    }
    public static HolderSingleton getInstance() { return Holder.INSTANCE; }
}

// === 5. ENUM SINGLETON (serialization/reflection safe) ===
enum EnumSingleton {
    INSTANCE;
    private String config;
    EnumSingleton() { config = "loaded"; }
    public void doSomething() { System.out.println("Enum singleton: " + config); }
}

// === DEMO ===
public class SingletonDemo {
    public static void main(String[] args) throws Exception {
        // === BASIC USAGE ===
        HolderSingleton s1 = HolderSingleton.getInstance();
        HolderSingleton s2 = HolderSingleton.getInstance();
        System.out.println("Same instance: " + (s1 == s2)); // true

        // === THREAD SAFETY ===
        List<HolderSingleton> instances = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            instances.add(HolderSingleton.getInstance());
        }
        boolean allSame = instances.stream().allMatch(s -> s == instances.get(0));
        System.out.println("All same (thread-safe): " + allSame); // true

        // === ENUM USAGE ===
        EnumSingleton.INSTANCE.doSomething();

        // === BREAKING SINGLETON VIA REFLECTION ===
        EagerSingleton e1 = EagerSingleton.getInstance();
        java.lang.reflect.Constructor<?> ctor = EagerSingleton.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        EagerSingleton e2 = ctor.newInstance(); // second instance!
        System.out.println("Reflection broke it: " + (e1 == e2)); // false

        // === BREAKING VIA SERIALIZATION (without readResolve) ===
        // Deserializing creates new instance unless readResolve() implemented
    }
}
```

Expected output:
```
Same instance: true
All same (thread-safe): true
Enum singleton: loaded
Reflection broke it: false
```

## 4. What Happens Internally

**Class loading and eager initialization:**
```java
class EagerSingleton {
    private static final EagerSingleton INSTANCE = new EagerSingleton();
    // ClassLoader loads EagerSingleton
    // static fields initialized: INSTANCE = new EagerSingleton()
    // JVM guarantees thread-safe class initialization (JLS 12.4.2)
}
```
JVM class initialization is synchronized implicitly. No two threads can initialize the same class concurrently.

**Bill Pugh / Holder pattern:**
```java
class HolderSingleton {
    private static class Holder {
        static final HolderSingleton INSTANCE = new HolderSingleton();
    }
    public static HolderSingleton getInstance() {
        return Holder.INSTANCE; // triggers Holder class loading
    }
}
```
When `getInstance()` is called:
1. `Holder` class not yet loaded → load it
2. `Holder` class initialization: `INSTANCE = new HolderSingleton()`
3. JVM guarantees Holder class init is thread-safe
4. Subsequent calls: `Holder` already loaded → return cached `INSTANCE`

Class loading is lazy (on first `getInstance()` call) and thread-safe (JVM guarantees).

**Double-checked locking with volatile:**
Without `volatile`:
```java
instance = new DclSingleton();
// JVM may reorder as:
// 1. allocate memory
// 2. assign reference to instance
// 3. call constructor
// Thread B sees non-null instance but partially constructed!
```
With `volatile`:
```java
private static volatile DclSingleton instance;
```
`volatile` prevents reordering. Constructor fully executes before `instance` is published.

**Serialization breaking singleton:**
`ObjectInputStream.readObject()` creates a NEW instance via reflection, bypassing constructor. Fix:
```java
private Object readResolve() {
    return INSTANCE; // return existing instance instead of deserialized one
}
```

**Reflection breaking singleton:**
`Constructor.setAccessible(true)` bypasses private constructor. Fix:
```java
private DclSingleton() {
    if (INSTANCE != null) throw new RuntimeException("Use getInstance()");
}
```

## 5. Tricky Interview Cases

**Case 1 — Enum serialization safe**
```java
enum EnumS {
    INSTANCE;
    private int value;
}
```
Output: Deserialization preserves singleton. `INSTANCE == deserialized` is `true`.
Explanation: JVM guarantees enum singletons. `readObject()` returns existing enum constant.

**Case 2 — Cloning breaks singleton**
```java
class CloneableSingleton implements Cloneable {
    private static final CloneableSingleton INSTANCE = new CloneableSingleton();
    private CloneableSingleton() {}
    public static CloneableSingleton getInstance() { return INSTANCE; }
    @Override protected Object clone() throws CloneNotSupportedException {
        return super.clone(); // returns NEW clone!
    }
}
```
Output: `clone()` returns different instance.
Fix: `throw new CloneNotSupportedException()` in `clone()`.

**Case 3 — Multiple classloaders create multiple singletons**
```java
// In Tomcat: each webapp has its own classloader
// Webapp1 classloader loads Config → Config instance A
// Webapp2 classloader loads Config → Config instance B
// A != B — each webapp has its own "singleton"
```
Output: Multiple singletons in application server.
Explanation: Singleton is per-classloader. Not per-JVM. Use JNDI or shared library in parent classloader for true JVM-wide singleton.

**Case 4 — DCL without volatile (broken pre-Java 5)**
```java
class BrokenDcl {
    private static BrokenDcl instance;
    public static BrokenDcl getInstance() {
        if (instance == null) {
            synchronized (BrokenDcl.class) {
                if (instance == null) instance = new BrokenDcl(); // reordered!
            }
        }
        return instance;
    }
}
```
Output: Thread may see non-null `instance` with default values for fields.
Explanation: Without `volatile`, JVM may reorder `instance = new BrokenDcl()`: allocate → assign ref → call constructor. Thread B sees non-null ref but object not constructed.

**Case 5 — Enum vs class-based singleton**
```java
// Class-based: reflection + serialization break it (unless fixed)
// Enum: JVM guarantees single instance, handles serialization natively
```
Output: Enum is safest.
Explanation: Enum constants are JVM-managed singletons. Cannot be instantiated via reflection. Serialization returns same enum constant.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Lazy singleton without synchronization | Multiple instances | Add `synchronized` or use Holder/DCL |
| DCL without `volatile` | Partially constructed object visible | Add `volatile` to instance |
| No `readResolve()` | Deserialization creates new instance | Add `readResolve()` returning existing |
| Not handling reflection | `setAccessible(true)` creates new instance | Throw exception if instance already exists |
| Enum singleton for stateful objects | Enums cannot extend classes, limited use | Use class-based if need inheritance |
| Singleton in multi-classloader env | Each classloader has its own instance | Accept limitation or use JVM-wide registry |

## 7. Production Usage

**Spring beans are singletons by default:**
```java
@Component // singleton scope (default)
public class ConfigLoader {
    // One instance per Spring ApplicationContext
}
```
Spring manages singleton lifecycle. Be careful with `@RequestScope` / `@SessionScope` — these are NOT singletons.

**Database connection pool:**
```java
public class ConnectionPool {
    private static final ConnectionPool INSTANCE = new ConnectionPool();
    private final Queue<Connection> pool = new ArrayDeque<>();
    private ConnectionPool() { /* initialize pool */ }
    public static ConnectionPool getInstance() { return INSTANCE; }
}
```
Eager initialization acceptable for connection pools (need to exist early).

**java.lang.Runtime:**
```java
Runtime rt = Runtime.getRuntime(); // JVM's singleton
long maxMemory = rt.maxMemory();
```
`Runtime.getRuntime()` returns JVM-wide singleton. Demonstrates classic eager singleton pattern.

## 8. Advanced Details

- **Serialization proxy pattern:** Instead of `readResolve()`, `writeReplace()` can return a serializable proxy. More robust for complex objects.
- **Enum singleton limitations:** Cannot be lazy (instantiated at class loading). Cannot extend another class. Cannot implement lazy initialization.
- **OSGi / modular JVM:** Each bundle/module has own classloader. Singleton not guaranteed across bundles.
- **`Runtime.getRuntime()`:** Classic singleton example. Private constructor, static factory, eager init.
- **`ThreadLocal` for per-thread singleton:** If you need one instance per thread, use `ThreadLocal<T>` — not Singleton pattern.

## 9. Interview Questions And Answers

### Beginner
Q: What is the Singleton pattern? How do you implement it in Java?
A: Singleton ensures only one instance of a class exists. Implementation:
```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private Singleton() {}
    public static Singleton getInstance() { return INSTANCE; }
}
```
Private constructor prevents external instantiation. Static field holds single instance. `getInstance()` provides access.

### Intermediate
Q: What is double-checked locking? Why is `volatile` necessary?
A: DCL is a lazy singleton optimization:
```java
if (instance == null) {           // check without lock (fast path)
    synchronized (Lock.class) {
        if (instance == null) {   // check with lock (safe path)
            instance = new Singleton();
        }
    }
}
```
Without `volatile`, the JVM/CPU may reorder instruction in `new Singleton()`: the reference assignment may become visible before the constructor finishes. `volatile` prevents this reordering.

### Senior
Q: enum Singleton is recommended over class-based. Why? What are the tradeoffs?
A: Enum singleton advantages:
1. **Serialization safe**: JVM guarantees single instance across serialization
2. **Reflection safe**: Cannot instantiate enum via `newInstance()`
3. **Thread-safe**: JVM handles enum initialization

Tradeoffs:
1. Not lazy (created at class loading)
2. Cannot extend another class (enums extend `java.lang.Enum`)
3. Cannot implement complex initialization logic
4. Less familiar to some developers

For simple stateless services (strategies, factories): enum is best. For stateful lazy-initialized objects (connection pools, caches): class-based with DCL or Holder pattern.

### Tricky
Q: A colleague implements Singleton using `synchronized` on the entire `getInstance()` method. You suggest double-checked locking. They ask: "Why not just use `synchronized`? Isn't it simpler?" Explain the performance implications and when synchronized-on-method is actually acceptable.
A: `synchronized` on entire method serializes ALL calls, even after instance is initialized. On a hot path called thousands of times per second, this is a scalability bottleneck.

DCL adds lock only during first initialization:
```java
public static Singleton getInstance() {
    if (instance == null) {        // fast path: no lock for 99.9% of calls
        synchronized (...) {
            instance = new Singleton(); // locked only once
        }
    }
    return instance;               // no lock
}
```

When full `synchronized` is acceptable:
- Initialization is rare (e.g., once at startup)
- Performance is not critical
- Simplicity is preferred

When DCL is preferred:
- `getInstance()` called frequently in hot path
- Low latency requirement (lock-free reads after initialization)
- High concurrency environment

Bill Pugh / Holder pattern is even better: zero synchronization overhead, fully lazy, thread-safe by class loading guarantees.

## 10. Final 30-Second Answer

Singleton = one instance, global access. **Eager**: `static final` field. **Lazy + thread-safe**: **Bill Pugh (Holder pattern)** — best. **DCL**: `volatile` instance + double check. **Enum**: serialization/reflection safe, not lazy. Serialization: add `readResolve()`. Reflection: guard constructor. Classloader creates per-classloader instances. Prefer Holder for lazy, Enum for stateless. Avoid `synchronized` on method — contention on hot path. Spring beans singletons by default.