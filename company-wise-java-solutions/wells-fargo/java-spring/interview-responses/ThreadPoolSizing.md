# Thread Pool Sizing - Interview Response

## What Is It?

Thread pool sizing means choosing how many worker threads and queued tasks a service should allow.

It is not:

```text
Just set 100 threads
Just use cached thread pool
Just increase threads when latency rises
```

It depends on:

```text
CPU cores
Blocking time
Downstream capacity
Queue size
Timeouts
Rejection policy
Latency SLO
```

## Why It Matters

In a Spring Boot service, a bad pool can create:

```text
High latency
Memory growth
Database overload
Retry storms
Thread starvation
Outage amplification
```

Example:

```text
Fraud API allows 20 concurrent calls.
Service creates 200 fraud-check threads.
Result: downstream overload, timeouts, retries, more overload.
```

## CPU-Bound Work

CPU-bound example:

```text
Encryption
Risk scoring in memory
Large calculation
JSON compression
```

Rule of thumb:

```text
threads ~= number of CPU cores
```

If machine has:

```text
8 cores
```

Start around:

```text
8 to 10 worker threads
```

More threads usually add context switching, not throughput.

## I/O-Bound Work

I/O-bound example:

```text
Database call
HTTP call
Kafka call
File/network I/O
```

Formula often discussed:

```text
threads = cores * (1 + waitTime / computeTime)
```

Example:

```text
cores = 8
compute = 20 ms
wait = 80 ms

threads = 8 * (1 + 80/20)
        = 8 * 5
        = 40
```

But this is only a starting point.

## Downstream Limit Is The Real Limit

If DB pool size is:

```text
20 connections
```

then 100 database worker threads do not help.

They mostly create:

```text
connection waiting
timeouts
queue buildup
more memory use
```

## Java Example

```java
ThreadPoolExecutor fraudPool = new ThreadPoolExecutor(
    16,
    16,
    0L,
    TimeUnit.MILLISECONDS,
    new ArrayBlockingQueue<>(500),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

Why this is safer:

```text
Fixed max threads
Bounded queue
Explicit rejection/backpressure behavior
```

## Spring Example

```java
@Bean
ThreadPoolTaskExecutor fraudTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(16);
    executor.setMaxPoolSize(16);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("fraud-check-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
}
```

## Queue Strategy

Unbounded queue:

```text
Looks stable at first
Hides overload
Consumes memory
Increases latency
```

Bounded queue:

```text
Makes overload visible
Allows rejection/backpressure
Protects memory
```

## Rejection Policies

```text
AbortPolicy -> fail fast
CallerRunsPolicy -> slow caller down
DiscardPolicy -> dangerous unless loss is acceptable
Custom policy -> return 429/503 or route to fallback
```

## Metrics To Watch

```text
active thread count
queue depth
task wait time
task execution time
rejection count
downstream latency
timeout count
CPU usage
```

## Forward / Backward Compatibility

Thread pool sizing affects runtime behavior and operational contracts.

Safe rollout:

```text
Expose pool size through config
Roll out gradually
Keep old defaults available
Monitor queue/rejection metrics
Rollback quickly if latency worsens
```

Semantic versioning:

```text
MAJOR -> changing public timeout/rejection behavior
MINOR -> adding configurable pool settings
PATCH -> tuning internal defaults with no client-visible behavior change
```

## Related Patterns

- Bulkhead
- Backpressure
- Circuit breaker
- Producer-consumer
- Load shedding

## Follow-Up Interview Questions

### Why not use cached thread pool?

```text
It can grow too aggressively under traffic spikes and overload downstream systems.
```

### What if queue is always full?

```text
Either traffic exceeds capacity, downstream is slow, pool is too small,
or timeout/retry policy is wrong. Measure before increasing threads.
```

### How do you size a payment posting pool?

```text
Start from downstream DB/partner capacity, not only CPU cores.
For financial writes, correctness and bounded overload matter more than raw concurrency.
```

## Interview Answer

In an interview, I would say: I size thread pools based on the type of work and the real bottleneck. CPU-bound work should stay close to core count, while blocking I/O can use more threads but must respect downstream limits like DB connection pool size and partner API capacity. I would always use bounded queues, explicit timeouts, a clear rejection policy, and metrics for active threads, queue depth, latency, and rejections. In production, thread pool sizing is a bulkhead decision: it protects the rest of the system from one slow dependency.
