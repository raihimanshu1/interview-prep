import java.util.ArrayList;
import java.util.List;

public class EncodeAndDecodeStrings {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Convert a list of strings into one string, then convert it back to the
     * exact same list.
     *
     * Sample Input:
     * ["neet", "code", "love"]
     *
     * Sample Output after encoding:
     * "4#neet4#code4#love"
     *
     * Sample Output after decoding:
     * ["neet", "code", "love"]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If we simply join words with a comma, a word containing a comma would
     * confuse decoding. So each word needs a clear label telling its length.
     *
     * This is a string parsing pattern: store enough structure in the encoded
     * text so decoding is unambiguous.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * A first working idea is to separate words using a special character like '|'.
     * But a normal word may also contain '|', so the baseline approach must escape
     * special characters.
     *
     * This is not "bad code"; it is the natural first complete design. It teaches
     * why delimiter-only encoding becomes messy and why the optimized length-prefix
     * idea is cleaner.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each string, double every slash and prefix separators with slash.
     * 2. Put a separator between encoded strings.
     * 3. While decoding, read characters and honor escape slashes.
     *
     * Time Complexity: O(total characters)
     * Space Complexity: O(total characters)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Input ["a|b", "c"] with separator '|'
     * Encode first word as "a\|b" so the inside '|' is not treated as a split.
     * Prefix the word count so [] and [""] are not confused.
     * Final encoded string is "2:a\|b|c".
     */
    public String bruteForceEncode(List<String> strs) {
        StringBuilder encoded = new StringBuilder();
        encoded.append(strs.size()).append(':');

        for (int i = 0; i < strs.size(); i++) {
            if (i > 0) {
                encoded.append('|');
            }

            for (char ch : strs.get(i).toCharArray()) {
                // Escape both the escape character and the separator.
                if (ch == '\\' || ch == '|') {
                    encoded.append('\\');
                }
                encoded.append(ch);
            }
        }

        return encoded.toString();
    }

    public List<String> bruteForceDecode(String s) {
        List<String> result = new ArrayList<>();
        int colon = s.indexOf(':');
        int expectedWords = Integer.parseInt(s.substring(0, colon));

        if (expectedWords == 0) {
            return result;
        }

        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (int i = colon + 1; i < s.length(); i++) {
            char ch = s.charAt(i);

            if (escaping) {
                current.append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if (ch == '|') {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        if (result.size() < expectedWords) {
            result.add(current.toString());
        }
        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Instead of escaping characters, write each string as:
     * length + '#' + actual string.
     *
     * The length tells the decoder exactly how many characters to read. It does
     * not matter what characters appear inside the string.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * Encoding:
     * 1. For each string, append its length.
     * 2. Append '#'.
     * 3. Append the string itself.
     *
     * Decoding:
     * 1. Read digits until '#'.
     * 2. Convert those digits to length.
     * 3. Read exactly length characters as the next string.
     *
     * Time Complexity: O(total characters)
     * Space Complexity: O(total characters)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Encoded = "4#neet4#code"
     * Read 4 before '#', then take next 4 chars -> "neet".
     * Read next 4 before '#', then take next 4 chars -> "code".
     */
    public String optimizedEncode(List<String> strs) {
        StringBuilder encoded = new StringBuilder();

        for (String str : strs) {
            // The length is the promise that tells decode how many chars to copy.
            encoded.append(str.length()).append('#').append(str);
        }

        return encoded.toString();
    }

    public List<String> optimizedDecode(String s) {
        List<String> result = new ArrayList<>();
        int index = 0;

        while (index < s.length()) {
            int delimiter = index;

            while (s.charAt(delimiter) != '#') {
                delimiter++;
            }

            int length = Integer.parseInt(s.substring(index, delimiter));
            int wordStart = delimiter + 1;
            String word = s.substring(wordStart, wordStart + length);
            result.add(word);

            // Jump exactly over the word we just decoded.
            index = wordStart + length;
        }

        return result;
    }
}
