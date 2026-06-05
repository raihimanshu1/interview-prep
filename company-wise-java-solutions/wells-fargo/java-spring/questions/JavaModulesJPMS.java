package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class JavaModulesJPMS {

    /*
     * QUESTION 82: What is JPMS and when does it matter?
     *
     * SHORT ANSWER
     * The Java Platform Module System defines explicit module boundaries, dependencies, and exported packages, helping large applications control encapsulation and reliable configuration.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - module-info.java declares required modules and exported packages.
     * - Strong encapsulation prevents accidental access to non-exported internals.
     * - JPMS is more common in libraries and large platforms than typical Spring Boot apps.
     * - Migration can be complex because reflection-heavy frameworks may need opens directives.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * module banking.payments {
     *   requires java.sql;
     *   exports com.bank.payments.api;
     *   opens com.bank.payments.internal.jpa to spring.core;
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * A large banking platform can use modules to separate public API packages from internal implementation packages.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not export every package.
     * - Do not ignore reflection requirements for frameworks.
     * - Do not introduce modules without a migration plan.
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
        return "The Java Platform Module System defines explicit module boundaries, dependencies, and exported packages, helping large applications control encapsulation and reliable configuration.";
    }

    public List<String> keyPoints() {
        return List.of(
            "module-info.java declares required modules and exported packages.",
            "Strong encapsulation prevents accidental access to non-exported internals.",
            "JPMS is more common in libraries and large platforms than typical Spring Boot apps.",
            "Migration can be complex because reflection-heavy frameworks may need opens directives."
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
        return "A large banking platform can use modules to separate public API packages from internal implementation packages.";
    }

    public String commonMistake() {
        return "Do not export every package.";
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
        JavaModulesJPMS notes = new JavaModulesJPMS();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
