package com.companywisejavasolutions.ebay.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestoreTheArrayFromAdjacentPairs {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We are given adjacent pairs from an original array. Rebuild one valid
     * original array.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The original array is a chain. Endpoints have only one neighbor; middle
     * values have two neighbors. Start at an endpoint and keep walking forward.
     */
    public int[] restoreArray(int[][] adjacentPairs) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int[] pair : adjacentPairs) {
            graph.computeIfAbsent(pair[0], key -> new ArrayList<>()).add(pair[1]);
            graph.computeIfAbsent(pair[1], key -> new ArrayList<>()).add(pair[0]);
        }

        int start = 0;
        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            if (entry.getValue().size() == 1) {
                start = entry.getKey();
                break;
            }
        }

        int[] answer = new int[adjacentPairs.length + 1];
        answer[0] = start;
        answer[1] = graph.get(start).get(0);

        for (int i = 2; i < answer.length; i++) {
            List<Integer> neighbors = graph.get(answer[i - 1]);
            answer[i] = neighbors.get(0) == answer[i - 2] ? neighbors.get(1) : neighbors.get(0);
        }

        return answer;
    }
}
