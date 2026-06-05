package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class StringInterning {

    /*
     * QUESTION 78: Explain string interning and when it can hurt.
     *
     * SHORT ANSWER
     * String interning stores canonical string instances so equal strings can share one object, but manual interning can increase memory pressure and contention if used blindly.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - String literals are interned automatically by the JVM.
     * - String.intern returns a canonical instance from the string pool.
     * - Interning high-cardinality or unbounded data can retain too many strings.
     * - Use profiling before applying interning as a memory optimization.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * String normalized = currencyCode.toUpperCase(Locale.ROOT);
     * // Safe only for bounded sets like ISO currency codes.
     * String canonical = normalized.intern();
     *
     * REAL BACKEND / BANKING USE CASE
     * Interning may help repeated small reference data codes, but it is dangerous for high-cardinality customer IDs or transaction references.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not intern user-generated unbounded values.
     * - Do not compare strings with == except when identity is intentionally guaranteed.
     * - Do not optimize memory without measuring retained strings.
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
        return "String interning stores canonical string instances so equal strings can share one object, but manual interning can increase memory pressure and contention if used blindly.";
    }

    public List<String> keyPoints() {
        return List.of(
            "String literals are interned automatically by the JVM.",
            "String.intern returns a canonical instance from the string pool.",
            "Interning high-cardinality or unbounded data can retain too many strings.",
            "Use profiling before applying interning as a memory optimization."
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
        return "Interning may help repeated small reference data codes, but it is dangerous for high-cardinality customer IDs or transaction references.";
    }

    public String commonMistake() {
        return "Do not intern user-generated unbounded values.";
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
        StringInterning notes = new StringInterning();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
