package wellsfargo.solutions;


public class RotateString {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given two strings s and goal, return true if s can become goal after some number of left rotations.
     *
     * INPUT
     * s = original string, goal = target string.
     *
     * OUTPUT
     * true if goal is a rotation of s, otherwise false.
     *
     * EXAMPLE
     * s = "abcde", goal = "cdeab" -> true.
     *
     * WHAT IT MEANS
     * A rotation keeps circular character order. It only chooses a different starting point.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1: s="abcde", goal="cdeab" -> true
     * Example 2: s="abcde", goal="abced" -> false
     * Example 3: s="", goal="" -> true
     *
     * EDGE CASES
     * - Different lengths can never match.
     * - Empty string rotates to empty string.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Try every cut point. Build s[cut..end] + s[0..cut) and compare with goal.
     *
     * Time Complexity: O(n^2), because each rotation builds/compares length n. Space Complexity: O(n).
     */

    /*
     * OPTIMIZED APPROACH
     *
     * If goal is a rotation of s, it must appear inside s + s. The doubled string exposes every wraparound rotation as a normal substring.
     *
     * Time Complexity: O(n) with efficient substring search. Space Complexity: O(n).
     */
    public boolean bruteForce(String s, String goal) {
        if (s.length() != goal.length()) {
            return false;
        }

        for (int cut = 0; cut < s.length(); cut++) {
            String rotated = s.substring(cut) + s.substring(0, cut);

            if (rotated.equals(goal)) {
                return true;
            }
        }

        return s.isEmpty() && goal.isEmpty();
    }

    public boolean optimized(String s, String goal) {
        if (s.length() != goal.length()) {
            return false;
        }

        return (s + s).contains(goal);
    }

    public static void main(String[] args) {
        RotateString solver = new RotateString();
        System.out.println("Run the examples from MORE INPUTS TO PRACTICE against bruteForce and optimized.");
    }
}
