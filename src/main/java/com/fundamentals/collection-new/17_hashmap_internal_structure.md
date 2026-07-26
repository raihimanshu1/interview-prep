# Chapter 17 — HashMap Internal Structure Deep Dive ⭐⭐⭐⭐⭐

Now we move from **hashing theory** into the actual **HashMap implementation**.

This chapter is extremely important for senior Java interviews because interviewers often ask:

* What happens internally when you create a HashMap?
* Is the table created during object creation?
* What is Node<K,V>?
* What is load factor?
* When does resize happen?
* Why is resizing expensive?
* What happens during resize in Java 7 vs Java 8?
* Why does HashMap capacity double?

---

# 1. HashMap Internal Architecture Overview

At a high level:

```text
                    HashMap


                       |
                       |
                       v


              Node<K,V>[] table


                       |
        ---------------------------------

        |              |               |

     Bucket 0       Bucket 1        Bucket 2


                       |
                       |
                       v


                 Node chain


              Node -> Node -> Node


                       OR


                 Red Black Tree

```

---

The main storage is:

```java
Node<K,V>[] table;
```

An array of buckets.

---

# 2. HashMap Class Variables ⭐⭐⭐⭐⭐

Simplified Java 8 HashMap:

```java
public class HashMap<K,V> {


    transient Node<K,V>[] table;


    transient int size;


    int threshold;


    final float loadFactor;


}
```

Let's understand each.

---

# 3. table[]

```java
Node<K,V>[] table;
```

This is the actual bucket array.

Example:

```text
table


Index

0
|
v
null


1
|
v
Node


2
|
v
Node


3
|
v
null

```

Each index represents one bucket.

---

# 4. Node Structure ⭐⭐⭐⭐⭐

Each entry is stored as a Node.

Java 8:

```java
static class Node<K,V>
implements Map.Entry<K,V> {


    final int hash;


    final K key;


    V value;


    Node<K,V> next;

}
```

---

Visual:

```text

Bucket 5


+----------------+
| hash           |
| key            |
| value          |
| next --------- |------+
+----------------+      |
                        |
                        v

                 +----------------+
                 | hash           |
                 | key            |
                 | value          |
                 | next           |
                 +----------------+

```

---

Each Node contains:

### 1. hash

Already calculated hash value.

Why store it?

Because during lookup:

we do not need to recalculate repeatedly.

---

### 2. key

Actual key.

Example:

```java
"John"
```

---

### 3. value

Associated value.

Example:

```java
100
```

---

### 4. next

Reference to next node in collision chain.

---

# 5. Creating a HashMap

Example:

```java
Map<String,Integer> map =
        new HashMap<>();
```

Question:

Does Java immediately create the bucket array?

Answer:

**No.**

Important interview point.

---

Initially:

```text
HashMap object


table = null

size = 0

threshold = 0

```

---

Why?

Because most HashMaps are created but never used.

Lazy initialization saves memory.

---

# 6. First put() Operation ⭐⭐⭐⭐⭐

Example:

```java
map.put("Java",100);
```

Flow:

```text

put()


 |
 v


calculate hash


 |
 v


table exists?


 |
 |
 No


 |
 v


Create table


 |
 v


Calculate bucket index


 |
 v


Insert Node


```

---

Diagram:

Before:

```text

HashMap


table = null


```

---

After first put:

```text

table


0  null

1  null

2  Node("Java",100)

3  null


```

---

# 7. Default Capacity

HashMap default capacity:

```java
16
```

Meaning:

Initially:

```text

table length = 16


```

Indexes:

```text
0 - 15

```

---

Diagram:

```text

[0][1][2][3][4][5][6][7]
[8][9][10][11][12][13][14][15]


```

---

# 8. Why Capacity Is Always Power of 2? ⭐⭐⭐⭐⭐

HashMap capacities:

```
16

32

64

128

256

```

Not:

```
10

20

30

```

Reason:

Bucket calculation.

Formula:

```java
index = hash & (n-1);
```

where:

```
n = table length
```

---

Example:

Capacity:

```
16
```

Binary:

```
10000
```

n-1:

```
01111
```

This makes bucket calculation extremely efficient.

---

# 9. Load Factor ⭐⭐⭐⭐⭐

Load factor controls when resize happens.

Default:

```java
0.75
```

Meaning:

When 75% of capacity is filled:

resize.

---

Formula:

```
threshold = capacity * loadFactor
```

---

Example:

Capacity:

```
16
```

Load factor:

```
0.75
```

Threshold:

```
16 * 0.75

= 12
```

---

Meaning:

After inserting 12 elements:

```text
Resize will happen

```

---

# 10. Why Load Factor Is 0.75?

Important interview question.

Tradeoff between:

## Memory

Higher load factor:

Example:

```
0.9
```

Means:

More entries per bucket.

Less memory.

But:

More collisions.

---

## Performance

Lower load factor:

Example:

```
0.5
```

Means:

More empty buckets.

Less collision.

But:

More memory.

---

0.75 gives a good balance.

---

# 11. Resize Trigger ⭐⭐⭐⭐⭐

Example:

Capacity:

```
16
```

Threshold:

```
12
```

Insert:

```
13th element
```

Flow:

```text

put()


 |
 v


size > threshold?


 |
 v


resize()


 |
 v


capacity doubles


```

---

Before:

```text

Capacity = 16


[ ][ ][ ][ ][ ]

```

After:

```text

Capacity = 32


[ ][ ][ ][ ][ ][ ][ ][ ]


```

---

# 12. Why Does Capacity Double? ⭐⭐⭐⭐⭐

Question:

Why not increase by 10?

Because:

Power of two property must remain.

Example:

Current:

```
16
```

Next:

```
32
```

Both allow:

```java
hash & (n-1)
```

efficient calculation.

---

# 13. Resize Internal Process ⭐⭐⭐⭐⭐

Resize is expensive.

Because every existing entry may need a new bucket.

Example:

Before:

```text

Capacity = 4


Bucket 1

A


Bucket 2

B


Bucket 3

C


```

After resize:

```text

Capacity = 8


Bucket 5

A


Bucket 6

B


Bucket 3

C


```

Entries may move.

---

Flow:

```text

Old Table


      |
      |
      v


Create New Table


      |
      |
      v


Recalculate positions


      |
      |
      v


Move Nodes


```

---

# 14. Resize Complexity ⭐⭐⭐⭐⭐

Suppose:

HashMap contains:

```
N elements
```

Resize requires:

```
Move N elements
```

Therefore:

```
O(n)
```

---

But remember:

Resize does not happen every insertion.

Therefore:

Average put remains:

```
O(1)
```

This is called:

**Amortized Complexity**

---

# 15. Amortized Complexity Example

Imagine:

100 insertions.

Most:

```
O(1)
```

Few:

```
O(n)
```

during resize.

Average:

```
O(1)
```

---

Graphically:

```text

put()

O(1)
O(1)
O(1)
O(1)
O(n)  <-- resize
O(1)
O(1)
O(1)
O(n)  <-- resize


Average = O(1)


```

---

# 16. Java 7 Resize Problem ⭐⭐⭐⭐⭐

Old HashMap used:

```text
Head insertion
```

during transfer.

Example:

Before:

```text

A -> B -> C

```

During resize:

Java 7 reverses:

```text

C -> B -> A

```

---

In multithreaded environment:

Two threads resizing simultaneously:

can create:

```text

A -> B -> A -> B -> A...


```

Cycle.

Result:

Infinite loop during get().

---

This was one reason Java 8 improved resize logic.

---

# 17. Java 8 Resize Improvement ⭐⭐⭐⭐⭐

Java 8 avoids unnecessary recalculation.

Old:

```text
calculate completely new hash position
```

New:

Uses old capacity information.

---

Example:

Old capacity:

```
16
```

New capacity:

```
32
```

A node either stays:

```text
same index
```

or moves:

```text
old index + 16
```

---

Example:

Old:

```text
Bucket 5

```

After resize:

Possible:

```
Bucket 5

or

Bucket 21

```

because:

```
5 + 16 = 21
```

---

# 18. Java 8 Resize Logic Diagram ⭐⭐⭐⭐⭐

```text

Old Bucket


        |
        v


Check high bit


        |
        |
 -----------------

 |               |

Same index     Move


 |               |

5              21


```

---

This avoids recalculating every hash.

---

# 19. HashMap Memory Structure Complete

```text

HashMap Object


 |
 |
 v


Node[] table


 |
 |
 +----------------+

 |                |

Bucket 0       Bucket 1


                  |
                  v


             Node


              |
              v


             Node


              |
              v


        Red Black Tree
        (if required)


```

---

# 20. Interview Complexity Summary ⭐⭐⭐⭐⭐

| Operation    | Average | Worst       |
| ------------ | ------- | ----------- |
| get()        | O(1)    | O(n) Java 7 |
| put()        | O(1)    | O(n) Java 7 |
| get() Java 8 | O(1)    | O(log n)    |
| put() Java 8 | O(1)    | O(log n)    |

---

# 21. Common Interview Questions

## Q1. When is HashMap table created?

Answer:

> HashMap uses lazy initialization. The internal table is created only during the first put operation.

---

## Q2. What is load factor?

Answer:

> Load factor determines when HashMap should resize. Default value is 0.75, meaning resize occurs when the number of elements exceeds 75% of capacity.

---

## Q3. Why resize is expensive?

Answer:

> Because existing entries must be redistributed into the new bucket array, requiring O(n) work.

---

## Q4. Why capacity doubles?

Answer:

> To maintain power-of-two capacity, enabling efficient bucket calculation using bit masking.

---

## Q5. Is HashMap put always O(1)?

Answer:

> No. It is O(1) average. During collisions or resizing, complexity can increase. Java 8 improves collision worst case to O(log n) using Red-Black trees.

---

# Final Mental Model

```text

HashMap


Create

 |
 v

table[]


 |
 v

hash(key)


 |
 v

bucket index


 |
 v

Node insertion


 |
 v

Collision?


 |
 +----------------+

 No              Yes

 |                |

Done        LinkedList


                 |

          > 8 nodes

                 |

          Red Black Tree


                 |

            O(log n)


```

---

# Chapter Complete ✅

Next Chapter:

# Chapter 18 — HashMap put() Internal Execution Line by Line ⭐⭐⭐⭐⭐

We will go through the actual Java 8 `put()` flow:

* `putVal()`
* hash calculation
* bucket selection
* empty bucket insertion
* collision handling
* equals comparison
* replacing existing value
* tree insertion
* resize trigger

This is the chapter where you can confidently answer:

> "Explain HashMap.put() internally."
