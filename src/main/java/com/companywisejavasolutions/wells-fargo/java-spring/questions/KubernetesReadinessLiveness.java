
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class KubernetesReadinessLiveness {

    /*
     * QUESTION 109: Readiness vs liveness probes for Spring Boot services.
     *
     * SHORT ANSWER
     * Readiness says whether a pod should receive traffic; liveness says whether the container should be restarted because it is stuck or unhealthy.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Readiness protects users by removing unready pods from load balancing.
     * - Liveness protects the platform by restarting stuck containers.
     * - Startup probes help slow-starting apps avoid premature restarts.
     * - Spring Boot Actuator health groups can expose different health views.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
     * - Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
     * - Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
     * - Production answer: optimize for correctness and operability first, then throughput.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Readiness: app initialized and can serve traffic.
     * // Liveness: JVM/process is not deadlocked or permanently broken.
     * // Keep dependency checks stricter in readiness than liveness.
     *
     * REAL BACKEND / BANKING USE CASE
     * A payment service should fail readiness when it cannot connect to required dependencies, but liveness should avoid restarting for every temporary downstream outage.
     *
     * SENIOR-SDE CHECKLIST
     * - Clarify scale, consistency, latency, security, compliance, and operability before choosing tools.
     * - Design for overload, dependency failure, stale data, duplicate requests, bad deploys, and rollback.
     * - Add observability and controls: metrics, traces, logs, probes, feature flags, rate limits, canaries, and audit trails.
     * - Verify with failure drills, load tests, deployment tests, and runbook-quality monitoring.
     *
     * COMMON MISTAKES
     * - Do not make liveness depend on every downstream service.
     * - Do not send traffic before warm-up is complete.
     * - Do not hide real dependency failures from readiness.
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
        return "Readiness says whether a pod should receive traffic; liveness says whether the container should be restarted because it is stuck or unhealthy.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Readiness protects users by removing unready pods from load balancing.",
            "Liveness protects the platform by restarting stuck containers.",
            "Startup probes help slow-starting apps avoid premature restarts.",
            "Spring Boot Actuator health groups can expose different health views."
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
        return "A payment service should fail readiness when it cannot connect to required dependencies, but liveness should avoid restarting for every temporary downstream outage.";
    }

    public String commonMistake() {
        return "Do not make liveness depend on every downstream service.";
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
        KubernetesReadinessLiveness notes = new KubernetesReadinessLiveness();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
