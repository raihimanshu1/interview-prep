# Chapter 3 — LinkedList Deep Dive ⭐⭐⭐⭐☆

`LinkedList` is one of the oldest and most misunderstood Java collections.

Many developers remember:

> "LinkedList is better for insertion and deletion."

This is only partially true.

A senior Java developer should explain:

* What internal data structure LinkedList uses
* Why `get()` is O(n)
* Why adding/removing at ends is O(1)
* Why middle insertion is not always faster than ArrayList
* Memory overhead
* Why LinkedList is rarely preferred in production
* How Java LinkedList implements both `List` and `Deque`

---

# 1. What Problem Does LinkedList Solve?

ArrayList problem:

```text
Insert in middle

A B C D E

Insert X at index 2


Need shifting:

A B _ C D E

```

Elements need to move.

LinkedList solves this by storing elements as connected nodes.

---

# 2. LinkedList Internal Structure ⭐⭐⭐⭐⭐

Java LinkedList is a:

> Doubly Linked List

Each element is stored inside a Node.

Diagram:

```text
Node


+---------+---------+---------+
|  prev   |  item   |  next   |
+---------+---------+---------+

```

Each node knows:

* Previous node
* Data
* Next node

---

Complete structure:

```text
        LinkedList Object


+----------------+
| first          |
| last           |
| size           |
+----------------+

        |
        |
        v


null

  |
  v

+------+-------+------+
|prev  |  A    |next |
+------+-------+------+
                 |
                 v

+------+-------+------+
|prev  |  B    |next |
+------+-------+------+
                 |
                 v

+------+-------+------+
|prev  |  C    |next |
+------+-------+------+

  ^
  |
null

```

---

# 3. LinkedList Class Structure

Simplified source:

```java
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>
{


    transient int size = 0;


    transient Node<E> first;


    transient Node<E> last;


}
```

Important fields:

---

## first

Reference to first node.

Example:

```text
first

 |

 v

[A]

```

---

## last

Reference to last node.

Example:

```text
last

 |

 v

[C]

```

---

## size

Number of nodes.

---

# 4. Node Class Internal

Java source:

```java
private static class Node<E> {

    E item;

    Node<E> next;

    Node<E> prev;


}
```

Example:

```text
Node A


prev = null

item = "Java"

next = Node B


```

---

# 5. Creating LinkedList

Example:

```java
List<String> list =
        new LinkedList<>();
```

Initially:

```text
first = null

last = null

size = 0

```

Memory:

```text
LinkedList Object


+--------+
| first  | ---> null
| last   | ---> null
| size 0 |
+--------+

```

---

# 6. add(E) Operation

Example:

```java
list.add("Java");
```

Default behavior:

Adds at the end.

---

Initial:

```text
first = null

last = null

```

---

Create Node:

```text
+------+-------+------+
|null  | Java  |null |
+------+-------+------+

```

---

Update references:

```text
first
 |
 v

+------+-------+------+
|null  | Java  |null |
+------+-------+------+
 ^
 |
last

```

---

Complexity:

```text
O(1)

```

Why?

Because LinkedList already maintains `last`.

No traversal needed.

---

# 7. addFirst(E)

Example:

```java
list.addFirst("Java");
```

Before:

```text
A <-> B <-> C

```

Add X:

```text
X <-> A <-> B <-> C

```

Only references change.

Steps:

```text
Create Node X

       |

Update X.next

       |

Update A.prev

       |

Move first pointer

```

Complexity:

```text
O(1)

```

---

# 8. addLast(E)

Example:

```java
list.addLast("Java");
```

Because LinkedList maintains:

```java
last
```

No traversal.

Before:

```text
A <-> B

last
 |
 v
 B

```

Add C:

```text
A <-> B <-> C


             ^
             |
            last

```

Complexity:

```text
O(1)

```

---

# 9. get(index) Operation ⭐⭐⭐⭐⭐

Example:

```java
list.get(3);
```

Many candidates incorrectly say:

> O(1)

Wrong.

LinkedList has no index array.

Memory:

```text
index 0       index 1       index 2


 A  ----->     B  ----->     C


```

Need traversal.

Flow:

```text
get(2)


first


 |

 v


Node A


 |

 v


Node B


 |

 v


Node C


return C

```

Complexity:

```text
O(n)

```

---

# 10. Optimization in LinkedList get()

Java does optimize traversal.

For index:

```java
get(index)
```

It checks:

Is index closer to beginning?

or

closer to end?

Diagram:

```text
A <-> B <-> C <-> D <-> E


get(1)


Start from first


get(4)


Start from last

```

But still:

Worst case:

```text
O(n)

```

---

# 11. remove(index)

Example:

```java
list.remove(2);
```

Before:

```text
A <-> B <-> C <-> D

```

Remove C.

Need first find C.

Traversal:

```text
A

|

B

|

C

```

After finding:

Disconnect:

```text
A <------------> D


```

Update:

```text
B.next = D

D.prev = B

```

---

Important:

Finding node:

```text
O(n)

```

Removing node:

```text
O(1)

```

Total:

```text
O(n)

```

---

# 12. Remove Node When Reference Is Available ⭐⭐⭐⭐⭐

This is where LinkedList shines.

Suppose:

```java
Node current;
```

You already have C.

Before:

```text
A <-> B <-> C <-> D

```

Remove C:

Just change:

```text
B.next = D

D.prev = B

```

Complexity:

```text
O(1)

```

This is why LinkedList is useful internally in some algorithms.

---

# 13. ArrayList vs LinkedList Memory

## ArrayList

Stores references:

```text
Object[]

+---+---+---+
| A | B | C |
+---+---+---+

```

Memory:

One array.

---

## LinkedList

Each element:

```text
Node


prev reference

data reference

next reference

```

Example:

```text
Node A

+------+------+------+
|prev  |data  |next |
+------+------+------+

```

For every element:

3 references.

Therefore:

LinkedList consumes much more memory.

---

# 14. Complete Complexity Table

| Operation     | LinkedList Complexity |
| ------------- | --------------------: |
| get(index)    |                  O(n) |
| addFirst()    |                  O(1) |
| addLast()     |                  O(1) |
| removeFirst() |                  O(1) |
| removeLast()  |                  O(1) |
| add(index)    |                  O(n) |
| remove(index) |                  O(n) |
| contains()    |                  O(n) |

---

# 15. LinkedList as Queue

Because it implements:

```java
Deque
```

you can use:

```java
Queue<String> queue =
        new LinkedList<>();

queue.offer("Task1");

queue.poll();

```

Flow:

```text
Producer


Task1

Task2

Task3


       |

       v


Consumer


Task1 first

```

---

# 16. LinkedList as Stack

Older code:

```java
Stack<Integer> stack =
        new Stack<>();

```

Modern recommendation:

```java
Deque<Integer> stack =
        new ArrayDeque<>();

```

Operations:

```java
push()

pop()

peek()

```

---

# 17. Why LinkedList Is Rarely Used?

This is an excellent senior interview discussion.

Many developers think:

> "LinkedList insertion is faster."

Reality:

Most application workloads:

* Need searching
* Need iteration
* Need random access

ArrayList wins because:

## 1. CPU Cache Locality

ArrayList:

```text
[A][B][C][D]

```

Data is together.

CPU loads nearby memory efficiently.

---

LinkedList:

```text
A

 |

(memory jump)

 |

B

 |

(memory jump)

 |

C

```

Cache misses are higher.

---

## 2. Memory Overhead

ArrayList:

```text
Reference only

```

LinkedList:

```text
Node object

+
prev reference

+
next reference

```

---

# 18. ArrayList vs LinkedList Interview Answer ⭐⭐⭐⭐⭐

Question:

"When will you use LinkedList?"

Weak answer:

> When insertion and deletion are frequent.

Better answer:

> I would use LinkedList only when I frequently insert/remove elements from the beginning or end, or when I already have references to nodes. For most business applications ArrayList is preferred because of better cache locality, lower memory overhead, and faster iteration.

---

# 19. Common Interview Questions

## Q1. Why LinkedList get() is O(n)?

Answer:

> LinkedList stores elements as nodes connected through references. Unlike ArrayList, it does not have direct index-based access, so it must traverse nodes.

---

## Q2. Why addFirst() is O(1)?

Answer:

> LinkedList maintains a reference to the first node, so inserting at the beginning only requires creating a node and updating references.

---

## Q3. Is LinkedList faster than ArrayList?

Answer:

> It depends on the operation. LinkedList is faster for node-based insertions/removals when the node location is known. But ArrayList is usually faster for real applications due to cache locality and lower memory usage.

---

## Q4. Difference between ArrayList and LinkedList internally?

Answer:

```text
ArrayList

Object[]

Index based


LinkedList

Node -> Node -> Node

Reference based

```

---

# Final Mental Model

Remember LinkedList as:

```text
LinkedList


Doubly Linked Nodes


A <----> B <----> C


Advantages:

O(1)
insert/remove at ends


Disadvantages:

O(n)
search/access


Memory:

High

```

---

# Chapter Complete ✅

Next Chapter:

# Chapter 4 — Vector and Stack Deep Dive ⭐⭐⭐

Topics:

* Why Vector exists
* Why Vector is legacy
* Internal synchronization
* Vector growth strategy
* Vector vs ArrayList
* Stack implementation
* Why Stack is discouraged
* Why ArrayDeque replaced Stack
* Interview questions and complexity comparison
