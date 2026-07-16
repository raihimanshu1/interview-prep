# 🏢 Problem 38: Conference Room Booking System

> **Difficulty**: ⭐⭐ | **Company Fit**: Google, Microsoft, any enterprise  
> **Est. Time**: 60 min | **Patterns**: Time-slot Management, TreeMap, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Book conference rooms for meetings."

**What the interviewer tests**:
```
1. Can you detect scheduling conflicts? (Double-booking)
2. Can you find available rooms? (By capacity, amenities)
3. Can you handle recurring meetings? (Daily standup)
4. Can you optimize room allocation? (Don't book 10-person room for 2 people)
```

### Step 2: The "Aha!" Moment

The key insight: **Room is a resource, meeting is a reservation.**

```
ROOMS (Resources):
  Room A: Capacity 10, Has projector, Has whiteboard
  Room B: Capacity 20, Has projector, Has video conf
  Room C: Capacity 4, No amenities

BOOKING LOGIC:
  Request: 5 people, needs projector, 2-4 PM
  → Room A fits (capacity 10, has projector)
  → Room B fits but wastes space (capacity 20)
  → Room C doesn't fit (no projector)
  → Best match: Room A

CONFLICT CHECK:
  Room A: 1-3 PM booked, 5-6 PM booked
  Request: 2-4 PM
  → CONFLICT! Overlaps 2-3 PM
```

### Step 3: How to optimize allocation?

```
GREEDY ALGORITHM:
  1. Filter rooms by requirements (capacity, amenities)
  2. Sort by best fit (smallest room that fits)
  3. Check availability for each
  4. Return first available

RECURRING MEETINGS:
  Daily standup 9-9:15 AM for next 30 days
  → Create 30 individual bookings
  → Or store as recurring + expand on query
```

---

## 💻 Core Implementation

```java
package com.booking;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: RoomBookingService manages conference room reservations.
 * 
 * Core operations:
 * - Find available rooms (filter by capacity, amenities, time)
 * - Book room (create reservation)
 * - Cancel booking
 * - Check conflicts
 */
public class RoomBookingService {
    
    // All rooms: roomId → ConferenceRoom
    private final Map<String, ConferenceRoom> rooms;
    
    // All bookings: bookingId → Booking
    private final Map<String, Booking> bookings;
    
    // Room bookings: roomId → sorted list of bookings
    private final Map<String, TreeSet<Booking>> roomSchedule;

    public RoomBookingService() {
        this.rooms = new ConcurrentHashMap<>();
        this.bookings = new ConcurrentHashMap<>();
        this.roomSchedule = new ConcurrentHashMap<>();
    }

    /**
     * INTUITION: Book a conference room.
     * 
     * 1. Find eligible rooms (capacity, amenities)
     * 2. Check availability for each
     * 3. Return best available room
     * 
     * @param capacity Required seating capacity
     * @param amenities Required amenities (projector, whiteboard, etc.)
     * @param start Start time
     * @param end End time
     * @return Booking or null if no room available
     */
    public synchronized Booking bookRoom(int capacity, Set<String> amenities,
                                          TimeSlot timeSlot, String organizer) {
        
        // Step 1: Find eligible rooms
        List<ConferenceRoom> eligible = findEligibleRooms(capacity, amenities);
        
        // Step 2: Check availability
        for (ConferenceRoom room : eligible) {
            if (isAvailable(room.getId(), timeSlot)) {
                // Step 3: Create booking
                Booking booking = new Booking(room.getId(), timeSlot, organizer);
                bookings.put(booking.getId(), booking);
                addToSchedule(room.getId(), booking);
                
                // Notify participants
                sendNotifications(booking);
                
                return booking;
            }
        }
        
        return null;  // No room available
    }

    /**
     * INTUITION: Find available rooms matching criteria.
     * 
     * Filter by:
     * - Capacity ≥ required
     * - Has all required amenities
     * - Available during time slot
     */
    public List<ConferenceRoom> findAvailableRooms(int capacity, Set<String> amenities,
                                                     TimeSlot timeSlot) {
        List<ConferenceRoom> available = new ArrayList<>();
        
        for (ConferenceRoom room : rooms.values()) {
            // Check capacity
            if (room.getCapacity() < capacity) continue;
            
            // Check amenities
            if (!room.hasAmenities(amenities)) continue;
            
            // Check availability
            if (!isAvailable(room.getId(), timeSlot)) continue;
            
            available.add(room);
        }
        
        // Sort by best fit (smallest room that fits)
        available.sort(Comparator.comparingInt(ConferenceRoom::getCapacity));
        
        return available;
    }

    /**
     * INTUITION: Cancel a booking.
     */
    public synchronized boolean cancelBooking(String bookingId) {
        Booking booking = bookings.remove(bookingId);
        if (booking != null) {
            removeFromSchedule(booking.getRoomId(), booking);
            sendCancellationNotifications(booking);
            return true;
        }
        return false;
    }

    /**
     * INTUITION: Check if room is available for time slot.
     * 
     * Uses TreeSet for efficient conflict detection.
     * Check only neighboring bookings.
     */
    public boolean isAvailable(String roomId, TimeSlot timeSlot) {
        TreeSet<Booking> schedule = roomSchedule.get(roomId);
        if (schedule == null || schedule.isEmpty()) {
            return true;
        }
        
        // Find booking that starts just before our start time
        Booking before = schedule.floor(new Booking(timeSlot));
        // Find booking that starts at or after our start time
        Booking after = schedule.ceiling(new Booking(timeSlot));
        
        // Check conflict with previous booking
        if (before != null && before.overlaps(timeSlot)) {
            return false;
        }
        
        // Check conflict with next booking
        if (after != null && after.overlaps(timeSlot)) {
            return false;
        }
        
        return true;
    }

    /**
     * Add room to system.
     */
    public void addRoom(ConferenceRoom room) {
        rooms.put(room.getId(), room);
        roomSchedule.computeIfAbsent(room.getId(), k -> new TreeSet<>());
    }

    // --- Helpers ---

    private List<ConferenceRoom> findEligibleRooms(int capacity, Set<String> amenities) {
        List<ConferenceRoom> eligible = new ArrayList<>();
        for (ConferenceRoom room : rooms.values()) {
            if (room.getCapacity() >= capacity && room.hasAmenities(amenities)) {
                eligible.add(room);
            }
        }
        return eligible;
    }

    private void addToSchedule(String roomId, Booking booking) {
        roomSchedule.computeIfAbsent(roomId, k -> new TreeSet<>()).add(booking);
    }

    private void removeFromSchedule(String roomId, Booking booking) {
        TreeSet<Booking> schedule = roomSchedule.get(roomId);
        if (schedule != null) {
            schedule.remove(booking);
        }
    }

    private void sendNotifications(Booking booking) {
        // Send email/SMS to organizer
        ConferenceRoom room = rooms.get(booking.getRoomId());
        System.out.println("✓ Room " + room.getName() + " booked for " + 
                          booking.getOrganizer() + ": " + booking.getTimeSlot());
    }

    private void sendCancellationNotifications(Booking booking) {
        System.out.println("✗ Booking cancelled: " + booking.getId());
    }
}
```

```java
package com.booking;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: ConferenceRoom represents a bookable room.
 */
public class ConferenceRoom {
    private final String id;
    private final String name;
    private final int capacity;
    private final Set<String> amenities;
    private final String building;
    private final int floor;

    public ConferenceRoom(String id, String name, int capacity, String building, int floor) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.amenities = new HashSet<>();
        this.building = building;
        this.floor = floor;
    }

    public void addAmenity(String amenity) {
        amenities.add(amenity);
    }

    public boolean hasAmenities(Set<String> required) {
        return amenities.containsAll(required);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public Set<String> getAmenities() { return Collections.unmodifiableSet(amenities); }
}
```

```java
package com.booking;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Booking represents a time slot reservation.
 * 
 * Comparable for TreeSet ordering (by start time).
 */
public class Booking implements Comparable<Booking> {
    private final String bookingId;
    private final String roomId;
    private final TimeSlot timeSlot;
    private final String organizer;
    private final Set<String> attendees;
    private final LocalDateTime bookedAt;
    private BookingStatus status;

    public Booking(String roomId, TimeSlot timeSlot, String organizer) {
        this.bookingId = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.timeSlot = timeSlot;
        this.organizer = organizer;
        this.attendees = new HashSet<>();
        this.bookedAt = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }

    public boolean overlaps(TimeSlot other) {
        return this.timeSlot.overlaps(other);
    }

    // Getters
    public String getId() { return bookingId; }
    public String getRoomId() { return roomId; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public String getOrganizer() { return organizer; }
    public LocalDateTime getStart() { return timeSlot.getStart(); }
    public LocalDateTime getEnd() { return timeSlot.getEnd(); }

    @Override
    public int compareTo(Booking other) {
        return this.timeSlot.getStart().compareTo(other.timeSlot.getStart());
    }
}

enum BookingStatus {
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
```

```java
package com.booking;

import java.time.LocalDateTime;

/**
 * INTUITION: TimeSlot represents a time range.
 * 
 * Used for conflict detection.
 */
public class TimeSlot {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public boolean overlaps(TimeSlot other) {
        return this.start.isBefore(other.end) && this.end.isAfter(other.start);
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    @Override
    public String toString() {
        return start + " - " + end;
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle recurring meetings?"
> "Create RecurringMeeting template. Generate individual bookings on-demand. Skip holidays. Allow exceptions."

### Q2: "How to handle time zones?"
> "Store all times in UTC. Convert on display. Use IANA timezone IDs. Handle DST transitions."

### Q3: "How to prevent no-shows?"
> "Send reminder 1 hour before. Cancellation policy: cancel > 24h = no penalty, < 1h = 50% penalty."

### Q4: "How to optimize for peak hours (9-11 AM)?"
> "Priority booking for executives. Auto-suggest alternative rooms. Dynamic pricing for off-peak."