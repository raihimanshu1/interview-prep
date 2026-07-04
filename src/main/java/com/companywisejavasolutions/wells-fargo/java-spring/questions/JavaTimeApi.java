
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class JavaTimeApi {

    /*
     * QUESTION 30: Explain date/time API changes after Java 8.
     *
     * SHORT ANSWER
     * Java 8 time API introduced immutable, clearer types such as Instant, LocalDate, LocalDateTime, ZonedDateTime, Duration, and Clock.
     *
     * IN-DEPTH ANSWER
     * - Avoid Date and Calendar in new code.
     * - Do not store LocalDateTime when an absolute moment is required.
     * - Keep timezone decisions explicit.
     * - Inject Clock instead of calling now directly in domain logic.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * Clock clock = Clock.systemUTC();
     * Instant postedAt = Instant.now(clock);
     * LocalDate businessDate = LocalDate.now(clock);
     *
     * REAL BACKEND / BANKING USE CASE
     * Use Instant for audit timestamps, LocalDate for business dates, and inject Clock for testability.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not use LocalDateTime for an absolute audit timestamp.
     * - Do not let system default timezone decide business rules silently.
     * - Do not make tests depend on the real current time.
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
        return "Java 8 time API introduced immutable, clearer types such as Instant, LocalDate, LocalDateTime, ZonedDateTime, Duration, and Clock.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Avoid Date and Calendar in new code.",
            "Do not store LocalDateTime when an absolute moment is required.",
            "Keep timezone decisions explicit.",
            "Inject Clock instead of calling now directly in domain logic."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Define the concept, describe internal behavior, and explain the production consequence.",
            "State when to use it, when not to use it, and what trade-off is being accepted.",
            "Include failure handling, testing approach, and observability signal.",
            "Production answer: connect the topic to a real banking/backend scenario."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "unit tests",
            "integration tests",
            "failure-path tests",
            "observability checks"
        );
    }

    public String commonMistake() {
        return "Do not use LocalDateTime for an absolute audit timestamp.";
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
        JavaTimeApi notes = new JavaTimeApi();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
