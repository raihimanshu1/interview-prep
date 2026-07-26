# Module 3 — Map Framework

# Chapter 15 — HashMap Introduction + Why HashMap Exists ⭐⭐⭐⭐⭐

This is the most important chapter in the entire Collections Framework.

For a **7+ years Java developer**, HashMap is expected at a very deep level.

Interviewers do not just ask:

> "What is HashMap?"

They ask:

* Why HashMap?
* How does HashMap store data?
* How does hashing work?
* Why is lookup O(1)?
* Why is worst case O(n)?
* Why did Java 8 introduce TreeNode?
* Why is worst case O(log n)?
* What happens during collision?
* How do hashCode() and equals() work together?
* Why must keys be immutable?

We will build this from basics.

---

# 1. Why Do We Need HashMap?

Before HashMap, imagine storing data.

Example:

```java
List<User> users = new ArrayList<>();
```

Data:

```
Index       User

0           John
1           Alice
2           Bob
3           David

```

Need to find Alice.

Approach:

Search one by one.

```
Start

 |
 v

John?
 |
No

 |
 v

Alice?
 |
Yes

```

Complexity:

```
O(n)
```

Because we may scan the entire list.

---

# Problem

Can we directly jump to Alice?

Instead of:

```
0
1
2
3
4
5
6
...
```

Can we calculate where Alice should be stored?

Answer:

**Hashing**

---

# 2. HashMap Core Idea ⭐⭐⭐⭐⭐

HashMap uses:

```
Key
 |
 |
 v
Hash Function
 |
 |
 v
Index
 |
 |
 v
Bucket
 |
 |
 v
Value

```

Example:

```
Key:

"John"


        |
        v


hashCode()


        |
        v


123456


        |
        v


index calculation


        |
        v


Bucket 5


        |
        v


Store value


```

Later:

Searching John:

```
Key

 |
 v

same hashCode()

 |
 v

same bucket

 |
 v

find value

```

---

# 3. HashMap Mental Model

Think of HashMap as:

```
Array of Buckets


Bucket 0

Bucket 1

Bucket 2

Bucket 3

Bucket 4


```

Each bucket can contain entries.

Example:

```
HashMap


+-------+
|Array  |
+-------+

0
 |
 v

Bucket


1
 |
 v

Bucket


2
 |
 v

Bucket


```

---

# 4. HashMap Internal Structure ⭐⭐⭐⭐⭐

Java HashMap internally maintains:

```java
Node<K,V>[] table;
```

Meaning:

An array of Nodes.

Diagram:

```
HashMap


table[]


Index


0
 |
 v

null


1
 |
 v

Node


2
 |
 v

Node


3
 |
 v

null


```

---

Each node:

```java
static class Node<K,V>
{

    final int hash;

    final K key;

    V value;

    Node<K,V> next;

}

```

---

Visual:

```
Bucket 5


+-------+
| Node  |
+-------+
    |
    v

+-------+
| Node  |
+-------+
    |
    v

+-------+
| Node  |
+-------+

```

This is collision handling.

---

# 5. Basic HashMap Example

Code:

```java
Map<String,Integer> map =
        new HashMap<>();


map.put("John",100);

map.put("Alice",200);


System.out.println(
    map.get("John")
);

```

Internally:

## put()

```
"John"

 |
 v

hashCode()

 |
 v

bucket index

 |
 v

store Node


```

---

## get()

```
"John"

 |
 v

hashCode()

 |
 v

same bucket

 |
 v

compare key

 |
 v

return value

```

---

# 6. Why HashMap Lookup Is O(1)? ⭐⭐⭐⭐⭐

This is where your interview rejection happened.

Many candidates say:

> HashMap complexity is O(1)

But interviewer asks:

> "Worst case?"

You need to explain both.

---

## Average Case

Good hash distribution:

```
Bucket 0

Bucket 1

Bucket 2
 |
 v
John


Bucket 3

```

Only one element.

Search:

```
hash

 |

bucket

 |

element

```

Complexity:

```
O(1)

```

---

## Worst Case

Collision happens.

Example:

All keys generate same hash.

```
Bucket 5


John
 |
Alice
 |
Bob
 |
David

```

Now lookup:

Need traversal.

```
John?

 |
Alice?

 |
Bob?

 |
David?

```

Complexity:

```
O(n)

```

---

# 7. Java 7 HashMap Collision Handling

Before Java 8:

Collision structure:

```
Bucket


Node

 |
 v

Node

 |
 v

Node

 |
 v

Node


```

It used:

```
Linked List

```

---

Example:

```
Bucket 10


[A]
 |
 v
[B]
 |
 v
[C]
 |
 v
[D]


```

Searching:

```
A?

B?

C?

D?

```

Complexity:

```
O(n)

```

---

# 8. Java 8 Improvement ⭐⭐⭐⭐⭐

Java 8 introduced:

```
Treeification

```

Meaning:

When collision becomes very high:

Linked List

becomes

Red Black Tree

---

Before:

```
Bucket


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


```

After:

```
        D

      /   \

     B     F

    / \     \

   A   C     E


```

---

Now searching:

Linked List:

```
O(n)

```

Tree:

```
O(log n)

```

---

# 9. Java 8 Treeification Rules ⭐⭐⭐⭐⭐

Important interview numbers.

HashMap converts linked list to tree when:

```
TREEIFY_THRESHOLD = 8

```

Meaning:

If bucket contains more than 8 nodes:

```
List length >= 8

```

then tree conversion happens.

---

But another condition:

```
MIN_TREEIFY_CAPACITY = 64

```

Meaning:

HashMap will not treeify until table size reaches 64.

Why?

Because resizing is cheaper than creating trees.

---

Flow:

```
Collision


 |
 v


Bucket size > 8?


 |
 Yes


Table size >=64?


       |
       |
      No

       |
       v

Resize


       |
      Yes

       |
       v

Treeify


```

---

# 10. HashMap Complete Flow Diagram ⭐⭐⭐⭐⭐

## put(key,value)

```
             put()

               |

          calculate hash

               |

        calculate bucket index

               |

          bucket empty?

          /          \

        Yes           No

        |              |

 Insert Node       Collision

                       |

              compare existing keys

                       |

             Same key?

             /       \

           Yes        No

            |          |

        Replace     Add Node

                       |

              size > threshold?

                       |

                    Resize


```

---

## get(key)

```
             get()

               |

          calculate hash

               |

        find bucket

               |

        compare hash

               |

        compare equals()

               |

          return value


```

---

# 11. HashMap Complexity Table ⭐⭐⭐⭐⭐

| Operation | Average | Worst Case Java 7 | Worst Case Java 8 |
| --------- | ------- | ----------------- | ----------------- |
| put()     | O(1)    | O(n)              | O(log n)          |
| get()     | O(1)    | O(n)              | O(log n)          |
| remove()  | O(1)    | O(n)              | O(log n)          |

---

Important:

Do not say:

> HashMap is always O(1)

Correct answer:

> HashMap provides O(1) average complexity with good hashing. Worst case is O(n) due to collisions, improved to O(log n) in Java 8 using Red-Black trees.

---

# 12. Why HashMap Does Not Maintain Order?

Because HashMap focuses on:

```
Fast lookup

```

It does not maintain:

* insertion order
* sorting order

For ordering:

Use:

```
LinkedHashMap

or

TreeMap

```

---

# 13. Why HashMap Allows One Null Key?

Important interview question.

Example:

```java
map.put(null,"value");
```

Allowed.

Why?

HashMap handles null specially.

Internally:

```java
hash(null)=0
```

So null key goes to bucket 0.

---

Multiple null keys?

```java
map.put(null,"A");

map.put(null,"B");

```

Result:

```
null -> B

```

Because keys are unique.

---

# 14. Why HashMap Allows Multiple Null Values?

Example:

```java
map.put("A",null);

map.put("B",null);

```

Allowed.

Because:

Keys must be unique.

Values can repeat.

---

# 15. Why Keys Should Be Immutable ⭐⭐⭐⭐⭐

Very common question.

Bad example:

```java
class Employee {

    int id;

}

```

Suppose:

```java
Employee e = new Employee(1);


map.put(e,"John");

```

Hash:

```
hashCode = 100

```

Stored:

```
Bucket 100

```

Now:

```
e.id = 2;

```

Hash changes:

```
hashCode = 200

```

Now:

```
map.get(e)

```

Searches:

```
Bucket 200

```

But object is in:

```
Bucket 100

```

Result:

```
null

```

---

Therefore:

Keys should be:

* Immutable
* Proper hashCode()
* Proper equals()

Examples:

Good keys:

```
String

Integer

UUID

Immutable objects

```

---

# 16. HashMap Interview Boundary ⭐⭐⭐⭐⭐

For senior level, you should explain:

## Basic

* HashMap stores key-value pairs
* Uses hashing
* Uses bucket array

## Intermediate

* hashCode()
* equals()
* collisions
* linked list

## Advanced

* Java 7 vs Java 8
* Treeification
* Red-black tree
* Load factor
* Resize
* Worst-case complexity

---

# Final Mental Model

Remember:

```
HashMap


Key

 |

hashCode()

 |

Hash Function

 |

Bucket Index

 |

Bucket

 |

Node


Collision?

 |

LinkedList

 |

Large collision

 |

Red Black Tree


```

Interview one-liner:

> "HashMap uses an array of buckets and hashing to provide O(1) average lookup. Collisions are handled using linked lists, which are converted into Red-Black trees after a threshold in Java 8, improving worst-case complexity from O(n) to O(log n)."

---

# Chapter Complete ✅

Next Chapter:

# Chapter 16 — Hashing Fundamentals (hashCode, equals, hash function, bucket calculation) ⭐⭐⭐⭐⭐

We will go deeper into:

* How hashCode() actually works
* Why same hash does not mean same object
* equals contract
* Why override both hashCode and equals
* Objects.hash()
* Why Java uses 31
* Bucket index calculation
* Bit operations (`hash ^ (hash >>> 16)`)
* Interview coding examples

This is the foundation before going into HashMap internals.
