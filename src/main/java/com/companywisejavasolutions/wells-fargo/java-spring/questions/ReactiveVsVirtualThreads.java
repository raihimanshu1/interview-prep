package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class ReactiveVsVirtualThreads {

    /*
     * QUESTION 91: Reactive programming vs virtual threads in Spring applications.
     *
     * SHORT ANSWER
     * Reactive programming uses non-blocking streams and backpressure; virtual threads keep blocking code style while scaling many blocking tasks, so the right choice depends on workload, team skill, and ecosystem constraints.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Reactive is strong for streaming, backpressure, and non-blocking pipelines.
     * - Virtual threads are strong for request-per-task blocking code with simpler stack traces.
     * - Mixing both casually can add complexity without value.
     * - The database and HTTP client connection pools still limit throughput in both models.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Decision rule:
     * // streaming/backpressure-heavy pipeline -> reactive may fit.
     * // normal request/response blocking I/O -> virtual threads may fit.
     * // CPU-bound work -> neither is a magic speed-up.
     *
     * REAL BACKEND / BANKING USE CASE
     * A WebFlux streaming API with backpressure may stay reactive, while a traditional Spring MVC service with many blocking repository/client calls may benefit from virtual threads.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not treat virtual threads as a replacement for backpressure.
     * - Do not use reactive only because it sounds advanced.
     * - Do not ignore team maintainability.
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
        return "Reactive programming uses non-blocking streams and backpressure; virtual threads keep blocking code style while scaling many blocking tasks, so the right choice depends on workload, team skill, and ecosystem constraints.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Reactive is strong for streaming, backpressure, and non-blocking pipelines.",
            "Virtual threads are strong for request-per-task blocking code with simpler stack traces.",
            "Mixing both casually can add complexity without value.",
            "The database and HTTP client connection pools still limit throughput in both models."
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
        return "A WebFlux streaming API with backpressure may stay reactive, while a traditional Spring MVC service with many blocking repository/client calls may benefit from virtual threads.";
    }

    public String commonMistake() {
        return "Do not treat virtual threads as a replacement for backpressure.";
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
        ReactiveVsVirtualThreads notes = new ReactiveVsVirtualThreads();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
