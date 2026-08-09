Excellent. We have now reached the **heart of the JVM**.

Until now, we've learned:

```text
.java Source Code
        │
        ▼
javac Compiler
        │
        ▼
.class (Bytecode)
        │
        ▼
Class Loader
        │
        ▼
Runtime Data Areas
```

Now comes the obvious interview question:

> **"We have bytecode in memory... who actually executes it?"**

The answer is:

> **The Execution Engine.**

This is one of the most frequently asked JVM topics for 7+ years interviews.

---

# Chapter 9 - Execution Engine ⭐⭐⭐⭐⭐

Before diving into Interpreter, JIT, C1, and C2, let's first understand **what the Execution Engine actually is**.

---

# What is the Execution Engine?

**Definition (Interview)**

The **Execution Engine** is the component of the JVM responsible for **executing Java bytecode**.

Think of it as the **CPU of the JVM**.

Just as a CPU executes machine instructions,

the Execution Engine executes **JVM bytecode instructions**.

---

# Where does it fit?

Let's revisit the entire JVM flow.

```text
                Developer

Main.java

        │

        ▼

javac

        │

        ▼

Main.class (Bytecode)

        │

        ▼

Class Loader

        │

        ▼

Runtime Data Areas

Heap

Stack

Metaspace

        │

        ▼

=============================
      Execution Engine
=============================

Interpreter

JIT Compiler

Garbage Collector

        │

        ▼

CPU executes Machine Code
```

Notice something.

The CPU **cannot execute bytecode**.

It only understands **machine code**.

So someone has to translate bytecode into machine instructions.

That's the job of the Execution Engine.

---

# First Important Question

Suppose our bytecode is

```text
iload_1

iload_2

iadd

istore_3
```

Can your Intel CPU execute this?

No.

Intel understands instructions like

```text
MOV

ADD

SUB

JMP
```

ARM CPUs understand different instructions.

Bytecode is neither Intel nor ARM.

It is **platform-independent**.

Therefore,

someone must convert bytecode into native machine code.

---

# Responsibility of Execution Engine

The Execution Engine performs several responsibilities.

```text
Execution Engine

│

├── Execute Bytecode

├── Convert Bytecode to Machine Code

├── Optimize Frequently Executed Code

├── Manage Runtime Optimizations

└── Work with Garbage Collector
```

---

# High-Level Architecture

```text
                    JVM

           +----------------------+
           |  Execution Engine    |
           +----------------------+

          /            |             \

         /             |              \

 Interpreter      JIT Compiler     Garbage
                                    Collector
```

Each component has a different responsibility.

We'll study them one by one.

---

# Execution Flow

Suppose we execute

```java
public static void main(String[] args) {

    int sum = add(10,20);

}
```

The flow becomes

```text
Java Source

↓

javac

↓

Bytecode

↓

Class Loader

↓

Execution Engine

↓

Machine Code

↓

CPU
```

Notice

Everything before the Execution Engine prepares the program.

The Execution Engine actually **runs** it.

---

# Why not compile directly to machine code?

This is a favourite interview question.

Suppose Java generated Intel machine code.

```text
Windows

↓

Intel Machine Code
```

Would it run on

* Linux ARM?
* macOS Apple Silicon?
* IBM Power?

No.

Java instead produces

```text
Platform Independent Bytecode
```

Every platform has its own JVM.

```text
Windows JVM

↓

Intel Machine Code

--------------------

Linux JVM

↓

ARM Machine Code

--------------------

macOS JVM

↓

Apple Silicon Machine Code
```

That's why Java follows the famous principle:

> **Write Once, Run Anywhere (WORA).**

---

# Components of Execution Engine

Now we'll zoom in.

```text
Execution Engine

│

├── Interpreter ⭐⭐⭐⭐⭐

├── JIT Compiler ⭐⭐⭐⭐⭐

│     ├── Hot Methods

│     ├── C1

│     ├── C2

│     └── Code Cache

│

├── Runtime Optimizations ⭐⭐⭐⭐

│     ├── Method Inlining

│     ├── Escape Analysis

│     ├── Dead Code Elimination

│     └── Loop Optimizations

│

└── Garbage Collector ⭐⭐⭐⭐⭐
```

This is the roadmap we'll follow.

---

# Common Misconception

Many developers think:

```text
Execution Engine

=

JIT Compiler
```

Wrong.

The JIT Compiler is only **one part** of the Execution Engine.

The Execution Engine includes:

* Interpreter
* JIT Compiler
* Runtime Optimisations
* Coordination with the Garbage Collector during execution

---

# Interview Questions

### Q1. What is the Execution Engine?

The Execution Engine is the JVM component responsible for executing Java bytecode by interpreting it or compiling it into native machine code.

---

### Q2. Why do we need an Execution Engine?

Because CPUs cannot execute Java bytecode directly.

The Execution Engine translates bytecode into native machine instructions and executes it efficiently.

---

### Q3. What are the main components of the Execution Engine?

* Interpreter
* JIT Compiler
* Runtime Optimisation mechanisms
* Garbage Collector (working alongside execution)

---

### Q4. Does the Execution Engine always use the JIT Compiler?

No.

Execution **starts with the Interpreter**.

The JIT Compiler compiles only **frequently executed ("hot") code** into native machine code.

This is one of the most important interview concepts.

---

# Interview Boundary

For a senior backend interview, you should know:

* Why the Execution Engine exists.
* Why CPUs cannot execute bytecode directly.
* Where it fits in the JVM architecture.
* Its major components.
* The difference between the Execution Engine and the JIT Compiler.

---

## Next Topic (⭐⭐⭐⭐⭐ Most Important)

Now we'll study the **Interpreter**.

This is where the famous interview question begins:

> **"If Java has a JIT Compiler, why does it need an Interpreter?"**

Understanding that answer naturally leads into **Hot Methods**, **JIT Compilation**, **C1/C2**, and all the JVM performance optimisations. This is one of the highest-value areas in JVM interviews.

Excellent. We now enter **the most important JVM topic**.

If you understand the next four topics, you'll be able to answer almost every JVM performance interview question.

The next few chapters are tightly connected:

```text
Execution Engine
│
├── Interpreter ⭐⭐⭐⭐⭐   ← Today
├── Hot Methods ⭐⭐⭐⭐⭐
├── JIT Compiler ⭐⭐⭐⭐⭐
├── C1 & C2 Compiler ⭐⭐⭐⭐
├── Code Cache ⭐⭐⭐
└── Runtime Optimisations ⭐⭐⭐⭐
```

Don't try to memorize them individually. Think of them as one continuous story.

---

# Chapter 9.1 - Interpreter ⭐⭐⭐⭐⭐

---

# First Question

Suppose we have this Java program.

```java
public class Main {

    public static void main(String[] args) {

        int sum = add(10, 20);

        System.out.println(sum);

    }

    static int add(int a, int b) {

        return a + b;

    }
}
```

We already know

```text
.java

↓

javac

↓

.class (Bytecode)
```

Now the JVM has the bytecode.

Question:

**Who executes it?**

Answer:

The **Execution Engine**.

But here's another question.

> **Does the JVM immediately compile all bytecode into machine code?**

Most people answer:

> Yes.

**Wrong.**

---

# Why doesn't the JVM compile everything immediately?

Imagine you start an application.

```text
Application Starts

↓

10,000 methods available

↓

Only 200 methods actually execute
```

Should the JVM spend time compiling all **10,000 methods**?

No.

That would make application startup extremely slow.

Most methods might never execute.

---

## Real-world Example

Think about Microsoft Word.

It contains thousands of features.

* Mail Merge
* Macros
* Spell Checker
* Translator
* Printing
* Charts

When Word starts,

does it load everything into memory?

No.

It loads only what it immediately needs.

The JVM follows the same idea.

---

# The Solution

Instead of compiling everything,

the JVM starts executing bytecode **immediately** using the **Interpreter**.

---

# What is the Interpreter?

**Definition (Interview)**

The **Interpreter** is the component of the JVM that **reads Java bytecode instruction by instruction and executes it immediately without producing machine code**.

Notice the important words.

* Reads one instruction
* Executes it
* Reads the next instruction
* Executes it

---

# Visualisation

Suppose the bytecode is

```text
0  iload_1

1  iload_2

2  iadd

3  istore_3

4  return
```

The Interpreter executes

```text
Read iload_1

↓

Execute

↓

Read iload_2

↓

Execute

↓

Read iadd

↓

Execute

↓

Read istore_3

↓

Execute

↓

Read return

↓

Execute
```

Notice

It never creates machine code.

It simply executes one bytecode instruction after another.

---

# Think of it like a Translator

Imagine someone gives you a book in Japanese.

Option 1

Translate the **entire book** into English first.

Then read it.

This takes a long time before you can start reading.

---

Option 2

Translate **one sentence**.

Read it.

Translate the next sentence.

Read it.

This is exactly how an Interpreter works.

```text
Bytecode

↓

Interpret One Instruction

↓

Execute

↓

Interpret Next

↓

Execute
```

---

# Why is the Interpreter useful?

Because it provides **fast startup**.

Suppose the application starts.

```text
Application Starts

↓

Interpreter Begins Immediately

↓

Application Responds Quickly
```

No waiting for compilation.

---

# Example

Suppose this method executes only once.

```java
public void login() {

    System.out.println("Login");

}
```

Should the JVM spend time compiling it into machine code?

No.

The Interpreter executes it once.

Done.

No compilation overhead.

---

# But there's a Problem...

Imagine this code.

```java
for (int i = 0; i < 1_000_000; i++) {

    add(10, 20);

}
```

The bytecode for `add()` might look like this.

```text
iload_1

iload_2

iadd

ireturn
```

If the Interpreter executes this method **1 million times**...

```text
Iteration 1

Read

↓

Execute

------------------

Iteration 2

Read

↓

Execute

------------------

Iteration 3

Read

↓

Execute

------------------

...

1,000,000 times
```

It keeps decoding the **same bytecode** repeatedly.

That's inefficient.

---

# Why is it slow?

Imagine you have to read the same recipe every morning before making tea.

```text
Read Recipe

↓

Boil Water

↓

Read Recipe Again

↓

Boil Water

↓

Repeat Every Day
```

Eventually,

you'll memorise the recipe.

The JVM thinks exactly the same way.

If a method is executed frequently,

why keep interpreting it?

Why not translate it once into machine code?

That leads us to the **JIT Compiler**.

---

# Interpreter vs Machine Code

Interpreter

```text
Bytecode

↓

Read

↓

Execute

↓

Read

↓

Execute

↓

Read

↓

Execute
```

Machine Code

```text
Machine Instructions

↓

CPU Executes Directly
```

Machine code is much faster because there is no repeated bytecode decoding.

---

# Pros and Cons

### Advantages

* Very fast application startup.
* No compilation cost for methods executed only once or a few times.
* Uses less CPU during application startup.

---

### Disadvantages

* Slower execution for frequently used methods.
* Repeatedly interprets the same bytecode.
* Cannot apply advanced runtime optimisations.

---

# Why not remove the Interpreter?

Interview Question:

> If the JIT Compiler exists, why do we still need the Interpreter?

Because:

If the JVM compiled every method before execution,

* Startup would be slow.
* CPU usage during startup would increase.
* Many methods might be compiled even though they are never used.

The Interpreter allows the application to start immediately.

The JIT Compiler optimises only the code that proves to be important.

---

# Complete Flow

```text
Application Starts

↓

Bytecode Loaded

↓

Interpreter Executes Bytecode

↓

Method Execution Count Increases

↓

Frequently Executed?

       │
       ├── No
       │
       ▼
Continue Interpreting

       │
       └── Yes

             ▼

      JIT Compiler
```

Notice something very important.

The Interpreter is **not replaced** by the JIT Compiler.

It is the **first stage** of execution.

---

# Interview Questions

### Q1. What is the Interpreter?

The Interpreter executes Java bytecode **instruction by instruction** without generating native machine code.

---

### Q2. Why does the JVM start with the Interpreter?

Because it provides fast startup and avoids compiling methods that may execute only once.

---

### Q3. Why is the Interpreter slower?

Because it repeatedly reads and decodes the same bytecode every time a method executes.

---

### Q4. Why doesn't the JVM compile every method immediately?

Because many methods are rarely or never executed. Compiling all methods would waste CPU time and increase startup latency.

---

# Summary

```text
Application Starts

        │

        ▼

Bytecode

        │

        ▼

Interpreter

        │

Read One Instruction

↓

Execute

↓

Read Next

↓

Execute

↓

Read Next

↓

Execute

        │

        ▼

Fast Startup

BUT

Repeated Interpretation

↓

Performance Bottleneck
```

---

# Interview Boundary

For a **7+ years Backend Java interview**, this is exactly what you should know.

Be able to explain:

* What the Interpreter is.
* Why the JVM starts with it.
* Why it provides fast startup.
* Why it becomes inefficient for frequently executed methods.
* Why the JVM cannot rely on the Interpreter alone.

Don't worry yet about **how the JVM decides a method is "hot."**

---

## Next Topic (Most Important)

Now comes the chapter that ties everything together:

# **Hot Methods ⭐⭐⭐⭐⭐**

We'll answer the natural follow-up question:

> **"How does the JVM know a method has been executed enough times to stop interpreting it and compile it with the JIT?"**

This is where concepts like **invocation counters**, **back-edge counters**, and **compilation thresholds** come into play and lead directly into the JIT Compiler. This is one of the most commonly discussed JVM performance topics in senior Java interviews.

I actually think that's a better approach. The handbook will stay interview-focused instead of turning into a JVM implementation book.

For every chapter, let's use **three sections**:

1. **Core Interview Knowledge** (Must know)
2. **Good to Know** (A little more depth)
3. **Boundary / Deep Dive** (Stop here unless interviewer asks)

For example, for the **Interpreter** chapter, I'd end it like this.

---

# Good to Know (Optional)

### 1. Is the Interpreter written in Java?

No.

The Interpreter is part of the **JVM implementation (e.g., HotSpot)** and is primarily written in **C++**, not Java.

Reason:

* It needs direct access to CPU instructions.
* It manages low-level runtime operations.
* It must be extremely fast.

---

### 2. Does every JVM have the same Interpreter?

No.

The **JVM Specification** only defines **what** the JVM should do.

Each implementation decides **how** to implement it.

Examples:

* HotSpot JVM
* OpenJ9
* GraalVM

Each has its own implementation of the interpreter and JIT compiler while following the same JVM specification.

---

### 3. Does the Interpreter execute source code?

No.

It executes **bytecode**.

```text
.java
   │
javac
   │
.class (Bytecode)
   │
Interpreter
   │
CPU
```

---

### 4. Does the Interpreter execute one instruction at a time?

Yes.

Conceptually, it follows this loop:

```text
Read Bytecode

↓

Decode Instruction

↓

Execute Instruction

↓

Move to Next Bytecode

↓

Repeat
```

This repeated decode-execute cycle is why interpretation is slower than executing compiled machine code.

---

# Interview Boundary

For a **7+ years Backend Java interview**, stop here.

You should confidently answer:

* What the Interpreter is.
* Why it exists.
* Why Java starts with the Interpreter.
* Why interpretation is slower than JIT-compiled code.
* Why the Interpreter is still necessary even though the JVM has a JIT compiler.

You **do not** need to know:

* HotSpot source code (`bytecodeInterpreter.cpp`)
* The internal interpreter dispatch loop (`switch` vs threaded dispatch)
* Assembly-level implementation details
* CPU register allocation inside the interpreter

These topics are useful for JVM engineers but are rarely expected in backend Java interviews.

---

I think this should be our standard format going forward. It gives just enough additional context to sound like a senior engineer without overwhelming you with JVM internals that are unlikely to come up in interviews. For the upcoming **Hot Methods**, **JIT**, **C1/C2**, and **Garbage Collection** chapters, we'll follow the same structure: deep enough to answer follow-up questions, but with a clearly defined boundary on where to stop.

Perfect. Now comes the **bridge between the Interpreter and the JIT Compiler**.

This is one of the most commonly misunderstood topics. Many people say:

> **"The JVM uses the JIT Compiler for frequently executed methods."**

The interviewer immediately asks:

> **"How does the JVM know a method is frequently executed?"**

If you can answer this, you'll stand out.

---

# Chapter 9.2 - Hot Methods ⭐⭐⭐⭐⭐

---

# First Question

We learned that the Interpreter starts executing bytecode.

Suppose we have:

```java
public class Main {

    public static void main(String[] args) {

        for (int i = 0; i < 1_000_000; i++) {
            add(10, 20);
        }
    }

    static int add(int a, int b) {
        return a + b;
    }
}
```

Initially,

the Interpreter executes:

```text
add()

↓

Read Bytecode

↓

Execute

↓

Read Bytecode

↓

Execute
```

But after thousands of executions,

the JVM asks:

> **"I'm interpreting the same method repeatedly. Should I compile it instead?"**

---

# What is a Hot Method?

**Definition (Interview)**

A **Hot Method** is a method that is executed **frequently enough** that the JVM decides it is worth compiling into native machine code using the **JIT Compiler**.

Think of it like this:

```text
Method Executed Once

↓

Not Hot

------------------

Executed 10 Times

↓

Still Not Hot

------------------

Executed Thousands of Times

↓

🔥 Hot Method

↓

JIT Compilation
```

---

# Why do we need Hot Methods?

Imagine an application with **20,000 methods**.

```text
Application

│

├── login()

├── logout()

├── validate()

├── generateReport()

├── exportPDF()

├── ...

20,000 methods
```

Question:

Should the JVM compile all 20,000 methods?

No.

Maybe only 200 methods are executed regularly.

Compiling everything would waste:

* CPU time
* Memory
* Startup time

Instead,

the JVM compiles **only the methods that prove they are important.**

---

# How does the JVM know?

The JVM maintains **execution counters**.

Every time a method executes,

its counter increases.

```text
add()

Execution Count

1

2

3

4

5

...

1000

...

10000
```

When the count crosses a threshold,

the method becomes **Hot**.

---

# Two Important Counters

For interviews, know these two names.

```text
JVM

│

├── Invocation Counter

└── Back Edge Counter
```

These determine whether code should be compiled.

---

# 1. Invocation Counter ⭐⭐⭐⭐⭐

The **Invocation Counter** counts:

> **How many times a method is called.**

Example:

```java
add();

add();

add();

add();
```

Conceptually:

```text
add()

Invocation Count

1

2

3

4
```

When the count reaches the JVM's compilation threshold,

the method becomes a candidate for JIT compilation.

---

# 2. Back Edge Counter ⭐⭐⭐⭐

This is the one many developers don't know.

Question:

Suppose we have:

```java
for (int i = 0; i < 1_000_000; i++) {

    sum += i;

}
```

The method itself may be called only once.

```text
main()

Invocation Count = 1
```

Does that mean it's not important?

No.

The loop executes **one million iterations**.

The JVM detects this using the **Back Edge Counter**.

---

# Why is it called a Back Edge?

Imagine the loop.

```text
        +------------------+
        | i < 1_000_000 ?  |
        +------------------+
                 |
          Yes    |
                 ▼
           Execute Body
                 |
                 |
                 |
                 +──────────────┐
                                │
                                ▼
                          Check Again
```

Notice the jump back to the loop condition.

That backward jump is called a **Back Edge**.

Every time it happens,

the Back Edge Counter increases.

---

# Why count loops separately?

Consider two methods.

Method A

```java
calculate();
```

Called

```text
5000 times
```

Method B

```java
for (...) {

}
```

Called only once,

but performs

```text
10 million iterations
```

Which one deserves optimisation?

Probably both.

That's why the JVM measures:

* Method calls
* Loop executions

---

# What happens when a method becomes Hot?

The JVM marks it as a candidate for compilation.

```text
Interpreter

↓

Method Executes

↓

Counters Increase

↓

Threshold Reached

↓

🔥 Hot Method

↓

Send to JIT Compiler
```

Notice:

The Interpreter **does not stop immediately**.

The method continues running while the JVM compiles it in the background.

Once compilation finishes,

future executions use the compiled machine code.

---

# Does the JVM stop the application?

No.

This is another common interview question.

The JVM compiles hot methods **in the background**.

```text
Application Running

↓

Interpreter Executes

↓

Background JIT Compilation

↓

Machine Code Ready

↓

Future Calls Use Machine Code
```

This avoids pausing the application.

---

# Are the thresholds fixed?

Not exactly.

The JVM uses heuristics and configurable thresholds, and they can vary by JVM implementation and configuration.

For interview purposes, remember:

> **A method is not compiled immediately. It must first become "hot".**

Avoid memorising specific threshold values—they differ across Java versions and JVM implementations.

---

# Complete Flow

```text
Bytecode

↓

Interpreter

↓

Invocation Counter ↑

↓

Back Edge Counter ↑

↓

Threshold Reached

↓

🔥 Hot Method

↓

JIT Compiler

↓

Native Machine Code

↓

Future Executions Become Faster
```

---

# Real-Life Analogy

Imagine you're learning a speech.

First time:

You read every line from paper.

```text
Paper

↓

Read

↓

Speak
```

After delivering it 100 times,

you memorise it.

```text
Memory

↓

Speak Directly
```

The Interpreter is like reading from the paper.

The JIT Compiler is like memorising the speech.

---

# Interview Questions

### Q1. What is a Hot Method?

A Hot Method is a method that executes frequently enough for the JVM to decide it should be compiled into native machine code by the JIT Compiler.

---

### Q2. How does the JVM know a method is hot?

The JVM tracks execution using counters, primarily:

* **Invocation Counter** (method calls)
* **Back Edge Counter** (loop iterations)

When the execution activity crosses internal thresholds, the method becomes a JIT compilation candidate.

---

### Q3. Why do we need the Back Edge Counter?

A method may execute only once but contain a loop that runs millions of times. The Back Edge Counter helps the JVM identify such performance-critical code.

---

### Q4. Does the JVM stop execution when compiling a hot method?

No.

The application continues running while the JVM compiles the method in the background. After compilation completes, future executions use the native machine code.

---

# Good to Know (Optional)

### 1. Is every frequently executed method compiled?

Not necessarily.

The JVM uses runtime profiling and heuristics. It may decide that compiling a method is not worthwhile in some situations.

---

### 2. Can a compiled method return to interpreted execution?

Yes.

If the assumptions made during optimisation become invalid (for example, due to class loading that changes type information), the JVM can **deoptimise** the compiled code and continue execution using the Interpreter before recompiling if appropriate.

This is an advanced optimisation concept, but mentioning **deoptimisation** briefly can impress an interviewer.

---

### 3. Can thresholds be configured?

Yes.

HotSpot exposes JVM options that influence compilation behaviour (such as compilation thresholds), but these values are JVM-version dependent and are rarely asked in backend interviews.

---

# Interview Boundary

For a **7+ years Backend Java interview**, know:

* What a Hot Method is.
* Why the JVM identifies hot code.
* Invocation Counter.
* Back Edge Counter.
* Why loops have a separate counter.
* That JIT compilation happens after a method becomes hot.
* That compilation occurs while the application continues running.

You do **not** need to know:

* Exact threshold values.
* HotSpot source code for profiling.
* Counter decay algorithms.
* Tiered compilation implementation details (we'll cover the high-level idea with C1/C2).

---

## Next Topic

Now we reach the **core JVM optimisation mechanism**:

# **JIT (Just-In-Time) Compiler ⭐⭐⭐⭐⭐**

This chapter will answer:

* What is the JIT Compiler?
* How does it work?
* Why is it faster than the Interpreter?
* Why doesn't Java compile everything with JIT from the beginning?
* How does the JVM switch from interpreted execution to compiled execution?

This is one of the highest-frequency JVM interview topics.


Excellent. This is **the most important chapter in JVM interviews**.

If an interviewer asks:

> **"How does Java achieve high performance?"**

or

> **"What is JIT?"**

Everything comes from this chapter.

One thing to remember:

> **Interpreter, Hot Methods, and JIT are one continuous story.**

Don't study them separately.

---

# Chapter 9.3 - JIT (Just-In-Time) Compiler ⭐⭐⭐⭐⭐

---

# Recap

From the previous chapter we learned:

```text id="6d5l4j"
Bytecode

↓

Interpreter

↓

Method Executes

↓

Counters Increase

↓

🔥 Hot Method
```

Question:

Now that the JVM knows a method is **Hot**,

**what happens next?**

Answer:

The method is sent to the **JIT Compiler**.

---

# What is the JIT Compiler?

**Definition (Interview)**

The **Just-In-Time (JIT) Compiler** is a component of the JVM that **compiles frequently executed bytecode (Hot Methods) into native machine code during program execution.**

Notice two important words:

* **Frequently Executed**
* **During Runtime**

Unlike `javac`, which works **before** the application runs,

the JIT Compiler works **while** the application is running.

---

# Why is it called "Just-In-Time"?

Because compilation happens:

* Not before execution
* Not after execution

It happens **just before the code needs to run repeatedly**.

```text id="z2ljbz"
Application Running

↓

Method Becomes Hot

↓

JIT Compiles It

↓

Future Executions Use Machine Code
```

Hence the name:

> **Just-In-Time**

---

# Interpreter vs JIT

Let's compare both.

### Interpreter

```text id="f67d7h"
Bytecode

↓

Read

↓

Decode

↓

Execute

↓

Repeat
```

Every execution repeats the same process.

---

### JIT Compiler

```text id="pq5mza"
Bytecode

↓

Compile Once

↓

Machine Code

↓

Execute Directly

↓

Execute Again

↓

Execute Again
```

Notice something.

Compilation happens **only once**.

After that,

the CPU executes native machine code directly.

---

# Visual Example

Suppose this method executes one million times.

```java id="1c9g9i"
static int add(int a, int b) {
    return a + b;
}
```

---

## Without JIT

Every call looks like:

```text id="tb1ek0"
Call 1

Read Bytecode

↓

Decode

↓

Execute

-------------------

Call 2

Read Bytecode

↓

Decode

↓

Execute

-------------------

...

1,000,000 times
```

The JVM keeps decoding the same instructions.

---

## With JIT

Initially

```text id="35j31v"
Interpreter

↓

Method Executes

↓

Counters Increase

↓

🔥 Hot Method
```

Now

```text id="mlfngm"
JIT Compiler

↓

Compile Once

↓

Machine Code
```

Future executions

```text id="obzb5m"
CPU

↓

Machine Code

↓

Machine Code

↓

Machine Code
```

No more bytecode interpretation.

---

# Where is Machine Code Stored?

Interviewers sometimes ask this.

The compiled machine code is stored in a special JVM memory area called the **Code Cache**.

```text id="c1pk98"
Bytecode

↓

JIT Compiler

↓

Machine Code

↓

Code Cache

↓

CPU
```

We'll study the Code Cache separately.

For now,

just remember:

> **Compiled native code is stored in the Code Cache.**

---

# Why is JIT Faster?

Suppose bytecode contains

```text id="4a1st9"
iload

iload

iadd

istore
```

Each execution requires

```text id="zj3r1t"
Read

↓

Decode

↓

Execute
```

Machine code removes the repeated decode step.

```text id="72mnij"
Machine Instruction

↓

CPU Executes Directly
```

Less work.

More speed.

---

# Real-life Analogy

Imagine travelling from home to work.

### Interpreter

Every morning

```text id="5hsv7s"
Open Google Maps

↓

Find Route

↓

Drive
```

Repeat every day.

---

### JIT

After driving the same route for months

```text id="6clv70"
Memorised Route

↓

Drive Directly
```

No need to consult the map.

That's exactly what the JVM does.

---

# Does the JVM Stop the Program?

No.

This is an important interview question.

Suppose

```text id="uvq9r9"
Application Running

↓

Method Gets Hot

↓

JIT Starts Compiling
```

Does the application pause?

No.

Instead

```text id="d8m4i5"
Interpreter

↓

Keeps Executing

↓

Background Compilation

↓

Machine Code Ready

↓

Future Calls Use Machine Code
```

The application continues running while compilation happens.

---

# Complete Execution Flow

```text id="xgxkl4"
Java Source

↓

javac

↓

Bytecode

↓

Interpreter

↓

Hot Method

↓

JIT Compiler

↓

Machine Code

↓

CPU
```

This is probably the most important JVM execution diagram.

---

# Why doesn't JIT compile every method?

Suppose an application contains

```text id="sbjlwm"
20,000 methods
```

Only

```text id="twwnce"
300 methods
```

execute frequently.

Compiling everything would waste:

* CPU
* Memory
* Startup time

Instead

```text id="c8qug7"
Cold Methods

↓

Interpreter

------------------

Hot Methods

↓

JIT
```

This gives Java the best balance between startup speed and runtime performance.

---

# Does JIT replace the Interpreter?

**No.**

Many developers think:

```text id="rf3xig"
Interpreter

↓

JIT

↓

Interpreter Gone
```

Wrong.

The Interpreter is **always available**.

Some methods continue to be interpreted for their entire lifetime because compiling them would provide little or no benefit.

---

# Interview Questions

### Q1. What is the JIT Compiler?

The JIT Compiler is a JVM component that compiles **hot bytecode** into **native machine code** during application execution to improve performance.

---

### Q2. Why is JIT faster than the Interpreter?

Because machine code is generated once and executed directly by the CPU, avoiding repeated bytecode decoding and interpretation.

---

### Q3. Why doesn't the JVM use JIT from the beginning?

Compiling every method would slow application startup and waste CPU and memory on methods that may never execute frequently.

---

### Q4. Does JIT compile all methods?

No.

Only methods identified as hot are typically compiled.

---

### Q5. Does the application stop while JIT compiles?

No.

The Interpreter continues executing while the JIT compiles hot methods in the background. Once compilation completes, future executions use the compiled version.

---

# Good to Know (Optional)

### 1. Is JIT part of the JVM Specification?

No.

The JVM Specification defines the behaviour of the JVM but does **not** require a JIT Compiler.

A JVM implementation could execute bytecode purely through interpretation and still conform to the specification (although it would generally be much slower).

---

### 2. Does every JVM use the same JIT?

No.

Different JVM implementations have different JIT compilers.

Examples include:

* **HotSpot JVM** (C1 and C2 compilers)
* **OpenJ9**
* **GraalVM** (can use the Graal compiler)

All follow the JVM Specification but implement optimisation differently.

---

### 3. Can compiled code become invalid?

Yes.

If the JVM makes an optimisation based on runtime assumptions and those assumptions later change (for example, due to new class loading), it can **deoptimise** the compiled code and temporarily fall back to interpreted execution.

You don't need to know the implementation details, but knowing that **deoptimisation exists** is useful.

---

# Interview Boundary

For a **7+ years Backend Java interview**, you should confidently know:

* What the JIT Compiler is.
* Why it exists.
* Why it's called "Just-In-Time".
* How it differs from the Interpreter.
* Why only hot methods are compiled.
* That compiled code is stored in the **Code Cache**.
* That JIT compilation happens while the application continues running.

You do **not** need to know:

* HotSpot compiler source code.
* Register allocation algorithms.
* Intermediate Representation (IR) internals.
* SSA (Static Single Assignment) form.

Those topics are more relevant to JVM compiler engineers than backend Java interviews.

---

## Next Topic

Now we'll answer another common senior-level interview question:

> **"I've heard about C1 and C2 Compilers. What are they, and why does HotSpot have two JIT compilers instead of one?"**

This leads us into **Tiered Compilation (C1 + C2)**, one of the most practical JVM performance concepts asked in experienced Java interviews.

Excellent. This is the last major JIT topic you need for interviews.

Many people know:

> "Java has a JIT Compiler."

Very few know:

> **"Why does HotSpot have two JIT compilers?"**

If you explain this well, interviewers usually know you've gone beyond surface-level JVM knowledge.

---

# Chapter 9.4 - C1 & C2 Compiler (Tiered Compilation) ⭐⭐⭐⭐

---

# First Question

We learned:

```text
Hot Method

↓

JIT Compiler

↓

Machine Code
```

Question:

> Is there only one JIT Compiler?

Answer:

**No (in the HotSpot JVM).**

HotSpot actually has **two JIT compilers**:

```text
HotSpot JVM

↓

JIT

├── C1 Compiler
└── C2 Compiler
```

---

# Why Two Compilers?

Imagine you're building a bridge.

Option 1

Spend six months designing the perfect bridge.

Very high quality.

But construction starts late.

---

Option 2

Build a simple bridge in two weeks.

People can start using it immediately.

Later,

replace it with a stronger bridge.

---

Which approach is better?

Usually,

**build something quickly first, then optimise later.**

The JVM follows the same philosophy.

---

# Problem with a Single Compiler

Suppose the JVM waits for maximum optimisation.

```text
Application Starts

↓

Method Gets Hot

↓

Heavy Optimisation

↓

Machine Code Ready
```

Problem:

Compilation itself takes time.

During compilation,

CPU is busy.

---

Instead,

HotSpot introduced **Tiered Compilation**.

---

# What is Tiered Compilation?

**Definition (Interview)**

Tiered Compilation is a HotSpot optimisation strategy that uses **multiple compilation levels**, starting with faster compilation and progressing to more aggressive optimisation for frequently executed code.

For interview purposes, focus on the two main JIT compilers:

* **C1 (Client Compiler)**
* **C2 (Server Compiler)**

---

# C1 Compiler

Think of C1 as

> **Compile Quickly**

Characteristics:

* Fast compilation
* Basic optimisations
* Produces machine code quickly
* Lower compilation overhead

```text
Method Becomes Hot

↓

C1

↓

Machine Code

↓

Application Gets Faster Quickly
```

---

# C2 Compiler

C2 takes a different approach.

Instead of compiling quickly,

it spends more time analysing the code.

Result:

* Better optimisation
* Faster execution
* Higher compilation cost

```text
Hot Method

↓

C2

↓

Advanced Optimisations

↓

Highly Optimised Machine Code
```

---

# Comparison

| C1 Compiler              | C2 Compiler                   |
| ------------------------ | ----------------------------- |
| Fast compilation         | Slower compilation            |
| Basic optimisations      | Advanced optimisations        |
| Lower compile cost       | Higher compile cost           |
| Good startup performance | Best long-running performance |

---

# Real-Life Analogy

Imagine writing notes.

### C1

```text
Quick Rough Notes

↓

Finish in 10 minutes

↓

Good enough
```

---

### C2

```text
Research

↓

Rewrite

↓

Improve

↓

Perfect Notes
```

More time.

Better result.

---

# Typical Flow

This is the interview diagram to remember.

```text
Application Starts

↓

Interpreter

↓

Method Executes

↓

Hot Method

↓

C1 Compiler

↓

Runs Faster

↓

Very Frequently Executed

↓

C2 Compiler

↓

Highly Optimised Machine Code
```

Notice something.

The JVM doesn't always jump directly to C2.

It often uses C1 first.

---

# What Does C2 Optimise?

You don't need implementation details,

just know the names.

Examples include:

* Method Inlining
* Escape Analysis
* Dead Code Elimination
* Loop Optimisation
* Constant Folding

We'll study each later.

---

# Why Not Always Use C2?

Interview Question.

Suppose every hot method goes directly to C2.

Problem:

```text
Application Starts

↓

Hot Method

↓

Long Compilation

↓

CPU Busy

↓

Application Waits
```

Startup becomes slower.

C1 provides a good balance.

---

# Why Not Always Use C1?

Because:

```text
Basic Optimisation

↓

Good Performance

↓

But Not Maximum Performance
```

Long-running server applications benefit greatly from deeper optimisation.

That's where C2 shines.

---

# Complete Tiered Compilation

```text
Bytecode

↓

Interpreter

↓

Method Executes

↓

Counters Increase

↓

Hot Method

↓

C1 Compiler

↓

Runs Faster

↓

More Profiling

↓

C2 Compiler

↓

Highly Optimised Machine Code
```

This is the complete execution story.

---

# Interview Questions

### Q1. What is the difference between C1 and C2?

**C1 (Client Compiler)**

* Compiles quickly.
* Applies basic optimisations.
* Improves startup performance.

**C2 (Server Compiler)**

* Compiles more slowly.
* Performs advanced optimisations.
* Delivers the best performance for long-running applications.

---

### Q2. Why does HotSpot have two JIT compilers?

To balance:

* Fast startup (C1)
* High peak performance (C2)

Using only one compiler would force a compromise between compilation speed and optimisation quality.

---

### Q3. What is Tiered Compilation?

Tiered Compilation is the HotSpot strategy of progressing from interpreted execution to increasingly optimised compiled code, combining fast startup with excellent long-term performance.

---

### Q4. Does every method reach C2?

No.

Only methods that continue to be heavily executed and are considered worth further optimisation.

---

# Good to Know (Optional)

### 1. Are C1 and C2 part of the JVM Specification?

No.

They are specific to the **HotSpot JVM implementation**.

Other JVMs may use different JIT compilers.

---

### 2. Does GraalVM use C1 and C2?

GraalVM can use the **Graal JIT Compiler**, which is a different compiler architecture from the traditional C2 compiler.

You don't need to know Graal internals unless you're interviewing specifically about GraalVM.

---

### 3. Are there more than two compilation levels?

Yes.

Modern HotSpot uses multiple **tiered compilation levels** internally, not just a simple C1-then-C2 sequence.

For backend interviews, understanding **Interpreter → C1 → C2** is sufficient and much easier to explain.

---

# Interview Boundary

For a **7+ years Backend Java interview**, know:

* Why HotSpot has two JIT compilers.
* The purpose of C1.
* The purpose of C2.
* The idea of Tiered Compilation.
* Why this improves both startup time and long-running performance.

You do **not** need to know:

* Internal compilation levels.
* Compiler Intermediate Representation (IR).
* Register allocation algorithms.
* SSA form.
* HotSpot compiler phases.

These are JVM compiler implementation details.

---

# JVM Execution Story (Completed So Far)

```text
.java
   │
javac
   │
.class (Bytecode)
   │
Class Loader
   │
Runtime Data Areas
   │
Execution Engine
   │
Interpreter
   │
Hot Method Detection
   │
JIT Compiler
   │
C1 Compiler
   │
C2 Compiler
   │
Machine Code
   │
CPU
```

At this point, you've built the complete execution pipeline from source code to CPU execution.

---

## Next Topic

Before moving to runtime optimisations like **Method Inlining** and **Escape Analysis**, the natural next topic is **Code Cache ⭐⭐⭐**.

It's a small but useful concept because we've already mentioned it several times. We'll answer:

* What is the Code Cache?
* Where does compiled machine code live?
* What happens if the Code Cache becomes full?

It's a 10-minute topic and completes the JIT compilation story before we move into the optimisation techniques themselves.
