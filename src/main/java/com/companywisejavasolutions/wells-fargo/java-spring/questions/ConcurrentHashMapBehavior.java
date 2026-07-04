
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ConcurrentHashMapBehavior {

    /*
     * QUESTION 40: Explain ConcurrentHashMap concurrency behavior.
     *
     * SHORT ANSWER
     * ConcurrentHashMap allows concurrent reads and bin-level updates without locking the entire map, but compound read-then-write logic still needs atomic map methods.
     *
     * IN-DEPTH ANSWER
     * - Reads are usually non-blocking.
     * - Iterators are weakly consistent, not fail-fast.
     * - Use compute, merge, and putIfAbsent for atomic compound operations.
     * - Do not use null keys or null values.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * ConcurrentMap<String, AtomicLong> counters = new ConcurrentHashMap<>();
     * counters.computeIfAbsent(accountId, ignored -> new AtomicLong()).incrementAndGet();
     * // computeIfAbsent avoids the race between checking and inserting.
     *
     * REAL BACKEND / BANKING USE CASE
     * Use computeIfAbsent for a cache entry instead of containsKey followed by put.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not use containsKey followed by put for atomic logic.
     * - Do not expect iteration to be a stable snapshot.
     * - Do not store null values.
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
        return "ConcurrentHashMap allows concurrent reads and bin-level updates without locking the entire map, but compound read-then-write logic still needs atomic map methods.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Reads are usually non-blocking.",
            "Iterators are weakly consistent, not fail-fast.",
            "Use compute, merge, and putIfAbsent for atomic compound operations.",
            "Do not use null keys or null values."
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
        return "Do not use containsKey followed by put for atomic logic.";
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
        ConcurrentHashMapBehavior notes = new ConcurrentHashMapBehavior();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
