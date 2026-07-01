o # Structural Design Patterns

> **Focus:** How classes and objects are composed to form larger structures.  
> **Core Idea:** Simplify relationships between entities by defining ways to assemble objects.

---

## Patterns Overview

| Pattern | Purpose | When to Use |
|---------|---------|-------------|
| **Adapter** | Match interfaces of different classes | Legacy integration, Third-party library wrapper |
| **Decorator** | Add responsibilities dynamically | Pizza toppings, Encryption layers, Logging |
| **Proxy** | Control access to an object | Lazy loading, Caching, Access control |
| **Facade** | Simplify complex subsystem | Order processing, Video conversion |
| **Composite** | Treat individual & group uniformly | File system, Organization hierarchy |
| **Bridge** | Decouple abstraction from implementation | Remote+Device, Shape+Color |
| **Flyweight** | Share common state efficiently | Text editors, Game trees |

---

## Interview Mindset

```
Q: Make incompatible interfaces work together?  → Adapter
Q: Add features dynamically to an object?       → Decorator
Q: Control/restrict access to something?        → Proxy
Q: Hide complex system behind simple API?       → Facade
Q: Tree structure of objects treated same?      → Composite
Q: Separate abstraction from implementation?    → Bridge
Q: Optimize memory with many similar objects?   → Flyweight