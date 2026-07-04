
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class ForkJoinWorkStealing {

    /*
     * QUESTION 89: Explain ForkJoinPool and work stealing.
     *
     * SHORT ANSWER
     * ForkJoinPool is designed for recursive, CPU-bound tasks where idle workers steal work from busy workers to improve parallelism.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - ForkJoinTask splits work into smaller subtasks and joins results.
     * - Work stealing reduces idle time by letting workers take tasks from others.
     * - The common pool is shared by parallel streams and many CompletableFuture defaults.
     * - Blocking in the common pool can starve unrelated work.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * class SumTask extends RecursiveTask<Long> {
     *   protected Long compute() {
     *     // split large range, fork one side, compute the other, then join
     *     return 0L;
     *   }
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * It can help with splitting a large in-memory risk calculation, but it is a poor fit for blocking database calls.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not use ForkJoinPool for blocking I/O by default.
     * - Do not block common-pool workers with long waits.
     * - Do not assume parallel streams have isolated capacity.
     *
     * INTERVIEW FOLLOW-UPS
     * - What breaks under high concurrency or partial failure?
     * - How would you verify this with tests, metrics, logs, or traces?
     * - What trade-off would make you choose a different design?
     *
     * RESEARCH SOURCES USED FOR TOPIC SELECTION
     * - https://www.geeksforgeeks.org/java-multithreading-interview-questions-and-answers/
     * - https://www.geeksforgeeks.org/springboot/spring-boot-interview-questions-and-answers/
     * - https://www.geeksforgeeks.org/java/java-microservices-architecture-development-interview-questions/
     * - https://www.baeldung.com/java-concurrency-interview-questions
     * - https://www.baeldung.com/spring-boot-interview-questions
     */

    public String shortAnswer() {
        return "ForkJoinPool is designed for recursive, CPU-bound tasks where idle workers steal work from busy workers to improve parallelism.";
    }

    public List<String> keyPoints() {
        return List.of(
            "ForkJoinTask splits work into smaller subtasks and joins results.",
            "Work stealing reduces idle time by letting workers take tasks from others.",
            "The common pool is shared by parallel streams and many CompletableFuture defaults.",
            "Blocking in the common pool can starve unrelated work."
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

    public String realBackendUseCase() {
        return "It can help with splitting a large in-memory risk calculation, but it is a poor fit for blocking database calls.";
    }

    public String commonMistake() {
        return "Do not use ForkJoinPool for blocking I/O by default.";
    }

    public boolean isSeniorLevelAnswer(String answer) {
        String normalized = Objects.requireNonNullElse(answer, "").toLowerCase();
        return normalized.contains("trade-off")
                || normalized.contains("failure")
                || normalized.contains("concurrency")
                || normalized.contains("monitor")
                || normalized.contains("test")
                || normalized.contains("rollback");
    }

    public BigDecimal requirePositiveAmount(BigDecimal amount) {
        BigDecimal value = Objects.requireNonNull(amount, "amount must not be null");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return value;
    }

    public Duration defaultTimeoutBudget() {
        return Duration.ofMillis(800);
    }

    public static void main(String[] args) {
        ForkJoinWorkStealing notes = new ForkJoinWorkStealing();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
