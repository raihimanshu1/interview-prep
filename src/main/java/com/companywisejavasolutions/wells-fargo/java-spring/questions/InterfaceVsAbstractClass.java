
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class InterfaceVsAbstractClass {

    /*
     * QUESTION 3: Interface vs abstract class. When would you use each in Java 8+?
     *
     * SHORT ANSWER
     * Use an interface for a capability contract and an abstract class when implementations must share state, constructors, or protected template behavior.
     *
     * IN-DEPTH ANSWER
     * - Interfaces define what a type can do and support multiple inheritance of type.
     * - Abstract classes define partial implementation and shared protected behavior.
     * - Java 8 default methods help evolve interfaces, but they should stay small and compatibility-focused.
     * - In Spring services, interfaces are useful for ports; abstract classes are useful for skeletal domain algorithms.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * interface FraudCheck { boolean approve(Transaction tx); }
     * abstract class AuditedFraudCheck implements FraudCheck {
     *   public final boolean approve(Transaction tx) {
     *     return doApprove(tx);
     *   }
     *   protected abstract boolean doApprove(Transaction tx);
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * PaymentGateway can be an interface; AbstractRetryingGateway can share retry bookkeeping only if every gateway truly needs it.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not put mutable shared state in interface default methods.
     * - Prefer composition when sharing behavior is optional.
     * - Avoid exposing implementation details through abstract protected APIs.
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
        return "Use an interface for a capability contract and an abstract class when implementations must share state, constructors, or protected template behavior.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Interfaces define what a type can do and support multiple inheritance of type.",
            "Abstract classes define partial implementation and shared protected behavior.",
            "Java 8 default methods help evolve interfaces, but they should stay small and compatibility-focused.",
            "In Spring services, interfaces are useful for ports; abstract classes are useful for skeletal domain algorithms."
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
        return "Do not put mutable shared state in interface default methods.";
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
        InterfaceVsAbstractClass notes = new InterfaceVsAbstractClass();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
