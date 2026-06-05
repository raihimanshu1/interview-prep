package wellsfargo.solutions;


public class MergeStringsAlternately {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given two strings, merge them by alternating characters, starting with word1.
     *
     * INPUT
     * word1 and word2.
     *
     * OUTPUT
     * A merged string.
     *
     * EXAMPLE
     * word1="abc", word2="pqr" -> "apbqcr".
     *
     * WHAT IT MEANS
     * Take one character from each string like dealing cards from two piles.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1: "abc", "pqr" -> "apbqcr"
     * Example 2: "ab", "pqrs" -> "apbqrs"
     * Example 3: "", "xyz" -> "xyz"
     *
     * EDGE CASES
     * - One string can be longer.
     * - One string can be empty.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Build the answer using direct string concatenation.
     *
     * Time Complexity: O((n+m)^2) in Java due to repeated String creation. Space Complexity: O(n+m).
     */

    /*
     * OPTIMIZED APPROACH
     *
     * Use StringBuilder so appending characters does not repeatedly recreate the whole answer.
     *
     * Time Complexity: O(n+m). Space Complexity: O(n+m) for output.
     */
    public String bruteForce(String word1, String word2) {
        String merged = "";
        int maxLength = Math.max(word1.length(), word2.length());

        for (int index = 0; index < maxLength; index++) {
            if (index < word1.length()) {
                merged = merged + word1.charAt(index);
            }

            if (index < word2.length()) {
                merged = merged + word2.charAt(index);
            }
        }

        return merged;
    }

    public String optimized(String word1, String word2) {
        StringBuilder merged = new StringBuilder();
        int maxLength = Math.max(word1.length(), word2.length());

        for (int index = 0; index < maxLength; index++) {
            if (index < word1.length()) {
                merged.append(word1.charAt(index));
            }

            if (index < word2.length()) {
                merged.append(word2.charAt(index));
            }
        }

        return merged.toString();
    }

    public static void main(String[] args) {
        MergeStringsAlternately solver = new MergeStringsAlternately();
        System.out.println("Run the examples from MORE INPUTS TO PRACTICE against bruteForce and optimized.");
    }
}
