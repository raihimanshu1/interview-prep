# Facade Pattern

> **Provides a simplified interface to a complex subsystem.**

## 📖 Concept

**Real-world analogy:** A restaurant waiter — you just tell the waiter your order. The waiter handles the kitchen, chefs, billing — you don't interact with them directly.

## 🔍 When to Use

- Complex system with many classes — provide a simple entry point
- Want to decouple clients from subsystem components
- Layered architecture — each layer has a facade
- Want to make a complex library or framework easier to use

## ✅ Interview Checklist

- [ ] Facade class knows which subsystem classes to use
- [ ] Subsystem classes do their own work, unaware of facade
- [ ] Client calls facade methods, not subsystem directly
- [ ] Facade simplifies the interface
- [ ] Subsystem can still be used directly if needed

## 🧪 Common Interview Question

**Problem:** Design an Order Processing system. The process involves Inventory check, Payment processing, Shipping, and Notification. The customer should only call one method `placeOrder()`.

## 💻 Java Implementation

### 1. Basic Facade

```java
// Complex Subsystem Classes
class InventoryService {
    public boolean checkStock(String productId, int quantity) {
        System.out.println("Checking stock for " + productId);
        return true; // simplified
    }
    public void reduceStock(String productId, int quantity) {
        System.out.println("Reducing stock for " + productId);
    }
}

class PaymentService {
    public boolean processPayment(String customerId, double amount) {
        System.out.println("Processing payment of ₹" + amount);
        return true;
    }
}

class ShippingService {
    public String scheduleShipping(String productId, String address) {
        System.out.println("Scheduling shipping to " + address);
        return "TRACK123";
    }
}

class NotificationService {
    public void sendEmail(String customerEmail, String message) {
        System.out.println("Email to " + customerEmail + ": " + message);
    }
}

// Facade
class OrderFacade {
    private InventoryService inventory = new InventoryService();
    private PaymentService payment = new PaymentService();
    private ShippingService shipping = new ShippingService();
    private NotificationService notification = new NotificationService();

    public String placeOrder(String productId, int quantity,
                             String customerId, String address, String email) {
        System.out.println("=== Placing Order ===");

        // Step 1: Check inventory
        if (!inventory.checkStock(productId, quantity)) {
            return "Out of stock";
        }

        // Step 2: Process payment
        if (!payment.processPayment(customerId, 500.0)) {
            return "Payment failed";
        }

        // Step 3: Reduce stock
        inventory.reduceStock(productId, quantity);

        // Step 4: Schedule shipping
        String trackingId = shipping.scheduleShipping(productId, address);

        // Step 5: Send notification
        notification.sendEmail(email, "Order placed! Tracking: " + trackingId);

        return "Order placed successfully. Tracking: " + trackingId;
    }
}
```

### 2. Usage

```java
public class FacadeDemo {
    public static void main(String[] args) {
        OrderFacade orderFacade = new OrderFacade();
        String result = orderFacade.placeOrder("PROD-123", 1,
                "CUST-001", "123 Main St", "customer@example.com");
        System.out.println(result);
    }
}
```

### 3. Full Working Example: Travel Booking System

```java
// Subsystem classes
class FlightBooking {
    public String bookFlight(String from, String to) {
        System.out.println("Flight booked: " + from + " → " + to);
        return "FLIGHT-123";
    }
}

class HotelBooking {
    public String bookHotel(String city) {
        System.out.println("Hotel booked in " + city);
        return "HOTEL-456";
    }
}

class CarRental {
    public String rentCar(String location) {
        System.out.println("Car rented at " + location);
        return "CAR-789";
    }
}

class PaymentGateway {
    public boolean pay(double amount) {
        System.out.println("Payment of ₹" + amount + " processed");
        return true;
    }
}

// Facade
class TravelPackageFacade {
    private FlightBooking flight = new FlightBooking();
    private HotelBooking hotel = new HotelBooking();
    private CarRental car = new CarRental();
    private PaymentGateway payment = new PaymentGateway();

    public String bookCompletePackage(String from, String to, String city) {
        System.out.println("=== Booking Travel Package ===");

        String flightId = flight.bookFlight(from, to);
        String hotelId = hotel.bookHotel(city);
        String carId = car.rentCar(to);

        double total = 15000.0;
        if (!payment.pay(total)) {
            return "Payment failed";
        }

        return "Package booked! Flight: " + flightId +
               ", Hotel: " + hotelId + ", Car: " + carId;
    }
}

// Client
public class TravelDemo {
    public static void main(String[] args) {
        TravelPackageFacade facade = new TravelPackageFacade();
        String booking = facade.bookCompletePackage("Delhi", "Goa", "Goa");
        System.out.println(booking);
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Facade becomes too smart | Keep it thin — just coordinate, don't add business logic |
| Facade hides too much | Expose multiple facades for different use cases |
| Subsystem classes depend on facade | Subsystem should not know about facade |
| God facade | Split by domain (OrderFacade, PaymentFacade, ShippingFacade) |

## 🎯 Related Interview Questions

1. **Design a Video Conversion API** — Facade over codec detection, decoding, encoding
2. **Design a Home Automation system** — One remote to control lights, AC, TV
3. **Difference between Facade and Mediator?** — Facade simplifies a subsystem; Mediator coordinates communication

## 🆚 Facade vs Adapter

| Aspect | Facade | Adapter |
|--------|--------|---------|
| Purpose | Simplify complex interface | Make incompatible interfaces work |
| Changes | Adds new simplified interface | Converts existing interface |
| Direction | Client → Facade → Subsystem | Client → Adapter → Adaptee |
| Example | Order processing with one call | Legacy payment to new system |