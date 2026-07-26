# HashSet — Why? What? How? When?

## 1. The Problem Before HashSet

### The Need for Uniqueness

Before HashSet, if you wanted a collection of **unique** elements (no duplicates), you had to manually check:

```java
// Checking for duplicates manually — tedious!
List<String> list = new ArrayList<>();
list.add("Alice");
list.add("Bob");
list.add("Alice");  // Duplicate allowed!

// Must manually prevent duplicates:
if (!list.contains("Alice")) {  // O(n) check!
    list.add("Alice");
}
```

**Problems with checking duplicates in a List:**
- **O(n) contains()**: Must scan every element to check if it exists
- **Manual enforcement**: No built-in uniqueness guarantee
- **Tedious code**: Every addition needs a `contains()` check

### What about using a Map with dummy values?

Developers would sometimes use a HashMap with dummy values:

```java
// Using HashMap just to get unique elements — hack!
Map<String, Boolean> uniqueNames = new HashMap<>();
uniqueNames.put("Alice", true);   // Value is irrelevant
uniqueNames.put("Bob", true);
uniqueNames.put("Alice", true);   // Replaces old entry — duplicate ignored!
// To get the unique elements:
Set<String> names = uniqueNames.keySet();  // The keys are the unique set
```

**Problems with this approach:**
- **Confusing**: A Map for a Set-like purpose
- **Wasted value**: You allocate a value object (Boolean) for every entry
- **Verbose**: Every operation needs map semantics

> **HashSet was created to solve this**: A Set backed by HashMap, giving you O(1) add/contains/remove with automatic deduplication, without needing to manage dummy values.

---

## 2. What is HashSet? (Simple Explanation)

```java
HashSet<String> names = new HashSet<>();
names.add("Alice");   // true — new element
names.add("Bob");     // true — new element
names.add("Alice");   // false — duplicate! Ignored silently
System.out.println(names.size());  // 2 (not 3!)
System.out.println(names.contains("Alice"));  // true — O(1)!
```

**HashSet = A collection with NO duplicates, backed by a HashMap.**

Internal picture:
```
HashSet (size=3)
    ↓ (backed by)
HashMap<String, PRESENT>   [PRESENT = a dummy object, same for all keys]
┌─────┬────────────────────────────┐
│  0  │ null                       │
│  1  │ Node{key="Alice", value=●} │  ← "Alice" exists
│  2  │ null                       │
│  3  │ Node{key="Bob", value=●}   │  ← "Bob" exists
│ ... │                            │
│ 15  │ null                       │
└─────┴────────────────────────────┘
         ● = PRESENT (same Object instance for ALL entries)
```

**Key insight**: HashSet uses HashMap internally. Each element you add becomes a **key** in the HashMap. A single static dummy object `PRESENT` is used as the **value** for ALL entries. This means:
- Uniqueness = HashMap's key uniqueness (via hashCode + equals)
- O(1) operations = HashMap's O(1) get/put
- No wasted values = single PRESENT object shared everywhere

---

## 3. How HashSet Works Internally (Basic Implementation)

### The Core Code

```java
public class HashSet<E> implements Set<E> {
    // The backing HashMap — stores elements as keys
    private transient HashMap<E, Object> map;
    
    // A single dummy value shared by ALL entries
    private static final Object PRESENT = new Object();
    
    public HashSet() {
        map = new HashMap<>();  // Default capacity 16, load factor 0.75
    }
    
    public boolean add(E e) {
        // e becomes the KEY, PRESENT is the VALUE
        // HashMap.put() returns null if key didn't exist (success!)
        // HashMap.put() returns old value if key existed (duplicate!)
        return map.put(e, PRESENT) == null;
    }
    
    public boolean contains(Object o) {
        return map.containsKey(o);  // O(1) — uses HashMap's hash/lookup
    }
    
    public boolean remove(Object o) {
        // HashMap.remove() returns the value (PRESENT) if found
        return map.remove(o) == PRESENT;
    }
    
    public int size() {
        return map.size();
    }
    
    public void clear() {
        map.clear();
    }
}
```

### Step-by-step execution:

```java
HashSet<String> set = new HashSet<>();
// Step 1: map = new HashMap<>() (empty, capacity=16)

set.add("Apple");
// Step 2: map.put("Apple", PRESENT)
//         HashMap computes hash("Apple") → index → bucket empty → store Node{"Apple", PRESENT}
//         HashMap.put() returns null → HashSet.add() returns true

set.add("Banana");
// Step 3: map.put("Banana", PRESENT)
//         Different hash → different bucket → store Node{"Banana", PRESENT}
//         Returns true

set.add("Apple");
// Step 4: map.put("Apple", PRESENT)
//         Same hash as before → same bucket → key "Apple" already exists
//         HashMap.put() returns the OLD value (PRESENT) → returns PRESENT
//         PRESENT == null? NO → HashSet.add() returns false (duplicate!)

set.contains("Apple");
// Step 5: map.containsKey("Apple")
//         Same hash → same bucket → "Apple".equals("Apple")? YES → true
//         O(1) !

set.remove("Banana");
// Step 6: map.remove("Banana")
//         HashMap removes the entry → returns PRESENT
//         PRESENT == PRESENT? YES → return true
```

### Why does `add()` check `== null`?

```java
return map.put(e, PRESENT) == null;
//                         ↑
//           HashMap.put() returns null if key was NEW
//           HashMap.put() returns OLD value (PRESENT) if key EXISTED
```

**This is the clever trick**: HashMap's `put()` returns:
- `null` → key was NOT in the map (newly inserted) → `add()` returns `true`
- Non-null (PRESENT) → key WAS already in the map → `add()` returns `false`

---

## 4. The PRESENT Dummy Object

```java
private static final Object PRESENT = new Object();
```

**Why a single shared object?**
- Non-null: HashMap allows null values, so we can't use null (it would mean "no mapping")
- Shared: Only ONE Object instance for ALL entries. No per-entity allocation overhead.
- Unused: The value is never accessed. We only care about the KEY.

**Memory comparison:**
```
Without PRESENT trick (storing true/false per entry):
  HashSet size 1M → 1M Boolean objects = ~16 MB wasted
  
With PRESENT trick:
  HashSet size 1M → 1 PRESENT object = ~16 bytes, shared!
```

---

## 5. HashSet vs Alternatives

| Aspect | **HashSet** | **TreeSet** | **LinkedHashSet** | **List** |
|--------|------------|-------------|-------------------|----------|
| **Add** | O(1)* | O(log n) | O(1)* | O(1) or O(n) |
| **Contains** | **O(1)** avg | **O(log n)** | **O(1)** avg | **O(n)** |
| **Remove** | **O(1)** avg | **O(log n)** | **O(1)** avg | **O(n)** |
| **Order** | None | **Sorted** | **Insertion order** | Insertion order |
| **Duplicates** | ❌ No | ❌ No | ❌ No | ✅ Yes |
| **Nulls** | ✅ One | ❌ No (NPE) | ✅ One | ✅ Yes |
| **Internal** | HashMap | TreeMap | LinkedHashMap | Array/Node |

> *amortized O(1) due to resize

---

## 6. HashSet: Advantages, Limitations, When to Use, When Not to Use

### ✅ Advantages

| Advantage | Why |
|-----------|-----|
| **O(1) contains** | Backed by HashMap — instant membership test |
| **Automatic deduplication** | No manual duplicate checking needed |
| **Fast add/remove** | O(1) average — simple hash-based operations |
| **Null allowed** | One null element stored at table[0] |

### ❌ Limitations

| Limitation | Why |
|------------|-----|
| **No order guarantee** | Bucket order, changes with resize |
| **Not thread-safe** | Same as HashMap — concurrent modification corrupts |
| **Requires equals/hashCode** | Broken contracts cause duplicates to appear |
| **Mutable keys break it** | Changing fields used in hashCode loses the element |
| **No indexes** | Can't do `get(0)` — no positional access |

### 🟢 When to Use

```java
// 1. Check if element exists — fast membership test
HashSet<String> blockedUsers = new HashSet<>();
blockedUsers.add("spammer123");
if (blockedUsers.contains(userId)) {  // O(1)!
    rejectRequest();
}

// 2. Deduplicate a list
List<Integer> numbers = List.of(1, 2, 2, 3, 3, 3, 4);
HashSet<Integer> unique = new HashSet<>(numbers);  // [1, 2, 3, 4]
// Or back to a list: new ArrayList<>(unique)

// 3. Set operations (union, intersection, difference)
HashSet<Integer> set1 = new HashSet<>(List.of(1, 2, 3));
HashSet<Integer> set2 = new HashSet<>(List.of(2, 3, 4));

HashSet<Integer> union = new HashSet<>(set1);
union.addAll(set2);        // [1, 2, 3, 4]

HashSet<Integer> intersection = new HashSet<>(set1);
intersection.retainAll(set2);  // [2, 3]

HashSet<Integer> difference = new HashSet<>(set1);
difference.removeAll(set2);    // [1]

// 4. Cache keys — which IDs have been processed?
HashSet<Long> processedIds = new HashSet<>();
if (processedIds.add(eventId)) {  // true if first time
    processEvent(eventId);
}
```

### 🔴 When NOT to Use

```java
// 1. Need order — use LinkedHashSet or TreeSet
HashSet<String> set = new HashSet<>();
// ORDER IS UNPREDICTABLE! Don't rely on it.

// 2. Need sorted elements — use TreeSet
HashSet<Integer> nums = new HashSet<>(List.of(5, 2, 8, 1));
// Can't get sorted iteration

// 3. Multi-threaded access — use ConcurrentHashMap.newKeySet()
HashSet<String> unsafe = new HashSet<>();
// Concurrent modification → corruption!
Set<String> safe = ConcurrentHashMap.newKeySet();  // Thread-safe!

// 4. Small collections (< 5-10 elements)
// List + linear scan might be faster and simpler
```

---

## 7. Common Pitfalls

### Mutable Keys — The "Lost Element" Bug

```java
class Person {
    String name;
    Person(String n) { this.name = n; }
    
    @Override public int hashCode() { return Objects.hash(name); }
    @Override public boolean equals(Object o) {
        return o instanceof Person && Objects.equals(name, ((Person) o).name);
    }
}

HashSet<Person> set = new HashSet<>();
Person p = new Person("Alice");
set.add(p);

p.name = "Bob";  // MUTATE the key!

System.out.println(set.contains(p));  // false! hashCode changed!
// "Alice" entry still in bucket for old hash. "Bob" hash → different bucket.
// p is PERMANENTLY LOST in the set. Memory leak!
```

### Forgetting hashCode()

```java
class User {
    String id;
    User(String id) { this.id = id; }
    
    @Override public boolean equals(Object o) {
        return o instanceof User && Objects.equals(id, ((User) o).id);
    }
    // ❌ NO hashCode() — uses Object.hashCode() (memory address)
}

HashSet<User> set = new HashSet<>();
User u1 = new User("123");
User u2 = new User("123");  // "Equal" by equals()

set.add(u1);
set.add(u2);  // BOTH added because different memory → different hash → different bucket!
System.out.println(set.size());  // 2 — but should be 1!
```

---

## 8. HashSet Performance Tuning

### Initial Capacity Matters

```java
// If you know you'll store 10,000 elements:
HashSet<String> bad = new HashSet<>();          // Starts at 16, resizes many times!
HashSet<String> good = new HashSet<>(16000);    // Fewer resizes

// Rule: expectedSize / 0.75 + 1 → avoid resize
// 10000 / 0.75 + 1 = 13334 → next power of 2 = 16384
```

### Load Factor

```java
// Default: 0.75 — good for most cases
// Higher (0.9): saves memory, slower lookups (more collisions)
// Lower (0.5): faster lookups, wastes memory (more buckets, fewer entries)
```

---

## 9. Thread Safety: ConcurrentHashSet Pattern

```java
// HashSet is NOT thread-safe:
HashSet<String> set = new HashSet<>();
new Thread(() -> set.add("A")).start();
new Thread(() -> set.add("B")).start();  // Corrupted!

// Option 1: Synchronized wrapper
Set<String> syncSet = Collections.synchronizedSet(new HashSet<>());

// Option 2: ConcurrentHashMap.newKeySet() (Java 8+)
Set<String> concurrentSet = ConcurrentHashMap.newKeySet();

// Option 3: CopyOnWriteArraySet (read-heavy, small sets)
Set<String> cowSet = new CopyOnWriteArraySet<>();
```

---

## 10. Interview Quick Reference

**Q: How does HashSet ensure uniqueness?**
A: HashSet is backed by HashMap. When `add(e)` is called, it does `map.put(e, PRESENT)`. The HashMap uses `hashCode()` to find the bucket and `equals()` to check if the key already exists. If it does, `put()` returns the old value (non-null) and `add()` returns `false` (duplicate).

**Q: What is PRESENT?**
A: A static dummy Object used as the value for every key in the backing HashMap. It's shared across all entries to avoid per-entry value allocation.

**Q: How to prevent duplicates in HashSet?**
A: Override `hashCode()` and `equals()` correctly. If `a.equals(b)` is true, `a.hashCode() == b.hashCode()` must be true. Use the same canonical fields in both.

**Q: Why does set.contains() not find an element after insertion?**
A: Two common causes: (1) The element was mutated after insertion, changing its hashCode. (2) The class didn't override hashCode()/equals() properly.

---

## 11. 30-Second Summary

```
HashSet = Set backed by HashMap. Elements stored as MAP KEYS.

add(e):   return map.put(e, PRESENT) == null   → true if new, false if duplicate
contains(e):  return map.containsKey(e)         → O(1)
remove(e):    return map.remove(e) == PRESENT   → O(1)

PRESENT = static Object, same instance for all entries (no wasted values).

✅ O(1) add/contains/remove    ❌ No order guarantee
✅ Automatic deduplication     ❌ Not thread-safe
✅ One null allowed            ❌ Requires equals/hashCode

implements: Set, Cloneable, Serializable
backed by:  HashMap

Best for: Fast membership tests, deduplication, set operations
Avoid for: Ordered iteration (use LinkedHashSet), sorting (use TreeSet)

</final_content>
</write_to_file>