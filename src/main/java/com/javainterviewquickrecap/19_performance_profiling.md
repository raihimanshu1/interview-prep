# Module — Performance Profiling & Tuning — JFR, Flame Graphs, async-profiler

> **Skill**: 7+ years — covers JFR, async-profiler, GC tuning, CPU/memory profiling.

---

## Q1. Profiling in Production — JFR, async-profiler, and Flame Graphs

### 1. Why This Matters at Senior Level
At 7+ years, you're expected to diagnose production performance issues without adding overhead. JFR is built-in and low overhead (~1%). async-profiler shows exactly what the CPU is doing. Interviewers ask this to test your **systematic debugging approach**.

### 2. Tool Comparison

| Tool | Overhead | What It Shows | Best For |
|------|----------|--------------|----------|
| **JFR** (Flight Recorder) | ~1% | Hot methods, GC, locks, I/O, allocations | Production monitoring |
| **async-profiler** | ~1-2% | CPU flame graphs, allocation, locks | Deep dive into CPU hotspots |
| **jstack/jcmd** | ~0% | Thread dumps (point-in-time) | Deadlocks, blocked threads |
| **jmap** | Full GC pause | Heap dump (all objects) | Memory leak analysis |

### 3. JFR Event Types

```java
// =====================================================
// JFR — Java Flight Recorder Built-in Events
// =====================================================

// CPU:
// - jdk.ExecutionSample     (CPU samples every 20ms)
// - jdk.ThreadPark          (threads waiting on park)
// - jdk.CPULoad             (CPU utilization)

// GC:
// - jdk.G1GarbageCollection (GC pause duration)
// - jdk.AllocationRequiringGC (allocation that triggered GC)
// - jdk.ObjectAllocationInNewTLAB (TLAB allocations)

// Locks:
// - jdk.JavaMonitorEnter   (synchronized block enters)
// - jdk.JavaMonitorWait    (wait() calls)
// - jdk.ThreadPark         (park calls)

// I/O:
// - jdk.FileWrite          (file writes)
// - jdk.FileRead           (file reads)
// - jdk.SocketRead         (socket reads)
// - jdk.SocketWrite        (socket writes)

// =====================================================
// STARTING JFR IN PRODUCTION
// =====================================================

// Start recording on running JVM: (zero startup cost!)
jcmd <pid> JFR.start name=profile duration=60s filename=profile.jfr
jcmd <pid> JFR.dump name=profile filename=profile.jfr
jcmd <pid> JFR.stop name=profile

// Or via JVM arguments:
-XX:StartFlightRecording=duration=120s,filename=recording.jfr,settings=profile

// Settings profiles:
// "default": ~1% overhead, ~100 events
// "profile": ~2% overhead, more events (CPU sampling, allocations)
```

### 4. async-profiler Flame Graphs

```bash
# =====================================================
# ASYNC-PROFILER — CPU profiling
# =====================================================

# CPU profiling:
./profiler.sh -e cpu -d 30 -f profile.html <pid>
# Creates: profile.html — interactive flame graph!

# Allocation profiling:
./profiler.sh -e alloc -d 30 -f alloc.html <pid>
# Shows where MOST memory is allocated

# Lock profiling:
./profiler.sh -e lock -d 30 -f lock.html <pid>
# Shows lock contention hotspots

# Wall-clock profiling (includes I/O wait):
./profiler.sh -e wall -d 30 -f wall.html <pid>
# Shows where time is SPENT (not CPU) — I/O included

# =====================================================
// READING FLAME GRAPHS
// =====================================================

// Each box = a method
// Width = time spent (wider = more CPU)
// Stack = call chain (bottom = entry point, top = current method)
// Colors = random (distinguish call stacks)

// Example flame graph:
// ┌─────────────────────────────────────┐
// │  UserService.getUser()  (40% CPU!)  │ ← WIDEST = HOTTEST
// ├─────────────────────────────────────┤
// │  UserService.getUser()              │
// │  UserRepository.findById()          │
// ├─────────────────────────────────────┤
// │  UserService.getUser()              │
// │  UserRepository.findById()          │
// │  Hibernate Session.load()           │
// ├─────────────────────────────────────┤
// │  ...                                │
// │  ThreadPoolExecutor.runWorker()     │
// └─────────────────────────────────────┘

// Read: "getUser() is 40% of all CPU time"
// Reason: "findById() dominates gateUser's profile → DB query is hotspot"
```

### 5. Performance Anti-Patterns (with Profiling Evidence)

```java
// =====================================================
// ANTI-PATTERN 1: String.concat in loops
// =====================================================

// BAD: O(n²) allocation!
String result = "";
for (Order o : orders) {
    result += o.getId();  // Creates NEW StringBuilder each iteration!
    // Profiler shows: high allocation rate, frequent Young GC
}

// GOOD: O(n) allocation
StringBuilder sb = new StringBuilder(orders.size() * 20);
for (Order o : orders) {
    sb.append(o.getId());
}
String result = sb.toString();

// =====================================================
// ANTI-PATTERN 2: Boxing overhead
// =====================================================

// BAD: auto-boxing creates Integer objects
Integer sum = 0;
for (int i = 0; i < 1_000_000; i++) {
    sum += i;  // Integer.valueOf() on each iteration!
}
// Profiler shows: high allocation, GC under pressure

// GOOD: use primitive
int sum = 0;
for (int i = 0; i < 1_000_000; i++) {
    sum += i;
}

// =====================================================
// ANTI-PATTERN 3: Lock contention
// =====================================================

// BAD: synchronized class (contention)
public synchronized void process(Order order) { ... }
// Profiler shows: threads BLOCKED waiting for lock

// GOOD: use appropriate concurrency
public void process(Order order) {
    // Use ReentrantLock, StampedLock, or lock-free approach
}
```

### 6. Common Profiling Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Profiling on dev machine | Different CPU, memory, load | Always profile on PRODUCTION-like environment |
| Profiling for 10 seconds | May miss periodic events (GC, batch jobs) | Profile for 30-60 minutes in production |
| Using sampling profiler on I/O-bound | Only shows CPU time, not I/O wait | Use wall-clock profiling (async-profiler -e wall) |
| Misreading JFR allocation events | Confusing TLAB vs outside-TLAB | TLAB = small object, outside-TLAB = large object |
| Fixing what profiler shows without understanding context | "The profiler shows method X is slow — let's optimize it!" | First understand WHY — maybe it's just doing legitimate work |

### 7. Senior Q&A

**Q**: Application is running hot (80% CPU) but GC is healthy and throughput is fine. How do you diagnose?

**A**: (1) Take a **thread dump** — any threads spinning infinite loops? (2) Run **async-profiler -e cpu** for 30 seconds — flame graph shows which methods consume CPU. (3) Check **JFR** events for high allocation rates. (4) If CPU is in application code (not system/idle), use the flame graph to identify the hot methods. (5) If CPU is in GC threads (even with good throughput), allocation rate is too high — reduce temporary objects. (6) Common causes: regex in hot path, String concat in loop, logging at DEBUG level, deserialization in request path. Fix the hottest 3 frames, not everything.

**Final 30-Second**: JFR for production monitoring (1% overhead). async-profiler for flame graphs (CPU, allocation, lock, wall-clock). Focus on hottest methods first. Don't profile dev machines. Wall-clock profiling for I/O-bound. Flame graph width = CPU time proportion.