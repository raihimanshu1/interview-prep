# REST API Versioning - Interview Response

## What Is It?

REST API versioning is the practice of evolving an API contract without silently breaking existing clients.

In simple terms:

```text
Old clients should continue working.
New clients can use new behavior.
Backend teams can deploy safely.
```

## Why It Matters

In banking systems, not every client upgrades at the same time.

Examples:

```text
Mobile app
Web frontend
Partner API client
Internal microservice
Batch job
```

If `/transfers` changes suddenly, old clients may fail during money movement.

## Common Versioning Strategies

### 1. URI Versioning

```http
GET /api/v1/transfers/123
GET /api/v2/transfers/123
```

Pros:

```text
Simple
Visible
Easy to route
Easy for interviews
```

Cons:

```text
Version is part of URL
Can lead to duplicated controllers
```

### 2. Header Versioning

```http
GET /api/transfers/123
X-API-Version: 2
```

Pros:

```text
Cleaner URL
Good for enterprise APIs
```

Cons:

```text
Less visible
Harder to test from browser
```

### 3. Media Type Versioning

```http
Accept: application/vnd.wells.transfer.v2+json
```

Pros:

```text
Very contract-focused
Useful for public APIs
```

Cons:

```text
More complex for clients
```

## Breaking vs Safe Changes

Breaking change:

```json
{
  "transferId": "T1",
  "amount": 100.00
}
```

changed to:

```json
{
  "id": "T1",
  "value": "100.00"
}
```

Problem:

```text
transferId renamed
amount type changed
old clients break
```

Backward-compatible change:

```json
{
  "transferId": "T1",
  "amount": 100.00,
  "currency": "USD",
  "statusReason": "PENDING_FRAUD_CHECK"
}
```

Old clients ignore unknown fields.

## Spring Controller Example

```java
@RestController
@RequestMapping("/api/v1/transfers")
class TransferV1Controller {
    @GetMapping("/{id}")
    TransferV1Response getTransfer(@PathVariable String id) {
        return service.getV1(id);
    }
}

@RestController
@RequestMapping("/api/v2/transfers")
class TransferV2Controller {
    @GetMapping("/{id}")
    TransferV2Response getTransfer(@PathVariable String id) {
        return service.getV2(id);
    }
}
```

Important:

```text
Do not expose JPA entities directly.
Use DTOs per API version.
Keep mapping logic explicit.
```

## Forward Compatibility

Forward compatibility means:

```text
Old clients can tolerate some future changes.
```

Example:

```text
Client ignores unknown JSON fields.
Client does not fail when a new enum value appears.
Client handles missing optional fields safely.
```

## Backward Compatibility

Backward compatibility means:

```text
New backend still supports old clients.
```

Rules:

```text
Add fields, do not rename existing fields.
Make new inputs optional first.
Keep old enum values.
Do not change field meaning silently.
```

## Semantic Versioning

```text
MAJOR -> breaking API contract change
MINOR -> backward-compatible field/endpoint addition
PATCH -> bug fix or internal behavior fix
```

Example:

```text
v1 -> v2 because transfer status semantics changed
v1.1 because optional currency field was added
v1.1.1 because validation message was fixed
```

## Big-Company API Evolution Mindset

Amazon/Google-style API evolution usually means:

```text
Prefer additive changes
Keep old contracts running
Use contract tests
Track old-client usage
Announce deprecation
Give migration windows
Roll out gradually
Keep rollback possible
```

## Related Patterns

- Adapter pattern for mapping v1 DTOs to internal models
- Facade pattern for stable external APIs
- Consumer-driven contract testing
- Strangler pattern for gradual migration

## Follow-Up Interview Questions

### When do you create a new API version?

```text
When the contract meaning breaks: renamed fields, removed fields, changed types,
changed status semantics, or new required inputs.
```

### Should every change create v2?

```text
No. Additive optional changes usually stay in the same version.
```

### How do you retire old versions?

```text
Monitor usage, announce deprecation, migrate clients, then remove after the agreed window.
```

## Interview Answer

In an interview, I would say: I version REST APIs only when the contract changes in a way that can break existing clients. For small additive changes, I keep the same version and add optional fields. For breaking changes, I usually prefer URI versioning like `/api/v1/transfers` and `/api/v2/transfers` because it is simple and visible, though headers or media types also work. In production, I would use DTOs per version, contract tests, deprecation timelines, old-client telemetry, and gradual rollout so mobile apps, partners, and internal services are not forced to upgrade at the same time.
