package com.companywisejavasolutions.karat.solutions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public class ReachableGoodEndings {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * A storybook has numbered pages. From most pages, reading continues to the
     * next page. Some pages have choices that jump to other pages. Some pages
     * are good endings and some are bad endings.
     *
     * Starting from page 1, return all good endings that can be reached.
     *
     * EXAMPLE
     * lastPage = 7
     * goodEndings = [4, 6]
     * badEndings = [7]
     * choices = [[2,4], [2,5], [5,6]]
     *
     * Output:
     * [4, 6]
     *
     * What It Means:
     * From page 2 we can reach good ending 4 directly or page 5 then good ending 6.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Think of this like a choose-your-own-adventure book.
     *
     * At a normal page, you turn to the next page.
     * At a choice page, you may jump to one or more other pages.
     * At an ending page, you stop.
     *
     * Since choices can create loops, we must remember pages we already visited.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * Treat the book like a graph.
     *
     * Each page is a node.
     * A normal page has one automatic edge to page + 1.
     * A choice page has one or more edges to the pages listed in choices.
     * A good ending or bad ending is a stopping page.
     *
     * Starting from page 1, we explore all possible reading paths. Whenever a
     * path reaches a good ending, that ending belongs in the answer. Whenever a
     * path reaches a bad ending, runs past lastPage, or repeats a page already
     * on the current path, that path stops.
     */

    /*
     * EXAMPLES AND EDGE CASES
     *
     * Example 1 - Two reachable good endings
     *
     * lastPage = 7
     * goodEndings = [4, 6]
     * badEndings = [7]
     * choices = [[2,4], [2,5], [5,6]]
     *
     * Page 1 goes to 2. From page 2, one path reaches 4 and another reaches
     * 5 then 6.
     * Output: [4, 6]
     *
     * Example 2 - Choice reaches a bad ending
     *
     * lastPage = 5
     * goodEndings = [5]
     * badEndings = [3]
     * choices = [[2,3], [2,4]]
     *
     * The 2 -> 3 path stops badly. The 2 -> 4 -> 5 path succeeds.
     * Output: [5]
     *
     * Example 3 - Loop still allows one good ending
     *
     * lastPage = 6
     * goodEndings = [6]
     * badEndings = []
     * choices = [[2,3], [3,2], [3,6]]
     *
     * The 2 -> 3 -> 2 loop is ignored once it repeats on the current path.
     * The 3 -> 6 branch still reaches a good ending.
     * Output: [6]
     *
     * Edge case 1 - Page 1 is already a good ending
     *
     * goodEndings = [1]
     * Output: [1]
     *
     * Edge case 2 - No good ending is reachable
     *
     * If every path reaches a bad ending, runs past lastPage, or loops forever,
     * the answer is [].
     *
     * Edge case 3 - Choice points beyond the book
     *
     * A page greater than lastPage is invalid, so that path stops.
     */

    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     * 1. What is this problem really?
     *    It is graph reachability. Pages are nodes and page transitions are edges.
     *
     * 2. What are the possible next pages?
     *    If the page has choices, follow those choices. Otherwise, go to page + 1.
     *
     * 3. What is the brute force?
     *    Recursively try every path from page 1 until we hit an ending.
     *
     * 4. What is the danger?
     *    A loop like 2 -> 3 -> 2 can run forever, so the optimized version tracks
     *    visited pages globally.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Endings are terminal: once reached, we do not continue reading.
     * 2. Choices replace the normal page + 1 movement for that page.
     * 3. A current-path set prevents one recursive branch from cycling forever.
     * 4. A sorted answer set makes output deterministic.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * The brute force builds the choice graph and then recursively explores from
     * page 1. Each call asks: is this page invalid, bad, good, a choice page, or
     * a normal page? The answer to that question decides whether we stop, record
     * a good ending, follow listed choices, or move to page + 1.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Follow paths recursively like a reader exploring the book. To prevent one
     * path from looping forever, keep the current path set. This brute force can
     * still revisit the same page through different paths.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Convert endings to sets for easy checking.
     * 2. Build a map from choice page to destination pages.
     * 3. Start DFS from page 1.
     * 4. If current page is bad or outside the book, stop.
     * 5. If current page is good, add it to answer.
     * 6. Otherwise follow choices, or page + 1 if no choice exists.
     *
     * Time Complexity: Can revisit pages through different paths.
     * Space Complexity: O(p) for recursion path and graph.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the storybook example above.
     * Page 1 goes to 2 by default.
     * At page 2, one choice reaches good ending 4.
     * Another choice reaches page 5, then good ending 6.
     * Final answer: [4, 6]
     */
    public List<Integer> bruteForce(int lastPage, int[] goodEndings, int[] badEndings,
            int[][] choices) {
        // Sets make ending checks read like the problem statement: is this page good or bad?
        Set<Integer> good = toSet(goodEndings);
        Set<Integer> bad = toSet(badEndings);
        // The graph stores only explicit choice jumps; normal pages still go to page + 1.
        Map<Integer, List<Integer>> graph = buildChoiceGraph(choices);
        // TreeSet keeps the reachable good endings sorted.
        Set<Integer> answer = new TreeSet<>();

        // Start reading at page 1 with an empty current path.
        dfsWithPath(1, lastPage, graph, good, bad, new HashSet<>(), answer);

        return new ArrayList<>(answer);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force may reprocess the same page from different paths. But once
     * we already know a page is reachable and have explored it, exploring it
     * again cannot discover anything new.
     *
     * BFS or DFS with a global visited set processes each page once.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build sets for good and bad endings.
     * 2. Build choice graph.
     * 3. Push page 1 into a queue.
     * 4. Repeatedly remove one page:
     *    a. Skip if invalid, bad, or already visited.
     *    b. If good, add it and do not continue from it.
     *    c. Otherwise enqueue its choice destinations or page + 1.
     * 5. Return sorted good endings.
     *
     * Time Complexity: O(p + c), where p is pages and c is choices.
     * Space Complexity: O(p + c)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the storybook example above.
     * Page 1 goes to 2 by default.
     * At page 2, one choice reaches good ending 4.
     * Another choice reaches page 5, then good ending 6.
     * Final answer: [4, 6]
     */
    public List<Integer> optimized(int lastPage, int[] goodEndings, int[] badEndings,
            int[][] choices) {
        Set<Integer> good = toSet(goodEndings);
        Set<Integer> bad = toSet(badEndings);
        Map<Integer, List<Integer>> graph = buildChoiceGraph(choices);
        Set<Integer> visited = new HashSet<>();
        Set<Integer> answer = new TreeSet<>();
        Queue<Integer> queue = new ArrayDeque<>();

        queue.add(1);

        while (!queue.isEmpty()) {
            int page = queue.remove();

            if (page > lastPage || bad.contains(page) || !visited.add(page)) {
                continue;
            }

            if (good.contains(page)) {
                answer.add(page);
                continue;
            }

            if (graph.containsKey(page)) {
                queue.addAll(graph.get(page));
            } else {
                queue.add(page + 1);
            }
        }

        return new ArrayList<>(answer);
    }

    private void dfsWithPath(int page, int lastPage, Map<Integer, List<Integer>> graph,
            Set<Integer> good, Set<Integer> bad, Set<Integer> path, Set<Integer> answer) {
        // If the page number is beyond the book, this path is invalid and cannot produce an ending.
        // If this page is bad, the story ends unsuccessfully, so there is nothing more to explore.
        // If this page is already in the current path, we found a loop like 2 -> 3 -> 2.
        // In all three cases, returning keeps the recursive search finite and faithful to the story rules.
        if (page > lastPage || bad.contains(page) || path.contains(page)) {
            return;
        }

        // A good ending is exactly what we are trying to collect.
        // It is also terminal, so we record it and intentionally do not move to page + 1 afterward.
        if (good.contains(page)) {
            answer.add(page);
            return;
        }

        // Mark this page as part of the current reading path.
        // This is path-specific, not global, because another branch may legitimately reach this page later.
        path.add(page);

        // If the page has explicit choices, those choices replace the normal "turn to the next page" rule.
        if (graph.containsKey(page)) {
            // Try every possible destination because any one of them may lead to a good ending.
            for (int next : graph.get(page)) {
                // Continue the same path with the chosen destination.
                dfsWithPath(next, lastPage, graph, good, bad, path, answer);
            }
        } else {
            // A non-choice page follows the default storybook rule: continue to the next numbered page.
            dfsWithPath(page + 1, lastPage, graph, good, bad, path, answer);
        }

        // Backtrack after all branches from this page are explored.
        // Removing the page lets sibling branches use it without being mistaken for a loop.
        path.remove(page);
    }

    private Map<Integer, List<Integer>> buildChoiceGraph(int[][] choices) {
        // The graph stores only special jumps, because normal page + 1 movement can be computed on demand.
        Map<Integer, List<Integer>> graph = new HashMap<>();

        // Each choice row says: from this page, the reader may jump to that destination page.
        for (int[] choice : choices) {
            int fromPage = choice[0];
            int toPage = choice[1];

            // A page may have multiple choices, so we keep a list of destinations for each source page.
            // Create that destination list the first time we see the source page.
            graph.putIfAbsent(fromPage, new ArrayList<>());

            // Add this explicit jump to the source page's options.
            graph.get(fromPage).add(toPage);
        }

        // Returning the map gives the traversal a quick way to ask whether a page is a choice page.
        return graph;
    }

    private Set<Integer> toSet(int[] values) {
        // A set matches the question we ask during traversal: "is this page one of the endings?"
        Set<Integer> set = new HashSet<>();

        // Copy every page number into the set so membership checks are fast and simple.
        for (int value : values) {
            // Duplicates, if any, naturally collapse, which is fine for ending membership.
            set.add(value);
        }

        // The caller uses this set for constant-average-time good/bad ending checks.
        return set;
    }

    public static void main(String[] args) {
        ReachableGoodEndings solver = new ReachableGoodEndings();

        int[] lastPages = {7, 5, 6};
        int[][] goodSamples = {
                {4, 6},
                {5},
                {6}
        };
        int[][] badSamples = {
                {7},
                {3},
                {}
        };
        int[][][] choiceSamples = {
                {{2, 4}, {2, 5}, {5, 6}},
                {{2, 3}, {2, 4}},
                {{2, 3}, {3, 2}, {3, 6}}
        };

        for (int i = 0; i < lastPages.length; i++) {
            System.out.println("Sample " + (i + 1) + ":");
            System.out.println("lastPage = " + lastPages[i]);
            System.out.println("goodEndings = " + formatArray(goodSamples[i]));
            System.out.println("badEndings = " + formatArray(badSamples[i]));
            System.out.println("choices = " + formatTable(choiceSamples[i]));
            System.out.println("bruteForce = "
                    + solver.bruteForce(lastPages[i], copyArray(goodSamples[i]), copyArray(badSamples[i]), copyTable(choiceSamples[i])));
            System.out.println("optimized = "
                    + solver.optimized(lastPages[i], copyArray(goodSamples[i]), copyArray(badSamples[i]), copyTable(choiceSamples[i])));
            System.out.println();
        }
    }

    private static int[] copyArray(int[] values) {
        int[] copy = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            copy[i] = values[i];
        }
        return copy;
    }

    private static int[][] copyTable(int[][] table) {
        int[][] copy = new int[table.length][];
        for (int row = 0; row < table.length; row++) {
            copy[row] = copyArray(table[row]);
        }
        return copy;
    }

    private static String formatArray(int[] values) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(values[i]);
        }
        builder.append("]");
        return builder.toString();
    }

    private static String formatTable(int[][] table) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int row = 0; row < table.length; row++) {
            if (row > 0) {
                builder.append(", ");
            }
            builder.append(formatArray(table[row]));
        }
        builder.append("]");
        return builder.toString();
    }
}
