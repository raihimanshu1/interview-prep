# Chapter 2 — ArrayList Deep Dive ⭐⭐⭐⭐⭐

`ArrayList` is probably the most used collection in Java applications.

For a **7+ years Java developer interview**, you should not answer:

> "ArrayList is a dynamic array."

That is only beginner level.

A senior answer should cover:

* Internal structure
* Memory layout
* Resizing mechanism
* Growth factor
* Amortized complexity
* Why random access is O(1)
* Why insertion/removal is O(n)
* Iterator behavior
* Fail-fast mechanism
* ArrayList vs LinkedList trade-offs

---

# 1. What Problem Does ArrayList Solve?

Normal array:

```java
String[] names = new String[5];
```

Problem:

Size is fixed.

Example:

```text
Array size = 5


+---+---+---+---+---+
| A | B | C |   |   |
+---+---+---+---+---+

```

Need sixth element:

```text
Cannot add
```

because memory is already allocated.

---

ArrayList solves this:

> A resizable array that grows automatically when required.

---

# 2. ArrayList High-Level Structure

Internally:

```text
ArrayList Object


+----------------------+
| size                 |
| elementData          |
| modCount             |
+----------------------+
            |
            |
            v


Object[] elementData


Index:

0       1       2       3

A       B       C       null


```

Important:

ArrayList itself does NOT store elements directly.

It stores a reference to an internal array.

---

# 3. Class Structure

Simplified source:

```java
public class ArrayList<E>
        extends AbstractList<E>
        implements List<E>,
                   RandomAccess {


    private Object[] elementData;


    private int size;


}
```

Important fields:

---

## elementData

Actual storage.

Example:

```java
elementData[0]="Java";

elementData[1]="Spring";

```

---

## size

Number of actual elements.

Example:

```text
Capacity = 10

Size = 3


Array:

[A][B][C][ ][ ][ ][ ][ ][ ][ ]

```

---

## Capacity vs Size ⭐⭐⭐⭐⭐

Very common interview question.

Capacity:

> How much data the internal array can hold.

Size:

> How many elements are currently stored.

Example:

```text
Capacity = 10

Size = 4


+---+---+---+---+---+---+---+---+---+---+
| A | B | C | D |   |   |   |   |   |   |
+---+---+---+---+---+---+---+---+---+---+


```

---

# 4. Creating ArrayList

Example:

```java
List<String> list =
        new ArrayList<>();
```

What happens internally?

Java creates:

```text
ArrayList object


elementData = empty array


size = 0

```

Modern Java versions use lazy allocation.

Meaning:

No actual 10-size array is created immediately.

---

# 5. Initial Capacity

Older Java:

```java
new ArrayList<>();
```

created:

```text
capacity = 10

```

Modern Java:

```java
new ArrayList<>();

```

creates empty array first.

On first add:

```text
capacity becomes 10

```

---

# 6. Adding Element — add(E)

Example:

```java
list.add("Java");
```

Let's understand complete flow.

---

## Step 1

Check capacity.

```text
Current array:


[]

size = 0

capacity = 0

```

---

## Step 2

Ensure capacity.

```text
Need space?

Yes

```

Create internal array.

```text
Capacity = 10


+---+---+---+---+---+
|   |   |   |   |   |
+---+---+---+---+---+

```

---

## Step 3

Insert element.

```text
Index = size


size = 0


elementData[0]="Java"


```

Final:

```text
+------+-------+-------+
| Java |       |       |
+------+-------+-------+

size = 1

```

---

# 7. ArrayList Resize Mechanism ⭐⭐⭐⭐⭐

This is a very common interview question.

Question:

> What happens when ArrayList becomes full?

Example:

Current:

```text
Capacity = 5

Size = 5


[A][B][C][D][E]

```

Need:

```java
add("F");
```

---

## Step 1

Create bigger array.

Java 8 growth:

```java
newCapacity =
oldCapacity + oldCapacity/2

```

Meaning:

```text
Growth factor = 1.5x

```

---

Example:

Old:

```text
5

```

New:

```text
5 + 2

=7

```

---

## Step 2

Copy elements.

```text
Old array


[A][B][C][D][E]


        |
        |
        v


New array


[A][B][C][D][E][ ][ ]

```

---

## Step 3

Add new element.

```text
[A][B][C][D][E][F][ ]

```

---

# 8. Resize Complexity

Important:

Resize requires copying.

Example:

```text
Old Array

[A][B][C][D]


Copy


New Array

[A][B][C][D][ ][ ]

```

Cost:

```text
O(n)

```

---

But why add() is O(1)?

Because resizing does not happen every time.

This is called:

# Amortized Complexity ⭐⭐⭐⭐⭐

---

Example:

Operations:

```text
add A

add B

add C

add D

resize

add E

add F

add G

```

Most operations:

```text
O(1)

```

Occasional:

```text
O(n)

```

Average over many operations:

```text
O(1)

```

---

# 9. ArrayList get(index)

Example:

```java
list.get(3);
```

Internal:

```text
Index 3


elementData[3]


Direct memory access

```

Diagram:

```text

Index:

0       1       2       3

A       B       C       D
                        ^
                        |
                     get(3)


```

No traversal.

Therefore:

# Complexity

```text
O(1)

```

---

# 10. ArrayList add(index,value)

Example:

```java
list.add(2,"Java");
```

Existing:

```text
Index:

0    1    2    3

A    B    C    D

```

Need insert at index 2.

Elements shift right:

```text
Before:


A    B    C    D



After shift:


A    B    _    C    D


Insert Java:


A    B    Java C    D

```

---

Need movement of elements.

Complexity:

```text
O(n)

```

---

# 11. ArrayList remove(index)

Example:

```java
list.remove(1);
```

Before:

```text
A    B    C    D

```

Remove B:

```text
A    C    D    _

```

Elements shift left.

Complexity:

```text
O(n)

```

---

# 12. ArrayList contains()

Example:

```java
list.contains("Java");
```

Internally:

Linear search.

```text
A

compare


B

compare


Java

found

```

Worst case:

```text
Need to scan all elements

```

Complexity:

```text
O(n)

```

---

# 13. Complete Complexity Table

| Operation        |     Complexity |
| ---------------- | -------------: |
| get(index)       |           O(1) |
| set(index,value) |           O(1) |
| add(end)         | O(1) amortized |
| add(index,value) |           O(n) |
| remove(index)    |           O(n) |
| contains()       |           O(n) |
| indexOf()        |           O(n) |
| clear()          |           O(n) |

---

# 14. Why ArrayList Is Faster Than LinkedList?

Very common interview question.

## ArrayList

Memory:

```text
Continuous


[A][B][C][D]

```

CPU cache friendly.

Access:

```text
index → direct

```

---

## LinkedList

Memory:

```text
Node1

 |

Node2

 |

Node3

```

Need traversal.

Example:

get(100):

```text
Node1
 |
Node2
 |
Node3
 |
...
 |
Node100

```

---

Comparison:

| Feature            | ArrayList     | LinkedList         |
| ------------------ | ------------- | ------------------ |
| Internal structure | Dynamic array | Doubly linked list |
| Random access      | O(1)          | O(n)               |
| Insert middle      | O(n)          | O(n)               |
| Remove middle      | O(n)          | O(1) if node known |
| Memory             | Less          | More               |
| Cache locality     | Better        | Poor               |

---

# 15. Why LinkedList Is Usually Not Preferred?

Many developers think:

"Insert/delete is faster in LinkedList."

Incomplete.

Example:

```java
list.add(500,"Java");
```

LinkedList:

First:

Find node 500.

Traversal:

```text
Node1 → Node2 → ... → Node500

```

Cost:

```text
O(n)

```

Then insertion:

```text
O(1)

```

Total:

```text
O(n)

```

ArrayList:

Shift elements:

```text
O(n)

```

Both are O(n).

But ArrayList usually wins because:

* better CPU cache
* less memory overhead

---

# 16. Iterator and modCount

ArrayList maintains:

```java
protected transient int modCount;
```

Every structural modification:

```text
add()

remove()

clear()

```

increments:

```text
modCount++

```

Iterator stores:

```java
expectedModCount

```

Example:

```java
Iterator iterator =
        list.iterator();

```

Initially:

```text
modCount = 5

expectedModCount = 5

```

Another modification:

```text
list.add("X");

modCount = 6

```

Iterator next:

```text
expected != actual

```

throws:

```text
ConcurrentModificationException

```

---

# 17. ArrayList Thread Safety

Important:

ArrayList is NOT thread-safe.

Example:

Thread A:

```java
add(A)
```

Thread B:

```java
add(B)
```

Possible:

Lost update.

---

Solutions:

## Option 1

```java
Collections.synchronizedList(
    new ArrayList<>()
);

```

---

## Option 2

Use:

```java
CopyOnWriteArrayList

```

for read-heavy scenarios.

---

# 18. ArrayList Interview Questions

## Q1. Why ArrayList get() is O(1)?

Answer:

> ArrayList stores elements in a contiguous Object array, so accessing an element requires direct index lookup without traversal.

---

## Q2. Why add() is amortized O(1)?

Answer:

> Most insertions happen at the end without resizing. Occasionally resizing requires O(n) copying, but averaged over many insertions the complexity remains O(1).

---

## Q3. What is the growth factor of ArrayList?

Answer:

> Java 8 increases capacity by approximately 1.5 times:

```java
oldCapacity + oldCapacity >> 1
```

---

## Q4. ArrayList vs LinkedList?

Senior answer:

> ArrayList provides faster random access because of contiguous memory and better cache locality. LinkedList only provides O(1) insertion/removal when the node reference is already available, otherwise searching makes it O(n).

---

## Q5. Why initial capacity matters?

Answer:

> If we know expected size, providing initial capacity avoids repeated resizing and array copying.

Example:

```java
new ArrayList<>(10000);

```

---

# Chapter Summary

ArrayList mental model:

```text
ArrayList


        Object[]

            |
            |
            v


Continuous memory


            |
            |
            v


Fast index access


            |
            |
            v


O(1) get()


            |
            |
            v


Resize when full


            |
            |
            v


Amortized O(1) add()

```

---

Next Chapter:

# Chapter 3 — LinkedList Deep Dive ⭐⭐⭐⭐☆

We will cover:

* Doubly linked list internals
* Node structure
* add/remove flow diagrams
* Why removal can be O(1)
* Why get is O(n)
* LinkedList as List + Deque
* ArrayList vs LinkedList interview traps
* Memory overhead analysis
* Real-world usage (and why rare in modern applications)
