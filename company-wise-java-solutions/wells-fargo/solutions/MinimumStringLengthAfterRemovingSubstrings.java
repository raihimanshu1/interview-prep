package wellsfargo.solutions;

import java.util.ArrayDeque;
import java.util.Deque;

public class MinimumStringLengthAfterRemovingSubstrings {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a string, repeatedly remove substrings "AB" and "CD" until no more can be removed. Return final length.
     *
     * INPUT
     * s = uppercase string.
     *
     * OUTPUT
     * Length after all possible removals.
     *
     * EXAMPLE
     * s="ABFCACDB" -> 2.
     *
     * WHAT IT MEANS
     * Adjacent pairs cancel. After cancellation, new neighbors may form another canceling pair.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1: "ABFCACDB" -> 2
     * Example 2: "ACBBD" -> 5
     * Example 3: "ABCD" -> 0
     *
     * EDGE CASES
     * - No removable pair returns original length.
     * - Entire string can disappear.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Repeatedly replace AB and CD with empty strings until the string stops changing.
     *
     * Time Complexity: O(n^2). Space Complexity: O(n).
     */

    /*
     * OPTIMIZED APPROACH
     *
     * Use a stack. When current character forms AB or CD with stack top, pop instead of pushing.
     *
     * Time Complexity: O(n). Space Complexity: O(n).
     */
    public int bruteForce(String s) {
        boolean changed = true;

        while (changed) {
            changed = false;
            String next = s.replace("AB", "").replace("CD", "");

            if (!next.equals(s)) {
                changed = true;
                s = next;
            }
        }

        return s.length();
    }

    public int optimized(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char current : s.toCharArray()) {
            if (!stack.isEmpty()) {
                char previous = stack.peek();

                if ((previous == 'A' && current == 'B')
                        || (previous == 'C' && current == 'D')) {
                    stack.pop();
                    continue;
                }
            }

            stack.push(current);
        }

        return stack.size();
    }

    public static void main(String[] args) {
        MinimumStringLengthAfterRemovingSubstrings solver = new MinimumStringLengthAfterRemovingSubstrings();
        System.out.println("Run the examples from MORE INPUTS TO PRACTICE against bruteForce and optimized.");
    }
}
