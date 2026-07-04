

package com.companywisejavasolutions.karat.solutions;
import java.util.*;

public class LongestContinuousUrlSequence {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given two users browsing histories, find the longest continuous sequence of URLs that appears in both histories in the same order.
     *
     * INPUT
     * first and second are ordered URL arrays.
     *
     * OUTPUT
     * The longest common contiguous URL block.
     *
     * EXAMPLE
     * first = ["/one", "/two", "/three", "/four"]
     * second = ["/zero", "/two", "/three", "/five"]
     * Output: [/two, /three]
     * 
     * The common block must be consecutive in both histories.
     *
     * WHAT IT MEANS
     * This is longest common substring, but the tokens are URLs instead of characters.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of comparing two browser histories and looking for the longest
     * identical run, not just any shared page.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * The important word is continuous.
     *
     * If both users visited "/cart" and "/checkout", that alone is not enough.
     * The pages must appear right next to each other in both histories, in the
     * same order.
     *
     * first:  /home, /search, /item, /cart, /checkout
     * second: /help, /search, /item, /cart, /profile
     *
     * The shared continuous block is:
     *
     * /search, /item, /cart
     *
     * This is the URL version of longest common substring. We are not choosing
     * scattered matches; once a mismatch appears, that particular block stops.
     */

    /*
     * EXAMPLES AND EDGE CASES
     *
     * Example 1 - Basic shared block
     *
     * first  = {"/one", "/two", "/three", "/four"}
     * second = {"/zero", "/two", "/three", "/five"}
     *
     * Output: ["/two", "/three"]
     *
     * Example 2 - Shared pages but not continuous
     *
     * first  = {"/a", "/b", "/c", "/d"}
     * second = {"/a", "/x", "/b", "/c"}
     *
     * The longest continuous shared block is ["/b", "/c"].
     * "/a", "/b", "/c" is not allowed because "/a" and "/b" are not adjacent
     * in the second history.
     *
     * Example 3 - Tie keeps the first best block found
     *
     * first  = {"/a", "/b", "/x", "/c", "/d"}
     * second = {"/a", "/b", "/y", "/c", "/d"}
     *
     * Both ["/a", "/b"] and ["/c", "/d"] have length 2.
     * This implementation keeps the first one it finds: ["/a", "/b"].
     *
     * Edge case 1 - No shared URL
     *
     * first = {"/a"}
     * second = {"/b"}
     * Output: []
     *
     * Edge case 2 - One history is empty
     *
     * If either array has length 0, there is no continuous shared block.
     * Output: []
     *
     * Edge case 3 - Entire histories match
     *
     * first = {"/a", "/b"}
     * second = {"/a", "/b"}
     * Output: ["/a", "/b"]
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    We compare two ordered browsing histories and need the longest same consecutive block.
     *
     * 2. What data structure does that naturally suggest?
     *    Use dynamic programming because matching suffix length depends on previous matching suffix length.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: try every starting pair and extend while URLs match.
     *
     * 4. What repeated work should I remove?
     *    Optimized: fill a DP table where dp[i][j] means matching suffix length ending at those positions.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. A continuous sequence means adjacent URLs only.
     * 2. We can compare URLs with equals because each URL is a String.
     * 3. Every possible answer has a start position in first and a start
     *    position in second.
     * 4. When two URLs at those starts differ, that candidate has length 0.
     * 5. When they match, keep moving both pointers forward until the match ends.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Brute force tests every possible pair of starting positions.
     * From each pair, it walks forward while both histories show the same URL.
     * Whenever that run is longer than the best run seen so far, we remember its
     * length and where it started in the first history.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: try every starting pair and extend while URLs match.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Keep bestLength and bestStart.
     * 2. Pick every possible firstStart in the first history.
     * 3. Pick every possible secondStart in the second history.
     * 4. From those starts, move forward while both URLs match.
     * 5. If this run is longer than bestLength, save it.
     * 6. Copy the best block from first into the answer list.
     * 
     * Time Complexity: Higher than optimized because this version repeats the direct work described above.
     * Space Complexity: O(n) for the answer and any direct helper state.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the two-history example above.
     * The block /two, /three appears consecutively in both histories.
     * /one and /four do not extend that shared block.
     * Final answer: [/two, /three]
     */
    public List<String> bruteForce(String[] first, String[] second) {

        // bestLength says how many URLs are in the best shared block found so far.
        int bestLength = 0;
        // bestStart points into first, because the final answer can be copied from either history.
        int bestStart = 0;

        // Every continuous block has some starting index in each history.
        for (int firstStart = 0; firstStart < first.length; firstStart++) {
            for (int secondStart = 0; secondStart < second.length; secondStart++) {
                int length = 0;

                // Extend this candidate block one URL at a time until a mismatch
                // appears or one history runs out.
                while (firstStart + length < first.length
                        && secondStart + length < second.length
                        && first[firstStart + length].equals(second[secondStart + length])) {
                    length++;
                }

                // We only replace on strictly longer runs, so ties keep the
                // earliest best block discovered by the nested loops.
                if (length > bestLength) {
                    bestLength = length;
                    bestStart = firstStart;
                }
            }
        }

        List<String> result = new ArrayList<>();
        // Rebuild the actual URL sequence after the search is done.
        for (int i = bestStart; i < bestStart + bestLength; i++) {
            result.add(first[i]);
        }

        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: fill a DP table where dp[i][j] means matching suffix length ending at those positions.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find the longest common consecutive URL block.
     * 2. Remove repeated work: use DP so each matching suffix length reuses the previous diagonal value.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the two-history example above.
     * The block /two, /three appears consecutively in both histories.
     * /one and /four do not extend that shared block.
     * Final answer: [/two, /three]
     */
    public List<String> optimized(String[] first, String[] second) {
        int[][] dp = new int[first.length + 1][second.length + 1];
        int bestLength = 0;
        int bestEnd = 0;
        for (int i = 1; i <= first.length; i++) {
            for (int j = 1; j <= second.length; j++) {
        if (first[i - 1].equals(second[j - 1])) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
        if (dp[i][j] > bestLength) {
                        bestLength = dp[i][j];
                        bestEnd = i;
                    }
                }
            }
        }
        if (bestLength == 0) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (int i = bestEnd - bestLength; i < bestEnd; i++) {
            result.add(first[i]);
        }
        return result;
    }

    public static void main(String[] args) {
        LongestContinuousUrlSequence solver = new LongestContinuousUrlSequence();

        String[][] firstSamples = {
                {"/one", "/two", "/three", "/four"},
                {"/a", "/b", "/c", "/d"},
                {"/a", "/b", "/x", "/c", "/d"}
        };
        String[][] secondSamples = {
                {"/zero", "/two", "/three", "/five"},
                {"/a", "/x", "/b", "/c"},
                {"/a", "/b", "/y", "/c", "/d"}
        };

        for (int i = 0; i < firstSamples.length; i++) {
            System.out.println("Sample " + (i + 1) + ":");
            System.out.println("first = " + Arrays.toString(firstSamples[i]));
            System.out.println("second = " + Arrays.toString(secondSamples[i]));
            System.out.println("bruteForce = "
                    + solver.bruteForce(copyArray(firstSamples[i]), copyArray(secondSamples[i])));
            System.out.println("optimized = "
                    + solver.optimized(copyArray(firstSamples[i]), copyArray(secondSamples[i])));
            System.out.println();
        }
    }

    private static String[] copyArray(String[] values) {
        String[] copy = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            copy[i] = values[i];
        }
        return copy;
    }
}
