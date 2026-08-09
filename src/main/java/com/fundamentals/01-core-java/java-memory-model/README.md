# Java Memory Model (JMM) — Explained Simply

## Chapter 1: Why Do We Even Need a Memory Model?

### The "Whiteboard in a Team Office" Analogy

Imagine you're on a team of 4 people working in an office.

```
Each person has their own NOTEPAD (CPU Cache).
The team shares one WHITEBOARD (Main Memory/RAM).

Person A writes: "Meeting at 3 PM" on their notepad.
Person B looks at the whiteboard → doesn't see it.
Person B goes to the wrong time. Confusion!

PROBLEM: When Person A writes on their notepad,
         when does it appear on the whiteboard?
         When does Person B see it?
```

This is EXACTLY the problem the Java Memory Model solves.

**JMM = The RULES that define when one thread's changes become visible to other threads.**

### Why This Matters in Real Code

```java
// ─── THE PROBLEM (very common bug) ───
// Two threads share a boolean flag.
// Thread 1 sets it. Thread 2 checks it.

boolean running = true;  // shared between threads

// Thread 1:
public void stop() {
    running = false;  // ← When does Thread 2 SEE this change?
}

// Thread 2:
public void run() {
    while (running) {  // ← Might loop FOREVER!
        // do work
    }
}
```

**Without JMM rules:** Thread 2 might loop forever because it never sees Thread 1's change. The JMM tells us WHY and HOW to fix it.

### What the JMM Is NOT

```
JMM is NOT:
  ✗ The JVM memory areas (heap, stack, metaspace)
  ✗ How JVM manages memory
  ✗ Garbage collection

JMM IS:
  ✓ Rules about WHEN writes from one thread are VISIBLE to another thread
  ✓ Rules about how instructions can be REORDERED
  ✓ The "Happens-Before" relationship
```

---

## Chapter 2: The Core Problem — Three Issues the JMM Solves

### Issue 1: Visibility

**Problem:** One thread writes, another thread doesn't see it.

```
Real World Analogy:
You mail a letter to your friend.
Your friend doesn't know the letter arrived until they check their mailbox.
But if they NEVER check → they NEVER know!

Thread Analogy:
Thread A writes to variable "x = 5"
Thread B reads "x" → might see "0" (old value) or "5" (new value)
The JMM says: Without synchronization, Thread B might see EITHER value!

WHY? Because:
  1. Thread A might keep "x = 5" in its CPU cache (not write to RAM)
  2. Thread B might read from its own stale CPU cache
  3. The compiler might reorder the instructions
```

### Issue 2: Reordering

**Problem:** The compiler, JVM, or CPU can change the ORDER of your instructions.

```java
int a = 1;   // Statement 1
int b = 2;   // Statement 2
int c = a + b;  // Statement 3
```

The JVM can reorder these as long as the RESULT is the same in a SINGLE thread:

```java
int c = a + b;  // Executed FIRST (JVM reordered!)
int a = 1;
int b = 2;
```

**In single-threaded code: PERFECTLY FINE.** Result is always 3.

**In multi-threaded code: DISASTER.**

```java
// Thread 1:
data = 42;          // Write data
ready = true;       // Write flag (signal)

// Thread 2:
if (ready) {        // Check flag
    System.out.println(data);  // Read data → might print 0 !!!
}
```

**Why?** JVM reordered Thread 1:
```java
ready = true;       // Executed FIRST!
data = 42;          // Executed SECOND!
```

Thread 2 sees `ready = true` but reads `data = 0` (not yet written!).

### Issue 3: Atomicity

**Problem:** Some operations that LOOK like one step are actually MULTIPLE steps.

```java
count++;  // This is NOT one operation!
```

```java
// What "count++" REALLY does:
int temp = count;      // Step 1: READ count
temp = temp + 1;       // Step 2: ADD 1
count = temp;          // Step 3: WRITE count back
```

**With two threads:**
```
Thread 1: read count (=5) → add 1 (=6) → WRITE count (=6)
Thread 2:                                read count (=6) → add 1 (=7) → WRITE count (=7)
Result: 7 ✓ (both ran one after another — lucky!)

Thread 1: read count (=5) → add 1 (=6)
Thread 2: read count (=5) → add 1 (=6)  ← BOTH read 5!
Thread 1: WRITE count (=6)
Thread 2: WRITE count (=6)  ← OVERWRITES Thread 1's write!

Result: 6 ✗ (count was incremented twice but only increased by 1!)
```

**This is called a RACE CONDITION.** The JMM defines which operations are atomic.

---

## Chapter 3: What IS the Java Memory Model?

### The Official Definition (Simplified)

> The JMM defines the rules for how threads interact through memory.
> It answers: "When does Thread A's write become visible to Thread B?"

### The Key Concept: Happens-Before

**Happens-Before Rule:** If action A happens-before action B, then B will SEE the effects of A.

```
Simple version:
  If X happens-before Y → X's changes are VISIBLE to Y

Think of it like causality in real life:
  "The alarm rang" happens-before "I woke up"
  "I woke up" happens-before "I brushed my teeth"
  
  Each action sees the effects of the previous one.
```

### The 6 Happens-Before Rules

```
RULE 1: Program Order Rule
  └─ Within a SINGLE thread, each action happens-before the next
  └─ "What you write in order, happens in order"
  └─ Example: x=1; y=2; → x=1 happens-before y=2

RULE 2: Monitor Lock Rule (synchronized)
  └─ An unlock on a lock happens-before every subsequent lock on that SAME lock
  └─ "When you leave a synchronized block, 
       everything you did is visible to the next person who enters"

RULE 3: Volatile Variable Rule
  └─ A write to a volatile field happens-before every subsequent read of that field
  └─ "When you write to volatile, 
       it's IMMEDIATELY visible to everyone who reads it"

RULE 4: Thread Start Rule
  └─ `thread.start()` happens-before any action in the started thread
  └─ "Everything before start() is visible to the new thread"

RULE 5: Thread Join Rule
  └─ All actions in a thread happen-before `thread.join()` returns
  └─ "When you join a thread, you see everything it did"

RULE 6: Transitivity
  └─ If A happens-before B, and B happens-before C, 
       then A happens-before C
  └─ "If your friend knows, and they tell you, you know too"
```

---

## Chapter 4: The 6 Happens-Before Rules — With Real Code

### Rule 1: Program Order Rule

```java
// ─── WITHIN ONE THREAD, ORDER IS PRESERVED ───

int a = 1;        // Action 1
int b = a + 1;    // Action 2
int c = b * 2;    // Action 3

// JMM guarantees: Action 1 → Action 2 → Action 3
// b will ALWAYS be 2, c will ALWAYS be 4
// This is true EVEN if JVM reorders internally
// Why? Because reordering doesn't change the RESULT in single thread
```

### Rule 2: Monitor Lock Rule (synchronized)

```java
// ─── THE CLASSIC EXAMPLE ───
// Thread 1 writes data. Thread 2 reads it.
// synchronized ensures visibility.

public class SharedData {
    private int value = 0;
    private boolean ready = false;
    
    // Thread 1 calls this:
    public synchronized void write() {
        // Step 1: Write data
        value = 42;
        ready = true;
        
        // Step 2: Exit synchronized block
        // ↑ This RELEASES THE LOCK
        // ↑ ALL writes are flushed to main memory
    }
    
    // Thread 2 calls this:
    public synchronized void read() {
        // Step 1: Enter synchronized block
        // ↑ This ACQUIRES THE LOCK
        // ↑ ALL writes from Thread 1 are now VISIBLE
        
        // Step 2: Read data
        if (ready) {
            System.out.println(value);  // ✅ Guaranteed to print 42!
        }
    }
}
```

**Visual:**
```
Thread 1:                             Thread 2:
  write()                                read()
  ┌─────────────────┐                   ┌─────────────────┐
  │ value = 42       │                   │                  │
  │ ready = true     │                   │                  │
  └────────┬────────┘                   │                  │
           │                            │                  │
           ▼                            │                  │
  ┌─────────────────┐                   │                  │
  │ UNLOCK (release)│────── FLUSH ──────▶│ LOCK (acquire)   │
  │ All writes go   │   main memory     │ All reads from   │
  │ to main memory  │                   │ main memory      │
  └─────────────────┘                   └────────┬─────────┘
                                                 │
                                                 ▼
                                       ┌──────────────────┐
                                       │ Read value = 42   │
                                       │ Read ready = true │
                                       │ (SEES ALL writes!)│
                                       └──────────────────┘
```

### Rule 3: Volatile Variable Rule

```java
// ─── VOLATILE = LIGHTWEIGHT VISIBILITY ───
// Use when you only need visibility, NOT atomicity

public class VolatileExample {
    // Without volatile: Thread 2 might NEVER see the change
    // With volatile: Thread 2 sees it IMMEDIATELY
    private volatile boolean running = true;
    
    // Thread 1:
    public void stop() {
        running = false;  // volatile write → flushed to RAM immediately
    }
    
    // Thread 2:
    public void execute() {
        while (running) {  // volatile read → reads from RAM, NOT cache
            // This will stop as soon as Thread 1 sets running = false
        }
        System.out.println("Stopped!");
    }
}
```

**When to use volatile vs synchronized:**

```java
// ─── VOLATILE: Use when ───
// 1. Only ONE thread writes the variable
// 2. Other threads only READ it
// 3. The operation is a SINGLE read or write (not read-modify-write)

private volatile boolean flag = true;  // ✅ Good: one writer, many readers
// flag = false;   ATOMIC (single write)
// if (flag) { }   ATOMIC (single read)

// ─── VOLATILE: DO NOT use when ───
private volatile int count = 0;
// count++;  NOT ATOMIC! (read + increment + write = 3 operations)
// Multiple threads calling count++ → race condition!

// Use AtomicInteger instead:
private AtomicInteger count = new AtomicInteger(0);
// count.incrementAndGet();  ← ATOMIC (single CPU instruction)
```

### Rule 4: Thread Start Rule

```java
// ─── EVERYTHING BEFORE start() IS VISIBLE TO THE NEW THREAD ───

public class ThreadStartExample {
    private int data = 0;
    private String message = null;
    
    public void startNewThread() {
        // Set up data BEFORE starting the thread
        data = 42;                           // Write 1
        message = "Hello from main thread";  // Write 2
        
        // Start the thread
        Thread worker = new Thread(() -> {
            // JMM guarantees: Thread sees data=42 and message="Hello..."
            // Because thread.start() happens-before any action in the thread
            System.out.println(data);     // ✅ Guaranteed to print 42
            System.out.println(message);  // ✅ Guaranteed to print "Hello..."
        });
        
        worker.start();  // ← This creates the happens-before edge
    }
}
```

### Rule 5: Thread Join Rule

```java
// ─── EVERYTHING IN A THREAD IS VISIBLE AFTER join() ───

public class ThreadJoinExample {
    private int result = 0;
    
    public int computeInBackground() throws InterruptedException {
        Thread worker = new Thread(() -> {
            // Do some computation
            int sum = 0;
            for (int i = 0; i < 100; i++) {
                sum += i;
            }
            result = sum;  // ← This write...
        });
        
        worker.start();
        
        // Do other work while thread runs...
        
        worker.join();  // ← ...happens-before this line returns
        
        // JMM guarantees: we see result = 4950
        // Because all actions in the thread happen-before join() returns
        return result;  // ✅ Guaranteed to be 4950
    }
}
```

### Rule 6: Transitivity

```java
// ─── IF A → B AND B → C, THEN A → C ───

public class TransitivityExample {
    private int a = 0;
    private volatile int flag = 0;  // volatile for visibility
    
    // Thread 1:
    public void write() {
        a = 1;           // Action A: normal write
        flag = 1;         // Action B: volatile write
        // volatile write → everything before it is visible
    }
    
    // Thread 2:
    public void readThenWrite() {
        if (flag == 1) {  // Action C: volatile read
            // Because volatile read sees volatile write (B → C)
            // And A happens-before B (program order)
            // Transitivity: A → B → C, therefore A → C
            int b = a;     // ✅ Guaranteed b = 1!
            
            flag = 2;      // Action D: another volatile write
        }
    }
    
    // Thread 3:
    public void read() {
        if (flag == 2) {  // Action E: volatile read
            // From Thread 2's perspective:
            // a = 1 (from Thread 1, via transitivity through flag = 1)
            // flag was already 2
            // We see everything!
            System.out.println(a);  // ✅ Guaranteed to print 1
        }
    }
}
```

---

## Chapter 5: The "Infinite Loop" Bug (Most Common JMM Problem)

### The Problem

```java
// ─── CAN YOU SPOT THE BUG? ───

public class InfiniteLoopExample {
    // No volatile! No synchronized!
    private boolean running = true;
    
    public void stop() {
        running = false;  // Thread 1 writes this
        System.out.println("Stop requested");
    }
    
    public void run() {
        System.out.println("Starting...");
        int count = 0;
        while (running) {  // Thread 2 reads this
            count++;
        }
        System.out.println("Stopped after " + count + " iterations");
    }
    
    public static void main(String[] args) throws InterruptedException {
        InfiniteLoopExample example = new InfiniteLoopExample();
        
        // Thread 2: runs the loop
        Thread worker = new Thread(() -> example.run());
        worker.start();
        
        Thread.sleep(1000);  // Let it run for 1 second
        
        // Thread 1: tries to stop
        example.stop();  // ← This should stop the loop, right?
        
        worker.join(5000);  // Wait 5 seconds
        
        // Is the worker thread still alive?
        System.out.println("Worker alive: " + worker.isAlive());
        // Output: "Worker alive: true" ← STUCK IN INFINITE LOOP!
    }
}
```

**What happens:**
```
Thread 1 sets running = false.
But Thread 2 NEVER SEES IT!
Thread 2's CPU cache still has running = true.
Thread 2 loops forever. 
```

### The Explanation (Step by Step)

```
Memory Layout:
  ┌─────────────────────────────────────┐
  │         MAIN MEMORY (RAM)            │
  │  running = true (initial value)      │
  └─────────────────────────────────────┘
                     ▲
                     │
        ┌────────────┴────────────┐
        │                         │
  ┌─────▼─────┐           ┌──────▼─────┐
  │ CPU CACHE  │           │  CPU CACHE  │
  │ Thread 1   │           │  Thread 2   │
  │ running=T  │           │  running=T  │
  └────────────┘           └─────────────┘

Thread 1 writes running = false:
  ┌─────────────────────────────────────┐
  │         MAIN MEMORY (RAM)            │
  │  running = false (updated!)          │
  └─────────────────────────────────────┘
                     ▲
                     │
        ┌────────────┴────────────┐
        │                         │
  ┌─────▼─────┐           ┌──────▼─────┐
  │ CPU CACHE  │           │  CPU CACHE  │
  │ Thread 1   │           │  Thread 2   │
  │ running=F  │           │  running=T  │← STILL OLD VALUE!
  └────────────┘           └─────────────┘
                           Thread 2 never re-reads from RAM!
                           Loops forever!
```

### The Fix

```java
// ─── FIX 1: Add volatile ───
// The SIMPLEST fix. volatile forces Thread 2 to read from RAM every time.

public class FixedExample {
    private volatile boolean running = true;
    //       ^^^^^^^^
    //  This tiny keyword fixes everything!
    //  Thread 2 will ALWAYS read running from main memory.
    //  Thread 1's write is IMMEDIATELY visible.
    
    public void stop() {
        running = false;
    }
    
    public void run() {
        while (running) {  // Now reads from RAM every iteration
            // Will stop when Thread 1 sets running = false
        }
    }
}
```

```java
// ─── FIX 2: Use synchronized ───
// Heavier solution, but also works.

public class FixedExample2 {
    private boolean running = true;
    
    public synchronized void stop() {
        // ↑ Acquire lock → read from RAM
        running = false;
        // ↓ Release lock → flush write to RAM
    }
    
    public synchronized boolean isRunning() {
        // ↑ Acquire lock → read from RAM (sees Thread 1's write!)
        return running;
        // ↓ Release lock
    }
    
    public void run() {
        while (isRunning()) {  // Uses synchronized getter
            // Will see Thread 1's change
        }
    }
}
```

---

## Chapter 6: Real-World Production Scenarios

### Scenario 1: Lazy Initialization (Double-Checked Locking)

```java
// ─── THE PROBLEM ───
// You want to create an expensive object ONCE, lazily.

public class LazyInitProblem {
    private static ExpensiveObject instance = null;
    
    public static ExpensiveObject getInstance() {
        if (instance == null) {           // First check (no lock)
            synchronized (LazyInitProblem.class) {
                if (instance == null) {   // Second check (with lock)
                    instance = new ExpensiveObject();  // ← BUG HERE
                }
            }
        }
        return instance;
    }
}

// WHAT CAN GO WRONG:
// `new ExpensiveObject()` is NOT a single operation!
// It's actually:
//   1. Allocate memory
//   2. Call constructor (initialize fields)
//   3. Assign `instance` to the memory address
//
// JVM can REORDER steps 2 and 3!
//   1. Allocate memory
//   3. Assign instance (object exists but NOT initialized!)
//   2. Call constructor (TOO LATE!)
//
// Thread A: executes steps 1, then 3 (instance is not null)
// Thread B: checks if (instance == null) → false!
//           Returns instance → but object's fields are DEFAULT VALUES!
//           BUG! BUG! BUG!
```

```java
// ─── THE FIX: volatile ───
// volatile prevents reordering!

public class LazyInitFix {
    // volatile ensures:
    // 1. Constructor runs BEFORE instance is assigned
    // 2. All threads see the fully constructed object
    private static volatile ExpensiveObject instance = null;
    //               ^^^^^^^^
    
    public static ExpensiveObject getInstance() {
        if (instance == null) {
            synchronized (LazyInitFix.class) {
                if (instance == null) {
                    instance = new ExpensiveObject();
                    // With volatile: constructor MUST finish before assignment
                }
            }
        }
        return instance;
    }
}
```

### Scenario 2: State Machine (When State Changes Signal Other Changes)

```java
// ─── REAL-WORLD: Order Processing System ───
// Order goes through states: NEW → PAID → SHIPPED → DELIVERED
// Each state transition has associated data.

public class OrderProcessor {
    private OrderState state = OrderState.NEW;
    private String trackingNumber = null;
    private long shippedAt = 0;
    
    // volatile guarantees visibility of state changes
    // AND transitively, the data changes that came before
    private volatile OrderState visibleState = OrderState.NEW;
    
    // Called when order is shipped
    public void shipOrder(String trackingNum) {
        // These are regular writes
        trackingNumber = trackingNum;  // Action A
        shippedAt = System.currentTimeMillis();  // Action B
        
        // This volatile write makes A and B visible to all threads
        visibleState = OrderState.SHIPPED;  // Action C: volatile write
        // Happens-before: A → B → C (program order)
        // The volatile write (C) acts as a MEMORY BARRIER
        // Everything before C is flushed to RAM
    }
    
    // Called by customer service to check status
    public String getShippingInfo() {
        if (visibleState == OrderState.SHIPPED) {  // volatile read
            // JMM guarantees: we see A and B too!
            // Because C (volatile write) happens-before this volatile read
            return "Shipped at " + shippedAt + ", tracking: " + trackingNumber;
            // ✅ These are GUARANTEED to be visible!
        }
        return "Not yet shipped";
    }
}
```

### Scenario 3: Configuration Refresh

```java
// ─── REAL-WORLD: Dynamic Config Reload ───
// Application config that can be updated at runtime without restart.

public class ConfigService {
    // volatile: all threads see updated config immediately
    private volatile AppConfig config = loadDefaultConfig();
    //                    ^^^^^^^^
    // Without volatile: threads might use stale config for SECONDS or MINUTES
    
    // Called by admin API to update config
    public void updateConfig(AppConfig newConfig) {
        // Validate before applying
        if (newConfig.isValid()) {
            this.config = newConfig;  // volatile write
            // All threads will see this immediately!
            System.out.println("Config updated: " + newConfig.getName());
        }
    }
    
    // Called by every request handler
    public AppConfig getConfig() {
        return config;  // volatile read → always latest
    }
    
    // Usage in a service:
    public void processRequest(Request req) {
        AppConfig cfg = getConfig();  // ← Always fresh!
        if (cfg.isFeatureEnabled("new-checkout")) {
            // Use new checkout flow
        } else {
            // Use old checkout flow
        }
    }
}
```

---

## Chapter 7: Memory Barriers — What Happens Under the Hood

### The CPU-Level Explanation

When you write to a volatile variable, the JVM inserts **memory barriers** — special CPU instructions.

```
┌────────────────────────────────────────────────────────┐
│                                                        │
│  Without volatile:                                      │
│                                                        │
│  Thread writes value → CPU cache → ??? → RAM (maybe)   │
│                                       ↑ Sometimes never!│
│                                                        │
│  With volatile:                                         │
│                                                        │
│  Thread writes value → CPU cache → BARRIER → RAM (NOW!)│
│                                       ↑ Force flush now!│
│                                                        │
└────────────────────────────────────────────────────────┘
```

### What the JVM Actually Inserts

```java
// ─── VOLATILE WRITE ───
public void setRunning(boolean value) {
    // JVM inserts: StoreStore barrier (prevents reordering of previous writes)
    // JVM inserts: StoreLoad barrier (flushes cache to main memory)
    this.running = value;  // The volatile write
}
```

```
Memory Barriers inserted by JVM:

Volatile Write:
  [StoreStore] ← All previous writes must complete before this
  [StoreLoad]  ← Flush cache to main memory NOW
  running = false;  ← The actual write

Volatile Read:
  running == true?  ← The actual read
  [LoadLoad]  ← All subsequent reads must wait for this
  [LoadStore] ← All subsequent writes must wait for this
```

### x86 vs ARM (Why JMM Matters More on Mobile)

```
x86 (Intel/AMD — your laptop):
  └─ Has a STRONG memory model
  └─ Most reorderings don't happen naturally
  └─ volatile writes ≈ normal writes with cache flush
  └─ Performance cost: LOW (~10% overhead)

ARM (Android phones, Apple M-series):
  └─ Has a WEAK memory model
  └─ CPU freely reorders instructions
  └─ volatile writes require FULL memory barrier
  └─ Performance cost: HIGH (~100% overhead)

This is why JMM bugs show up on Android but not on your dev machine!
Your code works on your laptop, crashes on production servers.
```

---

## Chapter 8: The final Keyword and JMM

### How final Helps

```java
// ─── final FIELDS HAVE SPECIAL JMM GUARANTEES ───
// When a constructor finishes:
//   - All final fields are FULLY initialized
//   - Any thread that sees the object sees the correct final field values
// This happens WITHOUT any synchronization!

public class SafePublication {
    private final int id;      // final → safe without volatile
    private final String name; // final → safe without volatile
    private int nonFinalValue; // NOT final → might be seen as 0!
    
    public SafePublication(int id, String name) {
        this.id = id;          // final: guaranteed visible
        this.name = name;      // final: guaranteed visible
        this.nonFinalValue = 999;  // NOT final: might not be visible!
    }
    
    // ─── THE BUG WITHOUT final ───
    // Thread 1: instance = new SafePublication(1, "Alice");
    // Thread 2: instance.nonFinalValue → might be 0 (not 999)!
    // But: instance.id → 1 (guaranteed!)
    //      instance.name → "Alice" (guaranteed!)
}
```

### The Immutable Object Pattern

```java
// ─── IMMUTABLE OBJECT = THREAD-SAFE WITHOUT SYNC ───
// All fields are final.
// Object cannot be modified after construction.
// Safe to share between threads WITHOUT any synchronization!

public final class ImmutableUser {
    private final long id;
    private final String username;
    private final List<String> roles;  // Defensive copy needed for collections
    
    public ImmutableUser(long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        // Defensive copy: original list can be changed by caller
        this.roles = Collections.unmodifiableList(new ArrayList<>(roles));
    }
    
    public long getId() { return id; }           // Safe: final field
    public String getUsername() { return username; }  // Safe: final field
    public List<String> getRoles() { return roles; }  // Safe: unmodifiable
    
    // No setters! Object cannot be modified.
}

// Usage: safe to share across threads!
public class UserCache {
    private volatile ImmutableUser currentUser;  // volatile for visibility
    
    public void updateUser(ImmutableUser user) {
        this.currentUser = user;  // Atomic reference swap
    }
    
    public ImmutableUser getUser() {
        return currentUser;  // Object inside is IMMUTABLE = thread-safe
    }
}
```

---

## Chapter 9: Common JMM Interview Questions

### Beginner

**Q1: What is the Java Memory Model?**
```
A: The JMM defines the rules for how threads interact through memory.
   It answers: "When does Thread A's write become visible to Thread B?"
   
   Key concept: Happens-Before relationship.
   If A happens-before B, then A's effects are visible to B.
```

**Q2: What is a race condition?**
```
A: When two threads access shared data simultaneously
   and at least one is writing, the result depends on timing.
   
   Example:
   count++ is 3 operations: read, add, write.
   Two threads doing count++ at the same time:
   Thread 1: read 5 → add 1 → write 6
   Thread 2:                     read 5 → add 1 → write 6
   Result: 6 (should be 7!)
```

**Q3: What is the difference between volatile and synchronized?**
```
volatile:
  ✓ Visibility only (one thread's writes seen by others)
  ✓ NO atomicity for compound operations
  ✓ Better performance (no locking)
  ✓ Use for: flags, status indicators

synchronized:
  ✓ Visibility (happens-before on lock/unlock)
  ✓ Atomicity (only one thread in the block at a time)
  ✓ Heavier performance (lock acquisition/release)
  ✓ Use for: critical sections, compound operations
```

### Intermediate

**Q4: Explain the happens-before relationship.**
```
A: If action A happens-before action B, then A's results are visible to B.
   
   6 rules:
   1. Program order: within a thread, sequence is preserved
   2. Monitor lock: unlock happens-before subsequent lock on same monitor
   3. Volatile: volatile write happens-before subsequent volatile read
   4. Thread start: start() happens-before any action in the thread
   5. Thread join: thread's actions happen-before join() returns
   6. Transitivity: A → B and B → C, then A → C
```

**Q5: What is the double-checked locking pattern and why does it need volatile?**
```
A: Lazy initialization pattern that minimizes synchronization.
   
   Without volatile, JVM can reorder:
     memory = allocate();    // Step 1
     instance = memory;      // Step 3 (REORDERED before constructor!)
     constructor(memory);    // Step 2
   
   Thread B sees instance != null, returns it, but constructor hasn't run!
   Fields have default values (0, null) → BUG!
   
   volatile prevents this reordering. Constructor ALWAYS finishes
   before the reference is assigned.
```

**Q6: When does the "infinite loop" bug happen?**
```
A: When one thread writes a boolean and another checks it in a loop:
   
   Thread 1: running = false;
   Thread 2: while (running) { }
   
   Without volatile/synchronized:
   - Thread 2 may cache 'running' in CPU register
   - Never re-reads from main memory
   - Loops forever even though Thread 1 set it to false
   
   Fix: Make 'running' volatile.
```

### Senior

**Q7: You have a performance-critical system. Thread A writes configuration. Thread B reads it. How do you ensure visibility with minimum overhead?**
```
A: Use volatile for the configuration reference.
   
   class ConfigService {
       private volatile AppConfig config;
       
       public void updateConfig(AppConfig newConfig) {
           this.config = newConfig;  // volatile write
       }
       
       public AppConfig getConfig() {
           return config;  // volatile read
       }
   }
   
   Cost: ~10-100ns per volatile read/write
   vs synchronized: ~1μs (10-100x more expensive)
   vs Lock: ~0.5μs (5-50x more expensive)
```

**Q8: Your application works on your laptop but has intermittent bugs on production servers. What could be the cause?**
```
A: Possible JMM-related causes:
   
   1. Different CPU architectures:
      - Dev laptop: x86 (strong memory model)
      - Production: ARM servers (weak memory model)
      - Bugs visible on ARM but not x86
   
   2. Different JVM configurations:
      - Dev: Client JIT (less aggressive optimization)
      - Production: Server JIT (more aggressive reordering)
   
   3. Load differences:
      - Dev: low load, thread scheduling is predictable
      - Production: high load, threads interleave unpredictably
   
   Debugging: Add -XX:+PrintCompilation to see JIT behavior.
   Fix: Use proper synchronization (volatile/synchronized/atomic classes).
```

**Q9: Why does the following code print "null" sometimes even though it looks correct?**
```java
public class Publisher {
    private static Map<String, String> config;
    private static boolean initialized = false;
    
    public static void init() {
        config = new HashMap<>();
        config.put("key", "value");
        initialized = true;  // ← Can be reordered!
    }
    
    public static String get(String key) {
        if (initialized) {
            return config.get(key);  // ← Might return null!
        }
        return null;
    }
}
```
```
A: JVM can reorder:
   
   Actual execution (reordered):
   initialized = true;    // Executed FIRST!
   config = new HashMap<>();  // Executed SECOND!
   
   Thread B sees initialized=true, calls config.get("key")
   But config is still null → NullPointerException!
   
   Fix: Make both volatile, or use synchronized.
```

**Q10: How do you design a lock-free, thread-safe counter?**
```
A: Use AtomicInteger (or AtomicLong):
   
   private AtomicInteger counter = new AtomicInteger(0);
   
   // Thread-safe increment
   counter.incrementAndGet();  // Equivalent to count++
   
   How it works:
   - Uses CAS (Compare-And-Swap) CPU instruction
   - Atomically: read, compare, write
   - If another thread changed the value in between → RETRY
   - No locks, no blocking, no context switching
   - ~3x faster than synchronized for high contention
```

### Tricky

**Q11: I have a volatile int counter. Can I use counter++ safely from multiple threads?**
```
A: NO! counter++ is NOT atomic even with volatile.
   
   counter++ is actually 3 operations:
   1. Read counter from memory
   2. Add 1 to the value
   3. Write counter back to memory
   
   Volatile ensures each individual operation is visible,
   but the three operations together are NOT atomic.
   
   Thread A: read 5 → add 1 → write 6 (step 3 not yet flushed)
   Thread B: read 5 (STALE! Thread A's write not visible yet!)
   
   Use AtomicInteger.incrementAndGet() instead.
```

**Q12: Can you have a race condition without shared mutable state?**
```
A: YES! Example: String immutability doesn't prevent publication races.
   
   public class Holder {
       private int value;
       
       public Holder(int value) { this.value = value; }
       
       public int getValue() { return value; }
   }
   
   // Thread 1:
   Holder holder = new Holder(42);  // Published
   
   // Thread 2:
   holder.getValue();  // Might see 0, not 42!
   
   Even though Holder's fields are set in constructor,
   without happens-before, another thread might see default values.
   
   Fix: Make value final, or use volatile reference.
```

**Q13: What happens if you read a 64-bit value (long/double) without volatile on a 32-bit JVM?**
```
A: On a 32-bit JVM, reading a 64-bit long/double is TWO operations:
   1. Read high 32 bits
   2. Read low 32 bits
   
   Without volatile, these can be INTERLEAVED with a write:
   
   Initial value: 0x00000000_00000000 (0)
   Thread A writes: 0x00000001_00000000 (4294967296)
   
   Thread B reads:
   Step 1: Read high 32 bits → 0x00000001
   Step 2: Thread A writes low 32 bits → 0x00000000
   Step 3: Read low 32 bits → 0x00000000
   Result: 0x00000001_00000000 (correct!) ← Sometimes works
   
   But could be:
   Step 1: Read high 32 bits → 0x00000000 (before write)
   Step 2: Thread A writes all 64 bits → 0x00000001_00000000
   Step 3: Read low 32 bits → 0x00000000 (after write)
   Result: 0x00000000_00000000 → 0 ← WRONG! Saw half old, half new!
   
   Fix: Use volatile (guarantees atomic 64-bit read/write).
   Or use AtomicLong.
```

**Q14: How does the JMM handle String immutability? Does it provide thread safety?**
```
A: String immutability + final fields = THREAD SAFE without synchronization.
   
   String's value field is final:
   private final char[] value;
   
   JMM guarantees: when constructor finishes, all final fields
   are properly initialized and visible to all threads.
   
   So:
   String s = "hello";  // Thread 1 creates it
   // Thread 2 reads s → sees "hello" correctly
   // No synchronization needed!
   
   This is why String is safe to use as HashMap keys
   across threads without external synchronization.
```

---

## Chapter 10: Quick Reference — JMM Cheat Sheet

### When to Use What

```
SITUATION                              SOLUTION
────────────────────────────────────  ────────────────────
Single write, many reads (flag)        volatile
Multiple threads updating a counter    AtomicInteger (CAS)
Complex critical section               synchronized or Lock
Lazy initialization                    volatile + double-checked locking
Immutable data shared across threads   final fields
Thread coordination (wait for thread)  join() (happens-before provided)
Producer-consumer                      BlockingQueue
No shared mutable state               No synchronization needed 🎉
```

### Common JMM Mistakes

```
MISTAKE                            WHY IT'S WRONG              FIX
─────────────────────────────────  ─────────────────────────  ────────────────
volatile on counter++              Not atomic                  AtomicInteger
Forgetting volatile on flags       Infinite loop               Add volatile
Double-checked locking without vol Reordered initialization    Add volatile
Reading 64-bit primitives on 32bit Torn read (half old/half   Mark volatile
Sharing non-final object without   Fields might show defaults  Make fields final
  synchronization                                              
Relying on x86 behavior            Breaks on ARM               Proper sync
```

### 30-Second Summary

```
JMM = Rules for thread visibility and reordering.

HAPPENS-BEFORE (6 rules):
  Program order → within a thread
  Monitor lock → unlock → lock on same monitor  
  Volatile → write → read same field
  Thread start → before start is visible to thread
  Thread join → thread's work visible after join
  Transitivity → chain of happens-before

KEY INSIGHTS:
  - Without sync, Thread 2 may NEVER see Thread 1's write
  - volatile = visibility only, NOT atomicity
  - synchronized = visibility + atomicity
  - final fields are safe without synchronization
  - counter++ is 3 operations, NOT atomic

COMMON BUGS:
  - Infinite loop with boolean flag (fix: volatile)
  - Double-checked locking without volatile (fix: volatile)
  - Race condition on count++ (fix: AtomicInteger)
  - Torn 64-bit read on 32-bit JVM (fix: volatile)