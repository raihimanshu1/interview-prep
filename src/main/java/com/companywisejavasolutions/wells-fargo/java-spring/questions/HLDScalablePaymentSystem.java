package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class HLDScalablePaymentSystem {

    /*
     * QUESTION 119: High-level design a scalable payment processing system.
     *
     * SHORT ANSWER
     * A senior HLD answer covers API entry, validation, idempotency, ledger consistency, asynchronous processing, fraud/risk checks, eventing, observability, and failure recovery.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Separate command intake from processing when latency or downstream reliability requires it.
     * - Use idempotency keys and durable state machines.
     * - Use outbox/events for integration and reconciliation jobs for correctness.
     * - Design for auditability, tracing, rate limits, and disaster recovery.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Cover idempotent API intake, durable state machine, ledger consistency, outbox events, reconciliation, and audit.
     * - Discuss synchronous vs asynchronous processing and status APIs.
     * - Include rate limits, fraud checks, partner failures, DLQ/retry policy, tracing, and rollback strategy.
     * - Production answer: design for duplicate requests, partial failure, and financial audit before optimizing throughput.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Components:
     * // API Gateway -> Payment API -> Idempotency store -> Payment DB/Ledger -> Outbox/Kafka -> Workers -> Partner gateways -> Reconciliation.
     *
     * REAL BACKEND / BANKING USE CASE
     * A scalable payment platform may accept a request synchronously, create a durable payment record, process downstream steps asynchronously, and expose status APIs.
     *
     * SENIOR-SDE CHECKLIST
     * - Clarify scale, consistency, latency, security, compliance, and operability before choosing tools.
     * - Design for overload, dependency failure, stale data, duplicate requests, bad deploys, and rollback.
     * - Add observability and controls: metrics, traces, logs, probes, feature flags, rate limits, canaries, and audit trails.
     * - Verify with failure drills, load tests, deployment tests, and runbook-quality monitoring.
     *
     * COMMON MISTAKES
     * - Do not design only the happy path.
     * - Do not ignore duplicate requests and partner timeouts.
     * - Do not skip audit, reconciliation, and observability.
     *
     * INTERVIEW FOLLOW-UPS
     * - What breaks under high concurrency or partial failure?
     * - How would you verify this with tests, metrics, logs, or traces?
     * - What trade-off would make you choose a different design?
     *
     * RESEARCH SOURCES USED FOR TOPIC SELECTION
     * - https://www.geeksforgeeks.org/java-multithreading-interview-questions-and-answers/
     * - https://www.geeksforgeeks.org/springboot/spring-boot-interview-questions-and-answers/
     * - https://www.geeksforgeeks.org/java/java-microservices-architecture-development-interview-questions/
     * - https://www.baeldung.com/java-concurrency-interview-questions
     * - https://www.baeldung.com/spring-boot-interview-questions
     */

    public String shortAnswer() {
        return "A senior HLD answer covers API entry, validation, idempotency, ledger consistency, asynchronous processing, fraud/risk checks, eventing, observability, and failure recovery.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Separate command intake from processing when latency or downstream reliability requires it.",
            "Use idempotency keys and durable state machines.",
            "Use outbox/events for integration and reconciliation jobs for correctness.",
            "Design for auditability, tracing, rate limits, and disaster recovery."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Cover idempotent API intake, durable state machine, ledger consistency, outbox events, reconciliation, and audit.",
            "Discuss synchronous vs asynchronous processing and status APIs.",
            "Include rate limits, fraud checks, partner failures, DLQ/retry policy, tracing, and rollback strategy.",
            "Production answer: design for duplicate requests, partial failure, and financial audit before optimizing throughput."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "failure-mode tests",
            "load and soak tests",
            "deployment rollback drills",
            "observability dashboard/alert validation"
        );
    }

    public String realBackendUseCase() {
        return "A scalable payment platform may accept a request synchronously, create a durable payment record, process downstream steps asynchronously, and expose status APIs.";
    }

    public String commonMistake() {
        return "Do not design only the happy path.";
    }

    public boolean isSeniorLevelAnswer(String answer) {
        String normalized = Objects.requireNonNullElse(answer, "").toLowerCase();
        return normalized.contains("trade-off")
                || normalized.contains("failure")
                || normalized.contains("concurrency")
                || normalized.contains("monitor")
                || normalized.contains("test")
                || normalized.contains("rollback");
    }

    public BigDecimal requirePositiveAmount(BigDecimal amount) {
        BigDecimal value = Objects.requireNonNull(amount, "amount must not be null");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return value;
    }

    public Duration defaultTimeoutBudget() {
        return Duration.ofMillis(800);
    }

    public static void main(String[] args) {
        HLDScalablePaymentSystem notes = new HLDScalablePaymentSystem();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
