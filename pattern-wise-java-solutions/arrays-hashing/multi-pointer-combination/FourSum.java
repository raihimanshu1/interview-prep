import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FourSum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return all unique groups of four numbers whose sum equals target.
     *
     * Sample Input: nums = [1,0,-1,0,-2,2], target = 0
     * Sample Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * We need four numbers to balance to target. If we fix two numbers, the
     * remaining work becomes finding two numbers that complete the sum.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Try every possible group of four indices and keep the groups that match.
     * A set removes duplicate value groups.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Use four loops to choose i, j, k, and l.
     * 2. If the sum equals target, sort that quadruplet.
     * 3. Store it in a set so duplicates appear once.
     * 4. Convert the set to a list.
     *
     * Time Complexity: O(n^4)
     * Space Complexity: O(number of answers)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Pick [-2, -1, 1, 2]. Sum is 0, so save it.
     * Pick [-2, 0, 0, 2]. Sum is 0, so save it.
     */
    public List<List<Integer>> bruteForce(int[] nums, int target) {
        Set<List<Integer>> unique = new HashSet<>();

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    for (int l = k + 1; l < nums.length; l++) {
                        long sum = (long) nums[i] + nums[j] + nums[k] + nums[l];
                        if (sum == target) {
                            List<Integer> quad = Arrays.asList(nums[i], nums[j], nums[k], nums[l]);
                            quad.sort(Integer::compareTo);
                            unique.add(quad);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(unique);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Sorting lets us fix the first two numbers and use two pointers for the
     * last two numbers. The brute force waste is choosing all four numbers
     * blindly. After two numbers are fixed, sorted order tells us how to move
     * the remaining two pointers.
     *
     * We skip repeated values to avoid duplicate answers.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Sort nums.
     * 2. Fix i and skip duplicate first values.
     * 3. Fix j and skip duplicate second values.
     * 4. Use left and right pointers to find the remaining sum.
     * 5. Move pointers based on whether the sum is too small or too large.
     *
     * Time Complexity: O(n^3)
     * Space Complexity: O(1) besides the answer
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sorted nums = [-2,-1,0,0,1,2]
     * Fix -2 and -1. Need 3, so left/right find 1 and 2.
     * Fix -2 and 0. Need 2, so left/right find 0 and 2.
     */
    public List<List<Integer>> optimized(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < nums.length - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                // Same first value would produce the same quadruplets again.
                continue;
            }

            for (int j = i + 1; j < nums.length - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) {
                    // Same second value under the same i repeats work.
                    continue;
                }

                int left = j + 1;
                int right = nums.length - 1;

                while (left < right) {
                    long sum = (long) nums[i] + nums[j] + nums[left] + nums[right];

                    if (sum == target) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                        left++;
                        right--;

                        while (left < right && nums[left] == nums[left - 1]) {
                            // Skip duplicate third values after recording an answer.
                            left++;
                        }
                        while (left < right && nums[right] == nums[right + 1]) {
                            // Skip duplicate fourth values after recording an answer.
                            right--;
                        }
                    } else if (sum < target) {
                        // Need a larger total; sorted order makes moving left useful.
                        left++;
                    } else {
                        // Need a smaller total; move the larger side inward.
                        right--;
                    }
                }
            }
        }

        return result;
    }
}
