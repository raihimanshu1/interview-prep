# Vending machines globally sync every night at 1 AM. What could go wrong?

## Problem In Simple Words

Analyze global scheduled sync risks.

## Input / Context Meaning

Thousands of vending machines worldwide sync inventory and sales at 1 AM.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

A global schedule sounds simple but distributed systems punish synchronized assumptions.

## Baseline Answer

Anti-pattern baseline: Every machine syncs at local 1 AM or one global 1 AM with a batch job. This can create traffic spikes and inconsistent semantics.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is intentionally weak. In the interview, do not stop here; use it to explain what a poor answer misses, then move to the stronger answer.

## Stronger Answer

Use jittered sync windows, per-region scheduling, idempotent uploads, conflict resolution, retries, offline queueing, and monitoring.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

A machine offline at 1 AM stores events locally, retries with idempotency keys, and server merges by event timestamp.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention thundering herd, daylight saving time, offline machines, duplicate uploads, clock skew, inventory conflicts, partial sync, and alerting.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
