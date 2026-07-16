package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class StringVsBuilderVsBuffer {

    /*
     * QUESTION 7: Difference between String, StringBuilder, and StringBuffer.
     *
     * SHORT ANSWER
     * String is immutable, StringBuilder is mutable and fast for single-threaded concatenation, and StringBuffer is synchronized for legacy thread-safe mutation.
     *
     * IN-DEPTH ANSWER
     * - String creates new objects when concatenated repeatedly outside compiler optimizations.
     * - StringBuilder is usually the right choice inside loops or formatting code.
     * - StringBuffer synchronizes every operation and is rarely needed in modern service code.
     * - For user-facing formatting, prefer Formatter, MessageFormat, or templating when clarity matters.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * StringBuilder message = new StringBuilder(128);
     * message.append("transferId=").append(transferId);
     * message.append(", status=").append(status);
     * // Builder stays local to the method, so no cross-request sharing occurs.
     * return message.toString();
     *
     * REAL BACKEND / BANKING USE CASE
     * Building a large audit message inside one request should use StringBuilder, while shared mutable builders should be avoided.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not keep StringBuilder as a singleton service field.
     * - Do not use StringBuffer for safety if the real issue is shared mutable state.
     * - Do not concatenate in hot loops blindly.
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
        return "String is immutable, StringBuilder is mutable and fast for single-threaded concatenation, and StringBuffer is synchronized for legacy thread-safe mutation.";
    }

    public List<String> keyPoints() {
        return List.of(
            "String creates new objects when concatenated repeatedly outside compiler optimizations.",
            "StringBuilder is usually the right choice inside loops or formatting code.",
            "StringBuffer synchronizes every operation and is rarely needed in modern service code.",
            "For user-facing formatting, prefer Formatter, MessageFormat, or templating when clarity matters."
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
        return "Do not keep StringBuilder as a singleton service field.";
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
        StringVsBuilderVsBuffer notes = new StringVsBuilderVsBuffer();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
