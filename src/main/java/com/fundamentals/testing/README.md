# Testing — Unit Testing, Mockito, Integration Testing, Testcontainers, Contract Testing

## 1. Why This Concept Matters

Testing is not optional — it's the safety net that allows confident refactoring, deployment, and regression prevention. Unit tests verify individual components in isolation (using mocks). Integration tests verify real interactions with databases, queues, and external services (using Testcontainers). Contract tests ensure compatibility between microservices. Interviewers test testing knowledge at every level — you must know how to structure tests, use Mockito effectively, handle async testing, and set up test containers.

Misunderstanding testing causes: fragile tests that break on any refactor, integration tests that depend on shared databases (flaky), over-mocking (testing mock behavior, not real behavior), and missing edge cases.

## 2. Unit Testing with JUnit + Mockito

```java
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private PaymentGateway paymentGateway;
    
    @InjectMocks  // Inject mocks into this class (constructor injection)
    private OrderService orderService;
    
    @Test
    void testCreateOrder_Success() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest("user-1", new BigDecimal("99.99"));
        Order savedOrder = new Order(1L, "user-1", new BigDecimal("99.99"), "PENDING");
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(paymentGateway.charge(anyString(), any(BigDecimal.class)))
            .thenReturn(new PaymentResult("txn-123", "SUCCESS"));
        
        // Act
        Order result = orderService.createOrder(request);
        
        // Assert
        assertNotNull(result);
        assertEquals("PAID", result.getStatus());
        verify(orderRepository).save(any(Order.class));     // Verify save was called
        verify(paymentGateway).charge(eq("user-1"), eq(new BigDecimal("99.99"))); // With exact args
    }
    
    @Test
    void testCreateOrder_PaymentFails() {
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());
        when(paymentGateway.charge(any(), any())).thenThrow(new PaymentException("Insufficient funds"));
        
        assertThrows(PaymentException.class, () -> orderService.createOrder(new CreateOrderRequest()));
        
        verify(orderRepository).delete(any(Order.class)); // Verify rollback
    }
    
    @Test
    void testFindOrder_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> orderService.findOrder(999L));
    }
}
```

**Key Mockito annotations:**
- `@Mock`: creates a mock of the class (all methods return defaults)
- `@Spy`: wraps a real object — calls real methods unless stubbed
- `@InjectMocks`: injects @Mock/@Spy fields into the test target
- `@Captor`: captures argument values for verification

**Important Mockito methods:**
```java
when(mock.method()).thenReturn(value);           // Return value
when(mock.method()).thenThrow(Exception.class);  // Throw exception
when(mock.method()).thenAnswer(invocation -> {   // Dynamic response
    return invocation.getArgument(0).toString() + " processed";
});

verify(mock).method();                           // Verify called once
verify(mock, times(3)).method();                 // Verify called 3x
verify(mock, never()).method();                  // Verify never called
verify(mock, timeout(1000)).method();            // Verify within 1s (async)
verifyNoInteractions(mock);                      // Verify no calls at all

ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
verify(mock).method(captor.capture());
assertEquals("expected", captor.getValue());
```

## 3. Integration Testing with Testcontainers

Testcontainers spins up real Docker containers (PostgreSQL, Kafka, Redis) for integration tests — no H2 in-memory database compromises.

```java
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@Testcontainers
@SpringBootTest
class OrderRepositoryIntegrationTest {
    
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
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private TestEntityManager em;
    
    @Test
    void testSaveAndFind() {
        Order order = new Order("user-1", new BigDecimal("50.00"));
        Order saved = orderRepository.save(order);
        
        Optional<Order> found = orderRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("user-1", found.get().getUserId());
    }
    
    @Test
    void testFindByUserId() {
        orderRepository.save(new Order("user-1", new BigDecimal("10.00")));
        orderRepository.save(new Order("user-1", new BigDecimal("20.00")));
        
        List<Order> orders = orderRepository.findByUserId("user-1");
        assertEquals(2, orders.size());
    }
}

// For Kafka:
// @Container
// static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.0"));
```

## 4. Web Layer Testing (MockMvc)

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean  // Spring Boot's mock — replaces bean in context
    private OrderService orderService;
    
    @Test
    void testGetOrder() throws Exception {
        when(orderService.findOrder(1L)).thenReturn(new OrderResponse(1L, "PAID"));
        
        mockMvc.perform(get("/api/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PAID"))
            .andExpect(jsonPath("$.id").value(1));
    }
    
    @Test
    void testCreateOrder_ValidationFails() throws Exception {
        String invalidJson = "{\"amount\": -1}"; // Missing userId, negative amount
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
```

## 5. Contract Testing

Contract tests verify that a service provider and consumer agree on the API contract. Prevents integration failures when one side changes.

**Spring Cloud Contract** (provider side):
```groovy
// contracts/shouldReturnOrder.groovy
Contract.make {
    description "should return order by ID"
    request {
        method GET()
        url "/api/orders/1"
        headers { accept(applicationJson()) }
    }
    response {
        status OK()
        headers { contentType(applicationJson()) }
        body([
            id: 1,
            userId: "user-123",
            amount: 99.99,
            status: "PAID"
        ])
    }
}
```

**Consumer side (using WireMock or Pact):**
```java
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureStubRunner(
    ids = "com.myapp:order-service:+:stubs:8090", // Download stubs
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class OrderServiceConsumerTest {
    // Tests use real stubs — no mocking
    @Autowired
    private OrderServiceClient orderServiceClient;
    
    @Test
    void testGetOrder() {
        Order order = orderServiceClient.getOrder(1L);
        assertEquals(1L, order.getId());
        assertEquals("PAID", order.getStatus());
    }
}
```

## 6. Testing Async Code

```java
@Test
void testCompletableFuture() throws Exception {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
        Thread.sleep(100);
        return "done";
    });
    
    // Block and wait
    String result = future.get(5, TimeUnit.SECONDS);
    assertEquals("done", result);
}

// Testing @Async methods
@Test
void testAsyncService() {
    AsyncService service = new AsyncService();
    Future<String> future = service.processAsync();
    String result = future.get(5, TimeUnit.SECONDS);
    assertEquals("processed", result);
}
```

## 7. Interview Traps & Frequently Asked Questions

### 🔴 Critical Traps

**Trap 1: @Mock vs @Spy confusion**
```java
@Mock
private List<String> mockList;
// All methods return defaults: mockList.get(0) → null, mockList.size() → 0

@Spy
private List<String> spyList = new ArrayList<>(); // Must initialize!
// Calls real methods unless stubbed: spyList.size() → 0 (real), spyList.get(0) → null (real)
when(spyList.get(anyInt())).thenReturn("mocked"); // Now this specific call is stubbed
```
❌ Common mistake: Forgetting to initialize `@Spy` → NullPointerException. Or using `@Spy` when you actually want `@Mock` and accidentally calling real code (which may throw exceptions or hit database).

**Trap 2: Stubbing the same method twice (last stub wins)**
```java
when(mock.save(any())).thenReturn(savedOrder);
when(mock.save(any())).thenThrow(new RuntimeException()); // This overwrites the first stub!
// Result: second call throws instead of returning savedOrder
```
❌ Fix: Use `thenReturn(...).thenThrow(...)` for sequential calls, or be explicit with arguments:
```java
when(mock.save(eq(order1))).thenReturn(result1);
when(mock.save(eq(order2))).thenThrow(new RuntimeException());
```

**Trap 3: `verifyNoMoreInteractions()` too early**
```java
verify(mock).save(any());
verifyNoMoreInteractions(mock); // Fails if ANY other method was called!
// Even orderRepo.findById(1L) (called by JPA internally) will fail this
```
❌ Fix: Use `verifyNoMoreInteractions(mock)` only when you truly expect zero additional calls. Prefer verifying only what matters.

**Trap 4: Mocking static/private methods without proper config**
```java
// This WON'T work without extra setup:
when(mock.somePrivateMethod()).thenReturn("x"); // ❌ @Mock can't mock private methods

// Options:
// 1. Use @Spy + Mockito's inline mocking (mockito-inline dependency)
// 2. Use PowerMock (legacy, not recommended)
// 3. Refactor: extract logic to testable public/protected method
```

**Trap 5: `@InjectMocks` injection failure**
```java
@InjectMocks
private OrderService orderService; // orderRepository is null!

// Why? By default, @InjectMocks tries constructor injection first.
// If OrderService has multiple constructors, it may pick wrong one.
// Fix: Add @Mock on all dependencies, or set constructorArgNames
```
❌ Common issue: Missing `@Mock` field → dependency not injected → NullPointerException. Always verify mocks are injected by asserting `assertNotNull(orderService.getOrderRepository())`.

### 📋 Common Interview Questions

**Q: What's the difference between `@Mock` and `@Spy`?**
A: `@Mock` creates a complete fake — all methods return default values (null, 0, empty list). `@Spy` wraps a real object — calls actual methods unless explicitly stubbed. Use `@Mock` for pure unit tests (no side effects). Use `@Spy` when you need real behavior for some methods but want to stub others.

**Q: What does `verify(mock, times(1)).method()` do?**
A: It asserts that `method` was called exactly once on the mock. `times(0)` = never called, `times(n)` = exactly n times, `never()` = alias for times(0), `atLeast(2)` = 2 or more, `atMost(3)` = 3 or fewer.

**Q: When would you use `ArgumentCaptor`?**
A: When you need to verify the *exact arguments* passed to a mock, especially for complex objects. Example: capture an `Order` object passed to `orderRepo.save()` and assert its fields (userId, amount, status) match expectations.

**Q: Difference between `@MockBean` (Spring) and `@Mock` (Mockito)?**
A: `@MockBean` is Spring Boot's annotation — it replaces a bean in the Spring ApplicationContext with a Mockito mock. Used in `@SpringBootTest` or `@WebMvcTest`. `@Mock` is pure Mockito — creates a mock but doesn't interact with Spring's context. Use `@Mock` for plain unit tests (no Spring context needed).

**Q: Why is `when(mock.method()).thenReturn(x)` called "stubbing"?**
A: Stubbing = defining behavior for a mock. When the test calls `mock.method()`, it returns `x` instead of the default. Stubbing lets you control the test environment — simulate success, failure, edge cases without hitting real dependencies.

**Q: What's the problem with over-mocking?**
A: Over-mocking means mocking every collaborator, including value objects or simple utilities. Result: tests verify mock interactions, not real business logic. If you mock everything, your tests become fragile and pass even when real code is broken. Rule: mock only external dependencies (DB, HTTP, queues). Use real objects for simple data structures.

**Q: What is a "flaky test" and how do you prevent it?**
A: A test that sometimes passes and sometimes fails without code changes. Common cause: shared mutable state between tests (static fields, DB not reset, thread pool reuse). Fix: `@BeforeEach` to reset state, use `@DirtiesContext` if needed, never share mutable static state, use Testcontainers for isolated DB state.

**Q: How do you test an exception with Mockito?**
A: 
```java
// JUnit 5:
assertThrows(ExpectedException.class, () -> service.methodThatThrows());

// Mockito:
when(mock.save(any())).thenThrow(new DataAccessException("DB down"));
```
Ensure the exception actually propagates (not caught inside the method under test).

### 🎯 One-Liner Interview Answers

"Hook the unit under test → mock its dependencies with `@Mock` → inject with `@InjectMocks` → stub behaviors with `when/thenReturn` → invoke method → assert result + `verify` interactions. For integration tests, use Testcontainers for real DB/queues. For web layer, use MockMvc. Never test implementation details — test observable behavior."

**Final 30-Second Answer:**
- **Unit**: `@Mock` + `@InjectMocks`, `when/thenReturn`, `verify`, `assertThrows`
- **Integration**: Testcontainers (real PostgreSQL/Kafka), `@Container`, `@DynamicPropertySource`
- **Web**: `@WebMvcTest`, MockMvc (`get/post`, `andExpect`, `jsonPath`)
- **Contract**: Spring Cloud Contract (provider publishes stubs, consumer tests against stubs)
- **Traps**: `@Mock` vs `@Spy`, last-stub-wins, verifyNoMoreInteractions, self-invocation doesn't mock, caught exceptions don't roll back

**Unit tests**: JUnit + Mockito (@Mock, @InjectMocks, verify, when/thenReturn). **Integration tests**: Testcontainers (PostgreSQL, Kafka containers), @DynamicPropertySource. **Web tests**: MockMvc (get/perfom, andExpect, jsonPath). **Contract tests**: Spring Cloud Contract (provider publishes stubs, consumer verifies against stubs). **Best practices**: test behavior not implementation, use real DB (Testcontainers), test edge cases (null, empty, errors), use timeouts for async tests. Never: over-mock, use H2 for PostgreSQL features, create flaky tests with shared state.