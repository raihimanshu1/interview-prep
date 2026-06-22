# ConcurrentHashMap — Complete Deep Dive

## 1. Why This Concept Matters

ConcurrentHashMap is the go-to thread-safe Map implementation in Java. It provides high concurrency by locking only a portion of the map (segments/buckets) rather than the entire structure. Understanding its internal segregation, lock-free reads, and CAS operations is essential for writing performant concurrent code. In production, ConcurrentHashMap is used for caches, configuration stores, and any shared mutable state accessed by multiple threads. Interviewers test this because it reveals your understanding of concurrent data structures, lock granularity, and the Java Memory Model.

Misunderstanding ConcurrentHashMap causes:
- Performance bottlenecks from coarse synchronization
- Race conditions from assuming atomicity of compound operations
- Memory consistency errors from incorrect expectations
- Using `Collections.synchronizedMap()` when ConcurrentHashMap is superior

## 2. Basic Meaning

ConcurrentHashMap is a hash-table based Map implementation supporting full concurrency for reads and adjustable concurrency for writes.

**Key vocabulary:**
- **Segmentation (Java 7)**: map divided into segments, each with its own lock
- **Synchronized blocks (Java 8+)**: synchronized on first node of each bin (bucket)
- **CAS (Compare-And-Swap)**: lock-free atomic operations for head node updates
- **`size()`**: approximate in Java 7+, sum of counter cells in Java 8+
- **`get()`**: lock-free, volatile read
- **`put()`**: locks only affected bucket
- **`Segment`**: in Java 7, a mini HashMap with its own lock

What it is NOT: ConcurrentHashMap is not a drop-in replacement for `Collections.synchronizedMap()` in all cases. It does NOT lock the entire map. It does NOT guarantee atomicity of compound operations like `putIfAbsent` + `get`.

## 3. Real Code / Real Example

```java
import java.util.concurrent.*;

public class ConcurrentHashMapDemo {
    public static void main(String[] args) throws InterruptedException {
        // === BASIC USAGE ===
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("Alice", 30);
        map.put("Bob", 25);
        map.put("Charlie", 35);
        System.out.println("Map: " + map);

        // === NULL KEYS/VALUES NOT ALLOWED ===
        try { map.put(null, 0); } catch (NullPointerException e) { System.out.println("NPE on null key"); }
        try { map.put("X", null); } catch (NullPointerException e) { System.out.println("NPE on null value"); }

        // === CONCURRENT WRITES ===
        ConcurrentHashMap<Integer, String> counter = new ConcurrentHashMap<>();
        Runnable writer = () -> {
            for (int i = 0; i < 100; i++) {
                counter.put(i, "val-" + i);
            }
        };
        Thread t1 = new Thread(writer);
        Thread t2 = new Thread(writer);
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("Counter size: " + counter.size()); // 100 (not 200, keys same)

        // === ATOMIC OPERATIONS ===
        ConcurrentHashMap<String, Integer> inventory = new ConcurrentHashMap<>();
        inventory.put("apple", 10);
        // putIfAbsent: atomic check-and-put
        Integer prev = inventory.putIfAbsent("apple", 20);
        System.out.println("Previous value: " + prev); // 10 (existing)
        System.out.println("After putIfAbsent: " + inventory.get("apple")); // 10 (not replaced)

        // replace: atomic compare-and-swap
        boolean replaced = inventory.replace("apple", 10, 99); // replace only if current == 10
        System.out.println("Replaced: " + replaced); // true
        System.out.println("After replace: " + inventory.get("apple")); // 99

        // === COMPOUND OPERATIONS (not atomic) ===
        ConcurrentHashMap<String, Integer> sales = new ConcurrentHashMap<>();
        sales.put("widget", 5);
        // NOT ATOMIC: read-modify-write
        Integer old = sales.get("widget");
        Integer newVal = (old == null) ? 1 : old + 1;
        sales.put("widget", newVal); // race condition possible here!

        // ATOMIC alternative: compute
        sales.compute("widget", (k, v) -> (v == null) ? 1 : v + 1);
        System.out.println("Sales: " + sales.get("widget"));

        // === ITERATION ===
        ConcurrentHashMap<String, Integer> iterMap = new ConcurrentHashMap<>();
        iterMap.put("A", 1); iterMap.put("B", 2); iterMap.put("C", 3);
        System.out.print("Iteration: ");
        for (String key : iterMap.keySet()) {
            System.out.print(key + "=" + iterMap.get(key) + " ");
            // Safe to modify map during iteration (concurrent collection)
            if (key.equals("A")) iterMap.put("D", 4);
        }
        System.out.println();

        // === SIZE AND EMPTY ===
        System.out.println("Size: " + iterMap.size()); // 4
        System.out.println("Empty: " + iterMap.isEmpty()); // false
    }
}
```

Expected output:
```
Map: {Alice=30, Bob=25, Charlie=35}
NPE on null key
NPE on null value
Counter size: 100
Previous value: 10
After putIfAbsent: 10
Replaced: true
After replace: 99
Sales: 6
Iteration: A=1 B=2 C=3 D=4 
Size: 4
Empty: false
```

## 4. What Happens Internally

**Java 8+ structure:**
```java
public class ConcurrentHashMap<K,V> {
    transient volatile Node<K,V>[] table;
    private transient volatile Node<K,V>[] nextTable; // for resize
    private transient volatile long baseCount;
    private transient volatile int transferStart; // resize coordination

    static class Node<K,V> {
        final int hash;
        final K key;
        volatile V value;       // volatile for lock-free reads
        volatile Node<K,V> next; // volatile for visibility
    }
}
```

**No segments in Java 8+:** Java 7 used `Segment` array (default 16 segments, each with its own lock). Java 8+ removed segments entirely — uses synchronized on first node of each bin, plus CAS for head updates.

**`get()` flow (lock-free):**
1. Compute hash: `hash = spread(key.hashCode())`
2. Compute index: `i = (table.length - 1) & hash`
3. Read `table[i]` (volatile read, sees latest value)
4. Traverse linked list/tree at bucket:
   - If `hash == hash` and `key.equals(node.key)` → return `node.value`
   - `value` is volatile: reads always see latest write
5. No locks acquired. Multiple threads can `get()` concurrently.

**`put()` flow (fine-grained lock):**
1. Compute hash, compute index
2. `synchronized (table[i])` — locks only the affected bucket
3. Traverse list:
   - If key exists: update value, return old
   - If not: insert new Node at head
4. If `size` exceeds `treeifyThreshold` (8): convert bin to tree
5. If `size >= threshold`: trigger resize (new table array, migrate entries)

**CAS operations for head insertion:**
```java
// Simplified: try to set new node as head without locking
if (U.compareAndSetObject(tab, i, f, newNode)) {
    // success: new node installed as head atomically
} else {
    // CAS failed: another thread inserted, fallback to synchronized
    synchronized (tab[i]) { ... insert ... }
}
```

**Resize in Java 8+:**
- Cooperative: all threads help migrate entries from old table to new
- `transfer()` method splits bins, moves entries to new positions
- Threads blocked on `put()` during resize participate in migration
- Uses `transferStart` to coordinate start

## 5. Tricky Interview Cases

**Case 1 — `putIfAbsent` atomicity**
```java
ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
// Thread A: cache.putIfAbsent("key", "valueA");
// Thread B: cache.putIfAbsent("key", "valueB");
// Result: exactly ONE succeeds, other gets null return
```
Output: Atomic. Only first writer wins.
Explanation: `putIfAbsent` locks the bucket during check-and-put. No interleaving.

**Case 2 — `compute()` atomicity**
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.compute("key", (k, v) -> v == null ? 1 : v + 1);
// Atomic: mapping function applied atomically per key
```
Output: Atomic increment.
Explanation: `compute` locks the bin for the duration of the mapping function. Other threads wait.

**Case 3 — `size()` is approximate**
```java
ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
map.put(1, "a");
map.put(2, "b");
// While size() runs, another thread adds 3
System.out.println(map.size()); // might be 2, 3, or more
```
Output: Non-deterministic count during concurrent modification.
Explanation: Java 7: `size()` sums segments but doesn't lock all. Java 8+: uses `baseCount` + `counterCells`, also not fully locked.

**Case 4 — `null` not allowed**
```java
ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
map.put("A", null);   // NullPointerException
map.put(null, "B");   // NullPointerException
```
Output: `NullPointerException` on both.
Explanation: `null` breaks atomicity. A thread reading `get(null)` couldn't distinguish between key absent and value-is-null.

**Case 5 — `replace()` atomicity**
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("A", 1);
boolean ok = map.replace("A", 1, 100); // true (replaces 1 with 100)
boolean fail = map.replace("A", 1, 200); // false (current is 100, not 1)
System.out.println(ok + ", " + fail); // true, false
```
Output: `true, false`
Explanation: `replace(key, oldVal, newVal)` is atomic compare-and-swap. Only succeeds if current value equals `oldVal`.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `get()` then `put()` expecting atomicity | Race condition between calls | Use `putIfAbsent`, `compute`, or `merge` |
| `size()` expecting exact count | Returns approximate value | Use `mappingCount()` or accept approximation |
| Assuming `put()` with existing key is atomic update | Concurrent puts on same key last-write-wins | Use `compute()` for atomic read-modify-write |
| Using `Collections.synchronizedMap` | Single lock, poor concurrency | Use `ConcurrentHashMap` instead |
| Iterating without copying | ConcurrentModification possible | Use entrySet iterator (weakly consistent) |
| `null` keys/values | NPE at runtime | Never store null in ConcurrentHashMap |

## 7. Production Usage

**Caching with computeIfAbsent:**
```java
LoadingCache<String, User> cache = Caffeine...
// Or manual with ConcurrentHashMap:
ConcurrentHashMap<String, User> cache = new ConcurrentHashMap<>();
User getUser(String id) {
    return cache.computeIfAbsent(id, key -> loadFromDb(key));
}
```
`computeIfAbsent` is atomic: only one thread loads, others wait and see cached result.

**Rate limiting:**
```java
class RateLimiter {
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    boolean allow(String userId, int maxTokens, long refillRate) {
        return buckets.computeIfAbsent(userId, k -> new TokenBucket(maxTokens, refillRate)).tryConsume();
    }
}
```

**Spring `@Cacheable` with ConcurrentHashMap:**
```java
@Cacheable("users")
public User findUser(String id) {
    return userRepo.findById(id);
}
// Simple cache manager uses ConcurrentHashMap internally
```

## 8. Advanced Details

- **Java 7 vs Java 8+ structure:** Java 7: `Segment<K,V>[]` (16 segments, each a mini HashMap). Java 8+: removed segments, uses `synchronized (node)` on bin head.
- **CAS operations:** Java 8+ uses `sun.misc.Unsafe.compareAndSetObject()` for lock-free head insertion. Falls back to `synchronized` if CAS fails.
- **Tree bins:** When bucket exceeds 8 nodes, converts to Red-Black tree (TreeNode). Reduces worst-case from O(n) to O(log n).
- **Forwarding nodes:** During resize, some bin entries become `ForwardingNode` — pointing to new table. Threads see forwarding and help migrate.
- **`sumCount()`:** Returns approximate `baseCount + sum(counterCells)`. Used by `size()`.
- **`Weakly consistent iterator:`** Reflects state at/after creation. Safe for concurrent modification (no CME).
- **`parallelStream()`:** Uses `ConcurrentHashMap` internally for reduction in parallel operations.

## 9. Interview Questions And Answers

### Beginner
Q: What is ConcurrentHashMap? How is it different from `Collections.synchronizedMap()`?
A: ConcurrentHashMap is a thread-safe Map with high concurrency. It locks only the affected bucket during writes (or uses CAS), allowing concurrent reads and writes to different buckets. `Collections.synchronizedMap()` uses a single lock for the entire map, serializing ALL operations.

### Intermediate
Q: Does `get()` in ConcurrentHashMap use locks? How does it guarantee visibility?
A: No, `get()` does NOT acquire locks. It uses volatile reads on `table[i]` and `node.value`. The `volatile` keyword ensures visibility: writes to `value` by one thread are immediately visible to reads by other threads.

### Senior
Q: `ConcurrentHashMap` does not allow `null` keys/values. Why? What would break if it did?
A: `null` would break atomicity guarantees:

If `map.put(key, null)` were allowed:
1. Thread A: `map.get(key)` returns `null`
2. Thread B: cannot distinguish between "key missing" and "value is null"
3. `putIfAbsent` logic breaks: `if (map.get(key) == null)` would be ambiguous

`null` is semantically ambiguous in concurrent context. Rejecting it forces explicit non-null contract.

### Tricky
Q: You have `ConcurrentHashMap<String, Integer> counts`. Thread A calls `counts.get("x") + 1`, Thread B does the same. Final value is less than 2. Why? How do you fix it?
A: `get()` + `+` + `put()` is NOT atomic. Two threads both read `0`, compute `1`, write `1`. Lost update.

```java
// BAD
Integer v = counts.get("x");
counts.put("x", v + 1); // race condition!

// GOOD: atomic read-modify-write
counts.compute("x", (k, v) -> v == null ? 1 : v + 1);

// OR: atomic integer values
ConcurrentHashMap<String, AtomicInteger> atomicCounts = new ConcurrentHashMap<>();
atomicCounts.computeIfAbsent("x", k -> new AtomicInteger()).incrementAndGet();
```

## 10. Final 30-Second Answer

ConcurrentHashMap = thread-safe HashMap with fine-grained locking (Java 8+: `synchronized` per bucket + CAS). `get()` is lock-free (volatile reads). `put()` locks affected bin. **No null keys/values.** Use `compute()`, `merge()`, `putIfAbsent()` for atomic operations — never separate `get()` + `put()`. `size()` is approximate. Java 7: segments. Java 8+: removed segments, tree bins at 8+ nodes. Weakly consistent iterator. Prefer over `Collections.synchronizedMap()`.