//```java id="hotel_booking_lld"
/*
============================================================
HOTEL BOOKING SYSTEM
============================================================


PROBLEM STATEMENT
============================================================


Design a Hotel Booking System like:


Booking.com
Airbnb


System should support:


- Search hotels
- Check room availability
- Book room
- Cancel booking
- Calculate price
- Manage guests
- Handle payments



Example:


Guest:

John


Search:

New York

Date:

10 July - 12 July



System:


Find available rooms


Book room


Generate confirmation



============================================================
CORE ENTITIES
============================================================



HotelBookingSystem


        |
        |
        +----------------+
        |                |
      Hotel            Guest


        |
        |
       Room


        |
        |
     Booking



============================================================
DESIGN THINKING
============================================================



1. ROOM ENTITY


A hotel has many rooms.


Room contains:


- room id
- room type
- price
- availability



------------------------------------------------------------


2. BOOKING ENTITY


Booking represents reservation.



Contains:


- guest
- room
- check in date
- check out date
- status



------------------------------------------------------------


3. PRICING STRATEGY


Price can change:


Normal day:

1000/day


Weekend:

1500/day


Festival:

3000/day



Instead of:


if weekend


else festival



Use:


PricingStrategy interface



        |

        |

NormalPricing

WeekendPricing



============================================================
FLOW
============================================================


Guest searches hotel


        |

        v


Check room availability


        |

        v


Select room


        |

        v


Calculate price


        |

        v


Create booking


        |

        v


Payment successful


        |

        v


Confirm booking



============================================================
ROOM AVAILABILITY LOGIC
============================================================



Room is available when:


No existing booking overlaps date.



Example:


Existing:

10 July - 15 July



New request:

12 July - 14 July



Overlap:


YES


Room unavailable



============================================================
TIME COMPLEXITY
============================================================


Search room:

O(n)


Booking lookup:

O(n)



Production:


Database indexing:

O(log n)



============================================================
SPACE COMPLEXITY
============================================================


O(hotels + rooms + bookings)



============================================================
INTERVIEW FOLLOW UPS
============================================================



1. Multiple cities


Add:


Location entity



------------------------------------------------------------



2. Payment integration


Add:


PaymentService



------------------------------------------------------------



3. Notifications


Add:


NotificationService



Email

SMS



------------------------------------------------------------



4. Concurrent booking


Two users book same room.


Solution:


Database transaction


Row locking



============================================================

*/


import java.util.*;

public class HotelBookingSystem {



    /*
    ========================================================
    ENUMS
    ========================================================
    */


    enum RoomType {


        SINGLE,


        DOUBLE,


        SUITE


    }





    enum BookingStatus {


        CONFIRMED,


        CANCELLED


    }









    /*
    ========================================================
    GUEST

    ========================================================
    */


    static class Guest {


        int id;


        String name;




        Guest(
                int id,
                String name
        ){

            this.id=id;

            this.name=name;


        }


    }









    /*
    ========================================================
    ROOM

    ========================================================
    */


    static class Room {


        int id;


        RoomType type;


        double price;



        List<Booking> bookings;




        Room(
                int id,
                RoomType type,
                double price
        ){

            this.id=id;

            this.type=type;

            this.price=price;


            bookings =
                    new ArrayList<>();


        }






        /*
        Check booking overlap

        Existing:

        10-15


        Requested:

        12-14


        Overlap exists

        */


        boolean isAvailable(
                int checkIn,
                int checkOut
        ){



            for(Booking booking: bookings){



                if(
                    booking.status
                    ==
                    BookingStatus.CONFIRMED
                ){



                    if(
                        checkIn < booking.checkOut
                        &&
                        checkOut > booking.checkIn
                    ){


                        return false;


                    }


                }


            }



            return true;


        }


    }









    /*
    ========================================================
    HOTEL

    ========================================================
    */


    static class Hotel {


        int id;


        String name;



        List<Room> rooms;




        Hotel(
                int id,
                String name
        ){

            this.id=id;

            this.name=name;


            rooms =
                    new ArrayList<>();

        }





        void addRoom(
                Room room
        ){

            rooms.add(room);


        }



    }









    /*
    ========================================================
    BOOKING

    ========================================================
    */


    static class Booking {


        int id;


        Guest guest;


        Room room;



        int checkIn;


        int checkOut;



        double amount;



        BookingStatus status;






        Booking(
                int id,
                Guest guest,
                Room room,
                int checkIn,
                int checkOut,
                double amount
        ){

            this.id=id;

            this.guest=guest;

            this.room=room;

            this.checkIn=checkIn;

            this.checkOut=checkOut;

            this.amount=amount;


            status =
                    BookingStatus.CONFIRMED;


        }



    }









    /*
    ========================================================
    PRICING STRATEGY

    Strategy Pattern

    ========================================================
    */


    interface PricingStrategy {


        double calculate(
                Room room,
                int days
        );


    }








    static class NormalPricing
            implements PricingStrategy {



        public double calculate(
                Room room,
                int days
        ){


            return room.price * days;


        }


    }









    /*
    ========================================================
    BOOKING SERVICE

    ========================================================
    */


    static class BookingService {



        PricingStrategy pricingStrategy;



        BookingService(){


            pricingStrategy =
                    new NormalPricing();


        }








        Booking bookRoom(
                int bookingId,
                Guest guest,
                Room room,
                int checkIn,
                int checkOut
        ){



            int days =
                    checkOut - checkIn;




            if(
                !room.isAvailable(
                        checkIn,
                        checkOut
                )
            ){


                throw new RuntimeException(
                        "Room unavailable"
                );


            }






            double price =
                    pricingStrategy.calculate(
                            room,
                            days
                    );







            Booking booking =
                    new Booking(
                            bookingId,
                            guest,
                            room,
                            checkIn,
                            checkOut,
                            price
                    );





            room.bookings.add(
                    booking
            );





            return booking;


        }







        void cancelBooking(
                Booking booking
        ){


            booking.status =
                    BookingStatus.CANCELLED;


        }


    }









    /*
    ========================================================
    MAIN
    ========================================================
    */


    public static void main(String[] args) {



        Hotel hotel =
                new Hotel(
                        1,
                        "Grand Hotel"
                );




        Room room =
                new Room(
                        101,
                        RoomType.DOUBLE,
                        2000
                );



        hotel.addRoom(
                room
        );






        Guest guest =
                new Guest(
                        1,
                        "John"
                );





        BookingService service =
                new BookingService();






        Booking booking =
                service.bookRoom(
                        1,
                        guest,
                        room,
                        10,
                        12
                );






        System.out.println(
                "Booking Confirmed"
        );


        System.out.println(
                "Amount : "
                +
                booking.amount
        );



    }


}
