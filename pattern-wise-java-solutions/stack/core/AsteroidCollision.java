import java.util.Stack;

public class AsteroidCollision {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Positive asteroids move right and negative asteroids move left. When two
     * moving toward each other meet, the smaller one explodes. Equal sizes both
     * explode.
     *
     * Sample Input: asteroids = [5,10,-5]
     * Sample Output: [5,10]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Only this shape can collide:
     * a positive asteroid on the left, then a negative asteroid on the right.
     *
     * Two positive asteroids move together to the right, two negative asteroids
     * move together to the left, and a negative followed by a positive moves
     * away from each other.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Repeatedly scan for the first neighboring collision and remove the
     * destroyed asteroid or asteroids. This matches how a beginner might act it
     * out: fix one crash, then look again because that crash may reveal a new
     * neighboring crash.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Copy asteroids to a list.
     * 2. Scan for adjacent pair where left > 0 and right < 0.
     * 3. Remove smaller asteroid, or both if equal.
     * 4. Restart scanning until no collision is found.
     *
     * BRUTE FORCE DRY RUN
     *
     * [5,10,-5]
     * 10 and -5 collide, -5 explodes -> [5,10]
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */
    public int[] bruteForce(int[] asteroids) {
        java.util.List<Integer> alive = new java.util.ArrayList<>();

        for (int asteroid : asteroids) {
            alive.add(asteroid);
        }

        boolean changed = true;
        while (changed) {
            changed = false;

            for (int i = 0; i < alive.size() - 1; i++) {
                int left = alive.get(i);
                int right = alive.get(i + 1);

                if (left > 0 && right < 0) {
                    if (Math.abs(left) > Math.abs(right)) {
                        alive.remove(i + 1);
                    } else if (Math.abs(left) < Math.abs(right)) {
                        alive.remove(i);
                    } else {
                        alive.remove(i + 1);
                        alive.remove(i);
                    }

                    changed = true;
                    break;
                }
            }
        }

        return toArray(alive);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Brute force keeps rescanning after each collision. A stack avoids that by
     * keeping only asteroids that have survived on the left.
     *
     * When a new asteroid moves right, it cannot hit anything already on its
     * left. When it moves left, it may crash into right-moving survivors on the
     * stack top, one by one. That is the hard stack moment: the current asteroid
     * may pop several smaller right-moving asteroids before it either explodes
     * or finally survives.
     *
     * OPTIMIZED ALGORITHM
     * 1. Keep survivors in a stack.
     * 2. For each asteroid, resolve collisions while stack top moves right and current moves left.
     * 3. Pop smaller right-moving asteroids.
     * 4. Skip current if it explodes.
     * 5. Push current if it survives.
     *
     * OPTIMIZED DRY RUN
     *
     * [10,2,-5]
     * push 10, push 2
     * -5 meets stack top 2: |-5| is bigger, so pop 2.
     * -5 now meets stack top 10: 10 is bigger, so -5 explodes.
     * remaining stack is [10]
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public int[] optimized(int[] asteroids) {
        Stack<Integer> stack = new Stack<>();

        for (int asteroid : asteroids) {
            boolean alive = true;

            while (alive && asteroid < 0 && !stack.isEmpty() && stack.peek() > 0) {
                int top = stack.peek();

                if (top < -asteroid) {
                    // Current left-moving asteroid is bigger, so the right-moving top explodes.
                    stack.pop();
                } else if (top == -asteroid) {
                    // Equal sizes destroy both asteroids.
                    stack.pop();
                    alive = false;
                } else {
                    // Stack top is bigger, so current asteroid explodes.
                    alive = false;
                }
            }

            if (alive) {
                // It survived all possible collisions with asteroids to its left.
                stack.push(asteroid);
            }
        }

        return toArray(stack);
    }

    private int[] toArray(java.util.List<Integer> values) {
        int[] answer = new int[values.size()];

        for (int i = 0; i < values.size(); i++) {
            answer[i] = values.get(i);
        }

        return answer;
    }
}
