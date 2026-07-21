package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Frequency Count (Brute Force)
 *
 * DESCRIPTION:
 * Given an array of integers, count the frequency of each element.
 *
 * Example 1:
 * Input: nums = [1, 2, 2, 3, 3, 3]
 * Output: {1=1, 2=2, 3=3}
 * Explanation: 1 appears once, 2 appears twice, 3 appears three times.
 *
 * Example 2:
 * Input: nums = [4, 4, 4, 4]
 * Output: {4=4}
 * Explanation: 4 appears four times.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 *
 * APPROACH:
 * 1. Use HashMap to store element -> count
 * 2. For each num, map.put(num, map.getOrDefault(num, 0) + 1)
 * Time: O(n), Space: O(n)
 */
import java.util.HashMap;
import java.util.Map;

public class A10_FrequencyCount {

    public static void main(String[] args) {
        // 1. Create our array containing duplicate numbers.
        int[] nums = {4, 5, 4, 8, 5, 4};

        // 2. Initialize our tally sheet (HashMap).
        // The first 'Integer' represents the Key (the number itself).
        // The second 'Integer' represents the Value (its frequency count).
        Map<Integer, Integer> frequencyMap = new HashMap<>();

        // 3. Start a loop to visit every number in our array using a clean enhanced for-loop.
        for (int num : nums) {

            // 4. Update the tally sheet for the current number.
            // 'frequencyMap.getOrDefault(num, 0)' does two things:
            //   - If 'num' is already in the map, it fetches its current count.
            //   - If 'num' is brand new, it safely returns 0 as a baseline.
            // Then, we add 1 to that count and save it back into the map using '.put()'.
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }

        // 5. Print the entire tally sheet to the console.
        // Under the hood, Java beautifully prints all key-value pairs stored in the map.
        System.out.println("Frequency Count: " + frequencyMap);
    }
}