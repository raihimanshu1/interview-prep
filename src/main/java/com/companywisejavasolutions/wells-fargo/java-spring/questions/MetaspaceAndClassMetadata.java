
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class MetaspaceAndClassMetadata {

    /*
     * QUESTION 87: What is Metaspace and how do classloader leaks happen?
     *
     * SHORT ANSWER
     * Metaspace stores class metadata outside the Java heap; classloader leaks happen when old classloaders remain reachable, retaining their classes and static state.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Metaspace replaced PermGen in Java 8.
     * - Class metadata is freed only when the defining classloader becomes unreachable.
     * - Static references, running threads, ThreadLocals, JDBC drivers, and listeners commonly retain classloaders.
     * - Monitor class count and Metaspace usage when diagnosing redeploy leaks.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
     * - Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
     * - Explain tuning trade-offs and why blindly changing flags or heap size is risky.
     * - Production answer: optimize based on workload evidence and SLO impact.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Cleanup lifecycle resources:
     * // stop executors, deregister drivers/listeners, clear ThreadLocals, close classloader-owned caches.
     *
     * REAL BACKEND / BANKING USE CASE
     * In app servers or plugin systems, redeploying without releasing threads, caches, or drivers can retain old application classloaders.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the JVM/runtime mechanism behind the concept, not only the Java syntax.
     * - Use evidence: GC logs, heap dumps, JFR, allocation profiles, class histograms, or thread dumps.
     * - Connect the topic to SLO impact: latency, throughput, memory footprint, startup, or deployment stability.
     * - Tune only after measuring under production-like workload.
     *
     * COMMON MISTAKES
     * - Do not assume Metaspace growth is a heap leak.
     * - Do not leave app-created threads running after undeploy.
     * - Do not store application classes in container-global caches.
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
        return "Metaspace stores class metadata outside the Java heap; classloader leaks happen when old classloaders remain reachable, retaining their classes and static state.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Metaspace replaced PermGen in Java 8.",
            "Class metadata is freed only when the defining classloader becomes unreachable.",
            "Static references, running threads, ThreadLocals, JDBC drivers, and listeners commonly retain classloaders.",
            "Monitor class count and Metaspace usage when diagnosing redeploy leaks."
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
        return "In app servers or plugin systems, redeploying without releasing threads, caches, or drivers can retain old application classloaders.";
    }

    public String commonMistake() {
        return "Do not assume Metaspace growth is a heap leak.";
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
        MetaspaceAndClassMetadata notes = new MetaspaceAndClassMetadata();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
