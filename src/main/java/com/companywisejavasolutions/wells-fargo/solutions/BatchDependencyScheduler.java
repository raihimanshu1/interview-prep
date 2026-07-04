
package com.companywisejavasolutions.wellsFargo.solutions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BatchDependencyScheduler {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a list of batch jobs and dependency relationships, return one valid
     * order in which all jobs can run.
     *
     * INPUT
     * jobs is the full job list.
     * dependencies[i] = [beforeJob, afterJob].
     *
     * OUTPUT
     * A valid job execution order, or an empty list if the jobs contain a cycle.
     *
     * EXAMPLE
     * jobs = ["Extract", "Transform", "Load"]
     * dependencies = [["Extract", "Transform"], ["Transform", "Load"]]
     * Output: [Extract, Transform, Load]
     *
     * WHAT IT MEANS
     * This is topological sorting of a dependency graph.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Think of a nightly banking batch pipeline:
     *
     * 1. Extract transaction files.
     * 2. Transform the records.
     * 3. Load them into reporting tables.
     *
     * Load cannot happen before Transform.
     * Transform cannot happen before Extract.
     *
     * So the question is:
     * "Which jobs are safe to run now?"
     *
     * A job is safe when all jobs it depends on have already completed.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * Every dependency hides this sentence:
     *
     * [A, B] means A must run before B.
     *
     * So B is waiting for A.
     *
     * If a job has no unfinished dependencies, we can run it now.
     * Running that job may unlock other jobs.
     *
     * If every remaining job is waiting for another remaining job, we are stuck.
     * That stuck state means there is a cycle.
     *
     * Example cycle:
     *
     * A -> B
     * B -> C
     * C -> A
     *
     * Nobody can run first because everyone is waiting.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Simple chain
     *
     * jobs = ["A", "B", "C"]
     * dependencies = [["A", "B"], ["B", "C"]]
     * Output: [A, B, C]
     *
     * Example 2 - Independent jobs
     *
     * jobs = ["A", "B", "C"]
     * dependencies = []
     * Valid outputs include [A, B, C], [B, A, C], or any order.
     *
     * Example 3 - Branching dependencies
     *
     * jobs = ["Extract", "Validate", "Transform", "Load"]
     * dependencies = [
     *     ["Extract", "Validate"],
     *     ["Extract", "Transform"],
     *     ["Validate", "Load"],
     *     ["Transform", "Load"]
     * ]
     *
     * Load waits for both Validate and Transform.
     *
     * Edge case 1 - No jobs
     *
     * jobs = []
     * dependencies = []
     * Output: []
     *
     * Edge case 2 - Self dependency
     *
     * jobs = ["A"]
     * dependencies = [["A", "A"]]
     * Output: []
     */

    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     * 1. What do I notice first?
     *    This is not really about batch jobs. It is about ordering things when
     *    some things must happen before other things.
     *
     * 2. What is the brute force idea?
     *    Keep scanning all jobs. Whenever a job's dependencies are already done,
     *    run it.
     *
     * 3. What repeated work does brute force do?
     *    For every job in every round, it scans the full dependency list again.
     *
     * 4. What optimized state removes that repeated work?
     *    Track how many unfinished prerequisites each job has. This count is
     *    called indegree.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Repeat rounds.
     *
     * In each round, ask every unfinished job:
     * "Are all jobs before me already completed?"
     *
     * If yes, add it to the order.
     * If a full round adds nothing, we are stuck forever, so return empty list.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create an answer list called order.
     * 2. Create a completed set.
     * 3. While order does not contain all jobs:
     *    a. Scan every job.
     *    b. Skip jobs already completed.
     *    c. For the current job, scan every dependency.
     *    d. If all its dependencies are completed, add it to order.
     *    e. Remember that this round made progress.
     * 4. If a full round makes no progress, return empty list.
     *
     * Time Complexity: O(jobs * jobs * dependencies) in the worst case,
     * because many rounds may scan all jobs and dependencies again.
     *
     * Space Complexity: O(jobs) for order and completed set.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * jobs = ["A", "B", "C"]
     * dependencies = [["A", "B"], ["B", "C"]]
     *
     * Round 1:
     * A has no dependency, so add A.
     * B waits for A, now A is completed, so add B.
     * C waits for B, now B is completed, so add C.
     *
     * order = [A, B, C]
     */
    public List<String> bruteForce(String[] jobs, String[][] dependencies) {
        // This is the final schedule we are building.
        // A job appears here only after all its prerequisites are completed.
        List<String> order = new ArrayList<>();

        // completed lets us answer:
        // "Has this prerequisite already run?"
        Set<String> completed = new HashSet<>();

        // Keep doing rounds because completing one job may unlock another job.
        while (order.size() < jobs.length) {
            // If an entire round adds nothing, the remaining jobs are stuck in a cycle.
            boolean addedInThisRound = false;

            // Try every job and ask whether it is safe to run now.
            for (String job : jobs) {
                // Already scheduled jobs should not be scheduled again.
                if (completed.contains(job)) {
                    continue;
                }

                // Brute force does not pre-build a graph.
                // It simply scans the dependency list again for this job.
                if (allDependenciesCompleted(job, dependencies, completed)) {
                    // Mark first so later jobs in the same round can be unlocked.
                    completed.add(job);

                    // The answer records the exact run order.
                    order.add(job);

                    // Progress means we should continue another round if needed.
                    addedInThisRound = true;
                }
            }

            // No progress means every remaining job is waiting for another remaining job.
            if (!addedInThisRound) {
                return new ArrayList<>();
            }
        }

        return order;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is repeatedly asking:
     * "Are this job's prerequisites done?"
     *
     * Instead, store that answer as a number.
     *
     * indegree[job] = how many prerequisites are still unfinished.
     *
     * Any job with indegree 0 can run now.
     * When that job runs, it reduces the indegree of jobs that depend on it.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build a graph:
     *    beforeJob -> list of jobs unlocked by beforeJob.
     * 2. Build indegree:
     *    job -> number of unfinished prerequisites.
     * 3. Put all indegree-0 jobs in a queue.
     * 4. Repeatedly remove a job from the queue and append it to order.
     * 5. For each dependent job, reduce indegree by 1.
     * 6. If a dependent job becomes indegree 0, add it to the queue.
     * 7. If order includes all jobs, return it; otherwise return empty list.
     *
     * Time Complexity: O(jobs + dependencies)
     * Space Complexity: O(jobs + dependencies)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * jobs = ["A", "B", "C"]
     * dependencies = [["A", "B"], ["B", "C"]]
     *
     * graph:
     * A -> [B]
     * B -> [C]
     *
     * indegree:
     * A = 0
     * B = 1
     * C = 1
     *
     * queue starts with A.
     * Run A, reduce B to 0, queue B.
     * Run B, reduce C to 0, queue C.
     * Run C.
     *
     * order = [A, B, C]
     */
    public List<String> optimized(String[] jobs, String[][] dependencies) {
        // graph answers:
        // "If this job finishes, which jobs might become unlocked?"
        Map<String, List<String>> graph = new HashMap<>();

        // indegree answers:
        // "How many prerequisites are still blocking this job?"
        Map<String, Integer> indegree = new HashMap<>();

        // Initialize every job, including independent jobs that never appear
        // in the dependency list.
        for (String job : jobs) {
            graph.put(job, new ArrayList<>());
            indegree.put(job, 0);
        }

        // Convert each [before, after] dependency into graph and indegree data.
        for (String[] dependency : dependencies) {
            String before = dependency[0];
            String after = dependency[1];

            // Finishing "before" can unlock "after".
            graph.get(before).add(after);

            // "after" has one more prerequisite to wait for.
            indegree.put(after, indegree.get(after) + 1);
        }

        Queue<String> queue = new ArrayDeque<>();

        // Jobs with no prerequisites can run immediately.
        for (String job : jobs) {
            if (indegree.get(job) == 0) {
                queue.offer(job);
            }
        }

        List<String> order = new ArrayList<>();

        // Process jobs in the order they become available.
        while (!queue.isEmpty()) {
            String job = queue.poll();
            order.add(job);

            // Running this job may unlock jobs that depend on it.
            for (String next : graph.get(job)) {
                // One prerequisite for next has now completed.
                indegree.put(next, indegree.get(next) - 1);

                // If no prerequisites remain, next is safe to run.
                if (indegree.get(next) == 0) {
                    queue.offer(next);
                }
            }
        }

        // If some jobs never reached indegree 0, they are trapped in a cycle.
        return order.size() == jobs.length ? order : new ArrayList<>();
    }

    private boolean allDependenciesCompleted(String job, String[][] dependencies, Set<String> completed) {
        // Scan every rule because brute force does not store job -> prerequisites.
        for (String[] dependency : dependencies) {
            // dependency[0] must happen before dependency[1].
            // If current job is dependency[1], then dependency[0] must be completed.
            if (dependency[1].equals(job) && !completed.contains(dependency[0])) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        BatchDependencyScheduler solver = new BatchDependencyScheduler();

        String[] jobs = {"Extract", "Transform", "Load"};
        String[][] dependencies = {
                {"Extract", "Transform"},
                {"Transform", "Load"}
        };

        System.out.println("Brute force: " + solver.bruteForce(jobs, dependencies));
        System.out.println("Optimized: " + solver.optimized(jobs, dependencies));

        String[] cyclicJobs = {"A", "B", "C"};
        String[][] cyclicDependencies = {
                {"A", "B"},
                {"B", "C"},
                {"C", "A"}
        };

        System.out.println("Cycle brute force: " + solver.bruteForce(cyclicJobs, cyclicDependencies));
        System.out.println("Cycle optimized: " + solver.optimized(cyclicJobs, cyclicDependencies));
    }
}
