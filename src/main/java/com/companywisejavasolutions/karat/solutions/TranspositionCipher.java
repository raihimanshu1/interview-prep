

package com.companywisejavasolutions.karat.solutions;
public class TranspositionCipher {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Write a message row by row into a grid with fixed columns, then read the grid column by column.
     *
     * INPUT
     * message and number of columns.
     *
     * OUTPUT
     * Encoded transposed string.
     *
     * EXAMPLE
     * message = "abcdefghi", columns = 3
     * Output: adgbehcfi
     * 
     * Write rows as abc / def / ghi, then read down each column.
     *
     * WHAT IT MEANS
     * Rows abc/def become columns ad/be/cf.
     */
    /*
     * IN-DEPTH EXPLANATION
     *
     * A transposition cipher does not change the letters themselves.
     * It changes the positions where those letters are read.
     *
     * Think of writing a message into a table row by row.
     * Then, instead of reading the table row by row again, read it column by
     * column.
     *
     * Example:
     *
     * message = "abcdefghi"
     * columns = 3
     *
     * Write row by row:
     *
     * a b c
     * d e f
     * g h i
     *
     * Read column by column:
     *
     * first column:  a d g
     * second column: b e h
     * third column:  c f i
     *
     * Output:
     * "adgbehcfi"
     *
     * What you need to know before solving:
     *
     * 1. The number of columns controls the width of the grid.
     * 2. The number of rows depends on how many characters are in the message.
     * 3. The final row may be incomplete.
     * 4. We should not add fake padding characters to the final answer.
     *
     * What we will do in brute force:
     *
     * Build the entire grid exactly like the drawing above.
     * Put message characters into the grid row by row.
     * Use spaces as temporary blanks for cells beyond the end of the message.
     * Then scan the grid column by column and append real characters only.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Perfect rectangle
     *
     * message = "abcdefghi"
     * columns = 3
     *
     * Grid:
     * a b c
     * d e f
     * g h i
     *
     * Output:
     * "adgbehcfi"
     *
     * Example 2 - Last row is incomplete
     *
     * message = "abcdefg"
     * columns = 3
     *
     * Grid:
     * a b c
     * d e f
     * g _ _
     *
     * Output:
     * "adgbecf"
     *
     * Why:
     * The missing cells are ignored when reading columns.
     *
     * Example 3 - More columns than rows
     *
     * message = "hello"
     * columns = 4
     *
     * Grid:
     * h e l l
     * o _ _ _
     *
     * Output:
     * "hoell"
     *
     * Edge case 1 - Empty message
     *
     * message = ""
     * columns = 3
     *
     * Output:
     * ""
     *
     * Why:
     * There is no character to place in the grid.
     *
     * Edge case 2 - One column
     *
     * message = "karat"
     * columns = 1
     *
     * Output:
     * "karat"
     *
     * Why:
     * A one-column grid is already read top to bottom in the original order.
     */

    /*
     * BRUTE FORCE APPROACH IN PLAIN ENGLISH
     *
     * First make the table.
     *
     * Fill it the same way we write English text: left to right, row by row.
     *
     * Then read it in the cipher order: top to bottom, column by column.
     *
     * This is brute force because we store the full table even though the
     * optimized version can calculate each source position directly.
    */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. The message is written row by row into a grid.
     * 2. The encoded text is read column by column.
     * 3. Empty grid cells should not become real message characters.
     * 4. One column means the message stays in the same order.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Create the full grid that the problem describes.
     * Place each message character into the grid from left to right.
     * Then scan columns from top to bottom and append real characters.
     * This makes the transformation visible before optimizing it.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Compute how many rows are needed.
     * 2. Create a rows x columns grid.
     * 3. Fill the grid row by row with message characters.
     * 4. Put a temporary blank in grid cells after the message ends.
     * 5. Read the grid column by column.
     * 6. Append only non-blank cells to the answer.
     *
     * Time Complexity: O(rows * columns), which is O(n) plus possible blank cells.
     * Space Complexity: O(rows * columns), because brute force stores the grid.
     */
    public String bruteForce(String message, int columns) {

        // Ceiling division:
        // enough rows to hold every character when each row has "columns" slots.
        int rows = (message.length() + columns - 1) / columns;

        // The brute force version physically builds the table from the story.
        char[][] grid = new char[rows][columns];

        // Points to the next message character that still needs to be placed.
        int index = 0;

        // Fill the grid row by row, just like writing into a notebook table.
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                // If the message still has characters, place the next one.
                // Otherwise, mark this extra cell with a temporary blank.
                grid[row][col] = index < message.length() ? message.charAt(index++) : ' ';
            }
        }

        // Now collect the encoded result by reading down each column.
        StringBuilder result = new StringBuilder();
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {

                // Ignore blank padding cells from an incomplete final row.
                if (grid[row][col] != ' ') {
                    result.append(grid[row][col]);
                }
            }
        }
        return result.toString();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: compute positions directly or use a compact grid only once.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: write text by rows and read by columns.
     * 2. Remove repeated work: compute row/column indexes directly without storing the grid.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: message = "abcdefghi", columns = 3 Output: adgbehcfi Write rows as abc / def / ghi, then read down each column.
     * Walk the records one by one and the expected result above is produced.
     */
    public String optimized(String message, int columns) {

        int rows = (message.length() + columns - 1) / columns;
        StringBuilder result = new StringBuilder();

        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                int index = row * columns + col;

                // Direct indexing avoids allocating a separate 2D grid.
                if (index < message.length()) {
                    result.append(message.charAt(index));
                }
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        TranspositionCipher solver = new TranspositionCipher();

        String[] messages = {"abcdefghi", "abcdefg", "hello"};
        int[] columns = {3, 3, 4};

        for (int i = 0; i < messages.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + solver.bruteForce(messages[i], columns[i]));
            System.out.println("optimized:  " + solver.optimized(messages[i], columns[i]));
            System.out.println();
        }
    }
}
