# Chapter 24 — LinkedHashMap Deep Dive ⭐⭐⭐⭐☆

Now we move from **HashMap** to **LinkedHashMap**.

This is a very common senior interview topic because it connects:

* HashMap internals
* Doubly Linked List
* Ordering
* LRU Cache Design
* Access patterns
* Java Collections design choices

Many interviewers ask:

> "How does LinkedHashMap maintain insertion order while HashMap does not?"

or:

> "Implement LRU Cache using LinkedHashMap."

Let's understand from basics.

---

# 1. Why Do We Need LinkedHashMap?

First understand the problem.

## HashMap

Example:

```java
Map<Integer,String> map = new HashMap<>();

map.put(3,"C");
map.put(1,"A");
map.put(2,"B");
```

Iteration:

Possible output:

```text
1 -> A
3 -> C
2 -> B
```

Why?

Because HashMap stores data based on:

```text
hashCode()
        |
        v
bucket index
```

It does not remember insertion sequence.

---

But sometimes we need:

```text
First inserted
       |
       v
Second inserted
       |
       v
Third inserted

```

Example:

* Browser history
* Cache eviction
* Recent items
* LRU cache

Solution:

# LinkedHashMap

---

# 2. What Is LinkedHashMap?

Definition:

> LinkedHashMap is a HashMap with an additional doubly linked list to maintain ordering.

It combines:

## HashMap

For fast lookup:

```text
O(1)
```

*

## Doubly Linked List

For maintaining order.

---

Internal structure:

```text
              LinkedHashMap


                    |

                    v


              Hash Table


                    |

        -----------------------


        Bucket 0

        Bucket 1

        Bucket 2


                    +

                    |


          Doubly Linked List



       A <----> B <----> C

```

---

# 3. LinkedHashMap Class Hierarchy

Important interview question.

```
              Map

               |

        AbstractMap

               |

             HashMap

               |

        LinkedHashMap

```

Meaning:

LinkedHashMap reuses HashMap functionality.

It does not rewrite everything.

---

# 4. HashMap Node vs LinkedHashMap Node ⭐⭐⭐⭐⭐

HashMap Node:

```java
static class Node<K,V>{

    int hash;

    K key;

    V value;

    Node<K,V> next;

}
```

---

LinkedHashMap Entry:

```java
static class Entry<K,V>
extends HashMap.Node<K,V>{

    Entry<K,V> before;

    Entry<K,V> after;

}
```

---

Extra pointers:

```text
before
  |
  v

Node

  |
  v

after

```

These maintain the linked list.

---

# 5. How Insertion Order Works ⭐⭐⭐⭐⭐

Example:

```java
LinkedHashMap<Integer,String> map =
        new LinkedHashMap<>();


map.put(1,"A");
map.put(2,"B");
map.put(3,"C");

```

Internal:

Hash table:

```
Bucket 1
 |
 A


Bucket 2
 |
 B


Bucket 3
 |
 C

```

Linked list:

```
HEAD

 |
 v

A <----> B <----> C

                        

TAIL

```

Iteration follows:

```text
HEAD → NEXT → NEXT

```

not buckets.

---

# 6. Complete put() Flow in LinkedHashMap ⭐⭐⭐⭐⭐

Example:

```java
map.put("Java",100);
```

Flow:

```
put()


 |
 v


Calculate hash


 |
 v


Find bucket


 |
 v


Create Node


 |
 v


Insert into HashMap bucket


 |
 v


Attach node into doubly linked list


 |
 v


Update tail


```

---

Diagram:

Before:

```
HEAD


A <----> B


TAIL

```

Insert C:

```
HEAD


A <----> B <----> C


                  |
                  v

                TAIL

```

---

# 7. Does LinkedHashMap Have O(1) Operations?

Yes.

Because lookup still uses HashMap.

Complexity:

| Operation | Complexity   |
| --------- | ------------ |
| put()     | O(1) average |
| get()     | O(1) average |
| remove()  | O(1) average |
| iteration | O(n)         |

---

# 8. Insertion Order vs Access Order ⭐⭐⭐⭐⭐

This is the most important LinkedHashMap feature.

Constructor:

```java
LinkedHashMap(
    int initialCapacity,
    float loadFactor,
    boolean accessOrder
)
```

The third parameter controls ordering.

---

## Case 1: accessOrder = false

Default.

Meaning:

Maintain insertion order.

Example:

```java
LinkedHashMap<Integer,String> map =
new LinkedHashMap<>();

map.put(1,"A");
map.put(2,"B");
map.put(3,"C");

```

Order:

```
1
2
3

```

---

## Case 2: accessOrder = true

Maintain access order.

Meaning:

Recently accessed entries move to the end.

Example:

Initial:

```
A <-> B <-> C

```

Access:

```java
map.get(A);
```

Result:

```
B <-> C <-> A

```

Because A became most recently used.

---

# 9. Why Access Order Exists?

Because of:

# LRU Cache

Least Recently Used cache.

Idea:

Remove the item that has not been used for the longest time.

---

Example:

Capacity:

```
3
```

Cache:

```
A B C

```

Access:

```
A

```

Now:

```
B C A

```

Insert:

```
D

```

Remove:

```
B

```

because B is least recently used.

---

# 10. Implementing LRU Cache Using LinkedHashMap ⭐⭐⭐⭐⭐

Classic interview question.

Requirement:

```
capacity = 3

put(A)

put(B)

put(C)


Cache:

A B C


get(A)


Cache:

B C A


put(D)


Remove B

```

---

Java implementation:

```java
class LRUCache<K,V>
        extends LinkedHashMap<K,V> {


    private final int capacity;


    public LRUCache(int capacity){

        super(
            capacity,
            0.75f,
            true
        );

        this.capacity = capacity;
    }


    @Override
    protected boolean removeEldestEntry(
            Map.Entry<K,V> eldest){

        return size() > capacity;
    }
}

```

---

Usage:

```java
LRUCache<Integer,String> cache =
        new LRUCache<>(3);


cache.put(1,"A");
cache.put(2,"B");
cache.put(3,"C");


cache.get(1);


cache.put(4,"D");

```

Final:

```
3
1
4

```

Entry 2 removed.

---

# 11. removeEldestEntry() ⭐⭐⭐⭐⭐

Interview question:

> "How does LinkedHashMap automatically remove old entries?"

Answer:

Using:

```java
removeEldestEntry()
```

After insertion:

```
put()


 |
 v


Add new entry


 |
 v


Call removeEldestEntry()


 |
 v


true?


 |
 v


Remove oldest

```

---

Default:

```java
protected boolean removeEldestEntry(
        Map.Entry<K,V> eldest){

    return false;
}

```

Meaning:

No automatic removal.

---

# 12. LinkedHashMap vs HashMap ⭐⭐⭐⭐⭐

| Feature   | HashMap      | LinkedHashMap            |
| --------- | ------------ | ------------------------ |
| Ordering  | No guarantee | Maintains order          |
| Structure | Hash table   | Hash table + linked list |
| Memory    | Less         | More                     |
| get()     | O(1)         | O(1)                     |
| Iteration | Bucket order | Linked list order        |
| Use case  | Fast lookup  | Ordered lookup           |

---

# 13. LinkedHashMap vs TreeMap ⭐⭐⭐⭐⭐

| Feature        | LinkedHashMap    | TreeMap        |
| -------------- | ---------------- | -------------- |
| Ordering       | Insertion/access | Sorted         |
| Data structure | Hash + DLL       | Red Black Tree |
| get()          | O(1)             | O(log n)       |
| Sorting        | No               | Yes            |

---

Example:

Need:

```
User login order

```

Use:

```
LinkedHashMap

```

Need:

```
Users sorted by ID

```

Use:

```
TreeMap

```

---

# 14. Internal Removal Flow ⭐⭐⭐⭐☆

Example:

Remove:

```java
map.remove(2);
```

Flow:

```
remove(key)


 |
 v


Find bucket


 |
 v


Remove HashMap node


 |
 v


Remove from doubly linked list


 |
 v


Reconnect neighbors


```

Before:

```
A <----> B <----> C

```

Remove B:

```
A <------------> C

```

---

# 15. Thread Safety ⭐⭐⭐⭐☆

Important:

LinkedHashMap is NOT thread-safe.

Example:

```java
LinkedHashMap<Integer,String> map =
        new LinkedHashMap<>();

```

Multiple threads:

```
Thread A

put()


Thread B

remove()

```

can corrupt:

* Hash table
* Linked list pointers

---

Solutions:

## Option 1

Synchronization wrapper:

```java
Collections.synchronizedMap(
    new LinkedHashMap<>()
);

```

---

## Option 2

Use concurrent cache libraries:

Examples:

* Caffeine
* ConcurrentHashMap based designs

---

# 16. LinkedHashMap Memory Cost ⭐⭐⭐⭐☆

Compared to HashMap:

Extra fields:

```java
before

after

```

Every entry stores two additional references.

Memory:

```
HashMap Node

hash
key
value
next


LinkedHashMap Entry

hash
key
value
next
before
after

```

---

Tradeoff:

More memory

but

ordered iteration.

---

# 17. Interview Traps ⭐⭐⭐⭐⭐

## Q1. How does LinkedHashMap maintain insertion order?

Answer:

> It maintains a doubly linked list connecting all entries. Iteration happens through this list instead of directly through buckets.

---

## Q2. Does LinkedHashMap use a tree when collisions increase?

Answer:

Yes.

Because it extends HashMap.

Java 8 treeification behaviour still applies.

---

## Q3. Is LinkedHashMap slower than HashMap?

Answer:

Slightly slower because every insertion/removal updates the linked list.

But lookup complexity remains O(1).

---

## Q4. How do you implement LRU cache?

Answer:

> Use LinkedHashMap with accessOrder=true and override removeEldestEntry().

---

## Q5. Difference between insertion order and access order?

Answer:

Insertion:

```
Order of put()

```

Access:

```
Order of recent usage

```

---

# 18. Complete Mental Model

```
                  LinkedHashMap


                         |

              ---------------------

              |                   |

          HashMap             Doubly Linked List


              |                   |

       Fast lookup          Maintains order


              |                   |

              +-------------------+


                    Result:


             O(1) lookup

             Ordered iteration

             LRU support


```

---

# Chapter Complete ✅

Covered:

✅ Why LinkedHashMap exists
✅ Internal structure
✅ HashMap + Doubly Linked List
✅ Node differences
✅ Insertion order
✅ Access order
✅ LRU Cache implementation
✅ removeEldestEntry()
✅ Complexity
✅ Memory trade-offs
✅ Interview questions

---

Next Chapter:

# Chapter 25 — TreeMap Deep Dive ⭐⭐⭐⭐☆

Topics:

* Why TreeMap exists
* Red Black Tree internals
* Sorted ordering
* Comparator vs Comparable
* Custom sorting
* TreeMap complexity O(log n)
* Null key behaviour
* HashMap vs TreeMap vs LinkedHashMap
* Real production use cases

This will continue the **Map family deep dive**.
