

package com.companywisejavasolutions.karat.solutions;
import java.util.*;

public class UnsortedWebResourceLogs {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given unsorted web access logs, return the earliest and latest access time for each user.
     *
     * INPUT
     * logs[i] = [time, user, resource].
     *
     * OUTPUT
     * A map from user to [earliestTime, latestTime].
     *
     * EXAMPLE
     * logs = [["58523","user_1","resource_1"], ["62314","user_1","resource_2"],
     *         ["54000","user_2","resource_1"], ["70000","user_2","resource_3"]]
     * Output: user_1 -> [58523, 62314], user_2 -> [54000, 70000]
     * 
     * Each user gets their own earliest and latest access time.
     *
     * WHAT IT MEANS
     * For each user, keep the minimum and maximum timestamp seen.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of messy access logs as timestamp cards. For each user, we only need
     * the earliest and latest card.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * The logs are unsorted, so the first row for a user is not necessarily
     * that user's first visit. We must inspect the actual timestamp values.
     *
     * For each user, we want exactly two numbers:
     * the smallest timestamp seen for that user and the largest timestamp seen
     * for that user.
     *
     * The resource column is useful context in the original log, but it does
     * not affect this specific question.
     */

    /*
     * NORMAL EXAMPLES
     *
     * Example 1
     * logs = [["58523","user_1","resource_1"], ["62314","user_1","resource_2"]]
     * Output: user_1 -> [58523, 62314]
     * Reason: user_1 has two visits; smaller is first, larger is last.
     *
     * Example 2
     * logs = [["70000","user_2","resource_3"], ["54000","user_2","resource_1"]]
     * Output: user_2 -> [54000, 70000]
     * Reason: input order is reversed, so compare timestamp values.
     *
     * Example 3
     * logs = [["10","amy","/a"], ["20","bob","/b"], ["15","amy","/c"]]
     * Output: amy -> [10, 15], bob -> [20, 20]
     * Reason: a one-log user has the same earliest and latest time.
     */

    /*
     * EDGE CASES
     *
     * 1. A user with one access returns [time, time].
     *
     * 2. Multiple users can be interleaved in any order.
     *
     * 3. Repeated timestamps are fine; min and max may be equal.
     *
     * 4. The answer uses sorted user names because TreeMap is used.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. The timestamp is logs[i][0].
     * 2. The user is logs[i][1].
     * 3. The resource is not needed for earliest/latest per user.
     * 4. Since input is unsorted, position in the array cannot decide first or last.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * The brute force method first finds every unique user.
     * Then, for each user, it scans all logs and updates that user's smallest
     * and largest timestamp. This is simple and very clear, even though it
     * repeats the scan once per user.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    Unsorted logs must be normalized before timeline questions become reliable.
     *
     * 2. What data structure does that naturally suggest?
     *    Use grouping maps because users/resources/sessions each need their own bucket.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: compare every log with every other log to form sessions or counts.
     *
     * 4. What repeated work should I remove?
     *    Optimized: group first and sort each group once before analyzing.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: compare every log with every other log to form sessions or counts.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Collect every unique user from the logs.
     * 2. For each user, initialize first to a very large number and last to a very small number.
     * 3. Scan all logs.
     * 4. When the row belongs to this user, parse the time.
     * 5. Update first with the smaller time and last with the larger time.
     * 6. Store [first, last] for that user.
     * 
     * Time Complexity: O(u * n), where u is unique users and n is logs.
     * Space Complexity: O(u) for users and the result.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the multi-user log example above.
     * user_1 earliest is 58523 and latest is 62314.
     * user_2 earliest is 54000 and latest is 70000.
     * Final answer stores one range per user.
     */
    public Map<String, int[]> bruteForce(String[][] logs) {

        // First discover every user we need to answer for.
        Set<String> users = new HashSet<>();
        for (String[] log : logs) {
            users.add(log[1]);
        }

        // TreeMap gives deterministic alphabetical user order in the output.
        Map<String, int[]> result = new TreeMap<>();

        for (String user : users) {
            // Start with extremes so the first matching log will replace both values.
            int first = Integer.MAX_VALUE;
            int last = Integer.MIN_VALUE;

            // Brute force rescans all logs for this one user.
            for (String[] log : logs) {
                if (log[1].equals(user)) {
                    int time = Integer.parseInt(log[0]);

                    // Keep the smallest timestamp as the earliest access.
                    first = Math.min(first, time);

                    // Keep the largest timestamp as the latest access.
                    last = Math.max(last, time);
                }
            }

            // Every discovered user had at least one log, so first and last are real values now.
            result.put(user, new int[] {first, last});
        }

        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: group first and sort each group once before analyzing.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find first and last access time per user.
     * 2. Remove repeated work: update each user min/max in one pass.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the multi-user log example above.
     * user_1 earliest is 58523 and latest is 62314.
     * user_2 earliest is 54000 and latest is 70000.
     * Final answer stores one range per user.
     */
    public Map<String, int[]> optimized(String[][] logs) {
        Map<String, int[]> timesByUser = new TreeMap<>();
        for (String[] log : logs) {
            int time = Integer.parseInt(log[0]);
            String user = log[1];
            timesByUser.putIfAbsent(user, new int[] {time, time});
            timesByUser.get(user)[0] = Math.min(timesByUser.get(user)[0], time);
            timesByUser.get(user)[1] = Math.max(timesByUser.get(user)[1], time);
        }
        return timesByUser;
    }

    public static void main(String[] args) {
        UnsortedWebResourceLogs solution = new UnsortedWebResourceLogs();

        String[][][] samples = {
                {
                        {"58523", "user_1", "resource_1"},
                        {"62314", "user_1", "resource_2"},
                        {"54000", "user_2", "resource_1"},
                        {"70000", "user_2", "resource_3"}
                },
                {
                        {"70000", "user_2", "resource_3"},
                        {"54000", "user_2", "resource_1"}
                },
                {
                        {"10", "amy", "/a"},
                        {"20", "bob", "/b"},
                        {"15", "amy", "/c"}
                }
        };

        for (int i = 0; i < samples.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + formatRanges(solution.bruteForce(samples[i])));
            System.out.println("optimized:  " + formatRanges(solution.optimized(samples[i])));
            System.out.println();
        }
    }

    private static String formatRanges(Map<String, int[]> map) {
        Map<String, String> formatted = new TreeMap<>();
        for (Map.Entry<String, int[]> entry : map.entrySet()) {
            int[] range = entry.getValue();
            formatted.put(entry.getKey(), "[" + range[0] + ", " + range[1] + "]");
        }
        return formatted.toString();
    }
}
