# Spring Boot — Complete Deep Dive for 7+ Years Experience

## 1. Why This Concept Matters

Spring Boot is the most widely used Java framework for building production-grade applications. It eliminates boilerplate configuration, provides production-ready features (actuator, health checks, metrics), and enforces best practices through convention over configuration. For senior engineers, understanding Spring Boot goes beyond `@SpringBootApplication` — you need to know auto-configuration internals, bean lifecycle, AOP proxying, context hierarchy, reactive programming with WebFlux, security architecture, and how to build scalable REST APIs. Interviewers test Spring Boot at every level: from "What does @SpringBootApplication do?" to "How would you debug a circular dependency in production?" to "Design a rate limiter using Spring Boot filters."

Misunderstanding Spring Boot causes:
- Bean creation failures from incorrect component scanning
- Circular dependencies taking down production services
- Security vulnerabilities from misconfigured filter chains
- Memory leaks from improper bean scoping
- N+1 queries from lazy loading in REST APIs
- Poor performance from blocking I/O in reactive applications

## 2. Core Concepts Deep Dive

### Spring Boot Auto-Configuration

Auto-configuration is the "magic" that configures beans based on classpath dependencies. When you add `spring-boot-starter-web`, Spring Boot automatically configures:
- Embedded Tomcat server
- DispatcherServlet
- Jackson for JSON serialization
- Error handling (`BasicErrorController`)
- Static resource serving

**How it works:**
1. `@EnableAutoConfiguration` triggers auto-configuration
2. SpringFactoriesLoader loads `META-INF/spring.factories` or `AutoConfiguration.imports`
3. Each auto-configuration class has `@Conditional` annotations
4. Conditions are evaluated: classpath, properties, existing beans
5. Only matching configurations are applied

**Key conditional annotations:**
- `@ConditionalOnClass` — only if class is on classpath
- `@ConditionalOnMissingBean` — only if no user-defined bean exists
- `@ConditionalOnProperty` — only if property matches
- `@ConditionalOnWebApplication` — only for web apps
- `@ConditionalOnExpression` — SpEL expression evaluation

### Bean Lifecycle & Scopes

```
Bean creation order:
1. Constructor (dependency injection)
2. @Autowired / @Value field injection
3. @PostConstruct init method
4. Bean ready for use
5. @PreDestroy on context close
```

**Scope matrix:**
| Scope | Instances | Use Case | Thread-Safe? |
|-------|-----------|----------|--------------|
| singleton | 1 per context | Services, repositories | ✅ Yes (stateless) |
| prototype | New per injection | Stateful beans, builders | ❌ No |
| request | 1 per HTTP request | Request-scoped data | ✅ Yes (one thread) |
| session | 1 per HTTP session | User shopping cart | ❌ No (concurrent requests) |
| application | 1 per ServletContext | Global counters | ❌ No |
| websocket | 1 per WebSocket session | Chat sessions | ❌ No |

### Dependency Injection Best Practices

**Constructor injection (PREFERRED):**
```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final EmailService emailService;
    
    // Spring auto-wires constructor (no @Autowired needed for single constructor)
    public OrderService(OrderRepository orderRepository, 
                       PaymentGateway paymentGateway,
                       EmailService emailService) {
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
        this.emailService = emailService;
    }
}
```

**Why constructor injection wins:**
1. `final` fields possible (immutability)
2. Required dependencies explicit (cannot pass null)
3. Easy to test (no reflection, just `new OrderService(mock1, mock2)`)
4. No `@Autowired` annotation noise (Spring 4.3+ infers for single constructor)
5. Failure at startup (missing dependency) vs runtime (field injection failure)

### Profiles & Configuration

**application.yml structure:**
```yaml
# Common defaults (always loaded)
spring:
  application:
    name: myapp
  profiles:
    active: ${APP_PROFILE:dev}  # env var with fallback

---
# Profile-specific section
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:devdb
  jpa:
    hibernate:
      ddl-auto: create-drop

---
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:postgresql://prod-db:5432/mydb
  jpa:
    hibernate:
      ddl-auto: validate
```

**Profile-specific beans:**
```java
@Configuration
public class DataSourceConfig {
    @Bean @Profile("dev")
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(H2)
            .addScript("schema-dev.sql")
            .build();
    }
    
    @Bean @Profile("prod")
    public DataSource prodDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(env.getProperty("DB_URL"));
        ds.setUsername(env.getProperty("DB_USER"));
        ds.setPassword(env.getProperty("DB_PASS"));
        return ds;
    }
}
```

## 3. REST API Best Practices

### REST Controller Design

```java
@RestController
@RequestMapping("/api/v1/orders")
@Validated  // Enables @Valid on @RequestParam, @PathVariable
public class OrderController {
    
    private final OrderService orderService;
    
    // Constructor injection
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    // GET /api/v1/orders?page=0&size=20&sort=createdAt,desc
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        
        // Parse sort parameter
        Sort.Order order = Sort.Order.desc("createdAt");
        if (sort[0].endsWith(",asc")) {
            order = Sort.Order.asc(sort[0].split(",")[0]);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<OrderResponse> orders = orderService.findAll(pageable);
        
        return ResponseEntity.ok(orders);
    }
    
    // GET /api/v1/orders/123
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return orderService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/v1/orders
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse created = orderService.create(request);
        URI location = URI.create("/api/v1/orders/" + created.id());
        return ResponseEntity.created(location).body(created);
    }
    
    // PUT /api/v1/orders/123
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request) {
        return ResponseEntity.ok(orderService.update(id, request));
    }
    
    // DELETE /api/v1/orders/123
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
    }
}
```

### Request/Response DTOs (Record types Java 16+)

```java
// Request DTO with validation
public record CreateOrderRequest(
    @NotBlank(message = "Customer ID is required")
    String customerId,
    
    @NotEmpty(message = "Order must contain items")
    List<@Valid OrderItemRequest> items,
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Total must be positive")
    BigDecimal total
) {}

public record OrderItemRequest(
    @NotBlank String productId,
    @Min(1) @Max(9999) int quantity,
    @DecimalMin("0.01") BigDecimal price
) {}

// Response DTO (immutable, no setters)
public record OrderResponse(
    Long id,
    String customerId,
    List<OrderItemResponse> items,
    BigDecimal total,
    OrderStatus status,
    Instant createdAt,
    Instant updatedAt
) {}

public record OrderItemResponse(
    String productId,
    String productName,
    int quantity,
    BigDecimal price
) {}

public enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
```

### Exception Handling (Global Exception Handler)

```java
@RestControllerAdvice  // Applies to all @RestController
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // Handle validation errors (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .toList();
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors,
            Instant.now()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    // Handle custom business exceptions (409 Conflict)
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            null,
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // Handle insufficient funds (422 Unprocessable Entity)
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            ex.getMessage(),
            Map.of("available", ex.getAvailable(), "required", ex.getRequired()),
            Instant.now()
        );
        return ResponseEntity.unprocessableEntity().body(error);
    }
    
    // Catch-all for unexpected errors (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            null,
            Instant.now()
        );
        return ResponseEntity.internalServerError().body(error);
    }
}

// Standardized error response structure
public record ErrorResponse(
    int status,
    String message,
    Object details,  // Can be List<String> or Map<String, Object>
    Instant timestamp
) {}
```

## 4. Global Exception Handler

_Already covered above in Exception Handling section._

## 5. OAuth2 Implementation (Spring Boot 3.x)

### OAuth2 Resource Server (JWT Validation)

```yaml
# application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # Spring Boot fetches public keys from this endpoint
          issuer-uri: https://auth.myapp.com
          jwk-set-uri: https://auth.myapp.com/.well-known/jwks.json
```

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/actuator/health").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        
        return http.build();
    }
    
    // Convert JWT claims to Spring Security authorities
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setAuthoritiesClaimName("roles");  // JWT claim name
        converter.setAuthorityPrefix("ROLE_");       // Prefix before role
        return converter;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

### OAuth2 Client (Login with Google/GitHub)

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid, profile, email
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope: read:user, user:email
        provider:
          github:
            authorization-uri: https://github.com/login/oauth/authorize
            token-uri: https://github.com/login/oauth/access_token
            user-info-uri: https://api.github.com/user
```

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login/**", "/error/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );
        
        return http.build();
    }
}

// Controller to get user info after OAuth2 login
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal OidcUser principal) {
        
        return ResponseEntity.ok(Map.of(
            "id", principal.getIdToken().getSubject(),
            "email", principal.getEmail(),
            "name", principal.getFullName(),
            "picture", principal.getPicture()
        ));
    }
}
```

### OAuth2 Authorization Server (Spring Authorization Server)

```java
@Configuration
public class AuthorizationServerConfig {
    
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("oidc-client")
            .clientSecret("{bcrypt}$2a$12$...")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://localhost:8080/login/oauth2/code/oidc-client")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("read:orders")
            .build();
        
        return new InMemoryRegisteredClientRepository(oidcClient);
    }
    
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }
    
    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
            .issuer("https://auth.myapp.com")
            .build();
    }
}

// Token endpoint: POST /oauth2/token
// Returns: access_token (JWT), refresh_token, id_token, expires_in
```

## 6. GraphQL Integration (Spring Boot + Spring for GraphQL)

```java
// Schema definition (schema.graphqls)
type Order {
    id: ID!
    customerId: ID!
    items: [OrderItem!]!
    total: BigDecimal!
    status: OrderStatus!
    createdAt: Instant!
}

type Query {
    order(id: ID!): Order
    orders(customerId: ID, page: Int = 0, size: Int = 20): OrderPage!
}

type Mutation {
    createOrder(input: CreateOrderInput!): Order!
    cancelOrder(id: ID!): Order!
}

input CreateOrderInput {
    customerId: ID!
    items: [OrderItemInput!]!
}

input OrderItemInput {
    productId: ID!
    quantity: Int!
}

enum OrderStatus {
    PENDING
    CONFIRMED
    SHIPPED
    CANCELLED
}

// Controller (RuntimeWiring / @SchemaMapping)
@Controller
public class OrderGraphQLController {
    
    private final OrderService orderService;
    
    public OrderGraphQLController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    // Query: order(id: "123") { id total status }
    @SchemaMapping(typeName = "Query")
    public Order order(Long id) {
        return orderService.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
    }
    
    @SchemaMapping(typeName = "Query")
    public Page<Order> orders(@Argument Integer page, @Argument Integer size) {
        return orderService.findAll(PageRequest.of(page, size));
    }
    
    // Mutation: createOrder(input: {customerId: "123", items: [...]})
    @MutationMapping
    public Order createOrder(@Argument CreateOrderInput input) {
        return orderService.create(input);
    }
    
    // Nested resolver: order.items -> [OrderItem]
    @SchemaMapping
    public List<OrderItem> items(Order order) {
        return orderService.findItemsByOrderId(order.getId());
    }
}
```

**GraphQL vs REST trade-offs:**
| Feature | GraphQL | REST |
|---------|---------|------|
| Data fetching | Single request, client specifies fields | Multiple endpoints |
| Over-fetching | ✅ None (request only what you need) | ❌ Common |
| Under-fetching | ✅ None (nested resolvers) | ❌ Common (multiple calls) |
| Caching | ❌ Hard (no HTTP cache by default) | ✅ Easy (HTTP cache) |
| Learning curve | Steeper | Easier |
| Tooling | GraphiQL, Apollo | Postman, Swagger |

## 7. WebFlux & Reactive Programming

### Reactive Controller (Non-blocking I/O)

```java
@RestController
@RequestMapping("/api/v1/orders")
public class ReactiveOrderController {
    
    private final OrderRepository orderRepository;
    
    public ReactiveOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    // Returns Flux<Order> — stream of orders
    @GetMapping("/stream")
    public Flux<Order> streamOrders() {
        return orderRepository.findAll();  // Reactive repository returns Flux
    }
    
    // Returns Mono<Order> — single result or empty
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    // Reactive save
    @PostMapping
    public Mono<ResponseEntity<Order>> createOrder(@Valid @RequestBody Mono<Order> orderMono) {
        return orderMono
            .flatMap(orderRepository::save)
            .map(saved -> ResponseEntity.created(
                URI.create("/api/v1/orders/" + saved.getId()))
                .body(saved)
            );
    }
}

// Reactive repository
public interface ReactiveOrderRepository extends ReactiveCrudRepository<Order, Long> {
    Flux<Order> findByCustomerId(String customerId);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    Flux<Order> findByStatus(@Param("status") OrderStatus status);
}
```

### WebClient (Reactive HTTP Client)

```java
@Service
public class ExternalApiService {
    
    private final WebClient webClient;
    
    public ExternalApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://api.external.com")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer token")
            .build();
    }
    
    // Non-blocking call — returns Mono<ApiResponse>
    public Mono<ApiResponse> fetchData(String id) {
        return webClient
            .get()
            .uri("/data/{id}", id)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, 
                response -> Mono.error(new ClientResponseException(...)))
            .bodyToMono(ApiResponse.class);
    }
    
    // Parallel calls — zip results together
    public Mono<CombinedResponse> fetchMultiple(String userId) {
        return Mono.zip(
            fetchOrders(userId),
            fetchPayments(userId),
            fetchProfile(userId)
        ).map(tuple -> new CombinedResponse(
            tuple.getT1(),
            tuple.getT2(),
            tuple.getT3()
        ));
    }
}
```

### Schedulers (Controlling Thread Pools)

```java
// Parallel I/O — use boundedElastic for blocking calls
@GetMapping("/blocking")
public Mono<String> blockingCall() {
    return Mono.fromCallable(() -> {
        // Blocking call (JDBC, file I/O)
        return blockForData();
    })
    .subscribeOn(Schedulers.boundedElastic())  // Dedicated thread pool
    .map(data -> process(data));
}

// CPU-bound work
@GetMapping("/compute")
public Mono<Integer> compute(int n) {
    return Mono.fromCallable(() -> expensiveComputation(n))
        .subscribeOn(Schedulers.parallel());  // CPU-intensive thread pool
}

// Event loop (never block!)
@GetMapping("/reactive")
public Flux<String> reactiveStream() {
    return Flux.interval(Duration.ofSeconds(1))
        .map(i -> "Tick " + i)
        .take(10);
}
```

**When to use WebFlux vs Spring MVC:**
- **WebFlux**: High concurrency (10K+ concurrent connections), streaming data (server-sent events), non-blocking I/O (reactive DB client), microservice gateway
- **Spring MVC**: Simple CRUD apps, blocking I/O (JDBC), easier debugging, wider ecosystem support

**Performance comparison:**
- Thread per request (Tomcat): ~1000 concurrent before thread exhaustion
- Reactive (Netty): ~10,000+ concurrent with same memory
- But: reactive code is harder to debug, stack traces are harder to read

## 8. Advanced Filters & CORS (Custom Filters)

### Custom Filter (Rate Limiting Example)

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)  // Run this filter FIRST
public class RateLimitFilter extends OncePerRequestFilter {
    
    private final RateLimiter rateLimiter;  // Use Bucket4j or Resilience4j
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        String clientId = request.getHeader("X-Client-Id");
        if (clientId == null) {
            clientId = request.getRemoteAddr();
        }
        
        // Check rate limit
        RateLimitResult result = rateLimiter.isAllowed(clientId, 100, Duration.ofMinutes(1));
        
        if (!result.allowed()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {"error": "Rate limit exceeded", "retryAfter": "%d"}
                """.formatted(result.retryAfterSeconds()));
            return;
        }
        
        // Add rate limit headers
        response.setHeader("X-RateLimit-Limit", "100");
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.remaining()));
        
        filterChain.doFilter(request, response);
    }
}
```

### CORS Configuration

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://myapp.com", "https://admin.myapp.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("X-Total-Count", "X-Page-Number")  // Custom headers
            .allowCredentials(true)
            .maxAge(3600);
    }
}

// Or configure via Spring Security:
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // ...
            ;
        return http.build();
    }
    
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://myapp.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
```


## 9. Advanced Topics for 7+ Years Experience

### AOP & Proxy Internals (How @Transactional, @Async Work)

Spring AOP uses proxy-based weaving by default. When you annotate a bean with `@Transactional`, Spring wraps it in a proxy:

```
Your Bean → CGLIB Proxy → Target Bean
                    ↓
              TransactionInterceptor
                    ↓
              Begins transaction → calls your method → commits/rolls back
```

**JDK Dynamic Proxy vs CGLIB:**
- **JDK Dynamic Proxy**: Only works if bean implements an interface. Creates proxy implementing same interface.
- **CGLIB**: Subclasses your bean at runtime. Works without interface. Default in Spring Boot 3+.

**How @Transactional actually works:**
```java
// Your code:
@Transactional
public void transfer(Account from, Account to, BigDecimal amount) {
    from.debit(amount);
    to.credit(amount);
}

// What Spring creates (simplified):
public class OrderServiceProxy extends OrderService {
    private TransactionInterceptor interceptor;
    
    public void transfer(...) {
        // 1. Open connection, set autoCommit=false
        // 2. Set isolation level
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        
        try {
            // 3. Call YOUR method
            super.transfer(from, to, amount);
            
            // 4. Commit
            conn.commit();
        } catch (Exception e) {
            // 5. Rollback
            conn.rollback();
            throw e;
        } finally {
            // 6. Close connection, return to pool
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```

**Self-invocation problem:**
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

// Inside placeOrder(), `this.updateInventory()` calls the raw object,
// NOT the proxy. Transaction is ignored.
```

### Spring Boot Actuator & Production Monitoring

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,beans,conditions,mappings,loggers,threaddump,heapdump,shutdown
  endpoint:
    health:
      show-details: always
      probes:
        enabled: true  # Kubernetes liveness/readiness
  info:
    env:
      enabled: true
    git:
      mode: full  # Show git commit, branch
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true  # Detailed latency percentiles
      slo:
        http.server.requests: 100ms, 500ms, 1s
```

**Custom actuator endpoint:**
```java
@Component
@Endpoint(id = "custom", enableByDefault = true)
public class CustomEndpoint {
    
    @ReadOperation
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "timestamp", Instant.now(),
            "database", checkDatabase(),
            "disk", checkDiskSpace()
        );
    }
    
    @WriteOperation
    public String resetCache() {
        cacheManager.getCache("orders").clear();
        return "Cache cleared";
    }
}
```

**Key production endpoints:**
| Endpoint | Purpose | Kubernetes Use |
|----------|---------|----------------|
| `/actuator/health` | App health status | Liveness probe |
| `/actuator/health/liveness` | Can app serve traffic? | Liveness probe |
| `/actuator/health/readiness` | Is app ready? | Readiness probe |
| `/actuator/metrics` | JVM, CPU, memory | Monitoring (Prometheus) |
| `/actuator/metrics/jvm.memory.used` | Memory usage | Alerting |
| `/actuator/threaddump` | Thread states | Deadlock detection |
| `/actuator/heapdump` | Heap dump | Memory leak analysis (manual) |

### Caching (Spring Cache Abstraction)

```java
@Service
public class ProductService {
    
    // Cache method result
    @Cacheable(value = "products", key = "#id", unless = "#result.price < 100")
    public Product findById(Long id) {
        // Only cached if price >= 100
        return productRepository.findById(id).orElse(null);
    }
    
    // Update cache when data changes
    @CacheEvict(value = "products", key = "#product.id")
    @CachePut(value = "products", key = "#product.id")
    public Product update(Product product) {
        return productRepository.save(product);
    }
    
    // Clear entire cache
    @CacheEvict(value = "products", allEntries = true)
    public void refreshCache() { ... }
}

// Configuration
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        // Simple in-memory (dev only)
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("products", "users");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000));
        return cacheManager;
    }
    
    // Production: Redis
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```

**Cache annotations:**
- `@Cacheable`: Cache method result. Only executes method on cache miss.
- `@CacheEvict`: Remove from cache. `allEntries=true` clears entire cache.
- `@CachePut`: Always execute method, update cache (for updates).
- `@Caching`: Group multiple cache operations.

### Scheduling & Batch Processing

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    // Fixed rate: every 5 seconds (measures from START of previous execution)
    @Scheduled(fixedRate = 5000)
    public void processQueue() {
        processPendingOrders();
    }
    
    // Fixed delay: wait 5 seconds AFTER completion of previous execution
    @Scheduled(fixedDelay = 5000)
    public void sendNotifications() {
        sendEmails();
    }
    
    // Cron expression: every day at 2 AM
    @Scheduled(cron = "0 0 2 * * *", zone = "America/New_York")
    public void generateDailyReport() {
        generateReport();
    }
    
    // Conditionally schedule with @ConditionalOnProperty
    @Scheduled(fixedRate = 60000)
    @ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true")
    public void conditionalTask() { ... }
}
```

**Distributed scheduling (clustered apps):**
```java
// Don't use @Scheduled in clustered apps - multiple instances run same job!
// Use Quartz with JDBC job store (database-backed locking):

@Component
public class QuartzJob {
    
    @Bean
    public JobDetailFactoryBean orderProcessingJob() {
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(OrderProcessingJob.class);
        factory.setDurability(true);
        return factory;
    }
    
    @Bean
    public Trigger orderProcessingTrigger(JobDetail jobDetail) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setRepeatInterval(60000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return trigger.getObject();
    }
}
```

### Resilience Patterns (Circuit Breaker, Retry, Timeout)

```java
@Service
public class PaymentService {
    
    private final PaymentGateway paymentGateway;
    private final RetryTemplate retryTemplate;
    
    // Retry with exponential backoff
    public PaymentResult processPayment(PaymentRequest request) {
        return retryTemplate.execute(context -> {
            try {
                return paymentGateway.charge(request);
            } catch (PaymentGatewayException e) {
                // Retry only on timeout/5xx errors
                if (context.getRetryCount() >= 3) {
                    throw new PaymentException("Gateway unavailable after 3 retries", e);
                }
                throw e; // Retry
            }
        });
    }
    
    // Circuit breaker pattern (Resilience4j)
    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "fallbackPayment")
    public PaymentResult charge(PaymentRequest request) {
        return paymentGateway.charge(request);
    }
    
    // Fallback method (same signature + Exception parameter)
    private PaymentResult fallbackPayment(PaymentRequest request, Exception e) {
        log.warn("Payment gateway down, using fallback", e);
        return PaymentResult.pending(request); // Queue for later
    }
}

// Configuration
@Configuration
public class ResilienceConfig {
    
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        
        // Retry 3 times with exponential backoff
        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(1000);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(10000);
        template.setBackOffPolicy(backOff);
        
        // Retry on specific exceptions
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        template.setRetryPolicy(retryPolicy);
        
        return template;
    }
    
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)  // Open circuit if 50% failures
            .waitDurationInOpenState(Duration.ofSeconds(30))  // Wait 30s before half-open
            .slidingWindowSize(10)  // Last 10 calls
            .permittedNumberOfCallsInHalfOpenState(3)
            .build();
        
        return CircuitBreakerRegistry.of(config);
    }
}
```

### Testing Strategies (Test Slices, @DataJpaTest)

```java
// Unit test: test single component in isolation
@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {
    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    void testCreateOrder() { ... }
}

// Repository slice: test JPA repository with real database
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class OrderRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Test
    void testSaveAndFind() {
        Order order = new Order("customer-1", new BigDecimal("100.00"));
        Order saved = orderRepository.save(order);
        
        Optional<Order> found = orderRepository.findById(saved.getId());
        assertTrue(found.isPresent());
    }
}

// Web slice: test controller with MockMvc
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;
    
    @Test
    void testGetOrder() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
            .andExpect(status().isOk());
    }
}

// Full integration test: entire context + real database
@SpringBootTest
@Testcontainers
class OrderIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Autowired
    private OrderController orderController;
    
    @Test
    void testEndToEnd() {
        // Test entire flow: controller → service → repository → database
    }
}
```

### Performance Tuning & Optimization

**Startup time optimization:**
```yaml
spring:
  main:
    lazy-initialization: true  # Creates beans only when first used
    
  # Or lazy init for specific beans
  @Lazy
  @Bean
  public ExpensiveService expensiveService() {
      return new ExpensiveService();
  }
```

**JVM tuning for Spring Boot:**
```bash
java -jar \
  -Xms512m \              # Initial heap size
  -Xmx2g \                # Max heap size
  -XX:+UseG1GC \          # G1 garbage collector (low latency)
  -XX:MaxGCPauseMillis=100 \  # Target pause time
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/dumps \
  -jar myapp.jar
```

**Connection pool tuning (HikariCP):**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20  # (connections = ((core_count * 2) + spindles))
      minimum-idle: 5
      connection-timeout: 3000  # 3 seconds
      idle-timeout: 600000  # 10 minutes
      max-lifetime: 1800000  # 30 minutes
      leak-detection-threshold: 60000  # Log if connection held > 60s
```

### Application Context Hierarchy

```
Root Context (parent)
└── DispatcherServlet Context (child)
    ├── Inherits all beans from parent
    ├── Has its own web beans (controllers, view resolvers)
    └── Can override parent beans

Benefits:
- Shared beans (services, repositories) in parent
- Web-specific beans (controllers) in child
- Multiple DispatcherServlets can share parent context
```

```java
// Parent context configuration
@Configuration
@ComponentScan(basePackages = "com.myapp.service")
public class RootConfig {
    @Bean
    public DataSource dataSource() { ... }
    @Bean
    public TransactionManager transactionManager() { ... }
}

// Web context configuration
@Configuration
@ComponentScan(basePackages = "com.myapp.controller")
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    // Controllers, view resolvers, etc.
}
```


## 9. Top 50 Spring Boot Interview Questions — Complete Deep Dive

### Bean Injection & DI (Questions 1-8)

**Q1: What are the different ways to inject beans in Spring? Which is best?**

A: Spring supports 5 injection mechanisms:

1. **Constructor Injection** ✅ BEST
```java
@Service
public class OrderService {
    private final OrderRepository repo;
    private final PaymentGateway gateway;
    
    // Spring auto-wires (no @Autowired needed for single constructor)
    public OrderService(OrderRepository repo, PaymentGateway gateway) {
        this.repo = repo;
        this.gateway = gateway;
    }
}
```
Pros: Immutable (`final`), required deps explicit, easy to test, fails at startup
Cons: Verbose without Lombok

2. **Setter Injection**
```java
@Service
public class OrderService {
    private OrderRepository repo;
    
    @Autowired
    public void setRepository(OrderRepository repo) {
        this.repo = repo;
    }
}
```
Pros: Good for optional dependencies, allows reconfiguration
Cons: Mutable, dependencies not obvious, can be called multiple times

3. **Field Injection** ❌ AVOID
```java
@Service
public class OrderService {
    @Autowired
    private OrderRepository repo; // Hidden dependency!
}
```
Pros: Minimal boilerplate
Cons: Cannot use `final`, hard to test (requires reflection), hides dependencies, NPE risk at runtime

4. **Method Parameter Injection** (in `@Bean` methods)
```java
@Configuration
public class AppConfig {
    @Bean
    public OrderService orderService(OrderRepository repo, PaymentGateway gateway) {
        return new OrderService(repo, gateway);
    }
}
```
Pros: Explicit, works with `@Bean` methods
Cons: Only in `@Configuration` classes

5. **`@Lookup` Method Injection** (special case)
```java
@Component
public abstract class OrderProcessor {
    @Lookup
    protected abstract ShoppingCart createCart(); // Spring overrides
    
    public void process() {
        ShoppingCart cart = createCart(); // NEW prototype instance
    }
}
```
Use case: Getting prototype bean from singleton.

**Winner: Constructor injection** for production code. Use `@RequiredArgsConstructor` (Lombok) to eliminate boilerplate.

---

**Q2: What is the difference between `@Autowired`, `@Resource`, and `@Inject`?**

A:
| Annotation | Package | Resolution | Required attribute | Use case |
|------------|---------|------------|-------------------|----------|
| `@Autowired` | Spring | By type + qualifier | Yes (`required=false`) | Spring-only projects |
| `@Resource` | JSR-250 (javax.annotation) | By name, then type | No | Java EE environments |
| `@Inject` | JSR-330 (javax.inject) | By type | No | Framework-agnostic code |

Example:
```java
@Component
class MyService {
    @Resource(name = "stripeGateway")  // Looks for bean named "stripeGateway"
    private PaymentGateway byName;
    
    @Autowired @Qualifier("stripe")  // Looks for bean with qualifier "stripe"
    private PaymentGateway byQualifier;
    
    @Inject  // Standard DI - looks for single PaymentGateway bean
    private PaymentGateway byType;
}
```

---

**Q3: What is `@Qualifier` vs `@Primary`? When to use which?**

A: Both resolve `NoUniqueBeanDefinitionException` when multiple beans of same type exist.

```java
@Component
@Primary  // This is the DEFAULT choice
class StripeGateway implements PaymentGateway { }

@Component
@Qualifier("razorpay")  // Must be explicitly selected
class RazorpayGateway implements PaymentGateway { }
```

Usage:
```java
@Autowired
private PaymentGateway gateway;  // Gets StripeGateway (primary)

@Autowired
@Qualifier("razorpay")  // Explicitly gets RazorpayGateway
private PaymentGateway razorpay;
```

**Rule:**
- Use `@Primary` for the **default** implementation (used 80% of the time)
- Use `@Qualifier` for **explicit** overrides (used 20% of the time, or for testing)

---

**Q4: What is `@Component`, `@Service`, `@Repository`, `@Controller`? Difference?**

A: All are `@Component` stereotypes for component scanning. Differences:

| Annotation | Layer | Special Behavior |
|------------|-------|-----------------|
| `@Component` | Generic | None — basic stereotype |
| `@Service` | Business logic | None — semantic marker only |
| `@Repository` | Data access | **Exception translation**: wraps JDBC exceptions into Spring's `DataAccessException` hierarchy |
| `@Controller` | Web (MVC) | Enables request mapping, view resolution |
| `@RestController` | Web (REST) | `@Controller` + `@ResponseBody` — returns JSON/XML directly |

**`@Repository` exception translation:**
```java
@Repository
public class JdbcOrderRepository {
    public Order findById(Long id) {
        try {
            // JDBC call
        } catch (SQLException ex) {
            // @Repository causes Spring to translate to:
            throw new DataAccessResourceFailureException(ex);
        }
    }
}
```
Without `@Repository`: caller sees `SQLException` (checked, SQL-specific).
With `@Repository`: caller sees `DataAccessException` (runtime, Spring abstraction).

---

### Bean Lifecycle & Scopes (Questions 5-10)

**Q5: What is the Spring bean lifecycle? Walk through each step.**

A:
```
1. Bean definition loaded (from @Component, @Bean, etc.)
2. BeanFactoryPostProcessors run (e.g., @PropertySource, @Configuration)
3. Instantiation: constructor called
4. Dependency injection: @Autowired fields/parameters set
5. @PostConstruct / afterPropertiesSet() called
6. BeanPostProcessor.postProcessAfterInitialization() [AOP proxies applied here]
7. Bean ready for use
8. @PreDestroy / destroy-method() on context close
9. Bean destroyed
```

**Key interfaces:**
- `BeanFactoryPostProcessor`: Modifies bean definitions BEFORE beans are created
- `BeanPostProcessor`: Wraps/modifies beans AFTER creation (before init)
- `InstantiationAwareBeanPostProcessor`: Even earlier — can bypass constructor

---

**Q6: What are Spring bean scopes? When to use each?**

A:

| Scope | Instances | Lifecycle | Use Case | Thread-Safe? |
|-------|-----------|-----------|----------|--------------|
| `singleton` | 1 per context | Created at startup, destroyed at shutdown | Services, repositories, controllers | ✅ Yes (stateless) |
| `prototype` | New per injection | Created on demand, never destroyed by container | Stateful beans, builders | ❌ No |
| `request` | 1 per HTTP request | Created at request start, destroyed at end | Request-scoped data | ✅ Yes |
| `session` | 1 per HTTP session | Created at session start, destroyed at session end | Shopping cart, user preferences | ❌ No |
| `application` | 1 per ServletContext | Created at startup, destroyed at shutdown | Global counters, caches | ❌ No |
| `websocket` | 1 per WebSocket session | Created on connect, destroyed on disconnect | Chat sessions | ❌ No |

**Prototype in Singleton trap:**
```java
@Component
public class OrderService {
    private final ShoppingCart cart; // Prototype injected ONCE!
    
    public OrderService(ShoppingCart cart) {
        this.cart = cart; // Same instance for ALL requests
    }
}

@Component @Scope("prototype")
public class ShoppingCart { ... }
```

**Fix:** Use `@Lookup` or `ObjectProvider<ShoppingCart>`.

---

**Q7: What is `@Lazy`? When and why would you use it?**

A: `@Lazy` delays bean initialization until first use.

```java
@Component
@Lazy  // Not created at startup
public class ExpensiveReportService {
    public ExpensiveReportService() {
        System.out.println("Creating expensive service...");
    }
}

@Component
public class OrderService {
    private final ExpensiveReportService reportService;
    
    public OrderService(@Lazy ExpensiveReportService reportService) {
        this.reportService = reportService; // Proxy injected, not real bean
    }
    
    public void generateReport() {
        reportService.generate(); // Real bean created HERE (first use)
    }
}
```

**Use cases:**
1. **Break circular dependencies:**
```java
@Component
class A {
    @Autowired @Lazy
    private B b; // A created, B resolved later when used
}
@Component
class B {
    @Autowired
    private A a;
}
```

2. **Speed up startup time:** Don't initialize beans that might not be used in every request.

3. **Conditional heavy initialization:** Only load expensive resources when actually needed.

**How it works internally:**
- Spring injects a **proxy** (CGLIB or JDK proxy)
- Real bean created on first method call
- Subsequent calls use real bean

---

**Q8: What is `@PreDestroy` vs `@DisallowPostConstruct`?**

A:
- `@PreDestroy`: Called before bean destruction (cleanup, close connections)
```java
@Component
class DatabaseService {
    @PreDestroy
    public void cleanup() {
        connectionPool.close();
    }
}
```

- `@DisallowPostConstruct`: Prevents `@PostConstruct` from running (rarely used)
```java
@Component
class ConditionalInit {
    @DisallowPostConstruct  // Skip @PostConstruct
    @PostConstruct
    public void init() { ... }  // NOT called
}
```

**Bean destruction order:**
- Dependent beans destroyed first
- Then dependencies
- Opposite of creation order

---

### Auto-Configuration & Internals (Questions 9-15)

**Q9: How does Spring Boot auto-configuration work? Walk through the internals.**

A:
```
1. @SpringBootApplication on main class
   = @Configuration + @EnableAutoConfiguration + @ComponentScan

2. @EnableAutoConfiguration triggers:
   - ImportAutoConfigurationSelector
   - Loads AutoConfiguration.imports (Spring Boot 3) 
     OR META-INF/spring.factories (Spring Boot 2)

3. For each auto-configuration class:
   - Evaluate @Conditional annotations:
     * @ConditionalOnClass: is class on classpath?
     * @ConditionalOnMissingBean: does user bean exist?
     * @ConditionalOnProperty: does property match?
     * @ConditionalOnWebApplication: is this a web app?

4. Only configurations where ALL conditions pass are applied

5. User-defined @Configuration processed FIRST (higher priority)

6. @ConditionalOnMissingBean checks user beans FIRST
   → Auto-config backs off if user provides bean
```

**Debug auto-configuration:**
```bash
java -jar app.jar --debug
# Shows which auto-config applied/skipped and why
```

Or use Actuator:
```
GET /actuator/conditions
```

---

**Q10: What is the difference between `@Configuration` and `@Component`?**

A:
```java
@Configuration
class AppConfig {
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }
    
    @Bean
    public OrderService orderService(DataSource ds) {
        return new OrderService(ds);
    }
}
```

**Key difference:**
- `@Component`: Regular bean, no special behavior
- `@Configuration`: **Proxied by CGLIB** — ensures `@Bean` method calls return same instance

**Example of why proxying matters:**
```java
@Configuration
class AppConfig {
    @Bean
    public ServiceA serviceA() {
        return new ServiceA(serviceB()); // Calls proxied serviceB()
    }
    
    @Bean
    public ServiceB serviceB() {
        return new ServiceB();
    }
}
```
Without CGLIB proxy: `serviceA()` calls real `serviceB()` directly → creates NEW instance (wrong for singleton).
With CGLIB proxy: `serviceA()` calls proxy's `serviceB()` → returns SAME singleton instance.

**Note:** `@Component` classes do NOT get this proxy behavior.

---

**Q11: What happens when you have multiple `@Bean` methods of the same type? How does Spring resolve?**

A: Spring throws `BeanDefinitionOverrideException` by default (Spring Boot 2+).

Solutions:
1. **`@Primary`**: Mark one as default
2. **`@Qualifier`**: Explicitly select by name
3. **`@Bean(name = "customName")`: Give unique name
4. **Allow override** (not recommended): `spring.main.allow-bean-definition-overriding=true`

```java
@Configuration
class DataSourceConfig {
    @Bean
    @Primary  // Default choice
    public DataSource primaryDataSource() {
        return new HikariDataSource();
    }
    
    @Bean(name = "reportingDataSource")  // Explicit name
    public DataSource reportingDataSource() {
        return new TomcatDataSource();
    }
}

@Service
class OrderService {
    public OrderService(@Qualifier("reportingDataSource") DataSource ds) {
        // Gets reportingDataSource explicitly
    }
}
```

---

**Q12: What is `@Import` vs `@ComponentScan`? When to use which?**

A:
- `@ComponentScan`: Scans packages for `@Component` classes
- `@Import`: Explicitly imports specific configuration classes

```java
@SpringBootApplication
@ComponentScan(basePackages = "com.myapp")  // Scans for @Component, @Service, etc.
@Import({DataSourceConfig.class, SecurityConfig.class})  // Explicit imports
public class Application {}
```

**When to use `@Import`:**
1. Importing third-party configuration classes (no package scanning)
2. Conditional imports: `@Import(AuditConfig.class)` only if auditing enabled
3. Importing non-component classes: plain Java config without annotations

**`@ComponentScan` alternatives:**
```java
@ComponentScan(
    basePackages = {"com.myapp.service", "com.myapp.controller"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeFromScan.class)
    }
)
```

---

**Q13: What is `@ConditionalOnProperty`? How does it work?**

A: Conditionally creates bean based on property value.

```java
@Bean
@ConditionalOnProperty(
    name = "payment.gateway.enabled",      // Property key
    havingValue = "true",                  // Required value
    matchIfMissing = false                 // If property missing: don't create
)
public PaymentGateway stripeGateway() {
    return new StripeGateway();
}
```

**`matchIfMissing` examples:**
```java
// 1. Property MUST be present and = "true"
@ConditionalOnProperty(name = "feature.x", havingValue = "true", matchIfMissing = false)

// 2. If property missing → create bean anyway
@ConditionalOnProperty(name = "feature.x", havingValue = "true", matchIfMissing = true)

// 3. Create if property is NOT "false"
@ConditionalOnProperty(name = "feature.x", havingValue = "false", matchIfMissing = true)
// Only blocked if explicitly: feature.x=false
```

---

**Q14: Explain Spring Boot's `@ConditionalOnClass` vs `@ConditionalOnMissingBean` with examples.**

A:
- `@ConditionalOnClass`: Only create bean if specific class is on classpath
- `@ConditionalOnMissingBean`: Only create bean if no user-defined bean exists

```java
@Configuration
public class RedisAutoConfiguration {
    
    // Only if Jedis (Redis client) is on classpath
    @ConditionalOnClass(name = "redis.clients.jedis.Jedis")
    // Only if user hasn't defined RedisTemplate bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        return new RedisTemplate<>();
    }
}
```

**Real-world scenario:**
```java
// Your application:
@Bean
public RedisTemplate<String, String> myRedis() {
    return new RedisTemplate<>();
}

// Auto-configuration:
@Bean
@ConditionalOnMissingBean(RedisTemplate.class)  // Evaluates to FALSE
public RedisTemplate<String, String> redisTemplate() { ... }  // Skipped!
```

This is how Spring Boot allows user overrides without explicit exclusion.

---

### Profiles & Configuration (Questions 15-20)

**Q15: What is `@Profile`? How does it work with `application-{profile}.yml`?**

A: `@Profile` conditionally creates beans based on active profiles.

```yaml
# application-dev.yml
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:devdb
```

```java
@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {
    public void run(String... args) {
        // Only runs in dev profile
    }
}
```

**Activation:**
```bash
java -jar app.jar --spring.profiles.active=dev,prod
```

**Multiple profiles:**
```java
@Component
@Profile({"dev", "test"})  // Active in BOTH dev AND test
public class TestDataSeeder { }
```

---

**Q16: What is `@ConfigurationProperties` vs `@Value`? Which is better?**

A:
```java
// @Value approach (NOT recommended for complex config)
@Component
public class AppConfig {
    @Value("${app.name:DefaultApp}")
    private String name;
    
    @Value("${app.max-connections:10}")
    private int maxConnections;
}

// @ConfigurationProperties approach (PREFERRED)
@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {
    private String name;
    private int maxConnections = 10;  // Default
    private List<String> adminEmails = new ArrayList<>();
    
    // Getters and setters REQUIRED
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

**Comparison:**
| Feature | `@Value` | `@ConfigurationProperties` |
|---------|-----------|----------------------------|
| Type safety | ❌ String only | ✅ Any type |
| Validation | ❌ No | ✅ `@Valid`, `@Min`, `@Email` |
| Complex structures | ❌ No | ✅ Nested objects, lists, maps |
| Defaults | ✅ Yes (`:default`) | ✅ Yes (field initializers) |
| Relaxed binding | ❌ No | ✅ `max-connections` → `maxConnections` |
| IDE support | ❌ Poor | ✅ Refactoring, completion |

**Enable `@ConfigurationProperties`:**
```java
@ConfigurationProperties(prefix = "app")
@Component  // Or @Configuration
public class AppProperties { ... }
```

In Spring Boot 2.2+: `@ConfigurationProperties` automatically registered if annotated with `@Component` or `@Configuration`.

---

**Q17: What are Spring Boot profiles? Give 3 real-world use cases.**

A: Profiles allow environment-specific configuration.

**Use cases:**
1. **Environment-specific datasources:**
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:devdb

# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/mydb
```

2. **Feature flags:**
```java
@Component
@Profile("beta")  // Only in beta testing
public class BetaFeatureService { }

@Component
@Profile("!beta")  // NOT in beta (all other profiles)
public class StableFeatureService { }
```

3. **Mock services in testing:**
```java
@Configuration
public class TestConfig {
    @Bean
    @Profile("test")
    public PaymentService paymentService() {
        return new MockPaymentService(); // Fake for tests
    }
}
```

---

### Circular Dependencies & Bean Resolution (Questions 18-22)

**Q18: What causes circular dependencies in Spring? How do you fix them?**

A: **Circular dependency**: A depends on B, B depends on A.

**Example:**
```java
@Component
class OrderService {
    private final PaymentService paymentService;
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}

@Component
class PaymentService {
    private final OrderService orderService;  // Circular!
    public PaymentService(OrderService orderService) {
        this.orderService = orderService;
    }
}
```

**Error:**
```
BeanCurrentlyInCreationException: 
Error creating bean with name 'orderService':
Requested bean is currently in creation: Is there an unresolvable circular reference?
```

**Fixes (in order of preference):**

1. **Redesign (BEST):** Remove circular dependency
```java
// Introduce mediator
@Component
class OrderProcessor {
    private final OrderService orderService;
    private final PaymentService paymentService;
    
    public OrderProcessor(OrderService o, PaymentService p) {
        this.orderService = o;
        this.paymentService = p;
    }
    
    public void process() {
        orderService.create();
        paymentService.charge();
    }
}
```

2. **`@Lazy`:** Delay resolution
```java
@Component
class OrderService {
    private final PaymentService paymentService;
    
    public OrderService(@Lazy PaymentService paymentService) {
        this.paymentService = paymentService; // Proxy injected
    }
}
```

3. **Setter injection:** Break cycle
```java
@Component
class OrderService {
    private PaymentService paymentService;
    
    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService; // Called AFTER construction
    }
}
```

4. **`ObjectProvider<T>`:** Lazy lookup
```java
@Component
class OrderService {
    private final ObjectProvider<PaymentService> paymentProvider;
    
    public OrderService(ObjectProvider<PaymentService> paymentProvider) {
        this.paymentProvider = paymentProvider;
    }
    
    public void process() {
        PaymentService ps = paymentProvider.getIfAvailable(); // Resolved on use
    }
}
```

---

**Q19: What is `@Lazy` initialization? How does it work internally?**

A: Delays bean creation until first use.

```java
@Component
@Lazy
public class ExpensiveService { ... }

@Component
public class Consumer {
    private final ExpensiveService expensive;
    
    public Consumer(@Lazy ExpensiveService expensive) {
        this.expensive = expensive; // Gets a proxy, not real bean
    }
    
    public void doWork() {
        expensive.expensiveOperation(); // Real bean created HERE
    }
}
```

**Internal mechanism:**
1. Spring injects CGLIB proxy instead of real bean
2. Proxy extends `ExpensiveService`
3. First method call: proxy creates real bean, delegates call
4. Subsequent calls: use real bean directly

**When to use:**
1. Break circular dependencies
2. Speed up startup for rarely-used beans
3. Conditional expensive initialization

**Not for:**
- Beans used in every request (adds overhead)
- Critical path dependencies (can surprise with NPE)

---

**Q20: What is `ObjectProvider<T>`? How is it different from `@Lazy`?**

A: `ObjectProvider<T>` is a lazy lookup mechanism.

```java
@Component
class OrderService {
    private final ObjectProvider<PaymentGateway> gatewayProvider;
    
    public OrderService(ObjectProvider<PaymentGateway> gatewayProvider) {
        this.gatewayProvider = gatewayProvider;
    }
    
    public void process() {
        PaymentGateway gateway = gatewayProvider.getIfAvailable();
        // Returns actual bean or null if not found
        
        // Or throw exception:
        PaymentGateway gw = gatewayProvider.getIfUnique();
    }
}
```

**`@Lazy` vs `ObjectProvider`:**
| Feature | `@Lazy` | `ObjectProvider` |
|---------|---------|------------------|
| Returns | Proxy of bean | Actual bean or null |
| Use case | Circular deps, all calls | Optional deps, conditional use |
| Null safety | Never null (proxy) | Can be null |
| Performance | Proxy overhead | Direct access |

**Use `@Lazy` when:** You need to inject a mandatory dependency but break a cycle.
**Use `ObjectProvider` when:** Dependency is optional or only needed conditionally.

---

### Component Scanning & Bean Management (Questions 21-25)

**Q21: How does `@ComponentScan` work? What if beans are in different packages?**

A: `@ComponentScan` recursively scans packages for `@Component` classes.

```java
@SpringBootApplication(
    scanBasePackages = {"com.myapp.controller", "com.myapp.service"}
)
public class Application {}
```

**Default behavior:** Scans package of `@SpringBootApplication` class and all sub-packages.

**Problem:**
```
com.myapp          ← @SpringBootApplication here
com.myapp.controller
com.myapp.service

com.external          ← Bean NOT scanned!
com.external.config
```

**Solution:**
```java
@SpringBootApplication(
    scanBasePackages = {
        "com.myapp", 
        "com.external"  // Added
    }
)
```

**Exclude packages:**
```java
@ComponentScan(
    basePackages = "com.myapp",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        value = TestComponent.class
    )
)
```

---

**Q22: What is `@SpringBootApplication`? What three annotations does it combine?**

A: Convenience annotation combining:
1. `@Configuration`: Marks class as bean definition source
2. `@EnableAutoConfiguration`: Triggers auto-configuration
3. `@ComponentScan`: Scans for `@Component` classes

```java
@SpringBootApplication  // = all three
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

**Customization:**
```java
@SpringBootApplication(
    scanBasePackages = {"com.myapp"},  // Override @ComponentScan
    exclude = {DataSourceAutoConfiguration.class}  // Exclude auto-config
)
public class MyApplication {}
```

---

**Q23: What is `@Bean` vs `@Component`? When to use each?**

A:
| Aspect | `@Bean` | `@Component` |
|--------|---------|---------------|
| Location | Method in `@Configuration` class | On class |
| Usage | Third-party classes, complex init | Your own classes |
| Control | Full control over creation | Spring manages automatically |
| Example | `@Bean DataSource ds() { return new HikariDataSource(); }` | `@Component class MyService {}` |

**Why `@Bean` for third-party:**
```java
// You CAN'T add @Component to Stripe SDK class
// But you CAN wrap it in @Bean:
@Configuration
class PaymentConfig {
    @Bean
    public PaymentGateway stripeGateway() {
        return new StripeGateway(
            env.getProperty("stripe.key"),
            env.getProperty("stripe.secret")
        );
    }
}
```

---

**Q24: What is `@Primary` vs `@Qualifier`? When to use which?**

A: Both resolve ambiguity with multiple beans.

```java
@Component
@Primary  // Default when no qualifier specified
class StripeGateway implements PaymentGateway { }

@Component
@Qualifier("razorpay")  // Explicit name
class RazorpayGateway implements PaymentGateway { }
```

**Usage:**
```java
@Autowired
private PaymentGateway gateway;  // Gets StripeGateway (@Primary)

@Autowired
@Qualifier("razorpay")  // Explicitly get RazorpayGateway
private PaymentGateway razorpay;
```

**Strategy:**
- Mark most common impl as `@Primary`
- Use `@Qualifier` for specific overrides
- In tests: override with `@Primary` on mock

---

**Q25: What is `@RequiredArgsConstructor` (Lombok)? How does it relate to constructor injection?**

A: Lombok annotation that generates constructor for all `final` fields.

```java
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class OrderService {
    private final OrderRepository repo;      // Constructor param
    private final PaymentGateway gateway;     // Constructor param
    private final EmailService email;         // Constructor param
    
    // Lombok generates:
    // public OrderService(OrderRepository repo, PaymentGateway gateway, EmailService email) {
    //     this.repo = repo;
    //     this.gateway = gateway;
    //     this.email = email;
    // }
}
```

**Equivalent without Lombok:**
```java
@Service
public class OrderService {
    private final OrderRepository repo;
    private final PaymentGateway gateway;
    private final EmailService email;
    
    public OrderService(OrderRepository repo, PaymentGateway gateway, EmailService email) {
        this.repo = repo;
        this.gateway = gateway;
        this.email = email;
    }
}
```

**Benefits:**
- No boilerplate
- Constructor injection (immutable, testable)
- Single constructor → no `@Autowired` needed

---

### Advanced Auto-Wiring Scenarios (Questions 26-35)

**Q26: What is `@Autowired(required = false)`? What happens?**

A: Silently ignores missing beans instead of throwing exception.

```java
@Component
class OptionalFeature {
    @Autowired(required = false)
    private AnalyticsService analytics;  // Injected if exists, null if not
    
    public void track() {
        if (analytics != null) {
            analytics.trackEvent("order_created");
        }
        // Else: analytics is null, skip
    }
}
```

**Better alternative: `ObjectProvider`**
```java
@Component
class OptionalFeature {
    private final ObjectProvider<AnalyticsService> analyticsProvider;
    
    public OptionalFeature(ObjectProvider<AnalyticsService> analyticsProvider) {
        this.analyticsProvider = analyticsProvider;
    }
    
    public void track() {
        AnalyticsService analytics = analyticsProvider.getIfAvailable();
        if (analytics != null) {
            analytics.trackEvent("order_created");
        }
    }
}
```

---

**Q27: What is `@Resource` vs `@Autowired`? When to use `@Resource`?**

A:
- `@Autowired`: By type (then qualifier), Spring-specific
- `@Resource`: By name (then type), JSR-250 standard

```java
@Component
class PaymentService {
    @Resource(name = "stripeGateway")  // Looks for bean named "stripeGateway"
    private PaymentGateway gateway;
    
    @Autowired @Qualifier("stripe")  // Looks for bean with qualifier "stripe"
    private PaymentGateway gateway2;
}
```

**When to use `@Resource`:**
```java
@Component("stripeGateway")  // Bean name IS "stripeGateway"
class StripeGateway implements PaymentGateway { }

// @Resource(name = "stripeGateway") matches
// @Autowired @Qualifier("stripeGateway") also matches
```

**When `@Resource` fails:**
```java
@Component
@Qualifier("stripe")  // Qualifier is "stripe", bean name is "stripeGateway"
class StripeGateway implements PaymentGateway { }

// @Resource(name = "stripe") fails (no bean named "stripe")
// @Autowired @Qualifier("stripe") succeeds
```

---

**Q28: Can you autowire primitives or Strings? How?**

A: ❌ Cannot autowire primitives/Strings directly.

```java
@Component
class Config {
    @Autowired private String appName;     // ERROR: NoSuchBeanDefinitionException
    @Autowired private int maxRetries;     // ERROR
}
```

**✅ Solutions:**
```java
// 1. @Value (SpEL)
@Component
class Config {
    @Value("${app.name:DefaultApp}")  // From application.properties
    private String appName;
    
    @Value("${app.max-retries:3}")
    private int maxRetries;
}

// 2. @ConfigurationProperties (PREFERRED)
@ConfigurationProperties(prefix = "app")
@Component
public class AppConfig {
    private String name = "DefaultApp";
    private int maxRetries = 3;
    // Getters/setters
}
```

---

**Q29: What is `@Inject` (JSR-330)? How is it different from `@Autowired`?**

A: `@Inject` is part of Java Dependency Injection standard (JSR-330).

```java
import javax.inject.Inject;

@Component
class OrderService {
    private final OrderRepository repo;
    
    @Inject  // Standard annotation
    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }
}
```

**Differences:**
| Feature | `@Inject` | `@Autowired` |
|---------|-----------|--------------|
| Package | `javax.inject` (standard) | `org.springframework.beans.factory.annotation` (Spring) |
| Required | No required attribute | `required=false` supported |
| Qualifier | `@Named` (JSR-330) | `@Qualifier` (Spring) |
| Portability | ✅ Works with Guice, Dagger | ❌ Spring-only |

**When to use `@Inject`:**
- Writing framework-agnostic libraries
- Need to support multiple DI frameworks
- Java EE / Jakarta EE environments

**When to use `@Autowired`:**
- Spring-only applications (most common)
- Need `required=false`
- Need Spring-specific features

---

**Q30: What is `ObjectProvider<T>`? When would you use it instead of `@Autowired`?**

A: `ObjectProvider<T>` is a lazy bean lookup mechanism.

```java
@Component
class OrderProcessor {
    private final ObjectProvider<ShoppingCart> cartProvider;
    
    public OrderProcessor(ObjectProvider<ShoppingCart> cartProvider) {
        this.cartProvider = car
</think>
<write_to_file>
<path>fundamentals/spring-boot/autowiring/INTERVIEW-QUESTIONS.md</path>
<content>
# Spring Boot — Top 50 Interview Questions: Autowiring, DI, Bean Management

## Autowiring & Dependency Injection

### Bean Injection Methods

**Q: How many ways can you inject beans in Spring? List all with examples.**

A: **5 ways:**

1. **Constructor Injection** ✅ PREFERRED
```java
@Service
public class OrderService {
    private final OrderRepository repo;
    
    // Spring auto-wires (no @Autowired needed for single constructor)
    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }
}
```

2. **Setter Injection**
```java
@Service
public class OrderService {
    private OrderRepository repo;
    
    @Autowired
    public void setRepository(OrderRepository repo) {
        this.repo = repo;
    }
}
```

3. **Field Injection** ❌ NOT RECOMMENDED
```java
@Service
public class OrderService {
    @Autowired
    private OrderRepository repo;  // Hidden dependency!
}
```

4. **Method Parameter Injection** (in @Configuration)
```java
@Configuration
class AppConfig {
    @Bean
    public OrderService orderService(OrderRepository repo) {
        return new OrderService(repo);
    }
}
```

5. **@Lookup Method Injection** (special case)
```java
@Component
public abstract class OrderProcessor {
    @Lookup
    protected abstract ShoppingCart createCart();
    
    public void process() {
        ShoppingCart cart = createCart(); // NEW prototype each time
    }
}
```

---

**Q: Why is constructor injection better than field injection? Give 5 reasons.**

A:
1. **Immutability**: Can use `final` fields → thread-safe
2. **Explicit dependencies**: Constructor parameters = required deps (cannot be null)
3. **Testability**: No reflection needed → `new OrderService(mockRepo, mockGateway)`
4. **Fail at startup**: Missing bean → startup failure, not runtime NPE
5. **No `@Autowired` noise**: Spring 4.3+ infers for single constructor

---

**Q: What is the difference between `@Autowired`, `@Resource`, and `@Inject`?**

A:
| Annotation | Resolution | Required attr | Use case |
|------------|-----------|--------------|----------|
| `@Autowired` (Spring) | By type + qualifier | Yes (`false`) | Spring-only projects |
| `@Resource` (JSR-250) | By name, then type | No | Java EE environments |
| `@Inject` (JSR-330) | By type | No | Framework-agnostic (Guice, Dagger) |

```java
@Component
class MyService {
    @Resource(name = "stripeGateway")  // By NAME first
    private PaymentGateway byName;
    
    @Autowired @Qualifier("stripe")   // By TYPE + qualifier
    private PaymentGateway byQualifier;
    
    @Inject                            // Standard by TYPE
    private PaymentGateway byType;
}
```

---

## Bean Scopes & Lifecycle

**Q: What are all Spring bean scopes? When to use each?**

A:
| Scope | Instances | Use Case | Thread-Safe? |
|-------|-----------|----------|--------------|
| `singleton` | 1 per context | Services, repos | ✅ Yes (stateless) |
| `prototype` | New per injection | Stateful beans | ❌ No |
| `request` | 1 per HTTP request | Request data | ✅ Yes |
| `session` | 1 per HTTP session | Shopping cart | ❌ No |
| `application` | 1 per ServletContext | Global state | ❌ No |
| `websocket` | 1 per WS session | Chat sessions | ❌ No |

---

**Q: What happens when you inject a prototype bean into a singleton?**

A: Prototype created ONCE during singleton initialization. All singleton methods share same instance → thread-safety issue.

```java
@Component
public class OrderService {
    private final ShoppingCart cart; // Same cart for ALL users!
    
    public OrderService(ShoppingCart cart) {
        this.cart = cart;
    }
}

@Component @Scope("prototype")
public class ShoppingCart { ... }
```

**Fix:** Use `@Lookup` or `ObjectProvider<ShoppingCart>`.

---

**Q: What is `@Lazy` initialization? How does it work internally?**

A: Delays bean creation until first use. Spring injects a **CGLIB proxy**. Real bean created on first method call.

```java
@Component
@Lazy
public class ExpensiveService { }

@Component
public class Consumer {
    private final ExpensiveService service;
    
    public Consumer(@Lazy ExpensiveService service) {
        this.service = service; // Gets proxy, not real bean
    }
    
    public void doWork() {
        service.heavyOperation(); // Real bean created HERE
    }
}
```

**Use cases:**
1. Break circular dependencies
2. Speed up startup (don't init unused beans)
3. Conditional expensive initialization

---

## Component Scanning & Stereotypes

**Q: What is `@Component` vs `@Service` vs `@Repository`?**

A: All are `@Component` stereotypes. Differences:

| Annotation | Purpose | Special Behavior |
|------------|---------|-----------------|
| `@Component` | Generic bean | None |
| `@Service` | Business layer | Semantic only (no special behavior) |
| `@Repository` | Data access | **Exception translation**: JDBC → `DataAccessException` |
| `@Controller` | Web MVC | Request mapping, view resolution |
| `@RestController` | REST API | `@Controller` + `@ResponseBody` |

**`@Repository` exception translation:**
```java
@Repository
public class JdbcOrderRepo {
    public Order find(Long id) {
        try {
            // JDBC throws SQLException
        } catch (SQLException ex) {
            // Spring translates to DataAccessException
            throw new DataAccessResourceFailureException(ex);
        }
    }
}
```

---

**Q: What is `@ComponentScan`? What is the default base package?**

A: Scans packages for `@Component` classes.

```java
@SpringBootApplication(
    scanBasePackages = {"com.myapp", "com.external"}
)
public class Application {}
```

**Default:** Scans package of `@SpringBootApplication` class and ALL sub-packages.

**Problem:** Beans in `com.external` NOT scanned (outside root package).
**Solution:** Explicitly add to `scanBasePackages`.

---

## @Bean vs @Component

**Q: What is `@Bean` vs `@Component`? When to use each?**

A:
| Aspect | `@Bean` | `@Component` |
|--------|---------|--------------|
| Location | Method in `@Configuration` | On class directly |
| Use when | Third-party class, complex init | Your own classes |
| Control | Full control | Spring manages |
| Example | `@Bean DataSource ds() { return new HikariDataSource(); }` | `@Component class MyService {}` |

**Why `@Bean` for third-party:**
```java
// Can't add @Component to Stripe SDK
@Configuration
class PaymentConfig {
    @Bean
    public PaymentGateway stripe() {
        return new StripeGateway(env.getProperty("stripe.key"));
    }
}
```

---

## Multiple Beans & Disambiguation

**Q: What is `@Qualifier` vs `@Primary`? When to use which?**

A: Both resolve ambiguity with multiple beans of same type.

```java
@Component
@Primary  // DEFAULT when no qualifier
class StripeGateway implements PaymentGateway { }

@Component
@Qualifier("razorpay")  // Explicit
class RazorpayGateway implements PaymentGateway { }
```

**Usage:**
```java
@Autowired
private PaymentGateway gateway;  // Gets StripeGateway (primary)

@Autowired
@Qualifier("razorpay")  // Explicitly gets Razorpay
private PaymentGateway razorpay;
```

**Strategy:**
- `@Primary`: Mark most common impl (80% use case)
- `@Qualifier`: Explicit overrides (20% use case, tests)

---

**Q: What is `@Primary`? How does it differ from `@Qualifier`?**

A: `@Primary` marks default bean when multiple candidates exist and NO qualifier specified.

```java
@Component
@Primary
class StripeGateway implements PaymentGateway { }

@Component
class RazorpayGateway implements PaymentGateway { }

// In autowiring:
@Autowired
private PaymentGateway gateway;  // Gets StripeGateway (@Primary)

// Override:
@Autowired
@Qualifier("razorpayGateway")  // Explicit
private PaymentGateway specific;
```

**Key difference:**
- `@Primary`: Automatic default (implicit selection)
- `@Qualifier`: Explicit selection (must specify)

---

## Circular Dependencies

**Q: What causes circular dependencies? How do you fix them?**

A: **Circular dependency**: A requires B, B requires A.

```java
@Component
class A {
    private final B b;
    public A(B b) { this.b = b; }
}

@Component
class B {
    private final A a;
    public B(A a) { this.a = a; }  // Circular!
}
```

**Error:**
```
BeanCurrentlyInCreationException: 
A is currently in creation: circular reference
```

**4 Fix strategies (in order):**

1. **Redesign (BEST)** — introduce mediator:
```java
@Component
class OrderProcessor {
    private final A a;
    private final B b;
    
    public OrderProcessor(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
```

2. **`@Lazy`** — delay resolution:
```java
@Component
class A {
    private final B b;
    public A(@Lazy B b) { this.b = b; }  // Proxy injected
}
```

3. **Setter injection** — break cycle:
```java
@Component
class A {
    private B b;
    
    @Autowired
    public void setB(B b) { this.b = b; }  // Called AFTER A constructed
}
```

4. **`ObjectProvider<B>`** — lazy lookup:
```java
@Component
class A {
    private final ObjectProvider<B> bProvider;
    
    public A(ObjectProvider<B> bProvider) {
        this.bProvider = bProvider;
    }
    
    public void doSomething() {
        B b = bProvider.getIfAvailable(); // Resolved on use, not construction
    }
}
```

**Constructor injection surfaces cycles at startup (good!)**. Field injection hides cycles until runtime (bad!).

---

## Profiles & Configuration

**Q: What is `@Profile`? How does it work with `application-{profile}.yml`?**

A: `@Profile` conditionally creates beans based on active profiles.

```java
@Component
@Profile("prod")
class ProdPaymentService implements PaymentService { }

@Component
@Profile("dev")
class DevPaymentService implements PaymentService { }
```

**Activation:**
```bash
java -jar app.jar --spring.profiles.active=prod
# OR
# application.yml:
spring:
  profiles:
    active: prod
```

**Multiple profiles:**
```java
@Component
@Profile({"dev", "test"})  // Active in BOTH
class TestDataSeeder { }
```

**Negation:**
```java
@Component
@Profile("!prod")  // Active in ALL profiles EXCEPT prod
class NonProdService { }
```

---

**Q: What is `@ConfigurationProperties` vs `@Value`?**

A:
| Feature | `@Value` | `@ConfigurationProperties` |
|---------|-----------|----------------------------|
| Type safety | ❌ String only | ✅ Any type (int, List, Map) |
| Validation | ❌ No | ✅ `@Valid`, `@Min`, `@Email` |
| Complex config | ❌ No | ✅ Nested objects |
| Relaxed binding | ❌ No | ✅ `max-connections` → `maxConnections` |
| IDE support | ❌ Poor | ✅ Refactoring, completion |

**`@ConfigurationProperties` example:**
```java
@ConfigurationProperties(prefix = "app")
@Component
public class AppConfig {
    private String name = "DefaultApp";
    private int maxConnections = 10;
    private List<String> adminEmails = new ArrayList<>();
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

// application.yml:
# app:
#   name: MyApp
#   max-connections: 20
#   admin-emails: admin@myapp.com
```

---

## Auto-Configuration Internals

**Q: How does Spring Boot auto-configuration work?**

A:
```
1. @EnableAutoConfiguration triggers auto-config
2. SpringFactoriesLoader loads:
   - META-INF/spring.factories (Boot 2)
   - META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports (Boot 3)
3. Each auto-config has @Conditional annotations
4. Conditions evaluated: classpath, properties, existing beans
5. Only matching configs applied
6. User-defined beans take priority
```

**Key conditionals:**
- `@ConditionalOnClass`: Class on classpath?
- `@ConditionalOnMissingBean`: User bean doesn't exist?
- `@ConditionalOnProperty`: Property matches?
- `@ConditionalOnWebApplication`: Is web app?
- `@ConditionalOnExpression`: SpEL evaluates to true?

---

**Q: What is `@ConditionalOnMissingBean`? Why is it important?**

A: Only creates bean if NO user-defined bean of same type exists.

```java
@Configuration
public class DataSourceAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(DataSource.class)  // Key!
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
```

**Why critical:**
1. User defines `@Bean DataSource` → auto-config skipped
2. User override without explicit exclusion
3. Allows customization without fighting framework

**Debug:**
```bash
java -jar app.jar --debug
# Shows which auto-config applied/skipped
```

---

## Advanced Scenarios

**Q: What is `@Autowired(required = false)`? Better alternative?**

A: Silently ignores missing beans.

```java
@Component
class OptionalService {
    @Autowired(required = false)
    private AnalyticsService analytics;  // null if not found
}
```

**Better: `ObjectProvider<T>`**
```java
@Component
class OptionalService {
    private final ObjectProvider<AnalyticsService> provider;
    
    public OptionalService(ObjectProvider<AnalyticsService> provider) {
        this.provider = provider;
    }
    
    public void track() {
        AnalyticsService analytics = provider.getIfAvailable();
        if (analytics != null) {
            analytics.track();
        }
    }
}
```

---

**Q: What is `@Lookup` method injection? When to use?**

A: For obtaining prototype beans from singletons.

```java
@Component
public abstract class OrderProcessor {
    
    @Lookup  // Spring overrides this method
    protected abstract ShoppingCart getCart();
    
    public void process(Order order) {
        ShoppingCart cart = getCart(); // NEW prototype each call
        cart.add(order);
    }
}

@Component @Scope("prototype")
class ShoppingCart {
    private List<Item> items = new ArrayList<>();
}
```

**Without `@Lookup`:** Prototype injected ONCE (wrong).
**With `@Lookup`:** New prototype on every call (correct).

---

**Q: Can you autowire interfaces with multiple implementations? How?**

A: Use `@Qualifier` or `@Primary`.

```java
interface PaymentGateway { }

@Component @Qualifier("stripe")
class StripeGateway implements PaymentGateway { }

@Component @Qualifier("razorpay")
class RazorpayGateway implements PaymentGateway { }

@Component
class OrderService {
    // Explicit selection
    public OrderService(@Qualifier("stripe") PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

---

**Q: What is `@Primary` vs `@Qualifier` in depth?**

A:
- `@Primary`: Default when NO qualifier specified
- `@Qualifier`: Explicit override

```java
@Component
@Primary  // Default
class StripeGateway implements PaymentGateway { }

@Component
@Qualifier("razorpay")
class RazorpayGateway implements PaymentGateway { }

// Injection:
@Autowired
private PaymentGateway gateway;  // StripeGateway (@Primary)

@Autowired
@Qualifier("razorpay")  // RazorpayGateway
private PaymentGateway specific;
```

**In tests:**
```java
@TestConfiguration
class TestConfig {
    @Bean
    @Primary  // Override real bean in tests
    public PaymentGateway paymentGateway() {
        return mock(PaymentGateway.class);
    }
}
```

---

## Constructor Injection Deep Dive

**Q: Constructor injection vs Setter injection — detailed comparison?**

A:
| Aspect | Constructor | Setter/Field |
|--------|-------------|--------------|
| Dependencies | Required (explicit) | Optional (can be null) |
| Mutability | Immutable (`final`) | Mutable |
| Testability | Easy (no reflection) | Hard (need reflection) |
| Circular deps | Surfaces at startup | Can hide cycles |
| NPE risk | At startup | At runtime |
| Use case | Required deps | Optional deps |

**Constructor injection**
- ✅ Preferred for required dependencies
- Enables immutability (`final` fields)
- Surfaces circular dependencies at compile time
- Easy to test
- Fails fast on missing beans at startup

**Setter injection**
- Use for optional dependencies
- Allows reconfiguration after bean creation
- Can break circular dependencies
- Used for `@Autowired` on methods

---

**Q: You have a `@Component` with two constructors: no-arg and one with `@Autowired`. Which does Spring use?**

A: **Spring 4.3+**: If there is exactly one constructor, `@Autowired` is optional. Spring uses that constructor.

If there are multiple constructors:
- If ONE has `@Autowired`, Spring uses that one.
- If NONE have `@Autowired` and there is a single constructor, Spring uses it.
- If NONE have `@Autowired` and there are MULTIPLE constructors (including no-arg), Spring uses the **no-arg constructor** and does **not** autowire dependencies → fields remain `null` → `NullPointerException` later.

**Conclusion:** Always use either a single constructor (no `@Autowired` needed) or annotate the desired constructor explicitly with `@Autowired` to avoid ambiguity.

---

**Q: Spring Boot 2.6+ throws `BeanCurrentlyInCreationException` for circular references by default. How do you fix this?**

A: Spring Boot 2.6 changed default: `spring.main.allow-circular-references=false`.

**Fixes (in order):**

1. **Redesign (BEST):** Extract shared logic into third bean.
```java
@Component
class OrderProcessor {
    private final OrderService orderService;
    private final PaymentService paymentService;
    
    public OrderProcessor(OrderService o, PaymentService p) {
        this.orderService = o;
        this.paymentService = p;
    }
    
    public void process() {
        orderService.create();
        paymentService.charge();
    }
}
```

2. **`@Lazy` on one side:**
```java
@Component
class A {
    private final B b;
    public A(@Lazy B b) { this.b = b; }  // Proxy injected
}
```

3. **Setter injection for one side:**
```java
@Component
class A {
    private B b;
    @Autowired
    public void setB(B b) { this.b = b; }
}
```

4. **`ObjectProvider<B>`:**
```java
@Component
class A {
    private final ObjectProvider<B> bProvider;
    public A(ObjectProvider<B> bProvider) { this.bProvider = bProvider; }
    public void doWork() {
        B b = bProvider.getIfAvailable();  // Resolved on use
    }
}
```

---

**Q: What is `@Configuration` vs `@Component`? Why does it matter for `@Bean` methods?**

A:
- `@Component`: Regular singleton bean.
- `@Configuration`: **CGLIB-proxied** to ensure `@Bean` method calls return the same instance.

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

Without CGLIB proxy: `serviceA()` calls real `serviceB()` directly → creates a NEW instance (wrong for singletons).
With CGLIB: `serviceA()` calls proxy's `serviceB()` → returns SAME singleton.

**Note:** `@Component` classes do NOT get this proxy behavior.

---

**Q: What is `@SpringBootApplication` composed of? Can you customize it?**

A: It combines three annotations:
1. `@Configuration`: Marks class as bean definition source
2. `@EnableAutoConfiguration`: Triggers auto-configuration
3. `@ComponentScan`: Scans for `@Component` classes

```java
@SpringBootApplication(
    scanBasePackages = {"com.myapp"},  // Override scan
    exclude = {DataSourceAutoConfiguration.class}  // Exclude auto-config
)
public class MyApplication {}
```

---

**Q: What is `@Profile`? How does it work with `application-{profile}.yml`?**

A: `@Profile` conditionally creates beans based on active profiles.

```java
@Component
@Profile("prod")
class ProdPaymentService implements PaymentService { }
```

```yaml
# application.yml
spring:
  profiles:
    active: ${APP_PROFILE:dev}
```

**Activation:**
```bash
java -jar app.jar --spring.profiles.active=prod
```

**Multiple profiles:**
```java
@Component @Profile({"dev", "test"})  // Active in BOTH
class TestDataSeeder { }
```

**Negation:**
```java
@Component @Profile("!prod")  // All EXCEPT prod
class NonProdService { }
```

---

**Q: What is `@ConditionalOnProperty`? How does `matchIfMissing` work?**

A: Conditionally creates bean based on property value.

```java
@Bean
@ConditionalOnProperty(
    name = "payment.gateway.enabled",
    havingValue = "true",
    matchIfMissing = false  // Property MUST be present
)
public PaymentGateway stripeGateway() { ... }
```

**`matchIfMissing` examples:**
- `matchIfMissing = false`: Property MUST be `"true"` to create bean.
- `matchIfMissing = true`: If property missing → create bean anyway.

---

**Q: What is `@ConditionalOnClass` vs `@ConditionalOnMissingBean`?**

A:
- `@ConditionalOnClass`: Only if specific class is on classpath.
- `@ConditionalOnMissingBean`: Only if no user bean of that type exists.

```java
@Configuration
public class RedisAutoConfiguration {
    
    @ConditionalOnClass(name = "redis.clients.jedis.Jedis")
    @ConditionalOnMissingBean(RedisTemplate.class)
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        return new RedisTemplate<>();
    }
}
```

**Why critical:** User defines `@Bean RedisTemplate` → auto-config skips. Allows customization without explicit exclusion.

---

**Q: What is `@Value` vs `@ConfigurationProperties`? Which is better?**

A:
| Feature | `@Value` | `@ConfigurationProperties` |
|---------|-----------|----------------------------|
| Type safety | ❌ String only | ✅ Any type |
| Validation | ❌ No | ✅ `@Valid`, `@Min`, `@Email` |
| Complex config | ❌ No | ✅ Nested objects, lists, maps |
| Relaxed binding | ❌ No | ✅ `max-connections` → `maxConnections` |
| IDE support | ❌ Poor | ✅ Refactoring, completion |

**`@ConfigurationProperties` example:**
```java
@ConfigurationProperties(prefix = "app")
@Component
public class AppConfig {
    private String name = "DefaultApp";
    private int maxConnections = 10;
    private List<String> adminEmails = new ArrayList<>();
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

---

**Q: What is `@Autowired(required = false)`? What is a better alternative?**

A: Silently ignores missing beans.

```java
@Component
class OptionalService {
    @Autowired(required = false)
    private AnalyticsService analytics;  // null if not found
}
```

**Better: `ObjectProvider<T>`**
```java
@Component
class OptionalService {
    private final ObjectProvider<AnalyticsService> provider;
    
    public OptionalService(ObjectProvider<AnalyticsService> provider) {
        this.provider = provider;
    }
    
    public void track() {
        AnalyticsService analytics = provider.getIfAvailable();
        if (analytics != null) {
            analytics.track();
        }
    }
}
```

---

**Q: What is Spring's event publishing mechanism? How to use it?**

A: Decouple components using events.

```java
// 1. Define event
public record OrderCreatedEvent(Long orderId, String customerId) {}

// 2. Publish event
@Component
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;
    
    public Order create(OrderRequest req) {
        Order order = save(req);
        eventPublisher.publishEvent(new OrderCreatedEvent(order.getId(), order.getCustomerId()));
        return order;
    }
}

// 3. Listen for event
@Component
public class OrderNotificationListener {
    
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        sendEmail(event.customerId(), "Order " + event.orderId() + " created!");
    }
    
    // Async event listener
    @EventListener
    @Async  // Runs in background thread
    public void onOrderCreatedAsync(OrderCreatedEvent event) {
        processInBackground(event);
    }
}
```

---

**Q: What are `BeanFactoryPostProcessor` and `BeanPostProcessor`?**

A:
- `BeanFactoryPostProcessor`: Modifies bean definitions **before** beans are created.
- `BeanPostProcessor`: Wraps/modifies beans **after** creation (before init).

```java
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // Called after bean init (@PostConstruct)
        // Can wrap bean in proxy, modify properties, etc.
        if (bean instanceof OrderService) {
            return new OrderServiceProxy((OrderService) bean);
        }
        return bean;
    }
}
```

**Use cases:**
- AOP proxying (transactions, security)
- Dependency injection field resolution
- Custom initialization logic

---

**Q: What is `@Import` vs `@ComponentScan`? When to use which?**

A:
- `@ComponentScan`: Recursively scans packages for `@Component` classes.
- `@Import`: Explicitly imports specific configuration classes.

```java
@SpringBootApplication
@ComponentScan(basePackages = "com.myapp")
@Import({DataSourceConfig.class, SecurityConfig.class})  // Explicit imports
public class Application {}
```

**When to use `@Import`:**
1. Importing third-party config classes
2. Conditional imports
3. Importing non-component classes

---

**Q: What is `@Primary` vs `@Qualifier`? When to use which?**

A: Both resolve ambiguity with multiple beans of same type.

```java
@Component
@Primary  // Default when no qualifier
class StripeGateway implements PaymentGateway { }

@Component
@Qualifier("razorpay")  // Explicit
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

**Q: What is `@RequiredArgsConstructor` (Lombok)? How does it relate to constructor injection?**

A: Lombok annotation that generates constructor for all `final` fields.

```java
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class OrderService {
    private final OrderRepository repo;
    private final PaymentGateway gateway;
    
    // Lombok generates:
    // public OrderService(OrderRepository repo, PaymentGateway gateway) {
    //     this.repo = repo;
    //     this.gateway = gateway;
    // }
}
```

**Benefits:**
- No boilerplate
- Constructor injection (immutable, testable)
- Single constructor → no `@Autowired` needed

---

**Q: Can you autowire primitives or Strings? How?**

A: ❌ Cannot autowire primitives/Strings directly.

```java
@Component
class Config {
    @Autowired private String appName;   // ERROR
    @Autowired private int maxRetries;   // ERROR
}
```

**✅ Solutions:**
```java
// 1. @Value
@Component
class Config {
    @Value("${app.name:DefaultApp}")
    private String appName;
    
    @Value("${app.max-retries:3}")
    private int maxRetries;
}

// 2. @ConfigurationProperties (PREFERRED)
@ConfigurationProperties(prefix = "app")
@Component
public class AppConfig {
    private String name = "DefaultApp";
    private int maxRetries = 3;
}
```

---

**Q: What is `@Inject` (JSR-330)? How is it different from `@Autowired`?**

A: `@Inject` is part of Java Dependency Injection standard (JSR-330).

```java
import javax.inject.Inject;

@Component
class OrderService {
    private final OrderRepository repo;
    
    @Inject  // Standard annotation
    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }
}
```

| Feature | `@Inject` | `@Autowired` |
|---------|-----------|--------------|
| Package | `javax.inject` (standard) | Spring-specific |
| Required | No `required` | `required=false` supported |
| Qualifier | `@Named` | `@Qualifier` |
| Portability | ✅ Guice, Dagger | ❌ Spring-only |

**Use `@Inject` when:** Writing framework-agnostic libraries.
**Use `@Autowired` when:** Spring-only projects (most common).

---

**Q: What is `@Bean` vs `@Component`? When to use each?**

A:
| Aspect | `@Bean` | `@Component` |
|--------|---------|--------------|
| Location | Method in `@Configuration` | On class directly |
| Use when | Third-party class, complex init | Your own classes |
| Control | Full control | Spring manages |
| Example | `@Bean DataSource ds() { ... }` | `@Component class MyService {}` |

**Why `@Bean` for third-party:**
```java
// Can't add @Component to Stripe SDK
@Configuration
class PaymentConfig {
    @Bean
    public PaymentGateway stripe() {
        return new StripeGateway(env.getProperty("stripe.key"));
    }
}
```

---

**Q: What is `@Scope("prototype")`? What is the "prototype in singleton" problem?**

A: Prototype creates new instance on every injection.

```java
@Component @Scope("prototype")
class ShoppingCart { }

@Component
class OrderService {
    private final ShoppingCart cart;  // Prototype injected ONCE!
    
    public OrderService(ShoppingCart cart) {
        this.cart = cart;  // Same instance for ALL requests
    }
}
```

**Fix:** Use `@Lookup` or `ObjectProvider<ShoppingCart>`.

---

**Q: What is `@Lookup` method injection? When to use?**

A: For obtaining prototype beans from singletons.

```java
@Component
public abstract class OrderProcessor {
    @Lookup  // Spring overrides this method
    protected abstract ShoppingCart getCart();
    
    public void process(Order order) {
        ShoppingCart cart = getCart(); // NEW prototype each call
        cart.add(order);
    }
}
```

**Without `@Lookup`:** Prototype injected ONCE (wrong).
**With `@Lookup`:** New prototype on every call (correct).

---

## 10. Final 30-Second Answer

**Spring Boot**: `@SpringBootApplication` = @Configuration + @EnableAutoConfiguration + @ComponentScan. Auto-config uses `@ConditionalOnMissingBean` for overrides. **DI**: 5 ways to inject — constructor (best), setter, field (avoid), `@Bean` params, `@Lookup`. **Scopes**: singleton (default), prototype, request, session, application. **Sterotypes**: `@Component`, `@Service`, `@Repository` (exception translation), `@Controller`. **Disambiguation**: `@Primary` (default), `@Qualifier` (explicit). **Circular deps**: Constructor injection surfaces at startup — redesign to avoid. **Auto-config**: `@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`. **Config**: `@ConfigurationProperties` (type-safe) vs `@Value` (simple). **Lazy**: `@Lazy` breaks cycles, speeds startup. **Prototype in singleton**: Use `@Lookup`.

## 11. Final 30-Second Answer

**Spring Boot**: `@SpringBootApplication` = @Configuration + @EnableAutoConfiguration + @ComponentScan. Auto-config uses `@ConditionalOnMissingBean` to allow overrides. **REST**: `@RestController`, `@Valid` + `@RequestBody`, `@RestControllerAdvice` for exceptions, pagination with `Pageable`, status codes (201 Created, 404 Not Found). **Security**: OAuth2 Resource Server (`issuer-uri` + `JwtDecoder`), OAuth2 Client (`oauth2Login()`), `@PreAuthorize`, CORS + OPTIONS. **Filters**: `OncePerRequestFilter` + `@Order` for position. **WebFlux**: `Flux<T>` (many), `Mono<T>` (one), `WebClient`, `Schedulers` for thread pools. **GraphQL**: `@SchemaMapping`, `@MutationMapping`, schema-first. **Profiles**: `application-{profile}.yml`, `@Profile` on beans. **Bean scopes**: singleton (default), prototype (new each time), `@Lookup` for prototype from singleton.
A: Spring Boot is a framework built on top of Spring that simplifies application development through auto-configuration, embedded servers, and starter dependencies. Spring Framework is the underlying IoC container + modules. Spring Boot makes Spring "just work" without XML configuration.

Q: What is the difference between @RestController and @Controller?
A: `@RestController` = `@Controller` + `@ResponseBody` on all methods. Returns JSON/XML directly to HTTP response. `@Controller` returns view names (server-rendered pages with Thymeleaf, JSP).

Q: What are Spring Boot starters?
A: Pre-configured dependency groups that bundle related dependencies. `spring-boot-starter-web` includes Tomcat, Spring MVC, Jackson. `spring-boot-starter-data-jpa` includes Hibernate, HikariCP, Spring Data JPA. Avoid version conflicts — starters align dependency versions.

Q: What is Spring Data JPA?
A: Abstractions over JPA that reduce boilerplate. `JpaRepository` gives you CRUD + paging + sorting for free. Method name parsing: `findByCustomerIdAndStatus()` auto-generates query.

### Intermediate
Q: How does Spring Boot's auto-configuration work?
A: `@EnableAutoConfiguration` scans classpath for `AutoConfiguration.imports`. Each configuration class has `@Conditional` annotations (`@ConditionalOnClass`, `@ConditionalOnMissingBean`). If conditions match, beans are created. User-defined beans take precedence — auto-config checks `@ConditionalOnMissingBean` and backs off if user bean exists.

Q: What is the difference between @Component, @Service, @Repository, @Controller?
A: All are `@Component` stereotypes. `@Repository` adds exception translation (JDBC → DataAccessException). `@Service` is semantic (no special behavior). `@Controller`/`@RestController` enable web layer.

Q: How do you handle validation in Spring Boot REST APIs?
A: Add `@Valid` or `@Validated` on `@RequestBody`. Use Jakarta Validation annotations (`@NotBlank`, `@Min`, `@Email`). Handle `MethodArgumentNotValidException` in `@RestControllerAdvice`.

### Senior
Q: Design a rate limiter using Spring Boot filters. How would you make it distributed?
A: Local rate limiter: `OncePerRequestFilter` + in-memory `ConcurrentHashMap` (not distributed-safe). Distributed: use Redis with `SET key value NX EX 60` (atomic). Or use Bucket4j with Redis extension. Filter checks Redis before proceeding.

Q: How would you implement a global exception handler with different HTTP status codes?
A: `@RestControllerAdvice` + `@ExceptionHandler` methods. Map business exceptions to status codes (404 for not found, 409 for conflict, 422 for validation). Always return structured error response: `{status, message, details, timestamp}`.

### Tricky / 7+ Years Experience
Q: How would you implement GraphQL in Spring Boot? What are the trade-offs vs REST?
A: Spring for GraphQL + `@SchemaMapping`/`@MutationMapping` annotations. Schema-first approach: define `schema.graphqls`. Controllers resolve fields. Trade-off: over-fetching eliminated, single endpoint, but no HTTP caching, steeper learning curve. Use when: mobile apps (save bandwidth), complex data graphs. Use REST when: simple CRUD, caching important.

Q: When would you use WebFlux over Spring MVC? What are the performance implications?
A: WebFlux: 10K+ concurrent connections, streaming, non-blocking I/O (reactive DB clients). Spring MVC: simpler, blocking I/O (JDBC), wider ecosystem. WebFlux uses Netty (event-loop); Spring MVC uses Tomcat (thread-per-request). WebFlux handles more concurrent users with less memory, but code is harder to debug (reactor stack traces).

Q: You need to add an OAuth2 resource server to a Spring Boot API. Walk through the configuration.
A: Add `spring-boot-starter-oauth2-resource-server`. Configure `issuer-uri` (or `jwk-set-uri`). Spring Boot auto-configures `JwtDecoder`. Add `oauth2ResourceServer()` to security filter chain. Authorities mapped from JWT claims. Use `@EnableMethodSecurity` + `@PreAuthorize("hasRole('ADMIN')")`.

Q: How do you implement CORS in Spring Boot? Why does OPTIONS preflight fail with 401?
A: Configure via `WebMvcConfigurer.addCorsMappings()` or Spring Security's `cors()`. Preflight (OPTIONS) fails if security requires auth for OPTIONS. Fix: `.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()`. Browser sends OPTIONS before actual request — if 401, actual request blocked.

Q: You have a filter chain in Spring Security. How do you add a custom filter at a specific position?
A: `http.addFilterBefore(new MyFilter(), UsernamePasswordAuthenticationFilter.class)`. Or `addFilterAfter()`, `addFilterAt()`. Position matters: authentication filters should come before authorization filters. `OncePerRequestFilter` is the base class for custom filters.

## 10. Final 30-Second Answer

**Spring Boot**: `@SpringBootApplication` = @Configuration + @EnableAutoConfiguration + @ComponentScan. Auto-config uses `@ConditionalOnMissingBean` to allow overrides. **REST**: `@RestController`, `@Valid` + `@RequestBody`, `@RestControllerAdvice` for exceptions, pagination with `Pageable`, status codes (201 Created, 404 Not Found). **Security**: OAuth2 Resource Server (`issuer-uri` + `JwtDecoder`), OAuth2 Client (`oauth2Login()`), `@PreAuthorize`, CORS + OPTIONS. **Filters**: `OncePerRequestFilter` + `@Order` for position. **WebFlux**: `Flux<T>` (many), `Mono<T>` (one), `WebClient`, `Schedulers` for thread pools. **GraphQL**: `@SchemaMapping`, `@MutationMapping`, schema-first. **Profiles**: `application-{profile}.yml`, `@Profile` on beans. **Bean scopes**: singleton (default), prototype (new each time), `@Lookup` for prototype from singleton.