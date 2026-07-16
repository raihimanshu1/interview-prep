package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class VirtualThreadsUseCases {

    /*
     * QUESTION 71: What are virtual threads in Java 21 and when should you use them?
     *
     * SHORT ANSWER
     * Virtual threads are lightweight JVM-managed threads designed for high-concurrency blocking I/O workloads, letting Java keep simple thread-per-request code without creating one expensive OS thread per request.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Platform threads map closely to OS threads and are expensive at high counts; virtual threads are scheduled by the JVM on a smaller set of carrier threads.
     * - They are best for blocking I/O, not for making CPU-bound work faster.
     * - They reduce the need for callback-heavy async code but do not remove the need for timeouts, backpressure, or connection-pool sizing.
     * - A senior answer should mention pinning, ThreadLocal cost, monitoring, and library compatibility.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain carrier threads, blocking unmount, and why virtual threads help high-concurrency blocking I/O.
     * - Mention they do not make CPU-bound work faster and do not remove connection-pool limits.
     * - Discuss pinning, ThreadLocal memory cost, diagnostics, and library compatibility.
     * - Production answer: migrate request-per-task blocking services carefully with load tests and downstream budgets.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
     *   Future<TransferStatus> status = executor.submit(() -> partnerClient.fetchStatus(referenceId));
     *   return status.get(800, TimeUnit.MILLISECONDS);
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * A payment reconciliation job can start many virtual-thread tasks for independent partner API calls, while still keeping each task written as straightforward blocking code.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not use virtual threads to bypass downstream limits.
     * - Do not expect CPU-bound work to speed up automatically.
     * - Do not ignore pinning and ThreadLocal-heavy libraries.
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
        return "Virtual threads are lightweight JVM-managed threads designed for high-concurrency blocking I/O workloads, letting Java keep simple thread-per-request code without creating one expensive OS thread per request.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Platform threads map closely to OS threads and are expensive at high counts; virtual threads are scheduled by the JVM on a smaller set of carrier threads.",
            "They are best for blocking I/O, not for making CPU-bound work faster.",
            "They reduce the need for callback-heavy async code but do not remove the need for timeouts, backpressure, or connection-pool sizing.",
            "A senior answer should mention pinning, ThreadLocal cost, monitoring, and library compatibility."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain carrier threads, blocking unmount, and why virtual threads help high-concurrency blocking I/O.",
            "Mention they do not make CPU-bound work faster and do not remove connection-pool limits.",
            "Discuss pinning, ThreadLocal memory cost, diagnostics, and library compatibility.",
            "Production answer: migrate request-per-task blocking services carefully with load tests and downstream budgets."
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
        return "A payment reconciliation job can start many virtual-thread tasks for independent partner API calls, while still keeping each task written as straightforward blocking code.";
    }

    public String commonMistake() {
        return "Do not use virtual threads to bypass downstream limits.";
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
        VirtualThreadsUseCases notes = new VirtualThreadsUseCases();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
