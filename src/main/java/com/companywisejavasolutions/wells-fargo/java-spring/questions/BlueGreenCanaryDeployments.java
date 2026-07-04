
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class BlueGreenCanaryDeployments {

    /*
     * QUESTION 115: Blue-green vs canary deployments.
     *
     * SHORT ANSWER
     * Blue-green switches traffic between two full environments, while canary gradually sends a small percentage of traffic to a new version to reduce blast radius.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Blue-green gives fast rollback but requires duplicate capacity.
     * - Canary gives gradual risk exposure but needs good metrics and traffic control.
     * - Both require backward-compatible database and API changes.
     * - Rollback plans must consider messages and data migrations, not only code.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
     * - Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
     * - Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
     * - Production answer: optimize for correctness and operability first, then throughput.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Canary checklist:
     * // deploy v2 -> route small traffic -> compare SLOs -> increase gradually -> rollback on error/latency regression.
     *
     * REAL BACKEND / BANKING USE CASE
     * A payment API can canary a new fraud-integration version to 5 percent of traffic while monitoring error rate and latency.
     *
     * SENIOR-SDE CHECKLIST
     * - Clarify scale, consistency, latency, security, compliance, and operability before choosing tools.
     * - Design for overload, dependency failure, stale data, duplicate requests, bad deploys, and rollback.
     * - Add observability and controls: metrics, traces, logs, probes, feature flags, rate limits, canaries, and audit trails.
     * - Verify with failure drills, load tests, deployment tests, and runbook-quality monitoring.
     *
     * COMMON MISTAKES
     * - Do not deploy incompatible schema changes first.
     * - Do not canary without objective success metrics.
     * - Do not assume rollback undoes data changes.
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
        return "Blue-green switches traffic between two full environments, while canary gradually sends a small percentage of traffic to a new version to reduce blast radius.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Blue-green gives fast rollback but requires duplicate capacity.",
            "Canary gives gradual risk exposure but needs good metrics and traffic control.",
            "Both require backward-compatible database and API changes.",
            "Rollback plans must consider messages and data migrations, not only code."
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
        return "A payment API can canary a new fraud-integration version to 5 percent of traffic while monitoring error rate and latency.";
    }

    public String commonMistake() {
        return "Do not deploy incompatible schema changes first.";
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
        BlueGreenCanaryDeployments notes = new BlueGreenCanaryDeployments();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
