

package com.companywisejavasolutions.ebay.solutions;
import java.util.Arrays;

public class MatchsticksToSquare {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Decide if all matchsticks can form a square, using every matchstick once.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A square needs four equal sides. Try placing each stick into one of four
     * buckets without letting any bucket exceed the target side length.
     */
    public boolean makesquare(int[] matchsticks) {
        int sum = 0;
        for (int stick : matchsticks) sum += stick;
        if (sum % 4 != 0) return false;

        Arrays.sort(matchsticks);
        reverse(matchsticks);
        return backtrack(matchsticks, 0, new int[4], sum / 4);
    }

    private boolean backtrack(int[] sticks, int index, int[] sides, int target) {
        if (index == sticks.length) {
            return sides[0] == target && sides[1] == target && sides[2] == target;
        }

        for (int i = 0; i < 4; i++) {
            if (sides[i] + sticks[index] > target) continue;
            sides[i] += sticks[index];
            if (backtrack(sticks, index + 1, sides, target)) return true;
            sides[i] -= sticks[index];
            if (sides[i] == 0) break;
        }

        return false;
    }

    private void reverse(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int temp = nums[left];
            nums[left++] = nums[right];
            nums[right--] = temp;
        }
    }
}
