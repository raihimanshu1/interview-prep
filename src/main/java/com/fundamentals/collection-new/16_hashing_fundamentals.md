# Chapter 16 — Hashing Fundamentals (`hashCode()`, `equals()`, Hash Function, Bucket Calculation) ⭐⭐⭐⭐⭐

Before going deeper into HashMap internals, we need to understand the **foundation**:

> How does Java decide where a key should be stored?

The answer:

```
hashCode()
+
hash function
+
bucket calculation
+
equals()
```

Almost every HashMap interview question eventually reaches here.

---

# 1. What is Hashing?

Hashing is a technique to convert a large input into a smaller fixed-size value.

Simple idea:

```
Input

   |
   |
   v

Hash Function

   |
   |
   v

Hash Value
```

Example:

```
"John"

        |
        v

hashCode()

        |
        v

2314567

```

The hash value helps us decide the storage location.

---

# 2. Real World Example

Imagine a library.

Without hashing:

You search:

```
Books:

A
B
C
D
E
...
100000 books
```

Need to scan everything.

Complexity:

```
O(n)
```

---

With hashing:

Book name:

```
Java Programming
```

Hash function:

```
Java Programming

       |
       v

Shelf Number 50

```

Directly go to shelf 50.

Complexity:

```
O(1)
```

---

# 3. HashMap Uses Hashing

HashMap flow:

```
              Key


               |
               v


          hashCode()


               |
               v


          Hash Function


               |
               v


          Bucket Index


               |
               v


          Store / Search


```

---

Example:

```java
Map<String,Integer> map = new HashMap<>();

map.put("Apple",100);
```

Internally:

```
"Apple"

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

Bucket 6

   |
   v

Store value 100

```

---

# 4. What is hashCode() in Java?

Every Java object inherits:

```java
Object.hashCode()
```

Definition:

> Returns an integer hash value representing the object.

Example:

```java
String name = "Java";

System.out.println(name.hashCode());
```

Output:

```
2301506
```

---

Important:

`hashCode()` does NOT return memory address.

Many developers misunderstand this.

---

# 5. hashCode() Contract ⭐⭐⭐⭐⭐

Java defines rules.

## Rule 1

If two objects are equal:

```java
a.equals(b)
```

then:

their hashCode must be same.

Example:

```java
String a = new String("Java");
String b = new String("Java");
```

Both:

```
equals() = true
```

Therefore:

```
a.hashCode()
=
b.hashCode()

```

---

## Rule 2

Same hashCode does NOT mean objects are equal.

Example:

```
Object A

hashCode = 100


Object B

hashCode = 100

```

Possible:

```
A.equals(B)

false

```

This is called:

```
Collision
```

---

# 6. HashCode Collision ⭐⭐⭐⭐⭐

Collision means:

> Multiple keys produce the same hash value.

Example:

```
Key A

hashCode()

   |
   v

100


Key B

hashCode()

   |
   v

100

```

Both want the same bucket.

HashMap handles this.

---

Before Java 8:

```
Bucket


A

|

B

|

C

```

After Java 8:

```
Bucket


        B

      /   \

     A     C

```

---

# 7. hashCode() vs equals() ⭐⭐⭐⭐⭐

This is one of the most asked questions.

## hashCode()

Answers:

> Which bucket should I check?

---

## equals()

Answers:

> Is this the exact key I am looking for?

---

Example:

```
HashMap


Key

 |
 |
 v

hashCode()

 |
 |
 v

Bucket

 |
 |
 v

equals()

 |
 |
 v

Correct Key?

```

---

# 8. Why Both Are Needed?

Suppose:

```java
map.get("John");
```

Step 1:

Calculate hash:

```
"John"

 |
 v

hashCode()

 |
 v

Bucket 5

```

Now bucket contains:

```
Bucket 5


John -> 100

Johnny -> 200

Jon -> 300

```

Hash is same.

Now:

```
equals()

```

finds exact key.

---

# 9. Custom Object as HashMap Key ⭐⭐⭐⭐⭐

Bad example:

```java
class Employee {

    int id;

    String name;

}
```

Usage:

```java
Employee e =
    new Employee(1,"John");


map.put(e,"Developer");
```

Problem:

Default Object methods:

```
hashCode()
```

and

```
equals()
```

compare object identity.

Meaning:

```
same data

but different objects

```

are treated differently.

---

Example:

```java
Employee e1 =
new Employee(1,"John");


Employee e2 =
new Employee(1,"John");
```

Without override:

```
e1.equals(e2)

false

```

Because:

```
Different memory references

```

---

# 10. Correct Implementation ⭐⭐⭐⭐⭐

Override:

```
equals()

hashCode()

```

Example:

```java
class Employee {

    private int id;

    private String name;


    public Employee(
        int id,
        String name
    ){
        this.id=id;
        this.name=name;
    }


    @Override
    public boolean equals(Object obj){

        if(this == obj)
            return true;


        if(!(obj instanceof Employee))
            return false;


        Employee other =
            (Employee)obj;


        return this.id == other.id;

    }


    @Override
    public int hashCode(){

        return Integer.hashCode(id);

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
```

Result:

```
equals()

true

```

and:

```
hashCode()

same

```

---

# 11. HashMap Bucket Calculation ⭐⭐⭐⭐⭐

Now the important internal part.

HashMap does not directly use:

```
hashCode % capacity
```

Java uses bit operations.

---

Example:

Capacity:

```
16
```

Indexes:

```
0 - 15
```

Need index:

```
0 to 15
```

---

Formula:

Java 8:

```java
index =
(hash) & (n-1);
```

where:

```
n = array length
```

---

Example:

Capacity:

```
16
```

Binary:

```
16

10000

```

n-1:

```
15

01111

```

---

Hash:

```
10110110

```

AND:

```
10110110

01111111

---------

00110110

```

Result:

```
bucket index

```

---

# 12. Why Use Bitwise AND Instead of Modulo?

Question:

Why not:

```java
hash % capacity
```

?

Because:

Bit operations are faster.

AND:

```
CPU level operation

```

Modulo:

```
division operation

```

Division is expensive.

---

# 13. Why Capacity Must Be Power of 2 ⭐⭐⭐⭐⭐

Important interview question.

HashMap capacity:

```
16

32

64

128

```

always power of two.

Why?

Because:

```
index = hash & (n-1)

```

works correctly.

---

Example:

Capacity:

```
16
```

n-1:

```
15

01111

```

This uses lower bits efficiently.

---

# 14. Hash Spreading in Java 8 ⭐⭐⭐⭐⭐

Problem:

Some hashCodes have poor distribution.

Example:

```
100000000000000

100000000000001

100000000000010

```

High bits differ.

Low bits same.

But bucket calculation uses low bits.

---

Solution:

Hash spreading.

Java 8:

```java
h ^ (h >>> 16)
```

---

Meaning:

Take high bits:

```
h >>> 16

```

Move them down.

Then XOR:

```
original hash

      ^

shifted hash

```

---

Diagram:

```
Original hash


11110000 11110000


          XOR


00000000 11110000



Result


11110000 00000000

```

More bits participate.

---

# 15. Complete HashMap Key Lookup Flow ⭐⭐⭐⭐⭐

```
get(key)


   |
   v


key.hashCode()


   |
   v


hash spreading


   |
   v


calculate bucket index


   |
   v


find bucket


   |
   v


compare hash


   |
   v


equals()


   |
   v


return value


```

---

# 16. Common Interview Trap

Interviewer:

> If two keys have same hashCode, will they replace each other?

Wrong:

```
Yes

```

Correct:

```
No.

Hash collision only means same bucket.

HashMap uses equals() to decide whether keys are same.

```

---

Example:

```java
map.put(
new Employee(1),
"A"
);


map.put(
new Employee(2),
"B"
);

```

Even if:

```
hashCode same

```

Both exist if:

```
equals=false

```

---

# 17. Why String Is a Good HashMap Key ⭐⭐⭐⭐⭐

String is:

## Immutable

Cannot change after creation.

---

## Overrides equals()

Example:

```java
"Java".equals(
new String("Java")
)

```

true.

---

## Good hashCode()

String calculates hash based on characters.

Example:

```
J
a
v
a

```

---

Therefore:

```java
Map<String,Object>
```

is very common.

---

# 18. Complexity Revisited ⭐⭐⭐⭐⭐

Now we understand why:

## Average

Good hash distribution:

```
hash

 |

bucket

 |

node

```

Complexity:

```
O(1)

```

---

## Worst Case

Bad hashing:

```
Bucket


A

|

B

|

C

|

D

```

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

# 19. Interview Questions ⭐⭐⭐⭐⭐

## Q1. Difference between hashCode() and equals()?

Answer:

> hashCode() identifies the bucket where an object may exist, while equals() confirms whether two objects are logically equal.

---

## Q2. Can two unequal objects have same hashCode?

Answer:

> Yes. This is called a hash collision.

---

## Q3. Can two equal objects have different hashCodes?

Answer:

> No. It violates the hashCode contract.

---

## Q4. Why override hashCode when overriding equals?

Answer:

> Hash-based collections first use hashCode to locate the bucket. Without consistent hashCode, equal objects may be stored in different buckets and lookup fails.

---

## Q5. Why HashMap capacity is power of two?

Answer:

> It allows efficient bucket calculation using bitwise AND and provides better hash distribution.

---

# Final Mental Model

Remember:

```
HashMap


Key


 |

hashCode()


 |

Hash Spreading


 |

Bucket Index


 |

Bucket


 |

equals()


 |

Correct Entry


```

The golden interview answer:

> "HashMap uses hashCode() to find the bucket and equals() to identify the exact key. hashCode provides fast location, while equals guarantees correctness. Collisions are handled internally using linked lists and Java 8 treeifies heavily populated buckets into Red-Black trees."

---

# Chapter Complete ✅

Next Chapter:

# Chapter 17 — HashMap Internal Structure Deep Dive ⭐⭐⭐⭐⭐

Topics:

* `Node<K,V>` structure
* Table initialization
* Lazy initialization
* Load factor
* Threshold calculation
* Resize mechanism
* Rehashing
* Why resizing is expensive
* Java 7 vs Java 8 resize difference
* Interview complexity questions

This is where we go inside the actual HashMap source code.
