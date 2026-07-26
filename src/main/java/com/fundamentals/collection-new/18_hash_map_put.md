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

# Chapter 18 — HashMap `put()` Internal Execution Line by Line ⭐⭐⭐⭐⭐

This is one of the **most frequently asked senior Java interview questions**.

Interviewer:

> "Explain what happens internally when you call `hashMap.put(key, value)`."

A beginner answer:

> "It stores the key and value."

A senior answer should explain:

* hash calculation
* bucket selection
* table initialization
* collision handling
* equals comparison
* node insertion
* treeification
* resize

Let's go step-by-step.

---

# 1. Simple Code

Example:

```java
Map<String, Integer> map = new HashMap<>();

map.put("Java", 100);
```

Question:

What happens internally?

---

# 2. Complete Put Flow

High-level flow:

```text
                 put(key,value)


                       |
                       v


              Calculate hash(key)


                       |
                       v


              Find bucket index


                       |
                       v


             Is table initialized?


                 /          \

               No            Yes

               |              |

        Create table      Continue


                       |
                       v


              Is bucket empty?


                 /          \

               Yes           No

               |              |

          Insert Node     Collision


                              |

                       Compare keys


                              |

                    Same key exists?


                       /             \

                     Yes              No

                     |                 |

              Replace value      Add new Node


                              |

                     Size > Threshold?


                              |

                           Resize

```

---

# 3. HashMap `put()` Method

Simplified Java 8 code:

```java
public V put(K key, V value) {

    return putVal(
        hash(key),
        key,
        value,
        false,
        true
    );

}
```

---

Notice:

Before calling `putVal()`:

HashMap calculates:

```java
hash(key)
```

---

# 4. Step 1 — Calculate Hash

Example:

```java
map.put("Java",100);
```

First:

```java
"Java".hashCode()
```

returns:

```
2301506
```

---

But Java does not directly use this.

It applies hash spreading:

```java
h ^ (h >>> 16)
```

---

Why?

To mix high bits and low bits.

Because bucket calculation uses lower bits.

---

Flow:

```text
"Java"


 |
 v


hashCode()

 |
 v


2301506


 |
 v


hash spreading


 |
 v


final hash


```

---

# 5. Step 2 — Calculate Bucket Index

HashMap table size:

```
16
```

Formula:

```java
index = hash & (n - 1);
```

where:

```
n = table.length
```

---

Example:

```
capacity = 16
```

Binary:

```
n-1

15

01111
```

Hash:

```
10110110
```

AND:

```
10110110
01111111
---------
00110110
```

Result:

```
bucket index
```

---

So:

```text
Java

 |
 v

Bucket 6

```

---

# 6. Step 3 — Check Table Initialization

Initially:

```java
HashMap map = new HashMap();
```

Internally:

```text
table = null
```

No array created yet.

---

During first put:

```java
map.put("Java",100);
```

HashMap calls:

```java
resize()
```

Not because of size.

But because table does not exist.

---

Creates:

```text
Node[16]
```

Diagram:

```text
table


0  []
1  []
2  []
3  []
4  []
5  []
6  []
7  []
...
15 []

```

---

# 7. Step 4 — Check Bucket

Suppose index:

```
6
```

HashMap checks:

```java
table[6]
```

Case 1:

Empty.

```text
Bucket 6

null

```

---

Action:

Create Node.

```java
Node node =
new Node(hash,key,value,null);
```

---

Result:

```text
Bucket 6


+-------------+
| Java | 100 |
+-------------+

```

---

Complexity:

```
O(1)
```

---

# 8. Case 2 — Collision Happens ⭐⭐⭐⭐⭐

Suppose:

```java
map.put("ABC",200);
```

generates same bucket.

Existing:

```text
Bucket 6


Java -> 100

```

New:

```text
ABC -> 200

```

Now collision.

---

Java checks:

```java
existing node?
```

Yes.

---

Now HashMap compares:

1. hash
2. equals()

---

# 9. Collision Decision Flow

```text
Bucket contains node


          |

          v


Compare hash


          |

          v


Same hash?


      /        \

    No          Yes

    |            |

Different    Compare equals()


               |

          Same key?


          /        \

        Yes         No

        |            |

 Replace       Add new node

 value


```

---

# 10. Same Key Case

Example:

```java
map.put("Java",100);

map.put("Java",200);
```

Second put:

Hash same.

equals:

```java
"Java".equals("Java")
```

true.

---

HashMap replaces:

Before:

```text
Java -> 100
```

After:

```text
Java -> 200
```

---

Important:

Size does NOT increase.

Before:

```
size = 1
```

After:

```
size = 1
```

---

# 11. Different Key Same Bucket

Example:

Imagine:

```
Java
ABC
XYZ
```

all produce bucket 5.

Structure:

```text
Bucket 5


Java
 |
 v
ABC
 |
 v
XYZ

```

---

New key:

```java
map.put("XYZ",300);
```

HashMap:

* finds bucket
* checks nodes
* calls equals()

If no match:

Adds new node.

---

# 12. Java 8 Collision Handling

Before Java 8:

Always linked list.

```text
Bucket


A

|

B

|

C

|

D

```

---

Java 8:

Initially:

```text
Linked List

A -> B -> C
```

After threshold:

```
8 nodes
```

convert:

```text
Red Black Tree


          D

       /     \

      B       F

     / \

    A   C

```

---

# 13. Tree Insertion Case

If bucket is already TreeNode:

HashMap calls:

```java
treeifyBin()
```

and inserts into:

```text
Red Black Tree
```

Complexity:

```
O(log n)
```

---

# 14. Step 5 — Update Size

After successful insertion:

```java
size++;
```

Example:

Before:

```
size = 10
```

After:

```
size = 11
```

---

# 15. Step 6 — Check Resize

HashMap maintains:

```java
threshold
```

Formula:

```
threshold =
capacity * loadFactor
```

Default:

```
16 * 0.75

=12
```

---

Condition:

```java
if(size > threshold)
    resize();
```

---

Example:

Before:

```
size = 12
```

Insert new element:

```
size = 13
```

Now:

```
13 > 12
```

Resize.

---

# 16. Complete Example Dry Run ⭐⭐⭐⭐⭐

Code:

```java
HashMap<String,Integer> map =
new HashMap<>();

map.put("A",1);

map.put("B",2);

map.put("A",3);
```

---

## First put

```java
put("A",1)
```

Flow:

```
hash("A")

 |
bucket 5

 |
empty

 |
insert

```

Map:

```text
Bucket 5

A -> 1

```

Size:

```
1
```

---

## Second put

```java
put("B",2)
```

Suppose:

```
bucket 5
```

Collision:

```text
Bucket 5


A -> 1

```

Compare:

```java
"A".equals("B")
```

false.

Add:

```text
Bucket 5


A -> 1
 |
 v
B -> 2

```

Size:

```
2
```

---

## Third put

```java
put("A",3)
```

Bucket:

```
5
```

Compare:

```java
"A".equals("A")
```

true.

Replace:

Before:

```text
A -> 1
```

After:

```text
A -> 3
```

Size:

```
2
```

---

# 17. Actual Internal Method Flow

Interviewers like this.

```text
HashMap.put()


        |

        v


putVal()


        |

        v


hash(key)


        |

        v


resize() if table null


        |

        v


calculate index


        |

        v


check bucket


        |

        v


newNode()


OR


collision handling


        |

        v


treeify if required


        |

        v


resize if threshold exceeded


```

---

# 18. Important Interview Questions ⭐⭐⭐⭐⭐

## Q1. Does HashMap create array during object creation?

Answer:

No.

HashMap uses lazy initialization. The internal table is created during the first insertion.

---

## Q2. What happens if two keys have same hashCode?

Answer:

They are stored in the same bucket. HashMap uses equals() to distinguish them.

---

## Q3. What happens if same key is inserted twice?

Answer:

The existing value is replaced, and size does not increase.

---

## Q4. Why does HashMap compare hash before equals?

Answer:

Because comparing integers is faster. equals() can be expensive.

---

## Q5. What happens if key is mutable?

Answer:

If key state changes after insertion, hashCode may change and HashMap may not find the object again.

---

# 19. Senior Interview Answer (2 minutes)

If interviewer asks:

> Explain HashMap put internally.

Answer:

> "When put() is called, HashMap first calculates the key hash and applies hash spreading. It calculates the bucket index using hash & (capacity-1). If the bucket is empty, it inserts a new Node. If a collision occurs, it compares hash and equals to determine whether the key already exists. If the key exists, it replaces the value; otherwise it adds a new node to the collision chain. In Java 8, heavily collided buckets are converted into Red-Black trees to improve worst-case lookup from O(n) to O(log n). After insertion, HashMap checks the load factor threshold and resizes if required."

---

# Chapter Complete ✅

Next Chapter:

# Chapter 19 — HashMap `get()` Internal Execution Deep Dive ⭐⭐⭐⭐⭐

Topics:

* How `get()` finds values
* Why get() is faster than put()
* hash comparison
* equals comparison order
* TreeNode lookup
* Worst-case lookup complexity
* Null key handling
* Real interview traps around get() complexity
