# ⚖️ Problem 35: Online Judge (LeetCode-like)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: LeetCode, HackerRank, Codeforces  
> **Est. Time**: 120 min | **Patterns**: Strategy, Factory, Observer, Sandbox

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Judge code submissions automatically."

**What the interviewer tests**:
```
1. Can you sandbox user code? (Security: no file system access)
2. Can you run test cases? (Input → Expected Output)
3. Can you handle multiple languages? (Java, Python, C++)
4. Can you detect infinite loops? (Timeout)
5. Can you compare outputs? (Exact match, float tolerance)
```

### Step 2: The "Aha!" Moment

The key insight: **Docker containers for isolation + Judge pattern.**

```
Submission Flow:
  User submits code
    ↓
  1. Save code
  2. Create Docker container (sandbox)
  3. Compile (if needed)
  4. Run with test case #1
     - Capture stdout
     - Compare with expected
     - Check runtime (timeout = 2s)
  5. Repeat for all test cases
  6. Destroy container
    ↓
  Result: Accepted / Wrong Answer / Time Limit Exceeded / Runtime Error

Security:
  - No network access
  - Limited CPU/memory
  - No file system access
  - 2-second timeout kills process
```

### Step 3: How to scale?

```
Queue System:
  Submissions → Queue → Workers (100 judges)

Worker:
  - Pulls submission from queue
  - Spins up Docker container
  - Runs tests
  - Updates database
  - Garbage collects

Priority:
  - Premium users: high priority queue
  - Contest submissions: highest priority
  - Regular: normal queue
```

---

## 💻 Core Implementation

```java
package com.judge;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: JudgeService evaluates code submissions.
 * 
 * Flow:
 * 1. Receive submission
 * 2. Queue for processing
 * 3. Worker picks up, creates sandbox
 * 4. Compiles (if needed)
 * 5. Runs test cases
 * 6. Returns result
 */
public class JudgeService {
    
    private final SubmissionQueue queue;
    private final List<JudgeWorker> workers;
    private final LanguageHandlerFactory handlerFactory;
    private final int workerCount = 10;

    public JudgeService() {
        this.queue = new SubmissionQueue();
        this.handlerFactory = new LanguageHandlerFactory();
        this.workers = new ArrayList<>();
        
        // Start workers
        for (int i = 0; i < workerCount; i++) {
            JudgeWorker worker = new JudgeWorker(queue, handlerFactory);
            workers.add(worker);
            new Thread(worker).start();
        }
    }

    /**
     * INTUITION: Submit code for judging.
     * 
     * 1. Validate code (no malicious imports)
     * 2. Create submission record
     * 3. Add to queue
     * 4. Return submission ID (for polling)
     */
    public String submitCode(String userId, String problemId, String code, 
                             String language) {
        
        // Get problem details (test cases)
        Problem problem = getProblem(problemId);
        
        // Create submission
        Submission submission = new Submission(
            UUID.randomUUID().toString(),
            userId,
            problemId,
            code,
            language,
            problem.getTimeLimit(),
            problem.getMemoryLimit()
        );
        
        // Add to queue
        queue.add(submission);
        
        return submission.getId();
    }

    /**
     * Get submission result (polling).
     */
    public SubmissionResult getResult(String submissionId) {
        // In production: database lookup
        return null;
    }

    private Problem getProblem(String problemId) {
        // Mock problem
        return new Problem(problemId, "Two Sum", 2.0, 256);
    }

    public void shutdown() {
        for (JudgeWorker worker : workers) {
            worker.shutdown();
        }
    }
}
```

```java
package com.judge;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * INTUITION: JudgeWorker picks submissions from queue and judges them.
 * 
 * Runs in background thread.
 */
public class JudgeWorker implements Runnable {
    
    private final SubmissionQueue queue;
    private final LanguageHandlerFactory handlerFactory;
    private volatile boolean running = true;

    public JudgeWorker(SubmissionQueue queue, LanguageHandlerFactory handlerFactory) {
        this.queue = queue;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Submission submission = queue.poll(1, TimeUnit.SECONDS);
                if (submission != null) {
                    judge(submission);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * INTUITION: Judge a submission.
     * 
     * 1. Get language handler (Java/Python/C++)
     * 2. Compile code
     * 3. Run each test case
     * 4. Aggregate results
     */
    private void judge(Submission submission) {
        System.out.println("Judging submission " + submission.getId());
        
        LanguageHandler handler = handlerFactory.getHandler(submission.getLanguage());
        Problem problem = getProblem(submission.getProblemId());
        
        List<TestCaseResult> results = new ArrayList<>();
        Verdict finalVerdict = Verdict.ACCEPTED;
        
        try {
            // Step 1: Compile
            if (!handler.compile(submission.getCode())) {
                finalVerdict = Verdict.COMPILATION_ERROR;
                submission.setResult(finalVerdict, 0, results);
                return;
            }
            
            // Step 2: Run test cases
            for (TestCase testCase : problem.getTestCases()) {
                TestCaseResult result = runTest(submission, handler, testCase);
                results.add(result);
                
                if (result.getVerdict() != Verdict.ACCEPTED) {
                    finalVerdict = result.getVerdict();
                    break;  // Stop on first failure
                }
            }
            
        } catch (Exception e) {
            finalVerdict = Verdict.RUNTIME_ERROR;
        } finally {
            // Cleanup
            handler.cleanup();
        }
        
        // Update submission
        submission.setResult(finalVerdict, results.size(), results);
    }

    private TestCaseResult runTest(Submission submission, LanguageHandler handler, 
                                    TestCase testCase) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Run with timeout
            String actualOutput = handler.run(testCase.getInput(), submission.getTimeLimit());
            
            long runtime = System.currentTimeMillis() - startTime;
            
            // Check timeout
            if (runtime > submission.getTimeLimit() * 1000) {
                return new TestCaseResult(Verdict.TIME_LIMIT_EXCEEDED, null, actualOutput, runtime);
            }
            
            // Check memory (simplified)
            // Compare output
            boolean passed = compareOutput(actualOutput, testCase.getExpectedOutput());
            
            return new TestCaseResult(
                passed ? Verdict.ACCEPTED : Verdict.WRONG_ANSWER,
                testCase.getExpectedOutput(),
                actualOutput,
                runtime
            );
            
        } catch (TimeoutException e) {
            return new TestCaseResult(Verdict.TIME_LIMIT_EXCEEDED, testCase.getExpectedOutput(), 
                                    "Timeout", submission.getTimeLimit() * 1000);
        } catch (Exception e) {
            return new TestCaseResult(Verdict.RUNTIME_ERROR, testCase.getExpectedOutput(), 
                                    e.getMessage(), 0);
        }
    }

    private boolean compareOutput(String actual, String expected) {
        // Normalize whitespace
        return actual.trim().equals(expected.trim());
    }

    public void shutdown() {
        this.running = false;
    }
}
```

```java
package com.judge;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: LanguageHandler abstracts different languages.
 * 
 * Strategy pattern: each language has its own compile/run logic.
 */
interface LanguageHandler {
    boolean compile(String code);
    String run(String input, double timeLimit) throws TimeoutException;
    void cleanup();
}

/**
 * Java handler.
 */
class JavaHandler implements LanguageHandler {
    @Override
    public boolean compile(String code) {
        System.out.println("Compiling Java code...");
        // In production: javac in Docker container
        return true;  // Assume compilation succeeds
    }

    @Override
    public String run(String input, double timeLimit) throws TimeoutException {
        System.out.println("Running Java code with input: " + input);
        // In production: java -cp . Solution < input
        return "output";
    }

    @Override
    public void cleanup() {
        System.out.println("Cleaning up Java files...");
    }
}

/**
 * Python handler.
 */
class PythonHandler implements LanguageHandler {
    @Override
    public boolean compile(String code) {
        System.out.println("Validating Python syntax...");
        // Python is interpreted, no compilation needed
        return true;
    }

    @Override
    public String run(String input, double timeLimit) throws TimeoutException {
        System.out.println("Running Python code with input: " + input);
        // In production: python3 solution.py < input
        return "output";
    }

    @Override
    public void cleanup() {
        // Python: no cleanup needed
    }
}

/**
 * Factory for language handlers.
 */
class LanguageHandlerFactory {
    private final Map<String, LanguageHandler> handlers;

    LanguageHandlerFactory() {
        this.handlers = new HashMap<>();
        handlers.put("java", new JavaHandler());
        handlers.put("python", new PythonHandler());
        handlers.put("cpp", new CppHandler());
    }

    LanguageHandler getHandler(String language) {
        LanguageHandler handler = handlers.get(language.toLowerCase());
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
        return handler;
    }
}

class CppHandler implements LanguageHandler {
    @Override
    public boolean compile(String code) {
        System.out.println("Compiling C++ code...");
        return true;
    }

    @Override
    public String run(String input, double timeLimit) throws TimeoutException {
        System.out.println("Running C++ code with input: " + input);
        return "output";
    }

    @Override
    public void cleanup() {
        System.out.println("Cleaning up C++ binaries...");
    }
}
```

```java
package com.judge;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: Submission represents a user's code submission.
 */
public class Submission {
    private final String id;
    private final String userId;
    private final String problemId;
    private final String code;
    private final String language;
    private final double timeLimit;
    private final int memoryLimit;
    private final LocalDateTime submittedAt;
    
    private Verdict verdict;
    private int passedTestCases;
    private List<TestCaseResult> results;

    public Submission(String id, String userId, String problemId, String code, 
                     String language, double timeLimit, int memoryLimit) {
        this.id = id;
        this.userId = userId;
        this.problemId = problemId;
        this.code = code;
        this.language = language;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.submittedAt = LocalDateTime.now();
        this.verdict = Verdict.PENDING;
        this.results = new ArrayList<>();
    }

    public void setResult(Verdict verdict, int passed, List<TestCaseResult> results) {
        this.verdict = verdict;
        this.passedTestCases = passed;
        this.results = results;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getProblemId() { return problemId; }
    public String getCode() { return code; }
    public String getLanguage() { return language; }
    public double getTimeLimit() { return timeLimit; }
    public int getMemoryLimit() { return memoryLimit; }
    public Verdict getVerdict() { return verdict; }
    public int getPassedTestCases() { return passedTestCases; }
}

/**
 * Submission queue for load balancing.
 */
class SubmissionQueue {
    private final BlockingQueue<Submission> queue = new LinkedBlockingQueue<>();

    void add(Submission submission) {
        queue.offer(submission);
        System.out.println("Submission queued: " + submission.getId());
    }

    Submission poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    public int size() {
        return queue.size();
    }
}

class Problem {
    private final String problemId;
    private final String name;
    private final double timeLimit;  // seconds
    private final int memoryLimit;   // MB
    private final List<TestCase> testCases;

    public Problem(String problemId, String name, double timeLimit, int memoryLimit) {
        this.problemId = problemId;
        this.name = name;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.testCases = new ArrayList<>();
        // Add test cases
        testCases.add(new TestCase("3 4\n", "7"));
        testCases.add(new TestCase("5 6\n", "11"));
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public double getTimeLimit() { return timeLimit; }
    public int getMemoryLimit() { return memoryLimit; }
}

class TestCase {
    private final String input;
    private final String expectedOutput;

    public TestCase(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    public String getInput() { return input; }
    public String getExpectedOutput() { return expectedOutput; }
}

class TestCaseResult {
    private final Verdict verdict;
    private final String expected;
    private final String actual;
    private final long runtime;

    public TestCaseResult(Verdict verdict, String expected, String actual, long runtime) {
        this.verdict = verdict;
        this.expected = expected;
        this.actual = actual;
        this.runtime = runtime;
    }

    public Verdict getVerdict() { return verdict; }
}

enum Verdict {
    PENDING,
    ACCEPTED,
    WRONG_ANSWER,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    RUNTIME_ERROR,
    COMPILATION_ERROR
}
```

---

## ❓ Follow-up Questions

### Q1: "How to prevent malicious code?"
> "Docker sandbox: no network, read-only filesystem, CPU/memory limits. Run as non-root user. Use seccomp to block system calls."

### Q2: "How to handle flaky test cases?"
> "Retry 3 times. If passes once → accepted. If fails all → wrong answer. Mark as flaky, don't use in contests."

### Q3: "How to support interactive problems?"
> "Replace stdin/stdout with bidrectional pipe. Simulate judge program. Each input → process → output."

### Q4: "How to generate plagiarism report?"
> "Tokenize code into AST. Compare tree similarity. MOSS algorithm: find longest common subsequences."