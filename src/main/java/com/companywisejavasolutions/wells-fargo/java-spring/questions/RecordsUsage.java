package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class RecordsUsage {

    /*
     * QUESTION 28: What are records and when would you use them?
     *
     * SHORT ANSWER
     * Records are concise immutable data carriers that generate constructor, accessors, equals, hashCode, and toString from declared components.
     *
     * IN-DEPTH ANSWER
     * - Records are shallowly immutable; mutable components still need care.
     * - Validate invariants in a compact constructor.
     * - Do not use records for JPA entities that need proxy/mutable lifecycle behavior.
     * - Keep domain behavior in richer classes when invariants are complex.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * record TransferRequest(String fromAccount, String toAccount, BigDecimal amount) {
     *   TransferRequest {
     *     Objects.requireNonNull(fromAccount);
     *     Objects.requireNonNull(toAccount);
     *     if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be positive");
     *   }
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * Use records for API DTOs, query projections, and small value objects when behavior is minimal.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not treat records as deeply immutable when components are mutable.
     * - Do not use records for mutable persistence entities.
     * - Do not skip constructor validation for value objects.
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
        return "Records are concise immutable data carriers that generate constructor, accessors, equals, hashCode, and toString from declared components.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Records are shallowly immutable; mutable components still need care.",
            "Validate invariants in a compact constructor.",
            "Do not use records for JPA entities that need proxy/mutable lifecycle behavior.",
            "Keep domain behavior in richer classes when invariants are complex."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Start with language/library semantics, then connect to correctness, maintainability, and performance.",
            "Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.",
            "Show when the feature improves design and when it makes code harder to read or maintain.",
            "Production answer: prefer simple, explicit code until the abstraction removes real complexity."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "unit tests for edge cases and contracts",
            "mutation/immutability tests where relevant",
            "performance tests only when the topic is performance-sensitive",
            "API readability review with realistic examples"
        );
    }

    public String commonMistake() {
        return "Do not treat records as deeply immutable when components are mutable.";
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
        RecordsUsage notes = new RecordsUsage();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
