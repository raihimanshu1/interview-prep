
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AtomicClassesVsLocks {

    /*
     * QUESTION 41: Atomic classes vs locks.
     *
     * SHORT ANSWER
     * Atomic classes handle simple independent state changes with lock-free compare-and-set; locks handle compound operations that must protect multiple variables or larger critical sections.
     *
     * IN-DEPTH ANSWER
     * - Use AtomicInteger/AtomicLong for counters, flags, and simple CAS updates.
     * - Use locks when several reads and writes must be consistent together.
     * - Prefer database transactions for persistent financial state.
     * - Measure contention because both atomics and locks can become bottlenecks.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * AtomicLong successfulPayments = new AtomicLong();
     * successfulPayments.incrementAndGet();
     * // For multi-row money movement, use a database transaction instead of only an atomic counter.
     *
     * REAL BACKEND / BANKING USE CASE
     * A counter for accepted requests can use AtomicLong, but moving money between two accounts needs a transaction or lock around the whole invariant.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not use an atomic variable to protect several related fields.
     * - Do not hide business invariants inside scattered CAS loops.
     * - Do not assume lock-free always means faster under contention.
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
        return "Atomic classes handle simple independent state changes with lock-free compare-and-set; locks handle compound operations that must protect multiple variables or larger critical sections.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Use AtomicInteger/AtomicLong for counters, flags, and simple CAS updates.",
            "Use locks when several reads and writes must be consistent together.",
            "Prefer database transactions for persistent financial state.",
            "Measure contention because both atomics and locks can become bottlenecks."
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
        return "Do not use an atomic variable to protect several related fields.";
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
        AtomicClassesVsLocks notes = new AtomicClassesVsLocks();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
