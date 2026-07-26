# Chapter 37 — Queue Family: PriorityQueue, ArrayDeque, BlockingQueue ⭐⭐⭐⭐

Queues are another important part of Collections Framework.

For senior Java interviews, focus on:

* How queues work internally
* Queue vs Stack
* Heap understanding
* PriorityQueue complexity
* ArrayDeque vs Stack
* BlockingQueue in concurrency

---

# 1. What Is Queue?

Queue follows:

```text
FIFO

First In First Out
```

Example:

People standing in line:

```text
First person enters

↓

First person leaves

```

---

Example:

```java
Queue<String> queue =
        new LinkedList<>();

queue.offer("A");
queue.offer("B");
queue.offer("C");
```

Structure:

```text
Front                         Rear

 A  --->  B  --->  C


remove()              add()

```

---

# 2. Queue Interface Hierarchy ⭐⭐⭐⭐

Java:

```text
Collection


     |

     v


   Queue


     |

 -----------------------

 |                     |

Deque             BlockingQueue


 |

 |

ArrayDeque


 |

 |

PriorityQueue

```

---

# 3. Queue Important Methods

Queue has two styles:

## Exception based

| Operation | Method    |
| --------- | --------- |
| Insert    | add()     |
| Remove    | remove()  |
| Check     | element() |

---

## Safe return based

| Operation | Method  |
| --------- | ------- |
| Insert    | offer() |
| Remove    | poll()  |
| Check     | peek()  |

---

Example:

```java
Queue<Integer> queue =
        new ArrayDeque<>();

queue.offer(10);
queue.offer(20);

System.out.println(queue.peek());
```

Output:

```text
10
```

---

# 4. ArrayDeque ⭐⭐⭐⭐⭐

`ArrayDeque` means:

> Array based Double Ended Queue.

It supports insertion/removal from both ends.

---

Internal structure:

```text

          Circular Array


Index:

0    1    2    3    4


A    B    C    D    E


 ^
 |
head


                ^
                |
               tail

```

---

Operations:

```java
deque.addFirst()
deque.addLast()

deque.removeFirst()
deque.removeLast()

```

---

# 5. Why ArrayDeque Over Stack? ⭐⭐⭐⭐⭐

Old Java:

```java
Stack<Integer> stack =
        new Stack<>();
```

Modern recommendation:

```java
Deque<Integer> stack =
        new ArrayDeque<>();
```

---

Why?

Stack extends Vector.

Vector uses synchronization.

Example:

```text
Stack

 |

 v

Vector

 |

 v

synchronized methods

```

Unnecessary overhead in single-threaded scenarios.

---

ArrayDeque:

* Faster
* No synchronization overhead
* Better design

---

# 6. ArrayDeque as Stack

LIFO:

```text
Last In First Out
```

Example:

```java
Deque<Integer> stack =
        new ArrayDeque<>();

stack.push(10);
stack.push(20);
stack.push(30);


stack.pop();

```

Flow:

```text

Before:


30
20
10


pop()


30 removed


20
10

```

---

Complexity:

| Operation | Complexity |
| --------- | ---------- |
| push      | O(1)       |
| pop       | O(1)       |
| peek      | O(1)       |

---

# 7. ArrayDeque as Queue

FIFO:

```java
Deque<Integer> queue =
        new ArrayDeque<>();

queue.offer(10);
queue.offer(20);
queue.offer(30);


queue.poll();

```

Before:

```text
10 -> 20 -> 30

```

After:

```text
20 -> 30

```

---

Complexity:

| Operation   | Complexity |
| ----------- | ---------- |
| addFirst    | O(1)       |
| addLast     | O(1)       |
| removeFirst | O(1)       |
| removeLast  | O(1)       |

---

# 8. PriorityQueue ⭐⭐⭐⭐⭐

Very common interview topic.

Normal Queue:

```text
FIFO

A
B
C

remove A

```

---

PriorityQueue:

Elements are removed based on priority.

Example:

Numbers:

```text
50
10
30
```

PriorityQueue:

```text
10
30
50

```

Smallest comes first by default.

---

# 9. PriorityQueue Internal Structure

Important:

PriorityQueue uses:

```text
Binary Heap
```

---

Default:

```text
Min Heap
```

Example:

```text

          10

       /      \

      20       30

    /
   40


```

Rule:

Parent <= Children

---

# 10. Heap Storage

Internally:

Array.

Example:

Heap:

```text

        10

      /    \

    20      30


```

Stored as:

```text
index:

0    1    2

10   20   30

```

---

Child calculation:

For index i:

Left child:

```java
2*i + 1
```

Right child:

```java
2*i + 2
```

Parent:

```java
(i-1)/2
```

---

# 11. PriorityQueue Insert ⭐⭐⭐⭐⭐

Example:

Insert:

```text
5
```

Current:

```text

       10

     /    \

   20      30

```

Add:

```text
5
```

First:

```text

       10

     /    \

   20      30

  /

5

```

Now violation:

```text
5 < 10
```

Heapify up.

Swap:

```text

        5

     /     \

    10      30

   /

 20

```

---

Complexity:

```text
O(log n)
```

---

# 12. PriorityQueue Remove ⭐⭐⭐⭐⭐

Remove root:

```text
5
```

Replace with last element.

Then heapify down.

Complexity:

```text
O(log n)
```

---

# 13. PriorityQueue Complexity

| Operation   | Complexity |
| ----------- | ---------- |
| peek        | O(1)       |
| offer/add   | O(log n)   |
| poll/remove | O(log n)   |
| search      | O(n)       |

---

# 14. PriorityQueue Custom Ordering

Example:

Max Heap.

Default:

```java
PriorityQueue<Integer> pq =
        new PriorityQueue<>();
```

Min heap.

---

Max heap:

```java
PriorityQueue<Integer> pq =
        new PriorityQueue<>(
            Collections.reverseOrder()
        );
```

---

Now:

Insert:

```text
10
50
20
```

Removal:

```text
50
20
10
```

---

# 15. Real Production Uses of PriorityQueue

## 1. Task Scheduling

Example:

```text
Critical task

      >

Normal task

      >

Low priority task

```

---

## 2. Top K Problems

Example:

Find top 10 largest numbers.

Use:

```text
Min Heap size 10
```

---

## 3. Dijkstra Algorithm

PriorityQueue stores:

```text
node + distance
```

Smallest distance processed first.

---

# 16. BlockingQueue ⭐⭐⭐⭐⭐

Now connecting collections with concurrency.

Used in:

```text
Producer Consumer Pattern
```

---

Problem without BlockingQueue:

Producer:

```java
queue.add(data);
```

Consumer:

```java
while(queue.isEmpty()){
   wait();
}
```

Need:

* wait()
* notify()
* synchronization

Complex.

---

BlockingQueue solves this.

---

Architecture:

```text

Producer


   |

   v


+----------------+

| BlockingQueue  |

+----------------+


   |

   v


Consumer


```

---

Producer:

```java
queue.put(order);
```

Consumer:

```java
queue.take();
```

---

Behavior:

Queue full:

```text
put()

blocks

```

Queue empty:

```text
take()

blocks

```

---

# 17. BlockingQueue Implementations

## ArrayBlockingQueue

Internal:

```text
Array
```

Characteristics:

* Fixed capacity
* Bounded

Example:

```java
new ArrayBlockingQueue<>(100);
```

Good for:

Memory control.

---

## LinkedBlockingQueue

Internal:

```text
Linked Nodes
```

Characteristics:

* Usually larger capacity
* Used by Executors

---

## PriorityBlockingQueue

Combination:

```text
PriorityQueue

+

Thread Safety
```

---

## DelayQueue

Elements become available after delay.

Example:

```text
Payment retry

after 5 minutes

```

---

# 18. BlockingQueue vs Queue

| Queue                         | BlockingQueue     |
| ----------------------------- | ----------------- |
| Does not wait                 | Can block         |
| Manual synchronization needed | Thread safe       |
| Single thread mostly          | Producer consumer |
| poll returns null             | take waits        |

---

# 19. PriorityQueue vs BlockingQueue

Common confusion.

## PriorityQueue

Purpose:

```text
Ordering
```

Example:

Highest priority task first.

---

## BlockingQueue

Purpose:

```text
Thread coordination
```

Example:

Producer consumer.

---

Can combine:

```text
PriorityBlockingQueue
```

---

# 20. Queue Selection Guide ⭐⭐⭐⭐⭐

```text

Need FIFO?

        |
        v

ArrayDeque


Need priority ordering?

        |
        v

PriorityQueue


Need producer consumer?

        |
        v

BlockingQueue


Need stack?

        |
        v

ArrayDeque


```

---

# 21. Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why ArrayDeque is preferred over Stack?

Answer:

> Stack is a legacy class based on Vector and synchronized unnecessarily. ArrayDeque provides faster stack operations without synchronization overhead.

---

## Q2. Is PriorityQueue completely sorted?

Answer:

No.

Important.

Only the head element is guaranteed to be the smallest/largest.

The internal heap is not fully sorted.

---

Example:

Heap:

```text
        10

     20     30

```

Array representation:

```text
10,20,30
```

Not sorted.

---

## Q3. Complexity of PriorityQueue?

Answer:

* peek → O(1)
* insert → O(log n)
* remove → O(log n)

---

## Q4. Difference between poll() and remove()?

Answer:

poll():

```text
returns null if empty
```

remove():

```text
throws NoSuchElementException
```

---

## Q5. Why use BlockingQueue?

Answer:

> It provides thread-safe communication between producers and consumers without manually managing wait/notify.

---

# Final Mental Model

```text
                 Queue Family


                     |

        --------------------------------


        |              |              |


   ArrayDeque    PriorityQueue   BlockingQueue


       |              |              |


    FIFO/LIFO       Heap        Producer Consumer


       |              |              |


    O(1) ops      O(log n)      Thread Safe


```

---

# Chapter Complete ✅

Covered:

✅ Queue basics
✅ Queue methods
✅ ArrayDeque internals
✅ Stack replacement
✅ PriorityQueue heap internals
✅ Heap complexity
✅ Custom comparator
✅ BlockingQueue
✅ Producer consumer connection
✅ Queue selection strategy
✅ Interview questions

---

Next (last important Collections area):

# Chapter 38 — Iterator Framework ⭐⭐⭐⭐

Topics:

* Iterator
* ListIterator
* Fail-fast
* Fail-safe
* ConcurrentModificationException
* Spliterator
* Internal working
* Parallel stream connection

This completes the Collections Framework module.
