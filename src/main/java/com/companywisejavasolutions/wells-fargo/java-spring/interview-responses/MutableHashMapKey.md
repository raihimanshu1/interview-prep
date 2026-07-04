# Mutable Hash Map Key - Interview Response

## What Is It?

If a key changes fields used by equals or hashCode after insertion, HashMap may not find it because it is stored in the bucket for the old hash.

## In Simple Terms

If a key changes fields used by equals or hashCode after insertion, HashMap may not find it because it is stored in the bucket for the old hash.

## Why It Matters

A mutable AccountKey whose account number changes can make cache lookups fail.

If we get it wrong:

```text
Do not mutate key fields after insertion.
Do not base hashCode on values that can change.
Do not use arrays directly as map keys unless identity semantics are intended.
```

## Example

```text
record AccountKey(String bankId, String accountId) { }
Map<AccountKey, BigDecimal> balances = new HashMap<>();
balances.put(new AccountKey("001", "A123"), BigDecimal.TEN);
```

Key interview details:

- Java code showing broken lookup after key mutation.

## Safe vs Unsafe

Safe:

```text
Use immutable key objects.
Use records or final fields for value keys.
Never mutate key identity while inside a map/set.
Remove and reinsert if identity must change.
```

Unsafe:

```text
Do not mutate key fields after insertion.
Do not base hashCode on values that can change.
Do not use arrays directly as map keys unless identity semantics are intended.
```

## Java / Spring Backend Use Case

A mutable AccountKey whose account number changes can make cache lookups fail.

Java/Spring angle:

```text
record AccountKey(String bankId, String accountId) { }
Map<AccountKey, BigDecimal> balances = new HashMap<>();
balances.put(new AccountKey("001", "A123"), BigDecimal.TEN);
```

## Production Concerns

- Start with language/library semantics, then connect to correctness, maintainability, and performance.
- Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
- Show when the feature improves design and when it makes code harder to read or maintain.
- Production answer: prefer simple, explicit code until the abstraction removes real complexity.

## Common Mistakes

- Do not mutate key fields after insertion.
- Do not base hashCode on values that can change.
- Do not use arrays directly as map keys unless identity semantics are intended.

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

- Strategy
- Adapter
- Factory
- Composition over inheritance

## Follow-Up Interview Questions

- How does this behave under concurrent requests?
- What happens when a downstream service or database operation fails?
- How would you test this and prove it works in production?

## Interview Answer

In an interview, I would say: If a key changes fields used by equals or hashCode after insertion, HashMap may not find it because it is stored in the bucket for the old hash. For example, a mutable AccountKey whose account number changes can make cache lookups fail. The main production risk is mutate key fields after insertion.
