package com.companywisejavasolutions.wellsfargo.javaspring.questions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class JwtOpaqueSession {

    /*
     * QUESTION 58: JWT vs opaque token vs session cookie.
     *
     * SHORT ANSWER
     * JWTs are self-contained signed tokens, opaque tokens require server-side introspection, and session cookies store identity through server-managed session state.
     *
     * IN-DEPTH ANSWER
     * - Use short lifetimes and validate issuer, audience, expiry, and signature.
     * - Do not put secrets or sensitive PII in JWT claims.
     * - Opaque tokens centralize revocation but add network calls.
     * - Cookies need SameSite, Secure, HttpOnly, and CSRF protection where applicable.
     *
     * ADVANCED / ARCHITECT VIEW
     * - Explain what Spring proxy/container behavior actually does at runtime, not just the annotation name.
     * - Cover transaction boundaries, validation, security, exception handling, and DTO/entity separation.
     * - Mention integration tests because many Spring behaviors only fail when wiring, proxying, or persistence is real.
     * - Production answer: keep controllers thin, services transactional, repositories persistence-focused, and APIs client-safe.
     *
     * PRODUCTION-READY IMPLEMENTATION SKETCH
     * // JWT resource server checklist:
     * // validate signature, issuer, audience, exp/nbf, scopes, and key rotation.
     * // Map claims to authorities with least privilege.
     *
     * REAL BACKEND / BANKING USE CASE
     * A resource server may validate JWT locally for speed, while opaque tokens allow immediate revocation through introspection.
     *
     * SENIOR-SDE CHECKLIST
     * - Explain the exact Spring runtime mechanism: proxy, filter chain, bean lifecycle, validation, transaction, or persistence context.
     * - Keep HTTP, business, persistence, and security responsibilities separated.
     * - Cover failure behavior: rollback, safe error response, authorization denial, validation failure, or lazy-loading failure.
     * - Verify with unit, slice, integration, contract, and security tests as appropriate.
     *
     * COMMON MISTAKES
     * - Do not treat JWT payloads as encrypted.
     * - Do not skip audience and issuer validation.
     * - Do not ignore token revocation requirements.
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
        return "JWTs are self-contained signed tokens, opaque tokens require server-side introspection, and session cookies store identity through server-managed session state.";
    }

    public List<String> keyPoints() {
        return List.of(
            "Use short lifetimes and validate issuer, audience, expiry, and signature.",
            "Do not put secrets or sensitive PII in JWT claims.",
            "Opaque tokens centralize revocation but add network calls.",
            "Cookies need SameSite, Secure, HttpOnly, and CSRF protection where applicable."
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
        return "Do not treat JWT payloads as encrypted.";
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
        JwtOpaqueSession notes = new JwtOpaqueSession();
        System.out.println(notes.shortAnswer());
        System.out.println("Key point: " + notes.keyPoints().get(0));
        System.out.println("Common mistake: " + notes.commonMistake());
    }
}
