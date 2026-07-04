
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class GarbageCollectorsG1ZGC {

    /*
     * QUESTION 83: Compare G1 GC and ZGC for senior Java backend systems.
     *
     * SHORT ANSWER
     * G1 is a balanced default collector for many services, while ZGC targets very low pause times for large heaps and latency-sensitive workloads, usually with different memory/throughput trade-offs.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - G1 divides heap into regions and tries to meet pause-time goals.
     * - ZGC performs most work concurrently to keep pauses very short.
     * - Collector choice depends on latency SLO, heap size, allocation rate, CPU budget, and JDK version.
     * - Senior engineers should verify with GC logs and production-like load tests.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
     * - Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
     * - Explain tuning trade-offs and why blindly changing flags or heap size is risky.
     * - Production answer: optimize based on workload evidence and SLO impact.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Example tuning conversation, not a universal prescription:
     * // -XX:+UseG1GC -XX:MaxGCPauseMillis=200
     * // or evaluate ZGC for large low-latency heaps after measuring allocation behavior.
     *
     * REAL BACKEND / BANKING USE CASE
     * A normal Spring Boot service may run well on G1; a high-throughput low-latency trading or risk service with a large heap may evaluate ZGC.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the JVM/runtime mechanism behind the concept, not only the Java syntax.
     * - Use evidence: GC logs, heap dumps, JFR, allocation profiles, class histograms, or thread dumps.
     * - Connect the topic to SLO impact: latency, throughput, memory footprint, startup, or deployment stability.
     * - Tune only after measuring under production-like workload.
     *
     * COMMON MISTAKES
     * - Do not choose a collector by trend alone.
     * - Do not tune GC before understanding allocation rate and pause SLO.
     * - Do not ignore CPU and memory overhead.
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
        return "G1 is a balanced default collector for many services, while ZGC targets very low pause times for large heaps and latency-sensitive workloads, usually with different memory/throughput trade-offs.";
    }

    public List<String> keyPoints() {
        return List.of(
            "G1 divides heap into regions and tries to meet pause-time goals.",
            "ZGC performs most work concurrently to keep pauses very short.",
            "Collector choice depends on latency SLO, heap size, allocation rate, CPU budget, and JDK version.",
            "Senior engineers should verify with GC logs and production-like load tests."
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
        return "A normal Spring Boot service may run well on G1; a high-throughput low-latency trading or risk service with a large heap may evaluate ZGC.";
    }

    public String commonMistake() {
        return "Do not choose a collector by trend alone.";
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
        GarbageCollectorsG1ZGC notes = new GarbageCollectorsG1ZGC();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
