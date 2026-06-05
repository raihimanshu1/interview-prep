package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class DebugProductionDeadlock {

    /*
     * QUESTION 35: How do you debug a production deadlock?
     *
     * SHORT ANSWER
     * Debug a production deadlock by capturing thread dumps, identifying blocked threads and owned locks, correlating with recent traffic/deployments, and applying a low-risk mitigation.
     *
     * IN-DEPTH ANSWER
     * - Take multiple thread dumps a few seconds apart.
     * - Look for BLOCKED threads and deadlock sections.
     * - Correlate thread names, endpoints, and lock owners.
     * - Mitigate carefully, then fix lock ordering or transaction design.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Operational flow:
     * // 1. Capture thread dump / JFR.
     * // 2. Identify lock cycle.
     * // 3. Reduce traffic or restart only if necessary.
     * // 4. Patch code to use consistent lock ordering/timeouts.
     *
     * REAL BACKEND / BANKING USE CASE
     * Use jstack or Java Flight Recorder to see which transfer threads hold account locks.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not restart before collecting evidence if the system is stable enough.
     * - Do not treat the symptom without fixing lock ordering.
     * - Do not ignore database locks; not every deadlock is a Java monitor deadlock.
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
     * - https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/package-summary.html
     */

    public String shortAnswer() {
        return "Debug a production deadlock by capturing thread dumps, identifying blocked threads and owned locks, correlating with recent traffic/deployments, and applying a low-risk mitigation.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Take multiple thread dumps a few seconds apart.",
            "Look for BLOCKED threads and deadlock sections.",
            "Correlate thread names, endpoints, and lock owners.",
            "Mitigate carefully, then fix lock ordering or transaction design."
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
        return "Do not restart before collecting evidence if the system is stable enough.";
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
        DebugProductionDeadlock notes = new DebugProductionDeadlock();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
