# 🔌 Problem 51: Connection Pool (Database Connection Pooling)

> **Difficulty**: ⭐⭐ | **Company Fit**: Any backend company  
> **Est. Time**: 60 min | **Patterns**: Object Pooling, Factory, Singleton

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a connection pool for database connections."

**What the interviewer tests**:
```
1. Can you reuse connections? (Pool pattern)
2. Can you handle concurrent requests? (Thread-safe)
3. Can you validate connections? (Health check)
4. Can you handle exhaustion? (Wait queue, max limit)
```

### Step 2: The "Aha!" Moment

The key insight: **Creating connections is expensive, reuse them.**

```
WITHOUT POOL:
  Request 1: Create connection (100ms) → query (10ms) → close
  Request 2: Create connection (100ms) → query (10ms) → close
  Total: 220ms

WITH POOL (10 connections):
  Request 1: Get from pool (1ms) → query (10ms) → return to pool
  Request 2: Get from pool (1ms) → query (10ms) → return to pool
  Total: 22ms
  
SAVINGS: 10x faster!
```

### Step 3: How to handle pool exhaustion?

```
POOL SIZE: 10 connections
ACTIVE: 10 (all in use)
NEW REQUEST: Wait or timeout

OPTIONS:
  1. Block and wait (default)
  2. Timeout after 30s
  3. Create temporary (overflow)
  4. Reject immediately
```

---

## 💻 Core Implementation

```java
package com.pool;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: ConnectionPool manages database connections.
 * 
 * Reuses connections to avoid creation overhead.
 */
public class ConnectionPool {
    
    private final String url;
    private final String username;
    private final String password;
    private final int maxPoolSize;
    private final long connectionTimeout;
    
    // Available connections
    private final BlockingQueue<PooledConnection> available;
    
    // In-use connections
    private final Set<PooledConnection> inUse;
    
    private final Object lock = new Object();
    private volatile boolean isRunning;

    private ConnectionPool(String url, String username, String password, 
                          int maxPoolSize, long connectionTimeout) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeout = connectionTimeout;
        this.available = new LinkedBlockingQueue<>(maxPoolSize);
        this.inUse = ConcurrentHashMap.newKeySet();
        this.isRunning = true;
        
        // Initialize pool
        initializePool();
    }

    /**
     * Singleton pattern.
     */
    private static volatile ConnectionPool instance;
    
    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool(
                        "jdbc:mysql://localhost:3306/mydb",
                        "user",
                        "password",
                        10,
                        30000
                    );
                }
            }
        }
        return instance;
    }

    /**
     * INTUITION: Get connection from pool.
     * 
     * 1. Check if available
     * 2. If not, wait up to timeout
     * 3. Validate connection
     * 4. Mark as in-use
     */
    public synchronized Connection getConnection() throws SQLException {
        if (!isRunning) {
            throw new SQLException("Pool is shutdown");
        }
        
        PooledConnection pooledConn = null;
        long startTime = System.currentTimeMillis();
        
        while (pooledConn == null) {
            // Try to get available connection
            pooledConn = available.poll();
            
            if (pooledConn == null) {
                // Check if we can create new connection
                if (getTotalConnections() < maxPoolSize) {
                    pooledConn = createConnection();
                } else {
                    // Wait for connection to be returned
                    long waitTime = connectionTimeout - (System.currentTimeMillis() - startTime);
                    if (waitTime <= 0) {
                        throw new SQLException("Connection timeout: pool exhausted");
                    }
                    
                    try {
                        pooledConn = available.poll(waitTime, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("Interrupted while waiting for connection");
                    }
                }
            }
            
            // Validate connection
            if (pooledConn != null && !isValid(pooledConn)) {
                // Invalid connection, discard and try again
                closeConnection(pooledConn);
                pooledConn = null;
            }
        }
        
        inUse.add(pooledConn);
        return pooledConn.getConnection();
    }

    /**
     * INTUITION: Return connection to pool.
     */
    public synchronized void releaseConnection(Connection conn) {
        if (conn == null) return;
        
        // Find pooled connection
        PooledConnection pooledConn = null;
        for (PooledConnection pc : inUse) {
            if (pc.getConnection() == conn) {
                pooledConn = pc;
                break;
            }
        }
        
        if (pooledConn != null) {
            inUse.remove(pooledConn);
            
            if (isRunning) {
                available.offer(pooledConn);
            } else {
                closeConnection(pooledConn);
            }
        }
    }

    /**
     * INTUITION: Create new physical connection.
     */
    private PooledConnection createConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            PooledConnection pooled = new PooledConnection(conn);
            return pooled;
        } catch (SQLException e) {
            throw new SQLException("Failed to create connection", e);
        }
    }

    /**
     * INTUITION: Validate connection is still alive.
     */
    private boolean isValid(PooledConnection pooledConn) {
        try {
            Connection conn = pooledConn.getConnection();
            return conn != null && conn.isValid(2);  // 2 second timeout
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Close physical connection.
     */
    private void closeConnection(PooledConnection pooledConn) {
        try {
            Connection conn = pooledConn.getConnection();
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Get total connections (available + in-use).
     */
    private int getTotalConnections() {
        return available.size() + inUse.size();
    }

    /**
     * Initialize pool with minimum connections.
     */
    private void initializePool() {
        int minConnections = Math.min(5, maxPoolSize);
        
        for (int i = 0; i < minConnections; i++) {
            try {
                PooledConnection conn = createConnection();
                available.offer(conn);
            } catch (SQLException e) {
                System.err.println("Failed to initialize connection pool: " + e.getMessage());
            }
        }
    }

    /**
     * Shutdown pool and close all connections.
     */
    public synchronized void shutdown() {
        isRunning = false;
        
        // Close available connections
        for (PooledConnection conn : available) {
            closeConnection(conn);
        }
        available.clear();
        
        // Close in-use connections
        for (PooledConnection conn : inUse) {
            closeConnection(conn);
        }
        inUse.clear();
    }

    // --- Getters ---

    public int getAvailableConnections() { return available.size(); }
    public int getInUseConnections() { return inUse.size(); }
    public int getMaxPoolSize() { return maxPoolSize; }
}

/**
 * Wrapper for pooled connection.
 */
class PooledConnection {
    private final Connection connection;
    private long lastUsed;

    PooledConnection(Connection connection) {
        this.connection = connection;
        this.lastUsed = System.currentTimeMillis();
    }

    Connection getConnection() { return connection; }
    long getLastUsed() { return lastUsed; }
    void setLastUsed(long lastUsed) { this.lastUsed = lastUsed; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle stale connections?"
> "Validation query: SELECT 1. Test before use. Remove if invalid. Configurable test interval."

### Q2: "How to handle connection leaks?"
> "Track connection borrow time. Warn if held > 5 min. Force close on timeout. Stack trace logging."

### Q3: "How to scale for high concurrency?"
> "Multiple pools per shard. Partition by user ID. Dynamic sizing based on load."

### Q4: "How to handle failover?"
> "Retry with backoff. Switch to replica on failure. Circuit breaker pattern."