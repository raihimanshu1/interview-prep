# JWT Opaque Session - Interview Response

## What Is It?

JWTs are self-contained signed tokens, opaque tokens require server-side introspection, and session cookies store identity through server-managed session state.

## In Simple Terms

JWTs are self-contained signed tokens, opaque tokens require server-side introspection, and session cookies store identity through server-managed session state.

## Why It Matters

A resource server may validate JWT locally for speed, while opaque tokens allow immediate revocation through introspection.

If we get it wrong:

```text
Do not treat JWT payloads as encrypted.
Do not skip audience and issuer validation.
Do not ignore token revocation requirements.
```

## Example

```text
JWT resource server checklist:
validate signature, issuer, audience, exp/nbf, scopes, and key rotation.
Map claims to authorities with least privilege.
```

Key interview details:

- JWT filters, revocation, introspection, cookie CSRF, banking security tradeoffs.

## Safe vs Unsafe

Safe:

```text
Use short lifetimes and validate issuer, audience, expiry, and signature.
Do not put secrets or sensitive PII in JWT claims.
Opaque tokens centralize revocation but add network calls.
Cookies need SameSite, Secure, HttpOnly, and CSRF protection where applicable.
```

Unsafe:

```text
Do not treat JWT payloads as encrypted.
Do not skip audience and issuer validation.
Do not ignore token revocation requirements.
```

## Java / Spring Backend Use Case

A resource server may validate JWT locally for speed, while opaque tokens allow immediate revocation through introspection.

Java/Spring angle:

```text
JWT resource server checklist:
validate signature, issuer, audience, exp/nbf, scopes, and key rotation.
Map claims to authorities with least privilege.
```

## Production Concerns

- Keep external contracts stable during deployment.
- Use DTOs instead of exposing entities or internal models.
- Add contract tests for public APIs and events.
- Define rollback, deprecation, and client migration plans.
- Monitor old-client usage before removing old versions.

## Common Mistakes

- Do not treat JWT payloads as encrypted.
- Do not skip audience and issuer validation.
- Do not ignore token revocation requirements.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after JWT Opaque Session changes are deployed.
Avoid removing fields, renaming fields, changing meanings, or making optional inputs required without a versioned rollout.
```

Semantic versioning:

```text
MAJOR -> breaking API/event/library contract change
MINOR -> backward-compatible capability or optional field
PATCH -> bug fix, tuning, or internal implementation improvement
```

Big-company API evolution mindset:

```text
Amazon/Google-style evolution usually favors additive contracts, consumer-driven tests, telemetry on old client usage, deprecation windows, gradual rollout, and rollback paths.
```

Related patterns:

- Adapter
- Facade
- Consumer-driven contracts
- Strangler migration

## Follow-Up Interview Questions

- How does this behave under concurrent requests?
- What happens when a downstream service or database operation fails?
- How would you test this and prove it works in production?

## Interview Answer

In an interview, I would say: JWTs are self-contained signed tokens, opaque tokens require server-side introspection, and session cookies store identity through server-managed session state. For example, a resource server may validate JWT locally for speed, while opaque tokens allow immediate revocation through introspection. The main production risk is treat JWT payloads as encrypted.
