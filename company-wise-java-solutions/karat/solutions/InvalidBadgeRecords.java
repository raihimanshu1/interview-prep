package karat.solutions;

import java.util.*;

public class InvalidBadgeRecords {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given enter and exit badge records, find employees who entered without a matching exit and employees who exited without a matching enter.
     *
     * INPUT
     * records[i] = [employeeName, "enter" or "exit"].
     *
     * OUTPUT
     * A map with missingExit and missingEnter lists.
     *
     * EXAMPLE
     * records = [["Paul","enter"], ["Paul","enter"], ["Paul","exit"], ["Martha","exit"], ["Curtis","enter"]]
     * Output: {missingExit=[Curtis, Paul], missingEnter=[Martha]}
     * 
     * Paul entered twice without exiting, Martha exited without entering, and Curtis never exited.
     *
     * WHAT IT MEANS
     * Track whether each employee is currently inside. Invalid state transitions reveal missing records.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of each employee as either inside or outside. Entering while already
     * inside means a missing exit; exiting while outside means a missing enter.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * A badge log is supposed to alternate for each person:
     * enter, exit, enter, exit, and so on.
     *
     * If someone enters twice in a row, the system never saw the exit between
     * those two enters. That person belongs in missingExit.
     *
     * If someone exits while the system thinks they are outside, the system
     * never saw the enter before that exit. That person belongs in missingEnter.
     *
     * At the very end, anyone still inside also belongs in missingExit because
     * the day ended without a matching exit record.
     */

    /*
     * NORMAL EXAMPLES
     *
     * Example 1
     * records = [["Paul","enter"], ["Paul","enter"], ["Paul","exit"]]
     * Output: {missingExit=[Paul], missingEnter=[]}
     * Reason: Paul's second enter happened before a matching exit.
     *
     * Example 2
     * records = [["Martha","exit"]]
     * Output: {missingExit=[], missingEnter=[Martha]}
     * Reason: Martha exited without first entering.
     *
     * Example 3
     * records = [["Curtis","enter"], ["Curtis","exit"], ["Curtis","enter"]]
     * Output: {missingExit=[Curtis], missingEnter=[]}
     * Reason: Curtis's final enter was never matched by an exit.
     */

    /*
     * EDGE CASES
     *
     * 1. A person can appear in both lists.
     *    Example: exit while outside, then enter and never exit.
     *
     * 2. Duplicate mistakes should not duplicate names in the answer.
     *    Sets are useful because each list should contain each name once.
     *
     * 3. A perfectly alternating timeline produces no missing records.
     *
     * 4. Records are processed in the order given because the sequence itself
     *    is the evidence.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. For one person, the only state we need is inside or outside.
     * 2. "enter" while inside means missing exit.
     * 3. "exit" while outside means missing enter.
     * 4. Still inside after all records means missing exit.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * The brute force version first collects every person.
     * Then it studies one person's whole timeline at a time by scanning all
     * records and ignoring records for other people.
     * This repeats work, but it makes the state transitions very visible.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    Each badge record changes a person's state: either inside or outside.
     *
     * 2. What data structure does that naturally suggest?
     *    Use a set of people currently inside because invalid actions are state violations.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: for every record, scan previous records to infer state.
     *
     * 4. What repeated work should I remove?
     *    Optimized: update the inside set as we read records once.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: for every record, scan previous records to infer state.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Collect all unique employee names.
     * 2. For each employee, start with inside = false.
     * 3. Scan all records from the beginning and only react to this employee's records.
     * 4. On "enter":
     *    if already inside, add the employee to missingExit; then mark inside.
     * 5. On "exit":
     *    if outside, add the employee to missingEnter; otherwise mark outside.
     * 6. After the scan, if still inside, add the employee to missingExit.
     * 
     * Time Complexity: O(p * n), where p is unique people and n is records.
     * Space Complexity: O(p) for people and result sets.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the multi-person badge example above.
     * Paul enters twice before exiting, so Paul is missing an exit.
     * Martha exits without entering, so Martha is missing an enter.
     * Curtis enters and never exits, so Curtis is missing an exit.
     */
    public Map<String, List<String>> bruteForce(String[][] records) {

        // First collect all names so we can study one complete person timeline at a time.
        Set<String> people = new HashSet<>();
        for (String[] record : records) {
            people.add(record[0]);
        }

        // Sets avoid duplicate names when the same person has the same mistake many times.
        Set<String> missingExit = new HashSet<>();
        Set<String> missingEnter = new HashSet<>();

        for (String person : people) {
            // For this one person, false means "currently outside".
            boolean inside = false;

            // Brute force studies one employee's full timeline at a time.
            for (String[] record : records) {
                // Ignore other people while reconstructing this person's state.
                if (!record[0].equals(person)) {
                    continue;
                }

                if ("enter".equals(record[1])) {
                    if (inside) {
                        // Entering while already inside means the previous visit never got an exit.
                        missingExit.add(person);
                    }

                    // After an enter record, the system considers the person inside.
                    inside = true;
                } else {
                    if (!inside) {
                        // Exiting while outside means there was no matching enter before this exit.
                        missingEnter.add(person);
                    }

                    // After an exit record, the system considers the person outside.
                    inside = false;
                }
            }

            if (inside) {
                // The scan ended while this person was still inside, so their final exit is missing.
                missingExit.add(person);
            }
        }

        Map<String, List<String>> result = new TreeMap<>();
        result.put("missingExit", new ArrayList<>(missingExit));
        result.put("missingEnter", new ArrayList<>(missingEnter));
        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: update the inside set as we read records once.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find enter/exit state mistakes.
     * 2. Remove repeated work: track all employees current inside state in one pass.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the multi-person badge example above.
     * Paul enters twice before exiting, so Paul is missing an exit.
     * Martha exits without entering, so Martha is missing an enter.
     * Curtis enters and never exits, so Curtis is missing an exit.
     */
    public Map<String, List<String>> optimized(String[][] records) {
        // HashSet lets us ask "have we seen this before?" in constant average time.
        Set<String> inside = new HashSet<>();
        // HashSet lets us ask "have we seen this before?" in constant average time.
        Set<String> missingExit = new HashSet<>();
        // HashSet lets us ask "have we seen this before?" in constant average time.
        Set<String> missingEnter = new HashSet<>();
        for (String[] record : records) {
            String name = record[0];
            String action = record[1];
        if ("enter".equals(action)) {
        if (inside.contains(name)) {
                    missingExit.add(name);
                }
                inside.add(name);
            } else {
        if (!inside.contains(name)) {
                    missingEnter.add(name);
                } else {
                    inside.remove(name);
                }
            }
        }

        missingExit.addAll(inside);
        Map<String, List<String>> result = new TreeMap<>();
        result.put("missingExit", new ArrayList<>(missingExit));
        result.put("missingEnter", new ArrayList<>(missingEnter));
        return result;
    }

    public static void main(String[] args) {
        InvalidBadgeRecords solution = new InvalidBadgeRecords();

        String[][][] samples = {
                {
                        {"Paul", "enter"},
                        {"Paul", "enter"},
                        {"Paul", "exit"},
                        {"Martha", "exit"},
                        {"Curtis", "enter"}
                },
                {
                        {"Curtis", "enter"},
                        {"Curtis", "exit"},
                        {"Curtis", "enter"}
                },
                {
                        {"Alice", "enter"},
                        {"Alice", "exit"},
                        {"Bob", "exit"},
                        {"Bob", "enter"}
                }
        };

        for (int i = 0; i < samples.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + formatMap(solution.bruteForce(samples[i])));
            System.out.println("optimized:  " + formatMap(solution.optimized(samples[i])));
            System.out.println();
        }
    }

    private static String formatMap(Map<String, List<String>> map) {
        Map<String, List<String>> sorted = new TreeMap<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> values = new ArrayList<>(entry.getValue());
            Collections.sort(values);
            sorted.put(entry.getKey(), values);
        }
        return sorted.toString();
    }
}
