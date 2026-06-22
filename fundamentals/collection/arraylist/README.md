# ArrayList Internals — Complete Deep Dive

## 1. Why This Concept Matters

ArrayList is the most widely used List implementation in Java. Understanding its internal array-based structure, resizing strategy, and performance characteristics is fundamental. In production, misuse of ArrayList causes excessive array copying, memory waste from incorrect initial capacity, and `ConcurrentModificationException` during iteration with modification. Interviewers test ArrayList because it reveals your understanding of dynamic arrays, amortized complexity, and fail-fast iterators.

Misunderstanding ArrayList causes:
- O(n) performance from repeated resizing in loops
- `ArrayIndexOutOfBoundsException` from concurrent modification
- Memory waste from default capacity (10) when expecting thousands of elements
- `ConcurrentModificationException` during iteration with structural changes

## 2. Basic Meaning

ArrayList is a resizable array implementation of the `List` interface. It stores elements in a contiguous array, growing dynamically as elements are added.

Key vocabulary:
- **`elementData`**: internal `Object[]` array holding elements
- **`size`**: number of actual elements (not array length)
- **Capacity**: length of internal `elementData` array
- **Resize / grow**: allocate new larger array, copy elements
- **`ArrayList(int initialCapacity)`**: constructor setting initial capacity
- **`ArrayList()`**: default constructor with capacity 10
- **`trimToSize()`**: shrinks internal array to match `size`
- **`ensureCapacity()`**: pre-allocates to avoid future resize
- **Fail-fast**: iterator throws `ConcurrentModificationException` on structural modification

What it is NOT: ArrayList is not thread-safe. It does not guarantee O(1) for insertions at arbitrary positions (O(n) due to shifting).

## 3. Real Code / Real Example

```java
import java.util.*;
import java.util.stream.*;

public class ArrayListDemo {
    public static void main(String[] args) {
        // === BASIC OPERATIONS ===
        List<String> names = new ArrayList<>();
        names.add("Alice");       // append at end — amortized O(1)
        names.add("Bob");
        names.add(0, "Zero");    // insert at index 0 — O(n) shift
        System.out.println("Names: " + names);

        // === DEFAULT CAPACITY (10) ===
        // new ArrayList<>() → elementData = new Object[10]
        // 11th element triggers resize: 10 → 16 (1.5x in Java 7+)

        // === RESIZE BEHAVIOR ===
        ArrayList<Integer> numbers = new ArrayList<>(4);
        System.out.println("Initial capacity: " + getCapacity(numbers)); // 4
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        System.out.println("After 3 adds, capacity: " + getCapacity(numbers)); // 4
        numbers.add(4); // size == capacity, triggers resize
        System.out.println("After resize (4th add): " + getCapacity(numbers)); // 6 (1.5x)
        numbers.add(5);
        numbers.add(6); // size == capacity again
        numbers.add(7); // next resize
        System.out.println("After second resize: " + getCapacity(numbers)); // 9 (1.5x)

        // === ADD AT MIDDLE (costly) ===
        long start = System.currentTimeMillis();
        List<Integer> expensive = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            expensive.add(0, i); // insert at FRONT — shifts all elements each time
        }
        long timeAtFront = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        List<Integer> cheap = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            cheap.add(i); // append at end — no shift
        }
        long timeAtEnd = System.currentTimeMillis() - start;

        System.out.printf("add(0,i): %d ms | add(i): %d ms%n", timeAtFront, timeAtEnd);
        // add(0,i): 200-500 ms | add(i): 1-3 ms

        // === ITERATION AND MODIFICATION (fail-fast) ===
        List<String> failFast = new ArrayList<>(List.of("A", "B", "C"));
        try {
            for (String s : failFast) {
                failFast.remove(s); // structural modification during iteration
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException caught");
        }

        // === SAFE REMOVAL VIA ITERATOR ===
        Iterator<String> it = failFast.iterator();
        while (it.hasNext()) {
            String s = it.next();
            it.remove(); // safe — updates cursor AND modCount
        }
        System.out.println("After safe removal: " + failFast); // []

        // === SUBLIST IS VIEW ===
        List<Integer> full = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> sub = full.subList(1, 3); // [2, 3] — VIEW into parent
        sub.clear(); // modifies PARENT list too!
        System.out.println("Parent after sub.clear(): " + full); // [1, 4, 5]

        // === TO ARRAY ===
        List<String> toArray = new ArrayList<>(List.of("a", "b", "c"));
        String[] arr = toArray.toArray(new String[0]); // preferred since Java 11
        System.out.println("Array: " + Arrays.toString(arr));

        // === ARRAYS.ASLIST TRAP ===
        String[] arr2 = {"x", "y", "z"};
        List<String> fixed = Arrays.asList(arr2);
        // fixed.set(0, "w"); // OK
        // fixed.add("new"); // UnsupportedOperationException! — fixed size
        // fixed.remove(0);  // UnsupportedOperationException!
        arr2[0] = "changed"; // reflects in list!
        System.out.println("Fixed list: " + fixed); // [changed, y, z]
    }

    // Helper to inspect ArrayList capacity via reflection
    static int getCapacity(ArrayList<?> list) {
        try {
            java.lang.reflect.Field field = ArrayList.class.getDeclaredField("elementData");
            field.setAccessible(true);
            Object[] data = (Object[]) field.get(list);
            return data == null ? 0 : data.length;
        } catch (Exception e) {
            return -1;
        }
    }
}
```

Expected output:
```
Names: [Zero, Alice, Bob]
Initial capacity: 4
After 3 adds, capacity: 4
After resize (4th add): 6
After second resize: 9
add(0,i): 200-500 ms | add(i): 1-3 ms
ConcurrentModificationException caught
After safe removal: []
Parent after sub.clear(): [1, 4, 5]
Array: [a, b, c]
Fixed list: [changed, y, z]
```

## 4. What Happens Internally

**ArrayList structure:**
```java
public class ArrayList<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    transient Object[] elementData; // internal array
    private int size;                // number of elements

    public ArrayList() {
        // Default constructor: empty array, grows to 10 on first add
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }
}
```

**`add(E e)` flow:**
1. Check capacity: `size + 1 > elementData.length`
2. If full: `grow(size + 1)` → calculate new capacity, allocate new array, `System.arraycopy()`
3. Place element: `elementData[size++] = e`
4. Return `true`

**`grow()` algorithm (Java 8+):**
```java
private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1); // 1.5x growth
    if (newCapacity - minCapacity < 0) newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0) newCapacity = hugeCapacity(minCapacity);
    elementData = Arrays.copyOf(elementData, newCapacity); // native array copy
}
```

Growth factor: **1.5x** (not 2x as in older Java). `oldCapacity + (oldCapacity >> 1)` = `oldCapacity * 1.5`.

**`add(int index, E element)` flow:**
1. `RangeCheck`: if `index > size` or `index < 0` → `IndexOutOfBoundsException`
2. If `size == elementData.length` → `grow(size + 1)`
3. `System.arraycopy(elementData, index, elementData, index + 1, size - index)`
4. `elementData[index] = element`
5. `size++`

Cost: O(n) due to array shift for each middle insertion.

**`remove(int index)` flow:**
1. `RangeCheck(index)`
2. `oldValue = elementData[index]`
3. `numMoved = size - index - 1`
4. If `numMoved > 0`: `System.arraycopy(elementData, index + 1, elementData, index, numMoved)`
5. `elementData[--size] = null` (avoid memory leak)
6. Return `oldValue`

**Fail-fast iterator:**
```java
private class Itr implements Iterator<E> {
    int cursor = 0;
    int lastRet = -1;
    int expectedModCount = modCount;

    public boolean hasNext() { return cursor != size; }

    public E next() {
        checkForComodification();
        // ... return element
    }

    public void remove() {
        checkForComodification();
        // ... update cursor, modCount, expectedModCount
    }

    final void checkForComodification() {
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }
}
```

`modCount` increments on every structural modification (add, remove, clear). Iterator captures `expectedModCount` at creation. Mismatch → `ConcurrentModificationException`.

## 5. Tricky Interview Cases

**Case 1 — `Arrays.asList()` returns fixed-size list**
```java
String[] arr = {"a", "b", "c"};
List<String> list = Arrays.asList(arr);
System.out.println(list.size()); // 3
// list.add("d"); // UnsupportedOperationException!
arr[0] = "z";
System.out.println(list); // [z, b, c]
```
Output: `UnsupportedOperationException` on add/remove.
Explanation: `Arrays.asList()` returns a fixed-size list backed by the array. No structural modifications allowed. But `set()` works and changes reflect in array.

**Case 2 — `subList()` modifications affect parent**
```java
List<Integer> parent = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
List<Integer> child = parent.subList(1, 3); // [2, 3]
child.clear();
System.out.println(parent); // [1, 4, 5]
```
Output: `[1, 4, 5]`
Explanation: `subList()` returns a view backed by parent list. Structural changes to child propagate to parent.

**Case 3 — Default capacity is 10, not 0**
```java
ArrayList<String> list = new ArrayList<>();
// Internal: elementData = {} (empty array, length 0)
// BUT first add triggers grow to DEFAULT_CAPACITY = 10
```
Output: First add allocates `Object[10]`.
Explanation: Empty constructor uses 0-length array to save memory. First `add()` sees 0-length, grows to 10.

**Case 4 — `toArray(new T[0])` vs `toArray(new T[size])`**
```java
List<String> list = List.of("a", "b");
String[] arr1 = list.toArray(new String[0]);   // Java 11+ preferred
String[] arr2 = list.toArray(new String[list.size()]); // older style, both work
```
Output: Both produce `[a, b]`.
Explanation: In Java 11+, `toArray(new T[0])` is faster and preferred. JIT optimizes zero-length array allocation.

**Case 5 — `removeIf()` modifies in-place**
```java
List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
list.removeIf(n -> n % 2 == 0); // remove evens
System.out.println(list); // [1, 3, 5]
```
Output: `[1, 3, 5]`
Explanation: `removeIf()` is a default method in `Collection`. It iterates once, removing matching elements. Faster than manual iteration + remove.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `new ArrayList()` with known large size | Multiple resizes, reallocations | `new ArrayList<>(expectedSize)` |
| `add(0, element)` in loop (queue behavior) | O(n^2) due to shifting each time | Use `LinkedList` or `ArrayDeque` |
| Iterating with for-each and removing | `ConcurrentModificationException` | Use `Iterator.remove()` or `removeIf()` |
| `Arrays.asList()` expecting full ArrayList | Fixed-size list, no add/remove | `new ArrayList<>(Arrays.asList(...))` |
| `subList()` forgetting it's a view | Modifications to parent affect sub and vice versa | Treat subList as view; `new ArrayList<>(subList)` for copy |
| Storing primitives | Auto-boxing to wrapper objects | Primitives always boxed in `ArrayList<Integer>` |
| `toArray(new T[0])` in Java < 11 | Slight performance overhead | `toArray(new T[size])` for Java 8 and earlier |

## 7. Production Usage

**Spring MVC request attributes:**
```java
// Spring uses ArrayList internally for many collections
List<String> errors = new ArrayList<>();
errors.add("field1: required");
errors.add("field2: invalid");
model.addAttribute("errors", errors);
```

**Batch processing with pre-sizing:**
```java
int batchSize = 10_000;
List<Record> batch = new ArrayList<>(batchSize);
for (int i = 0; i < totalRecords; i++) {
    batch.add(record);
    if (batch.size() == batchSize) {
        processBatch(batch);
        batch.clear(); // keeps capacity, avoids reallocation
    }
}
```

**Converting between array and list:**
```java
// Array → List
List<String> list = Arrays.asList("a", "b", "c");
List<String> mutable = new ArrayList<>(Arrays.asList("a", "b", "c"));
// OR Java 9+
List<String> list9 = List.of("a", "b", "c"); // immutable

// List → Array
String[] arr = list.toArray(new String[0]); // Java 11+
```

**Avoiding `subList` pitfalls:**
```java
List<Integer> parent = new ArrayList<>(range(1, 1000));
List<Integer> snapshot = new ArrayList<>(parent.subList(0, 100));
// snapshot is independent copy, safe to modify
```

## 8. Advanced Details

- **Resize growth factor:** Java 7+ uses 1.5x. Java 6 used 2x. Formula: `newCapacity = oldCapacity + (oldCapacity >> 1)`.
- **`System.arraycopy()`:** Native method, highly optimized JVM intrinsic. Copies memory block. Much faster than manual loop.
- **`ArrayList` vs `Vector`:** `Vector` is synchronized (all methods). `ArrayList` is not. `Vector` grows by 2x, `ArrayList` by 1.5x. `Vector` is largely legacy.
- **`ArrayList` random access:** `get(index)` is O(1) — direct array index access.
- **`ArrayList` memory overhead:** Each element is a reference (4-8 bytes). `ArrayList` with 1M Integer objects: array ~8 MB + 1M * 16 bytes (Integer objects) = ~24 MB.
- **`trimToSize()`:** Reduces `elementData` to `size`. Useful after bulk add then bulk remove.
- **`ensureCapacity(int minCapacity)`:** Public since Java 6+. Allows pre-allocation without adding elements.

## 9. Interview Questions And Answers

### Beginner
Q: How does ArrayList store elements internally? What happens when it reaches capacity?
A: ArrayList uses an `Object[]` array called `elementData`. When you add elements beyond current capacity, it allocates a new array 1.5x larger and copies all elements using `System.arraycopy()`. Default initial capacity is 10.

### Intermediate
Q: What is the time complexity of `add(E e)`, `add(int index, E e)`, `get(int index)`, and `remove(int index)` in ArrayList?
A:
- `add(E e)`: Amortized O(1). Most appends don't trigger resize. When resize occurs, O(n), but amortized over many adds is O(1).
- `add(int index, E e)`: O(n) because it shifts all elements at `index` and beyond.
- `get(int index)`: O(1) — direct array access.
- `remove(int index)`: O(n) — shifts elements after `index` left by one.

### Senior
Q: You need to insert 100,000 elements at the beginning of a List. Comparing `ArrayList` with `LinkedList`, which is better and why? Is there a third option?
A: `ArrayList.add(0, e)` is O(n) per insertion → O(n²) total for 100k inserts. `LinkedList.add(0, e)` is O(1) → O(n) total. So `LinkedList` wins for this specific pattern.

Third option: `ArrayDeque` (preferred over `LinkedList` for queue/stack):
```java
Deque<Integer> deque = new ArrayDeque<>(100_000);
for (int i = 0; i < 100_000; i++) deque.addFirst(i); // amortized O(1) each
```

`ArrayDeque` is faster than `LinkedList` for queue operations because:
1. No node allocation per element (contiguous array)
2. Better cache locality
3. Lower GC pressure

### Tricky
Q: `Arrays.asList("a", "b", "c")` returns a List. Can you call `add()` or `remove()` on it? What if you wrap it in `new ArrayList<>()`? Why does modifying the backing array reflect in the list?
A: `Arrays.asList()` returns a fixed-size list backed by the array. `add()`/`remove()` throw `UnsupportedOperationException` because the list's size is fixed to the array length. However, `set()` works — it modifies the array in place.

`new ArrayList<>(Arrays.asList(...))` creates a proper ArrayList with its own internal array. Full mutability.

Modifying the backing array reflects in the list because the list stores the original array reference:
```java
String[] arr = {"a", "b"};
List<String> list = Arrays.asList(arr);
arr[0] = "z";
System.out.println(list); // [z, b] — same array object
```

## 10. Final 30-Second Answer

ArrayList = resizable array backed by `Object[]`. Amortized O(1) `add()` at end via 1.5x resize. O(n) `add(index, e)` / `remove(index)` due to array shifting. O(1) `get(index)`. Fail-fast iterator via `modCount`. `Arrays.asList()` returns fixed-size view — wrap `new ArrayList<>()` for mutability. `subList()` is view into parent. **Pre-size constructor** to avoid resize cost. Not thread-safe. `removeIf()` for bulk removal. Use `ArrayDeque` for queue/stack.