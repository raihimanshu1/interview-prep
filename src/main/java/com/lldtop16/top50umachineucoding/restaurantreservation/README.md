# 🍽️ Problem 17: Restaurant Table Reservation System

> **Difficulty**: ⭐⭐ | **Company Fit**: Zomato, Swiggy, Uber Eats, Yelp  
> **Est. Time**: 90 min | **Patterns**: Strategy, Observer, Singleton

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

At first glance: "Allow customers to book tables at restaurants."

**But the interviewer is really testing**:
```
1. Can you handle TIME-BASED resource allocation? (Tables on specific dates)
2. Can you model physical layout? (Tables of different sizes)
3. Can you handle waitlist and cancellations? (Real-world complexity)
```

### Step 2: The "Aha!" Moment

The hardest part is NOT booking a table. It's **managing overlapping time slots**.

> If someone reserves Table 5 from 7-9 PM, can another person book it from 8-10 PM?
> No! Table is occupied from 7-10 PM.
> 
> But what about 6-7:30 PM? That works because it ends before 7 PM.

Time slot management is the core challenge. You need to check if ANY time overlaps with an existing booking.

### Step 3: How to check availability?

```
Simple check: Is table free from startTime to endTime?

For each existing booking:
  If newStart < existingEnd AND newEnd > existingStart:
    → OVERLAP! Can't book.
  Else:
    → Available
```

---

## 📋 Requirements (Questions to Ask)

| Question | Why It Matters |
|----------|---------------|
| "Single restaurant or multi?" | Single = simple, Multi = need restaurant registry |
| "Reservation durations?" | Fixed (2 hours) or variable? |
| "Table types?" | 2-seater, 4-seater, 6-seater, private room |
| "Walk-ins allowed?" | Without reservation? |
| "Waitlist needed?" | If no table available, queue customers |
| "Cancellation policy?" | Refund? Penalty? |
| "Customer history?" | Track frequent diners, preferences |
| "Special occasions noted?" | Birthday, anniversary → special treatment |
| "Staff assignment?" | Waiter assigned to specific tables? |

---

## 💻 Core Implementation

### Restaurant.java — Main System

```java
package com.reservation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * INTUITION: The Restaurant manages everything.
 * 
 * Key insight: Time-slot based resource allocation.
 * A table is a resource that's available during specific time windows.
 * We need to check for OVERLAPPING time slots.
 */
public class Restaurant {
    
    // All tables in this restaurant
    private final Map<String, Table> tables = new ConcurrentHashMap<>();
    
    // All reservations (bookingId → Reservation)
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    
    // Waitlist queue per date
    private final Map<LocalDate, Queue<WaitlistEntry>> waitlist = new ConcurrentHashMap<>();

    /**
     * INTUITION: The core booking algorithm.
     * 
     * For a given party size and time:
     * 1. Find compatible tables (capacity >= party size)
     * 2. Check if table is FREE during requested time slot
     * 3. If found, create reservation
     * 4. If not found, add to waitlist
     * 
     * @param partySize Number of guests
     * @param startTime Desired start time
     * @param endTime Desired end time
     * @param customer Customer details
     * @return Reservation if successful
     * @throws NoTableAvailableException if no table available
     */
    public synchronized Reservation makeReservation(
            int partySize,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Customer customer) {
        
        // Step 1: Find tables that can fit this party
        List<Table> compatibleTables = tables.values().stream()
            .filter(table -> table.getCapacity() >= partySize)
            .collect(Collectors.toList());
        
        // Step 2: Check which tables are free during this time
        for (Table table : compatibleTables) {
            if (table.isAvailable(startTime, endTime)) {
                // Create the reservation
                Reservation reservation = new Reservation(
                    customer, table, startTime, endTime, partySize
                );
                
                // Block the table for this time
                table.addBooking(startTime, endTime);
                
                // Store reservation
                reservations.put(reservation.getId(), reservation);
                
                return reservation;
            }
        }
        
        // Step 3: No table available
        // Add customer to waitlist
        addToWaitlist(customer, partySize, startTime, endTime);
        
        throw new NoTableAvailableException(
            "No tables available for " + partySize + " guests at " + startTime
        );
    }

    /**
     * Cancel a reservation and free up the table.
     * If there's a waitlist, notify the next customer.
     */
    public synchronized void cancelReservation(String reservationId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }
        
        // Free the table time slot
        Table table = reservation.getTable();
        table.removeBooking(reservation.getStartTime(), reservation.getEndTime());
        
        // Mark as cancelled
        reservation.cancel();
        
        // Check waitlist for this time slot
        LocalDate date = reservation.getStartTime().toLocalDate();
        Queue<WaitlistEntry> dateWaitlist = waitlist.get(date);
        
        if (dateWaitlist != null) {
            WaitlistEntry next = dateWaitlist.poll();
            if (next != null) {
                // Notify the customer (in production: send email/SMS)
                System.out.println("Notify " + next.customer.getName() + 
                    ": Table available for " + next.partySize + " at " + date);
            }
        }
    }

    private void addToWaitlist(Customer customer, int partySize, 
                                LocalDateTime startTime, LocalDateTime endTime) {
        LocalDate date = startTime.toLocalDate();
        waitlist.computeIfAbsent(date, k -> new ConcurrentLinkedQueue<>())
                .add(new WaitlistEntry(customer, partySize, startTime, endTime));
    }
}
```

### Table.java — The Resource

```java
package com.reservation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * INTUITION: A table is a reusable resource with time slots.
 * 
 * KEY INSIGHT: Use a TreeSet (sorted) to store booked time ranges.
 * This makes overlap checking efficient.
 * 
 * Two time slots overlap if:
 *   newStart < existingEnd AND newEnd > existingStart
 * 
 * Example:
 *   Existing: 7:00 PM - 9:00 PM
 *   New:      6:00 PM - 7:30 PM → 6:00 < 9:00 AND 7:30 > 7:00 → OVERLAP
 *   New:      5:00 PM - 6:30 PM → 5:00 < 9:00 AND 6:30 > 7:00 → FALSE (6:30 < 7:00)
 *   New:      8:00 PM - 10:00 PM → 8:00 < 9:00 AND 10:00 > 7:00 → OVERLAP
 */
public class Table {
    
    private final String id;
    private final String tableNumber;
    private final int capacity;  // How many people can sit here
    private final String location;  // Window, Patio, Indoor, Bar
    
    // Sorted set of booked time ranges
    // TreeSet keeps them sorted by start time for quick lookup
    private final TreeSet<TimeSlot> bookings = new TreeSet<>(
        Comparator.comparing(TimeSlot::getStartTime)
    );

    public Table(String tableNumber, int capacity, String location) {
        this.id = UUID.randomUUID().toString();
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.location = location;
    }

    /**
     * Check if this table is free during the requested time.
     * 
     * ALGORITHM:
     * 1. Find the booking that starts at or just before our start time
     * 2. Check if that booking overlaps with our request
     * 3. Check the NEXT booking as well (our request could overlap with it)
     * 
     * @param startTime Requested start
     * @param endTime Requested end
     * @return true if table is free during this slot
     */
    public boolean isAvailable(LocalDateTime startTime, LocalDateTime endTime) {
        
        // Find the booking just before or at our start time
        TimeSlot before = bookings.floor(new TimeSlot(startTime, endTime));
        
        if (before != null && timesOverlap(before, startTime, endTime)) {
            return false;
        }
        
        // Find the booking just after our start time
        TimeSlot after = bookings.ceiling(new TimeSlot(startTime, endTime));
        
        if (after != null && timesOverlap(after, startTime, endTime)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * INTUITION: Two time slots overlap if:
     * slot1 starts BEFORE slot2 ends AND slot1 ends AFTER slot2 starts
     * 
     * Visual:
     * Slot 1:   |-------|
     * Slot 2:       |-------|  → Overlap (slot1 starts before slot2 ends)
     * 
     * Slot 1:   |---|
     * Slot 2:         |---|    → No overlap (slot1 ends when slot2 starts)
     */
    private boolean timesOverlap(TimeSlot existing, 
                                  LocalDateTime newStart, LocalDateTime newEnd) {
        return newStart.isBefore(existing.getEndTime()) 
            && newEnd.isAfter(existing.getStartTime());
    }

    public synchronized void addBooking(LocalDateTime start, LocalDateTime end) {
        bookings.add(new TimeSlot(start, end));
    }

    public synchronized void removeBooking(LocalDateTime start, LocalDateTime end) {
        bookings.remove(new TimeSlot(start, end));
    }

    // Getters
    public String getId() { return id; }
    public String getTableNumber() { return tableNumber; }
    public int getCapacity() { return capacity; }
    public String getLocation() { return location; }

    /**
     * Simple value object for a time range.
     * Two TimeSlots are "equal" if they have the same start/end times.
     */
    private static class TimeSlot {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
        
        TimeSlot(LocalDateTime start, LocalDateTime end) {
            this.startTime = start;
            this.endTime = end;
        }
        
        LocalDateTime getStartTime() { return startTime; }
        LocalDateTime getEndTime() { return endTime; }
    }
}
```

### Reservation.java

```java
package com.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    
    private final String id;
    private final Customer customer;
    private final Table table;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int partySize;
    private ReservationStatus status;
    private String specialRequests;
    private LocalDateTime createdAt;

    public enum ReservationStatus {
        CONFIRMED,
        CANCELLED,
        COMPLETED,  // Customer arrived and ate
        NO_SHOW      // Customer didn't show up
    }

    public Reservation(Customer customer, Table table, 
                      LocalDateTime startTime, LocalDateTime endTime,
                      int partySize) {
        this.id = UUID.randomUUID().toString();
        this.customer = customer;
        this.table = table;
        this.startTime = startTime;
        this.endTime = endTime;
        this.partySize = partySize;
        this.status = ReservationStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    public void cancel() { this.status = ReservationStatus.CANCELLED; }
    public void markNoShow() { this.status = ReservationStatus.NO_SHOW; }
    public void complete() { this.status = ReservationStatus.COMPLETED; }

    public boolean canCancel() {
        // Can cancel up to 1 hour before reservation
        return LocalDateTime.now().plusHours(1).isBefore(startTime);
    }

    // Getters
    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Table getTable() { return table; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getPartySize() { return partySize; }
    public ReservationStatus getStatus() { return status; }
}
```

---

## 🔥 Edge Cases

| Scenario | Problem | Solution |
|----------|---------|----------|
| **Overlapping reservations** | Double booking | Time slot overlap check |
| **No-show** | Table wasted | Mark as no-show, charge penalty |
| **Walk-in during busy time** | No table available | Offer waitlist |
| **Large party booking** | No single table fits | Split across multiple tables |
| **Reservation too short** | Unhappy customer | Minimum 1 hour booking |
| **Reservation too long** | Blocks other customers | Maximum 4 hours during peak |

---

## ❓ Follow-up Questions

### Q1: "How to handle special table configurations (push together tables)?"
> "I'd add a `TableGroup` concept. Multiple tables can be combined. The group acts as a single virtual table with combined capacity. When reserving, check if a single table or a group fits."

### Q2: "How to add restaurant preferences (window seats, quiet area)?"
> "Add tags/labels to tables (Window, Quiet, Patio). Customers can specify preferences in search. Filter available tables by preferences."

### Q3: "How to handle recurring reservations (weekly dinner)?"
> "Add a `RecurringReservation` that generates individual reservations on specified days. Handle conflicts gracefully."