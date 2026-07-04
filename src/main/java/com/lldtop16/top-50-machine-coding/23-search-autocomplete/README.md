# 🔍 Problem 23: Search Autocomplete (Trie-Based)

> **Difficulty**: ⭐⭐ | **Company Fit**: Google, Microsoft, Amazon  
> **Est. Time**: 60 min | **Patterns**: Trie, PriorityQueue, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design autocomplete — like Google Search suggestions."

**What the interviewer tests**:
```
1. Can you build a Trie (prefix tree) for fast prefix search?
2. Can you rank suggestions by frequency/recency?
3. Can you handle millions of queries efficiently?
4. Can you personalize results per user?
```

### Step 2: The "Aha!" Moment

The key insight: **Trie + Priority Queue at each node.**

```
User types: "pro"

Trie traversal:
  root → 'p' → 'r' → 'o'
  
At 'o' node, we need to find TOP suggestions:
  - "program" (1000 searches)
  - "product" (800 searches)
  - "project" (600 searches)
  - "profile" (400 searches)

Each Trie node stores a PriorityQueue of top-k suggestions.
When user types "pro", we:
1. Navigate to 'o' node in O(length of prefix)
2. Return the pre-computed top-k suggestions in O(1)
```

### Step 3: How to make it fast?

```
INSERT a word: O(L) where L = word length
SEARCH prefix: O(P + K) where P = prefix length, K = suggestions returned
AUTOCOMPLETE: O(P + K) — just traverse to prefix node and read top-k

Space: O(N * L) where N = number of words, L = average length
```

---

## 💻 Core Implementation

```java
package com.autocomplete;

import java.util.*;

/**
 * INTUITION: The AutocompleteService is the entry point.
 * 
 * Uses a Trie where each node contains:
 * - Children map (char → TrieNode)
 * - A PriorityQueue of top-k suggestions sorted by frequency
 * 
 * When a word is inserted, we update all ancestor nodes' priority queues.
 */
public class AutocompleteService {
    private final TrieNode root;
    private final int maxSuggestions;  // Top-k, e.g., 5

    public AutocompleteService(int maxSuggestions) {
        this.root = new TrieNode();
        this.maxSuggestions = maxSuggestions;
    }

    /**
     * INTUITION: Insert a word with its frequency.
     * 
     * 1. Traverse/create nodes for each character
     * 2. At the end, mark as word-ending and store frequency
     * 3. Backtrack? No - we update PQ during forward traversal
     * 
     * @param word The word to insert
     * @param frequency How often this word is searched
     */
    public void insert(String word, int frequency) {
        TrieNode current = root;
        
        for (char c : word.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
            // Add this word to this node's suggestion list
            current.addSuggestion(word, frequency);
        }
        
        current.isEndOfWord = true;
    }

    /**
     * INTUITION: Get autocomplete suggestions for a prefix.
     * 
     * 1. Navigate to the node representing the prefix
     * 2. Return the top-k suggestions stored at that node
     * 
     * @param prefix The typed prefix, e.g., "pro"
     * @return List of suggestions, e.g., ["program", "product", "project"]
     */
    public List<String> getSuggestions(String prefix) {
        TrieNode node = root;
        
        // Navigate to prefix node
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return Collections.emptyList(); // No such prefix
            }
        }
        
        // Return top-k suggestions from this node
        return node.getTopSuggestions(maxSuggestions);
    }
}
```

```java
package com.autocomplete;

import java.util.*;

/**
 * INTUITION: Each TrieNode represents a character position.
 * 
 * The magic is in the `suggestions` PriorityQueue.
 * It keeps the TOP-K most frequent words that pass through this node.
 * 
 * Why PriorityQueue?
 * - Insertion: O(log K)
 * - Removal of smallest: O(log K)
 * - Get top-k: O(K)
 * 
 * We keep only K items, so space is bounded.
 */
class TrieNode {
    // Children: 'a' → child node, 'b' → child node, etc.
    Map<Character, TrieNode> children = new HashMap<>();
    
    // Top-k suggestions for this prefix
    PriorityQueue<WordFrequency> suggestions;
    
    boolean isEndOfWord = false;

    TrieNode() {
        // Min-heap: smallest frequency at top (for easy removal)
        suggestions = new PriorityQueue<>(Comparator.comparingInt(w -> w.frequency));
    }

    /**
     * INTUITION: Add a word to suggestions.
     * 
     * If queue has < K items, just add.
     * If queue has K items and new word has higher frequency than min, replace min.
     * Otherwise, ignore (not in top-k).
     */
    void addSuggestion(String word, int frequency) {
        WordFrequency wf = new WordFrequency(word, frequency);
        
        if (suggestions.size() < 5) {  // K = 5
            suggestions.offer(wf);
        } else if (frequency > suggestions.peek().frequency) {
            // Replace the least frequent
            suggestions.poll();
            suggestions.offer(wf);
        }
    }

    List<String> getTopSuggestions(int k) {
        List<WordFrequency> result = new ArrayList<>(suggestions);
        // Sort descending by frequency for display
        result.sort((a, b) -> Integer.compare(b.frequency, a.frequency));
        
        List<String> words = new ArrayList<>();
        for (WordFrequency wf : result) {
            words.add(wf.word);
        }
        return words;
    }
}

class WordFrequency {
    String word;
    int frequency;

    WordFrequency(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }
}
```

```java
package com.autocomplete;

import java.util.*;

/**
 * INTUITION: Personalization wrapper.
 * 
 * Combines global suggestions with user-specific history.
 */
public class PersonalizedAutocomplete {
    private final AutocompleteService globalService;
    private final Map<String, List<String>> userHistory; // userId → recent searches

    public PersonalizedAutocomplete(AutocompleteService globalService) {
        this.globalService = globalService;
        this.userHistory = new ConcurrentHashMap<>();
    }

    public List<String> getSuggestions(String userId, String prefix) {
        // Get global suggestions
        List<String> global = globalService.getSuggestions(prefix);
        
        // Get user's recent searches matching prefix
        List<String> personal = getUserSuggestions(userId, prefix);
        
        // Merge: personal first (boosted), then global
        Set<String> seen = new HashSet<>(personal);
        List<String> result = new ArrayList<>(personal);
        
        for (String s : global) {
            if (seen.add(s)) {  // Not already in personal
                result.add(s);
            }
        }
        
        // Record this search for future personalization
        recordSearch(userId, prefix);
        
        return result;
    }

    private List<String> getUserSuggestions(String userId, String prefix) {
        List<String> history = userHistory.getOrDefault(userId, Collections.emptyList());
        List<String> matches = new ArrayList<>();
        for (String search : history) {
            if (search.startsWith(prefix)) {
                matches.add(search);
            }
        }
        return matches;
    }

    private void recordSearch(String userId, String prefix) {
        userHistory.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                   .add(prefix);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle typos?"
> "Use Levenshtein distance for fuzzy matching. Or use BK-tree for efficient 'find similar words'. Limit to 1-2 character edits."

### Q2: "How to handle trending/popular searches?"
> "Maintain a separate TrendingSearcher that tracks hot queries. Combine with Trie results: trending items get a frequency boost."

### Q3: "How to update frequencies in real-time?"
> "Increment frequency counter asynchronously via Kafka. Batch updates to Trie every minute to avoid write amplification."

### Q4: "How to support multi-word suggestions?"
> "Index n-grams (pairs/triples of words). When user types first word, suggest common second words."

### Q5: "Memory concerns with millions of words?"
> "Compress Trie using double-array trie or succinct data structures. Or use Ternary Search Tree (TST) which uses less memory."