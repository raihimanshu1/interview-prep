# Spring Data JPA — Complete Deep Dive

## 1. Why This Concept Matters

Spring Data JPA is the de facto standard for database access in Spring Boot applications. It eliminates boilerplate DAO code by generating implementations from repository interfaces. In production, it powers CRUD operations, pagination, sorting, custom queries, and auditing. Interviewers test this extensively — repository patterns, query methods, @Query, transactions, N+1 problem, entity lifecycle.

Misunderstanding Spring Data JPA causes:
- N+1 query problem (lazy loading outside transaction)
- Incorrect transaction boundaries (read operations in write tx)
- Page/Slice confusion (Page requires count query)
- Dirty checking issues on detached entities

## 2. Basic Meaning

Spring Data JPA provides a repository abstraction over JPA (Hibernate). Define an interface extending `JpaRepository`, and Spring generates the implementation at runtime.

**Key vocabulary:**
- **`JpaRepository<T, ID>`**: CRUD + JPA-specific methods (flush, deleteInBatch)
- **`CrudRepository<T, ID>`**: basic CRUD (save, findById, findAll, count, delete)
- **`PagingAndSortingRepository<T, ID>`**: pagination + sorting
- **Query methods**: `findByFieldName`, `findByFieldContaining`, `countByField`
- **`@Query`**: custom JPQL or native SQL
- **`@Modifying`**: for UPDATE/DELETE queries
- **`@Transactional`**: transaction management
- **`Page<T>`**: paginated result with total count
- **`Slice<T>`**: paginated result without count (lighter)
- **`Specification<T>`**: dynamic query criteria
- **`EntityGraph`**: eager fetch plan (workaround for N+1)

What it is NOT: Not for complex joins (use JPQL). Not a replacement for JDBC batch operations. Not for stored procedures (use @Procedure).

## 3. Real Code / Real Example

```java
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

// === ENTITY ===
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "full_name")
    private String fullName;
    
    private Integer age;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    
    // getters/setters omitted for brevity
}

// === REPOSITORY ===
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Query method — Spring generates implementation
    Optional<User> findByEmail(String email);
    
    List<User> findByAgeGreaterThanEqual(int age);
    
    List<User> findByFullNameContainingIgnoreCase(String name);
    
    // Pagination
    Page<User> findByAgeGreaterThanEqual(int age, Pageable pageable);
    
    // Custom JPQL
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
    List<User> findByEmailDomain(@Param("domain") String domain);
    
    // Native SQL
    @Query(value = "SELECT * FROM users WHERE age > :minAge", nativeQuery = true)
    List<User> findOlderThanNative(@Param("minAge") int minAge);
    
    // Update query
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.fullName = :name WHERE u.id = :id")
    int updateUserName(@Param("id") Long id, @Param("name") String name);
    
    // Delete query
    @Modifying
    @Query("DELETE FROM User u WHERE u.email = :email")
    int deleteByEmail(@Param("email") String email);
}

// === SERVICE ===
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User createUser(String email, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setFullName(name);
        return userRepository.save(user); // returns managed entity
    }
    
    @Transactional(readOnly = true)
    public Page<User> getUsersByAge(int minAge, Pageable pageable) {
        return userRepository.findByAgeGreaterThanEqual(minAge, pageable);
    }
    
    @Transactional(readOnly = true)
    public User getUserWithOrders(Long userId) {
        // BAD: N+1 — accessing orders outside transaction triggers lazy load
        User user = userRepository.findById(userId).orElseThrow();
        // user.getOrders().size(); // LazyInitializationException!
        return user;
    }
}

// === CONTROLLER ===
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) { this.userService = userService; }
    
    @GetMapping
    public Page<User> getUsers(
            @RequestParam(defaultValue = "18") int minAge,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersByAge(minAge, PageRequest.of(page, size, Sort.by("fullName")));
    }
    
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest req) {
        return userService.createUser(req.email(), req.fullName());
    }
}
```

Expected output (simplified):
```
// POST /api/users {"email":"a@b.com","fullName":"Alice"}
// Response: {"id":1, "email":"a@b.com", "fullName":"Alice", "age":null}

// GET /api/users?minAge=18&page=0&size=10
// Response: {"content":[...], "totalElements":100, "totalPages":10, ...}
```

## 4. What Happens Internally

**Repository proxy creation:**
1. Spring scans for `@Repository` interfaces extending `JpaRepository`
2. For each, creates a JDK dynamic proxy backed by `SimpleJpaRepository`
3. `SimpleJpaRepository` has `EntityManager` injected
4. Query methods parsed at startup: `findByEmail` → `SELECT u FROM User u WHERE u.email = ?1`

**`save(entity)` flow:**
1. Check if entity is new (id == null or version == null)
2. If new: `entityManager.persist(entity)` — INSERT
3. If existing: `entityManager.merge(entity)` — SELECT + INSERT/UPDATE
4. Returns MANAGED entity

**`findById(id)` flow:**
1. Check persistence context (first-level cache)
2. If not found, execute SELECT query
3. Return entity (MANAGED) or Optional.empty()

**Transaction management:**
- `@Transactional(readOnly=true)`: Sets Hibernate flush mode to MANUAL — no dirty checking overhead
- `@Transactional`: auto-opens EntityManager, commits on method exit, rolls back on RuntimeException
- `Open Session in View` (OSIV): EntityManager stays open for the whole HTTP request — enables lazy loading in views (controversial)

## 5. Tricky Interview Cases

**Case 1 — `save()` vs `saveAndFlush()`**
```java
// save(): may batch INSERTs, flush at transaction commit
userRepository.save(user1);
userRepository.save(user2);
// No INSERT yet — batched

// saveAndFlush(): immediately flush to DB
userRepository.saveAndFlush(user1); // INSERT NOW
```

**Case 2 — `getReferenceById()` (proxy)**
```java
User proxy = userRepository.getReferenceById(1L);
// No SELECT executed! Proxy created.
System.out.println(proxy.getEmail()); // LAZY LOAD — SELECT now
```
Useful for setting foreign key without loading entity:
```java
order.setUser(userRepository.getReferenceById(userId)); // no SELECT
```

**Case 3 — Page vs Slice**
```java
// Page: executes count query + data query
Page<User> page = userRepository.findAll(PageRequest.of(0, 10));
long total = page.getTotalElements(); // requires count query

// Slice: only data query with +1 extra row to determine if more
Slice<User> slice = userRepository.findSliceByAge(18, PageRequest.of(0, 10));
boolean hasNext = slice.hasNext(); // no count query
```

**Case 4 — save() with existing ID (merge vs persist)**
```java
User detached = new User();
detached.setId(1L); // existing ID
detached.setFullName("Updated");
userRepository.save(detached); // merge() — SELECT + UPDATE
```

**Case 5 — @Modifying clearAutomatically**
```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
@Query("UPDATE User u SET u.fullName = :name WHERE u.id = :id")
int updateUserName(...);
```
`clearAutomatically=true`: clears persistence context after execution (avoids stale data).

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| N+1 queries from lazy loading | 1 + N SELECTs | Use `@EntityGraph`, `JOIN FETCH`, or `@Query` |
| `save()` outside transaction | TransientPropertyValueException | Add `@Transactional` |
| `findById()` inside loop | N+1 | Use `findAllById(ids)` or `IN` query |
| Modifying entity without `@Transactional` | No dirty checking | Add `@Transactional` |
| Returning entity from service | Lazy loading in controller | Use DTO or ensure transaction |
| `Page` when only need "has more" | Unnecessary count query | Use `Slice` instead |

## 7. Production Usage

**Auditing with `@CreatedDate`, `@LastModifiedDate`:**
```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
    @CreatedBy private String createdBy;
}
```

**Specifications for dynamic queries:**
```java
public class UserSpecs {
    public static Specification<User> ageGreaterThan(int age) {
        return (root, query, cb) -> cb.greaterThan(root.get("age"), age);
    }
    public static Specification<User> nameContains(String name) {
        return (root, query, cb) -> cb.like(root.get("fullName"), "%" + name + "%");
    }
}
List<User> users = userRepository.findAll(
    Specification.where(UserSpecs.ageGreaterThan(18)).and(UserSpecs.nameContains("Ali"))
);
```

**Pagination with sorting:**
```java
Pageable pageable = PageRequest.of(0, 20, Sort.by(
    Sort.Order.asc("fullName"),
    Sort.Order.desc("age")
));
Page<User> page = userRepository.findAll(pageable);
```

## 8. Advanced Details

- **`@Lock`**: Pessimistic/optimistic locking on query methods.
- **`@QueryHints`**: Hibernate-specific hints (cache mode, fetch size).
- **`@EntityGraph`**: Define fetch plan via `attributePaths` — avoids N+1 without writing JOIN FETCH.
- **Projections**: Interface-based DTO projections (`interface UserSummary { String getEmail(); }`).
- **`QuerydslPredicateExecutor`**: Type-safe dynamic queries with QueryDSL.
- **`JpaSpecificationExecutor`**: Criteria API-based dynamic queries.
- **Batch operations**: `deleteAllInBatch()` generates single DELETE, not SELECT+DELETE per entity.
- **`@Procedure`**: Call stored procedures.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between `CrudRepository`, `JpaRepository`, and `PagingAndSortingRepository`?
A: `CrudRepository<T,ID>`: basic CRUD (save, findById, findAll, delete). `PagingAndSortingRepository<T,ID>` extends CrudRepository with pagination/sorting methods. `JpaRepository<T,ID>` extends PagingAndSortingRepository with JPA-specific methods (flush, deleteInBatch, findAll(Sort)).

### Intermediate
Q: How does Spring Data JPA generate query methods? How does `findByEmailAndAgeGreaterThan` work?
A: Spring parses method name at startup: `findBy` → SELECT, `Email` → `WHERE email = ?`, `And` → AND, `AgeGreaterThan` → `age > ?`. It generates the JPQL using EntityManager + Criteria API. The implementation is cached; no reflection cost on runtime.

### Senior
Q: What is the N+1 query problem in JPA? How do you detect and fix it?
A: N+1: 1 query to fetch parent entities + N queries to fetch lazy associations for each parent.

Detection: Hibernate's `spring.jpa.properties.hibernate.generate_statistics=true` shows query count. Or use datasource-proxy.

Fixes:
1. `@EntityGraph(attributePaths = {"orders"})` on repository method
2. `@Query("SELECT u FROM User u JOIN FETCH u.orders")`
3. `@BatchSize(size=10)` on collection — batches N queries into N/10
4. DTO projections — fetch only needed fields

### Tricky
Q: You have `@Transactional` on a service method. Inside, you call `userRepository.save(user)` then `throw new RuntimeException()`. What happens to the INSERT?
A: The INSERT is rolled back because `@Transactional` defaults to rollback on RuntimeException. No data persisted. If you catch the exception inside the method, the transaction commits unless you mark it for rollback with `TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()`.

## 10. Final 30-Second Answer

Spring Data JPA = repository abstraction over JPA/Hibernate. Extend `JpaRepository<T,ID>` → get CRUD, pagination, sorting, query methods, @Query. **Query methods**: `findByField`, `countByField`, `deleteByField`. **@Query**: custom JPQL or native SQL. **`save()`**: persist (new) or merge (existing). **`@Transactional`**: open transaction, commit on return, rollback on RuntimeException. **N+1**: lazy loading → 1+N queries. **Fix**: Join Fetch, EntityGraph, BatchSize. **Page**: count query + data. **Slice**: data only, no count.