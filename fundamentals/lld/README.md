# Low-Level Design (LLD) — Complete Deep Dive

## 1. Why This Concept Matters

Low-Level Design (LLD) is the ability to translate real-world requirements into clean, maintainable, object-oriented code. In a typical LLD interview, you're given a problem (parking lot, elevator system, chess game, vending machine) and asked to design the classes, interfaces, and relationships. The interviewer evaluates your ability to identify core entities, model relationships (inheritance, composition, association), apply design patterns appropriately, and write SOLID-compliant code that is extensible for future requirements. Unlike HLD which tests architectural thinking, LLD tests your OOP skills — can you write code that is clean, testable, and easy to extend? Senior engineers are expected to design systems where adding a new vehicle type (e.g., electric car charging spot) requires minimal changes.

Misunderstanding LLD causes:
- God classes: a single class handles everything (breaks SRP, impossible to test)
- Tight coupling: changing one class breaks many others
- No separation of concerns: business logic mixed with I/O, data access, and presentation
- Switch/if-else chains that grow forever (breaks OCP — adding new type requires modifying existing code)
- Not handling edge cases: null checks, empty states, invalid inputs

## 2. The Framework

**Step 1: Clarify Requirements**
Ask questions to understand the scope:
- What are the core entities? (nouns → classes)
- What actions can be performed? (verbs → methods)
- What are the constraints? (capacity, time, concurrency)
- What are the extension scenarios? (new types, new payment methods, new rules)

**Step 2: Identify Core Entities**
List the nouns from requirements. Each becomes a class. Example for Parking Lot:
- Vehicle, Car, Bike, Truck (vehicle types)
- ParkingSpot, ParkingFloor, ParkingLot (spaces)
- ParkingTicket, Payment (transactions)
- EntryGate, ExitGate (entry/exit points)

**Step 3: Define Relationships**
- Inheritance: `Car extends Vehicle`, `Bike extends Vehicle`
- Composition: `ParkingLot has ParkingFloor has ParkingSpot`
- Association: `ParkingTicket associated with Vehicle and ParkingSpot`
- Aggregation: `ParkingLot contains EntryGate list`

**Step 4: Define Interfaces**
Contracts between components. This enables loose coupling:
- `ParkingStrategy` (different algorithms for finding spots)
- `PricingStrategy` (hourly, daily, weekend pricing)
- `PaymentStrategy` (credit card, cash, digital wallet)
- `Observable/Notifier` (for event-driven updates)

**Step 5: Apply Design Patterns**
- Factory: create different Vehicle types or Payment methods
- Strategy: swappable pricing calculation
- Observer: notification when parking spot becomes available
- State: vending machine states (idle, selecting, paying, dispensing)
- Singleton: ParkingLot instance

**Step 6: Write Code**
Implement the classes with attention to:
- Encapsulation: private fields, public methods
- Immutability where appropriate
- Null safety: return Optional or check for null
- Thread safety: if concurrent access expected
- Error handling: meaningful exceptions

**Step 7: Discuss Extensions**
Show how to add new features without modifying existing code:
- "If we add a new vehicle type (ElectricCar), we just extend Vehicle and implement getSpotsNeeded()"
- "If we add dynamic pricing, we create DynamicPricingStrategy implementing PricingStrategy"

## 3. Common LLD Problems

### Parking Lot
```
Requirements:
  - Multiple floors, each with different spot types (compact, large, handicapped)
  - Different vehicle types (car, bike, truck) require different spot sizes
  - Ticket-based: issue ticket on entry, calculate fee on exit
  - Multiple entry/exit gates
  - Track available spots per floor per type

Key classes:
  - Vehicle (abstract): licensePlate, getSpotsNeeded()
  - Car, Bike, Truck extends Vehicle
  - ParkingSpot: id, floor, spotType, isAvailable()
  - ParkingFloor: floorNumber, spotsByType, findAvailableSpot(vehicle)
  - ParkingLot: singleton, floors, entryGates, exitGates
  - ParkingTicket: ticketId, vehicle, spot, entryTime
  - PricingStrategy: interface, HourlyPricing, DailyPricing

Patterns:
  - Factory: VehicleFactory.create("car") → new Car()
  - Strategy: PricingStrategy (swap hourly vs daily)
  - Singleton: ParkingLot (one instance)

Extension:
  - Add electric car: extend Vehicle, add ChargingSpot extends ParkingSpot
  - Add reservation: ReservationService + ReservationStatus enum
  - Add valet parking: ValetParkingService collects car
```

### Elevator System
```
Requirements:
  - Multiple elevators serving multiple floors
  - Internal panel (floor selection) + external panel (up/down buttons)
  - Elevator moves up/down, opens/closes doors
  - Optimize: minimize wait time, minimize energy consumption

Key classes:
  - Elevator: id, currentFloor, direction (UP/DOWN/IDLE), doorsOpen
  - ElevatorController: elevators, requestQueue, schedule()
  - FloorPanel: upButton, downButton
  - InternalPanel: floorButtons, openDoor, closeDoor, emergency
  - Request: sourceFloor, destinationFloor, direction
  - SchedulingStrategy: interface, ScanAlgorithm, FCFS, LookAlgorithm

Patterns:
  - Strategy: SchedulingStrategy (scan vs FCFS vs Look)
  - State: ElevatorState (IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPEN)
  - Observer: Button → Controller (when pressed, controller schedules)

Concurrency:
  - Multiple elevators run concurrently
  - Thread-safe request queue (BlockingQueue)
  - Elevator movement in separate thread

Extension:
  - Add weight sensor (overload → skip floor, close door)
  - Add fire mode (all elevators go to ground floor)
  - Add VIP mode (prioritize VIP floor requests)
```

### Vending Machine
```
Requirements:
  - Dispense products on coin/card payment
  - Accept coins (1, 5, 10, 25 cents) and bills
  - Track inventory (product, quantity, price)
  - Return change
  - Multiple states: idle, selecting, paying, dispensing, out-of-stock

Key classes:
  - Product: name, price, quantity
  - Inventory: Map<Product, Integer>, addProduct, removeProduct, isAvailable
  - Coin: DENOMINATIONS (PENNY, NICKEL, DIME, QUARTER)
  - PaymentProcessor: accept(Coin), accept(Bill), getTotal(), refund()
  - Dispenser: dispense(Product)
  - VendingMachine: state, inventory, payment, dispenser

Patterns:
  - State: VendingMachineState (IDLE, SELECTING, PAYING, DISPENSING, OUT_OF_STOCK)
  - State transitions: IDLE → SELECTING (product selected) → PAYING (coin inserted) → DISPENSING (payment complete) → IDLE
  - Method: selectProduct() verifies state = IDLE, transitions to SELECTING
  - insertCoin() verifies state = SELECTING or PAYING

Extension:
  - Add credit card reader: implement PaymentMethod interface
  - Add discount logic: DiscountStrategy interface
  - Add restocking mode: maintainence override state
```

### Chess Game
```
Requirements:
  - 8x8 board, 2 players (white/black)
  - 6 piece types: King, Queen, Bishop, Knight, Rook, Pawn
  - Each piece has specific movement rules
  - Check/checkmate detection
  - Move history (undo support)

Key classes:
  - Piece (abstract): color, position, isValidMove(Board, from, to)
  - King, Queen, Bishop, Knight, Rook, Pawn extends Piece
  - Board: 8x8 grid of squares, each square may contain a piece
  - Move: from, to, piece, capturedPiece
  - Game: board, currentPlayer, moveHistory, status()
  - MoveValidator: validateMove(Board, Move, Player) → boolean
  - CheckDetector: isCheck(Board, Player), isCheckmate(Board, Player)

Patterns:
  - Strategy: each piece's movement validation (Piece.isValidMove)
  - Factory: PieceFactory.create(PieceType, Color) → new King(...)
  - Command: Move class (can undo by reversing the move)

Extension:
  - Add timed mode: ChessClock, Timer
  - Add castling, en passant: extend MoveValidator
  - Add AI: MinimaxStrategy implements MoveStrategy
  - Add game recording: PGN export (standard chess notation)
```

## 4. Key Automation Techniques

**Avoiding switch/if-else growth with polymorphism:**
```java
// BAD — adding new vehicle type requires modifying this method
double calculateParkingFee(Vehicle v, long hours) {
    if (v instanceof Car) return hours * 10;
    else if (v instanceof Bike) return hours * 5;
    else if (v instanceof Truck) return hours * 20;
    else throw new IllegalArgumentException();
}

// GOOD — each vehicle type defines its own rate
abstract class Vehicle {
    abstract double getHourlyRate();
}
class Car extends Vehicle { double getHourlyRate() { return 10; } }
class Bike extends Vehicle { double getHourlyRate() { return 5; } }
class Truck extends Vehicle { double getHourlyRate() { return 20; } }
// New vehicle: just extend Vehicle, no existing code changes
```

**Using Factory for object creation:**
```java
// Instead of: new Car(), new Bike(), new Truck() scattered everywhere
class VehicleFactory {
    static Vehicle create(String type) {
        return switch(type) {
            case "car" -> new Car();
            case "bike" -> new Bike();
            case "truck" -> new Truck();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
    // New type: add case to switch. Single point of change.
}
```

**Strategy pattern for algorithms:**
```java
// Instead of: if (weekend) use weekend pricing else use weekday
interface PricingStrategy {
    double calculate(long hours, DayOfWeek day);
}
class WeekdayPricing implements PricingStrategy { ... }
class WeekendPricing implements PricingStrategy { ... }
class DynamicPricing implements PricingStrategy { ... }
// Context:
class ParkingLot {
    private PricingStrategy strategy;
    void setPricingStrategy(PricingStrategy strategy) { this.strategy = strategy; }
}
```

## 5. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| God class (one class does everything) | Hard to test, change, understand | Split into focused classes (SRP) |
| Switch/if-else chains by type | Violates OCP — new type means modifying existing code | Polymorphism + factory |
| No interfaces for changeable behavior | Tight coupling, can't swap implementations | Program to interface, not implementation (DIP) |
| Mutable objects everywhere | Unexpected state changes, hard to debug | Immutable objects where possible (records) |
| Not handling null/empty/invalid | Production crashes from unexpected inputs | Validate at boundaries, return Optional |
| Public fields instead of getters | No encapsulation, change impacts consumers | Private fields + getters/setters |
| Deep inheritance hierarchies | Fragile base class problem | Favor composition over inheritance |
| Missing equals/hashCode for entities | HashSet/HashMap lookups fail | Implement equals/hashCode based on identity fields |

## 6. Exception Handling in LLD

```java
// Define domain-specific exceptions
class ParkingFullException extends RuntimeException {
    public ParkingFullException(String message) { super(message); }
}
class InvalidTicketException extends RuntimeException { ... }
class InsufficientPaymentException extends RuntimeException { ... }

// Use them in domain methods
class ParkingLot {
    public ParkingTicket parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findAvailableSpot(vehicle.getType());
        if (spot == null) {
            throw new ParkingFullException(
                "No available spot for " + vehicle.getType());
        }
        // ...
    }
}
```

## 7. Production Considerations

- **Thread safety**: If multiple threads access same ParkingLot (e.g., multiple entry gates), use `synchronized` or `ReentrantLock` on methods that modify shared state.
- **Logging**: Log all state changes for debugging (entry, exit, payment).
- **Monitoring**: Track occupancy, average stay duration, revenue.
- **Testing**: Unit test each strategy independently. Integration test the full flow.

## 8. Interview Questions And Answers

### Beginner
Q: Design a parking lot system. What are the core classes?
A: Core entities: Vehicle (abstract with getSpotsNeeded()), Car/Bike/Truck subclasses, ParkingSpot (id, type, isAvailable), ParkingFloor (list of spots by type), ParkingLot (floors, gates, singleton), ParkingTicket (vehicle, spot, entryTime). Interface: PricingStrategy (calculate fee). Key operations: parkVehicle (find spot, assign, issue ticket), unparkVehicle (calculate fee, accept payment, free spot).

### Intermediate
Q: How would you design an elevator system that optimizes for minimal wait time?
A: Core classes: Elevator (id, currentFloor, direction, doorsOpen), Request (sourceFloor, destinationFloor), ElevatorController (manages elevators, schedules requests). Implement SchedulingStrategy interface with multiple algorithms: FCFS (simplest), SCAN (elevator continues in same direction, picking up requests along the way), LOOK (elevator reverses at last request in direction). For minimal wait time: LOOK algorithm with request priority based on direction alignment. Use Observer pattern: floor button → controller, internal panel → controller.

### Senior
Q: Design a chess game that supports undo. How do you detect check/checkmate? How would you add castling?
A: Use Command pattern: each Move stores from, to, piece, capturedPiece, and previous state. Undo = reverse the move. Check detection: after each move, check if any opponent piece can attack the king. Checkmate: king is in check AND no legal moves exist (king can't move to safe square, no piece can block, no piece can capture attacker). Castling: special Move subclass CastlingMove that also moves the rook. Valid only if king and rook haven't moved, no pieces between them, king doesn't pass through check.

### Tricky
Q: Design a vending machine. It's in IDLE state. User selects product (moves to SELECTING). Inserts coins (PAYING). Machine dispenses and returns change (DISPENSING → IDLE). But what if the user changes their mind during PAYING? How do you handle cancel?
A: Each state handles cancel() differently:
- IDLE: nothing to cancel — ignore or throw
- SELECTING: return to IDLE
- PAYING: refund all inserted coins, return to IDLE
- DISPENSING: too late — product already being dispensed. But track "refund eligible" flag

State machine with cancel:
```
IDLE → selectProduct() → SELECTING
SELECTING → insertCoin() → PAYING
SELECTING → cancel() → IDLE (no refund needed)
PAYING → insertCoin() → PAYING (more coins)
PAYING → selectProduct() → IGNORE (already selected)
PAYING → cancel() → refund all → IDLE
PAYING → paymentComplete() → DISPENSING
DISPENSING → dispenseDone() → IDLE
DISPENSING → cancel() → IGNORE (already dispensing)
```

## 9. Final 30-Second Answer

LLD = class design + OOP modeling. **Framework**: Entities → Relationships → Interfaces → Patterns → Code → Extensions. **Common problems**: Parking Lot (Vehicle, Spot, Ticket, PricingStrategy), Elevator (SchedulingStrategy, Observer), Vending Machine (State pattern), Chess (Strategy per piece, Command for undo). **Always**: SOLID principles (SRP, OCP, LSP, ISP, DIP), program to interfaces, use factory/strategy/observer/state patterns, handle null/empty/invalid. Never: god classes, switch by type, tight coupling, missing interfaces. Extensions should be ADDING code, not MODIFYING existing code.