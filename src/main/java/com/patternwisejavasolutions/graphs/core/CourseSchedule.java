
package com.patternwisejavasolutions.graphs.core;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CourseSchedule {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Input numCourses and prerequisites.
     * prerequisite [a,b] means take b before a.
     * Return true if all courses can be completed.
     *
     * Sample Input:
     * numCourses = 2, prerequisites = [[1,0]]
     *
     * Sample Output:
     * true
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If there is a cycle, we are stuck.
     * Example: 0 needs 1 and 1 needs 0.
     * The arrows have direction: prerequisite -> course unlocked by that prerequisite.
     * So this is cycle detection in a directed graph, not just normal connectedness.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Try following prerequisite chains from each course.
     * If we come back to a course still in the current chain, those courses depend on each other forever.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Courses: 0 <- 1 and 1 <- 0.
     * Start DFS at 0, go to 1, then back to 0 while 0 is still in current path.
     * Cycle found -> false.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Build graph.
     * 2. Run DFS from every course.
     * 3. Track current recursion path.
     * 4. If we revisit a node in current path, cycle exists.
     * Time Complexity: O(V+E)
     * Space Complexity: O(V+E)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public boolean bruteForce(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = buildGraph(numCourses, prerequisites);
        int[] state = new int[numCourses];

        for (int course = 0; course < numCourses; course++) {
            if (hasCycle(course, graph, state)) {
                return false;
            }
        }

        return true;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * DFS detects the bad loop directly.
     * Topological sort solves the scheduling story directly: courses with indegree 0 have no
     * remaining prerequisites, so we can take them now and reduce indegrees of dependent courses.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Course 0 has no prerequisite, take it.
     * This may unlock course 1.
     * If every course gets unlocked, no cycle exists.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build graph and indegree array.
     * 2. Queue all courses with indegree 0.
     * 3. Process queue and reduce neighbor indegrees.
     * 4. If processed count equals numCourses, return true.
     * Time Complexity: O(V+E)
     * Space Complexity: O(V+E)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public boolean optimized(int numCourses, int[][] prerequisites) {
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

        int completed = 0;

        while (!queue.isEmpty()) {
            int course = queue.poll();
            completed++;

            for (int next : graph.get(course)) {
                indegree[next]--; // One prerequisite for next has just been completed.

                if (indegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }

        return completed == numCourses;
    }


    private List<List<Integer>> buildGraph(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : prerequisites) {
            int course = edge[0];
            int prerequisite = edge[1];
            graph.get(prerequisite).add(course);
        }

        return graph;
    }

    private boolean hasCycle(int course, List<List<Integer>> graph, int[] state) {
        if (state[course] == 1) {
            return true;
        }

        if (state[course] == 2) {
            return false;
        }

        state[course] = 1; // 1 means this course is in the current DFS chain.

        for (int next : graph.get(course)) {
            if (hasCycle(next, graph, state)) {
                return true;
            }
        }

        state[course] = 2; // 2 means this course and everything after it is safe.
        return false;
    }
}
