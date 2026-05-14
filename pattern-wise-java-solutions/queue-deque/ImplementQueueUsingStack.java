import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ImplementQueueUsingStack {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Build a queue using stacks. A queue removes the oldest inserted value
     * first, but a stack removes the newest inserted value first.
     *
     * Sample Input:
     * push(1), push(2), peek(), pop(), empty()
     *
     * Sample Output:
     * 1, 1, false
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Think of a queue like a line at a ticket counter: the first person to
     * arrive is served first. A stack is like a pile of plates: the last plate
     * placed is picked first. To turn a pile into a line, we can reverse order
     * using another pile.
     *
     * This is a queue/deque design problem. The optimized idea is to delay the
     * expensive reversal until we really need the front.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Store values in a simple list in queue order. push adds at the end, pop
     * removes from index 0. This is easy to understand, but removing index 0
     * shifts all later values left.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Keep a list named values.
     * 2. push(x): add x to the end.
     * 3. pop(): remember values[0], remove index 0, return remembered value.
     * 4. peek(): return values[0].
     * 5. empty(): check whether the list has no values.
     *
     * Time Complexity: push O(1), peek O(1), empty O(1), pop O(n)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * push(1) -> [1]
     * push(2) -> [1, 2]
     * peek() sees 1 because it arrived first.
     * pop() removes 1, remaining list is [2].
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    static class BruteForceQueue {
        private final List<Integer> values = new ArrayList<>();

        public void push(int value) {
            values.add(value);
        }

        public int pop() {
            int front = values.get(0);
            values.remove(0);
            return front;
        }

        public int peek() {
            return values.get(0);
        }

        public boolean empty() {
            return values.isEmpty();
        }
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is removing from the front of a list. With two
     * stacks, we use inputStack for new values. When outputStack is empty, we
     * pour all input values into it. Pouring reverses the order, so the oldest
     * value becomes the top of outputStack.
     *
     * Other useful approach: move everything on every push so pop is always
     * cheap. The lazy two-stack version is better overall because each value is
     * moved at most once from inputStack to outputStack.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. push(x): push x into inputStack.
     * 2. For pop or peek, first call moveIfNeeded().
     * 3. moveIfNeeded(): if outputStack is empty, move every input value to it.
     * 4. pop(): pop from outputStack.
     * 5. peek(): peek outputStack.
     *
     * Time Complexity: Amortized O(1) for each operation
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * push(1): input [1], output []
     * push(2): input [1, 2], output []
     * peek(): output is empty, move 2 then 1 -> output [2, 1], top is 1.
     * pop(): remove top of output, returns 1.
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A queue can also be implemented with one stack using recursion during pop
     * and peek. That is a useful interview exercise, but two stacks make the
     * first-in-first-out order easier to see.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    static class OptimizedQueue {
        private final Stack<Integer> inputStack = new Stack<>();
        private final Stack<Integer> outputStack = new Stack<>();

        public void push(int value) {
            // New values wait here until the output side needs to reveal the oldest value.
            inputStack.push(value);
        }

        public int pop() {
            moveIfNeeded();
            return outputStack.pop();
        }

        public int peek() {
            moveIfNeeded();
            return outputStack.peek();
        }

        public boolean empty() {
            return inputStack.isEmpty() && outputStack.isEmpty();
        }

        private void moveIfNeeded() {
            if (!outputStack.isEmpty()) {
                return;
            }

            while (!inputStack.isEmpty()) {
                // Reversal makes the oldest pushed value appear on top.
                outputStack.push(inputStack.pop());
            }
        }
    }
}
