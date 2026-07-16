package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ThreadSafeMethod {

    /*
     * QUESTION 42: How do you make a method thread-safe?
     *
     * SHORT ANSWER
     * A method is thread-safe when concurrent calls cannot corrupt shared state or observe inconsistent data; prefer statelessness, immutability, confinement, atomics, locks, or transactions.
     *
     * IN-DEPTH ANSWER
     * - Identify shared mutable state first.
     * - Prefer local variables and immutable objects.
     * - Use atomic classes for simple counters.
     * - Use locks or database transactions for compound invariants.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * public BigDecimal calculateFee(BigDecimal amount) {
     *   // amount and fee are local variables, so concurrent requests do not share them.
     *   BigDecimal fee = amount.multiply(new BigDecimal("0.02"));
     *   return fee.max(BigDecimal.ONE);
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * A singleton Spring service method is safe if it uses only local variables and thread-safe collaborators, but unsafe if it writes request data into fields.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not put per-request state in singleton fields.
     * - Do not assume local variables need synchronization.
     * - Do not use synchronized when a database transaction is the real consistency boundary.
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
        return "A method is thread-safe when concurrent calls cannot corrupt shared state or observe inconsistent data; prefer statelessness, immutability, confinement, atomics, locks, or transactions.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Identify shared mutable state first.",
            "Prefer local variables and immutable objects.",
            "Use atomic classes for simple counters.",
            "Use locks or database transactions for compound invariants."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Define the shared-state or scheduling problem before naming a concurrency primitive.",
            "Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.",
            "Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.",
            "Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics."
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
        return "Do not put per-request state in singleton fields.";
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
        ThreadSafeMethod notes = new ThreadSafeMethod();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
