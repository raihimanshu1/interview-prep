package com.patternwisejavasolutions.stack.contributionpattern;

import java.util.Stack;

public class SumOfSubarrayMinimums {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * For every subarray, find its minimum value.
     * Add all those minimum values.
     *
     * Sample Input:
     * arr = [3, 1, 2, 4]
     *
     * Sample Output:
     * 17
     *
     * Subarray minimums are:
     * [3]=3, [3,1]=1, [3,1,2]=1, [3,1,2,4]=1,
     * [1]=1, [1,2]=1, [1,2,4]=1,
     * [2]=2, [2,4]=2, [4]=4.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Brute force asks:
     * What is the minimum of each subarray?
     *
     * Optimized thinking asks:
     * For each element, in how many subarrays is this element the minimum?
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Generate every subarray and keep the running minimum while extending it.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * arr = [3, 1]
     *
     * start 0:
     * [3] minimum 3
     * [3,1] minimum 1
     * start 1:
     * [1] minimum 1
     * total = 5
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Choose every start index.
     * 2. Extend end index one step at a time.
     * 3. Keep minimum of current subarray.
     * 4. Add that minimum to total.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] arr) {
        int total = 0;

        for (int start = 0; start < arr.length; start++) {
            int minimum = Integer.MAX_VALUE;

            for (int end = start; end < arr.length; end++) {
                minimum = Math.min(minimum, arr[end]);
                total += minimum;
            }
        }

        return total;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Count contribution of each element.
     *
     * If arr[i] is minimum for:
     * leftChoices choices on left
     * rightChoices choices on right
     *
     * then it contributes:
     * arr[i] * leftChoices * rightChoices
     *
     * Monotonic stack helps find previous less-or-equal and next strictly less
     * boundaries.
     *
     * For duplicate values, this tie rule gives the subarray to the leftmost
     * equal value. That way, the same subarray is not counted twice.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * arr = [3, 1, 2]
     *
     * Element 1 at index 1 is smaller than both sides.
     * It can be minimum in subarrays:
     * [1], [3,1], [1,2], [3,1,2]
     * contribution = 1 * 2 * 2 = 4
     *
     * Hard duplicate moment:
     * for [1, 1], use previous less-or-equal on the left and next strictly
     * less on the right. The second 1 sees the first 1 as its left boundary,
     * so subarray [1,1] is owned by the first 1, not counted twice.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Find previous less-or-equal boundary for each index.
     * 2. Find next strictly less boundary for each index.
     * 3. Count left choices and right choices.
     * 4. Add arr[i] * leftChoices * rightChoices to total.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * You can flip the duplicate rule and use previous strictly less with next
     * less-or-equal instead. Both are correct as long as one side allows equal
     * values and the other side does not.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] arr) {
        int mod = 1_000_000_007;
        int n = arr.length;
        int[] previousLessOrEqual = new int[n];
        int[] nextLess = new int[n];

        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) {
                // Bigger values cannot stop arr[i]; keep equal values as the tie boundary.
                stack.pop();
            }

            previousLessOrEqual[i] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(i);
        }

        stack.clear();

        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
                // Pop equals on the right so duplicate minimums are owned by one side only.
                stack.pop();
            }

            nextLess[i] = stack.isEmpty() ? n : stack.peek();
            stack.push(i);
        }

        long total = 0;

        for (int i = 0; i < n; i++) {
            long leftChoices = i - previousLessOrEqual[i];
            long rightChoices = nextLess[i] - i;
            // Choose a start on the left and an end on the right where arr[i] is the minimum.
            long contribution = arr[i] * leftChoices * rightChoices;

            total = (total + contribution) % mod;
        }

        return (int) total;
    }
}
