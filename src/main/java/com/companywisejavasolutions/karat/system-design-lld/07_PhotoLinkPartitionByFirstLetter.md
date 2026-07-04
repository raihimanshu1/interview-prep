# Photo link app partitions storage by first letter of username. What problems do you see?

## Problem In Simple Words

Critique a bad partitioning strategy.

## Input / Context Meaning

A photo app stores users/photos in partitions based on the first letter of username.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Partitioning should distribute load evenly and stay stable. First letter does neither.

## Baseline Answer

Use first letter because it is simple and human-readable. This creates uneven partitions like A/S/M and poor growth.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is a basic answer. It is useful as a starting point, but the stronger answer should include constraints, tradeoffs, and validation.

## Stronger Answer

Use hashed user ID or consistent hashing. Keep username as metadata, not partition identity.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

If many users start with A, partition A becomes hot. If alice changes username to zalice, moving data is expensive.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention hot spots, celebrity/user skew, non-English names, case sensitivity, username changes, rebalancing, and hashed IDs.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
