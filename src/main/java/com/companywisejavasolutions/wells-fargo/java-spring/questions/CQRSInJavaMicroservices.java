package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class CQRSInJavaMicroservices {

    /*
     * QUESTION 98: What is CQRS and when is it useful?
     *
     * SHORT ANSWER
     * CQRS separates command models that change state from query models that read state, useful when read/write needs, scale, or data shapes differ significantly.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - CQRS can simplify complex domains but adds eventual consistency.
     * - It pairs naturally with event-driven read-model updates.
     * - It is not needed for every CRUD service.
     * - A senior answer should discuss consistency lag, replay, and operational complexity.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
     * - Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
     * - Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
     * - Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Command side: validate transfer and append ledger event.
     * // Query side: consume events and update statement projection.
     * // API must explain when read model may lag.
     *
     * REAL BACKEND / BANKING USE CASE
     * A payment command service can enforce transfer rules while a separate statement read model serves fast account-history queries.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not introduce CQRS for simple CRUD.
     * - Do not hide eventual consistency from clients.
     * - Do not forget rebuild/replay strategy for projections.
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
        return "CQRS separates command models that change state from query models that read state, useful when read/write needs, scale, or data shapes differ significantly.";
    }

    public List<String> keyPoints() {
        return List.of(
            "CQRS can simplify complex domains but adds eventual consistency.",
            "It pairs naturally with event-driven read-model updates.",
            "It is not needed for every CRUD service.",
            "A senior answer should discuss consistency lag, replay, and operational complexity."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.",
            "Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.",
            "Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.",
            "Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "consumer idempotency tests",
            "retry and DLQ integration tests",
            "schema/contract tests",
            "replay tests with duplicate and out-of-order events"
        );
    }

    public String realBackendUseCase() {
        return "A payment command service can enforce transfer rules while a separate statement read model serves fast account-history queries.";
    }

    public String commonMistake() {
        return "Do not introduce CQRS for simple CRUD.";
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
        CQRSInJavaMicroservices notes = new CQRSInJavaMicroservices();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
