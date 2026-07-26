Perfect. Now we move to the **third Runtime Data Area**.

This is an important topic because it connects directly with **Class Loading**, which we've already studied.

---

# Chapter 8.3 - Metaspace ⭐⭐⭐⭐

---

# What is Metaspace?

**Definition (Interview)**

**Metaspace** is a **shared runtime memory area** that stores **class metadata** required by the JVM.

In simple words,

> **If the Heap stores object instances, Metaspace stores information about the classes from which those objects are created.**

Remember this distinction.

* **Heap → Objects**
* **Metaspace → Class Definitions**

---

# Why do we need Metaspace?

Suppose you have:

```java
class Student {

    int id;
    String name;

    void study() {
    }
}
```

Now you create an object.

```java
Student student = new Student();
```

Question:

How does the JVM know

* Object size?
* Number of fields?
* Constructor?
* Methods?
* Parent class?

The object itself doesn't contain this information.

The JVM reads it from the class metadata stored in **Metaspace**.

---

# Heap vs Metaspace

Think of it like a blueprint and a building.

```text
               Student.class
             (Blueprint)
                   │
                   ▼
              Metaspace

        Field Metadata
        Method Metadata
        Constructor Info
        Class Information

                   │
                   ▼

          new Student()

                   │
                   ▼

                Heap

           Student Object
```

Blueprint ≠ Building

Similarly,

Class ≠ Object

---

# Relationship Between Heap and Metaspace

Suppose

```java
Student s1 = new Student();

Student s2 = new Student();
```

Memory looks like

```text
                Metaspace

        +----------------------+
        | Student.class        |
        |----------------------|
        | Fields               |
        | Methods              |
        | Constructors         |
        +----------------------+
                  ▲
                  │
      ┌───────────┴───────────┐
      │                       │

                Heap

      +-------------+   +-------------+
      | Student #1  |   | Student #2  |
      +-------------+   +-------------+
```

Notice:

There is

* One `Student.class`
* Many Student objects

Every object refers to the same class metadata.

---

# What is Stored in Metaspace?

For interviews, remember this list.

```text
Metaspace

├── Class Metadata
├── Method Metadata
├── Constructor Information
├── Field Metadata
├── Interface Information
├── Annotation Metadata
├── Runtime Constant Pool
└── Class Loader Information
```

Notice something.

Objects are **NOT** stored here.

---

# Is Static Data Stored in Metaspace?

This is a favourite interview question.

Consider:

```java
class Student {

    static int count = 100;

    int id;
}
```

Many people answer:

> Static variables are stored in Metaspace.

That was broadly true for older JVM implementations using **PermGen**.

### Since Java 8

The **class metadata** is stored in Metaspace.

The **static field itself** is associated with the class and resides on the Heap as part of the JVM's representation of the `Class` object.

A simple way to remember it:

```text
Metaspace
---------------------
Student.class metadata

↓

Heap
---------------------
Class<Student> object
↓

count = 100
```

For interviews, a safe answer is:

> **Metaspace stores class metadata. Static fields are associated with the loaded class, but in modern HotSpot JVMs the storage for static field values is on the Heap, not in Metaspace.**

---

# Runtime Constant Pool

Every class has a Runtime Constant Pool.

Example

```java
System.out.println("Hello");
```

The literal

```text
"Hello"
```

is referenced through constant pool information.

The Runtime Constant Pool is created from the constant pool in the class file when the class is loaded.

We'll cover the Constant Pool in detail later.

For now,

remember:

```text
Runtime Constant Pool

↓

Lives with the class metadata

↓

Part of Metaspace
```

---

# PermGen vs Metaspace

A very common interview question.

### Java 7 and Earlier

```text
JVM

↓

PermGen
```

Problems:

* Fixed size
* Easy to run out of memory
* Required manual tuning

---

### Java 8+

PermGen was removed.

It was replaced by

```text
Metaspace
```

Benefits:

* Uses native memory
* Can grow as needed (subject to limits)
* Fewer memory issues in typical applications

---

# OutOfMemoryError: Metaspace

Suppose an application continuously loads new classes but never unloads them.

Eventually,

the JVM may throw

```text
java.lang.OutOfMemoryError:
Metaspace
```

This commonly happens due to:

* Excessive dynamic class generation (e.g., proxies, bytecode generation libraries)
* Class loader leaks
* Application servers repeatedly deploying applications without releasing class loaders

---

# Heap vs Metaspace

| Heap                                          | Metaspace                                                                                                         |
| --------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| Stores object instances                       | Stores class metadata                                                                                             |
| Shared by all threads                         | Shared by all threads                                                                                             |
| Managed by GC                                 | Class metadata can be reclaimed when the corresponding class loader becomes unreachable and the class is unloaded |
| Contains arrays                               | Contains method and field metadata                                                                                |
| Can throw `OutOfMemoryError: Java heap space` | Can throw `OutOfMemoryError: Metaspace`                                                                           |

---

# Complete Picture

```text
                   JVM

        +--------------------------+
        |        Metaspace         |
        |--------------------------|
        | Student.class            |
        | Employee.class           |
        | Runtime Constant Pool    |
        | Method Metadata          |
        +--------------------------+
                    │
                    │
                    ▼

              Heap

        +------------------+
        | Student Object   |
        +------------------+

        +------------------+
        | Student Object   |
        +------------------+

        +------------------+
        | Student Object   |
        +------------------+
```

One class definition.

Many object instances.

---

# Interview Questions

### Q1. What is Metaspace?

Metaspace is a **shared runtime memory area** that stores class metadata such as fields, methods, constructors, annotations, and runtime constant pool information.

---

### Q2. Does Metaspace store objects?

No.

Objects are stored in the Heap.

Metaspace stores information **about the class**, not its instances.

---

### Q3. Why was PermGen replaced?

PermGen had a fixed size and often caused memory issues.

Metaspace uses native memory and can grow dynamically (within configured limits), making it more flexible.

---

### Q4. Where are static variables stored?

For modern HotSpot JVMs (Java 8+):

* **Class metadata** → Metaspace
* **Static field values** → Heap (associated with the `Class` object)

This distinction demonstrates an up-to-date understanding.

---

### Q5. What causes `OutOfMemoryError: Metaspace`?

Typically:

* Loading too many classes
* Dynamic proxy or bytecode generation
* Class loader leaks that prevent class unloading

---

# Interview Boundary

For a **7+ years Backend Java interview**, this is sufficient.

Know:

* What Metaspace is
* Why it exists
* What it stores
* Heap vs Metaspace
* PermGen vs Metaspace
* `OutOfMemoryError: Metaspace`

**Deep Dive (Optional):**

* Class unloading
* Metaspace allocation internals
* Compressed Class Space
* HotSpot Metaspace allocator
* Class loader data structures

Those topics are valuable if you're interviewing for JVM or performance-focused roles, but they're not typically expected in standard backend interviews.

---

## Next Topic

The next Runtime Data Area is **Program Counter (PC) Register ⭐⭐⭐**.

It's a relatively small topic (about 10–15 minutes to master) and is often asked only as part of broader JVM memory questions rather than on its own.
