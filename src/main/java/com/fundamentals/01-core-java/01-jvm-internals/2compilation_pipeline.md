Excellent. This is where most Java developers stop at:

> **"javac converts .java to .class."**

For a **7+ years interview**, that's nowhere near enough.

The interviewer wants to know:

> **"How does `javac` actually convert Java source code into bytecode?"**

We're going to study this exactly like compiler engineers do, but at an interview-friendly depth.

---

# Chapter 4 - Compilation Pipeline

---

# The Big Picture

Let's start with one simple Java program.

```java
public class Main {

    public static void main(String[] args) {
        int a = 10;
        int b = 20;
        int sum = a + b;

        System.out.println(sum);
    }
}
```

Now we run

```bash
javac Main.java
```

Most developers think

```text
Main.java
      │
      ▼
   javac
      │
      ▼
Main.class
```

This is **technically correct**, but it hides **7 major stages**.

The actual compilation pipeline is:

```text
                Main.java
                    │
                    ▼
        1. Lexical Analysis
                    │
                    ▼
             2. Parsing
                    │
                    ▼
      3. Abstract Syntax Tree (AST)
                    │
                    ▼
        4. Semantic Analysis
                    │
                    ▼
      5. Annotation Processing
                    │
                    ▼
       6. Bytecode Generation
                    │
                    ▼
              Main.class
```

Think of it like an assembly line in a factory.

Every stage transforms the program into something more structured.

---

# What happens when we execute `javac Main.java`?

The operating system starts the `javac` executable.

```text
Terminal

↓

javac Main.java

↓

Operating System

↓

Starts javac Process

↓

Reads Main.java

↓

Compilation Begins
```

Notice something.

The **JVM is not involved in compilation**.

`javac` itself is a Java application that runs on a JVM, but the compilation process is separate from the runtime execution of your program.

---

# Stage 1 - Lexical Analysis (Scanner / Lexer)

This is the first thing the compiler does.

## Problem

The compiler cannot understand plain English text.

It receives:

```java
int sum = a + b;
```

For us, this is readable.

For a computer, it's just characters.

```text
'i'
'n'
't'
' '
's'
'u'
'm'
...
```

Characters have no meaning.

So the compiler groups them into **tokens**.

---

## What is a Token?

A token is the smallest meaningful unit in the language.

Example:

```java
int sum = a + b;
```

becomes

```text
Keyword      → int

Identifier   → sum

Operator     → =

Identifier   → a

Operator     → +

Identifier   → b

Separator    → ;
```

Visual representation:

```text
Source Code

int sum = a + b;

        │
        ▼

Lexer

        │
        ▼

┌────────────┐
│ Keyword    │ → int
├────────────┤
│ Identifier │ → sum
├────────────┤
│ Operator   │ → =
├────────────┤
│ Identifier │ → a
├────────────┤
│ Operator   │ → +
├────────────┤
│ Identifier │ → b
├────────────┤
│ Separator  │ → ;
└────────────┘
```

Think of the lexer as someone reading an English sentence and identifying:

* nouns
* verbs
* punctuation

before understanding the grammar.

---

## Compilation Errors at this Stage

Example:

```java
int @abc = 10;
```

`@` is not valid in an identifier.

The lexer cannot even create tokens.

Compilation stops immediately.

---

# Stage 2 - Parsing

Now the compiler has tokens.

But tokens alone don't tell us the meaning.

Example:

```text
int

sum

=

a

+

b
```

We still don't know:

* What is the variable?
* What is the expression?
* What belongs together?

The parser applies Java's grammar rules.

---

## Example

Tokens:

```text
int

sum

=

a

+

b

;
```

Parser produces

```text
Variable Declaration

├── Type
│      int
│
├── Variable
│      sum
│
└── Expression
       │
       ├── a
       ├── +
       └── b
```

Now the compiler understands the structure of the statement.

---

## Parsing Errors

Example:

```java
int = 10;
```

Grammar is invalid.

No variable name exists.

Parser reports a syntax error.

Another example:

```java
if (x > 10 {

}
```

Missing `)`.

Again, a parser error.

---

# Stage 3 - Abstract Syntax Tree (AST)

This is one of the most important interview topics.

Many candidates have heard of AST but cannot explain it.

---

## Why do we need an AST?

Tokens are linear.

```text
int

sum

=

a

+

b
```

Programs are hierarchical.

The compiler needs a tree structure.

---

## Example

```java
int sum = a + b;
```

AST

```text
VariableDeclaration
│
├── Type
│      int
│
├── Name
│      sum
│
└── Initializer
       │
       BinaryExpression
       │
       ├── a
       ├── +
       └── b
```

Now the compiler can easily analyse and optimise the code.

Almost every later compilation stage works on the AST.

---

# Stage 4 - Semantic Analysis

The syntax is valid.

Now we ask:

> **Does the program actually make sense?**

This stage checks the meaning of the code.

---

## Example 1 - Undefined Variable

```java
int sum = a + b;
```

But

```java
a
```

was never declared.

Syntax is valid.

Semantically invalid.

Compiler error:

```text
Cannot find symbol
```

---

## Example 2 - Type Checking

```java
int x = "Hello";
```

Grammar is fine.

Meaning is wrong.

Compiler reports:

```text
Type mismatch
```

---

## Example 3 - Method Resolution

```java
student.calculateSalary();
```

Does the method exist?

If not:

```text
Cannot resolve method
```

---

## Semantic Analysis Checks

The compiler performs checks such as:

* Type compatibility
* Variable declaration before use
* Method overload resolution
* Generic type validation
* Access modifiers (`private`, `protected`, etc.)
* Checked exception rules
* Inheritance correctness
* Interface implementation

This is where most "cannot find symbol" and "incompatible types" errors originate.

---

# Stage 5 - Annotation Processing

This stage surprises many interview candidates.

Modern Java compilation isn't just about source code.

The compiler also processes annotations.

Example:

```java
@Getter
@Setter
@Entity
@Builder
```

Libraries such as Lombok generate additional source code during compilation.

High-level flow:

```text
Source Code

↓

AST

↓

Annotation Processor

↓

Generated Source

↓

Continue Compilation
```

Examples:

* Lombok
* MapStruct
* Dagger
* AutoValue

Interviewers may ask:

> **When does Lombok generate getters and setters?**

Answer:

**During annotation processing, before bytecode generation.**

---

# Stage 6 - Bytecode Generation

Finally, the compiler converts the validated AST into JVM bytecode.

Example:

```java
int c = a + b;
```

becomes instructions similar to:

```text
iload_1
iload_2
iadd
istore_3
```

Notice:

The compiler generates **JVM bytecode**, **not machine code**.

That bytecode is stored in:

```text
Main.class
```

The JVM will execute this bytecode later.

---

# Compilation Errors by Stage

| Stage                 | Example Error                                                |
| --------------------- | ------------------------------------------------------------ |
| Lexical Analysis      | Invalid character in identifier                              |
| Parsing               | Missing `;`, missing `)`                                     |
| Semantic Analysis     | Cannot find symbol, incompatible types, method not found     |
| Annotation Processing | Invalid annotation usage, annotation processor failures      |
| Bytecode Generation   | Rare; usually internal compiler issues rather than user code |

---

# Complete Compilation Pipeline

```text
                    Main.java
                        │
                        ▼
         1. Lexical Analysis (Scanner)
            Characters → Tokens
                        │
                        ▼
              2. Parsing
          Tokens → Syntax Structure
                        │
                        ▼
         3. Abstract Syntax Tree (AST)
         Hierarchical representation
                        │
                        ▼
          4. Semantic Analysis
       Type checks, symbol resolution,
       access checks, generics, etc.
                        │
                        ▼
        5. Annotation Processing
       Generate additional source code
                        │
                        ▼
        6. Bytecode Generation
        AST → JVM Bytecode (.class)
```

---

# Interview Questions

### Q1. What happens when you run `javac Main.java`?

A high-quality answer is:

> The `javac` compiler reads the Java source file and passes it through multiple compilation phases. It first performs lexical analysis to convert characters into tokens, then parses those tokens according to Java grammar to build an Abstract Syntax Tree. Next, it performs semantic analysis, including type checking, symbol resolution, and access validation. It then runs annotation processors, if any are present, and finally generates JVM bytecode, which is written to a `.class` file.

---

### Q2. What is the difference between parsing and semantic analysis?

* **Parsing** checks whether the code follows Java's grammar (syntax).
* **Semantic analysis** checks whether the syntactically correct code is logically valid, such as ensuring variables exist, types are compatible, methods can be resolved, and access rules are respected.

---

### Q3. What is an AST?

An **Abstract Syntax Tree (AST)** is a hierarchical tree representation of the program's structure. It removes unnecessary syntax details (such as many punctuation tokens) and organises the code into meaningful constructs like declarations, expressions, statements, and method calls. Most compiler analyses and optimisations operate on the AST rather than raw source text.

---

## Before Moving On

The next topic is **Bytecode**, and it's one of the most important in the JVM journey. We'll answer questions like:

* What exactly is bytecode?
* Why did Java choose a **stack-based virtual machine** instead of a register-based one?
* What are opcodes?
* How does one Java statement translate into multiple bytecode instructions?
* How does the JVM execute those instructions?

Understanding bytecode is the bridge between compilation and JVM execution, and it makes later topics like `javap`, class loading, and the execution engine much easier to understand.
