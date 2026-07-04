# ⏰ Problem 62: Job Scheduler (Cron-like)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any backend company  
> **Est. Time**: 90 min | **Patterns**: Observer, Strategy, Singleton

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a job scheduler for running tasks at specific times."

**What the interviewer tests**:
```
1. Can you schedule jobs? (Cron-like syntax)
2. Can you run jobs at specific times? (Fire at 2 AM)
3. Can you handle recurring jobs? (Every day, every Monday)
4. Can you handle failures? (Retry, DLQ)
```

### Step 2: The "Aha!" Moment

The key insight: **Scheduler = priority queue + timer wheel.**

```
JOB TYPES:
1. One-time: Run at 2026-06-15 10:00 AM
2. Recurring: Every minute/hour/day/cron
3. Delayed: Run after 30 seconds

CRON EXPRESSION:
  "0 0 2 * * *" = Every day at 2 AM
  "0 0 * * 1" = Every Monday at midnight
  "*/5 * * * *" = Every 5 minutes
  
PRIORITY:
  Higher priority jobs run first
```

### Step 3: How to handle millions of jobs?

```
DATA STRUCTURES:
1. PriorityQueue: Sort by next fire time
2. DelayQueue: Java's built-in delayed queue
3. Timer Wheel: O(1) insertion for fixed delays
  
SCALING:
  - Shard by job type or user
  - In-memory + persistence
  - Leader election for single scheduler
```

---

## 💻 Core Implementation

```java
package com.scheduler;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: JobScheduler schedules and executes jobs.
 */
public class JobScheduler {
    
    private final PriorityQueue<ScheduledJob> jobQueue;
    private final Map<String, ScheduledJob> jobs;
    private final ExecutorService executor;
    private final ScheduledExecutorService ticker;
    private volatile boolean isRunning;
    private final Object lock = new Object();

    public JobScheduler(int workerCount) {
        this.jobQueue = new PriorityQueue<>(Comparator.comparingLong(ScheduledJob::getNextFireTime));
        this.jobs = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(workerCount);
        this.ticker = Executors.newSingleThreadScheduledExecutor();
        this.isRunning = true;
        
        // Start ticker (checks every second)
        ticker.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * INTUITION: Schedule a one-time job.
     */
    public synchronized String schedule(Runnable job, long delayMs) {
        String jobId = UUID.randomUUID().toString();
        long nextFireTime = System.currentTimeMillis() + delayMs;
        
        ScheduledJob scheduled = new ScheduledJob(
            jobId, job, nextFireTime, JobType.ONE_TIME
        );
        
        jobs.put(jobId, scheduled);
        jobQueue.offer(scheduled);
        
        return jobId;
    }

    /**
     * INTUITION: Schedule recurring job.
     */
    public synchronized String scheduleRecurring(Runnable job, long periodMs) {
        String jobId = UUID.randomUUID().toString();
        long nextFireTime = System.currentTimeMillis() + periodMs;
        
        ScheduledJob scheduled = new ScheduledJob(
            jobId, job, nextFireTime, JobType.RECURRING, periodMs
        );
        
        jobs.put(jobId, scheduled);
        jobQueue.offer(scheduled);
        
        return jobId;
    }

    /**
     * INTUITION: Cancel job.
     */
    public synchronized boolean cancel(String jobId) {
        ScheduledJob job = jobs.remove(jobId);
        if (job != null) {
            job.cancel();
            jobQueue.remove(job);
            return true;
        }
        return false;
    }

    /**
     * Ticker: check for jobs to fire.
     */
    private void tick() {
        synchronized (lock) {
            while (isRunning) {
                ScheduledJob job = jobQueue.peek();
                
                if (job == null || job.getNextFireTime() > System.currentTimeMillis()) {
                    break;  // No jobs ready
                }
                
                jobQueue.poll();
                
                if (!job.isCancelled()) {
                    // Execute job
                    executor.submit(() -> {
                        try {
                            job.getTask().run();
                        } catch (Exception e) {
                            System.err.println("Job " + job.getId() + " failed: " + e.getMessage());
                            job.incrementRetry();
                        }
                    });
                }
                
                // Reschedule if recurring
                if (job.getType() == JobType.RECURRING && !job.isCancelled()) {
                    job.setNextFireTime(System.currentTimeMillis() + job.getPeriod());
                    jobQueue.offer(job);
                } else {
                    jobs.remove(job.getId());
                }
            }
        }
    }

    public void shutdown() {
        isRunning = false;
        ticker.shutdown();
        executor.shutdown();
    }
}

/**
 * Scheduled job.
 */
class ScheduledJob {
    private final String id;
    private final Runnable task;
    private long nextFireTime;
    private final JobType type;
    private final long period;
    private volatile boolean cancelled;
    private int retryCount;

    ScheduledJob(String id, Runnable task, long nextFireTime, JobType type) {
        this(id, task, nextFireTime, type, 0);
    }

    ScheduledJob(String id, Runnable task, long nextFireTime, 
                 JobType type, long period) {
        this.id = id;
        this.task = task;
        this.nextFireTime = nextFireTime;
        this.type = type;
        this.period = period;
    }

    public String getId() { return id; }
    public Runnable getTask() { return task; }
    public long getNextFireTime() { return nextFireTime; }
    public void setNextFireTime(long time) { this.nextFireTime = time; }
    public JobType getType() { return type; }
    public long getPeriod() { return period; }
    
    public void cancel() { this.cancelled = true; }
    public boolean isCancelled() { return cancelled; }
    
    public void incrementRetry() { this.retryCount++; }
    public int getRetryCount() { return retryCount; }
}

enum JobType {
    ONE_TIME, RECURRING
}

/**
 * Cron expression parser.
 */
class CronExpression {
    private final String expression;
    private final int minute;
    private final int hour;
    private final int dayOfMonth;
    private final int month;
    private final int dayOfWeek;

    CronExpression(String expression) {
        this.expression = expression;
        String[] parts = expression.split(" ");
        this.minute = parseField(parts[0], 0, 59);
        this.hour = parseField(parts[1], 0, 23);
        this.dayOfMonth = parseField(parts[2], 1, 31);
        this.month = parseField(parts[3], 1, 12);
        this.dayOfWeek = parseField(parts[4], 0, 6);
    }

    private int parseField(String field, int min, int max) {
        if ("*".equals(field)) return min;
        return Integer.parseInt(field);
    }

    boolean matches(Calendar cal) {
        return cal.get(Calendar.MINUTE) == minute &&
               cal.get(Calendar.HOUR_OF_DAY) == hour &&
               cal.get(Calendar.DAY_OF_MONTH) == dayOfMonth &&
               cal.get(Calendar.MONTH) + 1 == month &&
               cal.get(Calendar.DAY_OF_WEEK) - 1 == dayOfWeek;
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle persisted jobs?"
> "Store in DB. Load on startup. Update next fire time after execution."

### Q2: "How to handle job dependencies?"
> "DAG of jobs. Execute in topological order. Retry downstream on failure."

### Q3: "How to handle misfires?"
> "Fire immediately if missed. Skip if too old. Configure policy."

### Q4: "How to scale across nodes?"
> "Distributed lock. Partition by shard. Consistent hashing."