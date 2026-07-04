
package com.patternwisejavasolutions.heapPriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.LinkedList;

public class TaskScheduler {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given tasks represented by letters and a cooldown n, return the least number of time units
     * needed to finish all tasks. The same task letter must wait n units before running again.
     *
     * Sample Input: tasks = ['A','A','A','B','B','B'], n = 2
     * Sample Output: 8
     * One valid schedule: A B idle A B idle A B
     *
     * SCHOOL-LEVEL INTUITION:
     * The task with the highest remaining count is usually the most urgent, because it needs the
     * most separated slots. A heap helps pick that task quickly.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Simulate time. At each time unit, scan every task type and choose the available one with the
     * largest remaining count. If none is available, count an idle slot.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Count task frequencies.
     * 2. Track the next time each task is allowed to run.
     * 3. While tasks remain, scan all task types for the best available task.
     * 4. Run it or idle, then advance time.
     *
     * BRUTE FORCE DRY RUN
     * A:3, B:3, n=2
     * time 0 run A, time 1 run B, time 2 idle
     * time 3 run A, time 4 run B, time 5 idle
     * time 6 run A, time 7 run B -> total 8
     *
     * Time: O(time * uniqueTasks), Space: O(uniqueTasks)
     */
    public int bruteForce(char[] tasks, int n) {
        Map<Character, Integer> remaining = new HashMap<>();
        Map<Character, Integer> nextAllowed = new HashMap<>();

        for (char task : tasks) {
            remaining.put(task, remaining.getOrDefault(task, 0) + 1);
            nextAllowed.putIfAbsent(task, 0);
        }

        int time = 0;
        int tasksLeft = tasks.length;

        while (tasksLeft > 0) {
            Character bestTask = null;
            int bestCount = 0;

            for (char task : remaining.keySet()) {
                int count = remaining.get(task);
                if (count > bestCount && nextAllowed.get(task) <= time) {
                    bestTask = task;
                    bestCount = count;
                }
            }

            if (bestTask != null) {
                remaining.put(bestTask, remaining.get(bestTask) - 1);
                nextAllowed.put(bestTask, time + n + 1);
                tasksLeft--;
            }

            time++;
        }

        return time;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * Scanning every task each time is slow. Use a max heap for available task counts and a queue
     * for tasks cooling down. Each time unit, run the most frequent available task. When cooldown
     * finishes, put it back into the heap.
     *
     * Pattern used: Max heap + cooldown queue.
     *
     * Other useful approach: A math formula using max frequency gives O(1) after counting, but the
     * heap simulation is easier to understand because it shows the schedule being built.
     *
     * OPTIMIZED ALGORITHM
     * 1. Count tasks and add counts to a max heap.
     * 2. For each time unit, release cooled tasks back to the heap.
     * 3. If heap has a task, run the largest count and place leftover count in cooldown.
     * 4. Stop when both heap and cooldown queue are empty.
     *
     * OPTIMIZED DRY RUN
     * counts [3,3], n=2
     * time 1 run A left 2, cool until 4
     * time 2 run B left 2, cool until 5
     * time 3 idle, time 4 release/run A, and so on -> total 8
     *
     * Time: O(T log U), Space: O(U), where U is unique task types.
     */
    public int optimized(char[] tasks, int n) {
        Map<Character, Integer> frequency = new HashMap<>();
        for (char task : tasks) {
            frequency.put(task, frequency.getOrDefault(task, 0) + 1);
        }

        /*
         * Larger remaining count comes first because frequent tasks need more separated slots.
         * The cooldown queue keeps temporarily illegal tasks out of this available-task heap.
         */
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> {
            return Integer.compare(b, a);
        });
        maxHeap.addAll(frequency.values());

        // Each queue entry is [remaining count, time when it may run again].
        Queue<int[]> cooldown = new LinkedList<>();
        int time = 0;

        while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {
            time++;

            if (!cooldown.isEmpty() && cooldown.peek()[1] == time) {
                // A task's waiting period is over, so it can compete again.
                maxHeap.offer(cooldown.poll()[0]);
            }

            if (!maxHeap.isEmpty()) {
                // Run the most frequent currently available task for this time unit.
                int remainingCount = maxHeap.poll() - 1;

                if (remainingCount > 0) {
                    // It still has copies left, but must wait n full units before returning.
                    cooldown.offer(new int[] { remainingCount, time + n + 1 });
                }
            }
        }

        return time;
    }
}
