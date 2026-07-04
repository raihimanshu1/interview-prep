
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class ScopedValuesVsThreadLocal {

    /*
     * QUESTION 74: Scoped values vs ThreadLocal in modern Java.
     *
     * SHORT ANSWER
     * Scoped values provide bounded, immutable context sharing for a dynamic execution scope, while ThreadLocal stores mutable per-thread data that can leak in thread pools or become expensive with virtual threads.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - ThreadLocal is useful but risky in application servers because threads are reused.
     * - Virtual threads make massive ThreadLocal usage more memory-visible because there can be many more threads.
     * - Scoped values are designed for clear lifetime and inheritance across structured tasks.
     * - Business data should still be passed explicitly when it is part of method semantics.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * try {
     *   REQUEST_ID.set(correlationId);
     *   service.handle(request);
     * } finally {
     *   REQUEST_ID.remove();
     * }
     * // With scoped values, the lifetime is tied to the lexical scope instead of manual cleanup.
     *
     * REAL BACKEND / BANKING USE CASE
     * A request correlation ID can be passed through a scoped value for logging and tracing without keeping mutable context on pooled worker threads.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not use ThreadLocal as hidden global state.
     * - Do not forget remove in pooled threads.
     * - Do not store large mutable objects as thread context.
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
        return "Scoped values provide bounded, immutable context sharing for a dynamic execution scope, while ThreadLocal stores mutable per-thread data that can leak in thread pools or become expensive with virtual threads.";
    }

    public List<String> keyPoints() {
        return List.of(
            "ThreadLocal is useful but risky in application servers because threads are reused.",
            "Virtual threads make massive ThreadLocal usage more memory-visible because there can be many more threads.",
            "Scoped values are designed for clear lifetime and inheritance across structured tasks.",
            "Business data should still be passed explicitly when it is part of method semantics."
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
        return "A request correlation ID can be passed through a scoped value for logging and tracing without keeping mutable context on pooled worker threads.";
    }

    public String commonMistake() {
        return "Do not use ThreadLocal as hidden global state.";
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
        ScopedValuesVsThreadLocal notes = new ScopedValuesVsThreadLocal();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
