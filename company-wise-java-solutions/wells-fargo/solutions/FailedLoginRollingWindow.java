package wellsfargo.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FailedLoginRollingWindow {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given login events for accounts, find accounts that have at least
     * threshold failed login attempts within any rolling time window.
     *
     * INPUT
     * accounts[i] is the account id for event i.
     * minutes[i] is the event time measured as minutes from start of day.
     * failed[i] is true if event i is a failed login.
     * threshold is the minimum number of failed attempts needed to alert.
     * windowSize is the allowed rolling window size in minutes.
     *
     * OUTPUT
     * A map from account id to the first failed-login window that proves the alert.
     *
     * EXAMPLE
     * accounts = ["A", "A", "A", "B"]
     * minutes =  [600, 605, 620, 700]
     * failed =   [true,true,true,true]
     * threshold = 3, windowSize = 30
     * Output: {A=[600, 605, 620]}
     *
     * WHAT IT MEANS
     * Account A failed three times between minute 600 and 620, which is inside
     * a 30-minute window.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * This problem is not asking for total failed logins.
     * It is asking for a burst of failures in a short time.
     *
     * For fraud/security systems, timing matters:
     *
     * 3 failures across 3 months may be harmless.
     * 3 failures inside 5 minutes may indicate credential stuffing.
     *
     * What to know before solving:
     *
     * 1. Successful logins do not count.
     * 2. Each account must be checked separately.
     * 3. Event input may not be sorted.
     * 4. A rolling window is inclusive here: start..start + windowSize.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Alert
     * accounts = ["A", "A", "A"], minutes = [10, 20, 35], failed = [true,true,true]
     * threshold = 3, windowSize = 30
     * Output: {A=[10, 20, 35]}
     *
     * Example 2 - No alert because spread is too wide
     * accounts = ["A", "A", "A"], minutes = [10, 50, 90], failed = [true,true,true]
     * Output: {}
     *
     * Example 3 - Success events ignored
     * accounts = ["A", "A", "A"], failed = [true,false,true]
     * threshold = 2
     * Only the two failed events count.
     *
     * Edge case 1 - Empty input returns {}.
     * Edge case 2 - threshold = 1 means every account with one failure alerts.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Group failures by account.
     * For each account, pick every failed time as a possible window start.
     * Scan all failed times again and collect times inside start..start+windowSize.
     *
     * Time Complexity: O(n log n + k^2) across account timelines.
     * Space Complexity: O(n)
     */
    public Map<String, List<Integer>> bruteForce(String[] accounts, int[] minutes,
                                                 boolean[] failed, int threshold, int windowSize) {
        // Group first because failures from account A must not create an alert for account B.
        Map<String, List<Integer>> accountToFailures = groupFailedAttempts(accounts, minutes, failed);

        // TreeMap gives deterministic account order, which makes practice output stable.
        Map<String, List<Integer>> alerts = new TreeMap<>();

        for (Map.Entry<String, List<Integer>> entry : accountToFailures.entrySet()) {
            List<Integer> times = entry.getValue();

            // Sorting turns unsorted security logs into a timeline.
            Collections.sort(times);

            // Try each failed login as the start of a possible alert window.
            for (int start = 0; start < times.size(); start++) {
                List<Integer> window = new ArrayList<>();

                // Brute force scans the whole timeline again for this start.
                for (int current = 0; current < times.size(); current++) {
                    int difference = times.get(current) - times.get(start);

                    // Keep only failures from this start through the inclusive window end.
                    if (difference >= 0 && difference <= windowSize) {
                        window.add(times.get(current));
                    }
                }

                if (window.size() >= threshold) {
                    alerts.put(entry.getKey(), window);
                    break;
                }
            }
        }

        return alerts;
    }

    /*
     * OPTIMIZED APPROACH
     *
     * The repeated work in brute force is rescanning the same sorted timeline
     * for every start point.
     *
     * Once times are sorted, use two pointers:
     * left  = first failure in current window
     * right = newest failure in current window
     *
     * The right pointer only moves forward.
     * The left pointer only moves forward when the window becomes too wide.
     *
     * Time Complexity: O(n log n) for sorting plus O(n) window scans.
     * Space Complexity: O(n)
     */
    public Map<String, List<Integer>> optimized(String[] accounts, int[] minutes,
                                                boolean[] failed, int threshold, int windowSize) {
        Map<String, List<Integer>> accountToFailures = groupFailedAttempts(accounts, minutes, failed);
        Map<String, List<Integer>> alerts = new TreeMap<>();

        for (Map.Entry<String, List<Integer>> entry : accountToFailures.entrySet()) {
            List<Integer> times = entry.getValue();
            Collections.sort(times);

            int left = 0;

            for (int right = 0; right < times.size(); right++) {
                // Shrink until the current window fits inside windowSize.
                while (times.get(right) - times.get(left) > windowSize) {
                    left++;
                }

                // right-left+1 is the number of failures in the current valid window.
                if (right - left + 1 >= threshold) {
                    alerts.put(entry.getKey(), new ArrayList<>(times.subList(left, right + 1)));
                    break;
                }
            }
        }

        return alerts;
    }

    private Map<String, List<Integer>> groupFailedAttempts(String[] accounts, int[] minutes, boolean[] failed) {
        Map<String, List<Integer>> accountToFailures = new HashMap<>();

        for (int i = 0; i < accounts.length; i++) {
            if (!failed[i]) {
                continue;
            }

            accountToFailures.computeIfAbsent(accounts[i], ignored -> new ArrayList<>()).add(minutes[i]);
        }

        return accountToFailures;
    }

    public static void main(String[] args) {
        FailedLoginRollingWindow solver = new FailedLoginRollingWindow();

        String[] accounts = {"A", "A", "B", "A", "B"};
        int[] minutes = {600, 605, 610, 620, 900};
        boolean[] failed = {true, true, true, true, true};

        System.out.println("Brute force: " + solver.bruteForce(accounts, minutes, failed, 3, 30));
        System.out.println("Optimized: " + solver.optimized(accounts, minutes, failed, 3, 30));

        String[] noAlertAccounts = {"A", "A", "A"};
        int[] noAlertMinutes = {10, 50, 90};
        boolean[] noAlertFailed = {true, true, true};

        System.out.println("No alert: " + solver.optimized(noAlertAccounts, noAlertMinutes, noAlertFailed, 3, 30));
    }
}
