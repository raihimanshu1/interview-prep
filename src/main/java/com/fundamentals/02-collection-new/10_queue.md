# Chapter 10 — Queue Framework Introduction + PriorityQueue Deep Dive ⭐⭐⭐⭐⭐

Queue is one of the most important Java Collections topics because it connects:

* Data Structures
* Heap algorithms
* Threading
* Scheduling systems
* Producer-consumer design
* Priority processing

For a **7+ years Java interview**, Queue questions usually connect with:

* `PriorityQueue` internals
* Heap data structure
* `BlockingQueue`
* `ArrayDeque`
* BFS algorithms
* Task schedulers

---

# 1. What is a Queue?

A Queue follows:

> **FIFO — First In First Out**

Example:

People standing in line:

```text
First person enters

A

B

C


First person leaves

A

```

---

Data structure:

```text
Insertion

   tail
    |
    v

[A][B][C]

 ^
 |
head


Removal

from head

```

---

# 2. Queue Interface Hierarchy

Java Queue hierarchy:

```text
                 Collection
                     |
                  Queue
                     |
        +------------+-------------+
        |                          |
   Deque                    PriorityQueue
        |
 +------+------+
 |             |
ArrayDeque   LinkedList


Concurrent:

BlockingQueue
ConcurrentLinkedQueue

```

---

# 3. Queue Interface

Java:

```java
public interface Queue<E>
extends Collection<E>
```

Common methods:

| Operation          | Method            |
| ------------------ | ----------------- |
| Insert             | add(), offer()    |
| Remove             | remove(), poll()  |
| View first element | element(), peek() |

---

# 4. add() vs offer()

Both insert elements.

Example:

```java
Queue<String> queue =
        new LinkedList<>();

queue.add("A");

queue.offer("B");
```

Difference:

## add()

Throws exception if insertion fails.

```java
queue.add("A");
```

---

## offer()

Returns false if insertion fails.

```java
boolean result =
        queue.offer("A");
```

Preferred in production code.

---

# 5. remove() vs poll()

Both remove head element.

Example:

```java
queue.poll();
```

---

## remove()

If queue empty:

```java
NoSuchElementException
```

---

## poll()

If queue empty:

```java
null
```

---

# 6. element() vs peek()

Both inspect head.

Example:

```java
queue.peek();
```

---

## element()

Empty queue:

```
NoSuchElementException
```

---

## peek()

Empty queue:

```
null
```

---

# 7. Types of Queue

Java provides multiple implementations because different requirements need different structures.

---

## 1. LinkedList Queue

Uses:

```text
Doubly Linked List
```

Example:

```java
Queue<Integer> queue =
        new LinkedList<>();
```

Operations:

Insert:

```text
tail
 |
 v

A -> B -> C

```

Remove:

```text
head

A -> B -> C

```

Complexity:

| Operation | Complexity |
| --------- | ---------- |
| offer     | O(1)       |
| poll      | O(1)       |
| peek      | O(1)       |

---

# 8. PriorityQueue ⭐⭐⭐⭐⭐

Most important Queue topic.

Normal Queue:

```text
Insertion order decides removal
```

Example:

Insert:

```text
10
5
20
```

Removal:

```text
10
5
20

```

---

PriorityQueue:

> Elements are removed based on priority, not insertion order.

Example:

Insert:

```text
10
5
20
```

Remove:

```text
5
10
20
```

---

# 9. PriorityQueue Definition

Java:

```java
PriorityQueue<Integer> pq =
        new PriorityQueue<>();

pq.add(30);
pq.add(10);
pq.add(20);
```

Default:

```text
Min Heap
```

---

Output:

```java
pq.poll();
```

returns:

```
10
```

---

# 10. Internal Structure ⭐⭐⭐⭐⭐

Important:

PriorityQueue uses:

> Binary Heap

Specifically:

```text
Min Heap
```

by default.

---

Heap is stored inside an array.

Example:

Elements:

```text
10
20
30
40
50
```

Array:

```text
Index

0   1   2   3   4

10 20 30 40 50

```

---

Tree representation:

```text
              10

          /        \

        20          30

       /  \

     40    50

```

---

# 11. Why Array Instead of Tree Nodes?

A heap is a complete binary tree.

Complete binary trees have perfect array representation.

No need for:

```text
left pointer
right pointer
parent pointer

```

Saves memory.

---

# 12. Heap Relationship Formula ⭐⭐⭐⭐⭐

For node index:

```
i
```

Parent:

```
(i - 1) / 2
```

Left child:

```
2*i + 1
```

Right child:

```
2*i + 2
```

Example:

Index:

```
2
```

Children:

```
5 and 6
```

---

Diagram:

```text
          index 0

             10

       /           \

 index1             index2

   20                30


```

---

# 13. Min Heap Property

In Min Heap:

Parent is smaller than children.

Example:

```text
              5

          /       \

        10         20

       /
      30

```

Valid.

---

Invalid:

```text
              20

          /

        10

```

Because child is smaller.

---

# 14. add() Operation Internals ⭐⭐⭐⭐⭐

Example:

```java
pq.add(5);
```

Flow:

```text
add(5)


 |
 v


Insert at end


 |
 v


Compare with parent


 |
 v


Swap upward if smaller


 |
 v


Heap restored

```

This process:

```
Heapify Up
```

or

```
Bubble Up
```

---

Example:

Initial:

```text
        10

       /
     20

```

Insert:

```
5
```

Before balancing:

```text
        10

      /    \

    20      5

```

5 < 10

Swap:

```text
        5

      /   \

    20     10

```

---

Complexity:

Heap height:

```
log n
```

Therefore:

```
add() = O(log n)
```

---

# 15. poll() Operation Internals ⭐⭐⭐⭐⭐

Example:

```java
pq.poll();
```

Need to remove root.

Root:

```text
minimum element
```

---

Flow:

```text
Remove root


     |
     v


Move last element to root


     |
     v


Compare children


     |
     v


Swap downward


     |
     v


Heap restored

```

Called:

```
Heapify Down
```

---

Example:

Before:

```text
        5

      /   \

    10     20

```

Remove 5:

Move last:

```text
        20

      /

    10

```

Swap:

```text
        10

      /

    20

```

---

Complexity:

```
poll() = O(log n)
```

---

# 16. PriorityQueue Complexity ⭐⭐⭐⭐⭐

| Operation      | Complexity |
| -------------- | ---------- |
| add()          | O(log n)   |
| offer()        | O(log n)   |
| poll()         | O(log n)   |
| remove(Object) | O(n)       |
| peek()         | O(1)       |
| size()         | O(1)       |

---

# 17. Why peek() is O(1)?

Because root always contains priority element.

Array:

```text
index 0

     |
     v

minimum value

```

No searching required.

---

# 18. Min Heap vs Max Heap

Default:

```java
PriorityQueue<Integer> pq =
        new PriorityQueue<>();
```

is:

```
Min Heap
```

---

Example:

```java
pq.add(30);
pq.add(10);
pq.add(20);

pq.poll();
```

Output:

```
10
```

---

For Max Heap:

Use Comparator:

```java
PriorityQueue<Integer> pq =
    new PriorityQueue<>(
        Comparator.reverseOrder()
    );
```

Now:

```
30
20
10
```

---

# 19. Custom Objects in PriorityQueue ⭐⭐⭐⭐⭐

Example:

Task scheduler.

```java
class Task {

    String name;
    int priority;

}
```

Need ordering.

Option 1:

Comparable:

```java
class Task
implements Comparable<Task>
{

 public int compareTo(Task t){

    return this.priority - t.priority;

 }

}
```

---

Option 2:

Comparator:

```java
PriorityQueue<Task> queue =
new PriorityQueue<>(
 Comparator.comparing(Task::getPriority)
);
```

---

# 20. PriorityQueue vs TreeSet ⭐⭐⭐⭐⭐

Very common interview question.

Both maintain ordering.

But different purpose.

---

PriorityQueue:

```text
Only smallest/largest element matters

```

TreeSet:

```text
Need complete sorted collection

```

---

Comparison:

| Feature          | PriorityQueue | TreeSet        |
| ---------------- | ------------- | -------------- |
| Internal         | Heap          | Red Black Tree |
| Duplicate        | Allowed       | Not allowed    |
| Sorted iteration | No            | Yes            |
| Peek min/max     | O(1)          | O(log n)       |
| Insert           | O(log n)      | O(log n)       |

---

Example:

Find smallest element repeatedly:

Use:

```
PriorityQueue
```

Need all elements sorted:

Use:

```
TreeSet
```

---

# 21. PriorityQueue Does NOT Maintain Sorted Order ⭐⭐⭐⭐⭐

Important trap.

Example:

```java
PriorityQueue<Integer> pq =
new PriorityQueue<>();

pq.add(30);
pq.add(10);
pq.add(20);

System.out.println(pq);
```

Possible output:

```
[10,30,20]
```

Not:

```
[10,20,30]
```

Only guarantee:

```
head is smallest
```

---

# 22. Real Production Examples

## 1. Task Scheduler

Tasks:

```text
High priority payment

Medium email

Low cleanup

```

PriorityQueue:

```text
Payment
Email
Cleanup

```

---

## 2. Dijkstra Algorithm

Shortest path:

Need:

```
minimum distance node
```

Use:

```java
PriorityQueue
```

---

## 3. Top K Problems

Examples:

* Top K frequent elements
* K closest points
* K largest numbers

Uses:

```
Heap
```

---

# 23. Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. Internal implementation of PriorityQueue?

Answer:

> PriorityQueue internally uses a binary heap stored in an array. By default it is a min heap.

---

## Q2. Complexity of PriorityQueue?

Answer:

> Insert and remove operations are O(log n), while peek is O(1).

---

## Q3. Does PriorityQueue maintain sorted order?

Answer:

> No. Only the head element is guaranteed to have the highest priority. Iteration order is not sorted.

---

## Q4. Difference between TreeSet and PriorityQueue?

Answer:

> TreeSet maintains complete sorted order with unique elements using a Red-Black Tree. PriorityQueue only guarantees efficient access to the highest/lowest priority element using a heap.

---

## Q5. How to create Max Heap?

Answer:

```java
PriorityQueue<Integer> pq =
new PriorityQueue<>(
Comparator.reverseOrder()
);
```

---

# Final Mental Model

Remember:

```
Queue


FIFO
 |
 |
 +----------------+
 |                |
Normal Queue   PriorityQueue

                  |
                  |
                  v

              Binary Heap

                  |
                  |
                  v

          Fast min/max retrieval

```

PriorityQueue rule:

```
Insertion:
O(log n)

Removal:
O(log n)

Peek:
O(1)

```

---

# Chapter Complete ✅

Next Chapter:

# Chapter 11 — ArrayDeque Deep Dive ⭐⭐⭐⭐⭐

Topics:

* Why ArrayDeque is preferred over Stack
* Circular array internals
* Head/tail pointers
* Double-ended queue
* Stack implementation using Deque
* Queue implementation using Deque
* Complexity analysis
* ArrayDeque vs LinkedList
* Interview questions

This chapter is important because modern Java avoids the old `Stack` class and prefers `Deque`.
