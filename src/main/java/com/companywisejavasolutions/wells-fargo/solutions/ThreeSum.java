
package com.companywisejavasolutions.wellsFargo.solutions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreeSum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return all unique triplets [a, b, c] whose sum is 0.
     *
     * Sample Input:
     * nums = [-1, 0, 1, 2, -1, -4]
     *
     * Sample Output:
     * [[-1, -1, 2], [-1, 0, 1]]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Three numbers must balance to zero. If we fix one number, the remaining
     * job becomes Two Sum: find two numbers that add to the opposite value.
     * Sorting helps avoid duplicates and gives us the two-pointer pattern.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Try every possible group of three numbers. Sort each found triplet so the
     * same values are not added in different orders.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Use i, j, k to choose three different indices.
     * 2. If their sum is 0, sort the triplet.
     * 3. Add it to a set to avoid duplicates.
     * 4. Convert the set to a list.
     *
     * Time Complexity: O(n^3)
     * Space Complexity: O(number of answers)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Pick -1, 0, and 1. Their sum is 0, so add [-1, 0, 1].
     * Pick -1, -1, and 2. Their sum is 0, so add [-1, -1, 2].
     */
    public List<List<Integer>> bruteForce(int[] nums) {
        Set<List<Integer>> unique = new HashSet<>();

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        List<Integer> triplet = Arrays.asList(nums[i], nums[j], nums[k]);
                        triplet.sort(Integer::compareTo);
                        unique.add(triplet);
                    }
                }
            }
        }

        return new ArrayList<>(unique);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is choosing all three numbers blindly. Sort the
     * array, fix the first number, then use two pointers to find the other two.
     *
     * If the sum is too small, moving left rightward increases it. If the sum is
     * too large, moving right leftward decreases it. This is Two Sum II inside
     * Three Sum.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Sort nums.
     * 2. Fix index i as the first value.
     * 3. Skip duplicate first values.
     * 4. Use left and right pointers after i.
     * 5. Move pointers based on the sum and skip duplicates after finding an answer.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1) besides the answer
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sorted nums = [-4, -1, -1, 0, 1, 2]
     * Fix -1. left = -1, right = 2 gives 0, add [-1, -1, 2].
     * Continue and find [-1, 0, 1].
     */
    public List<List<Integer>> optimized(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }

            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    left++;
                    right--;

                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return result;
    }
}

