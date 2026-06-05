package wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class SpringBootAutoConfiguration {

    /*
     * QUESTION 49: How does Spring Boot auto-configuration work?
     *
     * SHORT ANSWER
     * Spring Boot auto-configuration creates beans based on classpath, properties, and existing beans, using conditional annotations to back off when you define your own bean.
     *
     * IN-DEPTH ANSWER
     * - Understand @ConditionalOnClass, @ConditionalOnMissingBean, and property conditions.
     * - Use auto-configuration report to debug why a bean was or was not created.
     * - Override by defining your own bean intentionally.
     * - Keep starter dependencies deliberate.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
     * - Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
     * - Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
     * - Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // If no custom ObjectMapper bean exists, Boot can configure a sensible default.
     * // Defining your own bean makes auto-config back off because of @ConditionalOnMissingBean.
     *
     * REAL BACKEND / BANKING USE CASE
     * Adding spring-boot-starter-data-jpa can auto-configure DataSource/JPA infrastructure when properties are present.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the exact Spring runtime mechanism: proxy, filter chain, bean lifecycle, validation, transaction, or persistence context.
     * - Keep HTTP, business, persistence, and security responsibilities separated.
     * - Cover failure behavior: rollback, safe error response, authorization denial, validation failure, or lazy-loading failure.
     * - Verify with unit, slice, integration, contract, and security tests as appropriate.
     *
     * COMMON MISTAKES
     * - Do not add starters casually without understanding the beans they create.
     * - Do not fight auto-configuration blindly; inspect condition reports.
     * - Do not define duplicate beans accidentally.
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
        return "Spring Boot auto-configuration creates beans based on classpath, properties, and existing beans, using conditional annotations to back off when you define your own bean.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Understand @ConditionalOnClass, @ConditionalOnMissingBean, and property conditions.",
            "Use auto-configuration report to debug why a bean was or was not created.",
            "Override by defining your own bean intentionally.",
            "Keep starter dependencies deliberate."
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
        return "Do not add starters casually without understanding the beans they create.";
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
        SpringBootAutoConfiguration notes = new SpringBootAutoConfiguration();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
