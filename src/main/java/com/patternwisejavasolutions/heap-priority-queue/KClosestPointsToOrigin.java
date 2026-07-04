
package com.patternwisejavasolutions.heapPriorityQueue;
import java.util.Arrays;
import java.util.PriorityQueue;

public class KClosestPointsToOrigin {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given points [x, y], return the k points closest to (0, 0).
     *
     * Sample Input: points = [[1, 3], [-2, 2]], k = 1
     * Sample Output: [[-2, 2]]
     *
     * SCHOOL-LEVEL INTUITION:
     * A point is close to the origin if x*x + y*y is small. We can compare squared distance and
     * avoid square roots because smaller squared distance means smaller real distance too.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Sort every point by distance and take the first k points.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Copy points.
     * 2. Sort by squared distance from origin.
     * 3. Return first k points.
     *
     * BRUTE FORCE DRY RUN
     * [1,3] distance 10, [-2,2] distance 8
     * sorted by distance -> [-2,2], [1,3]
     * k = 1 -> return [-2,2]
     *
     * Time: O(n log n), Space: O(n)
     */
    public int[][] bruteForce(int[][] points, int k) {
        int[][] copy = Arrays.copyOf(points, points.length);
        Arrays.sort(copy, (a, b) -> {
            return Long.compare(distance(a), distance(b));
        });
        return Arrays.copyOf(copy, k);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * Sorting all points is unnecessary when we only need k closest. Keep a max heap of size k.
     * The heap top is the farthest among the selected close points. If a new point is closer,
     * remove that farthest point.
     *
     * Pattern used: Fixed-size max heap.
     *
     * OPTIMIZED ALGORITHM
     * 1. Add points to a max heap ordered by squared distance.
     * 2. If heap size exceeds k, remove the farthest point.
     * 3. Return the points left in the heap.
     *
     * OPTIMIZED DRY RUN
     * points [[1,3],[-2,2]], k = 1
     * add [1,3]
     * add [-2,2], heap too big, remove farther [1,3]
     * return [-2,2]
     *
     * Time: O(n log k), Space: O(k)
     */
    public int[][] optimized(int[][] points, int k) {
        /*
         * Larger distance comes first, so the heap top is the farthest among the selected points.
         * When size grows past k, poll() removes the point that should not survive.
         */
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> {
            return Long.compare(distance(b), distance(a));
        });

        for (int[] point : points) {
            maxHeap.offer(point);

            if (maxHeap.size() > k) {
                // The farthest selected point no longer belongs in the k closest group.
                maxHeap.poll();
            }
        }

        int[][] answer = new int[k][2];
        for (int index = 0; index < k; index++) {
            answer[index] = maxHeap.poll();
        }

        return answer;
    }

    private long distance(int[] point) {
        return (long) point[0] * point[0] + (long) point[1] * point[1];
    }
}
