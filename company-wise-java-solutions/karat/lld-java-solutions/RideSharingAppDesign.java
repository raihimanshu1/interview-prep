import java.util.ArrayList;
import java.util.List;

public class RideSharingAppDesign {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Design core LLD for a ride-sharing app: riders request rides, drivers are
     * matched, and trips move through states.
     *
     * Sample Input:
     * two available drivers, rider pickup near driver B
     *
     * Sample Output:
     * TripService matches nearest available driver and marks driver unavailable.
     *
     * What is the problem really asking?
     * A ride-sharing system must match riders to drivers and keep trip state correct.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Model the trip lifecycle first. Matching is important, but the trip state
     * machine keeps the system correct.
     */

    /*
     * BASELINE DESIGN
     *
     * Scan every driver for every ride request. Works for small data, not for a
     * city-scale app.
     */

    /*
     * STRONGER DESIGN
     *
     * Keep available drivers indexed by location, match nearest driver, reserve
     * driver, then move trip through REQUESTED -> ACCEPTED -> STARTED -> COMPLETED.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Model Driver, Location, Trip, TripStatus, and TripService. Use readable nearest-driver logic.
     *
     * Why this approach works:
     * The state model prevents double assignment and makes trip transitions explicit.
     */
    public enum TripStatus {
        REQUESTED, ACCEPTED, STARTED, COMPLETED, CANCELLED
    }

    public static class Location {
        final double lat;
        final double lon;

        public Location(double lat, double lon) {
            // Latitude is the north/south coordinate.
            this.lat = lat;

            // Longitude is the east/west coordinate.
            this.lon = lon;
        }
    }

    public static class Driver {
        final String driverId;
        Location location;
        boolean available = true;

        public Driver(String driverId, Location location) {
            // driverId identifies the driver in trip records.
            this.driverId = driverId;

            // Current location is used by matching.
            this.location = location;
        }
    }

    public static class Trip {
        final String riderId;
        final Driver driver;
        TripStatus status = TripStatus.REQUESTED;

        public Trip(String riderId, Driver driver) {
            // riderId tells us who requested the trip.
            this.riderId = riderId;

            // driver is the reserved driver for this trip.
            this.driver = driver;
        }
    }

    public static class TripService {
        private final List<Driver> drivers = new ArrayList<>();

        public void addDriver(Driver driver) {
            // Add driver to the pool that requestRide can search.
            drivers.add(driver);
        }

        public Trip requestRide(String riderId, Location pickup) {
            // Find the closest driver who is still available.
            Driver driver = nearestAvailableDriver(pickup);

            // If no driver is available, no trip can be created.
            if (driver == null) {
                return null;
            }

            // Reserve the driver immediately so another rider cannot get the same driver.
            driver.available = false;

            // Create the trip after the driver is reserved.
            Trip trip = new Trip(riderId, driver);

            // Move from REQUESTED to ACCEPTED because a driver was matched.
            trip.status = TripStatus.ACCEPTED;

            // Return the accepted trip to the caller.
            return trip;
        }

        private Driver nearestAvailableDriver(Location pickup) {
            // best stores the closest available driver seen so far.
            Driver best = null;

            // Start with infinity so the first available driver becomes best.
            double bestDistance = Double.MAX_VALUE;

            // Scan each driver in this small interview implementation.
            for (Driver driver : drivers) {
                // Skip drivers already assigned to another trip.
                if (!driver.available) {
                    continue;
                }

                // Measure approximate straight-line distance.
                double distance = distance(driver.location, pickup);

                // If this driver is closer than the previous best, remember them.
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = driver;
                }
            }
            return best;
        }

        private double distance(Location a, Location b) {
            // Difference in latitude.
            double latDiff = a.lat - b.lat;

            // Difference in longitude.
            double lonDiff = a.lon - b.lon;

            // Pythagorean distance is enough for this LLD demo.
            return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
        }
    }

    public static void main(String[] args) {
        runSample("Sample 1 - nearest driver wins",
                new Driver[] {
                        new Driver("d1", new Location(0, 0)),
                        new Driver("d2", new Location(5, 5))
                },
                "r1", new Location(1, 1));

        runSample("Sample 2 - no drivers",
                new Driver[] {},
                "r2", new Location(1, 1));

        runSample("Sample 3 - first request reserves driver",
                new Driver[] {
                        new Driver("d3", new Location(0, 0))
                },
                "r3", new Location(0.1, 0.1));
    }

    private static void runSample(String label, Driver[] drivers, String riderId, Location pickup) {
        TripService service = new TripService();
        for (Driver driver : drivers) {
            service.addDriver(driver);
        }

        Trip trip = service.requestRide(riderId, pickup);

        System.out.println(label);
        System.out.println("riderId: " + riderId);
        System.out.println("pickup: " + format(pickup));
        System.out.println("trip: " + format(trip));
        System.out.println();
    }

    private static String format(Location location) {
        return "(" + location.lat + ", " + location.lon + ")";
    }

    private static String format(Trip trip) {
        if (trip == null) {
            return "no driver available";
        }
        return "Trip(driver=" + trip.driver.driverId + ", status=" + trip.status + ")";
    }
}
