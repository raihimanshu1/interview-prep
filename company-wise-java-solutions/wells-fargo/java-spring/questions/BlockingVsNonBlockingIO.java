package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class BlockingVsNonBlockingIO {

    /*
     * QUESTION 92: Blocking I/O vs non-blocking I/O in Java.
     *
     * SHORT ANSWER
     * Blocking I/O waits on a thread until data is ready; non-blocking I/O lets fewer threads manage many connections by reacting when channels are ready.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Blocking I/O is simpler and works well when concurrency is controlled.
     * - Non-blocking I/O reduces thread usage for many idle connections.
     * - Non-blocking code introduces callback/reactive complexity.
     * - The right answer depends on latency, connection count, backpressure, and team operability.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Blocking: one task waits for response.
     * // Non-blocking: selector/event loop is notified when channel can read/write.
     * // Both still need timeouts and resource limits.
     *
     * REAL BACKEND / BANKING USE CASE
     * A high-connection gateway may use non-blocking I/O, while a simple internal service may use blocking I/O with virtual threads or a bounded pool.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not use non-blocking I/O as a buzzword.
     * - Do not let blocking calls run on event-loop threads.
     * - Do not ignore timeout and cancellation behavior.
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
        return "Blocking I/O waits on a thread until data is ready; non-blocking I/O lets fewer threads manage many connections by reacting when channels are ready.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Blocking I/O is simpler and works well when concurrency is controlled.",
            "Non-blocking I/O reduces thread usage for many idle connections.",
            "Non-blocking code introduces callback/reactive complexity.",
            "The right answer depends on latency, connection count, backpressure, and team operability."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Define the concept, describe internal behavior, and explain the production consequence.",
            "State when to use it, when not to use it, and what trade-off is being accepted.",
            "Include failure handling, testing approach, and observability signal.",
            "Production answer: connect the topic to a real banking/backend scenario."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "unit tests",
            "integration tests",
            "failure-path tests",
            "observability checks"
        );
    }

    public String realBackendUseCase() {
        return "A high-connection gateway may use non-blocking I/O, while a simple internal service may use blocking I/O with virtual threads or a bounded pool.";
    }

    public String commonMistake() {
        return "Do not use non-blocking I/O as a buzzword.";
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
        BlockingVsNonBlockingIO notes = new BlockingVsNonBlockingIO();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
