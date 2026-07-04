
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class VirtualThreadPinning {

    /*
     * QUESTION 72: What is virtual thread pinning and why does it matter?
     *
     * SHORT ANSWER
     * Pinning happens when a virtual thread cannot unmount from its carrier platform thread while blocked, reducing scalability because the carrier stays occupied.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Virtual threads normally unmount when they block on supported JDK operations.
     * - Pinning can happen around synchronized blocks or native/foreign calls.
     * - Short synchronized sections are usually fine; long blocking sections are the risk.
     * - Use JFR and runtime diagnostics to find pinning before declaring a migration successful.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Prefer avoiding monitor locks around blocking calls.
     * lock.lock();
     * try {
     *   updateInMemoryStateOnly();
     * } finally {
     *   lock.unlock();
     * }
     * // Perform slow network/database calls outside the critical section.
     *
     * REAL BACKEND / BANKING USE CASE
     * A synchronized block around a slow JDBC or HTTP call can pin carrier threads and reduce the benefit of virtual threads under high load.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not wrap remote calls inside synchronized methods.
     * - Do not assume every old library is virtual-thread friendly.
     * - Do not skip load testing with production-like blocking behavior.
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
        return "Pinning happens when a virtual thread cannot unmount from its carrier platform thread while blocked, reducing scalability because the carrier stays occupied.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Virtual threads normally unmount when they block on supported JDK operations.",
            "Pinning can happen around synchronized blocks or native/foreign calls.",
            "Short synchronized sections are usually fine; long blocking sections are the risk.",
            "Use JFR and runtime diagnostics to find pinning before declaring a migration successful."
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
        return "A synchronized block around a slow JDBC or HTTP call can pin carrier threads and reduce the benefit of virtual threads under high load.";
    }

    public String commonMistake() {
        return "Do not wrap remote calls inside synchronized methods.";
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
        VirtualThreadPinning notes = new VirtualThreadPinning();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
