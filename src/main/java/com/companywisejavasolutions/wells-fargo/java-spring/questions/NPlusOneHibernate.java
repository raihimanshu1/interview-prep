package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class NPlusOneHibernate {

    /*
     * QUESTION 103: What is the N+1 query problem in Hibernate/JPA?
     *
     * SHORT ANSWER
     * N+1 happens when one query loads parent rows and then one additional query is executed per parent to load related data, causing latency and database load.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Lazy loading is useful but can surprise API code.
     * - Fetch joins, entity graphs, batch fetching, and DTO projections can fix N+1 depending on the use case.
     * - Do not blindly eager-load everything because that can create huge joins.
     * - Integration tests should assert query counts for important flows.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain how lazy relationship traversal causes one parent query plus one query per row.
     * - Compare fetch join, entity graph, batch fetching, and DTO projection trade-offs.
     * - Mention eager loading can create cartesian explosions and is not a universal fix.
     * - Production answer: shape repository queries around use cases and assert query counts in integration tests.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Prefer query shaped for the use case:
     * // SELECT new AccountSummaryDto(a.id, a.name, count(t))
     * // FROM Account a LEFT JOIN a.transactions t GROUP BY a.id, a.name
     *
     * REAL BACKEND / BANKING USE CASE
     * Loading 100 accounts and lazily fetching transactions for each account can produce 101 queries.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the exact Spring runtime mechanism: proxy, filter chain, bean lifecycle, validation, transaction, or persistence context.
     * - Keep HTTP, business, persistence, and security responsibilities separated.
     * - Cover failure behavior: rollback, safe error response, authorization denial, validation failure, or lazy-loading failure.
     * - Verify with unit, slice, integration, contract, and security tests as appropriate.
     *
     * COMMON MISTAKES
     * - Do not return entities directly from controllers.
     * - Do not set every relationship to EAGER.
     * - Do not ignore SQL logs in performance testing.
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
        return "N+1 happens when one query loads parent rows and then one additional query is executed per parent to load related data, causing latency and database load.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Lazy loading is useful but can surprise API code.",
            "Fetch joins, entity graphs, batch fetching, and DTO projections can fix N+1 depending on the use case.",
            "Do not blindly eager-load everything because that can create huge joins.",
            "Integration tests should assert query counts for important flows."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain how lazy relationship traversal causes one parent query plus one query per row.",
            "Compare fetch join, entity graph, batch fetching, and DTO projection trade-offs.",
            "Mention eager loading can create cartesian explosions and is not a universal fix.",
            "Production answer: shape repository queries around use cases and assert query counts in integration tests."
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
        return "Loading 100 accounts and lazily fetching transactions for each account can produce 101 queries.";
    }

    public String commonMistake() {
        return "Do not return entities directly from controllers.";
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
        NPlusOneHibernate notes = new NPlusOneHibernate();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
