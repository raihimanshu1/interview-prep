# 📝 Problem 18: Logger Framework

> **Difficulty**: ⭐⭐ | **Company Fit**: Any company - infrastructure question  
> **Est. Time**: 60 min | **Patterns**: Singleton, Observer, Chain of Responsibility

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a logging framework" — sounds simple but interviewers use this to test:
```
1. Can you handle MULTIPLE output destinations? (Console, File, Database, Network)
2. Can you handle LOG LEVELS? (DEBUG < INFO < WARN < ERROR)
3. Can you handle FORMATTING? (Plain text, JSON, XML)
4. Can you handle ASYNC logging? (Don't block the main thread)
```

### Step 2: The "Aha!" Moment

The key insight: **A logger is a pipeline.**

```
Message → Level Check → Format → Destination → Write
```

Each stage is INDEPENDENT. Level checking doesn't care about formatting. Formatting doesn't care about where it's written.

This screams **Chain of Responsibility** pattern.

### Step 3: How to make it extensible?

```
New log level? → Just add to the enum.
New destination? → Implement LogAppender interface and register.
New format? → Implement LogFormatter interface and register.
```

---

## 💻 Core Implementation

```java
package com.logger;

/**
 * INTUITION: Log levels in order of severity.
 * DEBUG < INFO < WARN < ERROR < FATAL
 * 
 * A logger set to INFO level will show INFO, WARN, ERROR, FATAL
 * but NOT DEBUG. This is "level inheritance" - higher levels include all below.
 */
public enum LogLevel {
    DEBUG(1),   // Detailed debugging information
    INFO(2),    // General operational information
    WARN(3),    // Potentially harmful situations
    ERROR(4),   // Error events that might still allow app to continue
    FATAL(5);   // Severe errors that cause app termination

    private final int level;

    LogLevel(int level) { this.level = level; }

    /**
     * Can this level be logged given the minimum threshold?
     * Example: If logger is set to WARN (level 3),
     *   - canLog(ERROR) → 4 >= 3 → true
     *   - canLog(DEBUG) → 1 >= 3 → false
     */
    public boolean canLog(LogLevel minimumLevel) {
        return this.level >= minimumLevel.level;
    }
}
```

```java
package com.logger;

/**
 * INTUITION: Logger is the main API that client code uses.
 * SINGLETON because we want ONE global logging configuration.
 * 
 * Usage:
 *   Logger.getInstance().info("User logged in: {}", userId);
 *   Logger.getInstance().error("Database connection failed", exception);
 */
public class Logger {
    private static volatile Logger instance;
    
    private LogLevel minimumLevel = LogLevel.DEBUG;
    private final List<LogAppender> appenders = new CopyOnWriteArrayList<>();
    private LogFormatter formatter = new PlainTextFormatter();

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    // --- Convenience methods ---
    public void debug(String message, Object... args) {
        log(LogLevel.DEBUG, formatMessage(message, args));
    }

    public void info(String message, Object... args) {
        log(LogLevel.INFO, formatMessage(message, args));
    }

    public void warn(String message, Object... args) {
        log(LogLevel.WARN, formatMessage(message, args));
    }

    public void error(String message, Object... args) {
        log(LogLevel.ERROR, formatMessage(message, args));
    }

    public void error(String message, Throwable throwable) {
        LogMessage logMessage = new LogMessage(
            LogLevel.ERROR, 
            message + " - " + throwable.getMessage(),
            Thread.currentThread().getName()
        );
        appendToAll(logMessage);
    }

    /**
     * INTUITION: Core logging logic.
     * 
     * Step 1: Check if this level is high enough to log
     * Step 2: If yes, send to ALL configured appenders
     * 
     * This is where the Chain of Responsibility kicks in -
     * each appender decides what to do with the message.
     */
    private void log(LogLevel level, String message) {
        if (!level.canLog(minimumLevel)) {
            return;  // This level is below our threshold - skip
        }

        LogMessage logMessage = new LogMessage(
            level, 
            message,
            Thread.currentThread().getName()
        );

        appendToAll(logMessage);
    }

    private void appendToAll(LogMessage message) {
        for (LogAppender appender : appenders) {
            appender.append(formatter.format(message));
        }
    }

    private String formatMessage(String template, Object... args) {
        if (args.length == 0) return template;
        return String.format(template.replace("{}", "%s"), args);
    }

    // --- Configuration ---
    public void setMinimumLevel(LogLevel level) { this.minimumLevel = level; }
    public void addAppender(LogAppender appender) { appenders.add(appender); }
    public void setFormatter(LogFormatter formatter) { this.formatter = formatter; }
}
```

```java
package com.logger;

/**
 * INTUITION: The LogAppender is the "destination" abstraction.
 * 
 * Chain of Responsibility pattern:
 * Each appender in the list processes the message.
 * A message can go to MULTIPLE destinations.
 * 
 * Examples:
 * - ConsoleAppender: prints to System.out
 * - FileAppender: writes to a log file
 * - DatabaseAppender: inserts into a log table
 * - NetworkAppender: sends to remote log aggregator
 */
public interface LogAppender {
    void append(String formattedMessage);
}

/**
 * Writes logs to the console.
 * Simple and fast - great for development.
 */
class ConsoleAppender implements LogAppender {
    @Override
    public void append(String formattedMessage) {
        System.out.println(formattedMessage);
    }
}

/**
 * Writes logs to a file.
 * Handles file rotation - when file gets too big, start a new one.
 */
class FileAppender implements LogAppender {
    private final String baseFileName;
    private final long maxFileSize;  // bytes
    private PrintWriter writer;
    private int currentFileIndex = 0;

    public FileAppender(String baseFileName, long maxFileSizeBytes) {
        this.baseFileName = baseFileName;
        this.maxFileSize = maxFileSizeBytes;
        openNewFile();
    }

    @Override
    public synchronized void append(String formattedMessage) {
        writer.println(formattedMessage);
        if (writer.checkError()) {
            openNewFile();  // File too big, rotate
        }
    }

    private void openNewFile() {
        if (writer != null) {
            writer.close();
        }
        try {
            String fileName = baseFileName + "." + currentFileIndex++ + ".log";
            writer = new PrintWriter(new FileWriter(fileName, true));
        } catch (IOException e) {
            System.err.println("Failed to open log file: " + e.getMessage());
        }
    }
}
```

```java
package com.logger;

/**
 * INTUITION: LogFormatter controls HOW the message looks.
 * 
 * Strategy Pattern - swap formatters without changing logger code.
 * 
 * Examples:
 * - PlainTextFormatter: "2024-01-15 10:30:00 [main] INFO - User logged in"
 * - JsonFormatter: {"timestamp":"...","level":"INFO","message":"..."}
 */
public interface LogFormatter {
    String format(LogMessage message);
}

class PlainTextFormatter implements LogFormatter {
    @Override
    public String format(LogMessage message) {
        return String.format("%s [%s] %s - %s",
            message.getTimestamp(),
            message.getThreadName(),
            message.getLevel(),
            message.getMessage()
        );
    }
}

class JsonFormatter implements LogFormatter {
    @Override
    public String format(LogMessage message) {
        return String.format(
            "{\"time\":\"%s\",\"thread\":\"%s\",\"level\":\"%s\",\"msg\":\"%s\"}",
            message.getTimestamp(),
            message.getThreadName(),
            message.getLevel(),
            escapeJson(message.getMessage())
        );
    }

    private String escapeJson(String s) {
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
```

```java
package com.logger;

/**
 * Value object that carries log information through the pipeline.
 * Immutable - once created, it doesn't change.
 */
public class LogMessage {
    private final LogLevel level;
    private final String message;
    private final String threadName;
    private final String timestamp;

    public LogMessage(LogLevel level, String message, String threadName) {
        this.level = level;
        this.message = message;
        this.threadName = threadName;
        this.timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
    }

    public LogLevel getLevel() { return level; }
    public String getMessage() { return message; }
    public String getThreadName() { return threadName; }
    public String getTimestamp() { return timestamp; }
}
```

```java
package com.logger;

/**
 * INTUITION: AsyncLogger wraps the main logger but processes
 * messages in a BACKGROUND THREAD.
 * 
 * WHY? Logging to file/database is SLOW.
 * If the main thread waits for every log write, performance suffers.
 * 
 * HOW IT WORKS:
 * 1. Main thread calls info() - message goes into a BlockingQueue
 * 2. Main thread continues immediately (doesn't wait)
 * 3. Background thread picks up messages from queue and writes them
 */
public class AsyncLogger {
    private final Logger logger = Logger.getInstance();
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AsyncLogger() {
        // Start the background worker
        executor.submit(() -> {
            while (true) {
                try {
                    Runnable task = queue.take();  // Blocks until message arrives
                    task.run();  // Write the log
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void info(String message) {
        queue.offer(() -> logger.info(message));  // Non-blocking!
    }

    public void error(String message, Throwable t) {
        queue.offer(() -> logger.error(message, t));
    }

    // Same for debug, warn, etc.

    public void shutdown() {
        executor.shutdown();
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to make logger thread-safe?"
> "Each LogAppender handles its own thread safety. ConsoleAppender doesn't need it (println is synchronized). FileAppender uses synchronized on append(). The Logger itself uses CopyOnWriteArrayList for appenders."

### Q2: "How to add MDC (Mapped Diagnostic Context)?"
> "Add a ThreadLocal<Map<String,String>> in Logger. Each thread can set context values (requestId, userId). The formatter reads these when creating the output string."

### Q3: "How to handle configuration reload without restart?"
> "Use a WatchService to monitor config file changes. When file changes, reload log levels and appenders atomically. Use ReentrantReadWriteLock to prevent race conditions during reload."

### Q4: "How to implement a database appender?"
> "Implement LogAppender. In append(), batch messages and insert via JDBC batch. Use a buffer - flush every 100 messages or every 5 seconds, whichever comes first. This avoids hammering the database."