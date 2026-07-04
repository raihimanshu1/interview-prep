# Describe making a quick decision.

## Problem In Simple Words

Judgment under time pressure.

## Input / Context Meaning

This prompt is checking how you behave in real engineering situations. For a 7+ years profile, answer with a specific story or a precise operating principle, then show judgment, ownership, and measurable learning. For this question, keep the focus on: Describe making a quick decision.

## School-Level Intuition

Use an incident or release decision with clear constraints.

## Basic Answer

During an incident, I chose rollback over continued debugging because customer impact was active and rollback was reversible. After restoring service, we debugged safely and shipped a tested fix.

## Strong 7+ Years Answer

During an incident, I had to choose between continuing to debug in production or rolling back. Since customer impact was active and root cause was not proven, I chose rollback first. That restored service quickly. After the system was stable, we reproduced the issue in a safe environment and shipped a tested fix. The lesson was that during active impact, recovery comes before perfect diagnosis.

## Why This Answer Works

This works because it gives the interviewer concrete evidence instead of adjectives. It shows what you personally did, why you chose that action, and how the result changed the project, team, or decision quality.

## Anti-Pattern Answer

Making it sound like guessing.

## Advanced Version / Add-On

Advanced angle: name decision criteria: customer impact, reversibility, blast radius, confidence, and learning loop.

## STAR Breakdown

Situation: Active production issue.
Task: Decide fast.
Action: Rollback first, debug second.
Result: Restored service and shipped safe fix.

## Dry Run / Walkthrough

Impact active -> rollback reversible -> restore -> investigate -> fix.

## Follow-Up Questions To Prepare

- If asked “rollback failed,” mention mitigation plan.
- If asked “pressure,” mention criteria.
- If asked “lesson,” say recovery before diagnosis during active impact.

## Final Interview Checklist

- Start with the situation in one sentence.
- Make your personal action clear.
- Add a senior signal: tradeoff, risk, metric, mentoring, or production impact.
- Keep the story under two minutes, then let the interviewer ask deeper probes.
- Avoid blame; show ownership and learning.
