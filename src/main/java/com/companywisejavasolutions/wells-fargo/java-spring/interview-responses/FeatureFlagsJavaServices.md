# Feature Flags Java Services - Interview Response

## What Is It?

Feature flags decouple deployment from release by letting teams enable, disable, or target behavior at runtime without shipping new code.

## In Simple Terms

Feature flags decouple deployment from release by letting teams enable, disable, or target behavior at runtime without shipping new code.

## Why It Matters

A new transfer-risk rule can be deployed disabled, enabled for internal users, then gradually rolled out.

If we get it wrong:

```text
Do not leave old flags forever.
Do not make flag failures choose unsafe behavior.
Do not hide flag state from logs/audit when it affects decisions.
```

## Example

```text
if (featureFlags.enabled("new-risk-rule", customerSegment)) {
riskEngine.applyNewRule(command);
} else {
riskEngine.applyCurrentRule(command);
}
```

Key interview details:

- rollout percentage, kill switch, config store, audit, stale flag cleanup.

## Safe vs Unsafe

Safe:

```text
Flags support canary releases, kill switches, A/B tests, and operational control.
Flag state must be observable and auditable for critical systems.
Flags need cleanup after rollout to avoid permanent complexity.
Critical financial behavior should have safe defaults when flag service is unavailable.
```

Unsafe:

```text
Do not leave old flags forever.
Do not make flag failures choose unsafe behavior.
Do not hide flag state from logs/audit when it affects decisions.
```

## Java / Spring Backend Use Case

A new transfer-risk rule can be deployed disabled, enabled for internal users, then gradually rolled out.

Java/Spring angle:

```text
if (featureFlags.enabled("new-risk-rule", customerSegment)) {
riskEngine.applyNewRule(command);
} else {
riskEngine.applyCurrentRule(command);
}
```

## Production Concerns

- Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
- Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
- Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
- Production answer: optimize for correctness and operability first, then throughput.

## Common Mistakes

- Do not leave old flags forever.
- Do not make flag failures choose unsafe behavior.
- Do not hide flag state from logs/audit when it affects decisions.

## Extra Details

Forward compatibility:

```text
Compatibility matters when this topic changes behavior exposed through APIs, shared libraries, event payloads, config properties, or deployment defaults. New behavior should be rolled out so older callers and services keep working safely.
```

Backward compatibility:

```text
Do not break existing callers, tests, serialized data, configuration, or operational runbooks silently. Keep old behavior available until users or services migrate.
```

Semantic versioning:

```text
MAJOR -> breaking public behavior or contract
MINOR -> compatible feature or API addition
PATCH -> bug fix or internal tuning
```

Big-company evolution mindset:

```text
Large engineering teams roll out changes gradually, keep compatibility during migration, measure usage, document deprecation, and avoid forcing all services to upgrade at once.
```

Related patterns:

- Dependency Injection
- Service layer
- Repository
- DTO/Adapter

## Follow-Up Interview Questions

- What breaks under high concurrency or partial failure?
- How would you verify this with tests, metrics, logs, or traces?
- What trade-off would make you choose a different design?

## Interview Answer

In an interview, I would say: Feature flags decouple deployment from release by letting teams enable, disable, or target behavior at runtime without shipping new code. For example, a new transfer-risk rule can be deployed disabled, enabled for internal users, then gradually rolled out. The main production risk is leave old flags forever.
