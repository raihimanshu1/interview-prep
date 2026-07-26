# HashMap — Why? What? How? When?

## 1. The Problem Before HashMap

### The Old Way: Arrays and Lists

Before HashMap, if you needed to find a value by a key, you had to scan:

```java
// Finding a person by ID using a List — SLOW!
record Person(int id, String name) {}

List<Person> people = List.of(
    new Person(1001, "Alice"),
    new Person(1002, "Bob"),
    new Person(1003, "Charlie")
);

// Find person with ID 1002 — O(n) scan!
Person found = null;
for (Person p : people) {
    if (p.id() == 1002) {  // Must check EVERY element
        found = p;
        break;
    }
}
```

**Problems with searching arrays/lists:**
- **O(n) time**: To find something, you must check every element
- **No key-value concept**: You store values but have no "key" to look them up directly
- **Manual indexing**: If you want fast access, you must build your own index (another array)

### What developers did before HashMap:

```java
// Manual "index" approach — tedious and limited
Person[] indexById = new Person[10000];
for (Person p : people) {
    indexById[p.id()] = p;  // Works only for integer keys
}

// But what about string keys? Need a conversion...
// What about sparse IDs? Waste of space.
```

**Problems this approach:**
- **Only integer keys**: Can't use strings, objects
- **Wasteful for sparse data**: Array of 10,000 slots for 3 people
- **No collision handling**: What if two IDs map to same index?
- **No resizing**: Fixed array size

> **HashMap was created to solve all of this**: O(1) get/put by any object key, automatic resizing, collision handling, and memory-efficient storage.

---

## 2. What is HashMap? (Simple Explanation)

```java
HashMap<String, Person> map = new HashMap<>();
map.put("A1001", new Person(1001, "Alice"));  // Key = "A1001", Value = Alice
map.put("A1002", new Person(1002, "Bob"));
map.put("A1003", new Person(1003, "Charlie"));

Person p = map.get("A1002");  // O(1) — instant! Returns Bob
```

**HashMap = A dictionary that maps keys to values, with O(1) lookup.**

Internal picture (simplified):
```
HashMap (size=3, capacity=16)
┌─────┬──────────────────────┐
│  0  │ null                  │
│  1  │ [hash=1, key="A1002"] → [hash=1, key="A1001"...] ← collision chain
│  2  │ null                  │
│  3  │ [hash=3, key="A1003"]
│ ... │                       │
│ 15  │ null                  │
└─────┴──────────────────────┘

Each bucket is either:
- null (empty)
- A linked list node (1-7 elements)
- A Red-Black tree root (8+ elements, Java 8+)
```

**Key insight**: HashMap uses an array of "buckets." A hash function converts the key into an array index. If two keys go to the same bucket (collision), they're stored as a linked list or tree.

---

## 3. Why Does HashMap Implement These Interfaces?

### Why `Serializable`?

```java
public class HashMap<K,V> ... implements java.io.Serializable { ... }
```

**Problem**: You have a cache or lookup table in memory. You want to save it, send it over the network, or restore it later.

**Without Serializable**: You'd have to manually iterate all entries, serialize key-value pairs, handle types, and rebuild the map on deserialization. For a HashMap, you'd also need to ensure the correct initial capacity and load factor are preserved.

**What Serializable does**: Allows converting the entire HashMap (all entries, resizing metadata, thresholds) to/from a byte stream in one operation.

```java
// Serialize entire map
try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("map.data"))) {
    oos.writeObject(map);  // Writes all entries
}

// Deserialize back — restored with correct capacity
try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("map.data"))) {
    HashMap<String, Person> restored = (HashMap<String, Person>) ois.readObject();
}
```

### Why `Cloneable`?

```java
public class HashMap<K,V> ... implements Cloneable { ... }
```

**Problem**: You need a separate copy of a HashMap. You want to modify the copy without affecting the original.

```java
HashMap<String, Integer> original = new HashMap<>();
original.put("A", 1);
original.put("B", 2);

// Without clone: manual copy every entry
HashMap<String, Integer> copy1 = new HashMap<>();
for (Map.Entry<String, Integer> e : original.entrySet()) {
    copy1.put(e.getKey(), e.getValue());  // Tedious
}

// With clone: one call
HashMap<String, Integer> copy2 = (HashMap<String, Integer>) original.clone();
```

**Important: shallow copy!**
```java
original.clone() creates a NEW HashMap with a NEW bucket array
But the KEY and VALUE objects are SHARED (not copied).

original:  "A" → StringBuilder("hello")
clone:     "A" → SAME StringBuilder("hello")

clone.get("A").append(" world");
System.out.println(original.get("A"));  // "hello world" — SAME object modified!
```

### Why `AbstractMap`?

```java
public class HashMap<K,V> extends AbstractMap<K,V> { ... }
```

**AbstractMap provides default implementations** for:
- `toString()` — `{key1=value1, key2=value2}`
- `isEmpty()` — `size() == 0`
- `containsValue()` — iterates entries checking equals
- `equals()` and `hashCode()` — for comparing maps

HashMap overrides most of these for efficiency, but the abstract class provides a safety net for any methods not explicitly overridden.

---

## 4. How HashMap Works Internally (Basic Implementation)

### The Core Idea

HashMap works in 3 steps:

```
Step 1:  key.hashCode() → 32-bit integer (e.g., -2013148756)
Step 2:  hash → spread (XOR high bits into low bits)
Step 3:  index = (table.length - 1) & hash → bucket position (e.g., 5)
```

### Simplified HashMap:

```java
public class SimpleHashMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    
    private Node<K, V>[] table;  // Array of buckets
    private int size = 0;
    
    @SuppressWarnings("unchecked")
    public SimpleHashMap() {
        table = new Node[DEFAULT_CAPACITY];
    }
    
    // A node in the linked list (or tree)
    static class Node<K, V> {
        final int hash;    // Cached hash code
        final K key;       // The key
        V value;           // The value
        Node<K, V> next;   // Next node in case of collision
        
        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    
    // Put a key-value pair — O(1) average
    public V put(K key, V value) {
        if (key == null) return putForNullKey(value);
        
        int hash = hash(key.hashCode());  // Step 1: Spread the hash
        int index = indexFor(hash, table.length);  // Step 2: Find bucket
        
        // Step 3: Check if key already exists
        for (Node<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && (e.key == key || key.equals(e.key))) {
                V oldValue = e.value;
                e.value = value;  // Replace value
                return oldValue;
            }
        }
        
        // Key doesn't exist — add new node
        addEntry(hash, key, value, index);
        return null;
    }
    
    // Get a value by key — O(1) average
    public V get(Object key) {
        if (key == null) return getForNullKey();
        
        int hash = hash(key.hashCode());
        int index = indexFor(hash, table.length);
        
        // Traverse the bucket's linked list
        for (Node<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && (e.key == key || key.equals(e.key))) {
                return e.value;  // Found!
            }
        }
        return null;  // Not found
    }
    
    // Spread hash bits: XOR high 16 bits into low 16 bits
    private int hash(int h) {
        h ^= (h >>> 16);
        return h;
    }
    
    // Convert hash to bucket index: uses AND instead of modulo
    private int indexFor(int h, int length) {
        return h & (length - 1);  // Works because length is power of 2
    }
    
    // Add new entry, resize if needed
    private void addEntry(int hash, K key, V value, int bucketIndex) {
        Node<K, V> e = table[bucketIndex];
        table[bucketIndex] = new Node<>(hash, key, value, e);  // Add at front
        
        if (size++ >= threshold()) {
            resize();  // Double table size, rehash all entries
        }
    }
    
    // Grow the table when too full
    private void resize() {
        int newCapacity = table.length * 2;
        @SuppressWarnings("unchecked")
        Node<K, V>[] newTable = new Node[newCapacity];
        
        // Rehash every entry into the new table
        for (Node<K, V> e : table) {
            if (e != null) {
                // Each linked list needs to be split between old and new buckets
                // ...
            }
        }
        table = newTable;
    }
    
    private int threshold() {
        return (int) (table.length * LOAD_FACTOR);
    }
    
    // Handle null key (stored separately in table[0])
    private V putForNullKey(V value) { ... }
    private V getForNullKey() { ... }
}
```

### Step-by-step execution:

```java
SimpleHashMap<String, String> map = new SimpleHashMap<>();

map.put("cat", "meow");
// Step 1: "cat".hashCode() = 123456 (example)
// Step 2: hash(123456) = spread bits
// Step 3: index = hash & 15 = 3
// Step 4: table[3] is null → create Node at table[3]
// table: [null, null, null, Node{hash=..., key="cat", value="meow"}, null, ...]

map.put("dog", "bark");
// Step 1: "dog".hashCode() = 789012
// Step 2: hash() spread
// Step 3: index = hash & 15 = 7
// Step 4: table[7] is null → create Node at table[7]

map.put("act", "play");
// Step 1: "act".hashCode() = 123456 (SAME as "cat" — collision!)
// Step 2: hash(123456) = same result
// Step 3: index = same as "cat" = 3
// Step 4: table[3] has "cat" → compare keys: "act".equals("cat")? NO
//         → Add "act" to FRONT of linked list
// table[3]: Node{key="act"} → Node{key="cat"} → null

String sound = map.get("cat");
// Step 1: hash("cat") = same as before
// Step 2: index = 3
// Step 3: table[3] = Node{key="act"} → hash matches? YES → equals? NO → next
//         table[3].next = Node{key="cat"} → hash matches? YES → equals? YES → return "meow"!
```

---

## 5. The Secret Sauce: Hash Spreading

### Why `hash() ^ (h >>> 16)`?

```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

**The problem**: `hashCode()` returns a 32-bit value. But bucket index only uses the low few bits (e.g., 4 bits for capacity=16).

```
Without spreading:
  hashCode = 0x1234ABCD → low 4 bits = D (1101)
  hashCode = 0x5678ABCD → low 4 bits = D (1101)  ← SAME bucket!

All keys ending in ABCD collide — bad distribution!
```

**With spreading (XOR high 16 bits into low 16 bits):**
```
  h        = 0x1234ABCD
  h >>> 16 = 0x00001234
  h ^ (h >>> 16) = 0x1234ABCD ^ 0x00001234 = 0x1234B9F9 → low 4 bits = 9 (1001)
  
  Different bucket! Much better distribution.
```

**Visual:**
```
Without XOR:
  hashCode low 4 bits = bucket index
  Problem: Two different objects whose hash codes differ only in high bits 
  (e.g., 0xFF01 and 0x0101) would go to the same bucket!

With XOR:
  High bits influence low bits → better distribution
  Even if hashCodes are similar, they spread across buckets
```

---

## 6. Collision Resolution: Linked List → Red-Black Tree

### Before Java 8: Only Linked Lists

```
Bucket 3: [Node A] → [Node B] → [Node C] → [Node D] → [Node E] → [Node F] → [Node G]
                                                                               ↑
                                                              To find this: 7 steps O(n)
```

**Problem**: If many keys have the same hash (bad hashCode), `get()` becomes O(n).

### Java 8+: Tree at Threshold 8

```java
// When a bucket has ≥ 8 nodes AND capacity ≥ 64:
// Convert linked list → Red-Black Tree

// Tree: O(log n) search instead of O(n)!
```

```
Bucket 3:           TreeNode (root)
                   /       \
              TreeNode    TreeNode
              /     \     /     \
          Node   Node  Node   Node
                                        ↑
                              To find any: O(log n) ~ 3 steps
```

**Why 8 for treeify and 6 for untreeify?**

```
If threshold was 8 for both:
  - Add 9th → treeify
  - Remove 1 → 8 nodes → untreeify back to list
  - Add 1 more → treeify again
  → Oscillation! Costly conversions back and forth.

Solution: 
  treeify → 8  (linked list → tree)
  untreeify → 6 (tree → linked list)  
  Gap of 2 prevents oscillation (hysteresis).
```

**Why 64 minimum capacity for treeify?**
```
If table is small (< 64), resizing doubles capacity and rehashes all entries.
Rehashing distributes keys to new buckets, naturally reducing collisions.
Better to resize than treeify for small tables!
```

---

## 7. Resize: The Expensive Operation

### When does resize happen?

```java
threshold = capacity * loadFactor = 16 * 0.75 = 12

// After 12th put: size (13) > threshold (12) → resize!
// New capacity = 32, new threshold = 32 * 0.75 = 24
```

### The Optimization: No Modulo Needed!

When resizing from capacity 16 to 32:

```
Old method (if we used modulo):
  newIndex = hash % 32  → expensive modulo operation

Reality (bit check optimization):
  if (hash & oldCapacity) == 0 → same index
  if (hash & oldCapacity) != 0 → index + oldCapacity
```

**Example:**
```
Old capacity = 16 (0b10000)
Old index = hash & 15 (low 4 bits)

New capacity = 32 (0b100000)
New index = hash & 31 (low 5 bits)

The 5th bit (bit 4, value 16) determines where the element goes:
  hash & 16 == 0: stays at same index j
  hash & 16 != 0: moves to j + 16
```

```
Old bucket layout (capacity=16):
  Bucket 3: [cat→3, dog→3]  (hash & 15 = 3 for both)

After resize (capacity=32):
  Bucket 3:  [cat→3]   (hash & 16 == 0 → stays)
  Bucket 19: [dog→19]  (hash & 16 != 0 → 3 + 16 = 19)
```

**No modulo! No complex rehashing! Just a bit check!**

---

## 8. HashMap: Advantages, Limitations, When to Use, When Not to Use

### ✅ Advantages

| Advantage | Why |
|-----------|-----|
| **O(1) average get/put/remove** | Direct bucket access via hash |
| **Any object as key** | Uses hashCode() + equals() |
| **Automatic resizing** | Grows when load factor exceeded |
| **Good distribution** | XOR hash spreading + tree at high collisions |
| **Handles collisions** | Linked list → Red-Black tree (Java 8+) |
| **One null key allowed** | Stored separately at table[0] |

### ❌ Limitations

| Limitation | Why |
|------------|-----|
| **No order guarantee** | Iteration order changes with resize |
| **Not thread-safe** | Concurrent put/resize causes infinite loop (Java 7) or data loss (Java 8) |
| **O(n) worst-case get** | Bad hashCode → all keys in one bucket |
| **O(n) resize cost** | When threshold hit, all entries rehashed |
| **Memory overhead** | Bucket array + per-entry Node objects |
| **Requires equals/hashCode** | Broken contracts cause bugs |

### 🟢 When to Use

```java
// 1. Fast key-value lookup — primary use case
HashMap<String, User> userCache = new HashMap<>();
userCache.put("user123", fetchUser("user123"));
User u = userCache.get("user123");  // O(1)!

// 2. Building an index / lookup table
HashMap<Integer, List<Order>> ordersByYear = new HashMap<>();
// Group orders by year for fast lookup

// 3. Counting / frequency mapping
HashMap<String, Integer> wordCount = new HashMap<>();
for (String word : words) {
    wordCount.merge(word, 1, Integer::sum);  // Count occurrences
}

// 4. Caching expensive computations
HashMap<CacheKey, Result> computeCache = new HashMap<>();
Result r = computeCache.computeIfAbsent(key, k -> expensiveComputation(k));
```

### 🔴 When NOT to Use

```java
// 1. Need ordered iteration — use LinkedHashMap or TreeMap
HashMap<String, Integer> map = new HashMap<>();
// Order unpredictable! Don't rely on it.

// 2. Multi-threaded access — use ConcurrentHashMap
HashMap<String, Integer> unsafe = new HashMap<>();
new Thread(() -> unsafe.put("A", 1)).start();
new Thread(() -> unsafe.put("B", 2)).start();
// Corrupted! Use ConcurrentHashMap instead.

// 3. Small collections (< 10 elements)
// A simple List + linear scan might be faster
// HashMap overhead (hash computation, Node objects) > O(n) scan

// 4. Sorted keys needed — use TreeMap
// HashMap has no sorting. TreeMap keeps keys in sorted order.

// 5. Keys are mutable
// If you modify a key after insertion, you can't find it again!
```

---

## 9. HashMap vs The Alternatives

| Aspect | HashMap | LinkedHashMap | TreeMap | ConcurrentHashMap |
|--------|---------|---------------|---------|-------------------|
| **Order** | None | Insertion/Access order | Sorted order | None |
| **Get** | O(1) avg | O(1) avg | O(log n) | O(1) avg |
| **Put** | O(1) avg | O(1) avg | O(log n) | O(1) avg |
| **Thread-safe** | ❌ No | ❌ No | ❌ No | ✅ Yes |
| **Null keys** | ✅ One | ✅ One | ❌ No | ❌ No |
| **Null values** | ✅ Yes | ✅ Yes | ✅ Yes | ❌ No |
| **Internal** | Bucket + list/tree | Bucket + list/tree + chain | Red-Black tree | Bucket + CAS + synch |
| **Iterator** | Fail-fast | Fail-fast | Fail-fast | Fail-safe |
| **Sorting** | None | None | Natural/Comparator | None |

---

## 10. The Critical Contract: equals() and hashCode()

### The Rule

> If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` **must** be true.
>
> If `a.hashCode() == b.hashCode()` is true, `a.equals(b)` **may** be false (collision).

### What happens when you break it?

```java
class BadKey {
    String id;
    
    BadKey(String id) { this.id = id; }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof BadKey && Objects.equals(id, ((BadKey) o).id);
    }
    
    // ❌ NO hashCode() override — uses Object.hashCode() (memory address)
}

HashMap<BadKey, String> map = new HashMap<>();
BadKey k1 = new BadKey("123");
BadKey k2 = new BadKey("123");  // "Equal" to k1

map.put(k1, "Alice");
System.out.println(map.get(k2));  // null! Why?

// k1.hashCode() = 0x7a3b4c (based on memory address)
// k2.hashCode() = 0x9d8e7f (different memory address!)
// Different hash → different bucket → not found!
```

### The Fix

```java
class GoodKey {
    String id;
    
    GoodKey(String id) { this.id = id; }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof GoodKey && Objects.equals(id, ((GoodKey) o).id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);  // Always override with equals!
    }
}

HashMap<GoodKey, String> map = new HashMap<>();
GoodKey k1 = new GoodKey("123");
GoodKey k2 = new GoodKey("123");

map.put(k1, "Alice");
System.out.println(map.get(k2));  // "Alice" — same hash → same bucket → equals true!
```

---

## 11. Common Pitfalls

| Mistake | Why It's Wrong | Correct Approach |
|---------|---------------|-----------------|
| **Mutating key after insertion** | Key in wrong bucket, can't find it | Use immutable keys (String, Integer) |
| **Forgetting hashCode()** | Objects equal by equals() but different buckets | Always override both equals and hashCode |
| **Assuming iteration order** | Order changes after resize | Use LinkedHashMap |
| **Forgetting initial capacity** | Multiple O(n) resizes | `new HashMap<>(expectedSize / 0.75f + 1)` |
| **Using HashMap in threads** | Race conditions, data loss, infinite loop (Java 7) | ConcurrentHashMap |
| **put() inside loop thinking O(1)** | If resize triggered each iteration, O(n²) | Pre-size the map |
| **Not checking for null from get()** | `Integer` + autoboxing → NPE | Use `getOrDefault()` |

---

## 12. HashMap Java 7 vs Java 8

| Aspect | Java 7 | Java 8+ |
|--------|--------|---------|
| **Collision handling** | Linked list only | Linked list → Red-Black tree at 8 |
| **Worst-case get** | O(n) | O(log n) |
| **Hash distribution** | 4 XOR operations | 1 XOR: `h ^ (h >>> 16)` |
| **Insertion in bucket** | Head insertion | Tail insertion (preserves order) |
| **Infinite loop in resize** | ✅ Yes! (circular list) | ❌ Fixed |
| **Null handling** | table[0] | table[0] (same) |
| **Capacity** | Power of 2 | Power of 2 |

---

## 13. Interview Quick Reference

**Q: How does HashMap work?**
A: Array of buckets. `put()` → compute `hash(key) → index = hash & (n-1)` → if bucket empty, create node. If collision, traverse linked list/ tree, replace if key exists, insert if not. Resize at `size > capacity × 0.75`.

**Q: Why is capacity always a power of 2?**
A: So `(n - 1) & hash` works as a fast modulo replacement. If n=16, n-1=0b1111, AND gives low 4 bits. If n=17, n-1=0b10000, AND gives only 1 bit — terrible distribution.

**Q: Why Java 8's treeify at 8 nodes?**
A: Poisson distribution: with good hash, probability of 8 collisions in same bucket is < 1 in 10 million. Tree improves worst case from O(n) to O(log n).

**Q: Why 0.75 load factor?**
A: Tradeoff between time and space. Higher (0.9): less space, more collisions. Lower (0.5): less collisions, more space. 0.75 is the empirical sweet spot.

**Q: Is HashMap thread-safe?**
A: No. Java 7: concurrent put during resize causes infinite loop. Java 8: data loss/ corruption. Use ConcurrentHashMap.

---

## 14. 30-Second Summary

```
HashMap = array of buckets (Node<K,V>[] table). Size must be power of 2.

put: hash(key) → index = hash & (n-1) → insert/replace
       (n-1) & hash works because if n=16, n-1=0b1111, AND=low 4 bits

Collisions: linked list ≤7 nodes, Red-Black tree ≥8 (Java 8+)
Resize: double capacity when size > capacity × 0.75
  Optimization: (hash & oldCapacity) == 0 → stays; != 0 → moves to j+oldCap

Get: O(1) average, O(log n) worst (tree), O(n) worst (list, Java 7)

Key rules:
  - Override equals() AND hashCode() together
  - Use immutable keys
  - Pre-size for expected elements
  - NOT thread-safe → use ConcurrentHashMap

implements: Map, Cloneable, Serializable