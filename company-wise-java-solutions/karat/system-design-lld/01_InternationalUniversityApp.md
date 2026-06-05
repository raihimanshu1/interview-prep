# A university app is deployed in the US and now wants to launch internationally. What concerns should be handled?

## Problem In Simple Words

Design a safe global rollout for an existing university application.

## Input / Context Meaning

A university app supports login, courses, assignments, grades, notifications, and payments. It currently runs only in the US.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Start with user experience and compliance, then move to architecture. Do not jump straight to servers.

## Baseline Answer

Single US region, English-only strings, one database, and manual support for international users. This is simple but creates latency, legal, and operational risk.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is a basic answer. It is useful as a starting point, but the stronger answer should include constraints, tradeoffs, and validation.

## Stronger Answer

Use regional deployment, CDN/static assets, localization, configurable school policies, data residency controls, feature flags, observability, and phased rollout.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

A student in Germany opens the app. Static assets come from CDN, traffic goes to EU region, personal data stays in EU storage if required, dates show local format, and feature flag enables Germany-specific payment provider.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention GDPR/data residency, time zones, i18n/l10n, regional failover, identity providers, payment/tax rules, support readiness, monitoring, and staged launch.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
