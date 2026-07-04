# Race Conditions & Producer-Consumer — Complete Deep Dive

## 1. Why This Concept Matters

Race conditions occur when multiple threads access shared mutable data without proper synchronization, producing unpredictable results. The producer-consumer problem is the classic synchronization challenge — coordinating a producer that creates data with a consumer that processes it, using a bounded buffer. These concepts test your understanding of thread safety, synchronization, wait/notify, and BlockingQueue — essential for any concurrent programming role.

## 2. Race Conditions

**Definition:** A race condition occurs when the behavior of software depends on the timing of uncontrollable events like thread scheduling. The classic example is a check-then-act sequence where the state can change between the check and the act.

```java
public class RaceConditionDemo {
    private int counter = 0;
    
    // RACE CONDITION: increment is NOT atomic!
    // counter++ = read → increment → write
    // Thread 1 reads counter=5, Thread 2 reads counter=5
    // Thread 1 writes 6, Thread 2 writes 6 → LOST UPDATE! (should be 7)
    public void increment() {
        counter++;  // Not thread-safe!
    }
    
    // FIX 1: synchronized
    public synchronized void incrementSafe() { counter++; }
    
    // FIX 2: AtomicInteger
    private final AtomicInteger atomicCounter = new AtomicInteger();
    public void incrementAtomic() { atomicCounter.incrementAndGet(); }
    
    // FIX 3: ReentrantLock
    private final Lock lock = new ReentrantLock();
    public void incrementLock() {
        lock.lock();
        try { counter++; } 
        finally { lock.unlock(); }
    }
}
```

**Check-then-act race condition (most common):**
```java
// BAD — thread-safe collection but check-then-act is racy
if (!map.containsKey(key)) {  // CHECK
    map.put(key, value);       // ACT — another thread may have put it!
}

// FIX — atomic operation
map.putIfAbsent(key, value);
```

## 3. Producer-Consumer

**Classic problem:** One or more producers generate data into a shared buffer. One or more consumers take data from the buffer. Producers must wait if buffer is full. Consumers must wait if buffer is empty.

### Solution 1: BlockingQueue (simplest, preferred)

```java
public class ProducerConsumerBlockingQueue {
    private static final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
    
    static class Producer implements Runnable {
        public void run() {
            try {
                while (true) {
                    String item = produceItem();
                    queue.put(item);  // Blocks if queue is full
                    System.out.println("Produced: " + item);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }
    
    static class Consumer implements Runnable {
        public void run() {
            try {
                while (true) {
                    String item = queue.take();  // Blocks if queue is empty
                    processItem(item);
                    System.out.println("Consumed: " + item);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }
}

// Usage:
ExecutorService exec = Executors.newFixedThreadPool(3);
exec.submit(new Producer());
exec.submit(new Consumer());
exec.submit(new Consumer()); // 2 consumers
```

### Solution 2: wait/notify (low-level)

```java
public class ProducerConsumerWaitNotify {
    private final List<String> buffer = new ArrayList<>();
    private final int capacity = 10;
    
    public synchronized void produce(String item) throws InterruptedException {
        while (buffer.size() == capacity) {
            wait();  // Release lock, wait for consumer to take
        }
        buffer.add(item);
        notifyAll();  // Wake up waiting consumers
    }
    
    public synchronized String consume() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait();  // Release lock, wait for producer to add
        }
        String item = buffer.remove(0);
        notifyAll();  // Wake up waiting producers
        return item;
    }
}
```

## 4. StringBuilder vs StringBuffer

```java
// StringBuffer: thread-safe (synchronized methods) — SLOWER
StringBuffer sb = new StringBuffer();
sb.append("hello"); // synchronized

// StringBuilder: NOT thread-safe — FASTER (10-20x in multi-threaded)
StringBuilder sb2 = new StringBuilder();
sb2.append("hello"); // not synchronized

// In single-threaded code: ALWAYS use StringBuilder
// StringBuffer is legacy — only use if you need thread-safety at String level
```

## 5. WeakReference, WeakHashMap

```java
// Strong reference (normal) — prevents GC
String strong = new String("hello");  // GC cannot collect

// Weak reference — GC CAN collect the object
WeakReference<String> weak = new WeakReference<>(new String("hello"));
System.out.println(weak.get()); // "hello"
System.gc();
System.out.println(weak.get()); // null (collected!)

// WeakHashMap — entries are automatically removed when key is no longer strongly referenced
WeakHashMap<UniqueKey, String> cache = new WeakHashMap<>();
// When key has no strong references outside the map, GC removes the entry
// Used for: caches where entries should expire when key is no longer in use
```

## 6. Fail-Fast vs Fail-Safe Iterators

```java
// FAIL-FAST: throws ConcurrentModificationException if map is modified during iteration
Map<String, String> map = new HashMap<>();
map.put("a", "1");
for (String key : map.keySet()) {
    map.put("b", "2"); // ConcurrentModificationException!
}

// FAIL-SAFE: works on a snapshot — allows modification during iteration
ConcurrentHashMap<String, String> chm = new ConcurrentHashMap<>();
chm.put("a", "1");
for (String key : chm.keySet()) {
    chm.put("b", "2"); // OK — iterator uses snapshot, does NOT throw
}
```

## 7. Final 30-Second Answer

**Race condition**: multiple threads access shared data without sync. Fix: synchronized, AtomicInteger, Lock. **Producer-Consumer**: use `BlockingQueue.put()/take()` (simplest), or `wait/notify` (low-level). **StringBuilder**: NOT thread-safe (FAST). **StringBuffer**: thread-safe via synchronized (SLOW, legacy). Use StringBuilder for single-threaded. **WeakHashMap**: entries removed when key has no strong references. **Fail-fast**: HashMap throws if modified during iteration. **Fail-safe**: ConcurrentHashMap uses snapshot — safe to modify during iteration.