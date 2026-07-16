package com.patternwisejavasolutions.intervalsgreedy.greedy;

import java.util.ArrayList;
import java.util.List;

public class PartitionLabels {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: s = "ababcbacadefegdehijhklij"
     * Sample Output: [9, 7, 8]
     *
     * Split the string into as many parts as possible so that each letter appears
     * in at most one part.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A part can end only after every letter inside it has finished appearing.
     * If the part contains 'a', we must include the last 'a'. Same for every
     * other letter inside the part.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Start a part at left. Keep extending right until all letters seen in this
     * part do not appear again after right.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Start a partition at left.
     * 2. Move right one step at a time.
     * 3. For every letter between left and right, scan the rest of the string.
     * 4. If none of those letters appears later, close the partition.
     *
     * Time Complexity: O(n^3)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "abac"
     * left = 0
     * right = 0 has 'a', but 'a' appears later, so extend.
     * right = 1 has 'a','b', but 'a' appears later, so extend.
     * right = 2 has 'a','b','a', none appears after 2, close size 3.
     * Last partition is "c", size 1.
     */

    public List<Integer> bruteForce(String s) {
        List<Integer> answer = new ArrayList<>();
        int left = 0;

        while (left < s.length()) {
            for (int right = left; right < s.length(); right++) {
                if (canCloseHere(s, left, right)) {
                    answer.add(right - left + 1);
                    left = right + 1;
                    break;
                }
            }
        }

        return answer;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Instead of repeatedly scanning to know where each letter ends, store the
     * last index of every letter once. While walking through a partition, the
     * partition end is the farthest last index of any letter seen so far.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Store last position of each character.
     * 2. Walk through the string.
     * 3. Extend current partition end to the farthest last position seen.
     * 4. When index equals end, close the partition.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "abac"
     * last[a] = 2, last[b] = 1, last[c] = 3
     * i = 0, end = 2
     * i = 1, end stays 2
     * i = 2, close size 3
     * i = 3, close size 1
     */

    public List<Integer> optimized(String s) {
        List<Integer> answer = new ArrayList<>();
        int[] lastIndex = new int[26];

        for (int i = 0; i < s.length(); i++) {
            lastIndex[s.charAt(i) - 'a'] = i;
        }

        int start = 0;
        int end = 0;

        for (int i = 0; i < s.length(); i++) {
            // Any letter in this partition may force the partition to stretch.
            end = Math.max(end, lastIndex[s.charAt(i) - 'a']);

            if (i == end) {
                answer.add(end - start + 1);
                start = i + 1;
            }
        }

        return answer;
    }

    private boolean canCloseHere(String s, int left, int right) {
        for (int i = left; i <= right; i++) {
            char current = s.charAt(i);
            for (int later = right + 1; later < s.length(); later++) {
                if (s.charAt(later) == current) {
                    return false;
                }
            }
        }
        return true;
    }
}
