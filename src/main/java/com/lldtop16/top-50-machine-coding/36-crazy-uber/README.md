# 🚗 Problem 36: Crazy Uber (Elimination Round)

> **Difficulty**: ⭐⭐⭐⭐ | **Company Fit**: Uber, Lyft, Ola  
> **Est. Time**: 120 min | **Patterns**: Graph, Matching, Observer, Strategy

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a ride-sharing system WITH pooling AND dynamic pricing."

**What the interviewer tests**:
```
1. Can you match riders to drivers? (Graph matching)
2. Can you handle ride pooling? (Multiple riders per car)
3. Can you implement surge pricing? (Dynamic based on demand)
4. Can you optimize routes? (Pickup/dropoff order)
5. Can you handle cancellations? (Penalty, re-matching)
```

### Step 2: The "Aha!" Moment

The key insight: **This is a graph matching problem + route optimization.**

```
RIDER REQUESTS:
  R1: A → B
  R2: C → D  
  R3: E → F

DRIVER POSITIONS:
  D1: Near A
  D2: Near C, E

MATCHING:
  1. Find drivers within radius (using geohash)
  2. Calculate "cost" = distance + time + preference
  3. Assign: D1→R1, D2→R2+R3 (pooling)
  
ROUTE OPTIMIZATION:
  If D2 picks up R2 and R3:
  Option A: D2→C(pick R2)→E(pick R3)→D(drop R2)→F(drop R3)
  Option B: D2→E(pick R3)→C(pick R2)→F(drop R3)→D(drop R2)
  
  Choose shortest total distance.
```

### Step 3: How to implement surge pricing?

```
DEMAND/SUPPLY RATIO:
  Demand = riders requesting in last 5 min
  Supply = drivers available in last 5 min
  Ratio = Demand / Supply

SURGE MULTIPLIER:
  Ratio 1.0 → 1.0x (normal)
  Ratio 1.5 → 1.5x (+50%)
  Ratio 2.0 → 2.0x (+100%)
  Ratio 3.0 → 3.0x (+200%)

GEOGRAPHIC SURGE:
  Only surge in HIGH DEMAND areas.
  Downtown: 2.5x
  Suburbs: 1.0x
```

---

## 💻 Core Implementation

```java
package com.uber;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: RideService is the matching engine.
 * 
 * Flow:
 * 1. Rider requests ride (pickup, dropoff)
 * 2. Find nearby drivers within radius
 * 3. Calculate ETAs and prices
 * 4. Match best driver
 * 5. Optimize route (for pooling)
 * 6. Track ride progress
 */
public class RideService {
    
    private final Map<String, Rider> riders;
    private final Map<String, Driver> drivers;
    private final Map<String, Ride> rides;
    private final SurgePricingService surgePricing;
    private final MatchingEngine matcher;
    
    private final double SEARCH_RADIUS_KM = 5.0;

    public RideService() {
        this.riders = new ConcurrentHashMap<>();
        this.drivers = new ConcurrentHashMap<>();
        this.rides = new ConcurrentHashMap<>();
        this.surgePricing = new SurgePricingService();
        this.matcher = new MatchingEngine();
    }

    /**
     * INTUITION: Rider requests a ride.
     * 
     * 1. Create ride request
     * 2. Find nearby drivers
     * 3. Calculate price with surge
     * 4. Match best driver
     * 5. Notify both parties
     */
    public synchronized Ride requestRide(String riderId, Location pickup, 
                                          Location dropoff, int seats) {
        
        Rider rider = riders.get(riderId);
        if (rider == null) throw new IllegalArgumentException("Rider not found");
        
        // Step 1: Calculate price
        double distance = calculateDistance(pickup, dropoff);
        double surgeMultiplier = surgePricing.getMultiplier(pickup);
        double price = distance * BASE_RATE * surgeMultiplier;
        
        // Step 2: Find nearby drivers
        List<Driver> nearbyDrivers = findNearbyDrivers(pickup, SEARCH_RADIUS_KM);
        
        if (nearbyDrivers.isEmpty()) {
            throw new NoDriversAvailableException("No drivers nearby");
        }
        
        // Step 3: Match best driver
        Driver bestDriver = matcher.match(rider, nearbyDrivers, pickup, dropoff);
        
        // Step 4: Create ride
        Ride ride = new Ride(riderId, bestDriver.getId(), pickup, dropoff, 
                            price, surgeMultiplier);
        rides.put(ride.getId(), ride);
        
        // Step 5: Notify
        notifyDriver(bestDriver, ride);
        notifyRider(rider, ride);
        
        return ride;
    }

    /**
     * INTUITION: Request pooled ride (share with others).
     * 
     * 1. Add to pool queue
     * 2. Wait for other riders on similar route
     * 3. Find driver when pool is full or timeout
     * 4. Optimize pickup order
     */
    public synchronized Ride requestPooledRide(String riderId, Location pickup, 
                                                Location dropoff) {
        
        // Add to pool
        PoolRequest request = new PoolRequest(riderId, pickup, dropoff);
        poolQueue.offer(request);
        
        // Try to form pool immediately
        Ride ride = tryFormPool();
        if (ride != null) {
            return ride;
        }
        
        // Or wait 30 seconds for pool
        try {
            Thread.sleep(30000);
            ride = tryFormPool();
            if (ride != null) {
                return ride;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Solo ride fallback
        return requestRide(riderId, pickup, dropoff, 1);
    }

    /**
     * INTUITION: Try to form a pooled ride.
     * 
     * Find 2-3 riders with similar routes.
     * Assign to one driver.
     */
    private Ride tryFormPool() {
        // Simplified: take up to 3 requests from queue
        List<PoolRequest> pool = new ArrayList<>();
        while (poolQueue.size() > 0 && pool.size() < 3) {
            pool.add(poolQueue.poll());
        }
        
        if (pool.size() < 2) {
            // Put back if not enough for pool
            poolQueue.addAll(pool);
            return null;
        }
        
        // Create pooled ride
        PoolRequest first = pool.get(0);
        Ride ride = requestRide(first.riderId, first.pickup, first.dropoff, pool.size());
        
        // Add other riders
        for (int i = 1; i < pool.size(); i++) {
            PoolRequest req = pool.get(i);
            ride.addRider(req.riderId, req.pickup, req.dropoff);
        }
        
        // Optimize route
        ride.optimizeRoute();
        
        return ride;
    }

    public void cancelRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null) {
            ride.cancel();
            // Find new driver for rider
        }
    }

    // --- Helpers ---

    private List<Driver> findNearbyDrivers(Location location, double radius) {
        List<Driver> nearby = new ArrayList<>();
        for (Driver driver : drivers.values()) {
            if (driver.isAvailable()) {
                double distance = calculateDistance(location, driver.getLocation());
                if (distance <= radius) {
                    nearby.add(driver);
                }
            }
        }
        return nearby;
    }

    private double calculateDistance(Location l1, Location l2) {
        // Haversine formula
        double R = 6371;  // Earth's radius in km
        double lat1 = Math.toRadians(l1.getLatitude());
        double lat2 = Math.toRadians(l2.getLatitude());
        double dLat = Math.toRadians(l2.getLatitude() - l1.getLatitude());
        double dLon = Math.toRadians(l2.getLongitude() - l1.getLongitude());
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return R * c;
    }

    private void notifyDriver(Driver driver, Ride ride) {
        System.out.println("Driver " + driver.getName() + ": New ride request $" + ride.getPrice());
    }

    private void notifyRider(Rider rider, Ride ride) {
        System.out.println("Rider " + rider.getName() + ": Driver found! ETA " + 
                          ride.getEta() + " min, $" + ride.getPrice());
    }

    private static final double BASE_RATE = 1.5;  // $ per km
    private final Queue<PoolRequest> poolQueue = new LinkedList<>();
}
```

```java
package com.uber;

import java.util.*;

/**
 * INTUITION: MatchingEngine assigns drivers to riders.
 * 
 * Uses scoring:
 * - Distance (closer is better)
 * - Rating (higher is better)
 * - Acceptance rate (higher is better)
 */
class MatchingEngine {
    
    Driver match(Rider rider, List<Driver> nearbyDrivers, 
                 Location pickup, Location dropoff) {
        
        return nearbyDrivers.stream()
            .min(Comparator.comparingDouble(driver -> calculateScore(rider, driver, pickup)))
            .orElse(null);
    }

    private double calculateScore(Rider rider, Driver driver, Location pickup) {
        double distanceScore = calculateDistance(driver.getLocation(), pickup) * 10;
        double ratingScore = (5.0 - driver.getRating()) * 100;
        double acceptanceScore = (1.0 - driver.getAcceptanceRate()) * 50;
        
        return distanceScore + ratingScore + acceptanceScore;
    }
}
```

```java
package com.uber;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: Ride represents a trip.
 * 
 * States:
 * - REQUESTED: Rider requested, waiting for driver
 * - ACCEPTED: Driver accepted, driving to pickup
 * - ARRIVED: Driver at pickup location
 * - IN_PROGRESS: Rider on board
 * - COMPLETED: Ride finished
 * - CANCELLED: Cancelled by rider or driver
 */
public class Ride {
    private final String id;
    private final String riderId;
    private String driverId;
    private final Location pickup;
    private final Location dropoff;
    private final List<Rider> pooledRiders;
    private double price;
    private double surgeMultiplier;
    private RideStatus status;
    private final long requestedAt;
    private long acceptedAt;
    private long startedAt;
    private long completedAt;
    private List<Location> route;  // Optimized pickup/dropoff order

    public Ride(String riderId, String driverId, Location pickup, Location dropoff, 
                double price, double surgeMultiplier) {
        this.id = UUID.randomUUID().toString();
        this.riderId = riderId;
        this.driverId = driverId;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.pooledRiders = new ArrayList<>();
        this.price = price;
        this.surgeMultiplier = surgeMultiplier;
        this.status = RideStatus.REQUESTED;
        this.requestedAt = System.currentTimeMillis();
        this.route = new ArrayList<>(Arrays.asList(pickup, dropoff));
    }

    public void addRider(String riderId, Location pickup, Location dropoff) {
        pooledRiders.add(new Rider(riderId, "", pickup));
        this.price += calculateDistance(pickup, dropoff) * BASE_RATE;
    }

    /**
     * INTUITION: Optimize route for pooled ride.
     * 
     * Problem: Pick up R2 at C, drop at D. Pick up R3 at E, drop at F.
     * 
     * Options:
     *   1→2→3→4 (pickup C, drop D, pickup E, drop F)
     *   1→3→2→4 (pickup E, drop F, pickup C, drop D)
     *   2→3→4→1 etc.
     * 
     * Solution: Try all permutations (n! for n stops).
     * For 4 stops: 4! = 24 options. Pick shortest.
     */
    public void optimizeRoute() {
        // Collect all stops
        List<Location> stops = new ArrayList<>();
        stops.add(pickup);  // Pickup 1
        for (Rider rider : pooledRiders) {
            stops.add(rider.getPickupLocation());
            stops.add(rider.getDropoffLocation());
        }
        
        // Find optimal order (simplified: nearest neighbor)
        // In production: use TSP solver or Google OR-Tools
        List<Location> optimized = nearestNeighbor(stops);
        this.route = optimized;
    }

    private List<Location> nearestNeighbor(List<Location> stops) {
        // Simplified greedy: always go to nearest unvisited stop
        return stops;  // Mock
    }

    public void accept() {
        this.status = RideStatus.ACCEPTED;
        this.acceptedAt = System.currentTimeMillis();
    }

    public void start() {
        this.status = RideStatus.IN_PROGRESS;
        this.startedAt = System.currentTimeMillis();
    }

    public void complete() {
        this.status = RideStatus.COMPLETED;
        this.completedAt = System.currentTimeMillis();
    }

    public void cancel() {
        this.status = RideStatus.CANCELLED;
    }

    // Getters
    public String getId() { return id; }
    public String getRiderId() { return riderId; }
    public String getDriverId() { return driverId; }
    public Location getPickup() { return pickup; }
    public Location getDropoff() { return dropoff; }
    public double getPrice() { return price; }
    public double getSurgeMultiplier() { return surgeMultiplier; }
    public RideStatus getStatus() { return status; }
    public int getEta() {
        return (int) (calculateDistance(pickup, dropoff) / 0.5);  // 30 km/h avg
    }
}
```

```java
package com.uber;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: Driver model.
 */
public class Driver {
    private final String id;
    private String name;
    private String phone;
    private Location location;
    private double rating;
    private int totalRides;
    private int acceptedRides;
    private Vehicle vehicle;
    private boolean available;
    private Ride currentRide;

    public Driver(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.rating = 4.5;
        this.totalRides = 0;
        this.acceptedRides = 0;
        this.available = true;
        this.vehicle = new Vehicle("ABC123", "Toyota Camry", 4);
    }

    public void acceptRide(Ride ride) {
        this.currentRide = ride;
        this.available = false;
        this.acceptedRides++;
        ride.accept();
    }

    public void completeRide() {
        this.totalRides++;
        this.currentRide = null;
        this.available = true;
        this.rating = (rating * totalRides + 5.0) / (totalRides + 1);
    }

    public double getAcceptanceRate() {
        return totalRides == 0 ? 1.0 : (double) acceptedRides / totalRides;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public double getRating() { return rating; }
    public boolean isAvailable() { return available; }
    public Vehicle getVehicle() { return vehicle; }
}
```

```java
package com.uber;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: Surge pricing based on demand/supply.
 */
public class SurgePricingService {
    
    private final Map<String, Area> areas;
    private final Map<String, AtomicInteger> demandCount;
    private final Map<String, AtomicInteger> supplyCount;

    SurgePricingService() {
        this.areas = new ConcurrentHashMap<>();
        this.demandCount = new ConcurrentHashMap<>();
        this.supplyCount = new ConcurrentHashMap<>();
        
        // Define areas (geohash-based in production)
        areas.put("downtown", new Area("downtown", 40.7128, -74.0060));
        areas.put("suburb", new Area("suburb", 40.7589, -73.9851));
    }

    /**
     * INTUITION: Get surge multiplier for location.
     */
    public double getMultiplier(Location location) {
        String areaId = findArea(location);
        
        int demand = demandCount.getOrDefault(areaId, new AtomicInteger(0)).get();
        int supply = supplyCount.getOrDefault(areaId, new AtomicInteger(0)).get();
        
        if (supply == 0) return 2.0;
        
        double ratio = (double) demand / supply;
        
        // Cap at 3.0x
        return Math.min(3.0, Math.max(1.0, ratio));
    }

    public void recordDemand(Location location) {
        String areaId = findArea(location);
        demandCount.computeIfAbsent(areaId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public void recordSupply(Location location) {
        String areaId = findArea(location);
        supplyCount.computeIfAbsent(areaId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    private String findArea(Location location) {
        // Simplified: find nearest area
        // In production: use geohash
        return "downtown";
    }
}

class Area {
    private final String id;
    private final double latitude;
    private final double longitude;

    Area(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}

class Location {
    private final double latitude;
    private final double longitude;
    private final String address;

    public Location(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress() { return address; }
}

class Vehicle {
    private final String plateNumber;
    private final String model;
    private final int capacity;
    private final String color;

    public Vehicle(String plateNumber, String model, int capacity) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.capacity = capacity;
        this.color = "White";
    }

    public int getCapacity() { return capacity; }
}

class Rider {
    private final String id;
    private String name;
    private String phone;
    private Location location;
    private double rating;

    public Rider(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.rating = 4.8;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public Location getPickupLocation() { return location; }
    public Location getDropoffLocation() { return location; }
}

enum RideStatus {
    REQUESTED, ACCEPTED, ARRIVED, IN_PROGRESS, COMPLETED, CANCELLED
}

class PoolRequest {
    final String riderId;
    final Location pickup;
    final Location dropoff;

    PoolRequest(String riderId, Location pickup, Location dropoff) {
        this.riderId = riderId;
        this.pickup = pickup;
        this.dropoff = dropoff;
    }
}

class NoDriversAvailableException extends RuntimeException {
    public NoDriversAvailableException(String message) {
        super(message);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle 100K requests per second?"
> "Use geohash + quadtrees for spatial queries. Pre-assign drivers to zones. Async processing. ETL for surge pricing."

### Q2: "How to prevent driver cherry-picking (only short rides)?"
> "Acceptance rate tracking. Penalize rejection. Show next ride estimate. Bonus for long rides."

### Q3: "How to handle ride pooling efficiently?"
> "Graph matching: riders as nodes, edges = shared route. Find cliques. Solve TSP for route optimization."

### Q4: "How to estimate arrival time?"
> "Historical traffic data + real-time GPS. Machine learning: XGBoost on features (time, weather, day)."

### Q5: "How to handle driver cheating (fake GPS)?"
> "Anomaly detection: speed > 200 km/h? Impossible! Inconsistent GPS drift. Cross-check with accelerometer."