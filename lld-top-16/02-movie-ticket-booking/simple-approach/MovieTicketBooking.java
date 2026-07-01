/*We already created a **Movie Ticket Booking System**. I assume now you mean **let's go deeper and make it more professional like the Parking Lot one** (production-style LLD).

        We will improve it with:

        * Better object modeling
        * Seat locking
        * Ticket generation
        * Payment abstraction
        * Pricing strategy
        * Multiple screens/shows
        * Booking lifecycle

        Structure:

        ```text
        MovieBookingSystem

        Movie
        |
        Show
        |
        Screen
        |
        Seat


        User
        |
        Booking
        |
        Ticket


        PaymentService
        |
        PaymentGateway


        PricingStrategy
        |
        DynamicPricing
        ```

        ---

        ## Design Explanation

        ### 1. Movie

        Only movie information.

        Example:

        ```
        Avengers
        Action
        180 mins
        ```

        ---

        ### 2. Show

        A movie itself is not bookable.

        A show is:

        ```
        Avengers
        +
        PVR Screen 1
        +
        7:30 PM
        ```

        ---

        ### 3. Seat

        Seat has lifecycle:

        ```
        AVAILABLE

        |
        v

        LOCKED

        |
        v

        BOOKED
        ```

        Why lock?

        Because:

        User A selects A1

        User B should not select same seat.

        ---

        ### 4. Booking

        Booking contains:

        * User
        * Show
        * Seats
        * Amount
        * Status

        ---

        ### 5. Pricing Strategy

        Today:

        ```
        Normal = 200
        Premium = 400
        ```

        Tomorrow:

        ```
        Weekend
        Holiday
        VIP
        ```

        No booking code change.

        ---

        ## Runnable Java Code

        ```java*/
import java.time.LocalDateTime;
import java.util.*;


public class MovieTicketBooking {


    /*
    =====================================================
    ENUMS
    =====================================================
    */


    enum SeatStatus {

        AVAILABLE,
        LOCKED,
        BOOKED

    }


    enum SeatType {

        NORMAL,
        PREMIUM

    }


    enum BookingStatus {

        CREATED,
        CONFIRMED,
        CANCELLED

    }



    /*
    =====================================================
    MOVIE
    =====================================================
    */


    static class Movie {


        private final String name;


        Movie(String name){

            this.name=name;

        }


        public String getName(){

            return name;

        }

    }





    /*
    =====================================================
    SEAT
    =====================================================

    Represents one physical seat

    =====================================================
    */


    static class Seat {


        private final String id;


        private final SeatType type;


        private SeatStatus status;



        Seat(
                String id,
                SeatType type
        ){

            this.id=id;
            this.type=type;
            this.status =
                    SeatStatus.AVAILABLE;

        }





        public boolean isAvailable(){

            return status ==
                    SeatStatus.AVAILABLE;

        }



        public void lock(){

            status =
                    SeatStatus.LOCKED;

        }




        public void book(){

            status =
                    SeatStatus.BOOKED;

        }





        public String getId(){

            return id;

        }




        public SeatType getType(){

            return type;

        }


    }






    /*
    =====================================================
    SCREEN

    Contains seats

    =====================================================
    */


    static class Screen {


        private final int screenId;


        private final List<Seat> seats;



        Screen(
                int screenId,
                List<Seat> seats
        ){

            this.screenId=screenId;
            this.seats=seats;

        }




        public Seat findSeat(
                String id
        ){


            for(Seat seat: seats){


                if(
                        seat.getId()
                                .equals(id)
                ){

                    return seat;

                }

            }


            return null;

        }


    }







    /*
    =====================================================
    SHOW

    Movie + Screen + Time

    =====================================================
    */


    static class Show {


        private final Movie movie;


        private final Screen screen;


        private final LocalDateTime time;



        Show(
                Movie movie,
                Screen screen,
                LocalDateTime time
        ){

            this.movie=movie;
            this.screen=screen;
            this.time=time;

        }



        public Screen getScreen(){

            return screen;

        }


        public Movie getMovie(){

            return movie;

        }

    }





    /*
    =====================================================
    TICKET
    =====================================================
    */


    static class Ticket {


        private final String id;


        private final Booking booking;



        Ticket(Booking booking){


            this.id =
                    UUID.randomUUID()
                            .toString();


            this.booking=booking;


        }

    }





    /*
    =====================================================
    BOOKING
    =====================================================
    */


    static class Booking {


        private final String id;


        private final Show show;


        private final List<Seat> seats;


        private BookingStatus status;



        Booking(
                Show show,
                List<Seat> seats
        ){


            this.id =
                    UUID.randomUUID()
                            .toString();


            this.show=show;

            this.seats=seats;


            this.status =
                    BookingStatus.CREATED;


        }




        public void confirm(){

            status =
                    BookingStatus.CONFIRMED;

        }



        public List<Seat> getSeats(){

            return seats;

        }


    }







    /*
    =====================================================
    PRICING STRATEGY
    =====================================================
    */


    interface PricingStrategy {


        double calculate(
                List<Seat> seats
        );


    }





    static class StandardPricing
            implements PricingStrategy {



        public double calculate(
                List<Seat> seats
        ){


            double amount=0;



            for(Seat seat: seats){



                if(
                        seat.getType()
                                ==
                                SeatType.PREMIUM
                ){

                    amount += 400;

                }

                else{

                    amount += 200;

                }

            }



            return amount;

        }


    }






    /*
    =====================================================
    PAYMENT
    =====================================================
    */


    interface PaymentGateway {


        boolean pay(double amount);


    }





    static class UpiPayment
            implements PaymentGateway {



        public boolean pay(
                double amount
        ){

            System.out.println(
                    "Paid using UPI : "
                            + amount
            );


            return true;

        }


    }






    /*
    =====================================================
    BOOKING SERVICE
    =====================================================
    */


    static class BookingService {



        public Ticket book(
                Show show,
                List<String> seatIds,
                PricingStrategy pricing,
                PaymentGateway payment
        ){


            List<Seat> seats =
                    new ArrayList<>();




            // Find seats

            for(String id: seatIds){


                Seat seat =
                        show.getScreen()
                                .findSeat(id);



                if(
                        seat==null
                                ||
                                !seat.isAvailable()
                ){

                    throw new RuntimeException(
                            "Seat unavailable"
                    );

                }



                seats.add(seat);

            }





            // Lock seats

            for(Seat seat: seats){

                seat.lock();

            }




            // Calculate amount

            double amount =
                    pricing.calculate(seats);





            // Payment

            boolean success =
                    payment.pay(amount);





            if(success){



                for(Seat seat: seats){

                    seat.book();

                }



                Booking booking =
                        new Booking(
                                show,
                                seats
                        );



                booking.confirm();



                return new Ticket(
                        booking
                );


            }



            throw new RuntimeException(
                    "Payment failed"
            );


        }


    }








    public static void main(String[] args) {



        Movie movie =
                new Movie(
                        "Avengers"
                );



        List<Seat> seats =
                List.of(

                        new Seat(
                                "A1",
                                SeatType.NORMAL
                        ),

                        new Seat(
                                "A2",
                                SeatType.PREMIUM
                        )

                );



        Screen screen =
                new Screen(
                        1,
                        seats
                );



        Show show =
                new Show(
                        movie,
                        screen,
                        LocalDateTime.now()
                );



        BookingService service =
                new BookingService();



        Ticket ticket =
                service.book(
                        show,
                        List.of(
                                "A1",
                                "A2"
                        ),
                        new StandardPricing(),
                        new UpiPayment()
                );



        System.out.println(
                "Ticket Generated"
        );


    }

}
/*```

        ---

        ## Output

```
Paid using UPI : 600.0

Ticket Generated
```

        ---

        ## Patterns Used

### Strategy Pattern

Pricing:

        ```
PricingStrategy

      |
              |
StandardPricing
        WeekendPricing
VIPPricing
```

        ---

        ### Dependency Injection

BookingService does not create:

        ```java
new UpiPayment()
```

It receives:

        ```java
PaymentGateway payment
```

So we can replace:

        ```
UPI
        CARD
PAYPAL
```

        ---

        ### Next improvements for interview level:

        1. Add seat timeout (5 min hold)
2. Add cancellation + refund
3. Add multiple theaters/cities
4. Add concurrency handling with locks
5. Add database entities mapping (JPA)

This is the LLD foundation.*/
