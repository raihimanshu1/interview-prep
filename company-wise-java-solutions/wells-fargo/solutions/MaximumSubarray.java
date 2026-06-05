package wellsfargo.solutions;

public class MaximumSubarray {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Find the largest possible sum of any continuous subarray.
 *
 * Sample Input:
 * nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
 *
 * Sample Output:
 * 6
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * The sample answer 6 comes from [4, -1, 2, 1]. Notice that the best answer can
 * start in the middle after earlier negative numbers, and it must stay
 * continuous.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * A subarray must stay continuous. If a previous running sum becomes negative,
 * carrying it forward only hurts the next subarray. This is the key idea behind
 * Kadane's pattern.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to try every possible start and end, compute that
 * subarray's sum, and keep the best sum seen.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Choose start index.
 * 2. Choose end index.
 * 3. Sum values from start to end.
 * 4. Track the largest sum.
 *
 * Time Complexity: O(n^3)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * In [-2,1,-3,4,-1,2,1,-5,4], the subarray [4,-1,2,1] has sum 6.
 * No checked subarray has a larger sum, so the answer is 6.
 */

public int bruteForce(int[] nums) {
        int best = Integer.MIN_VALUE;

        for (int start = 0; start < nums.length; start++) {
            for (int end = start; end < nums.length; end++) {
                int sum = 0;
                for (int i = start; i <= end; i++) {
                    sum += nums[i];
                }
                best = Math.max(best, sum);
            }
        }

        return best;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is recomputing subarray sums. Kadane's idea asks:
 * should the current number join the previous subarray, or start fresh here?
 * If the previous sum is negative, it drags the current number down.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Set current and best to nums[0].
 * 2. For each next number, current = max(num, current + num).
 * 3. best = max(best, current).
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * At 4 after a bad negative sum, current starts fresh at 4.
 * Then -1, 2, and 1 grow the running sum to 6.
 * best is updated to 6.
 */

public int optimized(int[] nums) {
        int current = nums[0];
        int best = nums[0];

        for (int i = 1; i < nums.length; i++) {
            // Either extend the previous subarray or start a new one at nums[i].
            current = Math.max(nums[i], current + nums[i]);
            // best remembers the strongest subarray ending anywhere so far.
            best = Math.max(best, current);
        }

        return best;
    }
}
