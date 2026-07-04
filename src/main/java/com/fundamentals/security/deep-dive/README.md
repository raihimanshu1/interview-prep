# Security Deep-Dive — OAuth2, JWT, mTLS, SQL Injection, XSS, CSRF, Webhooks

## 1. Why This Concept Matters

Security is the most critical non-functional requirement for any production system. Breaches cost millions, destroy user trust, and can take down companies. Understanding common vulnerabilities — SQL injection, XSS, CSRF, SSRF, replay attacks, insecure deserialization — and how to mitigate them is essential for any backend engineer. Interviewers test security extensively for senior roles because the most expensive bugs are security bugs. OAuth2, JWT, mTLS, and webhook signature validation are the core mechanisms for authentication, authorization, and secure inter-service communication in modern microservice architectures.

Misunderstanding security causes:
- SQL injection allowing attackers to read/delete entire databases
- XSS allowing attackers to steal user sessions and execute arbitrary JavaScript
- CSRF allowing attackers to perform actions as authenticated users
- JWT secrets leaked in code → anyone can forge tokens
- Webhook signatures not validated → fake events processed as real
- PII leaked in logs → compliance violations (GDPR, PCI-DSS)

## 2. OAuth2 Full Flow

OAuth2 is an authorization framework that allows third-party applications to obtain limited access to a user's resources without exposing the user's credentials. It's used by Google, GitHub, Facebook, and every major identity provider.

**Core Roles:**
- **Resource Owner**: the user who owns the data
- **Client**: the application requesting access (mobile app, web app, server)
- **Authorization Server**: issues tokens after authenticating the user
- **Resource Server**: serves protected data, validates access tokens

**Grant Types (Flows):**

| Grant Type | Use Case | Security |
|-----------|----------|----------|
| Authorization Code | Web apps with server-side backend | Most secure |
| PKCE | Mobile/SPA apps (no client_secret) | Secure (replaces Implicit) |
| Client Credentials | Machine-to-machine (no user) | For server-to-server |
| Refresh Token | Get new access tokens without user login | Required for long-lived sessions |

**Authorization Code Flow (most common):**
```
1. User clicks "Login with Google"
2. Browser redirected to:
   https://accounts.google.com/o/oauth2/auth?
     response_type=code&
     client_id=123.apps.googleusercontent.com&
     redirect_uri=https://myapp.com/callback&
     scope=email+profile&
     state=random-csrf-token
3. User logs in, grants permission
4. Google redirects back to:
   https://myapp.com/callback?code=AUTH_CODE&state=random-csrf-token
5. Server exchanges code for tokens:
   POST https://oauth2.googleapis.com/token
   Body: code=AUTH_CODE&client_id=123&client_secret=SECRET&
         redirect_uri=https://myapp.com/callback&
         grant_type=authorization_code
6. Google returns:
   { access_token, refresh_token, id_token (JWT), expires_in }
7. Server decodes id_token JWT, creates session
8. Subsequent API calls use access_token
```

**PKCE (Proof Key for Code Exchange) — for SPAs:**
```
1. Client generates: code_verifier (random string)
2. Client computes: code_challenge = SHA256(code_verifier)
3. Authorization request includes: code_challenge=SHA256(verifier)
4. Token exchange includes: code_verifier=original_string
5. Authorization server verifies: SHA256(verifier) == challenge
This prevents the authorization code from being exchanged by an attacker
even if intercepted (no client_secret needed)
```

**Client Credentials (M2M):**
```
POST https://auth.example.com/token
Body: grant_type=client_credentials&
      client_id=service-123&
      client_secret=SECRET&
      scope=api:read api:write
Response: { access_token: "eyJ...", expires_in: 3600 }
```
Used for: service-account access, background jobs, API integrations.

## 3. JWT (JSON Web Token)

JWT is a compact, URL-safe token format for stateless authentication. The token is self-contained — it carries all user information in the payload, signed by the server.

**Structure:** `header.payload.signature`
```
Header:  {"alg":"HS256","typ":"JWT"} → base64url
Payload: {"sub":"user-123","role":"admin","iat":1700000000,"exp":1700003600} → base64url
Signature: HMACSHA256(base64url(header) + "." + base64url(payload), secret)
```

**Signing Algorithms:**
| Algorithm | Type | Key | Use Case |
|-----------|------|-----|----------|
| HS256 | Symmetric | Shared secret | Single service, same secret signs + verifies |
| RS256 | Asymmetric | Private key signs, public key verifies | Microservices — auth service signs, others verify with public key |
| ES256 | Asymmetric | ECDSA keys | Like RS256 but smaller keys, faster |

**Security considerations:**
- Always validate signature — never trust a JWT without verification
- Always validate expiration (`exp` claim) — reject expired tokens
- Validate issuer (`iss`) — only accept tokens from your auth server
- Validate audience (`aud`) — only accept tokens intended for your service
- Never store secrets in code — use environment variables
- Short expiry (15-60 min) + refresh tokens for long-lived sessions
- Algorithm confusion attack: set `{"alg":"none"}` — always verify algorithm matches expected

**JWT vs OAuth2:**
- JWT is a TOKEN FORMAT (how the token looks). OAuth2 is a PROTOCOL (how tokens are issued and used).
- OAuth2 can use JWT as the access token format (common).
- JWT can be used without OAuth2 (simple self-issued tokens).

## 4. mTLS (Mutual TLS)

mTLS is mutual authentication where BOTH the client AND server present TLS certificates. Used for service-to-service authentication in zero-trust architectures.

**Flow:**
```
1. Client connects to server over TLS
2. Server presents its certificate (server authentication — normal TLS)
3. Server requests client certificate
4. Client presents its certificate (client authentication — mTLS)
5. Server validates client certificate against trusted CA
6. Both sides have verified each other's identity
7. Encrypted communication begins
```

**Production usage:**
```yaml
# Client configuration (e.g., Spring Boot)
server:
  ssl:
    client-auth: need  # Require client certificate
    key-store: classpath:server-keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    trust-store: classpath:truststore.p12  # Contains client CA
    trust-store-password: ${TRUSTSTORE_PASSWORD}

# When calling other services with mTLS:
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate(clientHttpRequestFactory());
}

private ClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    CloseableHttpClient httpClient = HttpClientBuilder.create()
        .setSSLContext(sslContext())  // Load keystore with client cert
        .build();
    factory.setHttpClient(httpClient);
    return factory;
}
```

**mTLS vs JWT for service-to-service:**
- mTLS: authenticates at the TRANSPORT layer (TLS). No token logic needed. Works for any protocol (HTTP, gRPC, database connections).
- JWT: authenticates at the APPLICATION layer (HTTP header). Needs token generation, validation, expiry management. More flexible for fine-grained authorization.
- Best practice: Use BOTH. mTLS for transport security, JWT for application-level auth (roles, scopes).

## 5. SQL Injection

SQL injection occurs when user input is directly concatenated into SQL queries without sanitization. It's the most critical web vulnerability — attackers can read, modify, or delete entire databases.

**Vulnerable code:**
```java
// BAD — string concatenation
String sql = "SELECT * FROM users WHERE email = '" + userInput + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(sql);
// If userInput = "a@b.com' OR '1'='1"
// SQL: SELECT * FROM users WHERE email = 'a@b.com' OR '1'='1'
// Returns ALL users!

// Even worse — DELETE attack
// userInput = "'; DROP TABLE users; --"
// SQL: SELECT * FROM users WHERE email = ''; DROP TABLE users; --'
```

**Safe code — PreparedStatement:**
```java
// GOOD — parameterized query
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement ps = connection.prepareStatement(sql);
ps.setString(1, userInput);
ResultSet rs = ps.executeQuery();
// Parameter value is DATA, never SQL syntax
// Even "a@b.com' OR '1'='1" is treated as a literal string value
```

**Additional protections:**
1. Input validation (reject unexpected characters)
2. Least privilege database user (read-only where possible)
3. ORM (JPA/Hibernate) — most ORMs use parameterized queries automatically
4. SQL injection scanners (part of CI/CD pipeline)

## 6. XSS (Cross-Site Scripting)

XSS allows attackers to inject malicious JavaScript into web pages viewed by other users. Types: Stored (persistent in DB), Reflected (in URL), DOM-based (client-side).

**Vulnerable code:**
```java
// BAD — outputting user input directly into HTML
@GetMapping("/profile")
public String getUserProfile(String username, Model model) {
    model.addAttribute("username", username);  // No sanitization!
    return "profile";
}
// If username = "<script>alert('XSS')</script>"
// Browser executes the script!

// GOOD — sanitize or escape
import org.springframework.web.util.HtmlUtils;

@GetMapping("/profile")
public String getUserProfile(String username, Model model) {
    model.addAttribute("username", HtmlUtils.htmlEscape(username));
    return "profile";
}
// <script> → <script> (rendered as text, not executed)
```

**Prevention:**
1. Contextual output encoding (HTML entity, JavaScript, CSS, URL encoding)
2. Content Security Policy (CSP) headers: `Content-Security-Policy: script-src 'self'`
3. HttpOnly + Secure cookies (prevents JavaScript from reading cookies)
4. Input validation (reject HTML tags in username fields)
5. Use templating engines with auto-escaping (Thymeleaf, React's JSX)

## 7. CSRF (Cross-Site Request Forgery)

CSRF tricks a logged-in user into performing actions on another site without their knowledge. Covered in detail in the Spring Security deep-dive. Key points:

1. CSRF = session cookie is automatically sent by browser → attacker's request looks legitimate
2. Prevention: CSRF token (random value tied to session) required for state-changing requests
3. Stateless JWT APIs: CSRF is unnecessary (no session cookie to abuse)
4. Session-based auth: CSRF protection is MANDATORY

## 8. Webhook Signature Validation

Webhooks are HTTP callbacks sent by services when events occur (payment completed, order shipped, user created). Without signature validation, anyone can send fake webhooks to your endpoint.

**How signatures work:**
```
Provider sends webhook POST to your endpoint:
  Headers: X-Signature-256: sha256=computed_hash
  Body: {"event":"payment.completed","order_id":123}

Signature = HMACSHA256(secret, body)

Server validates:
  computed = HMACSHA256(secret, requestBody)
  if computed == header_signature: process the webhook
  else: reject (403 Forbidden)
```

**Implementation:**
```java
@PostMapping("/webhooks/stripe")
public ResponseEntity<Void> handleStripeWebhook(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String signature) {
    
    String secret = System.getenv("STRIPE_WEBHOOK_SECRET");
    
    try {
        StripeWebhook.constructEvent(payload, signature, secret);
        // Signature is valid — process event
        processPayment(payload);
        return ResponseEntity.ok().build();
    } catch (SignatureVerificationException e) {
        // Invalid signature — reject
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
```

**Critical rules:**
1. Validate in EVERY webhook endpoint — never skip
2. Use constant-time comparison (not `==`) to prevent timing attacks
3. Rotate secrets regularly
4. Webhook endpoint should be as simple as possible (validate + queue to internal processor)
5. Replay protection: include timestamp in payload, reject if >5 minutes old

## 9. Additional Security Topics

**SSRF (Server-Side Request Forgery):**
Attacker makes the server make requests to internal resources.
```java
// BAD — user controls the URL
String url = request.getParameter("url");  // file:///etc/passwd or http://169.254.169.254/
RestTemplate rest = new RestTemplate();
String result = rest.getForObject(url, String.class);  // Reads internal resources!

// GOOD — validate URL scheme, domain, IP range
UriComponentsBuilder.fromUriString(url).build().toUri();
if (!ALLOWED_HOSTS.contains(uri.getHost())) throw new SecurityException("Blocked");
```

**Replay attacks:**
Attacker intercepts a valid request and re-sends it.
Prevention: Add nonce (unique request ID), timestamp, or sequence number. Server checks if nonce was already processed.

**PII masking in logs:**
```java
// BAD — logs user email and credit card
log.info("User {} paid with card {}", user.getEmail(), creditCardNumber);

// GOOD — mask sensitive data
log.info("User {} paid with card {}", maskEmail(user.getEmail()), maskCard(creditCardNumber));

private String maskEmail(String email) {
    return email.replaceAll("(.)(.*)(@.*)", "$1***$3");
}
// a***@example.com
```

**Encryption at rest and in transit:**
- In transit: TLS 1.2+ (HTTPS). mTLS for service-to-service.
- At rest: AES-256 for data at rest (database encryption, disk encryption). Key management with KMS (AWS KMS, HashiCorp Vault).
- Never store passwords, API keys, or secrets in plain text — BCrypt/Argon2 for passwords, environment variables/secrets manager for keys.

## 10. Final 30-Second Answer

**OAuth2**: Authorization Code (web apps), PKCE (SPAs), Client Credentials (M2M). Always validate `state` parameter. **JWT**: format = `header.payload.signature`. HS256 = shared secret, RS256 = private/public key pair. Validate: signature, expiration, issuer, audience. **mTLS**: both sides present certificates — use for service-to-service auth. **SQL Injection**: always PreparedStatement — never concatenate user input into SQL. **XSS**: escape output, CSP headers, HttpOnly cookies. **CSRF**: CSRF tokens for session auth, disable for stateless JWT. **Webhooks**: validate HMAC signature, use constant-time comparison, reject if >5 min old. **Never**: store secrets in code, skip signature validation, log PII, trust user-controlled URLs.