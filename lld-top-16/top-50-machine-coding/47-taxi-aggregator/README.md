# 🚕 Problem 47: Taxi Aggregator (Ola/Uber)

> **Difficulty**: ⭐⭐⭐⭐ | **Company Fit**: Uber, Ola, Lyft  
> **Est. Time**: 120 min | **Patterns**: Graph, Matching, Observer, Strategy

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a taxi aggregator with ride scheduling."

**What the interviewer tests**:
```
1. Can you schedule rides in advance? (Booking vs instant)
2. Can you handle multiple cab types? (Mini, Sedan, SUV, Luxury)
3. Can you manage driver allocation? (Shift-based)
4. Can you handle surge pricing? (Time, location, demand)
```

### Step 2: The "Aha!" Moment

The key insight: **Two modes - instant vs scheduled.**

```
INSTANT RIDE:
  User: "Book now"
  System: Find nearest available driver
  Time: Driver arrives in 5-10 min
  
SCHEDULED RIDE:
  User: "Book for tomorrow 9 AM"
  System: Assign driver, block calendar
  Time: Driver arrives at 9 AM
  
DRIVER MANAGEMENT:
  - Shift A: 6 AM - 2 PM
  - Shift B: 2 PM - 10 PM
  - Shift C: 10 PM - 6 AM
  
  Need to ensure coverage across shifts.
```

### Step 3: How to optimize allocation?

```
DRIVER-PASSENGER MATCHING:
  Constraints:
  - Driver location < 2km from pickup
  - Driver shift matches ride time
  - Cab type matches requested type
  - Driver rating > 4.0 (optional)
  
  Scoring:
  - Distance (closer = better)
  - Rating (higher = better)
  - Acceptance rate (higher = better)
```

---

## 💻 Core Implementation

```java
package com.taxi;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: TaxiAggregator manages ride booking and driver allocation.
 * 
 * Supports:
 * - Instant rides (book now)
 * - Scheduled rides (book for later)
 * - Multiple cab types
 * - Surge pricing
 */
public class TaxiAggregator {
    
    private final Map<String, User> users;
    private final Map<String, Driver> drivers;
    private final Map<String, Cab> cabs;
    private final Map<String, Ride> rides;
    private final MatchingEngine matchingEngine;
    private final PricingService pricingService;

    public TaxiAggregator() {
        this.users = new ConcurrentHashMap<>();
        this.drivers = new ConcurrentHashMap<>();
        this.cabs = new ConcurrentHashMap<>();
        this.rides = new ConcurrentHashMap<>();
        this.matchingEngine = new MatchingEngine();
        this.pricingService = new PricingService();
        
        // Initialize sample data
        initializeSampleData();
    }

    /**
     * INTUITION: Book instant ride.
     * 
     * 1. Find nearest available drivers
     * 2. Match best driver
     * 3. Calculate price
     * 4. Create ride
     * 5. Notify driver
     */
    public synchronized Ride bookInstantRide(String userId, Location pickup, 
                                             Location dropoff, CabType type) {
        
        User user = users.get(userId);
        if (user == null) throw new IllegalArgumentException("User not found");
        
        // Find available drivers with requested cab type
        List<Driver> available = findAvailableDrivers(pickup, type);
        
        if (available.isEmpty()) {
            throw new NoDriversAvailableException("No drivers available");
        }
        
        // Match best driver
        Driver driver = matchingEngine.match(user, available, pickup, dropoff);
        
        // Calculate price
        double distance = calculateDistance(pickup, dropoff);
        double price = pricingService.calculatePrice(distance, type);
        
        // Create ride
        Ride ride = new Ride(userId, driver.getId(), pickup, dropoff, type, price);
        rides.put(ride.getId(), ride);
        
        // Assign cab
        Cab cab = driver.getAssignedCab();
        ride.setCabId(cab.getId());
        
        // Notify driver
        notifyDriver(driver, ride);
        
        return ride;
    }

    /**
     * INTUITION: Schedule ride for later.
     * 
     * 1. Validate time (at least 1 hour from now)
     * 2. Find available drivers for that time slot
     * 3. Pre-assign driver
     * 4. Create scheduled ride
     */
    public synchronized Ride scheduleRide(String userId, Location pickup, 
                                          Location dropoff, CabType type,
                                          LocalDateTime scheduledTime) {
        
        // Validate time
        if (scheduledTime.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IllegalArgumentException("Schedule at least 1 hour in advance");
        }
        
        User user = users.get(userId);
        
        // Find drivers available at scheduled time
        List<Driver> available = findAvailableDriversForTime(scheduledTime, type);
        
        if (available.isEmpty()) {
            throw new NoDriversAvailableException("No drivers available for this time");
        }
        
        Driver driver = matchingEngine.match(user, available, pickup, dropoff);
        
        // Calculate price
        double distance = calculateDistance(pickup, dropoff);
        double price = pricingService.calculateScheduledPrice(distance, type, scheduledTime);
        
        // Create scheduled ride
        Ride ride = new Ride(userId, driver.getId(), pickup, dropoff, type, price);
        ride.setScheduled(true);
        ride.setScheduledTime(scheduledTime);
        rides.put(ride.getId(), ride);
        
        // Block driver's calendar
        blockDriverCalendar(driver.getId(), scheduledTime, ride.getEstimatedDuration());
        
        // Notify driver
        notifyDriver(driver, ride);
        
        return ride;
    }

    /**
     * Find drivers currently available.
     */
    private List<Driver> findAvailableDrivers(Location pickup, CabType type) {
        List<Driver> available = new ArrayList<>();
        
        for (Driver driver : drivers.values()) {
            if (driver.isAvailable() && driver.getCab().getType() == type) {
                double distance = calculateDistance(pickup, driver.getLocation());
                if (distance < 5.0) {  // Within 5km
                    available.add(driver);
                }
            }
        }
        
        return available;
    }

    /**
     * Find drivers available at specific time.
     */
    private List<Driver> findAvailableDriversForTime(LocalDateTime time, CabType type) {
        List<Driver> available = new ArrayList<>();
        
        for (Driver driver : drivers.values()) {
            if (isDriverAvailableAt(driver, time) && driver.getCab().getType() == type) {
                available.add(driver);
            }
        }
        
        return available;
    }

    private boolean isDriverAvailableAt(Driver driver, LocalDateTime time) {
        // Check driver's shift schedule
        for (Shift shift : driver.getShifts()) {
            if (shift.contains(time)) {
                // Check if no other ride scheduled
                return !driver.hasRideAt(time);
            }
        }
        return false;
    }

    private void blockDriverCalendar(String driverId, LocalDateTime start, int durationMinutes) {
        Driver driver = drivers.get(driverId);
        if (driver != null) {
            driver.blockTime(start, start.plusMinutes(durationMinutes));
        }
    }

    private double calculateDistance(Location l1, Location l2) {
        // Haversine formula
        return l1.distanceTo(l2);
    }

    private void notifyDriver(Driver driver, Ride ride) {
        System.out.println("🚕 Driver " + driver.getName() + ": New ride $" + ride.getPrice());
    }

    // --- Helpers ---

    public Ride getRide(String rideId) {
        return rides.get(rideId);
    }

    public void cancelRide(String rideId) {
        Ride ride = rides.remove(rideId);
        if (ride != null) {
            // Free driver
            Driver driver = drivers.get(ride.getDriverId());
            if (driver != null) {
                driver.cancelRide(ride);
            }
        }
    }

    private void initializeSampleData() {
        // Create sample drivers
        Driver driver1 = new Driver("D1", "John", new Location(40.7128, -74.0060));
        driver1.assignCab(new Cab("C1", "ABC123", CabType.SEDAN));
        drivers.put("D1", driver1);
        
        Driver driver2 = new Driver("D2", "Jane", new Location(40.7589, -73.9851));
        driver2.assignCab(new Cab("C2", "XYZ789", CabType.MINI));
        drivers.put("D2", driver2);
    }
}
```

```java
package com.taxi;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Driver with availability and schedule.
 */
public class Driver {
    private final String driverId;
    private String name;
    private Location location;
    private double rating;
    private Cab assignedCab;
    private boolean available;
    private Ride currentRide;
    private final List<Shift> shifts;
    private final Set<TimeSlot> blockedTimes;

    public Driver(String driverId, String name, Location location) {
        this.driverId = driverId;
        this.name = name;
        this.location = location;
        this.rating = 4.5;
        this.available = true;
        this.shifts = new ArrayList<>();
        this.blockedTimes = new HashSet<>();
        
        // Default shift: full time
        this.shifts.add(new Shift(LocalDateTime.now().minusDays(1), 
                                  LocalDateTime.now().plusDays(1)));
    }

    public void assignCab(Cab cab) {
        this.assignedCab = cab;
    }

    public boolean isAvailable() {
        return available && currentRide == null;
    }

    public boolean hasRideAt(LocalDateTime time) {
        return blockedTimes.contains(new TimeSlot(time, time.plusHours(1)));
    }

    public void blockTime(LocalDateTime start, LocalDateTime end) {
        blockedTimes.add(new TimeSlot(start, end));
        this.available = false;
    }

    public void cancelRide(Ride ride) {
        if (this.currentRide == ride) {
            this.currentRide = null;
            this.available = true;
            blockedTimes.remove(new TimeSlot(ride.getScheduledTime(), 
                ride.getScheduledTime().plusMinutes(ride.getEstimatedDuration())));
        }
    }

    // Getters
    public String getId() { return driverId; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public double getRating() { return rating; }
    public Cab getAssignedCab() { return assignedCab;; }
    public List<Shift> getShifts() { return shifts; }
}

/**
 * Shift represents working hours.
 */
class Shift {
    private final LocalDateTime start;
    private final LocalDateTime end;

    Shift(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    boolean contains(LocalDateTime time) {
        return !time.isBefore(start) && !time.isAfter(end);
    }
}

class TimeSlot {
    private final LocalDateTime start;
    private final LocalDateTime end;

    TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return start.equals(timeSlot.start) && end.equals(timeSlot.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
```

```java
package com.taxi;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Ride can be instant or scheduled.
 */
public class Ride {
    private final String rideId;
    private final String userId;
    private String driverId;
    private final Location pickup;
    private final Location dropoff;
    private final CabType type;
    private final double price;
    private final LocalDateTime requestedAt;
    private LocalDateTime scheduledTime;
    private boolean scheduled;
    private RideStatus status;
    private String cabId;
    private int estimatedDuration;  // in minutes

    public Ride(String userId, String driverId, Location pickup, Location dropoff,
                CabType type, double price) {
        this.rideId = UUID.randomUUID().toString();
        this.userId = userId;
        this.driverId = driverId;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.type = type;
        this.price = price;
        this.requestedAt = LocalDateTime.now();
        this.status = RideStatus.REQUESTED;
    }

    public void accept() {
        this.status = RideStatus.ACCEPTED;
    }

    public void start() {
        this.status = RideStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = RideStatus.COMPLETED;
    }

    public void cancel() {
        this.status = RideStatus.CANCELLED;
    }

    // Getters
    public String getId() { return rideId; }
    public String getUserId() { return userId; }
    public String getDriverId() { return driverId; }
    public Location getPickup() { return pickup; }
    public Location getDropoff() { return dropoff; }
    public double getPrice() { return price; }
    public RideStatus getStatus() { return status; }
    public boolean isScheduled() { return scheduled; }
    public void setScheduled(boolean scheduled) { this.scheduled = scheduled; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime time) { this.scheduledTime = time; }
    public String getCabId() { return cabId; }
    public void setCabId(String cabId) { this.cabId = cabId; }
    public int getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(int duration) { this.estimatedDuration = duration; }
}

enum RideStatus {
    REQUESTED, ACCEPTED, ARRIVED, IN_PROGRESS, COMPLETED, CANCELLED
}
```

```java
package com.taxi;

import java.time.LocalDateTime;

/**
 * Cab (vehicle) information.
 */
public class Cab {
    private final String cabId;
    private final String plateNumber;
    private final String model;
    private final CabType type;
    private final String color;

    public Cab(String cabId, String plateNumber, CabType type) {
        this.cabId = cabId;
        this.plateNumber = plateNumber;
        this.type = type;
        this.model = "Generic";
        this.color = "White";
    }

    public String getId() { return cabId; }
    public CabType getType() { return type; }
}

enum CabType {
    MINI(4, 1.0),        // 4 seats, base price
    SEDAN(4, 1.5),       // 4 seats, 1.5x price
    SUV(6, 2.0);         // 6 seats, 2x price
    
    private final int capacity;
    private final double priceMultiplier;
    
    CabType(int capacity, double priceMultiplier) {
        this.capacity = capacity;
        this.priceMultiplier = priceMultiplier;
    }
    
    public int getCapacity() { return capacity; }
    public double getPriceMultiplier() { return priceMultiplier; }
}
```

```java
package com.taxi;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Location with distance calculation.
 */
class Location {
    private final double latitude;
    private final double longitude;
    private final String address;

    Location(double latitude, double longitude) {
        this(latitude, longitude, "");
    }

    Location(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    double distanceTo(Location other) {
        // Haversine
        double R = 6371;
        double lat1 = Math.toRadians(latitude);
        double lat2 = Math.toRadians(other.latitude);
        double dLat = Math.toRadians(other.latitude - latitude);
        double dLon = Math.toRadians(other.longitude - longitude);
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return R * c;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
```

```java
package com.taxi;

import java.util.*;

/**
 * Pricing with surge and cab type.
 */
class PricingService {
    private static final double BASE_RATE = 1.5;  // per km
    private static final double MIN_FARE = 5.0;

    double calculatePrice(double distance, CabType type) {
        double price = Math.max(MIN_FARE, distance * BASE_RATE * type.getPriceMultiplier());
        return price;
    }

    double calculateScheduledPrice(double distance, CabType type, LocalDateTime time) {
        double price = calculatePrice(distance, type);
        
        // Peak hour surcharge (6-9 AM, 5-8 PM)
        int hour = time.getHour();
        if ((hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 20)) {
            price *= 1.5;
        }
        
        // Midnight surcharge (10 PM - 6 AM)
        if (hour >= 22 || hour <= 5) {
            price *= 1.3;
        }
        
        return price;
    }
}
```

```java
package com.taxi;

import java.util.*;

/**
 * Driver matching engine.
 */
class MatchingEngine {
    
    Driver match(User user, List<Driver> drivers, Location pickup, Location dropoff) {
        return drivers.stream()
            .min(Comparator.comparingDouble(d -> calculateScore(d, pickup)))
            .orElse(null);
    }

    private double calculateScore(Driver driver, Location pickup) {
        double distanceScore = driver.getLocation().distanceTo(pickup) * 10;
        double ratingScore = (5.0 - driver.getRating()) * 100;
        return distanceScore + ratingScore;
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle no-shows for scheduled rides?"
> "Call/SMS 15 min before. Wait 5 min. If no-show: cancel + penalty. Refund if driver cancels."

### Q2: "How to pool multiple bookings on same route?"
> "Check overlapping routes. Share cab. Split fare. Schedule pickups along route."

### Q3: "How to handle cancellation fees?"
> "Free cancellation > 1 hour before. 50% fee within 1 hour. No refund after pickup. Driver gets %."

### Q4: "How to handle driver preferences (home area)?"
> "Track preferred zones. Prioritize assignments in home area. Bonus for airport trips."