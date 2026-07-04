
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class BoundedWildcardsPECS {

    /*
     * QUESTION 80: Explain PECS: producer extends, consumer super.
     *
     * SHORT ANSWER
     * PECS means use ? extends T when a generic source produces T values, and ? super T when a destination consumes T values.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - ? extends improves read flexibility but prevents adding most values.
     * - ? super allows adding a specific subtype but reads come back as Object unless cast.
     * - Use invariance carefully: List<PremiumAccount> is not a List<Account>.
     * - This is an API design topic, not only syntax.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * void copyFraudEvents(List<? extends FraudEvent> source, Collection<? super FraudEvent> target) {
     *   for (FraudEvent event : source) {
     *     target.add(event);
     *   }
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * A pricing method reading AccountEvent values can accept List<? extends AccountEvent>, while a method adding FraudEvent values can accept Collection<? super FraudEvent>.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not use wildcards everywhere by default.
     * - Do not confuse extends with inheritance-only thinking.
     * - Do not expose raw collections.
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
        return "PECS means use ? extends T when a generic source produces T values, and ? super T when a destination consumes T values.";
    }

    public List<String> keyPoints() {
        return List.of(
            "? extends improves read flexibility but prevents adding most values.",
            "? super allows adding a specific subtype but reads come back as Object unless cast.",
            "Use invariance carefully: List<PremiumAccount> is not a List<Account>.",
            "This is an API design topic, not only syntax."
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
        return "A pricing method reading AccountEvent values can accept List<? extends AccountEvent>, while a method adding FraudEvent values can accept Collection<? super FraudEvent>.";
    }

    public String commonMistake() {
        return "Do not use wildcards everywhere by default.";
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
        BoundedWildcardsPECS notes = new BoundedWildcardsPECS();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
