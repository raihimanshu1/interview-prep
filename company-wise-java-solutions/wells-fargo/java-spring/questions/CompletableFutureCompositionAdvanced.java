package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class CompletableFutureCompositionAdvanced {

    /*
     * QUESTION 90: How do you compose CompletableFuture workflows safely?
     *
     * SHORT ANSWER
     * Compose CompletableFuture workflows by using thenCompose for dependent async steps, thenCombine for independent results, explicit executors, timeouts, and centralized exception handling.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - thenApply transforms a result synchronously; thenCompose flattens a future-returning step.
     * - thenCombine joins independent futures.
     * - Use explicit executors for blocking work.
     * - Always define timeout and exceptional behavior.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the shared-state or scheduling problem before naming a concurrency primitive.
     * - Explain Java Memory Model visibility, atomicity, ordering, contention, and cancellation where relevant.
     * - Discuss executor sizing, bounded queues, timeouts, rejection policy, and backpressure under load.
     * - Production answer: prove safety with concurrency tests, load tests, thread dumps, and metrics.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * CompletableFuture<FraudResult> fraud = CompletableFuture.supplyAsync(this::fraudCheck, executor);
     * CompletableFuture<LimitResult> limits = CompletableFuture.supplyAsync(this::limitCheck, executor);
     * return fraud.thenCombine(limits, Decision::from)
     *     .orTimeout(800, TimeUnit.MILLISECONDS)
     *     .exceptionally(Decision::failed);
     *
     * REAL BACKEND / BANKING USE CASE
     * A transfer pre-check can combine independent fraud and limits calls, then compose into the final authorization call only if both pass.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not use join everywhere and lose timeout control.
     * - Do not let async exceptions disappear.
     * - Do not run blocking work on the common pool without thinking.
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
        return "Compose CompletableFuture workflows by using thenCompose for dependent async steps, thenCombine for independent results, explicit executors, timeouts, and centralized exception handling.";
    }

    public List<String> keyPoints() {
        return List.of(
            "thenApply transforms a result synchronously; thenCompose flattens a future-returning step.",
            "thenCombine joins independent futures.",
            "Use explicit executors for blocking work.",
            "Always define timeout and exceptional behavior."
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
        return "A transfer pre-check can combine independent fraud and limits calls, then compose into the final authorization call only if both pass.";
    }

    public String commonMistake() {
        return "Do not use join everywhere and lose timeout control.";
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
        CompletableFutureCompositionAdvanced notes = new CompletableFutureCompositionAdvanced();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
