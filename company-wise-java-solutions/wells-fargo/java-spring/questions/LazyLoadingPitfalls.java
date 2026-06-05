package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class LazyLoadingPitfalls {

    /*
     * QUESTION 105: What are common lazy loading pitfalls in Spring Boot?
     *
     * SHORT ANSWER
     * Lazy loading can cause LazyInitializationException, N+1 queries, and accidental database access during serialization if entity relationships are accessed outside a transaction.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Lazy relationships are loaded when accessed inside an open persistence context.
     * - Open Session in View can hide boundaries but create unexpected queries in web rendering.
     * - DTO projections make API data needs explicit.
     * - Service methods should fetch exactly what the use case needs.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
     * - Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
     * - Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
     * - Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Better API shape:
     * // repository.findAccountSummary(id) returns DTO projection
     * // controller returns DTO, not JPA entity graph.
     *
     * REAL BACKEND / BANKING USE CASE
     * Returning a JPA Account entity from a REST controller may trigger lazy transaction loading during JSON serialization.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the exact Spring runtime mechanism: proxy, filter chain, bean lifecycle, validation, transaction, or persistence context.
     * - Keep HTTP, business, persistence, and security responsibilities separated.
     * - Cover failure behavior: rollback, safe error response, authorization denial, validation failure, or lazy-loading failure.
     * - Verify with unit, slice, integration, contract, and security tests as appropriate.
     *
     * COMMON MISTAKES
     * - Do not serialize JPA entities directly.
     * - Do not use Open Session in View as a design crutch.
     * - Do not fix lazy issues by making every relation eager.
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
        return "Lazy loading can cause LazyInitializationException, N+1 queries, and accidental database access during serialization if entity relationships are accessed outside a transaction.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Lazy relationships are loaded when accessed inside an open persistence context.",
            "Open Session in View can hide boundaries but create unexpected queries in web rendering.",
            "DTO projections make API data needs explicit.",
            "Service methods should fetch exactly what the use case needs."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.",
            "Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.",
            "Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.",
            "Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "unit tests for business rules",
            "slice tests for MVC/security/persistence boundaries",
            "integration tests for transactions and database behavior",
            "contract tests for public APIs/events"
        );
    }

    public String realBackendUseCase() {
        return "Returning a JPA Account entity from a REST controller may trigger lazy transaction loading during JSON serialization.";
    }

    public String commonMistake() {
        return "Do not serialize JPA entities directly.";
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
        LazyLoadingPitfalls notes = new LazyLoadingPitfalls();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
