
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class KafkaConsumerGroups {

    /*
     * QUESTION 94: How do Kafka consumer groups work?
     *
     * SHORT ANSWER
     * A Kafka consumer group lets multiple consumers share work by assigning partitions so each partition is consumed by at most one consumer in the group at a time.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Partitions define maximum parallelism inside a consumer group.
     * - Consumers in different groups each receive their own copy of messages.
     * - Rebalances move partition ownership when consumers join, leave, or fail.
     * - Offset commits define where processing resumes.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain partition assignment, rebalancing, offset commits, lag, and max poll interval.
     * - Discuss at-least-once processing and why idempotent consumers are normally required.
     * - Mention ordering only within a partition and how key choice affects correctness.
     * - Production answer: commit offsets after durable success and monitor lag/rebalance frequency.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Production pattern:
     * // poll records -> process safely -> commit offset after durable success.
     * // Keep processing idempotent because duplicates can still happen.
     *
     * REAL BACKEND / BANKING USE CASE
     * A payment notification service can scale consumers horizontally as long as the topic has enough partitions.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not expect more consumers than partitions to increase throughput.
     * - Do not commit offsets before durable processing.
     * - Do not ignore rebalance behavior.
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
        return "A Kafka consumer group lets multiple consumers share work by assigning partitions so each partition is consumed by at most one consumer in the group at a time.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Partitions define maximum parallelism inside a consumer group.",
            "Consumers in different groups each receive their own copy of messages.",
            "Rebalances move partition ownership when consumers join, leave, or fail.",
            "Offset commits define where processing resumes."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain partition assignment, rebalancing, offset commits, lag, and max poll interval.",
            "Discuss at-least-once processing and why idempotent consumers are normally required.",
            "Mention ordering only within a partition and how key choice affects correctness.",
            "Production answer: commit offsets after durable success and monitor lag/rebalance frequency."
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
        return "A payment notification service can scale consumers horizontally as long as the topic has enough partitions.";
    }

    public String commonMistake() {
        return "Do not expect more consumers than partitions to increase throughput.";
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
        KafkaConsumerGroups notes = new KafkaConsumerGroups();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
