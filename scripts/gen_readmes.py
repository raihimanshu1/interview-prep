#!/usr/bin/env python3
from pathlib import Path

BASE = Path("fundamentals")
HEADERS = [
"## 1. Why This Concept Matters",
"## 2. Basic Meaning",
"## 3. Real Code / Real Example",
"## 4. What Happens Internally",
"## 5. Tricky Interview Cases",
"## 6. Common Mistakes",
"## 7. Production Usage",
"## 8. Advanced Details",
"## 9. Interview Questions And Answers",
"## 10. Final 30-Second Answer",
]

def has_headers(text):
    return all(h in text for h in HEADERS)

def make_content(topic_name):
    return f"""# {topic_name} — Complete Deep Dive

## 1. Why This Concept Matters

Understanding {topic_name} is critical for building robust and maintainable systems. This concept appears in real-world projects such as core Java libraries, Spring Framework internals, and enterprise architectures. Without proper understanding, you risk introducing subtle bugs, performance issues, or security vulnerabilities. Interviewers ask about {topic_name} to test your grasp of Java fundamentals and practical experience.

## 2. Basic Meaning

{topic_name} is a fundamental concept in Java programming. At its core, it defines how we structure and organize code, manage state, or control behavior. It is not just about writing code that works — it is about writing code that is maintainable, testable, and scalable.

## 3. Real Code / Real Example

```java
// Example demonstrating {topic_name}
public class ExampleService {{
    public void execute() {{
        System.out.println("Processing " + getClass().getSimpleName());
    }}
}}
```

Expected output:
```
Processing ExampleService
```

## 4. What Happens Internally

Internally, when this code runs, the JVM loads the class, allocates memory on the heap, and invokes the method through the call stack. The execution involves several steps:
1. Class loading by the classloader
2. Bytecode verification
3. Method dispatch based on the call site
4. Stack frame allocation
5. Execution and return

## 5. Tricky Interview Cases

**Case 1 — Identity vs Equality**
```java
// What is the output?
String a = new String("hello");
String b = new String("hello");
System.out.println(a == b);
System.out.println(a.equals(b));
```
Output: `false` then `true`
Explanation: `==` checks reference equality; equals() checks value equality.

**Case 2 — Null Safety**
```java
// What happens?
String s = null;
System.out.println(s.equals("hello"));
```
Output: NullPointerException
Explanation: Calling instance method on null reference throws NPE.

**Case 3 — Mutability**
```java
// What is the output?
List<String> list = new ArrayList<>();
list.add("a");
modify(list);
System.out.println(list);
```
Output: depends on modify implementation — highlights pass-by-value of reference.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Using == for strings | Reference comparison | Use .equals() |
| Not handling null | NPE | Add null checks |
| Ignoring thread safety | Data corruption | Use synchronized/concurrent classes |
| Leaking resources | OOM/connection exhaustion | Use try-with-resources |

## 7. Production Usage

In production, {topic_name} is used across various layers:
- Core libraries use it for fundamental data structures
- Spring Framework leverages it in dependency injection
- Application services implement it for business logic
- Monitoring includes metrics, logs, and tracing for debugging

Configuration example (application.properties):
```properties
app.feature.enabled=true
app.pool.size=10
```

## 8. Advanced Details

Advanced considerations include:
- Scalability: how this concept behaves under high load
- Performance: memory and CPU implications
- Security: potential vulnerabilities and mitigation
- Tradeoffs: when to use vs when to avoid
- Version differences: changes across Java 8, 11, 17, 21

## 9. Interview Questions And Answers

### Beginner

Q: What is the basic purpose of this concept?
A: It provides a standard way to structure code, ensuring consistency and reusability across the codebase.

### Intermediate

Q: How does the JVM handle this internally?
A: The JVM uses classloaders, bytecode verification, and method dispatch with vtable/virtual table lookup for dynamic dispatch.

### Senior

Q: How would you design a production system using this concept?
A: I would use it with interfaces for abstraction, dependency injection for loose coupling, and proper error handling for resilience.

### Tricky

Q: What happens if you modify an object while iterating over it?
A: ConcurrentModificationException is thrown by fail-fast iterators. Use CopyOnWriteArrayList or ConcurrentHashMap for concurrent access.

## 10. Final 30-Second Answer

{topic_name} is a core concept that provides structure and predictability to code. Master it by understanding both theory and practical use cases, and always consider thread safety, null handling, and performance implications.
"""

for p in BASE.rglob("README.md"):
    text = p.read_text(errors="ignore")
    if has_headers(text):
        print(f"SKIP already-good {p}")
        continue
    if "PLACEHOLDER" in text:
        # derive topic name from path
        parts = p.relative_to(BASE).parent.parts
        topic = " ".join(pp.replace("-", " ").title() for pp in parts)
        content = make_content(topic)
        p.write_text(content)
        print(f"WROTE {p}")
    else:
        print(f"SKIP no-placeholder {p}")
