# 📊 Problem 68: Log Aggregator (Like ELK Stack)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any company with microservices  
> **Est. Time**: 90 min | **Patterns**: Producer-Consumer, Observer, Indexing

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a centralized log aggregation system."

**What the interviewer tests**:
```
1. Can you collect logs? (From multiple services)
2. Can you index logs? (Fast search)
3. Can you handle scale? (GBs per minute)
4. Can you alert on errors? (Real-time monitoring)
```

### Step 2: The "Aha!" Moment

The key insight: **Log aggregator = distributed queue + inverted index.**

```
WITHOUT AGGREGATION:
  Service A: logs in /var/log/a.log
  Service B: logs in /var/log/b.log
  Service C: logs in /var/log/c.log
  
  Debugging: SSH into each server
  
WITH AGGREGATION:
  All services → Log Aggregator
   ↓
  Centralized search: "ERROR user123"
   ↓
  Results: All errors across all services
```

### Step 3: How to handle scale?

```
INGESTION:
  - Fluentd/Filebeat on each server
  - Send to Kafka (buffer)
  - Indexer reads from Kafka
  - Store in Elasticsearch
  
SEARCH:
  - Inverted index (word → documents)
  - Full-text search
  - Time-based partitioning
```

---

## 💻 Core Implementation

```java
package com.logs;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: LogAggregator collects and indexes logs.
 */
public class LogAggregator {
    
    private final BlockingQueue<LogEvent> logQueue;
    private final Map<String, InvertedIndex> indexes;
    private final ExecutorService executor;
    private volatile boolean isRunning;

    public LogAggregator(int workerCount) {
        this.logQueue = new LinkedBlockingQueue<>(10000);
        this.indexes = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(workerCount);
        this.isRunning = true;
        
        // Start indexer workers
        for (int i = 0; i < workerCount; i++) {
            executor.submit(this::indexWorker);
        }
    }

    /**
     * INTUITION: Ingest log event.
     */
    public void ingest(LogEvent event) {
        try {
            logQueue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Index worker: processes log queue.
     */
    private void indexWorker() {
        while (isRunning) {
            try {
                LogEvent event = logQueue.poll(1, TimeUnit.SECONDS);
                if (event != null) {
                    index(event);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Index log event.
     */
    private void index(LogEvent event) {
        String service = event.getService();
        String message = event.getMessage();
        
        // Tokenize message
        String[] tokens = message.toLowerCase().split("\\s+");
        
        // Add to inverted index
        for (String token : tokens) {
            indexes.computeIfAbsent(token, k -> new InvertedIndex())
                   .add(event);
        }
    }

    /**
     * Search logs.
     */
    public List<LogEvent> search(String query, long startTime, long endTime) {
        String[] tokens = query.toLowerCase().split("\\s+");
        
        Set<LogEvent> results = null;
        
        for (String token : tokens) {
            InvertedIndex index = indexes.get(token);
            if (index == null) continue;
            
            Set<LogEvent> events = index.getEvents(startTime, endTime);
            
            if (results == null) {
                results = new HashSet<>(events);
            } else {
                results.retainAll(events);  // Intersection
            }
        }
        
        return results != null ? new ArrayList<>(results) : new ArrayList<>();
    }

    public void shutdown() {
        isRunning = false;
        executor.shutdown();
    }
}

/**
 * Log event.
 */
class LogEvent {
    private final String id;
    private final String service;
    private final String level;
    private final String message;
    private final long timestamp;
    private final Map<String, String> metadata;

    LogEvent(String id, String service, String level, String message) {
        this.id = id;
        this.service = service;
        this.level = level;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }

    public String getId() { return id; }
    public String getService() { return service; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}

/**
 * Inverted index for fast search.
 */
class InvertedIndex {
    private final Map<Long, LogEvent> events;  // eventId → event

    InvertedIndex() {
        this.events = new ConcurrentHashMap<>();
    }

    void add(LogEvent event) {
        events.put(event.getId().hashCode() * 1000000L + 
                   event.getTimestamp() / 1000, event);
    }

    Set<LogEvent> getEvents(long startTime, long endTime) {
        Set<LogEvent> result = new HashSet<>();
        
        for (LogEvent event : events.values()) {
            if (event.getTimestamp() >= startTime && 
                event.getTimestamp() <= endTime) {
                result.add(event);
            }
        }
        
        return result;
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle high cardinality?"
> "Cardinality limits. Sampling for metrics. Pre-aggregation."

### Q2: "How to handle retention?"
> "Hot/Warm/Cold tiers. Roll up old logs. Delete after 30 days."

### Q3: "How to alert on errors?"
> "Real-time query. Threshold alerting. PagerDuty integration."

### Q4: "How to trace distributed requests?"
> "Correlation IDs. Trace context propagation. OpenTelemetry."