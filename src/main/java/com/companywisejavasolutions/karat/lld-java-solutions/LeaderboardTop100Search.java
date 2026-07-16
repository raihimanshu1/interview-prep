package com.companywisejavasolutions.karat.lldjavasolutions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardTop100Search {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Design a leaderboard that can return top 100 contestants and also search
     * a contestant progress by name.
     *
     * Sample Flow:
     * update Alice score to 120
     * update Bob score to 90
     * top100 returns Alice before Bob
     * search("Alice") returns Alice profile
     *
     * Sample Input:
     * update("Alice", 120, 8), update("Bob", 90, 5), searchByName("Alice"), top100()
     *
     * Sample Output:
     * search returns Alice profile; top100 returns Alice before Bob.
     *
     * What is the problem really asking?
     * Top ranking and name search are two different access patterns.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Ranking and search are different access patterns. Keep profiles in a map
     * for name lookup and sort/rank by score for top contestants.
     */

    /*
     * BASELINE DESIGN
     *
     * Store contestants in a list and sort on every top100 call. Simple but slow
     * if reads are frequent.
     */

    /*
     * STRONGER DESIGN
     *
     * Store contestant by name and maintain ranking state. In production this
     * could be Redis sorted set plus DB profile table.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Use a map for direct name lookup and sort contestants for top ranking in this interview-sized implementation.
     *
     * Why this approach works:
     * The map makes search O(1) average. Sorting gives correct ranking and models how a production sorted set would behave.
     */
    public static class Contestant {
        private final String name;
        private int score;
        private int solvedProblems;

        public Contestant(String name) {
            // Name is the stable lookup key in this interview-sized design.
            this.name = name;
        }

        public void updateProgress(int score, int solvedProblems) {
            // Replace score with the latest score from the contest engine.
            this.score = score;

            // Keep solved count beside score because the UI may show both.
            this.solvedProblems = solvedProblems;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        public int getSolvedProblems() {
            return solvedProblems;
        }
    }

    public static class LeaderboardService {
        private final Map<String, Contestant> byName = new HashMap<>();

        public void update(String name, int score, int solvedProblems) {
            // If this contestant is new, create the profile explicitly.
            if (!byName.containsKey(name)) {
                byName.put(name, new Contestant(name));
            }

            // Now the map definitely has the contestant.
            Contestant contestant = byName.get(name);

            // Keep progress update in one method so score and solved count change together.
            contestant.updateProgress(score, solvedProblems);
        }

        public Contestant searchByName(String name) {
            // Map lookup answers direct search without scanning the leaderboard.
            return byName.get(name);
        }

        public List<Contestant> top100() {
            // Copy values so sorting does not mutate the map itself.
            List<Contestant> contestants = new ArrayList<>(byName.values());

            // Sort by score descending. If scores tie, sort by name for stable output.
            Comparator<Contestant> rankingOrder = Comparator
                    .comparingInt(Contestant::getScore)
                    .reversed()
                    .thenComparing(Contestant::getName);
            contestants.sort(rankingOrder);

            // Return up to 100 contestants. Small examples may have fewer than 100.
            return contestants.subList(0, Math.min(100, contestants.size()));
        }
    }

    public static void main(String[] args) {
        LeaderboardService service = new LeaderboardService();

        service.update("Alice", 120, 8);
        service.update("Bob", 90, 5);
        runSample(service, "Sample 1 - normal ranking", "Alice");

        service.update("Carol", 120, 7);
        runSample(service, "Sample 2 - tie by score uses name", "Carol");

        service.update("Bob", 150, 9);
        runSample(service, "Sample 3 - updated score changes rank", "Bob");
    }

    private static void runSample(LeaderboardService service, String label, String searchName) {
        System.out.println(label);
        Contestant found = service.searchByName(searchName);
        System.out.println("search(" + searchName + "): " + format(found));
        System.out.println("top: " + formatList(service.top100()));
        System.out.println();
    }

    private static String format(Contestant contestant) {
        if (contestant == null) {
            return "not found";
        }
        return contestant.getName() + "(score=" + contestant.getScore()
                + ", solved=" + contestant.getSolvedProblems() + ")";
    }

    private static String formatList(List<Contestant> contestants) {
        List<String> output = new ArrayList<>();
        for (Contestant contestant : contestants) {
            output.add(format(contestant));
        }
        return output.toString();
    }
}
