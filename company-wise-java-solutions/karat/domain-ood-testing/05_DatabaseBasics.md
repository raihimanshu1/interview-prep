# Database basics: indexes, transactions, joins, normalization, caching, replication, partitioning.

## Problem In Simple Words

Explain database tradeoffs in interviews.

## Input / Context Meaning

Interviewer asks how to make a query fast or data consistent.

## Sample Answer

"I would start by clarifying the exact scenario, then explain the core concept in practical terms. For this prompt, the key is: Interviewer asks how to make a query fast or data consistent. I would give a concrete example, mention the tradeoff, and close with how I would use or test it in real code."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Databases are about access patterns and consistency requirements.

## Baseline Answer

Anti-pattern baseline: Add indexes everywhere.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is intentionally weak. In the interview, do not stop here; use it to explain what a poor answer misses, then move to the stronger answer.

## Stronger Answer

Use indexes for frequent filters/sorts, transactions for atomic updates, normalization for consistency, denormalization/cache for read speed, replication for scale/HA, partitioning for large data.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

Order lookup by orderId should use indexed primary key. Product search may need search index, not only DB LIKE query.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention ACID, isolation, deadlocks, query plans, cache invalidation, read replicas, sharding keys, and migrations.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
