# Describe a time you challenged a teammate approach.

## Problem In Simple Words

Collaboration/conflict prompt. They want respectful disagreement and evidence.

## Input / Context Meaning

This prompt is checking how you behave in real engineering situations. For a 7+ years profile, answer with a specific story or a precise operating principle, then show judgment, ownership, and measurable learning. For this question, keep the focus on: Describe a time you challenged a teammate approach.

## School-Level Intuition

Use a technical disagreement where you listened, compared tradeoffs, and aligned.

## Basic Answer

A teammate suggested doing slow work synchronously inside an API because it was simpler. I acknowledged the simplicity but raised concerns about latency and retries, then proposed comparing sync versus async tradeoffs.

## Strong 7+ Years Answer

A teammate proposed doing a slow operation synchronously inside an API request because it was simpler. I agreed the implementation would be simple, but I was concerned about p95 latency and retry behavior. I suggested we compare both approaches: synchronous for the happy path versus queue-based async for resilience. After looking at expected latency and failure modes, we agreed to keep the user-facing request fast and move the slow work behind an event/queue. The key was not proving someone wrong; it was making tradeoffs visible.

## Why This Answer Works

This works because it gives the interviewer concrete evidence instead of adjectives. It shows what you personally did, why you chose that action, and how the result changed the project, team, or decision quality.

## Anti-Pattern Answer

Saying “I told them their design was wrong.”

## Advanced Version / Add-On

Senior add-on: disagreement should produce a better decision record. Capture context, options, decision, and consequences so the team can revisit later without re-litigating.

## STAR Breakdown

Situation: Technical design disagreement.
Task: Challenge constructively.
Action: Asked questions, showed latency/retry risk, proposed async option.
Result: Team chose a design with better user latency and resilience.

## Dry Run / Walkthrough

Concern: synchronous slow work. Evidence: latency/retry risk. Action: compare options. Result: async design.

## Follow-Up Questions To Prepare

- If asked “what if they resisted,” say you would propose a small experiment.
- If asked “conflict,” emphasize shared goal.
- If asked “mistake,” admit if evidence changed your view.

## Final Interview Checklist

- Start with the situation in one sentence.
- Make your personal action clear.
- Add a senior signal: tradeoff, risk, metric, mentoring, or production impact.
- Keep the story under two minutes, then let the interviewer ask deeper probes.
- Avoid blame; show ownership and learning.
