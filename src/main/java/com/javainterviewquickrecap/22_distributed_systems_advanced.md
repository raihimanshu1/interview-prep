# Module — Distributed Systems Advanced: Raft, Partitioning, Replication — Q&A

> **Skill**: 7+ years — Raft consensus, CAP theorem, partitioning strategies, replication, CQRS, Event Sourcing.

---

## Q1. Distributed Consensus — Raft Algorithm

### 1. Raft — Consensus Protocol for Distributed Systems

```
RAFT TERMS:
- Leader: Handles all client requests, replicates log
- Follower: Passive, accepts log entries from leader
- Candidate: Follower that initiates election
- Term: Monotonically increasing election number
- Log Entry: Client operation + term number

STATE TRANSITIONS:
    ┌──────────┐
    │ Follower │ ← Default state (starts here)
    └────┬─────┘
         │ Election timeout expires (150-300ms random)
         ▼
    ┌──────────┐
    │Candidate │ ← Votes for itself, asks other nodes
    └────┬─────┘
         │ Receives majority votes
         ▼
    ┌──────────┐
    │  Leader  │ ← All requests go through leader
    └──────────┘
         │ Sends heartbeats to maintain authority
         │ If heartbeat fails → back to Follower
```

### 2. Partitioning Strategies

| Strategy | How | Pros | Cons |
|----------|-----|------|------|
| **Range-based** | Split by key range (A-M, N-Z) | Simple, range scans possible | Hot spots, requires rebalancing |
| **Hash-based** | hash(key) % N | Even distribution | Range queries hit all partitions |
| **Consistent Hashing** | Virtual nodes on hash ring | Minimal rebalancing on add/remove | More complex, slightly uneven |
| **Directory-based** | Lookup table mapping key→partition | Flexible | Single point of failure |

### 3. CQRS & Event Sourcing

```
CQRS = Command Query Responsibility Segregation
- COMMANDS: Write model (inserts/updates) — optimized for writes
- QUERIES: Read model (reads/views) — optimized for reads, possibly denormalized
- Separate databases/models for read/write

Event Sourcing = Store EVENTS, not current state
- Every change = new event appended
- Current state = replay all events
- Benefits: audit trail, time travel, rebuild read models

┌──────────┐    Event Bus    ┌──────────┐
│ Command  │ ───────────────→│  Query   │
│ Side     │                 │  Side    │
│ (Write)  │                 │ (Read)   │
└──────────┘                 └──────────┘
     │                            │
     ▼                            ▼
Event Store                  Read Database
(append-only)               (denormalized)
```

**Final 30-Second**: Raft: leader election with random timeout, log replication to followers. Partitioning: consistent hashing for minimal rebalancing. CQRS separates reads and writes for independent scaling. Event sourcing provides complete audit trail by storing events instead of current state.