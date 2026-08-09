# Module 6b — Spring Boot, MVC, Security — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.
> 
> 

---

Perfect. This will be the last Spring recap.

We'll cover **Spring Security + JWT + OAuth2 + Cache + Async + Testing** in one chapter.

---

# Spring Security (7+ Years Interview Recap) ⭐⭐⭐⭐⭐

---

# 1. Why Spring Security?

Without Security

```text
Client
   │
   ▼
Controller
   │
Database
```

Anyone can call APIs.

With Spring Security

```text
Client
   │
   ▼
Security Filter Chain
   │
Authentication
   │
Authorization
   │
Controller
```

Every request passes through Security before reaching your controller.

---

# 2. Authentication vs Authorization ⭐⭐⭐⭐⭐

People confuse these.

## Authentication

> Who are you?

Example

```text
Username

Password

↓

Verified
```

Identity verification.

---

## Authorization

> What are you allowed to do?

```text
Admin

↓

Can Delete

User

↓

Cannot Delete
```

Permission check.

---

# 3. Spring Security Request Flow ⭐⭐⭐⭐⭐

```text
HTTP Request
      │
      ▼
Security Filter Chain
      │
      ▼
Authentication Filter
      │
      ▼
Authenticate User
      │
      ▼
Create Authentication Object
      │
      ▼
SecurityContext
      │
      ▼
Authorization Check
      │
      ▼
Controller
```

**Interview Question**

> What is the SecurityContext?

Answer:

> It stores the authenticated user's information for the current request/thread.

---

# 4. JWT (JSON Web Token) ⭐⭐⭐⭐⭐

Traditional Session Authentication

```text
Login

↓

Server Creates Session

↓

Session Stored

↓

Every Request Uses Session
```

Problem

Server must store sessions.

---

### JWT

```text
Login

↓

Server Generates JWT

↓

Client Stores JWT

↓

Authorization: Bearer Token

↓

Server Validates JWT

↓

Controller
```

No session storage required.

Stateless authentication.

---

## JWT Structure

```text
Header

.

Payload

.

Signature
```

Example

```text
xxxxx.yyyyy.zzzzz
```

### Header

Algorithm

Token type

---

### Payload

Claims

```text
User Id

Username

Roles

Expiry
```

---

### Signature

Used to verify token integrity.

If payload changes,

Signature becomes invalid.

---

# 5. JWT Authentication Flow ⭐⭐⭐⭐⭐

```text
User Login

      │

Username & Password

      │

AuthenticationManager

      │

Success

      │

Generate JWT

      │

Return JWT

────────────────────────

Next Request

↓

Bearer Token

↓

JWT Filter

↓

Validate Token

↓

SecurityContext

↓

Controller
```

---

# 6. OAuth2 ⭐⭐⭐⭐

Problem

Don't want to manage passwords.

Example

```text
Login with Google

Login with GitHub

Login with Microsoft
```

Flow

```text
Client

↓

Google Login

↓

Google Authenticates

↓

Authorization Code

↓

Application

↓

Access Token

↓

User Logged In
```

Interview

> OAuth2 delegates authentication to a trusted provider.

---

# 7. Spring Cache ⭐⭐⭐⭐

Without Cache

```text
Client

↓

Database

↓

Response
```

Every request hits DB.

---

With Cache

```text
Client

↓

Cache

↓

Found?

↓

Yes

↓

Return

↓

No

↓

Database

↓

Cache

↓

Response
```

Annotations

```java
@Cacheable

@CachePut

@CacheEvict
```

Popular cache providers

* Redis
* Caffeine

---

# 8. Scheduling ⭐⭐⭐

Run jobs automatically.

```java
@Scheduled
```

Examples

* Cleanup Job
* Report Generation
* Notification Job

---

# 9. Async Processing ⭐⭐⭐⭐

Without Async

```text
Request

↓

Email

↓

Wait

↓

Response
```

---

With Async

```text
Request

↓

Return Response

↓

Background Thread

↓

Send Email
```

Annotation

```java
@Async
```

Uses TaskExecutor.

---

# 10. Spring Events ⭐⭐⭐

Publisher

```text
Order Created

↓

Publish Event
```

Listeners

```text
Email

Audit

Notification
```

Loose coupling.

---

# 11. Spring Boot Testing ⭐⭐⭐⭐

Common Test Types

```text
Unit Test

↓

Mockito

──────────────

Web Layer

↓

@WebMvcTest

──────────────

JPA Layer

↓

@DataJpaTest

──────────────

Full Integration

↓

@SpringBootTest
```

Interview

Difference between

```text
@SpringBootTest

vs

@WebMvcTest
```

---

# Security Architecture

```text
Client
   │
   ▼
Security Filter Chain
   │
   ▼
Authentication
   │
   ▼
SecurityContext
   │
   ▼
Authorization
   │
   ▼
Controller
```

---

# JWT Flow

```text
Login

↓

AuthenticationManager

↓

JWT Generated

↓

Client Stores JWT

↓

Bearer Token

↓

JWT Filter

↓

Validate

↓

SecurityContext

↓

Controller
```

---

# Spring Cache Flow

```text
Request

↓

Cache

↓

Found?

├── Yes → Return

└── No

      ↓

   Database

      ↓

 Store in Cache

      ↓

   Return
```

---

# Frequently Asked Interview Questions ⭐⭐⭐⭐⭐

### Spring Security

* Authentication vs Authorization?
* Explain Spring Security Filter Chain.
* What is SecurityContext?
* How does JWT authentication work?
* Why is JWT stateless?
* JWT vs Session Authentication?
* Where is JWT stored?
* What happens if JWT expires?

### OAuth2

* OAuth2 vs JWT?
* Login with Google flow?
* Authorization Code Flow?

### Cache

* `@Cacheable` vs `@CachePut` vs `@CacheEvict`
* When would you use Redis?
* Cache invalidation strategies?

### Async

* How does `@Async` work?
* Difference between Async and Scheduling?

### Testing

* `@SpringBootTest` vs `@WebMvcTest`
* Mockito vs Integration Test?

---

# 5-Minute Revision

```text
Spring Security
        │
Authentication
        │
Authorization
        │
JWT
        │
OAuth2
        │
SecurityContext
        │
Controller

----------------------------

Cache
Async
Scheduling
Events
Testing
```

---

# ⭐ Final Spring Interview Priority (7+ Years)

If you're short on time, prioritise these topics:

### Tier 1 (Asked in almost every interview)

* Bean Lifecycle
* Dependency Injection
* AOP
* Auto Configuration
* DispatcherServlet & Request Flow
* Persistence Context
* Dirty Checking
* `@Transactional`
* Isolation Levels
* Propagation
* Spring Security Flow
* JWT

### Tier 2 (Common follow-ups)

* Hibernate Entity Lifecycle
* First-Level Cache
* Lazy vs Eager Loading
* N+1 Problem
* OAuth2
* Validation
* Global Exception Handling
* Async Processing

### Tier 3 (Know the basics)

* Spring Events
* Scheduling
* Spring Cache
* Spring Boot Testing

With these four recap modules, you've covered **the core Spring topics that appear in the vast majority of senior Java backend interviews**. The next stage would be deeper implementation details and production scenarios around transactions, Hibernate performance, security configuration, and debugging.


## Q1. How does Spring Boot auto-configuration work? What is the @SpringBootApplication annotation?

### 1. Why This Concept Matters
Auto-configuration is what makes Spring Boot "magical." Without understanding it, you can't debug why a bean was/wasn't created, why certain properties are needed, or why your app behaves differently than expected. Interviewers ask this to test **framework internals knowledge**.

### 2. Basic Meaning
**@SpringBootApplication** = @Configuration + @EnableAutoConfiguration + @ComponentScan

**Auto-Configuration**: Spring Boot automatically configures beans based on what's in your classpath. Have `spring-boot-starter-web` on classpath? → Auto-configures DispatcherServlet, Tomcat, Jackson. Have `spring-boot-starter-data-jpa`? → Auto-configures DataSource, EntityManager, TransactionManager.

### 3. What Happens Internally

```
@SpringBootApplication
├── @Configuration (marks class as bean definition source)
├── @EnableAutoConfiguration (enables auto-configuration magic)
│   └── Imports AutoConfigurationImportSelector
│       └── Reads spring.factories from all JARs:
│           META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
│           Lists all *AutoConfiguration classes (e.g., DataSourceAutoConfiguration)
│       └── For each auto-configuration:
│           └── Checks @ConditionalOnClass (is class on classpath?)
│           └── Checks @ConditionalOnMissingBean (no custom bean defined?)
│           └── Checks @ConditionalOnProperty (is property set?)
│           └── If ALL conditions pass → creates beans
└── @ComponentScan (scans current package + sub-packages for @Component, @Service, etc.)
```

**Conditional annotations that control auto-configuration:**
```java
@ConditionalOnClass(DataSource.class)     // Only if DataSource is on classpath
@ConditionalOnMissingBean(DataSource.class) // Only if no custom DataSource defined
@ConditionalOnProperty("spring.datasource.url") // Only if property is set
@ConditionalOnWebApplication             // Only for web applications
```

### 4. DispatcherServlet Flow

```
HTTP Request
  │
  ▼
DispatcherServlet (Front Controller)
  │
  ├── HandlerMapping → finds @RequestMapping method
  │     └── RequestMappingHandlerMapping scans all @Controller/@RestController
  │
  ├── HandlerAdapter → calls the controller method
  │     └── Converts HTTP params to method params (@RequestParam, @PathVariable)
  │     └── Validates @Valid parameters
  │
  ├── Controller → executes business logic, returns ModelAndView or @ResponseBody
  │
  ├── (if @ResponseBody) HttpMessageConverter → JSON serialization (Jackson)
  │
  └── Response sent to client
```

### 5. Common Spring Boot Properties

```yaml
server:
  port: 8080
  tomcat:
    max-threads: 200
    max-connections: 10000

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/app
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: validate  # validate | update | create | create-drop
    show-sql: true
    properties:
      hibernate:
        batch_size: 50
        jdbc.batch_size: 50
```

### 6. Interview Questions

#### Beginner

**Q**: What does @SpringBootApplication include?

**A**: It combines @Configuration (bean definitions), @EnableAutoConfiguration (auto-configures based on classpath), and @ComponentScan (scans package for components). Equivalent to all three annotations combined.

#### Intermediate

**Q**: How does Spring Boot know to configure an embedded Tomcat?

**A**: Auto-configuration reads spring.factories, finds ServletWebServerFactoryAutoConfiguration. @ConditionalOnClass checks if Tomcat classes are on classpath (from spring-boot-starter-web). @ConditionalOnMissingBean checks no custom WebServerFactory. If conditions pass, creates TomcatServletWebServerFactory. Similar for Jetty/Undertow if those dependencies are present instead.

#### Senior

**Q**: You have a custom DataSource bean but Spring Boot still creates another one. Why?

**A**: Your custom DataSource bean must be created BEFORE auto-configuration runs. Auto-configuration runs after custom beans via @ConditionalOnMissingBean. If your DataSource is created by a @Configuration class that's imported AFTER auto-configuration, the condition fails. Fix: (1) Ensure your @Configuration is in a package scanned by @SpringBootApplication; (2) Use @AutoConfigureBefore(DataSourceAutoConfiguration.class); (3) Exclude DataSourceAutoConfiguration: @SpringBootApplication(exclude = DataSourceAutoConfiguration.class).

---

## Q2. Explain Spring Security, JWT, and OAuth2 authentication flow.

### 1. Why This Concept Matters
Security is non-negotiable in production. Understanding authentication/authorization flows is essential for any backend role. JWT and OAuth2 are the most asked security topics in interviews.

### 2. Basic Flow

```
JWT Authentication flow:
1. Client POST /login with username/password
2. Server validates credentials
3. Server creates JWT token (header.payload.signature)
4. Server returns JWT to client
5. Client stores JWT (localStorage, cookie)
6. Client sends JWT in Authorization header for subsequent requests
7. Server validates JWT signature on each request
8. Server extracts user identity from JWT payload
```

### 3. Spring Security Config

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable for REST API
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // No session
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  // Login/register public
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()  // All other endpoints need auth
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Hash passwords, never store plaintext!
    }
}
```

### 4. JWT Structure

```
Header: {"alg": "HS256", "typ": "JWT"}
Payload: {"sub": "user123", "iat": 1516239022, "exp": 1516242622, "roles": ["ADMIN"]}
Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)

JWT = eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIzIn0.signature
```

### 5. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Storing JWT in localStorage | XSS vulnerability | Use httpOnly cookie or in-memory |
| No JWT expiration | Token never expires → stolen token works forever | Set short expiration (15 min) + refresh token |
| Storing plaintext passwords | DB breach exposes all passwords | Use BCrypt (bcrypt is slow by design) |
| Disabling CSRF without understanding | Same-site request forgery for cookie-based auth | For stateless JWT APIs, CSRF is not needed |
| Not validating JWT signature | Anyone can forge tokens | Always verify HMAC signature |

### 6. Interview Questions

#### Beginner

**Q**: Difference between authentication and authorization?

**A**: Authentication = who you are (verify identity — login/password, JWT). Authorization = what you can do (permissions — roles, scopes). Authentication comes first (get token), then authorization checks happen on each request (@PreAuthorize, hasRole).

#### Intermediate

**Q**: How does JWT signature verification work?

**A**: The server creates a JWT by: (1) encoding header + payload as base64; (2) concatenating with "."; (3) hashing with HMAC-SHA256 using a server-only private key; (4) appending the signature. On each request, the server re-computes the HMAC from the header+payload and compares it to the provided signature. If they match → token is authentic and unmodified. If they don't match → token was tampered with → reject.

#### Senior

**Q**: Design a stateless authentication system for a microservices architecture.

**A**: (1) Centralized Auth Service issues JWT tokens. (2) Each microservice validates JWT locally (no need to call auth service for each request — that's stateful). (3) JWT contains user ID and roles/authorities. (4) Share the JWT secret (HMAC) or public key (RSA) across services via config server/Vault. (5) Use short-lived access tokens (15 min) + longer-lived refresh tokens (7 days) stored in httpOnly cookies. (6) Refresh token rotation — each refresh issues new refresh token, invalidates old one. (7) For logout: add token to blacklist (Redis) until natural expiration. (8) API Gateway validates JWT first, then passes user info via headers to downstream services.

**Final 30-Second**: Spring Boot auto-configuration reads classpath dependencies and conditionally creates beans. @SpringBootApplication = @Configuration + @ComponentScan + @EnableAutoConfiguration. Spring Security: JWT auth (stateless, no session) or OAuth2 (delegated auth). Always hash passwords with BCrypt. Validate JWT signature on every request. Use httpOnly cookies for secure token storage.