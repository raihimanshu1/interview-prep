package com.patternwisejavasolutions.slidingwindowtwopointers.sumproductbased;

import java.util.HashMap;
import java.util.Map;

public class BinarySubarraysWithSum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * nums contains only 0 and 1.
     * Count subarrays whose sum is exactly goal.
     *
     * Sample Input:
     * nums = [1, 0, 1, 0, 1], goal = 2
     *
     * Sample Output:
     * 4
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Since subarray means continuous, try every start and every end.
     * Keep the sum while expanding.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Pick every possible start and keep adding the next number to a running
     * sum. Whenever that running sum equals goal, the subarray from start to
     * end is one answer.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * start = 0:
     * [1] sum = 1
     * [1,0] sum = 1
     * [1,0,1] sum = 2, count = 1
     * [1,0,1,0] sum = 2, count = 2
     * [1,0,1,0,1] sum = 3
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick start.
     * 2. Expand end and update running sum.
     * 3. If sum equals goal, count it.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] nums, int goal) {
        int answer = 0;

        for (int start = 0; start < nums.length; start++) {
            int sum = 0;

            for (int end = start; end < nums.length; end++) {
                sum += nums[end];

                if (sum == goal) {
                    answer++;
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * If current prefix sum is S, we want an earlier prefix sum equal to S - goal.
     *
     * Why?
     * subarray sum = current prefix - earlier prefix
     * goal = current prefix - earlier prefix
     * earlier prefix = current prefix - goal
     *
     * A map remembers how many times each prefix sum appeared.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,0,1], goal = 2
     *
     * Start prefix count: 0 appeared once.
     * prefix = 1 -> need -1, none.
     * prefix = 1 -> need -1, none.
     * prefix = 2 -> need 0, found once, count = 1.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Store prefix sum frequencies in a map.
     * 2. Start with prefix 0 appearing once.
     * 3. Add each number to prefix.
     * 4. Add map[prefix - goal] to answer.
     * 5. Store current prefix.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Prefix sums with a frequency map can count previous sums equal to
     * currentSum - goal. Because the array is binary, the atMost(goal) minus
     * atMost(goal - 1) sliding-window trick also works well.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] nums, int goal) {
        Map<Integer, Integer> prefixFrequency = new HashMap<>();
        prefixFrequency.put(0, 1);

        int prefixSum = 0;
        int answer = 0;

        for (int num : nums) {
            prefixSum += num;

            int neededPrefix = prefixSum - goal;
            // Any earlier prefix with this value forms a subarray sum of goal.
            answer += prefixFrequency.getOrDefault(neededPrefix, 0);

            prefixFrequency.put(
                prefixSum,
                prefixFrequency.getOrDefault(prefixSum, 0) + 1
            );
        }

        return answer;
    }
}
