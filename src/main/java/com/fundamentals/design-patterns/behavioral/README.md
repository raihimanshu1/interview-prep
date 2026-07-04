# Behavioral Design Patterns

> **Focus:** How objects communicate and distribute responsibility among themselves.  
> **Core Idea:** Define patterns for object interaction that increase flexibility.

---

## Patterns Overview

| Pattern | Purpose | When to Use |
|---------|---------|-------------|
| **Observer** | One-to-many dependency — notify on change | Event handling, Pub-Sub, Stock updates |
| **Strategy** | Select algorithm at runtime | Payment methods, Sorting, Compression |
| **Command** | Encapsulate request as object | Undo/Redo, Job queue, Macros |
| **Chain of Responsibility** | Pass request along handler chain | Logging levels, Validation pipeline |
| **State** | Change behavior when state changes | Vending machine, Order lifecycle |
| **Template Method** | Define skeleton, subclasses fill details | Data exporters, Recipe steps |
| **Mediator** | Centralize complex communication | Chat room, Air traffic control |
| **Iterator** | Traverse collections without exposing internals | Custom collections, Tree traversal |
| **Visitor** | Add operations without changing classes | AST processing, File export |
| **Interpreter** | Define grammar and interpret sentences | Expression parser, Rule engine |

---

## Interview Mindset

```
Q: Object needs to notify many others on change?     → Observer
Q: Multiple algorithms interchangeable at runtime?   → Strategy
Q: Need undo/redo or queue operations?               → Command
Q: Multiple handlers can process a request?          → Chain of Responsibility
Q: Object behavior changes with internal state?      → State
Q: Algorithm skeleton with variable steps?           → Template Method
Q: Reduce many-to-many communication complexity?     → Mediator
Q: Traverse collection without exposing structure?   → Iterator
Q: Need new operation without modifying classes?     → Visitor
Q: Domain-specific language or expression parsing?   → Interpreter