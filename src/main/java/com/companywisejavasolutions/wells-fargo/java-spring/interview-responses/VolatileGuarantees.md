# Volatile Guarantees - Interview Response

## What Is It?

`volatile` guarantees visibility and ordering for a variable.

It does not guarantee atomicity for compound operations.

In simple terms:

```text
volatile makes latest writes visible across threads.
volatile does not make count++ thread-safe.
```

## Why It Matters

Without visibility guarantees, one thread may update a value and another thread may keep reading an old cached value.

Common example:

```java
class Worker {
    private volatile boolean running = true;

    void stop() {
        running = false;
    }

    void runLoop() {
        while (running) {
            // do work
        }
    }
}
```

Here, `volatile` is useful because:

```text
one thread writes running=false
another thread sees it and exits
```

## What Volatile Guarantees

```text
Visibility
Happens-before relationship
Prevents certain instruction reordering around the volatile access
```

Meaning:

```text
When thread A writes to a volatile variable,
thread B reading that variable sees the latest value and prior writes that happened-before it.
```

## What Volatile Does Not Guarantee

Broken example:

```java
class Counter {
    private volatile int count = 0;

    void increment() {
        count++;
    }
}
```

Why broken:

```text
count++ is read + add + write
Two threads can read the same old value
One increment is lost
```

Fix with `AtomicInteger`:

```java
class Counter {
    private final AtomicInteger count = new AtomicInteger();

    void increment() {
        count.incrementAndGet();
    }
}
```

Or use a lock when multiple fields must change together.

## Good Use Cases

```text
stop flag
configuration reference swap
single-writer status flag
safe publication of immutable reference
```

## Bad Use Cases

```text
counters
bank balance updates
check-then-act logic
multi-field invariants
```

## Production Example

Safe:

```java
private volatile RiskConfig currentConfig;

public RiskDecision evaluate(Transaction tx) {
    RiskConfig config = currentConfig;
    return rules.evaluate(tx, config);
}
```

If `RiskConfig` is immutable, swapping the reference can be safe.

Unsafe:

```java
volatile BigDecimal balance;
balance = balance.subtract(amount);
```

Money movement needs transactions/locking, not `volatile`.

## Compatibility And Rollout Angle

For `volatile`, API evolution is usually not the main topic.

Where compatibility matters:

```text
Changing a config refresh mechanism
Changing concurrency behavior in a shared library
Changing runtime defaults that affect existing services
```

Semantic versioning:

```text
MAJOR -> breaking concurrency behavior in a public library
MINOR -> new safe configuration mechanism
PATCH -> internal visibility bug fix
```

## Related Patterns

- Atomic classes
- Immutable object
- Thread confinement
- Locking
- Copy-on-write configuration

## Follow-Up Interview Questions

### Is volatile enough for counters?

```text
No. Use AtomicInteger, LongAdder, or locks.
```

### Is volatile enough for a stop flag?

```text
Yes, usually.
```

### Difference between volatile and synchronized?

```text
volatile gives visibility.
synchronized gives visibility plus mutual exclusion.
```

## Interview Answer

In an interview, I would say: `volatile` is useful when I need visibility across threads, such as a stop flag or an immutable config reference swap. It creates a happens-before relationship so other threads see the latest write. But it does not make compound operations atomic, so `volatile int count; count++` is still broken. For counters I would use `AtomicInteger` or `LongAdder`; for multi-field business invariants like account balance updates, I would use locks or database transactions.
