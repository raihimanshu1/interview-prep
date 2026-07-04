# 📦 Problem 44: Amazon Locker System

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Amazon, logistics companies  
> **Est. Time**: 90 min | **Patterns**: Strategy, Observer, State

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a pickup locker system for packages."

**What the interviewer tests**:
```
1. Can you manage locker inventory? (Allocate/deallocate)
2. Can you handle different package sizes? (S, M, L, XL)
3. Can you handle time-based pickup? (48-hour window)
4. Can you optimize locker usage? (Best fit algorithm)
```

### Step 2: The "Aha!" Moment

The key insight: **Lockers are resources, packages are reservations.**

```
LOCKER TYPES:
  Small:   10x10x10 cm  (phone, accessories)
  Medium:  20x20x30 cm  (shoes, books)
  Large:   40x40x40 cm  (clothing box)
  XL:      60x60x60 cm  (large appliances)

PACKAGE:
  Size: MEDIUM
  Locker needed: 20x20x30 cm
  Available: Locker A7 (Large), Locker B2 (Medium)
  → Assign to B2 (best fit, saves Large for bigger packages)

TIMING:
  Package delivered: 10:00 AM
  Pickup window: 10:00 AM - 10:00 AM + 48 hours
  After 48h: Return to sender or donation
```

### Step 3: How to optimize allocation?

```
BEST FIT ALGORITHM:
  1. Filter lockers by size (S can fit in M, L, XL)
  2. Choose smallest locker that fits
  3. This maximizes capacity for large packages

PACKAGE FLOW:
  [Customer orders]
    ↓
  [Warehouse packs]
    ↓
  [Delivery agent delivers to locker]
    ↓
  [OTP sent to customer]
    ↓
  [Customer picks up with OTP]
    ↓
  [Locker freed]
```

---

## 💻 Core Implementation

```java
package com.locker;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: LockerService manages locker allocation and reservations.
 * 
 * Core operations:
 * - Find available locker for package
 * - Allocate locker (reserve)
 * - Pickup package (free locker)
 * - Handle expired packages
 */
public class LockerService {
    
    // All lockers: lockerId → Locker
    private final Map<String, Locker> lockers;
    
    // All packages: packageId → Package
    private final Map<String, Package> packages;
    
    // Reservations: lockerId → packageId
    private final Map<String, String> reservations;
    
    // Package locations: packageId → lockerId
    private final Map<String, String> packageLocations;
    
    private final LockerAllocationStrategy allocationStrategy;

    public LockerService() {
        this.lockers = new ConcurrentHashMap<>();
        this.packages = new ConcurrentHashMap<>();
        this.reservations = new ConcurrentHashMap<>();
        this.packageLocations = new ConcurrentHashMap<>();
        this.allocationStrategy = new BestFitStrategy();
        
        // Initialize lockers (in production: load from DB)
        initializeLockers();
    }

    /**
     * INTUITION: Find locker for package.
     * 
     * 1. Get package size
     * 2. Find all lockers that can fit it
     * 3. Choose best fit (smallest locker)
     * 4. Reserve it
     */
    public synchronized Locker findLocker(Package pkg) {
        List<Locker> available = new ArrayList<>();
        
        for (Locker locker : lockers.values()) {
            if (locker.getStatus() == LockerStatus.AVAILABLE && 
                locker.canFit(pkg.getSize())) {
                available.add(locker);
            }
        }
        
        if (available.isEmpty()) {
            return null;  // No locker available
        }
        
        // Apply allocation strategy
        Locker selected = allocationStrategy.select(available, pkg.getSize());
        
        // Reserve locker
        reserveLocker(selected.getId(), pkg);
        
        return selected;
    }

    /**
     * INTUITION: Reserve locker for package.
     */
    private void reserveLocker(String lockerId, Package pkg) {
        Locker locker = lockers.get(lockerId);
        locker.setStatus(LockerStatus.OCCUPIED);
        locker.setPackageId(pkg.getId());
        
        reservations.put(lockerId, pkg.getId());
        packageLocations.put(pkg.getId(), lockerId);
    }

    /**
     * INTUITION: Deliver package to locker.
     * 
     * Called by delivery agent.
     */
    public synchronized boolean deliverPackage(String packageId, LockerSize size) {
        Package pkg = packages.get(packageId);
        if (pkg == null) return false;
        
        Locker locker = findLocker(pkg);
        if (locker == null) return false;
        
        // Generate OTP
        String otp = generateOTP();
        locker.setOtp(otp);
        
        // Schedule expiration (48 hours)
        scheduleExpiration(packageId, locker.getId(), 48);
        
        // Notify customer
        sendPickupNotification(pkg.getCustomerId(), locker, otp);
        
        return true;
    }

    /**
     * INTUITION: Customer picks up package with OTP.
     */
    public synchronized Package pickupPackage(String lockerId, String otp) {
        Locker locker = lockers.get(lockerId);
        if (locker == null || !locker.validateOtp(otp)) {
            throw new InvalidOTPException();
        }
        
        String packageId = reservations.remove(lockerId);
        Package pkg = packages.get(packageId);
        
        // Free locker
        locker.setStatus(LockerStatus.AVAILABLE);
        locker.setPackageId(null);
        locker.setOtp(null);
        
        packageLocations.remove(packageId);
        
        // Mark package as picked up
        pkg.pickup();
        
        return pkg;
    }

    /**
     * INTUITION: Handle expired package.
     */
    public synchronized void handleExpiredPackage(String packageId, String lockerId) {
        Locker locker = lockers.get(lockerId);
        Package pkg = packages.get(packageId);
        
        if (locker != null && pkg != null) {
            // Options: Return to sender or donate
            if (pkg.canReturn()) {
                System.out.println("Returning package " + packageId + " to sender");
            } else {
                System.out.println("Donating package " + packageId);
            }
            
            // Free locker
            locker.setStatus(LockerStatus.AVAILABLE);
            locker.setPackageId(null);
            
            reservations.remove(lockerId);
            packageLocations.remove(packageId);
        }
    }

    /**
     * Schedule expiration check.
     */
    private void scheduleExpiration(String packageId, String lockerId, int hours) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            handleExpiredPackage(packageId, lockerId);
        }, hours, TimeUnit.HOURS);
    }

    private String generateOTP() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    private void sendPickupNotification(String customerId, Locker locker, String otp) {
        System.out.println("📧 Notify " + customerId + ": Pick up from " + 
                          locker.getLocation() + ", OTP: " + otp);
    }

    private void initializeLockers() {
        // Create sample lockers
        for (int i = 1; i <= 10; i++) {
            lockers.put("L" + i, new Locker("L" + i, LockerSize.SMALL, "Location A"));
        }
        for (int i = 11; i <= 20; i++) {
            lockers.put("L" + i, new Locker("L" + i, LockerSize.MEDIUM, "Location A"));
        }
        for (int i = 21; i <= 25; i++) {
            lockers.put("L" + i, new Locker("L" + i, LockerSize.LARGE, "Location A"));
        }
        for (int i = 26; i <= 30; i++) {
            lockers.put("L" + i, new Locker("L" + i, LockerSize.XL, "Location A"));
        }
    }

    // --- Helpers ---

    public Locker getLocker(String lockerId) {
        return lockers.get(lockerId);
    }

    public Package getPackage(String packageId) {
        return packages.get(packageId);
    }

    public int getAvailableLockers(LockerSize size) {
        return (int) lockers.values().stream()
            .filter(l -> l.getStatus() == LockerStatus.AVAILABLE && l.getSize() == size)
            .count();
    }
}
```

```java
package com.locker;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Locker represents a physical locker.
 */
public class Locker {
    private final String lockerId;
    private final LockerSize size;
    private final String location;
    private final int width;
    private final int height;
    private final int depth;
    
    private LockerStatus status;
    private String packageId;
    private String otp;
    private LocalDateTime reservedAt;

    public Locker(String lockerId, LockerSize size, String location) {
        this.lockerId = lockerId;
        this.size = size;
        this.location = location;
        this.status = LockerStatus.AVAILABLE;
        
        // Dimensions based on size
        switch (size) {
            case SMALL:
                this.width = 10; this.height = 10; this.depth = 10;
                break;
            case MEDIUM:
                this.width = 20; this.height = 20; this.depth = 30;
                break;
            case LARGE:
                this.width = 40; this.height = 40; this.depth = 40;
                break;
            case XL:
                this.width = 60; this.height = 60; this.depth = 60;
                break;
            default:
                this.width = 20; this.height = 20; this.depth = 30;
        }
    }

    /**
     * INTUITION: Check if package fits.
     */
    boolean canFit(LockerSize packageSize) {
        // Order: SMALL < MEDIUM < LARGE < XL
        return this.size.ordinal() >= packageSize.ordinal();
    }

    boolean validateOtp(String otp) {
        return this.otp != null && this.otp.equals(otp);
    }

    // Getters
    public String getId() { return lockerId; }
    public LockerSize getSize() { return size; }
    public String getLocation() { return location; }
    public LockerStatus getStatus() { return status; }
    public String getPackageId() { return packageId; }
    public String getOtp() { return otp; }
    public LocalDateTime getReservedAt() { return reservedAt; }

    // Setters
    public void setStatus(LockerStatus status) { this.status = status; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public void setOtp(String otp) { this.otp = otp; }
    public void setReservedAt(LocalDateTime reservedAt) { this.reservedAt = reservedAt; }
}

enum LockerStatus {
    AVAILABLE, OCCUPIED, MAINTENANCE
}

enum LockerSize {
    SMALL(10, 10, 10),    // 10x10x10 cm
    MEDIUM(20, 20, 30),   // 20x20x30 cm
    LARGE(40, 40, 40),    // 40x40x40 cm
    XL(60, 60, 60);       // 60x60x60 cm
    
    private final int width;
    private final int height;
    private final int depth;
    
    LockerSize(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
    
    public int getVolume() {
        return width * height * depth;
    }
}
```

```java
package com.locker;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Package represents a shipment.
 */
public class Package {
    private final String packageId;
    private final String orderId;
    private final String customerId;
    private final String senderId;
    private final LockerSize size;
    private final LocalDateTime expectedDelivery;
    private PackageStatus status;
    private LocalDateTime pickedAt;
    private LocalDateTime deliveredAt;

    public Package(String packageId, String orderId, String customerId, 
                   String senderId, LockerSize size, LocalDateTime expectedDelivery) {
        this.packageId = packageId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.senderId = senderId;
        this.size = size;
        this.expectedDelivery = expectedDelivery;
        this.status = PackageStatus.IN_TRANSIT;
    }

    public void deliver(Locker locker) {
        this.deliveredAt = LocalDateTime.now();
        this.status = PackageStatus.IN_LOCKER;
    }

    public void pickup() {
        this.pickedAt = LocalDateTime.now();
        this.status = PackageStatus.PICKED_UP;
    }

    public boolean canReturn() {
        // Can return if not picked up within 48 hours
        if (pickedAt != null) return false;
        if (deliveredAt == null) return false;
        
        LocalDateTime deadline = deliveredAt.plusHours(48);
        return LocalDateTime.now().isAfter(deadline);
    }

    // Getters
    public String getId() { return packageId; }
    public String getCustomerId() { return customerId; }
    public LockerSize getSize() { return size; }
    public PackageStatus getStatus() { return status; }
}

enum PackageStatus {
    IN_TRANSIT,    // Being delivered
    IN_LOCKER,     // Waiting for pickup
    PICKED_UP,     // Customer collected
    EXPIRED,       // Not picked up, returned
    RETURNED       // Returned to sender
}
```

```java
package com.locker;

import java.util.*;

/**
 * INTUITION: LockerAllocationStrategy.
 * 
 * Best fit: choose smallest locker.
 * Could also implement first fit, worst fit.
 */
interface LockerAllocationStrategy {
    Locker select(List<Locker> available, LockerSize requiredSize);
}

/**
 * Best fit: smallest locker that fits.
 * Maximizes space efficiency.
 */
class BestFitStrategy implements LockerAllocationStrategy {
    @Override
    public Locker select(List<Locker> available, LockerSize requiredSize) {
        return available.stream()
            .filter(l -> l.canFit(requiredSize))
            .min(Comparator.comparingInt(l -> l.getSize().getVolume()))
            .orElse(null);
    }
}

/**
 * First fit: first available locker.
 */
class FirstFitStrategy implements LockerAllocationStrategy {
    @Override
    public Locker select(List<Locker> available, LockerSize requiredSize) {
        for (Locker locker : available) {
            if (locker.canFit(requiredSize)) {
                return locker;
            }
        }
        return null;
    }
}

class InvalidOTPException extends RuntimeException {
    public InvalidOTPException() {
        super("Invalid OTP");
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle overflow during holidays?"
> "Overflow area: temporary storage. Waitlist system. Redirect to nearby lockers. Extend pickup window."

### Q2: "How to prevent theft?"
> "CCTV + AI monitoring. PIN/OTP + biometrics. Package insurance. Alert on forced opening."

### Q3: "How to optimize locker placement?"
> "Analyze delivery density by area. hotspots: apartments, offices. BFS to find optimal locations."

### Q4: "How to handle returns?"
> "Customer places in locker → delivery agent picks up. QR code for agent. Status: RETURN_INITIATED → Picked Up → Refunded."