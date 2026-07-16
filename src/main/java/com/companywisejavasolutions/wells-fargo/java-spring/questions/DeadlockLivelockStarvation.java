package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class DeadlockLivelockStarvation {

    /*
     * QUESTION 34: Explain deadlock, livelock, and starvation.
     *
     * SHORT ANSWER
     * Deadlock means threads wait forever on each other, livelock means they keep reacting without progress, and starvation means a thread rarely gets CPU or lock access.
     *
     * IN-DEPTH ANSWER
     * - Acquire locks in a consistent global order.
     * - Use timeouts around lock acquisition when possible.
     * - Prefer fair locks only when starvation matters; fairness can reduce throughput.
     * - Monitor blocked threads and lock wait times.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * Lock first = lockFor(minAccountId);
     * Lock second = lockFor(maxAccountId);
     * first.lock();
     * try { second.lock(); try { transfer work } finally { second.unlock(); } }
     * finally { first.unlock(); }
     *
     * REAL BACKEND / BANKING USE CASE
     * Two transfer threads locking account A then B and B then A can deadlock.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not acquire the same pair of locks in different orders.
     * - Do not spin forever while trying to be polite to another thread.
     * - Do not ignore starvation when a lock is highly contended.
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
        return "Deadlock means threads wait forever on each other, livelock means they keep reacting without progress, and starvation means a thread rarely gets CPU or lock access.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Acquire locks in a consistent global order.",
            "Use timeouts around lock acquisition when possible.",
            "Prefer fair locks only when starvation matters; fairness can reduce throughput.",
            "Monitor blocked threads and lock wait times."
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
        return "Do not acquire the same pair of locks in different orders.";
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
        DeadlockLivelockStarvation notes = new DeadlockLivelockStarvation();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
