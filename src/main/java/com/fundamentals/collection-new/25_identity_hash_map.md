# Chapter 28 — IdentityHashMap Deep Dive ⭐⭐⭐⭐☆

Now we cover one of the **least commonly used but highly interview-relevant Map implementations**.

IdentityHashMap is interesting because it breaks a fundamental rule we have learned so far:

> "Maps compare keys using `equals()`."

IdentityHashMap does **not** do that.

It compares keys using:

```java
==
```

(reference equality)

This makes it completely different from:

* HashMap
* LinkedHashMap
* TreeMap
* ConcurrentHashMap

---

# 1. Why Do We Need IdentityHashMap?

First understand normal Map behaviour.

Example:

```java
String a = new String("Java");

String b = new String("Java");
```

Memory:

```text
a
 |
 v
+---------+
|  Java   |
+---------+


b
 |
 v
+---------+
|  Java   |
+---------+

```

Two different objects.

But content is same.

---

Now:

```java
System.out.println(a.equals(b));
```

Output:

```text
true
```

because String overrides equals():

```java
"Java".equals("Java")
```

---

But:

```java
System.out.println(a == b);
```

Output:

```text
false
```

because references are different.

---

Normal HashMap uses:

```text
hashCode()
+
equals()

```

IdentityHashMap uses:

```text
==
```

---

# 2. What Is IdentityHashMap?

Definition:

> IdentityHashMap is a Map implementation that compares keys using reference equality (`==`) instead of logical equality (`equals()`).

---

Example:

```java
Map<String,Integer> map =
        new IdentityHashMap<>();

String a = new String("Java");

String b = new String("Java");


map.put(a,1);

map.put(b,2);

```

Result:

```
{
 Java=1,
 Java=2
}

```

Why?

Because:

```java
a == b
```

is false.

They are two different objects.

---

# 3. HashMap vs IdentityHashMap ⭐⭐⭐⭐⭐

Let's compare.

---

## HashMap

Comparison:

```java
key.equals(existingKey)
```

Example:

```java
String a =
new String("Java");


String b =
new String("Java");

```

HashMap:

```text
a.equals(b)

true

```

So:

```text
One entry

```

---

## IdentityHashMap

Comparison:

```java
key == existingKey
```

Result:

```text
a == b

false

```

So:

```text
Two entries

```

---

Diagram:

```text
HashMap


a("Java")

      |
      |
 equals()

      |
      v

b("Java")


Same key



--------------------------------


IdentityHashMap


a("Java")

      |
      |
      ==

      |
      X

b("Java")


Different keys

```

---

# 4. Internal Structure of IdentityHashMap ⭐⭐⭐⭐⭐

Important interview point:

IdentityHashMap does NOT use normal HashMap nodes.

---

HashMap:

```text
Bucket Array

      |
      v

Node

 |
 +-- key
 |
 +-- value
 |
 +-- next

```

---

IdentityHashMap:

Uses:

```text
Object[]

```

array.

---

Example:

```text
Table


index 0

key

index 1

value


index 2

key

index 3

value


```

---

Diagram:

```text
IdentityHashMap Table


+-----+-----+-----+-----+
| K1  | V1  | K2  | V2 |
+-----+-----+-----+-----+


Key and value stored adjacent


```

---

Why?

Because it does not need:

* Node objects
* Linked lists
* equals-based lookup

---

# 5. How Lookup Works ⭐⭐⭐⭐⭐

Example:

```java
map.get(key);
```

Flow:

```text
get(key)


    |

    v


calculate identity hash


    |

    v


find array position


    |

    v


compare using ==


    |

    v


return value

```

---

Important:

It uses:

```java
System.identityHashCode()
```

not:

```java
hashCode()
```

---

# 6. identityHashCode vs hashCode ⭐⭐⭐⭐⭐

Normal HashMap:

```java
key.hashCode()
```

---

IdentityHashMap:

```java
System.identityHashCode(key)
```

---

Difference:

`hashCode()` can be overridden.

Example:

```java
class User {

    int id;


    public int hashCode(){
        return id;
    }
}

```

---

Two objects:

```java
User u1 = new User(10);

User u2 = new User(10);

```

hashCode:

```text
u1.hashCode()

10


u2.hashCode()

10

```

Same.

---

But:

```java
System.identityHashCode(u1)

!=

System.identityHashCode(u2)

```

because objects are different.

---

# 7. Example With Custom Object ⭐⭐⭐⭐⭐

Class:

```java
class Employee {

    int id;


    Employee(int id){
        this.id=id;
    }


    @Override
    public boolean equals(Object obj){

        Employee e=(Employee)obj;

        return this.id == e.id;
    }


    @Override
    public int hashCode(){

        return id;
    }
}

```

---

Create:

```java
Employee e1 =
new Employee(101);


Employee e2 =
new Employee(101);

```

---

HashMap:

```java
Map<Employee,String> map =
new HashMap<>();


map.put(e1,"A");

map.put(e2,"B");

```

Result:

```
one entry

```

because:

```java
e1.equals(e2)

true

```

---

IdentityHashMap:

```java
Map<Employee,String> map =
new IdentityHashMap<>();


map.put(e1,"A");

map.put(e2,"B");

```

Result:

```
two entries

```

because:

```java
e1 == e2

false

```

---

# 8. Why Was IdentityHashMap Created?

This is the important interview question.

Question:

> "Why would anyone want identity comparison?"

Answer:

Some frameworks need to track **object identity**, not object equality.

---

Example:

Serialization.

Suppose:

```java
Object A

references

Object B

```

and:

```java
Object C

references

Object B

```

Serializer must know:

Is this the same object?

or

Two equal objects?

---

Identity matters.

---

# 9. Real Production Use Cases ⭐⭐⭐⭐⭐

## 1. Serialization Frameworks

Example:

Object graph:

```text
        Employee


          |

          v


       Address


          ^

          |

        Manager

```

Serializer maintains:

```text
Object instance -> serialized ID

```

IdentityHashMap helps avoid duplicate serialization.

---

## 2. Object Tracking

Example:

Debugging tool:

```text
Object instance

        |

        v

Metadata

```

Two objects may have same values but need separate tracking.

---

## 3. Graph Algorithms

Graph traversal:

```text
Node Object

        |

        v

Visited

```

Need:

same object?

not same value?

---

# 10. IdentityHashMap Allows Duplicate Logical Keys ⭐⭐⭐⭐⭐

Example:

```java
String a =
new String("Java");


String b =
new String("Java");


map.put(a,1);

map.put(b,2);

```

IdentityHashMap:

```
a -> 1
b -> 2

```

Even though:

```java
a.equals(b)

true

```

---

# 11. Null Handling ⭐⭐⭐☆

IdentityHashMap supports:

```java
null key
```

and:

```java
null value
```

Example:

```java
map.put(null,"Java");
```

Allowed.

---

Internally:

It uses special handling because:

```java
null == null

true

```

---

# 12. IdentityHashMap Is NOT Thread Safe ⭐⭐⭐⭐☆

Example:

```java
IdentityHashMap map =
new IdentityHashMap();

```

Multiple threads:

```text
Thread A

put()


Thread B

remove()

```

Unsafe.

---

Need:

```java
Collections.synchronizedMap(
    new IdentityHashMap<>()
);

```

---

# 13. IdentityHashMap Complexity

Because it uses array-based hashing:

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

# 14. IdentityHashMap vs WeakHashMap ⭐⭐⭐⭐⭐

Interesting comparison.

| Feature         | IdentityHashMap          | WeakHashMap              |
| --------------- | ------------------------ | ------------------------ |
| Key comparison  | `==`                     | `equals()`               |
| Reference type  | Strong                   | Weak                     |
| GC removes keys | No                       | Yes                      |
| Main purpose    | Object identity tracking | Memory-sensitive caching |
| Common use      | Serialization            | Metadata cache           |

---

# 15. IdentityHashMap vs HashMap ⭐⭐⭐⭐⭐

| Feature                | HashMap    | IdentityHashMap    |
| ---------------------- | ---------- | ------------------ |
| Equality               | equals()   | ==                 |
| Hash                   | hashCode() | identityHashCode() |
| Logical equality       | Yes        | No                 |
| Object identity        | No         | Yes                |
| Normal application use | Yes        | Rare               |

---

# 16. Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. Difference between HashMap and IdentityHashMap?

Answer:

> HashMap compares keys using equals() and hashCode(), whereas IdentityHashMap compares keys using reference equality (==) and System.identityHashCode().

---

## Q2. When would you use IdentityHashMap?

Answer:

> When tracking actual object instances is required, such as serialization frameworks, object graphs, or debugging tools.

---

## Q3. Why can IdentityHashMap contain duplicate keys?

Answer:

> Because two objects with equal values are still different references, and IdentityHashMap considers them different keys.

---

## Q4. Does IdentityHashMap violate Map contract?

Very important.

Answer:

Yes, partially.

The Map contract says:

```text
keys are compared using equals()

```

IdentityHashMap intentionally breaks this rule.

---

# 17. Complete Map Family Progress

Current understanding:

```text
                         Map


                          |


 -------------------------------------------------


 HashMap       LinkedHashMap       TreeMap


    |               |                  |


 Hashing       Hash + DLL       Red Black Tree


 O(1)             O(1)             O(log n)



 -------------------------------------------------


 Hashtable


    |

 Global Lock



 -------------------------------------------------


 WeakHashMap


    |

 Weak References + GC



 -------------------------------------------------


 IdentityHashMap


    |

 Reference Equality ==


```

---

# Chapter Complete ✅

Covered:

✅ Why IdentityHashMap exists
✅ `==` vs `equals()`
✅ identityHashCode()
✅ Internal array structure
✅ Difference from HashMap
✅ Duplicate logical keys
✅ Serialization use cases
✅ Thread safety
✅ Complexity
✅ Interview traps

---

Next Chapter:

# Chapter 29 — EnumMap Deep Dive ⭐⭐⭐⭐☆

Topics:

* Why EnumMap exists
* Internal array-based implementation
* Why EnumMap is faster than HashMap for enums
* Enum ordinal()
* Memory efficiency
* EnumMap vs HashMap
* Production use cases
* Interview questions

This will complete the remaining special-purpose Map implementations before starting **ConcurrentHashMap deep dive again at advanced level**.
