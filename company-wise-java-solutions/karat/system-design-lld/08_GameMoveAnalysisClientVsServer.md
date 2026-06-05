# A game analyzes every move; high-end machine takes 1 minute for 200 moves. Should analysis happen on phone or server farm?

## Problem In Simple Words

Choose client vs server compute for expensive analysis.

## Input / Context Meaning

A game app wants to analyze moves. Server hardware is fast; phones vary in power and battery.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Compute placement depends on product need: instant feedback, fairness, cost, and trust.

## Baseline Answer

Run everything on phone. It may work offline but drains battery and is inconsistent across devices.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is a basic answer. It is useful as a starting point, but the stronger answer should include constraints, tradeoffs, and validation.

## Stronger Answer

Use server-side deep analysis, lightweight client hints, caching, and async jobs. Keep sensitive anti-cheat logic server-side.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

Phone sends move history. Server queues analysis, returns result when ready, and client shows progress/cached quick hints.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention battery, thermal limits, privacy, cheating, server cost, queues, caching, offline fallback, and SLA.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
