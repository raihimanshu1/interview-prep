package com.patternwisejavasolutions.dynamicprogramming;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordBreak {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: s = "leetcode", wordDict = ["leet", "code"]
     * Sample Output: true
     *
     * Input: s = "leetcode", wordDict = ["leet", "code"]
     * Output: true because "leetcode" = "leet" + "code".
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try every prefix of the string.
     * If the prefix is a dictionary word, solve the remaining suffix.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Try placing the next cut after every possible prefix. If the prefix is a
     * dictionary word, recurse on the remaining suffix. The same suffix start
     * can be reached through different earlier cuts.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * s = "leetcode"
     * Prefix "leet" is in dictionary.
     * Now solve "code".
     * "code" is also in dictionary, so true.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. At index, try every end position.
     * 2. If substring index..end is a word, recurse from end.
     * 3. If any path reaches end of string, return true.
     * Time Complexity: exponential
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This walks the raw cut-position recursion tree.
     */
    public boolean bruteForce(String s, List<String> wordDict) {
        return canBreakFrom(s, 0, new HashSet<>(wordDict));
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The same starting index repeats.
     * Let dp[i] mean: can s[0..i) be segmented?
     * If dp[start] is true and s[start..end) is a word, then dp[end] becomes true.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * dp[0] = true
     * "leet" found from 0 to 4 -> dp[4] = true
     * "code" found from 4 to 8 -> dp[8] = true
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Put dictionary words in a set.
     * 2. dp[0] = true.
     * 3. For every end, try all starts.
     * 4. If dp[start] and substring is word, mark dp[end].
     * Time Complexity: O(n^3) because substring costs time
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * dp[end] remembers whether the prefix ending there can be split.
     */
    public boolean optimized(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        // Empty prefix is splittable; it is the starting point for the first word.
        dp[0] = true;

        for (int end = 1; end <= s.length(); end++) {
            for (int start = 0; start < end; start++) {
                if (dp[start] && words.contains(s.substring(start, end))) {
                    // A valid earlier prefix plus one dictionary word reaches end.
                    dp[end] = true;
                    break;
                }
            }
        }

        return dp[s.length()];
    }


    private boolean canBreakFrom(String s, int start, Set<String> words) {
        if (start == s.length()) {
            // All characters were successfully cut into dictionary words.
            return true;
        }

        for (int end = start + 1; end <= s.length(); end++) {
            if (words.contains(s.substring(start, end)) && canBreakFrom(s, end, words)) {
                // Found a dictionary prefix whose remaining suffix can also break.
                return true;
            }
        }

        return false;
    }
}
