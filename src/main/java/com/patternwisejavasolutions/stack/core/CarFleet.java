
package com.patternwisejavasolutions.stack.core;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarFleet {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Cars drive toward a target. A faster car catches a slower car ahead
     * and then they travel together as one fleet.
     *
     * Sample Input:
     * target = 12
     * position = [10, 8, 0, 5, 3]
     * speed = [2, 4, 1, 1, 3]
     *
     * Sample Output:
     * 3
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A car's arrival time is:
     * (target - position) / speed
     *
     * If a car behind reaches earlier than the car ahead,
     * it catches the car ahead and becomes part of that fleet.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Sort cars by position and repeatedly merge cars that catch the fleet ahead.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * target = 12
     * car at 10 speed 2 reaches in 1 hour
     * car at 8 speed 4 reaches in 1 hour
     * They meet at target, so one fleet.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pair every car's position with its speed.
     * 2. Sort cars by position.
     * 3. Store each car's arrival time in the same order.
     * 4. Scan from right to left and remove cars that merge into the fleet ahead.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int target, int[] position, int[] speed) {
        int n = position.length;
        int[][] cars = new int[n][2];

        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = speed[i];
        }

        Arrays.sort(cars, (a, b) -> Integer.compare(a[0], b[0]));

        List<Double> fleetTimes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double time = (double) (target - cars[i][0]) / cars[i][1];
            fleetTimes.add(time);
        }

        for (int i = fleetTimes.size() - 2; i >= 0; i--) {
            double currentCarTime = fleetTimes.get(i);
            double fleetAheadTime = fleetTimes.get(i + 1);

            if (currentCarTime <= fleetAheadTime) {
                fleetTimes.remove(i);
            }
        }

        return fleetTimes.size();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Look from the car closest to target moving backward.
     * Keep the slowest arrival time seen so far.
     *
     * If the current car takes longer, it cannot catch the fleet ahead,
     * so it starts a new fleet.
     * If it takes less or equal time, it catches the fleet ahead.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * After sorting by position:
     * (0,1), (3,3), (5,1), (8,4), (10,2)
     *
     * From right:
     * position 10 time 1 -> fleet 1
     * position 8 time 1 -> catches fleet 1
     * position 5 time 7 -> new fleet
     * position 3 time 3 -> catches time 7 fleet
     * position 0 time 12 -> new fleet
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Sort cars by starting position.
     * 2. Walk from closest to target toward farthest.
     * 3. Calculate each car's time to reach target.
     * 4. If time is greater than fleet ahead, start new fleet.
     * 5. Otherwise, it catches the fleet ahead.
     *
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A list-reduction version can physically remove cars that catch the fleet
     * ahead. It is useful for learning the merge idea, while the optimized
     * version keeps only the current fleet time instead of changing the list.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int target, int[] position, int[] speed) {
        int n = position.length;
        int[][] cars = new int[n][2];

        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = speed[i];
        }

        Arrays.sort(cars, (a, b) -> Integer.compare(a[0], b[0]));

        int fleets = 0;
        double slowestFleetTimeAhead = 0.0;

        for (int i = n - 1; i >= 0; i--) {
            double time = (double) (target - cars[i][0]) / cars[i][1];

            if (time > slowestFleetTimeAhead) {
                // This car cannot catch the fleet ahead, so it becomes a new fleet.
                fleets++;
                slowestFleetTimeAhead = time;
            }
        }

        return fleets;
    }
}
