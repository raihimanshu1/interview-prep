lets # Legacy Collections (AVOID) — Hashtable, Stack, Vector

## 1. The Problem: Collections That Time Forgot

These are the **old, synchronized collections** from Java 1.0/1.1. They were created before the modern Collections Framework (Java 2, 1998) and have been **superseded by better alternatives**.

| Legacy Class | Modern Replacement | Why Replace? |
|-------------|-------------------|--------------|
| **Hashtable** | `ConcurrentHashMap`, `HashMap` | Full synchronization = slow |
| **Vector** | `ArrayList` | Full synchronization = slow |
| **Stack** | `ArrayDeque` | Extends Vector = inherits bad design |

**The core problem with ALL three:** They synchronize **every single method**.

```java
// Hashtable — every method is synchronized!
public synchronized V put(K key, V value) { ... }
public synchronized V get(Object key) { ... }
public synchronized int size() { ... }

// Vector — every method is synchronized!
public synchronized boolean add(E e) { ... }
public synchronized E get(int index) { ... }
public synchronized boolean remove(Object o) { ... }
```

**Why this is bad:**
- **Unnecessary overhead in single-threaded code**: Taking a lock when no other thread exists
- **False sense of thread-safety**: Compound operations (check-then-act) still need external synchronization
- **Legacy design**: Before Java concurrency utilities (java.util.concurrent) existed

---

# HASHTABLE

## 2. The Problem Before Hashtable

Before Hashtable, there was no way to store key-value pairs that worked across threads. But the solution was too aggressive.

## 3. What is Hashtable?

```java
Hashtable<String, Integer> table = new Hashtable<>();
table.put("A", 1);
table.put("B", 2);
Integer val = table.get("A");  // 1
```

**Hashtable = A synchronized HashMap from Java 1.0.**

Internal structure (similar to old HashMap):
```
Hashtable (capacity=11, loadFactor=0.75)
┌─────┬───────────────────────┐
│  0  │ Entry{key="A", value=1} → null
│  1  │ null                   │
│ ... │                        │
│ 10  │ Entry{key="B", value=2} → null
└─────┴───────────────────────┘

Uses ENTRY array (not Node like HashMap).
Does NOT support Red-Black trees.
Uses MODULO for index (not bitwise AND).
```

## 4. Hashtable vs HashMap vs ConcurrentHashMap

| Aspect | **Hashtable** | **HashMap** | **ConcurrentHashMap** |
|--------|-------------|------------|----------------------|
| **Thread-safe** | ✅ Yes (full sync) | ❌ No | ✅ Yes (per-bucket) |
| **Null keys** | ❌ No | ✅ Yes (one) | ❌ No |
| **Null values** | ❌ No | ✅ Yes | ❌ No |
| **Performance (single-thread)** | **Slow** (sync overhead) | **Fast** | ~5-10% slower than HashMap |
| **Performance (multi-thread)** | **Terrible** (one lock) | Broken | **Excellent** (per-bucket) |
| **Initial capacity** | 11 | 16 | 16 |
| **Index calculation** | `hash % capacity` (modulo) | `hash & (n-1)` (bitwise) | `hash & (n-1)` (bitwise) |
| **Collision handling** | Linked list only | List → Tree (Java 8+) | List → Tree |
| **Iteration** | Fail-fast | Fail-fast | Fail-safe |
| **Enumeration** | ✅ Yes (legacy) | ❌ No | ❌ No |
| **Introduced** | **Java 1.0** (1996) | **Java 1.2** (1998) | **Java 5** (2004) |

## 5. Why Hashtable is BAD

### Reason 1: Full Synchronization = Slow

```java
// Hashtable locks the ENTIRE table for every read
Hashtable<String, Integer> table = new Hashtable<>();
table.get("A");  // Locks entire table — even for a read!
table.put("B", 2);  // Locks entire table — blocks all other threads

// ConcurrentHashMap doesn't lock reads at all:
ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
chm.get("A");  // Lock-free! No threads blocked.
```

### Reason 2: No Null Keys or Values

```java
Hashtable<String, Integer> table = new Hashtable<>();
table.put("A", null);  // NullPointerException!
table.put(null, 1);    // NullPointerException!
```

### Reason 3: Legacy Hashing (Modulo)

```java
// Hashtable uses modulo for index:
int index = (key.hashCode() & 0x7FFFFFFF) % capacity;

// HashMap uses bitwise AND (much faster):
int index = (n - 1) & hash;
// Only works because capacity is power of 2.
```

### Reason 4: No Tree Optimization

Hashtable uses **only linked lists** for collisions — worst case O(n). No Red-Black tree optimization like Java 8+ HashMap/ConcurrentHashMap.

## 6. Hashtable: When You MIGHT Still See It

```java
// 1. Legacy code — don't touch if it works
// 2. Some old Properties class uses Hashtable internally
Properties props = new Properties();
props.setProperty("key", "value");

// 3. Old serialized objects — Hashtable serialization format differs from HashMap

// 4. NEVER use in new code. Period.
```

---

# VECTOR

## 7. What is Vector?

```java
Vector<String> vector = new Vector<>();
vector.add("A");
vector.add("B");
String item = vector.get(1);  // "B"

// It's basically a synchronized ArrayList
```

**Vector = A synchronized ArrayList from Java 1.0.**

## 8. Vector vs ArrayList

| Aspect | **Vector** | **ArrayList** |
|--------|-----------|---------------|
| **Thread-safe** | ✅ Yes (full sync) | ❌ No |
| **Growth** | **Double** (2x) | **1.5x** |
| **Iterator** | Fail-fast + **Enumeration** (legacy) | Fail-fast only |
| **Initial capacity** | 10 | 10 |
| **Performance** | Slow (synchronized) | Fast |
| **Introduced** | Java 1.0 (1996) | Java 1.2 (1998) |

## 9. Why Vector is BAD

### Reason 1: Synchronized Methods Even When Not Needed

```java
Vector<String> vector = new Vector<>();
// Why lock when NO OTHER THREAD exists?
// Every add() acquires and releases a lock = wasted CPU cycles!

// ArrayList doesn't synchronize anything:
ArrayList<String> list = new ArrayList<>();  // Faster
```

### Reason 2: Double Growth is Wasteful

```java
// Vector: grows 2x when full
// Capacity: 10 → 20 → 40 → 80 → 160...
// At peak: up to 50% of the array is unused!

// ArrayList: grows 1.5x when full
// Capacity: 10 → 15 → 22 → 33 → 49...
// At peak: only ~33% unused
```

### Reason 3: Legacy Enumeration Interface

```java
Vector<String> vector = new Vector<>();

// Old way — DON'T use:
Enumeration<String> e = vector.elements();
while (e.hasMoreElements()) {
    System.out.println(e.nextElement());
}

// Modern way:
for (String s : vector) {
    System.out.println(s);
}
```

### Reason 4: False Thread-Safety

```java
Vector<Integer> vector = new Vector<>();

// ❌ Even though individual methods are synchronized:
if (vector.isEmpty()) {          // Thread-safe alone
    vector.add(1);                // Thread-safe alone
}
// But BETWEEN isEmpty() and add(), another thread can add!
// → Still need external synchronization!

synchronized (vector) {
    if (vector.isEmpty()) {
        vector.add(1);  // NOW it's thread-safe
    }
}
```

---

# STACK

## 10. What is Stack?

```java
Stack<String> stack = new Stack<>();
stack.push("A");
stack.push("B");
String top = stack.pop();  // "B"
```

**Stack = A LIFO stack that EXTENDS Vector (from Java 1.0).**

This is the **worst design problem**: Stack inherits ALL Vector methods, including ones that break LIFO semantics.

## 11. Why Stack is BAD

### Reason 1: Inherits List Operations (Breaks LIFO!)

```java
Stack<String> stack = new Stack<>();
stack.push("A");
stack.push("B");
stack.push("C");

// Stack inherits Vector methods — can access by index!
stack.get(0);        // "A" — breaks LIFO!
stack.remove(1);     // Removes "B" from middle — not LIFO!
stack.insertElementAt("X", 0);  // Insert at bottom!

// A Stack should only allow push/pop/peek.
// But Stack allows ANY List operation because it extends Vector.
```

### Reason 2: Synchronized = Slow

```java
Stack<String> stack = new Stack<>();
stack.push("A");  // Synchronized lock — overhead even single-threaded
stack.pop();      // Synchronized lock
```

### Reason 3: Extends Vector = Inherits All Vector Problems

Stack inherits Vector's:
- Synchronized methods (slow)
- Double growth (wasteful)
- Enumeration interface (legacy)
- Capacity methods (`capacity()`, `ensureCapacity()`) that have nothing to do with stacks

---

## 12. The Modern Replacement: ArrayDeque

```java
// ❌ Legacy — DON'T USE
Stack<String> oldStack = new Stack<>();
Hashtable<String, String> oldTable = new Hashtable<>();
Vector<String> oldVector = new Vector<>();

// ✅ Modern equivalents
Deque<String> stack = new ArrayDeque<>();         // Stack replacement
Map<String, String> map = new ConcurrentHashMap<>();  // Hashtable replacement
List<String> list = new ArrayList<>();             // Vector replacement
```

### Performance Comparison

```
Operation            Hashtable    HashMap    ConcurrentHashMap
───────────────────  ─────────    ───────    ─────────────────
put (1 thread)       150 ops/ms   250 ops/ms   220 ops/ms
get (1 thread)       200 ops/ms   350 ops/ms   300 ops/ms
put (8 threads)       30 ops/ms   BROKEN      200 ops/ms
get (8 threads)       40 ops/ms   BROKEN      350 ops/ms
```

### Why People Used Them (Historical Context)

```java
// Java 1.0 (1996) — Only Hashtable, Vector, Stack existed
// No HashMap, ArrayList, ConcurrentHashMap, ArrayDeque

// Java 1.2 (1998) — Added Collections Framework
// HashMap, ArrayList, LinkedList introduced
// Hashtable, Vector, Stack retrofitted to implement Map/List interfaces

// Java 5 (2004) — Added java.util.concurrent
// ConcurrentHashMap introduced — proper thread-safe map

// Today — NEVER use Hashtable, Vector, Stack in NEW code
```

---

## 13. Interview Quick Reference

**Q: Why is Hashtable considered legacy?**
A: It synchronizes every method, causing unnecessary overhead. ConcurrentHashMap provides much better concurrency with per-bucket locking and lock-free reads. HashMap is better for single-threaded use.

**Q: What's the difference between Hashtable and HashMap?**
A: Hashtable: synchronized, no null keys/values, modulo indexing, 11 initial capacity, linked-list only collisions, legacy (Java 1.0). HashMap: not synchronized, allows null key, bitwise indexing, 16 initial capacity, tree optimization at 8+ collisions (Java 8+), modern (Java 1.2+).

**Q: Why shouldn't you use Stack class?**
A: Stack extends Vector, which means it inherits all List operations (get, remove, insert by index). This breaks LIFO semantics. Use ArrayDeque instead.

**Q: What's the difference between Vector and ArrayList?**
A: Vector synchronizes all methods (slow), doubles capacity on resize. ArrayList is unsynchronized (fast), grows by 1.5x. Use ArrayList for single-threaded code, CopyOnWriteArrayList for concurrent reads, or Collections.synchronizedList().

---

## 14. 30-Second Summary

```
LEGACY COLLECTIONS — AVOID IN NEW CODE

❌ Hashtable → Use HashMap (single-thread) or ConcurrentHashMap (multi-thread)
   Problems: Full sync, no nulls, modulo indexing, no tree optimization

❌ Vector → Use ArrayList
   Problems: Full sync, 2x growth (wasteful), legacy Enumerations

❌ Stack → Use ArrayDeque
   Problems: Extends Vector (breaks LIFO), full sync, legacy

Rule of thumb:
  - If you find these in OLD code → leave them (if it ain't broke)
  - If you find these in NEW code → refactor to modern equivalents
  - NEVER use them in code you're writing today