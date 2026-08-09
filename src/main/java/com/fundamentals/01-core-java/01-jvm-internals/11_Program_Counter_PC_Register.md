Perfect. Now we move to the **fourth Runtime Data Area**.

This is a **small topic**. For a 7+ years interview, you don't need to spend hours on it. Just understand **what it is, why it exists, and how it helps the JVM execute instructions**.

---

# Chapter 8.4 - Program Counter (PC) Register ⭐⭐⭐

---

# What is the Program Counter (PC) Register?

**Definition (Interview)**

The **Program Counter (PC) Register** is a **thread-private runtime memory area** that stores the **address (or index) of the next JVM bytecode instruction to execute**.

Think of it as:

> **The JVM's bookmark for each thread.**

It tells the JVM:

> **"After executing the current instruction, where should I continue?"**

---

# Why do we need a PC Register?

Suppose the JVM is executing:

```java
public static void main(String[] args) {

    int a = 10;

    int b = 20;

    int c = a + b;

    System.out.println(c);

}
```

After compilation, the bytecode is conceptually like:

```text
0: bipush 10
2: istore_1

3: bipush 20
5: istore_2

6: iload_1
7: iload_2
8: iadd
9: istore_3

10: getstatic
13: invokevirtual
16: return
```

Question:

After executing instruction **6**, how does the JVM know the next instruction is **7**?

The answer is:

**PC Register.**

---

# Visualisation

Suppose the JVM is here.

```text
Bytecode

0  bipush 10

2  istore_1

3  bipush 20

5  istore_2

6  iload_1   <── Currently Executing

7  iload_2

8  iadd

9  istore_3

10 return
```

The PC Register stores

```text
PC Register = 7
```

meaning

> "Execute instruction number 7 next."

After instruction 7 executes,

```text
PC Register = 8
```

---

# Why is the PC Register Thread-Private?

Imagine two threads.

```java
Thread A

calculateSalary();

Thread B

sendEmail();
```

Thread A

```text
Instruction 15
```

Thread B

```text
Instruction 240
```

If they shared one PC Register,

```text
Current Instruction = ?
```

Impossible.

Each thread executes different bytecode independently.

Therefore,

every thread has its own PC Register.

```text
             JVM

Thread-1

PC = 15

Java Stack

--------------------

Thread-2

PC = 240

Java Stack
```

---

# PC Register During Context Switching

This is the **main reason** the PC Register exists.

Suppose:

Thread A is running.

```text
Instruction

10

11

12

13
```

The operating system pauses Thread A after instruction 12.

The JVM saves

```text
PC = 13
```

Now Thread B executes.

Later,

Thread A resumes.

How does it continue from exactly where it stopped?

The JVM reads

```text
PC = 13
```

and continues execution.

Without the PC Register,

every context switch would lose the execution position.

---

# Native Methods

Suppose Java calls native C/C++ code.

```java
System.arraycopy(...)
```

During native method execution,

the JVM is no longer executing Java bytecode.

Therefore,

the PC Register **does not point to a JVM bytecode instruction**.

A common interview answer is:

> **When executing a native method, the PC Register is undefined (or not applicable for Java bytecode execution).**

---

# Relationship with Other Runtime Areas

```text
                 Thread

        +----------------------+
        | PC Register          |
        | Next Instruction     |
        +----------------------+

        +----------------------+
        | Java Stack           |
        | Method Execution     |
        +----------------------+

                 │

                 ▼

        +----------------------+
        | Heap                 |
        | Objects              |
        +----------------------+
```

Each thread has:

* One PC Register
* One Java Stack

Both are thread-private.

---

# Interview Questions

### Q1. What is the PC Register?

The PC Register is a **thread-private memory area** that stores the address (or index) of the next JVM bytecode instruction to execute.

---

### Q2. Why does every thread have its own PC Register?

Because each thread executes its own sequence of bytecode instructions independently. Sharing a single PC Register would prevent threads from resuming execution correctly after context switches.

---

### Q3. What happens to the PC Register during context switching?

The JVM preserves the current instruction position in the thread's PC Register. When the thread resumes, execution continues from that saved position.

---

### Q4. What happens to the PC Register when executing native methods?

Since native methods execute outside the JVM's bytecode interpreter, the PC Register does not hold a Java bytecode instruction during that time (it is considered undefined or not applicable for Java bytecode execution).

---

# Summary

```text
                 JVM

           Thread-1
     +------------------+
     | PC Register      |
     | Next Instruction |
     +------------------+
     | Java Stack       |
     +------------------+

           Thread-2
     +------------------+
     | PC Register      |
     | Next Instruction |
     +------------------+
     | Java Stack       |
     +------------------+

          Shared

     +------------------+
     | Heap             |
     +------------------+
```

---

# Interview Boundary

For a **7+ years Backend Java interview**, this is enough.

You should know:

* What the PC Register is.
* Why it exists.
* One PC Register per thread.
* Its role in bytecode execution.
* Its importance during thread context switching.
* Why it's undefined for native methods.

**Deep Dive (Optional):**

* Mapping bytecode indices to machine instructions.
* Interpreter implementation details.
* HotSpot execution loop internals.

These are JVM implementation topics and are rarely expected unless you're interviewing for JVM or runtime engineering roles.

---

## Next Topic

The final Runtime Data Area is **Native Method Stack ⭐⭐⭐**.

It's closely related to **JNI (Java Native Interface)** and typically takes about **10–15 minutes** to understand. After that, we'll have completed all five Runtime Data Areas and can move on to the **Execution Engine**, where the JVM actually executes the bytecode we've been discussing.
