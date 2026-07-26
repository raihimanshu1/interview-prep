# Java `final` Keyword ⭐⭐⭐⭐⭐

`final` is one of the most asked Java keywords for senior developers because it connects with:

* Immutability
* Object references
* Inheritance
* JVM optimization
* Thread safety
* Design decisions

Many developers know the syntax but fail on the **difference between final variable and immutable object**.

Let's go deep.

---

# 1. What is `final`?

## Interview Definition

> `final` is a Java keyword used to restrict modification. Once something is marked final, it cannot be changed after initialization.

It can be applied to:

```text
final
 |
 +-- Variable
 |
 +-- Method
 |
 +-- Class
```

---

# 2. final Variable ⭐⭐⭐⭐⭐

A final variable can be assigned only once.

Example:

```java
final int age = 30;

age = 40; // Compilation error
```

Compiler error:

```text
cannot assign a value to final variable age
```

---

## Initialization Options

A final variable does not need to be initialized immediately.

Valid:

```java
final int age;

age = 30;
```

But after assignment:

```java
age = 40; // Error
```

---

# 3. final Reference Variable ⭐⭐⭐⭐⭐

This is the most common interview trap.

Example:

```java
final Employee emp = new Employee();
```

Question:

Can we modify Employee?

Answer:

**Yes.**

Can we change reference?

Answer:

**No.**

---

Memory:

```text
Stack

emp
 |
 |
 v

Heap

Employee Object
name="John"
```

Allowed:

```java
emp.setName("Mike");
```

Because object state changes.

---

Not allowed:

```java
emp = new Employee();
```

Because reference changes.

---

# Important Difference

## final reference

```java
final Employee emp;
```

Means:

```
Reference cannot change
```

---

## Immutable object

```java
String name="John";
```

Means:

```
Object state cannot change
```

Different concepts.

---

Example:

```java
final StringBuilder sb =
        new StringBuilder("Java");


sb.append(" Backend");
```

Allowed.

Why?

Because:

```text
Reference is final

but

Object is mutable
```

---

# 4. final with Objects ⭐⭐⭐⭐⭐

Example:

```java
class Employee {

    private final int id;

    Employee(int id){
        this.id = id;
    }

}
```

Here:

```java
id
```

cannot change after constructor.

---

Common immutable class pattern:

```java
public final class Employee {

    private final String name;

}
```

Connection:

```text
final class
+
final fields
+
no setters

=
Immutable object
```

---

# 5. final Method ⭐⭐⭐⭐

A final method cannot be overridden by subclasses.

Example:

```java
class Employee {


    public final void calculateSalary(){

        System.out.println("Salary calculation");

    }

}
```

Subclass:

```java
class Manager extends Employee {


    public void calculateSalary(){

    }

}
```

Compilation error.

---

# Why make a method final?

## 1. Protect behavior

Example:

```java
class Security {


    public final boolean authenticate(){

        // security logic

    }

}
```

Subclass cannot modify authentication logic.

---

## 2. Framework usage

Many Java frameworks use final methods to guarantee behavior.

---

# 6. final Class ⭐⭐⭐⭐⭐

A final class cannot be extended.

Example:

```java
final class SecurityManager {

}
```

Invalid:

```java
class CustomSecurityManager
        extends SecurityManager {

}
```

---

# Why make a class final?

## 1. Prevent inheritance

Example:

```java
String
```

is final.

```java
public final class String
```

Why?

Because String needs:

* Immutability
* Security
* Predictable behavior

---

## 2. Immutable classes

Example:

```java
public final class Employee {

}
```

Prevents subclass from adding mutable state.

---

# 7. final and Inheritance

Example:

```java
class Parent {

    final int x = 10;

}
```

Child inherits:

```java
class Child extends Parent {

}
```

Allowed.

Child gets:

```java
x = 10;
```

But cannot change it.

---

# 8. final and Constructor

Important question:

Can constructor be final?

Answer:

No.

Example:

```java
final Employee(){

}
```

Invalid.

Why?

Because constructors are not inherited.

`final` prevents overriding/inheritance behavior, but constructors are never overridden.

---

# 9. final Parameters ⭐⭐⭐⭐

Example:

```java
public void process(final Employee emp){

}
```

Means:

Inside method:

```java
emp = new Employee();
```

not allowed.

But:

```java
emp.setName("John");
```

allowed.

Again:

Reference restriction only.

---

# 10. final and Static ⭐⭐⭐⭐⭐

Common combination:

```java
public static final int MAX_SIZE = 100;
```

Meaning:

```text
static
+
final
```

creates constants.

---

Example:

```java
public class Config {

    public static final String APP_NAME =
            "Payment-Service";

}
```

Usage:

```java
Config.APP_NAME;
```

---

Naming convention:

```text
UPPER_CASE
```

Example:

```java
MAX_CONNECTIONS
DEFAULT_TIMEOUT
```

---

# 11. final and Java Memory Model ⭐⭐⭐⭐

Advanced senior point.

Final fields have special visibility guarantees.

Example:

```java
class Employee {


    private final int id;


    Employee(int id){

        this.id=id;

    }

}
```

After construction:

Other threads safely see:

```text
id value
```

without additional synchronization.

---

Why?

Java Memory Model provides:

```
final field semantics
```

during object construction.

---

# 12. final and JVM Optimization ⭐⭐⭐

Compiler/JVM can optimize final values.

Example:

```java
final int MAX = 100;
```

Compiler may replace:

```java
calculate(MAX)
```

with:

```java
calculate(100)
```

because value cannot change.

---

# 13. Common Interview Traps ⭐⭐⭐⭐⭐

---

## Q1. Is final object immutable?

Answer:

No.

Example:

```java
final List<String> list =
        new ArrayList<>();

list.add("Java");
```

Allowed.

Because:

```text
Reference fixed

Object mutable
```

---

## Q2. Can final method be overloaded?

Yes.

Example:

```java
final void display(){

}

final void display(int x){

}
```

Overloading is allowed.

---

## Q3. Can final class have non-final methods?

Yes.

Example:

```java
final class Employee {


    void work(){

    }

}
```

No inheritance exists, so overriding is impossible.

---

## Q4. Why is String final?

Answer:

Because String needs:

* Immutability
* Security
* Stable hashCode
* String pool safety

---

## Q5. Difference between final, finally, finalize?

Classic question.

| Keyword    | Purpose                  |
| ---------- | ------------------------ |
| final      | Restriction keyword      |
| finally    | Exception handling block |
| finalize() | Old GC cleanup method    |

Example:

```java
try {

}
catch(Exception e){

}
finally {

}
```

---

# Production Usage Examples

## Constants

```java
public static final int MAX_RETRY = 3;
```

---

## Dependency Injection

Spring:

```java
@Service
class OrderService {


private final PaymentService paymentService;


OrderService(PaymentService paymentService){

    this.paymentService=paymentService;

}

}
```

Modern Spring prefers constructor injection with final fields.

Benefits:

* Dependency cannot change
* Easier testing
* Thread safety

---

# Interview Boundary

For 7+ years Java developer:

Must know deeply:

✅ final variable
✅ final reference vs immutable object
✅ final method
✅ final class
✅ String is final reason
✅ static final constants
✅ final fields + Java Memory Model

Deep dive not required:

* Bytecode optimization details
* JIT constant folding internals

---

Next topic:

# `static` Keyword ⭐⭐⭐⭐⭐

We will cover:

* static variable
* static method
* static block
* class loading connection
* static initialization order
* static nested class
* singleton usage
* common interview traps

This is another very high-frequency senior Java interview topic.

# `static` Keyword ⭐⭐⭐⭐⭐

`static` is another **very high-value Java interview topic** because it connects with:

* Class loading
* Memory management
* Object lifecycle
* Singleton design pattern
* Utility classes
* Spring bean design
* Thread safety

For senior developers, interviewers usually ask:

* Where is static stored?
* When is static initialized?
* Why can static method not access instance variables?
* Static block execution order?
* Static vs instance?

---

# 1. What is `static`?

## Interview Definition

> `static` is a keyword used to define members that belong to the class rather than individual objects.

Meaning:

Without static:

```text
Each object gets its own copy
```

With static:

```text
One copy shared by the entire class
```

---

# 2. Static Variable (Class Variable) ⭐⭐⭐⭐⭐

Example:

```java
class Employee {

    private int id;

    static String company = "Oracle";

}
```

Create objects:

```java
Employee e1 = new Employee();

Employee e2 = new Employee();
```

Memory:

```text
Heap


Employee Object 1

id
company reference
        |
        |
        v


Employee Object 2

id
company reference
        |
        |
        v


Class Memory

Employee.class

company = "Oracle"
```

Only one `company` exists.

---

# Instance Variable vs Static Variable

Example:

```java
class Employee {

    int salary;

    static String company;

}
```

Objects:

```java
Employee e1 = new Employee();
Employee e2 = new Employee();
```

Memory:

```
e1
 |
 +-- salary


e2
 |
 +-- salary


Employee Class

 |
 +-- company
```

---

# Why use static variables?

## Example: Counter

Without static:

```java
class Employee {

    int count;

    Employee(){

        count++;

    }

}
```

Problem:

Every object gets its own count.

---

With static:

```java
class Employee {

    static int count;


    Employee(){

        count++;

    }

}
```

Now:

```java
Employee e1 = new Employee();
Employee e2 = new Employee();

System.out.println(Employee.count);
```

Output:

```
2
```

Because shared.

---

# 3. Static Method ⭐⭐⭐⭐⭐

Example:

```java
class MathUtil {


    static int add(int a,int b){

        return a+b;

    }

}
```

Call:

```java
MathUtil.add(10,20);
```

No object required.

---

# Why no object required?

Because static belongs to class:

```text
Class
 |
 |
 static method
```

not:

```text
Object
 |
 |
 method
```

---

# 4. Why Static Method Cannot Access Instance Variables?

Classic interview question.

Example:

```java
class Employee {

    int salary;


    static void printSalary(){

        System.out.println(salary);

    }

}
```

Compilation error.

Why?

Because:

```text
static method
```

exists before objects are created.

But:

```text
salary
```

exists only after object creation.

---

Timeline:

```
Class Loading

        |
        |
        v

static variables created
static methods available


        |
        |
        v

Object creation


        |
        |
        v

instance variables created
```

Static method cannot depend on something that may not exist.

---

# How static method accesses instance data?

Pass object:

```java
class Employee {


int salary;


static void print(Employee e){

    System.out.println(e.salary);

}

}
```

Now it has reference.

---

# 5. Static Block ⭐⭐⭐⭐⭐

Static block executes when class is loaded.

Example:

```java
class Database {


    static {

        System.out.println("Loading DB configuration");

    }

}
```

When JVM loads:

```java
Database
```

Output:

```
Loading DB configuration
```

---

# Execution Order ⭐⭐⭐⭐⭐

Example:

```java
class Demo {


    static int x = 10;


    static {

        System.out.println("Static block");

    }


    public static void main(String args[]){

        System.out.println("Main");

    }

}
```

Output:

```
Static block
Main
```

Because:

```
Class Loading

     |
     |
Static variables initialization

     |
     |
Static blocks

     |
     |
main()
```

---

# Multiple Static Blocks

Example:

```java
class Demo {


static {

System.out.println("Block 1");

}


static {

System.out.println("Block 2");

}

}
```

Execution:

```
Block 1
Block 2
```

Top to bottom order.

---

# 6. Static Initialization Example ⭐⭐⭐⭐⭐

Example:

```java
class Config {


static String url;


static {

    url = "localhost:8080";

}


}
```

Usage:

```java
System.out.println(Config.url);
```

No object required.

---

# 7. Static Nested Class ⭐⭐⭐⭐

Java allows:

```java
class Outer {


static class Inner {


}

}
```

Example:

```java
Outer.Inner obj =
        new Outer.Inner();
```

---

Difference:

## Non-static inner class

Requires outer object:

```java
Outer outer =
new Outer();

Outer.Inner inner =
outer.new Inner();
```

---

## Static nested class

No outer object required:

```java
new Outer.Inner();
```

---

# Why use static nested class?

Example:

Builder pattern:

```java
class User {


private String name;


static class Builder {


}

}
```

Used heavily in:

* Lombok
* DTO builders
* Configuration classes

---

# 8. Static and Class Loading ⭐⭐⭐⭐⭐

Important JVM connection.

When JVM loads a class:

Steps:

```
Loading

   |
   |
Linking

   |
   |
Initialization

   |
   |
static variables assigned

   |
   |
static blocks executed
```

Example:

```java
class Test {


static int value = 100;


static {

System.out.println(value);

}

}
```

Output:

```
100
```

---

# 9. Static and Singleton Pattern ⭐⭐⭐⭐⭐

Classic singleton:

```java
class Singleton {


private static Singleton instance;


private Singleton(){

}


public static Singleton getInstance(){

    if(instance == null){

        instance = new Singleton();

    }

    return instance;

}

}
```

Why static?

Because:

```text
One instance reference
shared by everyone
```

---

# Problem

Above code is not thread safe.

Two threads:

```
Thread 1
     |
     create object


Thread 2
     |
     create object
```

Solution:

* synchronized
* double checked locking
* static holder
* enum singleton

(covered in Design Patterns + Concurrency)

---

# 10. Static final Constants ⭐⭐⭐⭐⭐

Common production pattern:

```java
public class Config {


public static final int MAX_RETRY = 3;


}
```

Meaning:

```
static
+
final
```

= shared constant.

Usage:

```java
Config.MAX_RETRY;
```

---

# 11. Static Import ⭐⭐⭐

Example:

Without static import:

```java
Math.max(10,20);
```

With:

```java
import static java.lang.Math.max;
```

Now:

```java
max(10,20);
```

Less commonly used.

---

# 12. Static vs Instance ⭐⭐⭐⭐⭐

|                            | Static              | Instance         |
| -------------------------- | ------------------- | ---------------- |
| Belongs to                 | Class               | Object           |
| Created                    | Class loading       | Object creation  |
| Copies                     | One                 | One per object   |
| Access                     | Class name          | Object reference |
| Can access instance fields | No                  | Yes              |
| Memory                     | Class metadata area | Heap             |

---

# Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. When are static variables initialized?

Answer:

During class initialization phase, before the class is used.

---

## Q2. Can static method access non-static variable?

No.

Because instance variables need an object.

---

## Q3. Can we override static methods?

Important.

No.

Static methods are **hidden**, not overridden.

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

## Q4. Can constructor be static?

No.

Because constructor creates objects, while static belongs to class.

---

## Q5. Why is main method static?

```java
public static void main(String[] args)
```

Because JVM needs to call it without creating an object.

---

# Production Usage

## Utility classes

Example:

```java
StringUtils.isEmpty(value);
```

---

## Constants

```java
public static final String SERVICE_NAME="payment";
```

---

## Factory methods

Example:

```java
Integer.valueOf(100);
```

---

# Interview Boundary

For 7+ years Java developer:

Must know deeply:

✅ Static variable
✅ Static method
✅ Static block
✅ Initialization order
✅ Class loading connection
✅ Static vs instance
✅ Static method hiding
✅ Singleton connection
✅ Static nested class

Deep dive not required:

* JVM class unloading details
* Metaspace implementation

---

Next topic:

# Access Modifiers ⭐⭐⭐⭐

We will cover:

* private
* default/package-private
* protected
* public
* Class level access
* Method/variable access
* Protected tricky cases (very common interview question)
* Package and inheritance scenarios

# Access Modifiers ⭐⭐⭐⭐

Access modifiers control **visibility and accessibility** of classes, methods, variables, and constructors.

For senior Java interviews, the important parts are:

* Difference between `private`, `default`, `protected`, `public`
* Package + inheritance behaviour
* Why protected is confusing
* Class-level restrictions
* Encapsulation connection

---

# 1. Why do we need Access Modifiers?

Main purpose:

> Control who can access or modify our code.

Example:

```java
class BankAccount {

    private double balance;

}
```

Without restriction:

```java
account.balance = -10000;
```

Anyone can corrupt state.

With:

```java
private
```

we force controlled access.

Example:

```java
public void withdraw(double amount){

    if(amount > 0){
        balance -= amount;
    }

}
```

This is the foundation of **encapsulation**.

---

# 2. Four Access Levels ⭐⭐⭐⭐⭐

Java has four access levels:

| Modifier  | Same Class | Same Package | Subclass Different Package | Everywhere |
| --------- | ---------- | ------------ | -------------------------- | ---------- |
| private   | Yes        | No           | No                         | No         |
| default   | Yes        | Yes          | No                         | No         |
| protected | Yes        | Yes          | Yes                        | No         |
| public    | Yes        | Yes          | Yes                        | Yes        |

Remember:

```text
private
   |
   | more restrictive
   |
default
   |
protected
   |
public
```

---

# 3. private ⭐⭐⭐⭐⭐

## Definition

> Accessible only inside the same class.

Example:

```java
class Employee {


private double salary;


public double getSalary(){

    return salary;

}


}
```

Outside:

```java
Employee e = new Employee();

e.salary = 10000;
```

Compilation error.

---

## Why private?

Used for:

* Data hiding
* Encapsulation
* Prevent invalid state

Example:

```java
class Account {


private double balance;


public void deposit(double amount){

    if(amount > 0){

        balance += amount;

    }

}

}
```

---

# 4. Default Access (Package Private) ⭐⭐⭐⭐

If no modifier is written:

```java
class Employee {


String name;


void print(){

}


}
```

This is:

```text
default access
```

Meaning:

Accessible only inside the same package.

---

Example:

Package:

```
com.company.employee
```

Class:

```java
class Employee {

}
```

Another class:

```java
package com.company.employee;


class EmployeeService {


Employee e = new Employee();


}
```

Works.

---

Different package:

```
com.company.payment
```

```java
Employee e = new Employee();
```

Not accessible.

---

# 5. protected ⭐⭐⭐⭐⭐

Most asked modifier.

Definition:

> Protected members are accessible within the same package and also accessible in subclasses outside the package.

Example:

```java
class Animal {


protected String name;


}
```

Same package:

```java
class Dog {


void print(){

System.out.println(name);

}

}
```

Works.

---

Different package:

Parent:

```java
package animals;


public class Animal {


protected String name;


}
```

Child:

```java
package pets;


class Dog extends Animal {


void display(){

System.out.println(name);

}


}
```

Works.

---

# Protected Important Rule ⭐⭐⭐⭐⭐

Different package subclass can access protected member only through inheritance.

Example:

Parent:

```java
package a;


public class Parent {


protected int value;


}
```

Child:

```java
package b;


class Child extends Parent {


void test(){

    System.out.println(value);

}

}
```

Valid.

---

But:

```java
class Child extends Parent {


void test(Parent p){

    System.out.println(p.value);

}

}
```

May fail if accessing through parent reference from another package.

---

Why?

Because protected means:

> "I trust my subclasses"

not:

> "I expose it publicly."

---

# 6. public ⭐⭐⭐⭐⭐

Accessible everywhere.

Example:

```java
public class Employee {


public void work(){

}

}
```

Any package:

```java
Employee e =
new Employee();

e.work();
```

Allowed.

---

# 7. Class Level Access Modifiers ⭐⭐⭐⭐

Classes can only have:

```text
public
default
```

Example:

Valid:

```java
public class Employee {

}
```

or:

```java
class Employee {

}
```

---

Invalid:

```java
private class Employee {

}
```

Top-level classes cannot be:

* private
* protected

---

Why?

Because top-level classes belong to package, not another class.

---

# 8. Inner Classes Can Have All Modifiers ⭐⭐⭐⭐

Example:

```java
class Outer {


private class Inner {


}


protected class Test {


}


}
```

Allowed.

Because inner classes behave like members.

---

# 9. Access Modifier with Methods ⭐⭐⭐⭐⭐

Example:

```java
class Employee {


private void calculateSalary(){

}


public void process(){

    calculateSalary();

}

}
```

Private methods are used internally.

---

# 10. Access Modifier with Variables

Common pattern:

```java
class User {


private String username;


private int age;



public String getUsername(){

    return username;

}


}
```

Why?

Prevent direct modification.

---

# 11. Access Modifier and Overriding ⭐⭐⭐⭐

Important interview question.

Can we reduce visibility while overriding?

No.

Example:

Parent:

```java
class Parent {


public void display(){

}


}
```

Child:

```java
class Child extends Parent {


private void display(){

}


}
```

Compilation error.

---

Reason:

Child would reduce accessibility.

Parent contract says:

```text
Everyone can call display()
```

Child says:

```text
Only me can call display()
```

Violation.

---

But increasing visibility is allowed.

Parent:

```java
protected void display(){

}
```

Child:

```java
public void display(){

}
```

Allowed.

---

# 12. Constructor Access Modifiers ⭐⭐⭐⭐

Constructors can have:

* private
* default
* protected
* public

---

## Private Constructor

Used for:

* Singleton
* Utility classes

Example:

```java
class Singleton {


private Singleton(){}


}
```

Cannot create:

```java
new Singleton();
```

---

## Protected Constructor

Used in inheritance scenarios.

---

# 13. Access Modifier and Encapsulation ⭐⭐⭐⭐⭐

Good design:

```java
class Employee {


private int salary;


public void increaseSalary(int amount){

    if(amount > 0){

        salary += amount;

    }

}

}
```

Bad:

```java
class Employee {


public int salary;

}
```

Anyone can modify.

---

# 14. Real Spring Example

Controller:

```java
@RestController
public class UserController {


private final UserService service;


public UserController(UserService service){

    this.service = service;

}

}
```

Why private?

Dependency should not be changed.

---

Service:

```java
@Service
class UserService {


private UserRepository repository;


}
```

Internal implementation hidden.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Difference between protected and default?

Answer:

Default:

```
same package only
```

Protected:

```
same package
+
subclasses outside package
```

---

## Q2. Can private methods be overridden?

No.

Because private methods are not visible to child classes.

They are not inherited.

---

## Q3. Can we override public method with protected?

No.

Because visibility cannot be reduced.

---

## Q4. Why top-level class cannot be private?

Because a top-level class is not owned by another class. Private means accessible only inside enclosing class.

---

## Q5. Which modifier gives maximum restriction?

Answer:

```text
private
```

---

## Q6. Why variables are usually private?

Because:

* Protect state
* Enable validation
* Allow future changes without breaking callers

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ All four modifiers
✅ Protected tricky behaviour
✅ Package vs inheritance
✅ Class-level restrictions
✅ Overriding visibility rules
✅ Encapsulation connection

Deep dive not required:

* JVM access checks bytecode level
* SecurityManager history

---

Next topic:

# `abstract` Keyword ⭐⭐⭐⭐

We will cover:

* Abstract class
* Abstract method
* Interface vs abstract class
* When to choose which
* Constructor behaviour
* Real production usage
* Interview traps

This will complete the OOP inheritance/abstraction connection.

# `abstract` Keyword ⭐⭐⭐⭐

`abstract` is an important OOP concept because it connects directly with:

* Abstraction
* Inheritance
* Polymorphism
* Template Method Pattern
* Framework design (Spring, Hibernate, etc.)

For a **7+ years Java developer**, interviewer expects you to explain not only syntax but **when and why to use abstract class vs interface**.

---

# 1. What is `abstract`?

## Interview Definition

> `abstract` is a keyword used to define incomplete classes or methods. An abstract class provides a common blueprint, while subclasses provide the missing implementation.

Meaning:

```text
Abstract class
        |
        |
        +---- Common behaviour
        |
        +---- Missing behaviour (abstract methods)
```

---

# 2. Abstract Class ⭐⭐⭐⭐⭐

Example:

```java
abstract class Vehicle {


    String brand;


    void start(){

        System.out.println("Vehicle starting");

    }


    abstract void move();


}
```

Here:

Concrete method:

```java
start()
```

already has implementation.

Abstract method:

```java
move()
```

has no implementation.

---

# 3. Can We Create Object of Abstract Class?

No.

Example:

```java
Vehicle v = new Vehicle();
```

Compilation error:

```text
Vehicle is abstract; cannot be instantiated
```

Why?

Because abstract class is incomplete.

It has methods without implementation.

---

# 4. How Do We Use Abstract Class?

Create child class.

Example:

```java
class Car extends Vehicle {


    @Override
    void move(){

        System.out.println("Car moves on road");

    }

}
```

Now:

```java
Vehicle v = new Car();

v.move();
```

Output:

```
Car moves on road
```

This is runtime polymorphism.

---

# 5. Abstract Method ⭐⭐⭐⭐⭐

An abstract method has:

* No body
* Must be implemented by child class

Example:

```java
abstract class Payment {


    abstract void pay(double amount);


}
```

Implementation:

```java
class CreditCardPayment extends Payment {


    void pay(double amount){

        System.out.println("Paid using card");

    }

}
```

---

# 6. Rules of Abstract Methods ⭐⭐⭐⭐⭐

## Rule 1: Abstract method cannot have body

Invalid:

```java
abstract void process(){

    System.out.println("Processing");

}
```

Compilation error.

---

Correct:

```java
abstract void process();
```

---

## Rule 2: Child must implement abstract methods

Example:

```java
abstract class Animal {

    abstract void sound();

}
```

Child:

```java
class Dog extends Animal {


}
```

Compilation error.

Because:

```text
Dog is incomplete
```

---

Solution:

```java
class Dog extends Animal {


    void sound(){

        System.out.println("Bark");

    }

}
```

---

## Rule 3: If child does not implement, child must also be abstract

Example:

```java
abstract class Animal {

    abstract void sound();

}


abstract class Dog extends Animal {

}
```

Allowed.

---

# 7. Abstract Class Can Have Constructor ⭐⭐⭐⭐⭐

Very common interview question.

Question:

> If we cannot create abstract class object, why does it have a constructor?

Answer:

Because constructor initializes the parent part when child object is created.

Example:

```java
abstract class Vehicle {


    Vehicle(){

        System.out.println("Vehicle constructor");

    }

}
```

Child:

```java
class Car extends Vehicle {


    Car(){

        System.out.println("Car constructor");

    }

}
```

Create:

```java
Car c = new Car();
```

Output:

```
Vehicle constructor
Car constructor
```

---

Object creation flow:

```text
new Car()

    |
    |
    v

Vehicle constructor

    |
    |
    v

Car constructor
```

---

# 8. Abstract Class Can Have Variables ⭐⭐⭐⭐

Example:

```java
abstract class Employee {


    protected String name;


    protected int id;


}
```

Child inherits:

```java
class Developer extends Employee {


}
```

---

# 9. Abstract Class Can Have Static Methods ⭐⭐⭐

Example:

```java
abstract class Utility {


    static void print(){

        System.out.println("Hello");

    }

}
```

Allowed.

Call:

```java
Utility.print();
```

---

# 10. Abstract Class Can Have Final Methods ⭐⭐⭐⭐

Example:

```java
abstract class Payment {


    final void validate(){

        System.out.println("Validation");

    }


    abstract void pay();

}
```

Child:

```java
class CardPayment extends Payment {


    void pay(){

    }


}
```

Child cannot override:

```java
validate()
```

---

# 11. Abstract Class vs Interface ⭐⭐⭐⭐⭐

Most important comparison.

| Feature              | Abstract Class              | Interface                      |
| -------------------- | --------------------------- | ------------------------------ |
| Keyword              | abstract class              | interface                      |
| Methods              | Abstract + concrete         | Abstract + default/static      |
| Variables            | Instance variables allowed  | public static final by default |
| Constructor          | Yes                         | No                             |
| Multiple inheritance | No                          | Yes                            |
| State                | Can maintain state          | Cannot maintain instance state |
| Access modifiers     | Any                         | Methods public by default      |
| Relationship         | "is-a" with shared behavior | Contract                       |

---

# Example Decision

## Abstract Class

Use when classes share common code.

Example:

```java
abstract class Employee {


String name;


void login(){

}


abstract void calculateSalary();

}
```

All employees share login.

Salary differs.

---

## Interface

Use when unrelated classes share capability.

Example:

```java
interface Flyable {


void fly();

}
```

Implementations:

```java
class Bird implements Flyable {


}


class Airplane implements Flyable {


}
```

Bird and airplane are not the same family, but both can fly.

---

# 12. Abstract Class Real Production Example ⭐⭐⭐⭐⭐

Payment system:

```java
abstract class PaymentProcessor {


    public void processPayment(){

        validate();

        pay();

        sendReceipt();

    }


    protected void validate(){

        System.out.println("Validation");

    }


    protected abstract void pay();


    protected void sendReceipt(){

        System.out.println("Receipt sent");

    }

}
```

Implementations:

```java
class CardPaymentProcessor 
        extends PaymentProcessor {


    protected void pay(){

        System.out.println("Card payment");

    }

}
```

Flow:

```text
processPayment()

        |
        |
 validate()

        |
        |
 pay()
        |
        |
 receipt
```

This is the **Template Method Pattern**.

---

# 13. Abstract Class and Polymorphism ⭐⭐⭐⭐⭐

Example:

```java
PaymentProcessor payment =
        new CardPaymentProcessor();

payment.processPayment();
```

Reference:

```text
PaymentProcessor
```

Object:

```text
CardPaymentProcessor
```

Runtime decides:

```text
which pay() implementation runs
```

---

# 14. Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Can abstract class have constructor?

Yes.

Used for initializing common parent state.

---

## Q2. Can abstract class have no abstract methods?

Yes.

Example:

```java
abstract class Utility {

    void print(){

    }

}
```

Why?

To prevent direct object creation.

---

## Q3. Can abstract method be private?

No.

Why?

Because child classes need to override it.

Private methods are not inherited.

---

## Q4. Can abstract method be static?

No.

Because static belongs to class, not object.

Abstract requires subclass implementation.

---

## Q5. Can abstract class implement interface?

Yes.

Example:

```java
abstract class Animal 
implements Serializable {

}
```

---

## Q6. Difference between abstraction and encapsulation?

Very common.

### Abstraction:

Hides implementation complexity.

Example:

```java
payment.pay();
```

You don't know internal payment flow.

---

### Encapsulation:

Hides internal state.

Example:

```java
private balance;
```

Access through methods.

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ Abstract class
✅ Abstract method
✅ Why cannot instantiate
✅ Constructor execution
✅ Abstract vs interface
✅ Template method pattern
✅ Runtime polymorphism connection

Deep dive not required:

* Bytecode representation of abstract methods
* JVM method table internals

---

Next topic:

# `this` and `super` Keywords ⭐⭐⭐⭐

We will cover:

* Current object reference (`this`)
* Constructor chaining
* Parent reference (`super`)
* Method overriding behaviour
* `this()` vs `super()`
* Initialization order
* Common interview traps

# `this` and `super` Keywords ⭐⭐⭐⭐

These two keywords are fundamental for understanding:

* Object creation lifecycle
* Constructor chaining
* Inheritance
* Method overriding
* Parent-child relationship
* Runtime polymorphism

For senior interviews, common questions are:

* Difference between `this` and `super`
* `this()` vs `super()`
* Why `super()` is called first
* Can we use `this` in static methods?
* Constructor execution order

---

# 1. `this` Keyword ⭐⭐⭐⭐⭐

## Interview Definition

> `this` refers to the current object instance.

Meaning:

Inside an object method, `this` points to the object whose method is currently executing.

---

Example:

```java
class Employee {

    String name;


    void setName(String name){

        this.name = name;

    }

}
```

Here:

```java
this.name
```

means:

```text
current object's variable
```

and:

```java
name
```

means:

```text
method parameter
```

---

# Why do we need `this`?

Consider:

```java
class Employee {

    String name;


    void setName(String name){

        name = name;

    }

}
```

Problem:

Both names refer to parameter.

Execution:

```text
parameter name
        |
        |
        v

local variable

```

Instance variable never gets updated.

---

Correct:

```java
this.name = name;
```

Meaning:

```text
this.name  -> object variable

name       -> method parameter
```

---

# 2. `this` Represents Current Object ⭐⭐⭐⭐⭐

Example:

```java
class Employee {


    void display(){

        System.out.println(this);

    }

}
```

Usage:

```java
Employee e = new Employee();

e.display();
```

Output:

```
Employee@15db9742
```

Because:

```text
this == e
```

---

# 3. `this()` Constructor Chaining ⭐⭐⭐⭐⭐

`this()` calls another constructor of the same class.

Example:

```java
class Employee {


    String name;
    int age;


    Employee(){

        this("Unknown",0);

    }


    Employee(String name,int age){

        this.name = name;
        this.age = age;

    }

}
```

Flow:

```text
new Employee()

        |
        |
        v

Employee()

        |
        |
        v

Employee(String,int)
```

---

# Rule of this()

`this()` must be the first statement.

Invalid:

```java
Employee(){

    System.out.println("Hello");

    this("Unknown",0);

}
```

Compilation error.

Why?

Constructor chaining must happen before current constructor execution.

---

# 4. `this` Passing Current Object ⭐⭐⭐

Example:

```java
class Employee {


    void process(){

        Service service = new Service();

        service.save(this);

    }

}
```

Here:

```java
this
```

passes current Employee object.

---

Example:

```java
class Service {


    void save(Employee employee){

        System.out.println(employee);

    }

}
```

---

# 5. `this` in Method Chaining ⭐⭐⭐

Example:

```java
class Builder {


    Builder setName(String name){

        this.name = name;

        return this;

    }


    Builder setAge(int age){

        this.age = age;

        return this;

    }

}
```

Usage:

```java
new Builder()
    .setName("John")
    .setAge(30);
```

Commonly used in:

* Builder Pattern
* Fluent APIs

---

# 6. Can we use `this` in Static Method?

No.

Example:

```java
class Test {


static void display(){

    System.out.println(this);

}

}
```

Compilation error.

---

Why?

Static belongs to class.

No object exists.

Remember:

```text
static method

class level


this

object level
```

---

# 7. `super` Keyword ⭐⭐⭐⭐⭐

## Interview Definition

> `super` refers to the immediate parent class object.

Used for:

1. Access parent variables
2. Call parent methods
3. Call parent constructor

---

# 8. super Access Parent Variable

Example:

```java
class Employee {

    String name = "Employee";

}


class Manager extends Employee {


    String name = "Manager";


    void print(){

        System.out.println(name);

        System.out.println(super.name);

    }

}
```

Output:

```
Manager
Employee
```

---

Without `super`:

```java
name
```

searches current class first.

---

With:

```java
super.name
```

go directly to parent.

---

# 9. super Calling Parent Method ⭐⭐⭐⭐⭐

Example:

```java
class Employee {


    void work(){

        System.out.println("Employee working");

    }

}


class Manager extends Employee {


    void work(){

        System.out.println("Manager managing");

    }


    void display(){

        super.work();

        work();

    }

}
```

Output:

```
Employee working
Manager managing
```

---

Why?

Because:

```java
super.work()
```

forces parent implementation.

---

# 10. super() Constructor Calling ⭐⭐⭐⭐⭐

`super()` calls parent constructor.

Example:

```java
class Employee {


    Employee(){

        System.out.println("Employee constructor");

    }

}


class Manager extends Employee {


    Manager(){

        System.out.println("Manager constructor");

    }

}
```

Create:

```java
new Manager();
```

Output:

```
Employee constructor
Manager constructor
```

---

Why?

Because compiler automatically inserts:

```java
super();
```

inside child constructor.

---

Actual:

```java
Manager(){

    super();

    System.out.println("Manager constructor");

}
```

---

# 11. Constructor Execution Order ⭐⭐⭐⭐⭐

Example:

```java
class A {


    A(){

        System.out.println("A");

    }

}


class B extends A {


    B(){

        System.out.println("B");

    }

}


class C extends B {


    C(){

        System.out.println("C");

    }

}
```

Create:

```java
new C();
```

Output:

```
A
B
C
```

---

Flow:

```text
new C()

 |
 |
 v

C constructor

 |
 |
super()

 |
 |
 v

B constructor

 |
 |
super()

 |
 |
 v

A constructor

 |
 |
 v

B body

 |
 |
 v

C body
```

---

# 12. this() vs super() ⭐⭐⭐⭐⭐

| this()                       | super()                  |
| ---------------------------- | ------------------------ |
| Calls same class constructor | Calls parent constructor |
| Constructor chaining         | Inheritance chaining     |
| Must be first statement      | Must be first statement  |
| Cannot use both together     | Cannot use both together |

---

Example:

Invalid:

```java
class Child extends Parent {


Child(){

    this();

    super();

}

}
```

Only one can execute first.

---

# 13. Can Constructor Have Both this() and super()?

No.

Reason:

Both require first position.

Example:

```java
Child(){

    this(10);

    super();

}
```

Invalid.

---

# 14. super and Method Overriding ⭐⭐⭐⭐⭐

Example:

```java
class Payment {


void pay(){

    System.out.println("Payment");

}

}


class CardPayment extends Payment {


@Override
void pay(){

    super.pay();

    System.out.println("Card Payment");

}

}
```

Output:

```
Payment
Card Payment
```

Useful when child wants to extend parent behaviour.

---

# 15. this vs super Reference ⭐⭐⭐⭐⭐

Example:

```java
class Parent {


}


class Child extends Parent {


void show(){

    System.out.println(this);

    System.out.println(super);

}

}
```

Conceptually:

```text
this

current Child object


super

Parent part of same object
```

Important:

`super` is not a separate object.

---

Memory:

```text
Heap


Child Object

+----------------+
| Parent fields  |
| Parent methods |
+----------------+
| Child fields   |
| Child methods  |
+----------------+

        ^
        |
      this


super points to parent portion
```

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Difference between this and super?

Answer:

`this`:

* Current object
* Current class members
* Same class constructor

`super`:

* Parent object reference
* Parent class members
* Parent constructor

---

## Q2. Why is super() called first?

Because parent state must be initialized before child state.

---

## Q3. Can static method use this?

No.

Because static has no object context.

---

## Q4. Can we call super() anywhere?

No.

It must be the first statement in constructor.

---

## Q5. Is super a reference variable?

Conceptually yes, but it does not refer to a separate parent object.

It refers to parent part of current object.

---

## Q6. What happens if parent has only parameterized constructor?

Example:

```java
class Parent {


Parent(int x){

}

}
```

Child:

```java
class Child extends Parent {


Child(){

}

}
```

Compilation error.

Why?

Compiler tries:

```java
super();
```

but no default constructor exists.

Fix:

```java
Child(){

    super(10);

}
```

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ this reference
✅ this() constructor chaining
✅ super reference
✅ super() constructor call
✅ Constructor execution order
✅ Parent method invocation
✅ Static limitation
✅ this vs super difference

Deep dive not required:

* JVM bytecode instructions (`aload_0`, invokespecial)
* Object header reference details

---

Next topic:

# `volatile`, `synchronized`, `transient` Keywords ⭐⭐⭐⭐⭐

These connect with:

* Java Memory Model
* Multithreading
* Serialization
* Production concurrency issues

We will cover them before moving into Threading.

# `volatile`, `synchronized`, `transient` Keywords ⭐⭐⭐⭐⭐

These three keywords are very important for senior Java interviews because they connect with:

* Java Memory Model (JMM)
* Multithreading
* Thread safety
* Visibility problems
* Object serialization
* Distributed systems concepts

We will cover:

1. `volatile`
2. `synchronized`
3. `transient`

---

# 1. `volatile` Keyword ⭐⭐⭐⭐⭐

## Interview Definition

> `volatile` tells JVM that a variable's value can be modified by multiple threads and every read should come from main memory instead of thread-local cache.

The key concept:

**Visibility guarantee.**

---

# Why do we need volatile?

Consider:

```java
class Flag {

    boolean running = true;


    void stop(){

        running = false;

    }


    void process(){

        while(running){

            // do work

        }

    }

}
```

Two threads:

```
Thread 1                 Thread 2

process()                stop()

read running             update running=false


```

Problem:

Thread 1 may keep seeing:

```
running = true
```

because it cached the value.

---

# CPU Cache Problem ⭐⭐⭐⭐⭐

Modern systems:

```
              Main Memory

                  |
                  |
        ---------------------
        |                   |
      CPU 1               CPU 2

    Cache               Cache

 running=true       running=true

```

Thread 2 updates:

```
running=false
```

but Thread 1 may still see:

```
running=true
```

---

# Solution: volatile

```java
class Flag {

    volatile boolean running = true;

}
```

Now:

```
Thread writes

      |
      |
      v

Main Memory


Thread reads latest value
```

---

# volatile Guarantees ⭐⭐⭐⭐⭐

## 1. Visibility

Example:

```java
volatile boolean shutdown;
```

One thread changes:

```java
shutdown=true;
```

Other thread immediately sees it.

---

## 2. Prevents instruction reordering

Compiler/JVM cannot reorder volatile operations in unsafe ways.

Example:

Without volatile:

```
Operation A

Operation B

```

Compiler may reorder:

```
Operation B

Operation A

```

With volatile:

Ordering guarantees are stronger.

---

# What volatile DOES NOT Guarantee ⭐⭐⭐⭐⭐

Very important interview question.

`volatile` does NOT provide atomicity.

Example:

```java
volatile int count = 0;


count++;
```

Looks like one operation.

Actually:

```
read count

+

increment

+

write count
```

Three steps.

---

Two threads:

```
Thread 1:
read 0
increment 1


Thread 2:
read 0
increment 1


Final value = 1
```

Expected:

```
2
```

Problem:

Race condition.

---

Solution:

Use:

```java
AtomicInteger
```

or:

```java
synchronized
```

---

# volatile Example Use Cases ⭐⭐⭐⭐

## 1. Shutdown flag

Very common.

```java
class Worker {


private volatile boolean running=true;


public void stop(){

    running=false;

}


public void run(){

    while(running){

        process();

    }

}

}
```

---

## 2. Singleton Double Checked Locking

Example:

```java
class Singleton {


private volatile static Singleton instance;


public static Singleton getInstance(){

    if(instance==null){

        synchronized(Singleton.class){

            if(instance==null){

                instance=new Singleton();

            }

        }

    }

    return instance;

}

}
```

Why volatile?

Because object creation:

```
memory allocation

constructor execution

reference assignment
```

can be reordered.

---

# 2. `synchronized` Keyword ⭐⭐⭐⭐⭐

## Interview Definition

> synchronized provides mutual exclusion and guarantees that only one thread can execute a critical section at a time.

It provides:

1. Atomicity
2. Visibility
3. Ordering

---

# Race Condition Example

Without synchronization:

```java
class Counter {


int count;


void increment(){

    count++;

}

}
```

Two threads:

```
Thread 1:

read 0
add 1
write 1


Thread 2:

read 0
add 1
write 1

```

Result:

```
count = 1
```

Expected:

```
count = 2
```

---

# synchronized Method

```java
class Counter {


int count;


synchronized void increment(){

    count++;

}

}
```

Now:

```
Thread 1 enters

        |
        |
        v

Lock acquired


        |
        |
        v

Thread 2 waits

```

---

# How synchronized Works?

Every object has an intrinsic lock (monitor).

Example:

```java
synchronized(this){

}
```

means:

```
Acquire object's monitor

Execute block

Release monitor
```

---

# Types of synchronized ⭐⭐⭐⭐⭐

## 1. Synchronized Instance Method

```java
public synchronized void process(){

}
```

Lock:

```
current object
```

Equivalent:

```java
synchronized(this){

}
```

---

## 2. Synchronized Block

```java
public void process(){

    synchronized(this){

    }

}
```

Allows smaller critical section.

Better performance.

---

## 3. Static synchronized Method

Example:

```java
public static synchronized void process(){

}
```

Lock:

```
Class object
```

Equivalent:

```java
synchronized(MyClass.class){

}
```

---

# Object Lock vs Class Lock ⭐⭐⭐⭐⭐

Example:

```java
class Test {


synchronized void method1(){

}


static synchronized void method2(){

}


}
```

Locks:

```
method1()

Object instance lock


method2()

Class lock

```

They are different.

---

# synchronized vs volatile ⭐⭐⭐⭐⭐

|             | volatile   | synchronized      |
| ----------- | ---------- | ----------------- |
| Purpose     | Visibility | Thread safety     |
| Atomicity   | No         | Yes               |
| Lock        | No         | Yes               |
| Performance | Faster     | Slower            |
| Use case    | Flags      | Critical sections |

---

Example:

volatile:

```java
volatile boolean running;
```

synchronized:

```java
synchronized void updateBalance(){

}
```

---

# 3. `transient` Keyword ⭐⭐⭐⭐

## Interview Definition

> transient prevents a field from being serialized.

Used with:

```java
Serializable
```

---

# Serialization Reminder

Serialization:

```
Object

   |
   |
   v

Byte stream

   |
   |
   v

File / Network
```

Example:

```java
class User implements Serializable {


String username;


String password;


}
```

Serialize:

```
username
password
```

both stored.

---

# Problem

Password should not be stored.

Use transient:

```java
class User implements Serializable {


String username;


transient String password;


}
```

Serialization:

```
username
   |
   v
saved


password
   |
   v
ignored

```

---

# Example

```java
User user = new User();

user.username="john";
user.password="secret";
```

After serialization:

```
username = john

password = null
```

---

# transient and static ⭐⭐⭐

Static fields are not serialized anyway.

Example:

```java
class User implements Serializable {


static String company;


transient String password;

}
```

Serialization ignores:

```
company
password
```

---

# transient and final ⭐⭐⭐

Interesting case.

Example:

```java
class User implements Serializable {


transient final String password="abc";

}
```

After deserialization:

Value may not be restored.

Because transient fields are skipped.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Difference between volatile and synchronized?

Answer:

`volatile`:

* Visibility
* No locking
* No atomicity

`synchronized`:

* Locking
* Atomicity
* Visibility

---

## Q2. Is volatile thread safe?

Answer:

No.

It only guarantees visibility.

Example:

```java
volatile int count;

count++;
```

is not thread safe.

---

## Q3. When should we use volatile?

Answer:

When:

* One thread writes
* Multiple threads read
* Operation is simple assignment

Example:

```java
volatile boolean flag;
```

---

## Q4. Does synchronized block only one thread?

Answer:

Only threads trying to acquire the same lock.

Different objects have different locks.

---

## Q5. What happens to transient fields after deserialization?

Answer:

They get default values.

Example:

```
int -> 0

boolean -> false

Object -> null
```

---

## Q6. Can constructor be synchronized?

No.

Because object does not exist until constructor completes.

---

# Production Examples

## volatile

Shutdown signal:

```java
private volatile boolean running;
```

---

## synchronized

Bank balance update:

```java
public synchronized void withdraw(){

}
```

---

## transient

Security fields:

```java
private transient String password;
```

---

# Interview Boundary

For 7+ years Java developer:

Must know deeply:

✅ volatile visibility
✅ volatile not atomic
✅ synchronized locks
✅ object lock vs class lock
✅ synchronized block
✅ transient serialization behaviour
✅ volatile singleton usage

Deep dive not required:

* JVM monitor implementation
* biased locking details
* lock inflation mechanics

---

Next topic:

# Java `interface` ⭐⭐⭐⭐⭐

We will cover:

* Interface evolution (Java 8/9/17)
* Default methods
* Static methods
* Functional interfaces
* Interface vs abstract class
* Multiple inheritance
* Real Spring usage

This will complete the abstraction/OOP foundation before moving to Collections + Threads.

# Java Interface ⭐⭐⭐⭐⭐

Interfaces are one of the most important Java topics for senior developers because they are everywhere:

* Spring dependency injection
* Microservices contracts
* Design patterns
* API design
* Polymorphism
* Multiple inheritance
* Functional programming (Java 8+)

For a **7+ years Java developer**, interviewer expects more than syntax:

* Why interfaces exist
* Interface vs abstract class
* Default methods
* Static methods
* Functional interfaces
* Interface evolution problems

---

# 1. What is an Interface?

## Interview Definition

> An interface defines a contract that implementing classes must follow. It specifies what a class can do, without specifying how it does it.

Example:

```java
interface Payment {

    void pay();

}
```

Implementation:

```java
class CreditCardPayment implements Payment {


    public void pay(){

        System.out.println("Payment using card");

    }

}
```

Usage:

```java
Payment payment = new CreditCardPayment();

payment.pay();
```

Output:

```
Payment using card
```

---

# 2. Why Do We Need Interfaces?

Main reasons:

## 1. Abstraction

Hide implementation details.

Example:

```java
payment.pay();
```

Caller does not know:

* Bank API call
* Validation
* Encryption
* Network communication

---

## 2. Loose Coupling ⭐⭐⭐⭐⭐

Bad design:

```java
class OrderService {


    private StripePayment payment =
            new StripePayment();


}
```

Problem:

OrderService is tightly coupled to Stripe.

Tomorrow:

```
Stripe
 |
 |
change to
 |
 |
PayPal
```

You need code changes.

---

Better:

```java
interface Payment {

    void pay();

}
```

```java
class OrderService {


    private Payment payment;


    OrderService(Payment payment){

        this.payment = payment;

    }

}
```

Now:

```
OrderService

      |
      |
   Payment interface

      |
      |
--------------------
|                  |
Stripe          PayPal
```

---

# 3. Interface Syntax ⭐⭐⭐⭐

Example:

```java
public interface Vehicle {


    void start();


    void stop();


}
```

Implement:

```java
class Car implements Vehicle {


    public void start(){

        System.out.println("Car starts");

    }


    public void stop(){

        System.out.println("Car stops");

    }

}
```

---

# 4. Interface Methods Before Java 8 ⭐⭐⭐

Before Java 8:

Interface could have only:

```java
public abstract methods
```

Example:

```java
interface Animal {


    void sound();


}
```

Compiler internally treats:

```java
public abstract void sound();
```

---

# 5. Interface Variables ⭐⭐⭐⭐⭐

All interface variables are automatically:

```text
public static final
```

Example:

```java
interface Config {


    int MAX_CONNECTION = 10;


}
```

Compiler converts:

```java
public static final int MAX_CONNECTION = 10;
```

---

Meaning:

## public

Accessible everywhere.

## static

Belongs to interface.

## final

Cannot change.

---

Example:

```java
Config.MAX_CONNECTION = 20;
```

Compilation error.

---

# 6. Default Methods (Java 8) ⭐⭐⭐⭐⭐

This was introduced to solve interface evolution problems.

Problem:

Interface:

```java
interface Payment {


void pay();


}
```

Classes:

```java
class CardPayment implements Payment {

}

class UpiPayment implements Payment {

}
```

Now add new method:

```java
void refund();
```

Every implementation breaks.

---

Solution:

Default method.

```java
interface Payment {


void pay();


default void refund(){

    System.out.println("Refund processing");

}


}
```

Now existing classes continue working.

---

# Why default methods?

Main reason:

> Maintain backward compatibility when interfaces evolve.

Example:

Java 8 added:

```java
stream()
```

to Collection interfaces.

Existing implementations did not break because of default methods.

---

# 7. Can Default Methods Be Overridden?

Yes.

Example:

```java
interface Payment {


default void refund(){

    System.out.println("Default refund");

}

}
```

Implementation:

```java
class CardPayment implements Payment {


@Override
public void refund(){

    System.out.println("Card refund");

}

}
```

---

# 8. Interface Static Methods ⭐⭐⭐⭐

Java 8 introduced static methods.

Example:

```java
interface Utility {


static void print(){

    System.out.println("Hello");

}

}
```

Call:

```java
Utility.print();
```

Important:

Static interface methods are NOT inherited.

Example:

```java
class Test implements Utility {


}
```

Cannot:

```java
Test.print();
```

---

# 9. Functional Interface ⭐⭐⭐⭐⭐

Very important for Java 8 streams and lambda.

## Definition

> Interface with exactly one abstract method.

Example:

```java
@FunctionalInterface
interface Calculator {


int calculate(int a,int b);


}
```

Usage:

```java
Calculator add =
(a,b)->a+b;


System.out.println(add.calculate(10,20));
```

Output:

```
30
```

---

Examples from Java:

### Runnable

```java
@FunctionalInterface
interface Runnable {


void run();

}
```

---

### Comparator

```java
compare(T o1,T o2)
```

---

### Callable

```java
call()
```

---

# 10. Can Functional Interface Have Default Methods?

Yes.

Example:

```java
@FunctionalInterface
interface Calculator {


int calculate();


default void print(){

}


}
```

Allowed.

Because:

Only abstract methods count.

---

# 11. Multiple Interface Implementation ⭐⭐⭐⭐⭐

Java does not support multiple class inheritance:

Invalid:

```java
class C extends A,B
{

}
```

Reason:

Diamond problem.

---

But interfaces allow:

```java
interface A {


void show();

}


interface B {


void print();

}


class C implements A,B {


public void show(){

}


public void print(){

}


}
```

---

# 12. Diamond Problem with Interfaces ⭐⭐⭐⭐⭐

Example:

```java
interface A {


default void display(){

System.out.println("A");

}

}


interface B {


default void display(){

System.out.println("B");

}

}
```

Class:

```java
class C implements A,B {


}
```

Compilation error.

Why?

Ambiguous:

```
A.display()
       |
       |
       ?
       |
B.display()
```

---

Solution:

Override:

```java
class C implements A,B {


public void display(){

    A.super.display();

}

}
```

---

# 13. Interface vs Abstract Class ⭐⭐⭐⭐⭐

Very common interview question.

| Feature              | Interface                    | Abstract Class             |
| -------------------- | ---------------------------- | -------------------------- |
| Purpose              | Contract                     | Common base implementation |
| Methods              | Abstract + default + static  | Abstract + concrete        |
| Variables            | public static final          | Instance variables allowed |
| Constructor          | No                           | Yes                        |
| Multiple inheritance | Yes                          | No                         |
| State                | Cannot maintain object state | Can maintain state         |
| Coupling             | Lower                        | Higher                     |

---

# When to choose?

## Use Interface

When you define capability.

Example:

```java
interface Flyable {

    void fly();

}
```

Classes:

```
Bird
Airplane
Drone
```

---

## Use Abstract Class

When classes share common state/behavior.

Example:

```java
abstract class Employee {


String name;


void login(){

}


abstract void calculateSalary();

}
```

---

# 14. Interface in Spring ⭐⭐⭐⭐⭐

Very common production usage.

Example:

```java
public interface UserService {


User findUser(Long id);


}
```

Implementation:

```java
@Service
class UserServiceImpl implements UserService {


public User findUser(Long id){

}


}
```

Controller:

```java
@RestController
class UserController {


private final UserService service;


UserController(UserService service){

    this.service = service;

}

}
```

Spring injects implementation.

Benefits:

* Testing
* Loose coupling
* Replace implementations

---

# 15. Interface and Dependency Inversion Principle

SOLID DIP:

Bad:

```java
class OrderService {


MySQLRepository repo =
new MySQLRepository();

}
```

Good:

```java
class OrderService {


Repository repo;


OrderService(Repository repo){

this.repo=repo;

}

}
```

Interface becomes the contract.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Why Java does not allow multiple class inheritance?

Because of ambiguity (diamond problem).

---

## Q2. Can interface have constructor?

No.

Interfaces cannot create objects.

---

## Q3. Can interface extend another interface?

Yes.

Example:

```java
interface A {


}


interface B extends A {


}
```

---

## Q4. Can class implement multiple interfaces?

Yes.

Example:

```java
class PaymentService 
implements Serializable, Runnable {

}
```

---

## Q5. Why default methods were introduced?

To add new methods to existing interfaces without breaking implementations.

---

## Q6. Can interface have private methods?

Yes, Java 9 introduced private methods inside interfaces.

Example:

```java
interface Logger {


default void log(){

    format();

}


private void format(){

}

}
```

Purpose:

Reuse internal interface logic.

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ Interface purpose
✅ Loose coupling
✅ Interface variables
✅ Default methods
✅ Static methods
✅ Functional interfaces
✅ Multiple inheritance
✅ Diamond problem
✅ Interface vs abstract class
✅ Spring usage

Deep dive not required:

* JVM interface method dispatch internals
* Bytecode invokeinterface instruction

---

Next topic:

# `final` Keyword ⭐⭐⭐⭐⭐

We will cover:

* final variable
* final method
* final class
* final reference vs immutable object
* String final design
* Why immutable classes use final
* Interview traps (`final` vs `finally` vs `finalize`)


# `final` Keyword ⭐⭐⭐⭐⭐

`final` is a very common senior Java interview topic because it connects with:

* Immutability
* Thread safety
* Design principles
* Inheritance control
* Object references
* JVM optimizations

For 7+ years experience, interviewers usually ask:

* Difference between final variable and immutable object
* Can final object be modified?
* Why String is final?
* Can final methods be overridden?
* Difference between final, finally, finalize

---

# 1. What is `final`?

## Interview Definition

> `final` is a keyword used to restrict modification. Once assigned, a final entity cannot be changed.

It can be applied to:

1. Variable
2. Method
3. Class

---

# 2. final Variable ⭐⭐⭐⭐⭐

A final variable can be assigned only once.

Example:

```java
class Employee {

    final int id = 100;

}
```

Now:

```java
employee.id = 200;
```

Compilation error.

---

## final Variable Must Be Initialized

Option 1:

```java
final int id = 10;
```

---

Option 2:

Initialize in constructor:

```java
class Employee {

    final int id;


    Employee(int id){

        this.id = id;

    }

}
```

Allowed.

---

# 3. Blank final Variable ⭐⭐⭐⭐

A final variable without initialization is called a blank final variable.

Example:

```java
class Employee {


final int id;


Employee(int id){

    this.id=id;

}

}
```

Important:

It must be assigned exactly once.

---

# 4. final Reference Variable ⭐⭐⭐⭐⭐

Very important interview question.

Example:

```java
final Employee emp = new Employee();
```

Question:

Can we modify `emp`?

Answer:

Depends what you mean.

---

## Cannot change reference

Invalid:

```java
emp = new Employee();
```

Because reference is final.

Memory:

```
emp
 |
 |
 v

Employee Object 1


emp
 |
 |
 v

Employee Object 2  ❌
```

---

## But object can change

Example:

```java
class Employee {

    String name;

}
```

Usage:

```java
final Employee emp = new Employee();

emp.name = "John";
```

Allowed.

Why?

Because:

```
final reference
        |
        |
        v

same object

object state can change
```

---

# final Reference vs Immutable Object ⭐⭐⭐⭐⭐

Very common interview trap.

## final reference

Means:

```
Reference cannot point somewhere else
```

Example:

```java
final List<String> list = new ArrayList<>();
```

Allowed:

```java
list.add("Java");
```

Not allowed:

```java
list = new ArrayList<>();
```

---

## Immutable object

Means:

```
Object state cannot change
```

Example:

```java
String s = "Java";

s = "Python";
```

Looks like modification.

Actually:

```
Before:

s
 |
 v
"Java"


After:

s
 |
 v
"Python"
```

New object created.

Original object unchanged.

---

# 5. final Method ⭐⭐⭐⭐⭐

A final method cannot be overridden.

Example:

```java
class Vehicle {


final void start(){

    System.out.println("Starting");

}

}
```

Child:

```java
class Car extends Vehicle {


void start(){

}

}
```

Compilation error.

---

## Why make method final?

To prevent changing critical behavior.

Example:

```java
abstract class Bank {


final void processTransaction(){

    validate();

    debit();

    generateReceipt();

}


abstract void debit();

}
```

Child can change:

```
debit()
```

but cannot change:

```
transaction flow
```

This is Template Method Pattern.

---

# 6. final Class ⭐⭐⭐⭐⭐

A final class cannot be inherited.

Example:

```java
final class SecurityManager {


}
```

Invalid:

```java
class CustomSecurityManager 
extends SecurityManager {


}
```

---

# Why make class final?

Reasons:

## 1. Security

Prevent modification.

Example:

```java
String
```

is final.

---

## 2. Immutability

Immutable classes are usually final.

Example:

```java
public final class User {

}
```

Why?

Otherwise:

```java
class EvilUser extends User {

}
```

could break immutability.

---

## 3. Design decision

Class is not meant for extension.

---

# 7. Why is String final? ⭐⭐⭐⭐⭐

Classic interview question.

```java
public final class String
```

Reasons:

---

## 1. Security

Strings are used for:

* File paths
* URLs
* Database connections
* Class loading

Example:

```java
String path="/admin";
```

If String could be modified:

```
/admin

could become

/user
```

Security issue.

---

## 2. String Pool Safety

String pool stores shared objects.

Example:

```java
String a="Java";
String b="Java";
```

Both point to same object.

If String was mutable:

```
a changes Java -> Python

b also changes
```

Problem.

---

## 3. HashCode Caching

String is heavily used as HashMap key.

Example:

```java
Map<String,Integer> map;
```

If String changed:

```
hashCode changes
```

HashMap breaks.

---

# 8. final and Constructor ⭐⭐⭐⭐

Constructor itself cannot be final.

Invalid:

```java
final Employee(){

}
```

Why?

Constructor is not inherited.

Final prevents overriding.

Constructor cannot be overridden.

---

# 9. final Parameter ⭐⭐⭐

Example:

```java
void process(final int value){

}
```

Cannot:

```java
value = 100;
```

Useful when:

* Lambda usage
* Prevent accidental modification

---

Example:

```java
void print(final Employee e){

    e.name="John"; // allowed

}
```

Remember:

final reference ≠ immutable object.

---

# 10. final with Static ⭐⭐⭐⭐⭐

Common constant pattern:

```java
public static final String SERVICE_NAME="PAYMENT";
```

Meaning:

## static

One copy per class.

## final

Cannot change.

---

Memory:

```
PaymentService.class

SERVICE_NAME
     |
     v
"PAYMENT"
```

---

# 11. final and Thread Safety ⭐⭐⭐⭐

Final fields have special guarantees in Java Memory Model.

Example:

```java
class User {


private final String name;


User(String name){

    this.name=name;

}

}
```

After construction, other threads safely see:

```
name value
```

without synchronization (assuming object reference escapes safely).

---

This is one reason immutable objects are thread-safe.

---

# 12. final vs Immutability ⭐⭐⭐⭐⭐

Important distinction:

## final

Prevents reassignment.

Example:

```java
final int age=20;
```

---

## Immutable

Prevents state change.

Example:

```java
String name="Java";
```

---

To create immutable class:

```java
final class User {


private final String name;


User(String name){

    this.name=name;

}


public String getName(){

    return name;

}

}
```

Rules:

1. Class final
2. Fields private final
3. No setters
4. Initialize through constructor
5. Defensive copies for mutable fields

---

# 13. final vs finally vs finalize ⭐⭐⭐⭐⭐

Very common interview question.

|          | Meaning                   |
| -------- | ------------------------- |
| final    | Keyword for restriction   |
| finally  | Block used with try-catch |
| finalize | Old GC cleanup method     |

---

## final

```java
final int x=10;
```

---

## finally

```java
try{

}
finally{

    closeConnection();

}
```

Runs after try/catch.

---

## finalize

```java
protected void finalize(){

}
```

Called before garbage collection historically.

Deprecated since Java 9.

Why?

* Unpredictable execution
* Performance issues
* Can delay GC
* Security concerns

Use:

* try-with-resources
* AutoCloseable

instead.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Can final variable be changed?

No.

But if it is an object reference:

Reference cannot change, object may change.

---

## Q2. Can final method be overloaded?

Yes.

Example:

```java
final void print(int x)

final void print(String x)
```

Overloading is allowed.

---

## Q3. Can final class have child class?

No.

---

## Q4. Why String is immutable and final?

Because of:

* Security
* String pool
* HashMap key safety
* Thread safety

---

## Q5. Is immutable object automatically final?

Not necessarily.

But immutable classes are usually final to prevent subclasses breaking immutability.

---

## Q6. Are final variables stored in heap?

Depends.

* Instance final field → inside object on heap
* Static final → class metadata area
* Local final → stack frame

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ final variable
✅ final reference vs immutable object
✅ final method
✅ final class
✅ String final reason
✅ final + static constants
✅ final vs finally vs finalize
✅ Immutable class connection

Deep dive not required:

* JVM constant folding details
* Bytecode final checks

---

Next topic:

# `equals()` and `hashCode()` ⭐⭐⭐⭐⭐

We will cover:

* Object identity vs equality
* == vs equals()
* hashCode contract
* Why override both together
* HashMap internal dependency
* Common production bugs
* Interview scenarios (very frequently asked)


# `equals()` and `hashCode()` ⭐⭐⭐⭐⭐

This is one of the **most important Java interview topics** for 7+ years experience.

It connects with:

* Object identity
* Object comparison
* HashMap internals
* HashSet behaviour
* Caching
* Entity design (JPA/Hibernate)
* Immutability

Interviewers expect you to know:

* Difference between `==` and `equals()`
* Why override `hashCode()` with `equals()`
* HashMap dependency
* Common bugs when contract is violated

---

# 1. Object Identity vs Equality ⭐⭐⭐⭐⭐

First understand the difference.

## Identity

Question:

> Are these two references pointing to the same object?

Example:

```java
Employee e1 = new Employee("John");
Employee e2 = new Employee("John");
```

Memory:

```
Heap

Object 1
name="John"
   ^
   |
  e1


Object 2
name="John"
   ^
   |
  e2
```

They are different objects.

Identity:

```java
e1 == e2
```

Result:

```
false
```

---

## Equality

Question:

> Do these objects represent the same logical value?

Example:

Both employees:

```
name = John
id = 101
```

Business meaning:

"They are equal."

This is controlled by:

```java
equals()
```

---

# 2. `==` Operator ⭐⭐⭐⭐⭐

For objects:

`==` compares references.

Example:

```java
String a = new String("Java");
String b = new String("Java");


System.out.println(a == b);
```

Output:

```
false
```

Because:

```
a ----> "Java" object 1

b ----> "Java" object 2
```

---

# 3. `equals()` Method ⭐⭐⭐⭐⭐

`equals()` compares object content.

Example:

```java
String a = new String("Java");
String b = new String("Java");


System.out.println(a.equals(b));
```

Output:

```
true
```

Because String overrides equals.

---

# 4. Object Class equals() ⭐⭐⭐⭐⭐

Every Java class inherits from:

```java
java.lang.Object
```

Object provides:

```java
public boolean equals(Object obj)
```

Default implementation:

```java
public boolean equals(Object obj){

    return this == obj;

}
```

Meaning:

Default equals behaves like `==`.

---

Example:

```java
class Employee {


String name;


Employee(String name){

    this.name=name;

}


}
```

Usage:

```java
Employee e1 = new Employee("John");

Employee e2 = new Employee("John");


System.out.println(e1.equals(e2));
```

Output:

```
false
```

Why?

Because Employee did not override equals.

Default:

```
e1 == e2
```

---

# 5. Overriding equals() ⭐⭐⭐⭐⭐

Example:

```java
class Employee {


private int id;
private String name;


Employee(int id,String name){

    this.id=id;
    this.name=name;

}


@Override
public boolean equals(Object obj){

    if(this == obj)
        return true;


    if(!(obj instanceof Employee))
        return false;


    Employee other = (Employee)obj;


    return this.id == other.id
            &&
           this.name.equals(other.name);

}

}
```

Now:

```java
Employee e1 =
new Employee(101,"John");


Employee e2 =
new Employee(101,"John");


System.out.println(e1.equals(e2));
```

Output:

```
true
```

---

# 6. Why Override hashCode() with equals()? ⭐⭐⭐⭐⭐

This is the biggest interview point.

## Rule:

> If two objects are equal according to equals(), they MUST return the same hashCode().

Contract:

```
equals() true
        |
        |
        v
hashCode() must be same
```

---

Example:

```java
Employee e1 = new Employee(101,"John");

Employee e2 = new Employee(101,"John");
```

If:

```java
e1.equals(e2)
```

returns:

```
true
```

Then:

```java
e1.hashCode()
e2.hashCode()
```

must be:

```
same value
```

---

# 7. Why HashMap Needs hashCode()? ⭐⭐⭐⭐⭐

HashMap internally works using:

```
HashMap

      |
      |
      v

Bucket array

      |
      |
      v

hashCode()
      |
      |
      v

bucket location

      |
      |
      v

equals()
```

---

Example:

```java
Map<Employee,String> map = new HashMap<>();


Employee e1 =
new Employee(101,"John");


map.put(e1,"Developer");
```

Internally:

Step 1:

Calculate:

```java
e1.hashCode()
```

Step 2:

Find bucket.

Step 3:

Store object.

---

Retrieval:

```java
map.get(e2);
```

where:

```java
e2 = new Employee(101,"John");
```

Flow:

```
hashCode()
     |
     |
find bucket

     |
     |
equals()

     |
     |
return value
```

---

# 8. What Happens If We Override equals() But Not hashCode()? ⭐⭐⭐⭐⭐

Classic bug.

Example:

```java
class Employee {


int id;


@Override
public boolean equals(Object obj){

    Employee e=(Employee)obj;

    return id == e.id;

}

}
```

But no hashCode.

---

Usage:

```java
Map<Employee,String> map =
new HashMap<>();


Employee e1 =
new Employee(101);


map.put(e1,"John");


Employee e2 =
new Employee(101);


System.out.println(map.get(e2));
```

Expected:

```
John
```

Actual:

```
null
```

---

Why?

Because:

```
e1.equals(e2)

true
```

but:

```
e1.hashCode()

!=

e2.hashCode()
```

HashMap searches different bucket.

---

# 9. Correct equals + hashCode Implementation ⭐⭐⭐⭐⭐

Example:

```java
class Employee {


private int id;
private String name;


@Override
public boolean equals(Object obj){

    if(this == obj)
        return true;


    if(!(obj instanceof Employee))
        return false;


    Employee e=(Employee)obj;


    return id == e.id
            &&
           Objects.equals(name,e.name);

}


@Override
public int hashCode(){

    return Objects.hash(id,name);

}

}
```

---

Now:

```java
Employee e1 =
new Employee(101,"John");


Employee e2 =
new Employee(101,"John");


System.out.println(e1.equals(e2));

System.out.println(e1.hashCode()==e2.hashCode());
```

Output:

```
true
true
```

---

# 10. equals() Contract ⭐⭐⭐⭐⭐

Java defines five rules.

---

## 1. Reflexive

Object equals itself.

```
x.equals(x) == true
```

---

## 2. Symmetric

If:

```
x.equals(y)
```

then:

```
y.equals(x)
```

must also be true.

---

## 3. Transitive

If:

```
x.equals(y)

y.equals(z)
```

then:

```
x.equals(z)
```

---

## 4. Consistent

Multiple calls should return same result if data does not change.

---

## 5. Null check

```
x.equals(null)
```

must return:

```
false
```

---

# 11. hashCode Contract ⭐⭐⭐⭐⭐

Rules:

### Rule 1

Same object:

```
hashCode()
```

must consistently return same value.

---

### Rule 2

If equals true:

```
same hashCode
```

required.

---

### Rule 3

Different objects can have same hashCode.

Called:

```
collision
```

Example:

```
Object A

hashCode = 100


Object B

hashCode = 100
```

Allowed.

Then HashMap uses:

```
equals()
```

to differentiate.

---

# 12. String equals and hashCode Example ⭐⭐⭐⭐⭐

Example:

```java
String s1="Java";

String s2=new String("Java");
```

Comparison:

```java
s1==s2
```

false.

---

But:

```java
s1.equals(s2)
```

true.

Because String overrides:

```
equals()
hashCode()
```

based on characters.

---

# 13. HashSet Example ⭐⭐⭐⭐⭐

HashSet internally uses HashMap.

Example:

```java
Set<Employee> set =
new HashSet<>();


set.add(e1);

set.add(e2);
```

If:

```java
e1.equals(e2)
```

true

and:

```java
hashCode()
```

same

then:

```
Only one object stored
```

---

Without overriding:

```
Two objects stored
```

even if data is same.

---

# 14. equals() with Mutable Fields ⭐⭐⭐⭐

Dangerous example:

```java
class Employee {


int id;


@Override
public int hashCode(){

    return id;

}

}
```

Usage:

```java
Employee e =
new Employee(101);


map.put(e,"John");


e.id=200;
```

Now:

```
Old bucket:

hash 101


New hash:

200
```

HashMap cannot find object.

---

Rule:

Objects used as HashMap keys should ideally be immutable.

Examples:

Good keys:

* String
* Integer
* Immutable DTOs

---

# 15. Java Records and equals/hashCode ⭐⭐⭐

Java records automatically generate:

* equals()
* hashCode()
* toString()

Example:

```java
record Employee(int id,String name){}
```

Equivalent:

```java
equals()
hashCode()
toString()
```

generated automatically.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Difference between == and equals()?

Answer:

`==`

* Compares references
* Checks same object

`equals()`

* Compares logical equality
* Can be overridden

---

## Q2. Why override hashCode when overriding equals?

Because HashMap/HashSet use hashCode first.

Equal objects must go to same bucket.

---

## Q3. Can two unequal objects have same hashCode?

Yes.

Called collision.

---

## Q4. Can hashCode uniquely identify object?

No.

Different objects can have same hash.

---

## Q5. Why String is good HashMap key?

Because:

* Immutable
* Proper equals/hashCode
* Stable hash value

---

## Q6. What happens if HashMap key changes after insertion?

Object becomes unreachable because it is in wrong bucket.

---

# Interview Boundary

For 7+ years Java developer:

Must know deeply:

✅ == vs equals
✅ Object.equals default behaviour
✅ equals contract
✅ hashCode contract
✅ HashMap dependency
✅ HashSet behaviour
✅ Mutable key problem
✅ String implementation

Deep dive not required:

* JVM native hash generation
* Object header mark word hash storage

---

Next topic:

# Object Class Methods ⭐⭐⭐⭐⭐

We will cover:

* `toString()`
* `clone()`
* `finalize()` (deprecated)
* `getClass()`
* `wait()`
* `notify()`
* `notifyAll()`
* Why Object is root of Java hierarchy
* Interview questions

