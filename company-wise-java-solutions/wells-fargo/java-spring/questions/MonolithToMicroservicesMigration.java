package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class MonolithToMicroservicesMigration {

    /*
     * QUESTION 70: How do you migrate from a monolith to microservices safely?
     *
     * SHORT ANSWER
     * Migrate safely by carving services around business capabilities, using strangler patterns, preserving data consistency, and moving one capability at a time with observability.
     *
     * IN-DEPTH ANSWER
     * - Start with domain boundaries and dependency mapping.
     * - Use strangler routing instead of a big-bang rewrite.
     * - Keep data ownership clear; avoid shared databases long term.
     * - Add metrics, tracing, and rollback paths before migration.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.
     * - Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.
     * - Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.
     * - Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Migration sequence:
     * // identify bounded context -> create API/event contract -> dual-run if needed -> shift traffic gradually -> retire old path.
     *
     * REAL BACKEND / BANKING USE CASE
     * Extract statement generation before core ledger posting because it has lower consistency risk.
     *
     * SENIOR-SDE CHECKLIST
     * - State delivery guarantee, ordering boundary, offset/ack timing, retry, and duplicate-handling strategy.
     * - Use durable state for cross-resource consistency: outbox, inbox, saga, idempotency key, or reconciliation.
     * - Define DLQ, replay, schema evolution, consumer lag, and poison-message handling.
     * - Verify with duplicate, retry, rebalance, replay, and partial-failure tests.
     *
     * COMMON MISTAKES
     * - Do not split by technical layers instead of business capabilities.
     * - Do not begin with the riskiest core transaction path.
     * - Do not share one database forever without an ownership plan.
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
        return "Migrate safely by carving services around business capabilities, using strangler patterns, preserving data consistency, and moving one capability at a time with observability.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Start with domain boundaries and dependency mapping.",
            "Use strangler routing instead of a big-bang rewrite.",
            "Keep data ownership clear; avoid shared databases long term.",
            "Add metrics, tracing, and rollback paths before migration."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain delivery guarantees, ordering boundaries, retries, idempotency, and replay behavior.",
            "Discuss durable state, offset/ack timing, DLQ policy, schema evolution, and consumer lag.",
            "Mention that distributed consistency needs outbox/inbox, saga, or reconciliation patterns.",
            "Production answer: assume duplicates and partial failure; design consumers to be idempotent and observable."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "consumer idempotency tests",
            "retry and DLQ integration tests",
            "schema/contract tests",
            "replay tests with duplicate and out-of-order events"
        );
    }

    public String commonMistake() {
        return "Do not split by technical layers instead of business capabilities.";
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
        MonolithToMicroservicesMigration notes = new MonolithToMicroservicesMigration();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
