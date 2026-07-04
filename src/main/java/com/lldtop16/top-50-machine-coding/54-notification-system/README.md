# 🔔 Problem 54: Notification System (Multi-Channel)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any SaaS company  
> **Est. Time**: 90 min | **Patterns**: Observer, Strategy, Factory

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a notification system supporting multiple channels."

**What the interviewer tests**:
```
1. Can you support multiple channels? (Email, SMS, Push)
2. Can you handle templates? (Personalized messages)
3. Can you manage preferences? (User chooses channels)
4. Can you handle retries? (Failed notifications)
5. Can you track delivery? (Read receipts, status)
```

### Step 2: The "Aha!" Moment

The key insight: **Notifications are events with delivery strategies.**

```
NOTIFICATION TYPES:
  - Transactional: Password reset, OTP
  - Marketing: Promotions, newsletters
  - System: Alerts, updates
  
CHANNELS:
  - Email: Async, 1-5 min delivery
  - SMS: Near real-time, 10-30 sec
  - Push: Instant, < 1 sec
  
PRIORITY:
  Critical (OTP) → SMS + Push
  High (Security alert) → Push + Email
  Normal (Newsletter) → Email only
```

### Step 3: How to ensure delivery?

```
RETRY STRATEGY:
  1st attempt: Send immediately
  2nd attempt: Retry after 5 min (exponential backoff)
  3rd attempt: Retry after 15 min
  Final: Mark as failed, alert ops team
  
FAILURE HANDLING:
  - Email bounces → mark invalid, remove from list
  - SMS failed → try email fallback
  - Push failed → queue for later
```

---

## 💻 Core Implementation

```java
package com.notification;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: NotificationService sends notifications via multiple channels.
 * 
 * Uses Strategy pattern for different channels.
 */
public class NotificationService {
    
    private final Map<String, User> users;
    private final Map<String, NotificationChannel> channels;
    private final NotificationQueue queue;
    private final TemplateEngine templateEngine;
    private final ScheduledExecutorService scheduler;

    public NotificationService() {
        this.users = new ConcurrentHashMap<>();
        this.channels = new ConcurrentHashMap<>();
        this.queue = new NotificationQueue();
        this.templateEngine = new TemplateEngine();
        this.scheduler = Executors.newScheduledThreadPool(10);
        
        // Register channels
        registerChannel(new EmailChannel());
        registerChannel(new SMSChannel());
        registerChannel(new PushChannel());
        
        // Start workers
        startWorkers();
    }

    /**
     * INTUITION: Send notification to user.
     * 
     * 1. Get user preferences
     * 2. Select channels
     * 3. Queue notification
     * 4. Process asynchronously
     */
    public void send(String userId, String templateId, Map<String, String> data) {
        User user = users.get(userId);
        if (user == null) return;
        
        // Get template
        String template = templateEngine.getTemplate(templateId);
        String message = templateEngine.render(template, data);
        
        // Get user's preferred channels
        List<NotificationChannel> userChannels = new ArrayList<>();
        for (String channelType : user.getPreferredChannels()) {
            NotificationChannel channel = channels.get(channelType);
            if (channel != null) {
                userChannels.add(channel);
            }
        }
        
        // Create notification
        Notification notification = new Notification(
            UUID.randomUUID().toString(),
            userId,
            message,
            userChannels,
            user.getPriority()
        );
        
        // Queue for processing
        queue.add(notification);
    }

    /**
     * Register notification channel.
     */
    public void registerChannel(NotificationChannel channel) {
        channels.put(channel.getType(), channel);
    }

    /**
     * Start worker threads.
     */
    private void startWorkers() {
        int workerCount = 5;
        
        for (int i = 0; i < workerCount; i++) {
            scheduler.submit(() -> {
                while (true) {
                    try {
                        Notification notification = queue.poll(1, TimeUnit.SECONDS);
                        if (notification != null) {
                            processNotification(notification);
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
     * Process notification through all channels.
     */
    private void processNotification(Notification notification) {
        System.out.println("Processing notification " + notification.getId());
        
        boolean delivered = false;
        
        for (NotificationChannel channel : notification.getChannels()) {
            try {
                boolean success = channel.send(notification);
                
                if (success) {
                    delivered = true;
                    notification.markDelivered(channel.getType());
                    
                    // If critical priority, stop after first success
                    if (notification.getPriority() == Priority.CRITICAL) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Channel " + channel.getType() + " failed: " + e.getMessage());
                
                // Retry logic
                scheduleRetry(notification, channel);
            }
        }
        
        if (!delivered) {
            notification.markFailed();
            alertOpsTeam(notification);
        }
    }

    /**
     * Schedule retry with exponential backoff.
     */
    private void scheduleRetry(Notification notification, NotificationChannel channel) {
        int retryCount = notification.getRetryCount();
        
        if (retryCount < 3) {
            long delay = (long) Math.pow(5, retryCount) * 60;  // 5, 25, 125 seconds
            
            scheduler.schedule(() -> {
                notification.incrementRetry();
                queue.add(notification);
            }, delay, TimeUnit.SECONDS);
        }
    }

    private void alertOpsTeam(Notification notification) {
        System.err.println("ALERT: Notification " + notification.getId() + 
                          " failed after 3 retries");
    }

    public void createUser(String userId, List<String> channels, Priority priority) {
        User user = new User(userId, channels, priority);
        users.put(userId, user);
    }
}

/**
 * Notification message.
 */
class Notification {
    private final String id;
    private final String userId;
    private final String message;
    private final List<NotificationChannel> channels;
    private final Priority priority;
    private final Set<String> deliveredChannels;
    private int retryCount;
    private NotificationStatus status;

    Notification(String id, String userId, String message, 
                 List<NotificationChannel> channels, Priority priority) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.channels = channels;
        this.priority = priority;
        this.deliveredChannels = new HashSet<>();
        this.status = NotificationStatus.PENDING;
    }

    void markDelivered(String channelType) {
        deliveredChannels.add(channelType);
        if (deliveredChannels.size() == channels.size()) {
            this.status = NotificationStatus.DELIVERED;
        }
    }

    void markFailed() {
        this.status = NotificationStatus.FAILED;
    }

    void incrementRetry() {
        this.retryCount++;
    }

    public String getId() { return id; }
    public String getMessage() { return message; }
    public List<NotificationChannel> getChannels() { return channels; }
    public Priority getPriority() { return priority; }
    public int getRetryCount() { return retryCount; }
}

enum NotificationStatus {
    PENDING, DELIVERED, FAILED, CANCELLED
}

enum Priority {
    CRITICAL, HIGH, NORMAL, LOW
}

/**
 * Channel interface.
 */
interface NotificationChannel {
    String getType();
    boolean send(Notification notification);
}

/**
 * Email channel.
 */
class EmailChannel implements NotificationChannel {
    @Override
    public String getType() { return "email"; }
    
    @Override
    public boolean send(Notification notification) {
        System.out.println("Sending email: " + notification.getMessage());
        return true;  // Mock success
    }
}

/**
 * SMS channel.
 */
class SMSChannel implements NotificationChannel {
    @Override
    public String getType() { return "sms"; }
    
    @Override
    public boolean send(Notification notification) {
        System.out.println("Sending SMS: " + notification.getMessage());
        return true;  // Mock success
    }
}

/**
 * Push notification channel.
 */
class PushChannel implements NotificationChannel {
    @Override
    public String getType() { return "push"; }
    
    @Override
    public boolean send(Notification notification) {
        System.out.println("Sending push: " + notification.getMessage());
        return true;  // Mock success
    }
}
```

```java
package com.notification;

import java.util.*;
import java.util.concurrent.*;

/**
 * Priority queue for notifications.
 */
class NotificationQueue {
    private final PriorityBlockingQueue<Notification> queue;

    NotificationQueue() {
        this.queue = new PriorityBlockingQueue<>(1000, Comparator.comparingInt(n -> 
            n.getPriority().ordinal()
        ));
    }

    void add(Notification notification) {
        queue.offer(notification);
    }

    Notification poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }
}

/**
 * Template engine for message personalization.
 */
class TemplateEngine {
    private final Map<String, String> templates;

    TemplateEngine() {
        this.templates = new HashMap<>();
        templates.put("welcome", "Hello {{name}}, welcome to our platform!");
        templates.put("otp", "Your OTP is {{otp}}. Valid for 10 minutes.");
    }

    String getTemplate(String templateId) {
        return templates.get(templateId);
    }

    String render(String template, Map<String, String> data) {
        String result = template;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}

class User {
    private final String userId;
    private final List<String> preferredChannels;
    private final Priority priority;

    User(String userId, List<String> preferredChannels, Priority priority) {
        this.userId = userId;
        this.preferredChannels = preferredChannels;
        this.priority = priority;
    }

    public String getUserId() { return userId; }
    public List<String> getPreferredChannels() { return preferredChannels; }
    public Priority getPriority() { return priority; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle user time zones?"
> "Convert send time to user's timezone. Batch notifications. Quiet hours (10 PM - 8 AM)."

### Q2: "How to prevent notification fatigue?"
> "Daily limits per channel. Smart bundling. User preference center. Unsubscribe option."

### Q3: "How to handle viral notifications?"
> "Batching: group similar notifications. Sampling: show to 10% users. Throttling: max 1 per minute."

### Q4: "How to ensure idempotency?"
> "Deduplication by notification ID. Exactly-once semantics. At-least-once with dedup."