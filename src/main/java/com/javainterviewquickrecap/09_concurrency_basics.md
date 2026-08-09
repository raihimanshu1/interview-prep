# Module 4a — Concurrency Deep Dive: JMM, False Sharing, Lock-Free — Senior Q&A

> **Relevance**: 7+ years — covers CPU cache coherence, MESI protocol, false sharing, lock-free algorithms, VarHandle.

---

## Q1. Java Memory Model (JMM) — CPU Cache Coherence & Memory Barriers

### 1. Why This Matters at Senior Level
At 7+ years, you must understand WHY volatile works, not just what it does. The JMM maps to CPU-level memory barriers (LFENCE, SFENCE, MFENCE). Without understanding cache coherence protocols (MESI/MESIF), you can't diagnose visibility issues or design lock-free algorithms.

### 2. CPU Cache Architecture

```
Modern CPU (Skylake, 8-core):
┌─────────────────────────────────────────────────────────────┐
│                           Socket                             │
│                                                              │
│  Core 0             Core 1             Core 7               │
│  ┌─────────┐       ┌─────────┐       ┌─────────┐            │
│  │ L1d 32KB│       │ L1d 32KB│       │ L1d 32KB│            │
│  │ L1i 32KB│       │ L1i 32KB│       │ L1i 32KB│            │
│  │ L2 256KB│       │ L2 256KB│       │ L2 256KB│            │
│  └────┬────┘       └────┬────┘       └────┬────┘            │
│       │                 │                 │                  │
│       └─────────────────┼─────────────────┘                  │
│                         │                                     │
│                ┌────────▼────────┐                            │
│                │   L3 Cache      │  Shared (8MB-30MB)         │
│                │   (SRAM)        │                            │
│                └────────┬────────┘                            │
│                         │                                     │
│                ┌────────▼────────┐                            │
│                │   DRAM (RAM)    │  Main Memory               │
│                └─────────────────┘                            │
└─────────────────────────────────────────────────────────────┘

Latencies:
L1 cache:   ~1ns  (4 cycles)
L2 cache:   ~4ns  (12 cycles)
L3 cache:   ~10ns (40 cycles)
RAM:        ~60ns (250 cycles) — CACHE MISS is 60x slower!
```

### 3. MESI Cache Coherence Protocol

```
MESI states for each Cache Line (64 bytes):

MODIFIED (M): Cache line is DIRTY (modified), in THIS core's cache only
  → Write-back to L3/RAM needed before other cores can read

EXCLUSIVE (E): Cache line is CLEAN, in THIS core's cache only
  → Same as shared but WE'RE the only owner

SHARED (S): Cache line is CLEAN, may be in MULTIPLE cores' caches
  → Read-only copies exist in other cores

INVALID (I): Cache line is STALE — must re-read from L3/RAM

Protocol flow:
Core 0 reads X  → X loaded into L1 (E state — exclusive)
Core 1 reads X  → Core 0 sees read request
                  → Core 0 downgrades X to S (shared)
                  → Core 1 loads X into L1 (S state)
Core 0 writes X → Must INVALIDATE Core 1's copy
                  → Core 1's X becomes I (invalid)
                  → Core 0's X becomes M (modified)
Core 1 reads X  → Cache miss! (was invalidated)
                  → Core 0 writes back to L3 → Core 1 re-reads
                  → THIS IS WHERE VOLATILE MATTERS!
```

### 4. False Sharing — The Silent Performance Killer

```java
// =====================================================
// FALSE SHARING DEMONSTRATION
// =====================================================

// Two independent counters in SAME cache line:
class CounterPair {
    public volatile long counter1 = 0;  // These two fields
    public volatile long counter2 = 0;  // are on SAME cache line!
}

// Thread 1 writes counter1
// Thread 2 writes counter2
// THESE ARE INDEPENDENT — no sharing needed!
// But they SHARE a cache line (64 bytes, adjacent in memory)
// Each write INVALIDATES the other thread's cache!
// Result: 100x slower than expected!

// =====================================================
// FIX: Cache line padding (Java 8-)
// =====================================================

// Padding to ensure fields are on DIFFERENT cache lines:
class PaddedCounterPair {
    // Padding: fill 64 bytes before counter1
    public volatile long p1, p2, p3, p4, p5, p6, p7;  // 56 bytes
    public volatile long counter1 = 0;  // Offset 64 → new cache line!
    public volatile long q1, q2, q3, q4, q5, q6, q7;  // 56 bytes padding
    public volatile long counter2 = 0;  // Offset 128 → another cache line
}

// Java 8+: @Contended annotation (JVM flag required: -XX:-RestrictContended)
@jdk.internal.vm.annotation.Contended
class ContendedCounterPair {
    public volatile long counter1 = 0;
    public volatile long counter2 = 0;
}

// =====================================================
// PERFORMANCE IMPACT
// =====================================================

// 2 threads, each updating THEIR OWN field in a tight loop:
// Without padding:   ~20M ops/sec  (cache line bouncing)
// With padding:       ~200M ops/sec (no false sharing!)
// Difference:        ~10x

// Real-world examples where false sharing causes issues:
// 1. Multiple AtomicLong counters in an array
// 2. ThreadPoolExecutor's workers array
// 3. Disruptor ring buffer sequence numbers
// 4. Kafka's producer batch buffers
```

### 5. Lock-Free Programming with CAS

```java
// =====================================================
// CAS (Compare-And-Swap) Internals
// =====================================================

// AtomicInteger.incrementAndGet() implementation:
public final int incrementAndGet() {
    return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
}

// Unsafe.getAndAddInt (HotSpot JIT compiles to LOCK XADD instruction):
public final int getAndAddInt(Object o, long offset, int delta) {
    int v;
    do {
        v = getIntVolatile(o, offset);  // Read current value (volatile!)
    } while (!weakCompareAndSetInt(o, offset, v, v + delta));
    // CAS loop: retry if another thread modified the value
    return v;
}

// CPU instruction: LOCK CMPXCHG (x86)
// LOCK prefix: locks the cache line (MESI) during the operation
// If CAS fails → loop and retry

// =====================================================
// ABA Problem — The Hidden Trap
// =====================================================

// Thread 1 reads: stack top = NodeA
// Thread 1 wants to CAS: stack top from NodeA → NodeB
// BUT between Thread 1's read and CAS:
//   Thread 2 pops NodeA, NodeB 
//   Thread 2 pushes NodeA back!
// Thread 1's CAS succeeds (top STILL points to NodeA)
// BUT: the rest of the data structure has CHANGED!

// Example: Lock-free stack pop:
class Node {
    Node next;
    int value;
}

class LockFreeStack {
    private AtomicReference<Node> top = new AtomicReference<>();
    
    public void push(Node node) {
        while (true) {
            Node currentTop = top.get();
            node.next = currentTop;
            if (top.compareAndSet(currentTop, node)) break;
        }
    }
    
    public Node pop() {
        while (true) {
            Node currentTop = top.get();
            if (currentTop == null) return null;
            Node newTop = currentTop.next;
            // ABA PROBLEM HERE:
            // Between get() and CAS(), another thread could:
            // pop() [currentTop=A] → pop() [B] → push(A) → push(B)
            // Now: top=A, A.next=B → CORRECT
            // BUT: the original currentTop is a DIFFERENT node (reused!)
            if (top.compareAndSet(currentTop, newTop)) {
                return currentTop;
            }
        }
    }
}

// FIX: AtomicStampedReference (version/timestamp)
// Each modification increments a stamp → ABA detected
```

### 6. VarHandle (Java 9+) — Modern CAS

```java
// =====================================================
// VarHandle — replaces Unsafe for CAS operations
// =====================================================

class Point {
    private volatile int x;
    
    private static final VarHandle X;
    
    static {
        try {
            X = MethodHandles.lookup()
                .findVarHandle(Point.class, "x", int.class);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public boolean compareAndSetX(int expected, int newValue) {
        return X.compareAndSet(this, expected, newValue);
    }
    
    public int getAndSetX(int newValue) {
        return (int) X.getAndSet(this, newValue);
    }
    
    public int getXAcquire() {  // Acquire semantics (happens-before)
        return (int) X.getAcquire(this);
    }
    
    public void setXRelease(int value) {  // Release semantics
        X.setRelease(this, value);
    }
}

// VarHandle provides fine-grained memory ordering:
// - plain: no ordering guarantees (fastest)
// - opaque: prevents reordering within thread
// - acquire/release: happens-before boundaries
// - volatile: full ordering (same as volatile keyword)
```

### 7. Senior-Level Concurrency Patterns

```java
// =====================================================
// Pattern 1: Non-blocking bounded queue (Ring Buffer)
// =====================================================
// Used by: Disruptor, LMAX architecture (6M ops/sec)

class RingBuffer<T> {
    private final int size;
    private final Object[] buffer;
    private final AtomicLong writeIndex = new AtomicLong();
    private final AtomicLong readIndex = new AtomicLong();
    
    @SuppressWarnings("unchecked")
    public RingBuffer(int size) {
        this.size = Integer.highestOneBit(size) << 1;  // Power of 2
        this.buffer = new Object[this.size];
    }
    
    public boolean offer(T item) {
        long writePos = writeIndex.get();
        long readPos = readIndex.get();
        
        // Buffer full?
        if (writePos - readPos >= size) return false;
        
        buffer[(int)(writePos & (size - 1))] = item;  // Bitwise mask!
        writeIndex.setRelease(writePos + 1);  // Release: make item visible
        return true;
    }
    
    @SuppressWarnings("unchecked")
    public T poll() {
        long readPos = readIndex.get();
        long writePos = writeIndex.getAcquire();  // Acquire: see latest writes
        
        if (readPos >= writePos) return null;
        
        T item = (T) buffer[(int)(readPos & (size - 1))];
        readIndex.setRelease(readPos + 1);
        return item;
    }
}
// NO locks, NO CAS, just volatile writes!
// Single-producer + single-consumer model (LMAX pattern)
```

### 8. Production Debugging (Concurrency Issues)

**Tools:**
```bash
# Thread dump analysis:
jstack <pid> > threads.txt
# Look for: BLOCKED threads, deadlocks (found one!),
# threads stuck in same method (contention hotspot)

# Heap analysis:
jmap -dump:live,format=b,file=heap.hprof <pid>
# Look for: arrays of waiting threads (thread pool problems)

# JFR (Java Flight Recorder):
-XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=recording.jfr
# Analyze: lock contention, thread allocation, IO wait

# perf (Linux — CPU-level profiling):
perf stat -e cache-misses,cycles,instructions java App
# High cache-misses → false sharing!
```

### 9. Senior-Level Q&As

**Q (Staff)**: Design a counter that supports 100M increments/sec from 1000 threads.

**A**: Use LongAdder, not AtomicLong. LongAdder maintains an array of CounterCells (power of 2, typically 2x CPU cores). Each thread stripes to its own cell via ThreadLocalRandom. No CAS contention because each thread writes to its own cell. sum() is O(n) but increments are O(1) with near-zero contention. Benchmark: AtomicLong with 1000 threads ≈ 3M ops/sec (all threads CAS-same cache line). LongAdder with 1000 threads ≈ 150M ops/sec (each thread writes own cell). For even higher throughput: use ring buffer with batch writes.

**Q (Principal)**: Explain how the JMM guarantees visibility, and how the JIT can break it.

**A**: The JMM defines happens-before: volatile write → read, lock → unlock, thread start → join. But the JIT can reorder instructions if happens-before isn't established. Example: `x = 1; volatileFlag = true;` → JIT cannot reorder (volatile barrier). But `x = 1; y = 2;` → JIT CAN reorder! Worse, the JIT can eliminate "dead code": if a field isn't volatile, the JIT may hoist the read OUTSIDE a loop, making the loop condition stale forever. This is NOT a CPU bug — it's the JIT correctly following the spec. Fix: use volatile or VarHandle with acquire/release semantics. The JMM is a contract between you and the JVM — break it, and the JIT can legally do anything.

**Q (Staff)**: Your application shows 10x performance degradation on a 32-core server vs 8-core. Why?

**A**: Likely **false sharing** amplified by more cores. With 8 cores, 2 threads may share a cache line (minor bouncing). With 32 cores and 8 threads sharing the same cache line, invalidation storms increase quadratically. Each write invalidates ALL other cores' copies → massive cache coherence traffic. Check: (1) CPU profile shows high L1/L2 cache miss rates; (2) `perf stat -e cache-misses` shows 10x more misses on 32 cores; (3) Thread stacks shows most time in system calls (MESI protocol handling). Fix: (1) Pad cache lines to 64 bytes (@Contended); (2) Restructure data so independent fields don't share cache lines; (3) Use thread-local storage where possible.

### 10. Final 30-Second Answer

JMM: happens-before via volatile, synchronized, VarHandle. False sharing: independent fields on same cache line cause 10x slowdown — fix with @Contended or padding. CAS: LOCK CMPXCHG instruction, ABA problem via AtomicStampedReference. VarHandle (Java 9+) replaces Unsafe. Lock-free algorithms (ring buffer) use volatile + bitwise masking. Debug with jstack, jfr, perf stat.