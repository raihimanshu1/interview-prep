package com.companywisejavasolutions.wellsfargo.solutions;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUCache {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Design a cache with get and put.
     * If the cache is full, evict the least frequently used key.
     * If multiple keys have the same frequency, evict the least recently used
     * among those keys.
     *
     * EXAMPLE
     * capacity = 2
     * put(1, 10), put(2, 20), get(1), put(3, 30)
     *
     * Key 1 frequency becomes 2.
     * Key 2 frequency stays 1.
     * put(3, 30) evicts key 2.
     *
     * WHAT IT MEANS
     * Frequency decides first. Recency breaks ties.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Frequency eviction:
     * put 1, put 2, get 1, put 3 -> evict 2.
     *
     * Example 2 - Recency tie:
     * put 1, put 2, put 3 with capacity 2 -> evict 1 because both 1 and 2
     * have frequency 1, but 1 is older.
     *
     * Example 3 - Update existing key:
     * put 1, put 1 again should update value and increase usage.
     *
     * Edge case - capacity 0 stores nothing.
     */

    /*
     * BRUTE FORCE IMPLEMENTATION
     *
     * Store value, frequency, and last-used time. When full, scan all keys and
     * remove the key with the lowest frequency; if tied, remove the oldest use.
     *
     * Time Complexity: get O(1), put O(capacity) when eviction is needed.
     * Space Complexity: O(capacity)
     */
    public static class BruteForceLFUCache {
        private final int capacity;
        private int time;
        private final Map<Integer, Integer> values = new HashMap<>();
        private final Map<Integer, Integer> frequency = new HashMap<>();
        private final Map<Integer, Integer> lastUsed = new HashMap<>();

        public BruteForceLFUCache(int capacity) {
            this.capacity = capacity;
        }

        public int get(int key) {
            if (!values.containsKey(key)) {
                return -1;
            }

            touch(key);
            return values.get(key);
        }

        public void put(int key, int value) {
            if (capacity == 0) {
                return;
            }

            if (!values.containsKey(key) && values.size() == capacity) {
                int victim = findEvictionKey();
                values.remove(victim);
                frequency.remove(victim);
                lastUsed.remove(victim);
            }

            values.put(key, value);
            touch(key);
        }

        private void touch(int key) {
            time++;
            frequency.put(key, frequency.getOrDefault(key, 0) + 1);
            lastUsed.put(key, time);
        }

        private int findEvictionKey() {
            int victim = -1;
            int bestFrequency = Integer.MAX_VALUE;
            int oldestTime = Integer.MAX_VALUE;

            for (int key : values.keySet()) {
                int keyFrequency = frequency.get(key);
                int keyTime = lastUsed.get(key);

                if (keyFrequency < bestFrequency
                        || (keyFrequency == bestFrequency && keyTime < oldestTime)) {
                    victim = key;
                    bestFrequency = keyFrequency;
                    oldestTime = keyTime;
                }
            }

            return victim;
        }
    }

    /*
     * OPTIMIZED IMPLEMENTATION
     *
     * keyToValue gives direct lookup.
     * keyToFrequency tells which bucket a key currently belongs to.
     * frequencyToKeys keeps recency order inside each frequency bucket.
     *
     * Time Complexity: O(1) average for get and put.
     * Space Complexity: O(capacity)
     */
    public static class OptimizedLFUCache {
        private final int capacity;
        private int minFrequency;
        private final Map<Integer, Integer> keyToValue = new HashMap<>();
        private final Map<Integer, Integer> keyToFrequency = new HashMap<>();
        private final Map<Integer, LinkedHashSet<Integer>> frequencyToKeys = new HashMap<>();

        public OptimizedLFUCache(int capacity) {
            this.capacity = capacity;
        }

        public int get(int key) {
            if (!keyToValue.containsKey(key)) {
                return -1;
            }

            increaseFrequency(key);
            return keyToValue.get(key);
        }

        public void put(int key, int value) {
            if (capacity == 0) {
                return;
            }

            if (keyToValue.containsKey(key)) {
                keyToValue.put(key, value);
                increaseFrequency(key);
                return;
            }

            if (keyToValue.size() == capacity) {
                LinkedHashSet<Integer> leastFrequencyKeys = frequencyToKeys.get(minFrequency);
                int victim = leastFrequencyKeys.iterator().next();
                leastFrequencyKeys.remove(victim);
                keyToValue.remove(victim);
                keyToFrequency.remove(victim);
            }

            keyToValue.put(key, value);
            keyToFrequency.put(key, 1);
            frequencyToKeys.computeIfAbsent(1, ignored -> new LinkedHashSet<>()).add(key);
            minFrequency = 1;
        }

        private void increaseFrequency(int key) {
            int oldFrequency = keyToFrequency.get(key);
            LinkedHashSet<Integer> oldBucket = frequencyToKeys.get(oldFrequency);

            // Remove from the old bucket because this access moves the key to
            // a higher-frequency bucket.
            oldBucket.remove(key);

            if (oldFrequency == minFrequency && oldBucket.isEmpty()) {
                // If we emptied the lowest-frequency bucket, the next frequency
                // is now the lowest possible bucket.
                minFrequency++;
            }

            int newFrequency = oldFrequency + 1;
            keyToFrequency.put(key, newFrequency);
            frequencyToKeys.computeIfAbsent(newFrequency, ignored -> new LinkedHashSet<>()).add(key);
        }
    }

    public static void main(String[] args) {
        OptimizedLFUCache cache = new OptimizedLFUCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        System.out.println(cache.get(1)); // 10, key 1 frequency becomes 2
        cache.put(3, 30);                 // evicts key 2
        System.out.println(cache.get(2)); // -1
        System.out.println(cache.get(3)); // 30
    }
}
