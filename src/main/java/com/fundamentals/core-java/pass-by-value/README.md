# Java Pass-By-Value — Complete Deep Dive

## 1. Why This Concept Matters

The single most common Java interview question and source of confusion. Java is ALWAYS pass-by-value — never pass-by-reference. Understanding this means understanding exactly what happens to method parameters: the method receives a copy of the variable. For primitives, it's a copy of the value. For objects, it's a copy of the REFERENCE (not the object itself). This explains why you can modify an object's fields inside a method but cannot swap two objects. Interviewers ask this to test whether you truly understand how Java handles variables, references, and memory.

Misunderstanding pass-by-value causes:
- Confusion about why swap methods don't work in Java
- Assuming you can reassign the caller's variable from inside a method
- Incorrectly believing "objects are passed by reference"
- Subtle bugs in builder/setter patterns where reassignment is expected to propagate

## 2. Basic Meaning

**Pass-by-value**: the method receives a COPY of the variable's value. Modifying the parameter inside the method does NOT affect the original variable.

**Java is ALWAYS pass-by-value:**
- Primitives: a copy of the actual value is passed
- Objects: a copy of the REFERENCE (memory address) is passed
- Arrays: a copy of the array reference is passed (same array, different variable)

| Scenario | What is copied | Can you modify caller's value? | Can you modify object state? |
|----------|---------------|-------------------------------|------------------------------|
| Primitive (`int x`) | The number itself | NO | N/A |
| Object reference (`Person p`) | The memory address | NO | YES (same object) |
| Array (`int[] arr`) | The array reference | NO | YES (same array elements) |
| Immutable (`String s`) | The reference to immutable object | NO | NO (immutable by design) |

**What it is NOT:**
- Not pass-by-reference (like C++ `&` or C# `ref` keyword)
- Not "pass-by-reference for objects" (common myth — WRONG)
- Not "pass-by-sharing" — Java does not share variable bindings

## 3. Real Code / Real Example

```java
public class PassByValueDemo {

    public static void main(String[] args) {
        // === CASE 1: PRIMITIVES ===
        int x = 10;
        System.out.println("Before modifyPrimitive: x = " + x); // 10
        modifyPrimitive(x);
        System.out.println("After modifyPrimitive: x = " + x);  // 10 (UNCHANGED!)

        // === CASE 2: OBJECT REFERENCES (the tricky one) ===
        Person p = new Person("Alice");
        System.out.println("Before modifyReference: " + p.getName()); // Alice
        modifyReference(p);
        System.out.println("After modifyReference: " + p.getName());  // Bob (CHANGED!)

        // === CASE 3: REASSIGNMENT (proves pass-by-value) ===
        Person a = new Person("Alice");
        Person b = new Person("Bob");
        System.out.println("Before swap: a=" + a.getName() + ", b=" + b.getName());
        swap(a, b);
        System.out.println("After swap: a=" + a.getName() + ", b=" + b.getName());
        // STILL Alice and Bob — swap did NOT work!
        // Because Java passed COPIES of references, not the original references

        // === CASE 4: IMMUTABLE OBJECTS (String) ===
        String s = "Hello";
        System.out.println("Before modifyString: " + s); // Hello
        modifyString(s);
        System.out.println("After modifyString: " + s);  // Hello (UNCHANGED!)
        // String is immutable — "Hello"+" World" creates a NEW String
        // The original reference s still points to "Hello"

        // === CASE 5: ARRAYS (same as objects — reference is copied) ===
        int[] arr = {1, 2, 3};
        System.out.println("Before modifyArray: arr[0] = " + arr[0]); // 1
        modifyArray(arr);
        System.out.println("After modifyArray: arr[0] = " + arr[0]);  // 99 (CHANGED!)

        // === CASE 6: REASSIGNMENT WITH ARRAY ===
        System.out.println("Before reassignArray: arr length = " + arr.length); // 3
        reassignArray(arr);
        System.out.println("After reassignArray: arr length = " + arr.length);  // 3 (NOT 5!)
    }

    // copy of value — changing value does NOT affect original x
    static void modifyPrimitive(int value) {
        value = 20; // Only modifies the local COPY
        System.out.println("Inside modifyPrimitive: value = " + value);
    }

    // copy of reference — both original p and local person point to SAME object
    static void modifyReference(Person person) {
        person.setName("Bob"); // Modifies the OBJECT that the copy points to
        System.out.println("Inside modifyReference: " + person.getName());
    }

    // copies of references — swapping the COPIES doesn't affect original a, b
    static void swap(Person p1, Person p2) {
        Person temp = p1;
        p1 = p2;         // Swaps LOCAL copies — original references unchanged
        p2 = temp;
        System.out.println("Inside swap: p1=" + p1.getName() + ", p2=" + p2.getName());
    }

    // copy of String reference — "Hello World" creates NEW object
    static void modifyString(String str) {
        str = str + " World"; // Creates NEW String object, local str points to new object
        System.out.println("Inside modifyString: " + str);
        // Original str still points to "Hello"
    }

    // copy of array reference — same array, different variable
    static void modifyArray(int[] array) {
        array[0] = 99; // Modifies the SAME array that caller sees
        System.out.println("Inside modifyArray: array[0] = " + array[0]);
    }

    // reassigning the local copy does NOT affect caller's array variable
    static void reassignArray(int[] array) {
        array = new int[]{10, 20, 30, 40, 50}; // local variable points to NEW array
        System.out.println("Inside reassignArray: array length = " + array.length);
        // caller's arr still points to the OLD array {1, 2, 3}
    }
}

class Person {
    private String name;
    Person(String name) { this.name = name; }
    void setName(String name) { this.name = name; }
    String getName() { return name; }
}
```

Expected output:
```
Before modifyPrimitive: x = 10
Inside modifyPrimitive: value = 20
After modifyPrimitive: x = 10
Before modifyReference: Alice
Inside modifyReference: Bob
After modifyReference: Bob
Before swap: a=Alice, b=Bob
Inside swap: p1=Bob, p2=Alice
After swap: a=Alice, b=Bob
Before modifyString: Hello
Inside modifyString: Hello World
After modifyString: Hello
Before modifyArray: arr[0] = 1
Inside modifyArray: array[0] = 99
After modifyArray: arr[0] = 99
Before reassignArray: arr length = 3
Inside reassignArray: array length = 5
After reassignArray: arr length = 3
```

## 4. What Happens Internally

### Memory Model for Object Parameter Passing

**Case 2 — Object modification:**
```
Stack Frame (main)        Stack Frame (modifyReference)     Heap
┌─────────────────┐       ┌─────────────────────┐          ┌─────────────────────┐
│ p (reference)   │──┐    │ person (reference)  │──┐       │ Person instance     │
│ val = 0xABCD    │  │    │ val = 0xABCD        │  │  ┌──→│ name = "Alice"→"Bob"│
└─────────────────┘  │    └─────────────────────┘  │  │   └─────────────────────┘
                      └──────────────────────────────┘  │
                         Both contain SAME memory addr──┘
```
Key insight: The reference value (memory address) is COPIED. Both copies point to same heap object.

**Case 3 — Swap (proves pass-by-value):**
```
BEFORE:
main: a ──→ Person("Alice")    ←── p1 (copy)
main: b ──→ Person("Bob")      ←── p2 (copy)

INSIDE swap (after reassignment):
main: a ──→ Person("Alice")    ←── p2 (now points to Bob's object)
main: b ──→ Person("Bob")      ←── p1 (now points to Alice's object)

AFTER swap:
main: a ──→ Person("Alice")  ←─ still unchanged!
main: b ──→ Person("Bob")    ←─ still unchanged!
```
The swap only affected the local copies (p1, p2). The original references (a, b) were never accessible to the method.

### JVM Stack Frame Layout

When a method is called, the JVM creates a new stack frame containing:
1. **Local Variable Array** — stores parameters and local variables (including the COPIED reference)
2. **Operand Stack** — used for computations
3. **Frame Data** — constant pool resolution, exception handling

```java
// After calling modifyReference(p):
// main() frame:    local[0] = args, local[1] = p (ref to Person)
// modifyReference frame: local[0] = person (COPY of p's ref value)
//
// person.setName("Bob")
// JVM uses local[0] to find Person object on heap
// Calls Person.setName() which modifies the heap object
// When modifyReference returns, its frame is popped
// main's p still holds the same reference → sees the change
```

## 5. Tricky Interview Cases

**Case 1 — Ternary with null wrapper**
```java
public static void main(String[] args) {
    Integer val = null;
    String result = (val != null) ? val.toString() : "null";
    System.out.println(result); // "null"
    
    // But what if?
    // int x = (val != null) ? val : 0; // NO NPE — ternary evaluates branch only
    // int y = (val != null) ? val : null; // COMPILE ERROR — null can't unbox to int
}
```
Output: `"null"`
Explanation: Ternary evaluates only the selected branch. Since `val != null` is false, the `: "null"` branch is taken. No unboxing occurs.

**Case 2 — Object passed to method that modifies, then reassigns**
```java
class MutableContainer {
    int value;
    MutableContainer(int v) { this.value = v; }
}

static void tricky(MutableContainer c) {
    c.value = 100;   // modifies the object — caller sees this
    c = new MutableContainer(200); // reassigns LOCAL copy — caller does NOT see this
    c.value = 300;   // modifies the NEW object — caller never sees this
}

public static void main(String[] args) {
    MutableContainer c = new MutableContainer(50);
    tricky(c);
    System.out.println(c.value); // 100, NOT 300!
}
```
Output: `100`
Explanation: `c.value = 100` changes the original object. `c = new MutableContainer(200)` makes the local variable point to a DIFFERENT object. Subsequent `c.value = 300` modifies the new object that the caller's `c` doesn't point to.

**Case 3 — StringBuilder — mutable but often mistaken**
```java
static void appendString(StringBuilder sb) {
    sb.append(" World"); // Modifies the object — caller sees "Hello World"
}

static void reassignString(StringBuilder sb) {
    sb = new StringBuilder("New"); // Reassigns LOCAL copy — caller doesn't see this
}

public static void main(String[] args) {
    StringBuilder sb = new StringBuilder("Hello");
    appendString(sb);
    System.out.println(sb.toString()); // "Hello World"
    
    reassignString(sb);
    System.out.println(sb.toString()); // STILL "Hello World" (not "New")
}
```
Output: `Hello World` then `Hello World`
Explanation: Same principle — object mutation propagates, reference reassignment does not.

**Case 4 — List passed to method**
```java
static void addToList(List<String> list) {
    list.add("B"); // Modifies the list — caller sees it
}

static void reassignList(List<String> list) {
    list = new ArrayList<>();
    list.add("C"); // New list, caller's list unaffected
}

public static void main(String[] args) {
    List<String> list = new ArrayList<>();
    list.add("A");
    addToList(list);
    System.out.println(list); // [A, B]
    
    reassignList(list);
    System.out.println(list); // [A, B] (NOT [C])
}
```
Output: `[A, B]` then `[A, B]`

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|------|
| Writing `swap(a, b)` to swap two references | Doesn't work — method can't reassign caller's variables | Return new values or use a container/array |
| Saying "Java passes objects by reference" | Misleading — the REFERENCE itself is passed by value, not the object | Be precise: "Java passes the reference by value" |
| Expecting reassignment of parameter to affect caller | Parameter is a local variable — reassignment only affects local scope | Use return value or modify object's state (not reference) |
| Not realizing array params are modifyable but reassignable | `arr[0] = x` works; `arr = new int[]` doesn't propagate | Same rule applies as any object |
| Confusing String with mutable objects | String concatenation creates NEW object — original stays | Use StringBuilder for mutable string operations |
| Thinking `final` parameter makes object immutable | `final Person p` prevents reassignment of `p` but NOT `p.setName()` | `final` on parameter = local variable can't be reassigned only |
| Passing `this` to another method expecting state change | `this` is a reference — same object, different copy | Works for mutation, but method can't replace `this` |

## 7. Production Usage

**DTO mapping with libraries:**
```java
// MapStruct generates code that understands pass-by-value
// Source object is not modified — fields are read, new target is created
@Mapper
public interface PaymentMapper {
    PaymentDTO toDto(Payment payment); // payment object fields read, not modified
}

// Usage — payment object is safe from mutation
Payment payment = paymentService.findById(id);
PaymentDTO dto = paymentMapper.toDto(payment);
// payment object still has original state
```

**Builder pattern — method chaining:**
```java
public class OrderBuilder {
    private String id;
    private BigDecimal amount;
    
    public OrderBuilder withId(String id) {
        this.id = id; // setting field on SAME builder instance
        return this;  // returning 'this' reference (pass-by-value of reference)
    }
    
    public OrderBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
}

// Usage — each method returns same builder instance
Order order = new OrderBuilder()
    .withId("ORD-123")    // returns builder (same ref)
    .withAmount(new BigDecimal("99.99")) // returns builder (same ref)
    .build();
```

**Immutable collections and defensive copying:**
```java
public class UserService {
    private final Set<String> adminEmails;
    
    public UserService(Set<String> adminEmails) {
        // Defensive copy — prevents caller from modifying our internal state
        this.adminEmails = new HashSet<>(adminEmails);
    }
    
    public Set<String> getAdminEmails() {
        // Another defensive copy — prevents caller from modifying our internal set
        return new HashSet<>(adminEmails);
    }
}

// Production anti-pattern (without defensive copy):
Set<String> malicious = new HashSet<>();
malicious.add("admin@bank.com");
UserService service = new UserService(malicious);
malicious.clear(); // Would clear the internal set too! (same reference)
// Fix: defensive copy in constructor
```

**Spring bean injection — singleton reference sharing:**
```java
@Component
public class PaymentProcessor {
    private final PaymentRepository repository;
    private final NotificationService notifier;
    
    // Spring injects the SAME singleton instances into all beans
    // Constructor receives COPY of reference to each singleton
    public PaymentProcessor(PaymentRepository repository, 
                           NotificationService notifier) {
        this.repository = repository; // copy of reference to shared singleton
        this.notifier = notifier;
    }
    
    public void process(Payment payment) {
        repository.save(payment); // modifies DB through shared repository reference
        // this.repository was set ONCE during construction, never reassigned
    }
}
```

## 8. Advanced Details

- **C/C++/C# `ref` comparison**: In C#, `void swap(ref int a, ref int b)` can actually swap the caller's variables because the method receives a pointer to the variable itself. Java has no equivalent — no `ref`, `out`, or pointer-to-variable mechanism.
- **Java's `Reference` classes**: `WeakReference`, `SoftReference`, `PhantomReference` are NOT about pass-by-value. They are about garbage collection reachability. The reference value itself is still passed by value.
- **`VarHandle` (Java 9+)**: Provides access to variable handles for memory operations (like `compareAndSet`), but does NOT change pass-by-value semantics. You can atomically update a field of an object, but you cannot change which object a variable points to from a method.
- **`Unsafe.compareAndSwapObject()`**: Low-level CAS operations work on object fields, not local variables. This enables lock-free data structures but doesn't allow a method to swap the caller's reference.
- **Method handle `bindTo()`**: Binds a fixed reference argument to a method handle. The bound value is stored as a copy in the method handle — it's not a live reference to the caller's variable.
- **Lambda capture**: Lambdas capture variables by value (for primitives) or by reference value (for objects). The captured variable must be effectively final. Inside the lambda, you can modify object state but cannot reassign the captured reference.
- **`ThreadLocal`**: Each thread has its own copy of a `ThreadLocal` value. Even though the `ThreadLocal` reference is passed by value to `get()`/`set()`, the stored value is per-thread. This is NOT pass-by-reference — it's thread-local storage with pass-by-value parameter semantics.

## 9. Interview Questions And Answers

### Beginner
Q: Is Java pass-by-value or pass-by-reference?
A: Java is ALWAYS pass-by-value. For primitives, a copy of the value is passed. For objects, a copy of the REFERENCE is passed. The method can modify the object's state through the copied reference, but it can NEVER change which object the caller's variable points to. There is no "pass-by-reference for objects" in Java — that is a myth.

### Intermediate
Q: What is the output? `String s = "Hello"; changeString(s); System.out.println(s);` where `changeString(String str) { str = str + " World"; }`
A: The output is `Hello`. The method receives a copy of the reference to the String object `"Hello"`. When `str + " World"` executes, it creates a NEW String object `"Hello World"`. The local variable `str` now points to this new object. But the original `s` in main still points to `"Hello"`. String immutability combined with pass-by-value means the original variable is never affected by concatenation inside a method.

### Senior
Q: In a payment reconciliation system, you have a method that receives a `List<Transaction>` and needs to filter out duplicates and return the filtered list. Several team members suggest: (1) clearing the original list and adding filtered items back, (2) creating a new list and returning it, (3) using `removeIf` on the original list. Analyze each approach's implications for the caller.
A: 
1. **Clear and add back**: `list.clear(); list.addAll(filtered);` — This mutates the caller's list object. The caller sees the changes, which may be unexpected if they still need the original data. Data-loss risk if the method throws mid-execution.

2. **Return new list**: `List<Transaction> filtered = new ArrayList<>(); ... return filtered;` — This is the safest. The caller's original list remains unchanged. The caller decides whether to replace their reference. Follows principle of least surprise.

3. **`removeIf`**: `list.removeIf(dup -> isDuplicate(dup));` — Mutates the caller's list in-place. More efficient (no new allocation) but potentially dangerous if the caller expects the original reference to remain unchanged.

**Recommendation**: For production, use option 2 (new list + return). This avoids side effects and is thread-safe if the caller is iterating the original list. For performance-critical batch processing where allocation is expensive, use option 3 with clear documentation that the list is mutated.

### Tricky
Q: Consider this code and predict the output:
```java
public class TrickyPassByValue {
    static void process(Integer val) {
        val = val + 10;
    }
    
    static void process(int[] arr) {
        arr[0] = arr[0] + 10;
        arr = new int[]{100, 200};
    }
    
    public static void main(String[] args) {
        Integer x = 5;
        process(x);
        System.out.println("x = " + x);
        
        int[] nums = {1, 2, 3};
        process(nums);
        System.out.println("nums[0] = " + nums[0] + ", length = " + nums.length);
    }
}
```
A: Output is:
```
x = 5
nums[0] = 11, length = 3
```

Explanation: 
- For `Integer`: `val = val + 10` unboxes `val` to `int` (5), adds 10 → 15, then auto-boxes into a NEW `Integer(15)`. The local `val` points to the new Integer. But the original `x` still points to `Integer(5)`. Integer is immutable — no method can change its value.
- For `int[]`: `arr[0] = arr[0] + 10` modifies the first element of the SAME array object. The caller sees `nums[0] = 11`. But `arr = new int[]{100, 200}` reassigns the LOCAL copy to a new array. The caller's `nums` still points to the original array `{11, 2, 3}` with length 3.

## 10. Final 30-Second Answer

Java is ALWAYS pass-by-value. Primitives: copy of value. Objects: copy of reference. Method can modify object state through copied reference but CANNOT reassign the caller's variable. Swap methods don't work. String/BigDecimal/wrapper operations create new objects. Defensive copying protects internal state. Use return values instead of trying to modify caller's references.
