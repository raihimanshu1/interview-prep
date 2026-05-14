import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DesignHitCounter {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: hit(1), hit(2), hit(300), getHits(300), getHits(301)
     * Sample Output: 3, 2
     *
     * Count how many hits happened in the past 5 minutes, meaning the last 300
     * seconds including the current timestamp.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Write down every hit time. When asked for a count, only count times that
     * are still close enough to the current time.
     * The queue version behaves like a rolling five-minute log: the front is the
     * oldest hit and is the first one that can expire.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Store every hit timestamp exactly as it comes. getHits scans the whole
     * list and counts valid timestamps.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. hit(timestamp): append timestamp.
     * 2. getHits(timestamp): count all times greater than timestamp - 300.
     *
     * Time Complexity: hit O(1), getHits O(n)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * hits = [1,2,300]
     * getHits(300): valid times are > 0, count 3.
     * getHits(301): valid times are > 1, count 2.
     */

    public static class BruteForce {
        private List<Integer> hits = new ArrayList<>();

        public void hit(int timestamp) {
            hits.add(timestamp);
        }

        public int getHits(int timestamp) {
            int count = 0;
            for (int time : hits) {
                if (time > timestamp - 300) {
                    count++;
                }
            }
            return count;
        }
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Old hits never become useful again. Remove expired timestamps from the
     * front of a queue whenever we receive a hit or query.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep a queue of hit timestamps.
     * 2. Before hit or getHits, remove timestamps <= current - 300.
     * 3. hit adds current timestamp.
     * 4. getHits returns queue size.
     *
     * Time Complexity: O(1) amortized per operation
     * Space Complexity: O(number of hits in last 300 seconds)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * queue [1,2,300]
     * getHits(300): none expire, size 3.
     * getHits(301): 1 expires, size 2.
     */

    public static class Optimized {
        private Queue<Integer> recentHits = new LinkedList<>();

        public void hit(int timestamp) {
            removeExpired(timestamp);
            recentHits.offer(timestamp);
        }

        public int getHits(int timestamp) {
            removeExpired(timestamp);
            return recentHits.size();
        }

        private void removeExpired(int timestamp) {
            while (!recentHits.isEmpty() && recentHits.peek() <= timestamp - 300) {
                // Once the oldest hit is outside the window, it will never count again.
                recentHits.poll();
            }
        }
    }
}
