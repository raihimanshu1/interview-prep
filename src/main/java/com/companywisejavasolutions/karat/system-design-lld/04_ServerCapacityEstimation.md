# Given storage size and CPU/query load, estimate how many servers are needed.

## Problem In Simple Words

Explain capacity estimation from storage and QPS constraints.

## Input / Context Meaning

You are given data size, request rate, CPU capacity per server, disk size per server, and replication factor.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Capacity questions are about clear assumptions more than exact arithmetic.

## Baseline Answer

Anti-pattern baseline: Guess a server count. This is risky because it ignores the bottleneck.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is intentionally weak. In the interview, do not stop here; use it to explain what a poor answer misses, then move to the stronger answer.

## Stronger Answer

Compute storage servers = total data * replication / usable disk. Compute query servers = QPS / QPS per server. Take max, add 30-50% headroom.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

If data is 30 TB, replication is 3, and usable disk/server is 10 TB, storage needs 9 servers. If QPS needs 6 servers, choose 9 plus headroom.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention assumptions, peak traffic, replication, CPU, memory, disk IOPS, network, failover, growth, and monitoring.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
