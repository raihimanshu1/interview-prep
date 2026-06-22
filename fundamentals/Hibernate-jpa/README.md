# JPA & Hibernate — Complete Deep Dive

## 1. Why This Concept Matters

JPA (Jakarta Persistence) is the standard ORM specification for Java. Hibernate is the most popular implementation. Understanding entity lifecycle, caching, fetching strategies, and the N+1 problem is essential for building performant data access layers. Interviewers test this heavily — entity states, first/second level cache, lazy/eager fetching, N+1 detection and fixes, dirty checking.

Misunderstanding JPA/Hibernate causes:
- N+1 query explosion (lazy loading)
- LazyInitializationException (accessing lazy field outside transaction)
- Performance degradation from select-before-update (merge)
- Cache inconsistency (first-level cache stale data)

## 2. Basic Meaning

JPA is a specification — defines annotations, EntityManager, queries. Hibernate implements JPA and adds features (caching, batching, filtering).

**Key vocabulary:**
- **Entity**: POJO annotated with `@Entity`, mapped to a DB table
- **EntityManager**: manages entity lifecycle, queries
- **Persistence Context**: first-level cache (per transaction)
- **Entity states**: Transient, Managed, Detached, Removed
- **`@Id` + `@GeneratedValue`**: primary key
- **`@OneToMany`, `@ManyToOne`, `@ManyToMany`**: relationships
- **`FetchType.LAZY`**: load on access (proxy)
- **`FetchType.EAGER`**: load immediately with parent
- **Cascade**: propagate operations (PERSIST, MERGE, REMOVE, ALL)
- **JPQL**: entity-oriented query language
- **Native query**: SQL queries
- **First-level cache**: within persistence context
- **Second-level cache**: across sessions (Hibernate-specific)

What it is NOT: Not for batch operations (use JDBC). Not for complex analytics queries (use native SQL). Not a replacement for proper database design.

## 3. Real Code / Real Example

```java
import jakarta.persistence.*;
import java.util.*;

// === ENTITY ===
@Entity
@Table(name = "departments")
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();
    
    public void addEmployee(Employee emp) {
        employees.add(emp);
        emp.setDepartment(this);
    }
}

@Entity
@Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department department;
}

// === REPOSITORY ===
@Repository
public class DepartmentRepository {
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public Department createDepartment(String name) {
        Department dept = new Department();
        dept.setName(name);
        em.persist(dept); // State: TRANSIENT → MANAGED
        return dept;
    }
    
    @Transactional(readOnly = true)
    public Department findById(Long id) {
        return em.find(Department.class, id); // checks 1st-level cache first
    }
    
    @Transactional
    public void updateDepartmentName(Long id, String newName) {
        Department dept = em.find(Department.class, id);
        dept.setName(newName); // Dirty checking — auto UPDATE on flush
    }
    
    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = em.find(Department.class, id);
        em.remove(dept); // State: MANAGED → REMOVED
    }
    
    @Transactional(readOnly = true)
    public List<Department> findAllWithEmployees() {
        // SOLUTION for N+1: JOIN FETCH
        return em.createQuery(
            "SELECT DISTINCT d FROM Department d JOIN FETCH d.employees", 
            Department.class
        ).getResultList();
    }
    
    @Transactional(readOnly = true)
    public List<Department> findAllLazy() {
        return em.createQuery("SELECT d FROM Department d", Department.class)
                .getResultList();
        // Each d.employees access later = N+1 queries
    }
    
    // Batch operation
    @Transactional
    public void batchInsert(List<Employee> employees) {
        for (int i = 0; i < employees.size(); i++) {
            em.persist(employees.get(i));
            if (i % 50 == 0) {
                em.flush();
                em.clear(); // prevent memory leak from 1st-level cache
            }
        }
    }
}
```

Expected output:
```
Department dept = repo.createDepartment("Engineering"); // INSERT
dept.getName(); // "Engineering" (managed, from persistence context)

Department found = repo.findById(1L); // SELECT (unless cached)
found.setName("Engineering V2"); // UPDATE on flush/commit

List<Department> depts = repo.findAllWithEmployees(); // 1 query with JOIN
List<Department> lazy = repo.findAllLazy(); // 1 query only — N+1 on access
```

## 4. What Happens Internally

**Entity lifecycle states:**
```
Transient (new)
    │
    │ persist()
    ▼
Managed (in persistence context)
    │                  │
    │ close/clear      │ remove()
    ▼                  ▼
Detached           Removed
```

**Persistence context (1st-level cache):**
- Map<Class<?>, Map<Id, Entity>> — EntityManager holds references
- `find(id)`: checks cache first → if miss, SELECT DB → store in cache
- `merge(detached)`: SELECT exists in DB → copy state → return managed instance
- `flush()`: dirty check → generate SQL for changed managed entities
- `clear()`: detach all managed entities (cache cleared)

**Dirty checking:**
- Hibernate takes snapshot of entity when loaded
- On flush: compares current state with snapshot
- If changed: generates UPDATE SQL (only changed columns if `@DynamicUpdate`)

**N+1 Query Generation:**
```
SELECT d FROM Department d; -- 1 query → 10 departments
for each department:
    SELECT e.* FROM employees e WHERE e.dept_id = d.id; -- 10 queries
Total: 1 + 10 = 11 queries
```

**First-level cache hit/miss:**
```
em.find(Employee.class, 1L); // Cache miss → SELECT
em.find(Employee.class, 1L); // Cache hit → no SQL
em.find(Employee.class, 2L); // Cache miss → SELECT
```

## 5. Tricky Interview Cases

**Case 1 — LazyInitializationException**
```java
@Transactional(readOnly = true)
public Department getDepartment(Long id) {
    return em.find(Department.class, id);
}
// Controller:
Department dept = service.getDepartment(1L);
dept.getEmployees().size(); // LazyInitializationException!
```
Fix: Use JOIN FETCH, @EntityGraph, or keep transaction open (OSIV).

**Case 2 — `merge()` vs `persist()`**
```java
Employee emp = new Employee();
emp.setName("Alice");
em.persist(emp); // INSERT — entity becomes MANAGED
emp.setName("Alice Updated"); // auto UPDATE on flush (dirty checking)

Employee detached = new Employee();
detached.setId(1L); // existing ID
detached.setName("Bob");
em.merge(detached); // SELECT + UPDATE — returns managed instance
```

**Case 3 — Persist on detached entity**
```java
Employee emp = em.find(Employee.class, 1L); // MANAGED
em.clear(); // DETACHED
em.persist(emp); // IllegalArgumentException! Can't persist detached
```
Fix: Use `merge()` instead.

**Case 4 — `remove()` requires managed entity**
```java
Employee detached = new Employee();
detached.setId(1L);
em.remove(detached); // IllegalArgumentException
```
Fix: `em.remove(em.find(Employee.class, 1L))` or `em.remove(em.getReference(Employee.class, 1L))`.

**Case 5 — Batch insert memory leak**
```java
@Transactional
public void insertMany(List<Employee> list) {
    for (Employee e : list) {
        em.persist(e); // All entities stay in 1st-level cache!
    }
    // After 100,000 inserts: OutOfMemoryError
}
```
Fix: Flush + clear periodically: `if (i % 50 == 0) { em.flush(); em.clear(); }`

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| N+1 from lazy loading | 1+N queries | JOIN FETCH, EntityGraph, BatchSize |
| Calling `persist()` on detached entity | IllegalArgumentException | Use `merge()` |
| Not flushing before batch clear | Unsaved changes lost | `flush()` before `clear()` |
| Loading entire table into memory | OOM | Use `ScrollableResults` or streaming |
| Missing `@Transactional` on modification | LazyInitializationException | Add `@Transactional` |
| Cascading REMOVE on shared entity | Deletes records unintentionally | Review cascade types carefully |

## 7. Production Usage

**Second-level cache with Redis/Ehcache:**
```java
@Entity
@Cacheable // enables 2nd-level cache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Department { ... }
```

**Soft delete pattern:**
```java
@Entity
public class SoftDeletable {
    private boolean deleted = false;
    
    @Where(clause = "deleted = false")
    @OneToMany(mappedBy = "parent")
    private List<Child> children;
    
    public void softDelete() { this.deleted = true; }
}
```

**Optimistic locking:**
```java
@Entity
public class VersionedEntity {
    @Version
    private int version; // incremented on each UPDATE
    
    // If concurrent update detected: OptimisticLockException
}
```

## 8. Advanced Details

- **`@DynamicUpdate`**: Only update changed columns (vs all columns). Performance optimization.
- **`@BatchSize(size=10)`**: Load lazy collections in batches of 10.
- **`ScrollableResults`**: Cursor-based iteration for large result sets.
- **StatelessSession**: Hibernate's batch-optimized session (no 1st-level cache, no dirty checking).
- **Hibernate Interceptors**: `onSave()`, `onLoad()`, `preFlush()` — audit logging, soft delete.
- **JPQL vs Criteria API**: JPQL for static queries, Criteria API for dynamic query building.
- **`@Formula`**: Computed property from SQL expression (not stored in database).

## 9. Interview Questions And Answers

### Beginner
Q: Explain the entity lifecycle states in JPA.
A: Transient (new, not persisted, no ID), Managed (persisted, in persistence context, auto-synced on flush), Detached (persisted but no longer in persistence context — from clear/close), Removed (scheduled for deletion on flush).

### Intermediate
Q: What is the difference between `FetchType.LAZY` and `FetchType.EAGER`?
A: LAZY: loads association on demand (proxy created). First access triggers SELECT. EAGER: loads association immediately with parent (JOIN or separate SELECT). JPA defaults: `@*ToOne` = EAGER, `@*ToMany` = LAZY. EAGER can cause performance issues (Cartesian product, N+1). Prefer LAZY and use JOIN FETCH or EntityGraph when needed.

### Senior
Q: You have 100,000 records to insert. Using JPA, how do you avoid OOM and achieve good performance?
A: 1. Disable 2nd-level cache. 2. Set `hibernate.jdbc.batch_size=50`. 3. Flush + clear every 50 records. 4. Use `StatelessSession` (no 1st-level cache, no dirty checking). 5. Use sequence-generated IDs (not identity — identity disables JDBC batch). 6. `hibernate.order_inserts=true`. 7. For extremely large: use JDBC batch directly.

### Tricky
Q: `em.find()` vs `em.getReference()` — what's the difference?
A: `find()`: always executes SELECT immediately (or returns from cache). Returns the entity or null.
`getReference()`: returns a proxy without SELECT. Only loads data when a method is called on the proxy. If entity doesn't exist, throws EntityNotFoundException when accessed.

Use `getReference()` for setting foreign key relationships to avoid unnecessary SELECT.

## 10. Final 30-Second Answer

JPA = ORM standard. Hibernate = implementation. **Entity states**: Transient → Managed (persist) → Detached (clear) → Removed. **1st-level cache**: per EntityManager, checked before SQL. **N+1**: lazy collections = 1+N queries. **Fix**: JOIN FETCH, EntityGraph, BatchSize. **Dirty checking**: snapshot on load → compare on flush → UPDATE. **Cascade**: propagate persist/merge/remove. **@Version**: optimistic locking. **Batch**: flush + clear periodically. **LAZY** for performance, JOIN FETCH when needed.