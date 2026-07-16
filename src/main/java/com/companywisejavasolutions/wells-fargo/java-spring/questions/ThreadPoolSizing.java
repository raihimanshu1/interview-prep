package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class ThreadPoolSizing {

    /*
     * QUESTION 88: How do you size thread pools for Java backend services?
     *
     * SHORT ANSWER
     * Thread pool size depends on CPU cores, blocking ratio, downstream limits, queue size, latency SLOs, and rejection policy, not on a magic fixed number.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - CPU-bound work should usually stay near core count.
     * - Blocking I/O pools can be larger but must respect downstream connection pools and rate limits.
     * - Queues should be bounded to expose overload.
     * - Rejection policy should be a product decision: fail fast, shed load, or apply backpressure.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * ThreadPoolExecutor pool = new ThreadPoolExecutor(
     *     16, 16, 0L, TimeUnit.MILLISECONDS,
     *     new ArrayBlockingQueue<>(500),
     *     new ThreadPoolExecutor.CallerRunsPolicy());
     *
     * REAL BACKEND / BANKING USE CASE
     * A fraud-check pool should be limited by the fraud service capacity and caller deadline, not simply set to 200 because traffic is high.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not use unbounded queues casually.
     * - Do not size threads larger than downstream capacity.
     * - Do not forget metrics for active threads, queue depth, and rejections.
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
        return "Thread pool size depends on CPU cores, blocking ratio, downstream limits, queue size, latency SLOs, and rejection policy, not on a magic fixed number.";
    }

    public List<String> keyPoints() {
        return List.of(
            "CPU-bound work should usually stay near core count.",
            "Blocking I/O pools can be larger but must respect downstream connection pools and rate limits.",
            "Queues should be bounded to expose overload.",
            "Rejection policy should be a product decision: fail fast, shed load, or apply backpressure."
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
        return "A fraud-check pool should be limited by the fraud service capacity and caller deadline, not simply set to 200 because traffic is high.";
    }

    public String commonMistake() {
        return "Do not use unbounded queues casually.";
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
        ThreadPoolSizing notes = new ThreadPoolSizing();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
