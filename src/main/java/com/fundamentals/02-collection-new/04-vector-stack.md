# Chapter 4 — Vector and Stack Deep Dive ⭐⭐⭐

`Vector` and `Stack` are legacy collections, but they are still asked in interviews because they reveal whether you understand:

* Synchronization overhead
* Thread safety
* Modern alternatives
* Collection evolution in Java

A common senior interview question:

> "Why do we still have Vector and Stack if ArrayList and ArrayDeque exist?"

The answer requires understanding history.

---

# 1. Collection Evolution Timeline

Java Collections evolved over time.

```text
Java 1.0


Vector
Hashtable
Stack


        |
        |
        v


Java 1.2 Collections Framework


ArrayList
HashMap
LinkedList
Queue


        |
        |
        v


Modern Java


ConcurrentHashMap
CopyOnWriteArrayList
ArrayDeque

```

---

# Part 1 — Vector Deep Dive

# 2. What is Vector?

`Vector` is a:

> Dynamically growing array implementation that is synchronized.

Meaning:

It is similar to `ArrayList`, but every operation is protected by a lock.

Example:

```java
Vector<String> vector = new Vector<>();

vector.add("Java");
vector.add("Spring");
```

---

# 3. Vector Internal Structure

Vector internally uses:

```text
Object[]

```

Same idea as ArrayList.

Memory:

```text
Vector Object


+----------------+
| elementData    |
| elementCount   |
| capacityInc    |
+----------------+
        |
        |
        v


Object Array


+----+----+----+----+
| A  | B  | C  |    |
+----+----+----+----+

```

---

# 4. Vector Class Structure

Simplified:

```java
public class Vector<E>
    extends AbstractList<E>
    implements List<E>, RandomAccess {


    protected Object[] elementData;


    protected int elementCount;


    protected int capacityIncrement;

}
```

Important fields:

---

## elementData

Stores actual elements.

Example:

```text
[A][B][C][ ][ ]

```

---

## elementCount

Current number of elements.

Example:

```text
Capacity = 10

Size = 3

```

---

## capacityIncrement

Controls growth.

Unlike ArrayList, Vector can specify growth increment.

Example:

```java
new Vector<>(10,5);
```

Means:

Initial capacity:

```
10
```

After full:

```
15
```

---

# 5. Vector Add Operation

Example:

```java
vector.add("Java");
```

Flow:

```text

add("Java")

      |
      v

Acquire Lock

      |
      v

Check Capacity

      |
      v

Insert Element

      |
      v

Release Lock


```

Because methods are synchronized.

---

# 6. Vector Synchronization

Example:

Vector:

```java
public synchronized boolean add(E e)
{
    ...
}
```

The whole method is locked.

---

Two threads:

Thread A:

```java
vector.add("A");
```

Thread B:

```java
vector.add("B");
```

Execution:

```text

Thread A

     |
     v

 LOCK

     |
     v

 add()


----------------


Thread B

     |
     v

 WAIT


```

Only one thread modifies Vector at a time.

---

# 7. Why Vector Is Slow?

Because synchronization happens even when unnecessary.

Example:

Single-threaded application:

```java
vector.add("Java");
```

Still:

```
Lock
 |
Operation
 |
Unlock
```

The overhead exists.

---

Modern alternative:

```java
ArrayList
```

because:

* No unnecessary locking
* Faster
* Better optimized

---

# 8. Vector Growth Strategy

ArrayList:

```text
newCapacity = old + old/2

≈ 1.5x

```

Vector:

Default:

```text
double capacity

```

Example:

Old:

```
10
```

New:

```
20
```

---

Diagram:

```text
Old Vector


[A][B][C][D][E]


Full


       |
       v


New Array


[A][B][C][D][E][ ][ ][ ][ ][ ]

Capacity doubled


```

---

# 9. Vector Complexity

Because Vector is array based:

| Operation     | Complexity     |
| ------------- | -------------- |
| get(index)    | O(1)           |
| set(index)    | O(1)           |
| add(end)      | O(1) amortized |
| add(index)    | O(n)           |
| remove(index) | O(n)           |
| contains      | O(n)           |

Same as ArrayList.

Difference:

Synchronization.

---

# 10. ArrayList vs Vector ⭐⭐⭐⭐⭐

| Feature            | ArrayList     | Vector               |
| ------------------ | ------------- | -------------------- |
| Internal structure | Dynamic array | Dynamic array        |
| Thread safe        | No            | Yes                  |
| Synchronization    | None          | Method-level locking |
| Performance        | Faster        | Slower               |
| Growth             | 1.5x          | 2x default           |
| Introduced         | Java 1.2      | Java 1.0             |

---

Interview answer:

> Vector is a legacy synchronized dynamic array. ArrayList provides better performance in single-threaded scenarios because it avoids synchronization overhead. For concurrent scenarios, modern alternatives like CopyOnWriteArrayList or synchronized wrappers are preferred.

---

# Part 2 — Stack Deep Dive

# 11. What is Stack?

Stack follows:

```
LIFO

Last In First Out

```

Example:

Stack of plates:

```text

        Plate 3  ← top

        Plate 2

        Plate 1


```

Last added item comes out first.

---

# 12. Java Stack Class

Java provides:

```java
Stack<Integer> stack =
        new Stack<>();

```

Example:

```java
stack.push(10);
stack.push(20);
stack.push(30);

stack.pop();

```

Result:

```
30 removed

```

---

# 13. Stack Internal Structure

Important:

Java Stack extends Vector.

Hierarchy:

```text

Object

  |

AbstractCollection

  |

AbstractList

  |

Vector

  |

Stack


```

Therefore:

Stack inherits Vector synchronization.

---

# 14. Stack Operations

## push()

Example:

```java
stack.push(10);
```

Flow:

```text

push(10)


      |
      v


add element at end


      |
      v


top moves


```

---

Before:

```text
[]

```

After:

```text
[10]

```

---

Add:

```text
[10][20][30]

          ^
          |
         top

```

---

## pop()

Example:

```java
stack.pop();
```

Removes top.

Before:

```text
[10][20][30]

          ^
          |
         top

```

After:

```text
[10][20]

```

Returns:

```
30

```

---

# 15. Stack Complexity

| Operation | Complexity     |
| --------- | -------------- |
| push      | O(1) amortized |
| pop       | O(1)           |
| peek      | O(1)           |
| search    | O(n)           |

---

# 16. Why Stack Is Not Recommended?

Modern Java recommendation:

Use:

```java
ArrayDeque

```

instead of:

```java
Stack

```

---

Reasons:

## 1. Stack is legacy

It was created before Collections Framework.

---

## 2. Inherits Vector synchronization

Every operation has locking overhead.

---

## 3. Poor naming

Stack extends Vector, so it exposes methods like:

```java
add()

remove()

```

which break pure stack abstraction.

---

Example:

```java
Stack<Integer> stack = new Stack<>();

stack.add(100);

```

Technically allowed.

But conceptually wrong.

---

# 17. ArrayDeque as Stack

Modern:

```java
Deque<Integer> stack =
        new ArrayDeque<>();


stack.push(10);

stack.push(20);

stack.pop();

```

Internal:

```text
Circular Array


+----+----+----+----+
|10  |20  |   |    |
+----+----+----+----+

```

---

Advantages:

* Faster
* No synchronization overhead
* Better memory usage

---

# 18. Stack vs ArrayDeque ⭐⭐⭐⭐⭐

| Feature         | Stack          | ArrayDeque   |
| --------------- | -------------- | ------------ |
| Type            | Legacy class   | Modern deque |
| Thread safe     | Yes            | No           |
| Synchronization | Vector locking | None         |
| Performance     | Slower         | Faster       |
| Allows null     | Yes            | No           |
| Recommended     | No             | Yes          |

---

# 19. When Would You Use Vector Today?

Rare cases:

1. Maintaining old legacy code

2. APIs requiring Vector type

Otherwise:

Prefer:

```text
ArrayList

or

CopyOnWriteArrayList

```

---

# 20. When Would You Use Stack?

Almost never.

Replace:

```java
Stack<Integer>
```

with:

```java
Deque<Integer> stack =
        new ArrayDeque<>();

```

---

# 21. Interview Questions ⭐⭐⭐⭐⭐

## Q1. Difference between ArrayList and Vector?

Expected:

> Both use dynamic arrays. Vector is synchronized at method level, making it thread-safe but slower. ArrayList is not synchronized and provides better performance.

---

## Q2. Why is Vector considered legacy?

Answer:

> Vector was introduced before the Collections Framework. Its synchronization model is coarse-grained and modern concurrency utilities provide better alternatives.

---

## Q3. Why should we use ArrayDeque instead of Stack?

Answer:

> Stack extends Vector and inherits synchronization overhead. ArrayDeque provides stack operations with better performance and cleaner design.

---

## Q4. Is Vector completely thread-safe?

Important senior answer:

> Individual operations are synchronized, but compound operations are not automatically atomic.

Example:

```java
if(!vector.contains(x)){
    vector.add(x);
}

```

Two threads can still race.

---

# Final Mental Model

Remember:

```text

Vector

Dynamic Array
+
Synchronization


ArrayList

Dynamic Array
+
No Synchronization


Stack

Vector
+
LIFO


ArrayDeque

Modern Stack Replacement


```

---

# Chapter Complete ✅

Next Chapter:

# Chapter 5 — CopyOnWriteArrayList Deep Dive ⭐⭐⭐⭐⭐

Topics:

* Why normal ArrayList fails in concurrent scenarios
* Copy-on-write principle
* Internal array replacement flow
* Read vs write behavior
* Locking mechanism
* Memory trade-offs
* Iterator snapshot behavior
* Real production examples
* CopyOnWriteArrayList vs synchronizedList vs ArrayList
* Interview questions
