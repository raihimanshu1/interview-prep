# 🅿️ Parking Lot System — Interview Masterclass

---

## 🧠 The Real Interview Mindset

### What the Interviewer Actually Thinks

> **Before you start**: "Can this person think through a fuzzy problem? Do they ask the right questions? Or do they jump into code and make a mess?"

> **During your design**: "Are they considering real-world constraints? Do they know when to use a pattern vs when it adds unnecessary complexity?"

> **At the end**: "Would I want to work with this person? Can they explain complex ideas simply?"

### Your Job in This Interview

```
NOT to write perfect code          → TO demonstrate clear thinking
NOT to use all design patterns      → TO use the RIGHT one for the problem
NOT to build everything             → TO scope well and prioritize
NOT to know all answers             → TO handle follow-ups gracefully
```

---

## 🎬 The Complete Interview Script (What You Actually Say)

Below is the **word-for-word thought process** of a senior engineer solving this problem. Read this first, then study the detailed sections.

---

### 🎭 Act 1: "Wait, what exactly are we building?" (0-5 min)

> **You**: "Before I jump into design, I'd like to understand the scope better."
> 
> "Is this a single-floor lot or multi-floor? That changes how we track spots."
> 
> "What types of vehicles? Just cars, or bikes and trucks too? Each needs different spot sizes."
> 
> "Is pricing simple — like flat hourly rate — or do we need surge pricing, membership discounts?"
> 
> "Do you need me to handle payment integration, or just the parking logic?"
> 
> "Do multiple vehicles enter at the same time? Because that means I need thread safety."
> 
> "Any future features I should keep in mind? Like EV charging, reservation system?"

> **Interviewer**: "Multi-floor. Cars, bikes, trucks. Hourly pricing. No payment integration. Yes, concurrent entry. No future features needed."

---

### 🎭 Act 2: "Now I see the shape of it" (5-8 min)

> **You**: "Okay, so here's what I think we need..."
>
> "Every parking lot problem has the same core elements:
> 1. **Something that parks** — vehicles with attributes
> 2. **Something to park in** — spots organized in floors
> 3. **An entry/exit process** — ticket on entry, payment on exit
> 4. **A way to track it all** — who's in which spot, for how long
>
> The tricky part is concurrency. Two gates could try to assign the same spot. I need to prevent that."
>
> "I'm thinking we need:
> - A **Spot** entity (smallest unit, has type + occupied flag)
> - A **Floor** entity (collection of spots)
> - A **ParkingLot** that manages floors
> - A **Ticket** that tracks a parking session
> - A **PricingStrategy** because pricing might change
> - Services to orchestrate all this"

---

### 🎭 Act 3: "Here's where patterns come in" (8-15 min)

> **You**: "Let me think about the design patterns..."
>
> "Pricing is the most likely thing to change. If I hardcode rates in Ticket class, changing pricing means modifying Ticket. That breaks the **Open/Closed Principle**. So I'll use **Strategy Pattern** for pricing."
>
> "Spot allocation strategy might also differ — nearest to entrance, or spread across floors for load balancing. Another **Strategy Pattern** candidate."
>
> "The parking lot itself is a single physical entity — one instance per lot. **Singleton Pattern** is appropriate here but I'll be careful with thread safety."
>
> "For the class hierarchy, vehicles share behavior (license plate, type) but differ in size. I'll use an **abstract base class** with the **Template Method pattern** — subclasses just specify their type."
>
> "For thread safety on spot assignment... **synchronized** is simplest for single JVM, but I could use **ReentrantLock** for fairness."

---

### 🎭 Act 4: "Let me build the core flow" (15-35 min)

> **You**: "I'll start with the entities, then the services, then wire it together."
>
> "VehicleType as an enum because it's fixed and type-safe..."
>
> "Vehicle as abstract class because all vehicles share license plate..."
>
> "ParkingSpot with synchronized assign because this is THE critical section..."
>
> "PricingStrategy as an interface so we can add new pricing without changing existing code..."
>
> "ParkingService as the orchestrator that binds everything together..."

[This is where the code goes — the actual implementation phase]

---

### 🎭 Act 5: "But what if something goes wrong?" (35-40 min)

> **You**: "Let me think about edge cases..."
>
> "What if someone parks for 30 days? Do we cap the fee?"
> "What if payment succeeds but the gate doesn't open? Transaction rollback needed."
> "What if a driver loses their ticket? We'd charge the maximum daily rate."
> "What if two gates try to assign the same spot simultaneously? synchronized handles this."
> "What if the system crashes mid-transaction? We need database transactions or a compensation mechanism."

---

### 🎭 Act 6: "And if we need to scale?" (40-45 min)

> **You**: "This design works for a single lot. If we need to scale to a chain of lots..."
>
> "Each lot gets its own ParkingLot instance. A central API Gateway routes requests."
> "Redis for real-time spot availability across lots."
> "Shard the database by lot_id."
> "Add a FindParkingService that searches across nearby lots."
> "For distributed concurrency, replace synchronized with database optimistic locking."

---

### 🎭 Act 7: "The interviewer throws a curveball" (45-60 min)

> **Interviewer**: "What if we need to support EV charging?"
>
> **You**: "Great question! I'd extend it like this... [explains extension]"
>
> **Interviewer**: "How about monthly subscriptions?"
>
> **You**: "I'd add a Subscription entity and modify the pricing strategy to check for active subscriptions..."
>
> **Interviewer**: "What if this needs to handle 10,000 cars per hour?"
>
> **You**: "We'd need to... [explains scaling]"

---

## 📚 The Detailed Deep Dive

Now let's go through each element with the WHY behind every decision.

---

### 🔑 Key Intuition #1: Why Strategy Pattern?

**The confusion most juniors have**: "I'll just put the pricing logic in a calculateFee() method in Ticket class."

**The problem**: Next week, the business wants:
- Weekend surcharge (1.5x on Sat/Sun)
- Holiday special (flat $5 all day)
- Monthly member discount (free parking)
- EV charging fee

With hardcoded pricing, each change requires modifying Ticket class → risk of breaking existing code → need to retest everything → slow releases.

**The intuition**: Pricing is a **policy**, not a core entity behavior. Policies change more frequently than entities. **Isolate policies in their own classes.**

```java
// ❌ BAD: Pricing logic embedded in entity
public class Ticket {
    public double calculateFee() {
        // All pricing logic here!
        // Breaking OCP — every pricing change modifies this class
    }
}

// ✅ GOOD: Pricing is a separate concern
public class Ticket {
    public double calculateFee(PricingStrategy strategy) {
        return strategy.calculatePrice(duration, vehicleType);
    }
}
```

---

### 🔑 Key Intuition #2: Why Abstract class not Interface for Vehicle?

**The confusion**: "Interfaces are more flexible, right?"

**The intuition**: Think about what vehicles SHARE vs what they DIFFER on.

```
SHARE: license plate, unique ID, vehicle type
DIFFER: size (small vs large), special behaviors (EV charging)
```

When entities share state AND behavior, use abstract class. When they only share behavior contracts, use interface.

```java
// ❌ BAD: Interface when state is shared
public interface Vehicle {
    String getLicensePlate();  // Every impl duplicates this!
}

// ✅ GOOD: Abstract class captures shared state
public abstract class Vehicle {
    protected final String licensePlate;
    // Constructor, validation, getters all in one place
    // Subclasses are tiny!
}

public class Car extends Vehicle {
    public Car(String plate) { super(plate, VehicleType.CAR); }
    // ONE line of code!
}
```

---

### 🔑 Key Intuition #3: Why synchronized on ParkingSpot.assign()?

**The race condition visualized**:

```
Time → 
Gate 1: Check spot A3 → isOccupied? → false -----> assign(A3)
Gate 2: Check spot A3 → isOccupied? → false -----> assign(A3) ← BUG! Both assigned!
```

**The fix**: Make check-then-act atomic.

```java
public synchronized boolean assign(Vehicle vehicle) {
    // This whole block runs as ONE UNIT — no other thread can enter
    if (isOccupied) return false;    // Check
    this.currentVehicle = vehicle;    // Act
    this.isOccupied = true;          // Act
    return true;
}
```

**The intuition**: When you have a "check then act" pattern (check if empty → then fill), there's ALWAYS a race condition unless you make it atomic. `synchronized` is the simplest atomic wrapper for a single JVM.

---

### 🔑 Key Intuition #4: The Real Interview Flow

Here's exactly how this plays out:

> **Interviewer**: "Design a parking lot system."
>
> **Junior Dev**: "Okay, I'll make a ParkingLot class with spots. Let me code..."
> ❌ No questions asked. Code starts immediately. Messy design. Misses requirements.

> **Mid Dev**: "What vehicles? How many floors? Ok, I'll use an enum for vehicle type, a ParkingSpot class, and a ParkingLot class..."
> ⚠️ Basic questions asked. Design is functional but not extensible. Patterns not used well.

> **Senior Dev (YOU)**: 
> "Let me understand the scope first..."
> [Asks 7-8 clarifying questions]
> "Based on that, here's what I see..."
> [Decomposes into components]
> "For pricing, I'll use Strategy Pattern because..."
> [Explains WHY not just WHAT]
> "The tricky part is concurrent spot assignment. Here's my approach..."
> [Solves the hard problem first]
> "Let me code the core flow..."
> [Writes clean, minimal code]
> "Some edge cases I'm thinking about..."
> [Proactively addresses failure modes]

**The senior engineer's approach wins because they:**
1. Show they can handle ambiguity (ask questions)
2. Show they can think in abstractions (decompose)
3. Show they know WHEN and WHY to use patterns
4. Show they anticipate failure (edge cases)
5. Write code that's CLEAN and EXTENSIBLE

---

## 💻 Phase 5: Complete Implementation

### Package Structure

```
com.parkinglot/
├── Main.java                          # Entry point with demo
├── model/                             # Core entities
│   ├── Vehicle.java                   # Abstract base
│   ├── Car.java, Bike.java, Truck.java
│   ├── ParkingSpot.java
│   ├── ParkingSpotType.java           # Enum
│   ├── VehicleType.java               # Enum
│   └── Ticket.java
├── parking/
│   ├── ParkingLot.java                # Singleton
│   ├── ParkingFloor.java
│   └── ParkingStrategy.java           # Interface + impls
├── pricing/
│   ├── PricingStrategy.java           # Interface + impls
├── factory/
│   └── VehicleFactory.java
├── service/
│   ├── ParkingService.java
│   ├── TicketService.java
│   └── PaymentService.java
└── exception/
    └── ParkingException.java
```

### 1. VehicleType.java

```java
/**
 * INTUITION: Why enum?
 * 
 * I need a fixed set of vehicle types that:
 * 1. Can't have typos (no "CAR" vs "car" vs "Car" bugs)
 * 2. Can carry extra info (like spot size needed)
 * 3. Can be used in switch statements
 * 
 * String constants would fail on point 1.
 * Class hierarchy would be overkill for just type identification.
 * Enum hits the sweet spot.
 */
public enum VehicleType {
    CAR(2),     // Standard car needs 2 spot units
    BIKE(1),    // Bike needs 1 spot unit
    TRUCK(4);   // Truck needs 4 spot units

    private final int spotsNeeded;

    VehicleType(int spotsNeeded) {
        this.spotsNeeded = spotsNeeded;
    }

    public int getSpotsNeeded() {
        return spotsNeeded;
    }
}
```

### 2. Vehicle.java — Abstract Base Class

```java
/**
 * INTUITION: Why abstract class?
 * 
 * Think of it this way:
 * - A Car IS a Vehicle ✓
 * - A Bike IS a Vehicle ✓
 * - A Truck IS a Vehicle ✓
 * 
 * All vehicles share: license plate, unique ID, type
 * Vehicles differ in: size, special behaviors
 * 
 * Abstract class captures the "IS-A" relationship with shared state.
 * Interface captures "CAN-DO" capability (e.g., Comparable, Runnable).
 * 
 * Here, Vehicle is clearly an "IS-A" → Abstract class.
 * 
 * TEMPLATE METHOD PATTERN: 
 * Subclasses only need to specify their type.
 * The base class handles everything else.
 */
public abstract class Vehicle {
    private final String id;           // UUID — unique across system
    private final String licensePlate; // Human-readable identifier
    private final VehicleType type;    // CAR, BIKE, or TRUCK

    protected Vehicle(String licensePlate, VehicleType type) {
        // FAIL FAST: Validate at construction time, not later
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate is required");
        }
        this.id = UUID.randomUUID().toString();
        this.licensePlate = licensePlate.trim().toUpperCase();
        this.type = type;
    }

    // Immutable after creation — no setters
    public String getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle vehicle)) return false;
        return Objects.equals(licensePlate, vehicle.licensePlate);
    }

    @Override
    public int hashCode() { return Objects.hash(licensePlate); }

    @Override
    public String toString() {
        return type + " [" + licensePlate + "]";
    }
}
```

### 3. Concrete Vehicle Classes

```java
/**
 * INTUITION: Each subclass is TINY.
 * 
 * This is the Template Method pattern in action:
 * - Base class provides 95% of the logic
 * - Subclasses just specify what makes them unique
 * 
 * If you're writing more than 1 line per subclass, 
 * you're probably doing something wrong.
 */
public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);  // Just specify the type
    }
}

public class Bike extends Vehicle {
    public Bike(String licensePlate) {
        super(licensePlate, VehicleType.BIKE);
    }
}

public class Truck extends Vehicle {
    public Truck(String licensePlate) {
        super(licensePlate, VehicleType.TRUCK);
    }
}
```

### 4. ParkingSpot.java — THE Critical Section

```java
/**
 * INTUITION: This is THE most important class in the system.
 * 
 * Why? Because the core business rule is:
 * "No two vehicles can occupy the same spot at the same time."
 * 
 * This is where race conditions happen.
 * This is where money is lost if we mess up.
 * This is what the interviewer WILL ask about.
 * 
 * The pattern: CHECK → then → ACT
 *   CHECK: is the spot occupied?
 *   ACT:   if no, assign the vehicle
 *   
 * PROBLEM: Between CHECK and ACT, another thread could slip in.
 * SOLUTION: synchronized makes CHECK+ACT atomic.
 */
public class ParkingSpot {
    private final String id;
    private final String spotNumber;  // e.g. "A3", "B12"
    private final ParkingSpotType type;
    private volatile boolean isOccupied;  // volatile = visibility across threads
    private Vehicle currentVehicle;

    public ParkingSpot(String spotNumber, ParkingSpotType type) {
        this.id = UUID.randomUUID().toString();
        this.spotNumber = spotNumber;
        this.type = type;
        this.isOccupied = false;
    }

    /**
     * INTUITION BEHIND synchronized:
     * 
     * Thread 1 → enters assign() → locks the method → checks isOccupied → false → assigns
     * Thread 2 → tries assign() → WAITS (Thread 1 has the lock)
     * Thread 1 → finishes assign() → unlocks
     * Thread 2 → enters assign() → locks → checks isOccupied → TRUE → returns false
     * 
     * Without synchronized:
     * Thread 1 → checks isOccupied → false
     * Thread 2 → checks isOccupied → false  ← SAME SPOT, BOTH SAY AVAILABLE!
     * Thread 1 → assigns vehicle → isOccupied = true
     * Thread 2 → assigns vehicle → isOccupied = true ← OVERWRITES! BUG!
     */
    public synchronized boolean assign(Vehicle vehicle) {
        // Guard clause: fail fast if occupied or incompatible
        if (isOccupied) return false;
        if (!type.canPark(vehicle)) return false;
        
        this.currentVehicle = vehicle;
        this.isOccupied = true;
        return true;
    }

    public synchronized Vehicle vacate() {
        if (!isOccupied) return null;
        
        Vehicle vehicle = this.currentVehicle;
        this.currentVehicle = null;
        this.isOccupied = false;
        return vehicle;
    }

    // Getters
    public String getId() { return id; }
    public String getSpotNumber() { return spotNumber; }
    public ParkingSpotType getType() { return type; }
    public boolean isOccupied() { return isOccupied; }
    public synchronized Vehicle getCurrentVehicle() { return currentVehicle; }

    @Override
    public String toString() {
        return "Spot[" + spotNumber + "] " + type + 
               (isOccupied ? " [Occupied]" : " [Available]");
    }
}
```

### 5. Ticket.java

```java
/**
 * INTUITION: The Ticket is a "value object" that tracks a parking session.
 * 
 * Key decisions:
 * 1. Fee calculation is DELEGATED to PricingStrategy (Strategy Pattern)
 * 2. Time is recorded at creation (entryTime) and at fee calculation (exitTime)
 * 3. Status prevents double-completion
 * 
 * WHY delegate fee?
 * Because pricing rules change more often than ticket structure.
 * If we embed pricing in Ticket, every price change requires:
 * - Modifying Ticket class
 * - Recompiling Ticket
 * - Retesting all Ticket functionality
 * 
 * With delegation, pricing changes are isolated to new strategy classes.
 */
public class Ticket {
    private final String id;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double amount;
    private TicketStatus status;

    public enum TicketStatus { ACTIVE, COMPLETED, LOST }

    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.id = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
        this.status = TicketStatus.ACTIVE;
        this.amount = 0.0;
    }

    /**
     * INTUITION: This is where Strategy Pattern shines.
     * 
     * The Ticket doesn't need to know HOW pricing works.
     * It just asks the strategy to calculate.
     * 
     * Want weekend pricing? Pass WeekendPricingStrategy.
     * Want holiday pricing? Pass HolidayPricingStrategy.
     * Want both? Use Decorator pattern to combine them.
     * 
     * Ticket NEVER changes. Strategies are added independently.
     */
    public double calculateFee(PricingStrategy strategy) {
        if (status != TicketStatus.ACTIVE) {
            throw new IllegalStateException("Cannot calculate fee for " + status + " ticket");
        }
        this.exitTime = LocalDateTime.now();
        long durationMinutes = Duration.between(entryTime, exitTime).toMinutes();
        this.amount = strategy.calculatePrice(durationMinutes, vehicle.getType());
        return this.amount;
    }

    public void complete() { this.status = TicketStatus.COMPLETED; }

    // Getters
    public String getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getAmount() { return amount; }
    public TicketStatus getStatus() { return status; }
}
```

### 6. PricingStrategy.java — Strategy Pattern

```java
/**
 * INTUITION: WHY Strategy Pattern?
 * 
 * Real-world scenario:
 * Month 1: "Just charge $10/hour for all vehicles"
 * Month 2: "Bikes should be cheaper, trucks more expensive"
 * Month 3: "Add weekend surcharge"
 * Month 4: "Holiday special — 50% off"
 * 
 * If pricing is hardcoded in Ticket.calculateFee():
 * ❌ Month 1 works. Month 2 modifies Ticket. Month 3 modifies Ticket again.
 * ❌ Each change risks breaking previous pricing logic.
 * ❌ Testing matrix grows exponentially.
 * 
 * With Strategy Pattern:
 * ✅ Month 1: HourlyPricingStrategy (new class)
 * ✅ Month 2: Different rates in the same strategy (modify one class)
 * ✅ Month 3: WeekendDecorator wraps existing strategy (new class)
 * ✅ Month 4: HolidayDecorator wraps existing strategy (new class)
 * ✅ Existing code NEVER changes. New strategies are ADDED.
 * 
 * This is the Open/Closed Principle in action:
 * OPEN for extension (add new strategies)
 * CLOSED for modification (existing code stays unchanged)
 */
@FunctionalInterface
public interface PricingStrategy {
    double calculatePrice(long durationMinutes, VehicleType vehicleType);
}
```

```java
/**
 * The base implementation.
 * Start simple. Complexity is added via Decorators.
 */
public class HourlyPricingStrategy implements PricingStrategy {
    private static final Map<VehicleType, Double> HOURLY_RATES = Map.of(
        VehicleType.CAR, 10.0,
        VehicleType.BIKE, 5.0,
        VehicleType.TRUCK, 20.0
    );
    
    private static final long FREE_MINUTES = 30;

    @Override
    public double calculatePrice(long durationMinutes, VehicleType vehicleType) {
        if (durationMinutes <= FREE_MINUTES) return 0.0;
        
        double hourlyRate = HOURLY_RATES.getOrDefault(vehicleType, 10.0);
        // Ceiling division — round UP to nearest hour
        long hours = (durationMinutes - FREE_MINUTES + 59) / 60;
        
        return hours * hourlyRate;
    }
}
```

```java
/**
 * DECORATOR PATTERN: Add weekend surcharge WITHOUT changing base.
 * 
 * INTUITION: Why Decorator over modifying the base?
 * 
 * Modifying HourlyPricingStrategy:
 * - Changes behavior for ALL scenarios
 * - Need to test: weekday, weekend, holiday, special events
 * - Risk: "I broke normal pricing while adding weekend logic"
 * 
 * Decorator approach:
 * - WeekendSurcharge WRAPS HourlyPricingStrategy
 * - HourlyPricingStrategy doesn't know about weekends
 * - Both can be tested independently
 * - Can combine: new WeekendSurcharge(new HolidayDiscount(new HourlyPricingStrategy()))
 * 
 * This is the DECORATOR PATTERN:
 * Wraps an object to add behavior without changing the original class.
 */
public class WeekendSurchargeStrategy implements PricingStrategy {
    private final PricingStrategy base;
    private static final double SURCHARGE = 1.5;

    public WeekendSurchargeStrategy(PricingStrategy base) {
        this.base = base;
    }

    @Override
    public double calculatePrice(long durationMinutes, VehicleType vehicleType) {
        double basePrice = base.calculatePrice(durationMinutes, vehicleType);
        
        LocalDateTime now = LocalDateTime.now();
        if (now.getDayOfWeek() == DayOfWeek.SATURDAY || 
            now.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return basePrice * SURCHARGE;
        }
        return basePrice;
    }
}
```

### 7. ParkingLot.java — Singleton

```java
/**
 * INTUITION: Singleton makes sense here because...
 * 
 * A physical parking lot is ONE thing.
 * Having multiple ParkingLot instances managing the same physical spots
 * would lead to data inconsistency.
 * 
 * Singleton ensures:
 * - One instance per JVM
 * - Global access point
 * - Lazy initialization (created only when first needed)
 * 
 * BUT Singleton has drawbacks:
 * - Hard to test (can't instantiate in tests easily)
 * - Hidden dependencies
 * 
 * COMPROMISE: Use Singleton for now, but inject dependencies.
 * If we need multiple lots, we can refactor to a Factory.
 */
public class ParkingLot {
    private static volatile ParkingLot instance;
    
    private final String name;
    private final List<ParkingFloor> floors = new ArrayList<>();
    private ParkingStrategy strategy = new NearestFirstStrategy();
    
    // Private constructor — no one can create from outside
    private ParkingLot(String name) {
        this.name = name;
    }

    /**
     * Double-checked locking pattern.
     * 
     * Why volatile? 
     * Without volatile, the JVM can reorder instructions.
     * Thread 1 creates instance (not fully constructed)
     * Thread 2 sees instance != null → uses partially constructed instance
     * volatile prevents this reordering.
     * 
     * Why two null checks?
     * First check (without lock): performance optimization
     *   - Most of the time, instance already exists
     *   - No need to acquire lock if it does
     * Second check (with lock): thread safety
     *   - Two threads could both pass first check
     *   - Lock ensures only one creates the instance
     */
    public static ParkingLot getInstance(String name) {
        ParkingLot result = instance;
        if (result == null) {
            synchronized (ParkingLot.class) {
                result = instance;
                if (result == null) {
                    instance = result = new ParkingLot(name);
                }
            }
        }
        return instance;
    }

    /**
     * Find an available spot that fits the vehicle.
     * Delegates to ParkingStrategy for selection algorithm.
     */
    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        
        for (ParkingFloor floor : floors) {
            availableSpots.addAll(floor.getAvailableSpots(vehicle));
        }
        
        if (availableSpots.isEmpty()) return null;
        return strategy.findSpot(availableSpots, vehicle);
    }

    public void addFloor(ParkingFloor floor) { floors.add(floor); }
    public void setStrategy(ParkingStrategy strategy) { this.strategy = strategy; }
    
    // For testing — reset singleton
    public static void reset() { instance = null; }
}
```

### 8. ParkingService.java — The Orchestrator

```java
/**
 * INTUITION: Why a separate Service class?
 * 
 * ParkingLot (the entity) should manage floors and spots.
 * ParkingService (the service) should handle the workflow.
 * 
 * This separation lets us:
 * - Test ParkingLot logic independently
 * - Test ParkingService flow independently
 * - Change the parking algorithm without changing the flow
 * - Add new features (like reservation) without touching ParkingLot
 * 
 * This is the SINGLE RESPONSIBILITY PRINCIPLE:
 * Each class has ONE reason to change.
 * ParkingLot changes when spot management changes.
 * ParkingService changes when parking flow changes.
 */
public class ParkingService {
    private final ParkingLot parkingLot;
    private final TicketService ticketService;
    private final PaymentService paymentService;
    private final PricingStrategy pricingStrategy;
    private final ReentrantLock lock = new ReentrantLock(true); // fair lock

    public ParkingService(ParkingLot parkingLot, TicketService ticketService,
                         PaymentService paymentService, PricingStrategy pricingStrategy) {
        this.parkingLot = parkingLot;
        this.ticketService = ticketService;
        this.paymentService = paymentService;
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * INTUITION: The PARK flow
     * 
     * 1. Find a spot → if none, fail immediately
     * 2. Lock (prevent race conditions) → assign spot → unlock
     * 3. Create ticket with vehicle + spot info
     * 4. Return ticket to driver
     * 
     * Why use ReentrantLock instead of synchronized?
     * - Fair locking: threads are served in order (no starvation)
     * - Can check if lock is held
     * - More flexible timeout handling
     * - But: more verbose → need try-finally block
     */
    public Ticket parkVehicle(Vehicle vehicle) throws ParkingException {
        ParkingSpot spot = parkingLot.findAvailableSpot(vehicle);
        if (spot == null) {
            throw new ParkingException("No parking spot available", 
                ParkingException.Code.PARKING_FULL);
        }

        lock.lock();
        try {
            boolean assigned = spot.assign(vehicle);
            if (!assigned) {
                throw new ParkingException("Spot was taken", 
                    ParkingException.Code.SPOT_OCCUPIED);
            }
            return ticketService.createTicket(vehicle, spot);
        } finally {
            lock.unlock();  // ALWAYS release in finally block
        }
    }

    /**
     * INTUITION: The UNPARK flow
     * 
     * 1. Find the ticket → if invalid, fail
     * 2. Calculate fee using pricing strategy
     * 3. Process payment
     * 4. Vacate spot
     * 5. Complete ticket
     */
    public double unparkVehicle(String ticketId) throws ParkingException {
        Ticket ticket = ticketService.getTicket(ticketId);
        if (ticket == null) {
            throw new ParkingException("Invalid ticket", 
                ParkingException.Code.TICKET_NOT_FOUND);
        }

        double amount = ticket.calculateFee(pricingStrategy);
        
        try {
            paymentService.processPayment(ticket, amount);
            ticket.getSpot().vacate();
            ticket.complete();
            return amount;
        } catch (Exception e) {
            throw new ParkingException("Payment failed", 
                ParkingException.Code.PAYMENT_FAILED, e);
        }
    }
}
```

### 9. Main.java — Demo & Test

```java
/**
 * INTUITION: The Main class demonstrates the full flow.
 * 
 * This is what you'd show the interviewer to prove your design works.
 * It covers:
 * - Happy path (park → unpark)
 * - Error path (park when full)
 * - Concurrent access (multiple threads)
 * - Different pricing (weekend mode)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Parking Lot System Demo ===\n");

        try {
            // --- SETUP ---
            ParkingLot parkingLot = ParkingLot.getInstance("Central Parking");
            
            ParkingFloor floor1 = new ParkingFloor(1, 5);
            floor1.addSpot(new ParkingSpot("A1", ParkingSpotType.COMPACT));
            floor1.addSpot(new ParkingSpot("A2", ParkingSpotType.COMPACT));
            floor1.addSpot(new ParkingSpot("B1", ParkingSpotType.LARGE));
            floor1.addSpot(new ParkingSpot("C1", ParkingSpotType.BIKE));
            
            parkingLot.addFloor(floor1);
            System.out.println("✓ Setup complete: " + floor1.getAvailableCount() + " spots available\n");

            // --- SERVICES ---
            PricingStrategy pricing = new HourlyPricingStrategy();
            TicketService ticketService = new TicketService();
            PaymentService paymentService = new PaymentService();
            ParkingService parkingService = new ParkingService(
                parkingLot, ticketService, paymentService, pricing);

            // --- HAPPY PATH ---
            System.out.println("--- Happy Path: Park & Unpark ---");
            Vehicle car = VehicleFactory.createVehicle("MH-01-AB-1234", VehicleType.CAR);
            Ticket ticket = parkingService.parkVehicle(car);
            System.out.println("✓ Parked: " + car + " → Ticket: " + ticket.getId().substring(0, 8));

            Thread.sleep(50); // Simulate parking duration
            
            double fee = parkingService.unparkVehicle(ticket.getId());
            System.out.printf("✓ Unparked: Fee = $%.2f%n", fee);

            // --- ERROR PATH ---
            System.out.println("\n--- Error Path: Parking Full ---");
            try {
                for (int i = 0; i < 10; i++) {
                    Vehicle v = VehicleFactory.createVehicle("TEST-" + i, VehicleType.CAR);
                    parkingService.parkVehicle(v);
                }
            } catch (ParkingException e) {
                System.out.println("✓ Expected: " + e.getMessage());
            }

            // --- CONCURRENT ---
            System.out.println("\n--- Concurrent Access ---");
            Runnable park = () -> {
                try {
                    String name = Thread.currentThread().getName();
                    Vehicle v = VehicleFactory.createVehicle("THREAD-" + name, VehicleType.CAR);
                    Ticket t = parkingService.parkVehicle(v);
                    System.out.println("  Thread " + name + " parked: " + ticketId(t));
                } catch (ParkingException e) {
                    System.out.println("  Thread " + name + " failed: " + e.getMessage());
                }
            };

            Thread t1 = new Thread(park, "T1");
            Thread t2 = new Thread(park, "T2");
            t1.start(); t2.start();
            t1.join(); t2.join();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String ticketId(Ticket t) {
        return t.getId().substring(0, 8);
    }
}
```

---

## 🔥 Phase 6: Edge Cases — What Interviewers Look For

### The Edge Case Matrix

| Scenario | What Happens | My Solution | Interviewer's "Aha" |
|----------|-------------|-------------|---------------------|
| **Full lot** | Car arrives, no spots | Meaningful error: "Parking Full" | ✓ Clear error handling |
| **Lost ticket** | Driver can't scan | Charge max daily rate × assumed max days | ✓ Fair to both sides |
| **Race condition** | Two gates assign same spot | `synchronized` on assign() | ✓ Understands threading |
| **Payment fails** | Exit gate stuck | Retry 3×, then manual override | ✓ Graceful degradation |
| **Sensor failure** | Car leaves without scan | Reconciliation job cleans up after 24h | ✓ Background processing |
| **Wrong type** | Truck parks in bike spot | `canPark()` validation rejects at entry | ✓ Input validation |
| **System crash** | Crash mid-transaction | DB transaction rolls back | ✓ ACID awareness |
| **Power outage** | Gates stop working | Battery backup, manual ticket system | ✓ Real-world thinking |

### How To Talk About Edge Cases

> **You**: "Let me think about what could go wrong..."
>
> "The most critical failure is a **race condition** — two gates assigning the same spot. I handle this with synchronized on the assign method."
>
> "A **payment failure** at exit is bad UX. I'd implement retry logic — at least 3 attempts with a 5-second backoff. If all fail, generate a manual override code that security can use to open the gate."
>
> "For **lost tickets**, the standard practice is to charge the maximum daily rate for a fixed number of days. It's fair — the driver could have been here that long."
>
> "If the **system crashes** while a transaction is mid-process... hmm, that's tricky. I'd wrap the park and unpark operations in database transactions. If no DB, I'd implement an event log with replay capability."

---

## ❓ Phase 7: All Possible Follow-up Questions

### Category 1: Extensions (They want to see how you handle new requirements)

| Question | What They're Testing | Your Answer |
|----------|---------------------|-------------|
| "Add EV charging" | Can you extend without breaking existing code? | Add ChargingPort on some spots. ElectricVehicle subclass. Modify pricing to include charging fee. Add ChargingService. |
| "Monthly subscriptions" | Can you handle billing models? | Add Subscription entity (vehicle + validity). PricingStrategy checks for active subscription. Charge $0 if active. |
| "Reservation system" | Can you handle time-based state? | ReservationService. Reserve spots for future time slots. Lock for 15 min on no-show. Add to ParkingSpot state machine. |
| "VIP spots near entrance" | Can you handle priority allocation? | Add ParkingStrategy: VipFirstStrategy. VIP spots get priority. Regular spots used otherwise. |
| "Fleet vehicles (same company)" | Can you handle bulk operations? | FleetAccount with billing. Monthly invoice. Bulk reservation. Analytics dashboard. |

### Category 2: Concurrency & Scale (They want to see distributed systems knowledge)

| Question | What They're Testing | Your Answer |
|----------|---------------------|-------------|
| "Scale to 100 lots across city" | Distributed architecture thinking | Central API Gateway. Shard by lot_id. Redis per lot for real-time availability. Add FindParkingService for cross-lot search. |
| "Handle 10,000 cars/hour" | Performance optimization | Read replicas for availability queries. Async payment processing via Kafka. Redis for spot state (fast). Batch write to DB. |
| "Multi-datacenter deployment" | Disaster recovery | Active-passive with failover. Geo-routing to nearest DC. Async replication between DCs. |
| "What if Redis goes down?" | Degraded operation | Fall back to database reads (slower but works). Circuit breaker pattern. Health check + auto-reconnect. |
| "How to handle hot spots?" | Load balancing | Pre-compute and cache availability. Shard by floor ID. Rate limit at entry gates. |

### Category 3: Design Decisions (They want to challenge your choices)

| Question | What They're Testing | Your Answer |
|----------|---------------------|-------------|
| "Why Strategy Pattern? Just use if-else" | Knows when patterns add value | "If-else works for 2-3 variations. But pricing changes weekly. Each if-else branch needs testing. Strategy isolates changes to new classes. Single Responsibility." |
| "Why not just use LinkedHashMap for LRU?" | Knows data structure trade-offs | "I could, but implementing it myself shows I understand the internals. For production, yes, I'd use LinkedHashMap." |
| "Singleton is anti-pattern, why use it?" | Knows trade-offs | "You're right, it has testing issues. But this is a single physical lot. I'm injecting dependencies, so I can reset() for tests. If we need multiple lots, I'll refactor to a Factory." |
| "synchronized kills performance, why not lock-free?" | Concurrency expertise | "synchronized is fine for 100ms operations. For high throughput, I'd use ReentrantLock with tryLock. For distributed, optimistic locking via DB version column." |

### Category 4: Tricky Situations (They want to see problem-solving)

| Question | Your Answer |
|----------|-------------|
| "Someone stays for 30 days, fee is $7200, they can't pay" | Cap at daily max × N days. Offer payment plan. Flag for admin. Don't hold person hostage - release car, invoice later. |
| "Driver says they paid but system shows no payment" | Check transaction log. Check payment gateway (could be async settlement). As last resort, review CCTV and manually release. |
| "Revenue is missing — some cars are parking without tickets" | Entry/exit sensor monitoring. If exit sensor triggers without ticket scan → alert admin. CCTV integration for audit trails. |
| "Two cars have the same license plate" | Use internal UUID as primary key. License plate is for display only. Flag duplicates for manual review. |
| "Valet parking — driver drops car, someone else parks it" | Add driver info to ticket (name, phone). Valet gets a special "valet" ticket. Spot is assigned to the car, not the driver. |

---

## 📊 Complexity & Performance

```
Operation    | Time      | Space     | Bottleneck
-------------|-----------|-----------|-------------------
Park         | O(F×S)    | O(1)      | Spot search (can optimize)
Unpark       | O(1)      | O(1)      | None (hash lookup)
Fee calc     | O(1)      | O(1)      | None (pure math)

F = floors, S = spots per floor
```

### Optimization Progression

```
Phase 1: In-memory, linear scan (this solution)
  ✓ Works for 1 lot, 1000 spots
  ✓ Fine for interview scope

Phase 2: Add availability cache per floor
  O(1) spot search instead of O(F×S)
  Trade-off: extra memory for counters

Phase 3: Database persistence with Redis cache
  Survives restarts
  Multiple lot support
  Trade-off: slower, more complex

Phase 4: Distributed, sharded, event-driven
  City-wide scale
  Kafka for async events
  Trade-off: eventual consistency
```

---

## 🎯 Interview Scoring Checklist

```
Before Code (40% of score):
[x] Asked clarifying questions about requirements
[x] Broke problem into components
[x] Discussed trade-offs out loud
[x] Named patterns with reasons WHY (not just WHAT)

During Code (40% of score):
[x] Started with entities, then services, then wiring
[x] Handled race conditions explicitly
[x] Used meaningful names (not x, y, data)
[x] Thought about validation and edge cases

After Code (20% of score):
[x] Discussed testing strategy
[x] Addressed scaling concerns
[x] Handled follow-up questions gracefully
[x] Admitted trade-offs honestly
```

---

## ✅ What Senior Engineers Do Differently

| Junior | Senior |
|--------|--------|
| Starts coding immediately | Asks 5-7 clarifying questions first |
| Writes everything in one class | Splits into entities, services, strategies |
| Hardcodes pricing logic | Uses Strategy Pattern |
| Ignores thread safety | Makes critical sections synchronized |
| Writes the happy path only | Thinks about edge cases proactively |
| Uses patterns because "they're cool" | Uses patterns because they solve a specific problem |
| Takes feedback defensively | Says "Great point, let me think about that" |
| Tries to build everything | Scopes well, asks "what's in/out of scope" |