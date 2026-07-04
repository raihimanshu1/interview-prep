
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class RunnableCallableFutureCompletableFuture {

    /*
     * QUESTION 36: Difference between Runnable, Callable, Future, and CompletableFuture.
     *
     * SHORT ANSWER
     * Runnable performs work without a result, Callable returns a result or checked exception, Future represents pending completion, and CompletableFuture supports composing async stages.
     *
     * IN-DEPTH ANSWER
     * - Do not block common-pool threads with long blocking I/O.
     * - Prefer explicit executors for backend async work.
     * - Handle exceptional completion.
     * - Keep async composition readable and bounded.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> "approved", executor)
     *     .orTimeout(500, TimeUnit.MILLISECONDS)
     *     .exceptionally(ex -> "failed");
     *
     * REAL BACKEND / BANKING USE CASE
     * Use CompletableFuture to combine independent fraud and limits checks when latency matters and failure policy is clear.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not ignore Future.get timeouts.
     * - Do not use the common pool for heavy blocking I/O.
     * - Do not let async exceptions disappear silently.
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
        return "Runnable performs work without a result, Callable returns a result or checked exception, Future represents pending completion, and CompletableFuture supports composing async stages.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Do not block common-pool threads with long blocking I/O.",
            "Prefer explicit executors for backend async work.",
            "Handle exceptional completion.",
            "Keep async composition readable and bounded."
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
        return "Do not ignore Future.get timeouts.";
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
        RunnableCallableFutureCompletableFuture notes = new RunnableCallableFutureCompletableFuture();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
