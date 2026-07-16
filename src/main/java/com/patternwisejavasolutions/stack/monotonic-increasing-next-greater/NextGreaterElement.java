package com.patternwisejavasolutions.stack.monotonicincreasingnextgreater;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class NextGreaterElement {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * For each number in nums1, find the next greater number on its right in nums2.
     *
     * Sample Input:
     * nums1 = [4, 1, 2]
     * nums2 = [1, 3, 4, 2]
     *
     * Sample Output:
     * [-1, 3, -1]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * "Next greater" means:
     * start from the number's position, move right,
     * and find the first bigger number.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For each nums1 value, find it in nums2.
     * Then scan right until a bigger value appears.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums1 value = 1
     * nums2 = [1, 3, 4, 2]
     *
     * 1 is at index 0.
     * Look right: 3 is bigger.
     * Answer for 1 is 3.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each nums1 value, find its index in nums2.
     * 2. Scan right from that index.
     * 3. First bigger number is the answer.
     * 4. If no bigger number is found, keep -1.
     *
     * Time Complexity: O(n * m)
     * Space Complexity: O(1), not counting output
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int[] bruteForce(int[] nums1, int[] nums2) {
        int[] answer = new int[nums1.length];

        for (int i = 0; i < nums1.length; i++) {
            answer[i] = -1;
            int position = findIndex(nums2, nums1[i]);

            for (int j = position + 1; j < nums2.length; j++) {
                if (nums2[j] > nums1[i]) {
                    answer[i] = nums2[j];
                    break;
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Brute force searches to the right separately for each nums1 value. We can
     * scan nums2 once and solve next-greater answers as soon as they become
     * obvious.
     *
     * Use a stack to keep numbers that are still waiting for their next greater
     * element. The stack is decreasing from bottom to top, because a bigger
     * current number pops smaller waiting numbers.
     *
     * When current number is bigger than stack top,
     * current becomes the answer for that stack top.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums2 = [1, 3, 4, 2]
     *
     * see 1 -> stack [1]
     * see 3 -> 3 is greater than 1, pop 1 and map 1 -> 3
     * see 4 -> 4 is greater than 3, pop 3 and map 3 -> 4
     * see 2 -> 2 is not greater than 4, so it waits behind 4: stack [4, 2]
     *
     * 4 and 2 have no greater element, so -1.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Scan nums2 from left to right.
     * 2. Stack stores numbers waiting for a greater number.
     * 3. While current is greater than stack top, current is that answer.
     * 4. Save answers in a map.
     * 5. Build result for nums1 using the map.
     *
     * Time Complexity: O(n + m)
     * Space Complexity: O(m)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * If nums2 were circular, the same monotonic stack could scan nums2 twice.
     * For this non-circular version, one left-to-right pass builds all answers
     * needed by nums1.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int[] optimized(int[] nums1, int[] nums2) {
        Map<Integer, Integer> nextGreater = new HashMap<>();
        Stack<Integer> waiting = new Stack<>();

        for (int current : nums2) {
            while (!waiting.isEmpty() && current > waiting.peek()) {
                // Current is the first greater value after this smaller waiting number.
                int smallerNumber = waiting.pop();
                nextGreater.put(smallerNumber, current);
            }

            // Current waits until a larger number appears later.
            waiting.push(current);
        }

        int[] answer = new int[nums1.length];

        for (int i = 0; i < nums1.length; i++) {
            answer[i] = nextGreater.getOrDefault(nums1[i], -1);
        }

        return answer;
    }

    private int findIndex(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                return i;
            }
        }

        return -1;
    }
}
