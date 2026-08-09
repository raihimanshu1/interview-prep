# Wrapper Classes ⭐⭐⭐⭐

Wrapper classes are a smaller topic compared to String, but very frequently asked in Java interviews because they connect with:

* Collections
* Autoboxing/unboxing
* Object vs primitive behaviour
* Integer caching
* Null handling
* Performance issues

For a **7+ years Java developer**, you should understand the internals and common traps.

---

# 1. Primitive Types vs Wrapper Classes ⭐⭐⭐⭐⭐

Java has 8 primitive data types:

| Primitive | Wrapper   |
| --------- | --------- |
| byte      | Byte      |
| short     | Short     |
| int       | Integer   |
| long      | Long      |
| float     | Float     |
| double    | Double    |
| char      | Character |
| boolean   | Boolean   |

---

Primitive:

```java
int age = 30;
```

Stores actual value.

Memory:

```
Stack

age
 |
 30
```

---

Wrapper:

```java
Integer age = 30;
```

Stores object reference.

Memory:

```
Stack

age
 |
 v

Heap

Integer Object
value = 30
```

---

# 2. Why Do We Need Wrapper Classes? ⭐⭐⭐⭐⭐

Main reason:

## Collections work with Objects

Example:

```java
List<int> numbers;
```

Not allowed.

Java collections require objects:

```java
List<Integer> numbers = new ArrayList<>();
```

Because internally:

```java
List<E>
```

works with reference types.

---

Other reasons:

## 1. Utility methods

Example:

```java
Integer.parseInt("123");
```

returns:

```java
int
```

---

## 2. Null support

Primitive:

```java
int age = null;
```

Impossible.

Wrapper:

```java
Integer age = null;
```

Allowed.

Useful in:

* Database columns
* JSON
* APIs

Example:

Database:

```
age column

NULL
```

maps naturally to:

```java
Integer age;
```

---

# 3. Autoboxing ⭐⭐⭐⭐⭐

Autoboxing:

> Automatic conversion from primitive to wrapper object.

Example:

```java
int number = 10;

Integer obj = number;
```

Compiler converts:

```java
Integer obj =
Integer.valueOf(number);
```

Internally:

```java
Integer obj = Integer.valueOf(10);
```

---

# 4. Unboxing ⭐⭐⭐⭐⭐

Unboxing:

> Automatic conversion from wrapper object to primitive.

Example:

```java
Integer obj = 10;

int number = obj;
```

Compiler converts:

```java
int number =
obj.intValue();
```

---

# 5. Autoboxing in Collections ⭐⭐⭐⭐⭐

Example:

```java
List<Integer> list = new ArrayList<>();

list.add(10);
```

Looks like:

```java
list.add(Integer.valueOf(10));
```

---

Retrieval:

```java
int value = list.get(0);
```

Internally:

```java
int value =
list.get(0).intValue();
```

---

# 6. Integer Cache ⭐⭐⭐⭐⭐

This is a very common interview trap.

Example:

```java
Integer a = 100;

Integer b = 100;


System.out.println(a == b);
```

Output:

```
true
```

Why?

Because Java caches Integer objects.

---

Integer cache range:

```
-128 to 127
```

For these values JVM reuses existing objects.

Memory:

```
Integer Cache


100 Object

 ^
 |
+--- a
|
+--- b
```

---

# 7. Why Integer Cache Exists?

Because small integers are used very frequently.

Example:

```java
age = 25
status = 1
counter = 0
```

Creating millions of Integer objects wastes memory.

So JVM maintains cache.

---

# 8. Integer Cache Example Beyond Range ⭐⭐⭐⭐⭐

Example:

```java
Integer a = 1000;

Integer b = 1000;


System.out.println(a == b);
```

Output:

```
false
```

Why?

Because:

```
1000 not cached
```

Objects:

```
Heap


Integer(1000)

   ^
   |
   a


Integer(1000)

   ^
   |
   b
```

Different references.

---

Correct comparison:

```java
a.equals(b);
```

Output:

```
true
```

---

# 9. Wrapper Comparison Trap ⭐⭐⭐⭐⭐

Example:

```java
Integer x = 10;

int y = 10;


System.out.println(x == y);
```

Output:

```
true
```

Why?

Because one side is primitive.

Unboxing happens:

```java
x.intValue() == y
```

Comparison becomes:

```java
10 == 10
```

---

But:

```java
Integer x = 10;

Integer y = 10;


System.out.println(x == y);
```

Depends on cache.

---

# 10. Integer.valueOf() vs new Integer() ⭐⭐⭐⭐

Old:

```java
Integer a =
new Integer(100);
```

Creates new object.

---

Preferred:

```java
Integer a =
Integer.valueOf(100);
```

Uses cache.

---

Example:

```java
Integer a = new Integer(100);
Integer b = new Integer(100);


a == b
```

Result:

```
false
```

---

# 11. Null Pointer Trap ⭐⭐⭐⭐⭐

Very important.

Example:

```java
Integer count = null;

int total = count;
```

Compiler converts:

```java
count.intValue();
```

But:

```
count = null
```

So:

```
NullPointerException
```

---

Example in production:

```java
Integer quantity =
order.getQuantity();


if(quantity > 0){

}
```

Danger.

Because:

```java
quantity > 0
```

causes unboxing.

Equivalent:

```java
quantity.intValue() > 0
```

---

Safe:

```java
if(quantity != null && quantity > 0){

}
```

---

# 12. Wrapper Classes Are Immutable ⭐⭐⭐⭐⭐

All wrapper classes are immutable.

Example:

```java
Integer x = 10;

x++;
```

Looks like modification.

Actually:

```java
x = Integer.valueOf(x + 1);
```

New object created.

---

Memory:

Before:

```
x
|
10
```

After:

```
x
|
11
```

Original Integer unchanged.

---

# 13. Why Wrapper Classes Are Immutable?

Same reasons as String:

* Thread safety
* Safe caching
* HashMap keys
* Predictable behaviour

---

Example:

```java
Map<Integer,String> map;
```

Integer can safely be a key.

---

# 14. Primitive vs Wrapper Performance ⭐⭐⭐⭐

Primitive:

```java
int count;
```

Advantages:

* Less memory
* Faster
* No object creation

---

Wrapper:

```java
Integer count;
```

Costs:

* Object allocation
* Memory overhead
* Boxing/unboxing cost

---

Example:

Bad:

```java
Long sum = 0L;

for(long i=0;i<1000000;i++){

    sum += i;

}
```

Each iteration:

```
unbox Long
add
box new Long
```

---

Better:

```java
long sum = 0;

for(long i=0;i<1000000;i++){

    sum += i;

}
```

---

# 15. Boolean Cache ⭐⭐⭐⭐

Boolean has only two possible values:

```java
Boolean.TRUE

Boolean.FALSE
```

Example:

```java
Boolean a = true;

Boolean b = true;


a == b
```

Usually:

```
true
```

because JVM reuses instances.

---

# 16. Character Cache ⭐⭐⭐

Character cache:

```
0 - 127
```

Example:

```java
Character a='A';

Character b='A';

a == b
```

true.

---

# 17. Wrapper Classes in Streams and Generics ⭐⭐⭐⭐

Primitive collections do not exist:

```java
List<int>
```

Not possible.

So:

```java
List<Integer>
```

creates boxing overhead.

For performance-sensitive cases:

Java provides primitive streams:

```java
IntStream
LongStream
DoubleStream
```

Example:

```java
IntStream.range(1,100)
.sum();
```

No Integer objects created.

---

# 18. Real Production Usage ⭐⭐⭐⭐⭐

## DTO fields

Example:

```java
class UserDTO {

    private Integer age;

}
```

Why not int?

Because:

```
missing value = null
```

---

## Database entities

Example:

```java
@Column
private Long id;
```

Because before persistence:

```
id = null
```

After save:

```
id = generated value
```

---

## Counters

Use primitive:

```java
long requestCount;
```

because it is performance sensitive.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Why collections don't support primitives?

Because generics work only with objects.

---

## Q2. Difference between Integer == and equals()?

`==`

* compares references

`equals()`

* compares values

---

## Q3. Why Integer 100 == Integer 100 is true?

Because Integer cache (-128 to 127).

---

## Q4. Why Integer 1000 == Integer 1000 is false?

Different objects created.

---

## Q5. Is Integer immutable?

Yes.

---

## Q6. What happens during autoboxing?

Compiler calls:

```java
Integer.valueOf()
```

---

## Q7. What happens if wrapper object is null and unboxed?

NullPointerException.

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ Primitive vs wrapper
✅ Autoboxing/unboxing
✅ Integer cache
✅ == vs equals()
✅ Null unboxing issue
✅ Immutable wrapper classes
✅ Performance difference
✅ Why collections need wrappers

Deep dive not required:

* JVM IntegerCache implementation source
* Custom wrapper caching

---

Next topic:

# Enum ⭐⭐⭐⭐

We will cover:

* How enum works internally
* Enum vs constants
* Enum constructors
* Enum with fields and methods
* Enum singleton pattern
* EnumMap / EnumSet
* Why enum is type-safe
* Interview questions
