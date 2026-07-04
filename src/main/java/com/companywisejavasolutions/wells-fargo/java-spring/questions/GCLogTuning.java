
package com.companywisejavasolutions.wellsFargo.javaSpring.questions;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class GCLogTuning {

    /*
     * QUESTION 84: How do you analyze GC logs and tune memory?
     *
     * SHORT ANSWER
     * GC analysis starts by reading allocation rate, pause time, heap occupancy, promotion behavior, and full-GC frequency, then tuning based on measured SLO impact.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Enable useful GC logging before an incident when possible.
     * - Look for pause duration, cause, reclaimed memory, and heap before/after.
     * - High allocation rate often points to code/object churn rather than heap-size-only issues.
     * - Tune only after deciding whether the problem is leak, allocation churn, heap size, or collector mismatch.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
     * - Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
     * - Explain tuning trade-offs and why blindly changing flags or heap size is risky.
     * - Production answer: optimize based on workload evidence and SLO impact.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Java 17+ example:
     * // -Xlog:gc*:file=gc.log:time,uptime,level,tags
     * // Then inspect p95/p99 pauses, allocation rate, full GC, and heap trend.
     *
     * REAL BACKEND / BANKING USE CASE
     * If a payments service has latency spikes every few minutes, GC logs can reveal old-gen pressure, humongous allocations, or promotion failures.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the JVM/runtime mechanism behind the concept, not only the Java syntax.
     * - Use evidence: GC logs, heap dumps, JFR, allocation profiles, class histograms, or thread dumps.
     * - Connect the topic to SLO impact: latency, throughput, memory footprint, startup, or deployment stability.
     * - Tune only after measuring under production-like workload.
     *
     * COMMON MISTAKES
     * - Do not blindly increase heap for every GC issue.
     * - Do not compare collectors without the same workload.
     * - Do not ignore object allocation hot spots.
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
        return "GC analysis starts by reading allocation rate, pause time, heap occupancy, promotion behavior, and full-GC frequency, then tuning based on measured SLO impact.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Enable useful GC logging before an incident when possible.",
            "Look for pause duration, cause, reclaimed memory, and heap before/after.",
            "High allocation rate often points to code/object churn rather than heap-size-only issues.",
            "Tune only after deciding whether the problem is leak, allocation churn, heap size, or collector mismatch."
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
        return "If a payments service has latency spikes every few minutes, GC logs can reveal old-gen pressure, humongous allocations, or promotion failures.";
    }

    public String commonMistake() {
        return "Do not blindly increase heap for every GC issue.";
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
        GCLogTuning notes = new GCLogTuning();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
