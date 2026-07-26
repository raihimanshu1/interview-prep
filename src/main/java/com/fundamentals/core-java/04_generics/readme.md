# Generics — Why Generics? ⭐⭐⭐⭐⭐

This is one of the most fundamental Java topics because **almost every modern Java API uses Generics**:

* Collections (`List<String>`)
* Streams (`Stream<Employee>`)
* Optional (`Optional<User>`)
* CompletableFuture (`CompletableFuture<Result>`)
* Spring (`ResponseEntity<T>`, `JpaRepository<T, ID>`)
* Kafka (`Producer<K, V>`, `Consumer<K, V>`)

Without Generics, Java would rely heavily on casting, making code less safe and more error-prone.

---

# 1. What are Generics?

## Interview Definition

> Generics allow classes, interfaces and methods to work with different data types while providing compile-time type safety.

Instead of writing separate classes for every type:

```java
IntegerBox
StringBox
EmployeeBox
```

We write one generic class:

```java
Box<T>
```

where **T** represents a type that will be specified later.

---

# 2. Why Were Generics Introduced?

Generics were introduced in **Java 5 (JDK 1.5)**.

Before Java 5, collections stored everything as `Object`.

Example:

```java
List list = new ArrayList();

list.add("Java");
list.add(100);
list.add(new Employee());
```

Everything was allowed because every class extends `Object`.

---

Reading values:

```java
String language = (String) list.get(0);
```

Notice the explicit cast.

Every retrieval required casting.

---

# 3. Problems Before Generics ⭐⭐⭐⭐⭐

## Problem 1 — No Compile-Time Type Safety

Example:

```java
List list = new ArrayList();

list.add("Java");
list.add(100);
```

Both are accepted.

Later:

```java
String value = (String) list.get(1);
```

Runtime:

```text
ClassCastException
```

Compiler couldn't detect the mistake.

---

## Problem 2 — Excessive Casting

Example:

```java
Employee employee =
(Employee) employeeList.get(0);
```

Every read required casting.

Large applications became filled with unnecessary casts.

---

## Problem 3 — Runtime Errors Instead of Compile-Time Errors

Without Generics:

```text
Compile
    ✓

Run
    ✗ ClassCastException
```

With Generics:

```text
Compile
    ✗ Error

Run
    ✓ Safe
```

Finding mistakes during compilation is much cheaper than discovering them in production.

---

# 4. Solution — Generics ⭐⭐⭐⭐⭐

Instead of:

```java
List list = new ArrayList();
```

Use:

```java
List<String> list = new ArrayList<>();
```

Now:

```java
list.add("Java");
```

Valid.

But:

```java
list.add(100);
```

Compiler error:

```text
Required:
String

Found:
int
```

The mistake is caught immediately.

---

# 5. Type Safety ⭐⭐⭐⭐⭐

Generics provide **compile-time type checking**.

Example:

```java
List<Integer> numbers = new ArrayList<>();

numbers.add(10);
numbers.add(20);
```

Compiler guarantees:

Every element is an Integer.

When reading:

```java
Integer number = numbers.get(0);
```

No cast required.

---

# 6. No Explicit Casting ⭐⭐⭐⭐⭐

Without Generics:

```java
Object object = list.get(0);

String value = (String) object;
```

With Generics:

```java
String value = list.get(0);
```

Compiler already knows the type.

Cleaner and safer.

---

# 7. Improved Readability

Compare:

Without Generics:

```java
Map cache = new HashMap();
```

Questions:

* What is the key?
* What is the value?

Unknown.

---

With Generics:

```java
Map<String, Employee> cache =
new HashMap<>();
```

Immediately clear:

* Key → String
* Value → Employee

The type itself documents the code.

---

# 8. Reusability ⭐⭐⭐⭐⭐

Instead of creating many classes:

```java
IntegerBox

StringBox

EmployeeBox
```

Create one:

```java
Box<T>
```

Use it with different types:

```java
Box<String>

Box<Integer>

Box<Employee>
```

Same implementation.

Different type parameter.

---

# 9. Real Production Examples

Collections:

```java
List<Employee>

Set<String>

Map<Long, User>
```

---

Spring Data:

```java
JpaRepository<Employee, Long>
```

---

Optional:

```java
Optional<User>
```

---

CompletableFuture:

```java
CompletableFuture<Order>
```

---

Kafka:

```java
Producer<String, Employee>

Consumer<String, Employee>
```

---

# 10. How the Compiler Thinks

Example:

```java
List<String> names =
new ArrayList<>();
```

Compiler understands:

```text
List
        |
        |
Every element

must be String
```

So:

```java
names.add("Java");
```

✓

But:

```java
names.add(10);
```

✗ Compile-time error.

---

# 11. Compile-Time vs Runtime Safety ⭐⭐⭐⭐⭐

Without Generics:

```text
Store Wrong Type
        |
        v
Compiler Allows
        |
        v
Runtime Exception
```

---

With Generics:

```text
Store Wrong Type
        |
        v
Compiler Rejects
        |
        v
Application Never Runs
```

This is one of the biggest advantages of Generics.

---

# 12. Are Generics Only for Collections?

No.

Generics can be used with:

* Classes
* Interfaces
* Methods
* Constructors

Examples:

```java
class Box<T>
```

```java
interface Repository<T>
```

```java
<T> void print(T value)
```

We'll cover these next.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Why were Generics introduced?

To provide:

* Compile-time type safety
* Eliminate explicit casting
* Improve readability
* Enable reusable code

---

## Q2. What problem do Generics solve?

They prevent runtime `ClassCastException` by detecting incompatible types during compilation.

---

## Q3. Are Generics available at runtime?

No.

Java Generics exist only during compilation.

At runtime, type information is removed through **Type Erasure** (covered later).

---

## Q4. Can primitive types be used with Generics?

No.

This is invalid:

```java
List<int>
```

Use wrapper classes:

```java
List<Integer>
```

because Generics work only with reference types.

---

## Q5. Do Generics improve performance?

Not directly.

Their primary purpose is:

* Type safety
* Better readability
* Fewer runtime errors

Performance is essentially the same because of **Type Erasure**.

---

# Interview Boundary

For a **7+ years Java developer**, you should know deeply:

* Why Generics were introduced
* Problems before Java 5
* Compile-time type safety
* Elimination of casting
* Collections and framework usage
* Why primitive types are not allowed

Deep Dive if Needed:

* Generic implementation inside the compiler (covered under **Type Erasure**).

---

## Next Topic

**Generic Classes & Generic Methods ⭐⭐⭐⭐⭐**

We'll build the foundation for bounded types, wildcards, PECS and type erasure.

# Generic Classes & Generic Methods ⭐⭐⭐⭐⭐

This is the foundation of the entire Generics module.

Once you understand these two concepts, topics like **Wildcards, PECS, Type Erasure, Collections, Streams, Spring Repositories, Kafka APIs** become much easier.

---

# 1. What is a Generic Class?

## Interview Definition

> A Generic Class is a class that can work with different data types while maintaining compile-time type safety.

Instead of creating multiple classes:

```java
StringBox
IntegerBox
EmployeeBox
```

We create one generic class:

```java
Box<T>
```

where `T` represents a type that will be specified when the object is created.

---

# 2. Generic Class Syntax ⭐⭐⭐⭐⭐

```java
class Box<T> {

    private T value;

}
```

Here:

```java
<T>
```

is called a **Type Parameter**.

Think of it as a placeholder.

It is replaced by an actual type during object creation.

---

# 3. First Generic Class

Example:

```java
class Box<T> {

    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
```

Usage:

```java
Box<String> box = new Box<>();

box.set("Java");

String value = box.get();
```

Compiler knows:

```text
T = String
```

So internally it behaves like:

```java
class Box {

    private String value;

    public void set(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}
```

*(Conceptually. Actual implementation uses Type Erasure, which we'll cover later.)*

---

# 4. Same Class, Different Types ⭐⭐⭐⭐⭐

```java
Box<Integer> age = new Box<>();

age.set(25);

Integer value = age.get();
```

Now:

```text
T = Integer
```

---

Another example:

```java
Box<Employee> employeeBox = new Box<>();

employeeBox.set(new Employee());
```

Same class.

Different data types.

---

# 5. Compile-Time Type Safety

Example:

```java
Box<String> names = new Box<>();

names.set("Java");
```

Valid.

But:

```java
names.set(100);
```

Compiler error.

Because:

```text
Expected:
String

Found:
int
```

No runtime failure.

---

# 6. Multiple Type Parameters ⭐⭐⭐⭐

A generic class can have multiple type parameters.

Example:

```java
class Pair<K, V> {

    private K key;

    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
```

Usage:

```java
Pair<String, Integer> student =
        new Pair<>("John", 101);
```

Meaning:

```text
K → String

V → Integer
```

---

Real examples:

```java
Map<K, V>

HashMap<K, V>

Map.Entry<K, V>
```

---

# 7. Common Generic Type Names ⭐⭐⭐

These are conventions, not keywords.

| Type Parameter | Meaning |
| -------------- | ------- |
| T              | Type    |
| E              | Element |
| K              | Key     |
| V              | Value   |
| N              | Number  |
| R              | Result  |

Examples:

```java
List<E>

Map<K, V>

Optional<T>
```

---

# 8. Generic Methods ⭐⭐⭐⭐⭐

A class doesn't need to be generic to have a generic method.

Example:

```java
class Printer {

    public <T> void print(T value) {
        System.out.println(value);
    }

}
```

Usage:

```java
Printer printer = new Printer();

printer.print("Java");

printer.print(100);

printer.print(true);
```

Compiler infers:

```text
Call 1

T = String


Call 2

T = Integer


Call 3

T = Boolean
```

One method.

Many data types.

---

# 9. Generic Return Type ⭐⭐⭐⭐

Example:

```java
class Utility {

    public static <T> T getFirst(T[] array) {

        return array[0];

    }

}
```

Usage:

```java
String[] names = {"Java", "Spring"};

String first =
        Utility.getFirst(names);
```

Compiler infers:

```text
T = String
```

---

Another example:

```java
Integer[] numbers = {10, 20, 30};

Integer first =
        Utility.getFirst(numbers);
```

Same method.

Different type.

---

# 10. Generic Class vs Generic Method ⭐⭐⭐⭐⭐

Generic Class:

```java
class Box<T> {

    private T value;

}
```

The entire object uses the same type.

Example:

```java
Box<String> box;
```

Everything inside `box` works with `String`.

---

Generic Method:

```java
class Utility {

    public <T> void print(T value) {

    }

}
```

Only that method is generic.

The class itself is not.

---

Comparison:

| Generic Class                 | Generic Method                            |
| ----------------------------- | ----------------------------------------- |
| Type applies to entire object | Type applies only to one method           |
| `class Box<T>`                | `<T> void print(T value)`                 |
| Object created with one type  | Each method call may use a different type |

---

# 11. Type Inference ⭐⭐⭐⭐

Java usually infers generic types.

Example:

```java
Box<String> box = new Box<>();
```

Instead of:

```java
Box<String> box =
        new Box<String>();
```

Java 7 introduced the **Diamond Operator (`<>`)**.

Compiler infers:

```text
T = String
```

Automatically.

---

# 12. Real Production Examples ⭐⭐⭐⭐⭐

Spring Data:

```java
JpaRepository<Employee, Long>
```

---

Optional:

```java
Optional<User>
```

---

Kafka:

```java
Producer<String, Order>

Consumer<String, Order>
```

---

CompletableFuture:

```java
CompletableFuture<Response>
```

---

Collections:

```java
List<Employee>

Map<Long, User>

Set<String>
```

---

# 13. Common Mistakes

## Using Raw Types

Bad:

```java
Box box = new Box();
```

Compiler warning:

```text
Raw type usage
```

Lose type safety.

Always use:

```java
Box<String> box = new Box<>();
```

---

## Mixing Types

Bad:

```java
List list = new ArrayList();

list.add("Java");

list.add(10);
```

Possible runtime failure.

Prefer:

```java
List<String> list =
        new ArrayList<>();
```

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Difference between Generic Class and Generic Method?

**Generic Class**

* Type parameter belongs to the class.
* All methods use the same type.

**Generic Method**

* Type parameter belongs only to that method.
* Each invocation can use a different type.

---

## Q2. Can a non-generic class have a generic method?

Yes.

Example:

```java
class Utility {

    public <T> void print(T value) {

    }

}
```

---

## Q3. Can a generic class have generic methods?

Yes.

Example:

```java
class Box<T> {

    public <U> void print(U value) {

    }

}
```

Here:

* `T` belongs to the class.
* `U` belongs only to the method.

---

## Q4. What is the Diamond Operator?

```java
new ArrayList<>()
```

Introduced in Java 7 to let the compiler infer generic types and reduce repetition.

---

## Q5. Why avoid raw types?

Because they:

* Remove compile-time type safety.
* Require casting.
* Can lead to `ClassCastException`.

---

# Interview Boundary

For a **7+ years Java developer**, know deeply:

* Generic classes
* Generic methods
* Type parameters
* Multiple type parameters
* Type inference
* Diamond operator
* Raw type problems

**Deep Dive if Needed:**

* Compiler-generated signatures (naturally covered with **Type Erasure** later).

---

# Next Topic ⭐⭐⭐⭐⭐

**Bounded Types (`extends`)**

This is where Generics become truly powerful and leads directly into **Wildcards** and **PECS**, two of the most frequently asked senior Java interview topics.

# Bounded Types (`extends`) ⭐⭐⭐⭐⭐

So far, our generic types could accept **any object**.

Example:

```java
Box<String>

Box<Integer>

Box<Employee>

Box<Car>
```

Sometimes this flexibility is too broad.

Imagine writing a method to calculate the sum of numbers:

```java
sum(List<T> numbers)
```

Should this accept:

```java
List<String>
```

No.

We only want **numeric types**.

This is where **Bounded Types** come into the picture.

---

# 1. What are Bounded Types?

## Interview Definition

> Bounded Types restrict the types that can be used as generic type arguments.

Instead of:

```java
<T>
```

we can write:

```java
<T extends Number>
```

Meaning:

> T must be Number or one of its subclasses.

---

# 2. Why Do We Need Bounded Types? ⭐⭐⭐⭐⭐

Without bounds:

```java
class Calculator<T> {

    T value;

}
```

This allows:

```java
Calculator<String>
Calculator<Employee>
Calculator<Car>
```

But if Calculator performs mathematical operations, these types make no sense.

Bounded types let us express that only numbers are valid.

---

# 3. Upper Bounded Type ⭐⭐⭐⭐⭐

Syntax:

```java
<T extends Number>
```

Example:

```java
class Calculator<T extends Number> {

    private T value;

    public Calculator(T value) {
        this.value = value;
    }

    public double doubleValue() {
        return value.doubleValue();
    }
}
```

Usage:

```java
Calculator<Integer> c1 = new Calculator<>(10);

Calculator<Double> c2 = new Calculator<>(10.5);
```

Valid because:

```text
Integer → Number

Double → Number
```

---

But:

```java
Calculator<String> c =
        new Calculator<>("Java");
```

Compile error.

Because:

```text
String

does NOT extend Number
```

---

# 4. What Does `extends` Mean Here? ⭐⭐⭐⭐⭐

This is a very common interview question.

Many developers confuse it with inheritance.

In Generics:

```java
<T extends Number>
```

means:

> T can be Number **or any subclass of Number**.

It is a **type bound**, not inheritance of the generic class itself.

Example:

```text
             Object
                |
             Number
      /      |      \
 Integer   Double   Long
```

Allowed:

```java
Calculator<Number>

Calculator<Integer>

Calculator<Double>

Calculator<Long>
```

Not allowed:

```java
Calculator<String>
```

---

# 5. Bounded Generic Method ⭐⭐⭐⭐

Bounds are not limited to classes.

Example:

```java
public <T extends Number> void print(T value) {

    System.out.println(value.doubleValue());

}
```

Usage:

```java
print(10);

print(10.5);
```

Compiler infers:

```text
T = Integer

T = Double
```

---

# 6. Multiple Bounds ⭐⭐⭐⭐

Sometimes a type must satisfy multiple conditions.

Syntax:

```java
<T extends Class & Interface1 & Interface2>
```

Example:

```java
class Printer<T extends Number & Comparable<T>> {

}
```

Meaning:

T must:

* extend Number
* implement Comparable

Examples:

```java
Printer<Integer>
Printer<Double>
```

Valid.

---

Important rule:

If a class is present, it must come first.

Correct:

```java
<T extends Number & Comparable<T>>
```

Incorrect:

```java
<T extends Comparable<T> & Number>
```

Compile error.

---

# 7. Why Bounded Types Are Useful ⭐⭐⭐⭐⭐

Without bounds:

```java
class Box<T> {

    T value;

}
```

Compiler doesn't know anything about `T`.

So this fails:

```java
value.doubleValue();
```

Compiler error:

```text
Cannot resolve method doubleValue()
```

---

With bounds:

```java
class Box<T extends Number> {

    T value;

}
```

Compiler knows:

Every `T` is a `Number`.

So:

```java
value.doubleValue();
```

is perfectly valid.

---

# 8. Real Production Examples ⭐⭐⭐⭐

Collections:

```java
Collections.max(...)
```

Internally uses bounded generics because elements must be comparable.

Conceptually:

```java
<T extends Comparable<T>>
```

---

Sorting APIs:

Require objects to implement:

```java
Comparable
```

---

Frameworks:

Many framework APIs use bounded generics to ensure only compatible types are accepted.

---

# 9. Common Mistakes ⭐⭐⭐⭐⭐

## Mistake 1

Thinking `extends` means only subclasses.

Example:

```java
<T extends Number>
```

Also accepts:

```java
Number
```

It means:

> Number **or any subclass**.

---

## Mistake 2

Trying to use primitive types.

Invalid:

```java
Calculator<int>
```

Generics work only with reference types.

Correct:

```java
Calculator<Integer>
```

---

## Mistake 3

Assuming bounds affect runtime.

They don't.

Bounds exist only during compilation.

Runtime behaviour is governed by **Type Erasure**, which we'll cover later.

---

# 10. Difference Between Unbounded and Bounded

| Unbounded                      | Bounded                       |
| ------------------------------ | ----------------------------- |
| `<T>`                          | `<T extends Number>`          |
| Accepts any reference type     | Accepts only Number hierarchy |
| Compiler knows nothing about T | Compiler knows T is a Number  |
| Cannot call Number methods     | Can call Number methods       |

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Why do we use bounded types?

To restrict generic parameters and enable type-specific operations safely.

---

## Q2. What is the difference between `<T>` and `<T extends Number>`?

`<T>`

* Any reference type.

`<T extends Number>`

* Only `Number` or its subclasses.

---

## Q3. Can a bounded type have multiple bounds?

Yes.

Example:

```java
<T extends Number & Comparable<T>>
```

---

## Q4. Why can't we write `<T extends int>`?

Because primitives are not objects.

Use wrapper classes:

```java
Integer
Long
Double
```

---

## Q5. Why is `value.doubleValue()` allowed after adding `extends Number`?

Because the compiler knows every valid `T` is a `Number`, so it can safely call methods defined in `Number`.

---

# Interview Boundary

For a **7+ years Java developer**, know deeply:

* Why bounded types exist
* `<T extends Number>`
* Generic classes vs generic methods with bounds
* Multiple bounds
* Why class must appear before interfaces
* Compile-time restrictions

**Deep Dive if Needed:**

* Recursive bounds such as `<T extends Comparable<T>>` internals (you'll naturally encounter this in Collections).

---

# Next Topic ⭐⭐⭐⭐⭐

**Wildcards (`?`, `? extends`, `? super`)**

This is one of the **most frequently asked Generics interview topics** and is essential for understanding **PECS**, the principle behind many Collection APIs.

# Wildcards (`?`, `? extends`, `? super`) ⭐⭐⭐⭐⭐

This is **the most important Generics topic**.

If there's one Generics topic interviewers love asking, it's this one.

Almost every Java Collection API uses wildcards:

```java
Collections.copy()

Collections.sort()

Collections.max()

Comparator<? super T>

Class<?>

List<? extends Number>
```

Understanding wildcards is also the foundation for **PECS**, which we'll cover next.

---

# 1. Why Do We Need Wildcards?

Suppose we have:

```java
List<Integer> integers =
        List.of(1, 2, 3);

List<Double> doubles =
        List.of(1.2, 2.5);
```

Now we want a method:

```java
print(...)
```

that prints any list.

Without wildcards:

```java
void print(List<Object> list)
```

Looks reasonable.

But:

```java
print(integers);
```

Compiler error.

Why?

Because:

```text
List<Integer>

is NOT a subtype of

List<Object>
```

This surprises many developers.

---

# 2. Why Isn't `List<Integer>` a `List<Object>`? ⭐⭐⭐⭐⭐

Remember:

```text
Integer

extends

Object
```

But generic types are **invariant**.

Meaning:

```text
Integer  -----> Object
      ✓

List<Integer> -----> List<Object>
      ✗
```

Why?

Imagine if Java allowed this:

```java
List<Integer> numbers =
        new ArrayList<>();

List<Object> objects = numbers;

objects.add("Java");
```

Now:

```java
Integer value = numbers.get(0);
```

What should happen?

The list contains:

```text
10

20

"Java"
```

Type safety is broken.

So Java intentionally makes:

```text
List<Integer>

NOT

List<Object>
```

---

# 3. Unbounded Wildcard (`?`) ⭐⭐⭐⭐⭐

Syntax:

```java
List<?>
```

Meaning:

> A list of **some unknown type**.

Example:

```java
public void print(List<?> list) {

    for(Object value : list){

        System.out.println(value);

    }

}
```

Now:

```java
print(List.of(1,2,3));

print(List.of("Java","Spring"));

print(List.of(true,false));
```

All work.

---

## What Can We Do?

Reading:

```java
Object value =
        list.get(0);
```

Allowed.

---

Adding:

```java
list.add("Java");
```

Not allowed.

Compiler error.

Why?

Because compiler doesn't know:

```text
?

could be

String

Integer

Employee

User
```

Adding anything could break type safety.

---

# 4. Upper Bounded Wildcard (`? extends`) ⭐⭐⭐⭐⭐

Syntax:

```java
List<? extends Number>
```

Meaning:

> A list whose element type is **Number or any subclass of Number**.

Example:

```java
public void printNumbers(
        List<? extends Number> list){

    for(Number n : list){

        System.out.println(n);

    }

}
```

Valid:

```java
List<Integer>

List<Double>

List<Float>

List<Long>
```

All accepted.

---

# Reading Is Safe

Example:

```java
Number number =
        list.get(0);
```

Always valid.

Because every element is at least a `Number`.

---

# Writing Is NOT Safe

Example:

```java
list.add(10);
```

Compile error.

Why?

Suppose actual list is:

```java
List<Double>
```

If Java allowed:

```java
list.add(10);
```

You would insert an Integer into a List<Double>.

Impossible.

Therefore:

```text
? extends

Read ✓

Write ✗
```

---

# Memory View

```text
List<? extends Number>

        |

 ------------------------

 |          |          |

Integer   Double     Long
```

Compiler doesn't know which one.

So writing is forbidden.

---

# 5. Lower Bounded Wildcard (`? super`) ⭐⭐⭐⭐⭐

Syntax:

```java
List<? super Integer>
```

Meaning:

> A list whose element type is **Integer or any superclass of Integer**.

Possible types:

```text
Object

Number

Integer
```

---

Example:

```java
public void addNumbers(
        List<? super Integer> list){

    list.add(10);

    list.add(20);

}
```

Valid:

```java
List<Integer>

List<Number>

List<Object>
```

---

# Reading

```java
Object value =
        list.get(0);
```

Only Object is guaranteed.

Compiler cannot promise it's an Integer.

---

# Writing

Safe.

Because every allowed list can store Integer.

```text
? super

Read Object

Write Integer ✓
```

---

# Memory View

```text
Object

   ^

Number

   ^

Integer


List<? super Integer>

can be

Object

Number

Integer
```

---

# 6. Comparing the Three Wildcards ⭐⭐⭐⭐⭐

| Wildcard        | Read   | Write   |
| --------------- | ------ | ------- |
| `<?>`           | Object | No      |
| `<? extends T>` | T      | No      |
| `<? super T>`   | Object | Yes (T) |

This table is worth memorising.

---

# 7. Real Examples ⭐⭐⭐⭐⭐

## Read-only API

```java
double sum(
    List<? extends Number> list)
```

Only reading.

Perfect.

---

## Add API

```java
void addDefaults(
    List<? super Integer> list)
```

Adding Integers.

Perfect.

---

## Print API

```java
void print(
    List<?> list)
```

Works with every list.

---

# 8. Common Mistakes ⭐⭐⭐⭐⭐

## Mistake 1

```java
List<Object>

accepts

List<Integer>
```

False.

Generics are invariant.

---

## Mistake 2

Trying to add into:

```java
List<? extends Number>
```

Compiler rejects.

---

## Mistake 3

Expecting Integer while reading:

```java
List<? super Integer>
```

Compiler only guarantees:

```java
Object
```

---

# 9. Visual Summary ⭐⭐⭐⭐⭐

## `<?>`

```text
Unknown Type

Read → Object

Write → Nothing
```

---

## `? extends Number`

```text
Integer
Double
Long
Float

↓

Read Number

Write Nothing
```

---

## `? super Integer`

```text
Object

↓

Number

↓

Integer

Write Integer

Read Object
```

---

# 10. Why Collections Use Wildcards

Example:

```java
Collections.copy()

Collections.sort()

Collections.max()
```

Need flexibility.

Without wildcards:

```java
List<Integer>

List<Double>

List<Long>
```

would require separate overloads.

Wildcards allow one API to work safely with many related types.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. Why can't we add elements to `List<? extends Number>`?

Because the compiler doesn't know the actual subtype (`Integer`, `Double`, `Long`, etc.), so adding any specific subtype could violate type safety.

---

## Q2. Why can we add to `List<? super Integer>`?

Because every valid target (`Integer`, `Number`, or `Object`) can safely hold an `Integer`.

---

## Q3. What can we read from `List<? super Integer>`?

Only `Object` is guaranteed.

---

## Q4. Difference between `<T extends Number>` and `<? extends Number>`?

This is another favourite interview question.

| `<T extends Number>`                                                                               | `<? extends Number>`                                      |
| -------------------------------------------------------------------------------------------------- | --------------------------------------------------------- |
| Declares a **named type parameter** (`T`)                                                          | Uses an **anonymous wildcard** (`?`)                      |
| `T` can be used multiple times in the method/class, preserving the relationship between parameters | `?` is an unknown type and cannot be referred to later    |
| Use when you need to express "the same type" in multiple places                                    | Use when you only need flexibility for a single parameter |

Example:

```java
public <T extends Number> T first(List<T> list)
```

Here the return type is the **same** type as the list element.

With a wildcard:

```java
public Number first(List<? extends Number> list)
```

The exact subtype is unknown, so only `Number` can be returned.

---

## Q5. Which wildcard is used most in real projects?

All three appear, but `? extends` and `? super` are heavily used throughout the Java Collections Framework and Spring APIs.

---

# Interview Boundary

For a **7+ years Java developer**, know deeply:

* Why `List<Integer>` is **not** `List<Object>`
* `?`
* `? extends`
* `? super`
* Read vs write behaviour
* Invariance
* Real API usage

Deep Dive if Needed:

* Wildcard capture and helper methods (rarely asked outside advanced compiler/JDK interviews).

---

# Next Topic ⭐⭐⭐⭐⭐

**PECS (Producer Extends, Consumer Super)**

This is the golden rule that ties everything together and explains **when to use `extends` and when to use `super`** in real-world API design. It's one of the highest-ROI senior Java interview topics.

# PECS (Producer Extends, Consumer Super) ⭐⭐⭐⭐⭐

This is one of the **most frequently asked Generics interview questions**.

Most developers remember:

> **Producer Extends, Consumer Super**

but cannot explain **why**.

By the end of this chapter, you'll understand the reasoning instead of memorising the rule.

---

# 1. Why Do We Need PECS?

Suppose we want to write a method that calculates the sum of numbers.

Example:

```java
public static double sum(List<Number> numbers) {

    double sum = 0;

    for (Number number : numbers) {
        sum += number.doubleValue();
    }

    return sum;
}
```

Looks correct.

---

Now call it:

```java
List<Integer> integers = List.of(1, 2, 3);

sum(integers);
```

Compiler error.

Why?

Because:

```text
List<Integer>

≠

List<Number>
```

We already learned that Generics are **invariant**.

So we need another approach.

---

# 2. Producer vs Consumer

Before learning PECS, ask one question:

> **What is this collection doing?**

There are only two possibilities:

## Producer

The collection **produces** data.

You read values from it.

Example:

```java
for(Number n : list){

}
```

You're only reading.

---

## Consumer

The collection **consumes** data.

You insert values into it.

Example:

```java
list.add(10);
```

You're writing.

---

Everything in PECS is based on this simple observation.

---

# 3. Producer → Extends ⭐⭐⭐⭐⭐

Suppose a method only reads numbers.

```java
public static double sum(
        List<? extends Number> list) {

    double sum = 0;

    for (Number n : list) {
        sum += n.doubleValue();
    }

    return sum;
}
```

Now we can pass:

```java
List<Integer>

List<Double>

List<Long>

List<Float>
```

Everything works.

Why?

Because we're only reading.

The collection is producing values.

Hence:

```text
Producer

↓

Extends
```

---

Memory:

```text
           Number
          /   |   \
    Integer Double Long

List<? extends Number>
```

Every element is guaranteed to be at least a `Number`.

---

# 4. Why Can't Producer Accept Writes?

Suppose:

```java
List<? extends Number> list;
```

Actual object:

```java
List<Double>
```

If Java allowed:

```java
list.add(10);
```

The result would be:

```text
Double

Double

Integer   ❌
```

Type safety breaks.

Therefore:

```text
Producer

Read ✓

Write ✗
```

---

# 5. Consumer → Super ⭐⭐⭐⭐⭐

Suppose a method only inserts Integers.

```java
public static void addDefaults(
        List<? super Integer> list){

    list.add(10);

    list.add(20);

}
```

Valid:

```java
List<Integer>

List<Number>

List<Object>
```

Because every one of them can store Integer values.

---

Memory:

```text
Object

   ↑

Number

   ↑

Integer

List<? super Integer>
```

---

# 6. Why Consumer Cannot Read Integer?

Suppose:

```java
List<? super Integer> list;
```

Actual object might be:

```java
List<Object>
```

Contents:

```text
"Java"

Employee

10

true
```

Compiler cannot guarantee:

```java
Integer value =
        list.get(0);
```

Maybe first element is:

```text
Employee
```

Therefore:

Only this is safe:

```java
Object value =
        list.get(0);
```

---

# 7. PECS Rule ⭐⭐⭐⭐⭐

The famous rule:

```text
Producer

↓

Extends


Consumer

↓

Super
```

or simply:

```text
PECS

P → Extends

C → Super
```

---

# 8. Real Production Examples ⭐⭐⭐⭐⭐

## Example 1

Collections.copy()

Signature (simplified):

```java
copy(List<? super T> destination,
     List<? extends T> source)
```

Notice:

Source:

```text
Produces values

↓

extends
```

Destination:

```text
Consumes values

↓

super
```

---

Visual:

```text
Source

Integer
Integer
Integer

      |
      | copy
      V

Destination

Number
Number
Number
```

Exactly follows PECS.

---

## Example 2

Finding Maximum

Conceptually:

```java
max(List<? extends T>)
```

Only reading.

Producer.

---

## Example 3

Sorting

Comparator:

```java
Comparator<? super T>
```

Comparator consumes objects to compare them.

Therefore:

```text
Consumer

↓

super
```

---

# 9. Decision Tree ⭐⭐⭐⭐⭐

Whenever designing a generic API, ask:

```text
Will I read values?

        |

      Yes

        |

Use

? extends T



Will I write values?

        |

      Yes

        |

Use

? super T
```

---

If both?

Don't use wildcards.

Use a named type parameter.

Example:

```java
public <T> void swap(
        List<T> list,
        int i,
        int j)
```

Here you're both reading and writing the **same type**.

---

# 10. PECS Cheat Sheet ⭐⭐⭐⭐⭐

| Situation      | Use           |
| -------------- | ------------- |
| Only reading   | `? extends T` |
| Only writing   | `? super T`   |
| Read and write | `<T>`         |
| Unknown type   | `?`           |

This table is worth memorising.

---

# 11. Common Mistakes

## Mistake 1

Using:

```java
List<Number>
```

instead of:

```java
List<? extends Number>
```

This unnecessarily rejects:

```java
List<Integer>

List<Double>
```

---

## Mistake 2

Using:

```java
? extends
```

when inserting elements.

Compiler rejects it.

---

## Mistake 3

Using:

```java
? super
```

when expecting typed reads.

Compiler only guarantees:

```java
Object
```

---

# 12. Real Interview Example ⭐⭐⭐⭐⭐

**Question**

Write a method that copies one list into another.

Correct signature:

```java
public static <T> void copy(
        List<? super T> destination,
        List<? extends T> source)
```

Why?

Source:

* Produces data
* Use `extends`

Destination:

* Consumes data
* Use `super`

This is one of the most common interview examples for PECS.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. What does PECS stand for?

**Producer Extends, Consumer Super.**

---

## Q2. When should you use `? extends`?

When the collection is **only producing (reading)** values.

---

## Q3. When should you use `? super`?

When the collection is **only consuming (writing)** values.

---

## Q4. If a collection is both read from and written to, should you use PECS?

No.

Use a named type parameter.

Example:

```java
public <T> void process(List<T> list)
```

This preserves the relationship between reads and writes.

---

## Q5. Why does `Collections.copy()` use both `extends` and `super`?

Because:

* Source list **produces** elements → `? extends T`
* Destination list **consumes** elements → `? super T`

---

## Q6. How do you remember PECS?

Think about the direction of data flow:

```text
Read from collection
        ↓
Producer
        ↓
? extends T


Write into collection
        ↓
Consumer
        ↓
? super T
```

---

# Interview Boundary

For a **7+ years Java developer**, you should know deeply:

* The reasoning behind PECS (not just the acronym)
* Producer vs Consumer
* `Collections.copy()` example
* Why `Comparator<? super T>` uses `super`
* Decision tree (`extends`, `super`, `<T>`, `?`)

**Deep Dive if Needed:**

* Wildcard capture and helper methods used internally by the JDK compiler.

---

# Next Topic ⭐⭐⭐⭐⭐

**Type Erasure**

This is the most important JVM/compiler concept in Generics and another favourite question for senior Java interviews. It explains why Generics provide compile-time safety but don't exist at runtime.

# Type Erasure ⭐⭐⭐⭐⭐

Type Erasure is the **most important internal concept of Java Generics**.

Almost every senior Java interview eventually asks:

> **"If Generics provide type safety, where is that type information stored at runtime?"**

The answer is:

> **It isn't. Java implements Generics using Type Erasure.**

Understanding this chapter also explains:

* Why `List<String>` and `List<Integer>` are the same at runtime.
* Why `new T()` is not allowed.
* Why `instanceof List<String>` doesn't compile.
* Why Generic Arrays don't work.
* Why Bridge Methods exist.

So many "strange" Java rules suddenly make sense after understanding Type Erasure.

---

# 1. What is Type Erasure?

## Interview Definition

> Type Erasure is the process where the Java compiler removes all generic type information during compilation and replaces type parameters with their bounds (or `Object` if unbounded).

Generics exist only during **compilation**.

After compilation:

```text
Java Source Code
        |
        |
Contains Generics
        |
        |
javac Compiler
        |
        |
Type Erasure
        |
        |
Bytecode
        |
        |
JVM
```

The JVM **never sees**:

```java
List<String>

List<Employee>

Box<Integer>
```

It only sees:

```java
List

Box

Object
```

---

# 2. Why Did Java Choose Type Erasure? ⭐⭐⭐⭐⭐

Generics were introduced in **Java 5**.

At that time, millions of Java applications already existed.

Java designers wanted:

* Add Generics
* Without breaking old applications
* Without changing the JVM

Type Erasure achieved exactly that.

Old code:

```java
List list = new ArrayList();
```

continued to work.

New code:

```java
List<String> list = new ArrayList<>();
```

also worked.

Same JVM.

No compatibility issues.

This is called **backward compatibility**, and it's the primary reason Java chose Type Erasure instead of storing generic type information at runtime.

---

# 3. Example of Type Erasure ⭐⭐⭐⭐⭐

Source code:

```java
class Box<T> {

    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
```

After Type Erasure (conceptually):

```java
class Box {

    private Object value;

    public void set(Object value) {
        this.value = value;
    }

    public Object get() {
        return value;
    }
}
```

Notice:

```text
T

↓

Object
```

The compiler removes the type parameter.

---

# 4. Bounded Types After Erasure

Example:

```java
class Box<T extends Number> {

    T value;

}
```

After Type Erasure:

```java
class Box {

    Number value;

}
```

Rule:

```text
Unbounded Generic

↓

Object



Bounded Generic

↓

Upper Bound
```

Examples:

```java
<T>

↓

Object
```

```java
<T extends Number>

↓

Number
```

```java
<T extends Employee>

↓

Employee
```

---

# 5. Where Did Compile-Time Safety Go?

Consider:

```java
List<String> list =
        new ArrayList<>();

list.add("Java");
```

Compiler checks:

```text
Only String allowed
```

Then Type Erasure happens.

At runtime:

```java
List list =
        new ArrayList();
```

The safety has already been enforced by the compiler.

The JVM no longer needs the generic type.

---

# 6. Compiler Inserts Casts Automatically ⭐⭐⭐⭐⭐

Source code:

```java
List<String> list =
        new ArrayList<>();

String value = list.get(0);
```

Looks like:

No cast.

But after compilation (conceptually):

```java
String value =
        (String) list.get(0);
```

The compiler inserts the cast for you.

That's why Generics eliminate manual casting while remaining compatible with older bytecode.

---

# 7. Runtime View ⭐⭐⭐⭐⭐

Example:

```java
List<String> names =
        new ArrayList<>();

List<Integer> numbers =
        new ArrayList<>();
```

Runtime:

```text
Both become

ArrayList
```

Therefore:

```java
System.out.println(
        names.getClass() ==
        numbers.getClass());
```

Output:

```text
true
```

Both objects have the same runtime class.

---

# 8. Why Can't We Create `new T()`? ⭐⭐⭐⭐⭐

Suppose:

```java
class Box<T> {

    T create() {
        return new T();
    }

}
```

Compiler error.

Why?

Because after Type Erasure:

```java
Object create() {

    return new Object();

}
```

Compiler doesn't know which constructor to call.

There is no runtime information about `T`.

---

# 9. Why Can't We Write `instanceof List<String>`?

Invalid:

```java
if(list instanceof List<String>)
```

Why?

Runtime only knows:

```text
List
```

There is no:

```text
List<String>

List<Integer>
```

at runtime.

Correct:

```java
if(list instanceof List)
```

---

# 10. Why Static Generic Fields Aren't Allowed

Invalid:

```java
class Box<T> {

    static T value;

}
```

Why?

`static` belongs to the class, not an instance.

But different instances may use different type arguments:

```java
Box<String>

Box<Integer>
```

Which type should the single static field have?

Impossible.

Therefore, Java forbids static fields using the class's type parameter.

---

# 11. Reflection and Type Erasure

Example:

```java
List<String> list =
        new ArrayList<>();
```

Reflection:

```java
System.out.println(
        list.getClass());
```

Output:

```text
class java.util.ArrayList
```

Not:

```text
ArrayList<String>
```

The generic parameter has already been erased.

---

# 12. Summary of What Type Erasure Explains ⭐⭐⭐⭐⭐

| Feature                                      | Reason                         |
| -------------------------------------------- | ------------------------------ |
| No `new T()`                                 | Type unknown at runtime        |
| No `instanceof List<String>`                 | Generic type erased            |
| No runtime generic information               | Type erased                    |
| Automatic casts                              | Inserted by compiler           |
| `List<String>` == `List<Integer>` at runtime | Same runtime class             |
| Backward compatibility                       | Old bytecode continues to work |

---

# Visual Flow

```text
Source Code

List<String>

        |

        | Compile

        V

Type Checking

        |

        | Type Erasure

        V

List

        |

        | Runtime

        V

JVM
```

Think of it this way:

* **Compile time:** Generics are enforced.
* **Runtime:** Generics no longer exist.

---

# Common Interview Questions ⭐⭐⭐⭐⭐

---

## Q1. What is Type Erasure?

Type Erasure is the compiler process that removes generic type information from bytecode, replacing type parameters with their upper bound (or `Object` if unbounded).

---

## Q2. Why does Java use Type Erasure?

To maintain **backward compatibility** with code and JVMs that existed before Java 5.

---

## Q3. Does the JVM know about `List<String>`?

No.

The JVM only knows:

```text
List
```

---

## Q4. Why does `new T()` fail?

Because after Type Erasure, the compiler has no runtime information about what `T` actually is, so it cannot instantiate it.

---

## Q5. Why is `instanceof List<String>` illegal?

Because generic type arguments are erased before runtime. Only `instanceof List` is meaningful.

---

## Q6. Why doesn't Generics cause runtime overhead?

Because generic checks happen at **compile time**. The generated bytecode uses ordinary classes with compiler-inserted casts, so there's essentially no additional runtime cost for generic type information.

---

# Interview Boundary

For a **7+ years Java developer**, know deeply:

* What Type Erasure is
* Why Java chose it (backward compatibility)
* `Object` vs upper-bound replacement
* Automatic cast insertion
* Why `new T()` fails
* Why `instanceof List<String>` fails
* Why `List<String>` and `List<Integer>` have the same runtime class

**Deep Dive if Needed:**

* How Type Erasure leads to **Bridge Methods**, **Heap Pollution**, and **Generic Arrays**. Those are direct consequences and are the next topics in this module.

# Bridge Methods, Heap Pollution & Generic Arrays (Quick Coverage)

These are **advanced Generics internals**. They are occasionally asked in **Oracle, Amazon, JDK, JVM-heavy interviews**, but for most **7+ years backend interviews**, a surface-to-medium understanding is sufficient.

---

# 1. Bridge Methods ⭐⭐⭐

## Why Do Bridge Methods Exist?

Bridge methods are **compiler-generated methods** created after **Type Erasure** to preserve **polymorphism**.

You never write them yourself.

The Java compiler automatically generates them.

---

## Example

```java
class Animal {

    Animal get() {
        return new Animal();
    }

}

class Dog extends Animal {

    @Override
    Dog get() {
        return new Dog();
    }

}
```

This is valid because Java supports **Covariant Return Types**.

---

After Type Erasure, method signatures may no longer match correctly.

To maintain runtime polymorphism, the compiler generates something conceptually like:

```java
class Dog extends Animal {

    Dog get() {
        return new Dog();
    }

    // Compiler Generated
    Animal get() {
        return get();
    }

}
```

This extra method is called a **Bridge Method**.

---

## Generic Example

```java
class Parent<T> {

    T get() {
        return null;
    }

}

class Child extends Parent<String> {

    @Override
    String get() {
        return "Java";
    }

}
```

After Type Erasure

```java
class Parent {

    Object get() { ... }

}
```

Child becomes

```java
class Child extends Parent {

    String get() { ... }

    // Compiler generated bridge method
    Object get() {
        return get();
    }

}
```

This ensures overridden methods still behave correctly after erasure.

---

## Interview Questions

### Q. What is a Bridge Method?

A compiler-generated synthetic method created after **Type Erasure** to preserve **runtime polymorphism** and method overriding.

---

### Q. Do developers write Bridge Methods?

No.

The compiler generates them automatically.

---

### Interview Boundary

Know:

* Compiler generated
* Happens because of Type Erasure
* Preserves polymorphism

No need to memorise bytecode.

---

# 2. Heap Pollution ⭐⭐⭐

## Definition

Heap Pollution occurs when a variable of a **parameterized type** refers to an object that is **not of that parameterized type**.

Simply put:

> The compiler believes the collection contains one type, but at runtime it actually contains another.

---

## Example

```java
List<String> names = new ArrayList<>();

List raw = names;      // Raw type

raw.add(100);          // Allowed

String s = names.get(0);
```

Runtime:

```text
ClassCastException
```

Why?

Compiler believed:

```text
List<String>
```

Actual list contains

```text
Integer
```

---

## Another Example

```java
List<String> list = new ArrayList<>();

Object obj = list;

List<Integer> numbers = (List<Integer>) obj;
```

Compiler warns

```text
Unchecked cast
```

Possible runtime failure.

---

## Why Does Heap Pollution Happen?

Main reasons:

* Raw types
* Unchecked casts
* Generic varargs
* Reflection

---

## How to Avoid It

Always use:

```java
List<String>
```

instead of

```java
List
```

Avoid unchecked casts.

Avoid mixing raw and generic types.

---

## Interview Questions

### Q. What is Heap Pollution?

A situation where generic type safety is broken and a parameterized reference points to data of an incompatible type, often leading to `ClassCastException`.

---

### Q. What causes Heap Pollution?

* Raw types
* Unchecked casts
* Varargs with generics
* Reflection

---

### Interview Boundary

Know the concept and one raw type example.

---

# 3. Generic Arrays ⭐⭐⭐

This is one of the most confusing Generics rules.

---

## Why Can't We Create Generic Arrays?

Invalid:

```java
T[] array = new T[10];
```

Compiler error.

---

Also invalid:

```java
new List<String>[10];
```

Compiler error.

---

## Why?

Arrays and Generics behave differently.

### Arrays are Covariant

Meaning

```text
String[]

is-a

Object[]
```

Example

```java
String[] names = new String[5];

Object[] objects = names;

objects[0] = 100;
```

Runtime:

```text
ArrayStoreException
```

Arrays perform **runtime type checking**.

---

### Generics are Invariant

```text
List<String>

is NOT

List<Object>
```

Generics rely on **compile-time checking** due to **Type Erasure**.

---

If generic arrays were allowed:

```java
List<String>[] lists =
        new List<String>[5];
```

Then:

```java
Object[] objects = lists;

objects[0] = List.of(100);
```

Now

```java
String s = lists[0].get(0);
```

Runtime:

```text
ClassCastException
```

Since the JVM has no runtime generic type information (because of Type Erasure), it cannot protect against this.

To prevent this unsafe situation, Java simply **disallows generic array creation**.

---

## Workaround

Instead of arrays, use collections.

```java
List<List<String>> lists =
        new ArrayList<>();
```

Or, if absolutely necessary:

```java
@SuppressWarnings("unchecked")
List<String>[] lists =
        (List<String>[]) new List[10];
```

This compiles but produces an unchecked warning because the compiler cannot verify type safety.

---

## Interview Questions

### Q. Why are generic arrays not allowed?

Because arrays require **runtime type information**, while Generics remove type information through **Type Erasure**. The two models are incompatible.

---

### Q. What should we use instead?

Prefer:

```java
List<List<String>>
```

instead of

```java
List<String>[]
```

---

# Summary Table

| Topic              | Why it Exists                                        | Key Point                                          |
| ------------------ | ---------------------------------------------------- | -------------------------------------------------- |
| **Bridge Methods** | Preserve polymorphism after Type Erasure             | Compiler generates synthetic methods automatically |
| **Heap Pollution** | Generic type safety is violated                      | Usually caused by raw types or unchecked casts     |
| **Generic Arrays** | Arrays need runtime type information; Generics don't | `new T[]` and `new List<String>[]` are not allowed |

---

# Final Interview Boundary (7+ Years)

You are expected to know:

* **Bridge Methods:** Compiler-generated methods created because of Type Erasure to preserve overriding and polymorphism.
* **Heap Pollution:** Understand the concept, common causes (raw types, unchecked casts), and resulting `ClassCastException`.
* **Generic Arrays:** Know why they are disallowed (Type Erasure + array covariance) and that `List<List<T>>` is the preferred alternative.

**Deep JVM implementation details, bytecode inspection, and compiler internals are generally out of scope** unless you're interviewing for JVM, compiler, or JDK development roles.
