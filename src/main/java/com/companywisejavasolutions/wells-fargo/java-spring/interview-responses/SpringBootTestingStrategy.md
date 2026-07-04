# Spring Boot Testing Strategy - Interview Response

## What Is It?

A good Spring Boot testing strategy uses different test types for different risks.

In simple terms:

```text
Do not make every test a full SpringBootTest.
Do not rely only on unit tests.
Use the smallest test that proves the behavior.
```

## Why It Matters

Spring behavior often depends on runtime wiring:

```text
validation
security filters
transactions
JPA mappings
controller serialization
external service calls
```

So a banking API needs both fast tests and realistic integration tests.

## Test Pyramid

```text
Many unit tests
Some slice tests
Fewer integration tests
Contract tests at service boundaries
End-to-end tests only for critical flows
```

## 1. Unit Tests

Use for pure business rules.

Example:

```java
class TransferServiceTest {
    @Test
    void rejectsNegativeAmount() {
        assertThrows(InvalidAmountException.class,
            () -> service.createTransfer(commandWithAmount("-10.00")));
    }
}
```

Good for:

```text
fast feedback
business rules
edge cases
no Spring context needed
```

## 2. MVC Slice Test

Use `@WebMvcTest` for controller behavior.

```java
@WebMvcTest(TransferController.class)
class TransferControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void returns400ForInvalidPayload() throws Exception {
        mvc.perform(post("/api/v1/transfers")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
```

Good for:

```text
request validation
JSON serialization
HTTP status codes
controller advice
security wiring if included
```

## 3. JPA Slice Test

Use `@DataJpaTest` for repositories and mappings.

```java
@DataJpaTest
class LedgerRepositoryTest {
    @Autowired LedgerRepository repository;

    @Test
    void findsLedgerEntriesByTransferId() {
        // persist entry, flush, query, assert
    }
}
```

Good for:

```text
entity mappings
queries
indexes/constraints
transaction behavior around repository layer
```

## 4. Integration Test

Use `@SpringBootTest` when real wiring matters.

```java
@SpringBootTest
@Testcontainers
class TransferIntegrationTest {
    // real app context + real database container
}
```

Use Testcontainers for:

```text
PostgreSQL
Kafka
Redis
```

Good for:

```text
transactions
database constraints
message publishing
security + controller + service + repository together
```

## 5. External Dependency Tests

Use WireMock for downstream HTTP services:

```text
fraud service returns timeout
payment partner returns 500
limit service returns declined
```

Use Pact/contract tests for APIs:

```text
consumer defines expected response
provider verifies it before deployment
```

## Banking Test Cases

For transfer APIs, test:

```text
valid transfer
invalid amount
missing idempotency key
duplicate idempotency key
insufficient funds
database rollback
fraud service timeout
Kafka event published once
security scope denied
```

## Forward / Backward Compatibility

Testing must protect compatibility.

```text
Old API response fields still exist
New fields are optional
Error schema remains stable
Event schema remains compatible
Deprecated fields are still tested until removed
```

Semantic versioning:

```text
MAJOR -> breaking contract requires new contract tests
MINOR -> add tests for optional compatible behavior
PATCH -> regression tests for the bug
```

## Related Patterns

- Test pyramid
- Contract testing
- Test double
- Repository pattern
- Dependency injection

## Follow-Up Interview Questions

### Why avoid only `@SpringBootTest`?

```text
It is slower, more brittle, and makes failures harder to locate.
```

### When do you use Testcontainers?

```text
When real database, Redis, or Kafka behavior matters.
```

### What is contract testing for?

```text
To ensure provider and consumer APIs/events stay compatible during independent deployments.
```

## Interview Answer

In an interview, I would say: I do not use one test type for everything. I unit test business rules, use `@WebMvcTest` for controller validation and error responses, `@DataJpaTest` for repository mappings and queries, and `@SpringBootTest` with Testcontainers for full transaction and infrastructure behavior. For microservices, I add WireMock for downstream failures and Pact or contract tests for API compatibility. In a banking system, I would specifically test idempotency, rollback, duplicate requests, security scopes, and event publishing because those are the failures that hurt production.
