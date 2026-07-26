# TreeSet — Why? What? How? When?

## 1. The Problem Before TreeSet

### HashSet's Missing Feature: Sorted Order

HashSet gives you O(1) contains/add/remove, but you **cannot get elements in sorted order**:

```java
HashSet<Integer> set = new HashSet<>(List.of(5, 2, 8, 1, 3));
System.out.println(set);  // [1, 2, 3, 5, 8] — maybe sorted, maybe not!
// Next run: [2, 1, 5, 3, 8] — ORDER IS UNPREDICTABLE!
```

**Why can't HashSet be sorted?**
- Bucket order depends on hash codes
- Hash codes are designed for distribution, not ordering
- Resize changes bucket layout → changes order

### What developers did before TreeSet:

```java
// Manual sorting after using HashSet — tedious!
HashSet<Integer> set = new HashSet<>(List.of(5, 2, 8, 1, 3));
List<Integer> sorted = new ArrayList<>(set);
Collections.sort(sorted);  // O(n log n) every time!

// What if new elements added? Must sort again!
set.add(0);
sorted = new ArrayList<>(set);
Collections.sort(sorted);  // Sort again!

// What if you iterate a lot and add rarely?
// You're sorting every time — wasteful!
```

**Problems:**
- **Sorting every time**: O(n log n) overhead on iteration
- **Manual synchronization**: Must keep set and sorted list in sync
- **No range queries**: Can't get "elements between X and Y" efficiently

> **TreeSet was created to solve this**: Elements are ALWAYS sorted, with O(log n) operations, range queries, and no manual sorting.

---

## 2. What is TreeSet? (Simple Explanation)

```java
TreeSet<Integer> set = new TreeSet<>(List.of(5, 2, 8, 1, 3));
System.out.println(set);  // [1, 2, 3, 5, 8] — ALWAYS sorted!

set.add(0);
System.out.println(set);  // [0, 1, 2, 3, 5, 8] — still sorted automatically!

// Range queries — get elements between 2 and 5!
System.out.println(set.subSet(2, 6));  // [2, 3, 5] — O(log n)!

// Get first/last elements
System.out.println(set.first());  // 0
System.out.println(set.last());   // 8

// Get closest elements
System.out.println(set.higher(5));   // 8 (smallest > 5)
System.out.println(set.lower(5));    // 3 (largest < 5)
System.out.println(set.ceiling(4));  // 5 (smallest ≥ 4)
System.out.println(set.floor(4));    // 3 (largest ≤ 4)
```

**TreeSet = A sorted Set backed by a Red-Black Tree (TreeMap).**

Internal picture:
```
TreeSet (size=5)
    ↓ (backed by)
TreeMap<Integer, PRESENT>

              8 (BLACK)
            /   \
         3(RED)  (nothing)
        /   \
     2(B)   5(B)
    /
  1(R)
```

**Key insight**: TreeSet is backed by TreeMap (a Red-Black tree). Elements are stored as keys, PRESENT as values. The tree automatically maintains sorted order on every insertion/removal. No sorting needed — the tree IS always sorted.

---

## 3. How TreeSet Works Internally

### The Core Code

```java
public class TreeSet<E> implements NavigableSet<E>, Cloneable, Serializable {
    // The backing TreeMap — stores elements as keys
    private transient NavigableMap<E, Object> m;
    
    // Single dummy value (same trick as HashSet)
    private static final Object PRESENT = new Object();
    
    public TreeSet() {
        this(new TreeMap<>());  // Uses natural ordering (Comparable)
    }
    
    public TreeSet(Comparator<? super E> comparator) {
        this(new TreeMap<>(comparator));  // Custom ordering
    }
    
    public boolean add(E e) {
        return m.put(e, PRESENT) == null;  // Same pattern as HashSet!
    }
    
    public boolean contains(Object o) {
        return m.containsKey(o);
    }
    
    public boolean remove(Object o) {
        return m.remove(o) == PRESENT;
    }
    
    // NavigableSet methods:
    public E first() { return m.firstKey(); }
    public E last() { return m.lastKey(); }
    public E lower(E e) { return m.lowerKey(e); }      // < e
    public E floor(E e) { return m.floorKey(e); }      // ≤ e
    public E ceiling(E e) { return m.ceilingKey(e); }  // ≥ e
    public E higher(E e) { return m.higherKey(e); }    // > e
    public NavigableSet<E> subSet(E from, boolean fromInclusive, E to, boolean toInclusive) {
        return m.subMap(from, fromInclusive, to, toInclusive).navigableKeySet();
    }
}
```

---

## 4. How TreeSet Maintains Order: The Red-Black Tree

TreeSet is backed by a **Red-Black Tree** — a self-balancing binary search tree.

### Binary Search Tree (BST) Rule

```
For any node:
  Left subtree  → all values LESS than node
  Right subtree → all values GREATER than node
  
Example with insertions in order: 5, 2, 8, 1, 3

  5 ← root
 / \
2   8
/ \
1  3

In-order traversal (left → root → right):
  1 → 2 → 3 → 5 → 8  ← always sorted!
```

### Red-Black Tree Balance Rules

```
A Red-Black tree adds color (RED/BLACK) to each node with 5 rules:
1. Every node is RED or BLACK
2. Root is always BLACK
3. All leaves (null) are BLACK
4. RED nodes can only have BLACK children (no two REDs in a row)
5. Any path from root to leaf has the SAME number of BLACK nodes

These rules keep the tree balanced: longest path ≤ 2 × shortest path
→ O(log n) for all operations!
```

### When insertion breaks balance — Rotations

```
Problem: Inserting 1 after 5→3→2 creates an unbalanced chain:
    5
   /
  3
 /
2

Tree must rotate to rebalance (using RED-BLACK rules):
    3
   / \
  2   5
```

Rotations are O(1) and maintain the sorted order property.

---

## 5. TreeSet: Methods Beyond HashSet

| Method | Description | Example |
|--------|-------------|---------|
| `first()` | Smallest element | `set.first()` → 1 |
| `last()` | Largest element | `set.last()` → 8 |
| `lower(e)` | Largest < e | `set.lower(5)` → 3 |
| `floor(e)` | Largest ≤ e | `set.floor(5)` → 5 |
| `ceiling(e)` | Smallest ≥ e | `set.ceiling(4)` → 5 |
| `higher(e)` | Smallest > e | `set.higher(5)` → 8 |
| `pollFirst()` | Remove and return smallest | |
| `pollLast()` | Remove and return largest | |
| `subSet(from, to)` | Range view [from, to) | `subSet(2, 6)` → [2, 3, 5] |
| `headSet(to)` | Elements < to | `headSet(4)` → [1, 2, 3] |
| `tailSet(from)` | Elements ≥ from | `tailSet(4)` → [5, 8] |
| `descendingSet()` | Reverse order view | |

---

## 6. TreeSet vs HashSet vs LinkedHashSet

| Aspect | **HashSet** | **LinkedHashSet** | **TreeSet** |
|--------|------------|-------------------|-------------|
| **Add** | O(1)* | O(1)* | **O(log n)** |
| **Contains** | **O(1)** avg | **O(1)** avg | **O(log n)** |
| **Remove** | **O(1)** avg | **O(1)** avg | **O(log n)** |
| **Order** | None | Insertion order | **Sorted** |
| **Nulls** | ✅ One | ✅ One | ❌ No |
| **Range queries** | ❌ No | ❌ No | **✅ Yes** |
| **First/Last** | ❌ No | ❌ No | **✅ O(log n)** |
| **Memory** | Low | Medium | Medium |
| **Internal** | HashMap | LinkedHashMap | **TreeMap (R-B tree)** |

> *amortized O(1)

---

## 7. TreeSet: Advantages, Limitations, When to Use, When Not to Use

### ✅ Advantages

| Advantage | Why |
|-----------|-----|
| **Always sorted** | No manual sorting — tree maintains order |
| **O(log n) operations** | Red-Black tree = balanced binary search |
| **Range queries** | `subSet()`, `headSet()`, `tailSet()` at O(log n) |
| **Navigation methods** | `lower()`, `higher()`, `ceiling()`, `floor()` |
| **First/Last** | `first()`, `last()` in O(log n) |

### ❌ Limitations

| Limitation | Why |
|------------|-----|
| **Slower than HashSet** | O(log n) vs O(1) |
| **No null elements** | TreeMap doesn't allow null keys |
| **Requires Comparable/Comparator** | Must define ordering |
| **Not thread-safe** | Same as other collections |
| **Higher overhead** | Tree nodes are more complex than HashMap nodes |

### 🟢 When to Use

```java
// 1. Always need sorted iteration
TreeSet<String> sortedNames = new TreeSet<>();
sortedNames.add("Charlie");
sortedNames.add("Alice");
sortedNames.add("Bob");
// Always prints: Alice, Bob, Charlie
for (String name : sortedNames) { ... }

// 2. Range queries
TreeSet<Integer> scores = new TreeSet<>(List.of(55, 72, 88, 91, 65, 43));
// Get scores from 60 to 80 (passing range)
NavigableSet<Integer> passing = scores.subSet(60, true, 80, true);  // [65, 72]

// 3. Find closest values
TreeSet<Integer> numbers = new TreeSet<>(List.of(10, 20, 30, 40, 50));
System.out.println(numbers.ceiling(25));  // 30 (next available ≥ 25)
System.out.println(numbers.floor(25));   // 20 (previous available ≤ 25)

// 4. Leaderboard / ranking
TreeSet<Score> leaderboard = new TreeSet<>(Comparator.comparingInt(Score::points).reversed());
leaderboard.add(new Score("Alice", 100));
leaderboard.add(new Score("Bob", 85));
leaderboard.add(new Score("Charlie", 95));
// Alice (100) > Charlie (95) > Bob (85)
System.out.println("First: " + leaderboard.first());  // Alice
```

### 🔴 When NOT to Use

```java
// 1. Performance-critical, no sorting needed — use HashSet
TreeSet<Integer> slow = new TreeSet<>();  // O(log n)
HashSet<Integer> fast = new HashSet<>();  // O(1)

// 2. Insertion order preservation — use LinkedHashSet
// TreeSet sorts, it doesn't preserve insertion order

// 3. Need null elements — TreeSet doesn't allow null

// 4. Need cheap contains() — HashSet is faster
```

---

## 8. Comparable vs Comparator in TreeSet

### Natural Order (Comparable)

```java
// Elements implement Comparable — defines "natural order"
class Person implements Comparable<Person> {
    String name;
    int age;
    
    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public int compareTo(Person other) {
        return this.name.compareTo(other.name);  // Sort by name
    }
}

TreeSet<Person> people = new TreeSet<>();  // Uses compareTo()
people.add(new Person("Alice", 30));
people.add(new Person("Bob", 25));
// Sorted by name: Alice, Bob
```

### Custom Order (Comparator)

```java
// Comparator — define order without modifying the class
TreeSet<Person> byAge = new TreeSet<>(Comparator.comparingInt(Person::age));
byAge.add(new Person("Alice", 30));
byAge.add(new Person("Bob", 25));
// Sorted by age: Bob (25), Alice (30)

// Multiple criteria:
TreeSet<Person> byAgeThenName = new TreeSet<>(
    Comparator.comparingInt(Person::age)
              .thenComparing(Person::name)
);

// Reverse:
TreeSet<Person> byAgeDesc = new TreeSet<>(
    Comparator.comparingInt(Person::age).reversed()
);
```

### Critical: compareTo MUST be Consistent with equals

```java
// ❌ BAD: compareTo uses age, equals uses name
class BadPerson {
    String name;
    int age;
    
    @Override
    public boolean equals(Object o) {
        return o instanceof BadPerson && Objects.equals(name, ((BadPerson) o).name);
    }
    
    @Override
    public int compareTo(BadPerson o) {
        return Integer.compare(this.age, o.age);  // Different field!
    }
}

TreeSet<BadPerson> set = new TreeSet<>();
set.add(new BadPerson("Alice", 30));
set.add(new BadPerson("Bob", 30));
System.out.println(set.size());  // 2? 1? Depends!
// compareTo says Bob=30 == Alice=30 → SAME in TreeSet
// equals says "Alice" != "Bob" → DIFFERENT
// TreeSet uses compareTo, not equals, for uniqueness!
```

**Rule**: If `compareTo()` returns 0, TreeSet considers the elements equal (even if `equals()` says otherwise). For predictable behavior, ensure `compareTo` is consistent with `equals`.

---

## 9. Common Pitfalls

| Mistake | Why It's Wrong | Correct Approach |
|---------|---------------|-----------------|
| **compareTo inconsistent with equals** | TreeSet uses compareTo for uniqueness, not equals | Ensure compareTo returns 0 iff equals is true |
| **Comparator that changes after insertion** | Can't find element later | Use immutable fields |
| **Null elements** | NullPointerException | Check before adding |
| **Assuming O(1) like HashSet** | TreeSet is O(log n) — noticeable difference for large sets | Profile if performance matters |
| **Mutable fields in compareTo** | Lost element after mutation | Use immutable keys |

---

## 10. Interview Quick Reference

**Q: How does TreeSet maintain sorted order?**
A: Backed by TreeMap (Red-Black tree). Each insertion finds the correct position in the balanced BST. The tree automatically rotates to stay balanced, guaranteeing O(log n) operations.

**Q: What's the difference between TreeSet and HashSet?**
A: TreeSet: sorted, O(log n), range queries, requires Comparable/Comparator, no nulls. HashSet: unsorted, O(1), no range queries, requires equals/hashCode, allows one null.

**Q: How do range queries work in TreeSet?**
A: `subSet(from, to)` returns a view of elements between from and to. The view is backed by the original set — changes to the original are reflected in the view. Uses the Red-Black tree's ability to quickly find the fromKey and iterate.

**Q: Can TreeSet have duplicate elements?**
A: No. TreeSet uses `compareTo()`/`compare()` to determine equality. If two elements compare as equal (compareTo returns 0), they are considered duplicates — the second is rejected.

**Q: What happens if you add a null to TreeSet?**
A: NullPointerException. TreeMap doesn't allow null keys because `compareTo(null)` would throw NPE.

---

## 11. 30-Second Summary

```
TreeSet = Sorted Set backed by TreeMap (Red-Black tree).

Elements ALWAYS sorted. O(log n) add/contains/remove.

Key features:
  - first(), last(), lower(), higher(), ceiling(), floor()
  - subSet(), headSet(), tailSet() — range queries
  - ascending and descending iteration
  - Requires Comparable (natural order) or Comparator (custom order)

✅ Always sorted              ❌ O(log n) slower than O(1) HashSet
✅ Range queries              ❌ No null elements
✅ Navigation methods         ❌ Requires Comparable/Comparator
✅ pollFirst()/pollLast()     ❌ Not thread-safe

Best for: Sorted iteration, nearest-value search, range queries
Avoid for: Fast membership tests, insertion order preservation, nulls