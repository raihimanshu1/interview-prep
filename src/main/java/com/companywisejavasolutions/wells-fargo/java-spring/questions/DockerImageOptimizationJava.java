
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class DockerImageOptimizationJava {

    /*
     * QUESTION 110: How do you optimize Docker images for Java applications?
     *
     * SHORT ANSWER
     * Optimize Java Docker images with small trusted base images, layered jars, non-root users, right JVM memory settings, and reproducible builds.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Container memory limits must be understood by the JVM.
     * - Use minimal images while keeping debugging/security needs in mind.
     * - Run as non-root and scan images for vulnerabilities.
     * - Layer dependencies separately from changing application code to speed builds.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
     * - Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
     * - Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
     * - Production answer: optimize for correctness and operability first, then throughput.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Typical production concerns:
     * // base image pinning, non-root user, layered jar, health endpoint, JVM container memory flags, image scanning.
     *
     * REAL BACKEND / BANKING USE CASE
     * A Spring Boot service can use layered jar extraction so dependency layers are reused between deployments.
     *
     * SENIOR-SDE CHECKLIST
     * - Clarify scale, consistency, latency, security, compliance, and operability before choosing tools.
     * - Design for overload, dependency failure, stale data, duplicate requests, bad deploys, and rollback.
     * - Add observability and controls: metrics, traces, logs, probes, feature flags, rate limits, canaries, and audit trails.
     * - Verify with failure drills, load tests, deployment tests, and runbook-quality monitoring.
     *
     * COMMON MISTAKES
     * - Do not run containers as root by default.
     * - Do not ship unnecessary build tools in runtime images.
     * - Do not ignore JVM heap sizing inside containers.
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
        return "Optimize Java Docker images with small trusted base images, layered jars, non-root users, right JVM memory settings, and reproducible builds.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Container memory limits must be understood by the JVM.",
            "Use minimal images while keeping debugging/security needs in mind.",
            "Run as non-root and scan images for vulnerabilities.",
            "Layer dependencies separately from changing application code to speed builds."
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
        return "A Spring Boot service can use layered jar extraction so dependency layers are reused between deployments.";
    }

    public String commonMistake() {
        return "Do not run containers as root by default.";
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
        DockerImageOptimizationJava notes = new DockerImageOptimizationJava();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
