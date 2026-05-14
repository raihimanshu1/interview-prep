import java.util.Arrays;

public class SortAnArray {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sort an integer array in ascending order.
     *
     * Sample Input: nums = [5,2,3,1]
     * Sample Output: [1,2,3,5]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Sorting means arranging numbers from smallest to largest. A simple way is
     * to repeatedly place the smallest remaining number into the next position.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Selection sort is beginner-friendly: for each position, find the smallest
     * value in the unsorted part and swap it into place.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Copy the input so the original is not changed.
     * 2. For each index, find the smallest value from index to end.
     * 3. Swap that smallest value into index.
     * 4. Return the sorted copy.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n) for the returned copy
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * [5,2,3,1]
     * Smallest is 1, swap to front: [1,2,3,5].
     * Next smallest in the rest is 2, already correct.
     */
    public int[] bruteForce(int[] nums) {
        int[] arr = Arrays.copyOf(nums, nums.length);

        for (int i = 0; i < arr.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }

            swap(arr, i, minIndex);
        }

        return arr;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Merge sort divides the array into halves, sorts each half, then merges the
     * sorted halves. The brute force pain point is repeatedly scanning the
     * unsorted tail to find the next smallest value. Merge sort removes that
     * repeated scanning by sorting smaller pieces and combining them in linear
     * time per level.
     *
     * Quicksort also partitions around a pivot and is often fast in practice,
     * but its worst case can be O(n^2). For LeetCode 912, merge sort is a safer
     * optimized default because the time bound is predictable.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Copy nums.
     * 2. Recursively sort left half and right half.
     * 3. Merge the two sorted halves using a temporary array.
     * 4. Return the sorted copy.
     *
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * [5,2,3,1] splits into [5,2] and [3,1].
     * They become [2,5] and [1,3].
     * Merging gives [1,2,3,5].
     */
    public int[] optimized(int[] nums) {
        int[] arr = Arrays.copyOf(nums, nums.length);
        int[] temp = new int[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1);
        return arr;
    }

    public int[] quickSortVersion(int[] nums) {
        int[] arr = Arrays.copyOf(nums, nums.length);
        quickSort(arr, 0, arr.length - 1);
        return arr;
    }

    private void mergeSort(int[] arr, int[] temp, int left, int right) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;
        // Sort both halves before trying to combine them.
        mergeSort(arr, temp, left, mid);
        mergeSort(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private void merge(int[] arr, int[] temp, int left, int mid, int right) {
        int i = left;
        int j = mid + 1;
        int index = left;

        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                // Smaller front value belongs next in the merged order.
                temp[index++] = arr[i++];
            } else {
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
            // Copy only this merged range back into the main array.
            arr[k] = temp[k];
        }
    }

    private void quickSort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }

        int pivotIndex = partition(arr, left, right);
        quickSort(arr, left, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, right);
    }

    private int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int smaller = left;

        for (int index = left; index < right; index++) {
            if (arr[index] <= pivot) {
                // Keep values <= pivot on the left side of the partition.
                swap(arr, smaller, index);
                smaller++;
            }
        }

        swap(arr, smaller, right);
        return smaller;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
