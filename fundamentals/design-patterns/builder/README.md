# Builder Pattern — Complete Deep Dive

## 1. Why This Concept Matters

Builder pattern solves the problem of constructing complex objects with many optional parameters. Without Builder, you end up with "telescoping constructors" — a constructor with 2 parameters, one with 3, one with 4, etc. — making code hard to read and error-prone (which parameter was that boolean?). In production, Builder is used for configuration objects, HTTP request builders, query builders, test data factories, and any class where clients need to specify only a subset of parameters while keeping the object immutable. Interviewers test this as one of the most commonly used patterns in real Java code — Lombok's `@Builder` generates it automatically, and understanding how it works is essential for debugging generated code.

Misunderstanding Builder causes:
- Telescoping constructors with 10+ parameters (unreadable, error-prone)
- Mutable objects with setters (must set all fields, thread-unsafe if mutated after creation)
- Required vs optional fields not enforced at compile time
- Builder instances reused after build() (shared internal state)

## 2. Basic Meaning

Builder pattern separates object construction from its representation. A Builder class collects parameters step by step and then builds the final immutable object in one shot.

**Two forms:**
1. **Static Inner Builder (Joshua Bloch, Effective Java)**: the most common form in Java. Static inner class with fluent setter methods. `build()` creates the final object.
2. **Classic GoF Builder**: Abstract builder interface with concrete implementations. Director orchestrates construction. Used when the same construction process can create different representations.

**Key vocabulary:**
- **Builder**: static inner class with same fields as the target class
- **Fluent interface**: setter methods return `this` for method chaining
- **`build()`**: validates all required fields, creates the final object
- **Target class**: the class being built — has private constructor taking Builder
- **Required fields**: passed to Builder constructor (compile-time enforcement)
- **Optional fields**: set via fluent methods (can be omitted)
- **Immutability**: target class fields are `final`, no setters

## 3. Real Code / Real Example

```java
// === 1. STATIC INNER BUILDER (Joshua Bloch) ===
public class User {
    // All fields are final — immutable after construction
    private final String email;        // required
    private final String fullName;     // required
    private final Integer age;         // optional
    private final String phone;        // optional
    private final String address;      // optional
    private final List<String> roles;  // optional, defaults to empty list
    
    // Private constructor — only Builder can create User
    private User(Builder builder) {
        this.email = builder.email;
        this.fullName = builder.fullName;
        this.age = builder.age;
        this.phone = builder.phone;
        this.address = builder.address;
        this.roles = Collections.unmodifiableList(builder.roles); // defensive copy
    }
    
    // Getters (no setters — immutable)
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public Optional<Integer> getAge() { return Optional.ofNullable(age); }
    // ... other getters
    
    // Static inner Builder class
    public static class Builder {
        // Required fields — passed to constructor
        private final String email;
        private final String fullName;
        
        // Optional fields — have defaults
        private Integer age;
        private String phone;
        private String address;
        private List<String> roles = new ArrayList<>();
        
        // Builder constructor with REQUIRED fields
        public Builder(String email, String fullName) {
            this.email = Objects.requireNonNull(email, "email must not be null");
            this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
        }
        
        // Fluent setter methods for optional fields
        public Builder age(int age) {
            if (age < 0) throw new IllegalArgumentException("Age must be positive");
            this.age = age;
            return this;
        }
        
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public Builder addRole(String role) {
            this.roles.add(Objects.requireNonNull(role));
            return this;
        }
        
        public Builder roles(List<String> roles) {
            this.roles = new ArrayList<>(roles); // defensive copy
            return this;
        }
        
        // Build method — validates and creates
        public User build() {
            // Validation
            if (email == null || fullName == null) {
                throw new IllegalStateException("Email and fullName are required");
            }
            return new User(this);
        }
    }
    
    @Override
    public String toString() {
        return "User{email='" + email + "', fullName='" + fullName + "'}";
    }
}

// === USAGE ===
public class BuilderDemo {
    public static void main(String[] args) {
        // Only required fields
        User basic = new User.Builder("alice@example.com", "Alice")
                .build();
        System.out.println(basic); // User{email='alice@example.com', fullName='Alice'}
        
        // All optional fields
        User full = new User.Builder("bob@example.com", "Bob")
                .age(30)
                .phone("+1-555-1234")
                .address("123 Main St, City")
                .addRole("ADMIN")
                .addRole("USER")
                .build();
        System.out.println(full);
        
        // Validation catches missing required at COMPILE TIME (constructor params)
        // new User.Builder() // COMPILE ERROR: no default constructor
        // new User.Builder(null, "Name") // RUNTIME: NullPointerException from Objects.requireNonNull
    }
}

// === 2. CLASSIC GOF BUILDER ===
// Used when same construction process creates different representations:
interface DocumentBuilder {
    void addTitle(String title);
    void addParagraph(String text);
    void addImage(String url, String caption);
    Document build();
}

class HTMLDocumentBuilder implements DocumentBuilder {
    private StringBuilder html = new StringBuilder();
    
    public void addTitle(String title) {
        html.append("<h1>").append(title).append("</h1>\n");
    }
    public void addParagraph(String text) {
        html.append("<p>").append(text).append("</p>\n");
    }
    public void addImage(String url, String caption) {
        html.append("<figure><img src='").append(url)
            .append("'/><figcaption>").append(caption).append("</figcaption></figure>\n");
    }
    public Document build() {
        return new Document(html.toString(), "text/html");
    }
}

class MarkdownDocumentBuilder implements DocumentBuilder {
    private StringBuilder md = new StringBuilder();
    
    public void addTitle(String title) {
        md.append("# ").append(title).append("\n\n");
    }
    public void addParagraph(String text) {
        md.append(text).append("\n\n");
    }
    public void addImage(String url, String caption) {
        md.append("![").append(caption).append("](").append(url).append(")\n");
    }
    public Document build() {
        return new Document(md.toString(), "text/markdown");
    }
}

// Director — guides the construction process
class DocumentDirector {
    public Document constructSimpleDocument(DocumentBuilder builder) {
        builder.addTitle("My Document");
        builder.addParagraph("This is a simple document.");
        builder.addImage("https://example.com/image.png", "Sample Image");
        return builder.build();
    }
}

// Usage:
DocumentDirector director = new DocumentDirector();
Document html = director.constructSimpleDocument(new HTMLDocumentBuilder());
Document markdown = director.constructSimpleDocument(new MarkdownDocumentBuilder());
```

Expected output:
```
User{email='alice@example.com', fullName='Alice'}
User{email='bob@example.com', fullName='Bob'}
// HTML: <h1>My Document</h1><p>...</p>...
// Markdown: # My Document\n\n...
```

## 4. What Happens Internally

**Lombok @Builder — what it generates:**
```java
// Source:
@Builder
public class User {
    private String email;
    private String name;
}

// Lombok generates:
public class User {
    private String email;
    private String name;
    
    User(String email, String name) {
        this.email = email; this.name = name;
    }
    
    public static UserBuilder builder() {
        return new UserBuilder();
    }
    
    public static class UserBuilder {
        private String email;
        private String name;
        
        UserBuilder() {}
        
        public UserBuilder email(String email) {
            this.email = email; return this;
        }
        public UserBuilder name(String name) {
            this.name = name; return this;
        }
        public User build() {
            return new User(email, name);
        }
    }
}
```

**Key difference — Lombok @Builder creates ALL optional fields:**
- No compile-time enforcement of required fields (all fields are optional setters)
- `build()` returns null for unset fields
- To enforce required: `@Builder(builderMethodName = "builder")` + custom static method with required params

**Defensive copying in build():**
```java
public User build() {
    // Create COPY of mutable fields to prevent external modification
    List<String> rolesCopy = new ArrayList<>(this.roles);
    return new User(email, fullName, rolesCopy);
    // Without copy: caller could modify list after User is built
}
```

## 5. Tricky Interview Cases

**Case 1 — Reusing a Builder**
```java
User.Builder builder = new User.Builder("a@b.com", "Alice")
    .age(30);
User user1 = builder.build();
User user2 = builder.build(); // SAME internal builder state!
user1.getAge(); // 30
user2.getAge(); // 30 — OK, same value
// But if builder modifies after first build:
builder.phone("123-4567");
User user3 = builder.build(); // phone set, user1 doesn't have it
```
Problem: After `build()`, the builder still holds all state. Reusing it is technically fine (build() doesn't clear fields), but can be confusing. **Fix**: Either don't reuse builders, or add a `reset()` method or `clear()`.

**Case 2 — Inheritance and Builders**
```java
class Animal {
    protected String name;
    // Builder pattern...
}
class Dog extends Animal {
    private String breed;
    // How to create Dog builder that also sets Animal fields?
}
```
Fix: Use recursive generics (self-bounded types):
```java
abstract static class Builder<T extends Builder<T>> {
    protected String name;
    public T name(String name) { this.name = name; return self(); }
    protected abstract T self();
}
public static class DogBuilder extends Builder<DogBuilder> {
    private String breed;
    public DogBuilder breed(String breed) { this.breed = breed; return this; }
    protected DogBuilder self() { return this; }
    public Dog build() { return new Dog(this); }
}
// Usage: new DogBuilder().name("Rex").breed("German Shepherd").build();
```

**Case 3 — Thread safety**
```java
// Builder is NOT thread-safe by default
// Multiple threads calling builder.age(30) and builder.age(40) concurrently
// → race condition: final age might be 30 or 40 unpredictably
```
Fix: Don't share Builder between threads. Create a new Builder per thread.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| No required fields in Builder constructor | Compile-time not enforced — can build without critical fields | Pass required fields to Builder constructor |
| Mutable builder returns same instance on build() | Copy mutable fields (List, Map) or caller can modify | `new ArrayList<>(field)` in build() |
| Builder not returning `this` | Can't chain methods | Return `this` from each setter |
| Validation in setter instead of build() | Invalid state during construction (e.g., negative age during intermediate step) | Validate in setter for immediate feedback, validate in build() for final check |
| Lombok @Builder without required fields | All fields optional — missing required field compiles but fails at runtime | Custom static factory method with required params |
| Keeping builder after build() | Accidental reuse with stale state | Don't reuse, or add clear() to reset |
| Exposing public constructor | Clients can bypass Builder | Make constructor private |

## 7. Production Usage

**Spring Boot configuration properties:**
```java
@Data
@Builder
@ConfigurationProperties(prefix = "app.notification")
public class NotificationConfig {
    private String emailFrom;
    private int retryCount = 3;
    private boolean sendPush = true;
    private List<String> adminEmails = new ArrayList<>();
}
```

**HTTP request builder (OkHttp):**
```java
Request request = new Request.Builder()
    .url("https://api.example.com/users")
    .header("Authorization", "Bearer " + token)
    .post(RequestBody.create(mediaType, json))
    .build();
```

**Test data builders (Builder + Object Mother):**
```java
public class TestUserBuilder {
    public static User.UserBuilder defaultUser() {
        return new User.Builder("test@example.com", "Test User")
            .age(25)
            .phone("555-0000");
    }
    public static User.UserBuilder adminUser() {
        return defaultUser()
            .email("admin@example.com")
            .addRole("ADMIN");
    }
}
// Usage: TestUserBuilder.adminUser().build()
```

## 8. Advanced Details

- **Builder vs Factory**: Factory creates objects in one step (often with logic). Builder constructs step by step (for complex configuration).
- **Builder vs Constructor**: Constructor overloads (telescoping) grow exponentially with parameters. Builder handles any subset cleanly.
- **Lombok @Builder with @Singular**: `@Singular` adds individual `addXxx()` methods for collection fields, plus a `clearXxx()` method.
- **Immutability**: Builder pattern enables truly immutable objects — all fields final, no setters, defensive copies in getters.
- **Performance**: Builder creates one extra object (the Builder) per constructed instance. Negligible overhead. JIT may inline it.

## 9. Interview Questions And Answers

### Beginner
Q: What is the Builder pattern? Why use it instead of a constructor?
A: Builder pattern constructs objects step by step using a separate Builder class. Use it when a class has many optional parameters. Constructor overloading (telescoping constructors) becomes unreadable with >4 parameters — you can't tell which boolean is which. Builder makes the code self-documenting: `new User.Builder("email").name("Alice").age(30).build()` clearly shows what each value means.

### Intermediate
Q: How does Lombok's @Builder work? What does it generate?
A: `@Builder` generates a static inner Builder class with one setter method for each field in the class, a `builder()` static factory method that returns a new Builder instance, and a private all-args constructor on the target class. The Builder's `build()` calls this private constructor. Limitations: no compile-time enforcement of required fields (all fields are optional setters), and mutable collections need `@Singular` for proper copy behavior.

### Senior
Q: You're designing an Email class with 15 optional fields (to, cc, bcc, subject, body, attachments, priority, etc.). How do you make the Builder enforce that `to` and `subject` are always provided?
A: Two approaches:
1. **Constructor enforcement**: Make `to` and `subject` required in the Builder constructor:
```java
public static class Builder {
    private final String to;
    private final String subject;
    public Builder(String to, String subject) { ... }
    // Optional: cc(String), bcc(String), body(String), ...
}
```
2. **Build-time validation + static factory**: Use `@Builder` from Lombok with a static factory:
```java
public static Builder builder(String to, String subject) {
    return new Builder().to(to).subject(subject);
}
// In build(): if (to == null || subject == null) throw
```

The first approach (constructor) gives compile-time safety — the code won't compile if `to` is missing. Prefer this.

### Tricky
Q: What happens when you extend a class that has a Builder? How do you create a Builder for the subclass that inherits parent fields?
A: Use recursive generics (self-bounded types):
```java
abstract static class BaseBuilder<T extends BaseBuilder<T>> {
    protected String name;
    public T name(String name) { this.name = name; return self(); }
    protected abstract T self();
}
public static class ChildBuilder extends BaseBuilder<ChildBuilder> {
    private String extra;
    public ChildBuilder extra(String extra) { this.extra = extra; return this; }
    protected ChildBuilder self() { return this; }
    public Child build() { return new Child(this); }
}
// Usage: new ChildBuilder().name("parent").extra("child").build()
```
Without this pattern, `new ChildBuilder().name("x")` would return `BaseBuilder`, not `ChildBuilder`, so you couldn't chain `extra()` after `name()`.

## 10. Final 30-Second Answer

Builder = construct complex objects with many optional parameters. **Static inner Builder** (Joshua Bloch): required fields in Builder constructor, fluent setters for optional, `build()` creates immutable object. **Lombok @Builder**: auto-generates, but no required-field enforcement. **Always**: validation in `build()`, defensive copy of mutable fields (List/Map), make target class constructor private. When: >3-4 optional parameters, immutable objects needed, self-documenting construction. Never: reuse builder after build (unexpected state), expose public constructor (bypasses builder), forget required field validation.