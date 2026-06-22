# Observer Pattern — Complete Deep Dive

## 1. Why This Concept Matters

Observer pattern defines a one-to-many dependency between objects: when one object (subject) changes state, all its dependents (observers) are notified automatically. This is the foundation of event-driven programming in Java. It powers Swing/AWT event listeners, Spring's ApplicationEvent, reactive streams, message queues (Pub/Sub), and distributed event buses. In production, Observer enables loosely coupled notification systems — adding a new observer requires changing only the observer class, never the subject. Interviewers test this as the canonical behavioral pattern for decoupled communication, often asking how it differs from Pub/Sub, how to handle asynchronous notifications, and how to prevent memory leaks from unregistered observers.

Misunderstanding Observer causes:
- Tight coupling between components (if Subject knows Observer details)
- Memory leaks from forgotten unsubscription (subject holds strong references to observers)
- Synchronous notification blocking the subject (slow observer delays subject)
- Exception propagation (one observer's exception prevents others from being notified)
- Thread safety issues (concurrent modification of observer list during notification)

## 2. Basic Meaning

The Observer pattern defines a subscription mechanism: multiple Observer objects watch a Subject. When the Subject's state changes, it broadcasts to all subscribed Observers.

**Key components:**
- **Subject (Observable)**: maintains a list of observers. Provides `subscribe()`, `unsubscribe()`, and `notify()` methods.
- **Observer**: interface with a single `update()` (or `notify()`) method. Concrete observers implement this.
- **ConcreteSubject**: the actual class whose state changes trigger notifications.
- **ConcreteObserver**: the class that reacts to state changes.

**What it is NOT**: 
- Not the same as Pub/Sub (Pub/Sub has a message broker/event bus between publishers and subscribers — Observer is direct 1-to-1).
- Not thread-safe by default — observer list mutations during notification cause `ConcurrentModificationException`.
- Not asynchronous by default — `notify()` calls each observer's `update()` synchronously.

## 3. Real Code / Real Example

```java
import java.util.*;
import java.util.concurrent.*;

// === 1. BASIC OBSERVER PATTERN ===

// Observer interface
interface Observer {
    void update(String event, Object data);
}

// Subject (Observable)
class EventBus {
    private final Map<String, List<Observer>> observers = new ConcurrentHashMap<>();
    
    public void subscribe(String eventType, Observer observer) {
        observers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                 .add(observer);
    }
    
    public void unsubscribe(String eventType, Observer observer) {
        List<Observer> list = observers.get(eventType);
        if (list != null) list.remove(observer);
    }
    
    public void publish(String eventType, Object data) {
        List<Observer> list = observers.get(eventType);
        if (list != null) {
            for (Observer observer : list) {
                try {
                    observer.update(eventType, data);
                } catch (Exception e) {
                    // Isolate observer exceptions — don't break other observers
                    System.err.println("Observer failed: " + e.getMessage());
                }
            }
        }
    }
}

// Concrete observers
class EmailService implements Observer {
    @Override
    public void update(String event, Object data) {
        if ("USER_CREATED".equals(event)) {
            User user = (User) data;
            System.out.println("[EmailService] Sending welcome email to " + user.getEmail());
        }
    }
}

class AnalyticsService implements Observer {
    @Override
    public void update(String event, Object data) {
        System.out.println("[Analytics] Event: " + event + " -> " + data);
    }
}

class AuditService implements Observer {
    @Override
    public void update(String event, Object data) {
        System.out.println("[Audit] Logging " + event + " at " + System.currentTimeMillis());
    }
}

// === 2. ASYNC OBSERVER (Non-blocking notification) ===
class AsyncEventBus {
    private final Map<String, List<Observer>> observers = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    
    public void subscribe(String eventType, Observer observer) {
        observers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                 .add(observer);
    }
    
    public void publishAsync(String eventType, Object data) {
        List<Observer> list = observers.get(eventType);
        if (list != null) {
            for (Observer observer : list) {
                executor.submit(() -> {
                    try {
                        observer.update(eventType, data);
                    } catch (Exception e) {
                        System.err.println("Async observer failed: " + e.getMessage());
                    }
                });
            }
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}

// === 3. JAVA'S BUILT-IN OBSERVER (Legacy, deprecated since Java 9) ===
// java.util.Observable (subject) and java.util.Observer (interface)
// Deprecated because:
//   1. Observable is a class (not interface) — violates composition over inheritance
//   2. setChanged() + notifyObservers() is awkward API
//   3. No thread safety
//   4. Not serializable

// === 4. SPRING'S APPLICATION EVENT (Production-grade Observer) ===
// Spring's ApplicationEvent + ApplicationListener is the standard Observer implementation
// in Spring Boot:

// Event class:
// public class UserCreatedEvent extends ApplicationEvent {
//     private final User user;
//     public UserCreatedEvent(Object source, User user) {
//         super(source);
//         this.user = user;
//     }
//     public User getUser() { return user; }
// }

// Publisher:
// @Component
// public class UserService {
//     @Autowired private ApplicationEventPublisher publisher;
//     
//     public void createUser(User user) {
//         // Save user...
//         publisher.publishEvent(new UserCreatedEvent(this, user));
//     }
// }

// Listener (Observer):
// @Component
// public class EmailNotificationListener {
//     @EventListener
//     public void handleUserCreated(UserCreatedEvent event) {
//         // Send email...
//         System.out.println("Sending email to " + event.getUser().getEmail());
//     }
// }
// Spring's @EventListener is asynchronous if @Async is added

// === DEMO ===
public class ObserverDemo {
    public static void main(String[] args) {
        // Basic sync observer
        EventBus eventBus = new EventBus();
        
        Observer email = new EmailService();
        Observer analytics = new AnalyticsService();
        Observer audit = new AuditService();
        
        eventBus.subscribe("USER_CREATED", email);
        eventBus.subscribe("USER_CREATED", analytics);
        eventBus.subscribe("USER_CREATED", audit);
        
        User user = new User("Alice", "alice@example.com");
        eventBus.publish("USER_CREATED", user);
        
        // Unsubscribe email — no longer gets notifications
        eventBus.unsubscribe("USER_CREATED", email);
        eventBus.publish("USER_CREATED", new User("Bob", "bob@example.com"));
        // Only analytics and audit will print
        
        // Async observer
        AsyncEventBus asyncBus = new AsyncEventBus();
        asyncBus.subscribe("ORDER_PLACED", event -> {
            System.out.println("Processing order async on thread: " 
                + Thread.currentThread().getName());
        });
        asyncBus.publishAsync("ORDER_PLACED", "order-123");
        asyncBus.shutdown();
    }
}
```

Expected output:
```
[EmailService] Sending welcome email to alice@example.com
[Analytics] Event: USER_CREATED -> User{name=Alice}
[Audit] Logging USER_CREATED at 1234567890
[Analytics] Event: USER_CREATED -> User{name=Bob}
[Audit] Logging USER_CREATED at 1234567891
Processing order async on thread: pool-1-thread-1
```

## 4. What Happens Internally

**Thread safety in observer notification:**
```java
// Problem: ConcurrentModificationException
List<Observer> observers = new ArrayList<>();
observers.add(observer1);
// Thread A: iterates observers for notification
for (Observer o : observers) { o.update(data); }
// Thread B: subscribes new observer
observers.add(observer2); // ConcurrentModificationException on Thread A!

// Solution 1: CopyOnWriteArrayList — copy on write, iteration safe
List<Observer> observers = new CopyOnWriteArrayList<>();
// Adds create a new copy of the array. Iteration always sees stable snapshot.

// Solution 2: Synchronized + snapshot copy
synchronized(observers) {
    List<Observer> snapshot = new ArrayList<>(observers);
}
for (Observer o : snapshot) { o.update(data); }
```

**Memory leak from unregistered observers:**
```java
// Subject holds strong reference to Observer
// If Observer is a UI component (heavy), and Subject is long-lived:
Subject subject = new Subject();
Frame frame = new Frame();
subject.subscribe("EVENT", frame);
frame.dispose(); // Frame closed, but GC cannot collect frame
// because Subject.observers still has a strong reference to it!
// MEMORY LEAK!

// Fix 1: Explicit unsubscribe
subject.unsubscribe("EVENT", frame);

// Fix 2: WeakReference in observer list
// Subject stores WeakReference<Observer> instead of Observer
// GC collects frame even if Subject still "holds" it

// Fix 3: Use Spring's @EventListener (lifecycle-managed, auto-cleanup)
```

**Spring @EventListener internals:**
```java
// Spring creates an ApplicationListenerAdapter for each @EventListener method
// It uses a multicast (SimpleApplicationEventMulticaster) to notify all listeners
// If no Executor is configured, notifications are SYNCHRONOUS (publisher thread)
// To make async:
@Bean
public SimpleApplicationEventMulticaster multicaster() {
    SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
    multicaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
    return multicaster;
}
```

## 5. Tricky Interview Cases

**Case 1 — Observer exception isolation**
```java
class BrokenObserver implements Observer {
    public void update(String event, Object data) {
        throw new RuntimeException("I broke!");
    }
}
eventBus.subscribe("TEST", new BrokenObserver());
eventBus.subscribe("TEST", new WorkingObserver());
eventBus.publish("TEST", "data");
// Output: BrokenObserver throws → WorkingObserver NEVER called
// (unless the subject has try-catch per observer)
```
Fix: Always wrap each observer update in try-catch to isolate exceptions.

**Case 2 — Synchronous blocking of subject**
```java
class SlowObserver implements Observer {
    public void update(String event, Object data) {
        Thread.sleep(5000); // blocks subject thread for 5 seconds!
    }
}
```
Problem: Subject's `publish()` method blocks for 5 seconds for each slow observer. All other processing stops.
Fix: Use async notification (ExecutorService per observer or async event bus).

**Case 3 — Observer order dependency**
```java
eventBus.subscribe("EVENT", observer1);  // should run first
eventBus.subscribe("EVENT", observer2);  // should run second

// Observers may run in any order if using CopyOnWriteArrayList (iteration order = insertion order, but still)
// If observer1 and observer2 have a dependency, you have a design problem
```
Fix: If observers have ordering requirements, use a priority system or a single observer that coordinates. Observer pattern assumes observers are independent.

**Case 4 — Event flooding**
```java
for (int i = 0; i < 1000000; i++) {
    eventBus.publish("RAPID_EVENTS", item);
    // Each publish iterates ALL observers → 1M * N observers = massive overhead
}
```
Fix: Batch events, throttle, or use async queue (Kafka) that can buffer and batch.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Synchronous notification | Slow observer blocks subject and other observers | Use async executor, or at minimum try-catch per observer |
| Strong reference from subject to observer | Memory leak — observer can't be GC'd | Explicit unsubscribe, or use WeakReference |
| No exception isolation | One broken observer silences all others | Try-catch around each observer's update() |
| Thread-unsafe observer list | ConcurrentModificationException | CopyOnWriteArrayList or synchronized snapshot |
| Forgetting to unsubscribe | Memory leak, stale notifications | Clean up in resource's close()/dispose() |
| Observer ordering assumed | Observers may run in any order | Make observers independent, or use priority mechanism |
| Too many events / too many observers | Performance degradation | Batch, throttle, use async queue |

## 7. Production Usage

**Spring Boot event-driven architecture:**
```java
// Define domain events
public class OrderPlacedEvent {
    private final String orderId;
    private final BigDecimal amount;
    private final String customerEmail;
    // constructor, getters
}

// Publish event
@Service
public class OrderService {
    @Autowired private ApplicationEventPublisher publisher;
    
    @Transactional
    public void placeOrder(Order order) {
        orderRepository.save(order);
        publisher.publishEvent(new OrderPlacedEvent(
            order.getId(), order.getAmount(), order.getCustomerEmail()));
    }
}

// Async event handlers (each in separate component)
@Component
public class InventoryReservationHandler {
    @EventListener
    @Async
    public void handleOrderPlaced(OrderPlacedEvent event) {
        inventoryService.reserve(event.getOrderId());
    }
}

@Component
public class EmailConfirmationHandler {
    @EventListener
    @Async
    public void handleOrderPlaced(OrderPlacedEvent event) {
        emailService.sendConfirmation(event.getCustomerEmail(), event.getOrderId());
    }
}

@Component
public class AnalyticsTracker {
    @EventListener
    @Async
    public void handleOrderPlaced(OrderPlacedEvent event) {
        analytics.track("order_placed", event.getAmount());
    }
}
```

**Event sourcing (EventStore):**
```java
// Store events as the source of truth
interface EventStore {
    void append(DomainEvent event);
    List<DomainEvent> readEvents(String aggregateId);
}

// Rebuild state from events
class OrderProjection {
    private BigDecimal totalAmount;
    
    public void apply(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            if (event instanceof OrderPlacedEvent) {
                this.totalAmount = this.totalAmount.add(((OrderPlacedEvent) event).getAmount());
            }
            // Other event types...
        }
    }
}
```

## 8. Advanced Details

- **Observer vs Pub/Sub**: Observer is direct (Subject calls Observer). Pub/Sub has a broker/channel between publisher and subscriber. Pub/Sub enables: multiple publishers, multiple subscribers, decoupling in time (pub/sub don't need to be running simultaneously).
- **Observer vs Reactive Streams (RxJava, Project Reactor)**: Reactive streams are Observer with backpressure. The subscriber can tell the publisher "slow down, I can't keep up." This is essential for streaming data where producer is faster than consumer.
- **WeakReference pattern**: Store `WeakReference<Observer>` in the subject. When observer is no longer referenced elsewhere (e.g., closed GUI window), GC can collect it even if subject still holds the WeakReference. The subject must periodically clean out cleared WeakReferences.
- **PropertyChangeListener**: JavaBeans' built-in Observer variant. `java.beans.PropertyChangeSupport` manages listeners for property changes on POJOs.
- **Event multithreading**: Consider `ArrayBlockingQueue` between publisher and consumer threads. Publisher puts events, consumer takes and notifies observers on its own thread.

## 9. Interview Questions And Answers

### Beginner
Q: What is the Observer pattern? Give a real-world example.
A: Observer defines a one-to-many dependency: when the subject changes state, all dependent observers are notified. Real-world example: Spring's `ApplicationEvent`. When an order is placed (`OrderService` publishes `OrderPlacedEvent`), multiple listeners react: `InventoryService` reserves stock, `EmailService` sends confirmation, `AnalyticsService` tracks the event. Each listener is an observer — adding a new listener requires only creating a new `@EventListener` method, no changes to `OrderService`.

### Intermediate
Q: What is the difference between the Observer pattern and Pub/Sub?
A: Observer is direct: subject holds references to observers and calls them directly. Pub/Sub has a message broker (channel/topic) between publisher and subscriber — the publisher publishes to a channel, and subscribers consume from that channel. Pub/Sub decouples publishers from subscribers in time (pub and sub don't need to exist simultaneously), space (they don't know each other's location), and cardinality (many publishers can publish to same channel). Spring's `ApplicationEvent` is Observer (direct, same JVM). Kafka is Pub/Sub (broker, potentially different processes/machines).

### Senior
Q: You have a subject that updates frequently (1000 events/sec) with 50 observers. Each observer does a database write. The subject thread is blocked. How do you fix the performance bottleneck?
A: Multiple approaches in order of increasing complexity:
1. **Async executor**: Give the subject an `ExecutorService`. Each observer update runs in a separate thread. Subject thread returns immediately.
2. **Batched writes**: Collect events into batches (e.g., every 100ms or every 100 events) and write batch to DB. Use a queue with drainTo().
3. **Message queue (Kafka)**: Subject publishes to Kafka. Observers consume asynchronously. Kafka handles buffering, ordering, and persistence.
4. **Backpressure**: If observers can't keep up, implement backpressure. Reactive streams (RxJava, Project Reactor) handle this natively — observer signals "I can process at most N events/sec."

### Tricky
Q: In Spring, `@EventListener` methods are synchronous by default. You add `@Async` to make them async. A new developer adds `@Transactional` to the listener method. A RuntimeException occurs after the DB write but before the method returns. What happens to the transaction?
A: The transaction is rolled back. `@Async` runs the method on a different thread. `@Transactional` on that method creates a transaction bound to that thread. If the RuntimeException propagates, Spring's transaction interceptor catches it and marks the transaction for rollback. The DB write is undone.

However, the event was already published synchronously by the publisher BEFORE the listener ran (async). So the publisher's transaction (if any) is unaffected. This is important: the event publisher's transaction commits independently of the async listener's transaction. This is by design — async listeners should be designed for eventual consistency.

## 10. Final 30-Second Answer

Observer = one-to-many notification. **Subject** holds observer list, **observers** react to state changes. **Sync** (default): subject blocked during notification. **Async**: use ExecutorService or message queue. **Isolate**: try-catch per observer (one failure shouldn't break others). **Memory leaks**: observers not unsubscribed can't be GC'd — use explicit unsubscribe or WeakReference. **Spring**: `@EventListener` for sync, `@Async` + `@EventListener` for async. **Pub/Sub**: Observer + broker (Kafka). **Reactive**: Observer + backpressure (Project Reactor). Never: sync blocking, no exception isolation, forget to unsubscribe, assume observer ordering.