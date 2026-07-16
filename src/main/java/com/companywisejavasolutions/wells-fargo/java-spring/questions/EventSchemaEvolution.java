package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class EventSchemaEvolution {

    /*
     * QUESTION 69: How do you handle schema evolution in event payloads?
     *
     * SHORT ANSWER
     * Handle event schema evolution with backward/forward compatible changes, versioned contracts, tolerant readers, schema registry validation, and explicit deprecation windows.
     *
     * IN-DEPTH ANSWER
     * - Add optional fields before requiring them.
     * - Never change field meaning silently.
     * - Keep consumers tolerant of unknown fields.
     * - Use contract tests and schema registry checks in CI.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
     * - Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
     * - Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
     * - Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Prefer additive event changes:
     * // v1: { paymentId, amount }
     * // v2: { paymentId, amount, currency } // optional/defaulted for old consumers
     * // Breaking changes should publish a new event type or major version.
     *
     * REAL BACKEND / BANKING USE CASE
     * A PaymentPosted event can add optional fields without breaking old consumers, but removing or renaming fields requires a versioning plan.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not rename or remove fields without versioning.
     * - Do not change the meaning of an existing field silently.
     * - Do not deploy producer changes without consumer contract coverage.
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
        return "Handle event schema evolution with backward/forward compatible changes, versioned contracts, tolerant readers, schema registry validation, and explicit deprecation windows.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Add optional fields before requiring them.",
            "Never change field meaning silently.",
            "Keep consumers tolerant of unknown fields.",
            "Use contract tests and schema registry checks in CI."
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
        return "Do not rename or remove fields without versioning.";
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
        EventSchemaEvolution notes = new EventSchemaEvolution();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
