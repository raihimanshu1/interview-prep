# Chapter 26 — Hashtable Deep Dive ⭐⭐⭐☆

Now we cover **Hashtable**, one of Java's oldest Map implementations.

This is important because interviews often ask:

> "Why do we still have Hashtable when we have ConcurrentHashMap?"

or:

> "Difference between HashMap, Hashtable, and ConcurrentHashMap?"

Understanding Hashtable also helps you understand the evolution of Java collections.

---

# 1. History of Hashtable

Hashtable was introduced in:

```text
Java 1.0
```

Before:

```text
Java Collections Framework
```

Even existed.

It was designed as a:

```text
Thread-safe key-value storage
```

---

Timeline:

```text
Java 1.0

Hashtable
    |
    |
Java 1.2

HashMap + Collections Framework
    |
    |
Java 5

ConcurrentHashMap
```

---

Evolution:

```text
Hashtable

(single lock)

        ↓

HashMap

(faster but not thread-safe)

        ↓

ConcurrentHashMap

(scalable concurrency)

```

---

# 2. What Is Hashtable?

Definition:

> Hashtable is a synchronized Map implementation that stores key-value pairs using hashing.

Example:

```java
Hashtable<Integer,String> table =
        new Hashtable<>();

table.put(1,"Java");
table.put(2,"Spring");
```

Internally:

```text
Hashtable


     |

     v


Hash Table Array


     |

     v


Bucket


     |

     v


Entry Linked List

```

---

# 3. Hashtable Internal Structure ⭐⭐⭐⭐☆

Similar to old HashMap.

Internally:

```java
private transient Entry<?,?>[] table;
```

Entry:

```java
class Entry<K,V>{

    int hash;

    K key;

    V value;

    Entry<K,V> next;

}

```

---

Collision handling:

```text
Bucket 5


Entry A

   |

Entry B

   |

Entry C

```

Same concept as Java 7 HashMap.

---

# 4. How Hashtable put() Works

Example:

```java
table.put("Java","17");
```

Flow:

```text
put(key,value)


       |

       v


Calculate hashCode()


       |

       v


Calculate bucket index


       |

       v


Check collision


       |

       v


Insert entry


       |

       v


Synchronize operation

```

---

Important difference:

Hashtable wraps operations with synchronization.

---

# 5. Hashtable Synchronization ⭐⭐⭐⭐⭐

Every public method is synchronized.

Example:

```java
public synchronized V get(Object key)
```

and:

```java
public synchronized V put(K key,V value)
```

---

Meaning:

Thread A:

```text
put()

 |
 LOCK
 |
 execute
 |
 UNLOCK

```

Thread B:

```text
put()

 |
 WAIT
```

---

Only one thread can access Hashtable at a time.

---

# 6. Why Is Hashtable Thread Safe?

Because every operation acquires the same monitor lock.

Example:

```java
Hashtable<Integer,String> map =
        new Hashtable<>();
```

Thread 1:

```java
map.put(1,"A");
```

Thread 2:

```java
map.put(2,"B");
```

Execution:

```text
Thread 1


LOCK Hashtable


put()


UNLOCK



Thread 2


LOCK Hashtable


put()

```

---

Thread safety comes from:

```text
synchronized methods
```

---

# 7. Problem With Hashtable ⭐⭐⭐⭐⭐

Although thread-safe:

It does not scale.

Example:

100 threads:

```text
Thread 1
Thread 2
Thread 3
...
Thread 100

```

All need:

```text
same lock
```

---

Diagram:

```text

Thread A
    |
Thread B
    |
Thread C
    |
Thread D

       |
       v

  Hashtable LOCK

       |
       v

   Only one executes

```

---

This creates:

* contention
* waiting
* poor throughput

---

# 8. Hashtable vs ConcurrentHashMap ⭐⭐⭐⭐⭐

This is a very common senior interview question.

## Hashtable

```text
One global lock

↓

Entire Map locked

```

---

## ConcurrentHashMap

Java 8:

```text
CAS

+

bucket-level synchronization

+

lock-free reads

```

---

Diagram:

Hashtable:

```text
Bucket 1
Bucket 2
Bucket 3
Bucket 4


        |
        |
    ONE LOCK

```

---

ConcurrentHashMap:

```text
Bucket 1 --> lock

Bucket 2 --> available

Bucket 3 --> lock

Bucket 4 --> available

```

---

# 9. Comparison Table ⭐⭐⭐⭐⭐

| Feature           | Hashtable   | HashMap       | ConcurrentHashMap |
| ----------------- | ----------- | ------------- | ----------------- |
| Thread safe       | Yes         | No            | Yes               |
| Introduced        | Java 1.0    | Java 1.2      | Java 5            |
| Locking           | Entire map  | None          | Fine-grained      |
| Performance       | Slow        | Fast          | Fast concurrent   |
| Null key          | Not allowed | Allowed       | Not allowed       |
| Null value        | Not allowed | Allowed       | Not allowed       |
| Recommended today | No          | Single thread | Multithread       |

---

# 10. Why Does Hashtable Not Allow Null Keys? ⭐⭐⭐⭐⭐

Example:

```java
Hashtable<String,String> map =
        new Hashtable<>();

map.put(null,"value");
```

Exception:

```text
NullPointerException
```

---

Reason:

Similar to ConcurrentHashMap.

Ambiguity.

Consider:

```java
map.get(key)
```

returns:

```text
null
```

Does it mean:

Case 1:

```text
key exists
value=null
```

or:

Case 2:

```text
key does not exist
```

---

Hashtable avoids this.

---

# 11. HashMap vs Hashtable Null Behaviour

## HashMap

Allowed:

```java
map.put(null,"Java");
```

Allowed:

```java
map.put("key",null);
```

---

## Hashtable

Not allowed:

```java
table.put(null,"Java");
```

Not allowed:

```java
table.put("key",null);
```

---

# 12. Hashtable Enumeration ⭐⭐⭐☆

Old Java used:

```java
Enumeration
```

instead of:

```java
Iterator
```

Example:

```java
Enumeration<Integer> keys =
        table.keys();

while(keys.hasMoreElements()){

    Integer key =
        keys.nextElement();

}
```

---

Modern collections use:

```java
Iterator
```

because it supports:

```java
remove()
```

---

# 13. Hashtable vs HashMap Internal Difference ⭐⭐⭐⭐☆

## HashMap

Java 8:

```text
Node[]

+
Tree bins

```

---

## Hashtable

Old style:

```text
Entry[]

+
Linked lists

```

No treeification.

---

Therefore:

Hashtable collision worst case:

```text
O(n)
```

---

HashMap Java 8:

```text
O(log n)
```

after treeification.

---

# 14. Hashtable Load Factor ⭐⭐⭐☆

Default:

```java
0.75
```

Same concept.

Formula:

```text
threshold =
capacity * loadFactor

```

Example:

Capacity:

```text
16
```

Threshold:

```text
16 * 0.75

= 12

```

After 12 entries:

resize.

---

# 15. Hashtable Resize ⭐⭐⭐☆

Hashtable doubles capacity:

Old:

```text
11
```

New:

```text
23
```

Interesting:

Hashtable historically uses:

```text
2 * oldCapacity + 1
```

---

HashMap:

```text
16 -> 32 -> 64
```

Power of two.

---

# 16. Why Should We Avoid Hashtable Today? ⭐⭐⭐⭐⭐

Question:

> "Is Hashtable completely useless?"

No.

But rarely chosen.

Reasons:

---

## 1. Poor scalability

Single lock.

---

## 2. Legacy API

Designed before modern collections.

---

## 3. Better alternatives exist

Single thread:

```java
HashMap
```

Multiple threads:

```java
ConcurrentHashMap
```

---

# 17. Real Production Example

Old code:

```java
Hashtable<String,UserSession> sessions =
        new Hashtable<>();
```

Modern replacement:

```java
ConcurrentHashMap<String,UserSession> sessions =
        new ConcurrentHashMap<>();
```

Benefits:

* better concurrency
* higher throughput
* better scalability

---

# 18. Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why is Hashtable thread-safe?

Answer:

> Hashtable synchronizes its public methods, allowing only one thread to operate on the map at a time.

---

## Q2. Why is ConcurrentHashMap preferred over Hashtable?

Answer:

> Hashtable uses a single global lock, while ConcurrentHashMap allows concurrent operations using CAS and fine-grained synchronization.

---

## Q3. Can Hashtable store null keys?

Answer:

> No. Hashtable does not allow null keys or null values.

---

## Q4. Is Hashtable faster than HashMap?

Answer:

> No. HashMap is faster in single-threaded applications because it has no synchronization overhead.

---

## Q5. Is Hashtable deprecated?

Answer:

Technically:

```text
No
```

It is not marked deprecated.

But:

```text
not recommended for new development
```

---

# 19. Complete Map Evolution

```text
                Java 1.0


              Hashtable

                  |

                  |

         synchronized everything



                Java 1.2


               HashMap

                  |

                  |

          fast but not thread-safe



                Java 5


        ConcurrentHashMap

                  |

                  |

        scalable concurrency


```

---

# 20. Final Mental Model

```text
                    Map


                     |

        --------------------------------


        HashMap       LinkedHashMap       TreeMap


          |                |                 |


      Hash Table     Hash + DLL       Red Black Tree


          |                |                 |


       O(1)             O(1)             O(log n)



                     |

                     |

                 Hashtable


                     |

                     |

             Hash Table + Global Lock


                     |

                     |

              Thread Safe but Slow


```

---

# Chapter Complete ✅

Covered:

✅ Hashtable history
✅ Internal structure
✅ Synchronization mechanism
✅ Null restrictions
✅ HashMap comparison
✅ ConcurrentHashMap comparison
✅ Performance problems
✅ Resize behaviour
✅ Why it is legacy
✅ Interview questions

---

Next Chapter:

# Chapter 27 — WeakHashMap Deep Dive ⭐⭐⭐⭐☆

Topics:

* Strong vs Weak References
* Garbage Collector interaction
* How WeakHashMap automatically removes entries
* ReferenceQueue internals
* Real production use cases
* WeakHashMap vs HashMap
* Common interview traps

This is where Java Collections connects with **JVM Garbage Collection and Memory Management**.
