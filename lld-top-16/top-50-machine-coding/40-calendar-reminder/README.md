# 📆 Problem 40: Calendar & Reminder System

> **Difficulty**: ⭐⭐ | **Company Fit**: Google Calendar, Outlook, Apple Calendar  
> **Est. Time**: 60 min | **Patterns**: Observer, Strategy, Time-slot Management

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a calendar with reminders and notifications."

**What the interviewer tests**:
```
1. Can you manage events? (Create, edit, delete, recurring)
2. Can you handle reminders? (Multiple channels, snooze)
3. Can you detect conflicts? (Double-booking)
4. Can you share calendars? (Family, team calendars)
```

### Step 2: The "Aha!" Moment

The key insight: **Events + Reminders are separate but linked.**

```
EVENT: "Team Meeting"
  - Start: 2026-06-01 10:00
  - End: 2026-06-01 11:00
  - Location: Conference Room A
  - Attendees: [alice, bob, charlie]
  - Reminders: [10 min before, 1 hour before]

REMINDER:
  - Event: "Team Meeting"
  - Trigger: 2026-06-01 09:50
  - Channel: PUSH
  - Snoozable: Yes
  - Repeat: None

When event created:
  → Schedule reminders
  → Notify attendees
  → Check for conflicts
```

### Step 3: How to handle time zones?

```
STORAGE:
  Always store in UTC.
  Event: start=2026-06-01T14:00:00Z

DISPLAY:
  Convert to user's timezone.
  Alice (NY): 2026-06-01 10:00 AM EDT
  Bob (CA):  2026-06-01 07:00 AM PDT
  Charlie (UK): 2026-06-01 03:00 PM BST

RECURRING EVENTS:
  Store RRULE (iCalendar format).
  FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,WE,FR
```

---

## 💻 Core Implementation

```java
package com.calendar;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: CalendarService manages events and reminders.
 * 
 * Thread-safety: Multiple users creating events simultaneously.
 */
public class CalendarService {
    
    private final Map<String, Calendar> calendars;
    private final Map<String, Event> events;
    private final ReminderService reminderService;
    private final NotificationService notificationService;

    public CalendarService() {
        this.calendars = new ConcurrentHashMap<>();
        this.events = new ConcurrentHashMap<>();
        this.reminderService = new ReminderService();
        this.notificationService = new NotificationService();
    }

    /**
     * INTUITION: Create an event.
     * 
     * 1. Validate event (valid times, etc.)
     * 2. Check for conflicts
     * 3. Add to calendar
     * 4. Schedule reminders
     * 5. Send invitations
     */
    public synchronized Event createEvent(EventRequest request) {
        // Step 1: Validate
        if (request.getStart().isAfter(request.getEnd())) {
            throw new IllegalArgumentException("Start must be before end");
        }
        
        // Step 2: Check conflicts
        Calendar calendar = calendars.get(request.getCalendarId());
        if (hasConflict(calendar, request.getStart(), request.getEnd())) {
            throw new ConflictException("Time slot already booked");
        }
        
        // Step 3: Create event
        Event event = new Event(
            UUID.randomUUID().toString(),
            request.getTitle(),
            request.getDescription(),
            request.getStart(),
            request.getEnd(),
            request.getLocation(),
            request.getAttendees(),
            request.getRecurrence()
        );
        
        events.put(event.getId(), event);
        calendar.addEvent(event);
        
        // Step 4: Schedule reminders
        for (Reminder reminder : request.getReminders()) {
            reminderService.schedule(event, reminder);
        }
        
        // Step 5: Notify attendees
        notificationService.sendInvitations(event);
        
        return event;
    }

    /**
     * INTUITION: Check for conflicts.
     */
    private boolean hasConflict(Calendar calendar, ZonedDateTime start, ZonedDateTime end) {
        for (Event event : calendar.getEvents()) {
            if (event.overlaps(start, end)) {
                return true;
            }
        }
        return false;
    }

    /**
     * INTUITION: Update event.
     */
    public synchronized Event updateEvent(String eventId, EventUpdate update) {
        Event event = events.get(eventId);
        if (event == null) throw new EventNotFoundException(eventId);
        
        // Apply updates
        if (update.getTitle() != null) event.setTitle(update.getTitle());
        if (update.getStart() != null) event.setStart(update.getStart());
        if (update.getEnd() != null) event.setEnd(update.getEnd());
        if (update.getLocation() != null) event.setLocation(update.getLocation());
        
        // Reschedule reminders
        reminderService.reschedule(event);
        
        // Notify attendees of changes
        notificationService.sendUpdateNotifications(event);
        
        return event;
    }

    /**
     * INTUITION: Delete event.
     */
    public synchronized boolean deleteEvent(String eventId) {
        Event event = events.remove(eventId);
        if (event != null) {
            // Cancel reminders
            reminderService.cancel(event);
            
            // Notify attendees
            notificationService.sendCancellationNotifications(event);
            
            // Remove from calendar
            Calendar calendar = calendars.get(event.getCalendarId());
            if (calendar != null) {
                calendar.removeEvent(event);
            }
            
            return true;
        }
        return false;
    }

    /**
     * INTUITION: Share calendar with user.
     */
    public void shareCalendar(String calendarId, String userId, Permission permission) {
        Calendar calendar = calendars.get(calendarId);
        if (calendar != null) {
            calendar.addPermission(userId, permission);
        }
    }

    /**
     * INTUITION: Get events in a date range.
     */
    public List<Event> getEvents(String userId, ZonedDateTime start, ZonedDateTime end) {
        Calendar calendar = calendars.get(userId);
        if (calendar == null) return Collections.emptyList();
        
        List<Event> inRange = new ArrayList<>();
        for (Event event : calendar.getEvents()) {
            if (event.isInRange(start, end)) {
                inRange.add(event);
            }
        }
        return inRange;
    }

    public Event getEvent(String eventId) {
        return events.get(eventId);
    }
}
```

```java
package com.calendar;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: Event represents a calendar entry.
 * 
 * Can be single occurrence or recurring.
 */
public class Event {
    private final String id;
    private final String calendarId;
    private String title;
    private String description;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private String location;
    private final Set<String> attendees;
    private Recurrence recurrence;
    private final List<Reminder> reminders;
    private EventStatus status;
    private final ZonedDateTime createdAt;

    public Event(String id, String title, String description, 
                 ZonedDateTime start, ZonedDateTime end, String location,
                 Set<String> attendees, Recurrence recurrence) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.location = location;
        this.attendees = new CopyOnWriteArrayList<>(attendees);
        this.recurrence = recurrence;
        this.reminders = new CopyOnWriteArrayList<>();
        this.status = EventStatus.CONFIRMED;
        this.createdAt = ZonedDateTime.now();
    }

    public boolean overlaps(ZonedDateTime otherStart, ZonedDateTime end) {
        return this.start.isBefore(end) && this.end.isAfter(otherStart);
    }

    public boolean isInRange(ZonedDateTime rangeStart, ZonedDateTime rangeEnd) {
        return !this.end.isBefore(rangeStart) && !this.start.isAfter(rangeEnd);
    }

    // Getters
    public String getId() { return id; }
    public String getCalendarId() { return calendarId; }
    public String getTitle() { return title; }
    public ZonedDateTime getStart() { return start; }
    public ZonedDateTime getEnd() { return end; }
    public Set<String> getAttendees() { return Collections.unmodifiableSet(attendees); }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setStart(ZonedDateTime start) { this.start = start; }
    public void setEnd(ZonedDateTime end) { this.end = end; }
    public void setLocation(String location) { this.location = location; }
    public void setStatus(EventStatus status) { this.status = status; }
}

enum EventStatus {
    CONFIRMED, CANCELLED, TENTATIVE
}

/**
 * Recurrence rule (RRULE format).
 */
class Recurrence {
    private final Frequency frequency;
    private final int interval;
    private final Set<DayOfWeek> daysOfWeek;
    private final ZonedDateTime until;

    Recurrence(Frequency frequency, int interval) {
        this(frequency, interval, null, null);
    }

    Recurrence(Frequency frequency, int interval, Set<DayOfWeek> days, ZonedDateTime until) {
        this.frequency = frequency;
        this.interval = interval;
        this.daysOfWeek = days;
        this.until = until;
    }

    public boolean occursOn(ZonedDateTime date) {
        if (until != null && date.isAfter(until)) return false;
        
        if (frequency == Frequency.DAILY) {
            return true;
        } else if (frequency == Frequency.WEEKLY) {
            return daysOfWeek.contains(date.getDayOfWeek());
        } else if (frequency == Frequency.MONTHLY) {
            return date.getDayOfMonth() == daysOfWeek.iterator().next().getValue();
        }
        return false;
    }
}

enum Frequency {
    DAILY, WEEKLY, MONTHLY, YEARLY
}
```

```java
package com.calendar;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: Calendar belongs to a user or group.
 */
class Calendar {
    private final String calendarId;
    private final String userId;
    private final String name;
    private final String color;
    private final Map<String, Permission> permissions;
    private final Set<Event> events;

    Calendar(String calendarId, String userId, String name) {
        this.calendarId = calendar_id;
        this.userId = userId;
        this.name = name;
        this.color = "#3498db";
        this.permissions = new ConcurrentHashMap<>();
        this.events = new CopyOnWriteArrayList<>();
        
        // Owner has full access
        permissions.put(userId, Permission.OWNER);
    }

    void addEvent(Event event) {
        events.add(event);
    }

    void removeEvent(Event event) {
        events.remove(event);
    }

    Set<Event> getEvents() {
        return Collections.unmodifiableSet(new HashSet<>(events));
    }

    void addPermission(String userId, Permission permission) {
        permissions.put(userId, permission);
    }

    public String getCalendarId() { return calendarId; }
    public String getUserId() { return userId; }
}

enum Permission {
    OWNER,    // Full access
    EDITOR,   // Can edit events
    VIEWER    // Read-only
}
```

```java
package com.calendar;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: ReminderService schedules and manages reminders.
 * 
 * Uses ScheduledExecutorService to trigger at right time.
 */
class ReminderService {
    
    private final ScheduledExecutorService scheduler;
    private final Map<String, ScheduledFuture<?>> scheduledReminders;

    ReminderService() {
        this.scheduler = Executors.newScheduledThreadPool(10);
        this.scheduledReminders = new ConcurrentHashMap<>();
    }

    /**
     * INTUITION: Schedule reminder for event.
     */
    void schedule(Event event, Reminder reminder) {
        ZonedDateTime triggerTime = calculateTriggerTime(event, reminder);
        
        long delay = Duration.between(ZonedDateTime.now(), triggerTime).toMillis();
        
        if (delay > 0) {
            ScheduledFuture<?> future = scheduler.schedule(() -> {
                triggerReminder(event, reminder);
            }, delay, TimeUnit.MILLISECONDS);
            
            scheduledReminders.put(event.getId() + "_" + reminder.getChannel(), future);
        }
    }

    void reschedule(Event event) {
        // Cancel old reminders
        cancel(event);
        
        // Schedule new ones
        for (Reminder reminder : event.getReminders()) {
            schedule(event, reminder);
        }
    }

    void cancel(Event event) {
        for (Map.Entry<String, ScheduledFuture<?>> entry : scheduledReminders.entrySet()) {
            if (entry.getKey().startsWith(event.getId())) {
                entry.getValue().cancel(false);
            }
        }
    }

    private ZonedDateTime calculateTriggerTime(Event event, Reminder reminder) {
        return event.getStart().minus(reminder.getOffset());
    }

    private void triggerReminder(Event event, Reminder reminder) {
        System.out.println("🔔 Reminder: " + event.getTitle() + " in " + reminder.getOffset());
        notificationService.sendReminder(event, reminder);
    }

    private NotificationService notificationService;
}

class Reminder {
    private final String reminderId;
    private final ReminderChannel channel;
    private final Duration offset;  // How far before event
    private boolean snoozable;
    private SnoozeDuration snoozeDuration;

    Reminder(String reminderId, ReminderChannel channel, Duration offset) {
        this.reminderId = reminderId;
        this.channel = channel;
        this.offset = offset;
        this.snoozable = true;
        this.snoozeDuration = SnoozeDuration.TEN_MINUTES;
    }

    public Duration getOffset() { return offset; }
    public ReminderChannel getChannel() { return channel; }
}

enum ReminderChannel {
    PUSH, EMAIL, SMS
}

enum SnoozeDuration {
    FIVE_MINUTES(5), TEN_MINUTES(10), THIRTY_MINUTES(30), ONE_HOUR(60);
    
    private final int minutes;
    SnoozeDuration(int minutes) { this.minutes = minutes; }
}
```

```java
package com.calendar;

import java.time.ZonedDateTime;

/**
 * Request DTO for creating events.
 */
class EventRequest {
    private String calendarId;
    private String title;
    private String description;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private String location;
    private Set<String> attendees;
    private Recurrence recurrence;
    private List<Reminder> reminders;

    EventRequest(String title, ZonedDateTime start, ZonedDateTime end) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.attendees = new HashSet<>();
        this.reminders = new ArrayList<>();
    }

    EventRequest calendarId(String calendarId) {
        this.calendarId = calendarId;
        return this;
    }

    EventRequest description(String desc) {
        this.description = desc;
        return this;
    }

    EventRequest location(String loc) {
        this.location = loc;
        return this;
    }

    EventRequest addAttendee(String attendee) {
        this.attendees.add(attendee);
        return this;
    }

    EventRequest addReminder(Reminder reminder) {
        this.reminders.add(reminder);
        return this;
    }

    // Getters
    public String getCalendarId() { return calendarId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ZonedDateTime getStart() { return start; }
    public ZonedDateTime getEnd() { return end; }
    public String getLocation() { return location; }
    public Set<String> getAttendees() { return attendees; }
    public Recurrence getRecurrence() { return recurrence; }
    public List<Reminder> getReminders() { return reminders; }
}

class EventUpdate {
    private String title;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private String location;

    public EventUpdate title(String title) { this.title = title; return this; }
    public EventUpdate start(ZonedDateTime start) { this.start = start; return this; }
    public EventUpdate end(ZonedDateTime end) { this.end = end; return this; }
    public EventUpdate location(String location) { this.location = location; return this; }

    public String getTitle() { return title; }
    public ZonedDateTime getStart() { return start; }
    public ZonedDateTime getEnd() { return end; }
    public String getLocation() { return location; }
}

class ConflictException extends RuntimeException {
    public ConflictException(String msg) { super(msg); }
}

class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String id) { super("Event not found: " + id); }
}

/**
 * Simple notification service stub.
 */
class NotificationService {
    void sendInvitations(Event event) {
        System.out.println("📧 Sent " + event.getAttendees().size() + " invitations for: " + event.getTitle());
    }
    
    void sendReminder(Event event, Reminder reminder) {
        System.out.println("🔔 Reminder for " + event.getTitle());
    }
    
    void sendUpdateNotifications(Event event) {
        System.out.println("📧 Update sent for: " + event.getTitle());
    }
    
    void sendCancellationNotifications(Event event) {
        System.out.println("❌ Cancellation sent for: " + event.getTitle());
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle recurring events?"
> "Store RRULE. Expand on-the-fly for queries (next 30 days). Skip exceptions. Handle timezone DST transitions."

### Q2: "How to prevent meeting spam?"
> "Require acceptance. Allow declination with reason. Limit attendees per meeting. Require agenda."

### Q3: "How to find optimal meeting time for multiple attendees?"
> "Fetch all attendees' calendars. Find intersection of free windows. Rank by attendee count. Suggest top 3."

### Q4: "How to sync with external calendars?"
> "Use iCalendar/CalDAV protocol. Import .ics files. Two-way sync with Google/Outlook via API."