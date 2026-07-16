/*
package com.lldtop16.lrucache;

import java.util.HashMap;
import java.util.Map;

*/
/*
============================================================
                    LRU CACHE
            Least Recently Used Cache
============================================================

PROBLEM:
Cache has limited capacity.
Example:
Capacity = 3
put(1,100)  -> Cache: 1
put(2,200)  -> Cache: 1 -> 2
put(3,300)  -> Cache: 1 -> 2 -> 3
put(4,400)  -> Capacity full, remove least recently used
             If 1 was not accessed: Remove 1
             Final: 2 -> 3 -> 4

WHY HASHMAP + DOUBLY LINKED LIST?
We need two operations:
1. Find key quickly: HashMap gives O(1) lookup
2. Maintain usage order: Doubly linked list allows O(1) reordering

TIME COMPLEXITY:
get(): O(1)
put(): O(1)

DATA STRUCTURE:
1. HashMap<Integer, Node>: key -> Node mapping
2. Doubly Linked List: maintains access order
   - Head: Most Recently Used
   - Tail: Least Recently Used
*//*


public class LRUCache {
    
    static class Node {
        int key;
        int value;
        Node prev;
        Node next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    static class Cache {
        private final int capacity;
        private final Map<Integer, Node> map;
        private final Node head;
        private final Node tail;
        
        Cache(int capacity) {
            this.capacity = capacity;
            map = new HashMap<>();
            
            // Dummy nodes
            head = new Node(0, 0);
            tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
        }
        
        */
/*
        GET OPERATION
        1. Check if key exists in map
        2. If yes: Move node to front (most recent)
        3. Return value
        *//*

        public int get(int key) {
            if (!map.containsKey(key)) {
                return -1;
            }
            Node node = map.get(key);
            remove(node);
            insertAtHead(node);
            return node.value;
        }
        
        */
/*
        PUT OPERATION
        Case 1: Key already exists
          - Update value
          - Move to front
        Case 2: New key
          - Add node to front
          - If capacity exceeded, remove tail (least recent)
        *//*

        public void put(int key, int value) {
            if (map.containsKey(key)) {
                Node node = map.get(key);
                node.value = value;
                remove(node);
                insertAtHead(node);
                return;
            }
            
            Node node = new Node(key, value);
            map.put(key, node);
            insertAtHead(node);
            
            if (map.size() > capacity) {
                Node lru = tail.prev;
                remove(lru);
                map.remove(lru.key);
            }
        }
        
        private void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        
        private void insertAtHead(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }
        
        public void print() {
            Node current = head.next;
            while (current != tail) {
                System.out.print(current.key + "=" + current.value + " ");
                current = current.next;
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        Cache cache = new Cache(3);
        
        System.out.println("Adding: 1->100, 2->200, 3->300");
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);
        cache.print(); // Expected: 3=300 2=200 1=100
        
        System.out.println("Accessing key 1 (moves to front)");
        cache.get(1);
        cache.print(); // Expected: 1=100 3=300 2=200
        
        System.out.println("Adding 4->400 (removes least recent: 2)");
        cache.put(4, 400);
        cache.print(); // Expected: 4=400 1=100 3=300
        
        System.out.println("\nLRU Cache demonstration complete!");
    }
}*/
