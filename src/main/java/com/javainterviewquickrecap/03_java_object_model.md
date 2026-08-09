# Module 1c — Java Object Model (equals/hashCode/clone) — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---


Perfect. This is a **⭐⭐⭐⭐⭐ interview section** because these topics are interconnected. We'll cover them together instead of separately.

---

# Java Object Model ⭐⭐⭐⭐⭐

## Big Picture

Every class in Java ultimately inherits from `Object`.

```text
                    Object
         (Root of Java Class Hierarchy)
                     ▲
                     │
        ┌────────────┼────────────┐
        │            │            │
      String      Employee      ArrayList
        │            │            │
        └────────────┴────────────┘
             extends Object
```

### Why?

Java wants every object to have common behaviour like:

* equality
* hashing
* printing
* cloning
* synchronization

That's why every object gets methods from `Object`.

---

# Object Class ⭐⭐⭐⭐⭐

```java
public class Object {

    boolean equals(Object obj);

    int hashCode();

    String toString();

    Object clone();

    Class<?> getClass();

    void wait();

    void notify();

    void notifyAll();

    protected void finalize(); // Deprecated
}
```

### Interview Questions

* Why is Object parent of every class?
* Which Object methods are overridden most?
* Which methods are used by HashMap?

**Most important methods:**

```text
equals()
hashCode()
toString()
clone()
```

---

# equals() ⭐⭐⭐⭐⭐

## What?

Checks **logical equality**.

Default implementation

```java
Object obj1 = new Object();
Object obj2 = new Object();

obj1.equals(obj2);
```

↓

```text
false
```

Because default `equals()` behaves like `==`.

---

## Override Example

```java
class Employee {

    int id;

    Employee(int id){
        this.id=id;
    }

    @Override
    public boolean equals(Object o){

        if(this==o) return true;

        if(!(o instanceof Employee))
            return false;

        Employee e=(Employee)o;

        return id==e.id;
    }
}
```

Now

```java
new Employee(10).equals(new Employee(10))
```

↓

```text
true
```

---

## Interview Diagram

```text
Employee(10) ------------+
                          │
                          ▼
                  equals()

                          │
             Compare IDs instead of
              comparing references
                          │
                          ▼
                     true
```

---

# hashCode() ⭐⭐⭐⭐⭐

## What?

Returns an integer representing the object.

Used by

* HashMap
* HashSet
* Hashtable
* ConcurrentHashMap

---

## Rule ⭐⭐⭐⭐⭐

```text
equals()==true
        │
        ▼
hashCode MUST be same
```

But

```text
Same hashCode
       │
       ▼
equals may be false
```

---

## Interview Diagram

```text
Employee(101)

      │

hashCode()

      │

      ▼

Bucket 5

      │

equals()

      ▼

Actual Object Found
```

HashMap first finds the **bucket** using `hashCode()`, then calls `equals()` to locate the exact key.

---

## Interview Questions

* Why override hashCode with equals?
* What happens if only equals is overridden?
* Why collisions occur?
* Can two objects have same hashCode?

---

# equals() vs == ⭐⭐⭐⭐⭐

| ==                   | equals()                  |
| -------------------- | ------------------------- |
| Reference comparison | Logical comparison        |
| Cannot be overridden | Usually overridden        |
| Faster               | Depends on implementation |

Example

```java
String s1=new String("Java");
String s2=new String("Java");

s1==s2;
```

↓

false

```java
s1.equals(s2);
```

↓

true

---

# Identity vs Equality ⭐⭐⭐⭐⭐

```text
                Person A
                  │
             id=101
```

Another object

```text
                Person B
                  │
             id=101
```

Memory

```text
+---------+       +---------+
| Obj A   |       | Obj B   |
+---------+       +---------+
```

Identity

```text
Obj A == Obj B

false
```

Equality

```text
ObjA.equals(ObjB)

true
```

---

# toString() ⭐⭐⭐

Default

```java
Employee@5ca881b5
```

Override

```java
@Override
public String toString(){

    return "Employee{id="+id+"}";
}
```

Output

```text
Employee{id=10}
```

Used for

* Logging
* Debugging
* REST responses
* Exception messages

---

# clone() ⭐⭐⭐⭐

Creates a copy.

```java
Employee copy=(Employee)emp.clone();
```

Needs

```java
implements Cloneable
```

Otherwise

```text
CloneNotSupportedException
```

---

## Shallow Copy ⭐⭐⭐⭐⭐

```text
Employee
   │
   ▼
Address
```

Clone

```text
Employee Copy
     │
     ▼
Same Address Object
```

```text
Employee1 ------+
                │
                ▼
            Address

Employee2 ------+
```

Changing Address affects both.

---

## Deep Copy ⭐⭐⭐⭐⭐

```text
Employee1

    │

    ▼

Address1
```

Clone

```text
Employee2

    │

    ▼

Address2
```

Completely separate objects.

---

## Interview Question

Difference between shallow and deep copy?

| Shallow              | Deep                      |
| -------------------- | ------------------------- |
| References copied    | Objects copied            |
| Shared child objects | Independent child objects |
| Faster               | More expensive            |

---

# finalize() ⭐⭐⭐⭐

Old method

```java
protected void finalize()
```

Called before GC.

People used it for

* Closing files
* Releasing sockets
* Cleaning resources

---

## Why Deprecated?

Because

```text
GC decides

WHEN

or

IF

finalize() runs
```

Problems

* Unpredictable
* Slow
* Security issues
* Object resurrection

---

## Modern Alternative

Use

```java
try-with-resources
```

or

```java
AutoCloseable
```

instead of relying on `finalize()`.

---

# Interview Questions ⭐⭐⭐⭐⭐

* Why is Object class parent of every class?
* Difference between `==` and `equals()`?
* Identity vs equality?
* Why override `hashCode()` with `equals()`?
* What happens if `hashCode()` isn't overridden?
* Can two unequal objects have same hashCode?
* Why is `clone()` considered problematic?
* Difference between shallow and deep copy?
* Why is `finalize()` deprecated?
* Which `Object` methods are most commonly overridden?

---

# Quick Revision

```text
Object
   │
   ├── equals()     → Logical Equality
   ├── hashCode()   → Hash-based Collections
   ├── toString()   → Readable Output
   ├── clone()      → Object Copy
   └── finalize()   → Deprecated
```

---

# Interview Cheat Sheet ⭐⭐⭐⭐⭐

| Method       | Purpose               | Used In                     |
| ------------ | --------------------- | --------------------------- |
| `equals()`   | Logical comparison    | Collections, business logic |
| `hashCode()` | Bucket calculation    | HashMap, HashSet            |
| `toString()` | String representation | Logging, debugging          |
| `clone()`    | Object copy           | Rare in modern Java         |
| `finalize()` | Cleanup               | Deprecated                  |

---

## ⭐ 7+ Years Tip

The most important connection interviewers expect you to understand is:

```text
          HashMap.put(key, value)
                    │
                    ▼
          key.hashCode()
                    │
                    ▼
            Find Bucket
                    │
                    ▼
        Collision? ──────► Yes
             │                │
             ▼                ▼
        Insert Directly   key.equals(existingKey)
                               │
                     Equal? ───┴────► Replace Value
                               │
                              No
                               │
                               ▼
                     Store as another entry
```

This is one of the most frequently discussed execution flows in senior Java interviews and leads directly into the **HashMap internals** section, where we'll examine exactly how buckets, collisions, linked lists, and red-black trees work internally.


clone, shallow copy, deep copy -

Excellent question. This is exactly where many people get confused because **`clone()`**, **shallow copy**, and **deep copy** are related but **they are not the same thing**.

Think of it like this:

```text
clone()
   │
   ▼
How are we copying an object?

        ┌───────────────┬─────────────────┐
        ▼               ▼
 Shallow Copy      Deep Copy
```

**`clone()` is just one mechanism to copy an object.**
The copy produced by `clone()` is **shallow by default**.

Let's build the intuition.

---

# Step 1: Why do we need object copying?

Suppose you have an Employee object.

```java
Employee emp1 = new Employee("John");
```

Memory

```text
        emp1
         │
         ▼
   +-------------+
   | Employee    |
   | name=John   |
   +-------------+
```

Now suppose you write

```java
Employee emp2 = emp1;
```

Many beginners think a new object is created.

**No.**

Memory becomes

```text
emp1 ───────┐
            │
            ▼
      +-------------+
      | Employee    |
      | name=John   |
      +-------------+
            ▲
            │
emp2 ───────┘
```

There is still **only one object**.

If you modify

```java
emp2.name = "Mike";
```

then

```text
emp1.name
```

also becomes

```text
Mike
```

because both references point to the same object.

Sometimes we don't want this.

We want an **independent copy**.

That's why object copying exists.

---

# Step 2: clone()

Suppose

```java
Employee emp2 = emp1.clone();
```

Now Java creates another Employee object.

```text
emp1                    emp2

 │                       │
 ▼                       ▼

+----------+      +----------+
| Employee |      | Employee |
| John     |      | John     |
+----------+      +----------+
```

Great!

But wait...

This only works nicely when an object contains **primitive values**.

Real objects usually contain references.

---

# Step 3: Real Example

Suppose

```java
class Employee{

    String name;

    Address address;
}
```

Memory

```text
Employee

   │
   ├── name = John

   │
   ▼

 Address

    city = Bangalore
```

---

Now let's copy it.

## Shallow Copy

```java
Employee copy = emp.clone();
```

Memory

```text
Employee1                Employee2

    │                        │

    │                        │

    ▼                        ▼

+----------+          +----------+
| John     |          | John     |
|          |          |          |
| address--|--------┐ | address--|------┐
+----------+        │ +----------+      │
                    ▼                   ▼
             +------------------------------+
             | Address                      |
             | Bangalore                    |
             +------------------------------+
```

Notice carefully.

How many Employee objects?

✅ Two

How many Address objects?

❌ Only one

Both employees share the same Address.

---

Now modify

```java
copy.address.city = "Hyderabad";
```

Memory

```text
Employee1

Address.city

↓

Hyderabad

Employee2

Address.city

↓

Hyderabad
```

Both changed.

Because Address was shared.

This is called **Shallow Copy**.

---

# Why is it called Shallow?

Because Java copied only the **first level**.

```text
Employee

↓

Address
```

Employee copied.

Address not copied.

Only reference copied.

---

# Deep Copy

Now suppose we do

```java
Employee copy = new Employee();

copy.name = emp.name;

copy.address =
    new Address(emp.address.city);
```

Memory

```text
Employee1

     │

     ▼

 Address1

 Bangalore
```

Copy

```text
Employee2

     │

     ▼

 Address2

 Bangalore
```

Now change

```java
copy.address.city="Hyderabad";
```

Result

```text
Employee1

Bangalore
```

```text
Employee2

Hyderabad
```

Now they are completely independent.

That's **Deep Copy**.

---

# Visual Comparison ⭐⭐⭐⭐⭐

## Shallow Copy

```text
Employee1
   │
   │
   ▼
 Address
   ▲
   │
   │
Employee2
```

👉 Same Address object shared.

---

## Deep Copy

```text
Employee1

    │

    ▼

Address1



Employee2

    │

    ▼

Address2
```

👉 Everything copied.

---

# So where does clone() fit?

Very important interview point.

```text
clone()

     │

     ▼

By default

↓

Shallow Copy
```

Java's `Object.clone()` simply copies the object's fields.

If a field is a reference,

it copies the **reference**, not the object being referenced.

That's why the default clone is shallow.

If you want deep copy,

you must override `clone()` and explicitly clone nested objects too.

Example:

```java
@Override
public Employee clone() throws CloneNotSupportedException {

    Employee copy = (Employee) super.clone();

    copy.address = new Address(this.address.getCity());

    return copy;
}
```

Now the clone becomes a **deep copy**.

---

# Why don't we use clone() much nowadays?

Because it has several design issues:

* Must implement `Cloneable`.
* `CloneNotSupportedException`.
* Default behaviour is shallow, which surprises many developers.
* Complex for large object graphs.

Modern Java usually prefers:

```java
Employee copy = new Employee(original);
```

(copy constructor)

or

```java
Employee copy =
    Employee.builder(original).build();
```

or serialization/mapping libraries for deep copies.

---

# Interview Summary ⭐⭐⭐⭐⭐

```text
Need another independent object?
            │
            ▼
        Copy Object
            │
            ▼
       clone() (one way)
            │
            ▼
   Default = Shallow Copy
            │
            ▼
References Shared
            │
            ▼
Need complete independence?
            │
            ▼
Create Deep Copy
```

## One-liner to remember

| Term             | Meaning                                                 |
| ---------------- | ------------------------------------------------------- |
| **clone()**      | A mechanism to copy an object.                          |
| **Shallow Copy** | New parent object, nested objects are shared.           |
| **Deep Copy**    | New parent object and new copies of all nested objects. |

### Interview question

> **Does `clone()` always create a shallow copy?**

**Answer:** The default implementation of `Object.clone()` performs a shallow copy. However, a class can override `clone()` and explicitly clone its referenced objects to achieve a deep copy.


## Q1. Explain the contract between equals() and hashCode(). What happens if you violate it?

### 1. Why This Concept Matters
This is the #1 most frequently failed Java interview question. Violating the equals/hashCode contract causes HashMap to lose entries, HashSet to allow duplicates, and production bugs that are extremely hard to diagnose. Interviewers ask this to test your understanding of **hash-based collection internals**.

### 2. Basic Meaning

**The Contract** (from `Object` JavaDoc):
1. If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` **MUST** be true
2. If `a.hashCode() == b.hashCode()`, `a.equals(b)` MAY be true or false (hash collisions are allowed)
3. The reverse is NOT required: different hashCodes don't guarantee inequality

**Analogy**: Library books:
- **hashCode()** = Book's shelf number. Books with the same shelf number are in the same section
- **equals()** = Checking if two books are the exact same title/author
- Two identical books MUST have the same shelf number (contract)
- Two books on the same shelf may or may not be identical (collisions allowed)

### 3. Real Code / Real Example

```java
// =====================================================
// ❌ VIOLATION: equals() overridden, hashCode() NOT
// =====================================================
class BadEmployee {
    private String name;
    private int id;
    
    public BadEmployee(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
    // Only equals() overridden — NOT hashCode()!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BadEmployee that = (BadEmployee) o;
        return id == that.id && Objects.equals(name, that.name);
    }
    
    // ❌ hashCode() NOT overridden — inherits Object.hashCode()
    // Object.hashCode() returns memory address based hash
}

// =====================================================
// Testing the violation
// =====================================================
public class TestBadEmployee {
    public static void main(String[] args) {
        Map<BadEmployee, String> map = new HashMap<>();
        
        BadEmployee emp1 = new BadEmployee("Alice", 1);
        BadEmployee emp2 = new BadEmployee("Alice", 1);  // Logically equal to emp1
        
        map.put(emp1, "Engineering");
        
        // What does this return?
        String result = map.get(emp2);
        System.out.println(result);  // null !!!!!!
        
        // Why? emp1.hashCode() != emp2.hashCode()
        // They go to DIFFERENT buckets in HashMap!
        // HashMap checks hash FIRST before calling equals()
        
        System.out.println("emp1.hashCode() = " + emp1.hashCode());  // e.g., 1829164700
        System.out.println("emp2.hashCode() = " + emp2.hashCode());  // e.g., 2018699554
        // Different! So map.get(emp2) looks in the WRONG bucket
    }
}
```

**Expected output:**
```
null
emp1.hashCode() = 1829164700
emp2.hashCode() = 2018699554
```

### 4. What Happens Internally

**HashMap.get() step-by-step:**

```
map.put(emp1, "Engineering"):

Step 1: emp1.hashCode() = 1829164700
Step 2: hash = 1829164700 ^ (1829164700 >>> 16) = 1829164700 ^ 27915 = ...
Step 3: index = (16-1) & hash = 15 & hash = 12
Step 4: table[12] = Node{hash=..., key=emp1, value="Engineering"}


map.get(emp2):

Step 1: emp2.hashCode() = 2018699554  ← DIFFERENT from emp1!
Step 2: hash = 2018699554 ^ (2018699554 >>> 16)
Step 3: index = 15 & hash = 3  ← DIFFERENT from emp1! (went to bucket 3, not 12)
Step 4: table[3] is null → return null ← BUG!


WITH hashCode() OVERRIDDEN:

emp1.hashCode() = Objects.hash("Alice", 1) = 2091690... (same inputs = same hash)
emp2.hashCode() = Objects.hash("Alice", 1) = 2091690... (SAME!)

Step 1: Both give SAME hash
Step 2: SAME bucket index
Step 3: Navigate to bucket, find emp1's node
Step 4: Compare hash matches → then equals() → then return value "Engineering"
```

### 5. Tricky Interview Cases

**Case 1: Mutable fields used in hashCode()**
```java
class Employee {
    private String name;
    private int id;
    
    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, id);  // Uses mutable field 'name'
    }
    
    public void setName(String name) { this.name = name; }
}

Employee emp = new Employee("Alice", 1);
Map<Employee, String> map = new HashMap<>();
map.put(emp, "Engineering");

emp.setName("Bob");  // MUTATE the key after putting in map!

// Now try to retrieve:
System.out.println(map.get(emp));  // null! hashCode() CHANGED after put!
// HashMap stored it at bucket for "Alice"'s hash
// Now emp's hash = hash of "Bob" — different bucket → can't find it!
```

**Case 2: hashCode() returns a constant**
```java
// This is LEGAL (contract satisfied) but HORRIBLE for performance
@Override
public int hashCode() {
    return 42;  // All objects go to bucket 42 → O(n) HashMap!
}
// Contract: equals true → same hashCode? YES (same 42)
// But: ALL objects collide → HashMap becomes a linked list!
// Java 7: O(n) for all operations
// Java 8+: O(log n) after bucket has 8+ entries (treeification)
```

**Case 3: equals() with getClass() vs instanceof**
```java
class Animal {
    private String name;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Using getClass() — strict type check
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(name, animal.name);
    }
}

class Dog extends Animal {
    private String breed;
}

Animal a = new Animal(); a.name = "Max";
Dog d = new Dog(); d.name = "Max";

a.equals(d);  // false! Different class (Animal vs Dog) — even though same name!
// getClass() check prevents cross-type equality

// If we used instanceof instead:
// return o instanceof Animal && Objects.equals(name, ((Animal) o).name);
// a.equals(d) → true (Dog IS-A Animal)
```

**Case 4: equals() with null fields**
```java
class Person {
    private String name;  // may be null
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        
        // ❌ WRONG: name.equals(person.name) — NPE if name is null!
        // ✅ CORRECT: Objects.equals(name, person.name)
        return Objects.equals(name, person.name);
    }
}

Person p1 = new Person();  // name = null
Person p2 = new Person();  // name = null
System.out.println(p1.equals(p2));  // true (Objects.equals(null, null) = true)
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Override equals() but not hashCode() | HashMap/HashSet break — can't find objects | Always override both together |
| Use mutable fields in hashCode() | Hash changes after object is stored in collection | Use only immutable fields (id, createdDate) |
| Use `instanceof` in equals() without symmetry check | `x.equals(y) != y.equals(x)` violates symmetry | Use `getClass()` for strict equality or check `o instanceof ThisClass && this.getClass() == o.getClass()` |
| Forget null check in equals() | NullPointerException on `obj.equals(null)` | Use `Objects.equals()` for field comparison |
| hashCode() only uses a few fields | Many collisions → HashMap degrades | Use `Objects.hash(field1, field2, ...)` or 31 * result + field |

### 7. Production Usage

**Lombok for equals/hashCode:**
```java
@Entity
@Table(name = "users")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // Only include specified fields
public class User {
    @Id
    @EqualsAndHashCode.Include  // Only ID for equality
    private Long id;
    
    @EqualsAndHashCode.Include  // Include username too
    private String username;
    
    private String email;       // NOT included — changed by user, would break collections
    
    @CreationTimestamp
    private LocalDateTime createdAt;  // NOT included
}
```

**Hibernate entity best practice:**
```java
@Entity
public class OrderEntity {
    @Id
    private Long id;
    
    // Never include: version, timestamps, collections (lazy loading)
    // Always include: business key (natural ID)
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return id != null && Objects.equals(id, that.id);  // Use business key
        // NOT: return Objects.equals(id, that.id) — because null id means "not persisted"
        // Hibernate generates new id for transient entities
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);  // Only immutable field
    }
}
```

### 8. Advanced Details

**Performance: Objects.hash() vs manual**
```java
// Objects.hash() creates an array (varargs overhead)
@Override
public int hashCode() {
    return Objects.hash(name, id);  // Array allocated, boxing for primitives
}

// Manual implementation — faster (no allocation, no boxing)
@Override
public int hashCode() {
    int result = 1;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + id;  // int is not boxed
    return result;
}
// Performance: manual ~2-3x faster than Objects.hash()
// Why 31? It's prime, so multiplication distributes hash values evenly.
// 31 * i = (i << 5) - i — JIT optimizes to bit shift + subtract
```

**Record class (Java 16+) — automatically correct:**
```java
// Records automatically generate equals/hashCode based on ALL components
// No violations possible!
public record EmployeeRecord(String name, int id) {}

EmployeeRecord r1 = new EmployeeRecord("Alice", 1);
EmployeeRecord r2 = new EmployeeRecord("Alice", 1);

r1.equals(r2);  // true — correct, auto-generated
r1.hashCode();  // Same as r2.hashCode() — correct, auto-generated

// Immutable by default — no mutation risk
// Perfect for Map keys!
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: Why must we override hashCode() when we override equals()?

**A**: Collections like HashMap and HashSet use hashCode() to find the correct bucket. If two objects are equal (equals() returns true), they MUST be in the same bucket. If they have different hashCodes, they go to different buckets, and HashMap will never find the second object — even though it's logically equal. The contract guarantees: equal objects → equal hashCodes. Always override both.

#### Intermediate

**Q**: What is a good hashCode() implementation?

**A**: A good hashCode() is: (1) **Consistent** — same inputs always give same hash; (2) **Efficient** — fast to compute, uses only necessary fields; (3) **Uniform** — distributes objects evenly across buckets. The standard approach: `int result = 1; result = 31 * result + field1.hashCode(); result = 31 * result + field2.hashCode(); return result;` Or use `Objects.hash(field1, field2)` for simplicity (slightly slower). Never use mutable fields or fields that can be null without null check.

#### Senior

**Q**: How would you handle equals/hashCode for JPA entities?

**A**: For JPA entities, never include: (1) auto-generated IDs in hashCode (null before persist); (2) version fields; (3) lazy-loaded collections (trigger DB call in hashCode!). I use a **business key** — a natural unique identifier (like order number or username) — or the database-generated ID but handle the null case: `if (id == null) return super.hashCode()`. For equals, I use `id != null && Objects.equals(id, that.id)` — if both are null (both new), they are NOT equal. Best practice: use Lombok's `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` with explicit includes.

#### Tricky

**Q**: Two objects have the same hashCode() but equals() returns false. Is this a bug?

**A**: No, this is a **hash collision** — it's expected and allowed by the contract. HashMap handles it by storing multiple entries in the same bucket (linked list in Java 7, linked list→tree in Java 8+). However, too many collisions degrades performance. If all objects have hashCode = 42, HashMap becomes O(n) in Java 7 or O(log n) in Java 8+. A good hashCode() minimizes collisions; it doesn't eliminate them (impossible with finite buckets and infinite possible objects).

### 10. Final 30-Second Answer

equals() and hashCode() have a mutual contract: equal objects must have equal hashCodes. Violating this breaks all hash-based collections. Use immutable fields only. Lombok or Records handle this correctly. Never include mutable or lazily-loaded fields. Always override both or neither.

---

## Q2. Explain clone(), shallow vs deep copy, and why finalize() is deprecated.

### 1. Why This Concept Matters
Interviews test cloning to assess your understanding of **object copying strategies** — a common production need for defensive copies. finalize() is a historical trap: it seems useful (clean up before GC) but causes real production disasters (memory leaks, thread issues). Knowing why it was deprecated shows you understand JVM internals beyond surface level.

### 2. Basic Meaning

**Clone**: Create a copy of an object.

| Copy Type | What's Copied | Behavior |
|-----------|--------------|----------|
| **Shallow** | Primitive fields get NEW values. Reference fields point to SAME objects | Changes to nested objects affect BOTH copies |
| **Deep** | Everything gets NEW copies — primitives AND referenced objects | Changes to nested objects affect ONLY one copy |

**finalize()**: A method that JVM calls before reclaiming an object's memory. Deprecated since Java 9 because: unpredictable, performance-killing, and there are better alternatives.

### 3. Real Code / Real Example

```java
// =====================================================
// SHALLOW vs DEEP COPY
// =====================================================

class Address {
    String city;
    Address(String city) { this.city = city; }
    
    // No clone() — we'll see why shallow copy fails
}

class Person {
    String name;
    int age;
    Address address;
    
    Person(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.address = new Address(city);
    }
}

public class CopyDemo {
    public static void main(String[] args) {
        Person original = new Person("Alice", 30, "New York");
        
        // ❌ Shallow copy: just copy reference
        Person shallowCopy = original;  // Same object! Not a copy at all!
        
        // ✅ Manual shallow copy: copy fields
        Person manualCopy = new Person(original.name, original.age, original.address.city);
        // But: address is STILL the same object if we do:
        // manualCopy.address = original.address;  // WRONG!
        // We created a NEW Address, so this works here.
        
        // Demonstrate: modify shallow copy's address
        manualCopy.address.city = "Boston";
        System.out.println("Original city: " + original.address.city);
        // "Boston" — because original and copy SHARE the same Address object!
        // This is a SHALLOW copy problem.
        
        // =====================================================
        // PROPER DEEP COPY with Cloneable
        // =====================================================
    }
}

// Proper clone() implementation:
class DeepAddress implements Cloneable {
    String city;
    DeepAddress(String city) { this.city = city; }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();  // Shallow clone — but Address has only String (immutable!)
        // String is immutable, so sharing is safe
        // If we had mutable fields, we'd need to clone them too
    }
}

class DeepPerson implements Cloneable {
    String name;
    int age;
    DeepAddress address;
    
    DeepPerson(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.address = new DeepAddress(city);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        DeepPerson cloned = (DeepPerson) super.clone();  // Shallow clone first
        cloned.address = (DeepAddress) address.clone();   // Deep clone mutable fields
        // String name is immutable — no need to clone (safe to share)
        return cloned;
    }
}
```

```java
// =====================================================
// finalize() — WHY DEPRECATED
// =====================================================
class ResourceHolder {
    private FileInputStream stream;
    
    public ResourceHolder(String file) throws FileNotFoundException {
        this.stream = new FileInputStream(file);
    }
    
    // ❌ NEVER use finalize() — it's deprecated since Java 9
    @Override
    @Deprecated(since = "9")
    protected void finalize() throws Throwable {
        try {
            stream.close();  // Might never be called!
        } finally {
            super.finalize();
        }
    }
    // PROBLEMS:
    // 1. finalize() might NEVER be called (GC might not run)
    // 2. Even if called, it's UNPREDICTABLE when
    // 3. Objects with finalize() survive first GC cycle (special queue)
    // 4. If finalize() throws, resources are NEVER released
    // 5. Performs BADLY with many objects
}

// ✅ CORRECT: Use try-with-resources (AutoCloseable)
class ProperResource implements AutoCloseable {
    private FileInputStream stream;
    
    public ProperResource(String file) throws FileNotFoundException {
        this.stream = new FileInputStream(file);
    }
    
    @Override
    public void close() throws IOException {
        // Guaranteed to be called when try-with-resources exits
        if (stream != null) {
            stream.close();
        }
    }
}

// Usage — resources are ALWAYS closed:
try (ProperResource res = new ProperResource("data.txt")) {
    // Use the resource
}  // close() is called AUTOMATICALLY here, even if exception occurs
```

### 4. What Happens Internally

**Object.clone() internals:**
```
Object.clone() (native method):
1. Allocates memory: size = original.getClass().size()
2. Copies ALL bits from original to new location (bitwise copy)
3. Returns the new object

For primitives: exact copy (int 42 → int 42) — correct
For references: copies the POINTER, not the object — SHALLOW

Result: original.field == clone.field → true (same object)
```

**Why super.clone() works with Cloneable:**
```java
// Cloneable is a MARKER interface — no methods!
// It doesn't require implementing clone()!
// It just tells Object.clone() to NOT throw CloneNotSupportedException

// If a class doesn't implement Cloneable:
Object o = someObject.clone();
// Throws CloneNotSupportedException — JVM checks internally

// If a class implements Cloneable:
Object o = someObject.clone();
// Works — JVM creates field-by-field copy
```

**Finalize lifecycle:**
```
Normal object:
  Allocated → Used → Unreachable → GC reclaims memory

Object with finalize():
  Allocated → Used → Unreachable → GC puts in finalization queue
    → finalize() called (maybe)
    → If object not resurrected → GC reclaims memory (NEXT cycle!)
    → If object resurrected (stored this in a field) → alive again! (bad idea)

RESURRECTION BUG:
class Resurrector {
    static Resurrector saved;
    
    @Override
    protected void finalize() {
        saved = this;  // Resurrect! Now GC can't collect it
    }
}
```

### 5. Tricky Interview Cases

**Case 1: super.clone() returns Object — why cast?**
```java
@Override
public Object clone() {
    return super.clone();  // Returns Object, not MyClass
    // Covariant return types (Java 5+):
    // public MyClass clone() { return (MyClass) super.clone(); }
}

// Without covariant return type:
Object cloned = obj.clone();  // Need cast
MyClass cloned = (MyClass) obj.clone();  // Must cast

// With covariant return type:
MyClass cloned = obj.clone();  // No cast needed!
```

**Case 2: finalize() resurrection (don't do this)**
```java
class Leaker {
    private static Leaker INSTANCE;
    
    @Override
    protected void finalize() {
        System.out.println("Finalize called!");
        INSTANCE = this;  // Makes this reachable again!
    }
}

Leaker l = new Leaker();
l = null;  // Unreachable
System.gc();  // Hints GC
// Output: "Finalize called!" — but object is NOT collected!
// INSTANCE holds a reference now — object lives forever!
```

**Case 3: Cloneable and final fields**
```java
class Person {
    private final String name;  // final field
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();  // Allowed even with final fields!
        // Native clone() bypasses constructors and final restrictions
    }
    // BUT: if we return a Person, someone could modify the cloned name? NO
    // String is immutable. But if field is final List, we can't reassign
}
```

**Case 4: Arrays use clone() for efficient copying**
```java
int[] original = {1, 2, 3, 4, 5};
int[] clone = original.clone();  // Faster than Arrays.copyOf() for full copy

clone[0] = 99;
System.out.println(original[0]);  // 1 — independent copy!
// Arrays have public clone() method that returns proper type
// int[].clone() returns int[] — no cast needed
```

### 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Using `finalize()` for resource cleanup | Resource may never be released | Use `try-with-resources` or `Cleaner` class |
| Assuming `super.clone()` gives deep copy | Nested objects are shared | Override clone() to deep-copy mutable fields |
| Not implementing Cloneable | CloneNotSupportedException at runtime | Implement Cloneable marker interface |
| Forgetting to call `super.clone()` in clone() | Returns null or wrong type | Always start with `super.clone()` then customize |
| Making defensive copies with `=` instead of clone() | Security: caller can mutate internal state | Use copy constructor or clone() for arrays, Collections.unmodifiableList for collections |

### 7. Production Usage

**Defensive copying pattern:**
```java
public final class ImmutablePerson {
    private final String name;
    private final Date birthDate;  // Date is MUTABLE!
    
    public ImmutablePerson(String name, Date birthDate) {
        this.name = name;
        // Defensive copy: don't trust caller's reference
        this.birthDate = new Date(birthDate.getTime());
        // If we did: this.birthDate = birthDate;
        // Caller could do: person.getBirthDate().setTime(0);
    }
    
    public Date getBirthDate() {
        // Defensive copy on getter too!
        return new Date(birthDate.getTime());
        // NOT: return birthDate; — caller could mutate!
    }
}
```

**Copy constructor vs clone():**
```java
// Modern approach: copy constructor (preferred over clone())
public class Person {
    private final String name;
    private final Address address;
    
    // Copy constructor
    public Person(Person other) {
        this.name = other.name;  // Immutable — safe to share
        this.address = new Address(other.address);  // Deep copy Address
    }
}
// Advantages over clone():
// 1. No CloneNotSupportedException
// 2. No typecast needed
// 3. Can be final — no subclass interference
// 4. Works with final fields
```

### 8. Advanced Details

**Cleaner API (Java 9+ — modern replacement for finalize):**
```java
// Java Cleaner — more predictable than finalize()
public class DatabaseConnection implements AutoCloseable {
    private static final Cleaner CLEANER = Cleaner.create();
    private final Cleaner.Cleanable cleanable;
    private final Connection connection;
    
    public DatabaseConnection(String url) {
        this.connection = DriverManager.getConnection(url);
        // Register cleanup action
        this.cleanable = CLEANER.register(this, () -> {
            System.out.println("Cleaning up connection: " + url);
            // Close connection — called when object becomes phantom-reachable
        });
    }
    
    @Override
    public void close() {
        cleanable.clean();  // Explicit cleanup
        connection.close();
    }
}
```

**Performance of clone() vs copy constructor:**
```
clone(): ~5ns per object (native memory copy)
copy constructor: ~10-20ns per object (field-by-field + deep copy)
object streams (serialization): ~1000+ns per object (very slow)

For large arrays: clone() is significantly faster than
copying element-by-element in a loop.
```

### 9. Interview Questions And Answers

#### Beginner

**Q**: What is the difference between shallow copy and deep copy?

**A**: Shallow copy copies primitive field values and reference addresses — so original and copy share nested objects. Deep copy creates entirely new objects for all referenced fields — so modifications to nested objects don't affect the copy. Example: shallow-copying a `Person` with an `Address` field means both Person objects point to the same Address. Changing Address.city affects both.

#### Intermediate

**Q**: Why was finalize() deprecated?

**A**: finalize() is unpredictable — you don't know when or even if it will run. Objects with finalize() survive at least one GC cycle (moved to finalization queue), causing memory pressure. If finalize() throws an exception, resources leak. If it resurrects the object, GC never collects it. Alternatives: `try-with-resources` (AutoCloseable) for guaranteed cleanup, `Cleaner` (Java 9+) for native resource cleanup when AutoCloseable isn't practical.

#### Senior

**Q**: How would you make a class safely cloneable?

**A**: I generally **don't use Cloneable** — it's a broken API. Instead, I use a **copy constructor** or a **static factory** method. If I must use Cloneable: (1) Implement the marker interface; (2) Override clone() to call `super.clone()`; (3) Deep-clone all mutable reference fields; (4) Make the method `public` with covariant return type; (5) Don't call any overridable methods from clone(). For modern Java, I prefer copy constructors: `new Person(existing)` which are type-safe, don't throw checked exceptions, and work with final fields.

#### Tricky

**Q**: What happens if clone() is called on an object that doesn't implement Cloneable?

**A**: The JVM throws `CloneNotSupportedException` at runtime. This is checked by the JVM's native `Object.clone()` method — it checks if the class implements Cloneable before performing the bitwise copy. This is why Cloneable is called a "marker interface with a non-intuitive behavior" — it doesn't declare clone() itself (it's empty), but changes how Object.clone() behaves in the JVM. It's the only marker interface in Java that the JVM treats specially.

### 10. Final 30-Second Answer

Clone gives shallow copies via native memory copy. Deep clone requires manual field-by-field copying. Prefer copy constructors over clone(). finalize() is deprecated — use try-with-resources or Cleaner instead. Arrays clone efficiently with native methods.