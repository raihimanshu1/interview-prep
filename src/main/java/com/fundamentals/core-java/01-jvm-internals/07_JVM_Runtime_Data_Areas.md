Excellent. Now we start what is arguably the **most important JVM topic** for senior Java interviews.

This topic connects to:

* Garbage Collection
* Object Creation
* Memory Leaks
* StackOverflowError
* OutOfMemoryError
* Multithreading
* Performance Tuning

If you master this, nearly every other JVM topic becomes easier.

---

# Chapter 7 - JVM Runtime Data Areas ⭐⭐⭐⭐⭐

---

# First Question

When you execute:

```bash
java Main
```

the JVM starts.

Question:

**Where does your program actually live?**

Many developers answer:

> "Inside the Heap."

That's **incorrect**.

A Java program uses **multiple memory areas**, each designed for a specific purpose.

Think of the JVM like a company.

```
                    JVM
                     │
     ┌───────────────┼────────────────┐
     │               │                │
     ▼               ▼                ▼
   Heap           Stack          Metaspace
 (Objects)     (Method Calls)   (Class Metadata)

           + PC Register + Native Stack
```

Each area has a different responsibility.

---

# Why doesn't the JVM use one large memory block?

Imagine the JVM allocated memory like this.

```
+-------------------------------------------------------+

Objects

Method Calls

Class Metadata

Variables

Native Code

Everything Mixed Together

+-------------------------------------------------------+
```

Problems:

* Object allocation becomes difficult.
* Garbage Collection becomes inefficient.
* Thread isolation is impossible.
* Stack unwinding is difficult.
* Class metadata cannot be managed independently.

Instead, the JVM divides memory into specialised regions.

---

# Complete JVM Memory

```
                   JVM Memory

          +-------------------------+
          |      Heap               |
          +-------------------------+

          +-------------------------+
          |     Metaspace           |
          +-------------------------+

Thread 1

+---------+
| PC      |
+---------+

+---------+
| Stack   |
+---------+

+---------+
| Native  |
| Stack   |
+---------+

----------------------------

Thread 2

+---------+
| PC      |
+---------+

+---------+
| Stack   |
+---------+

+---------+
| Native  |
| Stack   |
+---------+
```

Notice something very important.

There is:

* **One Heap**
* **One Metaspace**

But

Every thread gets its own

* Stack
* PC Register
* Native Stack

This is one of the favourite interview questions.

---

# JVM Runtime Data Areas

The JVM Specification defines five runtime data areas.

```
JVM Runtime Data Areas

├── Heap
├── Java Stack
├── Metaspace (Method Area)
├── Program Counter Register
└── Native Method Stack
```

We'll study each one separately.

---

# Shared vs Thread-Specific Memory

This is one of the most commonly asked questions.

```
                 Shared Memory

           Heap

           Metaspace

------------------------------------

          Per Thread Memory

Thread A

Stack

PC Register

Native Stack

------------------------

Thread B

Stack

PC Register

Native Stack
```

---

## Shared Memory

Accessible by every thread.

Contains:

* Objects
* Class Metadata
* Static Fields (associated with classes)

---

## Thread Memory

Private to each thread.

Contains:

* Method calls
* Local variables
* Current instruction
* Native method execution

One thread cannot directly access another thread's stack.

---

# Why does each thread need its own Stack?

Imagine two threads calling the same method.

```
Thread A

calculateSalary()

--------------------

Thread B

calculateSalary()
```

Both have different local variables.

```
Thread A

salary = 100

--------------------

Thread B

salary = 200
```

If they shared one stack...

```
salary ?

100 ?

200 ?

Corruption
```

Impossible.

Therefore,

each thread gets its own stack.

---

# High-Level Overview

Let's understand each memory area.

---

## 1. Heap ⭐⭐⭐⭐⭐

Purpose

Stores

```
Objects

Arrays

Instance Variables
```

Example

```java
Student student = new Student();
```

Object goes to Heap.

Reference goes to Stack.

---

## 2. Java Stack ⭐⭐⭐⭐⭐

Purpose

Stores

```
Method Calls

Local Variables

Partial Results

References
```

Every method invocation creates a new **Stack Frame**.

---

## 3. Metaspace ⭐⭐⭐⭐⭐

Purpose

Stores

```
Class Metadata

Method Metadata

Field Metadata

Runtime Constant Pool
```

Notice

Objects do **NOT** live here.

---

## 4. PC Register ⭐⭐⭐⭐

Stores

```
Current instruction

being executed
```

Each thread has one.

Without it,

the JVM cannot resume execution after a context switch.

---

## 5. Native Method Stack ⭐⭐⭐

Stores

execution information for

JNI (Java Native Interface)

Example

```java
System.loadLibrary();
```

or JVM internal native methods.

Not frequently discussed unless JNI comes up.

---

# Visual Memory Layout

```
                    JVM

          +----------------------+
          |      Heap            |
          |                      |
          | Student              |
          | Employee             |
          | String Objects       |
          +----------------------+

          +----------------------+
          |     Metaspace        |
          |                      |
          | Student.class        |
          | Employee.class       |
          | Method Metadata      |
          +----------------------+

Thread 1

+----------------------+
| PC Register          |
+----------------------+

+----------------------+
| Java Stack           |
| main()               |
| login()              |
| validate()           |
+----------------------+

+----------------------+
| Native Stack         |
+----------------------+

Thread 2

+----------------------+
| PC Register          |
+----------------------+

+----------------------+
| Java Stack           |
+----------------------+

+----------------------+
| Native Stack         |
+----------------------+
```

---

# Example

```java
public class Main {

    public static void main(String[] args) {

        Student student = new Student();

        student.print();

    }

}
```

Memory at a high level:

```
                 Heap

        +----------------+

        Student Object

        +----------------+

               ▲

               │

          Reference

               │

Thread Stack

main()

student
```

Notice

The variable

```java
student
```

is **not the object**.

It is merely a **reference** stored in the current stack frame, pointing to the object in the Heap.

---

# Interview Questions

## Q1. Which JVM memory areas are shared?

Answer

* Heap
* Metaspace

---

## Q2. Which JVM memory areas are thread-local?

Answer

* Java Stack
* PC Register
* Native Method Stack

---

## Q3. Where are objects stored?

Heap.

---

## Q4. Where are local variables stored?

Java Stack (inside stack frames).

---

## Q5. Where is class metadata stored?

Metaspace.

---

## Q6. Where is the current instruction stored?

PC Register.

---

# Interview Tip

One of the easiest ways to impress an interviewer is to describe the JVM memory model in terms of **shared** versus **thread-local** memory:

```
Shared

Heap
Metaspace

----------------------

Per Thread

Stack
PC Register
Native Stack
```

Then explain *why* the JVM makes that distinction:

* Shared memory holds data that multiple threads may access, such as objects and loaded classes.
* Thread-local memory holds execution state (method calls, local variables, current instruction), preventing interference between threads and simplifying execution.

That shows you understand not just **what** the memory areas are, but **why** the JVM is designed that way.

---

## Next Chapter

Now we'll deep dive into the **Java Stack**, because understanding **stack frames** is essential before we can properly explain:

* Object creation
* Method calls
* Parameter passing
* Recursion
* `StackOverflowError`
* Local variables
* Operand Stack
* Return values

The **Java Stack** is one of the most frequently explored runtime memory areas in senior Java interviews, so it's the logical next step before moving on to the Heap.


will be learning in this order - 

Yes, that is actually a much better structure for interview preparation. It'll be easier to build the mental model instead of jumping between topics.

I'd organize the **Runtime Data Areas** chapter like this.

# Chapter 8 - Runtime Data Areas ⭐⭐⭐⭐⭐

## What are Runtime Data Areas?

When the JVM starts executing your program, it creates several memory areas. Each memory area has a specific responsibility.

The JVM Specification defines **5 Runtime Data Areas**.

```text
                    JVM
                     │
                     ▼
            Runtime Data Areas
                     │
      ┌──────────────┼──────────────┐
      │              │              │
      ▼              ▼              ▼
    Shared        Thread 1       Thread 2
    Memory         Memory         Memory
```

---

## Runtime Data Areas

```text
Runtime Data Areas
│
├── 1. Heap ⭐⭐⭐⭐⭐
│      ├── What is Heap?
│      ├── Why Heap?
│      ├── What is stored?
│      ├── Shared Memory
│      ├── Young Generation
│      ├── Old Generation
│      ├── Heap Allocation
│      ├── OutOfMemoryError
│      └── Interview Questions
│
├── 2. Java Stack ⭐⭐⭐⭐⭐
│      ├── What is Stack?
│      ├── Why Stack?
│      ├── One Stack per Thread
│      ├── Stack Frames
│      ├── Local Variable Table
│      ├── Operand Stack
│      ├── Frame Data
│      ├── Method Calls
│      ├── StackOverflowError
│      └── Interview Questions
│
├── 3. Metaspace ⭐⭐⭐⭐
│      ├── What is Metaspace?
│      ├── Why do we need it?
│      ├── Class Metadata
│      ├── Runtime Constant Pool
│      ├── Static Members
│      ├── Class Loading Relationship
│      ├── PermGen vs Metaspace
│      ├── OutOfMemoryError: Metaspace
│      └── Interview Questions
│
├── 4. Program Counter (PC) Register ⭐⭐⭐
│      ├── What is PC Register?
│      ├── Why every thread has one?
│      ├── Context Switching
│      ├── Native Methods
│      └── Interview Questions
│
└── 5. Native Method Stack ⭐⭐⭐
       ├── What is Native Stack?
       ├── JNI Relationship
       ├── Native Methods
       ├── C/C++ Execution
       └── Interview Questions
```

---

# Topics Closely Related to Runtime Data Areas

These aren't runtime data areas themselves, but they're built on top of them, so we'll study them immediately afterwards.

```text
Runtime Memory Concepts
│
├── 6. Object Creation ⭐⭐⭐⭐⭐
│      ├── new Keyword
│      ├── Memory Allocation
│      ├── Object Header
│      ├── Field Initialisation
│      ├── Constructor Invocation
│      ├── Reference Assignment
│      └── Complete Object Creation Flow
│
├── 7. Object Layout ⭐⭐⭐⭐
│      ├── Object Header
│      ├── Mark Word
│      ├── Klass Pointer
│      ├── Instance Fields
│      ├── Padding
│      └── Memory Alignment
│
└── 8. Memory Allocation ⭐⭐⭐⭐⭐
       ├── TLAB
       ├── Eden Allocation
       ├── Large Objects
       ├── Allocation Failure
       ├── Escape Analysis
       └── Stack Allocation
```

---

## Learning Order

This is the order I'd recommend because every topic naturally depends on the previous one.

```text
Runtime Data Areas
        │
        ▼
1. Heap
        │
        ▼
2. Java Stack
        │
        ▼
3. Metaspace
        │
        ▼
4. PC Register
        │
        ▼
5. Native Method Stack
        │
        ▼
6. Object Creation
        │
        ▼
7. Object Layout
        │
        ▼
8. Memory Allocation
```

I made one deliberate change from our previous discussion: **Heap comes before Java Stack**.

**Why?**

Because when an interviewer asks:

> **What happens when you execute `new Student()`?**

the explanation starts with the **Heap** (where the object is allocated), and then moves to the **Stack** (where the reference is stored). This order also makes the later chapters on Object Creation and Garbage Collection flow much more naturally.

I think this is the cleanest interview-oriented structure, and once we finish these eight topics, we'll move to the **Execution Engine**, which explains how the JVM actually executes the bytecode stored in these runtime memory areas.
