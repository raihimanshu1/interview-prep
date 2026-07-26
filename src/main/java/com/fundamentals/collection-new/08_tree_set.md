# Chapter 8 — TreeSet Deep Dive ⭐⭐⭐⭐⭐

TreeSet is one of the most important Set implementations because it introduces:

* **Tree data structure**
* **Binary Search Tree**
* **Red-Black Tree balancing**
* **Comparable**
* **Comparator**
* **O(log n) complexity**

This chapter is especially important because of your earlier interview experience:

> "HashMap complexity is O(1) but interviewer expected worst case O(log n)."

TreeSet is the collection where **O(log n)** is the normal expected complexity.

---

# 1. Why Do We Need TreeSet?

Let's compare the three Set implementations.

Suppose:

```java
Set<Integer> numbers = ???

numbers.add(50);
numbers.add(10);
numbers.add(30);
numbers.add(20);
```

---

## HashSet

Output:

```
50
20
10
30
```

No ordering.

Purpose:

```
Fast lookup
```

---

## LinkedHashSet

Output:

```
50
10
30
20
```

Insertion order.

Purpose:

```
Preserve insertion sequence
```

---

## TreeSet

Output:

```
10
20
30
50
```

Sorted order.

Purpose:

```
Maintain sorted data
```

---

# 2. TreeSet Definition

TreeSet is:

> A Set implementation that stores unique elements in sorted order using a Red-Black Tree internally.

Example:

```java
Set<Integer> set = new TreeSet<>();

set.add(50);
set.add(10);
set.add(30);

System.out.println(set);
```

Output:

```
[10, 30, 50]
```

---

# 3. TreeSet Internal Structure ⭐⭐⭐⭐⭐

Most important interview point:

```
TreeSet internally uses TreeMap
```

Similar relationship:

```
HashSet
    |
    v
HashMap


LinkedHashSet
    |
    v
LinkedHashMap


TreeSet
    |
    v
TreeMap

```

---

Internal:

```
TreeSet


      |
      v


TreeMap


+----------------+
| Key            |
| Value          |
+----------------+

10   PRESENT
20   PRESENT
30   PRESENT


       |
       v


Red Black Tree

```

---

# 4. TreeSet Class Structure

Simplified:

```java
public class TreeSet<E>
extends AbstractSet<E>
implements NavigableSet<E>
{

    private transient NavigableMap<E,Object> m;

}
```

Internally:

```java
TreeMap<E,Object>
```

---

Like HashSet:

TreeSet stores:

```
Element -> PRESENT
```

The value is dummy.

---

# 5. Why Red-Black Tree?

A normal Binary Search Tree can become unbalanced.

Example:

Insert sorted data:

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

This becomes like a linked list.

Searching:

```
O(n)
```

---

Red-Black Tree keeps itself balanced.

Balanced tree:

```
          30

        /    \

      20      50

     /       /

   10      40

```

Height remains:

```
log(n)
```

Therefore:

```
Search = O(log n)
Insert = O(log n)
Delete = O(log n)

```

---

# 6. Red-Black Tree Properties ⭐⭐⭐⭐⭐

You do not need implementation details usually, but senior interviews may ask.

A Red-Black Tree follows:

## Rule 1

Every node has a color:

```
RED
BLACK
```

---

## Rule 2

Root is always black.

---

## Rule 3

No two red nodes can be adjacent.

Example invalid:

```
      20(B)

      |

      10(R)

      |

       5(R)

```

---

## Rule 4

Every path from root to leaf has the same number of black nodes.

---

These rules maintain balance.

---

# 7. TreeSet Add Operation Flow ⭐⭐⭐⭐⭐

Example:

```java
treeSet.add(30);
```

Flow:

```
add(30)


 |
 v


TreeSet


 |
 v


TreeMap.put(30,PRESENT)


 |
 v


Compare with existing nodes


 |
 v


Find correct position


 |
 v


Insert node


 |
 v


Balance tree if required

```

---

Example:

Insert:

```
50
```

Tree:

```
50

```

---

Insert:

```
30
```

Comparison:

```
30 < 50

go left

```

Tree:

```
     50

    /

  30

```

---

Insert:

```
70
```

Tree:

```
       50

     /    \

   30      70

```

---

# 8. Duplicate Detection in TreeSet

Example:

```java
set.add(30);

set.add(30);
```

How does TreeSet know duplicate?

It uses:

```
compareTo()
```

or

```
Comparator.compare()
```

---

Flow:

```
Insert 30


    |

Compare with existing node


    |

compareTo() returns 0


    |

Duplicate


    |

Reject insertion

```

---

Important:

TreeSet does NOT primarily use:

```
equals()
hashCode()
```

Unlike HashSet.

---

# 9. Comparable Interface ⭐⭐⭐⭐⭐

Comparable defines natural ordering.

Example:

```java
class Employee implements Comparable<Employee>{

    int id;

    public int compareTo(Employee e){

        return this.id - e.id;

    }
}
```

Usage:

```java
TreeSet<Employee> employees =
        new TreeSet<>();
```

Now TreeSet sorts by:

```
id
```

---

# 10. Comparator Interface ⭐⭐⭐⭐⭐

Sometimes we want different sorting.

Example:

Employee:

```
id
name
salary

```

Sort by salary:

```java
TreeSet<Employee> employees =
    new TreeSet<>(
        Comparator.comparing(Employee::getSalary)
    );

```

---

Difference:

| Comparable       | Comparator             |
| ---------------- | ---------------------- |
| Natural ordering | Custom ordering        |
| Inside class     | Separate object        |
| compareTo()      | compare()              |
| One sorting rule | Multiple sorting rules |

---

# 11. TreeSet Complexity ⭐⭐⭐⭐⭐

Because Red-Black Tree height is log(n):

| Operation  | Complexity |
| ---------- | ---------- |
| add()      | O(log n)   |
| remove()   | O(log n)   |
| contains() | O(log n)   |
| first()    | O(log n)   |
| last()     | O(log n)   |

---

Compare:

| Collection    | contains     |
| ------------- | ------------ |
| HashSet       | O(1) average |
| LinkedHashSet | O(1) average |
| TreeSet       | O(log n)     |

---

# 12. HashSet vs TreeSet ⭐⭐⭐⭐⭐

| Feature   | HashSet      | TreeSet        |
| --------- | ------------ | -------------- |
| Internal  | HashMap      | TreeMap        |
| Structure | Hash table   | Red-black tree |
| Ordering  | No           | Sorted         |
| contains  | O(1) average | O(log n)       |
| Null      | Allowed      | Not allowed    |
| Memory    | Less         | More           |
| Use case  | Fast lookup  | Sorted data    |

---

# 13. LinkedHashSet vs TreeSet

| Feature    | LinkedHashSet        | TreeSet          |
| ---------- | -------------------- | ---------------- |
| Order      | Insertion            | Sorted           |
| Internal   | LinkedHashMap        | TreeMap          |
| Complexity | O(1)                 | O(log n)         |
| Memory     | Higher               | Higher           |
| Use case   | Preserve input order | Maintain sorting |

---

# 14. Null Handling ⭐⭐⭐⭐

HashSet:

```java
set.add(null);
```

Allowed.

---

TreeSet:

```java
TreeSet<Integer> set =
        new TreeSet<>();

set.add(null);
```

Throws:

```
NullPointerException
```

Why?

Because TreeSet needs comparison:

```
null.compareTo(value)
```

Impossible.

---

# 15. Custom Object Example ⭐⭐⭐⭐⭐

Employee:

```java
class Employee {

    int id;
    String name;

}
```

Trying:

```java
TreeSet<Employee> employees =
        new TreeSet<>();

employees.add(new Employee());
```

Problem:

TreeSet does not know ordering.

Exception:

```
ClassCastException
```

Need:

Option 1:

Implement Comparable.

```java
class Employee 
implements Comparable<Employee>
```

or

Option 2:

Provide Comparator.

---

# 16. TreeSet Navigation Methods

Because TreeSet implements:

```
NavigableSet
```

It provides powerful methods.

Example:

```java
TreeSet<Integer> set =
    new TreeSet<>();

set.add(10);
set.add(20);
set.add(30);
```

---

## first()

```java
set.first();
```

Output:

```
10
```

---

## last()

```java
set.last();
```

Output:

```
30
```

---

## higher()

Greater than given value.

```java
set.higher(20);
```

Output:

```
30
```

---

## lower()

Less than given value.

```java
set.lower(20);
```

Output:

```
10
```

---

## ceiling()

Greater than or equal.

```java
set.ceiling(20);
```

Output:

```
20
```

---

## floor()

Less than or equal.

```java
set.floor(20);
```

Output:

```
20
```

---

# 17. Real Production Examples

## 1. Leaderboard

Need:

```
Players sorted by score
```

TreeSet:

```
1000
950
900
```

---

## 2. Scheduler

Need:

```
Tasks ordered by execution time
```

TreeSet can maintain:

```
10:00
10:05
10:10
```

---

## 3. Range Queries

Example:

Find users between:

```
age 20 and 30
```

TreeSet navigation helps.

---

# 18. Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. How does TreeSet maintain sorting?

Answer:

> TreeSet internally uses TreeMap, which stores elements in a Red-Black Tree. The tree uses comparisons through Comparable or Comparator to maintain sorted order.

---

## Q2. Why TreeSet complexity is O(log n)?

Answer:

> Because it uses a balanced Red-Black Tree where the height remains logarithmic relative to the number of elements.

---

## Q3. Does TreeSet use hashCode()?

Answer:

No.

It uses:

```
compareTo()
```

or

```
Comparator.compare()
```

---

## Q4. Difference between HashSet and TreeSet?

Answer:

> HashSet provides faster average lookup using hashing but no ordering. TreeSet maintains sorted order using a balanced tree with O(log n) operations.

---

## Q5. Can TreeSet store custom objects?

Yes, but objects must provide ordering:

Either:

```
Comparable
```

or

```
Comparator
```

---

# 19. Complete Set Comparison

| Feature   | HashSet  | LinkedHashSet | TreeSet  |
| --------- | -------- | ------------- | -------- |
| Internal  | HashMap  | LinkedHashMap | TreeMap  |
| Ordering  | None     | Insertion     | Sorted   |
| Duplicate | No       | No            | No       |
| Lookup    | O(1) avg | O(1) avg      | O(log n) |
| Null      | Yes      | Yes           | No       |
| Memory    | Low      | Medium        | Higher   |

---

# Final Mental Model

Remember:

```
Set Family


HashSet

     |
     |
     v

HashMap

Fast lookup


----------------


LinkedHashSet

     |
     |
     v

LinkedHashMap

Fast lookup + insertion order


----------------


TreeSet

     |
     |
     v

TreeMap

Sorted data + O(log n)

```

---

# Chapter Complete ✅

Next Chapter:

# Chapter 9 — EnumSet Deep Dive ⭐⭐⭐⭐☆

Topics:

* Why EnumSet exists
* Internal bit vector representation
* RegularEnumSet vs JumboEnumSet
* Why it is extremely fast
* Memory optimization
* EnumSet vs HashSet
* Real production examples
* Interview questions

This is a smaller topic but a favourite in senior interviews because it tests whether you understand specialized collections and memory optimization.
