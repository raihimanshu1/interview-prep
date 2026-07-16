package com.companywisejavasolutions.karat.solutions;

import java.util.*;

public class SubstitutionCipher {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Build a substitution alphabet from a key and use it to encode a message.
     *
     * INPUT
     * key and lowercase message.
     *
     * OUTPUT
     * Encoded message.
     *
     * EXAMPLE
     * key = "keyword", message = "abc xyz"
     * Output: key vzx
     * 
     * The key builds the cipher alphabet; every lowercase letter is replaced.
     *
     * WHAT IT MEANS
     * Unique key letters start the cipher alphabet, followed by remaining alphabet letters.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * A substitution cipher is a "replace every letter using a secret alphabet"
     * problem.
     *
     * The key does not encode the message by itself. The key first creates a new
     * alphabet.
     *
     * Example:
     *
     * key = "keyword"
     *
     * First take unique key letters:
     * k e y w o r d
     *
     * Then append the remaining letters of the normal alphabet:
     * a b c f g h i j l m n p q s t u v x z
     *
     * Cipher alphabet:
     * k e y w o r d a b c f g h i j l m n p q s t u v x z
     *
     * Now line up the normal alphabet and cipher alphabet:
     *
     * normal: a b c d e f g h i j k l m n o p q r s t u v w x y z
     * cipher: k e y w o r d a b c f g h i j l m n p q s t u v x z
     *
     * So:
     * a becomes k
     * b becomes e
     * c becomes y
     * x becomes v
     * y becomes x
     * z becomes z
     *
     * Spaces, punctuation, and other non-lowercase characters are copied as-is.
     *
     * What you need to know before solving:
     *
     * 1. The key can contain duplicate letters, but duplicates should be used only once.
     * 2. The final cipher alphabet must still contain exactly 26 lowercase letters.
     * 3. Each lowercase message letter is replaced by the letter at the same position
     *    in the cipher alphabet.
     * 4. Non-lowercase characters are not part of the alphabet mapping.
     *
     * What we will do in brute force:
     *
     * Build the cipher alphabet.
     * For every message character, scan the normal alphabet from left to right.
     * When we find the matching normal letter, append the cipher letter at that
     * same position.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Basic key and spaced message
     *
     * key = "keyword"
     * message = "abc xyz"
     *
     * Output:
     * "key vzx"
     *
     * Why:
     * a->k, b->e, c->y, x->v, y->x, z->z. The space stays a space.
     *
     * Example 2 - Repeated key letters
     *
     * key = "banana"
     * message = "cab"
     *
     * Cipher alphabet starts:
     * b a n c d e ...
     *
     * Output:
     * "nba"
     *
     * Why:
     * The repeated a and n in banana do not get added again.
     *
     * Example 3 - Message contains punctuation
     *
     * key = "zebra"
     * message = "hello, world"
     *
     * Output:
     * "fbiil, ulpid"
     *
     * Why:
     * Lowercase letters are encoded. The comma and space are copied directly.
     *
     * Edge case 1 - Empty message
     *
     * key = "secret"
     * message = ""
     *
     * Output:
     * ""
     *
     * Why:
     * There are no characters to translate.
     *
     * Edge case 2 - Key has no lowercase letters
     *
     * key = "123"
     * message = "abc"
     *
     * Output:
     * "abc"
     *
     * Why:
     * The cipher alphabet falls back to the normal alphabet.
     */

    /*
     * BRUTE FORCE APPROACH IN PLAIN ENGLISH
     *
     * Build the secret alphabet once.
     *
     * Then for each message character:
     *
     * If it is not a lowercase letter, copy it.
     * If it is lowercase, search the normal alphabet to find its position.
     * Use that same position in the secret alphabet.
     *
     * This is brute force because every encoded letter performs a small search
     * through "abcdefghijklmnopqrstuvwxyz" instead of jumping straight by index.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. The key creates the cipher alphabet.
     * 2. Repeated letters in the key are used only once.
     * 3. Missing letters are filled from the normal alphabet.
     * 4. Non-lowercase characters are copied unchanged.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Build the cipher alphabet.
     * Then read the message one character at a time.
     * For a lowercase letter, find its normal alphabet position by scanning.
     * Append the cipher letter from that same position.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Build the cipher alphabet from the key.
     * 2. Create an empty answer.
     * 3. For every character in the message:
     *    a. If the character is not lowercase, append it unchanged.
     *    b. Otherwise, scan the normal alphabet until the character is found.
     *    c. Append the cipher alphabet character from the same index.
     * 4. Return the encoded message.
     *
     * Time Complexity: O(m * 26 + k), where m is message length and k is key length.
     * Space Complexity: O(m + 26), for the answer and cipher alphabet.
     */
    public String bruteForce(String key, String message) {

        // First build the secret alphabet from the key.
        // After this, index 0 tells us what 'a' becomes,
        // index 1 tells us what 'b' becomes, and so on.
        String alphabet = buildCipherAlphabet(key);

        // This is the plain alphabet we search through manually.
        // Brute force keeps this visible instead of using ch - 'a'.
        String normalAlphabet = "abcdefghijklmnopqrstuvwxyz";

        // Store the encoded message as we translate one character at a time.
        StringBuilder result = new StringBuilder();

        // Process the message exactly as a human would read it: left to right.
        for (char ch : message.toCharArray()) {

            // Only lowercase letters participate in the substitution table.
            if (ch >= 'a' && ch <= 'z') {

                // Search the normal alphabet until we find this exact letter.
                // Example: if ch is 'c', we stop at index 2.
                for (int i = 0; i < normalAlphabet.length(); i++) {
                    if (normalAlphabet.charAt(i) == ch) {

                        // Use the same index in the cipher alphabet.
                        // If normal[2] is 'c', cipher[2] is what 'c' becomes.
                        result.append(alphabet.charAt(i));

                        // Once translated, do not keep searching.
                        break;
                    }
                }
            } else {

                // Spaces, commas, digits, uppercase letters, etc. are not encoded.
                result.append(ch);
            }
        }

        return result.toString();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: build the substitution alphabet once, then translate each character in one pass.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: encode message using key-derived alphabet.
     * 2. Remove repeated work: index directly from letter to cipher alphabet.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: key = "keyword", message = "abc xyz" Output: key vzx The key builds the cipher alphabet; every lowercase letter is replaced.
     * Walk the records one by one and the expected result above is produced.
     */
    public String optimized(String key, String message) {
        String alphabet = buildCipherAlphabet(key);
        StringBuilder result = new StringBuilder();

        for (char ch : message.toCharArray()) {
        if (ch >= 'a' && ch <= 'z') {
                result.append(alphabet.charAt(ch - 'a'));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    private String buildCipherAlphabet(String key) {
        // Build the replacement alphabet from left to right.
        // At the end, cipher.charAt(0) is what 'a' maps to, charAt(1) is what 'b' maps to, etc.
        StringBuilder cipher = new StringBuilder();

        // HashSet lets us ask "have we seen this before?" in constant average time.
        // That matters because duplicate key letters should appear only once in the cipher alphabet.
        Set<Character> seen = new HashSet<>();

        // First consume the key because the problem says key letters get priority
        // at the front of the substitution alphabet.
        for (char ch : key.toCharArray()) {
            // Only lowercase letters are part of this cipher's alphabet.
            // seen.add(ch) returns true only the first time we encounter that letter.
        if (ch >= 'a' && ch <= 'z' && seen.add(ch)) {
                // Keep the first occurrence of each key letter in its original key order.
                cipher.append(ch);
            }
        }

        // Then fill in every lowercase letter that the key did not already use.
        // This guarantees the cipher alphabet still has all 26 letters exactly once.
        for (char ch = 'a'; ch <= 'z'; ch++) {
            // If the letter was already in the key prefix, seen.add returns false
            // and we skip it to avoid duplicate mappings.
        if (seen.add(ch)) {
                // Append unused normal alphabet letters in normal order.
                cipher.append(ch);
            }
        }

        // Return a complete 26-character substitution alphabet for the encoder.
        return cipher.toString();
    }

    public static void main(String[] args) {
        SubstitutionCipher solver = new SubstitutionCipher();

        String[] keys = {"keyword", "banana", "zebra"};
        String[] messages = {"abc xyz", "cab", "hello, world"};

        for (int i = 0; i < keys.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + solver.bruteForce(keys[i], messages[i]));
            System.out.println("optimized:  " + solver.optimized(keys[i], messages[i]));
            System.out.println();
        }
    }
}
