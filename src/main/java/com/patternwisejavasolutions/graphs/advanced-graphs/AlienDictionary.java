
package com.patternwisejavasolutions.graphs.advancedGraphs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class AlienDictionary {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given sorted words from an alien language.
     * Return a possible order of characters.
     * Example words = ["wrt","wrf","er","ett","rftt"] -> "wertf".
     *
     * Sample Input:
     * words = ["wrt","wrf","er","ett","rftt"]
     *
     * Sample Output:
     * "wertf"
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Compare neighboring words.
     * The first different character tells us ordering.
     * Example "wrt" before "wrf" means t comes before f.
     * Later letters do not matter once the first difference is found, just like normal dictionary order.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * First collect ordering rules by comparing each neighboring word pair.
     * Then use DFS to ask:
     * "Before I write this character in the answer, what characters must come after it?"
     *
     * DFS places a character into the answer only after all characters depending on it are handled.
     * Because that builds the answer backward, we reverse at the end.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Compare "wrt" and "wrf": t != f, so t -> f.
     * Compare "wrf" and "er": w != e, so w -> e.
     * These rules form a directed graph.
     *
     * DFS example:
     * If t -> f, DFS visits f first, then appends t.
     * That gives reverse order pieces, so reversing gives t before f.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Build character ordering graph from adjacent words.
     * 2. DFS detect cycle and build order.
     * Time Complexity: O(total characters)
     * Space Complexity: O(unique characters)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public String bruteForce(String[] words) {
        Map<Character, List<Character>> graph = new HashMap<>();

        if (!buildRules(words, graph)) {
            return "";
        }

        Map<Character, Integer> state = new HashMap<>();
        StringBuilder reversedOrder = new StringBuilder();

        for (char ch : graph.keySet()) {
            if (hasCycle(ch, graph, state, reversedOrder)) {
                return "";
            }
        }

        return reversedOrder.reverse().toString();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * DFS can build the order after checking for cycles.
     * Topological sort makes the alphabet-building idea more direct:
     * characters with indegree 0 have no letters forced before them, so they can come next.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * If t -> f, indegree[f] = 1.
     * When t is placed in answer, f's indegree becomes 0 and can be placed later.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Add every character as a graph node.
     * 2. Compare adjacent words to create edges.
     * 3. If invalid prefix case appears, return empty string.
     * 4. Topological sort with queue.
     * 5. If answer length misses characters, cycle exists.
     * Time Complexity: O(total characters)
     * Space Complexity: O(unique characters)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public String optimized(String[] words) {
        Map<Character, List<Character>> graph = new HashMap<>();
        Map<Character, Integer> indegree = new HashMap<>();

        for (String word : words) {
            for (char ch : word.toCharArray()) {
                graph.putIfAbsent(ch, new ArrayList<>());
                indegree.putIfAbsent(ch, 0);
            }
        }

        for (int i = 0; i < words.length - 1; i++) {
            String first = words[i];
            String second = words[i + 1];

            if (first.length() > second.length() && first.startsWith(second)) {
                return "";
            }

            int length = Math.min(first.length(), second.length());

            for (int index = 0; index < length; index++) {
                char from = first.charAt(index);
                char to = second.charAt(index);

                if (from != to) {
                    if (!graph.get(from).contains(to)) {
                        // This edge means "from must appear before to" in the alien alphabet.
                        graph.get(from).add(to);
                        indegree.put(to, indegree.get(to) + 1);
                    }
                    break;
                }
            }
        }

        Queue<Character> queue = new LinkedList<>();

        for (char ch : indegree.keySet()) {
            if (indegree.get(ch) == 0) {
                queue.offer(ch);
            }
        }

        StringBuilder order = new StringBuilder();

        while (!queue.isEmpty()) {
            char ch = queue.poll();
            order.append(ch);

            for (char next : graph.get(ch)) {
                indegree.put(next, indegree.get(next) - 1); // Placing ch satisfies one rule before next.

                if (indegree.get(next) == 0) {
                    queue.offer(next);
                }
            }
        }

        return order.length() == indegree.size() ? order.toString() : "";
    }

    private boolean buildRules(String[] words, Map<Character, List<Character>> graph) {
        for (String word : words) {
            for (char ch : word.toCharArray()) {
                graph.putIfAbsent(ch, new ArrayList<>());
            }
        }

        for (int i = 0; i < words.length - 1; i++) {
            String first = words[i];
            String second = words[i + 1];

            if (first.length() > second.length() && first.startsWith(second)) {
                return false;
            }

            int length = Math.min(first.length(), second.length());

            for (int index = 0; index < length; index++) {
                char from = first.charAt(index);
                char to = second.charAt(index);

                if (from != to) {
                    if (!graph.get(from).contains(to)) {
                        graph.get(from).add(to);
                    }
                    break;
                }
            }
        }

        return true;
    }

    private boolean hasCycle(
            char ch,
            Map<Character, List<Character>> graph,
            Map<Character, Integer> state,
            StringBuilder reversedOrder) {
        if (state.getOrDefault(ch, 0) == 1) {
            return true;
        }

        if (state.getOrDefault(ch, 0) == 2) {
            return false;
        }

        // State 1 means this character is currently in the DFS path.
        state.put(ch, 1);

        for (char next : graph.get(ch)) {
            if (hasCycle(next, graph, state, reversedOrder)) {
                return true;
            }
        }

        // After all later characters are placed, this character can be appended.
        state.put(ch, 2);
        reversedOrder.append(ch);
        return false;
    }
}
