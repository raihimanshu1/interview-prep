# 4. Polymorphism ⭐⭐⭐⭐⭐

## What is Polymorphism?

### Interview Definition

> Polymorphism is the OOP principle where the same interface or method can represent different behaviors depending on the object or context.

Simple meaning:

> One thing, many forms.

Example:

A `pay()` operation can have different implementations:

```text
id="h0s6iq"
Payment

pay()

  |
  +----------------+
  |                |
  v                v

Card Payment    UPI Payment
```

Both perform payment, but the behavior is different.

---

# Why do we need Polymorphism?

Without polymorphism, code becomes full of conditions.

Example:

```java
public class PaymentService {

    public void pay(String type) {

        if(type.equals("CARD")) {

            // card payment logic

        }
        else if(type.equals("UPI")) {

            // UPI payment logic

        }
        else if(type.equals("WALLET")) {

            // wallet payment logic

        }
    }
}
```

Problems:

* Every new payment type requires modifying existing code.
* Code becomes difficult to maintain.
* Violates Open/Closed Principle (SOLID).

---

With polymorphism:

```text
id="o5t8m2"
Payment Interface

        |
        |
 +------+------+
 |             |
 v             v

CardPayment  UPIPayment
```

The caller only knows:

```java
payment.pay();
```

The object decides which implementation runs.

---

# Types of Polymorphism in Java

Java supports two types:

```text
id="o5f7cj"
Polymorphism

      |
      |
+-----+------+
|            |
Compile     Runtime
Time        Time
```

---

# 1. Compile-Time Polymorphism ⭐⭐⭐⭐

Also called:

> Method Overloading

The compiler decides which method to call.

---

Example:

```java
class Calculator {


    int add(int a, int b) {

        return a + b;

    }


    int add(int a, int b, int c) {

        return a + b + c;

    }


    double add(double a, double b) {

        return a + b;

    }

}
```

Usage:

```java
Calculator calc = new Calculator();


calc.add(10,20);


calc.add(10,20,30);


calc.add(10.5,20.5);
```

Compiler determines:

```text
id="n0dd7d"
add(int,int)

or

add(int,int,int)

or

add(double,double)
```

---

## Method Overloading Rules

Methods must differ by:

* Number of parameters
* Type of parameters
* Order of parameters

Example:

Valid:

```java
calculate(int a)

calculate(String a)
```

Valid:

```java
calculate(int a, String b)

calculate(String a, int b)
```

---

Invalid:

```java
calculate(int a)

calculate(int b)
```

Why?

Parameter name does not matter.

Both are:

```java
calculate(int)
```

---

# 2. Runtime Polymorphism ⭐⭐⭐⭐⭐

Also called:

> Method Overriding

The JVM decides which method to execute at runtime.

This is the most important polymorphism for senior interviews.

---

Example:

Parent:

```java
class Animal {


    void sound(){

        System.out.println("Animal sound");

    }

}
```

Child:

```java
class Dog extends Animal {


    @Override
    void sound(){

        System.out.println("Bark");

    }

}
```

Now:

```java
Animal animal = new Dog();

animal.sound();
```

Output:

```text
Bark
```

---

Important concept:

Reference type:

```java
Animal
```

Object type:

```java
Dog
```

Method execution depends on:

```text
id="n2j2l9"
Object type
```

not reference type.

---

# How JVM Decides Method Call?

This is a common senior-level question.

Example:

```java
Animal animal = new Dog();

animal.sound();
```

Flow:

```text
id="g5m1az"
Compile Time

Check reference type

Animal

        |
        v

Does Animal have sound()?

Yes


Runtime

Check actual object

Dog

        |
        v

Execute Dog.sound()
```

This is called:

## Dynamic Method Dispatch

---

# Important Polymorphism Example

Consider backend notification system.

Interface:

```java
interface Notification {

    void send(String message);

}
```

Implementations:

```java
class EmailNotification implements Notification {


    public void send(String message){

        System.out.println("Sending Email");

    }
}
```

```java
class SMSNotification implements Notification {


    public void send(String message){

        System.out.println("Sending SMS");

    }
}
```

Service:

```java
class NotificationService {


    void notifyUser(Notification notification){

        notification.send("Hello");

    }

}
```

Usage:

```java
NotificationService service = new NotificationService();


service.notifyUser(
        new EmailNotification()
);


service.notifyUser(
        new SMSNotification()
);
```

Output:

```text
Sending Email

Sending SMS
```

The service does not change.

---

# Polymorphism + Dependency Injection ⭐⭐⭐⭐⭐

This is heavily used in Spring Boot.

Example:

```java
interface PaymentService {

    void pay();

}
```

Implementations:

```java
class RazorpayPayment implements PaymentService {

    public void pay(){

    }
}
```

```java
class StripePayment implements PaymentService {

    public void pay(){

    }
}
```

Spring injects:

```java
PaymentService paymentService;
```

The business code does not care about implementation.

This gives:

* Loose coupling
* Easy testing
* Extensibility

---

# Overloading vs Overriding ⭐⭐⭐⭐⭐

Very common interview question.

| Method Overloading        | Method Overriding         |
| ------------------------- | ------------------------- |
| Compile-time polymorphism | Runtime polymorphism      |
| Same class usually        | Parent-child relationship |
| Same method name          | Same method signature     |
| Parameters must differ    | Parameters must be same   |
| Inheritance not required  | Requires inheritance      |

---

Example:

## Overloading

```java
calculate(int)

calculate(double)
```

Compiler decides.

---

## Overriding

```java
Animal.sound()

Dog.sound()
```

JVM decides.

---

# Common Mistakes

## Mistake 1: Confusing inheritance with polymorphism

Inheritance:

```text
Dog extends Animal
```

Polymorphism:

```java
Animal a = new Dog();

a.sound();
```

Inheritance enables polymorphism, but they are different concepts.

---

## Mistake 2: Thinking method overloading is runtime

Wrong.

Overloading:

```text
Compile time
```

Overriding:

```text
Runtime
```

---

## Mistake 3: Static methods are polymorphic

They are not overridden.

Example:

```java
class Parent {

    static void show(){

    }

}


class Child extends Parent {

    static void show(){

    }

}
```

This is method hiding.

---

# Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. What is polymorphism?

Expected answer:

> Polymorphism allows an object to take multiple forms. In Java, it enables the same method call to execute different implementations based on the object type.

---

## Q2. Difference between compile-time and runtime polymorphism?

Expected answer:

Compile-time:

* Method overloading
* Compiler decides method call

Runtime:

* Method overriding
* JVM decides based on actual object type

---

## Q3. Explain runtime polymorphism with example.

Expected answer:

```java
Animal animal = new Dog();

animal.sound();
```

Although reference type is `Animal`, actual object is `Dog`, so JVM invokes `Dog.sound()`.

This is dynamic method dispatch.

---

## Q4. Why is polymorphism important in Spring?

Expected answer:

Spring heavily uses interfaces and dependency injection.

Example:

```java
PaymentService
```

can have:

* Stripe implementation
* Razorpay implementation

Business logic depends on the abstraction, not concrete classes.

---

## Q5. Can we override a static method?

Expected answer:

No.

Static methods belong to the class, not the object. If child defines the same static method, it is method hiding, not overriding.

---

## Q6. Can private methods be overridden?

Expected answer:

No.

Private methods are not visible to child classes, so they cannot participate in runtime polymorphism.

---

# Interview Boundary

For a 7+ years Java developer:

Must know:

✅ Definition
✅ Compile-time vs runtime polymorphism
✅ Overloading
✅ Overriding
✅ Dynamic method dispatch
✅ Interface-based design
✅ Spring dependency injection connection
✅ Overriding rules

Deep dive not required:

* JVM vtable implementation
* Method resolution bytecode internals

---

Next topic:

# Association, Aggregation, and Composition ⭐⭐⭐⭐⭐

We will cover together because they are related:

```text
Association
     |
     +-- Aggregation
     |
     +-- Composition
```

These are frequently asked in LLD/design interviews.
