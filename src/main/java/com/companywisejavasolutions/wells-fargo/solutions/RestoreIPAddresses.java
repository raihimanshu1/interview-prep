
package com.companywisejavasolutions.wellsFargo.solutions;
import java.util.ArrayList;
import java.util.List;

public class RestoreIPAddresses {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a string containing only digits, return all possible valid IP addresses by inserting three dots.
     *
     * INPUT
     * s is a digit string.
     *
     * OUTPUT
     * A list of valid IP address strings.
     *
     * EXAMPLE
     * s = "25525511135" -> [255.255.11.135, 255.255.111.35]
     *
     * WHAT IT MEANS
     * An IP address has exactly four parts. Each part must be from 0 to 255, and leading zeroes are not allowed unless the part is exactly 0.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1: "25525511135" -> [255.255.11.135, 255.255.111.35]
     * Example 2: "0000" -> [0.0.0.0]
     * Example 3: "101023" -> [1.0.10.23, 1.0.102.3, 10.1.0.23, 10.10.2.3, 101.0.2.3]
     *
     * EDGE CASES
     * - Length less than 4 or greater than 12 cannot form an IP.
     * - Segment "01" is invalid because of leading zero.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Try every possible location for the three dots, then validate the four resulting segments.
     *
     * Time Complexity: O(1) for IP constraints, because there are at most 12 digits. Conceptually O(n^3). Space Complexity: O(1) besides output.
     */

    /*
     * OPTIMIZED APPROACH
     *
     * Backtrack one segment at a time. Prune early when remaining characters are too few or too many for the remaining segments.
     *
     * Time Complexity: O(1) for fixed IP size. Space Complexity: O(1) recursion depth of at most 4, besides output.
     */
public List<String> bruteForce(String s) {
        List<String> answer = new ArrayList<>();

        for (int firstDot = 1; firstDot < s.length(); firstDot++) {
            for (int secondDot = firstDot + 1; secondDot < s.length(); secondDot++) {
                for (int thirdDot = secondDot + 1; thirdDot < s.length(); thirdDot++) {
                    String first = s.substring(0, firstDot);
                    String second = s.substring(firstDot, secondDot);
                    String third = s.substring(secondDot, thirdDot);
                    String fourth = s.substring(thirdDot);

                    if (isValidSegment(first) && isValidSegment(second)
                            && isValidSegment(third) && isValidSegment(fourth)) {
                        answer.add(first + "." + second + "." + third + "." + fourth);
                    }
                }
            }
        }

        return answer;
    }

    public List<String> optimized(String s) {
        List<String> answer = new ArrayList<>();
        backtrack(s, 0, new ArrayList<>(), answer);
        return answer;
    }

    private void backtrack(String s, int index, List<String> parts, List<String> answer) {
        if (parts.size() == 4) {
            if (index == s.length()) {
                answer.add(String.join(".", parts));
            }
            return;
        }

        int remainingParts = 4 - parts.size();
        int remainingChars = s.length() - index;

        if (remainingChars < remainingParts || remainingChars > remainingParts * 3) {
            return;
        }

        for (int length = 1; length <= 3 && index + length <= s.length(); length++) {
            String segment = s.substring(index, index + length);

            if (!isValidSegment(segment)) {
                continue;
            }

            parts.add(segment);
            backtrack(s, index + length, parts, answer);
            parts.remove(parts.size() - 1);
        }
    }

    private boolean isValidSegment(String segment) {
        if (segment.length() > 1 && segment.charAt(0) == '0') {
            return false;
        }

        int value = Integer.parseInt(segment);
        return value <= 255;
    }

    public static void main(String[] args) {
        RestoreIPAddresses solver = new RestoreIPAddresses();
        System.out.println("Use bruteForce and optimized with the examples in MORE INPUTS TO PRACTICE.");
    }
}
