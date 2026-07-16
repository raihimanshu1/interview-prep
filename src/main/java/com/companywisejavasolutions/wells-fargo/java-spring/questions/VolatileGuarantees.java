package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class VolatileGuarantees {

    /*
     * QUESTION 32: What does volatile guarantee and what does it not guarantee?
     *
     * SHORT ANSWER
     * volatile guarantees visibility and ordering for a single variable read/write, but it does not make compound actions like increment atomic.
     *
     * IN-DEPTH ANSWER
     * - Use volatile for simple state flags.
     * - Do not use volatile for check-then-act invariants.
     * - Volatile does not protect multiple fields together.
     * - Atomic classes or locks are needed for compound updates.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain happens-before: a write to a volatile field is visible to later reads of that same field.
     * - Mention visibility and ordering, then clearly state that volatile does not make compound actions atomic.
     * - Connect to CPU cache visibility and instruction reordering without drowning the answer in hardware details.
     * - Production answer: use volatile for flags, AtomicLong for counters, locks/transactions for multi-field invariants.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * private volatile boolean shutdownRequested;
     * void stop() { shutdownRequested = true; }
     * boolean shouldStop() { return shutdownRequested; }
     *
     * REAL BACKEND / BANKING USE CASE
     * A shutdown flag can be volatile; a shared counter should use AtomicLong or locking.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not use volatile for count++.
     * - Do not expect volatile to protect multiple variables together.
     * - Do not ignore visibility when using non-volatile stop flags.
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
        return "volatile guarantees visibility and ordering for a single variable read/write, but it does not make compound actions like increment atomic.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Use volatile for simple state flags.",
            "Do not use volatile for check-then-act invariants.",
            "Volatile does not protect multiple fields together.",
            "Atomic classes or locks are needed for compound updates."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain happens-before: a write to a volatile field is visible to later reads of that same field.",
            "Mention visibility and ordering, then clearly state that volatile does not make compound actions atomic.",
            "Connect to CPU cache visibility and instruction reordering without drowning the answer in hardware details.",
            "Production answer: use volatile for flags, AtomicLong for counters, locks/transactions for multi-field invariants."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "unit tests for basic behavior",
            "stress/concurrency tests for races",
            "load tests for queueing and rejection",
            "thread dumps/JFR during failure drills"
        );
    }

    public String commonMistake() {
        return "Do not use volatile for count++.";
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
        VolatileGuarantees notes = new VolatileGuarantees();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
