package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class JitCompilation {

    /*
     * QUESTION 85: Explain JIT compilation and warm-up in Java.
     *
     * SHORT ANSWER
     * The JVM starts by interpreting bytecode and then JIT-compiles hot methods into optimized machine code, which means performance often improves after warm-up.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - HotSpot profiles running code and optimizes methods based on actual execution.
     * - Optimizations include inlining, escape analysis, lock elision, and dead-code elimination.
     * - Deoptimization can occur when runtime assumptions change.
     * - Benchmark with JMH instead of ad hoc loops because JIT can fool naive tests.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
     * - Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
     * - Explain tuning trade-offs and why blindly changing flags or heap size is risky.
     * - Production answer: optimize based on workload evidence and SLO impact.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Use JMH for microbenchmarks rather than System.nanoTime loops.
     * // Warm-up iterations let the JIT see hot paths before measurement.
     *
     * REAL BACKEND / BANKING USE CASE
     * A latency benchmark for a pricing service must include warm-up or it may measure cold startup rather than steady-state performance.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the JVM/runtime mechanism behind the concept, not only the Java syntax.
     * - Use evidence: GC logs, heap dumps, JFR, allocation profiles, class histograms, or thread dumps.
     * - Connect the topic to SLO impact: latency, throughput, memory footprint, startup, or deployment stability.
     * - Tune only after measuring under production-like workload.
     *
     * COMMON MISTAKES
     * - Do not trust one-off microbenchmarks.
     * - Do not ignore warm-up for latency-sensitive services.
     * - Do not assume source-level code shape always predicts machine-level performance.
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
        return "The JVM starts by interpreting bytecode and then JIT-compiles hot methods into optimized machine code, which means performance often improves after warm-up.";
    }

    public List<String> keyPoints() {
        return List.of(
            "HotSpot profiles running code and optimizes methods based on actual execution.",
            "Optimizations include inlining, escape analysis, lock elision, and dead-code elimination.",
            "Deoptimization can occur when runtime assumptions change.",
            "Benchmark with JMH instead of ad hoc loops because JIT can fool naive tests."
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
        return "A latency benchmark for a pricing service must include warm-up or it may measure cold startup rather than steady-state performance.";
    }

    public String commonMistake() {
        return "Do not trust one-off microbenchmarks.";
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
        JitCompilation notes = new JitCompilation();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
