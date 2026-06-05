package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class KafkaExactlyOnce {

    /*
     * QUESTION 95: What does exactly-once mean in Kafka and what does it not mean?
     *
     * SHORT ANSWER
     * Kafka exactly-once semantics can make consume-process-produce pipelines atomic within Kafka transactions, but it does not automatically make external databases or side effects exactly once.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Idempotent producers prevent duplicate records from producer retries.
     * - Transactions can atomically write to output topics and commit consumed offsets.
     * - External systems still need their own idempotency or transactional integration.
     * - Senior answers should separate Kafka guarantees from end-to-end business guarantees.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Separate Kafka transactional guarantees from end-to-end business exactly-once semantics.
     * - Explain idempotent producers, transactional producers, and offset commits inside a Kafka transaction.
     * - Mention external databases need outbox, idempotency, or transactional integration.
     * - Production answer: exactly-once business processing is a design, not one Kafka config.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Kafka EOS helps with:
     * // input topic -> process -> output topic + offset commit.
     * // For database writes, use idempotent keys or transactional outbox.
     *
     * REAL BACKEND / BANKING USE CASE
     * A ledger update plus Kafka publish still needs idempotency, transactions, or an outbox pattern because the database side effect is outside Kafka.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not claim Kafka gives exactly-once for every side effect.
     * - Do not skip idempotency in consumers.
     * - Do not confuse at-least-once delivery with business duplicate prevention.
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
        return "Kafka exactly-once semantics can make consume-process-produce pipelines atomic within Kafka transactions, but it does not automatically make external databases or side effects exactly once.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Idempotent producers prevent duplicate records from producer retries.",
            "Transactions can atomically write to output topics and commit consumed offsets.",
            "External systems still need their own idempotency or transactional integration.",
            "Senior answers should separate Kafka guarantees from end-to-end business guarantees."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Separate Kafka transactional guarantees from end-to-end business exactly-once semantics.",
            "Explain idempotent producers, transactional producers, and offset commits inside a Kafka transaction.",
            "Mention external databases need outbox, idempotency, or transactional integration.",
            "Production answer: exactly-once business processing is a design, not one Kafka config."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "consumer idempotency tests",
            "retry and DLQ integration tests",
            "schema/contract tests",
            "replay tests with duplicate and out-of-order events"
        );
    }

    public String realBackendUseCase() {
        return "A ledger update plus Kafka publish still needs idempotency, transactions, or an outbox pattern because the database side effect is outside Kafka.";
    }

    public String commonMistake() {
        return "Do not claim Kafka gives exactly-once for every side effect.";
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
        KafkaExactlyOnce notes = new KafkaExactlyOnce();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
