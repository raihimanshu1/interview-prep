package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class SynchronizedVsReentrantLock {

    /*
     * QUESTION 33: synchronized vs ReentrantLock.
     *
     * SHORT ANSWER
     * synchronized is JVM-managed mutual exclusion; ReentrantLock offers explicit lock/unlock plus features like tryLock, fairness, interruptible waits, and multiple conditions.
     *
     * IN-DEPTH ANSWER
     * - Always release ReentrantLock in finally.
     * - Keep critical sections small.
     * - Use consistent lock ordering.
     * - Do not lock around slow network calls.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * Lock lock = new ReentrantLock();
     * if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
     *   try { protected work } finally { lock.unlock(); }
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * Use synchronized for simple critical sections and ReentrantLock when timeout or fairness behavior is required.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not forget unlock in a finally block.
     * - Do not lock around slow I/O.
     * - Do not assume fairness is free.
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
        return "synchronized is JVM-managed mutual exclusion; ReentrantLock offers explicit lock/unlock plus features like tryLock, fairness, interruptible waits, and multiple conditions.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Always release ReentrantLock in finally.",
            "Keep critical sections small.",
            "Use consistent lock ordering.",
            "Do not lock around slow network calls."
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
        return "Do not forget unlock in a finally block.";
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
        SynchronizedVsReentrantLock notes = new SynchronizedVsReentrantLock();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
