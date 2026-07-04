# 🗃️ Problem 29: Database (SQLite-like)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any infra company  
> **Est. Time**: 120 min | **Patterns**: B-Tree, Parser, Storage Engine

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a simple database."

**What the interviewer tests**:
```
1. Can you design a B-Tree for indexes?
2. Can you parse SQL?
3. Can you manage transactions (commit/rollback)?
4. Can you handle concurrent reads/writes?
```

### Step 2: The "Aha!" Moment

The key insight: **B-Tree for O(log n) reads/writes.**

```
Data stored in pages (blocks) on disk.

B-Tree structure:
        [P1]
       /     \
    [P2]     [P3]
   /  |  \   /  |  \
  L1 L2 L3 L4 L5 L6  (Leaf pages with actual data)

Search: Start at root, follow pointers down to leaf.
Insert: Find leaf, insert, split if overflow.
Delete: Find leaf, delete, merge if underflow.

Height 3 B-Tree with 100 keys per page → stores 1M+ keys.
Only 3 disk reads for any lookup!
```

### Step 3: How to handle transactions?

```
Write-Ahead Log (WAL):
  - Before modifying database, write changes to WAL
  - WAL is append-only (fast sequential writes)
  - On crash, replay WAL to restore consistency
  
Transactions:
  - BEGIN TRANSACTION
  - Multiple INSERT/UPDATE/DELETE
  - COMMIT (flush WAL) or ROLLBACK (discard WAL)
```

---

## 💻 Core Implementation

```java
package com.db;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * INTUITION: SimpleDatabase is the main API.
 * 
 * Uses:
 * - B-Tree for indexes
 * - WAL for transactions
 * - Page-based storage
 */
public class SimpleDatabase {
    
    private final BTree bTree;
    private final WAL wal;
    private final int pageSize = 4096;  // 4KB pages
    
    public SimpleDatabase(String filePath) throws IOException {
        this.bTree = new BTree(pageSize);
        this.wal = new WAL(filePath + ".wal");
        
        // Recover from WAL if exists
        recover();
    }

    /**
     * INSERT: Add a key-value pair.
     */
    public synchronized void insert(long key, String value) {
        // Write to WAL first
        wal.log(new WALEntry("INSERT", key, value));
        
        // Insert into B-Tree
        bTree.insert(key, value);
    }

    /**
     * GET: Retrieve value by key.
     */
    public String get(long key) {
        return bTree.search(key);
    }

    /**
     * DELETE: Remove key-value pair.
     */
    public synchronized void delete(long key) {
        wal.log(new WALEntry("DELETE", key, null));
        bTree.delete(key);
    }

    /**
     * BEGIN TRANSACTION.
     */
    public void beginTransaction() {
        wal.beginTransaction();
    }

    /**
     * COMMIT: Make changes permanent.
     */
    public synchronized void commit() {
        wal.commit();
    }

    /**
     * ROLLBACK: Discard changes in current transaction.
     */
    public synchronized void rollback() {
        wal.rollback();
        // Reload from last checkpoint
        bTree.rebuildFromWAL(wal.getLastCheckpoint());
    }

    /**
     * Recover from WAL after crash.
     */
    private void recover() {
        if (wal.hasUncommittedChanges()) {
            System.out.println("Recovering from WAL...");
            // Replay uncommitted changes or rollback
            wal.rollback();
        }
    }

    public void close() throws IOException {
        wal.close();
    }
}
```

```java
package com.db;

import java.io.*;
import java.util.*;

/**
 * INTUITION: BTreeNode represents a node in the B-Tree.
 * 
 * Can be either:
 * - Internal node: Contains keys and child pointers
 * - Leaf node: Contains actual data
 * 
 * Keys are sorted. Child i contains keys < keys[i].
 */
class BTreeNode {
    boolean isLeaf;
    List<Long> keys;
    List<String> values;      // Only used in leaf nodes
    List<BTreeNode> children;  // Only used in internal nodes
    
    static final int ORDER = 100;  // Max keys per node

    BTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
        this.children = new ArrayList<>();
    }
}
```

```java
package com.db;

import java.io.*;
import java.util.*;

/**
 * INTUITION: BTree is the index structure.
 * 
 * Self-balancing tree that keeps data sorted.
 * All leaves are at the same depth.
 * 
 * Properties:
 * - Every node has ORDER/2 to ORDER keys
 * - Every leaf has at least ORDER/2 keys
 * - All keys in left subtree < key < all keys in right subtree
 */
class BTree {
    private final int pageSize;
    private BTreeNode root;
    private final int order;

    BTree(int pageSize) {
        this.pageSize = pageSize;
        this.order = 100;  // Max keys per node
        this.root = new BTreeNode(true);
    }

    /**
     * SEARCH: O(log N)
     */
    String search(long key) {
        return search(root, key);
    }

    private String search(BTreeNode node, long key) {
        int i = 0;
        while (i < node.keys.size() && key > node.keys.get(i)) {
            i++;
        }
        
        if (i < node.keys.size() && key == node.keys.get(i)) {
            // Found in internal node
            if (node.isLeaf) {
                return node.values.get(i);
            }
        }
        
        if (node.isLeaf) {
            return null;  // Not found
        }
        
        // Traverse to child
        return search(node.children.get(i), key);
    }

    /**
     * INSERT: O(log N) with possible split
     */
    void insert(long key, String value) {
        BTreeNode newRoot = insert(root, key, value);
        if (newRoot != null) {
            root = newRoot;
        }
    }

    private BTreeNode insert(BTreeNode node, long key, String value) {
        // Find position
        int i = 0;
        while (i < node.keys.size() && key > node.keys.get(i)) {
            i++;
        }
        
        if (node.isLeaf) {
            // Insert into leaf
            node.keys.add(i, key);
            node.values.add(i, value);
            
            // Check if overflow
            if (node.keys.size() > order) {
                return split(node);
            }
            return null;
        } else {
            // Insert into child
            BTreeNode newChild = insert(node.children.get(i), key, value);
            if (newChild != null) {
                // Child split, promote middle key
                node.keys.add(i, newChild.keys.get(newChild.keys.size() / 2));
                node.values.add(i, newChild.values.get(newChild.keys.size() / 2));
                node.children.add(i + 1, newChild);
                
                if (node.keys.size() > order) {
                    return split(node);
                }
            }
            return null;
        }
    }

    /**
     * SPLIT: When node overflows, split into two.
     */
    private BTreeNode split(BTreeNode node) {
        int mid = node.keys.size() / 2;
        
        // Create new sibling
        BTreeNode sibling = new BTreeNode(node.isLeaf);
        
        // Move half keys to sibling
        sibling.keys = new ArrayList<>(node.keys.subList(mid, node.keys.size()));
        sibling.values = new ArrayList<>(node.values.subList(mid, node.values.size()));
        
        if (!node.isLeaf) {
            sibling.children = new ArrayList<>(node.children.subList(mid + 1, node.children.size()));
            node.children.subList(mid + 1, node.children.size()).clear();
        }
        
        // Truncate original node
        node.keys = new ArrayList<>(node.keys.subList(0, mid));
        node.values = new ArrayList<>(node.values.subList(0, mid));
        
        // Create new parent
        BTreeNode newParent = new BTreeNode(false);
        newParent.keys.add(node.keys.get(node.keys.size() - 1));
        newParent.values.add(node.values.get(node.values.size() - 1));
        newParent.children.add(node);
        newParent.children.add(sibling);
        
        return newParent;
    }

    void delete(long key) {
        // Simplified: mark as deleted (tombstone)
        // Real B-Tree delete is complex (borrow/merge)
    }

    void rebuildFromWAL(List<WALEntry> entries) {
        root = new BTreeNode(true);
        for (WALEntry entry : entries) {
            if ("INSERT".equals(entry.getType())) {
                insert(entry.getKey(), entry.getValue());
            }
        }
    }
}
```

```java
package com.db;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * INTUITION: WAL (Write-Ahead Log) for durability.
 * 
 * Format:
 * [txnId] [type] [key] [value?]
 * 
 * On recovery: Replay log to restore state.
 */
class WAL {
    private final File file;
    private final BufferedWriter writer;
    private final AtomicLong txnId = new AtomicLong(0);
    private boolean inTransaction = false;
    private final List<String> currentTxn = new ArrayList<>();

    WAL(String filePath) throws IOException {
        this.file = new File(filePath);
        this.writer = new BufferedWriter(new FileWriter(file, true));
    }

    void beginTransaction() {
        inTransaction = true;
    }

    void log(WALEntry entry) {
        String line = txnId.incrementAndGet() + "," + 
                      entry.getType() + "," + 
                      entry.getKey() + "," +
                      (entry.getValue() != null ? entry.getValue() : "");
        
        if (inTransaction) {
            currentTxn.add(line);
        } else {
            writeLine(line);
        }
    }

    void commit() {
        for (String line : currentTxn) {
            writeLine(line);
        }
        currentTxn.clear();
        inTransaction = false;
    }

    void rollback() {
        currentTxn.clear();  // Discard uncommitted
        inTransaction = false;
    }

    boolean hasUncommittedChanges() {
        return !currentTxn.isEmpty();
    }

    List<WALEntry> getLastCheckpoint() {
        // Return last committed entries
        return readAll();
    }

    List<WALEntry> readAll() {
        List<WALEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length >= 3) {
                    entries.add(new WALEntry(parts[1], 
                        Long.parseLong(parts[2]), 
                        parts.length > 3 ? parts[3] : null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private void writeLine(String line) {
        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("WAL write failed", e);
        }
    }

    void close() throws IOException {
        writer.close();
    }
}

class WALEntry {
    private final String type;
    private final long key;
    private final String value;

    WALEntry(String type, long key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    String getType() { return type; }
    long getKey() { return key; }
    String getValue() { return value; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to support indexes on multiple columns?"
> "B-Tree per column. Or composite B-Tree on (col1, col2). Query planner picks best index."

### Q2: "How to handle joins efficiently?"
> "Hash join for equi-joins. Sort-merge join for range queries. Nested loop for small tables."

### Q3: "How to implement ACID transactions?"
> "Use 2-phase locking. Write locks during transaction. Release at commit. WAL for durability."

### Q4: "How to handle concurrent writes?"
> "Row-level locks. Lock manager tracks lock requests. Deadlock detection via wait-for graph."