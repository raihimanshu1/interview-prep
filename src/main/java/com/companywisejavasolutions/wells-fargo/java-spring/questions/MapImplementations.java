package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class MapImplementations {

    /*
     * QUESTION 10: Difference between HashMap, LinkedHashMap, TreeMap, and ConcurrentHashMap.
     *
     * SHORT ANSWER
     * HashMap is unordered, LinkedHashMap preserves insertion/access order, TreeMap sorts keys, and ConcurrentHashMap supports concurrent access.
     *
     * IN-DEPTH ANSWER
     * - Choose based on ordering, concurrency, and key comparison needs.
     * - HashMap allows one null key; ConcurrentHashMap does not.
     * - TreeMap requires comparable keys or a Comparator.
     * - None of these replaces a transactional database for financial truth.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * Map<String, String> byId = new HashMap<>();
     * Map<String, String> ordered = new LinkedHashMap<>();
     * Map<String, String> sorted = new TreeMap<>();
     * ConcurrentMap<String, Long> concurrent = new ConcurrentHashMap<>();
     *
     * REAL BACKEND / BANKING USE CASE
     * Use LinkedHashMap for a small LRU-style order, TreeMap for sorted statement lines, and ConcurrentHashMap for shared counters.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not choose a map without considering ordering and concurrency.
     * - Do not use TreeMap keys that cannot be compared consistently.
     * - Do not store persistent truth in an in-memory map.
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
        return "HashMap is unordered, LinkedHashMap preserves insertion/access order, TreeMap sorts keys, and ConcurrentHashMap supports concurrent access.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Choose based on ordering, concurrency, and key comparison needs.",
            "HashMap allows one null key; ConcurrentHashMap does not.",
            "TreeMap requires comparable keys or a Comparator.",
            "None of these replaces a transactional database for financial truth."
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
        return "Do not choose a map without considering ordering and concurrency.";
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
        MapImplementations notes = new MapImplementations();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
