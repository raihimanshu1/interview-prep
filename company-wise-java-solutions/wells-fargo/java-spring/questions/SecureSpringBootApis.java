package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class SecureSpringBootApis {

    /*
     * QUESTION 57: How do you secure Spring Boot APIs?
     *
     * SHORT ANSWER
     * Secure Spring Boot APIs with strong authentication, least-privilege authorization, validation, transport security, secret management, audit logging, and safe error handling.
     *
     * IN-DEPTH ANSWER
     * - Use Spring Security as the default security layer.
     * - Validate all external input.
     * - Never log secrets, tokens, or full sensitive payloads.
     * - Apply TLS, rate limits, and dependency patching.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Show SecurityFilterChain, OAuth2 resource server JWT validation, authorities mapping, and method-level authorization.
     * - Discuss CSRF for browser cookies vs bearer-token APIs, CORS policy, and least privilege scopes.
     * - Add OWASP-style concerns: input validation, safe errors, dependency patching, secrets, audit logging, and rate limiting.
     * - Production answer: security belongs at gateway, service endpoint, and method/domain level, not one layer only.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // Security checklist:
     * // authenticate token -> authorize scope/role -> validate request -> execute use case -> audit sanitized result.
     *
     * REAL BACKEND / BANKING USE CASE
     * A transfer API should require bearer token validation, scope checks, request validation, idempotency, and audit trails.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the exact Spring runtime mechanism: proxy, filter chain, bean lifecycle, validation, transaction, or persistence context.
     * - Keep HTTP, business, persistence, and security responsibilities separated.
     * - Cover failure behavior: rollback, safe error response, authorization denial, validation failure, or lazy-loading failure.
     * - Verify with unit, slice, integration, contract, and security tests as appropriate.
     *
     * COMMON MISTAKES
     * - Do not rely only on client-side validation.
     * - Do not log tokens, passwords, or full sensitive payloads.
     * - Do not authorize only at the UI layer.
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
        return "Secure Spring Boot APIs with strong authentication, least-privilege authorization, validation, transport security, secret management, audit logging, and safe error handling.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Use Spring Security as the default security layer.",
            "Validate all external input.",
            "Never log secrets, tokens, or full sensitive payloads.",
            "Apply TLS, rate limits, and dependency patching."
        );
    }

    public List<String> architectReviewFocus() {
        return List.of(
            "Show SecurityFilterChain, OAuth2 resource server JWT validation, authorities mapping, and method-level authorization.",
            "Discuss CSRF for browser cookies vs bearer-token APIs, CORS policy, and least privilege scopes.",
            "Add OWASP-style concerns: input validation, safe errors, dependency patching, secrets, audit logging, and rate limiting.",
            "Production answer: security belongs at gateway, service endpoint, and method/domain level, not one layer only."
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
        return "Do not rely only on client-side validation.";
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
        SecureSpringBootApis notes = new SecureSpringBootApis();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
