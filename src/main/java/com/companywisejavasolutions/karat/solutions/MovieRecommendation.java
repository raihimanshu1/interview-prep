package com.companywisejavasolutions.karat.solutions;

import java.util.*;

public class MovieRecommendation {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given user movie ratings, recommend liked movies from similar users that the target user has not watched.
     *
     * INPUT
     * targetUser and ratings [user, movie, score].
     *
     * OUTPUT
     * List of recommended movie names.
     *
     * EXAMPLE
     * targetUser = "me"
     * ratings = [["me","A","5"], ["me","C","4"], ["u2","A","5"], ["u2","B","5"], ["u3","C","5"], ["u3","D","5"]]
     * Output: [B, D]
     * 
     * u2 and u3 share liked movies with the target, so their unseen liked movies are recommended.
     *
     * WHAT IT MEANS
     * Similar users share liked movies; recommend their other liked unseen movies.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of asking people with similar taste for suggestions. If they liked
     * something the target has not watched, it becomes a candidate recommendation.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * A movie recommendation is based on two ideas:
     *
     * First, we need to understand the target user's taste. In this solution,
     * a score greater than 3 means the user liked the movie.
     *
     * Second, we need to find other users who share at least one liked movie with
     * the target. Those users are considered similar enough to recommend from.
     *
     * Once a similar user is found, we look at that user's liked movies. Any liked
     * movie that the target has not already watched becomes a recommendation.
     *
     * So the flow is:
     * target liked movies -> similar users -> their liked movies -> remove watched movies.
     */

    /*
     * EXAMPLES TO UNDERSTAND THE PROBLEM
     *
     * Example 1 - Two similar users recommend one movie each
     * targetUser = "me"
     * ratings = [
     *   ["me","A","5"], ["me","C","4"],
     *   ["u2","A","5"], ["u2","B","5"],
     *   ["u3","C","5"], ["u3","D","5"]
     * ]
     * Output: [B, D]
     * u2 shares A with me. u3 shares C with me.
     *
     * Example 2 - Similar user likes a movie target already watched
     * targetUser = "me"
     * ratings = [
     *   ["me","A","5"], ["me","B","2"],
     *   ["u2","A","4"], ["u2","B","5"], ["u2","C","5"]
     * ]
     * Output: [C]
     * B is not recommended because the target already watched it, even though u2 liked it.
     *
     * Example 3 - Other user likes movies, but shares no liked movie with target
     * targetUser = "me"
     * ratings = [
     *   ["me","A","5"],
     *   ["u2","B","5"], ["u2","C","5"]
     * ]
     * Output: []
     * u2 is not similar because there is no liked movie overlap.
     *
     * Edge Case 1 - Target has no liked movies
     * If the target never rates anything above 3, nobody can share a liked movie
     * with them, so recommendations are empty.
     *
     * Edge Case 2 - Duplicate recommendation source
     * If multiple similar users recommend the same unseen movie, the HashSet keeps
     * it once in the answer.
     *
     * Edge Case 3 - Target user missing from ratings
     * With no target rows, target liked/watched sets are empty, so the result is empty.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * - A liked movie means score > 3.
     * - A watched movie is any movie the target has rated, even with a low score.
     * - A similar user needs at least one liked movie in common with the target.
     * - We recommend only liked movies from similar users.
     * - We never recommend a movie the target already watched.
     */

    /*
     * WHAT WE DO TO SOLVE IT
     *
     * First collect what the target liked and watched. Then inspect every other
     * user's ratings. If that other user has at least one liked movie in common
     * with the target, their other liked unseen movies are valid recommendations.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    Recommendation starts with similar users, then filters movies the target has not watched.
     *
     * 2. What data structure does that naturally suggest?
     *    Use user -> liked movies sets because overlap and unseen checks are set operations.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: compare target likes with every other user's likes directly.
     *
     * 4. What repeated work should I remove?
     *    Optimized: prebuild sets and collect unseen liked movies from users who share enough taste.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: compare target likes with every other user's likes directly.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Scan ratings once to collect:
     *    targetLiked = movies target rated above 3.
     *    targetWatched = every movie target rated.
     * 2. Look at each rating row as a possible recommendation source.
     * 3. Skip rows belonging to the target user.
     * 4. For that other user, rescan all ratings to see whether they share any
     *    liked movie with targetLiked.
     * 5. If they are similar, and the current row is a liked unseen movie, add it.
     * 6. Return the unique recommendation list.
     * 
     * Time Complexity: O(n^2) because each candidate row can rescan all ratings.
     * Space Complexity: O(n) for target sets and recommendations.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the ratings example above.
     * u2 shares liked movie A with me and recommends B.
     * u3 shares liked movie C with me and recommends D.
     * Final answer: [B, D]
     */
    public List<String> bruteForce(String targetUser, String[][] ratings) {

        Set<String> targetLiked = new HashSet<>();
        Set<String> targetWatched = new HashSet<>();

        for (String[] rating : ratings) {
            if (rating[0].equals(targetUser)) {
                // Any target rating means the target has already watched this movie.
                targetWatched.add(rating[1]);
                if (Integer.parseInt(rating[2]) > 3) {
                    // Only liked movies are used to decide whether another user is similar.
                    targetLiked.add(rating[1]);
                }
            }
        }

        Set<String> recommendations = new HashSet<>();

        for (String[] candidateRating : ratings) {
            String otherUser = candidateRating[0];
            if (otherUser.equals(targetUser)) {
                // We never recommend the target user's own rating rows back to them.
                continue;
            }

            boolean similar = false;
            for (String[] rating : ratings) {
                // Brute force check: rescan all rows to prove this user shares
                // at least one liked movie with the target.
                if (rating[0].equals(otherUser)
                        && Integer.parseInt(rating[2]) > 3
                        && targetLiked.contains(rating[1])) {
                    similar = true;
                    break;
                }
            }

            if (similar && Integer.parseInt(candidateRating[2]) > 3
                    && !targetWatched.contains(candidateRating[1])) {
                // A similar user's liked movie is useful only if target has not watched it.
                recommendations.add(candidateRating[1]);
            }
        }

        return new ArrayList<>(recommendations);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: prebuild sets and collect unseen liked movies from users who share enough taste.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: recommend unseen movies from similar users.
     * 2. Remove repeated work: build liked/watched sets by user and use set overlap.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the ratings example above.
     * u2 shares liked movie A with me and recommends B.
     * u3 shares liked movie C with me and recommends D.
     * Final answer: [B, D]
     */
    public List<String> optimized(String targetUser, String[][] ratings) {
        Map<String, Set<String>> liked = new HashMap<>();
        Map<String, Set<String>> watched = new HashMap<>();
        for (String[] rating : ratings) {
            String user = rating[0];
            String movie = rating[1];
            int score = Integer.parseInt(rating[2]);

            watched.putIfAbsent(user, new HashSet<>());
            watched.get(user).add(movie);

            if (score > 3) {
                liked.putIfAbsent(user, new HashSet<>());
                liked.get(user).add(movie);
            }
        }
        Set<String> targetLiked = liked.getOrDefault(targetUser, new HashSet<>());
        Set<String> targetWatched = watched.getOrDefault(targetUser, new HashSet<>());
        // HashSet lets us ask "have we seen this before?" in constant average time.
        Set<String> recommendations = new HashSet<>();
        for (String user : liked.keySet()) {
            if (user.equals(targetUser)) {
                continue;
            }
            if (sharesAny(targetLiked, liked.get(user))) {
                recommendations.addAll(liked.get(user));
            }
        }

        recommendations.removeAll(targetWatched);
        return new ArrayList<>(recommendations);
    }

    private boolean sharesAny(Set<String> first, Set<String> second) {
        // Check whether two users have at least one liked movie in common.
        // In the problem story, that single shared liked movie is enough to call
        // the other user "similar" to the target user.
        for (String value : first) {
            // Iterate through the target user's liked movies and ask whether
            // the other user's liked set contains the same title.
            if (second.contains(value)) {
                // The moment we find one overlap, the similarity condition is satisfied.
                // We can stop early because recommendations only need a yes/no answer.
                return true;
            }
        }

        // No liked movie appeared in both sets, so this user should not recommend movies.
        return false;
    }

    public static void main(String[] args) {
        MovieRecommendation solution = new MovieRecommendation();

        runSample(solution, "me", new String[][]{
                {"me", "A", "5"}, {"me", "C", "4"},
                {"u2", "A", "5"}, {"u2", "B", "5"},
                {"u3", "C", "5"}, {"u3", "D", "5"}
        });
        runSample(solution, "me", new String[][]{
                {"me", "A", "5"}, {"me", "B", "2"},
                {"u2", "A", "4"}, {"u2", "B", "5"}, {"u2", "C", "5"}
        });
        runSample(solution, "me", new String[][]{
                {"me", "A", "5"},
                {"u2", "B", "5"}, {"u2", "C", "5"}
        });
    }

    private static void runSample(MovieRecommendation solution, String targetUser, String[][] ratings) {
        System.out.println("targetUser = \"" + targetUser + "\", ratings = " + formatRatings(ratings));
        System.out.println("bruteForce = " + formatList(solution.bruteForce(targetUser, ratings)));
        System.out.println("optimized  = " + formatList(solution.optimized(targetUser, ratings)));
        System.out.println();
    }

    private static String formatRatings(String[][] ratings) {
        StringBuilder output = new StringBuilder("[");
        for (int i = 0; i < ratings.length; i++) {
            if (i > 0) {
                output.append(", ");
            }
            output.append(Arrays.toString(ratings[i]));
        }
        output.append(']');
        return output.toString();
    }

    private static String formatList(List<String> values) {
        List<String> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        return sorted.toString();
    }
}
