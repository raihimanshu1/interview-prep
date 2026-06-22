# HashMap Internals — Complete Deep Dive

## 1. Why This Concept Matters

HashMap is the most fundamental Map implementation in Java. Understanding its hash-based storage, collision resolution, resizing, and the equals/hashCode contract is essential for every Java developer. In production, HashMap is used everywhere — caching, indexing, grouping. Misunderstanding HashMap causes subtle bugs: missing keys, infinite loops during resize, and performance collapse from poor hash functions. Interviewers test HashMap because it reveals deep understanding of hashing, data structures, and the JMM.

Misunderstanding HashMap causes:
- Keys seemingly "lost" after resize (Java 7 infinite loop bug)
- O(n) performance from poor hashCode distribution
- Memory leaks from retained references (Java 7 substring issue)
- Broken contracts from incorrect equals/hashCode

## 2. Basic Meaning

HashMap stores key-value pairs in a hash table. Uses array of buckets (Node[]). Each bucket holds a linked list (or Red-Black tree at high collision).

**Key vocabulary:**
- **`table`**: internal `Node<K,V>[]` array (buckets)
- **`Node`**: entry holding hash, key, value, next pointer
- **`index`**: `(table.length - 1) & hash` — fast modulo
- **`capacity`**: length of `table` array (always power of 2)
- **`loadFactor`**: threshold = capacity × loadFactor (default 0.75)
- **`threshold`**: resize triggers when `size > threshold`
- **`hash()`**: spreads key.hashCode() bits to prevent collisions
- **`treeify`**: converts list to Red-Black tree at 8+ nodes
- **`untreeify`**: converts tree back to list on resize

What it is NOT: HashMap is not thread-safe. It is not ordered. It does not guarantee O(1) in worst case (collisions make it O(n) per bucket).

## 3. Real Code / Real Example

```java
import java.util.*;
import java.util.stream.Collectors;

public class HashMapDemo {
    public static void main(String[] args) {
        // === BASIC USAGE ===
        Map<String, Integer> map = new HashMap<>();
        map.put("Alice", 30);
        map.put("Bob", 25);
        map.put("Charlie", 35);
        System.out.println("Map: " + map);
        System.out.println("Size: " + map.size()); // 3

        // === NULL KEY AND VALUE ===
        map.put(null, 0);       // one null key allowed
        map.put("Dave", null);  // null value allowed
        System.out.println("With nulls: " + map);

        // === GET WITH DEFAULT ===
        System.out.println("Get Eve: " + map.getOrDefault("Eve", 18)); // 18

        // === COMPUTE IF ABSENT ===
        map.computeIfAbsent("Frank", k -> 28); // adds Frank=28
        map.computeIfAbsent("Alice", k -> 99); // Alice exists, NOT replaced
        System.out.println("After computeIfAbsent: " + map); // Alice=30

        // === COMPUTE IF PRESENT ===
        map.computeIfPresent("Bob", (k, v) -> v + 10); // Bob=25 → 35
        System.out.println("After computeIfPresent: " + map);

        // === MERGE ===
        map.merge("Charlie", 5, (old, add) -> old + add); // 35 + 5 = 40
        map.merge("George", 22, Integer::sum); // new entry
        System.out.println("After merge: " + map); // Charlie=40, George=22

        // === ITERATION ===
        System.out.print("Entries: ");
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            System.out.print(e.getKey() + "=" + e.getValue() + " ");
        }
        System.out.println();

        // === REPLACE ===
        map.replace("Alice", 31); // replace only if key exists
        System.out.println("After replace Alice: " + map.get("Alice")); // 31

        // === INITIAL CAPACITY AND LOAD FACTOR ===
        HashMap<String, String> sized = new HashMap<>(128, 0.8f);
        // table length = 128 (next power of 2 >= 128)
        // threshold = 128 * 0.8 = 102

        // === RESIZE IN ACTION ===
        HashMap<Integer, String> resizeDemo = new HashMap<>(4, 0.75f);
        // threshold = 4 * 0.75 = 3
        resizeDemo.put(1, "one");
        resizeDemo.put(2, "two");
        resizeDemo.put(3, "three");
        System.out.println("Size 3, capacity 4 (no resize): threshold=" + getThreshold(resizeDemo));
        resizeDemo.put(4, "four"); // triggers resize: 4 → 8
        System.out.println("After resize: capacity doubled to 8");

        // === HASH COLLISION ===
        HashMap<BadHash, String> collisionMap = new HashMap<>();
        collisionMap.put(new BadHash("A"), "valueA");
        collisionMap.put(new BadHash("B"), "valueB"); // same bucket!
        System.out.println("Collision map: " + collisionMap.size()); // 2, same bucket
    }

    static class BadHash {
        String key;
        BadHash(String k) { this.key = k; }
        @Override public int hashCode() { return 42; } // terrible: all same hash
        @Override public boolean equals(Object o) { return (o instanceof BadHash) && key.equals(((BadHash)o).key); }
    }

    static int getThreshold(HashMap<?, ?> map) {
        try {
            java.lang.reflect.Field f = HashMap.class.getDeclaredField("threshold");
            f.setAccessible(true);
            return (int) f.get(map);
        } catch (Exception e) { return -1; }
    }
}
```

Expected output:
```
Map: {Alice=30, Bob=25, Charlie=35}
Size: 3
With nulls: {null=0, Alice=30, Bob=25, Charlie=35, Dave=null}
Get Eve: 18
After computeIfAbsent: {null=0, Alice=30, Bob=25, Charlie=35, Dave=null, Frank=28}
After computeIfPresent: {null=0, Alice=30, Bob=35, Charlie=35, Dave=null, Frank=28}
After merge: {null=0, Alice=30, Bob=35, Charlie=40, Dave=null, Frank=28, George=22}
Entries: null=0 Alice=30 Bob=35 Charlie=40 Dave=null Frank=28 George=22 
After replace Alice: 31
Size 3, capacity 4 (no resize): threshold=3
After resize: capacity doubled to 8
Collision map: 2
```

## 4. What Happens Internally

**Node structure:**
```java
static class Node<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next; // next node in bucket (linked list)
}
```

**`put(K key, V value)` flow:**
1. If `table` null: `resize()` — initialize table
2. Compute hash: `hash = hash(key.hashCode())`
   - `hash(keyHash) = keyHash ^ (keyHash >>> 16)` (spread high bits)
3. Compute index: `i = (table.length - 1) & hash`
4. If `table[i]` is null: create new Node, set `table[i] = node`
5. If not null:
   - If `node.key.equals(key)`: update value, return old
   - Else: traverse linked list
     - If equal key found: update value, return old
     - If not: append new Node to list end
6. If `size++ > threshold`: `resize()`

**`get(Object key)` flow:**
1. Compute hash, compute index
2. If `table[i]` null: return null
3. If `table[i].hash == hash` and `table[i].key.equals(key)`: return value
4. If `table[i].next` not null: traverse linked list, check each node
5. If not found: return null

**`resize()` flow (Java 8):**
```java
final Node<K,V>[] oldTab = table;
int oldCap = oldTab.length; // e.g., 16
int newCap = oldCap << 1;   // 32
Node<K,V>[] newTab = (Node<K,V>[]) new Node[newCap];

for (int j = 0; j < oldCap; j++) {
    Node<K,V> e = oldTab[j];
    if (e != null) {
        // Unlink from old table
        oldTab[j] = null;
        if (e.next == null) {
            // Single node, no collision
            newTab[e.hash & (newCap - 1)] = e;
        } else if (e instanceof TreeNode) {
            // Tree node: split tree
            ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
        } else {
            // Linked list: split into two groups
            // Group 0: same index (hash & oldCap == 0)
            // Group 1: index + oldCap (hash & oldCap != 0)
            Node<K,V> loHead = null, loTail = null;
            Node<K,V> hiHead = null, hiTail = null;
            while (e != null) {
                if ((e.hash & oldCap) == 0) {
                    // stays at same index
                    if (loTail == null) loHead = e;
                    else loTail.next = e;
                    loTail = e;
                } else {
                    // moves to index + oldCap
                    if (hiTail == null) hiHead = e;
                    else hiTail.next = e;
                    hiTail = e;
                }
                e = e.next;
            }
            if (loHead != null) { loTail.next = null; newTab[j] = loHead; }
            if (hiHead != null) { hiTail.next = null; newTab[j + oldCap] = hiHead; }
        }
    }
}
table = newTab;
```

**`hash()` function:**
```java
static final int hash(Object key) {
    int h = key.hashCode();
    return (h == 0) ? 0 : h ^ (h >>> 16); // spread high bits to low
}
```
`(h >>> 16)` shifts 16 high bits down. XOR with low bits ensures high bits affect lower bits of index. Prevents collisions from table sizes where only low bits matter.

## 5. Tricky Interview Cases

**Case 1 — Java 7 infinite loop during resize (highly tested)**
```java
// Two threads trigger resize simultaneously
HashMap<Integer, String> map = new HashMap<>(4, 0.75f);
// Thread A: resize in progress
// Thread B: get() during resize
// OLD Java 7: linked list reversed during rehash → infinite loop in get()
```
Output: Java 7 HashMap.get() could loop infinitely.
Explanation: In Java 7, `resize()` reversed linked list order using `next` pointer. Concurrent resize caused cyclic linked list. Fix: Java 8 changed to preserve order using `loHead`/`hiHead`.

**Case 2 — Tree bins at high collision**
```java
HashMap<BadHash, String> map = new HashMap<>(16, 0.75f);
for (int i = 0; i < 16; i++) {
    map.put(new BadHash("key" + i), "val" + i);
}
System.out.println(map.size()); // 16, but single bucket is tree
```
Output: Single bucket becomes Red-Black tree.
Explanation: Same hashCode → same bucket. After 8 inserts, list converts to tree. `get()` O(log n) instead of O(n).

**Case 3 — `hashCode()` returning same value**
```java
class IntHash {
    int val;
    IntHash(int v) { this.val = v; }
    @Override public int hashCode() { return 42; }
    @Override public boolean equals(Object o) { return (o instanceof IntHash) && val == ((IntHash)o).val; }
}
HashMap<IntHash, String> map = new HashMap<>();
for (int i = 0; i < 100; i++) map.put(new IntHash(i), "val" + i);
// All go to same bucket (index 42 & 15 = 10)
// If threshold=12: resize to 32, redistribute (still mostly same bucket)
```
Output: All entries in same bucket, but still works.

**Case 4 — `getOrDefault` vs `computeIfAbsent`**
```java
Map<String, Integer> map = new HashMap<>();
// NOT atomic: two threads both see absent, both compute
Integer v1 = map.get("key");
if (v1 == null) map.put("key", computeExpensive());

// Atomic: computeIfAbsent locks bucket
map.computeIfAbsent("key", k -> computeExpensive());
```
Output: `computeIfAbsent` prevents duplicate computation.

**Case 5 — Capacity always power of 2**
```java
new HashMap<>(7) // table size = 8 (next power of 2)
new HashMap<>(16) // table size = 16
new HashMap<>(17) // table size = 32
```
Output: Always power of 2.
Explanation: Power-of-2 size enables `index = (length - 1) & hash` — faster than `%`. `tableSizeFor()` rounds up to nearest power of 2.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Not overriding hashCode | All objects go to same bucket, O(n) ops | Always override hashCode when overriding equals |
| Mutable key in HashMap | Key lost after mutation if hashCode changes | Use immutable keys |
| `new HashMap<>(n)` with small n | Rounds up to power of 2, may be larger than expected | Use `expectedSize / 0.75f + 1` |
| `HashMap` in multi-threaded | Infinite loop (Java 7), corruption (Java 8) | Use `ConcurrentHashMap` |
| Iterating while modifying | `ConcurrentModificationException` | Use iterator `remove()`, or `removeIf()` |
| Using HashMap for ordering | No order guarantee | Use `LinkedHashMap` or `TreeMap` |
| `keySet()` / `values()` / `entrySet()` modifying | Returns views, changes affect map | Treat as views; copy if needed |

## 7. Production Usage

**Caching:**
```java
LoadingCache<String, User> cache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build(key -> loadFromDatabase(key));
```
Underlying structure similar to ConcurrentHashMap.

**Grouping:**
```java
Map<String, List<Transaction>> byUser = transactions.stream()
    .collect(Collectors.groupingBy(Transaction::getUserId));
// HashMap-backed grouping
```

**Indexing:**
```java
Map<Integer, User> usersById = users.stream()
    .collect(Collectors.toMap(User::getId, u -> u));
// Fast O(1) lookup by ID
```

## 8. Advanced Details

- **Java 7 vs Java 8+ resize:** Java 7: `resize()` reversed list order. Java 8+: preserves order using `loHead`/`hiHead`. Java 7 had infinite loop bug under concurrent resize.
- **`tableSizeFor()`:** Rounds up to power of 2: `int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1); return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;`.
- **`TreeNode`:** Red-Black tree node. extends `LinkedHashMap.Entry`. Has parent, left, right, red flag.
- **`treeifyThreshold`:** 8 nodes → tree. `untreeifyThreshold`: 6 nodes → list (during resize).
- **`forwardingNodes`:** During resize, `newTab[j]` entries replaced with `ForwardingNode`. `get()` on forwarding node follows to new table.
- **`TREEIFY_THRESHOLD = 8`, `UNTREEIFY_THRESHOLD = 6`:** Between 6 and 8, tree stays tree.
- **`loadFactor` tradeoff:** 0.75 = balance. Lower → less collision, more memory. Higher → more collision, less memory.
- **`final int hash`:** Spreads hashCode bits. `hash ^ (hash >>> 16)` ensures high bits affect low bits.
- **`HashMap` vs `Hashtable`:** `Hashtable` is synchronized (legacy). `HashMap` not thread-safe (modern).

## 9. Interview Questions And Answers

### Beginner
Q: How does HashMap store and retrieve values? What is the time complexity?
A: `put()` computes hash from key, finds bucket via `(table.length-1) & hash`. If bucket empty: inserts Node. If collision: traverses linked list (or tree). If key equals existing key: updates value. `get()` computes same hash, finds bucket, traverses list. Average O(1), worst-case O(n) if all keys collide.

### Intermediate
Q: Why does HashMap use power-of-2 capacity? What is the purpose of `hash(keyHash) = keyHash ^ (keyHash >>> 16)`?
A: Power of 2 enables fast modulo: `index = (length - 1) & hash` instead of slow `%` operator.
`hash() ^ (hash >>> 16)`: spreads high 16 bits to low bits. Without this, collisions for keys where only low bits differ would cluster. XOR ensures high bits contribute to bucket selection.

### Senior
Q: In Java 7, concurrent `resize()` causes infinite loops. Explain the root cause and how Java 8 fixes it. What analogous issues exist in Java 8?
A: Java 7 `resize()` reversed linked list order by inserting at head during rehash:
```java
// Java 7 (simplified):
void transfer(Entry[] newTable) {
    for (Entry<K,V> e : oldTable) {
        while (e != null) {
            Entry<K,V> next = e.next;
            e.next = newTable[i]; // INSERT AT HEAD
            newTable[i] = e;
            e = next;
        }
    }
}
```
If two threads resize concurrently:
- Thread A processes node 1→2→3, ends up: 3→2→1
- Thread B processes: 1→2→3 (sees old next), also 3→2→1 but 2's next was already changed
- Result: cycle. 1→2→1→2...

Java 8 fix: Preserves order. Uses `loHead`/`hiTail` for stable insertion:
```java
if ((e.hash & oldCap) == 0) {
    // lo list: insert at tail
    if (loTail == null) loHead = e; else loTail.next = e;
    loTail = e;
}
```
Java 8 still NOT thread-safe, but no longer has infinite loop bug. ConcurrentHashMap must be used instead.

### Tricky
Q: `HashMap` allows one null key. How is `null` hash handled? What happens if you call `map.get(null)`?
A: `null` hash is hardcoded to 0:
```java
static final int hash(Object key) {
    int h = key.hashCode();
    return (h == 0) ? 0 : h ^ (h >>> 16);
}
```
`null.hashCode()` would NPE. JVM handles this: `hash(null)` returns 0.
`map.get(null)`: index = `(table.length-1) & 0` = 0. Always looks at `table[0]`. Only one `null` entry allowed (linked list check via `==`).

## 10. Final 30-Second Answer

HashMap = hash table with array of buckets (Node[]). `put()`: hash → bucket, collide via linked list/tree. `get()`: same path. **Capacity always power of 2** — `index = (length-1) & hash`. **`hash()` spreads bits** to reduce collisions. **Resize doubles capacity**, rehashes all entries. **Tree at 8+ collisions** (Red-Black). **LoadFactor 0.75**: balance memory vs collision. `null` key → hash 0. Not thread-safe. Java 7 resize had infinite loop bug (fixed in Java 8). Use `ConcurrentHashMap` for multi-threaded. **Always override hashCode + equals together**. `computeIfAbsent` for atomic lazy-init.