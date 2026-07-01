# Creational Design Patterns

> **Focus:** Object creation mechanisms that increase flexibility and reuse of existing code.  
> **Core Idea:** Let the class handle its own object creation rather than creating objects directly with `new`.

---

## Patterns Overview

| Pattern | Purpose | When to Use |
|---------|---------|-------------|
| **Singleton** | Ensure only ONE instance of a class | Loggers, Config, Connection Pools, Caches |
| **Factory Method** | Define interface for creating objects, let subclasses decide | Payment gateways, Document parsers, Notification services |
| **Abstract Factory** | Create families of related/ dependent objects | UI Theme systems (Dark/Light), Cross-platform widgets |
| **Builder** | Construct complex objects step-by-step | SQL Query builder, URL builder, Meal builder |
| **Prototype** | Clone objects instead of creating from scratch | Graphic editors, Game asset duplication, Cache population |

---

## Interview Mindset

```
Question asks: "Create an object" → Think Creational

Q: Need only one instance?                → Singleton
Q: Different types based on input?        → Factory Method
Q: Families of related objects?           → Abstract Factory
Q: Complex construction with many steps?  → Builder
Q: Expensive to create, want to clone?    → Prototype