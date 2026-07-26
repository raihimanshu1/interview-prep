# Security — JWT, OAuth2, Spring Security (Explained Simply)

## Chapter 1: Why Security Matters

### The "House Keys" Analogy

```
Your web application is like your house.

Without security:
  Door is always unlocked. Anyone can walk in.
  They can steal your TV (data), sleep in your bed (use your server),
  or change your locks (hijack your account).

JWT      = A keycard that proves "I am Alice" (authentication)
OAuth2    = Giving a friend a TEMPORARY key to your house (authorization)
Spring Security = The lock on your door + the bouncer inside
Filters  = Security checkpoints: "ID please!", "Bag check!", "VIP only!"
```

### What We'll Cover

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  Chapter 1: What is Authentication vs Authorization?         │
│  Chapter 2: JWT — JSON Web Tokens (deep dive)               │
│  Chapter 3: OAuth2 — The Authorization Framework            │
│  Chapter 4: Spring Security Filter Chain (how it works)      │
│  Chapter 5: Implementing JWT Auth in Spring Boot             │
│  Chapter 6: OAuth2 with Spring Security                      │
│  Chapter 7: Common Vulnerabilities & Fixes                   │
│  Chapter 8: All Interview Questions & Answers               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Chapter 2: Authentication vs Authorization

### The Airport Analogy

```
AUTHENTICATION = CHECKING YOUR PASSPORT
  "Who are you? Prove it."
  → You show passport → "You are Alice. Verified."

AUTHORIZATION = CHECKING YOUR BOARDING PASS
  "What are you allowed to do?"
  → You show boarding pass → "You can go to Gate 12, but NOT Gate 5."
```

```java
// ─── AUTHENTICATION (Who are you?) ───
// Login endpoint:
@PostMapping("/login")
public String login(@RequestBody LoginRequest request) {
    // Check username + password
    if (isValidUser(request.username(), request.password())) {
        // ✅ Authenticated! Generate JWT
        return generateJwt(request.username());
    }
    throw new UnauthorizedException("Invalid credentials");
}

// ─── AUTHORIZATION (What can you do?) ───
@GetMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")     // ← Authorization check
public List<User> getAllUsers() {
    return userService.findAll();
    // Only ADMIN role can call this
    // USER role → 403 Forbidden
}
```

**Simple table:**
```
AUTHENTICATION        AUTHORIZATION
─────────────────     ─────────────────
"Are you Alice?"      "Can Alice delete orders?"
Check: password       Check: role/permission
Fails: 401            Fails: 403
UnAuthORized          UnAuthORized
(401)                 (403)
```

---

## Chapter 3: JWT — JSON Web Token (Deep Dive)

### The "Wax Seal Letter" Analogy

```
Imagine you're the King. You send a letter sealed with your royal wax seal.

The letter says: "Bearer of this letter is Sir Lancelot. He may enter the armory."

ROYAL SEAL = JWT Signature (nobody can fake it)
LETTER CONTENTS = JWT Payload (who you are, what you can do)
ANYONE can READ the letter (JWT is NOT encrypted, just signed)
But nobody can FORGE the seal (without the king's signet ring)

JWT = A SELF-CONTAINED token. All information is INSIDE the token.
No need to check a database every time!
```

### What a JWT Looks Like

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsInJvbGUiOiJBRE1JTiJ9.dg4B5HgFfU0GjF

┌─────────────────┐.┌──────────────────────────┐.┌──────────────────┐
│     HEADER      │ │        PAYLOAD           │ │    SIGNATURE     │
│                 │ │                          │ │                  │
│ {               │ │ {                        │ │ HMACSHA256(      │
│  "alg": "HS256",│ │  "sub": "alice",         │ │   base64(header) │
│  "typ": "JWT"   │ │  "role": "ADMIN",         │ │   + "." +        │
│ }               │ │  "iat": 1700000000,       │ │   base64(payload)│
│                 │ │  "exp": 1700003600        │ │   + secret)      │
│                 │ │ }                        │ │                  │
└─────────────────┘ └──────────────────────────┘ └──────────────────┘
```

### JWT Structure — 3 Parts, Base64-Encoded, Dot-Separated

```java
// ─── REAL JWT EXAMPLE ───
String jwt = "eyJhbGciOiJIUzI1NiJ9"           // HEADER
           + "."
           + "eyJzdWIiOiJhbGljZSIsInJvbGUiOiJBRE1JTiJ9"  // PAYLOAD
           + "."
           + "dg4B5HgFfU0GjF";                        // SIGNATURE

// ─── DECODED HEADER ───
{
  "alg": "HS256",        // Algorithm used to sign
  "typ": "JWT"           // Token type
}

// ─── DECODED PAYLOAD ───
{
  "sub": "alice",        // Subject (who is this token for?)
  "role": "ADMIN",       // Custom claim (what can they do?)
  "iat": 1700000000,     // Issued At (when was it created?)
  "exp": 1700003600      // Expiration (when does it expire?)
}
```

### How JWT is Created (Signing)

```java
// ─── THIS IS WHAT HAPPENS WHEN YOU LOG IN ───

public String generateJwt(String username, String role) {
    // Step 1: Create HEADER (algorithm + type)
    String header = "{"alg":"HS256","typ":"JWT"}";
    String encodedHeader = base64Encode(header);
    // → "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    
    // Step 2: Create PAYLOAD (claims)
    String payload = "{"sub":"" + username + "","role":"" + role + ","exp":1700003600}";
    String encodedPayload = base64Encode(payload);
    // → "eyJzdWIiOiJhbGljZSIsInJvbGUiOiJBRE1JTiIsImV4cCI6MTcwMDAwMzYwMH0"
    
    // Step 3: Create SIGNATURE
    // Take header + payload + SECRET KEY → sign with algorithm
    String signature = HMACSHA256(encodedHeader + "." + encodedPayload, SECRET_KEY);
    // SECRET_KEY = "my-super-secret-key-that-nobody-knows"
    // → "dg4B5HgFfU0GjF"
    
    // Step 4: Combine all 3 parts with dots
    return encodedHeader + "." + encodedPayload + "." + signature;
}
```

### How JWT is Verified (Validation)

```java
// ─── THIS IS WHAT HAPPENS ON EVERY REQUEST ───

public boolean verifyJwt(String jwt) {
    // Step 1: Split by dots → need exactly 3 parts
    String[] parts = jwt.split("\\.");
    if (parts.length != 3) return false;
    // If we got only 2 parts → forged token!
    
    String header = parts[0];
    String payload = parts[1];
    String signature = parts[2];
    
    // Step 2: RECALCULATE signature using our SECRET KEY
    String expectedSignature = HMACSHA256(header + "." + payload, SECRET_KEY);
    
    // Step 3: Compare signatures
    if (!signature.equals(expectedSignature)) {
        // Signatures don't match → someone TAMPERED with the token!
        return false;
    }
    
    // Step 4: Check expiration
    String decodedPayload = base64Decode(payload);
    long exp = extractExpiry(decodedPayload);
    if (System.currentTimeMillis() > exp) {
        // Token expired → must login again
        return false;
    }
    
    // ✅ All checks passed. Token is valid!
    return true;
}
```

### Why JWT is Better Than Session Cookies

```
SESSION COOKIES (Old way):                    JWT (New way):
─────────────────────────                     ─────────────────
Server stores session in database             Server stores NOTHING
Every request: check DB for session           Every request: verify signature (no DB!)
Heavy under load                              Scales to ANY number of servers
Need sticky sessions (same server)            Any server can verify (stateless)

Big Problem with Sessions:                    Big Win with JWT:
User logs into Server A.                      User logs anywhere.
Server B doesn't know about it.               Token is self-contained.
"The session doesn't exist!"                  ALL servers can verify it.
                                               NO database lookup needed!
```

### JWT Pros and Cons

```
✅ PROS:
  Stateless: No server-side storage
  Self-contained: All info in the token (user ID, role, permissions)
  Portable: Works across different services (microservices)
  Scalable: No session store needed
  Mobile-friendly: Works with iOS, Android, Web

❌ CONS:
  Cannot be revoked: Once issued, token is valid until expiry
  Large: Bigger than session cookie (but usually fine)
  Secret must be kept safe: If leaked, anyone can forge tokens
  Payload is NOT encrypted: Anyone can read it (just signed)
```

**Key rule:** JWT should have SHORT expiry (15-60 minutes). Use REFRESH TOKENS for long-lived sessions.

```
JWT expiry = 15 minutes → If stolen, attacker has 15 min access
Refresh token = 7 days  → Can issue new JWTs without re-login
            → Can be revoked (stored in DB, unlike JWT)
```

---

## Chapter 4: OAuth2 — The Authorization Framework

### The "Valet Key" Analogy

```
You have a house. You hire a house cleaner.

You give them a VALET KEY:
  ✓ Opens the front door
  ✓ Opens the kitchen and living room
  ✗ Does NOT open your bedroom safe
  ✗ Does NOT work after 6 PM

OAuth2 is like giving different valet keys to different services:
  - "Google Calendar, you can READ my events"
  - "Spotify, you can PLAY music"
  - "Photo Editor, you can VIEW my photos"
  All WITHOUT giving them your username and password!
```

### What Problem Does OAuth2 Solve?

```
BEFORE OAuth2:
  App: "Enter your Gmail password so I can read your emails"
  You: "But... that's my password. You could read ALL my emails!"
  Problem: App gets FULL access, and you can't limit it.

AFTER OAuth2:
  App: "Redirecting you to Google..."
  Google: "App wants to read your emails. Allow?"
  You: "Yes, just emails"
  Google gives App a TOKEN that ONLY works for reading emails.
  App can't delete emails, can't see your contacts, can't change password.
```

### OAuth2 Roles (The 4 Characters)

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  RESOURCE OWNER = You (the user who owns the data)          │
│     └─ "I have photos on Google Photos"                     │
│                                                             │
│  CLIENT = The app requesting access                         │
│     └─ "I'm a photo printing service. I need your photos."   │
│                                                             │
│  AUTHORIZATION SERVER = Google's auth system                 │
│     └─ "I verify your identity and issue tokens"            │
│                                                             │
│  RESOURCE SERVER = Google Photos API                         │
│     └─ "I serve the photos (if you have a valid token)"      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### OAuth2 Flow (Authorization Code Grant) — Step by Step

This is the most common and SECURE flow.

```
USER (Resource Owner)        APP (Client)          Google (Auth Server)   Google Photos
│                              │                        │                    │
│  1. "Print my photos"        │                        │                    │
│─────────────────────────────▶│                        │                    │
│                              │                        │                    │
│  2. Redirect to Google       │                        │                    │
│◀─────────────────────────────│                        │                    │
│                              │                        │                    │
│  3. "App wants photos. OK?"  │                        │                    │
│──────────────────────────────────────────────────────▶│                    │
│                              │                        │                    │
│  4. "Yes, allow"            │                        │                    │
│◀──────────────────────────────────────────────────────│                    │
│                              │                        │                    │
│  5. "Here's an auth code"    │                        │                    │
│─────────────────────────────▶│                        │                    │
│                              │                        │                    │
│  6. "Exchange code for token"│                        │                    │
│                              │──────────────────────────────────────────▶│
│                              │                        │                    │
│  7. "Here's an access token" │                        │                    │
│                              │◀──────────────────────────────────────────│
│                              │                        │                    │
│  8. "Get my photos" (with token)                     │                    │
│                              │───────────────────────────────────────────│
│                              │                        │                    │
│  9. Photos received          │                        │                    │
│                              │◀───────────────────────────────────────────│
```

### OAuth2 Grant Types (Which One to Use)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                         │
│  AUTHORIZATION CODE (with PKCE) — ✅ RECOMMENDED for most apps          │
│  └─ Used by: Web apps, Mobile apps, SPAs                               │
│  └─ Why: Most secure. Code + secret + PKCE.                             │
│                                                                         │
│  CLIENT CREDENTIALS — Server-to-server (NO user involved)               │
│  └─ Used by: Microservices talking to each other                        │
│  └─ Example: Order Service calls Payment Service                        │
│  └─ No user context. Only "which app" matters.                          │
│                                                                         │
│  AUTHORIZATION CODE (without PKCE) — For server-side web apps           │
│  └─ Has client_secret (stored safely on server)                        │
│                                                                         │
│  IMPLICIT GRANT — ❌ DEPRECATED (was for SPAs, now replaced by PKCE)    │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### OAuth2 vs JWT — The Relationship

```
OAuth2 is the "HOW" — the protocol for getting access
JWT is the "WHAT" — the format of the access token

OAuth2 SAYS: "To access the API, present a token in the Authorization header"
JWT IS:      "Here's what that token looks like: eyJhbGciOiJIUzI1NiJ9..."

Think of it this way:
  OAuth2 = The process of checking into a hotel
  JWT    = The keycard they give you
```

---

## Chapter 5: Spring Security Filter Chain (Behind the Scenes)

### The "Bouncer at a Club" Analogy

```
You arrive at a VIP club. There are MULTIPLE checkpoints:

1. DOOR GUARD: "ID please!"          → Authentication filter
2. BAG CHECK: "Any weapons?"         → Security headers filter
3. MEMBERSHIP CHECK: "Are you VIP?"  → Authorization filter
4. VIP ROOM GUARD: "VIP only!"       → Role-based access filter

This is the SPRING SECURITY FILTER CHAIN.
Each filter does ONE check. If it passes, the next filter runs.
If ALL pass → request reaches your controller.

A chain of filters = Filter Chain
```

### What is the Filter Chain?

```java
// ─── SPRING SECURITY FILTER CHAIN (simplified) ───
// When a request arrives, it goes through these filters IN ORDER:

Request → [Filter 1] → [Filter 2] → [Filter 3] → ... → [Your Controller]

// Each filter can:
//   1. PASS the request to the next filter (do nothing)
//   2. MODIFY the request (add authentication, add headers)
//   3. BLOCK the request (return 401/403)
```

### The ACTUAL Spring Security Filter Chain

**Order matters!** Filters run in a specific sequence:

```java
// The default Spring Security filter chain (simplified order):
// (Numbers show the order in the chain)

1.  CsrfFilter                 // CSRF protection (POST/PUT/DELETE checks)
2.  CorsFilter                 // CORS headers for cross-origin requests
3.  LogoutFilter               // Handle /logout requests
4.  UsernamePasswordAuthFilter // Check login form (username + password)
5.  BasicAuthenticationFilter  // Check Basic Auth header
6.  BearerTokenAuthenticationFilter // ← YOUR JWT FILTER GOES HERE!
                                    // Checks "Authorization: Bearer eyJ..."
7.  RequestCacheFilter         // Save requests for later (after login redirect)
8.  SecurityContextHolderFilter// Put authentication in SecurityContext
9.  AnonymousAuthenticationFilter // If no auth → set "anonymous" user
10. SessionManagementFilter    // Session fixation protection
11. ExceptionTranslationFilter // Convert auth errors to 401/403
12. FilterSecurityInterceptor  // Check @PreAuthorize, URL security rules
    │
    ▼
Your Controller 🎉
```

### How JWT Authentication Works in the Filter Chain

```java
// ─── STEP-BY-STEP: JWT LOGIN ───

// Step 1: User sends POST /login with username + password
POST /login
Body: {"username": "alice", "password": "password123"}

// Step 2: UsernamePasswordAuthFilter checks credentials
//   → Calls UserDetailsService.loadUserByUsername("alice")
//   → Gets user from database
//   → Checks password matches

// Step 3: If valid, creates a JWT and returns it
Response: {"token": "eyJhbGciOiJIUzI1NiJ9...."}
```

```java
// ─── STEP-BY-STEP: JWT ON EVERY SUBSEQUENT REQUEST ───

// Step 1: Client sends request with JWT in header
GET /api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9....

// Step 2: BearerTokenAuthenticationFilter (our custom filter)
//   a. Extract token from "Authorization: Bearer ..." header
//   b. Verify signature (is it really from OUR server?)
//   c. Check expiration (is it still valid?)
//   d. Extract user details (username = "alice", role = "ADMIN")
//   e. Create Authentication object
//   f. Put in SecurityContextHolder (so controllers can access it)

// Step 3: FilterSecurityInterceptor checks authorization
//   → Does this URL require ADMIN role?
//   → Does "alice" have ADMIN role?
//   → YES → pass to controller
//   → NO  → return 403 Forbidden

// Step 4: Your controller receives the request
@GetMapping("/api/orders")
public List<Order> getOrders() {
    // SecurityContextHolder has the authentication
    String username = SecurityContextHolder.getContext()
        .getAuthentication().getName();
    // "alice"
    return orderService.findByUsername(username);
}
```

### Implementing a Custom JWT Filter

```java
// ─── THIS IS THE CORE OF JWT AUTH IN SPRING BOOT ───
// This filter runs on EVERY request (except /login)

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // ─── STEP 1: Extract JWT from header ───
        String authHeader = request.getHeader("Authorization");
        
        // If no Authorization header → skip this filter
        // (maybe it's a public endpoint like /login or /register)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extract the token (remove "Bearer " prefix)
        String jwt = authHeader.substring(7);
        
        try {
            // ─── STEP 2: Verify and extract user ───
            String username = jwtService.extractUsername(jwt);
            
            // ─── STEP 3: Create Authentication object ───
            // (if not already authenticated in this request)
            if (username != null && 
                SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Load user details (could be from DB or from JWT itself)
                UserDetails userDetails = userService.loadUserByUsername(username);
                
                // Check if token is still valid
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,                  // credentials (null for JWT)
                            userDetails.getAuthorities()  // roles/permissions
                        );
                    
                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token → clear any existing authentication
            SecurityContextHolder.clearContext();
        }
        
        // ─── STEP 4: Continue to next filter ───
        filterChain.doFilter(request, response);
    }
}
```

### The SecurityConfig (Wiring Everything Together)

```java
// ─── SPRING SECURITY CONFIG (Spring Boot 3.x) ───

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize, @Secured annotations
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ─── DISABLE CSRF (because we use JWT, not cookies) ───
            .csrf(csrf -> csrf.disable())
            
            // ─── CORS config ───
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // ─── WHICH URLS ARE PUBLIC VS PROTECTED ───
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()     // Login/Register: public
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin only
                .requestMatchers("/api/orders/**").authenticated() // Any logged-in user
                .anyRequest().authenticated()                      // Everything else: protected
            )
            
            // ─── MAKE SPRING STATELESS (no sessions!) ───
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ─── ADD OUR JWT FILTER BEFORE THE USERNAME/PASSWORD FILTER ───
            .addFilterBefore(jwtAuthFilter, 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Hash passwords securely
    }
}
```

---

## Chapter 6: Full JWT Authentication Implementation

### The 3 Classes You Need

```java
// ─── 1. JWT SERVICE (Generate + Validate Tokens) ───
// This handles ALL JWT operations

@Service
public class JwtService {
    
    // ⚠️ In production: store in environment variable, NOT in code!
    private static final String SECRET_KEY = "my-super-secret-key-at-least-256-bits-long!!";
    
    // ─── GENERATE TOKEN (called after successful login) ───
    public String generateToken(UserDetails userDetails) {
        // Create claims: who, what role, when issued, when expires
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())     // "alice"
            .setIssuedAt(new Date())                    // now
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))  // +15 min
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    // ─── EXTRACT USERNAME FROM TOKEN (no DB lookup!) ───
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    // ─── VALIDATE TOKEN ───
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

```java
// ─── 2. AUTH SERVICE (Login + Register Logic) ───

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Email already registered");
        }
        
        // Create new user (password is HASHED, not stored in plain text!)
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        //                              ↑ "password123" → "$2a$10$..."
        //                              BCrypt adds salt + hashes
        user.setRole(Role.USER);
        userRepository.save(user);
        
        // Generate JWT
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
    
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        // Check password (compare PLAIN with HASHED)
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        // Generate JWT
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
```

```java
// ─── 3. AUTH CONTROLLER (Login + Register Endpoints) ───

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

// Request/Response DTOs:
public record LoginRequest(String email, String password) {}
public record RegisterRequest(String email, String password, String name) {}
public record AuthResponse(String token) {}
```

### Complete Request Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│  1. REGISTER                                                        │
│     POST /api/auth/register                                         │
│     Body: {"email":"alice@test.com","password":"pass123"}           │
│                                                                     │
│     → User saved in DB (password hashed with BCrypt)                │
│     → JWT generated: {"sub":"alice","role":"USER","exp":...}        │
│     → Response: {"token": "eyJhbGci..."}                           │
│                                                                     │
│  2. LOGIN                                                           │
│     POST /api/auth/login                                            │
│     Body: {"email":"alice@test.com","password":"pass123"}           │
│                                                                     │
│     → Find user by email                                            │
│     → Verify password with BCrypt                                   │
│     → JWT generated (same as above)                                 │
│     → Response: {"token": "eyJhbGci..."}                           │
│                                                                     │
│  3. ACCESS PROTECTED ENDPOINT                                       │
│     GET /api/orders                                                 │
│     Header: Authorization: Bearer eyJhbGci...                       │
│                                                                     │
│     → JwtAuthFilter extracts token from header                      │
│     → Verifies signature (no tampering)                             │
│     → Checks expiry (not expired)                                   │
│     → Sets SecurityContext (user="alice", role="USER")              │
│     → FilterSecurityInterceptor: does /api/orders need auth? YES    │
│     → User is authenticated → pass                                  │
│     → Controller receives request, returns orders                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Chapter 7: OAuth2 with Spring Security

### The "Login with Google" Flow

```java
// ─── SPRING BOOT OAUTH2 CLIENT CONFIG ───
// Step 1: Add dependency
// build.gradle:
// implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'

// Step 2: Configure in application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: 123456789-xxxxx.apps.googleusercontent.com
            client-secret: GOCSPX-xxxxxxxxxxxx
            scope: profile, email
          
          github:
            client-id: Iv1.xxxxxxxxxxxx
            client-secret: xxxxxxxxxxxxxxxxxxxxxx
            scope: user:email

        provider:
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/v2/auth
            token-uri: https://oauth2.googleapis.com/token
            user-info-uri: https://www.googleapis.com/oauth2/v3/userinfo
```

```java
// ─── SPRING SECURITY CONFIG WITH OAUTH2 ───

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/oauth2/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")           // Custom login page (optional)
                .defaultSuccessUrl("/dashboard")  // Where to redirect after login
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
            );
        
        return http.build();
    }
}
```

### What Happens When User Clicks "Login with Google"

```
1. USER: Clicks "Login with Google" button
         → GET /oauth2/authorization/google

2. SPRING: Redirects to Google
         → https://accounts.google.com/o/oauth2/v2/auth?
              client_id=123...&
              redirect_uri=http://localhost:8080/login/oauth2/code/google&
              response_type=code&
              scope=profile+email

3. USER: Sees Google consent screen
         → "MyApp wants to: View your email address, View your profile"
         → User clicks "Allow"

4. GOOGLE: Redirects back to our app
         → GET /login/oauth2/code/google?code=4/0AX4XfW...

5. SPRING: Exchanges code for tokens
         → POST https://oauth2.googleapis.com/token
         → Body: code=4/0AX4XfW...&client_id=123...&client_secret=...
         → Response: {"access_token":"ya29...", "id_token":"eyJ..."}

6. SPRING: Fetches user info
         → GET https://www.googleapis.com/oauth2/v3/userinfo
         → Header: Authorization: Bearer ya29...
         → Response: {"sub":"12345","name":"Alice","email":"alice@gmail.com"}

7. SPRING: Creates user in our DB (if new), creates session
         → Redirects to /dashboard
         → User is logged in! 🎉
```

### OAuth2 Resource Server (Protecting APIs with JWT)

```java
// ─── CONFIGURING SPRING TO VALIDATE JWTs FROM A PROVIDER ───
// Your microservice doesn't issue JWTs. It just validates them
// using the AUTHORIZATION SERVER's public key.

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://accounts.google.com
          # Spring will fetch Google's public keys from:
          # https://www.googleapis.com/oauth2/v3/certs

// Now protect your API:
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @GetMapping
    public List<Order> getOrders() {
        // Spring automatically validates the JWT from the Authorization header
        // If invalid → 401
        // If valid → extracts user info from JWT claims
        return orderService.findAll();
    }
}
```

---

## Chapter 8: Common Vulnerabilities & How to Fix Them

### SQL Injection

```java
// ─── THE BUG ───
// ❌ NEVER DO THIS:
String query = "SELECT * FROM users WHERE email = '" + userInput + "'";
// If userInput = "alice@test.com' OR '1'='1"
// Query becomes: SELECT * FROM users WHERE email = 'alice@test.com' OR '1'='1'
// Returns ALL users! Data breach!

// ─── THE FIX ───
// ✅ Use parameterized queries:
@Query("SELECT * FROM users WHERE email = :email")
User findByEmail(@Param("email") String email);
// JPA/Spring automatically escapes dangerous characters
```

### XSS (Cross-Site Scripting)

```java
// ─── THE BUG ───
// User submits: <script>alert('hack!')</script>
// You display it: "Welcome <script>alert('hack!')</script>"
// → Script RUNS in browser of every user who visits!

// ─── THE FIX ───
// In Thymeleaf/JSP: Use th:text (auto-escapes HTML)
<p th:text="${userComment}">   <!-- ✅ Escapes HTML -->
<p th:utext="${userComment}">  <!-- ❌ Does NOT escape (dangerous) -->

// In React: React auto-escapes by default:
<div>{userComment}</div>  <!-- ✅ Safe -->
<div dangerouslySetInnerHTML={{__html: userComment}}/>  <!-- ❌ Dangerous -->

// Spring Boot: Add security headers:
spring.security.headers.content-security-policy: default-src 'self'
```

### CSRF (Cross-Site Request Forgery)

```java
// ─── THE BUG ───
// User is logged into yourbank.com
// They visit evil.com which has:
// <img src="https://yourbank.com/transfer?to=attacker&amount=1000">
// Browser sends the request WITH the user's cookies!
// Money transferred without user knowing!

// ─── THE FIX ───
// Spring Security CSRF protection (ENABLED by default for non-GET requests):
// In your form, include a CSRF token:
<form action="/transfer" method="POST">
    <input type="hidden" name="_csrf" value="abc123...">
    <input name="amount">
    <button>Transfer</button>
</form>

// For APIs (no browser forms): DISABLE CSRF (like we did in JWT config)
http.csrf(csrf -> csrf.disable());
// Why? Because JWT is NOT sent automatically by browser.
// CSRF only applies to COOKIE-based auth.
```

### Storing Passwords

```java
// ─── THE BUG ───
// Storing passwords in PLAIN TEXT:
user.setPassword(request.password());
// ← If DB is breached, ALL passwords are exposed!

// ─── THE FIX ───
// ALWAYS hash passwords:
user.setPassword(passwordEncoder.encode(request.password()));
// BCrypt adds a RANDOM SALT to each password
// Same password "password123" → different hash each time!
// Even if DB is breached, attacker can't reverse hashes

// ⚠️ NEVER use MD5 or SHA-256 for passwords (too fast, easy to crack)
// ✅ ALWAYS use BCrypt, SCrypt, or Argon2
```

---

## Chapter 9: All Interview Questions

### Beginner

**Q1: What's the difference between Authentication and Authorization?**
```
A: Authentication = WHO you are (passport check)
   Authorization = WHAT you can do (boarding pass check)
   
   401 Unauthorized = Not authenticated (who are you?)
   403 Forbidden   = Not authorized (you're Alice, but can't do this)
```

**Q2: What is JWT and what are its parts?**
```
A: JWT = JSON Web Token. A self-contained token with 3 parts:
   1. HEADER: algorithm + token type (e.g., {"alg":"HS256","typ":"JWT"})
   2. PAYLOAD: claims (sub, role, exp, iat)
   3. SIGNATURE: prevents tampering
   
   Format: header.payload.signature
   Example: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSJ9.dg4B5HgFfU0GjF
```

**Q3: How does JWT work?**
```
A: 1. User logs in with username + password
   2. Server validates credentials, creates JWT (signed with secret key)
   3. Server returns JWT to client
   4. Client stores JWT (localStorage or cookie)
   5. Client sends JWT in "Authorization: Bearer <token>" header on every request
   6. Server validates signature + checks expiry
   7. If valid → process request. If invalid → 401.
```

**Q4: Why can't you modify a JWT without knowing the secret?**
```
A: JWT has a SIGNATURE, which is:
   HMACSHA256(base64(header) + "." + base64(payload), SECRET_KEY)
   
   If you change even ONE character in the payload:
   → Signature becomes completely different
   → Server recalculates signature (using SECRET_KEY)
   → Signatures don't match → REJECTED
   
   Without the SECRET_KEY, you can't create a valid signature.
```

### Intermediate

**Q5: What's the difference between JWT and OAuth2?**
```
A: JWT is a TOKEN FORMAT (how the token looks)
   OAuth2 is a PROTOCOL (how to get the token)
   
   JWT:    {"sub":"alice","role":"ADMIN","exp":1700003600}
   OAuth2: The flow that gives you this token
   
   They're often used together:
   OAuth2 says "use Bearer token in Authorization header"
   JWT is the Bearer token
```

**Q6: Explain the Spring Security filter chain.**
```
A: A chain of filters that process EVERY HTTP request:
   
   Order (simplified):
   1. CsrfFilter — CSRF protection
   2. CorsFilter — CORS headers
   3. Your JWT Filter — extract + validate JWT
   4. SecurityContextHolderFilter — store authentication
   5. ExceptionTranslationFilter — convert errors to 401/403
   6. FilterSecurityInterceptor — check URL permissions
   7. Your Controller
   
   Each filter can: pass through, modify request, or block with error.
```

**Q7: Why disable CSRF when using JWT?**
```
A: CSRF attacks work by exploiting COOKIES (browser auto-sends them).
   JWT is sent via Authorization HEADER (browser does NOT auto-send).
   
   Since the browser doesn't auto-send JWT on requests from other sites,
   CSRF attacks CAN'T work with JWT auth.
   → CSRF protection is unnecessary overhead.
```

**Q8: How do you handle JWT expiration?**
```
A: Use TWO tokens:
   1. ACCESS TOKEN (short-lived, 15 minutes)
      - Sent on every API request
      - If expired → 401
   
   2. REFRESH TOKEN (long-lived, 7 days)
      - Sent ONLY to get new access tokens
      - Stored in database (can be revoked!)
   
   Flow:
   Access token expires → Client sends refresh token
   → Server checks refresh token in DB
   → If valid → issue new access token
   → If invalid (revoked) → require login again
```

### Senior

**Q9: How would you design a secure authentication system for a microservices architecture?**
```
A: 1. Each service does NOT authenticate users directly
   2. Dedicated AUTH SERVICE handles login/register
   3. Auth service issues JWT (signed with private key)
   4. Other services validate JWT using PUBLIC key (no need to call auth service!)
   
   Flow:
   API Gateway receives request with JWT
   → Validates JWT (checks signature, expiry)
   → Extracts user ID and roles from JWT claims
   → Forwards user info to downstream services via headers
   → Downstream services trust the gateway's headers
   
   Benefits:
   - Auth service is a single point for authentication
   - Other services don't need to store user credentials
   - JWT validation is stateless (no DB calls)
```

**Q10: How does Spring Security's SecurityContextHolder work?**
```
A: SecurityContextHolder stores the current user's authentication.
   
   It uses a ThreadLocal (each thread has its OWN copy):
   
   Request comes in → Filter sets authentication:
   SecurityContextHolder.getContext().setAuthentication(authToken);
   
   Controller reads it:
   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
   String user = auth.getName();
   boolean isAdmin = auth.getAuthorities().contains("ROLE_ADMIN");
   
   After request: filter clears it:
   SecurityContextHolder.clearContext();
   (Prevents leaking auth to next request — thread pool reuse!)
   
   MODES:
   1. MODE_THREADLOCAL (default): Each thread has its own
   2. MODE_INHERITABLETHREADLOCAL: Child threads inherit parent's auth
   3. MODE_GLOBAL: All threads share one (NOT recommended)
```

**Q11: How would you handle a scenario where a user's JWT is compromised?**
```
A: JWT can't be revoked (stateless). So use a BLOCKLIST:
   
   1. Add blacklisted JWT IDs to Redis (with TTL matching JWT expiry)
   2. On logout → add JWT ID to blacklist
   3. Every request → check if JWT ID is in blacklist
   4. If blacklisted → 401 Unauthorized
   
   Better approach: Use REFRESH TOKENS stored in DB
   → If compromised, DELETE the refresh token from DB
   → Attacker can use access token for remaining 15 minutes
   → But can't get new access tokens without refresh token
```

**Q12: What happens if you use the same JWT secret across all microservices?**
```
A: DANGER! If ONE service is compromised, the attacker can:
   - Forge tokens for ANY service
   - Impersonate ANY user
   - Access ANY endpoint
   
   Better: Each service should validate using PUBLIC key cryptography (RS256)
   - Auth service signs JWT with PRIVATE key (kept secret)
   - Other services verify with PUBLIC key (shared safely)
   - If one service is breached, they can only verify, not CREATE tokens
```

### Tricky

**Q13: A request comes in with a valid JWT but the user was deleted from the database. What happens?**
```
A: JWT says "alice" is authenticated.
   But "alice" was deleted from database 5 minutes ago.
   
   Problem: JWT doesn't know about DB changes (stateless!)
   
   Solutions:
   1. CHECK DB on every request (loses JWT benefit)
   2. Use SHORT-LIVED JWT (15 min) + validate on sensitive operations
   3. Maintain a "user validity version" in JWT claims
   4. Blocklist approach: add user's tokens to blocklist on account deletion
```

**Q14: How does BCrypt compare passwords if it uses a random salt?**
```
A: BCrypt stores the salt INSIDE the hash output!
   
   Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
          ││  ││                        ││
          ││  │└── Salt (22 chars) ─────┘│
          ││  │                          │
          ││  └── Cost factor (2^10 rounds)
          ││
          └── BCrypt version identifier

   When you call passwordEncoder.matches(plainPassword, hash):
   1. Extract salt from hash (N9qo8uLOickgx2ZMRZoMye)
   2. Hash plainPassword with that salt
   3. Compare NEW hash with STORED hash
   4. If same → password is correct!
```

**Q15: Your Spring Boot app uses sessions. You scale to 10 servers. Users get logged out randomly. Why?**
```
A: Session is stored on Server A's memory.
   User's next request goes to Server B (load balancer).
   Server B doesn't have the session → User is "not logged in"!
   
   Solutions:
   1. STICKY SESSIONS: Load balancer sends same user to same server
      (Problem: uneven load, server failure loses sessions)
   
   2. REDIS SESSION STORE: All servers share sessions in Redis
      spring.session.store-type=redis
      (Best approach!)
   
   3. JWT: No sessions needed! Token is self-contained.
      Any server can verify it. No shared storage needed.
      (This is why JWT became popular!)
```

### 30-Second Summary

```
SECURITY CHEAT SHEET:

AUTHENTICATION (Who?) vs AUTHORIZATION (What?)
  → 401 = Not authenticated. 403 = Not authorized.

JWT = Self-contained token with 3 parts: header.payload.signature
  → Stateless (no DB lookup). Signed (can't tamper).
  → Short expiry (15 min). Use refresh tokens for longer access.

OAUTH2 = Protocol for delegated access
  → "Login with Google" = OAuth2 Authorization Code flow
  → Resource Server validates JWT from Authorization Server

SPRING SECURITY FILTER CHAIN:
  Request → CsrfFilter → CorsFilter → JwtFilter → AuthFilter → Interceptor → Controller
  JwtFilter: Extract token → Validate signature → Set SecurityContext

COMMON VULNERABILITIES:
  SQL injection: Use parameterized queries (never concatenate strings)
  XSS: Escape HTML output (React auto-escapes, Thymeleaf th:text)
  CSRF: Disable for JWT (browser doesn't auto-send headers)
  Passwords: ALWAYS use BCrypt (never MD5/SHA)