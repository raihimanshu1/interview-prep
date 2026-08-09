# Chapter 9 — EnumSet Deep Dive ⭐⭐⭐⭐☆

`EnumSet` is a specialized Set implementation designed specifically for **Enum values**.

It is less commonly asked than HashSet or TreeSet, but for senior Java interviews it is valuable because it tests understanding of:

* Specialized collections
* Memory optimization
* Bit manipulation
* Performance trade-offs
* Internal JVM representation

---

# 1. Why Do We Need EnumSet?

Let's first understand the problem.

Suppose we have permissions:

```java
enum Permission {

    READ,
    WRITE,
    DELETE,
    EXECUTE

}
```

Now we need to store multiple permissions.

Common approach:

```java
Set<Permission> permissions =
        new HashSet<>();

permissions.add(Permission.READ);
permissions.add(Permission.WRITE);
```

This works.

But Java provides a better collection:

```java
EnumSet<Permission> permissions =
        EnumSet.of(
            Permission.READ,
            Permission.WRITE
        );
```

---

Why is EnumSet better?

Because enums have fixed known values.

Java can optimize storage.

---

# 2. EnumSet Definition

EnumSet is:

> A high-performance Set implementation specifically for enum types.

Important:

It can store only:

```java
enum values
```

Example:

Allowed:

```java
EnumSet<Day>
```

Not allowed:

```java
EnumSet<String>
```

---

# 3. EnumSet Hierarchy

Set hierarchy:

```
                Set
                 |
        ----------------
        |              |
     HashSet        EnumSet
                       |
             ----------------
             |
      RegularEnumSet
             |
       JumboEnumSet
```

---

# 4. Internal Implementation ⭐⭐⭐⭐⭐

Most important interview point:

> EnumSet does not use HashMap internally.

Unlike:

```
HashSet
   |
   v
HashMap
```

EnumSet uses:

```
Bit Vector
```

---

Example:

Enum:

```java
enum Permission {

    READ,      //0
    WRITE,     //1
    DELETE,    //2
    EXECUTE    //3

}
```

Internally:

```
Bit Position


READ       0
WRITE      1
DELETE     2
EXECUTE    3


```

---

If we store:

```java
READ
DELETE
```

Representation:

```
Index:

3 2 1 0

0 1 0 1

```

Binary:

```
0101
```

---

# 5. Why Bit Representation?

Because enum values are fixed.

Example:

Only four permissions exist:

```
READ
WRITE
DELETE
EXECUTE
```

We don't need objects.

We only need:

```
Present?
Yes/No
```

One bit is enough.

---

Memory comparison:

## HashSet

Stores objects:

```
Node

hash
key
value
next
```

For every element.

---

## EnumSet

Stores:

```
long bits
```

Example:

```
00010101
```

Very compact.

---

# 6. EnumSet Creation

## of()

Example:

```java
EnumSet<Permission> permissions =
        EnumSet.of(
            Permission.READ,
            Permission.WRITE
        );
```

---

## allOf()

Creates set containing all enum values.

Example:

```java
EnumSet<Permission> permissions =
        EnumSet.allOf(Permission.class);
```

Result:

```
READ
WRITE
DELETE
EXECUTE
```

---

## noneOf()

Empty EnumSet.

```java
EnumSet<Permission> permissions =
        EnumSet.noneOf(Permission.class);
```

---

## complementOf()

Opposite values.

Example:

Existing:

```
READ
WRITE
```

Complement:

```
DELETE
EXECUTE
```

---

# 7. RegularEnumSet vs JumboEnumSet ⭐⭐⭐⭐⭐

Internally EnumSet has two implementations.

---

## 1. RegularEnumSet

Used when:

```
Number of enum constants <= 64
```

Example:

```java
enum Day {

MON,
TUE,
WED,
THU,
FRI,
SAT,
SUN

}
```

Only 7 values.

Uses:

```
single long
```

---

Representation:

```
long bits


00000000000010101

```

---

## 2. JumboEnumSet

Used when:

```
Enum constants > 64
```

Example:

```java
enum HugeEnum {

VALUE1,
VALUE2,
...
VALUE100

}
```

Uses:

```
long[]
```

because one long stores only 64 bits.

---

Diagram:

```
RegularEnumSet


long

[64 bits]


----------------


JumboEnumSet


long[]

[64][64][64][64]

```

---

# 8. add() Operation Internals ⭐⭐⭐⭐⭐

Example:

```java
permissions.add(Permission.READ);
```

Flow:

```
add(READ)


     |
     v


Find ordinal()


     |
     v


Enum position


     |
     v


Set corresponding bit


     |
     v


Done

```

---

Example:

Before:

```
0000
```

Add READ:

```
0001
```

Add DELETE:

```
0101
```

---

Very fast.

---

# 9. contains() Operation

Example:

```java
permissions.contains(
    Permission.READ
);
```

Flow:

```
contains(READ)


       |
       v


Find ordinal


       |
       v


Check bit position


       |
       v


return true/false

```

---

Complexity:

```
O(1)
```

Actually it is almost a CPU bit operation.

---

# 10. EnumSet Complexity ⭐⭐⭐⭐⭐

| Operation  | Complexity |
| ---------- | ---------- |
| add()      | O(1)       |
| remove()   | O(1)       |
| contains() | O(1)       |
| iterator   | O(n)       |

---

# 11. EnumSet vs HashSet ⭐⭐⭐⭐⭐

| Feature      | EnumSet                | HashSet      |
| ------------ | ---------------------- | ------------ |
| Element type | Enum only              | Any Object   |
| Internal     | Bit vector             | Hash table   |
| Ordering     | Enum declaration order | No guarantee |
| Performance  | Faster                 | Slower       |
| Memory       | Very low               | Higher       |
| Null         | Not allowed            | Allowed      |
| Thread safe  | No                     | No           |

---

# 12. Why EnumSet Is Faster Than HashSet?

HashSet lookup:

```
Object

 |
 v

hashCode()

 |
 v

bucket calculation

 |
 v

equals()

```

Multiple steps.

---

EnumSet:

```
Enum

 |
 v

ordinal()

 |
 v

bit check

```

Much simpler.

---

# 13. Enum Ordering

Important:

EnumSet iteration follows:

```
Enum declaration order
```

Example:

```java
enum Priority {

LOW,
MEDIUM,
HIGH

}
```

Add:

```java
HIGH
LOW
```

Iteration:

```
LOW
HIGH

```

Not insertion order.

---

Compare:

LinkedHashSet:

```
Insertion order
```

EnumSet:

```
Enum declaration order
```

---

# 14. Null Handling

EnumSet does not allow null.

Example:

```java
permissions.add(null);
```

Throws:

```
NullPointerException
```

Why?

Because null has no:

```
ordinal()
```

---

# 15. Thread Safety

EnumSet is not thread-safe.

Example:

Thread A:

```java
set.add(READ);
```

Thread B:

```java
set.remove(WRITE);
```

Need synchronization.

Options:

```java
Collections.synchronizedSet(enumSet);
```

or external locking.

---

# 16. Real Production Examples ⭐⭐⭐⭐⭐

## 1. User Permissions

Example:

```java
enum Permission {

READ,
WRITE,
DELETE,
ADMIN

}
```

Store:

```java
EnumSet<Permission> permissions;
```

Instead of:

```
List<String>
```

---

## 2. Days Available

Example:

```java
enum Day {

MONDAY,
TUESDAY,
WEDNESDAY

}
```

Store working days:

```java
EnumSet<Day> workingDays;
```

---

## 3. Feature Flags

Example:

```java
enum Feature {

NEW_UI,
PAYMENT_V2,
SEARCH_V2

}
```

Enabled features:

```java
EnumSet<Feature>
```

---

# 17. Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why is EnumSet faster than HashSet?

Answer:

> EnumSet uses a bit vector internally because enum values are fixed. Operations become simple bit operations instead of hashing and equality checks.

---

## Q2. Does EnumSet use HashMap internally?

Answer:

No.

HashSet uses HashMap.

EnumSet uses:

```
RegularEnumSet
or
JumboEnumSet
```

with bit representation.

---

## Q3. Can EnumSet contain null?

Answer:

No.

Because enum constants are identified using ordinal values, and null has no ordinal.

---

## Q4. Does EnumSet maintain insertion order?

Answer:

No.

It maintains enum declaration order.

---

## Q5. Difference between EnumSet and HashSet?

Answer:

> HashSet is general-purpose and supports any object. EnumSet is optimized specifically for enum values using bit-level representation, giving better performance and lower memory usage.

---

# 18. Complete Set Comparison

```
                    Set


                     |
 ------------------------------------------------
 |                    |                         |
HashSet          LinkedHashSet              TreeSet
 |                    |                         |
HashMap        LinkedHashMap               TreeMap

Fast            Ordered                  Sorted


                    


                    EnumSet

                     |

              Bit Representation

              Extremely Fast

```

---

# Final Mental Model

Remember:

```
EnumSet


Enum value

     |
     v

ordinal()

     |
     v

Bit position

     |
     v

0 / 1


```

The key idea:

> HashSet stores objects. EnumSet stores bits.

---

# Chapter Complete ✅

Next Chapter:

# Chapter 10 — Queue Framework Introduction + PriorityQueue Deep Dive ⭐⭐⭐⭐⭐

Topics:

* Queue vs Stack
* FIFO principle
* PriorityQueue internals
* Binary Heap
* Min Heap vs Max Heap
* Heapify
* add() / poll() complexity
* PriorityQueue vs TreeSet
* Top K problems
* Interview questions

This chapter connects directly with DSA heap problems and system design use cases like task scheduling.
