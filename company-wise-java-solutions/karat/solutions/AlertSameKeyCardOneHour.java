package karat.solutions;

import java.util.*;

public class AlertSameKeyCardOneHour {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given key-card usage names and times, return names that used their key card three or more times in any one-hour period.
     *
     * INPUT
     * keyName and keyTime arrays.
     *
     * OUTPUT
     * Sorted list of alerted names.
     *
     * EXAMPLE
     * keyName = ["daniel", "daniel", "daniel", "luis", "luis", "luis"]
     * keyTime = ["10:00", "10:40", "10:50", "09:00", "10:10", "11:30"]
     * Output: [daniel]
     * 
     * Daniel has 3 uses in 50 minutes. Luis has 3 uses total, but not inside one hour.
     *
     * WHAT IT MEANS
     * Same sliding-window alert as badge access, but output only names.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of writing each employee badge time on that employee's own timeline.
     * The question is whether any timeline has three marks packed inside a
     * sixty-minute span.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * We are not counting how many total times an employee used a key card.
     * We are checking whether their usage became unusually dense.
     *
     * Three swipes at 10:00, 10:40, and 10:50 should alert because the first
     * and last swipe are only 50 minutes apart.
     *
     * Three swipes at 09:00, 10:10, and 11:30 should not alert because no
     * one-hour slice contains all three.
     *
     * The important interview idea is:
     * separate each employee first, then solve a timeline problem for one
     * employee at a time.
     */

    /*
     * NORMAL EXAMPLES
     *
     * Example 1
     * keyName = ["daniel", "daniel", "daniel"]
     * keyTime = ["10:00", "10:40", "10:50"]
     * Output: [daniel]
     * Reason: all three uses fit inside 10:00..11:00.
     *
     * Example 2
     * keyName = ["luis", "luis", "luis"]
     * keyTime = ["09:00", "10:10", "11:30"]
     * Output: []
     * Reason: luis has three total uses, but not within one hour.
     *
     * Example 3
     * keyName = ["amy", "bob", "amy", "bob", "amy", "bob"]
     * keyTime = ["08:00", "08:00", "08:30", "09:30", "08:59", "10:20"]
     * Output: [amy]
     * Reason: amy has 08:00, 08:30, 08:59. Bob's times are spread out.
     */

    /*
     * EDGE CASES
     *
     * 1. Exactly 60 minutes apart counts.
     *    10:00, 10:30, 11:00 is an alert.
     *
     * 2. Input order may not be sorted.
     *    We sort each person's times before checking windows.
     *
     * 3. A person with fewer than three swipes can never alert.
     *
     * 4. Multiple people can alert.
     *    The final result must be sorted alphabetically.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Convert HH:MM into minutes so time subtraction is easy.
     * 2. Never compare one employee's swipe with another employee's swipe.
     * 3. "Within one hour" means lastTime - firstTime <= 60.
     * 4. Once an employee qualifies, we can stop checking that employee.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * First, group all swipe times by employee name.
     * Then, for each employee, sort their times from earliest to latest.
     * Finally, use the brute force idea:
     * choose a start swipe and count every swipe that lands in the next 60 minutes.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    For each employee, only that employee's badge times matter; different employees should never be mixed.
     *
     * 2. What data structure does that naturally suggest?
     *    Use employee -> list of minutes because the one-hour rule is checked inside one person's timeline.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: pick every swipe as a start time and count later swipes within 60 minutes.
     *
     * 4. What repeated work should I remove?
     *    Optimized: sort each timeline and slide a window instead of restarting the count from scratch.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: pick every swipe as a start time and count later swipes within 60 minutes.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Build a map: employee name -> all swipe times in minutes.
     * 2. For each employee, sort their times.
     * 3. Treat each time as a possible beginning of a one-hour window.
     * 4. Count every time for that employee where 0 <= current - start <= 60.
     * 5. If the count reaches 3, add the employee and move to the next employee.
     * 6. Sort the alerted names before returning.
     * 
     * Time Complexity: O(n log n + k^2) across grouped timelines, where the
     * repeated counting is the brute force part.
     * Space Complexity: O(n) for grouped times and the answer.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the multi-user example above.
     * Daniel has 10:00, 10:40, 10:50 inside one hour.
     * Luis has 09:00, 10:10, 11:30, but no 3-use one-hour group.
     * Final answer: [daniel]
     */
    public List<String> bruteForce(String[] keyName, String[] keyTime) {

        // Step 1: Put each employee's swipes on their own timeline.
        // This prevents false alerts caused by mixing different employees.
        Map<String, List<Integer>> timesByName = new HashMap<>();

        for (int i = 0; i < keyName.length; i++) {
            // Take the current employee name from the parallel keyName array.
            String name = keyName[i];

            // Take the matching time string from the parallel keyTime array.
            String time = keyTime[i];

            // Convert "10:40" into minutes after midnight.
            // Example: "10:40" becomes 10 * 60 + 40 = 640.
            int minutes = toMinutes(time);

            // If this employee is not in the map yet,
            // create an empty list for that employee.
            timesByName.putIfAbsent(name, new ArrayList<>());

            // Add this swipe time to that employee's own timeline.
            timesByName.get(name).add(minutes);
        }

        List<String> result = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : timesByName.entrySet()) {
            List<Integer> times = entry.getValue();

            // Sort so "start time to current time" means a real forward window.
            Collections.sort(times);

            // Try every swipe as the beginning of the suspicious one-hour period.
            for (int start = 0; start < times.size(); start++) {
                int count = 0;

                // Brute force recounts the whole list for every possible start time.
                // We intentionally do this directly because it mirrors the rule:
                // "How many swipes happened within 60 minutes of this swipe?"
                for (int current = 0; current < times.size(); current++) {
                    int difference = times.get(current) - times.get(start);

                    // difference >= 0 means current is not before the chosen start.
                    // difference <= 60 means it still belongs to this one-hour window.
                    if (difference >= 0 && difference <= 60) {
                        count++;
                    }
                }

                if (count >= 3) {
                    // One valid window is enough; the output only needs the name.
                    result.add(entry.getKey());
                    break;
                }
            }
        }

        // Problem asks for names in sorted order.
        Collections.sort(result);
        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: sort each timeline and slide a window instead of restarting the count from scratch.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find employees with 3 key uses in one hour.
     * 2. Remove repeated work: sort times and check windows with index math.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the multi-user example above.
     * Daniel has 10:00, 10:40, 10:50 inside one hour.
     * Luis has 09:00, 10:10, 11:30, but no 3-use one-hour group.
     * Final answer: [daniel]
     */
    public List<String> optimized(String[] keyName, String[] keyTime) {
        // Group each employee's swipe times so the one-hour rule is checked
        // person by person, never across different people.
        Map<String, List<Integer>> timesByName = new HashMap<>();
        for (int i = 0; i < keyName.length; i++) {
            // keyName and keyTime are parallel arrays, so index i describes one swipe.
            String name = keyName[i];
            String time = keyTime[i];

            // Convert the clock string into minutes to make the 60-minute test numeric.
            int minutes = toMinutes(time);

            // Create this employee's timeline on first sight.
            timesByName.putIfAbsent(name, new ArrayList<>());

            // Add this swipe to only that employee's timeline.
            timesByName.get(name).add(minutes);
        }
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : timesByName.entrySet()) {
            List<Integer> times = entry.getValue();

            // Sorting makes every length-3 window easy to inspect from left to right.
            Collections.sort(times);

            // If any three consecutive swipes fit within 60 minutes, then at
            // least three swipes exist in one hour and the employee must alert.
            for (int i = 0; i + 2 < times.size(); i++) {
                if (times.get(i + 2) - times.get(i) <= 60) {
                    // One qualifying window is enough; no need to keep checking this name.
                    result.add(entry.getKey());
                    break;
                }
            }
        }

        // The problem asks for alerted employees in alphabetical order.
        Collections.sort(result);
        return result;
    }

    private int toMinutes(String time) {

        /*
        * "10:00" -> 10 * 60 + 0  = 600
            "10:40" -> 10 * 60 + 40 = 640
            "10:50" -> 10 * 60 + 50 = 650

            "09:00" -> 9 * 60 + 0   = 540
            "10:10" -> 10 * 60 + 10 = 610
            "11:30" -> 11 * 60 + 30 = 690
            So this array:

            ["10:00", "10:40", "10:50", "09:00", "10:10", "11:30"]
            gets stored as:

            [600, 640, 650, 540, 610, 690]
        * */

        // The input time comes in "HH:MM" format.
        // Example:
        // time = "10:40"
        //
        // We split around ":" because hour and minute are stored separately
        // inside the string.
        String[] parts = time.split(":");

        // parts[0] is the hour part.
        // Example:
        // "10:40" -> parts[0] = "10"
        //
        // It is still a String, so parse it into an int before math.
        int hours = Integer.parseInt(parts[0]);

        // parts[1] is the minute part.
        // Example:
        // "10:40" -> parts[1] = "40"
        //
        // This also needs parsing because we want numeric addition.
        int minutes = Integer.parseInt(parts[1]);

        // One hour has 60 minutes.
        // So "10 hours after midnight" means:
        // 10 * 60 = 600 minutes after midnight.
        int minutesFromHours = hours * 60;

        // Add the leftover minute part.
        // Example:
        // "10:40" -> 600 + 40 = 640.
        //
        // Meaning:
        // 10:40 is 640 minutes after midnight.
        int totalMinutesAfterMidnight = minutesFromHours + minutes;

        // Return one plain number so later logic can subtract times easily.
        // Example:
        // 10:50 - 10:00 becomes 650 - 600 = 50 minutes.
        return totalMinutesAfterMidnight;
    }

    public static void main(String[] args) {
        AlertSameKeyCardOneHour solution = new AlertSameKeyCardOneHour();

        String[][] sampleNames = {
                {"daniel", "daniel", "daniel", "luis", "luis", "luis"},
                {"amy", "bob", "amy", "bob", "amy", "bob"},
                {"john", "john", "john", "jane", "jane", "jane"}
        };

        String[][] sampleTimes = {
                {"10:00", "10:40", "10:50", "09:00", "10:10", "11:30"},
                {"08:00", "08:00", "08:30", "09:30", "08:59", "10:20"},
                {"10:00", "10:30", "11:00", "12:00", "13:10", "14:20"}
        };

        for (int i = 0; i < sampleNames.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + solution.bruteForce(sampleNames[i], sampleTimes[i]));
            System.out.println("optimized:  " + solution.optimized(sampleNames[i], sampleTimes[i]));
            System.out.println();
        }
    }
}
