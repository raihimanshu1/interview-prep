# ConcurrentHashMap — Why? What? How? When?

## 1. The Problem Before ConcurrentHashMap

### HashMap in Multi-threaded Code

HashMap is **not thread-safe**. Using it from multiple threads causes data corruption:

```java
HashMap<String, Integer> map = new HashMap<>();

// Thread 1: put 1000 entries
new Thread(() -> {
    for (int i = 0; i < 1000; i++) {
        map.put("key" + i, i);
    }
}).start();

// Thread 2: put 1000 entries concurrently
new Thread(() -> {
    for (int i = 0; i < 1000; i++) {
        map.put("key" + i, i);
    }
}).start();

// Result? Maybe 1000 entries, maybe 990, maybe corrupt!
// In Java 7: INFINITE LOOP during resize (circular linked list)!
// In Java 8: lost entries, wrong size, null values
```

### The Old Solution: synchronizedMap

```java
Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
```

**But this has a huge problem — it locks the ENTIRE map:**

```java
// synchronizedMap locks the whole map for EVERY operation
syncMap.put("A", 1);  // Locks entire map
syncMap.put("B", 2);  // Must wait if another thread is reading!
syncMap.get("A");     // Must wait if another thread is writing!

// Even iteration requires external synchronization:
synchronized (syncMap) {  // Must manually lock!
    for (String key : syncMap.keySet()) {
        // If you don't sync, ConcurrentModificationException!
    }
}
```

**Problems with synchronizedMap:**
- **One lock for everything**: Multiple threads can't read/write simultaneously
- **Contention bottleneck**: 10 threads → 9 must wait
- **Fail-fast iteration**: Requires manual synchronization during iteration
- **Poor scalability**: Performance degrades linearly with more threads

### What developers needed:

> A HashMap that allows **multiple threads to read and write concurrently**, where reads are never blocked, and writes on different buckets don't interfere.

---

## 2. What is ConcurrentHashMap? (Simple Explanation)

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Thread 1 — writes to bucket A
new Thread(() -> map.put("A", 1)).start();

// Thread 2 — writes to bucket B (different bucket! can run in parallel!)
new Thread(() -> map.put("B", 2)).start();

// Thread 3 — reads (never blocked!)
new Thread(() -> System.out.println(map.get("A"))).start();

// No external synchronization needed for iteration:
for (String key : map.keySet()) {  // Safe! Fail-safe iterator
    System.out.println(key + "=" + map.get(key));
}

// Atomic operations built-in:
map.putIfAbsent("C", 3);      // Only put if key doesn't exist
map.computeIfAbsent("D", k -> expensiveLookup(k));  // Atomic lazy init
map.merge("E", 1, Integer::sum);  // Atomic increment
```

**ConcurrentHashMap = Thread-safe HashMap with per-bucket locking and lock-free reads.**

Internal picture (Java 8+):
```
ConcurrentHashMap (size=3, capacity=16)
┌─────┬─────────────────────────────┐
│  0  │ null                         │
│  1  │ Node{key="A", val=1, next=}  │ ← Locked by synchronized on Node "A"
│  2  │ null                         │
│  3  │ Node{key="B", val=2, next=}  │ ← Can be modified by ANOTHER thread!
│ ... │                              │
│ 15  │ null                         │
└─────┴─────────────────────────────┘
```

**Key insight**: ConcurrentHashMap does NOT use a single lock. Instead, it uses **synchronized on the first node of each bucket**. This means:
- If Thread 1 writes to bucket 1 and Thread 2 writes to bucket 3: **They run in parallel**!
- **Reads are completely lock-free**: Uses volatile reads
- **No segments**: Java 8+ per-bucket locking, much finer granularity than Java 7

---

## 3. How ConcurrentHashMap Achieves Concurrency

### Java 7: Segment-based (16 locks)

```
Java 7:   ConcurrentHashMap
┌───────────────────────────────────┐
│  Segment 0 (ReentrantLock)        │
│  ┌──────┬──────┬──────┬──────┐   │
│  │ Hash │ Hash │ ...  │ Hash │   │
│  └──────┴──────┴──────┴──────┘   │
│───────────────────────────────────│
│  Segment 1 (ReentrantLock)        │
│  ┌──────┬──────┬──────┬──────┐   │
│  │ Hash │ Hash │ ...  │ Hash │   │
│  └──────┴──────┴──────┴──────┘   │
│───────────────────────────────────│
│  ... up to 16 segments           │
└───────────────────────────────────┘

Max concurrent writes: 16 (one per segment)
```

### Java 8+: Per-bucket (much finer)

```
Java 8+:  ConcurrentHashMap
┌─────┬─────┬─────┬─────┬─────┬─────┐
│  0  │  1  │  2  │  3  │ ... │ 15  │
│ null│NodeA│ null│NodeB│ ... │ null│
│     │  ↑  │     │  ↑  │     │     │
│     │sync │     │sync │     │     │
│     │ per │     │ per │     │     │
│     │bucket    │bucket     │     │
└─────┴─────┴─────┴─────┴─────┴─────┘

Max concurrent writes: up to number of buckets (initially 16)
If no collision: EVERY put on a different bucket is concurrent!
```

---

## 4. How ConcurrentHashMap Works Internally

### Simplified Structure

```java
public class ConcurrentHashMap<K,V> implements ConcurrentMap<K,V> {
    transient volatile Node<K,V>[] table;  // The bucket array (VOLATILE!)
    
    private transient volatile int sizeCtl;  // Control flag
    // -1: initializing
    // -N: N-1 threads resizing
    // positive: threshold
    
    private transient volatile long baseCount;  // Counter (uncontended)
    private transient volatile CounterCell[] counterCells;  // Counter (contended)
    
    // Node structure:
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        volatile V val;       // VALUE is VOLATILE — lock-free reads!
        volatile Node<K,V> next;  // NEXT is VOLATILE
    }
}
```

### How put() Works

```java
public V put(K key, V value) {
    return putVal(key, value, false);
}

final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    
    int hash = spread(key.hashCode());
    int binCount = 0;
    
    for (Node<K,V>[] tab = table;;) {  // CAS loop — retry on failure
        Node<K,V> f; int n, i, fh;
        
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();  // Lazy init (CAS on sizeCtl)
        
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            // Bucket EMPTY — CAS insert (NO lock!)
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value, null)))
                break;  // Success! Lock-free!
        }
        
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);  // Help with resize
        
        else {
            V oldVal = null;
            synchronized (f) {  // Lock ONLY the first node
                if (tabAt(tab, i) == f) {  // Double-check after lock
                    // Traverse linked list or tree → insert/replace
                }
            }
            if (binCount != 0) break;
        }
    }
    addCount(1L, binCount);  // Update size
    return null;
}
```

**Put strategy (3 cases):**
1. **Bucket empty** → CAS insert (no lock, no synchronized!)
2. **Bucket has chain** → `synchronized(f)` on first node (per-bucket lock)
3. **Resize in progress** → Help with resize (cooperative)

### How get() Works — Lock-Free!

```java
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    int h = spread(key.hashCode());
    
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (e = tabAt(tab, (n - 1) & h)) != null) {
        
        if ((eh = e.hash) == h) {
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;  // Volatile read — sees latest value!
        }
        else if (eh < 0)  // TreeBin or ForwardingNode (resize)
            return (p = e.find(h, key)) == null ? null : p.val;
        
        while ((e = e.next) != null) {
            if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;  // Volatile read!
        }
    }
    return null;
}
```

**Why get() doesn't need a lock:**
- `table` reference is `volatile` → sees latest bucket array
- `val` and `next` are `volatile` → sees latest values
- `tabAt()` uses `Unsafe.getObjectVolatile()` → reads from main memory
- No lock needed! Multiple readers can read the same bucket concurrently with a writer

---

## 5. The Core Duo: CAS + Synchronized

ConcurrentHashMap uses a **two-phase locking** strategy:

**Phase 1 — CAS (Compare-And-Swap): For empty buckets**

```java
// CAS: If bucket is null, atomically set it to new Node
// If two threads try simultaneously, only ONE succeeds
// The other retries (CAS loop)

casTabAt(tab, i, null, new Node<>(hash, key, value, null));
//       table  index  expected  new value
// Only succeeds if table[i] == null
// Returns true if set, false if another thread beat us
```

**Phase 2 — Synchronized: For existing buckets (collisions)**

```java
// If bucket has a node, synchronize on that node
// Only locks THIS bucket — other buckets are unaffected
synchronized (f) {  // f = first node in bucket
    // Thread-safe insert/replace
}
```

---

## 6. Size Tracking: CounterCells

```java
// Without contention: simple CAS on baseCount
// With contention: distribute count across CounterCell[]

// Java 7: size() locked all segments → slow
// Java 8: size() sums baseCount + CounterCells → approximate

public int size() {
    long n = sumCount();  // Sum all counter values (approximate!)
    return (n < 0L) ? 0 : (n > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int)n;
}
```

**Why approximate?** Because during `sumCount()`, other threads are still modifying counts. The value is **consistent** (no partial updates across buckets), but may be slightly stale.

---

## 7. Resize: Cooperative Multithreading

When ConcurrentHashMap needs to resize, it doesn't block all threads. Instead, it **cooperates**:

```java
// Thread 1 triggers resize
// It creates a new table and starts moving entries

// Thread 2, 3, 4 try to put() during resize
// They see MOVED nodes in the old table
// Instead of waiting, they HELP with the resize!

if ((fh = f.hash) == MOVED) {
    tab = helpTransfer(tab, f);  // Let me help too!
}

// Multiple threads simultaneously move entries from old table to new
// Each thread claims a range of buckets to transfer
// Independently moved to new table
```

**Benefits:**
- No thread is blocked during resize
- Resize completes faster (parallel work)
- Threads doing `put()` help instead of waiting

---

## 8. ConcurrentHashMap vs Alternatives

| Aspect | **ConcurrentHashMap** | **synchronizedMap** | **HashMap** |
|--------|----------------------|--------------------|-------------|
| **Thread-safe** | ✅ Yes | ✅ Yes (one lock) | ❌ No |
| **Read concurrency** | **Lock-free** | Blocked (one lock) | N/A |
| **Write concurrency** | **Per-bucket** (high) | One thread at a time | N/A |
| **Null keys** | ❌ No | ✅ Yes | ✅ Yes |
| **Null values** | ❌ No | ✅ Yes | ✅ Yes |
| **Iterator** | **Fail-safe** (snapshot) | Fail-fast (must sync) | Fail-fast |
| **Performance (multi-thread)** | **Excellent** | Poor | Broken |
| **Performance (single-thread)** | ~5-10% slower than HashMap | Slower (synchronization) | Fastest |
| **Atomic ops** | ✅ Built-in (putIfAbsent, merge) | ❌ Must manually sync | ❌ N/A |

### Performance: ConcurrentHashMap vs synchronizedMap

```
Scenario: 8 threads, each doing 100,000 put() operations

synchronizedMap:    ~500 ms  (all threads serialized by single lock)
ConcurrentHashMap:  ~80 ms   (threads rarely contend for same bucket)

→ ConcurrentHashMap is ~6x faster under contention!
```

---

## 9. ConcurrentHashMap: Advantages, Limitations, When to Use, When Not to Use

### ✅ Advantages

| Advantage | Why |
|-----------|-----|
| **Thread-safe without locking** | Lock-free reads via volatile |
| **High concurrency** | Per-bucket synchronized writes + CAS for empty buckets |
| **Fail-safe iterator** | Snapshot iteration — no ConcurrentModificationException |
| **Atomic operations** | putIfAbsent, computeIfAbsent, merge, replace |
| **Cooperative resize** | Multiple threads help resize in parallel |
| **Built-in optimizations** | Tree on high collisions (same as HashMap) |

### ❌ Limitations

| Limitation | Why |
|------------|-----|
| **No null keys/values** | Throws NPE |
| **size() is approximate** | Dynamic counting — value may be stale |
| **Slightly slower than HashMap** | Volatile overhead, CAS retries |
| **Not sorted** | Doesn't maintain order |
| **Higher memory** | CounterCell[] for tracking |

### 🟢 When to Use

```java
// 1. Any multi-threaded cache or lookup table
ConcurrentHashMap<String, User> userCache = new ConcurrentHashMap<>();
userCache.put("user123", fetchUser("user123"));

// Thread-safe iteration:
userCache.forEach((id, user) -> process(user));  // No sync needed!

// 2. Atomic counters / accumulators
ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
counts.merge("visits", 1, Integer::sum);  // Atomic increment!

// 3. Lazy initialization pattern
ConcurrentHashMap<String, ExpensiveObject> cache = new ConcurrentHashMap<>();
ExpensiveObject obj = cache.computeIfAbsent("key", k -> {
    return new ExpensiveObject(k);  // Only computed once!
});

// 4. Thread-safe Set
Set<String> concurrentSet = ConcurrentHashMap.newKeySet();

// 5. Collector-friendly (groupingBy, toMap with concurrent)
Map<Integer, List<String>> grouped = stream
    .collect(Collectors.groupingByConcurrent(String::length));
```

### 🔴 When NOT to Use

```java
// 1. Single-threaded — HashMap is faster
ConcurrentHashMap<String, Integer> slow = new ConcurrentHashMap<>();  // Overkill
HashMap<String, Integer> fast = new HashMap<>();  // ~5-10% faster

// 2. Need null keys/values
map.put(null, "value");  // NullPointerException!

// 3. Need sorted iteration — use ConcurrentSkipListMap
ConcurrentHashMap<Integer, String> unordered = new ConcurrentHashMap<>();  // Not sorted
ConcurrentSkipListMap<Integer, String> sorted = new ConcurrentSkipListMap<>();  // Sorted!

// 4. Need exact size() — HashMap+sync or use separate counter
```

---

## 10. Atomic Operations Deep Dive

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// putIfAbsent — only put if key doesn't exist
map.putIfAbsent("key", 1);  // Returns null if inserted, old value if existed

// computeIfAbsent — atomic lazy init (thread-safe!)
// Only ONE thread executes the function, others wait for result
map.computeIfAbsent("key", k -> {
    return database.query(k);  // Only runs once!
});

// computeIfPresent — atomically update existing key
map.computeIfPresent("key", (k, v) -> v + 1);

// compute — atomically create or update
map.compute("key", (k, v) -> v == null ? 1 : v + 1);

// merge — atomic upsert (combine existing with new)
map.merge("counter", 1, Integer::sum);  // Increment: 0→1, 1→2, etc.

// replace — only if current value matches
map.replace("key", 1, 2);  // Only if currently 1

// forEach — safe parallel iteration
map.forEach(100_000, (k, v) -> System.out.println(k + "=" + v));
// ↑ parallelism threshold: only parallelize if size > 100_000
```

### Without atomic ops (WRONG):

```java
// ❌ NOT atomic! Two threads can run between get and put
if (!map.containsKey("key")) {
    map.put("key", computeExpensiveValue());  // Race condition!
}

// ✅ Atomic — only ONE thread computes
map.computeIfAbsent("key", k -> computeExpensiveValue());
```

---

## 11. Common Pitfalls

| Mistake | Why It's Wrong | Correct Approach |
|---------|---------------|-----------------|
| **Null keys/values** | NullPointerException | Use sentinel values |
| **Assuming size() is exact** | CounterCell values are approximate | Use for monitoring, not decision logic |
| **Compound ops without atomic methods** | Race conditions between get/set | Use `computeIfAbsent()`, `merge()`, etc. |
| **Using ConcurrentHashMap when single-threaded** | ~5-10% overhead from volatiles | Use HashMap |
| **Not handling computeIfAbsent recursion** | computeIfAbsent inside computeIfAbsent → deadlock | Don't nest compute methods |

---

## 12. Interview Quick Reference

**Q: How does ConcurrentHashMap achieve thread-safety?**
A: Java 8+ uses per-bucket locking. Empty buckets: CAS (compare-and-swap). Non-empty buckets: `synchronized` on the first node. Reads are lock-free (volatile reads). This allows multiple threads to read/write different buckets concurrently.

**Q: What's the difference between Java 7 and Java 8 ConcurrentHashMap?**
A: Java 7: 16 Segment locks (ReentrantLock), max 16 concurrent writers. Java 8+: per-bucket (synchronized on first node), much higher concurrency + lock-free reads + tree on collisions.

**Q: How does get() work without any lock?**
A: `table` is volatile, `Node.val` and `Node.next` are volatile, `tabAt()` uses `Unsafe.getObjectVolatile()`. The Java Memory Model guarantees that volatile reads see the latest write from any thread.

**Q: Why doesn't ConcurrentHashMap allow null keys/values?**
A: To avoid ambiguity in `get()`. If `get()` returns null, you can't tell if key is absent or value is null. In single-threaded HashMap, you can check `containsKey()`. But in concurrent code, the key might be added between `containsKey()` and `get()`.

**Q: What happens during resize?**
A: Other threads that try to put() during resize see `MOVED` nodes and help with the transfer. This is cooperative multithreading — resize completes faster with more helpers.

---

## 13. 30-Second Summary

```
ConcurrentHashMap = Thread-safe HashMap (Java 8+), per-bucket locking.

get():  lock-free → volatile reads → never blocked
put():  CAS for empty bucket, synchronized( firstNode ) for collision
resize: cooperative — multiple threads help transfer
size(): approximate (CounterCells + baseCount)

✅ Thread-safe                  ❌ No null keys/values
✅ Lock-free reads              ❌ size() is approximate
✅ Per-bucket writes            ❌ ~5-10% slower than HashMap
✅ Atomic ops (putIfAbsent, etc)❌ Not sorted

Best for: Multi-threaded caches, counters, lazy initialization
Avoid for: Single-threaded code, sorted iteration, null requirements

NOTE: Prefer Java 8+ version. Java 7 version uses segments (max 16 writers).