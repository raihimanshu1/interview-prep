# 2. Abstraction ⭐⭐⭐⭐⭐

## What is Abstraction?

### Interview Definition

> Abstraction is the OOP principle of hiding internal implementation details and exposing only the essential functionality to the outside world.

Simple meaning:

> Tell the object **what to do**, but hide **how it does it**.

---

# Why do we need Abstraction?

Let's take a real example.

When you use a payment application:

```text
User

clicks

Pay ₹1000
```

The user only knows:

```text
pay()
```

But internally:

```text
Payment System

+--------------------+
| Validate Payment   |
| Check Balance      |
| Call Bank API      |
| Process Gateway    |
| Update Transaction |
+--------------------+
```

The user does not need to know these details.

---

Without abstraction:

```java
class PaymentService {

    void pay(){

        // validate card

        // call bank API

        // encrypt request

        // process response

    }
}
```

Every consumer depends on internal details.

Problem:

Tomorrow:

```
Change payment gateway
Change validation logic
Change encryption
```

Many places break.

---

With abstraction:

```text
Consumer

    |
    |
    v

Payment Interface

    |
    |
    +----------------+
    |                |
    v                v

Card Payment     UPI Payment
```

Consumer only depends on the contract.

---

# Abstraction in Java

Java provides abstraction using:

1. Abstract classes
2. Interfaces

---

# 1. Interface (Most common)

An interface defines a contract.

Example:

```java
interface PaymentService {

    void pay(double amount);

}
```

It says:

> Any payment implementation must provide pay().

---

Implementations:

```java
class CreditCardPayment implements PaymentService {


    @Override
    public void pay(double amount){

        System.out.println("Processing credit card payment");

    }
}
```

```java
class UPIPayment implements PaymentService {


    @Override
    public void pay(double amount){

        System.out.println("Processing UPI payment");

    }
}
```

---

Usage:

```java
PaymentService payment;


payment = new CreditCardPayment();

payment.pay(1000);


payment = new UPIPayment();

payment.pay(1000);
```

Output:

```
Processing credit card payment

Processing UPI payment
```

The caller does not know implementation details.

---

# 2. Abstract Class

An abstract class provides partial abstraction.

Example:

```java
abstract class Vehicle {


    abstract void start();


    void stop(){

        System.out.println("Vehicle stopped");

    }
}
```

Child class:

```java
class Car extends Vehicle {


    @Override
    void start(){

        System.out.println("Car starts with key");

    }
}
```

Usage:

```java
Vehicle vehicle = new Car();

vehicle.start();

vehicle.stop();
```

Output:

```
Car starts with key

Vehicle stopped
```

---

# Interface vs Abstract Class ⭐⭐⭐⭐⭐

Very common interview question.

| Interface                       | Abstract Class                 |
| ------------------------------- | ------------------------------ |
| Defines contract                | Defines partial implementation |
| Multiple interfaces allowed     | Only one class inheritance     |
| Uses `implements`               | Uses `extends`                 |
| No instance state traditionally | Can have instance variables    |
| Best for capability             | Best for common base behavior  |

---

Example:

## Interface

"What can it do?"

```java
interface Flyable {

    void fly();

}
```

Different objects:

```text
Bird
Airplane
Drone
```

All can fly.

---

## Abstract Class

"What is it?"

```java
abstract class Animal {

    String name;


    void eat(){

    }

}
```

Common animal behavior.

---

# Abstraction vs Encapsulation ⭐⭐⭐⭐⭐

Very common confusion.

## Abstraction

Focus:

> Hide complexity.

Question:

"What should the user see?"

Example:

```java
PaymentService.pay()
```

---

## Encapsulation

Focus:

> Protect internal state.

Question:

"How should data be controlled?"

Example:

```java
private balance;
```

---

Simple comparison:

|             | Abstraction               | Encapsulation    |
| ----------- | ------------------------- | ---------------- |
| Purpose     | Hide complexity           | Protect data     |
| Focus       | Behavior                  | State            |
| Achieved by | Interface, abstract class | Access modifiers |
| Example     | Payment API               | Bank balance     |

---

# Real Backend Example

## Notification System

Requirement:

Support:

* Email
* SMS
* Push notification

Bad design:

```java
class NotificationService {


    void send(String type){

        if(type.equals("EMAIL")){

            // email logic

        }

        else if(type.equals("SMS")){

            // sms logic

        }

    }
}
```

Problem:

Every new notification requires modifying existing code.

---

Using abstraction:

Contract:

```java
interface Notification {


    void send(String message);

}
```

Implementations:

```java
class EmailNotification implements Notification {


    public void send(String message){

        // email logic

    }
}
```

```java
class SMSNotification implements Notification {


    public void send(String message){

        // sms logic

    }
}
```

Service:

```java
class NotificationService {


    private Notification notification;


    NotificationService(Notification notification){

        this.notification = notification;

    }


    void notifyUser(String message){

        notification.send(message);

    }
}
```

Now:

Adding:

```text
PushNotification
WhatsAppNotification
```

does not change existing code.

This is abstraction + polymorphism + SOLID.

---

# Common Mistakes

## Mistake 1: Thinking abstraction means only abstract classes

Wrong.

Java abstraction is achieved through:

* Interfaces
* Abstract classes

---

## Mistake 2: Hiding everything

Bad design:

```java
100 interfaces

for 100 classes
```

Abstraction should exist where there is a meaningful contract.

---

## Mistake 3: Confusing abstraction with encapsulation

Example:

```java
private password;
```

This is encapsulation.

Not abstraction.

---

# Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. What is abstraction in Java?

Expected answer:

> Abstraction is the process of hiding implementation details and exposing only required functionality. In Java, it is achieved using interfaces and abstract classes.

---

## Q2. Why do we need abstraction?

Expected answer:

> Abstraction reduces complexity, hides implementation details, and allows changing implementations without affecting consumers.

Example:

PaymentService can switch from Stripe to another gateway without changing calling code.

---

## Q3. Interface vs Abstract class?

Expected answer:

> Use interfaces when defining a contract or capability that multiple unrelated classes can implement. Use abstract classes when classes share common state or behavior.

---

## Q4. Can an abstract class have a constructor?

Yes.

Example:

```java
abstract class Animal {

    Animal(){

        System.out.println("Constructor");

    }
}
```

Reason:

When child object is created, parent initialization happens.

---

## Q5. Can an interface have method implementation?

Yes, since Java 8:

```java
interface Payment {

    default void validate(){

        System.out.println("validation");

    }
}
```

Also:

* default methods
* static methods

---

## Q6. Which is better: Interface or Abstract Class?

Expected senior answer:

> It depends on the design. Prefer interfaces for contracts and loose coupling. Use abstract classes when multiple classes share common state or implementation.

---

# Interview Boundary

For a 7+ year Java developer:

Must know:

✅ Definition
✅ Why abstraction exists
✅ Interface vs abstract class
✅ Real-world examples
✅ Design benefits
✅ Connection with SOLID and polymorphism

Deep dive not required:

* JVM representation of interfaces
* Reflection internals
* Compiler implementation details

---

Next:

# Inheritance ⭐⭐⭐⭐⭐

We will cover:

* Why inheritance exists
* IS-A relationship
* Types of inheritance in Java
* Method overriding
* Overriding rules
* Why composition is often preferred over inheritance
* Interview questions.
