# 🚌 Problem 64: Event Bus (Distributed Events)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Microservices companies  
> **Est. Time**: 90 min | **Patterns**: Observer, Pub-Sub, Event-Driven

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design an event bus for decoupled microservices communication."

**What the interviewer tests**:
```
1. Can you decouple services? (Event-driven)
2. Can you handle multiple subscribers? (Fan-out)
3. Can you ensure delivery? (At-least-once)
4. Can you handle ordering? (Per entity)
```

### Step 2: The "Aha!" Moment

The key insight: **Event bus = pub-sub with durable storage.**

```
SERVICE A: Order created (emit event)
   ↓
EVENT BUS: {type: ORDER_CREATED, data: {...}}
   ↓
SERVICE B: Send email (subscribe to ORDER_CREATED)
SERVICE C: Update analytics (subscribe to ORDER_CREATED)
SERVICE D: Reserve inventory (subscribe to ORDER_CREATED)

Benefits:
  - Loose coupling
  - Scalability (add subscribers without changing producer)
  - Resilience (retry failed consumers)
```

### Step 3: How to ensure delivery?

```
DELIVERY GUARANTEES:
1. At-most-once: Fire and forget (may lose)
2. At-least-once: Store + retry (may duplicate)
3. Exactly-once: Idempotent consumers + dedup

IMPLEMENTATION:
  - Store events in DB
  - Mark as PENDING → SENT → ACKED
  - Retry PENDING events
```

---

## 💻 Core Implementation

```java
package com.eventbus;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: EventBus publishes events to subscribers.
 */
public class EventBus {
    
    private final Map<String, List<Subscriber>> subscribers;
    private final BlockingQueue<Event> eventQueue;
    private final ExecutorService executor;
    private volatile boolean isRunning;

    public EventBus(int workerCount) {
        this.subscribers = new ConcurrentHashMap<>();
        this.eventQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newFixedThreadPool(workerCount);
        this.isRunning = true;
        
        // Start dispatcher
        startDispatcher();
    }

    /**
     * INTUITION: Subscribe to event type.
     */
    public void subscribe(String eventType, Subscriber subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                   .add(subscriber);
    }

    /**
     * INTUITION: Publish event.
     */
    public void publish(Event event) {
        eventQueue.offer(event);
    }

    /**
     * Dispatch events to subscribers.
     */
    private void startDispatcher() {
        int dispatcherCount = 5;
        
        for (int i = 0; i < dispatcherCount; i++) {
            executor.submit(() -> {
                while (isRunning) {
                    try {
                        Event event = eventQueue.poll(1, TimeUnit.SECONDS);
                        if (event != null) {
                            dispatch(event);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    /**
     * Dispatch event to all subscribers.
     */
    private void dispatch(Event event) {
        List<Subscriber> eventSubscribers = subscribers.get(event.getType());
        if (eventSubscribers == null) return;
        
        for (Subscriber subscriber : eventSubscribers) {
            executor.submit(() -> {
                try {
                    subscriber.onEvent(event);
                } catch (Exception e) {
                    System.err.println("Subscriber " + subscriber.getName() + 
                                     " failed: " + e.getMessage());
                    // Retry logic
                    retry(event, subscriber);
                }
            });
        }
    }

    private void retry(Event event, Subscriber subscriber) {
        // Simplified: retry once
        try {
            subscriber.onEvent(event);
        } catch (Exception e) {
            System.err.println("Retry failed for " + subscriber.getName());
        }
    }

    public void shutdown() {
        isRunning = false;
        executor.shutdown();
    }
}

/**
 * Event.
 */
class Event {
    private final String id;
    private final String type;
    private final Map<String, Object> data;
    private final long timestamp;

    Event(String id, String type, Map<String, Object> data) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public Map<String, Object> getData() { return data; }
}

/**
 * Subscriber interface.
 */
interface Subscriber {
    void onEvent(Event event);
    String getName();
}

/**
 * Example: Email subscriber.
 */
class EmailSubscriber implements Subscriber {
    @Override
    public void onEvent(Event event) {
        if ("ORDER_CREATED".equals(event.getType())) {
            System.out.println("Sending order confirmation email");
        }
    }

    @Override
    public String getName() { return "EmailSubscriber"; }
}

/**
 * Example: Analytics subscriber.
 */
class AnalyticsSubscriber implements Subscriber {
    @Override
    public void onEvent(Event event) {
        System.out.println("Tracking analytics: " + event.getType());
    }

    @Override
    public String getName() { return "AnalyticsSubscriber"; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle event ordering?"
> "Partition by entity ID. Single consumer per partition. Sequence numbers."

### Q2: "How to handle event schema changes?"
> "Schema registry. Version events. Backward compatibility."

### Q3: "How to handle dead letters?"
> "DLQ after N retries. Manual replay. Alert ops team."

### Q4: "How to trace events across services?"
> "Correlation ID in headers. Distributed tracing (OpenTelemetry)."