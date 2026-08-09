Perfect. We'll now start with the **first Runtime Data Area**.

This is one of the most frequently asked JVM interview topics.

---

# Chapter 8.1 - Heap ⭐⭐⭐⭐⭐

---

# What is Heap?

**Definition (Interview)**

The **Heap** is a **shared runtime memory area** managed by the JVM where **all objects and arrays are allocated**. It is created when the JVM starts and is shared among all threads.

> **One Heap per JVM. Shared by all threads.**

This single sentence is worth remembering.

---

# Why do we need a Heap?

Let's understand the intuition instead of memorising definitions.

Consider this code:

```java
public class Main {

    public static void main(String[] args) {

        Student student = new Student();

        login(student);
    }

    static void login(Student student) {
        System.out.println(student);
    }
}
```

Question:

Where should the `Student` object be stored?

Option 1:

```text
main() Stack Frame

+------------------------+
| Student Object         |
+------------------------+
```

Would this work?

No.

Why?

Because `main()` calls `login()`.

Both methods need access to the same object.

Later another method may need it.

Even another thread may need it.

A stack frame belongs only to one method invocation and disappears when that method returns.

Objects often live much longer than a single method call.

Therefore, they need a separate memory area.

That memory area is the **Heap**.

---

# Real-Life Analogy

Imagine an office.

Each employee has a personal notebook.

```text
Employee A Notebook

Task 1
Task 2
Task 3
```

That's similar to the **Java Stack**.

Now imagine a shared filing cabinet.

```text
Shared Cabinet

Customer Records

Orders

Invoices

Products
```

Everyone in the office can access those records.

That's the **Heap**.

The Stack is **private**.

The Heap is **shared**.

---

# Heap is Shared Memory

One of the easiest interview questions.

```text
                    JVM

        +----------------------+
        |        Heap          |
        |                      |
        | Student              |
        | Employee             |
        | Order                |
        | Product              |
        +----------------------+

          ▲       ▲       ▲

       Thread1 Thread2 Thread3
```

Notice

There is

* One Heap
* Many Threads

All threads can access Heap objects (subject to your application's synchronisation logic).

---

# What is Stored in the Heap?

The Heap stores **objects**, not variables.

Example

```java
class Student {

    int id;

    String name;

}
```

Now

```java
Student student = new Student();
```

Memory

```text
Heap

+----------------------+
| Student Object       |
|----------------------|
| id = 0               |
| name = null          |
+----------------------+
```

The **object** lives here.

---

# Where is the Variable?

Interviewers love this.

```java
Student student = new Student();
```

Many people say

> "student is in the Heap."

Wrong.

The variable

```java
student
```

is stored inside the **current stack frame**.

Only the object is in the Heap.

```text
                    Heap

+-------------------------+
| Student Object          |
+-------------------------+
           ▲
           │
           │ reference
           │

Java Stack

+------------------------+
| main() Frame           |
| student -------------->|
+------------------------+
```

This distinction is extremely important.

---

# Multiple References

```java
Student s1 = new Student();

Student s2 = s1;
```

Memory

```text
Heap

+----------------------+
| Student Object       |
+----------------------+
      ▲
      │
 +----+----+
 │         │
s1        s2
```

There is only **one object**.

Two references point to it.

Therefore

```java
s1.name = "John";
```

```java
System.out.println(s2.name);
```

Output

```text
John
```

---

# Arrays also Live in the Heap

Interview Question:

Where is this allocated?

```java
int[] numbers = new int[100];
```

Answer

```text
Heap

+------------------------+
| int[100]               |
+------------------------+
```

The variable

```java
numbers
```

is a reference stored in the Stack.

---

# Every Object Lives in the Heap?

For normal Java programming, **yes**.

```java
new Student()

new Employee()

new ArrayList()

new HashMap()

new int[100]
```

All are allocated in the Heap.

> **Advanced Note:** Modern JVMs can optimise some short-lived objects using **Escape Analysis** and allocate them on the stack or eliminate them entirely. We'll cover this later in the Execution Engine chapter. For interview purposes, unless discussing JVM optimisations, assume objects are allocated in the Heap.

---

# Object Lifetime

Suppose

```java
public void test() {

    Student s = new Student();

}
```

When the method returns,

the stack frame disappears.

```text
Stack

+----------------------+
| test()               |
| s ------------------>|

↓

Method Returns

↓

Frame Removed
```

The object is still in the Heap.

```text
Heap

+----------------------+
| Student Object       |
+----------------------+
```

But now

no references point to it.

Therefore

```text
Eligible for Garbage Collection
```

Notice

The object is **not immediately deleted**.

It simply becomes eligible.

The Garbage Collector decides when to reclaim the memory.

---

# Heap is Garbage Collected

Unlike the Stack,

the Heap is **not cleaned automatically** after a method returns.

Instead

```text
Object Created

↓

Object Used

↓

Reference Lost

↓

Eligible for GC

↓

Garbage Collector Runs

↓

Memory Reclaimed
```

This is why Java developers don't manually free memory.

---

# OutOfMemoryError

Suppose

```java
List<Student> students = new ArrayList<>();

while (true) {
    students.add(new Student());
}
```

Every object remains reachable because the list keeps references to them.

Eventually

```text
Heap Full

↓

GC Runs

↓

Still No Free Memory

↓

java.lang.OutOfMemoryError:
Java heap space
```

---

# Heap vs Stack

| Heap                         | Stack                                 |
| ---------------------------- | ------------------------------------- |
| Shared by all threads        | One per thread                        |
| Stores objects and arrays    | Stores stack frames                   |
| Stores instance data         | Stores local variables and references |
| Managed by Garbage Collector | Automatically managed (LIFO)          |
| Can throw `OutOfMemoryError` | Can throw `StackOverflowError`        |

---

# Common Interview Questions

### Q1. What is the Heap?

The Heap is a **shared runtime memory area** where the JVM allocates objects and arrays. It is managed by the Garbage Collector.

---

### Q2. Is there one Heap per thread?

No.

There is **one Heap per JVM**, shared by all threads.

---

### Q3. What is stored in the Heap?

* Objects
* Arrays
* Instance fields (within objects)

---

### Q4. Where are object references stored?

It depends:

* **Local variables** (e.g. `Student s`) are stored in the current stack frame.
* **Instance fields** that reference other objects are stored as part of the object in the Heap.

Example:

```java
class Employee {
    Address address;
}
```

The `Employee` object is on the Heap, and its `address` reference field is also stored inside that Heap object.

---

### Q5. When is an object removed from the Heap?

Not immediately when it becomes unreachable.

It first becomes **eligible for Garbage Collection**, and the JVM reclaims its memory when the Garbage Collector runs.

---

# Summary

```text
                    JVM

           Shared Memory
      +----------------------+
      |       Heap           |
      |----------------------|
      | Objects              |
      | Arrays               |
      | Instance Fields      |
      +----------------------+

               ▲
               │ References

Thread Stack

+----------------------+
| Local Variables      |
| Method Parameters    |
| Object References    |
+----------------------+
```

---

## Next Topic: Heap Internals

We've answered **what the Heap is**.

The next question an interviewer typically asks is:

> **"How is the Heap organised internally?"**

That leads us to:

* Young Generation
* Old Generation
* Eden Space
* Survivor Spaces (S0 & S1)
* Why the Heap is split into generations
* How this design improves Garbage Collection

This is the foundation for understanding **Minor GC, Major GC, and G1 GC**, so it naturally comes before the Garbage Collection chapter.


Excellent. Now we move to the **internal structure of the Heap**.

This is where interviews become interesting. Almost every interviewer asks:

> **"Why is the Heap divided into Young Generation and Old Generation?"**

Most people answer:

> "Because of Garbage Collection."

That is true, but it doesn't explain **why**.

Let's understand it from first principles.

---

# Chapter 8.2 - Heap Internals (Young & Old Generation) ⭐⭐⭐⭐⭐

---

# First Question

Suppose your application creates objects like this:

```java
public void processRequest() {

    String token = generateToken();

    User user = new User();

    Order order = new Order();

}
```

Question:

How long will these objects live?

Probably only a few milliseconds.

After the request finishes,

they become unreachable.

---

Now another object.

```java
Cache cache = new Cache();
```

This object may live for

* hours
* days
* until the application shuts down

Question:

Should the JVM treat both kinds of objects the same?

```text
Temporary Objects

User

Order

Token

----------------------

Long-lived Objects

Cache

Configuration

Singletons
```

No.

Most Java objects die very quickly.

This observation is known as the **Weak Generational Hypothesis**.

---

# Weak Generational Hypothesis ⭐⭐⭐⭐⭐

This is one of the most important JVM concepts.

It states:

> **Most objects die young.**

Examples:

```java
StringBuilder sb = new StringBuilder();

Integer sum = calculate();

UserDTO dto = mapper.map(entity);
```

These objects usually exist only during one method call or one request.

The JVM is optimised around this behaviour.

---

# Why not keep everything in one Heap?

Imagine this.

```text
Heap

Object

Object

Object

Object

Object

Object

Object

Object

Object

Object
```

Suppose the Garbage Collector runs.

How many objects must it inspect?

Every object.

Even those that have lived for years.

This is inefficient.

---

# Solution

Split the Heap into generations.

```text
                     Heap

        +--------------------------+
        | Young Generation         |
        +--------------------------+
        | Old Generation           |
        +--------------------------+
```

Now,

the Garbage Collector can focus on the area where most objects die.

---

# Heap Structure

Conceptually, the Heap looks like this.

```text
                     Heap
+--------------------------------------------------+
|                                                  |
|              Young Generation                    |
|                                                  |
|  +---------+----------+----------+               |
|  |  Eden   | Survivor | Survivor |               |
|  |         |   S0     |    S1    |               |
|  +---------+----------+----------+               |
|                                                  |
|--------------------------------------------------|
|                                                  |
|              Old Generation                      |
|                                                  |
|        Long-Lived Objects                        |
|                                                  |
+--------------------------------------------------+
```

Interview tip:

Don't worry about exact sizes.

Understand the purpose of each region.

---

# Young Generation

This is where **every new object starts**.

Example:

```java
Student s = new Student();

Order o = new Order();

ArrayList<String> list = new ArrayList<>();
```

Initially

```text
Young Generation

↓

Eden Space
```

Every newly created object is allocated in **Eden**.

---

# Why Eden?

Think of Eden as a nursery.

Every newborn object starts here.

```text
New Object

↓

Eden
```

Most of them never grow up.

They die quickly.

---

# Survivor Spaces

Question:

Suppose an object survives one Garbage Collection.

Should it immediately move to Old Generation?

No.

Maybe it still won't live very long.

So the JVM gives it another chance.

```text
Young Generation

+---------+----------+----------+

| Eden    | S0       | S1       |

+---------+----------+----------+
```

S0 and S1 stand for **Survivor Spaces**.

Only one survivor space is active at a time for copying during a minor GC; after each collection, their roles swap.

---

# Typical Flow

Suppose we create three objects.

```java
Student s1 = new Student();

Student s2 = new Student();

Student s3 = new Student();
```

Initially

```text
Eden

s1

s2

s3
```

Now Minor GC happens.

Suppose

```text
s1

Dead

s2

Alive

s3

Dead
```

Only surviving objects move.

```text
Survivor Space

s2
```

The dead ones disappear.

---

# Object Age

Every surviving object has an **age**.

```text
Minor GC

↓

Age = 1

-----------------

Minor GC

↓

Age = 2

-----------------

Minor GC

↓

Age = 3
```

Each time the object survives a Minor GC, its age increases.

Once it reaches a configurable threshold (the default is often around **15**, though the JVM can adjust this dynamically), it is promoted to the Old Generation.

---

# Old Generation

Objects that live for a long time end up here.

Examples:

```java
Singleton Objects

Application Cache

Configuration Objects

Connection Pools
```

Diagram:

```text
Old Generation

+----------------------+

Cache

Configuration

Singleton

Long-Lived Objects

+----------------------+
```

These objects are expected to survive many Garbage Collection cycles.

---

# Complete Journey

```text
New Object

↓

Eden

↓

Minor GC

↓

Survivor S0

↓

Minor GC

↓

Survivor S1

↓

Minor GC

↓

Survivor S0

↓

Age Threshold Reached

↓

Old Generation
```

Notice:

Objects don't usually go directly from Eden to Old Generation.

They "grow up" through the Survivor Spaces first.

---

# Why Two Survivor Spaces?

Question:

Why not just one?

During a Minor GC, the JVM copies live objects from the current "from" space into the empty "to" space while increasing their age. When the GC finishes, the two Survivor spaces swap roles.

```text
Before Minor GC

Eden

↓

S0 (From)

↓

S1 (Empty)

------------------------

After Minor GC

Eden (Cleared)

↓

S0 (Empty)

↓

S1 (Now Contains Survivors)
```

On the next Minor GC, the direction reverses.

This copying approach keeps memory compact and avoids fragmentation within the Young Generation.

---

# Interview Questions

### Q1. Why is the Heap divided into Young and Old Generation?

Because **most objects die young**. By separating short-lived and long-lived objects, the JVM can reclaim memory more efficiently and avoid scanning the entire Heap during every Garbage Collection.

---

### Q2. Where is every new object allocated?

In **Eden Space** (with some specialised exceptions for very large objects or JVM optimisations).

---

### Q3. What are Survivor Spaces?

Survivor Spaces temporarily hold objects that have survived one or more Minor GCs. Their age increases with each survival until they are promoted to the Old Generation.

---

### Q4. Why are there two Survivor Spaces?

To support an efficient copying collection algorithm. Live objects are copied from one Survivor space to the other during Minor GC, and the roles of the spaces swap afterwards.

---

### Q5. What kinds of objects end up in the Old Generation?

Objects that survive multiple Minor GCs, such as caches, singleton instances, configuration objects, and other long-lived application data.

---

# Summary

```text
                        Heap

+--------------------------------------------------+
|                Young Generation                  |
|                                                  |
|   Eden  --->  Survivor S0  <-->  Survivor S1     |
|      New        Surviving Objects                |
+--------------------------------------------------+
|                Old Generation                    |
|                                                  |
|        Long-Lived Objects                        |
+--------------------------------------------------+

Object Lifecycle

New Object
     │
     ▼
   Eden
     │
Minor GC
     │
     ▼
Survivor Spaces
     │
Survives Multiple GCs
     │
     ▼
Old Generation
```

---

## What's Next?

We've now covered **how the Heap is organised**.

The next logical topic is:

# **Object Creation Process ⭐⭐⭐⭐⭐**

We'll answer exactly what happens when the JVM executes:

```java
Student student = new Student();
```

Step by step:

1. Class loading check
2. Memory allocation in Eden
3. Object header creation
4. Field default initialisation
5. Constructor execution
6. Reference assignment to the stack

This is one of the highest-frequency senior Java interview questions because it ties together **Class Loading, Heap, Stack, Metaspace, and later the Execution Engine** into one complete lifecycle.


Excellent. This is one of the **highest frequency JVM interview questions**.

For 7+ years experience, almost every interviewer has asked some variation of:

* **What happens when you execute `new Student()`?**
* **Explain object creation inside JVM.**
* **How is memory allocated?**
* **What happens before the constructor executes?**

Most candidates answer:

> JVM allocates memory then calls constructor.

That's only about **20% of the complete answer**.

Let's understand the entire lifecycle.

---

# Chapter 8.3 - Object Creation Process ⭐⭐⭐⭐⭐

---

# The Code

Let's start with a simple statement.

```java
Student student = new Student();
```

Looks simple.

But internally, the JVM performs several steps before the object is ready.

---

# High-Level Flow

```text
Student student = new Student();

        │
        ▼

1. Is Student.class loaded?

        │

        ▼

2. Allocate Heap Memory

        │

        ▼

3. Initialize Object Header

        │

        ▼

4. Initialize Instance Fields
   (Default Values)

        │

        ▼

5. Execute Constructor

        │

        ▼

6. Store Reference
   in Stack Variable

        │

        ▼

Object Ready
```

This entire sequence happens for every object you create.

---

# Step 1 - Class Loading Check ⭐⭐⭐⭐⭐

Before creating an object, the JVM asks:

> **Do I know what a Student object looks like?**

If the class is not loaded,

the JVM cannot determine:

* Object size
* Number of fields
* Methods
* Constructor
* Parent class

Therefore the first step is:

```text
Student.class

Loaded?

      │

Yes ───────────────► Continue

No

↓

Load Class

↓

Verify

↓

Link

↓

Initialize

↓

Continue
```

Notice how this connects to everything we studied earlier.

---

## Why is this necessary?

Suppose

```java
class Student {

    int id;

    String name;

}
```

How much memory is needed?

The JVM cannot answer until it reads the class metadata.

That metadata is stored in **Metaspace**.

---

# Step 2 - Allocate Memory ⭐⭐⭐⭐⭐

Now the JVM knows

```text
Student Object

↓

Needs 24 bytes
```

(Example size only.)

It allocates memory in the Heap.

Usually

```text
Heap

↓

Young Generation

↓

Eden Space
```

Diagram

```text
Heap

+--------------------------------+

Eden

+-------------+

Free Memory

↓

Allocate Student

+-------------+

Student

+-------------+
```

At this moment,

the object exists,

but nothing has been initialized.

---

# Step 3 - Initialize Object Header ⭐⭐⭐⭐

Every Java object begins with an **Object Header**.

Think of it as the JVM's metadata about the object.

```text
+-------------------------+
| Object Header           |
+-------------------------+
| Instance Fields         |
+-------------------------+
```

The Object Header contains information such as:

* Lock (synchronization) information
* Identity hash code (when needed)
* Garbage Collection metadata
* Pointer to the class metadata (`Student.class`)

A simplified view:

```text
+-----------------------------+

Mark Word

Klass Pointer

------------------------------

id

name

+-----------------------------+
```

We'll study Object Layout in detail later.

For now, remember:

> **Every object starts with an Object Header.**

---

# Step 4 - Initialize Instance Fields ⭐⭐⭐⭐⭐

The JVM now assigns **default values**.

Example

```java
class Student {

    int id;

    boolean active;

    String name;

}
```

Memory becomes

```text
Student

--------------------

id = 0

active = false

name = null
```

Notice

The constructor has **not executed yet**.

These values come directly from the JVM.

---

## Interview Question

Consider

```java
class Student {

    int id = 100;

}
```

Question

Immediately after memory allocation,

what is the value?

Answer

```text
0
```

Not

```text
100
```

The assignment

```java
id = 100;
```

is Java code.

It executes during object initialization as part of the constructor process.

---

# Step 5 - Execute Constructor ⭐⭐⭐⭐⭐

Now the JVM invokes the constructor.

```java
class Student {

    int id = 100;

    Student() {

        System.out.println("Constructor");

    }

}
```

Execution

```text
Default Values

↓

id = 0

↓

Execute Field Initializer

↓

id = 100

↓

Constructor Body

↓

Print

Constructor
```

Notice something important.

The field initializer:

```java
int id = 100;
```

executes **before** the constructor body.

Conceptually, the compiler places instance field initializers at the beginning of each constructor (after the implicit or explicit call to `super()`).

---

# Step 6 - Store Reference in Stack ⭐⭐⭐⭐⭐

Finally,

the JVM stores the reference.

```java
Student student = new Student();
```

Memory

```text
                 Heap

+-------------------------+
| Student Object          |
+-------------------------+
           ▲
           │
           │ Reference
           │

Java Stack

+------------------------+
| main() Frame           |
| student -------------->|
+------------------------+
```

The object is now fully ready.

---

# Complete Lifecycle

```text
new Student()

        │
        ▼

Class Loaded?

        │

No

↓

Load Class

↓

Verify

↓

Link

↓

Initialize

↓

Allocate Memory

↓

Create Object Header

↓

Default Values

↓

Field Initializers

↓

Constructor

↓

Reference Stored

↓

Ready
```

---

# Complete Example

```java
class Student {

    int id = 100;

    String name = "John";

    Student() {

        System.out.println("Constructor");

    }

}
```

When

```java
Student s = new Student();
```

Internally

```text
Load Student.class

↓

Allocate Heap Memory

↓

Object Header

↓

id = 0

name = null

↓

id = 100

name = "John"

↓

Constructor Executes

↓

Reference Stored

↓

Object Ready
```

---

# Interview Trap

Interviewer:

> **Does the constructor allocate memory?**

Answer:

**No.**

The JVM allocates memory **before** calling the constructor.

The constructor's job is to initialise the object's state, not to allocate the object itself.

---

# Interview Trap 2

Interviewer:

> **When does `super()` execute?**

For every constructor,

the first executable statement is an implicit or explicit call to the superclass constructor.

Conceptually:

```java
Student() {

    super();

    id = 100;

    System.out.println("Constructor");

}
```

So the actual sequence is:

```text
Allocate Memory

↓

Default Values

↓

Call super()

↓

Execute Instance Field Initializers

↓

Execute Constructor Body
```

This is a common senior-level interview question.

---

# Memory Snapshot

```text
Java Stack

+----------------------+
| student ------------+|
+---------------------|-+
                      |
                      ▼

Heap

+--------------------------------------+
| Object Header                        |
|--------------------------------------|
| id = 100                             |
| name = "John"                        |
+--------------------------------------+

Metaspace

+----------------------+
| Student.class        |
| Field Metadata       |
| Method Metadata      |
+----------------------+
```

Notice how **three JVM runtime areas** work together:

* **Metaspace** → Class definition
* **Heap** → Object instance
* **Stack** → Reference

---

# Interview Questions

### Q1. What happens when `new Student()` executes?

A complete answer:

1. The JVM checks whether `Student.class` is loaded.
2. If necessary, it loads, links, and initialises the class.
3. It allocates memory for the object in the Heap (typically Eden).
4. It creates the object header.
5. It assigns default values to instance fields.
6. It invokes the superclass constructor.
7. It executes instance field initialisers.
8. It executes the constructor body.
9. It stores the object reference in the current stack frame.

---

### Q2. Does the constructor allocate memory?

No.

Memory allocation is performed by the JVM before the constructor is invoked.

---

### Q3. When are default values assigned?

Immediately after memory allocation and before any Java constructor code runs.

---

### Q4. Which JVM memory areas participate in object creation?

* **Metaspace** → Provides class metadata.
* **Heap** → Stores the new object.
* **Java Stack** → Stores the reference in the current stack frame.

---

# Summary

```text
new Student()

        │
        ▼
Class Loaded?
        │
        ▼
Allocate Heap Memory
        │
        ▼
Create Object Header
        │
        ▼
Default Field Values
        │
        ▼
super()
        │
        ▼
Instance Field Initialisers
        │
        ▼
Constructor Body
        │
        ▼
Store Reference in Stack
        │
        ▼
Object Ready
```

---

## Next Topic

The next chapter is **Object Layout (Object Header, Mark Word, Klass Pointer, Alignment, Padding)**.

This dives into **how an object is physically laid out in memory**, which is useful for understanding synchronization, identity hash codes, compressed class pointers, and JVM memory efficiency. It's asked less often than object creation, but it's a valuable senior-level topic and helps explain later concepts like locking and garbage collection.
