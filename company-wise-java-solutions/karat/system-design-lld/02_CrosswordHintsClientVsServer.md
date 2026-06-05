# Crossword puzzle hints can be stored in the app or on the server. Discuss pros and cons.

## Problem In Simple Words

Compare client-side and server-side storage for crossword hints.

## Input / Context Meaning

A mobile crossword app has puzzle hints. The team can bundle hints inside the app or fetch them from the server.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

This is a tradeoff question. A good answer gives both sides and then recommends based on product goals.

## Baseline Answer

Bundle hints in the app. It is fast and works offline, but updates require app releases and users can inspect local files.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is a basic answer. It is useful as a starting point, but the stronger answer should include constraints, tradeoffs, and validation.

## Stronger Answer

Store hints on server with caching. It supports updates, A/B tests, abuse control, and analytics, while local cache preserves performance/offline support.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

If a clue is wrong, server-side hints can be fixed immediately. Client-bundled hints need an app update and users may stay on old versions.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Discuss offline mode, cache TTL, encrypted local cache, API versioning, hint unlock rules, latency, cost, and content moderation.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
