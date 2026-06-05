package wellsfargo.solutions;


public class CountBinarySubstrings {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a binary string, count substrings with equal consecutive 0s and 1s grouped together.
     *
     * INPUT
     * s contains only 0 and 1.
     *
     * OUTPUT
     * Number of valid substrings.
     *
     * EXAMPLE
     * s="00110011" -> 6.
     *
     * WHAT IT MEANS
     * Valid strings look like 0011 or 1100. They have two adjacent groups with equal sizes.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1: "00110011" -> 6
     * Example 2: "10101" -> 4
     * Example 3: "000" -> 0
     *
     * EDGE CASES
     * - All same character gives 0.
     * - Final group pair must be counted after the loop.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Generate every substring and check whether it has exactly two groups with equal counts.
     *
     * Time Complexity: O(n^3) with substring validation. Space Complexity: O(n) for substring creation.
     */

    /*
     * OPTIMIZED APPROACH
     *
     * Count lengths of adjacent groups. Each pair contributes min(previousGroup, currentGroup).
     *
     * Time Complexity: O(n). Space Complexity: O(1).
     */
    public int bruteForce(String s) {
        int count = 0;

        for (int start = 0; start < s.length(); start++) {
            for (int end = start + 1; end <= s.length(); end++) {
                if (isValid(s.substring(start, end))) {
                    count++;
                }
            }
        }

        return count;
    }

    public int optimized(String s) {
        int previousGroup = 0;
        int currentGroup = 1;
        int answer = 0;

        for (int index = 1; index < s.length(); index++) {
            if (s.charAt(index) == s.charAt(index - 1)) {
                currentGroup++;
            } else {
                answer += Math.min(previousGroup, currentGroup);
                previousGroup = currentGroup;
                currentGroup = 1;
            }
        }

        return answer + Math.min(previousGroup, currentGroup);
    }

    private boolean isValid(String text) {
        int index = 0;

        while (index < text.length() && text.charAt(index) == text.charAt(0)) {
            index++;
        }

        if (index == 0 || index == text.length()) {
            return false;
        }

        char second = text.charAt(index);
        int firstCount = index;
        int secondCount = 0;

        while (index < text.length() && text.charAt(index) == second) {
            secondCount++;
            index++;
        }

        return index == text.length() && firstCount == secondCount;
    }

    public static void main(String[] args) {
        CountBinarySubstrings solver = new CountBinarySubstrings();
        System.out.println("Run the examples from MORE INPUTS TO PRACTICE against bruteForce and optimized.");
    }
}
