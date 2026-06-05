package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class StructuredConcurrency {

    /*
     * QUESTION 73: Explain structured concurrency and how it improves Java async code.
     *
     * SHORT ANSWER
     * Structured concurrency treats related concurrent tasks as one scoped unit, making cancellation, failure handling, and joining easier to reason about than scattered futures.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - The parent scope owns child tasks and waits for them before leaving the scope.
     * - Failure policy becomes explicit: fail-fast, collect all results, or use first successful result.
     * - It improves observability because task lifetime matches the business operation.
     * - In Java, structured concurrency has been evolving as a preview/incubator API, so production usage depends on the JDK version and policy.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Conceptual shape:
     * // create scope -> fork related tasks -> join -> handle failure -> return combined result.
     * // The key design rule is that child tasks cannot outlive the request/use-case scope.
     *
     * REAL BACKEND / BANKING USE CASE
     * A loan decision service can run fraud, credit, and limits checks concurrently and cancel the remaining checks when one mandatory check fails.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not create background futures that outlive the request accidentally.
     * - Do not ignore cancellation when one child task fails.
     * - Do not hide business failure policy inside random CompletableFuture chains.
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
        return "Structured concurrency treats related concurrent tasks as one scoped unit, making cancellation, failure handling, and joining easier to reason about than scattered futures.";
    }

    public List<String> keyPoints() {
        return List.of(
            "The parent scope owns child tasks and waits for them before leaving the scope.",
            "Failure policy becomes explicit: fail-fast, collect all results, or use first successful result.",
            "It improves observability because task lifetime matches the business operation.",
            "In Java, structured concurrency has been evolving as a preview/incubator API, so production usage depends on the JDK version and policy."
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
        return "A loan decision service can run fraud, credit, and limits checks concurrently and cancel the remaining checks when one mandatory check fails.";
    }

    public String commonMistake() {
        return "Do not create background futures that outlive the request accidentally.";
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
        StructuredConcurrency notes = new StructuredConcurrency();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
