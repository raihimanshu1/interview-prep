# Chapter 32 — Collections Performance Analysis & Big-O Complexity ⭐⭐⭐⭐⭐

This chapter is extremely important for **senior Java interviews**.

This directly addresses the common mistake:

> Interviewer: What is HashMap complexity?
> Candidate: O(1)
> Interviewer: Worst case?
> Candidate: O(1)
> ❌ Rejected

The correct senior-level answer:

> "HashMap provides O(1) average-case complexity for get and put operations. In the worst case, due to hash collisions, it can degrade to O(n) in older implementations. Since Java 8, buckets can be converted into Red-Black Trees, improving worst-case lookup complexity to O(log n) when collisions exceed the treeification threshold."

This chapter will build that thinking for **all collections**.

---

# 1. What Is Big-O Complexity?

Big-O describes:

> How execution time or memory grows as input size increases.

Example:

Suppose:

```java
int[] arr = new int[1000];
```

Finding:

```java
arr[500]
```

takes the same time whether array size is:

```
10
100
1000000
```

Because direct indexing.

Complexity:

```
O(1)
```

---

# 2. Common Complexity Classes

From fastest to slowest:

```
O(1)

   ↓

O(log n)

   ↓

O(n)

   ↓

O(n log n)

   ↓

O(n²)

   ↓

O(2ⁿ)
```

---

## Example Growth

Input:

```
n = 1000
```

Operations:

| Complexity | Operations |
| ---------- | ---------- |
| O(1)       | 1          |
| O(log n)   | ~10        |
| O(n)       | 1000       |
| O(n²)      | 1,000,000  |

---

# 3. ArrayList Complexity ⭐⭐⭐⭐⭐

Internal structure:

```
ArrayList

      |
      v

Object[] array


Index:

0  1  2  3  4

A  B  C  D  E

```

---

# 3.1 get(index)

Example:

```java
list.get(3);
```

Internally:

```java
elementData[3]
```

Direct array access.

Complexity:

```
O(1)
```

---

Flow:

```
get(3)

 |

 v

array[3]

 |

 v

return value

```

---

# 3.2 add(element)

Example:

```java
list.add("Java");
```

If capacity available:

```
index = size

insert

```

Complexity:

```
O(1)
```

---

But sometimes resize happens.

Example:

Current:

```
capacity = 10

size = 10
```

Add new element:

```
Need bigger array
```

---

Resize:

```
Old Array

[A][B][C][D]


       |
       v


New Array

[A][B][C][D][ ][ ][ ]

```

Copying takes:

```
O(n)
```

---

Therefore:

Amortized complexity:

```
O(1)
```

Worst case:

```
O(n)
```

---

# 3.3 add(index, element)

Example:

```java
list.add(2,"Java");
```

Before:

```
0 1 2 3 4

A B C D E

```

Need shifting:

```
0 1 2 3 4 5

A B X C D E

```

Elements moved.

Complexity:

```
O(n)
```

---

# 3.4 remove(index)

Example:

```java
list.remove(2);
```

Before:

```
A B C D E

```

After removing C:

```
A B D E

```

Need shifting.

Complexity:

```
O(n)
```

---

## ArrayList Summary

| Operation     | Complexity     |
| ------------- | -------------- |
| get           | O(1)           |
| set           | O(1)           |
| add(end)      | O(1) amortized |
| add(index)    | O(n)           |
| remove(index) | O(n)           |
| search        | O(n)           |

---

# 4. LinkedList Complexity ⭐⭐⭐⭐⭐

Internal structure:

Doubly linked list:

```
Node

+------+------+
|prev  | next |
| data |
+------+------+

```

Example:

```
null

 |
 v

A <-> B <-> C <-> D

                        

```

---

# 4.1 get(index)

Example:

```java
list.get(500);
```

Problem:

No direct index.

Must traverse.

```
head

 |

 v

A -> B -> C -> .... -> 500

```

Complexity:

```
O(n)
```

---

# 4.2 addFirst()

Example:

```java
list.addFirst("Java");
```

Only pointer change:

```
Before:

A <-> B


After:

Java <-> A <-> B

```

Complexity:

```
O(1)
```

---

# 4.3 addLast()

Complexity:

```
O(1)
```

because LinkedList maintains tail reference.

---

# 4.4 Remove middle element

Need traversal first.

Complexity:

```
O(n)
```

---

## LinkedList Summary

| Operation   | Complexity |
| ----------- | ---------- |
| get         | O(n)       |
| addFirst    | O(1)       |
| addLast     | O(1)       |
| removeFirst | O(1)       |
| removeLast  | O(1)       |
| search      | O(n)       |

---

# 5. ArrayList vs LinkedList Interview Question ⭐⭐⭐⭐⭐

Question:

> Which one is faster?

Wrong answer:

"ArrayList is faster."

Correct:

Depends on operation.

---

## Random access

Example:

```java
get(1000)
```

Winner:

```
ArrayList
```

Because:

```
O(1)
```

---

## Insert at beginning

Example:

```java
addFirst()
```

Winner:

```
LinkedList
```

Because:

```
O(1)
```

---

## Searching

Both:

```
O(n)
```

---

# 6. HashMap Complexity ⭐⭐⭐⭐⭐

Most important.

Internal:

Java 8:

```
HashMap

 |

 v

Node[]

 |

 v

Bucket

 |

 +----------------+

 | Linked List    |

 |       OR       |

 | Tree           |

 +----------------+

```

---

# 6.1 get()

Example:

```java
map.get("Java");
```

Flow:

```
get(key)

   |

   v

hashCode()

   |

   v

bucket index

   |

   v

find node

```

---

Average:

Good hash distribution:

```
one bucket lookup

```

Complexity:

```
O(1)
```

---

# 6.2 Worst Case Before Java 8

Bad collision:

```
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

```

Search:

Need traversal.

Complexity:

```
O(n)
```

---

# 6.3 Java 8 Improvement

When collision count:

```
>= 8
```

and capacity:

```
>=64
```

Treeification happens.

Before:

```
Linked List

A
|
B
|
C
|
D

```

After:

```
Red Black Tree


       B

     /   \

    A     C

```

Lookup:

```
O(log n)
```

---

## HashMap Final Interview Answer

Say:

> Average case get and put are O(1) with good hash distribution. Worst case was O(n) due to collisions. Since Java 8, heavily-collided buckets become Red-Black Trees, making worst-case lookup O(log n).

---

# 7. LinkedHashMap Complexity ⭐⭐⭐⭐

Internally:

```
HashMap

+

Doubly Linked List

```

Example:

```
Hash Table


Bucket


    |

    v


A <-> B <-> C

```

---

Operations:

| Operation | Complexity |
| --------- | ---------- |
| get       | O(1)       |
| put       | O(1)       |
| remove    | O(1)       |

Extra overhead:

Maintains ordering.

---

# 8. TreeMap Complexity ⭐⭐⭐⭐⭐

Internal:

```
Red Black Tree

```

Example:

```
          50

       /      \

     30        70

    /         

  20

```

---

Operations:

Search:

```
left or right decision

```

Complexity:

```
O(log n)
```

---

TreeMap:

| Operation | Complexity |
| --------- | ---------- |
| get       | O(log n)   |
| put       | O(log n)   |
| remove    | O(log n)   |

---

# 9. HashSet Complexity ⭐⭐⭐⭐⭐

Internally:

```
HashSet

    |

    v

HashMap

```

Actually:

```java
HashSet.add(value)
```

does:

```java
map.put(value,PRESENT)
```

---

Complexity:

| Operation | Complexity   |
| --------- | ------------ |
| add       | O(1) average |
| contains  | O(1) average |
| remove    | O(1) average |

Worst:

```
O(log n)
```

Java 8 treeification.

---

# 10. TreeSet Complexity

Internally:

```
TreeMap
```

Therefore:

| Operation | Complexity |
| --------- | ---------- |
| add       | O(log n)   |
| remove    | O(log n)   |
| contains  | O(log n)   |

---

# 11. PriorityQueue Complexity ⭐⭐⭐⭐

Internal:

Binary Heap.

Example:

```
          10

       /      \

      20       30

    /
   40

```

---

Operations:

## Insert

Need heapify:

```
O(log n)
```

---

## Remove minimum

Heapify down:

```
O(log n)
```

---

## Peek

Root element:

```
O(1)
```

---

Summary:

| Operation | Complexity |
| --------- | ---------- |
| offer     | O(log n)   |
| poll      | O(log n)   |
| peek      | O(1)       |

---

# 12. Deque / ArrayDeque Complexity ⭐⭐⭐⭐

Internal:

Resizable circular array.

Operations:

| Operation   | Complexity |
| ----------- | ---------- |
| addFirst    | O(1)       |
| addLast     | O(1)       |
| removeFirst | O(1)       |
| removeLast  | O(1)       |

---

# 13. ConcurrentHashMap Complexity ⭐⭐⭐⭐⭐

Same as HashMap.

Average:

```
O(1)
```

Worst:

```
O(log n)
```

Java 8 tree bins.

---

# 14. Complete Complexity Cheat Sheet ⭐⭐⭐⭐⭐

| Collection        | get/search | insert   | delete   |
| ----------------- | ---------- | -------- | -------- |
| ArrayList         | O(1)       | O(1)*    | O(n)     |
| LinkedList        | O(n)       | O(1)**   | O(1)**   |
| HashMap           | O(1) avg   | O(1) avg | O(1) avg |
| LinkedHashMap     | O(1)       | O(1)     | O(1)     |
| TreeMap           | O(log n)   | O(log n) | O(log n) |
| HashSet           | O(1) avg   | O(1) avg | O(1) avg |
| TreeSet           | O(log n)   | O(log n) | O(log n) |
| PriorityQueue     | O(1) peek  | O(log n) | O(log n) |
| ArrayDeque        | O(1)       | O(1)     | O(1)     |
| ConcurrentHashMap | O(1) avg   | O(1) avg | O(1) avg |

* amortized
  ** if node known

---

# 15. Interview Golden Rules ⭐⭐⭐⭐⭐

Never answer:

❌

> HashMap is O(1)

Always answer:

✅

> HashMap is O(1) average case. Worst case depends on collision handling. Java 8 introduced tree bins, so heavily-collided buckets provide O(log n) lookup.

---

Never answer:

❌

> ArrayList insertion is O(1)

Answer:

✅

> Adding at the end is O(1) amortized, but inserting/removing in the middle requires shifting elements and is O(n).

---

Never answer:

❌

> LinkedList insertion is always O(1)

Answer:

✅

> Insertion is O(1) only when the node position is already known. Finding the position requires O(n).

---

# Chapter Complete ✅

Covered:

✅ Big-O fundamentals
✅ ArrayList complexity
✅ LinkedList complexity
✅ HashMap average vs worst case
✅ Java 8 treeification
✅ LinkedHashMap
✅ TreeMap
✅ HashSet
✅ TreeSet
✅ PriorityQueue
✅ ArrayDeque
✅ ConcurrentHashMap
✅ Interview complexity answers

---

Next Chapter:

# Chapter 33 — Collections Interview Problem Patterns ⭐⭐⭐⭐⭐

We will cover practical coding questions:

* Why HashMap key must be immutable
* Implement LRU Cache using LinkedHashMap
* Frequency counter problems
* Top K frequent elements using Heap
* Group Anagrams
* Custom sorting with TreeMap
* Designing cache using collections
* When to choose which collection in system design

This converts theory into interview-ready problem solving.


# Chapter 33 — Collections Interview Problem Patterns ⭐⭐⭐⭐⭐

Now we move from **collection internals → practical interview problem solving**.

For senior Java interviews, interviewers rarely ask only:

> "Explain HashMap."

They usually test:

* Can you choose the right collection?
* Do you understand complexity?
* Can you identify the pattern?
* Can you design using collections?

This chapter covers the most common patterns.

---

# 1. Pattern: Immutable Keys in HashMap ⭐⭐⭐⭐⭐

Very common interview question.

Question:

> Why should HashMap keys be immutable?

---

## First understand HashMap working

Example:

```java
Map<User,String> map = new HashMap<>();

User user = new User(101);

map.put(user,"Java");
```

Internally:

```text
              HashMap


                |

                v


            hashCode()


                |

                v


          Bucket Index


                |

                v


        Store key + value

```

---

Suppose:

```java
User user = new User(101);
```

hashCode:

```
101
```

Bucket:

```
5
```

Storage:

```text
Bucket 5

User(101) ---> Java

```

---

Now after insertion:

```java
user.setId(200);
```

Hash code changes.

Now:

```
hashCode = 200
```

New bucket:

```
10
```

---

But object is still physically stored in:

```
Bucket 5
```

---

Now:

```java
map.get(user);
```

Flow:

```text
get(user)

   |
   v

calculate hashCode()

   |
   v

go to bucket 10

   |
   v

search

   |
   v

NOT FOUND

```

---

Data exists but cannot be retrieved.

---

## Wrong Example

```java
class Employee {

    int id;

    Employee(int id){
        this.id=id;
    }


    public int hashCode(){
        return id;
    }


    public boolean equals(Object obj){
        return id == ((Employee)obj).id;
    }


    public void setId(int id){
        this.id=id;
    }
}

```

Problem:

`id` can change.

---

# Correct Design

Make key immutable.

```java
final class Employee {

    private final int id;


    Employee(int id){
        this.id=id;
    }


    public int getId(){
        return id;
    }


    @Override
    public int hashCode(){
        return id;
    }


    @Override
    public boolean equals(Object obj){

        Employee e = (Employee)obj;

        return this.id == e.id;
    }

}

```

---

# Interview Answer

> HashMap uses hashCode() to locate the bucket. If a key's state changes after insertion, its hashCode may change and HashMap will search in a different bucket, making the entry unreachable. Therefore keys should be immutable.

---

# 2. Frequency Counting Pattern ⭐⭐⭐⭐⭐

Very common coding pattern.

Question:

Count frequency of elements.

Example:

Input:

```
[1,2,2,3,3,3]
```

Output:

```
1 -> 1
2 -> 2
3 -> 3
```

---

## Brute Force

For every element:

Count again.

Complexity:

```
O(n²)
```

---

## HashMap Approach

Idea:

Store:

```
element -> count
```

---

Flow:

```text
Array


[1,2,2,3,3,3]


        |

        v


HashMap


1 -> 1

2 -> 2

3 -> 3

```

---

Code:

```java
public class FrequencyCounter {

    public static Map<Integer,Integer> count(int[] nums){

        Map<Integer,Integer> frequency =
                new HashMap<>();


        for(int num: nums){

            frequency.put(
                num,
                frequency.getOrDefault(num,0)+1
            );

        }


        return frequency;
    }
}

```

---

Complexity:

Time:

```
O(n)
```

Space:

```
O(n)
```

---

# 3. Group Anagrams ⭐⭐⭐⭐⭐

Very popular.

Problem:

Input:

```
["eat","tea","tan","ate","nat","bat"]
```

Output:

```
[
 [eat,tea,ate],
 [tan,nat],
 [bat]
]
```

---

## Observation

Anagrams have same characters.

Example:

```
eat

aet


tea

aet

```

Same key.

---

Pattern:

```
Sorted String -> List of words
```

---

Flow:

```text
eat


sort


aet


        |

        v


HashMap


aet -> [eat,tea,ate]

```

---

Code:

```java
public List<List<String>> groupAnagrams(String[] words){


    Map<String,List<String>> map =
            new HashMap<>();


    for(String word: words){


        char[] chars =
                word.toCharArray();


        Arrays.sort(chars);


        String key =
                new String(chars);


        map.computeIfAbsent(
                key,
                k -> new ArrayList<>()
        )
        .add(word);

    }


    return new ArrayList<>(map.values());

}

```

---

Complexity:

Let:

```
n = number of words
k = average word length
```

Sorting:

```
O(k log k)
```

Total:

```
O(n*k log k)
```

---

# 4. Top K Frequent Elements ⭐⭐⭐⭐⭐

Very common senior interview problem.

Example:

Input:

```
[1,1,1,2,2,3]

k=2
```

Output:

```
[1,2]
```

---

## Step 1

Count frequency.

HashMap:

```
1 -> 3
2 -> 2
3 -> 1

```

---

## Step 2

Need top K.

Options:

### Sorting

Sort all frequencies.

Complexity:

```
O(n log n)
```

---

Better:

Use Heap.

---

Min Heap:

Keep only K elements.

Flow:

```text
Frequency Map


      |

      v


Min Heap


      |

      v


If size > K

remove smallest

```

---

Example:

k=2

Heap:

```
(1,1)

(2,2)

```

Add:

```
(3,1)

```

Size becomes 3.

Remove smallest.

---

Code:

```java
public int[] topKFrequent(int[] nums,int k){


    Map<Integer,Integer> freq =
            new HashMap<>();


    for(int n:nums){

        freq.put(
            n,
            freq.getOrDefault(n,0)+1
        );

    }


    PriorityQueue<int[]> heap =
        new PriorityQueue<>(
            (a,b)->a[1]-b[1]
        );


    for(Map.Entry<Integer,Integer> entry:
            freq.entrySet()){


        heap.offer(
            new int[]{
                entry.getKey(),
                entry.getValue()
            }
        );


        if(heap.size()>k){
            heap.poll();
        }

    }


    int[] result=new int[k];


    int i=0;

    while(!heap.isEmpty()){

        result[i++]=heap.poll()[0];

    }


    return result;
}

```

---

Complexity:

Frequency:

```
O(n)
```

Heap:

```
O(m log k)
```

where:

```
m = unique elements
```

Total:

```
O(n log k)
```

---

# 5. LRU Cache Design ⭐⭐⭐⭐⭐

One of the most asked LLD + Collections questions.

Question:

> Design an LRU cache.

Requirement:

* get()
* put()
* Remove least recently used item

---

Need:

Fast lookup:

```
O(1)
```

Need ordering:

```
recent -> old
```

---

Combination:

```text
HashMap

+

Doubly LinkedList

```

---

Architecture:

```text

          HashMap


key
 |
 v

Node


         

Node structure


prev

key

value

next


```

---

Example:

Capacity = 3

Access:

```
A B C
```

Linked list:

```text
C <-> B <-> A

Newest          Oldest

```

Add D:

Remove A.

---

Java provides:

```java
LinkedHashMap
```

which internally does this.

---

Implementation:

```java
class LRUCache<K,V>
        extends LinkedHashMap<K,V>{


    private final int capacity;


    LRUCache(int capacity){

        super(
            capacity,
            0.75f,
            true
        );

        this.capacity=capacity;

    }


    protected boolean removeEldestEntry(
            Map.Entry<K,V> entry){

        return size()>capacity;

    }

}

```

---

Important constructor:

```java
accessOrder=true
```

means:

recently accessed items move to end.

---

# 6. When To Use Which Collection? ⭐⭐⭐⭐⭐

Interview scenario:

> Design a cache.

Answer:

```
HashMap + LinkedHashMap
```

---

Scenario:

> Need sorted keys.

Answer:

```
TreeMap
```

---

Scenario:

> Need unique values.

Answer:

```
HashSet
```

---

Scenario:

> Need insertion order.

Answer:

```
LinkedHashSet
LinkedHashMap

```

---

Scenario:

> Need priority processing.

Answer:

```
PriorityQueue
```

---

Scenario:

> Producer consumer.

Answer:

```
BlockingQueue

```

---

Scenario:

> Thread-safe key value store.

Answer:

```
ConcurrentHashMap

```

---

# 7. Collection Selection Decision Tree ⭐⭐⭐⭐⭐

```text
                 Need Collection?


                      |

                      v


             Need key-value?


             /             \


           Yes              No


            |                |

            v                v


      Need ordering?       Need unique?


       /      \             /      \


     Yes      No          Yes       No


      |        |            |         |


 LinkedHash  HashMap     HashSet   List


 TreeMap


```

---

# 8. Common Interview Traps ⭐⭐⭐⭐⭐

## Trap 1

Question:

Why not use HashMap everywhere?

Answer:

Because HashMap does not provide:

* Thread safety
* Ordering
* Sorting
* Duplicate handling

---

## Trap 2

Question:

ArrayList vs LinkedList?

Wrong:

"LinkedList is faster for insertion."

Correct:

> LinkedList is faster only when the insertion position/node is already known. Finding the position is O(n).

---

## Trap 3

Question:

HashMap complexity?

Wrong:

"O(1)"

Correct:

> Average O(1), worst case O(log n) in Java 8+ because collision buckets can become Red-Black Trees.

---

## Trap 4

Question:

Why LinkedHashMap?

Answer:

> It combines HashMap lookup performance with a doubly linked list to maintain insertion/access order.

---

# Chapter Complete ✅

Covered:

✅ Immutable HashMap keys
✅ Frequency counting pattern
✅ Group Anagrams
✅ Top K Frequent Elements
✅ Heap + HashMap combination
✅ LRU Cache design
✅ Collection selection strategy
✅ Interview traps
✅ Complexity discussion

---

Next Chapter:

# Chapter 34 — HashMap Complete Interview Revision ⭐⭐⭐⭐⭐

We will consolidate the biggest topic:

* HashMap internal memory layout
* hash() function
* bucket calculation
* equals/hashCode contract
* collision handling
* resizing
* load factor
* treeification
* Java 7 vs Java 8 differences
* HashMap interview questions (30+)

This will be the final HashMap mastery chapter.

# Chapter 34 — HashMap Complete Interview Revision ⭐⭐⭐⭐⭐

This is the **most important chapter in the entire Collections Framework** for senior Java interviews.

For a **7+ years Java developer**, HashMap questions are almost guaranteed.

Interviewers do not expect only:

> "HashMap stores key-value pairs."

They expect you to explain:

* Internal memory structure
* Hashing mechanism
* Bucket calculation
* Collision handling
* equals() and hashCode()
* Resizing
* Load factor
* Java 7 vs Java 8 differences
* Treeification
* Complexity analysis
* Real production considerations

---

# 1. What Is HashMap?

Definition:

> HashMap is a data structure that stores data in key-value pairs and provides average O(1) time complexity for insertion and retrieval using hashing.

Example:

```java
Map<Integer,String> map = new HashMap<>();

map.put(101,"Java");
map.put(102,"Spring");

System.out.println(map.get(101));
```

Output:

```
Java
```

---

# 2. Why HashMap Exists?

Suppose we store data in ArrayList:

```text
Index

0 -> Employee A
1 -> Employee B
2 -> Employee C

```

Searching:

Need to scan every element.

Complexity:

```
O(n)
```

---

HashMap idea:

Instead of searching:

```
Find data location directly
```

using:

```
key -> hash -> bucket
```

---

Flow:

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


          Value


```

---

# 3. HashMap Internal Structure ⭐⭐⭐⭐⭐

Java 8 HashMap:

```
HashMap


   |

   v


Node[] table


   |

   v


Buckets


   |

   +----------------+

   |                |

 LinkedList     Red Black Tree


```

---

Internal array:

```java
transient Node<K,V>[] table;
```

---

Node structure:

```java
static class Node<K,V>{

    final int hash;

    final K key;

    V value;

    Node<K,V> next;

}
```

---

Visual:

```
Bucket 0

null


Bucket 1

        +-------+
        | key   |
        | value |
        | next |
        +-------+
              |
              v
        +-------+
        | key   |
        | value |
        +-------+


Bucket 2

null

```

---

# 4. HashMap put() Internal Flow ⭐⭐⭐⭐⭐

Example:

```java
map.put("Java",100);
```

---

Complete flow:

```
put(key,value)


        |

        v


Calculate hash


        |

        v


Find bucket index


        |

        v


Check bucket empty?


        |

   ----------------

   |              |

 Yes              No


   |              |


Create Node     Compare keys


                    |

                    v


              Same key?


              /      \


            Yes       No


             |         |


        Replace      Add node


```

---

# 5. Hash Function Internal ⭐⭐⭐⭐⭐

Interview favourite:

> How does HashMap calculate bucket?

---

Java 8:

```java
static final int hash(Object key){

    int h;

    return (key == null) ? 0 :
        (h = key.hashCode()) ^ 
        (h >>> 16);
}
```

---

Why spread bits?

Because:

Original hashCode:

```
1111 0000 1010 1111
```

Only lower bits are used for bucket calculation.

Problem:

Poor distribution.

---

Hash spreading:

```
Original hash


1111000010101111


        XOR


0000000011110000



Result


1111000001011111

```

More randomness.

---

# 6. Bucket Index Calculation ⭐⭐⭐⭐⭐

Important interview question.

Formula:

```java
index = (n - 1) & hash
```

where:

```
n = table length
```

---

Example:

Capacity:

```
16
```

Binary:

```
16 = 10000
```

n-1:

```
15 = 01111
```

Hash:

```
10110101
```

AND:

```
10110101

&
00001111

----------

00000101

```

Bucket:

```
5
```

---

Why not modulo?

Instead of:

```
hash % n
```

Java uses:

```
(hash & (n-1))
```

because bit operation is faster.

---

# 7. Why Capacity Is Power of Two? ⭐⭐⭐⭐⭐

HashMap default:

```
16
```

Why?

Because:

```
n-1 & hash
```

works efficiently only when n is power of 2.

Examples:

```
16
32
64
128

```

---

If capacity was:

```
10
```

distribution becomes uneven.

---

# 8. Collision Handling ⭐⭐⭐⭐⭐

Collision means:

Two keys produce same bucket.

Example:

```
Key A

hash -> bucket 5


Key B

hash -> bucket 5

```

---

Before Java 8:

```
Bucket 5


A

|

B

|

C

```

Linked list.

---

Java 8:

```
Bucket 5


        Tree


          B

       /     \

      A       C


```

---

# 9. equals() and hashCode() Contract ⭐⭐⭐⭐⭐

Most important.

HashMap uses:

First:

```
hashCode()
```

Then:

```
equals()
```

---

Example:

```java
map.put(employee,"value");
```

Later:

```java
map.get(employee);
```

HashMap:

Step 1:

Find bucket using:

```java
hashCode()
```

Step 2:

Compare keys:

```java
equals()
```

---

Contract:

If:

```java
a.equals(b)==true
```

then:

must have:

```java
a.hashCode()==b.hashCode()
```

---

But:

Same hashCode does NOT mean equal.

Example:

```
hashCode collision


A -> 100

B -> 100

```

Need:

```
equals()
```

to distinguish.

---

# 10. Bad hashCode Example

Wrong:

```java
public int hashCode(){

    return 1;

}
```

Every object goes to same bucket.

Result:

```
Bucket 1


A

|

B

|

C

|

D

```

Complexity:

```
O(n)
```

---

Good hashCode:

Distributes objects evenly.

---

# 11. Resizing in HashMap ⭐⭐⭐⭐⭐

Important.

Default:

```
capacity = 16
```

Load factor:

```
0.75
```

Threshold:

```
capacity * loadFactor
```

---

Calculation:

```
16 * 0.75

=12

```

Meaning:

After 12 entries:

Resize.

---

Resize:

Old:

```
16 buckets

```

New:

```
32 buckets

```

---

Flow:

```
Old Table


Bucket 1
Bucket 2
Bucket 3


        |

        v


Create new table


        |

        v


Recalculate positions


        |

        v


Move nodes


```

---

# 12. Why Load Factor 0.75? ⭐⭐⭐⭐⭐

Trade-off between:

Memory

and

Performance.

---

Low load factor:

Example:

```
0.5

```

Advantages:

* Less collision

Disadvantages:

* More memory

---

High load factor:

Example:

```
0.9

```

Advantages:

* Less memory

Disadvantages:

* More collisions

---

0.75 gives good balance.

---

# 13. Java 7 vs Java 8 HashMap ⭐⭐⭐⭐⭐

## Java 7

Collision:

```
LinkedList
```

Worst case:

```
O(n)
```

---

Resize:

Problem:

During concurrent resize:

Possible infinite loop.

---

Why?

Head insertion:

```
A -> B -> C


Resize


C -> B -> A

```

Different threads can create cycle.

---

## Java 8

Collision:

```
LinkedList

        |

        v

Red Black Tree

```

Worst case:

```
O(log n)
```

---

Resize:

Uses tail insertion.

No infinite loop problem.

---

# 14. Treeification Rules ⭐⭐⭐⭐⭐

Constants:

```java
TREEIFY_THRESHOLD = 8
```

When bucket size:

```
>=8
```

convert to tree.

---

But:

Only if capacity:

```
>=64
```

---

Otherwise:

Resize first.

---

Why?

Small tables benefit more from resizing than tree creation.

---

# 15. HashMap Complexity ⭐⭐⭐⭐⭐

## Average Case

Good hashing:

```
put()

O(1)


get()

O(1)

```

---

## Worst Case

Before Java 8:

```
O(n)
```

because linked list.

---

Java 8:

```
O(log n)
```

because Red Black Tree.

---

Senior answer:

> HashMap provides O(1) average complexity. Worst case is O(n) due to collisions, but Java 8 improves this to O(log n) by converting heavily-collided buckets into Red-Black Trees.

---

# 16. Why HashMap Is Not Thread Safe? ⭐⭐⭐⭐⭐

Example:

Two threads:

```
Thread A

put(A)


Thread B

put(B)

```

Both modify internal table.

Possible issues:

* Lost updates
* Data inconsistency
* Resize corruption

---

For concurrent access:

Use:

```
ConcurrentHashMap
```

---

# 17. Why HashMap Allows Null Key? ⭐⭐⭐⭐

HashMap:

Allows:

```java
map.put(null,"Java");
```

Why?

HashMap handles null specially:

```java
hash = 0
```

Stored in:

```
Bucket 0
```

---

# 18. Why ConcurrentHashMap Does Not Allow Null? ⭐⭐⭐⭐⭐

Because:

```
null
```

means:

Either:

```
key absent
```

or:

```
key exists with null value

```

Ambiguous in concurrent environment.

---

# 19. HashMap Memory Layout

Example:

```java
Map<Integer,String> map = new HashMap<>();

map.put(10,"Java");

```

Memory:

```
Stack


map reference

      |
      |
      v


Heap


HashMap Object


table[]


bucket[10]


     |

     v


Node


hash = 10

key = 10

value = Java

next = null


```

---

# 20. Important Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why HashMap lookup is O(1)?

Answer:

Because hashCode directly identifies the bucket, avoiding full traversal.

---

## Q2. Why worst case is O(log n)?

Answer:

Because Java 8 converts heavily-collided buckets into Red-Black Trees.

---

## Q3. What happens if two keys have same hashCode?

Answer:

They go into the same bucket. HashMap uses equals() to distinguish them.

---

## Q4. Can HashMap have duplicate keys?

Answer:

No.

A new value replaces the existing value when equals() returns true.

---

## Q5. Can mutable objects be HashMap keys?

Technically yes, but dangerous.

Because changing fields affecting hashCode/equals can make the key unreachable.

---

## Q6. Why does HashMap capacity double?

Because power-of-two capacity allows efficient bucket calculation using bit masking.

---

# Final HashMap Mental Model

```
                 HashMap


                    |

                    v


              hashCode()


                    |

                    v


             hash spreading


                    |

                    v


          bucket index calculation


                    |

                    v


              Node[] table


                    |

        -----------------------


        Empty bucket

              |

              v

             CAS


        Collision

              |

              v

       LinkedList


              |

              v

       Red Black Tree


              |

              v

          O(log n)


```

---

# Chapter Complete ✅

Covered:

✅ HashMap internal structure
✅ Node design
✅ put() flow
✅ get() flow
✅ hash spreading
✅ bucket calculation
✅ equals/hashCode contract
✅ collision handling
✅ resizing
✅ load factor
✅ treeification
✅ Java 7 vs Java 8
✅ complexity discussion
✅ senior interview answers

---

Next Chapter:

# Chapter 35 — LinkedHashMap Deep Dive ⭐⭐⭐⭐

Topics:

* Internal architecture
* HashMap + Doubly LinkedList
* Insertion order vs Access order
* LRU Cache implementation
* removeEldestEntry()
* How Spring caching uses similar concepts
* Interview questions and design scenarios
