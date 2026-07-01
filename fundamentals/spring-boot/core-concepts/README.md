# Spring Boot Core Concepts — IoC, DI, Bean Scopes, Annotations, Profiles, Async, Actuator

## 1. Why This Concept Matters

Understanding Spring Boot's core concepts beyond the basics — bean scopes, `@Component` vs `@Service` vs `@Repository`, `@Bean` vs `@Component`, profiles for environment-specific config, `@Async` for asynchronous execution, and Actuator for production monitoring — is essential for building and debugging real applications. These are the most frequently asked Spring Boot interview questions.

---

## 2. IoC & Dependency Injection — Complete Deep Dive

### What is Inversion of Control (IoC)?

**Traditional approach (without Spring):**
```java
public class OrderService {
    private PaymentGateway paymentGateway = new StripeGateway(); // ❌ Tight coupling
    
    public void processOrder() {
        paymentGateway.charge(); // Hard-coded dependency
    }
}
```
Problems:
- Tight coupling to `StripeGateway`
- Cannot switch to `RazorpayGateway` without changing code
- Hard to test (cannot mock `StripeGateway`)
- Object creation scattered everywhere

**Spring IoC approach:**
```java
@Component
public class OrderService {
    private final PaymentGateway paymentGateway; // Interface
    
    // Spring injects the dependency
    public OrderService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
}

// Configuration
@Configuration
class PaymentConfig {
    @Bean
    @Primary  // Default choice
    PaymentGateway stripeGateway() {
        return new StripeGateway();
    }
    
    @Bean
    @Qualifier("razorpay")
    PaymentGateway razorpayGateway() {
        return new RazorpayGateway();
    }
}
```
Now `OrderService` doesn't know WHICH implementation it gets. Spring decides at runtime.

**IoC Container (ApplicationContext):**
```
1. Loads bean definitions (from @Component, @Bean, etc.)
2. Creates bean instances (instantiation)
3. Injects dependencies (DI)
4. Manages lifecycle (init/destroy callbacks)
5. Provides bean lookup (getBean())
```

### Dependency Injection (DI) — 3 Methods Deep Dive

**1. Constructor Injection ✅ BEST PRACTICE**

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final EmailService emailService;
    
    // Spring auto-wires (no @Autowired needed for single constructor)
    public OrderService(OrderRepository orderRepository,
                       PaymentGateway paymentGateway,
                       EmailService emailService) {
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
        this.emailService = emailService;
    }
}
```

**Advantages:**
- ✅ `final` fields → immutable → thread-safe
- ✅ Dependencies explicit → cannot be null
- ✅ Easy to test → `new OrderService(mockRepo, mockGateway)`
- ✅ Fails at startup if dependency missing (fail-fast)
- ✅ No `@Autowired` needed (Spring 4.3+ for single constructor)

**2. Setter Injection**

```java
@Service
public class OrderService {
    private OrderRepository orderRepository;
    
    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
```

**When to use:** Optional dependencies, circular dependency workaround, reconfiguration after bean creation.

**3. Field Injection ❌ NOT RECOMMENDED**

```java
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;  // Hidden dependency!
}
```

**Disadvantages:** Cannot use `final`, hard to test, hidden dependencies, NPE risk at runtime.

### How Spring Resolves Dependencies Internally

```
1. Spring scans for @Component, @Bean, etc.
2. For each @Autowired/constructor parameter:
   - Determine required type (e.g., OrderRepository)
   - Find all beans of that type in BeanFactory
   - If exactly 1: inject it
   - If 0: throw NoSuchBeanDefinitionException
   - If >1: throw NoUniqueBeanDefinitionException (unless @Qualifier/@Primary)
```

**Resolution order:**
1. Type match
2. `@Qualifier` match
3. `@Primary` match
4. Name match (field/parameter name)
5. `@Resource` (JSR-250): name first, then type

---

## 3. Bean Scopes & Lifecycle

| Scope | Instances | Thread-Safe? | When to Use |
|-------|-----------|--------------|-------------|
| **singleton** | 1 per context | ✅ Yes (stateless) | Default for services, repos |
| **prototype** | New per injection | ❌ No | Stateful beans, builders |
| **request** | 1 per HTTP request | ✅ Yes | Request-scoped data |
| **session** | 1 per HTTP session | ❌ No | Shopping cart, user prefs |
| **application** | 1 per ServletContext | ❌ No | Global state |
| **websocket** | 1 per WS session | ❌ No | Chat session data |

**Prototype in Singleton trap (CRITICAL):**
```java
@Component
public class OrderService {
    private final ShoppingCart cart;  // ❌ Prototype injected ONCE!
    
    public OrderService(ShoppingCart cart) {
        this.cart = cart;  // Same instance for ALL requests
    }
}

@Component
@Scope("prototype")
public class ShoppingCart {
    private List<Item> items = new ArrayList<>();  // Shared state!
}
```

**Fix:** Use `@Lookup` or `ObjectProvider<ShoppingCart>`.

### Bean Lifecycle Steps

```java
// 1. Load bean definition
// 2. BeanFactoryPostProcessors run
// 3. Instantiation: constructor called
// 4. Dependency injection: @Autowired fields set
// 5. @PostConstruct / afterPropertiesSet() called
// 6. BeanPostProcessor.postProcessAfterInitialization() [AOP proxies applied]
// 7. Bean ready for use
// 8. @PreDestroy / destroy-method() on context close
// 9. Bean destroyed
```

---

## 4. @Component vs @Service vs @Repository vs @Controller

| Annotation | Layer | Special Behavior |
|------------|-------|-----------------|
| `@Component` | Generic | None — basic stereotype |
| `@Service` | Business logic | None — semantic marker only |
| `@Repository` | Data access | **Exception translation**: JDBC → DataAccessException |
| `@Controller` | Web (MVC) | Enables request mapping, view resolution |
| `@RestController` | Web (REST) | `@Controller` + `@ResponseBody` — returns JSON/XML |

**Rule of thumb**: Use `@Service` for business logic, `@Repository` for data access, `@RestController` for APIs. Use `@Component` for utility classes.

---

## 5. @Bean vs @Component

| Aspect | @Bean | @Component |
|--------|-------|------------|
| **Where** | Method in `@Configuration` class | On the class itself |
| **Control** | Full control over creation | Spring manages automatically |
| **Use when** | Third-party classes, complex init | Your own classes |

**Why `@Bean` for third-party:**
```java
@Configuration
class PaymentConfig {
    @Bean
    public PaymentGateway stripe() {
        return new StripeGateway(env.getProperty("stripe.key"));
    }
}
```

**CGLIB proxying of `@Configuration` classes:**
```java
@Configuration
class AppConfig {
    @Bean
    public ServiceA serviceA() {
        return new ServiceA(serviceB()); // Calls PROXIED serviceB()
    }
    
    @Bean
    public ServiceB serviceB() {
        return new ServiceB();
    }
}
```

`serviceA()` calls proxy's `serviceB()` → returns SAME singleton. Without proxy: creates NEW instance.

---

## 6. @Qualifier & @Primary

```java
@Component
@Primary  // DEFAULT when no qualifier
class StripeGateway implements PaymentGateway { }

@Component
@Qualifier("razorpay")  // Must be explicitly selected
class RazorpayGateway implements PaymentGateway { }
```

```java
@Autowired
private PaymentGateway gateway;  // Gets StripeGateway (@Primary)

@Autowired
@Qualifier("razorpay")  // Explicitly gets RazorpayGateway
private PaymentGateway razorpay;
```

**Strategy:**
- `@Primary`: Mark most common impl (80% use case)
- `@Qualifier`: Explicit overrides (20% use case, tests)

---

## 7. Profiles — Environment-Specific Configuration

```yaml
# application.yml (common)
spring:
  profiles:
    active: ${APP_PROFILE:dev}

---
# application-dev.yml
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:devdb

---
# application-prod.yml
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:postgresql://prod-db:5432/mydb
```

**Bean-level profiles:**
```java
@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {
    public void run(String... args) {
        // Only in dev: seed test data
    }
}
```

**Activation:**
```bash
java -jar app.jar --spring.profiles.active=dev,test
# OR
export SPRING_PROFILES_ACTIVE=prod
java -jar app.jar
```

**Critical trap: Profile order**
```bash
java -jar app.jar --spring.profiles.active=dev,prod
```
Result: Only `prod` active. Last profile wins for conflicting properties.

To activate MULTIPLE profiles simultaneously, use separate files:
```yaml
# application-dev.yml
spring.config.activate.on-profile: dev

# application-test.yml  
spring.config.activate.on-profile: test
```
Both apply if both active.

---

## 8. @Async & Scheduling

### @Async Deep Dive

```java
@EnableAsync
@SpringBootApplication
public class Application {}

@Service
public class NotificationService {
    @Async
    public CompletableFuture<Void> sendEmail(String to, String message) {
        emailClient.send(to, message);
        return CompletableFuture.completedFuture(null);
    }
}
```

**CRITICAL limitations of @Async:**
1. **Only works on public methods** (AOP proxy)
2. **Only when called from another bean** (self-invocation bypasses proxy)
3. **Exceptions lost** unless you return `Future`/`CompletableFuture`
4. **SecurityContext NOT propagated** by default

**Example showing limitations:**
```java
@Service
public class OrderService {
    private final NotificationService notificationService;
    
    public void placeOrder(Order order) {
        // ❌ WRONG: Self-invocation, @Async NOT applied!
        sendEmail("user@example.com", "Order placed");
        
        // ✅ CORRECT: Call from another bean
        notificationService.sendEmail("user@example.com", "Order placed");
    }
    
    @Async  // ❌ This NEVER runs async because called from same class!
    public void sendEmail(String to, String message) {
        emailClient.send(to, message);
    }
}
```

### @Scheduled Deep Dive

```java
@EnableScheduling
@Component
public class ScheduledTasks {
    
    // Fixed rate: every 5s from START of previous execution
    @Scheduled(fixedRate = 5000)
    public void processQueue() {
        processPendingOrders();
    }
    
    // Fixed delay: 5s AFTER completion
    @Scheduled(fixedDelay = 5000)
    public void sendNotifications() {
        sendEmails();
    }
    
    // Cron: every day at 2 AM
    @Scheduled(cron = "0 0 2 * * *", zone = "America/New_York")
    public void generateDailyReport() {
        generateReport();
    }
}
```

**fixedRate vs fixedDelay:**
- `fixedRate = 5000`: Executes every 5s regardless of task duration
- `fixedDelay = 5000`: Waits 5s AFTER task COMPLETES

**Clustered apps:** Don't use `@Scheduled` — multiple instances run same job! Use Quartz with JDBC job store.

---

## 9. Spring Events — Decoupled Communication

```java
// 1. Define event
public record OrderCreatedEvent(Long orderId, String customerId, BigDecimal amount) {}

// 2. Publish event
@Component
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;
    
    public Order create(OrderRequest req) {
        Order order = save(req);
        eventPublisher.publishEvent(
            new OrderCreatedEvent(order.getId(), order.getCustomerId(), order.getTotal())
        );
        return order;
    }
}

// 3. Listen for event (synchronous by default)
@Component
public class OrderNotificationListener {
    
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        sendEmail(event.customerId(), "Order " + event.orderId() + " created!");
    }
}

// 4. Async listener
@Component
public class OrderAnalyticsListener {
    
    @EventListener
    @Async  // Runs in background
    public void onOrderCreated(OrderCreatedEvent event) {
        analyticsService.track("order_created", event.amount());
    }
}
```

**Event flow:**
```
OrderService.publishEvent()
    ↓
ApplicationEventPublisher
    ↓
OrderNotificationListener.onOrderCreated() (synchronous, same thread)
    ↓
OrderAnalyticsListener.onOrderCreated() (async, different thread)
```

---

## 10. Actuator

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,beans,conditions,mappings,loggers,threaddump,heapdump
  endpoint:
    health:
      show-details: always
  info:
    env:
      enabled: true
```

**Key endpoints:**
| Endpoint | Use |
|----------|-----|
| `/actuator/health` | Kubernetes liveness/readiness probes |
| `/actuator/metrics` | JVM, thread, memory, GC metrics |
| `/actuator/beans` | All beans in context |
| `/actuator/conditions` | Auto-configuration report |
| `/actuator/env` | All environment properties |
| `/actuator/loggers` | Log levels at runtime |
| `/actuator/threaddump` | Thread dump for deadlock detection |

---

## 11. Interview Traps & Frequently Asked Questions

### 🔴 Critical Traps

**Trap 1: Prototype-scoped bean inside Singleton**
```java
@Component
public class OrderService {
    private final ShoppingCart cart; // ❌ Prototype injected ONCE!
    
    public OrderService(ShoppingCart cart) {
        this.cart = cart; // Same instance for ALL requests
    }
}

@Component @Scope("prototype")
public class ShoppingCart {
    private List<Item> items = new ArrayList<>(); // Shared state!
}
```

❌ **Problem:** Prototype bean injected into singleton is created ONCE. All singleton methods share the same prototype instance → thread-safety issue, state leaks between users.

✅ **Fix options:**
1. `@Lookup` method:
```java
@Component
public abstract class OrderService {
    @Lookup
    protected abstract ShoppingCart getCart();
    
    public void process() {
        ShoppingCart cart = getCart(); // ✅ NEW instance
    }
}
```

2. `ObjectProvider<T>`:
```java
@Component
public class OrderService {
    private final ObjectProvider<ShoppingCart> cartProvider;
    
    public void process() {
        ShoppingCart cart = cartProvider.getIfAvailable(); // ✅ NEW instance
    }
}
```

---

**Trap 2: `@Value` vs `@ConfigurationProperties` in async context**
```java
@Component
public class AsyncService {
    @Value("${thread.pool.size:5}")  // ❌ Read ONCE at startup
    private int poolSize;
    
    @Async
    public void process() {
        // Uses old value if property changed at runtime
    }
}
```
✅ **Fix:** Use `@RefreshScope` (Spring Cloud) or read from `Environment` on each call.

---

**Trap 3: `@Async` exceptions lost**
```java
@Async
public void sendEmail(String to) {
    throw new RuntimeException("SMTP failed"); // ❌ Caller NEVER sees
}

// Caller:
sendEmail("user@test.com"); // Exception swallowed
```
✅ **Fix:** Return `CompletableFuture`:
```java
@Async
public CompletableFuture<Void> sendEmail(String to) {
    throw new RuntimeException("SMTP failed"); // Wrapped in Future
}

// Caller:
try {
    future.get(5, TimeUnit.SECONDS);
} catch (ExecutionException e) {
    // Root cause available
}
```

---

**Trap 4: Self-invocation bypasses @Transactional/@Async**
```java
@Service
public class OrderService {
    @Transactional
    public void placeOrder() {
        updateInventory(); // ❌ Self-invocation - bypasses proxy!
    }
    
    @Transactional
    public void updateInventory() { ... }
}
```
✅ **Fix:** Extract method to another bean or use self-injection.

---

**Trap 5: `@Bean` method calling another `@Bean` method — proxying issue**
```java
@Configuration
class AppConfig {
    @Bean
    public ServiceA serviceA() {
        return new ServiceA(serviceB()); // ❌ Direct call - bypasses proxy!
    }
    
    @Bean
    @Transactional
    public ServiceB serviceB() {
        return new ServiceB();
    }
}
```
✅ **Fix:** Use method parameter injection:
```java
@Bean
public ServiceA serviceA(ServiceB serviceB) { // ✅ Spring injects proxied bean
    return new ServiceA(serviceB);
}
```

---

**Trap 6: Field injection in production code**
```java
@Service
public class OrderService {
    @Autowired
    private OrderRepository repo; // ❌ Hidden dependency, hard to test
}
```
✅ **Fix:** Constructor injection:
```java
@Service
public class OrderService {
    private final OrderRepository repo; // ✅ Explicit, immutable
    
    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }
}
```

---

### 📋 Common Interview Questions

**Q: What is Inversion of Control (IoC)? How does Spring implement it?**

A: IoC = the framework controls object creation and lifecycle, not your code. Instead of `new Service()`, you declare dependencies and Spring injects them.

Spring implements IoC via:
1. **ApplicationContext** — the IoC container
2. **BeanFactory** — lower-level container (lazy initialization)
3. **Dependency Injection** — constructor, setter, field injection
4. **Lifecycle management** — init/destroy callbacks

Process:
1. Load bean definitions (from annotations or XML)
2. Create bean instances (instantiation)
3. Inject dependencies (DI)
4. Apply post-processors (AOP proxies)
5. Bean ready for use

---

**Q: Constructor injection vs Setter injection vs Field injection — which is best and why?**

A: **Constructor injection** is preferred.

| Aspect | Constructor | Setter | Field |
|--------|-------------|--------|-------|
| Mutability | ✅ Immutable (`final`) | ❌ Mutable | ❌ Mutable |
| Testability | ✅ Easy (no reflection) | ⚠️ Moderate | ❌ Hard (reflection) |
| Dependencies | ✅ Explicit (cannot be null) | ⚠️ Can be null | ❌ Hidden |
| NPE risk | ✅ At startup | ⚠️ At runtime | ❌ At runtime |
| Circular deps | ✅ Surfaces at startup | ⚠️ Can hide | ❌ Can hide |

**Constructor injection:**
- Final fields possible (immutability)
- Required dependencies explicit
- Easy to test
- Fails at startup if dependency missing
- No `@Autowired` needed (Spring 4.3+)

---

**Q: What are Spring bean scopes? When would you use each?**

A: Scopes control bean lifecycle and instance count:
- **singleton** (default): one per context. Use for stateless services, repositories, controllers.
- **prototype**: new instance per injection. Use for stateful, non-thread-safe beans (e.g., `ShoppingCart`).
- **request**: one per HTTP request. Use for web-scoped data.
- **session**: one per HTTP session. Use for user session data (shopping cart).
- **application**: one per ServletContext. Use for global application state.
- **websocket**: one per WebSocket session.

---

**Q: What is the difference between `@Component`, `@Service`, `@Repository`, `@Controller`?**

A: All are `@Component` stereotypes for component scanning. Differences:
- `@Component`: Generic stereotype
- `@Service`: Semantic marker for business logic (no special behavior)
- `@Repository`: Data access + **exception translation** — wraps JDBC `SQLException` into Spring's `DataAccessException` hierarchy
- `@Controller` / `@RestController`: Web layer — enables request mapping. `@RestController` = `@Controller` + `@ResponseBody` on all methods.

---

**Q: What is the difference between `@Bean` and `@Component`?**

A:
- `@Component`: Class-level annotation. Spring finds your class via component scanning and creates bean.
- `@Bean`: Method-level annotation on `@Configuration` class. You control creation programmatically.

Use `@Component` for your own classes. Use `@Bean` for:
1. Third-party classes (no source access)
2. Complex initialization logic
3. Conditional bean creation (`@ConditionalOnProperty`)

---

**Q: What does `@EnableAsync` do? What are its limitations?**

A: `@EnableAsync` registers `AsyncAnnotationBeanPostProcessor` which intercepts `@Async` methods and submits them to a `TaskExecutor`.

**Limitations:**
1. `@Async` works **only on public methods** (AOP proxy)
2. Works **only when called from another bean** (self-invocation bypasses)
3. Exceptions lost unless return `Future`/`CompletableFuture`
4. Need custom `TaskExecutor` for production
5. `SecurityContext` NOT propagated to async thread by default

---

**Q: How does Spring Boot's auto-configuration avoid conflicts with user-defined beans?**

A: Every auto-configuration bean uses `@ConditionalOnMissingBean`. Spring processes user `@Configuration` classes FIRST (higher priority), then auto-configuration. If user defines `DataSource` bean, `DataSourceAutoConfiguration`'s `@ConditionalOnMissingBean(DataSource.class)` evaluates to `false` → auto-config skipped. This allows override without explicit exclusion.

---

**Q: What happens when you define a `@Bean` method that calls another `@Bean` method in the same class?**

A: In `@Configuration` class, Spring uses CGLIB subclassing (proxy). Method calls go through proxy, returning same singleton instance.

```java
@Configuration
class AppConfig {
    @Bean
    public ServiceA serviceA() {
        return new ServiceA(serviceB()); // ✅ Goes through proxy
    }
    
    @Bean
    public ServiceB serviceB() {
        return new ServiceB();
    }
}
```

`serviceA()` calls proxy's `serviceB()` → returns SAME singleton. WITHOUT proxy: would call real method → creates NEW instance (wrong).

**Best practice:** Use method parameter injection:
```java
@Bean
public ServiceA serviceA(ServiceB serviceB) { // ✅ Spring injects
    return new ServiceA(serviceB);
}
```

---

**Q: When would you use `@Profile` vs separate `application-{profile}.yml`?**

A: Use both together:
- `application-{profile}.yml`: Environment-specific properties (DB URL, log level, port)
- `@Profile` on `@Bean`/`@Component`: Beans that should ONLY exist in certain environments (e.g., `DevDataInitializer` only in dev)
- `@ConditionalOnProperty`: Feature flags (enable/disable without code changes)

---

**Q: What is `@Lookup` and when would you use it?**

A: `@Lookup` tells Spring to override an abstract method to return a bean from context. Used for obtaining prototype beans from singletons:

```java
@Component
public abstract class OrderProcessor {
    @Lookup
    protected abstract ShoppingCart getCart();
    
    public void process(Order order) {
        ShoppingCart cart = getCart(); // ✅ NEW prototype instance
    }
}
```

Without `@Lookup`, prototype injected ONCE (wrong). With `@Lookup`, new prototype returned on every call.

---

**Q: What is Spring's event publishing mechanism?**

A: Decouple components using events:

```java
// 1. Define event
public record OrderCreatedEvent(Long orderId, String customerId) {}

// 2. Publish
@Component
public class OrderService {
    private final ApplicationEventPublisher publisher;
    
    public Order create(OrderRequest req) {
        Order order = save(req);
        publisher.publishEvent(new OrderCreatedEvent(order.getId(), order.getCustomerId()));
        return order;
    }
}

// 3. Listen
@Component
public class NotificationListener {
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        sendEmail(event.customerId(), "Order " + event.orderId() + " created!");
    }
    
    @Async  // Async listener
    @EventListener
    public void onOrderCreatedAsync(OrderCreatedEvent event) {
        processInBackground(event);
    }
}
```

Benefits: Loose coupling, multiple listeners, async support.

---

### 🎯 One-Liner Interview Answer

"Spring IoC: framework manages object lifecycle. Constructor injection preferred (immutable, testable, fail-fast). Scopes: singleton (default), prototype (stateful), request/session (web). Stereotypes: `@Service` (logic), `@Repository` (data + exception translation), `@RestController` (APIs). `@Bean` (third-party/complex), `@Component` (your classes). `@Primary` (default), `@Qualifier` (explicit). Profiles: `application-{profile}.yml`, `--spring.profiles.active`. @Async: separate thread, needs `@EnableAsync`, public + external call only. Events: `ApplicationEventPublisher`, `@EventListener`, `@Async` for async."

---

## 12. Final 30-Second Answer

**IoC**: Spring manages object lifecycle and dependencies. **Constructor injection** — `final` fields, explicit deps, easy to test. **Scopes**: singleton (default, stateless), prototype (stateful, use `@Lookup` from singleton), request/session (web). **Annotations**: `@Service` (business logic), `@Repository` (data access + exception translation), `@RestController` (REST APIs). `@Bean` (third-party/complex init in `@Configuration`), `@Component` (generic). `@Primary` (default when multiple beans), `@Qualifier` (explicit selection). **Profiles**: `@Profile`, `application-{profile}.yml`, `--spring.profiles.active`. **@Async**: `@EnableAsync`, public methods only, return `CompletableFuture` for exceptions. **Events**: `ApplicationEventPublisher` + `@EventListener` for decoupled communication. **Bean lifecycle**: constructor → `@Autowired` → `@PostConstruct` → AOP proxy → ready.