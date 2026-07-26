# 📖 2. ArrayList — The Workhorse of Java Collections

## 🎯 What Will You Learn?

| You Will Learn | You Will NOT Learn |
|---------------|-------------------|
| What problem does ArrayList solve? (Story) | Just interface definitions |
| Intuition: Why is get() O(1) but remove() O(n)? | Memorizing without understanding |
| Internal array, 1.5x growth, modCount | |
| Every operation's time complexity (with WHY) | |
| **7+ years interview questions with depth** | |

---

## 1. 📖 REAL LIFE STORY: "The Parking Lot"

### The Problem

Imagine you own a parking lot with **exactly 10 spots**.

```
Day 1: Cars arrive → [🚗][🚗][🚗][  ][  ][  ][  ][  ][  ][  ]  3 cars
Day 2: More arrive → [🚗][🚗][🚗][🚗][🚗][  ][  ][  ][  ][  ]  5 cars
Day 3: Even more →   [🚗][🚗][🚗][🚗][🚗][🚗][🚗][🚗][  ][  ]  8 cars
Day 4: "Sorry, we're full!" → Only 10 spots!

Customer: "I need 11 spots!"
You: "Let me buy the empty land next door and expand..."
```

**This is exactly what happens with a regular Java array:**

```java
String[] parkingLot = new String[10];  // Fixed size: 10
parkingLot[0] = "Car A";
// ... 9 more cars ...
parkingLot[10] = "Car K";  // ❌ ArrayIndexOutOfBoundsException!
```

**Without ArrayList**, you'd have to:

```java
// Step 1: Buy new land (create bigger array)
String[] biggerLot = new String[15];

// Step 2: Move all cars (copy elements)
System.arraycopy(parkingLot, 0, biggerLot, 0, 10);

// Step 3: Now park the 11th car
biggerLot[10] = "Car K";

// Step 4: Forget the old lot (old array gets garbage collected)
parkingLot = biggerLot;
```

**ArrayList automates ALL of this.** You just call `add()` and it handles everything.

---

## 2. 💡 INTUITION: What Happens Inside

### Picture an ArrayList in Memory

```
ArrayList<String> list = new ArrayList<>();

After adding "A", "B", "C":
  Internal array (capacity = 10):
  ┌─────┬─────┬─────┬──────┬──────┬──────┬──────┬──────┬──────┬──────┐
  │ "A" │ "B" │ "C" │ null │ null │ null │ null │ null │ null │ null │
  └─────┴─────┴─────┴──────┴──────┴──────┴──────┴──────┴──────┴──────┘
    ↑     ↑     ↑                          
    |     |     |                          
  data[0] data[1] data[2]    size = 3 (3 elements used)
```

### Why Operations Cost What They Do

```
✅ GET: list.get(1)
   ┌─────┬─────┬─────┐
   │ "A" │ "B" │ "C" │   →  Jump directly to index 1 → O(1)
   └─────┴─────┴─────┘
     ↑
   data[0] + 1 * (size of reference)

✅ ADD AT END: list.add("D")
   [A][B][C][D][ ][ ][ ]   →  Just put at data[size] → O(1)*
              ↑
           data[3] = "D"

❌ REMOVE AT FRONT: list.remove(0)
   Before: [A][B][C][D][ ]
   Remove A → Shift everything left:
   After:  [B][C][D][ ][ ]   →  Each element moves → O(n)

❌ INSERT IN MIDDLE: list.add(1, "X")
   Before: [A][B][C][D][ ]
   Insert at 1 → Shift B, C, D right:
   After:  [A][X][B][C][D]   →  Each element moves → O(n)
```

### Visual Analogy: Bookshelf

```
ArrayList = A bookshelf with numbered slots:

  get(2)  → Reach directly to slot 2, grab book → O(1)  ⚡
  add("Z") at end → Put book at first empty slot → O(1) ⚡
  remove(0) → Pull first book, push ALL others left → O(n) 🐢
  insert(0, "Z") → Push ALL books right, put at front → O(n) 🐢
```

---

## 3. 🔄 INTUITION → CODE (Step by Step)

### Step 1: The Simplest Version

```java
// Intuition: "I need a resizable parking lot"

public class SimpleArrayList<E> {
    private Object[] data;   // The parking lot (array)
    private int size = 0;    // How many cars are parked
    
    public SimpleArrayList() {
        data = new Object[10];  // Start with 10 spots
    }
}
```

### Step 2: Add — "Park a Car"

```java
// Intuition: "Put the car in the first empty spot"
// If full, buy more land first

public void add(E element) {
    if (size == data.length) {
        grow();  // Parking lot is full! Need expansion.
    }
    data[size] = element;  // Park at first empty spot
    size++;                // Track how many cars
}
```

### Step 3: Get — "Which Car is at Slot X?"

```java
// Intuition: "Walk to slot 3 and look"
// DIRECT access — no searching needed!

public E get(int index) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
    return (E) data[index];  // Direct jump — like array[index]
}
```

### Step 4: Remove — "Move Car #1 Out"

```java
// Intuition: "Remove car at spot 1, then push ALL cars after it LEFT one spot"

public E remove(int index) {
    E oldValue = (E) data[index];
    
    // How many cars need to shift left?
    int carsToShift = size - index - 1;
    
    if (carsToShift > 0) {
        // Shift: copy from (index+1) to (index)
        // Like: [A][B][C][D] remove(1) → copy from pos 2 to pos 1, 2 elements
        System.arraycopy(data, index + 1, data, index, carsToShift);
    }
    
    data[--size] = null;  // Clear last spot (help garbage collector)
    return oldValue;
}
```

### Step 5: Grow — "Expand the Parking Lot"

```java
// Intuition: "Buy adjacent land, move all cars, then park new ones"

private void grow() {
    int oldCapacity = data.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);  // 1.5x = old + (old/2)
    // Example: 10 → 15 → 22 → 33 → 49 → 73 → ...
    
    data = Arrays.copyOf(data, newCapacity);  // New array, copy all cars
}
```

---

## 4. ⚙️ INTERNAL WORKING: Full Source Code Walkthrough

### The Real ArrayList (Simplified from JDK)

```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    
    // THE CORE: An array of objects
    transient Object[] elementData;
    
    // Number of elements (NOT array length!)
    private int size;
    
    // Default: start with 10 empty spots
    private static final int DEFAULT_CAPACITY = 10;
    
    // Constructor: creates the array
    public ArrayList() {
        this.elementData = new Object[DEFAULT_CAPACITY];
        // elementData = [null, null, null, null, null, null, null, null, null, null]
        // size = 0
    }
    
    // ===== OPERATIONS =====
    
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Check if room
        elementData[size++] = e;           // Put at end, increment size
        return true;
    }
    
    public void add(int index, E element) {
        rangeCheckForAdd(index);           // Validate index
        ensureCapacityInternal(size + 1);  // Check if room
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        // Shift elements RIGHT from index
        elementData[index] = element;      // Insert new element
        size++;
    }
    
    public E get(int index) {
        rangeCheck(index);
        return elementData(index);         // return elementData[index];
    }
    
    public E remove(int index) {
        rangeCheck(index);
        E oldValue = elementData(index);
        int numMoved = size - index - 1;   // Count elements after removed one
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index, numMoved);
        elementData[--size] = null;        // Clear last for GC
        return oldValue;
    }
    
    public boolean contains(Object o) {
        return indexOf(o) >= 0;            // Linear scan!
    }
    
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i] == null) return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i])) return i;  // SCANS one by one!
        }
        return -1;
    }
    
    // ===== GROWTH =====
    
    private void ensureCapacityInternal(int minCapacity) {
        if (minCapacity > elementData.length) {
            grow(minCapacity);
        }
    }
    
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);  // 1.5x
        // If 1.5x is still not enough, use minCapacity
        if (newCapacity < minCapacity) newCapacity = minCapacity;
        // Cap at max array size
        if (newCapacity > MAX_ARRAY_SIZE) newCapacity = MAX_ARRAY_SIZE;
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
    
    // ===== MOD COUNT (for fail-fast iteration) =====
    protected transient int modCount = 0;
    // Every structural modification: modCount++
    // (add, remove, clear — anything that changes size)
    // NOT for set() — that's NOT structural
}
```

### The modCount Mechanism: Fail-Fast Iteration

```java
// How ArrayList knows you modified it during iteration:
// Each iterator stores expectedModCount at creation
// On each next() or remove(), checks modCount == expectedModCount

private class Itr implements Iterator<E> {
    int expectedModCount = modCount;
    int cursor;       // Index of next element
    int lastRet = -1; // Index of last returned element
    
    public E next() {
        checkForComodification();  // ← CRITICAL CHECK
        int i = cursor;
        if (i >= size) throw new NoSuchElementException();
        Object[] elementData = ArrayList.this.elementData;
        if (i >= elementData.length) throw new ConcurrentModificationException();
        cursor = i + 1;
        return (E) elementData[lastRet = i];
    }
    
    final void checkForComodification() {
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }
}
```

### Why Custom Serialization?

```java
// Default serialization would write the ENTIRE elementData array
// Including the null slots (capacity - size many nulls)
// That's wasteful!

// ArrayList's custom serialization only writes the actual elements:
private void writeObject(java.io.ObjectOutputStream s) {
    s.defaultWriteObject();
    s.writeInt(size);                    // Write how many real elements
    for (int i = 0; i < size; i++)
        s.writeObject(elementData[i]);   // Only write real elements, skip nulls
}
```

---

## 5. 📊 TIME COMPLEXITY ANALYSIS (With Explanations)

| Operation | Time | Why? |
|-----------|------|------|
| **get(int index)** | **O(1)** 🔥 | Direct array access: `elementData[index]` — instant |
| **set(int index, E e)** | **O(1)** 🔥 | Same: `elementData[index] = e` |
| **add(E e)** (at end) | **O(1)*** | Just store at `elementData[size]` — usually instant |
| **add(int index, E e)** | **O(n)** | Shift all elements after index right |
| **remove(int index)** | **O(n)** | Shift all elements after index left |
| **remove(Object o)** | **O(n)** | First scan to find (O(n)), then shift (O(n)) |
| **contains(Object o)** | **O(n)** | Must scan entire array: `for (i in 0..size)` |
| **indexOf(Object o)** | **O(n)** | Same linear scan |
| **size()** | **O(1)** | Just return `size` field |
| **isEmpty()** | **O(1)** | Return `size == 0` |
| **clear()** | **O(n)** | Must null out each slot: `for (i in 0..size) data[i] = null` |
| **iterator()** | **O(1)** | Just create iterator object |
| **iteration (full)** | **O(n)** | Visit each element once |
| **subList()** | **O(1)** | Just creates a view (not a copy!) |
| **trimToSize()** | **O(n)** | Copy to new array of exact size |
| **ensureCapacity()** | **O(n)** | May trigger grow + copy |

* = amortized O(1). Occasionally O(n) when array grows and all elements are copied.

### Why Amortized O(1) for add()?

Let's trace adding n elements starting with capacity = 1:

```
Add 1: capacity=1 → full after add → grow to 2, copy 1  → total copies = 1
Add 2: capacity=2 → full after add → grow to 3, copy 2  → total copies = 3
Add 3: capacity=3 → full after add → grow to 4, copy 3  → total copies = 6
Add 4: capacity=4 → full after add → grow to 6, copy 4  → total copies = 10
Add 5: capacity=6 → fine
Add 6: capacity=6 → full → grow to 9, copy 6             → total copies = 16
...

Total copies after n additions ≈ n (the sum of occasional resizes is ~n)
Average per addition = total_copies / n ≈ 1
Therefore: O(1) amortized!
```

---

## 6. ✅ WHEN TO USE / ❌ WHEN NOT TO USE

### ✅ When ArrayList is Perfect

```java
// 1. Reading data by index (most common use)
ArrayList<Student> students = new ArrayList<>();
// ... load 1000 students ...
Student s = students.get(500);  // O(1) — instant!

// 2. Appending data (build a list)
ArrayList<String> logs = new ArrayList<>();
logReader.forEach(line -> logs.add(line));  // O(1) each

// 3. Small to medium collections (< 10K)
// ArrayList has lowest overhead per element

// 4. Sorting data
Collections.sort(list);  // Efficient: copies to array, sorts, copies back
```

### ❌ When to Avoid ArrayList

```java
// 1. Frequent front insertions — AVOID!
ArrayList<Integer> list = new ArrayList<>();
for (int i = 0; i < 10000; i++) {
    list.add(0, i);  // O(n) EACH! Total: O(n²) — 50 million shifts!
}
// Use: LinkedList or ArrayDeque

// 2. Queue operations (FIFO) — AVOID!
list.add("A");       // O(1) 
list.remove(0);      // O(n) — shifts all left!
// Use: ArrayDeque — O(1) add/remove both ends

// 3. Multi-threaded access — AVOID without sync!
// Two threads calling add() simultaneously corrupts size
// Use: CopyOnWriteArrayList, Collections.synchronizedList(), or explicit locks

// 4. Need O(1) contains() — AVOID!
if (list.contains("needle")) {  // O(n) scan every time!
    // ...
}
// Use: HashSet for O(1) contains
```

---

## 7. 🧪 COMMON MISTAKES & EDGE CASES

### Mistake 1: remove(int) vs remove(Object)

```java
ArrayList<Integer> list = new ArrayList<>(List.of(1, 2, 3));

list.remove(1);    // Removes at INDEX 1 → removes "2"!
// list is now: [1, 3]

// To remove by VALUE:
list.remove(Integer.valueOf(1));  // Removes value 1
// list is now: [3]
```

**Why**: Java always calls the most specific method. `remove(int)` matches the primitive int overload before `remove(Object)`.

### Mistake 2: The subList() Trap

```java
ArrayList<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));
List<String> sub = list.subList(1, 3);  // ["B", "C"] — VIEW, not copy!

list.add("E");  // ⚠️ Structural modification of parent!
sub.get(0);     // ❌ ConcurrentModificationException!
```

**Why**: `subList()` returns a VIEW backed by the parent. Any structural change to parent invalidates the view.

**Fix**: Create a new ArrayList from the sublist:
```java
List<String> safeCopy = new ArrayList<>(list.subList(1, 3));  // Independent copy
```

### Mistake 3: Arrays.asList() is NOT a Regular List

```java
String[] arr = {"A", "B", "C"};
List<String> list = Arrays.asList(arr);

list.set(0, "X");   // ✅ Works, arr[0] becomes "X"
list.add("D");      // ❌ UnsupportedOperationException!

// Also: list.remove(0), list.clear() → all throw!
```

**Why**: `Arrays.asList()` returns a fixed-size list backed by the array. No structural modifications allowed.

### Mistake 4: Forgetting Initial Capacity

```java
// BAD: Will resize many times for large data
ArrayList<String> bad = new ArrayList<>();
for (int i = 0; i < 1_000_000; i++) bad.add("item" + i);
// Resizes: 10→15→22→33→... about 30 resizes!

// GOOD: Pre-size!
ArrayList<String> good = new ArrayList<>(1_000_000);
for (int i = 0; i < 1_000_000; i++) good.add("item" + i);
// No resizes!
```

### Mistake 5: removeIf() vs Looping

```java
// BAD: O(n²) — each remove() shifts elements!
for (int i = 0; i < list.size(); i++) {
    if (list.get(i).startsWith("X")) list.remove(i);
    // After remove, elements shift, i gets out of sync!
}

// GOOD: O(n) — single pass
list.removeIf(s -> s.startsWith("X"));
// Java 8+ uses internal efficient batch removal

// Also GOOD: O(n) with iterator
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().startsWith("X")) it.remove();
}
```

---

## 8. 🎯 7+ YEARS EXPERIENCE INTERVIEW QUESTIONS

### Q1: "Why does ArrayList use 1.5x growth instead of 2x? Explain the math."

**🔍 What the interviewer is testing**: 
Understanding of space-time tradeoffs, amortized analysis, and real JDK design decisions.

**💡 Simple Answer**: 
"2x doubles memory faster → less copying but more wasted space. 1.5x is a balance between copying cost and memory waste."

**🔬 Deep Explanation**:
```java
// FACT: ArrayList grows by: newCapacity = oldCapacity + (oldCapacity >> 1)
// That's exactly: new = old + old/2 = old * 1.5

// WHY NOT 2x?

// 1. MEMORY WASTE
// With 2x: After last grow, the wasted space equals the CURRENT size
// Example: size=1000, capacity was 1024, grew to 2048
//   → wasted = 2048 - 1000 = 1048 slots! (>100% waste!)

// With 1.5x: After last grow, wasted space ~ 50% of current size
// Example: size=1000, capacity was 1024, grew to 1536
//   → wasted = 1536 - 1000 = 536 slots (~50% waste)

// 2. AMORTIZED ANALYSIS
// With 2x: Amortized cost per add = O(1), with ~2 copies per element on average
// With 1.5x: Amortized cost per add = O(1), with ~3 copies per element on average
// The difference is small! (2 vs 3 copies)

// 3. THE REAL REASON: Memory fragmentation
// With 2x, the array doubles each time:
//   10 → 20 → 40 → 80 → 160 → ...
// Each growth requires finding a CONTIGUOUS block TWICE the previous size
// In fragmented heaps, this becomes impossible sooner!

// With 1.5x:
//   10 → 15 → 22 → 33 → 49 → 73 → 109 → 163 → ...
// Growth is more gradual — easier to find contiguous memory

// 4. PRACTICAL TEST (from JDK mailing list):
// Joshua Bloch (original author) chose 1.5x based on empirical testing
// as the sweet spot between CPU time (copies) and memory (wasted slots)
```

### Q2: "How would you implement a thread-safe ArrayList without using synchronized everywhere?"

**🔍 What they're testing**: 
Concurrent programming knowledge, lock-free techniques, trade-off analysis.

**💡 Simple Answer**: 
"Use CopyOnWriteArrayList for read-heavy, or ReadWriteLock for balanced workloads."

**🔬 Deep Explanation**:
```java
// OPTION 1: CopyOnWrite (Java's choice for read-heavy)
// Every write creates a new copy of the array
// Reads are NEVER blocked — always O(1)

class CopyOnWriteArrayList<E> {
    private volatile Object[] array;  // volatile for visibility
    
    public E get(int index) {
        return (E) array[index];  // No lock! Always safe
    }
    
    public synchronized boolean add(E e) {  // Only writes synchronize
        Object[] old = array;
        Object[] newArray = Arrays.copyOf(old, old.length + 1);
        newArray[old.length] = e;
        array = newArray;  // Atomic update → readers see it instantly
        return true;
    }
}

// OPTION 2: ReadWriteLock (for read-write balanced)
class ReadWriteArrayList<E> {
    private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private Object[] data = new Object[10];
    private int size = 0;
    
    public E get(int index) {
        rw.readLock().lock();   // Multiple readers allowed
        try { return (E) data[index]; }
        finally { rw.readLock().unlock(); }
    }
    
    public void add(E e) {
        rw.writeLock().lock();  // Exclusive access
        try {
            if (size == data.length) grow();
            data[size++] = e;
        } finally { rw.writeLock().unlock(); }
    }
}
```

### Q3: "You have 10 million integers. Remove all odd numbers. Most efficient way?"

**🔍 What they're testing**: 
Performance optimization, understanding memory copies, JVM behavior.

**💡 The Insight**:
The most efficient way is a two-pointer approach that does it in ONE pass without creating a new array.

**🔬 Solution**:
```java
// NAIVE: O(n) but shifts many times internally
list.removeIf(n -> n % 2 != 0);

// MOST EFFICIENT: Two-pointer in-place
static void removeOddsInPlace(ArrayList<Integer> list) {
    int writePos = 0;
    for (int readPos = 0; readPos < list.size(); readPos++) {
        Integer val = list.get(readPos);
        if (val % 2 == 0) {  // Even number → keep
            list.set(writePos, val);
            writePos++;
        }
    }
    // Remove the tail elements
    list.subList(writePos, list.size()).clear();
}

// What happens:
// [1, 2, 3, 4, 5, 6]
//  R↓
//  W↓  1 is odd → skip
//  [1, 2, 3, 4, 5, 6]
//     R↓
//  W↓  2 is even → write, W++
//  [2, 2, 3, 4, 5, 6]
// ...

// Time: O(n) — ONE pass, minimum copying!
// Space: O(1) — in-place!
```

### Q4: "Why is `new ArrayList<>(list.subList(0, n))` different from `list.subList(0, n)`?"

**🔍 What they're testing**: 
Understanding of view vs. copy, memory implications, and the subList contract.

**💡 Simple Answer**: 
"subList is a VIEW (not a copy). The constructor creates an INDEPENDENT copy."

**🔬 Deep Explanation**:
```java
List<String> original = new ArrayList<>(List.of("A", "B", "C", "D"));

// VIEW: points to original's internal array (No data copied)
List<String> view = original.subList(1, 3);  // ["B", "C"]

// COPY: has its OWN internal array (Data is copied)
List<String> copy = new ArrayList<>(original.subList(1, 3));  // ["B", "C"]

// Now modify original:
original.add("E");  // Invalidates view!
view.get(0);        // ❌ ConcurrentModificationException!
copy.get(0);        // ✅ "B" — independent!

// MEMORY:
// view: O(1) memory — just start/end indices
// copy: O(n) memory — copies all elements
```

### Q5: "You have an OutOfMemoryError with ArrayList. How do you diagnose and fix?"

**🔍 What they're testing**: 
Real production debugging, JVM memory management, profiling skills.

**💡 The Approach**:
```
Step 1: Is it really memory? Check with -Xmx and heap dump
Step 2: Is ArrayList growing unbounded? Check for infinite loops adding data
Step 3: Is capacity too large? Use trimToSize() after loading
Step 4: Consider alternatives (streaming, database, file-based)
```

**🔬 Diagnosis**:
```bash
# 1. Run with heap dump on OOM:
java -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -jar app.jar

# 2. Analyze the heap dump with Eclipse MAT or JProfiler
# Look for:
#   - ArrayList objects with huge elementData arrays
#   - Who holds references to these lists?
#   - What's the GC root chain?

# 3. Common causes in production:
// Cause 1: Loading entire DB table into memory
ArrayList<Record> allRecords = jdbcTemplate.query("SELECT * FROM HUGE_TABLE");
// Fix: Use pagination: LIMIT 1000 OFFSET ?

// Cause 2: Log collector that never clears
static ArrayList<String> allLogs = new ArrayList<>();
// Fix: Use bounded collection or clear periodically

// Cause 3: 1.5x growth leaves 50% overhead after last add
ArrayList<String> list = new ArrayList<>(1_000_000);
// ... load 1,000,000 elements ...
// Capacity is ~1,000,000, but ArrayList grew to 1,500,000 internally
// Solution: list.trimToSize(); → capacity becomes exactly 1,000,000
```

---

## 🎯 Final Summary in 30 Seconds

```
ArrayList = Resizable array (Object[]). Starts at capacity 10, grows 1.5x when full.

✅ O(1) get/set by index        ❌ O(n) insert/remove in middle
✅ O(1)* add at end             ❌ Not thread-safe
✅ Low memory + cache-friendly  ❌ O(n) contains/search
✅ Implements RandomAccess      ❌ Needs equals/hashCode

Internal: elementData[] array, size counter, modCount for fail-fast iterators

Best for: Fast random access, append-heavy, predictable iteration
Avoid for: Front insertions, FIFO queues, concurrent access (without sync)
```

---

> **Next Topic**: [LinkedList →](../linkedlist/README.md)
>
> Now compare ArrayList with its cousin: LinkedList!