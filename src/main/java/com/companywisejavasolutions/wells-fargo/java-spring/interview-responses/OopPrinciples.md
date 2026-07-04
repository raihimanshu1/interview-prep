# OOP Principles - Interview Response

## What Is It?

OOP organizes code around objects that combine state and behavior.

The four core principles are:

```text
Encapsulation
Abstraction
Inheritance
Polymorphism
```

## 1. Encapsulation

Encapsulation means hiding internal state and exposing controlled behavior.

Bad:

```java
class Account {
    public BigDecimal balance;
}
```

Good:

```java
class Account {
    private BigDecimal balance;

    public void debit(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        balance = balance.subtract(amount);
    }
}
```

Why it matters:

```text
Callers cannot randomly corrupt balance.
Business rules stay inside the domain object/service.
```

## 2. Abstraction

Abstraction means exposing what something does, not how it does it.

```java
interface PaymentProcessor {
    PaymentResult process(PaymentCommand command);
}
```

Implementations:

```java
class CardPaymentProcessor implements PaymentProcessor { }
class WirePaymentProcessor implements PaymentProcessor { }
class AchPaymentProcessor implements PaymentProcessor { }
```

Controller/service depends on the interface:

```text
PaymentService -> PaymentProcessor
```

not on every concrete payment type.

## 3. Inheritance

Inheritance reuses common behavior through a parent type.

Use carefully.

Example:

```java
abstract class BasePaymentProcessor {
    protected void validateAmount(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
```

Risk:

```text
Deep inheritance chains become hard to change.
Prefer composition when behavior varies a lot.
```

## 4. Polymorphism

Polymorphism means the same interface call can execute different implementations.

```java
class PaymentService {
    private final Map<PaymentType, PaymentProcessor> processors;

    PaymentResult pay(PaymentCommand command) {
        return processors.get(command.type()).process(command);
    }
}
```

This avoids:

```java
if (type == CARD) { ... }
else if (type == WIRE) { ... }
else if (type == ACH) { ... }
```

## Spring Boot Example

```java
@Service
class TransferService {
    private final FraudChecker fraudChecker;
    private final LedgerRepository ledgerRepository;

    TransferService(FraudChecker fraudChecker, LedgerRepository ledgerRepository) {
        this.fraudChecker = fraudChecker;
        this.ledgerRepository = ledgerRepository;
    }
}
```

OOP principle:

```text
TransferService depends on abstractions.
Repositories hide persistence details.
Domain methods protect invariants.
```

## Forward / Backward Compatibility

OOP affects compatibility when you expose interfaces or libraries.

Safe changes:

```text
Add a default method to an interface
Add a new implementation
Add optional behavior behind an abstraction
```

Breaking changes:

```text
Remove interface method
Change method signature
Change expected behavior silently
```

Semantic versioning:

```text
MAJOR -> breaking public interface/class change
MINOR -> new implementation or compatible method
PATCH -> bug fix inside implementation
```

## Related Patterns

- Strategy: polymorphic payment processors
- Factory: create correct processor
- Template Method: shared algorithm with overridable steps
- Repository: abstract persistence
- Adapter: map external API to internal interface

## Common Mistakes

```text
Using inheritance for everything
Exposing mutable fields
Creating giant interfaces
Putting business rules in controllers
Confusing abstraction with unnecessary complexity
```

## Follow-Up Interview Questions

### Why prefer composition over inheritance?

```text
Composition is easier to change and test when behavior varies independently.
```

### How does Spring use OOP?

```text
Spring injects dependencies through interfaces/classes and lets services depend on abstractions.
```

### Which OOP principle helps avoid if-else chains?

```text
Polymorphism, usually with Strategy pattern.
```

## Interview Answer

In an interview, I would say: Encapsulation protects object state, abstraction hides implementation details, inheritance reuses stable common behavior, and polymorphism lets the same contract call different implementations. In a Spring banking service, I would use encapsulation to protect account invariants, interfaces to abstract payment processors, composition instead of deep inheritance, and polymorphism to select card, ACH, or wire behavior without large if-else blocks. The senior point is to connect OOP to maintainability, testability, and safe API evolution.
