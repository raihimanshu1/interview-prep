# Ride-sharing app design.

## Problem In Simple Words

Design core ride-sharing backend.

## Input / Context Meaning

Riders request rides; nearby drivers accept; trip moves through states until completion.

## Sample Answer

"I would first clarify scale, users, and success metrics. Then I would describe the simple version of the design, identify where it breaks, and evolve it into a more reliable design. For this prompt, I would cover the main tradeoffs, failure modes, data flow, and how I would monitor success after launch."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

Model the trip state machine first, then the matching system.

## Baseline Answer

Store driver locations in a database and scan all drivers for each request. Simple but too slow.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is a basic answer. It is useful as a starting point, but the stronger answer should include constraints, tradeoffs, and validation.

## Stronger Answer

Use geo index for nearby drivers, trip service with state transitions, pricing service, notification service, and event stream.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

Rider requests pickup. Matching queries nearby available drivers, reserves one, sends offer, creates trip, tracks location, completes payment.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention geohash/H3, driver availability, retries, idempotency, surge pricing, ETA, cancellations, payments, safety, and observability.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.
