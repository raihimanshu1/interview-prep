# Shallow Copy vs Deep Copy ⭐⭐⭐⭐

This is one of the most common Java interview topics because it tests whether you truly understand **objects, references, memory, and cloning**.

Many developers know the definitions but fail to explain **what actually gets copied**.

---

# 1. Why This Concept Matters

Whenever you copy an object, Java has two choices:

* Copy only the object itself.
* Copy the object **and everything it references**.

This leads to two types of copying:

* **Shallow Copy**
* **Deep Copy**

Understanding this is important when working with:

* `clone()`
* Copy constructors
* Serialization
* Immutable objects
* DTOs
* Collections
* Spring applications

---

# 2. Object Memory

Consider:

```java
class Address {

    String city;

}

class Employee {

    String name;
    Address address;

}
```

Object in memory:

```text
Employee
+------------------+
| name = "John"    |
| address ---------+--------+
+------------------+        |
                            |
                            V
                     Address Object
                  +----------------+
                  | city = "Delhi" |
                  +----------------+
```

Notice:

Employee **doesn't contain** the Address object.

It only contains a **reference** to it.

This is the key to understanding shallow and deep copy.

---

# 3. What is a Shallow Copy?

## Interview Definition

> A shallow copy creates a new object but copies the references of nested objects instead of creating new nested objects.

Only the top-level object is duplicated.

Referenced objects are shared.

---

Example:

```java
Employee emp1 = new Employee();

emp1.name = "John";

emp1.address = new Address();
emp1.address.city = "Delhi";

Employee emp2 = emp1.clone();
```

Memory:

```text
emp1
   |
   V
+----------------------+
| name = John          |
| address ------------+----------------+
+----------------------+                |
                                        |
emp2                                   |
   |                                   |
   V                                   |
+----------------------+                |
| name = John          |                |
| address ------------+-----------------+
+----------------------+

                    Address
                +----------------+
                | city = Delhi   |
                +----------------+
```

Both employees point to the **same Address object**.

---

# 4. Problem with Shallow Copy

```java
emp2.address.city = "Mumbai";
```

Now:

```java
System.out.println(emp1.address.city);
```

Output:

```text
Mumbai
```

Why?

Because both objects share the same `Address`.

Only one Address object exists.

---

# 5. What is a Deep Copy?

## Interview Definition

> A deep copy creates a completely independent copy of an object, including all nested mutable objects.

Every referenced object is copied.

Nothing is shared.

---

Example:

```java
Employee emp2 = new Employee();

emp2.name = emp1.name;

emp2.address = new Address();
emp2.address.city = emp1.address.city;
```

Memory:

```text
emp1
   |
   V
+----------------------+
| name = John          |
| address ------------+
+----------------------+ 
                       |
                       V
                +----------------+
                | Delhi          |
                +----------------+

emp2
   |
   V
+----------------------+
| name = John          |
| address ------------+
+----------------------+
                       |
                       V
                +----------------+
                | Delhi          |
                +----------------+
```

Now two separate Address objects exist.

---

# 6. Modifying Deep Copy

```java
emp2.address.city = "Mumbai";
```

Now:

```java
emp1.address.city
```

Still:

```text
Delhi
```

Because both objects are completely independent.

---

# 7. Comparison

| Feature               | Shallow Copy | Deep Copy |
| --------------------- | ------------ | --------- |
| New top-level object  | ✓            | ✓         |
| Nested objects copied | ✗            | ✓         |
| References shared     | ✓            | ✗         |
| Independent objects   | ✗            | ✓         |
| Performance           | Faster       | Slower    |
| Memory usage          | Less         | More      |

---

# 8. How Does `clone()` Behave?

By default:

```java
Object.clone()
```

performs a **shallow copy**.

Example:

```java
Employee emp2 = emp1.clone();
```

Result:

```text
Employee copied

Address shared
```

This is why `clone()` often surprises developers.

To perform a deep copy using `clone()`, you must also clone every mutable field manually.

---

# 9. How to Create a Deep Copy?

There are several approaches.

### 1. Copy Constructor (Recommended)

```java
class Employee {

    private String name;
    private Address address;

    Employee(Employee other) {
        this.name = other.name;
        this.address = new Address(other.address);
    }
}
```

Simple, explicit, and commonly preferred.

---

### 2. Override `clone()`

Clone the parent object and then clone all mutable fields.

```java
Employee copy = (Employee) super.clone();

copy.address = address.clone();
```

Works but is generally less preferred because `Cloneable` has design limitations.

---

### 3. Serialization

Serialize the object and deserialize it.

Produces a complete deep copy but is relatively slow.

---

### 4. Mapping Libraries

Frameworks like MapStruct or Jackson can also create deep copies in certain scenarios.

---

# 10. Immutable Objects

Suppose `Address` is immutable.

```java
class Address {

    private final String city;

}
```

Now sharing is completely safe.

Multiple objects can reference the same immutable object.

This is one reason immutable classes simplify software design.

---

# 11. Real Production Example

Suppose an order service copies a customer object.

With a shallow copy:

```java
Customer copy = customer.clone();
```

Later:

```java
copy.getAddress().setCity("London");
```

Unexpectedly changes:

```text
Original Customer Address
```

This can lead to difficult production bugs.

A deep copy avoids this by creating a separate Address object.

---

# 12. Visual Summary

### Shallow Copy

```text
Employee A ----+
               |
               V
          Address

Employee B ----+
```

Both employees share the same Address.

---

### Deep Copy

```text
Employee A ---> Address A

Employee B ---> Address B
```

Completely independent.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

### Q1. What is the difference between shallow copy and deep copy?

A **shallow copy** duplicates only the top-level object, while nested mutable objects are shared.

A **deep copy** duplicates both the object and all nested mutable objects, making the copy completely independent.

---

### Q2. Does `Object.clone()` perform a shallow or deep copy?

By default, **`Object.clone()` performs a shallow copy**.

Developers must manually clone mutable fields if a deep copy is required.

---

### Q3. Which is faster?

* Shallow copy → Faster, less memory.
* Deep copy → Slower, more memory, but safer.

---

### Q4. Which approach is preferred in modern Java?

For most applications:

* **Copy constructors** or **factory methods** are preferred.
* `Cloneable` is generally discouraged because of its awkward API and design issues.

---

### Q5. When is a shallow copy safe?

When all referenced objects are **immutable**, or when sharing references is intentional.

---

# Interview Boundary

For a **7+ years Java developer**, you should know deeply:

* Difference between shallow and deep copy.
* Memory/reference diagrams.
* Why `clone()` performs a shallow copy by default.
* Different ways to implement a deep copy.
* Why copy constructors are generally preferred over `Cloneable`.
* Relationship between immutable objects and safe reference sharing.

**Deep Dive if Needed:**

* Serialization-based deep copy performance.
* Third-party libraries for object copying.
* Advanced cloning patterns in complex object graphs.

This completes the **Java Object Model** section alongside `Object`, `equals()`, `hashCode()`, `toString()`, `clone()`, `finalize()`, and **Identity vs Equality**.

Yes. For a **7+ years Java Backend interview**, you've covered **everything that has meaningful interview ROI** for **Shallow vs Deep Copy**.

### Covered ✅

* Difference between shallow and deep copy
* Memory diagrams
* Reference sharing
* Why `clone()` performs shallow copy
* Problems caused by shallow copy
* How to implement deep copy

    * Copy constructor
    * `clone()`
    * Serialization
* Immutable objects and why they're safe to share
* Production example
* Interview questions

---

### Optional (Very Low ROI)

These are occasionally asked but not worth spending much time on:

* Apache Commons `SerializationUtils.clone()`
* Jackson/Gson based deep copy
* Cyclic object graph cloning
* Performance comparison of different copy techniques

These are framework/library questions rather than Java fundamentals.

---

### One small addition I'd make (⭐⭐⭐⭐)

Add this interview question because it's surprisingly common:

> **Q. Why is `Cloneable` generally discouraged in modern Java?**

**Answer:**

* `Cloneable` is a marker interface with a confusing design.
* `Object.clone()` performs only a shallow copy.
* Developers must manually deep-copy mutable fields.
* It bypasses constructors, which can lead to partially initialized objects.
* It requires handling `CloneNotSupportedException`.
* Modern Java prefers **copy constructors**, **factory methods**, or **builder patterns** because they are clearer, safer, and easier to maintain.

---

Apart from that, **I would consider the Shallow vs Deep Copy topic complete** and wouldn't invest more time in it. The time is much better spent on **Collections, Concurrency, Streams, JVM, Spring, and System Design**, where interview weight is significantly higher.
