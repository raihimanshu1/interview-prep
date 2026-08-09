# Chapter 30 — ConcurrentHashMap Deep Dive (Advanced) ⭐⭐⭐⭐⭐

This is one of the **highest-value Java interview topics** for a senior backend developer.

Almost every company asks some variation:

* Why HashMap is not thread-safe?
* How does ConcurrentHashMap work internally?
* Difference between Java 7 and Java 8 ConcurrentHashMap?
* Why are reads lock-free?
* How does resize happen?
* How does `computeIfAbsent()` work?
* Why does ConcurrentHashMap not allow null keys?

We already covered the basics. Now we go into **JVM-level internals**.

---

# 1. Why Was ConcurrentHashMap Introduced?

Before Java 5, developers used:

```java
Hashtable
```

for thread-safe maps.

Problem:

Hashtable:

```text
Every operation

        |
        v

Acquire single lock

        |
        v

Execute operation

        |
        v

Release lock

```

Example:

100 threads accessing Hashtable:

```text
Thread 1 ----\
Thread 2 -----\
Thread 3 ------>  GLOBAL LOCK
Thread 4 -----/
Thread 5 ----/
```

Only one thread progresses.

---

Java needed:

* Thread safety
* Better scalability
* Higher throughput

Solution:

```text
ConcurrentHashMap
```

---

# 2. Basic Structure

At high level:

```text
ConcurrentHashMap


        |

        v


Table[]


        |

        v


Buckets


        |

        v


Nodes


```

Similar to HashMap:

```text
Bucket

   |
   |
   v

Node(key,value,next)

```

---

But synchronization strategy is completely different.

---

# 3. Java 7 ConcurrentHashMap Architecture ⭐⭐⭐⭐⭐

Java 7 used:

```text
Segment Locking
```

Think of it as:

A map divided into smaller HashMaps.

---

Structure:

```
ConcurrentHashMap


          |
          |
    -----------------

    Segment 0

       |
       |
    HashEntry[]


    Segment 1

       |
       |
    HashEntry[]


    Segment 2

       |
       |
    HashEntry[]

```

---

Each segment had its own lock.

Example:

```java
segment.lock()
```

---

Imagine:

```text
Thread A

updates Segment 1


Thread B

updates Segment 3

```

Both can execute.

---

Diagram:

```
Segment 1
-----------
LOCKED
-----------


Segment 2
-----------
FREE
-----------


Segment 3
-----------
LOCKED
-----------

```

---

Much better than Hashtable.

---

# 4. Java 7 Segment Internals

Each Segment was basically:

```java
class Segment<K,V>
        extends ReentrantLock {

    HashEntry<K,V>[] table;

}
```

Meaning:

Segment itself was a lock.

---

Flow:

```
put(key,value)


      |
      v


Find segment


      |
      v


Acquire segment lock


      |
      v


Insert entry


      |
      v


Release lock

```

---

# 5. Problems With Java 7 Design

Although better than Hashtable:

Still had limitations.

Example:

Capacity:

```
16 segments
```

Maximum parallel writes:

```
16
```

If you had:

```
1000 threads
```

many threads still wait.

---

Also memory overhead:

```
Segment objects

+
Locks

+
Tables

```

---

Java 8 redesigned it completely.

---

# 6. Java 8 ConcurrentHashMap Architecture ⭐⭐⭐⭐⭐

Java 8 removed segments.

No:

```
Segment
```

No:

```
global lock
```

Instead:

```
CAS

+

bucket-level synchronization

```

---

Structure:

```
ConcurrentHashMap


        |

        v


Node[] table


        |

        v


Bucket 0


Bucket 1


Bucket 2


Bucket 3

```

---

Each bucket works independently.

---

Example:

```
Bucket 1

Thread A
   |
   v
 synchronize


Bucket 2

Thread B
   |
   v
 synchronize


Bucket 3

Thread C
   |
   v
 CAS

```

---

# 7. Java 8 Internal Node Structure

Important interview point.

ConcurrentHashMap uses:

```java
static class Node<K,V>
```

Structure:

```java
class Node<K,V>{

    final int hash;

    final K key;

    volatile V val;

    volatile Node<K,V> next;

}

```

---

Notice:

```java
volatile
```

for:

```
value
next
```

Why?

Visibility between threads.

---

Example:

Thread A updates:

```
node.val = "Java"
```

Thread B doing:

```
get()
```

must see latest value.

---

# 8. How put() Works Internally ⭐⭐⭐⭐⭐

Example:

```java
map.put(1,"Java");
```

Flow:

```
put()


 |
 |
 v


spread hash


 |
 |
 v


Calculate bucket index


 |
 |
 v


Check bucket


```

Now three cases.

---

# Case 1: Empty Bucket

Example:

```
Bucket 5

null

```

ConcurrentHashMap tries:

```
CAS insertion
```

---

Flow:

```
Bucket empty


      |

      v


CAS(null,new Node)


      |

      v


Success


      |

      v


Done

```

---

No lock needed.

---

# Case 2: Existing Node

Example:

```
Bucket 5


Node A

 |
Node B

```

Now:

```
synchronized(first node)

```

---

Flow:

```
Bucket occupied


       |

       v


Acquire bucket lock


       |

       v


Traverse linked list


       |

       v


Insert/update


       |

       v


Release lock

```

---

# Case 3: Tree Bin

If collision becomes high:

Linked list becomes:

```
Red Black Tree
```

similar to HashMap.

---

# 9. Why Are Reads Lock-Free? ⭐⭐⭐⭐⭐

Very common question.

Question:

> Does get() acquire a lock?

Answer:

No.

---

get():

```
get(key)


   |

   v


calculate hash


   |

   v


find bucket


   |

   v


traverse nodes


   |

   v


return value

```

No synchronization.

---

Why safe?

Because:

```java
volatile
```

ensures visibility.

---

Multiple readers:

```
Thread A
   |
   v
 get()


Thread B
   |
   v
 get()


Thread C
   |
   v
 get()

```

All execute together.

---

# 10. Why Does ConcurrentHashMap Not Allow Null?

Example:

```java
map.put(null,"Java");
```

throws:

```
NullPointerException
```

---

Reason:

In concurrent environment:

Thread A:

```java
map.get(key)
```

returns:

```
null
```

What does it mean?

Possibility 1:

```
key does not exist
```

Possibility 2:

```
key exists with null value
```

Ambiguous.

---

Therefore:

ConcurrentHashMap says:

```
null means absence
```

---

# 11. Treeification in ConcurrentHashMap ⭐⭐⭐⭐⭐

Same idea as HashMap Java 8.

Threshold:

```
TREEIFY_THRESHOLD = 8
```

If bucket has:

```
> 8 nodes
```

convert:

```
Linked List

        |

        v

Red Black Tree

```

---

Example:

Before:

```
Bucket 5


A

|

B

|

C

|

D

|

E

|

F

|

G

|

H

|

I

```

After:

```
Bucket 5


       D

    /     \

   B       G

  /       /

 A       F

```

---

Complexity:

Before:

```
O(n)
```

After:

```
O(log n)
```

---

# 12. Resize Mechanism ⭐⭐⭐⭐⭐

This is a favourite senior interview question.

HashMap:

One thread resizes.

ConcurrentHashMap:

Multiple threads help resize.

---

Why?

Because resizing a huge map is expensive.

Example:

```
10 million entries

```

One thread doing everything:

slow.

---

Java 8 introduced:

```
ForwardingNode
```

---

Resize flow:

```
Thread A

starts resize


Thread B

helps resize


Thread C

helps resize

```

---

Diagram:

```
Old Table


Bucket 0
Bucket 1
Bucket 2


        |
        |
        v


New Table


Bucket 0
Bucket 1
Bucket 2

```

---

# 13. ForwardingNode ⭐⭐⭐⭐⭐

During resize:

Old bucket contains:

```
ForwardingNode
```

Meaning:

"Data moved to new table."

---

Example:

```
Old table


Bucket 5


ForwardingNode

        |
        v

New table bucket

```

---

If another thread reads:

It knows:

```
Go to new table
```

---

# 14. size() Internals ⭐⭐⭐⭐

Question:

> How does ConcurrentHashMap calculate size without locking?

HashMap:

```
counter++
```

simple.

---

ConcurrentHashMap:

Uses:

```
baseCount

+

CounterCells
```

---

Think:

Instead of one counter:

```
count

```

which causes contention:

```
100 threads
    |
    v
same variable

```

Use multiple counters:

```
CounterCell 1

CounterCell 2

CounterCell 3

CounterCell 4

```

---

Similar idea to:

LongAdder.

---

# 15. computeIfAbsent() Internals ⭐⭐⭐⭐⭐

Very common interview question.

Example:

```java
map.computeIfAbsent(
    "user1",
    k -> createUser()
);
```

---

Problem:

Without it:

```java
if(!map.containsKey(key)){

    map.put(key,value);

}

```

Race condition:

```
Thread A checks

key missing


Thread B checks

key missing


Both create value

```

---

computeIfAbsent:

```
Check key


   |

   v


If absent


   |

   v


Atomically compute


   |

   v


Insert

```

---

Guarantee:

Function executes atomically for that key.

---

# 16. ConcurrentHashMap vs HashMap Complexity

Average:

| Operation | HashMap | ConcurrentHashMap |
| --------- | ------- | ----------------- |
| get       | O(1)    | O(1)              |
| put       | O(1)    | O(1)              |
| remove    | O(1)    | O(1)              |

---

Worst case Java 8:

```
O(log n)
```

because of tree bins.

---

Important interview answer:

Do not say:

> HashMap is always O(1)

Say:

> Average O(1), worst case O(log n) in Java 8+ after treeification, otherwise O(n) for heavy collisions.

---

# 17. HashMap vs ConcurrentHashMap ⭐⭐⭐⭐⭐

| Feature       | HashMap       | ConcurrentHashMap     |
| ------------- | ------------- | --------------------- |
| Thread safe   | No            | Yes                   |
| Null key      | Yes           | No                    |
| Locking       | None          | CAS + bucket locks    |
| Reads         | Normal        | Lock-free             |
| Resize        | Single thread | Multiple threads help |
| Treeification | Yes           | Yes                   |
| Use case      | Single thread | Concurrent systems    |

---

# 18. Production Examples

## User Session Store

```
UserId -> Session

```

Choice:

```
ConcurrentHashMap
```

---

## API Cache

```
Request Key

      |

      v

Response

```

Choice:

```
ConcurrentHashMap
```

---

## Rate Limiter Counter

```
ClientId

   |

   v

Request Count

```

Choice:

```
ConcurrentHashMap
```

---

# 19. Senior Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why is ConcurrentHashMap faster than Hashtable?

Answer:

Because Hashtable uses a single lock, whereas ConcurrentHashMap allows concurrent operations using CAS, lock-free reads, and bucket-level synchronization.

---

## Q2. Are reads completely lock-free?

Answer:

Yes, normal reads do not acquire locks. They rely on volatile visibility and immutable node references.

---

## Q3. Does ConcurrentHashMap use synchronized?

Answer:

Yes.

Java 8 uses synchronized blocks on individual bins during updates, combined with CAS.

---

## Q4. How does resizing happen?

Answer:

Multiple threads can participate in resizing. ForwardingNode indicates migrated buckets.

---

## Q5. Why no null keys?

Answer:

Because null would make absence detection ambiguous during concurrent operations.

---

# Final Mental Model

```
                 ConcurrentHashMap


                         |

                         v


                  Node[] table


                         |

          -------------------------------


          Empty bucket

              |

              v

             CAS



          Existing bucket

              |

              v

        synchronized bin



          Many collisions

              |

              v

          TreeBin


          Resize

              |

              v

       ForwardingNode

              |

              v

      Multiple threads help


```

---

# Chapter Complete ✅

Covered:

✅ Java 7 Segment architecture
✅ Java 8 redesign
✅ CAS insertion
✅ Bucket locking
✅ Lock-free reads
✅ Node internals
✅ Tree bins
✅ Resize mechanism
✅ ForwardingNode
✅ CounterCells
✅ computeIfAbsent()
✅ Complexity
✅ Interview traps

---

Next Chapter:

# Chapter 31 — Iterator Framework Deep Dive ⭐⭐⭐⭐⭐

Topics:

* Iterator internal design
* Iterable vs Iterator
* Iterator vs ListIterator
* Fail-fast mechanism
* modCount
* ConcurrentModificationException
* Fail-safe iterators
* Spliterator
* Parallel streams internals

This connects Collections with Java 8 Streams and concurrency.
