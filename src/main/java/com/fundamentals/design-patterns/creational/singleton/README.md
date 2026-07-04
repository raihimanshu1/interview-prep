# Singleton Pattern

> **Ensures a class has only one instance and provides a global point of access to it.**

## 📖 Concept

**Real-world analogy:** A country can have only one president. No matter who needs the president, they always get the same person.

## 🔍 When to Use

- Need exactly ONE instance of a class (Logger, Config, Connection Pool)
- Global access point needed
- Controlled access to shared resources

## ✅ Interview Checklist

- [ ] Private constructor (prevents external instantiation)
- [ ] Static field holding the single instance
- [ ] Static method returning the instance
- [ ] Thread safety (synchronized / Bill Pugh / Enum)
- [ ] Handle serialization (readResolve)
- [ ] Prevent reflection attack

## 🧪 Common Interview Question

**Problem:** Design a thread-safe Logger that can be used across the entire application. Multiple threads may log simultaneously. Ensure log messages are written in order.

## 💻 Java Implementation

### 1. Bill Pugh Singleton (Best for most cases)

```java
public class Logger {
    private Logger() {}

    private static class LoggerHolder {
        private static final Logger INSTANCE = new Logger();
    }

    public static Logger getInstance() {
        return LoggerHolder.INSTANCE;
    }

    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}
```

### 2. Thread-Safe with Double-Checked Locking

```java
public class Logger {
    private static volatile Logger instance;

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}
```

### 3. Enum Singleton (Safest — prevents reflection & serialization)

```java
public enum Logger {
    INSTANCE;

    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}
```

## 🔧 Full Working Example: Logging System

```java
// Logger.java
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Logger {
    private static volatile Logger instance;
    private PrintWriter writer;

    private Logger() {
        try {
            writer = new PrintWriter(new FileWriter("app.log", true), true);
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize logger", e);
        }
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public void info(String message) {
        writer.println(LocalDateTime.now() + " [INFO] " + message);
    }

    public void error(String message) {
        writer.println(LocalDateTime.now() + " [ERROR] " + message);
    }
}

// Application.java
public class Application {
    public static void main(String[] args) {
        Logger logger1 = Logger.getInstance();
        Logger logger2 = Logger.getInstance();

        logger1.info("Application started");
        logger2.info("User logged in");

        System.out.println(logger1 == logger2); // true — same instance
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Reflection can break singleton | Use Enum OR throw in constructor if instance exists |
| Serialization creates new instance | Implement `readResolve()` |
| Cloning creates new instance | Override `clone()` to throw exception |
| Multi-threading creates multiple instances | Use synchronized / Bill Pugh / Enum |

## 🎯 Related Interview Questions

1. **Design a Configuration Manager** — Singleton that loads config from file once
2. **Design a Database Connection Pool** — Singleton pool managing multiple connections
3. **Design a Cache Manager** — Singleton LRU cache across application
4. **How to break Singleton in Java?** — Reflection, Serialization, Cloning
5. **What is the most secure Singleton implementation?** — Enum Singleton


I will break this like an interview explanation. For each implementation:

1. **Problem we are solving**
2. **Code**
3. **Line-by-line explanation**
4. **How it works with threads**
5. **Pros**
6. **Cons**
7. **When to use**

---

# 1. Basic Singleton (Not Thread Safe)

## Problem

We need only one Logger object.

Example:

```java
Logger logger1 = Logger.getInstance();
Logger logger2 = Logger.getInstance();
```

Expected:

```
logger1
   |
   |
 Logger Object
   |
   |
logger2
```

Both should point to same object.

---

## Code

```java
public class Logger {

    // stores the single object
    private static Logger instance;


    // private constructor
    // nobody can do: new Logger()
    private Logger() {

    }


    // gives access to same object
    public static Logger getInstance() {

        if(instance == null) {

            instance = new Logger();

        }

        return instance;
    }


    public void log(String message){

        System.out.println(message);

    }
}
```

---

## Usage

```java
public class Main {

    public static void main(String[] args) {


        Logger logger1 = Logger.getInstance();

        Logger logger2 = Logger.getInstance();


        logger1.log("Application started");


        System.out.println(logger1 == logger2);

    }
}
```

Output:

```
Application started

true
```

---

## Flow

First call:

```java
Logger logger1 = Logger.getInstance();
```

Inside:

```
instance == null
```

Create:

```
instance
  |
  |
 Logger Object
```

Return it.

Second call:

```
instance != null
```

Return existing object.

---

# Problem With This Code

## Multi-thread issue

Imagine:

Thread 1:

```java
if(instance == null)
```

Thread 2:

```java
if(instance == null)
```

Both see:

```
instance = null
```

Now:

Thread 1:

```java
instance = new Logger()
```

Thread 2:

```java
instance = new Logger()
```

Memory:

```
Thread 1

Logger Object A


Thread 2

Logger Object B
```

Singleton broken.

---

# 2. Synchronized Singleton

## Idea

Lock the method.

Meaning:

Only one thread can enter.

---

## Code

```java
public class Logger {


    private static Logger instance;


    private Logger(){

    }



    public static synchronized Logger getInstance(){


        if(instance == null){

            instance = new Logger();

        }


        return instance;

    }



    public void log(String message){

        System.out.println(message);

    }

}
```

---

# How synchronized works?

Suppose:

Thread 1:

```
getInstance()
        |
        |
       LOCK
```

Thread 2:

```
getInstance()

WAIT
```

Thread 1 finishes:

```
UNLOCK
```

Thread 2 enters.

---

## Example

Thread 1:

```
time 1

instance null

create Logger
```

Thread 2:

```
time 2

waits
```

After:

```
Both get same object
```

---

# Pros

* Very simple
* Thread safe
* Easy to understand

---

# Cons

Every call gets locked.

Example:

1000 requests:

```
Request 1
 lock
 unlock


Request 2
 lock
 unlock


Request 3
 lock
 unlock
```

But after first creation:

```
Object already exists
```

Still locking happens.

Performance hit.

---

# 3. Double Checked Locking

This is the one you asked: "why two locks?"

Actually it is not two locks.

It is two checks.

---

## Problem

We want:

* Thread safety
* No locking after object creation

---

## Code

```java
public class Logger {


    // volatile is important
    private static volatile Logger instance;



    private Logger(){

    }




    public static Logger getInstance(){


        // first check
        if(instance == null){



            synchronized(Logger.class){


                // second check
                if(instance == null){


                    instance = new Logger();


                }


            }

        }


        return instance;

    }



    public void log(String message){

        System.out.println(message);

    }

}
```

---

# Why first check?

```java
if(instance == null)
```

Most calls will stop here.

Example:

After object created:

```
Thread 1

getInstance()

instance != null


return immediately
```

No lock.

Fast.

---

# Why synchronized?

```java
synchronized(Logger.class)
```

Only during creation.

Example:

First time:

```
Thread 1
     |
     |
   lock
     |
 create object
     |
  unlock
```

---

# Why second check?

Important.

Imagine:

Thread 1:

```
first check passed

instance null

waiting for lock
```

Thread 2:

```
first check passed

gets lock

creates object

releases lock
```

Now Thread 1 enters.

Without second check:

```
Thread 1 creates another object
```

Broken.

So:

```java
if(instance == null)
```

inside lock again.

---

# Why volatile?

Without volatile:

Java can reorder:

Normal:

```
1. allocate memory
2. initialize object
3. assign reference
```

CPU may do:

```
1. allocate memory
2. assign reference
3. initialize object
```

Another thread sees:

```
object exists

but not fully created
```

volatile prevents this.

---

# Pros

* Thread safe
* Very fast after creation
* Used commonly

---

# Cons

* Complex
* Need volatile
* Easy to implement incorrectly

---

# 4. Bill Pugh Singleton

This is usually preferred.

---

## Code

```java
public class Logger {


    private Logger(){

    }



    private static class Holder{


        private static final Logger INSTANCE =
                new Logger();

    }




    public static Logger getInstance(){


        return Holder.INSTANCE;

    }




    public void log(String message){

        System.out.println(message);

    }

}
```

---

# How it works?

Java class loading is lazy.

Initially:

```
Logger loaded


Holder NOT loaded
```

When:

```java
getInstance()
```

called:

Java loads:

```
Holder class
```

Then:

```
INSTANCE = new Logger()
```

---

# Why thread safe?

JVM guarantees:

A class is initialized only once.

So:

Thread 1:

```
Holder loads
```

Thread 2:

```
waits
```

No duplicate object.

---

# Pros

* Thread safe
* No synchronized
* Clean
* Fast

---

# Cons

* Slightly less obvious
* Cannot handle reflection attack

---

# 5. Enum Singleton

## Code

```java
public enum Logger {


    INSTANCE;



    public void log(String message){

        System.out.println(message);

    }

}
```

---

Usage:

```java
Logger.INSTANCE.log("Started");
```

---

# What JVM creates?

Internally:

```
Logger

INSTANCE
   |
   |
 Logger Object
```

Only one.

---

# Why safest?

## Reflection

Normal:

```java
constructor.newInstance()
```

can create another object.

Enum:

JVM blocks it.

---

## Serialization

Normal singleton:

```
Write object

Read object

New object created
```

Enum:

JVM returns same INSTANCE.

---

# Pros

* Best protection
* Thread safe
* Serialization safe
* Reflection safe

---

# Cons

* Cannot extend class
* Less flexible
* Not suitable if lazy initialization needed with complex setup

---

# Comparison Table

| Type                   | Thread Safe | Performance | Complexity | Use           |
| ---------------------- | ----------- | ----------- | ---------- | ------------- |
| Basic                  | No          | Fast        | Easy       | Learning only |
| synchronized           | Yes         | Slow        | Easy       | Small apps    |
| Double checked locking | Yes         | Fast        | Medium     | Production    |
| Bill Pugh              | Yes         | Fast        | Easy       | Most common   |
| Enum                   | Yes         | Fast        | Easiest    | Safest        |

---

# Interview Recommendation

For 7 years experience:

Say:

"For most cases I prefer Bill Pugh Singleton because it provides lazy initialization and thread safety using JVM class loading. If I need strongest guarantee against reflection and serialization attacks, I use Enum Singleton."

---

## 🚀 Deep Dive: Advanced Topics

### 1. Class Loading Order in Bill Pugh Singleton

**Question:** When does `INSTANCE` actually get created?

**Answer:** When `getInstance()` is FIRST called, not when `Logger` class is loaded.

#### Java Class Loading Phases:

```
1. Loading     — JVM reads .class file into memory
2. Linking     — Verify, Prepare, Resolve
3. Initialization — Static initializers execute (lazy for nested classes)
```

#### Bill Pugh Breakdown:

```java
public class Logger {
    private Logger() {}  // Step 1: Logger class loaded, but Holder NOT loaded

    private static class LoggerHolder {
        // Step 3: LoggerHolder loaded ONLY when getInstance() called
        private static final Logger INSTANCE = new Logger();
    }

    public static Logger getInstance() {
        // Step 2: First call triggers Holder class loading
        return LoggerHolder.INSTANCE;
    }
}
```

#### Timeline Example:

```
Time 1: Class loading starts
    ↓
Logger class loaded into Metaspace
    ↓
    - private constructor: ready
    - LoggerHolder: NOT YET LOADED (lazy!)
    ↓

Time 2: First getInstance() call
    ↓
LoggerHolder class loads
    ↓
Class initializer runs:
    1. Allocate memory for Logger object
    2. Call Logger constructor (initialize)
    3. Assign reference to INSTANCE
    ↓

Time 3: Second getInstance() call
    ↓
LoggerHolder already initialized
    ↓
Return INSTANCE directly (no new object!)
```

#### Why is it lazy?

Unlike eager singleton:

```java
// EAGER: instance created when Logger class loads
private static final Logger INSTANCE = new Logger();
```

Bill Pugh doesn't create instance until:

```
Someone calls Logger.getInstance()
```

#### Thread Safety Guarantee:

JVM Specification guarantees:
- A class is initialized exactly once
- Initialization is atomic
- All threads see the fully initialized object

```
Thread 1                    Thread 2
    |                          |
getInstance()                getInstance()
    |                          |
LoggerHolder loads           waiting...
    |                       (JVM lock)
INSTANCE created
    |                       LoggerHolder initialized
return INSTANCE              return same INSTANCE
```

---

### 2. Reflection: What It Is and How It Breaks Singleton

**Reflection** = Ability to inspect and modify code at runtime.

**Analogy:** X-ray machine for classes — can see private constructors, private fields, and even call them!

#### How Reflection Works:

```java
import java.lang.reflect.Constructor;

public class ReflectionAttack {
    public static void main(String[] args) throws Exception {
        // Step 1: Get the Class object
        Class<?> clazz = Logger.class;

        // Step 2: Get private constructor
        Constructor<?> constructor = clazz.getDeclaredConstructor();

        // Step 3: Break access control
        constructor.setAccessible(true);

        // Step 4: Create new instance (breaks singleton!)
        Logger instance1 = Logger.getInstance();
        Logger instance2 = (Logger) constructor.newInstance();

        System.out.println(instance1 == instance2); // FALSE!
    }
}
```

#### Step-by-Step Explanation:

```
Normal way (blocked):
    new Logger()  →  Compile error: constructor is private

Reflection way (bypasses):
    1. Get Class<Logger> object
    2. Get private Constructor object
    3. constructor.setAccessible(true)  ← KEY: bypasses access checks!
    4. constructor.newInstance()  ← Creates NEW object
```

#### Memory After Reflection Attack:

```
Before:
LoggerHolder.INSTANCE → Logger Object A

After reflection:
LoggerHolder.INSTANCE → Logger Object A
constructor.newInstance() → Logger Object B  (DIFFERENT!)

instance1 → Object A
instance2 → Object B
instance1 == instance2 → false  (SINGLETON BROKEN!)
```

#### Prevention 1: Throw in Constructor

```java
public class Logger {
    private static volatile Logger instance;

    private Logger() {
        // Check if instance already exists
        if (instance != null) {
            throw new RuntimeException(
                "Use getInstance() to get Logger instance"
            );
        }
        // ... normal init
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }
}
```

**How it prevents:**

```
Reflection tries to create instance:
    1. constructor.setAccessible(true)
    2. constructor.newInstance()
       ↓
    new Logger() called
       ↓
    Check: instance != null?
       ↓
    YES (getInstance() already called)
       ↓
    Throw RuntimeException
```

#### Prevention 2: Use Enum (Best Solution)

```java
public enum Logger {
    INSTANCE;

    public void log(String message) {
        System.out.println(message);
    }
}
```

**Why Enum blocks reflection:**

```
JVM internally:
    1. Enum values created during class initialization
    2. Constructor marked special by JVM
    3. Reflection API checks: is this an enum?
    4. If YES → throw IllegalArgumentException
    5. Cannot bypass even with setAccessible(true)
```

---

### 3. Serialization: How It Breaks Singleton

**Serialization** = Converting object to bytes to save/send.

#### How Serialization Breaks:

```java
import java.io.*;

public class SerializationAttack {
    public static void main(String[] args) throws Exception {
        // Step 1: Get instance
        Logger instance1 = Logger.getInstance();

        // Step 2: Serialize to file
        ObjectOutput out = new ObjectOutputStream(
            new FileOutputStream("logger.ser")
        );
        out.writeObject(instance1);

        // Step 3: Deserialize from file
        ObjectInput in = new ObjectInputStream(
            new FileInputStream("logger.ser")
        );
        Logger instance2 = (Logger) in.readObject();

        System.out.println(instance1 == instance2); // FALSE!
    }
}
```

#### What Happens During Deserialization:

```
readObject() internally:
    1. Creates NEW object via reflection
    2. Bypasses constructor (no constructor call!)
    3. Returns fresh instance
```

#### Prevention: readResolve()

```java
public class Logger implements Serializable {
    private static volatile Logger instance;

    private Logger() {}

    public static Logger getInstance() {
        // ... DCL code
    }

    // Called after deserialization
    protected Object readResolve() {
        // Return existing instance, not new one!
        return getInstance();
    }
}
```

**How readResolve works:**

```
Deserialization process:
    1. Create new object from bytes
    2. Call readResolve() method (if exists)
    3. Replace new object with returned object
    4. Final result: existing singleton instance
```

---

### 4. Enum Singleton: Internal Working

#### How Enum Works Internally:

```java
public enum Logger {
    INSTANCE;  // This is actually a public static final field!
}
```

JVM compiles this to something like:

```java
public final class Logger extends java.lang.Enum<Logger> {
    public static final Logger INSTANCE;
    
    static {
        // Static initializer block
        INSTANCE = new Logger("INSTANCE", 0);
        
        // $VALUES array for Enum methods
        $VALUES = new Logger[] { INSTANCE };
    }
}
```

#### When is Enum Instance Created?

```
Class Loading Phase:
    1. Logger class loaded
    2. <clinit> (class initializer) runs
    3. INSTANCE = new Logger()  ← Created ONCE here
    4. Class initialization complete

Thread Safety:
    - JVM guarantees <clinit> runs exactly once
    - Happens before any thread accesses Logger
    - JVM holds lock during class initialization
```

#### Accessing Enum Singleton:

```java
// This is what you write:
Logger logger = Logger.INSTANCE;

// JVM does something like:
Logger logger = Logger.INSTANCE;  // Just a static field access!
```

**No method call needed** — it's just accessing a static final field.

#### Complete Enum Example:

```java
public enum Logger {
    // Only one element created by JVM
    INSTANCE;

    // Can have fields
    private PrintWriter writer;

    // Can have methods
    public void init() {
        try {
            writer = new PrintWriter(new FileWriter("app.log"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void info(String message) {
        writer.println("[INFO] " + message);
    }

    public void error(String message) {
        writer.println("[ERROR] " + message);
    }
}

// Usage:
public class Application {
    public static void main(String[] args) {
        // Access singleton
        Logger logger = Logger.INSTANCE;
        logger.init();
        logger.info("Application started");

        // Same instance everywhere:
        Logger sameLogger = Logger.INSTANCE;
        System.out.println(logger == sameLogger); // true
    }
}
```

#### Enum Serialization Guarantee:

```
Deserialization of Enum:
    1. JVM checks: is this an enum?
    2. Don't create new instance
    3. Return existing enum constant
    4. Result: same INSTANCE object
```

From JVM Spec:
> "During deserialization, the object is resolved to the enum constant."

#### Enum Reflection Protection:

```java
public enum Logger {
    INSTANCE
}

// Trying to break via reflection:
public class Attack {
    public static void main(String[] args) throws Exception {
        Constructor<?> constructor = Logger.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        // THIS WILL FAIL:
        Logger newInstance = (Logger) constructor.newInstance();
        // java.lang.IllegalArgumentException: 
        // Cannot reflectively create enum objects
    }
}
```

**Why JVM blocks it:**

```
JVM adds special check in Enum Constructor:
    if (<clinit> already ran) {
        throw new IllegalArgumentException(
            "Cannot reflectively create enum objects"
        );
    }
    
    // And <clinit> locks the class permanently
```

---

### 5. Complete Singleton Comparison Matrix

| Aspect | Bill Pugh | Enum |
|--------|-----------|------|
| **Instance creation** | Lazy (on first `getInstance()` call) | Eager (on class load) |
| **Thread safety** | Yes (JVM class loading) | Yes (JVM enum init) |
| **Performance** | Fast (no locking) | Fast (static field) |
| **Serialization safe** | No (need `readResolve()`) | Yes (built-in) |
| **Reflection safe** | No (can break) | Yes (JVM blocks) |
| **Complexity** | Medium | Low |
| **Lazy initialization** | Yes | No |
| **Can implement interface** | Yes | No (extends Enum) |
| **Can lazy load resources** | Yes | No |
| **Memory footprint** | Low | Low |

#### Visual Diagram: Class Loading Order

```
Bill Pugh Timeline:
┌─────────────────────────────────────────────────────┐
│ 1. Application starts                               │
│    Logger.class loaded into Metaspace               │
└─────────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────┐
│ 2. LoggerHolder NOT loaded yet (lazy!)              │
│    INSTANCE does NOT exist                          │
└─────────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────┐
│ 3. Thread A calls getInstance()                     │
│    → LoggerHolder class loads                        │
│    → <clinit> runs: INSTANCE = new Logger()         │
└─────────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────┐
│ 4. Thread B calls getInstance()                     │
│    → LoggerHolder already initialized               │
│    → Return same INSTANCE                           │
└─────────────────────────────────────────────────────┘

Enum Timeline:
┌─────────────────────────────────────────────────────┐
│ 1. Application starts                               │
│    Logger.class loaded into Metaspace               │
└─────────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────┐
│ 2. <clinit> runs IMMEDIATELY during class loading   │
│    INSTANCE = new Logger()                          │
│    (No lazy loading - always eager)                 │
└─────────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────┐
│ 3. Any thread accesses Logger.INSTANCE              │
│    → Returns already-created instance               │
└─────────────────────────────────────────────────────┘
```

---

### 6. Advanced Interview Questions

#### Q1: Which singleton should I use?

**Answer:**
| Use Case | Recommendation |
|-----------|---------------|
| General purpose | Bill Pugh (lazy, clean, fast) |
| Security critical | Enum (unbreakable) |
| Complex initialization | Bill Pugh with `readResolve()` |
| Eager loading OK | Enum (simplest) |
| Legacy system | Synchronized (simple, works) |

#### Q2: How to make Bill Pugh thread-safe?

**Answer:** Bill Pugh IS thread-safe by JVM specification. The JLS (Java Language Specification) guarantees:
- Class initialization is synchronized
- Happens-before relationship for all threads
- No additional `volatile` or `synchronized` needed

#### Q3: What if I need parameters in singleton?

**Problem:**
```java
// Can't pass parameters:
Logger config = new Logger("config.properties"); // Constructor is private!
```

**Solutions:**

```java
// Solution 1: Pass in getInstance()
public class Logger {
    private static volatile Logger instance;
    private String configFile;

    private Logger(String configFile) {
        this.configFile = configFile;
    }

    public static synchronized Logger getInstance(String configFile) {
        if (instance == null) {
            instance = new Logger(configFile);
        }
        return instance;
    }
}

// Solution 2: Initialization method
public class Logger {
    private static volatile Logger instance;
    private String configFile;

    private Logger() {}

    public static Logger getInstance() {
        return instance;
    }

    public synchronized Logger init(String configFile) {
        this.configFile = configFile;
        return this;
    }
}

// Usage:
Logger logger = Logger.getInstance("config.properties");
```

#### Q4: Singleton in Spring Framework

**Spring beans are singletons by default:**

```java
@Component  // Singleton scope (default)
public class Logger {
    // Spring creates ONE instance and manages it
}

@Service
public class Application {
    private final Logger logger;  // Same instance injected everywhere

    @Autowired
    public Application(Logger logger) {
        this.logger = logger;
    }
}
```

**Spring Singleton vs Classic Singleton:**

| Spring Singleton | Classic Singleton |
|-----------------|-------------------|
| One instance per Spring container | One instance per JVM |
| Managed by Spring | Self-managed |
| Can have dependencies injected | Manual dependency management |
| Easy to test (mockable) | Harder to test |

#### Q5: Can we clone Singleton?

**Yes, if we don't override `clone()`:**

```java
public class Logger implements Cloneable {
    // ... singleton code

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); // Creates duplicate!
    }
}

// Attack:
Logger obj1 = Logger.getInstance();
Logger obj2 = (Logger) obj1.clone(); // NEW OBJECT!
```

**Prevention:**

```java
@Override
protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("Cannot clone singleton");
}
```

---

### 7. Memory Model & Visibility

#### volatile Keyword Deep Dive:

```java
private static volatile Logger instance;
```

**Without volatile:**
```
Thread 1 writes:
    1. Allocate object in heap
    2. Set instance = objectRef
    3. Initialize object fields

Thread 2 reads:
    sees instance != null  (Step 2 done)
    tries to use object
    BUT fields not initialized yet! (Step 3 not done)
    → NullPointerException or corrupted state
```

**With volatile:**
```
Thread 1 writes:
    1. Allocate object
    2. Initialize object
    3. volatile write to instance (memory barrier)
    
Thread 2 reads:
    volatile read from instance (memory barrier)
    sees fully initialized object
```

#### Double-Checked Locking Correctness:

```
First check (no lock):
    instance == null
    ↓
Synchronized block:
    Lock acquired
    ↓
Second check:
    instance == null (could have changed!)
    ↓
Create instance
    ↓
volatile write (happens-before)
    ↓
Unlock
```

---

## ✅ Summary

**Bill Pugh Key Points:**
1. Lazy initialization via nested static class
2. Thread-safe due to class loading guarantees
3. No synchronized needed (better performance)

**Reflection Attack:**
1. Can access private constructors via `setAccessible(true)`
2. Prevents: Runtime check in constructor OR use Enum

**Enum Internal:**
1. Compiled to class with static final field
2. Instance created in `<clinit>` (class initializer)
3. JVM guarantees single initialization
4. Reflection blocked at JVM level

**When to use which:**
- Bill Pugh: Most cases (lazy, fast, clean)
- Enum: Maximum security needed (unbreakable)
