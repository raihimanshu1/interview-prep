package karat.solutions;

import java.util.*;

public class StorybookWithLoops {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a storybook graph that may contain loops, return reachable ending pages without infinite traversal.
     *
     * INPUT
     * lastPage, endings set, choices [fromPage, toPage].
     *
     * OUTPUT
     * List of ending pages reachable from page 1.
     *
     * EXAMPLE
     * lastPage = 6, endings = [5, 6], choices = [[2,3], [3,2], [3,5], [4,6]]
     * Output: [5]
     * 
     * The 2 <-> 3 loop must not run forever, and page 5 is reachable from that loop.
     *
     * WHAT IT MEANS
     * Use visited pages so loops do not trap traversal.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of a story map where pages point to other pages. Loops are possible,
     * so visited pages protect us from reading forever.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * This problem is also a graph problem, but the storybook wording makes it
     * easier to picture.
     *
     * A page is a place where the reader can stand.
     * A choice is a directed jump from one page to another page.
     * A page with no choice automatically continues to page + 1.
     * An ending page stops the story and belongs in the answer if we can reach it.
     *
     * Loops are the main danger. If page 2 can go to page 3 and page 3 can go
     * back to page 2, a naive recursive walk can keep reading forever. Brute
     * force still explores recursively, but it carries the pages already on the
     * current path so it can stop when a path circles back.
     */

    /*
     * EXAMPLES AND EDGE CASES
     *
     * Example 1 - Loop with an escape to an ending
     *
     * lastPage = 6
     * endings = [5, 6]
     * choices = [[2,3], [3,2], [3,5], [4,6]]
     *
     * Page 1 goes to 2. Pages 2 and 3 can loop, but 3 can also jump to 5.
     * Output: [5]
     *
     * Example 2 - Normal pages reach an ending
     *
     * lastPage = 4
     * endings = [4]
     * choices = []
     *
     * The story reads 1 -> 2 -> 3 -> 4.
     * Output: [4]
     *
     * Example 3 - Choice skips one ending and reaches another
     *
     * lastPage = 6
     * endings = [3, 6]
     * choices = [[2,5]]
     *
     * Page 2 has a choice, so it jumps to 5 instead of naturally going to 3.
     * Then 5 -> 6.
     * Output: [6]
     *
     * Edge case 1 - Loop with no reachable ending
     *
     * choices = [[1,2], [2,1]]
     * Output: []
     *
     * Edge case 2 - Page 1 is an ending
     *
     * endings = [1]
     * Output: [1]
     *
     * Edge case 3 - Choice beyond lastPage
     *
     * Any branch that lands past lastPage stops without adding an ending.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    This is graph reachability with possible cycles, so repeated pages are dangerous.
     *
     * 2. What data structure does that naturally suggest?
     *    Use visited pages because choices can send us back to an earlier page.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: follow each story path recursively.
     *
     * 4. What repeated work should I remove?
     *    Optimized: DFS/BFS with visited gives all reachable endings without infinite loops.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. A choice page uses its listed destinations instead of page + 1.
     * 2. A non-choice page moves to page + 1.
     * 3. Ending pages stop traversal.
     * 4. A path set prevents the current recursive branch from looping forever.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Brute force builds a map from each choice page to all pages it can jump to.
     * Then it follows the story from page 1. At each page it either stops,
     * records an ending, branches through choices, or moves to the next page.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: follow each story path recursively.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Start from the direct goal: find reachable endings in story pages with loops.
     * 2. Use the plain human method: recursively follow story paths using current path protection.
     * 3. Check the problem rule exactly for each candidate.
     * 4. Add valid results in the requested output shape.
     * 
     * Time Complexity: Higher than optimized because this version repeats the direct work described above.
     * Space Complexity: O(n) for the answer and any direct helper state.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the loop example above.
     * Pages 2 and 3 can loop, so visited tracking is required.
     * The path 2 -> 3 -> 5 still reaches ending 5.
     * Final answer: [5]
     */
    public List<Integer> bruteForce(int lastPage, Set<Integer> endings, int[][] choices) {

        // Store every explicit page jump. Pages not in this map naturally go to page + 1.
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int[] choice : choices) {
            int fromPage = choice[0];
            int toPage = choice[1];

            graph.putIfAbsent(fromPage, new ArrayList<>());
            graph.get(fromPage).add(toPage);
        }

        // TreeSet gives stable sorted output even if paths discover endings in another order.
        Set<Integer> answer = new TreeSet<>();
        // Start at page 1 with no pages on the current path yet.
        followStory(1, lastPage, endings, graph, new HashSet<>(), answer);
        return new ArrayList<>(answer);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: DFS/BFS with visited gives all reachable endings without infinite loops.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find reachable endings in story pages with loops.
     * 2. Remove repeated work: BFS/DFS with global visited so each page is processed once.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the loop example above.
     * Pages 2 and 3 can loop, so visited tracking is required.
     * The path 2 -> 3 -> 5 still reaches ending 5.
     * Final answer: [5]
     */
    public List<Integer> optimized(int lastPage, Set<Integer> endings, int[][] choices) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int[] choice : choices) {
            int fromPage = choice[0];
            int toPage = choice[1];

            graph.putIfAbsent(fromPage, new ArrayList<>());
            graph.get(fromPage).add(toPage);
        }

        // Queue is used for BFS because it processes nodes in distance/order layers.
        Queue<Integer> queue = new ArrayDeque<>();
        // HashSet lets us ask "have we seen this before?" in constant average time.
        Set<Integer> visited = new HashSet<>();
        List<Integer> result = new ArrayList<>();
        queue.offer(1);
        while (!queue.isEmpty()) {
            int page = queue.poll();
            if (page > lastPage || !visited.add(page)) {
                continue;
            }
            if (endings.contains(page)) {
                result.add(page);
                continue;
            }
            if (graph.containsKey(page)) {
                queue.addAll(graph.get(page));
            } else {
                queue.offer(page + 1);
            }
        }
        return result;
    }

    private void followStory(int page, int lastPage, Set<Integer> endings,
            Map<Integer, List<Integer>> graph, Set<Integer> path, Set<Integer> answer) {
        // A page past lastPage is outside the story, so this branch cannot reach a valid ending.
        // A page already in path means the current branch has looped back on itself.
        // Returning here is what prevents examples like 2 -> 3 -> 2 from recursing forever.
        if (page > lastPage || path.contains(page)) {
            return;
        }

        // If the current page is an ending, the story stops successfully at this page.
        // We add it to the answer and avoid following choices or page + 1 from a terminal page.
        if (endings.contains(page)) {
            answer.add(page);
            return;
        }

        // Add the current page to this branch's path before exploring children.
        // This lets deeper calls detect only loops on the active route, not pages from unrelated routes.
        path.add(page);

        // Choice pages use their listed jumps instead of the automatic next-page transition.
        if (graph.containsKey(page)) {
            // Explore every available choice because each one represents a possible reader decision.
            for (int next : graph.get(page)) {
                // Continue recursively from the selected destination page.
                followStory(next, lastPage, endings, graph, path, answer);
            }
        } else {
            // If there is no explicit choice, the story advances to the next numbered page.
            followStory(page + 1, lastPage, endings, graph, path, answer);
        }

        // Backtrack once all routes from this page have been tried.
        // Removing it is important because another branch may later reach this page without forming a loop.
        path.remove(page);
    }

    public static void main(String[] args) {
        StorybookWithLoops storybook = new StorybookWithLoops();

        int[] lastPages = {6, 4, 6};
        List<Set<Integer>> endingSamples = new ArrayList<>();
        endingSamples.add(makeSet(new int[] {5, 6}));
        endingSamples.add(makeSet(new int[] {4}));
        endingSamples.add(makeSet(new int[] {3, 6}));

        int[][][] choiceSamples = {
                {{2, 3}, {3, 2}, {3, 5}, {4, 6}},
                {},
                {{2, 5}}
        };

        for (int i = 0; i < lastPages.length; i++) {
            System.out.println("Sample " + (i + 1) + ":");
            System.out.println("lastPage = " + lastPages[i]);
            System.out.println("endings = " + endingSamples.get(i));
            System.out.println("choices = " + formatTable(choiceSamples[i]));
            System.out.println("bruteForce = "
                    + storybook.bruteForce(lastPages[i], copySet(endingSamples.get(i)), copyTable(choiceSamples[i])));
            System.out.println("optimized = "
                    + storybook.optimized(lastPages[i], copySet(endingSamples.get(i)), copyTable(choiceSamples[i])));
            System.out.println();
        }
    }

    private static Set<Integer> makeSet(int[] values) {
        Set<Integer> result = new TreeSet<>();
        for (int value : values) {
            result.add(value);
        }
        return result;
    }

    private static Set<Integer> copySet(Set<Integer> values) {
        Set<Integer> copy = new HashSet<>();
        for (int value : values) {
            copy.add(value);
        }
        return copy;
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
