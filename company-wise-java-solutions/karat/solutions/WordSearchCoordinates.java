package karat.solutions;

import java.util.*;

public class WordSearchCoordinates {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a character grid and a word, return coordinates of a path that spells the word using adjacent cells.
     *
     * INPUT
     * board and word.
     *
     * OUTPUT
     * List of coordinates, or empty list if not found.
     *
     * EXAMPLE
     * board = {{'C','A','T'}, {'D','O','G'}, {'P','I','G'}}, word = "CAT"
     * Output: [(0,0), (0,1), (0,2)]
     * 
     * The board has multiple letters, but the returned path follows the target word only.
     *
     * WHAT IT MEANS
     * Backtrack through matching adjacent cells and store the path.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * Think of tracing a word with your finger on a letter board.
     *
     * You can begin at any cell, but after that each next letter must be adjacent
     * to the previous one. For this problem, adjacent means up, down, left, or
     * right. The same cell cannot be used twice in one attempted path.
     *
     * Example 1 - Word found in one row
     *
     * board = {
     *     {'C', 'A', 'T'},
     *     {'D', 'O', 'G'},
     *     {'P', 'I', 'G'}
     * }
     * word = "CAT"
     *
     * Output:
     * {(0, 0), (0, 1), (0, 2)}
     *
     * Why:
     * C, A, and T are next to each other from left to right.
     *
     * Example 2 - Word turns a corner
     *
     * board = {
     *     {'C', 'A', 'X'},
     *     {'X', 'R', 'T'}
     * }
     * word = "CAR"
     *
     * Output:
     * {(0, 0), (0, 1), (1, 1)}
     *
     * Why:
     * The path goes right from C to A, then down to R.
     *
     * Example 3 - Word is not present
     *
     * board = {
     *     {'A', 'B'},
     *     {'C', 'D'}
     * }
     * word = "ABE"
     *
     * Output:
     * {}
     *
     * Why:
     * E does not exist on the board, so no path can spell the word.
     *
     * Edge case 1 - Single-letter word
     *
     * If word = "D" and D appears on the board, return that one coordinate.
     *
     * Edge case 2 - Reusing a cell would be required
     *
     * board = {{'A', 'B'}}
     * word = "ABA"
     *
     * Output = {}
     *
     * The only A would have to be used twice, which is not allowed in one path.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Try every cell as a possible starting point.
     * 2. A path succeeds only if the current cell matches the current character.
     * 3. Mark a cell visited while it is part of the current path.
     * 4. Unmark it when backing out so another path can use it later.
     * 5. The path list should grow when we choose a cell and shrink when that
     *    choice fails.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Start a depth-first search from each board cell.
     * At each step, match one character of the word.
     * If the character matches, add the coordinate and try four neighbors for
     * the next character.
     * If none of those paths work, remove the coordinate and try a different way.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of tracing a word on a letter board. Each next letter must be
     * adjacent, and the same cell cannot be reused in one path.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    A word path is built one character at a time through neighboring cells.
     *
     * 2. What data structure does that naturally suggest?
     *    Use backtracking with visited cells because a path cannot reuse the same cell.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: start from every cell and try all paths.
     *
     * 4. What repeated work should I remove?
     *    Optimized: stop early when the next character does not match and backtrack cleanly.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: start from every cell and try all paths.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Create an empty path and visited grid.
     * 2. Try every board cell as the first character.
     * 3. From a candidate cell, recursively match the current character.
     * 4. Try down, up, right, and left for the next character.
     * 5. Return the path as soon as one complete word is found.
     * 6. If every start fails, return an empty list.
     * 
     * Time Complexity: O(rows * cols * 4^wordLength) in the worst case.
     * Space Complexity: O(rows * cols + wordLength) for visited and the recursion path.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the 3x3 board above.
     * Start at C (0,0), move right to A (0,1), then right to T (0,2).
     * Final path: [(0,0), (0,1), (0,2)].
     */
    public List<int[]> bruteForce(char[][] board, String word) {
        // visited protects the current path from using the same cell twice.
        boolean[][] visited = new boolean[board.length][board[0].length];

        // path stores the coordinates in the exact order that spells the word.
        List<int[]> path = new ArrayList<>();

        // Any cell could be the first letter, so brute force tries all of them.
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {

                // dfs returns true only when it has filled path with a complete match.
                if (dfs(board, word, 0, row, col, visited, path)) {
                    return path;
                }
            }
        }

        // No starting position produced a full path.
        return new ArrayList<>();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: stop early when the next character does not match and backtrack cleanly.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: return coordinates forming a word in a board.
     * 2. Remove repeated work: precheck character counts and start DFS only from matching first-letter cells.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the 3x3 board above.
     * Start at C (0,0), move right to A (0,1), then right to T (0,2).
     * Final path: [(0,0), (0,1), (0,2)].
     */
    public List<int[]> optimized(char[][] board, String word) {

        if (!hasEnoughCharacters(board, word)) {
            return new ArrayList<>();
        }

        boolean[][] visited = new boolean[board.length][board[0].length];
        List<int[]> path = new ArrayList<>();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] == word.charAt(0)
                        && dfs(board, word, 0, row, col, visited, path)) {
                    return path;
                }
            }
        }

        return new ArrayList<>();
    }

    private boolean dfs(char[][] board, String word, int index, int row, int col,
            boolean[][] visited, List<int[]> path) {
        // If index reached the word length, every character has already been matched.
        // The path list now contains a complete coordinate sequence.
        if (index == word.length()) {
            return true;
        }
        // Reject coordinates outside the board before reading board[row][col].
        // Also reject cells already used in this path, because a word path cannot reuse a square.
        // Finally, reject letters that do not match the character we are trying to place now.
        if (row < 0 || row == board.length || col < 0 || col == board[0].length
                || visited[row][col] || board[row][col] != word.charAt(index)) {
            return false;
        }

        // Mark this cell as chosen for the current path.
        visited[row][col] = true;
        // Store the coordinate in order, so a successful result can be returned directly to the caller.
        path.add(new int[] {row, col});
        // Try to match the next character from each of the four neighboring cells.
        // The OR chain means we stop as soon as one direction completes the word.
        boolean found = dfs(board, word, index + 1, row + 1, col, visited, path)
                || dfs(board, word, index + 1, row - 1, col, visited, path)
                || dfs(board, word, index + 1, row, col + 1, visited, path)
                || dfs(board, word, index + 1, row, col - 1, visited, path);
        // If none of the four directions worked, this coordinate was a wrong turn.
        if (!found) {
            // Remove it so the path again represents only the choices that are still alive.
            path.remove(path.size() - 1);
        }
        // Unmark the cell when leaving the call.
        // If found is true, the path keeps the coordinate, but visited still resets for clean caller state.
        visited[row][col] = false;
        // Tell the caller whether this branch produced a complete word.
        return found;
    }

    private boolean hasEnoughCharacters(char[][] board, String word) {
        // Use 256 buckets so common ASCII characters can be counted directly by their char value.
        int[] boardCount = new int[256];
        // wordCount stores how many times the target word needs each character.
        int[] wordCount = new int[256];

        // Count every character available on the board.
        for (char[] row : board) {
            // Each row contributes all of its cells to the available-character inventory.
            for (char ch : row) {
                // The character value itself is the bucket index.
                boardCount[ch]++;
            }
        }
        // Count every character required by the target word.
        for (char ch : word.toCharArray()) {
            // Each occurrence matters; needing two A's requires two board cells containing A.
            wordCount[ch]++;
        }
        // Compare the requirement inventory against the board inventory.
        for (int i = 0; i < wordCount.length; i++) {
            // If the word needs more of any character than the board has, DFS cannot possibly succeed.
            if (wordCount[i] > boardCount[i]) {
                return false;
            }
        }
        // The board has enough raw characters; adjacency still has to be proven by DFS.
        return true;
    }

    public static void main(String[] args) {
        WordSearchCoordinates solver = new WordSearchCoordinates();

        char[][][] boards = {
                {
                        {'C', 'A', 'T'},
                        {'D', 'O', 'G'},
                        {'P', 'I', 'G'}
                },
                {
                        {'C', 'A', 'X'},
                        {'X', 'R', 'T'}
                },
                {
                        {'A', 'B'},
                        {'C', 'D'}
                }
        };
        String[] words = {"CAT", "CAR", "ABE"};

        for (int i = 0; i < boards.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + format(solver.bruteForce(copyBoard(boards[i]), words[i])));
            System.out.println("optimized:  " + format(solver.optimized(copyBoard(boards[i]), words[i])));
            System.out.println();
        }
    }

    private static char[][] copyBoard(char[][] board) {
        char[][] copy = new char[board.length][board[0].length];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                copy[row][col] = board[row][col];
            }
        }
        return copy;
    }

    private static String format(List<int[]> coordinates) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < coordinates.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(java.util.Arrays.toString(coordinates.get(i)));
        }
        builder.append("]");
        return builder.toString();
    }
}
