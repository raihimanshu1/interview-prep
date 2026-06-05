# Thread Safe Method - Interview Response

## What Is It?

A method is thread-safe when concurrent calls cannot corrupt shared state or observe inconsistent data; prefer statelessness, immutability, confinement, atomics, locks, or transactions.

## In Simple Terms

A method is thread-safe when concurrent calls cannot corrupt shared state or observe inconsistent data; prefer statelessness, immutability, confinement, atomics, locks, or transactions.

## Why It Matters

A singleton Spring service method is safe if it uses only local variables and thread-safe collaborators, but unsafe if it writes request data into fields.

If we get it wrong:

```text
Do not put per-request state in singleton fields.
Do not assume local variables need synchronization.
Do not use synchronized when a database transaction is the real consistency boundary.
```

## Example

```text
public BigDecimal calculateFee(BigDecimal amount) {
amount and fee are local variables, so concurrent requests do not share them.
BigDecimal fee = amount.multiply(new BigDecimal("0.02"));
return fee.max(BigDecimal.ONE);
}
```

Key interview details:

- stateless method, unsafe field, AtomicInteger, synchronized, immutable DTO, Spring singleton warning.

## Safe vs Unsafe

Safe:

```text
Identify shared mutable state first.
Prefer local variables and immutable objects.
Use atomic classes for simple counters.
Use locks or database transactions for compound invariants.
```

Unsafe:

```text
Do not put per-request state in singleton fields.
Do not assume local variables need synchronization.
Do not use synchronized when a database transaction is the real consistency boundary.
```

## Java / Spring Backend Use Case

A singleton Spring service method is safe if it uses only local variables and thread-safe collaborators, but unsafe if it writes request data into fields.

Java/Spring angle:

```text
public BigDecimal calculateFee(BigDecimal amount) {
amount and fee are local variables, so concurrent requests do not share them.
BigDecimal fee = amount.multiply(new BigDecimal("0.02"));
return fee.max(BigDecimal.ONE);
}
```

## Production Concerns

- Define the shared-state or scheduling problem before naming a concurrency primitive.
- Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
- Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
- Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.

## Common Mistakes

- Do not put per-request state in singleton fields.
- Do not assume local variables need synchronization.
- Do not use synchronized when a database transaction is the real consistency boundary.

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

- Immutable object pattern
- Thread confinement
- Producer-consumer
- Bulkhead

## Follow-Up Interview Questions

- How does this behave under concurrent requests?
- What happens when a downstream service or database operation fails?
- How would you test this and prove it works in production?

## Interview Answer

In an interview, I would say: A method is thread-safe when concurrent calls cannot corrupt shared state or observe inconsistent data; prefer statelessness, immutability, confinement, atomics, locks, or transactions. For example, a singleton Spring service method is safe if it uses only local variables and thread-safe collaborators, but unsafe if it writes request data into fields. The main production risk is put per-request state in singleton fields.
