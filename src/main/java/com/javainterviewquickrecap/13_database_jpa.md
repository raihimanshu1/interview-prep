# Module 7 — Database, JPA & Transactions — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---

Perfect. This is the **highest ROI Spring topic**. If you understand the **one complete request flow**, almost every JPA/Hibernate interview question becomes easy.

---

# Spring Data JPA + Hibernate + Transactions (7+ Years Recap) ⭐⭐⭐⭐⭐

---

# Complete Flow ⭐⭐⭐⭐⭐

```text
                HTTP Request
                      │
                      ▼
                 Controller
                      │
                      ▼
                  @Transactional
                      │
                      ▼
                    Service
                      │
                      ▼
             Spring Data Repository
                      │
                      ▼
               Hibernate (JPA Provider)
                      │
                      ▼
             Persistence Context
                      │
        ┌─────────────┴─────────────┐
        │                           │
   Entity Already Loaded?       Not Loaded
        │                           │
       Yes                          ▼
        │                    Execute SQL
        │                           │
        └──────────────► Entity Managed
                               │
                               ▼
                    Business Logic Changes Entity
                               │
                               ▼
                        Dirty Checking
                               │
                               ▼
                             Flush
                               │
                               ▼
                            Commit
                               │
                               ▼
                          Database Updated
```

**Remember this diagram.** Almost every JPA interview question comes from it.

---

# 1. Spring Data JPA ⭐⭐⭐⭐⭐

Spring Data JPA is a layer built on top of JPA.

```text
Application

↓

Spring Data JPA

↓

JPA Specification

↓

Hibernate

↓

Database
```

Spring provides the Repository abstraction.

Hibernate performs the actual ORM work.

---

# 2. JPA vs Hibernate ⭐⭐⭐⭐⭐

| JPA           | Hibernate      |
| ------------- | -------------- |
| Specification | Implementation |
| Defines APIs  | Executes them  |
| Interface     | Framework      |

Think of it like

```text
List  → Interface

ArrayList → Implementation
```

Similarly

```text
JPA

↓

Hibernate
```

---

# 3. Persistence Context ⭐⭐⭐⭐⭐

The **most important Hibernate concept**.

It is a memory area that stores all **managed entities**.

```text
Database

↓

Load Employee(1)

↓

Persistence Context

┌────────────────────┐
│ Employee(id=1)     │
└────────────────────┘
```

If the same entity is requested again

```java
repository.findById(1);

repository.findById(1);
```

Second call usually returns the managed entity from the Persistence Context (within the same transaction) instead of issuing another SQL query.

**Interview**

> Persistence Context is also called the **First-Level Cache**.

---

# 4. Entity States ⭐⭐⭐⭐⭐

```text
        new Employee()

             │

             ▼

        Transient

             │ persist()

             ▼

          Managed

             │ detach()

             ▼

          Detached

             │ remove()

             ▼

          Removed
```

| State     | Meaning                      |
| --------- | ---------------------------- |
| Transient | Plain Java object            |
| Managed   | Tracked by Hibernate         |
| Detached  | Exists but no longer tracked |
| Removed   | Scheduled for deletion       |

---

# 5. Dirty Checking ⭐⭐⭐⭐⭐

The most asked Hibernate question.

```java
Employee emp = repository.findById(1);

emp.setSalary(100000);
```

Notice

```java
repository.save(emp);
```

is not called.

Still,

Database gets updated.

Why?

Because Hibernate tracks managed entities.

```text
Managed Entity

↓

Property Changed

↓

Dirty Checking Detects Change

↓

UPDATE SQL Generated
```

No explicit `save()` is required for an already managed entity inside the transaction.

---

# 6. Flush vs Commit ⭐⭐⭐⭐⭐

People confuse these.

### Flush

```text
Persistence Context

↓

SQL Sent to Database
```

Transaction is **still open**.

Changes can still be rolled back.

---

### Commit

```text
Database

↓

Transaction Ends

↓

Changes Become Permanent
```

**Interview**

Flush ≠ Commit.

Flush synchronises changes.

Commit finalises them.

---

# 7. First-Level Cache ⭐⭐⭐⭐⭐

Every Persistence Context has one.

```text
findById(1)

↓

Database

↓

Cache

↓

Next findById(1)

↓

Cache
```

No second SQL.

---

# 8. Lazy vs Eager Loading ⭐⭐⭐⭐⭐

Suppose

```text
Employee

↓

Department
```

---

### Eager

Loads everything immediately.

```text
Employee

+

Department
```

---

### Lazy

Loads only Employee.

Department loads **only when accessed**.

```text
Employee

↓

Later

↓

Department
```

Lazy loading generally improves performance by avoiding unnecessary queries, but you need an active persistence context when the lazy association is accessed.

---

# 9. N+1 Problem ⭐⭐⭐⭐⭐

Example

100 Employees

Each has one Department.

```text
1 Query

↓

Employees

↓

100 More Queries

↓

Departments
```

Total

101 Queries

This is called

> **N+1 Problem**

Solutions

* Fetch Join
* EntityGraph
* Batch Fetching

---

# 10. @Transactional ⭐⭐⭐⭐⭐

```java
@Transactional
public void transfer() {
}
```

Spring starts

```text
Transaction Start

↓

Business Logic

↓

Commit

↓

End
```

If exception occurs

```text
Transaction

↓

Rollback
```

---

# 11. Isolation Levels ⭐⭐⭐⭐⭐

Control how transactions interact.

| Level            | Prevents                                                      |
| ---------------- | ------------------------------------------------------------- |
| READ_UNCOMMITTED | Nothing                                                       |
| READ_COMMITTED   | Dirty Reads                                                   |
| REPEATABLE_READ  | Non-repeatable Reads                                          |
| SERIALIZABLE     | All concurrency anomalies (strongest isolation, highest cost) |

Interview

Default depends on the database (for example, PostgreSQL uses `READ COMMITTED`, while MySQL InnoDB defaults to `REPEATABLE READ`).

---

# 12. Propagation ⭐⭐⭐⭐⭐

Controls what happens when one transactional method calls another.

| Propagation  | Behaviour                               |
| ------------ | --------------------------------------- |
| REQUIRED     | Join existing transaction or create new |
| REQUIRES_NEW | Suspend current and start new           |
| SUPPORTS     | Join if one exists                      |
| MANDATORY    | Must already have a transaction         |
| NEVER        | Fail if a transaction exists            |

Most common

* REQUIRED
* REQUIRES_NEW

---

# Frequently Asked Interview Questions ⭐⭐⭐⭐⭐

### JPA

* JPA vs Hibernate?
* What is Persistence Context?
* Entity Lifecycle?
* First-Level Cache?

### Hibernate

* Explain Dirty Checking.
* Flush vs Commit.
* Lazy vs Eager.
* N+1 Problem.

### Transactions

* What does `@Transactional` do?
* Default rollback behaviour?
* Isolation Levels?
* Propagation?
* REQUIRED vs REQUIRES_NEW?

---

# 5-Minute Revision

```text
Spring Data JPA
        │
        ▼
Hibernate
        │
        ▼
Persistence Context
        │
        ▼
Managed Entity
        │
        ▼
Dirty Checking
        │
        ▼
Flush
        │
        ▼
Commit
        │
        ▼
Database Updated
```

---

## ⭐ One Interview Tip

If a senior interviewer asks:

> **"What happens internally when you call `repository.findById()` inside a `@Transactional` method?"**

A strong answer is:

1. Spring opens a transaction.
2. Hibernate creates or uses the current Persistence Context.
3. It first checks the First-Level Cache.
4. If the entity isn't present, it executes a SQL `SELECT`.
5. The entity becomes **managed**.
6. Any changes to the managed entity are tracked by **Dirty Checking**.
7. On **flush**, Hibernate generates the necessary SQL (`UPDATE`, `INSERT`, or `DELETE`).
8. On **commit**, the database transaction is completed successfully (or rolled back if required).

That answer demonstrates an understanding of how Spring Transactions, JPA, and Hibernate work together, which is exactly what senior interviewers look for.




## Q1. Explain JPA/Hibernate – Entity lifecycle, Persistence Context, dirty checking.

### 1. Why This Concept Matters
Without understanding the persistence context, you'll produce N+1 queries, lazy initialization exceptions, and unexpected SQL statements. Interviewers ask this to test if you understand **how Hibernate bridges object-oriented and relational worlds**.

### 2. Basic Meaning
**Persistence Context** = Hibernate's first-level cache (per-session). It tracks all loaded entities, detects changes (dirty checking), and synchronizes with the database automatically.

### 3. Entity Lifecycle States
```
                        ┌─────────────┐
                        │    NEW      │ (transient — no DB identity)
                        │ (not tracked)│
                        └──────┬──────┘
                               │ persist()
                               ▼
                        ┌─────────────┐
                        │  MANAGED    │ (tracked by persistence context)
                        │ (dirty checked)│
                        └──────┬──────┘
                     ┌─────────┼─────────┐
                     │         │         │
                     ▼         ▼         ▼
                ┌────────┐ ┌────────┐ ┌──────────┐
                │DETACHED│ │REMOVED │ │REFRESHED │
                │(session│ │(sched  │ │(reloaded)│
                │ closed)│ │deleted)│ │          │
                └────────┘ └────────┘ └──────────┘
```

### 4. Real Code / Real Example

```java
@Entity
public class Product {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private double price;
}

// NEW: Not managed by Hibernate
Product product = new Product();
product.setName("Laptop");

// MANAGED: persist() puts entity in persistence context
entityManager.persist(product);  // INSERT scheduled, not executed yet

// DETACHED: flush() executes SQL, but after session close, entity is detached
entityManager.detach(product);   // No longer tracked

// MERGE: Re-attaches a detached entity
product.setPrice(999.99);
Product merged = entityManager.merge(product);  // Returns NEW managed instance

// REMOVED:
entityManager.remove(merged);  // DELETE scheduled, not executed yet

// FLUSH: Executes all pending SQL
entityManager.flush();  // NOW the INSERT/DELETE runs
```

### 5. Dirty Checking Internals

```
Hibernate takes a SNAPSHOT when entity is loaded:
Snapshot: Product{id=1, name="Laptop", price=999.99}
Current:  Product{id=1, name="Laptop", price=799.99}  ← Modified!

At flush time:
1. Compare snapshot vs current (field by field)
2. Detected: price changed (999.99 → 799.99)
3. Generate: UPDATE product SET price=799.99 WHERE id=1
4. Execute SQL

Without dirty checking: you'd manually call update() on every change!
Dirty checking is automatic — any modification to a managed entity
is detected and written to DB at flush time.
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| N+1 query problem | 1 query for parent + N queries for children | Use JOIN FETCH or @EntityGraph |
| Not using @Transactional on service methods | LazyInitializationException outside session | Mark service as @Transactional |
| Modifying entity outside transaction | Changes lost (no dirty checking) | Keep transaction open during modification |
| Forgetting cascade types | Children not persisted/deleted | Set CascadeType.ALL or CascadeType.PERSIST |
| Loading too much data | Memory issues, slow queries | Use DTO projections with JPQL constructor |
| Not closing EntityManager | Connection pool exhaustion | Use Spring-managed EntityManager |

### 7. Production Usage

**Fetching strategies:**
```java
// ❌ N+1 problem (default LAZY)
@OneToMany(mappedBy = "order")
private List<OrderItem> items;
// Accessing items triggers N additional queries

// ✅ Solution 1: JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
Order findOrderWithItems(@Param("id") Long id);

// ✅ Solution 2: @EntityGraph
@EntityGraph(attributePaths = {"items", "customer"})
@Query("SELECT o FROM Order o WHERE o.id = :id")
Order findWithDetails(@Param("id") Long id);

// ✅ Solution 3: DTO projection
@Query("SELECT new com.dto.OrderSummary(o.id, o.total, i.name) FROM Order o JOIN o.items i")
List<OrderSummary> findSummaries();
```

### 8. Advanced Details

**Persistence context flush modes:**
```
FlushModeType.AUTO (default): Flush before any query execution
  - Ensures queries see pending changes
  - Cost: frequent flushes

FlushModeType.COMMIT: Flush only at transaction commit
  - Better performance for read-heavy operations
  - Risk: query won't see unflushed changes in same transaction
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What is the difference between JPA and Hibernate?

**A**: JPA is the specification (interface) — defines how ORM should work. Hibernate is the most popular implementation. JPA provides @Entity, @Table, EntityManager interface. Hibernate adds Session, criteria API, and HQL extensions. Use JPA APIs when possible for vendor-independence.

#### Intermediate

**Q**: Explain the N+1 query problem and how to fix it.

**A**: N+1 occurs when you load parent entities (1 query), then access child collections (N queries for N parents). Example: loading 100 Orders and accessing order.getItems() triggers 100 additional queries. Fixes: (1) JOIN FETCH in JPQL — single SQL with join; (2) @EntityGraph(attributePaths="items"); (3) Batch fetching (@BatchSize). Monitor with spring.jpa.show-sql=true.

#### Senior

**Q**: How would you handle 1M entity updates efficiently?

**A**: Bulk operations: (1) JPQL UPDATE — single SQL statement, no entity loading: `@Modifying @Query("UPDATE Product p SET p.price = :price WHERE p.category = :cat") int updateAll(...)`. Returns affected row count. (2) StatelessSession (Hibernate) — bypasses persistence context, much faster for bulk operations. (3) JDBC batch: `spring.jpa.properties.hibernate.jdbc.batch_size=50` — groups multiple INSERT/UPDATE into one round trip. (4) For completely new data: use COPY (PostgreSQL) or LOAD DATA INFILE (MySQL).

#### Tricky

**Q**: Why does merge() sometimes create a duplicate row?

**A**: merge() creates a NEW managed instance and copies state from the detached entity. If the detached entity has a null or non-existent ID and the entity also has a generated ID strategy, merge() interprets "no existing record" as "new entity" → INSERT. Solution: (1) Use find() before merge to verify existence; (2) Or use persist() for new entities and merge() only for updating existing; (3) Always check if entity.exists() before merging.

### 10. Final 30-Second Answer

JPA: EntityManager manages persistence context (L1 cache). Entities are NEW, MANAGED, DETACHED, or REMOVED. Dirty checking auto-syncs on flush. N+1 queries from lazy loading — fix with JOIN FETCH or @EntityGraph. Bulk updates use @Modifying queries or batch operations.

---

## Q2. Explain Database Transactions – ACID, isolation levels, propagation.

### 1. Why This Concept Matters
Transaction mismanagement causes dirty reads, lost updates, and deadlocks — production-critical issues. Interviewers ask this to test your understanding of **concurrent data integrity**.

### 2. Basic Meaning

**ACID:**
| Property | Meaning | Violation Consequence |
|----------|---------|---------------------|
| Atomicity | All-or-nothing execution | Partial update on failure |
| Consistency | Valid state before and after | Invalid data |
| Isolation | Concurrent transactions don't interfere | Dirty reads, lost updates |
| Durability | Committed changes survive failures | Data loss on crash |

### 3. Real Code / Real Example

```java
@Service
@Transactional  // All methods in this class are transactional
public class TransferService {
    
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // Both operations succeed OR both fail (ATOMICITY)
        accountRepository.withdraw(fromId, amount);
        accountRepository.deposit(toId, amount);
        // If withdraw succeeds but deposit fails → ROLLBACK both!
    }
    
    @Transactional(readOnly = true)  // Optimizes for reads
    public BigDecimal getBalance(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow()
            .getBalance();
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)  // Separate transaction
    public void auditLog(String action) {
        // Always committed independently
        auditRepository.save(new AuditLog(action));
    }
}
```

### 4. Isolation Levels

```
PROBLEMS:
Dirty Read:     Read uncommitted data from another transaction
Non-repeatable: Same row read twice gives different values
Phantom Read:   Same query returns different rows (new rows inserted)

ISOLATION LEVELS:
READ_UNCOMMITTED: Nothing prevented (dirty reads allowed) — rarely used
READ_COMMITTED:   Dirty reads prevented — DEFAULT in PostgreSQL, SQL Server
REPEATABLE_READ:  Dirty + Non-repeatable prevented — DEFAULT in MySQL
SERIALIZABLE:     All prevented — maximum isolation, lowest concurrency
```

**Spring @Transactional isolation:**
```java
// Default: READ_COMMITTED
@Transactional(isolation = Isolation.READ_COMMITTED)
public void updateOrder(Order order) { ... }

// Strict: SERIALIZABLE
@Transactional(isolation = Isolation.SERIALIZABLE)
public void processPayment(Payment payment) { ... }
```

### 5. Propagation Behaviors

| Propagation | Behavior | Use Case |
|-------------|----------|----------|
| REQUIRED (default) | Join existing tx or create new | Most service methods |
| REQUIRES_NEW | Suspend existing, create new | Audit logging (always commit) |
| NESTED | Savepoint within existing tx | Partial rollback possible |
| SUPPORTS | Run with tx if exists, without if not | Read-only queries |
| NOT_SUPPORTED | Always run without tx | Batch operations to avoid long tx |
| NEVER | Fail if a tx exists | Validation methods |
| MANDATORY | Fail if no tx exists | Methods that require transaction context |

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Self-invocation of @Transactional method | AOP proxy bypassed — no transaction! | Inject self-reference or refactor |
| Long transactions | DB connections held, deadlocks, stale data | Keep transactions short, extract I/O |
| Forgetting @Transactional on service | Each JPA operation in separate transaction | Always annotate service methods |
| Using REQUIRES_NEW for everything | Connection pool exhaustion | Use REQUIRED unless explicit need |

### 7. Production Usage

**Optimistic vs Pessimistic locking:**
```java
// OPTIMISTIC: Version field, fails on conflict (no lock held)
@Entity
public class Account {
    @Version
    private Long version;  // Auto-incremented on update
}
// Hibernate throws OptimisticLockException on conflict
// Best for: low contention, read-heavy

// PESSIMISTIC: Database row lock
@Lock(LockModeType.PESSIMISTIC_WRITE)  // SELECT ... FOR UPDATE
@Query("SELECT a FROM Account a WHERE a.id = :id")
Account findForUpdate(@Param("id") Long id);
// Best for: high contention, money transfers
```

### 8. Advanced Details

**Transaction rollback rules:**
```java
@Transactional(
    rollbackFor = {DataAccessException.class},  // Also rollback for checked exceptions
    noRollbackFor = {BusinessException.class}   // Don't rollback for business errors
)
public void processOrder(Order order) throws BusinessException {
    // RuntimeException → ROLLBACK (default)
    // Checked exception → COMMIT (default, unless rollbackFor explicitly specified)
    // BusinessException (here) → COMMIT (noRollbackFor)
}
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What does @Transactional do in Spring?

**A**: @Transactional demarcates transaction boundaries. Spring wraps the method in a transaction: begin() before method, commit() after successful return, rollback() on RuntimeException. All database operations in the method share the same transaction. readOnly=true disables dirty checking (performance optimization for reads).

#### Intermediate

**Q**: Explain isolation levels and which one you'd use for a banking application.

**A**: READ_COMMITTED (default) is the baseline. For banking, use REPEATABLE_READ or SERIALIZABLE for critical operations: (1) Account balance transfer — SERIALIZABLE prevents phantom transactions; (2) Balance enquiry — READ_COMMITTED is acceptable (slight stale ok). Trade-off: higher isolation = lower throughput. Use PESSIMISTIC_WRITE lock for high-value transfers to guarantee consistency without full SERIALIZABLE overhead.

#### Senior

**Q**: You have a transaction that calls a microservice. If the microservice succeeds but a database constraint fails after, how do you handle rollback?

**A**: ACID cannot span microservices — this is the SAGA pattern problem. Solutions: (1) **Transactional Outbox** — write event to database table within the transaction, separate process publishes to microservice. If tx rolls back, event is never sent. (2) **Compensating Transaction** — if microservice call succeeds but subsequent DB fails, send compensation (reverse API call). (3) **Two-phase commit** (XA) — works but is slow and complex — avoid in microservices. Best practice: use transactional outbox + idempotent microservice endpoints for reliability.

#### Tricky

**Q**: Can @Transactional cause a deadlock? How would you debug it?

**A**: Yes. Two threads holding locks on different tables/rows and waiting for each other's locks. Debugging: (1) Enable `spring.jpa.show-sql=true` to see exact SQL order; (2) Database tools: `SHOW PROCESSLIST` (MySQL), `pg_locks` (PostgreSQL), `sp_who2` (SQL Server); (3) Analyze lock order — ensure all transactions acquire locks in the same order; (4) Reduce transaction scope — shorter transactions mean fewer concurrent locks; (5) Use `innodb_lock_wait_timeout` (MySQL) or `lock_timeout` (PostgreSQL); (6) Consider optimisic locking with retry instead of pessimistic.

### 10. Final 30-Second Answer

Transactions provide ACID guarantees. @Transactional in Spring manages begin/commit/rollback. Isolation: READ_COMMITTED (default), REPEATABLE_READ, SERIALIZABLE. Propagation: REQUIRED (default), REQUIRES_NEW (audit). Optimistic locking uses @Version, pessimistic uses @Lock. Keep transactions short. Self-invocation doesn't start new transaction.