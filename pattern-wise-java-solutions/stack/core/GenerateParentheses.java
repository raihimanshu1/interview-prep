import java.util.ArrayList;
import java.util.List;

public class GenerateParentheses {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Generate all valid combinations of n pairs of parentheses.
     *
     * Sample Input:
     * n = 3
     *
     * Sample Output:
     * ["((()))", "(()())", "(())()", "()(())", "()()()"]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * We are building strings step by step.
     * At each step, we can try adding '(' or ')'.
     * But we must not break validity.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Generate every string of length 2n using '(' and ')'.
     * Then check which ones are valid.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * n = 2, length = 4
     *
     * Try "((((" -> invalid
     * Try "(())" -> valid
     * Try "()()" -> valid
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Generate every string of length 2n using '(' and ')'.
     * 2. When length reaches 2n, check if it is valid.
     * 3. Add only valid strings to answer.
     *
     * Time Complexity: O(2^(2n) * n)
     * Space Complexity: O(n) recursion depth, not counting answers
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public List<String> bruteForce(int n) {
        List<String> answer = new ArrayList<>();
        generateEveryString(n, new StringBuilder(), answer);
        return answer;
    }

    private void generateEveryString(int n, StringBuilder current, List<String> answer) {
        if (current.length() == 2 * n) {
            if (isValid(current.toString())) {
                answer.add(current.toString());
            }
            return;
        }

        current.append('(');
        generateEveryString(n, current, answer);
        current.deleteCharAt(current.length() - 1);

        current.append(')');
        generateEveryString(n, current, answer);
        current.deleteCharAt(current.length() - 1);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Do not build strings that are already impossible.
     *
     * We can add '(' if we still have opening brackets left.
     * We can add ')' only if it will close an existing open bracket.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * n = 2
     *
     * Start ""
     * add '(' -> "("
     * add '(' -> "(("
     * now cannot add '(' anymore
     * add ')' -> "(()"
     * add ')' -> "(())"
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Track how many opening and closing brackets are used.
     * 2. Add '(' only while openUsed < n.
     * 3. Add ')' only while closeUsed < openUsed.
     * 4. When string length is 2n, save it.
     *
     * Time Complexity: O(number of valid answers * n)
     * Space Complexity: O(n) recursion depth
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * You can generate every string of length 2 * n using '(' and ')' and then
     * filter valid ones with a balance check. Backtracking is better because it
     * avoids building strings that are already impossible.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public List<String> optimized(int n) {
        List<String> answer = new ArrayList<>();
        backtrack(n, 0, 0, new StringBuilder(), answer);
        return answer;
    }

    private void backtrack(
        int n,
        int openUsed,
        int closeUsed,
        StringBuilder current,
        List<String> answer
    ) {
        if (current.length() == 2 * n) {
            answer.add(current.toString());
            return;
        }

        if (openUsed < n) {
            // We can still start a new pair.
            current.append('(');
            backtrack(n, openUsed + 1, closeUsed, current, answer);
            current.deleteCharAt(current.length() - 1);
        }

        if (closeUsed < openUsed) {
            // Close only when there is an unmatched opening bracket.
            current.append(')');
            backtrack(n, openUsed, closeUsed + 1, current, answer);
            current.deleteCharAt(current.length() - 1);
        }
    }

    private boolean isValid(String candidate) {
        int balance = 0;

        for (int i = 0; i < candidate.length(); i++) {
            if (candidate.charAt(i) == '(') {
                balance++;
            } else {
                balance--;
            }

            if (balance < 0) {
                return false;
            }
        }

        return balance == 0;
    }
}
