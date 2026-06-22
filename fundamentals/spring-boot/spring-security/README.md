# Spring Security — Complete Deep Dive

## 1. Why This Concept Matters

Spring Security is the de facto standard for authentication and authorization in Spring Boot applications. It intercepts every HTTP request through a chain of filters, verifying the caller's identity and permissions before the request reaches your controller. In production, Spring Security protects APIs against unauthorized access, CSRF attacks, session fixation, and many other common vulnerabilities. Understanding its filter chain architecture, authentication flow, JWT integration, and method-level security is essential for any backend engineer. Interviewers test this heavily — security filter chain ordering, how authentication flows through providers, JWT vs session auth tradeoffs, OAuth2 grant types, and common misconfigurations that leave APIs exposed.

Misunderstanding Spring Security causes:
- Exposed endpoints from incorrect `permitAll()` / `authenticated()` routing
- Stateless JWT APIs with CSRF protection enabled (unnecessary 403 errors)
- Session fixation vulnerabilities from not migrating session on login
- OAuth2 resource server misconfiguration (accepting tokens from wrong issuer)
- Method-level security not enabled (`@PreAuthorize` silently ignored)
- Custom filters placed at wrong position in the chain (bypassing authentication)

## 2. Basic Meaning

Spring Security secures Java applications by intercepting HTTP requests through a chain of servlet filters. It handles authentication (verifying identity: who are you?) and authorization (verifying permissions: what can you do?).

**Key vocabulary:**
- **Security Filter Chain**: ordered list of Filter instances that process every HTTP request. Each filter has a specific responsibility (authentication, authorization, CSRF, CORS, session management).
- **Authentication**: the process of verifying a user's identity. Spring Security supports multiple mechanisms: username/password, JWT tokens, OAuth2, LDAP, SAML.
- **Authorization**: the process of determining whether an authenticated user has permission to access a resource. Can be role-based (`hasRole("ADMIN")`), scope-based, or ACL-based.
- **Principal**: the currently authenticated user. Accessible via `SecurityContextHolder.getContext().getAuthentication()`.
- **GrantedAuthority**: a permission granted to the principal. Typically a role (e.g., `ROLE_ADMIN`) or a scope.
- **SecurityContext**: holds the `Authentication` object. Stored in a `ThreadLocal` by `SecurityContextHolder`.
- **SecurityContextHolder**: the central hub. Default mode: `MODE_THREADLOCAL` — each request thread has its own security context. Also supports `MODE_INHERITABLETHREADLOCAL` (context propagates to child threads) and `MODE_GLOBAL`.
- **AuthenticationManager**: the core strategy interface for authentication. Only one method: `authenticate(Authentication)` → returns an authenticated `Authentication` or throws `AuthenticationException`.
- **ProviderManager**: the default implementation of `AuthenticationManager`. Delegates to a list of `AuthenticationProvider` instances. If any provider returns an authenticated token, authentication succeeds. If all providers return null or throw, authentication fails.
- **AuthenticationProvider**: processes a specific type of authentication request. Examples: `DaoAuthenticationProvider` (username/password from database), `JwtAuthenticationProvider` (JWT token validation), `OAuth2LoginAuthenticationProvider` (OAuth2 login).
- **UserDetailsService**: loads user-specific data from a data source. The single method `loadUserByUsername(String username)` returns a `UserDetails` object containing username, password, authorities, and account status flags.
- **PasswordEncoder**: encodes passwords for storage and verifies passwords during authentication. Never store passwords in plain text. `BCryptPasswordEncoder` is the default (salt + hash, computationally expensive to crack). `Argon2PasswordEncoder` is the modern alternative (memory-hard, resistant to GPU attacks).
- **SecurityContextPersistenceFilter**: the first filter in the chain. Loads the `SecurityContext` from `HttpSession` at the start of a request and saves it back at the end. For stateless APIs, this filter is effectively skipped.
- **ExceptionTranslationFilter**: handles `AccessDeniedException` and `AuthenticationException`. Sends a 401 (Unauthorized) or 403 (Forbidden) response, or redirects to the login page.
- **FilterSecurityInterceptor**: the last filter in the chain. Makes the final authorization decision based on the configured `AccessDecisionManager`.
- **CSRF (Cross-Site Request Forgery)**: an attack that tricks a logged-in user into executing unwanted actions on a web application. Spring Security protects against this by requiring a CSRF token for every state-changing request (POST, PUT, DELETE, PATCH).
- **CORS (Cross-Origin Resource Sharing)**: a mechanism that allows restricted resources on a web page to be requested from another domain. Spring Security must be configured to accept CORS headers, otherwise browsers block cross-origin requests.
- **JWT (JSON Web Token)**: a compact, URL-safe token format for stateless authentication. The token contains claims (user info, roles, expiration) signed by the server. The server validates the signature on each request — no session storage needed.
- **OAuth2**: an authorization framework that enables third-party applications to obtain limited access to a user's resources. Spring Security supports Authorization Server, Resource Server, and Client roles.
- **RBAC (Role-Based Access Control)**: authorization based on roles assigned to users. `hasRole('ADMIN')` checks if the user has the `ROLE_ADMIN` authority.
- **Security annotation support**: `@EnableMethodSecurity` (Spring Security 6+) or `@EnableGlobalMethodSecurity` (legacy) enables `@PreAuthorize`, `@PostAuthorize`, `@Secured`, `@RolesAllowed` annotations on controller/service methods.

**What it is NOT:**
- Not a replacement for input validation (SQL injection, XSS protection is separate).
- Not a firewall — it operates at the application layer, not the network layer.
- Not automatic — you must explicitly configure which endpoints are protected and which are public.
- Not a complete identity solution (user registration, password reset, MFA enrollment are typically custom).

## 3. Real Code / Real Example

```java
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

// === 1. SECURITY CONFIGURATION ===
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF: disable ONLY for stateless JWT APIs
            // For session-based auth: .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .csrf(csrf -> csrf.disable())
            
            // Stateless: no HTTP session
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // CORS: allow trusted origins
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Route security: ORDER MATTERS — more specific routes first
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Role-based endpoints
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Everything else: authenticated
                .anyRequest().authenticated()
            )
            
            // Add JWT authentication filter BEFORE the default username/password filter
            .addFilterBefore(new JwtAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class)
            
            // Exception handling
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Unauthorized\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Forbidden\"}");
                })
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt: salt + hash, configurable strength (default 10 rounds)
        // For production: use strength 12-14 (takes ~500ms per hash)
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "https://myapp.com", 
            "https://admin.myapp.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}

// === 2. JWT AUTHENTICATION FILTER ===
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    // In production: inject via constructor from a @Component
    private final JwtTokenProvider tokenProvider = new JwtTokenProvider();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain chain) throws ServletException, IOException {
        
        try {
            String token = extractToken(request);
            
            if (token != null && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                // Set authentication in SecurityContext for this request
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            // Token invalid or expired — clear security context
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}

// === 3. JWT TOKEN PROVIDER ===
public class JwtTokenProvider {
    private final String secret = Base64.getEncoder()
        .encodeToString("my-super-secret-key-change-in-production!".getBytes());
    private final long validityMs = 3600000; // 1 hour
    
    public String createToken(String userId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);
        
        return io.jsonwebtoken.Jwts.builder()
            .setSubject(userId)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, secret)
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            io.jsonwebtoken.Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public Authentication getAuthentication(String token) {
        Claims claims = io.jsonwebtoken.Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
        
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + role)
        );
        
        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }
}

// === 4. USER DETAILS SERVICE ===
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.isActive(),               // enabled
            true,                           // accountNonExpired
            true,                           // credentialsNonExpired
            !user.isLocked(),               // accountNonLocked
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}

// === 5. AUTHENTICATION CONTROLLER ===
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(), 
                    request.getPassword()
                )
            );
            
            String token = tokenProvider.createToken(
                authentication.getName(),
                authentication.getAuthorities().iterator().next().getAuthority()
                    .replace("ROLE_", "")
            );
            
            return ResponseEntity.ok(new LoginResponse(token, "Bearer", 3600000));
            
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
        }
    }
}

// === 6. METHOD-LEVEL SECURITY ===
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    // Only users with ROLE_ADMIN can access
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }
    
    // User can access their own data, ADMIN can access all
    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable String userId) {
        return userService.findById(userId);
    }
    
    // Post-authorization check: verify returned user matches
    @PostAuthorize("returnObject.email == authentication.name or hasRole('ADMIN')")
    @GetMapping("/profile")
    public User getProfile() {
        return userService.findByEmail(
            SecurityContextHolder.getContext().getAuthentication().getName()
        );
    }
    
    // Filter input collection
    @PreFilter("filterObject.owner == authentication.name")
    @PostMapping("/documents")
    public void createDocuments(@RequestBody List<Document> documents) {
        // Only documents owned by current user are processed
    }
}
```

Expected behavior:
```
POST /api/auth/login {"email":"admin@test.com","password":"secret"}
→ 200: {"token":"eyJhbGciOiJIUzI1NiJ9...", "type":"Bearer", "expiresIn":3600000}

GET /api/admin/users (with admin token)
→ 200: [{"id":"1","email":"admin@test.com",...}]

GET /api/admin/users (no token)
→ 401: {"error":"Unauthorized"}

GET /api/admin/users (with user token)
→ 403: {"error":"Forbidden"}

GET /api/public/info
→ 200: (no auth required)
```

## 4. What Happens Internally

**Authentication flow (username/password):**
```
1. Client sends POST /login with email + password
2. UsernamePasswordAuthenticationFilter extracts credentials
3. Creates UsernamePasswordAuthenticationToken(unauthenticated)
4. Delegates to AuthenticationManager.authenticate(token)
5. ProviderManager iterates through AuthenticationProviders
6. DaoAuthenticationProvider:
   a. Calls UserDetailsService.loadUserByUsername(email)
   b. Gets UserDetails (username, password hash, roles)
   c. Uses PasswordEncoder.matches(rawPassword, storedHash)
   d. If matches: creates UsernamePasswordAuthenticationToken(authenticated)
   e. Sets token in SecurityContextHolder
7. SecurityContextPersistenceFilter saves to session (if stateful)
8. FilterSecurityInterceptor checks authorization
9. Request reaches controller
```

**Security Filter Chain (default order for session-based auth):**
```
1. SecurityContextPersistenceFilter
   → Loads SecurityContext from HttpSession into SecurityContextHolder
   → At end: saves back to session

2. LogoutFilter
   → If request matches logout URL: clears session, invalidates

3. UsernamePasswordAuthenticationFilter
   → If request matches login URL: extracts credentials, authenticates

4. DefaultLoginPageGeneratingFilter
   → If login page requested: generates default login HTML

5. BasicAuthenticationFilter
   → If Authorization: Basic header: extracts base64 credentials, authenticates

6. RequestCacheAwareFilter
   → Saves original request for redirect after login

7. SecurityContextHolderAwareRequestFilter
   → Wraps request to provide HttpServletRequest methods (isUserInRole, etc.)

8. AnonymousAuthenticationFilter
   → If no authentication in context: creates anonymous token (role: ROLE_ANONYMOUS)

9. SessionManagementFilter
   → Session fixation protection (change session ID after login)
   → Concurrent session control (limit sessions per user)

10. ExceptionTranslationFilter
    → Catches AccessDeniedException: sends 403 or redirects to login
    → Catches AuthenticationException: sends 401

11. FilterSecurityInterceptor
    → Makes final authorization decision
    → Uses AccessDecisionManager (voter-based or consensus-based)
    → Throws AccessDeniedException if not authorized
```

**JWT authentication flow:**
```
1. Client sends login request
2. Server validates credentials → creates JWT token (signed + expired)
3. Client stores token (localStorage, memory, httpOnly cookie)
4. Client sends every subsequent request with Authorization: Bearer <token>
5. JwtAuthenticationFilter (custom, added before UsernamePasswordAuthenticationFilter):
   a. Extracts token from Authorization header
   b. Validates signature (HMAC or RSA)
   c. Validates expiration (not expired)
   d. Extracts claims (userId, roles)
   e. Creates UsernamePasswordAuthenticationToken(authenticated)
   f. Sets in SecurityContextHolder
6. FilterSecurityInterceptor checks authorization
7. Request reaches controller
```

**CSRF protection:**
```
Without CSRF:
  1. User logs in → gets session cookie
  2. Attacker sends <img src="https://bank.com/transfer?to=attacker&amount=1000">
  3. Browser auto-sends session cookie → transfer executes!
  4. User's money stolen!

With CSRF:
  1. Server generates CSRF token, sends to client
  2. Client includes token in X-XSRF-TOKEN header
  3. Attacker's <img> tag cannot read the token (CORS prevents)
  4. Server validates token → rejects attacker's request
```

## 5. Tricky Interview Cases

**Case 1 — CSRF on stateless API**
```java
http
    .csrf(csrf -> csrf.disable()) // REQUIRED for stateless JWT
    .sessionManagement(session -> session.sessionCreationPolicy(STATELESS));
```
Problem: CSRF protection checks for a CSRF token on state-changing requests. JWT APIs are stateless — no session cookie to steal. CSRF is unnecessary and causes 403 errors for valid requests.
When CSRF is needed: session-based auth (traditional server-rendered apps).

**Case 2 — Self-invocation with @PreAuthorize**
```java
@Service
public class OrderService {
    @PreAuthorize("hasRole('ADMIN')")
    public void adminOperation() { ... }
    
    public void caller() {
        adminOperation(); // @PreAuthorize IGNORED! Self-invocation bypasses proxy
    }
}
```
Output: `caller()` can invoke `adminOperation()` without ADMIN role.
Fix: Inject self: `@Autowired private OrderService self;` then `self.adminOperation()`.

**Case 3 — CORS preflight with Authorization header**
```
Browser sends OPTIONS request (preflight) with:
  Origin: https://myapp.com
  Access-Control-Request-Method: POST
  Access-Control-Request-Headers: Authorization

Server must respond with:
  Access-Control-Allow-Origin: https://myapp.com
  Access-Control-Allow-Methods: POST, GET, OPTIONS
  Access-Control-Allow-Headers: Authorization
  Access-Control-Max-Age: 3600

If OPTIONS returns 401 (because no auth token): browser blocks the actual request.
```
Fix: Ensure CORS configuration allows OPTIONS without authentication:
```java
.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
```

**Case 4 — Multiple AuthenticationProviders**
```java
@Bean
public AuthenticationManager authManager(AuthenticationConfiguration config) {
    // ProviderManager chains providers in order
    // First one to respond non-null wins
    ProviderManager pm = (ProviderManager) config.getAuthenticationManager();
    pm.getProviders().add(0, new JwtAuthenticationProvider()); // check JWT first
    return pm;
}
```
Output: JwtAuthenticationProvider checked first for every request. If it can authenticate (valid JWT), username/password provider is skipped.

**Case 5 — Authority prefix and hasRole vs hasAuthority**
```java
// hasRole("ADMIN") checks for "ROLE_ADMIN" authority
// hasAuthority("ROLE_ADMIN") also checks for "ROLE_ADMIN"
// hasAuthority("WRITE") checks for "WRITE" (no prefix)

// If UserDetailsService returns:
List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("WRITE"))

// Then:
requestMatchers("/admin").hasRole("ADMIN")  // matches ROLE_ADMIN
requestMatchers("/write").hasAuthority("WRITE")  // matches WRITE
```

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| CSRF enabled for stateless JWT APIs | 403 errors on all POST/PUT/DELETE | `csrf().disable()` for stateless |
| `@EnableMethodSecurity` missing | `@PreAuthorize` silently ignored | Add `@EnableMethodSecurity` (Spring 6+) |
| CORS preflight blocked (OPTIONS 401) | Browser blocks cross-origin requests | Permit OPTIONS without auth |
| `permitAll()` for routes that need CSRF | CSRF vulnerability on stateful apps | Keep CSRF for session-based routes |
| Passwords stored in plain text | Security breach if DB compromised | Always `BCryptPasswordEncoder` |
| JWT secret hardcoded in source code | Secret exposed in git | Inject via environment variable |
| Token never expires | Token valid forever if leaked | Set short expiry (15-60 min) + refresh token |
| Self-invocation of secured methods | Authorization check bypassed | Inject self reference |
| `SecurityContextHolder.clearContext()` not called | Forgotten user context persists between requests | Clear in filter for stateless apps |

## 7. Production Usage

**OAuth2 Resource Server (JWT with public key verification):**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.myapp.com
          jwk-set-uri: https://auth.myapp.com/.well-known/jwks.json
```
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthConverter())
            )
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
```

**Rate limiting with Spring Security:**
```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimiter rateLimiter;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain) 
            throws ServletException, IOException {
        
        String clientIp = request.getRemoteAddr();
        if (!rateLimiter.isAllowed(clientIp)) {
            response.setStatus(429); // Too Many Requests
            return;
        }
        chain.doFilter(request, response);
    }
}
```

**Security context in async operations:**
```java
@Configuration
public class AsyncConfig {
    @Bean
    public Executor asyncExecutor() {
        // Propagate SecurityContext to async threads
        return new DelegatingSecurityContextExecutorService(
            Executors.newFixedThreadPool(10)
        );
    }
}
```

## 8. Advanced Details

- **Spring Security 6 vs 5**: Security 6 uses lambda DSL (`http.authorizeHttpRequests(auth -> ...)`) instead of the deprecated `http.authorizeRequests()`. `WebSecurityConfigurerAdapter` is removed. Use `SecurityFilterChain` bean.
- **Perfect forward secrecy**: Use ECDSA (ES256) instead of HMAC (HS256) for JWT signing. With HMAC, the same secret signs and verifies. With ECDSA, different keys — if the public key is compromised, tokens can't be forged.
- **Token revocation**: JWT tokens cannot be revoked before they expire (they're self-contained). For immediate revocation: maintain a server-side blocklist (Redis set of revoked token IDs). Or use short expiry (15 min) + refresh tokens.
- **Security headers**: Spring Security includes default security headers via `.headers()`:
  - `X-Content-Type-Options: nosniff`
  - `X-Frame-Options: DENY`
  - `Strict-Transport-Security: max-age=31536000`
  - `X-XSS-Protection: 0`
- **Testing**: `@WithMockUser(roles = "ADMIN")` for controller tests, `@WithUserDetails("test@user.com")` for integration tests.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between authentication and authorization in Spring Security?
A: Authentication verifies identity — who the user is (login with email + password). Authorization verifies permissions — what the user can do (has ADMIN role, can access /api/admin). Spring Security processes authentication first (via filters), then authorization (via FilterSecurityInterceptor or @PreAuthorize).

### Intermediate
Q: How does Spring Security's filter chain work? What is the purpose of the exception translation filter?
A: Spring Security processes every HTTP request through a chain of servlet filters. Each filter has a specific responsibility. The default chain includes: SecurityContextPersistenceFilter (loads/saves SecurityContext), LogoutFilter, UsernamePasswordAuthenticationFilter (login), ExceptionTranslationFilter (handles auth failures), FilterSecurityInterceptor (authorization decision). The ExceptionTranslationFilter catches two exceptions: AuthenticationException (sends 401) and AccessDeniedException (sends 403). It also triggers authentication entry points (login redirect for browser clients).

### Senior
Q: You're building a microservice architecture with API gateways. Each service needs to validate JWT tokens without calling the auth service on every request. How do you implement this? What happens when the auth service signing key rotates?
A: Use OAuth2 Resource Server with public key verification:
1. Auth service publishes its public key at a JWKS endpoint (`/.well-known/jwks.json`)
2. Each microservice configures `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`
3. Spring Security fetches the public keys, caches them, and uses them to verify JWT signatures locally
4. No network call per request — zero latency for auth verification

Key rotation:
5. Auth service generates new key pair, adds new JWK to JWKS response (keeping old key for grace period)
6. Spring Security's `NimbusJwtDecoder` periodically refreshes the JWKS cache
7. Tokens signed with the old key are valid until they expire (grace period)
8. Old key is removed from JWKS after all tokens signed with it have expired

### Tricky
Q: User A authenticates with valid credentials. User A's session is still active. An attacker steals the session cookie. How do you prevent the attacker from using the stolen session?
A: Multi-layered defense:
1. **Session fixation protection**: Spring Security changes the session ID after login (`SessionManagementFilter`). The attacker cannot force a known session ID.
2. **HTTPS only**: `HttpOnly` and `Secure` flags on session cookie prevent JavaScript access and ensure TLS-only transmission.
3. **IP binding**: Store client IP in session. On each request, compare `request.getRemoteAddr()` with stored IP. Mismatch → invalidate session.
4. **Device fingerprinting**: Store User-Agent and other headers in session. Detect anomalies.
5. **Short session timeout**: Max session age (e.g., 30 minutes of inactivity).
6. **Refresh token rotation**: For JWT: use refresh tokens that are single-use. Each refresh issues a new access token AND a new refresh token. If a refresh token is used twice (attacker + legitimate user), both are revoked.
7. **Multi-factor authentication**: Even with a valid session, require MFA for sensitive operations.

## 10. Final 30-Second Answer

Spring Security = filter chain around every HTTP request. **Authentication**: UserDetailsService loads user, PasswordEncoder verifies password, AuthenticationManager/ProviderManager delegates to providers. **Authorization**: route rules (`.permitAll()`, `.authenticated()`, `.hasRole()`), method security (`@PreAuthorize`, `@PostAuthorize`). **JWT**: stateless, custom filter extracts token from Authorization header, validates signature + expiry, sets SecurityContext. **OAuth2**: Resource Server validates JWTs using JWKS endpoint (no network call per request). **CSRF**: disable for stateless JWT APIs. **CORS**: configure allowed origins/methods for browser-based clients. Never: hardcode JWT secret, skip CSRF on session-based API, forget `@EnableMethodSecurity`, expose OPTIONS without auth.