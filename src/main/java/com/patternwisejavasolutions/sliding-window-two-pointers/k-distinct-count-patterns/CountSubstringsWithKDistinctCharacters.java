package com.patternwisejavasolutions.slidingwindowtwopointers.kdistinctcountpatterns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CountSubstringsWithKDistinctCharacters {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Count how many substrings have exactly k distinct characters.
     *
     * Sample Input:
     * s = "pqpqs", k = 2
     *
     * Sample Output:
     * 7
     *
     * Valid substrings include:
     * "pq", "pqp", "pqpq", "qp", "qpq", "pq", "qs"
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * "Exactly k distinct" means the substring should have neither fewer nor more
     * than k different characters.
     *
     * The direct way:
     * - Try every substring.
     * - Count distinct characters using a set.
     * - If set size is exactly k, count it.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Choose a start and grow the substring one character at a time. Track
     * distinct characters in a set; count the substring exactly when the set
     * size becomes k.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "pqp", k = 2
     *
     * start = 0:
     * "p"   -> 1 distinct, not counted
     * "pq"  -> 2 distinct, count = 1
     * "pqp" -> 2 distinct, count = 2
     *
     * start = 1:
     * "q"  -> 1 distinct, not counted
     * "qp" -> 2 distinct, count = 3
     *
     * start = 2:
     * "p" -> 1 distinct
     *
     * Answer for "pqp" is 3.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick every start index.
     * 2. Expand the substring end index.
     * 3. Track distinct characters in a set.
     * 4. If distinct count equals k, increase answer.
     * 5. If distinct count becomes greater than k, break.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(k) or O(character set)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(String s, int k) {
        int answer = 0;

        for (int start = 0; start < s.length(); start++) {
            Set<Character> distinctCharacters = new HashSet<>();

            for (int end = start; end < s.length(); end++) {
                distinctCharacters.add(s.charAt(end));

                if (distinctCharacters.size() == k) {
                    answer++;
                }

                if (distinctCharacters.size() > k) {
                    break;
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Counting exactly k is tricky with one sliding window because once a window is
     * valid, many smaller windows may also be valid.
     *
     * So we use a clever conversion:
     *
     * exactly(k) = atMost(k) - atMost(k - 1)
     *
     * Why?
     * atMost(k) counts substrings with 1, 2, ..., k distinct characters.
     * atMost(k - 1) counts substrings with 1, 2, ..., k - 1 distinct characters.
     * Subtracting leaves only exactly k.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "pqp", k = 2
     *
     * atMost(2) substrings:
     * "p", "pq", "pqp", "q", "qp", "p" -> 6
     *
     * atMost(1) substrings:
     * "p", "q", "p" -> 3
     *
     * exactly(2) = 6 - 3 = 3
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Write helper atMost(s, k).
     * 2. In atMost, use sliding window with a frequency map.
     * 3. If distinct count becomes more than k, shrink from left.
     * 4. For each right index, add right - left + 1 substrings.
     * 5. Return atMost(k) - atMost(k - 1).
     *
     * Time Complexity: O(n)
     * Space Complexity: O(k) or O(character set)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * The same exact-k idea can be written as atMost(k) - atMost(k - 1). That
     * avoids checking every substring after the window is valid.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(String s, int k) {
        return countAtMostKDistinct(s, k) - countAtMostKDistinct(s, k - 1);
    }

    private int countAtMostKDistinct(String s, int k) {
        if (k < 0) {
            return 0;
        }

        Map<Character, Integer> frequency = new HashMap<>();
        int left = 0;
        int answer = 0;

        for (int right = 0; right < s.length(); right++) {
            char rightCharacter = s.charAt(right);
            frequency.put(
                rightCharacter,
                frequency.getOrDefault(rightCharacter, 0) + 1
            );

            while (frequency.size() > k) {
                char leftCharacter = s.charAt(left);
                // Keep the helper as "at most k" by removing from the left.
                frequency.put(leftCharacter, frequency.get(leftCharacter) - 1);

                if (frequency.get(leftCharacter) == 0) {
                    frequency.remove(leftCharacter);
                }

                left++;
            }

            // With a valid window, every start from left through right is allowed.
            answer += right - left + 1;
        }

        return answer;
    }
}
