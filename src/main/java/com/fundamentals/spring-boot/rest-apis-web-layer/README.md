# REST APIs & Web Layer — Complete Deep Dive

## 1. Why This Concept Matters

REST APIs are the primary way microservices and web clients communicate. Spring MVC provides the annotations and infrastructure to build RESTful services — request mapping, parameter binding, validation, exception handling, CORS, file upload, pagination, and API versioning. Interviewers test these extensively because every backend role involves building and consuming APIs. Understanding the full web layer — from `@RequestParam` to `@ControllerAdvice` — is essential for building production-grade APIs.

Misunderstanding the web layer causes:
- Exposed internal implementation details (leaking entities in responses)
- HTTP 500 instead of proper 400/404/409 for validation failures
- CORS errors blocking legitimate frontend requests
- Insecure endpoints allowing unauthorized data access
- N+1 queries triggered by serializing lazy-loaded JPA associations
- Unwanted data exposure from serializing `Optional` or `LazyInitializationException`

## 2. Basic Meaning

Spring MVC is built on the **Front Controller** pattern — `DispatcherServlet` intercepts all HTTP requests and routes them to the appropriate `@Controller` method based on URL, HTTP method, headers, and parameters.

Key vocabulary:
- **DispatcherServlet**: the front controller that routes requests to handlers
- **HandlerMapping**: maps requests to controller methods
- **HandlerAdapter**: invokes the controller method and adapts the result
- **ViewResolver**: resolves logical view names to actual views (JSP, Thymeleaf)
- **HttpMessageConverter**: converts HTTP request/response bodies to/from Java objects (Jackson for JSON)
- **Content Negotiation**: determines response format based on `Accept` header, query param, or URL suffix
- **@ControllerAdvice**: global exception handler across all controllers
- **Filter vs Interceptor**: Filter (before DispatcherServlet) vs Interceptor (before controller method)

## 3. Core Annotations

| Annotation | Purpose | Example |
|-----------|---------|---------|
| `@RequestMapping` | Generic request mapping | `@RequestMapping("/api/users")` |
| `@GetMapping` | GET requests | `@GetMapping("/{id}")` |
| `@PostMapping` | POST requests | `@PostMapping` |
| `@PutMapping` | PUT (full update) | `@PutMapping("/{id}")` |
| `@PatchMapping` | PATCH (partial update) | `@PatchMapping("/{id}")` |
| `@DeleteMapping` | DELETE requests | `@DeleteMapping("/{id}")` |
| `@RequestParam` | Query parameters | `?page=1&size=20` |
| `@PathVariable` | URI template variables | `/users/{id}` |
| `@RequestBody` | Request body (JSON) | POST/PUT body |
| `@ResponseBody` | Response body (JSON) | Automatically serialized |
| `@ResponseStatus` | HTTP status code | `@ResponseStatus(HttpStatus.CREATED)` |

## 4. Parameter Binding

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    // === 1. PATH VARIABLE ===
    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId) {
        return orderService.findById(orderId);
    }
    
    // Multiple path variables
    @GetMapping("/{year}/{month}")
    public List<Order> getByMonth(@PathVariable int year, @PathVariable int month) {
        return orderService.findByMonth(year, month);
    }
    
    // === 2. QUERY PARAMETERS ===
    @GetMapping
    public List<Order> searchOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return orderService.search(status, page, size);
    }
    
    // === 3. REQUEST BODY ===
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }
    
    // === 4. REQUEST HEADERS ===
    @GetMapping("/export")
    public ResponseEntity<Resource> exportOrders(
            @RequestHeader("Accept") String acceptType,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {
        return orderService.export(acceptType);
    }
    
    // === 5. PAGINATION (Spring Data) ===
    @GetMapping("/paginated")
    public Page<Order> getOrders(Pageable pageable) {
        // Auto-binds: ?page=0&size=20&sort=createdAt,desc
        return orderRepository.findAll(pageable);
    }
}
```

## 5. Request/Response DTOs & Validation

```java
// === REQUEST DTO with validation ===
public class CreateOrderRequest {
    @NotBlank(message = "Customer email is required")
    @Email
    private String customerEmail;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotEmpty
    private List<@NotBlank String> itemIds;
    
    @Pattern(regexp = "USD|EUR|GBP", message = "Currency must be USD, EUR, or GBP")
    private String currency;
    
    // getters/setters
}

// === RESPONSE DTO (never expose entities directly) ===
public class OrderResponse {
    private Long id;
    private String customerEmail;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    
    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setAmount(order.getAmount());
        response.setStatus(order.getStatus().name());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}

// === VALIDATION ERROR HANDLING ===
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return Map.of("status", 400, "errors", errors);
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(EntityNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);
        return Map.of("error", "Internal server error");
    }
}
```

## 6. CORS Configuration

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://myapp.com", "https://admin.myapp.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600); // Cache preflight response for 1 hour
    }
}
```

## 7. File Upload & Download

```java
@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @PostMapping("/upload")
    public FileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Path.of("uploads", filename);
        Files.createDirectories(path.getParent());
        file.transferTo(path); // Save to disk
        
        return new FileResponse(filename, file.getContentType(), file.getSize());
    }
    
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Path path = Path.of("uploads", filename);
        Resource resource = new UrlResource(path.toUri());
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + filename + "\"")
            .body(resource);
    }
}
```

## 8. API Versioning

```java
// Strategy 1: URI versioning (most common)
@RestController
@RequestMapping("/api/v1/orders")
public class OrderControllerV1 { }

@RestController
@RequestMapping("/api/v2/orders")
public class OrderControllerV2 { }

// Strategy 2: Header versioning (content negotiation)
@GetMapping(headers = "X-API-Version=1")
public Order getOrderV1() { }

@GetMapping(headers = "X-API-Version=2")
public Order getOrderV2() { }

// Strategy 3: Accept header versioning
@GetMapping(produces = "application/vnd.myapp.v1+json")
public Order getOrderV1() { }
```

## 9. Idempotency

```java
@PostMapping("/payments")
public ResponseEntity<PaymentResponse> processPayment(
        @RequestBody PaymentRequest request,
        @RequestHeader("Idempotency-Key") String idempotencyKey) {
    
    // Check if this key was already processed
    PaymentResponse existing = paymentService.findByIdempotencyKey(idempotencyKey);
    if (existing != null) {
        return ResponseEntity.status(HttpStatus.OK).body(existing);
    }
    
    // Process payment (exactly once)
    PaymentResponse result = paymentService.process(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
}
```

## 10. What Happens Internally — Request Processing Flow

When a request hits `http://localhost:8080/api/orders/123`:

```
Step 1: Tomcat (or embedded server) accepts TCP connection
  └─ Creates HttpServletRequest + HttpServletResponse objects

Step 2: Filter chain executes (before servlet)
  └─ CharacterEncodingFilter (UTF-8)
  └─ CorsFilter (CORS headers)
  └─ OncePerRequestFilter (Spring Security - auth)
  └─ RequestContextFilter (request context for web scopes)

Step 3: DispatcherServlet receives the request
  └─ Servlet.service() is called by Tomcat
  └─ DispatcherServlet.doService() → doDispatch()

Step 4: HandlerMapping selects the controller method
  └─ RequestMappingHandlerMapping searches registered mappings
  └─ Pattern match: GET /api/orders/{orderId} → OrderController.getOrder()
  └─ Returns HandlerExecutionChain (handler + interceptors)

Step 5: Interceptors execute preHandle()
  └─ Security interceptor checks authentication/authorization
  └─ Custom interceptors (logging, rate limiting)

Step 6: HandlerAdapter invokes the controller method
  └─ Resolves method arguments:
      ├─ @PathVariable("orderId") → "123" → Long 123
      ├─ @RequestParam → query string values
      └─ @RequestBody → JSON deserialized via Jackson

Step 7: Controller method executes → returns Order object

Step 8: HandlerAdapter processes the return value
  └─ If @ResponseBody or @RestController: 
      └─ HttpMessageConverter writes Order → JSON
      └─ Content-Type: application/json
  └─ If ResponseEntity: set status, headers, body

Step 9: Interceptors execute postHandle()

Step 10: Response sent back through filters → Tomcat
```

## 11. Tricky Interview Cases

**Case 1 — Entity serialization triggers LazyInitializationException**
```java
@Entity
public class Order {
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items; // Lazy!
}

@GetMapping("/orders/{id}")
public Order getOrder(@PathVariable Long id) {
    Order order = orderService.findById(id);
    return order; // Jackson serializes → accesses items → LazyInitializationException!
}
```
Fix: Use DTOs (never expose entities), `@EntityGraph`, or `JOIN FETCH` in query.

**Case 2 — `@RequestBody` with `Optional` field**
```java
public class CreateOrderRequest {
    private Optional<String> couponCode; // ❌ DON'T use Optional in DTOs
    // Optional is not serializable by Jackson by default (Jackson 2.11+ has module)
}
```
Fix: Use `@JsonProperty(access = READ_WRITE)` or just nullable fields.

**Case 3 — `@RequestParam` vs `@PathVariable` conflict**
```java
@GetMapping("/orders/{status}")
public List<Order> getByStatus(@PathVariable String status) {
    // If request is: GET /orders/pending → works
    // If request is: GET /orders?status=pending → goes to DIFFERENT method!
}
```
Fix: Use `@PathVariable` for required RESTful resources, `@RequestParam` for optional filters.

**Case 4 — `ResponseEntity` vs `@ResponseStatus` — which wins?**
```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED) // This is the FALLBACK
public ResponseEntity<Order> create(@RequestBody Order order) {
    Order saved = service.create(order);
    return ResponseEntity.accepted().body(saved); // This WINS — returns 202
}
```
When both are present, the `ResponseEntity` status takes priority over `@ResponseStatus`.

**Case 5 — `@ExceptionHandler` in controller vs `@ControllerAdvice` — precedence**
```java
@RestController
public class OrderController {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleLocal(OrderNotFoundException e) {
        return ResponseEntity.status(404).body("Local: " + e.getMessage());
    }
}

@RestControllerAdvice
public class GlobalHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleGlobal(OrderNotFoundException e) {
        return ResponseEntity.status(404).body("Global: " + e.getMessage());
    }
}
```
Controller-level `@ExceptionHandler` takes priority over global `@ControllerAdvice`.

## 12. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|------|
| Exposing JPA entities directly | LazyInitializationException, over-fetching, circular serialization | Use DTOs, `@JsonIgnore`, or `@JsonManagedReference`/`@JsonBackReference` |
| Returning `Optional` from controller | Jackson serialization error or confusing null handling | Use DTO with nullable fields |
| Missing `@Valid` on `@RequestBody` | Validation annotations silently ignored | Add `@Valid` or `@Validated` before `@RequestBody` |
| Generic `Exception` catch in handlers | Swallows all errors, hard to debug | Catch specific exceptions, let others propagate to default handler |
| `@RequestParam(required = false)` for primitives | `int` defaults to 0, can't distinguish "not provided" from "0" | Use `Integer` (wrapper) or `Optional<Integer>` |
| CORS `allowCredentials(true)` with wildcard origin | Browser blocks the request — `*` is not allowed with credentials | List specific origins, not `*` |
| Not using `ResponseEntity` for status codes | All responses get 200 (success) or 500 (error) | Use `ResponseEntity.created()`, `.badRequest()`, `.notFound()` |
| File upload without size limits | Memory overflow from large files | Configure `spring.servlet.multipart.max-file-size` and `max-request-size` |

## 13. Production Usage

**Rate limiting with Spring Filters:**
```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final RateLimiter rateLimiter;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain chain) throws IOException, ServletException {
        String clientIp = request.getRemoteAddr();
        if (!rateLimiter.tryAcquire(clientIp)) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        chain.doFilter(request, response);
    }
}
```

**Global logging with Interceptor:**
```java
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, 
                               HttpServletResponse response, 
                               Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        log.info("{} {} {} {}ms", 
            request.getMethod(), request.getRequestURI(), 
            response.getStatus(), duration);
    }
}
```

**Pagination + Sorting pattern:**
```java
@GetMapping("/search")
public Page<OrderResponse> searchOrders(
        @RequestParam(required = false) String status,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) 
        Pageable pageable) {
    
    Page<Order> orders = orderService.searchByStatus(status, pageable);
    return orders.map(OrderResponse::from);
}
```

## 14. Advanced Details

- **`Filter` vs `Interceptor`**: Filter operates on `ServletRequest`/`ServletResponse` (before DispatcherServlet). Interceptor operates on `HandlerMethod` (after DispatcherServlet, before controller). Filters can be applied to static resources; interceptors only to controller methods. Filters are part of Servlet spec; interceptors are Spring-specific.
- **Content negotiation**: Spring checks: URL suffix (.json, .xml), URL parameter (`?format=json`), or `Accept` header. `ContentNegotiationConfigurer` customizes strategy.
- **`HttpMessageConverter` priority**: Spring uses first converter that can handle the media type. Default order: `ByteArrayHttpMessageConverter` → `StringHttpMessageConverter` → `MappingJackson2HttpMessageConverter` → `Jaxb2RootElementHttpMessageConverter`.
- **`@RestControllerAdvice`**: Combines `@ControllerAdvice` + `@ResponseBody`. Methods return JSON automatically. Add `@ExceptionHandler`, `@InitBinder`, `@ModelAttribute` methods that apply globally.
- **Async request processing**: `DeferredResult` or `Callable` return types allow the container thread to be released while processing completes on a separate thread. For long-polling or SSE (Server-Sent Events), use `SseEmitter` or `StreamingResponseBody`.

## 15. Interview Questions And Answers

### Beginner
Q: What is the difference between `@Controller` and `@RestController`?
A: `@RestController` = `@Controller` + `@ResponseBody`. With `@Controller`, you typically return a view name (Thymeleaf/JSP) that gets resolved by a `ViewResolver`. With `@RestController`, each method automatically has `@ResponseBody`, meaning the return value is written directly to the HTTP response body (usually as JSON). For REST APIs, always use `@RestController`.

### Intermediate
Q: How does `@RequestBody` work? What happens when invalid JSON is sent?
A: `@RequestBody` tells Spring to deserialize the HTTP request body into the annotated parameter. Spring uses `HttpMessageConverter` — typically `MappingJackson2HttpMessageConverter` (Jackson) for JSON. When invalid JSON is sent:
1. Jackson throws `JsonParseException` or `JsonMappingException`
2. Spring wraps it in `HttpMessageNotReadableException`
3. If no `@ExceptionHandler` catches it, Spring returns 400 Bad Request with a default error message

### Senior
Q: Your payment API receives a POST request with JSON body. The request takes 30 seconds to process because it calls an external slow payment gateway. Under high load, your service becomes unresponsive. How would you redesign this API to be more resilient?
A: **Synchronous approach**: The 30-second blocking ties up one Tomcat thread (default pool = 200). With 200 concurrent slow requests, all threads are blocked → service rejects new connections.

**Redesign options:**
1. **Async with `DeferredResult`**: Return `DeferredResult<PaymentResponse>` immediately, process on a separate thread pool. Tomcat thread is released immediately.
2. **Event-driven**: Accept the request, return 202 Accepted with a location header. Process asynchronously, provide a status endpoint for the client to poll.
3. **WebSocket/SSE**: Accept the request, push the result when ready via WebSocket.
4. **Virtual threads (Java 21+)**: `spring.threads.virtual.enabled=true` — each request uses a lightweight virtual thread. 30s blocking is acceptable because virtual threads are cheap (thousands can be blocked simultaneously).

### Tricky
Q: What happens if you have a `@RequestMapping` on both class and method level with different paths? What about different HTTP methods?
A: The class-level path is a **prefix**. The method-level path is **appended**:
```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) { ... }
    // Maps to: GET /api/orders/{id}
}
```

For HTTP methods, the method-level annotation overrides class-level:
```java
@RequestMapping("/api/orders")  // Any method
public class OrderController {
    @PostMapping  // POST only
    public Order create(...) { ... }
}
```

**Ambiguity resolution**: If two methods match the same path+method, Spring checks for the most specific match (more path variables, more headers, more params). If still ambiguous, throws `IllegalStateException` at startup.

## 16. Final 30-Second Answer

**REST APIs**: `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping`. **Bindings**: `@PathVariable` (URI), `@RequestParam` (query), `@RequestBody` (JSON), `@RequestHeader`. **Validation**: `@Valid` + Jakarta validation annotations + `@ControllerAdvice` for error handling. **CORS**: configure allowed origins, methods, headers. **File upload**: `MultipartFile`, `Resource` for download. **Versioning**: URI path (`/v1/`), header, or Accept header. **Idempotency**: `Idempotency-Key` header prevents duplicate processing. Always: use DTOs (never expose entities), validate inputs, handle errors globally, set proper HTTP status codes.