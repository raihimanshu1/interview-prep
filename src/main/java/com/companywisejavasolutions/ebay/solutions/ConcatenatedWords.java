package com.companywisejavasolutions.ebay.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConcatenatedWords {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return all words that can be built by joining at least two shorter words
     * from the same list.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Sort by length. When checking a word, the set contains only shorter words,
     * so dynamic programming can decide if the word can be split into known parts.
     */
    public List<String> findAllConcatenatedWordsInADict(String[] words) {
        Arrays.sort(words, (a, b) -> a.length() - b.length());
        Set<String> dictionary = new HashSet<>();
        List<String> answer = new ArrayList<>();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (canBuild(word, dictionary)) {
                answer.add(word);
            }
            dictionary.add(word);
        }

        return answer;
    }

    private boolean canBuild(String word, Set<String> dictionary) {
        if (dictionary.isEmpty()) {
            return false;
        }

        boolean[] dp = new boolean[word.length() + 1];
        dp[0] = true;

        for (int end = 1; end <= word.length(); end++) {
            for (int start = 0; start < end; start++) {
                if (dp[start] && dictionary.contains(word.substring(start, end))) {
                    dp[end] = true;
                    break;
                }
            }
        }

        return dp[word.length()];
    }
}
