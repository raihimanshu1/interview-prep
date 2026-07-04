
package com.companywisejavasolutions.wellsFargo.solutions;
public class MaximumNumberOfWeeks {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given milestone counts for projects, return the maximum weeks you can work without working on the same project in consecutive weeks.
     *
     * INPUT
     * milestones[i] is the number of tasks in project i.
     *
     * OUTPUT
     * Maximum number of valid work weeks.
     *
     * EXAMPLE
     * milestones = [1,2,3] -> 6.
     *
     * WHAT IT MEANS
     * The biggest project may need other projects as separators. If separators run out, you must stop.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1: [1,2,3] -> 6
     * Example 2: [5,2,1] -> 7
     * Example 3: [7] -> 1
     *
     * EDGE CASES
     * - Use long because sums can be large.
     * - One project alone can contribute only one valid week.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Try every possible next project recursively, skipping the project used in the previous week.
     *
     * Time Complexity: exponential, because it explores schedules. Space Complexity: O(total milestones) recursion depth.
     */

    /*
     * OPTIMIZED APPROACH
     *
     * Compare the largest project count with the sum of all other projects. If max <= rest + 1, all work fits. Otherwise answer is 2 * rest + 1.
     *
     * Time Complexity: O(n). Space Complexity: O(1).
     */
public long bruteForce(int[] milestones) {
        return search(milestones, -1);
    }

    private long search(int[] remaining, int previousProject) {
        long best = 0;

        for (int project = 0; project < remaining.length; project++) {
            if (project == previousProject || remaining[project] == 0) {
                continue;
            }

            remaining[project]--;
            best = Math.max(best, 1 + search(remaining, project));
            remaining[project]++;
        }

        return best;
    }

    public long optimized(int[] milestones) {
        long total = 0;
        long max = 0;

        for (int count : milestones) {
            total += count;
            max = Math.max(max, count);
        }

        long rest = total - max;

        if (max <= rest + 1) {
            return total;
        }

        return rest * 2 + 1;
    }

    public static void main(String[] args) {
        MaximumNumberOfWeeks solver = new MaximumNumberOfWeeks();
        System.out.println("Use bruteForce and optimized with the examples in MORE INPUTS TO PRACTICE.");
    }
}
