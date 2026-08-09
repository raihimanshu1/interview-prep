# Chapter 36 — TreeMap + TreeSet Deep Dive ⭐⭐⭐⭐

Now we cover the **sorted collections family**.

The main idea:

> Hash-based collections give fast lookup.
> Tree-based collections give sorted data.

Java provides:

```text
TreeMap  → key-value sorted collection

TreeSet  → unique sorted collection
```

Both internally use:

```text
Red Black Tree
```

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

```text
50
10
30
```

No ordering guarantee.

---

Requirement:

> Give me keys always sorted.

Example:

```text
10
30
50
```

Solution:

```java
TreeMap
```

---

# 2. TreeMap Internal Structure ⭐⭐⭐⭐⭐

TreeMap is implemented using:

```text
Red Black Tree
```

Structure:

```text
                 50

              /      \

            30        70

          /   \

        10     40

```

Each node contains:

```java
class Entry<K,V>{

    K key;

    V value;

    Entry left;

    Entry right;

    Entry parent;

    boolean color;

}
```

---

# 3. Why Red Black Tree?

A normal Binary Search Tree can become unbalanced.

Example:

Insert:

```text
10
20
30
40
50
```

Normal BST:

```text
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

It becomes like a linked list.

Search:

```text
O(n)
```

---

Red Black Tree keeps itself balanced.

Balanced tree:

```text
          30

       /      \

     20        50

    /         /

  10        40

```

Height remains:

```text
O(log n)
```

---

# 4. TreeMap Operations Complexity ⭐⭐⭐⭐⭐

Because it is a balanced tree:

| Operation   | Complexity |
| ----------- | ---------- |
| put         | O(log n)   |
| get         | O(log n)   |
| remove      | O(log n)   |
| containsKey | O(log n)   |
| firstKey    | O(log n)   |
| lastKey     | O(log n)   |

---

# 5. TreeMap put() Flow

Example:

```java
map.put(25,"Java");
```

Flow:

```text
Insert key


     |

     v


Compare with root


     |

     v


Smaller?

go left


Greater?

go right


     |

     v


Insert node


     |

     v


Balance tree


```

---

Comparison:

```java
compareTo()

or

Comparator
```

decides placement.

---

# 6. Comparable vs Comparator ⭐⭐⭐⭐⭐

Very common interview question.

---

## Comparable

Used when class has natural ordering.

Example:

Employee sorted by id.

```java
class Employee 
implements Comparable<Employee>{


    int id;


    public int compareTo(Employee e){

        return this.id - e.id;
    }

}
```

Usage:

```java
TreeSet<Employee> set =
        new TreeSet<>();
```

---

## Comparator

Used when we need custom sorting.

Example:

Sort employees by salary.

```java
Comparator<Employee> salaryComparator =
        (e1,e2) ->
        e1.salary - e2.salary;


TreeSet<Employee> set =
        new TreeSet<>(salaryComparator);
```

---

Difference:

| Comparable       | Comparator         |
| ---------------- | ------------------ |
| Inside class     | Outside class      |
| Natural ordering | Custom ordering    |
| compareTo()      | compare()          |
| One ordering     | Multiple orderings |

---

# 7. Important TreeMap Example

```java
TreeMap<Integer,String> map =
        new TreeMap<>();

map.put(30,"C");
map.put(10,"A");
map.put(20,"B");


System.out.println(map);
```

Output:

```text
10=A
20=B
30=C
```

---

# 8. Useful TreeMap Methods ⭐⭐⭐⭐

## firstKey()

```java
map.firstKey();
```

Returns:

```text
smallest key
```

---

## lastKey()

```java
map.lastKey();
```

Returns:

```text
largest key
```

---

## ceilingKey()

Smallest key greater than or equal.

Example:

```java
map.ceilingKey(25);
```

Data:

```text
10 20 30 40
```

Output:

```text
30
```

---

## floorKey()

Largest key smaller than or equal.

```java
map.floorKey(25);
```

Output:

```text
20
```

---

# 9. TreeSet Deep Dive ⭐⭐⭐⭐

TreeSet:

> Stores unique elements in sorted order.

Internally:

```text
TreeSet

   |

   v

TreeMap

```

Actually:

```java
TreeSet<E>
```

uses:

```java
TreeMap<E,Object>
```

internally.

---

Example:

```java
TreeSet<Integer> set =
        new TreeSet<>();

set.add(30);
set.add(10);
set.add(20);
```

Output:

```text
10
20
30
```

---

# 10. TreeSet Complexity

Same as TreeMap.

| Operation | Complexity |
| --------- | ---------- |
| add       | O(log n)   |
| remove    | O(log n)   |
| contains  | O(log n)   |

---

# 11. HashSet vs TreeSet ⭐⭐⭐⭐⭐

Very common.

| HashSet      | TreeSet                                    |
| ------------ | ------------------------------------------ |
| Hash table   | Red Black Tree                             |
| No ordering  | Sorted order                               |
| O(1) average | O(log n)                                   |
| Allows null  | Usually one null (natural ordering issues) |
| Faster       | Slower                                     |

---

Example:

HashSet:

```text
5
1
3
```

TreeSet:

```text
1
3
5
```

---

# 12. TreeMap vs HashMap ⭐⭐⭐⭐⭐

| HashMap       | TreeMap            |
| ------------- | ------------------ |
| No ordering   | Sorted keys        |
| O(1) average  | O(log n)           |
| Hashing       | Red Black Tree     |
| Uses hashCode | Uses compareTo     |
| Faster lookup | Ordered operations |

---

# 13. Important Interview Scenario

Question:

> You need to store user transactions and frequently find transactions between dates. Which collection?

Wrong:

```text
HashMap
```

Better:

```text
TreeMap
```

Why?

Because:

```java
subMap()

headMap()

tailMap()
```

are available.

---

Example:

```java
TreeMap<Integer,String> transactions =
        new TreeMap<>();

transactions.subMap(100,200);
```

Returns:

```text
transactions between 100 and 200
```

---

# 14. Common Mistake With TreeMap Keys

TreeMap does NOT use:

```java
hashCode()

equals()
```

for ordering.

It uses:

```java
compareTo()

or

Comparator
```

---

Example:

```java
TreeMap<Employee,String> map =
        new TreeMap<>();
```

If Employee does not implement Comparable:

Runtime error:

```text
ClassCastException
```

---

# 15. Duplicate Handling in TreeSet ⭐⭐⭐⭐⭐

Important.

TreeSet decides duplicate based on:

```java
compareTo()
```

NOT equals().

Example:

```java
compareTo() returns 0
```

TreeSet thinks:

"Same object"

and rejects insertion.

---

Example:

```java
Employee(101,"John")

Employee(101,"Mike")
```

If compareTo:

```java
return id comparison;
```

Both ids same.

Second object will not be stored.

---

# 16. When To Choose Which Collection?

## Need fastest lookup

Use:

```text
HashMap / HashSet
```

Complexity:

```text
O(1)
```

---

## Need sorted data

Use:

```text
TreeMap / TreeSet
```

Complexity:

```text
O(log n)
```

---

## Need insertion order

Use:

```text
LinkedHashMap / LinkedHashSet
```

---

# 17. Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why TreeMap is slower than HashMap?

Answer:

> HashMap uses hashing and gives O(1) average lookup. TreeMap maintains sorted order using a Red Black Tree, requiring O(log n) operations.

---

## Q2. How does TreeMap maintain sorting?

Answer:

> It uses a Red Black Tree and compares keys using Comparable or Comparator.

---

## Q3. Does TreeMap use hashCode()?

Answer:

No.

It uses:

```text
compareTo()

or Comparator
```

---

## Q4. Difference between TreeSet and HashSet?

Answer:

> TreeSet provides sorted unique elements using Red Black Tree with O(log n), while HashSet provides unique elements using hashing with O(1) average complexity.

---

## Q5. Why can TreeSet remove duplicate objects?

Answer:

Because if comparator returns zero, TreeSet considers them equal.

---

# Final Mental Model

```text
                 Sorted Collection


                       |

                       v


              TreeMap / TreeSet


                       |

                       v


              Red Black Tree


                       |

                       v


        Self Balanced Binary Search Tree


                       |

                       v


                 O(log n)


```

---

# Chapter Complete ✅

Covered:

✅ TreeMap architecture
✅ Red Black Tree basics
✅ Why O(log n)
✅ Comparable
✅ Comparator
✅ TreeSet internals
✅ HashMap vs TreeMap
✅ HashSet vs TreeSet
✅ Range queries
✅ Interview questions

---

Next (keeping pace, not over-stretching):

# Chapter 37 — Queue Family: PriorityQueue, ArrayDeque, BlockingQueue ⭐⭐⭐⭐

Focus:

* Heap internals
* PriorityQueue complexity
* Stack replacement using Deque
* Producer-consumer connection
* Interview scenarios
* When to choose which queue
