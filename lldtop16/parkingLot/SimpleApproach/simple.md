I will keep this as a **single runnable Java file** so you can directly copy-paste and run.

The design contains:

* `Vehicle` → abstract parent (common vehicle behavior)
* `Car`, `Bike`, `Truck` → concrete vehicles
* `VehicleType` → enum
* `ParkingSpot` → manages one spot
* `ParkingFloor` → manages multiple spots
* `ParkingLot` → main coordinator
* `Ticket` → entry record
* `FeeCalculator` → strategy for pricing
* `Main` → execution flow

---

## Complete Runnable Java Code

```java
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingLotSystem {


    /*
     ============================================================
     HIGH LEVEL DESIGN EXPLANATION

     Problem:
     Build a parking lot system.

     Flow:

     Vehicle enters
          |
          v
     ParkingLot finds available spot
          |
          v
     Spot assigned
          |
          v
     Ticket generated


     Vehicle exits
          |
          v
     Calculate parking duration
          |
          v
     Calculate fee
          |
          v
     Release spot


     Design Decisions:

     1. Vehicle is abstract class

        Why?

        All vehicles have common fields:
        - license plate
        - vehicle type

        But different vehicle objects exist:
        - Car
        - Bike
        - Truck


     2. Enum VehicleType

        Instead of String:

        "CAR"
        "BIKE"

        because String allows mistakes:

        "CAAR"

        Enum gives compile-time safety.


     3. Strategy Pattern for FeeCalculator

        Today:

        Hourly pricing

        Tomorrow:

        Weekend pricing
        Festival pricing
        VIP pricing

        We can add new calculator without changing parking logic.


     4. Single Responsibility

        Vehicle:
            stores vehicle data

        ParkingSpot:
            knows whether spot is occupied

        ParkingFloor:
            finds available spots

        ParkingLot:
            controls parking flow

        Ticket:
            stores entry information

     ============================================================
    */



    // ============================================================
    // ENUM
    // ============================================================

    enum VehicleType {

        BIKE,
        CAR,
        TRUCK

    }



    // ============================================================
    // ABSTRACT VEHICLE
    // ============================================================


    static abstract class Vehicle {


        private final String licensePlate;

        private final VehicleType type;



        protected Vehicle(
                String licensePlate,
                VehicleType type
        ){

            if(licensePlate == null ||
               licensePlate.isEmpty()){

                throw new IllegalArgumentException(
                        "License plate required"
                );
            }


            this.licensePlate =
                    licensePlate.toUpperCase();


            this.type = type;

        }



        public String getLicensePlate(){

            return licensePlate;

        }


        public VehicleType getType(){

            return type;

        }


    }



    // ============================================================
    // CHILD VEHICLES
    // ============================================================


    static class Car extends Vehicle {


        public Car(String plate){

            super(
                    plate,
                    VehicleType.CAR
            );

        }

    }



    static class Bike extends Vehicle {


        public Bike(String plate){

            super(
                    plate,
                    VehicleType.BIKE
            );

        }

    }



    static class Truck extends Vehicle {


        public Truck(String plate){

            super(
                    plate,
                    VehicleType.TRUCK
            );

        }

    }




    // ============================================================
    // PARKING SPOT
    // ============================================================


    static class ParkingSpot {


        private final int id;


        private final VehicleType supportedType;



        private Vehicle parkedVehicle;



        public ParkingSpot(
                int id,
                VehicleType supportedType
        ){

            this.id=id;
            this.supportedType=supportedType;

        }




        // Checks whether spot is empty

        public boolean isAvailable(){

            return parkedVehicle == null;

        }




        // Checks whether this spot supports vehicle

        public boolean canFit(
                Vehicle vehicle
        ){

            return supportedType ==
                    vehicle.getType();

        }





        public void park(
                Vehicle vehicle
        ){

            this.parkedVehicle = vehicle;

        }





        public void removeVehicle(){

            this.parkedVehicle=null;

        }





        public int getId(){

            return id;

        }



    }





    // ============================================================
    // PARKING FLOOR
    // ============================================================


    static class ParkingFloor {


        private final int floorNumber;


        private final List<ParkingSpot> spots;



        public ParkingFloor(
                int floorNumber,
                List<ParkingSpot> spots
        ){

            this.floorNumber=floorNumber;
            this.spots=spots;

        }




        // Find first available spot

        public ParkingSpot findSpot(
                Vehicle vehicle
        ){


            for(ParkingSpot spot: spots){


                if(
                    spot.isAvailable()
                    &&
                    spot.canFit(vehicle)
                ){

                    return spot;

                }

            }


            return null;

        }

    }




    // ============================================================
    // TICKET
    // ============================================================


    static class Ticket {


        private final String id;


        private final Vehicle vehicle;


        private final ParkingSpot spot;


        private final LocalDateTime entryTime;




        public Ticket(
                Vehicle vehicle,
                ParkingSpot spot
        ){


            this.id =
            UUID.randomUUID()
            .toString();



            this.vehicle=vehicle;

            this.spot=spot;


            this.entryTime =
                    LocalDateTime.now();

        }





        public LocalDateTime getEntryTime(){

            return entryTime;

        }



        public ParkingSpot getSpot(){

            return spot;

        }



        public Vehicle getVehicle(){

            return vehicle;

        }


    }




    // ============================================================
    // FEE CALCULATOR STRATEGY
    // ============================================================


    interface FeeCalculator {


        double calculate(
                Ticket ticket
        );

    }





    static class HourlyFeeCalculator
            implements FeeCalculator {



        private final int hourlyRate = 50;



        public double calculate(
                Ticket ticket
        ){


            long minutes =
            Duration.between(
                    ticket.getEntryTime(),
                    LocalDateTime.now()
            )
            .toMinutes();



            /*
              Ceiling logic:

              61 minutes

              (61 + 59) / 60

              = 120 / 60

              = 2 hours

            */


            long hours =
            (minutes + 59) / 60;



            return hours * hourlyRate;

        }

    }





    // ============================================================
    // PARKING LOT
    // ============================================================


    static class ParkingLot {


        private final List<ParkingFloor> floors;



        public ParkingLot(
                List<ParkingFloor> floors
        ){

            this.floors=floors;

        }




        public Ticket parkVehicle(
                Vehicle vehicle
        ){


            for(ParkingFloor floor: floors){



                ParkingSpot spot =
                floor.findSpot(vehicle);



                if(spot != null){


                    spot.park(vehicle);



                    System.out.println(
                        "Vehicle parked at spot "
                        + spot.getId()
                    );



                    return new Ticket(
                            vehicle,
                            spot
                    );

                }

            }



            throw new RuntimeException(
                    "Parking Full"
            );

        }




        public void exitVehicle(
                Ticket ticket,
                FeeCalculator calculator
        ){



            double amount =
            calculator.calculate(ticket);



            System.out.println(
                    "Parking Fee : "
                    + amount
            );



            ticket.getSpot()
                  .removeVehicle();



            System.out.println(
                    "Spot released"
            );


        }


    }





    // ============================================================
    // MAIN METHOD
    // ============================================================


    public static void main(String[] args) {



        // Creating parking spots

        List<ParkingSpot> spots =
                new ArrayList<>();



        spots.add(
                new ParkingSpot(
                        1,
                        VehicleType.CAR
                )
        );



        spots.add(
                new ParkingSpot(
                        2,
                        VehicleType.BIKE
                )
        );



        spots.add(
                new ParkingSpot(
                        3,
                        VehicleType.TRUCK
                )
        );




        // Creating floor

        ParkingFloor floor =
        new ParkingFloor(
                1,
                spots
        );




        // Creating parking lot

        ParkingLot parkingLot =
        new ParkingLot(
                List.of(floor)
        );




        // Vehicle enters

        Vehicle car =
        new Car(
                "KA01AB1234"
        );



        Ticket ticket =
        parkingLot.parkVehicle(car);




        // Vehicle exits

        FeeCalculator calculator =
        new HourlyFeeCalculator();



        parkingLot.exitVehicle(
                ticket,
                calculator
        );


    }

}
```

---

## Example Output

```
Vehicle parked at spot 1

Parking Fee : 0.0

Spot released
```

---

## Interview Discussion Points

If interviewer asks "How will you improve this?"

### Multiple gates

Add:

```
EntryGate
ExitGate
```

---

### Nearest spot allocation

Replace:

```java
for loop
```

with:

```
PriorityQueue
```

---

### Concurrent cars entering

Current issue:

Two cars can select same spot.

Fix:

```java
synchronized(spot)
```

or database locking.

---

### Dynamic pricing

Add:

```
WeekendFeeCalculator
FestivalFeeCalculator
```

without touching existing code because of Strategy Pattern.
