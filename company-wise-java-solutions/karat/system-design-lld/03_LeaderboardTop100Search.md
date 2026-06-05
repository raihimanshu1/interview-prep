# Design an efficient way to find the top 100 leaderboard contestants and search a contestant progress by name.

## Problem In Simple Words

Design leaderboard ranking plus name lookup.

## Input / Context Meaning

Contestants gain points over time. The product needs top 100 and fast progress lookup by contestant name.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

One data structure rarely serves every query perfectly. Use one structure for rank and one for profile lookup.

## Baseline Answer

Anti-pattern baseline: Scan all contestants and sort on every request. This is easy but too slow at scale.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is intentionally weak. In the interview, do not stop here; use it to explain what a poor answer misses, then move to the stronger answer.

## Stronger Answer

Keep contestant profile in a key-value store, maintain score in a sorted set/index, cache top 100, and update ranking on score changes.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

A score update for Alice writes profile progress and updates sorted set score. Top 100 reads from cache/sorted set; Alice search reads profile index.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention Redis sorted set, database index on name, unique IDs, tie-breakers, pagination, cache invalidation, consistency, and abuse/rate limits.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
