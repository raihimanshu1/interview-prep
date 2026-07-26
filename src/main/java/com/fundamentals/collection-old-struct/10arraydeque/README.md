# ArrayDeque — Why? What? How? When?

## 1. The Problem Before ArrayDeque

### The Stack and Queue Problem

Before ArrayDeque, if you needed a **stack** (LIFO) or **queue** (FIFO), you had limited options:

**Stack with ArrayList — BAD:**
```java
// Using ArrayList as a Stack — front inserts are O(n)!
ArrayList<Integer> stack = new ArrayList<>();
stack.add(0, 1);  // O(n) — shifts all elements!
stack.add(0, 2);  // O(n) — shifts again!
stack.remove(0);  // O(n) — shifts!
```

**Stack with LinkedList — OK, but:**
```java
LinkedList<Integer> stack = new LinkedList<>();
stack.push(1);  // O(1) — addFirst
stack.push(2);  // O(1)
stack.pop();    // O(1) — removeFirst
// LinkedList works, but each element is a Node object → ~32-40 bytes per element
```

**Queue with LinkedList — OK, but same problem:**
```java
Queue<Integer> queue = new LinkedList<>();
queue.offer(1);  // O(1)
queue.offer(2);  // O(1)
queue.poll();    // O(1)
// Works, but high memory overhead per element
```

### What developers used for stacks:

```java
// Stack class — LEGACY, DON'T USE
Stack<Integer> stack = new Stack<>();
stack.push(1);
stack.pop();
// Stack extends Vector (synchronized) — slow, legacy
```

**Problems:**
- **ArrayList**: O(n) for front operations, not designed for stack/queue
- **LinkedList**: High memory (Node objects), poor cache locality
- **Stack class**: Legacy (extends Vector), synchronized overhead even when not needed
- **No efficient double-ended structure**: One class that does BOTH queue and stack

> **ArrayDeque was created to solve this**: A resizable circular array that supports O(1) add/remove at BOTH ends, with minimal memory overhead and excellent cache locality.

---

## 2. What is ArrayDeque? (Simple Explanation)

```java
// As a Queue (FIFO):
ArrayDeque<String> queue = new ArrayDeque<>();
queue.offer("First");     // Add at tail
queue.offer("Second");
queue.offer("Third");
System.out.println(queue.poll());  // "First" — remove from head

// As a Stack (LIFO):
ArrayDeque<String> stack = new ArrayDeque<>();
stack.push("Bottom");    // Add at head
stack.push("Middle");
stack.push("Top");
System.out.println(stack.pop());  // "Top" — remove from head

// Double-ended operations:
ArrayDeque<Integer> deque = new ArrayDeque<>();
deque.addFirst(1);   // [1]
deque.addLast(2);    // [1, 2]
deque.addFirst(0);   // [0, 1, 2]
deque.addLast(3);    // [0, 1, 2, 3]
System.out.println(deque.removeFirst());  // 0
System.out.println(deque.removeLast());   // 3
```

**ArrayDeque = A resizable circular array that works as Queue, Stack, and Deque.** Faster than LinkedList, less memory than LinkedList, and supports all stack/queue/deque operations at O(1).

---

## 3. How ArrayDeque Works Internally

### The Circular Array

ArrayDeque uses a **circular array** — the head and tail wrap around:

```java
public class ArrayDeque<E> implements Deque<E> {
    transient Object[] elements;  // The backing array
    transient int head;            // Index of the first element
    transient int tail;            // Index where next element will be inserted
    
    // Default initial capacity: 16 (always power of 2)
    private static final int MIN_INITIAL_CAPACITY = 8;
}
```

### Visual: Why Circular?

```
Linear array (bad for deque):
Before: [A, B, C, _, _, _]  head=0, tail=3
addFirst(X): shift all right → [_, A, B, C, _, _] → [X, A, B, C, _, _]  O(n)!

Circular array (good):
Before: [A, B, C, _, _, _]  head=0, tail=3
addFirst(X): head = (head - 1) & (n-1)  → head=5
           : elements[5] = X
           : [A, B, C, _, _, X]  No shift! O(1)!
```

### How ArrayDeque uses bitwise wrapping

```java
// The magic: (index - 1) & (length - 1) wraps around naturally
// Works because length is ALWAYS a power of 2

// Example: length = 8 (indices 0-7)
// head = 0 → addFirst: head = (0 - 1) & 7 = -1 & 7 = 7
// elements[7] = X → array is now [A, B, C, D, E, F, G, X]

// head = 3 → addFirst: head = (3 - 1) & 7 = 2
// elements[2] = X → [A, B, X, D, E, F, G, _]
```

### Simplified ArrayDeque:

```java
public class SimpleArrayDeque<E> {
    private Object[] elements;
    private int head = 0;
    private int tail = 0;
    
    public SimpleArrayDeque() {
        elements = new Object[16];  // Power of 2
    }
    
    // Add at front — O(1)!
    public void addFirst(E e) {
        head = (head - 1) & (elements.length - 1);  // Wrap backward
        elements[head] = e;
        if (head == tail) {
            doubleCapacity();  // Array is full
        }
    }
    
    // Add at end — O(1)!
    public void addLast(E e) {
        elements[tail] = e;
        tail = (tail + 1) & (elements.length - 1);  // Wrap forward
        if (tail == head) {
            doubleCapacity();
        }
    }
    
    // Remove from front — O(1)!
    public E removeFirst() {
        E result = (E) elements[head];
        elements[head] = null;  // Help GC
        head = (head + 1) & (elements.length - 1);
        return result;
    }
    
    // Remove from end — O(1)!
    public E removeLast() {
        tail = (tail - 1) & (elements.length - 1);
        E result = (E) elements[tail];
        elements[tail] = null;  // Help GC
        return result;
    }
    
    // Double the capacity and realign
    private void doubleCapacity() {
        int n = elements.length;
        int r = n - head;  // Elements from head to end
        int newCapacity = n << 1;  // Double
        
        Object[] newElements = new Object[newCapacity];
        System.arraycopy(elements, head, newElements, 0, r);
        System.arraycopy(elements, 0, newElements, r, head);
        
        elements = newElements;
        head = 0;
        tail = n;  // New tail is at old length
    }
}
```

### Step-by-step execution:

```java
SimpleArrayDeque<String> deque = new SimpleArrayDeque<>();
// elements: [_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] (16 slots)
// head=0, tail=0 (empty)

deque.addLast("A");
// tail=0 → elements[0] = "A" → tail = (0+1)&15 = 1
// [A, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]
// head=0, tail=1

deque.addLast("B");
// tail=1 → elements[1] = "B" → tail = 2
// [A, B, _, _, _, _, _, _, _, _, _, _, _, _, _, _]

deque.addFirst("X");
// head = (0-1)&15 = 15 → elements[15] = "X"
// [A, B, _, _, _, _, _, _, _, _, _, _, _, _, _, X]
// head=15, tail=2

deque.removeFirst();
// result = elements[15] = "X", elements[15] = null
// head = (15+1)&15 = 0
// [A, B, _, _, _, _, _, _, _, _, _, _, _, _, _, _]

deque.removeLast();
// tail = (2-1)&15 = 1, result = elements[1] = "B", elements[1] = null
// [A, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]
// tail=1
```

---

## 4. ArrayDeque vs LinkedList vs Stack

| Aspect | **ArrayDeque** | **LinkedList** | **Stack** |
|--------|---------------|---------------|-----------|
| **Internal** | Object[] circular array | Doubly-linked nodes | Object[] (Vector) |
| **push/pop** | **O(1)** | **O(1)** | O(1) |
| **offer/poll** | **O(1)** | **O(1)** | Not a queue |
| **Memory per elem** | **~8 bytes** (ref) | **~32-40 bytes** (Node) | ~8 bytes (ref) |
| **Cache locality** | **Excellent** | **Poor** (scattered nodes) | Excellent |
| **Thread-safe** | ❌ No | ❌ No | **Synchronized** (legacy) |
| **Null elements** | ❌ No | ✅ Yes | ✅ Yes |
| **get(index)** | **O(n)** (no RandomAccess) | **O(n)** | **O(1)** (Vector) |
| **Grow strategy** | **Double** | Allocate per node | Double |
| **Iterator** | Fail-fast | Fail-fast | Fail-fast (Enumeration legacy) |

### Performance: ArrayDeque vs LinkedList

```
Operation              ArrayDeque    LinkedList    Winner
─────────────────────  ──────────    ──────────    ──────
addFirst               O(1)          O(1)          Tie
addLast                O(1)          O(1)          Tie
removeFirst            O(1)          O(1)          Tie
removeLast             O(1)          O(1)          Tie
peek/peekFirst         O(1)          O(1)          Tie
1M push+pop            ~15ms         ~45ms         ArrayDeque (3x faster)
Memory for 1M elems    ~8 MB         ~35 MB        ArrayDeque (4x less)
```

**ArrayDeque wins because:**
1. Contiguous memory → CPU cache prefetches → faster iteration
2. No per-element object (Node) → less memory → less GC pressure
3. Array-based → faster bulk operations

---

## 5. ArrayDeque: Advantages, Limitations, When to Use, When Not to Use

### ✅ Advantages

| Advantage | Why |
|-----------|-----|
| **O(1) add/remove at BOTH ends** | Circular array — no shifting, no Node allocation |
| **Low memory** | Just array references, no Node objects |
| **Excellent cache locality** | Contiguous memory access |
| **Faster than LinkedList** | ~3-4x for stack/queue operations |
| **Replaces both Stack and LinkedList** | Single class for deque/queue/stack |

### ❌ Limitations

| Limitation | Why |
|------------|-----|
| **Not thread-safe** | Same as ArrayList |
| **No null elements** | Throws NullPointerException |
| **No get(index) O(1)** | Designed for ends, not random access |
| **Resize cost** | Occasional O(n) when doubling |
| **Not a List** | Can't use List methods like get(index) |

### 🟢 When to Use

```java
// 1. As a Queue (FIFO) — best choice!
Queue<Task> queue = new ArrayDeque<>();
queue.offer(new Task("A"));
queue.offer(new Task("B"));
Task next = queue.poll();  // "A" — O(1)!

// 2. As a Stack (LIFO) — best choice!
Deque<String> stack = new ArrayDeque<>();
stack.push("Bottom");  // addFirst
stack.push("Top");     // addFirst
String top = stack.pop();  // removeFirst — "Top"

// 3. Double-ended operations
ArrayDeque<Integer> deque = new ArrayDeque<>();
deque.addLast(1);
deque.addLast(2);
deque.addFirst(0);
// deque: [0, 1, 2]

// 4. Sliding window — add to end, remove from front
ArrayDeque<Integer> window = new ArrayDeque<>();
for (int num : stream) {
    window.addLast(num);
    if (window.size() > WINDOW_SIZE) {
        window.removeFirst();  // O(1) — ArrayList would be O(n)!
    }
}
```

### 🔴 When NOT to Use

```java
// 1. Need random access by index — use ArrayList
ArrayDeque<Integer> deque = new ArrayDeque<>(List.of(1, 2, 3));
System.out.println(deque.get(1));  // NO such method!

// 2. Need thread-safety — use BlockingQueue or ConcurrentLinkedDeque
ArrayDeque<Integer> unsafe = new ArrayDeque<>();
// Multi-threaded access corrupts the circular array!

// 3. Need null elements — ArrayDeque doesn't allow nulls
deque.addLast(null);  // NullPointerException!

// 4. Need List operations (add at index, subList) — use ArrayList
```

---

## 6. Why NOT to Use Stack Class

```java
// ❌ Stack — legacy, DON'T USE
Stack<String> stack = new Stack<>();
stack.push("A");
stack.push("B");
String top = stack.pop();

// Stack extends Vector:
// - Vector is synchronized (unnecessary overhead)
// - Vector grows by 2x (not 1.5x like ArrayList)
// - Stack can access List methods (breaking stack semantics!)
stack.get(0);  // Can access elements by index! Breaks LIFO!
```

```java
// ✅ ArrayDeque — modern, use this
Deque<String> stack = new ArrayDeque<>();
stack.push("A");
stack.push("B");
String top = stack.pop();
// No unnecessary synchronization
// No broken methods
// Better performance
```

---

## 7. When to Use ArrayDeque vs LinkedList

| Use Case | Best Choice | Why |
|----------|------------|-----|
| **Queue (FIFO)** | **ArrayDeque** | Fast, low memory |
| **Stack (LIFO)** | **ArrayDeque** | Fast, low memory |
| **Double-ended queue** | **ArrayDeque** | O(1) both ends |
| **Need List + Deque** | **LinkedList** | Implements both |
| **Need null elements** | **LinkedList** | ArrayDeque doesn't allow null |
| **Massive collection** | **ArrayDeque** | ~8 MB vs ~35 MB per million |
| **Remove from middle** | **LinkedList** | O(1) after finding node |

---

## 8. Interview Quick Reference

**Q: What is ArrayDeque?**
A: A resizable circular array implementing Deque (double-ended queue). O(1) add/remove at both ends. Faster and more memory-efficient than LinkedList for stack/queue operations.

**Q: Why is ArrayDeque faster than LinkedList?**
A: (1) Contiguous memory → CPU cache prefetch (2) No per-element Node allocation (3) Less GC pressure. ~3-4x faster in practice.

**Q: How does the circular array work?**
A: Head and tail indices wrap around using bitwise AND: `head = (head - 1) & (length - 1)`. Works because capacity is always a power of 2.

**Q: When should you use ArrayDeque vs LinkedList?**
A: Use ArrayDeque for queues, stacks, deques. Use LinkedList when you also need List operations or null elements.

**Q: Why not use the Stack class?**
A: Stack extends Vector (legacy, synchronized unnecessarily). It also allows List operations that break stack semantics. ArrayDeque is the modern replacement.

---

## 9. 30-Second Summary

```
ArrayDeque = Resizable circular array for Deque/Queue/Stack.

Operations: addFirst/Last, removeFirst/Last, push/pop, offer/poll — all O(1)
Internal:  Object[] array with head/tail wrapping using bitwise AND
Growth:    Double capacity when head == tail (full)

✅ O(1) both ends              ❌ Not thread-safe
✅ ~3x faster than LinkedList  ❌ No nulls allowed
✅ ~4x less memory than LL     ❌ No List/random access
✅ Excellent cache locality    ❌ Occasional O(n) resize

Best for: Queue, Stack, Deque operations
Avoid for: Random access, thread safety, null elements

NOTE: ArrayDeque is the MODERN replacement for:
  - Stack class (legacy, DON'T USE)
  - LinkedList as Queue/Deque (use when you also need List)