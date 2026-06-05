package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class SOLIDInSpringServices {

    /*
     * QUESTION 117: Explain SOLID principles in Spring service design.
     *
     * SHORT ANSWER
     * SOLID helps Spring services stay cohesive, replaceable, and testable by keeping responsibilities small, depending on abstractions, and extending behavior without modifying stable code.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Single Responsibility means one reason to change.
     * - Open/Closed often appears as strategies or handlers for new payment types.
     * - Liskov means implementations must honor the interface contract.
     * - Interface Segregation and Dependency Inversion keep services from depending on fat concrete classes.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
     * - Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
     * - Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
     * - Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * final class TransferService {
     *   private final FraudCheck fraudCheck;
     *   private final LedgerPort ledgerPort;
     *   // Service depends on use-case ports, not concrete infrastructure details.
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * A TransferService should orchestrate a transfer use case, while fraud checks, limits, ledger posting, and notifications are separate collaborators.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the exact Spring runtime mechanism: proxy, filter chain, bean lifecycle, validation, transaction, or persistence context.
     * - Keep HTTP, business, persistence, and security responsibilities separated.
     * - Cover failure behavior: rollback, safe error response, authorization denial, validation failure, or lazy-loading failure.
     * - Verify with unit, slice, integration, contract, and security tests as appropriate.
     *
     * COMMON MISTAKES
     * - Do not create one giant service with every dependency.
     * - Do not make interfaces for every class without a reason.
     * - Do not violate contracts in alternative implementations.
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
        return "SOLID helps Spring services stay cohesive, replaceable, and testable by keeping responsibilities small, depending on abstractions, and extending behavior without modifying stable code.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Single Responsibility means one reason to change.",
            "Open/Closed often appears as strategies or handlers for new payment types.",
            "Liskov means implementations must honor the interface contract.",
            "Interface Segregation and Dependency Inversion keep services from depending on fat concrete classes."
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
        return "A TransferService should orchestrate a transfer use case, while fraud checks, limits, ledger posting, and notifications are separate collaborators.";
    }

    public String commonMistake() {
        return "Do not create one giant service with every dependency.";
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
        SOLIDInSpringServices notes = new SOLIDInSpringServices();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
