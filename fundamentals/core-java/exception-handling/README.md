# Exception Handling — Complete Deep Dive

## 1. Why This Concept Matters

Exception handling separates normal logic from error handling, enabling robust applications that fail gracefully. In production, unhandled exceptions crash services, lose requests, and expose stack traces to users. Understanding checked vs unchecked exceptions, try-with-resources, and custom exception design is essential. Interviewers test this because it reveals your understanding of Java's error model, resource management, and API design principles.

Misunderstanding exception handling causes:
- Swallowed exceptions hiding root causes
- Resource leaks from unclosed streams
- Tight coupling from exception-specific catch blocks
- Poor API design exposing internal implementation

## 2. Basic Meaning

Java's exception hierarchy:
- `Throwable` (root)
  - `Exception` (recoverable)
    - `IOException` (checked)
    - `RuntimeException` (unchecked: NPE, IllegalArgumentException, etc.)
  - `Error` (JVM-level, unrecoverable: OutOfMemoryError, StackOverflowError)

Key vocabulary:
- **Checked exception**: compiler enforces handling (catch or declare). Extends `Exception`.
- **Unchecked exception**: compiler does NOT enforce handling. Extends `RuntimeException`.
- **try-with-resources**: automatic resource management (AutoCloseable)
- **Suppressed exception**: secondary exception when close() also throws
- **Exception chaining**: `initCause()` or constructor passing cause
- **Stack trace**: call stack at throw point
- **`finally`**: always executes unless JVM exits or thread interrupted

What it is NOT: Checked exceptions are not "worse" than unchecked — they document recoverable failures. `finally` is not the only cleanup option (try-with-resources preferred). Catching `Exception` is almost always wrong.

## 3. Real Code / Real Example

```java
import java.io.*;
import java.nio.file.*;

public class ExceptionHandlingDemo {
    public static void main(String[] args) {
        // === BASIC TRY-CATCH ===
        try {
            int result = divide(10, 0);
            System.out.println("Result: " + result); // never reached
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero: " + e.getMessage());
        }

        // === MULTIPLE CATCH BLOCKS ===
        try {
            String data = readFile("config.json");
            System.out.println("Config: " + data);
        } catch (FileNotFoundException e) {
            System.out.println("Config file not found");
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        }

        // === TRY-WITH-RESOURCES (Java 7+) ===
        try (BufferedReader reader = Files.newBufferedReader(Path.of("data.txt"));
             PrintWriter writer = new PrintWriter("output.txt")) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line.toUpperCase());
            }
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
        // reader and writer AUTOMATICALLY closed here

        // === CUSTOM EXCEPTIONS ===
        try {
            validateAge(15);
        } catch (InvalidAgeException e) {
            System.out.println("Validation failed: " + e.getMessage());
        }

        // === EXCEPTION CHAINING ===
        try {
            processOrder("ORD-123");
        } catch (OrderException e) {
            System.out.println("Order failed: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }

        // === FINALLY (legacy resource cleanup) ===
        legacyCleanup();

        // === THROWS DECLARATION ===
        try {
            riskyOperation();
        } catch (Exception e) {
            System.out.println("Risky op failed: " + e);
        }
    }

    static int divide(int a, int b) { return a / b; }

    static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Path.of(path)));
    }

    static void validateAge(int age) throws InvalidAgeException {
        if (age < 18) throw new InvalidAgeException("Age must be 18+, got: " + age);
    }

    static void processOrder(String orderId) throws OrderException {
        try {
            // simulate failure
            throw new IOException("Database connection failed");
        } catch (IOException e) {
            throw new OrderException("Failed to process " + orderId, e); // chaining
        }
    }

    static void legacyCleanup() {
        Connection conn = null;
        try {
            conn = getConnection();
            // use connection
        } catch (SQLException e) {
            System.out.println("DB error");
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { /* swallow or log */ }
            }
        }
    }

    static void riskyOperation() throws Exception {
        throw new Exception("Something went wrong");
    }

    // Dummy classes for demo
    static class InvalidAgeException extends Exception {
        public InvalidAgeException(String msg) { super(msg); }
    }
    static class OrderException extends Exception {
        public OrderException(String msg, Throwable cause) { super(msg, cause); }
    }
    static class Connection { void close() throws SQLException {} }
    static class SQLException extends Exception {}
    static Connection getConnection() throws SQLException { return new Connection(); }
}
```

Expected output:
```
Cannot divide by zero: / by zero
Config file not found  (or Config: <content> or File error: ...)
Validation failed: Age must be 18+, got: 15
Order failed: Failed to process ORD-123
Cause: java.io.IOException: Database connection failed
Risky op failed: java.lang.Exception: Something went wrong
```

## 4. What Happens Internally

**Exception object creation:**
When `throw new NullPointerException("msg")` executes:
1. Allocate `NullPointerException` object on heap
2. Capture current stack trace via `fillInStackTrace()` — walks stack frames, stores method names, line numbers
3. Begin exception propagation: unwind stack, run finally blocks

**Stack trace capture:**
```java
// Simplified Throwable.fillInStackTrace()
public synchronized Throwable fillInStackTrace() {
    StackTraceElement[] stack = new StackTraceElement[frames.length];
    for (int i = 0; i < frames.length; i++) {
        stack[i] = new StackTraceElement(
            frames[i].getClassName(),
            frames[i].getMethodName(),
            frames[i].getFileName(),
            frames[i].getLineNumber()
        );
    }
    this.stackTrace = stack;
    return this;
}
```
`fillInStackTrace()` is expensive — it captures entire call stack. Avoid in hot paths.

**Propagation:**
1. Exception thrown at method `A()`
2. Current stack frame unwound
3. Search enclosing try-catch in caller (`B()`)
4. If found matching catch: handle
5. If no catch: propagate to `B()`'s caller, repeat
6. If reaches `main()` uncaught: JVM prints stack trace, exits

**try-with-resources:**
```java
try (Resource r = ...) { ... }
// Compiler generates:
Resource r = ...;
try {
    ...
} finally {
    if (r != null) r.close(); // suppressed exceptions handled
}
```
For multiple resources: closed in reverse order (last opened, first closed).

**Suppressed exceptions:**
If `body()` throws AND `close()` throws:
1. `close()` exception is added as suppressed to `body()` exception
2. `body()` exception propagates
3. Caller can retrieve suppressed via `e.getSuppressed()`

## 5. Tricky Interview Cases

**Case 1 — Return vs finally**
```java
int tricky() {
    try { return 1; }
    finally { return 2; }
}
```
Output: `2`
Explanation: `finally` executes AFTER `return` expression evaluated but BEFORE method actually returns. `finally`'s `return` overrides `try`'s return.

**Case 2 — Exception in finally**
```java
int important() {
    try { return 1; }
    finally { throw new RuntimeException("finally"); }
}
```
Output: Throws `RuntimeException("finally")`. Return value `1` is lost.
Explanation: Exception in finally overrides normal return or previous exception.

**Case 3 — Catch rethrow loses stack trace**
```java
void badCatch() {
    try { risky(); }
    catch (Exception e) { throw new ServiceException("failed"); } // loses cause!
}
void goodCatch() {
    try { risky(); }
    catch (Exception e) { throw new ServiceException("failed", e); } // preserves cause
}
```
Output: bad version loses original stack trace. Good version chains.

**Case 4 — Multi-catch with unrelated exceptions**
```java
try { ... }
catch (IOException | ClassNotFoundException e) { ... } // OK: both checked, unrelated
catch (IOException | RuntimeException e) { ... } // COMPILE ERROR
```
Output: Compile error on second catch.
Explanation: `RuntimeException` is superclass of `ArithmeticException`, `NullPointerException`, etc. If caught as `RuntimeException`, it would also catch `IOException`'s subclass — compiler disallows.

**Case 5 — try-finally without catch**
```java
void process() {
    try { criticalSection(); }
    finally { cleanup(); }
}
```
Output: Exceptions from `criticalSection()` propagate after `cleanup()` runs.
Explanation: `try-finally` ensures cleanup ALWAYS runs, exception still propagates. No catch = no handling.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| `catch (Exception e)` | Swallows everything, hides bugs | Catch specific exceptions |
| Empty catch block | Silently ignores errors | At least log the exception |
| Swallowing `InterruptedException` | Loses interrupt status | Restore: `Thread.currentThread().interrupt()` |
| `finally` with return | Suppressed original exception/return | Don't return from finally |
| Not using try-with-resources | Leaks resources (files, sockets) | Use try-with-resources for AutoCloseable |
| Throwing from `finally` | Masks original exception | Let finally only cleanup |
| Checked exception overuse | API clutter, callers forced to catch | Prefer unchecked for programming errors |
| `printStackTrace()` to stdout | In production, invisible in logs | Use logging framework (SLF4J/Logback) |

## 7. Production Usage

**Spring exception translation:**
```java
// Spring translates SQLException → DataAccessException
try {
    jdbcTemplate.update("INSERT ...");
} catch (DuplicateKeyException e) {
    // Spring-specific unchecked exception
    log.warn("Duplicate entry", e);
}
```
`@Repository` annotation enables exception translation via `PersistenceExceptionTranslationPostProcessor`.

**Custom exception hierarchy:**
```java
// Base for all application exceptions
public class AppException extends RuntimeException {
    private final ErrorCode code;
    public AppException(ErrorCode code, String msg) { super(msg); this.code = code; }
}

// Specific exceptions
public class ValidationException extends AppException {
    public ValidationException(String field, String reason) {
        super(ErrorCode.VALIDATION_FAILED, field + ": " + reason);
    }
}

// Global handler
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getCode(), e.getMessage()));
    }
}
```

**try-with-resources for JDBC:**
```java
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = conn.prepareStatement("SELECT ...");
     ResultSet rs = ps.executeQuery()) {
    while (rs.next()) {
        // process
    }
} catch (SQLException e) {
    // All three resources auto-closed in finally block
}
```

## 8. Advanced Details

- **Stack trace generation is expensive:** `fillInStackTrace()` walks stack frames. Consider `new Exception(msg, e, false, false)` (Java 7+) to skip stack trace for exceptions that won't be logged.
- **Synchronized throw:** `fillInStackTrace()` is `synchronized` on `Throwable`. Under high concurrency, exception creation can cause contention.
- **`Throwable.addSuppressed()`:** Java 7+. If `close()` throws during try-with-resources, original exception is preserved via suppression.
- **`Exception` vs `Error`:** Never catch `Error` (OutOfMemoryError, StackOverflowError). JVM is in unstable state.
- **`try-with-resources` requirements:** Resource must implement `AutoCloseable` (Java 7+) or `Closeable`. `final` or effectively final variables only.
- ** suppressed exceptions visibility:** `e.getSuppressed()` returns array. Multiple resources can cause multiple suppressed exceptions.
- **`Thread.stop()` deprecated:** Asynchronous exception delivery can leave objects in inconsistent state. Use interrupt + cooperative cancellation.
- **` StackWalker` (Java 9+):** More efficient than `fillInStackTrace()` for stack trace inspection. Lazy, stack-walking API.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between checked and unchecked exceptions?
A: Checked exceptions extend `Exception` (but not `RuntimeException`). The compiler forces you to either catch them or declare them with `throws`. Examples: `IOException`, `SQLException`. Unchecked exceptions extend `RuntimeException`. The compiler does not enforce handling. Examples: `NullPointerException`, `IllegalArgumentException`.

### Intermediate
Q: What is try-with-resources? How does it differ from try-finally?
A: Try-with-resources (Java 7+) automatically closes resources implementing `AutoCloseable`. Resources are closed in reverse order of declaration. If `close()` throws, it's suppressed (exception from try body takes precedence). Works with multiple resources without nested try-finally blocks.

### Senior
Q: You are designing a REST API. Should you create custom checked or unchecked exceptions for business rule violations (e.g., `InsufficientBalanceException`)? Justify your choice.
A: **Unchecked (extends RuntimeException).**

Business rule violations are not "recoverable I/O failures" — they are expected outcomes of user actions. Making them checked forces API clients to catch them everywhere, cluttering code. The client cannot "fix" insufficient balance; they can only inform the user.

```java
// Unchecked — client chooses whether to catch
public class InsufficientBalanceException extends RuntimeException {
    private final BigDecimal available;
    public InsufficientBalanceException(BigDecimal available) {
        super("Insufficient balance: " + available);
        this.available = available;
    }
}

// Controller translates to HTTP 409
@ExceptionHandler(InsufficientBalanceException.class)
public ResponseEntity<Error> handle(InsufficientBalanceException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new Error(e.getMessage()));
}
```

Checked exceptions suitable for: I/O errors (`IOException`), DB errors (`SQLException`), network timeouts — external failures the caller might want to retry.

### Tricky
Q: You see `catch (Exception e) { throw new RuntimeException(e); }` in production code. Is this acceptable? Under what conditions would this be a bad practice?
A: It is acceptable in entry points (main, servlet filter) where you want unchecked propagation to a global handler. But it is bad practice when:
1. Catching `Exception` hides programming errors (NPE, ClassCastException) that should fail fast and be fixed
2. Wrapping checked exceptions in `RuntimeException` without semantic meaning loses the ability to distinguish recoverable vs programming errors
3. Stack trace shows `RuntimeException` wrapper instead of actual root cause in monitoring

Better:
```java
// Map specific exceptions to domain-specific unchecked
catch (SQLException e) { throw new DataAccessException(e); }
catch (IOException e) { throw new FileProcessingException(e); }
// Let programming errors (NPE, etc.) propagate naturally
```

## 10. Final 30-Second Answer

Checked exceptions (compiler-enforced): `IOException`, `SQLException`. Unchecked (no enforcement): `RuntimeException` and subclasses. **Prefer unchecked for business logic**, checked for external I/O. **Try-with-resources** for AutoCloseable (auto-closes). `finally` for non-AutoCloseable cleanup. **Never catch `Exception`** — too broad. **Never swallow exceptions** — at minimum log. Custom exceptions: extend `RuntimeException` for business rules, add `ErrorCode`. Chain exceptions with cause constructor. `printStackTrace()` = debug only; use logging framework in production.