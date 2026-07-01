# 📅 Problem 25: Meeting Scheduler (Calendar)

> **Difficulty**: ⭐⭐ | **Company Fit**: Google Calendar, Outlook, Zoom  
> **Est. Time**: 60 min | **Patterns**: Time-slot Management, TreeMap, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Find available time slots for a meeting."

**What the interviewer tests**:
```
1. Can you handle meetings across time zones?
2. Can you detect conflicts efficiently?
3. Can you find the NEXT available slot?
4. Can you handle recurring meetings?
```

### Step 2: The "Aha!" Moment

The key insight: **Use TreeMap for O(log n) conflict detection.**

```
Meetings stored as intervals: [startTime, endTime, participants]

To check conflict:
  For each participant, check if new meeting overlaps with any of their existing meetings.
  
  Overlap condition:
    newStart < existingEnd AND newEnd > existingStart

Using TreeMap<LocalDateTime, Meeting>:
  - Keys are sorted by start time
  - ceilingEntry(startTime) gives next meeting after start
  - floorEntry(endTime) gives last meeting before end
  - Check only 2 neighbors (O(log n) instead of O(n))
```

### Step 3: How to optimize for multiple participants?

```
Naive: For each participant, scan all their meetings → O(P * M)
Optimized: Use a Room Scheduler that checks ALL participants at once
  - Build a set of busy intervals across ALL participants
  - Find intersection of all busy intervals
  - Slot is free if not in intersection
```

---

## 💻 Core Implementation

```java
package com.scheduler;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: CalendarService is the main scheduler.
 * 
 * Uses TreeMap for efficient interval queries.
 * TreeMap is sorted by key, so we can use ceiling/floor to find
 * nearby meetings in O(log n).
 */
public class CalendarService {
    
    // userId → their meetings (sorted by start time)
    private final Map<String, TreeMap<LocalDateTime, Meeting>> userCalendars;
    
    // meetingId → Meeting
    private final Map<String, Meeting> allMeetings;
    
    // Room management
    private final Map<String, MeetingRoom> rooms;

    public CalendarService() {
        this.userCalendars = new ConcurrentHashMap<>();
        this.allMeetings = new ConcurrentHashMap<>();
        this.rooms = new ConcurrentHashMap<>();
    }

    /**
     * INTUITION: Schedule a meeting if all participants are free.
     * 
     * Algorithm:
     * 1. For each participant, check if [start, end) overlaps with any of their meetings
     * 2. If ANY conflict, reject
     * 3. If ALL clear, create meeting and add to all participants' calendars
     * 
     * @param title Meeting title
     * @param start Start time
     * @param end End time
     * @param participants List of attendee user IDs
     * @param room Optional room ID
     * @return Scheduled meeting
     * @throws ConflictException if any participant is busy
     */
    public synchronized Meeting scheduleMeeting(String title, LocalDateTime start, 
                                                LocalDateTime end, List<String> participants,
                                                String roomId) throws ConflictException {
        
        // Step 1: Check all participants
        for (String userId : participants) {
            if (hasConflict(userId, start, end)) {
                throw new ConflictException(userId + " has a meeting conflict");
            }
        }
        
        // Step 2: Check room availability (if specified)
        MeetingRoom room = null;
        if (roomId != null) {
            room = rooms.get(roomId);
            if (room == null || !room.isAvailable(start, end)) {
                throw new ConflictException("Room not available");
            }
        }
        
        // Step 3: Create meeting
        Meeting meeting = new Meeting(title, start, end, participants, room);
        
        // Step 4: Add to all participants' calendars
        for (String userId : participants) {
            userCalendars.computeIfAbsent(userId, k -> new TreeMap<>())
                         .put(start, meeting);
        }
        
        // Step 5: Reserve room
        if (room != null) {
            room.addBooking(start, end);
        }
        
        // Step 6: Store globally
        allMeetings.put(meeting.getId(), meeting);
        
        // Notify participants (observer pattern)
        notifyParticipants(meeting);
        
        return meeting;
    }

    /**
     * INTUITION: Check if a user has any meeting overlapping with [start, end).
     * 
     * Using TreeMap, we only need to check neighbors, not all meetings.
     * 
     * TreeMap.floorEntry(start) → meeting just before start
     * TreeMap.ceilingEntry(start) → meeting at or after start
     * 
     * Overlap check:
     *   [existing-------]
     *            [new----]  → Overlap if newStart < existingEnd
     * 
     *   [existing---]
     *             [new----]  → No overlap if newStart >= existingEnd
     */
    private boolean hasConflict(String userId, LocalDateTime start, LocalDateTime end) {
        TreeMap<LocalDateTime, Meeting> calendar = userCalendars.get(userId);
        if (calendar == null || calendar.isEmpty()) {
            return false;
        }
        
        // Get the meeting just before or at our start time
        Map.Entry<LocalDateTime, Meeting> before = calendar.floorEntry(start);
        if (before != null) {
            Meeting prev = before.getValue();
            // Does the previous meeting overlap with our new one?
            if (prev.getEnd().isAfter(start)) {
                return true;  // prev ends after our start → overlap
            }
        }
        
        // Get the meeting at or after our start time
        Map.Entry<LocalDateTime, Meeting> after = calendar.ceilingEntry(start);
        if (after != null) {
            Meeting next = after.getValue();
            // Does this meeting overlap?
            // next.start < our.end (starts before we end)
            if (next.getKey().isBefore(end)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * INTUITION: Find the next available slot for ALL participants.
     * 
     * Greedy approach:
     * 1. Start from requested time
     * 2. Move forward in 15-min increments until ALL participants are free
     * 3. Also check room availability
     * 
     * In production: use interval merging to compute free windows
     */
    public Optional<TimeSlot> findNextAvailableSlot(List<String> participants, 
                                                     int durationMinutes,
                                                     LocalDateTime from) {
        LocalDateTime current = from;
        LocalDateTime end = from.plusDays(7);  // Search up to 1 week
        
        while (current.isBefore(end)) {
            LocalDateTime proposedEnd = current.plusMinutes(durationMinutes);
            
            boolean allFree = true;
            for (String userId : participants) {
                if (hasConflict(userId, current, proposedEnd)) {
                    allFree = false;
                    break;
                }
            }
            
            if (allFree) {
                return Optional.of(new TimeSlot(current, proposedEnd));
            }
            
            // Try next 15-min slot
            current = current.plusMinutes(15);
        }
        
        return Optional.empty();  // No slot found in next week
    }

    // --- Helpers ---

    private void notifyParticipants(Meeting meeting) {
        // In production: Send email/SMS/push notification
        for (String userId : meeting.getParticipants()) {
            System.out.println("Notify " + userId + ": New meeting " + meeting.getTitle());
        }
    }

    public Meeting getMeeting(String meetingId) {
        return allMeetings.get(meetingId);
    }

    public void cancelMeeting(String meetingId) {
        Meeting meeting = allMeetings.remove(meetingId);
        if (meeting != null) {
            for (String userId : meeting.getParticipants()) {
                TreeMap<LocalDateTime, Meeting> cal = userCalendars.get(userId);
                if (cal != null) {
                    cal.remove(meeting.getStart());
                }
            }
            if (meeting.getRoom() != null) {
                meeting.getRoom().removeBooking(meeting.getStart(), meeting.getEnd());
            }
        }
    }
}
```

```java
package com.scheduler;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Meeting is immutable once created.
 * 
 * Contains all metadata about a scheduled meeting.
 */
public class Meeting {
    private final String id;
    private final String title;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final List<String> participants;
    private final MeetingRoom room;
    private final MeetingStatus status;
    private final LocalDateTime createdAt;

    public enum MeetingStatus {
        SCHEDULED,
        CANCELLED,
        COMPLETED
    }

    public Meeting(String title, LocalDateTime start, LocalDateTime end,
                   List<String> participants, MeetingRoom room) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.start = start;
        this.end = end;
        this.participants = new ArrayList<>(participants);
        this.room = room;
        this.status = MeetingStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = MeetingStatus.CANCELLED;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public List<String> getParticipants() { return Collections.unmodifiableList(participants); }
    public MeetingRoom getRoom() { return room; }
    public MeetingStatus getStatus() { return status; }
}
```

```java
package com.scheduler;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * INTUITION: MeetingRoom is a resource with time slots.
 * 
 * Uses TreeSet (sorted) for quick overlap checking.
 */
public class MeetingRoom {
    private final String id;
    private final String name;
    private final int capacity;
    private final Set<String> amenities;  // Projector, Whiteboard, etc.
    
    private final TreeSet<TimeSlot> bookings;

    public MeetingRoom(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.amenities = new HashSet<>();
        this.bookings = new TreeSet<>(Comparator.comparing(TimeSlot::getStart));
    }

    public boolean isAvailable(LocalDateTime start, LocalDateTime end) {
        TimeSlot newSlot = new TimeSlot(start, end);
        
        // Check for overlap with any existing booking
        for (TimeSlot existing : bookings) {
            if (newSlot.overlaps(existing)) {
                return false;
            }
        }
        return true;
    }

    public synchronized void addBooking(LocalDateTime start, LocalDateTime end) {
        bookings.add(new TimeSlot(start, end));
    }

    public synchronized void removeBooking(LocalDateTime start, LocalDateTime end) {
        bookings.remove(new TimeSlot(start, end));
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }

    /**
     * Represents a time slot.
     */
    static class TimeSlot {
        private final LocalDateTime start;
        private final LocalDateTime end;

        TimeSlot(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        boolean overlaps(TimeSlot other) {
            return this.start.isBefore(other.end) && this.end.isAfter(other.start);
        }

        LocalDateTime getStart() { return start; }
        LocalDateTime getEnd() { return end; }
    }
}

class ConflictException extends Exception {
    public ConflictException(String message) {
        super(message);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle recurring meetings (weekly standup)?"
> "Create a RecurringMeeting that generates individual Meeting instances for each occurrence. Store recurrence rule (weekly, biweekly, monthly). Handle conflicts per occurrence."

### Q2: "How to handle time zones?"
> "Store all times in UTC. Convert to user's time zone on display. Use IANA timezone IDs (America/New_York). Account for DST transitions."

### Q3: "How to find optimal meeting time for 10 people across 3 time zones?"
> "Convert all to common timezone. Compute intersection of free windows. Rank by how many participants are free. Suggest top 3 options."

### Q4: "How to add video conferencing links?"
> "Integrate Zoom/Teams API. Generate unique meeting link when scheduling. Send link in calendar invite."

### Q5: "How to handle meeting room equipment requirements?"
> "Add amenities filter to room search. User specifies 'needs projector'. Filter rooms by amenities."