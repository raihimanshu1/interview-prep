

package com.companywisejavasolutions.ebay.solutions;
import java.util.Arrays;

public class EarliestPossibleDayOfFullBloom {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Each seed takes plantTime days to plant and growTime days after planting to
     * bloom. Plant one seed at a time. Return the earliest day all flowers bloom.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Seeds with longer grow time should start growing earlier. So plant them
     * before shorter-grow seeds.
     */
    public int earliestFullBloom(int[] plantTime, int[] growTime) {
        Integer[] order = new Integer[plantTime.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> growTime[b] - growTime[a]);

        int currentPlantDay = 0;
        int answer = 0;
        for (int index : order) {
            currentPlantDay += plantTime[index];
            answer = Math.max(answer, currentPlantDay + growTime[index]);
        }

        return answer;
    }
}
