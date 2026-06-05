package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class ApiRateLimiting {

    /*
     * QUESTION 111: How do you design API rate limiting?
     *
     * SHORT ANSWER
     * Rate limiting controls request volume per client, user, token, IP, or business key to protect capacity and prevent abuse.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Token bucket and leaky bucket are common algorithms.
     * - Limits can be enforced at gateway, service, or both.
     * - Distributed limits need shared state or approximate local limits.
     * - Responses should be clear, often HTTP 429 with retry-after guidance.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
     * - Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
     * - Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
     * - Production answer: optimize for correctness and operability first, then throughput.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Design dimensions:
     * // key = clientId/userId/IP; algorithm = token bucket; scope = endpoint class; response = 429 with retry-after.
     *
     * REAL BACKEND / BANKING USE CASE
     * A transfer API may allow stricter limits for create-transfer calls than for read-only status calls.
     *
     * SENIOR-SDE CHECKLIST
     * - Clarify scale, consistency, latency, security, compliance, and operability before choosing tools.
     * - Design for overload, dependency failure, stale data, duplicate requests, bad deploys, and rollback.
     * - Add observability and controls: metrics, traces, logs, probes, feature flags, rate limits, canaries, and audit trails.
     * - Verify with failure drills, load tests, deployment tests, and runbook-quality monitoring.
     *
     * COMMON MISTAKES
     * - Do not use one global limit for every endpoint.
     * - Do not forget distributed deployment behavior.
     * - Do not let rate limiting break trusted internal batch flows without policy.
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
        return "Rate limiting controls request volume per client, user, token, IP, or business key to protect capacity and prevent abuse.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Token bucket and leaky bucket are common algorithms.",
            "Limits can be enforced at gateway, service, or both.",
            "Distributed limits need shared state or approximate local limits.",
            "Responses should be clear, often HTTP 429 with retry-after guidance."
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
        return "A transfer API may allow stricter limits for create-transfer calls than for read-only status calls.";
    }

    public String commonMistake() {
        return "Do not use one global limit for every endpoint.";
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
        ApiRateLimiting notes = new ApiRateLimiting();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
