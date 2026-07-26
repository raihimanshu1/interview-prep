Great. Now we move to a very important JVM interview topic:

# Chapter 11 - String Pool & Runtime Constant Pool ⭐⭐⭐⭐⭐

This topic looks simple, but for a **7+ years Java developer interview**, interviewers often use it to test:

* Heap knowledge
* Class loading understanding
* Immutability
* Object creation
* JVM memory areas
* `==` vs `.equals()`
* `intern()`

---

# 1. Why does Java have String Pool?

First understand the problem.

Strings are everywhere:

```java
String country = "India";
String name = "Himanshu";
String status = "ACTIVE";
```

In a typical application:

* Configuration keys
* JSON fields
* API paths
* Database queries
* Log messages

create millions of strings.

If JVM creates a new object every time:

```java
String s1 = "Java";
String s2 = "Java";
String s3 = "Java";
```

Memory:

```text
Heap

+-------------+
| "Java"      |
+-------------+

+-------------+
| "Java"      |
+-------------+

+-------------+
| "Java"      |
+-------------+
```

Wasteful.

---

Java solves this using:

> **String Pool (String Intern Pool)**

---

# 2. What is String Pool?

## Interview Definition

String Pool is a special area maintained by JVM where it stores unique String literals to reuse existing String objects instead of creating duplicates.

---

Example:

```java
String s1 = "Java";

String s2 = "Java";
```

Memory:

```text
             String Pool

          +-------------+
s1 ------>| "Java"      |
          |             |
s2 ------>|             |
          +-------------+
```

Only one object exists.

---

# 3. Where is String Pool stored?

This is a very common interview question.

## Before Java 7

String Pool was inside:

```text
PermGen
```

Memory:

```text
JVM

+----------------+
| Heap           |
+----------------+

+----------------+
| PermGen        |
| String Pool    |
+----------------+
```

---

## Java 7 onwards

String Pool moved to:

```text
Heap
```

Reason:

PermGen had fixed size.

Example:

```text
PermGen:

256 MB

↓

String literals keep increasing

↓

OutOfMemoryError
```

Moving it to Heap allowed:

* Dynamic resizing
* Better memory management

---

Current:

```text
JVM Memory


+----------------------+
| Heap                 |
|                      |
|  String Pool         |
|                      |
|  Objects              |
+----------------------+


+----------------------+
| Metaspace             |
| Class Metadata        |
+----------------------+
```

---

# 4. String Literal Creation

Example:

```java
String s1 = "hello";
```

What happens internally?

Step 1:

Compiler sees:

```java
"hello"
```

It stores it in:

```text
Class File Constant Pool
```

Remember:

This is not the same as String Pool.

We will differentiate later.

---

Step 2:

Class loading happens.

JVM checks:

```
Does "hello" already exist in String Pool?
```

If yes:

```text
return existing reference
```

If no:

```text
create String object
add to String Pool
```

---

Flow:

```text
Java Source

String s = "hello";


        |
        v


Class File

Constant Pool

"hello"


        |
        v


JVM Runtime


String Pool


"hello"
```

---

# 5. String Object Creation using new

Now compare:

```java
String s1 = "Java";

String s2 = new String("Java");
```

Many candidates get confused here.

---

## Case 1

```java
String s1 = "Java";
```

Creates:

```text
String Pool

+---------+
| Java    |
+---------+

s1
 |
 v
Java
```

Only one object.

---

## Case 2

```java
String s2 = new String("Java");
```

Creates:

Two things:

### Object 1

String literal:

```text
String Pool

+---------+
| Java    |
+---------+
```

### Object 2

Heap object:

```text
Heap

+---------+
| Java    |
+---------+
```

Reference:

```text
s2
 |
 v

Heap Object
```

---

Memory:

```text
             String Pool

              "Java"
                 ^
                 |
                


Heap

              "Java"
                 ^
                 |
                 s2
```

---

# 6. == vs equals()

Very common interview question.

---

## ==

Checks:

> Reference comparison

Example:

```java
String a = "Java";

String b = "Java";


System.out.println(a == b);
```

Output:

```
true
```

Because both point to same pool object.

---

Memory:

```text
a ----+
      |
      v

   "Java"

      ^
      |
b ----+
```

---

## new String Example

```java
String a = new String("Java");

String b = new String("Java");


System.out.println(a == b);
```

Output:

```
false
```

Because two different heap objects.

Memory:

```text
Heap


a ---> "Java"


b ---> "Java"
```

---

## equals()

Checks:

> Content comparison

Example:

```java
String a = new String("Java");

String b = new String("Java");


System.out.println(a.equals(b));
```

Output:

```
true
```

Because content is same.

---

# 7. Why is String Immutable?

Another favorite question.

Example:

```java
String s = "Java";

s.concat(" World");
```

Many think:

```
s becomes Java World
```

Wrong.

Strings cannot change.

Memory:

Before:

```text
String Pool

"Java"

s
|
v
```

After:

```text
String Pool

"Java"

"Java World"


s
|
v
"Java"
```

A new object is created.

---

# Why immutable?

## 1. String Pool Safety

Imagine strings were mutable.

Example:

```java
String a = "Java";

String b = "Java";
```

Both share same object.

If:

```java
a.change("Python");
```

Then:

```text
b
?
```

Should b become Python?

Impossible.

Immutability allows safe sharing.

---

## 2. Security

Strings are used for:

* File paths
* Database URLs
* Class names
* Network connections

Example:

```java
connect("admin/password")
```

If String could change after validation:

```text
Validated:

admin/password


Changed:

admin/hacked
```

Security issue.

---

## 3. HashCode Optimization

Strings are heavily used as HashMap keys.

Example:

```java
Map<String, User> users;
```

HashMap depends on:

```java
hashCode()
```

Because String never changes:

```java
hashCode()
```

can be cached.

---

# 8. String.intern() ⭐⭐⭐⭐⭐

Now advanced interview question.

What does:

```java
intern()
```

do?

---

Example:

```java
String s1 = new String("Java");

String s2 = s1.intern();
```

Initially:

```text
Heap

s1 ---> "Java"


String Pool

"Java"
```

`intern()` returns the pool reference.

After:

```text
s2
 |
 v

String Pool "Java"
```

---

Example:

```java
String a = new String("Java");

String b = a.intern();

String c = "Java";


System.out.println(b == c);
```

Output:

```
true
```

Because:

```text
b
 |
 v

String Pool


c
 |
 v

String Pool
```

---

# 9. String Pool vs Runtime Constant Pool

This is where many candidates confuse.

They are related but different.

---

# Class File Constant Pool

Inside `.class` file:

```text
.class file

Constant Pool

-------------
UTF8 "Java"

Method references

Class references

Field references

-------------
```

Created during compilation.

---

# Runtime Constant Pool

When class is loaded:

```text
.class file

        |
        v

JVM Memory

Runtime Constant Pool
```

It contains:

* Class metadata references
* Method references
* Numeric constants
* String literal references

Stored in:

```text
Method Area / Metaspace
```

---

# String Pool

Different thing.

Contains:

```text
Actual String objects
```

Example:

```text
Heap

String Pool

"Java"
"Spring"
"Oracle"
```

---

# Complete Picture

```text
                 Compile Time


Java Code

String s = "Java";


        |
        v


.class File

Constant Pool

"Java"


        |
        v


Class Loading


        |
        +----------------+
        |
        v


Runtime Constant Pool

(Metaspace)


        |
        |
        v


String Pool

(Heap)


"Java"

```

---

# Interview Questions

## Q1. Where are Strings stored in Java?

Answer:

String objects are stored in Heap. String literals are stored in the String Pool, which has been part of Heap since Java 7.

---

## Q2. Difference between String Pool and Runtime Constant Pool?

String Pool stores actual String objects.

Runtime Constant Pool stores symbolic references and constants required by JVM during runtime.

---

## Q3. Why is String immutable?

Because it provides:

* String pool optimization
* Security
* Thread safety
* HashCode caching

---

## Q4. Difference between:

```java
String a="Java";
```

and

```java
String a=new String("Java");
```

First uses String Pool.

Second creates a new Heap object.

---

# Interview Boundary

For 7+ years backend:

Must know:

✅ String Pool
✅ Heap location
✅ Literal vs new String
✅ == vs equals
✅ intern()
✅ Why immutable
✅ Runtime Constant Pool difference

Do not deep dive:

* StringTable implementation
* JVM source code
* Hash table resizing inside String pool

---

Next topic after this:

# Class Loading ⭐⭐⭐⭐⭐

Because String Pool naturally connects with:

```
Compilation
   |
.class file
   |
Class Loader
   |
Runtime Constant Pool
   |
Heap / String Pool
```

We will cover:

* ClassLoader hierarchy
* Bootstrap / Platform / Application ClassLoader
* Loading → Linking → Initialization
* Parent delegation model
* Class loading interview scenarios
* Static blocks execution order
* ClassNotFoundException vs NoClassDefFoundError
