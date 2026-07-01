/*
============================================================
CAB BOOKING SYSTEM
============================================================


PROBLEM STATEMENT
============================================================


Design a cab booking system like:

Uber
Ola


System should support:


- Rider requests cab
- Find available driver nearby
- Assign driver
- Start ride
- Complete ride
- Calculate fare



Example:


User:

Himanshu


Pickup:

Location A


Drop:

Location B



System:


Find nearest driver


Driver accepts


Ride starts


Fare calculated



============================================================
CORE ENTITIES
============================================================


CabBookingSystem


        |
        |
        +----------------+
        |                |
      Rider           Driver


        |
        |
       Ride



Driver owns:

Cab


============================================================
DESIGN THINKING
============================================================


1. Driver and Rider are separate


Why?


Rider:

Requests ride


Driver:

Provides ride



Different responsibilities.





------------------------------------------------------------


2. Driver matching strategy


Question:


How to select driver?


Today:


Nearest available driver



Tomorrow:


- Highest rating driver
- Cheapest driver
- Premium driver



Use Strategy Pattern:



DriverMatchingStrategy


        |

        |

NearestDriverStrategy



============================================================
FARE CALCULATION
============================================================


Fare changes:


Normal:


distance * rate



Peak:


distance * surge price



Premium:


higher rate



So use:


FareStrategy


        |

        |

NormalFareStrategy


PeakFareStrategy



============================================================
FLOW
============================================================


Rider requests cab


        |

        v


Find available drivers


        |

        v


Matching Strategy


        |

        v


Assign driver


        |

        v


Create Ride


        |

        v


Complete Ride


        |

        v


Calculate Fare



============================================================
TIME COMPLEXITY
============================================================


Finding driver:


Current:

O(n)


because checking all drivers.



Production:


Use:

Geospatial index

Example:

Google Maps / Redis GEO



Then:

O(log n)



============================================================
INTERVIEW FOLLOW UPS
============================================================


1. Driver location updates


Use:


Location Service


Driver sends:

latitude

longitude



------------------------------------------------------------


2. Multiple vehicle types


Add:


VehicleType


CAR

BIKE

AUTO



------------------------------------------------------------


3. Payment


Add:


PaymentService



------------------------------------------------------------


4. Real time tracking


Use:


WebSocket



============================================================

*/


import java.util.*;

public class CabBookingSystem {


    /*
    ========================================================
    LOCATION

    Stores geographical position

    ========================================================
    */


    static class Location {


        double latitude;


        double longitude;




        Location(
                double latitude,
                double longitude
        ){

            this.latitude = latitude;

            this.longitude = longitude;

        }



        /*
        Simple distance calculation

        Real systems use:
        Google distance API
        Haversine formula

        */

        public double distance(
                Location other
        ){


            double x =
                    latitude - other.latitude;


            double y =
                    longitude - other.longitude;



            return Math.sqrt(
                    x*x + y*y
            );

        }



    }









    /*
    ========================================================
    RIDER

    Person booking cab

    ========================================================
    */


    static class Rider {


        int id;


        String name;



        Rider(
                int id,
                String name
        ){

            this.id=id;

            this.name=name;

        }


    }










    /*
    ========================================================
    CAB

    Vehicle information

    ========================================================
    */


    static class Cab {


        String number;


        String model;




        Cab(
                String number,
                String model
        ){

            this.number=number;

            this.model=model;

        }


    }











    /*
    ========================================================
    DRIVER


    Driver has cab and location


    ========================================================
    */


    static class Driver {


        int id;


        String name;


        Cab cab;


        Location location;



        boolean available;



        Driver(
                int id,
                String name,
                Cab cab,
                Location location
        ){

            this.id=id;

            this.name=name;

            this.cab=cab;

            this.location=location;


            this.available=true;

        }



        public void startRide(){


            available=false;


        }




        public void endRide(){


            available=true;


        }


    }









    /*
    ========================================================
    RIDE

    Represents one booking


    ========================================================
    */


    static class Ride {


        Rider rider;


        Driver driver;


        Location pickup;


        Location drop;



        double fare;




        Ride(
                Rider rider,
                Driver driver,
                Location pickup,
                Location drop
        ){

            this.rider=rider;

            this.driver=driver;

            this.pickup=pickup;

            this.drop=drop;

        }



    }









    /*
    ========================================================
    DRIVER MATCHING STRATEGY

    ========================================================
    */


    interface DriverMatchingStrategy {


        Driver findDriver(
                List<Driver> drivers,
                Location pickup
        );


    }










    /*
    ========================================================
    NEAREST DRIVER STRATEGY

    ========================================================
    */


    static class NearestDriverStrategy
            implements DriverMatchingStrategy {



        public Driver findDriver(
                List<Driver> drivers,
                Location pickup
        ){


            Driver selected=null;


            double minDistance =
                    Double.MAX_VALUE;




            for(Driver driver: drivers){


                if(driver.available){



                    double distance =
                            driver.location
                            .distance(
                                    pickup
                            );



                    if(distance < minDistance){


                        minDistance =
                                distance;


                        selected =
                                driver;


                    }


                }


            }


            return selected;


        }


    }









    /*
    ========================================================
    FARE STRATEGY

    ========================================================
    */


    interface FareStrategy {


        double calculate(
                Location start,
                Location end
        );


    }










    static class NormalFareStrategy
            implements FareStrategy {



        public double calculate(
                Location start,
                Location end
        ){



            double distance =
                    start.distance(end);



            return distance * 10;


        }


    }









    /*
    ========================================================
    CAB SERVICE

    Main business logic

    ========================================================
    */


    static class CabService {


        List<Driver> drivers;



        DriverMatchingStrategy strategy;



        FareStrategy fareStrategy;






        CabService(
                List<Driver> drivers
        ){

            this.drivers=drivers;


            strategy =
            new NearestDriverStrategy();



            fareStrategy =
            new NormalFareStrategy();


        }







        public Ride bookRide(
                Rider rider,
                Location pickup,
                Location drop
        ){



            Driver driver =
                    strategy.findDriver(
                            drivers,
                            pickup
                    );




            if(driver == null){


                throw new RuntimeException(
                        "No driver available"
                );


            }




            driver.startRide();




            Ride ride =
                    new Ride(
                            rider,
                            driver,
                            pickup,
                            drop
                    );




            return ride;


        }








        public void completeRide(
                Ride ride
        ){


            ride.fare =
                    fareStrategy.calculate(
                            ride.pickup,
                            ride.drop
                    );



            ride.driver.endRide();


        }



    }









    /*
    ========================================================
    MAIN
    ========================================================
    */


    public static void main(String[] args) {



        Driver d1 =
                new Driver(
                        1,
                        "Driver A",
                        new Cab(
                                "KA01",
                                "Sedan"
                        ),
                        new Location(
                                10,
                                10
                        )
                );




        Driver d2 =
                new Driver(
                        2,
                        "Driver B",
                        new Cab(
                                "KA02",
                                "SUV"
                        ),
                        new Location(
                                20,
                                20
                        )
                );




        CabService service =
                new CabService(
                        List.of(
                                d1,
                                d2
                        )
                );




        Rider rider =
                new Rider(
                        1,
                        "Himanshu"
                );




        Ride ride =
                service.bookRide(
                        rider,
                        new Location(
                                11,
                                11
                        ),
                        new Location(
                                15,
                                15
                        )
                );



        service.completeRide(
                ride
        );




        System.out.println(
                "Driver : "
                +
                ride.driver.name
        );



        System.out.println(
                "Fare : "
                +
                ride.fare
        );


    }


}
