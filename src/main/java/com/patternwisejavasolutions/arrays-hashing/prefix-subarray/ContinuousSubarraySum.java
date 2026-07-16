package com.patternwisejavasolutions.arrayshashing.prefixsubarray;

import java.util.HashMap;
import java.util.Map;

public class ContinuousSubarraySum {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Return true if a subarray of length at least 2 has a sum that is a multiple of k.
 *
 * Sample Input:
 * nums = [23, 2, 4, 6, 7], k = 6
 *
 * Sample Output:
 * true
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In [23, 2, 4, 6, 7] with k = 6, the piece [2, 4] sums to 6. The length rule
 * matters: the matching piece must contain at least two numbers.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * If two prefix sums have the same remainder when divided by k, their
 * difference is divisible by k. That difference is the subarray sum between the
 * two positions.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to try every subarray of length at least 2 and
 * check whether its sum is divisible by k.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Choose start.
 * 2. Grow end and keep sum.
 * 3. If length is at least 2 and sum % k == 0, return true.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * nums = [23, 2, 4, 6, 7], k = 6
 * subarray [2,4] has sum 6.
 * 6 is divisible by 6, so return true.
 */

    public boolean bruteForce(int[] nums, int k) {
        for (int start = 0; start < nums.length; start++) {
            int sum = 0;
            for (int end = start; end < nums.length; end++) {
                sum += nums[end];
                if (end - start + 1 >= 2 && isMultipleOfK(sum, k)) {
                    return true;
                }
            }
        }

        return false;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is checking every start for every end. Store the
 * earliest index where each prefix remainder appeared. Seeing the same
 * remainder again means the middle subarray is divisible by k.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Store remainder 0 at index -1.
 * 2. Track prefix sum.
 * 3. Compute remainder prefix % k.
 * 4. If seen before and distance is at least 2, return true.
 * 5. Store first occurrence only.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(k) or O(n)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * prefix after 23 has remainder 5.
 * prefix after 23 + 2 + 4 is 29, also remainder 5.
 * The distance is 2, so the subarray [2,4] has sum divisible by 6.
 */

    public boolean optimized(int[] nums, int k) {
        if (k == 0) {
            return hasLengthTwoSubarrayWithZeroSum(nums);
        }

        Map<Integer, Integer> firstIndexByRemainder = new HashMap<>();
        firstIndexByRemainder.put(0, -1);

        int prefix = 0;
        for (int i = 0; i < nums.length; i++) {
            prefix += nums[i];
            int remainder = prefix % k;

            if (firstIndexByRemainder.containsKey(remainder)) {
                if (i - firstIndexByRemainder.get(remainder) >= 2) {
                    return true;
                }
            } else {
                // Keep the earliest index to maximize possible subarray length.
                firstIndexByRemainder.put(remainder, i);
            }
        }

        return false;
    }

    private boolean isMultipleOfK(int sum, int k) {
        if (k == 0) {
            return sum == 0;
        }
        return sum % k == 0;
    }

    private boolean hasLengthTwoSubarrayWithZeroSum(int[] nums) {
        Map<Integer, Integer> firstIndexByPrefix = new HashMap<>();
        firstIndexByPrefix.put(0, -1);

        int prefix = 0;
        for (int i = 0; i < nums.length; i++) {
            prefix += nums[i];

            if (firstIndexByPrefix.containsKey(prefix)) {
                if (i - firstIndexByPrefix.get(prefix) >= 2) {
                    return true;
                }
            } else {
                firstIndexByPrefix.put(prefix, i);
            }
        }

        return false;
    }
}
