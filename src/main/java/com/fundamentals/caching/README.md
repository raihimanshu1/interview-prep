# Caching — Explained Simply

## Chapter 1: Why Do We Even Need Cache?

### The "Sticky Note on the Fridge" Analogy

Imagine you're cooking dinner. You keep checking the recipe on your phone.

```
Without cache:
  Open phone → Unlock → Open browser → Type URL → Wait for page → Read
  (Takes 30 seconds each time)

With cache:
  Look at sticky note on the fridge → Done!
  (Takes 2 seconds)
```

The sticky note is your **cache**. The recipe website is your **database**.

**Cache = a copy of slow-to-get data, placed somewhere fast.**

### What Happens WITHOUT Cache?

```java
// This is the problem:
// Every single user triggers a database query
//
// 1 user  → 1 query  → fine
// 100 users → 100 queries → fine
// 1,000,000 users → 1,000,000 queries → DATABASE CRASHES!

public Product getProduct(Long id) {
    // ❌ Database query for EVERY request
    return database.query("SELECT * FROM products WHERE id = ?", id);
    // every single time a user visits this page → database query
    // slow, expensive, and will crash under load
}
```

### What Changes WITH Cache?

```java
public Product getProduct(Long id) {
    // ─── STEP 1: Check if we have a copy nearby ───
    // The "sticky note" check
    Product cached = cache.get("product:" + id);
    
    // ─── STEP 2: If found, return instantly ───
    if (cached != null) {
        return cached;  // 🚀 5 milliseconds — super fast!
    }
    
    // ─── STEP 3: Not in cache? Go to database (slow) ───
    Product product = database.query("SELECT * FROM products WHERE id = ?", id);
    // 🐢 100 milliseconds — slow, expensive
    
    // ─── STEP 4: Save a copy for next time ───
    cache.put("product:" + id, product);
    // now the next person who asks gets it instantly
    
    return product;
}
```

**What changed?**
```
Before cache:
  Request 1 → DB (100ms)
  Request 2 → DB (100ms)
  Request 3 → DB (100ms)
  ...every request hits the database

After cache:
  Request 1  → DB (100ms) → saved to cache
  Request 2  → Cache (5ms) ← instantly!
  Request 3  → Cache (5ms) ← instantly!
  ...1000 requests: only 1 hits DB, 999 hit cache
```

---

## Chapter 2: Cache HIT vs Cache MISS

### The "Returning to Your Cafe" Analogy

You go to your favorite coffee shop. The barista knows you.

```
You: "The usual please!"
Barista: "Medium latte, oat milk, extra shot?"   ← CACHE HIT
You: "Yes!"
```

```
BUT if a new barista is working:
You: "The usual please!"
Barista: "Uh... what's your usual?"              ← CACHE MISS
You: "Medium latte, oat milk, extra shot"
Barista: "Got it!" → writes it down for next time  ← now cached
```

### Simple Code

```java
// ─── CACHE HIT ───
// When data IS found in cache
// Response is fast. No database call needed.

cache.get("user:123");
// Returns: {"name": "Alice", "email": "alice@example.com"}
// This is a HIT — data was there!
// Time taken: 2 milliseconds 🚀
```

```java
// ─── CACHE MISS ───
// When data is NOT found in cache
// Must go to database. Then store in cache for next time.

cache.get("product:456");
// Returns: null (nothing in cache)
// This is a MISS — data was NOT there!

// So we go to database:
Product p = database.query("SELECT * FROM products WHERE id = 456");
// 100 milliseconds later...

// And save in cache for next time:
cache.put("product:456", p);
// Next request → HIT!
```

### Visual Flow

```
                     ┌─────────────────┐
                     │   New Request    │
                     └────────┬────────┘
                              │
                              ▼
                     ┌─────────────────┐
                     │   Check Cache    │
                     └────────┬────────┘
                              │
                    ┌─────────┴──────────┐
                    ▼                    ▼
            ┌──────────────┐   ┌──────────────────┐
            │  CACHE HIT   │   │   CACHE MISS      │
            │              │   │                  │
            │ Return data  │   │ Query Database   │
            │ instantly!   │   │ (slow, 100ms)    │
            │   🚀 5ms     │   │                  │
            └──────────────┘   │ Store in cache   │
                               │ (for next time)  │
                               │                  │
                               │ Return data      │
                               └──────────────────┘

GOAL: Make 95% of requests go through "CACHE HIT"
      Only 5% should reach the "CACHE MISS" path
```

### Why This Matters

```
Real e-commerce site with 1 million users:

Without cache:
  1,000,000 requests → 1,000,000 database queries
  Database can handle ~10,000 queries per second
  → CRASHES in under 1 minute

With 95% cache hit rate:
  1,000,000 requests → 950,000 cache hits (fast!)
                     → 50,000 database queries only
  Database handles 50,000 just fine
  → Site stays up!
```

---

## Chapter 3: What is TTL? (Time To Live)

### The "Self-Destruct Timer" Analogy

TTL is like a timer on a text message that deletes itself.

```
You send a message that says: "Secret code: 1234"
Timer: 60 seconds

After 60 seconds: message is gone. POOF.
Anyone who looks for it after that → nothing there.
```

**TTL = Time To Live = A countdown timer attached to cached data.**

```java
// When you store data, you set a timer:
cache.setex("product:123", 3600, productData);
//       key          TTL   value
//                    ↑
//           "Keep for 3600 seconds (1 hour)"
//           After 1 hour, it disappears automatically
```

### TTL 20 Seconds — Step by Step

Let's say you set TTL = 20 seconds for a cricket match score.

```
Time 0s:     Score = 142/3 stored in cache with TTL=20s

Time 5s:     User A checks → Cache HIT → Score: 142/3
             (2 milliseconds — super fast!)

Time 10s:    User B checks → Cache HIT → Score: 142/3
             (2 milliseconds — still fast!)

Time 12s:    A SIX IS HIT! Score is now 148/3.
             Database updated. But cache still has 142/3.
             (TTL hasn't expired yet — 8 seconds left)

Time 15s:    User C checks → Cache HIT → Score: 142/3
             (This is STALE — 6 seconds late!
              But acceptable for a score app)

Time 20s:    ⏰ TTL EXPIRES! Cache deletes 142/3 automatically.

Time 21s:    User D checks → Cache MISS (nothing there!)
             → Goes to database → Gets 148/3 (fresh score!)
             → Stores 148/3 in cache with NEW TTL = 20s

Time 22s:    User E checks → Cache HIT → Score: 148/3 🚀
```

### Why TTL is Important

```
Without TTL:
  You put data in cache... and it stays there FOREVER.
  Memory fills up → server crashes.
  
  Like filling your fridge with leftovers and NEVER throwing anything away.
  Eventually, your fridge overflows with moldy food.

With TTL:
  Data cleans itself up after the timer runs out.
  Memory stays under control.
  Stale data gets replaced with fresh data.
  
  Like meal prep — eat within 3 days or throw away. Fresh food always.
```

### What TTL Should You Use?

```
Data Type        Recommended TTL    Reasoning
───────────────  ────────────────  ──────────────────────
Product catalog  1 hour            Changes rarely
Weather          30 minutes        Updates every 30 min
Cricket score    20 seconds        Changes fast, heavy traffic
User session     24 hours          Lasts all day
Shopping cart    7 days            User might return
Inventory        10-30 seconds     Must be somewhat accurate
Stock price      1-5 seconds       Changes every second
```

**Golden rule:** Set TTL as high as possible (more cache hits) but low enough that users don't see outdated data.

---

## Chapter 4: Cache Invalidation — The Hardest Problem

### The "Newspaper Delivery" Analogy

Imagine you get a physical newspaper delivered to your porch every morning.

```
Your porch = Cache
News company = Database

6:00 AM:  Newspaper arrives on your porch (fresh news!)
          You read it. Happy.

12:00 PM: BREAKING NEWS! Stock market crashes!
          But your newspaper still says "Market Stable"
          Your porch (cache) has STALE data — it's outdated

You need to: Throw away the old newspaper and get a fresh one.
             This is CACHE INVALIDATION.
```

**Cache Invalidation = The process of removing stale data from the cache so users see fresh data.**

### Why Is This SO Hard?

> *"There are only two hard things in Computer Science: cache invalidation and naming things."* — Phil Karlton

**You always have two bad choices:**

```
Choice 1: INVALIDATE TOO EARLY
  └─ Delete from cache the MOMENT data changes
  └─ Problem: Next 1000 reads hit database (slow!)
  └─ Even if nobody cared about that data

Choice 2: INVALIDATE TOO LATE
  └─ Let cache keep old data for a while
  └─ Problem: Users see outdated info
  └─ "The website says 'In Stock' but it's sold out!"
```

### Invalidation Method 1: TTL (Automatic Expiry)

You don't DO anything. The cache deletes itself when the timer runs out.

```java
// ─── TTL-BASED INVALIDATION ───
// The SIMPLEST approach. No extra code needed.

// When storing:
redis.setex("product:123", 3600, productData);
//             key        TTL    value
//             └─ 3600 seconds = 1 hour

// After 1 hour: data disappears automatically (TTL expired)
// Next request: cache miss → load from DB → fresh data
```

```
Pros:
  ✓ No extra code needed
  ✓ Automatic
  ✓ Handles crashes (data still expires)

Cons:
  ✗ Stale data until TTL expires
  ✗ You wait for expiry even if you know data changed
```

**Example — Cricket score app:**
```
TTL = 20 seconds.

Score changes at second 12.
Users see old score until second 20 (8 seconds of stale data).
Is this OK? YES — for a score app, 8 seconds is fine.
Better than crashing the database!
```

### Invalidation Method 2: Explicit (Force Delete)

When you KNOW data changed, you DELETE it from cache immediately.

```java
// ─── EXPLICIT INVALIDATION ───
// When data changes, YOU tell the cache to delete it.

public void updateProductPrice(Long productId, BigDecimal newPrice) {
    // Step 1: Update the database (source of truth)
    database.execute("UPDATE products SET price = ? WHERE id = ?", 
                     newPrice, productId);
    
    // Step 2: DELETE from cache (force next read to be fresh)
    redis.delete("product:" + productId);
    // cache no longer has the old price
    
    // Next user who reads this product:
    // Cache MISS → reads from DB → gets new price → stores in cache
    // User sees fresh data immediately!
}
```

```
Pros:
  ✓ Data is fresh immediately
  ✓ No waiting for TTL

Cons:
  ✗ Extra code needed (delete call on every update)
  ✗ Every update causes a cache miss for the next reader
```

### TTL vs Explicit — Which One?

```
USE TTL ALONE when:
  ✓ Stale data for a few seconds/minutes is OK
  ✓ You don't know WHEN data will change
  ✓ You want simplicity
  
  Examples:
  - Cricket scores (20 sec TTL)
  - Weather forecast (30 min TTL)
  - Trending products (1 hour TTL)
  - "You May Also Like" suggestions

USE EXPLICIT INVALIDATION when:
  ✓ Data MUST be fresh RIGHT NOW
  ✓ You KNOW exactly when data changes
  ✓ Accuracy is critical
  
  Examples:
  - User changes email/password (must update NOW)
  - Payment status (must be accurate)
  - Flash sale inventory (can't oversell)
  - Bank balance

BEST: Use BOTH!
  - Explicit invalidation for instant freshness
  - TTL as a fallback (in case invalidation fails)
  
  Like having a backup alarm clock — the explicit alarm wakes you,
  but if it fails, the backup TTL alarm still goes off.
```

---

## Chapter 5: Local Cache vs Distributed Cache

### Local Cache = Your Personal Notebook

```
You're studying for an exam.

You read a book (Database).
You write notes in your notebook (Local Cache).

The notebook is in YOUR pocket. Only YOU can read it.
  ✓ Super fast (no walking)
  ✗ YOUR copy only
  ✗ If you lose it, gone forever
```

```java
// ─── LOCAL CACHE ───
// Lives INSIDE your Java application's memory.
// Every server instance has its OWN copy.

// Using Caffeine (modern Java caching library):
Cache<String, Product> cache = Caffeine.newBuilder()
    .maximumSize(10_000)           // keep max 10,000 items
    .expireAfterWrite(10, MINUTES) // delete after 10 minutes
    .build();

// To use it:
Product p = cache.get("product:123", key -> {
    // This code runs ONLY on cache miss
    return database.query("SELECT * FROM products WHERE id = ?", key);
});
// First call → runs DB query (slow)
// Second call → returns from cache instantly (fast)!
```

**When to use Local Cache:**
- Data rarely changes (product catalog, settings)
- Each server can have its own copy (doesn't matter if they differ slightly)
- You need the absolute fastest speed (< 1 millisecond)

**When NOT to use Local Cache:**
- Data changes often (inventory count must be accurate)
- All servers MUST see exactly the same data
- Your server has limited memory

### Distributed Cache = Shared Library Bookshelf

```
You and your friends are studying together.

You ALL share ONE bookshelf in the library.
Anyone can put notes there. Anyone can read them.
  ✓ Everyone sees the SAME notes
  ✓ Notes survive even if you leave
  ✗ Slightly slower (you walk to the bookshelf)
```

```java
// ─── DISTRIBUTED CACHE (Redis) ───
// A SEPARATE server. All app instances connect to it.

// Step 1: Connect to Redis (like connecting to a website)
Jedis redis = new Jedis("redis-server-address", 6379);

// Step 2: Store data
redis.setex("product:123", 3600, productJson);
// This data is available to ALL app servers!

// Step 3: Read data (from any server)
String json = redis.get("product:123");
// If found → instant!
// If null → not in cache, go to database
```

**When to use Distributed Cache:**
- Multiple servers need the same data
- Data changes and servers must see updates
- You need more memory than one server has

### Which One Should You Use?

```
YOU NEED                              BEST CHOICE
─────────────────────────────────────────────────
Fastest possible (< 1ms)              Local Cache
Servers can have own copies           Local Cache
Data rarely changes                   Local Cache

All servers see same data             Distributed (Redis)
Data changes frequently               Distributed (Redis)
Need more memory than 1 server        Distributed (Redis)
Data must survive restarts            Distributed (Redis)
```

### BONUS: Use BOTH Together (Multi-Layer)

This is what big companies do. Two layers of cache.

```java
public Product getProduct(Long id) {
    // ─── LAYER 1: Local Cache (< 1ms) ───
    // In the application's memory. Super fast.
    // Each server has its own copy.
    Product p = localCache.getIfPresent(id);
    if (p != null) {
        return p;  // 🚀 Found in local! Instant!
    }
    
    // ─── LAYER 2: Redis Cache (~2ms) ───
    // Shared across all servers. Slower but shared.
    String key = "product:" + id;
    p = redis.get(key);
    if (p != null) {
        localCache.put(id, p);  // save in local for next time
        return p;  // 🚀 Found in Redis! Fast!
    }
    
    // ─── LAYER 3: Database (~100ms) ───
    // Only if BOTH caches miss.
    p = database.query("SELECT * FROM products WHERE id = ?", id);
    
    // Save in both caches
    redis.setex(key, 3600, p);      // Redis: keeps for 1 hour
    localCache.put(id, p);           // Local: keeps for 10 min
    
    return p;
}
```

**Why this works:**
```
100 requests:

Layer 1 (Local):  70 hits → 70 served in <1ms
Layer 2 (Redis):  25 hits → 25 served in ~2ms  
Layer 3 (DB):      5 hits →  5 served in ~100ms

Database went from 100 queries → 5 queries (95% reduction!)
```


The short answer is **where the memory lives relative to your application process**.

* **Caffeine** is an **In-Process Cache**. It runs inside the exact same Java Virtual Machine (JVM) memory footprint as your Java application.
* **Memcached** is a **Distributed / Remote Cache**. It runs as an independent service on its own server (or container) outside your application.

Let's break down the intuition, mechanics, and trade-offs step-by-step.

---

## 1. Visual Intuition

Imagine you are working at your office desk:

* **Caffeine (In-Process):** Like keeping a notebook **right on your desk**. Looking up an answer takes 0 seconds. But if you get up and leave the desk (app restarts), the desk is cleared.
* **Memcached (Distributed):** Like a shared filing cabinet **down the hallway**. You have to walk down the hall (network call) to fetch the document. It takes slightly longer, but everyone in the office (all microservice instances) shares that same filing cabinet.

```
+-------------------------------------------------------------------+
|  SERVER / CONTAINER                                               |
|                                                                   |
|   +-----------------------------------------------------------+   |
|   |  Java Application (JVM)                                   |   |
|   |                                                           |   |
|   |   +-----------------------+                               |   |
|   |   |   Caffeine Cache      |  <-- In-Memory (Heap)         |   |
|   |   |   (Nanoseconds access)|      Same Process             |   |
|   |   +-----------------------+                               |   |
|   +-----------------------------------------------------------+   |
+-------------------------------------------------------------------+
                               |
                        Network Call (TCP)
                               |
                               v
+-------------------------------------------------------------------+
|  MEMCACHED SERVER                                                 |
|                                                                   |
|   +-----------------------------------------------------------+   |
|   |  Memcached Process                                        |   |
|   |  (Milliseconds / Microseconds access)                      |   |
|   +-----------------------------------------------------------+   |
+-------------------------------------------------------------------+

```

---

## 2. Key Differences Breakdown

### 1. Speed & Network Overhead

* **Caffeine:** **Nanoseconds.** Access is a simple Java pointer lookup in RAM (`heap memory`). No serialization, no network socket, no context switching.
* **Memcached:** **Microseconds to Milliseconds.** Even though data is in RAM, your Java app must serialize the Java object into bytes, send it over TCP/IP across the network, and Memcached must deserialize it.

### 2. Multi-Instance / Horizontal Scaling

* **Caffeine:** **Isolated per instance.** If you scale your application to 5 instances behind a load balancer, each instance maintains its *own separate* Caffeine cache.
* *Problem:* Instance 1 might have cached `User A`'s updated profile, but Instance 2 still has `User A`'s old profile or a cache miss.


* **Memcached:** **Shared across all instances.** All 5 instances connect to the same centralized Memcached cluster. If Instance 1 writes to Memcached, Instance 2 reads that exact same value immediately.

### 3. Impact on JVM Garbage Collection (GC)

* **Caffeine:** Lives on the **Java Heap**. Storing millions of heavy objects in Caffeine increases memory pressure on the JVM, which can trigger GC pauses. (Caffeine uses highly optimized structures, but heap limit rules still apply).
* **Memcached:** Lives **completely outside the JVM**. It uses native system RAM, so storing 50 GB in Memcached has **zero impact** on your Java app's Garbage Collection.

---

## 3. Comparison Matrix

| Feature | Caffeine (Java) | Memcached |
| --- | --- | --- |
| **Type** | Local / In-Process | Distributed / Out-of-Process |
| **Location** | Inside JVM Heap | External Server / Container |
| **Access Latency** | ~10 - 100 **nanoseconds** | ~1 - 5 **milliseconds** (network dependent) |
| **Data Sharing** | No (Local to single JVM) | Yes (Shared across all app nodes) |
| **Network Overhead** | Zero | TCP/Socket Overhead |
| **Serialization Needed?** | No (Stores direct Java references) | Yes (Must convert objects to bytes) |
| **GC Pressure** | Yes (Consumes JVM Heap) | No (Completely independent) |

---

## 4. When to Use Which?

### Use Caffeine when:

1. **Speed is paramount:** You need absolute lowest latency (e.g., config flags, static reference data, high-frequency lookup tables).
2. **Data size is relatively small:** Fits comfortably in your assigned `-Xmx` JVM heap without causing GC stalls.
3. **Inconsistency across instances is tolerable:** It doesn't matter if Node A and Node B update their local cache a few seconds apart.

### Use Memcached when:

1. **You have many app instances:** You need a single "source of truth" cache shared across horizontal nodes.
2. **Data size is huge:** You need to cache gigabytes or terabytes of session/database data that won't fit in a single JVM heap.
3. **Cache survival across app redeploys:** When you deploy new Java code and restart your app container, Caffeine dies. Memcached stays alive on its own server, so your cache remains warm.

---

## 5. Pro Pattern: Two-Level Caching (L1 + L2)

In high-scale enterprise backend architectures, you often **combine both**:

```
[Request] ---> L1 Cache (Caffeine - In-Process)
                   | (Cache Miss)
                   v
               L2 Cache (Memcached/Redis - Distributed)
                   | (Cache Miss)
                   v
               Primary Database (PostgreSQL/MySQL)

```

1. **Check Caffeine (L1):** Ultra-fast lookup (~10ns).
2. **If Miss, check Memcached (L2):** Fetch over network (~2ms).
3. **If Miss, query Database:** Fetch from disk (~20ms), then populate both L2 and L1.

This gives you the raw speed of in-process caching for hot keys alongside the consistency of a shared distributed cache.


---

## Chapter 6: What is Memcached?

### Memcached vs Redis — The Simple Version

```
MEMCACHED = A basic storage locker
  - Put a box in, take a box out
  - That's ALL it does
  - If power goes out, everything is gone

REDIS = A storage locker WITH extra features
  - Everything Memcached does
  - PLUS: Organize boxes into lists and groups
  - PLUS: Backup copy (survives restart)
  - PLUS: Can notify other systems when things change
```

### Code Comparison

```java
// ─── MEMCACHED (simple, limited) ───
// Only 5 commands: get, set, delete, add, flush_all

MemcachedClient mc = new MemcachedClient("server", 11211);

// Store: key → value (value must be a string)
mc.set("user:123", 3600, "{\"name\": \"Alice\"}");
//            TTL    value

// Read:
String data = (String) mc.get("user:123");
// Returns null if not found

// THAT'S IT. Nothing more.
// No lists, no sets, no atomic operations.
```

```java
// ─── REDIS (powerful, many features) ───
// 200+ commands. Can do everything Memcached does, plus more.

Jedis redis = new Jedis("server", 6379);

// Same as Memcached:
redis.setex("user:123", 3600, "{\"name\": \"Alice\"}");
String data = redis.get("user:123");

// EXTRA: Lists (ordered collection — like a to-do list)
redis.lpush("recent:products:123", "product:456");
redis.lpush("recent:products:123", "product:789");
List<String> recent = redis.lrange("recent:products:123", 0, 9);
// Returns: ["product:789", "product:456"] (most recent first)

// EXTRA: Sets (unique items — no duplicates)
redis.sadd("user:roles:123", "admin", "editor");
boolean isAdmin = redis.sismember("user:roles:123", "admin");
// Returns: true

// EXTRA: Atomic counter (thread-safe increment)
redis.incr("page:views:123");
// Even if 1000 people call this at the same time → correct count

// EXTRA: Persistence (survives restart)
// Redis can save data to disk. Memcached cannot.
```

### When to Use Which

```
MEMCACHED — Use when:
  ✓ You only need get/set/delete (simple key-value)
  ✓ You don't care about data surviving restarts
  ✓ You want maximum simplicity
  
  Example: Cache HTML fragments, API responses

REDIS — Use when:
  ✓ You need data structures (lists, sets, sorted sets)
  ✓ You need data to survive restarts
  ✓ You need atomic operations (counters)
  ✓ You need Pub/Sub for notifications
  
  Example: Session storage, leaderboards, rate limiting, real-time analytics
```

**Simple rule:** Start with Redis. It does everything Memcached does and much more. Memcached is only better if you need raw simplicity.

---

## Chapter 7: Cache-Aside Pattern (Most Common)

This is the pattern you'll use 90% of the time.

### How It Works

```
1. User asks for data
2. You check cache first
3. If data in cache → return it (HIT!)
4. If not in cache → query database (MISS!)
5. Store result in cache for next time
6. Return to user
```

### Real-World Analogy

```
You want to know today's special at a restaurant.

Step 1: Check the chalkboard outside (Cache).
Step 2: If it's written there → you know it! (HIT)
Step 3: If it's blank → ask the waiter (Database).
Step 4: Waiter tells you, then writes it on the board.
Step 5: Next customer sees it on the board. (HIT!)
```

### Code With Line-by-Line Comments

```java
public Product getProduct(Long id) {
    // ─── STEP 1: TRY CACHE FIRST ───
    // Build a clear cache key: "product:123"
    // Always use this pattern: "type:id"
    String cacheKey = "product:" + id;
    
    // Try to get data from cache
    // Returns the data if found, null if not
    Product cached = redis.get(cacheKey);
    
    // ─── STEP 2: IF FOUND, RETURN INSTANTLY ───
    if (cached != null) {
        // CACHE HIT!
        // This is the FAST path
        // Takes ~2 milliseconds
        // No database call at all
        return cached;
    }
    
    // ─── STEP 3: NOT IN CACHE → QUERY DATABASE ───
    // This is the SLOW path
    // Takes ~100 milliseconds
    // Only happens when:
    //   a) This is the first time someone asked for this product
    //   b) The previous cache entry expired (TTL ran out)
    //   c) Cache was cleared
    Product product = database.query(
        "SELECT * FROM products WHERE id = ?", 
        id
    );
    
    // ─── STEP 4: SAVE IN CACHE FOR NEXT TIME ───
    // IMPORTANT: Always set a TTL!
    // Without TTL, cache will grow forever until memory fills up
    // TTL of 3600 = 1 hour
    // During that hour, ALL requests will be cache hits (fast!)
    redis.setex(cacheKey, 3600, product);
    //        ^key    ^TTL  ^value
    
    return product;
}
```

### What Happens Over Time

```
Imagine 10,000 users request product #123 in one hour:

FIRST USER:
  Cache MISS → Database query (100ms) → Save in cache → Return
  Time taken: 110ms

NEXT 9,999 USERS:
  Cache HIT → Return immediately (2ms)
  Time taken: 2ms each
  Database: NOT touched at all

TOTAL DATABASE QUERIES: 1 (instead of 10,000)
TOTAL TIME SAVED: ~16 minutes!
```

---

## Chapter 8: Write-Through Pattern (Update on Write)

### How It Works

When you UPDATE data, you also update the cache immediately.

```
WRITE:
  1. User updates data (e.g., changes email)
  2. Save to database first (source of truth)
  3. Delete or update the cache
  4. Tell user "Done!"

READ:
  Next user reads → cache hit with fresh data!
```

### Real-World Analogy

```
You change your address at the post office.

Step 1: They update their MASTER RECORD (Database).
Step 2: They also update the "quick reference card" (Cache).
Step 3: Next employee who checks → sees your new address immediately.
```

### Code

```java
// ─── WHEN USER UPDATES THEIR PROFILE ───
public void updateUserEmail(Long userId, String newEmail) {
    // Step 1: Update the DATABASE first
    // This is the SOURCE OF TRUTH
    // If cache crashes, we still have the data here
    database.execute(
        "UPDATE users SET email = ? WHERE id = ?",
        newEmail,
        userId
    );
    
    // Step 2: DELETE from cache
    // This forces the next read to get FRESH data from DB
    // Why delete instead of update? See below!
    redis.delete("user:" + userId);
    
    // Done! The next person who reads this user:
    // Cache MISS → reads from DB → gets new email → caches it
    // They see the updated email immediately
}
```

### Why DELETE Instead of UPDATE?

There's a subtle bug that happens with UPDATE:

```
SCENARIO: Two people looking at the same shopping cart

TIME 1: Cart has [Milk, Bread, Eggs]

Thread A reads cart → starts modifying (adds Butter)
Thread B removes Eggs → updates DB → updates cache with [Milk, Bread] 
Thread A finishes → overwrites cache with [Milk, Bread, Eggs, Butter] ← WRONG!

Eggs was removed! But the cache still shows Eggs because Thread A
wrote its version after Thread B's update.

FIX: DELETE instead of UPDATE

Thread B removes Eggs → DELETES cache entry
Thread A finishes → tries to save → cache miss → reads fresh [Milk, Bread] from DB
→ Now Thread A adds Butter to the correct list
```

**Rule of thumb:** Always DELETE from cache on update. Let the next read fetch fresh data.

---

## Chapter 9: Write-Behind Pattern (Fast Writes)

### How It Works

Write to cache FIRST (super fast), then write to database LATER (in background).

```
1. User updates data
2. Save to CACHE only (takes 2ms!)
3. Tell user "Done!" (user is happy, fast response)
4. BACKGROUND: Slowly write to database (takes 100ms)
   User doesn't wait for this!
```

### Real-World Analogy

```
You tell the waiter your order.

Waiter writes it on a notepad (Cache) and says "Got it!" — instant!
You get your confirmation in 2 seconds.
Waiter goes to the kitchen and tells the chef (Database) when he gets a chance.
Kitchen processes orders in batches. More efficient.
```

### Code

```java
public void updateInventory(Long skuId, int newQuantity) {
    // ─── STEP 1: WRITE TO CACHE ONLY (SUPER FAST!) ───
    // This completes in ~2 milliseconds
    // User gets instant confirmation
    String cacheKey = "inventory:" + skuId;
    redis.set(cacheKey, String.valueOf(newQuantity));
    
    // ─── STEP 2: SEND TO QUEUE FOR LATER ───
    // We'll write to database in a background process
    // User doesn't wait for this at all!
    kafka.send("inventory-updates", new InventoryUpdateEvent(
        skuId, newQuantity
    ));
    
    // Total time for user: ~5ms (very fast!)
}

// ─── BACKGROUND PROCESS (runs every 5 seconds) ───
@Scheduled(fixedRate = 5000)  // Runs every 5 seconds
public void syncToDatabase() {
    // Pick up all pending updates
    List<InventoryUpdateEvent> batch = kafka.poll("inventory-updates");
    
    if (batch.isEmpty()) return;
    
    // Batch update database (more efficient than one-by-one)
    database.batchExecute(
        "UPDATE inventory SET quantity = ? WHERE sku_id = ?",
        batch
    );
    
    System.out.println("Synced " + batch.size() + " updates to DB");
}
```

### Pros and Cons

```
PROS:
  ✓ Very fast writes (2ms vs 100ms)
  ✓ Reduces database load (batch writes)
  ✓ Great for high-volume writes

CONS:
  ✗ If cache crashes → DATA LOSS (data not yet in DB)
  ✗ Database is slightly behind (milliseconds to seconds)
  ✗ More complex (need message queue)
```

### When to Use

```
USE WRITE-BEHIND when:
  ✓ Speed is critical (user must get instant response)
  ✓ Small data loss is acceptable (a few seconds)
  ✓ You have high write volume

  Examples:
  - Page view counters (doesn't matter if a few views are lost)
  - Activity logs
  - Shopping cart saves (cart can be recreated)
  - Analytics events

DON'T USE when:
  ✗ Every write MUST be saved (bank transactions)
  ✗ Data must be immediately consistent
```

---

## Chapter 10: Cache Stampede — The Thundering Herd

### What Is It?

A popular cache key expires → ALL users miss cache at the same time → ALL hit the database → Database crashes.

```
NORMAL:
  Time 1s:  Request → Cache HIT (fast)
  Time 2s:  Request → Cache HIT (fast)
  Time 3s:  Request → Cache HIT (fast)
  ...
  99 out of 100 requests → Cache HIT ← database is happy

CACHE STAMPEDE:
  Time 20.000s:  TTL EXPIRES! Cache key deleted.
  Time 20.001s:  Request 1 → Cache MISS → DB Query
  Time 20.002s:  Request 2 → Cache MISS → DB Query
  Time 20.003s:  Request 3 → Cache MISS → DB Query
  ...
  Time 20.050s:  Request 500 → Cache MISS → DB Query
  
  Database: 500 queries in 50 milliseconds → CRASH!
```

### Real-World Example: Flash Sale

```
iPhone 15 flash sale. 1 million people click at exactly 8:00 AM.

8:00:00 AM:  First person clicks → loads from DB → stores in cache (TTL = 1 hour)
8:00:01 AM:  999,999 more clicks → all CACHE HIT (fast! all good!)
            ...
9:00:00 AM:  ⏰ TTL expires! Cache is empty!
9:00:01 AM:  1 million people refresh → ALL miss cache → ALL hit DB
             DB: "I can't handle 1 million queries!" → CRASH 💥
```

### Fix 1: Mutex Lock (Only One Goes to DB)

```java
public Product getProduct(Long id) {
    String cacheKey = "product:" + id;
    
    // Step 1: Try cache first
    Product cached = redis.get(cacheKey);
    if (cached != null) {
        return cached;  // 🚀 Cache hit! No problem.
    }
    
    // ─── CACHE MISS — CRITICAL SECTION ───
    // We need only ONE request to hit the database
    // All others should wait for that ONE to finish
    
    // Step 2: Try to acquire a lock
    // setnx = "set if not exists" — only succeeds ONCE
    String lockKey = "lock:" + cacheKey;
    boolean gotLock = redis.setnx(lockKey, "locked", 10);
    //                               value   lock expires in 10 sec
    
    if (gotLock) {
        // ✅ I'm THE lucky one who gets to query the database
        try {
            // Double-check: maybe someone else already loaded it
            // while we were getting the lock
            cached = redis.get(cacheKey);
            if (cached != null) {
                return cached;  // Someone else beat me to it
            }
            
            // Actually query the database
            cached = database.query("SELECT * FROM products WHERE id = ?", id);
            
            // Store in cache for everyone else
            redis.setex(cacheKey, 3600, cached);
            
            return cached;
            
        } finally {
            // Release the lock
            redis.delete(lockKey);
        }
    } else {
        // ❌ Someone else is already loading from DB
        // Wait 50 milliseconds, then retry
        Thread.sleep(50);
        
        // By now, the data should be in cache
        // Retry (will get cache hit this time)
        return getProduct(id);
    }
}
```

### Fix 2: Proactive Refresh (Don't Wait for Expiry)

Refresh the cache BEFORE it expires, not after.

```java
public Product getProduct(Long id) {
    String cacheKey = "product:" + id;
    
    // Step 1: Get the cached value
    Product cached = redis.get(cacheKey);
    
    if (cached == null) {
        // First time ever — must load from database
        cached = loadFromDB(id);
        redis.setex(cacheKey, 3600, cached);
        return cached;
    }
    
    // Step 2: Check how much TIME is LEFT
    Long ttl = redis.ttl(cacheKey);
    // TTL returns "seconds left" (e.g., 120 = 2 minutes)
    
    // Step 3: If TTL is running low, refresh in BACKGROUND
    if (ttl != null && ttl < 600) {
        // Less than 10 minutes left
        // Instead of letting it expire → stampede,
        // refresh NOW in a background thread
        
        executor.submit(() -> {
            Product fresh = database.query("SELECT * FROM products WHERE id = ?", id);
            redis.setex(cacheKey, 3600, fresh);
            // TTL reset to 1 hour. No stampede!
            System.out.println("Proactively refreshed: " + cacheKey);
        });
        
        // IMPORTANT: Return the CURRENT data immediately
        // User does NOT wait for the refresh
    }
    
    return cached;  // Return immediately, refresh runs in background
}
```

### Fix 3: Stale-While-Revalidate

Return old data IMMEDIATELY, refresh cache in background.

```java
public Product getProduct(Long id) {
    String activeKey = "product:" + id;       // Short TTL (1 hour)
    String staleKey = "product:" + id + ":stale";  // Long TTL (2 hours)
    
    // Step 1: Try active cache
    Product cached = redis.get(activeKey);
    if (cached != null) {
        return cached;  // 🚀 Fresh data, return immediately
    }
    
    // ─── ACTIVE CACHE EXPIRED ───
    // But we have a STALE backup we can use
    
    // Step 2: Try the stale backup
    Product stale = redis.get(staleKey);
    if (stale != null) {
        // Return STALE data immediately (don't block the user!)
        // Meanwhile, refresh active cache in background
        executor.submit(() -> {
            Product fresh = database.query("SELECT * FROM products WHERE id = ?", id);
            redis.setex(activeKey, 3600, fresh);  // Active: 1 hour
            redis.setex(staleKey, 7200, fresh);   // Stale: 2 hours
        });
        
        return stale;  // Return stale data NOW
                       // Next request will get fresh data
    }
    
    // ─── BOTH MISSED — Must load from DB (one time only) ───
    Product fresh = database.query("SELECT * FROM products WHERE id = ?", id);
    redis.setex(activeKey, 3600, fresh);
    redis.setex(staleKey, 7200, fresh);
    return fresh;
}
```

---

## Chapter 11: Real E-Commerce Use Cases (100M Users)

### Use Case 1: Product Catalog

**The situation:**
- 10 million products
- 100,000 users browsing per second
- Products change rarely (admin updates ~100 times per day)

**The problem:** Reading every product from DB = 100,000 queries/sec = crash

**The solution:** Cache everything with TTL. Invalidate on admin update.

```java
@Service
public class ProductService {
    
    // Layer 1: Local cache (in-memory, per server)
    // Fastest possible — < 1 millisecond
    private Cache<Long, Product> localCache = Caffeine.newBuilder()
        .maximumSize(5000)              // hottest 5000 products
        .expireAfterWrite(10, MINUTES)  // refresh every 10 min
        .build();
    
    // Layer 2: Redis (shared across ALL servers)
    private final RedisTemplate<String, Product> redis;
    
    public Product getProduct(Long id) {
        // ─── LAYER 1: Local cache ───
        // Catches about 70% of requests
        Product p = localCache.getIfPresent(id);
        if (p != null) {
            return p;  // 🚀 Instant! < 1ms
        }
        
        // ─── LAYER 2: Redis cache ───
        // Catches about 25% of requests
        String key = "product:" + id;
        p = redis.opsForValue().get(key);
        if (p != null) {
            localCache.put(id, p);  // Save in local for next time
            return p;  // 🚀 ~2ms
        }
        
        // ─── LAYER 3: Database ───
        // Only 5% of requests reach here
        p = productRepo.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        
        // Store in both caches
        redis.opsForValue().set(key, p, 1, HOURS);  // Redis: 1 hour
        localCache.put(id, p);                        // Local: 10 min
        
        return p;
    }
    
    // When admin updates a product:
    // 1. Update database
    // 2. Delete from cache (next read gets fresh data)
    @CacheEvict(value = "products", key = "#id")
    public Product updateProduct(Long id, Product updated) {
        return productRepo.save(updated);
        // @CacheEvict automatically deletes the Redis key
    }
}
```

**What happens:**
```
100,000 requests per second
  Layer 1 (local): 70,000 hits at <1ms
  Layer 2 (Redis): 25,000 hits at ~2ms
  Database:         5,000 queries at ~50ms

Database load reduced by 95%!
```

### Use Case 2: Inventory (Must Be Accurate)

**The situation:**
- 10 million products with stock quantities
- 50,000 users adding to cart per second
- MUST NOT oversell!

**The problem:** Inventory changes constantly. Can't cache for long. But DB can't handle 50K writes/sec.

**The solution:** Use Redis for REAL-TIME inventory with atomic operations.

```java
@Service
public class InventoryService {
    
    private final RedisTemplate<String, String> redis;
    
    // ─── CHECK STOCK ───
    public int getStock(Long skuId) {
        String key = "stock:" + skuId;
        
        // Try cache
        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            return Integer.parseInt(cached);  // 🚀 fast!
        }
        
        // Cache miss → database
        int stock = inventoryRepo.findStockBySkuId(skuId);
        
        // Store with SHORT TTL (30 seconds)
        // Inventory changes often, can't cache for longer
        redis.opsForValue().set(key, String.valueOf(stock), 30, SECONDS);
        
        return stock;
    }
    
    // ─── RESERVE FOR CART (ATOMIC) ───
    // Multiple users might try to buy the last item at the same time
    // We need to make sure we don't oversell
    public boolean reserveStock(Long skuId, int quantity) {
        String key = "stock:" + skuId;
        
        // Atomic decrement in Redis
        // Redis handles this ONE AT A TIME
        // Thread 1: decrement by 2 → remaining = 8
        // Thread 2: decrement by 3 → remaining = 5
        // NEVER race condition!
        Long remaining = redis.opsForValue().decrement(key, quantity);
        
        if (remaining == null) {
            // Key doesn't exist in cache — load from DB first
            loadStockFromDB(skuId);
            remaining = redis.opsForValue().decrement(key, quantity);
        }
        
        if (remaining >= 0) {
            return true;  // ✅ Reserved successfully
        }
        
        // Oversold! Rollback
        redis.opsForValue().increment(key, quantity);  // add back
        return false;  // ❌ Not enough stock
    }
}
```

### Use Case 3: Session Storage

**The situation:**
- 100 million registered users
- 20 million active per day
- Each request needs to check "who is this user?"

**The problem:** Can't query database for every single page load.

**The solution:** Store sessions in Redis. 24-hour TTL. Slide on each access.

```java
@Service
public class SessionService {
    
    private final RedisTemplate<String, Session> redis;
    
    // ─── CHECK SESSION ───
    public Session getSession(String sessionId) {
        String key = "session:" + sessionId;
        
        Session session = redis.opsForValue().get(key);
        
        if (session == null) {
            // Session expired or never existed
            return null;
        }
        
        // Slide TTL — extend session on each access
        // If user is actively using the site, keep session alive
        redis.expire(key, 24, TimeUnit.HOURS);
        //           ^reset timer to 24 hours
        
        return session;
    }
    
    // ─── CREATE SESSION (when user logs in) ───
    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        
        Session session = new Session(sessionId, user.getId(), user.getRole());
        
        redis.opsForValue().set(
            "session:" + sessionId,
            session,
            24, TimeUnit.HOURS  // Expires in 24 hours
        );
        
        return sessionId;  // Give this to the browser as a cookie
    }
    
    // ─── LOGOUT (delete session) ───
    public void invalidateSession(String sessionId) {
        redis.delete("session:" + sessionId);
    }
}
```

---

## Chapter 12: Cache Cheat Sheet

### Quick Decision Guide

```
YOUR SITUATION                          BEST APPROACH
────────────────────────────────────────────────────────────
Reading data that rarely changes        Cache-Aside + Long TTL
Reading data that changes often         Cache-Aside + Short TTL
Writing data, need fast response        Write-Behind
Writing data, must be accurate          Write-Through
Cache key expires → all hit DB          Mutex Lock or Proactive Refresh
Data must be fresh immediately          Explicit Invalidation
Data can be slightly stale              TTL-only
Need ultra-fast reads                   Local Cache (Caffeine)
Multiple servers need same data         Distributed Cache (Redis)
```

### Common Mistakes

```
MISTAKE                    WHY IT'S BAD           FIX
─────────────────────────  ────────────────────  ────────────────────
No TTL on cache            Memory fills up       Always set TTL
Too long TTL               Users see stale data  Set TTL based on data
Too short TTL              Too many DB queries   Longer TTL = more hits
No cache invalidation      Users see old data    Delete cache on update
Caching everything         Memory waste          Cache only what's hot
No monitoring              Don't know hit rate   Track cache metrics
Cache as database          Data lost on crash    DB is source of truth
```

### Cache Hit Rate Targets

```
Hit Rate    Meaning
──────────  ──────────────────────────────────────
> 95%       Excellent! Cache is very effective
90-95%      Good. Only 5-10% hit database
80-90%      Okay. Could be better
< 80%       Poor. Check your caching strategy
```

### 30-Second Summary

```
CACHING = Copy of slow data, stored somewhere fast.

Cache HIT  → Fast (2ms)  → Data found in cache
Cache MISS → Slow (100ms) → Must go to database

TTL = Time To Live. Data auto-deletes after N seconds.

Local Cache (Caffeine): Per server, <1ms, own copy
Redis Cache: Shared across servers, ~2ms, consistent

Patterns:
  Cache-Aside:    Check → Miss → Load DB → Store (most common)
  Write-Through:  Write DB + delete cache (fresh reads)
  Write-Behind:   Write cache → async → DB (fast writes)

Stampede prevention:
  Mutex Lock:     Only one hits DB, others wait
  Proactive:      Refresh before TTL expires
  Stale-while:    Return old data, refresh in background

RULES:
  - Always set TTL (no memory leak)
  - Delete on write (no stale data)
  - DB is source of truth (not cache)
  - Monitor hit rate (should be > 90%)