# String Internals ⭐⭐⭐⭐⭐

String is one of the **highest ROI Java interview topics** for senior developers.

It connects with:

* JVM memory
* Heap
* String Constant Pool
* Immutability
* HashMap keys
* Performance optimization
* Security

Common interview questions:

* Why is String immutable?
* Difference between `"Java"` and `new String("Java")`
* What is String pool?
* What does `intern()` do?
* Where are String objects stored?
* Why String is final?
* What changed in Java 9 String implementation?

---

# 1. String Is a Class ⭐⭐⭐

Many beginners think String is a primitive.

It is not.

```java
String name = "Java";
```

String is:

```java
java.lang.String
```

A normal Java class.

---

Example:

```java
String name = new String("Java");
```

This creates an object.

---

# 2. Why String Is Special in Java? ⭐⭐⭐⭐⭐

String is used everywhere:

```java
String username;
String url;
String sql;
String json;
String token;
```

Because of heavy usage, JVM gives special optimization:

## String Constant Pool

---

# 3. String Constant Pool ⭐⭐⭐⭐⭐

## What is it?

A special memory area inside heap where JVM stores unique string literals.

Example:

```java
String s1 = "Java";

String s2 = "Java";
```

JVM does NOT create two objects.

Memory:

```
Heap

String Constant Pool


       "Java"
          ^
         / \
        /   \
      s1     s2

```

Both references point to the same object.

---

# Why does JVM do this?

Because strings are immutable.

If String was mutable:

```java
s1 changes "Java" → "Python"
```

then:

```
s2

would also become Python
```

That would be dangerous.

---

# 4. String Literal Creation ⭐⭐⭐⭐⭐

Example:

```java
String a = "Hello";
```

Flow:

```
JVM checks String Pool

       |
       |
Does "Hello" exist?

       |
   -------------
   |           |
 Yes          No

Reuse       Create new object

```

---

Example:

```java
String a = "Hello";

String b = "Hello";
```

Comparison:

```java
System.out.println(a == b);
```

Output:

```
true
```

Because:

```
a
 \
  \
  "Hello"
  /
 /
b
```

Same reference.

---

# 5. new String() Creation ⭐⭐⭐⭐⭐

Now:

```java
String a = new String("Hello");
```

This behaves differently.

Two possible objects are involved.

---

Memory:

```
Heap


Object created by new

       "Hello"
          ^
          |
          a


String Pool


       "Hello"

```

The literal `"Hello"` goes into pool.

`new String()` creates another object in heap.

---

Example:

```java
String a = new String("Java");

String b = new String("Java");


System.out.println(a == b);
```

Output:

```
false
```

Because:

```
a ----> Heap Object 1

b ----> Heap Object 2

```

---

But:

```java
System.out.println(a.equals(b));
```

Output:

```
true
```

Because content is same.

---

# 6. String Pool and intern() ⭐⭐⭐⭐⭐

`intern()` moves/reuses a String from the pool.

Example:

```java
String a = new String("Java");

String b = a.intern();

String c = "Java";
```

Memory:

```
Heap

a
|
v
"Java" object


String Pool

b,c
 |
 v

"Java"

```

Comparison:

```java
b == c
```

Result:

```
true
```

Because both point to pool object.

---

# 7. intern() Internal Behaviour ⭐⭐⭐⭐

When calling:

```java
a.intern();
```

JVM checks:

```
Is this string already in pool?

        |
   ---------------
   |             |
 Yes            No

return          add string
existing        to pool

```

---

Example:

```java
String x = new String("Hello");

String y = x.intern();

String z = "Hello";


System.out.println(y == z);
```

Output:

```
true
```

---

# 8. String Immutability Internals ⭐⭐⭐⭐⭐

String cannot be changed.

Example:

```java
String s = "Java";

s.concat("17");
```

Many think:

```
s = Java17
```

Wrong.

Actual:

```
Heap


"Java"

"Java17"


s
|
v
"Java"

```

Unless:

```java
s = s.concat("17");
```

Now:

```
s
|
v
"Java17"

```

---

# 9. Why String is Immutable? ⭐⭐⭐⭐⭐

Very common interview question.

---

## Reason 1: Security

Strings are used for:

* File paths
* URLs
* Database connections
* Class names

Example:

```java
String file="/admin/config";
```

If mutable:

```
/admin/config

could become

/user/config

```

Security issue.

---

## Reason 2: String Pool

Pool sharing requires immutability.

Example:

```
"Java"

 ^
 |
s1,s2,s3

```

Any modification would affect everyone.

---

## Reason 3: HashMap Keys

Example:

```java
Map<String,Integer> map;
```

HashMap depends on:

```
hashCode()
```

If String changed:

```
hashCode changes
```

HashMap breaks.

---

## Reason 4: Thread Safety

Immutable objects are naturally thread-safe.

Multiple threads can share:

```java
String token;
```

without locks.

---

# 10. String hashCode Caching ⭐⭐⭐⭐

String stores calculated hash value.

Example:

```java
String s="Java";
```

First call:

```java
s.hashCode();
```

JVM calculates.

Next calls reuse cached value.

Why possible?

Because String never changes.

---

# 11. StringBuilder vs StringBuffer ⭐⭐⭐⭐⭐

Because String is immutable:

```java
String result="";

result=result+"A";

result=result+"B";

```

Creates many objects.

Example:

```
"A"

"AB"

"ABC"

```

Wasteful.

---

Solution:

## StringBuilder

Mutable.

```java
StringBuilder sb =
new StringBuilder();

sb.append("Java");

sb.append("17");
```

Only one object.

---

## StringBuffer

Same as StringBuilder but synchronized.

---

Comparison:

|             | String           | StringBuilder         | StringBuffer         |
| ----------- | ---------------- | --------------------- | -------------------- |
| Mutable     | No               | Yes                   | Yes                  |
| Thread safe | Yes              | No                    | Yes                  |
| Performance | Slow for changes | Fast                  | Slower               |
| Use case    | Fixed text       | Single thread changes | Multi-thread changes |

---

# 12. Java 8 vs Java 9 String Internal Change ⭐⭐⭐⭐

Before Java 9:

String internally used:

```java
char[]
```

Example:

```
String

value

char[]
```

Each character:

```
16 bits
```

---

Problem:

Most strings use:

* English
* Latin characters

which require only 8 bits.

Memory waste.

---

Java 9 introduced:

## Compact Strings

String internally uses:

```java
byte[]
```

plus encoding flag.

Example:

```
String

byte[]

coder
```

Two encodings:

```
LATIN1

UTF16
```

Benefits:

* Less memory usage
* Better cache utilization

---

# 13. String Concatenation Internals ⭐⭐⭐⭐⭐

Example:

```java
String result =
"Java" + "17";
```

Compiler optimizes:

```java
"Java17"
```

---

Runtime concatenation:

```java
String result =
name + age;
```

Compiler internally uses:

Java 8:

```java
StringBuilder
```

Example:

```java
new StringBuilder()
.append(name)
.append(age)
.toString();
```

---

Java 9:

Uses:

```java
StringConcatFactory
```

through invokedynamic.

---

# 14. String Comparison Interview Traps ⭐⭐⭐⭐⭐

## Case 1

```java
String a="Java";

String b="Java";


System.out.println(a==b);
```

Answer:

```
true
```

---

## Case 2

```java
String a=new String("Java");

String b=new String("Java");


System.out.println(a==b);
```

Answer:

```
false
```

---

## Case 3

```java
String a=new String("Java");

String b="Java";


System.out.println(a==b);
```

Answer:

```
false
```

---

## Case 4

```java
String a=new String("Java");

String b=a.intern();


System.out.println(b=="Java");
```

Answer:

```
true
```

---

# 15. Production Usage ⭐⭐⭐⭐⭐

## Configuration keys

```java
String ENV="PROD";
```

---

## API tokens

Immutable to avoid accidental changes.

---

## Cache keys

Example:

```java
"user:101"
```

---

## DTO fields

Most DTO string fields are immutable.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Difference between String literal and new String?

Literal:

```java
"Java"
```

uses String pool.

new:

```java
new String("Java")
```

creates new heap object.

---

## Q2. Why String is immutable?

Because of:

* Security
* String pool
* HashMap keys
* Thread safety

---

## Q3. Where is String Pool located?

Modern JVM:

Inside heap memory.

(Java 7 moved it from PermGen to heap.)

---

## Q4. What does intern() do?

Returns canonical pooled representation of String.

---

## Q5. Why StringBuilder is faster?

Because it modifies existing buffer instead of creating new String objects.

---

## Q6. Why String hashCode is cached?

Because String is immutable, hash never changes.

---

# Interview Boundary

For 7+ years Java developer:

Must know deeply:

✅ String pool
✅ Heap vs pool
✅ literal vs new String
✅ intern()
✅ immutability reason
✅ StringBuilder/StringBuffer
✅ Java 9 compact strings
✅ hashCode caching

Deep dive not required:

* Exact StringTable JVM implementation
* String deduplication GC feature

---

Next topic:

# Wrapper Classes ⭐⭐⭐⭐

We will cover:

* Primitive vs Wrapper
* Autoboxing/unboxing
* Integer cache
* == vs equals() with wrappers
* Null handling pitfalls
* Why collections need wrappers
* Performance considerations
