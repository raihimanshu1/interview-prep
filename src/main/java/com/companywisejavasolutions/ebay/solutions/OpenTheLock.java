

package com.companywisejavasolutions.ebay.solutions;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class OpenTheLock {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * A lock has four wheels from 0 to 9. Starting at 0000, find the fewest moves
     * to reach target without entering deadend states.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Every lock state is a graph node. One move turns one wheel up or down.
     * Breadth-first search finds the shortest number of turns.
     */
    public int openLock(String[] deadends, String target) {
        Set<String> blocked = new HashSet<>();
        for (String deadend : deadends) blocked.add(deadend);
        if (blocked.contains("0000")) return -1;

        Queue<String> queue = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();
        queue.offer("0000");
        seen.add("0000");
        int moves = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String current = queue.poll();
                if (current.equals(target)) return moves;

                for (String next : neighbors(current)) {
                    if (!blocked.contains(next) && seen.add(next)) {
                        queue.offer(next);
                    }
                }
            }
            moves++;
        }

        return -1;
    }

    private java.util.List<String> neighbors(String state) {
        java.util.List<String> result = new java.util.ArrayList<>();
        char[] chars = state.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char original = chars[i];
            chars[i] = original == '9' ? '0' : (char) (original + 1);
            result.add(new String(chars));
            chars[i] = original == '0' ? '9' : (char) (original - 1);
            result.add(new String(chars));
            chars[i] = original;
        }

        return result;
    }
}
