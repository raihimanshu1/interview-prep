# ArrayDeque Internals — Complete Deep Dive

## 1. Why This Concept Matters

ArrayDeque is the modern replacement for LinkedList as queue/stack. It provides O(1) amortized operations at both ends without per-element object allocation. Understanding its circular array structure and head/tail pointer mechanics is essential. In production, ArrayDeque is preferred over LinkedList for queues and stacks due to better cache locality and lower GC pressure.

Misunderstanding ArrayDeque causes:
- Using LinkedList where ArrayDeque is faster and lighter
- Incorrect capacity planning (no initial capacity constructor!)
- Confusion between addFirst/addLast vs push/pop
- IndexOutOfBounds when treating as random-access list

## 2. Basic Meaning

ArrayDeque is a resizable circular array implementation of Deque (double-ended queue). No null elements allowed. Grows by 1.5x when full.

**Key vocabulary:**
- **Circular array**: head and tail pointers wrap around end to beginning
- **`head`**: index of first element
- **`tail`**: index one past last element (exclusive)
- **`elements`**: internal Object[] array (always power of 2)
- **Amortized O(1)**: add/remove at both ends
- **`addFirst()` / `addLast()`**: insert at ends
- **`removeFirst()` / `removeLast()`**: remove from ends
- **`push()` / `pop()`**: stack semantics (addFirst/removeFirst)
- **`peek()` / `peekFirst()` / `peekLast()`**: view without removing

What it is NOT: ArrayDeque is not a List. Not thread-safe. Does not support random access by index. Cannot store null.

## 3. Real Code / Real Example

```java
import java.util.*;
import java.util.concurrent.*;

public class ArrayDequeDemo {
    public static void main(String[] args) {
        // === BASIC USAGE ===
        Deque<String> deque = new ArrayDeque<>();
        deque.addFirst("A");    // [A]
        deque.addLast("B");     // [A, B]
        deque.addFirst("C");    // [C, A, B]
        deque.addLast("D");     // [C, A, B, D]
        System.out.println("Deque: " + deque);

        // === QUEUE OPERATIONS ===
        Queue<String> queue = new ArrayDeque<>();
        queue.offer("1");
        queue.offer("2");
        queue.offer("3");
        System.out.println("Poll: " + queue.poll()); // 1 (FIFO)
        System.out.println("Peek: " + queue.peek()); // 2
        System.out.println("Queue: " + queue);       // [2, 3]

        // === STACK OPERATIONS ===
        Deque<String> stack = new ArrayDeque<>();
        stack.push("bottom");
        stack.push("middle");
        stack.push("top");
        System.out.println("Pop: " + stack.pop());    // top
        System.out.println("Pop: " + stack.pop());    // middle
        System.out.println("Stack: " + stack);        // [bottom]

        // === PEEK BOTH ENDS ===
        deque = new ArrayDeque<>(Arrays.asList("A", "B", "C", "D"));
        System.out.println("First: " + deque.peekFirst());   // A
        System.out.println("Last: " + deque.peekLast());     // D
        System.out.println("First: " + deque.getFirst());    // A
        System.out.println("Last: " + deque.getLast());      // D

        // === DESCENDING ITERATOR ===
        System.out.print("Reverse: ");
        Iterator<String> desc = deque.descendingIterator();
        while (desc.hasNext()) System.out.print(desc.next() + " ");
        System.out.println(); // D C B A

        // === OFFER vs ADD ===
        Deque<String> bounded = new ArrayDeque<>(2);
        System.out.println("offer A: " + bounded.offer("A")); // true
        System.out.println("offer B: " + bounded.offer("B")); // true
        System.out.println("offer C: " + bounded.offer("C")); // true (unbounded!)
        System.out.println("add D: " + bounded.add("D"));     // true

        // === REMOVE FIRST/LAST ===
        System.out.println("removeFirst: " + deque.removeFirst()); // A
        System.out.println("removeLast: " + deque.removeLast());   // D
        System.out.println("After removals: " + deque);            // [B, C]

        // === CLEAR ===
        deque.clear();
        System.out.println("Empty: " + deque.isEmpty()); // true
    }
}
```

Expected output:
```
Deque: [C, A, B, D]
Poll: 1
Peek: 2
Queue: [2, 3]
Pop: top
Pop: middle
Stack: [bottom]
First: A
Last: D
First: A
Last: D
Reverse: D C B A 
offer A: true
offer B: true
offer C: true
add D: true
removeFirst: A
removeLast: D
After removals: [B, C]
Empty: true
```

## 4. What Happens Internally

**Circular array structure:**
```
ArrayDeque grows: 16 → 24 → 36 → ...

Indices:  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
Elements: A  B  C  D  _  _  _  _  _  _  _  _  _  _  _  _
           ^head = 0              ^tail = 4

After addLast(E), addLast(F):
Elements: A  B  C  D  E  F  _  _  _  _  _  _  _  _  _  _
           ^head = 0              ^tail = 6

After removeFirst() twice (removes A, B):
Elements: _  _  C  D  E  F  _  _  _  _  _  _  _  _  _  _
                     ^head = 2     ^tail = 6

After addLast(G), addLast(H):
Elements: _  _  C  D  E  F  G  H  _  _  _  _  _  _  _  _
                     ^head = 2                          ^tail = 8 (wraps!)

Wait, tail = 8, array length = 16. Still fits.

When head/tail reach end, they wrap:
tail = (tail + 1) & (elements.length - 1)  // fast modulo
head = (head + 1) & (elements.length - 1)
```

**Internal array always power of 2:**
```java
transient Object[] elements;
private transient int head;
private transient int tail;
```
`head` points to first element. `tail` points to next insertion point. Array length always power of 2, enabling fast circular wrap: `(index + 1) & (length - 1)`.

**`addFirst(E e)`:**
1. `head = (head - 1) & (elements.length - 1)` — circular decrement
2. `elements[head] = e`
3. If `head == tail` (full): grow array
4. Return true

**`addLast(E e)`:**
1. `elements[tail] = e`
2. `tail = (tail + 1) & (elements.length - 1)`
3. If `head == tail` (full): grow array
4. Return true

**`pollFirst()`:**
1. If empty: return null
2. `result = elements[head]`
3. `elements[head] = null` (help GC)
4. `head = (head + 1) & (elements.length - 1)`
5. Return result

**`grow(int needed)`:**
```java
int newCapacity = elements.length + (elements.length >> 1); // 1.5x
// Copy elements in order from old circular array to new linear array
// head becomes 0, tail becomes size
```
Same growth strategy as ArrayList (1.5x). Copy preserves element order.

**`allocateElements(int numElements)`:**
```java
int initialCapacity = 16;
if (numElements >= MAX_ARRAY_SIZE) { ... }
int desiredCapacity = numElements; // NOT rounded up to power of 2!
// But internally it uses: newCapacity = 16 -> 24 -> 36 -> ...
```
Unlike HashMap/ArrayList, ArrayDeque rounds up to nearest power of 2 via bit manipulation in `(index + 1) & (length - 1)`.

## 5. Tricky Interview Cases

**Case 1 — No initial capacity constructor**
```java
ArrayDeque<Integer> deque = new ArrayDeque<>(100);
// Same as ArrayDeque<>()!
// Initial capacity parameter is just a HINT, not exact allocation
```
Output: Still starts with default small array, grows as needed.
Explanation: Constructor takes `initialCapacity` but immediately rounds to 16 (or next power-of-2 size). Not like ArrayList where `new ArrayList<>(100)` allocates array of 100 immediately.

**Case 2 — `push()` vs `addFirst()` in Queue context**
```java
Queue<String> q = new ArrayDeque<>();
q.push("A"); // OK — compiles!
q.add("B");  // OK
System.out.println(q.poll()); // A
```
Output: Works. `push()` inherited from Deque.
Explanation: `ArrayDeque` implements `Deque`, which extends `Queue`. Both interfaces available.

**Case 3 — Null elements rejected**
```java
ArrayDeque<String> deque = new ArrayDeque<>();
deque.addFirst(null); // NullPointerException
deque.addLast(null);  // NullPointerException
deque.offer(null);    // NullPointerException
```
Output: NPE on all null operations.
Explanation: `null` used as sentinel for empty slot detection internally.

**Case 4 — Circular wrap in action**
```java
ArrayDeque<Integer> deque = new ArrayDeque<>(4); // capacity 8 (first power of 2 >= 4)
// Fill: addLast 1,2,3,4,5,6,7
// Remove first: 1,2,3,4,5,6 (head now at index 6)
// Add last: 8, 9 (tail wraps from 7->0->1)
// Array: [9, ?, ?, ?, ?, ?, 7, 8]
//           ^tail    ^head
```
Output: Wrapping works seamlessly.

**Case 5 — Performance vs LinkedList**
```java
// ArrayDeque: contiguous array, no node allocation
ArrayDeque<Integer> ad = new ArrayDeque<>(100_000);
for (int i = 0; i < 100_000; i++) ad.addFirst(i);
for (int i = 0; i < 100_000; i++) ad.removeFirst();

// LinkedList: 100,000 Node allocations (40+ bytes each)
LinkedList<Integer> ll = new LinkedList<>();
for (int i = 0; i < 100_000; i++) ll.addFirst(i);
for (int i = 0; i < 100_000; i++) ll.removeFirst();
```
Output: ArrayDeque ~2-3x faster, less GC pressure.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Using `new ArrayDeque<>(n)` expecting exact capacity | n is hint, not exact allocation | Accept behavior — it grows as needed |
| Using as List | No random access, no get(index) | Use ArrayList for indexed access |
| `add(null)` / `offer(null)` | NPE | Never add nulls |
| Using LinkedList for queue/stack | Slower, more memory | Use ArrayDeque |
| Assuming bounded capacity | Unbounded (grows forever) | Use `ArrayBlockingQueue` for bounded |
| `remove()` on empty deque | NoSuchElementException | Check `isEmpty()` or use `poll()` |
| `element()` on empty deque | NoSuchElementException | Use `peek()` which returns null |

## 7. Production Usage

**BFS (breadth-first search) graph traversal:**
```java
Deque<Node> queue = new ArrayDeque<>();
queue.addLast(source);
while (!queue.isEmpty()) {
    Node curr = queue.removeFirst();
    if (curr.visited) continue;
    for (Node neighbor : curr.neighbors) {
        queue.addLast(neighbor);
    }
}
```

**DFS (depth-first search) with stack:**
```java
Deque<Node> stack = new ArrayDeque<>();
stack.push(root);
while (!stack.isEmpty()) {
    Node curr = stack.pop();
    // process
    for (Node child : curr.children) {
        stack.push(child);
    }
}
```

**Sliding window maximum:**
```java
Deque<Integer> deque = new ArrayDeque<>();
List<Integer> result = new ArrayList<>();
for (int i = 0; i < nums.length; i++) {
    while (!deque.isEmpty() && deque.peekFirst() < i - k) deque.removeFirst();
    while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) deque.removeLast();
    deque.addLast(i);
    if (i >= k - 1) result.add(nums[deque.peekFirst()]);
}
```

**Spring WebFlux (reactive):**
```java
// Reactor uses ArrayDeque internally for Subscriber queues
Flux<String> flux = Flux.just("a", "b", "c");
flux.subscribe(System.out::println);
```

## 8. Advanced Details

- **`head == tail` means empty, but how distinguish from full?** ArrayDeque never fully fills array. Keeps at least one slot empty: `head == tail` = empty, `(tail + 1) & length - 1 == head` = full. This avoids storing size separately.
- **`allocateElements()`:** Rounds up to power of 2 via bit manipulation if needed.
- **`ArrayDeque` vs `ArrayBlockingQueue`:** ArrayDeque is unbounded, not thread-safe. ArrayBlockingQueue is bounded, thread-safe (single lock).
- **`Stack` class is legacy:** Use `Deque<Integer> stack = new ArrayDeque<>()` instead of `Stack<Integer>`.
- **`pop()` throws NoSuchElementException` on empty:** Use `poll()` for null-returning safe removal.
- **`removeFirstOccurrence()` / `removeLastOccurrence()`:** O(n) linear scan, inherited from AbstractQueue.

## 9. Interview Questions And Answers

### Beginner
Q: Why is ArrayDeque preferred over LinkedList for queue and stack operations?
A: ArrayDeque uses a circular array — no per-element Node allocation, better cache locality, lower GC pressure. LinkedList creates 40+ byte Node for each element. ArrayDeque is typically 2-3x faster for add/remove at ends.

### Intermediate
Q: What is the time complexity of addFirst, addLast, removeFirst, removeLast, peekFirst, peekLast in ArrayDeque?
A: All are O(1) amortized. Array may resize (1.5x) when full, but amortized over many operations is O(1). Peek operations always O(1).

### Senior
Q: You need to implement a bounded, thread-safe, blocking deque. Can you use ArrayDeque? What are your options?
A: ArrayDeque is unbounded and not thread-safe. Options:
1. `ArrayBlockingQueue` — bounded, blocking, thread-safe (single lock). Implements BlockingQueue, has fixed capacity.
2. `LinkedBlockingQueue` — optionally bounded, blocking (two-lock queue).
3. `LinkedTransferQueue` — unbounded, high-performance, transfer semantics.
4. `SynchronousQueue` — zero-capacity, handoff between threads.

For bounded blocking deque: `ArrayBlockingQueue` is closest match.

### Tricky
Q: ArrayDeque has no `get(int index)` method. How would you access the middle element? Is there a data structure that supports both O(1) ends and O(1) random access?
A: Cannot access middle element of ArrayDeque in O(1) — would require traversal O(n). No single structure in Java standard library supports both deque operations and O(1) random access.

Approaches:
1. Maintain TWO structures: `ArrayDeque` for ends + `ArrayList` for random access. Keep in sync.
2. Use `Array` directly if bounds known — `arr[0]` and `arr[length-1]` O(1).
3. Ring buffer (circular buffer) with separate tracking — but not in standard library.

## 10. Final 30-Second Answer

ArrayDeque = circular array deque. O(1) amortized add/remove at both ends. No nulls allowed. **Prefer over LinkedList** for queue/stack — faster, less memory, better cache locality. `addFirst()/addLast()`, `removeFirst()/removeLast()`, `peekFirst()/peekLast()`. Grows 1.5x. `push()/pop()` for stack semantics. Not thread-safe. `ArrayBlockingQueue` for bounded blocking. No random access by index. `peek()` null-safe, `element()` throws on empty.