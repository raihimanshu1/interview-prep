# Wells Fargo Java / Spring Answer Format Standard

Use this format for every Java, Spring Boot, microservices, concurrency, API, HLD, and LLD interview question in this section.

The goal is simple:

```text
Clear
Interview-ready
Example-heavy
Production-aware
Easy to speak out loud
```

Avoid textbook paragraphs. The answer should feel like a strong candidate explaining the topic to an interviewer.

---

# Format For Simple Concept Questions

Use this when the question asks:

```text
What is X?
Difference between X and Y?
Why does X matter?
How does X work?
```

## Required Structure

```text
What is it?

In simple terms:

Why it matters

Example

Safe vs unsafe / good vs bad case

How to handle it in Java/Spring/backend systems

Real-world banking or microservices mindset

Quick mental model

Interview answer
```

## Example Shape

```text
What is API backward compatibility?

An API is backward compatible when a newer backend version can still work
with older clients without breaking them.

In simple terms:

Old apps should continue working even after backend changes.
```

Then continue with:

```text
Why it matters
Example
Breaking change
Backward-compatible change
How to maintain it
Real-world microservices mindset
Quick mental model
Interview answer
```

---

# Format For LLD / HLD / Design Questions

Use this when the question asks:

```text
Design X
How would you build X?
How would you scale X?
How would you make X production-ready?
```

## Required Structure

```text
Title - Low Level Design / High Level Design Deep Dive

Problem Statement

Requirements

Why Do We Need This?

High Level Flow

Key Interview Question

Approaches

Data Structures / Classes

Algorithm

Concurrency Problem

Distributed System Problem

Production Architecture

Failure Handling

Testing Strategy

Follow-Up Questions

Senior-Level Interview Answer
```

## Requirements Block

Use plain words:

```text
Fast
Scalable
Distributed
Accurate
Low memory
Reliable
Observable
```

Only include the requirements that fit the topic.

## Approach Format

Each approach should include:

```text
Idea
Example
Data Structure
Algorithm
Pros
Cons
When to use
```

Order approaches from basic to production-ready.

---

# Required Style Rules

## Use Short Sections

Prefer this:

```text
Old clients still work.
New clients can use the new field.
Backend can evolve safely.
```

Avoid this:

```text
Backward compatibility is an important distributed systems concern because
it allows providers and consumers to evolve independently in a complex
environment where deployments may happen at different times...
```

## Use Simple Examples

Every important idea should have a concrete example:

```text
Old API:
GET /user
{
  "id": 1,
  "name": "John"
}

Breaking new API:
{
  "userId": 1,
  "fullName": "John"
}
```

## Use Java / Spring When Relevant

Add small snippets where useful:

```java
public interface RateLimiter {
    boolean allowRequest(String userId);
}
```

Do not jump directly into code before explaining the design.

## Always Mention Production Concerns

For backend questions, include the relevant items:

```text
Concurrency
Transactions
Retries
Idempotency
Failure handling
Security
Validation
Testing
Observability
Deployment safety
Backward compatibility
```

## Always Add A Speakable Interview Answer

End with a polished answer the candidate can say directly:

```text
In an interview, I would say:

I would solve this by...
The main trade-off is...
In production, I would also handle...
```

## Extra Details Are Mandatory

Do not treat useful adjacent topics as optional follow-ups. Add them as a required section whenever they fit the question.

For API, contract, event, microservice, deployment, library, framework, or design-pattern questions, include:

```text
Forward compatibility
Backward compatibility
Semantic versioning: MAJOR.MINOR.PATCH
How large companies evolve APIs safely
Interview questions to expect
Design patterns or architecture patterns connected to the topic
```

For non-API questions, adapt the same idea:

```text
Compatibility impact
Upgrade or migration risk
Versioning or rollout concern
Real production practice
Common interview follow-ups
Related design patterns or Java/Spring patterns
```

Example:

```text
Extra Details

Forward compatibility:
Older services should ignore unknown fields or unsupported options safely.

Semantic versioning:
Breaking contract change -> MAJOR
Backward-compatible feature -> MINOR
Bug fix / internal improvement -> PATCH

Big-company mindset:
Amazon/Google-style API evolution usually favors additive changes, contract tests,
deprecation windows, telemetry on old clients, and gradual rollout.
```

---

# Review Gate Before Finalizing Any Answer

Reject the answer if it is:

```text
Too abstract
Too textbook
Too long-paragraph heavy
Missing examples
Missing tradeoffs
Missing Java/Spring angle
Missing concurrency when concurrency matters
Missing distributed details when distributed systems matter
Missing production behavior
Missing mandatory extra details
Repeated or duplicated
Not speakable in an interview
```

The final answer should pass this test:

```text
Can I read this out loud in an interview and sound clear, senior, and practical?
```
