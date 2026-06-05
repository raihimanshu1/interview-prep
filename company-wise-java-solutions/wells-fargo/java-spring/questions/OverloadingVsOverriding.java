package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class OverloadingVsOverriding {

    /*
     * QUESTION 2: Difference between overloading and overriding.
     *
     * SHORT ANSWER
     * Overloading is compile-time method selection by parameter list; overriding is runtime dispatch where a subclass provides behavior for an inherited method contract.
     *
     * IN-DEPTH ANSWER
     * - Overloading improves readability only when the methods represent the same operation with different inputs.
     * - Overriding requires the same signature and respects visibility, return-type, and exception rules.
     * - Runtime polymorphism means a parent reference can call the child implementation.
     * - Use @Override so compiler catches accidental signature mistakes.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * class FeeCalculator { Money calculate(Account account) { return Money.ZERO; } }
     * final class PremiumFeeCalculator extends FeeCalculator {
     *   @Override Money calculate(Account account) { return Money.ZERO; }
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * A NotificationService may overload send methods for different request shapes, while EmailNotification overrides a common deliver contract.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not confuse static overload selection with dynamic overriding.
     * - Do not reduce method visibility when overriding.
     * - Avoid overloads that differ only by nullable parameters.
     *
     * INTERVIEW TIPS
     * - Give the one-line definition first so the interviewer knows you are grounded.
     * - Immediately connect it to a banking/backend example.
     * - Name the trap, such as race conditions, hidden coupling, inconsistent data, or throughput loss.
     * - Explain the trade-off and the production guardrail you would put in place.
     *
     * FOLLOW-UP QUESTIONS TO BE READY FOR
     * - How does this behave under concurrent requests?
     * - What happens when a downstream service or database operation fails?
     * - How would you test this and prove it works in production?
     *
     * SOURCES
     * - https://docs.oracle.com/en/java/javase/17/docs/api/
     */

    public String shortAnswer() {
        return "Overloading is compile-time method selection by parameter list; overriding is runtime dispatch where a subclass provides behavior for an inherited method contract.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Overloading improves readability only when the methods represent the same operation with different inputs.",
            "Overriding requires the same signature and respects visibility, return-type, and exception rules.",
            "Runtime polymorphism means a parent reference can call the child implementation.",
            "Use @Override so compiler catches accidental signature mistakes."
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

    public String commonMistake() {
        return "Do not confuse static overload selection with dynamic overriding.";
    }

    public boolean isProductionReadyExample(String answer) {
        String normalized = Objects.requireNonNullElse(answer, "").toLowerCase();
        return normalized.contains("trade-off")
                || normalized.contains("failure")
                || normalized.contains("transaction")
                || normalized.contains("concurrent")
                || normalized.contains("test");
    }

    public BigDecimal positiveAmount(BigDecimal amount) {
        BigDecimal value = Objects.requireNonNull(amount, "amount must not be null");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return value;
    }

    public static void main(String[] args) {
        OverloadingVsOverriding notes = new OverloadingVsOverriding();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
