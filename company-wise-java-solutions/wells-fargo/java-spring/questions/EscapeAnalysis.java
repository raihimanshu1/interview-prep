package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class EscapeAnalysis {

    /*
     * QUESTION 86: What is escape analysis and why should senior Java developers care?
     *
     * SHORT ANSWER
     * Escape analysis lets the JVM determine whether an object is confined to a method or thread, enabling optimizations such as scalar replacement and lock elimination.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - An object escapes if it is returned, stored in a field, passed to unknown code, or otherwise visible outside its scope.
     * - Non-escaping objects may avoid heap allocation in optimized code.
     * - Synchronization on non-escaping objects can sometimes be eliminated.
     * - This is why allocation in Java is often cheaper than expected, but still must be measured.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
     * - Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
     * - Explain tuning trade-offs and why blindly changing flags or heap size is risky.
     * - Production answer: optimize based on workload evidence and SLO impact.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * BigDecimal calculateFee(BigDecimal amount) {
     *   Money local = new Money(amount);
     *   return local.fee();
     * }
     * // If local never escapes and code is hot, JVM may optimize aggressively.
     *
     * REAL BACKEND / BANKING USE CASE
     * A small Money calculation object created inside a tight loop may be optimized away if it does not escape the method.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the JVM/runtime mechanism behind the concept, not only the Java syntax.
     * - Use evidence: GC logs, heap dumps, JFR, allocation profiles, class histograms, or thread dumps.
     * - Connect the topic to SLO impact: latency, throughput, memory footprint, startup, or deployment stability.
     * - Tune only after measuring under production-like workload.
     *
     * COMMON MISTAKES
     * - Do not over-optimize by object-pooling normal short-lived objects.
     * - Do not assume escape analysis always applies.
     * - Do not benchmark without JIT-aware tooling.
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
        return "Escape analysis lets the JVM determine whether an object is confined to a method or thread, enabling optimizations such as scalar replacement and lock elimination.";
    }

    public List<String> keyPoints() {
        return List.of(
            "An object escapes if it is returned, stored in a field, passed to unknown code, or otherwise visible outside its scope.",
            "Non-escaping objects may avoid heap allocation in optimized code.",
            "Synchronization on non-escaping objects can sometimes be eliminated.",
            "This is why allocation in Java is often cheaper than expected, but still must be measured."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.",
            "Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.",
            "Explain tuning trade-offs and why blindly changing flags or heap size is risky.",
            "Production answer: optimize based on workload evidence and SLO impact."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "JFR or profiler capture",
            "GC log review under load",
            "heap/thread dump analysis when relevant",
            "before/after benchmark with production-like traffic"
        );
    }

    public String realBackendUseCase() {
        return "A small Money calculation object created inside a tight loop may be optimized away if it does not escape the method.";
    }

    public String commonMistake() {
        return "Do not over-optimize by object-pooling normal short-lived objects.";
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
        EscapeAnalysis notes = new EscapeAnalysis();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
