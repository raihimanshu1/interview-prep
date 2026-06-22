# Autowiring and Dependency Injection in Spring Boot — Complete Deep Dive

## 1. Why This Concept Matters

Autowiring is the mechanism by which Spring Boot automatically injects dependencies into beans, eliminating manual object creation and configuration. It is the backbone of the Inversion of Control (IoC) principle and enables loose coupling, testability, and maintainability. In production, incorrect autowiring causes `NoSuchBeanDefinitionException` at startup, circular dependency crashes, and unexpected bean selection when multiple candidates exist. Interviewers test autowiring because it reveals whether you truly understand the Spring container lifecycle, bean scopes, and component scanning.

Misunderstanding autowiring causes:
- `NoSuchBeanDefinitionException` at application startup
- `NoUniqueBeanDefinitionException` when multiple candidates match
- Circular dependency `BeanCurrentlyInCreationException`
- Unexpected bean injection when using interface types with multiple implementations
- hidden dependencies making code hard to test

## 2. Basic Meaning

**Dependency Injection (DI)** is a design pattern where objects receive their dependencies from an external source (the Spring container) rather than creating them internally.

**Autowiring** is Spring's automatic resolution of dependencies by type (or name) at container startup.

Key vocabulary:
- **`@Autowired`**: annotation triggering autowiring by type
- **`@Component`**: generic stereotype marking a class as a Spring bean
- **`@Service`**: specialized `@Component` for service layer
- **`@Repository`**: specialized `@Component` for data access (adds exception translation)
- **`@Controller` / `@RestController`**: specialized for web layer
- **`@Bean`**: method-level annotation inside `@Configuration` class
- **`@Qualifier`**: disambiguates when multiple beans of same type exist
- **`@Primary`**: marks preferred bean when multiple candidates exist
- **`@RequiredArgsConstructor`** (Lombok): constructor injection without boilerplate
- **Lazy initialization**: bean created on first request, not at startup

What it is NOT: autowiring is not magic. Spring still needs to find bean definitions, resolve dependencies, manage lifecycle. Field injection is not the only way — constructor injection is preferred for immutability and testability.

## 3. Real Code / Real Example

```java
// === DOMAIN ===
record OrderId(Long value) {}
record PaymentRequest(Long orderId, double amount) {}

// === REPOSITORY ===
interface PaymentRepository {
    Payment save(Payment p);
}

class JdbcPaymentRepository implements PaymentRepository {
    private final DataSource dataSource;
    // Constructor injection — preferred
    public JdbcPaymentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public Payment save(Payment p) {
        // JDBC insert logic
        return p;
    }
}

// === SERVICE ===
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    // Constructor injection: dependencies mandatory, immutable, testable
    public PaymentService(PaymentRepository paymentRepository,
                          PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    public Payment process(PaymentRequest req) {
        Payment p = new Payment(req.orderId(), req.amount());
        p.setStatus("PROCESSING");
        paymentGateway.charge(req.amount());
        return paymentRepository.save(p);
    }
}

// === CONFIGURATION ===
@Configuration
class PaymentConfig {
    @Bean
    PaymentGateway stripeGateway() {
        return new StripeGateway("sk_live_xxx");
    }

    @Bean
    PaymentGateway razorpayGateway() {
        return new RazorpayGateway("rzp_live_xxx");
    }

    @Bean
    PaymentRepository paymentRepository(DataSource ds) {
        return new JdbcPaymentRepository(ds);
    }
}

// === ALTERNATE IMPLEMENTATION ===
@Component
@Qualifier("stripe")
public class StripeGateway implements PaymentGateway {
    private final String apiKey;
    public StripeGateway(@Value("${stripe.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }
    @Override
    public boolean charge(double amount) {
        System.out.println("Stripe charged: " + amount);
        return true;
    }
}

@Component
@Qualifier("razorpay")
public class RazorpayGateway implements PaymentGateway {
    private final String apiKey;
    public RazorpayGateway(@Value("${razorpay.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }
    @Override
    public boolean charge(double amount) {
        System.out.println("Razorpay charged: " + amount);
        return true;
    }
}

// === PRIMARY SELECTION ===
@Component
@Primary  // preferred when multiple PaymentGateway beans exist
class StripeGateway implements PaymentGateway {
    // ...
}

// === FAILED AUTOWIRING SCENARIOS ===
@Component
class BrokenComponent {
    // No default constructor and no @Autowired constructor
    private final String requiredField;
    public BrokenComponent(String requiredField) { this.requiredField = requiredField; }
    // Spring cannot resolve String — multiple String beans or none
    // Throws NoSuchBeanDefinitionException at startup
}

// === CONDITIONAL AUTOWIRING ===
@Component
@ConditionalOnProperty(name = "payment.gateway.enabled", havingValue = "true")
class ConditionalPaymentService {
    // Only created if property is set
}

// === RUNTIME LOOKUP (rarely needed) ===
@Component
public class PaymentLookup {
    private final ApplicationContext ctx;
    public PaymentLookup(ApplicationContext ctx) { this.ctx = ctx; }
    public PaymentGateway getGateway(String name) {
        return ctx.getBean(name, PaymentGateway.class);
    }
}
```

```properties
# application.properties
stripe.api-key=sk_live_abc123
razorpay.api-key=rzp_live_xyz789
payment.gateway.enabled=true
```

Expected output (when `PaymentController` triggers `process`):
```
Stripe charged: 99.99
Payment saved: Payment[orderId=1, amount=99.99, status=PROCESSING]
```

If `@Primary` removed and no `@Qualifier` used:
```
Exception: NoUniqueBeanDefinitionException: expected single matching bean but found 2: stripe, razorpay
```

## 4. What Happens Internally

**Component scanning:**
When `@SpringBootApplication` starts:
1. Scan base package for classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, `@Configuration`
2. Register each as `BeanDefinition` in `BeanFactory`
3. `@ComponentScan` recursively scans sub-packages

**Autowiring resolution:**
1. For each `@Autowired` field/constructor/parameter:
   - Determine required type (e.g., `PaymentRepository`)
   - Find all beans of that type in `BeanFactory`
   - If exactly 1: inject it
   - If 0: throw `NoSuchBeanDefinitionException`
   - If >1: throw `NoUniqueBeanDefinitionException` unless `@Qualifier` or `@Primary` disambiguates

2. For `@Qualifier("stripe")`:
   - Find bean with name "stripe" OR qualifier "stripe"
   - `@Component("stripe")` sets bean name
   - `@Qualifier("stripe")` sets qualifier
   - Match by qualifier first, then fallback to name

3. For `@Primary`:
   - If multiple candidates and no qualifier, inject primary bean

**Constructor injection lifecycle:**
```java
// Spring resolves constructor parameters BEFORE instantiation
PaymentService ps = new PaymentService(paymentRepository, paymentGateway);
```
1. Resolve `PaymentRepository` bean (may trigger its own instantiation)
2. Resolve `PaymentGateway` bean
3. Instantiate `PaymentService` via constructor
4. Set fields, apply post-processors

**Field injection lifecycle:**
```java
@Service
public class MyService {
    @Autowired private PaymentRepository repo; // injected AFTER constructor
}
```
1. Call constructor (dependency may be null!)
2. Use `ReflectionUtils` to set fields marked `@Autowired`
3. Apply `@PostConstruct` methods

**Circular dependency detection:**
```java
@Component
class A { @Autowired B b; }
@Component
class B { @Autowired A a; }
```
Spring creates `A` (partially, `b`=null), sees `B` needed, creates `B` (partially, `a`=null). If both are singletons, Spring detects cycle at `createBean("a")` already in creation. Throws `BeanCurrentlyInCreationException`.

**Resolution order:**
1. Type match
2. Qualifier match
3. Primary match
4. Name match (field name if no qualifier)
5. `@Resource` (JSR-250): name-based, falls back to type

## 5. Tricky Interview Cases

**Case 1 — `NoUniqueBeanDefinitionException`**
```java
@Component class StripeGateway implements PaymentGateway { }
@Component class RazorpayGateway implements PaymentGateway { }

@Component
class PaymentService {
    @Autowired private PaymentGateway gateway; // ERROR: 2 candidates
}
```
Output: `NoUniqueBeanDefinitionException: expected single matching bean but found 2`
Fix: `@Qualifier("stripe")` or `@Primary` on one bean.

**Case 2 — Circular dependency with constructors**
```java
@Component class A {
    private final B b;
    @Autowired public A(B b) { this.b = b; }
}
@Component class B {
    private final A a;
    @Autowired public B(A a) { this.a = a; }  // DEADLOCK in Spring
}
```
Output: `BeanCurrentlyInCreationException: A is currently in creation`
Fix: Use setter injection for one side (`@Autowired` on method), or `@Lazy` on one bean.

**Case 3 — Primitive/String autowiring**
```java
@Component
class ConfigService {
    @Autowired private String appName;  // ERROR: no String bean
    @Autowired private int maxRetries;   // ERROR: no int bean
}
```
Output: `NoSuchBeanDefinitionException` for both
Fix: Use `@Value("${app.name}")` and `@Value("${max.retries}")` or constructor injection with `@ConfigurationProperties`.

**Case 4 — `@Resource` vs `@Autowired`**
```java
@Component class MyService {
    @Resource private PaymentGateway stripeGateway;   // by NAME
    @Autowired @Qualifier("stripe") private PaymentGateway gateway; // by TYPE + qualifier
}
```
If bean name is "stripeGateway" and qualifier is "stripe", `@Resource` fails (name mismatch), `@Autowired` succeeds (qualifier match).

**Case 5 — Optional autowiring**
```java
@Component
class OptionalService {
    @Autowired(required = false)
    private Optional<PaymentGateway> gateway;  // Java Optional, not Spring
    // If no bean found: sets field to null (not Optional.empty)
    // For Optional injection, use ObjectProvider
}
```
Output: `gateway` is `null` if not found.
Better: `ObjectProvider<PaymentGateway> gatewayProvider` → call `gatewayProvider.getIfAvailable()`.

**Case 6 — Profile-based autowiring**
```java
@Component
@Profile("prod")
class ProdPaymentService implements PaymentService { }
@Component
@Profile("test")
class TestPaymentService implements PaymentService { }
```
Output: Correct bean injected based on `spring.profiles.active=prod` or `test`. If no profile active and bean has no default profile, not created.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Field injection | Hard to test, hidden dependencies, mutable | Constructor injection (mandatory deps) |
| `@Autowired` on constructor with one arg | Redundant since Spring 4.3 | Omit `@Autowired` for single-constructor beans |
| No `@Qualifier` with multiple implementations | `NoUniqueBeanDefinitionException` | Add `@Qualifier` or `@Primary` |
| Circular dependency | `BeanCurrentlyInCreationException` | Setter injection, `@Lazy`, or refactor to remove cycle |
| Autowiring `ApplicationContext` | Service locator anti-pattern, hides deps | Constructor inject actual dependencies |
| `@Component` without package scanning | Bean not found | Ensure base package covers component |
| Field injection with `@Value` | Stringly-typed, no validation | Use `@ConfigurationProperties` |
| Forgetting `@Primary` with one main impl | Need qualifier everywhere | Mark main impl as `@Primary` |

## 7. Production Usage

**Spring Boot typical structure:**
```java
@SpringBootApplication
public class PaymentApp {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }
}
```
`@SpringBootApplication` is composed of:
- `@ComponentScan`: scans current package and sub-packages
- `@Configuration`: marks as bean definition source
- `@EnableAutoConfiguration`: triggers Spring Boot auto-configuration

**Constructor injection with Lombok:**
```java
@Service
@RequiredArgsConstructor  // generates constructor for all final fields
public class PaymentService {
    private final PaymentRepository repo;
    private final PaymentGateway gateway;
    // Lombok generates constructor, no boilerplate
}
```

**Profile-specific beans:**
```java
@Configuration
public class DataSourceConfig {
    @Bean
    @Profile("prod")
    DataSource prodDataSource() {
        return DataSourceBuilder.create().url("jdbc:postgresql://prod:5432/pay").build();
    }
    @Bean
    @Profile("test")
    DataSource testDataSource() {
        return DataSourceBuilder.create().url("jdbc:h2:mem:test").build();
    }
}
```

**Conditional bean registration:**
```java
@Bean
@ConditionalOnProperty(name = "feature.flags.new-payment", havingValue = "true")
PaymentGateway newPaymentGateway() { return new NewPaymentGateway(); }
```
Bean only created if property enabled. Enables feature flags via configuration.

## 8. Advanced Details

- **`@Autowired` required attribute:** `@Autowired(required = false)` silently ignores missing beans. Default `true` throws exception on missing.
- **`ObjectProvider<T>`:** Lazy lookup. `provider.getIfAvailable()` returns bean or null. Avoids eager initialization of optional dependencies.
- **`@Lookup` method injection:** For prototype-scoped beans injected into singletons. Spring overrides method to return fresh prototype each call.
- **Circular dependencies:** Spring 2.6+ disallows circular references by default (`spring.main.allow-circular-references=false`). Use constructor injection to surface cycles at compile time.
- **`@Configuration` vs `@Component`:** `@Configuration` classes are proxied (CGLIB) to ensure method-level `@Bean` calls return same instance. `@Component` beans are not proxied unless explicitly enhanced.
- **Bean post-processors:** `BeanPostProcessor` and `BeanFactoryPostProcessor` run before/after bean initialization. Can modify bean definitions or instances.
- **`@Import`:** Import additional configuration classes. `@Import(AuditConfig.class)` inside main config.
- **`@ConditionalOnJava`:** Auto-configure based on Java version. E.g., `@ConditionalOnJava(JavaVersion.EIGHT)` only loads on Java 8+.
- **`@ConditionalOnClass` / `@ConditionalOnMissingBean`:** Auto-configuration backbone. `spring-boot-autoconfigure` uses these heavily.

## 9. Interview Questions And Answers

### Beginner
Q: What is autowiring in Spring Boot? What are the different types?
A: Autowiring is Spring's automatic dependency injection. Types:
- **byType** (`@Autowired` default): resolves by matching bean type
- **byName**: resolves by matching bean name with field/method parameter name
- **Constructor**: resolves constructor parameters by type
- **Setter**: resolves setter method parameters by type

Spring Boot defaults to autowiring by type using `@Autowired` annotation.

### Intermediate
Q: What is the difference between `@Autowired`, `@Resource`, and `@Inject`? When would you use each?
A:
- **`@Autowired` (Spring-specific)**: by type + by qualifier. Supports `required=false`.
- **`@Resource` (JSR-250)**: by name first, then by type. No `required` attribute. Preferred in Java EE environments.
- **`@Inject` (JSR-330)**: by type, standard across DI frameworks (Guice, Dagger). No `required` attribute. Use for framework-agnostic code.

Use `@Autowired` in Spring-only projects. Use `@Inject` if code might run outside Spring (rare in practice).

### Senior
Q: You are migrating a Spring Boot application from field injection to constructor injection. After the change, the application fails to start with `BeanCurrentlyInCreationException` for `OrderService` → `PaymentService` → `OrderService`. Why? How does this inform your design going forward?
A: The original code used field injection, which allows circular references because Spring first instantiates the bean (with null fields), then injects dependencies. With constructor injection, all dependencies must be satisfied before the object is created. If A requires B and B requires A, neither can be constructed.

This is actually a **feature**, not a bug. Constructor injection surfaces design flaws at compile time or startup rather than at runtime.

Fix options:
1. **Redesign**: Introduce interface `PaymentProcessor` and extract common logic into third bean C. A and B both depend on C.
2. **Setter injection for one side**: `@Autowired` on setter breaks cycle (bean created with null, setter called later).
3. **`@Lazy`**: `@Autowired @Lazy PaymentService paymentService` delays resolution until first use.
4. **`ObjectProvider<B>`**: Defers lookup. A created immediately, B looked up lazily when needed.

Design principle: If circular dependency exists, it signals tight coupling. Prefer event-driven or mediator pattern over direct references.

### Tricky
Q: `@Component` class has two constructors: a no-arg constructor and one with `@Autowired`. Which does Spring use? What if both lack `@Autowired`?
A: **Spring 4.3+**: If there is exactly one constructor, `@Autowired` is optional. Spring uses single-arg or multi-arg constructor.

If there are multiple constructors:
- If one has `@Autowired`, Spring uses that one.
- If none have `@Autowired` and there is a single constructor, Spring uses it.
- If none have `@Autowired` and there are multiple constructors (including no-arg), Spring uses the no-arg constructor and does **not** autowire dependencies → fields remain `null` → `NullPointerException` later.

Trick:
```java
@Component
class Tricky {
    private final A a;
    public Tricky() { this.a = null; }  // chosen by Spring if no @Autowired on constructor below
    @Autowired
    public Tricky(A a) { this.a = a; }
    // Spring picks constructor WITH @Autowired because it is explicitly annotated
}
```

If neither constructor has `@Autowired`, Spring cannot determine intent and picks the no-arg constructor (alphabetically or by some internal heuristic). **Conclusion: Always use either single constructor (no `@Autowired` needed) or annotate the desired constructor explicitly with `@Autowired` to avoid ambiguity.**

## 10. Final 30-Second Answer

Autowiring = Spring automatically injects dependencies by type. `@Autowired` on constructor/field/setter. **Constructor injection preferred**: immutable, testable, fails fast on missing deps. `@Qualifier` disambiguates multiple beans; `@Primary` marks default. `@Component`, `@Service`, `@Repository`, `@Configuration` mark beans. `@Bean` in `@Configuration` class. Circular dependencies surface with constructor injection — redesign to avoid. Field injection hides deps, hard to test. Always prefer constructor injection for production code.