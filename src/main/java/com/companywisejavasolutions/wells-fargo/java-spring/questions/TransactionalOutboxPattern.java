package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class TransactionalOutboxPattern {

    /*
     * QUESTION 97: Explain the transactional outbox pattern.
     *
     * SHORT ANSWER
     * The transactional outbox pattern stores business data and an event record in the same database transaction, then a separate publisher sends the event reliably.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - It solves the dual-write problem between database and message broker.
     * - A relay process publishes pending outbox rows and marks them sent.
     * - Consumers must still be idempotent because publishing can retry.
     * - Outbox table growth and relay lag need monitoring.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain the dual-write problem and why database plus broker writes can lose or duplicate events.
     * - Describe durable outbox rows, relay publisher, idempotent event IDs, retry, and cleanup.
     * - Mention Debezium/CDC as an alternative to polling relays.
     * - Production answer: consumers still need idempotency because publishing can retry.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // @Transactional
     * // update ledger rows
     * // insert into outbox(event_id, aggregate_id, payload, status=PENDING)
     * // relay later publishes PENDING rows to Kafka and marks SENT.
     *
     * REAL BACKEND / BANKING USE CASE
     * When a transfer is posted, the service inserts ledger rows and an outbox PaymentPosted event atomically, avoiding lost events after commit.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not write DB and Kafka independently without a failure strategy.
     * - Do not forget idempotent event IDs.
     * - Do not leave outbox cleanup and monitoring undefined.
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
        return "The transactional outbox pattern stores business data and an event record in the same database transaction, then a separate publisher sends the event reliably.";
    }

    public List<String> keyPoints() {
        return List.of(
            "It solves the dual-write problem between database and message broker.",
            "A relay process publishes pending outbox rows and marks them sent.",
            "Consumers must still be idempotent because publishing can retry.",
            "Outbox table growth and relay lag need monitoring."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain the dual-write problem and why database plus broker writes can lose or duplicate events.",
            "Describe durable outbox rows, relay publisher, idempotent event IDs, retry, and cleanup.",
            "Mention Debezium/CDC as an alternative to polling relays.",
            "Production answer: consumers still need idempotency because publishing can retry."
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
        return "When a transfer is posted, the service inserts ledger rows and an outbox PaymentPosted event atomically, avoiding lost events after commit.";
    }

    public String commonMistake() {
        return "Do not write DB and Kafka independently without a failure strategy.";
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
        TransactionalOutboxPattern notes = new TransactionalOutboxPattern();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
