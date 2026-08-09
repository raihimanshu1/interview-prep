# Module 2b — Class Loading & Execution Engine — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---

## Q1. Explain class loading in Java — delegation model and breaking it.

### 1. Why This Concept Matters
ClassLoader issues cause NoClassDefFoundError, ClassNotFoundException, and weird "class version mismatch" errors. Understanding class loading is essential for debugging dependency conflicts, OSGi/Java module system, and web application deployment. Interviewers ask this to test your understanding of **how Java isolates and loads code**.

### 2. Basic Meaning
**ClassLoader**: Component that loads `.class` files into the JVM. It reads bytecode, defines the class, and returns a `Class<?>` object.

**Parent Delegation Model**: A ClassLoader first asks its parent to load a class before attempting to load it itself.

### 3. Real Code / Real Example

```java
// =====================================================
// CLASS LOADER HIERARCHY
// =====================================================

// Print which ClassLoader loaded which class:
public class ClassLoaderDemo {
    public static void main(String[] args) {
        // Bootstrap ClassLoader (null) — loads core Java classes
        System.out.println(String.class.getClassLoader());   // null
        System.out.println(Integer.class.getClassLoader());    // null
        
        // Platform ClassLoader — loads JDK modules
        System.out.println(DriverManager.class.getClassLoader());  // platform class loader
        
        // Application ClassLoader — loads your code
        System.out.println(ClassLoaderDemo.class.getClassLoader());  // AppClassLoader
        
        // Parent chain:
        ClassLoader appLoader = ClassLoaderDemo.class.getClassLoader();
        System.out.println(appLoader);                    // AppClassLoader
        System.out.println(appLoader.getParent());        // PlatformClassLoader
        System.out.println(appLoader.getParent().getParent());  // null (Bootstrap)
    }
}
```

**Expected output:**
```
null
null
platform class loader
jdk.internal.loader.ClassLoaders$AppClassLoader@...
jdk.internal.loader.ClassLoaders$PlatformClassLoader@...
null
```

### 4. What Happens Internally

**loadClass() method — the delegation algorithm:**
```java
protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // 1. Check if already loaded (cache check)
    Class<?> c = findLoadedClass(name);
    if (c != null) return c;
    
    // 2. Delegate to parent (if parent exists)
    if (parent != null) {
        c = parent.loadClass(name, false);  // RECURSIVE!
    } else {
        c = findBootstrapClassOrNull(name);  // Bootstrap ClassLoader
    }
    
    // 3. If parent didn't find it, try to load it ourselves
    if (c == null) {
        c = findClass(name);  // findClass() reads .class bytes
    }
    
    // 4. Return the class
    return c;
}
```

**Why delegation?**
- **Security**: Core Java classes (java.lang.String) are ALWAYS loaded by Bootstrap ClassLoader
- **Uniqueness**: A class is identified by (FQN + ClassLoader instance)
- **Consistency**: Prevents multiple versions of the same class

**Breaking delegation — why?**
- Application servers (Tomcat, JBoss): each webapp has its own ClassLoader
- Doesn't delegate for webapp classes → allows multiple versions of same library
- `Thread.currentThread().getContextClassLoader()` — for framework code that needs to find application classes

### 5. Tricky Interview Cases

**Case 1: ClassNotFoundException vs NoClassDefFoundError**
```java
// ClassNotFoundException — Class.forName() failed
try {
    Class.forName("com.missing.Class");  // Class NOT on classpath
} catch (ClassNotFoundException e) {
    // The class was NEVER available
}

// NoClassDefFoundError — compiled against the class but not found at runtime
public class UsesMissingLibrary {
    // Compiles fine because library was on classpath during compilation
    private MissingClass obj;  // Class present at compile time
    
    public void test() {
        obj = new MissingClass();  // NoClassDefFoundError at runtime!
        // Library JAR was removed or not included in deployment
    }
}
// KEY: CNFE = never available. NCDFE = was available at compile time, missing at runtime
```

**Case 2: ClassLoader leak in application servers**
```java
// When redeploying a webapp:
// 1. Old ClassLoader should be GC'd → old classes unloaded
// 2. But if something holds reference to old ClassLoader → MEMORY LEAK!
// Common causes:
//   - ThreadLocal with no remove() (thread pool keeps reference)
//   - Static field referencing application class
//   - Logging framework that holds ClassLoader reference
// Result: OutOfMemoryError: Metaspace after several redeployments
```

**Case 3: Custom ClassLoader for encrypted classes**
```java
// Hypothetical encrypted-class loader:
public class EncryptedClassLoader extends ClassLoader {
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] encrypted = readEncryptedClass(name);
        byte[] decrypted = decrypt(encrypted);
        return defineClass(name, decrypted, 0, decrypted.length);
        // defineClass() is the KEY method that creates Class<?> from bytes
    }
}
// This breaks delegation because we override findClass(), not loadClass()
// loadClass() still does parent delegation first!
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Confusing ClassNotFoundException and NoClassDefFoundError | Wrong debugging approach | CNFE = class path issue; NCDFE = runtime dependency missing |
| Forgetting Thread Context ClassLoader in frameworks | Can't find application classes | Use Thread.currentThread().getContextClassLoader() |
| Not calling super.loadClass() in custom ClassLoader | Breaks delegation model | Override findClass(), not loadClass() |
| Static field holding ClassLoader reference | Memory leak on redeploy | Never store ClassLoader in static fields |

### 7. Production Usage

**Context ClassLoader in Spring/Java SPI:**
```java
// ServiceLoader uses context ClassLoader
public static <S> ServiceLoader<S> load(Class<S> service) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return new ServiceLoader<>(service, cl);
}
// Framework code can't use its own ClassLoader (it's in a JAR)
// Context ClassLoader bridges to the application's classes
```

### 8. Advanced Details

**Java 9+ modules vs ClassLoaders:**
```
Java 8: rt.jar + ClassPath → ClassLoaders
Java 9+: Module path + class path → still uses ClassLoaders internally
         But strong encapsulation limits reflective access
         
Bootstrap ClassLoader → loads java.base module
Platform ClassLoader  → loads other JDK modules  
Application ClassLoader → loads application modules/classpath
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What is the parent delegation model in class loading?

**A**: When a ClassLoader needs to load a class, it first asks its parent ClassLoader. The parent asks its parent, all the way up to Bootstrap ClassLoader. Only if all parents fail does the original ClassLoader attempt to load it. This ensures core Java classes (java.lang.String) are always loaded by the Bootstrap ClassLoader, preventing malicious code from replacing them.

#### Intermediate

**Q**: How would you load a class from a byte array?

**A**: Use a custom ClassLoader that overrides findClass(). Call defineClass(name, bytes, 0, bytes.length) which creates a Class<?> from raw bytecode. This is how JSP engines work: they compile JSP files to bytecode, then load them via custom ClassLoaders. The key method is defineClass() — the native method that actually creates the Class object.

#### Senior

**Q**: Diagnose: after 5 redeployments of a webapp, you get OutOfMemoryError: Metaspace.

**A**: This is a ClassLoader leak. Each redeploy creates a new ClassLoader (per webapp). If the old ClassLoader can't be GC'd, its loaded classes stay in Metaspace forever. Typical causes: (1) ThreadLocal not calling remove() — the thread pool thread holds a reference to the old webapp class; (2) Logging/shutdown hooks referencing webapp classes; (3) Quartz schedulers not stopped. Fix: (1) Always remove() ThreadLocal values; (2) Register cleanup via ServletContextListener; (3) Use -XX:+TraceClassUnloading to verify classes are unloaded.

#### Tricky

**Q**: Can two classes with the same fully qualified name exist in the JVM?

**A**: Yes — if they're loaded by different ClassLoaders. In application servers like Tomcat, two webapps can both have com.example.MyService — each loaded by its own WebappClassLoader. They are COMPLETELY different types at runtime: myService1.getClass() != myService2.getClass(). This causes ClassCastException even though class names are identical. This is why frameworks warn against sharing static singletons across applications.

### 10. Final 30-Second Answer

ClassLoaders use parent delegation: ask parent first, load if parent fails. Prevents core class replacement. Custom loaders override findClass(), not loadClass(). ClassLoader leaks cause Metaspace OOM on redeploy. Two classes with same name loaded by different loaders are distinct types.