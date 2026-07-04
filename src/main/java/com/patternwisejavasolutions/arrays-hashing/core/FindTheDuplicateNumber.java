
package com.patternwisejavasolutions.arraysHashing.core;
import java.util.HashSet;
import java.util.Set;

public class FindTheDuplicateNumber {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We have n + 1 numbers, and every number is between 1 and n. At least one
     * number is repeated. Return the repeated number.
     *
     * Sample Input:
     * nums = [1, 3, 4, 2, 2]
     *
     * Sample Output:
     * 2
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * There are more items than allowed unique labels. If 5 students choose
     * desk numbers from only 1 to 4, at least two students must choose the same
     * desk. This is the pigeonhole idea.
     *
     * Brute force compares values. The optimized pattern treats each value as a
     * pointer to another index, which creates a cycle. The cycle entrance is the
     * duplicate number.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Compare every pair of positions. If two positions hold the same value,
     * that value is duplicate.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick index i.
     * 2. Compare nums[i] with every nums[j] after it.
     * 3. Return nums[i] when a match is found.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1, 3, 4, 2, 2]
     * When i points to the first 2, scanning later finds another 2.
     * Return 2.
     */
    public int bruteForce(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) {
                    return nums[i];
                }
            }
        }

        return -1;
    }

    public int hashingApproach(int[] nums) {
        Set<Integer> seen = new HashSet<>();

        for (int num : nums) {
            if (seen.contains(num)) {
                return num;
            }
            seen.add(num);
        }

        return -1;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is comparing too many pairs. Hashing improves
     * this, but uses extra space. Floyd's cycle detection uses the special rule
     * that every value points to a valid index.
     *
     * Example: index -> nums[index]. Because a value repeats, two paths point
     * into the same place, creating a cycle. The duplicate is where the cycle
     * begins.
     *
     * Why repeated value means cycle entrance:
     * Values are between 1 and n, so every value can be used as the next index.
     * Starting from nums[0], we keep jumping to another valid index forever.
     * Since there are limited indexes, eventually we must visit an index again.
     * The repeated number is the first shared doorway into that loop: two different
     * positions point to the same value.
     *
     * Why moving from start and meeting works:
     * This is the same Floyd math as linked-list cycle start. The meeting point is
     * inside the loop. One pointer starts at nums[0], one starts at the meeting value.
     * Moving both one jump at a time makes them meet at the loop entrance, which is
     * the duplicate value.
     *
     * Other useful approach: HashSet in O(n) time and O(n) space.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Move slow by one step: slow = nums[slow].
     * 2. Move fast by two steps: fast = nums[nums[fast]].
     * 3. Stop when they meet inside the cycle.
     * 4. Start another pointer at index 0.
     * 5. Move both one step until they meet; that value is duplicate.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1, 3, 4, 2, 2]
     * Following values as pointers eventually loops through value 2.
     * After slow and fast meet, moving one pointer from start and one from the
     * meeting point makes them meet at 2.
     */
    public int optimized(int[] nums) {
        int slow = nums[0];
        int fast = nums[0];

        do {
            // slow walks one pointer step.
            slow = nums[slow];
            // fast walks two pointer steps, so it must meet slow inside the cycle.
            fast = nums[nums[fast]];
        } while (slow != fast);

        int fromStart = nums[0];
        int fromMeeting = slow;

        while (fromStart != fromMeeting) {
            /*
             * Both pointers are now the same number of jumps away from the cycle entrance.
             * In this problem the entrance value is the duplicate number.
             */
            fromStart = nums[fromStart];
            fromMeeting = nums[fromMeeting];
        }

        return fromStart;
    }
}
