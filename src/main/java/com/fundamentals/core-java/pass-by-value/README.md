# Java Pass-By-Value — Complete Deep Dive

## 1. Why This Concept Matters

The single most common Java interview question and source of confusion. Java is ALWAYS pass-by-value — never pass-by-reference. Understanding this means understanding exactly what happens to method parameters: the method receives a copy of the variable. For primitives, it's a copy of the value. For objects, it's a copy of the REFERENCE (not the object itself). This explains why you can modify an object's fields inside a method but cannot swap two objects. Interviewers ask this to test whether you truly understand how Java handles variables, references, and memory.

## 2. Basic Meaning

**Pass-by-value**: the method receives a COPY of the variable's value. Modifying the parameter inside the method does NOT affect the original variable.

**Java is ALWAYS pass-by-value:**
- Primitives: a copy of the actual value is passed
- Objects: a copy of the REFERENCE (memory address) is passed

**What it is NOT:**
- Not pass-by-reference (like C++ `&` or C# `ref` keyword)
- Not "pass-by-reference for objects" (common myth — WRONG)

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
    }
    
    static void modifyPrimitive(int value) {
        value = 20; // Only modifies the local COPY
    }
    
    static void modifyReference(Person person) {
        person.setName("Bob"); // Modifies the OBJECT that the copy points to
        // Both original AND copy point to the SAME object
    }
    
    static void swap(Person p1, Person p2) {
        Person temp = p1;
        p1 = p2;         // Swaps LOCAL copies — original references unchanged
        p2 = temp;
        System.out.println("Inside swap: p1=" + p1.getName() + ", p2=" + p2.getName());
    }
    
    static void modifyString(String str) {
        str = str + " World"; // Creates NEW String object, local str points to new object
        // Original str still points to "Hello"
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
```

## 4. What Happens Internally

**Memory diagram for Case 2:**
```
Stack (main)          Stack (modifyReference)     Heap
┌──────────┐           ┌──────────┐              ┌────────────────┐
│ p = 0x100│──copy──→  │ person   │────same────→│ Person("Alice")│
└──────────┘           │ = 0x100  │────ref─────→│ name="Bob"     │
                        └──────────┘              └────────────────┘
Both p and person point to the SAME object on the heap.
Modifying through person affects the same object p sees.
But reassigning person inside the method does NOT affect p.
```

## 5. Final 30-Second Answer

Java is ALWAYS pass-by-value. Primitives: copy of value (original unchanged). Objects: copy of reference (both point to same object — you can modify its fields, but you CANNOT swap or reassign the original variable). Myth busted: "objects are pass-by-reference" is FALSE. The reference itself is passed by value. Inside a method, you get a copy of the reference — changing which object the copy points to does NOT affect the caller's variable.