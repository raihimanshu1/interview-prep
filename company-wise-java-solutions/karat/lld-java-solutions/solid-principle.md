# SOLID Principles

SOLID is a set of 5 design principles used to write:

* maintainable code
* scalable systems
* loosely coupled code
* easy-to-test applications

These principles are heavily used in:

* Spring Boot
* Microservices
* Large enterprise systems
* Low Level Design interviews

---

# S — Single Responsibility Principle (SRP)

## Meaning

A class should have:

> only ONE reason to change

One class → one responsibility.

---

## Bad Example

```java
class UserService {

    void saveUser() {
        System.out.println("Saving user to DB");
    }

    void sendEmail() {
        System.out.println("Sending email");
    }

    void generateReport() {
        System.out.println("Generating report");
    }
}
```

Problem:

* DB logic
* Email logic
* Report logic

All mixed together.

If email changes → class changes.

If report format changes → same class changes.

Too many responsibilities.

---

# Good Example

```java
class UserRepository {

    void saveUser() {
        System.out.println("Saving user to DB");
    }
}

class EmailService {

    void sendEmail() {
        System.out.println("Sending email");
    }
}

class ReportService {

    void generateReport() {
        System.out.println("Generating report");
    }
}
```

Now:

* each class has one job
* easier to test
* easier to maintain

---

# Real Thinking

Imagine:

* one employee doing accounting
* security
* HR
* development

Chaos.

Same with classes.

---

# O — Open Closed Principle (OCP)

## Meaning

Software should be:

> Open for extension
> Closed for modification

Meaning:

* add new behavior
* without changing existing code

---

# Bad Example

```java
class PaymentService {

    void pay(String type) {

        if (type.equals("UPI")) {
            System.out.println("UPI payment");
        }
        else if (type.equals("CARD")) {
            System.out.println("Card payment");
        }
    }
}
```

Problem:
Every new payment type:

* modify existing class

Risk:

* breaking old logic

---

# Good Example

```java
interface Payment {

    void pay();
}

class UpiPayment implements Payment {

    public void pay() {
        System.out.println("UPI payment");
    }
}

class CardPayment implements Payment {

    public void pay() {
        System.out.println("Card payment");
    }
}

class PaymentService {

    void processPayment(Payment payment) {
        payment.pay();
    }
}
```

Now adding:

* NetBankingPayment
* WalletPayment

No existing code changes.

Only extension.

---

# Real Thinking

Good systems grow by:

* adding new classes
* not editing stable code repeatedly

---

# L — Liskov Substitution Principle (LSP)

## Meaning

Child class should properly replace parent class
without breaking behavior.

---

# Bad Example

```java
class Bird {

    void fly() {
        System.out.println("Bird flies");
    }
}

class Penguin extends Bird {

    void fly() {
        throw new RuntimeException("Penguin cannot fly");
    }
}
```

Problem:
Penguin is not behaving like Bird.

Code expecting Bird breaks.

---

# Good Example

```java
class Bird {
}

interface Flyable {

    void fly();
}

class Sparrow extends Bird implements Flyable {

    public void fly() {
        System.out.println("Sparrow flies");
    }
}

class Penguin extends Bird {
}
```

Now:

* only flying birds implement Flyable
* no broken behavior

---

# Real Thinking

If child cannot truly behave like parent:

* inheritance is wrong

---

# I — Interface Segregation Principle (ISP)

## Meaning

Do not force classes
to implement methods they don't need.

Better:

* many small interfaces
  than
* one huge interface

---

# Bad Example

```java
interface Worker {

    void work();

    void eat();

    void sleep();
}
```

Now Robot must implement:

* eat()
* sleep()

Which makes no sense.

---

# Good Example

```java
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

class Human implements Workable, Eatable, Sleepable {

    public void work() {
        System.out.println("Human working");
    }

    public void eat() {
        System.out.println("Human eating");
    }

    public void sleep() {
        System.out.println("Human sleeping");
    }
}

class Robot implements Workable {

    public void work() {
        System.out.println("Robot working");
    }
}
```

Now:

* no unnecessary methods
* cleaner design

---

# Real Thinking

Big interfaces become:

* bloated
* hard to maintain
* hard to implement

---

# D — Dependency Inversion Principle (DIP)

## Meaning

High-level classes should NOT depend on low-level classes.

Both should depend on abstraction.

---

# Bad Example

```java
class MySQLDatabase {

    void connect() {
        System.out.println("Connected to MySQL");
    }
}

class UserService {

    MySQLDatabase db = new MySQLDatabase();

    void save() {
        db.connect();
    }
}
```

Problem:
UserService tightly coupled with MySQL.

Tomorrow:

* PostgreSQL
* MongoDB

Need code changes.

---

# Good Example

```java
interface Database {

    void connect();
}

class MySQLDatabase implements Database {

    public void connect() {
        System.out.println("Connected to MySQL");
    }
}

class PostgreSQLDatabase implements Database {

    public void connect() {
        System.out.println("Connected to PostgreSQL");
    }
}

class UserService {

    private Database db;

    UserService(Database db) {
        this.db = db;
    }

    void save() {
        db.connect();
    }
}

public class Main {

    public static void main(String[] args) {

        Database db = new PostgreSQLDatabase();

        UserService service = new UserService(db);

        service.save();
    }
}
```

---

# Real Thinking

High-level business logic:

* should not care
* which DB/email/logger implementation is used

This is foundation of:

* Spring Dependency Injection
* IoC Container
* Microservices flexibility

---

# Final Summary

| Principle | Meaning                                     |
| --------- | ------------------------------------------- |
| SRP       | One class → one responsibility              |
| OCP       | Extend without modifying                    |
| LSP       | Child should properly replace parent        |
| ISP       | Small focused interfaces                    |
| DIP       | Depend on abstraction, not concrete classes |

---

# Easy Way to Remember

```text
S → Single job
O → Open to extend
L → Replace child safely
I → Small interfaces
D → Depend on abstraction
```

---

# Most Important Real Interview Insight

SOLID is mainly about:

* reducing coupling
* improving extensibility
* improving maintainability
* avoiding fragile code

Most enterprise frameworks like:

* Spring Framework
* Spring Boot
* Hibernate

internally follow SOLID principles heavily.
