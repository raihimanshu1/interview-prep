# Constructors, Static Blocks & Initialization Order — Complete Deep Dive

## 1. Why This Concept Matters

Understanding Java initialization order is essential for debugging NullPointerExceptions, predicting constructor behavior in inheritance hierarchies, and avoiding subtle bugs when static fields reference each other. The order is strict: static blocks and static fields execute when the class is FIRST loaded (in order of appearance), then instance fields and constructors execute when `new` is called (parent first). Interviewers ask about this because it reveals your understanding of the JVM class lifecycle, and it's a common source of tricky output questions.

## 2. Basic Meaning

**Initialization order for a single class when instantiated:**
1. Static variable declarations & static initializer blocks (in code order) — run once when class loads
2. Instance variable declarations & instance initializer blocks (in code order)
3. Constructor body

**Initialization order for inheritance:**
1. Parent class static initializers
2. Child class static initializers
3. Parent instance initializers
4. Parent constructor
5. Child instance initializers
6. Child constructor

## 3. Real Code / Real Example

```java
public class InitOrderDemo {
    
    public static void main(String[] args) {
        System.out.println("=== First instantiation ===");
        Child c1 = new Child();
        
        System.out.println("\n=== Second instantiation (static blocks NOT re-run) ===");
        Child c2 = new Child();
    }
}

class Parent {
    static {
        System.out.println("1. Parent static block");
    }
    
    private static String parentStatic = initParentStatic();
    
    {
        System.out.println("3. Parent instance block");
    }
    
    private String parentField = initParentField();
    
    public Parent() {
        System.out.println("4. Parent constructor");
    }
    
    private static String initParentStatic() {
        System.out.println("2. Parent static field init");
        return "parent-static";
    }
    
    private String initParentField() {
        System.out.println("3.5 Parent instance field init");
        return "parent-instance";
    }
}

class Child extends Parent {
    static {
        System.out.println("2.5 Child static block");
    }
    
    private static String childStatic = initChildStatic();
    
    {
        System.out.println("5. Child instance block");
    }
    
    private String childField = initChildField();
    
    public Child() {
        System.out.println("6. Child constructor");
    }
    
    private static String initChildStatic() {
        System.out.println("2.75 Child static field init");
        return "child-static";
    }
    
    private String initChildField() {
        System.out.println("5.5 Child instance field init");
        return "child-instance";
    }
}
```

Expected output:
```
=== First instantiation ===
1. Parent static block
2. Parent static field init
2.5 Child static block
2.75 Child static field init
3. Parent instance block
3.5 Parent instance field init
4. Parent constructor
5. Child instance block
5.5 Child instance field init
6. Child constructor

=== Second instantiation (static blocks NOT re-run) ===
3. Parent instance block
3.5 Parent instance field init
4. Parent constructor
5. Child instance block
5.5 Child instance field init
6. Child constructor
```

## 4. Tricky Cases

**Case 1 — Static field referencing another static field not yet initialized**
```java
class Tricky {
    static int a = 5;
    static int b = a * 2;   // b = 10 (a is already initialized)
    
    static int c = getD();  // d is still 0 at this point!
    static int d = 10;
    
    static int getD() { return d; }  // Returns 0! (d not initialized yet)
}
```
Output: `c = 0`, `d = 10`. Fix: never reference another static field that hasn't been declared yet.

**Case 2 — Instance initializer throwing exception**
```java
class Risky {
    { if (someCondition) throw new RuntimeException(); }
    String name = "test"; // NEVER initialized — instance init failed
}
// Constructor never completes. name field remains default (null).
```

**Case 3 — super() vs this() in constructor**
```java
class Base {
    Base() { System.out.println("Base"); }
}

class Derived extends Base {
    Derived() {
        // super() is called IMPLICITLY if not specified
        System.out.println("Derived");
    }
}
// Output: Base → Derived
```

## 5. Final 30-Second Answer

Initialization order: static blocks/fields on class load (parent before child) → instance blocks/fields on `new` (parent before child) → constructor bodies (parent before child). Static initializers run ONCE per class. Instance initializers run EVERY `new`. Never reference a static field before its declaration. super() is called implicitly as the first statement in every constructor.