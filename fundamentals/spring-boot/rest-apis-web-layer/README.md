# REST APIs & Web Layer — Complete Deep Dive

## 1. Why This Concept Matters

REST APIs are the primary way microservices and web clients communicate. Spring MVC provides the annotations and infrastructure to build RESTful services — request mapping, parameter binding, validation, exception handling, CORS, file upload, pagination, and API versioning. Interviewers test these extensively because every backend role involves building and consuming APIs. Understanding the full web layer — from `@RequestParam` to `@ControllerAdvice` — is essential for building production-grade APIs.

## 2. Core Annotations

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

## 3. Parameter Binding

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

## 4. Request/Response DTOs & Validation

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

## 5. CORS Configuration

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

## 6. File Upload & Download

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

## 7. API Versioning

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

## 8. Idempotency

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

## 9. Final 30-Second Answer

**REST APIs**: `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping`. **Bindings**: `@PathVariable` (URI), `@RequestParam` (query), `@RequestBody` (JSON), `@RequestHeader`. **Validation**: `@Valid` + Jakarta validation annotations + `@ControllerAdvice` for error handling. **CORS**: configure allowed origins, methods, headers. **File upload**: `MultipartFile`, `Resource` for download. **Versioning**: URI path (`/v1/`), header, or Accept header. **Idempotency**: `Idempotency-Key` header prevents duplicate processing. Always: use DTOs (never expose entities), validate inputs, handle errors globally, set proper HTTP status codes.