# Object Class Methods ⭐⭐⭐⭐⭐

`Object` class is the **root class of Java hierarchy**.

Every Java class directly or indirectly inherits from:

```java
java.lang.Object
```

Example:

```java
class Employee {

}
```

Compiler internally treats it as:

```java
class Employee extends Object {

}
```

That means every object gets methods from `Object`.

---

# 1. Why Object Class Exists?

Java follows:

> Everything is an object.

The JVM needs a common parent so every object has basic behaviour.

Common operations:

* Compare objects
* Convert object to string
* Get runtime class information
* Synchronization support
* Cloning support

---

# Object Class Methods

`Object` contains 11 methods:

| Method      | Purpose                   |
| ----------- | ------------------------- |
| equals()    | Compare objects           |
| hashCode()  | Generate hash value       |
| toString()  | String representation     |
| clone()     | Create object copy        |
| getClass()  | Runtime class information |
| finalize()  | GC cleanup (deprecated)   |
| wait()      | Thread waiting            |
| notify()    | Wake one thread           |
| notifyAll() | Wake all waiting threads  |

We already covered:

* equals()
* hashCode()

Now remaining important ones.

---

# 1. `toString()` ⭐⭐⭐⭐⭐

## Purpose

Returns a string representation of an object.

Method in Object:

```java
public String toString()
```

---

## Default behaviour

Example:

```java
class Employee {

    int id = 101;
    String name = "John";

}


public class Main {

    public static void main(String[] args){

        Employee e = new Employee();

        System.out.println(e);

    }

}
```

Output:

```
Employee@5e2de80c
```

---

What is this?

Default implementation:

```java
getClass().getName()
+
"@"
+
Integer.toHexString(hashCode())
```

Meaning:

```
ClassName@hashCode
```

---

# Why Override toString()?

For debugging and logging.

Without:

```
Employee@5e2de80c
```

Not useful.

---

With override:

```java
class Employee {


int id;
String name;


@Override
public String toString(){

    return "Employee{id="
            + id
            + ", name='"
            + name
            + "'}";

}

}
```

Output:

```
Employee{id=101, name='John'}
```

---

Production usage:

Logs:

```java
logger.info("Processing employee {}", employee);
```

Internally calls:

```java
employee.toString()
```

---

# Interview Question

## Q: Difference between equals() and toString()?

Answer:

`equals()`:

* Compares objects
* Returns boolean

`toString()`:

* Represents object as String
* Used for logging/debugging

---

# 2. `getClass()` ⭐⭐⭐⭐

## Purpose

Returns runtime class information.

Example:

```java
Employee emp = new Employee();

System.out.println(emp.getClass());
```

Output:

```
class Employee
```

---

Equivalent:

```java
emp.getClass().getName()
```

Output:

```
Employee
```

---

# Why is getClass() final?

Object method:

```java
public final native Class<?> getClass()
```

It cannot be overridden.

Why?

Because JVM must always return the actual runtime type.

---

Example:

```java
Animal a = new Dog();
```

Compile-time type:

```
Animal
```

Runtime type:

```
Dog
```

So:

```java
a.getClass()
```

returns:

```
Dog
```

---

# 3. `clone()` ⭐⭐⭐⭐⭐

## Purpose

Creates a copy of an object.

Object method:

```java
protected native Object clone()
```

---

But cloning has rules.

A class must implement:

```java
Cloneable
```

marker interface.

---

Example:

```java
class Employee implements Cloneable {


int id;


Employee(int id){

    this.id=id;

}


@Override
public Employee clone()
        throws CloneNotSupportedException {

    return (Employee)super.clone();

}

}
```

Usage:

```java
Employee e1 =
new Employee(101);


Employee e2 =
e1.clone();
```

Now:

```
e1 != e2

but

e1.id == e2.id
```

---

# Important: clone() creates shallow copy ⭐⭐⭐⭐⭐

Example:

```java
class Address {

    String city;

}


class Employee implements Cloneable {


Address address;


public Employee clone()
throws CloneNotSupportedException {

    return (Employee)super.clone();

}

}
```

Original:

```
Employee 1

   |
   |
 Address object
 city="London"
```

Clone:

```
Employee 2

   |
   |
 Same Address object
 city="London"
```

Both share same Address.

---

# Shallow Copy

Copies:

```
primitive values
references
```

but not referenced objects.

---

# Deep Copy

Creates completely independent objects.

Example:

```
Employee 1

    |
 Address 1


Employee 2

    |
 Address 2
```

---

# Why clone() is discouraged?

Modern Java prefers:

* Copy constructors
* Factory methods

Example:

```java
class Employee {


Employee(Employee other){

    this.id = other.id;

}

}
```

Better because:

* Explicit
* Type safe
* Handles deep copy easily

---

# 4. `finalize()` ⭐⭐⭐⭐

## Old Purpose

Called before object garbage collection.

Example:

```java
@Override
protected void finalize(){

    System.out.println("Cleanup");

}
```

---

Idea:

```
Object unreachable

        |
        v

GC identifies object

        |
        v

finalize()

        |
        v

Memory cleanup
```

---

# Why finalize() is Deprecated? ⭐⭐⭐⭐⭐

Deprecated since Java 9.

Problems:

## 1. No guarantee

You don't know:

* When GC runs
* Whether finalize executes

---

## 2. Performance impact

GC becomes slower.

---

## 3. Object resurrection

Object can make itself reachable again.

Example:

```java
protected void finalize(){

    staticReference=this;

}
```

Object comes back.

---

## Replacement:

Use:

### try-with-resources

```java
try(Resource r = new Resource()){

}
```

or:

```java
AutoCloseable
```

---

# 5. wait() ⭐⭐⭐⭐⭐

Important for threads.

Object method:

```java
public final void wait()
```

Purpose:

> Current thread releases the lock and waits until another thread signals.

---

Example:

```java
synchronized(lock){

    lock.wait();

}
```

Flow:

```
Thread A

gets lock

 |
 |
wait()

 |
 |
releases lock


Thread B

gets lock

 |
 |
notify()

 |
 |
Thread A resumes
```

---

# Important:

`wait()` must be called inside synchronized block.

Invalid:

```java
lock.wait();
```

without:

```java
synchronized(lock)
```

throws:

```
IllegalMonitorStateException
```

---

# 6. notify() ⭐⭐⭐⭐⭐

Wakes one waiting thread.

Example:

```java
synchronized(lock){

    lock.notify();

}
```

---

# 7. notifyAll() ⭐⭐⭐⭐⭐

Wakes all waiting threads.

Example:

```java
synchronized(lock){

    lock.notifyAll();

}
```

---

# wait() vs sleep() ⭐⭐⭐⭐⭐

Very common interview question.

| wait()                      | sleep()          |
| --------------------------- | ---------------- |
| Object method               | Thread method    |
| Releases lock               | Keeps lock       |
| Used for communication      | Used for delay   |
| Must be inside synchronized | No requirement   |
| Wakes by notify             | Wakes after time |

Example:

sleep:

```java
Thread.sleep(1000);
```

wait:

```java
lock.wait();
```

---

# Object Lifecycle Connection

Object creation:

```
new Employee()
        |
        |
        v

Constructor

        |
        |
        v

Object alive

        |
        |
        v

No references

        |
        |
        v

Eligible for GC

        |
        |
        v

(finalize old mechanism)

        |
        |
        v

Memory reclaimed
```

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Why every class extends Object?

Because Java needs common behaviour for all objects.

---

## Q2. Why override toString()?

For meaningful logs and debugging.

---

## Q3. Difference between clone and copy constructor?

clone():

* JVM based
* Usually shallow copy
* Requires Cloneable

Copy constructor:

* Explicit
* Type safe
* Better control

---

## Q4. Why finalize deprecated?

Because:

* unpredictable
* slow
* unsafe

---

## Q5. Difference between wait and sleep?

wait:

* releases lock
* thread communication

sleep:

* keeps lock
* delay only

---

## Q6. Can we override getClass()?

No.

It is final.

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ Object class purpose
✅ toString()
✅ getClass()
✅ clone() shallow vs deep copy
✅ finalize deprecated reason
✅ wait/notify basics
✅ wait vs sleep

Deep dive not required:

* JVM native implementation of Object methods
* Object header mark word details

---

Next topic:

# Immutable Classes ⭐⭐⭐⭐⭐

We will cover:

* What is immutability
* Why String is immutable
* Rules to create immutable class
* Defensive copying
* Immutable objects and thread safety
* Real production examples (DTO, configuration, value objects)
