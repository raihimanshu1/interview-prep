# Strategy Pattern — Complete Deep Dive

## 1. Why This Concept Matters

Strategy pattern defines a family of interchangeable algorithms, encapsulates each one, and makes them swappable at runtime. This is the canonical "favor composition over inheritance" pattern — instead of subclassing to change behavior, you compose the object with different strategies. In production, Strategy is used for payment processing (credit card, PayPal, crypto), validation rules, sorting/comparison algorithms, compression, authentication providers, and pricing calculations. Interviewers test this because it's the cleanest way to eliminate if-else chains and make code open for extension but closed for modification (OCP). Combined with Dependency Injection (Spring), Strategy becomes a powerful pattern where new strategies can be added without touching existing code.

Misunderstanding Strategy causes:
- Massive switch/if-else chains that grow with every new feature
- Duplicate code across similar algorithms (violates DRY)
- Violation of Open/Closed Principle — adding a new algorithm requires modifying existing classes
- Tight coupling — context class knows implementation details of each algorithm

## 2. Basic Meaning

Strategy pattern separates an algorithm from the object that uses it. The Context class holds a reference to a Strategy interface and delegates algorithm execution to it. ConcreteStrategy implementations provide the actual algorithm.

**Key vocabulary:**
- **Context**: the class that uses a Strategy. Has a `setStrategy()` method to swap at runtime.
- **Strategy**: an interface common to all supported algorithms. Declares the algorithm method(s).
- **ConcreteStrategy**: one specific implementation of the Strategy interface.
- **Composition over inheritance**: Strategy uses composition (context has-a strategy) instead of inheritance (context is-a strategy subclass).
- **Runtime switching**: context can change its strategy at runtime via `setStrategy()`.

**Strategy vs State:**
- Strategy: algorithms are independent and swappable. Context selects which algorithm to use.
- State: behavior depends on internal state. Context transitions between states, which change behavior.

**Strategy vs Template Method:**
- Strategy: the entire algorithm is interchangeable. Uses composition.
- Template Method: the algorithm skeleton is fixed. Subclasses override specific steps. Uses inheritance.

## 3. Real Code / Real Example

```java
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// === 1. BASIC STRATEGY PATTERN ===

// Strategy interface
interface PaymentStrategy {
    void pay(BigDecimal amount);
}

// Concrete strategies
class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;
    private final String cvv;
    private final String expiry;
    
    public CreditCardPayment(String cardNumber, String cvv, String expiry) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiry = expiry;
    }
    
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("Paid $" + amount + " via Credit Card " + maskCard(cardNumber));
        // In production: call Stripe/Payment gateway API
    }
    
    private String maskCard(String card) {
        return "****-****-****-" + card.substring(card.length() - 4);
    }
}

class PayPalPayment implements PaymentStrategy {
    private final String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("Paid $" + amount + " via PayPal (" + email + ")");
        // In production: call PayPal API
    }
}

class CryptoPayment implements PaymentStrategy {
    private final String walletAddress;
    private final String currency; // BTC, ETH, USDT
    
    public CryptoPayment(String walletAddress, String currency) {
        this.walletAddress = walletAddress;
        this.currency = currency;
    }
    
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("Paid $" + amount + " in " + currency + " to " + walletAddress);
        // In production: call blockchain API
    }
}

// Context
class ShoppingCart {
    private final List<Item> items = new ArrayList<>();
    private PaymentStrategy paymentStrategy;
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    public BigDecimal calculateTotal() {
        return items.stream()
            .map(Item::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void checkout() {
        BigDecimal total = calculateTotal();
        paymentStrategy.pay(total);
        items.clear();
    }
}

// === 2. STRATEGY WITH SPRING DI ===

// Spring automatically injects all PaymentStrategy beans into the Map
@Service
public class PaymentService {
    private final Map<String, PaymentStrategy> strategies;
    
    public PaymentService(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                s -> s.getClass().getSimpleName()
                    .replace("Payment", "")
                    .toLowerCase(),
                Function.identity()
            ));
    }
    
    public void processPayment(String method, BigDecimal amount) {
        PaymentStrategy strategy = strategies.get(method.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        strategy.pay(amount);
    }
}

// === 3. STRATEGY WITH ENUM (Selector-style) ===

enum PaymentMethod {
    CREDIT_CARD(new CreditCardPayment("4111-1111-1111-1111", "123", "12/28")),
    PAYPAL(new PayPalPayment("user@example.com")),
    CRYPTO(new CryptoPayment("0x1234...", "BTC"));
    
    private final PaymentStrategy strategy;
    PaymentMethod(PaymentStrategy strategy) { this.strategy = strategy; }
    public PaymentStrategy getStrategy() { return strategy; }
}

// === 4. STRATEGY FOR SORTING/COMPARISON ===

interface SortStrategy {
    <T extends Comparable<T>> void sort(List<T> list);
}

class BubbleSort implements SortStrategy {
    public <T extends Comparable<T>> void sort(List<T> list) {
        System.out.println("Using BubbleSort");
        // O(n²) — good for small lists
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j).compareTo(list.get(j + 1)) > 0) {
                    Collections.swap(list, j, j + 1);
                }
            }
        }
    }
}

class QuickSort implements SortStrategy {
    public <T extends Comparable<T>> void sort(List<T> list) {
        System.out.println("Using QuickSort");
        // O(n log n) average — good for large lists
        Collections.sort(list); // Java's optimized QuickSort/TimSort
    }
}

// === DEMO ===
public class StrategyDemo {
    public static void main(String[] args) {
        // Basic strategy
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new Item("Laptop", new BigDecimal("999.99")));
        cart.addItem(new Item("Mouse", new BigDecimal("49.99")));
        
        cart.setPaymentStrategy(new CreditCardPayment("4111-1111-1111-1111", "123", "12/28"));
        cart.checkout();
        
        cart.setPaymentStrategy(new PayPalPayment("alice@example.com"));
        cart.checkout();
        
        // Strategy with enum
        PaymentMethod.CREDIT_CARD.getStrategy().pay(new BigDecimal("100"));
        
        // Sort strategy
        List<Integer> numbers = new ArrayList<>(List.of(5, 2, 8, 1, 9));
        SortStrategy sorter = new QuickSort();
        sorter.sort(numbers);
        System.out.println(numbers); // [1, 2, 5, 8, 9]
    }
}
```

Expected output:
```
Paid $1049.98 via Credit Card ****-1111
Paid $1049.98 via PayPal (alice@example.com)
Paid $100 via Credit Card ****-1111
Using QuickSort
[1, 2, 5, 8, 9]
```

## 4. Strategy in Spring Boot (Real Production Pattern)

```java
// Define strategy interface
public interface NotificationStrategy {
    boolean supports(String channel);
    void send(String recipient, String message);
}

// Concrete strategies
@Component
public class EmailNotification implements NotificationStrategy {
    @Override public boolean supports(String channel) { return "email".equals(channel); }
    @Override public void send(String recipient, String message) {
        // Send via SMTP
    }
}

@Component
public class SmsNotification implements NotificationStrategy {
    @Override public boolean supports(String channel) { return "sms".equals(channel); }
    @Override public void send(String recipient, String message) {
        // Send via Twilio
    }
}

@Component
public class PushNotification implements NotificationStrategy {
    @Override public boolean supports(String channel) { return "push".equals(channel); }
    @Override public void send(String recipient, String message) {
        // Send via Firebase
    }
}

// Service uses all strategies
@Service
public class NotificationService {
    private final Map<String, NotificationStrategy> strategyMap;
    
    public NotificationService(List<NotificationStrategy> strategies) {
        this.strategyMap = strategies.stream()
            .collect(Collectors.toMap(
                s -> s.getClass().getSimpleName().replace("Notification", "").toLowerCase(),
                Function.identity()
            ));
    }
    
    public void sendNotification(String channel, String recipient, String message) {
        NotificationStrategy strategy = strategyMap.get(channel);
        if (strategy == null) throw new IllegalArgumentException("Unknown channel: " + channel);
        strategy.send(recipient, message);
    }
}
```

## 5. Tricky Interview Cases

**Case 1 — Strategy with state**
```java
class RateLimitingStrategy {
    private final int maxRequests;
    private final AtomicInteger counter = new AtomicInteger(0);
    
    public boolean allow() {
        return counter.incrementAndGet() <= maxRequests;
    }
    public void reset() { counter.set(0); }
}
// Problem: Strategy has state. If shared across contexts, state is shared.
// Fix: Either make strategies stateless, or create per-context instances.
```

**Case 2 — Choosing strategy based on runtime conditions**
```java
// Instead of if-else in context:
PaymentStrategy selectStrategy(PaymentRequest req) {
    if (req.getMethod().equals("CREDIT_CARD")) return new CreditCardStrategy(req);
    else if (req.getMethod().equals("PAYPAL")) return new PayPalStrategy(req);
    else throw new IllegalArgumentException();
}
// Use a StrategyFactory:
class PaymentStrategyFactory {
    private final Map<String, PaymentStrategy> strategies;
    
    PaymentStrategy create(PaymentRequest req) {
        PaymentStrategy strategy = strategies.get(req.getMethod());
        if (strategy == null) throw new IllegalArgumentException();
        return strategy.withContext(req); // or strategy.newInstance(req)
    }
}
```

**Case 3 — Strategy + Decorator for cross-cutting concerns**
```java
// Want to add logging to every strategy without modifying each one:
class LoggingPaymentStrategy implements PaymentStrategy {
    private final PaymentStrategy delegate;
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Override
    public void pay(BigDecimal amount) {
        log.info("Paying {} via {}", amount, delegate.getClass().getSimpleName());
        delegate.pay(amount);
        log.info("Payment completed");
    }
}
```

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Strategy interface too large | Many methods to implement for simple algorithms | Keep interface focused (1-2 methods) |
| If-else/switch in context to select strategy | Strategy pattern's purpose is defeated | Use Factory or registry (Map) |
| Stateful strategies shared across contexts | Race conditions, incorrect state | Stateless strategies or per-request instances |
| Forgetting to set strategy before use | NullPointerException | Default strategy or validate in context |
| Strategy pattern for one algorithm | Over-engineering | Use simple if-else for 1-2 variants |
| Strategies not injected via DI | Tight coupling, hard to test | Spring: inject List of strategies |

## 7. Final 30-Second Answer

Strategy = interchangeable algorithms. **Context** holds Strategy reference. **Concrete strategies** implement same interface. Switch at runtime via `setStrategy()`. **Favor composition over inheritance** — instead of subclassing to change behavior, compose with different strategies. **Spring**: inject `Map<String, Strategy>` from `List<Strategy>` using `@Component` on each strategy. **Use for**: payment methods, validation, sorting, compression, auth providers, notification channels. **Never**: if-else chains for algorithm selection, stateful strategies (unless per-request), large strategy interfaces. **Pattern variants**: Strategy + Factory (strategy selection), Strategy + Decorator (cross-cutting). Eliminates switch/if-else chains — adding new algorithm = new class, no existing code changes.