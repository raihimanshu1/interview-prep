

package com.companywisejavasolutions.karat.solutions;
import java.util.Map;
import java.util.TreeMap;

public class DomainClickCount {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given visit counts for full domains, compute the total visit count for
     * every subdomain.
     *
     * Input:
     * countPairs contains strings like "900 mail.yahoo.com".
     *
     * Output:
     * A map where each key is a domain/subdomain and each value is its total
     * visit count.
     *
     * Example:
     * countPairs = ["900 mail.yahoo.com", "50 yahoo.com", "1 sports.yahoo.com"]
     * 
     * Output:
     * {com=951, mail.yahoo.com=900, sports.yahoo.com=1, yahoo.com=951}
     * 
     * Each full domain contributes to itself and each parent suffix.
     *
     * What It Means:
     * A visit to mail.yahoo.com also counts as a visit to yahoo.com and com.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Think of a postal address:
     * apartment -> building -> city.
     *
     * If one letter reaches the apartment, then that same letter also reached
     * the building and the city. Similarly, one visit to mail.yahoo.com counts
     * for mail.yahoo.com, yahoo.com, and com.
     *
     * So the real job is:
     * 1. Read the count.
     * 2. Break the domain into levels.
     * 3. Add the count to every suffix domain.
     */

    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     * 1. What do I notice first?
     *    Each row has two parts: a number and a full domain.
     *
     * 2. What should the answer be grouped by?
     *    The answer is grouped by subdomain name, so a map from subdomain to
     *    total count is the natural structure.
     *
     * 3. How do I build the brute force?
     *    Split the domain by dots, then use nested loops to rebuild every suffix.
     *    This is easy to see on paper, even though it repeatedly rebuilds strings.
     *
     * 4. How do I improve it?
     *    The optimized version avoids rebuilding suffixes part by part. It scans
     *    the original domain string and takes suffix substrings starting after
     *    each dot.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * A domain is layered from specific to general.
     *
     * mail.yahoo.com
     *
     * means:
     * mail.yahoo.com is the exact place,
     * yahoo.com is the parent place,
     * com is the broad top-level place.
     *
     * If 900 visits landed on mail.yahoo.com, those 900 visits also count for
     * yahoo.com and com. The visitor passed through every parent level.
     *
     * Brute force solves this in the most visible way:
     * split the domain into pieces, then rebuild every suffix by hand.
     * This makes the idea easy to explain even though it repeats string work.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - One three-part domain
     * countPairs = ["900 mail.yahoo.com"]
     * Contributions:
     * mail.yahoo.com += 900
     * yahoo.com += 900
     * com += 900
     * Output: {com=900, mail.yahoo.com=900, yahoo.com=900}
     *
     * Example 2 - Counts merge at parent domains
     * countPairs = ["900 mail.yahoo.com", "50 yahoo.com"]
     * mail.yahoo.com contributes 900 to mail.yahoo.com, yahoo.com, and com.
     * yahoo.com contributes 50 to yahoo.com and com.
     * Output: {com=950, mail.yahoo.com=900, yahoo.com=950}
     *
     * Example 3 - Different branches share the same parent
     * countPairs = ["10 maps.google.com", "20 mail.google.com"]
     * Both branches add to google.com and com.
     * Output: {com=30, google.com=30, mail.google.com=20, maps.google.com=10}
     *
     * Edge Case 1 - Already top-level
     * countPairs = ["7 org"]
     * There are no dots, so only org receives the count.
     * Output: {org=7}
     *
     * Edge Case 2 - Empty input
     * countPairs = []
     * No visits exist, so the result map is empty.
     * Output: {}
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Each input row has a number and a domain separated by a space.
     * 2. Dots split the domain into levels.
     * 3. Every suffix after splitting is a valid subdomain to count.
     * 4. A map is the natural place to accumulate totals because the same
     *    subdomain can be reached by many full domains.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * For every input row, we first separate the visit count from the domain.
     * Then we split the domain by dots.
     *
     * For "mail.yahoo.com", the starts are:
     * start at mail  -> mail.yahoo.com
     * start at yahoo -> yahoo.com
     * start at com   -> com
     *
     * Every rebuilt suffix gets the same visit count added to the answer map.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For "mail.yahoo.com", split into:
     * ["mail", "yahoo", "com"]
     *
     * Then build:
     * mail.yahoo.com
     * yahoo.com
     * com
     *
     * Add the same count to all three.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create a map for final counts.
     * 2. For each "count domain" row:
     *    a. Split into count and domain.
     *    b. Split domain by dot.
     *    c. For every starting part, rebuild the suffix domain.
     *    d. Add the count to that suffix in the map.
     * 3. Return the map.
     *
     * Time Complexity: O(n * p^2), where p is number of domain parts.
     * Space Complexity: O(k), where k is number of unique subdomains.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the multi-domain example above.
     * 900 mail.yahoo.com contributes to mail.yahoo.com, yahoo.com, and com.
     * 50 yahoo.com contributes to yahoo.com and com.
     * 1 sports.yahoo.com contributes to sports.yahoo.com, yahoo.com, and com.
     * Final answer: {com=951, mail.yahoo.com=900, sports.yahoo.com=1, yahoo.com=951}
     */
    public Map<String, Integer> bruteForce(String[] countPairs) {
        // TreeMap keeps output deterministic and easy to read in examples/tests.
        Map<String, Integer> countsByDomain = new TreeMap<>();

        // Handle each "count domain" record independently.
        for (String pair : countPairs) {
            // Split once so the number and the full domain are handled separately.
            String[] countAndDomain = pair.split(" ");
            // The left side is the number of visits.
            int count = Integer.parseInt(countAndDomain[0]);
            // The right side is split into domain levels like ["mail", "yahoo", "com"].
            String[] parts = countAndDomain[1].split("\\.");

            // Each starting position creates one suffix subdomain.
            for (int start = 0; start < parts.length; start++) {
                // In brute force, we rebuild the suffix manually from domain parts.
                StringBuilder suffix = new StringBuilder();

                // Join parts[start..end] with dots to form the current suffix.
                for (int index = start; index < parts.length; index++) {
                    if (suffix.length() > 0) {
                        suffix.append('.');
                    }
                    suffix.append(parts[index]);
                }

                String domain = suffix.toString();
                // Add this visit count to the running total for the suffix.
                countsByDomain.put(domain, countsByDomain.getOrDefault(domain, 0) + count);
            }
        }

        // Every full domain and parent suffix has now been counted.
        return countsByDomain;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force repeatedly creates suffixes by joining domain parts.
     * But the original string already contains all suffixes:
     *
     * mail.yahoo.com
     *      yahoo.com
     *            com
     *
     * So we add the full domain first, then every substring after a dot.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Create a map for final counts.
     * 2. For each row:
     *    a. Parse count and domain.
     *    b. Add count to the full domain.
     *    c. Walk through the domain string.
     *    d. Whenever a dot is found, add count to the suffix after that dot.
     * 3. Return the map.
     *
     * Time Complexity: O(total domain characters)
     * Space Complexity: O(k), where k is number of unique subdomains.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the multi-domain example above.
     * 900 mail.yahoo.com contributes to mail.yahoo.com, yahoo.com, and com.
     * 50 yahoo.com contributes to yahoo.com and com.
     * 1 sports.yahoo.com contributes to sports.yahoo.com, yahoo.com, and com.
     * Final answer: {com=951, mail.yahoo.com=900, sports.yahoo.com=1, yahoo.com=951}
     */
    public Map<String, Integer> optimized(String[] countPairs) {
        Map<String, Integer> countsByDomain = new TreeMap<>();

        for (String pair : countPairs) {
            String[] countAndDomain = pair.split(" ");
            int count = Integer.parseInt(countAndDomain[0]);
            String domain = countAndDomain[1];

            // The full domain always receives the visit count.
            addCount(countsByDomain, domain, count);

            for (int index = 0; index < domain.length(); index++) {
                // A dot means the text after it is a parent subdomain.
                if (domain.charAt(index) == '.') {
                    String suffix = domain.substring(index + 1);
                    addCount(countsByDomain, suffix, count);
                }
            }
        }

        return countsByDomain;
    }

    private void addCount(Map<String, Integer> countsByDomain, String domain, int count) {
        // Look up the total we have already collected for this domain suffix.
        // If this suffix has not appeared before, treat its previous total as 0.
        // Then add the current visit count because one full-domain visit also
        // belongs to every parent suffix from the problem statement.
        countsByDomain.put(domain, countsByDomain.getOrDefault(domain, 0) + count);
    }

    public static void main(String[] args) {
        DomainClickCount solution = new DomainClickCount();

        runSample(solution, new String[]{"900 mail.yahoo.com"});
        runSample(solution, new String[]{"900 mail.yahoo.com", "50 yahoo.com"});
        runSample(solution, new String[]{"10 maps.google.com", "20 mail.google.com"});
    }

    private static void runSample(DomainClickCount solution, String[] countPairs) {
        System.out.println("countPairs = " + formatArray(countPairs));
        System.out.println("bruteForce = " + solution.bruteForce(countPairs));
        System.out.println("optimized  = " + solution.optimized(countPairs));
        System.out.println();
    }

    private static String formatArray(String[] values) {
        StringBuilder output = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                output.append(", ");
            }
            output.append('"').append(values[i]).append('"');
        }
        output.append(']');
        return output.toString();
    }
}
