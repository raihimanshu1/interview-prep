# LinkedHashMap — Why? What? How? When?

## 1. The Problem Before LinkedHashMap

### HashMap's Missing Feature: Predictable Order

HashMap is fast (O(1) get/put), but has a **critical limitation**:

```java
HashMap<String, Integer> map = new HashMap<>();
map.put("C", 3);
map.put("A", 1);
map.put("B", 2);
map.put("D", 4);

System.out.println(map);  // {A=1, B=2, C=3, D=4} — alphabetical? Or different?
// Next run:  {D=4, A=1, B=2, C=3} — ORDER CHANGES!

// Iteration: unpredictable!
for (String key : map.keySet()) {
    // Order depends on hash codes, bucket positions, resize history
}
```

**HashMap's iteration order:**
- Is NOT insertion order
- Is NOT sorted order
- Is NOT consistent between runs
- Changes when resize happens
- Depends on hash code implementations

**Why does this matter?**

```java
// Example: Cache that needs to expire oldest entries
HashMap<String, CacheEntry> cache = new HashMap<>();
cache.put("session1", data1);
cache.put("session2", data2);
cache.put("session3", data3);
// ... time passes
// Want to remove OLDEST entry — but HashMap doesn't know which is oldest!
```

### What developers did before LinkedHashMap:

```java
// Option 1: Use separate list for order — tedious!
HashMap<String, Object> map = new HashMap<>();
List<String> insertionOrder = new ArrayList<>();  // Maintain separate order

void put(String key, Object value) {
    if (!map.containsKey(key)) {
        insertionOrder.add(key);  // Track insertion order manually
    }
    map.put(key, value);
}

// Option 2: Use TreeMap — but it sorts, doesn't preserve insertion order
// Option 3: Accept unpredictable order — dangerous for some use cases
```

> **LinkedHashMap was created to solve this**: A HashMap that preserves **insertion order** (or **access order**) without sacrificing performance.

---

## 2. What is LinkedHashMap? (Simple Explanation)

```java
LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
map.put("C", 3);
map.put("A", 1);
map.put("B", 2);

System.out.println(map);  // {C=3, A=1, B=2} — INSERTION ORDER preserved!
for (String key : map.keySet()) {
    // Always: C, A, B (insertion order)
    // Never changes!
}
```

**LinkedHashMap = HashMap + a doubly-linked list running through all entries.**

Internal picture:
```
LinkedHashMap (size=3)
┌────────────────────────────────────────────────────┐
│  HashMap bucket array                               │
│  bucket[?] → Node{key="C"}  → Node{key="A"} (chain)│
│  bucket[?] → Node{key="B"}                          │
└────────────────────────────────────────────────────┘
        │
        ▼
  Doubly-linked chain (maintains order):
  head → [Entry "C"] ←→ [Entry "A"] ←→ [Entry "B"] ←→ tail
```

**Key insight**: LinkedHashMap extends HashMap. It overrides the Node class to add `before` and `after` pointers that form a separate doubly-linked list. This list runs through ALL entries, maintaining either insertion order or access order.

---

## 3. How LinkedHashMap Works Internally

### The Entry Class (extends HashMap.Node)

```java
// LinkedHashMap's entry adds before/after pointers
static class Entry<K,V> extends HashMap.Node<K,V> {
    Entry<K,V> before, after;  // Pointers for the doubly-linked chain
    
    Entry(int hash, K key, V value, Node<K,V> next) {
        super(hash, key, value, next);
    }
}
```

### Internal Structure

```
table[] (bucket array, from HashMap):
  index 3: Entry("C") → Entry("A")  (collision chain)
  index 7: Entry("B")

Doubly-linked chain (maintains order):
  head → Entry("C") ←→ Entry("A") ←→ Entry("B") → tail
              ↓             ↓             ↓
          bucket[3]      bucket[3]      bucket[7]
```

### How insertion order is maintained

```java
// HashMap.put() calls these hook methods:
void afterNodeAccess(Node<K,V> p) { }     // Called when entry is accessed
void afterNodeInsertion(boolean evict) { }  // Called after insert
void afterNodeRemoval(Node<K,V> p) { }     // Called after removal
```

LinkedHashMap **overrides** these hooks:

```java
// After insertion: link to tail
void afterNodeInsertion(boolean evict) {
    LinkedHashMap.Entry<K,V> first = head;
    if (evict && first != null && removeEldestEntry(first)) {
        // Used for LRU cache — remove oldest if policy says so
        K key = first.key;
        removeNode(hash(key), key, null, false, true);
    }
}

// After access: move to tail (for access-order mode)
void afterNodeAccess(Node<K,V> e) {
    if (accessOrder) {  // Only if access-order mode
        // Move 'e' to the end of the linked chain
        // Unlink from current position
        // Link at tail
    }
}
```

---

## 4. Key Feature: Access Order (LRU Cache)

```java
// Create with accessOrder = true
LinkedHashMap<String, String> lru = new LinkedHashMap<>(16, 0.75f, true);
//                                     capacity  loadFactor  accessOrder=true

lru.put("A", "Apple");
lru.put("B", "Banana");
lru.put("C", "Cherry");

System.out.println(lru);  // {A=Apple, B=Banana, C=Cherry}

lru.get("A");  // Access "A" — moves it to END
System.out.println(lru);  // {B=Banana, C=Cherry, A=Apple}
//                ↑ "A" is now last (most recently accessed)

lru.get("B");  // Access "B" — moves it to END
System.out.println(lru);  // {C=Cherry, A=Apple, B=Banana}
//                ↑ "B" is now last
```

**Order semantics:**
- **insertionOrder=false (default)**: Maintains order in which entries were inserted
- **accessOrder=true**: Moves entry to END each time it's accessed (get/put)

---

## 5. Building an LRU Cache

The real power of LinkedHashMap is building an LRU (Least Recently Used) cache:

```java
// Simple LRU Cache with LinkedHashMap
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxCapacity;
    
    public LRUCache(int maxCapacity) {
        // initialCapacity, loadFactor, accessOrder=true
        super(maxCapacity, 0.75f, true);
        this.maxCapacity = maxCapacity;
    }
    
    // Override to define eviction policy
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
        // Returns true → removes the eldest (least recently used) entry
    }
}

// Usage:
LRUCache<Integer, String> cache = new LRUCache<>(3);
cache.put(1, "One");
cache.put(2, "Two");
cache.put(3, "Three");
System.out.println(cache);  // {1=One, 2=Two, 3=Three}

cache.get(1);  // Access 1 — moves to end
cache.put(4, "Four");  // Exceeds capacity! Removes eldest (2)
System.out.println(cache);  // {3=Three, 1=One, 4=Four}
//                          // 2 was removed (least recently used)
```

**How removeEldestEntry works:**
```java
// After each insertion, LinkedHashMap calls:
void afterNodeInsertion(boolean evict) {
    Entry<K,V> first = head;  // The OLDEST entry
    if (evict && first != null && removeEldestEntry(first)) {
        // YOUR POLICY says "remove this"
        removeNode(hash(first.key), first.key, null, false, true);
    }
}
```

**In access-order mode:**
- `head` = least recently used (LRU) — oldest by access
- `tail` = most recently used (MRU) — newest by access

---

## 6. LinkedHashMap vs HashMap vs TreeMap

| Aspect | **HashMap** | **LinkedHashMap** | **TreeMap** |
|--------|------------|-------------------|-------------|
| **Order** | None | **Insertion** or **Access** | **Sorted** by key |
| **Get** | O(1) avg | O(1) avg | O(log n) |
| **Put** | O(1) avg | O(1) avg | O(log n) |
| **Memory** | Lowest | **Extra per entry** (before/after) | Lower |
| **Null keys** | ✅ One | ✅ One | ❌ No |
| **LRU Cache** | ❌ No | ✅ Yes (access-order) | ❌ No |
| **Sorting** | None | None | Natural/Comparator |
| **Internal** | Bucket+list/tree | Bucket+list/tree+chain | Red-Black tree |

### Memory Overhead

```
HashMap.Node: hash + key + value + next = 4 fields + header ≈ 32 bytes
LinkedHashMap.Entry: hash + key + value + next + before + after = 6 fields + header ≈ 40-48 bytes
```

LinkedHashMap uses ~25-50% more memory per entry due to the before/after pointers.

---

## 7. LinkedHashMap: Advantages, Limitations, When to Use, When Not to Use

### ✅ Advantages

| Advantage | Why |
|-----------|-----|
| **Predictable iteration order** | Insertion order guaranteed (unless access-order) |
| **LRU Cache support** | Built-in via removeEldestEntry + accessOrder |
| **O(1) get/put** | Same as HashMap — linked chain doesn't affect performance |
| **Backward compatible** | Can replace HashMap without breaking code that expects order |

### ❌ Limitations

| Limitation | Why |
|------------|-----|
| **Higher memory per entry** | before/after pointers (~8-16 bytes extra per entry) |
| **Slightly slower insert/remove** | Must update linked chain pointers |
| **Not thread-safe** | Same as HashMap |
| **Not sorted** | If you need sorted iteration, use TreeMap |

### 🟢 When to Use

```java
// 1. Need predictable iteration order
LinkedHashMap<String, Config> config = new LinkedHashMap<>();
config.put("database", dbConfig);
config.put("cache", cacheConfig);
config.put("auth", authConfig);
// Iteration always returns in insertion order

// 2. Build an LRU Cache
LRUCache<String, Result> cache = new LRUCache<>(100);

// 3. FIFO eviction cache
class FIFOCache<K,V> extends LinkedHashMap<K,V> {
    private final int maxSize;
    FIFOCache(int maxSize) {
        super(maxSize, 0.75f, false);  // insertion-order
        this.maxSize = maxSize;
    }
    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > maxSize;  // Removes oldest inserted
    }
}

// 4. Maintaining insertion order for display/processing
LinkedHashMap<String, Step> pipeline = new LinkedHashMap<>();
pipeline.put("validate", new ValidationStep());
pipeline.put("transform", new TransformStep());
pipeline.put("enrich", new EnrichStep());
// Steps always execute in the order they were registered
```

### 🔴 When NOT to Use

```java
// 1. Memory-constrained environments — use HashMap
// 2. Need sorted keys — use TreeMap
// 3. Concurrent access — use ConcurrentSkipListMap or ConcurrentHashMap
// 4. No order requirement — HashMap is simpler and more memory-efficient
```

---

## 8. Common Pitfalls

| Mistake | Why It's Wrong | Correct Approach |
|---------|---------------|-----------------|
| **Forgetting accessOrder param** | Default is insertion-order (false) | `new LinkedHashMap<>(16, 0.75f, true)` |
| **Not overriding removeEldestEntry** | Cache never evicts | Override to check size() |
| **Assuming thread safety** | Same as HashMap — not thread-safe | Synchronize or use ConcurrentHashMap |
| **Mutable keys** | Lost entries (same as HashMap) | Use immutable keys |

---

## 9. Interview Quick Reference

**Q: How does LinkedHashMap maintain order?**
A: It extends HashMap and overrides the Node class to add `before`/`after` pointers. These form a doubly-linked list that runs through all entries. After each insert (or access), LinkedHashMap updates this chain via hook methods: `afterNodeInsertion()`, `afterNodeAccess()`, `afterNodeRemoval()`.

**Q: What's the difference between insertion-order and access-order?**
A: Insertion-order: entries are ordered as they were first added. Access-order: each get/put moves the accessed entry to the end. Access-order is used for LRU caches.

**Q: How to build an LRU cache with LinkedHashMap?**
A: Create with `accessOrder=true` and override `removeEldestEntry()`:
```java
new LinkedHashMap<>(capacity, 0.75f, true) {
    @Override protected boolean removeEldestEntry(Map.Entry e) {
        return size() > capacity;
    }
};
```

**Q: What's the performance of LinkedHashMap compared to HashMap?**
A: Same O(1) get/put. Slightly slower due to linked chain maintenance. More memory (~40-48 bytes per entry vs ~32 bytes for HashMap.Node).

---

## 10. 30-Second Summary

```
LinkedHashMap = HashMap + doubly-linked chain maintaining order.

Extends HashMap. Adds Entry<K,V> with before/after pointers.

Two modes:
  insertion-order (default): entries ordered by first insertion
  access-order:              entries ordered by last access (LRU)

LRU Cache: new LinkedHashMap<>(cap, 0.75f, true) + removeEldestEntry()

✅ Predictable iteration     ❌ More memory than HashMap
✅ O(1) get/put              ❌ Not thread-safe
✅ LRU cache built-in        ❌ Not sorted (use TreeMap)

Best for: Maintaining insertion order, LRU caches, predictable iteration
Avoid for: Memory-critical apps, concurrent access, sorted iteration