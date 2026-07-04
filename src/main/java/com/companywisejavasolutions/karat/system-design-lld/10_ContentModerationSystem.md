# Content review and moderation system architecture.

## Problem In Simple Words

Design a moderation pipeline for user-generated content.

## Input / Context Meaning

Users upload content that may need automated and human review before publishing or after reports.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Moderation is a workflow system with risk scoring and auditability.

## Baseline Answer

Send every item to humans. Accurate but slow and expensive.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is a basic answer. It is useful as a starting point, but the stronger answer should include constraints, tradeoffs, and validation.

## Stronger Answer

Use ML/rule-based scoring, auto-allow low risk, auto-block high risk, queue uncertain cases for reviewers, and log all decisions.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

A post is uploaded. Classifier gives medium risk, it enters review queue, reviewer decides, audit log records decision and reason.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention queues, priority, reviewer tooling, policy versions, appeals, audit logs, abuse reports, latency, and false positives.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
