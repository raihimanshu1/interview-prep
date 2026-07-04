
package com.companywisejavasolutions.wellsFargo.solutions;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class CloneGraph {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given a node in an undirected graph, return a deep copy of the graph.
     * Every cloned node must be new, not the same object.
     *
     * Sample Input:
     * adjList = [[2,4],[1,3],[2,4],[1,3]]
     *
     * Sample Output:
     * A new cloned graph with the same neighbor lists.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If we clone a node, we must also clone its neighbors.
     * But graphs can have cycles, like friend A points to friend B and B points back to A.
     * We need a map from original node to cloned node so each original gets exactly one copy.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Treat cloning like copying a friend's contact network level by level.
     * Create a clone when a node first appears, then use a queue to fill each clone's neighbor list.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Put original node 1 in the queue and create clone 1.
     * Visit 1's neighbors 2 and 4, create their clones, and add those originals to the queue.
     * When 2 points back to 1, reuse clone 1 from the map instead of making another node.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. If node is null, return null.
     * 2. Create a clone for the start node and put the original in a queue.
     * 3. While queue is not empty, process one original node.
     * 4. For each neighbor, create its clone if needed and add the cloned neighbor to the current clone.
     * Time Complexity: O(V+E)
     * Space Complexity: O(V)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public Node bruteForce(Node node) {
        if (node == null) {
            return null;
        }

        Map<Node, Node> cloned = new HashMap<>();
        Queue<Node> queue = new ArrayDeque<>();

        cloned.put(node, new Node(node.val));
        queue.add(node);

        while (!queue.isEmpty()) {
            Node current = queue.remove();
            Node currentCopy = cloned.get(current);

            for (Node neighbor : current.neighbors) {
                if (!cloned.containsKey(neighbor)) {
                    cloned.put(neighbor, new Node(neighbor.val));
                    queue.add(neighbor);
                }

                currentCopy.neighbors.add(cloned.get(neighbor));
            }
        }

        return cloned.get(node);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * BFS and DFS both visit every graph piece once.
     * DFS expresses the copy rule neatly: clone this node, then recursively clone each neighbor.
     * The map prevents infinite loops and preserves shared connections.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * map[original1] = clone1.
     * Later, when original1 appears again as a neighbor, return clone1 from map.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. If node is null, return null.
     * 2. Use HashMap original -> clone.
     * 3. DFS clone nodes and neighbor lists.
     * Time Complexity: O(V+E)
     * Space Complexity: O(V)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public Node optimized(Node node) {
        return clone(node, new HashMap<>());
    }


    private Node clone(Node node, Map<Node, Node> cloned) {
        if (node == null) {
            return null;
        }

        if (cloned.containsKey(node)) {
            return cloned.get(node);
        }

        Node copy = new Node(node.val);
        cloned.put(node, copy); // Save before cloning neighbors so cycles can reuse this copy.

        for (Node neighbor : node.neighbors) {
            copy.neighbors.add(clone(neighbor, cloned));
        }

        return copy;
    }

    static class Node {
        public int val;
        public List<Node> neighbors;

        public Node(int val) {
            this.val = val;
            this.neighbors = new ArrayList<>();
        }
    }
}
