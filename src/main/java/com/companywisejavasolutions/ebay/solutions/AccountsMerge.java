

package com.companywisejavasolutions.ebay.solutions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsMerge {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Accounts with common emails belong to the same person. Merge accounts and
     * return each name with sorted emails.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Emails are nodes. Emails in the same account are connected. Union Find
     * groups connected emails together.
     */
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        UnionFind unionFind = new UnionFind();
        Map<String, String> emailToName = new HashMap<>();

        for (List<String> account : accounts) {
            String name = account.get(0);
            String firstEmail = account.get(1);
            unionFind.add(firstEmail);
            emailToName.put(firstEmail, name);

            for (int i = 2; i < account.size(); i++) {
                String email = account.get(i);
                unionFind.add(email);
                emailToName.put(email, name);
                unionFind.union(firstEmail, email);
            }
        }

        Map<String, List<String>> rootToEmails = new HashMap<>();
        for (String email : emailToName.keySet()) {
            String root = unionFind.find(email);
            rootToEmails.computeIfAbsent(root, key -> new ArrayList<>()).add(email);
        }

        List<List<String>> answer = new ArrayList<>();
        for (List<String> emails : rootToEmails.values()) {
            Collections.sort(emails);
            List<String> merged = new ArrayList<>();
            merged.add(emailToName.get(emails.get(0)));
            merged.addAll(emails);
            answer.add(merged);
        }

        return answer;
    }

    private static class UnionFind {
        private final Map<String, String> parent = new HashMap<>();

        void add(String value) {
            parent.putIfAbsent(value, value);
        }

        String find(String value) {
            if (!parent.get(value).equals(value)) {
                parent.put(value, find(parent.get(value)));
            }
            return parent.get(value);
        }

        void union(String first, String second) {
            parent.put(find(first), find(second));
        }
    }
}
