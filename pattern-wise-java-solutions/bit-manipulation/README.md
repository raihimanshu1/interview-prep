# Bit Manipulation

> **Core Pattern:** Use bitwise operators (&, |, ^, ~, <<, >>) for efficient computation and state tracking.  
> **Learning Path:** Basic bit operations → XOR tricks → Counting bits → Bitmask for subsets.

---

## 📖 Conceptual Foundation

### Key Bitwise Operators
| Operator | Name | Use Case |
|----------|------|----------|
| `&` | AND | Check if bit is set, mask |
| `\|` | OR | Set a bit |
| `^` | XOR | Toggle, find missing/unique |
| `~` | NOT | Complement |
| `<<` | Left Shift | Multiply by 2ⁿ |
| `>>` | Right Shift | Divide by 2ⁿ |

### XOR Tricks
```
x ^ 0 = x
x ^ x = 0
x ^ y ^ x = y         // find unique element
x & (x-1) = 0         // power of two check (clears lowest set bit)
```

### Bit Tricks
```java
// Check if k-th bit is set
if ((num & (1 << k)) != 0) { ... }

// Set k-th bit
num |= (1 << k);

// Clear k-th bit
num &= ~(1 << k);

// Toggle k-th bit
num ^= (1 << k);

// Isolate rightmost set bit
rightmost = num & (-num);

// Count set bits (Brian Kernighan)
int count = 0;
while (num != 0) { num &= (num - 1); count++; }
```

---

## 📚 Learning Order

### Phase 1: Basic XOR & Bit Counting

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Single Number** | [SingleNumber.java](SingleNumber.java) | XOR all — every duplicate cancels out | 🟢 Easy |
| 2 | **Missing Number** | [BitMissingNumber.java](BitMissingNumber.java) | XOR index + value, OR sum formula | 🟢 Easy |
| 3 | **Number of 1 Bits** | [NumberOf1Bits.java](NumberOf1Bits.java) | `n & (n-1)` trick to count set bits | 🟢 Easy |
| 4 | **Counting Bits** | [CountingBits.java](CountingBits.java) | `dp[i] = dp[i >> 1] + (i & 1)` | 🟢 Easy |
| 5 | **Power of Two** | [PowerOfTwo.java](PowerOfTwo.java) | `n > 0 && (n & (n-1)) == 0` | 🟢 Easy |
| 6 | **Reverse Bits** | [ReverseBits.java](ReverseBits.java) | Extract LSB, build result by shifting | 🟢 Easy |

### Phase 2: Advanced XOR & Bitmask

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 7 | **Single Number II** | [SingleNumberII.java](SingleNumberII.java) | Count bits % 3 — each bit appears 3× except unique | 🟡 Medium |
| 8 | **Subsets Using Bitmask** | [SubsetsUsingBitmask.java](SubsetsUsingBitmask.java) | `for (int mask = 0; mask < (1 << n); mask++)` — O(2ⁿ) | 🟡 Medium |

---

## 🔑 Key Insights

1. **XOR** = unique element finder: `x ^ x = 0`, `x ^ 0 = x`
2. **Power of Two** = only one bit set: `n & (n-1) == 0`
3. **Count bits** = `dp[i] = dp[i >> 1] + (i & 1)` OR `n & (n-1)` loop
4. **Bitmask subsets** = iterate from 0 to (2ⁿ-1), each mask is a subset
5. **Single Number II** = count bits per position % k

---

## 🎯 Practice Checklist

- [ ] Phase 1: XOR & counting basics
- [ ] Phase 2: Bitmask subsets & advanced XOR