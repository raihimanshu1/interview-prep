# Security And Auth Deep Dive Guidance

Use this when generating content for JWT, OAuth, Spring Security, authentication, authorization, filters, interceptors, API security, role-based access, secrets, or downstream microservice validation.

## JWT Must-Cover Checklist

For JWT, cover:

- what JWT is,
- why stateless authentication uses it,
- token structure: header, payload, signature,
- common claims: `sub`, `iss`, `aud`, `exp`, `iat`, `jti`, roles/scopes,
- what is signed vs encrypted,
- why payload is readable and should not contain secrets,
- login flow,
- request flow with `Authorization: Bearer <token>`,
- validation flow,
- expiry handling,
- refresh token strategy,
- access token vs refresh token,
- role-based access,
- Spring Security filter chain,
- custom JWT filter,
- downstream microservice token validation,
- secret/key management,
- key rotation,
- symmetric vs asymmetric signing,
- common attacks and mitigations,
- production debugging and logging.

## JWT Minimal Flow

```text
1. User sends username/password to auth service.
2. Auth service validates credentials.
3. Auth service creates signed JWT.
4. Client stores token safely.
5. Client sends token in Authorization header.
6. API gateway/service validates signature and expiry.
7. Service extracts subject and roles/scopes.
8. Service allows or rejects the request.
```

## JWT Structure

```text
header.payload.signature
```

Header example:

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

Payload example:

```json
{
  "sub": "user-123",
  "roles": ["ROLE_USER"],
  "iss": "auth-service",
  "exp": 1893456000
}
```

HTTP request example:

```http
GET /api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## Spring Boot Code Must Include When Asked For JWT

Include:

- dependency note,
- JWT utility/service,
- authentication controller,
- JWT filter extending `OncePerRequestFilter`,
- `SecurityFilterChain` configuration,
- secured controller endpoint,
- sample request with token.

Keep code minimal but realistic.

## Security Warnings

Always mention:

- JWT payload is Base64URL encoded, not encrypted.
- Never put password, OTP, card data, or secrets in JWT payload.
- Validate signature, expiry, issuer, audience when applicable.
- Use short-lived access tokens.
- Store refresh tokens securely.
- Rotate keys.
- Do not log full tokens.
- Use HTTPS.
- Be careful with token revocation because JWT is stateless by default.

