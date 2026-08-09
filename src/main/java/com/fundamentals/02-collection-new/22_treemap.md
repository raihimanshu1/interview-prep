# Chapter 25 — TreeMap Deep Dive ⭐⭐⭐⭐☆

Now we move from **Hash-based Maps** to **Sorted Maps**.

So far:

```text
HashMap
 |
 |-- Fast lookup
 |-- No ordering
 |-- O(1) average


LinkedHashMap
 |
 |-- Fast lookup
 |-- Maintains insertion/access order
 |-- O(1) average


TreeMap
 |
 |-- Sorted ordering
 |-- O(log n)
 |-- Red Black Tree

```

TreeMap is one of the most important collections for senior interviews because it tests:

* Data structure understanding
* Binary Search Tree concepts
* Comparator design
* Time complexity
* Ordering requirements

---

# 1. Why Do We Need TreeMap?

Consider:

```java
Map<Integer,String> map = new HashMap<>();

map.put(50,"Fifty");
map.put(10,"Ten");
map.put(30,"Thirty");
```

HashMap output:

```
50
10
30
```

No guarantee.

---

Now requirement:

> "I need keys always sorted."

Example:

```
10 -> Ten
30 -> Thirty
50 -> Fifty
```

HashMap cannot provide this.

Solution:

# TreeMap

---

# 2. What Is TreeMap?

Definition:

> TreeMap is a Map implementation that stores keys in sorted order using a Red Black Tree.

Internally:

```
TreeMap

      |
      v

Red Black Tree

      |
      v

Sorted Keys

```

---

Example:

```java
TreeMap<Integer,String> map =
        new TreeMap<>();

map.put(50,"Fifty");
map.put(10,"Ten");
map.put(30,"Thirty");

```

Internal tree:

```
          30

        /    \

      10      50

```

Iteration:

```
10
30
50

```

---

# 3. TreeMap Class Hierarchy

Important interview question.

```
                Map

                 |

          SortedMap

                 |

        NavigableMap

                 |

             TreeMap

```

TreeMap implements:

```java
SortedMap
NavigableMap

```

---

# 4. Internal Data Structure ⭐⭐⭐⭐⭐

TreeMap internally stores:

```java
Entry<K,V>

```

Structure:

```java
static class Entry<K,V>{

    K key;

    V value;

    Entry<K,V> left;

    Entry<K,V> right;

    Entry<K,V> parent;

    boolean color;

}

```

---

Diagram:

```
              Entry


                |

       ----------------

       |              |

     left          right


                |

             parent


                |

             color


```

Why color?

Because it is a:

# Red Black Tree

---

# 5. Why Red Black Tree?

A normal Binary Search Tree can become unbalanced.

Example:

Insert:

```
10
20
30
40
50

```

Normal BST:

```
10

 \
 20

  \
   30

    \
     40

      \
       50

```

Looks like linked list.

Search:

```
O(n)

```

---

Red Black Tree balances itself.

Result:

```
        30

      /    \

    20      40

   /          \

 10            50

```

Height remains:

```
log(n)

```

Therefore:

```
get()
put()
remove()

= O(log n)

```

---

# 6. TreeMap Put Operation Internals ⭐⭐⭐⭐⭐

Example:

```java
map.put(40,"A");

```

Flow:

```
put(key,value)


       |

       v


Tree empty?


       |

       v


Create root


```

---

Insert second value:

```java
map.put(20,"B");

```

Compare:

```
20 < 40

go left

```

Tree:

```
        40

       /

     20

```

---

Insert:

```java
map.put(60,"C");

```

Comparison:

```
60 > 40

go right

```

Tree:

```
          40

        /    \

      20      60

```

---

# 7. Searching in TreeMap ⭐⭐⭐⭐⭐

Example:

```java
map.get(60);

```

Search:

```
          40

           \

            60

```

Steps:

1. Compare 60 with root 40

```
60 > 40

```

Go right.

2. Found 60.

---

Complexity:

```
O(log n)

```

---

# 8. Comparator vs Comparable ⭐⭐⭐⭐⭐

Very common interview question.

Question:

> "How does TreeMap know how to sort keys?"

Answer:

Using:

1. Comparable
2. Comparator

---

# 9. Comparable

Object defines its natural ordering.

Example:

```java
class Employee implements Comparable<Employee>{

    int id;


    public int compareTo(Employee e){

        return this.id - e.id;

    }

}

```

Now:

```java
TreeMap<Employee,String> map =
        new TreeMap<>();

```

TreeMap uses:

```
compareTo()

```

---

# 10. Comparator

External sorting logic.

Example:

Sort employees by salary.

```java
Comparator<Employee> salaryComparator =
        (e1,e2) ->
        e1.salary - e2.salary;


TreeMap<Employee,String> map =
        new TreeMap<>(salaryComparator);

```

---

Flow:

```
Insert key


    |

    v


Comparator.compare()


    |

    v


Left or Right child?


```

---

# 11. Comparable vs Comparator

| Comparable        | Comparator             |
| ----------------- | ---------------------- |
| Inside class      | Outside class          |
| Natural ordering  | Custom ordering        |
| compareTo()       | compare()              |
| One sorting logic | Multiple sorting logic |

---

Example:

Employee:

Natural:

```
sort by id

```

Different requirement:

```
sort by salary

```

Use Comparator.

---

# 12. TreeMap Null Key Behaviour ⭐⭐⭐⭐⭐

Important interview trap.

Question:

> "Can TreeMap have null keys?"

Answer:

No (with natural ordering).

Example:

```java
TreeMap<Integer,String> map =
        new TreeMap<>();

map.put(null,"value");

```

Throws:

```
NullPointerException

```

---

Why?

Because TreeMap needs comparison:

```java
compare(key1,key2)

```

How to compare:

```
null < 10 ?

```

Not defined.

---

# 13. Can TreeMap Have Null Values?

Yes.

Example:

```java
map.put(1,null);

```

Allowed.

Because sorting depends only on keys.

---

# 14. TreeMap Navigation Methods ⭐⭐⭐⭐⭐

Because TreeMap implements NavigableMap.

Example:

```java
TreeMap<Integer,String> map =
new TreeMap<>();

map.put(10,"A");
map.put(20,"B");
map.put(30,"C");

```

---

## firstKey()

```java
map.firstKey();

```

Output:

```
10

```

---

## lastKey()

```java
map.lastKey();

```

Output:

```
30

```

---

## higherKey()

Greater than key.

```java
map.higherKey(20);

```

Output:

```
30

```

---

## lowerKey()

Smaller than key.

```java
map.lowerKey(20);

```

Output:

```
10

```

---

# 15. Real Production Use Cases ⭐⭐⭐⭐⭐

## 1. Leaderboard

Need:

```
Highest score first

```

TreeMap:

```
score -> players

```

---

## 2. Time-based events

Example:

```
timestamp -> event

```

Need:

```
next event after time X

```

Use:

```java
higherKey(timestamp)

```

---

## 3. Database indexes

Sorted lookup:

```
key ranges

```

---

## 4. Rate limiting windows

Example:

```
timestamp -> request count

```

Need:

```
remove old timestamps

```

---

# 16. TreeMap vs HashMap ⭐⭐⭐⭐⭐

| Feature       | HashMap      | TreeMap        |
| ------------- | ------------ | -------------- |
| Ordering      | No           | Sorted         |
| Structure     | Hash table   | Red Black Tree |
| get()         | O(1) average | O(log n)       |
| put()         | O(1) average | O(log n)       |
| Null key      | Allowed      | Not allowed    |
| Memory        | Less         | More           |
| Range queries | No           | Yes            |

---

# 17. TreeMap vs LinkedHashMap ⭐⭐⭐⭐⭐

| Feature        | LinkedHashMap   | TreeMap        |
| -------------- | --------------- | -------------- |
| Ordering       | Insertion order | Sorted order   |
| Data structure | Hash + DLL      | Red Black Tree |
| Lookup         | O(1)            | O(log n)       |
| Range queries  | No              | Yes            |

---

Example:

Need:

```
Recently viewed products

```

Use:

```
LinkedHashMap

```

Need:

```
Products sorted by price

```

Use:

```
TreeMap

```

---

# 18. Range Query Example ⭐⭐⭐⭐⭐

Suppose:

```java
TreeMap<Integer,String> map =
new TreeMap<>();

map.put(100,"A");
map.put(200,"B");
map.put(300,"C");
map.put(400,"D");

```

Need:

```
All values between 150 and 350

```

Use:

```java
map.subMap(150,350);

```

Result:

```
200
300

```

---

HashMap cannot efficiently do this.

---

# 19. TreeMap Complexity ⭐⭐⭐⭐⭐

Because height of Red Black Tree:

```
log n

```

Operations:

| Operation     | Complexity |
| ------------- | ---------- |
| put()         | O(log n)   |
| get()         | O(log n)   |
| remove()      | O(log n)   |
| containsKey() | O(log n)   |
| firstKey()    | O(log n)   |
| lastKey()     | O(log n)   |

---

# 20. Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why is TreeMap slower than HashMap?

Answer:

> HashMap uses hashing for direct bucket access giving O(1) average lookup, while TreeMap performs tree traversal requiring O(log n).

---

## Q2. How does TreeMap maintain sorting?

Answer:

> TreeMap stores entries in a Red Black Tree and uses Comparable or Comparator to compare keys.

---

## Q3. Can TreeMap store custom objects?

Yes.

But object must provide:

Either:

```java
Comparable

```

or:

```java
Comparator

```

---

## Q4. Why does TreeMap not allow null keys?

Answer:

> Because keys need to be compared to maintain ordering, and null cannot participate in comparison.

---

## Q5. HashMap vs TreeMap when would you choose?

Answer:

> Use HashMap when fast lookup is required. Use TreeMap when sorted ordering or range queries are required.

---

# 21. Complete Map Family Mental Model ⭐⭐⭐⭐⭐

```
                         Map


                          |

          --------------------------------


          HashMap        LinkedHashMap       TreeMap


             |                 |                |


       Hash Table       Hash + DLL       Red Black Tree


             |                 |                |


          O(1)             O(1)             O(log n)


             |                 |                |


     Fast lookup       Ordered data       Sorted data



```

---

# Chapter Complete ✅

Covered:

✅ Why TreeMap exists
✅ Internal Red Black Tree structure
✅ TreeMap hierarchy
✅ put/get internals
✅ Comparable
✅ Comparator
✅ Null behaviour
✅ Navigation APIs
✅ Range queries
✅ Complexity
✅ HashMap vs LinkedHashMap vs TreeMap
✅ Production use cases

---

Next Chapter:

# Chapter 26 — Hashtable Deep Dive ⭐⭐⭐☆

Topics:

* Why Hashtable exists
* Why it is legacy
* Synchronization mechanism
* Hashtable vs HashMap
* Hashtable vs ConcurrentHashMap
* Null key/value restrictions
* Why modern applications avoid Hashtable

This completes the older Map implementations before moving into:
**WeakHashMap, IdentityHashMap, EnumMap, ConcurrentHashMap deep dives.**
