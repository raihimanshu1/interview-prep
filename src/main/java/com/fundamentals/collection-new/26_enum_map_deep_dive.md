# Chapter 29 — EnumMap Deep Dive ⭐⭐⭐⭐☆

Now we cover the last **special-purpose Map implementation** before moving to the most important concurrent collection:

**ConcurrentHashMap Deep Dive**

EnumMap is less commonly used in day-to-day coding, but senior interviews ask it because it tests whether you understand:

* Enum internals
* Array-based optimization
* Memory efficiency
* When choosing the right collection matters

---

# 1. Why Do We Need EnumMap?

First understand the problem.

Suppose we have an enum:

```java
enum Day {

    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY

}
```

And we want:

```text
Day -> Working Hours
```

Example:

```java
Map<Day,Integer> hours =
new HashMap<>();

hours.put(Day.MONDAY,8);
hours.put(Day.TUESDAY,8);
```

This works.

But HashMap is doing unnecessary work.

---

HashMap internally needs:

```text
hashCode()

+

bucket calculation

+

collision handling

+

Node objects

```

But enum values already have:

* fixed number of constants
* unique ordinal numbers
* known positions

Example:

```java
MONDAY.ordinal() = 0

TUESDAY.ordinal() = 1

WEDNESDAY.ordinal() = 2

```

So why not directly use an array?

That is exactly what EnumMap does.

---

# 2. What Is EnumMap?

Definition:

> EnumMap is a Map implementation designed specifically for enum keys. Internally, it uses an array indexed by enum ordinal values.

---

Example:

```java
EnumMap<Day,Integer> map =
        new EnumMap<>(Day.class);


map.put(Day.MONDAY,8);

```

Internally:

```text
EnumMap


        Array


Index     Value


0   --->  8
1   --->  null
2   --->  null
3   --->  null

```

---

# 3. EnumMap Internal Structure ⭐⭐⭐⭐⭐

Unlike HashMap:

```text
HashMap

Bucket Array

     |
     v

Node

     |
     +-- key
     +-- value
     +-- next

```

---

EnumMap:

```text
EnumMap


        |

        v


 Object[] vals


        |

        v


Index based on enum ordinal


```

---

Example:

Enum:

```java
enum Status {

    NEW,
    PROCESSING,
    COMPLETED

}
```

Ordinal:

```text
NEW          = 0

PROCESSING   = 1

COMPLETED    = 2

```

Array:

```text
index        value


0 ---------- "Created"

1 ---------- "Running"

2 ---------- "Done"

```

---

# 4. Why Is EnumMap Faster Than HashMap? ⭐⭐⭐⭐⭐

Because it avoids hashing completely.

---

HashMap get():

```text
get(key)


   |

   v


hashCode()


   |

   v


calculate bucket


   |

   v


search node


   |

   v


equals()

```

---

EnumMap get():

```text
get(enumKey)


      |

      v


enum.ordinal()


      |

      v


array[index]


```

---

Much simpler.

---

# 5. Complexity of EnumMap

Because it uses an array:

| Operation     | Complexity |
| ------------- | ---------- |
| put()         | O(1)       |
| get()         | O(1)       |
| remove()      | O(1)       |
| containsKey() | O(1)       |

Same as HashMap.

But:

EnumMap has lower constant overhead.

Meaning:

```text
O(1) EnumMap

is faster than

O(1) HashMap

```

in enum-key scenarios.

---

# 6. EnumMap Example ⭐⭐⭐⭐⭐

Example:

```java
enum TrafficLight {

    RED,
    YELLOW,
    GREEN

}
```

Map:

```java
EnumMap<TrafficLight,String> map =
        new EnumMap<>(TrafficLight.class);


map.put(
    TrafficLight.RED,
    "STOP"
);


map.put(
    TrafficLight.GREEN,
    "GO"
);

```

Internally:

```text
Index


0 RED

   |
   v

STOP



1 YELLOW

   |
   v

null



2 GREEN

   |
   v

GO

```

---

# 7. Why Does EnumMap Need Enum Class? ⭐⭐⭐⭐⭐

Constructor:

```java
new EnumMap<>(TrafficLight.class)
```

Why?

Because before adding entries:

EnumMap needs to know:

* How many enum constants exist
* Their ordinal positions

Example:

```java
TrafficLight.values()
```

returns:

```text
[
 RED,
 YELLOW,
 GREEN
]

```

EnumMap creates:

```text
Object[3]

```

---

# 8. EnumMap Does Not Allow Null Keys ⭐⭐⭐⭐☆

Example:

```java
map.put(null,"value");
```

throws:

```text
NullPointerException
```

---

Why?

Because:

```java
null.ordinal()
```

does not exist.

EnumMap needs:

```java
enumKey.ordinal()
```

---

# 9. EnumMap Allows Null Values

Example:

```java
map.put(
    TrafficLight.RED,
    null
);
```

Allowed.

Because:

Key decides array position.

Value can be null.

---

# 10. EnumMap Maintains Enum Order ⭐⭐⭐⭐☆

Important.

Example:

```java
enum Priority {

    HIGH,
    MEDIUM,
    LOW

}
```

Insert:

```java
map.put(LOW,"L");

map.put(HIGH,"H");

map.put(MEDIUM,"M");

```

Iteration:

Output:

```text
HIGH

MEDIUM

LOW

```

Why?

Because EnumMap follows:

```text
enum declaration order
```

not insertion order.

---

Diagram:

```text
Enum declaration


0 HIGH

1 MEDIUM

2 LOW



EnumMap iteration:


0

1

2

```

---

# 11. EnumMap vs HashMap ⭐⭐⭐⭐⭐

Very common interview question.

| Feature            | HashMap      | EnumMap     |
| ------------------ | ------------ | ----------- |
| Key type           | Any object   | Only enum   |
| Internal structure | Hash table   | Array       |
| Hashing            | Yes          | No          |
| Ordering           | No guarantee | Enum order  |
| Null key           | Allowed      | Not allowed |
| Performance        | O(1)         | O(1)        |
| Memory             | Higher       | Lower       |

---

# 12. EnumMap vs EnumSet ⭐⭐⭐⭐☆

Both are optimized for enums.

## EnumSet

Stores:

```text
Set of enums
```

Example:

```java
EnumSet<Day>
```

---

## EnumMap

Stores:

```text
Enum key -> value
```

Example:

```java
EnumMap<Day,String>
```

---

Example:

Permissions:

```java
EnumSet<Permission>

READ
WRITE
DELETE

```

Mapping:

```java
EnumMap<Role,PermissionSet>

ADMIN -> READ WRITE DELETE

USER -> READ

```

---

# 13. EnumMap Memory Advantage ⭐⭐⭐⭐⭐

Compare:

## HashMap

For each entry:

```text
Node object


hash

key reference

value reference

next reference

```

Many objects.

---

## EnumMap

Only:

```text
Object[]

```

Example:

```text
EnumMap


values[]


[0] value

[1] value

[2] value

```

Less memory.

---

# 14. Real Production Use Cases ⭐⭐⭐⭐⭐

## 1. State Machine

Example:

Order lifecycle:

```java
enum State {

CREATED,

PAID,

SHIPPED,

DELIVERED

}

```

Mapping:

```text
State -> Allowed Actions

```

Use:

```java
EnumMap<State,List<Action>>

```

---

## 2. Configuration Management

Example:

```java
enum Environment {

DEV,

TEST,

PROD

}

```

Mapping:

```text
Environment -> Configuration

```

---

## 3. Permission System

Example:

```java
enum Role {

ADMIN,

USER,

GUEST

}

```

Mapping:

```text
Role -> Permissions

```

---

# 15. EnumMap Thread Safety ⭐⭐⭐⭐☆

EnumMap is:

```text
NOT thread-safe
```

Example:

```java
EnumMap<State,String> map =
new EnumMap<>(State.class);
```

Multiple threads modifying:

Unsafe.

---

Solution:

```java
Collections.synchronizedMap(
    enumMap
);

```

or use external synchronization.

---

# 16. EnumMap Internal Flow ⭐⭐⭐⭐⭐

put():

```text
put(State.CREATED,"value")


          |

          v


State.ordinal()


          |

          v


Find array index


          |

          v


values[index]=value


```

---

get():

```text
get(State.CREATED)


          |

          v


ordinal()


          |

          v


values[index]


          |

          v


return value

```

---

# 17. Why Not Always Use EnumMap?

Because:

Only works when:

```text
Keys are enum values
```

Example:

Good:

```java
EnumMap<Day,String>
```

Bad:

```java
EnumMap<String,String>
```

Impossible.

---

# 18. Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. What is EnumMap?

Answer:

> EnumMap is a specialized Map implementation for enum keys that internally uses an array indexed by enum ordinal values.

---

## Q2. Why is EnumMap faster than HashMap?

Answer:

> Because it avoids hashing and collision handling. It directly accesses an array position using enum ordinal.

---

## Q3. Does EnumMap maintain insertion order?

Answer:

> No. It maintains enum declaration order.

---

## Q4. Why does EnumMap require enum class in constructor?

Answer:

> It needs the enum type to determine all possible keys and allocate the internal array.

---

## Q5. Can EnumMap have null keys?

Answer:

> No, because EnumMap uses enum ordinal values internally and null has no ordinal.

---

# 19. Complete Map Family Final Picture ⭐⭐⭐⭐⭐

Now the complete Map family:

```text
                         Map


                          |


 -----------------------------------------------------------


 HashMap          LinkedHashMap          TreeMap


   |                   |                    |


Hash Table       Hash + DLL          Red Black Tree


 O(1)                O(1)               O(log n)



 -----------------------------------------------------------


 Hashtable


   |

 Global Lock



 -----------------------------------------------------------


 WeakHashMap


   |

 Weak References + GC



 -----------------------------------------------------------


 IdentityHashMap


   |

 == comparison



 -----------------------------------------------------------


 EnumMap


   |

 Array + Enum.ordinal()

```

---

# Chapter Complete ✅

Covered:

✅ Why EnumMap exists
✅ Internal array implementation
✅ Enum ordinal() usage
✅ Complexity
✅ Memory advantage
✅ Null behaviour
✅ Ordering behaviour
✅ EnumMap vs HashMap
✅ EnumMap vs EnumSet
✅ Production examples
✅ Interview questions

---

Next Chapter:

# Chapter 30 — ConcurrentHashMap Deep Dive (Advanced) ⭐⭐⭐⭐⭐

This is the **most important Map topic after HashMap**.

We will go deeper than earlier:

* Java 7 Segment locking
* Java 8 redesign
* Node, TreeBin, ForwardingNode
* CAS insertion
* synchronized blocks
* resize mechanism
* sizeCtl
* counterCells
* computeIfAbsent internals
* Atomic operations
* Interview traps

This will be one of the highest-value chapters for a 7+ years Java interview.
