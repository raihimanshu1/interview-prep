# AtomicInteger and Atomic Classes — Complete Deep Dive

## 1. Why This Concept Matters

Atomic classes in `java.util.concurrent.atomic` provide lock-free, thread-safe operations on single variables. They are fundamental to building high-performance concurrent systems without `synchronized`. Understanding CAS (Compare-And-Swap), memory visibility, and when to use atomic classes vs locks is essential. In production, atomic classes are used for counters, sequence generators, and lock-free algorithms. Interviewers test this because it reveals your understanding of hardware-level concurrency, the JMM, and lock-free programming.

Misunderstanding atomic classes causes:
- Race conditions from assuming `++` is atomic (it's not)
- Performance issues from unnecessary `synchronized` blocks
- ABA problems in lock-free algorithms
- Confusion about memory visibility guarantees

## 2. Basic Meaning

Atomic classes use **CAS (Compare-And-Swap)** — a CPU instruction that atomically compares a memory location to an expected value and, if equal, updates it to a new value.

**Key vocabulary:**
- **CAS**: CPU-level atomic operation (compare-and-swap)
- **`AtomicInteger`**: atomic int with `incrementAndGet()`, `compareAndSet()`, etc.
- **`AtomicLong`**: atomic long
- **`AtomicBoolean`**: atomic boolean
- **`AtomicReference<V>`**: atomic reference to any object
- **`AtomicStampedReference<V>`**: prevents ABA problem with version stamp
- **`LongAdder`**: high-throughput counter (better than `AtomicLong` for contention)
- **`LazyInitialization`**: some atomic classes use unsafe for memory access

What it is NOT: Atomic classes are not a replacement for `synchronized` in all cases. They only work on single variables, not compound operations across multiple variables.

## 3. Real Code / Real Example

```java
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;

public class AtomicDemo {
    public static void main(String[] args) throws InterruptedException {
        // === ATOMICINTEGER: THREAD-SAFE COUNTER ===
        AtomicInteger counter = new AtomicInteger(0);
        
        // Increment atomically
        counter.incrementAndGet(); // returns 1
        counter.getAndIncrement(); // returns 1, then increments to 2
        counter.addAndGet(5);     // returns 7
        System.out.println("Counter: " + counter.get()); // 7

        // === COMPARE-AND-SET ===
        AtomicInteger max = new AtomicInteger(0);
        boolean updated = max.compareAndSet(0, 100); // true (0 -> 100)
        System.out.println("Updated: " + updated + ", max: " + max.get()); // true, 100
        boolean failed = max.compareAndSet(0, 200); // false (current is 100, not 0)
        System.out.println("Failed CAS: " + failed); // false

        // === ATOMICLONG ===
        AtomicLong total = new AtomicLong(0L);
        total.addAndGet(100L);
        System.out.println("Total: " + total.get()); // 100

        // === ATOMICBOOLEAN ===
        AtomicBoolean flag = new AtomicBoolean(false);
        flag.compareAndSet(false, true);
        System.out.println("Flag: " + flag.get()); // true

        // === ATOMICREFERENCE ===
        AtomicReference<String> ref = new AtomicReference<>("hello");
        ref.compareAndSet("hello", "world");
        System.out.println("Ref: " + ref.get()); // world

        // === CONCURRENT UPDATES (race condition with non-atomic) ===
        AtomicInteger safeCounter = new AtomicInteger(0);
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                safeCounter.incrementAndGet(); // atomic
            }
        };
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("Safe counter: " + safeCounter.get()); // 2000

        // === LONGADDER: HIGH-THROUGHPUT (better under contention) ===
        LongAdder adder = new LongAdder();
        // Multiple threads increment simultaneously
        for (int i = 0; i < 10; i++) {
            new Thread(() -> { for (int j = 0; j < 1000; j++) adder.increment(); }).start();
        }
        Thread.sleep(100); // wait for threads
        System.out.println("LongAdder: " + adder.sum()); // 10000

        // === ATOMICSTAMPEDREFERENCE: PREVENT ABA ===
        AtomicStampedReference<String> stamped = new AtomicStampedReference<>("A", 0);
        int stamp = stamped.getStamp();
        boolean casOk = stamped.compareAndSet("A", "B", stamp, stamp + 1);
        System.out.println("CAS stamped: " + casOk + ", value: " + stamped.getReference()); // true, B
    }
}
```

Expected output:
```
Counter: 7
Updated: true, max: 100
Failed CAS: false
Flag: true
Ref: world
Safe counter: 2000
LongAdder: 10000
CAS stamped: true, value: B
```

## 4. What Happens Internally

**CAS operation (hardware level):**
```java
// Simplified CAS via sun.misc.Unsafe
public final boolean compareAndSet(int expected, int update) {
    return unsafe.compareAndSwapInt(this, valueOffset, expected, update);
}
```

CPU instructions:
- `LOCK CMPXCHG` on x86: atomically compare register with memory, swap if equal
- Memory barrier ensures visibility across cores
- If CAS fails (another thread modified value), JVM retries loop

**`AtomicInteger` structure:**
```java
public class AtomicInteger extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 0L;
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;
    
    private volatile int value; // volatile for visibility
    
    static {
        try { valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value")); }
        catch (Exception e) { throw new Error(e); }
    }
    
    public AtomicInteger(int initial) { this.value = initial; }
    
    public final int get() { return value; } // volatile read
    
    public final int incrementAndGet() {
        for (;;) { // CAS loop
            int current = get();
            int next = current + 1;
            if (compareAndSet(current, next)) return next;
            // CAS failed: another thread changed value, retry
        }
    }
    
    public final boolean compareAndSet(int expected, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expected, update);
    }
}
```

**CAS loop pattern:**
```java
for (;;) { // infinite retry loop
    int current = unsafe.getIntVolatile(this, valueOffset);
    int next = current + 1;
    if (current == expected && unsafe.compareAndSwapInt(this, valueOffset, current, next)) {
        return next; // success
    }
    // else: retry with updated current value
}
```

**`LongAdder` structure (Java 8+):**
`LongAdder` uses cell-based accumulation for high contention:
```java
public class LongAdder extends Number {
    private transient volatile Cell[] cells;
    private transient volatile long base;
    
    // Cell: contention bin
    private static final class Cell {
        volatile long value;
        final Unsafe unsafe;
        // CAS update on this cell only
    }
    
    public void increment() {
        long current = base;
        if (!addCurrent(current)) { // try base CAS first
            Cell[] cs = cells;
            if (cs != null) {
                // hash thread to cell, CAS that cell
                Cell c = cs[(int)threadHash % cs.length];
                c.value.increment(); // CAS loop on cell
            }
        }
    }
}
```

Multiple cells reduce contention: each thread updates different cell. `sum()` adds all cells + base.

**Memory visibility:**
- `volatile` write (CAS success) writes to main memory
- `volatile` read (get()) reads from main memory
- Happens-before established by successful CAS

## 5. Tricky Interview Cases

**Case 1 — `++` is NOT atomic**
```java
AtomicInteger a = new AtomicInteger(0);
// a++ is NOT atomic — it's get() + add + put()
int old = a.get();
a.set(old + 1); // race condition between get and set!
```
Output: Lost updates possible.
Fix: Use `incrementAndGet()` or `addAndGet()`.

**Case 2 — `compareAndSet` loop for atomic increment**
```java
AtomicInteger ai = new AtomicInteger(0);
// Manual CAS loop (what incrementAndGet does internally)
for (;;) {
    int current = ai.get();
    int next = current + 1;
    if (ai.compareAndSet(current, next)) break;
}
System.out.println(ai.get()); // 1
```
Output: `1`
Explanation: `compareAndSet` returns `false` if another thread modified `ai` between `get()` and CAS. Loop retries.

**Case 3 — ABA problem**
```java
AtomicReference<String> ref = new AtomicReference<>("A");
// Thread 1: reads "A", then context switch
// Thread 2: CAS "A" -> "B", then CAS "B" -> "A"
// Thread 1 resumes: CAS "A" -> "C" succeeds! (thinks still "A")
// But value was changed and changed back!
boolean ok = ref.compareAndSet("A", "C");
System.out.println("ABA ok: " + ok + ", value: " + ref.get()); // true, C
```
Output: CAS succeeds but value was modified in between.
Explanation: `AtomicReference` only tracks value, not version. ABA problem.

Fix: `AtomicStampedReference` adds version stamp:
```java
AtomicStampedReference<String> stamped = new AtomicStampedReference<>("A", 0);
// Thread 1 sees stamp 0
// Thread 2 changes to "B", stamp 1, then back to "A", stamp 2
// Thread 1 CAS with stamp 0 fails (current stamp is 2)
```

**Case 4 — `LongAdder` vs `AtomicLong` under contention**
```java
// High contention: many threads updating same counter
AtomicLong atomic = new AtomicLong(0);
LongAdder adder = new LongAdder();
for (int i = 0; i < 100; i++) {
    new Thread(() -> {
        for (int j = 0; j < 1000; j++) {
            atomic.incrementAndGet(); // CAS contention on single cell
            adder.increment();         // thread-local cell, less contention
        }
    }).start();
}
```
Output: Under high thread count, `LongAdder` is 2-5x faster.
Explanation: `AtomicLong` uses single CAS on one variable. `LongAdder` spreads updates across cells, reducing cache line bouncing.

**Case 5 — `AtomicIntegerArray`**
```java
AtomicIntegerArray arr = new AtomicIntegerArray(5);
arr.set(0, 10); // atomic write
arr.compareAndSet(1, 0, 20); // atomic CAS on index 1
System.out.println(arr.get(0) + ", " + arr.get(1)); // 10, 20
```
Output: `10, 20`
Explanation: `AtomicIntegerArray` - each element is independently atomic. Uses unsafe to access array elements.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `a++` on AtomicInteger | Not atomic (get + add + set) | Use `incrementAndGet()` |
| `compareAndSet` without loop | Single attempt may fail under contention | Loop until success or use `compareAndSet` in conditional |
| `AtomicLong` for simple counting | Single-cell contention under high concurrency | Use `LongAdder` for counters |
| `AtomicReference` with mutable object | Reference atomicity doesn't make object thread-safe | Make referenced object immutable |
| Forgetting `volatile` semantics | Visibility issues if not using atomic classes | Atomic classes use volatile + CAS for visibility |
| `AtomicIntegerFieldUpdater` misuse | Field must be `volatile` and non-static | Check constraints: `volatile int` instance field |
| ABA with AtomicReference | Lost changes between read and CAS | Use `AtomicStampedReference` |

## 7. Production Usage

**Thread-safe counter:**
```java
// Simple counter
AtomicLong requestCount = new AtomicLong(0);
requestCount.incrementAndGet();
// High-concurrency counter
LongAdder activeUsers = new LongAdder();
activeUsers.increment();
long total = activeUsers.sum();
```

**Sequence generator:**
```java
AtomicLong sequence = new AtomicLong(0);
public long nextId() { return sequence.incrementAndGet(); }
// Each call returns unique, monotonically increasing ID
```

**Lock-free stack (Treiber stack):**
```java
class LockFreeStack<T> {
    private final AtomicReference<Node<T>> top = new AtomicReference<>(null);
    
    void push(T item) {
        Node<T> newNode = new Node<>(item);
        do {
            Node<T> current = top.get();
            newNode.next = current;
        } while (!top.compareAndSet(current, newNode)); // CAS loop
    }
    
    T pop() {
        while (true) {
            Node<T> current = top.get();
            if (current == null) return null;
            Node<T> next = current.next;
            if (top.compareAndSet(current, next)) return current.item;
            // CAS failed: retry
        }
    }
}
```

**Rate limiter with AtomicLong:**
```java
class RateLimiter {
    private final AtomicLong lastCall = new AtomicLong(0);
    private final long minIntervalMs;
    
    boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long prev = lastCall.get();
        if (now - prev >= minIntervalMs) {
            return lastCall.compareAndSet(prev, now);
        }
        return false;
    }
}
```

## 8. Advanced Details

- **`sun.misc.Unsafe`:** Atomic classes use JVM-internal `Unsafe` class for CAS. `compareAndSwapInt`, `compareAndSwapObject`, `compareAndSwapLong` map to CPU instructions.
- **False sharing:** `AtomicLong` uses single `volatile long` field. Multiple threads updating it cause cache line bouncing (false sharing). `LongAdder` mitigates with cell array.
- **`VarHandle` (Java 9+):** `AtomicInteger` can be replaced with `VarHandle` for more flexible CAS. `VarHandle` is standardized API (replaces `Unsafe`).
- **Memory ordering:** `compareAndSet` has full memory barrier semantics. Successful CAS acts as both load and store barrier.
- **`getAcquire()` / `setRelease()` (Java 9+):** Weaker ordering than full volatile. For advanced JMM control.
- **Striped64 (LongAdder/DoubleAdder):** Uses power-of-2 cell array, thread hashing to spread contention. Falls back to base CAS when no contention.
- **Performance:** CAS failure rate determines performance. Under low contention, CAS succeeds on first try (fast). Under high contention, retry loops add overhead.

## 9. Interview Questions And Answers

### Beginner
Q: What is CAS? How does AtomicInteger use it?
A: CAS (Compare-And-Swap) is a CPU instruction that atomically compares a memory value to an expected value and updates it if equal. `AtomicInteger.incrementAndGet()` loops: reads current value, computes next, calls `compareAndSet(current, next)`. If another thread modified the value between read and CAS, it retries.

### Intermediate
Q: What is the ABA problem? How does `AtomicStampedReference` solve it?
A: ABA problem: Thread reads value `A`. Another thread changes it to `B` then back to `A`. First thread's CAS succeeds (thinks value unchanged) but it was modified in between.

`AtomicStampedReference` adds a version stamp. Each update increments stamp. CAS checks both value AND stamp:
```java
boolean cas = stampedRef.compareAndSet("A", "C", currentStamp, currentStamp + 1);
```
If intermediate changes occurred, stamp mismatch → CAS fails.

### Senior
Q: You need a high-concurrency counter for a web server tracking requests per second. `AtomicLong` vs `LongAdder` — which do you choose and why?
A: **`LongAdder`.**
- `AtomicLong`: single-cell CAS. Under high concurrency (many threads updating simultaneously), CAS contention causes retry storms.
- `LongAdder`: multiple cells. Each thread updates different cell based on thread hash. Near-zero contention.

Benchmarks show `LongAdder` is 2-5x faster under 8+ threads. Tradeoff: `LongAdder` has higher memory footprint (cell array) and `sum()` must aggregate all cells. For exact current value, `AtomicLong` is better; for throughput counting where occasional aggregation is OK, `LongAdder` wins.

## 10. Final 30-Second Answer

Atomic classes = lock-free thread-safe single variables via CAS (CPU-level compare-and-swap). `AtomicInteger`, `AtomicLong`, `AtomicBoolean`, `AtomicReference`. `compareAndSet(expected, update)` — succeeds only if current equals expected. Loop for atomic read-modify-write. **`LongAdder`** for high-contention counters (cell-based). **`AtomicStampedReference`** prevents ABA. Visibility via `volatile` + CAS memory barriers. Use instead of `synchronized` for single-variable atomics. Not for compound operations across multiple variables.