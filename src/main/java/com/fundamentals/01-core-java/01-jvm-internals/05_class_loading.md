I agree. At this point, we should optimise for **interview ROI**, not for covering every possible JVM detail.

From now on, let's divide topics into three categories:

* **⭐⭐⭐⭐⭐ Must Know** – Asked very frequently in 7+ years interviews. We'll cover these in depth with diagrams, internal flow, examples, production scenarios, and interview questions.
* **⭐⭐⭐ Good to Know** – We'll cover these at a medium depth. Enough to answer interviews confidently, without spending hours on internals.
* **⭐ Nice to Know** – High-level overview only. We'll revisit only if needed.

For example:

| Topic                | Priority | Coverage   |
| -------------------- | -------- | ---------- |
| Class Loading        | ⭐⭐⭐⭐⭐    | Deep       |
| Runtime Data Areas   | ⭐⭐⭐⭐⭐    | Deep       |
| Execution Engine     | ⭐⭐⭐⭐⭐    | Deep       |
| JIT Compiler         | ⭐⭐⭐⭐⭐    | Deep       |
| Garbage Collection   | ⭐⭐⭐⭐⭐    | Deep       |
| `javap`              | ⭐⭐⭐      | Medium     |
| Class File Structure | ⭐⭐       | High-level |
| JNI                  | ⭐⭐       | High-level |
| Native Libraries     | ⭐        | Overview   |

I think this will save us a lot of time while focusing on what actually gets asked.

---

# Next Topic: Class Loading ⭐⭐⭐⭐⭐

This is one of the highest ROI JVM topics.

## Why does Class Loading exist?

Imagine you've written:

```java
public class Main {
    public static void main(String[] args) {
        Student student = new Student();
    }
}
```

The `Student.class` file is sitting somewhere on disk.

Can the CPU execute code directly from a file on disk?

**No.**

The JVM must first:

* Find the `.class` file.
* Read its contents.
* Verify it's a valid Java class.
* Create internal metadata.
* Make it available in JVM memory.

This entire process is called **Class Loading**.

---

## High-Level Flow

```text
                Disk
         ┌─────────────────┐
         │   Main.class    │
         │ Student.class   │
         │ Object.class    │
         └─────────────────┘
                  │
                  ▼
          Class Loader Subsystem
                  │
                  ▼
      JVM Runtime (Memory Structures)
                  │
                  ▼
          Execution Engine
                  │
                  ▼
                 CPU
```

Without class loading, the JVM has nothing to execute.

---

## What happens when you run `java Main`?

At a high level:

```text
Terminal
    │
    ▼
java Main
    │
    ▼
Operating System starts JVM process
    │
    ▼
JVM initialises
    │
    ▼
Bootstrap ClassLoader loads core Java classes
    │
    ▼
Application ClassLoader loads Main.class
    │
    ▼
Main class is linked
    │
    ▼
Main class is initialised
    │
    ▼
main() method is invoked
```

Notice that **the JVM doesn't immediately jump to `main()`**. A lot happens first.

---

## The Three Built-in Class Loaders

Every JVM has three primary class loaders.

```text
                 Bootstrap
                      │
                      ▼
                 Platform
                      │
                      ▼
                Application
```

### 1. Bootstrap ClassLoader

Loads the core Java classes required for the JVM itself.

Examples:

* `java.lang.Object`
* `java.lang.String`
* `java.lang.System`
* `java.util.ArrayList`

Without these classes, the JVM couldn't even start executing Java code.

---

### 2. Platform ClassLoader

Loads standard platform modules that are not part of the core bootstrap classes.

Examples include parts of:

* `java.sql`
* `java.xml`
* `java.management`

---

### 3. Application ClassLoader

Loads your application's classes.

Examples:

* `Main.class`
* `Student.class`
* `EmployeeService.class`

Everything you write is typically loaded here.

---

## Interview Question

> **Who loads `String.class`?**

**Answer:** Bootstrap ClassLoader.

---

> **Who loads `Main.class`?**

**Answer:** Application ClassLoader.

---

## The Class Loading Lifecycle

Loading a class isn't just "read the file."

It has three phases:

```text
Class Loading

↓

Loading

↓

Linking

↓

Initialization
```

Inside **Linking**:

```text
Linking

├── Verification
├── Preparation
└── Resolution
```

So the complete lifecycle is:

```text
Loading
     │
     ▼
Verification
     │
     ▼
Preparation
     │
     ▼
Resolution
     │
     ▼
Initialization
```

We'll deep dive into each phase next because they are asked very frequently.

---

## High-Value Interview Questions

These are the questions you should definitely be able to answer after this chapter:

* What is Class Loading?
* Why is a ClassLoader needed?
* What are the three built-in ClassLoaders?
* What is the Parent Delegation Model?
* What are Loading, Linking, and Initialisation?
* When do static variables and static blocks execute?
* What is the difference between `ClassNotFoundException` and `NoClassDefFoundError`?
* How does Spring Boot load your application classes?
* Can two ClassLoaders load the same class?
* Why are custom ClassLoaders used?

---

## We'll cover the rest in this order

1. **Loading** (what actually happens when a `.class` file is read)
2. **Parent Delegation Model** (one of the most asked interview concepts)
3. **Linking** (Verification, Preparation, Resolution)
4. **Initialization** (static fields, static blocks, class initialisation triggers)
5. **ClassNotFoundException vs NoClassDefFoundError**
6. **Custom ClassLoaders & real-world use cases**

This gives you nearly all of the Class Loading knowledge expected in senior backend Java interviews without getting lost in JVM implementation details.


Perfect. Now we enter the **most asked part** of Class Loading.

This topic appears in almost every senior Java interview.

---

# Chapter 6.1 - Class Loading Lifecycle

After the JVM decides that a class is needed, it doesn't simply read the `.class` file and execute it.

It goes through **five well-defined phases**.

```
          Main.class
               │
               ▼
          Loading
               │
               ▼
          Verification
               │
               ▼
          Preparation
               │
               ▼
          Resolution
               │
               ▼
         Initialization
               │
               ▼
           Ready to Execute
```

Notice that **Verification, Preparation and Resolution together are called Linking.**

```
Loading
    │
    ▼
Linking
    ├── Verification
    ├── Preparation
    └── Resolution
    │
    ▼
Initialization
```

---

# Complete Flow

Let's understand it using this class.

```java
public class Student {

    static int count = 100;

    static {
        System.out.println("Static Block");
    }

    private String name;

    public Student() {
    }
}
```

When the JVM needs this class, it performs:

```
Student.class

↓

Load bytes from disk

↓

Verify class

↓

Allocate static memory

↓

Resolve symbolic references

↓

Execute static initializers

↓

Class Ready
```

---

# Phase 1 - Loading

This is the job of the **ClassLoader**.

Question:

Where is the class currently?

```
Disk

↓

Student.class
```

Can the JVM execute it directly?

No.

The ClassLoader reads the file.

```
Disk

↓

Student.class

↓

ClassLoader

↓

JVM Memory
```

What happens during Loading?

* Locate the class file.
* Read its bytes.
* Create an internal representation of the class.
* Store class metadata in **Metaspace**.
* Create a `java.lang.Class` object representing the class.

Visualisation:

```
               Disk

Student.class

        │
        ▼

ClassLoader

        │
        ▼

Metaspace

Class Metadata

Methods

Fields

Constructors

Interfaces

Annotations

        │
        ▼

Class Object

Student.class
```

### Important

Loading **does not execute any code.**

It simply makes the class available.

---

## Interview Question

### Does Loading create objects?

No.

Only the **class definition** is loaded.

Objects are created later using `new`.

---

# Phase 2 - Verification

Suppose someone edits a class file manually.

```
Student.class

↓

Hex Editor

↓

Random Bytes
```

Should the JVM execute it?

Absolutely not.

The verifier checks that the bytecode is valid.

Typical checks include:

* Correct class file format
* Valid bytecode instructions
* Type safety
* Valid stack operations
* No illegal jumps
* Proper method signatures

Think of it as a security guard.

```
Student.class

↓

Verifier

↓

Valid ?

Yes

↓

Continue

No

↓

ClassFormatError

VerifyError
```

---

## Why is verification important?

Imagine downloading an unknown JAR from the Internet.

Without verification, malicious bytecode could attempt to violate JVM safety guarantees.

Verification is one of the reasons Java is considered a secure platform.

---

# Phase 3 - Preparation

Many candidates answer this incorrectly.

Question:

What happens here?

People usually say

> Static variables are initialized.

This is **not completely correct.**

During Preparation,

the JVM allocates memory for **static variables** and assigns **default values**, **not your values**.

Example

```java
class Student {

    static int count = 100;

    static String name = "Java";

}
```

During Preparation:

```
count = 0

name = null
```

Not

```
count = 100

name = "Java"
```

Those assignments happen later.

---

## Why?

Because your Java code has not executed yet.

Preparation is handled entirely by the JVM.

---

# Phase 4 - Resolution

Now the JVM resolves symbolic references.

Example:

```java
Student s = new Student();
```

Inside the bytecode, references look more like:

```
Student

println

Object

String
```

These are **symbolic references**, not actual memory addresses.

During Resolution, the JVM finds the real runtime representations.

```
Symbolic Reference

↓

java/lang/String

↓

Locate String.class

↓

Resolved
```

Think of it like resolving a contact name to a phone number.

```
"Himanshu"

↓

Contact List

↓

+91xxxxxxxxxx
```

Before resolution, the JVM knows only the symbolic name.

After resolution, it knows the actual runtime entity.

---

# Phase 5 - Initialization

This is where your Java code finally executes.

Remember our class?

```java
class Student {

    static int count = 100;

    static {

        System.out.println("Hello");

    }

}
```

Only during Initialization does the JVM execute:

```
count = 100

↓

Static Block

↓

Class Ready
```

Output:

```
Hello
```

Now the class is fully initialized.

---

# Complete Timeline

```
Student.class

↓

Loading
(Read class)

↓

Verification
(Is class valid?)

↓

Preparation
(Create static memory
Assign default values)

↓

Resolution
(Convert symbolic references)

↓

Initialization
(Execute static variables
Execute static blocks)

↓

Ready
```

---

# Interview Trap

Consider this code.

```java
class Test {

    static int x = 100;

}
```

Question:

During Preparation, what is the value of `x`?

Answer:

```
0
```

Question:

When does it become `100`?

Answer:

During **Initialization**.

This is one of the most common senior interview questions.

---

# Real Example

```java
class A {

    static {

        System.out.println("A Loaded");

    }

}
```

```java
public class Main {

    public static void main(String[] args) {

        System.out.println("Start");

        new A();

    }

}
```

Execution:

```
Main Loaded

↓

main()

↓

new A()

↓

Loading

↓

Verification

↓

Preparation

↓

Resolution

↓

Initialization

↓

Print

A Loaded

↓

Constructor Executes
```

Notice:

The static block executes **before** the constructor because the class must be initialized before any instance is created.

---

# Summary Table

| Phase          | Purpose                                             | Executes Java Code? |
| -------------- | --------------------------------------------------- | ------------------- |
| Loading        | Read class and create metadata                      | ❌ No                |
| Verification   | Validate bytecode                                   | ❌ No                |
| Preparation    | Allocate static memory and assign default values    | ❌ No                |
| Resolution     | Convert symbolic references to runtime references   | ❌ No                |
| Initialization | Execute static field initializers and static blocks | ✅ Yes               |

---

# High-Frequency Interview Questions

### 1. What are the phases of class loading?

**Answer:** Loading → Linking (Verification, Preparation, Resolution) → Initialization.

---

### 2. When are static variables assigned their actual values?

During **Initialization**.

During **Preparation**, they only receive default values.

---

### 3. Which phase executes Java code?

Only **Initialization** executes your Java code (static initialisers and static blocks).

---

### 4. Which phase creates objects?

None of these phases create normal Java objects. They prepare the **class**. Objects are created later when code executes `new`.

---

## Next Topic (Very Important)

Now that you understand **how a class is loaded**, the next question naturally becomes:

> **Who performs the loading?**

That leads us to one of the most frequently asked JVM interview topics:

# **ClassLoaders & Parent Delegation Model**

This includes:

* Bootstrap ClassLoader
* Platform ClassLoader
* Application ClassLoader
* Parent Delegation Model
* Why it exists
* How it prevents duplicate loading
* `ClassNotFoundException` vs `NoClassDefFoundError`
* Custom ClassLoaders

This topic is asked far more often than most JVM internals in senior backend interviews, so we'll cover it in depth next.


Excellent. This is probably the **#1 most asked JVM interview topic** after Heap vs Stack and Garbage Collection.

If you understand **Parent Delegation Model**, you'll answer almost every ClassLoader interview question confidently.

---

# Chapter 6.2 - ClassLoaders & Parent Delegation Model ⭐⭐⭐⭐⭐

---

# First Question

We know the JVM needs to load classes.

But...

**Who actually loads them?**

Suppose we execute:

```bash
java Main
```

Our program uses

```java
String
ArrayList
HashMap
Student
Employee
Main
```

Who loads all these classes?

Is there one ClassLoader?

No.

Java uses multiple ClassLoaders.

---

# Why Multiple ClassLoaders?

Imagine there was only one.

```text
Single ClassLoader

↓

Loads Everything
```

Would this work?

Yes.

Would it be secure?

No.

Would it scale?

Not really.

Would Java modules work?

No.

So Java separates responsibilities.

---

# Built-in ClassLoaders

The JVM provides three primary ClassLoaders.

```text
               Bootstrap
                    │
                    ▼
               Platform
                    │
                    ▼
              Application
```

Think of them as a management hierarchy.

```text
CEO

↓

Manager

↓

Employee
```

Each has a specific responsibility.

---

# Bootstrap ClassLoader

This is the most important one.

Question:

Who loads

```java
String

Object

System

Integer

Thread
```

Answer:

Bootstrap ClassLoader.

These are the core Java classes.

Without them the JVM cannot even start.

Diagram:

```text
Bootstrap

↓

java.lang.Object

↓

java.lang.String

↓

java.lang.System

↓

java.util.*

↓

java.io.*
```

Interesting fact:

The Bootstrap ClassLoader is implemented inside the JVM (native code), not as a normal Java class.

---

# Platform ClassLoader

Introduced with the Java Platform Module System (Java 9).

It loads platform modules that are outside the minimal core runtime.

Examples:

```text
java.sql

java.xml

java.management

java.naming
```

Think of these as standard Java APIs that are not needed to bootstrap the JVM itself.

---

# Application ClassLoader

This is where **your code** comes from.

Example

```text
Main.class

↓

Student.class

↓

OrderService.class

↓

PaymentController.class
```

Everything you build in your application is usually loaded here.

---

# Complete Picture

```text
                     JVM

        Bootstrap ClassLoader
                │
                ▼
       java.lang.Object
       java.lang.String
       java.lang.System
                │
                ▼
        Platform ClassLoader
                │
                ▼
       java.sql
       java.xml
       java.management
                │
                ▼
      Application ClassLoader
                │
                ▼
       Main.class
       Student.class
       Service.class
```

---

# Interview Question

Who loads `String.class`?

Bootstrap.

Who loads `ArrayList.class`?

Bootstrap.

Who loads `Main.class`?

Application ClassLoader.

Who loads `java.sql.Connection`?

Platform ClassLoader.

---

# How does loading actually happen?

Suppose

```java
public class Main {

    public static void main(String[] args) {

        Student student = new Student();

    }

}
```

Question:

Application ClassLoader wants `Student.class`.

Does it immediately load it?

No.

This is where the **Parent Delegation Model** comes in.

---

# Parent Delegation Model

This is one of the most frequently asked interview concepts.

The rule is simple:

> **A ClassLoader first asks its parent to load the class. It only loads the class itself if the parent cannot.**

Remember this sentence.

It is the essence of Parent Delegation.

---

# Complete Flow

Suppose we need

```text
Student.class
```

Application ClassLoader receives the request.

Does it load it immediately?

No.

```text
Application

↓

Ask Parent

↓

Platform

↓

Ask Parent

↓

Bootstrap
```

Bootstrap looks for the class.

```text
Bootstrap

↓

Student.class ?

↓

Not Found
```

Returns back.

```text
Bootstrap

↓

Not Found

↓

Platform
```

Platform tries.

```text
Platform

↓

Student.class ?

↓

Not Found
```

Returns back.

Finally

```text
Application

↓

Student.class ?

↓

Found

↓

Load Class
```

---

# Complete Delegation Flow

```text
Need Student.class

        │
        ▼

Application Loader

        │

Ask Parent

        ▼

Platform Loader

        │

Ask Parent

        ▼

Bootstrap Loader

        │

Found ?

No

        ▲

Return

        │

Platform

Found ?

No

        ▲

Return

        │

Application

Found ?

Yes

↓

Load Class
```

Notice:

The Application ClassLoader only loads the class after every parent says:

> "I don't have it."

---

# Another Example

Suppose we need

```java
String
```

Application receives request.

```text
Application

↓

Ask Parent
```

Platform receives.

```text
Platform

↓

Ask Parent
```

Bootstrap receives.

```text
Bootstrap

↓

String.class

↓

Found

↓

Load
```

Done.

Application never loads it.

---

# Why does Java use Parent Delegation?

This is the real interview question.

There are three major reasons.

---

## 1. Security

Imagine if your application could replace

```java
java.lang.String
```

with its own implementation.

```java
package java.lang;

public class String {

}
```

Without Parent Delegation,

your fake `String` might be loaded instead of the real one.

That would completely break the JVM and could introduce major security risks.

Because Bootstrap gets the first chance, the genuine JDK class is always loaded.

---

## 2. Avoid Duplicate Classes

Suppose there are 500 classes using

```java
String
```

Without delegation:

```text
Main Loader

↓

Loads String

Service Loader

↓

Loads String

DAO Loader

↓

Loads String
```

Multiple copies of the same core class would waste memory and cause type inconsistencies.

With Parent Delegation:

```text
Bootstrap

↓

Loads String Once

↓

Everyone Uses It
```

---

## 3. Consistency

If two different `String` classes existed:

```text
String A

String B
```

What happens?

```java
String s = ...
```

Which `String`?

Impossible.

Java guarantees that core classes are loaded once by the appropriate loader.

---

# Interview Question

## Can the same class be loaded twice?

Yes.

But only if **different ClassLoaders** load it.

Example:

```text
Loader A

↓

Student.class

-------------------

Loader B

↓

Student.class
```

The JVM considers these two different runtime classes because a class is uniquely identified by:

```text
(ClassLoader, Fully Qualified Class Name)
```

This is the basis for plugin systems, application servers, and OSGi.

---

# Common Interview Question

## Why do application servers use custom ClassLoaders?

Because each deployed application should have its own isolated set of classes.

For example:

```text
Tomcat

├── App 1
│     Spring 5
│
└── App 2
      Spring 6
```

Without separate ClassLoaders, the two applications would conflict over library versions.

Custom ClassLoaders provide isolation.

---

# Summary

```text
                    JVM

       Bootstrap ClassLoader
             (Core JDK)
                    │
                    ▼
       Platform ClassLoader
        (Platform Modules)
                    │
                    ▼
     Application ClassLoader
        (Your Application)

Parent Delegation Rule

Child receives request
        │
        ▼
Ask Parent
        │
        ▼
Parent tries first
        │
        ▼
If Parent cannot load
        │
        ▼
Child loads class
```

---

# Interview Questions

### Q1. What are the three built-in ClassLoaders?

* **Bootstrap ClassLoader** – Loads core JDK classes such as `java.lang` and `java.util`.
* **Platform ClassLoader** – Loads platform modules like `java.sql` and `java.xml`.
* **Application ClassLoader** – Loads classes from your application's classpath.

---

### Q2. What is the Parent Delegation Model?

A ClassLoader first delegates the request to its parent. Only if the parent cannot find the class does the child attempt to load it itself. This ensures security, avoids duplicate loading, and maintains consistency.

---

### Q3. Why doesn't the Application ClassLoader load `String.class`?

Because it delegates the request to its parent. The Bootstrap ClassLoader finds and loads `String.class` first, so the request never comes back down to the Application ClassLoader.

---

### Q4. Can two different ClassLoaders load the same `.class` file?

Yes. The JVM treats them as different classes because class identity is based on both the fully qualified class name and the ClassLoader that loaded it.

---

## What's Next?

The remaining high-value Class Loading topics are:

1. **Loading vs Linking vs Initialisation** (we've already covered this)
2. **Class Initialisation Triggers** (When exactly does a class initialise? `new`, static methods, static fields, reflection, etc.)
3. **`ClassNotFoundException` vs `NoClassDefFoundError`** ⭐⭐⭐⭐⭐ (one of the most commonly asked interview questions)
4. **Custom ClassLoaders** (medium priority)

For interview preparation, I recommend **`ClassNotFoundException` vs `NoClassDefFoundError` next**, because it is asked far more frequently than custom ClassLoaders and often catches candidates out.


Excellent choice.

This is probably one of the **top 10 most asked Java interview questions**.

Many developers simply memorize:

> **ClassNotFoundException → Compile Time**
>
> **NoClassDefFoundError → Runtime**

This answer is **wrong** (or at least very incomplete).

An interviewer will usually ask:

> **"Can you explain with an example?"**

Let's understand it properly.

---

# Chapter 6.3 - ClassNotFoundException vs NoClassDefFoundError ⭐⭐⭐⭐⭐

---

# First Understanding

Both are related to **class loading**.

But they occur at **different stages** and for **different reasons**.

Think of it this way.

```text
Need Student class

↓

Can JVM obtain it?

↓

YES → Continue

NO

↓

What kind of failure?

↓

ClassNotFoundException ?

or

NoClassDefFoundError ?
```

---

# First Difference

| ClassNotFoundException                               | NoClassDefFoundError                              |
| ---------------------------------------------------- | ------------------------------------------------- |
| Checked Exception                                    | Error                                             |
| Recoverable                                          | Usually not recoverable                           |
| Thrown when code explicitly asks JVM to load a class | JVM expected a class to exist but couldn't use it |

This distinction is the key.

---

# What is ClassNotFoundException?

Suppose you explicitly tell the JVM:

```java
Class.forName("com.demo.Student");
```

Now the JVM tries to locate

```
com.demo.Student
```

Suppose the class does not exist.

The JVM cannot find it.

It throws

```
ClassNotFoundException
```

Flow

```text
Class.forName()

↓

Load Student

↓

Class Exists?

↓

No

↓

ClassNotFoundException
```

---

## Real Example

```java
public class Main {

    public static void main(String[] args) throws Exception {

        Class.forName("com.demo.Student");

    }

}
```

Output

```text
java.lang.ClassNotFoundException:
com.demo.Student
```

---

# Why does it happen?

Because **your code explicitly requested a class**.

Examples

```java
Class.forName()

ClassLoader.loadClass()
```

The JVM simply says

> "Sorry, I cannot find this class."

---

# Common Real-world Example

JDBC.

Earlier versions required:

```java
Class.forName("com.mysql.jdbc.Driver");
```

If the JDBC driver JAR was missing,

you got

```text
ClassNotFoundException
```

---

# What is NoClassDefFoundError?

This one is different.

Suppose the class **existed during compilation**.

Everything compiled successfully.

```text
Main.java

↓

javac

↓

Main.class
```

Later someone deletes

```
Student.class
```

Now you run

```bash
java Main
```

The JVM needs Student.

It cannot find it.

It throws

```
NoClassDefFoundError
```

---

## Example

Compilation

```java
Student s = new Student();
```

Everything is fine.

Later

```
Student.class

↓

Deleted
```

Now execution.

```text
Main

↓

new Student()

↓

Need Student.class

↓

Missing

↓

NoClassDefFoundError
```

---

# Another Example

Suppose

```java
class Student {

}
```

Compile.

Everything succeeds.

Delete

```
Student.class
```

Run

```text
Exception in thread "main"

java.lang.NoClassDefFoundError
```

Notice.

You never called

```java
Class.forName()
```

The JVM itself required the class.

---

# The Biggest Interview Trap

Many people say

> ClassNotFoundException happens during compile time.

Wrong.

Both happen at **runtime**.

Let's repeat that.

## Both occur at Runtime.

Difference is **who requested the class**.

---

## ClassNotFoundException

Your code requested it.

```java
Class.forName()

↓

Load this class
```

---

## NoClassDefFoundError

The JVM requested it.

```java
new Student()

↓

Need Student

↓

Missing
```

---

# Side-by-side Comparison

## Scenario 1

```java
Class.forName("Student");
```

Result

```
ClassNotFoundException
```

---

## Scenario 2

```java
Student student = new Student();
```

Student.class removed.

Result

```
NoClassDefFoundError
```

---

# Another Important Cause of NoClassDefFoundError

This surprises many developers.

Consider:

```java
class Student {

    static {

        throw new RuntimeException();

    }

}
```

Now

```java
new Student();
```

Output

```
ExceptionInInitializerError
```

Now try again.

```java
new Student();
```

Output

```
NoClassDefFoundError
```

Why?

Because class initialization failed.

The JVM marks the class as **erroneous**.

It will never try to initialize it again in the same ClassLoader.

So subsequent uses result in

```
NoClassDefFoundError
```

This is a favourite senior-level interview scenario.

---

# Visual Comparison

## ClassNotFoundException

```text
Application

↓

Class.forName()

↓

Need Student

↓

Not Found

↓

ClassNotFoundException
```

---

## NoClassDefFoundError

```text
Application

↓

new Student()

↓

JVM Needs Student

↓

Missing

↓

NoClassDefFoundError
```

---

# Error vs Exception

Another interview favourite.

Why is one an Exception and the other an Error?

Because

**ClassNotFoundException**

```text
Developer asked

↓

Class missing

↓

Maybe fix classpath

↓

Continue
```

It is considered something your application may reasonably handle.

Example:

```java
try {

    Class.forName(driverName);

} catch (ClassNotFoundException e) {

    // Try another driver

}
```

---

**NoClassDefFoundError**

```text
JVM expected class

↓

Cannot continue safely

↓

Serious runtime problem
```

This usually indicates a deployment or initialization problem.

---

# Interview Comparison Table

| Feature                           | ClassNotFoundException                       | NoClassDefFoundError                                                     |
| --------------------------------- | -------------------------------------------- | ------------------------------------------------------------------------ |
| Type                              | Checked Exception                            | Error                                                                    |
| Happens at                        | Runtime                                      | Runtime                                                                  |
| Triggered by                      | `Class.forName()`, `ClassLoader.loadClass()` | JVM while resolving or initialising classes                              |
| Class existed during compilation? | Not required                                 | Usually yes                                                              |
| Can be caught?                    | Yes                                          | Yes (inherits from `Throwable`), but usually indicates a serious problem |
| Common cause                      | Missing JAR or incorrect class name          | Missing `.class` at runtime or failed class initialisation               |

> **Note:** Although `Error` can technically be caught, it generally represents conditions that applications are not expected to recover from.

---

# Interview Questions

## Q1. What's the difference between ClassNotFoundException and NoClassDefFoundError?

**Answer**

`ClassNotFoundException` is thrown when application code explicitly requests a class using APIs such as `Class.forName()` or `ClassLoader.loadClass()`, and the JVM cannot locate it.

`NoClassDefFoundError` occurs when the JVM itself expects a class to be available during normal execution (for example, while executing `new Student()` or resolving dependencies), but the class definition cannot be loaded or has previously failed to initialise.

---

## Q2. Which one is more common in production?

**NoClassDefFoundError.**

Typical causes include:

* Missing dependency JAR after deployment.
* Version mismatch between compile-time and runtime dependencies.
* Static initialisation failure.
* Corrupted deployment.

---

## Q3. Do both happen at runtime?

**Yes.**

This is one of the most common misconceptions.

Neither is a compile-time error.

---

# Interview Tip

If an interviewer asks:

> **"Explain ClassNotFoundException vs NoClassDefFoundError."**

A senior-level answer is:

> "`ClassNotFoundException` occurs when my code explicitly requests a class using APIs like `Class.forName()` and the JVM cannot locate it. `NoClassDefFoundError` occurs when the JVM itself needs a class during normal execution or class initialisation but cannot use it, either because the class is missing at runtime or because a previous initialisation failed. Both occur at runtime, but one is an application-level exception while the other is a JVM-level error."

---

## Class Loading Coverage

At this point, you've covered about **90–95% of the Class Loading knowledge** typically expected in senior backend Java interviews.

The only remaining topics are:

* **Class Initialisation Triggers** (when exactly a class is initialised)
* **Custom ClassLoaders** (useful for plugin architectures, application servers, hot deployment)

These are valuable, but they are asked less frequently than the topics we've already covered. After a brief pass through them, the next major chapter should be **Runtime Data Areas (Heap, Stack, Metaspace, PC Register, Native Method Stack)**, which is one of the highest-priority JVM topics for interviews.

Perfect. We'll do the **last high-ROI Class Loading topic** and then move to **Runtime Memory**.

I won't go into Custom ClassLoaders now because for a 7+ years backend interview, it's usually a 2-minute discussion unless you're interviewing for JVM, Tomcat, OSGi, or plugin framework roles.

---

# Chapter 6.4 - When is a Class Initialized? ⭐⭐⭐⭐⭐

This is a favourite interview question because many developers confuse **Loading** with **Initialization**.

Interviewer:

> **When exactly does the JVM execute a static block?**

or

> **When is a class initialized?**

Let's understand.

---

# First Remember

Loading ≠ Initialization

```text
Student.class

↓

Loading
(Read class)

↓

Linking
(Verify, Prepare, Resolve)

↓

Initialization
(Execute Java code)
```

Only during **Initialization** does the JVM execute:

* Static variable assignments
* Static blocks

---

# Example

```java
class Student {

    static {

        System.out.println("Student Loaded");

    }

}
```

Question:

When will this print?

Not when you compile.

Not when the JVM starts.

Only when the JVM decides the class must be initialized.

---

# Trigger 1 - Creating an Object ⭐⭐⭐⭐⭐

```java
Student student = new Student();
```

Flow

```text
new Student()

↓

Load Class (if needed)

↓

Link

↓

Initialize

↓

Execute Static Block

↓

Constructor

↓

Object Created
```

Output

```text
Student Loaded
```

This is the most common trigger.

---

# Trigger 2 - Calling a Static Method ⭐⭐⭐⭐⭐

```java
class Student {

    static {

        System.out.println("Loaded");

    }

    static void print() {

    }

}
```

```java
Student.print();
```

Flow

```text
Need Student

↓

Initialize Class

↓

Execute Static Block

↓

Execute print()
```

Output

```text
Loaded
```

---

# Trigger 3 - Accessing a Static Variable ⭐⭐⭐⭐⭐

```java
class Student {

    static int count = 100;

    static {

        System.out.println("Loaded");

    }

}
```

```java
System.out.println(Student.count);
```

Output

```text
Loaded

100
```

Accessing a non-constant static field triggers initialization.

---

# Interview Trap

Consider

```java
class Student {

    static final int AGE = 18;

    static {

        System.out.println("Loaded");

    }

}
```

```java
System.out.println(Student.AGE);
```

Question

Will it print

```text
Loaded
```

?

Answer

**No.**

Output

```text
18
```

---

# Why?

Because

```java
static final int AGE = 18;
```

is a **compile-time constant**.

The compiler replaces

```java
Student.AGE
```

with

```java
18
```

There is no need to initialize `Student`.

Conceptually:

```text
Source

↓

Student.AGE

↓

Compiler

↓

18
```

This optimisation is called **constant inlining**.

---

# But...

```java
static final Integer AGE = 18;
```

Now

```java
System.out.println(Student.AGE);
```

This **does** initialize the class.

Why?

Because `Integer` is an object.

It cannot be treated as a compile-time constant.

---

# Trigger 4 - Reflection ⭐⭐⭐⭐

```java
Class.forName("Student");
```

Flow

```text
Reflection

↓

Need Student

↓

Initialize Class

↓

Static Block Executes
```

This is why many frameworks initialise classes through reflection.

Examples:

* Spring
* Hibernate
* JDBC
* JUnit

---

# Trigger 5 - Main Class ⭐⭐⭐⭐⭐

```java
public class Main {

    public static void main(String[] args) {

    }

}
```

Before `main()` executes,

the JVM initializes `Main`.

So

```java
class Main {

    static {

        System.out.println("Main Loaded");

    }

    public static void main(String[] args) {

    }

}
```

Output

```text
Main Loaded
```

before `main()` starts.

---

# What DOES NOT Trigger Initialization?

This is where interviews become interesting.

---

## Creating an Array

```java
Student[] students = new Student[10];
```

Question

Will this initialize `Student`?

Answer

**No.**

Only an array object is created.

The `Student` class itself isn't initialized.

---

## Using `.class`

```java
Student.class
```

Does this initialize the class?

No.

The JVM returns the `Class` object without triggering initialization.

---

## Compile-Time Constants

We already saw

```java
Student.AGE
```

where

```java
static final int AGE = 18;
```

No initialization.

---

# Complete Summary

| Operation             | Initializes Class? |
| --------------------- | ------------------ |
| `new Student()`       | ✅ Yes              |
| Static method call    | ✅ Yes              |
| Access static field   | ✅ Yes              |
| `Class.forName()`     | ✅ Yes              |
| JVM starts `main()`   | ✅ Yes              |
| `Student.class`       | ❌ No               |
| `new Student[10]`     | ❌ No               |
| Compile-time constant | ❌ No               |

---

# Real Production Example

Spring Boot starts.

```text
Spring Boot

↓

Scans Components

↓

Reflection

↓

Loads Bean Classes

↓

Static Blocks Execute
```

Sometimes developers are surprised to see log messages from static blocks before any requests are handled. This is because Spring's reflective class loading has triggered class initialization.

---

# Interview Questions

### Q1. When does the JVM execute a static block?

During **class initialization**, not during loading or linking.

---

### Q2. Does `Student.class` initialize the class?

No.

It returns the `Class` object without triggering initialization.

---

### Q3. Does `new Student[100]` initialize `Student`?

No.

Only the array object is created.

---

### Q4. Why doesn't `static final int AGE = 18` initialize the class?

Because it is a **compile-time constant**. The compiler inlines the value into the calling code, so the JVM never needs to initialize `Student` just to read that constant.

---

# Class Loading is Complete ✅

For a senior backend interview, you've now covered essentially all of the high-value Class Loading topics:

* ✅ Class Loading lifecycle
* ✅ Loading, Linking, Initialization
* ✅ Bootstrap / Platform / Application ClassLoaders
* ✅ Parent Delegation Model
* ✅ `ClassNotFoundException` vs `NoClassDefFoundError`
* ✅ Class initialization triggers

---

# Next Chapter (One of the Biggest in JVM)

Now we move to **Runtime Data Areas**.

This is where interview frequency becomes extremely high.

```text
                    JVM
                     │
                     ▼
           Runtime Data Areas
                     │
     ┌─────────┬──────────┬────────────┬──────────────┬──────────────┐
     ▼         ▼          ▼            ▼              ▼
   Heap      Stack    Metaspace    PC Register   Native Method Stack
```

This chapter is foundational for understanding:

* Object creation
* Memory allocation
* Garbage Collection
* `OutOfMemoryError`
* `StackOverflowError`
* Thread behaviour
* Performance tuning

It's one of the most important JVM topics for 7+ years Java interviews, and we'll cover it in depth next.
