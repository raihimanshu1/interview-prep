# Module 5c — Advanced Java 8+: Spliterator, Custom Collector, ForkJoinPool — Q&A

> **Skill**: 7+ years depth — covers parallel stream internals, Spliterator mechanics, custom Collector design, ForkJoinPool work-stealing.

---

## Q1. Spliterator Internals — How Parallel Streams Split Work

### 1. Why This Matters at Senior Level
Spliterator is the foundation of ALL parallel stream operations. Without understanding trySplit() mechanics, you can't optimize parallel performance or create custom parallel-capable data sources.

### 2. Spliterator Interface Deep Dive

```java
// =====================================================
// Spliterator — Splitable Iterator (Java 8+)
// =====================================================

public interface Spliterator<T> {
    // Try to split: partition remaining elements into a NEW Spliterator
    // Returns null if cannot split further
    Spliterator<T> trySplit();
    
    // Try to advance: process next element in current partition
    boolean tryAdvance(Consumer<? super T> action);
    
    // Estimate remaining elements remaining in this partition
    long estimateSize();
    
    // Characteristics: ordered, distinct, sorted, sized, etc.
    int characteristics();
}

// =====================================================
// CHARACTERISTICS FLAGS
// =====================================================

// Characteristics determine WHICH operations can be optimized:
public static final int ORDERED    = 0x00000010;  // Has encounter order
public static final int DISTINCT   = 0x00000001;  // All elements unique
public static final int SORTED     = 0x00000004;  // Sorted by Comparator
public static final int SIZED      = 0x00000040;  // Known exact size
public static final int NONNULL    = 0x00000100;  // No null elements
public static final int IMMUTABLE  = 0x00000400;  // Source can't be modified
public static final int CONCURRENT = 0x00001000;  // Source can be safely modified
public static final int SUBSIZED   = 0x00004000;  // trySplit produces SIZED spliterators

// =====================================================
// HOW ARRAYLIST SPLITWORKS (simplified from JDK source)
// =====================================================

// ArrayListSpliterator.trySplit():
public Spliterator<E> trySplit() {
    int lo = index, mid = (lo + fence) >>> 1;  // Binary split!
    // Split range [lo, fence) into [lo, mid) and [mid, fence)
    if (lo >= mid) return null;  // Too small to split
    index = mid;
    return new ArrayListSpliterator<>(list, lo, mid, ...);
    // Returns: first half as NEW Spliterator
    // Keeps: second half for itself
}

// =====================================================
// PERFECT SPLITTING (Balanced Binary Tree)
// =====================================================

// List of 8 elements:
//                 [0-8]
//              /         \
//          [0-4]         [4-8]
//         /     \        /    \
//      [0-2]  [2-4]  [4-6]  [6-8]
//      /  \    /  \    /  \    /  \
//    [0][1] [2][3] [4][5] [6][7]
//
// Depth: log₂(8) = 3
// Splits: 7 total for 8 elements
// Each leaf = 1 element

// =====================================================
// CUSTOM SPLITERATOR — For your own data source
// =====================================================

class RangeSpliterator implements Spliterator<Integer> {
    private int start, end;
    
    public RangeSpliterator(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public Spliterator<Integer> trySplit() {
        int mid = (start + end) >>> 1;  // Binary split
        if (mid == start) return null;   // Size 1 — can't split
        
        Spliterator<Integer> prefix = new RangeSpliterator(start, mid);
        start = mid;  // Keep suffix
        return prefix;
    }
    
    @Override
    public boolean tryAdvance(Consumer<? super Integer> action) {
        if (start < end) {
            action.accept(start++);
            return true;
        }
        return false;  // No more elements
    }
    
    @Override
    public long estimateSize() {
        return end - start;
    }
    
    @Override
    public int characteristics() {
        return SIZED | SUBSIZED | IMMUTABLE | ORDERED;
    }
}
```

### 3. Custom Collector — Advanced Example

```java
// =====================================================
// CUSTOM COLLECTOR — Grouping + Aggregation
// =====================================================

// Problem: Group orders by status AND compute stats per group
// Stream API provides groupingBy(), but not with this complexity

class OrderStats {
    private int count;
    private BigDecimal total;
    private BigDecimal average;
    // constructor, getters
    
    public OrderStats() {
        this.count = 0;
        this.total = BigDecimal.ZERO;
        this.average = BigDecimal.ZERO;
    }
    
    public void accept(Order order) {
        count++;
        total = total.add(order.getAmount());
        average = total.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
    }
    
    public OrderStats combine(OrderStats other) {
        // MERGE two partial results (for parallel streams!)
        count += other.count;
        total = total.add(other.total);
        average = total.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
        return this;
    }
}

// Custom Collector:
Collector<Order, OrderStats, OrderStats> statsCollector = 
    Collector.of(
        OrderStats::new,           // Supplier: create accumulator
        OrderStats::accept,        // Accumulator: add one element
        OrderStats::combine,       // Combiner: merge two accumulators (parallel!)
        Collector.Characteristics.CONCURRENT  // Optional: thread-safe
    );

// Usage:
Map<String, OrderStats> statsByStatus = orders.stream()
    .collect(Collectors.groupingBy(
        Order::getStatus,
        statsCollector
    ));
// Result: Map<String, OrderStats> — one stats object per status

// =====================================================
// TEING COLLECTOR — Combine two collectors
// =====================================================

// Count + sum in ONE pass:
Map<String, Map<String, Object>> result = orders.stream()
    .collect(Collectors.groupingBy(
        Order::getStatus,
        Collectors.teeing(
            Collectors.counting(),           // Collector 1: count
            Collectors.summingDouble(Order::getAmount), // Collector 2: sum
            (count, sum) -> Map.of("count", count, "sum", sum)  // Merge
        )
    ));
```

### 4. ForkJoinPool Work-Stealing

```java
// =====================================================
// FORKJOINPOOL — The Engine Behind Parallel Streams
// =====================================================

// Work-Stealing Algorithm:
// 
// Each worker thread has a DEQUE (double-ended queue) of tasks
// Thread works on its OWN deque (LIFO — push/pop from top)
// Thread steals from OTHER threads' deque bottoms (FIFO)
//
// Why LIFO for own, FIFO for steal?
// - Own tasks are the MOST RECENTLY created (likely smallest)
// - Stolen tasks are the OLDEST (largest) — maximize parallelism
//
// Worker-1 deque:  [TaskA, TaskB]    (TaskA = oldest, TaskB = newest)
// Worker-2 deque:  [TaskC]           (idle → steal from Worker-1)
// Worker-1 pops: TaskB (LIFO)
// Worker-2 steals: TaskA (FIFO from bottom)

// =====================================================
// CUSTOM FORKJOIN TASK
// =====================================================

class SumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10_000;
    private final long[] array;
    private final int start, end;
    
    public SumTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected Long compute() {
        int length = end - start;
        
        // Small enough → compute directly
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) sum += array[i];
            return sum;
        }
        
        // Large → split into two subtasks
        int mid = start + length / 2;
        SumTask left = new SumTask(array, start, mid);
        SumTask right = new SumTask(array, mid, end);
        
        // Fork: submit to work-queue
        left.fork();     // Asynchronously execute left
        // Compute: execute right in CURRENT thread
        Long rightResult = right.compute();
        // Join: wait for left to complete
        Long leftResult = left.join();
        
        // Merge results
        return leftResult + rightResult;
    }
}

// Usage:
ForkJoinPool pool = new ForkJoinPool();  // Or ForkJoinPool.commonPool()
long result = pool.invoke(new SumTask(array, 0, array.length));
```

### 5. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Assuming Spliterator always splits evenly | Uneven partitions cause load imbalance | Check characteristics: SUBSIZED if splitting is balanced |
| Using Collections.synchronizedList with parallel streams | Contention trashes parallelism | Use ConcurrentHashMap or CopyOnWriteArrayList |
| Shared mutable state in parallel streams | Race conditions | Use Collector with CONCURRENT characteristic or ConcurrentHashMap |
| Not calling fork() before compute() | Stack overflow (no parallelism) | Call left.fork() first, then right.compute() |
| Forgetting common pool is shared | CPU starvation from other parallel operations | Use custom ForkJoinPool for long-running tasks |

### 6. Senior Q&A

**Q**: Why does parallelStream() on a LinkedList not improve performance despite having many elements?

**A**: LinkedList.spliterator() has SIZED but NOT SUBSIZED characteristics. The trySplit() implementation traverses nodes linearly to find the midpoint — O(n) just to determine the split point! This traversal cost often outweighs parallelism benefits. ArrayList.spliterator() supports SUBSIZED (splits by index, O(1)), making it suitable for parallel streams. Rule: Only use parallel streams with RANDOM ACCESS data sources (ArrayList, arrays, IntStream.range). Not with LinkedList, TreeSet, or filtered streams.

### 7. Final 30-Second Answer

Spliterator splits work via trySplit() (binary split for ArrayList). ForkJoinPool uses work-stealing: LIFO on own deque, FIFO stealing from others. Custom Collectors need supplier + accumulator + combiner for parallel. Custom ForkJoinTask: fork() to submit, compute() inline, join() to wait. Only use parallel streams with SUBSIZED spliterators (ArrayList, arrays).