package com.patternwisejavasolutions.queuedeque.basics;

/*
 * QUEUE / DEQUE — Basics & Warmup
 * 
 * Before jumping into problems, master these fundamentals:
 * 1. Queue using Array (Circular Queue)
 * 2. Queue using LinkedList
 * 3. Deque using Array (Circular)
 * 4. Priority Queue basics
 * 5. Sliding window max using Deque (CRITICAL pattern)
 */


import java.util.*;

public class QueueBasics {

    // ==========================================
    // 1. CIRCULAR QUEUE (Array-based)
    // ==========================================
    // Uses front + rear pointers with wrap-around
    // Efficient: O(1) for all operations
    
    static class CircularQueue {
        private int[] arr;
        private int front;   // points to first element
        private int rear;    // points to last element
        private int size;    // current number of elements
        private int capacity;
        
        CircularQueue(int capacity) {
            this.capacity = capacity;
            arr = new int[capacity];
            front = 0;
            rear = -1;
            size = 0;
        }
        
        void enqueue(int val) {
            if (isFull()) throw new RuntimeException("Queue full");
            rear = (rear + 1) % capacity;  // wrap around
            arr[rear] = val;
            size++;
        }
        
        int dequeue() {
            if (isEmpty()) throw new RuntimeException("Queue empty");
            int val = arr[front];
            front = (front + 1) % capacity;  // wrap around
            size--;
            return val;
        }
        
        int peek() {
            if (isEmpty()) throw new RuntimeException("Queue empty");
            return arr[front];
        }
        
        boolean isEmpty() { return size == 0; }
        boolean isFull() { return size == capacity; }
        int size() { return size; }
    }

    // ==========================================
    // 2. QUEUE USING LINKED LIST
    // ==========================================
    // Maintain head (for dequeue) and tail (for enqueue)
    
    static class ListQueue {
        private static class Node {
            int val;
            Node next;
            Node(int val) { this.val = val; }
        }
        
        private Node head;  // front (dequeue from here)
        private Node tail;  // rear (enqueue from here)
        private int size;
        
        void enqueue(int val) {
            Node node = new Node(val);
            if (tail != null) tail.next = node;
            tail = node;
            if (head == null) head = node;
            size++;
        }
        
        int dequeue() {
            if (isEmpty()) throw new RuntimeException("Queue empty");
            int val = head.val;
            head = head.next;
            if (head == null) tail = null;
            size--;
            return val;
        }
        
        int peek() {
            if (isEmpty()) throw new RuntimeException("Queue empty");
            return head.val;
        }
        
        boolean isEmpty() { return head == null; }
        int size() { return size; }
    }

    // ==========================================
    // 3. DEQUE (Double-Ended Queue)
    // ==========================================
    // Can add/remove from both ends — O(1)
    // ArrayDeque is the standard Java implementation
    
    static class ArrayDeque {
        private int[] arr;
        private int front;
        private int rear;
        private int size;
        private int capacity;
        
        ArrayDeque(int capacity) {
            this.capacity = capacity;
            arr = new int[capacity];
            front = 0;
            rear = -1;
            size = 0;
        }
        
        void addFirst(int val) {
            if (isFull()) throw new RuntimeException("Deque full");
            front = (front - 1 + capacity) % capacity;
            arr[front] = val;
            if (size == 0) rear = front;  // first element
            size++;
        }
        
        void addLast(int val) {
            if (isFull()) throw new RuntimeException("Deque full");
            rear = (rear + 1) % capacity;
            arr[rear] = val;
            if (size == 0) front = rear;
            size++;
        }
        
        int removeFirst() {
            if (isEmpty()) throw new RuntimeException("Deque empty");
            int val = arr[front];
            front = (front + 1) % capacity;
            size--;
            return val;
        }
        
        int removeLast() {
            if (isEmpty()) throw new RuntimeException("Deque empty");
            int val = arr[rear];
            rear = (rear - 1 + capacity) % capacity;
            size--;
            return val;
        }
        
        int getFirst() { return arr[front]; }
        int getLast() { return arr[rear]; }
        boolean isEmpty() { return size == 0; }
        boolean isFull() { return size == capacity; }
    }

    // ==========================================
    // 4. PRIORITY QUEUE (Min-Heap by default)
    // ==========================================
    // Java's PriorityQueue is a Min-Heap
    // For Max-Heap: Collections.reverseOrder()
    
    static void priorityQueueDemo() {
        // Min-Heap (default)
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.offer(5);
        minHeap.offer(1);
        minHeap.offer(3);
        System.out.println("Min-Heap poll: " + minHeap.poll() + " (expected: 1)");
        System.out.println("Min-Heap poll: " + minHeap.poll() + " (expected: 3)");
        
        // Max-Heap
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        maxHeap.offer(5);
        maxHeap.offer(1);
        maxHeap.offer(3);
        System.out.println("Max-Heap poll: " + maxHeap.poll() + " (expected: 5)");
        
        // Custom comparator
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{3, "a".hashCode()});
        pq.offer(new int[]{1, "b".hashCode()});
        pq.offer(new int[]{2, "c".hashCode()});
        System.out.println("Custom PQ poll: " + pq.poll()[0] + " (expected: 1)");
    }

    // ==========================================
    // 5. SLIDING WINDOW MAXIMUM (CRITICAL PATTERN!)
    // ==========================================
    // Use a Deque to maintain decreasing order of elements
    // Front of deque = max element in current window
    // O(n) — each element added & removed at most once
    
    public static int[] slidingWindowMax(int[] arr, int k) {
        if (arr == null || k == 0 || k > arr.length) return new int[0];
        
        int n = arr.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new LinkedList<>();  // stores indices
        
        int ri = 0;  // result index
        
        for (int i = 0; i < n; i++) {
            // Remove indices that are out of window (from front)
            while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
            
            // Remove smaller elements from back (maintain decreasing order)
            while (!deque.isEmpty() && arr[deque.peekLast()] < arr[i]) {
                deque.pollLast();
            }
            
            deque.offerLast(i);
            
            // Add to result when window is complete
            if (i >= k - 1) {
                result[ri++] = arr[deque.peekFirst()];
            }
        }
        return result;
    }

    // ==========================================
    // MAIN — Test everything
    // ==========================================
    
    public static void main(String[] args) {
        System.out.println("=== QUEUE BASICS ===");
        
        // Circular Queue
        System.out.println("\n--- CircularQueue ---");
        CircularQueue cq = new CircularQueue(3);
        cq.enqueue(1); cq.enqueue(2); cq.enqueue(3);
        System.out.println("Dequeue: " + cq.dequeue() + " (expected: 1)");
        cq.enqueue(4);
        System.out.println("Peek: " + cq.peek() + " (expected: 2)");
        System.out.println("Dequeue: " + cq.dequeue() + " (expected: 2)");
        System.out.println("Dequeue: " + cq.dequeue() + " (expected: 3)");
        System.out.println("Dequeue: " + cq.dequeue() + " (expected: 4)");
        
        // Queue using LinkedList
        System.out.println("\n--- ListQueue ---");
        ListQueue lq = new ListQueue();
        lq.enqueue(10); lq.enqueue(20); lq.enqueue(30);
        System.out.println("Dequeue: " + lq.dequeue() + " (expected: 10)");
        System.out.println("Size: " + lq.size() + " (expected: 2)");
        
        // ArrayDeque
        System.out.println("\n--- ArrayDeque ---");
        ArrayDeque ad = new ArrayDeque(5);
        ad.addLast(1); ad.addLast(2); ad.addFirst(0); ad.addLast(3);
        System.out.println("Remove first: " + ad.removeFirst() + " (expected: 0)");
        System.out.println("Remove last: " + ad.removeLast() + " (expected: 3)");
        
        // Priority Queue
        System.out.println("\n--- PriorityQueue ---");
        priorityQueueDemo();
        
        // Sliding Window Maximum
        System.out.println("\n--- Sliding Window Max ---");
        int[] arr = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        System.out.println("Array: " + Arrays.toString(arr));
        System.out.println("Window size: " + k);
        System.out.println("Max per window: " + Arrays.toString(slidingWindowMax(arr, k)));
        
        System.out.println("\n=== KEY QUEUE/DEQUE PATTERNS ===");
        System.out.println("1. Circular Queue → O(1) operations with wrap-around");
        System.out.println("2. List-based Queue → head for dequeue, tail for enqueue");
        System.out.println("3. Deque → O(1) add/remove from both ends");
        System.out.println("4. Sliding Window Max → Decreasing Deque (CRITICAL!)");
        System.out.println("5. Priority Queue → Min/Max Heap, always O(log n) ops");
        System.out.println("6. BFS uses Queue → level-order tree, shortest path graph");
    }
}