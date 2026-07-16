package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class LambdasAndFunctionalInterfaces {

    /*
     * QUESTION 21: Explain lambda expressions and functional interfaces.
     *
     * SHORT ANSWER
     * A lambda is an inline implementation of a functional interface, which is an interface with exactly one abstract method.
     *
     * IN-DEPTH ANSWER
     * - Keep lambdas short and side-effect light.
     * - Use named methods when logic needs explanation or reuse.
     * - Functional interfaces enable strategy-style behavior.
     * - Be careful capturing mutable variables from outer scope.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * Predicate<Transaction> highValue = tx -> tx.amount().compareTo(limit) > 0;
     * transactions.stream().filter(highValue).toList();
     *
     * REAL BACKEND / BANKING USE CASE
     * Use lambdas for small behavior parameters such as filtering suspicious transactions.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not put large business workflows inside one lambda.
     * - Do not mutate shared state from a lambda in a parallel stream.
     * - Do not forget @FunctionalInterface can catch accidental extra abstract methods.
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
        return "A lambda is an inline implementation of a functional interface, which is an interface with exactly one abstract method.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Keep lambdas short and side-effect light.",
            "Use named methods when logic needs explanation or reuse.",
            "Functional interfaces enable strategy-style behavior.",
            "Be careful capturing mutable variables from outer scope."
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
        return "Do not put large business workflows inside one lambda.";
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
        LambdasAndFunctionalInterfaces notes = new LambdasAndFunctionalInterfaces();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
