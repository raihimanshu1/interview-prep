package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class ServiceDiscovery {

    /*
     * QUESTION 108: Explain service discovery in microservices.
     *
     * SHORT ANSWER
     * Service discovery lets clients or infrastructure find healthy service instances dynamically instead of hardcoding hostnames and ports.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Client-side discovery lets the app choose an instance.
     * - Server-side discovery uses a load balancer/proxy to route traffic.
     * - Health checks determine whether instances should receive traffic.
     * - Discovery does not replace timeouts, retries, or circuit breakers.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
     * - Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
     * - Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
     * - Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // In Kubernetes:
     * // service DNS name fraud-service.default.svc.cluster.local routes to ready pods.
     * // The app still sets connect/read timeouts and retry budgets.
     *
     * REAL BACKEND / BANKING USE CASE
     * A transfer service can discover available fraud-service instances through Kubernetes DNS, Eureka, Consul, or a load balancer.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not hardcode pod IPs.
     * - Do not send traffic to unhealthy instances.
     * - Do not treat discovery as resilience by itself.
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
        return "Service discovery lets clients or infrastructure find healthy service instances dynamically instead of hardcoding hostnames and ports.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Client-side discovery lets the app choose an instance.",
            "Server-side discovery uses a load balancer/proxy to route traffic.",
            "Health checks determine whether instances should receive traffic.",
            "Discovery does not replace timeouts, retries, or circuit breakers."
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
        return "A transfer service can discover available fraud-service instances through Kubernetes DNS, Eureka, Consul, or a load balancer.";
    }

    public String commonMistake() {
        return "Do not hardcode pod IPs.";
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
        ServiceDiscovery notes = new ServiceDiscovery();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
