public class MedianOfTwoSortedArrays {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given two sorted arrays, return the median of all their numbers together.
     *
     * Sample Input: nums1 = [1,3], nums2 = [2]
     * Sample Output: 2.0
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Median is the middle value after all numbers are sorted. Since both arrays are already
     * sorted, the real question is where to split them so the left side contains the smaller half
     * and the right side contains the larger half.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Merge both sorted arrays into one sorted array, then read the middle. This is the natural
     * first attempt because once everything is in one sorted list, median is easy.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Merge nums1 and nums2 like merge sort.
     * 2. If total length is odd, return the middle element.
     * 3. If even, return the average of the two middle elements.
     *
     * Time Complexity: O(m + n)
     * Space Complexity: O(m + n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums1 = [1,3], nums2 = [2]
     * Merged = [1,2,3]. The middle is 2.
     */
    public double bruteForce(int[] nums1, int[] nums2) {
        int[] merged = new int[nums1.length + nums2.length];
        int i = 0;
        int j = 0;
        int index = 0;

        while (i < nums1.length || j < nums2.length) {
            if (j == nums2.length || (i < nums1.length && nums1[i] <= nums2[j])) {
                merged[index++] = nums1[i++];
            } else {
                merged[index++] = nums2[j++];
            }
        }

        int total = merged.length;
        if (total % 2 == 1) {
            return merged[total / 2];
        }

        return (merged[total / 2 - 1] + merged[total / 2]) / 2.0;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * The brute force waste is building the whole merged list even though the median only needs
     * the border between two halves. We binary search how many numbers to take from the smaller
     * array so every left-half number is less than or equal to every right-half number.
     *
     * Think of the final sorted array as two boxes:
     * left box  = smaller half
     * right box = larger half
     *
     * We do not need to build the boxes. We only need to know the four border values:
     * biggest value on left side of nums1, smallest value on right side of nums1,
     * biggest value on left side of nums2, smallest value on right side of nums2.
     *
     * The split is correct only when both left-border values can safely stay before both
     * right-border values.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Binary search on the smaller array.
     * 2. Choose cut1 from nums1 and cut2 from nums2 so left half has enough values.
     * 3. If left1 <= right2 and left2 <= right1, the split is correct.
     * 4. Return middle value for odd total or average for even total.
     * 5. Otherwise move the cut left or right.
     *
     * Time Complexity: O(log(min(m, n)))
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums1 = [1,3], nums2 = [2]
     * Use smaller [2]. Correct split puts [1] and [2] on the left side.
     * Total length is odd, so median is max(left side) = 2.
     *
     * Bigger example idea:
     * nums1 = [1, 2], nums2 = [3, 4]
     * We need leftSize = (4 + 1) / 2 = 2 values on the left.
     * If cut1 = 1, cut2 = 1:
     * left side has [1] and [3], right side has [2] and [4].
     * This is wrong because 3 > 2, so we took too few from nums1 and too many from nums2.
     */
    public double optimized(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return optimized(nums2, nums1);
        }

        int total = nums1.length + nums2.length;
        /*
         * For odd total, left side gets one extra value.
         * Example total 5 -> leftSize 3, so median is max(left side).
         * For even total, left and right are equal sized.
         */
        int leftSize = (total + 1) / 2;
        int left = 0;
        int right = nums1.length;

        while (left <= right) {
            int cut1 = left + (right - left) / 2;
            /*
             * cut1 means: take cut1 numbers from nums1 into the left box.
             * cut2 must fill the remaining left-box slots from nums2.
             */
            int cut2 = leftSize - cut1;

            /*
             * If a cut takes nothing from one side, there is no real border value.
             * MIN_VALUE and MAX_VALUE act like safe imaginary borders so the same
             * comparison code still works.
             */
            int left1 = cut1 == 0 ? Integer.MIN_VALUE : nums1[cut1 - 1];
            int right1 = cut1 == nums1.length ? Integer.MAX_VALUE : nums1[cut1];
            int left2 = cut2 == 0 ? Integer.MIN_VALUE : nums2[cut2 - 1];
            int right2 = cut2 == nums2.length ? Integer.MAX_VALUE : nums2[cut2];

            if (left1 <= right2 && left2 <= right1) {
                /*
                 * Both biggest-left values are <= both smallest-right values.
                 * That means every value in the left box can legally come before
                 * every value in the right box.
                 */
                if (total % 2 == 1) {
                    return Math.max(left1, left2);
                }

                return (Math.max(left1, left2) + Math.min(right1, right2)) / 2.0;
            }

            if (left1 > right2) {
                /*
                 * nums1 contributed a value that is too large for the left box.
                 * So reduce how many values we take from nums1.
                 */
                right = cut1 - 1;
            } else {
                /*
                 * nums2 contributed a value too large for the left box.
                 * To balance it, take more from nums1 and fewer from nums2.
                 */
                left = cut1 + 1;
            }
        }

        return 0.0;
    }
}
