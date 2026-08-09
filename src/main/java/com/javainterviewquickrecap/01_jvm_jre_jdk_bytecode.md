# Module 1a — JVM, JRE, JDK & Bytecode — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied to every answer.
> Every Q&A covers: Best case | Average case | Worst case | Version differences | Edge cases | Internal implementation | Trade-offs | Common mistakes | Production example | Anticipated follow-ups.


Language Basics
JVM, JRE, JDK
Compilation pipeline
Bytecode
javac
javap
Class file structure
Class loading overview

---

## Q1. What is the difference between JDK, JRE, and JVM?

### 1. Why This Concept Matters
Without understanding the JDK/JRE/JVM boundary, you can't diagnose build failures, runtime errors, or deployment issues. In production, an app that compiles fine on your machine (JDK 17) but crashes on the server (JRE 8) is the #1 deployment mistake. Interviewers ask this to test if you know the **separation of concerns** in Java's architecture.

### 2. Basic Meaning

| Term | Full Form | What It IS | What It CONTAINS |
|------|-----------|-----------|-----------------|
| **JVM** | Java Virtual Machine | The **engine** that executes bytecode | Heap, Stack, Metaspace, Execution Engine (Interpreter + JIT) |
| **JRE** | Java Runtime Environment | The **runtime** needed to RUN Java apps | JVM + Core Libraries (rt.jar, java.lang, java.util, etc.) |
| **JDK** | Java Development Kit | The **toolkit** needed to DEVELOP Java apps | JRE + Development Tools (javac, jar, javap, jlink, javadoc) |

**Analogy**: Car manufacturing:
- **JVM** = The engine (does the actual work)
- **JRE** = The car without tools (you can drive, but can't fix it)
- **JDK** = The car + full mechanic's toolkit (you can build, fix, and drive)

### 3. Real Code / Real Example

```java
// =====================================================
// Understanding the pipeline through code
// =====================================================

// Step 1: You write this source file (uses JDK tools to compile)
// File: Hello.java
public class Hello {
    public static void main(String[] args) {
        // This runs on JVM — no JDK needed at runtime
        System.out.println("Hello, JVM!");
    }
}
```

```bash
# =====================================================
# Step 2: Compilation (JDK required — javac is JDK-only)
# =====================================================
javac Hello.java
# Produces: Hello.class (bytecode)

# =====================================================
# Step 3: Run (Only JRE/JVM required — javac not needed)
# =====================================================
java Hello
# Output: Hello, JVM!

# Verify the class file format:
javap -verbose Hello.class
# Shows: major_version=61 (Java 17), magic=0xCAFEBABE
```

**Expected output**:
```
Hello, JVM!

// javap output snippet:
// Classfile /Users/me/Hello.class
//   Last modified Jul 26, 2024
//   SHA-256 checksum ...
//   Compiled from "Hello.java"
// public class Hello
//   minor version: 0
//   major version: 61   ← Java 17
//   flags: (0x0021) ACC_PUBLIC, ACC_SUPER
//   this_class: #7                  // Hello
//   super_class: #2                 // java/lang/Object
```

### 4. What Happens Internally

```
Source (.java) ──[javac]──> Bytecode (.class) ──[ClassLoader]──> JVM Runtime
                                                                    │
                         ┌──────────────────────────────────────────┘
                         ▼
                    Execution Engine
                    ┌─────────────────┐
                    │   Interpreter   │  ← First run: interprets bytecode line-by-line
                    │   (fast start)  │     Counts "hot" method invocations
                    └────────┬────────┘
                             │ "Hot" threshold reached (~10,000 calls)
                             ▼
                    ┌─────────────────┐
                    │   JIT Compiler  │  ← Compiles bytecode → native machine code
                    │   (fast exec)   │     Cached in Code Cache
                    └─────────────────┘
```

**Step-by-step at runtime:**
1. `java Hello` → JVM process starts
2. Bootstrap ClassLoader loads core classes (`java.lang.*`)
3. Application ClassLoader loads `Hello.class` from classpath
4. Bytecode Verifier checks: no illegal bytecode, no stack overflow, valid types
5. `main()` method executes: interpreter runs first
6. If `main()` loops enough → JIT compiles to native code

### 5. Tricky Interview Cases

**Case 1: What if you only have JRE but try to compile?**
```bash
# ❌ This will fail:
javac Hello.java
# Error: javac: command not found
# JRE doesn't include javac! Only JDK has it.
```

**Case 2: Can you run a Java 17 compiled class on JRE 8?**
```bash
# Compile with Java 17:
javac --release 17 Hello.java  # major_version = 61

# Run on JRE 8:
java -version  # java version "1.8.0_291"
java Hello
# Output: java.lang.UnsupportedClassVersionError:
#   Hello has been compiled by a more recent version of the Java Runtime
#   (class file version 61.0), this version of the Java Runtime
#   only recognizes class file versions up to 52.0
```

**Case 3: What if you compile with `--release 8` on JDK 17?**
```bash
# Compile targeting older JVM:
javac --release 8 Hello.java  # major_version = 52.0

# Now it runs on JRE 8! ✅
java Hello  # Works fine!
# But you can't use Java 17+ features (records, sealed classes, etc.)
```

**Case 4: Modular JDK (Java 9+) — No more rt.jar**
```bash
# Java 8 location:
ls $JAVA_HOME/jre/lib/rt.jar  # Exists in Java 8

# Java 17 location:
ls $JAVA_HOME/jre/lib/rt.jar  # Does NOT exist — Java 9 removed it!
# Instead:
ls $JAVA_HOME/lib/jrt-fs.jar  # JRT filesystem for modules
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Installing only JRE and trying to compile | javac not found, build fails | Install JDK instead of JRE |
| Running app compiled with newer JDK on older JRE | `UnsupportedClassVersionError` | Compile with `--release <target>` or match versions |
| Assuming JDK includes every JRE feature | Some JRE configurations differ (e.g., compact profiles in Java 8) | Test on target runtime |
| Thinking JVM is just a "virtual machine" | JVM does execution optimization, garbage collection, thread management — much more | Study JVM internals beyond the abstract spec |

### 7. Production Usage

**Docker multi-stage build pattern:**
```dockerfile
# Stage 1: Build with JDK (heavy — includes compiler, tools)
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

# Stage 2: Run with JRE (lightweight — only runtime)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
# JRE is ~50% smaller than JDK — critical for Docker images
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Production monitoring:**
```bash
# Check which JVM is running your app:
ps -ef | grep java
# Output: /usr/lib/jvm/java-17-openjdk/bin/java -jar app.jar

# Check class file version of a compiled dependency:
javap -verbose some-dependency.jar | grep major_version
```

### 8. Advanced Details

**Version compatibility matrix:**

| Compiled On | Can Run On | Notes |
|------------|-----------|-------|
| JDK 8 (52) | JRE 8, 11, 17, 21 | ✅ Always forward-compatible |
| JDK 11 (55) | JRE 11, 17, 21 | ✅ Forward-compatible |
| JDK 17 (61) | JRE 17, 21 | ✅ Forward-compatible |
| JDK 17 (61) | JRE 11 | ❌ `UnsupportedClassVersionError` |
| JDK 21 (65) | JRE 17 | ❌ `UnsupportedClassVersionError` |

**Size comparison (Docker images):**
```
eclipse-temurin:17-jdk    → ~450MB (includes javac, javadoc, etc.)
eclipse-temurin:17-jre    → ~200MB (~55% smaller!)
eclipse-temurin:17-alpine → ~150MB (even smaller, musl libc)
```

**JVM implementations beyond HotSpot:**
- **HotSpot** (Oracle JDK, OpenJDK) — most common, default since Java 1.3
- **GraalVM** — polyglot, AOT compilation, native images
- **J9/OpenJ9** (IBM) — optimized for cloud, smaller memory footprint

### 9. Interview Questions And Answers

#### Beginner

**Q**: What is the difference between JRE and JDK?

**A**: JDK = JRE + Development Tools (javac, javap, jar, jlink). JRE = JVM + Core Libraries (for running Java apps only). You need JDK to compile code; JRE is enough to run pre-compiled code. In Docker, you build with JDK but run with JRE to keep images small.

#### Intermediate

**Q**: Can you run a program compiled with Java 17 on Java 11? What about the reverse?

**A**: You **cannot** run Java 17 bytecode on JRE 11 — you get `UnsupportedClassVersionError` because the class file version (61) exceeds what JRE 11 supports (55). But you **can** compile with `--release 11` on JDK 17, producing version 55 bytecode that runs on both JRE 11 and JRE 17. The reverse (Java 11 bytecode on JRE 17) always works — forward compatibility.

#### Senior

**Q**: You're deploying a Java microservice. What factors influence your choice between using the full JDK vs JRE in the runtime container?

**A**: In production, I **always use JRE** in runtime containers, not JDK. Reasons: (1) **Security** — fewer tools means smaller attack surface; (2) **Image size** — JRE is ~55% smaller, reducing pull time and storage costs; (3) **Resource usage** — JDK tools like javac are never used at runtime. However, I need JDK in the **build stage** for compilation. I use multi-stage Docker builds: JDK to compile, JRE to run. One exception: if the app uses runtime compilation (e.g., JSP, dynamic proxy generation), JDK might be needed.

#### Tricky

**Q**: Java 9 removed the JRE distribution. How do you run Java apps now without a JDK?

**A**: Starting with Java 9, Oracle stopped shipping a standalone JRE. Instead, you can: (1) Use `jlink` to create a **custom runtime image** containing only the modules your app needs — this can be smaller than the old JRE; (2) Use third-party JRE distributions like Eclipse Temurin, Amazon Corretto, or Adoptium which still provide separate JRE and JDK builds; (3) Use the full JDK — many DevOps teams do this anyway since the size difference is acceptable with modern infrastructure. The `jlink` approach is production-best: `jlink --add-modules java.base,java.sql --output custom-jre` creates a ~40MB JRE for a simple app.

### 10. Final 30-Second Answer

JDK = JRE + tools for development. JRE = JVM + libraries for running. JVM executes bytecode with an interpreter (fast start) and JIT (fast execution). Newer class file versions can't run on older JREs. Docker best practice: build with JDK, run with JRE.

---

## Q2. Explain the Java compilation and execution pipeline step by step.

### 1. Why This Concept Matters
Understanding the pipeline helps debug issues at each stage: compile errors in `.java` → `javac`, class not found at runtime → ClassLoader, slow startup → Interpreter, slow after warmup → JIT. Interviewers ask this to see if you understand the **full lifecycle** from source to execution.

### 2. Basic Meaning

```
[Source Code] ──javac──> [Bytecode] ──ClassLoader──> [Runtime] ──Execution Engine──> [Machine Code]
   Hello.java           Hello.class                     JVM                 Native
```

**4 stages:**
1. **Compilation** (javac): `.java` → `.class` (bytecode)
2. **Loading** (ClassLoader): `.class` → JVM memory
3. **Verification**: Bytecode checked for safety
4. **Execution** (Interpreter + JIT): Bytecode → Machine code

### 3. Real Code / Real Example

```java
// File: Calculator.java
public class Calculator {
    // Simple method to trace through pipeline
    public int add(int a, int b) {
        return a + b;  // Single bytecode instruction: iadd
    }
    
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        // First call: INTERPRETED (slow)
        // 10,001st call: JIT COMPILED (fast)
        for (int i = 0; i < 100_000; i++) {
            calc.add(i, i+1);
        }
    }
}
```

```bash
# Step 1: Compile
javac Calculator.java
# Creates: Calculator.class

# Step 2: View bytecode
javap -c Calculator
# Output:
# public int add(int, int);
#     Code:
#        0: iload_1        // Push 'a' onto stack
#        1: iload_2        // Push 'b' onto stack  
#        2: iadd           // Pop both, add, push result
#        3: ireturn        // Return top of stack

# Step 3: Run with JIT logging
java -XX:+PrintCompilation Calculator
# Output shows JIT compiling add() after ~10,000 calls:
#     68   17       3       Calculator::add (4 bytes)
```

### 4. What Happens Internally

```
Phase 1: COMPILATION (by javac)
────────────────────────────────
Source: int add(int a, int b) { return a + b; }
  │
  1. Lexical Analysis: Tokenizes into int, add, (, int, a, ... 
  2. Parsing: Builds Abstract Syntax Tree (AST)
  3. Semantic Analysis: Type checking (a and b are int, + is valid)
  4. Bytecode Generation: iload_1, iload_2, iadd, ireturn
  │
  ▼
.class file written

Phase 2: LOADING (by ClassLoader) — happens lazily
────────────────────────────────
  1. First reference to Calculator triggers loading
  2. Parent delegation: App ClassLoader → Platform → Bootstrap
  3. Bootstrap doesn't have it → Platform doesn't → App loads it
  4. defineClass() reads .class bytes, creates Class<?> object
  │
  ▼
Phase 3: LINKING (Verification + Preparation + Resolution)
────────────────────────────────
  - Verify: No illegal bytecode, valid stack maps
  - Prepare: Static fields get default values (0 for int)
  - Resolve: Symbolic references → direct references (String → actual address)

Phase 4: EXECUTION
────────────────────────────────
  First call: Interpreter runs bytecode (SLOW but no delay)
  After threshold (~10K calls): JIT compiles to NATIVE code
    - Inlines add() — removes method call overhead
    - Result: CPU executes x86 ADD instruction directly
```

### 5. Tricky Interview Cases

**Case 1: What happens when a class fails verification?**
```java
// Hypothetical malicious bytecode:
// Stack: [int, Object]
// Instruction: iadd  ← Would fail! Stack has int + Object
// But this can't happen from normal javac output
```

**Case 2: `-XX:-TieredCompilation` — force pure interpreter**
```bash
java -XX:-TieredCompilation Calculator
# Runs ENTIRELY in interpreter — NO JIT
# ~10x slower for long-running methods
# Useful for debugging JIT-related issues
```

**Case 3: What if code is only run once?**
```java
// If add() is called only ONCE, it stays INTERPRETED forever
// JIT never triggers — no compilation overhead, but also no optimization
Calculator calc = new Calculator();
int result = calc.add(3, 4);  // Only call → stays interpreted
System.out.println(result);   // 7 — correct, just slower
```

**Case 4: `-Xbatch` — force synchronous JIT compilation**
```bash
java -Xbatch -XX:+PrintCompilation Calculator
# Normal: JIT compiles in background threads (async)
# -Xbatch: JIT compiles in foreground (sync) — slower startup
# Useful for deterministic behavior in benchmarks
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Thinking javac is needed at runtime | Add JDK to Docker image unnecessarily | Use multi-stage build: JDK to compile, JRE to run |
| Assuming bytecode is exactly the same across JDK versions | Different javac versions produce slightly different bytecode | Compile with `--release` for cross-version compatibility |
| Not understanding JIT warmup | Benchmarking cold JVM gives misleading results | Run warmup iterations before measuring (typically 10K+) |
| Confusing class loading with class initialization | Class may be loaded but not initialized (static blocks not run) | Use `-XX:+TraceClassLoading` vs `-XX:+TraceClassInitialization` |

### 7. Production Usage

**Monitoring JIT compilation in production:**
```bash
# Log JIT compilation to file (Java 8)
-XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation

# Java 9+ unified logging:
-Xlog:jit+compilation=debug:file=jit.log
```

**Detecting warmup issues:**
```bash
# Run with tiered compilation logging:
java -XX:+PrintCompilation -jar app.jar

# Look for:
# 128   1       3       com.example.service.OrderService::processOrder (42 bytes)
# ^   ^ ^       ^       ^
# |   | |       |       └── Method being compiled
# |   | |       └── Compiler level (3 = C1 with profiling)
# |   | └── Order in compilation queue
# |   └── Time (ms) since JVM start
# └── Compilation ID
```

### 8. Advanced Details

**JIT Compilation levels (tiered compilation):**
```
Level 0: Interpreter only (no profiling, no compilation)
Level 1: C1 simple (trivial methods, no profiling)
Level 2: C1 limited (minimal profiling for hot methods)
Level 3: C1 full (full profiling for very hot methods)
Level 4: C2 (maximum optimization, slowest compilation)
```

**Escape Analysis in the pipeline:**
```java
void calculate() {
    Point p = new Point(1, 2);  // Normally: heap allocation
    int sum = p.x + p.y;        // EA: allocates on STACK (no GC!)
    System.out.println(sum);
}
// JIT detects p doesn't escape calculate()
// → Scalar replacement: p.x becomes local var, p.y becomes local var
// → No heap allocation at all!
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What does `javac` do?

**A**: `javac` compiles `.java` source files into `.class` bytecode files. It performs lexical analysis, parsing, semantic checking, and bytecode generation. The output is platform-independent bytecode that any JVM can execute.

#### Intermediate

**Q**: What's the difference between interpreter and JIT compiler?

**A**: The interpreter reads bytecode line-by-line and executes it immediately — fast startup but slow execution. The JIT compiler monitors "hot" methods (called ~10,000+ times) and compiles them to native machine code — slow startup but fast execution. Modern JVMs use **tiered compilation**: start with interpreter, profile with C1, then compile with C2.

#### Senior

**Q**: How would you diagnose a production issue where an API endpoint is slow only for the first few requests?

**A**: That's a classic **JIT warmup** issue. I'd: (1) Verify with `-XX:+PrintCompilation` — the slow first requests are interpreted; (2) Check if the method reaches compilation threshold — if the endpoint is rarely called, it stays interpreted forever; (3) Solutions: use AOT compilation (GraalVM native image), increase request rate during warmup, or use `-XX:CompileThreshold` to lower the JIT threshold; (4) For critical paths, use application-level warmup on startup (call the endpoint with dummy data).

#### Tricky

**Q**: If you modify a local variable's value in the debugger, can the JIT-optimized code reflect that change?

**A**: Not reliably. When JIT compiles a method with full optimization (C2), it may: (1) Eliminate local variables entirely via scalar replacement; (2) Reorder instructions; (3) Inline the method into the caller. In such cases, the debugger cannot modify local variables because they don't exist as variables — they're either in registers or optimized away. If I need to debug optimized code, I use `-XX:-Inline` or `-XX:CompileCommand=dontinline,*` to force more debuggable compilation. Most JVMs support "deoptimization" — when the debugger attaches, the JVM reverts to interpreted mode for the affected methods.


Excellent question. **Class Loaders** are one of the most frequently asked JVM topics in **7+ years** interviews.

Interviewers often ask:

* What are the three class loaders in Java?
* How does class loading work?
* Which class loader loads `String.class`?
* Can two classes with the same name exist?
* What is Parent Delegation?
* Can we write our own ClassLoader?
* Why is Bootstrap ClassLoader written in native code?
* What is the difference between Platform and Application ClassLoader?
* How does Spring Boot load nested JARs?

Let's cover this in depth.

---

# Chapter — Java Class Loaders

---

# Why Do We Need a Class Loader?

Suppose you write:

```java
public class Employee {

    public static void main(String[] args) {
        System.out.println("Hello");
    }

}
```

Question:

When you run

```text
java Employee
```

How does the JVM know:

* Where `Employee.class` is?
* How to read it?
* How to create a `Class` object?
* How to load `String.class`?
* How to load `System.class`?

Some component has to perform these tasks.

That component is called the **Class Loader**.

---

# What is a Class Loader?

**Definition:**

A **Class Loader** is a JVM subsystem responsible for locating, loading, and defining Java classes into the JVM.

It performs three major jobs:

```text
Locate .class file
        │
        ▼
Read bytecode
        │
        ▼
Create java.lang.Class object
```

Once a class is loaded, the JVM can execute it.

---

# Class Loading Process

```text
Employee.class

        │
        ▼

Class Loader

        │
        ▼

Bytecode Verification

        │
        ▼

Linking

        │
        ▼

Initialization

        │
        ▼

Class Ready
```

The class loader is only the first stage.

---

# The Three Built-in Class Loaders

Java provides three primary class loaders.

```text
                Bootstrap
                     ▲
                     │
              Platform
                     ▲
                     │
             Application
```

Notice:

This is a **parent hierarchy**, not inheritance.

Each loader delegates to its parent first.

---

# 1. Bootstrap Class Loader

Most important one.

### Responsibilities

Loads core Java classes.

Examples

```java
java.lang.String

java.lang.Object

java.lang.Integer

java.util.ArrayList

java.util.HashMap

java.lang.System
```

Question:

Who loads `String.class`?

Answer:

**Bootstrap Class Loader**

---

### Where are these classes?

JDK 8

```text
rt.jar
```

JDK 9+

No `rt.jar`.

Modules are used instead.

Located inside

```text
jmods/
```

---

### Interesting Fact

Bootstrap Class Loader is **not written in Java**.

It is implemented in native code (part of the JVM implementation).

Therefore

```java
System.out.println(String.class.getClassLoader());
```

Output

```text
null
```

Question

Does `null` mean no loader?

No.

It means

> Loaded by the Bootstrap Class Loader.

Interview favourite.

---

# 2. Platform Class Loader

Before Java 9

It was called

```text
Extension ClassLoader
```

After Java 9

It became

```text
Platform ClassLoader
```

---

### Responsibility

Loads platform libraries.

Examples

```text
java.sql

java.xml

java.management

java.naming
```

These are standard JDK modules but not part of the minimal core runtime.

---

Example

```java
System.out.println(java.sql.Driver.class.getClassLoader());
```

Output

```text
jdk.internal.loader.ClassLoaders$PlatformClassLoader
```

---

# 3. Application Class Loader

Also called

```text
System Class Loader
```

Responsibilities

Loads

* Your classes
* Third-party libraries
* Maven dependencies
* Gradle dependencies

Example

```java
public class Employee {

}
```

Loaded by

Application Class Loader.

---

Example

```java
System.out.println(Employee.class.getClassLoader());
```

Output

```text
jdk.internal.loader.ClassLoaders$AppClassLoader
```

---

# Complete Hierarchy

```text
Bootstrap ClassLoader
        │
        ▼
Platform ClassLoader
        │
        ▼
Application ClassLoader
        │
        ▼
Employee.class
```

---

# Real Example

Suppose

```java
public class Demo {

    public static void main(String[] args) {

        System.out.println(String.class.getClassLoader());

        System.out.println(java.sql.Driver.class.getClassLoader());

        System.out.println(Demo.class.getClassLoader());

    }

}
```

Output

```text
null

PlatformClassLoader

AppClassLoader
```

---

# What Does Each Loader Load?

| Class                      | Loaded By   |
| -------------------------- | ----------- |
| `java.lang.String`         | Bootstrap   |
| `java.lang.Object`         | Bootstrap   |
| `java.util.ArrayList`      | Bootstrap   |
| `java.sql.Driver`          | Platform    |
| `java.xml.DocumentBuilder` | Platform    |
| Your `Employee` class      | Application |
| Spring Boot classes        | Application |
| Hibernate classes          | Application |
| Jackson classes            | Application |

---

# Parent Delegation Model

This is **one of the most important interview topics**.

Suppose you write

```java
new Employee();
```

Application Loader receives the request.

Does it immediately load it?

No.

Flow:

```text
Application Loader

        │

        ▼

Ask Parent

        │

        ▼

Platform Loader

        │

        ▼

Ask Parent

        │

        ▼

Bootstrap Loader
```

Bootstrap asks:

> Do I have Employee?

No.

Returns.

Platform asks:

> Do I have Employee?

No.

Returns.

Application finally loads it.

---

Diagram

```text
Employee

↓

Application Loader

↓

Parent?

↓

Platform

↓

Parent?

↓

Bootstrap

↓

Not Found

↓

Platform

↓

Not Found

↓

Application

↓

Loads Employee
```

---

# Why Parent Delegation?

Imagine there were no delegation.

Suppose you accidentally create

```java
package java.lang;

public class String {

}
```

Without delegation

Your fake `String` might get loaded instead of the real one.

That would completely break the JVM.

With parent delegation

Bootstrap loads the real `java.lang.String` first.

Your fake version is ignored (and, in practice, defining classes in `java.*` packages is prohibited).

This improves

* Security
* Stability
* Consistency

---

# Can Two Classes Have the Same Name?

Surprisingly,

**Yes**, if:

* They are loaded by different class loaders.

Example

```text
Employee.class

↓

Loader A
```

and

```text
Employee.class

↓

Loader B
```

The JVM treats them as **different classes**.

Because class identity is

```text
Fully Qualified Name

+

Class Loader
```

Not just

```text
Fully Qualified Name
```

This behaviour is fundamental to application servers and plugin architectures.

---

# Can We Create Our Own Class Loader?

Absolutely.

```java
public class MyClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name)
            throws ClassNotFoundException {

        // Load bytes

        // Define class

        return defineClass(...);

    }

}
```

Used in

* Application servers
* Plugin systems
* IDEs
* Hot reloading
* Custom scripting engines

---

# How Spring Boot Uses Class Loaders

Spring Boot executable JARs contain nested JARs:

```text
app.jar

│

├── BOOT-INF/classes

├── BOOT-INF/lib

│
```

The standard Application ClassLoader cannot load classes directly from nested JARs.

Spring Boot solves this by providing its own launcher and custom class loader (for example, `LaunchedClassLoader` in recent versions), which knows how to load classes from the nested archive layout.

This is why a Spring Boot executable JAR can run with a simple:

```text
java -jar app.jar
```

---

# Common Interview Questions

### Q1. Why does `String.class.getClassLoader()` return `null`?

Because `String` is loaded by the **Bootstrap Class Loader**, which is implemented by the JVM itself rather than as a normal Java `ClassLoader` object.

---

### Q2. What is the difference between Platform and Application ClassLoader?

| Platform                   | Application                                |
| -------------------------- | ------------------------------------------ |
| Loads JDK platform modules | Loads application and third-party classes  |
| Parent is Bootstrap        | Parent is Platform                         |
| Part of the JDK runtime    | Uses the application classpath/module path |

---

### Q3. Why is Parent Delegation important?

It prevents core Java classes from being replaced accidentally or maliciously, avoids duplicate loading of shared classes, and maintains consistent class identity.

---

### Q4. Can you break Parent Delegation?

Yes.

Custom class loaders can choose a **child-first** strategy instead of the usual parent-first strategy. This is used in some application servers, plugin systems, and OSGi-like environments, but it must be implemented carefully because it can lead to class conflicts.

---

### Q5. What determines the identity of a class?

A class is uniquely identified by:

```text
Fully Qualified Class Name

+

Class Loader
```

Therefore, the same `.class` file loaded by two different class loaders represents two distinct types to the JVM.

---

# Quick Revision

| Class Loader | Loads                                               | Parent            |
| ------------ | --------------------------------------------------- | ----------------- |
| Bootstrap    | Core Java classes (`java.lang`, `java.util`, etc.)  | None (native JVM) |
| Platform     | JDK platform modules (`java.sql`, `java.xml`, etc.) | Bootstrap         |
| Application  | Your application classes and libraries              | Platform          |

---

## Interview Tip

A very common follow-up question is:

> **"What are the stages after a class is loaded?"**

That leads into the **Class Loading Lifecycle**:

1. Loading
2. Verification
3. Preparation
4. Resolution
5. Initialization

Understanding those stages completes the picture of how a class goes from a `.class` file on disk to executable code in the JVM. This is another high-priority topic for senior Java interviews.


For **7+ years**, interviewers rarely ask isolated questions like *"What is JVM?"*. Instead, they probe your understanding by chaining related questions. A single discussion on JVM/JDK/JRE often lasts **15–20 minutes**.

Below is a comprehensive recap of the most common questions, along with the depth expected from a senior backend engineer.

---

# 1. What is Java?

### Basic Answer

Java is a high-level, object-oriented, class-based programming language designed with the principle of **Write Once, Run Anywhere (WORA)**.

---

### Senior Answer

Java consists of two major parts:

* **Java Language** – The syntax used by developers.
* **Java Platform** – The JVM and standard libraries that execute Java programs.

Java source code is compiled into **platform-independent bytecode**, which is executed by a JVM implementation available for each operating system.

---

## Interview Follow-up

> Is Java interpreted or compiled?

Correct answer:

Java is **both**.

```
Java Source
       │
       ▼
Compiled by javac
       │
       ▼
Bytecode (.class)
       │
       ▼
Interpreted by JVM
       │
       ▼
Frequently executed code
       │
       ▼
JIT Compiler
       │
       ▼
Native Machine Code
```

---

# 2. Why was Java created?

Many candidates answer:

> Platform Independence.

Not enough.

### Problems C++ had

* Platform dependent binaries
* Manual memory management
* Pointer misuse
* Multiple inheritance complexity
* Buffer overflows
* Difficult enterprise deployment

Java introduced:

* JVM
* Automatic Garbage Collection
* Bytecode
* Strong type checking
* Security Sandbox

---

# 3. Explain WORA (Write Once Run Anywhere)

Question

How can one program run on Windows and Linux?

```
Employee.java

↓

javac

↓

Employee.class

↓

Windows JVM

↓

Windows Machine Code
```

```
Employee.class

↓

Linux JVM

↓

Linux Machine Code
```

The **bytecode remains identical**.

Only the JVM implementation changes.

---

## Follow-up

Why can't C++ do this?

Because C++ compiler generates

```
Machine Code
```

Machine code is CPU and OS dependent.

Java compiler generates

```
Bytecode
```

Bytecode is platform independent.

---

# 4. What is JDK?

Interview mistake

Many candidates say

> JDK contains JRE.

That was true before Java 9.

### Modern Answer

JDK (Java Development Kit) contains:

* Java compiler (`javac`)
* Debugger (`jdb`)
* Documentation tool (`javadoc`)
* Packaging tool (`jar`)
* Runtime components
* JVM implementation
* Development utilities

Used for **developing Java applications**.

---

# 5. What is JRE?

JRE provides everything required to **run** Java applications.

Contains:

* JVM
* Core Java Libraries
* Runtime components

Cannot compile Java code.

**Note:** Since Java 9, Oracle no longer distributes a standalone JRE separately, but the concept still exists architecturally and is frequently asked in interviews.

---

# 6. What is JVM?

Most candidates say

> JVM executes Java programs.

Incomplete.

### Senior Answer

The JVM is a specification and its implementations are responsible for:

* Loading classes
* Bytecode verification
* Memory management
* Garbage Collection
* Thread management
* Security
* Bytecode execution
* JIT compilation

---

# 7. Difference Between JVM, JRE and JDK

| Feature            | JVM | JRE     | JDK |
| ------------------ | --- | ------- | --- |
| Executes bytecode  | ✅   | ✅       | ✅   |
| Compiler           | ❌   | ❌       | ✅   |
| Debugger           | ❌   | ❌       | ✅   |
| Javadoc            | ❌   | ❌       | ✅   |
| Runtime libraries  | ❌   | ✅       | ✅   |
| Used by developers | ❌   | Limited | ✅   |

---

# 8. Explain Java Compilation Pipeline

One of the favourite senior questions.

```
Employee.java

↓

Lexical Analysis

↓

Parsing

↓

Semantic Analysis

↓

Bytecode Generation

↓

Employee.class

↓

Class Loader

↓

Verification

↓

Interpreter

↓

JIT Compiler

↓

Machine Code

↓

CPU
```

Interviewers love this diagram.

---

# 9. What does `javac` do?

Not just

> Compiles Java.

Actual stages:

```
Read Source

↓

Tokenization

↓

Parsing

↓

AST Creation

↓

Semantic Checks

↓

Annotation Processing

↓

Optimisation

↓

Bytecode Generation
```

---

## Follow-up

What errors are caught by `javac`?

* Syntax errors
* Type mismatch
* Missing classes
* Invalid inheritance
* Generic type checks
* Access modifier violations

Not runtime errors.

---

# 10. What is Bytecode?

Definition:

Bytecode is an intermediate instruction set generated by the Java compiler that is independent of operating systems and CPU architectures.

Example

```
Employee.class
```

contains bytecode.

Not machine code.

---

## Why Bytecode?

Instead of generating

```
Windows Machine Code

Linux Machine Code

Mac Machine Code
```

Java generates

```
One Bytecode

↓

Many JVMs
```

---

# 11. Is Bytecode Binary?

Yes.

`.class` is a binary file.

Open it in Notepad.

You'll see garbage characters.

---

# 12. Can Humans Read Bytecode?

Not directly.

Use

```
javap
```

---

# 13. What is `javap`?

Interview favourite.

It disassembles bytecode.

Example

```
javap Employee
```

Shows

* Methods
* Fields
* Bytecode
* Constant Pool
* Method descriptors

---

## Useful Commands

```
javap Employee
```

```
javap -c Employee
```

Shows bytecode instructions.

```
javap -v Employee
```

Verbose output.

Shows:

* Constant Pool
* Stack size
* Local variables
* Bytecode
* Attributes
* Line Number Table

---

# 14. What is inside a `.class` file?

```
CAFEBABE

↓

Version

↓

Constant Pool

↓

Access Flags

↓

This Class

↓

Super Class

↓

Interfaces

↓

Fields

↓

Methods

↓

Attributes
```

Every Java class follows this structure.

---

# 15. What is the Constant Pool?

One of the most important interview topics.

Stores

* String literals
* Class names
* Method names
* Field names
* Type descriptors
* Symbolic references

It avoids duplication.

---

# 16. What is `CAFEBABE`?

Magic Number.

Every valid Java class starts with

```
0xCAFEBABE
```

JVM uses it to verify that the file is a valid Java class.

---

# 17. How does JVM execute bytecode?

```
Bytecode

↓

Interpreter

↓

Frequently Executed?

↓

Yes

↓

JIT Compiler

↓

Machine Code

↓

CPU
```

---

# 18. Interpreter vs JIT

| Interpreter           | JIT                            |
| --------------------- | ------------------------------ |
| Executes line by line | Compiles to native code        |
| No optimisation       | Highly optimised               |
| Slower                | Faster after warm-up           |
| Starts quickly        | Compilation overhead initially |

---

# 19. Why does Java become faster after some time?

Because

```
Interpreter

↓

Hot Methods

↓

JIT

↓

Machine Code

↓

Cached

↓

Future calls become faster
```

This is known as **JVM warm-up**.

---

# 20. What is a Hot Method?

A frequently executed method.

The JVM profiles execution and identifies methods that are called often.

Only these methods are JIT compiled.

---

# 21. What is HotSpot JVM?

Oracle's JVM implementation.

Features:

* Interpreter
* JIT Compiler
* Adaptive Optimisation
* Garbage Collectors
* Profiling
* Escape Analysis
* Method Inlining

---

# 22. Explain End-to-End Java Execution

This is the answer that impresses interviewers.

```
Developer

↓

Employee.java

↓

javac

↓

Employee.class

↓

Application ClassLoader

↓

Verification

↓

Preparation

↓

Resolution

↓

Initialization

↓

Interpreter

↓

Hot Method Detection

↓

JIT Compilation

↓

Machine Code

↓

CPU
```

---

# 23. Frequently Asked Senior Follow-up Questions

### Why doesn't `javac` generate machine code directly?

Because Java aims for platform independence. Bytecode can run on any JVM implementation.

---

### Why is Java slower than C++ on startup?

Because Java must:

* Load classes
* Verify bytecode
* Initialise the JVM
* Interpret code initially
* Warm up the JIT compiler

---

### Does the JVM understand Java source code?

No.

It only understands **bytecode**.

---

### Can JVM execute Kotlin?

Yes.

---

### Can JVM execute Scala?

Yes.

---

### Can JVM execute Groovy?

Yes.

---

### Why?

Because they all compile to JVM bytecode.

---

### Does every language use `javac`?

No.

Each language has its own compiler.

Examples:

* Kotlin → `kotlinc`
* Scala → `scalac`
* Groovy → `groovyc`

All generate JVM-compatible bytecode.

---

### Is JVM the same on every operating system?

No.

Each operating system has its own JVM implementation.

Examples:

* Windows JVM
* Linux JVM
* macOS JVM

They all execute the same bytecode but generate native code suitable for their respective platforms.

---

# 24. 7+ Years Rapid Revision

| Topic         | One-Line Revision                                                                |
| ------------- | -------------------------------------------------------------------------------- |
| Java          | Compiled + interpreted language targeting the JVM                                |
| JDK           | Development toolkit with compiler, runtime, and tools                            |
| JRE           | Runtime environment (conceptually includes JVM and libraries)                    |
| JVM           | Loads, verifies, manages memory, executes bytecode, and performs JIT compilation |
| `javac`       | Compiles Java source into bytecode                                               |
| Bytecode      | Platform-independent instructions stored in `.class` files                       |
| `javap`       | Disassembler for inspecting compiled class files                                 |
| Constant Pool | Symbol table storing names, literals, and symbolic references                    |
| `CAFEBABE`    | Magic number identifying a valid Java class file                                 |
| Interpreter   | Executes bytecode instruction by instruction                                     |
| JIT           | Compiles hot bytecode into native machine code                                   |
| HotSpot       | JVM implementation with adaptive optimisation                                    |

---

## Expected Interview Flow

A typical senior interview often progresses like this:

```text
Why Java?
      │
      ▼
Platform Independence
      │
      ▼
JDK vs JRE vs JVM
      │
      ▼
javac
      │
      ▼
Bytecode
      │
      ▼
javap
      │
      ▼
.class File Structure
      │
      ▼
Class Loaders
      │
      ▼
Class Loading Process
      │
      ▼
Interpreter vs JIT
      │
      ▼
HotSpot Optimisations
      │
      ▼
JVM Memory
      │
      ▼
Garbage Collection
```

If you can confidently explain this entire chain—from writing `Employee.java` to the CPU executing optimised native code—you'll be well prepared for the JVM fundamentals portion of a senior Java backend interview.
