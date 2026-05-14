public class CountInversionsInArray {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Count pairs (i, j) where i < j but nums[i] > nums[j].
     * Such a pair is called an inversion because the larger number appears first.
     *
     * Sample Input: nums = [2,4,1,3,5]
     * Sample Output: 3
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If a taller student stands before a shorter student in an increasing-height
     * line, that pair is out of order. Count all such out-of-order pairs.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Check every pair and count the pairs where the left value is bigger.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Choose i from 0 to n - 1.
     * 2. Choose j after i.
     * 3. If nums[i] > nums[j], increase count.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [2,4,1,3,5]
     * Inversions are (2,1), (4,1), and (4,3), so answer is 3.
     */
    public long bruteForce(int[] nums) {
        long count = 0;

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] > nums[j]) {
                    count++;
                }
            }
        }

        return count;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Merge sort already compares left-half values with right-half values. When
     * a right value is smaller than a left value, it is smaller than every
     * remaining value in the sorted left half.
     *
     * That removes the brute force waste: instead of checking each pair one by
     * one, one comparison can count many inversions at once.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Split the array like merge sort.
     * 2. Count inversions in the left half.
     * 3. Count inversions in the right half.
     * 4. During merge, count cross inversions.
     *
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Merge [2,4] with [1,3,5].
     * 1 is smaller than 2, so it creates 2 inversions: (2,1), (4,1).
     * 3 is smaller than 4, so it creates 1 inversion: (4,3).
     */
    public long optimized(int[] nums) {
        int[] copy = nums.clone();
        int[] temp = new int[nums.length];
        return mergeSortAndCount(copy, temp, 0, nums.length - 1);
    }

    private long mergeSortAndCount(int[] arr, int[] temp, int left, int right) {
        if (left >= right) {
            return 0;
        }

        int mid = left + (right - left) / 2;
        long count = 0;
        // Count inversions fully inside each half first.
        count += mergeSortAndCount(arr, temp, left, mid);
        count += mergeSortAndCount(arr, temp, mid + 1, right);
        // Then count pairs where the left value is in the left half and the
        // smaller right value is in the right half.
        count += mergeAndCount(arr, temp, left, mid, right);
        return count;
    }

    private long mergeAndCount(int[] arr, int[] temp, int left, int mid, int right) {
        int i = left;
        int j = mid + 1;
        int index = left;
        long count = 0;

        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[index++] = arr[i++];
            } else {
                // arr[j] is smaller than arr[i] through arr[mid].
                count += mid - i + 1;
                temp[index++] = arr[j++];
            }
        }

        while (i <= mid) {
            temp[index++] = arr[i++];
        }

        while (j <= right) {
            temp[index++] = arr[j++];
        }

        for (int k = left; k <= right; k++) {
            // Keep arr sorted so parent merge steps can count in batches too.
            arr[k] = temp[k];
        }

        return count;
    }
}
