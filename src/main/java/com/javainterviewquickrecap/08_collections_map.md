# Module 3b — Collections: Map Deep Dive — Senior-Level Interview Q&A

> **Relevance**: 7+ years experience — covers JVM internals, concurrent access patterns, memory layout, production debugging.

---

## Q1. HashMap Internals — Complete Source-Level Analysis

### 1. Why This Matters at Senior Level
At 7+ years, you're expected to know not just that HashMap is "O(1) average" but exactly what happens at the JVM level during resize, treeification, and concurrent access. Production incidents from HashMap misuse (infinite loops, data loss, OOM) in high-throughput systems are common.

### 2. Source Code Deep Dive (JDK 17)

```java
// =====================================================
// HASH COMPUTATION — Why this specific algorithm?
// =====================================================

// From HashMap.java (JDK 17):
static final int hash(Object key) {
    int h;
    // key.hashCode() is a NATIVE method (usually)
    // XOR high 16 bits into low 16 bits — why?
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}

// Why h ^ (h >>> 16)?
// Most objects have small hashCodes or hashes that differ only in HIGH bits
// Without this: two keys with hashCode=0xFFFF0000 and 0xFFFF0001
//   Both map to bucket 0 because LOWER 4 bits are 0000!
// With XOR: high bits influence low bits → better distribution
// Why not XOR more bits? Performance — 16-bit shift is fast (CPU instruction)

// =====================================================
// TABLE INDEX CALCULATION — Why (n-1) & hash?
// =====================================================

// Index formula: i = (n - 1) & hash
// Where n = table.length (always power of 2)

// This REPLACES: i = hash % n  (modulo — EXPENSIVE!)
// Why it works:
//   n = 16 → n-1 = 15 (binary: 00001111)
//   (n-1) & hash = lower 4 bits of hash
//   hash % 16 = lower 4 bits of hash
//   Same result, but AND is 1 CPU cycle vs ~40 for modulo!

// CRITICAL: This is why capacity MUST be power of 2
// If capacity = 15 → n-1 = 14 (binary: 00001110)
// LSB is ALWAYS 0 → even buckets only get entries → 50% bucket waste!

// =====================================================
// PUT METHOD — Full JVM execution trace
// =====================================================

final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    
    // Step 1: Lazy initialization
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;  // First put triggers resize!
    
    // Step 2: Bucket is EMPTY → fast path (no collision)
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        // Step 3: Bucket has entries → collision handling
        Node<K,V> e; K k;
        
        // 3a: Check if FIRST node matches (same key → replace)
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        
        // 3b: Check if bucket is treeified (Red-Black tree)
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        
        // 3c: Scan linked list
        else {
            for (int binCount = 0; ; ++binCount) {
                // End of list → append new node
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // TREEIFY THRESHOLD = 8 — linked list becomes tree!
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for first node
                        treeifyBin(tab, hash);
                    break;
                }
                // Found matching key in list
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        
        // Step 4: Key already exists → replace value
        if (e != null) {
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);  // For LinkedHashMap
            return oldValue;
        }
    }
    
    // Step 5: Increment size, check resize threshold
    ++modCount;  // Fail-fast iterator tracking
    if (++size > threshold)
        resize();  // Double capacity, rehash all entries → O(n)
    
    afterNodeInsertion(evict);  // For LinkedHashMap
    return null;
}

// =====================================================
// RESIZE — The most expensive operation
// =====================================================

// When size > threshold (= capacity * loadFactor)
// Default: threshold = 16 * 0.75 = 12

final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    
    // Double capacity
    if (oldCap > 0) {
        if (oldCap >= MAXIMUM_CAPACITY) {  // 1 << 30
            threshold = Integer.MAX_VALUE;  // Stop resizing
            return oldTab;
        }
        newCap = oldCap << 1;  // DOUBLE the capacity!
        newThr = oldThr << 1;  // Double threshold too
    }
    
    // Create new array: Node<K,V>[] newTab = new Node[newCap];
    // Transfer old entries to new table:
    for (int j = 0; j < oldCap; ++j) {
        Node<K,V> e;
        if ((e = oldTab[j]) != null) {
            oldTab[j] = null;  // Help GC
            
            // CASE 1: Single node at bucket → simple rehash
            if (e.next == null)
                newTab[e.hash & (newCap - 1)] = e;
            
            // CASE 2: Tree node → split tree
            else if (e instanceof TreeNode)
                ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
            
            // CASE 3: Linked list → preserve order (Java 8+)
            else {
                // Java 8 OPTIMIZATION: No need to recompute hash!
                // Use: (e.hash & oldCap) == 0 → same position
                // Use: (e.hash & oldCap) != 0 → position + oldCap
                Node<K,V> loHead = null, loTail = null;
                Node<K,V> hiHead = null, hiTail = null;
                Node<K,V> next;
                do {
                    next = e.next;
                    if ((e.hash & oldCap) == 0) {  // Same index
                        if (loTail == null) loHead = e;
                        else loTail.next = e;
                        loTail = e;
                    } else {  // new index = oldIndex + oldCap
                        if (hiTail == null) hiHead = e;
                        else hiTail.next = e;
                        hiTail = e;
                    }
                    e = next;
                } while (e != null);
                
                if (loTail != null) {
                    loTail.next = null;
                    newTab[j] = loHead;  // Same position
                }
                if (hiTail != null) {
                    hiTail.next = null;
                    newTab[j + oldCap] = hiHead;  // oldPos + oldCap
                }
            }
        }
    }
    return newTab;
}
```

### 3. ConcurrentHashMap — Java 8+ Complete Internals

```java
// =====================================================
// ConcurrentHashMap — NOT just "synchronized HashMap"
// =====================================================

// Java 7 ConcurrentHashMap: Segment locking (16 segments, each with own lock)
// Java 8+ ConcurrentHashMap: CAS + synchronized on bucket head

// =====================================================
// PUT (Java 8+) — Lock-free for most cases
// =====================================================

final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();  // No nulls!
    int hash = spread(key.hashCode());  // Similar to HashMap
    int binCount = 0;
    
    for (Node<K,V>[] tab = table;;) {  // CAS loop — retry on failure
        Node<K,V> f; int n, i, fh; K fk; V fv;
        
        // CASE 1: Table not initialized → initialize (CAS)
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();  // CAS-based initialization
        
        // CASE 2: Bucket is EMPTY → CAS insert (NO LOCK!)
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            // Atomic CAS: if bucket is empty, place node
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value)))
                break;  // Success! No lock needed!
            // CAS failed → another thread inserted → retry
        }
        
        // CASE 3: Resize in progress → help resize
        else if ((fh = f.hash) == MOVED)  // -1 sentinel
            tab = helpTransfer(tab, f);  // Help resize (multi-threaded!)
        
        // CASE 4: Collision → synchronized on bucket HEAD only
        else {
            V oldVal = null;
            synchronized (f) {  // Lock ONLY this bucket's head node
                if (tabAt(tab, i) == f) {  // Double-check
                    if (fh >= 0) {  // Normal linked list
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            // Find or insert
                        }
                    }
                    else if (f instanceof TreeBin)  // Tree
                        // Insert into tree while holding bucket lock
                }
            }
            // Treeify if needed
        }
    }
    addCount(1L, binCount);  // CAS-based size update (striped counters)
    return null;
}

// =====================================================
// GET (Java 8+) — Completely LOCK-FREE!
// =====================================================

public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    int h = spread(key.hashCode());
    
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (e = tabAt(tab, (n - 1) & h)) != null) {
        
        // Check head
        if ((eh = e.hash) == h) {
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        // Tree or forwarding node during resize
        else if (eh < 0)
            return (p = e.find(h, key)) != null ? p.val : null;
        // Linked list scan
        while ((e = e.next) != null) {
            if (e.hash == h &&
                ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
        }
    }
    return null;
}
// NO locks! Table is volatile, Node.val is volatile
// Reads see latest writes without synchronization!

// =====================================================
// SIZE() — Striped Counters (Java 8+)
// =====================================================

// In Java 7: size() locked all segments — expensive!
// In Java 8: CounterCell[] array — each thread updates its own cell
//   → No contention on size updates!

public int size() {
    long n = sumCount();  // Sum all CounterCell values — approximate!
    // NOT exact! Concurrent updates may not be reflected
    // Use mappingCount() for long values
    return (n < 0L) ? 0 : (n > (long)Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int)n;
}

// For exact count under concurrent access: use LongAdder instead
// ConcurrentHashMap.size() is best-effort
```

### 4. Senior-Level Tricky Cases

**Case 1: HashMap infinite loop (Java 7 — root cause analysis)**
```java
// Java 7 HashMap.resize() in multi-threaded environment:
// Transfer loop:
void transfer(Entry[] newTable, boolean rehash) {
    int newCapacity = newTable.length;
    for (Entry<K,V> e : table) {
        while(null != e) {
            Entry<K,V> next = e.next;        // Save next
            int i = indexFor(e.hash, newCapacity);
            e.next = newTable[i];             // HEAD INSERTION!
            newTable[i] = e;                  // New node becomes head
            e = next;                         // Move to next
        }
    }
}

// Thread 1 and Thread 2 both resize simultaneously:
// Both read entry chain: A → B → null
// Thread 1 reverses: B → A (head insertion)
// Thread 2 also reverses: A → B
// Combined: A → B → A → B → ... CIRCULAR!
// get() or put() on this bucket → INFINITE LOOP → CPU 100%!

// Java 8+ fixes this by using tail insertion + preserving original order
// But HashMap is STILL not thread-safe! Data corruption possible.
// Use ConcurrentHashMap always in multi-threaded code.
```

**Case 2: HashMap.size() overflow**
```java
// HashMap capacity is capped at MAXIMUM_CAPACITY = 1 << 30 = 1,073,741,824
// After reaching MAXIMUM_CAPACITY: threshold = Integer.MAX_VALUE
// resize() returns immediately — no more doubling!
// But size continues to grow beyond MAXIMUM_CAPACITY...
// Performance degrades to O(log n) as all buckets are chained!

// Fix: Use ConcurrentHashMap for any map that could grow beyond 1B entries
// Or implement your own segmented map
```

**Case 3: HashMap iteration and ConcurrentModificationException**
```java
Map<String, String> map = new HashMap<>();
map.put("A", "1");
map.put("B", "2");

for (Map.Entry<String, String> entry : map.entrySet()) {
    if ("A".equals(entry.getKey())) {
        map.remove(entry.getKey());  // ❌ ConcurrentModificationException!
    }
}

// Why? modCount changes on structural modification (put/remove)
// Iterator checks: expectedModCount == modCount on each next()
// If they differ → ConcurrentModificationException

// ✅ FIX:
Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
while (it.hasNext()) {
    Map.Entry<String, String> entry = it.next();
    if ("A".equals(entry.getKey())) {
        it.remove();  // Safe! Updates expectedModCount
    }
}

// ConcurrentHashMap: NO ConcurrentModificationException
// Uses snapshot-based iterator — may not see latest writes
```

**Case 4: IdentityHashMap — when == beats equals()**
```java
// IdentityHashMap uses REFERENCE equality (==) not equals()
// Used for: serialization, deep-copy, proxy mappings
IdentityHashMap<Class<?>, Object> classCache = new IdentityHashMap<>();
classCache.put(String.class, "string");  // Keyed by Class identity
classCache.put(Integer.class, "int");

// Two Class objects that are equals() but different references:
Class<?> c1 = String.class;
Class<?> c2 = Class.forName("java.lang.String");
System.out.println(c1 == c2);  // true — same Class object (JVM guarantees)
// IdentityHashMap normally works because JVM caches Class objects

// But for serialization/deserialization:
// Each ClassLoader load creates DIFFERENT Class objects!
// IdentityHashMap correctly treats them as DIFFERENT keys
```

### 5. Production Debugging Scenarios

```java
// Scenario 1: HashMap causing 100% CPU
// → Check thread stacks: all threads stuck in HashMap.get()/put()
// → Infinite loop in linked list (Java 7) or data corruption
// → Fix: dump heap, find corrupted HashMap, check if used concurrently

// Scenario 2: HashMap OOM
// → Unbounded cache using HashMap
// → Fix: Use Caffeine/Guava cache with maxSize
// → Or: LinkedHashMap with removeEldestEntry

// Scenario 3: ConcurrentHashMap not performing as expected
// → Too many collisions (bad hashCode)
// → Too many resize operations (initial capacity too small)
// → Fix: Profile with -XX:+PrintGCDetails to see resize cost
```

### 6. Senior-Level Q&As

**Q (Senior)**: Your production app has a HashMap used in a read-mostly but occasionally updated scenario. You see "null" values for existing keys. Why?

**A**: HashMap is not thread-safe. When Thread A calls get() while Thread B calls put() that triggers resize(), Thread A may see: (1) Null from partially migrated table (resize creates new array, old entries not yet moved); (2) Infinite loop (Java 7); (3) Stale values from CPU cache (no volatile guarantees). Fix: (1) Use ConcurrentHashMap for any concurrent access; (2) If reads vastly outnumber writes, use CopyOnWriteMap pattern or immutable map with volatile reference.

**Q (Staff)**: Design a high-throughput cache with 99.999% hit rate supporting 100K writes/sec and 5M reads/sec.

**A**: (1) Use Caffeine as cache (ConcurrentHashMap-based, LRU + W-TinyLFU eviction). (2) Two-tier: L1 (on-heap, Caffeine) + L2 (off-heap, Redis with SSD persistence). (3) Write-through + write-behind: writes go to Caffeine immediately, async to Redis. (4) Bloom filter at L1 for negative lookups to prevent L2 storms on cache misses. (5) Monitor: hit rate, eviction count, load time. (6) Pre-warm on startup by replaying last 60 minutes of writes. (7) For 5M reads/sec: 20 Caffeine instances sharded by key hash, each with own thread pool. Without Caffeine: use ConcurrentHashMap with segment-level locks.

**Q (Principal)**: A ConcurrentHashMap.get() returns a stale value even after a put() completed. Is this possible?

**A**: Yes. ConcurrentHashMap guarantees happens-before between PUT and GET of the SAME key only if the GET happens AFTER the PUT in wall-clock time. But: (1) If the key was not in the map and another thread calls computeIfAbsent() concurrently, only one puts the key — the other thread may still get null. (2) resizing() transfers nodes — during transfer, get() walks both old and new tables. A put() to old table, followed by get() that hits the new table first → misses the new entry. (3) Node.val is volatile, so value writes are visible. But the REFERENCE to the node may be from the old array. Use computeIfAbsent() for atomic check-and-insert.

### 10. Final 30-Second Answer

HashMap: array + list/tree. (n-1) & hash index, power-of-2 required. Resize doubles capacity, O(n). Treeify at 8 collisions (Poisson 0.0000006 probability). ConcurrentHashMap: lock-free reads, CAS + bucket-level locks for writes, striped counters for size(), no null keys. Java 7 HashMap had infinite loop bug on concurrent resize. IdentityHashMap uses ==. WeakHashMap entries GC'd when key has no strong references. Never use HashMap in concurrent code.