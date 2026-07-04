
package com.patternwisejavasolutions.arraysHashing.prefixSubarray;
import java.util.HashMap;
import java.util.Map;

public class SubarraySumEqualsK {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Count continuous subarrays whose sum equals k.
 *
 * Sample Input:
 * nums = [1, 1, 1], k = 2
 *
 * Sample Output:
 * 2
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In [1, 1, 1] with k = 2, there are two answers because the first two 1's
 * make 2 and the last two 1's also make 2. The subarrays must be continuous,
 * and overlapping answers still count separately.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * A subarray sum can be found from two prefix sums. If currentPrefix -
 * oldPrefix = k, then the numbers between those two positions sum to k.
 *
 * This is prefix sum plus hashing: keep a memory of old prefix sums so the
 * needed oldPrefix can be found quickly.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to choose a start, grow the end one step at a time,
 * and keep adding values. Whenever the running sum becomes k, count it.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Choose start.
 * 2. Add values as end moves right.
 * 3. When running sum equals k, increase count.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * nums = [1, 1, 1], k = 2
 * start 0: sum 1, then 2 -> count 1 for [1,1].
 * start 1: sum 1, then 2 -> count 2 for the second [1,1].
 */

public int bruteForce(int[] nums, int k) {
        int count = 0;

        for (int start = 0; start < nums.length; start++) {
            int sum = 0;
            for (int end = start; end < nums.length; end++) {
                sum += nums[end];
                if (sum == k) {
                    count++;
                }
            }
        }

        return count;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is trying every possible start for each end.
 * Store how many times each prefix sum has appeared. For current prefix, any
 * previous prefix equal to current - k creates a valid subarray ending here.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Put prefix 0 in the map once.
 * 2. Walk through nums and update prefix.
 * 3. Add count of prefix - k to answer.
 * 4. Store current prefix count.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * prefix after first two 1s is 2.
 * prefix - k = 2 - 2 = 0.
 * Prefix 0 was seen once before the array started, so [1,1] is counted.
 */

public int optimized(int[] nums, int k) {
        Map<Integer, Integer> prefixCounts = new HashMap<>();
        prefixCounts.put(0, 1);

        int prefix = 0;
        int count = 0;

        for (int num : nums) {
            prefix += num;
            // Any earlier prefix of prefix - k leaves a middle subarray summing to k.
            count += prefixCounts.getOrDefault(prefix - k, 0);
            // Store this prefix for subarrays that may end later.
            prefixCounts.put(prefix, prefixCounts.getOrDefault(prefix, 0) + 1);
        }

        return count;
    }
}
