# Chapter 19 — HashMap `get()` Internal Execution Deep Dive ⭐⭐⭐⭐⭐

We covered `put()` internally.

Now we understand the reverse operation:

```java
map.get(key)
```

This is equally important because many interviews ask:

> "Explain how HashMap retrieves a value internally."

A senior answer should cover:

* hash calculation
* bucket calculation
* node traversal
* hash comparison
* equals comparison
* linked list lookup
* tree lookup
* null key handling
* complexity

---

# 1. Simple Example

```java
Map<String,Integer> map = new HashMap<>();

map.put("Java",100);


Integer value = map.get("Java");
```

Question:

How does HashMap find `100`?

---

# 2. High-Level get() Flow ⭐⭐⭐⭐⭐

```text
                    get(key)


                       |
                       v


              Is key null?


             /            \

           Yes             No

           |                |

     bucket 0          calculate hash


                            |

                            v


                    calculate index


                            |

                            v


                    find bucket


                            |

                            v


              Compare first node


                            |

                            v


                 hash matches?


                            |

                            v


                  equals() check


                            |

                            v


                  return value


```

---

# 3. HashMap get() Method

Simplified Java 8 code:

```java
public V get(Object key) {

    Node<K,V> e;

    return (e = getNode(hash(key), key)) == null
            ? null
            : e.value;

}
```

---

Important:

`get()` internally calls:

```java
getNode()
```

---

# 4. Step 1 — Calculate Hash

Example:

```java
map.get("Java");
```

First:

```java
"Java".hashCode()
```

Example result:

```
2301506
```

---

Then HashMap applies:

```java
h ^ (h >>> 16)
```

Why?

To improve distribution.

---

Flow:

```text

"Java"


 |
 v


hashCode()


 |
 v


2301506


 |
 v


hash spreading


 |
 v


final hash


```

---

# 5. Step 2 — Calculate Bucket Index

Assume:

```
capacity = 16
```

Formula:

```java
index = hash & (n-1)
```

Example:

```text
hash

   &
   
01111

```

Result:

```
bucket index = 6
```

---

Now HashMap directly jumps:

```text
table[6]
```

---

# 6. Step 3 — Check Bucket

Example:

```text
table[6]


      |
      v


+----------------+
| key = Java     |
| value = 100    |
+----------------+

```

Now HashMap checks:

Is this the required key?

---

# 7. First Comparison — Hash Comparison ⭐⭐⭐⭐⭐

Node stores:

```java
final int hash;
```

Example:

Stored:

```
hash = 12345
```

Searching:

```
hash = 12345
```

Compare:

```java
if(node.hash == hash)
```

---

Why compare hash first?

Because it is cheap.

Integer comparison:

```java
==
```

is faster than:

```java
equals()
```

---

---

# 8. Second Comparison — equals()

If hash matches:

Now:

```java
node.key.equals(key)
```

Example:

Stored:

```java
new String("Java")
```

Searching:

```java
"Java"
```

Hash same.

Now:

```java
equals()
```

returns:

```
true
```

Value returned.

---

Flow:

```text

Bucket Node


     |
     v


Same hash?


     |
     v


Same key using equals()?


     |
     v


Return value


```

---

# 9. Collision Case During get() ⭐⭐⭐⭐⭐

Suppose:

Three keys landed in same bucket.

```text

Bucket 5


Java
 |
 v
Python
 |
 v
C++

```

Now:

```java
map.get("C++")
```

Flow:

---

Check first node:

```text
Java

hash?
No

```

Move:

```
next
```

---

Second:

```text
Python

hash?
No

```

Move:

```
next
```

---

Third:

```text
C++

hash?
Yes

equals?
Yes

```

Return value.

---

Diagram:

```text

Bucket 5


[Java]
   |
   v
[Python]
   |
   v
[C++]  <-- found


```

---

# 10. Java 7 get() Complexity

Before Java 8:

Collision structure:

```text

Bucket


A

|

B

|

C

|

D

```

Search:

```
A?
B?
C?
D?
```

Worst case:

```
O(n)
```

---

# 11. Java 8 TreeNode Lookup ⭐⭐⭐⭐⭐

After treeification:

Bucket becomes:

```text

             D

          /     \

         B       F

        / \     /

       A   C   E


```

Search uses:

Red Black Tree rules.

Instead of checking every node:

It follows tree path.

Complexity:

```
O(log n)
```

---

Example:

Searching:

```
E
```

Flow:

```text

        D

        |
        v

        F

        |
        v

        E


```

Only few comparisons.

---

# 12. get() With Null Key ⭐⭐⭐⭐⭐

Important interview question:

> How does HashMap handle null key?

Example:

```java
map.put(null,"value");
```

---

HashMap allows one null key.

Why?

Special handling.

Inside HashMap:

```java
hash(null)
```

returns:

```
0
```

---

Therefore:

Null key always goes to:

```
Bucket 0
```

---

Example:

```text

table[0]


 |
 v


Node

key=null

value=value


```

---

Retrieval:

```java
map.get(null)
```

directly checks bucket 0.

---

# 13. Multiple Null Keys

Example:

```java
map.put(null,"A");

map.put(null,"B");
```

What happens?

Only one null key exists.

Final:

```text
null -> B
```

Because second insertion replaces value.

---

# 14. Why get() Does Not Lock? ⭐⭐⭐⭐⭐

Interview:

> Does HashMap get() need synchronization?

Answer:

No.

But important:

HashMap itself is NOT thread-safe.

---

Single thread:

```
get()
```

works without lock.

---

Multiple threads:

Possible issues:

* visibility problems
* concurrent modification
* inconsistent state

---

For concurrency:

Use:

```java
ConcurrentHashMap
```

---

# 15. get() vs put() Complexity ⭐⭐⭐⭐⭐

## get()

Average:

```
O(1)
```

Why?

Because:

```
hash
 |
bucket
 |
node
```

direct access.

---

Worst:

Java 7:

```
O(n)
```

Java 8:

```
O(log n)
```

after treeification.

---

## put()

Average:

```
O(1)
```

Worst:

```
O(log n)
```

Java 8.

---

# 16. Why get() Is Usually Faster Than put()

Because put() may require:

* node creation
* collision handling
* resize
* treeification

get() only:

* calculate hash
* search

---

Comparison:

```text

get()


hash

 |

bucket

 |

find


--------------------


put()


hash

 |

bucket

 |

collision?

 |

insert

 |

resize?


```

---

# 17. Complete get() Internal Diagram ⭐⭐⭐⭐⭐

```text

              get(key)


                  |

                  v


             hash(key)


                  |

                  v


          hash spreading


                  |

                  v


          calculate index


                  |

                  v


             table[index]


                  |

                  v


          Is bucket empty?


            /          \

          Yes           No

          |              |

       return null    compare nodes


                          |

                    hash comparison


                          |

                    equals comparison


                          |

                    return value



```

---

# 18. Interview Questions ⭐⭐⭐⭐⭐

## Q1. How does HashMap get() work internally?

Answer:

> HashMap first calculates the hash of the key, applies hash spreading, calculates the bucket index, then searches the bucket. It compares hash values first and then uses equals() to identify the correct key.

---

## Q2. Why does HashMap compare hash before equals?

Answer:

> Hash comparison is faster than equals(). It avoids expensive equality checks when hashes are different.

---

## Q3. What happens if two keys have same hashCode?

Answer:

> They are placed in the same bucket. HashMap uses equals() to distinguish between them.

---

## Q4. Why is HashMap get() O(1)?

Answer:

> Because hashing allows direct bucket access instead of scanning the entire collection.

---

## Q5. What is worst-case complexity of HashMap get()?

Answer:

> Java 7: O(n) because collisions use linked lists. Java 8: O(log n) when a bucket is converted into a Red-Black tree.

---

## Q6. How does HashMap handle null key?

Answer:

> Null key gets hash value 0 and is stored in bucket index 0. Only one null key is allowed.

---

# 19. Senior Interview 2-Minute Answer

If asked:

> Explain HashMap.get().

Say:

> "When get() is called, HashMap calculates the hash of the key and applies hash spreading. It then calculates the bucket index using bit masking. It checks the bucket's nodes, first comparing hash values and then using equals() to find the exact key. If the bucket contains a linked list, it traverses the list. If Java 8 treeification has occurred, it searches the Red-Black tree in O(log n). Average lookup complexity is O(1), while worst case is O(log n) in modern Java."

---

# Chapter Complete ✅

Next Chapter:

# Chapter 20 — HashMap Resize Mechanism Deep Dive (Java 7 vs Java 8) ⭐⭐⭐⭐⭐

We will go deeper into:

* Why resize happens
* Threshold calculation
* Transfer process
* Rehashing
* Java 7 infinite loop problem
* Java 8 optimization
* Low/high bucket splitting
* Interview diagrams

This is another very frequently asked HashMap internal question.
