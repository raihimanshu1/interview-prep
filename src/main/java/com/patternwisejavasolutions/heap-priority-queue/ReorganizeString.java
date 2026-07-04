
package com.patternwisejavasolutions.heapPriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ReorganizeString {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Rearrange a string so no two neighboring letters are the same. If impossible, return "".
     *
     * Sample Input: s = "aab"
     * Sample Output: "aba"
     *
     * SCHOOL-LEVEL INTUITION
     * If one letter appears too many times, it cannot be separated from itself. Otherwise, keep
     * choosing common letters while avoiding the letter placed just before.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Try every possible arrangement using backtracking. Stop when one valid arrangement is found.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Count letters.
     * 2. Build the answer one character at a time.
     * 3. At each position, try every letter that still remains and is not equal to previous letter.
     * 4. If the answer length reaches s.length(), return it.
     *
     * BRUTE FORCE DRY RUN
     * s = "aab"
     * try a -> cannot place a next, place b -> place a -> "aba"
     *
     * Time Complexity: O(n!)
     * Space Complexity: O(n + alphabet)
     */
    public String bruteForce(String s) {
        Map<Character, Integer> frequency = count(s);
        StringBuilder path = new StringBuilder();

        if (buildAnyValidString(s.length(), frequency, path, '\0')) {
            return path.toString();
        }

        return "";
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force waste is trying many full arrangements. Always use the character with the
     * highest remaining count, but do not reuse the previous character immediately. A max heap
     * makes the most needed character easy to find.
     *
     * Pattern used: Max heap with one-turn holdback.
     *
     * OPTIMIZED ALGORITHM
     * 1. Count characters and add them to a max heap.
     * 2. Remove the most frequent character and append it.
     * 3. Keep the previously used character out of the heap for one turn.
     * 4. Add the previous character back if it still has remaining count.
     * 5. If final length is too short, return "".
     *
     * OPTIMIZED DRY RUN
     * s = "aaabbc"
     * pick a, then b, then a, then c, then a, then b -> "abacab"
     *
     * Time Complexity: O(n log alphabet)
     * Space Complexity: O(alphabet)
     */
    public String optimized(String s) {
        Map<Character, Integer> frequency = count(s);
        /*
         * Higher remaining count comes first, so the character hardest to separate is picked next.
         * The previous character is held out separately, so this heap only contains safe choices.
         */
        PriorityQueue<Character> maxHeap = new PriorityQueue<>((a, b) -> {
            return Integer.compare(frequency.get(b), frequency.get(a));
        });
        maxHeap.addAll(frequency.keySet());

        StringBuilder answer = new StringBuilder();
        Character previous = null;

        while (!maxHeap.isEmpty()) {
            char current = maxHeap.poll();
            answer.append(current);
            frequency.put(current, frequency.get(current) - 1);

            // Previous is now safe to use again because current separates it.
            if (previous != null && frequency.get(previous) > 0) {
                maxHeap.offer(previous);
            }

            // Hold current out for one turn so it cannot be placed next to itself.
            previous = current;
        }

        return answer.length() == s.length() ? answer.toString() : "";
    }

    private Map<Character, Integer> count(String s) {
        Map<Character, Integer> frequency = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            frequency.put(ch, frequency.getOrDefault(ch, 0) + 1);
        }

        return frequency;
    }

    private boolean buildAnyValidString(int targetLength, Map<Character, Integer> frequency, StringBuilder path, char previous) {
        if (path.length() == targetLength) {
            return true;
        }

        for (char ch : frequency.keySet()) {
            if (frequency.get(ch) == 0 || ch == previous) {
                continue;
            }

            path.append(ch);
            frequency.put(ch, frequency.get(ch) - 1);

            if (buildAnyValidString(targetLength, frequency, path, ch)) {
                return true;
            }

            frequency.put(ch, frequency.get(ch) + 1);
            path.deleteCharAt(path.length() - 1);
        }

        return false;
    }
}
