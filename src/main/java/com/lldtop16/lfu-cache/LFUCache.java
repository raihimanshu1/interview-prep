

package com.lldtop16.lfuCache;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/*
============================================================
                    LFU CACHE
            Least Frequently Used Cache
============================================================

DIFFERENCE FROM LRU:
LRU: Removes based on TIME (least recently used)
LFU: Removes based on FREQUENCY (least frequently used)

Example:
A accessed 10 times
B accessed 2 times
If cache full: Remove B (less frequently used)

PROBLEM:
Capacity = 3
put(A): frequency: A=1
get(A): frequency: A=2
put(B): frequency: B=1
When cache full: Remove key with smallest frequency

DATA STRUCTURE:
1. HashMap<Integer, Node>: key -> Node
2. Frequency Map: frequency -> LinkedHashSet<Node>
   Why LinkedHashSet? Maintains insertion order for tie-breaking

TIME COMPLEXITY:
get(): O(1)
put(): O(1)
*/

public class LFUCache {
    
    static class Node {
        int key;
        int value;
        int frequency;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.frequency = 1;
        }
    }
    
    static class Cache {
        private final int capacity;
        private int minFrequency;
        private final Map<Integer, Node> nodes;
        private final Map<Integer, LinkedHashSet<Node>> frequencyMap;
        
        Cache(int capacity) {
            this.capacity = capacity;
            this.minFrequency = 0;
            this.nodes = new HashMap<>();
            this.frequencyMap = new HashMap<>();
        }
        
        /*
        GET OPERATION
        1. Check if key exists
        2. If yes: Increase frequency
        3. Return value
        */
        public int get(int key) {
            if (!nodes.containsKey(key)) {
                return -1;
            }
            
            Node node = nodes.get(key);
            increaseFrequency(node);
            return node.value;
        }
        
        /*
        PUT OPERATION
        1. If key exists: Update value and increase frequency
        2. If new key:
           - If capacity full: Remove LFU (lowest frequency, oldest if tie)
           - Add new node with frequency 1
        3. Update minFrequency
        */
        public void put(int key, int value) {
            if (capacity == 0) {
                return;
            }
            
            if (nodes.containsKey(key)) {
                Node node = nodes.get(key);
                node.value = value;
                increaseFrequency(node);
                return;
            }
            
            if (nodes.size() == capacity) {
                // Remove least frequently used
                LinkedHashSet<Node> set = frequencyMap.get(minFrequency);
                Node remove = set.iterator().next(); // Oldest in this frequency
                set.remove(remove);
                nodes.remove(remove.key);
                
                if (set.isEmpty()) {
                    frequencyMap.remove(minFrequency);
                }
            }
            
            // Add new node
            Node node = new Node(key, value);
            nodes.put(key, node);
            frequencyMap.computeIfAbsent(1, x -> new LinkedHashSet<>()).add(node);
            minFrequency = 1;
        }
        
        /*
        Increase frequency of a node
        1. Remove from old frequency set
        2. Add to new frequency set (frequency + 1)
        3. Update minFrequency if needed
        */
        private void increaseFrequency(Node node) {
            int oldFrequency = node.frequency;
            frequencyMap.get(oldFrequency).remove(node);
            
            node.frequency++;
            
            frequencyMap.computeIfAbsent(node.frequency, x -> new LinkedHashSet<>()).add(node);
            
            // If old frequency set is empty and was the min, increment minFrequency
            if (oldFrequency == minFrequency && frequencyMap.get(oldFrequency).isEmpty()) {
                minFrequency++;
            }
        }
        
        public void print() {
            System.out.println("Cache contents:");
            for (Node node : nodes.values()) {
                System.out.println("  Key: " + node.key + ", Value: " + node.value + ", Frequency: " + node.frequency);
            }
        }
    }
    
    public static void main(String[] args) {
        Cache cache = new Cache(3);
        
        System.out.println("Adding: 1->100, 2->200, 3->300");
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);
        cache.print();
        
        System.out.println("\nAccessing key 1 twice (frequency increases to 3)");
        cache.get(1);
        cache.get(1);
        cache.print();
        
        System.out.println("\nAccessing key 2 once (frequency increases to 2)");
        cache.get(2);
        cache.print();
        
        System.out.println("\nAdding 4->400 (removes LFU: key 3 with frequency 1)");
        cache.put(4, 400);
        cache.print();
        
        System.out.println("\nLFU Cache demonstration complete!");
    }
}