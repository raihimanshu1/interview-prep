# Wells Fargo HLD / System Design

Senior Java interviews can lean toward business-realistic architecture: transaction correctness, auditability, resiliency, API integration, cloud migration, and secure data handling.

| # | Question |
|---:|---|
| 1 | Design a high-throughput payment processing platform for millions of banking transactions. Cover API layer, validation, ledger writes, queues, retries, reconciliation, and audit logs. |
| 2 | Design a real-time fraud detection system that scores transactions before authorization and also supports offline model refresh. |
| 3 | Design a case-management modernization platform migrating legacy batch/on-prem flows to cloud-native Spring Boot APIs. |
| 4 | Design a distributed ledger service where every money movement must be immutable, auditable, and exactly-once from the customer perspective. |
| 5 | Design an account balance service that supports high read traffic while avoiding stale or incorrect balances after transfers. |
| 6 | Design a real-time notification platform for debit card alerts, suspicious login alerts, and payment-status updates. |
| 7 | Design a customer profile service with PII protection, audit access, consent flags, and downstream integrations. |
| 8 | Design an API gateway for internal banking microservices with authentication, authorization, rate limiting, tracing, and versioning. |
| 9 | Design a batch-to-event migration for nightly settlement jobs. How do you run old and new systems safely in parallel? |
| 10 | Design a transaction search system where support agents can search by customer, account, reference id, amount, date range, and status. |
| 11 | Design a data reconciliation system that compares bank ledger records with external network settlement files. |
| 12 | Design a reliable document upload and review system for loan or account-opening workflows. |
| 13 | Design a distributed caching layer for customer/session/lookup data. Explain invalidation, TTLs, cache stampede protection, and consistency risk. |
| 14 | Design a bank-grade logging and observability platform. What logs are safe, what must be masked, and how do you trace one customer action across services? |
| 15 | Design disaster recovery for a critical banking API. Define RTO, RPO, multi-region strategy, failover, and data consistency. |
| 16 | Design a secure fund transfer system. Cover idempotency, duplicate prevention, limits, sanctions/fraud checks, and rollback strategy. |
| 17 | Design a high-volume statement generation system that creates monthly PDFs and supports re-generation on demand. |
| 18 | Design an entitlement service for employee access to internal banking tools using role-based and attribute-based access control. |
| 19 | Design a real-time data pipeline that streams transaction events to analytics, monitoring, and fraud consumers. |
| 20 | Design a feature flag and rollout system for regulated banking services. How do you limit blast radius and provide audit evidence? |
| 21 | Design a customer support timeline that aggregates events from cards, deposits, loans, and authentication systems. |
| 22 | Design an SLA-aware workflow engine for operational cases with escalations, due dates, and assignment rules. |
| 23 | Design a secure partner API integration for payment status callbacks and file exchange. |
| 24 | Design a cloud migration plan for a monolithic Java banking application. Include strangler pattern, data migration, testing, and rollback. |

## Senior-Level Talking Points

- Correctness beats raw throughput in money movement.
- Prefer immutable event/audit trails, idempotency keys, and reconciliation over blind retry.
- Call out transactional boundaries clearly: database transaction, message transaction, distributed saga, and compensation.
- Explain where strong consistency is required and where eventual consistency is acceptable.
- Always mention PII masking, encryption, secrets, least privilege, and regulatory auditability.
- Include operational readiness: dashboards, alerts, runbooks, SLOs, load tests, and rollback.
