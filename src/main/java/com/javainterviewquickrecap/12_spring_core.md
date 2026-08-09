# Module 6a — Spring Core: DI, IoC, AOP — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---

I think you meant:

> **What is a Bean in Spring Boot?**

This is one of the most fundamental Spring interview questions.

---

# What is a Bean?

A **Spring Bean** is simply an **object whose lifecycle is managed by the Spring IoC (Inversion of Control) Container**.

Instead of creating objects yourself using `new`, Spring creates them, stores them, injects them wherever needed, and destroys them when the application shuts down.

Think of it as:

> **A Bean = A Java object managed by Spring.**

---

## Without Spring

Normally in Java, we create objects ourselves.

```java
public class Main {

    public static void main(String[] args) {

        EmailService emailService = new EmailService();

        emailService.sendEmail();
    }
}
```

Here,

You are responsible for

* creating object
* maintaining object
* passing object to other classes
* destroying object

---

## With Spring

Instead, Spring creates the object.

```java
@Service
public class EmailService {

    public void sendEmail() {
        System.out.println("Email Sent");
    }
}
```

Now if another class needs it,

```java
@Service
public class UserService {

    @Autowired
    private EmailService emailService;

    public void registerUser() {
        emailService.sendEmail();
    }
}
```

Notice

We never wrote

```java
new EmailService();
```

Spring already created the object and injected it.

---

# Real World Analogy

Imagine a hotel.

Without Spring

You cook your own food.

```
You
 ↓
Buy vegetables
 ↓
Cook
 ↓
Eat
```

With Spring

```
You
 ↓
Ask waiter
 ↓
Hotel kitchen prepares food
 ↓
Waiter serves it
```

You only consume it.

Spring acts like the hotel kitchen.

---

# Why is it called Bean?

Historically, Java used the term **JavaBeans** for reusable components.

Spring adopted the same term.

In Spring,

> **Any object managed by Spring Container is called a Bean.**

It doesn't have to follow old JavaBean conventions (getters/setters, no-arg constructor).

---

# Who creates the Bean?

The **Spring IoC Container**.

Usually,

```
Application Starts
        ↓
Spring Boot Starts
        ↓
Creates ApplicationContext
        ↓
Scans Classes
        ↓
Creates Beans
        ↓
Injects Dependencies
        ↓
Application Ready
```

---

# How does Spring know which class should become a Bean?

Using annotations.

## 1. @Component

```java
@Component
public class EmailService {

}
```

Spring creates one object.

---

## 2. @Service

```java
@Service
public class UserService {

}
```

Exactly same as Component.

Just indicates business logic.

---

## 3. @Repository

```java
@Repository
public class UserRepository {

}
```

Used for DAO layer.

Also converts database exceptions.

---

## 4. @Controller

```java
@Controller
public class HomeController {

}
```

Used for MVC Controllers.

---

## 5. @RestController

```java
@RestController
public class UserController {

}
```

Used for REST APIs.

---

## 6. @Configuration + @Bean

Sometimes we cannot modify the source code (e.g., third-party libraries).

```java
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

Spring will create this bean.

---

# Bean Lifecycle

```
Application Starts
        │
        ▼
Instantiate Bean
        │
        ▼
Inject Dependencies
        │
        ▼
@PostConstruct
        │
        ▼
Bean Ready
        │
        ▼
Application Running
        │
        ▼
@PreDestroy
        │
        ▼
Bean Destroyed
```

---

# Where are Beans Stored?

Inside the **ApplicationContext**.

```
ApplicationContext

│
├── UserService
│
├── EmailService
│
├── UserRepository
│
└── ObjectMapper
```

Whenever someone asks for

```java
@Autowired
private EmailService emailService;
```

Spring looks inside the container.

Returns

```
Existing EmailService Bean
```

instead of creating a new object every time (for singleton scope).

---

# Bean Scopes

By default

```
Singleton
```

One object for the entire application.

Other scopes include:

* Singleton (default)
* Prototype
* Request
* Session
* Application
* WebSocket

Example:

```java
@Scope("prototype")
@Component
public class MyBean {

}
```

Each request for the bean gets a new object.

---

# Bean vs Object

This is a favourite interview question.

| Object                         | Bean                          |
| ------------------------------ | ----------------------------- |
| Created using `new`            | Created by Spring             |
| Managed by JVM                 | Managed by Spring             |
| No dependency injection        | Supports dependency injection |
| No lifecycle callbacks         | Full lifecycle support        |
| Not stored in Spring container | Stored in ApplicationContext  |

Every Bean is an Object.

But **not every Object is a Bean**.

Example

```java
@Service
public class UserService {

}
```

Bean ✔

But

```java
User user = new User();
```

This is just a normal Java object.

---

# How Dependency Injection Works

```
                Spring Container
              --------------------
              |                  |
              | EmailService     |
              | UserRepository   |
              | PaymentService   |
              --------------------
                     |
                     |
                     ▼
              UserService Bean
            /       |         \
EmailService Repository PaymentService
```

Spring resolves the dependencies and injects the appropriate bean automatically.

---

# Interview Questions

### Q1. What is a Spring Bean?

A Java object managed by the Spring IoC container.

### Q2. Who creates Beans?

The Spring IoC Container (`ApplicationContext`).

### Q3. Where are Beans stored?

Inside the `ApplicationContext`.

### Q4. How are Beans created?

* Component scanning (`@Component`, `@Service`, etc.)
* Java configuration using `@Bean`
* XML configuration (legacy)

### Q5. Difference between `@Component` and `@Bean`?

| `@Component`                      | `@Bean`                                                          |
| --------------------------------- | ---------------------------------------------------------------- |
| Applied to a class                | Applied to a method                                              |
| Discovered via component scanning | Explicitly declared in a `@Configuration` class                  |
| Used for classes you own          | Often used for third-party classes or custom bean creation logic |

### Q6. What is the default bean scope?

`singleton`.

---

## Key Takeaway

A **Spring Bean** is a regular Java object whose **creation, configuration, dependency injection, lifecycle, and destruction are managed by the Spring IoC container**. This allows your application components to remain loosely coupled and easier to test and maintain.

Yes, exactly. **Dependency Injection (DI) is one of the ways Spring implements Inversion of Control (IoC).**

A common interview statement is:

> **IoC is the principle, DI is the technique used to achieve it.**

Let's understand this clearly.

---

# What is IoC (Inversion of Control)?

Normally, your class controls the creation of its dependencies.

```java
public class UserService {

    private EmailService emailService = new EmailService();

    public void registerUser() {
        emailService.sendEmail();
    }
}
```

Here:

* `UserService` is responsible for creating `EmailService`.
* The control of object creation is inside `UserService`.

---

## With IoC

```java
@Service
public class UserService {

    @Autowired
    private EmailService emailService;

    public void registerUser() {
        emailService.sendEmail();
    }
}
```

Now:

* `UserService` does **not** create `EmailService`.
* Spring creates it and injects it.

The **control of object creation has been inverted** from your code to the Spring container.

Hence the name **Inversion of Control (IoC)**.

---

# Where does DI come in?

Dependency Injection is the **mechanism** Spring uses to provide dependencies.

Instead of:

```java
EmailService emailService = new EmailService();
```

Spring does something conceptually like:

```java
EmailService emailService = new EmailService();
UserService userService = new UserService(emailService);
```

Your class simply receives the dependency.

---

# Relationship

```text
          IoC
 (Inversion of Control)
           │
           │ achieved using
           ▼
Dependency Injection (DI)
           │
           ▼
Spring injects objects (Beans)
```

Think of it this way:

* **IoC** = "Who controls object creation?" (a design principle)
* **DI** = "How are objects provided?" (an implementation technique)

---

# Real-world Analogy

Imagine you own a restaurant.

### Without IoC

You cook your own food.

```text
You
 │
 ▼
Buy ingredients
 │
 ▼
Cook
 │
 ▼
Eat
```

### With IoC + DI

You order food.

```text
You
 │
 ▼
Restaurant
 │
 ▼
Chef prepares food
 │
 ▼
Waiter serves it
```

* **Restaurant managing the process** = IoC
* **Waiter bringing the food to you** = Dependency Injection

---

# Types of Dependency Injection in Spring

### 1. Constructor Injection (Recommended)

```java
@Service
public class UserService {

    private final EmailService emailService;

    public UserService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

### 2. Setter Injection

```java
@Service
public class UserService {

    private EmailService emailService;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

### 3. Field Injection (Simple but generally discouraged)

```java
@Service
public class UserService {

    @Autowired
    private EmailService emailService;
}
```

Constructor injection is preferred because it makes dependencies explicit, supports immutable fields (`final`), and makes unit testing easier.

---

# Interview Answer (2–3 lines)

> **Inversion of Control (IoC)** is a design principle where the control of object creation and lifecycle is delegated to the Spring container instead of the application code. **Dependency Injection (DI)** is the mechanism Spring uses to implement IoC by automatically supplying the required dependencies to objects.

So your understanding is correct:

> **IoC is the concept (principle), and DI is the implementation technique that Spring primarily uses to achieve IoC.**


Perfect. Let's do a **rapid interview recap**. We'll **skip code unless needed**, focus on **flow + diagrams + interview questions**.

---

# Spring Core - Interview Recap (7+ Years) ⭐⭐⭐⭐⭐

---

# 1. What is Spring?

### Before Spring

```text
Controller

↓

new Service()

↓

new Repository()

↓

new Database()
```

Everything was tightly coupled.

---

### After Spring

```text
               Spring IoC Container

        ┌───────────────────────────────┐
        │ Creates Beans                 │
        │ Injects Dependencies          │
        │ Manages Lifecycle             │
        └──────────────┬────────────────┘
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
   Controller       Service      Repository
```

Spring's main job is:

* Create Objects
* Wire Objects
* Manage Objects

This is called **IoC (Inversion of Control).**

---

# 2. IoC vs DI ⭐⭐⭐⭐⭐

People confuse these.

## IoC

Spring controls object creation.

Instead of

```java
UserService service = new UserService();
```

Spring creates it.

---

## DI (Dependency Injection)

Spring also injects required dependencies.

```text
Controller

      │

needs

      ▼

Service

      │

needs

      ▼

Repository
```

Spring automatically connects them.

> **IoC is the principle. DI is one way Spring implements IoC.**

---

# 3. Bean ⭐⭐⭐⭐⭐

A Bean is simply

> **An object created and managed by the Spring Container.**

Example

```java
@Service
class UserService{}
```

Spring creates one instance and manages it.

---

# 4. Bean Lifecycle ⭐⭐⭐⭐⭐

One of the favourite interview questions.

```text
Application Starts
        │
        ▼
Component Scan
        │
        ▼
Bean Created
        │
        ▼
Dependencies Injected
        │
        ▼
@PostConstruct
        │
        ▼
Bean Ready
        │
        ▼
Application Running
        │
        ▼
@PreDestroy
        │
        ▼
Bean Destroyed
```

Interview

* Difference between constructor and `@PostConstruct`?
* When is `@PreDestroy` called?

---

# 5. Dependency Injection ⭐⭐⭐⭐⭐

Three types

```text
Constructor Injection ⭐⭐⭐⭐⭐ (Preferred)

Setter Injection

Field Injection (Avoid)
```

Why constructor injection?

✔ Immutable

✔ Easier testing

✔ Mandatory dependencies

---

# 6. Bean Scopes ⭐⭐⭐⭐

| Scope     | Meaning                                 |
| --------- | --------------------------------------- |
| Singleton | One bean for entire application         |
| Prototype | New object every request from container |
| Request   | One bean per HTTP request               |
| Session   | One bean per HTTP session               |

Interview

> Default scope?

**Singleton**

---

# 7. Component Stereotypes ⭐⭐⭐⭐

```text
@Component

Generic Bean

↓

@Service

Business Logic

↓

@Repository

Database Layer

↓

@Controller

MVC Controller

↓

@RestController

REST API
```

Mostly semantic, but `@Repository` also enables exception translation.

---

# 8. AOP ⭐⭐⭐⭐⭐

Problem

Suppose every method needs

* Logging
* Transaction
* Security

Without AOP

```text
Business Logic

Logging

Security

Transaction

Business Logic

Logging

Security

Transaction
```

Lots of duplicate code.

---

### Solution

```text
Client

      │

      ▼

Spring Proxy

      │

Logging

↓

Security

↓

Transaction

↓

Actual Method
```

Business code stays clean.

---

## Common Advice

* Before
* After
* Around
* AfterReturning
* AfterThrowing

Interview

* How does Spring AOP work?

Answer

> Using **JDK Dynamic Proxy** or **CGLIB Proxy**.

---

# 9. Circular Dependency ⭐⭐⭐⭐

```text
A

↓

depends on

↓

B

↓

depends on

↓

A
```

Problem

Spring cannot construct either bean.

Solution

* Redesign
* Constructor Injection exposes the issue early
* `@Lazy` can break the cycle (not ideal)

---

# 10. Spring Events ⭐⭐⭐

Spring supports publish-subscribe inside the application.

```text
Order Created

      │

Publish Event

      │

───────────────

Listener 1

Listener 2

Listener 3
```

Useful for

* Email
* Audit
* Notifications

without tightly coupling components.

---

# Bean Lifecycle (One Diagram to Remember)

```text
@Component

      │

Constructor

      │

Dependency Injection

      │

@PostConstruct

      │

Bean Ready

      │

Application Running

      │

@PreDestroy
```

---

# Spring Core Interview Questions ⭐⭐⭐⭐⭐

### Very Common

* What is IoC?
* IoC vs DI?
* What is a Spring Bean?
* Explain Bean Lifecycle.
* Constructor Injection vs Field Injection.
* Bean Scopes.
* Singleton Bean thread safety?
* How does AOP work internally?
* JDK Proxy vs CGLIB?
* What is Circular Dependency?
* Difference between `@Component`, `@Service`, and `@Repository`?
* How do Spring Events work?

---

# 5-Minute Revision

```text
Spring

↓

IoC Container

↓

Creates Beans

↓

Injects Dependencies

↓

Bean Lifecycle

↓

Scopes

↓

AOP (Proxy)

↓

Events
```

---

## ⭐ Next Chapter

The natural continuation is:

**Spring Boot & Spring MVC**

We'll cover:

* `@SpringBootApplication`
* Auto Configuration (one of the most asked questions)
* Embedded Tomcat
* DispatcherServlet request flow
* Controller → Service → Repository
* Validation
* Global Exception Handling

This is another **20–25 minute recap** and is heavily asked in senior Spring Boot interviews.

# Spring Boot & Spring MVC - Interview Recap (7+ Years) ⭐⭐⭐⭐⭐

This is probably the **most frequently asked Spring Boot module**.

---

# 1. Spring vs Spring Boot ⭐⭐⭐⭐⭐

| Spring               | Spring Boot                           |
| -------------------- | ------------------------------------- |
| Framework            | Opinionated framework built on Spring |
| Manual configuration | Auto Configuration                    |
| External Tomcat      | Embedded Tomcat                       |
| XML/Java Config      | Mostly Annotation Based               |
| More setup           | Minimal setup                         |

**Interview Question**

> Why Spring Boot?

Answer:

* Reduces boilerplate
* Auto Configuration
* Embedded server
* Production-ready features (Actuator, Metrics, Health Checks)

---

# 2. What happens when we start a Spring Boot Application? ⭐⭐⭐⭐⭐

```text
main()

     │

SpringApplication.run()

     │

Create ApplicationContext

     │

Read application.properties

     │

Component Scan

     │

Create Beans

     │

Dependency Injection

     │

Auto Configuration

     │

Embedded Tomcat Starts

     │

DispatcherServlet Registered

     │

Application Ready
```

This entire flow is a favourite interview topic.

---

# 3. @SpringBootApplication ⭐⭐⭐⭐⭐

```java
@SpringBootApplication
public class App {
}
```

Internally combines three annotations.

```text
@SpringBootApplication

        │

        ├── @Configuration

        ├── @EnableAutoConfiguration

        └── @ComponentScan
```

### @Configuration

Marks configuration class.

---

### @ComponentScan

Searches for

* @Component
* @Service
* @Repository
* @Controller

---

### @EnableAutoConfiguration

Automatically configures

* DataSource
* DispatcherServlet
* Jackson
* MVC
* Security (if dependency present)

based on the libraries available on the classpath.

---

# 4. Auto Configuration ⭐⭐⭐⭐⭐

Most asked Spring Boot question.

Suppose you add

```xml
spring-boot-starter-web
```

Spring Boot detects

```text
Starter Present

↓

MVC Required

↓

Configure DispatcherServlet

↓

Configure Jackson

↓

Configure Embedded Tomcat
```

You didn't configure anything manually.

Spring Boot did.

---

## How does Auto Configuration know what to configure?

Simplified flow:

```text
Classpath

↓

@EnableAutoConfiguration

↓

Auto Configuration Classes

↓

Conditional Annotations

↓

Create Beans
```

Examples

```java
@ConditionalOnClass

@ConditionalOnMissingBean

@ConditionalOnProperty
```

Spring creates beans **only if conditions are satisfied**.

---

# 5. Embedded Tomcat ⭐⭐⭐⭐

Before Boot

```text
Application

↓

Build WAR

↓

Deploy on External Tomcat
```

After Boot

```text
Application

↓

Embedded Tomcat

↓

Run

java -jar app.jar
```

Much easier deployment.

---

# 6. Spring MVC Request Flow ⭐⭐⭐⭐⭐

Very common interview question.

```text
HTTP Request

      │

      ▼

Embedded Tomcat

      │

      ▼

DispatcherServlet

      │

      ▼

Handler Mapping

      │

      ▼

Controller

      │

      ▼

Service

      │

      ▼

Repository

      │

      ▼

Database

      │

      ▲

Response Object

      │

      ▼

Jackson (Object → JSON)

      │

      ▼

HTTP Response
```

**Remember this flow.**

---

# 7. DispatcherServlet ⭐⭐⭐⭐⭐

Think of it as the **Front Controller**.

Every request enters here first.

Responsibilities

* Find Controller
* Execute Controller
* Handle Exceptions
* Resolve Views (MVC)
* Convert Objects to JSON/XML

---

# 8. Validation ⭐⭐⭐⭐

Instead of manual validation

```java
if(name == null)
```

Use Bean Validation.

```java
@NotNull

@NotBlank

@Email

@Size
```

Controller

```java
@Valid
```

Flow

```text
HTTP Request

↓

Validation

↓

Valid?

↓

Yes → Controller

No → Exception
```

---

# 9. Global Exception Handling ⭐⭐⭐⭐⭐

Without it

Every controller has

```java
try{
}
catch(Exception e){
}
```

Repeated everywhere.

Instead

```text
Controller

↓

Throws Exception

↓

@ControllerAdvice

↓

Common Error Response
```

Annotations

```java
@ControllerAdvice

@ExceptionHandler
```

Benefits

* Centralised error handling
* Consistent API responses
* Cleaner controllers

---

# 10. Response Flow ⭐⭐⭐⭐

```text
Controller

↓

Java Object

↓

Jackson

↓

JSON

↓

HTTP Response
```

Spring Boot automatically converts objects to JSON using Jackson.

---

# 11. application.properties ⭐⭐⭐⭐

Stores configuration.

Example

```properties
server.port=8080

spring.datasource.url=...

spring.jpa.hibernate.ddl-auto=update
```

Spring Boot binds these properties to beans.

---

# Complete Spring Boot Flow ⭐⭐⭐⭐⭐

```text
Application Starts

        │

SpringApplication.run()

        │

ApplicationContext Created

        │

Component Scan

        │

Dependency Injection

        │

Auto Configuration

        │

Embedded Tomcat

        │

DispatcherServlet

        │

Waiting for HTTP Requests

────────────────────────────────

Client Request

↓

DispatcherServlet

↓

Controller

↓

Service

↓

Repository

↓

Database

↓

Response

↓

Jackson

↓

JSON

↓

Client
```

---

# Frequently Asked Interview Questions ⭐⭐⭐⭐⭐

### Spring Boot

* Spring vs Spring Boot?
* What does `@SpringBootApplication` do?
* Explain Auto Configuration.
* How does Spring Boot know which beans to create?
* What are starter dependencies?
* Why Embedded Tomcat?

### Spring MVC

* Explain the complete request lifecycle.
* What is DispatcherServlet?
* What is HandlerMapping?
* How does Spring return JSON?
* What happens after a request reaches DispatcherServlet?

### Validation

* `@Valid` vs `@Validated`
* How does validation work?

### Exception Handling

* Why `@ControllerAdvice`?
* Difference between local and global exception handling?

---

# 5-Minute Revision

```text
@SpringBootApplication
        │
        ▼
@Configuration
@ComponentScan
@EnableAutoConfiguration
        │
        ▼
ApplicationContext
        │
        ▼
Embedded Tomcat
        │
        ▼
DispatcherServlet
        │
        ▼
Controller
        │
        ▼
Service
        │
        ▼
Repository
        │
        ▼
Database
        │
        ▼
Jackson
        │
        ▼
JSON Response
```

---

## ⭐ Next (Most Important Spring Topic)

Now comes the highest ROI section for a **7+ years Java backend interview**:

**Spring Data JPA + Hibernate + Transactions**

We'll cover together because they're tightly connected:

* Persistence Context
* Entity Lifecycle
* Dirty Checking
* Flush vs Commit
* First-Level Cache
* Lazy vs Eager Loading
* N+1 Problem
* `@Transactional`
* Isolation Levels
* Propagation

This is where interviewers usually spend the most time with senior backend developers.


## Q1. Explain Spring IoC and Dependency Injection. How does Spring create beans?

### 1. Why This Concept Matters
Spring IoC (Inversion of Control) is the foundation of the entire Spring ecosystem. Without understanding how beans are created, scoped, and wired, you can't debug injection failures, scope issues, or circular dependencies. Interviewers ask this to test your grasp of the **container-managed lifecycle**.

### 2. Basic Meaning
**IoC (Inversion of Control)**: Instead of objects creating their dependencies (via `new`), the **container** creates all objects (beans) and injects them where needed.

### 3. Real Code / Real Example

```java
// =====================================================
// WITHOUT Spring (Tight coupling)
// =====================================================
public class OrderService {
    private EmailService emailService = new EmailService();  // Hard-coded!
    private InventoryService inventoryService = new InventoryService();  // Hard-coded!
}

// =====================================================
// WITH Spring (Loose coupling via DI)
// =====================================================

@Component  // Spring creates and manages this bean
public class EmailService {
    public void send(String to, String body) {
        // send email
    }
}

@Component
public class InventoryService {
    public boolean checkStock(String sku, int qty) {
        // check inventory
        return true;
    }
}

@Service  // @Service = @Component (semantic for service layer)
public class OrderService {
    
    // Spring injects these automatically
    private final EmailService emailService;
    private final InventoryService inventoryService;
    
    // Constructor injection (PREFERRED — since Spring 4.3+)
    // No @Autowired needed if single constructor
    public OrderService(EmailService emailService, InventoryService inventoryService) {
        this.emailService = emailService;
        this.inventoryService = inventoryService;
    }
    
    public void placeOrder(Order order) {
        if (inventoryService.checkStock(order.getSku(), order.getQty())) {
            // process order
            emailService.send(order.getEmail(), "Order confirmed!");
        }
    }
}

@Configuration
public class AppConfig {
    // Explicit bean definition (when you can't use @Component)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### 4. What Happens Internally

**Spring Bean Lifecycle:**
```
───────────────────────────────────────────────────────────
PHASE 1: LOADING
───────────────────────────────────────────────────────────
1. Scan classpath for @Component, @Service, @Repository, @Controller
   Scan @Configuration classes for @Bean methods
   
2. Build BeanDefinition objects
   └─ Bean class, scope (singleton/prototype), init/destroy methods
   └─ Dependencies (what needs injection)
   
3. BeanDefinitionRegistry → store all BeanDefinitions

───────────────────────────────────────────────────────────
PHASE 2: INSTANTIATION
───────────────────────────────────────────────────────────
For each bean (handles dependency order):

4. Create instance via constructor
   └─ If constructor has params → resolve those beans first
   └─ Circular dependency? → Early-expose proxy (3-level cache)

5. Property injection
   └─ @Autowired fields
   └─ @Autowired setter methods

6. BeanPostProcessor — BEFORE init
   └─ @PostConstruct → @Autowired → BeanPostProcessor.postProcessBeforeInitialization
   └─ This is where @Autowired works (AutowiredAnnotationBeanPostProcessor)

7. Initialization
   └─ @PostConstruct method
   └─ InitializingBean.afterPropertiesSet()
   └─ Custom init-method

8. BeanPostProcessor — AFTER init
   └─ postProcessAfterInitialization
   └─ If bean is proxy (AOP): returns proxy instead of actual bean

───────────────────────────────────────────────────────────
PHASE 3: READY
───────────────────────────────────────────────────────────
9. Bean is ready for use
   └─ Stored in singleton cache (DefaultSingletonBeanRegistry)

───────────────────────────────────────────────────────────
PHASE 4: DESTRUCTION
───────────────────────────────────────────────────────────
10. @PreDestroy method
11. DisposableBean.destroy()
12. Custom destroy-method
```

**Circular Dependency Resolution (3-level cache):**
```
Spring uses THREE caches to resolve circular dependencies:

Level 1: singletonObjects — Fully initialized beans (ready)
Level 2: earlySingletonObjects — Partially initialized beans (proxy exposed)
Level 3: singletonFactories — ObjectFactory to create early-exposed instances

Example: Bean A depends on Bean B. Bean B depends on Bean A.

1. Spring starts creating A
2. A's constructor needs B → Spring pauses A, starts creating B
3. B's constructor needs A → DEADLOCK!
4. But A was registered in Level 3 (singletonFactories) before it finished
5. B gets the EARLY reference to A (before A's full init)
6. B finishes → stored in Level 1
7. A resumes, gets B from Level 1, finishes → stored in Level 1

NOTE: This only works for SETTER injection!
Constructor injection = can't early-expose → throws BeanCurrentlyInCreationException
```

### 5. Tricky Interview Cases

**Case 1: Field injection vs constructor injection**
```java
@Service
public class UserService {
    // ❌ Field injection — HARD TO TEST, HARD TO MOCK
    @Autowired
    private UserRepository repository;
    
    // ✅ Constructor injection (preferred)
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    // - Immutable (final fields)
    // - Testable (no Spring needed — just call constructor)
    // - Required dependencies are explicit
}
```

**Case 2: Prototype scope in singleton**
```java
@Component
@Scope("prototype")  // New instance every time
class PaymentProcessor {
    // Stateful — each use needs fresh instance
}

@Component
class OrderService {  // Singleton (default scope)
    @Autowired
    private PaymentProcessor processor;  // ❌ Injected ONCE only!
    // processor is injected during singleton creation
    // NEVER gets a new instance even though scope is prototype!
    
    // FIX: Use ObjectFactory or Provider
    @Autowired
    private ObjectFactory<PaymentProcessor> processorFactory;
    
    public void process(Order order) {
        PaymentProcessor fresh = processorFactory.getObject();  // NEW instance!
    }
}
```

**Case 3: @Bean method vs @Component**
```java
// @Component — Spring discovers automatically via component scan
@Component
class MyService {}

// @Bean — Explicit definition, for classes you can't annotate
@Configuration
class AppConfig {
    @Bean
    public MyService myService() {  // For external library classes
        return new MyService();
    }
    
    @Bean
    public RestTemplate restTemplate() {  // Third-party class
        return new RestTemplate();
    }
}
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Field injection | Can't create bean without Spring in tests | Use constructor injection |
| Circular dependency with constructor injection | BeanCurrentlyInCreationException | Use setter injection or @Lazy |
| Forgetting scope for stateful beans | Thread safety issues with singleton | Use prototype scope or ThreadLocal |
| @PostConstruct in @Configuration classes | May be called before all beans are ready | Use @EventListener(ApplicationReadyEvent.class) |
| Not using @Primary for ambiguous beans | NoUniqueBeanDefinitionException | Add @Primary or @Qualifier |
| Forgetting @Autowired on constructor in Spring <4.3 | Constructor not auto-wired | Use @Autowired on constructor (or upgrade to 4.3+) |

### 7. Production Usage

**Bean profiles for environment-specific beans:**
```java
@Configuration
public class DataSourceConfig {
    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
    
    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://prod-db:5432/app")
            .username("${db.user}")
            .password("${db.password}")
            .build();
    }
}
```

**Conditional beans:**
```java
@Bean
@ConditionalOnMissingBean  // Only create if no other CacheManager exists
public CacheManager defaultCacheManager() {
    return new ConcurrentMapCacheManager();
}

@Bean
@ConditionalOnProperty(name = "cache.redis.enabled", havingValue = "true")
public CacheManager redisCacheManager() {
    return RedisCacheManager.builder(redisConnectionFactory()).build();
}
```

### 8. Advanced Details

**Bean scopes:**
| Scope | Description | Use Case |
|-------|-------------|----------|
| singleton | One instance per container (default) | Stateless services |
| prototype | New instance per injection/getBean | Stateful objects |
| request | One instance per HTTP request | Web request context |
| session | One instance per HTTP session | User session data |
| application | One instance per ServletContext | App-wide shared state |
| websocket | One instance per WebSocket | WebSocket state |

### 9. Interview Questions And Answers

#### Beginner

**Q**: What is Dependency Injection in Spring?

**A**: DI means objects receive their dependencies from the container rather than creating them themselves. Instead of `new EmailService()`, Spring injects it. Types: constructor injection (preferred — final fields, testable), setter injection (optional dependencies), field injection (not recommended). Spring uses reflection to find @Autowired dependencies and inject them.

#### Intermediate

**Q**: How does Spring resolve circular dependencies?

**A**: Spring uses a **3-level cache**: singletonObjects (fully created), earlySingletonObjects (partially created proxies), singletonFactories (object factories). When Bean A depends on B and B depends on A: Spring creates A, adds an early reference to Level 3, starts creating B. B needs A → gets the early reference from Level 3. B finishes, stored in Level 1. A finishes, replaces early reference with full bean. This only works for setter injection — constructor injection throws BeanCurrentlyInCreationException for circular deps.

#### Senior

**Q**: Your Spring Boot application takes 5+ minutes to start in production. How do you diagnose and fix it?

**A**: (1) Enable `-Dspring.profiles.active=dev` startup logging at debug level; (2) Check `spring.autoconfigure.exclusion` — many unnecessary auto-configurations run (e.g., JPA if not used); (3) Use `@ConditionalOnClass` and `@ConditionalOnMissingBean` to skip irrelevant configs; (4) Set `spring.main.lazy-initialization=true` to defer bean creation until first use; (5) Use `spring-boot-starter-validation` exclusion if not using validation; (6) Consider Spring Native (AOT compilation) for critical startup time reduction; (7) Profile with -XX:+PrintClassHistogram to identify class loading bottlenecks; (8) Split monolithic app into microservices for independent scaling.

#### Tricky

**Q**: Can you have two beans of the same type in Spring? How do you inject a specific one?

**A**: Yes. If Spring finds multiple beans of the same type, it throws NoUniqueBeanDefinitionException. Resolutions: (1) Mark one with @Primary (default choice); (2) Use @Qualifier("beanName") on both bean definition and injection point; (3) Inject all with `List<OrderService>` (Spring injects all beans of that type); (4) Use @Resource(name="beanName") for JSR-250 style. Best practice: use @Qualifier with meaningful names for clarity.

### 10. Final 30-Second Answer

Spring IoC: container manages bean lifecycle and injects dependencies. Constructor injection preferred. Singleton scope by default. Circular deps resolved via 3-level cache (only for setter injection). Use @Profile for environment configs, @Conditional for optional beans. Lazy initialization for faster startup.