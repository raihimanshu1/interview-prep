
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class PreventCascadingFailures {

    /*
     * QUESTION 66: How do you prevent cascading failures?
     *
     * SHORT ANSWER
     * Prevent cascading failures with timeouts, bulkheads, circuit breakers, rate limits, backpressure, fallbacks, and clear dependency budgets.
     *
     * IN-DEPTH ANSWER
     * - Set timeouts shorter than caller deadlines.
     * - Use bulkheads so one dependency cannot consume every worker.
     * - Apply circuit breakers for repeated failures.
     * - Avoid retry storms with jitter and retry budgets.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Resilience policy:
     * // timeout 500ms, max 2 retries with jitter, circuit breaker on repeated failures, separate executor for fraud calls.
     *
     * REAL BACKEND / BANKING USE CASE
     * If a fraud service slows down, payment posting should fail fast or degrade according to policy instead of exhausting all request threads.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not let one slow dependency consume all threads.
     * - Do not retry indefinitely.
     * - Do not use fallbacks that hide financial correctness problems.
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
        return "Prevent cascading failures with timeouts, bulkheads, circuit breakers, rate limits, backpressure, fallbacks, and clear dependency budgets.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Set timeouts shorter than caller deadlines.",
            "Use bulkheads so one dependency cannot consume every worker.",
            "Apply circuit breakers for repeated failures.",
            "Avoid retry storms with jitter and retry budgets."
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
        return "Do not let one slow dependency consume all threads.";
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
        PreventCascadingFailures notes = new PreventCascadingFailures();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
