# Enum ⭐⭐⭐⭐

Enums look simple, but for senior Java interviews they are important because they connect with:

* Type safety
* Singleton pattern
* Switch statements
* Serialization
* Design choices
* Java internals

---

# 1. What is Enum?

## Interview Definition

> Enum is a special Java type used to represent a fixed set of constants. Internally, every enum is a class that extends `java.lang.Enum`.

Example:

```java
enum Status {

    CREATED,
    PROCESSING,
    COMPLETED,
    FAILED

}
```

Usage:

```java
Status status = Status.CREATED;
```

---

# 2. Enum Internally is a Class ⭐⭐⭐⭐⭐

Important interview point.

When we write:

```java
enum Day {

    MONDAY,
    TUESDAY

}
```

Java internally creates something similar:

```java
final class Day extends Enum<Day> {


    public static final Day MONDAY =
            new Day("MONDAY",0);


    public static final Day TUESDAY =
            new Day("TUESDAY",1);


}
```

Meaning:

* Enum constants are objects
* They are created once
* They are static final references

---

# Memory View

Example:

```java
Status s = Status.CREATED;
```

Memory:

```text
Stack


s
|
|
v


Heap


Status Object

CREATED
```

Only one CREATED object exists.

---

# 3. Why Enum Instead of Constants?

Old approach:

```java
public class Status {

    public static final String CREATED =
            "CREATED";

    public static final String FAILED =
            "FAILED";

}
```

Problem:

No type safety.

Example:

```java
String status="HELLO";
```

Compiler allows it.

---

Enum:

```java
Status status =
        Status.CREATED;
```

Now:

```java
status = "HELLO";
```

Compilation error.

---

# Enum Benefits

## 1. Type Safety

Prevents invalid values.

---

## 2. Singleton Guarantee

Each enum constant exists only once.

Example:

```java
Status a = Status.CREATED;

Status b = Status.CREATED;


System.out.println(a == b);
```

Output:

```
true
```

Because same object.

---

## 3. Readability

Instead of:

```java
if(status == 1)
```

Use:

```java
if(status == Status.COMPLETED)
```

---

# 4. Enum with Fields and Constructor ⭐⭐⭐⭐⭐

Enums can have:

* fields
* constructors
* methods

Example:

```java
enum PaymentStatus {


    SUCCESS(200),
    FAILED(500);


    private final int code;


    PaymentStatus(int code){

        this.code = code;

    }


    public int getCode(){

        return code;

    }

}
```

Usage:

```java
System.out.println(
    PaymentStatus.SUCCESS.getCode()
);
```

Output:

```
200
```

---

# Important Point

Enum constructor is:

```java
private
```

implicitly.

You cannot do:

```java
new PaymentStatus(200);
```

Because enum instances are controlled by JVM.

---

# 5. Enum Methods

Every enum gets built-in methods.

---

## values()

Returns all constants.

Example:

```java
for(Status s : Status.values()){

    System.out.println(s);

}
```

Output:

```
CREATED
PROCESSING
COMPLETED
FAILED
```

---

## valueOf()

Convert String to enum.

Example:

```java
Status status =
        Status.valueOf("CREATED");
```

Returns:

```
Status.CREATED
```

Important:

If value doesn't exist:

```java
Status.valueOf("DONE");
```

throws:

```
IllegalArgumentException
```

---

## ordinal()

Returns position.

Example:

```java
enum Status {

    CREATED,
    COMPLETED

}
```

Then:

```java
Status.CREATED.ordinal()
```

returns:

```
0
```

and:

```java
Status.COMPLETED.ordinal()
```

returns:

```
1
```

---

# Should we use ordinal()?

Usually no.

Bad:

```java
if(status.ordinal()==1)
```

Why?

Changing enum order breaks logic.

Example:

Before:

```
CREATED 0
FAILED 1
```

After:

```
CREATED 0
PROCESSING 1
FAILED 2
```

Logic breaks.

Use explicit fields:

```java
FAILED(500)
```

---

# 6. Enum in switch ⭐⭐⭐⭐⭐

Very common.

Example:

```java
switch(status){

case CREATED:
    processCreation();
    break;


case COMPLETED:
    sendEmail();
    break;


case FAILED:
    retry();
    break;

}
```

Benefits:

* Cleaner
* Compiler checks cases
* No string comparison

---

# 7. Enum Singleton Pattern ⭐⭐⭐⭐⭐

Very important senior interview question.

Question:

> How do you create a thread-safe singleton?

Classic approaches:

* Double checked locking
* Static holder pattern
* Enum singleton

Enum is considered the safest.

---

Example:

```java
public enum DatabaseConnection {


    INSTANCE;


    public void connect(){

        System.out.println("Connected");

    }

}
```

Usage:

```java
DatabaseConnection db =
        DatabaseConnection.INSTANCE;


db.connect();
```

---

Why is it Singleton?

Because JVM guarantees:

```text
Only one INSTANCE object
```

---

# Why Enum Singleton is Better?

## 1. Thread Safe

JVM creates enum constants during class initialization.

No synchronization needed.

---

## 2. Serialization Safe

Normal singleton problem:

```java
Singleton obj1 = instance;

serialize(obj1);

deserialize();

obj2
```

Can create another object.

Enum avoids this.

JVM serialization guarantees:

```text
same enum instance
```

---

## 3. Reflection Safe

Reflection cannot create another enum instance.

---

# 8. Enum vs Static Final Constants ⭐⭐⭐⭐⭐

| Static final            | Enum                |
| ----------------------- | ------------------- |
| No type safety          | Type safe           |
| Can have invalid values | Restricted values   |
| Primitive/String mostly | Full objects        |
| No built-in methods     | values(), valueOf() |
| No singleton guarantee  | Singleton guarantee |

---

# 9. Enum and Strategy Pattern

Advanced but useful.

Enums can contain behavior.

Example:

```java
enum Operation {


    ADD {

        public int apply(int a,int b){
            return a+b;
        }

    },


    SUBTRACT {

        public int apply(int a,int b){
            return a-b;
        }

    };


    public abstract int apply(int a,int b);

}
```

Usage:

```java
Operation.ADD.apply(10,5);
```

Output:

```
15
```

This avoids:

```java
if(operation=="ADD")
```

chains.

---

# 10. Common Interview Questions ⭐⭐⭐⭐⭐

## Q1. Is enum a class in Java?

Answer:

Yes.

Every enum extends:

```java
java.lang.Enum
```

and enum constants are objects.

---

## Q2. Can enum have constructor?

Yes.

Example:

```java
enum Status {

SUCCESS(200);

private int code;

Status(int code){

this.code=code;

}

}
```

Constructor is private.

---

## Q3. Can we create enum object using new?

No.

Enum instances are created by JVM.

---

## Q4. Why is enum singleton preferred?

Because:

* Thread safe
* Serialization safe
* Reflection safe
* Simple implementation

---

## Q5. Difference between == and equals() for enum?

Both work.

Example:

```java
status == Status.CREATED
```

is preferred.

Reason:

Enum constants are singleton objects.

---

# Interview Boundary

For 7+ years Java developer:

Must know:

✅ Enum is a class
✅ Enum constants are objects
✅ Constructor + fields
✅ values()/valueOf()
✅ Enum singleton
✅ Serialization safety
✅ Enum vs constants

Deep dive not required:

* Enum bytecode
* JVM enum initialization internals

---

# Java Object Model Module Completed ✅

Covered:

```text
Java Object Model

✓ Object class
✓ equals()
✓ hashCode()
✓ toString()
✓ clone()
✓ finalize()
✓ Identity vs Equality
✓ Shallow vs Deep Copy
✓ Immutable Classes
✓ String Internals
✓ Wrapper Classes
✓ Enum
```

Next logical module:

# Java Keywords ⭐⭐⭐⭐⭐

Order:

1. `final` keyword (deep dive)
2. `static` keyword
3. abstract
4. volatile
5. synchronized
6. transient
7. this/super

I recommend starting with **final keyword** because it connects directly with immutability, String, and JVM memory model.
