# OOD: override vs overload, polymorphism, inheritance vs composition, interface vs abstract class.

## Problem In Simple Words

Answer common object-oriented design questions.

## Input / Context Meaning

Interviewer may ask definitions and when to use each concept.

## Sample Answer

"I would start by clarifying the exact scenario, then explain the core concept in practical terms. For this prompt, the key is: Interviewer may ask definitions and when to use each concept. I would give a concrete example, mention the tradeoff, and close with how I would use or test it in real code."

## Why

This answer works because it is spoken like an interview response, not just a list. It starts with assumptions, gives a practical solution, names tradeoffs, and finishes with verification or measurement.

## School-Level Intuition

OOD answers should show design judgment, not memorized definitions.

## Baseline Answer

Anti-pattern baseline: Only define terms.

This is acceptable as a first pass because it shows the main idea without over-designing. The weakness is that it may miss scale, failure modes, edge cases, or measurable impact.

This is intentionally weak. In the interview, do not stop here; use it to explain what a poor answer misses, then move to the stronger answer.

## Stronger Answer

Explain override is runtime behavior replacement; overload is same method name with different params; prefer composition for flexible behavior; use interfaces for contracts.

To make it interview-ready, also mention assumptions, constraints, alternatives considered, and what you would monitor after launch.

Spoken upgrade: "The reason I choose this path is that it handles the common case first, then adds safeguards for scale, failure, and maintenance. I would validate it with metrics and revisit the design if the assumptions change."

## Dry Run / Walkthrough

PaymentProcessor interface lets CreditCardPayment and UpiPayment implement pay(). A service depends on interface, not concrete class.

Concrete walkthrough structure:
1. State the simplest user or system flow.
2. Identify the first risk or bottleneck.
3. Add the component, policy, or communication step that handles it.
4. Explain how you verify it worked.

## Interview Checklist

Mention SOLID, dependency inversion, testability, coupling, extensibility, and avoiding deep inheritance.


Extra checks:
- Clarify assumptions before jumping into the answer.
- Mention tradeoffs, not only the final choice.
- Include one failure mode or edge case.
- End with how you would measure success.



## Inheritance vs Composition

Both are ways to reuse code and model relationships between classes.

---

## Inheritance

Inheritance means:

> "IS-A" relationship

Child class automatically gets properties and behavior of parent class.

Example:

* Dog IS-A Animal
* Car IS-A Vehicle

### Simple Example

```java
// Parent class
class Animal {

    void eat() {
        System.out.println("Animal is eating");
    }
}

// Child class
class Dog extends Animal {

    void bark() {
        System.out.println("Dog is barking");
    }
}

public class Main {
    public static void main(String[] args) {

        Dog dog = new Dog();

        // inherited method
        dog.eat();

        // own method
        dog.bark();
    }
}
```

### Output

```text
Animal is eating
Dog is barking
```

---

## How to Think About Inheritance

Dog did not define `eat()`.

But since Dog extends Animal:

* Dog automatically gets all non-private behavior of Animal.

So:

* Parent = common behavior
* Child = specialized behavior

---

## Problem With Too Much Inheritance

Inheritance creates tight coupling.

Example:

```java
class Bird {
    void fly() {}
}

class Penguin extends Bird {
}
```

Problem:

* Penguin cannot fly.
* But inheritance forced wrong behavior.

This is why composition is preferred in many real systems.

---

# Composition

Composition means:

> "HAS-A" relationship

A class contains another class object and uses it.

Example:

* Car HAS-A Engine
* Computer HAS-A Keyboard

---

## Simple Example

```java
class Engine {

    void start() {
        System.out.println("Engine started");
    }
}

class Car {

    // composition
    private Engine engine = new Engine();

    void startCar() {
        engine.start();
        System.out.println("Car started");
    }
}

public class Main {
    public static void main(String[] args) {

        Car car = new Car();
        car.startCar();
    }
}
```

### Output

```text
Engine started
Car started
```

---

## How to Think About Composition

Car does not become Engine.

Instead:

* Car USES Engine.

This gives flexibility.

Tomorrow:

* PetrolEngine
* ElectricEngine
* HybridEngine

You can swap them easily.

---

## Real Difference

| Inheritance                      | Composition             |
| -------------------------------- | ----------------------- |
| IS-A relation                    | HAS-A relation          |
| Tight coupling                   | Loose coupling          |
| Child depends strongly on parent | Objects can be replaced |
| Less flexible                    | More flexible           |
| `extends`                        | object reference        |
| Reuse by extending               | Reuse by combining      |

---

## Interview Rule

Prefer:

* Composition over inheritance

Unless:

* There is a true IS-A relationship.

---

# Interface vs Abstract Class

This is another very famous confusion.

---

# Interface

Interface defines:

* WHAT a class should do
* Not HOW

Think:

* Contract
* Capability

Example:

* Flyable
* Drivable
* Payable

---

## Simple Interface Example

```java
interface Payment {

    void pay();
}

class CreditCardPayment implements Payment {

    public void pay() {
        System.out.println("Paid using credit card");
    }
}

class UpiPayment implements Payment {

    public void pay() {
        System.out.println("Paid using UPI");
    }
}

public class Main {

    public static void main(String[] args) {

        Payment p1 = new CreditCardPayment();
        Payment p2 = new UpiPayment();

        p1.pay();
        p2.pay();
    }
}
```

---

## Important Idea

Interface says:

> "Any class implementing me MUST provide this behavior."

But interface itself does not care HOW.

---

# Abstract Class

Abstract class is:

* Partial implementation
* Common base class

It can contain:

* abstract methods
* normal methods
* variables
* constructors

---

## Simple Abstract Class Example

```java
abstract class Animal {

    // concrete method
    void eat() {
        System.out.println("Animal is eating");
    }

    // abstract method
    abstract void sound();
}

class Dog extends Animal {

    void sound() {
        System.out.println("Dog barks");
    }
}

public class Main {

    public static void main(String[] args) {

        Dog dog = new Dog();

        dog.eat();
        dog.sound();
    }
}
```

---

# Why Abstract Class?

Because some behavior is common.

All animals:

* eat
* sleep

But sound differs:

* dog barks
* cat meows

So:

* common logic in abstract class
* varying logic in child classes

---

# Core Difference

| Interface                           | Abstract Class               |
| ----------------------------------- | ---------------------------- |
| Contract                            | Partial implementation       |
| Focus on capability                 | Focus on common base         |
| Supports multiple inheritance       | Single inheritance           |
| Mostly method declarations          | Can have full implementation |
| No instance variables traditionally | Can have state               |
| `implements`                        | `extends`                    |

---

# Real-Life Analogy

## Interface

```text
Remote control buttons
```

TV and AC both can implement:

* ON
* OFF

But internal working differs.

---

## Abstract Class

```text
Vehicle base class
```

All vehicles:

* have wheels
* can start

But:

* bike starts differently
* car starts differently

---

# Very Important Interview Point

Use Interface when:

* unrelated classes share capability
* loose coupling needed
* multiple implementations expected

Example:

* Payment
* Logger
* NotificationSender

---

Use Abstract Class when:

* classes are closely related
* common code exists
* shared state/fields required

Example:

* Animal
* Vehicle
* Employee

---

# One Powerful Example

```java
interface Flyable {
    void fly();
}

abstract class Bird {

    void eat() {
        System.out.println("Bird is eating");
    }
}

class Sparrow extends Bird implements Flyable {

    public void fly() {
        System.out.println("Sparrow flies");
    }
}
```

Here:

* Bird = common base behavior
* Flyable = capability

This is very common in real systems.
