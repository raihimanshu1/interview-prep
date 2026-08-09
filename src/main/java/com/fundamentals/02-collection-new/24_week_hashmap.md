# Chapter 27 — WeakHashMap Deep Dive ⭐⭐⭐⭐☆

Now we enter one of the more **advanced Java Collections topics**.

WeakHashMap is important because it connects:

* Collections Framework
* JVM Garbage Collector
* References
* Memory management
* Caching design

Senior interviews often ask:

> "How is WeakHashMap different from HashMap?"

or:

> "How does WeakHashMap remove entries automatically?"

To answer properly, you need to understand **strong vs weak references**.

---

# 1. Why Do We Need WeakHashMap?

Let's start with a problem.

Imagine a cache:

```java
Map<Key, Value> cache = new HashMap<>();
```

Example:

```java
cache.put(user, userData);
```

Internally:

```text
HashMap

     |
     v

 Entry

     |
     +------ Key
     |
     +------ Value

```

The HashMap keeps a strong reference to the key.

Meaning:

Even if nobody else uses:

```java
user
```

the object cannot be garbage collected.

---

Example:

```java
User user = new User("Himanshu");

cache.put(user, "Profile Data");


user = null;

```

Now:

```text
Application reference

        X


HashMap reference

        |
        v

       User Object

```

The object is still reachable.

GC says:

> "I cannot delete this object because HashMap still references it."

---

Problem:

Cache keeps growing.

Memory leak.

---

# 2. Solution: Weak References

Java provides:

```text
WeakReference
```

Meaning:

> "Keep this object only if somebody else needs it."

---

Example:

```java
WeakReference<User> ref =
        new WeakReference<>(user);
```

Now:

```text
Strong Reference

Application
    |
    |
    v
  User Object


Weak Reference

WeakReference
    |
    |
    v
  User Object

```

---

If application removes its reference:

```java
user = null;
```

Then:

```text
Application

     X


WeakReference

     |
     v

 User Object

```

GC can remove it.

---

# 3. What Is WeakHashMap?

Definition:

> WeakHashMap is a Map implementation where keys are stored using WeakReferences.

Meaning:

If a key is no longer strongly referenced elsewhere, its entry can automatically disappear.

---

Example:

```java
Map<User,String> map =
        new WeakHashMap<>();


User user = new User();


map.put(user,"Data");


user = null;

```

After GC:

```text
Before GC:


WeakHashMap


User Object
    |
    |
 Entry


After GC:


WeakHashMap


(empty)

```

---

# 4. Internal Structure of WeakHashMap ⭐⭐⭐⭐⭐

WeakHashMap internally uses:

```text
Hash Table

+

WeakReference Keys

+

ReferenceQueue

```

---

Architecture:

```text
                WeakHashMap


                    |

                    v


              Entry Table


                    |

                    v


        -----------------------


        Entry


          |

          +---- WeakReference(key)


          |

          +---- Value


                    |

                    v


             ReferenceQueue


```

---

# 5. Why ReferenceQueue?

Important interview question.

Question:

> "How does WeakHashMap know that a key was garbage collected?"

Answer:

Using:

```text
ReferenceQueue
```

---

Flow:

```text
Step 1

WeakHashMap stores key


        |

        v


Step 2

GC detects key is unreachable


        |

        v


Step 3

GC clears WeakReference


        |

        v


Step 4

Reference added to ReferenceQueue


        |

        v


Step 5

WeakHashMap removes entry


```

---

Complete flow:

```text
        WeakHashMap


             |
             |
             v


        WeakReference(key)


             |
             |
             v


            GC


             |
             |
             v


       ReferenceQueue


             |
             |
             v


       Remove Entry

```

---

# 6. WeakHashMap Example ⭐⭐⭐⭐⭐

Let's see practically.

```java
import java.util.*;

public class Demo {

    public static void main(String[] args) {


        Map<String,String> map =
                new WeakHashMap<>();


        String key = new String("Java");


        map.put(key,"Programming");


        System.out.println(map);


        key = null;


        System.gc();


        System.out.println(map);
    }
}

```

Possible output:

Before GC:

```text
{Java=Programming}
```

After GC:

```text
{}
```

---

Important:

GC timing is not guaranteed.

So output may vary.

---

# 7. HashMap vs WeakHashMap ⭐⭐⭐⭐⭐

| Feature            | HashMap      | WeakHashMap     |
| ------------------ | ------------ | --------------- |
| Key reference      | Strong       | Weak            |
| GC removes entries | No           | Yes             |
| Memory leak risk   | Higher       | Lower           |
| Key lifetime       | Map controls | GC controls     |
| Performance        | Faster       | Slight overhead |

---

# 8. Strong Reference vs Weak Reference ⭐⭐⭐⭐⭐

## Strong Reference

Normal Java reference.

Example:

```java
User user = new User();

```

Meaning:

```text
As long as reference exists:

Object survives

```

GC:

```
Cannot collect

```

---

## Weak Reference

Example:

```java
WeakReference<User> ref;

```

Meaning:

```
Object survives only if another strong reference exists

```

GC:

```
Can collect anytime

```

---

Comparison:

| Reference | GC Behaviour                             |
| --------- | ---------------------------------------- |
| Strong    | Never collect while reachable            |
| Soft      | Collect under memory pressure            |
| Weak      | Collect during GC if no strong reference |
| Phantom   | Used for cleanup tracking                |

---

# 9. WeakHashMap Does NOT Make Values Weak ⭐⭐⭐⭐☆

Important trap.

Example:

```java
WeakHashMap<Key,Value>
```

Only:

```text
Key
```

is weak.

Value remains strong.

---

Diagram:

```text
WeakHashMap


Entry


Key
 |
WeakReference


Value
 |
Strong Reference

```

---

Problem:

If value references key:

```java
class Value {

    Key key;

}

```

Cycle:

```text
Entry

 |
 +---- Weak Key

 |
 +---- Strong Value

             |
             v

            Key

```

The key may still survive.

---

# 10. WeakHashMap Use Cases ⭐⭐⭐⭐⭐

## 1. Metadata Cache

Example:

Framework stores metadata:

```text
Object -> Reflection Information

```

When object disappears:

Metadata also disappears.

---

## 2. Temporary Object Association

Example:

You want to attach extra information to objects:

```text
Object

   |

   v

Extra Data

```

without controlling object lifetime.

---

## 3. Proxy / Framework Internals

Used in:

* Hibernate internals
* IDE tooling
* Reflection utilities

---

# 11. WeakHashMap As Cache? ⭐⭐⭐⭐☆

Common misconception:

> "Can I use WeakHashMap as a normal cache?"

Not always.

Because:

Entries can disappear anytime.

Example:

```java
cache.get(key)

```

may return:

```text
null
```

because GC removed it.

---

For application caching usually use:

* Caffeine
* Redis
* ConcurrentHashMap based cache

---

# 12. WeakHashMap Thread Safety ⭐⭐⭐⭐☆

WeakHashMap is:

```text
NOT thread-safe
```

Example:

```java
WeakHashMap map =
new WeakHashMap();

```

Multiple threads:

```text
Thread A

put()


Thread B

remove()

```

can cause issues.

---

Need synchronization:

```java
Map<K,V> map =
Collections.synchronizedMap(
    new WeakHashMap<>()
);

```

---

# 13. WeakHashMap Complexity

Because internally it is hash based:

Average:

| Operation | Complexity |
| --------- | ---------- |
| put()     | O(1)       |
| get()     | O(1)       |
| remove()  | O(1)       |

Worst case:

```text
O(n)
```

---

# 14. WeakHashMap vs ConcurrentHashMap ⭐⭐⭐⭐⭐

| Feature            | WeakHashMap                   | ConcurrentHashMap       |
| ------------------ | ----------------------------- | ----------------------- |
| Thread safe        | No                            | Yes                     |
| Key reference      | Weak                          | Strong                  |
| GC removes entries | Yes                           | No                      |
| Use case           | Memory-sensitive associations | Shared concurrent cache |
| Performance        | Lower                         | Higher                  |

---

# 15. Interview Questions ⭐⭐⭐⭐⭐

## Q1. What is WeakHashMap?

Answer:

> WeakHashMap is a Map implementation where keys are stored using weak references, allowing garbage collection to remove entries when keys are no longer strongly referenced.

---

## Q2. How does WeakHashMap remove entries automatically?

Answer:

> JVM clears weak references during garbage collection and places them into a ReferenceQueue. WeakHashMap polls this queue and removes corresponding entries.

---

## Q3. Does WeakHashMap prevent memory leaks?

Answer:

> It helps prevent memory leaks caused by unnecessary strong references from maps, but it is not a universal cache solution.

---

## Q4. Are values weak in WeakHashMap?

Answer:

> No. Only keys are weak. Values are strongly referenced.

---

## Q5. Difference between HashMap and WeakHashMap?

Answer:

> HashMap keeps keys alive through strong references, while WeakHashMap allows keys to be garbage collected when no longer used elsewhere.

---

# 16. Complete Reference Hierarchy ⭐⭐⭐⭐⭐

Java has four reference types:

```text
                 Object Reference


                       |

        --------------------------------


        Strong          Soft          Weak          Phantom


          |              |             |              |


       Normal       Memory low      GC can       After GC

       reference    collection      collect      notification

```

---

# 17. Complete Map Family Progress

We now have:

```text
                         Map


                          |


       ------------------------------------------


       HashMap        LinkedHashMap        TreeMap


          |                |                  |


       Hashing        Hash + DLL       Red Black Tree


          |                |                  |


        O(1)             O(1)             O(log n)



                         |


                    Hashtable


                         |


                  Global Lock



                         |


                    WeakHashMap


                         |


              Weak Reference + GC

```

---

# Chapter Complete ✅

Covered:

✅ Strong vs Weak references
✅ WeakReference
✅ ReferenceQueue
✅ WeakHashMap internals
✅ GC interaction
✅ Memory leak prevention
✅ HashMap vs WeakHashMap
✅ Thread safety
✅ Production use cases
✅ Interview traps

---

Next Chapter:

# Chapter 28 — IdentityHashMap Deep Dive ⭐⭐⭐⭐☆

Topics:

* Why IdentityHashMap exists
* `==` vs `equals()`
* Identity-based comparison
* Internal implementation
* Difference from HashMap
* Serialization frameworks use cases
* Interview traps

This is a rare but very senior-level collection topic.
