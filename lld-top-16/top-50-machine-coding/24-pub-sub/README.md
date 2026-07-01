# 📡 Problem 24: Pub-Sub (Publish-Subscribe) System

> **Difficulty**: ⭐⭐ | **Company Fit**: Any company (Kafka, RabbitMQ, AWS SQS)  
> **Est. Time**: 90 min | **Patterns**: Observer, Message Broker, Topic-based Routing

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a system where publishers send messages and subscribers receive them."

**What the interviewer tests**:
```
1. Can you decouple producers from consumers? (Publishers don't know subscribers)
2. Can you handle multiple topics/channels?
3. Can you deliver messages reliably? (At-least-once, At-most-once, Exactly-once)
4. Can you handle slow consumers? (Buffer overflow, backpressure)
```

### Step 2: The "Aha!" Moment

The key insight: **A topic is a mailbox. Subscribers pull from it.**

```
Publisher → [Topic: "orders"] → Message Queue → Subscriber A
                                                → Subscriber B
                                                → Subscriber C

Each subscriber has its own OFFSET (position in the queue).
Slow subscriber A reads at position 100.
Fast subscriber B reads at position 500.
They don't block each other.

When a new message arrives:
  - It's appended ONCE to the topic
  - Each subscriber advances their own offset when they read
```

This is the **push-based fan-out** model (like Kafka).

### Step 3: Delivery guarantees

```
AT-LEAST-ONCE (Most common):
  Message delivered ≥ 1 time. Possible duplicates.
  Used when missing a message is worse than processing twice.
  Example: Order processing, payment events.

AT-MOST-ONCE:
  Message delivered ≤ 1 time. Possible loss.
  Used when duplicates are worse than missing a message.
  Example: Metrics, logging.

EXACTLY-ONCE (Hard):
  Message delivered exactly 1 time.
  Requires: deduplication + transactional writes + consumer acknowledgment.
  Example: Financial transactions.
```

---

## 💻 Core Implementation

```java
package com.pubsub;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * INTUITION: PubSubService is the message broker.
 * 
 * Thread-safety: Multiple publishers AND subscribers accessing
 * topics simultaneously. Use ConcurrentHashMap + CopyOnWriteArrayList.
 */
public class PubSubService {
    
    // All topics: topicName → Topic
    private final Map<String, Topic> topics = new ConcurrentHashMap<>();
    
    // All subscriptions: subscriberId → List of subscriptions
    private final Map<String, List<Subscription>> subscriptions = new ConcurrentHashMap<>();
    
    // Message ID generator
    private final AtomicLong messageIdGenerator = new AtomicLong(1);
    
    // In production: Persistent storage (Kafka log, database)
    // For demo: in-memory

    /**
     * INTUITION: Create a topic if it doesn't exist.
     * Topics are persistent until explicitly deleted.
     */
    public Topic createTopic(String name, int retentionDays) {
        return topics.computeIfAbsent(name, 
            k -> new Topic(name, retentionDays));
    }

    /**
     * INTUITION: Publish a message to a topic.
     * 
     * 1. Get the topic (create if doesn't exist)
     * 2. Assign a unique message ID
     * 3. Append message to topic's message list
     * 4. Notify ALL active subscribers (push OR let them pull)
     * 
     * @param topicName Where to publish
     * @param message The message content
     * @return The published message with ID
     */
    public Message publish(String topicName, String message) {
        Topic topic = topics.computeIfAbsent(topicName, 
            k -> new Topic(topicName, 7));
        
        // Assign unique ID
        long messageId = messageIdGenerator.getAndIncrement();
        
        // Create message
        Message msg = new Message(messageId, message, System.currentTimeMillis());
        
        // Append to topic
        topic.addMessage(msg);
        
        // Notify subscribers (push model)
        notifySubscribers(topicName, msg);
        
        return msg;
    }

    /**
     * INTUITION: Subscribe to a topic.
     * 
     * Creates a new subscription with offset = 0 (start from beginning).
     * Or offset = -1 (start from latest).
     */
    public Subscription subscribe(String subscriberId, String topicName, 
                                   Subscription.StartFrom startFrom) {
        Topic topic = topics.computeIfAbsent(topicName, 
            k -> new Topic(topicName, 7));
        
        Subscription subscription = new Subscription(
            subscriberId, topic, startFrom
        );
        
        subscriptions.computeIfAbsent(subscriberId, k -> new CopyOnWriteArrayList<>())
                     .add(subscription);
        
        topic.addSubscription(subscription);
        
        return subscription;
    }

    /**
     * INTUITION: Pull messages from a subscription.
     * 
     * Subscriber actively requests messages from their offset.
     * This is the Kafka-style pull model.
     * 
     * @param subscription The subscription to pull from
     * @param maxMessages Max messages to return (batch size)
     * @return List of messages
     */
    public List<Message> pull(Subscription subscription, int maxMessages) {
        return subscription.getMessages(maxMessages);
    }

    /**
     * INTUITION: Acknowledge a message (mark as processed).
     * Only used for at-least-once delivery.
     * Advances subscriber's offset past this message.
     */
    public void acknowledge(Subscription subscription, long messageId) {
        subscription.acknowledge(messageId);
    }

    private void notifySubscribers(String topicName, Message message) {
        Topic topic = topics.get(topicName);
        if (topic == null) return;
        
        // Push message to all subscribers
        for (Subscription sub : topic.getSubscriptions()) {
            sub.onMessage(message);
        }
    }
}
```

```java
package com.pubsub;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * INTUITION: A Topic is a message log (like Kafka).
 * 
 * Messages are APPEND-ONLY. Once written, they're immutable.
 * Consumers read at their own pace from their own offset.
 * 
 * Think of it as a never-ending file:
 * [msg1] [msg2] [msg3] [msg4] [msg5] ...
 *          ↑                      ↑
 *     Subscriber A at pos 2  Subscriber B at pos 5
 * 
 * Subscribers don't interfere with each other.
 */
class Topic {
    private final String name;
    private final int retentionDays;
    private final List<Message> messages = new CopyOnWriteArrayList<>();
    private final List<Subscription> subscriptions = new CopyOnWriteArrayList<>();

    Topic(String name, int retentionDays) {
        this.name = name;
        this.retentionDays = retentionDays;
    }

    public synchronized void addMessage(Message message) {
        messages.add(message);
        
        // In production: Write to disk (WAL - Write Ahead Log)
        // wal.append(message);
    }

    public List<Message> getMessages(long fromOffset, int maxMessages) {
        int start = (int) fromOffset;
        int end = Math.min(start + maxMessages, messages.size());
        
        if (start >= messages.size()) {
            return Collections.emptyList();
        }
        
        return messages.subList(start, end);
    }

    public synchronized void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public List<Subscription> getSubscriptions() {
        return Collections.unmodifiableList(subscriptions);
    }

    public String getName() { return name; }
    public int getMessageCount() { return messages.size(); }
}
```

```java
package com.pubsub;

import java.util.*;

/**
 * INTUITION: A Subscription represents one subscriber's position in a topic.
 * 
 * Each subscriber has their own offset (position in the message log).
 * Two subscribers to the same topic DON'T share offsets.
 * 
 * State:
 * - offset: Current read position
 * - ackedUpTo: Last acknowledged message (for at-least-once)
 * - pending: Messages sent but not yet acknowledged
 */
public class Subscription {
    
    public enum StartFrom { BEGINNING, LATEST }

    private final String subscriberId;
    private final Topic topic;
    private final StartFrom startFrom;
    
    // Current position in message log
    private long offset;
    
    // Messages sent but not yet acknowledged
    private final Set<Long> pendingMessages = ConcurrentHashMap.newKeySet();
    
    // Delivery guarantee: AT_LEAST_ONCE, AT_MOST_ONCE, EXACTLY_ONCE
    private DeliveryGuarantee guarantee;

    public Subscription(String subscriberId, Topic topic, StartFrom startFrom) {
        this.subscriberId = subscriberId;
        this.topic = topic;
        this.startFrom = startFrom;
        this.guarantee = DeliveryGuarantee.AT_LEAST_ONCE;
        
        // Set initial offset
        if (startFrom == StartFrom.BEGINNING) {
            this.offset = 0;
        } else {
            this.offset = topic.getMessageCount();  // Start at end
        }
    }

    /**
     * INTUITION: Get messages starting from current offset.
     * 
     * Pull model: subscriber actively requests.
     * Returns up to maxMessages, advances offset.
     * 
     * @param maxMessages Max batch size
     * @return List of messages
     */
    public List<Message> getMessages(int maxMessages) {
        List<Message> msgs = topic.getMessages(offset, maxMessages);
        
        // Track pending (for at-least-once)
        if (guarantee == DeliveryGuarantee.AT_LEAST_ONCE) {
            for (Message msg : msgs) {
                pendingMessages.add(msg.getId());
            }
        }
        
        // Advance offset (optimistic - will rollback if nack)
        offset += msgs.size();
        
        return msgs;
    }

    /**
     * INTUITION: Acknowledge message as processed.
     * 
     * Removes from pending set.
     * For exactly-once, also commits offset to durable store.
     */
    public void acknowledge(long messageId) {
        pendingMessages.remove(messageId);
        
        // In production: Commit offset to Kafka/Zookeeper
        // This enables recovery after crash
    }

    /**
     * INTUITION: Notify subscriber of new message (push model).
     * 
     * In a real system, this would be:
     * - WebSocket push to browser
     * - HTTP callback to webhook
     * - Message placed in consumer's mailbox
     */
    public void onMessage(Message message) {
        // Push notification - in real system, send to consumer
        System.out.println("Push to " + subscriberId + ": " + message.getContent());
        
        if (guarantee == DeliveryGuarantee.AT_MOST_ONCE) {
            // Don't track - if consumer misses it, too bad
        } else if (guarantee == DeliveryGuarantee.AT_LEAST_ONCE) {
            // Track for potential redelivery
            pendingMessages.add(message.getId());
        }
    }

    // Getters
    public String getSubscriberId() { return subscriberId; }
    public Topic getTopic() { return topic; }
    public long getOffset() { return offset; }
}
```

```java
package com.pubsub;

import java.time.LocalDateTime;

/**
 * INTUITION: Message is immutable once created.
 * 
 * Contains:
 * - ID: unique identifier
 * - Content: the actual message payload
 * - Timestamp: when it was published
 * - Headers: metadata (content-type, correlation-id, etc.)
 */
public class Message {
    private final long id;
    private final String content;
    private final long timestamp;
    private final Map<String, String> headers;

    public Message(long id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    // Getters
    public long getId() { return id; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public Map<String, String> getHeaders() { return headers; }
}

enum DeliveryGuarantee {
    AT_LEAST_ONCE,   // May deliver duplicates
    AT_MOST_ONCE,    // May lose messages
    EXACTLY_ONCE     // Delivered exactly once
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle slow consumers?"
> "Backpressure: if consumer's queue > 1000 messages, pause publishing to that consumer. Or use pull model: consumer reads at own pace. Monitor consumer lag."

### Q2: "How to support message replay?"
> "Store messages in persistent log (Kafka). Subscribers can reset offset to any point in time. Retention policy: keep 7 days by default."

### Q3: "How to filter messages per subscriber?"
> "Server-side filtering: subscriber specifies predicate on subscribe. Only matching messages are delivered. Reduces network traffic."

### Q4: "How to ensure ordering?"
> "Single-partition topics guarantee FIFO order. For multi-partition, use message key to ensure same key goes to same partition."

### Q5: "How to handle Poison messages (always fail processing)?"
> "Dead Letter Queue (DLQ). After 3 retry attempts, move to DLQ. Alert ops team. Manual review and replay."