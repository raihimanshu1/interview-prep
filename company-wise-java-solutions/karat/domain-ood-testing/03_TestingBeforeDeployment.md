# Testing before deployment and test strategy.

## Problem In Simple Words

Explain how to test code before production.

## Input / Context Meaning

A feature is ready; interviewer asks what tests you would write and why.

## Sample Answer

"I would start by clarifying the exact scenario, then explain the core concept in practical terms. For this prompt, the key is: A feature is ready; interviewer asks what tests you would write and why. I would give a concrete example, mention the tradeoff, and close with how I would use or test it in real code."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Testing is about confidence at different layers.

## Baseline Answer

Anti-pattern baseline: Only run manual QA.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is intentionally weak. In the interview, do not stop here; use it to explain what a poor answer misses, then move to the stronger answer.

## Stronger Answer

Write unit tests for logic, integration tests for DB/API contracts, E2E for critical flows, plus canary and rollback after deploy.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

For checkout: unit test price calculation, integration test payment API mock, E2E test add-to-cart to order confirmation.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention test pyramid, fixtures, mocks, flaky tests, CI, coverage meaning, canary, rollback, and observability.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
