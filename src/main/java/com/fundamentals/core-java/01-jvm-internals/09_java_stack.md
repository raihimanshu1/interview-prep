Perfect. Now we move to the **second Runtime Data Area**.

This is another **⭐⭐⭐⭐⭐ Must Know** topic.

One of the most common interview questions is:

> **"Explain the Java Stack."**

Let's build it from first principles.

---

# Chapter 8.2 - Java Stack ⭐⭐⭐⭐⭐

---

# What is the Java Stack?

**Definition (Interview)**

The **Java Stack** is a **thread-private runtime memory area** that stores the execution state of method calls.

It contains **stack frames**, and **each method invocation creates a new stack frame**.

> **One Stack per Thread. One Stack Frame per Method Call.**

If you remember only one sentence, remember this one.

---

# Why do we need a Stack?

Suppose you write:

```java
public static void main(String[] args) {

    int x = 10;

    calculate();
}

static void calculate() {

    int y = 20;

}
```

Question:

Where should the JVM store:

* Method parameters?
* Local variables?
* Return address?
* Temporary calculation results?

Should they go into the Heap?

No.

Why?

Because these values exist **only while the method is executing**.

Once the method finishes, they should disappear automatically.

So the JVM uses a separate memory area dedicated to **method execution**.

That memory area is the **Java Stack**.

---

# One Stack Per Thread

Every thread gets its own Java Stack.

```text
                    JVM

           Shared Memory
      +----------------------+
      |        Heap          |
      +----------------------+
      |     Metaspace        |
      +----------------------+

----------------------------------------

Thread-1

+----------------------+
|     Java Stack       |
+----------------------+

----------------------------------------

Thread-2

+----------------------+
|     Java Stack       |
+----------------------+

----------------------------------------

Thread-3

+----------------------+
|     Java Stack       |
+----------------------+
```

Unlike the Heap,

the Stack is **never shared**.

---

# Why is the Stack Thread-Private?

Imagine two threads execute the same method.

```java
calculateSalary();
```

Thread A

```java
salary = 1000;
```

Thread B

```java
salary = 5000;
```

If both threads shared one stack:

```text
salary = ??
```

There would be no isolation between method executions.

Instead,

each thread has its own execution context.

```text
Thread A                    Thread B

Java Stack                  Java Stack

salary = 1000               salary = 5000
```

This is one reason **local variables are naturally thread-safe**—each thread has its own copy.

> **Note:** If a local variable refers to a shared object in the Heap, the object itself is **not** thread-safe automatically. Only the local reference is private.

---

# Stack Grows and Shrinks Automatically

Suppose

```java
public static void main(String[] args) {

    login();

}

static void login() {

    validate();

}

static void validate() {

}
```

Execution begins.

Initially

```text
Java Stack

Empty
```

---

`main()` starts.

```text
+----------------+
| main()         |
+----------------+
```

---

`main()` calls `login()`.

```text
+----------------+
| login()        |
+----------------+
| main()         |
+----------------+
```

---

`login()` calls `validate()`.

```text
+----------------+
| validate()     |
+----------------+
| login()        |
+----------------+
| main()         |
+----------------+
```

---

`validate()` returns.

```text
+----------------+
| login()        |
+----------------+
| main()         |
+----------------+
```

---

`login()` returns.

```text
+----------------+
| main()         |
+----------------+
```

---

`main()` returns.

```text
Empty Stack
```

Notice the order.

The **last method called** is the **first method to return**.

This follows the **LIFO (Last In, First Out)** principle.

---

# What is a Stack Frame?

The Stack does **not** store methods directly.

It stores **Stack Frames**.

Each method invocation creates **one Stack Frame**.

```text
Java Stack

+---------------------------+
| Stack Frame               |
| validate()                |
+---------------------------+

+---------------------------+
| Stack Frame               |
| login()                   |
+---------------------------+

+---------------------------+
| Stack Frame               |
| main()                    |
+---------------------------+
```

A Stack Frame represents the complete execution context of a method.

When the method finishes, its frame is removed from the stack.

---

# What Does a Stack Frame Contain?

A Stack Frame has three important parts.

```text
+--------------------------------------+
|          Stack Frame                 |
+--------------------------------------+
| Local Variable Table                 |
+--------------------------------------+
| Operand Stack                        |
+--------------------------------------+
| Frame Data                           |
| (Return Address, Metadata, etc.)     |
+--------------------------------------+
```

We'll study each one.

---

# 1. Local Variable Table

Stores:

* Method parameters
* Local primitive variables
* Object references

Example

```java
void calculate(int a, int b) {

    int sum = a + b;

    Student student = new Student();

}
```

Conceptually

```text
Local Variable Table

Slot 0 → a

Slot 1 → b

Slot 2 → sum

Slot 3 → student (reference)
```

Important:

The **Student object is not here**.

Only its **reference** is stored.

The actual object lives in the Heap.

---

# 2. Operand Stack

The JVM is a **stack-based virtual machine**.

It performs calculations using the Operand Stack.

Example

```java
int c = a + b;
```

Conceptually

```text
Push a

↓

Push b

↓

Add

↓

Store Result
```

Visualization

```text
Operand Stack

Push 10

+------+
| 10   |
+------+

Push 20

+------+
| 20   |
+------+
| 10   |
+------+

iadd

+------+
| 30   |
+------+

istore

Empty
```

This is why many JVM bytecode instructions are named:

* `iload`
* `istore`
* `iadd`
* `isub`
* `imul`

They operate on the Operand Stack.

---

# 3. Frame Data

Frame Data stores information the JVM needs to continue execution.

Examples include:

* Return address
* Reference to the runtime constant pool
* Exception handling information

You don't need to memorise every field for interviews.

Just remember:

> **Frame Data allows the JVM to return to the calling method and continue execution correctly.**

---

# Stack vs Heap

| Java Stack                  | Heap                         |
| --------------------------- | ---------------------------- |
| One per thread              | One per JVM                  |
| Stores Stack Frames         | Stores Objects               |
| Local variables             | Instance data                |
| Object references           | Actual objects               |
| Automatically grows/shrinks | Managed by Garbage Collector |
| LIFO                        | No ordering                  |
| `StackOverflowError`        | `OutOfMemoryError`           |

---

# StackOverflowError

Suppose

```java
void recurse() {
    recurse();
}
```

Each recursive call creates another Stack Frame.

```text
+----------------+
| recurse()      |
+----------------+
| recurse()      |
+----------------+
| recurse()      |
+----------------+
| recurse()      |
+----------------+
        ...
```

Eventually,

the thread's stack is exhausted.

The JVM throws:

```text
java.lang.StackOverflowError
```

This is completely different from:

```text
java.lang.OutOfMemoryError: Java heap space
```

Interviewers often ask you to explain the difference.

---

# Interview Questions

### Q1. What is the Java Stack?

The Java Stack is a **thread-private runtime memory area** that stores the execution state of methods as **Stack Frames**.

---

### Q2. What is stored in the Java Stack?

* Stack Frames
* Local variables
* Method parameters
* Object references
* Operand Stack
* Frame metadata

---

### Q3. Does the Stack store objects?

No.

The Stack stores **references** to objects.

The actual objects are stored in the Heap.

---

### Q4. Why is the Stack thread-private?

Each thread requires an independent execution context. Sharing a stack would cause method calls, local variables, and return addresses from different threads to interfere with one another.

---

### Q5. Why is Stack allocation fast?

Because the Stack follows a strict **LIFO** structure. Creating a new frame simply moves the stack pointer, and removing a frame moves it back. No garbage collection is needed.

---

# Interview Boundary

For a **7+ years Backend Java interview**, this is enough.

You should confidently explain:

* What the Java Stack is
* Why it exists
* One Stack per Thread
* Stack Frames
* Local Variable Table
* Operand Stack
* Frame Data (high level)
* Method Call Flow
* `StackOverflowError`
* Stack vs Heap

**Deep Dive (Optional):**

* JVM slot allocation
* Long/double occupying two slots (historically)
* Frame implementation inside HotSpot
* Stack frame layout in machine memory

These are useful for JVM internals but are rarely expected in a typical backend interview.

---

## Next Topic

The next Runtime Data Area is **Metaspace ⭐⭐⭐⭐**, where we'll answer:

* What is Metaspace?
* Why did Java replace PermGen?
* What is stored in Metaspace?
* How does it relate to Class Loading?
* What is `OutOfMemoryError: Metaspace`?

This naturally connects to everything we've already learned about class loading and object creation.
