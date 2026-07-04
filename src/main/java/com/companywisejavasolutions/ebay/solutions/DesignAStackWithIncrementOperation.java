

package com.companywisejavasolutions.ebay.solutions;
public class DesignAStackWithIncrementOperation {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Build a stack with push, pop, and increment(k, val), where increment adds
     * val to the bottom k elements.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Instead of updating many bottom elements immediately, store a pending
     * increment at the highest affected index. When popping, pass the pending
     * value down one level.
     */
    public static class CustomStack {
        private final int[] stack;
        private final int[] increment;
        private int top = -1;

        public CustomStack(int maxSize) {
            stack = new int[maxSize];
            increment = new int[maxSize];
        }

        public void push(int x) {
            if (top + 1 == stack.length) {
                return;
            }
            top++;
            stack[top] = x;
        }

        public int pop() {
            if (top == -1) {
                return -1;
            }

            int value = stack[top] + increment[top];
            if (top > 0) {
                increment[top - 1] += increment[top];
            }
            increment[top] = 0;
            top--;
            return value;
        }

        public void increment(int k, int val) {
            int index = Math.min(k - 1, top);
            if (index >= 0) {
                increment[index] += val;
            }
        }
    }
}
