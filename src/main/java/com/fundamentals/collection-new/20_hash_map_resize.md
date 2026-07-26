# Chapter 20 — HashMap Resize Mechanism Deep Dive (Java 7 vs Java 8) ⭐⭐⭐⭐⭐

This is one of the **highest-value HashMap interview topics**.

Many candidates know:

> "HashMap doubles its size when it reaches load factor."

But senior interviews go deeper:

* Why does resize happen?
* What exactly happens during resize?
* Why is resize expensive?
* Why capacity doubles?
* How did Java 7 resize differ from Java 8?
* What was the Java 7 infinite loop problem?
* How does Java 8 optimize redistribution?

Let's understand from the beginning.

---

# 1. Why Does HashMap Resize?

HashMap stores entries inside buckets.

Example:

Initial capacity:

```text
capacity = 4
```

Buckets:

```text
Index

0
1
2
3

```

Suppose we insert:

```java
map.put("A",1);
map.put("B",2);
map.put("C",3);
```

Structure:

```text
HashMap


0
|
v
null


1
|
v
A


2
|
v
B


3
|
v
C

```

---

Now more elements come.

More entries mean:

* more collisions
* longer linked lists
* slower lookup

Example:

```text
Bucket 2


A

|

B

|

C

|

D

```

Lookup changes from:

```text
O(1)
```

towards:

```text
O(n)
```

---

Therefore HashMap increases capacity.

This process is called:

# Resize

---

# 2. Load Factor and Resize Trigger ⭐⭐⭐⭐⭐

HashMap has:

```java
loadFactor = 0.75
```

Formula:

```
threshold = capacity * loadFactor
```

---

Example:

Capacity:

```
16
```

Threshold:

```
16 * 0.75 = 12
```

Meaning:

After 12 entries:

```text
resize required
```

---

Flow:

```text
put()


 |
 v


size++


 |
 v


size > threshold?


       |
       |
      Yes


       |
       v


    resize()

```

---

# 3. What Happens During Resize?

Current:

```text
Capacity = 4
```

Buckets:

```text
0
1
2
3

```

After resize:

```text
Capacity = 8
```

Buckets:

```text
0
1
2
3
4
5
6
7

```

---

But the question is:

> Where do existing entries go?

They must be redistributed.

---

Example:

Before:

```text
Old Table


Bucket 1


A


Bucket 3


B


Bucket 0


C

```

After:

```text
New Table


Bucket 5


A


Bucket 3


B


Bucket 0


C

```

Because bucket index depends on capacity.

---

# 4. Why Is Resize Expensive? ⭐⭐⭐⭐⭐

During resize:

HashMap must:

1. Create new array
2. Visit every existing node
3. Calculate new position
4. Move node

---

Example:

If HashMap contains:

```
1 million entries
```

Resize requires:

```
move 1 million nodes
```

Complexity:

```
O(n)
```

---

But resize does not happen every insertion.

Therefore:

Average:

```
put() = O(1)
```

This is called:

# Amortized Complexity

---

# 5. Why Does Capacity Double? ⭐⭐⭐⭐⭐

Question:

Why:

```
16 → 32 → 64
```

instead of:

```
16 → 20 → 25
```

?

Reason:

HashMap requires capacity to be a power of two.

Because bucket calculation uses:

```java
index = hash & (capacity - 1)
```

---

Example:

Capacity:

```
16
```

Binary:

```
10000
```

capacity - 1:

```
01111
```

This gives efficient distribution.

---

# 6. Java 7 Resize Mechanism ⭐⭐⭐⭐⭐

Java 7 used:

```
Entry[]
```

Internally:

```java
Entry<K,V>[] table;
```

Collision structure:

```text
Bucket


A

|

B

|

C

```

---

During resize:

Java 7:

1. Creates new array
2. Iterates old buckets
3. Transfers nodes
4. Recalculates position

---

Important:

Java 7 used:

# Head Insertion

Meaning:

When moving nodes:

old:

```text
A -> B -> C
```

after transfer:

```text
C -> B -> A
```

---

Example:

Before:

```text
Old Bucket


Node A

 |
 v

Node B

 |
 v

Node C

```

After resize:

```text
New Bucket


Node C

 |
 v

Node B

 |
 v

Node A

```

---

# 7. Java 7 Multithreading Resize Problem ⭐⭐⭐⭐⭐

Important interview question.

HashMap is not thread-safe.

Suppose:

Two threads resize simultaneously.

Thread 1:

```text
A -> B -> C
```

Thread 2:

also resizing.

Because Java 7 uses head insertion:

One thread may reverse links while another is reading them.

Possible result:

```text
A -> B -> A -> B -> A ...

```

Cycle created.

---

Diagram:

```text

A
|
v
B
|
v
A
|
v
B


```

Now:

```java
map.get(key)
```

can enter infinite loop.

---

This was a famous Java 7 HashMap bug.

---

# 8. Java 8 Resize Improvement ⭐⭐⭐⭐⭐

Java 8 changed the approach.

Instead of completely recalculating positions:

Java 8 uses:

# Low/High Split

---

Example:

Old capacity:

```
16
```

New capacity:

```
32
```

---

A node can only go to:

Option 1:

Same index

```
oldIndex
```

or

Option 2:

Old index + old capacity

```
oldIndex + 16
```

---

Example:

Node in bucket:

```
5
```

After resize:

Possible locations:

```
5

or

21
```

because:

```
5 + 16 = 21
```

---

Diagram:

```text

Old Table


Bucket 5


     |
     |
     v


Check hash bit


       /       \


 LOW          HIGH


       |          |


Bucket 5     Bucket 21


```

---

# 9. Why Does Java 8 Do This?

Because doubling capacity only changes one additional bit.

Example:

Old:

```
16

10000
```

New:

```
32

100000
```

Only one bit decides whether the node moves.

---

Therefore:

No complete rehash needed.

---

# 10. Java 7 vs Java 8 Resize Comparison ⭐⭐⭐⭐⭐

| Feature                 | Java 7                  | Java 8          |
| ----------------------- | ----------------------- | --------------- |
| Structure               | Entry[]                 | Node[]          |
| Transfer                | Recalculate hash        | Low/high split  |
| Insertion during resize | Head insertion          | Preserves order |
| Complexity              | O(n)                    | O(n)            |
| Optimization            | Less                    | Better          |
| Infinite loop risk      | Possible in concurrency | Fixed           |

---

# 11. Resize Flow Diagram

Complete flow:

```text

put(key,value)


       |
       v


insert node


       |
       v


size exceeds threshold?


       |
       v


resize()


       |
       v


Create new table


       |
       v


Move existing nodes


       |
       v


Redistribute buckets


       |
       v


Continue operation


```

---

# 12. Why Not Resize Every Time?

Suppose:

Every insertion increases capacity.

Example:

```
16
32
64
128
256
```

Memory waste.

Also too many copy operations.

---

HashMap waits until:

```
75% full
```

then expands.

This balances:

* memory
* performance

---

# 13. Example Dry Run ⭐⭐⭐⭐⭐

Initial:

```
capacity = 4
loadFactor = 0.75
```

Threshold:

```
4 * 0.75 = 3
```

---

Insert:

```
A
B
C
```

Size:

```
3
```

No resize.

---

Insert:

```
D
```

Size:

```
4
```

Check:

```
4 > 3
```

Resize.

---

Before:

```text
Capacity 4


0
1 -> A
2 -> B
3 -> C

```

After:

```text
Capacity 8


0
1 -> A
2
3 -> C
4 -> B

```

---

# 14. Resize and Complexity ⭐⭐⭐⭐⭐

Normal insertion:

```
O(1)
```

Resize insertion:

```
O(n)
```

But because resize happens occasionally:

Average:

```
O(1)
```

---

# 15. Interview Questions ⭐⭐⭐⭐⭐

## Q1. When does HashMap resize?

Answer:

> When the number of entries exceeds capacity multiplied by load factor. Default load factor is 0.75.

---

## Q2. Why is resize expensive?

Answer:

> Because HashMap creates a new bucket array and redistributes all existing nodes.

---

## Q3. Why does HashMap double capacity?

Answer:

> To maintain power-of-two capacity and allow efficient bucket calculation using bit operations.

---

## Q4. What changed in Java 8 resize?

Answer:

> Java 8 avoids recalculating hashes completely. It uses the old capacity bit to decide whether a node stays at the same index or moves by old capacity.

---

## Q5. Why did Java 7 HashMap have infinite loop during resize?

Answer:

> Because concurrent resize operations combined with head insertion could create cyclic linked lists.

---

## Q6. Does Java 8 HashMap become thread-safe?

Answer:

No.

Java 8 fixed the resize cycle issue but HashMap is still not thread-safe.

For concurrency use:

```java
ConcurrentHashMap
```

---

# 16. Senior Interview Answer (2 Minutes)

If asked:

> Explain HashMap resize.

Answer:

> "HashMap resizes when its size exceeds the threshold calculated using capacity multiplied by load factor. During resize, it creates a new bucket array with double capacity and redistributes existing nodes. Resize is an O(n) operation because every entry may need to be moved. Java 7 recalculated bucket positions and used head insertion during transfer, which could create cycles during concurrent resize. Java 8 optimized this using low/high bucket splitting, where nodes either remain at the same index or move by the old capacity."

---

# Final Mental Model

```text
HashMap Resize


Capacity Full


      |
      v


Create Bigger Array


      |
      v


Move Nodes


      |
      v


Redistribute


      |
      v


Continue O(1) operations



Java 7:
Rehash + Head insertion

Java 8:
Low/High Split + Better transfer

```

---

# Chapter Complete ✅

Next Chapter:

# Chapter 21 — HashMap `hashCode()` and `equals()` Deep Interview Questions ⭐⭐⭐⭐⭐

Topics:

* equals contract
* hashCode contract
* Why override both
* Common bugs
* Mutable key problems
* Objects.hash()
* String hashCode implementation
* Custom immutable keys
* Interview coding examples

This chapter usually decides whether someone is considered "strong in Java Collections" in senior interviews.


# Chapter 21 — HashMap `hashCode()` and `equals()` Deep Interview Questions ⭐⭐⭐⭐⭐

This is one of the **most important HashMap interview chapters**.

Many candidates understand:

```
HashMap uses hashCode()
HashMap uses equals()
```

But senior interviews go deeper:

* Why do we need both?
* What happens if we override only equals()?
* What happens if we override only hashCode()?
* Why must immutable objects be used as HashMap keys?
* How does String work as a key?
* What is the contract between equals() and hashCode()?
* How does HashMap find duplicate keys?

---

# 1. Why Does HashMap Need hashCode() and equals()?

Remember:

HashMap internally stores data like:

```text
HashMap

        table[]

          |
          |
          v

       Bucket


          |
          |
          v

       Node

       key
       value
```

When we do:

```java
map.put(key,value);
```

HashMap needs two answers:

## Question 1

Where should I store this key?

Answer:

```
hashCode()
```

---

## Question 2

Is this key already present?

Answer:

```
equals()
```

---

Flow:

```text

              Key


               |
               v


          hashCode()


               |
               v


        Find bucket


               |
               v


        equals()


               |
               v


       Same key or new key?



```

---

# 2. hashCode() Responsibility

`hashCode()` decides the bucket.

Example:

```java
String key = "Java";
```

Hash:

```
2301506
```

After hash spreading:

```
some final hash
```

Bucket:

```
index = hash & (n-1)
```

---

Example:

```text

hashCode()

    |
    v

Bucket 5


```

---

Important:

`hashCode()` does NOT guarantee uniqueness.

---

Example:

```java
class User {

    String name;

}
```

Two objects:

```java
User u1 = new User("John");

User u2 = new User("John");
```

They may have:

```
same hashCode
```

but are different objects.

---

# 3. Collision Example

Suppose:

```java
key1.hashCode() = 100

key2.hashCode() = 100
```

Both go:

```
Bucket 5
```

Structure:

```text

Bucket 5


        key1
          |
          v
        key2


```

Now HashMap uses:

```java
equals()
```

to differentiate.

---

# 4. equals() Responsibility

equals() answers:

> Are these two objects logically the same?

Example:

```java
String a = new String("Java");

String b = new String("Java");
```

Memory:

```
a ---> "Java"

b ---> "Java"

```

Different objects.

But:

```java
a.equals(b)
```

returns:

```
true
```

because content is same.

---

# 5. hashCode() Contract ⭐⭐⭐⭐⭐

Java has strict rules.

## Rule 1

If two objects are equal:

```java
a.equals(b) == true
```

then:

```java
a.hashCode() == b.hashCode()
```

must be true.

---

Example:

```java
String s1 = "Java";

String s2 = new String("Java");
```

Equals:

```
true
```

Therefore:

```
hashCode must be same
```

---

## Rule 2

If hashCodes are same:

```
a.hashCode() == b.hashCode()
```

objects do NOT have to be equal.

---

Example:

```
Collision
```

Possible:

```
Object A

hash = 100


Object B

hash = 100

```

But:

```
A.equals(B)

false

```

---

Diagram:

```text

Same hash


     |
     |

+----+----+

|         |

A         B


equals?

false


```

---

# 6. Why Override Both hashCode() and equals()? ⭐⭐⭐⭐⭐

Example:

```java
class Employee {

    int id;

    String name;


}
```

Now:

```java
Employee e1 =
new Employee(1,"John");


Employee e2 =
new Employee(1,"John");

```

Logically:

```
same employee
```

But default Object implementation:

```java
equals()
```

uses reference comparison.

Meaning:

```
e1 == e2
```

false.

---

Now:

```java
Map<Employee,String> map =
new HashMap<>();

map.put(e1,"Developer");


map.get(e2);

```

Expected:

```
Developer
```

Actual:

```
null
```

Why?

Because HashMap cannot find the bucket.

---

# 7. Wrong Implementation Example

## Override equals only

```java
class Employee {


    int id;


    @Override
    public boolean equals(Object obj){

        Employee e =
        (Employee)obj;

        return id == e.id;

    }

}
```

---

Problem:

HashCode is still different.

Example:

```
e1.hashCode()

12345


e2.hashCode()

67890

```

Even though:

```
e1.equals(e2)

true
```

HashMap sends them to different buckets.

---

Result:

```text

put(e1)


Bucket 3


get(e2)


Bucket 8


Not found


```

---

# 8. Correct Implementation ⭐⭐⭐⭐⭐

Override both.

```java
class Employee {


    private int id;


    private String name;



    @Override
    public boolean equals(Object obj){

        if(this == obj)
            return true;


        if(!(obj instanceof Employee))
            return false;


        Employee e =
        (Employee)obj;


        return id == e.id
               &&
               name.equals(e.name);

    }



    @Override
    public int hashCode(){

        return Objects.hash(id,name);

    }

}
```

---

Now:

```java
Employee e1 =
new Employee(1,"John");


Employee e2 =
new Employee(1,"John");


System.out.println(e1.equals(e2));
```

Output:

```
true
```

Hash:

```
same
```

HashMap works correctly.

---

# 9. Why Immutable Keys Are Preferred ⭐⭐⭐⭐⭐

Very common senior question:

> Why should HashMap keys be immutable?

Because hashCode must not change after insertion.

---

Bad example:

```java
class User {

    String name;

}
```

Insert:

```java
User user =
new User("John");


map.put(user,"value");

```

At this moment:

```
hashCode = 100
```

Bucket:

```
Bucket 4

```

---

Now modify:

```java
user.name="Alex";
```

New hash:

```
hashCode = 200
```

---

Now:

```java
map.get(user);
```

HashMap searches:

```
Bucket 8
```

But object exists:

```
Bucket 4
```

Result:

```
null
```

---

Diagram:

Before:

```text

User("John")


hash=100


Bucket 4

```

After:

```text

User("Alex")


hash=200


Search Bucket 8


Not found


```

---

# 10. String as HashMap Key ⭐⭐⭐⭐⭐

Why is String commonly used as key?

Because String is immutable.

Example:

```java
Map<String,Integer> map =
new HashMap<>();

map.put("Java",100);

```

String value cannot change.

---

Internally:

```java
String.hashCode()
```

is calculated from characters.

Formula:

```
hash = 31 * hash + character
```

---

Example:

String:

```
"ABC"
```

Calculation:

```
A = 65
B = 66
C = 67
```

Conceptually:

```
hash = ((0*31+65)*31+66)*31+67
```

---

# 11. Why 31 in String hashCode? ⭐⭐⭐⭐⭐

Interview favourite.

Why:

```java
31
```

?

Reasons:

### 1. Prime number

Prime numbers reduce collisions.

---

### 2. Good distribution

Produces better spread.

---

### 3. JVM optimization

Compiler can optimize:

```java
31 * i
```

as:

```
(i << 5) - i
```

because:

```
31 = 32 - 1
```

---

# 12. Objects.hash() Internals

Example:

```java
@Override
public int hashCode(){

    return Objects.hash(id,name);

}
```

Internally:

```java
Arrays.hashCode()
```

is used.

Conceptually:

```
result = 1

result = 31 * result + id

result = 31 * result + name.hashCode()

```

---

# 13. Interview Trap — Can Two Keys Have Same HashCode?

Yes.

Example:

```
hashCode collision
```

is normal.

HashMap handles it using:

```
equals()
```

---

Example:

```text

Bucket 10


Employee A

     |

Employee B


```

Hash:

same

Equals:

different

---

# 14. Interview Trap — Can equals() return true with different hashCode?

No.

This breaks HashMap.

Violation:

```java
a.equals(b)

true


a.hashCode()

100


b.hashCode()

200

```

Invalid.

---

# 15. HashMap Duplicate Key Detection Complete Flow ⭐⭐⭐⭐⭐

Example:

```java
map.put(new Employee(1),"A");

map.put(new Employee(1),"B");

```

Second put:

---

Step 1:

Calculate hash:

```
Employee.hashCode()
```

---

Step 2:

Find bucket.

```
Bucket 5
```

---

Step 3:

Compare existing node:

```
hash same?
```

Yes.

---

Step 4:

equals():

```
existingEmployee.equals(newEmployee)

true

```

---

Step 5:

Replace value.

Before:

```
Employee(1) -> A
```

After:

```
Employee(1) -> B
```

Size remains:

```
1
```

---

# 16. Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why are hashCode and equals both required?

Answer:

> hashCode identifies the bucket location, while equals identifies the exact key inside that bucket.

---

## Q2. What happens if we override equals but not hashCode?

Answer:

> Equal objects may generate different hash values, causing HashMap lookup failures.

---

## Q3. Why should HashMap keys be immutable?

Answer:

> Because changing key state after insertion may change hashCode, making the object unreachable.

---

## Q4. Can two unequal objects have the same hashCode?

Answer:

> Yes. This is called collision and HashMap handles it using equals().

---

## Q5. Why does String work well as HashMap key?

Answer:

> String is immutable, has a well-distributed hashCode implementation, and provides correct equals/hashCode behavior.

---

# 17. Senior Interview Answer (2 Minutes)

If asked:

> Explain hashCode and equals relationship in HashMap.

Answer:

> "HashMap uses hashCode to locate the bucket where an entry should reside. Since multiple keys can have the same hashCode, HashMap uses equals to identify the exact key inside that bucket. The Java contract requires that if two objects are equal, they must have the same hashCode. Therefore whenever equals is overridden, hashCode must also be overridden. HashMap keys should ideally be immutable because modifying a key after insertion can change its hashCode and make the entry impossible to retrieve."

---

# Final Mental Model

```text

HashMap Key Lookup


          key


           |

           v


     hashCode()


           |

           v


      Find Bucket


           |

           v


      Collision?


        /       \

      No         Yes


      |            |

 Return       equals()


                 |

          Same key?


            /     \

          Yes      No


          |         |

      Return     Continue


```

---

# Chapter Complete ✅

Next Chapter:

# Chapter 22 — HashMap Treeification & Red-Black Tree Deep Dive ⭐⭐⭐⭐⭐

Topics:

* Why Java 8 introduced tree bins
* TREEIFY_THRESHOLD (8)
* UNTREEIFY_THRESHOLD (6)
* MIN_TREEIFY_CAPACITY (64)
* Why linked list changes to tree
* Red-Black Tree basics
* Why O(n) becomes O(log n)
* TreeNode internals
* Interview complexity questions

This chapter directly connects to the interview rejection point you mentioned:
**"HashMap is O(1)" vs "What is worst-case complexity?"**.


# Chapter 22 — HashMap Treeification & Red-Black Tree Deep Dive ⭐⭐⭐⭐⭐

This is the chapter that explains the interview question:

> "HashMap is O(1), but what happens in the worst case?"

The answer depends on **Java version**.

Before Java 8:

```
Collision bucket
        |
        v
Linked List
        |
        v
Worst case O(n)
```

Java 8 onwards:

```
Collision bucket
        |
        v
Linked List

        |
        |
  More collisions

        |
        v

Red Black Tree

        |
        v

Worst case O(log n)
```

---

# 1. Why Did Java 8 Introduce Treeification?

Before Java 8:

A bucket with many collisions looked like:

```
Bucket 5


Node A

   |
   v

Node B

   |
   v

Node C

   |
   v

Node D

   |
   v

Node E

```

Searching:

```java
map.get(key)
```

requires traversal.

Example:

Search Node E:

```
A ?
 |
No

B ?
 |
No

C ?
 |
No

D ?
 |
No

E ?
 |
Found
```

Time:

```
O(n)
```

---

Problem:

An attacker could intentionally create many keys with the same hash.

Example:

```
Bucket 10


BadKey1
   |
BadKey2
   |
BadKey3
   |
BadKey4
   |
...
```

This is called:

**Hash Collision Attack**

---

# 2. Java 8 Solution

Java 8 introduced:

**Tree Bin**

Instead of:

```
Linked List
```

use:

```
Red Black Tree
```

when collisions become too high.

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
|
G
|
H

```

After:

```
Bucket


          D

       /     \

      B       F

     / \     / \

    A   C   E   H

```

---

# 3. Important Constants ⭐⭐⭐⭐⭐

HashMap has three important constants.

---

## TREEIFY_THRESHOLD

```java
static final int TREEIFY_THRESHOLD = 8;
```

Meaning:

When a bucket contains:

```
8 nodes
```

HashMap considers converting:

```
Linked List

        ↓

Red Black Tree
```

---

Example:

```
Before


Bucket 3


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


8 nodes

```

Treeification candidate.

---

# 4. But Is Treeification Immediate?

Important interview trap.

Answer:

**No.**

There is another condition.

---

# 5. MIN_TREEIFY_CAPACITY ⭐⭐⭐⭐⭐

```java
static final int MIN_TREEIFY_CAPACITY = 64;
```

Meaning:

HashMap will treeify only if:

```
capacity >= 64
```

---

Why?

Because sometimes the problem is not collision.

The problem is:

```
small table size
```

Example:

Capacity:

```
16
```

Many collisions happen.

Instead of creating trees:

HashMap prefers:

```
resize()
```

---

Decision:

```
Bucket has 8 nodes?


          |
          v


Capacity >= 64?


       /          \

     Yes           No

      |             |

 Treeify        Resize

```

---

# 6. Why Resize Instead of Treeify?

Example:

Small HashMap:

```
Capacity = 16


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

```

Instead of tree:

Increase capacity:

```
16

↓

32

↓

64

```

More buckets mean:

less collision.

---

Java assumes:

> "Maybe the table is too small, not that the hash function is bad."

---

# 7. Complete Treeification Flow ⭐⭐⭐⭐⭐

Example:

Insert many keys.

```
put(key,value)


       |
       v


Calculate bucket


       |
       v


Collision?


       |
       v


Add node to linked list


       |
       v


Count nodes


       |
       v


Node count >= 8?


       |
       v


Capacity >= 64?


       |
       v


Convert to Red Black Tree

```

---

# 8. Linked List vs Tree Complexity ⭐⭐⭐⭐⭐

## Linked List

Example:

```
A -> B -> C -> D -> E
```

Search:

Worst case:

```
E
```

Comparisons:

```
A
B
C
D
E
```

Complexity:

```
O(n)
```

---

## Red Black Tree

Example:

```
             D

          /     \

         B       F

        / \     / \

       A   C   E   G

```

Search:

Find G:

```
D

go right


F

go right


G

found

```

Complexity:

```
O(log n)
```

---

# 9. What Is a Red Black Tree?

A Red Black Tree is a:

```
Self-balancing Binary Search Tree
```

Meaning:

The height remains balanced.

---

Normal Binary Tree:

Could become:

```
A

 \
  B

   \
    C

     \
      D

```

Looks like linked list.

Search:

```
O(n)
```

---

Balanced Tree:

```
        B

      /   \

     A     C

```

Height is controlled.

Search:

```
O(log n)
```

---

# 10. Red Black Tree Rules ⭐⭐⭐⭐⭐

You do not need to implement it in interviews usually.

But understand the properties.

A Red Black Tree maintains:

---

## Rule 1

Every node has a color:

```
RED
or
BLACK
```

---

## Rule 2

Root is always black.

---

## Rule 3

Red nodes cannot have red children.

Invalid:

```
       Red

       |

       Red

```

---

## Rule 4

Every path from node to leaf has same number of black nodes.

---

These rules keep tree balanced.

---

# 11. HashMap TreeNode Structure

Normal Node:

```java
static class Node<K,V>{

    int hash;

    K key;

    V value;

    Node<K,V> next;

}
```

---

TreeNode extends Node:

```java
static final class TreeNode<K,V>
extends LinkedHashMap.Entry<K,V>{

    TreeNode<K,V> parent;

    TreeNode<K,V> left;

    TreeNode<K,V> right;

    TreeNode<K,V> prev;

    boolean red;

}
```

---

Extra information:

```
parent

left child

right child

color
```

needed for balancing.

---

# 12. Tree Lookup Process ⭐⭐⭐⭐⭐

Suppose:

```
Bucket 5


          D

       /     \

      B       F

     / \

    A   C

```

Search:

```java
map.get("C")
```

---

Step 1:

Compare with root:

```
C vs D
```

C smaller.

Go left.

---

Step 2:

```
C vs B
```

C greater.

Go right.

---

Step 3:

```
C found
```

---

Path:

```
D

↓

B

↓

C

```

Only 3 comparisons.

---

# 13. Treeification Example

Let's create a bad key.

```java
class BadKey {

    int id;


    public int hashCode(){
        return 1;
    }


    public boolean equals(Object o){
        return this.id ==
               ((BadKey)o).id;
    }
}
```

Every key goes:

```
hash = 1
```

Therefore:

```
Bucket 1


Key1
 |
Key2
 |
Key3
 |
...
```

After enough inserts:

```
Bucket 1


          Key5

       /        \

    Key2       Key8

```

---

# 14. Untreeification ⭐⭐⭐⭐⭐

Interviewers may ask:

> Once converted to tree, does it remain forever?

Answer:

No.

There is:

```java
UNTREEIFY_THRESHOLD = 6
```

---

If nodes reduce:

```
Tree nodes <= 6
```

HashMap converts back:

```
Red Black Tree

        ↓

Linked List
```

---

Why?

Trees have extra memory overhead.

For small data:

Linked list is faster.

---

Flow:

```
Tree


 |
 v


Remove elements


 |
 v


Size <= 6?


 |
 v


Convert to linked list

```

---

# 15. Complete Threshold Summary ⭐⭐⭐⭐⭐

| Constant             | Value | Meaning                            |
| -------------------- | ----- | ---------------------------------- |
| TREEIFY_THRESHOLD    | 8     | Convert list → tree                |
| UNTREEIFY_THRESHOLD  | 6     | Convert tree → list                |
| MIN_TREEIFY_CAPACITY | 64    | Minimum capacity required for tree |

---

# 16. Complexity Summary ⭐⭐⭐⭐⭐

## Java 7 HashMap

| Operation | Average | Worst |
| --------- | ------- | ----- |
| get       | O(1)    | O(n)  |
| put       | O(1)    | O(n)  |

Because:

```
Collision → Linked List
```

---

## Java 8 HashMap

| Operation | Average | Worst    |
| --------- | ------- | -------- |
| get       | O(1)    | O(log n) |
| put       | O(1)    | O(log n) |

Because:

```
Large Collision → Red Black Tree
```

---

# 17. Important Interview Trap

Interviewer:

> "Is HashMap always O(log n) in Java 8?"

Wrong answer:

```
Yes
```

Correct answer:

> "No. Average complexity remains O(1). O(log n) only applies to heavily collided buckets after treeification."

---

# 18. Another Trap

Interviewer:

> "If bucket has 8 nodes, will it become tree?"

Wrong:

```
Yes
```

Correct:

> "Only if capacity is at least 64. Otherwise HashMap resizes first."

---

# 19. Complete HashMap Collision Lifecycle ⭐⭐⭐⭐⭐

```
Insert key


     |

     v


Calculate hash


     |

     v


Bucket


     |

     v


Collision?


  No       Yes

  |          |

Insert    Linked List


              |

              v


       Nodes >= 8?


              |

              v


       Capacity >=64?


          /        \

        Yes        No

         |          |

     Treeify     Resize


```

---

# 20. Senior Interview Answer (2 minutes)

If asked:

> "How does Java 8 improve HashMap collision handling?"

Answer:

> "Before Java 8, HashMap handled collisions using linked lists, giving O(n) worst-case lookup. Java 8 introduced treeification. When a bucket contains at least 8 nodes and the table capacity is at least 64, the linked list is converted into a Red Black Tree. This reduces worst-case lookup from O(n) to O(log n). However, normal HashMap operations still remain O(1) on average."

---

# Chapter Complete ✅

At this point we have covered:

✅ HashMap architecture
✅ Node structure
✅ put() internals
✅ get() internals
✅ resize mechanism
✅ hashCode/equals contract
✅ collision handling
✅ treeification
✅ complexity analysis

Next Chapter:

# Chapter 23 — HashMap Advanced Interview Traps ⭐⭐⭐⭐⭐

Topics:

* Why HashMap is not thread-safe
* Concurrent modification during iteration
* Fail-fast iterator
* modCount
* Structural modification
* HashMap vs ConcurrentHashMap
* HashMap memory overhead
* Initial capacity optimization
* Why mutable keys are dangerous
* Production mistakes

This chapter connects Collections with Concurrency.

# Chapter 23 — HashMap Advanced Interview Traps ⭐⭐⭐⭐⭐

Now we move from **internal implementation** to the questions that separate a normal Java developer from a senior Java engineer.

These are the questions frequently asked in:

* Amazon
* Oracle
* Microsoft
* JPMorgan
* Flipkart
* Uber
* Goldman Sachs

because they test whether you understand **real production behaviour**.

---

# 1. Why Is HashMap Not Thread-Safe? ⭐⭐⭐⭐⭐

Very common interview question.

Question:

> "HashMap works fine in single-threaded applications. Why does it fail in multithreading?"

Answer:

Because multiple threads can modify internal structure simultaneously.

HashMap does not provide:

* locking
* synchronization
* atomic operations

---

Example:

```java
Map<Integer,String> map = new HashMap<>();

Thread 1:

map.put(1,"A");


Thread 2:

map.put(2,"B");

```

Both threads modify:

```text
table[]

```

at the same time.

---

Possible problems:

## 1. Lost Updates

Example:

Initial:

```text
size = 10
```

Thread A:

```java
size++
```

Thread B:

```java
size++
```

Expected:

```text
size = 12
```

Actual:

```text
size = 11
```

because both read the same old value.

---

## 2. Visibility Problems

Thread A inserts:

```java
map.put(1,"Java");
```

Thread B:

```java
map.get(1);
```

Without proper synchronization:

Thread B may not see the latest update.

---

## 3. Resize Problems

The most famous problem.

Two threads resize simultaneously.

Java 7 could create:

```text
A -> B -> C -> A -> B -> C

```

Cycle.

Then:

```java
get()
```

can run forever.

---

# 2. HashMap vs ConcurrentHashMap ⭐⭐⭐⭐⭐

Very common senior question.

## HashMap

```java
Map<Integer,String> map =
        new HashMap<>();
```

Characteristics:

* Not thread-safe
* No locking
* Faster in single-threaded use

---

## ConcurrentHashMap

```java
Map<Integer,String> map =
        new ConcurrentHashMap<>();
```

Characteristics:

* Thread-safe
* Supports concurrent access
* Uses CAS + synchronization internally

---

Comparison:

| Feature           | HashMap              | ConcurrentHashMap   |
| ----------------- | -------------------- | ------------------- |
| Thread safe       | No                   | Yes                 |
| Locking           | None                 | Fine-grained        |
| Null keys         | Allowed              | Not allowed         |
| Null values       | Allowed              | Not allowed         |
| Concurrent writes | Unsafe               | Safe                |
| Performance       | Faster single thread | Better multi-thread |

---

# 3. Why Does ConcurrentHashMap Not Allow Null Keys? ⭐⭐⭐⭐⭐

Interview favourite.

Question:

> "HashMap allows null keys. Why doesn't ConcurrentHashMap?"

Because ambiguity.

Consider:

```java
map.get(key)
```

returns:

```java
null
```

What does it mean?

Case 1:

Key exists:

```text
key -> null

```

Case 2:

Key does not exist:

```text
key absent

```

Both return:

```text
null

```

---

HashMap:

```java
containsKey()
```

can differentiate.

But ConcurrentHashMap wants simpler concurrent semantics.

Therefore:

```java
put(null,value)
```

throws:

```text
NullPointerException

```

---

# 4. HashMap Fail-Fast Iterator ⭐⭐⭐⭐⭐

Now we connect HashMap with Iterator.

Example:

```java
Map<Integer,String> map =
        new HashMap<>();

map.put(1,"A");
map.put(2,"B");


for(Integer key : map.keySet()) {

    map.remove(key);

}

```

What happens?

Exception:

```
ConcurrentModificationException
```

---

Why?

Because HashMap iterator is:

# Fail-Fast

---

# 5. What Is Fail-Fast? ⭐⭐⭐⭐⭐

Fail-fast means:

> Iterator detects structural modification and immediately throws an exception.

---

Example:

Iterator created:

```text
HashMap


A

B

C


```

Iterator remembers:

```java
expectedModCount
```

---

Then another modification happens:

```java
map.put(4,"D");

```

HashMap changes:

```java
modCount++

```

Now:

```text
expectedModCount != modCount

```

Iterator detects change.

Throws:

```text
ConcurrentModificationException

```

---

# 6. modCount Internal Working ⭐⭐⭐⭐⭐

HashMap has:

```java
transient int modCount;
```

Meaning:

Number of structural modifications.

---

Structural modifications:

## Add entry

```java
put()
```

increments:

```java
modCount++

```

---

## Remove entry

```java
remove()
```

increments:

```java
modCount++

```

---

But:

Updating existing value:

```java
map.put(key,newValue)

```

does NOT change size.

Example:

Before:

```text
A -> 100

```

After:

```text
A -> 200

```

No structural change.

---

# 7. Iterator Internal Flow ⭐⭐⭐⭐⭐

Example:

```java
Iterator<Integer> iterator =
        map.keySet().iterator();

```

Internally:

```text
Iterator created


       |

       v


expectedModCount = modCount


       |

       v


next()


       |

       v


compare values


       |

       v


same?


 /       \

Yes       No

 |          |

Continue   Exception


```

---

# 8. Why Does HashMap Iterator Fail Fast?

Question:

> "Why not make iterator automatically handle modification?"

Because:

1. Expensive
2. Hard to guarantee correctness
3. Better to detect programming errors

Fail-fast is a bug detection mechanism.

---

Important:

Fail-fast is:

```text
best effort

```

not a guarantee.

---

# 9. HashMap Memory Overhead ⭐⭐⭐⭐☆

Senior interviews sometimes ask:

> "How much memory does HashMap consume?"

A HashMap stores:

```text
HashMap Object

        |

        v

Node[] table

        |

        v

Node objects


```

Each entry requires:

```java
Node {

 int hash;

 K key;

 V value;

 Node next;

}

```

---

Memory components:

1. Bucket array

```text
Node[]

```

2. Node objects

3. Keys

4. Values

5. References

---

Example:

```java
map.put("Java",100);

```

Memory:

```text
HashMap

 |
 v

Node[]

 |
 v

Node

 |
 +---- key "Java"

 |
 +---- value 100

```

---

# 10. Initial Capacity Optimization ⭐⭐⭐⭐⭐

Important production question.

Suppose:

You know:

```text
Expected entries = 1000

```

Should you do:

```java
new HashMap<>(1000);

```

?

Not exactly.

---

Why?

Because HashMap resizes at:

```text
capacity * 0.75

```

---

If:

```text
capacity = 1000

```

threshold:

```text
750

```

You insert:

```text
1000 entries

```

Resize happens.

---

Better:

Formula:

```java
initialCapacity =
(expectedSize / 0.75f) + 1

```

---

Example:

Expected:

```text
1000 entries

```

Capacity:

```
1000 / 0.75 + 1

= 1334

```

Use:

```java
new HashMap<>(1334);

```

---

This avoids unnecessary resize.

---

# 11. Why Mutable Keys Are Dangerous ⭐⭐⭐⭐⭐

We discussed this earlier, but let's see production impact.

Bad:

```java
class User {

    String id;

}

```

Usage:

```java
User user =
new User("101");


map.put(user,"Employee");

```

---

At insertion:

```text
hashCode = 500

Bucket = 5

```

Later:

```java
user.id="102";

```

Now:

```text
hashCode = 700

Bucket = 7

```

---

Lookup:

```java
map.get(user)

```

Searches:

```text
Bucket 7

```

But data is in:

```text
Bucket 5

```

Result:

```text
null

```

---

# 12. Why String Is Safe as Key?

Because:

```java
String is immutable

```

Example:

```java
String s="Java";

```

You cannot modify:

```text
Java

```

into:

```text
Python

```

The JVM creates a new String.

---

Therefore:

```java
Map<String,Integer>

```

is safe.

---

# 13. HashMap Ordering Question ⭐⭐⭐⭐⭐

Question:

> "Does HashMap maintain insertion order?"

Answer:

No.

Example:

```java
map.put(3,"C");
map.put(1,"A");
map.put(2,"B");

```

Output may be:

```text
1
2
3

```

or:

```text
3
1
2

```

No guarantee.

---

Need insertion order?

Use:

```java
LinkedHashMap
```

---

Need sorted order?

Use:

```java
TreeMap
```

---

# 14. HashMap vs LinkedHashMap vs TreeMap ⭐⭐⭐⭐⭐

| Feature        | HashMap      | LinkedHashMap            | TreeMap        |
| -------------- | ------------ | ------------------------ | -------------- |
| Ordering       | No guarantee | Insertion order          | Sorted order   |
| Data structure | Hash table   | Hash table + linked list | Red Black Tree |
| get complexity | O(1)         | O(1)                     | O(log n)       |
| Null key       | Yes          | Yes                      | No             |
| Use case       | Fast lookup  | Maintain order           | Sorted data    |

---

# 15. Common Production Mistakes

## Mistake 1

Using HashMap for shared data.

Wrong:

```java
static Map<String,Object> cache =
new HashMap<>();

```

Multiple threads:

unsafe.

Use:

```java
ConcurrentHashMap

```

---

## Mistake 2

Using mutable objects as keys.

Wrong:

```java
Map<User,String>

```

if User changes.

---

## Mistake 3

Not setting initial capacity.

Large batch:

```java
for(records){

 map.put()

}

```

causes repeated resizing.

---

## Mistake 4

Assuming iteration order.

Wrong:

```java
for(entry: map)

```

expecting insertion order.

---

# 16. Senior Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why is HashMap not thread-safe?

Answer:

> Because it has no synchronization mechanism. Multiple threads can modify buckets, size, and internal structure simultaneously.

---

## Q2. What is fail-fast iterator?

Answer:

> An iterator that detects structural modification by comparing expectedModCount with HashMap's modCount and throws ConcurrentModificationException.

---

## Q3. Is ConcurrentModificationException guaranteed?

Answer:

> No. Fail-fast behavior is best effort. It is designed to detect bugs, not provide thread safety.

---

## Q4. How can you avoid ConcurrentModificationException?

Options:

### Iterator remove()

```java
iterator.remove();

```

---

### Copy collection

```java
new ArrayList<>(map.keySet())

```

---

### Concurrent collections

```java
ConcurrentHashMap

```

---

# 17. Complete HashMap Interview Mental Model

```text
                 HashMap


                    |

                    v


             hashCode()


                    |

                    v


              Bucket Index


                    |

        ----------------------


        Empty             Collision


          |                   |

       Node              Linked List


                              |

                        Nodes >= 8


                              |

                        Capacity >=64


                              |

                      Red Black Tree


                              |

                         O(log n)



```

---

# Chapter Complete ✅

HashMap section is now almost complete.

Covered:

✅ Architecture
✅ Node internals
✅ put()
✅ get()
✅ resize
✅ Java 7 vs Java 8
✅ hashCode/equals
✅ collisions
✅ treeification
✅ complexity
✅ thread safety
✅ iterators
✅ production mistakes

---

# Next Chapter

## Chapter 24 — LinkedHashMap Deep Dive ⭐⭐⭐⭐☆

Topics:

* Why LinkedHashMap exists
* Internal structure
* HashMap + Doubly Linked List
* Insertion order vs Access order
* LRU Cache implementation using LinkedHashMap
* removeEldestEntry()
* Interview design questions

This connects directly with your previous **LRU Cache implementation** discussion.
