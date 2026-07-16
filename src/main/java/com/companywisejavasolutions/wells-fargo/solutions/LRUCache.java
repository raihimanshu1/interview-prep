package com.companywisejavasolutions.wellsfargo.solutions;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: capacity = 2, put(1,1), put(2,2), get(1), put(3,3), get(2)
     * Sample Output: get(1) = 1, get(2) = -1
     *
     * Build a cache with fixed capacity.
     * get(key) returns value if key exists, otherwise -1.
     * put(key, value) inserts or updates.
     * If cache is full, remove the least recently used key.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Imagine a small notebook that can hold only a few entries.
     * Whenever a key is read or written, it becomes the newest entry.
     * When the notebook is full, we erase the entry that has been ignored the longest.
     * The design pattern is "fast lookup + recency order".
     * The HashMap is the notebook index by key; the linked list is the recency
     * line with newest near head and oldest near tail.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Store key-value pairs in a map.
     * Also store a time counter for each key.
     * Every get or put makes that key recently used.
     * When full, scan all keys to find the smallest time.
     */
    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Store key-value pairs in one map.
     * 2. Store last used time in another map.
     * 3. On get, update that key's time.
     * 4. On put when full, scan all keys to remove the oldest time.
     * Time Complexity: get O(1), put O(capacity)
     * Space Complexity: O(capacity)
     */
    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     */
    public static class BruteForceLRUCache {
        private int capacity;
        private int time;
        private Map<Integer, Integer> values = new HashMap<>();
        private Map<Integer, Integer> lastUsedTime = new HashMap<>();

        public BruteForceLRUCache(int capacity) {
            this.capacity = capacity;
        }

        public int get(int key) {
            if (!values.containsKey(key)) {
                return -1;
            }

            time++;
            lastUsedTime.put(key, time);
            return values.get(key);
        }

        public void put(int key, int value) {
            time++;

            if (!values.containsKey(key) && values.size() == capacity) {
                int leastRecentKey = findLeastRecentKey();
                values.remove(leastRecentKey);
                lastUsedTime.remove(leastRecentKey);
            }

            values.put(key, value);
            lastUsedTime.put(key, time);
        }

        private int findLeastRecentKey() {
            int leastRecentKey = -1;
            int oldestTime = Integer.MAX_VALUE;

            for (int key : lastUsedTime.keySet()) {
                if (lastUsedTime.get(key) < oldestTime) {
                    oldestTime = lastUsedTime.get(key);
                    leastRecentKey = key;
                }
            }

            return leastRecentKey;
        }
    }

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * capacity = 2
     * put(1,1), put(2,2)
     * get(1) makes key 1 recent.
     * put(3,3) removes key 2 because key 2 is least recent.
     */

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * We need fast get by key and fast removal of least recent.
     * HashMap gives fast key lookup.
     * Doubly linked list gives fast move-to-front and remove-from-tail.
     */
    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Most recent keys stay near head.
     * Least recent key stays near tail.
     * get(1) moves key 1 back to the head.
     * If capacity is crossed, remove tail.prev.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. HashMap stores key -> linked list node.
     * 2. Doubly linked list stores recency order.
     * 3. On get, move node to front.
     * 4. On put, insert/update at front.
     * 5. If too large, remove the tail node.
     * Time Complexity: O(1) for get and put
     * Space Complexity: O(capacity)
     */
    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     */
    public static class OptimizedLRUCache {
        private int capacity;
        private Map<Integer, Node> map = new HashMap<>();
        private Node head = new Node(0, 0);
        private Node tail = new Node(0, 0);

        public OptimizedLRUCache(int capacity) {
            this.capacity = capacity;
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            if (!map.containsKey(key)) {
                return -1;
            }

            Node node = map.get(key);
            // Reading a key counts as using it, so move it to most recent.
            remove(node);
            addAfterHead(node);
            return node.value;
        }

        public void put(int key, int value) {
            if (map.containsKey(key)) {
                remove(map.get(key));
            }

            Node node = new Node(key, value);
            map.put(key, node);
            addAfterHead(node);

            if (map.size() > capacity) {
                // tail.prev is the least recently used real node.
                Node leastRecent = tail.prev;
                remove(leastRecent);
                map.remove(leastRecent.key);
            }
        }

        private void addAfterHead(Node node) {
            // Insert directly after the head sentinel to mark most recent use.
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        private void remove(Node node) {
            // Bypass node in O(1) because each node knows both neighbors.
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private static class Node {
        private int key;
        private int value;
        private Node prev;
        private Node next;

        private Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
