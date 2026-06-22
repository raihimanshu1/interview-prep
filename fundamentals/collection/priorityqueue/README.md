# PriorityQueue Internals — Complete Deep Dive

## 1. Why This Concept Matters

PriorityQueue is the go-to implementation for priority-based processing. Understanding its binary heap structure, ordering mechanisms, and performance characteristics is essential. In production, PriorityQueue is used for task scheduling, Dijkstra's algorithm, event simulation, and any scenario requiring "always get the smallest/largest element" semantics. Interviewers test this because it reveals understanding of heap data structures and tree-based collections.

Misunderstanding PriorityQueue causes:
- Wrong ordering when using without Comparator on non-Comparable elements
- O(n) performance from incorrect `remove(Object)` usage
- Confusion about iteration order (NOT sorted!)
- `ClassCastException` at runtime for mixed Comparable/Comparator usage

## 2. Basic Meaning

PriorityQueue implements Queue interface backed by a binary heap (priority heap). Elements ordered by natural order (Comparable) or custom Comparator. Head of queue = least element (min-heap by default).

**Key vocabulary:**
- **Binary heap**: complete binary tree stored in array
- **Min-heap**: parent ≤ children (default, smallest at root)
- **Max-heap**: parent ≥ children (use `Comparator.reverseOrder()`)
- **`peek()` / `poll()`**: O(1) and O(log n) access to head
- **`offer()` / `add()`**: O(log n) insertion
- **`remove(Object)`**: O(n) — must scan entire heap
- **NOT thread-safe**: use `PriorityBlockingQueue` for concurrency

What it is NOT: PriorityQueue is not sorted list. Iteration order is arbitrary, not priority order. It is not thread-safe.

## 3. Real Code / Real Example

```java
import java.util.*;

public class PriorityQueueDemo {
    public static void main(String[] args) {
        // === MIN-HEAP (natural order, smallest first) ===
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.offer(5);
        minHeap.offer(2);
        minHeap.offer(8);
        minHeap.offer(1);
        minHeap.offer(10);
        System.out.println("Min-heap peek: " + minHeap.peek()); // 1
        System.out.println("Poll order: " + minHeap.poll()); // 1
        System.out.println("Poll order: " + minHeap.poll()); // 2
        System.out.println("Poll order: " + minHeap.poll()); // 5
        System.out.println("Remaining: " + minHeap); // [8, 10] order varies internally

        // === MAX-HEAP (reverse order) ===
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.offer(5);
        maxHeap.offer(2);
        maxHeap.offer(8);
        System.out.println("Max-heap peek: " + maxHeap.peek()); // 8
        System.out.println("Poll: " + maxHeap.poll()); // 8

        // === CUSTOM OBJECT WITH COMPARATOR ===
        PriorityQueue<Task> tasks = new PriorityQueue<>(Comparator.comparingInt(Task::getPriority));
        tasks.add(new Task("Fix bug", 3));
        tasks.add(new Task("Write docs", 1));
        tasks.add(new Task("Deploy", 5));
        tasks.add(new Task("Code review", 2));
        System.out.println("Highest priority: " + tasks.peek().getName()); // Write docs (1)
        while (!tasks.isEmpty()) {
            Task t = tasks.poll();
            System.out.println("Processing: " + t.getName() + " (priority " + t.getPriority() + ")");
        }

        // === TOP-K PROBLEM ===
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 10, 3, 7);
        PriorityQueue<Integer> topK = new PriorityQueue<>(3); // keep 3 largest
        for (int n : numbers) {
            if (topK.size() < 3) topK.offer(n);
            else if (n > topK.peek()) { topK.poll(); topK.offer(n); }
        }
        System.out.println("Top 3: " + topK); // [8, 10, 7] (or similar)

        // === ITERATION ORDER (NOT sorted!) ===
        PriorityQueue<Integer> pq = new PriorityQueue<>(Arrays.asList(3, 1, 4, 1, 5));
        System.out.print("Iterator: ");
        for (int n : pq) System.out.print(n + " "); // arbitrary order!
        System.out.println();
        System.out.println("Poll order: " + pq.poll()); // 1

        // === INITIAL CAPACITY ===
        PriorityQueue<Integer> sized = new PriorityQueue<>(100);
        // internal array sized to accommodate 100 without resize
    }

    static class Task {
        String name; int priority;
        Task(String n, int p) { this.name = n; this.priority = p; }
        public String getName() { return name; }
        public int getPriority() { return priority; }
    }
}
```

Expected output:
```
Min-heap peek: 1
Poll order: 1
Poll order: 2
Poll order: 5
Remaining: [8, 10]
Max-heap peek: 8
Poll: 8
Highest priority: Write docs (priority 1)
Processing: Write docs (priority 1)
Processing: Code review (priority 2)
Processing: Fix bug (priority 3)
Processing: Deploy (priority 5)
Top 3: [8, 10, 7]
Iterator: 1 1 3 4 5 
Poll order: 1
```

## 4. What Happens Internally

**Binary heap structure (array-based):**
```
Min-heap: [1, 3, 2, 5, 10, 8]
Index:     0  1  2  3   4  5

Tree:
       1
     /   \
    3     2
   / \   /
  5  10 8

Parent(i) = (i-1) / 2
Left(i) = 2i + 1
Right(i) = 2i + 2
```

**`offer(E e)` (insertion):**
1. Add element at end of array (next available leaf)
2. "Sift up" (bubble up): compare with parent, swap if smaller (min-heap)
3. Repeat until heap property restored: parent ≤ child
4. O(log n) — height of tree

**`poll()` (remove head):**
1. Save root element (to return)
2. Move last element to root
3. "Sift down" (bubble down): compare with children, swap with smaller child
4. Repeat until heap property restored
5. O(log n)

**`peek()`:**
Return `elementData[0]` (root). O(1).

**`remove(Object o)` — O(n):**
1. Scan entire array to find index of object (using equals)
2. Swap with last element
3. Remove last element
4. Sift up or sift down from swapped position
5. Scan cost makes it O(n).

## 5. Tricky Interview Cases

**Case 1 — Iteration does NOT return sorted order**
```java
PriorityQueue<Integer> pq = new PriorityQueue<>(Arrays.asList(3, 1, 4, 1, 5));
System.out.println(pq); // might print [1, 1, 4, 3, 5]
```
Output: Arbitrary order (level-order of heap tree).
Explanation: Iterator returns elements in heap array order, NOT sorted. To get sorted: poll all elements.

**Case 2 — `remove(Object)` removes first occurrence only**
```java
PriorityQueue<String> pq = new PriorityQueue<>(List.of("B", "A", "C", "A"));
boolean removed = pq.remove("A");
System.out.println(pq); // [A, C, B] — only first "A" removed
```
Output: One "A" remains.
Explanation: `remove(Object)` removes first matching element found during scan.

**Case 3 — `remove` arbitrary element is O(n)**
```java
PriorityQueue<Integer> pq = new PriorityQueue<>(1000);
for (int i = 0; i < 1000; i++) pq.offer(i);
long start = System.currentTimeMillis();
pq.remove(Integer.valueOf(999)); // scan 1000 elements
System.out.println("remove: " + (System.currentTimeMillis() - start) + "ms");
start = System.currentTimeMillis();
pq.poll(); // O(log n)
System.out.println("poll: " + (System.currentTimeMillis() - start) + "ms");
```
Output: remove: 0-1ms, poll: 0ms (but ratio grows with size).
Explanation: `remove(Object)` scans linearly. `poll()` just takes root + sift down.

**Case 4 — Null not allowed**
```java
PriorityQueue<String> pq = new PriorityQueue<>();
pq.add(null); // NullPointerException!
```
Output: `NullPointerException`
Explanation: `null` hash/compareTo would NPE during sift up/down. Unlike HashMap (one null key), PriorityQueue rejects all nulls.

**Case 5 — `toArray()` returns heap order, not sorted**
```java
PriorityQueue<Integer> pq = new PriorityQueue<>(Arrays.asList(5, 2, 8, 1));
Object[] arr = pq.toArray();
System.out.println(Arrays.toString(arr)); // [1, 2, 5, 8] or [1, 2, 8, 5] — heap order
```
Output: Heap order, not fully sorted.
Explanation: Array is internal heap representation.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Assuming iteration returns sorted order | Wrong! iteration is heap array order | Poll all elements to get sorted |
| `remove(Object)` expecting O(log n) | O(n) scan required | Avoid arbitrary remove; use `poll()` |
| Not providing Comparator for non-Comparable | `ClassCastException` | Provide Comparator or make class Comparable |
| Using `contains()` frequently | O(n) scan | Consider HashSet alongside if contains needed |
| `add(null)` | NPE | Never add null |
| Assuming thread-safe | ConcurrentModification | Use `PriorityBlockingQueue` |

## 7. Production Usage

**Task scheduling (single-threaded):**
```java
PriorityQueue<Task> queue = new PriorityQueue<>(Comparator.comparing(Task::getDeadline));
while (!queue.isEmpty()) {
    Task t = queue.poll();
    execute(t);
}
```

**Dijkstra's algorithm (graph shortest path):**
```java
PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::getDist));
pq.add(source);
while (!pq.isEmpty()) {
    Node u = pq.poll();
    if (u.visited) continue;
    u.visited = true;
    for (Edge e : u.edges) {
        if (dist[e.to] > dist[u] + e.weight) {
            dist[e.to] = dist[u] + e.weight;
            pq.add(new Node(e.to, dist[e.to]));
        }
    }
}
```

**Top-K frequent elements:**
```java
PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
    Map.Entry.comparingByValue() // min-heap by frequency
);
for (Map.Entry<String, Integer> e : frequencyMap.entrySet()) {
    pq.offer(e);
    if (pq.size() > K) pq.poll(); // remove smallest frequency
}
// pq now contains K most frequent elements
```

## 8. Advanced Details

- **Array-based heap:** `Object[] queue` (or `E[]`). No Node objects — better cache locality than linked structures.
- **`grow()`:** Array doubles when full: `newCapacity = oldCapacity + (oldCapacity >> 1)` (1.5x, same as ArrayList).
- **`removeEq(Object o)`:** Linear scan, but once found, swaps with last and sifts. If object has duplicates, removes arbitrary one.
- **`PriorityBlockingQueue`:** Thread-safe variant. Uses single lock for all operations. Unbounded.
- **`DelayQueue`:** Extends PriorityQueue for delayed elements. Elements only available after delay expires.
- **`heapify()`:** Bulk insert: O(n) heap construction vs O(n log n) individual inserts. `PriorityQueue(Collection)` uses this.
- **`siftUp()` / `siftDown()`:** Core heap operations. Use `Comparable` or `Comparator` for comparisons.

## 9. Interview Questions And Answers

### Beginner
Q: What is the internal structure of PriorityQueue? What are the time complexities of insert, peek, and poll?
A: PriorityQueue uses a binary heap stored in an array. Insert (`offer`): O(log n) — sift up. Peek (`peek`): O(1) — return root. Poll (`poll`): O(log n) — remove root, sift down.

### Intermediate
Q: Why is `remove(Object)` O(n) in PriorityQueue but O(log n) in TreeSet? Can you optimize it?
A: `PriorityQueue.remove(Object)` must scan entire O(n) array to find the object because heap order is not sorted order. `TreeSet` uses binary search in Red-Black tree: O(log n).

Fix: Maintain auxiliary `HashMap<E, Integer>` mapping elements to heap indices. On remove: swap with last, sift up/down. Update map. But complex.

### Senior
Q: You need to find the K-th largest element in a stream. Using `PriorityQueue`, explain the approach and why it's optimal.
A: Maintain min-heap of size K:
```java
PriorityQueue<Integer> pq = new PriorityQueue<>(K);
for (int n : stream) {
    if (pq.size() < K) pq.offer(n);
    else if (n > pq.peek()) { pq.poll(); pq.offer(n); }
}
return pq.peek(); // K-th largest
```
Each element: O(log K). Total: O(n log K). Memory: O(K).

Why min-heap: smallest of top-K at root. When new element > root, it belongs in top-K. Root evicted. Other variants: max-heap of size n-K, but less memory-efficient.

## 10. Final 30-Second Answer

PriorityQueue = binary heap (array) with O(log n) insert/poll. Min-heap by default; max-heap via `Comparator.reverseOrder()`. `peek()` O(1). **Iteration NOT sorted** — use `poll()` loop for sorted extraction. `remove(Object)` O(n) — avoid in hot paths. Not thread-safe. Top-K: min-heap of size K. `Comparator` required for non-Comparable elements. Null not allowed. `heapify()` for bulk O(n) build.