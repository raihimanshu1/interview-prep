/*We will build **Elevator System LLD** in the same style as previous ones:
        * Single Java file
        * Directly paste into IntelliJ and run
        * Explanation at the top as comments
        * Clean OOP design
        * Elevator selection strategy
        * Request handling
        * Elevator movement
        * Door operation

        ---
        # High Level Design Explanation
        ```text
        ===========================================================
        ELEVATOR SYSTEM
        Example:
        User on Floor 5 presses DOWN
        Request created:
        Source Floor = 5
        Direction = DOWN
        Elevator Controller receives request
        |
        v
        Find best elevator
        |
        v
        Assign request
        |
        v
        Elevator moves
        |
        v
        Door opens
        ===========================================================
        MAIN ENTITIES:
        ElevatorSystem
        |
        |
        +---- ElevatorController
        |
        |
        +---- Elevator
        Elevator
        |
        |
        +---- Door
        Request
        |
        |
        +---- Floor
        +---- Direction
        ===========================================================
        DESIGN DECISIONS:
        1. Elevator is a separate object
        Because every elevator has its own:
        - current floor
        - state
        - direction
        Example:
        Elevator 1
        Floor 5
        MOVING_UP
        Elevator 2
        Floor 10
        IDLE
        -----------------------------------------------------------
        2. Strategy Pattern for elevator selection
        Question:
        Which elevator should handle request?
        Today:
        Nearest elevator
        Tomorrow:
        AI based
        Traffic based
        So create:
        ElevatorSelectionStrategy
        -----------------------------------------------------------
        3. Enum for fixed states
        ElevatorState:
        IDLE
        MOVING_UP
        MOVING_DOWN
        Direction:
        UP
        DOWN
        -----------------------------------------------------------
        4. Single Responsibility
        Elevator:
        Movement
        Door:
        Open/close
        Controller:
        Assign elevator
        System:
        Receive requests
        ===========================================================
        ```
        ---
        # Complete Runnable Java Code
        Create file:
        ```
        ElevatorSystem.java
        ```
        Paste everything:
        ```java*/

package com.lldtop16.elevator;



import java.util.*;
public class ElevatorSystem {
    /*
    =====================================================
    ENUMS
    =====================================================
    */
    enum Direction {
        UP, DOWN
    }
    enum ElevatorState {
        IDLE, MOVING_UP, MOVING_DOWN
    }
    /*
    =====================================================
    REQUEST
    Represents user request
    Example:
    Floor 5
    Going UP
    =====================================================
    */
    static class Request {
        private final int floor;
        private final Direction direction;
        Request(int floor, Direction direction) {
            this.floor = floor;
            this.direction = direction;
        }
        public int getFloor() {
            return floor;
        }
        public Direction getDirection() {
            return direction;
        }
    }
    /*
    =====================================================
    DOOR
    Responsible only for door operations
    =====================================================
    */
    static class Door {
        public void open() {
            System.out.println("Door opened");
        }
        public void close() {
            System.out.println("Door closed");
        }
    }
    /*
    =====================================================
    ELEVATOR
    Represents one elevator
    =====================================================
    */
    static class Elevator {
        private final int id;
        private int currentFloor;
        private ElevatorState state;
        private final Door door;
        private final Queue<Integer> requests;
        Elevator(int id) {
            this.id = id;
            this.currentFloor = 0;
            this.state = ElevatorState.IDLE;
            this.door = new Door();
            this.requests = new LinkedList<>();
        }
        public int getCurrentFloor() {
            return currentFloor;
        }
        public ElevatorState getState() {
            return state;
        }
        public void addRequest(int floor) {
            requests.offer(floor);
        }
        public void move() {
            while (!requests.isEmpty()) {
                int destination = requests.poll();
                moveToFloor(destination);
            }
        }
        private void moveToFloor(int floor) {
            if (floor > currentFloor) {
                state = ElevatorState.MOVING_UP;
            } else if (floor < currentFloor) {
                state = ElevatorState.MOVING_DOWN;
            }
            System.out.println("Elevator " + id + " moving from " + currentFloor + " to " + floor);
            currentFloor = floor;
            state = ElevatorState.IDLE;
            door.open();
            System.out.println("Passenger enters");
            door.close();
        }
        public boolean isIdle() {
            return state == ElevatorState.IDLE;
        }
        public int getId() {
            return id;
        }
    }
    /*
    =====================================================
    ELEVATOR SELECTION STRATEGY
    Decides which elevator should handle request
    =====================================================
    */
    interface ElevatorSelectionStrategy {
        Elevator select(List<Elevator> elevators, Request request);
    }
    /*
    =====================================================
    NEAREST ELEVATOR STRATEGY
    Choose closest idle elevator
    =====================================================
    */
    static class NearestElevatorStrategy implements ElevatorSelectionStrategy {
        public Elevator select(List<Elevator> elevators, Request request) {
            Elevator selected = null;
            int minDistance = Integer.MAX_VALUE;
            for (Elevator elevator : elevators) {
                if (elevator.isIdle()) {
                    int distance = Math.abs(elevator.getCurrentFloor() - request.getFloor());
                    if (distance < minDistance) {
                        minDistance = distance;
                        selected = elevator;
                    }
                }
            }
            return selected;
        }
    }
    /*
    =====================================================
    CONTROLLER
    Handles requests
    =====================================================
    */
    static class ElevatorController {
        private final List<Elevator> elevators;
        private final ElevatorSelectionStrategy strategy;
        ElevatorController(List<Elevator> elevators, ElevatorSelectionStrategy strategy) {
            this.elevators = elevators;
            this.strategy = strategy;
        }
        public void submitRequest(Request request) {
            Elevator elevator = strategy.select(elevators, request);
            if (elevator == null) {
                throw new RuntimeException("No elevator available");
            }
            System.out.println("Elevator " + elevator.getId() + " assigned");
            elevator.addRequest(request.getFloor());
            elevator.move();
        }
    }
    /*
    =====================================================
    MAIN
    =====================================================
    */
    public static void main(String[] args) {
        Elevator e1 = new Elevator(1);
        Elevator e2 = new Elevator(2);
        Elevator e3 = new Elevator(3);
        List<Elevator> elevators = List.of(e1, e2, e3);
        ElevatorController controller = new ElevatorController(elevators, new NearestElevatorStrategy());
        // User on floor 5 presses UP
        Request request1 = new Request(5, Direction.UP);
        controller.submitRequest(request1);
        // User on floor 2 presses DOWN
        Request request2 = new Request(2, Direction.DOWN);
        controller.submitRequest(request2);
    }
}
/*
```
        ---
        # Example Output
```
Elevator 1 assigned
Elevator 1 moving from 0 to 5
Door opened
Passenger enters
Door closed
Elevator 2 assigned
Elevator 2 moving from 0 to 2
Door opened
Passenger enters
Door closed
```
        ---
        # Interview Explanation
## Elevator Selection
Current:
        ```
Nearest idle elevator
```
Strategy:
        ```
ElevatorSelectionStrategy
        |
                |
NearestElevatorStrategy
```
Tomorrow:
        ```
TrafficAwareStrategy
        MinimumLoadStrategy
```
No controller change.
---
        # Real System Improvements
## Multiple floors inside elevator
Add:
        ```java
Queue<Integer> destinationFloors
```
Already partially done.
---
        ## Emergency mode
Add:
        ```
ElevatorState
        EMERGENCY
MAINTENANCE
```
        ---
        ## Concurrency
Real issue:
Two users request elevator simultaneously.
        Solution:
        ```java
synchronized(submitRequest)
        ```
or database queue.
---
        ## Multiple buildings
Add:
        ```
Building
    |
            |
ElevatorSystem
```
        ---
This is the standard **Elevator LLD asked in Java interviews (Amazon / Google / Uber style)**.
*/
