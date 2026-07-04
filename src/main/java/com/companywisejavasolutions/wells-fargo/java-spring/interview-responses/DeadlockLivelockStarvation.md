# Deadlock Livelock Starvation - Interview Response

## What Is It?

Deadlock means threads wait forever on each other, livelock means they keep reacting without progress, and starvation means a thread rarely gets CPU or lock access.

## In Simple Terms

Deadlock means threads wait forever on each other, livelock means they keep reacting without progress, and starvation means a thread rarely gets CPU or lock access.

## Why It Matters

Two transfer threads locking account A then B and B then A can deadlock.

If we get it wrong:

```text
Do not acquire the same pair of locks in different orders.
Do not spin forever while trying to be polite to another thread.
Do not ignore starvation when a lock is highly contended.
```

## Example

```text
Lock first = lockFor(minAccountId);
Lock second = lockFor(maxAccountId);
first.lock();
try { second.lock(); try { transfer work } finally { second.unlock(); } }
finally { first.unlock(); }
```

## Safe vs Unsafe

Safe:

```text
Acquire locks in a consistent global order.
Use timeouts around lock acquisition when possible.
Prefer fair locks only when starvation matters; fairness can reduce throughput.
Monitor blocked threads and lock wait times.
```

Unsafe:

```text
Do not acquire the same pair of locks in different orders.
Do not spin forever while trying to be polite to another thread.
Do not ignore starvation when a lock is highly contended.
```

## Java / Spring Backend Use Case

Two transfer threads locking account A then B and B then A can deadlock.

Java/Spring angle:

```text
Lock first = lockFor(minAccountId);
Lock second = lockFor(maxAccountId);
first.lock();
try { second.lock(); try { transfer work } finally { second.unlock(); } }
finally { first.unlock(); }
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not acquire the same pair of locks in different orders.
- Do not spin forever while trying to be polite to another thread.
- Do not ignore starvation when a lock is highly contended.

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

In an interview, I would say: Deadlock means threads wait forever on each other, livelock means they keep reacting without progress, and starvation means a thread rarely gets CPU or lock access. For example, Two transfer threads locking account A then B and B then A can deadlock. The main production risk is acquire the same pair of locks in different orders.
