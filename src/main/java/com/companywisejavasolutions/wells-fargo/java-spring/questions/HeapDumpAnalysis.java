package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class HeapDumpAnalysis {

    /*
     * QUESTION 17: How do you read and analyze a heap dump?
     *
     * SHORT ANSWER
     * Heap dump analysis identifies what objects consume memory, who retains them, and whether growth is expected cache usage or an actual leak.
     *
     * IN-DEPTH ANSWER
     * - Capture dump safely because it can contain sensitive data.
     * - Start with dominator tree and retained size.
     * - Inspect GC roots retaining large object graphs.
     * - Compare dumps over time for growth patterns.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
     * - Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
     * - Explain tuning trade-offs and why blindly changing flags or heap size is risky.
     * - Production answer: optimize based on workload evidence and SLO impact.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Analysis checklist:
     * // 1. Top retained objects.
     * // 2. Path to GC roots.
     * // 3. Static maps, ThreadLocals, listeners, caches.
     * // 4. Confirm with allocation/GC metrics.
     *
     * REAL BACKEND / BANKING USE CASE
     * A ThreadLocal or static cache can retain request data and show up as a large retained-size path.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the JVM/runtime mechanism behind the concept, not only the Java syntax.
     * - Use evidence: GC logs, heap dumps, JFR, allocation profiles, class histograms, or thread dumps.
     * - Connect the topic to SLO impact: latency, throughput, memory footprint, startup, or deployment stability.
     * - Tune only after measuring under production-like workload.
     *
     * COMMON MISTAKES
     * - Do not share heap dumps casually; they may contain secrets.
     * - Do not confuse high allocation rate with retained leak.
     * - Do not stop at shallow size; retained size is usually more useful.
     *
     * INTERVIEW TIPS
     * - Give the one-line definition first so the interviewer knows you are grounded.
     * - Immediately connect it to a banking/backend example.
     * - Name the trap, such as race conditions, hidden coupling, inconsistent data, or throughput loss.
     * - Explain the trade-off and the production guardrail you would put in place.
     *
     * FOLLOW-UP QUESTIONS TO BE READY FOR
     * - How does this behave under concurrent requests?
     * - What happens when a downstream service or database operation fails?
     * - How would you test this and prove it works in production?
     *
     * SOURCES
     * - https://docs.oracle.com/en/java/javase/17/docs/api/
     */

    public String shortAnswer() {
        return "Heap dump analysis identifies what objects consume memory, who retains them, and whether growth is expected cache usage or an actual leak.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Capture dump safely because it can contain sensitive data.",
            "Start with dominator tree and retained size.",
            "Inspect GC roots retaining large object graphs.",
            "Compare dumps over time for growth patterns."
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

    public String commonMistake() {
        return "Do not share heap dumps casually; they may contain secrets.";
    }

    public boolean isProductionReadyExample(String answer) {
        String normalized = Objects.requireNonNullElse(answer, "").toLowerCase();
        return normalized.contains("trade-off")
                || normalized.contains("failure")
                || normalized.contains("transaction")
                || normalized.contains("concurrent")
                || normalized.contains("test");
    }

    public BigDecimal positiveAmount(BigDecimal amount) {
        BigDecimal value = Objects.requireNonNull(amount, "amount must not be null");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return value;
    }

    public static void main(String[] args) {
        HeapDumpAnalysis notes = new HeapDumpAnalysis();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
