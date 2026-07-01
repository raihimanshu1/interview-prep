# Tries

> **Core Pattern:** Prefix tree for efficient string prefix matching and dictionary lookups.  
> **Learning Path:** Basic Trie implementation → Word search → Advanced matching.

---

## 📖 Conceptual Foundation

### Trie Node Structure
```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];  // or HashMap
    boolean isEnd;
}
```

### Trie Operations
```
Insert:   Start from root, add child nodes for each char, mark end
Search:   Traverse chars, check if path exists and ends at isEnd=true
Prefix:   Traverse chars, check if path exists (don't need isEnd)
```

### Trie Template
```java
class Trie {
    TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) node.children[c - 'a'] = new TrieNode();
            node = node.children[c - 'a'];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) return false;
            node = node.children[c - 'a'];
        }
        return node.isEnd;
    }

    public boolean startsWith(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (node.children[c - 'a'] == null) return false;
            node = node.children[c - 'a'];
        }
        return true;
    }
}
```

---

## 📚 Learning Order

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Implement Trie (Prefix Tree)** | [ImplementTriePrefixTree.java](ImplementTriePrefixTree.java) | Basic insert, search, startsWith | 🟡 Medium |
| 2 | **Design Add and Search Words** | [DesignAddAndSearchWordsDataStructure.java](DesignAddAndSearchWordsDataStructure.java) | Trie + DFS for `.` wildcard | 🟡 Medium |
| 3 | **Replace Words** | [ReplaceWords.java](ReplaceWords.java) | Insert roots in Trie, replace word with shortest root | 🟡 Medium |
| 4 | **Word Search II** | [WordSearchII.java](WordSearchII.java) | Trie + DFS on grid (prune with isEnd) | 🔴 Hard |
| 5 | **Palindrome Pairs** | [PalindromePairs.java](PalindromePairs.java) | Trie + palindrome check on reverse | 🔴 Hard |

---

## 🔑 Key Insights

1. **Trie = Prefix Tree** — optimal for prefix matching, autocomplete, spell check
2. **Wildcard search** → DFS to handle `.` (try all children)
3. **Word Search II** → build Trie of all words, DFS on grid, prune when Trie path ends
4. **Palindromic pairs** → store words in Trie reversed, check palindrome on remaining suffix

---

## 🎯 Practice Checklist

- [ ] Implement Trie (basics)
- [ ] Add & Search with wildcard
- [ ] Replace Words
- [ ] Word Search II
- [ ] Palindrome Pairs