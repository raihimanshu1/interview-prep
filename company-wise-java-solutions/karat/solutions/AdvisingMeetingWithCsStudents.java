package karat.solutions;

import java.util.*;

public class AdvisingMeetingWithCsStudents {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given courses and prerequisite relationships, return one valid order in which a student can take all courses.
     *
     * INPUT
     * courses is the full course list. prerequisites[i] = [beforeCourse, afterCourse].
     *
     * OUTPUT
     * A valid course order, or empty list if impossible.
     *
     * EXAMPLE
     * courses = ["Intro", "DS", "Algo", "ML"]
     * prerequisites = [["Intro", "DS"], ["DS", "Algo"], ["Algo", "ML"]]
     * Output: [Intro, DS, Algo, ML]
     * 
     * The schedule must put every prerequisite before the course that depends on it.
     *
     * WHAT IT MEANS
     * This is topological sorting of a prerequisite graph.
     */


    /*
     * IN-DEPTH EXPLANATION
     *
     * Before writing code, understand the sentence hidden inside every
     * prerequisite:
     *
     * [A, B] means A must happen before B.
     *
     * So this is not really about courses only. It is about ordering tasks when
     * some tasks depend on earlier tasks.
     *
     * What you need to know before solving:
     *
     * 1. A course with no unfinished prerequisites can be taken now.
     * 2. A course with even one unfinished prerequisite must wait.
     * 3. If every remaining course is waiting for another remaining course,
     *    we are stuck.
     * 4. Being stuck means there is a cycle, so a full valid order is impossible.
     *
     * What we will do in brute force:
     *
     * Keep repeating rounds.
     * In each round, scan all courses.
     * If a course's prerequisites are already completed, add it to the answer.
     * If one full round adds nothing, return an empty list.
     */


    /*
    * Problem Statement

You are given:

A list of courses.
A list of prerequisite relationships.

A prerequisite pair:

[A, B]

means:

To take course B, you must complete course A first.

You need to return:

One valid order to complete all courses.
If it is impossible (because of a cycle), return an empty list.
Think Like Real Life

Imagine university subjects:

Before learning Algorithms,
you must learn Data Structures.

Before learning Machine Learning,
you must learn Algorithms.

So obviously you cannot study ML first.

The problem is asking:

What is a valid sequence of learning?
Example 1 - Simple Chain
Input
courses = ["Intro", "DS", "Algo", "ML"]

prerequisites =
[
  ["Intro", "DS"],
  ["DS", "Algo"],
  ["Algo", "ML"]
]
Understanding the Relations
Intro -> DS -> Algo -> ML

Meaning:

Intro before DS
DS before Algo
Algo before ML
Valid Order
[Intro, DS, Algo, ML]

Because every prerequisite is satisfied.

Example 2 - Multiple Valid Answers
Input
courses = ["Math", "Physics", "Programming", "AI"]

prerequisites =
[
  ["Math", "AI"],
  ["Programming", "AI"]
]
Graph
Math --------\
              -> AI
Programming -/

Physics is independent.

Valid Outputs

All these are valid:

[Math, Programming, Physics, AI]
[Programming, Math, AI, Physics]
[Physics, Math, Programming, AI]

Why?

Because only rule is:

Math before AI
Programming before AI

Physics can be anywhere.

Example 3 - Impossible Case (Cycle)
Input
courses = ["A", "B", "C"]

prerequisites =
[
  ["A", "B"],
  ["B", "C"],
  ["C", "A"]
]
Graph
A -> B -> C -> A

Now think carefully:

A needs C
C needs B
B needs A

Circular dependency.

Nobody can start first.

Output
[]

Because completing all courses is impossible.

Example 4 - Independent Courses
Input
courses = ["Java", "DB", "OS"]

prerequisites = []

No restrictions.

Valid Outputs

Any order works:

[Java, DB, OS]
[OS, Java, DB]
    *
    * */


    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 5 - Branching prerequisites
     *
     * courses = ["Intro", "DS", "Discrete", "Algo"]
     * prerequisites = {
     *     {"Intro", "DS"},
     *     {"Intro", "Discrete"},
     *     {"DS", "Algo"},
     *     {"Discrete", "Algo"}
     * }
     *
     * One valid output:
     * [Intro, DS, Discrete, Algo]
     *
     * Why:
     * Algo waits until both DS and Discrete are completed.
     *
     * Example 6 - Two independent chains
     *
     * courses = ["A", "B", "C", "D"]
     * prerequisites = {
     *     {"A", "B"},
     *     {"C", "D"}
     * }
     *
     * Valid outputs include:
     * [A, B, C, D]
     * [C, D, A, B]
     * [A, C, B, D]
     *
     * Why:
     * A only controls B, and C only controls D.
     *
     * Example 7 - Duplicate-looking pressure
     *
     * courses = ["Intro", "DS", "Algo", "Capstone"]
     * prerequisites = {
     *     {"Intro", "DS"},
     *     {"DS", "Algo"},
     *     {"Intro", "Capstone"},
     *     {"Algo", "Capstone"}
     * }
     *
     * One valid output:
     * [Intro, DS, Algo, Capstone]
     *
     * Why:
     * Capstone cannot happen immediately after Intro because Algo is also needed.
     *
     * Edge case 1 - No courses
     *
     * courses = []
     * prerequisites = []
     *
     * Output:
     * []
     *
     * Why:
     * There is nothing to schedule.
     *
     * Edge case 2 - Self dependency
     *
     * courses = ["A"]
     * prerequisites = {{"A", "A"}}
     *
     * Output:
     * []
     *
     * Why:
     * A is waiting for A, so A can never be the first completed course.
     */

    /*
     * BRUTE FORCE APPROACH IN PLAIN ENGLISH
     *
     * Repeat:
     * Find courses whose prerequisites are already done.
     * Complete them.
     *
     * Until:
     * all courses are finished
     * OR
     * one full scan adds nothing, which means we are stuck forever.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. [A, B] means A must come before B.
     * 2. A course can be taken only when all its prerequisites are completed.
     * 3. More than one valid answer can exist.
     * 4. If no remaining course can be taken, the prerequisites contain a cycle.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Keep a completed set.
     * Repeatedly scan every course.
     * Add a course when all prerequisite pairs say it is safe.
     * If a full scan adds no course, return an empty list.
     */


    public List<String> bruteForce(String[] courses, String[][] prerequisites) {

        // final answer order
        // example: [Intro, DS, Algo]
        List<String> order = new ArrayList<>();


        // tracks courses already completed
        // needed because future courses depend on them
        Set<String> completed = new HashSet<>();


        // keep running until all courses are completed.
        // one round may unlock new courses for the next round.
        // example:
        // round 1 completes Intro
        // round 2 can now complete DS
        // round 3 can now complete Algo
        while (order.size() < courses.length) {

            // used to detect whether we made progress
            // if no course gets added in entire round,
            // then we are stuck (cycle/impossible)
            boolean addedInThisRound = false;


            // try every course and ask:
            // "Can I complete this course now?"
            for (String course : courses) {

                // if already completed,
                // no need to process again
                if (completed.contains(course)) {
                    continue;
                }


                // initially assume:
                // all prerequisites are satisfied
                boolean prerequisitesDone = true;


                // check every prerequisite relation.
                // brute force does not pre-build a graph here.
                // it simply scans the full prerequisite list again for this course.
                for (String[] prerequisite : prerequisites) {

                    // prerequisite[0] = before course
                    // prerequisite[1] = after course

                    String beforeCourse = prerequisite[0];
                    String afterCourse = prerequisite[1];


                    // if current course depends on something not completed yet,
                    // then we cannot take this course in this round.
                    if (afterCourse.equals(course)
                            && !completed.contains(beforeCourse)) {

                        prerequisitesDone = false;

                        // no need to check further prerequisites
                        break;
                    }
                }


                // if all prerequisites are completed,
                // we can safely take this course now.
                if (prerequisitesDone) {

                    // mark course completed
                    completed.add(course);

                    // add to final answer
                    order.add(course);

                    // we made progress in this round
                    addedInThisRound = true;
                }
            }


            // after checking all courses, if nothing got added,
            // that means every remaining course is still waiting.
            // That is the cycle/impossible signal.
            if (!addedInThisRound) {
                break;
            }
        }


        // if all courses were not completed,
        // then valid ordering does not exist
        if (order.size() != courses.length) {
            return new ArrayList<>();
        }


        return order;
    }



    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: use topological sort with a queue so each course and prerequisite edge is processed once.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: produce a valid course order from prerequisites.
     * 2. Remove repeated work: use indegree and queue for topological sort.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: courses = ["Intro", "DS", "Algo", "ML"] prerequisites = [["Intro", "DS"], ["DS", "Algo"], ["Algo", "ML"]] Output: [Intro, DS, Algo, ML] The schedule must put every prerequisite before the course that depends on it.
     * Walk the records one by one and the expected result above is produced.
     */
    public List<String> optimized(String[] courses, String[][] prerequisites) {
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> indegree = new HashMap<>();
        for (String course : courses) {
            graph.put(course, new ArrayList<>());
            indegree.put(course, 0);
        }
        for (String[] prerequisite : prerequisites) {
            String before = prerequisite[0];
            String after = prerequisite[1];
            if (graph.containsKey(before) && indegree.containsKey(after)) {
                graph.get(before).add(after);
                indegree.put(after, indegree.get(after) + 1);
            }
        }

        // Queue is used for BFS because it processes nodes in distance/order layers.
        Queue<String> queue = new ArrayDeque<>();
        for (String course : courses) {
            if (indegree.get(course) == 0) {
                queue.offer(course);
            }
        }

        List<String> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            String course = queue.poll();
            order.add(course);
            for (String next : graph.get(course)) {
                indegree.put(next, indegree.get(next) - 1);
                if (indegree.get(next) == 0) {
                    queue.offer(next);
                }
            }
        }
        return order.size() == courses.length ? order : new ArrayList<>();
    }

    public static void main(String[] args) {
        AdvisingMeetingWithCsStudents solution = new AdvisingMeetingWithCsStudents();

        String[][] sampleCourses = {
                {"Intro", "DS", "Algo", "ML"},
                {"Math", "Physics", "Programming", "AI"},
                {"A", "B", "C"}
        };

        String[][][] samplePrerequisites = {
                {
                        {"Intro", "DS"},
                        {"DS", "Algo"},
                        {"Algo", "ML"}
                },
                {
                        {"Math", "AI"},
                        {"Programming", "AI"}
                },
                {
                        {"A", "B"},
                        {"B", "C"},
                        {"C", "A"}
                }
        };

        for (int i = 0; i < sampleCourses.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + solution.bruteForce(sampleCourses[i], samplePrerequisites[i]));
            System.out.println("optimized:  " + solution.optimized(sampleCourses[i], samplePrerequisites[i]));
            System.out.println();
        }
    }
}
