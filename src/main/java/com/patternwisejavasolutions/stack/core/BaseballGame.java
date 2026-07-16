package com.patternwisejavasolutions.stack.core;

import java.util.Stack;

public class BaseballGame {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * You are given baseball score operations. Numbers add scores, "+" adds
     * the previous two scores, "D" doubles the previous score, and "C" removes
     * the previous score.
     *
     * Sample Input: ops = ["5","2","C","D","+"]
     * Sample Output: 30
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Every special operation talks about the most recent valid score. "C" is
     * the giveaway: the last valid score must be easy to remove. That is stack
     * memory: the newest kept score is the first one we need again.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Keep valid scores in an array-like list. Use the list end as the latest
     * score because all special operations point backward to the most recent
     * valid rounds.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Walk through operations.
     * 2. For a number, append it.
     * 3. For C, remove the last score.
     * 4. For D or +, look at the last one or two scores and append the new score.
     * 5. Sum the list.
     *
     * BRUTE FORCE DRY RUN
     *
     * 5 -> [5], 2 -> [5,2], C -> [5], D -> [5,10], + -> [5,10,15], sum 30
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public int bruteForce(String[] ops) {
        java.util.List<Integer> scores = new java.util.ArrayList<>();

        for (String op : ops) {
            int size = scores.size();

            if (op.equals("C")) {
                scores.remove(size - 1);
            } else if (op.equals("D")) {
                scores.add(scores.get(size - 1) * 2);
            } else if (op.equals("+")) {
                scores.add(scores.get(size - 1) + scores.get(size - 2));
            } else {
                scores.add(Integer.parseInt(op));
            }
        }

        return sum(scores);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The list version already uses the end as the latest score. A stack is the
     * same idea with the right tool: push new scores and pop canceled scores.
     * The top is always the score a "C" or "D" cares about.
     *
     * OPTIMIZED ALGORITHM
     * 1. Use a stack of valid scores.
     * 2. Apply each operation using stack top values.
     * 3. Sum remaining stack values.
     *
     * OPTIMIZED DRY RUN
     *
     * ops ["5","-2","4","C","D","9","+","+"]
     * push 5, push -2, push 4 -> [5,-2,4]
     * C pops 4 because it is the most recent valid score -> [5,-2]
     * D doubles -2 -> [5,-2,-4]
     * 9 -> [5,-2,-4,9]
     * + adds 9 and -4 -> push 5
     * + adds 5 and 9 -> push 14
     * stack ends as [5,-2,-4,9,5,14], sum 27
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public int optimized(String[] ops) {
        Stack<Integer> scores = new Stack<>();

        for (String op : ops) {
            if (op.equals("C")) {
                // Cancel the most recent valid score.
                scores.pop();
            } else if (op.equals("D")) {
                // New score is double the most recent valid score.
                scores.push(scores.peek() * 2);
            } else if (op.equals("+")) {
                int last = scores.pop();
                int newScore = last + scores.peek();
                // Put last back, then append the sum of the two latest valid scores.
                scores.push(last);
                scores.push(newScore);
            } else {
                scores.push(Integer.parseInt(op));
            }
        }

        return sum(scores);
    }

    private int sum(java.util.List<Integer> scores) {
        int total = 0;

        for (int score : scores) {
            total += score;
        }

        return total;
    }
}
