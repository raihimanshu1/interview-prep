package com.companywisejavasolutions.karat.solutions;

import java.util.*;

public class AdvancedMahjongGame {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given Mahjong-like numbered tiles, determine whether they can form exactly one pair plus valid groups of triples or consecutive runs.
     *
     * INPUT
     * tiles as numeric strings.
     *
     * OUTPUT
     * true if the hand is winning.
     *
     * EXAMPLE
     * tiles = ["1","1","2","3","4","5","5","5"]
     * Output: true
     * 
     * Use pair 1,1; run 2,3,4; triple 5,5,5.
     *
     * WHAT IT MEANS
     * Try each possible pair, then recursively remove triples or runs.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * A winning hand has one pair plus groups of three. A group of three can be:
     *
     * 1. A triple, like 5,5,5.
     * 2. A run, like 2,3,4.
     *
     * The original order of the input does not matter. What matters is how many
     * copies of each tile value we have. That is why both the brute-force and
     * optimized versions begin with a frequency map.
     *
     * What to know before solving:
     *
     * 1. Exactly one pair must be chosen.
     * 2. Every remaining tile must belong to a triple or a run.
     * 3. A tile cannot be reused after it has been removed for a pair or group.
     * 4. Trying the wrong pair can fail even when another pair succeeds.
     * 5. Backtracking is natural here because we make a choice, test it, and undo it.
     *
     * What we do to solve:
     *
     * Try every tile value that could be the pair. After removing that pair,
     * recursively consume the rest of the hand. At each step, look at the
     * smallest remaining tile. Since it must be used somehow, try removing it as
     * part of a triple or as the start of a run. If either path empties the hand,
     * the hand is winning.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - Pair, run, and triple
     *
     * tiles = {"1","1","2","3","4","5","5","5"}
     *
     * Output:
     * true
     *
     * Why:
     * Pair: 1,1
     * Run: 2,3,4
     * Triple: 5,5,5
     *
     * Example 2 - Pair plus two runs
     *
     * tiles = {"2","2","3","4","5","6","7","8"}
     *
     * Output:
     * true
     *
     * Why:
     * Pair: 2,2
     * Runs: 3,4,5 and 6,7,8
     *
     * Example 3 - Looks close but fails
     *
     * tiles = {"1","1","1","2","2","3","4","6"}
     *
     * Output:
     * false
     *
     * Why:
     * After any pair choice, the remaining tiles cannot all be grouped into triples or consecutive runs.
     *
     * Edge case 1 - Only a pair
     *
     * tiles = {"9","9"}
     *
     * Output:
     * true
     *
     * Why:
     * The pair requirement is satisfied and there are no remaining groups to form.
     *
     * Edge case 2 - No possible pair
     *
     * tiles = {"1","2","3","4","5"}
     *
     * Output:
     * false
     *
     * Why:
     * A winning hand must have exactly one pair.
    */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. A valid hand needs one pair plus valid groups.
     * 2. A pair means two equal tiles.
     * 3. A group usually means three equal tiles or a valid sequence.
     * 4. Brute force is allowed to try possible pairs and then test the rest.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Count the tiles.
     * Try each possible tile value as the pair.
     * Remove that pair temporarily.
     * Then recursively check whether all remaining tiles can be consumed in groups.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Count every tile value.
     * 2. For every tile value with count at least 2, temporarily remove two copies as the pair.
     * 3. Recursively check whether all remaining tiles can be removed as triples or runs.
     * 4. During recursion, choose the smallest remaining tile because it cannot be hidden behind smaller choices.
     * 5. Try removing three copies of that tile as a triple.
     * 6. Try removing that tile, tile + 1, and tile + 2 as a run.
     * 7. If a choice works, return true.
     * 8. If a choice fails, restore the counts and try the next choice.
     *
     * Time Complexity: Exponential in the number of tile groups because pair/group choices branch recursively.
     * Space Complexity: O(unique tile values) for tile counts and recursion depth.
     */
    public boolean bruteForce(String[] tiles) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (String tile : tiles) {
            int value = Integer.parseInt(tile);
            // The hand is easier to reason about as counts, not as original positions.
            counts.put(value, counts.getOrDefault(value, 0) + 1);
        }

        for (int tile : counts.keySet()) {
            // A winning hand needs exactly one pair, so try each value that can supply it.
            if (counts.get(tile) >= 2) {
                counts.put(tile, counts.get(tile) - 2);
                if (canFormGroups(counts)) {
                    return true;
                }
                // Restore the pair before trying a different pair candidate.
                counts.put(tile, counts.get(tile) + 2);
            }
        }
        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: keep counts and recursively consume the smallest remaining tile so the search stays controlled.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Use a frequency map instead of sorting every arrangement.
     * 2. Try a pair once, then recursively consume the smallest remaining tile.
     * 3. Prefer triples/runs based on current counts.
     * 4. Backtrack counts after each trial.
     * 
     * Time Complexity: Lower than brute force because repeated scanning is replaced with stored state.
     * Space Complexity: O(unique tile values) for the frequency map and backtracking recursion.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: tiles = ["1","1","2","3","4","5","5","5"] Output: true Use pair 1,1; run 2,3,4; triple 5,5,5.
     * Walk the records one by one and the expected result above is produced.
     */
    public boolean optimized(String[] tiles) {
        if (tiles.length % 3 != 2) {
            return false;
        }

        Map<Integer, Integer> counts = new HashMap<>();
        for (String tile : tiles) {
            int value = Integer.parseInt(tile);
            counts.put(value, counts.getOrDefault(value, 0) + 1);
        }

        for (int tile : counts.keySet()) {
        if (counts.get(tile) >= 2) {
                counts.put(tile, counts.get(tile) - 2);
        if (canFormGroups(counts)) {
                    return true;
                }
                counts.put(tile, counts.get(tile) + 2);
            }
        }
        return false;
    }

    private boolean canFormGroups(Map<Integer, Integer> counts) {
        // Pick the smallest tile that is still unused. If a winning grouping
        // exists, this tile must belong to either a triple or the start of a run.
        int first = -1;
        // Scan the count map to find that smallest remaining tile value.
        for (int tile : counts.keySet()) {
            // Ignore tiles whose count has dropped to 0 because they have
            // already been consumed by earlier pair/group choices.
            if (counts.get(tile) > 0 && (first == -1 || tile < first)) {
                // Keep the smallest positive tile seen so far.
                first = tile;
            }
        }
        // No tiles left means every tile was successfully placed into a group.
        if (first == -1) {
            return true;
        }
        // First try using the smallest remaining tile as a triple.
        if (counts.get(first) >= 3) {
            // Remove three copies temporarily to test the triple choice.
            counts.put(first, counts.get(first) - 3);
            // Recursively check whether the rest of the hand can be grouped
            // after committing to this triple.
            if (canFormGroups(counts)) {
                return true;
            }
            // The triple path failed, so restore the three tiles before trying
            // a different way to use the smallest tile.
            counts.put(first, counts.get(first) + 3);
        }
        // Then try using it as the start of a consecutive run.
        if (counts.getOrDefault(first + 1, 0) > 0 && counts.getOrDefault(first + 2, 0) > 0) {
            // Remove first, first+1, and first+2 because a Mahjong run consumes
            // exactly one of each consecutive value.
            counts.put(first, counts.get(first) - 1);
            counts.put(first + 1, counts.get(first + 1) - 1);
            counts.put(first + 2, counts.get(first + 2) - 1);
            // If the remaining tiles can all be grouped, this run choice proves
            // the hand is valid.
            if (canFormGroups(counts)) {
                return true;
            }
            // Restore the run so another grouping choice can be tested correctly.
            counts.put(first, counts.get(first) + 1);
            counts.put(first + 1, counts.get(first + 1) + 1);
            counts.put(first + 2, counts.get(first + 2) + 1);
        }
        // The smallest remaining tile could not be legally placed in any group,
        // so this partial hand cannot become a winning hand.
        return false;
    }

    public static void main(String[] args) {
        AdvancedMahjongGame game = new AdvancedMahjongGame();

        String[][] samples = {
                {"1", "1", "2", "3", "4", "5", "5", "5"},
                {"2", "2", "3", "4", "5", "6", "7", "8"},
                {"1", "1", "1", "2", "2", "3", "4", "6"}
        };

        for (int i = 0; i < samples.length; i++) {
            String[] tiles = samples[i];
            System.out.println("Sample " + (i + 1) + ": tiles = " + Arrays.toString(tiles));
            System.out.println("bruteForce = " + game.bruteForce(copyArray(tiles)));
            System.out.println("optimized = " + game.optimized(copyArray(tiles)));
            System.out.println();
        }
    }

    private static String[] copyArray(String[] values) {
        String[] copy = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            copy[i] = values[i];
        }
        return copy;
    }
}
