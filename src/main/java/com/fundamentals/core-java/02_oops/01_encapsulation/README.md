Understood. We will restart the **OOP module cleanly** and avoid duplication.

We already completed JVM, so now we move to **Java Object-Oriented Programming** from an interview perspective.

For each OOP topic, we will maintain this structure:

1. **Concept**
2. **Why it exists / problem it solves**
3. **How Java implements it**
4. **Real backend example**
5. **Design considerations (7+ years level)**
6. **Common mistakes**
7. **Interview questions with explanation**
8. **Boundary: what is enough vs deep dive**

---

# Module 3 — Object-Oriented Programming (OOP)

Roadmap:

```text
OOP

1. Object-Oriented Principles
        |
        v
2. Encapsulation ⭐⭐⭐⭐⭐
        |
        v
3. Abstraction ⭐⭐⭐⭐⭐
        |
        v
4. Inheritance ⭐⭐⭐⭐⭐
        |
        v
5. Polymorphism ⭐⭐⭐⭐⭐
        |
        v
6. Association
        |
        v
7. Aggregation
        |
        v
8. Composition ⭐⭐⭐⭐⭐
        |
        v
9. SOLID Principles ⭐⭐⭐⭐⭐
        |
        v
10. Object Lifecycle
```

---

# 1. Object-Oriented Principles ⭐⭐⭐⭐⭐

Before going into encapsulation, first understand the foundation.

## What is OOP?

Interview definition:

> Object-Oriented Programming is a programming paradigm where software is modeled as a collection of objects that contain state and behavior.

An object represents a real-world entity.

Example:

```text
Employee

State:
- name
- id
- salary

Behavior:
- calculateSalary()
- updateProfile()
```

Java representation:

```java
class Employee {

    private String name;
    private int id;
    private double salary;


    public void calculateSalary() {

    }


    public void updateProfile() {

    }
}
```

Object:

```java
Employee emp = new Employee();
```

---

# Why OOP?

Before OOP, procedural programming was common.

Example:

```text
Employee Data

        +

Functions

calculateSalary()
updateEmployee()
printEmployee()
```

Problem:

As applications grow:

```text
Employee Data

        |
        |
Hundreds of functions modifying it
```

Problems:

* Difficult maintenance
* No control over data
* High coupling
* Hard to extend

---

OOP solves this by combining:

```text
Object

+----------------+
| State          |
|                |
| Behavior       |
+----------------+
```

Example:

```text
Employee Object

State:
salary

Behavior:
increaseSalary()
```

The object owns its data and operations.

---

# Core Principles of OOP

There are four main pillars:

```text
                 OOP

                  |
      +-----------+-----------+
      |           |           |
Encapsulation Abstraction Inheritance Polymorphism
```

---

# 1. Encapsulation

First pillar.

## Definition

> Encapsulation is the mechanism of wrapping data and methods together inside a class and controlling access to the internal state of an object.

Simple meaning:

> Object should control its own data.

---

## Problem Without Encapsulation

Example:

```java
class BankAccount {

    public double balance;

}
```

Usage:

```java
BankAccount account = new BankAccount();

account.balance = -5000;
```

Now object state:

```text
BankAccount

balance = -5000 ❌
```

The object is invalid.

Who should decide whether balance can change?

The BankAccount itself.

---

# Encapsulation Solution

```java
class BankAccount {

    private double balance;


    public void deposit(double amount){

        if(amount > 0){
            balance += amount;
        }
    }


    public void withdraw(double amount){

        if(amount > 0 && amount <= balance){
            balance -= amount;
        }
    }


    public double getBalance(){

        return balance;
    }
}
```

Now:

```text
Outside world

      |
      |
      v

BankAccount

+----------------+
| private balance|
+----------------+
| deposit()      |
| withdraw()     |
| getBalance()   |
+----------------+
```

The object controls its state.

---

# Key Point

Many developers explain encapsulation as:

> "Making variables private and creating getters/setters."

That is incomplete.

Real encapsulation means:

> Protect object invariants by exposing meaningful operations.

Example:

Bad:

```java
account.setBalance(-10000);
```

Better:

```java
account.withdraw(10000);
```

Because withdrawal has business rules.

---

# Encapsulation in Real Backend Design

Example: Order Management

Bad:

```java
class Order {

    public String status;

}
```

Anyone can do:

```java
order.status = "DELIVERED";
```

Invalid flow:

```text
CREATED

   |
   v

DELIVERED

   |
   v

CANCELLED ❌
```

---

Better:

```java
class Order {

    private OrderStatus status;


    public void deliver(){

        if(status == OrderStatus.SHIPPED){

            status = OrderStatus.DELIVERED;

        }
    }


    public void cancel(){

        if(status != OrderStatus.DELIVERED){

            status = OrderStatus.CANCELLED;

        }
    }
}
```

Now:

```text
Order object

owns

state transitions
```

This is proper encapsulation.

---

# Benefits of Encapsulation

## 1. Data Protection

Prevents invalid states.

Example:

```text
balance cannot become negative
```

---

## 2. Loose Coupling

External classes depend on behavior, not implementation.

Today:

```java
private ArrayList<User> users;
```

Tomorrow:

```java
private HashMap<Long,User> users;
```

No external impact.

---

## 3. Maintainability

Business rules stay inside the object.

Example:

Tax calculation belongs to:

```text
Order

not

100 external services
```

---

# Common Interview Trap

## Is this encapsulation?

```java
class User {

    private String name;


    public String getName(){

        return name;
    }


    public void setName(String name){

        this.name = name;
    }
}
```

Technically yes, but weak encapsulation.

Why?

Because anyone can set anything.

Better:

```java
changeEmail()
updateAddress()
activateUser()
```

Methods should represent business actions.

---

# Interview Questions

## Q1. What is encapsulation?

Expected answer:

> Encapsulation is the OOP principle of combining data and behavior inside a class while restricting direct access to internal state. It helps maintain object integrity by allowing controlled modification of data.

---

## Q2. Why should fields be private?

Answer:

Because direct access allows external code to put objects into invalid states. Private fields allow the class to control modifications through methods.

---

## Q3. Difference between encapsulation and data hiding?

Answer:

**Data hiding:**

Restricting access to internal implementation.

Example:

```java
private balance;
```

**Encapsulation:**

Combining data + behavior and controlling access.

Example:

```text
BankAccount

balance

deposit()

withdraw()
```

Data hiding is achieved using encapsulation.

---

## Q4. Are getters and setters enough for encapsulation?

Answer:

No.

They provide access control but do not necessarily protect object rules.

Example:

```java
setBalance(-1000)
```

breaks business rules.

Better:

```java
withdraw(amount)
```

---

## Q5. Give a real-world example.

Answer:

Bank account.

The balance is private, and users interact through deposit and withdrawal methods. The account validates operations and maintains a valid state.

---

# Boundary for Interview Preparation

For a 7+ year Java developer:

Must know:

✅ Definition
✅ Why encapsulation is required
✅ Private fields
✅ Controlled access
✅ Business invariant protection
✅ Real backend examples
✅ Getter/setter limitations

No need deep dive:

* Reflection breaking encapsulation
* JVM access flags
* Bytecode level access checks

---

Next topic:

# Abstraction ⭐⭐⭐⭐⭐

We will cover:

* What abstraction really means
* Interface vs abstract class
* When to use which
* Payment/Notification backend examples
* Interview questions.
