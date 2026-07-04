
package com.companywisejavasolutions.wellsFargo.solutions;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MinStack {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input:
     * push(-2), push(0), push(-3), getMin()
     *
     * Sample Output:
     * -3
     *
     * Design a stack that supports:
     * push, pop, top, and getMin.
     *
     * getMin should return the smallest number currently in the stack.
     *
     * Sample Operations:
     * push(-2), push(0), push(-3), getMin()
     *
     * Sample Output:
     * -3
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A normal stack only knows the top element.
     * To get the minimum, the simple way is to scan all values.
     * The better way is to remember the minimum at each stage.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Store stack values in a list.
     * For getMin, scan every value and return the smallest.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * values = [-2, 0, -3]
     * getMin scans:
     * min = -2
     * compare 0, min stays -2
     * compare -3, min becomes -3
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Store all stack values in insertion order.
     * 2. push adds to the end.
     * 3. pop removes the last value.
     * 4. top reads the last value.
     * 5. getMin scans every value to find the smallest.
     *
     * Time Complexity: push/pop/top O(1), getMin O(n)
     * Space Complexity: O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    static class BruteForceMinStack {
        private final List<Integer> values = new ArrayList<>();

        public void push(int value) {
            values.add(value);
        }

        public void pop() {
            values.remove(values.size() - 1);
        }

        public int top() {
            return values.get(values.size() - 1);
        }

        public int getMin() {
            int minimum = values.get(0);

            for (int value : values) {
                if (value < minimum) {
                    minimum = value;
                }
            }

            return minimum;
        }
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * getMin becomes fast if we keep another stack of minimums.
     *
     * Every time we push a value, we also push the minimum so far.
     * So the top of minStack always tells us current minimum.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * push -2:
     * values [-2], minStack [-2]
     *
     * push 0:
     * current min is min(0, -2) = -2
     * values [-2, 0], minStack [-2, -2]
     *
     * push -3:
     * current min is -3
     * values [-2, 0, -3], minStack [-2, -2, -3]
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. values stack stores actual pushed numbers.
     * 2. minimums stack stores the minimum at each same level.
     * 3. push adds value and the new current minimum.
     * 4. pop removes from both stacks so they stay aligned.
     * 5. getMin reads the top of minimums.
     *
     * Time Complexity: O(1) for all operations
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Another design stores pairs of value and current minimum in one stack. The
     * two-stack design separates normal values from minimum history, which makes
     * each operation easy to explain.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    static class OptimizedMinStack {
        private final Stack<Integer> values = new Stack<>();
        private final Stack<Integer> minimums = new Stack<>();

        public void push(int value) {
            values.push(value);

            if (minimums.isEmpty()) {
                // First value is automatically the minimum so far.
                minimums.push(value);
            } else {
                // Store the minimum at this exact stack depth.
                int currentMinimum = Math.min(value, minimums.peek());
                minimums.push(currentMinimum);
            }
        }

        public void pop() {
            // Pop both stacks so their levels stay aligned.
            values.pop();
            minimums.pop();
        }

        public int top() {
            return values.peek();
        }

        public int getMin() {
            return minimums.peek();
        }
    }
}
