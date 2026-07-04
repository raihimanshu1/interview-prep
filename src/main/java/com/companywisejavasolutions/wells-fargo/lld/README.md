# Wells Fargo LLD / OOD

Use Java classes, interfaces, invariants, and test cases. For senior rounds, focus on clean extension points and correctness under concurrency.

| # | Question | Design Focus |
|---:|---|---|
| 1 | Design an LRU cache. | HashMap + doubly linked list, O(1), capacity edge cases. |
| 2 | Design an LFU cache. | Frequency buckets, tie-break by recency, O(1) operations. |
| 3 | Design a rate limiter for banking APIs. | Token bucket/sliding window, per customer/app, distributed counters. |
| 4 | Design an idempotency-key store for transfer APIs. | Request hash, response replay, TTL, race handling. |
| 5 | Design a money transfer service. | Account, ledger entry, transaction state, validation, compensation. |
| 6 | Design an ATM machine. | Cash inventory, withdrawal strategy, card/session/auth, failure states. |
| 7 | Design a notification service. | Channel strategy, templates, retry, preference, audit. |
| 8 | Design a fraud rule engine. | Rule interface, score aggregation, rule versioning, explainability. |
| 9 | Design a workflow/case management engine. | State machine, assignment, SLA timers, comments, audit history. |
| 10 | Design a transaction search API. | Query object, filters, pagination, sorting, access rules. |
| 11 | Design a retry framework. | Backoff, max attempts, dead-letter queue, idempotent handler. |
| 12 | Design a circuit breaker. | Closed/open/half-open states, metrics, reset interval. |
| 13 | Design a file ingestion validator. | Parser, schema validator, duplicate detector, quarantine. |
| 14 | Design a scheduler for recurring batch jobs. | Job metadata, locks, retries, dependencies, missed executions. |
| 15 | Design a secure audit logger. | Append-only entries, masking, correlation id, tamper detection. |
| 16 | Design a parking lot or elevator system. | Classic OOD warm-up; entities, state, allocation rules. |
| 17 | Design a bank account domain model. | Account, customer, balance, holds, postings, available vs ledger balance. |
| 18 | Design a statement generator. | Data fetchers, renderer, storage, async job status. |
| 19 | Design a distributed lock abstraction. | Lease, fencing token, renewal, timeout, failure handling. |
| 20 | Design a customer entitlement model. | User, role, permission, resource, policy evaluation. |
| 21 | Design a transaction reconciliation engine. | Match strategy, tolerance, exception queue, manual resolution. |
| 22 | Design a metrics collector. | Counters/timers/gauges, tags, async publishing, cardinality control. |

## What Interviewers Usually Probe

- Can the model handle invalid states?
- Is the class boundary clean enough to test?
- What happens under concurrent calls?
- How would the design change for distributed deployment?
- What gets persisted and what can be derived?
- How do you avoid leaking PII in logs, exceptions, and test data?
