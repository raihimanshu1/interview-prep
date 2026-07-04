
package com.patternwisejavasolutions.design;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ParkingLotSystem {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: lot has 1 bike spot and 1 car spot, park(CAR), park(BIKE),
     * park(TRUCK), leave(carTicket)
     * Sample Output: car ticket, bike ticket, null, true
     *
     * Build a simple parking lot where vehicles park in matching spot types and
     * receive a ticket. Leaving with a valid ticket frees that spot.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A parking lot is a group of spots. A vehicle can park only in a free spot
     * that can fit its type. A ticket remembers which spot was used.
     * The optimized design mirrors a parking attendant's desk: one queue of free
     * spots per vehicle type, plus a ticket book that points back to the spot.
     */

    public enum VehicleType {
        BIKE,
        CAR,
        TRUCK
    }

    /*
     * BRUTE FORCE INTUITION
     *
     * Keep all spots in one list. Every time someone parks, scan the list until
     * a free matching spot is found.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Build a list of spots.
     * 2. park(type): scan for the first unused spot with same type.
     * 3. Mark it occupied and create a ticket.
     * 4. leave(ticket): find the ticket, free the spot, and remove the ticket.
     *
     * Time Complexity: park O(number of spots), leave O(1)
     * Space Complexity: O(number of spots + active tickets)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Spots: C1, B1.
     * park(CAR) scans and takes C1.
     * park(BIKE) scans and takes B1.
     * park(TRUCK) finds no truck spot and returns null.
     */

    public static class BruteForce {
        private List<Spot> spots = new ArrayList<>();
        private Map<String, Spot> ticketToSpot = new HashMap<>();
        private int nextTicketNumber = 1;

        public BruteForce(int bikeSpots, int carSpots, int truckSpots) {
            addSpots(VehicleType.BIKE, bikeSpots);
            addSpots(VehicleType.CAR, carSpots);
            addSpots(VehicleType.TRUCK, truckSpots);
        }

        public String park(VehicleType vehicleType) {
            for (Spot spot : spots) {
                if (!spot.occupied && spot.type == vehicleType) {
                    spot.occupied = true;
                    String ticket = "T" + nextTicketNumber++;
                    ticketToSpot.put(ticket, spot);
                    return ticket;
                }
            }
            return null;
        }

        public boolean leave(String ticketId) {
            Spot spot = ticketToSpot.remove(ticketId);
            if (spot == null) {
                return false;
            }

            spot.occupied = false;
            return true;
        }

        private void addSpots(VehicleType type, int count) {
            for (int i = 0; i < count; i++) {
                spots.add(new Spot(type));
            }
        }
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Instead of scanning all spots, keep a queue of free spots for each vehicle
     * type. Parking then takes directly from the matching queue.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep type -> queue of currently free spots.
     * 2. park(type): poll one spot from that type's queue.
     * 3. Store ticket -> spot.
     * 4. leave(ticket): put the spot back into its type queue.
     *
     * Time Complexity: O(1) for park and leave
     * Space Complexity: O(number of spots + active tickets)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * free[CAR] has C1, free[BIKE] has B1.
     * park(CAR) polls C1.
     * leave(ticket) offers C1 back into free[CAR].
     */

    public static class Optimized {
        private Map<VehicleType, Queue<Spot>> freeSpots = new EnumMap<>(VehicleType.class);
        private Map<String, Spot> ticketToSpot = new HashMap<>();
        private int nextTicketNumber = 1;

        public Optimized(int bikeSpots, int carSpots, int truckSpots) {
            for (VehicleType type : VehicleType.values()) {
                freeSpots.put(type, new ArrayDeque<>());
            }

            addSpots(VehicleType.BIKE, bikeSpots);
            addSpots(VehicleType.CAR, carSpots);
            addSpots(VehicleType.TRUCK, truckSpots);
        }

        public String park(VehicleType vehicleType) {
            Queue<Spot> queue = freeSpots.get(vehicleType);
            if (queue.isEmpty()) {
                return null;
            }

            Spot spot = queue.poll();
            String ticket = "T" + nextTicketNumber++;
            // The ticket is the only handle the driver needs to free this spot later.
            ticketToSpot.put(ticket, spot);
            return ticket;
        }

        public boolean leave(String ticketId) {
            Spot spot = ticketToSpot.remove(ticketId);
            if (spot == null) {
                return false;
            }

            // Returning the spot to its own type queue makes it available again.
            freeSpots.get(spot.type).offer(spot);
            return true;
        }

        private void addSpots(VehicleType type, int count) {
            Queue<Spot> queue = freeSpots.get(type);
            for (int i = 0; i < count; i++) {
                queue.offer(new Spot(type));
            }
        }
    }

    private static class Spot {
        private VehicleType type;
        private boolean occupied;

        private Spot(VehicleType type) {
            this.type = type;
        }
    }
}
