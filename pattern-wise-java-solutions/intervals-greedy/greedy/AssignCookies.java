import java.util.Arrays;

public class AssignCookies {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: greed = [1,2,3], cookies = [1,1]
     * Sample Output: 1
     *
     * Input children greed = [1,2,3], cookies = [1,1]
     * Output: 1 because only one child can be satisfied.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A child is satisfied if cookie size >= greed.
     * To satisfy maximum children, give small cookies to less greedy children first.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to choose a cookie for the first child, then
     * choose a cookie for the next child, and so on. Because an early choice can
     * affect later children, brute force tries every possible matching.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * Child greed 1 can take cookie 1.
     * Child greed 2 cannot take remaining cookie 1.
     * Answer = 1.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Try assigning cookies in many possible ways.
     * 2. Count maximum satisfied children.
     * Time Complexity: exponential
     * Space Complexity: O(children)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * The used array marks which cookies are already given away in the current
     * assignment path.
     */
    public int bruteForce(int[] greed, int[] cookies) {
        boolean[] used = new boolean[cookies.length];
        return assignSlow(greed, cookies, used, 0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Sort both arrays.
     * Use the smallest cookie that can satisfy the least greedy remaining child.
     * This avoids wasting big cookies too early.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * greed [1,2,3], cookies [1,1]
     * cookie 1 satisfies child 1.
     * next cookie 1 cannot satisfy child 2.
     * Answer 1.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Sort greed and cookies.
     * 2. Use two pointers.
     * 3. If cookie satisfies child, move both.
     * 4. Else try next bigger cookie.
     * Time Complexity: O(n log n + m log m)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * Sorting lets us compare the least greedy remaining child with the smallest
     * remaining cookie, which is the safest local choice.
     */
    public int optimized(int[] greed, int[] cookies) {
        // Least greedy children first; smallest cookies first.
        Arrays.sort(greed);
        Arrays.sort(cookies);
        int child = 0;
        int cookie = 0;

        while (child < greed.length && cookie < cookies.length) {
            if (cookies[cookie] >= greed[child]) {
                // This smallest available cookie works, so save bigger cookies.
                child++;
            }

            // If it did not work, it is too small for every remaining child.
            cookie++;
        }

        return child;
    }


    private int assignSlow(int[] greed, int[] cookies, boolean[] used, int child) {
        if (child == greed.length) {
            return 0;
        }

        int best = assignSlow(greed, cookies, used, child + 1);

        for (int i = 0; i < cookies.length; i++) {
            if (!used[i] && cookies[i] >= greed[child]) {
                used[i] = true;
                best = Math.max(best, 1 + assignSlow(greed, cookies, used, child + 1));
                used[i] = false;
            }
        }

        return best;
    }
}
