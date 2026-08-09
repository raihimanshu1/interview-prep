# Module 11 — System Design: LLD & HLD — Interview Q&A

> **Skill**: `interview-classroom-content` — Strict Answer Framework applied.

---

## Q1. Design a URL Shortener (like bit.ly) — HLD

### 1. Why This Concept Matters
URL shortener is the classic system design interview question. It tests: hashing, database design, caching, rate limiting, and scaling. Interviewers ask this to evaluate **end-to-end architecture thinking**.

### 2. Requirements

**Functional:**
- Generate short URL for given long URL
- Redirect short URL to original URL
- Custom alias option
- Track click analytics

**Non-functional:**
- High availability (99.99%)
- Low latency (<100ms redirect)
- Scalable to billions of URLs

### 3. High-Level Design

```
                         ┌──────────┐
                         │  Client  │
                         └────┬─────┘
                              │
                        ┌─────▼──────┐
                        │  Load      │
                        │  Balancer  │
                        └─────┬──────┘
                              │
                  ┌───────────┴───────────┐
                  │                       │
            ┌─────▼─────┐          ┌──────▼──────┐
            │  Write     │          │  Read       │
            │  API       │          │  API        │
            │  (create)  │          │  (redirect) │
            └─────┬─────┘          └──────┬──────┘
                  │                       │
            ┌─────▼─────┐          ┌──────▼──────┐
            │  URL       │          │  Cache      │
            │  Service   │          │  (Redis)    │
            └─────┬─────┘          └──────┬──────┘
                  │                       │
            ┌─────▼───────────────────────▼──────┐
            │           Database (Cassandra)      │
            │   ┌────────────────────────────┐    │
            │   │ url_id (PK) | long_url     │    │
            │   │ abc123    | google.com/... │    │
            │   │ xyz789    | facebook.com/..│    │
            │   └────────────────────────────┘    │
            └────────────────────────────────────┘
```

### 4. Key Design Decisions

**Hashing strategy — generating short key:**
```java
// Option 1: Base62 encoding (6 chars = 62^6 = 56 billion URLs)
String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

public String encode(long id) {
    StringBuilder sb = new StringBuilder();
    while (id > 0) {
        sb.append(base62.charAt((int)(id % 62)));
        id /= 62;
    }
    return sb.reverse().toString();  // e.g., id=125 → "cb"
}

// Option 2: MD5 hash + take first 6 chars (collision risk!)
// Use base62 encoding for compactness + collision check

// Database ID → base62 encoding:
// ID = 1 → "a", ID = 62 → "ab", ID = 3844 → "abc"
// With 6 chars: 56 billion unique URLs
```

**Redirect flow (read-heavy — 99% reads):**
```
1. Client GET /abc123
2. Load balancer → Read API
3. Read API checks Redis cache:
   - Cache hit (99%): return long URL → HTTP 302 redirect
   - Cache miss (1%): query Cassandra → set cache → redirect
4. Total time: <5ms (cache) / <50ms (DB)
```

### 5. Scaling Considerations

| Component | Strategy |
|-----------|----------|
| Database | Cassandra (wide-column) — partition by url_id, no joins needed |
| Cache | Redis cluster — 99% hit rate with LRU eviction |
| Rate limiting | Token bucket per IP/user — 100 creates/min |
| Analytics | Separate Kafka pipeline — async, doesn't block redirect |
| ID generation | Snowflake ID (distributed, unique, time-sorted) |

### 6. Interview Question

**Q**: How would you handle 10M redirects/second?

**A**: (1) Multi-tier caching: L1 (in-memory local cache per server) + L2 (Redis cluster). L1 cache for ~80% of traffic (fastest, ~1ms). (2) Anycast DNS — route users to nearest data center. (3) CDN for popular short URLs — cache redirects at edge. (4) Precompute redirects for top 1% URLs (store in CDN). (5) Write-behind cache for click analytics (batch write to Kafka, not per-request DB write). (6) Auto-scaling: CPU > 70% → spawn more read API instances.

---

## Q2. Design a Parking Lot System — LLD (Object-Oriented Design)

### 1. Requirements
- Multiple floors, each with parking spots of different sizes (Small, Medium, Large)
- Vehicles: Motorcycle, Car, Truck (each requires specific spot size)
- Ticket on entry, payment on exit
- Track available spots per floor
- Support hourly pricing

### 2. Class Design

```java
// =====================================================
// ENUMS & VALUE OBJECTS
// =====================================================
public enum VehicleType { MOTORCYCLE, CAR, TRUCK }
public enum SpotSize { SMALL, MEDIUM, LARGE }
public enum SpotStatus { AVAILABLE, OCCUPIED }

// =====================================================
// PARKING SPOT
// =====================================================
public class ParkingSpot {
    private int id;
    private SpotSize size;
    private SpotStatus status;
    private Vehicle vehicle;
    private int floor;
    
    public boolean canFit(Vehicle vehicle) {
        // SMALL fits motorcycle only
        // MEDIUM fits motorcycle + car
        // LARGE fits all
        return switch(vehicle.getType()) {
            case MOTORCYCLE -> true;  // can fit any spot
            case CAR -> this.size != SpotSize.SMALL;
            case TRUCK -> this.size == SpotSize.LARGE;
        };
    }
}

// =====================================================
// VEHICLE (Abstract)
// =====================================================
public abstract class Vehicle {
    private String licensePlate;
    private VehicleType type;
    // getters
}

public class Motorcycle extends Vehicle {
    public Motorcycle(String plate) {
        super(plate, VehicleType.MOTORCYCLE);
    }
}

// =====================================================
// TICKET
// =====================================================
public class Ticket {
    private String id;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double amount;
    
    public double calculatePayment(PricingStrategy strategy) {
        long hours = ChronoUnit.HOURS.between(entryTime, exitTime);
        if (hours < 1) hours = 1;
        return strategy.calculate(hours, spot.getSize());
    }
}

// =====================================================
// PARKING LOT (Singleton — one per physical location)
// =====================================================
public class ParkingLot {
    private static ParkingLot INSTANCE;
    private List<ParkingFloor> floors;
    private Map<String, Ticket> activeTickets;
    
    private ParkingLot() {}  // Singleton
    
    public static synchronized ParkingLot getInstance() {
        if (INSTANCE == null) INSTANCE = new ParkingLot();
        return INSTANCE;
    }
    
    public Ticket enter(Vehicle vehicle) {
        // Find available spot
        ParkingSpot spot = findAvailableSpot(vehicle);
        if (spot == null) throw new ParkingFullException("No spot available");
        
        // Assign spot
        spot.park(vehicle);
        Ticket ticket = new Ticket(UUID.randomUUID().toString(), vehicle, spot);
        activeTickets.put(ticket.getId(), ticket);
        return ticket;
    }
    
    public PaymentReceipt exit(String ticketId, PaymentMethod payment) {
        Ticket ticket = activeTickets.get(ticketId);
        ticket.setExitTime(LocalDateTime.now());
        
        double amount = ticket.calculatePayment(new HourlyPricing());
        payment.process(amount);
        
        ticket.getSpot().vacate();
        activeTickets.remove(ticketId);
        
        return new PaymentReceipt(ticket, amount);
    }
    
    private ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isAvailable() && spot.canFit(vehicle)) {
                    return spot;
                }
            }
        }
        return null;
    }
}
```

### 3. Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Singleton** | ParkingLot | One parking lot instance per location |
| **Strategy** | Pricing | Different pricing strategies (hourly, daily, weekend) |
| **Factory** | Spot creation | Create spots based on floor configuration |
| **Observer** | Entry/exit | Notify display boards when spots change |

### 4. Interview Questions

#### Q: Design the database schema for a parking lot system.
**A**: Tables: `parking_lot (id, name, address)`, `floor (id, lot_id, number)`, `spot (id, floor_id, size, status)`, `vehicle (id, license, type)`, `ticket (id, vehicle_id, spot_id, entry_time, exit_time, amount)`. Index: ticket(vehicle_id) for active tickets, spot(floor_id, status) for available spots.

#### Q: How would you handle peak hour congestion?
**A**: (1) Pre-book spots via mobile app; (2) Reserve entry lanes for pre-booked customers; (3) Display real-time availability on approach roads; (4) Dynamic pricing — higher rates during peak hours; (5) Valet parking overflow — use nearby lots; (6) Grace period for exit (15 min free) to prevent checkout line buildup.

**Final 30-Second**: System design follows: requirements → scale estimation → data model → API design → component architecture → trade-off analysis. For LLD: use design patterns (Singleton, Strategy, Factory), SOLID principles, clean interfaces. Always consider failure modes, not just happy path.