# API Keys & Password Hashing — Complete Deep Dive

## 1. Why This Concept Matters

API keys are the simplest form of authentication for programmatic access — they identify the calling application or user. Unlike OAuth2 tokens, API keys are long-lived and static, making them both convenient and dangerous. Password hashing is the foundation of user authentication — storing passwords in plaintext is the #1 security violation. Understanding bcrypt, scrypt, Argon2, salting, and hash verification is essential for any application that authenticates users. Interviewers ask about these because API key leaks and password breaches are the most common security incidents.

## 2. API Keys

**What they are:** A unique, secret string issued to a client (application or user) that identifies them when making API calls. Usually sent as a header (`X-API-Key`) or query parameter.

**API Key vs OAuth2/JWT:**
| Aspect | API Key | OAuth2 Token (JWT) |
|--------|---------|-------------------|
| Lifetime | Long-lived (months/years) | Short-lived (15-60 min) |
| Scope | Fixed (permissions set at creation) | Can be scoped per request |
| Rotation | Manual / scheduled | Automatic (refresh tokens) |
| Revocation | Immediate (delete key) | Immediate (blocklist or short expiry) |
| Best for | M2M integrations, public APIs | User-facing apps, fine-grained auth |

**Production API Key Management:**
```java
// 1. KEY GENERATION — cryptographically random
public class ApiKeyGenerator {
    public static String generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 32 bytes = 256 bits
        random.nextBytes(bytes);
        // Encode as base64 or hex
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        // Example output: "x8Kj3mZpQ9L2rN5vB7wE1tY6uI4oA0cF"
    }
}

// 2. STORE HASHED, NOT PLAINTEXT
// NEVER store the raw API key in database!
// Store: SHA-256 hash of the key
// When client sends key: hash it, compare with stored hash
public class ApiKeyService {
    private final ApiKeyRepository repository;
    
    public String createApiKey(String clientId, Set<String> permissions) {
        String rawKey = ApiKeyGenerator.generateApiKey();
        String hashedKey = hashApiKey(rawKey); // Store only the hash!
        
        repository.save(new ApiKeyEntity(clientId, hashedKey, permissions));
        return rawKey; // Return to client ONCE — they must save it!
    }
    
    public boolean validateApiKey(String rawKey) {
        String hashedKey = hashApiKey(rawKey);
        return repository.findByHashedKey(hashedKey).isPresent();
    }
    
    private String hashApiKey(String key) {
        return Hashing.sha256().hashString(key, StandardCharsets.UTF_8).toString();
    }
}

// 3. SECURITY HEADERS — never log API keys
@Configuration
public class ApiKeySecurityConfig {
    
    @Bean
    public FilterRegistrationBean<ApiKeyAuthenticationFilter> apiKeyFilter() {
        FilterRegistrationBean<ApiKeyAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ApiKeyAuthenticationFilter());
        registration.addUrlPatterns("/api/v1/*");
        registration.setOrder(1);
        return registration;
    }
}

// 4. RATE LIMITING BY API KEY
public class RateLimitingInterceptor implements HandlerInterceptor {
    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
            Object handler) throws Exception {
        String apiKey = request.getHeader("X-API-Key");
        RateLimiter limiter = rateLimiters.computeIfAbsent(apiKey, 
            k -> RateLimiter.create(100.0)); // 100 requests per second
        if (!limiter.tryAcquire()) {
            response.setStatus(429); // Too Many Requests
            return false;
        }
        return true;
    }
}
```

**Security best practices for API keys:**
1. **Generate with SecureRandom** — never use `Random` or UUID (predictable)
2. **Hash before storing** — never store raw keys in database
3. **Show key only once** — display on creation, never again
4. **Prefix for identification** — `sk_live_...` for Stripe, `ghp_...` for GitHub
5. **Scoped permissions** — read-only vs read-write, specific endpoints
6. **Rotation policy** — expire keys after 90 days, let clients regenerate
7. **Audit logging** — log which key accessed what, when
8. **Never log keys** — strip from log statements, filter in logback
9. **Rate limit per key** — prevent abuse of individual keys

## 3. Password Hashing

**The #1 rule: NEVER store passwords in plaintext. NEVER use MD5 or SHA for passwords.**

**Why hashing is not enough for passwords:**
- MD5/SHA are FAST — attackers can try billions of hashes per second
- Rainbow tables — precomputed hash-to-password mappings
- Solution: use SLOW, SALTED hashing algorithms

**Modern password hashing algorithms:**
| Algorithm | Year | Features | Recommended? |
|-----------|------|----------|-------------|
| MD5 | 1992 | 128-bit, extremely fast | ❌ — broken, instant cracking |
| SHA-1 | 1995 | 160-bit, fast | ❌ — can crack 10B+/sec with GPU |
| SHA-256 | 2001 | 256-bit, fast | ❌ — too fast for passwords |
| bcrypt | 1999 | Adaptive, salt built-in, 192-bit | ✅ — current standard |
| scrypt | 2009 | Memory-hard (requires RAM), salt | ✅ — better GPU resistance |
| Argon2id | 2015 | Memory-hard + side-channel resistant | ✅ — OWASP recommended (best) |

**Why password hashing must be slow:**
```
GPU can attempt: 10 BILLION MD5 hashes/second
                1 BILLION SHA-256 hashes/second
                100 THOUSAND bcrypt hashes/second (cost=12)
                1 THOUSAND Argon2 hashes/second (memory=64MB)

That 10B vs 1K difference = bcrypt is 10 MILLION times harder to brute-force!
```

**bcrypt in Spring Boot:**
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12); // cost=12
    
    public User register(String email, String rawPassword) {
        // Hash password BEFORE storing
        String hashedPassword = passwordEncoder.encode(rawPassword);
        // Store hashedPassword in DB (NOT rawPassword!)
        User user = new User(email, hashedPassword);
        return userRepository.save(user);
    }
    
    public boolean authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthenticationException("User not found"));
        
        // passwordEncoder.matches() does:
        // 1. Extract salt from stored hash
        // 2. Hash rawPassword + salt with same cost
        // 3. Compare with stored hash
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}

// BCrypt hash format:
// $2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqLziyDm9xKrzmHd4wVsXqy
// ├┘├┘├──────────────────────────────────────────────────────┘
// │ │ └──────────────────────────── Hash (53 chars base64)
// │ └──────────────────────────────── Cost (log2 rounds): 12 = 2^12 iterations
// └────────────────────────────────── Algorithm: 2a = bcrypt
```

**Argon2 (OWASP recommended):**
```java
// Using Spring Security's Argon2PasswordEncoder
@Bean
public PasswordEncoder passwordEncoder() {
    // saltLength, hashLength, parallelism, memory, iterations
    return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    // Memory: 64MB, Iterations: 3, Parallelism: 1
}

// Manual Argon2 with Bouncy Castle:
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

public String hashPassword(char[] password) {
    byte[] salt = generateSalt(16);
    byte[] hash = new byte[32];
    
    Argon2BytesGenerator generator = new Argon2BytesGenerator();
    Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
        .withSalt(salt)
        .withParallelism(1)
        .withMemoryAsKB(65536)  // 64MB
        .withIterations(3)
        .build();
    
    generator.init(params);
    generator.generateBytes(password, hash);
    return encodeHash(salt, hash); // Store: salt + hash
}
```

**Password validation rules:**
```java
// Never implement your own password policy — use a library
// Or follow NIST guidelines (2023):
// - Min 8 characters (12+ recommended)
// - No complexity rules (uppercase, special char, etc.)
// - Check against known breached passwords
// - Allow up to 64+ characters

@Component
public class PasswordValidator {
    private final Set<String> breachedPasswords = loadBreachedPasswords();
    
    public void validate(String password) {
        if (password.length() < 8) {
            throw new WeakPasswordException("Password must be at least 8 characters");
        }
        if (password.length() > 128) {
            throw new WeakPasswordException("Password too long");
        }
        if (breachedPasswords.contains(password)) {
            throw new WeakPasswordException("Password found in data breach — choose another");
        }
    }
}
```

## 4. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Storing API keys in plaintext | Data breach leaks ALL keys | Hash with SHA-256 before storing |
| Showing API key in response after creation | Intercepted in logs/network | Show ONCE on creation, never again |
| Using UUID as API key | Predictable (time-based component) | Use SecureRandom (32+ bytes) |
| No key rotation policy | Old keys never expire, risk accumulates | Mandate rotation every 90 days |
| MD5/SHA for passwords | Billions of hashes/sec — instant cracking | Use bcrypt (cost 12+) or Argon2id |
| Same salt for all passwords | Rainbow tables still work | Unique random salt per password (bcrypt does this automatically) |
| Custom password hashing algorithm | Almost certainly wrong | Use standard library (BCryptPasswordEncoder) |
| Not checking against breached passwords | Users reuse common passwords | Integrate HaveIBeenPwned API or local list |

## 5. Final 30-Second Answer

**API keys**: generate with `SecureRandom` (32+ bytes), hash before storing (`SHA-256`), show only once, prefix for identification, rate limit per key, rotate every 90 days, never log. **Password hashing**: NEVER plaintext, NEVER MD5/SHA. Use **bcrypt** (cost 12+) with built-in salt — `BCryptPasswordEncoder(12)` in Spring Boot. **Argon2id** is OWASP recommended (memory-hard, GPU-resistant). Always validate against breached password lists. `passwordEncoder.matches(raw, hash)` — never compare hashes manually.