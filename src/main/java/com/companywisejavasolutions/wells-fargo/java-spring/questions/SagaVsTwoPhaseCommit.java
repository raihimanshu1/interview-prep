package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class SagaVsTwoPhaseCommit {

    /*
     * QUESTION 65: Saga vs two-phase commit.
     *
     * SHORT ANSWER
     * Two-phase commit coordinates one atomic transaction across resources; saga breaks work into local transactions with compensating actions and eventual consistency.
     *
     * IN-DEPTH ANSWER
     * - 2PC gives stronger atomicity but adds coordinator complexity and blocking risk.
     * - Saga scales across services but requires compensation and state tracking.
     * - Use orchestration when central workflow visibility matters.
     * - Use choreography carefully to avoid hidden coupling.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Model saga as a state machine with durable steps, retries, compensation, idempotency keys, and timeout handling.
     * - Compare orchestration and choreography with coupling and observability trade-offs.
     * - Mention compensation is not true rollback; it is a business action that must be auditable.
     * - Production answer: use normal ACID transaction when one database owns the invariant; use saga across service boundaries.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Saga example:
     * // reserve funds -> create payment -> notify ledger.
     * // If payment fails, compensate by releasing reserved funds.
     *
     * REAL BACKEND / BANKING USE CASE
     * A cross-service loan workflow often uses saga; a single database transfer can use a normal ACID transaction.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not use saga when strict immediate atomicity is required and a local transaction is possible.
     * - Do not forget compensation paths.
     * - Do not hide workflow state across many event handlers without observability.
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
        return "Two-phase commit coordinates one atomic transaction across resources; saga breaks work into local transactions with compensating actions and eventual consistency.";
    }

    public List<String> keyPoints() {
        return List.of(
            "2PC gives stronger atomicity but adds coordinator complexity and blocking risk.",
            "Saga scales across services but requires compensation and state tracking.",
            "Use orchestration when central workflow visibility matters.",
            "Use choreography carefully to avoid hidden coupling."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Model saga as a state machine with durable steps, retries, compensation, idempotency keys, and timeout handling.",
            "Compare orchestration and choreography with coupling and observability trade-offs.",
            "Mention compensation is not true rollback; it is a business action that must be auditable.",
            "Production answer: use normal ACID transaction when one database owns the invariant; use saga across service boundaries."
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
        return "Do not use saga when strict immediate atomicity is required and a local transaction is possible.";
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
        SagaVsTwoPhaseCommit notes = new SagaVsTwoPhaseCommit();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
