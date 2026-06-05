# System design vocabulary: load balancer, queues, cache, sharding, consistency, CDN.

## Problem In Simple Words

Prepare quick explanations of common architecture terms.

## Input / Context Meaning

Interviewer asks what component you would use and why.

## Sample Answer

"I would start by clarifying the exact scenario, then explain the core concept in practical terms. For this prompt, the key is: Interviewer asks what component you would use and why. I would give a concrete example, mention the tradeoff, and close with how I would use or test it in real code."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Vocabulary matters because it lets you explain design decisions precisely.

## Baseline Answer

Anti-pattern baseline: Drop buzzwords.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is intentionally weak. In the interview, do not stop here; use it to explain what a poor answer misses, then move to the stronger answer.

## Stronger Answer

Load balancer spreads traffic; cache reduces repeated reads; queue decouples slow work; CDN serves static content near users; sharding splits data; eventual consistency trades freshness for availability.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

Image upload: API stores metadata, queue triggers thumbnail generation, object served through CDN.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention when not to use each component, failure modes, backpressure, cache TTL, idempotency, and consistency.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
