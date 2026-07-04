
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class RetryWithoutDuplicateFinancialOperations {

    /*
     * QUESTION 44: How do you design a retry without duplicating financial operations?
     *
     * SHORT ANSWER
     * Retries must be protected by idempotency keys, unique constraints, operation status tracking, and safe retry policies so the same financial command is executed once.
     *
     * IN-DEPTH ANSWER
     * - Generate or require a stable operation key.
     * - Store the key and operation result in the same transaction as the financial write.
     * - Make external calls idempotent or reconcile by reference ID.
     * - Retry only transient failures with backoff and jitter.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Unique key: (clientId, idempotencyKey)
     * // If insert succeeds, process once.
     * // If duplicate, return stored status/result.
     * // If in progress, return 202 or wait according to API policy.
     *
     * REAL BACKEND / BANKING USE CASE
     * A network timeout after posting a transfer should allow the client to retry and receive the existing transfer result.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not retry money movement without idempotency.
     * - Do not store idempotency keys outside the transaction that creates the operation.
     * - Do not treat every error as retryable.
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
        return "Retries must be protected by idempotency keys, unique constraints, operation status tracking, and safe retry policies so the same financial command is executed once.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Generate or require a stable operation key.",
            "Store the key and operation result in the same transaction as the financial write.",
            "Make external calls idempotent or reconcile by reference ID.",
            "Retry only transient failures with backoff and jitter."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Define the concept, describe internal behavior, and explain the production consequence.",
            "State when to use it, when not to use it, and what trade-off is being accepted.",
            "Include failure handling, testing approach, and observability signal.",
            "Production answer: connect the topic to a real banking/backend scenario."
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
        return "Do not retry money movement without idempotency.";
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
        RetryWithoutDuplicateFinancialOperations notes = new RetryWithoutDuplicateFinancialOperations();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
