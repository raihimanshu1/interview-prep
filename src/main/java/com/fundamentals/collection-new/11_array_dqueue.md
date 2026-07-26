# Chapter 11 — ArrayDeque Deep Dive ⭐⭐⭐⭐⭐

`ArrayDeque` is one of the most important Queue/Deque implementations in Java.

For senior interviews, the key discussion points are:

* Why `ArrayDeque` is preferred over `Stack`
* Why it is faster than `LinkedList`
* Internal circular array implementation
* Head and tail pointers
* Stack implementation using Deque
* Queue implementation using Deque
* Complexity analysis
* Common interview traps

---

# 1. What is Deque?

Deque means:

> Double Ended Queue

Unlike normal Queue:

```text
Queue

Insert  --->  [ A ][ B ][ C ]  ---> Remove

```

Operations happen only at one side.

---

Deque allows both ends:

```text
              addFirst()
                  |
                  v

        +----+----+----+----+
        | A  | B  | C  | D  |
        +----+----+----+----+

                  ^
                  |
              addLast()


removeFirst()              removeLast()

```

Meaning:

You can use it as:

* Queue
* Stack
* Double-ended queue

---

# 2. Queue vs Deque

## Queue

FIFO:

```text
First In First Out


A -> B -> C


remove A

```

---

## Stack

LIFO:

```text
Last In First Out


A
B
C


remove C

```

---

## Deque

Supports both:

```text
A -> B -> C


remove A


or


remove C

```

---

# 3. ArrayDeque Introduction ⭐⭐⭐⭐⭐

Example:

```java
Deque<Integer> deque =
        new ArrayDeque<>();

deque.add(10);
deque.add(20);
deque.add(30);
```

Output:

```text
10 20 30
```

---

ArrayDeque implements:

```java
Deque
```

and internally uses:

```text
Resizable Circular Array

```

---

# 4. Why Was ArrayDeque Introduced?

Before Java 6:

Common choices:

```text
Stack

LinkedList

```

Problems:

## Stack

Old legacy class:

```java
Stack<Integer> stack =
        new Stack<>();
```

Problems:

* Extends Vector
* Every operation synchronized
* Slower
* Legacy API

---

## LinkedList

Can work as Queue:

```java
Queue<Integer> queue =
        new LinkedList<>();
```

But:

Each node requires extra memory:

```text
Node

+---------+
| value   |
| next    |
| prev    |
+---------+

```

---

ArrayDeque solves both.

---

# 5. Internal Structure ⭐⭐⭐⭐⭐

ArrayDeque internally maintains:

```java
transient Object[] elements;
```

Example:

```text
Internal Array


Index:

0    1    2    3    4

[A] [B] [C] [D] [ ]

```

But unlike ArrayList:

It treats the array as circular.

---

# 6. Circular Array Concept ⭐⭐⭐⭐⭐

Problem with normal array:

Suppose:

```text
Capacity = 5


[A][B][C][D][E]

```

Remove first two:

```text
[ ][ ][C][D][E]

```

Space at beginning is wasted.

---

ArrayDeque reuses that space.

Circular view:

```text

        0
        |
        v

[ ][B][C][D][E]

 ^
 |
 4


```

After adding:

```text
X

```

It goes back:

```text
[X][B][C][D][E]

```

---

# 7. Head and Tail Pointers ⭐⭐⭐⭐⭐

ArrayDeque maintains two indexes:

```text
head

tail

```

Example:

```text
Array


0     1     2     3     4

[A]   [B]   [C]   [D]   [ ]

 ^
 |
head


             ^
             |
            tail

```

---

Meaning:

```text
head = first element

tail = next insertion position

```

---

# 8. addFirst() Flow

Example:

```java
deque.addFirst(10);
```

Flow:

```text
addFirst(10)


       |
       v


Move head backward


       |
       v


Store element


       |
       v


Update head pointer

```

Diagram:

Before:

```text
head
 |
 v

[ ][A][B][C][ ]

```

After:

```text
head
 |
 v

[10][A][B][C][ ]

```

---

Complexity:

```text
O(1)

```

---

# 9. addLast() Flow

Example:

```java
deque.addLast(20);
```

Flow:

```text
addLast()


     |
     v


Insert at tail


     |
     v


Move tail

```

Example:

Before:

```text
[A][B][C][ ]

          ^
          |
         tail

```

After:

```text
[A][B][C][20]

```

Complexity:

```text
O(1)

```

---

# 10. removeFirst()

Example:

```java
deque.removeFirst();
```

Flow:

```text
removeFirst()


        |
        v


Read head element


        |
        v


Clear position


        |
        v


Move head forward

```

Complexity:

```text
O(1)

```

---

# 11. removeLast()

Example:

```java
deque.removeLast();
```

Flow:

```text
removeLast()


        |
        v


Move tail backward


        |
        v


Remove element

```

Complexity:

```text
O(1)

```

---

# 12. ArrayDeque as Queue ⭐⭐⭐⭐⭐

Queue means FIFO.

Example:

```java
Queue<String> queue =
        new ArrayDeque<>();

queue.offer("A");
queue.offer("B");
queue.offer("C");
```

Internal:

```text
A -> B -> C

```

Remove:

```java
queue.poll();
```

Returns:

```text
A

```

---

Operations mapping:

| Queue Operation | ArrayDeque Method |
| --------------- | ----------------- |
| Insert          | offer()           |
| Remove          | poll()            |
| Peek            | peek()            |

---

# 13. ArrayDeque as Stack ⭐⭐⭐⭐⭐

Modern Java recommendation:

Do NOT use:

```java
Stack<Integer>
```

Use:

```java
Deque<Integer>
```

Example:

```java
Deque<Integer> stack =
        new ArrayDeque<>();

stack.push(10);
stack.push(20);
stack.push(30);
```

Stack:

```text
Top

30

20

10

Bottom

```

---

Remove:

```java
stack.pop();
```

Returns:

```text
30

```

---

Operations:

| Stack | ArrayDeque    |
| ----- | ------------- |
| push  | addFirst()    |
| pop   | removeFirst() |
| peek  | peekFirst()   |

---

# 14. Why ArrayDeque Does Not Allow Null?

Important interview question.

Example:

```java
deque.add(null);
```

Throws:

```text
NullPointerException
```

Why?

Because:

```java
poll()
```

returns:

```text
null
```

when queue is empty.

If null values were allowed:

Cannot distinguish:

```text
Queue empty?

or

Actual null element?

```

---

# 15. ArrayDeque vs LinkedList ⭐⭐⭐⭐⭐

Very common question.

## ArrayDeque

Internal:

```text
Circular Array

```

---

## LinkedList

Internal:

```text
Doubly Linked List

```

---

Comparison:

| Feature        | ArrayDeque     | LinkedList         |
| -------------- | -------------- | ------------------ |
| Structure      | Circular array | Doubly linked list |
| Memory         | Lower          | Higher             |
| Cache locality | Better         | Poor               |
| Random access  | No             | No                 |
| Null allowed   | No             | Yes                |
| Thread safe    | No             | No                 |
| Performance    | Faster         | Slower             |

---

# 16. Why ArrayDeque Is Faster?

CPU cache.

Array:

```text
[A][B][C][D]

```

Stored continuously.

CPU can load nearby values quickly.

---

LinkedList:

```text
A -> memory location 500

B -> memory location 900

C -> memory location 200

```

Nodes scattered in memory.

More cache misses.

---

# 17. ArrayDeque vs Stack ⭐⭐⭐⭐⭐

| Feature         | Stack  | ArrayDeque     |
| --------------- | ------ | -------------- |
| Type            | Legacy | Modern         |
| Internal        | Vector | Circular array |
| Synchronization | Yes    | No             |
| Performance     | Slower | Faster         |
| Recommended     | No     | Yes            |

---

Example:

Old:

```java
Stack<Integer> stack =
        new Stack<>();
```

Modern:

```java
Deque<Integer> stack =
        new ArrayDeque<>();
```

---

# 18. ArrayDeque Complexity ⭐⭐⭐⭐⭐

| Operation     | Complexity |
| ------------- | ---------- |
| addFirst()    | O(1)       |
| addLast()     | O(1)       |
| removeFirst() | O(1)       |
| removeLast()  | O(1)       |
| peekFirst()   | O(1)       |
| peekLast()    | O(1)       |
| contains()    | O(n)       |

---

# 19. Resizing Internals

Initial array:

```text
capacity = 16

```

When full:

```text
Old Array

[A][B][C][D]


New Larger Array

[A][B][C][D][ ][ ][ ][ ]

```

Elements copied.

Complexity:

```text
Resize = O(n)

```

But amortized:

```text
add = O(1)

```

---

# 20. Real Production Examples ⭐⭐⭐⭐⭐

## 1. Browser History

Need:

Previous page

Next page

Example:

```text
Google
 |
Amazon
 |
Netflix

```

Deque works well.

---

## 2. Sliding Window Algorithms

Example:

Maximum in window:

```text
[1,3,-1,-3,5]

```

Uses:

```java
Deque<Integer>
```

---

## 3. Undo/Redo Systems

Stack behavior:

```text
Action1

Action2

Action3


Undo Action3

```

ArrayDeque.

---

## 4. BFS Algorithm

Graph traversal:

```text
Queue<Node>

```

ArrayDeque preferred.

Example:

```java
Queue<Node> queue =
        new ArrayDeque<>();

```

---

# 21. Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why prefer ArrayDeque over Stack?

Answer:

> Stack is a legacy synchronized class extending Vector. ArrayDeque provides better performance using a resizable circular array and is the recommended stack implementation.

---

## Q2. Why is ArrayDeque faster than LinkedList?

Answer:

> ArrayDeque uses contiguous memory storage, giving better cache locality and lower memory overhead compared to LinkedList nodes.

---

## Q3. How does ArrayDeque implement both Queue and Stack?

Answer:

> Because Deque supports insertion and removal from both ends. Queue operations use one side, while stack operations use the other side.

---

## Q4. Why does ArrayDeque not allow null?

Answer:

> Because null is used as a special return value by methods like poll() and peek() to indicate an empty queue.

---

## Q5. Is ArrayDeque thread-safe?

Answer:

No.

It is not synchronized.

For concurrent scenarios use:

```text
BlockingDeque

or

ConcurrentLinkedDeque

```

---

# 22. Complete Queue Comparison

```text

                 Queue

                   |
        -----------------------
        |                     |
    PriorityQueue          Deque

        |                     |
      Heap              ArrayDeque

                           |
              ---------------------
              |                   |

            Queue              Stack


```

---

# Final Mental Model

Remember:

```text
ArrayDeque


Circular Array


        |
        |

head <-----------> tail


        |
        |

O(1) insertion/removal from both ends

```

The interview one-liner:

> "ArrayDeque is a resizable circular array implementation of Deque. It provides O(1) insertion and removal from both ends and is preferred over legacy Stack and LinkedList for stack and queue operations."

---

# Chapter Complete ✅

Next Chapter:

# Chapter 12 — BlockingQueue Deep Dive ⭐⭐⭐⭐⭐

Topics:

* Why BlockingQueue exists
* Producer Consumer pattern
* put() vs offer()
* take() vs poll()
* ArrayBlockingQueue internals
* LinkedBlockingQueue internals
* PriorityBlockingQueue
* DelayQueue
* ExecutorService relation
* Thread pool work queues
* Interview questions

This will connect Collections directly with the Concurrency module you already completed.
