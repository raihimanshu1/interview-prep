import java.util.ArrayDeque;
import java.util.Deque;

public class DequeSlidingWindowMaximum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given an array and a window size k, move the window from left to right.
     * For every window, return the biggest number inside it.
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
     * Imagine placing a ruler over exactly k numbers. First the ruler covers
     * the first k numbers, then it slides one step right. In each position we
     * only need one answer: the largest number under the ruler.
     *
     * The pattern is a fixed-size sliding window. The optimized version also
     * uses a deque because we need to quickly forget old numbers and quickly
     * know which remaining number is biggest.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The most direct idea is to inspect every window from scratch. For each
     * start position, look at all k values and choose the largest.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. If k is invalid, return an empty answer.
     * 2. Try every window start from 0 to n - k.
     * 3. For that window, scan start through start + k - 1.
     * 4. Store the maximum found for that window.
     *
     * Time Complexity: O(n * k)
     * Space Complexity: O(1), not counting the output array
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1, 3, -1, -3], k = 3
     *
     * Window [1, 3, -1] -> maximum is 3.
     * Window [3, -1, -3] -> maximum is 3.
     * Answer = [3, 3]
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int[] bruteForce(int[] nums, int k) {
        if (nums == null || k <= 0 || k > nums.length) {
            return new int[0];
        }

        int[] answer = new int[nums.length - k + 1];

        for (int start = 0; start <= nums.length - k; start++) {
            int maximum = nums[start];

            for (int i = start; i < start + k; i++) {
                // Check every number because brute force has no memory.
                maximum = Math.max(maximum, nums[i]);
            }

            answer[start] = maximum;
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is repeated scanning. If a new number enters
     * the window and it is bigger than numbers behind it, those smaller numbers
     * can never become maximum while the bigger number is still present.
     *
     * A deque lets us keep only useful indices in decreasing value order. The
     * front always points to the current maximum.
     *
     * Other useful approaches: a max-heap can also work, but it is O(n log k).
     * The monotonic deque is the clean O(n) sliding-window approach.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Walk with right from 0 to n - 1.
     * 2. Remove front indices that are outside the current window.
     * 3. Remove back indices whose values are <= nums[right].
     * 4. Add right to the back.
     * 5. Once the first full window exists, answer is nums[deque front].
     *
     * Time Complexity: O(n)
     * Space Complexity: O(k)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1, 3, -1, -3, 5], k = 3
     *
     * right 0: deque holds index 0(value 1)
     * right 1: 3 is bigger than 1, remove index 0, add index 1
     * right 2: -1 is smaller than 3, add index 2 behind it
     * First full window formed. Front is index 1, value 3.
     *
     * right 3: add -3 behind -1, front index 1 still answers window [3,-1,-3].
     * right 4: window left is 2, so index 1 is stale and must leave.
     * 5 then removes -3 and -1 from the back because a newer bigger value
     * makes them impossible future maximums.
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A max heap can also solve this by keeping values with their indices and
     * discarding heap entries that fall outside the window. The deque is faster
     * because each index is added and removed at most once.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int[] optimized(int[] nums, int k) {
        if (nums == null || k <= 0 || k > nums.length) {
            return new int[0];
        }

        int[] answer = new int[nums.length - k + 1];
        Deque<Integer> usefulIndices = new ArrayDeque<>();

        for (int right = 0; right < nums.length; right++) {
            int leftBoundary = right - k + 1;

            while (!usefulIndices.isEmpty() && usefulIndices.peekFirst() < leftBoundary) {
                // This index is no longer under the sliding window.
                usefulIndices.pollFirst();
            }

            while (!usefulIndices.isEmpty()
                    && nums[usefulIndices.peekLast()] <= nums[right]) {
                // Smaller old values are useless because current value is newer and bigger.
                usefulIndices.pollLast();
            }

            usefulIndices.offerLast(right);

            if (leftBoundary >= 0) {
                // Deque front is the largest useful value for this completed window.
                answer[leftBoundary] = nums[usefulIndices.peekFirst()];
            }
        }

        return answer;
    }
}
