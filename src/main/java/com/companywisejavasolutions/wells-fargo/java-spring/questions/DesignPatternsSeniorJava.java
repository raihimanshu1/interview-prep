package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class DesignPatternsSeniorJava {

    /*
     * QUESTION 116: Which design patterns matter most for senior Java backend interviews?
     *
     * SHORT ANSWER
     * Senior Java interviews expect practical pattern usage: Strategy for interchangeable behavior, Factory for creation, Template Method for algorithms, Decorator for cross-cutting behavior, and Adapter for external integrations.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Patterns are vocabulary for trade-offs, not goals by themselves.
     * - Spring already uses Proxy, Factory, Template, and Dependency Injection heavily.
     * - Use patterns to reduce coupling and isolate change.
     * - Over-patterned code is harder to maintain than direct code.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * interface PaymentRail { TransferResult post(Command command); }
     * final class AchRail implements PaymentRail { public TransferResult post(Command command) { return TransferResult.ok(); } }
     * // Strategy lets service choose rail without if/else spread everywhere.
     *
     * REAL BACKEND / BANKING USE CASE
     * A payment service can use Strategy for payment rails, Adapter for partner APIs, and Decorator for metrics around a client.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not force patterns where simple code is enough.
     * - Do not describe patterns without a business use case.
     * - Do not hide dependencies behind unnecessary factories.
     *
     * INTERVIEW FOLLOW-UPS
     * - What breaks under high concurrency or partial failure?
     * - How would you verify this with tests, metrics, logs, or traces?
     * - What trade-off would make you choose a different design?
     *
     * RESEARCH SOURCES USED FOR TOPIC SELECTION
     * - https://www.geeksforgeeks.org/java-multithreading-interview-questions-and-answers/
     * - https://www.geeksforgeeks.org/springboot/spring-boot-interview-questions-and-answers/
     * - https://www.geeksforgeeks.org/java/java-microservices-architecture-development-interview-questions/
     * - https://www.baeldung.com/java-concurrency-interview-questions
     * - https://www.baeldung.com/spring-boot-interview-questions
     */

    public String shortAnswer() {
        return "Senior Java interviews expect practical pattern usage: Strategy for interchangeable behavior, Factory for creation, Template Method for algorithms, Decorator for cross-cutting behavior, and Adapter for external integrations.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Patterns are vocabulary for trade-offs, not goals by themselves.",
            "Spring already uses Proxy, Factory, Template, and Dependency Injection heavily.",
            "Use patterns to reduce coupling and isolate change.",
            "Over-patterned code is harder to maintain than direct code."
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

    public String realBackendUseCase() {
        return "A payment service can use Strategy for payment rails, Adapter for partner APIs, and Decorator for metrics around a client.";
    }

    public String commonMistake() {
        return "Do not force patterns where simple code is enough.";
    }

    public boolean isSeniorLevelAnswer(String answer) {
        String normalized = Objects.requireNonNullElse(answer, "").toLowerCase();
        return normalized.contains("trade-off")
                || normalized.contains("failure")
                || normalized.contains("concurrency")
                || normalized.contains("monitor")
                || normalized.contains("test")
                || normalized.contains("rollback");
    }

    public BigDecimal requirePositiveAmount(BigDecimal amount) {
        BigDecimal value = Objects.requireNonNull(amount, "amount must not be null");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return value;
    }

    public Duration defaultTimeoutBudget() {
        return Duration.ofMillis(800);
    }

    public static void main(String[] args) {
        DesignPatternsSeniorJava notes = new DesignPatternsSeniorJava();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
