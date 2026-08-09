# Module — Testing: JUnit 5, Mockito, Integration Testing — Q&A

> **Skill**: 7+ years — JUnit 5 architecture, Mockito internals, integration testing, testcontainers.

---

## Q1. JUnit 5 Architecture & Advanced Features

```java
// JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage

// =====================================================
// JUPITER — Modern testing API
// =====================================================

class OrderServiceTest {
    @Test
    @DisplayName("Should calculate total with discount")
    @Tag("regression")
    void calculateTotal_WithDiscount_ReturnsDiscountedPrice() {
        // Given
        Order order = new Order(100.0, DiscountCode.FLAT_20);
        
        // When
        double total = service.calculateTotal(order);
        
        // Then
        assertEquals(80.0, total);
    }
    
    // Lifecycle callbacks:
    @BeforeEach
    void setUp() {}
    
    @AfterEach
    void tearDown() {}
    
    @BeforeAll
    static void globalSetup() {}  // Must be static (once per class)
    
    @AfterAll
    static void globalTeardown() {}
    
    // Test interfaces:
    @TestFactory  // Dynamic tests
    Stream<DynamicTest> tests() {
        return Stream.of(
            DynamicTest.dynamicTest("test1", () -> assertEquals(1, 1)),
            DynamicTest.dynamicTest("test2", () -> assertEquals(2, 2))
        );
    }
    
    // Parameterized tests:
    @ParameterizedTest
    @CsvSource({
        "100, 20, 80",
        "200, 50, 150",
        "300, 0, 300"
    })
    void calculateTotal_Discounts(double price, double discount, double expected) {
        assertEquals(expected, service.applyDiscount(price, discount));
    }
    
    // Assertions: assertEquals, assertTrue, assertThrows, assertAll (bulk)
}

// =====================================================
// MOCKITO — Mocking Internals
// =====================================================

// How Mockito creates mocks:
// 1. CGLIB proxy for classes (extends class)
// 2. Java Proxy for interfaces (implements interface)

UserRepository mockRepo = mock(UserRepository.class);
when(mockRepo.findById(1L)).thenReturn(Optional.of(new User("Alice")));

// Mock vs Spy:
User mock = mock(User.class);           // Full mock — all methods return default values
User spy = spy(new User("Alice"));      // Partial mock — calls real methods by default

when(mock.getName()).thenReturn("Mocked Alice");  // Mock: full control
when(spy.getName()).thenReturn("Spy Alice");      // Spy: override specific methods

// Argument matchers:
when(mock.save(argThat(user -> user.getName().startsWith("A")))).thenReturn(user);
when(mock.save(any(User.class))).thenReturn(user);
when(mock.save(eq(expectedUser))).thenReturn(user);

// Verify:
verify(mockRepo).findById(1L);
verify(mockRepo, times(1)).findById(1L);
verify(mockRepo, never()).delete(any());
verify(mockRepo, atLeastOnce()).save(any());

// BDD style (Behavior-Driven):
given(repo.findById(1L)).willReturn(Optional.of(user));
willThrow(IllegalArgumentException.class).given(repo).save(null);

// =====================================================
// INTEGRATION TESTING — Testcontainers
// =====================================================

@Testcontainers
class OrderRepositoryIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private OrderRepository repo;
    
    @Test
    void save_Order_ReturnsSavedEntity() {
        Order order = new Order("Item1", 100.0);
        Order saved = repo.save(order);
        assertNotNull(saved.getId());
    }
}
```

**Final 30-Second**: JUnit 5: @Test, parameterized tests, lifecycle callbacks. Mockito: mocks (full control), spies (partial), argument matchers, verify behavior vs state. Testcontainers for integration tests: spin up real PostgreSQL in Docker for testing. Use @MockBean in Spring Boot tests to mock beans in application context.