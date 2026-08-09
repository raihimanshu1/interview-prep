# Chapter 7 — LinkedHashSet Deep Dive ⭐⭐⭐⭐☆

`LinkedHashSet` is the bridge between:

* **HashSet** → fast lookup
* **LinkedList** → ordering

It answers an important interview question:

> "How can a Set maintain insertion order if Set does not allow duplicates?"

The answer:

> LinkedHashSet internally uses a LinkedHashMap.

---

# 1. Why Do We Need LinkedHashSet?

HashSet problem:

```java
Set<String> set = new HashSet<>();

set.add("Java");
set.add("Spring");
set.add("Kafka");

System.out.println(set);
```

Output:

```
Kafka
Java
Spring
```

Order is unpredictable.

HashSet only guarantees:

```
No duplicates
```

It does NOT guarantee:

```
Insertion order
```

---

But sometimes we need:

Input:

```
Java
Spring
Kafka
Java
```

Output:

```
Java
Spring
Kafka
```

Requirements:

* No duplicates
* Maintain insertion order

Solution:

```
LinkedHashSet
```

---

# 2. LinkedHashSet Definition

`LinkedHashSet` is:

> A hash table + doubly linked list implementation of Set that maintains insertion order.

It provides:

* Unique elements
* Predictable iteration order
* O(1) average lookup

---

# 3. LinkedHashSet Internal Structure ⭐⭐⭐⭐⭐

Most important interview point:

## LinkedHashSet internally uses LinkedHashMap

Similar to:

```java
HashSet

        |
        |
        v

HashMap
```

But:

```java
LinkedHashSet

        |
        |
        v

LinkedHashMap
```

---

Diagram:

```
LinkedHashSet


        |
        |
        v


LinkedHashMap


+-----------------------------+
| Key        Value            |
+-----------------------------+
| Java       PRESENT          |
| Spring     PRESENT          |
| Kafka      PRESENT          |
+-----------------------------+


             +
             |
             v


Doubly Linked List


Java <----> Spring <----> Kafka

```

---

# 4. Class Structure

Simplified:

```java
public class LinkedHashSet<E>
extends HashSet<E>
implements Set<E>
{

}
```

Interesting:

It extends `HashSet`.

But internally HashSet constructor can create different map implementation.

---

HashSet normally:

```java
HashMap
```

LinkedHashSet:

```java
LinkedHashMap
```

---

# 5. HashSet vs LinkedHashSet Internal Difference ⭐⭐⭐⭐⭐

## HashSet

Storage:

```
HashMap


Bucket Array


0
1
2
3


```

Only bucket relationships exist.

---

## LinkedHashSet

Storage:

```
LinkedHashMap


Bucket Array


+
+
Doubly linked list


A <----> B <----> C

```

---

# 6. add() Operation Flow

Example:

```java
set.add("Java");
```

with LinkedHashSet.

Flow:

```
add("Java")


       |
       v


LinkedHashSet


       |
       v


LinkedHashMap.put(
    "Java",
    PRESENT
)


       |
       v


Calculate hashCode()


       |
       v


Find bucket


       |
       v


Check equals()


       |
       v


Insert node


       |
       v


Connect linked list pointers

```

---

# 7. Internal Node Structure

HashSet uses:

```
HashMap.Node
```

Structure:

```
+-------+-------+
| hash  | key   |
+-------+-------+
| value | next  |
+-------+-------+

```

---

LinkedHashSet uses:

```
LinkedHashMap.Entry
```

Structure:

```
+--------------------------------+
| hash                            |
| key                             |
| value                           |
| next                            |
| before                          |
| after                           |
+--------------------------------+

```

Extra:

```
before
after
```

maintain insertion order.

---

# 8. Example Flow

Insert:

```java
set.add("Java");
set.add("Spring");
set.add("Kafka");
```

Hash buckets:

```
Bucket Array


0
|
1 ---> Java

2 ---> Kafka

3 ---> Spring

```

Bucket order is random.

---

Linked list:

```
head

 |
 v

Java <----> Spring <----> Kafka

```

Iteration follows:

```
Linked List order

```

not bucket order.

---

# 9. Why Does LinkedHashSet Maintain Order?

Because every entry contains:

```
before
after
```

references.

Example:

Insert:

```
A
B
C
```

Internally:

```
null

 |
 v

A

 |
 v

B

 |
 v

C

 |
 v

null

```

Actually:

```
null <- A <-> B <-> C -> null

```

---

# 10. Complexity Analysis ⭐⭐⭐⭐⭐

Because it uses HashMap internally:

| Operation  | Average | Worst    |
| ---------- | ------- | -------- |
| add()      | O(1)    | O(log n) |
| remove()   | O(1)    | O(log n) |
| contains() | O(1)    | O(log n) |

Same as HashSet.

---

But:

Iteration performance:

HashSet:

```
Depends on bucket traversal
```

LinkedHashSet:

```
Traverse linked list directly
```

---

# 11. Memory Difference

HashSet:

```
Node


hash
key
value
next

```

---

LinkedHashSet:

```
Entry


hash
key
value
next

before
after

```

Extra:

```
before
after

```

Therefore:

LinkedHashSet uses more memory.

---

# 12. HashSet vs LinkedHashSet ⭐⭐⭐⭐⭐

| Feature            | HashSet          | LinkedHashSet   |
| ------------------ | ---------------- | --------------- |
| Duplicate allowed  | No               | No              |
| Ordering           | No guarantee     | Insertion order |
| Internal structure | HashMap          | LinkedHashMap   |
| Lookup             | O(1) average     | O(1) average    |
| Memory             | Lower            | Higher          |
| Iteration          | Faster sometimes | Predictable     |

---

# 13. LinkedHashSet vs TreeSet

Very common interview comparison.

## LinkedHashSet

Maintains:

```
Insertion order
```

Example:

Input:

```
30
10
20
```

Output:

```
30
10
20
```

---

## TreeSet

Maintains:

```
Sorted order
```

Output:

```
10
20
30
```

---

Comparison:

| Feature     | LinkedHashSet        | TreeSet        |
| ----------- | -------------------- | -------------- |
| Internal    | LinkedHashMap        | Red Black Tree |
| Ordering    | Insertion            | Sorted         |
| Complexity  | O(1)                 | O(log n)       |
| Allows null | Yes                  | Usually no     |
| Use case    | Preserve input order | Sorted data    |

---

# 14. Null Handling

LinkedHashSet allows:

```java
set.add(null);
```

Why?

Because LinkedHashMap allows one null key.

Example:

```java
Set<String> set =
        new LinkedHashSet<>();

set.add(null);
set.add(null);
```

Result:

```
null
```

Only once.

---

# 15. Duplicate Detection

Same as HashSet.

Example:

```java
set.add(new Employee(1));
```

Flow:

```
hashCode()

    |

bucket

    |

equals()

    |

duplicate?

```

---

# 16. Mutable Object Problem

Same danger exists.

Example:

```java
class User {

 int id;

}
```

Add:

```java
User user =
    new User(10);

set.add(user);

```

Hash calculated:

```
hash = 100

bucket = 5

```

Modify:

```java
user.id = 20;
```

Now:

```
new hash = 500

bucket = 8

```

Search fails.

---

Rule:

Keys inside LinkedHashSet should be immutable.

---

# 17. Thread Safety

LinkedHashSet is NOT thread-safe.

Example:

Thread A:

```java
set.add("A");
```

Thread B:

```java
set.add("B");
```

No synchronization.

---

Options:

```java
Collections.synchronizedSet(
    new LinkedHashSet<>()
);
```

or external locking.

---

# 18. Real Production Examples

## 1. Remove duplicates but preserve order

Input:

```
API response:

Java
Python
Java
Go
Python

```

Need:

```
Java
Python
Go

```

Use:

```java
LinkedHashSet
```

---

## 2. Recently processed IDs

Example:

```
Request IDs

123
456
123
789

```

Need unique IDs while preserving arrival order.

---

## 3. Ordered permissions

Input:

```
READ
WRITE
DELETE
READ

```

Output:

```
READ
WRITE
DELETE

```

---

# 19. Interview Questions ⭐⭐⭐⭐⭐

## Q1. How does LinkedHashSet maintain insertion order?

Answer:

> LinkedHashSet internally uses LinkedHashMap. LinkedHashMap maintains a doubly linked list among entries using before and after pointers, allowing predictable insertion-order iteration.

---

## Q2. Difference between HashSet and LinkedHashSet?

Answer:

> HashSet provides uniqueness without ordering. LinkedHashSet adds insertion-order maintenance by maintaining a linked list internally.

---

## Q3. Is LinkedHashSet slower than HashSet?

Answer:

> Lookup complexity is the same on average, O(1), but LinkedHashSet has additional memory overhead and pointer maintenance for maintaining order.

---

## Q4. Does LinkedHashSet sort elements?

Answer:

No.

Example:

```
Input:

5
1
3


Output:

5
1
3

```

For sorting use:

```
TreeSet
```

---

# 20. Final Mental Model

Remember:

```
LinkedHashSet


          Unique Elements

                |

                v


          LinkedHashMap


                |

       +----------------+

       |                |

 Hash Buckets     Doubly Linked List


                |

                v


     Fast Lookup + Ordered Iteration


```

---

# Chapter Complete ✅

Next Chapter:

# Chapter 8 — TreeSet Deep Dive ⭐⭐⭐⭐⭐

Topics:

* Why TreeSet exists
* Red-Black Tree internals
* Sorted order guarantee
* Comparator vs Comparable
* O(log n) complexity
* Rotations
* Balancing
* TreeSet vs HashSet vs LinkedHashSet
* Common interview problems and traps

This chapter directly connects with your HashMap complexity discussion because TreeSet is where **O(log n)** comes from.
