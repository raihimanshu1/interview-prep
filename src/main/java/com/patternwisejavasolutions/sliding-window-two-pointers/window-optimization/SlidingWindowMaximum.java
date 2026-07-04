
package com.patternwisejavasolutions.slidingWindowTwoPointers.windowOptimization;
import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowMaximum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * For every window of size k, return the maximum number inside that window.
     *
     * Sample Input:
     * nums = [1, 3, -1, -3, 5, 3, 6, 7], k = 3
     *
     * Sample Output:
     * [3, 3, 5, 5, 6, 7]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * First, do exactly what the problem says.
     * Take each window of size k and scan all numbers inside it to find the maximum.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Put the window at each possible start and scan the k numbers inside it.
     * Since brute force keeps no memory, it rediscovers the maximum from
     * scratch for every window.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,3,-1,-3,5], k = 3
     *
     * window [1,3,-1]  -> max = 3
     * window [3,-1,-3] -> max = 3
     * window [-1,-3,5] -> max = 5
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Start every window at index start.
     * 2. Scan from start to start + k - 1.
     * 3. Store the biggest value found.
     *
     * Time Complexity: O(n * k)
     * Space Complexity: O(1) except output
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int[] bruteForce(int[] nums, int k) {
        if (nums == null || k <= 0 || k > nums.length) {
            return new int[0];
        }

        int[] answer = new int[nums.length - k + 1];

        for (int start = 0; start + k <= nums.length; start++) {
            int maximum = nums[start];

            for (int index = start; index < start + k; index++) {
                maximum = Math.max(maximum, nums[index]);
            }

            answer[start] = maximum;
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * We need the maximum quickly.
     *
     * If a new number is bigger than numbers behind it, those smaller numbers can
     * never become maximum while the bigger number is still in the window.
     *
     * So we keep useful indices in a deque.
     * Values in the deque stay decreasing from front to back.
     * The front always points to the maximum.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,3,-1], k = 3
     *
     * Add 1 -> deque values [1]
     * Add 3 -> remove 1 because 3 is bigger, deque values [3]
     * Add -1 -> deque values [3, -1]
     *
     * Window size is 3.
     * Front is 3, so maximum is 3.
     *
     * If the next values were [-3, 5]:
     * after adding -3, index of 3 is still inside the window, so answer is 3.
     * when 5 enters, 3 has slid out and stale index 1 is removed first.
     * 5 then removes -3 and -1 from the back because neither can beat 5.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Store indices in a deque, not values.
     * 2. Remove front index if it is outside the current window.
     * 3. Remove back indices while their values are <= current value.
     * 4. Add current index.
     * 5. Once first window is ready, answer is nums[deque front].
     *
     * Time Complexity: O(n)
     * Space Complexity: O(k)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A max heap can also keep the largest value for each window. It is easier
     * to remember, but it needs lazy removal of indices that have moved out of
     * the window and usually costs O(n log k).
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int[] optimized(int[] nums, int k) {
        if (nums == null || k <= 0 || k > nums.length) {
            return new int[0];
        }

        int[] answer = new int[nums.length - k + 1];
        Deque<Integer> decreasingIndexes = new ArrayDeque<>();

        for (int right = 0; right < nums.length; right++) {
            while (
                !decreasingIndexes.isEmpty()
                    && decreasingIndexes.peekFirst() <= right - k
            ) {
                // The front index is left of the current window, so it cannot answer anymore.
                decreasingIndexes.removeFirst();
            }

            while (
                !decreasingIndexes.isEmpty()
                    && nums[decreasingIndexes.peekLast()] <= nums[right]
            ) {
                // Current is newer and at least as large, so the old back value is never useful.
                decreasingIndexes.removeLast();
            }

            decreasingIndexes.addLast(right);

            if (right >= k - 1) {
                int answerIndex = right - k + 1;
                answer[answerIndex] = nums[decreasingIndexes.peekFirst()];
            }
        }

        return answer;
    }
}
