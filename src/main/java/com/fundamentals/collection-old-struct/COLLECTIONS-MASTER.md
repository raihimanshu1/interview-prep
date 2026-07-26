# 📚 Java Collections — COMPLETE MASTER GUIDE

> ⚠️ **Warning**: This is NOT a surface-level tutorial. Every time complexity is explained with EXACT conditions. If an interviewer asks you about HashMap time complexity, you will give them the FULL answer — not "O(1)".

---

## 🎯 How to Use This File

This is your **single source of truth**. Read in order. Each topic builds on the previous.

```
Part 1: Foundation  → Hierarchy, Interfaces, Iterator pattern
Part 2: Lists       → ArrayList, LinkedList
Part 3: Maps        → HashMap (MOST IMPORTANT), LinkedHashMap
Part 4: Sets        → HashSet, TreeSet
Part 5: Queues      → PriorityQueue, ArrayDeque
Part 6: Concurrent  → ConcurrentHashMap, BlockingQueue, CopyOnWriteArrayList
Part 7: Special     → WeakHashMap, EnumMap, IdentityHashMap
Part 8: Legacy      → Stack, Vector, Hashtable
Part 9: Reference   → Full time complexity table, 7+ year interview questions
```

---

# PART 1: FOUNDATION

## 1.1 The Story: Why Collections Exist

Before Java Collections, every project wrote the same data structures from scratch:

```java
// Every team wrote their own "resizable array"
class MyArrayList {
    String[] data = new String[10];
    int size = 0;
    void add(String s) { data[size++] = s; }
    String get(int i) { return data[i]; }
}
```

**Problem**: 1000 projects × 100 developers each = 100,000 implementations of the same thing. All buggy, all slightly different.

**Java's solution**: Standard interfaces + implementations. One ArrayList that everyone uses.

```
Interface = CONTRACT (what methods must exist)
Abstract  = REUSABLE CODE (partial implementation)
Class     = ACTUAL IMPLEMENTATION (how it works)
```

---

## 1.2 The Hierarchy (Complete)

```
Iterable (interface)          ← "Can be used in foreach loop"
  │
  ├── Collection (interface)  ← "A group of elements"
  │     │
  │     ├── List (interface)       ← "Ordered, duplicates allowed, indexed"
  │     │     ├── ArrayList
  │     │     ├── LinkedList       ← Also implements Deque
  │     │     └── Vector (legacy)
  │     │           └── Stack (legacy)
  │     │
  │     ├── Set (interface)        ← "No duplicates"
  │     │     ├── HashSet          ← Backed by HashMap
  │     │     ├── LinkedHashSet    ← Insertion order
  │     │     ├── TreeSet          ← Sorted, backed by TreeMap
  │     │     └── EnumSet          ← Bit vector for enums
  │     │
  │     └── Queue (interface)      ← "FIFO typically"
  │           ├── PriorityQueue    ← Binary heap, priority order
  │           └── Deque (interface) ← "Double ended"
  │                 ├── ArrayDeque
  │                 └── LinkedList
  │
  └── Map (interface)         ← SEPARATE! NOT a Collection!
        ├── HashMap           ← Hash table, O(1) average
        ├── LinkedHashMap     ← HashMap + insertion/access order
        ├── TreeMap           ← Red-Black tree, O(log n)
        ├── ConcurrentHashMap ← Thread-safe hash table
        ├── WeakHashMap       ← Keys can be GC'd
        ├── IdentityHashMap   ← Uses == not equals()
        ├── EnumMap           ← Keys are enums
        └── Hashtable (legacy)
```

**CRITICAL**: `Map` does NOT extend `Collection`. It's a separate hierarchy. This means:

```java
Collection<String> c = new HashMap<>();  // ❌ COMPILE ERROR!
Map<String, Integer> m = new HashMap<>();  // ✅ Correct
```

---

## 1.3 The 3-Layer Architecture

```
Example: ArrayList

Layer 1: Iterable (interface)     ← "You must have iterator()"
Layer 2: Collection (interface)   ← "You must have add(), remove(), size()..."  
Layer 3: AbstractList (abstract)  ← "I'll implement iterator(), subList() for you"
Layer 4: ArrayList (concrete)     ← "I use Object[] array"
```

**Why 3 layers?** Code reuse without forcing inheritance.

```java
// AbstractCollection provides these for FREE:
public abstract class AbstractCollection<E> implements Collection<E> {
    public boolean isEmpty() { return size() == 0; }     // Uses your size()
    public String toString() { /* iterates and builds string */ }
    public boolean contains(Object o) { /* iterates and checks equals */ }
}
```

**Your job as ArrayList**: Just implement `size()` and the array operations. Everything else comes free.

---

## 1.4 Iterable vs Collection vs Iterator

### The Library Analogy

```
📚 Library (Iterable)  = "I have books you can browse"
📖 Librarian (Iterator) = "I hand you books one at a time and remember where you are"
🗂️ Catalog (Collection) = "Rules for adding/removing/counting books"
```

### How foreach Actually Works

```java
// You write this:
for (String s : list) {
    System.out.println(s);
}

// Java compiler converts to this:
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    System.out.println(s);
}
```

### The Three Interfaces

```java
// 1. Iterable — "Can be looped"
public interface Iterable<T> {
    Iterator<T> iterator();  // Returns an iterator
}

// 2. Iterator — "Does the looping"  
public interface Iterator<E> {
    boolean hasNext();       // More elements?
    E next();                // Get next element
    default void remove() { throw new UnsupportedOperationException(); }
}

// 3. Collection — "Group of elements"
public interface Collection<E> extends Iterable<E> {
    boolean add(E e);
    boolean remove(Object o);
    int size();
    boolean isEmpty();
    boolean contains(Object o);
    void clear();
}
```

### Fail-Fast vs Fail-Safe (CRITICAL Interview Topic)

**Fail-Fast (ArrayList, HashMap, HashSet)**:
- Maintains `int modCount` (modification counter)
- Iterator stores `expectedModCount` at creation
- On every `next()` and `remove()`: checks `modCount == expectedModCount`
- If mismatch → `ConcurrentModificationException`

```java
// Internal mechanism:
class ArrayList {
    private int modCount = 0;
    
    public boolean add(E e) {
        modCount++;  // Every structural modification increments
        // ... add to array ...
    }
    
    private class Itr implements Iterator<E> {
        int expectedModCount = modCount;  // Snapshot at creation
        
        public E next() {
            checkForComodification();
            // ... actual next logic ...
        }
        
        void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }
}

// This throws:
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
for (String s : list) {
    if (s.equals("B")) list.remove(s);  // modCount changes! Exception!
}
```

**Fail-Safe (ConcurrentHashMap, CopyOnWriteArrayList)**:
- Iterator works on a SNAPSHOT of the data
- Modifications after creation are NOT seen
- But NO exception is thrown either

```java
ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
map.put("A", "1");
Iterator<String> it = map.keySet().iterator();
map.put("B", "2");  // After iterator creation
it.next();  // Works fine! Might see "B", might not. No exception!
```

| Aspect | Fail-Fast | Fail-Safe |
|--------|-----------|-----------|
| **Exception on concurrent mod** | ✅ Yes | ❌ No |
| **Sees latest data** | ✅ Yes | ❌ May be stale |
| **Uses** | Single-thread (bug detection) | Multi-thread (must not crash) |
| **Memory** | Low (just int) | Higher (snapshot) |
| **Collections** | ArrayList, HashMap, HashSet | ConcurrentHashMap, CopyOnWriteArrayList |

---

# PART 2: LISTS

## 2.1 ArrayList — The Resizable Array

### The Problem It Solves

Regular arrays have FIXED size. You must know the size at creation.

```java
String[] names = new String[3];  // Fixed at 3 — can't grow!
names[3] = "Dave";  // ❌ ArrayIndexOutOfBoundsException!
```

**Before ArrayList**: You manually copied arrays every time you needed to grow.

```java
String[] old = new String[2];
old[0] = "A"; old[1] = "B";
// Need to add "C":
String[] newArr = new String[3];
System.arraycopy(old, 0, newArr, 0, 2);
newArr[2] = "C";
old = newArr;  // Tedious! Error-prone!
```

**ArrayList automates this**: Just call `add()`.

### Internal Structure

```java
public class ArrayList<E> {
    transient Object[] elementData;  // The actual array
    private int size;                 // How many elements used
    
    // Default: start with 10
    public ArrayList() {
        this.elementData = new Object[10];
        this.size = 0;
    }
}
```

Memory picture:

```
ArrayList (capacity=10, size=3):
┌─────┬─────┬─────┬──────┬──────┬──────┬──────┬──────┬──────┬──────┐
│ "A" │ "B" │ "C" │ null │ null │ null │ null │ null │ null │ null │
└─────┴─────┴─────┴──────┴──────┴──────┴──────┴──────┴──────┴──────┘
   0     1     2      3      4      5      6      7      8      9
                                   ↑ capacity - size = 7 unused slots
```

### Key Operations (with exact time complexity)

```
✅ get(int index):
   return elementData[index];  → O(1) — DIRECT array access
   No searching. No looping. Just jump to memory location.
   
✅ set(int index, E e):
   elementData[index] = e;     → O(1) — Same direct access

✅ add(E e) [at end]:
   elementData[size++] = e;    → O(1) amortized
   
   "Amortized" means: MOST adds are O(1). Occasionally one is O(n) 
   when array needs to grow. But the AVERAGE across all adds is O(1).
   
   Proof: Adding N elements starting from capacity=1:
     Add 1:  capacity=1 → full → grow to 2, copy 1  → cost = 1
     Add 2:  capacity=2 → full → grow to 3, copy 2  → cost = 2
     Add 3:  capacity=3 → full → grow to 4, copy 3  → cost = 3
     Add 4:  capacity=4 → full → grow to 6, copy 4  → cost = 4
     Add 5:  no grow → cost = 0
     Add 6:  full → grow to 9, copy 6 → cost = 6
     
     Total cost for N adds ≈ N (sum of resizes ≈ N)
     Average per add = N/N = 1 → O(1) amortized

❌ add(int index, E e) [at position]:
   System.arraycopy(elementData, index, elementData, index+1, size-index);
   elementData[index] = e;
   → O(n) because ALL elements after index shift right

❌ remove(int index):
   System.arraycopy(elementData, index+1, elementData, index, size-index-1);
   → O(n) because ALL elements after index shift left

❌ remove(Object o):
   First: indexOf(o) → O(n) scan
   Then: shift elements → O(n)
   Total: O(n)

❌ contains(Object o):
   indexOf(o) → O(n) — linear scan of entire array

❌ remove(Object o) for first element:
   Shift ALL n-1 elements left → O(n)

✅ remove last element:
   elementData[--size] = null → O(1) — no shift needed!
```

### Growth Strategy: Why 1.5x?

```java
private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);  // old + old/2 = 1.5x
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

**Why not 2x?**
- 2x: Less copying (good), but wastes up to 50% memory (bad)
- 1.5x: Slightly more copying, but wastes only ~33% memory
- Joshua Bloch chose 1.5x empirically as the sweet spot

**Growth sequence**: 10 → 15 → 22 → 33 → 49 → 73 → 109 → 163 → 244 → 366 → ...

### Complete Time Complexity Table

| Operation | Complexity | Exact Explanation |
|-----------|-----------|-------------------|
| `get(i)` | **O(1)** | `return elementData[i]` — direct array index |
| `set(i, e)` | **O(1)** | `elementData[i] = e` — direct array index |
| `add(e)` (end) | **O(1)*** | `elementData[size++] = e` — *amortized, occasional resize |
| `add(i, e)` (middle) | **O(n)** | Shift n-i elements right |
| `add(0, e)` (front) | **O(n)** | Shift ALL n elements right |
| `remove(i)` | **O(n)** | Shift n-i-1 elements left |
| `remove(0)` (front) | **O(n)** | Shift ALL n-1 elements left |
| `remove(Object)` | **O(n)** | O(n) scan to find + O(n) shift |
| `contains(o)` | **O(n)** | Linear scan: for(i=0; i<size; i++) |
| `indexOf(o)` | **O(n)** | Same linear scan |
| `lastIndexOf(o)` | **O(n)** | Reverse linear scan |
| `size()` | **O(1)** | Return `size` field |
| `isEmpty()` | **O(1)** | Return `size == 0` |
| `clear()` | **O(n)** | Must null each slot: for(i=0; i<size; i++) data[i]=null |
| `iterator()` | **O(1)** | Create iterator object (O(1), no data copy) |
| `full iteration` | **O(n)** | Visit each element exactly once |
| `subList(i, j)` | **O(1)** | Returns VIEW, not copy |
| `trimToSize()` | **O(n)** | Copy to new array of exact size |
| `ensureCapacity()` | **O(n)** | May trigger full array copy |

---

## 2.2 LinkedList — The Doubly-Linked Chain

### The Problem It Solves

ArrayList has a fatal weakness: **front insertion is O(n)**.

```java
ArrayList<String> list = new ArrayList<>();
list.add(0, "X");  // O(n) — shifts ALL elements right!
list.remove(0);    // O(n) — shifts ALL elements left!
```

**LinkedList: Each element is a separate NODE with pointers to neighbors**.

```java
private static class Node<E> {
    E item;         // The data
    Node<E> prev;   // Pointer to previous node
    Node<E> next;   // Pointer to next node
}
```

### Internal Structure

```
LinkedList<String> list = new LinkedList<>();
list.add("A"); list.add("B"); list.add("C");

Memory (NOT contiguous!):
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ prev = null  │    │ prev = 0xABC │    │ prev = 0xDEF │
│ item = "A"   │ ←─→│ item = "B"   │ ←─→│ item = "C"   │
│ next = 0xABC │    │ next = 0xDEF │    │ next = null  │
└──────────────┘    └──────────────┘    └──────────────┘
  ↑ first                                    ↑ last
```

### Complete Time Complexity

| Operation | Complexity | Exact Explanation |
|-----------|-----------|-------------------|
| `get(i)` | **O(n)** | Must traverse from nearest end: i steps forward OR size-i backward |
| `set(i, e)` | **O(n)** | Must traverse to find node first (O(n)), then set (O(1)) |
| `add(e)` (end) | **O(1)** | `last.next = newNode; last = newNode;` — 2 pointer updates |
| `addFirst(e)` | **O(1)** | `newNode.next = first; first.prev = newNode; first = newNode;` |
| `addLast(e)` | **O(1)** | Same as add(e) |
| `add(i, e)` | **O(n)** | Traverse to i (O(min(i, n-i))), then link (O(1)) |
| `removeFirst()` | **O(1)** | `first = first.next; first.prev = null;` |
| `removeLast()` | **O(1)** | `last = last.prev; last.next = null;` |
| `remove(i)` | **O(n)** | Traverse to i (O(n)), then unlink (O(1)) |
| `remove(Object)` | **O(n)** | Linear scan to find, then O(1) unlink |
| `contains(o)` | **O(n)** | Linear scan through all nodes |
| `size()` | **O(1)** | Return `size` field |
| `indexOf(o)` | **O(n)** | Linear scan with counter |
| `offer(e)` | **O(1)** | Same as add(e) at end |
| `poll()` | **O(1)** | Same as removeFirst() |
| `peek()` | **O(1)** | Return `first.item` |
| `iterator()` | **O(1)** | Create iterator with cursor = first |
| `full iteration` | **O(n)** | Walk through each node exactly once |

### The node() Optimization (Partial)

```java
Node<E> node(int index) {
    if (index < (size >> 1)) {        // First half?
        Node<E> x = first;
        for (int i = 0; i < index; i++) x = x.next;
        return x;
    } else {                           // Second half?
        Node<E> x = last;
        for (int i = size - 1; i > index; i--) x = x.prev;
        return x;
    }
}
```

**Effect**: `get(size-1)` is O(1) (starts from last). `get(size/2)` is still O(n/2).

### The Memory Problem (Why LinkedList is EXPENSIVE)

```
For 3 String elements:
┌──────────────────────┬──────────────────────┬──────────────────────┐
│ ArrayList:           │ LinkedList:           │ Memory Ratio         │
│ Object[3] array      │ 3 Node objects       │                      │
│ 12 bytes (3 refs)    │ 72+ bytes (3 nodes)  │ LinkedList = 6x MORE │
└──────────────────────┴──────────────────────┴──────────────────────┘

Each Node (64-bit JVM, compressed OOPs):
  12 bytes object header
  4 bytes  item (reference)
  4 bytes  prev (reference)
  4 bytes  next (reference)
  ─────────────────
  24 bytes per node × 3 = 72 bytes
```

### When to ACTUALLY Use LinkedList

Only when you need BOTH List and Deque operations on the SAME collection. This is rare.

---

# PART 3: MAPS (MOST IMPORTANT)

## 3.1 HashMap — The King of Collections

### ⚠️ CRITICAL: HashMap Time Complexity is NOT Always O(1)

This is what got you rejected. The COMPLETE answer:

```
HashMap time complexity:

Operation | Best Case   | Average Case | Worst Case (Java 7) | Worst Case (Java 8+)
----------|-------------|--------------|--------------------|--------------------
put()     | O(1)        | O(1)         | O(n)               | O(log n)
get()     | O(1)        | O(1)         | O(n)               | O(log n)  
remove()  | O(1)        | O(1)         | O(n)               | O(log n)
contains()| O(1)        | O(1)         | O(n)               | O(log n)

Best case:   Perfect hash — every key goes to a DIFFERENT bucket. Direct array access.
Average case: Reasonable hash distribution — most buckets have 0-3 elements.
Worst case:  ALL keys have SAME hash → all in ONE bucket.
             Java 7: That bucket becomes a linked list → O(n)
             Java 8+: After 8 collisions, list becomes Red-Black tree → O(log n)
```

### The Problem HashMap Solves

**Before HashMap**: To find something by a key, you scanned the entire list.

```java
// Finding by ID — O(n) scan!
List<Person> people = List.of(new Person(1001, "Alice"), ...);
for (Person p : people) {
    if (p.id() == 1002) { found = p; break; }  // Check every element!
}
```

**HashMap: O(1) lookup by any object key.**

### How HashMap Works (3 Steps)

```
put("Alice", age30):
  Step 1: "Alice".hashCode() → -2013148756  (32-bit integer)
  Step 2: hash = h ^ (h >>> 16)             (spread high bits into low bits)
  Step 3: index = (n - 1) & hash            (convert to array index)
           if n=16: (16-1) & hash = 15 & hash = last 4 bits
  Step 4: table[index] = node               (store at that bucket)
```

### Internal Structure

```java
public class HashMap<K, V> {
    // The core: array of buckets
    transient Node<K, V>[] table;
    
    // Default: 16 buckets
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    
    // When to resize: size > capacity × 0.75
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    // Java 8+: When to convert linked list → tree (for collision handling)
    static final int TREEIFY_THRESHOLD = 8;
    
    // A single bucket entry (linked list node OR tree node)
    static class Node<K, V> {
        final int hash;    // Cached hash code (computed once)
        final K key;       // The key (immutable recommended)
        V value;           // The value
        Node<K, V> next;   // Next node in same bucket (collision chain)
    }
}
```

### Why Capacity Must Be Power of 2

```java
// Index calculation: (n - 1) & hash
// If n = 16 (power of 2):
//   n-1 = 15 = 0b1111
//   (0b1111) & hash = last 4 bits of hash → good distribution

// If n = 17 (NOT power of 2):
//   n-1 = 16 = 0b10000
//   (0b10000) & hash = either 0 or 16 → TERRIBLE distribution!
//   Only 2 possible buckets! Most buckets empty!
```

### Complete put() Flow (Java 8+)

```java
public V put(K key, V value) {
    // 1. Handle null key separately
    if (key == null) return putForNullKey(value);
    
    // 2. Compute hash (spread bits)
    int hash = hash(key);
    
    // 3. Find bucket index
    int index = (table.length - 1) & hash;
    
    // 4. Check if key already exists in this bucket
    for (Node<K, V> e = table[index]; e != null; e = e.next) {
        if (e.hash == hash && (e.key == key || key.equals(e.key))) {
            // Key found → REPLACE value
            V oldValue = e.value;
            e.value = value;
            return oldValue;
        }
    }
    
    // 5. Key NOT found → add new node
    //    If bucket already has ≥ TREEIFY_THRESHOLD (8) nodes,
    //    convert linked list → Red-Black tree
    addEntry(hash, key, value, index);
    return null;
}
```

### The Hash Function (Why It Matters)

```java
// Java 8's hash function:
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    // XOR high 16 bits into low 16 bits
    // Why? hashCode() can return any 32-bit value
    // But we only use low bits for bucket index (n-1)
    // High bits are "wasted" — so we mix them in
}

// Example:
// key.hashCode() = 0b 1010 1100 0101 1000 1111 0000 0011 0110
// h >>> 16      = 0b 0000 0000 0000 0000 1010 1100 0101 1000
// XOR           = 0b 1010 1100 0101 1000 0101 1100 0110 1110
//                   (high bits preserved, low bits improved)
```

### Resize: When and How

```java
// WHEN: size > capacity × loadFactor
// Example: capacity=16, loadFactor=0.75 → resize when size > 12

// HOW: Double capacity, rehash ALL entries
void resize(int newCapacity) {
    Node<K, V>[] oldTable = table;
    int oldCapacity = oldTable.length;
    
    Node<K, V>[] newTable = new Node[newCapacity];
    table = newTable;
    
    // Rehash each entry — DON'T just copy!
    // Because (n-1) changed, the index for each key may be DIFFERENT
    for (Node<K, V> e : oldTable) {
        if (e != null) {
            // Re-compute index: (newCapacity - 1) & hash(e.key)
            // Add to newTable at new position
        }
    }
}

// Java 8 OPTIMIZATION: 
// If old capacity = 16, new capacity = 32
// An entry either STAYS at same index OR moves to index + oldCapacity
// This is determined by: (hash & oldCapacity) == 0 → stay; != 0 → move
// This avoids re-computing hash for every entry!
```

### Collision Resolution: The Complete Story

```
Java 7: Linked list ONLY
  Problem: If 1000 keys collide → O(1000) for get! → O(n)

Java 8+: Linked list → Red-Black tree at 8 collisions
  Threshold = 8 (from Poisson distribution analysis)
  Probability of 8 collisions in same bucket with good hash: < 1 in 10 MILLION
  
  But if hashCode is BAD (all keys return same hash):
  → 1000 keys in ONE bucket
  → After 8th collision: linked list → Red-Black tree
  → get() becomes O(log 1000) instead of O(1000)!
  
  Tree → list conversion at 6 collisions (hysteresis to prevent oscillation)
```

### Thread Safety: The FULL Truth

```java
// NO, HashMap is NOT thread-safe. Period.

// What happens with 2 threads putting simultaneously:

// Thread 1: put("A", 1)
//   Reads table.length (16)
//   Computes index (5)
//   Reads table[5] → null
//   [CONTEXT SWITCH]

// Thread 2: put("B", 2)
//   Reads table.length (16)
//   Computes index (5)
//   Sets table[5] = Node("B", 2)
//   Size increment (size = 1)
//   [CONTEXT SWITCH]

// Thread 1 resumes:
//   Sets table[5] = Node("A", 1)  ← OVERWRITES Thread 2's entry!
//   Size increment (size = 2)
//   Result: Node("B", 2) is LOST!

// Worse: During resize, concurrent put can cause:
//   Java 7: INFINITE LOOP (circular linked list) → CPU 100% forever
//   Java 8: Data corruption, null values, missing entries

// SOLUTION: ConcurrentHashMap
ConcurrentHashMap<String, Integer> safe = new ConcurrentHashMap<>();
```

### When is O(1) NOT Actually O(1)?

```
Scenario 1: Poor hashCode
  class User {
      String name;
      
      @Override
      public int hashCode() {
          return 42;  // ALL users have same hash → ALL in ONE bucket
      }
  }
  // get() becomes O(n) [Java 7] or O(log n) [Java 8+]

Scenario 2: Resize is O(n)
  HashMap<String, Integer> map = new HashMap<>(16);
  map.put("A", 1);  // O(1)
  // ... 12 more puts (all O(1)) ...
  map.put("N", 14);  // size=13 > 16*0.75=12 → RESIZE!
  // 13 entries → new array of 32 → rehash all 13 → O(n)
  // That ONE put cost O(n)

Scenario 3: hashCode() is expensive
  class Document {
      byte[] content = // 100MB file;
      
      @Override
      public int hashCode() {
          return computeHash(content);  // Reads 100MB file!
      }
  }
  // put() calls hashCode() → reads 100MB → takes 100ms
  // Even though bucket calculation is O(1), the hash computation is NOT
```

### HashMap Time Complexity Table (COMPLETE)

| Operation | Best Case | Average Case | Worst (Java 7) | Worst (Java 8+) | Notes |
|-----------|-----------|--------------|----------------|-----------------|-------|
| `put(k, v)` | **O(1)** | **O(1)** | **O(n)** | **O(log n)** | Tree after 8 collisions (Java 8+) |
| `get(k)` | **O(1)** | **O(1)** | **O(n)** | **O(log n)** | Direct bucket or tree search |
| `remove(k)` | **O(1)** | **O(1)** | **O(n)** | **O(log n)** | Find + unlink |
| `containsKey(k)` | **O(1)** | **O(1)** | **O(n)** | **O(log n)** | Same as get() internally |
| `containsValue(v)` | **O(n)** | **O(n)** | **O(n)** | **O(n)** | Must scan ALL buckets and all entries |
| `size()` | **O(1)** | **O(1)** | **O(1)** | **O(1)** | Return `size` field |
| `keySet()` | **O(1)** | **O(1)** | **O(1)** | **O(1)** | Returns view (not copy) |
| `resize()` | **O(n)** | **O(n)** | **O(n)** | **O(n)** | Rehash ALL entries to new array |
| `iteration` | **O(c+n)** | **O(c+n)** | **O(c+n)** | **O(c+n)** | c = capacity, n = size. Must scan empty buckets too |

**Best case for get/put**: Perfect hash distribution. Each bucket has 0 or 1 entries. Direct index access.

**Worst case**: All keys have identical hashCode. Single bucket with n entries.

**Memory**: HashMap with capacity 16, 10 entries ≈ 16 bucket references + 10 Node objects ≈ 320 bytes overhead.

---

## 3.2 LinkedHashMap — HashMap That Remembers Order

### What It Adds

```java
public class LinkedHashMap<K, V> extends HashMap<K, V> {
    // Extra pointers to maintain ORDER
    transient LinkedHashMap.Entry<K, V> head;  // Oldest entry
    transient LinkedHashMap.Entry<K, V> tail;  // Newest entry
    
    // Each entry also has before/after pointers:
    static class Entry<K, V> extends HashMap.Node<K, V> {
        Entry<K, V> before, after;  // Doubly-linked chain for order
    }
}
```

### Two Ordering Modes

```java
// 1. INSERTION ORDER (default)
LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
map.put("A", 1);
map.put("C", 3);
map.put("B", 2);
// Iteration: A → C → B (insertion order!)
// HashMap would give unpredictable order

// 2. ACCESS ORDER (for LRU cache)
LinkedHashMap<String, Integer> lru = new LinkedHashMap<>(16, 0.75f, true);
// accessOrder=true → every get()/put() moves entry to END
// Most recently accessed = at end
// Least recently accessed = at head
```

### LRU Cache in 3 Lines

```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;
    
    LRUCache(int maxSize) {
        super(16, 0.75f, true);  // accessOrder=true
        this.maxSize = maxSize;
    }
    
    // Called AFTER every put(). Remove eldest if over capacity.
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}

// Usage:
LRUCache<String, String> cache = new LRUCache<>(3);
cache.put("A", "1");  // cache: A (eldest)
cache.put("B", "2");  // cache: A, B
cache.put("C", "3");  // cache: A, B, C
cache.get("A");        // cache: B, C, A (A moved to end)
cache.put("D", "4");  // cache: C, A, D (B evicted! It was eldest)
```

### Time Complexity

| Operation | Complexity | Same as HashMap? |
|-----------|-----------|-----------------|
| `put(k, v)` | **O(1)** avg | Same + O(1) to update linked chain |
| `get(k)` | **O(1)** avg | Same + O(1) to move to end (access order) |
| `remove(k)` | **O(1)** avg | Same + O(1) to unlink from chain |
| `containsKey(k)` | **O(1)** avg | Same |
| `containsValue(v)` | **O(n)** | Same |
| `iteration` | **O(n)** | **FASTER than HashMap!** No empty bucket scanning |

**Iteration is actually FASTER than HashMap** because LinkedHashMap maintains a separate doubly-linked list of ALL entries. HashMap must scan through possibly empty buckets. LinkedHashMap just follows the chain.

---

# PART 4: SETS

## 4.1 HashSet — Backed by HashMap

### The Secret: HashSet is JUST a HashMap

```java
public class HashSet<E> extends AbstractSet<E> implements Set<E> {
    // THE SECRET: HashSet uses HashMap internally!
    private transient HashMap<E, Object> map;
    
    // Dummy value for all keys
    private static final Object PRESENT = new Object();
    
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;  // Element is the KEY!
    }
    
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }
    
    public boolean contains(Object o) {
        return map.containsKey(o);  // O(1) average!
    }
}
```

**Critical insight**: When you `add("Alice")`, HashSet stores:
- Key = "Alice"
- Value = a dummy object (PRESENT)

The VALUE is never used. Only keys matter. That's why HashSet is O(1) — it's HashMap underneath.

### Time Complexity (Same as HashMap)

| Operation | Best | Average | Worst (Java 7) | Worst (Java 8+) |
|-----------|------|---------|----------------|-----------------|
| `add(e)` | O(1) | O(1) | O(n) | O(log n) |
| `remove(e)` | O(1) | O(1) | O(n) | O(log n) |
| `contains(e)` | O(1) | O(1) | O(n) | O(log n) |
| `size()` | O(1) | O(1) | O(1) | O(1) |
| `iteration` | O(c+n) | O(c+n) | O(c+n) | O(c+n) |

**Why iteration is O(c+n)**: Must scan HashMap's bucket array. Capacity c may be much larger than size n.

---

## 4.2 TreeSet — Sorted, Backed by TreeMap

### The Secret: TreeSet = TreeMap (Red-Black Tree)

```java
public class TreeSet<E> extends AbstractSet<E> 
        implements NavigableSet<E>, Cloneable, java.io.Serializable {
    
    // Backed by NavigableMap (TreeMap)
    private transient NavigableMap<E, Object> m;
    private static final Object PRESENT = new Object();
    
    public boolean add(E e) {
        return m.put(e, PRESENT) == null;  // Element is the KEY in TreeMap
    }
}
```

TreeMap stores keys in a **Red-Black Tree** — a self-balancing binary search tree.

```
           "Charlie" (BLACK)
          /         \
   "Alice" (RED)  "David" (RED)
                         \
                     "Eve" (RED)

Properties:
1. Root is BLACK
2. No two RED nodes adjacent (parent and child can't both be RED)
3. Every path from root to leaf has same number of BLACK nodes
4. This ensures O(log n) height
```

### Time Complexity

| Operation | Complexity | Why |
|-----------|-----------|-----|
| `add(e)` | **O(log n)** | Insert into balanced BST (log n comparisons) |
| `remove(e)` | **O(log n)** | Find + rebalance |
| `contains(e)` | **O(log n)** | Binary search down the tree |
| `first()` | **O(log n)** | Traverse to leftmost node |
| `last()` | **O(log n)** | Traverse to rightmost node |
| `lower(e)` | **O(log n)** | Find greatest element < e |
| `higher(e)` | **O(log n)** | Find smallest element > e |
| `ceiling(e)` | **O(log n)** | Find smallest element >= e |
| `floor(e)` | **O(log n)** | Find greatest element <= e |
| `subSet(a, b)` | **O(1)** | Returns view (not copy) |
| `headSet(e)` | **O(1)** | Returns view (not copy) |
| `tailSet(e)` | **O(1)** | Returns view (not copy) |
| `size()` | **O(1)** | Return `size` field |
| `iteration` | **O(n)** | In-order traversal |

### Comparable vs Comparator

```java
// Comparable: Natural ordering (String, Integer already implement it)
// If your class doesn't implement Comparable:
class Person implements Comparable<Person> {
    String name;
    int age;
    
    @Override
    public int compareTo(Person other) {
        return this.name.compareTo(other.name);  // Sort by name alphabetically
    }
}
TreeSet<Person> byName = new TreeSet<>();  // Uses compareTo()

// Comparator: Custom ordering (without modifying the class)
TreeSet<Person> byAge = new TreeSet<>(
    (p1, p2) -> Integer.compare(p1.age, p2.age)  // Sort by age
);
```

---

# PART 5: QUEUES

## 5.1 PriorityQueue — Binary Heap

### The Problem

Regular Queue: FIFO (first in, first out). But what if some tasks are MORE URGENT?

```java
// FIFO queue processes in order added → urgent tasks wait behind normal tasks!
Queue<Task> q = new LinkedList<>();
q.offer(new Task("Normal", 5));
q.offer(new Task("URGENT", 1));  // This should go FIRST!
```

**PriorityQueue: Always returns HIGHEST PRIORITY element first, regardless of insertion order.**

### Internal Structure: Binary Heap

```
Not sorted! But SMALLEST element is ALWAYS at root.

Min-heap (default):
          1 (root = smallest)       ← always at index 0
        /   \
       3     5
      / \   /
     7   9 8

Array representation: [1, 3, 5, 7, 9, 8, ...]

For any node at index i:
  left child  = 2*i + 1
  right child = 2*i + 2
  parent      = (i - 1) / 2
```

### Key Operations

```java
// offer(e) — Add element → O(log n)
// 1. Add to end of array
// 2. Bubble UP: compare with parent, swap if smaller
// 3. Continue until parent is smaller or root reached

// poll() — Remove smallest → O(log n)
// 1. Save root (smallest) for return
// 2. Move LAST element to root
// 3. Bubble DOWN: compare with children, swap with smaller
// 4. Continue until position correct

// peek() — Look at smallest → O(1)
// return queue[0] — ALWAYS the smallest!
```

### Step-by-Step Example

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();

// offer(5):
// Array: [5, _, _, ...]  → root = 5

// offer(3):
// Array: [5, 3, _, ...]  → add at end
// Bubble up: compare 3 with parent(5) → 3 < 5 → SWAP
// Array: [3, 5, _, ...]  → root = 3 (smallest)

// offer(7):
// Array: [3, 5, 7, ...]  → add at end
// Bubble up: compare 7 with parent(1)→5 → 7 > 5 → STOP
// Array: [3, 5, 7, ...]  → OK

// offer(1):
// Array: [3, 5, 7, 1, ...]  → add at end
// Bubble up: compare 1 with parent(1)→5 → 1 < 5 → SWAP
// Array: [3, 1, 7, 5, ...]
// Bubble up: compare 1 with parent(0)→3 → 1 < 3 → SWAP
// Array: [1, 3, 7, 5, ...]  → root = 1 (smallest!)

// poll():
// Save root (1) for return
// Move last (5) to root: [5, 3, 7, _, ...]
// Bubble down: compare 5 with children(3, 7) → 3 is smaller → SWAP with 3
// Array: [3, 5, 7, ...]
// Return 1
```

### Time Complexity

| Operation | Complexity | Why |
|-----------|-----------|-----|
| `offer(e)` | **O(log n)** | Add at end + bubble up (tree height = log n) |
| `poll()` | **O(log n)** | Move last to root + bubble down (tree height = log n) |
| `peek()` | **O(1)** | Return `queue[0]` — always the smallest |
| `remove(o)` | **O(n)** | Must scan to find (O(n)) + remove + bubble (O(log n)) |
| `contains(o)` | **O(n)** | Must scan entire array (no ordering guarantee for search) |
| `size()` | **O(1)** | Return `size` field |
| `iteration` | **O(n)** | Visit each element (BUT NOT in sorted order!) |

### ⚠️ CRITICAL: Iterator is NOT Sorted!

```java
PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 3, 8, 2));
System.out.println(pq);     // [1, 2, 3, 8, 5] — LOOKS sorted but NOT guaranteed!
for (int n : pq) {
    System.out.println(n);  // 1, 2, 3, 8, 5 — NOT sorted!
}

// Only poll() returns in sorted order:
while (!pq.isEmpty()) {
    System.out.println(pq.poll());  // 1, 2, 3, 5, 8 — YES sorted!
}
```

### Min-Heap vs Max-Heap

```java
// Default: min-heap (smallest = highest priority)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max-heap (largest = highest priority):
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
maxHeap.offer(5);
maxHeap.offer(1);
maxHeap.offer(3);
System.out.println(maxHeap.poll());  // 5 — largest first!
```

---

## 5.2 ArrayDeque — Circular Array

### What It Solves

```
Problem: Need O(1) add/remove at BOTH ends.
- ArrayList: O(1) at end, O(n) at front  ❌
- LinkedList: O(1) at both ends, but high memory + poor cache  ❌
- ArrayDeque: O(1) at both ends, low memory, good cache  ✅
```

### Internal Structure: Circular Array

```
ArrayDeque<String> deque = new ArrayDeque<>(8);
deque.addLast("A");  // tail = 0
deque.addLast("B");  // tail = 1

Initial:
  head=0, tail=2
  [A, B, _, _, _, _, _, _]
    ↑
  tail (adds go here)

After addFirst("X"):
  head = (0 - 1) & (8 - 1) = 7
  [A, B, _, _, _, _, _, X]
                         ↑
                       head

After removeFirst():
  X removed, head = (7 + 1) & 7 = 0
  [A, B, _, _, _, _, _, _]
   ↑
  head

After removeLast():
  tail = (2 - 1) & 7 = 1
  [A, _, _, _, _, _, _, _]
       ↑
     tail (now points to empty slot before last element)
```

**The trick**: `(head - 1) & (capacity - 1)` wraps around when capacity is power of 2.

### Time Complexity

| Operation | Complexity | Why |
|-----------|-----------|-----|
| `addFirst(e)` | **O(1)** | `elements[head = (head - 1) & (len - 1)] = e` |
| `addLast(e)` | **O(1)** | `elements[tail] = e; tail = (tail + 1) & (len - 1)` |
| `removeFirst()` | **O(1)** | `e = elements[head]; elements[head] = null; head = (head + 1) & (len - 1)` |
| `removeLast()` | **O(1)** | `tail = (tail - 1) & (len - 1); e = elements[tail]; elements[tail] = null` |
| `getFirst()` | **O(1)** | Return `elements[head]` |
| `getLast()` | **O(1)** | Return `elements[(tail - 1) & (len - 1)]` |
| `offer(e)` | **O(1)** | Same as addLast(e) |
| `poll()` | **O(1)** | Same as removeFirst() |
| `peek()` | **O(1)** | Return `elements[head]` |
| `push(e)` | **O(1)** | Same as addFirst(e) |
| `pop()` | **O(1)** | Same as removeFirst() |
| `contains(o)` | **O(n)** | Linear scan from head to tail |
| `size()` | **O(1)** | `(tail - head) & (len - 1)` |
| `iteration` | **O(n)** | Walk from head to tail |

### ArrayDeque vs LinkedList: The Winner

```
┌──────────────────┬─────────────┬──────────────┐
│                  │ ArrayDeque  │  LinkedList   │
├──────────────────┼─────────────┼──────────────┤
│ addFirst/addLast │ O(1)        │ O(1)          │
│ Memory per elem  │ 4 bytes     │ 24+ bytes     │
│ Cache locality   │ Excellent   │ Poor           │
│ Can be List?     │ ❌ No       │ ✅ Yes         │
│ Null elements    │ ❌ No       │ ✅ Yes         │
│ Random access    │ ❌ No       │ O(n)           │
└──────────────────┴─────────────┴──────────────┘

Bottom line: For Queue/Deque/Stack, ALWAYS use ArrayDeque over LinkedList.
```

---

# PART 6: CONCURRENT COLLECTIONS

## 6.1 ConcurrentHashMap — Thread-Safe HashMap

### The Problem: HashMap is NOT Thread-Safe

```java
// Two threads putting into HashMap simultaneously:
HashMap<String, Integer> map = new HashMap<>();

// Thread 1: map.put("A", 1)
// Thread 2: map.put("B", 2)

// Possible outcomes:
// 1. Lost update: One put overwrites the other silently
// 2. Corrupted size counter: map.size() returns wrong value
// 3. Java 7: INFINITE LOOP during resize (CPU 100%)
// 4. Java 8: Data corruption, null values

// "Look at, don't touch" — reading during write is also unsafe!
```

### How ConcurrentHashMap is Different

```java
// Synchronized map (bad approach):
Map<String, String> bad = Collections.synchronizedMap(new HashMap<>());
// Every operation locks the ENTIRE map
// One thread reading → blocks ALL other threads
// Throughput: terrible

// ConcurrentHashMap:
ConcurrentHashMap<String, String> good = new ConcurrentHashMap<>();
// Reads: NEVER blocked (no locks!)
// Writes: Only lock the SPECIFIC bucket being written
// Multiple threads can write to DIFFERENT buckets simultaneously
```

### Internal Structure (Java 8+)

```java
public class ConcurrentHashMap<K, V> {
    // Array of "bins" (buckets)
    transient volatile Node<K, V>[] table;
    
    // Each bin has its OWN lock
    // Actually: Uses synchronized on the first node of each bin
    // (Not a separate lock object — memory efficient!)
    
    // Read operations: NO locks!
    // get() just reads table[index] → O(1)
    // Because Node.key and Node.val are VOLATILE → visibility guaranteed
    
    // Write operations: synchronized on bin's first node
    // put() locks only the specific bin being written to
    // Multiple threads write to different bins → NO CONTENTION
    
    // Size tracking: Uses LongAdder (striped counters)
    // Multiple counters updated independently
    // sum() at the end adds them all up
}
```

### Key Differences from HashMap

| Aspect | HashMap | ConcurrentHashMap |
|--------|---------|-------------------|
| **Thread-safe** | ❌ No | ✅ Yes |
| **Null keys** | ✅ Yes (1 key) | ❌ No (NPE) |
| **Null values** | ✅ Yes | ❌ No (NPE) |
| **Read concurrency** | Not safe | **Lock-free** (always safe) |
| **Write concurrency** | Not safe | Lock per bucket |
| **size() accuracy** | Exact | Approximate (striped counter) |
| **Iterator** | Fail-fast | Fail-safe (snapshot) |
| **Performance (read)** | Very fast | Same as HashMap |
| **Performance (write)** | Fast | Slightly slower (lock overhead) |

### Complete Time Complexity

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| `get(k)` | **O(1)** avg | Lock-free! Volatile read of table[index] |
| `put(k, v)` | **O(1)** avg | Synchronized on bin's first node |
| `remove(k)` | **O(1)** avg | Synchronized on bin's first node |
| `containsKey(k)` | **O(1)** avg | Same as get() |
| `containsValue(v)` | **O(n)** | Must scan entire table |
| `size()` | **O(1)** | Sum of striped counters (approximate) |
| `mappingCount()` | **O(1)** | Returns long (more accurate than size()) |
| `putIfAbsent(k, v)` | **O(1)** avg | Atomic "put if not exists" |
| `computeIfAbsent(k, fn)` | **O(1)** avg | Atomic "compute if not exists" |
| `iteration` | **O(c+n)** | May see stale data (snapshot) |

---

## 6.2 BlockingQueue — Producer-Consumer

### The Problem

```java
// Without BlockingQueue: Producers and consumers must coordinate manually
// Producer: "I made an item!" (but queue is full!)
// Consumer: "I want an item!" (but queue is empty!)
// Manual wait/notify is ERROR-PRONE

class Producer extends Thread {
    Queue<String> queue;
    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.size() == MAX) {
                    queue.wait();  // Must wait for space
                }
                queue.offer("item");
                queue.notifyAll();  // Must notify consumer
            }
        }
    }
}

// BlockingQueue handles ALL of this automatically:
BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

// Producer:
queue.put("item");  // BLOCKS if queue is full, resumes when space available

// Consumer:
String item = queue.take();  // BLOCKS if queue is empty, resumes when item available
```

### Types of BlockingQueue

```java
// 1. ArrayBlockingQueue — Bounded, array-based
BlockingQueue<String> abq = new ArrayBlockingQueue<>(100);  // Fixed capacity

// 2. LinkedBlockingQueue — Optionally bounded, linked nodes
BlockingQueue<String> lbq = new LinkedBlockingQueue<>();    // Unbounded (Integer.MAX_VALUE)
BlockingQueue<String> lbq2 = new LinkedBlockingQueue<>(100); // Bounded

// 3. PriorityBlockingQueue — Unbounded, priority-based
BlockingQueue<Task> pbq = new PriorityBlockingQueue<>();    // Like PriorityQueue but thread-safe

// 4. SynchronousQueue — No capacity! Hand-off only
BlockingQueue<String> sq = new SynchronousQueue<>();
// put() blocks until take() happens → DIRECT handoff (like Exchanger)

// 5. DelayQueue — Elements can only be taken after their delay expires
BlockingQueue<Delayed> dq = new DelayQueue<>();
```

### Time Complexity

| Operation | ArrayBlockingQueue | LinkedBlockingQueue | PriorityBlockingQueue |
|-----------|-------------------|-------------------|----------------------|
| `put(e)` | **O(1)** | **O(1)** | **O(log n)** |
| `take()` | **O(1)** | **O(1)** | **O(log n)** |
| `offer(e)` | **O(1)** | **O(1)** | **O(log n)** |
| `poll()` | **O(1)** | **O(1)** | **O(log n)** |
| `peek()` | **O(1)** | **O(1)** | **O(1)** |
| `size()` | **O(1)** | **O(1)** | **O(1)** |

**The lock cost**: All operations involve lock acquire/release. Even O(1) operations have overhead of ~50-100ns compared to non-blocking queues.

---

## 6.3 CopyOnWriteArrayList — Read-Optimized Thread-Safe List

### The Core Idea

```java
public class CopyOnWriteArrayList<E> {
    private volatile transient Object[] array;  // The ONLY copy of data
    
    // READ: O(1), never blocked, no locks
    public E get(int index) {
        return (E) array[index];  // Just read from current array — VOLATILE guarantees visibility
    }
    
    // WRITE: O(n) — creates FULL COPY of array
    public boolean add(E e) {
        synchronized (this) {  // Only writes synchronize (reads don't)
            Object[] old = array;
            Object[] newArray = Arrays.copyOf(old, old.length + 1);  // COPY EVERYTHING!
            newArray[old.length] = e;
            array = newArray;   // Atomic assignment → readers see new array instantly
        }
        return true;
    }
}
```

### The Copy-On-Write Trade-off

```
WRITE: Every modification copies the ENTIRE array
  add(e):     O(n) — copy all n elements + add one
  remove(i):  O(n) — copy all elements except one
  set(i, e):  O(n) — copy entire array with one changed value

READ: Never blocked, never locked
  get(i):     O(1) — just read volatile reference
  iterator(): Never throws ConcurrentModificationException!
              Iterator holds reference to array at creation time
              Even if array is replaced later, iterator still sees OLD data
              
MEMORY: For writes, TWO arrays exist simultaneously
  Old array (being read by existing iterators)
  New array (being written)
  → Memory doubles during writes!
```

### When to Use

```java
// ✅ PERFECT: Read-heavy, write-rare workloads
// Example: List of configuration properties refreshed hourly
CopyOnWriteArrayList<String> configKeys = new CopyOnWriteArrayList<>();
configKeys.add("db.url");  // Hourly update
configKeys.add("api.key");

// Hundreds of threads READING simultaneously — NO contention!
String key = configKeys.get(0);  // O(1), no lock

// ❌ TERRIBLE: Write-heavy workloads
// Every add() copies the entire array!
CopyOnWriteArrayList<Integer> bad = new CopyOnWriteArrayList<>();
for (int i = 0; i < 10000; i++) {
    bad.add(i);  // Each add copies growing array → O(1+2+3+...+10000) = O(n²)!
}
```

### Time Complexity

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| `get(i)` | **O(1)** | Volatile read — no lock |
| `set(i, e)` | **O(n)** | Copy entire array (synchronized) |
| `add(e)` | **O(n)** | Copy entire array + 1 (synchronized) |
| `add(i, e)` | **O(n)** | Copy with shift (synchronized) |
| `remove(i)` | **O(n)** | Copy all except one (synchronized) |
| `remove(o)` | **O(n)** | Find O(n) + copy O(n) (synchronized) |
| `contains(o)` | **O(n)** | Linear scan — no lock |
| `indexOf(o)` | **O(n)** | Linear scan — no lock |
| `size()` | **O(1)** | Return array.length |
| `iterator()` | **O(1)** | Returns snapshot iterator (never fails) |
| `iteration` | **O(n)** | Uses snapshot array |

---

# PART 7: SPECIAL COLLECTIONS

## 7.1 WeakHashMap — Keys That Can Be Garbage Collected

### The Problem: Memory Leaks in Caches

```java
// Normal HashMap: Key holds strong reference → NEVER garbage collected
Map<String, Data> cache = new HashMap<>();
String key = new String("config");  // Strong reference
cache.put(key, loadData());

key = null;  // WE lost our reference...
// But HashMap INTERNALLY still holds reference to "config" key!
// → Cache entry NEVER removed! MEMORY LEAK!
```

### How WeakHashMap Solves This

```java
// WeakHashMap: Keys are WEAK references
Map<String, Data> cache = new WeakHashMap<>();
String key = new String("config");
cache.put(key, loadData());

key = null;  // Only WEAK reference remains in WeakHashMap
// Next GC: key is collected → entry is automatically removed!
// → No memory leak!
```

### When to Use

```java
// 1. Caching metadata that can be recreated
// 2. Canonical mappings (store once, reuse)
// 3. Avoiding memory leaks in long-running apps

// Real example: ClassLoader + static cache (prevent ClassLoader leak)
```

### Time Complexity

Same as HashMap: O(1) average, O(log n) worst (Java 8+).

**BUT**: After each operation, WeakHashMap processes the ReferenceQueue. This adds small overhead.

---

## 7.2 EnumMap — Ultra-Fast Map for Enum Keys

### Why It's Special

```java
enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

// EnumMap: Uses plain array, NOT hashCode!
EnumMap<Day, String> map = new EnumMap<>(Day.class);
map.put(Day.MONDAY, "Meeting");
map.put(Day.FRIDAY, "Party");

// Internal: array[ordinal] = value
// Day.MONDAY.ordinal() = 0 → array[0] = "Meeting"
// Day.FRIDAY.ordinal() = 4 → array[4] = "Party"

// get(): return array[key.ordinal()] → O(1)
// No hashCode computation! No collision resolution! Just array access!
```

### Time Complexity

| Operation | Complexity | Why |
|-----------|-----------|-----|
| `get(k)` | **O(1)** | `array[key.ordinal()]` — direct array access |
| `put(k, v)` | **O(1)** | `array[key.ordinal()] = value` |
| `remove(k)` | **O(1)** | `array[key.ordinal()] = null` |
| `containsKey(k)` | **O(1)** | `array[key.ordinal()] != null` |
| `containsValue(v)` | **O(n)` | Must scan array (n = number of enum constants) |
| `size()` | **O(1)** | Implemented efficiently using bit masks |

**Memory**: Array of exactly enum constant count. No empty buckets. No Node objects. Extremely compact.

---

## 7.3 IdentityHashMap — Uses == Not equals()

### The Difference

```java
// Normal HashMap: uses equals() for key comparison
Map<String, String> normal = new HashMap<>();
String k1 = new String("key");
String k2 = new String("key");
normal.put(k1, "value1");
normal.put(k2, "value2");  // k1.equals(k2) → true → OVERWRITES!
System.out.println(normal.size());  // 1

// IdentityHashMap: uses == for key comparison
Map<String, String> identity = new IdentityHashMap<>();
identity.put(k1, "value1");
identity.put(k2, "value2");  // k1 == k2 → FALSE → BOTH stored!
System.out.println(identity.size());  // 2
```

### Internal Structure

Uses **linear probing** (not chaining). Array is power of 2. Keys in even indices, values in odd indices.

```
Array: [key1, val1, key2, val2, null, null, null, null]
         index: 0    1    2    3    4    5    6    7

If collision: try next slot (linear probing)
```

### Time Complexity

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| `get(k)` | **O(1)** avg | Linear probing — may scan a few slots |
| `put(k, v)` | **O(1)** avg | Linear probing |
| `remove(k)` | **O(1)** avg | Remove + may need to rehash cluster |
| `contains(k)` | **O(1)** avg | Linear probing |
| `size()` | **O(1)** | |

**Worst case**: Many collisions → O(n) (all keys probe full array)

---

# PART 8: LEGACY (AVOID)

## 8.1 Stack — Why NOT to Use

```java
// ❌ Legacy: Stack extends Vector (both synchronized)
Stack<String> stack = new Stack<>();
stack.push("A");
stack.push("B");
String top = stack.pop();  // "B"

// PROBLEMS:
// 1. Extends Vector → ALL methods are synchronized (slow!)
// 2. Extends Vector → inherits Vector's array (can access by index!)
//    stack.get(0);  // Breaks stack abstraction! Can access middle of stack!
// 3. Legacy code, no generics support originally

// ✅ MODERN: Use ArrayDeque
Deque<String> stack = new ArrayDeque<>();
stack.push("A");
stack.push("B");
String top = stack.pop();  // "B" — same behavior, faster, cleaner
```

## 8.2 Vector — Why NOT to Use

```java
// ❌ Legacy: Like ArrayList but ALL methods synchronized
Vector<String> vector = new Vector<>();
vector.add("A");
vector.get(0);

// PROBLEMS:
// 1. Every method is synchronized → thread-safe overhead even in single-thread
// 2. Synchronization is NAIVE (locks entire instance)
// 3. Legacy code, Iterator is fail-fast not fail-safe

// ✅ MODERN: Use ArrayList (single-thread) or CopyOnWriteArrayList (thread-safe)
List<String> list = new ArrayList<>();  // Single-thread: faster
```

## 8.3 Hashtable — Why NOT to Use

```java
// ❌ Legacy: Like HashMap but ALL methods synchronized
Hashtable<String, String> table = new Hashtable<>();
table.put("A", "1");
table.get("A");

// PROBLEMS:
// 1. Every method synchronized → slow
// 2. No null keys/values (NPE)
// 3. Legacy enumeration (not Iterator)
// 4. Initial capacity 11 (not power of 2) → no bitwise optimization

// ✅ MODERN: Use HashMap (single-thread) or ConcurrentHashMap (multi-thread)
Map<String, String> map = new HashMap<>();           // Single-thread
Map<String, String> safe = new ConcurrentHashMap<>(); // Multi-thread
```

---

# PART 9: REFERENCE

## 9.1 Complete Time Complexity Table (VERIFIED)

### Lists

| Operation | ArrayList | LinkedList |
|-----------|-----------|------------|
| `get(i)` | **O(1)** | **O(n)** |
| `set(i, e)` | **O(1)** | **O(n)** |
| `add(e)` (end) | **O(1)*** | **O(1)** |
| `add(i, e)` | **O(n)** | **O(n)** |
| `addFirst(e)` | **O(n)** | **O(1)** |
| `remove(i)` | **O(n)** | **O(n)** |
| `removeFirst()` | **O(n)** | **O(1)** |
| `removeLast()` | **O(1)** | **O(1)** |
| `contains(o)` | **O(n)** | **O(n)** |
| `indexOf(o)` | **O(n)** | **O(n)** |
| `size()` | **O(1)** | **O(1)** |
| `clear()` | **O(n)** | **O(n)** |
| `iteration` | **O(n)** | **O(n)** |
| `subList()` | **O(1)** | **O(1)** |
| Memory per elem | **4 bytes** | **24+ bytes** |

* = amortized O(1)

### Maps (CRITICAL: Know the Distinctions!)

| Operation | HashMap Best | HashMap Average | HashMap Worst (Java 7) | HashMap Worst (Java 8+) | LinkedHashMap | TreeMap | ConcurrentHashMap |
|-----------|-------------|-----------------|----------------------|----------------------|---------------|---------|-------------------|
| `put(k, v)` | O(1) | O(1) | **O(n)** | **O(log n)** | O(1) | **O(log n)** | O(1) |
| `get(k)` | O(1) | O(1) | **O(n)** | **O(log n)** | O(1) | **O(log n)** | O(1) |
| `remove(k)` | O(1) | O(1) | **O(n)** | **O(log n)** | O(1) | **O(log n)** | O(1) |
| `containsKey(k)` | O(1) | O(1) | O(n) | O(log n) | O(1) | O(log n) | O(1) |
| `containsValue(v)` | O(n) | O(n) | O(n) | O(n) | O(n) | O(n) | O(n) |
| `size()` | O(1) | O(1) | O(1) | O(1) | O(1) | O(1) | O(1) |
| `iteration` | O(c+n) | O(c+n) | O(c+n) | O(c+n) | **O(n)** | **O(n)** | O(c+n) |

**Notes**:
- HashMap worst case Java 8+: O(log n) **only if collision chain becomes tree** (≥8 collisions)
- HashMap worst case Java 7: O(n) **always** (only linked list for collisions)
- HashMap iteration: Must scan capacity c (including empty buckets) + size n entries
- LinkedHashMap iteration: O(n) — follows linked chain of entries only (no empty bucket scanning)
- TreeMap: All operations are O(log n) because it's a Red-Black tree (no O(1) for anything)

### Sets

| Operation | HashSet | TreeSet |
|-----------|---------|---------|
| `add(e)` | O(1) avg, O(log n) worst (8+) | **O(log n)** |
| `remove(e)` | O(1) avg, O(log n) worst (8+) | **O(log n)** |
| `contains(e)` | O(1) avg, O(log n) worst (8+) | **O(log n)** |
| `first()` | — | **O(log n)** |
| `last()` | — | **O(log n)** |
| `ceiling(e)/floor(e)` | — | **O(log n)** |
| `subSet()` | — | O(1) view |
| `size()` | O(1) | O(1) |
| `iteration` | O(c+n) | **O(n)** |

### Queues/Deques

| Operation | PriorityQueue | ArrayDeque | ArrayBlockingQueue | LinkedBlockingQueue |
|-----------|--------------|------------|-------------------|---------------------|
| `offer(e)` | **O(log n)** | **O(1)** | O(1) | O(1) |
| `poll()` | **O(log n)** | **O(1)** | O(1) | O(1) |
| `peek()` | **O(1)** | **O(1)** | O(1) | O(1) |
| `add(e)` | **O(log n)** | **O(1)** | O(1) | O(1) |
| `remove(o)` | **O(n)** | O(n) | O(n) | O(n) |
| `contains(o)` | **O(n)** | O(n) | O(n) | O(n) |
| `size()` | O(1) | O(1) | O(1) | O(1) |

---

## 9.2 Summary Cheat Sheet

```
Data Structure Choice Guide:

FAST RANDOM ACCESS (by index):
  → ArrayList (O(1) get/set)

FAST ADD/REMOVE AT ENDS:
  → ArrayDeque (O(1) both ends, queue/stack/deque)
  → LinkedList (O(1) both ends, but memory-heavy)

FAST LOOKUP BY KEY (unique keys):
  → HashMap (O(1) avg, O(log n) worst in Java 8+)
  → ConcurrentHashMap (thread-safe)

MAINTAIN SORTED ORDER (unique elements):
  → TreeSet / TreeMap (O(log n) all operations)

MAINTAIN INSERTION ORDER:
  → LinkedHashMap / LinkedHashSet (O(1) + iteration order)

THREAD-SAFE:
  Reads only:            CopyOnWriteArrayList
  Reads + writes:        ConcurrentHashMap, BlockingQueue
  Rare writes, many reads: CopyOnWriteArrayList

PRIORITY-BASED PROCESSING:
  → PriorityQueue (O(log n) offer/poll, O(1) peek)

NEVER USE:
  → Stack (use ArrayDeque)
  → Vector (use ArrayList or CopyOnWriteArrayList)
  → Hashtable (use HashMap or ConcurrentHashMap)
```

---

## 9.3 7+ Years Interview Questions (With Complete Answers)

### Q1: "What is the time complexity of HashMap get() and put()?"

**❌ WRONG ANSWER THAT GETS YOU REJECTED**: "O(1)"

**✅ CORRECT ANSWER**:
"It depends on the Java version and hash distribution:

**Best case** (perfect hash): O(1) — each key goes to its own bucket.

**Average case** (reasonable hashCode): O(1) — most buckets have 0-3 elements.

**Worst case** (all keys same hashCode):
- **Java 7**: O(n) — all keys in one bucket as a linked list. Search scans entire list.
- **Java 8+**: O(log n) — after 8 collisions, the linked list TREEIFIES into a Red-Black tree.

**Additionally**:
- The hash function `h ^ (h >>> 16)` mixes high bits into low bits to improve distribution.
- Resize is O(n) but happens infrequently (amortized).
- Bad hashCode() implementation can make the hash computation itself expensive regardless of O(1) bucket access."

### Q2: "Why does ConcurrentHashMap not allow null keys while HashMap does?"

**The short answer**: Ambiguity. If `map.get(key)` returns null, did the key not exist, or was the value null?

**For HashMap**: You can use `containsKey()` to disambiguate. This works because HashMap is single-threaded.

**For ConcurrentHashMap**: Between `containsKey()` and `get()`, another thread could modify the map. So null is ambiguous. Rather than dealing with this, Doug Lea (author) decided to simply forbid nulls.

```java
ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
map.put("A", null);  // ❌ NullPointerException!

// Why? Because:
// if (map.get("A") == null) {
//     // Did "A" not exist? Or value was null?
//     // Between get() and this check, another thread put("A", "X")
//     // → AMBIGUOUS!
// }
```

### Q3: "What happens when two threads put into a HashMap simultaneously?"

**Java 7**: **Infinite loop during resize!**
```java
// During resize, HashMap rehashes entries to new table
// If two threads resize simultaneously:
// Thread 1: Reads Entry A → Entry B (circular list forming)
// Thread 2: Writes Entry A.next = Entry B, Entry B.next = Entry A
// → CIRCULAR LINKED LIST!
// → get()/put() loop forever → CPU 100% → Application hangs
```

**Java 8+**: No infinite loop (fixed), but still data corruption:
- Lost entries (one put overwrites another)
- Wrong size
- Null values appearing where they shouldn't

### Q4: "Design a thread-safe counter that can be used as a value in ConcurrentHashMap. How do you increment atomically?"

**Solution**: Use `compute()` or `merge()` or `LongAdder`:

```java
ConcurrentHashMap<String, LongAdder> counts = new ConcurrentHashMap<>();

// ✅ CORRECT: Atomic increment
counts.computeIfAbsent("key", k -> new LongAdder()).increment();

// OR with merge:
counts.merge("key", 1L, Long::sum);

// ❌ WRONG: Race condition!
LongAdder counter = counts.get("key");
if (counter == null) {
    counter = new LongAdder();
    counts.put("key", counter);  // Not atomic! Two threads can both put!
}
counter.increment();
```

### Q5: "Your PriorityQueue iterator prints elements in unexpected order. Why?"

```java
PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 3, 8, 2));
for (int n : pq) System.out.println(n);  // [1, 2, 3, 8, 5] — NOT sorted!

// Why? PriorityQueue stores elements as a binary heap ARRAY
// The array has heap property (parent ≤ children) but NOT full sorted order
// heap array: [1, 2, 3, 8, 5]
//              ↑ parent  |  children: 2,3 are ≤ 8,5 individually
//              But 5 (right child of 2) is > 2? Yes, that's fine for heap

// Only poll() returns in sorted order because it removes the root and re-heapifies:
while (!pq.isEmpty()) {
    System.out.println(pq.poll());  // 1, 2, 3, 5, 8 — CORRECT
}
```

### Q6: "ArrayList vs LinkedList — which is faster for iteration?"

```java
// 1,000,000 elements, full iteration:
ArrayList<Integer> arrayList = new ArrayList<>();
LinkedList<Integer> linkedList = new LinkedList<>();
// ... fill both ...

// ArrayList iteration: ~5ms
// LinkedList iteration: ~25ms
// ArrayList is 5x FASTER because:
// 1. Contiguous memory → CPU cache prefetches next elements
// 2. No pointer chasing (don't need to dereference next/prev)
// 3. No null checks on each node

// But what about get(i) in a for loop?
// ArrayList: for(int i=0; i<size; i++) list.get(i) → O(1) each → O(n) total
// LinkedList: for(int i=0; i<size; i++) list.get(i) → O(n) each → O(n²) total!
// LinkedList get(i) in loop = 1,000,000,000,000 operations vs ArrayList's 1,000,000
```

### Q7: "Explain the Java 8 HashMap improvements. Why treeify at 8?"

```java
// Java 7 HashMap problems:
// 1. Worst case O(n) for get() (all keys in same bucket)
// 2. Head insertion during resize → circular linked list (infinite loop)
// 3. No hash spreading (directly used hashCode without mixing)

// Java 8 HashMap fixes:
// 1. Treeify: After 8 collisions, linked list → Red-Black tree
//    Worst case becomes O(log n) instead of O(n)
// 2. Tail insertion: New nodes added at END of list (no circular list)
// 3. Better hash: h ^ (h >>> 16) — mixes high bits
// 4. Resize optimization: (hash & oldCap) == 0 → stay; != 0 → move

// Why threshold = 8?
// Poisson distribution analysis:
// With good hash, probability of 8 collisions in same bucket ≈ 0.0000001
// (1 in 10 million)
// So treeification is a SAFETY NET for bad hashCode, not normal operation

// Why 6 for untreeify?
// Hysteresis: If tree shrinks to 6, convert back to list
// Prevents constant oscillation: add 1 → tree, remove 1 → list, add 1 → tree...
```

---

## 🎯 FINAL: The Answer Template for HashMap Time Complexity

When an interviewer asks "What's the time complexity of HashMap?", say THIS:

> "HashMap get() and put() are **O(1) on average** with a good hash function. However, the COMPLETE answer has nuances:
>
> **Best case: O(1)** — perfect hash distribution, each key maps to a unique bucket.
>
> **Average case: O(1)** — with a well-distributed hashCode, most buckets contain 0-3 entries.
>
> **Worst case (Java 7): O(n)** — if all keys have the same hashCode, they all land in ONE bucket as a linked list, and get() must scan the entire list.
>
> **Worst case (Java 8+): O(log n)** — Java 8 improved this. After 8 collisions in the same bucket, the linked list converts to a Red-Black tree, so even in the worst case, get() is O(log n).
>
> **Additionally**: 
> - The resize operation is O(n) when it happens (rehashing all entries), but it's amortized over many adds.
> - The hashCode() itself is called once per key and cached in the Node object.
> - If hashCode() is expensive (e.g., hashing a large object), that cost dominates even though bucket access is O(1).
>
> **In short**: HashMap is O(1) average but can degrade to O(log n) in Java 8+ or O(n) in Java 7 with poor hash distribution."

**That** is the answer that gets you the job. ✅