# 10. Object Lifecycle ⭐⭐⭐

Object lifecycle is a good topic to close OOP fundamentals.

For a **7+ years Java developer**, interviewer usually does not expect academic lifecycle theory. They want to know:

* How objects are created
* How memory is allocated
* How initialization happens
* What happens before object destruction
* Relationship with JVM, Heap, GC
* Common mistakes in production

---

# What is Object Lifecycle?

### Interview Definition

> Object lifecycle describes the different stages an object goes through from creation, usage, and finally removal from memory by the Garbage Collector.

In Java:

```text id="x3k7p8"
Object Lifecycle

      |
      |
      v

1. Creation

      |
      |
      v

2. Initialization

      |
      |
      v

3. Usage

      |
      |
      v

4. Becoming Eligible for GC

      |
      |
      v

5. Garbage Collection
```

---

# 1. Object Creation ⭐⭐⭐⭐⭐

An object is created using:

```java
Employee emp = new Employee();
```

This has two parts:

```java
Employee emp;
```

Reference creation.

and:

```java
new Employee();
```

Object creation.

---

Memory:

```text
Stack                         Heap

emp  --------------------->   Employee Object
                              |
                              |
                              name
                              salary
```

Important:

* Reference variable is stored in Stack.
* Actual object lives in Heap.

---

# What happens internally when we use new?

Example:

```java
Employee emp = new Employee("John");
```

Steps:

## Step 1: Class Loading

Before creating object:

JVM checks:

```text
Employee.class loaded?
```

If not:

```text
ClassLoader
       |
       v
Loads Employee bytecode
```

---

## Step 2: Memory Allocation

JVM allocates memory in Heap.

Example:

```text
Heap

+----------------+
| Employee Object|
+----------------+
```

---

## Step 3: Default Initialization

Before constructor runs:

Instance variables get default values.

Example:

```java
class Employee {

    String name;

    int age;

    boolean active;

}
```

Before constructor:

```text
name = null

age = 0

active = false
```

---

## Step 4: Constructor Execution

Then constructor executes.

```java
class Employee {

    String name;


    Employee(String name){

        this.name = name;

    }
}
```

Now:

```text
name = "John"
```

---

# 2. Object Initialization ⭐⭐⭐⭐

Initialization happens in order.

Very important interview question.

Example:

```java
class Employee {


    static {

        System.out.println("Static block");

    }


    {

        System.out.println("Instance block");

    }


    Employee(){

        System.out.println("Constructor");

    }

}
```

Create object:

```java
new Employee();
```

Output:

```text
Static block

Instance block

Constructor
```

---

Order:

```text
Class Loading

        |
        v

Static variables initialization

        |
        v

Static block

        |
        v

Object creation

        |
        v

Instance variables initialization

        |
        v

Instance blocks

        |
        v

Constructor
```

---

# 3. Object Usage

After creation:

```java
Employee emp = new Employee();

emp.calculateSalary();
emp.updateProfile();
```

Object is alive while:

* References exist
* Application is using it

---

# 4. Object Becomes Eligible for Garbage Collection ⭐⭐⭐⭐⭐

Important:

Java does NOT destroy objects manually.

Example:

```java
Employee emp = new Employee();

emp = null;
```

Now:

```text
Stack

emp
 |
 |
 X


Heap

Employee Object

(no reference)
```

Object becomes:

```text
Eligible for GC
```

But GC may run later.

---

# Different Ways Object Becomes Eligible

## 1. Reference assigned null

```java
Employee e = new Employee();

e = null;
```

---

## 2. Reference reassignment

```java
Employee e1 = new Employee();

e1 = new Employee();
```

Old object has no reference.

---

## 3. Local variable scope ends

Example:

```java
void process(){

    Employee e = new Employee();

}
```

After method finishes:

```text
e reference disappears
```

Object may become eligible.

---

## 4. Island of Isolation ⭐⭐⭐

Interesting interview question.

Example:

```java
class Employee {

    Department department;

}


class Department {

    Employee employee;

}
```

Objects:

```text
Employee
   |
   v
Department

Department
   |
   v
Employee
```

Both reference each other.

But nobody outside references them.

They are unreachable.

Modern GC can collect them.

---

# 5. Garbage Collection ⭐⭐⭐⭐⭐

When object is unreachable:

```text
Reachable

      |
      v

Unreachable

      |
      v

GC removes it
```

Example:

```java
Employee e = new Employee();

e = null;
```

GC eventually reclaims memory.

---

# finalize() (Important but Low Priority)

Older Java:

```java
protected void finalize(){

}
```

Idea:

Before GC removes object, JVM may call finalize.

But:

* Deprecated
* Not guaranteed
* Bad for resource cleanup

Do not use.

---

Modern approach:

Use:

```java
try-with-resources
```

Example:

```java
try(Connection con = datasource.getConnection()){

}
```

---

# Object Lifecycle and JVM Memory

Connection:

```text
Object Lifecycle

Creation
    |
    |
    v

Heap Allocation

    |
    |
    v

Young Generation

    |
    |
    v

Old Generation

    |
    |
    v

GC Cleanup
```

Objects usually start in:

```text
Eden Space
```

then survive:

```text
Survivor Space
```

then:

```text
Old Generation
```

(covered in JVM GC section)

---

# Object Creation Optimization ⭐⭐⭐

Senior-level awareness.

## Escape Analysis

JVM checks:

> Does this object escape the method?

Example:

```java
void calculate(){

    Employee e = new Employee();

}
```

Object does not escape.

JVM may optimize:

* Allocate on stack
* Remove allocation

(Deep dive covered under JVM optimization)

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Explain object lifecycle in Java.

Expected answer:

> Object lifecycle starts with creation using the new keyword. JVM allocates memory in heap, initializes fields, executes constructors, object is used by the application, and when no reachable references exist, it becomes eligible for garbage collection.

---

## Q2. Where are objects stored in Java?

Answer:

Objects are stored in Heap memory.

References are usually stored in Stack frames.

---

## Q3. Does setting object reference to null immediately destroy object?

No.

Example:

```java
obj = null;
```

Only makes object eligible for GC.

Garbage collector decides when to remove it.

---

## Q4. Can Java have memory leaks even with Garbage Collection?

Yes.

Very important senior question.

Example:

```java
static List<Employee> employees =
        new ArrayList<>();
```

Adding objects:

```java
employees.add(new Employee());
```

Objects are still referenced.

GC cannot remove them.

Result:

Memory leak.

---

## Q5. Constructor vs Object Creation?

Difference:

Object creation:

```java
new Employee()
```

allocates memory.

Constructor:

```java
Employee()
```

initializes object state.

---

## Q6. What happens first: constructor or memory allocation?

Memory allocation happens first.

Order:

```text
Allocate memory

↓

Default values

↓

Constructor execution
```

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ Object creation steps
✅ Stack vs Heap reference
✅ Constructor execution
✅ Initialization order
✅ GC eligibility
✅ Memory leak despite GC
✅ finalize limitations
✅ Connection with JVM Heap

Deep dive not required here:

* Object header layout
* Mark word
* Compressed OOPs
* TLAB allocation

(Already belongs to JVM internals section.)

---

# OOP Module Completed

We have now covered:

```text
OOP

✅ Principles
✅ Encapsulation
✅ Abstraction
✅ Inheritance
✅ Polymorphism
✅ Association
✅ Aggregation
✅ Composition
✅ Coupling
✅ Cohesion
✅ SOLID
✅ Object Lifecycle
```

Next logical Java interview module:

# Java Language Fundamentals ⭐⭐⭐⭐⭐

Suggested order:

```text
1. Java Keywords
2. Access Modifiers
3. final keyword
4. static keyword
5. equals() and hashCode()
6. Object class methods
7. Immutable Classes
8. String internals
9. Wrapper Classes
10. Enum
```

This connects directly with Collections and JVM.
