
package com.companywisejavasolutions.wellsFargo.solutions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TransactionStreamTopKRiskyAccounts {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given account risk-score updates, return the top k accounts by total risk
     * score. If scores tie, smaller account id comes first.
     *
     * INPUT
     * accounts[i] is the account receiving scoreDelta[i].
     * scoreDelta[i] is the risk score change from one transaction/event.
     * k is the number of accounts to return.
     *
     * OUTPUT
     * Account ids ordered by highest total score, then account id ascending.
     *
     * EXAMPLE
     * accounts = ["A", "B", "A", "C"]
     * scoreDelta = [10, 7, 5, 20], k = 2
     * totals: A=15, B=7, C=20
     * Output: [C, A]
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * Fraud/risk systems often maintain a watchlist.
     *
     * The watchlist does not need every account sorted all the time. It needs
     * the top k accounts after scores are aggregated.
     *
     * First step is always counting/aggregating:
     * account -> total risk score.
     *
     * Then we rank accounts by total score.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Tie by id:
     * A=10, B=10, k=2 -> [A, B]
     *
     * Example 2 - k larger than account count:
     * A=3, B=4, k=5 -> [B, A]
     *
     * Example 3 - Negative adjustment:
     * A=10 then A=-4 -> A total is 6.
     *
     * Edge case - Empty input returns [].
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Aggregate all scores, sort every account by best order, take first k.
     *
     * Time Complexity: O(n + u log u), where u is unique account count.
     * Space Complexity: O(u)
     */
    public List<String> bruteForce(String[] accounts, int[] scoreDelta, int k) {
        Map<String, Integer> scoreByAccount = buildScores(accounts, scoreDelta);

        List<String> allAccounts = new ArrayList<>(scoreByAccount.keySet());

        // Sorting all accounts is simple and correct, but may be wasteful when k is small.
        allAccounts.sort((first, second) -> compareBest(first, second, scoreByAccount));

        return new ArrayList<>(allAccounts.subList(0, Math.min(k, allAccounts.size())));
    }

    /*
     * OPTIMIZED APPROACH
     *
     * The repeated work in brute force is fully sorting accounts we will never
     * return.
     *
     * Keep a min-heap of size k. The heap root is the weakest account among
     * the current best k. When heap grows beyond k, remove that weakest account.
     *
     * Time Complexity: O(n + u log k)
     * Space Complexity: O(u + k)
     */
    public List<String> optimized(String[] accounts, int[] scoreDelta, int k) {
        Map<String, Integer> scoreByAccount = buildScores(accounts, scoreDelta);

        PriorityQueue<String> heap = new PriorityQueue<>(
                (first, second) -> compareWorst(first, second, scoreByAccount));

        for (String account : scoreByAccount.keySet()) {
            heap.offer(account);

            if (heap.size() > k) {
                heap.poll();
            }
        }

        List<String> answer = new ArrayList<>(heap);
        answer.sort((first, second) -> compareBest(first, second, scoreByAccount));
        return answer;
    }

    private Map<String, Integer> buildScores(String[] accounts, int[] scoreDelta) {
        Map<String, Integer> scoreByAccount = new HashMap<>();

        for (int i = 0; i < accounts.length; i++) {
            scoreByAccount.put(accounts[i], scoreByAccount.getOrDefault(accounts[i], 0) + scoreDelta[i]);
        }

        return scoreByAccount;
    }

    private int compareBest(String first, String second, Map<String, Integer> scoreByAccount) {
        int scoreCompare = Integer.compare(scoreByAccount.get(second), scoreByAccount.get(first));

        if (scoreCompare != 0) {
            return scoreCompare;
        }

        return first.compareTo(second);
    }

    private int compareWorst(String first, String second, Map<String, Integer> scoreByAccount) {
        int scoreCompare = Integer.compare(scoreByAccount.get(first), scoreByAccount.get(second));

        if (scoreCompare != 0) {
            return scoreCompare;
        }

        return second.compareTo(first);
    }

    public static void main(String[] args) {
        TransactionStreamTopKRiskyAccounts solver = new TransactionStreamTopKRiskyAccounts();

        String[] accounts = {"A", "B", "A", "C"};
        int[] scores = {10, 7, 5, 20};

        System.out.println("Brute force: " + solver.bruteForce(accounts, scores, 2));
        System.out.println("Optimized: " + solver.optimized(accounts, scores, 2));

        String[] tieAccounts = {"B", "A"};
        int[] tieScores = {10, 10};
        System.out.println("Tie: " + solver.optimized(tieAccounts, tieScores, 2));
    }
}
