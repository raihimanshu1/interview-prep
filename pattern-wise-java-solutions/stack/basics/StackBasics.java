/**
 * STACK — Basics & Warmup
 * 
 * Before jumping into problems, master these fundamentals:
 * 1. Stack using Array
 * 2. Stack using LinkedList
 * 3. Monotonic Stack (increasing & decreasing) — CRITICAL pattern
 * 4. Next Greater/Previous Greater/NSE/PSE patterns
 * 5. Min Stack (track min in O(1))
 */

import java.util.*;

public class StackBasics {

    // ==========================================
    // 1. STACK USING ARRAY (Fixed size)
    // ==========================================
    
    static class ArrayStack {
        private int[] arr;
        private int top;      // index of top element
        private int capacity;
        
        ArrayStack(int capacity) {
            this.capacity = capacity;
            arr = new int[capacity];
            top = -1;  // empty stack
        }
        
        void push(int val) {
            if (top == capacity - 1) throw new RuntimeException("Stack overflow");
            arr[++top] = val;
        }
        
        int pop() {
            if (isEmpty()) throw new RuntimeException("Stack empty");
            return arr[top--];
        }
        
        int peek() {
            if (isEmpty()) throw new RuntimeException("Stack empty");
            return arr[top];
        }
        
        boolean isEmpty() { return top == -1; }
        int size() { return top + 1; }
    }
    
    // ==========================================
    // 2. STACK USING LINKED LIST (Dynamic)
    // ==========================================
    
    static class ListStack {
        private static class Node {
            int val;
            Node next;
            Node(int val) { this.val = val; }
        }
        
        private Node head;  // top of stack
        private int size;
        
        void push(int val) {
            Node newNode = new Node(val);
            newNode.next = head;
            head = newNode;
            size++;
        }
        
        int pop() {
            if (isEmpty()) throw new RuntimeException("Stack empty");
            int val = head.val;
            head = head.next;
            size--;
            return val;
        }
        
        int peek() {
            if (isEmpty()) throw new RuntimeException("Stack empty");
            return head.val;
        }
        
        boolean isEmpty() { return head == null; }
        int size() { return size; }
    }

    // ==========================================
    // 3. MIN STACK — O(1) for push/pop/min
    // ==========================================
    // Maintain a separate stack that tracks the minimum
    
    static class MinStack {
        private Stack<Integer> stack = new Stack<>();
        private Stack<Integer> minStack = new Stack<>();
        
        void push(int val) {
            stack.push(val);
            // Push to minStack if it's the new minimum (or equal)
            if (minStack.isEmpty() || val <= minStack.peek()) {
                minStack.push(val);
            }
        }
        
        int pop() {
            int val = stack.pop();
            if (val == minStack.peek()) {
                minStack.pop();
            }
            return val;
        }
        
        int top() { return stack.peek(); }
        int getMin() { return minStack.peek(); }
    }

    // ==========================================
    // 4. MONOTONIC INCREASING STACK
    // ==========================================
    // Stack where elements are in INCREASING order (bottom to top)
    // Used for: Next Smaller Element, Previous Smaller Element
    
    // Find next smaller element to the right for each element
    // O(n) — each element pushed and popped at most once
    public static int[] nextSmallerElement(int[] arr) {
        int n = arr.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        
        Stack<Integer> stack = new Stack<>();  // stores indices
        
        for (int i = 0; i < n; i++) {
            // Maintain increasing stack: pop while current is smaller
            while (!stack.isEmpty() && arr[i] < arr[stack.peek()]) {
                result[stack.pop()] = arr[i];
            }
            stack.push(i);
        }
        return result;
    }
    
    // Find previous smaller element to the left for each element
    public static int[] previousSmallerElement(int[] arr) {
        int n = arr.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        
        Stack<Integer> stack = new Stack<>();  // stores indices
        
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[i] < arr[stack.peek()]) {
                result[stack.pop()] = arr[i];
            }
            stack.push(i);
        }
        return result;
    }

    // ==========================================
    // 5. MONOTONIC DECREASING STACK
    // ==========================================
    // Stack where elements are in DECREASING order (bottom to top)
    // Used for: Next Greater Element, Previous Greater Element
    
    // Find next greater element to the right
    public static int[] nextGreaterElement(int[] arr) {
        int n = arr.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        
        Stack<Integer> stack = new Stack<>();  // stores indices
        
        for (int i = 0; i < n; i++) {
            // Maintain decreasing stack: pop while current is greater
            while (!stack.isEmpty() && arr[i] > arr[stack.peek()]) {
                result[stack.pop()] = arr[i];
            }
            stack.push(i);
        }
        return result;
    }
    
    // Find previous greater element to the left
    public static int[] previousGreaterElement(int[] arr) {
        int n = arr.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        
        Stack<Integer> stack = new Stack<>();  // stores indices
        
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[i] > arr[stack.peek()]) {
                result[stack.pop()] = arr[i];
            }
            stack.push(i);
        }
        return result;
    }

    // ==========================================
    // 6. STACK PATTERNS
    // ==========================================
    
    // Pattern: Parentheses matching
    public static boolean isValidParentheses(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if ((c == ')' && top != '(') ||
                    (c == '}' && top != '{') ||
                    (c == ']' && top != '[')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
    
    // Pattern: Remove adjacent duplicates
    public static String removeAdjacentDuplicates(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && stack.peek() == c) {
                stack.pop();  // remove pair
            } else {
                stack.push(c);
            }
        }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) sb.append(stack.pop());
        return sb.reverse().toString();
    }

    // ==========================================
    // MAIN — Test everything
    // ==========================================
    
    public static void main(String[] args) {
        System.out.println("=== STACK BASICS ===");
        
        // Array-based stack
        System.out.println("\n--- ArrayStack ---");
        ArrayStack arrayStack = new ArrayStack(5);
        arrayStack.push(1); arrayStack.push(2); arrayStack.push(3);
        System.out.println("Pop: " + arrayStack.pop() + " (expected: 3)");
        System.out.println("Peek: " + arrayStack.peek() + " (expected: 2)");
        
        // MinStack
        System.out.println("\n--- MinStack ---");
        MinStack minStack = new MinStack();
        minStack.push(3); minStack.push(5); minStack.push(2); minStack.push(1);
        System.out.println("Min: " + minStack.getMin() + " (expected: 1)");
        minStack.pop();
        System.out.println("Min after pop: " + minStack.getMin() + " (expected: 2)");
        
        // Monotonic stacks
        int[] arr = {4, 2, 1, 5, 3};
        System.out.println("\n--- Monotonic Stack ---");
        System.out.println("Array: " + Arrays.toString(arr));
        System.out.println("Next Smaller:    " + Arrays.toString(nextSmallerElement(arr)));
        System.out.println("Previous Smaller: " + Arrays.toString(previousSmallerElement(arr)));
        System.out.println("Next Greater:    " + Arrays.toString(nextGreaterElement(arr)));
        System.out.println("Previous Greater: " + Arrays.toString(previousGreaterElement(arr)));
        
        // Parentheses
        System.out.println("\n--- Patterns ---");
        System.out.println("Valid parens '({[]})': " + isValidParentheses("({[]})"));
        System.out.println("Valid parens '(]': " + isValidParentheses("(]"));
        System.out.println("Remove duplicates 'abbaca': " + removeAdjacentDuplicates("abbaca"));
        
        System.out.println("\n=== KEY STACK PATTERNS ===");
        System.out.println("1. Monotonic Increasing Stack → NSE, PSE");
        System.out.println("2. Monotonic Decreasing Stack → NGE, PGE");
        System.out.println("3. Min Stack → track min with separate stack");
        System.out.println("4. Parentheses → push opening, match closing");
        System.out.println("5. Duplicate Removal → stack with peek check");
        System.out.println("6. Expression Evaluation → postfix (RPN)");
    }
}