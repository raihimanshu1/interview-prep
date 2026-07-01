# 🗄️ Problem 26: Key-Value Store (Like Redis/LevelDB)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any infra company  
> **Est. Time**: 120 min | **Patterns**: HashMap, WAL, MemTable, SSTable

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design an in-memory key-value store."

**What the interviewer tests**:
```
1. Can you make reads fast? (O(1) lookups)
2. Can you handle writes without losing data? (Durability)
3. Can you handle data larger than memory? (Disk spill)
4. Can you handle crashes? (No data loss)
```

### Step 2: The "Aha!" Moment

The core insight: **Two-layer architecture.**

```
LAYER 1: In-memory HashMap (MemTable)
  - All writes go here
  - O(1) reads
  - Survives until crash

LAYER 2: Disk (SSTable - Sorted Strings Table)
  - Periodic snapshots of MemTable
  - Immutable append-only files
  - Sequential writes (fast on disk)

When MemTable is full:
  1. Write MemTable to disk as new SSTable
  2. Create empty MemTable
  3. Old SSTables are merged in background (compaction)
```

### Step 3: How to ensure durability?

```
Option A: WAL (Write-Ahead Log)
  Before writing to MemTable, append to WAL on disk.
  On crash: Replay WAL to rebuild MemTable.

Option B: Snapshot + WAL (Better)
  Periodically snapshot MemTable and truncate WAL.
  On crash: Load latest snapshot + replay remaining WAL.

This is EXACTLY how LevelDB/RocksDB works.
```

---

## 💻 Core Implementation

```java
package com.kvstore;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: KeyValueStore is the main API.
 * 
 * Architecture:
 * - MemTable: In-memory sorted map (ConcurrentSkipListMap)
 * - SSTable: Immutable sorted file on disk
 * - WAL: Append-only log for durability
 */
public class KeyValueStore {
    
    // In-memory sorted map (auto-sorted by key)
    private final ConcurrentSkipListMap<String, String> memTable;
    
    // Disk-based sorted tables (immutable)
    private final List<SSTable> ssTables;
    
    // Write-ahead log for crash recovery
    private final WAL wal;
    
    // Configuration
    private static final int MEMTABLE_SIZE_LIMIT = 1000;  // Flush to disk after N entries
    private int writeCount = 0;

    public KeyValueStore(String dataDir) throws IOException {
        this.memTable = new ConcurrentSkipListMap<>();
        this.ssTables = new CopyOnWriteArrayList<>();
        this.wal = new WAL(dataDir);
        
        // Recover from WAL on startup
        recover();
    }

    /**
     * INTUITION: PUT operation.
     * 
     * 1. Write to WAL (durability)
     * 2. Write to MemTable (fast read)
     * 3. If MemTable full, flush to SSTable
     * 
     * @param key The key
     * @param value The value
     */
    public void put(String key, String value) {
        // Step 1: Write to WAL FIRST (before memtable)
        wal.append(new WALEntry(key, value));
        
        // Step 2: Write to MemTable
        memTable.put(key, value);
        writeCount++;
        
        // Step 3: Check if MemTable is full
        if (writeCount >= MEMTABLE_SIZE_LIMIT) {
            flushToSSTable();
        }
    }

    /**
     * INTUITION: GET operation.
     * 
     * 1. Check MemTable (newest data)
     * 2. If miss, check SSTables newest to oldest
     * 3. Return null if not found
     * 
     * @param key The key
     * @return Value or null
     */
    public String get(String key) {
        // Step 1: Check MemTable first (most recent)
        String value = memTable.get(key);
        if (value != null) {
            return value;
        }
        
        // Step 2: Check SSTables (newest to oldest)
        // Newer SSTables override older ones
        for (int i = ssTables.size() - 1; i >= 0; i--) {
            value = ssTables.get(i).get(key);
            if (value != null) {
                return value;
            }
        }
        
        // Not found
        return null;
    }

    /**
     * INTUITION: Flush MemTable to disk.
     * 
     * 1. Sort MemTable entries
     * 2. Write to new SSTable file
     * 3. Clear MemTable
     * 4. Reset write counter
     * 5. Truncate WAL
     */
    private synchronized void flushToSSTable() {
        System.out.println("Flushing MemTable to SSTable...");
        
        // Create new SSTable from current MemTable
        SSTable ssTable = new SSTable(memTable);
        ssTables.add(ssTable);
        
        // Clear MemTable
        memTable.clear();
        writeCount = 0;
        
        // Truncate WAL (data is now in SSTable)
        wal.truncate();
        
        // Trigger compaction (merge old SSTables)
        compact();
    }

    /**
     * INTUITION: Compaction merges old SSTables.
     * 
     * Why? Because deletions leave tombstones.
     * Multiple versions of same key across SSTables.
     * Compaction merges them and removes duplicates/deletions.
     */
    private void compact() {
        // In production: merge sorted SSTables efficiently
        // For demo, just note it
        System.out.println("Compacting " + ssTables.size() + " SSTables...");
    }

    /**
     * INTUITION: Recover from WAL on startup.
     * 
     * If system crashed, MemTable is lost.
     * WAL has all recent writes.
     * Replay WAL to rebuild MemTable.
     */
    private void recover() {
        List<WALEntry> entries = wal.readAll();
        System.out.println("Recovering " + entries.size() + " entries from WAL...");
        
        for (WALEntry entry : entries) {
            memTable.put(entry.getKey(), entry.getValue());
        }
        writeCount = entries.size();
    }

    public void close() throws IOException {
        wal.close();
    }
}
```

```java
package com.kvstore;

import java.io.*;
import java.util.*;

/**
 * INTUITION: SSTable is an immutable sorted file.
 * 
 * "Sorted Strings Table"
 * - All entries are sorted by key
 * - Once written, NEVER modified (append-only)
 * - Enables binary search O(log N)
 * 
 * This is the foundation of LSM-tree storage engines (LevelDB, RocksDB, Cassandra).
 */
class SSTable {
    private final File file;
    private final Map<String, String> index;  // In-memory index for fast lookup

    SSTable(Map<String, String> data) throws IOException {
        // Sort data by key
        TreeMap<String, String> sorted = new TreeMap<>(data);
        
        // Create file
        this.file = new File("sstable_" + System.currentTimeMillis() + ".dat");
        this.index = new HashMap<>();
        
        // Write sorted data to disk
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> entry : sorted.entrySet()) {
                // Build block index (every 100 entries)
                if (index.size() % 100 == 0) {
                    index.put(entry.getKey(), file.getAbsolutePath());
                }
                
                // Write: key,value\n
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        }
    }

    /**
     * Get value by key from SSTable.
     * Uses binary search on the sorted file.
     */
    String get(String key) {
        // For demo, scan linearly. Production would use binary search.
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts[0].equals(key)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

```java
package com.kvstore;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * INTUITION: WAL (Write-Ahead Log) ensures durability.
 * 
 * "Write-Ahead" means: write to WAL BEFORE writing to main data store.
 * 
 * WHY? If we write to MemTable first and crash:
 *   - WAL exists → we can recover
 *   - WAL doesn't exist → data is gone
 * 
 * WAL format: append-only log file
 * [key1,value1]
 * [key2,value2]
 * ...
 * 
 * On recovery: Read all lines and replay into empty MemTable.
 */
class WAL {
    private final File file;
    private final BufferedWriter writer;
    private final AtomicLong sequence = new AtomicLong(0);
    private final String filePath;

    WAL(String dataDir) throws IOException {
        this.filePath = dataDir + "/wal.log";
        this.file = new File(filePath);
        // Append mode (preserve existing WAL for recovery)
        this.writer = new BufferedWriter(new FileWriter(file, true));
    }

    void append(WALEntry entry) {
        try {
            long seq = sequence.incrementAndGet();
            writer.write(seq + "," + entry.getKey() + "," + entry.getValue());
            writer.newLine();
            writer.flush();  // Force to disk (in real system: group commits)
        } catch (IOException e) {
            throw new RuntimeException("WAL write failed", e);
        }
    }

    List<WALEntry> readAll() {
        List<WALEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    entries.add(new WALEntry(parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    void truncate() {
        try {
            writer.close();
            new FileWriter(file, false).close();  // Truncate
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close() throws IOException {
        writer.close();
    }
}

class WALEntry {
    private final String key;
    private final String value;

    WALEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    String getKey() { return key; }
    String getValue() { return value; }
}
```

```java
package com.kvstore;

/**
 * INTUITION: Iterator for scanning keys in range.
 * Like `cursor` in MongoDB or `SCAN` in Redis.
 */
public class KeyIterator {
    private final KeyValueStore store;
    private String currentKey;
    private String prefix;

    public KeyIterator(KeyValueStore store, String prefix) {
        this.store = store;
        this.prefix = prefix;
        this.currentKey = null;
    }

    public String next() {
        // Simplified - in production, maintain position across MemTable + SSTables
        return null;
    }

    public boolean hasNext() {
        return false;
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle deletes?"
> "Write a tombstone (delete marker) to MemTable. During compaction, tombstones are discarded. This is cheaper than physical deletion."

### Q2: "How to support transactions (atomic read-modify-write)?"
> "Use compare-and-swap (CAS). Read current value, compute new value, write only if matches. Or use MVCC for multi-version concurrency."

### Q3: "How to handle hot keys (one key read 1M times/sec)?"
> "Cache hot keys in Redis. Use LRU eviction. Shard by key to distribute load."

### Q4: "How to support range scans?"
> "Use memtable + SSTable iterators. Concatenate sorted results. Deduplicate by key (newer wins)."