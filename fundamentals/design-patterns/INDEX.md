# Design Patterns — Master Index

> **Goal:** Interview-ready on all 23 Gang of Four (GoF) design patterns.  
> Each pattern includes: definition, when to use, Java code example solving a real interview problem, and common interview questions.

---

## How Design Patterns Are Categorized

Think of it like building a house:

Creational patterns → How do we create bricks? (object creation)

Structural patterns → How do we arrange bricks to build rooms? (object composition) 
 - [how classes and objects are combined together to form larger structures]

Behavioral patterns → How do rooms communicate with each other? (object interaction)

Structural patterns answer questions like:

How can we add new functionality without changing existing code?
How can incompatible classes work together?
How can we simplify complex object relationships?

```
                    ┌─────────────────────────────┐
                    │     Design Patterns (23)     │
                    └─────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        ▼                       ▼                       ▼
┌───────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Creational   │     │   Structural   │     │   Behavioral    │
│  (5 patterns)  │     │  (7 patterns)  │     │  (11 patterns)  │
├───────────────┤     ├─────────────────┤     ├─────────────────┤
│ Object creation│     │ Class/object    │     │ Communication   │
│ & management   │     │ composition     │     │ between objects │
└───────────────┘     └─────────────────┘     └─────────────────┘
```

---

## 📚 Creational Patterns (Object Creation)

> **Focus:** How objects are created — making the system independent of how its objects are created, composed, and represented.

| # | Pattern | What It Solves | Real Interview Problem | Go To File |
|---|---------|---------------|----------------------|------------|
| 1 | **Singleton** | Only one instance of a class | Logger, Config Manager, DB Connection Pool | [singleton/](./singleton/) |
| 2 | **Factory Method** | Create objects without specifying exact class | Payment Gateway (CreditCard/PayPal/UPI) | [factory/](./factory/) |
| 3 | **Abstract Factory** | Create families of related objects | UI Theme (Dark/Light: Button + TextField + Checkbox) | [abstract-factory/](./abstract-factory/) |
| 4 | **Builder** | Construct complex objects step-by-step | URL Builder, Pizza Order, SQL Query Builder | [builder/](./builder/) |
| 5 | **Prototype** | Clone objects instead of creating from scratch | Shape/Graphic editor with copy-paste | [prototype/](./prototype/) |

---

## 🏗️ Structural Patterns (Class/Object Composition)

> **Focus:** How classes and objects are composed to form larger structures.

| # | Pattern | What It Solves | Real Interview Problem | Go To File |
|---|---------|---------------|----------------------|------------|
| 1 | **Adapter** | Make incompatible interfaces work together | Legacy payment system → New API wrapper | [adapter/](./adapter/) |
| 2 | **Decorator** | Add responsibilities to objects dynamically | Pizza topping calculator, Encryption layers | [decorator/](./decorator/) |
| 3 | **Proxy** | Control access to another object | Lazy loading, Access control, Caching | [proxy/](./proxy/) |
| 4 | **Facade** | Provide a simple interface to a complex system | Video conversion API, Order processing | [facade/](./facade/) |
| 5 | **Composite** | Treat individual and composite objects uniformly | File System (File + Folder), Organization chart | [composite/](./composite/) |
| 6 | **Bridge** | Decouple abstraction from implementation | Device + Remote control, Shapes + Colors | [bridge/](./bridge/) |
| 7 | **Flyweight** | Share common state among many objects | Text editor character rendering, Game trees | [flyweight/](./flyweight/) |

---

## 🔄 Behavioral Patterns (Communication Between Objects)

> **Focus:** How objects interact and distribute responsibility.

| # | Pattern | What It Solves | Real Interview Problem | Go To File |
|---|---------|---------------|----------------------|------------|
| 1 | **Observer** | One-to-many dependency (notify updates) | Stock price notifier, Event bus, Pub-Sub | [observer/](./observer/) |
| 2 | **Strategy** | Select algorithm at runtime | Payment strategy, Sorting strategy, Compression | [strategy/](./strategy/) |
| 3 | **Command** | Encapsulate request as an object | Undo/Redo, Job queue, Remote control | [command/](./command/) |
| 4 | **Chain of Responsibility** | Pass request along a chain of handlers | Logging (DEBUG→INFO→ERROR), ATM dispenser | [chain-of-responsibility/](./chain-of-responsibility/) |
| 5 | **State** | Change behavior when internal state changes | Vending Machine, Order State (NEW→PAID→SHIPPED) | [state/](./state/) |
| 6 | **Template Method** | Define skeleton, let subclasses fill details | Data mining (CSV→PDF→Excel), Cooking recipe | [template-method/](./template-method/) |
| 7 | **Mediator** | Reduce direct connections between objects | Chat Room, Air Traffic Control | [mediator/](./mediator/) |
| 8 | **Iterator** | Traverse collections without exposing internals | Custom collection traversal, Tree traversal | [iterator/](./iterator/) |
| 9 | **Visitor** | Add operations without changing classes | AST operations (Compile, Pretty Print, Analyze) | [visitor/](./visitor/) |
| 10 | **Interpreter** | Define grammar and interpret sentences | Mathematical expression parser, SQL parser | [interpreter/](./interpreter/) |

---

## 📖 Interview Questions by Pattern

See **[interview-questions/README.md](interview-questions/README.md)** for categorized list of pattern-based interview questions asked at top tech companies.

---

## ✅ Progress Tracker

### Creational (5)
- [ ] Singleton
- [ ] Factory Method
- [ ] Abstract Factory
- [ ] Builder
- [ ] Prototype

### Structural (7)
- [ ] Adapter
- [ ] Decorator
- [ ] Proxy
- [ ] Facade
- [ ] Composite
- [ ] Bridge
- [ ] Flyweight

### Behavioral (11)
- [ ] Observer
- [ ] Strategy
- [ ] Command
- [ ] Chain of Responsibility
- [ ] State
- [ ] Template Method
- [ ] Mediator
- [ ] Iterator
- [ ] Visitor
- [ ] Interpreter