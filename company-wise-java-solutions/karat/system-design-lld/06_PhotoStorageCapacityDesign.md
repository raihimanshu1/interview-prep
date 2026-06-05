# Photo storage capacity and in-house database design: what would you estimate?

## Problem In Simple Words

Estimate photo storage and metadata architecture.

## Input / Context Meaning

Users upload photos. We need estimate storage, metadata, thumbnails, and retrieval architecture.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Photos are large blobs; databases are better for metadata than raw image bytes.

## Baseline Answer

Anti-pattern baseline: Store everything in one database table. Simple, but backups, scaling, and delivery become painful.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is intentionally weak. In the interview, do not stop here; use it to explain what a poor answer misses, then move to the stronger answer.

## Stronger Answer

Store images in object storage/CDN, metadata in DB, thumbnails generated asynchronously, with lifecycle and replication policies.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

10M users * 100 photos * 3MB = 3PB raw. Add thumbnails and replication, then plan object storage capacity and CDN.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention average photo size, growth, replication, compression, thumbnails, CDN, metadata schema, privacy, deletion, backups, and cost.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
