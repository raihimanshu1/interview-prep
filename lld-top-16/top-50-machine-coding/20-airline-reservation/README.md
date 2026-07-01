# ✈️ Problem 20: Airline Reservation System

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Amadeus, Sabre, Expedia, MakeMyTrip  
> **Est. Time**: 120 min | **Patterns**: Strategy, Factory, Observer, Seat Locking

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Book seats on a flight."

**What the interviewer tests**:
```
1. Can you handle complex Seat Lock management? (Hold while paying)
2. Can you model layered pricing? (Base fare + seat selection + baggage + meal)
3. Can you handle concurrent booking for limited seats? (100 seats, 500 users)
4. Can you model flight inventory? (Multiple fare classes, stopovers)
```

### Step 2: The "Aha!" Moment

The hardest part is NOT finding available seats. It's **seat locking with expiration**.

```
User A selects seat 12A → Seat is LOCKED for 5 minutes
User B tries seat 12A → "Currently held by another user"
User A pays → Seat is CONFIRMED
User A doesn't pay → Lock EXPIRES after 5 min → Seat becomes available
```

This is EXACTLY like Movie Ticket Booking but with:
- **Cancellation fees** (basic economy = no refund)
- **Waitlist** (if full, queue customers)
- **Multi-city** (A→B→C has different segments)

### Step 3: How to model seats?

Treat seats as a **FIRST-COME-FIRST-SERVED with active holds** resource.

```
Available seats = Total seats - Confirmed bookings - Active locks
Active lock = seat lock created < 5 min ago AND not yet confirmed/cancelled
```

---

## 💻 Core Implementation

### Flight.java — The Inventory

```java
package com.airline;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: A flight is a container with fixed seat capacity.
 * Like a theater show - seats are the scarce resource.
 * 
 * Key insight: Seats are allocated per SEGMENT.
 * A flight A→B→C has two segments:
 *   - Segment 1: A→B
 *   - Segment 2: B→C
 * A passenger traveling A→C needs BOTH segments available.
 */
public class Flight {
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDateTime departure;
    private final LocalDateTime arrival;
    private final Aircraft aircraft;
    private final Map<String, FareClass> fareClasses; // "ECONOMY", "BUSINESS"
    private final List<Segment> segments;
}

/**
 * A fare class is a category of tickets with different prices and rules.
 * Economy, Business, First Class - each has different price and cancellation policy.
 */
class FareClass {
    private final String code;          // "Y" = economy, "J" = business
    private final String name;
    private final double basePrice;
    private final int totalSeats;
    private final int bookedSeats;      // Confirmed bookings
    private final Map<String, SeatLock> activeLocks; // seatId → lock
    private final CancellationPolicy policy;
}

/**
 * Cancellation policy defines refund rules.
 */
class CancellationPolicy {
    private final int hoursBefore;  // e.g., 24 hours before flight
    private final double refundPercentage;  // e.g., 100% or 50% or 0%
    private final double cancellationFee;
}

/**
 * A seat lock holds a seat temporarily while user pays.
 */
class SeatLock {
    private final String seatId;
    private final String userId;
    private final LocalDateTime lockedAt;
    private final int lockDurationMinutes;  // e.g., 5 minutes
    private LockStatus status;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(lockedAt.plusMinutes(lockDurationMinutes));
    }

    public enum LockStatus { ACTIVE, CONFIRMED, EXPIRED, CANCELLED }
}
```

### SeatAllocator — Complex Seat Finding

```java
package com.airline;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * INTUITION: The seat allocator is the heart of the system.
 * 
 * ALGORITHM:
 * 1. User requests: Flight ABC123, from Seat=A, to Seat=C (multi-city)
 * 2. For EACH segment in the journey:
 *    a. Find fare class matching user's ticket
 *    b. Check availability on that segment
 *    c. Check if all required seats are free (not booked, not locked)
 * 3. If all segments available → CREATE LOCKS on all seats
 * 4. Return booking reference + payment timer
 * 
 * The tricky part: A multi-city flight uses SAME aircraft,
 * so seat 12A is used for ENTIRE journey. We need to check
 * ALL segments have that seat available.
 */
public class SeatAllocator {
    private final Map<String, Flight> flights;
    private final Map<String, List<SeatLock>> seatLocks; // flightNumber → active locks
    private final ReentrantLock lock = new ReentrantLock();
    private final ScheduledExecutorService expiryService = 
        Executors.newScheduledThreadPool(10);

    public SeatAllocator(Map<String, Flight> flights) {
        this.flights = flights;
        this.seatLocks = new ConcurrentHashMap<>();
    }

    /**
     * Try to lock seats for a booking.
     * Returns null if no seats available.
     */
    public synchronized Booking attemptSeatLock(
            String flightNumber, 
            List<String> seatIds,
            String userId,
            int durationMinutes) {
        
        Flight flight = flights.get(flightNumber);
        if (flight == null) throw new IllegalArgumentException("Flight not found");

        // Check ALL requested seats across ALL segments
        for (String seatId : seatIds) {
            if (!isSeatAvailable(flight, seatId)) {
                return null; // One seat unavailable
            }
        }

        // Create locks
        List<SeatLock> locks = new ArrayList<>();
        for (String seatId : seatIds) {
            SeatLock lock = new SeatLock(seatId, userId, durationMinutes);
            locks.add(lock);
            flight.addLock(seatId, lock);
            seatLocks.computeIfAbsent(flightNumber, k -> new ArrayList<>()).add(lock);
        }

        // Schedule auto-expiry
        expiryService.schedule(() -> expireLocks(flightNumber, locks, userId), 
            durationMinutes, TimeUnit.MINUTES);

        return new Booking(flightNumber, seatIds, userId, locks);
    }

    private boolean isSeatAvailable(Flight flight, String seatId) {
        // Check all fare classes for this seat
        for (FareClass fareClass : flight.getFareClasses().values()) {
            if (fareClass.hasSeat(seatId)) {
                // Check if booked
                if (fareClass.isBooked(seatId)) return false;
                // Check if locked by someone else
                SeatLock lock = fareClass.getLock(seatId);
                if (lock != null && !lock.getUserId().equals(userId)) return false;
                return true;
            }
        }
        return false;
    }

    private void expireLocks(String flightNumber, List<SeatLock> locks, String userId) {
        lock.lock();
        try {
            Flight flight = flights.get(flightNumber);
            for (SeatLock seatLock : locks) {
                if (seatLock.getUserId().equals(userId) && seatLock.isExpired()) {
                    flight.removeLock(seatLock.getSeatId());
                    System.out.println("Lock expired: " + seatLock.getSeatId());
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
```

### Booking.java — The Reservation Record

```java
package com.airline;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: A booking ties together:
 * - Passenger
 * - Flight segments
 * - Seats
 * - Payments
 * - Ancillaries (baggage, meals, seat selection fees)
 */
public class Booking {
    private final String bookingId;
    private final String userId;
    private final String flightNumber;
    private final List<String> seatIds;
    private final List<FlightSegment> segments;
    private BookingStatus status;
    private Payment payment;
    private final Map<String, String> specialRequests; // seat, meal, etc.
    private final LocalDateTime bookedAt;

    public enum BookingStatus {
        PENDING_PAYMENT,    // Seat locked, waiting for payment
        CONFIRMED,          // Paid and confirmed
        CANCELLED,          // Cancelled by user
        NO_SHOW,            // Didn't board
        COMPLETED           // Flight completed
    }

    public Booking(String flightNumber, List<String> seatIds, 
                   String userId, List<SeatLock> locks) {
        this.bookingId = generateBookingId();
        this.userId = userId;
        this.flightNumber = flightNumber;
        this.seatIds = seatIds;
        this.status = BookingStatus.PENDING_PAYMENT;
        this.bookedAt = LocalDateTime.now();
        this.specialRequests = new HashMap<>();
    }

    public void confirm(Payment payment) {
        this.payment = payment;
        this.status = BookingStatus.CONFIRMED;
    }

    public boolean canCancel() {
        if (status != BookingStatus.CONFIRMED) return false;
        // Can cancel if >24 hours before flight
        return LocalDateTime.now().plusHours(24).isBefore(segments.get(0).getDeparture());
    }

    public void cancel() {
        if (!canCancel()) throw new IllegalStateException("Cannot cancel");
        this.status = BookingStatus.CANCELLED;
        // Refund logic based on fare class policy
    }

    public void addSpecialRequest(String seatId, String request) {
        specialRequests.put(seatId, request);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle multi-city bookings?"
> "Model as Booking with multiple FlightSegments. Each segment is a separate booking. Seat lock must hold Across ALL segments. Passengers with connections have priority on reuse of seats."

### Q2: "How to implement overbooking (sell more tickets than seats)?"
> "Overbook by 5-10 seats (industry standard). When overbooked, create a waitlist. If no-shows exceed X%, waitlist moves to confirmed. Compensate bumped passengers with vouchers."

### Q3: "How to handle different fare classes?"
> "Each fare class is independent inventory. Economy full? User sees only Business class available. Different cancellation rules per class stored in CancellationPolicy."

### Q4: "How to support group bookings (10+ people together)?"
> "Reserve contiguous seats in advance. Block seat range. If partial availability, offer closest alternative. Group check-in priority."

### Q5: "What if seat map changes after booking?"
> "Version seat maps. Booking references version. If seat no longer exists on new map, reassign to equivalent seat. Notify passenger."