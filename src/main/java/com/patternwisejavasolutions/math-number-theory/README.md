# Math & Number Theory

> **Core Pattern:** Mathematical reasoning — prime numbers, GCD/LCM, modular arithmetic, exponentiation.  
> **Learning Path:** Primes (Sieve) → GCD/LCM → Exponentiation → Modular arithmetic.

---

## 📖 Conceptual Foundation

### Key Concepts
| Concept | Technique | Use Case |
|---------|-----------|----------|
| Primes | Sieve of Eratosthenes | Count primes up to N |
| GCD/LCM | Euclidean algorithm | Fraction reduction, ratio |
| Modular Exponentiation | Fast pow | (a^b) % mod for large b |
| Sieve | Boolean array marking | O(n log log n) |

### GCD (Euclidean Algorithm)
```java
int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
}
```

### Sieve of Eratosthenes
```java
boolean[] isPrime = new boolean[n + 1];
Arrays.fill(isPrime, true);
isPrime[0] = isPrime[1] = false;
for (int i = 2; i * i <= n; i++) {
    if (isPrime[i]) {
        for (int j = i * i; j <= n; j += i) isPrime[j] = false;
    }
}
```

### Fast Exponentiation (Binary Exponentiation)
```java
long pow(long base, long exp, long mod) {
    long result = 1;
    while (exp > 0) {
        if ((exp & 1) == 1) result = (result * base) % mod;
        base = (base * base) % mod;
        exp >>= 1;
    }
    return result;
}
```

---

## 📚 Learning Order

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **GCD and LCM** | [GcdAndLcm.java](GcdAndLcm.java) | Euclidean algorithm: `gcd(a,b) = gcd(b, a%b)` | 🟢 Easy |
| 2 | **Count Primes (Sieve)** | [CountPrimes.java](CountPrimes.java) | Sieve of Eratosthenes O(n log log n) | 🟡 Medium |
| 3 | **Sieve of Eratosthenes** | [SieveOfEratosthenes.java](SieveOfEratosthenes.java) | Same, separate implementation | 🟡 Medium |
| 4 | **Pow(x, n)** | [PowXN.java](PowXN.java) | Fast exponentiation (handle negative n) | 🟡 Medium |
| 5 | **Modular Exponentiation** | [ModularExponentiation.java](ModularExponentiation.java) | Binary exponentiation with mod | 🟡 Medium |
| 6 | **Excel Sheet Column Title** | [ExcelSheetColumnTitle.java](ExcelSheetColumnTitle.java) | Base-26 conversion (1-indexed) | 🟢 Easy |

---

## 🔑 Key Insights

1. **Sieve** = mark multiples starting from i² (not 2i), O(n log log n)
2. **GCD** = Euclidean algorithm — recursive modulo
3. **Fast exponentiation** = binary exponentiation: O(log n)
4. **Excel column** = base-26 with `A=1`, so `num--` before modulo
5. **LCM(a,b)** = `a / gcd(a,b) * b` (prevent overflow by dividing first)

---

## 🎯 Practice Checklist

- [ ] GCD/LCM
- [ ] Sieve (Count Primes)
- [ ] Fast Exponentiation (Pow, Modular)
- [ ] Base conversion (Excel)