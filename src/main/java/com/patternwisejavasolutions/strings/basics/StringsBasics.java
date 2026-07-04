/**
 * STRINGS — Basics & Warmup
 * 
 * Before jumping into problems, master these fundamentals:
 * 1. String immutability & StringBuilder/StringBuffer
 * 2. Character frequency counting (hashmap & array)
 * 3. Anagram detection patterns
 * 4. Palindrome patterns
 * 5. Sliding window on strings
 * 6. Two-pointer string patterns
 */

package com.patternwisejavasolutions.strings.basics;




import java.util.*;
public class StringsBasics {
    // ==========================================
    // 1. STRING IMMUTABILITY & BUILDERS
    // ==========================================
    // String is immutable in Java!
    // StringBuilder (not thread-safe) / StringBuffer (thread-safe)
    
    // Reverse string (using StringBuilder)
    public static String reverseString(String s) {
        return new StringBuilder(s).reverse().toString();
    }
    
    // Reverse string manually
    public static String reverseStringManual(String s) {
        char[] chars = s.toCharArray();
        int left = 0, right = chars.length - 1;
        while (left < right) {
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            left++;
            right--;
        }
        return new String(chars);
    }
    
    // Check if string is palindrome (two-pointer)
    public static boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }
    
    // Check if string is palindrome (ignore non-alphanumeric, case-insensitive)
    public static boolean isPalindromeAlphanumeric(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) left++;
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) right--;
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    // ==========================================
    // 2. CHARACTER FREQUENCY COUNTING
    // ==========================================
    
    // Using HashMap (any character set)
    public static Map<Character, Integer> frequencyMap(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }
    
    // Using int[26] for lowercase letters only (faster, O(1) space)
    public static int[] frequencyArray(String s) {
        int[] freq = new int[26];
        for (char c : s.toCharArray()) {
            freq[c - 'a']++;
        }
        return freq;
    }
    
    // Using int[128] for ASCII
    public static int[] frequencyArrayASCII(String s) {
        int[] freq = new int[128];
        for (char c : s.toCharArray()) {
            freq[c]++;
        }
        return freq;
    }
    // ==========================================
    // 3. ANAGRAM PATTERNS
    // ==========================================
    
    // Check if two strings are anagrams
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        
        int[] freq = new int[26];
        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i) - 'a']++;
            freq[t.charAt(i) - 'a']--;
        }
        
        for (int count : freq) {
            if (count != 0) return false;
        }
        return true;
    }
    
    // Group anagrams together
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }
    // ==========================================
    // 4. SUBSTRING PATTERNS
    // ==========================================
    
    // Check if s2 contains s1 as substring
    public static boolean isSubstring(String s1, String s2) {
        return s2.contains(s1);
    }
    
    // Count occurrences of substring (non-overlapping)
    public static int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();  // non-overlapping
        }
        return count;
    }
    
    // Longest common prefix among strings
    public static String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";
        
        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            while (strs[i].indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) return "";
            }
        }
        return prefix;
    }
    // ==========================================
    // 5. SLIDING WINDOW ON STRINGS
    // ==========================================
    
    // Longest substring without repeating characters
    // O(n) time using sliding window + hashmap
    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int maxLen = 0;
        int left = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            
            // If character seen before, move left boundary
            if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
                left = lastSeen.get(c) + 1;
            }
            
            lastSeen.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
    // ==========================================
    // 6. BASIC STRING PROCESSING
    // ==========================================
    
    // Most frequent character
    public static char mostFrequentChar(String s) {
        int[] freq = frequencyArrayASCII(s);
        int max = 0;
        char result = ' ';
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] > max) {
                max = freq[i];
                result = (char) i;
            }
        }
        return result;
    }
    
    // Remove vowels from string
    public static String removeVowels(String s) {
        StringBuilder sb = new StringBuilder();
        String vowels = "aeiouAEIOU";
        for (char c : s.toCharArray()) {
            if (vowels.indexOf(c) == -1) sb.append(c);
        }
        return sb.toString();
    }
    
    // Reverse words in a sentence
    public static String reverseWords(String s) {
        String[] words = s.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]);
            if (i > 0) sb.append(" ");
        }
        return sb.toString();
    }
    
    // Character case conversion
    public static String toLowerCase(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c >= 'A' && c <= 'Z') sb.append((char) (c + 32));
            else sb.append(c);
        }
        return sb.toString();
    }
    // ==========================================
    // MAIN — Test everything
    // ==========================================
    
    public static void main(String[] args) {
        System.out.println("=== STRINGS BASICS ===");
        
        // Palindrome
        System.out.println("\n--- Palindrome ---");
        System.out.println("Is 'racecar' palindrome: " + isPalindrome("racecar"));
        System.out.println("Is 'A man, a plan, a canal: Panama' palindrome: " + 
                          isPalindromeAlphanumeric("A man, a plan, a canal: Panama"));
        
        // Anagram
        System.out.println("\n--- Anagram ---");
        System.out.println("Is 'listen'/'silent' anagram: " + isAnagram("listen", "silent"));
        System.out.println("Group anagrams: " + groupAnagrams(new String[]{"eat","tea","tan","ate","nat","bat"}));
        
        // Frequency
        System.out.println("\n--- Frequency ---");
        System.out.println("Frequency map of 'hello': " + frequencyMap("hello"));
        System.out.println("Most frequent in 'hello': " + mostFrequentChar("hello"));
        
        // Substring & prefix
        System.out.println("\n--- Substring ---");
        System.out.println("Longest common prefix: " + longestCommonPrefix(new String[]{"flower","flow","flight"}));
        System.out.println("Longest substring without repeating: " + lengthOfLongestSubstring("abcabcbb"));
        
        // String processing
        System.out.println("\n--- Processing ---");
        System.out.println("Remove vowels from 'hello': " + removeVowels("hello"));
        System.out.println("Reverse words 'hello world java': " + reverseWords("hello world java"));
        System.out.println("Reverse 'hello': " + reverseString("hello"));
        
        System.out.println("\n=== KEY STRING PATTERNS ===");
        System.out.println("1. Palindrome → Two-pointer from ends");
        System.out.println("2. Anagram → Frequency array [26] or HashMap");
        System.out.println("3. Frequency Counting → int[26] (faster) vs HashMap (flexible)");
        System.out.println("4. Sliding Window → substring problems with char counts");
        System.out.println("5. StringBuilder → efficient concatenation");
        System.out.println("6. Character → isLetter, isDigit, toLowerCase, toUpperCase");
    }
}
