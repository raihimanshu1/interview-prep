# Observer Pattern

> **Defines a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.**

## 📖 Concept

**Real-world analogy:** YouTube subscriptions — you subscribe to a channel. When the channel uploads a video, all subscribers get notified.

## 🔍 When to Use

- One object (subject) needs to notify many objects (observers)
- Don't want tight coupling between subject and observers
- Event handling, Pub-Sub systems, real-time updates
- Multiple views need to update when model changes (MVC)

## ✅ Interview Checklist

- [ ] Subject interface with attach, detach, notify methods
- [ ] Observer interface with update method
- [ ] Concrete Subject maintains list of observers
- [ ] Concrete Observer implements update
- [ ] Push vs Pull notification model

## 🧪 Common Interview Question

**Problem:** Design a Stock Price Notifier. When a stock price changes, all subscribed investors should be notified via Email or SMS.

## 💻 Java Implementation

### 1. Basic Observer

```java
import java.util.ArrayList;
import java.util.List;

// Subject (Observable)
interface Stock {
    void subscribe(Investor investor);
    void unsubscribe(Investor investor);
    void setPrice(double newPrice);
}

// Concrete Subject
class StockImpl implements Stock {
    private String symbol;
    private double price;
    private List<Investor> investors = new ArrayList<>();

    public StockImpl(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    @Override
    public void subscribe(Investor investor) {
        investors.add(investor);
    }

    @Override
    public void unsubscribe(Investor investor) {
        investors.remove(investor);
    }

    @Override
    public void setPrice(double newPrice) {
        if (this.price != newPrice) {
            this.price = newPrice;
            notifyInvestors();
        }
    }

    private void notifyInvestors() {
        for (Investor investor : investors) {
            investor.update(symbol, price);
        }
    }
}

// Observer
interface Investor {
    void update(String symbol, double price);
}

// Concrete Observers
class EmailInvestor implements Investor {
    private String email;

    public EmailInvestor(String email) { this.email = email; }

    @Override
    public void update(String symbol, double price) {
        System.out.println("Email to " + email + ": " + symbol + " price is now ₹" + price);
    }
}

class MobileInvestor implements Investor {
    private String phoneNumber;

    public MobileInvestor(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public void update(String symbol, double price) {
        System.out.println("SMS to " + phoneNumber + ": " + symbol + " → ₹" + price);
    }
}
```

### 2. Usage

```java
public class ObserverDemo {
    public static void main(String[] args) {
        Stock apple = new StockImpl("AAPL", 150.0);

        Investor alice = new EmailInvestor("alice@example.com");
        Investor bob = new MobileInvestor("+91-9876543210");

        apple.subscribe(alice);
        apple.subscribe(bob);

        apple.setPrice(155.0); // Both get notified
    }
}
```

### 3. Full Working Example: Event Bus

```java
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Event
interface Event {
    String getType();
}

class PriceChangeEvent implements Event {
    private String symbol;
    private double oldPrice;
    private double newPrice;

    public PriceChangeEvent(String symbol, double oldPrice, double newPrice) {
        this.symbol = symbol;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    @Override
    public String getType() { return "PRICE_CHANGE"; }

    public String getSymbol() { return symbol; }
    public double getOldPrice() { return oldPrice; }
    public double getNewPrice() { return newPrice; }
}

// Event Bus
class EventBus {
    private Map<String, List<EventListener>> listeners = new ConcurrentHashMap<>();

    public void subscribe(String eventType, EventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribe(String eventType, EventListener listener) {
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) eventListeners.remove(listener);
    }

    public void publish(Event event) {
        List<EventListener> eventListeners = listeners.get(event.getType());
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                listener.onEvent(event);
            }
        }
    }
}

// Event Listener
interface EventListener {
    void onEvent(Event event);
}

// Concrete Listeners
class EmailNotifier implements EventListener {
    @Override
    public void onEvent(Event event) {
        if (event instanceof PriceChangeEvent e) {
            System.out.println("[EMAIL] " + e.getSymbol()
                + " changed from ₹" + e.getOldPrice() + " to ₹" + e.getNewPrice());
        }
    }
}

class SMSNotifier implements EventListener {
    @Override
    public void onEvent(Event event) {
        if (event instanceof PriceChangeEvent e) {
            System.out.println("[SMS] Alert! " + e.getSymbol()
                + " is now ₹" + e.getNewPrice());
        }
    }
}

class AuditLogger implements EventListener {
    @Override
    public void onEvent(Event event) {
        System.out.println("[AUDIT] Event logged: " + event.getType()
            + " at " + new Date());
    }
}

// Usage
public class EventBusDemo {
    public static void main(String[] args) {
        EventBus bus = new EventBus();

        bus.subscribe("PRICE_CHANGE", new EmailNotifier());
        bus.subscribe("PRICE_CHANGE", new SMSNotifier());
        bus.subscribe("PRICE_CHANGE", new AuditLogger());

        bus.publish(new PriceChangeEvent("AAPL", 150.0, 155.0));
        bus.publish(new PriceChangeEvent("GOOGL", 2800.0, 2820.0));
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Memory leak from forgotten unsubscription | Use weak references or lifecycle management |
| Notification storm | Batch updates, debounce, or filter events |
| Order of notification matters | Document order or use priority |
| Cascading updates | Break cycles or use event filtering |

## 🎯 Related Interview Questions

1. **Design an Event Bus / Pub-Sub system**
2. **Design a Weather Station** — WeatherData changes → Display panels update
3. **How does Observer differ from Pub-Sub?** — Pub-Sub has a broker/event channel
4. **Java's built-in Observer** — `java.util.Observer` deprecated, use `java.beans.PropertyChangeListener`

## 🆚 Observer vs Pub-Sub

| Aspect | Observer | Pub-Sub |
|--------|----------|---------|
| Coupling | Subject knows observers directly | Subject doesn't know subscribers |
| Mediator | None | Message broker/channel |
| Scale | One-to-many | One-to-many, often many topics |
| Example | Java PropertyChangeListener | Kafka, Redis Pub-Sub |