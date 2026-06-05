package karat.solutions;

import java.util.*;

public class AdConversionRate {


    // directly look optimised approach

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * You are given three datasets: completed purchase user IDs, ad click logs, and a mapping from user IDs to IP addresses. For every ad, return how many users who clicked that ad later completed a purchase, along with total clicks for that ad.
     *
     * INPUT
     * completedPurchaseUserIds: user IDs that purchased.
     * adClicks: strings in the form "IP,Time,Ad Text".
     * allUserIps: strings in the form "UserId,IP".
     *
     * OUTPUT
     * A map from ad text to "conversions of clicks".
     *
     * EXAMPLE
     * completedPurchaseUserIds = ["u1", "u3"]
     * adClicks = [
     *     "1.1.1.1,2016-11-03 11:41:19,AdA",
     *     "2.2.2.2,2016-11-03 11:42:00,AdA",
     *     "3.3.3.3,2016-11-03 11:45:00,AdB",
     *     "4.4.4.4,2016-11-03 11:46:00,AdB"
     * ]
     * allUserIps = ["u1,1.1.1.1", "u2,2.2.2.2", "u3,3.3.3.3", "u4,4.4.4.4"]
     * Output: {AdA=1 of 2, AdB=1 of 2}
     * 
     * Here u1 and u3 purchased. u2 and u4 clicked but did not purchase.
     *
     * WHAT IT MEANS
     * Map IP to user, count every click by ad, and count a conversion when the clicking IP belongs to a purchasing user.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of matching a receipt to a person. A click gives an IP address, the IP
     * table tells us the user, and the purchase list tells us whether that user
     * bought something. The ad is the bucket where we count clicks and
     * conversions.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * The data is split across three separate lists, so the main challenge is not
     * the counting itself. The challenge is connecting the same real-world person
     * across different identifiers.
     *
     * A click log gives us:
     * IP address -> time -> ad text
     *
     * The purchase list gives us:
     * user id values that purchased
     *
     * The IP table is the bridge:
     * user id -> IP address
     *
     * For each click, we ask three plain questions:
     * 1. Which ad was clicked?
     * 2. Which user owns the clicking IP?
     * 3. Is that user in the completed purchase list?
     *
     * Every click always increases the ad's total click count. A click increases
     * the conversion count only when the clicking IP maps to a user who purchased.
     */

    /*
     * EXAMPLES TO UNDERSTAND THE PROBLEM
     *
     * Example 1 - Two ads, one conversion each
     * completedPurchaseUserIds = ["u1", "u3"]
     * adClicks = [
     *   "1.1.1.1,2016-11-03 11:41:19,AdA",
     *   "2.2.2.2,2016-11-03 11:42:00,AdA",
     *   "3.3.3.3,2016-11-03 11:45:00,AdB"
     * ]
     * allUserIps = ["u1,1.1.1.1", "u2,2.2.2.2", "u3,3.3.3.3"]
     * Output: {AdA=1 of 2, AdB=1 of 1}
     *
     * Example 2 - Same purchaser clicks the same ad twice
     * completedPurchaseUserIds = ["u1"]
     * adClicks = [
     *   "1.1.1.1,2016-11-03 10:00:00,Shoes",
     *   "1.1.1.1,2016-11-03 10:05:00,Shoes"
     * ]
     * allUserIps = ["u1,1.1.1.1"]
     * Output: {Shoes=2 of 2}
     * Each click is counted. The problem counts converting clicks, not unique buyers.
     *
     * Example 3 - Purchaser exists, but did not click a specific ad
     * completedPurchaseUserIds = ["u9"]
     * adClicks = ["5.5.5.5,2016-11-03 10:00:00,Travel"]
     * allUserIps = ["u5,5.5.5.5", "u9,9.9.9.9"]
     * Output: {Travel=0 of 1}
     *
     * Edge Case 1 - Unknown IP
     * If a click IP does not appear in allUserIps, we still count the click, but
     * it cannot become a conversion because we cannot connect it to a purchasing user.
     *
     * Edge Case 2 - No clicks
     * If adClicks is empty, there are no ads to report, so the result is empty.
     *
     * Edge Case 3 - Ad text contains commas
     * We split click rows with a limit of 3, so the third field remains the full
     * ad text even if that text itself contains commas.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * - "Conversion" means the click's IP belongs to a user id in the completed purchase list.
     * - The timestamp is present in the input but not needed for this calculation.
     * - The output should include every ad that received at least one click.
     * - We need two counters per ad: total clicks and converting clicks.
     * - In brute force, it is okay to rescan the raw lists because the goal is clarity first.
     */

    /*
     * WHAT WE DO TO SOLVE IT
     *
     * For each click, first count it under its ad. Then search the IP mapping to
     * find the user for that IP. Then search the purchase list to decide whether
     * the user converted. Finally, format the two counters as "conversions of clicks".
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    Read the three lists as a join problem: IP tells us who clicked, user id tells us who purchased, and ad name is what we count.
     *
     * 2. What data structure does that naturally suggest?
     *    Build ip -> user first, because every click starts with an IP address and we need to know whether that IP belongs to a purchasing user.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: for each click, scan all IP mappings to find the user, then scan purchases to decide if it converted.
     *
     * 4. What repeated work should I remove?
     *    Optimized: store IP lookup and purchaser lookup in maps/sets so every click can be counted in one pass.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: for each click, scan all IP mappings to find the user, then scan purchases to decide if it converted.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create two maps:
     *    clicksByAd counts every click for each ad.
     *    conversionsByAd counts only clicks from users who purchased.
     * 2. For every raw click string:
     *    parse the IP and ad name.
     * 3. Immediately increment total clicks for that ad.
     * 4. Scan all user/IP rows until we find the user who owns the click IP.
     * 5. Scan completedPurchaseUserIds to see whether that user purchased.
     * 6. If they purchased, increment that ad's conversion count.
     * 7. Build the final map using strings like "2 of 5".
     * 
     * Time Complexity: O(clicks * (users + purchases)) because each click scans lookup lists.
     * Space Complexity: O(number of ads) for click and conversion counters.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * completedPurchaseUserIds = ["u1", "u3"]
     * adClicks has 4 clicks: two for AdA and two for AdB.
     * allUserIps maps four IP addresses to u1, u2, u3, and u4.
     * 
     * Click 1: 1.1.1.1 -> u1 -> purchased -> AdA conversion count becomes 1.
     * Click 2: 2.2.2.2 -> u2 -> not purchased -> AdA click count only.
     * Click 3: 3.3.3.3 -> u3 -> purchased -> AdB conversion count becomes 1.
     * Click 4: 4.4.4.4 -> u4 -> not purchased -> AdB click count only.
     * 
     * Final answer: {AdA=1 of 2, AdB=1 of 2}
     */
    public Map<String, String> bruteForce(
    String[] completedPurchaseUserIds, String[] adClicks, String[] allUserIps) {

        Map<String, Integer> clicksByAd = new HashMap<>();
        Map<String, Integer> conversionsByAd = new HashMap<>();

        for (String rawClick : adClicks) {
            // Each row has IP, timestamp, and ad text. The timestamp does not affect
            // conversion rate, so we parse only the IP and ad text we need.
            String[] clickParts = rawClick.split(",", 3);
            String clickIp = clickParts[0];
            String ad = clickParts[2];

            // Every row is a real click, even if the IP is unknown or the user never purchased.
            clicksByAd.put(ad, clicksByAd.getOrDefault(ad, 0) + 1);

            // Keep a zero ready so ads with clicks but no conversions still appear as "0 of N".
            conversionsByAd.putIfAbsent(ad, 0);

            String userForClick = null;
            for (String userIp : allUserIps) {
                String[] ipParts = userIp.split(",");
                // Brute force bridge: scan user/IP rows until this click IP reveals a user id.
                if (ipParts[1].equals(clickIp)) {
                    userForClick = ipParts[0];
                    break;
                }
            }

            boolean purchased = false;
            for (String completedUserId : completedPurchaseUserIds) {
                // A click converts only if its user id is present in the purchase list.
                if (completedUserId.equals(userForClick)) {
                    purchased = true;
                    break;
                }
            }

            if (purchased) {
                // Same user clicking twice counts as two converting clicks in this brute-force model.
                conversionsByAd.put(ad, conversionsByAd.get(ad) + 1);
            }
        }

        Map<String, String> result = new HashMap<>();
        for (String ad : clicksByAd.keySet()) {
            // Match the requested presentation: "converted clicks of total clicks".
            result.put(ad, conversionsByAd.get(ad) + " of " + clicksByAd.get(ad));
        }

        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: store IP lookup and purchaser lookup in maps/sets so every click can be counted in one pass.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Prebuild IP -> user so a click IP becomes a user in O(1).
     * 2. Prebuild a purchase set so conversion lookup is O(1).
     * 3. Process every click once and update ad counters.
     * 4. Return the same conversion-rate strings.
     * 
     * Time Complexity: Lower than brute force because repeated scanning is replaced with stored state.
     * Space Complexity: O(users + purchases + ads) for lookup maps and counters.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Build ipToUser:
     * 1.1.1.1 -> u1, 2.2.2.2 -> u2, 3.3.3.3 -> u3, 4.4.4.4 -> u4.
     * Build completedPurchase set: {u1, u3}.
     * 
     * Now each click becomes O(1) lookup:
     * AdA gets 2 clicks and 1 conversion.
     * AdB gets 2 clicks and 1 conversion.
     * 
     * Final answer: {AdA=1 of 2, AdB=1 of 2}
     */
    public Map<String, String> optimized(
    String[] completedPurchaseUserIds, String[] adClicks, String[] allUserIps) {
        Map<String, String> ipToUser = new HashMap<>();
        Map<String, Integer> clicksByAd = new HashMap<>();
        Map<String, Integer> conversionsByAd = new HashMap<>();
        Map<String, Boolean> completedPurchase = new HashMap<>();
        for (String userId : completedPurchaseUserIds) {
            completedPurchase.put(userId, true);
        }
        for (String userIp : allUserIps) {
            String[] parts = userIp.split(",");
            ipToUser.put(parts[1], parts[0]);
        }
        for (String rawClick : adClicks) {
            String[] parts = rawClick.split(",", 3);
            String ip = parts[0];
            String ad = parts[2];
            clicksByAd.put(ad, clicksByAd.getOrDefault(ad, 0) + 1);
            conversionsByAd.putIfAbsent(ad, 0);

            String userId = ipToUser.get(ip);
        if (userId != null && completedPurchase.containsKey(userId)) {
                conversionsByAd.put(ad, conversionsByAd.get(ad) + 1);
            }
        }
        Map<String, String> result = new HashMap<>();
        for (String ad : clicksByAd.keySet()) {
            result.put(ad, conversionsByAd.get(ad) + " of " + clicksByAd.get(ad));
        }
        return result;
    }

    public static void main(String[] args) {
        AdConversionRate calculator = new AdConversionRate();

        String[][] purchaseSamples = {
                {"u1", "u3"},
                {"u1"},
                {"u9"}
        };
        String[][] clickSamples = {
                {
                        "1.1.1.1,2016-11-03 11:41:19,AdA",
                        "2.2.2.2,2016-11-03 11:42:00,AdA",
                        "3.3.3.3,2016-11-03 11:45:00,AdB",
                        "4.4.4.4,2016-11-03 11:46:00,AdB"
                },
                {
                        "1.1.1.1,2016-11-03 10:00:00,Shoes",
                        "1.1.1.1,2016-11-03 10:05:00,Shoes"
                },
                {
                        "5.5.5.5,2016-11-03 10:00:00,Travel"
                }
        };
        String[][] ipSamples = {
                {"u1,1.1.1.1", "u2,2.2.2.2", "u3,3.3.3.3", "u4,4.4.4.4"},
                {"u1,1.1.1.1"},
                {"u5,5.5.5.5", "u9,9.9.9.9"}
        };

        for (int i = 0; i < purchaseSamples.length; i++) {
            System.out.println("Sample " + (i + 1) + ":");
            System.out.println("completedPurchaseUserIds = " + Arrays.toString(purchaseSamples[i]));
            System.out.println("adClicks = " + Arrays.toString(clickSamples[i]));
            System.out.println("allUserIps = " + Arrays.toString(ipSamples[i]));
            System.out.println("bruteForce = "
                    + formatMap(calculator.bruteForce(copyArray(purchaseSamples[i]), copyArray(clickSamples[i]), copyArray(ipSamples[i]))));
            System.out.println("optimized = "
                    + formatMap(calculator.optimized(copyArray(purchaseSamples[i]), copyArray(clickSamples[i]), copyArray(ipSamples[i]))));
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

    private static String formatMap(Map<String, String> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);

        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            String key = keys.get(i);
            builder.append(key).append("=").append(map.get(key));
        }
        builder.append("}");
        return builder.toString();
    }
}
