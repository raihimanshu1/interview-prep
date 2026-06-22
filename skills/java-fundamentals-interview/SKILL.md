---
name: interview-classroom-content
description: Use when creating deep classroom-style interview preparation notes for any technical topic, including Java, OOP, SOLID, design patterns, Java 8, collections, exceptions, strings, multithreading, Spring Boot, REST APIs, SQL, databases, system design, HLD, LLD, microservices, Kafka, Redis, cloud, security, DevOps, testing, scenario-based questions, classic problems, and tricky fundamentals. Produces one concept at a time from beginner to advanced with simple language, real examples, internals, production usage, tricky cases, and complete follow-up questions with answers.
---

# Interview Classroom Content

Use this skill when the user wants in-depth interview material for **any technical topic**, especially when they ask for simple language, classroom-style explanation, tricky questions, real examples, implementation details, and follow-up questions.

Examples:

- Java: Integer cache, autoboxing, string pool, `HashMap`, JVM, concurrency.
- Spring Boot: bean lifecycle, transactions, security, REST APIs.
- SQL/database: indexes, joins, isolation levels, locking, query tuning.
- System design: rate limiter, notification system, payment system, URL shortener.
- Microservices: saga, outbox, circuit breaker, idempotency.
- Kafka/Redis/cloud/security/testing/DevOps concepts.
- Scenario-based and classic problems: production debugging, memory issue, high traffic, duplicate processing, queue backlog, thread pool exhaustion.

## Core Rule

Do **one concept per file** unless the user explicitly asks for a batch. **Depth over speed — always.**

Default output location inside the current workspace:

```text
fundamentals/interview-classroom-content/
```

If the user is working inside a company folder, it is acceptable to place the generated concept under that company folder and link back to the fundamentals folder.

## Workflow

1. Pick the exact concept from the user's request.
2. Create or update an index file for the destination folder.
3. Create one markdown file for that concept.
4. **MANDATORY: Follow the 10-section structure in `references/answer-template.md` exactly.**
5. If the concept is listed in `references/topic-map.md`, use that to choose follow-ups and related topics.
6. Include real code, SQL, API request/response, diagram, pseudocode, or config depending on the topic.
7. Include expected output/result before explaining internals when the example is executable.
8. Explain from beginner level to senior/architect level.
9. Add tricky interview variations and follow-up questions with answers.
10. End with a short 30-second interview answer.

## Deep Dive Requirements

**FAILURE TO INCLUDE THESE = CONTENT IS INCOMPLETE**

For every concept, include enough depth that the user can answer follow-ups, not just the first question.

MANDATORY sections (must all be present):

1. **Why This Concept Matters** — real project usage, what problem it solves, why interviewers ask, what goes wrong if misunderstood
2. **Basic Meaning** — plain English definition, analogy, key vocabulary, what it is NOT
3. **Real Code / Real Example** — simplest working example with expected output/result
4. **What Happens Internally** — step-by-step implementation/behavior flow
5. **Tricky Interview Cases** — at least 3-4 confusing examples with exact output, step-by-step explanation
6. **Common Mistakes** — table of mistakes with problem and fix
7. **Production Usage** — real architecture, configuration, monitoring, debugging tips
8. **Advanced Details** — edge cases, performance, scalability, security, version differences, tradeoffs
9. **Interview Questions And Answers** — minimum 4 questions: 1 Beginner, 1 Intermediate, 1 Senior, 1 Tricky. Each Q&A must have real substance.
10. **Final 30-Second Answer** — crisp interview-ready summary

For flow-heavy topics, include Mermaid diagrams.
For Java/Spring topics, include companion `.java` files when real code makes idea clearer.
For SQL/database topics, include `.sql` files when queries matter.

## Quality Checklist (MANDATORY BEFORE COMPLETION)

Before marking any concept as complete, verify ALL of these:

- [ ] File has all 10 sections (section headers exactly as in template)
- [ ] Section 1 explains WHY this matters in real projects (not just "it's useful")
- [ ] Section 3 has REAL executable code (not pseudocode with `/* ... */`)
- [ ] Section 3 shows expected output/result where applicable
- [ ] Section 4 explains internals step-by-step (not just API listing)
- [ ] Section 5 has at least 3 tricky cases with EXPLICIT output values
- [ ] Section 6 is a table with minimum 4 rows
- [ ] Section 7 includes production config/architecture/diagram
- [ ] Section 8 includes performance/scalability/security considerations
- [ ] Section 9 has exactly 4 Q&As labeled Beginner/Intermediate/Senior/Tricky
- [ ] Section 10 is under 50 words and memorable
- [ ] Total file length > 200 lines (shallow content is unacceptable)
- [ ] No section contains just a list without explanation
- [ ] Code examples use realistic class/method names (not Foo/Bar)

## Artifact Output Rule

Create the output as a small learning package when the topic benefits from it:

- Always create one main `.md` file for the concept.
- Put Mermaid diagrams directly inside the `.md` when explaining flows, architecture, state machines, lifecycle, or request paths.
- Add companion `.java` files for Java/Spring concepts when real code makes the idea clearer.
- Add `.sql` files for SQL/database topics when queries matter.
- Add `.yml`, `.properties`, `.http`, or `.json` examples for Spring Boot, API, security, cloud, DevOps, or config topics when useful.
- Keep companion code minimal, readable, and interview-oriented; it does not need to be a full runnable application unless the user asks.
- Prefer folder-per-concept for large topics, for example `fundamentals/Authentication-Authz/jwt/README.md` plus `examples/*.java`.

## Topic Expansion Rule

When the user gives a topic list, do not only copy the exact topics. Expand each topic into likely interview forms:

- definition questions,
- internal working questions,
- comparison questions,
- scenario-based questions,
- coding questions when applicable,
- production debugging questions,
- performance and scaling questions,
- security and failure-mode questions,
- senior design tradeoff questions.

## Answer Expectations

Each concept answer MUST follow this exact structure:

```markdown
# Concept Name

## 1. Why This Concept Matters
## 2. Basic Meaning
## 3. Real Code / Real Example
## 4. What Happens Internally
## 5. Tricky Interview Cases
## 6. Common Mistakes
## 7. Production Usage
## 8. Advanced Details
## 9. Interview Questions And Answers
## 10. Final 30-Second Answer
```

For tricky examples, avoid vague wording. Be precise:

- state exactly what the output/result is,
- explain why it happens,
- mention version/configuration differences when relevant,
- separate beginner intuition from real implementation behavior,
- call out what should and should not be used in production.

## Tone

Use a patient classroom + interview-coach style. The user wants basic to advanced, one by one, no rush. Keep explanations simple, concrete, example-first, and practical.

## References

- Use `references/answer-template.md` for the required output structure.
- Use `references/topic-map.md` for the broad concept roadmap.
- Use `references/integer-cache-example.md` when handling Integer cache, boxing, unboxing, or wrapper comparison questions.
- Use `references/security-auth-deep-dive.md` when handling JWT, OAuth, Spring Security, authentication, authorization, filters, or API security topics.

## Enforcement

This skill is MANDATORY. Every generated file must pass the Quality Checklist before completion. If any section is missing or shallow, regenerate it. The user explicitly rejects surface-level content. Depth is non-negotiable.