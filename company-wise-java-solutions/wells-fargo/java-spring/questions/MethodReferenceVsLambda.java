package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class MethodReferenceVsLambda {

    /*
     * QUESTION 26: Method reference vs lambda.
     *
     * SHORT ANSWER
     * A method reference is compact syntax for a lambda that only calls an existing method; a lambda is better when logic needs parameters, conditions, or clarity.
     *
     * IN-DEPTH ANSWER
     * - Use method references for direct delegation.
     * - Use lambdas when naming parameters improves readability.
     * - Do not sacrifice clarity for clever syntax.
     * - Both compile to functional-interface implementations.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
     * - Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
     * - Explain tuning trade-offs and why blindly changing flags or heap size is risky.
     * - Production answer: optimize based on workload evidence and SLO impact.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * List<String> ids = transactions.stream().map(Transaction::id).toList();
     * List<String> masked = transactions.stream().map(tx -> mask(tx.id())).toList();
     *
     * REAL BACKEND / BANKING USE CASE
     * Transaction::id is readable for simple mapping; tx -> mask(tx.id()) is clearer when extra logic is needed.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the JVM/runtime mechanism behind the concept, not only the Java syntax.
     * - Use evidence: GC logs, heap dumps, JFR, allocation profiles, class histograms, or thread dumps.
     * - Connect the topic to SLO impact: latency, throughput, memory footprint, startup, or deployment stability.
     * - Tune only after measuring under production-like workload.
     *
     * COMMON MISTAKES
     * - Do not use method references when a lambda would explain intent better.
     * - Do not hide checked exception handling awkwardly.
     * - Do not optimize syntax at the cost of readability.
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
        return "A method reference is compact syntax for a lambda that only calls an existing method; a lambda is better when logic needs parameters, conditions, or clarity.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Use method references for direct delegation.",
            "Use lambdas when naming parameters improves readability.",
            "Do not sacrifice clarity for clever syntax.",
            "Both compile to functional-interface implementations."
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
        return "Do not use method references when a lambda would explain intent better.";
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
        MethodReferenceVsLambda notes = new MethodReferenceVsLambda();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
