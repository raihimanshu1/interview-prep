import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class EvaluateReversePolishNotation {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Evaluate an expression written in Reverse Polish Notation.
     * Operators come after their numbers.
     *
     * Sample Input:
     * tokens = ["2", "1", "+", "3", "*"]
     *
     * Sample Output:
     * 9
     *
     * Because (2 + 1) * 3 = 9.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * When we see a number, save it.
     * When we see an operator, use the last two saved numbers.
     *
     * "last two saved numbers" is a stack idea.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Use a list and whenever we see an operator,
     * remove the last two numbers from the list and add the result back.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * ["2", "1", "+"]
     *
     * list = [2]
     * list = [2, 1]
     * operator + uses 2 and 1 -> list = [3]
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Copy tokens into a list.
     * 2. Find the first operator in the list.
     * 3. The two values before it are the numbers to combine.
     * 4. Replace those three list items with the calculated value.
     * 5. Repeat until only one value remains.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(String[] tokens) {
        List<String> expression = new ArrayList<>();

        for (String token : tokens) {
            expression.add(token);
        }

        while (expression.size() > 1) {
            for (int i = 0; i < expression.size(); i++) {
                String token = expression.get(i);

                if (!isOperator(token)) {
                    continue;
                }

                int left = Integer.parseInt(expression.get(i - 2));
                int right = Integer.parseInt(expression.get(i - 1));
                int result = apply(left, right, token);

                expression.set(i - 2, String.valueOf(result));
                expression.remove(i);
                expression.remove(i - 1);
                break;
            }
        }

        return Integer.parseInt(expression.get(0));
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The stack solution is already the natural optimized solution.
     * We only need one pass because every token is used exactly once.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * tokens = ["4", "13", "5", "/", "+"]
     *
     * push 4
     * push 13
     * push 5
     * / means 13 / 5 = 2, push 2
     * + means 4 + 2 = 6
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Use a stack of numbers.
     * 2. Push numbers when seen.
     * 3. On operator, pop right value first and left value second.
     * 4. Push the calculated result.
     * 5. Final stack value is the answer.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A recursive evaluator can read tokens from right to left: when it sees an
     * operator, it recursively evaluates the right expression and then the left
     * expression. The stack version is simpler for beginners because it follows
     * the tokens in their given order.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(String[] tokens) {
        Stack<Integer> numbers = new Stack<>();

        for (String token : tokens) {
            if (!isOperator(token)) {
                numbers.push(Integer.parseInt(token));
                continue;
            }

            int right = numbers.pop();
            int left = numbers.pop();
            // Pop right first because subtraction and division depend on order.
            int result = apply(left, right, token);

            numbers.push(result);
        }

        return numbers.pop();
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private int apply(int left, int right, String operator) {
        if (operator.equals("+")) {
            return left + right;
        }

        if (operator.equals("-")) {
            return left - right;
        }

        if (operator.equals("*")) {
            return left * right;
        }

        return left / right;
    }
}
