# Spring Boot Versions — What Changed? (Explained Simply)

## Chapter 1: Why Spring Boot Versions Matter

### The "Car Model" Analogy

```
Spring Boot 1.x = Old Maruti 800 (2000s) — worked, but basic
Spring Boot 2.x = Modern Sedan   (2018) — huge upgrade, auto-pilot
Spring Boot 3.x = Tesla          (2022) — electric, self-driving
```

Each version broke some things BUT brought massive improvements:

```
SPRING BOOT 1.x (2014-2018)
  └─ XML config, Java 8, Servlet containers
  └─ What you learned in college

SPRING BOOT 2.x (2018-2022)
  └─ Java 8+, Reactive (WebFlux), Metrics, OAuth2
  └─ What MOST companies use today

SPRING BOOT 3.x (2022+)
  └─ Java 17+, Virtual threads, AOT, GraalVM native
  └─ What MODERN companies are adopting

SPRING BOOT 4.x (Future)
  └─ Will require Java 21+
```

### What This Guide Covers

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  Spring Boot 1.x → 2.x — The BIG migration                  │
│    • What broke? What improved?                              │
│    • Auto-configuration, Actuator, Security                  │
│                                                             │
│  Spring Boot 2.x features (version by version)               │
│    • 2.0: WebFlux, Data JDBC, Micrometer                    │
│    • 2.3: Graceful shutdown, layered JARs                   │
│    • 2.4: Config import, Data JDBC improvements             │
│    • 2.6: Redis cache metrics, Dependency Management         │
│    • 2.7: Auto-configuration replacement                    │
│                                                             │
│  Spring Boot 3.x — The Java 17+ revolution                  │
│    • 3.0: Java 17 base, GraalVM native, Observability       │
│    • 3.1: Testcontainers, Service Connections                │
│    • 3.2: Virtual threads support, RestClient               │
│    • 3.3: CDS support, Maven POM simplification             │
│    • 3.4: (Latest) Improved Docker, Better defaults         │
│                                                             │
│  Migration guides: 1→2, 2→3, what breaks how to fix         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Key insight for interviews:**
> If you're interviewing for a senior role, they'll ask:
> * "What's new in Spring Boot 3.x?"
> * "How does virtual threads change your service?"
> * "Have you migrated from 2.x to 3.x? What broke?"

---

## Chapter 2: Spring Boot 1.x → 2.x — The BIG Migration

### What Changed (The TL;DR)

```
SPRING BOOT 1.x                     SPRING BOOT 2.x
─────────────────────────────       ─────────────────────────────
Java 7/8 base                       Java 8+ (minimum)
XML config common                   Java config (annotation-based)
No reactive support                 WebFlux (reactive stack)
Actuator (basic)                    Actuator (metrics, health, env)
No Micrometer                       Micrometer (vendor-neutral metrics)
OAuth (deprecated)                  OAuth2 (Spring Security 5)
Embedded Tomcat 8                   Embedded Tomcat 9
Starter versions tied               Independent versioning (spring-boot-dependencies)
```

### The Pain of Migration (1.x → 2.x)

```java
// ─── BEFORE (Spring Boot 1.x) ───
// Security was XML-heavy or used deprecated classes

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// security config (old way):
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/api/**").authenticated()
            .and()
            .httpBasic();
    }
}
// ↑ This COMPILED in 1.x. In 2.x, some things got deprecated.
```

```java
// ─── AFTER (Spring Boot 2.x) ───
// SecurityConfigurerAdapter still works... for now
// But the new way is Lambda DSL:

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // In 2.x, you could still extend WebSecurityConfigurerAdapter
    // In 3.x, WebSecurityConfigurerAdapter is REMOVED
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
```

### What Actually Broke in 2.x

```yaml
# ─── application.properties (1.x) ───
server.context-path=/myapp
# ↑ This property existed in 1.x
# In 2.x: RENAMED to server.servlet.context-path

# ─── application.properties (2.x) ───
server.servlet.context-path=/myapp
```

```java
// ─── Actuator endpoints changed ───
// 1.x: /health, /info, /metrics (all enabled by default)
// 2.x: /actuator/health, /actuator/info (MOST DISABLED by default)

// To enable in 2.x:
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

### What Spring Boot 2.x Gave You

```java
// ─── 1. Micrometer (vendor-neutral metrics) ───
// In 1.x: metrics were limited, vendor-specific
// In 2.x: Micrometer — works with ANY monitoring system

@Bean
public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> registry.config().commonTags(
        "application", "myapp",
        "environment", "production"
    );
}

// Auto-collected metrics:
// JVM memory, GC, threads, CPU
// HTTP request counts, latencies, errors
// Database connection pool
// Cache hits/misses

// ─── 2. WebFlux (reactive) ───
// In 1.x: only Servlet stack (Tomcat)
// In 2.x: Both Servlet (Tomcat) AND Reactive (Netty)

// Reactive controller:
@RestController
public class ProductController {
    
    // Returns FLUX (reactive stream) — non-blocking
    @GetMapping("/products")
    public Flux<Product> getAllProducts() {
        return productRepository.findAll();  // reactive repository
    }
    
    // WebClient — reactive version of RestTemplate
    @GetMapping("/orders/{id}")
    public Mono<Order> getOrder(@PathVariable String id) {
        WebClient client = WebClient.create("https://order-service");
        return client.get()
            .uri("/orders/{id}", id)
            .retrieve()
            .bodyToMono(Order.class);
    }
}
```

---

## Chapter 3: Spring Boot 2.x Features (Version by Version)

### Spring Boot 2.0 — The Foundation (2018)

```java
// ─── 2.0 KEY FEATURE: Reactive Stack ───
// You could now choose between:
//   Servlet stack (Tomcat) — traditional, blocking
//   Reactive stack (Netty) — non-blocking, event-loop

// Traditional:
@SpringBootApplication
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
        // Default: Tomcat, servlet-based
    }
}

// Reactive (explicit):
@SpringBootApplication
public class ReactiveApp {
    public static void main(String[] args) {
        SpringApplication.run(ReactiveApp.class, args);
        // Add spring-boot-starter-webflux instead of spring-boot-starter-web
    }
}
```

```java
// ─── 2.0 FEATURE: Data JDBC ───
// Simpler than JPA. No lazy loading, no caching. Direct JDBC.

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    // JPA: findByName(String name)
    // Data JDBC: SAME syntax! But NO lazy loading (simpler)
    List<Order> findByCustomerId(Long customerId);
}
// Good for: simple CRUD without JPA overhead
// Bad for: complex relationships, many-to-many
```

### Spring Boot 2.3 — Graceful Shutdown (May 2020)

```java
// ─── 2.3 FEATURE: Graceful Shutdown ───
// Before 2.3: When you kill the app, in-flight requests get DROPPED
// After 2.3: App waits for requests to finish before shutting down

// application.properties:
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
// ↑ Now when you stop the app:
//   1. Stop accepting new requests
//   2. Wait up to 30s for existing requests to finish
//   3. Shut down

// Without this: database connections drop mid-query → data corruption
// With this: requests finish gracefully → no data loss
```

```java
// ─── 2.3 FEATURE: Layered JARs ───
// Docker images built faster by caching dependencies separately

// Old way (1.x): Single fat JAR → any code change rebuilds ENTIRE layer
// New way (2.3): Dependencies in one layer, code in another

# Dockerfile (optimized for layers):
FROM eclipse-temurin:17-jre as builder
WORKDIR /app
COPY build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract
# ↑ Extracts JAR into layers: dependencies, spring-boot, application

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

# Now: change 1 line of code → rebuild only the "application" layer
#      dependencies layer is CACHED → 2-second build instead of 2 minutes!
```

### Spring Boot 2.4 — Config Import (Nov 2020)

```yaml
# ─── 2.4 FEATURE: spring.config.import ───
# BEFORE 2.4: You could only use application.properties/yml
# AFTER 2.4: You can IMPORT config from MULTIPLE sources

# application.yml (main config):
spring:
  config:
    import:
      - classpath:db-config.yml           # Database settings from another file
      - classpath:secrets.yml             # Secret keys (or from Vault)
      - optional:configserver:http://config-server/  # Optional: fetch from config server

# spring.config.import also supports:
#   - file:./external-config.yml          # External file
#   - vault://secret/myapp                 # HashiCorp Vault
#   - consul:myapp/config                  # Consul
#   - aws-secretsmanager:/secret/myapp    # AWS Secrets Manager (2.6+)
```

### Spring Boot 2.7 — Auto-configuration Replacement (May 2022)

```java
// ─── 2.7 FEATURE: New Auto-configuration Registration ───
// BEFORE 2.7: spring.factories file (hard to discover)
// AFTER 2.7: META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

// Old way (1.x - 2.6):
// File: META-INF/spring.factories
// org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
// com.myapp.MyAutoConfiguration

// New way (2.7+):
// File: META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
// com.myapp.MyAutoConfiguration

// Simpler. Cleaner. Easier to discover what auto-configurations exist.
```

---

## Chapter 4: Spring Boot 3.x — The Revolution (2022+)

### Why Spring Boot 3.x is a BIG Deal

```
SPRING BOOT 2.x                  SPRING BOOT 3.x
────────────────────────────     ────────────────────────────
Requires Java 8+                 Requires Java 17+ (MUST)
Servlet stack                    Both servlet + reactive
RestTemplate (blocking)          RestClient (fluent, non-blocking)
WebSecurityConfigurerAdapter     SecurityFilterChain (lambda DSL)
Micrometer (basic)               Micrometer + Observability API
No native support                GraalVM native images
Thread pool (200 threads)        Virtual threads (1M threads)
```

### Spring Boot 3.0 — The Foundation (Nov 2022)

```java
// ─── 3.0: JAVA 17 IS MANDATORY ───
// If you're still on Java 11 → CANNOT use Spring Boot 3.x
// You MUST be on at least Java 17

// What Java 17 gives you:
//   - Records (clean DTOs)
//   - Sealed classes (restricted hierarchies)
//   - Text blocks (multi-line strings)
//   - Pattern matching (cleaner instanceof)
```

```java
// ─── 3.0 FEATURE: RestClient (replaces RestTemplate) ───
// RestTemplate is deprecated (not removed, but don't use)

// OLD RESTTEMPLATE (Spring Boot 2.x):
RestTemplate rest = new RestTemplate();
// ↓ Blocking call — thread waits for response
ResponseEntity<User> response = rest.getForEntity(
    "https://api.example.com/users/{id}",
    User.class,
    123
);
User user = response.getBody();

// NEW RESTCLIENT (Spring Boot 3.x):
RestClient rest = RestClient.create();

User user = rest.get()
    .uri("https://api.example.com/users/{id}", 123)
    .retrieve()
    .body(User.class);
// ↓ Fluent API. Same blocking behavior. Cleaner code.

// Better: with virtual threads, blocking is fine!
```

```java
// ─── 3.0 FEATURE: Observability (Micrometer Tracing) ───
// BEFORE 3.0: You needed Spring Cloud Sleuth for distributed tracing
// AFTER 3.0: Micrometer Tracing built-in (no Sleuth needed!)

// Old way (2.x): spring-cloud-starter-sleuth
// New way (3.0): micrometer-tracing + micrometer-tracing-bridge-brave

@Configuration
public class ObservabilityConfig {
    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }
}

// Auto-configuration:
//   - HTTP requests → automatically traced with traceId
//   - Database calls → traced
//   - Redis calls → traced
//   - Kafka messages → traced across services

// Log output automatically includes traceId:
// 2024-01-15 10:30:00 [traceId=abc123, spanId=def456] GET /api/orders
// 2024-01-15 10:30:01 [traceId=abc123, spanId=ghi789] SELECT * FROM orders
```

```java
// ─── 3.0 FEATURE: GraalVM Native Images ───
// Compile Spring Boot app to NATIVE binary (no JVM needed!)

// Before: Java app starts in 3-5 seconds
// After: Native app starts in 0.05 seconds!

// To enable:
// Add to build.gradle:
plugins {
    id 'org.springframework.boot' version '3.2.0'
    id 'org.graalvm.buildtools.native' version '0.9.28'
}

// Then build: ./gradlew nativeCompile
// Result: ./build/native/nativeCompile/myapp  (standalone binary!)
// Start time: 50ms (vs 3 seconds for JVM)
// Memory: 50MB (vs 200MB for JVM)
// Perfect for: serverless (AWS Lambda), containers (scale faster)
```

### Spring Boot 3.1 — Testcontainers + Service Connections (May 2023)

```java
// ─── 3.1 FEATURE: Service Connections (Auto-configure test containers) ───
// BEFORE 3.1: You wrote @TestContainers boilerplate
// AFTER 3.1: Spring auto-configure test containers for you!

// OLD WAY (2.x / 3.0):
@SpringBootTest
@Testcontainers
class OrderServiceTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    // ↑ Boilerplate. Every single test class needs this.
}
```

```java
// ─── NEW WAY (Spring Boot 3.1+) ───
// Just add test dependencies. Spring handles the rest.

@SpringBootTest
class OrderServiceTest {
    // That's it! No @TestContainers. No @DynamicPropertySource.
    // Spring Boot 3.1 auto-detects you have postgres container in test scope
    // Auto-starts container, auto-configures datasource
    
    @Autowired
    private OrderService orderService;
    
    @Test
    void shouldCreateOrder() {
        Order order = orderService.createOrder(1L, List.of(100L, 200L));
        assertThat(order.getTotal()).isGreaterThan(0);
    }
}

// How? Service Connection = new abstraction for managing external services
// In application-test.properties:
// spring.docker.compose.enabled=true  (auto-start docker-compose)
//                                   OR
// spring.testcontainers.enabled=true  (auto-configure containers)

// Supported services: PostgreSQL, MySQL, MongoDB, Redis, Kafka, RabbitMQ, Elasticsearch
```

```yaml
# ─── 3.1 FEATURE: Docker Compose Integration ───
# Put docker-compose.yml in your project root.
# Spring Boot auto-starts it during development!

# docker-compose.yml:
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: myapp
      POSTGRES_USER: myapp
      POSTGRES_PASSWORD: myapp
  redis:
    image: redis:7

# application.properties:
spring.docker.compose.enabled=true    # ← Auto-start containers!
# Spring reads docker-compose.yml, starts services,
# figures out connection details, configures datasource/redis FOR YOU!
```

### Spring Boot 3.2 — Virtual Threads (Nov 2023)

```java
// ─── 3.2 FEATURE: Virtual Threads Support ───
// This is the BIGGEST change in Spring Boot since 2.0!
// Enables handling THOUSANDS of concurrent requests with simple blocking code.

// BEFORE 3.2:
// Each request uses a Platform Thread (1MB stack)
// Thread pool = 200 threads (can't have more without crashing)
// Blocking calls (DB, API, Redis) waste threads

@RestController
public class OrderController {
    
    @GetMapping("/orders/{id}")
    public Order getOrder(@PathVariable Long id) {
        // This thread BLOCKS waiting for database
        // While blocked, it can't serve OTHER requests
        // Wasted potential!
        Order order = orderService.findById(id);
        return order;
    }
}
```

```yaml
# ─── AFTER 3.2: Enable Virtual Threads ───
# application.properties:
spring.threads.virtual.enabled=true
# That's it. ONE property. Changes EVERYTHING.

# Now each request uses a VIRTUAL thread (few KB, not 1MB)
# When request blocks on DB → JVM parks the virtual thread
# Carrier thread serves ANOTHER virtual thread
# NO thread waste!

# Before: 200 concurrent requests max (thread pool limit)
# After:  100,000+ concurrent requests (virtual threads are cheap)
```

```java
// ─── 3.2 ALSO: HttpInterface (Declarative HTTP Clients) ───
// Like Feign, but BUILT INTO Spring Boot. No extra dependency!

// Define an interface:
interface UserServiceClient {
    @GetExchange("/users/{id}")
    User getUser(@PathVariable Long id);
    
    @PostExchange("/users")
    User createUser(@RequestBody User user);
}

// Create a client from the interface:
@Configuration
class AppConfig {
    @Bean
    UserServiceClient userClient(WebClient.Builder builder) {
        return HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(builder.build()))
            .build()
            .createClient(UserServiceClient.class);
    }
}

// Use it anywhere:
@Service
class OrderService {
    private final UserServiceClient userClient;
    
    public Order createOrder(Long userId, OrderRequest request) {
        User user = userClient.getUser(userId);  // ← Simple! Like Feign!
        // ...
    }
}
```

### Spring Boot 3.3 — CDS + POM Simplification (May 2024)

```java
// ─── 3.3 FEATURE: CDS (Class Data Sharing) ───
// Makes app start even faster by pre-loading classes

// BEFORE: App starts → loads classes from JAR → 3 seconds
// AFTER:  CDS pre-loads classes → 1 second

// Enable in build.gradle:
tasks.named("bootBuildImage") {
    environment["BP_JVM_CDS_ENABLED"] = "true"
}

// Or manually generate CDS archive:
// java -XX:DumpLoadedClassList=classes.lst -jar myapp.jar
// java -Xshare:dump -XX:SharedClassListFile=classes.lst -XX:SharedArchiveFile=myapp.jsa
// java -Xshare:on -XX:SharedArchiveFile=myapp.jsa -jar myapp.jar
// ↑ Startup time reduced by 40-50%!
```

```yaml
# ─── 3.3 FEATURE: POM/Config Simplification ───
# OLD WAY (3.2):
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: myuser
    password: mypass
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

# NEW WAY (3.3) - Most of this is AUTO-CONFIGURED:
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: myuser
    password: mypass
# That's it! Spring 3.3 auto-detects:
#   - HikariCP is on classpath (auto-configures pool)
#   - PostgreSQL driver is on classpath (auto-detects driver class)
#   - Sensible defaults for pool size (max=10, min-idle=10)
```

### Spring Boot 3.4 — Latest (Nov 2024)

```yaml
# ─── 3.4 FEATURE: Structured Logging ───
# Before: logs are plain text, hard to parse
# After: logs are JSON, easy to send to logging systems

# application.properties:
logging.structured.format.console=logstash
# Output becomes:
# {"@timestamp":"2024-01-15T10:30:00","level":"INFO","message":"Order created","service":"order-service","traceId":"abc123"}

# Or use:
logging.structured.format.file=logstash
logging.structured.format.console=ecs  # Elastic Common Schema
```

```yaml
# ─── 3.4 FEATURE: Better Docker Defaults ───
# Spring Boot 3.4 generates OPTIMAL Dockerfiles by default

# BEFORE 3.4: You had to manually configure layers
# AFTER 3.4: Default Dockerfile is production-ready

# ./gradlew bootBuildImage  # ← This now creates:
#   - Optimized layered JAR (dependencies cached)
#   - Non-root user (security best practice)
#   - Small base image (eclipse-temurin, not full JDK)

# Result:
# Size: 180MB → 120MB (33% smaller)
# Security: runs as non-root user
# Build speed: 30s → 5s (layer caching)
```

```java
// ─── 3.4 FEATURE: Problem Details for Errors ───
// RFC 9457 — Standard error format for REST APIs

// BEFORE 3.4: Every app had different error format
// {
//   "error": "Order not found",
//   "status": 404
// }

// AFTER 3.4: Automatic RFC 9457 error responses
// Just add:
spring.mvc.problemdetails.enabled=true

// Now errors look like:
// {
//   "type": "about:blank",
//   "title": "Not Found",
//   "status": 404,
//   "detail": "Order with id 999 not found",
//   "instance": "/api/orders/999"
// }

// Custom exception → automatic RFC response:
@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order with id " + id + " not found");
    }
}
// Spring 3.4 auto-converts this to RFC 9457 format!
```

---

## Chapter 5: Migration Guide — Spring Boot 2.x → 3.x

### What WILL Break

```
CHANGE                                   WHAT TO DO
──────────────────────────────────────  ──────────────────────────
Java 8/11 NOT supported                 Upgrade to Java 17+
WebSecurityConfigurerAdapter REMOVED    Use SecurityFilterChain bean
spring.factories auto-config REMOVED    Use AutoConfiguration.imports
RestTemplate NOT auto-configured        Use RestClient or WebClient
Old actuator endpoints (health, env)    Use /actuator/health, /actuator/env
javax.* → jakarta.*                     Replace javax imports with jakarta
Spring Cloud Sleuth REMOVED             Use Micrometer Tracing
Cassandra/Elasticsearch drivers         Upgrade to compatible versions
```

### Step-by-Step Migration

```java
// ─── STEP 1: Update Java to 17+ ───
// build.gradle:
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// ─── STEP 2: Update Spring Boot version ───
// build.gradle:
id 'org.springframework.boot' version '3.2.0'
id 'io.spring.dependency-management' version '1.1.4'

// ─── STEP 3: Fix import javax.* → jakarta.* ───
// OLD:
import javax.persistence.*;
import javax.validation.*;

// NEW:
import jakarta.persistence.*;
import jakarta.validation.*;

// ─── STEP 4: Fix Security Config ───
// OLD (REMOVED in 3.x):
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/api/**").authenticated()
            .and()
            .httpBasic();
    }
}

// NEW (3.x):
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
    
    @Bean
    public UserDetailsService users() {
        // In-memory user store
        UserDetails user = User.builder()
            .username("admin")
            .password("{noop}password")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
}
```

### The Spring Boot 3.x Migration Checker

```yaml
# ─── Use Spring Boot Migrator to automate migration ───
# CLI tool that scans your code and suggests fixes

# Install:
# sdk install spring-boot-migrator

# Run:
spring-boot-migrator migrate --input myapp.jar --output output/

# What it detects:
#   1. javax.* → jakarta.* (automatic replacement)
#   2. WebSecurityConfigurerAdapter → SecurityFilterChain
#   3. spring.factories → AutoConfiguration.imports
#   4. Deprecated properties → new properties
#   5. Outdated dependencies → compatible versions
```

---

## Chapter 6: Which Version Should YOU Use?

### Decision Guide

```
YOUR SITUATION                         BEST VERSION
─────────────────────────────────────  ──────────────────────
Starting a NEW project today           Spring Boot 3.4
   → Java 21, Virtual threads, RestClient, Observability out of box

Company on Java 11, can't upgrade      Spring Boot 2.7 (last 2.x release)
   → Still gets security patches until Nov 2025
   → But you MUST plan migration to 3.x

Company already on Java 17             Spring Boot 3.4
   → No reason to stay on 2.x
   → Migration is worth it: 10x performance with virtual threads

Serverless / AWS Lambda                Spring Boot 3.4 + GraalVM native
   → Cold start: 3 seconds → 50ms
   → Memory: 200MB → 50MB
   → Cost: 4x cheaper

Microservices (many small services)    Spring Boot 3.4
   → Virtual threads simplify code (no reactive needed)
   → Observability built-in (tracing, metrics)
   → Native images for instant startup
```

### 30-Second Summary

```
SPRING BOOT 2.x → 3.x

WHAT CHANGED:
  Java 17+ REQUIRED (no more Java 8/11)
  SecurityFilterChain (WebSecurityConfigurerAdapter gone)
  javax.* → jakarta.* (legal reasons)
  RestClient → RestTemplate (cleaner API)
  Micrometer Tracing → Spring Cloud Sleuth (built-in)
  Virtual threads → 200 threads → 1M concurrent

KEY FEATURES BY VERSION:
  2.0: WebFlux, Micrometer, Data JDBC
  2.3: Graceful shutdown, Layered JARs
  3.0: Java 17, AOT, GraalVM, Observability
  3.1: Testcontainers auto-config, Docker Compose
  3.2: Virtual threads, HttpInterface
  3.3: CDS startup, POM simplification
  3.4: Structured logging, RFC 9457 errors, Better Docker

MIGRATE IF:
  ✓ Starting something new → 3.4
  ✓ On Java 17+ → 3.4 (no brainer)
  ✓ Need virtual threads → 3.2+
  ✓ On Java 8/11 → Upgrade Java first, THEN Boot

STAY ON 2.7 IF:
  ✗ Can't upgrade Java
  ✗ Large codebase, no migration budget
  ✗ Dependencies not compatible with 3.x
  But plan migration before Nov 2025 (end of 2.x support)