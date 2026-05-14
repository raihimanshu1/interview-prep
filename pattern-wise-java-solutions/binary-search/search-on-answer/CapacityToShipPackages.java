public class CapacityToShipPackages {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Packages must be shipped in the same order as given. Choose the smallest ship capacity
     * so all packages can be shipped within the given number of days.
     *
     * Sample Input:
     * weights = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10], days = 5
     *
     * Sample Output:
     * 15
     *
     * SCHOOL-LEVEL INTUITION:
     * A ship capacity is like a school bag limit. If the limit is too small, you need too many trips.
     * If the limit is large enough, you can carry the packages in fewer days. We are looking for the
     * smallest limit that still works.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Try every possible capacity from the heaviest single package up to the total weight.
     * The first capacity that can finish within days is the answer.
     *
     * BRUTE FORCE ALGORITHM
     * 1. The minimum possible capacity is max(weights), because one package cannot be split.
     * 2. The maximum possible capacity is sum(weights), shipping everything in one day.
     * 3. For every capacity in that range, count how many days it needs.
     * 4. Return the first capacity whose needed days <= days.
     *
     * BRUTE FORCE DRY RUN
     * weights = [3, 2, 2, 4, 1, 4], days = 3
     * capacity 4 needs 5 days -> too small
     * capacity 5 needs 4 days -> too small
     * capacity 6 needs 3 days -> works, return 6
     *
     * Time: O(n * (sum - max)), Space: O(1)
     */
    public int bruteForce(int[] weights, int days) {
        int low = 0;
        int high = 0;

        for (int weight : weights) {
            low = Math.max(low, weight);
            high += weight;
        }

        for (int capacity = low; capacity <= high; capacity++) {
            if (daysNeeded(weights, capacity) <= days) {
                return capacity;
            }
        }

        return high;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is trying capacities one by one. Notice the yes/no pattern:
     * if capacity 15 works, any capacity larger than 15 also works. If capacity 10 fails, any
     * capacity smaller than 10 also fails. That sorted answer space lets us use binary search.
     *
     * Pattern used: Binary Search on Answer.
     *
     * OPTIMIZED ALGORITHM
     * 1. Set low = max weight and high = total weight.
     * 2. Pick mid capacity.
     * 3. If mid works within days, save it and try smaller capacities.
     * 4. If mid does not work, try larger capacities.
     * 5. Return the smallest working capacity.
     *
     * OPTIMIZED DRY RUN
     * weights = [3, 2, 2, 4, 1, 4], days = 3
     * low = 4, high = 16, mid = 10 -> works, answer = 10, try smaller
     * mid = 6 -> works, answer = 6, try smaller
     * mid = 4 -> fails, need larger
     * mid = 5 -> fails, need larger
     * return 6
     *
     * Time: O(n log(sum - max)), Space: O(1)
     */
    public int optimized(int[] weights, int days) {
        int low = 0;
        int high = 0;

        for (int weight : weights) {
            low = Math.max(low, weight);
            high += weight;
        }

        int answer = high;

        while (low <= high) {
            int capacity = low + (high - low) / 2;
            int needed = daysNeeded(weights, capacity);

            if (needed <= days) {
                // This capacity works, so we keep it and search left for a smaller working answer.
                answer = capacity;
                high = capacity - 1;
            } else {
                // Too many days means the ship is too small.
                low = capacity + 1;
            }
        }

        return answer;
    }

    private int daysNeeded(int[] weights, int capacity) {
        int days = 1;
        int currentLoad = 0;

        for (int weight : weights) {
            if (currentLoad + weight > capacity) {
                // Starting a new day keeps the original package order while respecting capacity.
                days++;
                currentLoad = 0;
            }

            currentLoad += weight;
        }

        return days;
    }
}

