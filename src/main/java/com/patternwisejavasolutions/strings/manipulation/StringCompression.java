package com.patternwisejavasolutions.strings.manipulation;

public class StringCompression {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Compress repeated groups of characters in-place and return the new length.
 *
 * Sample Input:
 * chars = ["a", "a", "b", "b", "c", "c", "c"]
 *
 * Sample Output:
 * 6 with array beginning ["a", "2", "b", "2", "c", "3"]
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * The sample has runs: two a's, two b's, and three c's. The compressed array
 * writes each run as the letter followed by the count only when the count is
 * bigger than 1.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * We read one group of equal letters at a time. A group like "aaa" becomes
 * "a3"; a single "b" stays just "b".
 *
 * The important habit is to separate reading from writing: one pointer finds
 * the next group, and another pointer records the compressed answer.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to build the compressed text somewhere else, where
 * writing is easy. After that string is complete, copy it back into chars.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Read one group at a time.
 * 2. Append the character.
 * 3. Append count digits when count > 1.
 * 4. Copy compressed text back to chars.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * chars = ["a","a","b","b","c","c","c"]
 * group "aa" -> write "a2"
 * group "bb" -> write "b2"
 * group "ccc" -> write "c3"
 */
    public int bruteForce(char[] chars) {
        StringBuilder compressed = new StringBuilder();
        int index = 0;

        while (index < chars.length) {
            char current = chars[index];
            int count = 0;

            while (index < chars.length && chars[index] == current) {
                index++;
                count++;
            }

            compressed.append(current);
            if (count > 1) {
                compressed.append(count);
            }
        }

        for (int i = 0; i < compressed.length(); i++) {
            chars[i] = compressed.charAt(i);
        }

        return compressed.length();
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is the extra StringBuilder. We can write directly
 * into the same char array because each compressed group uses at most the space
 * of that original group in the final answer.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Use read to scan groups.
 * 2. Use write to place compressed output.
 * 3. Write the group char.
 * 4. If count > 1, write each digit of count.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * read sees group "ccc", so count = 3.
 * write places 'c', then writes digit '3'.
 * The next group will continue writing after that.
 */
    public int optimized(char[] chars) {
        int read = 0;
        int write = 0;

        while (read < chars.length) {
            char current = chars[read];
            int count = 0;

            while (read < chars.length && chars[read] == current) {
                // Move read across one full group before writing its summary.
                read++;
                count++;
            }

            chars[write++] = current;
            if (count > 1) {
                for (char digit : String.valueOf(count).toCharArray()) {
                    // Multi-digit counts, like 12, must be written as '1' then '2'.
                    chars[write++] = digit;
                }
            }
        }

        return write;
    }

}
