# Method Overloading vs Overriding, Serializable, Clone, BigDecimal

## 1. Why This Concept Matters

**Method overloading** (compile-time polymorphism) and **overriding** (runtime polymorphism) are fundamental to Java's type system. Overloading resolves at compile time based on reference type and parameter types. Overriding resolves at runtime based on the actual object type. **Serializable** is the marker interface that enables Java object serialization — understanding `serialVersionUID`, transient fields, and custom serialization is essential for distributed systems and caching. **Clone** is a commonly misunderstood mechanism with shallow vs deep copy implications. **BigDecimal** is critical for financial calculations where `double` loses precision.

## 2. Overloading vs Overriding

| Aspect | Overloading | Overriding |
|--------|-------------|------------|
| When | Same class, different parameters | Subclass, same signature |
| Resolution | Compile time (based on reference type) | Runtime (based on actual object type) |
| Return type | Can differ freely | Must be covariant (same or subtype) |
| Exception | Can differ | Cannot throw broader checked exceptions |
| Access modifier | Can differ freely | Cannot be more restrictive |
| static methods | Can overload static methods | Cannot override static (can hide) |
| private methods | Can overload | Cannot override (private not inherited) |

```java
public class OverloadOverrideDemo {
    
    // === OVERLOADING ===
    public void print(int i) { System.out.println("int: " + i); }
    public void print(String s) { System.out.println("String: " + s); }
    public void print(int i, String s) { System.out.println(i + ", " + s); }
    
    // Widening beats boxing, boxing beats varargs
    public void accept(long l) { System.out.println("long: " + l); }
    public void accept(Integer i) { System.out.println("Integer: " + i); }
    public void accept(int... args) { System.out.println("varargs: " + args.length); }
    
    public static void main(String[] args) {
        OverloadOverrideDemo demo = new OverloadOverrideDemo();
        
        // Overload resolution:
        demo.print(5);        // int: 5 — exact match
        demo.print("hello");  // String: hello
        
        // Widening vs boxing vs varargs priority:
        demo.accept(5);       // long: 5 — widening beats boxing beats varargs
        
        // === OVERRIDING ===
        Animal a = new Dog();
        a.speak();  // "Woof!" (runtime: calls Dog's override)
        // a.fetch(); // COMPILE ERROR! Animal doesn't have fetch()
    }
}

class Animal {
    public void speak() { System.out.println("Some sound"); }
}

class Dog extends Animal {
    @Override
    public void speak() { System.out.println("Woof!"); }
    
    public void fetch() { System.out.println("Fetching..."); }
}
```

## 3. Serializable

```java
import java.io.*;

// Serializable is a marker interface (no methods)
class Employee implements Serializable {
    private static final long serialVersionUID = 1L; // CRITICAL for versioning
    
    private String name;
    private int age;
    private transient String password; // NOT serialized (security)
    private static String company = "MyCorp"; // NOT serialized (belongs to class)
    
    // Serialization:
    // ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("emp.ser"));
    // oos.writeObject(emp);
    
    // Deserialization:
    // ObjectInputStream ois = new ObjectInputStream(new FileInputStream("emp.ser"));
    // Employee emp = (Employee) ois.readObject();
}
```

**serialVersionUID**: If you change the class (add a field), deserializing an old object throws `InvalidClassException`. The UID tells JVM if the serialized object matches the current class definition. Always declare explicitly.

**transient**: Fields marked `transient` are NOT serialized — on deserialization they get default values (null for objects, 0 for primitives).

**Custom serialization**:
```java
class SecureData implements Serializable {
    private String encryptedData;
    
    // Custom serialization — encrypt before writing
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Serialize normally
        // Additional custom logic
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Deserialize normally
        // Decrypt or validate
    }
}
```

## 4. Clone

```java
class Person implements Cloneable {
    String name;
    Address address; // Mutable object
    
    public Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }
    
    // SHALLOW clone (default) — shares Address object
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    // DEEP clone — creates new Address
    public Person deepClone() {
        return new Person(this.name, new Address(this.address.getCity()));
    }
}
```

**Shallow copy**: cloned object shares the same mutable sub-objects. Changing cloned.address also changes original.address.
**Deep copy**: cloned object has independent copies of all mutable sub-objects. Safer but more complex.

## 5. BigDecimal

```java
BigDecimal a = new BigDecimal("0.1");  // ✅ Use String constructor
BigDecimal b = new BigDecimal(0.1);    // ❌ Uses double — imprecise!
BigDecimal c = BigDecimal.valueOf(0.1); // ✅ Uses String internally

// Comparison: use compareTo(), NOT equals()
BigDecimal x = new BigDecimal("1.0");
BigDecimal y = new BigDecimal("1.00");
System.out.println(x.equals(y));    // false! (different scale: 1 vs 2)
System.out.println(x.compareTo(y)); // 0 (equal by value — correct for business logic)
```

## 6. Final 30-Second Answer

**Overloading**: same method name, different params, compile-time. **Overriding**: same signature, runtime polymorphism via actual object type. Widening > boxing > varargs in overload resolution. **Serializable**: marker interface, always declare `serialVersionUID`, use `transient` for sensitive fields. **Clone**: `Cloneable` marker + `clone()` method — shallow by default, implement deep copy for mutable fields. **BigDecimal**: use `new BigDecimal("0.1")`, `compareTo()` not `equals()` for value comparison.