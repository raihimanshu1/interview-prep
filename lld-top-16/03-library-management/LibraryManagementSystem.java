/*We will build **Library Management System LLD** similar to Parking Lot and Movie Booking.

        I will first explain the design, then give the complete runnable Java implementation.

        ---

        # Library Management System - Design Explanation

        ## Problem Statement

        Design a library system where:

        * Library has books
        * Members can search books
        * Members can borrow books
        * Members return books
        * System calculates fine for late returns

        ---

        # Core Design Thinking

        ## 1. Why separate Book and BookCopy?

        This is an important LLD decision.

        Imagine:

        Book:

        ```
        Clean Code
        Author: Robert Martin
        ```

        Library owns 5 copies:

        ```
        Copy-101
        Copy-102
        Copy-103
        Copy-104
        Copy-105
        ```

        The information about the book is same.

        But availability differs.

        Example:

        ```
        Clean Code

        Copy-101 -> Issued
        Copy-102 -> Available
        Copy-103 -> Available
        ```

        So:

        ```
        Book
        |
        |
        +--- BookCopy
        +--- BookCopy
        +--- BookCopy
        ```

        ---

        # 2. Entities

        ```
        Library

        Book
        |
        BookCopy


        Member

        BorrowRecord


        FineCalculator
        ```

        ---

        # 3. Responsibilities

        | Class          | Responsibility      |
        | -------------- | ------------------- |
        | Book           | Book information    |
        | BookCopy       | Physical copy state |
        | Member         | User details        |
        | BorrowRecord   | Issue history       |
        | Library        | Main operations     |
        | FineCalculator | Fine calculation    |

        ---

        # 4. Design Pattern

        ## Strategy Pattern

        Fine calculation can change.

        Today:

        ```
        ₹10/day
        ```

        Tomorrow:

        ```
        Student -> ₹5/day
        Premium -> Free
        ```

        Instead of changing Library:

        ```java
        FineCalculator
        |
        |
        SimpleFineCalculator
        StudentFineCalculator
        ```

        ---

        # Complete Runnable Java Code

        ```java*/
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class LibraryManagementSystem {


    /*
    =====================================================
    ENUMS
    =====================================================
    */


    enum BookStatus {

        AVAILABLE,
        ISSUED

    }





    /*
    =====================================================
    BOOK

    Represents book information

    Example:

    Clean Code
    Robert Martin

    =====================================================
    */


    static class Book {


        private final String isbn;


        private final String title;


        private final String author;



        Book(
                String isbn,
                String title,
                String author
        ){

            this.isbn=isbn;
            this.title=title;
            this.author=author;

        }





        public String getTitle(){

            return title;

        }


    }






    /*
    =====================================================
    BOOK COPY

    Physical copy inside library

    =====================================================
    */


    static class BookCopy {


        private final String copyId;


        private final Book book;


        private BookStatus status;



        BookCopy(
                String copyId,
                Book book
        ){

            this.copyId=copyId;
            this.book=book;

            this.status =
                    BookStatus.AVAILABLE;

        }





        public boolean isAvailable(){

            return status ==
                    BookStatus.AVAILABLE;

        }




        public void issue(){

            status =
                    BookStatus.ISSUED;

        }




        public void returnBook(){

            status =
                    BookStatus.AVAILABLE;

        }



        public Book getBook(){

            return book;

        }


    }






    /*
    =====================================================
    MEMBER

    Library user

    =====================================================
    */


    static class Member {


        private final String id;


        private final String name;



        Member(
                String name
        ){

            this.id =
                    UUID.randomUUID()
                            .toString();


            this.name=name;

        }



        public String getName(){

            return name;

        }


    }







    /*
    =====================================================
    BORROW RECORD

    Stores issue details

    =====================================================
    */


    static class BorrowRecord {


        private final Member member;


        private final BookCopy copy;


        private final LocalDateTime issueDate;


        private LocalDateTime returnDate;



        BorrowRecord(
                Member member,
                BookCopy copy
        ){

            this.member=member;

            this.copy=copy;


            this.issueDate =
                    LocalDateTime.now();

        }





        public void returned(){

            returnDate =
                    LocalDateTime.now();

        }




        public long getLateDays(){


            if(returnDate == null){

                return 0;

            }


            long days =
                    Duration.between(
                                    issueDate,
                                    returnDate
                            )
                            .toDays();



            /*
              Assume allowed duration = 7 days

              Example:

              borrowed 10 days

              late = 3 days

            */


            return Math.max(
                    0,
                    days - 7
            );

        }


    }







    /*
    =====================================================
    FINE STRATEGY

    =====================================================
    */


    interface FineCalculator {


        double calculate(
                long lateDays
        );


    }







    static class SimpleFineCalculator
            implements FineCalculator {



        public double calculate(
                long lateDays
        ){

            return lateDays * 10;

        }


    }







    /*
    =====================================================
    LIBRARY

    Main business class

    =====================================================
    */


    static class Library {


        private final List<BookCopy> copies;



        private final List<BorrowRecord> records;



        Library(
                List<BookCopy> copies
        ){

            this.copies=copies;

            this.records =
                    new ArrayList<>();

        }





        // Search available book

        public BookCopy searchBook(
                String title
        ){


            for(BookCopy copy: copies){


                if(
                        copy.isAvailable()
                                &&
                                copy.getBook()
                                        .getTitle()
                                        .equalsIgnoreCase(title)
                ){

                    return copy;

                }

            }


            return null;

        }






        // Issue book

        public BorrowRecord issueBook(
                Member member,
                String title
        ){


            BookCopy copy =
                    searchBook(title);



            if(copy == null){

                throw new RuntimeException(
                        "Book unavailable"
                );

            }



            copy.issue();



            BorrowRecord record =
                    new BorrowRecord(
                            member,
                            copy
                    );



            records.add(record);



            return record;


        }





        // Return book


        public double returnBook(
                BorrowRecord record,
                FineCalculator calculator
        ){


            record.returned();


            record.copy.returnBook();



            long lateDays =
                    record.getLateDays();



            return calculator.calculate(
                    lateDays
            );


        }


    }








    /*
    =====================================================
    MAIN
    =====================================================
    */


    public static void main(String[] args) {



        // Create book

        Book book =
                new Book(
                        "123",
                        "Clean Code",
                        "Robert Martin"
                );




        // Create physical copy


        BookCopy copy =
                new BookCopy(
                        "COPY-1",
                        book
                );




        // Create library


        Library library =
                new Library(
                        List.of(copy)
                );





        // Member


        Member member =
                new Member(
                        "Himanshu"
                );





        // Issue


        BorrowRecord record =
                library.issueBook(
                        member,
                        "Clean Code"
                );



        System.out.println(
                "Book issued"
        );





        // Return


        FineCalculator calculator =
                new SimpleFineCalculator();




        double fine =
                library.returnBook(
                        record,
                        calculator
                );



        System.out.println(
                "Fine : "
                        + fine
        );


    }


}
/*
```

        ---

        # Output

```
Book issued

Fine : 0.0
        ```

        ---

        # Flow Explanation

## Issue

```
Member

   |
v

Library.issueBook()

   |
v

Find BookCopy

   |
v

Change status

AVAILABLE
      |
v
        ISSUED


Create BorrowRecord
```

        ---

        ## Return

```
Member returns book

        |
v

BookCopy status

ISSUED
   |
v
        AVAILABLE


Calculate late days

        |
v

        FineCalculator

        |
v

Fine amount

```

        ---

        # Interview Improvements

## 1. Multiple copies

Already supported:

        ```
Book

COPY-1
COPY-2
COPY-3
        ```

        ---

        ## 2. Multiple libraries

Add:

        ```
LibraryBranch

        Library
```

        ---

        ## 3. Search optimization

Current:

        ```
O(n)
```

For large systems:

Use:

        ```
HashMap<String,List<BookCopy>>
```

        ---

        ## 4. Concurrent issue problem

Two users issue same book.

        Solution:

        ```java
synchronized(copy){

        }
        ```

or database locking.

---

This is the basic professional LLD foundation for a Library Management System.
*/
