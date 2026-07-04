
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class JpaEntityLifecycle {

    /*
     * QUESTION 104: Explain JPA entity lifecycle states.
     *
     * SHORT ANSWER
     * JPA entities move through transient, managed, detached, and removed states, and behavior differs based on whether the persistence context is tracking the object.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Transient entities are new and not associated with persistence context.
     * - Managed entities are tracked and dirty-checked.
     * - Detached entities have identity but are no longer tracked.
     * - Removed entities are scheduled for deletion at flush/commit.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
     * - Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
     * - Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
     * - Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * Account account = entityManager.find(Account.class, id);
     * account.changeEmail(email);
     * // Managed entity is dirty-checked and updated on flush/commit.
     *
     * REAL BACKEND / BANKING USE CASE
     * A detached Account modified outside a transaction will not be saved unless merged or reloaded in a managed context.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the exact Spring runtime mechanism: proxy, filter chain, bean lifecycle, validation, transaction, or persistence context.
     * - Keep HTTP, business, persistence, and security responsibilities separated.
     * - Cover failure behavior: rollback, safe error response, authorization denial, validation failure, or lazy-loading failure.
     * - Verify with unit, slice, integration, contract, and security tests as appropriate.
     *
     * COMMON MISTAKES
     * - Do not confuse detached object changes with saved changes.
     * - Do not rely on merge without understanding what graph is copied.
     * - Do not keep long-lived persistence contexts in stateless services.
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
        return "JPA entities move through transient, managed, detached, and removed states, and behavior differs based on whether the persistence context is tracking the object.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Transient entities are new and not associated with persistence context.",
            "Managed entities are tracked and dirty-checked.",
            "Detached entities have identity but are no longer tracked.",
            "Removed entities are scheduled for deletion at flush/commit."
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
        return "A detached Account modified outside a transaction will not be saved unless merged or reloaded in a managed context.";
    }

    public String commonMistake() {
        return "Do not confuse detached object changes with saved changes.";
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
        JpaEntityLifecycle notes = new JpaEntityLifecycle();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
