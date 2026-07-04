# Mediator Pattern

> **Defines an object that encapsulates how a set of objects interact. Promotes loose coupling by keeping objects from referring to each other explicitly.**

## 📖 Concept

**Real-world analogy:** Air traffic control tower — all pilots communicate through the tower, not directly with each other. The tower coordinates landings and takeoffs.

## 🔍 When to Use

- Many-to-many communication is too complex
- Want to centralize control logic (rules, policies)
- Components should be reusable independently
- Want to decouple colleagues from each other

## ✅ Interview Checklist

- [ ] Mediator interface defines communication methods
- [ ] Concrete Mediator coordinates colleagues
- [ ] Colleagues know only the Mediator, not each other
- [ ] Adding new colleague doesn't affect existing ones
- [ ] Mediator can become complex — split if needed

## 🧪 Common Interview Question

**Problem:** Design a Chat Room where multiple users send messages. Users should not directly reference each other. All communication goes through the ChatRoom mediator.

## 💻 Java Implementation

### 1. Basic Mediator

```java
// Mediator
interface ChatMediator {
    void sendMessage(String message, User user);
    void addUser(User user);
}

// Concrete Mediator
class ChatRoom implements ChatMediator {
    private List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void sendMessage(String message, User sender) {
        for (User user : users) {
            if (user != sender) {
                user.receive(message);
            }
        }
    }
}

// Colleague
abstract class User {
    protected ChatMediator mediator;
    protected String name;

    public User(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    public abstract void send(String message);
    public abstract void receive(String message);
}

// Concrete Colleagues
class ChatUser extends User {
    public ChatUser(ChatMediator mediator, String name) {
        super(mediator, name);
    }

    @Override
    public void send(String message) {
        System.out.println(name + " sends: " + message);
        mediator.sendMessage(message, this);
    }

    @Override
    public void receive(String message) {
        System.out.println(name + " receives: " + message);
    }
}
```

### 2. Usage

```java
public class MediatorDemo {
    public static void main(String[] args) {
        ChatRoom chatRoom = new ChatRoom();

        User alice = new ChatUser(chatRoom, "Alice");
        User bob = new ChatUser(chatRoom, "Bob");
        User charlie = new ChatUser(chatRoom, "Charlie");

        chatRoom.addUser(alice);
        chatRoom.addUser(bob);
        chatRoom.addUser(charlie);

        alice.send("Hello everyone!");
        bob.send("Hi Alice!");
    }
}
```

### 3. Full Working Example: Air Traffic Control

```java
// Mediator
interface ATCMediator {
    void registerFlight(Flight flight);
    void sendMessage(String message, Flight flight);
    void requestLanding(Flight flight);
    void grantLanding(Flight flight);
}

// Colleague
abstract class Flight {
    protected ATCMediator mediator;
    protected String flightNumber;

    public Flight(ATCMediator mediator, String flightNumber) {
        this.mediator = mediator;
        this.flightNumber = flightNumber;
    }

    public abstract void land();
    public abstract void send(String message);
}

// Concrete Colleagues
class PassengerFlight extends Flight {
    public PassengerFlight(ATCMediator mediator, String flightNumber) {
        super(mediator, flightNumber);
    }
    @Override
    public void land() {
        System.out.println(flightNumber + " landing");
        mediator.requestLanding(this);
    }
    @Override
    public void send(String message) {
        mediator.sendMessage(flightNumber + ": " + message, this);
    }
}

class Runway {
    private boolean available = true;
    private ATCMediator mediator;

    public Runway(ATCMediator mediator) {
        this.mediator = mediator;
    }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

// Concrete Mediator
class AirportControlTower implements ATCMediator {
    private List<Flight> flights = new ArrayList<>();
    private Runway runway;
    private Queue<Flight> landingQueue = new LinkedList<>();

    public AirportControlTower(Runway runway) {
        this.runway = runway;
    }

    @Override
    public void registerFlight(Flight flight) {
        flights.add(flight);
    }

    @Override
    public void sendMessage(String message, Flight flight) {
        System.out.println("[ATC] " + message);
    }

    @Override
    public void requestLanding(Flight flight) {
        if (runway.isAvailable()) {
            System.out.println("[ATC] " + flight.flightNumber + " — Landing cleared");
            runway.setAvailable(false);
            new Thread(() -> {
                try { Thread.sleep(3000); } catch (InterruptedException e) {}
                runway.setAvailable(true);
                System.out.println("[ATC] Runway available");
            }).start();
        } else {
            System.out.println("[ATC] " + flight.flightNumber + " — Hold position, runway busy");
            landingQueue.offer(flight);
        }
    }

    @Override
    public void grantLanding(Flight flight) {
        runway.setAvailable(true);
        System.out.println("[ATC] " + flight.flightNumber + " — You may land");
    }
}

// Usage
public class ATCDemo {
    public static void main(String[] args) {
        Runway runway = new Runway(null);
        AirportControlTower atc = new AirportControlTower(runway);
        runway = new Runway(atc);

        Flight flight1 = new PassengerFlight(atc, "AI-101");
        Flight flight2 = new PassengerFlight(atc, "AI-202");

        atc.registerFlight(flight1);
        atc.registerFlight(flight2);

        flight1.land();
        flight2.land();
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| God mediator | Split by domain or concern |
| Mediator becomes complex | Keep business logic in colleagues |
| Performance bottleneck | Message queue or async handling |
| Hard to debug | Log all message flow |

## 🎯 Related Interview Questions

1. **Design an Air Traffic Control system** — Tower mediates between flights
2. **Design an Auction House** — Auctioneer mediates between bidders
3. **Design a Chat Application** — ChatRoom mediates messages

## 🆚 Mediator vs Observer

| Aspect | Mediator | Observer |
|--------|----------|----------|
| Communication | Centralized through mediator | Direct broadcast to observers |
| Coupling | Colleagues know mediator | Subject knows observers |
| Control | Mediator controls flow | No central control |
| Example | Chat room, ATC | Event bus, Pub-Sub |