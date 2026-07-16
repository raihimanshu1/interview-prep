package com.lldtop16.parkinglot.simpleapproach;

/*

Good. Now we are at the point where we should **improve design without overengineering**.
Current code works, but professional LLD improvements:
        ### Problems in current version
1. `PriceCalculator` knows all pricing rules:
        ```java
if(BIKE)
        if(TRUCK)
        else CAR
```
Tomorrow:
        * weekend pricing
* night pricing
* premium parking
* EV vehicle pricing
we keep modifying the same class.
This violates **Open/Closed Principle**.
Solution:
Use **Strategy Pattern**.
        ---
        2. ParkingLot creates pricing logic directly.
        Better:
        ```java
        ParkingLot
    |
v
        PricingStrategy
    |
            |
HourlyPricing
        WeekendPricing
```
ParkingLot does not care how price is calculated.
        ---
        3. Use `Ticket` as the single source of parking session.
        Flow:
        ```
Ticket
 |
         |-- Vehicle
 |-- Spot
 |-- EntryTime
```
Good.
---
Here is cleaner professional LLD version.
*/

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
public class ParkingLotSystem {
    /*
        DESIGN PATTERNS USED:
        1. Strategy Pattern
           PricingStrategy
              |
              |
           HourlyPricing
           Tomorrow:
           WeekendPricing
           VIPPricing
           can be added without changing ParkingLot.
        2. Single Responsibility
        Vehicle:
            Vehicle details
        ParkingSpot:
            Spot state
        Ticket:
            Parking session
        ParkingLot:
            Parking flow
        PricingStrategy:
            Pricing calculation
    */
    // ================= VEHICLE =================
    enum VehicleType {
        CAR, BIKE, TRUCK
    }
    static abstract class Vehicle {
        private final String number;
        private final VehicleType type;
        public Vehicle(String number, VehicleType type) {
            this.number = number;
            this.type = type;
        }
        public VehicleType getType() {
            return type;
        }
        public String getNumber() {
            return number;
        }
    }
    static class Car extends Vehicle {
        public Car(String number) {
            super(number, VehicleType.CAR);
        }
    }
    // ================= PARKING SPOT =================
    static class ParkingSpot {
        private final int id;
        private Vehicle vehicle;
        public ParkingSpot(int id) {
            this.id = id;
        }
        public boolean isAvailable() {
            return vehicle == null;
        }
        public void assign(Vehicle vehicle) {
            this.vehicle = vehicle;
        }
        public void release() {
            vehicle = null;
        }
        public int getId() {
            return id;
        }
    }
    // ================= TICKET =================
    static class Ticket {
        private final String id;
        private final Vehicle vehicle;
        private final ParkingSpot spot;
        private final LocalDateTime entryTime;
        public Ticket(Vehicle vehicle, ParkingSpot spot) {
            this.id = UUID.randomUUID().toString();
            this.vehicle = vehicle;
            this.spot = spot;
            this.entryTime = LocalDateTime.now();
        }
        public String getId() {
            return id;
        }
        public Vehicle getVehicle() {
            return vehicle;
        }
        public ParkingSpot getSpot() {
            return spot;
        }
        public LocalDateTime getEntryTime() {
            return entryTime;
        }
    }
    // ================= STRATEGY =================
    interface PricingStrategy {
        double calculate(Ticket ticket);
    }
    static class HourlyPricing implements PricingStrategy {
        private final Map<VehicleType, Integer> rates = Map.of(VehicleType.CAR, 50, VehicleType.BIKE, 20, VehicleType.TRUCK, 100);
        public double calculate(Ticket ticket) {
            long minutes = Duration.between(ticket.getEntryTime(), LocalDateTime.now()).toMinutes();
//            long hours = Math.max(1, (minutes + 59) / 60);
            long hours = (minutes + 59) / 60;
            int rate = rates.get(ticket.getVehicle().getType());
            return hours * rate;
        }
    }
    // ================= PARKING LOT =================
    static class ParkingLot {
        private final Map<VehicleType, List<ParkingSpot>> spots;
        private final Map<String, Ticket> tickets;
        private final PricingStrategy pricingStrategy;
        public ParkingLot(Map<VehicleType, List<ParkingSpot>> spots, PricingStrategy pricingStrategy) {
            this.spots = spots;
            this.tickets = new HashMap<>();
            this.pricingStrategy = pricingStrategy;
        }
        public Ticket parkVehicle(Vehicle vehicle) {
            List<ParkingSpot> availableSpots = spots.get(vehicle.getType());
            for (ParkingSpot spot : availableSpots) {
                if (spot.isAvailable()) {
                    spot.assign(vehicle);
                    Ticket ticket = new Ticket(vehicle, spot);
                    tickets.put(ticket.getId(), ticket);
                    return ticket;
                }
            }
            throw new RuntimeException("Parking Full");
        }
        public void exitVehicle(String ticketId) {
            Ticket ticket = tickets.get(ticketId);
            if (ticket == null) {
                throw new RuntimeException("Invalid Ticket");
            }
            double amount = pricingStrategy.calculate(ticket);
            System.out.println("Amount : " + amount);
            ticket.getSpot().release();
            tickets.remove(ticketId);
            System.out.println("Vehicle exited");
        }
    }
    // ================= MAIN =================
    public static void main(String[] args) {
        Map<VehicleType, List<ParkingSpot>> storage = new HashMap<>();
        storage.put(VehicleType.CAR, Arrays.asList(new ParkingSpot(1), new ParkingSpot(2)));
        ParkingLot lot = new ParkingLot(storage, new HourlyPricing());
        Vehicle car = new Car("KA01AB1234");
        Ticket ticket = lot.parkVehicle(car);
        System.out.println("Ticket : " + ticket.getId());
        lot.exitVehicle(ticket.getId());
    }
}
/*```
Now it looks like an interview-quality LLD:
        * Strategy Pattern used correctly
* No `if-else` pricing explosion
* ParkingLot only manages parking flow
* Pricing can change independently
* Data ownership is clear
Next natural improvement would be adding:
        * `PaymentService`
        * `EntryGate/ExitGate`
        * `ParkingSpotAllocationStrategy`
but this version is already a good 7+ years LLD answer.*/
