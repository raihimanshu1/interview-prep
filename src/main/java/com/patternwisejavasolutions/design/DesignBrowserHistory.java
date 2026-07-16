package com.patternwisejavasolutions.design;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DesignBrowserHistory {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: BrowserHistory("leetcode.com"), visit("google.com"),
     * back(1), forward(1)
     * Sample Output: "leetcode.com", "google.com"
     *
     * Build browser history with visit, back, and forward operations.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Browser history is like a row of pages with a finger pointing at the
     * current page. Visiting a new page erases everything ahead of the finger.
     * In the optimized design, the back stack is the pages behind the finger and
     * the forward stack is the pages ahead of it.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Store pages in a list. For back and forward, move one step at a time.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Keep a list of visited pages and a current index.
     * 2. On visit, remove every page after current index and append new page.
     * 3. On back, move current index left one step at a time.
     * 4. On forward, move current index right one step at a time.
     *
     * Time Complexity: visit O(n), back/forward O(steps)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * home = "a.com"
     * visit "b.com" -> [a,b], current b
     * back(1) -> current a
     * forward(1) -> current b
     */

    public static class BruteForce {
        private List<String> history = new ArrayList<>();
        private int currentIndex;

        public BruteForce(String homepage) {
            history.add(homepage);
        }

        public void visit(String url) {
            while (history.size() > currentIndex + 1) {
                history.remove(history.size() - 1);
            }

            history.add(url);
            currentIndex = history.size() - 1;
        }

        public String back(int steps) {
            while (steps > 0 && currentIndex > 0) {
                currentIndex--;
                steps--;
            }
            return history.get(currentIndex);
        }

        public String forward(int steps) {
            while (steps > 0 && currentIndex + 1 < history.size()) {
                currentIndex++;
                steps--;
            }
            return history.get(currentIndex);
        }
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Keep past pages in one stack and future pages in another. Back moves pages
     * from past to future; forward moves pages back from future to past.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep current page, a back stack, and a forward stack.
     * 2. On visit, push current to back stack and clear forward stack.
     * 3. On back, move current to forward stack and pop from back stack.
     * 4. On forward, move current to back stack and pop from forward stack.
     *
     * Time Complexity: O(steps) for back/forward, O(1) for visit
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * current a, visit b: back stack [a], current b.
     * back: future [b], current a.
     * forward: back [a], current b.
     */

    public static class Optimized {
        private String currentPage;
        private Stack<String> backPages = new Stack<>();
        private Stack<String> forwardPages = new Stack<>();

        public Optimized(String homepage) {
            currentPage = homepage;
        }

        public void visit(String url) {
            // The old current page becomes something we can go back to.
            backPages.push(currentPage);
            currentPage = url;
            // A new visit creates a new path, so old forward history disappears.
            forwardPages.clear();
        }

        public String back(int steps) {
            while (steps > 0 && !backPages.isEmpty()) {
                // Moving back makes the current page available to move forward to.
                forwardPages.push(currentPage);
                currentPage = backPages.pop();
                steps--;
            }
            return currentPage;
        }

        public String forward(int steps) {
            while (steps > 0 && !forwardPages.isEmpty()) {
                // Moving forward makes the current page part of back history again.
                backPages.push(currentPage);
                currentPage = forwardPages.pop();
                steps--;
            }
            return currentPage;
        }
    }
}
