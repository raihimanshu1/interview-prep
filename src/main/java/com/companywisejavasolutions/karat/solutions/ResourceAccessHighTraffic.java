

package com.companywisejavasolutions.karat.solutions;
import java.util.*;

public class ResourceAccessHighTraffic {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given web resource access logs, find resources that receive at least a threshold number of accesses within any five-minute window.
     *
     * INPUT
     * logs[i] = [timeInSeconds, userId, resource], threshold is the required count.
     *
     * OUTPUT
     * A sorted list of resources that satisfy the threshold.
     *
     * EXAMPLE
     * logs = [["0","u1","/home"], ["60","u2","/home"], ["120","u3","/home"],
     *         ["400","u4","/home"], ["10","u5","/cart"], ["500","u6","/cart"]]
     * threshold = 3
     * Output: [/home]
     * 
     * /home has 3 hits inside 0..300 seconds. /cart does not.
     *
     * WHAT IT MEANS
     * Group timestamps by resource, sort each resource timeline, and check five-minute windows.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of every resource having its own clock. We look for a five-minute span
     * where enough accesses land close together.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * We are looking for bursts of traffic, not total popularity.
     * A resource with 100 visits spread across the whole day might not qualify,
     * while a resource with 5 visits in a few minutes might qualify.
     *
     * Because the rule is per resource, each URL/resource gets its own timeline.
     * Once a resource's timestamps are sorted, the question becomes:
     * "Starting at this access, how many accesses happen in the next 300 seconds?"
     */

    /*
     * NORMAL EXAMPLES
     *
     * Example 1
     * logs = [["0","u1","/home"], ["60","u2","/home"], ["120","u3","/home"]]
     * threshold = 3
     * Output: [/home]
     * Reason: three /home hits happen in the 0..300 second window.
     *
     * Example 2
     * logs = [["10","u1","/cart"], ["500","u2","/cart"], ["900","u3","/cart"]]
     * threshold = 3
     * Output: []
     * Reason: /cart has three total hits, but never three inside five minutes.
     *
     * Example 3
     * logs = [["0","u1","/a"], ["100","u2","/a"], ["200","u3","/a"],
     *         ["0","u4","/b"], ["400","u5","/b"], ["450","u6","/b"]]
     * threshold = 3
     * Output: [/a]
     * Reason: /a qualifies; /b does not have three hits in any 300-second window.
     */

    /*
     * EDGE CASES
     *
     * 1. Exactly 300 seconds apart counts.
     *    Hits at 0, 100, and 300 qualify for threshold 3.
     *
     * 2. Logs may be unsorted, so sort each resource timeline.
     *
     * 3. threshold <= 1 means every resource with at least one access qualifies.
     *
     * 4. Different users do not matter for counting here; the resource is the key.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Five minutes equals 300 seconds.
     * 2. The resource name is logs[i][2].
     * 3. The timestamp is logs[i][0] and must be parsed as an integer.
     * 4. The output should be sorted so results are stable.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Group all access times by resource.
     * For each resource, sort the times and try each access as the start of a
     * five-minute window. Count how many accesses land in that window.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    Each resource has its own traffic timeline; different resources should not be mixed.
     *
     * 2. What data structure does that naturally suggest?
     *    Use resource -> list of timestamps because high traffic is checked per resource.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: pick every access as a window start and count accesses within five minutes.
     *
     * 4. What repeated work should I remove?
     *    Optimized: sort each timeline and slide a window for the five-minute range.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: pick every access as a window start and count accesses within five minutes.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Build a map: resource -> list of access times.
     * 2. Sort each resource's access times.
     * 3. For each time, treat it as the window start.
     * 4. Count every access for that resource where 0 <= current - start <= 300.
     * 5. If the count reaches threshold, add the resource and stop checking it.
     * 6. Sort the final list of qualifying resources.
     * 
     * Time Complexity: O(n log n + k^2) across grouped timelines.
     * Space Complexity: O(n) for grouped times and the answer.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the multi-record example above.
     * /home times are [0, 60, 120, 400].
     * Window 0..300 contains 0, 60, and 120, so /home qualifies.
     * /cart times are [10, 500], so it never reaches threshold 3.
     * Final answer: [/home]
     */
    public List<String> bruteForce(String[][] logs, int threshold) {

        // Group by resource because the high-traffic rule is checked per resource.
        Map<String, List<Integer>> timesByResource = new HashMap<>();

        for (String[] log : logs) {
            int seconds = Integer.parseInt(log[0]);
            String resource = log[2];
            // Store only the timestamp; user id is not needed for this count.
            timesByResource.putIfAbsent(resource, new ArrayList<>());
            timesByResource.get(resource).add(seconds);
        }

        List<String> result = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : timesByResource.entrySet()) {
            List<Integer> times = entry.getValue();

            // Sort timestamps so each start time opens a forward five-minute window.
            Collections.sort(times);

            // Try every access as the beginning of a possible traffic spike.
            for (int start = 0; start < times.size(); start++) {
                int count = 0;

                // Brute force counts a fresh five-minute window for this start time.
                for (int current = 0; current < times.size(); current++) {
                    int difference = times.get(current) - times.get(start);

                    // 300 seconds is five minutes. The boundary is inclusive.
                    if (difference >= 0 && difference <= 300) {
                        count++;
                    }
                }

                if (count >= threshold) {
                    // One qualifying window proves this resource has high traffic.
                    result.add(entry.getKey());
                    break;
                }
            }
        }

        // Return resources in sorted order.
        Collections.sort(result);
        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: sort each timeline and slide a window for the five-minute range.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find resources with many hits in five minutes.
     * 2. Remove repeated work: sort times and maintain a sliding left boundary.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the multi-record example above.
     * /home times are [0, 60, 120, 400].
     * Window 0..300 contains 0, 60, and 120, so /home qualifies.
     * /cart times are [10, 500], so it never reaches threshold 3.
     * Final answer: [/home]
     */
    public List<String> optimized(String[][] logs, int threshold) {
        Map<String, List<Integer>> timesByResource = new HashMap<>();
        for (String[] log : logs) {
            int seconds = Integer.parseInt(log[0]);
            String resource = log[2];
            timesByResource.putIfAbsent(resource, new ArrayList<>());
            timesByResource.get(resource).add(seconds);
        }
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : timesByResource.entrySet()) {
            List<Integer> times = entry.getValue();
            Collections.sort(times);

            int left = 0;
            for (int right = 0; right < times.size(); right++) {
                while (times.get(right) - times.get(left) > 300) {
                    left++;
                }
        if (right - left + 1 >= threshold) {
                    result.add(entry.getKey());
                    break;
                }
            }
        }
            Collections.sort(result);
        return result;
    }

    public static void main(String[] args) {
        ResourceAccessHighTraffic solution = new ResourceAccessHighTraffic();

        String[][][] sampleLogs = {
                {
                        {"0", "u1", "/home"},
                        {"60", "u2", "/home"},
                        {"120", "u3", "/home"},
                        {"400", "u4", "/home"},
                        {"10", "u5", "/cart"},
                        {"500", "u6", "/cart"}
                },
                {
                        {"10", "u1", "/cart"},
                        {"500", "u2", "/cart"},
                        {"900", "u3", "/cart"}
                },
                {
                        {"0", "u1", "/a"},
                        {"100", "u2", "/a"},
                        {"300", "u3", "/a"},
                        {"0", "u4", "/b"},
                        {"400", "u5", "/b"},
                        {"450", "u6", "/b"}
                }
        };

        int[] thresholds = {3, 3, 3};

        for (int i = 0; i < sampleLogs.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + solution.bruteForce(sampleLogs[i], thresholds[i]));
            System.out.println("optimized:  " + solution.optimized(sampleLogs[i], thresholds[i]));
            System.out.println();
        }
    }
}
