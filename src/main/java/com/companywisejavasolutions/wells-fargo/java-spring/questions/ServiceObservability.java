package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ServiceObservability {

    /*
     * QUESTION 68: How do you make services observable?
     *
     * SHORT ANSWER
     * Observability means logs, metrics, traces, and health signals explain what the service is doing and why it is failing.
     *
     * IN-DEPTH ANSWER
     * - Log structured events with correlation IDs.
     * - Measure RED metrics: rate, errors, duration.
     * - Trace cross-service calls.
     * - Alert on symptoms users feel, not only CPU.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain RED metrics for request services and USE metrics for resources.
     * - Include correlation IDs, trace IDs, structured logs, metrics, distributed traces, dashboards, and SLO-based alerts.
     * - Discuss cardinality control so labels do not explode monitoring cost.
     * - Production answer: instrument user-visible symptoms first, then dependency and resource internals.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Every request should carry a correlation/trace id.
     * // Log business-safe identifiers, emit timers/counters, and propagate trace context to downstream calls.
     *
     * REAL BACKEND / BANKING USE CASE
     * A payment API should expose request latency, error rate, dependency timing, and trace IDs across ledger/fraud calls.
     *
     * SENIOR-SDE CHECKLIST
     * - Clarify scale, consistency, latency, security, compliance, and operability before choosing tools.
     * - Design for overload, dependency failure, stale data, duplicate requests, bad deploys, and rollback.
     * - Add observability and controls: metrics, traces, logs, probes, feature flags, rate limits, canaries, and audit trails.
     * - Verify with failure drills, load tests, deployment tests, and runbook-quality monitoring.
     *
     * COMMON MISTAKES
     * - Do not log sensitive customer data.
     * - Do not rely only on logs when metrics/traces are needed.
     * - Do not alert on noisy internals while missing user-visible failures.
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
     * - https://docs.spring.io/spring-framework/reference/
     * - https://docs.spring.io/spring-boot/reference/
     */

    public String shortAnswer() {
        return "Observability means logs, metrics, traces, and health signals explain what the service is doing and why it is failing.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Log structured events with correlation IDs.",
            "Measure RED metrics: rate, errors, duration.",
            "Trace cross-service calls.",
            "Alert on symptoms users feel, not only CPU."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain RED metrics for request services and USE metrics for resources.",
            "Include correlation IDs, trace IDs, structured logs, metrics, distributed traces, dashboards, and SLO-based alerts.",
            "Discuss cardinality control so labels do not explode monitoring cost.",
            "Production answer: instrument user-visible symptoms first, then dependency and resource internals."
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

    public String commonMistake() {
        return "Do not log sensitive customer data.";
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
        ServiceObservability notes = new ServiceObservability();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
