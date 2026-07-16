package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class MicroserviceCommunication {

    /*
     * QUESTION 64: How do microservices communicate: REST, messaging, events?
     *
     * SHORT ANSWER
     * Microservices communicate through synchronous APIs such as REST/gRPC and asynchronous messaging/events, chosen by consistency, latency, coupling, and failure tolerance.
     *
     * IN-DEPTH ANSWER
     * - Use sync calls for immediate answers the user is waiting for.
     * - Use events for state changes consumed by many services.
     * - Design timeouts, retries, and idempotency for every remote call.
     * - Keep contracts versioned and tested.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
     * - Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
     * - Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
     * - Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // REST: request/response, direct dependency, user-facing latency.
     * // Event: publish state change, consumers process independently, eventual consistency.
     * // Messaging command: async work queue with explicit consumer ownership.
     *
     * REAL BACKEND / BANKING USE CASE
     * Balance inquiry may use REST; payment-posted notifications are better as events.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not make every interaction synchronous.
     * - Do not publish events without clear ownership and schema versioning.
     * - Do not ignore timeouts and idempotency.
     *
     * INTERVIEW TIPS
     * - Give the one-line definition first so the interviewer knows you are grounded.
     * - Immediately connect it to a banking/backend example.
     * - Name the trap, such as race conditions, hidden coupling, inconsistent data, or throughput loss.
     * - Explain the trade-off and the production guardrail you would put in place.
     *
     * FOLLOW-UP QUESTIONS TO BE READY FOR
     * - How does this behave under concurrent requests?
     * - What happens when a downstream service or database operation fails?
     * - How would you test this and prove it works in production?
     *
     * SOURCES
     * - https://docs.oracle.com/en/java/javase/17/docs/api/
     */

    public String shortAnswer() {
        return "Microservices communicate through synchronous APIs such as REST/gRPC and asynchronous messaging/events, chosen by consistency, latency, coupling, and failure tolerance.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Use sync calls for immediate answers the user is waiting for.",
            "Use events for state changes consumed by many services.",
            "Design timeouts, retries, and idempotency for every remote call.",
            "Keep contracts versioned and tested."
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

    public String commonMistake() {
        return "Do not make every interaction synchronous.";
    }

    public boolean isProductionReadyExample(String answer) {
        String normalized = Objects.requireNonNullElse(answer, "").toLowerCase();
        return normalized.contains("trade-off")
                || normalized.contains("failure")
                || normalized.contains("transaction")
                || normalized.contains("concurrent")
                || normalized.contains("test");
    }

    public BigDecimal positiveAmount(BigDecimal amount) {
        BigDecimal value = Objects.requireNonNull(amount, "amount must not be null");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return value;
    }

    public static void main(String[] args) {
        MicroserviceCommunication notes = new MicroserviceCommunication();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
