package com.patternwisejavasolutions.stack.monotonicdecreasingnextsmaller;

import java.util.Stack;

public class NextSmallerElement {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * For every element, find the next smaller element on its right.
     * If none exists, use -1.
     *
     * Sample Input:
     * [4, 8, 5, 2, 25]
     *
     * Sample Output:
     * [2, 5, 2, -1, -1]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For each number, look right and stop at the first smaller number.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Try every number with every number to its right.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * value 8:
     * look right -> 5 is smaller
     * answer for 8 is 5.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each index, scan all positions to its right.
     * 2. Stop at the first smaller value.
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

            for (int j = i + 1; j < nums.length; j++) {
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
     * Brute force scans right again for every index. Instead, keep a stack of
     * indices still waiting for a smaller number.
     *
     * The waiting values are increasing from bottom to top. When a smaller
     * current value arrives, it can resolve the larger values on top, possibly
     * popping several indexes in a row.
     *
     * When current value is smaller than stack top,
     * current is the next smaller answer for stack top.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [4, 8, 5, 2]
     *
     * see 4 -> wait
     * see 8 -> wait
     * see 5 -> 5 is smaller than 8, pop 8 and answer for 8 is 5
     * see 2 -> 2 pops 5, then pops 4. Both were waiting for the first smaller
     * value on their right, and 2 is the first one they meet.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Stack stores indices waiting for a next smaller value.
     * 2. When current value is smaller than stack top value, pop it.
     * 3. Current value becomes answer for the popped index.
     * 4. Push current index to wait for its own smaller value.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A brute-force nested loop is useful to confirm the meaning of "next". The
     * monotonic stack improves it by keeping only candidates that can still be
     * the next smaller value for something to their left.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int[] optimized(int[] nums) {
        int[] answer = new int[nums.length];
        Stack<Integer> waitingIndices = new Stack<>();

        for (int i = 0; i < nums.length; i++) {
            answer[i] = -1;

            while (!waitingIndices.isEmpty() && nums[i] < nums[waitingIndices.peek()]) {
                // Current is the first smaller value for this waiting index.
                int index = waitingIndices.pop();
                answer[index] = nums[i];
            }

            // This index now waits for a smaller value somewhere to its right.
            waitingIndices.push(i);
        }

        return answer;
    }
}
