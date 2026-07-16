# 🚶 Problem 34: Queue Management System (Like Queue Management)

> **Difficulty**: ⭐⭐ | **Company Fit**: QLess, Q-MATIC, banks, hospitals  
> **Est. Time**: 60 min | **Patterns**: Queue, Observer, State

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Manage queues for service centers."

**What the interviewer tests**:
```
1. Can you model waiting lines? (FIFO queues)
2. Can you handle priorities? (VIP, premium, regular)
3. Can you estimate wait times? (Dynamic)
4. Can you notify users? (Your turn, almost there)
```

### Step 2: The "Aha!" Moment

The key insight: **Virtual queue, not physical line.**

```
OLD WAY:
  Customer arrives → takes physical ticket #45
  Waits in crowded room
  Screens shows: "Now serving #38"
  Customer checks every 5 minutes

NEW WAY (virtual queue):
  Customer joins via app → gets ticket #45
  Goes anywhere (coffee, car)
  Gets push notification when #40 is called (5 min warning)
  Gets SMS when #45 is called
  Can check estimated wait time: "10 minutes"

Queue state:
  [38, 39, 40, 41, 42, 43, 44, 45, 46, 47]
   ↑                                    ↑
 Serving                    You (position 8)
```

### Step 3: How to estimate wait time?

```
Average service time × people ahead of you

If:
  - Average service: 5 minutes
  - You're position 8
  - Currently serving: 38
  
Then: 8 - 38 = -30... wait that's wrong.

Actually:
  - 8 people between you and current
  - 8 × 5 min = 40 minutes
```

---

## 💻 Core Implementation

```java
package com.queue;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: QueueService manages service queues.
 * 
 * Maintains:
 * - FIFO queue for regular customers
 * - Priority queues for VIP/premium
 * - Real-time position tracking
 * - Estimated wait calculation
 */
public class QueueService {
    
    // Queue per service counter
    private final Map<String, ServiceQueue> queues;
    
    // Users in queue: userId → QueueTicket
    private final Map<String, QueueTicket> activeTickets;
    
    // Notification service
    private final NotificationSystem notifications;

    public QueueService() {
        this.queues = new ConcurrentHashMap<>();
        this.activeTickets = new ConcurrentHashMap<>();
        this.notifications = new NotificationSystem();
    }

    /**
     * INTUITION: Customer joins a queue.
     * 
     * 1. Determine queue type (regular, VIP)
     * 2. Get next ticket number
     * 3. Calculate estimated wait
     * 4. Return ticket with position
     */
    public synchronized QueueTicket joinQueue(String userId, String serviceId, 
                                              CustomerType type) {
        
        ServiceQueue queue = queues.computeIfAbsent(serviceId, 
            k -> new ServiceQueue(serviceId));
        
        // Generate ticket
        int ticketNumber = queue.getNextTicket(type);
        
        QueueTicket ticket = new QueueTicket(
            userId, serviceId, ticketNumber, type, queue.getEstimatedWait(type)
        );
        
        activeTickets.put(userId, ticket);
        
        // Send notification
        notifications.sendWelcome(userId, ticket);
        
        // Schedule notifications
        scheduleNotifications(userId, ticket, queue);
        
        return ticket;
    }

    /**
     * INTUITION: Call next customer.
     * 
     * Called when counter is free.
     * 1. Check VIP queue first
     * 2. Then premium
     * 3. Then regular
     */
    public synchronized Optional<QueueTicket> callNext(String serviceId) {
        ServiceQueue queue = queues.get(serviceId);
        if (queue == null) return Optional.empty();
        
        QueueTicket ticket = queue.dequeue();
        if (ticket != null) {
            ticket.setStatus(TicketStatus.SERVING);
            
            // Notify user
            notifications.sendYourTurn(ticket.getUserId(), ticket);
            
            // Notify user 2 positions ahead
            notifyUpcoming(ticket);
        }
        
        return Optional.ofNullable(ticket);
    }

    /**
     * INTUITION: Skip current customer (no-show).
     */
    public synchronized void skipCurrent(String serviceId) {
        ServiceQueue queue = queues.get(serviceId);
        if (queue != null) {
            QueueTicket skipped = queue.peek();
            if (skipped != null) {
                skipped.setStatus(TicketStatus.SKIPPED);
                queue.dequeue();  // Remove
                notifications.sendSkipped(skipped.getUserId(), skipped);
            }
        }
    }

    /**
     * INTUITION: Get current position.
     */
    public int getPosition(String userId) {
        QueueTicket ticket = activeTickets.get(userId);
        if (ticket == null || ticket.getStatus() == TicketStatus.SERVING) {
            return -1;  // Being served
        }
        
        ServiceQueue queue = queues.get(ticket.getServiceId());
        return queue.getPosition(ticket);
    }

    /**
     * INTUITION: Cancel ticket (leave queue).
     */
    public synchronized void cancelTicket(String userId) {
        QueueTicket ticket = activeTickets.remove(userId);
        if (ticket != null && ticket.getStatus() == TicketStatus.WAITING) {
            ServiceQueue queue = queues.get(ticket.getServiceId());
            if (queue != null) {
                queue.removeTicket(ticket);
                notifications.sendCancelled(userId, ticket);
            }
        }
    }

    /**
     * Schedule notifications for user.
     * - 10 min warning: "Your turn in 10 min"
     * - 5 min warning: "Your turn in 5 min"
     * - 1 min warning: "Your turn in 1 min"
     */
    private void scheduleNotifications(String userId, QueueTicket ticket, 
                                       ServiceQueue queue) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        // Calculate delays based on wait time
        long waitMs = ticket.getEstimatedWait() * 60_000L;  // Convert min to ms
        
        // 1 min before
        scheduler.schedule(() -> 
            notifications.sendAlmostThere(userId, ticket), 
            Math.max(0, waitMs - 60_000), TimeUnit.MILLISECONDS);
    }

    private void notifyUpcoming(QueueTicket current) {
        // Find next ticket in queue
        QueueTicket next = getNextInQueue(current.getServiceId());
        if (next != null) {
            notifications.sendUpcoming(next.getUserId(), current, next);
        }
    }
}
```

```java
package com.queue;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * INTUITION: ServiceQueue manages one service counter's queue.
 * 
 * Uses multiple PriorityQueues per customer type:
 * - VIP: Priority 1 (highest)
 * - Premium: Priority 2
 * - Regular: Priority 3
 * 
 * Dequeue checks VIP first, then premium, then regular.
 */
class ServiceQueue {
    private final String serviceId;
    private final AtomicInteger ticketCounter = new AtomicInteger(0);
    
    // Separate queues per customer type
    private final Map<CustomerType, PriorityBlockingQueue<QueueTicket>> queues;
    
    // Average service time (in minutes)
    private int avgServiceTime = 5;

    ServiceQueue(String serviceId) {
        this.serviceId = serviceId;
        this.queues = new HashMap<>();
        for (CustomerType type : CustomerType.values()) {
            queues.put(type, new PriorityBlockingQueue<>(
                Comparator.comparingInt(QueueTicket::getTicketNumber)));
        }
    }

    /**
     * INTUITION: Get next ticket number.
     * 
     * Ticket numbers are unique across all types.
     * Example: VIP-001, VIP-002, REG-003, PRE-004, REG-005
     */
    int getNextTicket(CustomerType type) {
        return ticketCounter.incrementAndGet();
    }

    /**
     * INTUITION: Estimate wait time.
     * 
     * Wait = (number ahead) × avgServiceTime
     */
    long getEstimatedWait(CustomerType type) {
        PriorityBlockingQueue<QueueTicket> queue = queues.get(type);
        int ahead = queue.size();
        return ahead * avgServiceTime;
    }

    /**
     * INTUITION: Enqueue a ticket.
     */
    void enqueue(QueueTicket ticket) {
        PriorityBlockingQueue<QueueTicket> queue = 
            queues.computeIfAbsent(ticket.getType(), 
                k -> new PriorityBlockingQueue<>());
        queue.offer(ticket);
    }

    /**
     * INTUITION: Dequeue next customer.
     * 
     * Priority order: VIP > Premium > Regular
     */
    QueueTicket dequeue() {
        // Try VIP first
        PriorityBlockingQueue<QueueTicket> vipQueue = queues.get(CustomerType.VIP);
        QueueTicket ticket = vipQueue.poll();
        if (ticket != null) return ticket;
        
        // Try premium
        PriorityBlockingQueue<QueueTicket> premiumQueue = queues.get(CustomerType.PREMIUM);
        ticket = premiumQueue.poll();
        if (ticket != null) return ticket;
        
        // Try regular
        PriorityBlockingQueue<QueueTicket> regularQueue = queues.get(CustomerType.REGULAR);
        return regularQueue.poll();
    }

    QueueTicket peek() {
        QueueTicket ticket = queues.get(CustomerType.VIP).peek();
        if (ticket != null) return ticket;
        
        ticket = queues.get(CustomerType.PREMIUM).peek();
        if (ticket != null) return ticket;
        
        return queues.get(CustomerType.REGULAR).peek();
    }

    void removeTicket(QueueTicket ticket) {
        PriorityBlockingQueue<QueueTicket> queue = queues.get(ticket.getType());
        if (queue != null) {
            queue.remove(ticket);
        }
    }

    int getPosition(QueueTicket ticket) {
        // Count how many tickets of same or higher priority are ahead
        int position = 0;
        
        for (CustomerType type : CustomerType.values()) {
            if (type == ticket.getType()) {
                // Count in this queue
                PriorityBlockingQueue<QueueTicket> queue = queues.get(type);
                for (QueueTicket t : queue) {
                    if (t.getTicketNumber() > ticket.getTicketNumber()) {
                        position++;
                    }
                }
                break;
            } else {
                // Add all tickets of higher priority
                PriorityBlockingQueue<QueueTicket> queue = queues.get(type);
                position += queue.size();
            }
        }
        
        return position + 1;  // 1-indexed
    }

    public void setAvgServiceTime(int minutes) {
        this.avgServiceTime = minutes;
    }
}
```

```java
package com.queue;

import java.time.LocalDateTime;

/**
 * INTUITION: QueueTicket represents a customer's place in line.
 * 
 * Like a "take a number" ticket at a deli.
 */
public class QueueTicket {
    private final String ticketId;
    private final String userId;
    private final String serviceId;
    private final int ticketNumber;
    private final CustomerType type;
    private final LocalDateTime joinedAt;
    private final String queueType;  // "VIP-001", "REG-123"
    
    private TicketStatus status;
    private long estimatedWait;  // In minutes

    public QueueTicket(String userId, String serviceId, int ticketNumber,
                      CustomerType type, long estimatedWait) {
        this.ticketId = UUID.randomUUID().toString();
        this.userId = userId;
        this.serviceId = serviceId;
        this.ticketNumber = ticketNumber;
        this.type = type;
        this.joinedAt = LocalDateTime.now();
        this.queueType = type.getPrefix() + "-" + String.format("%03d", ticketNumber);
        this.status = TicketStatus.WAITING;
        this.estimatedWait = estimatedWait;
    }

    // Getters
    public String getTicketId() { return ticketId; }
    public String getUserId() { return userId; }
    public String getServiceId() { return serviceId; }
    public int getTicketNumber() { return ticketNumber; }
    public CustomerType getType() { return type; }
    public String getQueueType() { return queueType; }
    
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public long getEstimatedWait() { return estimatedWait; }
    public void setEstimatedWait(long minutes) { this.estimatedWait = minutes; }
}

enum TicketStatus {
    WAITING,    // In queue
    SERVING,    // Currently being served
    SKIPPED,    // Missed their turn
    CANCELLED   // Left the queue
}

enum CustomerType {
    VIP(1, "VIP"),
    PREMIUM(2, "PRE"),
    REGULAR(3, "REG");
    
    private final int priority;
    private final String prefix;
    
    CustomerType(int priority, String prefix) {
        this.priority = priority;
        this.prefix = prefix;
    }
    
    public int getPriority() { return priority; }
    public String getPrefix() { return prefix; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle multiple service types (bank teller vs customer service)?"
> "Separate queues per service. Customer can join multiple queues. Notify when any queue reaches their ticket."

### Q2: "How to optimize for peak hours?"
> "Dynamic counter allocation: open more counters when queue > 20. Predictive: use historical data to schedule staff."

### Q3: "How to handle appointment + walk-in?"
> "Mixed queue: appointments get time slots, walk-ins fill gaps. Precedence: booked appointment > walk-in. Calendar integration."

### Q4: "How to detect no-shows and optimize?"
> "Track skip rate per time slot. Overbook by 10-15%. Send reminder SMS 30 min before. If confirmed, mark as priority."