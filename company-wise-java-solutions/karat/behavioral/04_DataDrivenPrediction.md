# Describe a time you forecasted demand or made a data-driven prediction.

## Problem In Simple Words

Analytical thinking prompt. They want assumptions, data, baseline, validation, and decision.

## Input / Context Meaning

This prompt is checking how you behave in real engineering situations. For a 7+ years profile, answer with a specific story or a precise operating principle, then show judgment, ownership, and measurable learning. For this question, keep the focus on: Describe a time you forecasted demand or made a data-driven prediction.

## School-Level Intuition

Use a simple forecast story: traffic, capacity, inventory, release impact, support volume, or test duration.

## Basic Answer

I estimated peak load for a rollout using historical traffic, expected launch multiplier, and headroom. I used a simple baseline forecast first because the team could audit it quickly.

## Strong 7+ Years Answer

For a backend capacity-style task, I estimated expected peak load before a feature rollout. I started with historical request volume, identified the busiest hour, adjusted for the expected launch multiplier, and added headroom for retries and uneven traffic. I did not jump to a complex model first; I used a baseline estimate that the team could audit. Based on that, we decided how many instances were needed and what dashboards to watch during rollout. The value was not perfect prediction; it was making risk visible early and having a rollback/scale-up plan ready.

## Why This Answer Works

This works because it gives the interviewer concrete evidence instead of adjectives. It shows what you personally did, why you chose that action, and how the result changed the project, team, or decision quality.

## Anti-Pattern Answer

Saying “I used data” without naming the data, assumption, or decision.

## Advanced Version / Add-On

Advanced angle: include validation. After rollout, compare predicted QPS, p95 latency, error rate, and instance utilization against actuals, then update future assumptions.

## STAR Breakdown

Situation: Upcoming rollout could increase traffic.
Task: Estimate capacity and reduce risk.
Action: Used historical peak, multiplier, and headroom; prepared dashboards.
Result: Team had a clear scale plan and monitoring during launch.

## Dry Run / Walkthrough

Data: historical QPS. Assumption: launch doubles traffic. Action: capacity plan. Result: monitored p95 and error rate after rollout.

## Follow-Up Questions To Prepare

- If asked “wrong forecast,” say you plan rollback/scale-up.
- If asked “why simple model,” say explainability and speed mattered.
- If asked “metric,” mention QPS, p95, error rate, CPU/memory.

## Final Interview Checklist

- Start with the situation in one sentence.
- Make your personal action clear.
- Add a senior signal: tradeoff, risk, metric, mentoring, or production impact.
- Keep the story under two minutes, then let the interviewer ask deeper probes.
- Avoid blame; show ownership and learning.
