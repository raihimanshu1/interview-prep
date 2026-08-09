# Module — More Collections: WeakHashMap, EnumMap, DelayQueue, ConcurrentLinkedQueue — Q&A

> **Skill**: 7+ years — covers WeakHashMap reference queues, EnumMap array backing, DelayQueue priority queue internals, ConcurrentLinkedQueue lock-free algorithm.

---

## Q1. WeakHashMap — Automatic Entry Removal via GC

```java
// =====================================================
// WeakHashMap — Keys held with WEAK references
// =====================================================

Map<Key, Value> cache = new WeakHashMap<>();

// Key points:
// - Keys are stored as WeakReference<Object>
// - When key is no longer strongly reachable → GC collects key
// - Entry is automatically removed from map (via ReferenceQueue)
// - Values are held with strong references (use WeakValueMap if needed)

// Internal mechanics:
class WeakHashMapEntry extends WeakReference<Object> implements Map.Entry {
    private final int hash;
    private Object value;
    private final ReferenceQueue<Object> queue;  // GC notifies here
    
    // When GC collects key:
    // 1. ReferenceQueue.poll() returns cleared WeakReference
    // 2. WeakHashMap.expungeStaleEntries() removes from table
    // 3. Table slot set to null → GC'd
}

// =====================================================
// USE CASE: Canonicalizing Map (memory-sensitive caching)
// =====================================================

// Problem: Large number of unique keys, only some are "hot"
// Solution: WeakHashMap auto-evicts cold keys

class ImageCache {
    private final Map<ImageId, BufferedImage> cache = new WeakHashMap<>();
    
    public BufferedImage get(ImageId id) {
        return cache.get(id);  // Returns null if GC'd
    }
    
    public void put(ImageId id, BufferedImage image) {
        cache.put(id, image);
        // GC runs → if no strong refs to ImageId → entire entry removed
    }
}

// DANGER: Values hold strong refs to Keys → leak!
Map<Key, Value> bad = new WeakHashMap<>();
Key k = new Key();
bad.put(k, new Value(k));  // Value holds strong ref to Key!
// Key NEVER GC'd! → Memory leak!

// FIX: Use Guava MapMaker or Caffeine for proper weak/soft values
```

## Q2. EnumMap — Optimal Map for Enum Keys

```java
// =====================================================
// EnumMap — Array-backed map for enum keys
// =====================================================

// Backed by Object[] array, indexed by enum ordinal
// Performance: O(1) get/put, no hashing, no boxing!

enum Status { PENDING, APPROVED, REJECTED }

EnumMap<Status, String> map = new EnumMap<>(Status.class);
map.put(Status.PENDING, "Waiting");
map.put(Status.APPROVED, "Done");

// Internal representation:
// Object[] table = new Object[Status.values().length];  // 3 elements
// table[0] = "Waiting";   // PENDING.ordinal() = 0
// table[1] = "Done";      // APPROVED.ordinal() = 1
// table[2] = null;        // REJECTED.ordinal() = 2

// BENEFITS:
// - No hashCode() or equals() calls on keys
// - No boxing (enum ordinals are ints)
// - NO null keys (throws NPE)
// - Iteration in natural enum order (PENDING → APPROVED → REJECTED)

// vs HashMap<Status, String>:
// HashMap: hash(key) → bucket, equals() check, possible collisions
// EnumMap: ordinal() → array index in ONE operation
// SPEED: EnumMap is ~2x faster than HashMap for enum keys
```

## Q3. DelayQueue — Time-Based Priority Queue

```java
// =====================================================
// DelayQueue — Elements become available after delay
// =====================================================

// Elements must implement Delayed interface:
class ExpiringCacheEntry implements Delayed {
    private String key;
    private Object value;
    private long expiryTime;  // When this entry expires
    
    public ExpiringCacheEntry(String key, Object value, long ttlSeconds) {
        this.key = key;
        this.value = value;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }
    
    // Remaining delay (in nanoseconds)
    @Override
    public long getDelay(TimeUnit unit) {
        long remaining = expiryTime - System.currentTimeMillis();
        return unit.convert(remaining, TimeUnit.MILLISECONDS);
    }
    
    // Compare delays to order PriorityQueue
    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.expiryTime, ((ExpiringCacheEntry) other).expiryTime);
    }
}

// Usage:
DelayQueue<ExpiringCacheEntry> queue = new DelayQueue<>();

// Add entries with different TTLs:
queue.put(new ExpiringCacheEntry("A", "valueA", 5));   // Expires in 5s
queue.put(new ExpiringCacheEntry("B", "valueB", 10));  // Expires in 10s

// Take() blocks until next entry expires:
ExpiringCacheEntry expired = queue.take();  // Blocks until "A" expires (5s)
// "A" is removed, "B" remains (7 more seconds)

// Real-world: Time-based cache, retry queue, scheduled task executor
```

## Q4. ConcurrentLinkedQueue — Lock-Free Queue Internals

```java
// =====================================================
// ConcurrentLinkedQueue — Michael-Scott Queue (MS Queue)
// =====================================================

// Lock-free, wait-free, non-blocking queue
// Uses CAS on head/tail pointers

// Internal node structure:
class Node {
    volatile E item;
    volatile Node next;
    
    Node(E item) {
        this.item = item;
        this.next = null;
    }
}

// Head and tail are always real nodes (sentinel/dummy):
private transient volatile Node<E> head;
private transient volatile Node<E> tail;

// Initial state: head →tail→ null (both point to sentinel)
// After offer("A"): head → sentinel → tail → [A] → null
// After offer("B"): head → sentinel → tail → [B] → null
//                                   ↑
//                        both point to same (only element)

// =====================================================
// OFFER — CAS-based (simplified)
// =====================================================

public boolean offer(E e) {
    final Node<E> newNode = new Node<>(e);
    
    for (;;) {
        Node<E> t = tail;
        Node<E> s = t.next;
        
        if (t == tail) {  // Consistent snapshot check
            if (s == null) {  // Tail points to last node
                if (casNext(t, null, newNode)) {  // Try to link new node
                    casTail(t, newNode);  // Swing tail forward
                    return true;
                }
            } else {
                // Tail lagged behind — help advance it
                casTail(t, s);
            }
        }
    }
}

// =====================================================
// POLL — CAS-based (simplified)
// =====================================================

public E poll() {
    for (;;) {
        Node<E> h = head;
        Node<E> t = tail;
        Node<E> first = h.next;
        
        if (h == head) {
            if (h == t) {  // Empty queue
                return null;
            }
            
            E item = first.item;
            if (item != null) {  // Item not being offered
                casItem(first, null);  // Clear item (mark as consumed)
                casHead(h, first);  // Advance head
                return item;
            }
        }
    }
}

// PROBLEMS WITH MS Queue:
// 1. ABA problem possible (for nodes, not items)
// 2. Memory reclamation: removed nodes stay in queue until GC
//    → Use version counters or hazard pointers in highly concurrent env
// 3. Tail pointer can lag behind (but self-corrects)
```

## Senior Q&A

**Q**: Why use WeakHashMap when Guava Cache exists?

**A**: Guava Cache (Caffeine) is superior for most production use: supports eviction by size/time, statistics, automatic loading. WeakHashMap should be used ONLY when you need automatic removal based on GC and nothing else. WeakHashMap is: (1) Slow under GC pressure (ReferenceQueue processing); (2) No size-based eviction; (3) Values held strongly (memory leak). Use WeakHashMap sparingly for canonicalizing maps where keys are externally referenced and values are lightweight. Prefer Caffeine with weakValues() for production caching.

**Q**: EnumMap vs switch statement — which is faster?

**A**: EnumMap is faster for >=3 lookups. Switch statement compiles to tableswitch (O(1) for ordinals) but requires inline all cases. EnumMap is better when: (1) Lookup key is dynamic (not compile-time constant); (2) You need to iterate in enum order; (3) Map needs to be passed around. Switch is best for fixed logic with few cases.

**Q**: ConcurrentLinkedQueue vs ArrayDeque in multi-producer scenario?

**A**: ArrayDeque is NOT thread-safe (throws ConcurrentModificationException). ConcurrentLinkedQueue is lock-free and supports multi-producer, multi-consumer (MPMC). Use ConcurrentLinkedQueue when multiple threads enqueue/dequeue concurrently. For single-producer, single-consumer (SPSC), ArrayDeque is faster (no CAS overhead) but MUST be externally synchronized.

**Final 30-Second**: WeakHashMap auto-evicts via GC (WeakReference keys). EnumMap uses array indexed by ordinal — fastest for enum keys, no hashing. DelayQueue is priority queue with time-based blocking (Delayed interface). ConcurrentLinkedQueue: Michael-Scott lock-free queue, CAS on head/tail, multi-producer safe.