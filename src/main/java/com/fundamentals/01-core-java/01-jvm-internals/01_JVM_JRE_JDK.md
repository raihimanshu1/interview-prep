

---

# JVM, JRE & JDK – Complete Deep Dive (7+ Years Interview)

---

# Learning Objectives

By the end of this topic, you should be able to answer:

* What are JDK, JRE and JVM?
* Why do we need all three?
* Why is Java platform independent?
* Why is JVM platform dependent?
* What happens internally when we execute `java Main`?
* How does bytecode get converted into machine code?
* What is the role of the Interpreter and JIT Compiler?
* Why is Java considered "Write Once, Run Anywhere"?
* Why is Java still one of the fastest managed languages?

---

# Big Picture

Before understanding JDK, JRE and JVM, understand **how a CPU executes programs**.

## How CPU Understands Programs

CPU understands only **Machine Language**.

Example:

```
10110010
00101101
11010110
```

It **cannot understand Java**.

It **cannot understand Python**.

It **cannot understand C++**.

It only understands binary instructions.

---

# Then how does Java run?

Imagine you wrote:

```java
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```

The CPU cannot execute this.

So Java performs several transformations.

```
                    Java Source Code
                    (Main.java)
                          │
                          │
                          ▼
                  javac Compiler
                          │
                          │
                          ▼
               Bytecode (Main.class)
                          │
                          │
             Platform Independent
                          │
                          ▼
                     JVM Starts
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
 Class Loader     Bytecode Verifier   Runtime Libraries
        │
        ▼
 Execution Engine
        │
   ┌────┴─────┐
   │          │
Interpreter   JIT Compiler
   │          │
   └────┬─────┘
        ▼
 Native Machine Code
        │
        ▼
       CPU
```

This entire process happens every time you execute:

```
java Main
```

---

# Why doesn't Java compile directly into Machine Code?

This is probably the **most important interview question**.

Imagine Java compiled directly into Windows machine code.

```
Java Code
      │
      ▼
Windows EXE
```

Now try running that executable on Linux.

```
Linux

↓

Cannot understand Windows executable
```

It fails.

You would need to compile separately for:

* Windows
* Linux
* macOS
* Solaris
* AIX

This would defeat Java's portability.

Instead Java introduced an intermediate language called **Bytecode**.

```
Java

↓

Bytecode

↓

Windows JVM

↓

Windows Machine Code
```

```
Java

↓

Bytecode

↓

Linux JVM

↓

Linux Machine Code
```

```
Java

↓

Bytecode

↓

Mac JVM

↓

Mac Machine Code
```

Compile once.

Run everywhere.

This is why Java became famous for:

> **Write Once, Run Anywhere (WORA)**

---

# Understanding JDK

## Definition

JDK stands for

> **Java Development Kit**

Think of it as the complete toolbox for Java developers.

```
                 JDK
                  │
      ┌───────────┴───────────┐
      │                       │
 Development Tools         Runtime
```

---

## What does JDK contain?

```
                   JDK
─────────────────────────────────────

javac        → Compiler

java         → Starts JVM

jar          → Package applications

jdb          → Debugger

javadoc      → Documentation Generator

jshell       → Interactive Java Shell

jlink        → Custom Runtime Images

JRE          → Runtime Environment

─────────────────────────────────────
```

The JDK is everything required to **develop, build, debug, package, and run** Java applications.

---

## Why do Developers need JDK?

Suppose you're building a Spring Boot application.

You need to:

```
Write Code

↓

Compile

↓

Debug

↓

Package

↓

Run

↓

Generate Documentation
```

All these tools are provided by the JDK.

Without the JDK, you cannot compile Java source code.

---

# Understanding JRE

JRE stands for

> **Java Runtime Environment**

Imagine a person who only wants to use your application.

Example:

```
Spotify

IntelliJ

Jenkins

Jira
```

The user doesn't write Java code.

They only execute applications.

Therefore they don't need:

* javac
* debugger
* javadoc

They only need the runtime.

Hence:

```
JRE

↓

JVM

+

Runtime Libraries

+

Configuration Files

+

Native Libraries
```

---

## Runtime Libraries

Every Java program uses classes like

```
String

List

Map

ArrayList

HashMap

Files

Socket
```

Where do these come from?

Inside the JRE.

Examples

```
java.lang

java.util

java.io

java.nio

java.time

java.net
```

Without these libraries, Java programs cannot run.

---

# Understanding JVM

Now comes the heart of Java.

JVM means

> **Java Virtual Machine**

Notice the word **Virtual**.

It behaves like a small computer running inside your operating system.

```
               Operating System

+-----------------------------------------+

          Java Virtual Machine

+-----------------------------------------+

       Your Java Application

+-----------------------------------------+
```

---

## Why Virtual Machine?

Because it provides its own environment.

It manages

* Memory
* Threads
* Garbage Collection
* Object Allocation
* Class Loading
* Security
* Bytecode Verification

Your Java program never talks directly to the operating system.

Instead:

```
Application

↓

JVM

↓

Operating System

↓

CPU
```

---

# Responsibilities of JVM

Senior interviewers love this question.

The JVM is responsible for:

```
Class Loading

↓

Bytecode Verification

↓

Memory Allocation

↓

Object Creation

↓

Method Execution

↓

Garbage Collection

↓

JIT Compilation

↓

Thread Management

↓

Exception Handling

↓

Security Checks
```

---

# JVM Internal Architecture

This is one of the most commonly drawn interview diagrams.

```
                      JVM
──────────────────────────────────────────────────────

           Class Loader Subsystem
                     │
                     ▼
             Bytecode Verifier
                     │
                     ▼
              Runtime Data Areas
        ┌────────────────────────────┐
        │ Heap                       │
        │ Java Stack                 │
        │ Method Area (Metaspace)    │
        │ PC Register                │
        │ Native Method Stack        │
        └────────────────────────────┘
                     │
                     ▼
             Execution Engine
        ┌─────────────────────┐
        │ Interpreter         │
        │ JIT Compiler        │
        │ Garbage Collector   │
        └─────────────────────┘
                     │
                     ▼
          Native Method Interface
                     │
                     ▼
              Operating System
```

Every one of these boxes can be a separate interview topic.

---

# Relationship between JDK, JRE and JVM

```
+----------------------------------------------------+
|                      JDK                           |
|                                                    |
|  javac   jar   javadoc   jdb   jshell   jlink      |
|                                                    |
|    +-------------------------------------------+   |
|    |                  JRE                      |   |
|    |                                           |   |
|    | Runtime Libraries                         |   |
|    | Native Libraries                          |   |
|    |                                           |   |
|    |   +-----------------------------------+   |   |
|    |   |              JVM                  |   |   |
|    |   |                                   |   |   |
|    |   | Class Loader                      |   |   |
|    |   | Execution Engine                  |   |   |
|    |   | Garbage Collector                 |   |   |
|    |   | Memory Management                 |   |   |
|    |   +-----------------------------------+   |   |
|    +-------------------------------------------+   |
+----------------------------------------------------+
```

A common interview answer is:

> **JDK contains the JRE, and the JRE contains the JVM. The JVM is the execution engine, while the JRE provides the runtime environment, and the JDK provides development tools on top of that.**

---

# Why is Java Platform Independent?

This is asked in almost every Java interview.

The key idea is **bytecode**.

```
                 Java Source Code
                        │
                        ▼
                   javac Compiler
                        │
                        ▼
             Platform Independent Bytecode
                        │
         ┌──────────────┼──────────────┐
         ▼              ▼              ▼
    Windows JVM     Linux JVM      macOS JVM
         │              │              │
         ▼              ▼              ▼
   Windows Code    Linux Code    macOS Code
         │              │              │
         └──────────────┼──────────────┘
                        ▼
                     Executes
```

The `.class` file remains the same. Only the JVM implementation changes.

---

# Then Why is JVM Platform Dependent?

This is a favourite follow-up.

The JVM interacts with:

* CPU instruction set
* Operating system APIs
* Memory management
* Native libraries
* Thread scheduling

A Windows JVM cannot simply run on Linux because it is compiled specifically for Windows.

```
Windows JVM
      │
Windows Kernel APIs
      │
Windows Hardware

-------------------------

Linux JVM
      │
Linux Kernel APIs
      │
Linux Hardware
```

So the JVM must be built separately for each platform, while the bytecode remains portable.

---

# Interview Tips

If an interviewer asks:

> **"Explain JDK, JRE and JVM."**

Don't stop at definitions. Explain the execution flow, portability, and the responsibilities of the JVM. A strong answer naturally leads into topics like the Class Loader, JIT Compiler, Java Memory Model, and Garbage Collection, demonstrating a solid understanding of the Java runtime.

---

## What Comes Next?

The natural continuation after this topic is **"What happens internally when we run `java Main`?"** We can trace every stage—from the `javac` compilation, class loading, bytecode verification, memory allocation, interpreter, JIT compilation, and finally execution by the CPU—with production-level depth and interview-focused explanations. This is one of the highest-value topics for senior Java interviews.



I actually think that's a better interview order.

We now understand the **architecture**:

> Specification → Implementation → JDK → JRE → JVM

That gives us the mental model.

Now, for interviews, we should switch to the order interviewers actually ask questions.

```
JDK
   ↓
JRE
   ↓
JVM
   ↓
Class Loading
   ↓
Memory Areas
   ↓
Execution Engine
   ↓
JIT
   ↓
GC
```

This is much more natural because every topic builds on the previous one.

---

# Chapter 1 - JDK (Java Development Kit)

---

# What is JDK?

The **JDK (Java Development Kit)** is the complete software package used to **develop, build, debug, package, and run** Java applications.

Think of it this way:

```
As a Java Developer

You need to

✓ Write code
✓ Compile code
✓ Run code
✓ Debug code
✓ Package code
✓ Generate documentation
✓ Analyse code

JDK provides tools for all of these.
```

So the JDK is **not just a compiler**.

It is an entire **development toolkit**.

---

# What does JDK contain?

This is the first interview question.

```
                         JDK
------------------------------------------------------

           Development Tools
                  │
      ┌───────────┼────────────┐
      │           │            │
    javac       jar          jdb
      │
    jshell
      │
   javadoc
      │
    jdeps
      │
    jlink
      │
     java
      │
      ▼
     JRE
      │
      ▼
     JVM

------------------------------------------------------
```

A common mistake is to answer:

> "JDK contains JRE."

That's correct but **incomplete**.

Interviewers expect you to know the important tools too.

---

# JDK Components

Let's understand each one.

---

## 1. javac (Java Compiler)

This is probably the most important tool after `java`.

### Purpose

Compiles Java source code into bytecode.

```
Main.java

↓

javac

↓

Main.class
```

Example

```bash
javac Main.java
```

Output

```
Main.class
```

Notice

```
Source Code

↓

Bytecode

NOT

Machine Code
```

Many candidates incorrectly say that `javac` creates machine code.

It does **not**.

It generates **bytecode**.

---

### Internally, what does `javac` do?

Although you don't need compiler internals for most interviews, you should know the high-level pipeline:

```
Main.java

↓

Lexical Analysis
(Tokenization)

↓

Parsing

↓

Abstract Syntax Tree (AST)

↓

Semantic Analysis
(Type checking, symbol resolution)

↓

Optimisation

↓

Bytecode Generation

↓

Main.class
```

This shows that `javac` is much more than a text-to-bytecode converter.

---

## 2. java (Java Launcher)

Many people confuse `java` with the JVM.

They are **not the same**.

When you run:

```bash
java Main
```

You are **not directly starting bytecode execution**.

The `java` command is a launcher.

Its responsibilities include:

```
java command

↓

Reads command-line arguments

↓

Locates JVM

↓

Starts JVM process

↓

Passes Main.class to JVM

↓

JVM begins execution
```

So:

```
java

≠

JVM
```

The `java` executable is simply the entry point that creates and configures the JVM process.

---

## 3. jar

Java applications usually contain many `.class` files.

Example

```
Main.class

Student.class

Employee.class

Service.class

Controller.class

Repository.class
```

Instead of shipping hundreds of files, we package them.

```
1000 .class files

↓

jar

↓

application.jar
```

Example

```bash
jar cf app.jar *.class
```

A JAR is essentially a ZIP archive with Java-specific metadata (like the manifest).

---

## 4. javadoc

Generates HTML documentation from source code comments.

Example

```java
/**
 * Calculates total salary.
 */
public double calculateSalary() { }
```

Running:

```bash
javadoc Employee.java
```

produces browsable API documentation.

---

## 5. jdb (Java Debugger)

Command-line debugger.

Supports:

* Breakpoints
* Step Into
* Step Over
* Variable inspection
* Thread inspection

Modern IDEs like IntelliJ and Eclipse use richer graphical debuggers, but `jdb` remains part of the JDK.

---

## 6. jshell

Introduced in Java 9.

Interactive Java REPL (Read-Eval-Print Loop).

Example

```java
jshell> int x = 10

jshell> x + 20

30
```

Useful for experimenting without creating a full project.

---

## 7. jdeps

Analyses module and dependency usage.

Example

```
Application

↓

Uses

↓

java.sql

↓

java.logging

↓

java.xml
```

Helpful when migrating to the Java Module System.

---

## 8. jlink

Introduced with Java's module system.

Allows you to build a **custom runtime image**.

Instead of shipping the entire JDK/JRE, you can include only the modules your application needs.

```
Application

↓

Needs

java.base

java.sql

↓

jlink

↓

Small Runtime Image
```

This reduces deployment size and startup overhead.

---

# JDK vs JRE

Interviewers often ask this immediately.

| JDK                                    | JRE                              |
| -------------------------------------- | -------------------------------- |
| Development kit                        | Runtime environment              |
| Used by developers                     | Used to execute applications     |
| Contains compiler (`javac`)            | No compiler                      |
| Contains debugging and packaging tools | No development tools             |
| Contains JRE                           | Contains JVM + runtime libraries |

---

# Where are Java Runtime Libraries?

Many people think:

```
JVM

↓

String

HashMap

ArrayList
```

Wrong.

The correct picture is:

```
                     JDK
                      │
      ┌───────────────┴────────────────┐
      │                                │
 Development Tools                    JRE
                                      │
                     ┌────────────────┴───────────────┐
                     │                               │
             Java Runtime Libraries                JVM
                     │
      ┌──────────────┼────────────────────────────┐
      │              │                            │
 java.lang      java.util                    java.io
      │
 String
 Object
 Math
 Thread
```

The **runtime libraries are part of the JRE**, not the JVM.

The JVM executes them just like it executes your own classes.

---

# Common Interview Questions

### Q1. Is `javac` part of the JVM?

**No.**

`javac` is part of the JDK. It is a compiler that converts `.java` source files into `.class` bytecode before the JVM is involved.

---

### Q2. Is the `java` command the JVM?

**No.**

The `java` command is a launcher. It starts and configures a JVM process, which then loads and executes your application.

---

### Q3. Can you compile Java without the JDK?

**No.**

Compilation requires the `javac` compiler, which is part of the JDK.

---

### Q4. Can you run a compiled Java application without the full JDK?

**Yes.**

A compiled application only needs a Java runtime (or a custom runtime image created with `jlink`) because the source has already been compiled into bytecode.

---

## Senior-Level Interview Summary

> **The JDK is the complete development toolkit for Java. It contains tools such as `javac` for compilation, `java` for launching the JVM, `jar` for packaging, `javadoc` for documentation, `jdb` for debugging, `jshell` for interactive execution, and utilities like `jdeps` and `jlink`. It also includes the Java Runtime Environment, which contains the JVM and the standard Java runtime libraries. During development, the JDK is required because it provides both the build tools and the runtime needed to compile, execute, debug, and package Java applications.**

I think this is the right depth for a **7+ years interview**—deep enough to answer follow-up questions without diving into compiler implementation details unless the interviewer specifically asks about them. The next chapter should naturally be **JRE**, because once we know the development tools, we can examine what the runtime environment actually consists of before finally opening up the JVM itself.



Perfect. This is where many developers have an incorrect understanding.

Most people say:

> **JRE = JVM + Libraries**

While technically true, that's an oversimplification. For a **7+ years interview**, you should understand **what exactly the JRE contains**, **why it exists**, and **how it interacts with the JVM**.

---

# Chapter 2 - JRE (Java Runtime Environment)

---

# What is JRE?

The **Java Runtime Environment (JRE)** provides everything required to **execute a Java application**.

Notice the wording carefully.

It **does not develop** Java applications.

It **does not compile** Java applications.

Its only responsibility is:

> **Run already compiled Java applications.**

Think of it like this.

```text
Developer                    End User

Needs JDK                   Needs JRE

Write Code                  Run Application

Compile Code                Execute Application

Debug                       Nothing Else
```

---

# Why was JRE introduced?

Imagine there was no JRE.

Every user who wants to run a Java application would need the complete JDK.

Example

```text
Developer Tools

javac

jar

javadoc

jdb

jshell

...

User

↓

Never uses any of these.
```

That would waste:

* Disk Space
* Memory
* Installation Size

Therefore Java separated

```text
JDK

↓

Development

-------------------

JRE

↓

Runtime
```

---

# Where does JRE sit?

```
                 JDK
                  │
      ┌───────────┴────────────┐
      │                        │
 Development Tools            JRE
                              │
              ┌───────────────┴──────────────┐
              │                              │
      Java Runtime Libraries               JVM
              │
      Native Support Files
```

Notice

The JVM is **inside** the JRE.

The runtime libraries are also **inside** the JRE.

---

# What exactly does JRE contain?

This is the interview question.

```
                      JRE

------------------------------------------------

1. JVM

2. Java Runtime Libraries

3. Native Libraries

4. Configuration Files

5. Resources

------------------------------------------------
```

Let's understand every component.

---

# 1. JVM

The first component inside the JRE is the JVM.

The JVM is the execution engine.

Its responsibilities include:

* Load classes
* Verify bytecode
* Allocate memory
* Execute bytecode
* Manage threads
* Perform JIT compilation
* Garbage Collection

We'll study these in detail later.

For now, think of it as:

```
JVM

↓

Runs Java Bytecode
```

---

# 2. Java Runtime Libraries

This is the largest part of the JRE.

These are the APIs we use every day.

Example

```java
String

ArrayList

HashMap

Optional

Files

Thread

Socket

Collections
```

Question

Where do these classes come from?

Answer

Inside the runtime libraries.

---

## Packages included

```
java.lang

java.util

java.io

java.nio

java.net

java.sql

java.time

java.math

java.security

java.text

...
```

Each package contains hundreds of Java classes.

Example

```
java.lang

↓

Object

String

Thread

Math

System

Integer

Double

Boolean
```

---

# Important Point

These are **normal Java classes**.

Example

```
String.java

↓

javac

↓

String.class
```

Same for

```
HashMap.java

↓

HashMap.class
```

The JVM executes these classes exactly like it executes your own classes.

Nothing magical.

---

# Example

Suppose you write

```java
public class Main {

    public static void main(String[] args) {

        ArrayList<String> list =
                new ArrayList<>();

        list.add("Java");
    }

}
```

Question

Who provides

```
ArrayList
```

Answer

Runtime Library

Question

Who loads

```
ArrayList.class
```

Answer

JVM

Question

Who executes

```
ArrayList.add()
```

Answer

JVM

Question

Who allocates the object?

Answer

JVM

Notice the separation of responsibilities.

---

# Runtime Libraries vs JVM

Many people confuse these.

```
ArrayList.java

↓

Compiled

↓

ArrayList.class

↓

Loaded by JVM

↓

Executed by JVM

↓

Stored in Heap managed by JVM
```

So

Libraries provide functionality.

The JVM provides the execution environment.

---

# 3. Native Libraries

This is a topic many candidates don't mention.

The JVM itself is written mainly in C/C++.

Sometimes Java needs to interact with native code.

Examples include:

* File System
* Keyboard
* Mouse
* Networking
* Operating System APIs
* Fonts
* Graphics
* Compression
* Cryptography

These capabilities are provided through native libraries.

Examples:

```
Windows

↓

DLL Files

-------------------

Linux

↓

.so Files

-------------------

macOS

↓

.dylib Files
```

These are not Java classes.

They are operating-system-specific binaries.

---

# Why are Native Libraries needed?

Suppose Java wants to read a file.

```java
Files.readString(path);
```

Eventually,

the operating system must perform the actual disk I/O.

The JVM cannot directly access the hardware.

Instead,

```
Java Code

↓

JVM

↓

Native Library

↓

Operating System

↓

Disk
```

The same applies to sockets, timers, process creation, and many other OS-level operations.

---

# 4. Configuration Files

The runtime also contains configuration information such as:

* Security policies
* Character encodings
* Locale data
* Time zone data
* Module configuration

These help the runtime behave consistently across platforms.

---

# 5. Resources

The runtime bundles various data resources, including:

* Unicode tables
* Time zone databases
* Certificate stores
* Internationalisation (i18n) data
* Localisation (l10n) resources

For example, when you write:

```java
ZoneId.of("Asia/Kolkata")
```

The mapping between `"Asia/Kolkata"` and its time zone rules comes from data shipped with the runtime.

---

# Complete JRE Architecture

```
                   JRE
────────────────────────────────────────────

                JVM
                  │
   ┌──────────────┼──────────────┐
   │              │              │
Class Loader   Memory Mgmt   Execution

────────────────────────────────────────────

        Java Runtime Libraries

java.lang

java.util

java.io

java.nio

java.net

java.sql

java.time

...

────────────────────────────────────────────

Native Libraries

Windows DLL

Linux SO

macOS DYLIB

────────────────────────────────────────────

Configuration

Security

Locales

Charset

Time Zone

Certificates

────────────────────────────────────────────
```

---

# Common Misconceptions

## Misconception 1

> String is part of the JVM.

Wrong.

```
String

↓

java.lang.String

↓

Runtime Library
```

The JVM simply loads and executes it.

---

## Misconception 2

> HashMap is implemented inside the JVM.

Wrong.

HashMap is a Java class.

You can read its source code in OpenJDK.

---

## Misconception 3

> The JVM knows how ArrayList works.

Not really.

The JVM executes the bytecode inside `ArrayList.class`.

It doesn't contain a hardcoded implementation of `ArrayList`.

---

# Interview Questions

## Q1. What is the JRE?

**Answer**

The Java Runtime Environment provides everything required to execute Java applications. It includes the JVM, the standard Java runtime libraries, native libraries, configuration files, and runtime resources. It does not include development tools like `javac` or `javadoc`, which are part of the JDK.

---

## Q2. Why does the JRE include both the JVM and runtime libraries?

Because they have different responsibilities.

* The **JVM** is the execution engine. It loads classes, manages memory, executes bytecode, performs JIT compilation, and handles garbage collection.
* The **runtime libraries** provide the standard Java APIs such as `String`, `ArrayList`, `HashMap`, `Files`, and `Thread`.

Without the JVM, the libraries cannot execute. Without the libraries, applications would lack the standard functionality they depend on.

---

## Q3. Can the JVM run without the runtime libraries?

Technically, a JVM can exist without the standard Java libraries, but it would not be able to execute ordinary Java applications because almost every program depends on classes like `Object`, `String`, and `System`. In practice, a usable Java runtime includes both the JVM and the runtime libraries.

---

# Key Takeaways

```
JRE
│
├── JVM
│   ├── Executes bytecode
│   ├── Manages memory
│   ├── Performs GC
│   ├── Loads classes
│   └── Runs threads
│
├── Java Runtime Libraries
│   ├── String
│   ├── Object
│   ├── ArrayList
│   ├── HashMap
│   ├── Files
│   └── Thread
│
├── Native Libraries
│
├── Configuration
│
└── Runtime Resources
```

This sets us up perfectly for the next chapter: **JVM**. We'll no longer treat it as a black box—we'll open it up layer by layer, starting with **what happens inside the JVM from the moment the `java` launcher starts it** and then exploring each subsystem (Class Loader, Runtime Data Areas, Execution Engine, JIT, Garbage Collector, JNI, and more) in interview-level depth.



Perfect. This is the **heart of Java**.

Everything we've learned so far was just preparation.

```
JDK
   ↓
JRE
   ↓
JVM   ← We are here
```

This is also one of the most important interview topics because **HashMap, Threads, GC, Spring, Reflection, Serialization, ClassLoader, Performance**—everything eventually depends on the JVM.

---

# Chapter 3 - JVM (Java Virtual Machine)

---

# First Interview Question

> **What is JVM?**

Most people answer:

> "JVM is a virtual machine which executes Java bytecode."

This is **correct but only 20% of the answer.**

For a senior interview, you should understand **what JVM really is.**

---

# What is JVM?

The JVM (Java Virtual Machine) is a **software implementation of the JVM Specification** that provides the complete runtime environment for executing Java bytecode.

It is an actual **native application** (mostly written in C/C++) that runs as a normal operating system process.

Think of it as another application like Chrome or IntelliJ.

```
Operating System

├── Chrome Process
├── VS Code Process
├── Docker Process
└── JVM Process   ← This is HotSpot/OpenJ9/GraalVM
```

When you run

```bash
java Main
```

the operating system creates a **JVM process**.

That JVM process is responsible for everything that happens afterwards.

---

# What exactly does the JVM do?

This is the answer interviewers expect.

The JVM is responsible for:

```
                JVM

        Loads Classes

              ↓

      Verifies Bytecode

              ↓

     Creates Runtime Memory

              ↓

       Executes Bytecode

              ↓

        Creates Objects

              ↓

      Manages Threads

              ↓

      Performs JIT Compilation

              ↓

     Garbage Collection

              ↓

 Executes Native Methods

              ↓

      Terminates Process
```

Notice something.

Converting bytecode to machine code is **only one responsibility**.

The JVM is much bigger than that.

---

# Think of the JVM as an Operating System for Java

This analogy is extremely useful.

Your operating system provides:

* Memory
* Processes
* Threads
* File access
* CPU scheduling

Similarly, the JVM provides Java-specific services.

```
               Operating System

        Process

        Thread

        Memory

        File System

        Network

──────────────────────────────────

                JVM

        Heap

        Java Stack

        Class Loader

        GC

        JIT

        Interpreter

        Thread Management
```

Your Java application talks to the JVM.

The JVM talks to the operating system.

---

# Complete JVM Architecture

This is one of the most important diagrams in Java interviews.

```
                    JVM
──────────────────────────────────────────────

            Class Loader Subsystem
                     │
                     ▼
            Bytecode Verifier
                     │
                     ▼
            Runtime Data Areas
────────────────────────────────────

Heap

Java Stack

Metaspace

PC Register

Native Method Stack

────────────────────────────────────
                     │
                     ▼
            Execution Engine
────────────────────────────────────

Interpreter

JIT Compiler

Garbage Collector

────────────────────────────────────
                     │
                     ▼
        Native Method Interface (JNI)
                     │
                     ▼
           Native Libraries
                     │
                     ▼
            Operating System
```

If you understand this diagram, almost every advanced Java topic has a place in your mind.

---

# Let's understand every box

---

# 1. Class Loader

Question

```
Where does Main.class come from?
```

The JVM cannot execute a class that isn't loaded.

So the first responsibility is

```
Disk

↓

Main.class

↓

Class Loader

↓

Memory
```

The Class Loader:

* Finds the class
* Reads the `.class` file
* Creates runtime metadata
* Makes the class available for execution

We'll dedicate an entire chapter to Class Loaders later.

---

# 2. Bytecode Verifier

Suppose someone edits a `.class` file manually.

Example

```
Invalid bytecode

↓

Jump into middle of instruction

↓

Corrupted stack

↓

Illegal memory access
```

Should the JVM execute it?

No.

The Bytecode Verifier checks that the bytecode follows JVM rules before execution.

It prevents malformed or malicious bytecode from breaking JVM guarantees.

---

# 3. Runtime Data Areas

Once classes are loaded, the JVM creates memory.

```
Heap

↓

Objects

------------------------

Stack

↓

Method Calls

------------------------

Metaspace

↓

Class Metadata

------------------------

PC Register

↓

Current Instruction

------------------------

Native Stack

↓

JNI Calls
```

These are logical memory areas defined by the JVM specification.

We'll study each one in depth.

---

# 4. Execution Engine

Now the JVM is ready to execute bytecode.

The execution engine contains three major components.

```
Execution Engine

↓

Interpreter

↓

JIT Compiler

↓

Garbage Collector
```

---

## Interpreter

Initially, the JVM executes bytecode instruction by instruction.

Example bytecode:

```
iload_0

iload_1

iadd

istore_2
```

The interpreter reads one instruction at a time.

Simple.

Reliable.

But slower.

---

## JIT Compiler

Suppose this method executes

```
10 times

100 times

1000 times

100000 times
```

Instead of interpreting forever,

the JVM compiles that hot method into native machine code.

```
Bytecode

↓

JIT

↓

Machine Code

↓

CPU
```

Next execution skips interpretation.

Huge performance improvement.

---

## Garbage Collector

Suppose

```java
Student s = new Student();

s = null;
```

Nobody references the object anymore.

The Garbage Collector eventually identifies it as unreachable and reclaims its memory.

Without GC,

Java developers would need manual memory management like C/C++.

---

# 5. Native Method Interface (JNI)

Java cannot directly call operating system APIs.

Example

```java
Files.readString(path);
```

Eventually,

the operating system performs the actual file read.

The flow looks like:

```
Java Code

↓

JVM

↓

JNI

↓

Native Library

↓

Operating System
```

JNI is the bridge between Java and native code.

---

# 6. Native Libraries

These are operating-system-specific libraries.

```
Windows

↓

DLL

------------------

Linux

↓

.so

------------------

macOS

↓

.dylib
```

Examples:

* File I/O
* Network I/O
* Graphics
* Compression
* Cryptography

---

# Responsibilities of the JVM

This is a favourite interview question.

```
JVM Responsibilities

✓ Load classes

✓ Verify bytecode

✓ Create runtime memory

✓ Execute bytecode

✓ Create objects

✓ Manage threads

✓ Synchronization

✓ Garbage Collection

✓ JIT Compilation

✓ Native method execution

✓ Exception handling
```

Notice again—

It does much more than "run Java code."

---

# What the JVM does NOT do

This is equally important.

The JVM does **not** provide:

```
String

HashMap

ArrayList

Files

Collections

Optional

Stream
```

These belong to the **Java Runtime Libraries** (part of the JRE).

The JVM **loads and executes** these classes.

It doesn't implement their business logic.

---

# Mental Model

This is the picture I use when teaching senior engineers.

```
             Your Application
──────────────────────────────────

Main.class

Employee.class

Service.class

──────────────────────────────────

        Java Runtime Libraries

String.class

HashMap.class

ArrayList.class

Thread.class

Files.class

──────────────────────────────────

                 JVM

Class Loader

Verifier

Heap Manager

Thread Manager

Interpreter

JIT Compiler

Garbage Collector

JNI

──────────────────────────────────

          Operating System

──────────────────────────────────

             Hardware
```

Everything above the JVM is **Java bytecode**.

Everything below the JVM is **native operating system code**.

The JVM is the bridge between these two worlds.

---

# Interview Questions

### Q1. Is the JVM an application or a specification?

The term "JVM" can refer to either:

* The **JVM Specification**, which defines the required behaviour.
* A **JVM implementation**, such as HotSpot or OpenJ9, which is a native application implementing that specification.

When you install Java, you install a JVM implementation.

---

### Q2. Is the JVM part of the operating system?

No.

The JVM is a user-space process started by the operating system.

It uses OS services but is not part of the OS.

---

### Q3. Does the JVM know how `HashMap` works?

No.

`HashMap` is implemented as Java classes in the runtime libraries.

The JVM loads the `HashMap.class` bytecode, allocates its objects, and executes its methods.

---

### Q4. Why is the JVM called a Virtual Machine?

Because it provides a complete execution environment that abstracts away platform-specific details. Java applications interact with the JVM instead of directly interacting with the operating system or hardware, giving the illusion of running on their own machine.

---

# Before We Go Deeper

Now that you know **what the JVM is**, we should stop here.

The rest of the JVM consists of **independent interview topics**, each of which deserves its own deep dive:

```
JVM
│
├── Class Loader ⭐⭐⭐⭐⭐
├── Bytecode Verification ⭐⭐⭐
├── Runtime Data Areas ⭐⭐⭐⭐⭐
├── Execution Engine ⭐⭐⭐⭐⭐
│     ├── Interpreter
│     ├── JIT Compiler
│     └── Garbage Collector
├── JNI ⭐⭐⭐
└── Native Libraries ⭐⭐
```

I recommend we go **exactly in this order**, because that's also the order in which the JVM executes a Java program internally. By the end, you'll not only answer interview questions confidently but also understand the complete lifecycle from `java Main` to CPU execution.
