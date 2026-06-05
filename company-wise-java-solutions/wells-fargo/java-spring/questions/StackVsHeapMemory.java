package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class StackVsHeapMemory {

    /*
     * QUESTION 18: Explain stack memory vs heap memory.
     *
     * SHORT ANSWER
     * Stack memory stores per-thread call frames and local references; heap memory stores objects shared across the JVM and managed by garbage collection.
     *
     * IN-DEPTH ANSWER
     * - Local primitive variables are on the stack frame.
     * - Objects are generally on the heap even when references are local.
     * - StackOverflowError often means deep recursion.
     * - OutOfMemoryError often means heap pressure or leaks.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
     * - Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
     * - Explain tuning trade-offs and why blindly changing flags or heap size is risky.
     * - Production answer: optimize based on workload evidence and SLO impact.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * void process() {
     *   BigDecimal amount = BigDecimal.TEN; // local reference in stack frame
     *   // BigDecimal object itself is on the heap.
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * A request thread has its own stack, but singleton service objects and DTO instances live on the heap.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the JVM/runtime mechanism behind the concept, not only the Java syntax.
     * - Use evidence: GC logs, heap dumps, JFR, allocation profiles, class histograms, or thread dumps.
     * - Connect the topic to SLO impact: latency, throughput, memory footprint, startup, or deployment stability.
     * - Tune only after measuring under production-like workload.
     *
     * COMMON MISTAKES
     * - Do not say all local variables are heap objects.
     * - Do not ignore that threads have separate stacks but share heap.
     * - Do not diagnose memory issues without distinguishing stack overflow from heap pressure.
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
        return "Stack memory stores per-thread call frames and local references; heap memory stores objects shared across the JVM and managed by garbage collection.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Local primitive variables are on the stack frame.",
            "Objects are generally on the heap even when references are local.",
            "StackOverflowError often means deep recursion.",
            "OutOfMemoryError often means heap pressure or leaks."
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
        return "Do not say all local variables are heap objects.";
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
        StackVsHeapMemory notes = new StackVsHeapMemory();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
