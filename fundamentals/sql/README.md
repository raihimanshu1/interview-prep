# SQL & Relational Databases — Complete Deep Dive

## 1. Why This Concept Matters

SQL is the universal language for relational databases. Understanding indexing, query optimization, isolation levels, and database-specific features (PostgreSQL vs MySQL) is critical for every backend engineer. Interviewers test SQL across all levels — joins, aggregations, subqueries, window functions, indexing strategies, and database internals.

Misunderstanding SQL causes:
- Slow queries from missing indexes (full table scans)
- Deadlocks from incorrect isolation levels
- Phantom reads / non-repeatable reads
- Incorrect GROUP BY / HAVING usage
- N+1 from application-level joins

## 2. Basic Meaning

SQL (Structured Query Language) is used to query and manipulate relational databases. Relational databases store data in tables with relationships through foreign keys.

**Key vocabulary:**
- **SELECT**: retrieve data
- **JOIN (INNER, LEFT, RIGHT, FULL)**: combine tables
- **WHERE**: filter rows
- **GROUP BY**: aggregate rows
- **HAVING**: filter after aggregation
- **ORDER BY**: sort results
- **LIMIT/OFFSET**: pagination
- **Index**: data structure (B+Tree) for fast lookups
- **Primary key**: unique identifier for each row (clustered index)
- **Foreign key**: references primary key of another table
- **Normalization**: reduce redundancy (1NF, 2NF, 3NF)
- **ACID**: Atomicity, Consistency, Isolation, Durability
- **Transaction**: group of operations as a single unit
- **Isolation levels**: Read Uncommitted, Read Committed, Repeatable Read, Serializable
- **MVCC**: Multi-Version Concurrency Control (PostgreSQL, MySQL InnoDB)
- **Window functions**: `ROW_NUMBER()`, `RANK()`, `SUM() OVER`

What it is NOT: Not for unstructured data (use NoSQL). Not for real-time event streams (use Kafka). Not for full-text search at scale (use Elasticsearch).

## 3. Real Code / Real Example

```sql
-- === SCHEMA DESIGN ===
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    salary DECIMAL(10,2),
    department_id INTEGER REFERENCES departments(id),
    hire_date DATE NOT NULL
);

CREATE INDEX idx_employees_department ON employees(department_id);
CREATE INDEX idx_employees_salary ON employees(salary DESC);

-- === BASIC QUERIES ===
-- All employees with department name
SELECT e.name, d.name AS department, e.salary
FROM employees e
INNER JOIN departments d ON e.department_id = d.id
WHERE e.salary > 50000
ORDER BY e.salary DESC;

-- Aggregation: average salary per department
SELECT d.name, AVG(e.salary) AS avg_salary, COUNT(*) AS emp_count
FROM employees e
JOIN departments d ON e.department_id = d.id
GROUP BY d.name
HAVING AVG(e.salary) > 60000
ORDER BY avg_salary DESC;

-- === SUBQUERY ===
-- Employees earning more than department average
SELECT e.name, e.salary, e.department_id
FROM employees e
WHERE e.salary > (
    SELECT AVG(e2.salary) FROM employees e2 WHERE e2.department_id = e.department_id
);

-- === WINDOW FUNCTIONS ===
-- Rank employees by salary within department
SELECT name, department_id, salary,
    RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) AS salary_rank
FROM employees;

-- Running total of salaries
SELECT name, salary, 
    SUM(salary) OVER (ORDER BY hire_date) AS running_total
FROM employees;

-- === PAGINATION ===
SELECT * FROM employees ORDER BY id LIMIT 10 OFFSET 20;
-- For large offsets: keyset pagination (WHERE id > last_seen_id LIMIT 10)

-- === POSTGRESQL SPECIFIC (JSONB) ===
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    payload JSONB
);
CREATE INDEX ON events USING GIN(payload);
SELECT * FROM events WHERE payload @> '{"type": "click"}';

-- === MYSQL SPECIFIC (EXPLAIN ANALYZE) ===
EXPLAIN ANALYZE SELECT * FROM employees WHERE salary > 50000;
```

Expected behavior:
```
INNER JOIN: only employees with matching department
LEFT JOIN: all employees, NULL department if missing
GROUP BY with HAVING: filters groups after aggregation
Window function: computes per-partition without collapsing rows
```

## 4. What Happens Internally

**B+Tree index structure:**
- Leaf pages contain sorted data + pointers to rows
- Internal pages guide search (logarithmic depth)
- Range queries efficient: `WHERE salary BETWEEN 50000 AND 70000`
- Each index = separate B+Tree (non-clustered) or table data in leaf (clustered)

**Query execution plan (simplified):**
```
SELECT e.name, d.name FROM employees e JOIN departments d ON e.department_id = d.id WHERE e.salary > 50000
→
1. Seq Scan on employees (full table scan if no index on salary)
   OR Index Scan on idx_employees_salary (if index exists)
2. For each employee: Index Lookup on departments primary key (via idx_employees_department)
3. Nested Loop Join → Return results
```

**PostgreSQL vs MySQL InnoDB:**
- PostgreSQL: MVCC via tuple versioning. New row version = new tuple. Old versions in table until vacuumed.
- MySQL InnoDB: MVCC via undo log. Old versions stored in rollback segment. Purge thread cleans up.
- PostgreSQL: `VACUUM` required to reclaim space from dead tuples.
- MySQL: InnoDB automatically reclaims space (but undo logs can grow).

## 5. Tricky Interview Cases

**Case 1 — SELECT without ORDER BY**
```sql
SELECT * FROM employees;
```
Output: No guaranteed order. May vary across executions. Always use ORDER BY for deterministic results.

**Case 2 — NULL in WHERE clause**
```sql
SELECT * FROM employees WHERE salary > 50000;
-- NULL salary rows excluded (NULL > 50000 = UNKNOWN)
SELECT * FROM employees WHERE salary IS NULL;
-- Use IS NULL / IS NOT NULL
```

**Case 3 — COUNT(*) vs COUNT(column)**
```sql
SELECT COUNT(*) FROM employees; -- counts all rows
SELECT COUNT(salary) FROM employees; -- counts non-NULL salary values
-- Different results if salary has NULLs!
```

**Case 4 — GROUP BY without aggregate**
```sql
SELECT name, department_id FROM employees GROUP BY department_id; -- ERROR!
-- If name not in GROUP BY or aggregate, most DBs error (MySQL may return arbitrary value)
```

**Case 5 — Index not used**
```sql
SELECT * FROM employees WHERE UPPER(name) = 'ALICE'; -- function on column disables index!
-- Fix: Use expression index: CREATE INDEX ON employees(UPPER(name));
```

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| No index on JOIN column | Nested loop full scan | CREATE INDEX on foreign key column |
| `SELECT *` in production | Unnecessary data transfer | Select only needed columns |
| Pagination with large OFFSET | Scanning skipped rows | Keyset pagination (WHERE id > last_id) |
| Missing WHERE on UPDATE/DELETE | Updates all rows | Always verify WHERE clause |
| Implicit type conversion | Index not used | Match query types to column types |
| Lazy loading in application code | N+1 queries | Use JOIN in single query |

## 7. Production Usage

**Query optimization workflow:**
```sql
-- 1. Check slow query log
-- 2. EXPLAIN ANALYZE the query
-- 3. Look for: Seq Scan on large tables, high cost, missing index
-- 4. Add index: CREATE INDEX CONCURRENTLY idx_name ON table(column);
-- 5. Retest with EXPLAIN ANALYZE
```

**Connection pooling (HikariCP):**
```yaml
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000
```

## 8. Advanced Details

- **Covering index**: Includes all columns needed by query — no table access needed.
- **Partial index**: `CREATE INDEX ON employees(salary) WHERE salary > 100000` — smaller index.
- **`EXPLAIN (ANALYZE, BUFFERS)`** (PostgreSQL): Detailed execution analysis.
- **`OPTIMIZE TABLE`** (MySQL): Rebuild table and indexes.
- **`VACUUM ANALYZE`** (PostgreSQL): Reclaim space + update statistics.
- **Connection pooling**: Reuse DB connections (expensive to create). HikariCP is fastest.
- **Read replicas**: Route SELECT queries to replicas, writes to primary.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between INNER JOIN and LEFT JOIN?
A: INNER JOIN returns only rows with matching keys in both tables. LEFT JOIN returns all rows from left table, NULLs for right columns when no match.

### Intermediate
Q: What is an index? When would you create one? When does an index not help?
A: Index = data structure (B+Tree) for fast lookups. Create on columns used in WHERE, JOIN, ORDER BY. Not helpful for: small tables (full scan is faster), columns with few distinct values (low cardinality), columns frequently updated (index maintenance cost > benefit).

### Senior
Q: PostgreSQL vs MySQL — when would you choose each?
A: PostgreSQL: better for complex queries, JSONB, GIS, analytics, CTEs, window functions, stricter SQL compliance. MySQL: better for simple read-heavy workloads, replication ease, smaller operational overhead, wider hosting support. PostgreSQL is usually the better choice for modern applications.

### Tricky
Q: Your query is slow despite having an index. What do you check?
A: 1. Is the index being used? (EXPLAIN) 2. Is column function applied? (`WHERE UPPER(name)`) 3. Is the query selective enough? (index vs seq scan threshold) 4. Are statistics up to date? (`ANALYZE`) 5. Is the index covering? 6. Are there type mismatches? 7. Is the query returning too many rows? (index scan + table lookups may be slower than sequential scan)

## 10. Final 30-Second Answer

SQL = relational queries. **JOINs**: INNER (match), LEFT (all left), RIGHT (all right), FULL (all). **Indexing**: B+Tree for fast lookups, WHERE/JOIN/ORDER BY columns. **GROUP BY + HAVING**: aggregate, then filter groups. **Window functions**: ROW_NUMBER, RANK, SUM OVER (partition) — no row collapsing. **Isolation levels**: Read Committed (default PG), Repeatable Read (default MySQL InnoDB), Serializable. **MVCC**: concurrent reads don't block writes. **No `SELECT *`**, **no large OFFSET**, **EXPLAIN ANALYZE** for optimization. PostgreSQL for complexity, MySQL for simplicity.