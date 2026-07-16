# 📨 Problem 63: Message Queue (Kafka-like)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any distributed systems company  
> **Est. Time**: 90 min | **Patterns**: Producer-Consumer, Observer, Pub-Sub

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a message queue for async communication."

**What the interviewer tests**:
```
1. Can you decouple producers/consumers? (Async)
2. Can you persist messages? (Disk storage)
3. Can you handle multiple consumers? (Fan-out)
4. Can you handle failures? (ACK, replay)
```

### Step 2: The "Aha!" Moment

The key insight: **Message queue = persistent log with consumer offsets.**

```
PRODUCER: Send message
  ↓
TOPIC (partitioned log)
  ↓
CONSUMER: Read at own pace
  
BENEFITS:
  - Buffering: Handle traffic spikes
  - Decoupling: Producers don't wait
  - Replay: Re-read messages
  - Fan-out: Multiple consumers
```

### Step 3: Partitioning strategy?

```
PARTITIONING:
  - Round-robin
  - Key-based (hash)
  - Custom partitioner
  
  key: user123 → partition(user123) → partition 2
  
  Guarantees order per partition
```

---

## 💻 Core Implementation

```java
package com.mq;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;

/**
 * INTUITION: MessageQueue stores messages for consumers.
 */
public class MessageQueue {
    
    private final Map<String, Topic> topics;
    private final Map<String, ConsumerGroup> consumerGroups;

    public MessageQueue() {
        this.topics = new ConcurrentHashMap<>();
        this.consumerGroups = new ConcurrentHashMap<>();
    }

    /**
     * Create topic.
     */
    public void createTopic(String topicName, int partitions) {
        Topic topic = new Topic(topicName, partitions);
        topics.put(topicName, topic);
    }

    /**
     * INTUITION: Produce message to topic.
     */
    public void produce(String topicName, Message message) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicName);
        }
        
        topic.append(message);
    }

    /**
     * INTUITION: Consume messages from topic.
     */
    public List<Message> consume(String topicName, String consumerId, int count) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicName);
        }
        
        return topic.read(consumerId, count);
    }

    /**
     * Commit offset (acknowledge).
     */
    public void commitOffset(String topicName, String consumerId) {
        Topic topic = topics.get(topicName);
        if (topic != null) {
            topic.commitOffset(consumerId);
        }
    }
}

/**
 * Topic with partitions.
 */
class Topic {
    private final String topicName;
    private final int partitionCount;
    private final List<Partition> partitions;
    private final Map<String, Long> consumerOffsets;

    Topic(String topicName, int partitionCount) {
        this.topicName = topicName;
        this.partitionCount = partitionCount;
        this.partitions = new CopyOnWriteArrayList<>();
        this.consumerOffsets = new ConcurrentHashMap<>();
        
        // Create partitions
        for (int i = 0; i < partitionCount; i++) {
            partitions.add(new Partition(i));
        }
    }

    void append(Message message) {
        // Simple round-robin partitioning
        int partitionId = (int) (message.getId() % partitionCount);
        Partition partition = partitions.get(partitionId);
        partition.append(message);
    }

    List<Message> read(String consumerId, int count) {
        // Simplified: read from first partition
        Partition partition = partitions.get(0);
        long offset = consumerOffsets.getOrDefault(consumerId, 0L);
        return partition.read(offset, count);
    }

    void commitOffset(String consumerId) {
        // Simplified: commit offset from first partition
        Partition partition = partitions.get(0);
        long offset = partition.getNextOffset();
        consumerOffsets.put(consumerId, offset);
    }

    long getNextOffset() {
        return partitions.get(0).getNextOffset();
    }
}

/**
 * Partition (immutable log).
 */
class Partition {
    private final int partitionId;
    private final List<Message> messages;
    private long nextOffset;

    Partition(int partitionId) {
        this.partitionId = partitionId;
        this.messages = new CopyOnWriteArrayList<>();
        this.nextOffset = 0;
    }

    void append(Message message) {
        messages.add(message);
        nextOffset++;
    }

    List<Message> read(long offset, int count) {
        List<Message> result = new ArrayList<>();
        
        for (int i = (int) offset; i < Math.min(offset + count, messages.size()); i++) {
            result.add(messages.get(i));
        }
        
        return result;
    }

    public long getNextOffset() { return nextOffset; }
}

/**
 * Message.
 */
class Message {
    private final String id;
    private final String key;
    private final String payload;
    private final long timestamp;

    Message(String id, String key, String payload) {
        this.id = id;
        this.key = key;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getPayload() { return payload; }
}

/**
 * Consumer group.
 */
class ConsumerGroup {
    private final String groupId;
    private final List<Consumer> consumers;

    ConsumerGroup(String groupId) {
        this.groupId = groupId;
        this.consumers = new CopyOnWriteArrayList<>();
    }

    void addConsumer(Consumer consumer) {
        consumers.add(consumer);
    }
}

class Consumer {
    private final String consumerId;
    private String topicName;

    Consumer(String consumerId) {
        this.consumerId = consumerId;
    }

    public void subscribe(String topic) {
        this.topicName = topic;
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to ensure message delivery?"
> "At-least-once delivery. Message ACK from consumer. Retry on failure."

### Q2: "How to handle message ordering?"
> "Ordering per partition. Same key → same partition. FIFO within partition."

### Q3: "How to scale consumer throughput?"
> "More consumers in group. Multiple partitions. Parallel reading."

### Q4: "How to handle dead letters?"
> "DLQ after N retries. Manual replay. Alert and monitor."