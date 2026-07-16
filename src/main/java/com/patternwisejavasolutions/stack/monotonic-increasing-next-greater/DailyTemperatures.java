package com.patternwisejavasolutions.stack.monotonicincreasingnextgreater;

import java.util.Stack;

public class DailyTemperatures {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * For each day, tell how many days we must wait for a warmer temperature.
     * If there is no warmer future day, answer is 0.
     *
     * Sample Input:
     * [73, 74, 75, 71, 69, 72, 76, 73]
     *
     * Sample Output:
     * [1, 1, 4, 2, 1, 1, 0, 0]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For each day, we are looking to the right.
     * We need the first future day with a higher temperature.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For every day, scan future days one by one.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * day 0 temperature 73
     * day 1 temperature 74 is warmer
     * wait = 1 day
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each day, scan future days.
     * 2. Stop at the first warmer temperature.
     * 3. Store future - current as waiting days.
     * 4. If no warmer day exists, answer remains 0.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1), not counting output
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int[] bruteForce(int[] temperatures) {
        int[] answer = new int[temperatures.length];

        for (int day = 0; day < temperatures.length; day++) {
            for (int future = day + 1; future < temperatures.length; future++) {
                if (temperatures[future] > temperatures[day]) {
                    answer[day] = future - day;
                    break;
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Brute force keeps looking right from every day. Instead, keep days that
     * are still waiting for a warmer temperature.
     * The stack stores indices, not temperatures, because answer needs distance.
     *
     * When current temperature is warmer than the day on top of stack,
     * current day solves that earlier day.
     *
     * The stack is monotonic decreasing by temperature: colder unresolved days
     * sit on top of warmer unresolved days. A warm day may pop several colder
     * days because it is the first warmer day for all of them.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * temps = [73, 74, 75, 71, 69, 72]
     *
     * day 0, 73 waits: stack [0]
     * day 1, 74 pops day 0, answer[0] = 1.
     * day 2, 75 pops day 1, answer[1] = 1.
     * day 3, 71 waits; day 4, 69 waits above it.
     * day 5, 72 pops day 4 and day 3 because 72 is warmer than both.
     * answer[4] = 1 and answer[3] = 2.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Stack stores indices of days waiting for warmer temperature.
     * 2. For each day, compare current temperature with stack top day.
     * 3. Pop and answer all colder previous days.
     * 4. Push current day because it may need a warmer future day.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Because temperatures are bounded, another solution can store the next seen
     * day for each temperature value and choose the closest warmer day. The stack
     * version works without relying on that temperature range.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int[] optimized(int[] temperatures) {
        int[] answer = new int[temperatures.length];
        Stack<Integer> waitingDays = new Stack<>();

        for (int today = 0; today < temperatures.length; today++) {
            while (
                !waitingDays.isEmpty()
                    && temperatures[today] > temperatures[waitingDays.peek()]
            ) {
                // Today is the first warmer day for the colder day on top.
                int previousDay = waitingDays.pop();
                answer[previousDay] = today - previousDay;
            }

            // Today now waits for a warmer future temperature.
            waitingDays.push(today);
        }

        return answer;
    }
}
