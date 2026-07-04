# JDBC — Complete Deep Dive

## 1. Why This Concept Matters

JDBC (Java Database Connectivity) is the lowest-level database access API in Java. Every ORM — Hibernate, MyBatis, Spring Data JPA — sits on top of JDBC. When you call `userRepository.save(user)`, somewhere in the stack, a JDBC `PreparedStatement` executes an INSERT. Understanding JDBC means understanding connection management, statement lifecycle, batch operations, result set streaming, and transaction boundaries at the wire level. When production systems hit connection pool exhaustion, SQL injection vulnerabilities, or batch insert performance bottlenecks, the root cause is almost always at the JDBC layer. Interviewers test JDBC for senior roles because it reveals whether you understand what ORMs abstract away — and when that abstraction leaks.

Misunderstanding JDBC causes:
- Connection leaks that crash production databases (exhausted pool)
- SQL injection vulnerabilities from string concatenation in Statement
- Application memory OOM from unbounded ResultSet fetching
- Batch inserts that run 100x slower than necessary (single INSERT per round trip)
- Transaction boundaries that silently auto-commit every statement

## 2. Basic Meaning

JDBC is the standard Java API for connecting to relational databases. It defines interfaces (Connection, Statement, PreparedStatement, ResultSet) that database vendors implement via JDBC drivers. The driver translates JDBC calls into the database's native wire protocol (e.g., PostgreSQL's v3 protocol, MySQL's protocol).

**Key components:**
- **DriverManager**: legacy connection factory. Loads driver via `Class.forName()`. Not used in production.
- **DataSource**: modern connection factory. Returns pooled connections. Used by Spring Boot (HikariCP).
- **Connection**: represents a TCP session to the database. Holds transaction state, session variables.
- **Statement**: static SQL — parameters concatenated into string. **SQL injection risk.**
- **PreparedStatement**: parameterized SQL (`?` placeholders). Precompiled by DB. **SQL injection safe.**
- **CallableStatement**: stored procedure calls. `{call procedure_name(?)}`.
- **ResultSet**: cursor over query results. Scrollable, updatable, or forward-only (default).
- **ResultSetMetaData**: column count, names, types from query result.
- **Batch**: `addBatch()` + `executeBatch()` — multiple statements in one network round trip.
- **Transaction**: `setAutoCommit(false)` → operations → `commit()` or `rollback()`.

**What it is NOT:**
- Not an ORM — no automatic object-relational mapping.
- Not thread-safe — `Connection` is NOT safe for multi-threaded access.
- Not connection-aware — the pool wraps Connection; `close()` returns it to pool, doesn't close TCP.

## 3. Real Code / Real Example

```java
import java.sql.*;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.*;

public class JdbcDemo {
    private static DataSource dataSource;
    
    // === HIKARICP CONNECTION POOL SETUP ===
    public static void init() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("app_user");
        config.setPassword("secret");
        config.setMaximumPoolSize(10);        // Max connections in pool
        config.setMinimumIdle(2);              // Keep 2 always ready
        config.setConnectionTimeout(30000);    // Wait 30s before timeout
        config.setIdleTimeout(600000);         // Close idle after 10min
        config.setMaxLifetime(1800000);        // Max lifetime 30min
        config.setConnectionTestQuery("SELECT 1"); // Validation query
        
        dataSource = new HikariDataSource(config);
    }
    
    // === 1. BASIC QUERY (find by ID) ===
    public static User findById(long id) throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users WHERE id = ?";
        
        // try-with-resources auto-closes Connection, PreparedStatement, ResultSet
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);  // Set parameter — type-safe
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));              // By column name
                    user.setName(rs.getString("name"));         // String
                    user.setEmail(rs.getString("email"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
                return null;  // Not found
            }
        }
        // Connection automatically returned to pool (not closed!)
    }
    
    // === 2. INSERT WITH GENERATED KEY ===
    public static long createUser(String name, String email) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, 
                     Statement.RETURN_GENERATED_KEYS)) {  // Request auto-generated keys
            
            ps.setString(1, name);
            ps.setString(2, email);
            int affected = ps.executeUpdate();
            
            if (affected == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);  // Returns auto-generated ID
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }
    
    // === 3. BATCH INSERT (100x faster than individual inserts) ===
    public static void batchInsert(List<User> users) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);  // Start transaction for batch
            
            int count = 0;
            for (User user : users) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.addBatch();          // Buffer statement in batch
                count++;
                
                // Execute batch every 500 records to avoid memory bloat
                if (count % 500 == 0) {
                    int[] results = ps.executeBatch();  // Send batch to DB
                    conn.commit();                       // Commit this chunk
                    // Check results for failures
                    for (int r : results) {
                        if (r == Statement.EXECUTE_FAILED) {
                            throw new SQLException("Batch insert failed at record " + count);
                        }
                    }
                }
            }
            
            // Execute remaining records
            int[] results = ps.executeBatch();
            conn.commit();
            System.out.println("Inserted " + users.size() + " users in batch");
        }
    }
    
    // === 4. TRANSACTION (transfer with rollback) ===
    public static void transfer(long fromId, long toId, BigDecimal amount) 
            throws SQLException {
        
        String debitSql = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        String creditSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);  // BEGIN TRANSACTION
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            
            try (PreparedStatement debit = conn.prepareStatement(debitSql);
                 PreparedStatement credit = conn.prepareStatement(creditSql)) {
                
                // Debit from account
                debit.setBigDecimal(1, amount);
                debit.setLong(2, fromId);
                debit.setBigDecimal(3, amount);  // Check sufficient balance
                int debitUpdated = debit.executeUpdate();
                
                if (debitUpdated == 0) {
                    throw new SQLException("Insufficient funds in account " + fromId);
                }
                
                // Credit to account
                credit.setBigDecimal(1, amount);
                credit.setLong(2, toId);
                int creditUpdated = credit.executeUpdate();
                
                if (creditUpdated == 0) {
                    throw new SQLException("Target account " + toId + " not found");
                }
                
                conn.commit();  // COMMIT TRANSACTION
                System.out.println("Transfer " + amount + " from " + fromId + " to " + toId);
                
            } catch (SQLException e) {
                conn.rollback();  // ROLLBACK — both operations undone
                System.err.println("Transfer failed, rolled back: " + e.getMessage());
                throw e;
            }
        }
    }
    
    // === 5. CALLABLE STATEMENT (stored procedure) ===
    public static void processUserReport(long userId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call generate_report(?)}")) {
            
            cs.setLong(1, userId);
            boolean hasResults = cs.execute();
            
            // Process result sets if procedure returns data
            if (hasResults) {
                try (ResultSet rs = cs.getResultSet()) {
                    while (rs.next()) {
                        System.out.println(rs.getString("report_data"));
                    }
                }
            }
        }
    }
    
    // === 6. STREAMING LARGE RESULTS ===
    public static void processLargeResultSet() throws SQLException {
        String sql = "SELECT * FROM audit_logs ORDER BY created_at";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            
            // PostgreSQL: fetch one row at a time (no memory bloat)
            ps.setFetchSize(1000);  // Fetch 1000 rows per network round trip
            // For very large results, set to Integer.MIN_VALUE (PostgreSQL streaming)
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String action = rs.getString("action");
                    processRow(id, action);  // Process one row at a time
                }
            }
        }
    }
    
    // === 7. METADATA (dynamic column handling) ===
    public static List<Map<String, Object>> executeDynamicQuery(String sql) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = meta.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
        }
        return results;
    }
}
```

Expected output:
```
findById(1L) → User{id=1, name="Alice", email="a@b.com"}
createUser("Bob", "b@c.com") → 1002 (auto-generated ID)
batchInsert(10000 users) → "Inserted 10000 users in batch" (takes ~200ms vs 10s for individual)
transfer(1, 2, 100) → "Transfer 100 from 1 to 2" or "Transfer failed, rolled back: ..."
processLargeResultSet() → processes 1M rows using < 10MB heap
```

## 4. What Happens Internally

**Connection lifecycle in HikariCP:**
```
Spring Boot start → HikariCP creates minimumIdle(2) connections
                  → Each connection executes "SELECT 1" to validate
                  → Connections sit idle in pool

Request comes in:
  1. HikariPool.getConnection():
     - If idle connection available: return it immediately
     - If pool not full (= maxPoolSize): create new connection
     - If pool full and all busy: wait up to connectionTimeout(30s)
     - If timeout expires: throw SQLException (pool exhausted)
  
  2. Application uses connection (query/tx)
  
  3. Connection.close():
     - HikariCP intercepts: return connection to pool
     - Reset autoCommit, isolation level, clear warnings
     - If connection is stale (closed by DB, network issue): discard, create new

  Connection idle > idleTimeout(10min): close it (return to TCP level)
  Connection lifetime > maxLifetime(30min): close it (avoid DB proxy timeouts)
```

**PreparedStatement execution flow:**
```
String sql = "SELECT * FROM users WHERE id = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setLong(1, 100L);
ResultSet rs = ps.executeQuery();

Under the hood (PostgreSQL protocol):

1. conn.prepareStatement(sql):
   → PostgreSQL: sends PARSE message (query text + parameter types)
   → Server: parses, validates, creates prepared statement handle
   → "Named statement" stored on server until connection closes or deallocate
  
2. ps.setLong(1, 100L):
   → Sets parameter in client-side buffer. No network activity.

3. ps.executeQuery():
   → PostgreSQL: sends BIND message (parameter values)
   → Server: binds parameters, creates portal (execution state)
   → PostgreSQL: sends DESCRIBE message (column metadata)
   → Server: returns RowDescription (column count, names, types)
   → PostgreSQL: sends EXECUTE message (fetch rows)
   → Server: returns DataRow messages for each row
   → PostgreSQL: sends CLOSE message for portal
   
4. rs.next():
   → Client reads from buffered DataRow messages
   → If all rows consumed: triggers another EXECUTE for next batch
   → Each batch is fetchSize rows (default: 500)
```

**SQL injection contrast:**
```java
// UNSAFE — Statement with concatenation:
Statement stmt = conn.createStatement();
String sql = "SELECT * FROM users WHERE email = '" + userInput + "'";
ResultSet rs = stmt.executeQuery(sql);
// If userInput = "a@b.com' OR '1'='1"
// SQL becomes: SELECT * FROM users WHERE email = 'a@b.com' OR '1'='1'
// Returns ALL users — data breach!

// SAFE — PreparedStatement:
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM users WHERE email = ?");
ps.setString(1, userInput);
// Parameter value is LITERAL DATA, never SQL syntax
// Even if userInput = "a@b.com' OR '1'='1"
// It's treated as: WHERE email = "a@b.com' OR '1'='1"
// No rows returned (no matching email)
```

## 5. Tricky Interview Cases

**Case 1 — Connection leak from unclosed resources**
```java
public void leakyQuery() throws SQLException {
    Connection conn = dataSource.getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM users");
    // Processing...
    rs.close();   // OK
    stmt.close(); // OK
    // conn.close() MISSING! Connection never returned to pool.
    // After 10 calls: pool exhausted → application hangs on getConnection()
}
```
Fix: Always use try-with-resources or ensure `finally` block closes everything.
```java
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    // Auto-close in reverse order: rs → ps → conn
}
```

**Case 2 — Auto-commit behavior**
```java
Connection conn = dataSource.getConnection();
// By default: autoCommit = true (EACH statement = separate transaction)
PreparedStatement ps1 = conn.prepareStatement("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
ps1.executeUpdate(); // IMMEDIATELY COMMITTED!

// Application crashes here...

PreparedStatement ps2 = conn.prepareStatement("UPDATE accounts SET balance = balance + 100 WHERE id = 2");
ps2.executeUpdate(); // Never reached!
// Result: $100 permanently lost!
```
Fix: Always `conn.setAutoCommit(false)` for multi-statement operations, then `conn.commit()` or `conn.rollback()`.

**Case 3 — Generated keys not available**
```java
// WRONG: Generated keys not requested
PreparedStatement ps = conn.prepareStatement("INSERT INTO users (name) VALUES (?)");
ps.setString(1, "Alice");
ps.executeUpdate();
ResultSet keys = ps.getGeneratedKeys(); // EMPTY! Not requested!
```
Fix: Pass `Statement.RETURN_GENERATED_KEYS` flag:
```java
PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
```

**Case 4 — Connection pool exhaustion**
```
Scenario: 10 threads, pool max=10
Thread 1: gets conn → starts slow query (5s)
Thread 2: gets conn → starts slow query (5s)
...
Thread 10: gets conn → starts slow query (5s)
Thread 11: getConnection() → WAITS (30s timeout) → SQLException!
All 10 connections busy for 5 seconds. Queue builds up.
```
Root cause: Connection pool too small for concurrent slow queries. Fix: increase pool size, optimize queries, use async processing.

**Case 5 — Fetch size and memory**
```java
// BAD: fetches ALL rows into memory
PreparedStatement ps = conn.prepareStatement("SELECT * FROM billion_row_table");
ResultSet rs = ps.executeQuery(); // Default fetch size = ALL rows!
while (rs.next()) { /* process */ } // OOM after loading everything

// GOOD: streaming
PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
ps.setFetchSize(1000); // Fetch 1000 rows at a time
ResultSet rs = ps.executeQuery();
while (rs.next()) { /* process 1000-row chunks */ }
```

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Not closing Connection/Statement/ResultSet | Connection leak → pool exhaustion → application crash | Always try-with-resources |
| String concatenation in SQL (Statement) | SQL injection — data breach | Always PreparedStatement with ? placeholders |
| No batch for mass inserts | N network round trips (1000x slower) | `addBatch()` + `executeBatch()` |
| Default autoCommit=true for transactions | Partial updates on crash — data corruption | `setAutoCommit(false)` + commit/rollback |
| Fetching ResultSet with no limit | OOM for large tables | Set fetchSize, use LIMIT/OFFSET in SQL |
| Not checking executeUpdate return value | Silent failures (0 rows updated) | Check affected rows, throw if 0 |
| Using DriverManager in production | No connection pooling | Use HikariCP DataSource |
| Calling close() on pooled connection | Might close TCP if not wrapped correctly | HikariCP intercepts close() — safe |

## 7. Production Usage

**Spring Boot HikariCP configuration:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://primary:5432/mydb
    username: ${DB_USER}
    password: ${DB_PASS}
    hikari:
      maximum-pool-size: 20        # Rule of thumb: (peak_connections * latency) / query_time
      minimum-idle: 5
      connection-timeout: 5000      # 5s max wait for connection
      idle-timeout: 300000          # 5min idle → close
      max-lifetime: 1200000         # 20min max lifetime
      connection-test-query: SELECT 1
      pool-name: MyAppPool
```

**Health check for database connectivity:**
```java
@Endpoint(id = "database")
public class DatabaseHealthEndpoint {
    private final DataSource dataSource;
    
    @ReadOperation
    public Map<String, Object> check() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            return Map.of("status", "UP", "database", "available");
        } catch (SQLException e) {
            return Map.of("status", "DOWN", "error", e.getMessage());
        }
    }
}
```

**Read/write split with multiple DataSources:**
```java
@Configuration
public class DataSourceConfig {
    @Bean @Primary @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() { return DataSourceBuilder.create().build(); }
    
    @Bean @ConfigurationProperties("spring.datasource.replica")
    public DataSource replicaDataSource() { return DataSourceBuilder.create().build(); }
    
    @Bean
    public RoutingDataSource routingDataSource() {
        return new RoutingDataSource(primaryDataSource(), replicaDataSource());
    }
}
```

## 8. Advanced Details

- **PostgreSQL multiple hosts**: `jdbc:postgresql://host1:5432,host2:5432/mydb` — failover support.
- **MySQL rewriteBatchedStatements**: `jdbc:mysql://localhost:3306/db?rewriteBatchedStatements=true` — converts batch INSERT INTO single multi-row INSERT for 10x batch improvement.
- **PreparedStatement pooling**: Some connection pools cache PreparedStatement objects (pgbouncer, some app servers). Avoid for one-shot queries. Use for repeated queries.
- **LOB streaming**: `ps.setBinaryStream()`, `ps.setCharacterStream()` for large BLOBs/CLOBs without loading into memory.
- **Connection validation**: `connection-test-query` verifies connection is alive before use. HikariCP uses `connectionTestQuery` or `isValid()` (Java 8+).
- **Driver types**: Type 4 (pure Java, direct socket to DB) is standard. Type 2 (JNI, native DB library) rare today.
- **Network timeout**: `socketTimeout` controls how long JDBC waits for DB response. Default: infinite. Set to 30s to detect DB hangs.

## 9. Interview Questions And Answers

### Beginner
Q: What is JDBC? What are its core interfaces?
A: JDBC is the Java API for database access. Core interfaces: Connection (database session), Statement (static SQL), PreparedStatement (parameterized SQL, SQL injection safe), CallableStatement (stored procedures), ResultSet (query results), DataSource (connection factory). PreparedStatement is the most important — it prevents SQL injection by separating SQL from data.

### Intermediate
Q: How do batch inserts work in JDBC? Why are they faster than individual inserts?
A: `addBatch()` queues prepared statements in a buffer on the client side. `executeBatch()` sends ALL queued statements in a single network round trip to the database. Individual inserts send N round trips. For 10,000 inserts, this is ~10ms (batch) vs ~5000ms (individual). PostgreSQL and MySQL support multi-row INSERT under the hood. `rewriteBatchedStatements=true` for MySQL converts batch INSERT INTO `INSERT INTO t VALUES (1),(2),(3)...` — even faster.

### Senior
Q: Your application is experiencing intermittent "Connection is not available, request timed out after 30000ms" errors. The database is healthy. What do you investigate?
A: Connection pool exhaustion. Steps:
1. Check HikariCP metrics: `activeConnections`, `idleConnections`, `pendingThreads`, `timeoutTotal`.
2. Identify what's holding connections: slow queries (> 1s), connection leaks, or transaction boundaries too wide.
3. Check `pg_stat_activity` (PostgreSQL) / `SHOW PROCESSLIST` (MySQL) for active connections from your app.
4. If connections are held for long periods: look for missing `close()` in catch blocks, or `@Transactional` methods doing slow external API calls.
5. Fix: add connection leak detection: `spring.datasource.hikari.leak-detection-threshold=60000` — logs stack trace of connection holders.
6. Long-term: reduce query time, add connection pool monitoring, ensure `@Transactional(timeout = 5)` for long operations.

### Tricky
Q: You need to export 10 million rows to CSV without crashing the application. The naive `SELECT * FROM table` + `rs.next()` loop crashes with OOM. What do you do?
A: Use streaming with cursor:
```java
// PostgreSQL:
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM large_table", 
    ResultSet.TYPE_FORWARD_ONLY, 
    ResultSet.CONCUR_READ_ONLY);
ps.setFetchSize(1000); // Fetch 1000 rows per network packet
// For true streaming: setFetchSize(Integer.MIN_VALUE) — PostgreSQL streams one row at a time

try (ResultSet rs = ps.executeQuery();
     BufferedWriter writer = Files.newBufferedWriter(Paths.get("export.csv"))) {
    while (rs.next()) {
        writer.write(rs.getLong("id") + "," + rs.getString("name") + "\n");
    }
}
```
This uses constant memory (~1MB) regardless of table size. Never loads all rows into heap. Each `rs.next()` fetches the next batch from the database cursor.

## 10. Final 30-Second Answer

JDBC = Java's database access layer beneath all ORMs. **Connection** (from HikariCP pool), **PreparedStatement** (parameterized, SQL injection safe), **ResultSet** (cursor, use fetchSize for large results). **Always**: try-with-resources (no leaks), `setAutoCommit(false)` + commit/rollback (transactions), `addBatch()` + `executeBatch()` (inserts). **Never**: string concatenation in SQL (injection risk), missing close() (pool exhaustion), default autoCommit (partial writes). HikariCP is the default connection pool — configure pool size, timeout, and leak detection. For streaming: setFetchSize, forward-only ResultSet.