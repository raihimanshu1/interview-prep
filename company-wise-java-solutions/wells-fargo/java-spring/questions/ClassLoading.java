package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ClassLoading {

    /*
     * QUESTION 20: Explain class loading in Java.
     *
     * SHORT ANSWER
     * Java class loading loads bytecode, verifies it, links it, and initializes static state through a parent-delegation class loader model.
     *
     * IN-DEPTH ANSWER
     * - Know loading, linking, and initialization phases.
     * - Static initialization happens once per class loader.
     * - Class loader leaks happen when long-lived references retain old deployment classes.
     * - Avoid heavy work in static initializers.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Move from definition to runtime behavior: allocation, reachability, class metadata, JIT, GC pauses, or memory areas.
     * - Discuss measurement first: GC logs, heap dumps, JFR, allocation profiles, thread dumps, and latency percentiles.
     * - Explain tuning trade-offs and why blindly changing flags or heap size is risky.
     * - Production answer: optimize based on workload evidence and SLO impact.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * Class<?> type = Class.forName("java.lang.String");
     * // Class.forName loads and initializes by default.
     * return type.getClassLoader() == null ? "bootstrap" : type.getClassLoader().getName();
     *
     * REAL BACKEND / BANKING USE CASE
     * Spring Boot applications may use custom class loaders for executable jars, devtools reloads, or app servers.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the JVM/runtime mechanism behind the concept, not only the Java syntax.
     * - Use evidence: GC logs, heap dumps, JFR, allocation profiles, class histograms, or thread dumps.
     * - Connect the topic to SLO impact: latency, throughput, memory footprint, startup, or deployment stability.
     * - Tune only after measuring under production-like workload.
     *
     * COMMON MISTAKES
     * - Do not put expensive work in static initialization.
     * - Do not ignore class loader leaks in redeployable environments.
     * - Do not assume the same class loaded by two loaders is the same type.
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
        return "Java class loading loads bytecode, verifies it, links it, and initializes static state through a parent-delegation class loader model.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Know loading, linking, and initialization phases.",
            "Static initialization happens once per class loader.",
            "Class loader leaks happen when long-lived references retain old deployment classes.",
            "Avoid heavy work in static initializers."
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
        return "Do not put expensive work in static initialization.";
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
        ClassLoading notes = new ClassLoading();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
