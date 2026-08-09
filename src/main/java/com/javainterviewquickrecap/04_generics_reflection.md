# Module 1d — Generics & Reflection — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---


Perfect. These topics are mostly **language features**. For a **7+ years Java interview**, you should know **what they solve, where they're used, and the common interview questions**. Very few interviewers go into JVM internals here (except Type Erasure and Reflection).

Let's rapid-fire them.

---

# 1. Generics ⭐⭐⭐⭐⭐

## What?

Allows writing **type-safe reusable code**.

Without Generics

```java
List list = new ArrayList();

list.add("Java");
list.add(100);
```

Problem

```java
String s = (String) list.get(1); // ClassCastException
```

With Generics

```java
List<String> list = new ArrayList<>();
```

Compiler prevents invalid types.

---

## Why?

```text
Without Generics
      │
      ▼
Object
      │
Manual Casting
      │
Runtime Errors

----------------------------

With Generics
      │
      ▼
Compile Time Type Checking
      │
No Casting
      │
Safer Code
```

### Interview Questions

* Why Generics?
* What problem do they solve?
* Are Generics available at runtime? (No)

---

# 2. Type Erasure ⭐⭐⭐⭐⭐

Most asked Generics question.

## What?

Java removes generic type information during compilation.

```java
List<String>
```

becomes

```java
List
```

at runtime.

---

### Diagram

```text
Source Code

List<String>

      │ javac

      ▼

Bytecode

List

      │

      ▼

JVM
```

Because of this

```java
new ArrayList<String>().getClass()
==
new ArrayList<Integer>().getClass()
```

↓

```text
true
```

### Interview Questions

* What is Type Erasure?
* Why introduced? (Backward compatibility)
* Can Reflection know generic types? (Limited metadata only.)

---

# 3. Bounded Types ⭐⭐⭐

Restrict generic types.

```java
class Box<T extends Number>
```

Only

```text
Integer

Double

Float
```

Allowed.

Useful when generic code needs methods from a superclass/interface.

---

# 4. Wildcards ⭐⭐⭐⭐

Three types.

```java
<?>
```

Unknown type.

```java
<? extends Number>
```

Read only (Producer)

```java
<? super Integer>
```

Write Integer (Consumer)

---

# 5. PECS ⭐⭐⭐⭐⭐

Very common interview question.

> **Producer Extends Consumer Super**

```text
Reads Data?
      │
      ▼
extends

Writes Data?
      │
      ▼
super
```

Example

```java
List<? extends Number>
```

Read

✔

Add

❌

---

```java
List<? super Integer>
```

Read as Object

✔

Add Integer

✔

### Interview Question

Explain PECS with an example.

---

# 6. Bridge Methods ⭐⭐⭐

Rare but asked by JVM enthusiasts.

Compiler generates synthetic methods after Type Erasure to preserve polymorphism.

Example

```java
class Parent<T>{

    T get(){}
}
```

↓

Compiler generates bridge methods internally.

Know the concept; no need to memorise bytecode.

---

# 7. Heap Pollution ⭐⭐⭐

Mixing raw types with generics.

```java
List<String> list = new ArrayList();

List raw = list;

raw.add(100);
```

Later

```java
String s = list.get(0);
```

↓

Runtime failure.

Avoid raw types.

---

# 8. Generic Arrays ⭐⭐⭐

Illegal

```java
new T[10];
```

Why?

Type Erasure.

Allowed

```java
Object[]
```

or

```java
Array.newInstance()
```

### Interview Question

Why can't Java create generic arrays?

---

---

# Reflection ⭐⭐⭐⭐⭐

## What?

Allows inspecting and modifying classes at runtime.

```java
Class<?> clazz = Employee.class;
```

Can inspect

* fields
* methods
* constructors
* annotations

---

### Diagram

```text
Running JVM

      │

Employee.class

      │

Reflection API

      │

Read Methods

Read Fields

Invoke Methods
```

---

### Where Used?

* Spring
* Hibernate
* JUnit
* Jackson
* Dependency Injection

---

### Interview Questions

* What is Reflection?
* Why slower?
* Why dangerous?
* Where is Reflection used?

---

# Reflection API ⭐⭐⭐⭐

Important classes

```java
Class

Method

Field

Constructor
```

Example

```java
clazz.getDeclaredMethods();
```

---

# Dynamic Proxy ⭐⭐⭐⭐⭐

Extremely important for Spring.

Creates objects dynamically at runtime.

```text
Client

   │

Proxy

   │

Real Service
```

Used for

* Spring AOP
* Transactions
* Logging
* Security

### Interview Questions

* What is JDK Dynamic Proxy?
* Difference from CGLIB?

Quick answer:

| JDK Proxy          | CGLIB               |
| ------------------ | ------------------- |
| Interface required | Subclasses class    |
| Uses Proxy API     | Bytecode generation |

---

# Method Handles ⭐⭐⭐

Modern alternative to Reflection.

```java
MethodHandle
```

Faster than Reflection.

Used heavily inside JVM and modern frameworks.

Know the purpose only.

---

# Annotations ⭐⭐⭐⭐⭐

Metadata for compiler/framework.

Example

```java
@Override

@Service

@Entity

@Autowired
```

Annotations themselves don't execute code.

Frameworks read them.

---

### Diagram

```text
Source Code

 @Service

      │

Compiler

      │

Class Metadata

      │

Spring Reads

      │

Creates Bean
```

---

### Interview Questions

* Can annotations contain logic? (No)
* Retention policies?
* Built-in vs Custom annotations?

---

# Runtime Metadata ⭐⭐⭐

Information stored in `.class` file.

Contains

* annotations
* methods
* fields
* modifiers

Reflection reads this metadata.

---

# Complete Flow ⭐⭐⭐⭐⭐

This connects almost every topic.

```text
            Java Source

                 │

      Generics / Annotations

                 │

             javac

                 │

      Type Erasure Happens

                 │

             Bytecode

                 │

               JVM

                 │

        Runtime Metadata

                 │

           Reflection API

                 │

      Spring / Hibernate

                 │

 Dynamic Proxies / AOP
```

---

# 7+ Years Interview Questions ⭐⭐⭐⭐⭐

## Generics

* Why do we need Generics?
* Explain Type Erasure.
* What is PECS?
* Why can't generic arrays be created?
* What is Heap Pollution?

---

## Reflection

* What is Reflection?
* Where is Reflection used in Spring?
* Why is Reflection slower than direct method calls?
* Can Reflection access private methods? (**Yes, with appropriate access checks**)

---

## Dynamic Proxy

* What is Dynamic Proxy?
* Difference between JDK Proxy and CGLIB?
* How does Spring AOP work internally?
* Why does `@Transactional` fail on self-invocation? (**Because the call bypasses the proxy.**)

---

## Annotations

* How are annotations processed?
* What are retention policies?
* Difference between `SOURCE`, `CLASS`, and `RUNTIME`?
* Can you create custom annotations?

---

# ⭐ Must Remember (Most Frequently Asked)

```text
Generics
    │
    ▼
Compile-time Safety
    │
    ▼
Type Erasure
    │
    ▼
Bytecode
    │
    ▼
Runtime Metadata
    │
    ▼
Reflection
    │
    ▼
Dynamic Proxy
    │
    ▼
Spring AOP / @Transactional / @Cacheable
```

This is the mental model interviewers expect from a **7+ years Java developer**. You don't just know each feature in isolation—you understand how the language features enable frameworks like Spring and Hibernate internally.



Sure—here it is as a reusable writing block.

# 1. Type Erasure ⭐⭐⭐⭐⭐

## What is it?

**Type Erasure is the process where the Java compiler removes all generic type information after compilation.**

Example:

```java
List<String> names = new ArrayList<>();
```

After compilation, JVM actually sees

```java
List names = new ArrayList();
```

The JVM does **not** know that it was `String`.

---

## Why was it introduced?

Imagine Java 1.4 (before Generics).

Millions of programs already existed.

Java 5 introduced Generics.

If JVM suddenly required

```java
List<String>
```

instead of

```java
List
```

every old application would stop working.

Instead Java compiler does this

```text
          Java Source

List<String>

        │

        ▼

      javac

        │

Remove Generic Types

        │

        ▼

Bytecode

List
```

This keeps old bytecode compatible.

**Problem Solved**

✔ Backward compatibility

---

## Example

```java
List<String> names = new ArrayList<>();

List<Integer> ages = new ArrayList<>();

System.out.println(names.getClass());

System.out.println(ages.getClass());
```

Output

```text
class java.util.ArrayList

class java.util.ArrayList
```

Both are same.

Why?

Because runtime only sees

```text
ArrayList
```

---

## Interview Questions

* Why Type Erasure?
* Is Generic information available at runtime?
* Why can't JVM differentiate `List<String>` and `List<Integer>`?

---

# 2. Bounded Types ⭐⭐⭐⭐

## What?

Restrict generic types.

Instead of allowing **any** type

```java
class Box<T>
```

allow only Numbers

```java
class Box<T extends Number>
```

---

## Why?

Suppose

```java
class Calculator<T>{
}
```

Inside

```java
value.doubleValue();
```

Compiler complains.

Why?

T may be

```text
String
Employee
Car
```

No `doubleValue()` method.

So we restrict it.

```java
class Calculator<T extends Number>
```

Now compiler knows

```text
Integer
Double
Float
```

are valid.

---

## Diagram

```text
        T

        │

extends Number

        │

 ┌──────┼────────┐

Integer Double Float
```

---

## Example

```java
class Calculator<T extends Number>{

    T value;

    double square(){

        return value.doubleValue()*value.doubleValue();
    }
}
```

---

## Interview Questions

* Why bounded generics?
* Difference between `extends` and `super`?

---

# 3. Wildcards ⭐⭐⭐⭐

## What?

Represents an **unknown type**.

```java
List<?>
```

Means

"I don't know what's inside."

---

## Why?

Suppose

```java
List<Integer>
List<Double>
List<Float>
```

Need one method.

Instead of writing three methods

Use

```java
List<? extends Number>
```

---

## Diagram

```text
           ?

Unknown Type

      │

Could be

 │    │    │

Int Double Float
```

---

## Example

```java
void print(List<?> list){

    for(Object o:list)

        System.out.println(o);
}
```

Works for

```java
List<Integer>
List<String>
List<Employee>
```

---

## Interview Questions

* Difference between `T` and `?`
* Why wildcard?

---

# 4. PECS ⭐⭐⭐⭐⭐

## What?

PECS stands for **Producer Extends Consumer Super**.

It helps decide which wildcard to use depending on whether you are **reading** or **writing**.

---

## Why?

If a collection **produces** objects for you to read, use:

```java
? extends T
```

If a collection **consumes** objects that you insert, use:

```java
? super T
```

---

## Diagram

```text
Reads?

↓

extends

Writes?

↓

super
```

---

## Example

```java
Collections.copy(destination, source);
```

Signature

```java
copy(List<? super T> dest,
     List<? extends T> src)
```

Source produces elements.

Destination consumes elements.

---

## Interview Questions

* Explain PECS.
* Why is source `extends` and destination `super`?

---

# 5. Bridge Methods ⭐⭐⭐

## What?

Compiler-generated methods that preserve polymorphism after Type Erasure.

---

## Why?

```java
class Parent<T>{

    T get(){ return null; }
}

class Child extends Parent<String>{

    @Override
    String get(){
        return "Java";
    }
}
```

After Type Erasure

Parent becomes

```java
Object get()
```

Child becomes

```java
String get()
```

The signatures no longer match.

The compiler secretly generates

```java
Object get(){
    return get();
}
```

This synthetic method is called a **Bridge Method**.

---

## Diagram

```text
Source Code

Parent<T>

      │

Type Erasure

      │

Compiler Adds

Bridge Method

      │

Runtime Works
```

---

## Interview Questions

* Why bridge methods?
* Relation with Type Erasure?

---

# 6. Heap Pollution ⭐⭐⭐⭐

## What?

Heap contains an object of an unexpected type.

---

## Why?

Mixing raw types with generic types.

Example

```java
List<String> list = new ArrayList<>();

List raw = list;

raw.add(100);
```

Later

```java
String s = list.get(0);
```

Throws

```text
ClassCastException
```

---

## Diagram

```text
Expected

List<String>

        │

Actual

String

Integer ❌
```

---

## Interview Questions

* What is Heap Pollution?
* Why are raw types dangerous?

---

# 7. Generic Arrays ⭐⭐⭐

## What?

Java doesn't allow

```java
new T[10];
```

---

## Why?

Arrays know their component type at runtime.

Generics lose type information due to Type Erasure.

---

## Diagram

```text
Arrays

Know Type

Runtime

✔



Generics

Type Removed

Compile Time

❌
```

---

## Interview Questions

* Why can't Java create generic arrays?

---

# 8. Reflection ⭐⭐⭐⭐⭐

## What?

Allows inspecting and manipulating classes during runtime.

---

## Why?

Frameworks don't know your classes beforehand.

Reflection allows them to discover them dynamically.

---

## Example

```java
Class<?> clazz = Employee.class;

clazz.getDeclaredMethods();
```

---

## Diagram

```text
Application Starts

       │

Employee.class

       │

Reflection

       │

Read

Fields

Methods

Constructors
```

---

## Interview Questions

* What is Reflection?
* Why is Reflection slower?
* Where is Reflection used?

---

# 9. Reflection API ⭐⭐⭐

## What?

The Java API used for reflection.

Main classes

```text
Class
Field
Method
Constructor
```

Example

```java
Method method = clazz.getDeclaredMethod("save");
```

---

## Interview Questions

* Difference between Reflection and Reflection API?

---

# 10. Dynamic Proxies ⭐⭐⭐⭐⭐

## What?

Objects generated dynamically at runtime.

---

## Why?

To add extra behaviour without modifying the original class.

Examples

* Logging
* Transactions
* Security
* AOP

---

## Diagram

```text
Client

 │

 ▼

Proxy

 │

 ▼

Actual Service
```

---

## Spring Example

```java
@Transactional
public void transfer(){}
```

Proxy

↓

Start Transaction

↓

Call Original Method

↓

Commit/Rollback

---

## Interview Questions

* JDK Dynamic Proxy vs CGLIB?
* Why does self-invocation break `@Transactional`?

---

# 11. Method Handles ⭐⭐⭐

## What?

A modern and faster alternative to Reflection.

---

## Why?

Reflection has runtime overhead.

Method Handles are optimised by the JVM.

---

## Diagram

```text
Reflection

↓

Slower



MethodHandle

↓

Faster
```

---

## Interview Questions

* Difference between Reflection and MethodHandle?

---

# 12. Annotations ⭐⭐⭐⭐⭐

## What?

Metadata attached to classes, methods or fields.

Example

```java
@Service

@Entity

@Override
```

---

## Why?

Allows frameworks to configure behaviour without XML.

---

## Diagram

```text
@Service

     │

Compiler Stores

Metadata

     │

Spring Reads

     │

Creates Bean
```

---

## Interview Questions

* Can annotations execute code?
* What are Retention Policies?
* How do custom annotations work?

---

# 13. Runtime Metadata ⭐⭐⭐

## What?

Metadata stored inside the `.class` file.

Includes

* Fields
* Methods
* Constructors
* Modifiers
* Annotations

Reflection reads this metadata.

---

## Diagram

```text
Java Source

      │

javac

      │

.class File

      │

Runtime Metadata

      │

Reflection

      │

Frameworks
```

---

# Complete Interview Flow ⭐⭐⭐⭐⭐

```text
           Java Source Code
                  │
     ┌────────────┴────────────┐
     │                         │
 Generics                Annotations
     │                         │
     └────────────┬────────────┘
                  ▼
               javac
                  │
        Type Erasure Happens
                  │
                  ▼
             .class File
                  │
      Runtime Metadata Stored
                  │
                  ▼
          Reflection API Reads
                  │
                  ▼
        Spring / Hibernate / JUnit
                  │
                  ▼
     Dynamic Proxies / AOP / DI
```

If you'd like, the next section can follow the same interview-focused format for **Exception Handling** or we can jump directly to **Collections Framework** (the highest-priority topic for 7+ years Java interviews).

## Q1. How does Java implement generics? Explain type erasure and its consequences.

### 1. Why This Concept Matters
Generics is the most misunderstood feature in Java. Most developers use `List<String>` without understanding how it works. When things break — heap pollution, bridge method errors, unchecked warnings — you need to understand erasure. Interviewers ask this to separate developers who "use" Java from those who "understand" Java.

### 2. Basic Meaning

**Generics**: Types that can be parameterized (e.g., `List<String>` where String is the type parameter).

**Type Erasure**: The compiler removes all generic type information during compilation. The runtime has NO knowledge of generics.

**Analogy**: Shipping boxes with labels.
- **With generics**: Box labeled "Books" — you KNOW it contains books (compile-time safety)
- **Without generics**: Plain box — you must open and check (runtime casts)
- **Erasure**: At shipping time (runtime), the label is removed. The box is just a box of Objects

### 3. Real Code / Real Example

```java
// =====================================================
// TYPE ERASURE DEMONSTRATION
// =====================================================

// What you write:
List<String> strings = new ArrayList<>();
List<Integer> integers = new ArrayList<>();

// What the compiler sees after erasure (SAME bytecode):
List strings = new ArrayList();      // Raw type
List integers = new ArrayList();     // Raw type

// Proof at runtime:
System.out.println(strings.getClass() == integers.getClass());  // TRUE!
// Both are ArrayList.class — generic info is GONE

// What you write:
public class Box<T> {
    private T value;
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }
}

// After erasure (compiler replaces T with Object):
public class Box {
    private Object value;
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
}

// If bounded: <T extends Number>
// After erasure: T becomes Number (not Object)
public class NumberBox<T extends Number> {
    private T value;
    public T getValue() { return value; }
    // After erasure: value is Number, getValue returns Number
}

// =====================================================
// CONSEQUENCES OF ERASURE
// =====================================================

// 1. Cannot use instanceof with generic types:
// ❌ COMPILE ERROR:
// if (list instanceof List<String>) { }  // Cannot check type parameter

// ✅ Workaround: unbounded wildcard
if (list instanceof List<?>) { }  // OK — just checks if it's a List

// 2. Cannot create arrays of generic types:
// ❌ COMPILE ERROR:
// List<String>[] array = new List<String>[10];

// ✅ Workaround: raw type + cast (with warning)
@SuppressWarnings("unchecked")
List<String>[] array = (List<String>[]) new List[10];

// 3. Cannot use primitive type parameters:
// ❌ COMPILE ERROR:
// List<int> numbers = new ArrayList<>();

// ✅ Must use wrapper types:
List<Integer> numbers = new ArrayList<>();

// 4. Cannot create new instances of type parameter:
// ❌ COMPILE ERROR:
// public T createInstance() { return new T(); }
```

### 4. What Happens Internally

**Compilation pipeline for generics:**
```
Source: Box<String> box = new Box<>();

Step 1: Parser creates AST
  └─ Type: Box<String> (parameterized type)
  
Step 2: Type checking
  └─ Verify: String is valid argument for T (no bounds violation)
  
Step 3: Type erasure
  └─ Remove type parameters: Box<String> → Box
  └─ Insert casts where needed
  └─ Generate bridge methods if needed

Step 4: Bytecode generation
  └─ box.setValue("hello") 
     → Before erasure: invokes Box.setValue(String)
     → After erasure: invokes Box.setValue(Object) with implicit cast
     
  └─ String s = box.getValue()
     → Before erasure: returns String
     → After erasure: returns Object → checkcast String (inserted by compiler)
```

**Inserted casts:**
```java
// Source code:
Box<String> box = new Box<>();
box.setValue("hello");
String value = box.getValue();

// After erasure (what JVM executes):
Box box = new Box();
box.setValue("hello");          // Object parameter — String is Object, OK
String value = (String) box.getValue();  // COMPILER INSERTED CAST!
```

### 5. Tricky Interview Cases

**Case 1: Bridge methods — overriding with generics**
```java
// Parent:
class Parent {
    public Object getValue() { return "parent"; }
}

// Child with generic override:
class Child extends Parent {
    @Override
    public String getValue() { return "child"; }
    // Override with covariant return type (String instead of Object)
}

// Compiler generates a BRIDGE method:
// public Object getValue() {       // BRIDGE — calls String version
//     return this.getValue();      // Calls Child's String getValue()
// }
// This is needed because at bytecode level, overriding requires
// exact signature match. The bridge bridges Object→String.
```

**Case 2: PECS violation at compile time**
```java
// Producer Extends, Consumer Super

List<? extends Number> producer = new ArrayList<Integer>();
Number n = producer.get(0);  // OK — read as Number
// producer.add(5);           // ❌ COMPILE ERROR — cannot add to producer

List<? super Integer> consumer = new ArrayList<Number>();
consumer.add(5);               // OK — can add Integer
// Integer i = consumer.get(0); // ❌ COMPILE ERROR — read as Object
Object o = consumer.get(0);    // OK — read as Object
```

**Case 3: Raw type causes heap pollution**
```java
List<String> strings = new ArrayList<>();
List raw = strings;                 // Raw type — assignment OK (backward compat)
raw.add(123);                       // Heap pollution! Integer in List<String>!

String s = strings.get(0);         // ClassCastException at runtime!
// Compiler inserted cast to String, but element is Integer
// Exception: java.lang.ClassCastException: Integer cannot be cast to String
```

**Case 4: Varargs and heap pollution**
```java
// @SafeVarargs suppresses the warning
@SafeVarargs
public static <T> void addToList(List<T>... lists) {  // Array of List!
    // lists parameter is actually List[] (array of raw List)
    Object[] array = lists;  // Legal! List<T>[] → Object[]
    array[0] = Arrays.asList(123);  // Heap pollution!
    
    T element = lists[0].get(0);  // ClassCastException when T is String!
}
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Using `instanceof` with generic type | Compile error | Use `instanceof Collection<?>` or check element class individually |
| Creating arrays of parameterized types | Compile error | Use `ArrayList<List<String>>` or suppressed cast |
| Mixing raw and generic types | Heap pollution — ClassCastException at runtime | Never assign raw type to generic variable |
| Using primitive types as type arguments | Compile error | Use wrapper types (Integer, Double, etc.) |
| Assuming `T.class` is available at runtime | Compile error (`T` is erased) | Pass `Class<T>` as method parameter |

### 7. Production Usage

**Type token pattern (pass Class<T> to retain type info):**
```java
// Generic Spring-like rest template
public class GenericRestClient {
    private final RestTemplate restTemplate;
    
    public <T> T getForObject(String url, Class<T> responseType) {
        // Class<T> survives erasure — can instantiate via reflection
        ResponseEntity<T> response = restTemplate.exchange(
            url, HttpMethod.GET, null, responseType
        );
        return response.getBody();
    }
}

// Usage: type info preserved
User user = client.getForObject("/api/users/1", User.class);
// No cast needed! Class<User> provides runtime type info
```

**TypeReference pattern (Jackson):**
```java
// Jackson's TypeReference uses anonymous class to capture generic type
// The anonymous class preserves the generic type information
// because it's part of the class's actual (non-erased) type!

List<User> users = objectMapper.readValue(
    json, 
    new TypeReference<List<User>>() {}  // {} creates anonymous subclass
    // Anonymous class: TypeReference<List<User>> extends TypeReference
    // JVM CAN get List<User> from this class's generic superclass
);
```

### 8. Advanced Details

**Reflection with generics — getGenericSuperclass():**
```java
// How TypeReference works internally:
public abstract class TypeReference<T> {
    private final Type type;
    
    protected TypeReference() {
        // Get the generic superclass of the ANONYMOUS subclass
        Type superclass = getClass().getGenericSuperclass();
        // superclass is: TypeReference<List<User>>
        // Not erased because it's part of the CLASS definition
        
        // Extract the actual type parameter:
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        // type = List<User>
    }
    
    public Type getType() { return type; }
}
```

**Reifiable types: Types NOT affected by erasure:**
- Primitive types (int, double)
- Non-generic classes/interface (String, List)
- Raw types (List, Map)
- Unbounded wildcards (List<?>, Map<?,?>)

### 9. Interview Questions And Answers

#### Beginner

**Q**: What is type erasure in Java generics?

**A**: Type erasure means the compiler removes all generic type information during compilation. `List<String>` and `List<Integer>` become just `List` at runtime. The compiler inserts casts where needed. This ensures backward compatibility with pre-Java 5 code that didn't have generics. Consequences: you can't use `instanceof List<String>`, can't create `new T()`, and can't create arrays of parameterized types.

#### Intermediate

**Q**: Explain the PECS principle with an example.

**A**: PECS = Producer Extends, Consumer Super. If a collection **produces** elements (you read from it), use `? extends T`: `List<? extends Number>` — safe to read as Number. If a collection **consumes** elements (you write to it), use `? super T`: `List<? super Integer>` — safe to write Integers. Neither allows both read and write safely. Example: `Collections.copy(List<? super T> dest, List<? extends T> src)` — dest consumes, src produces.

#### Senior

**Q**: How does Jackson's TypeReference work to preserve generic type information despite erasure?

**A**: Jackson uses the **super type token pattern**. `new TypeReference<List<User>>() {}` creates an **anonymous subclass** of `TypeReference<List<User>>`. The generic superclass of this anonymous class is `TypeReference<List<User>>` — a `ParameterizedType`. Since the anonymous class is created at compile time (not erased), the JVM retains the actual type argument. `getClass().getGenericSuperclass()` returns `TypeReference<List<User>>`, and `getActualTypeArguments()[0]` returns `List<User>` — which preserves the `User` type that would otherwise be erased.

#### Tricky

**Q**: Can you create a method that accepts either `List<Integer>` or `List<Double>` but NOT `List<String>`?

**A**: No, you cannot check this at compile time because `List<Integer>`, `List<Double>`, and `List<String>` are all the same type at the bytecode level (`List`). However, you can constrain using wildcards: `void process(List<? extends Number> list)` accepts `List<Integer>`, `List<Double>`, but compile-time rejects `List<String>` because `String` doesn't extend `Number`. But this is compile-time safety through bounds, not runtime type checking. You CANNOT write a method that accepts `List<Integer>` only and rejects `List<Double>` — both extend Number, both are erased to `List`.

### 10. Final 30-Second Answer

Generics use type erasure: type parameters are removed at compile time, casts are inserted. Consequences: no `instanceof`, no `new T()`, no generic arrays. PECS guide wildcards. Type tokens (Class<T>, TypeReference) preserve type info through anonymous subclasses.

---

## Q2. How does Java reflection work? What are the risks and use cases?

### 1. Why This Concept Matters
Reflection is the foundation of Spring, Hibernate, JPA, Mockito, Jackson, and every major Java framework. Without reflection, dependency injection, ORM, serialization, and testing would be impossible. Interviewers ask this to test if you understand **meta-programming** — code that operates on other code.

### 2. Basic Meaning

**Reflection**: The ability of a program to **inspect and modify its own structure at runtime**. You can access classes, methods, fields, and constructors that weren't known at compile time.

**Analogy**: A mirror in a dressing room. You can see yourself (the class), examine your clothes (fields), try on outfits (methods), and change your look (modify state) — all at runtime, without knowing what you'd wear beforehand.

### 3. Real Code / Real Example

```java
// =====================================================
// REFLECTION BASICS
// =====================================================

class Person {
    private String name;
    private int age;
    
    public Person() {}  // No-arg constructor needed for Class.newInstance()
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    private String getSecret() {
        return "This is secret: " + name;
    }
    
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
}

public class ReflectionDemo {
    public static void main(String[] args) throws Exception {
        
        // =====================================================
        // 1. GET CLASS OBJECT (3 ways)
        // =====================================================
        
        // Way 1: Class.forName() — most common in frameworks
        Class<?> clazz1 = Class.forName("com.example.Person");
        
        // Way 2: .class literal — compile-time known
        Class<?> clazz2 = Person.class;
        
        // Way 3: getClass() — from an instance
        Person person = new Person();
        Class<?> clazz3 = person.getClass();
        
        // All 3 return the SAME Class object (one per classloader)
        System.out.println(clazz1 == clazz2);  // true
        
        // =====================================================
        // 2. CREATE INSTANCE (without calling constructor directly)
        // =====================================================
        
        // Using no-arg constructor:
        Person p1 = (Person) clazz1.getDeclaredConstructor().newInstance();
        
        // Using parameterized constructor:
        Constructor<?> constructor = clazz1.getDeclaredConstructor(String.class, int.class);
        Person p2 = (Person) constructor.newInstance("Alice", 30);
        
        // =====================================================
        // 3. ACCESS PRIVATE FIELDS
        // =====================================================
        
        Field nameField = clazz1.getDeclaredField("name");
        nameField.setAccessible(true);  // OVERRIDE private access!
        nameField.set(p2, "Bob");       // Set private field!
        System.out.println(p2.getName());  // "Bob"
        
        // =====================================================
        // 4. INVOKE PRIVATE METHOD
        // =====================================================
        
        Method secretMethod = clazz1.getDeclaredMethod("getSecret");
        secretMethod.setAccessible(true);  // Access private method!
        String secret = (String) secretMethod.invoke(p2);
        System.out.println(secret);  // "This is secret: Bob"
    }
}
```

### 4. What Happens Internally

**Class.forName() internals:**
```
Class.forName("com.example.Person"):

1. Check if class is already loaded in ClassLoader
2. If not: Delegate to ClassLoader
   └─ Bootstrap → Platform → Application ClassLoader
3. ClassLoader reads Person.class file
4. defineClass() parses bytecode
5. Creates Class<?> object in Metaspace
6. Returns Class<?> reference

This is WHY class names must be fully qualified:
"com.example.Person" not "Person"
```

**Method.invoke() internals:**
```
method.invoke(target, args):

1. JVM checks accessibility (can throw IllegalAccessException)
2. If setAccessible(true) was called → skip access check
3. JIT: if method is "hot", may inline the call
4. Performance:
   - Normal method call: ~1ns
   - Reflective call: ~200ns (200x slower!)
   - With setAccessible(true): ~100ns (100x slower)
   - After JIT inlining: ~10ns (added cost of reflection overhead)
```

### 5. Tricky Interview Cases

**Case 1: ClassNotFoundException vs NoClassDefFoundError**
```java
// ClassNotFoundException — class NOT on classpath at all:
try {
    Class.forName("com.missing.Class");
} catch (ClassNotFoundException e) {
    // Class was NEVER available — path issue
}

// NoClassDefFoundError — class WAS available when compiled, MISSING at runtime:
// This happens when a dependency JAR is missing at runtime
try {
    new SomeClass();  // Compiled fine, but runtime: SomeClass.class not found
} catch (NoClassDefFoundError e) {
    // Class was present at compile time, missing at runtime
}
```

**Case 2: setAccessible() — security implications**
```java
// Java 17+: Strong encapsulation by default
// --add-opens java.base/java.lang=ALL-UNNAMED
// Without this, setAccessible() on JDK internal classes fails!

// ❌ Java 17+ (without --add-opens):
Method m = String.class.getDeclaredMethod("value");  // private char[] value
m.setAccessible(true);  // InaccessibleObjectException!
// String's internal field 'value' is not accessible by default

// ✅ Workaround: ReflectionFactory (framework use only)
```

**Case 3: Performance — cached vs uncached reflection**
```java
// ❌ SLOW: Lookup every time
for (int i = 0; i < 100000; i++) {
    Method m = MyClass.class.getMethod("doSomething");
    m.invoke(obj);
}

// ✅ FAST: Cache the Method object
Method m = MyClass.class.getMethod("doSomething");
m.setAccessible(true);
for (int i = 0; i < 100000; i++) {
    m.invoke(obj);  // Reuse cached Method object
}
// Cached is ~10-50x faster than uncached!
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Not calling setAccessible(true) | IllegalAccessException on private members | Call setAccessible(true) before invoking |
| Forgetting method signatures must match exactly | NoSuchMethodException | Include parameter types in getDeclaredMethod |
| Using reflection when static code is possible | Performance penalty, compile-time errors missed | Use reflection only for framework code, not business logic |
| Not handling checked exceptions | InvocationTargetException wraps the real exception | Call getCause() on InvocationTargetException |
| Modifying final fields via reflection | Works but is dangerous and may not be visible | Avoid unless absolutely necessary |

### 7. Production Usage

**Spring DI — how @Autowired works internally:**
```java
// Simplified Spring bean instantiation:
public class SpringContainer {
    private Map<Class<?>, Object> beans = new HashMap<>();
    
    public <T> T createBean(Class<T> clazz) throws Exception {
        // 1. Create instance (uses reflection)
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        T instance = constructor.newInstance();
        
        // 2. Inject dependencies (uses reflection on fields)
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                Class<?> dependencyType = field.getType();
                Object dependency = beans.get(dependencyType);
                field.set(instance, dependency);  // Inject!
            }
        }
        
        // 3. Call lifecycle methods
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.setAccessible(true);
                method.invoke(instance);
            }
        }
        
        return instance;
    }
}
```

### 8. Advanced Details

**Java 17+ — Reflection and module system:**
```
⌠ Module A (java.base)                ⌉
│  ┌────────────────────────────────┐ │
│  │ String.class (public API)      │ │ → Accessible without --add-opens
│  │ String.value (private field)   │ │ → NOT accessible! 
│  └────────────────────────────────┘ │
⌡                                      ⌡
   If Module B wants to reflectively access String.value:
   --add-opens java.base/java.lang=com.myapp
   
   Without this flag (Java 17+ default):
   String.value is ENCAPSULATED — cannot access via reflection
```

**Reflection alternatives (Java version comparison):**
```
Java 7-: Reflection only
Java 7+: MethodHandle (faster, JIT-inlinable)
Java 15+: Hidden Classes (for frameworks)
Java 16+: Records — auto-generated components
Java 17+: Sealed classes — restrict subclassing
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What is Java reflection used for?

**A**: Reflection lets code inspect and modify classes at runtime — accessing methods, fields, and constructors dynamically. It's used by Spring for dependency injection (@Autowired), Hibernate for ORM (accessing entity fields), Jackson for JSON serialization (reading getters), and JUnit for test discovery. Without reflection, these frameworks couldn't work — they process classes they don't know about at compile time.

#### Intermediate

**Q**: Why is reflection slow? How do you mitigate it?

**A**: Reflection is slower because: (1) JIT cannot inline reflective calls; (2) Security checks run on every invocation; (3) Boxing/unboxing for primitives; (4) Variable argument arrays are created. Mitigations: (1) Cache Method/Field objects (not class name + method name lookups); (2) Call setAccessible(true) to skip access checks; (3) Use MethodHandles (Java 7+) which JIT can inline; (4) For Spring/DI, runtime-generated subclasses (CGLIB) are faster than pure reflection.

#### Senior

**Q**: How does Java 17's module system affect framework code that uses reflection?

**A**: Java 9+ modules enforce strong encapsulation by default. Internal JDK APIs (like `sun.misc.Unsafe`, `java.lang.reflect.AccessibleObject`) are not accessible reflectively. Framework code must: (1) Use exported APIs; (2) Specify `--add-opens` flags for deep reflection; (3) Or use the `jdk.internal.reflect.ReflectionFactory` for internal access. Spring Boot 3.x (Java 17+) explicitly documents which `--add-opens` flags are needed. This breaks older libraries that relied on internal API access — they must be updated for Java 17+.

#### Tricky

**Q**: Can you change a final field using reflection? What are the risks?

**A**: Yes, field.setAccessible(true) + field.set(obj, newValue) works even for final fields in most JVMs. But it's dangerous: (1) If the field is inlined by the compiler (static final primitives/Strings), changes may not be visible; (2) SecurityManager may block setAccessible; (3) Java 17+ module system restricts it; (4) Changes to final fields in already-used objects may cause unpredictable behavior — the JVM assumes final fields never change for optimizations. Use with extreme caution, typically only in frameworks/serialization.

### 10. Final 30-Second Answer

Reflection inspects and modifies classes at runtime — critical for frameworks like Spring, Hibernate, Jackson. It's slow (~100x slower than direct calls), so cache Method/Field objects. Java 9+ modules restrict internal API access. Use reflection for framework code, avoid in business logic.