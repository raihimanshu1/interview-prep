package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class MachineCodingJavaBackend {

    /*
     * QUESTION 120: What do senior Java machine-coding rounds evaluate?
     *
     * SHORT ANSWER
     * Machine-coding rounds evaluate clean object design, correctness, extensibility, concurrency awareness, tests, and the ability to explain trade-offs while coding under time pressure.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Start by clarifying requirements and constraints.
     * - Model domain objects before writing framework code.
     * - Keep code testable without a running server.
     * - Mention concurrency and persistence assumptions explicitly.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Clarify requirements, scale, consistency, latency, operational ownership, and rollback before choosing tools.
     * - Discuss failure modes: overload, stale data, duplicate processing, dependency outage, deployment regression, and observability gaps.
     * - Tie design to concrete controls: rate limits, probes, tracing, flags, canaries, cache TTLs, and audit trails.
     * - Production answer: optimize for correctness and operability first, then throughput.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Good approach:
     * // requirements -> domain model -> service API -> in-memory repository -> tests -> edge cases -> extension points.
     * // Use simple Java first; add Spring only if the problem asks for it.
     *
     * REAL BACKEND / BANKING USE CASE
     * A parking-lot, rate-limiter, split-expense, or order-management problem should be solved with small cohesive classes and clear extension points.
     *
     * SENIOR-SDE CHECKLIST
     * - Clarify scale, consistency, latency, security, compliance, and operability before choosing tools.
     * - Design for overload, dependency failure, stale data, duplicate requests, bad deploys, and rollback.
     * - Add observability and controls: metrics, traces, logs, probes, feature flags, rate limits, canaries, and audit trails.
     * - Verify with failure drills, load tests, deployment tests, and runbook-quality monitoring.
     *
     * COMMON MISTAKES
     * - Do not jump into controllers before domain logic.
     * - Do not over-engineer with unnecessary frameworks.
     * - Do not skip tests for edge cases.
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
        return "Machine-coding rounds evaluate clean object design, correctness, extensibility, concurrency awareness, tests, and the ability to explain trade-offs while coding under time pressure.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Start by clarifying requirements and constraints.",
            "Model domain objects before writing framework code.",
            "Keep code testable without a running server.",
            "Mention concurrency and persistence assumptions explicitly."
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
        return "A parking-lot, rate-limiter, split-expense, or order-management problem should be solved with small cohesive classes and clear extension points.";
    }

    public String commonMistake() {
        return "Do not jump into controllers before domain logic.";
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
        MachineCodingJavaBackend notes = new MachineCodingJavaBackend();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
