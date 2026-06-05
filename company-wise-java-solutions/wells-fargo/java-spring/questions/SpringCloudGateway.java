package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class SpringCloudGateway {

    /*
     * QUESTION 107: What does Spring Cloud Gateway do in microservices?
     *
     * SHORT ANSWER
     * Spring Cloud Gateway is an API gateway framework that routes requests and applies cross-cutting filters such as authentication, rate limiting, header propagation, and resilience policies.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Gateways centralize edge concerns but should not contain core business logic.
     * - Filters can rewrite paths, add headers, authenticate, rate limit, or call circuit breakers.
     * - Downstream services must still protect themselves; gateway security is not enough.
     * - Gateway metrics are critical because it becomes a high-traffic chokepoint.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
     * - Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
     * - Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
     * - Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Gateway responsibilities:
     * // route -> authenticate -> rate limit -> add trace headers -> forward -> record metrics.
     * // Business validation stays inside downstream services.
     *
     * REAL BACKEND / BANKING USE CASE
     * A bank gateway can route /payments to payment-service while adding correlation IDs and enforcing coarse-grained access policies.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not put business workflows in the gateway.
     * - Do not trust gateway-only authorization for internal services.
     * - Do not ignore gateway as a single choke point.
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
        return "Spring Cloud Gateway is an API gateway framework that routes requests and applies cross-cutting filters such as authentication, rate limiting, header propagation, and resilience policies.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Gateways centralize edge concerns but should not contain core business logic.",
            "Filters can rewrite paths, add headers, authenticate, rate limit, or call circuit breakers.",
            "Downstream services must still protect themselves; gateway security is not enough.",
            "Gateway metrics are critical because it becomes a high-traffic chokepoint."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.",
            "Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.",
            "Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.",
            "Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable."
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
        return "A bank gateway can route /payments to payment-service while adding correlation IDs and enforcing coarse-grained access policies.";
    }

    public String commonMistake() {
        return "Do not put business workflows in the gateway.";
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
        SpringCloudGateway notes = new SpringCloudGateway();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
