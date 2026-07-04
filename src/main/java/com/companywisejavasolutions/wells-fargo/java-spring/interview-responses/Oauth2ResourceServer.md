# Oauth2 Resource Server - Interview Response

## What Is It?

An OAuth2 resource server validates access tokens, extracts scopes/authorities, and authorizes protected API requests without handling user passwords.

## In Simple Terms

An OAuth2 resource server validates access tokens, extracts scopes/authorities, and authorizes protected API requests without handling user passwords.

## Why It Matters

A payments API validates a bearer token and checks scope transfer:write before posting money.

If we get it wrong:

```text
Do not skip issuer/audience validation.
Do not map every authenticated user to admin-level access.
Do not expose token contents in logs.
```

## Example

```text
Spring Security sketch:
http.oauth2ResourceServer(oauth2 -> oauth2.jwt());
@PreAuthorize("hasAuthority('SCOPE_transfer:write')")
public TransferResponse createTransfer(...) { ... }
```

Key interview details:

- JWT validation, issuer/JWK, scope authorities, 401 vs 403.

## Safe vs Unsafe

Safe:

```text
Validate issuer, audience, expiry, signature, and scopes.
Use JWKS rotation for JWTs or introspection for opaque tokens.
Apply method/path-level authorization.
Return client-safe 401/403 responses.
```

Unsafe:

```text
Do not skip issuer/audience validation.
Do not map every authenticated user to admin-level access.
Do not expose token contents in logs.
```

## Java / Spring Backend Use Case

A payments API validates a bearer token and checks scope transfer:write before posting money.

Java/Spring angle:

```text
Spring Security sketch:
http.oauth2ResourceServer(oauth2 -> oauth2.jwt());
@PreAuthorize("hasAuthority('SCOPE_transfer:write')")
public TransferResponse createTransfer(...) { ... }
```

## Production Concerns

- Keep external contracts stable during deployment.
- Use DTOs instead of exposing entities or internal models.
- Add contract tests for public APIs and events.
- Define rollback, deprecation, and client migration plans.
- Monitor old-client usage before removing old versions.

## Common Mistakes

- Do not skip issuer/audience validation.
- Do not map every authenticated user to admin-level access.
- Do not expose token contents in logs.

## Extra Details

Forward compatibility:

```text
New producers or services can add optional fields, headers, event attributes, or response data without forcing every old consumer to redeploy immediately.
Consumers should ignore unknown fields and tolerate defaults where the contract allows it.
```

Backward compatibility:

```text
Old clients must still work after Oauth2 Resource Server changes are deployed.
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

In an interview, I would say: An OAuth2 resource server validates access tokens, extracts scopes/authorities, and authorizes protected API requests without handling user passwords. For example, a payments API validates a bearer token and checks scope transfer:write before posting money. The main production risk is skip issuer/audience validation.
