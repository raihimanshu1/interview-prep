

package com.companywisejavasolutions.karat.solutions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BadgeAccessOneHourWindow {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given badge access records for employees, find every employee who used their badge three or more times within any one-hour period. Return the employee and the badge times in that first one-hour window.
     *
     * INPUT
     * names[i] and times[i] describe one badge swipe. Times may be HH:MM or HHMM/HMM numeric strings.
     *
     * OUTPUT
     * A map from employee name to the first list of times proving the one-hour alert.
     *
     * EXAMPLE
     * names = ["daniel", "daniel", "daniel", "luis", "luis", "luis", "luis"]
     * times = ["800", "830", "855", "900", "940", "1030", "1200"]
     * Output: {daniel=[08:00, 08:30, 08:55]}
     * 
     * Daniel has 3 swipes within 55 minutes. Luis has swipes, but no 3-swipe one-hour window.
     *
     * WHAT IT MEANS
     * Group times by employee, sort them, and look for any three times where max - min <= 60 minutes.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Imagine writing each employee's badge times on a separate paper.
     * For daniel, the paper has: 08:00, 08:30, 08:55.
     *
     * Now ask a simple question:
     * "Starting from this time, how many swipes happen before one hour passes?"
     *
     * If the answer is 3 or more, that employee should be in the result.
     *
     * The pattern is grouping + sorting + sliding window.
     * Grouping separates each employee.
     * Sorting puts their times in real timeline order.
     * Sliding window finds 3 close swipes without checking the same range again
     * and again.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * This problem is about finding evidence, not just saying "yes" or "no".
     * For each employee who breaks the rule, we return the actual badge times
     * from the first one-hour window that proves it.
     *
     * The logs can arrive in any order, so we cannot trust the input position.
     * We first group times by employee, then sort that employee's times.
     *
     * Once the times are sorted, the rule becomes easy:
     * if a group of at least 3 times has last - first <= 60, that group is an
     * alert window.
     */

    /*
     * NORMAL EXAMPLES
     *
     * Example 1
     * names = ["daniel", "daniel", "daniel"]
     * times = ["800", "830", "855"]
     * Output: {daniel=[08:00, 08:30, 08:55]}
     * Reason: all three swipes are within 55 minutes.
     *
     * Example 2
     * names = ["luis", "luis", "luis", "luis"]
     * times = ["900", "940", "1030", "1200"]
     * Output: {}
     * Reason: no one-hour window has three luis swipes.
     *
     * Example 3
     * names = ["amy", "bob", "amy", "amy", "bob", "bob"]
     * times = ["0900", "1000", "0930", "0955", "1100", "1120"]
     * Output: {amy=[09:00, 09:30, 09:55]}
     * Reason: amy qualifies; bob has only two swipes in any one-hour window.
     */

    /*
     * EDGE CASES
     *
     * 1. A time exactly 60 minutes after the start still belongs to the window.
     *    08:00, 08:30, 09:00 is valid.
     *
     * 2. Times may be written as "800", "0800", or "08:00".
     *    The helper normalizes all formats into minutes.
     *
     * 3. Employees with fewer than three records are ignored naturally.
     *
     * 4. If more than three times are in the first window, return all of them
     *    from that window, not only the first three.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Group by employee before doing time math.
     * 2. Sort each employee's times because input order is not meaningful.
     * 3. A one-hour window is inclusive: difference <= 60.
     * 4. The return value is a map from name to the proving list of formatted times.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * For every employee, choose each badge time as a possible window start.
     * Then scan the employee's full timeline and collect all times inside the
     * next 60 minutes. If that collected window has at least 3 times, store it
     * as the answer for that employee.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    For each employee, only that employee's badge times matter; mixing employees would create a false alert.
     *
     * 2. What data structure does that naturally suggest?
     *    Use employee -> list of minutes because a sorted timeline makes one-hour windows visible.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: pick every swipe as the possible start and scan all other swipes to collect the one-hour group.
     *
     * 4. What repeated work should I remove?
     *    Optimized: sort and move two pointers so the end of the window never moves backward.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: pick every swipe as the possible start and scan all other swipes to collect the one-hour group.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Convert every input time to minutes and group times by employee.
     * 2. Sort each employee's list of times.
     * 3. For each possible start time, create a fresh window list.
     * 4. Scan all times for that employee and add the ones within start..start+60.
     * 5. If the window has 3 or more times, format it and store it in the answer.
     * 6. Stop checking that employee after the first qualifying window.
     * 
     * Time Complexity: O(n log n + k^2) across employee timelines.
     * Space Complexity: O(n) for grouped times, windows, and the answer.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the multi-employee example above.
     * Daniel times 08:00, 08:30, 08:55 fit inside 55 minutes.
     * Luis has multiple swipes, but no 3 swipes inside one hour.
     * Final answer: {daniel=[08:00, 08:30, 08:55]}
     */
    public Map<String, List<String>> bruteForce(String[] names, String[] times) {
        // Group first: each employee's alert must be proven only from that employee's swipes.
        Map<String, List<Integer>> employeeToTimes = groupTimes(names, times);

        // TreeMap keeps names sorted in the returned map, which makes output predictable.
        Map<String, List<String>> answer = new TreeMap<>();

        for (Map.Entry<String, List<Integer>> entry : employeeToTimes.entrySet()) {
            List<Integer> badgeTimes = entry.getValue();

            // Sorting turns messy log order into a real timeline.
            Collections.sort(badgeTimes);

            // Try each time as the first swipe in a possible one-hour alert window.
            for (int start = 0; start < badgeTimes.size(); start++) {
                List<Integer> window = new ArrayList<>();

                // Brute force scans all times again for this start.
                // The optimized version avoids this repeated work, but this is easiest to reason about.
                for (int current = 0; current < badgeTimes.size(); current++) {
                    int difference = badgeTimes.get(current) - badgeTimes.get(start);

                    // Keep only times from this start through the next 60 minutes.
                    if (difference >= 0 && difference <= 60) {
                        window.add(badgeTimes.get(current));
                    }
                }

                if (window.size() >= 3) {
                    // This is the first qualifying window for this employee because starts move forward.
                    answer.put(entry.getKey(), toTimeStrings(window));
                    break;
                }
            }
        }
        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: sort and move two pointers so the end of the window never moves backward.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find 3 badge swipes within one hour per employee.
     * 2. Remove repeated work: sort each timeline and slide the end pointer forward.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the multi-employee example above.
     * Daniel times 08:00, 08:30, 08:55 fit inside 55 minutes.
     * Luis has multiple swipes, but no 3 swipes inside one hour.
     * Final answer: {daniel=[08:00, 08:30, 08:55]}
     */
    public Map<String, List<String>> optimized(String[] names, String[] times) {
        Map<String, List<Integer>> employeeToTimes = groupTimes(names, times);
        Map<String, List<String>> answer = new TreeMap<>();
        for (Map.Entry<String, List<Integer>> entry : employeeToTimes.entrySet()) {
            List<Integer> badgeTimes = entry.getValue();
            Collections.sort(badgeTimes);

            int end = 0;
            for (int start = 0; start < badgeTimes.size(); start++) {
                while (end < badgeTimes.size() && badgeTimes.get(end) - badgeTimes.get(start) <= 60) {
                    end++;
                }
                if (end - start >= 3) {
                    answer.put(entry.getKey(), toTimeStrings(badgeTimes.subList(start, end)));
                    break;
                }
            }
        }
        return answer;
    }

    private Map<String, List<Integer>> groupTimes(String[] names, String[] times) {
        // Store each employee's swipes separately because the alert rule is per
        // person. A TreeMap keeps the helper's output deterministic for examples,
        // tests, and interview discussion.
        Map<String, List<Integer>> employeeToTimes = new TreeMap<>();

        // Each index represents one badge record: names[i] used their badge at
        // times[i]. Walk both arrays together so every time is attached to the
        // correct employee.
        for (int i = 0; i < names.length; i++) {
            // Pull out the employee name so the map update reads like the problem:
            // "this person used their badge at this time."
            String name = names[i];

            // Pull out the matching raw time from the parallel times array.
            String time = times[i];

            // Convert the raw time into minutes.
            // Minutes are easier to sort and subtract than strings like "08:30".
            int minutes = toMinutes(time);

            // Create the employee's list the first time we see their name.
            employeeToTimes.putIfAbsent(name, new ArrayList<>());

            // Append this badge swipe to that employee's own timeline.
            employeeToTimes.get(name).add(minutes);
        }

        // Return the grouped timelines so the caller can sort each employee and
        // search for a one-hour window independently.
        return employeeToTimes;
    }

    private int toMinutes(String time) {
        // Some inputs arrive as compact badge times such as "800", "0800", or
        // "930". If there is no colon, integer math separates hours from minutes:
        // everything before the last two digits is the hour, and the last two
        // digits are the minute.
        if (!time.contains(":")) {
            int numericTime = Integer.parseInt(time);
            return (numericTime / 100) * 60 + numericTime % 100;
        }

        // Colon-formatted inputs are already split visually into hour and minute.
        // Convert both pieces to integers, then normalize to total minutes so all
        // formats can be compared with the same one-hour arithmetic.
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private List<String> toTimeStrings(List<Integer> minutesList) {
        // The solving logic uses total minutes because subtraction answers
        // "within one hour?" directly. When we need to display those minutes to
        // people, this helper converts the provided list back to HH:MM strings.
        List<String> result = new ArrayList<>();

        // Preserve the order of the already-sorted window while formatting each
        // minute count into a readable badge time.
        for (int totalMinutes : minutesList) {
            // Integer division gives the hour portion.
            int hours = totalMinutes / 60;

            // The remainder gives the minute portion within that hour.
            int minutes = totalMinutes % 60;

            // Pad with leading zeroes so 8:5 becomes the standard "08:05" form.
            result.add(String.format("%02d:%02d", hours, minutes));
        }

        // Return the formatted version of exactly the minute values we were given.
        return result;
    }

    public static void main(String[] args) {
        BadgeAccessOneHourWindow solution = new BadgeAccessOneHourWindow();

        String[][] sampleNames = {
                {"daniel", "daniel", "daniel", "luis", "luis", "luis", "luis"},
                {"amy", "bob", "amy", "amy", "bob", "bob"},
                {"mike", "mike", "mike", "sara", "sara", "sara"}
        };

        String[][] sampleTimes = {
                {"800", "830", "855", "900", "940", "1030", "1200"},
                {"0900", "1000", "0930", "0955", "1100", "1120"},
                {"08:00", "08:30", "09:00", "10:00", "11:10", "12:20"}
        };

        for (int i = 0; i < sampleNames.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + solution.bruteForce(sampleNames[i], sampleTimes[i]));
            System.out.println("optimized:  " + solution.optimized(sampleNames[i], sampleTimes[i]));
            System.out.println();
        }
    }
}
