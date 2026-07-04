
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class IdempotentPostApis {

    /*
     * QUESTION 61: How do you design idempotent POST APIs?
     *
     * SHORT ANSWER
     * Design idempotent POST APIs with a client-provided idempotency key, a uniqueness constraint, request fingerprinting, and replay of the original result.
     *
     * IN-DEPTH ANSWER
     * - Require a unique idempotency key per logical operation.
     * - Store request hash and response/status atomically.
     * - Reject key reuse with a different payload.
     * - Use database uniqueness as the final guard.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // @Transactional
     * // INSERT idempotency_key; if duplicate, return stored result.
     * // Verify request hash matches the first request.
     * // Execute operation once and persist final response before commit.
     *
     * REAL BACKEND / BANKING USE CASE
     * Retrying a transfer request with the same key should return the same transfer result, not create a second transfer.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not depend only on a client retry flag.
     * - Do not allow one idempotency key to represent different payloads.
     * - Do not keep idempotency state only in memory.
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
        return "Design idempotent POST APIs with a client-provided idempotency key, a uniqueness constraint, request fingerprinting, and replay of the original result.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Require a unique idempotency key per logical operation.",
            "Store request hash and response/status atomically.",
            "Reject key reuse with a different payload.",
            "Use database uniqueness as the final guard."
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
        return "Do not depend only on a client retry flag.";
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
        IdempotentPostApis notes = new IdempotentPostApis();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
