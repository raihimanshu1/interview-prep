package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class DistributedTracingOpenTelemetry {

    /*
     * QUESTION 112: Explain distributed tracing with OpenTelemetry.
     *
     * SHORT ANSWER
     * Distributed tracing follows one request across services using trace IDs and spans, helping engineers identify latency, errors, and dependency paths in microservices.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Trace context must be propagated through HTTP, messaging, and async boundaries.
     * - Spans should have useful names and safe attributes.
     * - Sampling balances cost with diagnostic value.
     * - Tracing works best with metrics and structured logs.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
     * - Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
     * - Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
     * - Production answer: optimize for correctness and operability first, then throughput.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Production practice:
     * // propagate traceparent header, create spans around dependencies, log traceId, export via OTLP to tracing backend.
     *
     * REAL BACKEND / BANKING USE CASE
     * A payment request trace can show controller time, DB write time, fraud-service latency, Kafka publish time, and downstream errors.
     *
     * SENIOR-SDE CHECKLIST
     * - Clarify scale, consistency, latency, security, compliance, and operability before choosing tools.
     * - Design for overload, dependency failure, stale data, duplicate requests, bad deploys, and rollback.
     * - Add observability and controls: metrics, traces, logs, probes, feature flags, rate limits, canaries, and audit trails.
     * - Verify with failure drills, load tests, deployment tests, and runbook-quality monitoring.
     *
     * COMMON MISTAKES
     * - Do not add sensitive account data as span attributes.
     * - Do not trace only the first service.
     * - Do not rely on traces without metrics and logs.
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
        return "Distributed tracing follows one request across services using trace IDs and spans, helping engineers identify latency, errors, and dependency paths in microservices.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Trace context must be propagated through HTTP, messaging, and async boundaries.",
            "Spans should have useful names and safe attributes.",
            "Sampling balances cost with diagnostic value.",
            "Tracing works best with metrics and structured logs."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.",
            "Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.",
            "Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.",
            "Production answer: optimize for correctness and operability first, then throughput."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "failure-mode tests",
            "load and soak tests",
            "deployment rollback drills",
            "observability dashboard/alert validation"
        );
    }

    public String realBackendUseCase() {
        return "A payment request trace can show controller time, DB write time, fraud-service latency, Kafka publish time, and downstream errors.";
    }

    public String commonMistake() {
        return "Do not add sensitive account data as span attributes.";
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
        DistributedTracingOpenTelemetry notes = new DistributedTracingOpenTelemetry();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
