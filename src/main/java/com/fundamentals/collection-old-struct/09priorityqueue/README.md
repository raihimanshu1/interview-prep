# PriorityQueue — Why? What? How? When?

## 1. The Problem Before PriorityQueue

### Regular Queues Process in FIFO Order

A regular queue (like LinkedList or ArrayDeque) processes elements in **insertion order** (FIFO):

```java
Queue<Task> queue = new LinkedList<>();
queue.offer(new Task("Normal", 5));
queue.offer(new Task("Urgent", 1));
queue.offer(new Task("Low", 10));

while (!queue.isEmpty()) {
    Task t = queue.poll();
    System.out.println(t.name);  // Normal, Urgent, Low — FIFO order!
}
```

But what if **urgent tasks need to be processed first**?

```java
// Manual priority handling — tedious!
List<Task> tasks = new ArrayList<>();
tasks.add(new Task("Normal", 5));
tasks.add(new Task("Urgent", 1));
tasks.add(new Task("Low", 10));

while (!tasks.isEmpty()) {
    // Find highest priority task — O(n) each time!
    Task highest = tasks.get(0);
    for (Task t : tasks) {
        if (t.priority < highest.priority) highest = t;  // Lower = more urgent
    }
    tasks.remove(highest);  // O(n) remove
    process(highest);
}
// Total: O(n²) — terrible for large queues!
```

**What developers did before PriorityQueue:**

```java
// Sort every time you add — O(n log n) per insertion
List<Task> sortedTasks = new ArrayList<>();

void addTask(Task t) {
    sortedTasks.add(t);
    Collections.sort(sortedTasks);  // O(n log n) every time!
}

// Or maintain sorted insertion — O(n) per insertion
void addTaskSorted(Task t) {
    int i = 0;
    while (i < sortedTasks.size() && sortedTasks.get(i).priority < t.priority) {
        i++;
    }
    sortedTasks.add(i, t);  // O(n) insert
}
```

**Problems:**
- **Sort every time**: O(n log n) — expensive
- **Manual sorting**: Error-prone, hard to maintain
- **O(n) insertion**: Finding the right position requires scanning
- **No efficient "peek at highest priority"**: Must scan entire list

> **PriorityQueue was created to solve this**: Always retrieve the **highest priority** element in O(log n) time, without manual sorting.

---

## 2. What is PriorityQueue? (Simple Explanation)

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(5);
pq.offer(1);
pq.offer(3);

System.out.println(pq.poll());  // 1 — smallest (highest priority) first!
System.out.println(pq.poll());  // 3
System.out.println(pq.poll());  // 5

// Custom priority — Tasks with lowest priority number first:
PriorityQueue<Task> tasks = new PriorityQueue<>(Comparator.comparingInt(Task::priority));
tasks.offer(new Task("Normal", 5));
tasks.offer(new Task("Urgent", 1));
tasks.offer(new Task("Low", 10));

while (!tasks.isEmpty()) {
    Task t = tasks.poll();  // Always returns highest priority (lowest number)
    System.out.println(t.name);  // Urgent, Normal, Low
}
```

**PriorityQueue = A queue where the element with the HIGHEST priority (smallest value by default) is always at the front.**

Internal picture:
```
PriorityQueue (min-heap):
         1 (root = smallest = highest priority)
       /   \
      3     5
     / \
    7   9

poll() → removes 1, heapifies → new root = 3
peek() → returns 1 (O(1))
offer(2) → adds at bottom, bubbles up → new root = 1? No, 1 still smaller
```

**Key insight**: PriorityQueue uses a **binary heap** — a complete binary tree stored as an array. The smallest element is ALWAYS at the root (index 0). Every insertion and removal maintains the heap property.

---

## 3. How PriorityQueue Works Internally: The Binary Heap

### The Heap Structure

```java
public class PriorityQueue<E> extends AbstractQueue<E> {
    transient Object[] queue;  // The heap array
    private int size = 0;
    private final Comparator<? super E> comparator;
    
    // Default initial capacity: 11
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
}
```

### Array Representation of a Binary Tree

```
Heap (complete binary tree):
         1 (index 0)          ← smallest element
       /   \
      3     5 (indices 1, 2)
     / \   /
    7  9  8 (indices 3, 4, 5)

Array: [1, 3, 5, 7, 9, 8, ...]
        ↑  ↑  ↑  ↑  ↑  ↑
        0  1  2  3  4  5

For any node at index i:
  left child  = 2*i + 1
  right child = 2*i + 2
  parent      = (i - 1) / 2
```

**No node objects!** Just a flat array. Children/parent are computed by index math. This is why PriorityQueue is memory-efficient.

### Heap Property

```java
// MIN-HEAP (default): parent <= both children
// For any i: queue[i] <= queue[2*i+1] AND queue[i] <= queue[2*i+2]

// This means the SMALLEST element is ALWAYS at queue[0] (root)
```

### Simplified PriorityQueue:

```java
public class SimplePriorityQueue<E> {
    private Object[] heap;
    private int size = 0;
    private Comparator<? super E> comparator;
    
    public SimplePriorityQueue() {
        this.heap = new Object[11];  // Default capacity
    }
    
    // Add element — O(log n)
    public boolean offer(E e) {
        if (size == heap.length) grow();
        
        heap[size] = e;      // Add at the end
        siftUp(size);         // Bubble up to correct position
        size++;
        return true;
    }
    
    // Remove and return smallest — O(log n)
    public E poll() {
        if (size == 0) return null;
        
        E result = (E) heap[0];   // Save smallest (root)
        heap[0] = heap[size - 1]; // Move last element to root
        heap[size - 1] = null;    // Help GC
        size--;
        siftDown(0);               // Bubble down to restore heap
        
        return result;
    }
    
    // Peek at smallest — O(1)!
    public E peek() {
        return (E) heap[0];
    }
    
    // Bubble up — O(log n)
    private void siftUp(int index) {
        E key = (E) heap[index];
        
        while (index > 0) {
            int parent = (index - 1) / 2;  // Parent index
            E parentVal = (E) heap[parent];
            
            if (compare(key, parentVal) >= 0) break;  // Correct position
            
            // Swap with parent
            heap[index] = parentVal;
            index = parent;
        }
        heap[index] = key;
    }
    
    // Bubble down — O(log n)
    private void siftDown(int index) {
        E key = (E) heap[index];
        int half = size / 2;
        
        while (index < half) {
            int child = 2 * index + 1;  // Left child
            int right = child + 1;
            
            // Find smaller child
            if (right < size && compare((E) heap[right], (E) heap[child]) < 0) {
                child = right;  // Right child is smaller
            }
            
            if (compare(key, (E) heap[child]) <= 0) break;  // Correct position
            
            // Swap with smaller child
            heap[index] = heap[child];
            index = child;
        }
        heap[index] = key;
    }
    
    private int compare(E a, E b) {
        if (comparator != null) return comparator.compare(a, b);
        return ((Comparable<E>) a).compareTo(b);
    }
}
```

### Step-by-step: Adding elements

```java
SimplePriorityQueue<Integer> pq = new SimplePriorityQueue<>();

pq.offer(5);
// Heap: [5, _, _, ...] — root = 5

pq.offer(3);
// Heap: [5, 3, _, ...]
// 3 added at end. siftUp(1): compare 3 with parent(5) → 3 < 5 → swap!
// Heap: [3, 5, _, ...] — root = 3 (smaller)

pq.offer(7);
// Heap: [3, 5, 7, _, ...]
// 7 added at end. siftUp(2): compare 7 with parent(1) = 5 → 7 > 5 → stop
// Heap: [3, 5, 7, ...] — correct

pq.offer(1);
// Heap: [3, 5, 7, 1, ...]
// 1 added at end. siftUp(3): compare 1 with parent(1) = 5 → 1 < 5 → swap!
// Heap: [3, 1, 7, 5, ...]
// siftUp(1): compare 1 with parent(0) = 3 → 1 < 3 → swap!
// Heap: [1, 3, 7, 5, ...] — root = 1 (smallest!)

System.out.println(pq.peek());  // 1 — O(1)!
```

### Step-by-step: Removing elements (poll)

```java
pq.poll();
// Save result = heap[0] = 1
// Move last element to root: heap[0] = heap[3] = 5
// Heap: [5, 3, 7, _, ...]
// siftDown(0): compare 5 with children (3, 7) → 3 is smaller
//   → swap 5 with 3
// Heap: [3, 5, 7, ...]
// siftDown(1): compare 5 with children (7) → 5 < 7 → stop
// Return 1

// Next poll:
// Result = 3, move 7 to root
// Heap: [7, 5, _, ...]
// siftDown(0): compare 7 with child (5) → 5 < 7 → swap
// Heap: [5, 7, ...]
// Return 3
```

---

## 4. The Heap Visualization

```
Adding 5:
  [5]

Adding 3:
  [5, 3] → siftUp: 3 < 5 → swap
  [3, 5]  ← heap property restored (root = smallest)

Adding 7:
  [3, 5, 7] → 7 > parent(5) → OK
  [3, 5, 7]

Adding 1:
  [3, 5, 7, 1] → 1 < parent(5) → swap
  [3, 1, 7, 5] → 1 < parent(3) → swap
  [1, 3, 7, 5] ← root = 1 (smallest)

Final tree:
         1
       /   \
      3     7
     /
    5

Poll (remove root 1):
  Last element (5) moves to root:
         5
       /   \
      3     7
    
  siftDown: 5 > child(3) → swap
         3
       /   \
      5     7    ← heap restored
```

---

## 5. PriorityQueue vs Other Structures

| Aspect | **PriorityQueue** | **TreeSet** | **LinkedList (sorted)** |
|--------|-----------------|-------------|------------------------|
| **Add** | **O(log n)** | **O(log n)** | **O(n)** (find + insert) |
| **Poll** | **O(log n)** | O(log n) via first() + remove | **O(1)** poll first |
| **Peek** | **O(1)** | O(log n) | **O(1)** |
| **contains** | O(n) | **O(log n)** | O(n) |
| **Duplicates** | ✅ Yes | ❌ No | ✅ Yes |
| **Nulls** | ❌ No | ❌ No | ✅ Yes |
| **Order all elements** | ✅ Only smallest | ✅ All sorted | ✅ All sorted |
| **Memory** | **Low** (array) | Medium (tree) | Low |
| **Internal** | **Binary heap (array)** | Red-Black tree | Array/Node |

---

## 6. PriorityQueue: Advantages, Limitations, When to Use, When Not to Use

### ✅ Advantages

| Advantage | Why |
|-----------|-----|
| **O(log n) offer/poll** | Binary heap — always balanced |
| **O(1) peek** | Smallest element always at root |
| **Low memory** | Flat array, no node objects |
| **Duplicate friendly** | Allows equal-priority elements |
| **Flexible ordering** | Natural order or custom Comparator |

### ❌ Limitations

| Limitation | Why |
|------------|-----|
| **Not thread-safe** | Same as other collections |
| **No nulls** | Throws NPE |
| **O(n) contains** | Must scan entire array |
| **No order guarantee on iteration** | `toString()` or iterator is NOT sorted |
| **No fixed capacity** | Always unbounded |
| **Not a FIFO queue** | Order = priority, not insertion |

### 🟢 When to Use

```java
// 1. Task scheduling — always process highest priority first
PriorityQueue<Task> scheduler = new PriorityQueue<>(Comparator.comparingInt(Task::priority));
scheduler.offer(new Task("Backup", 10));
scheduler.offer(new Task("Critical fix", 1));
scheduler.offer(new Task("Report", 5));

while (!scheduler.isEmpty()) {
    process(scheduler.poll());  // Critical fix → Report → Backup
}

// 2. Find top K elements from a stream
PriorityQueue<Integer> topK = new PriorityQueue<>(k);  // Min-heap of size k
for (int num : stream) {
    topK.offer(num);
    if (topK.size() > k) topK.poll();  // Remove smallest → keep largest k
}
// topK now contains K LARGEST elements

// 3. Dijkstra's shortest path algorithm
// PriorityQueue to always process closest node first

// 4. Median finder — two heaps
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
// Add to maxHeap (lower half), balance to minHeap (upper half)
// median = maxHeap.peek() (or average of both peaks)

// 5. Merge K sorted lists
// Add all heads to PQ, repeatedly poll smallest, add next from that list
```

### 🔴 When NOT to Use

```java
// 1. Need FIFO order — use ArrayDeque or LinkedList
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(5);
pq.offer(1);
pq.offer(3);
System.out.println(pq.poll());  // 1 (smallest), not 5 (first inserted)

// 2. Need sorted iteration of ALL elements
PriorityQueue<Integer> pq2 = new PriorityQueue<>(List.of(5, 1, 3, 8, 2));
for (int n : pq2) {
    System.out.println(n);  // NOT sorted! May be [1, 2, 3, 8, 5] or similar
}
// Only poll() returns in sorted order!
// If you need sorted all elements, use TreeSet or Collections.sort()

// 3. Need O(1) contains or fast search — use HashSet
```

---

## 7. Important: PriorityQueue Iterator is NOT Sorted

```java
PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 3, 8, 2));
System.out.println(pq);  // [1, 2, 3, 8, 5] — maybe sorted, maybe not!

// The heap array is: [1, 2, 3, 8, 5]
// But this doesn't mean iteration is sorted!

for (int n : pq) {
    System.out.println(n);  // 1, 2, 3, 8, 5 — NOT fully sorted!
}

// Only poll() returns in sorted order:
while (!pq.isEmpty()) {
    System.out.println(pq.poll());  // 1, 2, 3, 5, 8 — CORRECT sorted order
}
```

**Why?** The heap array maintains the heap property (parent ≤ children), but not full sorted order. Only `poll()` (which removes root and siftDowns) returns elements in sorted order.

---

## 8. Min-Heap vs Max-Heap

```java
// Default: min-heap (smallest has highest priority)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max-heap (largest has highest priority):
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
maxHeap.offer(5);
maxHeap.offer(1);
maxHeap.offer(3);
System.out.println(maxHeap.poll());  // 5 — largest first!

// Or with custom Comparator:
PriorityQueue<Task> byHighestPriority = new PriorityQueue<>(
    (a, b) -> b.priority - a.priority  // Reverse: higher number = higher priority
);
```

---

## 9. Common Pitfalls

| Mistake | Why It's Wrong | Correct Approach |
|---------|---------------|-----------------|
| **Iterating instead of polling** | Iterator is NOT sorted | Use `while (!pq.isEmpty()) pq.poll()` |
| **Assuming FIFO order** | PriorityQueue is NOT a regular queue | Use ArrayDeque for FIFO |
| **Mutable priority fields** | Element can't be found/fixed after mutation | Remove → mutate → re-add |
| **Not specifying Comparator** | Natural order may not match priority | Always use `Comparator.comparingXxx()` |
| **Null elements** | NullPointerException | Filter nulls before adding |

---

## 10. Interview Quick Reference

**Q: How does PriorityQueue work internally?**
A: Binary heap stored as an array. Parent at i, left child at 2i+1, right child at 2i+2. Smallest element at root (index 0). `offer()` adds at end and bubbles up. `poll()` removes root, moves last element to root, and siftDowns.

**Q: What's the time complexity of PriorityQueue operations?**
A: `offer()`: O(log n). `poll()`: O(log n). `peek()`: O(1). `contains()`: O(n). `size()`: O(1).

**Q: Is the PriorityQueue iterator sorted?**
A: No. Only polling returns elements in sorted order. Iterator returns elements in heap array order (not fully sorted).

**Q: What's the difference between PriorityQueue and TreeSet?**
A: PriorityQueue: allows duplicates, O(log n) offer/poll, O(1) peek. TreeSet: no duplicates, O(log n) add/contains/remove, sorted iteration. TreeSet is always fully sorted; PriorityQueue only returns sorted via poll().

**Q: How to get a max-heap in PriorityQueue?**
A: `new PriorityQueue<>(Comparator.reverseOrder())`

---

## 11. 30-Second Summary

```
PriorityQueue = Binary heap (min-heap by default). Smallest element always at front.

Internal: Object[] array arranged as a complete binary tree.
offer(e): add at end → bubble up → O(log n)
poll():   remove root → move last → sift down → O(log n)
peek():   return root → O(1)

✅ O(log n) offer/poll        ❌ Not FIFO (priority order)
✅ O(1) peek                  ❌ Iterator NOT sorted
✅ Duplicates allowed         ❌ O(n) contains
✅ Low memory (no nodes)      ❌ No nulls
✅ Min or max via Comparator  ❌ Not thread-safe

Best for: Priority-based scheduling, top-K, Dijkstra's, median tracking
Avoid for: FIFO queues, sorted iteration, fast contains

</final_content>
</write_to_file>