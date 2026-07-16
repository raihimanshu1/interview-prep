package com.patternwisejavasolutions.stack.core;

import java.util.Stack;

public class ValidParentheses {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Check if brackets are closed in the correct order.
     *
     * Sample Input:
     * s = "()[]{}"
     *
     * Sample Output:
     * true
     *
     * Sample Input:
     * s = "(]"
     *
     * Sample Output:
     * false
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The most recent opening bracket must be closed first.
     *
     * Example:
     * In "({})", the '{' opens after '('.
     * So '}' must close before ')'.
     *
     * This "last opened, first closed" behavior is exactly a stack.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Keep removing valid pairs: "()", "{}", "[]".
     * If the string becomes empty, it was valid.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "({})"
     *
     * Remove "{}" -> "()"
     * Remove "()" -> ""
     * Empty means true.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Repeatedly remove "()", "{}", and "[]".
     * 2. Stop when one full pass cannot remove anything.
     * 3. If string is empty, return true.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public boolean bruteForce(String s) {
        boolean changed = true;

        while (changed) {
            String before = s;

            s = s.replace("()", "");
            s = s.replace("{}", "");
            s = s.replace("[]", "");

            changed = !s.equals(before);
        }

        return s.isEmpty();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Use a stack to remember opening brackets.
     * When we see a closing bracket, it must match the top opening bracket.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "({})"
     *
     * '(' -> push
     * '{' -> push
     * '}' -> top is '{', match, pop
     * ')' -> top is '(', match, pop
     * stack empty -> true
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Push opening brackets onto stack.
     * 2. For closing bracket, stack cannot be empty.
     * 3. Pop top opening bracket and check if it matches.
     * 4. At the end, stack must be empty.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Repeatedly deleting pairs like (), [], and {} eventually works, but each
     * deletion shifts the string and can be slow. A stack checks nesting in one
     * left-to-right pass.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public boolean optimized(String s) {
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char current = s.charAt(i);

            if (current == '(' || current == '{' || current == '[') {
                // Opening brackets wait for a matching closing bracket later.
                stack.push(current);
            } else {
                if (stack.isEmpty()) {
                    // A closing bracket with no earlier opening bracket cannot be valid.
                    return false;
                }

                char lastOpening = stack.pop();

                if (!isMatchingPair(lastOpening, current)) {
                    // The most recent opening bracket must be the one that closes now.
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    private boolean isMatchingPair(char open, char close) {
        return (open == '(' && close == ')')
            || (open == '{' && close == '}')
            || (open == '[' && close == ']');
    }
}
