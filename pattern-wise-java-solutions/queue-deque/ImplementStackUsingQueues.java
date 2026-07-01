/**
 * IMPLEMENT STACK USING QUEUES
 * 
 * Implement a LIFO stack using only two queues.
 * The implemented stack should support push, pop, top, and empty.
 * 
 * Approach: Use two queues. On push, add to q2, then move all from q1 to q2,
 * then swap. This makes push O(n), pop O(1).
 * 
 * LeetCode 225 - Easy
 */

import java.util.LinkedList;
import java.util.Queue;

public class ImplementStackUsingQueues {
    
    static class MyStack {
        private Queue<Integer> q1;
        private Queue<Integer> q2;
        
        public MyStack() {
            q1 = new LinkedList<>();
            q2 = new LinkedList<>();
        }
        
        // Push element onto stack (O(n))
        public void push(int x) {
            q2.offer(x);
            while (!q1.isEmpty()) {
                q2.offer(q1.poll());
            }
            // Swap q1 and q2
            Queue<Integer> temp = q1;
            q1 = q2;
            q2 = temp;
        }
        
        // Removes the element on top and returns it (O(1))
        public int pop() {
            return q1.poll();
        }
        
        // Get the top element (O(1))
        public int top() {
            return q1.peek();
        }
        
        // Returns whether the stack is empty
        public boolean empty() {
            return q1.isEmpty();
        }
    }
    
    public static void main(String[] args) {
        MyStack stack = new MyStack();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        
        System.out.println("=== STACK USING QUEUES ===");
        System.out.println("Top: " + stack.top() + " (expected: 3)");
        System.out.println("Pop: " + stack.pop() + " (expected: 3)");
        System.out.println("Pop: " + stack.pop() + " (expected: 2)");
        System.out.println("Empty: " + stack.empty() + " (expected: false)");
        System.out.println("Pop: " + stack.pop() + " (expected: 1)");
        System.out.println("Empty: " + stack.empty() + " (expected: true)");
        
        System.out.println("\n=== KEY INSIGHT ===");
        System.out.println("Queue is FIFO, Stack is LIFO — need reversal");
        System.out.println("Option 1 (above): Make push O(n) by reversing during push");
        System.out.println("Option 2: Make pop O(n) by reversing during pop");
    }
}