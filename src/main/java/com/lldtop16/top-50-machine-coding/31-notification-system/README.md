# 🔔 Problem 31: Notification System (Like Firebase Cloud Messaging)

> **Difficulty**: ⭐⭐ | **Company Fit**: Firebase, AWS SNS, Twilio  
> **Est. Time**: 90 min | **Patterns**: Observer, Template Method, Strategy

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Send notifications to users through multiple channels."

**What the interviewer tests**:
```
1. Can you abstract notification channels? (Email, SMS, Push, In-app)
2. Can you handle different priorities? (Urgent vs normal)
3. Can you handle retries? (What if SMS provider is down?)
4. Can you support templates? (Personalized messages)
```

### Step 2: The "Aha!" Moment

The key insight: **Separate notification logic from delivery mechanism.**

```
NotificationService
    ├── createNotification(userId, type, data)
    │       ↓
    │   [Notification] with status PENDING
    │       ↓
    ├── NotificationProcessor
    │       ↓
    │   For each user preference:
    │   - If prefers EMAIL → EmailNotificationHandler
    │   - If prefers SMS → SMSNotificationHandler
    │   - If prefers PUSH → PushNotificationHandler
    │       ↓
    │   [Delivered] or [Failed] → retry later
```

### Step 3: How to handle failures?

```
RETRY STRATEGY:
  1st attempt: Immediate
  2nd attempt: After 30 seconds
  3rd attempt: After 5 minutes
  Final: Mark as FAILED, alert ops team

PRIORITY QUEUES:
  - CRITICAL: Retry every 10 seconds, max 10 attempts
  - HIGH: Retry every 1 minute, max 5 attempts
  - NORMAL: Retry every 5 minutes, max 3 attempts
  - LOW: Single attempt, fail silently
```

---

## 💻 Core Implementation

```java
package com.notification;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: NotificationService is the main entry point.
 * 
 * Flow:
 * 1. Create notification (store in DB)
 * 2. Queue for processing
 * 3. Processor picks up and delivers via appropriate channel
 * 4. Update status (sent, failed, retry)
 */
public class NotificationService {
    
    private final NotificationProcessor processor;
    private final NotificationStore store;
    private final PriorityBlockingQueue<Notification> queue;
    private final ScheduledExecutorService retryService;
    
    private static final int MAX_RETRIES = 3;

    public NotificationService() {
        this.store = new InMemoryNotificationStore();
        this.processor = new NotificationProcessor(store);
        this.queue = new PriorityBlockingQueue<>(100, 
            Comparator.comparing(Notification::getPriority));
        this.retryService = Executors.newScheduledThreadPool(10);
        
        // Start processor
        new Thread(processor).start();
    }

    /**
     * INTUITION: Send a notification to a user.
     * 
     * 1. Create notification with PENDING status
     * 2. Add to priority queue based on priority
     * 3. Processor will pick it up and deliver
     * 
     * @param userId Target user
     * @param type NotificationType (EMAIL, SMS, PUSH)
     * @param priority HIGH, NORMAL, LOW
     * @param content Message content
     * @return Notification ID
     */
    public String sendNotification(String userId, NotificationType type, 
                                    Priority priority, String content) {
        Notification notification = new Notification(
            UUID.randomUUID().toString(),
            userId,
            type,
            priority,
            content
        );
        
        // Store in DB
        store.save(notification);
        
        // Add to queue for processing
        queue.offer(notification);
        
        return notification.getId();
    }

    /**
     * INTUITION: Send template-based notification.
     * 
     * Templates allow personalization:
     *   Template: "Hi {{name}}, your order {{orderId}} is shipped!"
     *   Data: {name: "John", orderId: "12345"}
     *   Result: "Hi John, your order 12345 is shipped!"
     */
    public String sendTemplate(String userId, String templateId, 
                                Map<String, String> data) {
        // Get template
        String template = getTemplate(templateId);
        
        // Replace placeholders
        String content = template;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        return sendNotification(userId, NotificationType.EMAIL, Priority.NORMAL, content);
    }

    /**
     * INTUITION: Retry failed notification.
     * 
     * Scheduled by processor when delivery fails.
     */
    void retryNotification(Notification notification) {
        if (notification.getRetryCount() >= MAX_RETRIES) {
            notification.setStatus(NotificationStatus.FAILED);
            store.update(notification);
            System.out.println("Giving up on notification " + notification.getId());
            return;
        }
        
        notification.incrementRetryCount();
        notification.setStatus(NotificationStatus.RETRY);
        store.update(notification);
        
        // Schedule retry with backoff
        long delay = calculateBackoff(notification.getRetryCount());
        retryService.schedule(() -> {
            queue.offer(notification);
        }, delay, TimeUnit.MILLISECONDS);
    }

    private long calculateBackoff(int retryCount) {
        // Exponential backoff: 1s, 2s, 4s
        return (long) Math.pow(2, retryCount) * 1000;
    }

    public void shutdown() {
        processor.shutdown();
        retryService.shutdown();
    }

    private String getTemplate(String templateId) {
        // In production: fetch from database/cache
        return "Hello {{name}}!";
    }
}
```

```java
package com.notification;

import java.time.LocalDateTime;

/**
 * INTUITION: Notification is immutable once created.
 * 
 * Represents a single notification to be delivered.
 */
public class Notification implements Comparable<Notification> {
    private final String id;
    private final String userId;
    private final NotificationType type;
    private final Priority priority;
    private final String content;
    private final LocalDateTime createdAt;
    
    private NotificationStatus status;
    private int retryCount;
    private String errorMessage;

    public Notification(String id, String userId, NotificationType type,
                       Priority priority, String content) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.priority = priority;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.status = NotificationStatus.PENDING;
        this.retryCount = 0;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public NotificationType getType() { return type; }
    public String getContent() { return content; }
    public Priority getPriority() { return priority; }
    
    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }
    public int getRetryCount() { return retryCount; }
    public void incrementRetryCount() { this.retryCount++; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    @Override
    public int compareTo(Notification other) {
        // Higher priority first
        return other.priority.ordinal() - this.priority.ordinal();
    }
}

enum NotificationType { EMAIL, SMS, PUSH, IN_APP }

enum NotificationStatus {
    PENDING,      // Waiting to be processed
    QUEUED,       // In processor queue
    SENDING,      // Being delivered
    SENT,         // Delivered successfully
    FAILED,       // Permanent failure
    RETRY,        // Scheduled for retry
    READ          // In-app notification read by user
}

enum Priority {
    CRITICAL(1), HIGH(2), NORMAL(3), LOW(4);
    
    private final int level;
    Priority(int level) { this.level = level; }
    public int getLevel() { return level; }
}
```

```java
package com.notification;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: NotificationProcessor runs in background.
 * 
 * Continuously polls queue for pending notifications.
 * Delivers via appropriate handler based on type.
 */
public class NotificationProcessor implements Runnable {
    
    private final NotificationStore store;
    private final Map<NotificationType, NotificationHandler> handlers;
    private volatile boolean running = true;

    public NotificationProcessor(NotificationStore store) {
        this.store = store;
        this.handlers = new HashMap<>();
        
        // Register handlers
        handlers.put(NotificationType.EMAIL, new EmailNotificationHandler());
        handlers.put(NotificationType.SMS, new SMSNotificationHandler());
        handlers.put(NotificationType.PUSH, new PushNotificationHandler());
        handlers.put(NotificationType.IN_APP, new InAppNotificationHandler());
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Wait for notification (blocks until available)
                Notification notification = store.getPendingNotification(100, TimeUnit.MILLISECONDS);
                
                if (notification != null) {
                    processNotification(notification);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processNotification(Notification notification) {
        notification.setStatus(NotificationStatus.QUEUED);
        
        NotificationHandler handler = handlers.get(notification.getType());
        if (handler == null) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage("No handler for type: " + notification.getType());
            store.update(notification);
            return;
        }
        
        try {
            notification.setStatus(NotificationStatus.SENDING);
            handler.send(notification);
            notification.setStatus(NotificationStatus.SENT);
            store.update(notification);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            store.update(notification);
            
            // Retry asynchronously
            retry(notification);
        }
    }

    private void retry(Notification notification) {
        // Trigger retry logic
        // In production: schedule with exponential backoff
    }

    public void shutdown() {
        this.running = false;
    }
}
```

```java
package com.notification;

/**
 * INTUITION: NotificationHandler is a Strategy.
 * 
 * Each channel (email, SMS, push) has its own handler.
 * Adding a new channel = adding a new handler.
 */
public interface NotificationHandler {
    void send(Notification notification) throws Exception;
}

/**
 * Email handler using SMTP.
 */
class EmailNotificationHandler implements NotificationHandler {
    @Override
    public void send(Notification notification) throws Exception {
        System.out.println("📧 Sending EMAIL to " + notification.getUserId() + 
                          ": " + notification.getContent());
        // In production: Use JavaMail API
        // Send email via SMTP server
    }
}

/**
 * SMS handler using Twilio/AWS SNS.
 */
class SMSNotificationHandler implements NotificationHandler {
    @Override
    public void send(Notification notification) throws Exception {
        System.out.println("📱 Sending SMS to " + notification.getUserId() + 
                          ": " + notification.getContent());
        // In production: Call Twilio API
    }
}

/**
 * Push notification handler using Firebase/APNS.
 */
class PushNotificationHandler implements NotificationHandler {
    @Override
    public void send(Notification notification) throws Exception {
        System.out.println("🔔 Sending PUSH to " + notification.getUserId() + 
                          ": " + notification.getContent());
        // In production: Call Firebase Cloud Messaging API
    }
}

/**
 * In-app notification (stored in database, shown when user opens app).
 */
class InAppNotificationHandler implements NotificationHandler {
    @Override
    public void send(Notification notification) throws Exception {
        System.out.println("💬 Storing IN-APP notification for " + notification.getUserId());
        // Store in database, no external API call needed
    }
}
```

```java
package com.notification;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: NotificationStore persists notifications.
 * 
 * In production: Database table
 * For demo: ConcurrentHashMap
 */
interface NotificationStore {
    void save(Notification notification);
    void update(Notification notification);
    Notification getPendingNotification(long timeout, TimeUnit unit) throws InterruptedException;
}

class InMemoryNotificationStore implements NotificationStore {
    private final Map<String, Notification> notifications = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<String> pendingQueue = 
        new PriorityBlockingQueue<>(100, (id1, id2) -> {
            Notification n1 = notifications.get(id1);
            Notification n2 = notifications.get(id2);
            return n2.getPriority().compareTo(n1.getPriority());
        });

    @Override
    public void save(Notification notification) {
        notifications.put(notification.getId(), notification);
        pendingQueue.offer(notification.getId());
    }

    @Override
    public void update(Notification notification) {
        notifications.put(notification.getId(), notification);
    }

    @Override
    public Notification getPendingNotification(long timeout, TimeUnit unit) 
            throws InterruptedException {
        String id = pendingQueue.poll(timeout, unit);
        if (id == null) return null;
        return notifications.get(id);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle thousands of notifications per second?"
> "Use message queue (Kafka/RabbitMQ). Multiple consumer threads. Async processing. Batch sends to providers (combine 100 SMS into one API call)."

### Q2: "How to respect quiet hours (don't send at 2 AM)?"
> "Check user's timezone and preferences. If quiet hours, schedule for later. Store 'next send time', not just 'send immediately'."

### Q3: "How to prevent notification spam?"
> "Aggregate: combine 10 messages → 1 digest. Rate limit: max 5 notifications/hour. User preferences: let users choose what to receive."

### Q4: "How to track delivery status (opened, clicked)?"
> "Include tracking pixel in emails. Use callback URLs for SMS/Push. Store events in analytics DB. Build dashboard for delivery rates."

### Q5: "How to handle multilingual notifications?"
> "Store templates per locale. Detect user's language preference. Use template engine with i18n support (gettext, resource bundles)."