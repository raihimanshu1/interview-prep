
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class OopPrinciples {

    /*
     * QUESTION 1: Explain OOP principles with Java examples: encapsulation, inheritance, polymorphism, abstraction.
     *
     * SHORT ANSWER
     * OOP organizes code around cohesive objects: encapsulation protects state, abstraction exposes intent, inheritance reuses stable behavior, and polymorphism lets callers depend on contracts instead of concrete classes.
     *
     * IN-DEPTH ANSWER
     * - Encapsulation is not only private fields; it is validating state changes at the boundary so an Account cannot become invalid.
     * - Abstraction means callers use a small domain contract such as PaymentProcessor instead of knowing card, ACH, or wallet details.
     * - Inheritance is useful for true is-a relationships, but composition is safer for most service code because it avoids fragile base classes.
     * - Polymorphism removes if/else chains and lets new implementations be added with minimal changes to callers.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * interface PaymentMethod { Money authorize(Money amount); }
     * final class AchPayment implements PaymentMethod {
     *   public Money authorize(Money amount) {
     *     // Validate ACH-specific limits before moving money.
     *     return amount;
     *   }
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * A banking payment service can process card, ACH, and wire transfers through one PaymentMethod interface while each implementation owns its validation and posting behavior.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Do not expose mutable fields directly through getters.
     * - Do not use inheritance just to share two helper methods.
     * - Keep domain rules close to the object or service that owns the invariant.
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
        return "OOP organizes code around cohesive objects: encapsulation protects state, abstraction exposes intent, inheritance reuses stable behavior, and polymorphism lets callers depend on contracts instead of concrete classes.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Encapsulation is not only private fields; it is validating state changes at the boundary so an Account cannot become invalid.",
            "Abstraction means callers use a small domain contract such as PaymentProcessor instead of knowing card, ACH, or wallet details.",
            "Inheritance is useful for true is-a relationships, but composition is safer for most service code because it avoids fragile base classes.",
            "Polymorphism removes if/else chains and lets new implementations be added with minimal changes to callers."
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
        return "Do not expose mutable fields directly through getters.";
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
        OopPrinciples notes = new OopPrinciples();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
