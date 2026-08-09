# Module 3a — Collections: List, Set, Queue Deep Dive — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---
Perfect. Since this is a **recap**, let's do **one chapter for the entire Collections Framework** and only slow down on **HashMap, ConcurrentHashMap, ArrayList and CopyOnWriteArrayList** later.

---

# Collections Framework (Quick Interview Recap) ⭐⭐⭐⭐⭐

## Why was Collections Framework introduced?

Before Java Collections, Java had classes like:

* Vector
* Hashtable
* Arrays

Each had different APIs.

Collections Framework introduced

* Common interfaces
* Reusable algorithms
* Standard implementations

---

# Complete Hierarchy ⭐⭐⭐⭐⭐

```text
                           Iterable
                               │
                           Collection
          ┌────────────────────┼────────────────────┐
          │                    │                    │
          ▼                    ▼                    ▼
         List                 Set                Queue
          │                    │                    │
 ┌────────┼─────────┐     ┌────┼──────┐      ┌──────┼──────────────┐
 ▼        ▼         ▼     ▼    ▼      ▼      ▼      ▼              ▼
ArrayList LinkedList Vector HashSet LinkedHashSet TreeSet PriorityQueue ArrayDeque BlockingQueue
          │
          ▼
        Stack (Legacy)

-----------------------------------------

                     Map (Separate Hierarchy)
                           │
        ┌──────────────┬─────────────┬──────────────┐
        ▼              ▼             ▼              ▼
     HashMap     LinkedHashMap    TreeMap    ConcurrentHashMap
        │
        ├── Hashtable
        ├── WeakHashMap
        ├── IdentityHashMap
        └── EnumMap
```

> **Remember:** `Map` is **not** part of the `Collection` interface hierarchy.

---

# List ⭐⭐⭐⭐⭐

## Characteristics

✔ Ordered

✔ Allows duplicates

✔ Index-based access

Example

```java
[10, 20, 20, 30]
```

Use when order matters.

---

## ArrayList ⭐⭐⭐⭐⭐

### Internal Structure

```text
Array

+----+----+----+----+
| 10 | 20 | 30 | 40 |
+----+----+----+----+
```

### Pros

* Fast random access O(1)
* Cache friendly

### Cons

* Insert/delete in middle O(n)

### Use When

Mostly reading data.

---

## LinkedList ⭐⭐⭐⭐⭐

```text
+----+    +----+    +----+
| 10 |--->| 20 |--->| 30 |
+----+    +----+    +----+
```

Pros

* Fast insertion/deletion (if node reference is known)

Cons

* Slow random access O(n)
* Extra memory for pointers

---

## Vector ⭐⭐⭐

* Legacy class
* Synchronized
* Slower than ArrayList

Mostly replaced by:

* ArrayList
* CopyOnWriteArrayList

---

## Stack ⭐⭐⭐

Legacy implementation.

Preferred today

```java
Deque<Integer> stack = new ArrayDeque<>();
```

---

## CopyOnWriteArrayList ⭐⭐⭐⭐⭐

Every write creates a **new copy**.

```text
Read Threads

↓

Original Array

Write

↓

New Array Created
```

Best for

* Many reads
* Very few writes

Example

* Event listeners
* Configuration cache

---

# Set ⭐⭐⭐⭐⭐

Characteristics

✔ No duplicates

---

## HashSet ⭐⭐⭐⭐⭐

Internally uses

```text
HashMap
```

Order

❌ Not guaranteed

Complexity

O(1)

---

## LinkedHashSet ⭐⭐⭐⭐

Maintains insertion order.

Internally

```text
HashMap

+

Linked List
```

---

## TreeSet ⭐⭐⭐⭐

Stores elements in sorted order.

Internally

```text
Red Black Tree
```

Complexity

O(log n)

---

## EnumSet ⭐⭐

Optimized only for Enum values.

Very memory efficient.

---

# Queue ⭐⭐⭐⭐⭐

FIFO structure.

---

## PriorityQueue ⭐⭐⭐⭐

Elements ordered by priority.

Internally

```text
Binary Heap
```

Peek

```text
Smallest element
```

---

## ArrayDeque ⭐⭐⭐⭐⭐

Double-ended queue.

Can be used as

* Queue
* Stack

Preferred over Stack.

---

## BlockingQueue ⭐⭐⭐⭐⭐

Thread-safe queue.

Producer waits.

Consumer waits.

Used in

* ThreadPoolExecutor
* Producer Consumer pattern

---

## ConcurrentLinkedQueue ⭐⭐⭐⭐

Non-blocking.

Lock-free.

Used in high concurrency.

---

## DelayQueue ⭐⭐⭐

Stores delayed tasks.

Example

Cache expiration.

---

# Map ⭐⭐⭐⭐⭐

Stores

```text
Key → Value
```

Keys unique.

---

## HashMap ⭐⭐⭐⭐⭐

Most important.

Internally

```text
Array

↓

Bucket

↓

Linked List

↓

Red Black Tree
```

Average

O(1)

We'll study separately.

---

## LinkedHashMap ⭐⭐⭐⭐

Maintains insertion/access order.

Used for

```text
LRU Cache
```

---

## TreeMap ⭐⭐⭐⭐

Sorted map.

Internally

```text
Red Black Tree
```

Complexity

O(log n)

---

## Hashtable ⭐⭐⭐

Legacy synchronized map.

Mostly replaced by

ConcurrentHashMap.

---

## WeakHashMap ⭐⭐⭐⭐

Keys stored as Weak References.

Unused entries removed automatically by GC.

Useful

Caches.

---

## IdentityHashMap ⭐⭐⭐

Uses

```text
==
```

instead of

```text
equals()
```

Rarely used.

---

## EnumMap ⭐⭐

Optimized for Enum keys.

Very fast.

---

## ConcurrentHashMap ⭐⭐⭐⭐⭐

Thread-safe HashMap.

High concurrency.

No global lock.

Very common interview topic.

We'll cover deeply later.

---

# Iterator Framework ⭐⭐⭐⭐⭐

## Iterator

Forward traversal.

```java
hasNext()

next()

remove()
```

---

## ListIterator

Bidirectional.

```text
Forward

Backward

Add

Set
```

Only for List.

---

## Fail-Fast ⭐⭐⭐⭐⭐

Detects concurrent modification.

Throws

```java
ConcurrentModificationException
```

Examples

* ArrayList
* HashMap

---

## Fail-Safe ⭐⭐⭐⭐⭐

Works on a snapshot.

No exception.

Example

```java
CopyOnWriteArrayList
```

---

## Spliterator ⭐⭐⭐⭐

Introduced in Java 8.

Supports

* Parallel Streams
* Efficient splitting

---

# Complexity Cheat Sheet ⭐⭐⭐⭐⭐

| Collection        | Search     | Insert   | Delete   | Ordered | Sorted     | Thread Safe |
| ----------------- | ---------- | -------- | -------- | ------- | ---------- | ----------- |
| ArrayList         | O(1) index | O(1)*    | O(n)     | Yes     | No         | No          |
| LinkedList        | O(n)       | O(1)**   | O(1)**   | Yes     | No         | No          |
| HashSet           | O(1)       | O(1)     | O(1)     | No      | No         | No          |
| TreeSet           | O(log n)   | O(log n) | O(log n) | No      | Yes        | No          |
| HashMap           | O(1)       | O(1)     | O(1)     | No      | No         | No          |
| TreeMap           | O(log n)   | O(log n) | O(log n) | No      | Yes (keys) | No          |
| ConcurrentHashMap | O(1) avg   | O(1) avg | O(1) avg | No      | No         | Yes         |

* Amortized at the end of the list.
  ** When the node/position is already known.

---

# Frequently Asked (7+ Years)

### Very Frequently

* ArrayList vs LinkedList
* HashMap internal working
* ConcurrentHashMap internals
* HashMap vs Hashtable
* HashMap vs TreeMap
* HashSet internally uses what?
* Why is LinkedHashMap used for LRU Cache?
* CopyOnWriteArrayList use cases
* Fail-fast vs Fail-safe
* Iterator vs ListIterator
* ArrayDeque vs Stack

---

## ⭐ Priority for Deep Dive

```text
Collections
      │
      ├── ArrayList ⭐⭐⭐⭐⭐
      ├── LinkedList ⭐⭐⭐⭐⭐
      ├── HashMap ⭐⭐⭐⭐⭐⭐⭐⭐⭐ (Most Important)
      ├── ConcurrentHashMap ⭐⭐⭐⭐⭐⭐⭐
      ├── CopyOnWriteArrayList ⭐⭐⭐⭐⭐
      └── Iterator / Fail-Fast ⭐⭐⭐⭐⭐
```

These six topics account for the majority of Java Collections questions asked in senior backend interviews. Everything else is usually covered through comparison or follow-up questions.


## Q1. Compare ArrayList vs LinkedList — when to use which?

### 1. Why This Concept Matters
Choosing the wrong List implementation causes production performance issues. ArrayList for random access, LinkedList for frequent front insertion — common interview question testing Big-O understanding and memory awareness.

### 2. Key Comparison

| Operation | ArrayList | LinkedList |
|-----------|-----------|------------|
| `get(i)` | **O(1)** — array index | **O(n)** — traverse from head/tail |
| `add(e)` at end | **O(1)** amortized | **O(1)** |
| `add(i, e)` | **O(n)** — shift elements | **O(n)** — find position |
| `addFirst(e)` | **O(n)** — shift all | **O(1)** |
| `remove(i)` | **O(n)** — shift | **O(n)** — find + unlink |
| `removeFirst()` | **O(n)** | **O(1)** |
| Memory per entry | ~4 bytes (reference) | ~24+ bytes (prev+next+data) |

### 3. Memory Comparison

```
ArrayList (capacity=10, size=5):
┌────┬────┬────┬────┬────┬────┬────┬────┬────┬────┐
│ A  │ B  │ C  │ D  │ E  │    │    │    │    │    │
└────┴────┴────┴────┴────┴────┴────┴────┴────┴────┘
  Memory: 10 references = 40 bytes (compressed OOPs)

LinkedList (5 elements):
┌───┐  ┌───┐  ┌───┐  ┌───┐  ┌───┐
│ A │→│ B │→│ C │→│ D │→│ E │
│● ●│  │● ●│  │● ●│  │● ●│  │● ●│
└───┘  └───┘  └───┘  └───┘  └───┘
  Memory per node: 3 refs + overhead ≈ 24-32 bytes
  Total: 5 × 28 = 140 bytes vs ArrayList 40 bytes = 3.5x more!
```

### 4. HashSet vs TreeSet vs LinkedHashSet

| Feature | HashSet | TreeSet | LinkedHashSet |
|---------|---------|---------|--------------|
| Ordering | None | Sorted (natural/comparator) | Insertion order |
| Internal | HashMap (array + list/tree) | Red-Black Tree | LinkedHashMap |
| Add/Remove/Contains | O(1) avg, O(log n) worst | **O(log n)** | O(1) avg |
| Iterator order | Unpredictable | Ascending | Insertion order |
| Null elements | One null | ❌ No (NPE) | One null |

### 5. PriorityQueue vs ArrayDeque

```java
// PriorityQueue: Binary heap — sorted by priority
Queue<Task> tasks = new PriorityQueue<>(Comparator.comparing(Task::getPriority));
tasks.offer(new Task("Critical", 1));  // O(log n) — sift up
tasks.offer(new Task("Low", 10));      // O(log n)
Task first = tasks.poll();             // O(log n) — removes root (highest priority)

// ArrayDeque: Circular array — fast at both ends
Deque<String> deque = new ArrayDeque<>();
deque.addFirst("A");     // O(1) — head = (head-1) & (len-1)
deque.addLast("B");      // O(1) — tail = (tail+1) & (len-1)
deque.removeFirst();     // O(1)
deque.removeLast();      // O(1)
```

### 6. Interview Questions

#### Q: When to use ArrayList vs LinkedList?
**A**: ArrayList for 95% of cases. LinkedList only for: (1) Frequent insert/delete at FRONT (deque — but use ArrayDeque instead); (2) Implementing LRU cache with removeEldestEntry (LinkedHashMap, not LinkedList). LinkedList uses ~3x more memory and has O(n) get. Even for front insertion, ArrayDeque is faster and more memory-efficient.

#### Q: How does HashSet ensure uniqueness?
**A**: HashSet backs by HashMap. add(e) calls map.put(e, PRESENT). HashMap checks if key exists via hashCode() → bucket → equals() check. If equals() returns true, old value replaced — no duplicate added. That's why proper equals/hashCode is CRITICAL for Set elements.

#### Q: When to use TreeSet over HashSet?
**A**: TreeSet when you need sorted iteration or range operations (subSet, headSet, tailSet). HashSet for general use (O(1) vs O(log n)). TreeSet also requires Comparable or Comparator — elements must implement Comparable or pass Comparator. Trade-off: HashSet O(1) but unordered; TreeSet O(log n) but sorted.

**Final 30-Second**: ArrayList for lists (O(1) get), ArrayDeque for stacks/queues (O(1) both ends), HashMap/HashSet for key-value/unique lookup (O(1)), TreeSet/TreeMap for sorted (O(log n)), LinkedHashMap for insertion order. Never use Vector, Stack, Hashtable (legacy).