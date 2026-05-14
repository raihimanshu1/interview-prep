import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CourseScheduleII {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Return an order to take all courses. prerequisite [a,b] means take b before a. If impossible,
     * return an empty array.
     *
     * Sample Input: numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
     * Sample Output: [0,1,2,3]
     *
     * SCHOOL-LEVEL INTUITION
     * Take courses that have no remaining prerequisites first. Each completed course may unlock
     * more courses.
     * The answer is not just yes/no here; we must record the order we actually take them.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Repeatedly scan all courses and take any course whose prerequisites are already completed.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Track completed courses.
     * 2. Keep scanning all untaken courses.
     * 3. If every prerequisite for a course is complete, add that course to order.
     * 4. If a full scan takes nothing, a cycle blocks us.
     *
     * BRUTE FORCE DRY RUN
     * prerequisites [[1,0],[2,0],[3,1],[3,2]]
     * take 0, then 1 and 2, then 3 -> [0,1,2,3]
     *
     * Time Complexity: O(V * (V + E))
     * Space Complexity: O(V + E)
     */
    public int[] bruteForce(int numCourses, int[][] prerequisites) {
        List<List<Integer>> requiredByCourse = buildPrerequisiteList(numCourses, prerequisites);
        boolean[] completed = new boolean[numCourses];
        int[] order = new int[numCourses];
        int takenCount = 0;

        while (takenCount < numCourses) {
            boolean tookSomething = false;

            for (int course = 0; course < numCourses; course++) {
                if (!completed[course] && prerequisitesDone(requiredByCourse.get(course), completed)) {
                    completed[course] = true;
                    order[takenCount++] = course;
                    tookSomething = true;
                }
            }

            if (!tookSomething) {
                return new int[0];
            }
        }

        return order;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain is repeatedly scanning every course to find one that is ready.
     * Topological sort keeps a queue of ready courses, where ready means indegree 0.
     * When a course is completed, only its direct dependents need their indegree reduced.
     *
     * OPTIMIZED ALGORITHM
     * 1. Build graph prerequisite -> next course.
     * 2. Count indegree, meaning remaining prerequisites, for each course.
     * 3. Queue all courses with indegree 0.
     * 4. Pop a course, add it to order, and reduce indegree of unlocked courses.
     * 5. If all courses are added, return order; otherwise return empty array.
     *
     * OPTIMIZED DRY RUN
     * 0 unlocks 1 and 2.
     * After both 1 and 2 are taken, 3 has indegree 0 and can be taken.
     *
     * Time Complexity: O(V + E)
     * Space Complexity: O(V + E)
     */
    public int[] optimized(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = buildGraph(numCourses, prerequisites);
        int[] indegree = new int[numCourses];

        for (int[] edge : prerequisites) {
            indegree[edge[0]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int course = 0; course < numCourses; course++) {
            if (indegree[course] == 0) {
                queue.offer(course);
            }
        }

        int[] order = new int[numCourses];
        int index = 0;

        while (!queue.isEmpty()) {
            int course = queue.poll();
            order[index++] = course;

            for (int next : graph.get(course)) {
                indegree[next]--; // Completing course removes one remaining prerequisite from next.

                if (indegree[next] == 0) {
                    // All prerequisites for next are done, so it can enter the order.
                    queue.offer(next);
                }
            }
        }

        return index == numCourses ? order : new int[0];
    }

    private List<List<Integer>> buildPrerequisiteList(int numCourses, int[][] prerequisites) {
        List<List<Integer>> requiredByCourse = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            requiredByCourse.add(new ArrayList<>());
        }

        for (int[] edge : prerequisites) {
            requiredByCourse.get(edge[0]).add(edge[1]);
        }

        return requiredByCourse;
    }

    private List<List<Integer>> buildGraph(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : prerequisites) {
            graph.get(edge[1]).add(edge[0]);
        }

        return graph;
    }

    private boolean prerequisitesDone(List<Integer> prerequisites, boolean[] completed) {
        for (int prerequisite : prerequisites) {
            if (!completed[prerequisite]) {
                return false;
            }
        }

        return true;
    }
}
