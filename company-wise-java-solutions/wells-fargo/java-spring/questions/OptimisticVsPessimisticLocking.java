package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class OptimisticVsPessimisticLocking {

    /*
     * QUESTION 102: Optimistic vs pessimistic locking in JPA/database systems.
     *
     * SHORT ANSWER
     * Optimistic locking detects conflicts at commit using a version column; pessimistic locking prevents conflicts earlier by locking rows while the transaction runs.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Optimistic locking scales well when conflicts are rare.
     * - Pessimistic locking gives stronger immediate protection but can block and deadlock.
     * - Version columns allow safe retry logic.
     * - Lock choice depends on contention, invariant risk, and transaction duration.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain version columns and conflict detection for optimistic locking.
     * - Explain row locks, blocking, deadlock risk, and short critical sections for pessimistic locking.
     * - Tie choice to contention level and invariant risk.
     * - Production answer: retry optimistic conflicts carefully and never hold DB locks across remote calls.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Optimistic:
     * // @Version long version;
     * // On stale update, catch OptimisticLockException and retry or return conflict.
     * // Pessimistic: SELECT ... FOR UPDATE around short critical transaction.
     *
     * REAL BACKEND / BANKING USE CASE
     * Optimistic locking is good for rare account-profile conflicts, while pessimistic locking may be needed for high-risk balance updates.
     *
     * SENIOR-SDE CHECKLIST
     * - Identify shared mutable state, blocking points, cancellation behavior, and thread ownership.
     * - Choose the primitive deliberately: volatile, atomic, lock, transaction, executor, virtual thread, or queue.
     * - Protect capacity with bounded queues, timeouts, rejection policy, and backpressure.
     * - Verify with stress tests, load tests, thread dumps, JFR, and contention metrics.
     *
     * COMMON MISTAKES
     * - Do not hold pessimistic locks during remote calls.
     * - Do not ignore optimistic-lock retry policy.
     * - Do not use application-only locks across multiple service instances.
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
        return "Optimistic locking detects conflicts at commit using a version column; pessimistic locking prevents conflicts earlier by locking rows while the transaction runs.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Optimistic locking scales well when conflicts are rare.",
            "Pessimistic locking gives stronger immediate protection but can block and deadlock.",
            "Version columns allow safe retry logic.",
            "Lock choice depends on contention, invariant risk, and transaction duration."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain version columns and conflict detection for optimistic locking.",
            "Explain row locks, blocking, deadlock risk, and short critical sections for pessimistic locking.",
            "Tie choice to contention level and invariant risk.",
            "Production answer: retry optimistic conflicts carefully and never hold DB locks across remote calls."
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
        return "Optimistic locking is good for rare account-profile conflicts, while pessimistic locking may be needed for high-risk balance updates.";
    }

    public String commonMistake() {
        return "Do not hold pessimistic locks during remote calls.";
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
        OptimisticVsPessimisticLocking notes = new OptimisticVsPessimisticLocking();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
