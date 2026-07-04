# Strings

> **Core Pattern:** Character frequency counting, two-pointer on string, palindrome checks.  
> **Learning Path:** Anagram detection → palindrome variations → substring manipulation.

---

## 📖 Conceptual Foundation

### String Algorithm Techniques
| Technique | When to Use | Example |
|-----------|-------------|---------|
| Character frequency array `int[26]` | Need to compare contents ignoring order | Valid Anagram, Group Anagrams |
| Two-pointer from ends | Palindrome check | Valid Palindrome |
| Expand around center | Find palindromic substrings | Longest Palindromic Substring |
| Sliding window + frequency | Substring anagram/search | n/a (covered in sliding-window) |

### Frequency Array Template
```java
int[] freq = new int[26]; // for lowercase letters
for (char c : str.toCharArray()) {
    freq[c - 'a']++;
}
```

---

## 📚 Learning Order

### Phase 1: Core (Anagrams & Prefix)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Valid Anagram** | [core/ValidAnagram.java](core/ValidAnagram.java) | `int[26]` frequency OR sort & compare | 🟢 Easy |
| 2 | **Group Anagrams** | [core/GroupAnagrams.java](core/GroupAnagrams.java) | `HashMap<String, List>` with sorted key | 🟡 Medium |
| 3 | **Longest Common Prefix** | [core/LongestCommonPrefix.java](core/LongestCommonPrefix.java) | Horizontal scanning / Trie | 🟢 Easy |

### Phase 2: String Manipulation

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 4 | **Isomorphic Strings** | [manipulation/IsomorphicStrings.java](manipulation/IsomorphicStrings.java) | Two HashMap bijection mapping `char→char` | 🟢 Easy |
| 5 | **String Compression** | [manipulation/StringCompression.java](manipulation/StringCompression.java) | In-place counting + overwriting | 🟡 Medium |
| 6 | **Repeated Substring Pattern** | [manipulation/RepeatedSubstringPattern.java](manipulation/RepeatedSubstringPattern.java) | `s + s` trick — remove first/last, check contains | 🟢 Easy |
| 7 | **Implement strStr()** | [manipulation/ImplementStrStr.java](manipulation/ImplementStrStr.java) | KMP or sliding window substring search | 🟢 Easy |

### Phase 3: Palindrome

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 8 | **Valid Palindrome** | [palindrome/ValidPalindrome.java](palindrome/ValidPalindrome.java) | Two-pointer from ends, skip non-alphanumeric | 🟢 Easy |
| 9 | **Palindrome** | [palindrome/Palindrome.java](palindrome/Palindrome.java) | Basic palindrome check | 🟢 Easy |
| 10 | **String Valid Palindrome II** | [palindrome/StringValidPalindromeII.java](palindrome/StringValidPalindromeII.java) | Two-pointer with at-most-one-delete tolerance | 🟢 Easy |
| 11 | **Palindromic Substrings** | [palindrome/PalindromicSubstrings.java](palindrome/PalindromicSubstrings.java) | Expand around center (count all) | 🟡 Medium |
| 12 | **Longest Palindromic Substring** | [palindrome/LongestPalindromicSubstring.java](palindrome/LongestPalindromicSubstring.java) | Expand around center (track max) | 🟡 Medium |

---

## 🔑 Key Insights

1. **Anagram check** → frequency array or sorted string comparison
2. **Palindrome check** → two-pointer from ends
3. **Find palindromes** → expand around center (odd/even length)
4. **String mapping** → two HashMaps for bijection
5. **`int[26]`** faster than `HashMap<Character, Integer>` for lowercase letters

---

## 🎯 Practice Checklist

- [ ] Phase 1: Core problems (Anagrams, Prefix)
- [ ] Phase 2: Manipulation techniques
- [ ] Phase 3: Palindrome patterns