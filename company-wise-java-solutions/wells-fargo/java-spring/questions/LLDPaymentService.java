package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class LLDPaymentService {

    /*
     * QUESTION 118: Low-level design a payment transfer service.
     *
     * SHORT ANSWER
     * A strong LLD answer identifies entities, commands, service boundaries, validation, idempotency, transaction handling, concurrency control, and error responses.
     *
     * BASICS TO ADVANCED EXPLANATION
     * - Model TransferCommand, Account, LedgerEntry, Transfer, and IdempotencyRecord.
     * - Put orchestration and transaction boundary in TransferService.
     * - Use repositories/ports for persistence and external dependencies.
     * - Test insufficient funds, duplicate key, concurrent transfers, and rollback behavior.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Identify domain entities, command objects, ports, repositories, transaction boundary, and idempotency record.
     * - Explain locking/versioning and ledger entry invariants.
     * - Include tests for insufficient funds, duplicate requests, concurrent transfers, and rollback.
     * - Production answer: service layer owns business invariant; controller only translates HTTP.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // TransferService.create(command):
     * // validate -> reserve/idempotency record -> lock/read accounts -> debit/credit -> ledger entries -> commit -> publish event via outbox.
     *
     * REAL BACKEND / BANKING USE CASE
     * For bank transfers, the core invariant is that debit, credit, and ledger entries must be consistent and duplicate requests must not post twice.
     *
     * SENIOR-SDE CHECKLIST
     * - Clarify scale, consistency, latency, security, compliance, and operability before choosing tools.
     * - Design for overload, dependency failure, stale data, duplicate requests, bad deploys, and rollback.
     * - Add observability and controls: metrics, traces, logs, probes, feature flags, rate limits, canaries, and audit trails.
     * - Verify with failure drills, load tests, deployment tests, and runbook-quality monitoring.
     *
     * COMMON MISTAKES
     * - Do not start with controllers and DTOs only.
     * - Do not ignore race conditions and idempotency.
     * - Do not put money movement behind weak in-memory state.
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
        return "A strong LLD answer identifies entities, commands, service boundaries, validation, idempotency, transaction handling, concurrency control, and error responses.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Model TransferCommand, Account, LedgerEntry, Transfer, and IdempotencyRecord.",
            "Put orchestration and transaction boundary in TransferService.",
            "Use repositories/ports for persistence and external dependencies.",
            "Test insufficient funds, duplicate key, concurrent transfers, and rollback behavior."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Identify domain entities, command objects, ports, repositories, transaction boundary, and idempotency record.",
            "Explain locking/versioning and ledger entry invariants.",
            "Include tests for insufficient funds, duplicate requests, concurrent transfers, and rollback.",
            "Production answer: service layer owns business invariant; controller only translates HTTP."
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

    public String realBackendUseCase() {
        return "For bank transfers, the core invariant is that debit, credit, and ledger entries must be consistent and duplicate requests must not post twice.";
    }

    public String commonMistake() {
        return "Do not start with controllers and DTOs only.";
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
        LLDPaymentService notes = new LLDPaymentService();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
