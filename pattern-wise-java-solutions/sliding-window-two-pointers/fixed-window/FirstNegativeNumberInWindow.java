import java.util.ArrayDeque;
import java.util.Deque;

public class FirstNegativeNumberInWindow {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input:
     * nums = [12, -1, -7, 8, -15, 30, 16, 28], k = 3
     *
     * Sample Output:
     * [-1, -1, -7, -15, -15, 0]
     *
     *
     * For every window of size k, return the first negative number.
     * If a window has no negative number, return 0 for that window.
     *
     * Example:
     * nums = [12, -1, -7, 8, -15, 30, 16, 28]
     * k = 3
     *
     * Window [12, -1, -7] -> first negative is -1
     * Window [-1, -7, 8] -> first negative is -1
     * Window [-7, 8, -15] -> first negative is -7
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For each window, scan from left to right.
     * The first negative number we find is the answer for that window.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For each window, pretend we know nothing from the previous window.
     * Start at the left edge and walk right until the first negative appears.
     * This is natural because "first negative" is about order inside the window.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [12, -1, -7, 8]
     * k = 3
     *
     * window [12, -1, -7]:
     * 12 is positive
     * -1 is negative
     * answer = -1
     *
     * window [-1, -7, 8]:
     * -1 is negative
     * answer = -1
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each window start.
     * 2. Scan k elements.
     * 3. Return first negative found.
     * 4. If none found, store 0.
     *
     * Time Complexity: O(n * k)
     * Space Complexity: O(n - k + 1) for answer
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int[] bruteForce(int[] nums, int k) {
        int[] answer = new int[nums.length - k + 1];

        for (int start = 0; start + k <= nums.length; start++) {
            int firstNegative = 0;

            for (int i = start; i < start + k; i++) {
                if (nums[i] < 0) {
                    firstNegative = nums[i];
                    break;
                }
            }

            answer[start] = firstNegative;
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is looking again at positive numbers that can
     * never be an answer. A window only needs to remember negative numbers, and
     * it needs them in left-to-right order because the leftmost negative is the
     * answer.
     *
     * The front of the deque will always be the first negative number
     * for the current window. When that index slides out, we remove it and the
     * next negative index becomes the answer.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [12, -1, -7, 8, -15]
     * k = 3
     *
     * right 0, value 12: positive, deque = []
     *
     * right 1, value -1: store index 1, deque = [1]
     * right 2, value -7: store index 2, deque = [1, 2]
     * window [12, -1, -7], front index 1 gives -1.
     *
     * right 3, value 8: window [-1, -7, 8], front index 1 still gives -1.
     *
     * right 4, value -15: store index 4, window left is 2.
     * index 1 is stale now, so remove it.
     * deque becomes [2, 4], front index 2 gives -7.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Traverse using right pointer.
     * 2. If nums[right] is negative, add its index to deque.
     * 3. When window size reaches k:
     *    - remove negative indices that are outside window
     *    - front of deque is answer
     *
     * Time Complexity: O(n)
     * Space Complexity: O(k)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A deque of negative indices gives the first negative number in O(1) after
     * each slide. Another option is to keep a pointer to the first negative and
     * move it forward whenever it leaves the window.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int[] optimized(int[] nums, int k) {
        int[] answer = new int[nums.length - k + 1];
        Deque<Integer> negativeIndices = new ArrayDeque<>();

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] < 0) {
                // Only negative numbers can ever become an answer, so store their indexes.
                negativeIndices.addLast(right);
            }

            if (right >= k - 1) {
                int left = right - k + 1;

                while (!negativeIndices.isEmpty() && negativeIndices.peekFirst() < left) {
                    // This negative was first before, but it is outside this window now.
                    negativeIndices.removeFirst();
                }

                if (negativeIndices.isEmpty()) {
                    answer[left] = 0;
                } else {
                    answer[left] = nums[negativeIndices.peekFirst()];
                }
            }
        }

        return answer;
    }
}
