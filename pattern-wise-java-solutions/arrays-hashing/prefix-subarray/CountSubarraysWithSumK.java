import java.util.HashMap;
import java.util.Map;

public class CountSubarraysWithSumK {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Count continuous subarrays whose sum equals k.
 *
 * Sample Input:
 * nums = [1, 2, 3], k = 3
 *
 * Sample Output:
 * 2
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In [1, 2, 3] with k = 3, both [1, 2] and [3] count. The answer is counting
 * how many continuous pieces hit k, not just asking whether one exists.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * Continuous means we cannot skip elements. Prefix sums let us compare the
 * total up to two different places. Their difference is exactly the continuous
 * subarray between them.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to try each start and grow the end while adding the
 * current number to a running sum.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. For each start index, set sum to 0.
 * 2. Move end from start to the end of the array.
 * 3. Add nums[end].
 * 4. Count when sum equals k.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * nums = [1, 2, 3], k = 3
 * start 0: 1 + 2 = 3, count [1,2].
 * start 2: 3 = 3, count [3].
 * Total count is 2.
 */

public int bruteForce(int[] nums, int k) {
        int answer = 0;

        for (int start = 0; start < nums.length; start++) {
            int sum = 0;
            for (int end = start; end < nums.length; end++) {
                sum += nums[end];
                if (sum == k) {
                    answer++;
                }
            }
        }

        return answer;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is checking all starts again and again. If prefix
 * is current total, we need an old prefix equal to prefix - k. A map tells us
 * how many such old prefixes exist in O(1) average time.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Store prefix sum 0 with count 1.
 * 2. Update prefix while scanning nums.
 * 3. Add frequency of prefix - k to answer.
 * 4. Increase frequency of current prefix.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * After reading 1 and 2, prefix is 3.
 * prefix - k = 0, seen once, so [1,2] is counted.
 * After reading 3 alone from a later start, another needed old prefix is found.
 */

public int optimized(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1);

        int prefix = 0;
        int answer = 0;

        for (int num : nums) {
            prefix += num;
            // Each old prefix of prefix - k forms one valid subarray ending here.
            answer += prefixCount.getOrDefault(prefix - k, 0);
            prefixCount.put(prefix, prefixCount.getOrDefault(prefix, 0) + 1);
        }

        return answer;
    }
}
