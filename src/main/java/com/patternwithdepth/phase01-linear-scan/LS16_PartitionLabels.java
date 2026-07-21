package com.patternwithdepth.phase01_linear_scan;

import java.util.ArrayList;
import java.util.List;

/**
 * PROBLEM: Partition Labels
 * (Problem number: 33 in the DSA Playbook)
 *
 * DESCRIPTION:
 * You are given a string s. Partition it into as many parts as possible so that
 * each letter appears in at most one part. Return a list of integers representing
 * the size of these parts.
 *
 * Example 1:
 * Input: s = "ababcbacadefegdehijhklij"
 * Output: [9, 7, 8]
 * Explanation: The partition is "ababcbaca", "defegde", "hijhklij".
 *
 * Example 2:
 * Input: s = "eccbbbbdec"
 * Output: [10]
 * Explanation: Only one partition possible.
 *
 * CONSTRAINTS:
 * - 1 <= s.length <= 500
 * - s consists of lowercase English letters
 *
 * APPROACH:
 * OPTIMIZED: O(n) - Track last occurrence of each character, greedily cut partitions
 */

public class LS16_PartitionLabels {

    public static void main(String[] args) {
        // 1. Create our input string.
        String s = "ababcbacadefegdehijhklij";

        // 2. --- OPTIMIZED APPROACH ---
        List<Integer> result = partitionLabels(s);
        System.out.println("Partition Sizes: " + result); // Expected: [9, 7, 8]

        // 3. Test another example.
        String s2 = "eccbbbbdec";
        System.out.println("Single Partition: " + partitionLabels(s2)); // Expected: [10]
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: For each character, record its LAST occurrence index.
    //       Then scan from left to right, extending the current partition's
    //       end to the farthest last occurrence seen so far.
    //       When current index == partition end, cut and start new partition.
    // Time:  O(n)   |  Space: O(1) extra (fixed alphabet size)
    // -------------------------------------------------------------------------
    public static List<Integer> partitionLabels(String s) {
        int n = s.length();
        int[] lastOccurrence = new int[26]; // 1. Track last index for each letter.

        // 2. Record the last occurrence of each character.
        for (int i = 0; i < n; i++) {
            lastOccurrence[s.charAt(i) - 'a'] = i;
        }

        List<Integer> partitions = new ArrayList<>(); // 3. Store result partition sizes.
        int start = 0; // 4. Start index of current partition.
        int end = 0; // 5. End index of current partition.

        // 6. Iterate through each character.
        for (int i = 0; i < n; i++) {
            // 7. Extend the end of the current partition to include the last
            //    occurrence of the current character.
            end = Math.max(end, lastOccurrence[s.charAt(i) - 'a']);

            // 8. If we reached the end of the partition, we can cut here.
            if (i == end) {
                // 9. Add the size of this partition to the result.
                partitions.add(end - start + 1);

                // 10. Start a new partition from the next character.
                start = i + 1;
            }
        }

        return partitions;
    }
}