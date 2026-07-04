# Python - Quick Refresher

Use this file for fast Python interview recall with clear examples and short production-minded answers.

---

# 1. List vs Tuple

## What Is It?

Both list and tuple store multiple values.

Main difference:

```text
List  -> mutable
Tuple -> immutable
```

---

## In Simple Terms

List can be changed after creation.

Tuple cannot be changed after creation.

---

## Example

```python
items = [1, 2, 3]
items.append(4)

point = (10, 20)
```

This works:

```python
items[0] = 100
```

This fails:

```python
point[0] = 100
```

---

## When To Use

Use list when:

```text
Data changes
Need add/remove/update
Order matters but content is dynamic
```

Use tuple when:

```text
Data should not change
Returning fixed grouped values
Using as dictionary key
Protecting intent
```

---

## Common Mistake

Tuple is immutable, but if it contains a mutable object, that nested object can still change.

```python
data = ([1, 2], "active")
data[0].append(3)
```

The tuple structure did not change, but the list inside it changed.

---

## Interview Answer

> A list is mutable, so we can add, remove, or update values. A tuple is immutable, so its elements cannot be reassigned after creation. Lists are used for dynamic collections, while tuples are useful for fixed data, returning multiple values, and dictionary keys when all elements are hashable. Tuple immutability also makes intent clearer.

---

# 2. Shallow Copy vs Deep Copy

## What Is It?

Copying can happen at two levels:

```text
Shallow copy -> copies outer object only
Deep copy    -> recursively copies nested objects
```

---

## Example

```python
import copy

original = [[1, 2], [3, 4]]

shallow = copy.copy(original)
deep = copy.deepcopy(original)
```

If nested list changes:

```python
original[0].append(99)
```

Then:

```text
shallow sees nested change
deep does not
```

---

## In Simple Terms

Shallow copy creates a new outer container but shares inner objects.

Deep copy creates new inner objects also.

---

## Common Mistake

Using shallow copy for nested data:

```python
new_users = users.copy()
```

If each user has nested preferences, changing preferences in one copy may affect the other.

---

## When To Use

Use shallow copy:

```text
Flat data
Nested sharing is acceptable
Performance matters
```

Use deep copy:

```text
Nested data must be independent
Avoid accidental mutation
```

---

## Interview Answer

> A shallow copy copies only the outer object, so nested objects are still shared. A deep copy recursively copies nested objects too. For flat data, shallow copy is usually enough. For nested mutable objects, deep copy is safer because changes in one copy will not affect the other.

---

# 3. Recursion

## What Is It?

Recursion means a function calls itself to solve a smaller version of the same problem.

---

## Required Parts

Every recursive function needs:

```text
Base case
Recursive case
```

---

## Example

```python
def factorial(n):
    if n == 0:
        return 1

    return n * factorial(n - 1)
```

Flow:

```text
factorial(3)
= 3 * factorial(2)
= 3 * 2 * factorial(1)
= 3 * 2 * 1 * factorial(0)
= 6
```

---

## Common Mistake

Missing base case:

```python
def bad(n):
    return bad(n - 1)
```

This causes:

```text
RecursionError
```

---

## Production Mindset

Recursion is elegant for:

```text
Tree traversal
Graph traversal with visited set
Divide and conquer
Nested structures
```

But for very deep input:

```text
Use iteration to avoid stack overflow
```

---

## Interview Answer

> Recursion is when a function calls itself to solve smaller subproblems. It must have a base case to stop and a recursive case to continue. It is useful for tree, graph, and divide-and-conquer problems. The main risk is too much call depth, which can cause a recursion limit error, so iteration may be safer for very deep inputs.

---

# 4. Generator Expression

## What Is It?

A generator expression creates values lazily.

Syntax:

```python
(expression for item in iterable)
```

---

## Example

```python
squares = (x * x for x in range(5))
```

It does not create all values immediately.

It produces values one by one:

```python
for value in squares:
    print(value)
```

---

## In Simple Terms

List comprehension stores everything in memory.

Generator expression produces one item at a time.

---

## Comparison

```python
list_values = [x * x for x in range(1000000)]
gen_values = (x * x for x in range(1000000))
```

List:

```text
Stores all values
More memory
```

Generator:

```text
Lazy
Memory efficient
Can be consumed once
```

---

## Common Mistake

Generators are consumed after iteration.

```python
gen = (x for x in range(3))

list(gen)  # [0, 1, 2]
list(gen)  # []
```

---

## Interview Answer

> A generator expression uses parentheses and creates values lazily, one at a time. It is memory efficient because it does not store the full result list. It is useful for large data processing pipelines, but it can usually be consumed only once.

---

# 5. Decorators

## What Is It?

A decorator is a function that takes another function and returns a modified function.

---

## In Simple Terms

Decorator adds extra behavior without changing the original function code.

Examples:

```text
Logging
Authentication
Authorization
Timing
Retry
Caching
```

---

## Example

```python
def log_call(func):
    def wrapper(*args, **kwargs):
        print("Before call")
        result = func(*args, **kwargs)
        print("After call")
        return result

    return wrapper

@log_call
def greet(name):
    return f"Hello {name}"
```

This:

```python
@log_call
def greet(name):
    ...
```

is similar to:

```python
greet = log_call(greet)
```

---

## Common Mistake

Not returning the original function result:

```python
def wrapper(*args, **kwargs):
    func(*args, **kwargs)
```

If original function returns something, it gets lost.

---

## Production Mindset

Use decorators for cross-cutting concerns:

```text
Metrics
Tracing
Access checks
Retry
Validation
```

But avoid hiding too much business logic inside decorators.

---

## Interview Answer

> A decorator is a function that takes another function and returns a new function with added behavior. It is commonly used for logging, timing, authentication, caching, and retry logic. The `@decorator` syntax is just a cleaner way of wrapping a function. A good decorator should preserve arguments, return values, and ideally function metadata.

---

# 6. `*args` and `**kwargs`

## What Is It?

`*args` collects variable positional arguments.

`**kwargs` collects variable keyword arguments.

---

## Example

```python
def print_values(*args, **kwargs):
    print(args)
    print(kwargs)

print_values(1, 2, name="John", active=True)
```

Output:

```text
(1, 2)
{'name': 'John', 'active': True}
```

---

## In Simple Terms

`*args` means:

```text
extra unnamed values
```

`**kwargs` means:

```text
extra named values
```

---

## Common Use

```python
def wrapper(*args, **kwargs):
    return original_function(*args, **kwargs)
```

This is common in decorators.

---

## Interview Answer

> `*args` allows a function to accept any number of positional arguments, and `**kwargs` allows any number of keyword arguments. They are useful when writing flexible functions, wrappers, decorators, and APIs where the exact arguments may vary.

---

# 7. Exception Handling Block

## What Is It?

Exception handling is used to handle runtime errors gracefully.

Python uses:

```text
try
except
else
finally
raise
```

---

## Example

```python
try:
    value = int("abc")
except ValueError:
    print("Invalid number")
else:
    print("Conversion successful")
finally:
    print("Always runs")
```

---

## Meaning

```text
try     -> risky code
except  -> handle error
else    -> runs if no exception
finally -> always runs
raise   -> throw exception
```

---

## Common Mistake

Bad:

```python
try:
    process()
except Exception:
    pass
```

This hides bugs.

Better:

```python
try:
    process()
except SpecificError as ex:
    log_error(ex)
    raise
```

---

## Production Mindset

Use exceptions carefully:

```text
Catch specific exceptions
Log useful context
Do not swallow errors silently
Clean up resources in finally
Raise meaningful errors
```

---

## Interview Answer

> Python exception handling uses `try`, `except`, `else`, `finally`, and `raise`. Risky code goes in `try`, specific errors are handled in `except`, `else` runs when no exception occurs, and `finally` runs always. In production, I avoid broad silent catches and prefer specific exceptions, proper logging, cleanup, and re-raising when needed.

---

# 8. Class Method In Python

## What Is It?

A class method is a method bound to the class, not a specific object.

It uses:

```python
@classmethod
```

First parameter is:

```text
cls
```

---

## Example

```python
class User:
    def __init__(self, name):
        self.name = name

    @classmethod
    def from_email(cls, email):
        name = email.split("@")[0]
        return cls(name)
```

Usage:

```python
user = User.from_email("john@example.com")
```

---

## Difference

```text
Instance method -> gets self
Class method    -> gets cls
Static method   -> gets neither self nor cls
```

---

## When To Use

Use class method for:

```text
Alternative constructors
Factory methods
Logic that depends on class, not instance
Subclass-friendly object creation
```

---

## Interview Answer

> A class method is defined using `@classmethod` and receives the class as the first argument, usually named `cls`. It is commonly used for alternative constructors or factory methods. Unlike instance methods, it does not need an existing object. Unlike static methods, it can access the class and works well with inheritance.

---

# 9. `__new__` vs `__init__`

## What Is It?

`__new__` creates the object.

`__init__` initializes the object.

---

## In Simple Terms

Creation happens first:

```text
__new__
```

Then setup happens:

```text
__init__
```

---

## Example

```python
class User:
    def __new__(cls, *args, **kwargs):
        print("Creating instance")
        return super().__new__(cls)

    def __init__(self, name):
        print("Initializing instance")
        self.name = name
```

Usage:

```python
user = User("John")
```

Flow:

```text
__new__ creates object
__init__ sets name
```

---

## When `__new__` Is Used

Usually used for:

```text
Immutable classes
Singleton-like control
Custom object creation
Subclassing built-in immutable types
```

Most normal classes only need `__init__`.

---

## Interview Answer

> `__new__` is responsible for creating and returning a new instance, while `__init__` initializes that instance after it is created. In most classes we only override `__init__`. We override `__new__` when we need control over object creation, especially with immutable types or special construction logic.

---

# 10. Iterator Must Implement Which Methods?

## What Is It?

An iterator is an object that returns values one at a time.

It must implement:

```text
__iter__()
__next__()
```

---

## Example

```python
class Counter:
    def __init__(self, max_value):
        self.current = 0
        self.max_value = max_value

    def __iter__(self):
        return self

    def __next__(self):
        if self.current >= self.max_value:
            raise StopIteration

        self.current += 1
        return self.current
```

Usage:

```python
for value in Counter(3):
    print(value)
```

Output:

```text
1
2
3
```

---

## Important Point

When no more values exist:

```python
raise StopIteration
```

This tells Python iteration is complete.

---

## Interview Answer

> A Python iterator must implement `__iter__()` and `__next__()`. `__iter__()` returns the iterator object, and `__next__()` returns the next value. When there are no more values, `__next__()` raises `StopIteration`.

---

# 11. Why Immutable Objects Are Important For Dictionary Keys

## What Is It?

Dictionary keys must be hashable.

Hashable means:

```text
The object has a stable hash value
```

Immutable objects usually have stable hashes.

---

## In Simple Terms

Dictionary uses a key's hash to find the value.

If the key changes after insertion:

```text
Dictionary may not find it correctly
```

---

## Example

Allowed:

```python
data = {
    ("IN", "Mumbai"): "West Region"
}
```

Not allowed:

```python
data = {
    ["IN", "Mumbai"]: "West Region"
}
```

List is mutable, so it is not hashable.

---

## Common Hashable Keys

Examples:

```text
str
int
float
bool
tuple of immutable values
frozenset
```

---

## Common Mistake

This tuple is not hashable:

```python
key = ([1, 2], "active")
```

Because it contains a list.

---

## Production Mindset

Immutable keys are important for:

```text
Caching
Memoization
Grouping
Lookup maps
Configuration keys
```

Stable keys make lookups predictable.

---

## Interview Answer

> Dictionary keys must be hashable because Python uses the key's hash value to store and find entries efficiently. Immutable objects are preferred because their hash value does not change. If a mutable object like a list were allowed as a key and then changed, dictionary lookup could break. That is why strings, numbers, and tuples of immutable values can be keys, but lists cannot.

---

## Strict Review Fixes: Follow-Up Questions And Senior Details

This section adds the missing production framing, safe vs unsafe cases, and follow-up questions for every Python topic.

### List vs Tuple Follow-Ups

Why it matters:

```text
Choosing mutable vs immutable data affects correctness, readability,
dictionary key usage, and accidental mutation risk.
```

Safe:

```text
Use list for changing collections.
Use tuple for fixed grouped values or hashable keys.
```

Unsafe:

```text
Using list as dictionary key
Mutating shared list unexpectedly
Assuming tuple contents can never change when tuple contains a mutable object
```

Likely follow-up questions:

```text
Can a tuple contain mutable objects?
Why can tuple be a dictionary key?
When would you choose list?
When would you choose tuple?
```

Interview answer:

> Lists are mutable and good for dynamic collections. Tuples are immutable and good for fixed values, multiple return values, and dictionary keys when all elements are hashable. A tuple can still contain a mutable object, so immutability applies to the tuple structure, not necessarily every nested object.

### Shallow Copy vs Deep Copy Follow-Ups

Production example:

```python
users = [{"name": "Asha", "prefs": {"theme": "dark"}}]
copy_users = users.copy()

copy_users[0]["prefs"]["theme"] = "light"
```

The nested preferences object is shared in a shallow copy.

Performance detail:

```text
deepcopy is safer for nested mutation but can be expensive for large objects.
```

Likely follow-up questions:

```text
What does shallow copy copy?
When is deepcopy expensive?
What if nested objects are immutable?
How do you copy a list safely?
```

Interview answer:

> Shallow copy creates a new outer object but shares nested objects. Deep copy recursively copies nested objects too. For flat data, shallow copy is fine. For nested mutable data, deep copy avoids accidental shared mutation, but it can be more expensive.

### Recursion Follow-Ups

Safe use:

```text
Tree traversal
Divide and conquer
Nested structures
Graph traversal with visited set
```

Unsafe use:

```text
Missing base case
Very deep recursion
Graph recursion without visited set
Recursive solution where simple loop is clearer
```

Likely follow-up questions:

```text
What is base case?
What causes RecursionError?
When would you use iteration instead?
Why is visited set needed for graph recursion?
```

Interview answer:

> Recursion solves a problem by calling itself on smaller inputs. It needs a base case and a recursive case. It is useful for trees and divide-and-conquer problems, but deep recursion can hit Python's recursion limit. For very deep or simple linear problems, iteration may be safer.

### Generator Expression Follow-Ups

Why it matters:

```text
Generators save memory because values are produced lazily.
This matters for large files, large ranges, and streaming pipelines.
```

Safe:

```text
Use generator when processing one item at a time.
```

Unsafe:

```text
Expecting generator to be reusable
Trying to index generator directly
Using generator when repeated traversal is required
```

Likely follow-up questions:

```text
Generator expression vs list comprehension?
Can a generator be consumed twice?
Why are generators memory efficient?
How do you get next value manually?
```

Interview answer:

> A generator expression creates values lazily using parentheses. It is memory efficient because it does not build the entire collection at once. The trade-off is that it is usually consumed once and cannot be indexed like a list.

### Decorator Follow-Ups

Important production detail:

```python
from functools import wraps

def log_call(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        return func(*args, **kwargs)
    return wrapper
```

`wraps` preserves function name, docstring, and metadata.

Common mistakes:

```text
Not returning original result
Not forwarding *args/**kwargs
Not using functools.wraps
Hiding too much business logic in decorators
```

Likely follow-up questions:

```text
What does @decorator syntax mean?
Why use functools.wraps?
What are common decorator use cases?
Can decorators take arguments?
```

Interview answer:

> A decorator wraps a function to add behavior like logging, auth, timing, retry, or caching. In production, I use `functools.wraps` so metadata is preserved, pass through `*args` and `**kwargs`, and return the original function result.

### `*args` and `**kwargs` Follow-Ups

Argument order:

```python
def func(positional, *args, keyword=None, **kwargs):
    pass
```

Common use:

```text
Forwarding arguments in wrappers/decorators
Building flexible APIs
Supporting optional keyword configuration
```

Related common mistake:

```python
def bad(items=[]):
    items.append("x")
    return items
```

Use `None` instead of mutable default arguments.

Likely follow-up questions:

```text
What is the difference between args and kwargs?
What is keyword-only argument?
How do decorators forward arguments?
Why are mutable default arguments risky?
```

Interview answer:

> `*args` collects extra positional arguments, while `**kwargs` collects extra keyword arguments. They are useful for flexible functions and decorators. The normal order is positional arguments, `*args`, keyword-only arguments, and `**kwargs`.

### Exception Handling Follow-Ups

Likely follow-up questions:

```text
What is finally used for?
When does else run?
Why avoid broad except?
How do you re-raise an exception?
```

Senior detail:

```text
Catch specific exceptions, log useful context, clean up resources,
and avoid swallowing failures silently.
```

### Class Method Follow-Ups

Common mistakes:

```text
Using classmethod when instance state is needed
Using staticmethod when subclass-aware construction is needed
Hardcoding class name instead of cls
```

Inheritance detail:

```text
cls points to the subclass when called from a subclass,
so classmethod works well for factory constructors.
```

Likely follow-up questions:

```text
classmethod vs staticmethod?
Why does classmethod receive cls?
What is an alternative constructor?
How does classmethod help inheritance?
```

Interview answer:

> A class method receives the class as `cls` and is commonly used for factory or alternative constructors. It is different from a static method because it can access class state and respects inheritance when called from subclasses.

### `__new__` vs `__init__` Follow-Ups

Common mistakes:

```text
Overriding __new__ unnecessarily
Returning wrong object type from __new__
Putting initialization logic in __new__ when __init__ is enough
Using singleton-style __new__ without considering testing/threading
```

Likely follow-up questions:

```text
Which runs first, __new__ or __init__?
Why use __new__ for immutable objects?
What should __new__ return?
Do most classes need __new__?
```

Interview answer:

> `__new__` creates and returns the object, while `__init__` initializes it. Most classes only need `__init__`. I override `__new__` only for special object creation cases, such as immutable types or controlled instance creation.

### Iterator Follow-Ups

Iterable vs iterator:

```text
Iterable -> can return an iterator using __iter__()
Iterator -> has __iter__() and __next__()
```

Production mindset:

```text
Iterators are memory efficient because they produce values one at a time.
Generator functions are often simpler than writing iterator classes manually.
```

Likely follow-up questions:

```text
What methods must an iterator implement?
What is StopIteration?
Iterable vs iterator?
Generator function vs iterator class?
```

Interview answer:

> An iterator implements `__iter__()` and `__next__()`. It returns values one at a time and raises `StopIteration` when finished. Iterators are memory efficient, and generator functions often provide a simpler way to create iterator behavior.

### Immutable Dictionary Key Follow-Ups

Safe keys:

```text
str
int
tuple of immutable values
frozenset
```

Unsafe keys:

```text
list
dict
set
tuple containing a list
```

Likely follow-up questions:

```text
What does hashable mean?
Why are lists not dictionary keys?
Can tuple always be a dictionary key?
What happens if a key's hash changes?
```

Interview answer:

> Dictionary keys must be hashable and stable. Immutable objects are usually safe because their hash does not change. Lists and dictionaries are mutable, so they cannot be keys. A tuple can be a key only if all values inside it are also hashable.
