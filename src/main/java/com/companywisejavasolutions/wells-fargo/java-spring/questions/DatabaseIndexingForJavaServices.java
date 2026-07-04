
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class DatabaseIndexingForJavaServices {

    /*
     * QUESTION 101: What should a Java backend engineer know about database indexing?
     *
     * SHORT ANSWER
     * Indexes speed up reads by letting the database find rows without scanning everything, but they cost storage and slow writes because indexes must be maintained.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Composite index column order matters.
     * - Low-cardinality columns alone may not be selective enough.
     * - Indexes help WHERE, JOIN, ORDER BY, and uniqueness constraints when designed correctly.
     * - Use explain plans and real query stats, not guesses.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Query:
     * // WHERE customer_id = ? AND created_at >= ? ORDER BY created_at DESC
     * // Candidate index: (customer_id, created_at DESC)
     *
     * REAL BACKEND / BANKING USE CASE
     * A transfer lookup by customerId and createdAt needs an index that matches the query filter and sort pattern.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not add indexes blindly for every column.
     * - Do not ignore write overhead.
     * - Do not diagnose slow APIs without checking query plans.
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
        return "Indexes speed up reads by letting the database find rows without scanning everything, but they cost storage and slow writes because indexes must be maintained.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Composite index column order matters.",
            "Low-cardinality columns alone may not be selective enough.",
            "Indexes help WHERE, JOIN, ORDER BY, and uniqueness constraints when designed correctly.",
            "Use explain plans and real query stats, not guesses."
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

    public String realBackendUseCase() {
        return "A transfer lookup by customerId and createdAt needs an index that matches the query filter and sort pattern.";
    }

    public String commonMistake() {
        return "Do not add indexes blindly for every column.";
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
        DatabaseIndexingForJavaServices notes = new DatabaseIndexingForJavaServices();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
