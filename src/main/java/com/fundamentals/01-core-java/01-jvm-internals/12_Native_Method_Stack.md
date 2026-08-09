Perfect. This is the **last Runtime Data Area**. Fortunately, it's also one of the smallest topics.

For a **7+ years Backend Java interview**, you usually only need a conceptual understanding unless you're working with JNI or native libraries.

---

# Chapter 8.5 - Native Method Stack ⭐⭐⭐

---

# What is the Native Method Stack?

**Definition (Interview)**

The **Native Method Stack** is a **thread-private runtime memory area** used by the JVM when executing **native methods** (methods written in languages such as C or C++).

> **Java methods use the Java Stack. Native methods use the Native Method Stack.**

That's the easiest way to remember it.

---

# First Question

Suppose you write:

```java
public class Main {

    public static void main(String[] args) {

        System.out.println("Hello");

    }

}
```

Everything looks like Java.

But is **every method inside the JDK actually written in Java?**

**No.**

Many JDK methods eventually call operating system APIs written in **C/C++**.

Examples:

* File operations
* Network operations
* Thread creation
* Memory management
* Process management

These are implemented using **native code**.

---

# What is a Native Method?

A native method is simply a Java method whose implementation is **not written in Java**.

Example:

```java
public class Example {

    public native void print();

}
```

Notice the keyword:

```java
native
```

There is **no Java method body**.

The implementation exists in a native library (for example, a C/C++ library).

---

# Why do we need Native Methods?

Java is platform-independent.

But the operating system is not.

Suppose Java wants to:

* Create a thread
* Read a file
* Open a socket
* Allocate OS resources

Eventually, someone must talk to the operating system.

```text
Java Code

↓

JVM

↓

Native Code (C/C++)

↓

Operating System
```

Java itself cannot directly execute operating system APIs.

Native code acts as the bridge.

---

# Where does the Native Method Stack fit?

Suppose we call:

```java
Thread.sleep(1000);
```

Conceptually, execution looks like this:

```text
Java Method

↓

Java Stack

↓

Native Method

↓

Native Method Stack

↓

Operating System
```

The Java Stack handles Java execution.

When execution enters native code, the Native Method Stack is used.

---

# Relationship with JNI

The JVM cannot directly call C/C++ functions.

It uses **JNI (Java Native Interface)**.

```text
Java Code

↓

JNI

↓

Native Library (.dll/.so/.dylib)

↓

Operating System
```

JNI acts as the bridge between Java and native code.

We'll study JNI separately later.

---

# One Native Stack Per Thread

Just like the Java Stack,

every thread has its own Native Method Stack.

```text
                Thread-1

+--------------------------+
| Java Stack               |
+--------------------------+
| Native Method Stack      |
+--------------------------+

--------------------------------

                Thread-2

+--------------------------+
| Java Stack               |
+--------------------------+
| Native Method Stack      |
+--------------------------+
```

---

# Native Libraries

Native methods are implemented inside platform-specific libraries.

Examples:

| Operating System | Library Format |
| ---------------- | -------------- |
| Windows          | `.dll`         |
| Linux            | `.so`          |
| macOS            | `.dylib`       |

The JVM loads these libraries when required.

Example:

```java
System.loadLibrary("mylibrary");
```

The JVM searches for the appropriate platform-specific library and loads it into the process.

---

# Does Every Java Program Use the Native Method Stack?

Indirectly, **yes**.

Even a simple program like:

```java
System.out.println("Hello");
```

may eventually invoke native code within the JDK to interact with the operating system.

However, **most application developers never write native methods themselves**.

---

# Java Stack vs Native Method Stack

| Java Stack               | Native Method Stack                        |
| ------------------------ | ------------------------------------------ |
| Executes Java methods    | Executes native methods                    |
| Stores Java stack frames | Stores native call frames                  |
| Managed by the JVM       | Used during native execution               |
| Bytecode execution       | C/C++ (or other native language) execution |

---

# Complete Runtime Data Areas

```text
                    JVM

        Shared Runtime Areas
+-----------------------------------+
| Heap                              |
| Metaspace                         |
+-----------------------------------+

---------------------------------------

Thread-1

+-----------------------+
| Java Stack            |
+-----------------------+
| PC Register           |
+-----------------------+
| Native Method Stack   |
+-----------------------+

---------------------------------------

Thread-2

+-----------------------+
| Java Stack            |
+-----------------------+
| PC Register           |
+-----------------------+
| Native Method Stack   |
+-----------------------+
```

---

# Interview Questions

### Q1. What is the Native Method Stack?

The Native Method Stack is a **thread-private runtime memory area** used while executing native methods implemented outside Java (typically in C/C++).

---

### Q2. Why does Java need native methods?

Because certain operations—such as interacting with the operating system, hardware, or platform-specific APIs—cannot be implemented purely in Java and require native code.

---

### Q3. What is the difference between the Java Stack and the Native Method Stack?

* **Java Stack** → Executes Java bytecode.
* **Native Method Stack** → Executes native (non-Java) methods.

---

### Q4. How does Java call native code?

Using **JNI (Java Native Interface)**, which provides the bridge between Java and native libraries.

---

# Interview Boundary

For a **7+ years Backend Java interview**, this is sufficient.

You should know:

* What the Native Method Stack is.
* Why native methods exist.
* The role of the `native` keyword.
* The relationship between the Native Method Stack and JNI.
* That native libraries are platform-specific (`.dll`, `.so`, `.dylib`).

**Deep Dive (Optional):**

* JNI programming with C/C++.
* JNI memory management.
* Native stack implementation in HotSpot.
* Java Native Access (JNA) vs JNI.
* Foreign Function & Memory (FFM) API introduced in newer Java versions.

---

# Runtime Data Areas - Completed

We've now covered all **five Runtime Data Areas** defined by the JVM Specification:

```text
Runtime Data Areas
│
├── ✅ Heap ⭐⭐⭐⭐⭐
├── ✅ Java Stack ⭐⭐⭐⭐⭐
├── ✅ Metaspace ⭐⭐⭐⭐
├── ✅ PC Register ⭐⭐⭐
└── ✅ Native Method Stack ⭐⭐⭐
```

## What's Next?

Now we move to one of the most important JVM topics:

```text
Execution Engine ⭐⭐⭐⭐⭐
│
├── Interpreter
├── JIT Compiler
├── Hot Methods
├── C1 Compiler
├── C2 Compiler
├── Code Cache
├── Runtime Optimisations
│   ├── Method Inlining
│   ├── Escape Analysis
│   └── Dead Code Elimination
└── Complete Execution Flow
```

This chapter answers the question we've been building towards since the beginning:

> **"We have compiled bytecode, loaded classes, and allocated memory... but how does the JVM actually execute the bytecode efficiently?"**

It's one of the highest-value topics for senior Java backend interviews.
