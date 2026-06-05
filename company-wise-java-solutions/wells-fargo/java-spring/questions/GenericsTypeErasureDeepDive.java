package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class GenericsTypeErasureDeepDive {

    /*
     * QUESTION 79: Explain Java generics type erasure and its production consequences.
     *
     * SHORT ANSWER
     * Java generics are mostly enforced at compile time; at runtime many type parameters are erased, which affects reflection, overloads, arrays, and serialization frameworks.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Erasure keeps backward compatibility with pre-generics Java bytecode.
     * - You cannot create new T() or generic arrays directly.
     * - Overloads that erase to the same signature are illegal.
     * - Frameworks use TypeReference, ParameterizedTypeReference, or reflection metadata to recover generic intent.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * ParameterizedTypeReference<List<TransferResponse>> type =
     *     new ParameterizedTypeReference<>() {};
     * // The anonymous subclass preserves generic type metadata for the framework.
     *
     * REAL BACKEND / BANKING USE CASE
     * A REST client deserializing List<TransferResponse> must provide type information because runtime List alone does not preserve the element type.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not expect List<String>.class to exist.
     * - Do not use raw types in production APIs.
     * - Do not ignore unchecked warnings without understanding the risk.
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
        return "Java generics are mostly enforced at compile time; at runtime many type parameters are erased, which affects reflection, overloads, arrays, and serialization frameworks.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Erasure keeps backward compatibility with pre-generics Java bytecode.",
            "You cannot create new T() or generic arrays directly.",
            "Overloads that erase to the same signature are illegal.",
            "Frameworks use TypeReference, ParameterizedTypeReference, or reflection metadata to recover generic intent."
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
        return "A REST client deserializing List<TransferResponse> must provide type information because runtime List alone does not preserve the element type.";
    }

    public String commonMistake() {
        return "Do not expect List<String>.class to exist.";
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
        GenericsTypeErasureDeepDive notes = new GenericsTypeErasureDeepDive();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
