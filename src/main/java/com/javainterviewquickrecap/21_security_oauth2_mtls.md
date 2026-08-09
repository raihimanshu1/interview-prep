# Module — Security Advanced: OAuth2 Grant Types, mTLS, Secrets Management — Q&A

> **Skill**: 7+ years — OAuth2 grant types, mTLS, secrets rotation, token exchange patterns.

---

## Q1. OAuth2 Grant Types & When to Use Each

| Grant Type | Use Case | Client Type | Refresh Token |
|-----------|----------|-------------|--------------|
| **Authorization Code** | Web apps with backend | Confidential | ✅ Yes |
| **Authorization Code + PKCE** | SPA / Mobile apps | Public | ✅ Yes |
| **Client Credentials** | Service-to-service | Confidential | ❌ No |
| **Resource Owner Password** | Legacy / first-party (NOT recommended) | Both | ✅ Yes |
| **Device Code** | Input-constrained devices (TV, CLI) | Public | ✅ Yes |

## Q2. mTLS (Mutual TLS) for Service-to-Service Auth

```
Standard TLS: Only client verifies server
  Client ──── TLS handshake ──── Server
    Client asks: "Is Server who it says?" ✅
    Server verifies client: ❌ (anyone can connect)

mTLS: BOTH sides verify each other
  Client ──── mTLS handshake ──── Server
    Client: "Is Server who it says?" ✅
    Server: "Is Client who it says?" ✅ (mutual verification!)
```

## Q3. Secrets Management

| Approach | Security Level | Complexity | Auto-rotation |
|----------|---------------|------------|--------------|
| Environment variables | Low | Lowest | ❌ Manual |
| Vault (HashiCorp) | High | Medium | ✅ Yes |
| AWS Secrets Manager | High | Low (AWS) | ✅ Yes |
| Kubernetes Secrets | Medium | Low (K8s) | ❌ Manual |
| Encrypted config files | Medium | Low | ❌ Manual |

**Final 30-Second**: OAuth2: auth code + PKCE for SPAs, client credentials for backend, device code for CLI/TV. mTLS for zero-trust service mesh. Secrets should be short-lived, auto-rotated, never in code. Use Vault or cloud-native secret stores.