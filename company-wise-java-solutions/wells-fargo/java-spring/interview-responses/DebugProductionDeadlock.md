# Debug Production Deadlock - Interview Response

## What Is It?

Debug a production deadlock by capturing thread dumps, identifying blocked threads and owned locks, correlating with recent traffic/deployments, and applying a low-risk mitigation.

## In Simple Terms

Debug a production deadlock by capturing thread dumps, identifying blocked threads and owned locks, correlating with recent traffic/deployments, and applying a low-risk mitigation.

## Why It Matters

Use jstack or Java Flight Recorder to see which transfer threads hold account locks.

If we get it wrong:

```text
Do not restart before collecting evidence if the system is stable enough.
Do not treat the symptom without fixing lock ordering.
Do not ignore database locks; not every deadlock is a Java monitor deadlock.
```

## Example

```text
Operational flow:
1. Capture thread dump / JFR.
2. Identify lock cycle.
3. Reduce traffic or restart only if necessary.
4. Patch code to use consistent lock ordering/timeouts.
```

Key interview details:

- thread dump, jstack, blocked threads, lock owners, mitigation, and prevention.

## Safe vs Unsafe

Safe:

```text
Take multiple thread dumps a few seconds apart.
Look for BLOCKED threads and deadlock sections.
Correlate thread names, endpoints, and lock owners.
Mitigate carefully, then fix lock ordering or transaction design.
```

Unsafe:

```text
Do not restart before collecting evidence if the system is stable enough.
Do not treat the symptom without fixing lock ordering.
Do not ignore database locks; not every deadlock is a Java monitor deadlock.
```

## Java / Spring Backend Use Case

Use jstack or Java Flight Recorder to see which transfer threads hold account locks.

Java/Spring angle:

```text
Operational flow:
1. Capture thread dump / JFR.
2. Identify lock cycle.
3. Reduce traffic or restart only if necessary.
4. Patch code to use consistent lock ordering/timeouts.
```

## Production Concerns

- Define the concept, describe internal behavior, and explain the production consequence.
- State when to use it, when not to use it, and what trade-off is being accepted.
- Include failure handling, testing approach, and observability signal.
- Production answer: connect the topic to a real banking/backend scenario.

## Common Mistakes

- Do not restart before collecting evidence if the system is stable enough.
- Do not treat the symptom without fixing lock ordering.
- Do not ignore database locks; not every deadlock is a Java monitor deadlock.

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

In an interview, I would say: Debug a production deadlock by capturing thread dumps, identifying blocked threads and owned locks, correlating with recent traffic/deployments, and applying a low-risk mitigation. For example, use jstack or Java Flight Recorder to see which transfer threads hold account locks. The main production risk is restart before collecting evidence if the system is stable enough.
