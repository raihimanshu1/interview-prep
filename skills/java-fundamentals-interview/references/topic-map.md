# Interview Classroom Content Topic Map

Use this as a roadmap. Generate one concept per file unless the user asks for a batch. For every topic, include scenario-based questions, classic interview traps, and production-style follow-ups when relevant.

## Java Fundamentals

- JVM, JDK, JRE
- how Java is platform independent
- JVM architecture and class loading
- ClassLoader basics and types
- `ClassNotFoundException` vs `NoClassDefFoundError`
- runtime data areas
- Java Memory Model, JMM
- explain Java Memory Model in interviews
- volatile and happens-before
- GC analysis and performance tuning
- Metaspace vs PermGen
- Integer cache and wrapper comparison
- autoboxing and unboxing
- `==` vs `equals`
- `final` variable vs immutable object
- pass-by-value in Java
- static blocks and initialization order
- constructor execution order
- `null` with overloaded methods
- widening vs boxing vs varargs method selection
- `BigDecimal.equals()` vs `compareTo()`
- post-increment and pre-increment puzzles
- unreachable code and finally return behavior

## Object-Oriented Programming

- encapsulation
- inheritance
- polymorphism
- abstraction
- Serializable interface
- can a class be static, final, or private
- OOP vs scripting languages
- method overloading vs overriding
- interfaces vs abstract classes
- association, aggregation, composition
- immutable classes
- how to create an immutable class
- creating custom immutable classes
- tight coupling vs loose coupling
- object contract
- `equals` and `hashCode`
- `Comparable` vs `Comparator`
- shallow copy vs deep copy
- clone pitfalls
- records as value objects

## SOLID Principles

- Single Responsibility Principle
- Open Closed Principle
- Liskov Substitution Principle
- Interface Segregation Principle
- Dependency Inversion Principle
- applying SOLID in Spring Boot
- scenario: refactor a tightly coupled service using SOLID
- scenario: identify which SOLID principle is violated in code
- scenario: design payment/notification modules using SOLID

## Design Patterns

- Singleton
- Factory
- Abstract Factory
- Builder
- Strategy
- Observer
- Decorator
- Adapter
- Proxy
- Facade
- Chain of Responsibility
- Template Method
- scenario: choose a pattern for multiple payment providers
- scenario: choose a pattern for notification channels
- scenario: choose a pattern for request validation chain
- scenario: identify overengineering with patterns

## Java 8 Features

- lambda expressions
- why lambda functions are useful
- functional interfaces
- Predicate, Function, Consumer, Supplier
- Supplier vs Consumer
- Runnable, Comparator, Callable as functional interface examples
- method references
- Streams API
- parallel streams
- CompletableFuture
- asynchronous programming
- Optional
- map, flatMap, filter, reduce
- collectors and grouping
- interface constructors and why interfaces cannot have constructors
- for-loop vs for-each performance
- scenario: process payment list with streams
- scenario: combine multiple API calls using CompletableFuture
- scenario: why parallel stream hurts production performance

## Collections Framework

- ArrayList internals
- ArrayList vs LinkedList
- HashMap internals
- HashMap collision handling
- HashMap resizing and load factor
- HashMap fail-fast behavior
- HashMap vs Hashtable vs ConcurrentHashMap
- HashMap vs ConcurrentHashMap vs synchronizedMap
- ConcurrentHashMap
- TreeMap
- LinkedList
- PriorityQueue
- CopyOnWriteArrayList
- Comparator vs Comparable
- WeakHashMap
- SoftReference
- WeakReference
- mutable keys in HashMap
- HashSet implementation
- fail-fast vs fail-safe iterators
- `Collections.synchronizedMap` vs `ConcurrentHashMap`
- `List.of()` immutability
- List vs Set vs Map and when to use each
- how `hashCode()` is generated and used
- Iterable interface
- scenario: choose collection for top K transactions
- scenario: choose collection for read-heavy configuration list
- scenario: debug HashMap key lookup failure

## Exception Handling

- checked vs unchecked exceptions
- superclass of all exceptions and errors
- `Error` vs `Exception`
- throw vs throws
- exception chaining
- exception propagation
- try-with-resources
- suppressed exceptions
- custom exceptions
- global exception handling
- `@ControllerAdvice`
- `@ExceptionHandler`
- finally behavior
- scenario: design API error response model
- scenario: map payment provider errors to client responses
- scenario: avoid leaking sensitive data in exceptions

## String Handling

- String pool
- `intern()`
- String interning
- String immutability
- why String is immutable
- how String Pool works
- `==` vs `equals()` for strings
- StringBuilder vs StringBuffer
- String vs StringBuilder vs StringBuffer
- String performance optimization
- `new String("abc")` vs string literal
- compile-time concatenation vs runtime concatenation
- scenario: optimize string handling in logging
- scenario: why String is safe as HashMap key

## Multithreading And Concurrency

- thread lifecycle
- synchronization techniques
- `wait()`, `notify()`, `notifyAll()`
- race conditions
- visibility issues
- ExecutorService
- ExecutorService usage and internals
- thread pools
- thread pools and preventing race conditions
- ReentrantLock
- ThreadLocal
- CountDownLatch
- CyclicBarrier
- atomic classes
- volatile vs atomic variables
- Runnable vs Callable
- deadlock
- producer-consumer problem
- blocking queue
- CompletableFuture in concurrent workflows
- CompletableFuture vs Future
- Future vs CompletableFuture
- process vs thread
- virtual threads
- Java 21 virtual threads
- scenario: process 1 million events safely
- scenario: prevent duplicate payment processing under concurrency
- scenario: prevent race conditions in thread pools
- scenario: debug thread pool exhaustion
- scenario: code using 5 threads printing in sequence
- scenario: classic producer-consumer implementation
- scenario: classic deadlock detection and prevention

## Spring Boot Core

- IoC and Dependency Injection
- what Spring Boot is and why it is popular
- what happens internally when a Spring Boot application starts
- bean lifecycle
- explain bean lifecycle in Spring
- bean scopes
- autowiring
- auto configuration
- component scanning
- constructor injection vs field injection
- `@Component`, `@Service`, `@Repository`, `@Controller`
- difference between `@Component`, `@Service`, and `@Repository`
- `@Bean`
- `@Qualifier`
- `@Value`
- `@Autowired`
- `@Async`
- configuration properties
- `application.properties` purpose
- profiles
- Actuator
- scenario: two beans of same type
- scenario: circular dependency
- ways to resolve circular dependency
- scenario: bean not getting created
- scenario: conditional auto-configuration
- scenario: MVC responsibility split between Controller, Service, and Repository
- how `@Transactional` works internally
- transaction propagation in Spring
- Spring Security filter chain
- Filter vs Interceptor

## REST APIs And Web Layer

- `@RequestParam`
- `@PathVariable`
- `@RequestBody`
- JSON and XML handling
- HttpMessageConverters
- CORS configuration
- file upload and download
- idempotency in REST APIs
- validation
- pagination and filtering
- API versioning
- request/response DTOs
- `@Controller` vs `@RestController`
- controller exception handling
- Spring Data pagination with `Pageable`
- unique constraints
- scenario: design idempotent payment API
- scenario: handle duplicate callback
- scenario: file download for large reports
- scenario: secure and validate request body

## Microservices

- API Gateway
- service discovery
- load balancing
- monolithic vs microservices architecture
- how microservices communicate with each other
- circuit breaker
- how to handle service failures in microservices
- service communication
- messaging queues
- distributed systems fundamentals
- REST vs gRPC vs messaging
- synchronous vs asynchronous communication
- saga pattern
- outbox pattern
- idempotency in distributed systems
- bulkhead
- rate limiting
- distributed tracing
- failure handling and tracing in decoupled systems
- eventual consistency
- contract testing for microservices
- scenario: one service is down
- scenario: payment success but notification failed
- scenario: duplicate messages from queue
- scenario: split monolith into services
- scenario: avoid cascading failure
- scenario: service communication using messaging queues
- distributed transactions across microservices

## SQL And Databases

- joins
- SQL vs NoSQL
- indexes
- composite indexes
- query execution plan
- transactions
- ACID
- isolation levels
- locks
- deadlocks
- optimistic locking
- pessimistic locking
- normalization vs denormalization
- partitioning
- replication
- sharding
- connection pooling
- N+1 query problem
- N+1 query problem causes and fixes
- lazy vs eager loading
- cascade remove vs orphan removal
- Spring Data JPA main interfaces
- how Spring/Hibernate generates SQL
- SQL query: find the 2nd highest salary
- `WHERE` vs `HAVING`
- database indexing
- indexing strategies and B-Tree concepts
- optimize a slow-running query
- SQL query optimization and bottleneck analysis
- SQL window functions: `ROW_NUMBER` vs `RANK` vs `DENSE_RANK`
- SQL duplicate record detection
- scenario: optimize slow transaction search
- scenario: avoid lost update
- scenario: choose index for merchant transaction table
- scenario: reconcile failed payments

## Kafka And Messaging

- topic, partition, offset
- what Kafka is
- Kafka message durability
- Kafka vs traditional databases
- Kafka consumer scaling strategies
- consumer group
- ordering guarantees
- at-least-once delivery
- exactly-once semantics
- idempotent consumer
- retry topics
- dead-letter queues
- schema evolution
- Kafka lag
- replaying events safely
- scenario: process events in order per merchant
- scenario: retry failed events without duplicates
- scenario: poison message handling
- scenario: choose partition key

## Streams API

- what Streams API is
- ways to create streams
- streams vs collections
- intermediate vs terminal operations
- map vs flatMap
- `collect()`
- `findFirst()`
- `findAny()`
- exception handling in streams
- converting stream to collection
- sequential vs parallel streams
- scenario: choose stream vs normal loop
- scenario: debug lazy stream execution
- scenario: handle checked exceptions inside stream pipeline

## Functional Programming

- what a functional interface is
- can a functional interface have default and static methods
- Runnable as functional interface
- Comparator as functional interface
- Callable as functional interface
- lambda vs anonymous class
- pure functions and side effects
- scenario: pass behavior as method argument
- scenario: replace strategy class with lambda

## Redis And Caching

- cache-aside pattern
- write-through vs write-behind
- TTL
- cache stampede
- distributed lock
- Redis data structures
- what Redis is and why we use it
- rate limiter with Redis
- eviction policies
- cache consistency
- scenario: cache merchant config
- scenario: prevent cache stampede
- scenario: implement token bucket rate limiter
- scenario: safely use Redis lock

## System Design / HLD

- rate limiter
- notification system
- payment system
- payment system design
- URL shortener
- design a URL shortener system
- file upload service
- audit logging system
- search system
- analytics dashboard
- reconciliation system
- multi-tenant SaaS
- real-time fraud detection
- queue-based worker system
- high-traffic system design
- high-traffic ticket booking architecture
- scenario: one tenant triggers huge load
- scenario: exactly-once from user perspective
- scenario: provider timeout after payment debit
- scenario: dashboard analytics from events
- database sharding concepts
- configuring multiple databases in Spring Boot

## LLD / Object-Oriented Design

- SOLID principles in code
- strategy pattern LLD
- factory pattern LLD
- observer pattern LLD
- builder pattern LLD
- parking lot
- vending machine
- elevator system
- rate limiter LLD
- notification service LLD
- payment state machine
- logger design
- cache design
- scenario: design extensible notification channels
- scenario: design validation chain
- scenario: design retry policy module

## Security

- authentication vs authorization
- OAuth2
- OAuth basics and role-based access
- JWT
- JWT validation in downstream microservices
- secret key management strategies
- mTLS
- API keys
- password hashing
- SQL injection
- XSS
- CSRF
- SSRF
- replay attacks
- webhook signature validation
- PII masking
- encryption at rest and in transit
- scenario: secure payment callback
- scenario: rotate API keys
- scenario: prevent replay attack
- scenario: mask logs safely

## Testing

- unit testing
- integration testing
- mocking
- `@Mock` vs `@InjectMocks`
- Testcontainers
- contract testing in CI
- load testing
- chaos testing
- flaky tests
- testing asynchronous systems
- scenario: test retry logic
- scenario: test idempotent API
- scenario: test Kafka consumer
- scenario: test Spring Boot controller

## Cloud / DevOps / Observability

- Docker basics
- Kubernetes pod, deployment, service
- horizontal pod autoscaling
- config maps and secrets
- rolling deployment
- blue-green deployment
- canary deployment
- observability
- logs, metrics, traces
- CI/CD pipeline
- CI/CD pipelines and version control strategies
- zero-downtime deployments
- AWS S3 practical use cases
- IAM practical use cases
- production debugging
- production debugging and log analysis
- JVM memory issues in containers
- performance tuning in production
- scenario: CPU high in production
- scenario: memory leak in pod
- scenario: latency spike after deployment
- scenario: rollback bad release
- scenario: handling failures in distributed systems

## Real-World Scenarios

- production debugging scenarios
- JVM memory issue scenarios
- performance tuning scenarios
- caching strategies
- high-traffic system design scenarios
- microservices failure handling
- payment duplicate prevention
- callback idempotency
- database deadlock
- slow API debugging
- queue backlog
- thread pool exhaustion
- GC pause spike
- cache inconsistency
- downstream timeout
- partial failure in distributed transaction

## Coding Questions

- find the first non-repeating character in a string
- find duplicate elements in an array
- reverse a string without using built-in methods
- sorting objects using streams
- longest substring problems
- removing duplicates from collections
- scenario: explain time and space complexity of coding solution
- scenario: solve coding question first with brute force, then optimize

## Project, Managerial, And HR

- introduce yourself and explain your project architecture
- explain your role and responsibilities in the project
- describe a challenging production issue you solved
- how to handle tight deadlines
- how to prioritize multiple tasks
- disagreement with a team member and resolution
- why do you want to join a specific company, for example KPMG
- what motivates you as a developer
- where do you see yourself in the next 5 years
- scenario: explain ownership and impact in a project
- scenario: communicate technical risk to non-technical stakeholders
