package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class TransactionPropagationIsolation {

    /*
     * QUESTION 56: Transaction propagation and isolation in Spring.
     *
     * SHORT ANSWER
     * Propagation controls whether a method joins, creates, suspends, or forbids a transaction; isolation controls what concurrent database changes are visible inside a transaction.
     *
     * IN-DEPTH ANSWER
     * - REQUIRED joins an existing transaction or creates one.
     * - REQUIRES_NEW suspends the current transaction and starts a new one.
     * - READ_COMMITTED avoids dirty reads in many databases.
     * - SERIALIZABLE is strongest but can reduce throughput and increase retries.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Add rollback rules: unchecked exceptions roll back by default, checked exceptions need rollbackFor unless configured.
     * - Explain Spring proxy boundaries, including self-invocation and private method traps.
     * - Discuss isolation anomalies: dirty read, non-repeatable read, phantom read, and database-specific behavior.
     * - Production answer: keep transactions short, retry deadlocks/serialization failures, and avoid remote calls while holding DB locks.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // @Transactional(propagation = REQUIRED, isolation = READ_COMMITTED)
     * // public void transfer(...) { debit(); credit(); }
     * // @Transactional(propagation = REQUIRES_NEW)
     * // public void writeAudit(...) { ... }
     *
     * REAL BACKEND / BANKING USE CASE
     * A transfer use case may use REQUIRED for debit/credit, while an audit write may use REQUIRES_NEW so it commits independently.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not use high isolation everywhere without measuring lock impact.
     * - Do not assume REQUIRES_NEW participates in the outer rollback.
     * - Do not call transactional methods through self-invocation and expect proxy behavior.
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
        return "Propagation controls whether a method joins, creates, suspends, or forbids a transaction; isolation controls what concurrent database changes are visible inside a transaction.";
    }

    public List<String> keyPoints() {
        return List.of(
            "REQUIRED joins an existing transaction or creates one.",
            "REQUIRES_NEW suspends the current transaction and starts a new one.",
            "READ_COMMITTED avoids dirty reads in many databases.",
            "SERIALIZABLE is strongest but can reduce throughput and increase retries."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Add rollback rules: unchecked exceptions roll back by default, checked exceptions need rollbackFor unless configured.",
            "Explain Spring proxy boundaries, including self-invocation and private method traps.",
            "Discuss isolation anomalies: dirty read, non-repeatable read, phantom read, and database-specific behavior.",
            "Production answer: keep transactions short, retry deadlocks/serialization failures, and avoid remote calls while holding DB locks."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "unit tests",
            "integration tests",
            "failure-path tests",
            "observability checks"
        );
    }

    public String commonMistake() {
        return "Do not use high isolation everywhere without measuring lock impact.";
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
        TransactionPropagationIsolation notes = new TransactionPropagationIsolation();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
