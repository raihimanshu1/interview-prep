public class IntegerToEnglishWords {

    private static final String[] BELOW_TWENTY = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };
    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };
    private static final String[] THOUSANDS = {"", "Thousand", "Million", "Billion"};

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Convert a non-negative integer into English words.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Read the number in chunks of three digits: hundreds, thousands, millions,
     * billions. Convert each chunk and attach its scale word.
     */
    public String numberToWords(int num) {
        if (num == 0) {
            return "Zero";
        }

        StringBuilder result = new StringBuilder();
        int group = 0;

        while (num > 0) {
            int chunk = num % 1000;
            if (chunk != 0) {
                String words = convertChunk(chunk);
                if (!THOUSANDS[group].isEmpty()) {
                    words += " " + THOUSANDS[group];
                }
                result.insert(0, words + " ");
            }
            num /= 1000;
            group++;
        }

        return result.toString().trim();
    }

    private String convertChunk(int num) {
        if (num == 0) return "";
        if (num < 20) return BELOW_TWENTY[num];
        if (num < 100) {
            return (TENS[num / 10] + " " + convertChunk(num % 10)).trim();
        }
        return (BELOW_TWENTY[num / 100] + " Hundred " + convertChunk(num % 100)).trim();
    }
}
