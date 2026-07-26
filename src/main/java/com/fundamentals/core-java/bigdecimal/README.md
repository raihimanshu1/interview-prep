# BigDecimal — Complete Deep Dive

## 1. Why This Concept Matters

`BigDecimal` is Java's solution for precise decimal arithmetic — essential for financial calculations, tax computations, currency conversions, and any domain where floating-point errors are unacceptable. A `double` loses precision on `0.1 + 0.2` (gives `0.30000000000000004`). In a payment system processing millions of transactions, such errors accumulate into significant discrepancies. Interviewers ask about `BigDecimal` because it's the #1 class for monetary values, and misunderstanding its `equals()` vs `compareTo()` behavior is a common source of production bugs.

Misunderstanding BigDecimal causes:
- Financial calculation errors from using `double` instead of `BigDecimal`
- Incorrect comparisons using `equals()` instead of `compareTo()` (scale mismatch)
- Performance issues from creating too many BigDecimal objects in hot loops
- Rounding mode mistakes that lose pennies on every transaction
- `ArithmeticException` from division without specifying scale and rounding

## 2. Basic Meaning

`BigDecimal` is an immutable arbitrary-precision decimal number. It consists of:
- An unscaled integer value (all digits)
- A scale (number of digits to the right of the decimal point)

```java
new BigDecimal("123.45")
// unscaled = 12345, scale = 2
// value = 12345 × 10^(-2) = 123.45
```

**Key vocabulary:**
- **Scale**: number of decimal places. `123.45` has scale 2. `123` has scale 0.
- **Precision**: total number of significant digits. `123.45` has precision 5.
- **Rounding mode**: how to handle extra digits (HALF_UP, DOWN, CEILING, etc.)
- **MathContext**: precision + rounding mode configuration
- **Monetary values**: ALWAYS use `BigDecimal`, never `double` or `float`

**What it is NOT:** Not a replacement for `long` in performance-critical integer arithmetic. Not compatible with primitive operators (`+`, `-`, `*`, `/`) — must use method calls. Not automatically `hashCode()`-safe across different scales (see `equals()` vs `compareTo()`).

## 3. Real Code / Real Example

```java
import java.math.*;
import java.util.*;

public class BigDecimalDemo {

    public static void main(String[] args) {
        // === CONSTRUCTION — ALWAYS use String constructor ===
        BigDecimal bd1 = new BigDecimal("0.1");       // ✅ Precise: 0.1
        BigDecimal bd2 = new BigDecimal(0.1);          // ❌ Imprecise: 0.10000000000000000555...
        BigDecimal bd3 = BigDecimal.valueOf(0.1);      // ✅ Precise (uses String internally)
        
        System.out.println("String constructor: " + bd1);  // 0.1
        System.out.println("double constructor: " + bd2);  // 0.10000000000000000555...
        System.out.println("valueOf: " + bd3);             // 0.1

        // === ARITHMETIC ===
        BigDecimal a = new BigDecimal("10.50");
        BigDecimal b = new BigDecimal("3.20");
        
        BigDecimal sum = a.add(b);                          // 13.70
        BigDecimal diff = a.subtract(b);                    // 7.30
        BigDecimal product = a.multiply(b);                 // 33.6000 (scale = 4)
        BigDecimal quotient = a.divide(b, 2, RoundingMode.HALF_UP); // 3.28
        
        System.out.println("Sum: " + sum);
        System.out.println("Quotient: " + quotient);

        // === COMPARISON TRAP ===
        BigDecimal x = new BigDecimal("1.0");
        BigDecimal y = new BigDecimal("1.00");
        
        System.out.println("x.equals(y): " + x.equals(y));     // false! (scale differs: 1 vs 2)
        System.out.println("x.compareTo(y): " + x.compareTo(y)); // 0 (correct: equal by value)
        
        // === ROUNDING ===
        BigDecimal price = new BigDecimal("19.995");
        BigDecimal rounded = price.setScale(2, RoundingMode.HALF_UP);
        System.out.println("Rounded price: " + rounded); // 20.00

        // === DIVISION WITHOUT ROUNDING MODE — WILL THROW ===
        try {
            BigDecimal one = new BigDecimal("1");
            BigDecimal three = new BigDecimal("3");
            BigDecimal result = one.divide(three); // ArithmeticException!
        } catch (ArithmeticException e) {
            System.out.println("Division failed: " + e.getMessage());
            // "Non-terminating decimal expansion; no exact representable decimal result"
        }
        
        // === CORRECT DIVISION ===
        BigDecimal result = BigDecimal.ONE.divide(
            BigDecimal.valueOf(3), 10, RoundingMode.HALF_UP
        );
        System.out.println("Safe division: " + result); // 0.3333333333

        // === STRIP TRAILING ZEROS ===
        BigDecimal withZeros = new BigDecimal("10.5000");
        System.out.println("With zeros: " + withZeros);            // 10.5000
        System.out.println("Strip zeros: " + withZeros.stripTrailingZeros()); // 10.5
    }
}
```

Expected output:
```
String constructor: 0.1
double constructor: 0.1000000000000000055511151231257827021181583404541015625
valueOf: 0.1
Sum: 13.70
Quotient: 3.28
x.equals(y): false
x.compareTo(y): 0
Rounded price: 20.00
Division failed: Non-terminating decimal expansion; no exact representable decimal result
Safe division: 0.3333333333
With zeros: 10.5000
Strip zeros: 10.5
```

## 4. What Happens Internally

### Internal Representation

```java
// BigDecimal stores:
// - intVal: BigInteger  (the unscaled integer value)
// - scale: int         (number of decimal places)
// - precision: int     (number of significant digits, lazily computed)

// Example: new BigDecimal("123.45")
// intVal = BigInteger.valueOf(12345)
// scale = 2
// Value = 12345 × 10^(-2) = 123.45
```

### Why `new BigDecimal(0.1)` is imprecise

The `double` literal `0.1` is NOT exactly representable in binary floating-point:
```java
0.1 in IEEE 754 double = 0.1000000000000000055511151231257827021181583404541015625
// When passed to BigDecimal(double) constructor, this EXACT value is stored.
// Most of those extra digits are noise.
```

`BigDecimal.valueOf(0.1)` avoids this by:
1. Converting `0.1` to `String` (`Double.toString(0.1)` gives `"0.1"`)
2. Parsing the string to construct BigDecimal

### Division Algorithm

```java
// one.divide(three, 10, RoundingMode.HALF_UP)
// 1. Compute 1 / 3 = 0.33333... (infinite repeating)
// 2. Scale to 10 decimal places: 0.3333333333
// 3. Check next digit for rounding: 3 (next digit = 3 < 5 → no round up)
// 4. Result: 0.3333333333 with scale 10
// Without scale/rounding: ArithmeticException because result is infinite non-terminating
```

### `equals()` vs `compareTo()` — The Scale Problem

```java
BigDecimal a = new BigDecimal("1.0");   // scale=1, intVal=10
BigDecimal b = new BigDecimal("1.00");  // scale=2, intVal=100

// equals(): compares both value AND scale
// 10 × 10^(-1) = 1.0
// 100 × 10^(-2) = 1.00
// intVal differ (10 vs 100) → equals() returns false

// compareTo(): compares only numeric value
// 10 × 10^(-1) = 1.0
// 100 × 10^(-2) = 1.00
// Numeric value is 1.0 = 1.00 → compareTo() returns 0
```

## 5. Tricky Interview Cases

**Case 1 — `equals()` != `compareTo()` with HashSet**
```java
Set<BigDecimal> set = new HashSet<>();
set.add(new BigDecimal("1.0"));
set.add(new BigDecimal("1.00"));
System.out.println(set.size()); // 2! Because equals() returns false (scale differs)

Set<BigDecimal> treeSet = new TreeSet<>();
treeSet.add(new BigDecimal("1.0"));
treeSet.add(new BigDecimal("1.00"));
System.out.println(treeSet.size()); // 1! TreeSet uses compareTo(), not equals()
```
Violation: `BigDecimal`'s `compareTo()` is NOT consistent with `equals()`. This breaks the `Set` contract when mixing `HashSet` and `TreeSet`.

**Case 2 — `new BigDecimal("0.0")` vs `BigDecimal.ZERO`**
```java
BigDecimal zero1 = new BigDecimal("0.0");  // scale=1
BigDecimal zero2 = BigDecimal.ZERO;         // scale=0

System.out.println(zero1.equals(zero2));   // false! Different scales
System.out.println(zero1.compareTo(zero2)); // 0 (same numeric value)
```

**Case 3 — Division by zero**
```java
BigDecimal.ONE.divide(BigDecimal.ZERO, 2, RoundingMode.HALF_UP);
// Throws ArithmeticException: Division by zero
// Even with scale and rounding mode, division by zero is undefined
```

**Case 4 — Negative scale**
```java
BigDecimal bd = new BigDecimal("12300");
BigDecimal scaled = bd.setScale(-2); // scale = -2
System.out.println(scaled); // 1.23E+4 (12300 → 123 × 10^2)
// Negative scale means rounding to powers of 10
// -1 = round to nearest 10, -2 = round to nearest 100
```

**Case 5 — BigDecimal in HashMap keys**
```java
Map<BigDecimal, String> map = new HashMap<>();
map.put(new BigDecimal("1.0"), "value1");
map.put(new BigDecimal("1.00"), "value2");  // Different key! (equals returns false)
System.out.println(map.get(new BigDecimal("1.0")));  // "value1"
System.out.println(map.get(new BigDecimal("1.00"))); // "value2"

// To avoid this, normalize scale before using as key:
BigDecimal normalized = new BigDecimal("1.00").stripTrailingZeros();
// normalized = 1E+0 (scale = 0)
```

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|------|
| `new BigDecimal(0.1)` with double constructor | Creates imprecise value | Use `new BigDecimal("0.1")` or `BigDecimal.valueOf(0.1)` |
| Using `equals()` instead of `compareTo()` | `1.0` != `1.00` (scale mismatch) | Use `compareTo()` for business value comparison |
| Division without scale/rounding mode | `ArithmeticException` for non-terminating decimals | Always provide scale and `RoundingMode` |
| Using `==` for BigDecimal comparison | Never works — compares references | Use `compareTo()` or `equals()` |
| Mutating BigDecimal (it's immutable!) | BigDecimal is immutable — operations return new objects | Assign result: `a = a.add(b)` |
| Storing money as `double` | Precision errors accumulate | Always store money as `BigDecimal` (or `long` in cents) |
| Not normalizing scale in HashMap keys | Same numeric value maps to different keys | `stripTrailingZeros()` before using as key |
| Using `BigDecimal` for integers in performance paths | Object overhead and slower arithmetic | Use `long` for integer counters, `BigDecimal` only for decimal values |

## 7. Production Usage

**Payment amount handling:**
```java
public class Money {
    private final BigDecimal amount;
    private final Currency currency;
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    
    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount.setScale(SCALE, ROUNDING); // Normalize scale
        this.currency = currency;
    }
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money multiply(BigDecimal multiplier) {
        return new Money(
            this.amount.multiply(multiplier).setScale(SCALE, ROUNDING),
            this.currency
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0  // compareTo! NOT equals
            && currency.equals(money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency); // Normalize for hash
    }
}
```

**Spring JPA entity with BigDecimal:**
```java
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(precision = 19, scale = 2) // 19 digits total, 2 decimal places
    private BigDecimal amount;
    
    @Column(precision = 19, scale = 8) // For crypto/forex rates
    private BigDecimal exchangeRate;
    
    // JPA maps BigDecimal to DECIMAL(19,2) in database
    // This ensures database-level precision matches Java-level precision
}
```

**Tax calculation with rounding:**
```java
public class TaxCalculator {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.08"); // 8%
    
    public BigDecimal calculateTax(BigDecimal subtotal) {
        // Multiply price × tax rate, then round to 2 decimal places
        return subtotal.multiply(TAX_RATE)
            .setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateTotal(BigDecimal subtotal) {
        return subtotal.add(calculateTax(subtotal));
    }
}
```

## 8. Advanced Details

- **Performance**: `BigDecimal` operations are 10-100x slower than primitive `double` or `long`. For high-frequency trading or real-time analytics, consider `long` (storing cents) with manual rounding.
- **Memory**: Each `BigDecimal` object is ~40-80 bytes. Creating many in loops causes GC pressure. `BigDecimal.valueOf()` caches values for `0` through `10`.
- **`MathContext`**: `new MathContext(10, RoundingMode.HALF_UP)` controls both precision and rounding. `BigDecimal.ONE.divide(BigDecimal.valueOf(3), new MathContext(10, RoundingMode.HALF_UP))` = `0.3333333333`.
- **`BigDecimal.ROUND_*` constants (deprecated)**: Java 9 deprecated the older `ROUND_HALF_UP` constants. Use `RoundingMode.HALF_UP` enum instead.
- **`BigDecimal` and `doubleValue()`**: `new BigDecimal("0.1").doubleValue()` returns the imprecise `double 0.1` representation. For JSON serialization, always serialize as String.
- **`BigDecimal.ZERO`, `ONE`, `TEN`**: Predefined constants for common values. `BigDecimal.valueOf(long)` is preferred over `new BigDecimal(long)`.
- **`toPlainString()` vs `toString()`**: `new BigDecimal("1E+2").toString()` = `"1E+2"`. `toPlainString()` = `"100"`. Use `toPlainString()` for display.
- **Java 9+ `BigDecimal` improvements**: Better `sqrt()` method, `MathContext` precision improvements.

## 9. Interview Questions And Answers

### Beginner
Q: Why should you use `BigDecimal` instead of `double` for monetary values?
A: `double` uses binary floating-point arithmetic, which cannot represent decimal fractions like `0.1` exactly. For example, `0.1 + 0.2` gives `0.30000000000000004` instead of `0.3`. In financial applications, these tiny errors accumulate into significant discrepancies. `BigDecimal` represents values as integers with a scale, enabling exact decimal arithmetic. Always use `BigDecimal` for money, tax, interest rates, and any calculation requiring precise decimal results.

### Intermediate
Q: What is the difference between `BigDecimal.equals()` and `BigDecimal.compareTo()`? When would you use each?
A: `equals()` compares both numeric value AND scale. `new BigDecimal("1.0").equals(new BigDecimal("1.00"))` returns `false` because scale differs (1 vs 2). `compareTo()` compares only numeric value — `1.0` and `1.00` are equal by value. 

**Use `compareTo()`** for: business logic comparisons, sorting, `TreeSet`, `TreeMap` — value-based equality.
**Use `equals()`** for: `HashMap`/`HashSet` keys, strict identity (rarely needed). 

**Important**: Because `compareTo()` is inconsistent with `equals()`, be careful mixing `HashSet` and `TreeSet`. Same numeric value with different scales may be treated as different or same depending on the collection.

### Senior
Q: In a payment system, you receive amounts from an external API as `String` values. Some have scale 8 (e.g., `"0.12345678"`) for crypto transactions. Your database stores `DECIMAL(19,2)`. How do you safely handle this conversion?
A: 
```java
public BigDecimal normalizeAmount(String rawAmount) {
    // Parse String to preserve exact precision
    BigDecimal amount = new BigDecimal(rawAmount);
    // Round to 2 decimal places for storage
    return amount.setScale(2, RoundingMode.HALF_UP);
}
```

**Considerations:**
1. **Rounding mode**: `HALF_UP` is standard for payments. Document if `HALF_EVEN` (banker's rounding) is needed.
2. **Loss tracking**: Log the rounding difference for reconciliation: `roundingLoss = original.subtract(normalized).abs()`.
3. **Scale mismatch errors**: If the input `"0.12345678"` represents 0.12345678 units, rounding to 2 decimal places loses 0.00345678 per transaction. For 1M transactions, that's ~3456 units lost. Ensure business stakeholders approve the rounding policy.
4. **Alternative**: Store in database as `DECIMAL(19,8)` for crypto and `DECIMAL(19,2)` for fiat — use separate columns.

### Tricky
Q: Predict the output and explain:
```java
public class BigDecimalTricky {
    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");
        BigDecimal c = new BigDecimal("2");
        
        System.out.println(a.equals(b) + " " + a.compareTo(b));
        System.out.println(a.equals(c) + " " + a.compareTo(c));
        System.out.println(b.equals(c) + " " + b.compareTo(c));
        
        Set<BigDecimal> hashSet = new HashSet<>();
        hashSet.add(a); hashSet.add(b); hashSet.add(c);
        System.out.println("HashSet size: " + hashSet.size());
        
        Set<BigDecimal> treeSet = new TreeSet<>();
        treeSet.add(a); treeSet.add(b); treeSet.add(c);
        System.out.println("TreeSet size: " + treeSet.size());
    }
}
```
A: Output is:
```
false 0    // a.equals(b): scale 1 vs 2 → false. compareTo: same value → 0
false 0    // a.equals(c): scale 1 vs 0 → false. compareTo: same value → 0
false 0    // b.equals(c): scale 2 vs 0 → false. compareTo: same value → 0
HashSet size: 3    // All three have different scales — treated as distinct objects
TreeSet size: 1    // All three have same value — treated as equal by compareTo
```

Explanation: This shows the fundamental inconsistency in `BigDecimal`. `HashSet` uses `equals()+hashCode()` → 3 elements (different scales). `TreeSet` uses `compareTo()` → 1 element (same value). In production, normalize scale before putting in any collection: `amount.setScale(2, RoundingMode.HALF_UP)`.

## 10. Final 30-Second Answer

`BigDecimal` = exact decimal arithmetic. **Always** use `String` constructor or `valueOf()`, never `double`. `compareTo()` for value comparison (NOT `equals()` — scale mismatch). Always specify scale + `RoundingMode` for division. Normalize scale before use in collections. Immutable — operations return new objects. Essential for all financial calculations.