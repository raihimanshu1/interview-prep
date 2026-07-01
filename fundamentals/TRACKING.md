push# Fundamentals — Complete Tracking & Master Plan

This file tracks every topic in `skills/java-fundamentals-interview/references/topic-map.md` — what's done, what's pending, and in what order to generate them.

## Legend
- ✅ **Done** — Full deep-dive README exists (10 sections, >200 lines, real code, internals, tricky cases, Q&A)
- 🔄 **Needs Upgrade** — README exists but needs: Mermaid diagrams, companion code files, or depth expansion
- ⬜ **Pending** — Not yet created

---

## 1. Java Fundamentals

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | JVM, JDK, JRE | ⬜ | |
| 2 | how Java is platform independent | ⬜ | |
| 3 | JVM architecture and class loading | ⬜ | |
| 4 | ClassLoader basics and types | ⬜ | |
| 5 | ClassNotFoundException vs NoClassDefFoundError | ⬜ | |
| 6 | runtime data areas | ⬜ | |
| 7 | Java Memory Model (JMM) | ⬜ | |
| 8 | explain Java Memory Model in interviews | ⬜ | |
| 9 | volatile and happens-before | ✅ | `multi-threading/volatile/README.md` |
| 10 | GC analysis and performance tuning | ⬜ | |
| 11 | Metaspace vs PermGen | ⬜ | |
| 12 | Integer cache and wrapper comparison | ✅ | `core-java/integer-cache/README.md` |
| 13 | autoboxing and unboxing | ✅ | `core-java/autoboxing/README.md` |
| 14 | == vs equals | ⬜ | |
| 15 | final variable vs immutable object | ⬜ | |
| 16 | pass-by-value in Java | ⬜ | |
| 17 | static blocks and initialization order | ⬜ | |
| 18 | constructor execution order | ⬜ | |
| 19 | null with overloaded methods | ⬜ | |
| 20 | widening vs boxing vs varargs method selection | ⬜ | |
| 21 | BigDecimal.equals() vs compareTo() | ⬜ | |
| 22 | post-increment and pre-increment puzzles | ⬜ | |
| 23 | unreachable code and finally return behavior | ⬜ | |

## 2. Object-Oriented Programming

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | encapsulation | ⬜ | |
| 2 | inheritance | ⬜ | |
| 3 | polymorphism | ✅ | `object-oriented-programming/polymorphism/README.md` |
| 4 | abstraction | ⬜ | |
| 5 | Serializable interface | ⬜ | |
| 6 | can a class be static, final, or private | ⬜ | |
| 7 | OOP vs scripting languages | ⬜ | |
| 8 | method overloading vs overriding | ⬜ | |
| 9 | interfaces vs abstract classes | ⬜ | |
| 10 | association, aggregation, composition | ⬜ | |
| 11 | immutable classes | ✅ | `object-oriented-programming/immutable-classes/README.md` |
| 12 | how to create an immutable class | ✅ | Same file |
| 13 | creating custom immutable classes | ✅ | Same file |
| 14 | tight coupling vs loose coupling | ⬜ | |
| 15 | object contract | ⬜ | |
| 16 | equals and hashCode | ✅ | `object-oriented-programming/equals-hashcode/README.md` |
| 17 | Comparable vs Comparator | ✅ | `core-java/comparable-comparator/README.md` |
| 18 | shallow copy vs deep copy | ⬜ | |
| 19 | clone pitfalls | ⬜ | |
| 20 | records as value objects | ⬜ | |

## 3. SOLID Principles

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1-5 | All 5 SOLID principles | ✅ | `object-oriented-programming/solid-principles/README.md` |
| 6 | applying SOLID in Spring Boot | ✅ | Same file |
| 7-9 | scenario questions | ⬜ | Need scenario-specific files |

## 4. Design Patterns

| # | Pattern | Type | Status | Notes |
|---|---------|------|--------|-------|
| 1 | Singleton | Creational | ✅ | `design-patterns/singleton/README.md` |
| 2 | Factory | Creational | ✅ | `design-patterns/factory/README.md` |
| 3 | Abstract Factory | Creational | ⬜ | |
| 4 | Builder | Creational | ✅ | `design-patterns/builder/README.md` |
| 5 | Prototype | Creational | ⬜ | |
| 6 | Strategy | Behavioral | ✅ | `design-patterns/strategy/README.md` |
| 7 | Observer | Behavioral | ✅ | `design-patterns/observer/README.md` |
| 8 | Template Method | Behavioral | ⬜ | |
| 9 | Chain of Responsibility | Behavioral | ⬜ | |
| 10 | Command | Behavioral | ⬜ | |
| 11 | State | Behavioral | ⬜ | |
| 12 | Iterator | Behavioral | ⬜ | |
| 13 | Mediator | Behavioral | ⬜ | |
| 14 | Decorator | Structural | ⬜ | |
| 15 | Adapter | Structural | ⬜ | |
| 16 | Proxy | Structural | ⬜ | |
| 17 | Facade | Structural | ⬜ | |
| 18 | Composite | Structural | ⬜ | |
| 19 | Flyweight | Structural | ⬜ | |
| 20 | Bridge | Structural | ⬜ | |

## 5. Java 8 Features

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | lambda expressions | ⬜ | |
| 2 | why lambda functions are useful | ⬜ | |
| 3 | functional interfaces | ⬜ | |
| 4 | Predicate, Function, Consumer, Supplier | ⬜ | |
| 5 | Supplier vs Consumer | ⬜ | |
| 6 | Runnable, Comparator, Callable as functional interface | ⬜ | |
| 7 | method references | ⬜ | |
| 8 | Streams API | ✅ | `core-java/stream-api/README.md` |
| 9 | parallel streams | ✅ | Same file |
| 10 | CompletableFuture | ✅ | `multi-threading/completablefuture/README.md` |
| 11 | asynchronous programming | ✅ | Same file |
| 12 | Optional | ✅ | `core-java/optional/README.md` |
| 13 | map, flatMap, filter, reduce | ✅ | Stream + Optional files |
| 14 | collectors and grouping | ✅ | Stream file |
| 15 | interface constructors — why no constructors | ⬜ | |
| 16 | for-loop vs for-each performance | ⬜ | |
| 17-19 | scenarios | ⬜ | |

## 6. Collections Framework

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | ArrayList internals | ✅ | `collection/arraylist/README.md` |
| 2 | ArrayList vs LinkedList | ✅ | Both files |
| 3 | HashMap internals | ✅ | `collection/hashmap/README.md` |
| 4 | HashMap collision handling | ✅ | Same file |
| 5 | HashMap resizing and load factor | ✅ | Same file |
| 6 | HashMap fail-fast behavior | ✅ | Same file |
| 7 | HashMap vs Hashtable vs ConcurrentHashMap | ✅ | HashMap + ConcurrentHashMap files |
| 8 | HashMap vs ConcurrentHashMap vs synchronizedMap | ✅ | Same |
| 9 | ConcurrentHashMap | ✅ | `collection/concurrenthashmap/README.md` |
| 10 | TreeMap | ⬜ | |
| 11 | LinkedList | ✅ | `collection/linkedlist/README.md` |
| 12 | PriorityQueue | ✅ | `collection/priorityqueue/README.md` |
| 13 | CopyOnWriteArrayList | ✅ | `collection/copyonwritearraylist/README.md` |
| 14 | Comparator vs Comparable | ✅ | `core-java/comparable-comparator/README.md` |
| 15 | WeakHashMap | ⬜ | |
| 16 | SoftReference | ⬜ | |
| 17 | WeakReference | ⬜ | |
| 18 | mutable keys in HashMap | ⬜ | |
| 19 | HashSet implementation | ✅ | `collection/hashset/README.md` |
| 20 | fail-fast vs fail-safe iterators | ⬜ | |
| 21 | Collections.synchronizedMap vs ConcurrentHashMap | ✅ | ConcurrentHashMap file |
| 22 | List.of() immutability | ⬜ | |
| 23 | List vs Set vs Map when to use | ⬜ | |
| 24 | how hashCode() is generated and used | ✅ | equals-hashcode file |
| 25 | Iterable interface | ⬜ | |
| 26-28 | scenarios | ⬜ | |

## 7. Exception Handling

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | checked vs unchecked exceptions | ✅ | `core-java/exception-handling/README.md` |
| 2 | superclass of all exceptions and errors | ✅ | Same file |
| 3 | Error vs Exception | ✅ | Same file |
| 4 | throw vs throws | ✅ | Same file |
| 5 | exception chaining | ⬜ | |
| 6 | exception propagation | ⬜ | |
| 7 | try-with-resources | ✅ | Same file |
| 8 | suppressed exceptions | ⬜ | |
| 9 | custom exceptions | ⬜ | |
| 10 | global exception handling | ⬜ | |
| 11 | @ControllerAdvice | ⬜ | |
| 12 | @ExceptionHandler | ⬜ | |
| 13 | finally behavior | ✅ | Same file |
| 14-16 | scenarios | ⬜ | |

## 8. String Handling

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | String pool | ✅ | `core-java/string-pool-intern/README.md` |
| 2 | intern() | ✅ | Same file |
| 3 | String interning | ✅ | Same file |
| 4 | String immutability | ✅ | `core-java/string-handling/README.md` |
| 5 | why String is immutable | ✅ | Same file |
| 6 | how String Pool works | ✅ | string-pool-intern file |
| 7 | == vs equals() for strings | ⬜ | |
| 8 | StringBuilder vs StringBuffer | ⬜ | |
| 9 | String vs StringBuilder vs StringBuffer | ⬜ | |
| 10 | String performance optimization | ⬜ | |
| 11 | new String("abc") vs string literal | ⬜ | |
| 12 | compile-time vs runtime concatenation | ⬜ | |
| 13-14 | scenarios | ⬜ | |

## 9. Multithreading & Concurrency

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | thread lifecycle | ⬜ | |
| 2 | synchronization techniques | ✅ | `multi-threading/synchronized/README.md` |
| 3 | wait(), notify(), notifyAll() | ✅ | `multi-threading/wait-notify/README.md` |
| 4 | race conditions | ⬜ | |
| 5 | visibility issues | ✅ | volatile file |
| 6 | ExecutorService | ✅ | `multi-threading/executor-service/README.md` |
| 7 | ExecutorService usage and internals | ✅ | Same file |
| 8 | thread pools | ✅ | Same file |
| 9 | thread pools and preventing race conditions | ⬜ | |
| 10 | ReentrantLock | ✅ | `multi-threading/reentrantlock/README.md` |
| 11 | ThreadLocal | ⬜ | |
| 12 | CountDownLatch | ✅ | `multi-threading/countdownlatch-cyclicbarrier/README.md` |
| 13 | CyclicBarrier | ✅ | Same file |
| 14 | atomic classes | ✅ | `multi-threading/atomic-integer/README.md` |
| 15 | volatile vs atomic variables | ✅ | volatile + atomic-integer files |
| 16 | Runnable vs Callable | ⬜ | |
| 17 | deadlock | ✅ | `multi-threading/deadlock/README.md` |
| 18 | producer-consumer problem | ⬜ | |
| 19 | blocking queue | ✅ | `collection/blockingqueue/README.md` |
| 20 | CompletableFuture in concurrent workflows | ✅ | `multi-threading/completablefuture/README.md` |
| 21 | CompletableFuture vs Future | ✅ | Same file |
| 22 | Future vs CompletableFuture | ✅ | Same file |
| 23 | process vs thread | ⬜ | |
| 24 | virtual threads (Java 21) | ⬜ | |
| 25 | Java 21 virtual threads | ⬜ | |
| 26-31 | scenarios | ⬜ | |

## 10. Spring Boot Core

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | IoC and Dependency Injection | ⬜ | |
| 2 | what Spring Boot is and why popular | ⬜ | |
| 3 | what happens internally when Spring Boot starts | ⬜ | |
| 4 | bean lifecycle | ⬜ | |
| 5 | explain bean lifecycle in Spring | ⬜ | |
| 6 | bean scopes | ⬜ | |
| 7 | autowiring | ✅ | `spring-boot/autowiring/README.md` |
| 8 | auto configuration | ⬜ | |
| 9 | component scanning | ⬜ | |
| 10 | constructor injection vs field injection | ⬜ | |
| 11 | @Component, @Service, @Repository, @Controller | ⬜ | |
| 12 | difference between @Component, @Service, @Repository | ⬜ | |
| 13 | @Bean | ⬜ | |
| 14 | @Qualifier | ⬜ | |
| 15 | @Value | ⬜ | |
| 16 | @Autowired | ⬜ | |
| 17 | @Async | ⬜ | |
| 18 | configuration properties | ⬜ | |
| 19 | application.properties purpose | ⬜ | |
| 20 | profiles | ⬜ | |
| 21 | Actuator | ⬜ | |
| 22 | scenario: two beans of same type | ⬜ | |
| 23 | scenario: circular dependency | ⬜ | |
| 24 | ways to resolve circular dependency | ⬜ | |
| 25 | scenario: bean not getting created | ⬜ | |
| 26 | scenario: conditional auto-configuration | ⬜ | |
| 27 | scenario: MVC responsibility split | ⬜ | |
| 28 | how @Transactional works internally | ✅ | `transactions/README.md` |
| 29 | transaction propagation in Spring | ✅ | Same file |
| 30 | Spring Security filter chain | ✅ | `spring-boot/spring-security/README.md` |
| 31 | Filter vs Interceptor | ⬜ | |

## 11. REST APIs & Web Layer

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1-15 | REST topics | ⬜ | All pending |
| 16-19 | scenarios | ⬜ | |

## 12. Microservices

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | API Gateway | ✅ | `microservices/README.md` |
| 2 | service discovery | ✅ | Same file |
| 3 | load balancing | ✅ | Same file |
| 4 | monolithic vs microservices | ✅ | Same file |
| 5 | how microservices communicate | ✅ | Same file |
| 6 | circuit breaker | ✅ | Same file |
| 7 | how to handle service failures | ✅ | Same file |
| 8 | service communication | ✅ | Same file |
| 9 | messaging queues | ⬜ | |
| 10 | distributed systems fundamentals | ⬜ | |
| 11 | REST vs gRPC vs messaging | ✅ | Same file |
| 12 | sync vs async communication | ✅ | Same file |
| 13 | saga pattern | ✅ | Same file |
| 14 | outbox pattern | ⬜ | |
| 15 | idempotency in distributed systems | ⬜ | |
| 16 | bulkhead | ✅ | Same file |
| 17 | rate limiting | ✅ | Same file |
| 18 | distributed tracing | ✅ | Same file |
| 19 | failure handling and tracing | ✅ | Same file |
| 20 | eventual consistency | ✅ | Same file |
| 21 | contract testing for microservices | ⬜ | |
| 22-26 | scenarios | ⬜ | |

## 13. SQL & Databases

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | joins | ✅ | `sql/README.md` |
| 2 | SQL vs NoSQL | ✅ | Same file |
| 3 | indexes | ✅ | Same file |
| 4 | composite indexes | ⬜ | |
| 5 | query execution plan | ✅ | Same file |
| 6 | transactions | ✅ | `transactions/README.md` |
| 7 | ACID | ✅ | Same file |
| 8 | isolation levels | ✅ | Same file |
| 9 | locks | ✅ | Same file |
| 10 | deadlocks | ✅ | Same file |
| 11 | optimistic locking | ✅ | Same file |
| 12 | pessimistic locking | ✅ | Same file |
| 13 | normalization vs denormalization | ⬜ | |
| 14 | partitioning | ⬜ | |
| 15 | replication | ⬜ | |
| 16 | sharding | ✅ | `scalability/README.md` |
| 17 | connection pooling | ✅ | `jdbc/README.md` |
| 18 | N+1 query problem | ✅ | `Hibernate-jpa/README.md` + `spring-boot/spring-data-jpa/README.md` |
| 19 | N+1 causes and fixes | ✅ | Same files |
| 20 | lazy vs eager loading | ✅ | Same files |
| 21 | cascade remove vs orphan removal | ⬜ | |
| 22 | Spring Data JPA main interfaces | ✅ | `spring-boot/spring-data-jpa/README.md` |
| 23 | how Spring/Hibernate generates SQL | ✅ | Same file |
| 24 | SQL query: 2nd highest salary | ⬜ | |
| 25 | WHERE vs HAVING | ⬜ | |
| 26 | database indexing | ✅ | `sql/README.md` |
| 27 | indexing strategies and B-Tree | ✅ | Same file |
| 28 | optimize a slow-running query | ✅ | Same file |
| 29 | SQL query optimization | ✅ | Same file |
| 30 | SQL window functions | ⬜ | |
| 31 | SQL duplicate record detection | ⬜ | |
| 32-34 | scenarios | ⬜ | |

## 14. Kafka & Messaging

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1-16 | All Kafka topics | ⬜ | All pending |
| 17-20 | scenarios | ⬜ | |

## 15. Security

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | authentication vs authorization | ✅ | `spring-boot/spring-security/README.md` |
| 2 | OAuth2 | ✅ | Same file |
| 3 | OAuth basics and role-based access | ✅ | Same file |
| 4 | JWT | ✅ | Same file |
| 5 | JWT validation in microservices | ✅ | Same file |
| 6 | secret key management | ✅ | Same file |
| 7 | mTLS | ⬜ | |
| 8 | API keys | ⬜ | |
| 9 | password hashing | ⬜ | |
| 10 | SQL injection | ⬜ | |
| 11 | XSS | ⬜ | |
| 12 | CSRF | ✅ | Spring Security file |
| 13 | SSRF | ⬜ | |
| 14 | replay attacks | ⬜ | |
| 15 | webhook signature validation | ⬜ | |
| 16 | PII masking | ⬜ | |
| 17 | encryption at rest and in transit | ⬜ | |
| 18-20 | scenarios | ⬜ | |

## 16. Redis & Caching

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | cache-aside pattern | ✅ | `redis/README.md` |
| 2 | write-through vs write-behind | ✅ | Same file |
| 3 | TTL | ✅ | Same file |
| 4 | cache stampede | ✅ | Same file |
| 5 | distributed lock | ⬜ | |
| 6 | Redis data structures | ✅ | Same file |
| 7 | what Redis is and why use it | ✅ | Same file |
| 8 | rate limiter with Redis | ✅ | Same file |
| 9 | eviction policies | ✅ | Same file |
| 10 | cache consistency | ⬜ | |
| 11-14 | scenarios | ⬜ | |

## 17. Testing

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1-16 | All testing topics | ⬜ | All pending |

## 18. Cloud & DevOps

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1-22 | All Cloud/DevOps topics | ⬜ | All pending |

## 19. System Design / HLD

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | rate limiter | ✅ | `hld/README.md` |
| 2 | notification system | ⬜ | |
| 3 | payment system | ⬜ | |
| 4 | payment system design | ⬜ | |
| 5 | URL shortener | ✅ | `hld/README.md` |
| 6 | file upload service | ⬜ | |
| 7 | audit logging system | ⬜ | |
| 8 | search system | ⬜ | |
| 9 | analytics dashboard | ⬜ | |
| 10 | reconciliation system | ⬜ | |
| 11 | multi-tenant SaaS | ⬜ | |
| 12 | real-time fraud detection | ⬜ | |
| 13 | queue-based worker system | ⬜ | |
| 14 | high-traffic system design | ⬜ | |
| 15 | high-traffic ticket booking | ⬜ | |
| 16-19 | scenarios | ⬜ | |

## 20. LLD

| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | SOLID in code | ✅ | SOLID file |
| 2 | strategy pattern LLD | ✅ | Strategy + LLD files |
| 3 | factory pattern LLD | ✅ | Factory + LLD files |
| 4 | observer pattern LLD | ✅ | Observer + LLD files |
| 5 | builder pattern LLD | ✅ | Builder + LLD files |
| 6 | parking lot | ✅ | `lld/README.md` |
| 7 | vending machine | ✅ | Same file |
| 8 | elevator system | ✅ | Same file |
| 9 | rate limiter LLD | ⬜ | |
| 10 | notification service LLD | ⬜ | |
| 11 | payment state machine | ⬜ | |
| 12 | logger design | ⬜ | |
| 13 | cache design | ⬜ | |
| 14-16 | scenarios | ⬜ | |

---

## Phase 1 Progress — Adding Diagrams & Companion Code

| # | File | Mermaid Diagrams | Companion Code | Status |
|---|------|-----------------|----------------|--------|
| 1 | `spring-boot/spring-security/README.md` | JWT flow, Filter chain, OAuth2, CSRF | `spring-security/examples/JwtExample.java` | ✅ DONE |
| 2 | `transactions/README.md` | ACID, MVCC, deadlock, propagation, Saga | — | ✅ DONE |
| 3 | `hld/README.md` | URL shortener, Chat, News Feed, Video arch | — | ✅ DONE |
| 4 | `microservices/README.md` | Circuit breaker state machine, Saga orch + chor | — | ✅ DONE |
| 5 | `multi-threading/completablefuture/README.md` | Async pipeline | `completablefuture/examples/` | ⬜ |
| 6 | `redis/README.md` | Cache patterns, Cluster arch | — | ⬜ |
| 7 | `spring-boot/spring-data-jpa/README.md` | JPA lifecycle, Transaction flow | — | ⬜ |
| 8 | `Hibernate-jpa/README.md` | Entity states, N+1 flow | — | ⬜ |
| 9 | `sql/README.md` | B+Tree, Query plan flow | `sql/examples/` | ⬜ |
| 10 | `jdbc/README.md` | Connection pool, PreparedStatement flow | `jdbc/examples/` | ⬜ |
| 11 | `scalability/README.md` | DB evolution, CAP diagram | — | ⬜ |
| 12 | `lld/README.md` | Parking Lot class diagram | — | ⬜ |
| 13 | `design-patterns/strategy/README.md` | Strategy pattern diagram | `strategy/examples/` | ⬜ |
| 14 | `design-patterns/observer/README.md` | Observer pattern diagram | `observer/examples/` | ⬜ |
| 15 | `design-patterns/builder/README.md` | Builder pattern diagram | `builder/examples/` | ⬜ |
| 16 | `core-java/stream-api/README.md` | Stream pipeline flow | — | ⬜ |
| 17 | `multi-threading/reentrantlock/README.md` | AQS internals flow | — | ⬜ |
| 18 | `collection/blockingqueue/README.md` | Producer-consumer flow | — | ⬜ |

### Phase 2 — Create Missing High-Priority Topics 🟡 Complete ✅

All 6 high-priority topics created:

| # | Topic | File | Status |
|---|-------|------|--------|
| 1 | JVM architecture | `core-java/jvm-architecture/README.md` | ✅ |
| 2 | Spring Boot internals | `spring-boot/internals/README.md` | ✅ |
| 3 | Missing Design Patterns | `design-patterns/additional-patterns/README.md` | ✅ |
| 4 | Kafka deep-dive | `kafka/README.md` | ✅ |
| 5 | Java 8 Functional | `core-java/java-8-functional/README.md` | ✅ |
| 6 | Security deep-dive | `security/deep-dive/README.md` | ✅ |

### Phase 3 — Create Remaining Topics

---

## Stats

| Metric | Count |
|--------|-------|
| Total topics in topic-map.md | ~300 |
| Done (✅ deep-dive READMEs) | 49 |
| Pending (⬜) | ~250 |
| Coverage | ~16% |