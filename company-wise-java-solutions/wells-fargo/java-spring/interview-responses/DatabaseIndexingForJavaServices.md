# Database Indexing For Java Services - Interview Response

## What Is It?

Indexes speed up reads by letting the database find rows without scanning everything, but they cost storage and slow writes because indexes must be maintained.

## In Simple Terms

Indexes speed up reads by letting the database find rows without scanning everything, but they cost storage and slow writes because indexes must be maintained.

## Why It Matters

A transfer lookup by customerId and createdAt needs an index that matches the query filter and sort pattern.

If we get it wrong:

```text
Do not add indexes blindly for every column.
Do not ignore write overhead.
Do not diagnose slow APIs without checking query plans.
```

## Example

```text
Query:
WHERE customer_id = ? AND created_at >= ? ORDER BY created_at DESC
Candidate index: (customer_id, created_at DESC)
```

Key interview details:

- single/composite indexes, selectivity, EXPLAIN plan, write overhead, and Spring Data query impact.

## Safe vs Unsafe

Safe:

```text
Composite index column order matters.
Low-cardinality columns alone may not be selective enough.
Indexes help WHERE, JOIN, ORDER BY, and uniqueness constraints when designed correctly.
Use explain plans and real query stats, not guesses.
```

Unsafe:

```text
Do not add indexes blindly for every column.
Do not ignore write overhead.
Do not diagnose slow APIs without checking query plans.
```

## Java / Spring Backend Use Case

A transfer lookup by customerId and createdAt needs an index that matches the query filter and sort pattern.

Java/Spring angle:

```text
Query:
WHERE customer_id = ? AND created_at >= ? ORDER BY created_at DESC
Candidate index: (customer_id, created_at DESC)
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not add indexes blindly for every column.
- Do not ignore write overhead.
- Do not diagnose slow APIs without checking query plans.

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

In an interview, I would say: Indexes speed up reads by letting the database find rows without scanning everything, but they cost storage and slow writes because indexes must be maintained. For example, a transfer lookup by customerId and createdAt needs an index that matches the query filter and sort pattern. The main production risk is add indexes blindly for every column.
