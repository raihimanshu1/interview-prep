

package com.companywisejavasolutions.ebay.solutions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ReconstructItinerary {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We are given flight tickets [from, to]. Use all tickets exactly once and
     * start from JFK. If multiple valid routes exist, return the lexicographically
     * smallest one.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Think of every airport as a folder of next airports sorted alphabetically.
     * We always try the smallest next airport first. Hierholzer's algorithm works
     * because this is an Euler path problem: every ticket is an edge used once.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build a graph from source to a min-heap of destinations.
     * 2. Start DFS from JFK.
     * 3. While the airport has outgoing tickets, consume the smallest ticket.
     * 4. Add airport to the front after all outgoing tickets are consumed.
     *
     * Time Complexity: O(E log E)
     * Space Complexity: O(E)
     */
    public List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<>();

        for (List<String> ticket : tickets) {
            graph.computeIfAbsent(ticket.get(0), key -> new PriorityQueue<>()).offer(ticket.get(1));
        }

        LinkedList<String> route = new LinkedList<>();
        dfs("JFK", graph, route);
        return route;
    }

    private void dfs(String airport, Map<String, PriorityQueue<String>> graph, LinkedList<String> route) {
        PriorityQueue<String> nextAirports = graph.get(airport);

        while (nextAirports != null && !nextAirports.isEmpty()) {
            dfs(nextAirports.poll(), graph, route);
        }

        route.addFirst(airport);
    }
}
