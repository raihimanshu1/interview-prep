import java.util.Arrays;
import java.util.Stack;

public class NextGreaterElementII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * For each number in a circular array, find the first bigger number after
     * it. Circular means after the last index, we can continue from index 0.
     *
     * Sample Input: nums = [1,2,1]
     * Sample Output: [2,-1,2]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Each number looks to its right for the first bigger number. Since the
     * array is circular, the last element is allowed to wrap around and keep
     * looking from index 0.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For every index, move one step at a time around the circle until a bigger
     * value is found. This is the most literal way to respect the word "next":
     * check positions in the exact order the circular array would visit them.
     *
     * BRUTE FORCE ALGORITHM
     * 1. For each index i, check the next n - 1 positions using modulo.
     * 2. The first value greater than nums[i] is the answer.
     * 3. If none is found, answer remains -1.
     *
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,2,1]
     * index 0 sees 2 -> answer 2
     * index 1 sees 1 then 1 -> no bigger -> -1
     * index 2 wraps and sees 1 then 2 -> answer 2
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1), not counting output
     */
    public int[] bruteForce(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];
        Arrays.fill(answer, -1);

        for (int i = 0; i < n; i++) {
            for (int step = 1; step < n; step++) {
                int nextIndex = (i + step) % n;

                if (nums[nextIndex] > nums[i]) {
                    answer[i] = nums[nextIndex];
                    break;
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Brute force wastes time walking around the circle from every index. A
     * decreasing stack stores indexes still waiting for a greater value.
     *
     * Scanning twice simulates the circular array without restarting the search
     * for every number. We only push indexes during the first pass so each real
     * index waits once; the second pass is only there to resolve values that
     * need to wrap around.
     *
     * OPTIMIZED ALGORITHM
     * 1. Fill answer with -1.
     * 2. Scan indexes from 0 to 2*n - 1.
     * 3. Convert each scan position to real index using modulo.
     * 4. While current value is greater than stack top value, current is that answer.
     * 5. Push indexes only during the first pass.
     *
     * OPTIMIZED DRY RUN
     *
     * nums = [1,2,1]
     * first pass index 0 value 1 waits: stack [0]
     * index 1 value 2 is greater than nums[0], so pop 0 and answer[0] = 2
     * index 1 waits, index 2 value 1 waits behind it: stack [1,2]
     * second pass index 1 value 2 is greater than nums[2], so pop 2.
     * answer[2] = 2. index 1 stays because no bigger value exists.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public int[] optimized(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];
        Arrays.fill(answer, -1);
        Stack<Integer> waitingIndexes = new Stack<>();

        for (int i = 0; i < 2 * n; i++) {
            int currentIndex = i % n;

            while (!waitingIndexes.isEmpty() && nums[currentIndex] > nums[waitingIndexes.peek()]) {
                // Current value is the next greater answer for the index on top.
                answer[waitingIndexes.pop()] = nums[currentIndex];
            }

            if (i < n) {
                // Push each real index once; the second pass only resolves waiting answers.
                waitingIndexes.push(currentIndex);
            }
        }

        return answer;
    }
}
