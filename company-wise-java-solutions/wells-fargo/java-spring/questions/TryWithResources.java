package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class TryWithResources {

    /*
     * QUESTION 15: Explain try-with-resources.
     *
     * SHORT ANSWER
     * try-with-resources automatically closes AutoCloseable resources in reverse creation order, even when exceptions occur.
     *
     * IN-DEPTH ANSWER
     * - Declare resources in the try header.
     * - Close order is reverse declaration order.
     * - Suppressed exceptions preserve close failures.
     * - Prefer it over finally cleanup for closeable resources.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * try (InputStream in = Files.newInputStream(path)) {
     *   return in.readAllBytes();
     * } // InputStream is closed automatically
     *
     * REAL BACKEND / BANKING USE CASE
     * Use it for files, streams, JDBC resources, and any resource that must be closed reliably.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not manually close resources in several duplicated finally blocks.
     * - Do not ignore suppressed exceptions during debugging.
     * - Do not forget that resources close in reverse order.
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
        return "try-with-resources automatically closes AutoCloseable resources in reverse creation order, even when exceptions occur.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Declare resources in the try header.",
            "Close order is reverse declaration order.",
            "Suppressed exceptions preserve close failures.",
            "Prefer it over finally cleanup for closeable resources."
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
        return "Do not manually close resources in several duplicated finally blocks.";
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
        TryWithResources notes = new TryWithResources();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
