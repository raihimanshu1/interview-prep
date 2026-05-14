public class SqrtX {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return the integer square root of x. That means the largest whole number
     * whose square is less than or equal to x.
     *
     * Sample Input: x = 8
     * Sample Output: 2
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * We want a number a where a * a is close to x but does not cross it.
     * For x = 8, 2 * 2 is 4 and 3 * 3 is 9, so the answer is 2.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Try 0, 1, 2, 3, and so on until the square becomes too large. The last number whose square
     * does not cross x is the integer square root.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Start answer at 0.
     * 2. Keep increasing number while number * number <= x.
     * 3. Return the last valid number.
     *
     * Time Complexity: O(sqrt(x))
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * x = 8
     * 1 * 1 <= 8, answer = 1
     * 2 * 2 <= 8, answer = 2
     * 3 * 3 > 8, stop and return 2
     */
    public int bruteForce(int x) {
        int answer = 0;

        while ((long) (answer + 1) * (answer + 1) <= x) {
            answer++;
        }

        return answer;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * The brute force waste is testing every possible root one at a time. The answer is between
     * 0 and x, and squares grow as the number grows. If mid * mid is too big, every larger number
     * is also too big. That monotonic clue is exactly what binary search needs.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Binary search from 0 to x.
     * 2. If mid * mid <= x, save mid and search bigger.
     * 3. If mid * mid > x, search smaller.
     * 4. Return the last saved valid mid.
     *
     * Time Complexity: O(log x)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * x = 8
     * mid = 4 is too big, search left.
     * mid = 1 is valid, search right.
     * mid = 2 is valid, return 2.
     */
    public int optimized(int x) {
        int left = 0;
        int right = x;
        int answer = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            long square = (long) mid * mid;

            if (square <= x) {
                // mid is a valid root candidate; try right to see if a larger valid root exists.
                answer = mid;
                left = mid + 1;
            } else {
                // mid is too large, and every number after mid is too large too.
                right = mid - 1;
            }
        }

        return answer;
    }
}
