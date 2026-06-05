package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class RestControllerVsController {

    /*
     * QUESTION 51: @RestController vs @Controller.
     *
     * SHORT ANSWER
     * @RestController combines @Controller and @ResponseBody, so methods return response bodies; @Controller typically returns views unless @ResponseBody is used.
     *
     * IN-DEPTH ANSWER
     * - Use @RestController for REST APIs.
     * - Use @Controller for server-rendered pages.
     * - Keep HTTP concerns at this layer.
     * - Return DTOs, not persistence entities.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
     * - Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
     * - Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
     * - Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // @RestController
     * // class TransferController {
     * //   @PostMapping("/transfers")
     * //   TransferResponse create(@Valid @RequestBody TransferRequest request) { ... }
     * // }
     *
     * REAL BACKEND / BANKING USE CASE
     * A JSON payment API uses @RestController; an MVC web page controller returns a template name.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the exact Spring runtime mechanism: proxy, filter chain, bean lifecycle, validation, transaction, or persistence context.
     * - Keep HTTP, business, persistence, and security responsibilities separated.
     * - Cover failure behavior: rollback, safe error response, authorization denial, validation failure, or lazy-loading failure.
     * - Verify with unit, slice, integration, contract, and security tests as appropriate.
     *
     * COMMON MISTAKES
     * - Do not use @Controller for JSON endpoints unless you also use @ResponseBody.
     * - Do not put business logic in controllers.
     * - Do not return entities directly from APIs.
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
        return "@RestController combines @Controller and @ResponseBody, so methods return response bodies; @Controller typically returns views unless @ResponseBody is used.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Use @RestController for REST APIs.",
            "Use @Controller for server-rendered pages.",
            "Keep HTTP concerns at this layer.",
            "Return DTOs, not persistence entities."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.",
            "Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.",
            "Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.",
            "Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe."
        );
    }

    public List<String> testingStrategy() {
        return List.of(
            "unit tests for business rules",
            "slice tests for MVC/security/persistence boundaries",
            "integration tests for transactions and database behavior",
            "contract tests for public APIs/events"
        );
    }

    public String commonMistake() {
        return "Do not use @Controller for JSON endpoints unless you also use @ResponseBody.";
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
        RestControllerVsController notes = new RestControllerVsController();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
