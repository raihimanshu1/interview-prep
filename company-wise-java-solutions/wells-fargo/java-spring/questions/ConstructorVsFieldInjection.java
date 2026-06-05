package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ConstructorVsFieldInjection {

    /*
     * QUESTION 47: Constructor injection vs field injection.
     *
     * SHORT ANSWER
     * Constructor injection makes dependencies explicit, supports final fields, improves testability, and fails fast when required dependencies are missing.
     *
     * IN-DEPTH ANSWER
     * - Prefer constructor injection for required dependencies.
     * - Use field injection only in legacy code or framework-managed tests when unavoidable.
     * - Keep constructors small by reducing service responsibility if dependencies explode.
     * - Inject Clock for testable time-dependent logic.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Define the concept, describe internal behavior, and explain the production consequence.
     * - State when to use it, when not to use it, and what trade-off is being accepted.
     * - Include failure handling, testing approach, and observability signal.
     * - Production answer: connect the topic to a real banking/backend scenario.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * final class TransferService {
     *   private final LedgerRepository ledgerRepository;
     *   TransferService(LedgerRepository ledgerRepository) {
     *     this.ledgerRepository = Objects.requireNonNull(ledgerRepository);
     *   }
     * }
     *
     * REAL BACKEND / BANKING USE CASE
     * A TransferService should receive repositories, gateways, and clocks through its constructor.
     *
     * SENIOR-SDE CHECKLIST
     * - Define the concept clearly, then explain the production consequence.
     * - Name when to use it, when not to use it, and the trade-off.
     * - Add failure handling, test strategy, and observability.
     * - Use a banking/backend example so the answer sounds practical.
     *
     * COMMON MISTAKES
     * - Do not hide required dependencies as private autowired fields.
     * - Do not let one service grow until the constructor has too many responsibilities.
     * - Do not manually new infrastructure collaborators inside business services.
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
        return "Constructor injection makes dependencies explicit, supports final fields, improves testability, and fails fast when required dependencies are missing.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Prefer constructor injection for required dependencies.",
            "Use field injection only in legacy code or framework-managed tests when unavoidable.",
            "Keep constructors small by reducing service responsibility if dependencies explode.",
            "Inject Clock for testable time-dependent logic."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Define the concept, describe internal behavior, and explain the production consequence.",
            "State when to use it, when not to use it, and what trade-off is being accepted.",
            "Include failure handling, testing approach, and observability signal.",
            "Production answer: connect the topic to a real banking/backend scenario."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "unit tests",
            "integration tests",
            "failure-path tests",
            "observability checks"
        );
    }

    public String commonMistake() {
        return "Do not hide required dependencies as private autowired fields.";
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
        ConstructorVsFieldInjection notes = new ConstructorVsFieldInjection();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
