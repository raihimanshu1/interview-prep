

package com.companywisejavasolutions.karat.solutions;
import java.util.*;

public class BadgeRoomPresenceFrequentGroups {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given badge enter/exit logs for a room, find pairs of employees who were
     * present together for at least a required number of minutes.
     *
     * INPUT
     * logs[i] = [employeeName, "enter" or "exit", "HH:MM"].
     * minOverlapMinutes is the minimum total overlap needed for a pair.
     *
     * OUTPUT
     * A list of employee pairs formatted as "employeeA,employeeB".
     *
     * EXAMPLE
     * logs = [["A","enter","09:00"], ["B","enter","09:10"], ["C","enter","09:20"],
     *         ["A","exit","10:00"], ["B","exit","10:05"], ["C","exit","09:30"]]
     * minOverlapMinutes = 30
     * Output: [A,B]
     * 
     * A and B overlap for 50 minutes. C overlaps too briefly to qualify.
     *
     * WHAT IT MEANS
     * Convert enter/exit records into time intervals, then measure how long
     * each pair of employees had overlapping intervals.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of a room sign-in sheet. Each enter starts an interval and each exit
     * closes it. Two people are together only where their intervals overlap.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * The badge log is just a timeline written as separate events.
     *
     * "A enters at 09:00" does not tell us the whole story yet. It only opens a
     * time window for A. When we later see "A exits at 10:00", we can turn those
     * two records into the interval [09:00, 10:00].
     *
     * After every employee has intervals, the question becomes much easier:
     *
     * For each pair of people, how many minutes do their intervals overlap?
     *
     * Two intervals overlap only in the shared middle portion:
     *
     * A:       09:00 -------- 10:00
     * B:             09:10 -------- 10:05
     * Shared:        09:10 -- 10:00  = 50 minutes
     *
     * If a pair meets or beats minOverlapMinutes, we include that pair.
     */

    /*
     * EXAMPLES AND EDGE CASES
     *
     * Example 1 - One qualifying pair
     *
     * logs = {
     *     {"A", "enter", "09:00"},
     *     {"B", "enter", "09:10"},
     *     {"A", "exit", "10:00"},
     *     {"B", "exit", "10:05"}
     * }
     * minOverlapMinutes = 30
     *
     * A and B overlap from 09:10 to 10:00, which is 50 minutes.
     * Output: [A,B]
     *
     * Example 2 - Same room, not enough shared time
     *
     * logs = {
     *     {"A", "enter", "09:00"},
     *     {"A", "exit", "09:15"},
     *     {"B", "enter", "09:10"},
     *     {"B", "exit", "09:20"}
     * }
     * minOverlapMinutes = 10
     *
     * They overlap only from 09:10 to 09:15, which is 5 minutes.
     * Output: []
     *
     * Example 3 - Multiple visits add up
     *
     * logs = {
     *     {"A", "enter", "09:00"}, {"B", "enter", "09:05"},
     *     {"A", "exit", "09:20"},  {"B", "exit", "09:25"},
     *     {"A", "enter", "10:00"}, {"B", "enter", "10:10"},
     *     {"A", "exit", "10:30"},  {"B", "exit", "10:40"}
     * }
     * minOverlapMinutes = 35
     *
     * First overlap is 15 minutes. Second overlap is 20 minutes.
     * Total is 35, so the pair qualifies.
     * Output: [A,B]
     *
     * Edge case 1 - Empty log
     *
     * logs = {}
     * Output: []
     *
     * Edge case 2 - Person never exits
     *
     * If an employee has only an enter event, there is no closed interval to
     * compare, so that open visit is ignored by this solution.
     *
     * Edge case 3 - minOverlapMinutes is 0
     *
     * Every pair of employees with closed interval records qualifies because
     * every total overlap is at least 0.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    This is an event-log problem: badge events change who is inside the room at each time.
     *
     * 2. What data structure does that naturally suggest?
     *    Use a current-inside set plus group counters because co-presence is about the active set at a moment.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: build intervals for each person and compare intervals for overlaps.
     *
     * 4. What repeated work should I remove?
     *    Optimized: build sorted intervals once and compare those interval lists directly.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Convert clock time to minutes so subtraction is simple.
     * 2. An "enter" event starts an interval; an "exit" event closes it.
     * 3. Overlap between [aStart, aEnd] and [bStart, bEnd] is:
     *    min(aEnd, bEnd) - max(aStart, bStart), but never below 0.
     * 4. A pair can overlap across several visits, so we add all their overlaps.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * In brute force, we first turn raw logs into intervals per employee.
     * Then we compare every employee against every other employee.
     * For each pair, we walk through their intervals and total the shared time.
     * If the total is large enough, we write the pair into the answer.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: build intervals for each person and compare intervals for overlaps.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create a map: employee -> list of closed room intervals.
     * 2. Collect the employee names from that map.
     * 3. For every pair of employees:
     *    a. Compute their total overlapping minutes.
     *    b. If the total is at least minOverlapMinutes, add "A,B".
     * 4. Return every qualifying pair.
     * 
     * Time Complexity: O(pairs * intervals^2) after intervals are built.
     * Space Complexity: O(number of badge intervals) for employee interval lists.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Sample: logs = [["A","enter","09:00"], ["B","enter","09:10"], ["C","enter","09:20"],         ["A","exit","10:00"], ["B","exit","10:05"], ["C","exit","09:30"]] minOverlapMinutes = 30 Output: [A,B] A and B overlap for 50 minutes. C overlaps too briefly to qualify.
     * minOverlapMinutes = 30
     * Direct checking leads to: [A,B]
     */
    public List<String> bruteForce(String[][] logs, int minOverlapMinutes) {
        // First translate noisy badge events into clean time ranges per person.
        Map<String, List<Interval>> intervalsByPerson = buildIntervals(logs);
        List<String> people = new ArrayList<>(intervalsByPerson.keySet());
        List<String> result = new ArrayList<>();

        // Try every unique pair once. If we checked both A,B and B,A, we would
        // duplicate the same relationship.
        for (int i = 0; i < people.size(); i++) {
            for (int j = i + 1; j < people.size(); j++) {
                String first = people.get(i);
                String second = people.get(j);

                // Count every minute these two people were simultaneously in the room.
                int overlap = totalOverlap(intervalsByPerson.get(first), intervalsByPerson.get(second));
                if (overlap >= minOverlapMinutes) {
                    // The requested output shape is one string per qualifying pair.
                    result.add(first + "," + second);
                }
            }
        }
        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: build sorted intervals once and compare those interval lists directly.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build intervals once from the event log.
     * 2. Sort each person's intervals by start time.
     * 3. Compare interval lists directly for each pair.
     * 4. Return all qualifying pairs.
     * 
     * Time Complexity: Lower than brute force because repeated scanning is replaced with stored state.
     * Space Complexity: O(number of badge intervals) for interval storage and results.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: logs = [["A","enter","09:00"], ["B","enter","09:10"], ["C","enter","09:20"],         ["A","exit","10:00"], ["B","exit","10:05"], ["C","exit","09:30"]] minOverlapMinutes = 30 Output: [A,B] A and B overlap for 50 minutes. C overlaps too briefly to qualify.
     * minOverlapMinutes = 30
     * After applying build presence intervals and compare overlaps, the answer is: [A,B]
     */
    public List<String> optimized(String[][] logs, int minOverlapMinutes) {
        if (logs.length == 0) {
            return new ArrayList<>();
        }

        Map<String, List<Interval>> intervalsByPerson = buildIntervals(logs);
        List<String> people = new ArrayList<>(intervalsByPerson.keySet());
        List<String> result = new ArrayList<>();
        for (int i = 0; i < people.size(); i++) {
            for (int j = i + 1; j < people.size(); j++) {
                String first = people.get(i);
                String second = people.get(j);
                int overlap = totalOverlap(intervalsByPerson.get(first), intervalsByPerson.get(second));
                if (overlap >= minOverlapMinutes) {
                    result.add(first + "," + second);
                }
            }
        }
        return result;
    }

    private Map<String, List<Interval>> buildIntervals(String[][] logs) {
        // Track employees who are currently inside the room.
        // The value is their unmatched enter time, waiting for a future exit.
        Map<String, Integer> openEnter = new HashMap<>();

        // Store the closed visits we can safely compare later.
        // Each employee can have several intervals because they may enter/exit many times.
        Map<String, List<Interval>> intervals = new HashMap<>();

        // Read each badge event and turn matching enter/exit pairs into intervals.
        for (String[] log : logs) {
            // log[0] identifies whose room visit this event belongs to.
            String name = log[0];

            // log[1] tells us whether this event opens or closes a visit.
            String action = log[1];

            // Convert "HH:MM" to a number so interval length is simple subtraction.
            int time = toMinutes(log[2]);
            if ("enter".equals(action)) {
                // An enter starts a new open interval for this person.
                // We cannot compare it yet because we do not know the exit time.
                openEnter.put(name, time);
            } else if (openEnter.containsKey(name)) {
                // An exit only forms a usable interval if we previously saw an enter.
                int enterTime = openEnter.remove(name);

                // Pair the saved enter time with this exit time to close the visit.
                Interval closedVisit = new Interval(enterTime, time);

                // Create the employee's interval list the first time needed.
                intervals.putIfAbsent(name, new ArrayList<>());

                // Add this completed room visit to that employee's interval list.
                intervals.get(name).add(closedVisit);
            }
        }

        // Sort every person's visits by start time before overlap comparison.
        // The totalOverlap helper uses a two-pointer merge, and that only makes
        // sense when each person's timeline is in chronological order.
        for (List<Interval> personIntervals : intervals.values()) {
            personIntervals.sort(Comparator.comparingInt(interval -> interval.start));
        }

        // Return only complete, sorted intervals; unmatched enters are intentionally ignored.
        return intervals;
    }

    private int totalOverlap(List<Interval> first, List<Interval> second) {
        // If either person has no completed room visits, they cannot share time.
        if (first == null || second == null) {
            return 0;
        }

        // i points at the current interval for the first employee.
        int i = 0;

        // j points at the current interval for the second employee.
        int j = 0;

        // Accumulate shared minutes across all visits.
        int overlap = 0;

        // Walk both interval lists together, like merging two sorted timelines.
        while (i < first.size() && j < second.size()) {
            // Current visit window for the first employee.
            Interval a = first.get(i);

            // Current visit window for the second employee.
            Interval b = second.get(j);

            // The shared part starts at the later enter time and ends at the earlier exit time.
            // If the intervals do not overlap, the subtraction is negative, so clamp at 0.
            overlap += Math.max(0, Math.min(a.end, b.end) - Math.max(a.start, b.start));
            if (a.end < b.end) {
                // The first interval ends earlier, so it cannot overlap any more
                // of b or later intervals; move first's pointer forward.
                i++;
            } else {
                // Otherwise the second interval is the one that is finished first,
                // so advance second's pointer.
                j++;
            }
        }

        // This total is what we compare against minOverlapMinutes.
        return overlap;
    }

    private int toMinutes(String time) {
        // Split the clock string into hour and minute pieces.
        String[] parts = time.split(":");

        // Convert HH hours into minutes, then add the MM part.
        // This is why overlap can be computed with plain subtraction later.
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private static class Interval {
        int start;
        int end;

        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public static void main(String[] args) {
        BadgeRoomPresenceFrequentGroups solution = new BadgeRoomPresenceFrequentGroups();

        String[][][] sampleLogs = {
                {
                        {"A", "enter", "09:00"},
                        {"B", "enter", "09:10"},
                        {"C", "enter", "09:20"},
                        {"A", "exit", "10:00"},
                        {"B", "exit", "10:05"},
                        {"C", "exit", "09:30"}
                },
                {
                        {"A", "enter", "09:00"},
                        {"A", "exit", "09:15"},
                        {"B", "enter", "09:10"},
                        {"B", "exit", "09:20"}
                },
                {
                        {"A", "enter", "09:00"},
                        {"B", "enter", "09:05"},
                        {"A", "exit", "09:20"},
                        {"B", "exit", "09:25"},
                        {"A", "enter", "10:00"},
                        {"B", "enter", "10:10"},
                        {"A", "exit", "10:30"},
                        {"B", "exit", "10:40"}
                }
        };

        int[] minimumOverlaps = {30, 10, 35};

        for (int i = 0; i < sampleLogs.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + solution.bruteForce(sampleLogs[i], minimumOverlaps[i]));
            System.out.println("optimized:  " + solution.optimized(sampleLogs[i], minimumOverlaps[i]));
            System.out.println();
        }
    }
}
