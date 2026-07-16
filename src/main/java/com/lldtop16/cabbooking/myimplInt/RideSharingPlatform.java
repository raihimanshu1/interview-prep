package main.java.com.lldtop16.cabbooking.myimplInt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * RideSharingPlatform - A comprehensive ride-sharing application
 * 
 * Core Features:
 * - Rider registration and ride booking
 * - Driver management with availability status
 * - Driver matching strategies (nearest, highest-rated)
 * - Pricing strategies (hourly fare, surge pricing)
 * 
 * Design Patterns Used:
 * - Strategy Pattern for driver matching and pricing
 * - Factory Pattern for creating ride instances
 * - Singleton Pattern for platform instance
 */
public class RideSharingPlatform {
    
    /**
     * Booking status enumeration representing the state of a ride booking
     */
    public enum BookingStatus {
        ACCEPTED,    // Driver has accepted the ride
        REJECTED,    // Driver has rejected the ride
        PENDING,     // Ride request is pending driver acceptance
        COMPLETED,   // Ride has been completed
        CANCELLED    // Ride has been cancelled
    }
    
    /**
     * Driver availability status enumeration
     */
    public enum AvailabilityStatus {
        AVAILABLE,   // Driver is available for rides
        BUSY,        // Driver is currently on a ride
        OFFLINE      // Driver is offline
    }
    
    /**
     * Vehicle type enumeration
     */
    public enum VehicleType {
        CAR,
        BIKE,
        AUTO,
        SUV
    }
    
    // ==================== Interfaces ====================
    
    /**
     * Pricing strategy interface - defines how fare is calculated
     * Implements Strategy Pattern for flexible pricing algorithms
     */
    public interface PricingStrategy {
        /**
         * Calculates total fare for a ride
         * @param distanceInKm distance traveled in kilometers
         * @param durationInMinutes duration of ride in minutes
         * @param surgeMultiplier surge pricing multiplier (1.0 = normal pricing)
         * @return total fare amount
         */
        double calculateFare(double distanceInKm, int durationInMinutes, double surgeMultiplier);
    }
    
    /**
     * Driver matching strategy interface - defines how drivers are matched to riders
     * Implements Strategy Pattern for different matching algorithms
     */
    public interface DriverMatchingStrategy {
        /**
         * Finds the best driver for a ride request
         * @param rider rider requesting the ride
         * @param availableDrivers list of available drivers
         * @return matched driver or null if no driver found
         */
        Driver findDriver(Rider rider, List<Driver> availableDrivers);
    }
    
    // ==================== Concrete Strategies ====================
    
    /**
     * Hourly fare pricing strategy - charges based on duration
     */
    public class HourlyPricing implements PricingStrategy {
        private static final double BASE_RATE_PER_HOUR = 50.0;
        private static final double MINIMUM_FARE = 50.0;
        
        @Override
        public double calculateFare(double distanceInKm, int durationInMinutes, double surgeMultiplier) {
            double hours = durationInMinutes / 60.0;
            double fare = hours * BASE_RATE_PER_HOUR * surgeMultiplier;
            return Math.max(fare, MINIMUM_FARE);
        }
    }
    
    /**
     * Distance-based pricing strategy - charges based on distance traveled
     */
    public class DistancePricing implements PricingStrategy {
        private static final double RATE_PER_KM = 15.0;
        private static final double BASE_FARE = 30.0;
        private static final double MINIMUM_FARE = 40.0;
        
        @Override
        public double calculateFare(double distanceInKm, int durationInMinutes, double surgeMultiplier) {
            double fare = (BASE_FARE + (distanceInKm * RATE_PER_KM)) * surgeMultiplier;
            return Math.max(fare, MINIMUM_FARE);
        }
    }
    
    /**
     * Nearest driver matching strategy - matches rider with closest available driver
     */
    public class NearestDriverMatching implements DriverMatchingStrategy {
        
        @Override
        public Driver findDriver(Rider rider, List<Driver> availableDrivers) {
            if (availableDrivers == null || availableDrivers.isEmpty()) {
                return null;
            }
            
            Driver nearestDriver = null;
            double minDistance = Double.MAX_VALUE;
            
            for (Driver driver : availableDrivers) {
                if (driver.isAvailable()) {
                    double distance = calculateDistance(
                        rider.getStartLocation(), 
                        driver.getLocation()
                    );
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestDriver = driver;
                    }
                }
            }
            
            return nearestDriver;
        }
        
        /**
         * Calculates Euclidean distance between two locations
         * @param loc1 first location
         * @param loc2 second location
         * @return distance in kilometers
         */
        private double calculateDistance(Location loc1, Location loc2) {
            double latDiff = loc1.getLatitude() - loc2.getLatitude();
            double lonDiff = loc1.getLongitude() - loc2.getLongitude();
            return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
        }
    }
    
    /**
     * Highest-rated driver matching strategy - matches rider with best-rated driver
     */
    public class HighestRatedDriverMatching implements DriverMatchingStrategy {
        
        @Override
        public Driver findDriver(Rider rider, List<Driver> availableDrivers) {
            if (availableDrivers == null || availableDrivers.isEmpty()) {
                return null;
            }
            
            Driver bestDriver = null;
            double highestRating = -1.0;
            
            for (Driver driver : availableDrivers) {
                if (driver.isAvailable() && driver.getRating() > highestRating) {
                    highestRating = driver.getRating();
                    bestDriver = driver;
                }
            }
            
            return bestDriver;
        }
    }
    
    // ==================== Model Classes ====================
    
    /**
     * Location class represents geographical coordinates
     * Immutable value object for location data
     */
    public static class Location {
        private final double latitude;
        private final double longitude;
        
        /**
         * Constructs a Location with latitude and longitude
         * @param latitude latitude coordinate
         * @param longitude longitude coordinate
         */
        public Location(double latitude, double longitude) {
            if (latitude < -90 || latitude > 90) {
                throw new IllegalArgumentException("Latitude must be between -90 and 90");
            }
            if (longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("Longitude must be between -180 and 180");
            }
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        public double getLatitude() {
            return latitude;
        }
        
        public double getLongitude() {
            return longitude;
        }
        
        @Override
        public String toString() {
            return String.format("(%.4f, %.4f)", latitude, longitude);
        }
    }
    
    /**
     * Rider class represents a person who books rides
     */
    public static class Rider {
        private final String id;
        private String name;
        private String email;
        private String phoneNumber;
        private Location startLocation;
        private Location endLocation;
        private BookingStatus bookingStatus;
        
        /**
         * Constructs a Rider with personal details and locations
         * @param name rider's name
         * @param email rider's email
         * @param phoneNumber rider's phone number
         * @param startLocation starting location
         * @param endLocation destination location
         */
        public Rider(String name, String email, String phoneNumber, 
                     Location startLocation, Location endLocation) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.startLocation = startLocation;
            this.endLocation = endLocation;
            this.bookingStatus = BookingStatus.PENDING;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public Location getStartLocation() { return startLocation; }
        public void setStartLocation(Location startLocation) { this.startLocation = startLocation; }
        public Location getEndLocation() { return endLocation; }
        public void setEndLocation(Location endLocation) { this.endLocation = endLocation; }
        public BookingStatus getBookingStatus() { return bookingStatus; }
        public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }
    }
    
    /**
     * Vehicle class represents a vehicle in the system
     */
    public static class Vehicle {
        private final String id;
        private String vehicleNumber;
        private VehicleType vehicleType;
        private String vehicleName;
        private float rating;
        
        /**
         * Constructs a Vehicle with basic details
         * @param vehicleNumber registration number
         * @param vehicleType type of vehicle
         * @param vehicleName model name
         */
        public Vehicle(String vehicleNumber, VehicleType vehicleType, String vehicleName) {
            this.id = UUID.randomUUID().toString();
            this.vehicleNumber = vehicleNumber;
            this.vehicleType = vehicleType;
            this.vehicleName = vehicleName;
            this.rating = 0.0f; // Default rating
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
        public VehicleType getVehicleType() { return vehicleType; }
        public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
        public String getVehicleName() { return vehicleName; }
        public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
        public float getRating() { return rating; }
        public void setRating(float rating) { this.rating = rating; }
    }
    
    /**
     * Driver class represents a driver in the system
     */
    public static class Driver {
        private final String id;
        private String name;
        private String phoneNumber;
        private AvailabilityStatus availabilityStatus;
        private Location location;
        private Vehicle vehicle;
        private double rating;
        private int totalRides;
        
        /**
         * Constructs a Driver with basic details
         * @param name driver's name
         * @param phoneNumber driver's phone number
         * @param vehicle assigned vehicle
         */
        public Driver(String name, String phoneNumber, Vehicle vehicle) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.vehicle = vehicle;
            this.availabilityStatus = AvailabilityStatus.AVAILABLE;
            this.rating = 5.0; // Default rating for new drivers
            this.totalRides = 0;
        }
        
        /**
         * Alternative constructor without vehicle
         * @param name driver's name
         * @param phoneNumber driver's phone number
         */
        public Driver(String name, String phoneNumber) {
            this(name, phoneNumber, null);
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
        public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) { 
            this.availabilityStatus = availabilityStatus; 
        }
        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }
        public Vehicle getVehicle() { return vehicle; }
        public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }
        public int getTotalRides() { return totalRides; }
        public void setTotalRides(int totalRides) { this.totalRides = totalRides; }
        
        /**
         * Checks if driver is available for rides
         * @return true if driver is available, false otherwise
         */
        public boolean isAvailable() {
            return availabilityStatus == AvailabilityStatus.AVAILABLE;
        }
        
        /**
         * Updates driver status after accepting a ride
         */
        public void acceptRide() {
            this.availabilityStatus = AvailabilityStatus.BUSY;
            this.totalRides++;
        }
        
        /**
         * Updates driver status after completing a ride
         */
        public void completeRide() {
            this.availabilityStatus = AvailabilityStatus.AVAILABLE;
        }
        
        /**
         * Updates driver rating after ride completion
         * @param newRating rating given by rider (1-5)
         */
        public void updateRating(float newRating) {
            // Weighted average: 70% old rating, 30% new rating
            this.rating = (float) (this.rating * 0.7 + newRating * 0.3);
        }
    }
    
    /**
     * Ride class represents a single ride instance
     */
    public static class Ride {
        private final String rideId;
        private Rider rider;
        private Driver driver;
        private Location startLocation;
        private Location endLocation;
        private BookingStatus status;
        private double fare;
        private long startTime;
        private long endTime;
        
        /**
         * Constructs a Ride instance
         * @param rider rider booking the ride
         * @param driver assigned driver
         * @param startLocation pickup location
         * @param endLocation drop location
         */
        public Ride(Rider rider, Driver driver, Location startLocation, Location endLocation) {
            this.rideId = UUID.randomUUID().toString();
            this.rider = rider;
            this.driver = driver;
            this.startLocation = startLocation;
            this.endLocation = endLocation;
            this.status = BookingStatus.PENDING;
            this.startTime = System.currentTimeMillis();
        }
        
        // Getters and setters
        public String getRideId() { return rideId; }
        public Rider getRider() { return rider; }
        public void setRider(Rider rider) { this.rider = rider; }
        public Driver getDriver() { return driver; }
        public void setDriver(Driver driver) { this.driver = driver; }
        public Location getStartLocation() { return startLocation; }
        public void setStartLocation(Location startLocation) { this.startLocation = startLocation; }
        public Location getEndLocation() { return endLocation; }
        public void setEndLocation(Location endLocation) { this.endLocation = endLocation; }
        public BookingStatus getStatus() { return status; }
        public void setStatus(BookingStatus status) { this.status = status; }
        public double getFare() { return fare; }
        public void setFare(double fare) { this.fare = fare; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        
        /**
         * Calculates ride duration in minutes
         * @return duration in minutes
         */
        public int getDurationInMinutes() {
            if (endTime == 0) {
                return (int) ((System.currentTimeMillis() - startTime) / 60000);
            }
            return (int) ((endTime - startTime) / 60000);
        }
    }
    
    // ==================== Service Classes ====================
    
    /**
     * BookingService handles ride booking operations
     * Implements the Facade pattern to simplify booking process
     */
    public static class BookingService {
        private List<Driver> registeredDrivers;
        private PricingStrategy pricingStrategy;
        private DriverMatchingStrategy driverMatchingStrategy;
        
        /**
         * Constructs a BookingService with default strategies
         */
        public BookingService() {
            this.registeredDrivers = new ArrayList<>();
            this.pricingStrategy = new HourlyPricing();
            this.driverMatchingStrategy = new NearestDriverMatching();
        }
        
        /**
         * Constructs a BookingService with custom strategies
         * @param pricingStrategy pricing strategy to use
         * @param driverMatchingStrategy driver matching strategy to use
         */
        public BookingService(PricingStrategy pricingStrategy, DriverMatchingStrategy driverMatchingStrategy) {
            this();
            this.pricingStrategy = pricingStrategy;
            this.driverMatchingStrategy = driverMatchingStrategy;
        }
        
        /**
         * Registers a driver in the system
         * @param driver driver to register
         */
        public void registerDriver(Driver driver) {
            if (driver == null) {
                throw new IllegalArgumentException("Driver cannot be null");
            }
            registeredDrivers.add(driver);
            System.out.println("Driver registered: " + driver.getName());
        }
        
        /**
         * Books a ride for a rider
         * @param rider rider requesting the ride
         * @return Ride object if booking successful, null otherwise
         */
        public Ride bookRide(Rider rider) {
            validateRider(rider);
            
            // Find available driver using matching strategy
            Driver matchedDriver = driverMatchingStrategy.findDriver(rider, registeredDrivers);
            
            if (matchedDriver == null) {
                System.out.println("No drivers available for booking");
                rider.setBookingStatus(BookingStatus.REJECTED);
                return null;
            }
            
            // Create ride
            Ride ride = new Ride(rider, matchedDriver, 
                               rider.getStartLocation(), 
                               rider.getEndLocation());
            
            // Update driver and rider status
            matchedDriver.acceptRide();
            rider.setBookingStatus(BookingStatus.ACCEPTED);
            
            // Calculate fare
            double surgeMultiplier = calculateSurgePricing();
            int estimatedDuration = estimateDuration(rider.getStartLocation(), rider.getEndLocation());
            ride.setFare(pricingStrategy.calculateFare(0, estimatedDuration, surgeMultiplier));
            
            System.out.println("Ride booked successfully!");
            System.out.println("Driver: " + matchedDriver.getName());
            System.out.println("Estimated fare: $" + String.format("%.2f", ride.getFare()));
            
            return ride;
        }
        
        /**
         * Cancels a ride
         * @param ride ride to cancel
         */
        public void cancelRide(Ride ride) {
            if (ride == null || ride.getStatus() == BookingStatus.COMPLETED) {
                throw new IllegalArgumentException("Invalid ride for cancellation");
            }
            
            ride.getDriver().completeRide();
            ride.getRider().setBookingStatus(BookingStatus.CANCELLED);
            ride.setStatus(BookingStatus.CANCELLED);
            
            System.out.println("Ride cancelled successfully");
        }
        
        /**
         * Completes a ride
         * @param ride ride to complete
         * @param riderRating rating given by rider (1-5)
         */
        public void completeRide(Ride ride, float riderRating) {
            if (ride == null) {
                throw new IllegalArgumentException("Ride cannot be null");
            }
            
            ride.getDriver().completeRide();
            ride.getDriver().updateRating(riderRating);
            ride.getRider().setBookingStatus(BookingStatus.COMPLETED);
            ride.setStatus(BookingStatus.COMPLETED);
            ride.setEndTime(System.currentTimeMillis());
            
            System.out.println("Ride completed. Driver: " + ride.getDriver().getName());
        }
        
        /**
         * Validates rider before booking
         * @param rider rider to validate
         * @throws IllegalArgumentException if rider is invalid
         */
        private void validateRider(Rider rider) {
            if (rider == null) {
                throw new IllegalArgumentException("Rider cannot be null");
            }
            if (rider.getStartLocation() == null || rider.getEndLocation() == null) {
                throw new IllegalArgumentException("Start and end locations must be specified");
            }
            if (rider.getBookingStatus() != BookingStatus.PENDING) {
                throw new IllegalArgumentException("Rider already has an active booking");
            }
        }
        
        /**
         * Calculates surge pricing multiplier based on demand
         * @return surge multiplier (1.0 to 3.0)
         */
        private double calculateSurgePricing() {
            int availableDrivers = 0;
            for (Driver driver : registeredDrivers) {
                if (driver.isAvailable()) {
                    availableDrivers++;
                }
            }
            
            // Simple surge logic: fewer drivers = higher surge
            if (availableDrivers == 0) return 3.0;
            if (availableDrivers < 3) return 2.0;
            if (availableDrivers < 6) return 1.5;
            return 1.0;
        }
        
        /**
         * Estimates ride duration between two locations
         * @param start starting location
         * @param end ending location
         * @return estimated duration in minutes
         */
        private int estimateDuration(Location start, Location end) {
            double distance = calculateDistance(start, end);
            // Assume average speed of 40 km/h
            return (int) (distance / 40.0 * 60);
        }
        
        /**
         * Calculates distance between two locations
         * @param loc1 first location
         * @param loc2 second location
         * @return distance in kilometers
         */
        private double calculateDistance(Location loc1, Location loc2) {
            double latDiff = loc1.getLatitude() - loc2.getLatitude();
            double lonDiff = loc1.getLongitude() - loc2.getLongitude();
            return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
        }
    }
    
    // ==================== Singleton Platform ====================
    
    /**
     * RideSharingPlatformManager - Singleton class for managing the platform
     */
    public static class RideSharingPlatformManager {
        private static RideSharingPlatformManager instance;
        private final BookingService bookingService;
        private final List<Rider> registeredRiders;
        
        /**
         * Private constructor for Singleton pattern
         */
        private RideSharingPlatformManager() {
            this.bookingService = new BookingService();
            this.registeredRiders = new ArrayList<>();
        }
        
        /**
         * Gets the singleton instance of the platform manager
         * @return singleton instance
         */
        public static synchronized RideSharingPlatformManager getInstance() {
            if (instance == null) {
                instance = new RideSharingPlatformManager();
            }
            return instance;
        }
        
        /**
         * Registers a rider in the system
         * @param rider rider to register
         */
        public void registerRider(Rider rider) {
            if (rider == null) {
                throw new IllegalArgumentException("Rider cannot be null");
            }
            registeredRiders.add(rider);
            System.out.println("Rider registered: " + rider.getName());
        }
        
        /**
         * Gets the booking service
         * @return booking service instance
         */
        public BookingService getBookingService() {
            return bookingService;
        }
        
        /**
         * Gets all registered riders
         * @return list of registered riders
         */
        public List<Rider> getRegisteredRiders() {
            return new ArrayList<>(registeredRiders);
        }
    }
    
    // ==================== Main Method ====================
    
    /**
     * Main method demonstrating the ride-sharing platform
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== Ride Sharing Platform ===\n");
        
        // Get platform instance
        RideSharingPlatformManager platform = RideSharingPlatformManager.getInstance();
        
        // Create locations
        Location startLocation = new Location(10.0, 10.0);
        Location endLocation = new Location(20.0, 20.0);
        
        // Create riders
        Rider rider = new Rider("Himanshu", "himanshu@example.com", 
                               "8840000000", startLocation, endLocation);
        
        // Create vehicles
        Vehicle car1 = new Vehicle("DL12345", VehicleType.CAR, "Toyota Camry");
        Vehicle car2 = new Vehicle("DL67890", VehicleType.SUV, "Mahindra XUV");
        Vehicle bike1 = new Vehicle("DL11111", VehicleType.BIKE, "Royal Enfield");
        
        // Create drivers
        Location driverLoc1 = new Location(10.2, 10.1);
        Location driverLoc2 = new Location(10.5, 10.3);
        Location driverLoc3 = new Location(11.0, 11.0);
        
        Driver driver1 = new Driver("Sanjay", "9876543210", car1);
        Driver driver2 = new Driver("Rahul", "9876543211", car2);
        Driver driver3 = new Driver("Priya", "9876543212", bike1);
        
        driver1.setLocation(driverLoc1);
        driver2.setLocation(driverLoc2);
        driver3.setLocation(driverLoc3);
        
        // Set driver ratings
        driver1.setRating(4.5f);
        driver2.setRating(4.8f);
        driver3.setRating(4.2f);
        
        // Register riders and drivers
        platform.registerRider(rider);
        platform.getBookingService().registerDriver(driver1);
        platform.getBookingService().registerDriver(driver2);
        platform.getBookingService().registerDriver(driver3);
        
        System.out.println();
        
        // Book a ride using default strategy (nearest driver)
        System.out.println("--- Booking Ride (Nearest Driver Strategy) ---");
        Ride ride = platform.getBookingService().bookRide(rider);
        
        if (ride != null) {
            // Simulate ride completion with rating
            System.out.println("\n--- Completing Ride ---");
            platform.getBookingService().completeRide(ride, 4.5f);
            
            // Display ride summary
            System.out.println("\n--- Ride Summary ---");
            System.out.println("Ride ID: " + ride.getRideId());
            System.out.println("Rider: " + ride.getRider().getName());
            System.out.println("Driver: " + ride.getDriver().getName());
            System.out.println("Status: " + ride.getStatus());
            System.out.println("Fare: $" + String.format("%.2f", ride.getFare()));
        }
        
        System.out.println("\n=== Demo Complete ===");
    }
}