# 3. Inheritance ⭐⭐⭐⭐⭐

## What is Inheritance?

### Interview Definition

> Inheritance is an OOP mechanism where one class acquires the properties and behaviors of another class, allowing code reuse and representing an **IS-A relationship**.

Simple meaning:

> A child class can reuse and extend the functionality of a parent class.

---

# Why do we need Inheritance?

Consider this example:

```text
Vehicle

start()
stop()
speed


Car

start()
stop()
speed


Bike

start()
stop()
speed
```

Problem:

Common behavior is duplicated.

```text
Car
 |
 +-- start()
 +-- stop()


Bike
 |
 +-- start()
 +-- stop()
```

Both have the same behavior.

Instead:

```text
             Vehicle

                |
        +-------+-------+
        |               |

       Car             Bike
```

Common functionality stays in the parent.

---

# Inheritance in Java

Java uses:

```java
extends
```

Example:

```java
class Vehicle {

    void start(){

        System.out.println("Vehicle started");

    }


    void stop(){

        System.out.println("Vehicle stopped");

    }
}
```

Child:

```java
class Car extends Vehicle {


}
```

Usage:

```java
Car car = new Car();

car.start();
car.stop();
```

Output:

```text
Vehicle started
Vehicle stopped
```

The child inherited parent behavior.

---

# Adding Child-Specific Behavior

Inheritance is not only for reuse.

Child can add new behavior.

Example:

```java
class Car extends Vehicle {


    void openSunroof(){

        System.out.println("Sunroof opened");

    }

}
```

Now:

```text
Vehicle

start()
stop()


Car

start()
stop()
openSunroof()
```

---

# IS-A Relationship ⭐⭐⭐⭐⭐

This is the most important inheritance rule.

Inheritance should represent:

```text
Child IS-A Parent
```

Examples:

Valid:

```text
Car IS-A Vehicle

Dog IS-A Animal

Manager IS-A Employee
```

Invalid:

```text
Engine IS-A Car ❌

Wheel IS-A Car ❌
```

Why?

Engine and Wheel are parts of a car, not types of car.

That is composition.

---

# Types of Inheritance in Java

## 1. Single Inheritance

One parent, one child.

```text
Vehicle

   |

 Car
```

Java:

```java
class Vehicle {

}


class Car extends Vehicle {

}
```

---

## 2. Multilevel Inheritance

Chain inheritance.

```text
Animal

   |

Mammal

   |

Dog
```

Java:

```java
class Animal {

}


class Mammal extends Animal {

}


class Dog extends Mammal {

}
```

---

## 3. Hierarchical Inheritance

One parent, multiple children.

```text
        Vehicle

       /      \

     Car      Bike
```

Java:

```java
class Car extends Vehicle {

}


class Bike extends Vehicle {

}
```

---

## Multiple Inheritance

Java does NOT support:

```text
        A

       / \

      B   C

       \ /

        D
```

Why?

Diamond problem.

Example:

```java
class A {

    void print(){

    }
}


class B extends A {

}


class C extends A {

}


// Which print() should D get?
class D extends B, C {

}
```

Ambiguity.

Java avoids this with classes.

---

# How Java Handles Multiple Inheritance?

Using interfaces.

Example:

```java
interface Camera {

    void takePhoto();

}


interface GPS {

    void navigate();

}


class Phone implements Camera, GPS {


    public void takePhoto(){

    }


    public void navigate(){

    }

}
```

A class can implement multiple interfaces.

---

# Method Overriding ⭐⭐⭐⭐⭐

Inheritance enables runtime polymorphism.

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

Usage:

```java
Animal animal = new Dog();

animal.sound();
```

Output:

```text
Bark
```

The child implementation executes.

---

# Important Overriding Rules

## 1. Same method signature

Parent:

```java
void display()
```

Child:

```java
void display()
```

---

## 2. Return type

Allowed:

Covariant return type.

Example:

Parent:

```java
Animal getAnimal()
```

Child:

```java
Dog getAnimal()
```

---

## 3. Access modifier

Cannot reduce visibility.

Valid:

```text
Parent:
protected

Child:
public
```

Invalid:

```text
Parent:
public

Child:
private ❌
```

---

## 4. Static methods are not overridden

They are hidden.

Example:

```java
class Parent {

    static void display(){

    }
}


class Child extends Parent {

    static void display(){

    }
}
```

Called:

```java
Parent p = new Child();

p.display();
```

Parent method executes.

---

# Inheritance vs Composition ⭐⭐⭐⭐⭐

Very important senior interview question.

## Inheritance

Relationship:

```text
IS-A
```

Example:

```text
Car IS-A Vehicle
```

---

## Composition

Relationship:

```text
HAS-A
```

Example:

```text
Car HAS-A Engine
```

---

Many modern designs prefer composition.

Why?

Because inheritance creates tight coupling.

Example:

```text
Animal

 |
Dog
```

If Animal changes:

```text
Dog may break
```

---

Composition:

```text
Car

 |
 +-- Engine
 +-- Brake
 +-- GPS
```

Parts can change independently.

---

# Real Backend Example

Suppose:

```text
Employee

Developer
Manager
Tester
```

Inheritance:

```java
class Employee {

    String name;

    double salary;

}


class Developer extends Employee {


    void writeCode(){

    }

}


class Manager extends Employee {


    void manageTeam(){

    }

}
```

Good because:

```text
Developer IS-A Employee

Manager IS-A Employee
```

---

But:

```text
Employee

 |
 Address
```

Wrong.

Employee does not become an Address.

Use:

```java
class Employee {

    Address address;

}
```

Composition.

---

# Common Mistakes

## Mistake 1: Using inheritance only for code reuse

Bad thinking:

> "Two classes have similar code, let's extend one."

Similarity is not enough.

Need:

```text
IS-A relationship
```

---

## Mistake 2: Deep inheritance hierarchy

Example:

```text
A

|

B

|

C

|

D

|

E
```

Problems:

* Hard to understand
* Tight coupling
* Changes impact many classes

---

## Mistake 3: Extending concrete classes unnecessarily

Prefer:

```text
Interface

    |
Implementation
```

for flexible designs.

---

# Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. What is inheritance?

Expected answer:

> Inheritance is an OOP feature where a child class acquires properties and behavior from a parent class. It promotes code reuse and represents an IS-A relationship.

---

## Q2. Why does Java not support multiple inheritance?

Expected answer:

> Java avoids multiple inheritance with classes because it can create ambiguity, especially the diamond problem, where the compiler cannot determine which parent implementation should be used.

---

## Q3. Difference between inheritance and composition?

Expected answer:

Inheritance:

```
IS-A relationship
```

Example:

```
Dog IS-A Animal
```

Composition:

```
HAS-A relationship
```

Example:

```
Car HAS-A Engine
```

Composition provides better flexibility and reduces coupling.

---

## Q4. Why prefer composition over inheritance?

Expected answer:

> Inheritance creates a strong coupling between parent and child classes. Composition allows changing behavior by replacing components without affecting the whole hierarchy.

---

## Q5. Can constructor be inherited?

No.

Reason:

Constructors initialize objects and belong to the class where they are defined.

However, child constructors can call parent constructors using:

```java
super();
```

---

## Q6. Are private methods inherited?

No.

Reason:

Private members belong only to the parent class and are not accessible to child classes.

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ IS-A relationship
✅ Types of inheritance
✅ Method overriding
✅ Overriding rules
✅ Multiple inheritance limitation
✅ Diamond problem
✅ Composition vs inheritance
✅ Real design usage

Deep dive not required:

* JVM method table internals
* Bytecode inheritance resolution
* Compiler inheritance implementation

---

Next:

# Polymorphism ⭐⭐⭐⭐⭐

We will cover:

* Compile-time vs runtime polymorphism
* Method overloading
* Method overriding
* Dynamic method dispatch
* How JVM decides which method to call
* Real backend examples
* Interview questions.
