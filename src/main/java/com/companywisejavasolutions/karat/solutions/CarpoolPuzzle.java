

package com.companywisejavasolutions.karat.solutions;
import java.util.*;

public class CarpoolPuzzle {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given directed roads, two car starting locations, and people locations, assign each person to the car that reaches them first.
     *
     * INPUT
     * roads [from,to,minutes], starts, people [name,location].
     *
     * OUTPUT
     * Two lists of people assigned to car 1 and car 2.
     *
     * EXAMPLE
     * roads = [["A","B","10"], ["B","Camp","10"], ["C","B","5"], ["D","Camp","7"]]
     * starts = ["A", "C"]
     * people = [["Pat","B"], ["Lee","Camp"]]
     * Output: [[], [Pat, Lee]]
     * 
     * The second car reaches B and Camp earlier than the first car.
     *
     * WHAT IT MEANS
     * Compute arrival time from each start to every location, then compare per person.
     */
    /*
     * IN-DEPTH EXPLANATION
     *
     * Picture two cars already on the road. People are waiting at named
     * locations. For each person, we only care about one question:
     *
     * Which car reaches this person's location first?
     *
     * The roads in this version behave like a fixed route map: from a location,
     * there is at most one next road stored by the brute-force map. So tracing a
     * car is like following signs from one place to the next while adding travel
     * minutes.
     *
     * What to know before solving:
     *
     * 1. Each road is [from, to, minutes].
     * 2. starts[0] is car 1's starting point and starts[1] is car 2's starting point.
     * 3. Each person is [name, location].
     * 4. If both cars arrive at the same time, this implementation assigns the person to car 1.
     * 5. If a car cannot reach a location, we treat its arrival time as infinity.
     *
     * What we do to solve:
     *
     * Build a quick "from location -> next road" map. Then, for every person,
     * trace car 1 from its start to that person's location and trace car 2 from
     * its start to that same location. Compare the two times and place the
     * person's name in the winning car's list.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - Second car is faster to everyone
     *
     * roads = {{"A","B","10"}, {"B","Camp","10"}, {"C","B","5"}, {"D","Camp","7"}}
     * starts = {"A", "C"}
     * people = {{"Pat","B"}, {"Lee","Camp"}}
     *
     * Output:
     * [[], [Pat, Lee]]
     *
     * Why:
     * Car 2 reaches B in 5 minutes and Camp in 15 minutes.
     * Car 1 reaches B in 10 minutes and Camp in 20 minutes.
     *
     * Example 2 - Split assignment
     *
     * roads = {{"A","B","4"}, {"B","C","4"}, {"X","C","3"}}
     * starts = {"A", "X"}
     * people = {{"Mia","B"}, {"Noah","C"}}
     *
     * Output:
     * [[Mia], [Noah]]
     *
     * Why:
     * Only car 1 reaches B. Car 2 reaches C in 3 minutes, before car 1 reaches C in 8.
     *
     * Example 3 - Tie goes to car 1
     *
     * roads = {{"A","Meet","5"}, {"B","Meet","5"}}
     * starts = {"A", "B"}
     * people = {{"Sam","Meet"}}
     *
     * Output:
     * [[Sam], []]
     *
     * Why:
     * The code uses first <= second, so equal arrival times choose car 1.
     *
     * Edge case 1 - Person at a car's starting point
     *
     * starts = {"A", "B"}
     * people = {{"Ava","A"}}
     *
     * Output:
     * [[Ava], []]
     *
     * Why:
     * Car 1 reaches A in 0 minutes.
     *
     * Edge case 2 - Neither car can reach the person
     *
     * starts = {"A", "B"}
     * people = {{"Kai","Z"}}
     *
     * Output:
     * [[Kai], []]
     *
     * Why:
     * Both times are infinity, and the tie rule still gives the person to car 1.
    */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Each car starts from a known location.
     * 2. A person can ride with a car only if that car can reach them.
     * 3. Roads define reachability.
     * 4. Brute force may trace routes separately for every person.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * For each person, trace from each car's start location.
     * If the trace reaches the person's location, that car can pick them up.
     * Store the possible riders for each car.
     * Repeat this direct check for every person.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Convert the road list into a map from starting location to the next Road.
     * 2. Create two answer lists, one for each car.
     * 3. For each person, trace car 1 from starts[0] to that person's location.
     * 4. Trace car 2 from starts[1] to the same location.
     * 5. If car 1's time is less than or equal to car 2's time, assign to car 1.
     * 6. Otherwise, assign to car 2.
     * 7. Return the two lists.
     *
     * Time Complexity: O(P * R), where P is people count and R is the length of a traced route.
     * Space Complexity: O(R + P) for the road map and answer lists.
     */
    public List<List<String>> bruteForce(String[][] roads, String[] starts, String[][] people) {

        Map<String, Road> nextRoad = new HashMap<>();
        for (String[] road : roads) {
            // Store the next road from each location so tracing a car is just following one link at a time.
            nextRoad.put(road[0], new Road(road[1], Integer.parseInt(road[2])));
        }

        List<List<String>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        result.add(new ArrayList<>());

        for (String[] person : people) {
            String name = person[0];
            String location = person[1];

            // Brute force repeats the route trace for every person instead of reusing earlier arrival times.
            int first = timeToReach(starts[0], location, nextRoad);
            int second = timeToReach(starts[1], location, nextRoad);

            // Ties intentionally go to the first car because it satisfies "reaches first or no later."
            if (first <= second) {
                result.get(0).add(name);
            } else {
                result.get(1).add(name);
            }
        }

        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: compute arrival time from each car once, then compare those times for every person.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: assign people to the car that reaches them first.
     * 2. Remove repeated work: precompute arrival times from each car once and reuse them.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the road example above.
     * Second car from C reaches B in 5 minutes, before first car from A reaches B in 10.
     * Second car also reaches Camp in 15 minutes, before first car reaches Camp in 20.
     * Final answer: [[], [Pat, Lee]]
     */
    public List<List<String>> optimized(String[][] roads, String[] starts, String[][] people) {
        Map<String, Road> nextRoad = new HashMap<>();
        for (String[] road : roads) {
            nextRoad.put(road[0], new Road(road[1], Integer.parseInt(road[2])));
        }

        Map<String, Integer> firstArrival = arrivalTimes(starts[0], nextRoad);
        Map<String, Integer> secondArrival = arrivalTimes(starts[1], nextRoad);
        List<List<String>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        result.add(new ArrayList<>());
        for (String[] person : people) {
            String name = person[0];
            String location = person[1];
            int first = firstArrival.getOrDefault(location, Integer.MAX_VALUE);
            int second = secondArrival.getOrDefault(location, Integer.MAX_VALUE);
        if (first <= second) {
                result.get(0).add(name);
            } else {
                result.get(1).add(name);
            }
        }
        return result;
    }

    private Map<String, Integer> arrivalTimes(String start, Map<String, Road> nextRoad) {
        // Store the earliest known arrival time for every location this car visits.
        // This is the optimized helper's whole purpose: compute once, reuse many times.
        Map<String, Integer> arrivals = new HashMap<>();

        // Begin exactly where the car starts.
        // The start location is reachable without driving anywhere.
        String current = start;

        // Arrival time at the starting location is 0 minutes by definition.
        int time = 0;

        // Follow the one-way route while the location is real and new.
        // The containsKey check also protects the interview solution from cycles
        // like A -> B -> A, which would otherwise loop forever.
                while (current != null && !arrivals.containsKey(current)) {
            // Record the time before driving away from this location.
            // Later, each person's location can be answered with a simple map lookup.
            arrivals.put(current, time);

            // Ask whether there is a next directed road leaving this location.
            // If not, this car's reachable route ends here.
            Road road = nextRoad.get(current);
        if (road == null) {
                // No outgoing road means there are no more reachable locations
                // from this start under the "follow next road" model.
                break;
            }

            // Add the travel time for the road we are about to take.
            // This derives directly from the problem's [from, to, minutes] input.
            time += road.minutes;

            // Move the car to the road's destination and continue tracing.
            current = road.destination;
        }

        // Return the complete reachable timeline for this car.
        // Missing locations are treated by the caller as unreachable/infinity.
        return arrivals;
    }

    private static class Road {
        String destination;
        int minutes;

        Road(String destination, int minutes) {
            this.destination = destination;
            this.minutes = minutes;
        }
    }

    private int timeToReach(String start, String target, Map<String, Road> nextRoad) {
        // A car starts with zero minutes traveled.
        int time = 0;

        // current is the location we are standing at while tracing the route.
        String current = start;

        // Remember visited locations so a cycle in the roads does not trap us.
        Set<String> seen = new HashSet<>();

        // Walk the route until we arrive, run out of road, or detect a cycle.
        while (current != null && seen.add(current)) {
            // If the current location is the person's location, this car can pick
            // them up after exactly the minutes accumulated so far.
            if (current.equals(target)) {
                return time;
            }

            // Look up the next directed road from this location.
            // The brute force version does this fresh for each person.
            Road road = nextRoad.get(current);
            if (road == null) {
                // No next road means the car cannot continue toward the target.
                break;
            }
            // Add this road's travel time before moving to the next location.
            time += road.minutes;

            // Continue tracing from the destination of that road.
            current = road.destination;
        }

        // Integer.MAX_VALUE represents "this car never reaches the target."
        return Integer.MAX_VALUE;
    }

    public static void main(String[] args) {
        CarpoolPuzzle puzzle = new CarpoolPuzzle();

        String[][][] roadSamples = {
                {{"A", "B", "10"}, {"B", "Camp", "10"}, {"C", "B", "5"}, {"D", "Camp", "7"}},
                {{"A", "B", "4"}, {"B", "C", "4"}, {"X", "C", "3"}},
                {{"A", "Meet", "5"}, {"B", "Meet", "5"}}
        };
        String[][] startSamples = {
                {"A", "C"},
                {"A", "X"},
                {"A", "B"}
        };
        String[][][] peopleSamples = {
                {{"Pat", "B"}, {"Lee", "Camp"}},
                {{"Mia", "B"}, {"Noah", "C"}},
                {{"Sam", "Meet"}}
        };

        for (int i = 0; i < roadSamples.length; i++) {
            System.out.println("Sample " + (i + 1) + ":");
            System.out.println("roads = " + formatTable(roadSamples[i]));
            System.out.println("starts = " + Arrays.toString(startSamples[i]));
            System.out.println("people = " + formatTable(peopleSamples[i]));
            System.out.println("bruteForce = "
                    + puzzle.bruteForce(copyTable(roadSamples[i]), copyArray(startSamples[i]), copyTable(peopleSamples[i])));
            System.out.println("optimized = "
                    + puzzle.optimized(copyTable(roadSamples[i]), copyArray(startSamples[i]), copyTable(peopleSamples[i])));
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

    private static String[][] copyTable(String[][] table) {
        String[][] copy = new String[table.length][];
        for (int row = 0; row < table.length; row++) {
            copy[row] = copyArray(table[row]);
        }
        return copy;
    }

    private static String formatTable(String[][] table) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int row = 0; row < table.length; row++) {
            if (row > 0) {
                builder.append(", ");
            }
            builder.append(Arrays.toString(table[row]));
        }
        builder.append("]");
        return builder.toString();
    }
}
