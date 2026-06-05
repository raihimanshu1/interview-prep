package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class NioSelector {

    /*
     * QUESTION 93: Explain Java NIO Selector at a high level.
     *
     * SHORT ANSWER
     * A Selector lets one thread monitor multiple channels for readiness events, enabling scalable non-blocking network I/O.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Channels are registered with a selector for operations like accept, connect, read, and write.
     * - The selector blocks until one or more channels are ready.
     * - Application code must not block the selector/event-loop thread.
     * - Most backend developers use this through frameworks rather than writing selectors directly.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * Selector selector = Selector.open();
     * // register non-blocking channels with interest ops
     * // selector.select(); then process selected keys quickly
     *
     * REAL BACKEND / BANKING USE CASE
     * Frameworks like Netty use event-loop ideas so a gateway can manage many connections without one platform thread per socket.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not perform database or HTTP blocking work on event-loop threads.
     * - Do not busy-spin selector loops.
     * - Do not reinvent Netty/Reactor unless there is a strong reason.
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
        return "A Selector lets one thread monitor multiple channels for readiness events, enabling scalable non-blocking network I/O.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Channels are registered with a selector for operations like accept, connect, read, and write.",
            "The selector blocks until one or more channels are ready.",
            "Application code must not block the selector/event-loop thread.",
            "Most backend developers use this through frameworks rather than writing selectors directly."
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
        return "Frameworks like Netty use event-loop ideas so a gateway can manage many connections without one platform thread per socket.";
    }

    public String commonMistake() {
        return "Do not perform database or HTTP blocking work on event-loop threads.";
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
        NioSelector notes = new NioSelector();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
