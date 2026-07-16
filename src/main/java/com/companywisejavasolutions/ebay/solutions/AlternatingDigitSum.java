package com.companywisejavasolutions.ebay.solutions;

public class AlternatingDigitSum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Take the digits of a number from left to right. Add the first digit,
     * subtract the second, add the third, and so on.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The sign keeps switching like plus, minus, plus, minus. Converting to a
     * string makes left-to-right traversal natural.
     */

    /*
     * Time Complexity: O(d), where d is number of digits
     * Space Complexity: O(d)
     */
    public int alternateDigitSum(int n) {
        String digits = String.valueOf(n);
        int sum = 0;
        int sign = 1;

        for (int i = 0; i < digits.length(); i++) {
            sum += sign * (digits.charAt(i) - '0');
            sign *= -1;
        }

        return sum;
    }
}
