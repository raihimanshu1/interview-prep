package com.patternwisejavasolutions.strings.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAnagrams {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Group words that are anagrams of each other.
 *
 * Sample Input:
 * strs = ["eat", "tea", "tan", "ate", "nat", "bat"]
 *
 * Sample Output:
 * [["eat", "tea", "ate"], ["tan", "nat"], ["bat"]]
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In the sample, "eat", "tea", and "ate" are grouped because they use the same
 * letters with the same counts. The original order of letters inside each word
 * does not matter, but the letter counts do.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * Words are anagrams if they have the same letters with the same counts.
 * Imagine each word as a bag of letter tiles: "eat", "tea", and "ate" all
 * contain one a, one e, and one t, so they belong in the same group.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * A natural first idea is to build groups one word at a time. For a new word,
 * compare it with the first word in each existing group. If it matches one
 * group's representative, put it there; otherwise start a new group.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Keep a list of groups.
 * 2. For each word, try to place it into an existing group.
 * 3. Use an anagram check against the first word of each group.
 * 4. If no group matches, create a new group.
 *
 * Time Complexity: O(n^2 * k log k)
 * Space Complexity: O(nk)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * groups = [["eat"]]
 * word = "tea"
 * Compare "tea" with group leader "eat"; both sort to "aet".
 * Add "tea" to that group: [["eat", "tea"]].
 */
    public List<List<String>> bruteForce(String[] strs) {
        List<List<String>> groups = new ArrayList<>();

        for (String word : strs) {
            boolean placed = false;

            for (List<String> group : groups) {
                if (sameLetters(word, group.get(0))) {
                    group.add(word);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                List<String> newGroup = new ArrayList<>();
                newGroup.add(word);
                groups.add(newGroup);
            }
        }

        return groups;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is comparing a word with many group leaders.
 * Instead, create one key that represents the word's letter makeup. All
 * anagrams have the same sorted key, so a HashMap jumps directly to the right
 * group without testing every existing group.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. For each word, sort its characters to build a key.
 * 2. Use a map from key to list of words.
 * 3. Add the word to its key group.
 * 4. Return all map values.
 *
 * Time Complexity: O(n * k log k)
 * Space Complexity: O(nk)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * "eat" -> key "aet" -> create group ["eat"]
 * "tea" -> key "aet" -> append to same group
 * "tan" -> key "ant" -> create a different group
 */
    public List<List<String>> optimized(String[] strs) {
        Map<String, List<String>> groupsByKey = new HashMap<>();

        for (String word : strs) {
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            // The key is the "letter makeup"; equal keys mean equal letter counts.
            groupsByKey.computeIfAbsent(key, unused -> new ArrayList<>()).add(word);
        }

        return new ArrayList<>(groupsByKey.values());
    }

    private boolean sameLetters(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        char[] first = a.toCharArray();
        char[] second = b.toCharArray();
        Arrays.sort(first);
        Arrays.sort(second);
        return Arrays.equals(first, second);
    }
}
