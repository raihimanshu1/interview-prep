package com.patternwisejavasolutions.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeBasedKeyValueStore {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: set("foo","bar",1), get("foo",3)
     * Sample Output: "bar"
     *
     * set(key, value, timestamp) stores a value at a time.
     * get(key, timestamp) returns the latest value with time <= timestamp.
     *
     * Example:
     * set("foo", "bar", 1)
     * get("foo", 1) -> "bar"
     * get("foo", 3) -> "bar"
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A key can have many values because the value changes over time.
     * For a get request, we are not looking for the exact timestamp only.
     * We need the newest saved timestamp that is still not after the requested time.
     * That naturally leads from scanning records to binary search over sorted times.
     * Each key maps to its own timeline of records, just like a history log for
     * that one key.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Store all records for each key in a list.
     * For get, scan the whole list and keep the best timestamp that is not greater than requested time.
     */
    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Store each key's records in a list.
     * 2. For get, scan all records for that key.
     * 3. Keep the record with largest timestamp <= requested timestamp.
     * Time Complexity: set O(1), get O(number of records for key)
     * Space Complexity: O(total records)
     */
    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     */
    public static class BruteForceTimeMap {
        private Map<String, List<Record>> map = new HashMap<>();

        public void set(String key, String value, int timestamp) {
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(new Record(value, timestamp));
        }

        public String get(String key, int timestamp) {
            if (!map.containsKey(key)) {
                return "";
            }

            String answer = "";
            int bestTime = -1;

            for (Record record : map.get(key)) {
                if (record.timestamp <= timestamp && record.timestamp > bestTime) {
                    bestTime = record.timestamp;
                    answer = record.value;
                }
            }

            return answer;
        }
    }

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * Records for foo: (bar,1), (bar2,4)
     * get(foo,3) cannot use time 4.
     * Best time <= 3 is 1, so return bar.
     */

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Timestamps for each key are stored in increasing order.
     * So for get, we need the rightmost timestamp <= requested timestamp.
     * That is a binary search pattern.
     */
    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Records for foo: time 1, time 4, time 8.
     * get(foo, 5) needs the rightmost time <= 5.
     * Binary search lands on time 4.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Store records for each key in timestamp order.
     * 2. For get, binary search the rightmost timestamp <= requested time.
     * 3. Return that value, or empty string if none exists.
     * Time Complexity: set O(1), get O(log records)
     * Space Complexity: O(total records)
     */
    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     */
    public static class OptimizedTimeMap {
        private Map<String, List<Record>> map = new HashMap<>();

        public void set(String key, String value, int timestamp) {
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(new Record(value, timestamp));
        }

        public String get(String key, int timestamp) {
            if (!map.containsKey(key)) {
                return "";
            }

            List<Record> records = map.get(key);
            int left = 0;
            int right = records.size() - 1;
            String answer = "";

            while (left <= right) {
                int mid = left + (right - left) / 2;
                Record record = records.get(mid);

                if (record.timestamp <= timestamp) {
                    // This record is valid, but there may be a newer valid one to the right.
                    answer = record.value;
                    left = mid + 1;
                } else {
                    // This record is too new, so search earlier timestamps.
                    right = mid - 1;
                }
            }

            return answer;
        }
    }

    private static class Record {
        private String value;
        private int timestamp;

        private Record(String value, int timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
