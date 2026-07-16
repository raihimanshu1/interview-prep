package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class FixedVsCachedThreadPool {

    /*
     * QUESTION 38: Fixed thread pool vs cached thread pool.
     *
     * SHORT ANSWER
     * A fixed thread pool limits concurrency with a fixed number of workers; a cached thread pool can grow aggressively and is risky for unbounded backend load.
     *
     * IN-DEPTH ANSWER
     * - Use fixed/bounded pools for predictable backend work.
     * - Avoid cached pools for request-driven production workloads unless carefully limited.
     * - Size pools based on CPU vs blocking behavior.
     * - Monitor queue depth, active threads, and rejection count.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * ThreadPoolExecutor pool = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(500));
     * // Add a rejection policy that fails fast or applies backpressure.
     *
     * REAL BACKEND / BANKING USE CASE
     * For payment posting, a bounded fixed pool protects the service from too many simultaneous downstream calls.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not use cached pools for unlimited request fan-out.
     * - Do not forget that an unbounded queue can hide overload until latency explodes.
     * - Do not size pools without considering blocking time.
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
        return "A fixed thread pool limits concurrency with a fixed number of workers; a cached thread pool can grow aggressively and is risky for unbounded backend load.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Use fixed/bounded pools for predictable backend work.",
            "Avoid cached pools for request-driven production workloads unless carefully limited.",
            "Size pools based on CPU vs blocking behavior.",
            "Monitor queue depth, active threads, and rejection count."
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
        return "Do not use cached pools for unlimited request fan-out.";
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
        FixedVsCachedThreadPool notes = new FixedVsCachedThreadPool();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
