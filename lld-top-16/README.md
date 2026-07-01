# LLD Top 16 — Complete Interview Preparation Guide

> 🎯 **Real Interview Approach** — This isn't about writing code. This is about **demonstrating how you think**. Each problem walks through the exact thought process, requirements gathering, trade-offs, HLD components, and end-to-end flows interviewers expect from senior engineers.

---

## 🧠 The LLD Interview Framework

Every LLD round follows this pattern. Master this framework, and you can handle ANY problem.

```
Phase 1: Requirements Gathering (5 min)  → Ask questions, clarify scope
Phase 2: Problem Decomposition (5 min)   → Break into components
Phase 3: HLD Architecture (10 min)       → System context, services, data flow
Phase 4: Design Decisions (10 min)       → Trade-offs, patterns, concurrency
Phase 5: Implementation (30 min)         → Working code with thought process
Phase 6: Edge Cases (5 min)              → Error handling, failure modes
Phase 7: Testing (5 min)                 → Unit tests, concurrency tests
Phase 8: Follow-ups (10 min)             → Scaling, extensions, distribution
```

---

## 📋 The 16 Problems — Ranked by Complexity

| # | Problem | Key HLD Components | Key LLD Patterns | Est. Time |
|---|---------|-------------------|-----------------|-----------|
| 01 | [**Parking Lot System**](./01-parking-lot/README.md) | Entry/Exit gates, Spot Manager, Payment Gateway | Strategy (pricing, allocation), Singleton, Factory | 90 min |
| 02 | Movie Ticket Booking | Theater Service, Seat Lock Manager, Payment Service | Observer, Factory, Lock Manager | 120 min |
| 03 | Library Management | Book Service, Lending Service, Fine Service | Strategy, Observer, Singleton | 90 min |
| 04 | Splitwise Clone | User Service, Expense Service, Balance Service | Strategy (3 split types), Greedy Debt Simplification | 120 min |
| 05 | Snake and Ladder | Game Engine, Board, Dice | Strategy (dice types), Observer, Singleton | 90 min |
| 06 | Elevator System | Elevator Controller, Dispatcher, Door Control | State, Strategy (dispatch), SCAN Algorithm | 120 min |
| 07 | Cache (LRU/LFU) | In-memory store, Eviction Manager | Strategy (eviction), DoublyLinkedList + HashMap | 90 min |
| 08 | Rate Limiter | Token Bucket, Sliding Window, Redis Backend | Strategy, Factory, Token Bucket Algorithm | 90 min |
| 09 | Task Management | Board Service, Task Service, Notification Service | Observer, State, Command | 90 min |
| 10 | **Cab Booking System** | Matching Service, Trip Manager, Geo-Index | Strategy (pricing, matching), Observer | 120 min |
| 11 | Chess Game | Board, Piece Validators, Move Executor | Strategy (piece moves), Command, State | 120 min |
| 12 | Tic-Tac-Toe | Board, Player Manager, Win Detector | Singleton, Strategy | 60 min |
| 13 | **Vending Machine** | Inventory, Payment Handler, Dispenser | **State** (5 states — most important pattern), Singleton | 90 min |
| 14 | ATM Machine | Cash Dispenser, Authentication, Account Service | **State**, Chain of Responsibility (dispensing) | 90 min |
| 15 | Hotel Booking System | Room Inventory, Booking Manager, Pricing Engine | Strategy, Observer, Singleton | 120 min |
| 16 | E-Commerce Cart | Cart Manager, Inventory, Tax/Shipping Calculators | **Decorator**, Strategy, Observer | 120 min |

> ⭐ **Start with**: Parking Lot (#1) → Snake & Ladder (#5) → Tic-Tac-Toe (#12) → Vending Machine (#13) → Cache (#7) → Splitwise (#4) → Elevator (#6) → Chess (#11)

---

## 🏗️ Common HLD Patterns Across Problems

### 1. Service Decomposition Pattern
```
┌─────────────────────────────────────────────┐
│           API Gateway / Controller           │
├──────────┬──────────┬──────────┬────────────┤
│ Service1 │ Service2 │ Service3 │ Service4   │
│ (Core)   │ (Mgmt)   │ (Engine) │ (External) │
├──────────┴──────────┴──────────┴────────────┤
│              Data Store / Cache              │
└─────────────────────────────────────────────┘
```

### 2. Data Flow Patterns Used

| Pattern | When to Use | Examples |
|---------|-------------|----------|
| **Request-Reply** | Synchronous operations | Park vehicle, Book ticket |
| **Event-Driven** | Async notifications | Payment confirmed → notify user |
| **CQRS** | Read-heavy + Write-heavy | Search shows (read) vs Book seats (write) |
| **Saga** | Distributed transactions | Book flight + Hotel + Car |

### 3. Common Data Stores

| Store | Used For | Because |
|-------|----------|---------|
| **PostgreSQL** | Primary data, transactions | ACID compliance |
| **Redis** | Real-time state, locks, cache | Sub-millisecond reads |
| **Kafka** | Event streaming, async processing | Decouples services, replayability |

---

## 🎯 Design Patterns — Decision Map

```
┌──────────────────────────────────────────────────────┐
│              What are you trying to do?               │
├────────────────────────┬─────────────────────────────┤
│ Object Creation?       │ Factory, Builder, Singleton  │
│ Algorithm Variation?   │ Strategy, Template Method    │
│ State Management?      │ State, Observer              │
│ Request Handling?      │ Command, Chain of Resp.      │
│ Feature Addition?      │ Decorator, Proxy             │
│ Structure?             │ Composite, Adapter           │
└────────────────────────┴─────────────────────────────┘
```

### Pattern Usage Frequency in Interviews

```
Strategy    ████████████████████████  90%  ← Most common
Singleton   ██████████████████        70%
Observer    ████████████████          60%
Factory     ████████████████          60%
State       ██████████                40%
Decorator   ██████                    30%
Command     ████                      20%
Chain of    ███                       15%
```

---

## 📊 Concurrency — Cheat Sheet

| Requirement | Solution | When It Fails |
|-------------|----------|---------------|
| Simple critical section | `synchronized` | Multiple JVMs |
| Read-heavy workloads | `ReadWriteLock` | Needs write priority |
| Fine-grained locking | `ReentrantLock` | Code complexity |
| Atomic counters | `AtomicInteger/Long` | Multiple fields |
| Thread-safe maps | `ConcurrentHashMap` | Compound operations |
| Producer-consumer | `BlockingQueue` | Needs alerting |
| Async operations | `CompletableFuture` | Error handling |
| Scheduled tasks | `ScheduledExecutorService` | Missed execution |

---

## 📁 Directory Structure

```
lld-top-16/
├── README.md                   ← You are here
├── 01-parking-lot/          ✅ Complete — Interview-ready
├── 02-movie-ticket-booking/ 🚧 Coming — Same depth
├── 03-library-management/   🚧 Coming
├── 04-splitwise/            🚧 Coming
├── 05-snake-and-ladder/     🚧 Coming
├── 06-elevator/             🚧 Coming
├── 07-cache-lru-lfu/        🚧 Coming
├── 08-rate-limiter/         🚧 Coming
├── 09-task-management/      🚧 Coming
├── 10-cab-booking/          🚧 Coming
├── 11-chess/                🚧 Coming
├── 12-tic-tac-toe/          🚧 Coming
├── 13-vending-machine/      🚧 Coming
├── 14-atm/                  🚧 Coming
├── 15-hotel-booking/        🚧 Coming
└── 16-ecommerce-cart/       🚧 Coming
```

---

## 🚀 How to Use This Guide

### For Each Problem, You'll Find:

```
1️⃣ THOUGHT PROCESS — How to think out loud
2️⃣ REQUIREMENTS — Questions to ask the interviewer
3️⃣ HLD — Architecture with components and data flow
4️⃣ TRADE-OFFS — Why I chose X over Y
5️⃣ EDGE CASES — What could go wrong
6️⃣ CODE — Complete implementation with LINE-BY-LINE reasoning
7️⃣ FOLLOW-UPS — Scaling, distribution, extensions
8️⃣ CHEAT SHEET — Key classes and interfaces
```

### Example Walk-Through:

> **You**: "Let me ask some clarifying questions first..."
> **Interviewer**: Answers your questions
> **You**: "Based on that, I see X, Y, Z components..."
> **You**: "For the data flow, when a user parks, this happens..."
> **You**: "I'll use Strategy Pattern here because pricing changes..."
> **You**: "Let me now code the core flow..."

---

## ✅ Progress Tracker

| Problem | Requirements | HLD | Trade-offs | Code | Edge Cases | Follow-ups |
|---------|:---:|:---:|:---:|:---:|:---:|:---:|
| 01 Parking Lot | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 02 Movie Booking | 🚧 | 🚧 | 🚧 | 🚧 | 🚧 | 🚧 |
| ... | 🚧 | 🚧 | 🚧 | 🚧 | 🚧 | 🚧 |
| 16 E-Commerce | 🚧 | 🚧 | 🚧 | 🚧 | 🚧 | 🚧 |

---

> **Remember**: The code you write is secondary. The **thought process** and **design decisions** are what get you hired. This guide teaches you how to think like a senior engineer in an LLD interview.