# Builder Pattern

> **Separates the construction of a complex object from its representation so that the same construction process can create different representations.**

## 📖 Concept

**Real-world analogy:** A Subway sandwich — you choose the bread, toppings, sauces, and each choice builds the sandwich step-by-step. The same process can produce a Veggie or a Chicken sub.

## 🔍 When to Use

- Object has many optional parameters
- Construction requires multiple steps
- Same construction process should create different representations
- Too many constructor parameters (telescoping constructor anti-pattern)
- Want immutable objects with many fields

## ✅ Interview Checklist

- [ ] Builder class with methods for each optional parameter
- [ ] Builder returns `this` for method chaining (fluent interface)
- [ ] `build()` method returns the constructed object
- [ ] Product class has private constructor taking Builder
- [ ] Builder is static nested class
- [ ] Consider varargs for repeated parameters (toppings, ingredients)

## 🧪 Common Interview Question

**Problem:** Design a URL Builder that constructs URLs with protocol, host, port, path, query parameters, and fragments. Support method chaining.

## 💻 Java Implementation

### 1. Basic Builder

```java
// Product
class URL {
    private String protocol;
    private String host;
    private int port;
    private String path;
    private String queryParams;
    private String fragment;

    private URL(Builder builder) {
        this.protocol = builder.protocol;
        this.host = builder.host;
        this.port = builder.port;
        this.path = builder.path;
        this.queryParams = builder.queryParams;
        this.fragment = builder.fragment;
    }

    @Override
    public String toString() {
        StringBuilder url = new StringBuilder();
        url.append(protocol).append("://").append(host);
        if (port != 80 && port != 443) url.append(":").append(port);
        if (path != null) url.append("/").append(path);
        if (queryParams != null) url.append("?").append(queryParams);
        if (fragment != null) url.append("#").append(fragment);
        return url.toString();
    }

    // Builder
    public static class Builder {
        private String protocol = "https";
        private String host;
        private int port = 80;
        private String path;
        private String queryParams;
        private String fragment;

        public Builder(String host) {
            this.host = host;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder queryParam(String key, String value) {
            if (this.queryParams == null) this.queryParams = "";
            else this.queryParams += "&";
            this.queryParams += key + "=" + value;
            return this;
        }

        public Builder fragment(String fragment) {
            this.fragment = fragment;
            return this;
        }

        public URL build() {
            return new URL(this);
        }
    }
}
```

### 2. Usage

```java
public class BuilderDemo {
    public static void main(String[] args) {
        URL url = new URL.Builder("api.example.com")
                .protocol("https")
                .port(443)
                .path("users")
                .queryParam("id", "123")
                .queryParam("active", "true")
                .fragment("section1")
                .build();

        System.out.println(url);
        // Output: https://api.example.com/users?id=123&active=true#section1
    }
}
```

### 3. Full Working Example: Pizza Order Builder

```java
// Pizza.java
class Pizza {
    private String size;           // Required
    private String crust;          // Required
    private boolean cheese;
    private boolean pepperoni;
    private boolean olives;
    private boolean mushrooms;
    private String sauce;

    private Pizza(Builder builder) {
        this.size = builder.size;
        this.crust = builder.crust;
        this.cheese = builder.cheese;
        this.pepperoni = builder.pepperoni;
        this.olives = builder.olives;
        this.mushrooms = builder.mushrooms;
        this.sauce = builder.sauce;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(size).append(" ").append(crust).append(" pizza");
        sb.append(" with ").append(sauce).append(" sauce");
        if (cheese) sb.append(" + cheese");
        if (pepperoni) sb.append(" + pepperoni");
        if (olives) sb.append(" + olives");
        if (mushrooms) sb.append(" + mushrooms");
        return sb.toString();
    }

    // Builder
    public static class Builder {
        private final String size;    // Required
        private final String crust;   // Required
        private boolean cheese = false;
        private boolean pepperoni = false;
        private boolean olives = false;
        private boolean mushrooms = false;
        private String sauce = "tomato";

        public Builder(String size, String crust) {
            this.size = size;
            this.crust = crust;
        }

        public Builder cheese(boolean cheese) {
            this.cheese = cheese;
            return this;
        }

        public Builder pepperoni(boolean pepperoni) {
            this.pepperoni = pepperoni;
            return this;
        }

        public Builder olives(boolean olives) {
            this.olives = olives;
            return this;
        }

        public Builder mushrooms(boolean mushrooms) {
            this.mushrooms = mushrooms;
            return this;
        }

        public Builder sauce(String sauce) {
            this.sauce = sauce;
            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}

// Usage
public class PizzaDemo {
    public static void main(String[] args) {
        Pizza pizza = new Pizza.Builder("Large", "Thin")
                .cheese(true)
                .pepperoni(true)
                .sauce("white")
                .build();

        System.out.println(pizza);
        // Output: Large Thin pizza with white sauce + cheese + pepperoni
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Builder becomes too large | Split into sub-builders or use telescoping pattern for related params |
| Forgetting to call `build()` | Make `build()` final, validate in it |
| Builder allows invalid state | Validate in `build()` before constructing product |
| Duplicate code between Builder and Product | Builder should be the only way to create Product |

## 🎯 Related Interview Questions

1. **Design a SQL Query Builder** — `SELECT * FROM users WHERE id = 1 ORDER BY name`
2. **Design a Computer Configuration Builder** — RAM, CPU, Storage, GPU options
3. **Design an Email Builder** — to, cc, bcc, subject, body, attachments
4. **Difference between Builder and Abstract Factory?** — Builder focuses on step-by-step construction; Factory focuses on which object to create