# GC Analysis and Performance Tuning — Complete Deep Dive

## 1. Why This Concept Matters

Garbage Collection is the most misunderstood performance topic in Java. GC pauses can cause latency spikes, timeouts, and cascading failures in production systems. Understanding GC algorithms (Serial, Parallel, CMS, G1, ZGC, Shenandoah), generational heap layout, and how to analyze GC logs is essential for tuning application performance. Interviewers ask about GC to separate engineers who just "set -Xmx" from those who understand what's actually happening in memory.

## 2. Basic Meaning

GC automatically reclaims memory from objects that are no longer reachable (not referenced by any live thread).

**Heap Structure (Generational):**
- **Young Generation**: where new objects are allocated. Further divided into **Eden** (most objects created here) and **Survivor spaces (S0, S1)** (objects that survived a minor GC move here).
- **Old Generation (Tenured)**: objects that survived multiple GC cycles (promoted from survivor spaces).
- **Metaspace**: class metadata (not part of heap).

**GC Algorithms:**

| GC | Java Version | Description | Pause Time |
|----|-------------|-------------|-----------|
| Serial | All (single-threaded) | Single-threaded, freezes all app threads | Long (seconds) |
| Parallel | Default Java 8 | Multi-threaded young GC, single-threaded old GC | Medium |
| CMS | Deprecated Java 14 | Low-pause concurrent old GC | Short |
| G1 | Default Java 9+ | Region-based, predictable pause targets | Configurable (ms) |
| ZGC | Java 15+ (experimental) | Ultra-low-pause, handles TB heaps | <10ms |
| Shenandoah | Java 12+ | Ultra-low-pause concurrent compaction | <10ms |

## 3. GC Types & Lifecycle

```mermaid
graph TD
    subgraph "Object Allocation"
        N[new Object()]
    end
    
    subgraph "Young Generation"
        E[Eden]
        S0[Survivor 0]
        S1[Survivor 1]
    end
    
    subgraph "Old Generation"
        O[Old/Tenured]
    end
    
    N -->|Allocated| E
    E -->|Minor GC: surviving objects| S0
    S0 -->|Minor GC: promote| S1
    S1 -->|After multiple cycles| O
    O -->|Major GC: live objects stay| O
    O -->|Full GC: collect unreachable| O
```

**GC Types:**
- **Minor GC (Young GC)**: collects Eden + Survivor. Stop-the-world (STW) but fast (ms). Promotes surviving objects to other survivor or old gen.
- **Major GC (Old GC)**: collects Old Generation. Can be STW (Parallel) or concurrent (G1, ZGC). Slower (100ms-sec).
- **Full GC**: collects ALL regions (young + old + metaspace). LONG pause (seconds). Indicates a problem.
- **System.gc()**: SUGGESTS a Full GC (not guaranteed). `-XX:+DisableExplicitGC` disables it.

## 4. Real Code / How to Analyze GC

```bash
# Enable GC logging (Java 8)
-XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -Xloggc:gc.log

# Enable GC logging (Java 11+ unified logging)
-Xlog:gc*:file=gc.log:time,uptime:filecount=10,filesize=10M

# Key JVM flags for GC analysis
-XX:+PrintHeapAtGC              # Show heap before/after GC
-XX:+PrintTenuringDistribution  # Show age distribution
-XX:+PrintGCApplicationStoppedTime # Show actual pause time
```

**Sample GC log analysis:**
```
2026-06-23T10:00:00.123+0000: 1.234: [GC (Allocation Failure) 
  [PSYoungGen: 1024K->512K(2048K)] 2048K->1024K(4096K), 0.0054321 secs] 
  [Times: user=0.02 sys=0.01, real=0.01 secs]
```

**What this tells you:**
- `1024K->512K(2048K)`: Young gen went from 1024KB used → 512KB used, capacity 2048KB
- `2048K->1024K(4096K)`: Total heap from 2048KB → 1024KB used, total 4096KB
- `0.0054321 secs`: GC took 5ms
- `Allocation Failure`: GC triggered because Eden was full

**Red flags:**
- Full GC in production → indicates heap is too small or memory leak
- GC taking > 10% of total CPU → GC tuning needed
- Increasing old gen after GC → objects not being collected
- Metaspace growing unbounded → classloader leak

## 5. G1 GC Tuning

```bash
# G1 is default in Java 9+. Production recommended flags:
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100        # Target max pause 100ms
-XX:G1HeapRegionSize=4m          # Region size (1-32MB)
-XX:G1NewSizePercent=5           # Initial young gen size
-XX:G1MaxNewSizePercent=60       # Max young gen size
-XX:G1HeapWastePercent=5         # Heap waste allowed
-XX:InitiatingHeapOccupancyPercent=45 # Start concurrent cycle at 45% heap
```

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Not setting Xms = Xmx | JVM wastes time resizing heap | Set equal to avoid resize overhead |
| Heap too large for default GC | Long GC pauses (minutes) | Use G1 or ZGC for > 4GB heaps |
| No GC logging in production | Can't diagnose GC issues | Always enable in production |
| Ignoring Full GC | Memory leak or insufficient heap | Analyze heap dump |
| System.gc() calls | Unnecessary full GC pauses | `-XX:+DisableExplicitGC` |
| Running out of Metaspace | Classloader leak | Set `-XX:MaxMetaspaceSize` |

## 7. Production Debugging Steps

```bash
# 1. Check GC stats
jstat -gcutil <pid> 1s 10
  S0    S1    E    O    M    YGC   YGCT   FGC   FGCT
  0.00  0.00  50   75   85   1200  12.34  5     30.22
  
# 2. Heap dump on OOM
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/path/heap.hprof

# 3. Analyze with jvisualvm or Eclipse MAT

# 4. Monitor with Prometheus + Grafana (G1GC metrics)
jvm_gc_pause_seconds
jvm_memory_used_bytes{area="heap"}
jvm_gc_memory_promoted_bytes_total
```

## 8. Final 30-Second Answer

GC reclaims unreachable objects. **Generations**: Young (Eden + Survivor) for new objects, Old for long-lived. **GC types**: Minor (young, fast, STW), Major (old, concurrent in G1/ZGC), Full (all, slow, indicates problem). **G1** is default (Java 9+), targets < 100ms pauses. **Monitor**: GC logs (`-Xlog:gc*`), `jstat`, heap dumps. **Tune**: set Xms=Xmx, choose GC for heap size (< 4GB: Parallel, 4-32GB: G1, >32GB: ZGC). Always enable GC logging in production.