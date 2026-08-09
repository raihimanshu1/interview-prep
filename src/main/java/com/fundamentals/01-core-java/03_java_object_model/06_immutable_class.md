# Immutable Classes ⭐⭐⭐⭐⭐

Immutability is one of the most important Java design concepts for senior developers.

It connects with:

* Thread safety
* String design
* Collections as keys
* Caching
* Functional programming
* Distributed systems
* Value objects
* Security

Interviewers commonly ask:

* What is an immutable class?
* How do you create one?
* Why is String immutable?
* Is final enough for immutability?
* How does immutability help multithreading?

---

# 1. What is Immutability?

## Interview Definition

> An immutable object is an object whose state cannot be changed after it is created.

Meaning:

Once constructed:

```java
User user = new User("John");
```

The internal data:

```text
name = John
```

can never become:

```text
name = Mike
```

The object remains unchanged.

---

# Mutable vs Immutable ⭐⭐⭐⭐⭐

## Mutable Object

State can change.

Example:

```java
class User {

    String name;

    void setName(String name){
        this.name = name;
    }

}
```

Usage:

```java
User user = new User();

user.setName("John");

user.setName("Mike");
```

Same object changed:

```
Before:

User
 |
 name = John


After:

User
 |
 name = Mike
```

---

## Immutable Object

State cannot change.

Example:

```java
String name = "Java";

name = "Python";
```

Looks like modification.

But internally:

```
Before:

name
 |
 v
"Java"


After:

name
 |
 v
"Python"
```

New object created.

Original:

```
"Java"
```

still exists unchanged.

---

# 2. Why Do We Need Immutable Objects? ⭐⭐⭐⭐⭐

## 1. Thread Safety

Multiple threads can safely share immutable objects.

Example:

```
Thread 1
     |
     |
     v

 User(name="John")


Thread 2
     |
     |
     v

 User(name="John")
```

Nobody can modify it.

No:

* synchronized
* locks
* volatile

required.

---

## 2. HashMap Key Safety

Example:

```java
Map<User,String> map = new HashMap<>();

map.put(user,"Developer");
```

HashMap depends on:

```text
hashCode()
```

If key changes:

```
Before:

hashCode = 100


After modification:

hashCode = 200
```

HashMap cannot find it.

Immutable objects avoid this.

---

## 3. Security

Example:

```java
String path="/admin/config";
```

Imagine String was mutable.

Some code could modify:

```
/admin/config

to

/user/config
```

Security issue.

---

## 4. Caching

Immutable objects can be safely cached.

Example:

```java
String pool
Integer cache
```

because values never change.

---

# 3. Rules to Create Immutable Class ⭐⭐⭐⭐⭐

There are common rules.

---

# Rule 1: Make Class final

Why?

Prevent subclass modification.

Bad:

```java
class User {

}
```

Someone can do:

```java
class EvilUser extends User {

    // change behaviour

}
```

---

Better:

```java
final class User {

}
```

Now inheritance is blocked.

---

# Rule 2: Make Fields private and final

Example:

Bad:

```java
class User {

    String name;

}
```

Anyone can modify.

---

Good:

```java
final class User {

    private final String name;

}
```

Meaning:

* private → no direct access
* final → assigned once

---

# Rule 3: Initialize Fields Through Constructor

Example:

```java
final class User {

    private final String name;


    User(String name){

        this.name=name;

    }

}
```

Object is complete after creation.

---

# Rule 4: No Setters

Bad:

```java
public void setName(String name){

    this.name=name;

}
```

This breaks immutability.

---

# Rule 5: Return Defensive Copies for Mutable Objects ⭐⭐⭐⭐⭐

This is the most important senior-level point.

Consider:

```java
class Employee {

    private final Date joiningDate;


    Employee(Date date){

        this.joiningDate=date;

    }


    public Date getJoiningDate(){

        return joiningDate;

    }

}
```

Problem:

`Date` is mutable.

External code:

```java
employee.getJoiningDate()
       .setYear(2026);
```

Now internal state changed.

Immutability broken.

---

## Solution: Defensive Copy

Constructor:

```java
Employee(Date date){

    this.joiningDate =
        new Date(date.getTime());

}
```

Getter:

```java
public Date getJoiningDate(){

    return new Date(joiningDate.getTime());

}
```

Now:

External object:

```
Date 1
```

Internal object:

```
Date 2
```

are separate.

---

# 4. Complete Immutable Class Example ⭐⭐⭐⭐⭐

```java
import java.util.List;

public final class Employee {


    private final int id;

    private final String name;

    private final List<String> skills;



    public Employee(int id,
                    String name,
                    List<String> skills){

        this.id=id;
        this.name=name;

        this.skills =
            List.copyOf(skills);

    }



    public int getId(){

        return id;

    }


    public String getName(){

        return name;

    }


    public List<String> getSkills(){

        return skills;

    }

}
```

---

Why immutable?

## Class final

```java
final class Employee
```

Cannot extend.

---

## Fields final

```java
private final int id;
```

Cannot reassign.

---

## No setters

No external modification.

---

## List.copyOf()

Creates immutable copy.

Without it:

```java
skills.add("Java");
```

could modify internal state.

---

# 5. Is final Enough for Immutability? ⭐⭐⭐⭐⭐

No.

Very common interview trap.

Example:

```java
final class User {


private final Address address;


}
```

Question:

Is this immutable?

Answer:

Depends on Address.

---

If Address:

```java
class Address {

String city;

}
```

Mutable.

Then:

```java
user.getAddress()
    .setCity("London");
```

changes User state.

---

Need defensive copying.

---

# 6. String Immutability ⭐⭐⭐⭐⭐

String is the most famous immutable class.

Example:

```java
String s="Java";

s.concat("17");
```

Many beginners think:

```
s = Java17
```

Wrong.

Actual:

```
s
 |
 v
"Java"


New object:

"Java17"
```

Unless:

```java
s=s.concat("17");
```

---

Why String immutable?

## 1. Security

Used in:

* URLs
* File paths
* Class loading
* Database connections

---

## 2. String Pool

Example:

```java
String a="Java";

String b="Java";
```

Both share:

```
       "Java"

        ^
       / \
      a   b
```

If mutable:

Changing one changes all.

---

## 3. HashMap Key

Example:

```java
Map<String,Integer> map;
```

String hash is stable.

---

# 7. Immutable Collections ⭐⭐⭐⭐

Java provides:

```java
List.of()
Set.of()
Map.of()
```

Example:

```java
List<String> names =
List.of("Java","Python");
```

Modification:

```java
names.add("Go");
```

throws:

```
UnsupportedOperationException
```

---

# 8. Immutable Objects and Multithreading ⭐⭐⭐⭐⭐

Mutable:

```
Thread 1
    |
    v
Object
    ^
    |
Thread 2

Need:
lock/synchronization
```

---

Immutable:

```
Thread 1
    |
    v

Immutable Object


Thread 2
    |
    v

Same Object
```

No race condition.

---

Example:

```java
public final class Config {

    private final String url;

}
```

Many threads can safely read.

---

# 9. Immutable vs Unmodifiable ⭐⭐⭐⭐

Important distinction.

## Immutable

Object itself cannot change.

Example:

```java
String
```

---

## Unmodifiable View

Wrapper prevents modification but original can change.

Example:

```java
List<String> original =
new ArrayList<>();


List<String> view =
Collections.unmodifiableList(original);
```

Now:

```java
view.add("Java");
```

fails.

But:

```java
original.add("Java");
```

works.

View changes.

---

# 10. Real Production Examples ⭐⭐⭐⭐⭐

## DTO / Value Objects

Example:

```java
Money
Address
Currency
Email
UserId
```

These represent values.

They should not change.

---

## Configuration Objects

Example:

```java
DatabaseConfig
```

Once loaded:

```
url
username
timeout
```

should remain constant.

---

## Cache Keys

Example:

```java
CacheKey(userId, region)
```

Must be immutable.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Is String final because it is immutable?

Not exactly.

String is:

* immutable
* final

Final prevents subclasses from breaking immutability.

---

## Q2. Is final object immutable?

No.

Example:

```java
final List<String> list;
```

List contents can change.

---

## Q3. How does immutability provide thread safety?

Because state cannot change after creation.

No synchronization needed.

---

## Q4. Why immutable objects are good HashMap keys?

Because hashCode never changes.

---

## Q5. How to make a class immutable with List field?

Use defensive copy:

```java
this.list = List.copyOf(list);
```

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ Definition of immutable object
✅ Rules to create immutable class
✅ final vs immutable
✅ Defensive copying
✅ String immutability reasons
✅ Thread safety advantage
✅ HashMap key importance

Deep dive not required:

* JVM String object memory layout
* Escape analysis of immutable objects

---

Next topic:

# String Internals ⭐⭐⭐⭐⭐

We will cover:

* String pool
* Heap vs String Constant Pool
* `new String()` vs literal
* intern()
* Java 8 vs Java 9 String changes
* String immutability connection
* Interview traps
