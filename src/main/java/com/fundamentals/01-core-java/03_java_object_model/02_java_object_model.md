# Java Object Model ⭐⭐⭐⭐⭐

This is the foundation of how Java treats objects internally.

For a **7+ years Java developer**, interviewers usually test:

* What is an object in Java?
* Why does every class inherit from Object?
* Difference between identity and equality
* equals() and hashCode() contract
* Why HashMap depends on them
* Shallow vs deep copy
* Immutable objects

Let's start from the base.

---

# 1. What is the Java Object Model?

### Interview Definition

> Java Object Model defines how objects are represented, created, compared, and managed in Java. Every object in Java inherits common behavior from the Object class.

In Java:

```java
class Employee {

}
```

is internally:

```java
class Employee extends Object {

}
```

Even if we don't write `extends Object`, JVM automatically adds it.

---

# Object Hierarchy

Every Java class ultimately derives from:

```text
java.lang.Object
          |
          |
          +------------- Employee
          |
          +------------- String
          |
          +------------- ArrayList
          |
          +------------- Thread
```

Because of this inheritance, every object gets common methods.

---

# Object Class

Package:

```java
java.lang.Object
```

No import required.

Important methods:

```java
public class Object {

    public boolean equals(Object obj)

    public int hashCode()

    public String toString()

    protected Object clone()

    protected void finalize()

    public final Class<?> getClass()

    public final void wait()

    public final void notify()

    public final void notifyAll()

}
```

For this module:

Focus:

```text
equals()
hashCode()
toString()
clone()
finalize()
```

Thread methods will move to concurrency.

---

# 2. Object Identity vs Equality ⭐⭐⭐⭐⭐

This is one of the most important concepts.

Many developers confuse:

```java
==
```

and:

```java
equals()
```

---

# Identity

Identity means:

> Are these two references pointing to the exact same object in memory?

Operator:

```java
==
```

Example:

```java
Employee e1 = new Employee("John");

Employee e2 = e1;


System.out.println(e1 == e2);
```

Memory:

```text
Stack

e1 --------+
           |
           |
e2 --------+
           |
           v

        Employee Object
```

Output:

```
true
```

Because both references point to the same object.

---

# Equality

Equality means:

> Do these two objects represent the same logical value?

Method:

```java
equals()
```

Example:

```java
Employee e1 =
        new Employee("John");


Employee e2 =
        new Employee("John");


System.out.println(e1.equals(e2));
```

Question:

Are they the same object?

No.

Memory:

```text
e1 ---> Employee("John")


e2 ---> Employee("John")
```

Two different objects.

But logically:

```
same name
```

So equals can return true.

---

# Default equals() Behavior

Important interview point.

Object class implementation:

```java
public boolean equals(Object obj){

    return this == obj;

}
```

Meaning:

By default:

```text
equals()
=
reference comparison
```

Example:

```java
class Employee {

    String name;

}
```

Without overriding equals:

```java
Employee e1 =
        new Employee("John");

Employee e2 =
        new Employee("John");


System.out.println(e1.equals(e2));
```

Output:

```
false
```

Why?

Because Object.equals() checks identity.

---

# Why Override equals()?

Because business objects need logical comparison.

Example:

Bank system:

```java
Account a1 =
new Account("ACC123");


Account a2 =
new Account("ACC123");
```

Should they be equal?

Business says:

Yes.

Because account number is same.

Therefore:

Override equals.

---

# 3. equals() Contract ⭐⭐⭐⭐⭐

If you override equals(), Java expects you to follow the contract.

## Rule 1: Reflexive

Object equals itself.

```java
x.equals(x)
```

must always be:

```
true
```

---

## Rule 2: Symmetric

If:

```java
x.equals(y)
```

is true,

then:

```java
y.equals(x)
```

must also be true.

Example:

```text
Employee equals Manager

Manager not equal Employee
```

Problem.

---

## Rule 3: Transitive

If:

```text
x == y

and

y == z
```

then:

```text
x == z
```

must be true.

---

## Rule 4: Consistent

Multiple calls should give same result.

Unless object state changes.

---

## Rule 5: Null

For:

```java
x.equals(null)
```

must return:

```
false
```

---

# Example Correct equals()

```java
class Employee {

    private int id;
    private String name;


    @Override
    public boolean equals(Object obj){

        if(this == obj)
            return true;


        if(obj == null ||
           getClass() != obj.getClass())
            return false;


        Employee other = (Employee)obj;


        return id == other.id &&
               Objects.equals(name, other.name);
    }

}
```

---

# Why this order?

Interviewers like this.

```java
if(this == obj)
```

Optimization.

No need to compare fields.

---

```java
if(obj == null)
```

Avoid NullPointerException.

---

```java
getClass()
```

Avoid comparing unrelated classes.

---

# 4. hashCode() ⭐⭐⭐⭐⭐

Now the most important connection.

## What is hashCode?

> hashCode returns an integer representation of an object used by hash-based collections for locating objects efficiently.

Used by:

* HashMap
* HashSet
* Hashtable
* ConcurrentHashMap

---

Example:

```java
Map<Employee,String> map =
        new HashMap<>();
```

Internally:

```text
Employee Object

        |
        |
     hashCode()

        |
        |
     Bucket Number

        |
        |
     Store Object
```

---

# hashCode Contract

Very important.

## Rule 1

If two objects are equal:

```java
a.equals(b)
```

then:

```java
a.hashCode()
==
b.hashCode()
```

must be true.

---

## Rule 2

Same hashCode does NOT mean objects are equal.

Example:

```text
Object A

hashCode = 100


Object B

hashCode = 100
```

Possible.

This is called:

```
Collision
```

---

# Why override hashCode with equals?

Classic interview question.

Suppose:

```java
class Employee {

    int id;

    equals()
}
```

Only override equals.

Then:

```java
Set<Employee> set =
new HashSet<>();
```

Add:

```java
Employee e1 =
new Employee(1);

Employee e2 =
new Employee(1);


set.add(e1);
set.add(e2);
```

Expected:

```
one employee
```

But:

HashSet checks:

Step 1:

```text
hashCode()
```

Different buckets.

Step 2:

equals() is never called.

Result:

Duplicate objects.

---

Therefore:

Always:

```java
override equals()

+

override hashCode()
```

---

# 5. Quick Interview Comparison

|                  | ==                             | equals()          |
| ---------------- | ------------------------------ | ----------------- |
| Type             | Operator                       | Method            |
| Checks           | Identity                       | Logical equality  |
| Default behavior | Same reference                 | Same reference    |
| Can override     | No                             | Yes               |
| Used for         | Primitive/reference comparison | Object comparison |

---

# Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why does every class inherit Object?

Answer:

> Object provides common behavior required by all objects like equality comparison, hashing, string representation, synchronization methods, and runtime type information.

---

## Q2. Difference between == and equals()?

Answer:

`==`

Checks whether two references point to the same object.

`equals()`

Checks logical equality and can be overridden.

---

## Q3. Why override hashCode when overriding equals?

Answer:

> Hash-based collections use hashCode first to locate buckets. Equal objects must produce the same hashCode to maintain correct behavior in HashMap and HashSet.

---

## Q4. Can two unequal objects have same hashCode?

Yes.

That is a collision.

Example:

```text
Object A
hashCode = 50


Object B
hashCode = 50
```

HashMap handles it internally.

---

# Interview Boundary

For 7+ years Java developer:

Must know deeply:

✅ Object class
✅ Identity vs equality
✅ equals contract
✅ hashCode contract
✅ HashMap connection
✅ == vs equals

Next topic:

# Object Class Methods Deep Dive

We will cover:

1. toString()
2. clone()
3. finalize()
4. getClass()

Then:

# Shallow Copy vs Deep Copy

Then:

# Immutable Classes

This order will connect naturally.


# Object Class Methods Deep Dive ⭐⭐⭐⭐⭐

We already covered:

* `equals()`
* `hashCode()`

Now let's cover the remaining important `Object` methods:

```text
java.lang.Object

|
+-- equals()
+-- hashCode()
+-- toString()
+-- clone()
+-- finalize()
+-- getClass()
+-- wait()
+-- notify()
+-- notifyAll()
```

Thread-related methods:

```
wait()
notify()
notifyAll()
```

we will cover in **Multithreading**, because they make sense with locks and monitors.

---

# 1. toString() ⭐⭐⭐⭐

## What is toString()?

### Interview Definition

> `toString()` provides a string representation of an object. It is mainly used for debugging, logging, and displaying object information.

Method in Object:

```java
public String toString()
```

---

# Default Object.toString()

Example:

```java
class Employee {

    private int id;
    private String name;

}


public class Main {

    public static void main(String[] args) {

        Employee emp = new Employee();

        System.out.println(emp);

    }
}
```

Output:

```
Employee@5e2de80c
```

Why?

Because default implementation:

```java
public String toString() {

    return getClass().getName()
           + "@"
           + Integer.toHexString(hashCode());

}
```

Format:

```
ClassName + @ + HashCode
```

---

# Why Override toString()?

Without override:

```text
Employee@5e2de80c
```

Not useful.

With override:

```java
class Employee {

    private int id;
    private String name;


    @Override
    public String toString(){

        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';

    }

}
```

Output:

```
Employee{id=101, name='John'}
```

Useful for:

* Logs
* Debugging
* Exception messages
* Monitoring

---

# Spring Boot Example

Very common.

Suppose:

```java
@RestController
class EmployeeController {


    @GetMapping("/employee")
    Employee getEmployee(){

        return employee;

    }

}
```

When logging:

```java
log.info("Employee {}", employee);
```

Internally:

```java
employee.toString()
```

is called.

---

# Common Mistake

Do not include sensitive information.

Bad:

```java
@Override
public String toString(){

return password;

}
```

Logs may expose:

* Password
* Tokens
* Personal data
* Secrets

---

# 2. clone() ⭐⭐⭐

## What is clone()?

### Interview Definition

> `clone()` creates a copy of an object. It performs a field-by-field copy and provides shallow copying by default.

Method:

```java
protected Object clone()
```

---

# Why is clone() protected?

Because Java designers wanted classes to explicitly allow cloning.

To use clone:

Class must implement:

```java
Cloneable
```

Example:

```java
class Employee implements Cloneable {


    int id;

    String name;


    @Override
    protected Object clone()
            throws CloneNotSupportedException {

        return super.clone();

    }

}
```

Usage:

```java
Employee e1 = new Employee();

Employee e2 = (Employee)e1.clone();
```

Now:

```
e1 ---> Employee Object

e2 ---> Employee Object
```

Two separate objects.

---

# Important: Cloneable is a Marker Interface

It has no methods.

```java
public interface Cloneable {

}
```

It only tells JVM:

> This object is allowed to be cloned.

---

# Shallow Copy ⭐⭐⭐⭐⭐

Default clone behavior.

Example:

```java
class Address {

    String city;

}


class Employee implements Cloneable {

    int id;

    Address address;


    public Object clone()
    throws CloneNotSupportedException {

        return super.clone();

    }

}
```

Create:

```java
Employee e1 = new Employee();

Employee e2 =
(Employee)e1.clone();
```

Memory:

```
e1
 |
 |
 v
Employee
 |
 |
 v
Address Object


e2
 |
 |
 v
Employee
 |
 |
 +------ same Address Object
```

Primitive values copied.

References copied.

---

Example:

```java
e2.id = 200;
```

No impact on e1.

Because:

```
id
```

is primitive.

---

But:

```java
e2.address.city = "London";
```

affects e1 also.

Because both point to same Address object.

---

# Deep Copy ⭐⭐⭐⭐⭐

Creates completely independent objects.

Memory:

```
e1
 |
 v
Employee

 |
 v

Address Object A



e2
 |
 v
Employee

 |
 v

Address Object B
```

Now:

```java
e2.address.city="London";
```

does not affect e1.

---

# Shallow vs Deep Copy

|                  | Shallow Copy | Deep Copy  |
| ---------------- | ------------ | ---------- |
| Object           | New object   | New object |
| Primitive fields | Copied       | Copied     |
| Reference fields | Shared       | New copy   |
| Performance      | Faster       | Slower     |
| Independence     | Partial      | Complete   |

---

# Is clone() recommended?

For modern Java:

Usually no.

Reasons:

## 1. Cloneable design is broken

Joshua Bloch (Effective Java):

> Cloneable is a flawed mechanism.

Problems:

* Doesn't use constructor
* Doesn't work well with final fields
* Difficult inheritance behavior
* Easy to create shallow copies accidentally

---

## Better alternatives

## Copy Constructor

Preferred.

Example:

```java
class Employee {


    int id;

    Address address;


    Employee(Employee other){

        this.id = other.id;

        this.address =
             new Address(other.address);

    }

}
```

---

## Factory Method

Example:

```java
Employee copy =
Employee.copyOf(existingEmployee);
```

---

# 3. finalize() ⭐⭐ (Legacy)

## What is finalize()?

### Interview Definition

> finalize() was a method called by Garbage Collector before reclaiming an object, intended for cleanup operations.

Example:

```java
protected void finalize(){

}
```

---

# Why was finalize() deprecated?

Because it is unreliable.

Problems:

## 1. No guarantee it runs

Example:

```java
object = null;
```

Does not mean:

```text
finalize()
```

will execute.

---

## 2. Performance overhead

Objects with finalize require special GC handling.

---

## 3. Can delay garbage collection

Objects wait in finalization queue.

---

## 4. Security issues

Objects can resurrect themselves.

Example:

```java
protected void finalize(){

    staticReference = this;

}
```

Object becomes reachable again.

---

# Modern Alternative

Use:

## try-with-resources

Example:

```java
try(FileInputStream file =
    new FileInputStream("data.txt")){

}
```

Resource automatically closes.

---

Or:

```java
AutoCloseable
```

interface.

---

# 4. getClass() ⭐⭐⭐⭐

Method:

```java
public final Class<?> getClass()
```

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

Used in:

* Reflection
* Frameworks
* Serialization
* Dependency injection

---

# equals() vs getClass()

Important connection.

Inside equals:

Option 1:

```java
if(obj instanceof Employee)
```

Allows subclass comparison.

Option 2:

```java
if(getClass()!=obj.getClass())
```

Requires exact same class.

Example:

```java
class Employee {}

class Manager extends Employee {}
```

Question:

Should:

```
Employee == Manager
```

be true?

Depends on business requirement.

---

# Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why override toString()?

Answer:

> To provide meaningful object representation for debugging and logging instead of default ClassName@hashCode output.

---

## Q2. Why is clone() considered bad practice?

Answer:

> Cloneable creates shallow copies by default, does not work well with inheritance and final fields, and breaks encapsulation. Copy constructors or factory methods are preferred.

---

## Q3. Difference between shallow and deep copy?

Answer:

Shallow copy:

> Copies object but shares referenced objects.

Deep copy:

> Copies object and creates independent copies of referenced objects.

---

## Q4. Why was finalize deprecated?

Answer:

> finalize() is unpredictable, impacts GC performance, and does not guarantee execution. Resource cleanup should use try-with-resources.

---

# Interview Boundary

Must know:

✅ toString purpose
✅ clone basics
✅ Cloneable marker interface
✅ Shallow vs deep copy
✅ finalize deprecated reason
✅ getClass usage

Deep dive not required:

* JVM finalizer queue internals
* Object resurrection examples
* Native cloning details

---

Next topic:

# Immutable Classes ⭐⭐⭐⭐⭐

This is highly important because it connects:

```
String
+
Thread Safety
+
HashMap keys
+
final keyword
+
Concurrency
```

and is frequently asked for senior Java roles.


# Immutable Classes ⭐⭐⭐⭐⭐

This is one of the **highest ROI Java interview topics** for a senior developer.

Interviewers ask this because immutability connects with:

* String design
* Thread safety
* HashMap keys
* Caching
* Functional programming
* Defensive copying
* Java memory model

---

# What is Immutability?

## Interview Definition

> An immutable object is an object whose state cannot be changed after it is created.

Meaning:

Once an object is created:

```text id="0q1w8m"
Object State

Created

  |
  |
  v

Cannot change
```

Any modification creates a **new object**.

---

# Example: String is Immutable

```java id="m0g5os"
String name = "John";

name = name + " Smith";
```

Many developers think:

```text id="5g3n8q"
"John" changed to "John Smith"
```

But internally:

```text id="8b1c4w"
String Pool


"John"

   |
   |
   v

"John Smith"
```

Original object remains unchanged.

---

# Mutable vs Immutable

## Mutable Object

State can change.

Example:

```java id="1x9h1c"
StringBuilder sb = new StringBuilder("Hello");

sb.append(" World");
```

Same object:

```text id="pxq8zy"
Before:

StringBuilder
value="Hello"


After:

StringBuilder
value="Hello World"
```

---

## Immutable Object

State cannot change.

Example:

```java id="s7s8r4"
String s = "Hello";

s.concat(" World");
```

Nothing happens.

Because:

```java id="g1r5js"
concat()
```

creates a new String.

Correct:

```java id="9qz8j2"
s = s.concat(" World");
```

---

# Why Immutable Objects? ⭐⭐⭐⭐⭐

## 1. Thread Safety

Important senior interview point.

Multiple threads can safely share immutable objects.

Example:

```java id="6q9h0r"
String token = "abc123";
```

Thread 1:

```text id="8k6x0v"
Read token
```

Thread 2:

```text id="n6a9bf"
Read token
```

No synchronization needed.

Because nobody can modify it.

---

Mutable object:

```text id="5v3h8x"
Thread 1

modify object


Thread 2

reading same object
```

Requires:

* synchronized
* locks
* volatile

---

# 2. HashMap Key Safety ⭐⭐⭐⭐⭐

Very important.

Example:

```java id="1x2f4m"
Map<Employee,String> map =
new HashMap<>();

Employee e =
new Employee(101);

map.put(e,"Developer");
```

HashMap stores based on:

```text id="6ysn5p"
hashCode()
```

Suppose:

Before:

```text id="z5q0jm"
Employee id=101

hashCode=101
```

Later:

```java id="p4a5w2"
e.setId(200);
```

Now:

```text id="5ry6h4"
hashCode=200
```

HashMap searches wrong bucket.

Object exists but cannot be found.

---

Therefore:

Immutable objects make excellent keys.

Examples:

```text id="j8z6v9"
String
Integer
LocalDate
UUID
```

---

# 3. Security

Immutable objects prevent unwanted modification.

Example:

Database connection URL:

```java id="v3w8q2"
String url =
"jdbc:mysql://localhost/db";
```

If mutable:

Someone could modify it after validation.

Immutable:

Original value remains safe.

---

# 4. Caching

Immutable objects can be safely cached.

Example:

```java id="7n0q8a"
Integer.valueOf(100)
```

Java caches common immutable objects.

---

# How to Create Immutable Class? ⭐⭐⭐⭐⭐

Interview favourite.

Rules:

## Rule 1: Make class final

Why?

Prevent subclass from changing behavior.

Bad:

```java id="t7z1m8"
class Employee {

}
```

Someone can extend:

```java id="1v4c9s"
class SpecialEmployee extends Employee {

    modifyState()

}
```

Better:

```java id="q8h4fj"
final class Employee {

}
```

---

## Rule 2: Make fields private and final

Example:

```java id="4zq9pb"
private final String name;

private final int age;
```

Why?

* private → no direct access
* final → assigned once

---

## Rule 3: Initialize fields through constructor

Example:

```java id="3h8n9m"
public Employee(String name,int age){

    this.name = name;

    this.age = age;

}
```

---

## Rule 4: No setters

Bad:

```java id="8v7r0s"
public void setName(String name){

    this.name=name;

}
```

Setter breaks immutability.

---

## Rule 5: Defensive copying for mutable fields

Most important advanced point.

Example:

```java id="2v4n0m"
class Employee {

    private final Date joiningDate;


    Employee(Date date){

        this.joiningDate=date;

    }

}
```

Problem:

Date is mutable.

External code:

```java id="9k2d4p"
date.setYear(2020);
```

Changes Employee state.

---

Correct:

```java id="8w5z6p"
class Employee {


private final Date joiningDate;


Employee(Date date){

    this.joiningDate =
        new Date(date.getTime());

}


public Date getJoiningDate(){

    return new Date(joiningDate.getTime());

}

}
```

Now nobody can modify internal state.

---

# Complete Immutable Class Example

```java id="q5v8n3"
public final class Employee {


    private final int id;

    private final String name;

    private final Address address;


    public Employee(int id,
                    String name,
                    Address address){

        this.id = id;

        this.name = name;

        this.address =
             new Address(address);

    }


    public int getId(){

        return id;

    }


    public String getName(){

        return name;

    }


    public Address getAddress(){

        return new Address(address);

    }

}
```

---

# Immutable Object with Mutable Reference

Important interview trap.

Question:

Is this immutable?

```java id="9x5q0k"
final class Employee {


private final Address address;


public Address getAddress(){

    return address;

}

}
```

Answer:

❌ No.

Why?

Because:

```java id="2q9s7v"
employee.getAddress()
        .setCity("London");
```

changes internal state.

Need defensive copy.

---

# final vs Immutable ⭐⭐⭐⭐⭐

Common confusion.

## final reference

Means:

Reference cannot change.

Example:

```java id="x7m3h2"
final Employee e =
        new Employee();
```

This is allowed:

```java id="p3v8z0"
e.setName("John");
```

if Employee is mutable.

---

## Immutable object

Means:

Object state cannot change.

Different concepts.

---

# Java Immutable Classes Examples

Built-in:

```text id="x9v1b7"
String

Integer

Long

BigDecimal

LocalDate

LocalDateTime

UUID
```

---

# Interview Questions ⭐⭐⭐⭐⭐

## Q1. How do you create an immutable class?

Answer:

1. Make class final
2. Make fields private final
3. Initialize through constructor
4. No setters
5. Defensive copy mutable objects
6. Return defensive copies from getters

---

## Q2. Why is String immutable?

Expected:

Because String is used heavily in:

* String pool
* Security
* HashMap keys
* Thread sharing

Immutability provides safety and performance.

---

## Q3. Is final class always immutable?

No.

Example:

```java id="s9v3m2"
final class Employee {

    private int age;

    public void setAge(int age){

        this.age=age;

    }

}
```

Class is final but mutable.

---

## Q4. Why are immutable objects thread safe?

Because:

> Their state cannot change after construction, so multiple threads can safely read the same object without synchronization.

---

## Q5. Why are String objects good HashMap keys?

Because:

* hashCode never changes
* equals result never changes
* safe bucket lookup

---

# Interview Boundary

For 7+ years Java developer:

Must know deeply:

✅ Definition
✅ Benefits
✅ Creating immutable class
✅ Defensive copying
✅ String immutability
✅ final vs immutable
✅ HashMap key connection
✅ Thread safety connection

Deep dive not required:

* JVM string implementation details (covered separately)
* Persistent data structures

---

Next topic:

# String Internals ⭐⭐⭐⭐⭐

We will cover:

* String pool
* Heap vs String pool
* `new String()` vs literal
* `intern()`
* Why String is immutable
* Java 8 vs Java 9 String implementation changes
* StringBuilder vs StringBuffer

This is one of the most asked Java senior interview topics.


# String Internals ⭐⭐⭐⭐⭐

String is one of the **most frequently asked Java interview topics** because it touches:

* JVM memory
* Heap
* String Constant Pool
* Immutability
* Performance optimization
* HashMap keys
* Thread safety

For a **7+ years Java developer**, you should know the internal behaviour, not just "String is immutable".

---

# 1. Why is String Special in Java?

In Java:

```java
String name = "John";
```

is not treated like a normal object.

String has special JVM support:

```text id="h7s0cz"
String Literal

        |
        |
        v

String Constant Pool
```

The JVM maintains a pool of unique string literals to save memory.

---

# 2. String Immutability ⭐⭐⭐⭐⭐

Let's start with the foundation.

Example:

```java
String s = "Hello";

s = s + " World";
```

Many think:

```text
Hello

becomes

Hello World
```

But internally:

```text id="0j6r3w"
String Pool


"Hello"

     |
     |
     v

"Hello World"
```

The original object is unchanged.

The variable `s` now points to a new String object.

---

# Why is String Immutable?

## Reason 1: Security ⭐⭐⭐⭐⭐

Strings are used for sensitive operations:

Examples:

```text id="4p2f4j"
File paths

Database URLs

Network connections

Class names

Security permissions
```

Example:

```java
String file = "/home/user/config";
```

Imagine String was mutable:

```text id="5d9n4m"
Validate path

        |

Someone changes string

        |

Application accesses wrong file
```

Immutability prevents this.

---

## Reason 2: String Pool ⭐⭐⭐⭐⭐

Consider:

```java
String a = "Java";

String b = "Java";
```

JVM can reuse the same object:

```text id="q9j1s0"
String Pool


       "Java"
        /  \
       /    \
      a      b
```

If String was mutable:

```java
a.replace("Java","Python");
```

then:

```text
b
```

would also change.

That would break memory optimization.

---

## Reason 3: HashMap Keys

Example:

```java
Map<String,Integer> map =
        new HashMap<>();

map.put("John",100);
```

HashMap depends on:

```text id="4f8h3k"
hashCode()
```

String hashCode is cached.

Because String cannot change:

```text id="2a7g5p"
"John"

hashCode = fixed
```

Safe as a key.

---

## Reason 4: Thread Safety

Multiple threads can safely share String objects.

Example:

```java
String token = "abc123";
```

Thread 1:

```
read token
```

Thread 2:

```
read token
```

No synchronization required.

---

# 3. String Constant Pool ⭐⭐⭐⭐⭐

The String pool is a special memory area inside the Heap.

(Older Java versions had it in PermGen, Java 7+ moved it to Heap.)

Example:

```java
String s1 = "Java";

String s2 = "Java";
```

Execution:

First line:

```text
Create "Java" in pool
```

Second line:

```text
Reuse existing "Java"
```

Memory:

```text
Stack                  Heap/String Pool


s1  -----------------> "Java"
                         ^
                         |
s2  ---------------------+
```

---

# 4. new String() vs String Literal ⭐⭐⭐⭐⭐

Very common interview question.

## Case 1: String Literal

```java
String s1 = "Java";
```

Creates:

```text
String Pool

"Java"
```

---

## Case 2: new String()

```java
String s2 = new String("Java");
```

Creates:

Two objects possible:

```text
Heap


String Object
     |
     |
     v

"Java"


String Pool

"Java"
```

The literal part goes to the pool.

The `new` creates another object in heap.

---

# Comparison

```java
String s1 = "Java";

String s2 = new String("Java");

System.out.println(s1 == s2);
```

Output:

```
false
```

Why?

Because:

```text
s1 -> Pool object

s2 -> Heap object
```

Different references.

---

But:

```java
System.out.println(s1.equals(s2));
```

Output:

```
true
```

Because content is same.

---

# 5. intern() Method ⭐⭐⭐⭐

## What does intern() do?

`intern()` returns the pooled version of a String.

Example:

```java
String s1 =
        new String("Java");

String s2 =
        s1.intern();
```

Now:

```text
s2
 |
 v

String Pool

"Java"
```

---

Example:

```java
String a = new String("Hello");

String b = a.intern();

String c = "Hello";


System.out.println(b == c);
```

Output:

```
true
```

Because both point to pool object.

---

# When to use intern()?

Usually rarely.

Reason:

Modern JVM handles string pooling efficiently.

Wrong usage:

```java
for(large data){

    string.intern();

}
```

Can create memory pressure.

---

# 6. String Concatenation Internals ⭐⭐⭐⭐

Example:

```java
String result =
        "Java" + "Developer";
```

Compile time:

Compiler optimizes:

```java
"JavaDeveloper"
```

because both are constants.

---

Runtime:

```java
String name = "Java";

String result =
        name + " Developer";
```

Internally:

Before Java 9:

```text
StringBuilder

append()

toString()
```

Equivalent:

```java
new StringBuilder()
.append(name)
.append(" Developer")
.toString();
```

---

Java 9+:

Compiler may use:

```text
invokedynamic
```

for better concatenation performance.

---

# 7. String vs StringBuilder vs StringBuffer ⭐⭐⭐⭐⭐

## String

Immutable.

Use when:

* Value rarely changes
* Thread-safe sharing required

Example:

```java
String name="John";
```

---

## StringBuilder

Mutable.

Not thread-safe.

Faster.

Use:

```java
StringBuilder sb =
new StringBuilder();

sb.append("Java");
sb.append(" Backend");
```

---

## StringBuffer

Mutable.

Thread-safe.

Uses synchronization.

Slower than StringBuilder.

---

Comparison:

|             | String           | StringBuilder              | StringBuffer              |
| ----------- | ---------------- | -------------------------- | ------------------------- |
| Mutable     | No               | Yes                        | Yes                       |
| Thread-safe | Yes              | No                         | Yes                       |
| Performance | Slow for changes | Fast                       | Medium                    |
| Use case    | Fixed text       | Single thread modification | Multi-thread modification |

---

# 8. Java 8 vs Java 9 String Internals ⭐⭐⭐

## Before Java 9

String internally:

```java
private final char[] value;
```

Example:

```text
"Hello"

char[]

H e l l o
```

---

## Java 9+

String uses:

```java
private final byte[] value;
```

with:

```text
Coder
```

to support:

* Latin-1
* UTF-16

Why?

Memory optimization.

Example:

Most English text requires only one byte.

So:

```text
char[]

2 bytes per character
```

became:

```text
byte[]

1 byte per character
```

when possible.

Feature:

**Compact Strings**

---

# 9. Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Why is String immutable?

Answer:

Because it provides:

* Security
* String pool optimization
* Thread safety
* Stable hashCode for collections

---

## Q2. Difference between:

```java
String s1="Java";

String s2=new String("Java");
```

Answer:

Literal:

* Uses String Pool
* Reuses existing object

new:

* Creates new object in Heap

---

## Q3. Where is String stored?

Answer:

Modern Java:

* String objects → Heap
* String literals → String Constant Pool inside Heap

---

## Q4. Why is String a good HashMap key?

Answer:

Because:

* Immutable
* hashCode does not change
* Equality remains consistent

---

## Q5. Difference between StringBuilder and StringBuffer?

Answer:

StringBuilder:

* Faster
* Not synchronized

StringBuffer:

* Thread-safe
* Synchronised
* Slower

---

## Q6. Does intern() create a new String?

Answer:

No.

It returns the existing pooled String if available, otherwise adds the string to the pool.

---

# Interview Boundary

For 7+ years Java developer:

Must know deeply:

✅ String immutability
✅ String pool
✅ Heap vs pool
✅ literal vs new String()
✅ intern()
✅ StringBuilder/StringBuffer
✅ Java 8 vs Java 9 compact strings

Deep dive not required:

* JVM StringTable implementation
* Hash bucket tuning
* Native String operations

---

Next topic:

# Wrapper Classes ⭐⭐⭐⭐

We will cover:

* Primitive vs Wrapper
* Autoboxing/unboxing
* Integer cache
* `==` vs `equals()`
* Null pitfalls
* Why Collections use wrappers

This connects nicely with Java memory and performance.

# Wrapper Classes ⭐⭐⭐⭐

Wrapper classes are a smaller topic compared to String, but they are **very common in Java interviews** because they test:

* Primitive vs Object understanding
* Heap vs Stack basics
* Autoboxing/unboxing
* Integer caching
* `==` vs `equals()`
* Null handling
* Collections behavior

---

# 1. What are Wrapper Classes?

## Interview Definition

> Wrapper classes provide an object representation of primitive data types so that primitives can be used where objects are required.

Java has 8 primitive types:

```text
byte
short
int
long
float
double
char
boolean
```

Corresponding wrapper classes:

```text
Primitive       Wrapper

byte       ->   Byte
short      ->   Short
int        ->   Integer
long       ->   Long
float      ->   Float
double     ->   Double
char       ->   Character
boolean    ->   Boolean
```

---

# Why do we need Wrapper Classes?

Because Java is not purely object-oriented.

Example:

Primitive:

```java
int age = 30;
```

`int` is not an object.

It cannot:

* call methods
* be stored in collections
* be used with generics

Example:

```java
List<int> numbers;
```

Invalid.

Java collections work with objects:

```java
List<Integer> numbers;
```

Valid.

---

# 2. Primitive vs Wrapper

## Primitive

```java
int a = 10;
```

Stored as value:

```text
Stack

a
|
10
```

---

## Wrapper

```java
Integer b = 10;
```

Conceptually:

```text
Stack

b
|
|
v

Heap

Integer Object
value = 10
```

---

# 3. Autoboxing ⭐⭐⭐⭐⭐

## What is Autoboxing?

Automatic conversion:

```text
primitive
    |
    |
    v
wrapper object
```

Example:

```java
int num = 100;

Integer obj = num;
```

Compiler converts:

```java
Integer obj =
        Integer.valueOf(num);
```

---

Actual:

```java
Integer obj = Integer.valueOf(100);
```

---

# 4. Unboxing

Reverse process:

```text
Wrapper object

      |

      v

Primitive
```

Example:

```java
Integer obj = 100;

int num = obj;
```

Compiler converts:

```java
int num =
obj.intValue();
```

---

# 5. Integer Cache ⭐⭐⭐⭐⭐

Very common interview question.

Consider:

```java
Integer a = 100;

Integer b = 100;


System.out.println(a == b);
```

Output:

```text
true
```

Why?

Because Integer caches values:

```text
-128 to 127
```

Memory:

```text
Integer Cache


100
 |
 +---- a

 |
 +---- b
```

Both references point to same cached object.

---

Now:

```java
Integer a = 200;

Integer b = 200;


System.out.println(a == b);
```

Output:

```text
false
```

Why?

Because 200 is outside cache range.

Objects are created separately.

Memory:

```text
Heap


Integer(200)

    ^
    |
    a


Integer(200)

    ^
    |
    b
```

---

# Important Interview Trap

Never compare wrapper objects using:

```java
==
```

Use:

```java
equals()
```

Example:

Wrong:

```java
Integer x = 200;
Integer y = 200;

if(x == y)
```

Correct:

```java
if(x.equals(y))
```

---

# 6. Why Integer.valueOf() instead of new Integer()?

Old:

```java
Integer x =
new Integer(100);
```

Creates new object every time.

Modern:

```java
Integer x =
Integer.valueOf(100);
```

Uses cache.

---

Example:

```java
Integer a = Integer.valueOf(100);
Integer b = Integer.valueOf(100);

System.out.println(a == b);
```

true.

---

# 7. Wrapper Classes are Immutable ⭐⭐⭐⭐⭐

All wrapper classes are immutable.

Example:

```java
Integer x = 10;

x = x + 5;
```

Looks like modification.

But internally:

```text
Before:

Integer Object
value=10


After:

New Integer Object
value=15
```

Original object unchanged.

---

Why?

Benefits:

* Thread safety
* Cache support
* Safe usage as HashMap keys

---

# 8. Null Problem ⭐⭐⭐⭐⭐

This is a common production bug.

Primitive:

```java
int count = 0;
```

Cannot be null.

---

Wrapper:

```java
Integer count = null;
```

Allowed.

But:

```java
int value = count;
```

causes:

```
NullPointerException
```

because JVM tries:

```java
count.intValue()
```

on null.

---

Example:

```java
Integer number = null;

System.out.println(number + 10);
```

Compiler converts:

```java
number.intValue() + 10
```

which fails.

---

# 9. Wrapper Classes and Collections

Collections require objects.

Example:

Primitive:

```java
List<int> list;
```

Not allowed.

Wrapper:

```java
List<Integer> list =
new ArrayList<>();

list.add(10);
```

Internally:

```java
list.add(Integer.valueOf(10));
```

Autoboxing happens.

---

# 10. Parsing Methods

Wrappers provide conversion utilities.

Example:

String → int

```java
String value = "123";

int number =
Integer.parseInt(value);
```

Output:

```text
123
```

---

String → Integer

```java
Integer obj =
Integer.valueOf("123");
```

Difference:

```text
parseInt()

returns primitive


valueOf()

returns wrapper object
```

---

# 11. Primitive vs Wrapper Decision

## Use primitive when:

* Performance matters
* Value cannot be null
* Local calculations

Example:

```java
for(int i=0;i<100000;i++)
```

---

## Use wrapper when:

* Collections
* Generics
* Nullable values
* Framework objects

Example:

Spring DTO:

```java
class UserRequest {

    private Integer age;

}
```

Why not:

```java
private int age;
```

Because:

Missing field:

```json
{
}
```

can be represented as:

```java
age = null
```

---

# Interview Questions ⭐⭐⭐⭐⭐

## Q1. Why do we need wrapper classes?

Answer:

> Java collections and generics work only with objects. Wrapper classes provide object representation for primitive types and also provide utility methods.

---

## Q2. Difference between int and Integer?

| int            | Integer          |
| -------------- | ---------------- |
| Primitive      | Object           |
| Cannot be null | Can be null      |
| Faster         | Slight overhead  |
| No methods     | Has methods      |
| Stack value    | Object reference |

---

## Q3. Why does this return true?

```java
Integer a = 100;
Integer b = 100;

a == b
```

Answer:

Because Integer caches values from -128 to 127.

---

## Q4. Why does this return false?

```java
Integer a = 200;
Integer b = 200;

a == b
```

Answer:

Because outside cache range, separate objects are created.

---

## Q5. What happens internally during autoboxing?

Answer:

Compiler converts:

```java
Integer x = 10;
```

into:

```java
Integer x =
Integer.valueOf(10);
```

---

## Q6. Why are wrapper classes immutable?

Answer:

Because immutability provides:

* Thread safety
* Stable hashCode
* Safe caching

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ Primitive vs Wrapper
✅ Autoboxing/unboxing
✅ Integer cache
✅ `==` vs `equals()`
✅ NullPointerException during unboxing
✅ Collections usage
✅ valueOf vs new

Deep dive not required:

* JVM IntegerCache implementation details
* Custom wrapper creation

---

Next topic:

# Enum ⭐⭐⭐⭐

We will cover:

* Enum internals (Enum is actually a class)
* Enum constructor
* Fields and methods
* Enum Singleton pattern
* Enum vs constants
* Why enum is preferred over static final constants
* Serialization safety of enum singleton
