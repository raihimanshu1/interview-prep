package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class DefensiveCopiesInImmutableObjects {

    /*
     * QUESTION 6: Why should immutable objects defensively copy mutable fields?
     *
     * SHORT ANSWER
     * Without copying, outside code can mutate a collection or date after construction and silently change your object.
     *
     * IN-DEPTH ANSWER
     * - Start with the simple definition, then explain the production consequence.
     * - Name the failure mode: race condition, data inconsistency, memory pressure, latency, or security exposure.
     * - Explain the trade-off instead of presenting one option as universally best.
     * - Close with how you would test or monitor it in a real service.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Start with language/library semantics, then connect to correctness, maintainability, and performance.
     * - Explain edge cases interviewers test: mutability, equality, erasure, ordering, nulls, and API contracts.
     * - Show when the feature improves design and when it makes code harder to read or maintain.
     * - Production answer: prefer simple, explicit code until the abstraction removes real complexity.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Production answer pattern:
     * // define concept -> give banking example -> name failure mode -> show guardrail -> mention test/monitoring.
     *
     * REAL BACKEND / BANKING USE CASE
     * In a banking backend, Defensive Copies In Immutable Objects matters because correctness, security, concurrency, and observability must survive real production traffic.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the language or collection contract and the edge cases interviewers usually test.
     * - Show how mutability, equality, ordering, generics, null handling, or readability affects production correctness.
     * - Choose APIs based on clear trade-offs, not habit or syntax preference.
     * - Verify with focused unit tests and realistic examples; benchmark only when performance is actually relevant.
     *
     * COMMON MISTAKES
     * - Unmodifiable views still reflect changes to the original collection unless you copy first.
     * - Do not skip the production failure mode.
     * - Do not ignore testing and observability.
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
        return "Without copying, outside code can mutate a collection or date after construction and silently change your object.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Start with the simple definition, then explain the production consequence.",
            "Name the failure mode: race condition, data inconsistency, memory pressure, latency, or security exposure.",
            "Explain the trade-off instead of presenting one option as universally best.",
            "Close with how you would test or monitor it in a real service."
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
        return "Unmodifiable views still reflect changes to the original collection unless you copy first.";
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
        DefensiveCopiesInImmutableObjects notes = new DefensiveCopiesInImmutableObjects();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
