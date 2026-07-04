
package com.patternwisejavasolutions.stack.monotonicDecreasingNextSmaller;
import java.util.Stack;

public class PreviousSmallerElement {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * For every element, find the closest smaller element on its left.
     * If none exists, use -1.
     *
     * Sample Input:
     * [4, 10, 5, 8, 20, 15, 3, 12]
     *
     * Sample Output:
     * [-1, 4, 4, 5, 8, 8, -1, 3]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * "Previous" means left side.
     * For each number, walk backward until you find a smaller number.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For each index, scan left from i - 1 to 0.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * value 8 at index 3:
     * left side is [4, 10, 5]
     * closest smaller from right side is 5.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each index, scan left from i - 1.
     * 2. Stop at first smaller value.
     * 3. If no smaller value appears, keep -1.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1), not counting output
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int[] bruteForce(int[] nums) {
        int[] answer = new int[nums.length];

        for (int i = 0; i < nums.length; i++) {
            answer[i] = -1;

            for (int j = i - 1; j >= 0; j--) {
                if (nums[j] < nums[i]) {
                    answer[i] = nums[j];
                    break;
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * When standing at current number,
     * bigger numbers on the left cannot be previous smaller for current
     * or for any future smaller/equal number.
     * So we remove them.
     *
     * The remaining stack top is the closest smaller value.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [4, 10, 5]
     *
     * 4 -> no previous smaller, push 4
     * 10 -> top 4 is smaller, answer 4, push 10
     * 5 -> pop 10 because it is bigger, top 4 is smaller, answer 4
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep a stack of possible previous smaller values.
     * 2. Remove values greater than or equal to current.
     * 3. After removals, stack top is closest smaller value.
     * 4. Push current value for future elements.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * You can scan left for each index to find the previous smaller value, but
     * that repeats work. The stack keeps a cleaned-up history of smaller
     * candidates as the scan moves right.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int[] optimized(int[] nums) {
        int[] answer = new int[nums.length];
        Stack<Integer> possibleSmallerValues = new Stack<>();

        for (int i = 0; i < nums.length; i++) {
            while (!possibleSmallerValues.isEmpty() && possibleSmallerValues.peek() >= nums[i]) {
                // Bigger/equal values cannot be the previous smaller for current or later values.
                possibleSmallerValues.pop();
            }

            if (possibleSmallerValues.isEmpty()) {
                answer[i] = -1;
            } else {
                answer[i] = possibleSmallerValues.peek();
            }

            // Current may become the closest smaller value for a future element.
            possibleSmallerValues.push(nums[i]);
        }

        return answer;
    }
}
